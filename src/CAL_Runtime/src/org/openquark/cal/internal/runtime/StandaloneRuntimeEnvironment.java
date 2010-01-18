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

import org.openquark.cal.internal.compiler.StandaloneRuntimeForeignClassResolver;
import org.openquark.cal.runtime.ResourceAccess;

/**
 * A RuntimeEnvironment when the Program object is not available, such as in
 * Standalone JARs.
 * @author Bo Ilic
 */
public final class StandaloneRuntimeEnvironment extends RuntimeEnvironment {
    
    /**
     * The class loader for loading foreign classes when running in standalone JAR mode (needed
     * because the class loader which loaded this class may be an <i>ancestor</i> of the one which
     * loaded the standalone JAR (e.g. the bootstrap class loader), and thus may not have access to
     * foreign classes necessary for the standalone JAR to run).   
     */   
    private final ClassLoader foreignClassLoader;   
        
    public StandaloneRuntimeEnvironment(ClassLoader foreignClassLoader, ResourceAccess resourceAccess) {
        super(resourceAccess);
        this.foreignClassLoader = foreignClassLoader;
    }
    
    /** {@inheritDoc} */
    @Override   
    public final Class<?> getForeignClass(final String qualifiedTypeConsName, final String foreignName) throws StandaloneRuntimeForeignClassResolver.UnableToResolveForeignEntityException {

        // We do not have a program (e.g. we're running in a standalone jar).
        // Since there is no program, we attempt to resolve the class by its foreign name (as returned by Class.getName())
        return StandaloneRuntimeForeignClassResolver.resolveClassForForeignType(foreignName, foreignClassLoader);
    }    

}
