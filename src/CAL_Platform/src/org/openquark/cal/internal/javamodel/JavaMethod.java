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
 * JavaMethod.java
 * Creation date: Oct 8, 2003.
 * By: Edward Lam
 */
package org.openquark.cal.internal.javamodel;

import java.util.ArrayList;
import java.util.List;

import org.openquark.cal.internal.javamodel.JavaStatement.Block;
import org.openquark.cal.services.Assert;


/**
 * A representation of a Java method.
 * 
 * @author Edward Lam
 */
public class JavaMethod {

    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    /** The modifiers for the method, as defined by the constants in java.lang.reflect.Modifier. */
    private final int modifiers;
    
    /** The return type of the method. */
    private final JavaTypeName returnType;
    
    /** The names of the method params. */
    private final String[] paramNames;
    
    /** The types of the method params. */
    private final JavaTypeName[] paramTypes;
    
    /** 
     * Whether the parameter is final or not. null means that all parameters are not final. The final parameter on method arguments does
     * not affect byte code generation, but can detect some errors in the generated Java source.
     */
    private final boolean[] paramFinal;
    
    /** The name of the method. */
    private final String methodName;
    
    /** The code for the body of the method. */
    private final Block bodyCode = new Block();
    
    /** (List of JavaTypeName) Names of exceptions declared as thrown by this method. */
    private final List<JavaTypeName> thrownExceptionList = new ArrayList<JavaTypeName>();

    /** The JavaDoc for this class. */
    private JavaStatement.JavaDocComment javaDocComment = null;
    

    /**
     * Constructor for a zero-argument java method.
     * @param modifiers for the method, as defined by the constants in java.lang.reflect.Modifier.
     * @param returnType
     * @param methodName
     */
    public JavaMethod(int modifiers, JavaTypeName returnType, String methodName) {
        this(modifiers, returnType, EMPTY_STRING_ARRAY, new JavaTypeName[] {}, null, methodName);
    }

    /**
     * Constructor for a single-argument java method.
     * @param modifiers for the method, as defined by the constants in java.lang.reflect.Modifier.
     * @param returnType
     * @param paramName
     * @param paramType
     * @param paramFinal
     * @param methodName
     */
    public JavaMethod(int modifiers, JavaTypeName returnType, String paramName, JavaTypeName paramType, boolean paramFinal, String methodName) {
        this(modifiers, returnType, new String[] {paramName}, new JavaTypeName[] {paramType}, new boolean[] {paramFinal}, methodName);
    }

    /**
     * Constructor for a java method.
     * @param modifiers for the method, as defined by the constants in java.lang.reflect.Modifier.
     * @param returnType
     * @param paramNames
     * @param paramTypes
     * @param paramFinal may be null, indicating that all params are not final
     * @param methodName
     */
    public JavaMethod(int modifiers, JavaTypeName returnType, String[] paramNames, JavaTypeName[] paramTypes, boolean[] paramFinal, String methodName) {
        Assert.isNotNull(returnType);
        Assert.isNotNull(paramNames);
        Assert.isNotNull(paramTypes);
        Assert.isNotNull(methodName);
        if (paramNames.length != paramTypes.length) {
            throw new IllegalArgumentException("paramNames and paramTypes must have the same length.");
        }
        
        if (paramFinal == null) {
            this.paramFinal = new boolean[paramNames.length];
        } else if (paramNames.length != paramFinal.length) {
            throw new IllegalArgumentException("paramNames and paramFinal must have the same length.");
        } else {               
            this.paramFinal = paramFinal.clone();
        }        
        
        this.modifiers = modifiers;
        this.returnType = returnType;
        this.paramNames = paramNames.clone();
        this.paramTypes = paramTypes.clone();        
        this.methodName = methodName;
    }
    
    /**
     * Add a statement to the body of the method.
     * @param statement
     */
    public void addStatement(JavaStatement statement) {
        Assert.isNotNull(statement);
        bodyCode.addStatement(statement);
    }
    
    /**
     * Add a thrown exception to the method declaration.
     * @param thrownExceptionName
     */
    public void addThrows(JavaTypeName thrownExceptionName) {
        thrownExceptionList.add(thrownExceptionName);
    }
    
    /**
     * Get the modifiers for the method.
     * @return int modifiers for the method, as defined by the constants in java.lang.reflect.Modifier.
     */
    public int getModifiers() {
        return modifiers;
    }

    /**
     * Get the code for the body of the method.
     * @return Block
     */
    public Block getBodyCode() {
        return bodyCode;
    }

    /**
     * Get the method name.
     * @return String
     */
    public String getMethodName() {
        return methodName;
    }    
    
    public int getNParams() {
        return paramNames.length;
    }    
    public String getParamName(int n) {
        return paramNames[n];
    }    
    public JavaTypeName getParamType(int n) {
        return paramTypes[n];
    }
    public boolean isParamFinal(int n) {      
        return paramFinal[n];
    }
    
    /**
     * Get the return type.
     * @return JavaTypeName
     */
    public JavaTypeName getReturnType() {
        return returnType;
    }    
             
    /**     
     * @return int the number of exceptions thrown by this constructor.
     */
    public int getNThrownExceptions() {
        return thrownExceptionList.size();
    }
    public JavaTypeName getThrownException(int n) {
        return thrownExceptionList.get(n);
    }
    
    /**        
     * @return the method descriptor, as specified in the Java Virtual Machine Specification section 4.3. 
     */
    public String getJVMMethodDescriptor() {
        StringBuilder sb = new StringBuilder("(");
        for (int i = 0, nParams = paramNames.length; i < nParams; ++i) {
            sb.append(paramTypes[i].getJVMDescriptor());
        }
        sb.append(')').append(returnType.getJVMDescriptor());
        return sb.toString();
    }
    
    /**
     * @return representation of this JavaMethod for debug purposes only.
     */    
    @Override
    public String toString() {
        return JavaSourceGenerator.toDebugString(this);
    }
  
    /**
     * Accepts the visitation of a visitor, which implements the
     * JavaModelVisitor interface. This abstract method is to be overridden
     * by each concrete subclass so that the correct visit method on the
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
        return visitor.visitJavaMethod(this, arg);
    }

    public JavaStatement.JavaDocComment getJavaDocComment() {
        return javaDocComment;
    }

    public void setJavaDocComment(JavaStatement.JavaDocComment javaDocComment) {
        this.javaDocComment = javaDocComment;
    }
    
}


