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
 * CALDocToTextUtilities.java
 * Creation date: Nov 9, 2005.
 * By: Joseph Wong
 */
package org.openquark.cal.caldoc;

import java.util.HashSet;
import java.util.Set;

import org.openquark.cal.compiler.CALDocComment;
import org.openquark.cal.compiler.CALDocCommentTextBlockTraverser;
import org.openquark.cal.compiler.ClassMethod;
import org.openquark.cal.compiler.Function;
import org.openquark.cal.compiler.FunctionalAgent;
import org.openquark.cal.compiler.ScopedEntity;
import org.openquark.cal.compiler.ScopedEntityNamingPolicy;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.compiler.CALDocComment.ArgBlock;
import org.openquark.cal.compiler.CALDocComment.CodeSegment;
import org.openquark.cal.compiler.CALDocComment.DataConsLinkSegment;
import org.openquark.cal.compiler.CALDocComment.EmphasizedSegment;
import org.openquark.cal.compiler.CALDocComment.FunctionOrClassMethodLinkSegment;
import org.openquark.cal.compiler.CALDocComment.ListItem;
import org.openquark.cal.compiler.CALDocComment.ListParagraph;
import org.openquark.cal.compiler.CALDocComment.ModuleLinkSegment;
import org.openquark.cal.compiler.CALDocComment.ModuleReference;
import org.openquark.cal.compiler.CALDocComment.PlainTextSegment;
import org.openquark.cal.compiler.CALDocComment.ScopedEntityReference;
import org.openquark.cal.compiler.CALDocComment.StronglyEmphasizedSegment;
import org.openquark.cal.compiler.CALDocComment.SubscriptSegment;
import org.openquark.cal.compiler.CALDocComment.SuperscriptSegment;
import org.openquark.cal.compiler.CALDocComment.TextBlock;
import org.openquark.cal.compiler.CALDocComment.TextParagraph;
import org.openquark.cal.compiler.CALDocComment.TypeClassLinkSegment;
import org.openquark.cal.compiler.CALDocComment.TypeConsLinkSegment;
import org.openquark.cal.compiler.CALDocComment.URLSegment;


/**
 * This is a utility class containing helper methods for converting CALDoc into
 * pretty-printed text.
 *
 * @author Joseph Wong
 */
public class CALDocToTextUtilities {
    
    /** The number of spaces per indent in the generated text. */
    private static final int INDENT_LEVEL = 2;

    /**
     * Implements a traverser which traverses through a CALDoc text block and builds up a text representation for it.
     *
     * @author Joseph Wong
     */
    private static final class TextBuilder extends CALDocCommentTextBlockTraverser<TextBuilder.VisitationArg, Void> {

        /**
         * The string builder used to aggregate the generated text.
         */
        private final StringBuilder buffer = new StringBuilder();
        
        /**
         * The current indentation level.
         */
        private int indentLevel;
        
        /** A marker interface for the types that can be used as arguments in this visitor. */
        private static interface VisitationArg {}
        
        /** An enumeration representing the various visitation options. */
        private static enum VisitationOption implements VisitationArg {
            /**
             * Visitation argument value for suppressing the initial paragraph break in a block of paragraphs during generation.
             */
            SUPPRESS_INITIAL_PARAGRAPH_BREAK,

            /**
             * Visitation argument value for suppressing the initial paragraph break in a block of paragraphs
             * and trimming one leading whitespace character during generation.
             */
            SUPPRESS_INITIAL_PARAGRAPH_BREAK_AND_TRIM_LEADING_WHITESPACE_CHAR,

            /**
             * Visitation argument value for trimming one leading whitespace character during generation.
             */
            TRIM_LEADING_WHITESPACE_CHAR_IN_SEGMENT,

            /**
             * Visitation argument value for indicating that the segment being generated is the last one in a paragraph whose paragraph
             * breaks are to be suppressed.
             */
            LAST_SEGMENT_IN_SUPPRESSED_PARAGRAPH
        }
        
        /**
         * Base class for a bullet maker for making bullets for list items.
         *
         * @author Joseph Wong
         */
        private static abstract class ListItemBulletMaker implements VisitationArg {
            
            /**
             * Implements a bullet maker for an ordered list, generating bullets '1. ', '2. ', '3. ',...
             *
             * @author Joseph Wong
             */
            private static final class Ordered extends ListItemBulletMaker {
                
                /**
                 * The next item's index. The first item starts at 1.
                 */
                private int nextItemIndex = 1;
                
                /**
                 * @return the bullet for the next list item.
                 */
                @Override
                String getNextBullet() {
                    final int index = nextItemIndex++;
                    return index + ". ";
                }
            }
            
            /**
             * Implements a bullet maker for an unordered list, generating bullets of the form: '- '.
             *
             * @author Joseph Wong
             */
            private static final class Unordered extends ListItemBulletMaker {
                /**
                 * @return the bullet for the next list item.
                 */
                @Override
                String getNextBullet() {
                    return "- ";
                }
            }
            
            /**
             * @return the bullet for the next list item.
             */
            abstract String getNextBullet();
        }
        
        /**
         * Constructs an instance of this class with no initial indentation.
         */
        TextBuilder() {
            this(0);
        }
        
        /**
         * Constructs an instance of this class with the specified initial indentation.
         * @param indentLevel the initial indentation level.
         */
        TextBuilder(final int indentLevel) {
            this.indentLevel = indentLevel;
        }
        
        /**
         * @return the generated text for the text block.
         */
        String getText() {
            return buffer.toString();
        }
        
        /**
         * Appends a newline and leading spaces for indenting the next line to the string builder.
         */
        private void appendNewline() {
            addNewlineToStringBuilder(buffer);
        }
        
        /**
         * Creates a string containing a newline and leading spaces for indenting the next line.
         * @return the requested string of newline and spaces.
         */
        private String getNewline() {
            final StringBuilder buf = new StringBuilder();
            addNewlineToStringBuilder(buf);
            return buf.toString();
        }
        
        /**
         * Appends a newline and leading spaces for indenting the next line to the given string builder.
         * @param buf the string builder to be modified.
         */
        private void addNewlineToStringBuilder(final StringBuilder buf) {
            buf.append('\n');
            for (int i = 0; i < indentLevel; i++) {
                buf.append(' ');
            }
        }
        
        /**
         * Generates text for the given text block.
         * @param block the text block to be traversed.
         * @param arg additional argument for the traversal.
         * @return null.
         */
        @Override
        public Void visitTextBlock(final TextBlock block, final VisitationArg arg) {
            for (int i = 0, n = block.getNParagraphs(); i < n; i++) {
                final boolean isFirstSegment = (i == 0);
                
                final VisitationArg paragraphArg;
                if (isFirstSegment) {
                    if (arg == VisitationOption.SUPPRESS_INITIAL_PARAGRAPH_BREAK || arg == VisitationOption.SUPPRESS_INITIAL_PARAGRAPH_BREAK_AND_TRIM_LEADING_WHITESPACE_CHAR) {
                        paragraphArg = arg;
                    } else {
                        paragraphArg = VisitationOption.SUPPRESS_INITIAL_PARAGRAPH_BREAK;
                    }
                } else {
                    paragraphArg = null;
                }
                
                block.getNthParagraph(i).accept(this, paragraphArg);
            }
            
            return null;
        }
        
        /**
         * Generates text for the given text paragraph.
         * @param paragraph the text paragraph to be traversed.
         * @param arg additional argument for the traversal.
         * @return null.
         */
        @Override
        public Void visitTextParagraph(final TextParagraph paragraph, final VisitationArg arg) {
            boolean suppressParagraphBreak = (arg == VisitationOption.SUPPRESS_INITIAL_PARAGRAPH_BREAK || arg == VisitationOption.SUPPRESS_INITIAL_PARAGRAPH_BREAK_AND_TRIM_LEADING_WHITESPACE_CHAR);
            
            if (!suppressParagraphBreak) {
                appendNewline();
                appendNewline();
            }
            
            for (int i = 0, n = paragraph.getNSegments(); i < n; i++) {
                final boolean isFirstSegment = (i == 0);
                final boolean isLastSegment = (i == n - 1);
                
                final VisitationArg segmentArg;
                if (isFirstSegment && arg == VisitationOption.SUPPRESS_INITIAL_PARAGRAPH_BREAK_AND_TRIM_LEADING_WHITESPACE_CHAR) {
                    segmentArg = VisitationOption.TRIM_LEADING_WHITESPACE_CHAR_IN_SEGMENT;
                } else if (isLastSegment && suppressParagraphBreak) {
                    segmentArg = VisitationOption.LAST_SEGMENT_IN_SUPPRESSED_PARAGRAPH;
                } else {
                    segmentArg = null;
                }
                
                paragraph.getNthSegment(i).accept(this, segmentArg);
            }
            
            return null;
        }

        /**
         * Generates text for the given list paragraph.
         * @param paragraph the list paragraph to be traversed.
         * @param arg additional argument for the traversal.
         * @return null.
         */
        @Override
        public Void visitListParagraph(final ListParagraph paragraph, final VisitationArg arg) {
            ListItemBulletMaker bulletMaker;
            if (paragraph.isOrdered()) {
                bulletMaker = new ListItemBulletMaker.Ordered();
            } else {
                bulletMaker = new ListItemBulletMaker.Unordered();
            }
            
            super.visitListParagraph(paragraph, bulletMaker);
            appendNewline();
            
            return null;
        }

        /**
         * Generates text for the given list item.
         * @param item the list item to be traversed.
         * @param arg additional argument for the traversal.
         * @return null.
         */
        @Override
        public Void visitListItem(final ListItem item, final VisitationArg arg) {
            final ListItemBulletMaker bulletMaker = (ListItemBulletMaker)arg;
            appendNewline();
            final String bullet = bulletMaker.getNextBullet();
            
            final int oldIndentLevel = indentLevel;
            indentLevel += bullet.length();
            
            buffer.append(bullet);
            super.visitListItem(item, arg);
            
            indentLevel = oldIndentLevel;
            
            appendNewline();
            
            return null;
        }

        /**
         * Generates text for the given plain text segment.
         * @param segment the segment to be traversed.
         * @param arg additional argument for the traversal.
         * @return null.
         */
        @Override
        public Void visitPlainTextSegment(final PlainTextSegment segment, final VisitationArg arg) {
            String text = segment.getText();
            
            if (arg == VisitationOption.TRIM_LEADING_WHITESPACE_CHAR_IN_SEGMENT) {
                if (text.length() > 0 && Character.isWhitespace(text.charAt(0))) {
                    text = text.substring(1);
                }
            } else if (arg == VisitationOption.LAST_SEGMENT_IN_SUPPRESSED_PARAGRAPH) {
                text = text.replaceAll("\\s+\\z", "");
            }
            
            text = text.replaceAll("(\r\n|\r|\n)", getNewline());
            
            buffer.append(text);
            
            return super.visitPlainTextSegment(segment, arg);
        }

        /**
         * Generates text for the given URL segment.
         * @param segment the segment to be traversed.
         * @param arg additional argument for the traversal.
         * @return null.
         */
        @Override
        public Void visitURLSegment(final URLSegment segment, final VisitationArg arg) {
            buffer.append('<').append(segment.getURL()).append('>');
            
            return super.visitURLSegment(segment, arg);
        }
        
        /**
         * Generates text for the given inline module cross-reference.
         * @param segment the segment to be traversed.
         * @param arg additional argument for the traversal.
         * @return null.
         */
        @Override
        public Void visitModuleLinkSegment(final ModuleLinkSegment segment, final VisitationArg arg) {
            final ModuleReference reference = segment.getReference();
            
            final String moduleNameInSource = reference.getModuleNameInSource();
            if (moduleNameInSource.length() == 0) {
                buffer.append(reference.getName());
            } else {
                buffer.append(moduleNameInSource);
            }
            
            return super.visitModuleLinkSegment(segment, arg);
        }
        
        /**
         * Returns the appropriately qualified name for a scoped entity reference.
         * @param reference the reference whose name is to be obtained.
         * @return the appropriately qualified name for a scoped entity reference.
         */
        private String getScopedEntityReferenceName(final ScopedEntityReference reference) {
            final String moduleNameInSource = reference.getModuleNameInSource();
            final String unqualifiedName = reference.getName().getUnqualifiedName();
            
            if (moduleNameInSource.length() == 0) {
                return unqualifiedName;
            } else {
                return moduleNameInSource + '.' + unqualifiedName;
            }
        }

        /**
         * Generates text for the given inline function or class method cross-reference.
         * @param segment the segment to be traversed.
         * @param arg additional argument for the traversal.
         * @return null.
         */
        @Override
        public Void visitFunctionOrClassMethodLinkSegment(final FunctionOrClassMethodLinkSegment segment, final VisitationArg arg) {
            buffer.append(getScopedEntityReferenceName(segment.getReference()));
            
            return super.visitFunctionOrClassMethodLinkSegment(segment, arg);
        }

        /**
         * Generates text for the given inline type constructor cross-reference.
         * @param segment the segment to be traversed.
         * @param arg additional argument for the traversal.
         * @return null.
         */
        @Override
        public Void visitTypeConsLinkSegment(final TypeConsLinkSegment segment, final VisitationArg arg) {
            buffer.append(getScopedEntityReferenceName(segment.getReference()));
            
            return super.visitTypeConsLinkSegment(segment, arg);
        }

        /**
         * Generates text for the given inline data constructor cross-reference.
         * @param segment the segment to be traversed.
         * @param arg additional argument for the traversal.
         * @return null.
         */
        @Override
        public Void visitDataConsLinkSegment(final DataConsLinkSegment segment, final VisitationArg arg) {
            buffer.append(getScopedEntityReferenceName(segment.getReference()));
            
            return super.visitDataConsLinkSegment(segment, arg);
        }

        /**
         * Generates text for the given inline type class cross-reference.
         * @param segment the segment to be traversed.
         * @param arg additional argument for the traversal.
         * @return null.
         */
        @Override
        public Void visitTypeClassLinkSegment(final TypeClassLinkSegment segment, final VisitationArg arg) {
            buffer.append(getScopedEntityReferenceName(segment.getReference()));
            
            return super.visitTypeClassLinkSegment(segment, arg);
        }

        /**
         * Generates text for the given code segment.
         * @param segment the segment to be traversed.
         * @param arg additional argument for the traversal.
         * @return null.
         */
        @Override
        public Void visitCodeSegment(final CodeSegment segment, final VisitationArg arg) {
            super.visitCodeSegment(segment, VisitationOption.SUPPRESS_INITIAL_PARAGRAPH_BREAK_AND_TRIM_LEADING_WHITESPACE_CHAR);
            
            return null;
        }

        /**
         * Generates text for the given emphasized segment.
         * @param segment the segment to be traversed.
         * @param arg additional argument for the traversal.
         * @return null.
         */
        @Override
        public Void visitEmphasizedSegment(final EmphasizedSegment segment, final VisitationArg arg) {
            buffer.append('/');
            super.visitEmphasizedSegment(segment, VisitationOption.SUPPRESS_INITIAL_PARAGRAPH_BREAK_AND_TRIM_LEADING_WHITESPACE_CHAR);
            buffer.append('/');
            
            return null;
        }

        /**
         * Generates text for the given strongly emphasized segment.
         * @param segment the segment to be traversed.
         * @param arg additional argument for the traversal.
         * @return null.
         */
        @Override
        public Void visitStronglyEmphasizedSegment(final StronglyEmphasizedSegment segment, final VisitationArg arg) {
            buffer.append('*');
            super.visitStronglyEmphasizedSegment(segment, VisitationOption.SUPPRESS_INITIAL_PARAGRAPH_BREAK_AND_TRIM_LEADING_WHITESPACE_CHAR);
            buffer.append('*');
            
            return null;
        }

        /**
         * Generates text for the given superscript segment.
         * @param segment the segment to be traversed.
         * @param arg additional argument for the traversal.
         * @return null.
         */
        @Override
        public Void visitSuperscriptSegment(final SuperscriptSegment segment, final VisitationArg arg) {
            buffer.append('^');
            super.visitSuperscriptSegment(segment, VisitationOption.SUPPRESS_INITIAL_PARAGRAPH_BREAK_AND_TRIM_LEADING_WHITESPACE_CHAR);
            
            return null;
        }

        /**
         * Generates text for the given superscript segment.
         * @param segment the segment to be traversed.
         * @param arg additional argument for the traversal.
         * @return null.
         */
        @Override
        public Void visitSubscriptSegment(final SubscriptSegment segment, final VisitationArg arg) {
            buffer.append('_');
            super.visitSubscriptSegment(segment, VisitationOption.SUPPRESS_INITIAL_PARAGRAPH_BREAK_AND_TRIM_LEADING_WHITESPACE_CHAR);
            
            return null;
        }
    }

    /** Private constructor. */
    private CALDocToTextUtilities() {}
    
    /**
     * Generates the text for a text block appearing in a CALDoc comment.
     * @param textBlock the text block to be formatted as text.
     * @return the text for the text block.
     */
    public static String getTextFromCALDocTextBlock(final CALDocComment.TextBlock textBlock) {
        return getTextFromCALDocTextBlock(textBlock, 0);
    }
    
    /**
     * Generates the text for a text block appearing in a CALDoc comment.
     * @param textBlock the text block to be formatted as text.
     * @param initialIndentLevel the number of spaces to indent all of the text.
     * @return the text for the text block.
     */
    public static String getTextFromCALDocTextBlock(final CALDocComment.TextBlock textBlock, final int initialIndentLevel) {
        final TextBuilder builder = new TextBuilder(initialIndentLevel);
        textBlock.accept(builder, null);
        return builder.getText();
    }
    
    /**
     * Generates the text for a text block appearing in a CALDoc comment with the first line indented and with
     * a trailing newline at the end of the block.
     * @param textBlock the text block to be formatted as text.
     * @param initialIndentLevel the number of spaces to indent all of the text.
     * @return the text for the text block.
     */
    private static String getTextFromCALDocTextBlockWithLeadingIndentAndTrailingNewline(final CALDocComment.TextBlock textBlock, final int initialIndentLevel) {
        return spaces(initialIndentLevel) + getTextFromCALDocTextBlock(textBlock, initialIndentLevel) + '\n';
    }
    
    /**
     * Generates a plain text representation for a CALDoc comment of a ScopedEntity.
     * @param entity the entity with the CALDoc comment.
     * @return a plain text representation for the comment, or null if the entity does not have a CALDoc comment.
     */
    public static String getTextFromCALDocCommentOfScopedEntity(final ScopedEntity entity) {
        final CALDocComment caldoc = entity.getCALDocComment();
        if (caldoc == null) {
            return null;
        }
        
        if (entity instanceof FunctionalAgent) {
            return getTextFromCALDocComment(caldoc, (FunctionalAgent)entity, ScopedEntityNamingPolicy.FULLY_QUALIFIED);
        } else {
            return getTextFromCALDocComment(caldoc);
        }
    }

    /**
     * Generates a plain text representation for a CALDoc comment.
     * @param caldoc the CALDoc comment.
     * @return a plain text representation for the comment.
     */
    public static String getTextFromCALDocComment(final CALDocComment caldoc) {
        // even though we specify ScopedEntityNamingPolicy.QUALIFIED here, it has no effect on the generated
        // text as there is no FunctionalAgent from which a type signature (potentially containing types in other modules)
        // can be extracted.
        return getTextFromCALDocComment(caldoc, null, ScopedEntityNamingPolicy.FULLY_QUALIFIED);
    }
    
    /**
     * Generates a plain text representation for a CALDoc comment.
     * @param caldoc the CALDoc comment.
     * @param envEntity the FunctionalAgent being documented. Can be null if the comment is not for an FunctionalAgent.
     * @param scopedEntityNamingPolicy the naming policy to use for generating qualified/unqualified names.
     * @return a plain text representation for the comment.
     */
    public static String getTextFromCALDocComment(final CALDocComment caldoc, final FunctionalAgent envEntity, final ScopedEntityNamingPolicy scopedEntityNamingPolicy) {
        TypeExpr typeExpr;
        if (envEntity == null) {
            typeExpr = null;
        } else {
            typeExpr = envEntity.getTypeExpr();
        }
        
        return getTextFromCALDocComment(caldoc, envEntity, typeExpr, scopedEntityNamingPolicy);
    }
    
    /**
     * Generates a plain text representation for a CALDoc comment.
     * @param caldoc the CALDoc comment.
     * @param envEntity the FunctionalAgent being documented. Can be null if the comment is not for an FunctionalAgent.
     * @param typeExpr the type of the entity. Can be null if envEntity is null.
     * @param scopedEntityNamingPolicy the naming policy to use for generating qualified/unqualified names.
     * @return a plain text representation for the comment.
     */
    public static String getTextFromCALDocComment(final CALDocComment caldoc, final FunctionalAgent envEntity, final TypeExpr typeExpr, final ScopedEntityNamingPolicy scopedEntityNamingPolicy) {
        
        final StringBuilder buffer = new StringBuilder();
        
        /// The deprecated block comes first.
        //
        final TextBlock deprecatedBlock = caldoc.getDeprecatedBlock();
        if (deprecatedBlock != null) {
            buffer.append("[").append(CALDocMessages.getString("deprecatedColon")).append("\n");
            buffer.append(getTextFromCALDocTextBlockWithLeadingIndentAndTrailingNewline(deprecatedBlock, INDENT_LEVEL));
            buffer.append("]\n\n");
        }
        
        /// If the entity is a class method, then display whether it is required or optional
        //
        if (envEntity instanceof ClassMethod) {
            final ClassMethod method = (ClassMethod)envEntity;
            
            buffer.append("\n");
            if (method.getDefaultClassMethodName() == null) {
                // no default - required
                buffer.append(CALDocMessages.getString("requiredMethodIndicator"));
            } else {
                // has default - optional
                buffer.append(CALDocMessages.getString("optionalMethodIndicator"));
            }
            buffer.append("\n\n");
        }
        
        /// The main description
        //
        buffer.append(getTextFromCALDocTextBlockWithLeadingIndentAndTrailingNewline(caldoc.getDescriptionBlock(), 0));
        
        /// The arguments and the return value
        //
        buffer.append(getTextForArgumentsAndReturnValue(caldoc, envEntity, typeExpr, scopedEntityNamingPolicy));
        
        /// The authors
        //
        final int nAuthors = caldoc.getNAuthorBlocks();
        if (nAuthors > 0) {
            buffer.append("\n").append(CALDocMessages.getString("authorColon")).append("\n");
            for (int i = 0; i < nAuthors; i++) {
                final TextBlock authorBlock = caldoc.getNthAuthorBlock(i);
                buffer.append(getTextFromCALDocTextBlockWithLeadingIndentAndTrailingNewline(authorBlock, INDENT_LEVEL));
            }
        }
        
        /// The version
        //
        final TextBlock versionBlock = caldoc.getVersionBlock();
        if (versionBlock != null) {
            buffer.append("\n").append(CALDocMessages.getString("versionColon")).append("\n");
            buffer.append(getTextFromCALDocTextBlockWithLeadingIndentAndTrailingNewline(versionBlock, INDENT_LEVEL));
        }
        
        /// The "See Also" references
        //
        final StringBuilder seeAlso = new StringBuilder();
        
        // module cross references
        final int nModuleRefs = caldoc.getNModuleReferences();
        if (nModuleRefs > 0) {
            seeAlso.append(spaces(INDENT_LEVEL)).append(CALDocMessages.getString("modulesColon")).append("\n");
            for (int i = 0; i < nModuleRefs; i++) {
                seeAlso.append(spaces(INDENT_LEVEL * 2)).append(caldoc.getNthModuleReference(i).getName()).append('\n');
            }
            seeAlso.append('\n');
        }
                
        // function and class method references
        final int nFuncRefs = caldoc.getNFunctionOrClassMethodReferences();
        if (nFuncRefs > 0) {
            seeAlso.append(spaces(INDENT_LEVEL)).append(CALDocMessages.getString("functionsAndClassMethodsColon")).append("\n");
            for (int i = 0; i < nFuncRefs; i++) {
                seeAlso.append(spaces(INDENT_LEVEL * 2)).append(caldoc.getNthFunctionOrClassMethodReference(i).getName()).append('\n');
            }
            seeAlso.append('\n');
        }
    
        // type constructor references
        final int nTypeConsRefs = caldoc.getNTypeConstructorReferences();
        if (nTypeConsRefs > 0) {
            seeAlso.append(spaces(INDENT_LEVEL)).append(CALDocMessages.getString("typeConstructorsColon")).append("\n");
            for (int i = 0; i < nTypeConsRefs; i++) {
                seeAlso.append(spaces(INDENT_LEVEL * 2)).append(caldoc.getNthTypeConstructorReference(i).getName()).append('\n');
            }
            seeAlso.append('\n');
        }
    
        // data constructor references
        final int nDataConsRefs = caldoc.getNDataConstructorReferences();
        if (nDataConsRefs > 0) {
            seeAlso.append(spaces(INDENT_LEVEL)).append(CALDocMessages.getString("dataConstructorsColon")).append("\n");
            for (int i = 0; i < nDataConsRefs; i++) {
                seeAlso.append(spaces(INDENT_LEVEL * 2)).append(caldoc.getNthDataConstructorReference(i).getName()).append('\n');
            }
            seeAlso.append('\n');
        }
        
        // type class references
        final int nTypeClassRefs = caldoc.getNTypeClassReferences();
        if (nTypeClassRefs > 0) {
            seeAlso.append(spaces(INDENT_LEVEL)).append(CALDocMessages.getString("typeClassesColon")).append("\n");
            for (int i = 0; i < nTypeClassRefs; i++) {
                seeAlso.append(spaces(INDENT_LEVEL * 2)).append(caldoc.getNthTypeClassReference(i).getName()).append('\n');
            }
            seeAlso.append('\n');
        }
        
        if (seeAlso.length() > 0) {
            buffer.append("\n").append(CALDocMessages.getString("seeAlsoColon")).append("\n").append(seeAlso);
        }
        
        return buffer.toString();
    }

    /**
     * Generates a plain text representation for the arguments and return value of a function, class method, or data constructor.
     * @param caldoc the associated CALDoc. Can be null.
     * @param envEntity the associated entity. Can be null.
     * @param scopedEntityNamingPolicy the naming policy to use for generating qualified/unqualified names.
     */
    public static String getTextForArgumentsAndReturnValue(final CALDocComment caldoc, final FunctionalAgent envEntity, final ScopedEntityNamingPolicy scopedEntityNamingPolicy) {
        TypeExpr typeExpr;
        if (envEntity == null) {
            typeExpr = null;
        } else {
            typeExpr = envEntity.getTypeExpr();
        }
        
        return getTextForArgumentsAndReturnValue(caldoc, envEntity, typeExpr, scopedEntityNamingPolicy);
    }
    
    /**
     * Generates a plain text representation for the arguments and return value of a function, class method, or data constructor.
     * @param caldoc the associated CALDoc. Can be null.
     * @param envEntity the associated entity. Can be null.
     * @param typeExpr the type of the entity. Can be null if envEntity is null.
     * @param scopedEntityNamingPolicy the naming policy to use for generating qualified/unqualified names.
     */
    public static String getTextForArgumentsAndReturnValue(final CALDocComment caldoc, final FunctionalAgent envEntity, final TypeExpr typeExpr, final ScopedEntityNamingPolicy scopedEntityNamingPolicy) {
        final StringBuilder buffer = new StringBuilder();
                       
        /// The arguments
        //
        final int nArgsInCALDoc = (caldoc == null) ? -1 : caldoc.getNArgBlocks();
        final int arity = (typeExpr == null) ? -1 : typeExpr.getArity();
        
        final int nArgs = Math.max(nArgsInCALDoc, arity);
        
        TypeExpr[] typePieces = null;
        String[] typePieceStrings = null;
        if (typeExpr != null) {
            typePieces = typeExpr.getTypePieces();
            typePieceStrings = TypeExpr.toStringArray(typePieces, true, scopedEntityNamingPolicy);
        }
        
        if (nArgs > 0) {
            buffer.append("\n").append(CALDocMessages.getString("argumentsColon")).append("\n");
            
            final Set<String> setOfArgumentNames = new HashSet<String>();
            
            for (int i = 0; i < nArgs; i++) {
                buffer.append(spaces(INDENT_LEVEL)).append(getNthArgumentName(caldoc, envEntity, i, setOfArgumentNames));
                
                if (typePieces != null) {
                    buffer.append(" :: ").append(typePieceStrings[i]);
                }
                
                buffer.append("\n");
                
                if (caldoc != null && i < nArgsInCALDoc) {
                    final ArgBlock argBlock = caldoc.getNthArgBlock(i);
                    buffer.append(getTextFromCALDocTextBlockWithLeadingIndentAndTrailingNewline(argBlock.getTextBlock(), INDENT_LEVEL * 2));
                }
            }
        }
        
        /// The return value
        //
        final boolean hasReturnBlock = (caldoc != null) && (caldoc.getReturnBlock() != null);
        
        if (hasReturnBlock || envEntity instanceof Function || envEntity instanceof ClassMethod) {
            buffer.append("\n").append(CALDocMessages.getString("returnsColon")).append("\n");
            
            if (typePieces != null) {
                final String returnTypeString = typePieceStrings[arity];
                buffer.append(spaces(INDENT_LEVEL)).append(CALDocMessages.getString("returnValueIndicator")).append(" :: ").append(returnTypeString).append("\n");
            }
            
            if (hasReturnBlock) {
                final TextBlock returnBlock = caldoc.getReturnBlock();
                buffer.append(getTextFromCALDocTextBlockWithLeadingIndentAndTrailingNewline(returnBlock, INDENT_LEVEL * 2));
            }
        }
        
        return buffer.toString();
    }
    
    /**
     * Generates an argument name of an FunctionalAgent. It gives preference to the name given in
     * CALDoc, if it is different from the name appearing in the entity itself.
     * 
     * @param caldoc the CALDoc comment of the entity. Can be null.
     * @param envEntity the FunctionalAgent whose argument name is being generated.
     * @param index the position of the argument in the argument list.
     * @param setOfArgumentNames the (Set of Strings) of argument names already used (for disambiguation purposes).
     * @return the appropriate argument name.
     */
    private static String getNthArgumentName(final CALDocComment caldoc, final FunctionalAgent envEntity, final int index, final Set<String> setOfArgumentNames) {
        ////
        /// First fetch the name from the entity. This will mostly be the same name as the one appearing in code, except for
        /// foreign functions, which may have their sames extracted from the Java classes' debug info.
        //
        String nameFromEntity = null;
        if (index < envEntity.getNArgumentNames()) {
            nameFromEntity = envEntity.getArgumentName(index);
        }
        
        String result;
        
        if (caldoc != null && index < caldoc.getNArgBlocks()) {
            ////
            /// Get the CALDoc name, if available.
            //
            
            final String nameFromCALDoc = caldoc.getNthArgBlock(index).getArgName().getCalSourceForm();
            
            result = nameFromCALDoc;
            setOfArgumentNames.add(nameFromCALDoc);
            
        } else if (nameFromEntity != null) {
            ////
            /// If the entity yielded a name, use it.
            //
            
            result = nameFromEntity;
            setOfArgumentNames.add(nameFromEntity);
            
        } else {
            ////
            /// Since not even the entity yielded a name, construct an artificial one of the form arg_x, where x is the
            /// 1-based index of the argument in the argument list, or of the form arg_x_y if arg_x, arg_x_1, ...
            /// arg_x_(y-1) have all appeared previously in the argument list.
            //
            
            // the base artificial name we'll attempt to use is arg_x, where x is the 1-based index of this argument
            final String baseArtificialName = "arg_" + (index + 1);
            String artificialName = baseArtificialName;
            
            // if the base artificial name already appears in previous arguments, then
            // make the argument name arg_x_y, where y is a supplementary disambiguating number
            // chosen so that the resulting name will not collide with any of the previous argument names
            
            int supplementaryDisambiguator = 1;
            while (setOfArgumentNames.contains(artificialName)) {
                artificialName = baseArtificialName + "_" + supplementaryDisambiguator;
                supplementaryDisambiguator++;
            }
            
            result = artificialName;
            setOfArgumentNames.add(artificialName);
        }
        
        return result;
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
}
