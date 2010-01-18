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
 * CALDocCommentTextBlockVisitor.java
 * Creation date: Nov 7, 2005.
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
 * A visitor interface for visiting elements of a text block in a CALDoc comment.
 * <p>
 * 
 * The visitor mechanism supported by this interface is more general than the
 * regular visitor pattern. In particular, each visit method boasts an argument
 * of the generic type java.lang.Object, and also a return type of
 * java.lang.Object. This allows additional arguments, suitably encapsulated in
 * an object, to be passed in to any visit method. The visit methods are also
 * able to return values, which is useful for cases where the visitor needs to
 * aggregate results in a hierarchical fashion from visiting the tree of text
 * block elements.
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
 * While it is certainly possible to directly implement the CALDocCommentTextBlockVisitor
 * interface, it may be easier to subclass from one of the predefined visitor
 * classes. To build a visitor which traverses a text block, the
 * <code>CALDocCommentTextBlockTraverser</code> class would be an appropriate base class
 * to subclass, since it provides a default pre-order traversal logic for all
 * text block elements.
 * <p>
 * 
 * If the CALDoc comment structure is changed (e.g. when new element
 * classes are added, or when existing element classes are moved around in the
 * inheritance and/or containment hierarchy), then this visitor interface and
 * all implementors must be updated. This may include renaming existing
 * interface methods if element classes have been moved.
 * <p>
 * 
 * @param <T> the argument type. If the visitation argument is not used, specify {@link Void}.
 * @param <R> the return type. If the return value is not used, specify {@link Void}.
 * 
 * @see org.openquark.cal.compiler.CALDocCommentTextBlockTraverser
 * 
 * @author Joseph Wong
 */
public interface CALDocCommentTextBlockVisitor<T, R> {

    /**
     * @param block the text block to be visited.
     * @param arg additional argument for the visitation.
     * @return the result from the visitation.
     */
    public R visitTextBlock(TextBlock block, T arg);

    /**
     * @param paragraph the text paragraph to be visited.
     * @param arg additional argument for the visitation.
     * @return the result from the visitation.
     */
    public R visitTextParagraph(TextParagraph paragraph, T arg);

    /**
     * @param paragraph the list paragraph to be visited.
     * @param arg additional argument for the visitation.
     * @return the result from the visitation.
     */
    public R visitListParagraph(ListParagraph paragraph, T arg);

    /**
     * @param item the list item to be visited.
     * @param arg additional argument for the visitation.
     * @return the result from the visitation.
     */
    public R visitListItem(ListItem item, T arg);

    /**
     * @param segment the segment to be visited.
     * @param arg additional argument for the visitation.
     * @return the result from the visitation.
     */
    public R visitPlainTextSegment(PlainTextSegment segment, T arg);

    /**
     * @param segment the segment to be visited.
     * @param arg additional argument for the visitation.
     * @return the result from the visitation.
     */
    public R visitURLSegment(URLSegment segment, T arg);

    /**
     * @param segment the segment to be visited.
     * @param arg additional argument for the visitation.
     * @return the result from the visitation.
     */
    public R visitModuleLinkSegment(ModuleLinkSegment segment, T arg);

    /**
     * @param segment the segment to be visited.
     * @param arg additional argument for the visitation.
     * @return the result from the visitation.
     */
    public R visitFunctionOrClassMethodLinkSegment(FunctionOrClassMethodLinkSegment segment, T arg);

    /**
     * @param segment the segment to be visited.
     * @param arg additional argument for the visitation.
     * @return the result from the visitation.
     */
    public R visitTypeConsLinkSegment(TypeConsLinkSegment segment, T arg);

    /**
     * @param segment the segment to be visited.
     * @param arg additional argument for the visitation.
     * @return the result from the visitation.
     */
    public R visitDataConsLinkSegment(DataConsLinkSegment segment, T arg);

    /**
     * @param segment the segment to be visited.
     * @param arg additional argument for the visitation.
     * @return the result from the visitation.
     */
    public R visitTypeClassLinkSegment(TypeClassLinkSegment segment, T arg);

    /**
     * @param segment the segment to be visited.
     * @param arg additional argument for the visitation.
     * @return the result from the visitation.
     */
    public R visitCodeSegment(CodeSegment segment, T arg);

    /**
     * @param segment the segment to be visited.
     * @param arg additional argument for the visitation.
     * @return the result from the visitation.
     */
    public R visitEmphasizedSegment(EmphasizedSegment segment, T arg);

    /**
     * @param segment the segment to be visited.
     * @param arg additional argument for the visitation.
     * @return the result from the visitation.
     */
    public R visitStronglyEmphasizedSegment(StronglyEmphasizedSegment segment, T arg);

    /**
     * @param segment the segment to be visited.
     * @param arg additional argument for the visitation.
     * @return the result from the visitation.
     */
    public R visitSuperscriptSegment(SuperscriptSegment segment, T arg);

    /**
     * @param segment the segment to be visited.
     * @param arg additional argument for the visitation.
     * @return the result from the visitation.
     */
    public R visitSubscriptSegment(SubscriptSegment segment, T arg);

}
