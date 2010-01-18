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
 * CodeAnalyser.java
 * Creation date: February 14, 2004
 * By: Iulian Radu
 */

package org.openquark.cal.compiler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openquark.cal.compiler.TypeChecker.TypeCheckInfo;


/**
 * This is a helper class that analyzes the code for code editors. It can analyze code that is passed in
 * by qualifying symbols, determining arguments and type-checking the code. The results returned are stored in the Results inner
 * class, which stores the compiler messages, as well as the found arguments and output type expression.
 * <p>
 * Creation Date: Oct 2 2002
 * @author Ken Wong
 */
public class CodeAnalyser {
    
    /**
     * Wrapper class for offset compiler messages. Messages are generated
     * on fully qualified code, and are offset during code analysis to
     * match visible code.
     * <p>
     * For example, the code "sin True" is padded with a newline at its start, 
     * and fully qualified, such that compiler messages are gathered from the expression:
     * <pre>
     * "
     *  Prelude.sin Prelude.True
     * "
     * </pre>
     * The compiler produces the message:
     *   "Type error during application..." at line 2, column 21
     * <p>
     * The offset compiler message, produced after analysis will match visible code:
     *   "Type error during application..." at line 1, column 5  
     * 
     * @author Iulian Radu
     */
    public static final class OffsetCompilerMessage {
        
        /** Original compiler message */
        private final CompilerMessage message;
        
        /** New line position after offset */
        private int offsetLine;
        
        /** New column position after offset */
        private int offsetColumn;      
        
        private OffsetCompilerMessage(CompilerMessage message, int lineOffset, int columnOffset) {
            if (message == null) {
                throw new IllegalArgumentException();
            }
            this.message = message;
            
            SourcePosition oldPosition = message.getSourceRange().getStartSourcePosition();
            if (oldPosition != null) {
                offsetLine   = oldPosition.getLine();
                offsetColumn = oldPosition.getColumn();
            } else {
                offsetLine = -1;
                offsetColumn = -1;
            }
            
            addOffset(lineOffset, columnOffset);
        }
        
        /** Returns new line position of message */
        public int getOffsetLine() {
            return offsetLine;
        }
        
        /** Returns new column position of message */
        public int getOffsetColumn() {
            return offsetColumn;
        }
        
        /**
         * @return a source position representing the new line and column of the message.
         */
        public SourcePosition getOffsetPosition() {
            return new SourcePosition(offsetLine, offsetColumn, "");
        }
        
        /** True if message has a source position associated with it */
        public boolean hasPosition() {
            return (message.getSourceRange() != null);
        }
        
        /** 
         * Increment offset in position by the specified amount.
         *  
         * @return False if this message does not have a position associated with it  
         */
        boolean addOffset (int lineOffset, int columnOffset) {
            if (hasPosition()) {
                offsetLine   = offsetLine + lineOffset;
                offsetColumn = offsetColumn + columnOffset;
                
                return true;
            } else {
                return false;
            }
        }
        
        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            String result = message.getMessage();
            if (message.getSourceRange() != null) {
                result += " at line: " + message.getSourceRange().getStartLine() + " col: " + message.getSourceRange().getStartColumn();
                result += " (was line: " + offsetLine + " col: " + offsetColumn + ")";
            }
            return result;
        }
        
        /**
         * Comparator object to order Offset Compiler Messages by increasing offset source position.
         */
        public static final SourcePositionComparator sourcePositionComparator = new SourcePositionComparator();
        private static class SourcePositionComparator implements Comparator<OffsetCompilerMessage> {
            
            /** {@inheritDoc}*/
            public int compare(OffsetCompilerMessage o1, OffsetCompilerMessage o2) {
                
                if ((o1 == null) || (o2 == null)) {
                    throw new NullPointerException();
                }
                
                int line1 = o1.getOffsetLine();
                int line2 = o2.getOffsetLine();
                
                // Put messages with no position at the end
                
                if (line2 == -1) {
                    return -1;
                } else if (line1 == -1) {
                    return 1;
                }
                
                int compareLines = SourcePositionComparator.compareInts (line1, line2);                
                
                int column1 = o1.getOffsetColumn();
                int column2 = o2.getOffsetColumn();
                              
                if (compareLines == 0) {
                    return SourcePositionComparator.compareInts (column1, column2);
                }
                
                return compareLines;                    
            }
            
            private static int compareInts(int i1, int i2) {
                //note: it is a cute trick to return i1 - i2, but this doesn't work
                //for all ints due to overflow in int arithmetic.                   
                return (i1 < i2 ? -1 : (i1 == i2 ? 0 : 1));
            }
        }
        
        // CompilerMessage methods
        
        public String getMessage() {
            return message.getMessage();
        }
        
        public CompilerMessage.Severity getSeverity() {
            return message.getSeverity();
        }
        
        public Exception getException() {
            return message.getException();
        }
    }
    
    /**
     * Wrapper class for source identifiers. Analysed identifiers differ
     * from source identifiers in that they are adjusted to fit visible code,
     * they are associated to their module if possible during analysis,
     * and they track their qualification method.
     * <p>
     * For example, on inspection of the code
     * <pre>
     *   "let x = and False 
     *                Prelude.True; 
     *    in arg x"
     * 
     *   We gather the source identifiers: 
     *     and   - at line 2 col 9,  of category SC_OR_METHOD, with no module
     *     False - at line 2 col 13, of category DATA_CONSTRUCTOR, with no module
     *     True  - at line 3 col 21, of category DATA_CONSTRUCTOR, with module Prelude
     *     arg   - at line 4 col 4,  of category SC_OR_METHOD  
     *   (note that line numbers are offset by 1, since the code is padded
     *    prior to analysis)
     * 
     *   After analysis completes, we have the analysed identifiers (with the same categories):
     *     and   - at line 1 col 9,  module Prelude, Unqualified Resolved Top Level Symbol
     *     False - at line 1 col 13, module Prelude, Unqualified Resolved Top Level Symbol
     *     True  - at line 2 col 21, module Prelude, Qualified Resolved Top Level Symbol
     *     arg   - at line 3 col 4,  no module,      Unqualified Argument
     * </pre>
     * @author Iulian Radu
     */
    public static final class AnalysedIdentifier {
        
        /**
         * Comparator object to order Analysed Identifier by increasing offset source position
         * of the unqualified name.
         */
        public  static final Comparator<AnalysedIdentifier> offsetPositionComparator = new OffsetPositionComparator();
        private static class OffsetPositionComparator implements Comparator<AnalysedIdentifier> {
            public int compare(AnalysedIdentifier identifier1, AnalysedIdentifier identifier2) {
                if ((identifier1 == null) || (identifier2 == null)) {
                    throw new NullPointerException();
                }                
                                
                SourceRange identifierOffsetRange1 = identifier1.getOffsetRange();
                SourceRange identifierOffsetRange2 = identifier2.getOffsetRange();
                
                int compareLines = compareInts (identifierOffsetRange1.getStartLine(), identifierOffsetRange2.getStartLine());
                if (compareLines != 0) {
                    // Different lines; order by lines
                    return compareLines;
                    
                } else {
                    // Positions on same line; order by column
                    
                    return compareInts (identifierOffsetRange1.getStartColumn(), identifierOffsetRange2.getStartColumn());
                }
            }
            
            private static int compareInts(int i1, int i2) {
                //note: it is a cute trick to return i1 - i2, but this doesn't work
                //for all ints due to overflow in int arithmetic.                   
                return (i1 < i2 ? -1 : (i1 == i2 ? 0 : 1));
            }
        }
        
        /**
         * Specifies the method of qualification for this identifier,
         * and whether this is an argument or top level symbol.
         * 
         * @author Iulian Radu
         */
        public static final class QualificationType {
            
            private final String enumType;
            private QualificationType(String type) {
                if (type == null) {
                    throw new NullPointerException();
                }
                enumType = type;
            }

            /** Indicates whether the identifier is successfully resolved to a top level symbol */
            public boolean isResolvedTopLevelSymbol() {
                return (this == QualifiedResolvedTopLevelSymbol) ||
                       (this == UnqualifiedResolvedTopLevelSymbol);
            }
            
            /** Indicates whether the identifier appears in qualified form within the code */
            public boolean isCodeQualified() {
                return (this == QualifiedResolvedTopLevelSymbol) ||
                       (this == QualifiedUnresolvedTopLevelSymbol);                      
            }
                       
            /** An unqualified argument (eg: arg1). Note that arguments are always unqualified in CAL. **/
            public static final QualificationType UnqualifiedArgument = new QualificationType("UnqualifiedArgument");
            
            /** A top level symbol qualified in code, which resolves successfully (eg: Prelude.True)*/ 
            public static final QualificationType QualifiedResolvedTopLevelSymbol = new QualificationType("QualifiedResolvedTopLevelSymbol");
            
            /** A top level symbol qualified in code, which does not resolve successfully (eg: Prelude.NonExiSt3nt) */
            public static final QualificationType QualifiedUnresolvedTopLevelSymbol = new QualificationType("QualifiedUnresolvedTopLevelSymbol");
            
            /** An unqualified top level symbol, which was successfully qualified to the local or an imported module 
             * (eg: 'not' in a module importing Prelude; 'not' in module Prelude)*/
            public static final QualificationType UnqualifiedResolvedTopLevelSymbol = new QualificationType("UnqualifiedResolvedTopLevelSymbol");
            
            /** An unqualified top level symbol, not qualified because it resolves to multiple external modules 
             * (eg: 'test1' existing in modules M1 and M2) */
            public static final QualificationType UnqualifiedAmbiguousTopLevelSymbol = new QualificationType("UnqualifiedAmbiguousTopLevelSymbol");
            
            /** An unqualified top level symbol, which cannot be qualified to any module (eg: non3xisTent) */
            public static final QualificationType UnqualifiedUnresolvedTopLevelSymbol = new QualificationType("UnqualifiedUnresolvedTopLevelSymbol");
                     
            
            /** A local variable appearing in unqualified form in the code. (eg: let localVar = .. in localVar) */
            public static final QualificationType UnqualifiedLocalVariable = new QualificationType("UnqualifiedLocalVariable");
                        
            /**
             * Converts enumeration to string.
             */
            @Override
            public String toString() {
                return enumType;
            }
        }
        
        /** Original identifier */
        private final SourceIdentifier identifier;
        
        /** The offset affecting the line position of the identifier and module name*/
        private int lineOffset;
        
        /** The offset affecting the column position of the identifier and its module name*/
        private int columnOffset;
        
        /** Specifies way in which this identifier was qualified **/
        private final QualificationType qualificationType;
        
        /** 
         * Name of the module which the identifier qualifies to.
         * This name is filled for identifiers which are qualified through map, and
         * left null for others.
         */
        private ModuleName newModuleName = null;
        
        /**
         * If {@link #newModuleName} is resolvable, then this is the minimally qualified module name that resolves to the same module.
         * Otherwise, this holds the same value as {@link #newModuleName}.
         * This name is filled for identifiers which are qualified through map, and
         * left null for others.
         */
        private ModuleName minimimallyQualifiedModuleName = null;
        
        /** Reference to the definition of this identifier, if this is known */
        private AnalysedIdentifier definitionIdentifier = null;
        
        /** Constructors */    
        
        private AnalysedIdentifier (SourceIdentifier identifier, QualificationType qualificationType) {
            if ((identifier == null) || (qualificationType == null)) {
                throw new IllegalArgumentException();
            }
            
            this.identifier = identifier;
            this.qualificationType = qualificationType;
            
            lineOffset = 0;
            columnOffset = 0;
        }
        
        /** 
         * Clones this object 
         * Note: References to definition identifiers are not modified.
         */
        AnalysedIdentifier makeCopy() {
            
            AnalysedIdentifier clone = new AnalysedIdentifier(this.identifier, this.qualificationType);
            
            clone.newModuleName = this.newModuleName;
            clone.minimimallyQualifiedModuleName = this.minimimallyQualifiedModuleName;
            clone.lineOffset = this.lineOffset;
            clone.columnOffset = this.columnOffset;
            clone.definitionIdentifier = this.definitionIdentifier;
            
            return clone;
        }
        
        /**
         * @return the new source range of the identifier.
         */
        public SourceRange getOffsetRange() {
            return makeOffsetRange(identifier.getSourceRange());
        }

        /**
         * Constructs a source range adjusted by the offset.
         * @param originalRange the original source range.
         * @return the source range adjusted by the offset.
         */
        private SourceRange makeOffsetRange(SourceRange originalRange) {
            int startLine = originalRange.getStartLine() + lineOffset;
            int endLine = originalRange.getEndLine() + lineOffset;
            
            int startCol;
            int endCol;
            
            // If the source range starts on the same line as the start of the identifier, then start column needs the offset.
            // If the source range ends on the same line as the start of the identifier, then its end column needs the offset too.
            if (identifier.hasRawModuleSourceRange()) {
                if (startLine == identifier.getRawModuleSourceRange().getStartLine()) {
                    
                    startCol = originalRange.getStartColumn() + columnOffset;
                    
                    if (originalRange.getStartLine() == originalRange.getEndLine()) {
                        endCol = originalRange.getEndColumn() + columnOffset;
                    } else {
                        endCol = originalRange.getEndColumn();
                    }
                    
                } else {
                    startCol = originalRange.getStartColumn();
                    endCol = originalRange.getEndColumn();
                }
                
            } else {
                startCol = originalRange.getStartColumn() + columnOffset;
                
                if (originalRange.getStartLine() == originalRange.getEndLine()) {
                    endCol = originalRange.getEndColumn() + columnOffset;
                } else {
                    endCol = originalRange.getEndColumn();
                }
            }
            
            return new SourceRange(
                new SourcePosition(startLine, startCol, originalRange.getStartSourcePosition().getSourceName()),
                new SourcePosition(endLine, endCol, originalRange.getEndSourcePosition().getSourceName()));
        }
        
        /**
         * @return the new source range of the module name in the identifier.
         */
        public SourceRange getOffsetModuleNameRange() {
            SourceRange rawModuleSourceRange = identifier.getRawModuleSourceRange();
            if (rawModuleSourceRange != null) {
                return makeOffsetRange(rawModuleSourceRange);
            } else {
                return null;
            }
        }
        
        /**
         * @return the original source range of the identifier.
         */
        public SourceRange getOriginalRange() {
            return identifier.getSourceRange();
        }
        
        /** Increment offset in position by the specified amount. */
        void addOffset (int lineOffset, int columnOffset) {
            this.lineOffset   += lineOffset;
            this.columnOffset += columnOffset;
        }
        
        public QualificationType getQualificationType() {
            return qualificationType;
        }
        
        public AnalysedIdentifier getDefinitionIdentifier() {
            return definitionIdentifier;
        }
        
        /** Sets the module name for an identifier qualified through map */
        void setModuleName(ModuleName moduleName, ModuleName minimimallyQualifiedModuleName) {
            this.newModuleName = moduleName;
            this.minimimallyQualifiedModuleName = minimimallyQualifiedModuleName;
        }
        
        /** Sets the reference to definition identifier */
        void setDefinitionIdentifier(AnalysedIdentifier definitionIdentifier) {
            this.definitionIdentifier = definitionIdentifier;
        }
        
        /** @return string representation of this object */
        @Override
        public String toString() {
            String result = identifier + " analysed type: " + qualificationType + " module: " + (getResolvedModuleName() == null ? "(empty)" : getResolvedModuleName().toSourceText());
            result += " at new range: " + getOffsetRange().toString();
            return result;
        }
        
        
        // SourceIdentifier methods
        
        public ModuleName getResolvedModuleName() {
            if (newModuleName == null) {
                return identifier.getResolvedModuleName();
            } else {
                return newModuleName;
            }
        }
        
        public ModuleName getMinimallyQualifiedModuleName() {
            if (minimimallyQualifiedModuleName == null) {
                return identifier.getMinimallyQualifiedModuleName();
            } else {
                return minimimallyQualifiedModuleName;
            }
        }
        
        /**
         * @return the original module name, as it appears lexically.
         */
        public ModuleName getRawModuleName() {
            return identifier.getRawModuleName();
        }
        
        public String getName() {
            return identifier.getName();
        }
        
        public SourceIdentifier.Category getCategory() {
            return identifier.getCategory();
        }
        
        public boolean hasRawModuleSourceRange() {
            return identifier.hasRawModuleSourceRange();
        }
    }
    
    /**
     * This class is a wrapper that encapsulates the results from the code analysis
     * @author Ken Wong
     * Creation Date Oct 2nd 2002
     */
    public static final class AnalysisResults {
        
        /** The type expression of the code */
        private final TypeExpr typeExpr;
        
        /** 
         * List (OffsetCompilerMessage) of compiler messages generated from the analysis.
         * These messages are adjusted to fit the visible (unqualified) code, and
         * are sorted by increasing source positions.  
         * Line numbers are 1-index based. */
        private final List<OffsetCompilerMessage> compilerMessages;
        
        /** The code qualification results */
        private QualificationResults qualificationResults;
        
        /**
         * Default constructor for the Results
         * @param typeExpr the type expression of the code
         * @param compilerMessages the adjusted compiler messages generated from analysing the code
         * @param qualificationResults results from code qualification
         */
        private AnalysisResults(
                TypeExpr typeExpr, 
                List<OffsetCompilerMessage> compilerMessages, 
                QualificationResults qualificationResults) {
            
            if (compilerMessages == null) {
                throw new IllegalArgumentException();
            }
            this.typeExpr = typeExpr;
            this.compilerMessages = compilerMessages;
            this.qualificationResults = qualificationResults;
        }
        
        /**
         * Accessor methods for the typeExpr
         * @return TypeExpr the Type expression captured in the results
         */
        public TypeExpr getTypeExpr() {
            return typeExpr;
        }
        
        /**
         * Accessor methods for the compiler message generated by compiler
         * @return list of OffsetCompilerMessage
         */
        public List<OffsetCompilerMessage> getCompilerMessages() {
            return Collections.unmodifiableList(compilerMessages);
        }
        
        /**
         * Indicates whether code analysis was performed successfully
         * (ie: the code was successfully parsed, identifiers extracted, 
         * and qualification map updated)
         *  
         * If analysis is not successful, then this structure contains
         * only compiler messages.
         *  
         * @return whether analysis was successful
         */
        public boolean analysisSuccessful() {
            return (qualificationResults != null);
        }
        
        /**
         * Retrieves the unqualified names of all arguments found in the code
         * The arguments are ordered by first appearance in code, and duplicates are not included. 
         * @return String[]
         */
        public String[] getAllArgumentNames() {
            if (analysisSuccessful()) {
                return qualificationResults.getAllArgumentNames();
            } else {
                return new String[0];
            }
        }
        
        /** 
         * Accessor method for qualifiedCode
         * If analysis was not successful, an empty string is returned.
         * @return fully qualified code text
         */
        public String getQualifiedCode() {
            if (analysisSuccessful()) {
                return qualificationResults.getQualifiedCode();
            } else {
                return "";
            }
        }
        
        /** 
         * Accessor method for qualification map
         * If analysis was not successful, an empty map is returned
         * @return mapping from unqualified to qualified names
         */
        public CodeQualificationMap getQualificationMap() {
            if (analysisSuccessful()) {
                return qualificationResults.getQualificationMap().makeCopy();
            } else {
                return new CodeQualificationMap();
            }
        }
        
        /**
         * Accessor for analysedIdentifiers
         * If analysis was not successful, an empty list is returned.
         * @return list (AnalysedIdentifier) names, categories, positions of 
         *         qualified and unqualified identifiers from the original code, 
         *         ordered by their appearance in code.
         */
        public List<AnalysedIdentifier> getAnalysedIdentifiers() {
            if (analysisSuccessful()) {
                return qualificationResults.getAnalysedIdentifiers();
            } else {
                return new ArrayList<AnalysedIdentifier>();
            }
        }
    }

    /**
     * This class is a wrapper that encapsulates the results from fully qualifying
     * a piece of code.
     * 
     * @author Iulian Radu
     * Creation Date Feb 8th 2004
     */
    public static final class QualificationResults {
        
        /** 
         * The fully qualified code text 
         * This is null if parse errors were encountered while inspecting
         * the original code. 
         */
        private String qualifiedCode;
        
        /** 
         * Mapping of unqualified to qualified names.
         * 
         * Code arguments do not appear in this map, and can be 
         * found in the argumentNames field. 
         */
        private CodeQualificationMap qualificationMap;
        
        /** Array containing the unqualified names of all arguments found in the code */
        private String[] unqualifiedArgumentNames;
        
        /** 
         * (AnalysedIdentifier) The list of identifiers, along with category, position, and 
         * method of qualification, which are found in the code text. The list is ordered by 
         * increasing source positions; identifiers with no positions are kept at the end of the list.
         * 
         * The positions of these identifiers match the visible (unqualified) code.
         */
        private List<AnalysedIdentifier> analysedIdentifiers;
        
        /**
         * Constructor for the Results
         * @param unqualifiedArgumentNames the unqualified names of arguments found in code
         * @param qualifiedCode the fully qualified code
         * @param qualificationMap map identifying qualifications
         * @param analysedIdentifiers occurrences of unqualified identifiers in code 
         */
        private QualificationResults(
                String[] unqualifiedArgumentNames,
                String qualifiedCode,
                CodeQualificationMap qualificationMap,
                List<AnalysedIdentifier> analysedIdentifiers) {
            if ((unqualifiedArgumentNames == null) || (qualifiedCode == null) || (qualificationMap == null) || (analysedIdentifiers == null)) {
                throw new IllegalArgumentException();
            }
            this.unqualifiedArgumentNames = unqualifiedArgumentNames;
            this.qualifiedCode = qualifiedCode;
            this.qualificationMap = qualificationMap;
            this.analysedIdentifiers = analysedIdentifiers;
        }
        
        /**
         * @return String[] array of unqualified names of all arguments,
         *         ordered by first appearance in code, not including duplicates
         */
        public String[] getAllArgumentNames() {
            return unqualifiedArgumentNames;
        }
        
        /**
         * Removes all argument names.
         * This method is invoked by analyseCode when the 
         * compiled code is not allowed to have arguments.
         */
        private void resetArgumentNames() {
            unqualifiedArgumentNames = new String[0];
        }
        
        /** Accessor method for qualifiedCode
         * @return fully qualified code text
         */
        public String getQualifiedCode() {
            return qualifiedCode;
        }
        
        /** Accessor method for qualification map
         * @return mapping from unqualified to qualified names
         */
        public CodeQualificationMap getQualificationMap() {
            return qualificationMap.makeCopy();
        }
        
        /**
         * Accessor for analysedIdentifiers
         * @return list (AnalysedIdentifiers) names, categories, positions of qualified 
         *         and unqualified identifiers, ordered by their appearance in code
         */
        public List<AnalysedIdentifier> getAnalysedIdentifiers() {
            return (analysedIdentifiers == null? null : Collections.unmodifiableList(analysedIdentifiers));
        }
        
        /**
         * Sets the qualified code as specified.
         * This method is used by analyseCode for removing its added padding on the code text.
         * @param newQualifiedCode
         */
        private void setQualifiedCode(String newQualifiedCode) {
            qualifiedCode = newQualifiedCode;
        }
        
        /**
         * Sets the analysed identifiers as specified.
         * This method is used by analyseCode for removing its added padding on the identifier positions.
         * @param analysedIdentifiers
         */
        private void setAnalysedIdentifiers(List<AnalysedIdentifier> analysedIdentifiers) {
            this.analysedIdentifiers = analysedIdentifiers;
        }
    }

    /**
     * Results from intermediary symbol analysis by method extractArgumentsAndQualifications()  
     * @author Iulian Radu
     */
    private static class ExtractionResults {
        
        /** (AnalysedIdentifiers) Identifiers with proper qualification type and module name */
        private final List<AnalysedIdentifier> analysedIdentifiers;
        
        /** Unqualified names of arguments which appear in the code */
        private final Set<String> usedArgumentNames;
        
        /** Updated qualification map with resolved unqualified symbols */
        private final CodeQualificationMap qualificationMap;
        
        ExtractionResults(List<AnalysedIdentifier> analysedIdentifiers, Set<String> usedArgumentNames, CodeQualificationMap qualificationMap) {
            if ((usedArgumentNames == null) || (qualificationMap == null) || (analysedIdentifiers == null)) {
                throw new IllegalArgumentException();
            }
            this.analysedIdentifiers = analysedIdentifiers;
            this.usedArgumentNames = usedArgumentNames;
            this.qualificationMap = qualificationMap;
        }
        // Accessors
        
        /**
         * @return the list of analysed identifiers; this is ordered by 
         *         occurrences in code (ie: source position of identifiers).
         */
        public List<AnalysedIdentifier> getAnalysedIdentifiers() {
            return analysedIdentifiers;
        }
        
        /**
         * @return the set of unqualified argument names which appear in the code,
         *         ordered by first appearance, and not including duplicates.
         */
        public Set<String> getArgumentNames() {
            return usedArgumentNames;
        }
        
        /**
         * @return code qualification map
         */
        public CodeQualificationMap getQualificationMap() {
            return qualificationMap;
        }
    } 
    
    /** Type information for the module which analysed code belongs to */
    private ModuleTypeInfo moduleTypeInfo;
    
    /** Object to use for type checking source */
    private TypeChecker typeChecker;
    
    /** Whether the code is allowed to have arguments */
    private boolean allowNewArguments;
    
    /** Whether to qualify symbols which belong to multiple modules */
    private boolean qualifyAmbiguousNames;
    
    /** 
     * Construct code analyser 
     * 
     * @param typeChecker type checker to use
     * @param moduleTypeInfo information for the module the code belongs to
     * @param allowNewArguments whether the code is allowed to have new arguments
     * @param qualifyAmbiguousNames whether to qualify symbols which belong to multiple modules 
     */
    public CodeAnalyser(TypeChecker typeChecker, ModuleTypeInfo moduleTypeInfo, boolean allowNewArguments, boolean qualifyAmbiguousNames) {
        
        if ((typeChecker == null) || (moduleTypeInfo == null)) {
            throw new IllegalArgumentException();
        }
        
        this.moduleTypeInfo = moduleTypeInfo;
        this.typeChecker = typeChecker;
        this.allowNewArguments = allowNewArguments;
        this.qualifyAmbiguousNames = qualifyAmbiguousNames;
    }
    
    /**
     * Analyses the code and extracts information about identifiers found in code, 
     * identifies arguments, qualifies symbols, and determines the type expression
     * of the fully qualified code.
     * 
     * @param codeText the code to be analysed
     * @param varNamesWhichAreArgs set (String) of variable names that should be arguments (assumed nonexistent if null)
     * @param previousQualificationMap mapping from unqualified to qualified names (assumed nonexistent if null)
     * @return CodeAnalyser.AnalysisResults
     */
    public AnalysisResults analyseCode(
            String codeText, 
            Set<String> varNamesWhichAreArgs, 
            CodeQualificationMap previousQualificationMap) {

        if (codeText == null) {
            throw new IllegalArgumentException();
        }
        
        if (codeText.length() == 0) {
            return new AnalysisResults(null, new ArrayList<OffsetCompilerMessage>(), new QualificationResults(new String[0], "", new CodeQualificationMap(), new ArrayList<AnalysedIdentifier>()));
        }
        
        // Build the object that we'll store the results in.
        CompilerMessageLogger logger = new MessageLogger ();
        
        // Add a linefeed at the end to assure parsing is done correctly.
        // Also add one in the front so that the extra code added in is on its own line.
        codeText = "\n" + codeText + "\n";
        
        // Qualify code
        
        QualificationResults qualificationResults = qualifyExpression(
                                                            codeText, 
                                                            varNamesWhichAreArgs, 
                                                            previousQualificationMap, 
                                                            logger);
        if (qualificationResults == null) {
            
            // Expression failed to be parsed. 
            // Make sure messages are adjusted to visible code, and report them.
               
            List<CompilerMessage> compilerMessages = logger.getCompilerMessages(CompilerMessage.Severity.ERROR);        
            List<OffsetCompilerMessage> offsetMessages = adjustMessagesToVisibleCode(compilerMessages, null, null);
            return new AnalysisResults(null, offsetMessages, null); 
        }
        
        String qualifiedCode = qualificationResults.getQualifiedCode();
        
        // Use the other unmatched names as arguments
        
        if (!allowNewArguments) {
            // If we are not allowed to have arguments we create an empty array.
            qualificationResults.resetArgumentNames();
        }
        
        // Check and extract type of fully qualified code
        
        TypeCheckInfo info = typeChecker.getTypeCheckInfo(moduleTypeInfo.getModuleName());
        TypeExpr typeExpr = CodeAnalyser.typeCheckSource(qualificationResults.getQualifiedCode(), qualificationResults.getAllArgumentNames(), info, logger);
        
        // Align positions of identifiers and messages with visible code 
        
        List<AnalysedIdentifier> offsetIdentifiers = adjustIdentifiersToVisibleCode(qualificationResults.getAnalysedIdentifiers());
        qualificationResults.setAnalysedIdentifiers(offsetIdentifiers);
        
        List<CompilerMessage> compilerMessages = logger.getCompilerMessages(CompilerMessage.Severity.ERROR);        
        List<OffsetCompilerMessage> offsetMessages = adjustMessagesToVisibleCode(
                compilerMessages, offsetIdentifiers, qualificationResults.getQualificationMap());

        
        // Adjust code by removing the padded beginning and end newline
        
        qualifiedCode = qualifiedCode.substring(1, qualifiedCode.length()-1);
        qualificationResults.setQualifiedCode(qualifiedCode);
        
        // Return results object        
        return new AnalysisResults(typeExpr, offsetMessages, qualificationResults);
    }
    
    /**
     * Extract any free variables (to be treated as arguments) from the set of qualified and unqualified
     * symbol positions, and update the qualification map with entries if unqualified identifiers
     * can be qualified. If the qualification map supplied contains entries which are not used, 
     * the respective entries are removed. 
     * 
     * The SourceIdentifier objects of the supplied list are converted to AnalysedIdentifier 
     * objects, with proper qualification type. If the identifier belongs to the current module,
     * is fully qualified in code, or can be successfully resolved, then its module name 
     * is also filled with the proper module. 
     *  
     * @param symbolPositions list (SourceIdentifiers) of qualified and unqualified symbols within the code
     *                           (code is assumed erroneous if this is null)                 
     * @param varNamesWhichAreArgs set (String) of variable names which are known arguments to the code
     *                           (assumed nonexistent if null)
     * @param previousQualificationMap previously used qualification map (assumed nonexistent if null) 
     * @return CodeAnalyser.ExtractionResults containing used arguments, analysed identifiers and updated map
     */
    private ExtractionResults extractArgumentsAndQualifications(
                                List<SourceIdentifier> symbolPositions,
                                Set<String> varNamesWhichAreArgs,
                                CodeQualificationMap previousQualificationMap) {
       
        // Make sure we use a valid qualification map
        CodeQualificationMap qualificationMap;
        if (previousQualificationMap == null) {
            qualificationMap = new CodeQualificationMap();
        } else {
            qualificationMap = previousQualificationMap.makeCopy();
        }
        
        // Make sure we use a valid varNamesWhichAreArgs object
        if (varNamesWhichAreArgs == null) {
            varNamesWhichAreArgs = new LinkedHashSet<String>();
        }
        
        // Do not continue if parsing did not succeed
        if (symbolPositions == null) {
            removeUnusedMapEntries(null, qualificationMap);
            return new ExtractionResults(new ArrayList<AnalysedIdentifier>(), new LinkedHashSet<String>(), qualificationMap);
        }
        
        
        // (String) These sets keep track of valid argument names found in the code                
        Set<String> foundArgumentNames = new LinkedHashSet<String>();
        
        // (SourceIdentifier -> AnalysedIdentifiers) Mapping between local variable definition 
        // source and analysed identifiers 
        Map<SourceIdentifier, AnalysedIdentifier> localDefinitionsMap = new HashMap<SourceIdentifier, AnalysedIdentifier>();
        
        // (AnalysedIdentifier -> SourceIdentifier) 
        // Mapping between reference analysed identifiers and their definition source identifiers.
        //
        // It is possible for references to come before their definitions (eg: "let a=b+1; b=2; in..").
        // This list will contain analysed identifiers which are local variable references, but which 
        // could not be linked to their definition analysed identifiers in the initial pass through the
        // source identifier list.   
        Map<AnalysedIdentifier, SourceIdentifier> localUndefinedReferencesMap = new HashMap<AnalysedIdentifier, SourceIdentifier>();
        
        // (AnalysedIdentifiers) will hold the analysed source identifiers
        List<AnalysedIdentifier> analysedIdentifiers = new ArrayList<AnalysedIdentifier>();
        
        // Analyse all identifier positions found in code, and construct analysed identifiers
        
        for (int i = 0, n = symbolPositions.size(); i < n; i++) {
            SourceIdentifier symbolPosition = symbolPositions.get(i);
            
            // Is the symbol a local variable ?
            if (isLocalVariable(symbolPosition)) {
                analysedIdentifiers.add(analyseLocalVariable(symbolPosition, localDefinitionsMap, localUndefinedReferencesMap));
                continue;
            }
            
            // Is the symbol already qualified in code ?
            if (isQualifiedSymbol(symbolPosition)) {
                analysedIdentifiers.add(analyseQualifiedSymbol(symbolPosition));
                continue;
            }
            
            // Symbol is not qualified; was it found to be an argument ?
            if (isExistingUnqualifiedArgument(symbolPosition, qualificationMap, varNamesWhichAreArgs, foundArgumentNames)) {
                
                analysedIdentifiers.add(analyseExistingUnqualifiedArgument(symbolPosition, qualificationMap, foundArgumentNames));
                continue;
            }
            
            // Finally, the unqualified symbol not a supplied argument, 
            // so determine if it is locally or externally qualified, a new argument, or unknown.
            
            AnalysedIdentifier analysedIdentifier = 
                analyseUnqualifiedNewArgumentOrTopLevelSymbol(symbolPosition, foundArgumentNames, qualificationMap);
            if (analysedIdentifier == null) {
                return null;
            }
            analysedIdentifiers.add(analysedIdentifier);
        }
        
        // Now link identifiers whose references come before their definitions
        
        for (final Map.Entry<AnalysedIdentifier, SourceIdentifier> entry : localUndefinedReferencesMap.entrySet()) {
            final AnalysedIdentifier analysedIdentifier = entry.getKey();
            final SourceIdentifier sourceIdentifier = entry.getValue();
            AnalysedIdentifier definitionIdentifier = localDefinitionsMap.get(sourceIdentifier);
            if (definitionIdentifier == null) {
                throw new IllegalStateException("Local reference identifier does not have a corresponding definition identifier");
            }
            analysedIdentifier.setDefinitionIdentifier(definitionIdentifier);
        }
        
        // Remove unused entries from map
        removeUnusedMapEntries(analysedIdentifiers, qualificationMap);
        
        return new ExtractionResults(analysedIdentifiers, foundArgumentNames, qualificationMap);
    }
  
    /**
     * Qualifies the unqualified external top level symbols in the specified 
     * code according to the qualification map. 
     *  
     * @param originalCode original (visible) code
     * @param identifiers locations of symbols within code. 
     *                    Note: these locations must match the code supplied.
     * @param qualifyAllSymbols whether to qualify all symbols 
     *        (if false, only unqualified symbols mapped to external modules are qualified)
     * @return fully qualified code
     */
    public String replaceUnqualifiedSymbols(
                String originalCode,
                List<AnalysedIdentifier> identifiers,
                boolean qualifyAllSymbols) {
        
        if (identifiers == null) {
            return originalCode;
        }
        
        // Qualify names by using a Renamer object with replacements
        // of each occurrence of unqualified identifiers.
        
        SourceModifier codeReplacements = new SourceModifier();
        
        for (final AnalysedIdentifier identifier : identifiers) {
            
            String unqualifiedName = identifier.getName();
            ModuleName moduleNameOrNull = identifier.getResolvedModuleName();
            
            QualifiedName qualifiedName;
            if (!qualifyAllSymbols) {
                // Not qualifying all symbols, so just look at the non-local mapped ones
                if ((identifier.getQualificationType() != AnalysedIdentifier.QualificationType.UnqualifiedResolvedTopLevelSymbol) ||
                    (moduleTypeInfo.getModuleName().equals(moduleNameOrNull))) {
                    continue;
                }
                if (moduleNameOrNull == null) {
                    // Is not qualified through map
                    continue;
                }
                qualifiedName = QualifiedName.make(moduleNameOrNull, unqualifiedName);
                
            } else {
                
                // Qualifying all symbols which have a module attached to them
                if (moduleNameOrNull == null) {
                    // Is not qualified through map
                    continue;
                }
                if (identifier.getQualificationType().isCodeQualified()) {
                    if (identifier.getResolvedModuleName().equals(identifier.getRawModuleName())) {
                        // Appears in fully qualified form in code
                        continue;
                    }
                }
                if (identifier.getCategory() == SourceIdentifier.Category.LOCAL_VARIABLE_DEFINITION) {
                    // Do not qualify definitions
                    continue;
                }
                if (identifier.getCategory() == SourceIdentifier.Category.LOCAL_VARIABLE) {
                    // Do not qualify local variables 
                    continue;
                }
                qualifiedName = QualifiedName.make(moduleNameOrNull, unqualifiedName);
            }
            
            if (qualifiedName != null) {
                
                SourceRange identifierOffsetModuleNameRange = identifier.getOffsetModuleNameRange();
                
                if (identifierOffsetModuleNameRange != null) {
                    codeReplacements.addSourceModification(makeReplaceText(originalCode, identifierOffsetModuleNameRange, qualifiedName.getModuleName().toSourceText()));
                    
                } else {
                    
                    SourceRange identifierOffsetUnqualifiedNameRange = identifier.getOffsetRange();
                    codeReplacements.addSourceModification(makeReplaceText(originalCode, identifierOffsetUnqualifiedNameRange, qualifiedName.getQualifiedName()));
                }
                
            } else {
                throw new IllegalStateException("Unqualified resolved symbol is not qualified through map");
            }
        }
        return codeReplacements.apply(originalCode);
    }
    
    /**
     * Checks the unqualified identifier is contained and visible within the specified module.
     * 
     * @param importedModuleTypeInfo type info for the module we are searching
     * @param unqualifiedName unqualified name of identifier
     * @param type identifier category
     * @param currentModuleTypeInfo  this module must be either the same as, or import 'importedModuleTypeInfo'. 
     * @return True if identifier contained and visible in the module; False otherwise.
     */
    private static boolean moduleContainsIdentifier(ModuleTypeInfo importedModuleTypeInfo, String unqualifiedName, SourceIdentifier.Category type, ModuleTypeInfo currentModuleTypeInfo) {
        ScopedEntity entity = null;
        
        // Get entity, depending on its type
        if (type == SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD) {
            entity = importedModuleTypeInfo.getFunctionOrClassMethod(unqualifiedName);
        } else if (type == SourceIdentifier.Category.DATA_CONSTRUCTOR) {
            entity = importedModuleTypeInfo.getDataConstructor(unqualifiedName);
        } else if (type == SourceIdentifier.Category.TYPE_CONSTRUCTOR) {
            entity = importedModuleTypeInfo.getTypeConstructor(unqualifiedName);
        } else if (type == SourceIdentifier.Category.TYPE_CLASS) {
            entity = importedModuleTypeInfo.getTypeClass(unqualifiedName);
        } else { // Unknown type
            throw new IllegalArgumentException();
        }
        
        if (entity == null) {
            return false;
        }
        
        // If entity exists and is visible, we can use it from this module 
        return currentModuleTypeInfo.isEntityVisible(entity);        
    }
    
    /**
     * Retrieves a list of modules containing the specified identifier.
     * If the symbol is contained within the current module, this module name 
     * is at the front of the returned list. The rest of the module names are
     * stored in their import order.
     * 
     * @param unqualifiedName
     * @param type
     * @param currentModuleTypeInfo
     * @return list (ModuleName) of module names containing this identifier
     */
    public static List/*ModuleName*/<ModuleName> getModulesContainingIdentifier(
            String unqualifiedName, 
            SourceIdentifier.Category type, 
            ModuleTypeInfo currentModuleTypeInfo) {
        
        if (unqualifiedName == null || type == null || currentModuleTypeInfo == null) {
            throw new NullPointerException();
        }
        
        List/*ModuleName*/<ModuleName> moduleNames = new ArrayList<ModuleName>();       
        
        // Check if current module contains the identifier 
        if (moduleContainsIdentifier(currentModuleTypeInfo, unqualifiedName, type, currentModuleTypeInfo)) {
            moduleNames.add(currentModuleTypeInfo.getModuleName());
        }
        
        // Check if any imported module contains the identifier 
        int nImportedModules = currentModuleTypeInfo.getNImportedModules();
        for (int i = 0; i < nImportedModules; ++i) {
            ModuleTypeInfo importedModuleTypeInfo = currentModuleTypeInfo.getNthImportedModule(i);
            if (moduleContainsIdentifier(importedModuleTypeInfo, unqualifiedName, type, currentModuleTypeInfo)) {
                moduleNames.add(importedModuleTypeInfo.getModuleName());
            }
        }
        
        return moduleNames;
    }
  
    /**
     * Retrieve the entity object which corresponds to the qualified name, if accessible from
     * the specified module.
     * 
     * @param qualifiedName qualified entity name
     * @param type entity type
     * @param moduleTypeInfo type info for the module searched
     * @return scoped entity; null if none found
     */
    public static ScopedEntity getVisibleModuleEntity(QualifiedName qualifiedName, SourceIdentifier.Category type, ModuleTypeInfo moduleTypeInfo) {
        if ((qualifiedName == null) || (moduleTypeInfo == null)) {
            throw new IllegalArgumentException();
        }
        
        ScopedEntity entity = null;
        
        // Get entity, depending on its type
        
        if (type == SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD) {
            entity = moduleTypeInfo.getVisibleFunction(qualifiedName);
            if (entity == null) {
                entity = moduleTypeInfo.getVisibleClassMethod(qualifiedName);
            }
            
        } else if (type == SourceIdentifier.Category.DATA_CONSTRUCTOR) {
            entity = moduleTypeInfo.getVisibleDataConstructor(qualifiedName);
            
        } else if (type == SourceIdentifier.Category.TYPE_CONSTRUCTOR) {
            entity = moduleTypeInfo.getVisibleTypeConstructor(qualifiedName);
            
        } else if (type == SourceIdentifier.Category.TYPE_CLASS) {
            entity = moduleTypeInfo.getVisibleTypeClass(qualifiedName);
            
        } else { // Unknown type
            throw new IllegalArgumentException();
        }
        
        return entity;
    }
    
    /**
     * Resolve the specified unqualified name into a qualified name according to
     * CAL name resolution rules. In particular, if the named entity exists
     * within the specified module, the qualified name returned would reference
     * the specified module. Otherwise, the import statements of the specified
     * module are checked to see if the name appears in the appropriate using
     * clause. If so, then the qualified name would reference the module named
     * by the import statement. If the name is still not resolved, then null is
     * returned.
     * 
     * @param unqualifiedName
     *            the unqualified name to be qualified.
     * @param type
     *            the type of the entity.
     * @param moduleTypeInfo
     *            type info for the module checked.
     * @return a qualified name representing the named entity, or null if there
     *         is no entity visible according to CAL name resolution rules by
     *         that name.
     */
    public static QualifiedName resolveEntityNameAccordingToImportUsingClauses(String unqualifiedName, SourceIdentifier.Category type, ModuleTypeInfo moduleTypeInfo) {
        if (unqualifiedName == null) {
            throw new NullPointerException();
        }
        
        ModuleName resolvedModuleName = null;
        
        if (type == SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD) {
            if (moduleTypeInfo.getFunctionOrClassMethod(unqualifiedName) != null) {
                resolvedModuleName = moduleTypeInfo.getModuleName();
            } else {
                resolvedModuleName = moduleTypeInfo.getModuleOfUsingFunctionOrClassMethod(unqualifiedName);
            }
        } else if (type == SourceIdentifier.Category.DATA_CONSTRUCTOR) {
            if (moduleTypeInfo.getDataConstructor(unqualifiedName) != null) {
                resolvedModuleName = moduleTypeInfo.getModuleName();
            } else {
                resolvedModuleName = moduleTypeInfo.getModuleOfUsingDataConstructor(unqualifiedName);
            }
        } else if (type == SourceIdentifier.Category.TYPE_CONSTRUCTOR) {
            if (moduleTypeInfo.getTypeConstructor(unqualifiedName) != null) {
                resolvedModuleName = moduleTypeInfo.getModuleName();
            } else {
                resolvedModuleName = moduleTypeInfo.getModuleOfUsingTypeConstructor(unqualifiedName);
            }
        } else if (type == SourceIdentifier.Category.TYPE_CLASS) {
            if (moduleTypeInfo.getTypeClass(unqualifiedName) != null) {
                resolvedModuleName = moduleTypeInfo.getModuleName();
            } else {
                resolvedModuleName = moduleTypeInfo.getModuleOfUsingTypeClass(unqualifiedName);
            }
        } else { // Unknown type
            throw new IllegalArgumentException();
        }
        
        if (resolvedModuleName != null) {
            return QualifiedName.make(resolvedModuleName, unqualifiedName);
        } else {
            return null;
        }
    }
    
    /**
     * Adjust positions of compiler messages to fit the visible (unqualified) code.
     * 
     * @param compilerMessages list of original CompilerMessage objects
     * @param identifiers positions of symbols within original code (set to null if parsing failed) 
     * @param qualificationMap qualification map, used for determining offsets (set to null if parsing failed)
     * @return List of OffsetCompilerMessages sorted by increasing adjusted source position
     */
    private List<OffsetCompilerMessage> adjustMessagesToVisibleCode(
                List<CompilerMessage> compilerMessages, 
                List<AnalysedIdentifier> identifiers,
                CodeQualificationMap qualificationMap) {
        
        if (compilerMessages == null) {
            return null;
        }
        
        // Create a list of adjusted messages from the specified messages
        
        List<OffsetCompilerMessage> adjustedMessages = new ArrayList<OffsetCompilerMessage>();
        for (final CompilerMessage message : compilerMessages) {           
        
            // The code was padded with one newline, so messages must be shifted up
            adjustedMessages.add(new OffsetCompilerMessage(message, -1, 0));
        }
        Collections.sort(adjustedMessages, OffsetCompilerMessage.sourcePositionComparator);
        
        // Adjust position of messages by accounting for each qualified symbol.
        
        if (identifiers == null) {
            return adjustedMessages;
        }
        
        for (final AnalysedIdentifier symbol : identifiers) {
                       
            String originalSymbolName = symbol.getName();
            
            if ((symbol.getQualificationType() != AnalysedIdentifier.QualificationType.UnqualifiedResolvedTopLevelSymbol) ||
                (moduleTypeInfo.getModuleName().equals(symbol.getResolvedModuleName()))) {
                // This symbol is was not qualified to an external module through map, so ignore it
                continue;
            } 

            // Only mappings to different name lengths affect message positions
            QualifiedName qualifiedSymbolName = qualificationMap.getQualifiedName(originalSymbolName, symbol.getCategory());
            
            if (qualifiedSymbolName != null && !qualifiedSymbolName.getModuleName().equals(symbol.getRawModuleName())) {
                
                // Symbol was mapped and compiler messages should be shifted.
                // Go through sorted compiler messages and update the necessary ones
                
                for (final OffsetCompilerMessage message : adjustedMessages) {
                    
                    // There might be messages with no associated position; the sorting
                    // puts these at the end of the message list.
                    if (!message.hasPosition()) {
                        break;
                    }
                    
                    // Look for compiler messages on the same line as the symbol;
                    // stop iterating through messages once they exceed symbol line.
                    
                    SourceRange symbolOffsetRange = symbol.getOffsetRange();
                    
                    // There is an assumption that the message was reported on a symbol that
                    // does not contain whitespaces
                    // e.g. A.B.C
                    //   instead of:
                    // A .
                    // B  .  C
                    
                    int symbolOffsetLine = symbolOffsetRange.getStartLine();
                    int symbolOffsetColumn = symbolOffsetRange.getStartColumn();
                    
                    if (symbolOffsetLine < message.getOffsetLine()) {
                        break;
                    }
                    
                    if ((message.getOffsetLine() == symbolOffsetLine) &&
                        (symbolOffsetColumn < message.getOffsetColumn())) {
                        // Symbol is on same line, to the left of the message
                        
                        // This offset holds the extra length generated by the replacement
                        int offsetLength = qualifiedSymbolName.getQualifiedName().length() - originalSymbolName.length();
                        
                        // If error hits on the module part of the name,
                        if ((message.getOffsetColumn() >= symbolOffsetColumn) &&
                            (message.getOffsetColumn() < (symbolOffsetColumn + offsetLength))) {
                            
                            // Point error to the beginning of our name in visible code
                            message.addOffset(0, symbolOffsetColumn - message.getOffsetColumn());
                            
                        } else {
                            // Error not pointing on our module, so shift error left by the offset
                            message.addOffset(0, -offsetLength);
                        }
                        
                        // adjust for the fact that the symbol might have originally been partially qualified (and span more than one line!)
                        SourceRange symbolOffsetModuleNameRange = symbol.getOffsetModuleNameRange();
                        if (symbolOffsetModuleNameRange != null) {
                            message.addOffset(
                                symbolOffsetRange.getEndLine() - symbolOffsetModuleNameRange.getStartLine(),
                                (symbolOffsetRange.getEndColumn() - originalSymbolName.length()) - symbolOffsetModuleNameRange.getStartColumn());
                        }
                    }
                }
            }
        }
        
        // The appropriate compiler messages have been shifted, and
        // will be returned.
        
        return adjustedMessages;
    }

    /**
     * Adjust analysed source identifiers to fit the visible code
     * 
     * @param analysedIdentifiers list of identifiers to be adjusted
     * @return List (AnalysedIdentifier) identifiers matching visible code
     */ 
    private List<AnalysedIdentifier> adjustIdentifiersToVisibleCode(List<AnalysedIdentifier> analysedIdentifiers) {
        if (analysedIdentifiers == null) {
            return null;
        }
        
        Map<AnalysedIdentifier, AnalysedIdentifier> adjustedIdentifiers = new LinkedHashMap<AnalysedIdentifier, AnalysedIdentifier>();
        
        // Copy and adjust identifiers to the proper position 
        
        for (final AnalysedIdentifier oldIdentifier : analysedIdentifiers) {
            
            AnalysedIdentifier newIdentifier = oldIdentifier.makeCopy();
            
            // The code is padded with one newline, so identifiers need to be shifted up
            newIdentifier.addOffset(-1,0);
            adjustedIdentifiers.put(oldIdentifier, newIdentifier);
        }
        
        // Now update references for identifiers which have links to definitions 
        // (these were not updated in the previous copy pass since a reference may appear before a definition)
        
        for (final AnalysedIdentifier adjustedIdentifier : adjustedIdentifiers.values()) {
            
            AnalysedIdentifier oldDefinitionIdentifier = adjustedIdentifier.getDefinitionIdentifier();
            if (oldDefinitionIdentifier != null) {
                AnalysedIdentifier newDefinitionIdentifier = adjustedIdentifiers.get(oldDefinitionIdentifier);
                if (newDefinitionIdentifier == null) {
                    throw new IllegalStateException("Attempting to copy identifier reference whose definition was not copied");
                }
                adjustedIdentifier.setDefinitionIdentifier(newDefinitionIdentifier);
            }
        }
        
        return new ArrayList<AnalysedIdentifier>(adjustedIdentifiers.values());
    }
    
    /**
     * Checks that entries in qualification map are still valid 
     * (ie: the qualifications contained still successfully resolve)
     * 
     * @param qualificationMap map to check
     * @return True if entries are still valid; False if qualification map
     *         needs to be revisited since entities do not qualify anymore.
     */
    public boolean checkQualificationMapValidity(CodeQualificationMap qualificationMap) {
        
        if (qualificationMap == null) {
            throw new IllegalArgumentException();
        }
        
        Set<QualifiedName> qualifiedFunctions = qualificationMap.getQualifiedNames(SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD);
        for (final QualifiedName name : qualifiedFunctions) {
            if ((moduleTypeInfo.getVisibleFunction(name) == null) &&
                (moduleTypeInfo.getVisibleClassMethod(name) == null)) {
                return false;
            }
        }
        
        Set<QualifiedName> qualifiedConstructors = qualificationMap.getQualifiedNames(SourceIdentifier.Category.DATA_CONSTRUCTOR);
        for (final QualifiedName qualifiedName : qualifiedConstructors) {
            if (moduleTypeInfo.getVisibleDataConstructor(qualifiedName) == null) {
                return false;
            }
        }
        
        Set<QualifiedName> qualifiedTypes = qualificationMap.getQualifiedNames(SourceIdentifier.Category.TYPE_CONSTRUCTOR);
        for (final QualifiedName qualifiedName : qualifiedTypes) {
            if (moduleTypeInfo.getVisibleTypeConstructor(qualifiedName) == null) {
                return false;
            }
        }
        
        Set<QualifiedName> qualifiedClasses = qualificationMap.getQualifiedNames(SourceIdentifier.Category.TYPE_CLASS);
        for (final QualifiedName qualifiedName : qualifiedClasses) {
            if (moduleTypeInfo.getVisibleTypeClass(qualifiedName) == null) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Garbage collection function for removing unused map entries.
     * 
     * Ensures that the map contains only entries which qualify
     * the specified identifier position set. If the positions
     * are empty, then all map entries are deleted.
     *  
     * @param identifierPositions list (AnalysedIdentifiers) of analysed identifiers
     * @param qualificationMap map to simplify
     */
    private static void removeUnusedMapEntries(List<AnalysedIdentifier> identifierPositions, CodeQualificationMap qualificationMap) {
        
        // Separate identifiers into type sets
        
        Set<String> usedFunctions = new HashSet<String>();
        Set<String> usedTypes = new HashSet<String>();
        Set<String> usedClasses = new HashSet<String>();
        Set<String> usedConstructors = new HashSet<String>();
        if (identifierPositions != null) {
            for (final AnalysedIdentifier identifier : identifierPositions) {
                String name = identifier.getName();
                SourceIdentifier.Category type = identifier.getCategory();
                
                if (identifier.getQualificationType() != AnalysedIdentifier.QualificationType.UnqualifiedResolvedTopLevelSymbol) {
                    // Identifier not qualified through map; ignore it
                    continue;
                }
                
                if (type == SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD) {
                    usedFunctions.add(name);
                } else if (type == SourceIdentifier.Category.DATA_CONSTRUCTOR) {
                    usedConstructors.add(name);
                } else if (type == SourceIdentifier.Category.TYPE_CONSTRUCTOR) {
                    usedTypes.add(name);
                } else if (type == SourceIdentifier.Category.TYPE_CLASS) {
                    usedClasses.add(name);
                } else {
                    throw new IllegalArgumentException();
                } 
            }
        }
        
        // Now remove unnecessary entries from our maps
        
        Set<String> mapFunctions = qualificationMap.getUnqualifiedNames(SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD);
        mapFunctions.removeAll(usedFunctions);
        for (final String mapFunction : mapFunctions) {
            qualificationMap.removeQualification(mapFunction, SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD);
        }
        
        Set<String> mapClasses = qualificationMap.getUnqualifiedNames(SourceIdentifier.Category.TYPE_CLASS);
        mapClasses.removeAll(usedClasses);
        for (final String mapClass : mapClasses) {
            qualificationMap.removeQualification(mapClass, SourceIdentifier.Category.TYPE_CLASS);
        }
        
        Set<String> mapTypes = qualificationMap.getUnqualifiedNames(SourceIdentifier.Category.TYPE_CONSTRUCTOR);
        mapTypes.removeAll(usedTypes);
        for (final String mapType : mapTypes) {
            qualificationMap.removeQualification(mapType, SourceIdentifier.Category.TYPE_CONSTRUCTOR);
        }
        
        Set<String> mapConstructors = qualificationMap.getUnqualifiedNames(SourceIdentifier.Category.DATA_CONSTRUCTOR);
        mapConstructors.removeAll(usedConstructors);
        for (final String mapConstructor : mapConstructors) {
            qualificationMap.removeQualification(mapConstructor, SourceIdentifier.Category.DATA_CONSTRUCTOR);
        }
    }
    
    /**
     * Type check source code.
     * Creation date: (03/07/2001 10:44:37 AM)
     * @param source the source code to type check
     * @param argNames the names of the arguments in order of their needed appearance on the rhs of the sc definition.
     * @param info the info to use to type check the variables.
     * @param logger the message logger used in type checking
     * @return TypeExpr the resulting type of the source
     */
    private static TypeExpr typeCheckSource(String source, String[] argNames, TypeCheckInfo info, CompilerMessageLogger logger) {
        
        // create a string with all the arguments in natural order
        StringBuilder args = new StringBuilder();
        for (final String argName : argNames) {
            args.append(argName + " ");
        }
        
        // Now determine the type of the output of this function and set the type field
        
        StringBuilder candidateFunction = new StringBuilder ("cdInternal_typeCheckOnly ");
        candidateFunction.append(args);
        candidateFunction.append("= ");
        candidateFunction.append(source);
        candidateFunction.append(";\n");
        
        return info.getTypeChecker().checkFunction(
            new AdjunctSource.FromText(candidateFunction.toString()), info.getModuleName(), logger);
    }
    
    /**
     * Fully qualifies the specified expression which may contain arguments, after 
     * determining information about the identifiers found in code.
     * A message logger may be supplied to gather errors encountered in code parsing.
     * 
     * @param codeText the code to be analysed
     * @param varNamesWhichAreArgs set (String) variable names that should be arguments (assumed nonexistent if null)
     * @param previousQualificationMap mapping from unqualified to qualified names (assumed nonexistent if null)
     * @param logger message logger for compiler results
     * @return CodeAnalyser.QualificationResults; if a parse error occurs, null is returned
     *         and the logger contains the appropriate error 
     */
    public QualificationResults qualifyExpression(
            String codeText,
            Set<String> varNamesWhichAreArgs,
            CodeQualificationMap previousQualificationMap,
            CompilerMessageLogger logger) {
        
        return qualifyExpression(codeText, varNamesWhichAreArgs, previousQualificationMap, logger, false);
    }

    /**
     * Fully qualifies the specified expression which may contain arguments, after 
     * determining information about the identifiers found in code.
     * A message logger may be supplied to gather errors encountered in code parsing.
     * 
     * @param codeText the code to be analysed
     * @param varNamesWhichAreArgs set (String) variable names that should be arguments (assumed nonexistent if null)
     * @param previousQualificationMap mapping from unqualified to qualified names (assumed nonexistent if null)
     * @param logger message logger for compiler results
     * @param qualifyAllSymbols whether to qualify all symbols
     * @return CodeAnalyser.QualificationResults; if a parse error occurs, null is returned
     *         and the logger contains the appropriate error 
     */
    public QualificationResults qualifyExpression(
            String codeText,
            Set<String> varNamesWhichAreArgs,
            CodeQualificationMap previousQualificationMap,
            CompilerMessageLogger logger,
            boolean qualifyAllSymbols) {
        
        if ( (codeText == null) || (typeChecker == null) || (moduleTypeInfo == null)) {
            throw new IllegalArgumentException();
        }
        
        // Get all the identifiers that are used in the code gem.
        
        List<SourceIdentifier> identifierPositions = 
            typeChecker.findIdentifiersInExpression(codeText, moduleTypeInfo.getModuleName(), moduleTypeInfo.getModuleNameResolver(), logger);
        if (identifierPositions == null) {
            return null;
        }
        
        // Retrieve arguments, review qualification map and update code with new qualifications
        ExtractionResults extractions = 
            extractArgumentsAndQualifications(identifierPositions, varNamesWhichAreArgs, previousQualificationMap);
        if (extractions == null) {
            return null;
        }
        
        String qualifiedCode = replaceUnqualifiedSymbols(codeText, extractions.getAnalysedIdentifiers(), qualifyAllSymbols);
        
        // Convert arguments set to array
        
        Set<String> freeVarNames = extractions.getArgumentNames();
        String[] unqualifiedArgumentNames;
        if (freeVarNames != null) {
            unqualifiedArgumentNames = new String[freeVarNames.size()];
            freeVarNames.toArray(unqualifiedArgumentNames);
        } else {
            unqualifiedArgumentNames =  new String[] {};
        }
        
        // Order the analysed identifiers by source position
        
        List<AnalysedIdentifier> sortedAnalysedIdentifiers = extractions.getAnalysedIdentifiers();
        Collections.sort(sortedAnalysedIdentifiers, AnalysedIdentifier.offsetPositionComparator);
        
        return new QualificationResults(unqualifiedArgumentNames, qualifiedCode, extractions.getQualificationMap(), sortedAnalysedIdentifiers);
    }
    
    /**
     * @param sourceIdentifier
     * @return Whether the specified identifier should be analysed as a local variable
     */
    private boolean isLocalVariable(SourceIdentifier sourceIdentifier) {
        return ((sourceIdentifier.getCategory() == SourceIdentifier.Category.LOCAL_VARIABLE) || 
                (sourceIdentifier.getCategory() == SourceIdentifier.Category.LOCAL_VARIABLE_DEFINITION));
    }
    
    /**
     * @param sourceIdentifier
     * @param localDefinitionsMap (SourceIdentifier -> AnalysedIdentifier) mapping of converted local definitions 
     * @param localUndefinedReferencesMap (AnalysedIdentifier -> SourceIdentifier) mapping from converted references to their definition identifiers
     * @return AnalysedIdentifier representing local variable
     */
    private AnalysedIdentifier analyseLocalVariable(SourceIdentifier sourceIdentifier, Map<SourceIdentifier, AnalysedIdentifier> localDefinitionsMap, Map<AnalysedIdentifier, SourceIdentifier> localUndefinedReferencesMap) {       
        
        AnalysedIdentifier analysedIdentifier =
            new AnalysedIdentifier(sourceIdentifier, AnalysedIdentifier.QualificationType.UnqualifiedLocalVariable);
        
        if (sourceIdentifier.getCategory() == SourceIdentifier.Category.LOCAL_VARIABLE_DEFINITION) {
            // Local variable definition. Map the source identifier to the analysed identifier of the definition.
            localDefinitionsMap.put(sourceIdentifier, analysedIdentifier);
            
        } else {
            // Local variable reference which has a definition.
            // Retrieve the analysed identifier for the definition and link if possible
            AnalysedIdentifier definitionIdentifier = localDefinitionsMap.get(sourceIdentifier.getDefinition());
            if (definitionIdentifier != null) {
                analysedIdentifier.setDefinitionIdentifier(definitionIdentifier);
                
            } else {
                // This identifier is a reference without a definition; it will be resolved in the second pass
                localUndefinedReferencesMap.put(analysedIdentifier, sourceIdentifier.getDefinition());
            }
        }
        
        return analysedIdentifier;
        
    }
    
    /**
     * @param sourceIdentifier
     * @return Whether the specified identifier should be analysed as a qualified symbol
     */
    private boolean isQualifiedSymbol(SourceIdentifier sourceIdentifier) {
        return (sourceIdentifier.getResolvedModuleName() != null);
    }
    
    /** 
     * Analyses the specified symbol which is fully qualified in code.
     * The symbol may be a resolved top level symbol or unresolved top level symbol.      
     * 
     * @param sourceIdentifier   
     * @return new analysed identifier
     */
    private AnalysedIdentifier analyseQualifiedSymbol(  SourceIdentifier sourceIdentifier) 
    {   
        String symbolName = sourceIdentifier.getName();
        ModuleName symbolModuleOrNull = sourceIdentifier.getResolvedModuleName();
        SourceIdentifier.Category symbolType = sourceIdentifier.getCategory();
        final boolean isExistingEntity;
        if (symbolModuleOrNull == null) {
            throw new IllegalStateException("expected a qualified source identifier, but it is unqualified");
        } else {
            isExistingEntity = (CodeAnalyser.getVisibleModuleEntity(QualifiedName.make(symbolModuleOrNull, symbolName), symbolType, moduleTypeInfo) != null);
        }
        AnalysedIdentifier.QualificationType qualificationType;       
                       
        if (isExistingEntity) {
            
            // found a valid resolution
            qualificationType = AnalysedIdentifier.QualificationType.QualifiedResolvedTopLevelSymbol;
            
        } else {
            
            // Could not be resolved
            qualificationType = AnalysedIdentifier.QualificationType.QualifiedUnresolvedTopLevelSymbol;
        }
                
        return new AnalysedIdentifier(sourceIdentifier, qualificationType);
    }
    
    /**
     * Indicates whether the specified identifier is an existing argument
     * (that is, it has been specified as an argument, or was previously determined to
     * be one)
     * 
     * @param sourceIdentifier
     * @param qualificationMap 
     * @param varNamesWhichAreArgs
     * @param foundUnqualifiedArgumentNames
     * @return whether the identifier is an existing argument
     */
    private boolean isExistingUnqualifiedArgument(SourceIdentifier sourceIdentifier, CodeQualificationMap qualificationMap, Set<String> varNamesWhichAreArgs, Set<String> foundUnqualifiedArgumentNames) {
        String symbolName = sourceIdentifier.getName();
        SourceIdentifier.Category symbolType = sourceIdentifier.getCategory();
        QualifiedName mapQualifiedName = qualificationMap.getQualifiedName(symbolName, symbolType);
        boolean locallyQualified = ((mapQualifiedName != null) && (mapQualifiedName.getModuleName().equals(moduleTypeInfo.getModuleName())));
        return (symbolType == SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD) &&
               ( foundUnqualifiedArgumentNames.contains(symbolName) || 
                (varNamesWhichAreArgs.contains(symbolName) && ((mapQualifiedName == null) || locallyQualified)));
    }

    /**
     * Analyses the specified identifier which is an unqualified existing argument.
     * The is treated as being an unqualified local argument.
     * 
     * @param sourceIdentifier
     * @param qualificationMap    
     * @param foundArgumentNames ordered set (String) of unqualified names which have been found to be arguments of the expression;     *        
     * @return new analysed identifier
     */
    private AnalysedIdentifier analyseExistingUnqualifiedArgument(
            SourceIdentifier sourceIdentifier, 
            CodeQualificationMap qualificationMap,                                                                    
            Set<String> foundArgumentNames) {
        
        QualifiedName mapQualifiedName = qualificationMap.getQualifiedName(sourceIdentifier.getName(), sourceIdentifier.getCategory());
        boolean locallyQualified = ((mapQualifiedName != null) && (mapQualifiedName.getModuleName().equals(moduleTypeInfo.getModuleName())));
        if ((mapQualifiedName == null) || locallyQualified) {            
            foundArgumentNames.add(sourceIdentifier.getName());
        } 
        
        return new AnalysedIdentifier(sourceIdentifier, AnalysedIdentifier.QualificationType.UnqualifiedArgument);
    }
    
    /**
     * Analyses the specified unqualified symbol and determine if it is a new argument, or 
     * resolvable local or external qualification.
     * 
     * This method attempts to auto-qualify the symbol via the qualification map:
     *  If qualification is possible, the symbol is treated as an unqualified resolvable top level symbol
     *  and a module name is attached.
     *  If the symbol is ambiguous and we do not allow ambiguities, the symbol is 
     *  an unqualified ambiguous top level symbol with no module.
     *  Otherwise the symbol is termed a local argument (if allowed),
     *  or unqualified unresolved top level symbol.
     *  
     * @param sourceIdentifier
     * @param foundArgumentNames ordered set (String) of unqualified names which have been found to be arguments of the expression;
     *        this is only used to maintain an ordered list of found arguments
     * @param qualificationMap
     * @return new analysed identifier
     */
    private AnalysedIdentifier analyseUnqualifiedNewArgumentOrTopLevelSymbol(SourceIdentifier sourceIdentifier, Set<String> foundArgumentNames, CodeQualificationMap qualificationMap) 
    {
        String symbolName = sourceIdentifier.getName();
        SourceIdentifier.Category symbolType = sourceIdentifier.getCategory();
        
        // Try to get qualification through the map
        
        QualifiedName qualifiedName = qualificationMap.getQualifiedName(symbolName, symbolType);
        if (qualifiedName == null) {

            // Identifier not mapped through map. See if it can be found in a module.
            // Note: If the symbol can be resolved in the current module, this module will be first.
            
            List/*ModuleName*/<ModuleName> matchingModuleNames = getModulesContainingIdentifier(symbolName, symbolType, moduleTypeInfo);
            if (!matchingModuleNames.isEmpty()) {
                
                if ((matchingModuleNames.size() > 1) && 
                    !matchingModuleNames.get(0).equals(moduleTypeInfo.getModuleName()) &&
                    !qualifyAmbiguousNames) {
                    // The module list is ambiguous, 
                    // the identifier cannot be mapped to the current module,
                    // and we do not allow ambiguities. 
                    
                    // So mark this as an ambiguity without module.
                    return new AnalysedIdentifier(sourceIdentifier, AnalysedIdentifier.QualificationType.UnqualifiedAmbiguousTopLevelSymbol);
                    
                } else {
                    // The identifier can be mapped to only one module,
                    // the identifier can be found in the current module,
                    // or ambiguities allowed
                    
                    ModuleName newModuleName = matchingModuleNames.get(0);
                    ModuleName minimallyQualifiedModuleName = moduleTypeInfo.getModuleNameResolver().getMinimallyQualifiedModuleName(newModuleName);
                    // So map this to the first module
                    qualificationMap.putQualification(
                            symbolName, 
                            newModuleName,
                            symbolType);
                    AnalysedIdentifier analysedIdentifier = new AnalysedIdentifier(sourceIdentifier, AnalysedIdentifier.QualificationType.UnqualifiedResolvedTopLevelSymbol);
                    analysedIdentifier.setModuleName(newModuleName, minimallyQualifiedModuleName);
                    return analysedIdentifier;
                }
                    
            } else {

                // No module found; if allowed, treat this as a free argument 
                if (allowNewArguments && (symbolType == SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD)) {                    
                    foundArgumentNames.add(symbolName);
                    return new AnalysedIdentifier(sourceIdentifier, AnalysedIdentifier.QualificationType.UnqualifiedArgument);
                    
                } else {
                    return new AnalysedIdentifier(sourceIdentifier, AnalysedIdentifier.QualificationType.UnqualifiedUnresolvedTopLevelSymbol);
                }
            }
            
        } else {
            // Symbol is qualified through the map. See if it is resolvable before marking.
            
            AnalysedIdentifier.QualificationType qualificationType;
            if (CodeAnalyser.getVisibleModuleEntity(qualifiedName, sourceIdentifier.getCategory(), moduleTypeInfo) != null) {
                qualificationType = AnalysedIdentifier.QualificationType.UnqualifiedResolvedTopLevelSymbol;
            } else {
                return null;
            }
            
            AnalysedIdentifier analysedIdentifier = new AnalysedIdentifier(sourceIdentifier, qualificationType);
            ModuleName newModuleName = qualifiedName.getModuleName();
            ModuleName minimallyQualifiedModuleName = moduleTypeInfo.getModuleNameResolver().getMinimallyQualifiedModuleName(newModuleName);
            analysedIdentifier.setModuleName(newModuleName, minimallyQualifiedModuleName);
            return analysedIdentifier;
        }
    }
    
    /**
     * Constructs and returns a SoruceModification that replaces the text at sourceRange of sourceText with newText. 
     * @param sourceText
     * @param sourceRange
     * @param newText
     * @return A SourceModification.ReplaceText
     */
    private static SourceModification makeReplaceText(String sourceText, SourceRange sourceRange, String newText) {
        SourcePosition startPosition = sourceRange.getStartSourcePosition();
        SourcePosition endPosition = sourceRange.getEndSourcePosition();
        int startIndex = startPosition.getPosition(sourceText);
        int endIndex = endPosition.getPosition(sourceText, startPosition, startIndex);
        String oldText = sourceText.substring(startIndex, endIndex);
        
        return new SourceModification.ReplaceText(oldText, newText, startPosition);
    }
}
