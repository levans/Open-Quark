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

package org.openquark.cal.internal.runtime;

import org.openquark.cal.runtime.ResourceAccess;

/**
 * Provides access to information about entities in the workspace as needed to run the given CAL program.
 * In the case of non-standalone JARs, DynamicRuntimeEnvironment is a wrapper on a Program and ResourceAccess objects.
 * In the case of Standalone JARs, StandaloneRuntimeEnvironment is something less- just sufficient to get the information
 * needed to support the lookup or reflection-like capabilities supported by Standalone JARs.
 * 
 * @author Bo Ilic
 */
public abstract class RuntimeEnvironment {
         
    private static final String[] EMPTY_STRING_ARRAY = new String[0];
    private static final String[][] NO_ARG_INFO_ARRAY = new String[][] {EMPTY_STRING_ARRAY, EMPTY_STRING_ARRAY};
    
    /** Provides access to the resources of the current environment (e.g. from the workspace, or from Eclipse). */
    private ResourceAccess resourceAccess;
    
    
    public RuntimeEnvironment(final ResourceAccess resourceAccess) {
        super();
        this.resourceAccess = resourceAccess;
    }


    /**     
     * @return provides access to the resources of the current environment (e.g. from the workspace, or from Eclipse).
     */
    public final ResourceAccess getResourceAccess() {
        return resourceAccess;
    }
    
    
    /**
     * Retrieves the Class object associated with a CAL foreign type.
     *      
     * @param qualifiedTypeConsName the fully-qualified type constructor name as a String e.g. "Cal.Core.Prelude.Maybe".
     * @param foreignName the name of the foreign class as returned by <code>Class.getName()</code>.
     * @return Class of the foreign type constructor, or null if not a foreign type.
     * @throws Exception if the class cannot be resolved.
     */   
    public abstract Class<?> getForeignClass(final String qualifiedTypeConsName, final String foreignName) throws Exception;
    
    /**    
     * @param qualifiedFunctionName
     * @return an array of size 2. The 0th element is an array of the arg-names of the function, the 1th element is the arg types. 
     *     The arg-names and arg-types arrays will have the same length. May be empty if the information is not retrievable.
     */
    public String[][] getArgNamesAndTypes(final String qualifiedFunctionName) {
        return NO_ARG_INFO_ARRAY;
    }
        
}
