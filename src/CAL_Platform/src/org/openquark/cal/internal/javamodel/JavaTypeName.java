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
 * JavaTypeName.java
 * Created: Dec 17, 2004
 * By: Bo Ilic
 */

package org.openquark.cal.internal.javamodel;

import java.io.PrintStream;
import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.internal.runtime.lecc.RTValue;
import org.openquark.cal.machine.CALExecutor;
import org.openquark.cal.runtime.CALExecutorException;
import org.openquark.cal.runtime.CalFunction;
import org.openquark.cal.runtime.CalValue;
import org.openquark.cal.runtime.DebugSupport;
import org.openquark.cal.runtime.ErrorInfo;
import org.openquark.cal.services.Assert;
import org.openquark.cal.util.EquivalenceRelation;



/**
 * A representation of a Java class / type. This is similar to the java.lang.Class type except that the class 
 * represented does not need to exist.
 *  
 * @author Edward Lam
 */

abstract public class JavaTypeName {
    
    public static final int VOID_TAG = 0;
    public static final int BOOLEAN_TAG = 1;
    public static final int BYTE_TAG = 2;
    public static final int SHORT_TAG = 3;
    public static final int CHAR_TAG = 4;
    public static final int INT_TAG = 5;
    public static final int LONG_TAG = 6;
    public static final int DOUBLE_TAG = 7;
    public static final int FLOAT_TAG = 8;       
    public static final int ARRAY_TAG = 9;
    public static final int OBJECT_TAG = 10;  
    
    /**
     * It turns out that benchmarking shows that calling JavaTypeName.getJVMInternalName and getJVMDescriptor was expensive
     * when generating bytecodes using the ASM bytecode generator.
     * We reduce this cost by
     * a) caching the results of these functions in JavaTypeName
     * b) limiting the number of different JavaTypeName objects created- many are for the same types shown below.
     * Use these static constants above wherever possible.
     * 
     * Note that some of the static constants such as RTFullApp* are not used in the Java code. The purpose of adding them here
     * is to add them to the cache since they are used many times over in the generated lecc Java code.
     * 
     */
    private static final Map<String, JavaTypeName> cachedTypes = new HashMap<String, JavaTypeName>();       
    
    //primitive Java types
    public static final JavaTypeName VOID = makeCached(void.class);
    public static final JavaTypeName BOOLEAN = makeCached(boolean.class);
    public static final JavaTypeName BYTE = makeCached(byte.class);
    public static final JavaTypeName SHORT = makeCached(short.class);
    public static final JavaTypeName CHAR = makeCached(char.class);
    public static final JavaTypeName INT = makeCached(int.class);
    public static final JavaTypeName LONG = makeCached(long.class);
    public static final JavaTypeName DOUBLE = makeCached(double.class);
    public static final JavaTypeName FLOAT = makeCached(float.class);
       
    //primitive one dimensional array types
    public static final JavaTypeName BOOLEAN_ARRAY = makeCached(boolean[].class);
    public static final JavaTypeName BYTE_ARRAY = makeCached(byte[].class);
    public static final JavaTypeName SHORT_ARRAY = makeCached(short[].class);
    public static final JavaTypeName CHAR_ARRAY = makeCached(char[].class);
    public static final JavaTypeName INT_ARRAY = makeCached(int[].class);
    public static final JavaTypeName LONG_ARRAY = makeCached(long[].class);
    public static final JavaTypeName DOUBLE_ARRAY = makeCached(double[].class);
    public static final JavaTypeName FLOAT_ARRAY = makeCached(float[].class);   
    
    //object wrappers to the primitive Java types
    public static final JavaTypeName VOID_OBJECT = makeCached(Void.class);
    public static final JavaTypeName BOOLEAN_OBJECT = makeCached(Boolean.class);
    public static final JavaTypeName BYTE_OBJECT = makeCached(Byte.class);
    public static final JavaTypeName SHORT_OBJECT = makeCached(Short.class);
    public static final JavaTypeName CHARACTER_OBJECT = makeCached(Character.class);
    public static final JavaTypeName INTEGER_OBJECT = makeCached(Integer.class);
    public static final JavaTypeName LONG_OBJECT = makeCached(Long.class);
    public static final JavaTypeName DOUBLE_OBJECT = makeCached(Double.class);
    public static final JavaTypeName FLOAT_OBJECT = makeCached(Float.class);
            
    //classes in the standard java libraries
    public static final JavaTypeName OBJECT = makeCached(Object.class);
    public static final JavaTypeName STRING = makeCached(String.class);
    public static final JavaTypeName STRING_ARRAY = makeCached(String[].class);    
    public static final JavaTypeName STRING_BUILDER = makeCached(StringBuilder.class);  
    public static final JavaTypeName THROWABLE = makeCached(Throwable.class);          
    public static final JavaTypeName BIG_INTEGER = makeCached(BigInteger.class);
    public static final JavaTypeName LIST = makeCached(java.util.List.class);     
    public static final JavaTypeName MAP = makeCached(java.util.Map.class);
    public static final JavaTypeName WEAK_HASH_MAP = makeCached(WeakHashMap.class);
    public static final JavaTypeName SYSTEM = makeCached(System.class);
    public static final JavaTypeName PRINT_STREAM = makeCached(PrintStream.class);  
    public static final JavaTypeName COMPARATOR = makeCached(java.util.Comparator.class);
    public static final JavaTypeName THREAD = makeCached(Thread.class);
    public static final JavaTypeName COLLECTIONS = makeCached (Collections.class);
    public static final JavaTypeName ITERATOR = makeCached(java.util.Iterator.class);
    public static final JavaTypeName CLASS = makeCached(Class.class);
   
    // Exception types from the standard Java libraries
    public static final JavaTypeName CLASS_CAST_EXCEPTION = makeCached(ClassCastException.class);
    public static final JavaTypeName RUNTIME_EXCEPTION = makeCached(RuntimeException.class);
    public static final JavaTypeName NULL_POINTER_EXCEPTION = makeCached(NullPointerException.class);
    public static final JavaTypeName INDEX_OUT_OF_BOUNDS_EXCEPTION = makeCached(IndexOutOfBoundsException.class);
    public static final JavaTypeName ASSERTION_ERROR = makeCached(AssertionError.class);
    
    // Error info type
    public static final JavaTypeName ERRORINFO = makeCached(ErrorInfo.class);
    
    public static final JavaTypeName CAL_VALUE = makeCached(CalValue.class);
    public static final JavaTypeName CAL_VALUE_ARRAY = makeCached(CalValue[].class);
    public static final JavaTypeName DEBUG_SUPPORT = makeCached(DebugSupport.class);
    public static final JavaTypeName CAL_EXECUTOR = makeCached(CALExecutor.class);
    public static final JavaTypeName CAL_EXECUTOR_EXCEPTION = makeCached(CALExecutorException.class);
    public static final JavaTypeName EQUIVALENCE_RELATION = makeCached(EquivalenceRelation.class);    
    public static final JavaTypeName CAL_FUNCTION = makeCached(CalFunction.class);
    public static final JavaTypeName MODULE_NAME = makeCached(ModuleName.class);
    public static final JavaTypeName QUALIFIED_NAME = makeCached(QualifiedName.class);
    public static final JavaTypeName[] NO_ARGS = new JavaTypeName[0];              
    
    //todoBI we should not have a dependency on lecc classes in this file
    static final JavaTypeName RTVALUE = makeCached(RTValue.class);
    
    /**
     * Subclasses of Primitive represent the primitive Java types (void, int, char, ...).
     * The subclasses are singletons.
     * 
     * @author Bo Ilic
     */
    public abstract static class Primitive extends JavaTypeName {           
        
        public static final class Void extends Primitive {
            
            private Void () {/* Constructor made private to prevent instantiation. */}
            
            @Override
            public String getName() {
                return "void";
            }
            
            @Override
            public boolean equals(Object other) {
                return other instanceof Void;                
            }
            
            @Override
            public int getTag() {
                return JavaTypeName.VOID_TAG;
            }
            
            @Override
            public String getJVMDescriptor() {
                //void is not a field type, and so does not have a field descriptor, however, methods that return void use V do denote the return               
                return "V";
            }
            
           @Override
        JavaTypeName makeArrayType() {
               throw new UnsupportedOperationException("[void] is not a valid type.");
           }
        }
        
        public static final class Boolean extends Primitive {
            
            private Boolean() {/* Constructor made private to prevent instantiation. */}
            
            @Override
            public String getName() {
                return "boolean";
            } 
            
            @Override
            public boolean equals(Object other) {
                return other instanceof Boolean;                
            }
            
            @Override
            public int getTag() {
                return JavaTypeName.BOOLEAN_TAG;
            }
            
            @Override
            public String getJVMDescriptor() {               
                return "Z";
            }
            
            @Override
            JavaTypeName makeArrayType() {
                return JavaTypeName.BOOLEAN_ARRAY;                
            }            
        }
        
        public static final class Byte extends Primitive {
            
            private Byte () {/* Constructor made private to prevent instantiation. */}
            
            @Override
            public String getName() {
                return "byte";
            }
            
            @Override
            public boolean equals(Object other) {
                return other instanceof Byte;                
            }
            
            @Override
            public int getTag() {
                return JavaTypeName.BYTE_TAG;
            }
            
            @Override
            public String getJVMDescriptor() {               
                return "B";
            }
            
            @Override
            JavaTypeName makeArrayType() {
                return JavaTypeName.BYTE_ARRAY;                
            }            
        }
        
        public static final class Short extends Primitive {
            
            private Short() {/* Constructor made private to prevent instantiation. */}
            
            @Override
            public String getName() {
                return "short";
            }
            
            @Override
            public boolean equals(Object other) {
                return other instanceof Short;                
            }
            
            @Override
            public int getTag() {
                return JavaTypeName.SHORT_TAG;
            }
            
            @Override
            public String getJVMDescriptor() {               
                return "S";
            }
            
            @Override
            JavaTypeName makeArrayType() {
                return JavaTypeName.SHORT_ARRAY;                
            }                        
        }
        
        public static final class Char extends Primitive {
            
            private Char() {/* Constructor made private to prevent instantiation. */}
            
            @Override
            public String getName() {
                return "char";
            }
            
            @Override
            public boolean equals(Object other) {
                return other instanceof Char;                
            }
            
            @Override
            public int getTag() {
                return JavaTypeName.CHAR_TAG;
            }
            
            @Override
            public String getJVMDescriptor() {               
                return "C";
            }
            
            @Override
            JavaTypeName makeArrayType() {
                return JavaTypeName.CHAR_ARRAY;                
            }            
        }
        
        public static final class Int extends Primitive {
            
            private Int() {/* Constructor made private to prevent instantiation. */}
            
            @Override
            public String getName() {
                return "int";
            }
            
            @Override
            public boolean equals(Object other) {
                return other instanceof Int;                
            }
            
            @Override
            public int getTag() {
                return JavaTypeName.INT_TAG;
            }
            
            @Override
            public String getJVMDescriptor() {               
                return "I";
            }
            
            @Override
            JavaTypeName makeArrayType() {
                return JavaTypeName.INT_ARRAY;                
            }            
        }
        
        public static final class Long extends Primitive {
            
            private Long() {/* Constructor made private to prevent instantiation. */}
            
            @Override
            public String getName() {
                return "long";
            }
            
            @Override
            public boolean equals(Object other) {
                return other instanceof Long;                
            }
            
            @Override
            public int getTag() {
                return JavaTypeName.LONG_TAG;
            }
            
            @Override
            public String getJVMDescriptor() {               
                return "J";
            }
            
            @Override
            JavaTypeName makeArrayType() {
                return JavaTypeName.LONG_ARRAY;                
            }            
        }
        
        public static final class Double extends Primitive {
            
            private Double() {/* Constructor made private to prevent instantiation. */}
            
            @Override
            public String getName() {
                return "double";
            }
            
            @Override
            public boolean equals(Object other) {
                return other instanceof Double;                
            }
            
            @Override
            public int getTag() {
                return JavaTypeName.DOUBLE_TAG;
            }
            
            @Override
            public String getJVMDescriptor() {               
                return "D";
            }
            
            @Override
            JavaTypeName makeArrayType() {
                return JavaTypeName.DOUBLE_ARRAY;                
            }            
        }
        
        public static final class Float extends Primitive {
            
            private Float() {/* Constructor made private to prevent instantiation. */}
            
            @Override
            public String getName() {
                return "float";
            }
            
            @Override
            public boolean equals(Object other) {
                return other instanceof Float
                ;                
            } 
            
            @Override
            public int getTag() {
                return JavaTypeName.FLOAT_TAG;
            }
            
            @Override
            public String getJVMDescriptor() {               
                return "F";
            }
            
            @Override
            JavaTypeName makeArrayType() {
                return JavaTypeName.FLOAT_ARRAY;                
            }            
        }
        
        private Primitive() {/* Constructor made private to prevent instantiation. */}
        
        @Override
        public String getFullJavaSourceName() {
            return getName();
        }
        
        @Override
        public String getUnqualifiedJavaSourceName() {
            return getName();
        }
        
        @Override
        public String getPackageName() {
            return "";
        }
        
        @Override
        public boolean isInnerClass() {
            return false;
        }       
        
        /**
         * @return true if this is an Object type.
         */
        @Override
        public boolean isObjectReference () {
            return false;
        }
        
        @Override
        public boolean isInterface() {
            return false;
        }
    }
                 
    /**
     * Represent reference types in Java i.e. array or object types.
     * @author Bo Ilic
     */
    public abstract static class Reference extends JavaTypeName {
        
        /** 
         * The fully qualified name of the class. Uses $ as a separator between nested classes and
         * the period . as the package name separator. Uses the special "right bracket" notation
         * for arrays as specified in the JVM spec i.e. this is the name returned by java.lang.Class.getName().
         */
        private final String name;        
        
        public static final class Array extends Reference {
            
            private final boolean isElementTypeAnInterface;
            
            private String cachedJVMDescriptor;            
            
            private Array(String name, boolean isElementTypeAnInterface) {
                super(name);
                this.isElementTypeAnInterface = isElementTypeAnInterface;
            }
            
            @Override
            public boolean equals(java.lang.Object other) {
                if (other instanceof Array) {
                    return getName().equals(((Array)other).getName());
                }
                
                return false;
            }
            
            @Override
            public int hashCode () {
                return getName().hashCode();
            }
            
            @Override
            public String getFullJavaSourceName() {
                return getFullJavaSourceName(super.name, true);                
            }
            
            /**             
             * @param arrayTypeName the name of the class returned by java.lang.Class.getName().
             * @param replaceDollarSigns true if dollar signs should be replaced by periods
             * @return the fully qualified name of a class as used in Java source code.
             */
            static String getFullJavaSourceName(final String arrayTypeName, boolean replaceDollarSigns) {
                final int dim = getNDimensions(arrayTypeName);
                final StringBuilder sb = new StringBuilder();
                
                switch (arrayTypeName.charAt(dim)) {
                    case 'Z':
                        sb.append("boolean");
                        break;
                    case 'B':
                        sb.append("byte");
                        break;
                    case 'C':
                        sb.append("char");
                        break;
                    case 'D':
                        sb.append("double");
                        break;
                    case 'F':
                        sb.append("float");
                        break;                        
                    case 'I':
                        sb.append("int");
                        break;
                    case 'J':
                        sb.append("long");
                        break;
                    case 'S':
                        sb.append("short");
                        break;
                    case 'L':
                    {
                        //the format is: [[[LelementTypeName;
                        //so we skip the L and the final semicolon
                        //also replace the $ if the element type is an inner class with .
                        final int length = arrayTypeName.length();
                        final String elementTypeName = arrayTypeName.substring(dim + 1, length - 1);
                        final String sourceElementTypeName;
                        if (replaceDollarSigns) {
                            sourceElementTypeName = elementTypeName.replace('$', '.');
                        } else {
                            sourceElementTypeName = elementTypeName;
                        }
                        sb.append(sourceElementTypeName);
                        break;
                    }
                    
                    default: 
                    {
                        throw new IllegalStateException();
                    }
                }
                        
                for (int i = 0; i < dim; ++i) {
                    sb.append("[]");
                }

                return sb.toString();
            }            
            
            @Override
            public String getUnqualifiedJavaSourceName() {
                String sourceName = getFullJavaSourceName();
                int lastDotIndex = sourceName.lastIndexOf('.');
                if (lastDotIndex == -1) {
                    //an array of primitive types, such as int[]
                    return sourceName;
                }
                
                return sourceName.substring(lastDotIndex + 1);
            }
            
            /**             
             * @return the element type of the array. For example, for the 2-dimensional array int[][] this is int.
             */
            public JavaTypeName getElementType() {
                int dim = getNDimensions();
                
                switch (super.name.charAt(dim)) {
                    case 'Z':
                        return JavaTypeName.BOOLEAN;
                        
                    case 'B':
                        return JavaTypeName.BYTE;
                        
                    case 'C':
                        return JavaTypeName.CHAR;
                        
                    case 'D':
                        return JavaTypeName.DOUBLE;
                        
                    case 'F':
                        return JavaTypeName.FLOAT;
                        
                    case 'I':
                        return JavaTypeName.INT;
                        
                    case 'J':
                        return JavaTypeName.LONG;
                       
                    case 'S':
                        return JavaTypeName.SHORT;
                      
                    case 'L':
                    {
                        //the format is: [[[LelementTypeName;
                        //so we skip the L and the final semicolon
                       
                        int length = super.name.length();
                        return new Reference.Object(super.name.substring(dim + 1, length - 1), isElementTypeAnInterface);                       
                    }
                    
                    default: 
                    {
                        throw new IllegalStateException();
                    }
                }                                       
            }             
            
            /**             
             * @return the number of dimensions of this array type. Will be greater than or equal to 1.
             */
            public int getNDimensions() {
               return getNDimensions(super.name);
            }
            
            /**
             * @param arrayTypeName the name of a Java array type, as returned by Class.getName()             
             * @return the number of dimensions of this array type. Will be greater than or equal to 1.
             */            
            static int getNDimensions(final String arrayTypeName) {
                //we know that this is an array, so it is at least 1 dimensional
                int i = 1;
                while (arrayTypeName.charAt(i) == '[') {
                    ++i;
                }
                return i;
            }
            
            /**                         
             * @return the incremental element type of the array. For example, for the 2-dimensional array int[][] this is int[]
             *    (and not int).
             */
            public JavaTypeName getIncrementalElementType() {
                final int dim = getNDimensions();
                
                if (dim == 1) {
                    return getElementType();
                }
                                
                return new Reference.Array(super.name.substring(1), isElementTypeAnInterface);                                                   
            }            
            
            @Override
            public String getPackageName() {
                int dim = getNDimensions();
                
                switch (super.name.charAt(dim)) {
                    case 'Z':                        
                    case 'B':                       
                    case 'C':                       
                    case 'D':  
                    case 'F':
                    case 'I':                       
                    case 'J':                       
                    case 'S':
                        return "";
                        
                    case 'L':
                    {
                        //the format is: [[[LelementTypeName;
                        //so we skip the L and the final semicolon
                        //also replace the $ if the element type is an inner class with .
                        int lastDotIndex = super.name.lastIndexOf('.');
                        return super.name.substring(dim + 1, lastDotIndex);                       
                    }
                    
                    default: 
                    {
                        throw new IllegalStateException();
                    }
                }              
            }                
           
            @Override
            public int getTag() {
                return JavaTypeName.ARRAY_TAG;
            }
            
            @Override
            public String getJVMDescriptor() {
                if (cachedJVMDescriptor != null) {
                    return cachedJVMDescriptor;
                }
                
                return cachedJVMDescriptor = super.name.replace('.', '/');                
            }
            
            @Override
            public JavaTypeName makeArrayType() {
                //delegate to JavaTypeName.make in case the type is in the cache.
                return JavaTypeName.make("[" + super.name, isElementTypeAnInterface);                           
            }
            
            @Override
            public boolean isInnerClass() {
                return false;
            }
            
            /**
             * @return true if this is an Object type.
             */
            @Override
            public boolean isObjectReference () {
                return false;
            }
            
            @Override
            public boolean isInterface() {
                return false;
            }            
            
        }
        
        public static final class Object extends Reference {   
            
            /** true if this type is an interface (rather than a class) */
            private final boolean isInterface;
            
            private String cachedJVMDescriptor;
            private String cachedJVMInternalName;
            
            private Object(String name, boolean isInterface) {
                super(name);
                
                if (name.indexOf('.') == -1) {
                    throw new IllegalArgumentException("The default package is not supported.");
                }
                
                this.isInterface = isInterface;
            }
            
            @Override
            public boolean equals(java.lang.Object other) {
                if (other instanceof Reference.Object) {
                    return getName().equals(((Reference.Object)other).getName());
                }
                
                return false;
            } 
            
            @Override
            public int hashCode () {
                return getName().hashCode();
            }
            
            @Override
            public String getFullJavaSourceName() {
                return getName().replace('$', '.');
            }
            
            @Override
            public String getPackageName() {
                int lastDotIndex = super.name.lastIndexOf('.');
                return super.name.substring(0, lastDotIndex);
            }
            
            @Override
            public int getTag() {
                return JavaTypeName.OBJECT_TAG;
            }
            
            /**
             * Get the Object type's unqualified name in its internal format.
             * This is what is returned by JavaTypeName.getName() without the package part.
             *  $ signs will be present for inner classes.
             * @return what is returned by JavaTypeName.getName() without the package part.
             */
            public String getInternalUnqualifiedName() {
                
                //todoBI change name to getUnqualifiedName().
                
                // find the last '.', and return everything to the right of it.
                int lastDotIndex = super.name.lastIndexOf('.');
                return super.name.substring(lastDotIndex + 1);                    
            }
                           
            @Override
            public String getUnqualifiedJavaSourceName() {
                                    
                // find the last '.', and return everything to the right of it, with '$' replaced by '.'.
                return getInternalUnqualifiedName().replace('$', '.');
            } 
            
            /**
             * Get the base name ie. everything to the right of any '.' or '$'.
             * For example, this is "String" for "java.lang.String" and 
             * "OperatorExpression" for "org.openquark.cal.internal.runtime.lecc.JavaExpression$OperatorExpression"
             * 
             * @return the base name.
             */
            public String getBaseName() {
                // find the last '$' or '.', and return everything to the right of it.
                int index = super.name.lastIndexOf('$');
                if (index == -1) {
                    index = super.name.lastIndexOf('.');
                }
                
                return super.name.substring(index + 1);                                 
            }
            
            /**
             * Get the name of the type that should be imported to import this type.
             * For example, for JavaTypeName.make("org.openquark.cal.internal.runtime.lecc.JavaExpression$OperatorExpression")
             * this is "org.openquark.cal.internal.runtime.lecc.JavaExpression".
             * @return String
             */
            public String getImportName() {
                // find the last '$', and return everything to the left of it.                   
                int index = super.name.indexOf('$');
                if (index != -1) {
                    return super.name.substring(0, index);
                }
                return super.name;
            }
            
            /**
             * For Object types, this is defined in the JVM spec as the fully qualified name of the class (i.e. what is returned by getName()),
             * with '.' replaced by '/'.
             * @return the internal name of this Object type, in the sense of the JVM spec.
             */
            @Override
            public String getJVMInternalName() {
                if (cachedJVMInternalName != null) {
                    return cachedJVMInternalName;
                }
                
                return cachedJVMInternalName = super.name.replace('.', '/');
            }            
            
            @Override
            public String getJVMDescriptor() {  
                if (cachedJVMDescriptor != null) {
                    return cachedJVMDescriptor;
                }
                
                return cachedJVMDescriptor = new StringBuilder("L").append(getJVMInternalName()).append(';').toString();
            }
            
            @Override
            JavaTypeName makeArrayType() {
                //delegate to JavaTypeName.make in case the type is in the cache.
                return JavaTypeName.make(new StringBuilder("[L").append(super.name).append(';').toString(), isInterface);                             
            } 
            
            @Override
            public boolean isInnerClass() {
                return super.name.indexOf('$') != -1;
            }
            
            /**
             * @return true if this is an Object type.
             */
            @Override
            public boolean isObjectReference () {
                return true;
            }
            
            @Override
            public boolean isInterface() {
                return isInterface;
            }                           
        }
        
        private Reference(String name) {
            Assert.isNotNull(name);
            this.name = name;
        }               
        
        @Override
        public String getName() {
            return name;
        }
        
        
    }
    
    private JavaTypeName () {
        // Constructor made private to prevent direct instantiation. 
    }      
    
    /**          
     * @param name the name of the class as returned by the java.lang.Class.getName() method.
     * @param isInterfaceOrInterfaceArray true if this type is an interface (such as java.util.List,
     *      or an array, possibly multidimensional, of interfaces.
     * @return JavaTypeName. May not be a new reference if the JavaTypeName already exists in the cache.     
     */
    public static JavaTypeName make(String name, boolean isInterfaceOrInterfaceArray) {
        //the primitive types are all in the cache.
        JavaTypeName typeName = cachedTypes.get(name);
        if (typeName != null) {
            return typeName;
        }
        
        if (name.charAt(0) == '[') {
            return new Reference.Array(name, isInterfaceOrInterfaceArray);
        }
        
        return new Reference.Object(name, isInterfaceOrInterfaceArray);              
    }
    
    /**     
     * @param type
     * @return JavaTypeName. May not be a new reference if the JavaTypeName already exists in the cache.
     */
    public static JavaTypeName make(Class<?> type) {
        return make(type.getName(), isInterfaceOrInterfaceArray(type));        
    }
    
    /**     
     * @param type
     * @return true if type is an interface itself or is an array, possibly multidimensional of interfaces.
     */
    private static boolean isInterfaceOrInterfaceArray(Class<?> type) {
        if (type.isInterface()) {
            return true;
        }
        
        if (type.isArray()) {  
            //Class.getComponentType returns int[] for int[][]
            return isInterfaceOrInterfaceArray(type.getComponentType());            
        }
        
        //class that is not an array, or a primitive type
        return false;
    }
    
    private static JavaTypeName makeCached(Class<?> type) {
        
        final String name = type.getName();   
        final JavaTypeName typeName;
        
        if (type.isPrimitive()) {
            
            if (type == int.class) {
                typeName = new JavaTypeName.Primitive.Int();                
                
            } else if (type == void.class) {                
                typeName = new JavaTypeName.Primitive.Void();
                
            } else if (type == boolean.class) {
                typeName = new JavaTypeName.Primitive.Boolean();
                
            } else if (type == byte.class) {                
                typeName = new JavaTypeName.Primitive.Byte();
                
            } else if (type == char.class) {                
                typeName = new JavaTypeName.Primitive.Char();
                
            } else if (type == short.class) {
                typeName = new JavaTypeName.Primitive.Short();
                
            } else if (type == double.class) {                
                typeName = new JavaTypeName.Primitive.Double();
                
            } else if (type == float.class) {
                typeName = new JavaTypeName.Primitive.Float();
                
            } else if (type == long.class) {
                typeName = new JavaTypeName.Primitive.Long();
                
            } else {
                throw new IllegalArgumentException();
            }
                        
        } else if (type.isArray()) {
            
            typeName = new Reference.Array(type.getName(), isInterfaceOrInterfaceArray(type));
            
        } else {
                    
            typeName = new Reference.Object(type.getName(), type.isInterface());
        }
        
        //add to cache
        if (cachedTypes.put(name, typeName) != null) {
            throw new IllegalArgumentException("Attempt to cache previously cached type.");
        }
        
        return typeName;        
    }
    
    /**     
     * @param elementType
     * @return a 1-dimensional array having elements of the given elementType (which may itself be an array type)
     */
    public static JavaTypeName makeArrayType(JavaTypeName elementType) {
        return elementType.makeArrayType();
    }
                   
    /**     
     * For example,
     * "int" (i.e. int.class.getName())
     * "void" (void.class.getName())
     * "java.lang.String"
     * "[I" (int[].class.getName())
     * "[Ljava.lang.String;" (java.lang.String[].class.getName())
     * 
     * Inner class names are separated from their enclosing class names via a $.
     * 
     * @return the name of the type as would be returned by java.lang.Class.getName(). 
     */
    abstract public String getName ();
    
    @Override
    abstract public boolean equals(java.lang.Object other);
    
    @Override
    public int hashCode() {
        return getName().hashCode();
    }
    
    /**
     * The full name of the type in Java, i.e. including the package name for non-primitive types, suitable for use in a Java source file.
     * In other words, for array brackets appear at the end of the name in matching pairs (e.g. java.lang.String[])
     * There are no $'s in the names of inner classes (the . separator is used).  
     * 
     * For example, this is
     * "String" for JavaTypeName.make("java.lang.String")
     * "org.openquark.cal.internal.runtime.lecc.JavaExpression.OperatorExpression" for JavaTypeName.make("org.openquark.cal.internal.runtime.lecc.JavaExpression$OperatorExpression")
     * "int" for JavaTypeName.make("int")
     * "void" for JavaTypeName.make("void")
     * "int[]" for JavaTypeName.make("[I")
     * "java.lang.String[]" for JavaTypeName.make("[Ljava.lang.String;")            
     *      
     * @return full name of the type in Java, suitable for use in a Java source file.
     */
    abstract public String getFullJavaSourceName();
    
    /**
     * The full name of the type in Java, i.e. including the package name for non-primitive types, suitable for use in a Java source file.
     * In other words, for array brackets appear at the end of the name in matching pairs (e.g. java.lang.String[])
     * There are no $'s in the names of inner classes (the . separator is used).  
     * 
     * For example, this is
     * "String" for java.lang.String.class
     * "org.openquark.cal.internal.runtime.lecc.JavaExpression.OperatorExpression" for org.openquark.cal.internal.runtime.lecc.JavaExpression.OperatorExpression.class
     * "int" for int.class
     * "void" for void.class
     * "int[]" for int[].class
     * "java.lang.String[]" for java.lang.String[].class            
     *      
     * @param type
     * @return full name of the type in Java, suitable for use in a Java source file.
     */
    static public String getFullJavaSourceName(Class<?> type) {
        if (type.isPrimitive()) {
            return type.getName();
        }
        
        if (type.isArray()) {
            return JavaTypeName.Reference.Array.getFullJavaSourceName(type.getName(), true);
        }
        
        return type.getName().replace('$', '.');              
    }
    
    /**
     * The name of the type, in a canonical form suitable for appearing in CAL source in a foreign data declaration.
     * This is similar to what is returned by getFullJavaSourceName(Class) except that inner classes are separated by a $.
     * 
     * For example, it will return:
     * java.lang.String
     * java.util.Map$Entry
     * int
     * void
     * int[]
     * java.lang.String[]
     * 
     * @param type
     * @return a canonical form of the Java type name, suitable for use in a CAL source file.
     */
    static public String getCalSourceName(Class<?> type) {
        if (type.isPrimitive()) {
            return type.getName();
        }
        
        if (type.isArray()) {
            return JavaTypeName.Reference.Array.getFullJavaSourceName(type.getName(), false);
        }
        
        return type.getName();
    }
    
    /**
     * Get the unqualified name of this type as seen in Java source.
     * This is what is returned by JavaTypeName.getFullSourceName() without the package part.
     * 
     * For example, this is
     * "String" for JavaTypeName.make("java.lang.String")
     * "JavaExpression.OperatorExpression" for JavaTypeName.make("org.openquark.cal.internal.runtime.lecc.JavaExpression$OperatorExpression")
     * "int" for JavaTypeName.make("int")
     * "void" for JavaTypeName.make("void")
     * "int[]" for JavaTypeName.make("[I")
     * "String[]" for JavaTypeName.make("[Ljava.lang.String;")
     * 
     * @return what is returned by JavaTypeName.getFullJavaSourceName() without the package part. Suitable for use in a Java source file.
     */
    abstract public String getUnqualifiedJavaSourceName();
    
    /**    
     * @return package name, or the empty String if there is not package (such as for primitive types, or the array types 
     *   with primitive elements such as int[]. For example, returns "java.lang" for JavaTypeName.make("java.lang.String").
     */
    abstract public String getPackageName();              
    
    abstract public int getTag();
    
    /**
     * As specified in section 4.3 of the Java Virtual Machine Spec.
     * @return the name of the type, in the format of a field descriptor, as specified in the Java language specification.
     */
    abstract public String getJVMDescriptor();   
    
    /**     
     * @return the type of a 1-dimensional array having this JavaTypeName as its element type.
     */
    abstract JavaTypeName makeArrayType();
    
    @Override
    public String toString() {
        return getName();
    }
    
    /**
     * For Object types, this is defined in the JVM spec as the fully qualified name of the class (i.e. what is returned by getName()),
     * with '.' replaced by '/'. For other types we define it as equal to the JVM descriptor.
     * @return the internal name of this Object type, in the sense of the JVM spec.
     */
    public String getJVMInternalName() {
        return getJVMDescriptor();
    }
    
    /**     
     * @return true if an Object type that is an inner class. Note: not true for arrays of inner classes.
     */
    public abstract boolean isInnerClass();
    
    /**
     * @return true if this is an Object type.
     */
    public abstract boolean isObjectReference ();
    
    /**     
     * @return true if this type is in fact an interface. Note that arrays of interfaces return false.
     */
    public abstract boolean isInterface();
}
