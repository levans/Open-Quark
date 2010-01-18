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
 * CodeGenerationException.java
 * Created: May 19, 2004
 * By: rcypher
 */

package org.openquark.cal.internal.machine;

/**
 * CodeGenerationException
 * Class for raising exceptions during the code generation process.
 * @author rcypher
 * Created: May 19, 2004
 */
public class CodeGenerationException extends Exception {
      
    private static final long serialVersionUID = -6310507883559829154L;

    /**
     * Constructs a new exception without a message.
     */
    public CodeGenerationException() {
        super();
    }

    /**
     * Constructs a new exception with the specified message, which can be null.
     *
     * @param message the detail message. Can be null.
     */
    public CodeGenerationException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified cause, which can be null.
     *
     * @param cause the cause. Can be null.
     */
    public CodeGenerationException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new exception with the specified message and cause.
     *
     * @param message the detail message. Can be null.
     * @param cause the cause. Can be null.
     */
    public CodeGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
