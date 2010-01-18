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
 * UnableToResolveForeignEntityException.java
 * Created: May 18, 2007
 * By: Joseph Wong
 */

package org.openquark.cal.compiler;

/**
 * This exception class represents a failure to resolve a class for a CAL foreign type at
 * runtime (e.g. when foreign entities are resolved and loaded lazily).
 *
 * @author Joseph Wong
 */
public final class UnableToResolveForeignEntityException extends Exception {
    
    private static final long serialVersionUID = -2097574651595819019L;

    /** The underlying compiler message. */
    private final CompilerMessage compilerMessage;
    
    /**
     * Constructs an instance of this exception.
     * @param compilerMessage the underlying compiler message. Cannot be null.
     */
    UnableToResolveForeignEntityException(final CompilerMessage compilerMessage) {
        super(compilerMessage.getMessage(), compilerMessage.getException());
        this.compilerMessage = compilerMessage;
    }
    
    /**
     * @return the underlying compiler message.
     */
    public CompilerMessage getCompilerMessage() {
        return compilerMessage;
    }
}