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
 * ForeignEntityResolver.java
 * Created: July 4, 2007
 * By: Joseph Wong
 */

package org.openquark.cal.internal.compiler;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * This class contains helper methods for resolving foreign entities from their
 * Java names and type signatures:
 * {@link Class}es, {@link Method}s, {@link Field}s, and {@link Constructor}s.
 * This class is not meant to be instantiated. 
 *
 * @author Bo Ilic
 * @author Joseph Wong
 */
final public class ForeignEntityResolver {

    /**
     * (String -> Class) map from the primitive Java type names such as "int" to their Class objects. 
     */
    static private final Map<String, Class<?>> primitiveJavaTypesMap = new HashMap<String, Class<?>>();
    static {
        //note that ("void", void.class) is intentionally not added here.
        
        primitiveJavaTypesMap.put("char", char.class);
        primitiveJavaTypesMap.put("boolean", boolean.class);
        primitiveJavaTypesMap.put("byte", byte.class);
        primitiveJavaTypesMap.put("short", short.class);
        primitiveJavaTypesMap.put("int", int.class);
        primitiveJavaTypesMap.put("long", long.class);
        primitiveJavaTypesMap.put("float", float.class);                                   
        primitiveJavaTypesMap.put("double", double.class);               
    }

    /**
     * An enumeration class containing constants for the different kinds of status that could result from
     * an attempt to resolve a foreign entity.
     *
     * @author Joseph Wong
     */
    public static final class ResolutionStatus {
        
        /**
         * A textual description of the status. For debug purposes.
         */
        private final String description;
        
        /**
         * Status for a successful attempt at resolving a foreign entity.
         */
        public static final ResolutionStatus SUCCESS = new ResolutionStatus("Success");
        
        /**
         * Status for the case when the foreign entity cannot be found.
         * This corresponds to an exception of the following types:
         * {@link NoSuchMethodException} for resolving methods and constructors,
         * {@link NoSuchFieldException} for resolving fields, and
         * {@link ClassNotFoundException} for resolving classes.
         */
        public static final ResolutionStatus NO_SUCH_ENTITY = new ResolutionStatus("NoSuchEntity");
        
        /**
         * Status for the case when the resolution attempt results in a security violation.
         * This corresponds to an exception of the type {@link SecurityException}.
         */
        public static final ResolutionStatus SECURITY_VIOLATION = new ResolutionStatus("SecurityViolation");
        
        /**
         * Status for the case when a dependee class of the class to be resolved is not found.
         * This corresponds to an error of the type {@link NoClassDefFoundError} with a message explaining the dependee class needed. 
         */
        public static final ResolutionStatus DEPENDEE_CLASS_NOT_FOUND = new ResolutionStatus("DependeeClassNotFound");
        
        /**
         * Status for the case when a class can be found, but cannot be loaded.
         * This corresponds to an error of the type {@link NoClassDefFoundError} without a message.
         */
        public static final ResolutionStatus CANNOT_LOAD_CLASS = new ResolutionStatus("CannotLoadClass");
        
        /**
         * Status for the case when a class cannot be initialized (because its static initializers result in an uncaught exception).
         * This corresponds to an error of the type {@link ExceptionInInitializerError}.
         */
        public static final ResolutionStatus CANNOT_INITIALIZE_CLASS = new ResolutionStatus("CannotInitializeClass");
        
        /**
         * Status for the case when a dependee class has incompatibly changed after the compilation of the dependent class.
         * This corresponds to an error of the type {@link LinkageError}.
         */
        public static final ResolutionStatus LINKAGE_ERROR = new ResolutionStatus("LinkageError");
        
        /**
         * Status for the case when the class to be resolved should be public, but is in fact not public.
         */
        public static final ResolutionStatus NOT_ACCESSIBLE = new ResolutionStatus("NotAccessible");
        
        /**
         * Status for the case when the method/field is expected to be static but is found to be non-static.
         */
        public static final ResolutionStatus EXPECT_STATIC_FOUND_NON_STATIC = new ResolutionStatus("ExpectStaticFoundNonStatic");
        
        /**
         * Status for the case when the method/field is expected to be non-static but is found to be static.
         */
        public static final ResolutionStatus EXPECT_NON_STATIC_FOUND_STATIC = new ResolutionStatus("ExpectNonStaticFoundStatic");
        
        /**
         * Status for the case when the actual return type of the method/field/constructor does not match the declared return type.
         */
        public static final ResolutionStatus ACTUAL_RETURN_TYPE_DOES_NOT_MATCH_DECLARED_RETURN_TYPE = new ResolutionStatus("ActualReturnTypeDoesNotMatchDeclaredReturnType");
        
        /**
         * Private constructor for this enumeration class.
         * @param description a textual description of the status. For debug purposes.
         */
        private ResolutionStatus(final String description) {
            this.description = description;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return description;
        }
    }

    /**
     * This is the abstract base class for representing the result of an attempt to resolve a foreign entity:
     * a class, a method, a constructor, or a field. These different kinds of resolution results are represented
     * by concrete subclasses.
     *
     * @param <T> the type of the resolved entity. This can be a {@link Class}, a {@link Constructor}, a {@link Method}
     *            or a {@link Field}.
     * 
     * @author Joseph Wong
     */
    public static final class ResolutionResult<T> {
        
        /**
         * The resolved entity. Can be null if the resolution failed.
         */
        private final T resolvedEntity;
        
        /**
         * The status of the resolution attempt. Can either be success or one of the failure codes.
         * Cannot be null.
         */
        private final ResolutionStatus status;
        
        /**
         * The message associated with the status.
         * Can be null.
         */
        private final String associatedMessage;
        
        /**
         * The {@link Exception} or {@link Error} associated with the status.
         * Can be null.
         */
        private final Throwable throwable;
        
        /**
         * Private constructor. Only meant to be called by subclasses.
         * @param resolvedEntity the resolved entity. Can be null if the resolution failed.
         * @param status the status of the resolution attempt. Can either be success or one of the failure codes. Cannot be null.
         * @param associatedMessage the associated message. Can be null.
         * @param throwable the associated {@link Exception} or {@link Error}. Can be null.
         */
        private ResolutionResult(final T resolvedEntity, final ResolutionStatus status, final String associatedMessage, final Throwable throwable) {
            if (status == null) {
                throw new NullPointerException();
            }
            this.resolvedEntity = resolvedEntity;
            this.status = status;
            this.associatedMessage = associatedMessage;
            this.throwable = throwable;
        }
        
        /**
         * @return the resolved entity. Can be null if the resolution failed.
         */
        public T getResolvedEntity() {
            return resolvedEntity;
        }
        
        /**
         * @return the status of the resolution attempt. Can either be success or one of the failure codes. Cannot be null. 
         */
        public ResolutionStatus getStatus() {
            return status;
        }
        
        /**
         * @return the message associated with the status. Can be null. 
         */
        public final String getAssociatedMessage() {
            return associatedMessage;
        }
        
        /**
         * @return the {@link Exception} or {@link Error} associated with the status. Can be null. 
         */
        public Throwable getThrowable() {
            return throwable;
        }
    }

    /** Private constructor. */
    private ForeignEntityResolver() {}

    /**
     * Resolves the specified method (with given name and argument types) in the given Class object.
     * @param isStatic true if the Java method is static.
     * @param foreignClass the class containing the method to be resolved.
     * @param methodName the name of the method.
     * @param argTypes the types of the arguments of the method.
     * @param declaredReturnType the Class object corresponding to the declared return type of the foreign function.
     * @return the result of the resolution attempt, which contains a status code and the {@link Method} object,
     *         if the resolution was successful.
     */
    public static ResolutionResult<Method> resolveMethod(final boolean isStatic, final Class<?> foreignClass, final String methodName, final Class<?>[] argTypes, final Class<?> declaredReturnType) {
        
        Method method = null;
        
        ResolutionStatus status = ResolutionStatus.SUCCESS;
        String associatedMessage = null;
        Throwable exceptionOrError = null;
        
        try {  
            method = foreignClass.getMethod(methodName, argTypes);        
           
        } catch (final NoSuchMethodException e) {
            //todoBI it would be better to provide a more precise error message in the case where the desired
            //method exists but is not accessible. This could be done by analyzing the returned methods
            //from Class.getMethods() which includes all the methods (i.e. non-publics as well).
            //The difficulty is in reverse engineering the lookup process that Class.getMethod() does to locate the method with
            //the given specified arguments.     
            //Note that a similar comment also applied to the code resolving Java fields and constructors, but I've only put the
            //todo here.
            
            status = ResolutionStatus.NO_SUCH_ENTITY;
            exceptionOrError = e;
            
        } catch (final SecurityException e) {
            status = ResolutionStatus.SECURITY_VIOLATION;
            exceptionOrError = e;
            
        } catch (final NoClassDefFoundError e) {
            // Class.getMethod() indirectly calls an internal version of Class.getDeclaredMethods().
            // This can cause a NoClassDefFoundError to be thrown if any of the Classes used in any methods are not findable.
            
            final String notFoundClass = e.getMessage();
    
            if (notFoundClass != null) {
                status = ResolutionStatus.DEPENDEE_CLASS_NOT_FOUND;
                associatedMessage = notFoundClass;
            } else {
                status = ResolutionStatus.CANNOT_LOAD_CLASS;
            }
            
            exceptionOrError = e;
    
        } catch (final LinkageError e) {
            // Class.getMethod() indirectly calls an internal version of Class.getDeclaredMethods().
            // This can cause a LinkageError to be thrown if there is a problem in any of the Classes used in any of the other methods.
    
            status = ResolutionStatus.LINKAGE_ERROR;
            exceptionOrError = e;
        }
    
        // If we actually got a Method object, we need to run more checks on it.
        if (method != null) {
            //check that the method is actually static if so specified within CAL. 
            //Note that this is a necessary check since in some degenerate cases this is not caught otherwise. For example:
            //public TypeExpr getResultType() {...} //within the TypeExpr class 
            //foreign unsafe import jvm "static method org.openquark.cal.compiler.TypeExpr.getResultType" public getResultType :: TypeExpr;
            if (isStatic != Modifier.isStatic(method.getModifiers())) { 
                if (isStatic) {
                    status = ResolutionStatus.EXPECT_STATIC_FOUND_NON_STATIC;
                } else {
                    status = ResolutionStatus.EXPECT_NON_STATIC_FOUND_STATIC;
                }            
            }        
    
            //check that the actual return type is the same as the declared return type or a subclass of it
    
            final Class<?> actualReturnType = method.getReturnType();
    
            if (!declaredReturnType.isAssignableFrom(actualReturnType)) {
                status = ResolutionStatus.ACTUAL_RETURN_TYPE_DOES_NOT_MATCH_DECLARED_RETURN_TYPE;    
            }
        }
        
        return new ResolutionResult<Method>(method, status, associatedMessage, exceptionOrError);
    }

    /**
     * Resolves the specified field in the given Class object.
     * @param isStatic true if the Java field is static.
     * @param foreignClass the class containing the field to be resolved.
     * @param fieldName the name of the field.
     * @param declaredReturnType the Class object corresponding to the declared return type of the foreign function.
     * @return the result of the resolution attempt, which contains a status code and the {@link Field} object,
     *         if the resolution was successful.
     */
    public static ResolutionResult<Field> resolveField(final boolean isStatic, final Class<?> foreignClass, final String fieldName, final Class<?> declaredReturnType) {
        
        Field field = null;
        
        ResolutionStatus status = ResolutionStatus.SUCCESS;
        String associatedMessage = null;
        Throwable exceptionOrError = null;
        
        try {  
            field = foreignClass.getField(fieldName);        
           
        } catch (final NoSuchFieldException e) {
            status = ResolutionStatus.NO_SUCH_ENTITY;
            exceptionOrError = e;
            
        } catch (final SecurityException e) {
            status = ResolutionStatus.SECURITY_VIOLATION;
            exceptionOrError = e;
            
        } catch (final NoClassDefFoundError e) {
            // Class.getField() indirectly calls an internal version of Class.getDeclaredFields().
            // This can cause a NoClassDefFoundError to be thrown if any of the Classes used in any fields are not findable.
            
            final String notFoundClass = e.getMessage();
    
            if (notFoundClass != null) {
                status = ResolutionStatus.DEPENDEE_CLASS_NOT_FOUND;
                associatedMessage = notFoundClass;
            } else {
                status = ResolutionStatus.CANNOT_LOAD_CLASS;
            }
            
            exceptionOrError = e;
    
        } catch (final LinkageError e) {
            // Class.getField() indirectly calls an internal version of Class.getDeclaredFields().
            // This can cause a LinkageError to be thrown if there is a problem in any of the Classes used in any of the other fields.
    
            status = ResolutionStatus.LINKAGE_ERROR;
            exceptionOrError = e;
        }
        
        // If we actually got a Field object, we need to run more checks on it.
        if (field != null) {
            //check that the field is actually static if so specified within CAL. 
            //Note that this is a necessary check since in some degenerate cases this is not caught otherwise. For example:        
            if (isStatic != Modifier.isStatic(field.getModifiers())) {
                if (isStatic) {
                    status = ResolutionStatus.EXPECT_STATIC_FOUND_NON_STATIC;
                } else {
                    status = ResolutionStatus.EXPECT_NON_STATIC_FOUND_STATIC;
                }            
            }                      
    
            //check that the actual return type is the same as the declared return type or a subclass of it
    
            final Class<?> actualReturnType = field.getType();
    
            if (!declaredReturnType.isAssignableFrom(actualReturnType)) {
                status = ResolutionStatus.ACTUAL_RETURN_TYPE_DOES_NOT_MATCH_DECLARED_RETURN_TYPE;    
            }
        }
        
        return new ResolutionResult<Field>(field, status, associatedMessage, exceptionOrError);
    }

    /**
     * Resolves the specified constructor (with the specified argument types) in the given Class object.
     * @param objectType the class containing the constructor to be resolved.
     * @param argTypes the types of the arguments of the constructor .
     * @param declaredReturnType the Class object corresponding to the declared return type of the foreign function.
     * @return the result of the resolution attempt, which contains a status code and the {@link Constructor} object,
     *         if the resolution was successful.
     */
    public static ResolutionResult<Constructor<?>> resolveConstructor(final Class<?> objectType, final Class<?>[] argTypes, final Class<?> declaredReturnType) {
        
        Constructor<?> constructor = null;
        
        ResolutionStatus status = ResolutionStatus.SUCCESS;
        String associatedMessage = null;
        Throwable exceptionOrError = null;
        
        try {  
            constructor = objectType.getConstructor(argTypes);        
           
        } catch (final NoSuchMethodException e) {
            status = ResolutionStatus.NO_SUCH_ENTITY;
            exceptionOrError = e;
            
        } catch (final SecurityException e) {
            status = ResolutionStatus.SECURITY_VIOLATION;
            exceptionOrError = e;
            
        } catch (final NoClassDefFoundError e) {
            // Class.getConstructor() indirectly calls an internal version of Class.getDeclaredConstructors().
            // This can cause a NoClassDefFoundError to be thrown if any of the Classes used in any constructors are not findable.
            
            final String notFoundClass = e.getMessage();
    
            if (notFoundClass != null) {
                status = ResolutionStatus.DEPENDEE_CLASS_NOT_FOUND;
                associatedMessage = notFoundClass;
            } else {
                status = ResolutionStatus.CANNOT_LOAD_CLASS;
            }
            
            exceptionOrError = e;
    
        } catch (final LinkageError e) {
            // Class.getConstructor() indirectly calls an internal version of Class.getDeclaredConstructors().
            // This can cause a LinkageError to be thrown if there is a problem in any of the Classes used in any of the other constructors.
    
            status = ResolutionStatus.LINKAGE_ERROR;
            exceptionOrError = e;
        }
        
        //check that the actual return type is the same as the declared return type or a subclass of it
        if (!declaredReturnType.isAssignableFrom(objectType)) {
            status = ResolutionStatus.ACTUAL_RETURN_TYPE_DOES_NOT_MATCH_DECLARED_RETURN_TYPE;    
        }
                  
        return new ResolutionResult<Constructor<?>>(constructor, status, associatedMessage, exceptionOrError);
    }

    /**
     * Resolves the specified class using the given class loader.
     * @param className the name of the class to be resolved.
     * @param classLoader the class loader to use for loading the requested class.
     * @return the result of the resolution attempt, which contains a status code and the {@link Class} object,
     *         if the resolution was successful.
     */
    public static ResolutionResult<Class<?>> resolveClass(final String className, final ClassLoader classLoader) {
        
        Class<?> theClass = null;
        
        ResolutionStatus status = ResolutionStatus.SUCCESS;
        String associatedMessage = null;
        Throwable exceptionOrError = null;
        
        try {
            theClass = Class.forName(className, true, classLoader); 
                        
        } catch (final ClassNotFoundException e) {
            status = ResolutionStatus.NO_SUCH_ENTITY;
            exceptionOrError = e;
    
        } catch (final NoClassDefFoundError e) {
    
            final String notFoundClass = e.getMessage();
    
            if (notFoundClass != null) {
                status = ResolutionStatus.DEPENDEE_CLASS_NOT_FOUND;
                associatedMessage = notFoundClass;
            } else {
                status = ResolutionStatus.CANNOT_LOAD_CLASS;
            }
            
            exceptionOrError = e;
    
        } catch (final ExceptionInInitializerError e) {
            //Class.forName could also throw an ExceptionInInitializerError.
            status = ResolutionStatus.CANNOT_INITIALIZE_CLASS;
            exceptionOrError = e;
            
        } catch (final LinkageError e) {
            status = ResolutionStatus.LINKAGE_ERROR;
            exceptionOrError = e;
        }
    
        // If everything up until now is okay, then check the access modifier of the class.
        if (status == ResolutionStatus.SUCCESS) {
            //Class.forName will successfully locate non-public classes and interfaces. However, these cannot be used successfully
            //from within CAL since they will cause access violation exceptions when running CAL functions that make use of these types.
            //
            //We also exclude classes in an unnamed package. These can't be imported by classes outside the unnamed
            //package so they effectively cannot be used by a CAL module, since its generated code is always within a named package.
            if (!Modifier.isPublic(theClass.getModifiers()) || inUnnamedPackage(theClass)) {
                status = ResolutionStatus.NOT_ACCESSIBLE;
            }
        }
            
        return new ResolutionResult<Class<?>>(theClass, status, associatedMessage, exceptionOrError);                
    }

    /**
     * A helper function to convert Java type names to the format required by Class.forName.
     * 
     * @param javaSourceName the name of the Java type from CAL source e.g. essentially the fully qualified Java souce name 
     *     except that inner classes are separated using a $.
     * @return the name 
     */
    public static String javaSourceReferenceNameToJvmInternalName(final String javaSourceName) {
        if (primitiveJavaTypesMap.containsKey(javaSourceName) || javaSourceName.equals("void")) {
            throw new IllegalStateException("the name of a reference type is required");
        }
         
        //count the number of 
        int arrayDim = 0;
        for (int currentPos = javaSourceName.length() - 1;
            currentPos - 1 >= 0 && javaSourceName.charAt(currentPos - 1) == '[' && javaSourceName.charAt(currentPos) == ']';
            currentPos -= 2) {
            ++arrayDim;
        }
         
        if (arrayDim > 0) {
                        
            
            final char[] arrayHeader = new char[arrayDim];
            Arrays.fill(arrayHeader, '[');
            
            final String javaSourceElementTypeName = javaSourceName.substring(0, javaSourceName.length() - 2*arrayDim);
            final String internalElementTypeName;
            
            
            if (primitiveJavaTypesMap.containsKey(javaSourceElementTypeName)) {
                
                //there are special element type names for the primitive types
                
                if (javaSourceElementTypeName.equals("boolean")) {
                    internalElementTypeName = "Z";                     
                } else if (javaSourceElementTypeName.equals("byte")) {
                    internalElementTypeName = "B";
                } else if (javaSourceElementTypeName.equals("char")) {
                    internalElementTypeName = "C";
                } else if (javaSourceElementTypeName.equals("double")) {
                    internalElementTypeName = "D";
                } else if (javaSourceElementTypeName.equals("float")) {
                    internalElementTypeName = "F";
                } else if (javaSourceElementTypeName.equals("int")) {
                    internalElementTypeName = "I";
                } else if (javaSourceElementTypeName.equals("long")) {
                    internalElementTypeName = "J";
                } else if (javaSourceElementTypeName.equals("short")) {
                    internalElementTypeName = "S";
                } else {
                    throw new IllegalStateException();
                }                
                
            } else {
                internalElementTypeName = new StringBuilder("L").append(javaSourceElementTypeName).append(";").toString();
            }
            
            return new StringBuilder(String.valueOf(arrayHeader)).append(internalElementTypeName).toString();
            
        }
        return javaSourceName;
        
    }

    /**
     * A helper function that converts from String names of primitive types to their class object. This is need because
     * Class.forName does not recognize the primitive type names.
     * @param primitiveJavaTypeName primitive Java type name such as "int"
     * @return Class the Class object for the primitive type e.g. int.class
     */
    public static Class<?> getPrimitiveType(final String primitiveJavaTypeName) {
        return primitiveJavaTypesMap.get(primitiveJavaTypeName);
    }
    
    /**        
     * @param type
     * @return true if the type belongs to an unnamed package (also called "the default package").
     *      Note that primitive types and primitive array types are not in an unnamed package.
     */
    static private boolean inUnnamedPackage(final Class<?> type) {
       
        //If there is no period in the name, and the type is not a primitive or primitive array type
        //then it is in an unnamed package
        
        //the element type e.g. int for int[][][]
        Class<?> elemType = type;      
        while (elemType.getComponentType() != null) {
            elemType = elemType.getComponentType();                
        } 
        
        if (elemType.isPrimitive()) {
            return false;
        }
                     
        return elemType.getName().indexOf('.') == -1;
    }    
}
