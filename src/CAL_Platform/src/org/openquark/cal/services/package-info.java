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
 * Provides the top-level API for accessing services provided by the Quark 
 * Platform.
 * <h2>Key classes in this package</h2>
 * <dl>
 *         <dt>{@link org.openquark.cal.services.BasicCALServices}</dt>
 *         <dd>
 *                 This helper class provides assistance with common workflows for using CAL 
 *                 services. It handles the initialization and loading of a workspace, and 
 *                 provides methods for evaluating CAL code and filtering gems.
 *         </dd>
 * </dl>
 * <dl>
 *         <dt>{@link org.openquark.cal.services.WorkspaceManager} 
 *                 and {@link org.openquark.cal.services.CALWorkspace}</dt>
 *         <dd>
 *                 A <code>CALWorkspace</code> encapsulates a workspace and the persistent 
 *                 resources associated with it, while a <code>WorkspaceManager</code> is responsible for 
 *                 managing both a <code>CALWorkspace</code> and the CAL runtime environment.
 *                 <p>
 *                 These two classes represent the main entry points for accessing many of the services
 *                 provided by the Quark platform.
 *         </dd>
 * </dl>
 * <dl>
 *         <dt>{@link org.openquark.cal.services.CarTool}</dt>
 *         <dd>
 *                 This class provides a command line interface to the CAL Archive (Car) building tool.
 *         </dd>
 * </dl>
 * 
 * @author Joseph Wong
 * @author Edward Lam
 */
package org.openquark.cal.services;
