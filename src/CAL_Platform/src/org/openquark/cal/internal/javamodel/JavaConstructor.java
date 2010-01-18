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
 * JavaConstructor.java
 * Creation date: Oct 9, 2003.
 * By: Edward Lam
 */
package org.openquark.cal.internal.javamodel;

import java.util.ArrayList;
import java.util.List;

import org.openquark.cal.internal.javamodel.JavaStatement.Block;
import org.openquark.cal.services.Assert;


/**
 * A representation of a Java constructor.
 * @author Edward Lam
 */
public class JavaConstructor {

    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    /** The modifiers for the constructor, as defined by the constants in java.lang.reflect.Modifier. */
    private final int modifiers;

    /** The name of the constructor. */    
    private final String constructorName;

    /** The constructor's param names. */
    private final String[] paramNames;
    
    /** The constructor's param types. */
    private final JavaTypeName[] paramTypes;
    
    /** The body of the constructor. */
    private final Block bodyCode = new Block();

    /** (List of JavaTypeName) Names of exceptions declared as thrown by this method. */
    private final List<JavaTypeName> thrownExceptionList = new ArrayList<JavaTypeName>();

    /** Parameter types for call to superclass constructor. */
    private final JavaTypeName[] superConstructorParamTypes;
    
    /** Parameter values for call to superclass constructor. */
    private final JavaExpression[] superConstructorParamValues;
    
    /**
     * Constructor for a zero-argument java constructor.
     * @param modifiers modifiers for the constructor, as defined by the constants in java.lang.reflect.Modifier.
     * @param constructorName
     */
    public JavaConstructor(int modifiers, String constructorName) {
        this(modifiers, EMPTY_STRING_ARRAY, new JavaTypeName[] {}, constructorName, new JavaExpression[]{}, new JavaTypeName[]{});
    }
    

    /**
     * Constructor for a java constructor.
     * @param modifiers modifiers for the constructor, as defined by the constants in java.lang.reflect.Modifier.
     * @param paramNames
     * @param paramTypes
     * @param constructorName
     */
    public JavaConstructor(int modifiers, String[] paramNames, JavaTypeName[] paramTypes, String constructorName) {
        this (modifiers, paramNames, paramTypes, constructorName, new JavaExpression[]{}, new JavaTypeName[]{});
    }
    
    /**
     * Constructor for a java constructor.  Includes argument values for a call to the superclass
     * constructor. 
     * @param modifiers
     * @param paramNames - parameter names for this constructor
     * @param paramTypes - parameter types for this constructor
     * @param constructorName
     * @param superConstructorParamValues - parameter values for call to superclass constructor
     * @param superConstructorParamTypes - parameter types for call to superclass constructor
     */
    public JavaConstructor(int modifiers, 
                    String[] paramNames, 
                    JavaTypeName[] paramTypes, 
                    String constructorName,
                    JavaExpression[] superConstructorParamValues,
                    JavaTypeName[] superConstructorParamTypes) {
        Assert.isNotNull(paramNames);
        Assert.isNotNull(paramTypes);
        Assert.isNotNull(constructorName);
        Assert.isNotNull(superConstructorParamValues);
        Assert.isNotNull(superConstructorParamTypes);
        if (paramNames.length != paramTypes.length) {
            throw new IllegalArgumentException("paramNames and paramTypes must have the same length.");
        }
        if (superConstructorParamValues.length != superConstructorParamTypes.length) {
            throw new IllegalArgumentException("superConstructorParamNames and superConstructorParamTypes must have the same length.");
        }

        this.modifiers = modifiers;
        this.paramNames = paramNames.clone();
        this.paramTypes = paramTypes.clone();
        this.constructorName = constructorName;
        this.superConstructorParamTypes = superConstructorParamTypes;
        this.superConstructorParamValues = superConstructorParamValues;
    }
    
    /**
     * Get the constructor name
     * @return String
     */
    public String getConstructorName() {
        return constructorName;
    }

    /**
     * Add a statement to the body of the constructor.
     * @param statement
     */
    public void addStatement(JavaStatement statement) {
        Assert.isNotNull(statement);
        bodyCode.addStatement(statement);
    }
    
    /**
     * Add a thrown exception to the constructor.
     * @param thrownExceptionName
     */
    public void addThrows(JavaTypeName thrownExceptionName) {
        thrownExceptionList.add(thrownExceptionName);
    }
    
    /**    
     * @return int modifiers for the constructor, as defined by the constants in java.lang.reflect.Modifier.
     */
    public int getModifiers() {
        return modifiers;
    }

    /**
     * Get the body code.
     * @return Block
     */
    public Block getBodyCode() {
        return bodyCode;
    }
    
    public int getNParams() {
        return paramNames.length;
    }    
    public String getParamName(int n) {
        return paramNames[n];
    }
    /**
     * Get the parameter names. Note using getNParams and getParamName is more efficient if just traversing this array.
     * @return String[]
     */
    public String[] getParamNames() {
        return paramNames.clone();
    }     
    public JavaTypeName getParamType(int n) {
        return paramTypes[n];
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
     * Note that the method descriptor of the constuctor always treats the returned value as void (V).   
     * @return the method descriptor of the constructor, as specified in the Java Virtual Machine Specification section 4.3. 
     */
    public String getJVMMethodDescriptor() {
        StringBuilder sb = new StringBuilder("(");
        for (int i = 0, nParams = paramNames.length; i < nParams; ++i) {
            sb.append(paramTypes[i].getJVMDescriptor());
        }
        sb.append(")V");
        return sb.toString();
    }
    
    /**
     * @return representation of this JavaConstructor for debug purposes only.
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
        return visitor.visitJavaConstructor(this, arg);
    }


    public JavaTypeName[] getSuperConstructorParamTypes() {
        return superConstructorParamTypes;
    }


    public JavaExpression[] getSuperConstructorParamValues() {
        return superConstructorParamValues;
    }
    
}
