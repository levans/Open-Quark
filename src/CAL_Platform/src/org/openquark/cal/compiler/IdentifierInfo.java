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
 * IdentifierInfo.java
 * Created: Sep 5, 2007
 * By: Joseph Wong
 */

package org.openquark.cal.compiler;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.openquark.util.Pair;

/**
 * This is the root class of a hierarchy of classes representing different kinds of identifiers in
 * CAL. Each subclass of this class represents the <i>identity</i> of a particular kind of CAL
 * entity. For example, a top-level function is represented by an
 * {@link org.openquark.cal.compiler.IdentifierInfo.TopLevel.FunctionOrClassMethod IdentifierInfo.TopLevel.FunctionOrClassMethod}.
 * 
 * <p>
 * This is independent of the <i>context</i> in which the actual identifier appears in source: for
 * example, the name of a top-level function in its definition must be unqualified, while a
 * reference to the same function in an expression is <i>qualifiable</i> (can be qualified) - both
 * occurrences would be associated with the same {@link IdentifierInfo}. These two occurrences can
 * be distinguished by their representing {@link IdentifierOccurrence} - the former would be an
 * {@link org.openquark.cal.compiler.IdentifierOccurrence.Binding.Definition IdentifierOccurrence.Binding.Definition},
 * while the latter would be an
 * {@link org.openquark.cal.compiler.IdentifierOccurrence.Reference.Qualifiable IdentifierOccurrence.Reference.Qualifiable}.
 * 
 * <p>
 * At the same time, some concepts which are grammatically similar are distinguished by different subclasses, e.g.
 * a record field name vs a data constructor field name - while both can be either textual or ordinal, the identity of the
 * latter also includes the data constructor(s) associated with the field name.
 * 
 * <p>
 * The hierarchy of classes and what they represent are as follows:
 * 
 * <ul>
 *  <li>a module name
 *      - {@link org.openquark.cal.compiler.IdentifierInfo.Module Module}
 *  <li>a name to a top-level (or scoped) entity
 *      - {@link org.openquark.cal.compiler.IdentifierInfo.TopLevel TopLevel}
 *      <ul>
 *          <li>function/class method
 *              - {@link org.openquark.cal.compiler.IdentifierInfo.TopLevel.FunctionOrClassMethod FunctionOrClassMethod}
 *          <li>type constructor
 *              - {@link org.openquark.cal.compiler.IdentifierInfo.TopLevel.TypeCons TypeCons}
 *          <li>data constructor
 *              - {@link org.openquark.cal.compiler.IdentifierInfo.TopLevel.DataCons DataCons}
 *          <li>type class
 *              - {@link org.openquark.cal.compiler.IdentifierInfo.TopLevel.TypeClass TypeClass}
 *      </ul>
 *  <li>a local variable
 *      - {@link org.openquark.cal.compiler.IdentifierInfo.Local Local}
 *      <ul>
 *          <li>local function
 *              - {@link org.openquark.cal.compiler.IdentifierInfo.Local.Function Function}
 *          <li>pattern variable in a (lazy) local pattern match declaration
 *              - {@link org.openquark.cal.compiler.IdentifierInfo.Local.PatternMatchVariable PatternMatchVariable}
 *          <li>pattern variable in a case expression alternative
 *              - {@link org.openquark.cal.compiler.IdentifierInfo.Local.CasePatternVariable CasePatternVariable}
 *          <li>parameter
 *              - {@link org.openquark.cal.compiler.IdentifierInfo.Local.Parameter Parameter}
 *              <ul>
 *                  <li>...of a top-level function/class method
 *                      - {@link org.openquark.cal.compiler.IdentifierInfo.Local.Parameter.TopLevelFunctionOrClassMethod TopLevelFunctionOrClassMethod}
 *                  <li>...of a local function
 *                      - {@link org.openquark.cal.compiler.IdentifierInfo.Local.Parameter.LocalFunction LocalFunction}
 *                  <li>...of a lambda expression
 *                      - {@link org.openquark.cal.compiler.IdentifierInfo.Local.Parameter.Lambda Lambda}
 *                  <li>...of an instance method (declared in CALDoc)
 *                      - {@link org.openquark.cal.compiler.IdentifierInfo.Local.Parameter.InstanceMethodCALDoc InstanceMethodCALDoc}
 *              </ul>
 *      </ul>
 *  <li>a data constructor field name
 *      - {@link org.openquark.cal.compiler.IdentifierInfo.DataConsFieldName DataConsFieldName}
 *  <li>a record field name
 *      - {@link org.openquark.cal.compiler.IdentifierInfo.RecordFieldName RecordFieldName}
 *  <li>a type variable
 *      - {@link org.openquark.cal.compiler.IdentifierInfo.TypeVariable TypeVariable}
 * </ul>
 * 
 * @see IdentifierOccurrence
 *      IdentifierOccurrence -
 *      this is the closely related class representing an identifier in the context in which it appears
 *      (e.g. a binding vs a reference, qualifiable vs non-qualifiable)
 * 
 * @author Joseph Wong
 * @author Iulian Radu
 */
/*
 * @history
 * 
 * The representation of the different kinds of identifiers via a class hierarchy (as opposed to an enum-based representation)
 * is inspired by the SourceModel, by Bo Ilic.
 * 
 * Many of the different kinds of identifiers have also been represented by the typesafe-enum SourceIdentifier.Category,
 * by Iulian Radu.
 */

/*
 * Note: Eclipse compilation may mistakenly report compile errors related to cycles in the type hierarchy, etc of the classes in this file.
 * You can make a small edit to the IdentifierInfo.TopLevel nested class to make Eclipse recompile without errors.
 */

public abstract class IdentifierInfo {
    
    /**
     * Represents the identity of a module referred to by a module name identifier, namely the module's fully qualified name.
     *
     * @author Joseph Wong
     */
    public static final class Module extends IdentifierInfo {
        
        /**
         * The resolved, fully qualified name of the module referred to by the identifier.
         */
        private final ModuleName resolvedName;

        /**
         * Constructs an instance of this identifier info.
         * @param resolvedName the resolved, fully qualified name of the module referred to by the identifier.
         */
        public Module(final ModuleName resolvedName) {
            if (resolvedName == null) {
                throw new NullPointerException();
            }
            this.resolvedName = resolvedName;
        }

        /**
         * @return the resolved, fully qualified name of the module referred to by the identifier.
         */
        public ModuleName getResolvedName() {
            return resolvedName;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(final Object obj) {
            return obj instanceof Module && ((Module)obj).getResolvedName().equals(getResolvedName());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            return getResolvedName().hashCode();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return toStringOneField("Module", "resolvedName", getResolvedName());
        }
    }
    
    /**
     * Abstract base class for the identity of a top-level (or scoped) entity referred to by an identifier.
     *
     * @author Joseph Wong
     */
    public static abstract class TopLevel extends IdentifierInfo {
        
    	/*
		 * Note: Eclipse compilation may mistakenly report compile errors related to cycles in the type hierarchy, etc of the classes in this file.
		 * You can make a small edit to this nested class to make Eclipse recompile without errors.
    	 */
    	
        /**
         * The resolved, fully qualified name of the entity referred to by the identifier.
         */
        private final QualifiedName resolvedName;
        
        /**
         * Represents the identity of a top-level function or class method referred to by an identifier, namely the
         * function or method's fully qualified name.
         *
         * @author Joseph Wong
         */
        public static final class FunctionOrClassMethod extends TopLevel {
            
            /**
             * Constructs an instance of this identifier info.
             * @param resolvedName the resolved, fully qualified name of the entity referred to by the identifier.
             */
            public FunctionOrClassMethod(final QualifiedName resolvedName) {
                super(resolvedName);
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public String toString() {
                return toStringOneField("TopLevel.FunctionOrClassMethod", "resolvedName", getResolvedName());
            }
        }
        
        /**
         * Represents the identity of a type constructor referred to by an identifier, namely the
         * type constructor's fully qualified name.
         *
         * @author Joseph Wong
         */
        public static final class TypeCons extends TopLevel {
            
            /**
             * Constructs an instance of this identifier info.
             * @param resolvedName the resolved, fully qualified name of the entity referred to by the identifier.
             */
            public TypeCons(final QualifiedName resolvedName) {
                super(resolvedName);
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public String toString() {
                return toStringOneField("TopLevel.TypeCons", "resolvedName", getResolvedName());
            }
        }
        
        /**
         * Represents the identity of a data constructor referred to by an identifier, namely the
         * data constructor's fully qualified name.
         *
         * @author Joseph Wong
         */
        public static final class DataCons extends TopLevel {
            
            /**
             * Constructs an instance of this identifier info.
             * @param resolvedName the resolved, fully qualified name of the entity referred to by the identifier.
             */
            public DataCons(final QualifiedName resolvedName) {
                super(resolvedName);
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public String toString() {
                return toStringOneField("TopLevel.DataCons", "resolvedName", getResolvedName());
            }
        }
        
        /**
         * Represents the identity of a type class referred to by an identifier, namely the
         * type class's fully qualified name.
         *
         * @author Joseph Wong
         */
        public static final class TypeClass extends TopLevel {
            
            /**
             * Constructs an instance of this identifier info.
             * @param resolvedName the resolved, fully qualified name of the entity referred to by the identifier.
             */
            public TypeClass(final QualifiedName resolvedName) {
                super(resolvedName);
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public String toString() {
                return toStringOneField("TopLevel.TypeClass", "resolvedName", getResolvedName());
            }
        }
        
        /**
         * Private constructor. Only meant to be called by inner subclasses.
         * @param resolvedName the resolved, fully qualified name of the entity referred to by the identifier.
         */
        private TopLevel(final QualifiedName resolvedName) {
            if (resolvedName == null) {
                throw new NullPointerException();
            }
            this.resolvedName = resolvedName;
        }
        
        /**
         * @return the resolved, fully qualified name of the entity referred to by the identifier.
         */
        public QualifiedName getResolvedName() {
            return resolvedName;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public final boolean equals(final Object obj) {
            return obj != null && obj.getClass().equals(this.getClass()) && ((TopLevel)obj).getResolvedName().equals(getResolvedName());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public final int hashCode() {
            return getResolvedName().hashCode();
        }
    }
    
    /**
     * Abstract base class for the identity of a locally-defined entity referred to by an identifier.
     *
     * @author Joseph Wong
     */
    public static abstract class Local extends IdentifierInfo {
        
        /**
         * The name of the local variable, as appearing in source.
         */
        private final String varName;
        
        /**
         * An enum representing the different kinds of pattern variables. A pattern variable's kind
         * is determined by the context in which it is defined.
         *
         * @author Joseph Wong
         */
        public static enum PatternVariableKind {
            /**
             * A regular, non-punned pattern variable (e.g. bar in {foo=bar})
             */
            regular,
            /**
             * A punned data constructor field pattern (e.g. bar in DC {bar})
             */
            punnedDataConsField,
            /**
             * A punned record field pattern (e.g. bar in {r|bar})
             */
            punnedRecordField
        }
        
        /**
         * Represents the identity of a case-bound pattern variable referred to by an identifier.
         *
         * @author Joseph Wong
         */
        public static final class CasePatternVariable extends Local {
            
            /**
             * The unique ID for the pattern variable. 
             */
            // todo-jowong this is currently not assigned by the canonical LocalFunctionIdentifierGenerator that
            // generates unique IDs for let-bound functions and local pattern match variables.
            private final LocalFunctionIdentifier casePatternVariableIdentifier;
            
            /**
             * The kind of pattern variable represented.
             */
            private final PatternVariableKind kind;

            /**
             * Constructs an instance of this identifier info.
             * @param varName the name of the pattern variable, as appearing in source.
             * @param casePatternVariableIdentifier the unique ID for the pattern variable.
             * @param kind the kind of pattern variable represented.
             */
            public CasePatternVariable(final String varName, final LocalFunctionIdentifier casePatternVariableIdentifier, final PatternVariableKind kind) {
                super(varName);
                if (casePatternVariableIdentifier == null || kind == null) {
                    throw new NullPointerException();
                }
                this.casePatternVariableIdentifier = casePatternVariableIdentifier;
                this.kind = kind;
            }
            
            /**
             * @return the unique ID for the pattern variable. 
             */
            public LocalFunctionIdentifier getCasePatternVariableIdentifier() {
                return casePatternVariableIdentifier;
            }

            /**
             * @return the kind of pattern variable represented.
             */
            public PatternVariableKind getKind() {
                return kind;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public boolean equals(final Object obj) {
                if (obj instanceof CasePatternVariable) {
                    final CasePatternVariable other = (CasePatternVariable)obj;
                    return other.getVarName().equals(getVarName()) && other.getCasePatternVariableIdentifier().equals(getCasePatternVariableIdentifier()) && other.getKind().equals(getKind());
                } else {
                    return false;
                }
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public int hashCode() {
                return hashThreeFields(getVarName(), getCasePatternVariableIdentifier(), getKind());
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public String toString() {
                return toStringThreeFields("Local.CasePatternVariable",
                    "varName", getVarName(),
                    "kind", getKind(),
                    "casePatternVariableIdentifier", getCasePatternVariableIdentifier());
            }
        }
        
        /**
         * Represents the identity of a let-bound local function referred to by an identifier.
         *
         * @author Joseph Wong
         */
        public static final class Function extends Local {
            
            /**
             * The canonical unique ID for the local function.
             */
            private final LocalFunctionIdentifier localFunctionIdentifier;
            
            /**
             * Constructs an instance of this identifier info.
             * @param varName the name of the local function, as appearing in source.
             * @param localFunctionIdentifier the canonical unique ID for the local function.
             */
            public Function(final String varName, final LocalFunctionIdentifier localFunctionIdentifier) {
                super(varName);
                if (localFunctionIdentifier == null) {
                    throw new NullPointerException();
                }
                this.localFunctionIdentifier = localFunctionIdentifier;
            }

            /**
             * @return the canonical unique ID for the local function.
             */
            public LocalFunctionIdentifier getLocalFunctionIdentifier() {
                return localFunctionIdentifier;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public boolean equals(final Object obj) {
                if (obj instanceof Function) {
                    final Function other = (Function)obj;
                    return other.getVarName().equals(getVarName()) && other.getLocalFunctionIdentifier().equals(getLocalFunctionIdentifier());
                } else {
                    return false;
                }
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public int hashCode() {
                return hashTwoFields(getVarName(), getLocalFunctionIdentifier());
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public String toString() {
                return toStringTwoFields("Local.Function",
                    "varName", getVarName(),
                    "localFunctionIdentifier", getLocalFunctionIdentifier());
            }
        }
        
        /**
         * Represents the identity of a let-bound pattern variable appearing in a (lazy) local pattern match declaration.
         *
         * @author Joseph Wong
         */
        public static final class PatternMatchVariable extends Local {
            
            /**
             * The kind of pattern variable represented.
             */
            private final PatternVariableKind kind;
            
            /**
             * The canonical unique ID for the local function.
             */
            private final LocalFunctionIdentifier localFunctionIdentifier;

            /**
             * Constructs an instance of this identifier info.
             * @param varName the name of the pattern variable, as appearing in source.
             * @param kind the kind of pattern variable represented.
             * @param localFunctionIdentifier the canonical unique ID for the local function.
             */
            public PatternMatchVariable(final String varName, final PatternVariableKind kind, final LocalFunctionIdentifier localFunctionIdentifier) {
                super(varName);
                if (kind == null || localFunctionIdentifier == null) {
                    throw new NullPointerException();
                }
                this.kind = kind;
                this.localFunctionIdentifier = localFunctionIdentifier;
            }

            /**
             * @return the kind of pattern variable represented.
             */
            public PatternVariableKind getKind() {
                return kind;
            }

            /**
             * @return the canonical unique ID for the local function.
             */
            public LocalFunctionIdentifier getLocalFunctionIdentifier() {
                return localFunctionIdentifier;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public boolean equals(final Object obj) {
                if (obj instanceof PatternMatchVariable) {
                    final PatternMatchVariable other = (PatternMatchVariable)obj;
                    return other.getVarName().equals(getVarName()) && other.getKind().equals(getKind()) && other.getLocalFunctionIdentifier().equals(getLocalFunctionIdentifier());
                } else {
                    return false;
                }
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public int hashCode() {
                return hashThreeFields(getVarName(), getKind(), getLocalFunctionIdentifier());
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public String toString() {
                return toStringThreeFields("Local.PatternMatchVariable",
                    "varName", getVarName(),
                    "kind", getKind(),
                    "localFunctionIdentifier", getLocalFunctionIdentifier());
            }
        }
        
        /**
         * Abstract base class for the identity of a parameter, which can be defined as part of a function's parameter list,
         * or in a CALDoc arg tag.
         *
         * @author Joseph Wong
         */
        public static abstract class Parameter extends Local {
            
            /**
             * Represents the identity of a lambda-bound parameter.
             *
             * @author Joseph Wong
             */
            public static final class Lambda extends Parameter {
                
                /**
                 * The unique ID for the parameter.
                 */
                // todo-jowong this is currently not assigned by the canonical LocalFunctionIdentifierGenerator that
                // generates unique IDs for let-bound functions and local pattern match variables.
                private final LocalFunctionIdentifier lambdaParameterIdentifier;

                /**
                 * Constructs an instance of this identifier info.
                 * @param varName the name of the lambda parameter, as appearing in source.
                 * @param lambdaParameterIdentifier the unique ID for the parameter.
                 */
                public Lambda(final String varName, final LocalFunctionIdentifier lambdaParameterIdentifier) {
                    super(varName);
                    if (lambdaParameterIdentifier == null) {
                        throw new NullPointerException();
                    }
                    this.lambdaParameterIdentifier = lambdaParameterIdentifier;
                }
                
                /**
                 * @return the unique ID for parameter.
                 */
                public LocalFunctionIdentifier getLambdaParameterIdentifier() {
                    return lambdaParameterIdentifier;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public boolean equals(final Object obj) {
                    if (obj instanceof Lambda) {
                        final Lambda other = (Lambda)obj;
                        return other.getVarName().equals(getVarName()) && other.getLambdaParameterIdentifier().equals(getLambdaParameterIdentifier());
                    } else {
                        return false;
                    }
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public int hashCode() {
                    return hashTwoFields(getVarName(), getLambdaParameterIdentifier());
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public String toString() {
                    return toStringTwoFields("Local.Parameter.Lambda",
                        "varName", getVarName(),
                        "lambdaParameterIdentifier", getLambdaParameterIdentifier());
                }
            }
            
            /**
             * Represents the identity of a parameter of an instance method as declared in the associated CALDoc comment.
             *
             * @author Joseph Wong
             */
            // todo-jowong the identity should really contain the qualified class method name, the qualified type name, and the defining module name
            public static final class InstanceMethodCALDoc extends Parameter {

                /**
                 * Constructs an instance of this identifier info.
                 * @param varName the name of the parameter, as appearing in source.
                 */
                public InstanceMethodCALDoc(final String varName) {
                    super(varName);
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public boolean equals(final Object obj) {
                    return obj instanceof InstanceMethodCALDoc && ((InstanceMethodCALDoc)obj).getVarName().equals(getVarName());
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public int hashCode() {
                    return getVarName().hashCode();
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public String toString() {
                    return toStringOneField("Local.Parameter.InstanceMethodCALDoc", "varName", getVarName());
                }
            }
            
            /**
             * Represents the identity of a parameter of a let-bound local function, which can be
             * defined as part of the function's parameter list, or in the associated CALDoc comment.
             * 
             * @author Joseph Wong
             */
            public static final class LocalFunction extends Parameter {
                
                /**
                 * The identity of the associated local function.
                 */
                public final Local.Function associatedFunction;

                /**
                 * Constructs an instance of this identifier info.
                 * @param varName the name of the local function parameter, as appearing in source.
                 * @param associatedFunction the identity of the associated local function.
                 */
                public LocalFunction(final String varName, final Local.Function associatedFunction) {
                    super(varName);
                    if (associatedFunction == null) {
                        throw new NullPointerException();
                    }
                    this.associatedFunction = associatedFunction;
                }

                /**
                 * @return the identity of the associated local function.
                 */
                public Local.Function getAssociatedFunction() {
                    return associatedFunction;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public boolean equals(final Object obj) {
                    if (obj instanceof LocalFunction) {
                        final LocalFunction other = (LocalFunction)obj;
                        return other.getAssociatedFunction().equals(getAssociatedFunction()) && other.getVarName().equals(getVarName());
                    } else {
                        return false;
                    }
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public int hashCode() {
                    return hashTwoFields(getAssociatedFunction(), getVarName());
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public String toString() {
                    return toStringTwoFields("Local.Parameter.LocalFunction",
                        "varName", getVarName(),
                        "associatedFunction", getAssociatedFunction());
                }
            }
            
            /**
             * Represents the identity of a parameter of a top-level function or class method, which can be
             * defined as part of the function's parameter list, or in the associated CALDoc comment.
             *
             * @author Joseph Wong
             */
            public static final class TopLevelFunctionOrClassMethod extends Parameter {
                
                /**
                 * The identity of the associated function/method.
                 */
                public final TopLevel.FunctionOrClassMethod associatedFunction;
                
                /**
                 * Constructs an instance of this identifier info.
                 * @param varName the name of the function/method parameter, as appearing in source.
                 * @param associatedFunction the identity of the associated function/method.
                 */
                public TopLevelFunctionOrClassMethod(final String varName, final TopLevel.FunctionOrClassMethod associatedFunction) {
                    super(varName);
                    if (associatedFunction == null) {
                        throw new NullPointerException();
                    }
                    this.associatedFunction = associatedFunction;
                }

                /**
                 * @return the identity of the associated function/method.
                 */
                public TopLevel.FunctionOrClassMethod getAssociatedFunction() {
                    return associatedFunction;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public boolean equals(final Object obj) {
                    if (obj instanceof TopLevelFunctionOrClassMethod) {
                        final TopLevelFunctionOrClassMethod other = (TopLevelFunctionOrClassMethod)obj;
                        return other.getAssociatedFunction().equals(getAssociatedFunction()) && other.getVarName().equals(getVarName());
                    } else {
                        return false;
                    }
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public int hashCode() {
                    return hashTwoFields(getAssociatedFunction(), getVarName());
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public String toString() {
                    return toStringTwoFields("Local.Parameter.TopLevelFunctionOrClassMethod",
                        "varName", getVarName(),
                        "associatedFunction", getAssociatedFunction());
                }
            }
            
            /**
             * Private constructor. Only meant to be called by inner subclasses.
             * @param varName the name of the parameter, as appearing in source.
             */
            private Parameter(final String varName) {
                super(varName);
            }
        }
        
        /**
         * Private constructor. Only meant to be called by inner subclasses.
         * @param varName the name of the local variable, as appearing in source.
         */
        private Local(final String varName) {
            if (varName == null) {
                throw new NullPointerException();
            }
            this.varName = varName;
        }

        /**
         * @return the name of the local variable, as appearing in source.
         */
        public String getVarName() {
            return varName;
        }
    }
    
    /**
     * Represents the identity of one or more data constructor fields referred to by an identifier,
     * which consists of the (common) field name and the identities of the associated data constructors.
     * 
     * <p>
     * A data constructor field name can be associated with multiple data constructors when it appears
     * in a case alternative containing more than one data constructor, e.g.
     * <pre>
     * case foo of
     * (DC1|DC2|DC3) {field1=bar, field2} -> ...
     * </pre>
     * Both field1 and field2 are associated with DC1, DC2, and DC3.
     *
     * <p>
     * This class encapsulates <i>one or more</i> associated data constructors. This is part of the design
     * of the IdentifierOccurrence class, which is in a strict 1-to-1 relationship with an IdentifierInfo.
     * 
     * <p>
     * Thus, to check whether two data constructor field names may refer to the same field (or fields), one should
     * check whether the set of data constructors overlap (and whether the field names match), rather than rely
     * on the strict equality semantics implemented in equals().
     *
     * @author Joseph Wong
     */
    public static final class DataConsFieldName extends IdentifierInfo {
        
        /**
         * The name of the field.
         */
        private final FieldName fieldName;
        
        /**
         * The set of identities of the associated data constructors. This is a set because order does not matter
         * for the identity of this data constructor field name.
         */
        private final Set<TopLevel.DataCons> associatedDataConstructors;

        /**
         * Constructs an instance of this identifier info.
         * @param fieldName the name of the field.
         * @param associatedDataConstructors the set of identities of the associated data constructors.
         */
        public DataConsFieldName(final FieldName fieldName, final Collection<TopLevel.DataCons> associatedDataConstructors) {
            if (fieldName == null || associatedDataConstructors == null) {
                throw new NullPointerException();
            }
            this.fieldName = fieldName;
            this.associatedDataConstructors = Collections.unmodifiableSet(new LinkedHashSet<TopLevel.DataCons>(associatedDataConstructors));
            if (this.associatedDataConstructors.isEmpty()) {
                throw new IllegalArgumentException("there must be at least one associated data constructor");
            }
        }

        /**
         * @return the name of the field.
         */
        public FieldName getFieldName() {
            return fieldName;
        }

        /**
         * @return the set of identities of the associated data constructors.
         */
        public Set<TopLevel.DataCons> getAssociatedDataConstructors() {
            return associatedDataConstructors;
        }
        
        /**
         * @return the identity of the first associated data constructor. The ordering of data constructors follows from the
         * ordering of the collection specified on construction of this instance. 
         */
        public TopLevel.DataCons getFirstAssociatedDataConstructor() {
            return associatedDataConstructors.iterator().next();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof DataConsFieldName) {
                final DataConsFieldName other = (DataConsFieldName)obj;
                return other.getFieldName().getCalSourceForm().equals(getFieldName().getCalSourceForm()) && other.getAssociatedDataConstructors().equals(getAssociatedDataConstructors());
            } else {
                return false;
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            return hashTwoFields(getFieldName().getCalSourceForm(), getAssociatedDataConstructors());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return toStringTwoFields("DataConsFieldName",
                "fieldName", getFieldName().getCalSourceForm(),
                "associatedDataConstructors", getAssociatedDataConstructors());
        }
    }
    
    /**
     * Represents the identity of a record field referred to by an identifier. This is distinguished from
     * a data constructor field name, which is associated with one or more data constructors.
     *
     * @author Joseph Wong
     */
    public static final class RecordFieldName extends IdentifierInfo {
        
        /**
         * The name of the field.
         */
        private final FieldName fieldName;

        /**
         * Constructs an instance of this identifier info.
         * @param fieldName the name of the field.
         */
        public RecordFieldName(final FieldName fieldName) {
            if (fieldName == null) {
                throw new NullPointerException();
            }
            this.fieldName = fieldName;
        }

        /**
         * @return the name of the field.
         */
        public FieldName getFieldName() {
            return fieldName;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(final Object obj) {
            return obj instanceof RecordFieldName && ((RecordFieldName)obj).getFieldName().getCalSourceForm().equals(getFieldName().getCalSourceForm());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            return getFieldName().getCalSourceForm().hashCode();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return toStringOneField("RecordFieldName", "fieldName", getFieldName().getCalSourceForm());
        }
}
    
    /**
     * Represents the identity of a type variable referred to by an identifier, namely the variable name and
     * the variable's unique ID.
     *
     * @author Joseph Wong
     */
    /*
     * @discussion
     * 
     * A source-position-based unique ID scheme is currently implemented for type variables. This means that
     * a type variable's identity is globally unique, but is invalidated by source changes. However, this also means that
     * the unique ID is invariant under different source tree traversal schemes.
     * 
     * An alternative is to assign a counter-based ID to the type variables, but that means the generated IDs are sensitive
     * to the order of traversal. Thus to get consistent IDs for all type variables, the all type variables in
     * a module definition needs to be traversed completely every time. 
     */
    public static final class TypeVariable extends IdentifierInfo {
        
        /**
         * The name of the type variable.
         */
        private final String typeVarName;
        
        /**
         * The unique ID for the type variable.
         */
        private final Pair<ModuleName, SourcePosition> typeVarUniqueIdentifier;

        /**
         * Constructs an instance of this identifier info.
         * @param typeVarName the name of the type variable.
         * @param typeVarUniqueIdentifier the unique ID for the type variable.
         */
        public TypeVariable(final String typeVarName, final Pair<ModuleName, SourcePosition> typeVarUniqueIdentifier) {
            if (typeVarName == null) {
                throw new NullPointerException();
            }
            this.typeVarName = typeVarName;
            this.typeVarUniqueIdentifier = typeVarUniqueIdentifier;
        }

        /**
         * @return the name of the type variable.
         */
        public String getTypeVarName() {
            return typeVarName;
        }
        
        /**
         * @return the unique ID for the type variable.
         */
        public Pair<ModuleName, SourcePosition> getTypeVarUniqueIdentifier() {
            return typeVarUniqueIdentifier;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof TypeVariable) {
                final TypeVariable other = (TypeVariable)obj;
                return other.getTypeVarName().equals(getTypeVarName()) && other.getTypeVarUniqueIdentifier().equals(getTypeVarUniqueIdentifier());
            } else {
                return false;
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            return hashTwoFields(getTypeVarName(), getTypeVarUniqueIdentifier());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return toStringTwoFields("TypeVariable",
                "typeVarName", getTypeVarName(),
                "typeVarUniqueIdentifier", getTypeVarUniqueIdentifier());
        }
    }
    
    /** Private constructor. Only meant to be called by inner subclasses. */
    private IdentifierInfo() {}
    
    /**
     * Helper method to hash an object with two fields.
     * @param field1 the first field.
     * @param field2 the second field.
     * @return a hash value.
     */
    private static int hashTwoFields(final Object field1, final Object field2) {
        return (37 * (37 * 17 + field1.hashCode())) + field2.hashCode();
    }
    
    /**
     * Helper method to hash an object with three fields.
     * @param field1 the first field.
     * @param field2 the second field.
     * @param field3 the third field.
     * @return a hash value.
     */
    private static int hashThreeFields(final Object field1, final Object field2, final Object field3) {
        int result = 17;
        result = (result * 37) + field1.hashCode();
        result = (result * 37) + field2.hashCode();
        result = (result * 37) + field3.hashCode();
        return result;
    }
    
    /**
     * Helper method to produce a string representation of an object with one field.
     * @param className the short name of the class (after "IdentifierInfo.")
     * @param label1 the first field's name.
     * @param field1 the first field.
     * @return the string representation.
     */
    private static String toStringOneField(final String className, final String label1, final Object field1) {
        return "[IdentifierInfo." + className + " " + label1 + "=" + field1 + "]";
    }
    
    /**
     * Helper method to produce a string representation of an object with two field.
     * @param className the short name of the class (after "IdentifierInfo.")
     * @param label1 the first field's name.
     * @param field1 the first field.
     * @param label2 the second field's name.
     * @param field2 the second field.
     * @return the string representation.
     */
    private static String toStringTwoFields(final String className, final String label1, final Object field1, final String label2, final Object field2) {
        return "[IdentifierInfo." + className + " " + label1 + "=" + field1 + " " + label2 + "=" + field2 + "]";
    }
    
    /**
     * Helper method to produce a string representation of an object with three field.
     * @param className the short name of the class (after "IdentifierInfo.")
     * @param label1 the first field's name.
     * @param field1 the first field.
     * @param label2 the second field's name.
     * @param field2 the second field.
     * @param label3 the third field's name.
     * @param field3 the third field.
     * @return the string representation.
     */
    private static String toStringThreeFields(final String className, final String label1, final Object field1, final String label2, final Object field2, final String label3, final Object field3) {
        return "[IdentifierInfo." + className + " " + label1 + "=" + field1 + " " + label2 + "=" + field2 + " " + label3 + "=" + field3 + "]";
    }
    
    /// All subclasses must implement equals, hashCode, and toString
    
    /**
     * {@inheritDoc}
     */
    @Override
    public abstract boolean equals(Object obj);
    
    /**
     * {@inheritDoc}
     */
    @Override
    public abstract int hashCode();
    
    /**
     * {@inheritDoc}
     */
    @Override
    public abstract String toString();
}
