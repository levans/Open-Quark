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
 * Provides classes for interacting with the CAL compiler - please also see the 
 * {@link org.openquark.cal.services services} package for the 
 * top-level API for accessing the compiler's functionality.
 * <h2>Key classes in this package</h2>
 * <dl>
 *     <dt>{@link org.openquark.cal.compiler.SourceModel} and its 
 *         inner classes</dt>
 *     <dd>
 *         These classes provide a programmatic mechanism for constructing fragments of 
 *         CAL source. The compiler API provides the ability to compile new CAL modules 
 *         and to evaluate CAL expressions defined via <code>SourceModel</code>s.
 *         <p>
 *             Also available to work with <code>SourceModel</code>s are visitor classes 
 *             ({@link org.openquark.cal.compiler.SourceModelVisitor},
 *             {@link org.openquark.cal.compiler.SourceModelTraverser},
 *             {@link org.openquark.cal.compiler.SourceModelCopier}) and 
 *             utility classes ({@link org.openquark.cal.compiler.SourceModelUtilities}
 *             and its subclasses).</p>
 *     </dd>
 * </dl>
 * <dl>
 *     <dt>{@link org.openquark.cal.compiler.ModuleTypeInfo} and 
 *         related classes: {@link org.openquark.cal.compiler.Function},
 *         {@link org.openquark.cal.compiler.TypeConstructor},
 *         {@link org.openquark.cal.compiler.DataConstructor},
 *         {@link org.openquark.cal.compiler.TypeClass},
 *         {@link org.openquark.cal.compiler.ClassMethod},
 *         {@link org.openquark.cal.compiler.ClassInstance},
 *         {@link org.openquark.cal.compiler.CALDocComment}</dt>
 *     <dd>
 *         These classes provide a reflection API with which one can query information 
 *         about CAL modules loaded into the runtime and the entities declared in these 
 *         modules.
 *     </dd>
 * </dl>
 * <dl>
 *     <dt>{@link org.openquark.cal.compiler.TypeExpr}</dt>
 *     <dd>
 *         A <code>TypeExpr</code> is the representation of a CAL type 
 *         expression, such as <code>a -&gt; b</code>, <code>Int</code>, <code>(Int, a -&gt; 
 *             Boolean)</code>.
 *     </dd>
 * </dl>
 * <dl>
 *     <dt>{@link org.openquark.cal.compiler.QualifiedName}</dt>
 *     <dd>
 *         The Java representation of a qualified name in CAL, e.g. <code>List.map</code>. 
 *         See also the {@link org.openquark.cal.module module} package for 
 *         binding classes containing <code>QualifiedName</code> constants for entities 
 *         defined in modules which form the CAL API.
 *     </dd>
 * </dl>
 * <dl>
 *     <dt>{@link org.openquark.cal.compiler.MessageLogger}</dt>
 *     <dd>
 *         Many methods in this package accept a <code>MessageLogger</code> argument for 
 *         logging messages related to the compilation process (parsing, static analysis, 
 *         type-checking, code generation).
 *     </dd>
 * </dl>
 * <dl>
 *     <dt>{@link org.openquark.cal.compiler.CALSourceGenerator}</dt>
 *     <dd>
 *         The <code>CALSourceGenerator</code> can be used to generate CAL source for 
 *         {@link org.openquark.gems.client.Gem Gem}s.
 *     </dd>
 * </dl>
 * 
 * @author Joseph Wong
 * @author Edward Lam
 */
package org.openquark.cal.compiler;
