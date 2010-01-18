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
 * CALDocToHTMLUtilities.java
 * Creation date: Sep 28, 2005.
 * By: Joseph Wong
 */
package org.openquark.cal.caldoc;

import java.text.BreakIterator;
import java.util.Locale;

import javax.swing.text.html.HTML;

import org.openquark.cal.caldoc.HTMLBuilder.AttributeList;
import org.openquark.cal.compiler.CALDocComment;
import org.openquark.cal.compiler.CALDocCommentTextBlockTraverser;
import org.openquark.cal.compiler.QualifiedName;
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
import org.openquark.cal.services.CALFeatureName;
import org.openquark.cal.services.LocaleUtilities;


/**
 * This is a utility class containing helper methods for converting CALDoc into
 * properly formatted HTML.
 *
 * @author Joseph Wong
 */
public final class CALDocToHTMLUtilities {
    
    /**
     * This class encapsulates a piece of content that is convertible to HTML. It is effectively used as a
     * lazily-evaluated thunk that is executed to generate the HTML when needed (and at the correct position!)
     *
     * @author Joseph Wong
     */
    static abstract class ContentConvertibleToHTML {
        /** @return true if there is no content; false otherwise. */
        abstract boolean isEmpty();
        /**
         * Generate the content as HTML.
         * @param builder the HTMLBuilder to use for generating the HTML.
         * @param referenceGenerator the reference generator to use for generating cross references.
         */
        abstract void generateHTML(HTMLBuilder builder, CrossReferenceHTMLGenerator referenceGenerator);
    }

    /**
     * A subclass of ContentConvertibleToHTML for representing simple string content.
     *
     * @author Joseph Wong
     */
    static final class SimpleStringContent extends ContentConvertibleToHTML {
        /** The string content. */
        private final String content;
        
        /** @param content the string content. Can be null.*/
        SimpleStringContent(final String content) {
            this.content = (content != null) ? content.trim() : null; // trim the content of its leading and trailing whitespace
        }
        
        /** {@inheritDoc} */
        @Override
        final boolean isEmpty() {
            return content == null || content.length() == 0;
        }
        
        /** {@inheritDoc} */
        @Override
        final void generateHTML(final HTMLBuilder currentPage, final CrossReferenceHTMLGenerator referenceGenerator) {
            currentPage.addText(content);
        }
    }

    /**
     * A subclass of ContentConvertibleToHTML for representing a single CALDoc paragraph.
     *
     * @author Joseph Wong
     */
    static final class SingleParagraphContent extends ContentConvertibleToHTML {
        /** The CALDoc paragraph. */
        private final CALDocComment.Paragraph paragraph;
        
        /** The style class for code blocks. Can be null. */
        private final StyleClass codeStyleClass;
        
        /** The HTML tag to use for formatting a code fragment. Should be either HTML.Tag.TT or HTML.Tag.CODE. */
        private final HTML.Tag codeFormattingTag;
        
        /**
         * @param paragraph the CALDoc paragraph.
         * @param codeStyleClass the style class to use for code blocks. Can be null.
         * @param codeFormattingTag the HTML tag to use for formatting a code fragment. Should be either HTML.Tag.TT or HTML.Tag.CODE.
         */
        SingleParagraphContent(final CALDocComment.Paragraph paragraph, final StyleClass codeStyleClass, final HTML.Tag codeFormattingTag) {
            this.paragraph = paragraph;
            this.codeStyleClass = codeStyleClass;
            this.codeFormattingTag = codeFormattingTag;
        }
        
        /** {@inheritDoc} */
        @Override
        final boolean isEmpty() {
            return paragraph == null;
        }
        
        /** {@inheritDoc} */
        @Override
        final void generateHTML(final HTMLBuilder currentPage, final CrossReferenceHTMLGenerator referenceGenerator) {
            generateHTMLForCALDocParagraph(paragraph, currentPage, referenceGenerator, codeStyleClass, codeFormattingTag);
        }
    }
    
    /**
     * A subclass of ContentConvertibleToHTML for representing the initial segments of a text paragraph.
     *
     * @author Joseph Wong
     */
    static final class TextParagraphInitialSegments extends ContentConvertibleToHTML {
        /** The CALDoc text paragraph. */
        private final CALDocComment.TextParagraph paragraph;
        
        /** The number of complete initial segments to include. */
        private final int nCompleteSegments;
        
        /** The plain text to form the end of the generated text. */
        private final String endPlainText;
        
        /** The style class for code blocks. Can be null. */
        private final StyleClass codeStyleClass;
        
        /** The HTML tag to use for formatting a code fragment. Should be either HTML.Tag.TT or HTML.Tag.CODE. */
        private final HTML.Tag codeFormattingTag;
        
        /**
         * @param paragraph the CALDoc text paragraph.
         * @param nCompleteSegments the number of complete initial segments to include.
         * @param endPlainText the plain text to form the end of the generated text.
         * @param codeStyleClass the style class to use for code blocks. Can be null.
         * @param codeFormattingTag the HTML tag to use for formatting a code fragment. Should be either HTML.Tag.TT or HTML.Tag.CODE.
         */
        TextParagraphInitialSegments(final CALDocComment.TextParagraph paragraph, final int nCompleteSegments, final String endPlainText, final StyleClass codeStyleClass, final HTML.Tag codeFormattingTag) {
            this.paragraph = paragraph;
            this.nCompleteSegments = Math.min(paragraph.getNSegments(), nCompleteSegments);
            this.endPlainText = endPlainText;
            this.codeStyleClass = codeStyleClass;
            this.codeFormattingTag = codeFormattingTag;
        }
        
        /** {@inheritDoc} */
        @Override
        final boolean isEmpty() {
            return paragraph == null || paragraph.getNSegments() == 0 || (nCompleteSegments == 0 && endPlainText.length() == 0);
        }
        
        /** {@inheritDoc} */
        @Override
        final void generateHTML(final HTMLBuilder currentPage, final CrossReferenceHTMLGenerator referenceGenerator) {
            for (int i = 0; i < nCompleteSegments; i++) {
                generateHTMLForCALDocSegment(paragraph.getNthSegment(i), currentPage, referenceGenerator, codeStyleClass, codeFormattingTag);    
            }
            currentPage.addText(htmlEscape(endPlainText));
        }
    }

    /**
     * A subclass of ContentConvertibleToHTML for representing a single CALDoc text block.
     *
     * @author Joseph Wong
     */
    static final class SingleTextBlockContent extends ContentConvertibleToHTML {
        /** The CALDoc text block. */
        private final CALDocComment.TextBlock textBlock;
        
        /** The style class for code blocks. Can be null. */
        private final StyleClass codeStyleClass;
        
        /** The HTML tag to use for formatting a code fragment. Should be either HTML.Tag.TT or HTML.Tag.CODE. */
        private final HTML.Tag codeFormattingTag;
        
        /**
         * @param textBlock the CALDoc text block.
         * @param codeStyleClass the style class to use for code blocks. Can be null.
         * @param codeFormattingTag the HTML tag to use for formatting a code fragment. Should be either HTML.Tag.TT or HTML.Tag.CODE.
         */
        SingleTextBlockContent(final CALDocComment.TextBlock textBlock, final StyleClass codeStyleClass, final HTML.Tag codeFormattingTag) {
            this.textBlock = textBlock;
            this.codeStyleClass = codeStyleClass;
            this.codeFormattingTag = codeFormattingTag;
        }
        
        /** {@inheritDoc} */
        @Override
        final boolean isEmpty() {
            return textBlock == null || textBlock.getNParagraphs() == 0;
        }
        
        /** {@inheritDoc} */
        @Override
        final void generateHTML(final HTMLBuilder currentPage, final CrossReferenceHTMLGenerator referenceGenerator) {
            generateHTMLForCALDocTextBlock(textBlock, currentPage, referenceGenerator, codeStyleClass, codeFormattingTag);
        }
    }

    /**
     * Abstract base class for a generator capable of generating appropriate HTML for cross-references.
     * 
     * Generators that produce the HTML as strings should use {@link CALDocToHTMLUtilities.CrossReferenceHTMLStringGenerator}
     * as the base class.
     *
     * @author Joseph Wong
     * @see CALDocToHTMLUtilities.CrossReferenceHTMLStringGenerator
     */
    public static abstract class CrossReferenceHTMLGenerator {
        
        /** Package-scoped constructor. */
        CrossReferenceHTMLGenerator() {}
        
        /**
         * Generates HTML for a module cross-reference.
         * @param builder the HTMLBuilder to use for generating the cross-reference.
         * @param reference the module cross-reference.
         */
        abstract void generateModuleReference(HTMLBuilder builder, ModuleReference reference);
        
        /**
         * Generates HTML for a type constructor cross-reference.
         * @param builder the HTMLBuilder to use for generating the cross-reference.
         * @param reference the type constructor cross-reference.
         */
        abstract void generateTypeConsReference(HTMLBuilder builder, ScopedEntityReference reference);
        
        /**
         * Generates HTML for a data constructor cross-reference.
         * @param builder the HTMLBuilder to use for generating the cross-reference.
         * @param reference the data constructor cross-reference.
         */
        abstract void generateDataConsReference(HTMLBuilder builder, ScopedEntityReference reference);
        
        /**
         * Generates HTML for a function or class method cross-reference.
         * @param builder the HTMLBuilder to use for generating the cross-reference.
         * @param reference the function or class method cross-reference.
         */
        abstract void generateFunctionOrClassMethodReference(HTMLBuilder builder, ScopedEntityReference reference);
        
        /**
         * Generates HTML for a type class cross-reference.
         * @param builder the HTMLBuilder to use for generating the cross-reference.
         * @param reference the type class cross-reference.
         */
        abstract void generateTypeClassReference(HTMLBuilder builder, ScopedEntityReference reference);
    }
    
    /**
     * Abstract base class for a generator capable of generating appropriate HTML text for cross-references.
     *
     * @author Joseph Wong
     */
    public static abstract class CrossReferenceHTMLStringGenerator extends CrossReferenceHTMLGenerator {
        
        /**
         * {@inheritDoc}
         */
        @Override
        final void generateModuleReference(final HTMLBuilder builder, final ModuleReference reference) {
            builder.addText(getModuleReferenceHTML(reference));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        final void generateTypeConsReference(final HTMLBuilder builder, final ScopedEntityReference reference) {
            builder.addText(getTypeConsReferenceHTML(reference));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        final void generateDataConsReference(final HTMLBuilder builder, final ScopedEntityReference reference) {
            builder.addText(getDataConsReferenceHTML(reference));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        final void generateFunctionOrClassMethodReference(final HTMLBuilder builder, final ScopedEntityReference reference) {
            builder.addText(getFunctionOrClassMethodReferenceHTML(reference));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        final void generateTypeClassReference(final HTMLBuilder builder, final ScopedEntityReference reference) {
            builder.addText(getTypeClassReferenceHTML(reference));
        }

        /**
         * Generates HTML for a module cross-reference.
         * @param reference the module cross-reference.
         * @return the appropriate HTML text.
         */
        public abstract String getModuleReferenceHTML(ModuleReference reference);

        /**
         * Generates HTML for a type constructor cross-reference.
         * @param reference the type constructor cross-reference.
         * @return the appropriate HTML text.
         */
        public abstract String getTypeConsReferenceHTML(ScopedEntityReference reference);

        /**
         * Generates HTML for a data constructor cross-reference.
         * @param reference the data constructor cross-reference.
         * @return the appropriate HTML text.
         */
        public abstract String getDataConsReferenceHTML(ScopedEntityReference reference);

        /**
         * Generates HTML for a function or class method cross-reference.
         * @param reference the function or class method cross-reference.
         * @return the appropriate HTML text.
         */
        public abstract String getFunctionOrClassMethodReferenceHTML(ScopedEntityReference reference);

        /**
         * Generates HTML for a type class cross-reference.
         * @param reference the type class cross-reference.
         * @return the appropriate HTML text.
         */
        public abstract String getTypeClassReferenceHTML(ScopedEntityReference reference);
    }
    
    /**
     * Abstract base class for a CrossReferenceHTMLGenerator implementation that is built to handle cross-references
     * expressed as CALFeatureNames.
     *
     * @author Joseph Wong
     */
    public static abstract class CALFeatureCrossReferenceGenerator extends CrossReferenceHTMLGenerator {

        /**
         * {@inheritDoc}
         */
        @Override
        final void generateModuleReference(final HTMLBuilder builder, final ModuleReference reference) {
            builder.addText(getRelatedFeatureLinkHTML(CALFeatureName.getModuleFeatureName(reference.getName()), reference.getModuleNameInSource()));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        final void generateTypeConsReference(final HTMLBuilder builder, final ScopedEntityReference reference) {
            builder.addText(getRelatedFeatureLinkHTML(CALFeatureName.getTypeConstructorFeatureName(reference.getName()), reference.getModuleNameInSource()));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        final void generateDataConsReference(final HTMLBuilder builder, final ScopedEntityReference reference) {
            builder.addText(getRelatedFeatureLinkHTML(CALFeatureName.getDataConstructorFeatureName(reference.getName()), reference.getModuleNameInSource()));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        final void generateFunctionOrClassMethodReference(final HTMLBuilder builder, final ScopedEntityReference reference) {
            final QualifiedName qualifiedName = reference.getName();
            
            CALFeatureName featureName;
            if (isClassMethodName(qualifiedName)) {
                featureName = CALFeatureName.getClassMethodFeatureName(qualifiedName);
            } else {
                featureName = CALFeatureName.getFunctionFeatureName(qualifiedName);
            }
            builder.addText(getRelatedFeatureLinkHTML(featureName, reference.getModuleNameInSource()));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        final void generateTypeClassReference(final HTMLBuilder builder, final ScopedEntityReference reference) {
            builder.addText(getRelatedFeatureLinkHTML(CALFeatureName.getTypeClassFeatureName(reference.getName()), reference.getModuleNameInSource()));
        }
        
        /**
         * Returns whether the given qualified name is a class method name.
         * @param qualifiedName the name to check.
         * @return true if the given qualified name is a class method name, false otherwise.
         */
        public abstract boolean isClassMethodName(QualifiedName qualifiedName);
        
        /**
         * Builds a well-formed HTML fragment for the cross-reference, given here as a CALFeatureName.
         * @param featureName the cross-reference.
         * @param moduleNameInSource how the module name portion of the reference appears in source. Could be the empty string if the reference is unqualified in source.
         * @return the HTML fragment for the cross-reference.
         */
        public abstract String getRelatedFeatureLinkHTML(CALFeatureName featureName, String moduleNameInSource);
    }
    
    /**
     * Implements a traverser which traverses through a CALDoc text block and generates the corresponding HTML for it.
     *
     * @author Joseph Wong
     */
    private static final class TextBlockHTMLGenerator extends CALDocCommentTextBlockTraverser<TextBlockHTMLGenerator.VisitationOption, Void> {

        /**
         * The HTMLBuilder to use for generating the HTML.
         */
        private final HTMLBuilder builder;
        
        /**
         * The cross-reference generator to use for generating HTML for cross-references.
         */
        private final CrossReferenceHTMLGenerator crossRefGen;
        
        /**
         * The HTML attribute to use for elements that represent "@code" blocks in the text block.
         */
        private final HTMLBuilder.AttributeList codeStyleClassAttribute;
        
        /**
         * The HTML tag to use for formatting a code fragment. Should be either HTML.Tag.TT or HTML.Tag.CODE.
         */
        private final HTML.Tag codeFormattingTag;
        
        /**
         * The nesting level of "@code"/"@link" blocks which require code formatting. It starts out at 0.
         */
        private int codeFormattingLevel = 0;

        /** An enumeration representing the various visitation options. */
        private static enum VisitationOption {
            /**
             * Visitation argument value for suppressing the initial paragraph tag in a block of paragraphs during generation.
             */
            SUPPRESS_INITIAL_PARAGRAPH_TAG,

            /**
             * Visitation argument value for suppressing the initial paragraph tag in a block of paragraphs
             * and trimming one leading whitespace character during generation.
             */
            SUPPRESS_INITIAL_PARAGRAPH_TAG_AND_TRIM_LEADING_WHITESPACE_CHAR,

            /**
             * Visitation argument value for trimming one leading whitespace character during generation.
             */
            TRIM_LEADING_WHITESPACE_CHAR_IN_SEGMENT,

            /**
             * Visitation argument value for indicating that the segment being generated is the last one in a paragraph whose paragraph
             * tags are to be suppressed.
             */
            LAST_SEGMENT_IN_SUPPRESSED_PARAGRAPH
        }

        /**
         * Implements a text block traverser which determines whether a text block spans more than one source line.
         *
         * @author Joseph Wong
         */
        private static final class MultipleLinesChecker extends CALDocCommentTextBlockTraverser<Void, Void> {
            
            /**
             * Keeps track of whether the text block being traversed spans more than one source line.
             */
            private boolean spansMultipleLines = false;
            
            /**
             * Determines whether the text block being traversed spans more than one source line.
             * @param block the text block to be traversed.
             * @param arg unused argument.
             * @return null.
             */
            @Override
            public Void visitTextBlock(final TextBlock block, final Void arg) {
                if (block.getNParagraphs() > 1) {
                    spansMultipleLines = true;
                } else {
                    // only traverse into the block to check if there is just zero or one paragraph in the block
                    super.visitTextBlock(block, arg);
                }
                
                return null;
            }
            
            /**
             * Determines whether the plain text segment being traversed spans more than one source line.
             * @param segment the segment to be traversed.
             * @param arg unused argument.
             * @return null.
             */
            @Override
            public Void visitPlainTextSegment(final PlainTextSegment segment, final Void arg) {
                if (segment.getText().indexOf('\n') >= 0 || segment.getText().indexOf('\r') >= 0) {
                    spansMultipleLines = true;
                }
                
                return super.visitPlainTextSegment(segment, arg);
            }
        }
        
        /**
         * Constructs a TextBlockHTMLGenerator for generating HTML for a text block.
         * @param builder the HTMLBuilder to use for generating the HTML.
         * @param crossRefGen the cross-reference generator to use for generating HTML for cross-references.
         * @param codeStyleClass the style class to use for HTML elements that represent "@code" blocks in the text block. Can be null.
         * @param codeFormattingTag the HTML tag to use for formatting a code fragment. Should be either HTML.Tag.TT or HTML.Tag.CODE.
         */
        private TextBlockHTMLGenerator(final HTMLBuilder builder, final CrossReferenceHTMLGenerator crossRefGen, final StyleClass codeStyleClass, final HTML.Tag codeFormattingTag) {
            if (builder == null) {
                throw new NullPointerException();
            }
            this.builder = builder;
            
            if (crossRefGen == null) {
                throw new NullPointerException();
            }
            this.crossRefGen = crossRefGen;

            if (codeStyleClass == null) {
                this.codeStyleClassAttribute = HTMLBuilder.AttributeList.make();
            } else {
                this.codeStyleClassAttribute = HTMLBuilder.AttributeList.make(HTML.Attribute.CLASS, codeStyleClass.toHTML());
            }
            
            if (codeFormattingTag == null) {
                throw new NullPointerException();
            }
            this.codeFormattingTag = codeFormattingTag;
        }
        
        /**
         * Generates HTML for the given text block.
         * @param block the text block to be traversed.
         * @param arg additional argument for the traversal.
         * @return null.
         */
        @Override
        public Void visitTextBlock(final TextBlock block, final VisitationOption arg) {
            for (int i = 0, n = block.getNParagraphs(); i < n; i++) {
                final boolean isFirstSegment = (i == 0);
                
                final VisitationOption paragraphArg;
                if (isFirstSegment) {
                    if (arg == VisitationOption.SUPPRESS_INITIAL_PARAGRAPH_TAG || arg == VisitationOption.SUPPRESS_INITIAL_PARAGRAPH_TAG_AND_TRIM_LEADING_WHITESPACE_CHAR) {
                        paragraphArg = arg;
                    } else {
                        paragraphArg = VisitationOption.SUPPRESS_INITIAL_PARAGRAPH_TAG;
                    }
                } else {
                    paragraphArg = null;
                }
                
                block.getNthParagraph(i).accept(this, paragraphArg);
            }
            
            return null;
        }
        
        /**
         * Generates HTML for the given text paragraph.
         * @param paragraph the text paragraph to be traversed.
         * @param arg additional argument for the traversal.
         * @return null.
         */
        @Override
        public Void visitTextParagraph(final TextParagraph paragraph, final VisitationOption arg) {
            boolean suppressParagraphTag = (arg == VisitationOption.SUPPRESS_INITIAL_PARAGRAPH_TAG || arg == VisitationOption.SUPPRESS_INITIAL_PARAGRAPH_TAG_AND_TRIM_LEADING_WHITESPACE_CHAR);
            
            if (!suppressParagraphTag) {
                builder.emptyTag(HTML.Tag.P);
            }
            
            for (int i = 0, n = paragraph.getNSegments(); i < n; i++) {
                final boolean isFirstSegment = (i == 0);
                final boolean isLastSegment = (i == n - 1);
                
                final VisitationOption segmentArg;
                if (isFirstSegment && arg == VisitationOption.SUPPRESS_INITIAL_PARAGRAPH_TAG_AND_TRIM_LEADING_WHITESPACE_CHAR) {
                    segmentArg = VisitationOption.TRIM_LEADING_WHITESPACE_CHAR_IN_SEGMENT;
                } else if (isLastSegment && suppressParagraphTag) {
                    segmentArg = VisitationOption.LAST_SEGMENT_IN_SUPPRESSED_PARAGRAPH;
                } else {
                    segmentArg = null;
                }
                
                paragraph.getNthSegment(i).accept(this, segmentArg);
            }
            
            return null;
        }

        /**
         * Generates HTML for the given list paragraph.
         * @param paragraph the list paragraph to be traversed.
         * @param arg additional argument for the traversal.
         * @return null.
         */
        @Override
        public Void visitListParagraph(final ListParagraph paragraph, final VisitationOption arg) {
            final HTML.Tag listTag = paragraph.isOrdered() ? HTML.Tag.OL : HTML.Tag.UL;
            
            builder.openTag(listTag);
            super.visitListParagraph(paragraph, arg);
            builder.closeTag(listTag);
            
            return null;
        }

        /**
         * Generates HTML for the given list item.
         * @param item the list item to be traversed.
         * @param arg additional argument for the traversal.
         * @return null.
         */
        @Override
        public Void visitListItem(final ListItem item, final VisitationOption arg) {
            builder.openTag(HTML.Tag.LI);
            super.visitListItem(item, VisitationOption.SUPPRESS_INITIAL_PARAGRAPH_TAG_AND_TRIM_LEADING_WHITESPACE_CHAR);
            builder.closeTag(HTML.Tag.LI);
            
            return null;
        }

        /**
         * Generates HTML for the given plain text segment.
         * @param segment the segment to be traversed.
         * @param arg additional argument for the traversal.
         * @return null.
         */
        @Override
        public Void visitPlainTextSegment(final PlainTextSegment segment, final VisitationOption arg) {
            String text = segment.getText();
            
            if (arg == VisitationOption.TRIM_LEADING_WHITESPACE_CHAR_IN_SEGMENT) {
                if (text.length() > 0 && Character.isWhitespace(text.charAt(0))) {
                    text = text.substring(1);
                }
            } else if (arg == VisitationOption.LAST_SEGMENT_IN_SUPPRESSED_PARAGRAPH) {
                text = text.replaceAll("\\s+\\z", "");
            }
            
            builder.addText(htmlEscape(text));
            
            return super.visitPlainTextSegment(segment, arg);
        }

        /**
         * Generates HTML for the given URL segment.
         * @param segment the segment to be traversed.
         * @param arg additional argument for the traversal.
         * @return null.
         */
        @Override
        public Void visitURLSegment(final URLSegment segment, final VisitationOption arg) {
            builder.addTaggedText(HTML.Tag.A, AttributeList.make(HTML.Attribute.HREF, htmlEscape(segment.getURL())), htmlEscape(segment.getURL()));
            
            return super.visitURLSegment(segment, arg);
        }

        /**
         * Adds an open tag for code formatting.
         */
        private void startCodeFormatting() {
            startCodeFormatting(codeFormattingTag);
        }

        /**
         * Adds an open tag for code formatting.
         * @param wrapperTag the tag to use.
         */
        private void startCodeFormatting(final HTML.Tag wrapperTag) {
            if (codeFormattingLevel == 0) {
                builder.openTag(wrapperTag, codeStyleClassAttribute);
            }
            codeFormattingLevel++;
        }

        /**
         * Adds a close tag for code formatting.
         */
        private void endCodeFormatting() {
            endCodeFormatting(codeFormattingTag);
        }

        /**
         * Adds a close tag for code formatting.
         * @param wrapperTag the tag to use.
         */
        private void endCodeFormatting(final HTML.Tag wrapperTag) {
            codeFormattingLevel--;
            if (codeFormattingLevel == 0) {
                builder.closeTag(wrapperTag);
            }
        }
        
        /**
         * Generates HTML for the given inline module cross-reference.
         * @param segment the segment to be traversed.
         * @param arg additional argument for the traversal.
         * @return null.
         */
        @Override
        public Void visitModuleLinkSegment(final ModuleLinkSegment segment, final VisitationOption arg) {
            startCodeFormatting();
            crossRefGen.generateModuleReference(builder, segment.getReference());
            endCodeFormatting();
            
            return super.visitModuleLinkSegment(segment, arg);
        }

        /**
         * Generates HTML for the given inline function or class method cross-reference.
         * @param segment the segment to be traversed.
         * @param arg additional argument for the traversal.
         * @return null.
         */
        @Override
        public Void visitFunctionOrClassMethodLinkSegment(final FunctionOrClassMethodLinkSegment segment, final VisitationOption arg) {
            startCodeFormatting();
            crossRefGen.generateFunctionOrClassMethodReference(builder, segment.getReference());
            endCodeFormatting();
            
            return super.visitFunctionOrClassMethodLinkSegment(segment, arg);
        }

        /**
         * Generates HTML for the given inline type constructor cross-reference.
         * @param segment the segment to be traversed.
         * @param arg additional argument for the traversal.
         * @return null.
         */
        @Override
        public Void visitTypeConsLinkSegment(final TypeConsLinkSegment segment, final VisitationOption arg) {
            startCodeFormatting();
            crossRefGen.generateTypeConsReference(builder, segment.getReference());
            endCodeFormatting();
            
            return super.visitTypeConsLinkSegment(segment, arg);
        }

        /**
         * Generates HTML for the given inline data constructor cross-reference.
         * @param segment the segment to be traversed.
         * @param arg additional argument for the traversal.
         * @return null.
         */
        @Override
        public Void visitDataConsLinkSegment(final DataConsLinkSegment segment, final VisitationOption arg) {
            startCodeFormatting();
            crossRefGen.generateDataConsReference(builder, segment.getReference());
            endCodeFormatting();
            
            return super.visitDataConsLinkSegment(segment, arg);
        }

        /**
         * Generates HTML for the given inline type class cross-reference.
         * @param segment the segment to be traversed.
         * @param arg additional argument for the traversal.
         * @return null.
         */
        @Override
        public Void visitTypeClassLinkSegment(final TypeClassLinkSegment segment, final VisitationOption arg) {
            startCodeFormatting();
            crossRefGen.generateTypeClassReference(builder, segment.getReference());
            endCodeFormatting();
            
            return super.visitTypeClassLinkSegment(segment, arg);
        }

        /**
         * Generates HTML for the given code segment.
         * @param segment the segment to be traversed.
         * @param arg additional argument for the traversal.
         * @return null.
         */
        @Override
        public Void visitCodeSegment(final CodeSegment segment, final VisitationOption arg) {
            final MultipleLinesChecker multipleLinesChecker = new MultipleLinesChecker();
            segment.accept(multipleLinesChecker, null);
            
            final HTML.Tag wrapperTag = multipleLinesChecker.spansMultipleLines ? HTML.Tag.PRE : codeFormattingTag;
            
            if (multipleLinesChecker.spansMultipleLines) {
                builder.newline();
            }
            
            startCodeFormatting(wrapperTag);
            super.visitCodeSegment(segment, VisitationOption.SUPPRESS_INITIAL_PARAGRAPH_TAG_AND_TRIM_LEADING_WHITESPACE_CHAR);
            endCodeFormatting(wrapperTag);
            
            return null;
        }

        /**
         * Generates HTML for the given emphasized segment.
         * @param segment the segment to be traversed.
         * @param arg additional argument for the traversal.
         * @return null.
         */
        @Override
        public Void visitEmphasizedSegment(final EmphasizedSegment segment, final VisitationOption arg) {
            builder.openTag(HTML.Tag.EM);
            super.visitEmphasizedSegment(segment, VisitationOption.SUPPRESS_INITIAL_PARAGRAPH_TAG_AND_TRIM_LEADING_WHITESPACE_CHAR);
            builder.closeTag(HTML.Tag.EM);
            
            return null;
        }

        /**
         * Generates HTML for the given strongly emphasized segment.
         * @param segment the segment to be traversed.
         * @param arg additional argument for the traversal.
         * @return null.
         */
        @Override
        public Void visitStronglyEmphasizedSegment(final StronglyEmphasizedSegment segment, final VisitationOption arg) {
            builder.openTag(HTML.Tag.STRONG);
            super.visitStronglyEmphasizedSegment(segment, VisitationOption.SUPPRESS_INITIAL_PARAGRAPH_TAG_AND_TRIM_LEADING_WHITESPACE_CHAR);
            builder.closeTag(HTML.Tag.STRONG);
            
            return null;
        }

        /**
         * Generates HTML for the given superscript segment.
         * @param segment the segment to be traversed.
         * @param arg additional argument for the traversal.
         * @return null.
         */
        @Override
        public Void visitSuperscriptSegment(final SuperscriptSegment segment, final VisitationOption arg) {
            builder.openTag(HTML.Tag.SUP);
            super.visitSuperscriptSegment(segment, VisitationOption.SUPPRESS_INITIAL_PARAGRAPH_TAG_AND_TRIM_LEADING_WHITESPACE_CHAR);
            builder.closeTag(HTML.Tag.SUP);
            
            return null;
        }

        /**
         * Generates HTML for the given superscript segment.
         * @param segment the segment to be traversed.
         * @param arg additional argument for the traversal.
         * @return null.
         */
        @Override
        public Void visitSubscriptSegment(final SubscriptSegment segment, final VisitationOption arg) {
            builder.openTag(HTML.Tag.SUB);
            super.visitSubscriptSegment(segment, VisitationOption.SUPPRESS_INITIAL_PARAGRAPH_TAG_AND_TRIM_LEADING_WHITESPACE_CHAR);
            builder.closeTag(HTML.Tag.SUB);
            
            return null;
        }
    }
    
    /** Private constructor. */
    private CALDocToHTMLUtilities() {}
    
    /**
     * Generates the HTML for a text block appearing in a CALDoc comment.
     * @param textBlock the text block to be formatted as HTML.
     * @param crossRefGen the cross reference generator.
     * @return the HTML for the text block.
     */
    public static String getHTMLForCALDocTextBlock(final CALDocComment.TextBlock textBlock, final CrossReferenceHTMLGenerator crossRefGen) {
        // specifies 'null' for the style class of code blocks and HTML.Tag.CODE for the tag
        final StyleClass codeStyleClass = null;
        final HTML.Tag codeFormattingTag = HTML.Tag.CODE;
        return getHTMLForCALDocTextBlock(textBlock, crossRefGen, codeStyleClass, codeFormattingTag);
    }
    
    /**
     * Generates the HTML for a text block appearing in a CALDoc comment.
     * @param textBlock the text block to be formatted as HTML.
     * @param crossRefGen the cross reference generator.
     * @param codeStyleClass the style class to use for code blocks. Can be null.
     * @param codeFormattingTag the HTML tag to use for formatting a code fragment. Should be either HTML.Tag.TT or HTML.Tag.CODE.
     * @return the HTML for the text block.
     */
    static String getHTMLForCALDocTextBlock(final CALDocComment.TextBlock textBlock, final CrossReferenceHTMLGenerator crossRefGen, final StyleClass codeStyleClass, final HTML.Tag codeFormattingTag) {
        final HTMLBuilder builder = new HTMLBuilder();
        generateHTMLForCALDocTextBlock(textBlock, builder, crossRefGen, codeStyleClass, codeFormattingTag);
        return builder.toString();
    }
    
    /**
     * Generates the HTML for a text block appearing in a CALDoc comment.
     * @param textBlock the text block to be formatted as HTML.
     * @param builder the HTML builder for aggregating the result.
     * @param crossRefGen the cross reference generator.
     * @param codeStyleClass the style class to use for code blocks. Can be null.
     * @param codeFormattingTag the HTML tag to use for formatting a code fragment. Should be either HTML.Tag.TT or HTML.Tag.CODE.
     */
    static void generateHTMLForCALDocTextBlock(final CALDocComment.TextBlock textBlock, final HTMLBuilder builder, final CrossReferenceHTMLGenerator crossRefGen, final StyleClass codeStyleClass, final HTML.Tag codeFormattingTag) {
        textBlock.accept(new TextBlockHTMLGenerator(builder, crossRefGen, codeStyleClass, codeFormattingTag), null);
    }
    
    /**
     * Generates the HTML for a paragraph appearing in a CALDoc comment.
     * @param paragraph the paragraph to be formatted as HTML.
     * @param builder the HTML builder for aggregating the result.
     * @param crossRefGen the cross reference generator.
     * @param codeStyleClass the style class to use for code blocks. Can be null.
     * @param codeFormattingTag the HTML tag to use for formatting a code fragment. Should be either HTML.Tag.TT or HTML.Tag.CODE.
     */
    static void generateHTMLForCALDocParagraph(final CALDocComment.Paragraph paragraph, final HTMLBuilder builder, final CrossReferenceHTMLGenerator crossRefGen, final StyleClass codeStyleClass, final HTML.Tag codeFormattingTag) {
        paragraph.accept(new TextBlockHTMLGenerator(builder, crossRefGen, codeStyleClass, codeFormattingTag), TextBlockHTMLGenerator.VisitationOption.SUPPRESS_INITIAL_PARAGRAPH_TAG);
    }
    
    /**
     * Generates the HTML for a segment appearing in a CALDoc comment.
     * @param segment the segment to be formatted as HTML.
     * @param builder the HTML builder for aggregating the result.
     * @param crossRefGen the cross reference generator.
     * @param codeStyleClass the style class to use for code blocks. Can be null.
     * @param codeFormattingTag the HTML tag to use for formatting a code fragment. Should be either HTML.Tag.TT or HTML.Tag.CODE.
     */
    static void generateHTMLForCALDocSegment(final CALDocComment.Segment segment, final HTMLBuilder builder, final CrossReferenceHTMLGenerator crossRefGen, final StyleClass codeStyleClass, final HTML.Tag codeFormattingTag) {
        segment.accept(new TextBlockHTMLGenerator(builder, crossRefGen, codeStyleClass, codeFormattingTag), null);
    }
    
    /**
     * Builds the HTML for the summary of a CALDoc comment.
     * @param docComment the CALDoc comment.
     * @param crossRefGen the cross reference generator.
     * @param locale the locale for the documentation generation.
     * @param returnsColonPrefix the appropriately localized version of "Returns:". 
     * @return the HTML for the summary of a CALDoc comment.
     */
    public static String getHTMLForCALDocSummary(final CALDocComment docComment, final CrossReferenceHTMLGenerator crossRefGen, final Locale locale, final String returnsColonPrefix) {
        // specifies 'null' for the style class of code blocks and HTML.Tag.CODE for the tag
        final StyleClass codeStyleClass = null;
        final HTML.Tag codeFormattingTag = HTML.Tag.CODE;
        return getHTMLForCALDocSummary(docComment, crossRefGen, locale, returnsColonPrefix, codeStyleClass, codeFormattingTag);
    }

    /**
     * Builds the HTML for the summary of a CALDoc comment.
     * @param docComment the CALDoc comment.
     * @param crossRefGen the cross reference generator.
     * @param locale the locale for the documentation generation.
     * @param returnsColonPrefix the appropriately localized version of "Returns:". 
     * @param codeStyleClass the style class to use for code blocks. Can be null.
     * @param codeFormattingTag the HTML tag to use for formatting a code fragment. Should be either HTML.Tag.TT or HTML.Tag.CODE.
     * @return the HTML for the summary of a CALDoc comment.
     */
    static String getHTMLForCALDocSummary(final CALDocComment docComment, final CrossReferenceHTMLGenerator crossRefGen, final Locale locale, final String returnsColonPrefix, final StyleClass codeStyleClass, final HTML.Tag codeFormattingTag) {
        final ContentConvertibleToHTML summaryFromCALDoc = getSummaryFromCALDoc(docComment, crossRefGen, locale, returnsColonPrefix, codeStyleClass, codeFormattingTag);
        
        if (summaryFromCALDoc != null) {
            final HTMLBuilder builder = new HTMLBuilder();
            summaryFromCALDoc.generateHTML(builder, crossRefGen);
            return builder.toString();
        } else {
            return ""; // there is no summary, so the valid HTML for no summary is the empty string.
        }
    }

    /**
     * Fetches the summary (short description) from a CALDoc comment.
     * @param docComment the CALDoc comment to be extracted. Can be null.
     * @param referenceGenerator the reference generator to use for generating cross references.
     * @param locale the locale to use for obtaining the appropriate BreakIterator.
     * @param returnsColonPrefix the appropriately localized version of "Returns:". 
     * @param codeStyleClass the style class to use for code blocks. Can be null.
     * @param codeFormattingTag the HTML tag to use for formatting a code fragment. Should be either HTML.Tag.TT or HTML.Tag.CODE.
     * @return the summary, or null if none is available.
     */
    static ContentConvertibleToHTML getSummaryFromCALDoc(final CALDocComment docComment, final CrossReferenceHTMLGenerator referenceGenerator, final Locale locale, final String returnsColonPrefix, final StyleClass codeStyleClass, final HTML.Tag codeFormattingTag) {
        /// If there is no comment or if it has no main description, return null
        //
        if (docComment == null) {
            return null;
        }
        
        /// If there is an {@summary} block, use it.
        //
        final CALDocComment.TextBlock summaryBlock = docComment.getSummary();
        if (summaryBlock != null) {
            return new SingleTextBlockContent(summaryBlock, codeStyleClass, codeFormattingTag);
        }
        
        /// There is no {@summary} block, so try using the first sentence as the summary.
        //
        final CALDocComment.TextBlock textBlock = docComment.getDescriptionBlock();
        if (textBlock != null) {
            final ContentConvertibleToHTML firstSentence = getFirstSentenceFromCALDocTextBlock(textBlock, locale, codeStyleClass, codeFormattingTag);
            
            if (firstSentence != null && !firstSentence.isEmpty()) {
                return firstSentence;
            }
        }
        
        /// There is no available first sentence, so use the return block instead.
        //
        final CALDocComment.TextBlock returnBlock = docComment.getReturnBlock();
        if (returnBlock != null) {
            final String returnBlockHTML = getHTMLForCALDocTextBlock(returnBlock, referenceGenerator, codeStyleClass, codeFormattingTag);
            
            if (returnBlockHTML != null && returnBlockHTML.length() > 0) {
                return new SimpleStringContent(returnsColonPrefix + " " + returnBlockHTML);
            }
        }

        /// Absolutely nothing available, so we return null as per the spec.
        //
        return null;
    }

    /**
     * Obtains the first sentence from a CALDoc text block.
     * @param textBlock the text block. Can be null.
     * @param locale the locale to use for obtaining the appropriate BreakIterator.
     * @param codeStyleClass the style class to use for code blocks. Can be null.
     * @param codeFormattingTag the HTML tag to use for formatting a code fragment. Should be either HTML.Tag.TT or HTML.Tag.CODE.
     * @return the first sentence, or null if none is available.
     */
    static ContentConvertibleToHTML getFirstSentenceFromCALDocTextBlock(final CALDocComment.TextBlock textBlock, final Locale locale, final StyleClass codeStyleClass, final HTML.Tag codeFormattingTag) {
        if (textBlock == null) {
            return null;
        }
        
        if (textBlock.getNParagraphs() == 0) {
            return null;
        }
        
        /// Set up a BreakIterator to parse the text and identify the first sentence.
        //
        BreakIterator breakIterator;
        if (LocaleUtilities.isInvariantLocale(locale)) {
            breakIterator = BreakIterator.getSentenceInstance();
        } else {
            breakIterator = BreakIterator.getSentenceInstance(locale);
        }
        
        /// If the first paragraph is a text paragraph, then try to identify which one contains the first "sentence break".
        //
        final CALDocComment.Paragraph paragraph = textBlock.getNthParagraph(0);
        if (paragraph instanceof CALDocComment.TextParagraph) {
            final CALDocComment.TextParagraph textParagraph = (CALDocComment.TextParagraph)paragraph;
            
            for (int i = 0, n = textParagraph.getNSegments(); i < n; i++) {
                final CALDocComment.Segment segment = textParagraph.getNthSegment(i);
                
                if (segment instanceof CALDocComment.PlainTextSegment) {
                    final CALDocComment.PlainTextSegment plainTextSegment = (CALDocComment.PlainTextSegment)segment;
                    
                    // Pad the string out with some whitespace at the end, so that if the break iterator returns
                    // the end of string as the boundary, we know that the original string could not have contained the
                    // boundary (or otherwise the boundary would've occurred before the end of the padded string).
                    final String text = plainTextSegment.getText() + "   ";
                    
                    breakIterator.setText(text);
                    final int firstSentenceBoundary = breakIterator.next();
                    
                    if (firstSentenceBoundary != BreakIterator.DONE && firstSentenceBoundary < text.length()) {
                        return new TextParagraphInitialSegments(textParagraph, i, text.substring(0, firstSentenceBoundary), codeStyleClass, codeFormattingTag);
                    }
                }
            }
        }
        
        // it's either not a text paragraph, or a "first sentence" could not be found, so just use the entire paragraph
        return new SingleParagraphContent(paragraph, codeStyleClass, codeFormattingTag);
    }

    /**
     * Escapes the text into proper HTML, converting characters into character entities when required.
     * @param text the text to be escaped.
     * @return the escaped text.
     */
    static String htmlEscape(final String text) {
        return text
            .replaceAll("&", "&amp;") // replace the ampersand first because it will show up later as part of character entities
            .replaceAll("<", "&lt;")
            .replaceAll(">", "&gt;");
    }
}
