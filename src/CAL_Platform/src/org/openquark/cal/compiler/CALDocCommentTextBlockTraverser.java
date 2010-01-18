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
 * CALDocCommentTextBlockTraverser.java
 * Creation date: Nov 8, 2005.
 * By: Joseph Wong
 */
package org.openquark.cal.compiler;

import org.openquark.cal.compiler.CALDocComment.CodeSegment;
import org.openquark.cal.compiler.CALDocComment.DataConsLinkSegment;
import org.openquark.cal.compiler.CALDocComment.EmphasizedSegment;
import org.openquark.cal.compiler.CALDocComment.FunctionOrClassMethodLinkSegment;
import org.openquark.cal.compiler.CALDocComment.ListItem;
import org.openquark.cal.compiler.CALDocComment.ListParagraph;
import org.openquark.cal.compiler.CALDocComment.ModuleLinkSegment;
import org.openquark.cal.compiler.CALDocComment.PlainTextSegment;
import org.openquark.cal.compiler.CALDocComment.StronglyEmphasizedSegment;
import org.openquark.cal.compiler.CALDocComment.SubscriptSegment;
import org.openquark.cal.compiler.CALDocComment.SuperscriptSegment;
import org.openquark.cal.compiler.CALDocComment.TextBlock;
import org.openquark.cal.compiler.CALDocComment.TextParagraph;
import org.openquark.cal.compiler.CALDocComment.TypeClassLinkSegment;
import org.openquark.cal.compiler.CALDocComment.TypeConsLinkSegment;
import org.openquark.cal.compiler.CALDocComment.URLSegment;

/**
 * CALDocCommentTextBlockTraverser is an implementation of the CALDocCommentTextBlockVisitor which
 * performs a pre-order traversal of the text block elements it visits.
 * This class is intended to be the base class of other visitors that need to
 * traverse a text block, or parts thereof.
 * <p>
 * 
 * In CALDocCommentTextBlockTraverser, the argument supplied to the visit methods are
 * ignored, and all methods return null as their return values. Subclasses of
 * CALDocCommentTextBlockTraverser are free to use the argument and return value for their
 * own purposes, and the traversal logic in CALDocCommentTextBlockTraverser will properly
 * propagate the supplied arguments down to child elements.
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
 * @author Joseph Wong
 */
public class CALDocCommentTextBlockTraverser<T, R> implements CALDocCommentTextBlockVisitor<T, R> {
    
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
     * @param block the text block to be traversed.
     * @param arg additional argument for the traversal.
     * @return the result from the traversal.
     */
    public R visitTextBlock(final TextBlock block, final T arg) {
        verifyArg(block, "block");
        
        for (int i = 0, n = block.getNParagraphs(); i < n; i++) {
            block.getNthParagraph(i).accept(this, arg);
        }
        
        return null;
    }

    /**
     * @param paragraph the text paragraph to be traversed.
     * @param arg additional argument for the traversal.
     * @return the result from the traversal.
     */
    public R visitTextParagraph(final TextParagraph paragraph, final T arg) {
        verifyArg(paragraph, "paragraph");
        
        for (int i = 0, n = paragraph.getNSegments(); i < n; i++) {
            paragraph.getNthSegment(i).accept(this, arg);
        }
        
        return null;
    }

    /**
     * @param paragraph the list paragraph to be traversed.
     * @param arg additional argument for the traversal.
     * @return the result from the traversal.
     */
    public R visitListParagraph(final ListParagraph paragraph, final T arg) {
        verifyArg(paragraph, "paragraph");
        
        for (int i = 0, n = paragraph.getNItems(); i < n; i++) {
            paragraph.getNthItem(i).accept(this, arg);
        }
        
        return null;
    }

    /**
     * @param item the list item to be traversed.
     * @param arg additional argument for the traversal.
     * @return the result from the traversal.
     */
    public R visitListItem(final ListItem item, final T arg) {
        verifyArg(item, "item");
        
        item.getContent().accept(this, arg);
        
        return null;
    }

    /**
     * @param segment the segment to be traversed.
     * @param arg additional argument for the traversal.
     * @return the result from the traversal.
     */
    public R visitPlainTextSegment(final PlainTextSegment segment, final T arg) {
        verifyArg(segment, "segment");
        
        // no children to traverse
        return null;
    }

    /**
     * @param segment the segment to be traversed.
     * @param arg additional argument for the traversal.
     * @return the result from the traversal.
     */
    public R visitURLSegment(final URLSegment segment, final T arg) {
        verifyArg(segment, "segment");
        
        // no children to traverse
        return null;
    }

    /**
     * @param segment the segment to be traversed.
     * @param arg additional argument for the traversal.
     * @return the result from the traversal.
     */
    public R visitModuleLinkSegment(final ModuleLinkSegment segment, final T arg) {
        verifyArg(segment, "segment");
        
        // no children to traverse
        return null;
    }

    /**
     * @param segment the segment to be traversed.
     * @param arg additional argument for the traversal.
     * @return the result from the traversal.
     */
    public R visitFunctionOrClassMethodLinkSegment(final FunctionOrClassMethodLinkSegment segment, final T arg) {
        verifyArg(segment, "segment");
        
        // no children to traverse
        return null;
    }

    /**
     * @param segment the segment to be traversed.
     * @param arg additional argument for the traversal.
     * @return the result from the traversal.
     */
    public R visitTypeConsLinkSegment(final TypeConsLinkSegment segment, final T arg) {
        verifyArg(segment, "segment");
        
        // no children to traverse
        return null;
    }

    /**
     * @param segment the segment to be traversed.
     * @param arg additional argument for the traversal.
     * @return the result from the traversal.
     */
    public R visitDataConsLinkSegment(final DataConsLinkSegment segment, final T arg) {
        verifyArg(segment, "segment");
        
        // no children to traverse
        return null;
    }

    /**
     * @param segment the segment to be traversed.
     * @param arg additional argument for the traversal.
     * @return the result from the traversal.
     */
    public R visitTypeClassLinkSegment(final TypeClassLinkSegment segment, final T arg) {
        verifyArg(segment, "segment");
        
        // no children to traverse
        return null;
    }

    /**
     * @param segment the segment to be traversed.
     * @param arg additional argument for the traversal.
     * @return the result from the traversal.
     */
    public R visitCodeSegment(final CodeSegment segment, final T arg) {
        verifyArg(segment, "segment");
        
        segment.getContent().accept(this, arg);
        
        return null;
    }

    /**
     * @param segment the segment to be traversed.
     * @param arg additional argument for the traversal.
     * @return the result from the traversal.
     */
    public R visitEmphasizedSegment(final EmphasizedSegment segment, final T arg) {
        verifyArg(segment, "segment");
        
        segment.getContent().accept(this, arg);
        
        return null;
    }

    /**
     * @param segment the segment to be traversed.
     * @param arg additional argument for the traversal.
     * @return the result from the traversal.
     */
    public R visitStronglyEmphasizedSegment(final StronglyEmphasizedSegment segment, final T arg) {
        verifyArg(segment, "segment");
        
        segment.getContent().accept(this, arg);
        
        return null;
    }

    /**
     * @param segment the segment to be traversed.
     * @param arg additional argument for the traversal.
     * @return the result from the traversal.
     */
    public R visitSuperscriptSegment(final SuperscriptSegment segment, final T arg) {
        verifyArg(segment, "segment");
        
        segment.getContent().accept(this, arg);
        
        return null;
    }

    /**
     * @param segment the segment to be traversed.
     * @param arg additional argument for the traversal.
     * @return the result from the traversal.
     */
    public R visitSubscriptSegment(final SubscriptSegment segment, final T arg) {
        verifyArg(segment, "segment");
        
        segment.getContent().accept(this, arg);
        
        return null;
    }
}
