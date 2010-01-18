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
 * JavaFieldDeclaration.java
 * Creation date: Oct 8, 2003.
 * By: Edward Lam
 */
package org.openquark.cal.internal.javamodel;

import org.openquark.cal.internal.javamodel.JavaStatement.JavaDocComment;
import org.openquark.cal.services.Assert;


public class JavaFieldDeclaration {
    
    /** The modifiers for the field, as defined by the constants in java.lang.reflect.Modifier. */
    private final int modifiers;

    /** The type of the field. */
    private final JavaTypeName fieldType;
    
    /** The name of the field. */
    private final String fieldName;
    
    /** The field's initializer, if any. */
    private final JavaExpression initializer;
    
    /** The JavaDoc for this field declaration.  May be null.*/
    private JavaDocComment javaDocComment;

    /**
     * Constructor for a java field declaration
     * @param modifiers modifiers for the field, as defined by the constants in java.lang.reflect.Modifier.
     * @param fieldType
     * @param fieldName
     * @param initializer the field's initializer, or null if the field has no initializer.
     */
    public JavaFieldDeclaration(int modifiers, JavaTypeName fieldType, String fieldName, JavaExpression initializer) {
        Assert.isNotNull(fieldType);
        Assert.isNotNull(fieldName);
        
        this.modifiers = modifiers;
        this.fieldType = fieldType;
        this.fieldName = fieldName;
        this.initializer = initializer;
    }

    /**    
     * @return int modifiers for the field, as defined by the constants in java.lang.reflect.Modifier.
     */
    public int getModifiers() {
        return modifiers;
    }

    /**
     * Get the name of the field.
     * @return String
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * Get the field's type.
     * @return JavaTypeName
     */
    public JavaTypeName getFieldType() {
        return fieldType;
    }

    /**
     * Get the field's initializer.
     * @return JavaExpression the initializer, or null if there is no initializer.
     */
    public JavaExpression getInitializer() {
        return initializer;
    }
    
    /**
     * @return representation of this JavaFieldDeclaration for debug purposes only.
     */
    @Override
    public String toString() {
        return JavaSourceGenerator.toDebugString(this);        
    }       
    
    /**
     * Accepts the visitation of a visitor, which implements the
     * JavaModelVisitor interface. This abstract method is to be overriden
     * by each concrete subclass so that the corrent visit method on the
     * visitor may be called based upon the type of the element being
     * visited. Each concrete subclass of JavaExpression should correspond
     * one-to-one with a visit method declaration in the SourceModelVisitor
     * interface.
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
        return visitor.visitJavaFieldDeclaration(this, arg);
    }

    public JavaDocComment getJavaDoc() {
        return javaDocComment;
    }

    public void setJavaDoc(JavaDocComment javaDocComment) {
        this.javaDocComment = javaDocComment;
    }
}
