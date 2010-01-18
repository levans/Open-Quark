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
 * JavaClassRep.java
 * Creation date: Oct 8, 2003.
 * By: Edward Lam
 */
package org.openquark.cal.internal.javamodel;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.openquark.cal.internal.javamodel.JavaStatement.MultiLineComment;
import org.openquark.cal.services.Assert;


/**
 * A representation of a Java class.
 *   This doesn't include bits like imports or class comments.
 * @author Edward Lam
 */
public class JavaClassRep {
    
    /**
     * Constants used to indicate the relation of this class to the use
     * of assertsions.  These are used to differentiate
     * between the different situations of classes containing asserts.
     * The return value of containsAsserts() will be one of or a 
     * combination of these values.
     */
    public static final int ASSERTS_UNKNOWN = 0x01;
    public static final int ASSERTS_NONE = 0x02;
    public static final int ASSERTS_IN_CLASS = 0x04;
    public static final int ASSERTS_IN_INNER_CLASS = 0x08;
    
    /** The name of the class. */
    private final JavaTypeName className;
    
    /** The name of the super class. */
    private final JavaTypeName superclassName;
    
    /** The modifiers for the class, as defined by the constants in java.lang.reflect.Modifier. */
    private final int modifiers;
    
    /** The names of any interfaces implemented by the class. */
    private final JavaTypeName[] interfaces;
    
    /** (List of JavaFieldDeclaration) The class's field declarations. */
    private final List<JavaFieldDeclaration> fieldDeclarations = new ArrayList<JavaFieldDeclaration>();
    
    /** (List of JavaConstructor) The class's constructors. */
    private final List<JavaConstructor> constructors = new ArrayList<JavaConstructor>();

    /** (List of JavaMethod) The class's methods. */
    private final List<JavaMethod> methods = new ArrayList<JavaMethod>();
    
    /** (List of JavaClassRep) Inner classes. */
    private final List<JavaClassRep> innerClasses = new ArrayList<JavaClassRep>();
    
    /** Flag used to indicate the association of this class with asssert. 
     * It will be one or a combination of the ASSERTS_... constants. */
    private int assertionContainment = ASSERTS_UNKNOWN;
    
    /** The JavaDoc for this class. May be null.*/
    private JavaStatement.JavaDocComment javaDocComment = null;
    
    /** A comment to go at the beginning of the class source.
     *  Usually used for copyright notice, etc. May be null.
     */
    private MultiLineComment comment = null;

    /** A List containing all the top level elements in the class
     *  this includes constructors, methods, fields, and inner classes.
     */
    private final List<Object> classContent = new ArrayList<Object>();
    
    /**
     * Constructor for a JavaClassRep
     * @param className the name of the class
     * @param superclassName the name of the super class
     * @param modifiers modifiers for the class, as defined by the constants in java.lang.reflect.Modifier.
     * @param interfaces names of any interfaces implemented by this class. 
     */
    public JavaClassRep(JavaTypeName className, JavaTypeName superclassName, int modifiers, JavaTypeName[] interfaces) {
        Assert.isNotNull(className);
        Assert.isNotNull(superclassName);
        Assert.isNotNull(interfaces);

        this.className = className;
        this.superclassName = superclassName;
        this.modifiers = modifiers;
        this.interfaces = interfaces.clone();
    }
    
    /**
     * Add a field declaration to the class representation.
     * @param fieldDeclaration the field declaration to add.
     */
    public void addFieldDeclaration(JavaFieldDeclaration fieldDeclaration) {
        Assert.isNotNull(fieldDeclaration);
        fieldDeclarations.add(fieldDeclaration);
        classContent.add(fieldDeclaration);
    }
    
    /**
     * Add a constructor to the class representation.
     * @param javaConstructor the constructor to add. 
     */
    public void addConstructor(JavaConstructor javaConstructor) {
        Assert.isNotNull(javaConstructor);
        if (!isConstructorNameCompatible(javaConstructor)) {
            throw new IllegalArgumentException("The constructor name " + javaConstructor.getConstructorName() + " is not compatible with the class name " + getClassName());
        }
        constructors.add(javaConstructor);
        classContent.add(javaConstructor);
    }

    /**
     * @return true if the given java constructor has a name that is compatible with this class representation.
     * For example, if the class name is "com.xyz.Foo", then the constructor's name must be "Foo".
     * If the class name is "com.xyz.Foo$Bar", then the constructor's name could be "Bar" (because this class
     * may be an inner class) or "Foo$Bar" (if this class is a top-level class).
     */
    private boolean isConstructorNameCompatible(final JavaConstructor javaConstructor) {
        if (getClassName() instanceof JavaTypeName.Reference.Object) {
            
            final JavaTypeName.Reference.Object className = (JavaTypeName.Reference.Object)getClassName();
            
            // base name of "com.xyz.Foo" is "Foo", of "com.xyz.Foo$Bar" is "Bar"
            final String baseName = className.getBaseName();
            // internal unqualified name of "com.xyz.Foo" is "Foo", of "com.xyz.Foo$Bar" is "Foo$Bar"
            final String internalUnqualifiedName = className.getInternalUnqualifiedName();
            
            final String constructorName = javaConstructor.getConstructorName();
            
            if (!baseName.equals(constructorName) && !internalUnqualifiedName.equals(constructorName)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Add a method to the class representation.
     * @param javaMethod the method to add.
     */
    public void addMethod(JavaMethod javaMethod) {
        Assert.isNotNull(javaMethod);
        methods.add(javaMethod);
        classContent.add(javaMethod);
    }
    
    /**
     * Add an inner class to the class representation.
     * @param innerClassRep the inner class to add
     */
    public void addInnerClass(JavaClassRep innerClassRep) {
        Assert.isNotNull(innerClassRep);
        innerClasses.add(innerClassRep);
        classContent.add(innerClassRep);
    }
    
    public void addComment(MultiLineComment comment) {
        assert (comment != null) : "Null comment in JavaClassRep.addComment";
        classContent.add(comment);
    }

    /**
     * Get the name of the class.
     * @return JavaTypeName
     */
    public JavaTypeName getClassName() {
        return className;
    }

    /**
     * Get the name of the super class.
     * @return JavaTypeName
     */
    public JavaTypeName getSuperclassName() {
        return superclassName;
    }

    /**
     * Get the modifiers for the class.
     * @return int modifiers for the class, as defined by the constants in java.lang.reflect.Modifier.
     */
    public int getModifiers() {
        return modifiers;
    }
   
    /**   
     * @return int the number of implemented interfaces.
     */
    public int getNInterfaces() {
        return interfaces.length;
    }
    public JavaTypeName getInterface(int n) {
        return interfaces[n];
    }
    
    public int getNFieldDeclarations() {
        return fieldDeclarations.size();
    }
    public JavaFieldDeclaration getFieldDeclaration(int n) {
        return fieldDeclarations.get(n);
    }  

    public int getNConstructors() {
        return constructors.size();
    }
    public JavaConstructor getConstructor(int n) {
        return constructors.get(n);
    }
      
    public int getNMethods() {
        return methods.size();
    }
    public JavaMethod getMethod(int n) {
        return methods.get(n);
    }    

    public int getNInnerClasses() {
        return innerClasses.size();
    }
    public JavaClassRep getInnerClass(int n) {
        return innerClasses.get(n);
    } 
    
    /**     
     * @return true if this JavaClassRep contains a static field that has an initializer.
     */
    public boolean hasInitializedStaticField() {
        
        int nFields = getNFieldDeclarations();
        
        for (int i = 0; i < nFields; ++i) {
            
            JavaFieldDeclaration fieldDecl = getFieldDeclaration(i);
            JavaExpression initializer = fieldDecl.getInitializer();
            
            if (initializer != null && Modifier.isStatic(fieldDecl.getModifiers())) {
                return true;
            }
        }
        
        return false;
    }   
    
    /**
     * @return true if this is an inner class.
     */
    public boolean isInnerClass () {
        return getClassName().getJVMInternalName().indexOf('$') >= 0;
    }
     
    /**
     * @return representation of this JavaClassRep for debug purposes only.
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
        return visitor.visitJavaClassRep(this, arg);
    }

    /**
     * Return a value describing how this class contains assertions.
     * @return one or a combination of the ASSERTS_... constants.
     */
    public int getAssertionContainment() {
        return assertionContainment;
    }

    /**
     * Indicate that this class contains assertions.
     */
    public void setContainsAssertions () {
        assertionContainment ^= ASSERTS_UNKNOWN;
        assertionContainment |= ASSERTS_IN_CLASS;
    }

    /**
     * Indicate that this class contains inner classes
     * which contain assertions.
     */
    public void setInnerClassContainsAssertions () {
        assertionContainment ^= ASSERTS_UNKNOWN;
        assertionContainment |= ASSERTS_IN_INNER_CLASS;
    }

    public JavaStatement.JavaDocComment getJavaDoc() {
        return javaDocComment;
    }

    public void setJavaDoc(JavaStatement.JavaDocComment classComment) {
        this.javaDocComment = classComment;
    }

    public MultiLineComment getComment() {
        return comment;
    }

    public void setComment(MultiLineComment comment) {
        this.comment = comment;
    }

    public List<Object> getClassContent() {
        return classContent;
    }
    
}