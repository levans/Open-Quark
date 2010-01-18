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
 * JavaExceptionHandler.java
 * Created: Nov 12, 2003  11:36:04 AM
 * By: RCypher
 */
package org.openquark.cal.internal.javamodel;

/**
 * A convenience class that bundles information necessary to generate a
 * catch block or exception handler.
 *
 *  @author RCypher
 */
public final class JavaExceptionHandler {

    /** The class of the exception to be caught. */
    private Class<?> exceptionClass;
    
    /** The code to execute in the catch block. */
    private JavaStatement handlerCode;
    
    /** The variable name to use for the caught exception. */
    private String exceptionVarName;
      
    /**
     * Constructor for a JavaExceptionHandler
     * @param exceptionClass
     * @param exceptionVarName
     * @param code
     */
    public JavaExceptionHandler (Class<?> exceptionClass, String exceptionVarName, JavaStatement code) {
        if (exceptionClass == null || exceptionVarName == null || code == null) {
            throw new IllegalArgumentException ("Unable to create JavaExceptionHandler: null argument.");
        }
        this.exceptionClass = exceptionClass;
        this.exceptionVarName = exceptionVarName;
        this.handlerCode = code;
    }
       
    /**
     * Get the class of the exception being caught.
     * @return Class
     */
    public Class<?> getExceptionClass () {
        return exceptionClass;
    }
    
    /**
     * Get the code to place in the catch block.
     * @return JavaStatement
     */
    public JavaStatement getHandlerCode () {
        return handlerCode;
    }
    
    /**
     * Get the name of the exception variable.
     * @return String
     */
    public String getExceptionVarName() {
        return exceptionVarName;
    }
    
    /**
     * Accepts the visitation of a visitor, which implements the
     * JavaModelVisitor interface. 
     * <p>
     * 
     * As the JavaModelVisitor follows a more general visitor pattern
     * where arguments can be passed into the visit methods and return
     * values obtained from them, this method passes through the argument
     * into the visit method, and returns as its return value the return
     * value of the visit method.
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
     * @see JavaModelVisitor
     * 
     * @param <T> the argument type. If the visitation argument is not used, specify {@link Void}.
     * @param <R> the return type. If the return value is not used, specify {@link Void}.
     * 
     * @param visitor
     *            the visitor
     * @param arg
     *            the argument to be passed to the visitor's visitXXX method
     * @return the return value of the visitor's visitXXX method
     */
    public <T, R> R accept(JavaModelVisitor<T, R> visitor, T arg) {
        return visitor.visitJavaExceptionHandler(this, arg);
    }
    
}
