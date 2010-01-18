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
 * IdentifierOccurrence.java
 * Created: Sep 11, 2007
 * By: Joseph Wong
 */

package org.openquark.cal.compiler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.openquark.cal.compiler.SourceModel.Name;
import org.openquark.cal.compiler.SourceModel.SourceElement;

/**
 * This is the root class of a hierarchy of classes representing different kinds of identifier
 * occurrences in CAL. Each subclass of this class represents the <i>identity</i> of the CAL entity
 * referred to by the identifier, as well as, the <i>context</i> in which the identifier appears in
 * source. (The entity identity is encapsulated by an instance of {@link IdentifierInfo}.) Also,
 * each identifier occurrence may be associated with a
 * {@link org.openquark.cal.compiler.SourceModel.SourceElement SourceElement} and a {@link SourceRange},
 * if the occurrence corresponds to an identifier appearing in source. 
 * 
 * <p>
 * For example, the name of a top-level function in its definition must be unqualified, while a
 * reference to the same function in an expression is <i>qualifiable</i> (can be qualified) - both
 * occurrences would be associated with the same {@link IdentifierInfo}. These two occurrences can
 * be distinguished by their representing {@link IdentifierOccurrence} - the former would be an
 * {@link org.openquark.cal.compiler.IdentifierOccurrence.Binding.Definition IdentifierOccurrence.Binding.Definition},
 * while the latter would be an
 * {@link org.openquark.cal.compiler.IdentifierOccurrence.Reference.Qualifiable IdentifierOccurrence.Reference.Qualifiable}.
 * 
 * <p>
 * At the same time, some concepts which are grammatically similar are distinguished by different subclasses, e.g.
 * a punned ordinal data constructor field name vs a punned textual data constructor field name - the former is a simple
 * reference to a data constructor field, while the latter is also a binding of a local variable.
 * 
 * <p>
 * The hierarchy of classes and what they represent are as follows:
 * 
 * <ul>
 *  <li>a name binding (which binds a name to an entity in a particular scope)
 *      - {@link org.openquark.cal.compiler.IdentifierOccurrence.Binding}
 *      <ul>
 *          <li>a definition (the actual definition in source of the entity to which the name is bound)
 *              - {@link org.openquark.cal.compiler.IdentifierOccurrence.Binding.Definition Definition}
 *          <li>a name listed in an {@code import using} clause
 *              - {@link org.openquark.cal.compiler.IdentifierOccurrence.Binding.ImportUsingItem ImportUsingItem}
 *          <li>an external name binding (one that does not appear in the source code being analyzed, e.g. one that
 *              is implied by the information in a {@link ModuleTypeInfo} or a {@link CodeQualificationMap})
 *              - {@link org.openquark.cal.compiler.IdentifierOccurrence.Binding.External External}
 *          <li>a data constructor field name appearing in the definition of its associated data constructor
 *              - {@link org.openquark.cal.compiler.IdentifierOccurrence.Binding.DataConsFieldName DataConsFieldName}
 *          <li>a punned textual data constructor field name (which is a local variable binding)
 *              - {@link org.openquark.cal.compiler.IdentifierOccurrence.Binding.PunnedTextualDataConsFieldName PunnedTextualDataConsFieldName}
 *          <li>a punned textual record field name (which is a local variable binding)
 *              - {@link org.openquark.cal.compiler.IdentifierOccurrence.Binding.PunnedTextualRecordFieldName PunnedTextualRecordFieldName}
 *      </ul>
 *  <li>a reference (to an entity in scope)
 *      - {@link org.openquark.cal.compiler.IdentifierOccurrence.Reference Reference}
 *      <ul>
 *          <li>a reference to a module (whether the name can be partially qualified, or must be fully qualified)
 *              - {@link org.openquark.cal.compiler.IdentifierOccurrence.Reference.Module Module}
 *          <li>a reference to a top-level (scoped) entity in a context where qualification is allowed or required
 *              - {@link org.openquark.cal.compiler.IdentifierOccurrence.Reference.Qualifiable Qualifiable}
 *          <li>a reference to a top-level (scoped) entity in a context where qualification is <i>not</i> allowed
 *              - {@link org.openquark.cal.compiler.IdentifierOccurrence.Reference.NonQualifiable NonQualifiable}
 *          <li>a reference to a locally-bound entity
 *              - {@link org.openquark.cal.compiler.IdentifierOccurrence.Reference.Local Local}
 *          <li>a reference to a data constructor field (including occurrences of all associated data constructors)
 *              - {@link org.openquark.cal.compiler.IdentifierOccurrence.Reference.DataConsFieldName DataConsFieldName}
 *              <ul>
 *                  <li>...a field name not appearing in a punned context
 *                      - {@link org.openquark.cal.compiler.IdentifierOccurrence.Reference.DataConsFieldName.NonPunned NonPunned}
 *                  <li>...a punned ordinal field name
 *                      - {@link org.openquark.cal.compiler.IdentifierOccurrence.Reference.DataConsFieldName.PunnedOrdinal PunnedOrdinal}
 *              </ul>
 *          <li>a reference to a record field
 *              - {@link org.openquark.cal.compiler.IdentifierOccurrence.Reference.RecordFieldName RecordFieldName}
 *              <ul>
 *                  <li>...a field name not appearing in a punned context
 *                      - {@link org.openquark.cal.compiler.IdentifierOccurrence.Reference.RecordFieldName.NonPunned NonPunned}
 *                  <li>...a punned ordinal field name
 *                      - {@link org.openquark.cal.compiler.IdentifierOccurrence.Reference.RecordFieldName.PunnedOrdinal PunnedOrdinal}
 *              </ul>
 *          <li>an operator reference (to a function, class method, type constructor, or data constructor)
 *              - {@link org.openquark.cal.compiler.IdentifierOccurrence.Reference.Operator Operator}
 *          <li>a reference to a type variable
 *              - {@link org.openquark.cal.compiler.IdentifierOccurrence.Reference.TypeVariable TypeVariable}
 *      </ul>
 *  <li>a foreign descriptor (or external name) for a foreign entity
 *      - {@link org.openquark.cal.compiler.IdentifierOccurrence.ForeignDescriptor ForeignDescriptor}
 * </ul>
 * 
 * @see IdentifierInfo
 *      IdentifierInfo -
 *      this is the closely related class representing the identity of the entity being referred to by an occurrence,
 *      independent of the context in which it appears
 * 
 * @author Joseph Wong
 * @author Iulian Radu
 * @author Peter Cardwell
 */
/*
 * @history
 * 
 * The representation of the different kinds of occurrences via a class hierarchy (as opposed to an enum-based representation)
 * is inspired by the SourceModel, by Bo Ilic.
 * 
 * Some of the kinds of occurrences (in particular: module names and qualifiable scoped entity names) have also been
 * represented by the SourceIdentifier class, by Iulian Radu and Peter Cardwell; and by the AnalysedIdentifier class,
 * by Iulian Radu.
 */
/*
 * @discussion
 * 
 * One of the design choices in the creation of IdentifierOccurrence is to make it mostly orthogonal to the underlying identity
 * of the entity being referred to, which is represented by IdentifierInfo - hence there are two class hierarchies rather
 * than one intertwined hierarchy. At the same time, certain kinds of occurrences only make sense for certain kinds of names
 * (e.g. a qualifiable reference can only refer to top-level (scoped) entities), thus classes in the IdentifierOccurrence
 * hierarchy are parameterized by the kinds of IdentifierInfo allowable.
 */
// todo-jowong revisit the decision to parameterize on the kind of identifier in an occurrence
public abstract class IdentifierOccurrence<I extends IdentifierInfo> {
    
    /**
     * Abstract base class for an occurrence which is a name binding (binding a name to an entity in a particular scope).
     *
     * @author Joseph Wong
     */
    public static abstract class Binding<I extends IdentifierInfo> extends IdentifierOccurrence<I> {
        
        /**
         * Represents a definition (the actual definition in source of the entity to which the name is bound).
         *
         * @author Joseph Wong
         * @author Iulian Radu
         * @author Peter Cardwell
         */
        public static final class Definition<I extends IdentifierInfo> extends Binding<I> {
            
            /**
             * Constructs an instance of this identifier occurrence.
             * @param identifierInfo the identity of the CAL entity referred to by the identifier. Can *not* be null.
             * @param sourceElement the source element associated with the occurrence. This may be more than just a name,
             *          but can be the entire definition of an entity. Can be null.
             * @param sourceRange the source range associated with the actual name occurring in source. Can be null.
             */
            private Definition(final I identifierInfo, final SourceElement sourceElement, final SourceRange sourceRange) {
                super(identifierInfo, sourceElement, sourceRange);
            }
            
            /**
             * Factory method for constructing an instance of this identifier occurrence.
             * @param identifierInfo the identity of the CAL entity referred to by the identifier. Can *not* be null.
             * @param sourceElement the source element associated with the occurrence. This may be more than just a name,
             *          but can be the entire definition of an entity. Can be null.
             * @param sourceRange the source range associated with the actual name occurring in source. Can be null.
             * @return an instance of this class.
             */
            static <I extends IdentifierInfo> Definition<I> make(final I identifierInfo, final SourceElement sourceElement, final SourceRange sourceRange) {
                return new Definition<I>(identifierInfo, sourceElement, sourceRange);
            }
        }
        
        /**
         * Represents a name listed in an {@code import using} clause.
         *
         * @author Joseph Wong
         */
        public static final class ImportUsingItem<I extends IdentifierInfo.TopLevel> extends Binding<I> {
            
            /**
             * The index into the array of names in the {@code import using} clause.
             */
            // todo-jowong this won't be needed when the source model is refactored to represent unqualified names.
            private final int itemIndex;
            
            /**
             * Constructs an instance of this identifier occurrence.
             * @param identifierInfo the identity of the CAL entity referred to by the identifier. Can *not* be null.
             * @param sourceElement the source element associated with the occurrence. This may be more than just a name,
             *          but can be the entire definition of an entity. Can be null.
             * @param itemIndex the index into the array of names in the {@code import using} clause.
             * @param sourceRange the source range associated with the actual name occurring in source. Can be null.
             */
            private ImportUsingItem(final I identifierInfo, final SourceModel.Import.UsingItem sourceElement, final int itemIndex, final SourceRange sourceRange) {
                super(identifierInfo, sourceElement, sourceRange);
                this.itemIndex = itemIndex;
            }
            
            /**
             * Factory method for constructing an instance of this identifier occurrence.
             * @param identifierInfo the identity of the CAL entity referred to by the identifier. Can *not* be null.
             * @param sourceElement the source element associated with the occurrence. This may be more than just a name,
             *          but can be the entire definition of an entity. Can be null.
             * @param itemIndex the index into the array of names in the {@code import using} clause.
             * @param sourceRange the source range associated with the actual name occurring in source. Can be null.
             * @return an instance of this class.
             */
            static <I extends IdentifierInfo.TopLevel> ImportUsingItem<I> make(final I identifierInfo, final SourceModel.Import.UsingItem sourceElement, final int itemIndex, final SourceRange sourceRange) {
                return new ImportUsingItem<I>(identifierInfo, sourceElement, itemIndex, sourceRange);
            }
            
            /**
             * @return the index into the array of names in the {@code import using} clause.
             */
            public int getItemIndex() {
                return itemIndex;
            }
            
            /**
             * {@inheritDoc}
             */
            @Override
            public String toString() {
                return toStringWithCustomContent(" itemIndex=" + itemIndex);
            }
        }
        
        /**
         * Represents an external name binding (one that does not appear in the source code being analyzed, e.g. one that
         * is implied by the information in a {@link ModuleTypeInfo} or a {@link CodeQualificationMap}).
         *
         * @author Joseph Wong
         */
        public static final class External<I extends IdentifierInfo> extends Binding<I> {
            
            /**
             * Constructs an instance of this identifier occurrence.
             * @param identifierInfo the identity of the CAL entity referred to by the identifier. Can *not* be null.
             */
            private External(final I identifierInfo) {
                super(identifierInfo, null, null);
            }
            
            /**
             * Factory method for constructing an instance of this identifier occurrence.
             * @param identifierInfo the identity of the CAL entity referred to by the identifier. Can *not* be null.
             * @return an instance of this class.
             */
            static <I extends IdentifierInfo> External<I> make(final I identifierInfo) {
                return new External<I>(identifierInfo);
            }
        }
        
        /**
         * Represents a data constructor field name appearing in the definition of its associated data constructor.
         *
         * @author Joseph Wong
         */
        public static final class DataConsFieldName extends Binding<IdentifierInfo.TopLevel.DataConsFieldName> {
            
            /**
             * The definition of the data constructor defining this field. Can *not* be null.
             */
            private final Binding.Definition<IdentifierInfo.TopLevel.DataCons> dataConsOccurrence;
            
            /**
             * Constructs an instance of this identifier occurrence.
             * @param identifierInfo the identity of the CAL entity referred to by the identifier. Can *not* be null.
             * @param sourceElement the source element associated with the occurrence. This may be more than just a name,
             *          but can be the entire definition of an entity. Can be null.
             * @param dataConsOccurrence the definition of the data constructor defining this field. Can *not* be null.
             * @param sourceRange the source range associated with the actual name occurring in source. Can be null.
             */
            private DataConsFieldName(
                final IdentifierInfo.TopLevel.DataConsFieldName identifierInfo, final Binding.Definition<IdentifierInfo.TopLevel.DataCons> dataConsOccurrence,
                final Name.Field sourceElement, final SourceRange sourceRange) {
                
                super(identifierInfo, sourceElement, sourceRange);
                
                if (dataConsOccurrence == null) {
                    throw new NullPointerException();
                }
                this.dataConsOccurrence = dataConsOccurrence;
            }
            
            /**
             * Factory method for constructing an instance of this identifier occurrence.
             * @param identifierInfo the identity of the CAL entity referred to by the identifier. Can *not* be null.
             * @param sourceElement the source element associated with the occurrence. This may be more than just a name,
             *          but can be the entire definition of an entity. Can be null.
             * @param dataConsOccurrence the definition of the data constructor defining this field. Can *not* be null.
             * @param sourceRange the source range associated with the actual name occurring in source. Can be null.
             * @return an instance of this class.
             */
            static DataConsFieldName make(
                final IdentifierInfo.TopLevel.DataConsFieldName identifierInfo, final Binding.Definition<IdentifierInfo.TopLevel.DataCons> dataConsOccurrence,
                final Name.Field sourceElement, final SourceRange sourceRange) {
                
                return new DataConsFieldName(identifierInfo, dataConsOccurrence, sourceElement, sourceRange);
            }
            
            /**
             * @return the definition of the data constructor defining this field. Can *not* be null.
             */
            public Binding.Definition<IdentifierInfo.TopLevel.DataCons> getDataConsOccurrence() {
                return dataConsOccurrence;
            }
            
            /**
             * {@inheritDoc}
             */
            @Override
            public String toString() {
                return toStringWithCustomContent(" dataConsOccurrence=" + dataConsOccurrence);
            }
        }
        
        /**
         * Represents a punned textual data constructor field name (which is a local variable binding).
         * The associated identifier info should be either a case patter variable or a local pattern match variable.
         *
         * <p>
         * A data constructor field name can be associated with multiple data constructors when it appears
         * in a case alternative containing more than one data constructor, e.g.
         * <pre>
         * case foo of
         * (DC1|DC2|DC3) {field1=bar, field2} -> ...
         * </pre>
         * Both field1 and field2 are associated with DC1, DC2, and DC3, and field2 is a punned data constructor field name.
         * 
         * @author Joseph Wong
         */
        public static final class PunnedTextualDataConsFieldName<I extends IdentifierInfo.Local> extends Binding<I> {
            
            /**
             * The list of bindings for the data constructors associated with the field name. Can *not* be null.
             * The list should be non-empty. If the list has more than one data constructor, the ordering should
             * following that of the case alternative.
             */
            private final List<Binding<IdentifierInfo.TopLevel.DataCons>> dataConsNameBindings;
            
            /**
             * Constructs an instance of this identifier occurrence.
             * @param identifierInfo the identity of the CAL entity referred to by the identifier. Can *not* be null.
             * @param dataConsNameBindings the list of bindings for the data constructors associated with the field name.
             *          Can *not* be null. The list should be non-empty.
             * @param sourceElement the source element associated with the occurrence. This may be more than just a name,
             *          but can be the entire definition of an entity. Can be null.
             * @param sourceRange the source range associated with the actual name occurring in source. Can be null.
             */
            private PunnedTextualDataConsFieldName(
                final I identifierInfo, final List<Binding<IdentifierInfo.TopLevel.DataCons>> dataConsNameBindings,
                final SourceElement sourceElement, final SourceRange sourceRange) {
                
                super(identifierInfo, sourceElement, sourceRange);
                
                if (dataConsNameBindings == null) {
                    throw new NullPointerException();
                }
                this.dataConsNameBindings = Collections.unmodifiableList(new ArrayList<Binding<IdentifierInfo.TopLevel.DataCons>>(dataConsNameBindings));
            }
            
            /**
             * Factory method for constructing an instance of this identifier occurrence.
             * @param identifierInfo the identity of the CAL entity referred to by the identifier. Can *not* be null.
             * @param dataConsNameBindings the list of bindings for the data constructors associated with the field name.
             *          Can *not* be null. The list should be non-empty.
             * @param sourceElement the source element associated with the occurrence. This may be more than just a name,
             *          but can be the entire definition of an entity. Can be null.
             * @param sourceRange the source range associated with the actual name occurring in source. Can be null.
             * @return an instance of this class.
             */
            static <I extends IdentifierInfo.Local> PunnedTextualDataConsFieldName<I> make(
                final I identifierInfo, final List<Binding<IdentifierInfo.TopLevel.DataCons>> dataConsNameBindings,
                final SourceElement sourceElement, final SourceRange sourceRange) {
                
                return new PunnedTextualDataConsFieldName<I>(identifierInfo, dataConsNameBindings, sourceElement, sourceRange);
            }
            
            /**
             * @return the list of bindings for the data constructors associated with the field name. Can *not* be null.
             * The list should be non-empty. If the list has more than one data constructor, the ordering should
             * following that of the case alternative.
             */
            public List<Binding<IdentifierInfo.TopLevel.DataCons>> getDataConsNameBindings() {
                return dataConsNameBindings;
            }
            
            /**
             * {@inheritDoc}
             */
            @Override
            public String toString() {
                return toStringWithCustomContent(" dataConsNameBindings=" + dataConsNameBindings);
            }
        }
        
        /**
         * Represents a punned textual record field name (which is a local variable binding).
         * The associated identifier info should be either a case patter variable or a local pattern match variable.
         *
         * @author Joseph Wong
         */
        public static final class PunnedTextualRecordFieldName<I extends IdentifierInfo.Local> extends Binding<I> {
            
            /**
             * Constructs an instance of this identifier occurrence.
             * @param identifierInfo the identity of the CAL entity referred to by the identifier. Can *not* be null.
             * @param sourceElement the source element associated with the occurrence. This may be more than just a name,
             *          but can be the entire definition of an entity. Can be null.
             * @param sourceRange the source range associated with the actual name occurring in source. Can be null.
             */
            private PunnedTextualRecordFieldName(final I identifierInfo, final SourceElement sourceElement, final SourceRange sourceRange) {
                super(identifierInfo, sourceElement, sourceRange);
            }
            
            /**
             * Factory method for constructing an instance of this identifier occurrence.
             * @param identifierInfo the identity of the CAL entity referred to by the identifier. Can *not* be null.
             * @param sourceElement the source element associated with the occurrence. This may be more than just a name,
             *          but can be the entire definition of an entity. Can be null.
             * @param sourceRange the source range associated with the actual name occurring in source. Can be null.
             * @return an instance of this class.
             */
            static <I extends IdentifierInfo.Local> PunnedTextualRecordFieldName<I> make(final I identifierInfo, final SourceElement sourceElement, final SourceRange sourceRange) {
                return new PunnedTextualRecordFieldName<I>(identifierInfo, sourceElement, sourceRange);
            }
        }
        
        /**
         * The source element associated with the occurrence. This may be more than just a name, but
         * can be the entire definition of an entity.
         * 
         * Can be null.
         */
        private final SourceElement sourceElement;
        
        /**
         * Private constructor. Intended for subclasses only.
         * @param identifierInfo the identity of the CAL entity referred to by the identifier. Can *not* be null.
         * @param sourceElement the source element associated with the occurrence. This may be more than just a name,
         *          but can be the entire definition of an entity. Can be null.
         * @param sourceRange the source range associated with the actual name occurring in source. Can be null.
         */
        private Binding(final I identifierInfo, final SourceElement sourceElement, final SourceRange sourceRange) {
            super(identifierInfo, sourceRange);
            this.sourceElement = sourceElement;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public final SourceElement getSourceElement() {
            return sourceElement;
        }
    }
    
    /**
     * Abstract base class for an occurrence which is a reference (to an entity in scope).
     * Unlike the other occurrence types, an instance of a class in this hierarchy *must* be associated with a source element.
     *
     * @author Joseph Wong
     */
    public static abstract class Reference<I extends IdentifierInfo> extends IdentifierOccurrence<I> {
        
        /**
         * Represents a reference to a module (whether the name can be partially qualified, or must be fully qualified).
         *
         * @author Joseph Wong
         * @author Iulian Radu
         * @author Peter Cardwell
         */
        public static final class Module extends Reference<IdentifierInfo.Module> {
            
            /**
             * Whether the name must be fully qualified or can be partially qualified.
             */
            private final boolean mustBeFullyQualified;
            
            /**
             * The source element associated with the occurrence. Can *not* be null.
             * This should be either a
             * {@link org.openquark.cal.compiler.SourceModel.Name.Module SourceModel.Name.Module} or a
             * {@link org.openquark.cal.compiler.SourceModel.Name.WithoutContextCons SourceModel.Name.WithoutContextCons}.
             */
            private final Name sourceElement;
            
            /**
             * Constructs an instance of this identifier occurrence.
             * @param mustBeFullyQualified whether the name must be fully qualified or can be partially qualified.
             * @param identifierInfo the identity of the CAL entity referred to by the identifier. Can *not* be null.
             * @param sourceElement the source element associated with the occurrence. Can *not* be null.
             * @param sourceRange the source range associated with the actual name occurring in source. Can be null.
             */
            private Module(final boolean mustBeFullyQualified, final IdentifierInfo.Module identifierInfo, final Name sourceElement, final SourceRange sourceRange) {
                super(identifierInfo, sourceRange);
                if (sourceElement == null) {
                    throw new NullPointerException();
                }
                this.mustBeFullyQualified = mustBeFullyQualified;
                this.sourceElement = sourceElement;
            }
            
            /**
             * Factory method for constructing an instance of this identifier occurrence.
             * @param mustBeFullyQualified whether the name must be fully qualified or can be partially qualified.
             * @param identifierInfo the identity of the CAL entity referred to by the identifier. Can *not* be null.
             * @param sourceElement the source element associated with the occurrence. Can be null.
             * @return an instance of this class.
             */
            static Module make(final boolean mustBeFullyQualified, final IdentifierInfo.Module identifierInfo, final Name.Module sourceElement) {
                return new Module(mustBeFullyQualified, identifierInfo, sourceElement, sourceElement.getSourceRange());
            }
            
            /**
             * Factory method for constructing an instance of this identifier occurrence.
             * @param mustBeFullyQualified whether the name must be fully qualified or can be partially qualified.
             * @param identifierInfo the identity of the CAL entity referred to by the identifier. Can *not* be null.
             * @param sourceElement the source element associated with the occurrence. Can be null.
             * @return an instance of this class.
             */
            static Module make(final boolean mustBeFullyQualified, final IdentifierInfo.Module identifierInfo, final Name.WithoutContextCons sourceElement) {
                return new Module(mustBeFullyQualified, identifierInfo, sourceElement, sourceElement.getSourceRange());
            }
            
            /**
             * @return whether the name must be fully qualified or can be partially qualified.
             */
            public boolean mustBeFullyQualified() {
                return mustBeFullyQualified;
            }
            
            /**
             * {@inheritDoc}
             * 
             * @return the source element associated with the occurrence. Can *not* be null. This should be either a
             *   {@link org.openquark.cal.compiler.SourceModel.Name.Module SourceModel.Name.Module} or a
             *   {@link org.openquark.cal.compiler.SourceModel.Name.WithoutContextCons SourceModel.Name.WithoutContextCons}.
             */
            @Override
            public Name getSourceElement() {
                return sourceElement;
            }
            
            /**
             * {@inheritDoc}
             */
            @Override
            public String toString() {
                return toStringWithCustomContent(" mustBeFullyQualified=" + mustBeFullyQualified);
            }
        }
        
        /**
         * Represents a reference to a top-level (scoped) entity in a context where qualification is allowed or required.
         *
         * @author Joseph Wong
         * @author Iulian Radu
         * @author Peter Cardwell
         */
        public static final class Qualifiable<I extends IdentifierInfo.TopLevel> extends Reference<I> {
            
            /**
             * Whether the reference appears in an expression context (i.e. the reference itself is an expression).
             */
            private final boolean isExpressionContext;

            /**
             * The occurrence of the module name within this occurrence.
             * Can be null if this occurrence is an unqualified (but qualifiable) occurrence.
             */
            private final Reference.Module moduleNameOccurrence;
            
            /**
             * The source element associated with the occurrence. Can *not* be null.
             */
            private final Name.Qualifiable sourceElement;
            
            /**
             * Constructs an instance of this identifier occurrence.
             * @param identifierInfo the identity of the CAL entity referred to by the identifier. Can *not* be null.
             * @param isExpressionContext whether the reference appears in an expression context (i.e. the reference itself is an expression).
             * @param moduleNameOccurrence the occurrence of the module name within this occurrence.
             *          Can be null if this occurrence is an unqualified (but qualifiable) occurrence.
             * @param sourceElement the source element associated with the occurrence. Can *not* be null.
             * @param sourceRange the source range associated with the actual name occurring in source. Can be null.
             */
            private Qualifiable(final I identifierInfo, final boolean isExpressionContext, final Reference.Module moduleNameOccurrence, final Name.Qualifiable sourceElement, final SourceRange sourceRange) {
                super(identifierInfo, sourceRange);
                this.isExpressionContext = isExpressionContext;
                this.moduleNameOccurrence = moduleNameOccurrence;
                
                if (sourceElement == null) {
                    throw new NullPointerException();
                }
                this.sourceElement = sourceElement;
            }
            
            /**
             * Factory method for constructing an instance of this identifier occurrence.
             * @param identifierInfo the identity of the CAL entity referred to by the identifier. Can *not* be null.
             * @param isExpressionContext whether the reference appears in an expression context (i.e. the reference itself is an expression).
             * @param moduleNameOccurrence the occurrence of the module name within this occurrence.
             *          Can be null if this occurrence is an unqualified (but qualifiable) occurrence.
             * @param sourceElement the source element associated with the occurrence. Can *not* be null.
             * @return an instance of this class.
             */
            static <I extends IdentifierInfo.TopLevel> Qualifiable<I> make(final I identifierInfo, final boolean isExpressionContext, final Reference.Module moduleNameOccurrence, final Name.Qualifiable sourceElement) {
                return new Qualifiable<I>(identifierInfo, isExpressionContext, moduleNameOccurrence, sourceElement, sourceElement.getSourceRange());
            }
            
            /**
             * @return true if the reference appears in an expression context; false otherwise.
             */
            public boolean isExpressionContext() {
                return isExpressionContext;
            }
            
            /**
             * @return the occurrence of the module name within this occurrence.
             * Can be null if this occurrence is an unqualified (but qualifiable) occurrence. 
             */
            public Reference.Module getModuleNameOccurrence() {
                return moduleNameOccurrence;
            }

            /**
             * {@inheritDoc}
             * 
             * @return the source element associated with the occurrence. Can *not* be null.
             */
            @Override
            public Name.Qualifiable getSourceElement() {
                return sourceElement;
            }
            
            /**
             * {@inheritDoc}
             */
            @Override
            public String toString() {
                return toStringWithCustomContent(" isExpressionContext=" + isExpressionContext + " moduleNameOccurrence=" + moduleNameOccurrence);
            }
        }
        
        /**
         * Represents a reference to a top-level (scoped) entity in a context where qualification is <i>not</i> allowed.
         *
         * @author Joseph Wong
         */
        public static final class NonQualifiable<I extends IdentifierInfo> extends Reference<I> {
            
            /**
             * The source element associated with the occurrence. Can *not* be null.
             */
            private final SourceElement sourceElement;
            
            /**
             * Constructs an instance of this identifier occurrence.
             * @param identifierInfo the identity of the CAL entity referred to by the identifier. Can *not* be null.
             * @param sourceElement the source element associated with the occurrence. Can *not* be null.
             * @param sourceRange the source range associated with the actual name occurring in source. Can be null.
             */
            private NonQualifiable(final I identifierInfo, final SourceElement sourceElement, final SourceRange sourceRange) {
                super(identifierInfo, sourceRange);
                if (sourceElement == null) {
                    throw new NullPointerException();
                }
                this.sourceElement = sourceElement;
            }
            
            /**
             * Factory method for constructing an instance of this identifier occurrence.
             * @param identifierInfo the identity of the CAL entity referred to by the identifier. Can *not* be null.
             * @param sourceElement the source element associated with the occurrence. Can *not* be null.
             * @param sourceRange the source range associated with the actual name occurring in source. Can be null.
             * @return an instance of this class.
             */
            static <I extends IdentifierInfo> NonQualifiable<I> make(final I identifierInfo, final SourceElement sourceElement, final SourceRange sourceRange) {
                return new NonQualifiable<I>(identifierInfo, sourceElement, sourceRange);
            }
            
            /**
             * {@inheritDoc}
             * 
             * @return the source element associated with the occurrence. Can *not* be null.
             */
            @Override
            public SourceElement getSourceElement() {
                return sourceElement;
            }
        }
        
        /**
         * Represents a reference to a locally-bound entity.
         *
         * @author Joseph Wong
         */
        public static final class Local<I extends IdentifierInfo.Local> extends Reference<I> {
            
            /**
             * The source element associated with the occurrence. Can *not* be null.
             * This should be either a
             * {@link org.openquark.cal.compiler.SourceModel.Name.Function SourceModel.Name.Function} or a
             * {@link org.openquark.cal.compiler.SourceModel.Name.Field SourceModel.Name.Field}.
             */
            private final Name sourceElement;
            
            /**
             * Constructs an instance of this identifier occurrence.
             * @param identifierInfo the identity of the CAL entity referred to by the identifier. Can *not* be null.
             * @param sourceElement the source element associated with the occurrence. Can *not* be null.
             * @param sourceRange the source range associated with the actual name occurring in source. Can be null.
             */
            private Local(final I identifierInfo, final Name sourceElement, final SourceRange sourceRange) {
                super(identifierInfo, sourceRange);
                if (sourceElement == null) {
                    throw new NullPointerException();
                }
                this.sourceElement = sourceElement;
            }
            
            /**
             * Factory method for constructing an instance of this identifier occurrence.
             * @param identifierInfo the identity of the CAL entity referred to by the identifier. Can *not* be null.
             * @param sourceElement the source element associated with the occurrence. Can *not* be null.
             * @return an instance of this class.
             */
            static <I extends IdentifierInfo.Local> Local<I> make(final I identifierInfo, final Name.Function sourceElement) {
                if (sourceElement.getModuleName() != null) {
                    throw new IllegalArgumentException("The name should be unqualified");
                }
                return new Local<I>(identifierInfo, sourceElement, sourceElement.getSourceRange());
            }
            
            /**
             * Factory method for constructing an instance of this identifier occurrence.
             * @param identifierInfo the identity of the CAL entity referred to by the identifier. Can *not* be null.
             * @param sourceElement the source element associated with the occurrence. Can *not* be null.
             * @return an instance of this class.
             */
            static <I extends IdentifierInfo.Local> Local<I> make(final I identifierInfo, final Name.Field sourceElement) {
                return new Local<I>(identifierInfo, sourceElement, sourceElement.getSourceRange());
            }
            
            /**
             * {@inheritDoc}
             * 
             * @return the source element associated with the occurrence. Can *not* be null. This should be either a
             *   {@link org.openquark.cal.compiler.SourceModel.Name.Function SourceModel.Name.Function} or a
             *   {@link org.openquark.cal.compiler.SourceModel.Name.Field SourceModel.Name.Field}.
             */
            @Override
            public Name getSourceElement() {
                return sourceElement;
            }
        }
        
        /**
         * Abstract base class for an occurrence which is a reference to a data constructor field (including occurrences
         * of all associated data constructors).
         *
         * @author Joseph Wong
         */
        public static abstract class DataConsFieldName extends Reference<IdentifierInfo.TopLevel.DataConsFieldName> {
            
            /**
             * Represents a data constructor field name not appearing in a punned context (including occurrences
             * of all associated data constructors).
             *
             * @author Joseph Wong
             */
            public static final class NonPunned extends DataConsFieldName {
                
                /**
                 * Constructs an instance of this identifier occurrence.
                 * @param identifierInfo the identity of the CAL entity referred to by the identifier. Can *not* be null.
                 * @param dataConsOccurrences the list of references for the data constructors associated with the field name.
                 *          Can *not* be null. The list should be non-empty.
                 * @param sourceElement the source element associated with the occurrence. Can *not* be null.
                 * @param sourceRange the source range associated with the actual name occurring in source. Can be null.
                 */
                private NonPunned(
                    final IdentifierInfo.TopLevel.DataConsFieldName identifierInfo, final List<Reference.Qualifiable<IdentifierInfo.TopLevel.DataCons>> dataConsOccurrences,
                    final Name.Field sourceElement, final SourceRange sourceRange) {
                    
                    super(identifierInfo, dataConsOccurrences, sourceElement, sourceRange);
                }
                
                /**
                 * Factory method for constructing an instance of this identifier occurrence.
                 * @param identifierInfo the identity of the CAL entity referred to by the identifier. Can *not* be null.
                 * @param dataConsOccurrences the list of references for the data constructors associated with the field name.
                 *          Can *not* be null. The list should be non-empty.
                 * @param sourceElement the source element associated with the occurrence. Can *not* be null.
                 * @return an instance of this class.
                 */
                static NonPunned make(
                    final IdentifierInfo.TopLevel.DataConsFieldName identifierInfo, final List<Reference.Qualifiable<IdentifierInfo.TopLevel.DataCons>> dataConsOccurrences,
                    final Name.Field sourceElement) {
                    
                    return new NonPunned(identifierInfo, dataConsOccurrences, sourceElement, sourceElement.getSourceRange());
                }
            }
            
            /**
             * Represents a punned ordinal data constructor field name (including occurrences of all associated data constructors).
             *
             * @author Joseph Wong
             */
            public static final class PunnedOrdinal extends DataConsFieldName {
                
                /**
                 * Constructs an instance of this identifier occurrence.
                 * @param identifierInfo the identity of the CAL entity referred to by the identifier. Can *not* be null.
                 * @param dataConsOccurrences the list of references for the data constructors associated with the field name.
                 *          Can *not* be null. The list should be non-empty.
                 * @param sourceElement the source element associated with the occurrence. Can *not* be null.
                 * @param sourceRange the source range associated with the actual name occurring in source. Can be null.
                 */
                private PunnedOrdinal(
                    final IdentifierInfo.TopLevel.DataConsFieldName identifierInfo, final List<Reference.Qualifiable<IdentifierInfo.TopLevel.DataCons>> dataConsOccurrences,
                    final Name.Field sourceElement, final SourceRange sourceRange) {
                    
                    super(identifierInfo, dataConsOccurrences, sourceElement, sourceRange);
                }
                
                /**
                 * Factory method for constructing an instance of this identifier occurrence.
                 * @param identifierInfo the identity of the CAL entity referred to by the identifier. Can *not* be null.
                 * @param dataConsOccurrences the list of references for the data constructors associated with the field name.
                 *          Can *not* be null. The list should be non-empty.
                 * @param sourceElement the source element associated with the occurrence. Can *not* be null.
                 * @return an instance of this class.
                 */
                static PunnedOrdinal make(
                    final IdentifierInfo.TopLevel.DataConsFieldName identifierInfo, final List<Reference.Qualifiable<IdentifierInfo.TopLevel.DataCons>> dataConsOccurrences,
                    final Name.Field sourceElement) {
                    
                    if (!(sourceElement.getName() instanceof FieldName.Ordinal)) {
                        throw new IllegalArgumentException("The field name is not an ordinal: " + sourceElement);
                    }
                    return new PunnedOrdinal(identifierInfo, dataConsOccurrences, sourceElement, sourceElement.getSourceRange());
                }
            }
            
            /**
             * The list of references for the data constructors associated with the field name. Can *not* be null.
             * The list should be non-empty. If the list has more than one data constructor, the ordering should
             * following that of the case alternative.
             */
            private final List<Reference.Qualifiable<IdentifierInfo.TopLevel.DataCons>> dataConsOccurrences;
            
            /**
             * The source element associated with the occurrence. Can *not* be null.
             */
            private final Name.Field sourceElement;
            
            /**
             * Constructs an instance of this identifier occurrence.
             * @param identifierInfo the identity of the CAL entity referred to by the identifier. Can *not* be null.
             * @param dataConsOccurrences the list of references for the data constructors associated with the field name.
             *          Can *not* be null. The list should be non-empty.
             * @param sourceElement the source element associated with the occurrence. Can *not* be null.
             * @param sourceRange the source range associated with the actual name occurring in source. Can be null.
             */
            private DataConsFieldName(
                final IdentifierInfo.TopLevel.DataConsFieldName identifierInfo, final List<Reference.Qualifiable<IdentifierInfo.TopLevel.DataCons>> dataConsOccurrences,
                final Name.Field sourceElement, final SourceRange sourceRange) {
                
                super(identifierInfo, sourceRange);
                if (sourceElement == null || dataConsOccurrences == null) {
                    throw new NullPointerException();
                }
                this.sourceElement = sourceElement;
                this.dataConsOccurrences = Collections.unmodifiableList(new ArrayList<Reference.Qualifiable<IdentifierInfo.TopLevel.DataCons>>(dataConsOccurrences));
            }
            
            /**
             * @return the list of references for the data constructors associated with the field name. Can *not* be null.
             * The list should be non-empty. If the list has more than one data constructor, the ordering should
             * following that of the case alternative.
             */
            public List<Reference.Qualifiable<IdentifierInfo.TopLevel.DataCons>> getDataConsOccurrences() {
                return dataConsOccurrences;
            }
            
            /**
             * {@inheritDoc}
             * 
             * @return the source element associated with the occurrence. Can *not* be null.
             */
            @Override
            public Name.Field getSourceElement() {
                return sourceElement;
            }
            
            /**
             * {@inheritDoc}
             */
            @Override
            public String toString() {
                return toStringWithCustomContent(" dataConsOccurrences=" + dataConsOccurrences);
            }
        }
        
        /**
         * Abstract base class for an occurrence which is a reference to a record field.
         *
         * @author Joseph Wong
         */
        public static abstract class RecordFieldName extends Reference<IdentifierInfo.RecordFieldName> {
            
            /**
             * Represents a record field name not appearing in a punned context.
             *
             * @author Joseph Wong
             */
            public static final class NonPunned extends RecordFieldName {
                
                /**
                 * Constructs an instance of this identifier occurrence.
                 * @param identifierInfo the identity of the CAL entity referred to by the identifier. Can *not* be null.
                 * @param sourceElement the source element associated with the occurrence. Can *not* be null.
                 * @param sourceRange the source range associated with the actual name occurring in source. Can be null.
                 */
                private NonPunned(final IdentifierInfo.RecordFieldName identifierInfo, final Name.Field sourceElement, final SourceRange sourceRange) {
                    super(identifierInfo, sourceElement, sourceRange);
                }                
                
                /**
                 * Factory method for constructing an instance of this identifier occurrence.
                 * @param identifierInfo the identity of the CAL entity referred to by the identifier. Can *not* be null.
                 * @param sourceElement the source element associated with the occurrence. Can *not* be null.
                 * @return an instance of this class.
                 */
                static NonPunned make(
                    final IdentifierInfo.RecordFieldName identifierInfo, final Name.Field sourceElement) {
                    
                    return new NonPunned(identifierInfo, sourceElement, sourceElement.getSourceRange());
                }
            }
            
            /**
             * Represents a punned ordinal record field name.
             *
             * @author Joseph Wong
             */
            public static final class PunnedOrdinal extends RecordFieldName {
                
                /**
                 * Constructs an instance of this identifier occurrence.
                 * @param identifierInfo the identity of the CAL entity referred to by the identifier. Can *not* be null.
                 * @param sourceElement the source element associated with the occurrence. Can *not* be null.
                 * @param sourceRange the source range associated with the actual name occurring in source. Can be null.
                 */
                private PunnedOrdinal(final IdentifierInfo.RecordFieldName identifierInfo, final Name.Field sourceElement, final SourceRange sourceRange) {
                    super(identifierInfo, sourceElement, sourceRange);
                }                
                
                /**
                 * Factory method for constructing an instance of this identifier occurrence.
                 * @param identifierInfo the identity of the CAL entity referred to by the identifier. Can *not* be null.
                 * @param sourceElement the source element associated with the occurrence. Can *not* be null.
                 * @return an instance of this class.
                 */
                static PunnedOrdinal make(
                    final IdentifierInfo.RecordFieldName identifierInfo, final Name.Field sourceElement) {
                    
                    if (!(sourceElement.getName() instanceof FieldName.Ordinal)) {
                        throw new IllegalArgumentException("The field name is not an ordinal: " + sourceElement);
                    }
                    return new PunnedOrdinal(identifierInfo, sourceElement, sourceElement.getSourceRange());
                }
            }
            
            /**
             * The source element associated with the occurrence. Can *not* be null.
             */
            private final Name.Field sourceElement;
            
            /**
             * Constructs an instance of this identifier occurrence.
             * @param identifierInfo the identity of the CAL entity referred to by the identifier. Can *not* be null.
             * @param sourceElement the source element associated with the occurrence. Can *not* be null.
             * @param sourceRange the source range associated with the actual name occurring in source. Can be null.
             */
            private RecordFieldName(final IdentifierInfo.RecordFieldName identifierInfo, final Name.Field sourceElement, final SourceRange sourceRange) {
                super(identifierInfo, sourceRange);
                if (sourceElement == null) {
                    throw new NullPointerException();
                }
                this.sourceElement = sourceElement;
            }
            
            /**
             * {@inheritDoc}
             * 
             * @return the source element associated with the occurrence. Can *not* be null.
             */
            @Override
            public Name.Field getSourceElement() {
                return sourceElement;
            }
        }
        
        /**
         * Represents an operator reference (to a function, class method, type constructor, or data constructor).
         *
         * @author Joseph Wong
         */
        public static final class Operator<I extends IdentifierInfo.TopLevel> extends Reference<I> {
            
            /**
             * Whether the reference appears in an expression context (i.e. the reference itself is an operator in an operator expression).
             */
            private final boolean isExpressionContext;
            
            /**
             * The source element associated with the occurrence. Can *not* be null.
             */
            private final SourceElement sourceElement;
            
            /**
             * Whether the operator consists of a pair of delimiters, e.g. (), [].
             */
            private final boolean isDelimiterPair;
            
            /**
             * Constructs an instance of this identifier occurrence.
             * @param identifierInfo the identity of the CAL entity referred to by the identifier. Can *not* be null.
             * @param isExpressionContext whether the reference appears in an expression context (i.e. the reference itself is an operator in an operator expression).
             * @param sourceElement the source element associated with the occurrence. Can *not* be null.
             * @param isDelimiterPair whether the operator consists of a pair of delimiters, e.g. (), [].
             * @param operatorSourceRange the source range associated with the actual operator occurring in source. Can be null.
             */
            private Operator(final I identifierInfo, final boolean isExpressionContext, final SourceElement sourceElement, final boolean isDelimiterPair, final SourceRange operatorSourceRange) {
                super(identifierInfo, operatorSourceRange);
                if (sourceElement == null) {
                    throw new NullPointerException();
                }
                this.isExpressionContext = isExpressionContext;
                this.sourceElement = sourceElement;
                this.isDelimiterPair = isDelimiterPair;
            }
            
            /**
             * Factory method for constructing an instance of this identifier occurrence.
             * @param identifierInfo the identity of the CAL entity referred to by the identifier. Can *not* be null.
             * @param isExpressionContext whether the reference appears in an expression context (i.e. the reference itself is an operator in an operator expression).
             * @param sourceElement the source element associated with the occurrence. Can *not* be null.
             * @param isDelimiterPair whether the operator consists of a pair of delimiters, e.g. (), [].
             * @param operatorSourceRange the source range associated with the actual operator occurring in source. Can be null.
             * @return an instance of this class.
             */
            static <I extends IdentifierInfo.TopLevel> Operator<I> make(
                final I identifierInfo, final boolean isExpressionContext, final SourceElement sourceElement, final boolean isDelimiterPair, final SourceRange operatorSourceRange) {
                
                return new Operator<I>(identifierInfo, isExpressionContext, sourceElement, isDelimiterPair, operatorSourceRange);
            }
            
            /**
             * @return whether the reference appears in an expression context (i.e. the reference itself is an operator in an operator expression).
             */
            public boolean isExpressionContext() {
                return isExpressionContext;
            }

            /**
             * {@inheritDoc}
             * 
             * @return the source element associated with the occurrence. Can *not* be null. 
             */
            @Override
            public SourceElement getSourceElement() {
                return sourceElement;
            }
            
            /**
             * @return wWhether the operator consists of a pair of delimiters, e.g. (), []. 
             */
            public boolean isDelimiterPair() {
                return isDelimiterPair;
            }
            
            /**
             * {@inheritDoc}
             */
            @Override
            public String toString() {
                return toStringWithCustomContent(" isExpressionContext=" + isExpressionContext + " isDelimiterPair=" + isDelimiterPair);
            }
        }
        
        /**
         * Represents a reference to a type variable.
         *
         * @author Joseph Wong
         */
        public static final class TypeVariable extends Reference<IdentifierInfo.TypeVariable> {
            
            /**
             * The source element associated with the occurrence. Can *not* be null.
             */
            private final Name.TypeVar sourceElement;

            /**
             * Constructs an instance of this identifier occurrence.
             * @param identifierInfo the identity of the CAL entity referred to by the identifier. Can *not* be null.
             * @param sourceElement the source element associated with the occurrence. Can *not* be null.
             * @param sourceRange the source range associated with the actual name occurring in source. Can be null.
             */
            private TypeVariable(final IdentifierInfo.TypeVariable identifierInfo, final Name.TypeVar sourceElement, final SourceRange sourceRange) {
                super(identifierInfo, sourceRange);
                if (sourceElement == null) {
                    throw new NullPointerException();
                }
                this.sourceElement = sourceElement;
            }
            
            /**
             * Factory method for constructing an instance of this identifier occurrence.
             * @param identifierInfo the identity of the CAL entity referred to by the identifier. Can *not* be null.
             * @param sourceElement the source element associated with the occurrence. Can *not* be null.
             * @return an instance of this class.
             */
            static TypeVariable make(final IdentifierInfo.TypeVariable identifierInfo, final Name.TypeVar sourceElement) {
                return new TypeVariable(identifierInfo, sourceElement, sourceElement.getSourceRange());
            }

            /**
             * {@inheritDoc}
             * 
             * @return the source element associated with the occurrence. Can *not* be null. 
             */
            @Override
            public SourceElement getSourceElement() {
                return sourceElement;
            }
        }
        
        /**
         * Private constructor. Intended for subclasses only.
         * @param identifierInfo the identity of the CAL entity referred to by the identifier. Can *not* be null.
         * @param sourceRange the source range associated with the actual name occurring in source. Can be null.
         */
        private Reference(final I identifierInfo, final SourceRange sourceRange) {
            super(identifierInfo, sourceRange);
        }
    }
    
    /**
     * Represents a foreign descriptor (or external name) for a foreign entity.
     *
     * @author Joseph Wong
     */
    public static final class ForeignDescriptor<I extends IdentifierInfo.TopLevel> extends IdentifierOccurrence<I> {
        
        /**
         * The source element associated with the occurrence. Can *not* be null.
         */
        private final SourceElement sourceElement;
        
        /**
         * Constructs an instance of this identifier occurrence.
         * @param identifierInfo the identity of the CAL entity referred to by the identifier. Can *not* be null.
         * @param sourceElement the source element associated with the occurrence. Can *not* be null.
         * @param foreignDescriptorSourceRange the source range associated with the actual foreign descriptor occurring in source. Can be null.
         */
        private ForeignDescriptor(final I identifierInfo, final SourceElement sourceElement, final SourceRange foreignDescriptorSourceRange) {
            super(identifierInfo, foreignDescriptorSourceRange);
            if (sourceElement == null) {
                throw new NullPointerException();
            }
            this.sourceElement = sourceElement;
        }
        
        /**
         * Factory method for constructing an instance of this identifier occurrence.
         * @param identifierInfo the identity of the CAL entity referred to by the identifier. Can *not* be null.
         * @param sourceElement the source element associated with the occurrence. Can *not* be null.
         * @param foreignDescriptorSourceRange the source range associated with the actual foreign descriptor occurring in source. Can be null.
         * @return an instance of this class.
         */
        static <I extends IdentifierInfo.TopLevel> ForeignDescriptor<I> make(
            final I identifierInfo, final SourceElement sourceElement, final SourceRange foreignDescriptorSourceRange) {
            
            return new ForeignDescriptor<I>(identifierInfo, sourceElement, foreignDescriptorSourceRange);
        }

        /**
         * {@inheritDoc}
         * 
         * @return the source element associated with the occurrence. Can *not* be null.
         */
        @Override
        public SourceElement getSourceElement() {
            return sourceElement;
        }
    }
    
    /**
     * A {@link Comparator} implementation for {@link IdentifierOccurrence}s based on source positions.
     *
     * @author Joseph Wong
     */
    private static final class SourcePositionBasedComparator implements Comparator<IdentifierOccurrence<?>> {

        public int compare(final IdentifierOccurrence<?> a, final IdentifierOccurrence<?> b) {
            final SourceRange aRange = a.getSourceRange();
            final SourceRange bRange = b.getSourceRange();
            
            // null < non-null
            if (aRange == null) {
                if (bRange == null) {
                    return 0;
                } else {
                    return -1;
                }
            } else {
                if (bRange == null) {
                    return 1;
                } else {
                    // first by start position, then by end position
                    final int startPosComparison = SourcePosition.compareByPosition.compare(
                        aRange.getStartSourcePosition(), bRange.getStartSourcePosition());
                    
                    if (startPosComparison != 0) {
                        return startPosComparison;
                    } else {
                        return SourcePosition.compareByPosition.compare(
                            aRange.getEndSourcePosition(), bRange.getEndSourcePosition());
                    }
                }
            }
        }
    }
    
    /**
     * A {@link Comparator} singleton for {@link IdentifierOccurrence}s based on source positions.
     */
    public static final Comparator<IdentifierOccurrence<?>> SOURCE_POSITION_BASED_COMPARATOR = new SourcePositionBasedComparator();
    
    /**
     * The identity of the CAL entity referred to by the identifier.
     * 
     * Can *not* be null.
     */
    private final I identifierInfo;
    
    /**
     * The source range associated with the precise reference occurring in source.
     * 
     * Can be null.
     */
    private final SourceRange sourceRange;
    
    /**
     * Private constructor. Intended for subclasses only.
     * @param identifierInfo the identity of the CAL entity referred to by the identifier. Can *not* be null.
     * @param sourceRange the source range associated with the precise reference occurring in source. Can be null.
     */
    private IdentifierOccurrence(final I identifierInfo, final SourceRange sourceRange) {
        if (identifierInfo == null) {
            throw new NullPointerException();
        }
        this.identifierInfo = identifierInfo;
        this.sourceRange = sourceRange;
    }
    
    /**
     * @return the identity of the CAL entity referred to by the identifier. Can *not* be null.
     */
    public final I getIdentifierInfo() {
        return identifierInfo;
    }
    
    /**
     * @return the source element associated with the occurrence. This may be more than just a name,
     *         but can be the entire definition of an entity. Can be null.
     */
    public abstract SourceElement getSourceElement();
    
    /**
     * @return the source range associated with the precise reference occurring in source. Can be null.
     */
    public final SourceRange getSourceRange() {
        return sourceRange;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return toStringWithCustomContent("");
    }

    /**
     * Produces a string for {@link #toString()} containing standard content, and with the provided custom content.
     * @param customContent the custom contented to be included.
     * @return a string representing this instance.
     */
    String toStringWithCustomContent(final String customContent) {
        return "{" + getShortClassName(this.getClass())
            + " identifier=" + getIdentifierInfo()
            + customContent
            + " sourceElement::" + getShortClassName(getSourceElement().getClass())
            + " sourceRange=" + getSourceRange() + "}";
    }
    
    /**
     * Produces a short name of a class, e.g. IdentifierOccurrence.Reference.Module is a short name.
     * @param clazz the class, which must be in a package.
     * @return a short name for the class.
     */
    private static final String getShortClassName(final Class<?> clazz) {
        return clazz.getName().substring(clazz.getPackage().getName().length() + 1).replace('$', '.');
    }
}
