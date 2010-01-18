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
 * package-info.java
 * Creation date: Aug 16, 2004.
 * By: Edward Lam
 */

/**
 * Provides support for converting CALDoc comments to various formats (HTML, pretty-printed text, Javadoc).
 * <h2>Key classes in this package</h2>
 * <dl>
 *     <dt>{@link org.openquark.cal.caldoc.CALDocTool}</dt>
 *     <dd>
 *         This class provides both the command line interface for the HTML documentation generator, and
 *          a programmatic interface for launching the tool with various options (as represented by
 *          {@link org.openquark.cal.caldoc.HTMLDocumentationGeneratorConfiguration}).
 *     </dd>
 * </dl>
 * <dl>
 *     <dt>{@link org.openquark.cal.caldoc.CALDocToHTMLUtilities} and
 *     {@link org.openquark.cal.caldoc.CALDocToTextUtilities}</dt>
 *     <dd>
 *         These two classes provide the ability to convert CALDoc comments into HTML and pretty-printed text respectively.
 *     </dd>
 * </dl>
 */
package org.openquark.cal.caldoc;
