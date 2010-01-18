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
 * CALDocComment.java
 * Creation date: Aug 12, 2005.
 * By: Joseph Wong
 */
package org.openquark.cal.compiler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openquark.cal.internal.serialization.ModuleSerializationTags;
import org.openquark.cal.internal.serialization.RecordInputStream;
import org.openquark.cal.internal.serialization.RecordOutputStream;
import org.openquark.cal.internal.serialization.RecordInputStream.RecordHeaderInfo;


/**
 * The representation of a CALDoc comment as stored by ModuleTypeInfo objects. A
 * CALDoc comment consists of a description block followed by a list of tagged
 * blocks.
 * <p>
 * This class is intended to be constructible only by other classes within this
 * package. Clients should not be able to create instances of this class.
 * Therefore, the compiler can guarantee that a CALDocComment object represents
 * a syntactically and semantically correct CALDoc comment.
 * <p>
 * This class is intended to be immutable, so that objects of this class can be
 * safely returned to clients without worrying about the possibility of these
 * objects being modified.
 * 
 * @author Joseph Wong
 */
public final class CALDocComment {

    /** The serialization schema for CALDoc comments. */
    private static final int serializationSchema = 0; 
    
    /** The summary of the comment. */
    private final TextBlock summary;
    
    /** The description block at the start of the comment. */
    private final TextBlock descriptionBlock;
    
    /** The "@author" blocks in the comment. The array is empty if there are none of these blocks. */
    private final TextBlock[] authorBlocks;
    
    /** The "@version" block in the comment, or null if there is none. */
    private final TextBlock versionBlock;
    
    /** The "@deprecated" block in the comment, or null if there is none. */
    private final TextBlock deprecatedBlock;
    
    /** The "@arg" blocks in the comment. The array is empty if there are none of these blocks. */
    private final ArgBlock[] argBlocks;
    
    /** The "@return" block in the comment, or null if there is none. */
    private final TextBlock returnBlock;
    
    /** The "@see" module references in the comment. The array is empty if there are none of these references. */
    private final ModuleReference[] moduleRefs;
    
    /** The "@see" function and class method references in the comment. The array is empty if there are none of these references. */
    private final ScopedEntityReference[] functionOrClassMethodRefs;
    
    /** The "@see" type constructor references in the comment. The array is empty if there are none of these references. */
    private final ScopedEntityReference[] typeConsRefs;
    
    /** The "@see" data constructor references in the comment. The array is empty if there are none of these references. */
    private final ScopedEntityReference[] dataConsRefs;
    
    /** The "@see" type class references in the comment. The array is empty if there are none of these references. */
    private final ScopedEntityReference[] typeClassRefs;
    
    public static final TextBlock[] NO_TEXT_BLOCKS = new TextBlock[0];
    public static final ArgBlock[] NO_ARG_BLOCKS = new ArgBlock[0];
    public static final ModuleReference[] NO_MODULE_REFS = new ModuleReference[0];
    public static final ScopedEntityReference[] NO_SCOPED_ENTITY_REFS = new ScopedEntityReference[0];
    
    /** The number of spaces per indent in the string generated by the toString() methods. */
    private static final int INDENT_LEVEL = 2;

    /**
     * Represents a reference in a list of references in a CALDoc "@see" block.
     * A reference can either be checked or unchecked. An unchecked reference is
     * represented in source by surrounding the name in double quotes.
     * 
     * @author Joseph Wong
     */
    public static abstract class Reference {
        
        /** Indicates whether the reference is to be statically checked and resolved during compilation. */
        private final boolean checked;
        
        /**
         * How the module name portion of the reference appears in source.
         * Could be the empty string if the reference is unqualified in source.
         */
        private final String moduleNameInSource;
        
        /**
         * Private constructor for this base class for a reference in a list of
         * references in an "@see" block. Intended to be invoked only by subclass
         * constructors.
         * 
         * @param checked
         *          whether the reference is to be statically checked and
         *          resolved during compilation.
         * @param moduleNameInSource how the module name portion of the reference appears in source. Could be the empty string if the reference is unqualified in source.
         */
        private Reference(final boolean checked, final String moduleNameInSource) {
            this.checked = checked;
            
            verifyArg(moduleNameInSource, "moduleNameInSource");
            this.moduleNameInSource = moduleNameInSource;
        }
        
        /**
         * @return true if the reference is to be statically checked and resolved during compilation; false otherwise. 
         */
        public boolean isChecked() {
            return checked;
        }
        
        /**
         * @return how the module name portion of the reference appears in source.
         *         Could be the empty string if the reference is unqualified in source.
         */
        public String getModuleNameInSource() {
            return moduleNameInSource;
        }
    }
    
    /**
     * Represents a (checked/unchecked) module name reference in a CALDoc "@see module = ..." block.
     *
     * @author Joseph Wong
     */
    public static final class ModuleReference extends Reference {
        
        /** The module name encapsulated by this reference. */
        private final ModuleName name;
        
        /**
         * Creates a representation of a module name reference in a CALDoc "@see module = ..." block.
         * 
         * @param name the module name.
         * @param checked whether the name is to be checked and resolved statically during compilation.
         * @param moduleNameInSource how the module name portion of the reference appears in source. Could be the empty string if the reference is unqualified in source.
         */
        ModuleReference(final ModuleName name, final boolean checked, final String moduleNameInSource) {
            super(checked, moduleNameInSource);
            
            verifyArg(name, "name");
            this.name = name;
        }
        
        /**
         * @return the module name encapsulated by this reference.
         */
        public ModuleName getName() {
            return name;
        }
        
        /** @return a string representation of this instance. */
        @Override
        public String toString() {
            return "[ModuleReference " + name + " (" + (isChecked() ? "checked" : "unchecked") + ", module name '" + getModuleNameInSource() + "' in source)]";
        }
        
        /**
         * Write this instance of ModuleReference to the RecordOutputStream.
         * @param s the RecordOutputStream to be written to.
         */
        void write(final RecordOutputStream s) throws IOException {
            s.writeModuleName(name);
            s.writeBoolean(isChecked());
            s.writeUTF(getModuleNameInSource());
        }
        
        /**
         * Load an instance of ModuleReference from the RecordInputStream.
         * @param s the RecordInputStream to be read from.
         * @return a ModuleReference instance deserialized from the stream.
         */
        static ModuleReference load(final RecordInputStream s) throws IOException {
            final ModuleName name = s.readModuleName();
            final boolean checked = s.readBoolean();
            final String moduleNameInSource = s.readUTF();
            return new ModuleReference(name, checked, moduleNameInSource);
        }
    }
    
    /**
     * Represents a (checked/unchecked) scoped entity name reference in a CALDoc "@see" block.
     *
     * @author Joseph Wong
     */
    public static final class ScopedEntityReference extends Reference {
        
        /** The qualified name encapsulated by this reference. */
        private final QualifiedName name;
        
        /**
         * Creates a representation of a scoped entity name reference in a CALDoc "@see" block.
         * 
         * @param name the name of the scoped entity.
         * @param checked whether the name is to be checked and resolved statically during compilation.
         * @param moduleNameInSource how the module name portion of the reference appears in source. Could be the empty string if the reference is unqualified in source.
         */
        ScopedEntityReference(final QualifiedName name, final boolean checked, final String moduleNameInSource) {
            super(checked, moduleNameInSource);
            
            verifyArg(name, "name");
            this.name = name;
        }
        
        /**
         * @return the qualified name encapsulated by this reference.
         */
        public QualifiedName getName() {
            return name;
        }
        
        /** @return a string representation of this instance. */
        @Override
        public String toString() {
            return "[ScopedEntityReference " + name + " (" + (isChecked() ? "checked" : "unchecked") + ", module name '" + getModuleNameInSource() + "' in source)]";
        }
        
        /**
         * Write this instance of ScopedEntityReference to the RecordOutputStream.
         * @param s the RecordOutputStream to be written to.
         */
        void write(final RecordOutputStream s) throws IOException {
            s.writeQualifiedName(name);
            s.writeBoolean(isChecked());
            s.writeUTF(getModuleNameInSource());
        }
        
        /**
         * Load an instance of ScopedEntityReference from the RecordInputStream.
         * @param s the RecordInputStream to be read from.
         * @return a ScopedEntityReference instance deserialized from the stream.
         */
        static ScopedEntityReference load(final RecordInputStream s) throws IOException {
            final QualifiedName name = s.readQualifiedName();
            final boolean checked = s.readBoolean();
            final String moduleNameInSource = s.readUTF();
            return new ScopedEntityReference(name, checked, moduleNameInSource);
        }
    }

    ////=============
    /// Paragraphs
    //
    
    /**
     * Represents a paragraph within a CALDoc comment. A paragraph is simply a
     * block of text that is to be separated from the text that comes before and
     * after it with paragraph breaks.
     * 
     * @author Joseph Wong
     */
    public static abstract class Paragraph {
        
        /**
         * The array of possible record tags used in calls to {@link RecordInputStream#findRecord(short[])} by
         * the {@link #loadWithRecordTag} method.
         */
        private static final short[] PARAGRAPH_RECORD_TAGS = new short[] {
            ModuleSerializationTags.CALDOC_TEXT_PARAGRAPH,
            ModuleSerializationTags.CALDOC_LIST_PARAGRAPH
        };

        /** Private constructor to be called by subclasses only. */
        private Paragraph() {}
        
        /**
         * Accepts the visitation of a visitor, which implements the
         * CALDocCommentTextBlockVisitor interface. This abstract method is to be overridden
         * by each concrete subclass so that the correct visit method on the
         * visitor may be called based upon the type of the element being
         * visited.
         * <p>
         * 
         * As the CALDocCommentTextBlockVisitor follows a more general visitor pattern
         * where arguments can be passed into the visit methods and return
         * values obtained from them, this method passes through the argument
         * into the visit method, and returns as its return value the return
         * value of the visit method.
         * <p>
         * 
         * Nonetheless, for a significant portion of the common cases, the state of the
         * visitation can simply be kept as member variables within the visitor itself,
         * thereby eliminating the need to use the argument and return value of the
         * visit methods. In these scenarios, the recommended approach is to use
         * {@link Void} as the type argument for both <code>T</code> and <code>R</code>, and
         * pass in null as the argument, and return null as the return value.
         * <p>
         * 
         * @param <T> the argument type. If the visitation argument is not used, specify {@link Void}.
         * @param <R> the return type. If the return value is not used, specify {@link Void}.
         * 
         * @param visitor
         *            the visitor.
         * @param arg
         *            the argument to be passed to the visitor's visitXXX method.
         * @return the return value of the visitor's visitXXX method.
         */
        public abstract <T, R> R accept(CALDocCommentTextBlockVisitor<T, R> visitor, T arg);
        
        /** @return a string representation of this instance. */
        @Override
        public final String toString() {
            final StringBuilder result = new StringBuilder();
            toStringBuilder(result, 0);
            return result.toString();
        }
        
        /**
         * Fills the given StringBuilder with a string representation of this instance.
         * @param result the StringBuilder to fill.
         * @param indentLevel the indent level to use in indenting the generated text.
         */
        public abstract void toStringBuilder(StringBuilder result, int indentLevel);
        
        /**
         * Write this instance to the RecordOutputStream.
         * @param s the RecordOutputStream to be written to.
         */
        abstract void writeWithRecordTag(RecordOutputStream s) throws IOException;
        
        /**
         * Load an instance of Paragraph from the RecordInputStream.
         * @param s the RecordInputStream to be read from.
         * @return a Paragraph instance deserialized from the stream.
         */
        static Paragraph loadWithRecordTag(final RecordInputStream s) throws IOException {
            final short[] paragraphRecordTags = PARAGRAPH_RECORD_TAGS;
            
            final RecordHeaderInfo rhi = s.findRecord(paragraphRecordTags);
            if (rhi == null) {
                throw new IOException("Unable to find Paragraph record header.");
            }
            
            Paragraph paragraph;
            
            switch (rhi.getRecordTag()) {
            case ModuleSerializationTags.CALDOC_TEXT_PARAGRAPH:
                paragraph = TextParagraph.load(s);
                break;
            
            case ModuleSerializationTags.CALDOC_LIST_PARAGRAPH:
                paragraph = ListParagraph.load(s);
                break;
            
            default:
                throw new IOException("Unrecognized record tag of " + rhi.getRecordTag() + " for Paragraph.");
            }
            
            s.skipRestOfRecord();
            return paragraph;
        }
    }
    
    /**
     * Represents a text paragraph within a CALDoc comment, i.e. a simple paragraph of text.
     *
     * @author Joseph Wong
     */
    public static final class TextParagraph extends Paragraph {
        
        /**
         * The segments which constitute this paragraph.
         */
        private final Segment[] segments;
        public static final Segment[] NO_SEGMENTS = new Segment[0];
        
        /**
         * Creates a representation of a text paragraph in a CALDoc comment.
         * 
         * @param segments the segments which constitute this paragraph.
         */
        TextParagraph(final Segment[] segments) {
            
            verifyArrayArg(segments, "segments");
            if (segments.length == 0) {
                this.segments = NO_SEGMENTS;
            } else {
                this.segments = segments.clone();
            }
        }
        
        /**
         * @return the number of segments in this paragraph.
         */
        public int getNSegments() {
            return segments.length;
        }
        
        /**
         * Returns the segment at the specified position in this paragraph.
         * @param index the index of the segment to return.
         * @return the segment at the specified position in this paragraph.
         */
        public Segment getNthSegment(final int index) {
            return segments[index];
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public <T, R> R accept(final CALDocCommentTextBlockVisitor<T, R> visitor, final T arg) {
            return visitor.visitTextParagraph(this, arg);
        }
        
        /**
         * Fills the given StringBuilder with a string representation of this instance.
         * @param result the StringBuilder to fill.
         * @param indentLevel the indent level to use in indenting the generated text.
         */
        @Override
        public void toStringBuilder(final StringBuilder result, final int indentLevel) {
            addSpacesToStringBuilder(result, indentLevel);
            result.append("[TextParagraph\n");
            
            for (final Segment segment : segments) {
                segment.toStringBuilder(result, indentLevel + INDENT_LEVEL);
            }
            
            addSpacesToStringBuilder(result, indentLevel);
            result.append("]\n");
        }
        
        /**
         * Write this instance of TextParagraph to the RecordOutputStream.
         * @param s the RecordOutputStream to be written to.
         */
        @Override
        void writeWithRecordTag(final RecordOutputStream s) throws IOException {
            s.startRecord(ModuleSerializationTags.CALDOC_TEXT_PARAGRAPH, serializationSchema);
            write(s);
            s.endRecord();
        }
        
        /**
         * Write this instance of TextParagraph to the RecordOutputStream.
         * @param s the RecordOutputStream to be written to.
         */
        void write(final RecordOutputStream s) throws IOException {
            s.writeIntCompressed(segments.length);
            for (final Segment segment : segments) {
                segment.writeWithRecordTag(s);
            }
        }
        
        /**
         * Load an instance of TextParagraph from the RecordInputStream.
         * @param s the RecordInputStream to be read from.
         * @return a TextParagraph instance deserialized from the stream.
         */
        static TextParagraph load(final RecordInputStream s) throws IOException {
            final int nSegments = s.readIntCompressed();
            
            final Segment[] segments = new Segment[nSegments];
            for (int i = 0; i < nSegments; i++) {
                segments[i] = Segment.loadWithRecordTag(s);
            }
            
            return new TextParagraph(segments);
        }
    }
    
    /**
     * Represents a list (either ordered or unordered) within a CALDoc comment.
     *
     * @author Joseph Wong
     */
    public static final class ListParagraph extends Paragraph {
        
        /**
         * Whether the list is ordered or unordered.
         */
        private final boolean isOrdered;
        
        /**
         * The list items which constitute this list.
         */
        private final ListItem[] items;
        public static final ListItem[] NO_ITEMS = new ListItem[0];
        
        /**
         * Creates a representation of a list in a CALDoc comment.
         * 
         * @param isOrdered whether the list is ordered or unordered.
         * @param items the list items which constitute this list.
         */
        ListParagraph(final boolean isOrdered, final ListItem[] items) {
            this.isOrdered = isOrdered;
            
            verifyArrayArg(items, "items");
            if (items.length == 0) {
                this.items = NO_ITEMS;
            } else {
                this.items = items; // no cloning because this constructor is package-scoped, and we count on the caller to have done the appropriate cloning already
            }
        }
        
        /**
         * @return whether the list is ordered or unordered.
         */
        public boolean isOrdered() {
            return isOrdered;
        }
        
        /**
         * @return the number of items in this list.
         */
        public int getNItems() {
            return items.length;
        }
        
        /**
         * Returns the item at the specified position in this list.
         * @param index the index of the item to return.
         * @return the item at the specified position in this list.
         */
        public ListItem getNthItem(final int index) {
            return items[index];
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public <T, R> R accept(final CALDocCommentTextBlockVisitor<T, R> visitor, final T arg) {
            return visitor.visitListParagraph(this, arg);
        }
        
        /**
         * Fills the given StringBuilder with a string representation of this instance.
         * @param result the StringBuilder to fill.
         * @param indentLevel the indent level to use in indenting the generated text.
         */
        @Override
        public void toStringBuilder(final StringBuilder result, final int indentLevel) {
            addSpacesToStringBuilder(result, indentLevel);
            if (isOrdered) {
                result.append("[Ordered ListParagraph\n");
            } else {
                result.append("[Unordered ListParagraph\n");
            }
            
            for (final ListItem item : items) {
                item.toStringBuilder(result, indentLevel + INDENT_LEVEL);
            }
            
            addSpacesToStringBuilder(result, indentLevel);
            result.append("]\n");
        }
        
        /**
         * Write this instance of ListParagraph to the RecordOutputStream.
         * @param s the RecordOutputStream to be written to.
         */
        @Override
        void writeWithRecordTag(final RecordOutputStream s) throws IOException {
            s.startRecord(ModuleSerializationTags.CALDOC_LIST_PARAGRAPH, serializationSchema);
            
            s.writeBoolean(isOrdered);
            
            s.writeIntCompressed(items.length);
            for (final ListItem item : items) {
                item.write(s);
            }
            
            s.endRecord();
        }
        
        /**
         * Load an instance of ListParagraph from the RecordInputStream.
         * @param s the RecordInputStream to be read from.
         * @return a ListParagraph instance deserialized from the stream.
         */
        static ListParagraph load(final RecordInputStream s) throws IOException {
            final boolean isOrdered = s.readBoolean();
            
            final int nItems = s.readIntCompressed();
            
            final ListItem[] items = new ListItem[nItems];
            for (int i = 0; i < nItems; i++) {
                items[i] = ListItem.load(s);
            }
            
            return new ListParagraph(isOrdered, items);
        }
    }
    
    /**
     * Represents a list item within a CALDoc comment, either in a ordered list or
     * an unordered list.
     *
     * @author Joseph Wong
     */
    public static final class ListItem {
        
        /**
         * The text block which constitute the content of this list item.
         */
        private final TextBlock content;
        
        /**
         * Creates a representation of a list item in a CALDoc comment.
         * 
         * @param content the text block which constitute the content of this list item.
         */
        ListItem(final TextBlock content) {
            verifyArg(content, "content");
            this.content = content;
        }
        
        /**
         * @return the text block which constitute the content of this list item.
         */
        public TextBlock getContent() {
            return content;
        }
        
        /**
         * Accepts the visitation of a visitor, which implements the
         * CALDocCommentTextBlockVisitor interface.
         * <p>
         * 
         * As the CALDocCommentTextBlockVisitor follows a more general visitor pattern
         * where arguments can be passed into the visit methods and return
         * values obtained from them, this method passes through the argument
         * into the visit method, and returns as its return value the return
         * value of the visit method.
         * <p>
         * 
         * Nonetheless, for a significant portion of the common cases, the state of the
         * visitation can simply be kept as member variables within the visitor itself,
         * thereby eliminating the need to use the argument and return value of the
         * visit methods. In these scenarios, the recommended approach is to use
         * {@link Void} as the type argument for both <code>T</code> and <code>R</code>, and
         * pass in null as the argument, and return null as the return value.
         * <p>
         * 
         * @param <T> the argument type. If the visitation argument is not used, specify {@link Void}.
         * @param <R> the return type. If the return value is not used, specify {@link Void}.
         * 
         * @param visitor
         *            the visitor.
         * @param arg
         *            the argument to be passed to the visitor's visitListItem method.
         * @return the return value of the visitor's visitListItem method.
         */
        public <T, R> R accept(final CALDocCommentTextBlockVisitor<T, R> visitor, final T arg) {
            return visitor.visitListItem(this, arg);
        }
        
        /** @return a string representation of this instance. */
        @Override
        public final String toString() {
            final StringBuilder result = new StringBuilder();
            toStringBuilder(result, 0);
            return result.toString();
        }
        
        /**
         * Fills the given StringBuilder with a string representation of this instance.
         * @param result the StringBuilder to fill.
         * @param indentLevel the indent level to use in indenting the generated text.
         */
        public void toStringBuilder(final StringBuilder result, final int indentLevel) {
            addSpacesToStringBuilder(result, indentLevel);
            result.append("[ListItem\n");
            
            content.toStringBuilder(result, indentLevel + INDENT_LEVEL);
            
            addSpacesToStringBuilder(result, indentLevel);
            result.append("]\n");
        }
        
        /**
         * Write this instance of ListItem to the RecordOutputStream.
         * @param s the RecordOutputStream to be written to.
         */
        void write(final RecordOutputStream s) throws IOException {
            content.write(s);
        }
        
        /**
         * Load an instance of ListItem from the RecordInputStream.
         * @param s the RecordInputStream to be read from.
         * @return a ListItem instance deserialized from the stream.
         */
        static ListItem load(final RecordInputStream s) throws IOException {
            final TextBlock content = TextBlock.load(s);
            return new ListItem(content);
        }
    }
    
    ////=============
    /// Segments
    //
    
    /**
     * Represents a text segment in a CALDoc comment. A segment is simply
     * a unit of text, and a text paragraph is represented as a collection
     * of such segments.
     *
     * @author Joseph Wong
     */
    public static abstract class Segment {
        
        /**
         * The array of possible record tags used in calls to {@link RecordInputStream#findRecord(short[])} by
         * the {@link #loadWithRecordTag} method.
         */
        private static final short[] SEGMENT_RECORD_TAGS = new short[] {
            ModuleSerializationTags.CALDOC_PLAIN_TEXT_SEGMENT,
            ModuleSerializationTags.CALDOC_URL_SEGMENT,
            ModuleSerializationTags.CALDOC_MODULE_LINK_SEGMENT,
            ModuleSerializationTags.CALDOC_FUNCTION_OR_CLASS_METHOD_LINK_SEGMENT,
            ModuleSerializationTags.CALDOC_TYPE_CONS_LINK_SEGMENT,
            ModuleSerializationTags.CALDOC_DATA_CONS_LINK_SEGMENT,
            ModuleSerializationTags.CALDOC_TYPE_CLASS_LINK_SEGMENT,
            ModuleSerializationTags.CALDOC_CODE_SEGMENT,
            ModuleSerializationTags.CALDOC_EMPHASIZED_SEGMENT,
            ModuleSerializationTags.CALDOC_STRONGLY_EMPHASIZED_SEGMENT,
            ModuleSerializationTags.CALDOC_SUPERSCRIPT_SEGMENT,
            ModuleSerializationTags.CALDOC_SUBSCRIPT_SEGMENT
        };

        /** Private constructor to be called by subclasses only. */
        private Segment() {}
        
        /**
         * Accepts the visitation of a visitor, which implements the
         * CALDocCommentTextBlockVisitor interface. This abstract method is to be overridden
         * by each concrete subclass so that the correct visit method on the
         * visitor may be called based upon the type of the element being
         * visited.
         * <p>
         * 
         * As the CALDocCommentTextBlockVisitor follows a more general visitor pattern
         * where arguments can be passed into the visit methods and return
         * values obtained from them, this method passes through the argument
         * into the visit method, and returns as its return value the return
         * value of the visit method.
         * <p>
         * 
         * Nonetheless, for a significant portion of the common cases, the state of the
         * visitation can simply be kept as member variables within the visitor itself,
         * thereby eliminating the need to use the argument and return value of the
         * visit methods. In these scenarios, the recommended approach is to use
         * {@link Void} as the type argument for both <code>T</code> and <code>R</code>, and
         * pass in null as the argument, and return null as the return value.
         * <p>
         * 
         * @param <T> the argument type. If the visitation argument is not used, specify {@link Void}.
         * @param <R> the return type. If the return value is not used, specify {@link Void}.
         * 
         * @param visitor
         *            the visitor.
         * @param arg
         *            the argument to be passed to the visitor's visitXXX method.
         * @return the return value of the visitor's visitXXX method.
         */
        public abstract <T, R> R accept(CALDocCommentTextBlockVisitor<T, R> visitor, T arg);
        
        /** @return a string representation of this instance. */
        @Override
        public final String toString() {
            final StringBuilder result = new StringBuilder();
            toStringBuilder(result, 0);
            return result.toString();
        }
        
        /**
         * Fills the given StringBuilder with a string representation of this instance.
         * @param result the StringBuilder to fill.
         * @param indentLevel the indent level to use in indenting the generated text.
         */
        public abstract void toStringBuilder(StringBuilder result, int indentLevel);
        
        /**
         * Unescapes the given text that appears in the source of a CALDoc comment.
         * @param text the text to be unescaped.
         * @return the unescaped text.
         */
        static String caldocUnescape(String text) {
            text = text.replaceAll("\\\\\\{@", "{@"); // '\{@' -> '{@'
            text = text.replaceAll("\\\\@", "@");     // '\@'  -> '@'

            return text;
        }
        
        /**
         * Write this instance to the RecordOutputStream.
         * @param s the RecordOutputStream to be written to.
         */
        abstract void writeWithRecordTag(RecordOutputStream s) throws IOException;
        
        /**
         * Load an instance of Segment from the RecordInputStream.
         * @param s the RecordInputStream to be read from.
         * @return a Segment instance deserialized from the stream.
         */
        static Segment loadWithRecordTag(final RecordInputStream s) throws IOException {
            final short[] paragraphRecordTags = SEGMENT_RECORD_TAGS;
            
            final RecordHeaderInfo rhi = s.findRecord(paragraphRecordTags);
            if (rhi == null) {
                throw new IOException("Unable to find Segment record header.");
            }
            
            Segment segment;
            
            switch (rhi.getRecordTag()) {
            case ModuleSerializationTags.CALDOC_PLAIN_TEXT_SEGMENT:
                segment = PlainTextSegment.load(s);
                break;
                
            case ModuleSerializationTags.CALDOC_URL_SEGMENT:
                segment = URLSegment.load(s);
                break;
                
            case ModuleSerializationTags.CALDOC_MODULE_LINK_SEGMENT:
                segment = ModuleLinkSegment.load(s);
                break;
                
            case ModuleSerializationTags.CALDOC_FUNCTION_OR_CLASS_METHOD_LINK_SEGMENT:
                segment = FunctionOrClassMethodLinkSegment.load(s);
                break;
                
            case ModuleSerializationTags.CALDOC_TYPE_CONS_LINK_SEGMENT:
                segment = TypeConsLinkSegment.load(s);
                break;
                
            case ModuleSerializationTags.CALDOC_DATA_CONS_LINK_SEGMENT:
                segment = DataConsLinkSegment.load(s);
                break;
                
            case ModuleSerializationTags.CALDOC_TYPE_CLASS_LINK_SEGMENT:
                segment = TypeClassLinkSegment.load(s);
                break;
                
            case ModuleSerializationTags.CALDOC_CODE_SEGMENT:
                segment = CodeSegment.load(s);
                break;
                
            case ModuleSerializationTags.CALDOC_EMPHASIZED_SEGMENT:
                segment = EmphasizedSegment.load(s);
                break;
                
            case ModuleSerializationTags.CALDOC_STRONGLY_EMPHASIZED_SEGMENT:
                segment = StronglyEmphasizedSegment.load(s);
                break;
                
            case ModuleSerializationTags.CALDOC_SUPERSCRIPT_SEGMENT:
                segment = SuperscriptSegment.load(s);
                break;
                
            case ModuleSerializationTags.CALDOC_SUBSCRIPT_SEGMENT:
                segment = SubscriptSegment.load(s);
                break;
                
            default:
                throw new IOException("Unrecognized record tag of " + rhi.getRecordTag() + " for Segment.");
            }
            
            s.skipRestOfRecord();
            return segment;
        }
    }
    
    /**
     * Represents a simple piece of text in a CALDoc comment.
     *
     * @author Joseph Wong
     */
    public static final class PlainTextSegment extends Segment {
        
        /**
         * The text encapsulated by this instance.
         */
        private final String text;
        
        /**
         * Creates a representation of a simple piece of text in a CALDoc comment.
         * 
         * @param text the text encapsulated by this instance.
         */
        PlainTextSegment(final String text) {
            verifyArg(text, "text");
            this.text = text;
        }
        
        /**
         * Factory method for constructing a representation of a simple piece of text in a CALDoc comment
         * with a piece of escaped text (as in the format used in the source code).
         * 
         * @param escapedText the escaped text.
         * @return a new instance of this class.
         */
        static PlainTextSegment makeWithEscapedText(final String escapedText) {
            return new PlainTextSegment(caldocUnescape(escapedText));
        }
        
        /**
         * @return the text encapsulated by this instance.
         */
        public String getText() {
            return text;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public <T, R> R accept(final CALDocCommentTextBlockVisitor<T, R> visitor, final T arg) {
            return visitor.visitPlainTextSegment(this, arg);
        }
        
        /**
         * Fills the given StringBuilder with a string representation of this instance.
         * @param result the StringBuilder to fill.
         * @param indentLevel the indent level to use in indenting the generated text.
         */
        @Override
        public void toStringBuilder(final StringBuilder result, final int indentLevel) {
            addSpacesToStringBuilder(result, indentLevel);
            result.append("[PlainTextSegment\n");
            
            addSpacesToStringBuilder(result, indentLevel + INDENT_LEVEL - 1);
            result.append('\"');
            result.append(text.replaceAll("(\r\n|\n|\r)", "\n" + spaces(indentLevel + INDENT_LEVEL))).append("\"\n");
            
            addSpacesToStringBuilder(result, indentLevel);
            result.append("]\n");
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        void writeWithRecordTag(final RecordOutputStream s) throws IOException {
            s.startRecord(ModuleSerializationTags.CALDOC_PLAIN_TEXT_SEGMENT, serializationSchema);
            s.writeUTF(text);
            s.endRecord();
        }
        
        /**
         * Load an instance of PlainTextSegment from the RecordInputStream.
         * @param s the RecordInputStream to be read from.
         * @return a PlainTextSegment instance deserialized from the stream.
         */
        static PlainTextSegment load(final RecordInputStream s) throws IOException {
            final String text = s.readUTF();
            return new PlainTextSegment(text);
        }
    }
    
    /**
     * Represents an inline tag segment in a CALDoc comment, i.e. segments appearing
     * in the source code as "{at-tagName ...at-}". 
     *
     * @author Joseph Wong
     */
    public static abstract class InlineTagSegment extends Segment {
        
        /** Private constructor to be called by subclasses only. */
        private InlineTagSegment() {}
    }
    
    /**
     * Represents a hyperlinkable URL in a CALDoc comment.
     *
     * @author Joseph Wong
     */
    public static final class URLSegment extends InlineTagSegment {
        
        /**
         * The URL encapsulated by this instance.
         */
        private final String url;
        
        /**
         * Creates a representation of a hyperlinkable URL in a CALDoc comment.
         * 
         * @param url the URL encapsulated by this instance.
         */
        URLSegment(final String url) {
            verifyArg(url, "url");
            this.url = url;
        }
        
        /**
         * Factory method for constructing a representation of a hyperlinkable URL in a CALDoc comment
         * with a piece of escaped text (as in the format used in the source code).
         * 
         * @param escapedText the escaped text.
         * @return a new instance of this class.
         */
        static URLSegment makeWithEscapedText(final String escapedText) {
            return new URLSegment(caldocUnescape(escapedText).trim()); // trim any excess whitespace
        }
        
        /**
         * @return the URL encapsulated by this instance.
         */
        public String getURL() {
            return url;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public <T, R> R accept(final CALDocCommentTextBlockVisitor<T, R> visitor, final T arg) {
            return visitor.visitURLSegment(this, arg);
        }
        
        /**
         * Fills the given StringBuilder with a string representation of this instance.
         * @param result the StringBuilder to fill.
         * @param indentLevel the indent level to use in indenting the generated text.
         */
        @Override
        public void toStringBuilder(final StringBuilder result, final int indentLevel) {
            addSpacesToStringBuilder(result, indentLevel);
            result.append("[URLSegment ").append(url).append("]\n");
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        void writeWithRecordTag(final RecordOutputStream s) throws IOException {
            s.startRecord(ModuleSerializationTags.CALDOC_URL_SEGMENT, serializationSchema);
            s.writeUTF(url);
            s.endRecord();
        }
        
        /**
         * Load an instance of URLSegment from the RecordInputStream.
         * @param s the RecordInputStream to be read from.
         * @return a URLSegment instance deserialized from the stream.
         */
        static URLSegment load(final RecordInputStream s) throws IOException {
            final String url = s.readUTF();
            return new URLSegment(url);
        }
    }
    
    /**
     * Represents an inline, hyperlinkable cross-reference in a CALDoc comment.
     *
     * @author Joseph Wong
     */
    public static abstract class LinkSegment extends InlineTagSegment {
        
        /** Private constructor to be called by subclasses only. */
        private LinkSegment() {}
    }
    
    /**
     * Represents an inline, hyperlinkable cross-reference to a module in a CALDoc comment.
     *
     * @author Joseph Wong
     */
    public static final class ModuleLinkSegment extends LinkSegment {
        
        /**
         * The cross-reference encapsulated by this link segment.
         */
        private final ModuleReference reference;
        
        /**
         * Creates a representation of an inline, hyperlinkable cross-reference to a module in a CALDoc comment.
         * 
         * @param reference the cross-reference encapsulated by this link segment.
         */
        ModuleLinkSegment(final ModuleReference reference) {
            verifyArg(reference, "reference");
            this.reference = reference;
        }
        
        /**
         * @return the cross-reference encapsulated by this link segment.
         */
        public ModuleReference getReference() {
            return reference;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public <T, R> R accept(final CALDocCommentTextBlockVisitor<T, R> visitor, final T arg) {
            return visitor.visitModuleLinkSegment(this, arg);
        }
        
        /**
         * Fills the given StringBuilder with a string representation of this instance.
         * @param result the StringBuilder to fill.
         * @param indentLevel the indent level to use in indenting the generated text.
         */
        @Override
        public void toStringBuilder(final StringBuilder result, final int indentLevel) {
            addSpacesToStringBuilder(result, indentLevel);
            result.append("[ModuleLinkSegment ").append(reference).append("]\n");
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        void writeWithRecordTag(final RecordOutputStream s) throws IOException {
            s.startRecord(ModuleSerializationTags.CALDOC_MODULE_LINK_SEGMENT, serializationSchema);
            reference.write(s);
            s.endRecord();
        }
        
        /**
         * Load an instance of ModuleLinkSegment from the RecordInputStream.
         * @param s the RecordInputStream to be read from.
         * @return a ModuleLinkSegment instance deserialized from the stream.
         */
        static ModuleLinkSegment load(final RecordInputStream s) throws IOException {
            final ModuleReference reference = ModuleReference.load(s);
            return new ModuleLinkSegment(reference);
        }
    }
    
    /**
     * Represents an inline, hyperlinkable cross-reference to a scoped entity in a CALDoc comment.
     *
     * @author Joseph Wong
     */
    public static abstract class ScopedEntityLinkSegment extends LinkSegment {
        
        /**
         * The cross-reference encapsulated by this link segment.
         */
        private final ScopedEntityReference reference;
        
        /**
         * Private constructor to be called by subclasses only.
         * @param reference the cross-reference encapsulated by this link segment.
         */
        private ScopedEntityLinkSegment(final ScopedEntityReference reference) {
            verifyArg(reference, "reference");
            this.reference = reference;
        }
        
        /**
         * @return the cross-reference encapsulated by this link segment.
         */
        public ScopedEntityReference getReference() {
            return reference;
        }
    }
    
    /**
     * Represents an inline, hyperlinkable cross-reference to a function or class method in a CALDoc comment.
     *
     * @author Joseph Wong
     */
    public static final class FunctionOrClassMethodLinkSegment extends ScopedEntityLinkSegment {
        
        /**
         * Creates a representation of an inline, hyperlinkable cross-reference to a function or class method in a CALDoc comment.
         * 
         * @param reference the cross-reference encapsulated by this link segment.
         */
        FunctionOrClassMethodLinkSegment(final ScopedEntityReference reference) {
            super(reference);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public <T, R> R accept(final CALDocCommentTextBlockVisitor<T, R> visitor, final T arg) {
            return visitor.visitFunctionOrClassMethodLinkSegment(this, arg);
        }
        
        /**
         * Fills the given StringBuilder with a string representation of this instance.
         * @param result the StringBuilder to fill.
         * @param indentLevel the indent level to use in indenting the generated text.
         */
        @Override
        public void toStringBuilder(final StringBuilder result, final int indentLevel) {
            addSpacesToStringBuilder(result, indentLevel);
            result.append("[FunctionOrClassMethodLinkSegment ").append(getReference()).append("]\n");
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        void writeWithRecordTag(final RecordOutputStream s) throws IOException {
            s.startRecord(ModuleSerializationTags.CALDOC_FUNCTION_OR_CLASS_METHOD_LINK_SEGMENT, serializationSchema);
            getReference().write(s);
            s.endRecord();
        }
        
        /**
         * Load an instance of FunctionOrClassMethodLinkSegment from the RecordInputStream.
         * @param s the RecordInputStream to be read from.
         * @return a FunctionOrClassMethodLinkSegment instance deserialized from the stream.
         */
        static FunctionOrClassMethodLinkSegment load(final RecordInputStream s) throws IOException {
            final ScopedEntityReference reference = ScopedEntityReference.load(s);
            return new FunctionOrClassMethodLinkSegment(reference);
        }
    }
    
    /**
     * Represents an inline, hyperlinkable cross-reference to a type constructor in a CALDoc comment.
     *
     * @author Joseph Wong
     */
    public static final class TypeConsLinkSegment extends ScopedEntityLinkSegment {
        
        /**
         * Creates a representation of an inline, hyperlinkable cross-reference to a type constructor in a CALDoc comment.
         * 
         * @param reference the cross-reference encapsulated by this link segment.
         */
        TypeConsLinkSegment(final ScopedEntityReference reference) {
            super(reference);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public <T, R> R accept(final CALDocCommentTextBlockVisitor<T, R> visitor, final T arg) {
            return visitor.visitTypeConsLinkSegment(this, arg);
        }
        
        /**
         * Fills the given StringBuilder with a string representation of this instance.
         * @param result the StringBuilder to fill.
         * @param indentLevel the indent level to use in indenting the generated text.
         */
        @Override
        public void toStringBuilder(final StringBuilder result, final int indentLevel) {
            addSpacesToStringBuilder(result, indentLevel);
            result.append("[TypeConsLinkSegment ").append(getReference()).append("]\n");
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        void writeWithRecordTag(final RecordOutputStream s) throws IOException {
            s.startRecord(ModuleSerializationTags.CALDOC_TYPE_CONS_LINK_SEGMENT, serializationSchema);
            getReference().write(s);
            s.endRecord();
        }
        
        /**
         * Load an instance of TypeConsLinkSegment from the RecordInputStream.
         * @param s the RecordInputStream to be read from.
         * @return a TypeConsLinkSegment instance deserialized from the stream.
         */
        static TypeConsLinkSegment load(final RecordInputStream s) throws IOException {
            final ScopedEntityReference reference = ScopedEntityReference.load(s);
            return new TypeConsLinkSegment(reference);
        }
    }
    
    /**
     * Represents an inline, hyperlinkable cross-reference to a data constructor in a CALDoc comment.
     *
     * @author Joseph Wong
     */
    public static final class DataConsLinkSegment extends ScopedEntityLinkSegment {
        
        /**
         * Creates a representation of an inline, hyperlinkable cross-reference to a data constructor in a CALDoc comment.
         * 
         * @param reference the cross-reference encapsulated by this link segment.
         */
        DataConsLinkSegment(final ScopedEntityReference reference) {
            super(reference);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public <T, R> R accept(final CALDocCommentTextBlockVisitor<T, R> visitor, final T arg) {
            return visitor.visitDataConsLinkSegment(this, arg);
        }
        
        /**
         * Fills the given StringBuilder with a string representation of this instance.
         * @param result the StringBuilder to fill.
         * @param indentLevel the indent level to use in indenting the generated text.
         */
        @Override
        public void toStringBuilder(final StringBuilder result, final int indentLevel) {
            addSpacesToStringBuilder(result, indentLevel);
            result.append("[DataConsLinkSegment ").append(getReference()).append("]\n");
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        void writeWithRecordTag(final RecordOutputStream s) throws IOException {
            s.startRecord(ModuleSerializationTags.CALDOC_DATA_CONS_LINK_SEGMENT, serializationSchema);
            getReference().write(s);
            s.endRecord();
        }
        
        /**
         * Load an instance of DataConsLinkSegment from the RecordInputStream.
         * @param s the RecordInputStream to be read from.
         * @return a DataConsLinkSegment instance deserialized from the stream.
         */
        static DataConsLinkSegment load(final RecordInputStream s) throws IOException {
            final ScopedEntityReference reference = ScopedEntityReference.load(s);
            return new DataConsLinkSegment(reference);
        }
    }
    
    /**
     * Represents an inline, hyperlinkable cross-reference to a type class in a CALDoc comment.
     *
     * @author Joseph Wong
     */
    public static final class TypeClassLinkSegment extends ScopedEntityLinkSegment {
        
        /**
         * Creates a representation of an inline, hyperlinkable cross-reference to a type class in a CALDoc comment.
         * 
         * @param reference the cross-reference encapsulated by this link segment.
         */
        TypeClassLinkSegment(final ScopedEntityReference reference) {
            super(reference);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public <T, R> R accept(final CALDocCommentTextBlockVisitor<T, R> visitor, final T arg) {
            return visitor.visitTypeClassLinkSegment(this, arg);
        }
        
        /**
         * Fills the given StringBuilder with a string representation of this instance.
         * @param result the StringBuilder to fill.
         * @param indentLevel the indent level to use in indenting the generated text.
         */
        @Override
        public void toStringBuilder(final StringBuilder result, final int indentLevel) {
            addSpacesToStringBuilder(result, indentLevel);
            result.append("[TypeClassLinkSegment ").append(getReference()).append("]\n");
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        void writeWithRecordTag(final RecordOutputStream s) throws IOException {
            s.startRecord(ModuleSerializationTags.CALDOC_TYPE_CLASS_LINK_SEGMENT, serializationSchema);
            getReference().write(s);
            s.endRecord();
        }
        
        /**
         * Load an instance of TypeClassLinkSegment from the RecordInputStream.
         * @param s the RecordInputStream to be read from.
         * @return a TypeClassLinkSegment instance deserialized from the stream.
         */
        static TypeClassLinkSegment load(final RecordInputStream s) throws IOException {
            final ScopedEntityReference reference = ScopedEntityReference.load(s);
            return new TypeClassLinkSegment(reference);
        }
    }
    
    /**
     * Represents a block of source code in a CALDoc comment. In generating formatted output, the
     * whitespace contained within the text is respected.
     *
     * @author Joseph Wong
     */
    public static final class CodeSegment extends InlineTagSegment {
        
        /**
         * The text of the block of source code.
         */
        private final TextBlock content;
        
        /**
         * Creates a representation of a block of source code in a CALDoc comment.
         * 
         * @param content the text of the block of source code.
         */
        CodeSegment(final TextBlock content) {
            verifyArg(content, "content");
            this.content = content;
        }
        
        /**
         * @return the text of the block of source code.
         */
        public TextBlock getContent() {
            return content;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public <T, R> R accept(final CALDocCommentTextBlockVisitor<T, R> visitor, final T arg) {
            return visitor.visitCodeSegment(this, arg);
        }
        
        /**
         * Fills the given StringBuilder with a string representation of this instance.
         * @param result the StringBuilder to fill.
         * @param indentLevel the indent level to use in indenting the generated text.
         */
        @Override
        public void toStringBuilder(final StringBuilder result, final int indentLevel) {
            addSpacesToStringBuilder(result, indentLevel);
            result.append("[CodeSegment\n");
            
            content.toStringBuilder(result, indentLevel + INDENT_LEVEL);
            
            addSpacesToStringBuilder(result, indentLevel);
            result.append("]\n");
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        void writeWithRecordTag(final RecordOutputStream s) throws IOException {
            s.startRecord(ModuleSerializationTags.CALDOC_CODE_SEGMENT, serializationSchema);
            content.write(s);
            s.endRecord();
        }
        
        /**
         * Load an instance of CodeSegment from the RecordInputStream.
         * @param s the RecordInputStream to be read from.
         * @return a CodeSegment instance deserialized from the stream.
         */
        static CodeSegment load(final RecordInputStream s) throws IOException {
            final TextBlock content = TextBlock.load(s);
            return new CodeSegment(content);
        }
    }
    
    /**
     * Represents a piece of text in a CALDoc comment that has formatting applied to it.
     *
     * @author Joseph Wong
     */
    public static abstract class FormattedSegment extends InlineTagSegment {
        
        /**
         * The content encapsulated by this formatted segment.
         */
        private final TextParagraph content;
        
        /**
         * Private constructor to be called by subclasses only.
         * @param content the content encapsulated by this formatted segment.
         */
        private FormattedSegment(final TextParagraph content) {
            verifyArg(content, "content");
            this.content = content;
        }
        
        /**
         * @return the content encapsulated by this formatted segment.
         */
        public TextParagraph getContent() {
            return content;
        }
    }
    
    /**
     * Represents a piece of text in a CALDoc comment that is emphasized.
     *
     * @author Joseph Wong
     */
    public static final class EmphasizedSegment extends FormattedSegment {
        
        /**
         * Creates a representation of a piece of text in a CALDoc comment that is emphasized.
         * 
         * @param content the content encapsulated by this segment.
         */
        EmphasizedSegment(final TextParagraph content) {
            super(content);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public <T, R> R accept(final CALDocCommentTextBlockVisitor<T, R> visitor, final T arg) {
            return visitor.visitEmphasizedSegment(this, arg);
        }
        
        /**
         * Fills the given StringBuilder with a string representation of this instance.
         * @param result the StringBuilder to fill.
         * @param indentLevel the indent level to use in indenting the generated text.
         */
        @Override
        public void toStringBuilder(final StringBuilder result, final int indentLevel) {
            addSpacesToStringBuilder(result, indentLevel);
            result.append("[EmphasizedSegment\n");
            
            getContent().toStringBuilder(result, indentLevel + INDENT_LEVEL);
            
            addSpacesToStringBuilder(result, indentLevel);
            result.append("]\n");
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        void writeWithRecordTag(final RecordOutputStream s) throws IOException {
            s.startRecord(ModuleSerializationTags.CALDOC_EMPHASIZED_SEGMENT, serializationSchema);
            getContent().write(s);
            s.endRecord();
        }
        
        /**
         * Load an instance of EmphasizedSegment from the RecordInputStream.
         * @param s the RecordInputStream to be read from.
         * @return a EmphasizedSegment instance deserialized from the stream.
         */
        static EmphasizedSegment load(final RecordInputStream s) throws IOException {
            final TextParagraph content = TextParagraph.load(s);
            return new EmphasizedSegment(content);
        }
    }
    
    /**
     * Represents a piece of text in a CALDoc comment that is strongly emphasized.
     *
     * @author Joseph Wong
     */
    public static final class StronglyEmphasizedSegment extends FormattedSegment {
        
        /**
         * Creates a representation of a piece of text in a CALDoc comment that is strongly emphasized.
         * 
         * @param content the content encapsulated by this segment.
         */
        StronglyEmphasizedSegment(final TextParagraph content) {
            super(content);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public <T, R> R accept(final CALDocCommentTextBlockVisitor<T, R> visitor, final T arg) {
            return visitor.visitStronglyEmphasizedSegment(this, arg);
        }
        
        /**
         * Fills the given StringBuilder with a string representation of this instance.
         * @param result the StringBuilder to fill.
         * @param indentLevel the indent level to use in indenting the generated text.
         */
        @Override
        public void toStringBuilder(final StringBuilder result, final int indentLevel) {
            addSpacesToStringBuilder(result, indentLevel);
            result.append("[StronglyEmphasizedSegment\n");
            
            getContent().toStringBuilder(result, indentLevel + INDENT_LEVEL);
            
            addSpacesToStringBuilder(result, indentLevel);
            result.append("]\n");
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        void writeWithRecordTag(final RecordOutputStream s) throws IOException {
            s.startRecord(ModuleSerializationTags.CALDOC_STRONGLY_EMPHASIZED_SEGMENT, serializationSchema);
            getContent().write(s);
            s.endRecord();
        }
        
        /**
         * Load an instance of StronglyEmphasizedSegment from the RecordInputStream.
         * @param s the RecordInputStream to be read from.
         * @return a StronglyEmphasizedSegment instance deserialized from the stream.
         */
        static StronglyEmphasizedSegment load(final RecordInputStream s) throws IOException {
            final TextParagraph content = TextParagraph.load(s);
            return new StronglyEmphasizedSegment(content);
        }
    }
    
    /**
     * Represents a piece of text in a CALDoc comment that is superscripted.
     *
     * @author Joseph Wong
     */
    public static final class SuperscriptSegment extends FormattedSegment {
        
        /**
         * Creates a representation of a piece of text in a CALDoc comment that is superscripted.
         * 
         * @param content the content encapsulated by this segment.
         */
        SuperscriptSegment(final TextParagraph content) {
            super(content);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public <T, R> R accept(final CALDocCommentTextBlockVisitor<T, R> visitor, final T arg) {
            return visitor.visitSuperscriptSegment(this, arg);
        }
        
        /**
         * Fills the given StringBuilder with a string representation of this instance.
         * @param result the StringBuilder to fill.
         * @param indentLevel the indent level to use in indenting the generated text.
         */
        @Override
        public void toStringBuilder(final StringBuilder result, final int indentLevel) {
            addSpacesToStringBuilder(result, indentLevel);
            result.append("[SuperscriptSegment\n");
            
            getContent().toStringBuilder(result, indentLevel + INDENT_LEVEL);
            
            addSpacesToStringBuilder(result, indentLevel);
            result.append("]\n");
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        void writeWithRecordTag(final RecordOutputStream s) throws IOException {
            s.startRecord(ModuleSerializationTags.CALDOC_SUPERSCRIPT_SEGMENT, serializationSchema);
            getContent().write(s);
            s.endRecord();
        }
        
        /**
         * Load an instance of SuperscriptSegment from the RecordInputStream.
         * @param s the RecordInputStream to be read from.
         * @return a SuperscriptSegment instance deserialized from the stream.
         */
        static SuperscriptSegment load(final RecordInputStream s) throws IOException {
            final TextParagraph content = TextParagraph.load(s);
            return new SuperscriptSegment(content);
        }
    }
    
    /**
     * Represents a piece of text in a CALDoc comment that is subscripted.
     *
     * @author Joseph Wong
     */
    public static final class SubscriptSegment extends FormattedSegment {
        
        /**
         * Creates a representation of a piece of text in a CALDoc comment that is subscripted.
         * 
         * @param content the content encapsulated by this segment.
         */
        SubscriptSegment(final TextParagraph content) {
            super(content);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public <T, R> R accept(final CALDocCommentTextBlockVisitor<T, R> visitor, final T arg) {
            return visitor.visitSubscriptSegment(this, arg);
        }
        
        /**
         * Fills the given StringBuilder with a string representation of this instance.
         * @param result the StringBuilder to fill.
         * @param indentLevel the indent level to use in indenting the generated text.
         */
        @Override
        public void toStringBuilder(final StringBuilder result, final int indentLevel) {
            addSpacesToStringBuilder(result, indentLevel);
            result.append("[SubscriptSegment\n");
            
            getContent().toStringBuilder(result, indentLevel + INDENT_LEVEL);
            
            addSpacesToStringBuilder(result, indentLevel);
            result.append("]\n");
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        void writeWithRecordTag(final RecordOutputStream s) throws IOException {
            s.startRecord(ModuleSerializationTags.CALDOC_SUBSCRIPT_SEGMENT, serializationSchema);
            getContent().write(s);
            s.endRecord();
        }
        
        /**
         * Load an instance of SubscriptSegment from the RecordInputStream.
         * @param s the RecordInputStream to be read from.
         * @return a SubscriptSegment instance deserialized from the stream.
         */
        static SubscriptSegment load(final RecordInputStream s) throws IOException {
            final TextParagraph content = TextParagraph.load(s);
            return new SubscriptSegment(content);
        }
    }
    
    /**
     * Represents a block of paragraphs within a CALDoc comment. The description
     * block of a CALDoc comment is a TextBlock, and so are the descriptions
     * contained with a number of the tagged blocks.
     * 
     * @author Joseph Wong
     */
    public static final class TextBlock {
        
        /** The paragraphs of text that constitute this text block. */
        private final Paragraph[] paragraphs;
        public static final Paragraph[] NO_PARAGRAPHS = new Paragraph[0];
        
        /**
         * Creates a representation of a block of paragraphs in a CALDoc
         * comment.
         * 
         * @param paragraphs the paragraphs of text that constitute the text block.
         */
        TextBlock(final Paragraph[] paragraphs) {
            verifyArrayArg(paragraphs, "paragraphs");

            if (paragraphs.length == 0) {
                this.paragraphs = NO_PARAGRAPHS;
            } else {
                this.paragraphs = paragraphs.clone();
            }
        }
        
        /**
         * Creates a representation of a block of paragraphs in a CALDoc
         * comment.
         * 
         * @param paragraphs the paragraphs of text that constitute the text block.
         */
        TextBlock(final List<Paragraph> paragraphs) {
            verifyArg(paragraphs, "paragraphs");
            
            final Paragraph[] paragraphsArray = paragraphs.toArray(new Paragraph[0]);

            if (paragraphsArray.length == 0) {
                this.paragraphs = NO_PARAGRAPHS;
            } else {
                this.paragraphs = paragraphsArray;
            }
        }
        
        /**
         * @return the number of paragraphs in this text block.
         */
        public int getNParagraphs() {
            return paragraphs.length;
        }
        
        /**
         * Returns the paragraph at the specified position in this text block.
         * @param index the index of the paragraph to return.
         * @return the paragraph at the specified position in this text block.
         */
        public Paragraph getNthParagraph(final int index) {
            return paragraphs[index];
        }
        
        /**
         * Accepts the visitation of a visitor, which implements the
         * CALDocCommentTextBlockVisitor interface.
         * <p>
         * 
         * As the CALDocCommentTextBlockVisitor follows a more general visitor pattern
         * where arguments can be passed into the visit methods and return
         * values obtained from them, this method passes through the argument
         * into the visit method, and returns as its return value the return
         * value of the visit method.
         * <p>
         * 
         * Nonetheless, for a significant portion of the common cases, the state of the
         * visitation can simply be kept as member variables within the visitor itself,
         * thereby eliminating the need to use the argument and return value of the
         * visit methods. In these scenarios, the recommended approach is to use
         * {@link Void} as the type argument for both <code>T</code> and <code>R</code>, and
         * pass in null as the argument, and return null as the return value.
         * <p>
         * 
         * @param <T> the argument type. If the visitation argument is not used, specify {@link Void}.
         * @param <R> the return type. If the return value is not used, specify {@link Void}.
         * 
         * @param visitor
         *            the visitor.
         * @param arg
         *            the argument to be passed to the visitor's visitTextBlock method.
         * @return the return value of the visitor's visitTextBlock method.
         */
        public <T, R> R accept(final CALDocCommentTextBlockVisitor<T, R> visitor, final T arg) {
            return visitor.visitTextBlock(this, arg);
        }
        
        /** @return a string representation of this instance. */
        @Override
        public final String toString() {
            final StringBuilder result = new StringBuilder();
            toStringBuilder(result, 0);
            return result.toString();
        }
        
        /**
         * Fills the given StringBuilder with a string representation of this instance.
         * @param result the StringBuilder to fill.
         * @param indentLevel the indent level to use in indenting the generated text.
         */
        public void toStringBuilder(final StringBuilder result, final int indentLevel) {
            addSpacesToStringBuilder(result, indentLevel);
            result.append("[TextBlock\n");
            
            for (final Paragraph paragraph : paragraphs) {
                paragraph.toStringBuilder(result, indentLevel + INDENT_LEVEL);
            }
            
            addSpacesToStringBuilder(result, indentLevel);
            result.append("]\n");
        }
        
        /**
         * Write this instance of TextBlock to the RecordOutputStream.
         * @param s the RecordOutputStream to be written to.
         */
        void write(final RecordOutputStream s) throws IOException {
            s.writeIntCompressed(paragraphs.length);
            for (final Paragraph paragraph : paragraphs) {
                paragraph.writeWithRecordTag(s);
            }
        }
        
        /**
         * Load an instance of TextBlock from the RecordInputStream.
         * @param s the RecordInputStream to be read from.
         * @return a TextBlock instance deserialized from the stream.
         */
        static TextBlock load(final RecordInputStream s) throws IOException {
            final int nParagraphs = s.readIntCompressed();
            
            final Paragraph[] paragraphs = new Paragraph[nParagraphs];
            for (int i = 0; i < nParagraphs; i++) {
                paragraphs[i] = Paragraph.loadWithRecordTag(s);
            }
            
            return new TextBlock(paragraphs);
        }
    }
    
    /**
     * Represents the "@arg" CALDoc tag. The tagged block contains a reference
     * to an argument name, and is therefore valid only for a CALDoc function
     * comment or a CALDoc data constructor comment.
     * 
     * @author Joseph Wong
     */
    public static final class ArgBlock {
        
        /** The name of the argument referenced by this "@arg" tag. */
        private final FieldName argName;
        
        /** The text block describing the argument. */
        private final TextBlock textBlock;
        
        /**
         * Creates a representation of an "@arg" block in a CALDoc comment.
         * 
         * @param argName
         *            the name of the argument referenced by this tagged block.
         * @param textBlock
         *            the text block that forms the trailing portion of this
         *            tagged block.
         */
        ArgBlock(final FieldName argName, final TextBlock textBlock) {
            this.argName = argName;
            this.textBlock = textBlock;
        }
        
        /**
         * @return the name of the argument referenced by this "@arg" tag.
         */
        public FieldName getArgName() {
            return argName;
        }
        
        /**
         * @return the text block describing the argument.
         */
        public TextBlock getTextBlock() {
            return textBlock;
        }
        
        /** @return a string representation of this instance. */
        @Override
        public final String toString() {
            final StringBuilder result = new StringBuilder();
            toStringBuilder(result, 0);
            return result.toString();
        }
        
        /**
         * Fills the given StringBuilder with a string representation of this instance.
         * @param result the StringBuilder to fill.
         * @param indentLevel the indent level to use in indenting the generated text.
         */
        public void toStringBuilder(final StringBuilder result, final int indentLevel) {
            addSpacesToStringBuilder(result, indentLevel);
            result.append("[ArgBlock - ").append(argName).append(":\n");
            
            textBlock.toStringBuilder(result, indentLevel + INDENT_LEVEL);
            
            addSpacesToStringBuilder(result, indentLevel);
            result.append("]\n");
        }
        
        /**
         * Write this instance of ArgBlock to the RecordOutputStream.
         * @param s the RecordOutputStream to be written to.
         */
        void write(final RecordOutputStream s) throws IOException {
            FieldNameIO.writeFieldName(argName, s);
            textBlock.write(s);
        }
        
        /**
         * Load an instance of ArgBlock from the RecordInputStream.
         * @param s the RecordInputStream to be read from.
         * @param moduleName the name of the module being loaded
         * @param msgLogger the logger to which to log deserialization messages.
         * @return an ArgBlock instance deserialized from the stream.
         */
        static ArgBlock load(final RecordInputStream s, final ModuleName moduleName, final CompilerMessageLogger msgLogger) throws IOException {
            final FieldName argName = FieldNameIO.load(s, moduleName, msgLogger);
            final TextBlock textBlock = TextBlock.load(s);
            return new ArgBlock(argName, textBlock);
        }
    }
    
    /**
     * A package-private abstract class for associating summary paragraphs
     * with a CALDocComment.
     *
     * @author Joseph Wong
     */
    static abstract class SummaryCollector {
        /**
         * Adds a summary paragraph to the comment.
         * @param paragraph the summary paragraph.
         */
        abstract void addSummaryParagraph(Paragraph paragraph);
    }

    /**
     * A package-private helper class for building a CALDocComment. Once a
     * CALDocComment is constructed, it cannot be changed since it is immutable.
     * 
     * @author Joseph Wong
     */
    static final class Builder extends SummaryCollector {
        
        /** A list of the paragraphs forming the summary of the comment. */
        private final List<Paragraph> summaryParagraphs = new ArrayList<Paragraph>(); 
        
        /** The description block at the start of the comment. */
        private TextBlock descriptionBlock = null;
        
        /** A list of the "@author" blocks in the comment. The list is empty if there are none of these blocks. */
        private final List<TextBlock> authorBlocks = new ArrayList<TextBlock>();
        
        /** The "@version" block in the comment, or null if there is none. */
        private TextBlock versionBlock = null;
        
        /** The "@deprecated" block in the comment, or null if there is none. */
        private TextBlock deprecatedBlock = null;
        
        /** A list of the "@arg" blocks in the comment. The list is empty if there are none of these blocks. */
        private final List<ArgBlock> argBlocks = new ArrayList<ArgBlock>();
        
        /** The "@return" block in the comment, or null if there is none. */
        private TextBlock returnBlock = null;
        
        /** a list of the "@see" module references in the comment. The list is empty if there are none of these references. */
        private final List<ModuleReference> moduleRefs = new ArrayList<ModuleReference>();
        
        /** a list of the "@see" function and class method references in the comment. The list is empty if there are none of these references. */
        private final List<ScopedEntityReference> functionOrClassMethodRefs = new ArrayList<ScopedEntityReference>();
        
        /** a list of the "@see" type constructor references in the comment. The list is empty if there are none of these references. */
        private final List<ScopedEntityReference> typeConsRefs = new ArrayList<ScopedEntityReference>();
        
        /** a list of the "@see" data constructor references in the comment. The list is empty if there are none of these references. */
        private final List<ScopedEntityReference> dataConsRefs = new ArrayList<ScopedEntityReference>();
        
        /** a list of the "@see" type class references in the comment. The list is empty if there are none of these references. */
        private final List<ScopedEntityReference> typeClassRefs = new ArrayList<ScopedEntityReference>();
        
        /**
         * Adds a summary paragraph to the comment.
         * @param paragraph the summary paragraph.
         */
        @Override
        void addSummaryParagraph(final Paragraph paragraph) {
            summaryParagraphs.add(paragraph);
        }
        
        /**
         * Sets the description block of the comment.
         * @param block the description block.
         */
        void setDescriptionBlock(final TextBlock block) {
            descriptionBlock = block;
        }
        
        /**
         * @return true iff the comment has a description block.
         */
        boolean hasDescriptionBlock() {
            return descriptionBlock != null;
        }
        
        /**
         * Adds an "@author" block to the comment. 
         * @param block the "@author" block.
         */
        void addAuthorBlock(final TextBlock block) {
            authorBlocks.add(block);
        }
        
        /**
         * Sets the "@version" block of the comment.
         * @param block the "@version" block.
         */
        void setVersionBlock(final TextBlock block) {
            versionBlock = block;
        }
        
        /**
         * @return true iff the comment has a "@version" block.
         */
        boolean hasVersionBlock() {
            return versionBlock != null;
        }
        
        /**
         * Sets the "@deprecated" block of the comment.
         * @param block the "@deprecated" block.
         */
        void setDeprecatedBlock(final TextBlock block) {
            deprecatedBlock = block;
        }
        
        /**
         * @return true iff the comment has a "@deprecated" block.
         */
        boolean hasDeprecatedBlock() {
            return deprecatedBlock != null;
        }
        
        /**
         * Adds an "@arg" block to the comment. 
         * @param block the "@arg" block.
         */
        void addArgBlock(final ArgBlock block) {
            argBlocks.add(block);
        }
        
        /**
         * @return the number of "@arg" blocks in the comment.
         */
        int getNArgBlocks() {
            return argBlocks.size();
        }
        
        /**
         * Sets the "@return" block of the comment.
         * @param block the "@return" block.
         */
        void setReturnBlock(final TextBlock block) {
            returnBlock = block;
        }
        
        /**
         * @return true iff the comment has a "@return" block.
         */
        boolean hasReturnBlock() {
            return returnBlock != null;
        }
        
        /**
         * Adds an "@see" module reference to the comment.
         * @param ref the reference.
         */
        void addModuleReference(final ModuleReference ref) {
            moduleRefs.add(ref);
        }
        
        /**
         * Adds an "@see" function or class method reference to the comment.
         * @param ref the reference.
         */
        void addFunctionOrClassMethodReference(final ScopedEntityReference ref) {
            functionOrClassMethodRefs.add(ref);
        }
        
        /**
         * Adds an "@see" type constructor reference to the comment.
         * @param ref the reference.
         */
        void addTypeConstructorReference(final ScopedEntityReference ref) {
            typeConsRefs.add(ref);
        }
        
        /**
         * Adds an "@see" data constructor reference to the comment.
         * @param ref the reference.
         */
        void addDataConstructorReference(final ScopedEntityReference ref) {
            dataConsRefs.add(ref);
        }
        
        /**
         * Adds an "@see" type class reference to the comment.
         * @param ref the reference.
         */
        void addTypeClassReference(final ScopedEntityReference ref) {
            typeClassRefs.add(ref);
        }
        
        /**
         * Constructs a CALDocComment based on the values of the fields.
         * @return a new CALDocComment instance.
         */
        CALDocComment toComment() {
            TextBlock summary;
            if (summaryParagraphs.size() > 0) {
                summary = new TextBlock(summaryParagraphs);
            } else {
                summary = null;
            }
            
            return new CALDocComment(
                summary,
                descriptionBlock,
                authorBlocks.toArray(new TextBlock[0]),
                versionBlock,
                deprecatedBlock,
                argBlocks.toArray(new ArgBlock[0]),
                returnBlock,
                moduleRefs.toArray(new ModuleReference[0]),
                functionOrClassMethodRefs.toArray(new ScopedEntityReference[0]),
                typeConsRefs.toArray(new ScopedEntityReference[0]),
                dataConsRefs.toArray(new ScopedEntityReference[0]),
                typeClassRefs.toArray(new ScopedEntityReference[0]));
        }
    }
    
    /**
     * Private constructor for a CALDocComment.
     * 
     * @param summary
     *            the summary of the comment.
     * @param descriptionBlock
     *            the description block at the start of the comment.
     * @param authorBlocks
     *            the "@author" blocks in the comment.
     * @param versionBlock
     *            the "@version" block in the comment.
     * @param deprecatedBlock
     *            the "@deprecated" block in the comment.
     * @param argBlocks
     *            the "@arg" blocks in the comment.
     * @param returnBlock
     *            the "@return" block in the comment.
     * @param moduleRefs
     *            the "@see" module references in the comment.
     * @param functionOrClassMethodRefs
     *            the "@see" function and class method references in the
     *            comment.
     * @param typeConsRefs
     *            the "@see" type constructor references in the comment.
     * @param dataConsRefs
     *            the "@see" data constructor references in the comment.
     * @param typeClassRefs
     *            the "@see" type class references in the comment.
     */
    private CALDocComment(
        final TextBlock summary,
        final TextBlock descriptionBlock,
        final TextBlock[] authorBlocks,
        final TextBlock versionBlock,
        final TextBlock deprecatedBlock,
        final ArgBlock[] argBlocks,
        final TextBlock returnBlock,
        final ModuleReference[] moduleRefs,
        final ScopedEntityReference[] functionOrClassMethodRefs,
        final ScopedEntityReference[] typeConsRefs,
        final ScopedEntityReference[] dataConsRefs,
        final ScopedEntityReference[] typeClassRefs) {
        
        this.summary = summary; // the summary can be null 
        
        verifyArg(descriptionBlock, "descriptionBlock"); // the description block cannot be null
        this.descriptionBlock = descriptionBlock;
        
        verifyArrayArg(authorBlocks, "authorBlocks");
        if (authorBlocks.length == 0) {
            this.authorBlocks = NO_TEXT_BLOCKS;
        } else {
            this.authorBlocks = authorBlocks; // no cloning because this constructor is private, and we count on the caller to have done the appropriate cloning already
        }
        
        this.versionBlock = versionBlock;
        
        this.deprecatedBlock = deprecatedBlock;
        
        verifyArrayArg(argBlocks, "argBlocks");
        if (argBlocks.length == 0) {
            this.argBlocks = NO_ARG_BLOCKS;
        } else {
            this.argBlocks = argBlocks; // no cloning because this constructor is private, and we count on the caller to have done the appropriate cloning already
        }
        
        this.returnBlock = returnBlock;
        
        verifyArrayArg(moduleRefs, "moduleRefs");
        if (moduleRefs.length == 0) {
            this.moduleRefs = NO_MODULE_REFS;
        } else {
            this.moduleRefs = moduleRefs; // no cloning because this constructor is private, and we count on the caller to have done the appropriate cloning already
        }
        
        verifyArrayArg(functionOrClassMethodRefs, "functionOrClassMethodRefs");
        if (functionOrClassMethodRefs.length == 0) {
            this.functionOrClassMethodRefs = NO_SCOPED_ENTITY_REFS;
        } else {
            this.functionOrClassMethodRefs = functionOrClassMethodRefs; // no cloning because this constructor is private, and we count on the caller to have done the appropriate cloning already
        }
        
        verifyArrayArg(typeConsRefs, "typeConsRefs");
        if (typeConsRefs.length == 0) {
            this.typeConsRefs = NO_SCOPED_ENTITY_REFS;
        } else {
            this.typeConsRefs = typeConsRefs; // no cloning because this constructor is private, and we count on the caller to have done the appropriate cloning already
        }
        
        verifyArrayArg(dataConsRefs, "dataConsRefs");
        if (dataConsRefs.length == 0) {
            this.dataConsRefs = NO_SCOPED_ENTITY_REFS;
        } else {
            this.dataConsRefs = dataConsRefs; // no cloning because this constructor is private, and we count on the caller to have done the appropriate cloning already
        }
        
        verifyArrayArg(typeClassRefs, "typeClassRefs");
        if (typeClassRefs.length == 0) {
            this.typeClassRefs = NO_SCOPED_ENTITY_REFS;
        } else {
            this.typeClassRefs = typeClassRefs; // no cloning because this constructor is private, and we count on the caller to have done the appropriate cloning already
        }
    }
    
    /**
     * Throws an exception if the array is null or has any null elements.
     * @param array Object[]
     * @param argName String name of the array for display in the error text of any generated exception
     * @throws NullPointerException if array is null, or any of its elements are null      
     */
    static void verifyArrayArg(final Object[] array, final String argName) {
        
        verifyArg(array, argName);
        
        for (int i = 0, nElems = array.length; i < nElems; i++) {
            if (array[i] == null) {                 
                throw new NullPointerException(argName + "[" + i + "] is null.");                   
            }
        }                  
    }
    
    /**
     * Throws an exception if arg is null.
     * @param arg Object
     * @param argName String name of the arg to display in the error text of any generated exception
     * @throws NullPointerException if 'arg' is null
     */
    static void verifyArg(final Object arg, final String argName) {
        if (arg == null) {
            throw new NullPointerException("The argument '" + argName + "' cannot be null.");
        }
    }

    /**
     * @return the summary of the comment.
     */
    public TextBlock getSummary() {
        return summary;
    }
    
    /**
     * @return the description block at the start of the comment.
     */
    public TextBlock getDescriptionBlock() {
        return descriptionBlock;
    }
    
    /**
     * @return the number of "@author" blocks in the comment.
     */
    public int getNAuthorBlocks() {
        return authorBlocks.length;
    }
    
    /**
     * Returns the "@author" block from the array of such blocks at the specified index.
     * @param index the index of the block.
     * @return the block at the specified index.
     */
    public TextBlock getNthAuthorBlock(final int index) {
        return authorBlocks[index];
    }

    /**
     * @return the "@version" block in the comment, or null if there is none.
     */
    public TextBlock getVersionBlock() {
        return versionBlock;
    }
    
    /**
     * @return the "@deprecated" block in the comment, or null if there is none.
     */
    public TextBlock getDeprecatedBlock() {
        return deprecatedBlock;
    }

    /**
     * @return the number of "@arg" blocks in the comment.
     */
    public int getNArgBlocks() {
        return argBlocks.length;
    }
    
    /**
     * Returns the "@arg" block from the array of such blocks at the specified index.
     * @param index the index of the block.
     * @return the block at the specified index.
     */
    public ArgBlock getNthArgBlock(final int index) {
        return argBlocks[index];
    }
    
    /**
     * @return the "@return" block in the comment, or null if there is none.
     */
    public TextBlock getReturnBlock() {
        return returnBlock;
    }
    
    /**
     * @return the number of "@see" module references in the comment.
     */
    public int getNModuleReferences() {
        return moduleRefs.length;
    }
    
    /**
     * Returns the "@see" module reference block from the array of such references at the specified index.
     * @param index the index of the reference.
     * @return the reference at the specified index.
     */
    public ModuleReference getNthModuleReference(final int index) {
        return moduleRefs[index];
    }
    
    /**
     * @return the number of "@see" function and class method references in the comment.
     */
    public int getNFunctionOrClassMethodReferences() {
        return functionOrClassMethodRefs.length;
    }
    
    /**
     * Returns the "@see" function or class method reference block from the array of such references at the specified index.
     * @param index the index of the reference.
     * @return the reference at the specified index.
     */
    public ScopedEntityReference getNthFunctionOrClassMethodReference(final int index) {
        return functionOrClassMethodRefs[index];
    }
    
    /**
     * @return the number of "@see" type constructor references in the comment.
     */
    public int getNTypeConstructorReferences() {
        return typeConsRefs.length;
    }
    
    /**
     * Returns the "@see" type constructor reference block from the array of such references at the specified index.
     * @param index the index of the reference.
     * @return the reference at the specified index.
     */
    public ScopedEntityReference getNthTypeConstructorReference(final int index) {
        return typeConsRefs[index];
    }

    /**
     * @return the number of "@see" data constructor references in the comment.
     */
    public int getNDataConstructorReferences() {
        return dataConsRefs.length;
    }
    
    /**
     * Returns the "@see" data constructor reference block from the array of such references at the specified index.
     * @param index the index of the reference.
     * @return the reference at the specified index.
     */
    public ScopedEntityReference getNthDataConstructorReference(final int index) {
        return dataConsRefs[index];
    }

    /**
     * @return the number of "@see" type class references in the comment.
     */
    public int getNTypeClassReferences() {
        return typeClassRefs.length;
    }
    
    /**
     * Returns the "@see" type class reference block from the array of such references at the specified index.
     * @param index the index of the reference.
     * @return the reference at the specified index.
     */
    public ScopedEntityReference getNthTypeClassReference(final int index) {
        return typeClassRefs[index];
    }
    
    /** @return a string representation of this instance. */
    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();
        
        result.append("[**\n");
        
        final int indentLevel = INDENT_LEVEL;
        
        // the summary, or null
        addSpacesToStringBuilder(result, indentLevel);
        result.append("summary = ");
        addMaybeNullTextBlockToStringBuilder(result, indentLevel + INDENT_LEVEL, summary);
        
        // the description block
        addSpacesToStringBuilder(result, indentLevel);
        result.append("descriptionBlock = ");
        addMaybeNullTextBlockToStringBuilder(result, indentLevel + INDENT_LEVEL, descriptionBlock);
        
        // the author blocks
        addSpacesToStringBuilder(result, indentLevel);
        result.append("authorBlocks = [\n");
        for (final TextBlock authorBlock : authorBlocks) {
            authorBlock.toStringBuilder(result, indentLevel + INDENT_LEVEL);
        }
        addSpacesToStringBuilder(result, indentLevel);
        result.append("]\n");
        
        // the version block, or null
        addSpacesToStringBuilder(result, indentLevel);
        result.append("versionBlock = ");
        addMaybeNullTextBlockToStringBuilder(result, indentLevel + INDENT_LEVEL, versionBlock);
        
        // the deprecated block, or null
        addSpacesToStringBuilder(result, indentLevel);
        result.append("deprecatedBlock = ");
        addMaybeNullTextBlockToStringBuilder(result, indentLevel + INDENT_LEVEL, deprecatedBlock);
        
        // the arg blocks
        addSpacesToStringBuilder(result, indentLevel);
        result.append("argBlocks = [\n");
        for (final ArgBlock argBlock : argBlocks) {
            argBlock.toStringBuilder(result, indentLevel + INDENT_LEVEL);
        }
        addSpacesToStringBuilder(result, indentLevel);
        result.append("]\n");
        
        // the return block, or null
        addSpacesToStringBuilder(result, indentLevel);
        result.append("returnBlock = ");
        addMaybeNullTextBlockToStringBuilder(result, indentLevel + INDENT_LEVEL, returnBlock);
        
        // the module references
        addSpacesToStringBuilder(result, indentLevel);
        result.append("moduleRefs = [");
        for (final ModuleReference moduleRef : moduleRefs) {
            result.append('\n');
            addSpacesToStringBuilder(result, indentLevel + INDENT_LEVEL);
            result.append(moduleRef);
        }
        result.append('\n');
        addSpacesToStringBuilder(result, indentLevel);
        result.append("]\n");
        
        // the function and class method references
        addSpacesToStringBuilder(result, indentLevel);
        result.append("functionOrClassMethodRefs = [");
        for (final ScopedEntityReference functionOrClassMethodRef : functionOrClassMethodRefs) {
            result.append('\n');
            addSpacesToStringBuilder(result, indentLevel + INDENT_LEVEL);
            result.append(functionOrClassMethodRef);
        }
        result.append('\n');
        addSpacesToStringBuilder(result, indentLevel);
        result.append("]\n");
        
        // the type constructor references
        addSpacesToStringBuilder(result, indentLevel);
        result.append("typeConsRefs = [");
        for (final ScopedEntityReference typeConsRef : typeConsRefs) {
            result.append('\n');
            addSpacesToStringBuilder(result, indentLevel + INDENT_LEVEL);
            result.append(typeConsRef);
        }
        result.append('\n');
        addSpacesToStringBuilder(result, indentLevel);
        result.append("]\n");
        
        // the data constructor references
        addSpacesToStringBuilder(result, indentLevel);
        result.append("dataConsRefs = [");
        for (final ScopedEntityReference dataConsRef : dataConsRefs) {
            result.append('\n');
            addSpacesToStringBuilder(result, indentLevel + INDENT_LEVEL);
            result.append(dataConsRef);
        }
        result.append('\n');
        addSpacesToStringBuilder(result, indentLevel);
        result.append("]\n");
        
        // the type class references
        addSpacesToStringBuilder(result, indentLevel);
        result.append("typeClassRefs = [");
        for (final ScopedEntityReference typeClassRef : typeClassRefs) {
            result.append('\n');
            addSpacesToStringBuilder(result, indentLevel + INDENT_LEVEL);
            result.append(typeClassRef);
        }
        result.append('\n');
        addSpacesToStringBuilder(result, indentLevel);
        result.append("]\n");
        
        result.append("*]\n");
        
        return result.toString();
    }
    
    /**
     * Appends the string representation of a TextBlock to the given StringBuilder. The TextBlock can be null.
     * @param buffer the StringBuilder to fill.
     * @param indentLevel the indent level to use.
     * @param block the TextBlock whose string representation is to be added to the buffer.
     */
    private void addMaybeNullTextBlockToStringBuilder(final StringBuilder buffer, final int indentLevel, final TextBlock block) {
        if (block == null) {
            buffer.append("null\n");
        } else {
            buffer.append("\n");
            block.toStringBuilder(buffer, indentLevel);
        }
    }
    
    /**
     * Fills the specified StringBuilder with the specified number of spaces.
     * @param buffer the StringBuilder to fill.
     * @param nSpaces the number of spaces to be appended to the buffer.
     */
    private static void addSpacesToStringBuilder(final StringBuilder buffer, final int nSpaces) {
        for (int i = 0; i < nSpaces; i++) {
            buffer.append(' ');
        }
    }
    
    /**
     * Returns a string of spaces.
     * @param nSpaces the number of space characters.
     * @return the requested string of spaces.
     */
    private static String spaces(final int nSpaces) {
        final StringBuilder buffer = new StringBuilder();
        addSpacesToStringBuilder(buffer, nSpaces);
        return buffer.toString();
    }
    
    /**
     * Write this instance of CALDocComment to the RecordOutputStream.
     * @param s the RecordOutputStream to be written to.
     */
    void write(final RecordOutputStream s) throws IOException {
        s.startRecord(ModuleSerializationTags.CALDOC_COMMENT, serializationSchema);
        
        // the description block
        descriptionBlock.write(s);
        
        // the flags
        final boolean hasVersionBlock = (versionBlock != null);
        final boolean hasDeprecatedBlock = (deprecatedBlock != null);
        final boolean hasReturnBlock = (returnBlock != null);
        final boolean hasAnySeeBlocks =
            moduleRefs.length > 0 ||
            functionOrClassMethodRefs.length > 0 ||
            typeConsRefs.length > 0 ||
            dataConsRefs.length > 0 ||
            typeClassRefs.length > 0;
            
        final boolean hasSummary = (summary != null);
        
        final byte[] flags = RecordOutputStream.booleanArrayToBitArray(new boolean[] {hasVersionBlock, hasDeprecatedBlock, hasReturnBlock, hasAnySeeBlocks, hasSummary});
        if (flags.length != 1) {
            throw new IOException("Unexpected number of flag bytes saving CALDocComment.");
        }
        s.writeByte(flags[0]);
        
        // the summary, or null
        if (hasSummary) {
            summary.write(s);
        }
        
        // the author blocks
        s.writeIntCompressed(authorBlocks.length);
        for (final TextBlock authorBlock : authorBlocks) {
            authorBlock.write(s);
        }
        
        // the version block, or null
        if (hasVersionBlock) {
            versionBlock.write(s);
        }
        
        // the deprecated block, or null
        if (hasDeprecatedBlock) {
            deprecatedBlock.write(s);
        }
        
        // the arg blocks
        s.writeIntCompressed(argBlocks.length);
        for (final ArgBlock argBlock : argBlocks) {
            argBlock.write(s);
        }
        
        // the return block, or null
        if (hasReturnBlock) {
            returnBlock.write(s);
        }
        
        // only write out the arrays of references if at least one of them is non-empty
        // (this optimizes the common case where a CALDoc comment has no "@see" references)
        if (hasAnySeeBlocks) {
            // the module references
            s.writeIntCompressed(moduleRefs.length);
            for (final ModuleReference moduleRef : moduleRefs) {
                moduleRef.write(s);
            }
            
            // the function and class method references
            s.writeIntCompressed(functionOrClassMethodRefs.length);
            for (final ScopedEntityReference functionOrClassMethodRef : functionOrClassMethodRefs) {
                functionOrClassMethodRef.write(s);
            }
            
            // the type constructor references
            s.writeIntCompressed(typeConsRefs.length);
            for (final ScopedEntityReference typeConsRef : typeConsRefs) {
                typeConsRef.write(s);
            }
            
            // the data constructor references
            s.writeIntCompressed(dataConsRefs.length);
            for (final ScopedEntityReference dataConsRef : dataConsRefs) {
                dataConsRef.write(s);
            }
            
            // the type class references
            s.writeIntCompressed(typeClassRefs.length);
            for (final ScopedEntityReference typeClassRef : typeClassRefs) {
                typeClassRef.write(s);
            }
        }
        
        s.endRecord();
    }
    
    /**
     * Load an instance of CALDocComment from the RecordInputStream.
     * Read position will be before the record header.
     * @param s the RecordInputStream to be read from.
     * @param moduleName the name of the module being loaded
     * @param msgLogger the logger to which to log deserialization messages.
     * @return a CALDocComment instance deserialized from the stream.
     */
    static CALDocComment load(final RecordInputStream s, final ModuleName moduleName, final CompilerMessageLogger msgLogger) throws IOException {
        final RecordHeaderInfo rhi = s.findRecord(ModuleSerializationTags.CALDOC_COMMENT);
        if (rhi == null) {
            throw new IOException ("Unable to find CALDoc record header.");
        }
        
        // the description block
        final TextBlock descriptionBlock = TextBlock.load(s);
        
        // the flags
        final byte flags = s.readByte();
        final boolean hasVersionBlock = (flags & 0x01) > 0;
        final boolean hasDeprecatedBlock = (flags & 0x02) > 0;
        final boolean hasReturnBlock = (flags & 0x04) > 0;
        final boolean hasAnySeeBlocks = (flags & 0x08) > 0;
        final boolean hasSummary = (flags & 0x10) > 0;
        
        // the summary, or null
        final TextBlock summary = hasSummary ? TextBlock.load(s) : null;

        // the author blocks
        final int nAuthorBlocks = s.readIntCompressed();
        final TextBlock[] authorBlocks = new TextBlock[nAuthorBlocks];
        for (int i = 0; i < nAuthorBlocks; i++) {
            authorBlocks[i] = TextBlock.load(s);
        }
        
        // the version block, or null
        final TextBlock versionBlock = hasVersionBlock ? TextBlock.load(s) : null;
        
        // the deprecated block, or null
        final TextBlock deprecatedBlock = hasDeprecatedBlock ? TextBlock.load(s) : null;
        
        // the arg blocks
        final int nArgBlocks = s.readIntCompressed();
        final ArgBlock[] argBlocks = new ArgBlock[nArgBlocks];
        for (int i = 0; i < nArgBlocks; i++) {
            argBlocks[i] = ArgBlock.load(s, moduleName, msgLogger);
        }
        
        // the return block, or null
        final TextBlock returnBlock = hasReturnBlock ? TextBlock.load(s) : null;
        
        // the arrays of references were only written if at least one of them was non-empty
        // (this optimizes the common case where a CALDoc comment has no "@see" references)
        //
        // so only proceed to reading them if they are indeed in the stream
        // (as indicated by the hasAnySeeBlocks flag)
        
        ModuleReference[] moduleRefs;
        ScopedEntityReference[] functionOrClassMethodRefs;
        ScopedEntityReference[] dataConsRefs;
        ScopedEntityReference[] typeConsRefs;
        ScopedEntityReference[] typeClassRefs;
        
        if (hasAnySeeBlocks) {
            // the module references
            final int nModuleRefs = s.readIntCompressed();
            moduleRefs = new ModuleReference[nModuleRefs];
            for (int i = 0; i < nModuleRefs; i++) {
                moduleRefs[i] = ModuleReference.load(s);
            }
            
            // the function and class method references
            final int nFunctionOrClassMethodRefs = s.readIntCompressed();
            functionOrClassMethodRefs = new ScopedEntityReference[nFunctionOrClassMethodRefs];
            for (int i = 0; i < nFunctionOrClassMethodRefs; i++) {
                functionOrClassMethodRefs[i] = ScopedEntityReference.load(s);
            }
            
            // the type constructor references
            final int nTypeConsRefs = s.readIntCompressed();
            typeConsRefs = new ScopedEntityReference[nTypeConsRefs];
            for (int i = 0; i < nTypeConsRefs; i++) {
                typeConsRefs[i] = ScopedEntityReference.load(s);
            }
            
            // the data constructor references
            final int nDataConsRefs = s.readIntCompressed();
            dataConsRefs = new ScopedEntityReference[nDataConsRefs];
            for (int i = 0; i < nDataConsRefs; i++) {
                dataConsRefs[i] = ScopedEntityReference.load(s);
            }
            
            // the type class references
            final int nTypeClassRefs = s.readIntCompressed();
            typeClassRefs = new ScopedEntityReference[nTypeClassRefs];
            for (int i = 0; i < nTypeClassRefs; i++) {
                typeClassRefs[i] = ScopedEntityReference.load(s);
            }
            
        } else {
            // there are no "@see" references in this comment, so all the arrays of references should be empty
            moduleRefs = new ModuleReference[0];
            functionOrClassMethodRefs = new ScopedEntityReference[0];
            typeConsRefs = new ScopedEntityReference[0];
            dataConsRefs = new ScopedEntityReference[0];
            typeClassRefs = new ScopedEntityReference[0];
        }
        
        s.skipRestOfRecord();
        
        return new CALDocComment(
            summary,
            descriptionBlock,
            authorBlocks,
            versionBlock,
            deprecatedBlock,
            argBlocks,
            returnBlock,
            moduleRefs,
            functionOrClassMethodRefs,
            typeConsRefs,
            dataConsRefs,
            typeClassRefs);
    }
}
