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
 * ForeignContextProvider.java
 * Creation date: May 17, 2006.
 * By: Edward Lam
 */
package org.openquark.cal.compiler;


/**
 * This interface provides a way for the client to specify a custom foreign context for cal modules.
 * The default foreign context is the environment in which the compiler itself lives -
 *   ie. by default classes are resolved with respect to the classloader which loads the classes for the cal compiler itself.
 * 
 * @author Edward Lam
 */
public interface ForeignContextProvider {
    /**
     * Get the classloader to use to load foreign references for a given module.
     * Note that the hierarchy of classloaders returned by calls to this method should satisfy constraints imposed by the program and the jvm.
     *   For instance, if module M1 is used by module M2, the classloader for M1 should be equal to or an ancestor of the classloader for M2.
     *   Another example: modules which exist in the implementation of the CAL_Platform must return as their classloader the classloader for CAL_Platform.
     * Otherwise LinkageErrors may occur during compilation or execution of the program.
     * 
     * @param moduleName the name of a module
     * @return the classloader to use to load foreign references for the module, or null if the module name is not recognized by this provider.
     *   The current behaviour of the compiler when null is returned for a module is to flag an error.
     */
    public ClassLoader getClassLoader(ModuleName moduleName);
}
