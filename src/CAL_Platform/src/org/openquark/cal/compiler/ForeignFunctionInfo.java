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
 * ForeignFunctionInfo.java
 * Created: May 6, 2002
 * By: Bo Ilic
 */
package org.openquark.cal.compiler;

import java.io.IOException;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.openquark.cal.internal.serialization.ModuleSerializationTags;
import org.openquark.cal.internal.serialization.RecordInputStream;
import org.openquark.cal.internal.serialization.RecordOutputStream;
import org.openquark.cal.internal.serialization.RecordInputStream.RecordHeaderInfo;
import org.openquark.util.Pair;


/**
 * Class used to provide the information needed to call a CAL foreign function.
 * The CAL foreign function can be, as a Java entity, one of:
 * <ol>
 *      <li> a Java (non-static) method
 *      <li> a Java static method
 *      <li> a Java (non-static) field
 *      <li> a Java static field
 *      <li> a Java constructor
 *      <li> a Java identity cast (conversion) (see the JVM spec section 2.6 for the definition of the various conversions)
 *      <li> a Java widening primitive cast (conversion)
 *      <li> a Java narrowing primitive cast (conversion)
 *      <li> a Java widening reference cast (conversion)
 *      <li> a Java narrowing reference cast (conversion) 
 *      <li> a Java instanceof operator 
 *      <li> a Java null reference value
 *      <li> a Java isNull reference check
 *      <li> a Java isNotNull reference check
 *      <li> array creation (new array)
 *      <li> array length
 *      <li> array subscript
 *      <li> array update
 *      <li> a Java class literal
 * </ol>
 * <p>
 * Note in particular that the Java entity need not be a Java function e.g. conversions corresponds to casts in Java.
 * They are however exposed as functions within CAL.
 * <p>
 * This class must remain immutable, as objects of the class are intended to be shared.
 * <p>
 * Creation date: (May 6, 2002)
 * @author Bo Ilic 
 */
public abstract class ForeignFunctionInfo {
           
    /** CAL function name corresponding to this ForeignFunctionInfo object */
    private final QualifiedName calName;
    
    /** whether a method, static method, field, static field, constructor or cast. */
    private final JavaKind javaKind;
    
    /**
     * The array of possible record tags used in calls to {@link RecordInputStream#findRecord(short[])} by
     * the {@link #load} method.
     */
    private static final short[] SERIALIZATION_RECORD_TAGS = new short[] {
        ModuleSerializationTags.FOREIGN_FUNCTION_INFO_INVOCATION,
        ModuleSerializationTags.FOREIGN_FUNCTION_INFO_CAST,
        ModuleSerializationTags.FOREIGN_FUNCTION_INFO_INSTANCE_OF,
        ModuleSerializationTags.FOREIGN_FUNCTION_INFO_NULL_LITERAL,
        ModuleSerializationTags.FOREIGN_FUNCTION_INFO_NULL_CHECK,
        ModuleSerializationTags.FOREIGN_FUNCTION_INFO_NEW_ARRAY,
        ModuleSerializationTags.FOREIGN_FUNCTION_INFO_LENGTH_ARRAY,
        ModuleSerializationTags.FOREIGN_FUNCTION_INFO_SUBSCRIPT_ARRAY,
        ModuleSerializationTags.FOREIGN_FUNCTION_INFO_UPDATE_ARRAY,
        ModuleSerializationTags.FOREIGN_FUNCTION_INFO_CLASS_LITERAL
    };

    /**
     * Type-safe enumeration describing the different kinds of Java entities that can be imported into CAL
     * via a foreign function declaration.
     * 
     * @author Bo Ilic
     */                            
    public static final class JavaKind {
        
        private static final int serializationSchema = 0;
              
        private final String description;
        private final int ordinal;
        
        private static final int METHOD_ORDINAL = 0;
        private static final int STATIC_METHOD_ORDINAL = 1;
        private static final int FIELD_ORDINAL = 2;
        private static final int STATIC_FIELD_ORDINAL = 3;
        private static final int CONSTRUCTOR_ORDINAL = 4;       
        private static final int IDENTITY_CAST_ORDINAL = 5;
        private static final int WIDENING_PRIMITIVE_CAST_ORDINAL = 6;
        private static final int NARROWING_PRIMITIVE_CAST_ORDINAL = 7;
        private static final int WIDENING_REFERENCE_CAST_ORDINAL = 8;
        private static final int NARROWING_REFERENCE_CAST_ORDINAL = 9;
        private static final int INSTANCE_OF_ORDINAL = 10;
        private static final int NULL_LITERAL_ORDINAL = 11;
        private static final int NULL_CHECK_ORDINAL = 12;       
        private static final int NEW_ARRAY_ORDINAL = 13;
        private static final int LENGTH_ARRAY_ORDINAL = 14;
        private static final int SUBSCRIPT_ARRAY_ORDINAL = 15;
        private static final int UPDATE_ARRAY_ORDINAL = 16;
        private static final int CLASS_LITERAL_ORDINAL = 17;
        
        public static final JavaKind METHOD = new JavaKind ("method", METHOD_ORDINAL);
        public static final JavaKind STATIC_METHOD = new JavaKind ("static method", STATIC_METHOD_ORDINAL);       
        public static final JavaKind FIELD = new JavaKind ("field", FIELD_ORDINAL);
        public static final JavaKind STATIC_FIELD = new JavaKind ("static field", STATIC_FIELD_ORDINAL);
        public static final JavaKind CONSTRUCTOR = new JavaKind ("constructor", CONSTRUCTOR_ORDINAL);
        /**
         * A cast between any Java type T and itself.
         */
        public static final JavaKind IDENTITY_CAST = new JavaKind ("identity cast", IDENTITY_CAST_ORDINAL);
        /**
         * A valid cast from a Java primitive type T to a Java primitive type S that is not an identity cast
         * and such that the numeric value is preserved exactly e.g. casting an int to a long. Casting a float
         * to a double is also a widening primitive cast, but only necessarily preserve the value exactly in strictfp mode.
         */
        public static final JavaKind WIDENING_PRIMITIVE_CAST = new JavaKind("widening primitive cast", WIDENING_PRIMITIVE_CAST_ORDINAL);
        /**
         * A valid cast from a Java primitive type T to a Java primitive type S that is not an identity cast nor a widening primitive
         * cast. Such casts can lose precision about the numeric value e.g. casting long to a byte.         
         */
        public static final JavaKind NARROWING_PRIMITIVE_CAST = new JavaKind("narrowing primitive cast", NARROWING_PRIMITIVE_CAST_ORDINAL);        
        /**
         * A cast of a Java reference type to a supertype.
         */
        public static final JavaKind WIDENING_REFERENCE_CAST = new JavaKind("widening reference cast", WIDENING_REFERENCE_CAST_ORDINAL);
         /**
         * A cast of a Java reference type to another Java reference type that is not an identity cast or widening reference cast.
         * These are casts whose validy must be checked at runtime. Java does not allow narrowing casts that can be guaranteed to 
         * fail at compile time.
         */
        public static final JavaKind NARROWING_REFERENCE_CAST = new JavaKind("narrowing reference cast", NARROWING_REFERENCE_CAST_ORDINAL);
        /**
         * A call to the Java "instanceof" primitive operator.
         */
        public static final JavaKind INSTANCE_OF = new JavaKind("instanceof", INSTANCE_OF_ORDINAL);        
        /**
         * A Java literal null value.
         */
        public static final JavaKind NULL_LITERAL = new JavaKind("null", NULL_LITERAL_ORDINAL);        
        /**
         * Either "expr == null" or "expr != null".
         */
        public static final JavaKind NULL_CHECK = new JavaKind("nullCheck", NULL_CHECK_ORDINAL);
        /**
         * A Java class literal (of Java type java.lang.Class).
         */
        public static final JavaKind CLASS_LITERAL = new JavaKind("class", CLASS_LITERAL_ORDINAL);        
        
        public static final JavaKind NEW_ARRAY = new JavaKind("newArray", NEW_ARRAY_ORDINAL);
        public static final JavaKind LENGTH_ARRAY = new JavaKind("lengthArray", LENGTH_ARRAY_ORDINAL);
        public static final JavaKind UPDATE_ARRAY = new JavaKind("updateArray", UPDATE_ARRAY_ORDINAL);
        public static final JavaKind SUBSCRIPT_ARRAY = new JavaKind("subscriptArray", SUBSCRIPT_ARRAY_ORDINAL);
                           
        private JavaKind (final String description, final int ordinal) {
            this.description = description;  
            this.ordinal = ordinal;
        }
        
        /**        
         * @return true for a method (static or not), field (static or not) or constructor invocation.
         */
        public boolean isInvocation() {
            return isMethod() || isField() || isConstructor();
        }       
        
        /** 
         * @return boolean true if this is a method or static method
         */
        public boolean isMethod() {
            return this == METHOD || this == STATIC_METHOD;
        }
        
        /**
         * @return boolean true if this is a field or static field
         */
        public boolean isField() {
            return this == FIELD || this == STATIC_FIELD;
        }
        
        /**
         * @return boolean true if this is a constructor
         */
        public boolean isConstructor() {
            return this == CONSTRUCTOR;
        }
        
        /**
         * @return boolean true if this is a cast
         */
        public boolean isCast() {
            switch (ordinal) 
            {
                case IDENTITY_CAST_ORDINAL:
                case WIDENING_PRIMITIVE_CAST_ORDINAL:
                case NARROWING_PRIMITIVE_CAST_ORDINAL:
                case WIDENING_REFERENCE_CAST_ORDINAL:
                case NARROWING_REFERENCE_CAST_ORDINAL:
                    return true;
                default:
                    return false;
            }                     
        } 
        
        /**         
         * @return true if this is the "instanceof" JavaKind.
         */
        public boolean isInstanceOf() {
            return this == INSTANCE_OF;
        }
        
        public boolean isNullLiteral() {
            return this == NULL_LITERAL;
        }
        
        public boolean isNullCheck() {
            return this == NULL_CHECK;
        }
        
        /**
         * @return true if this is the "class" JavaKind.
         */
        public boolean isClassLiteral() {
            return this == CLASS_LITERAL;
        }
               
               
        /**
         * @return boolean true if this is a static method, static field, or constructor
         */
        public boolean isStatic() {
            return this == STATIC_METHOD || this == STATIC_FIELD || this == CONSTRUCTOR;
        }                    
        
        /**
         * {@inheritDoc}
         */
        @Override
        public String toString () {
            return description;
        }
        
        /**
         * Write the JavaKind instance to the RecordOutputStream.
         * @param s
         * @throws IOException
         */
        final void write (final RecordOutputStream s) throws IOException {
            s.startRecord(ModuleSerializationTags.FOREIGN_FUNCTION_INFO_JAVA_KIND, serializationSchema);
            s.writeByte(ordinal);
            s.endRecord();
        }
        
        /**
         * Load an instance of JavaKind from the RecordInputStream.
         * @param s
         * @param moduleName the name of the module being loaded
         * @param msgLogger the logger to which to log deserialization messages.
         * @return an instance of Kind
         * @throws IOException
         */
        static final JavaKind load (final RecordInputStream s, final ModuleName moduleName, final CompilerMessageLogger msgLogger) throws IOException {
            // Look for Record header.
            final RecordHeaderInfo rhi = s.findRecord(ModuleSerializationTags.FOREIGN_FUNCTION_INFO_JAVA_KIND);
            if(rhi == null) {
               throw new IOException("Unable to find ForeignFunctionInfo.JavaKind record header.");
            }
            DeserializationHelper.checkSerializationSchema(rhi.getSchema(), serializationSchema, moduleName, "ForeignFunctionInfo.JavaKind", msgLogger);

            final byte ordinalValue = s.readByte();
            s.skipRestOfRecord();

            switch (ordinalValue) {
                case METHOD_ORDINAL:
                    return METHOD;
                
                case STATIC_METHOD_ORDINAL:
                    return STATIC_METHOD;
                    
                case FIELD_ORDINAL:
                    return FIELD;
                    
                case STATIC_FIELD_ORDINAL:
                    return STATIC_FIELD;
                    
                case CONSTRUCTOR_ORDINAL:
                    return CONSTRUCTOR;
                    
                case IDENTITY_CAST_ORDINAL:
                    return IDENTITY_CAST;
                 
                case WIDENING_PRIMITIVE_CAST_ORDINAL:
                    return WIDENING_PRIMITIVE_CAST;
                    
                case NARROWING_PRIMITIVE_CAST_ORDINAL:
                    return NARROWING_PRIMITIVE_CAST;
                    
                case WIDENING_REFERENCE_CAST_ORDINAL:
                    return WIDENING_REFERENCE_CAST;
                    
                case NARROWING_REFERENCE_CAST_ORDINAL:
                    return NARROWING_REFERENCE_CAST;  
                    
                case INSTANCE_OF_ORDINAL:
                    return INSTANCE_OF;
                    
                case NULL_LITERAL_ORDINAL:
                    return NULL_LITERAL;
                    
                case NULL_CHECK_ORDINAL:
                    return NULL_CHECK;  
                    
                case NEW_ARRAY_ORDINAL:
                    return NEW_ARRAY;
                    
                case LENGTH_ARRAY_ORDINAL:
                    return LENGTH_ARRAY;
                    
                case UPDATE_ARRAY_ORDINAL:
                    return UPDATE_ARRAY;
                    
                case SUBSCRIPT_ARRAY_ORDINAL:
                    return SUBSCRIPT_ARRAY;
                    
                case CLASS_LITERAL_ORDINAL:
                    return CLASS_LITERAL;
            }
                                             
            throw new IOException("Unable to resolve JavaKind with ordinal: " + ordinalValue);            
        }
    }    
    
    /**
     * An abstract base class for a resolver which resolves a pair of classes at the same time, potentially running validity
     * checks on the pair of classes. This class is meant to act as an adapter, for it provides a 
     * {@link org.openquark.cal.compiler.ForeignEntityProvider.Resolver Resolver} implementation for each of the first and second
     * class in the pair.
     *
     * @author Joseph Wong
     */
    private static abstract class ClassPairResolver {
        
        /**
         * The cached pair of classes. Initially null. A value will be assigned on the first call to
         * {@link #synchronizedGet}. This field must be accessed in a thread-safe manner.
         */
        private Pair<Class<?>, Class<?>> pair;
        
        /**
         * A Resolver for the first class in the pair.
         */
        private final ForeignEntityProvider.Resolver<Class<?>> firstResolver;
        
        /**
         * A Resolver for the second class in the pair.
         */
        private final ForeignEntityProvider.Resolver<Class<?>> secondResolver;
        
        /**
         * Constructor for this abstract base class.
         * @param firstDisplayName a display name for the first class.
         * @param secondDisplayName a display name for the second class.
         */
        ClassPairResolver(final String firstDisplayName, final String secondDisplayName) {
            
            firstResolver = new ForeignEntityProvider.Resolver<Class<?>>(firstDisplayName) {
                @Override
                Class<?> resolve(final ForeignEntityProvider.MessageHandler messageHandler) throws UnableToResolveForeignEntityException {
                    return synchronizedGet(messageHandler).fst();
                }
            };
            
            secondResolver = new ForeignEntityProvider.Resolver<Class<?>>(secondDisplayName) {
                @Override
                Class<?> resolve(final ForeignEntityProvider.MessageHandler messageHandler) throws UnableToResolveForeignEntityException {
                    return synchronizedGet(messageHandler).snd();
                }
            };
        }
        
        /**
         * Resolves the pair of classes from their specs.
         * @param messageHandler the MessageHandler to use for handling {@link CompilerMessage}s.
         * @return the pair of classes.
         * @throws UnableToResolveForeignEntityException if this exception is thrown by the message handler, it is propagated.
         */
        abstract Pair<Class<?>, Class<?>>resolveClassPair(ForeignEntityProvider.MessageHandler messageHandler) throws UnableToResolveForeignEntityException;
        
        /**
         * Returns the pair of classes, running the resolution logic and caching its result on first invocation.
         * @return the pair of classes.
         * @throws UnableToResolveForeignEntityException if this exception is thrown by the message handler, it is propagated.
         */
        private final synchronized Pair<Class<?>, Class<?>> synchronizedGet(final ForeignEntityProvider.MessageHandler messageHandler) throws UnableToResolveForeignEntityException {
            if (pair == null) {
                pair = resolveClassPair(messageHandler);
            }
            return pair;
        }
        
        /**
         * @return a Resolver for the first class in the pair.
         */
        ForeignEntityProvider.Resolver<Class<?>> getFirstResolver() {
            return firstResolver;
        }
        
        /**
         * @return a Resolver for the second class in the pair.
         */
        ForeignEntityProvider.Resolver<Class<?>> getSecondResolver() {
            return secondResolver;
        }
    }
    
    /**
     * Information about a CAL foreign function that is in fact a Java invocation of a
     * field (static or non-static), method (static or non-static) or constructor.
     * 
     * @author Bo Ilic
     */
    public static final class Invocation extends ForeignFunctionInfo {
        
        private static final int serializationSchema = 0;

        /** Provider for the field, method or constructor that this foreign function corresponds to. */
        private final ForeignEntityProvider<? extends AccessibleObject> javaProxyProvider;

        /**     
         * Provider for the class from which to invoke a method or field (both static and non-static) which cannot be null,
         * unless this is a constructor invocation.
         * <i>The provider itself will be null only for constructors.</i>
         *  
         * It is sometimes necessary to invoke a method/field from a class other than which it was defined.
         * For example, if package scope class A defines a static public field f, and public class B extends A, 
         * then B.f in a different package will not result in a compilation error but A.f will.
         * 
         * Or for example, if package scope class A defines a non-static public method m, and public class B extends A, 
         * then in a different package we cannot invoke m on an object of type B if:
         * - the invocation is done via reflection, or
         * - the reference is first cast to the method's declared type, in this case A, i.e. ((A)b).m()
         */
        private final ForeignEntityProvider<Class<?>> invocationClassProvider;
        
        /**
         * Provider for the class corresponding to the declared return type of the foreign function, which cannot be null.
         */
        private final ForeignEntityProvider<Class<?>> declaredReturnTypeProvider;

        /** number of arguments, considered as a CAL foreign function. */
        private int nArguments = -1; // the initial sentinel value is -1, which will be replaced by the correct value on first access 
        
        /**    
         * Private constructor for a method, field or constructor invocation.
         * @param calName CAL name of the foreign function e.g. Prelude.sin
         * @param invocationClassProvider provider for the class from which to invoke a method or field (both static and non-static) which cannot be null,
         *      unless this is a constructor invocation. 
         * @param javaProxyProvider provider for the accessible object corresponding to the foreign function e.g. "java.lang.Math.sqrt", which can be
         *      a Java method, constructor or field.
         * @param declaredReturnTypeProvider provider for the class corresponding to the declared return type of the foreign function, which cannot be null.
         * @param javaKind the JavaKind describing the kind of Java entity represented (static/non-static method, static/non-static field, or constructor)
         */
        private Invocation(final QualifiedName calName, final ForeignEntityProvider<Class<?>> invocationClassProvider, final ForeignEntityProvider<? extends AccessibleObject> javaProxyProvider, final ForeignEntityProvider<Class<?>> declaredReturnTypeProvider, final JavaKind javaKind) {

            super(calName, javaKind);
            
            if (javaProxyProvider == null || declaredReturnTypeProvider == null) {
                throw new NullPointerException();
            }
            
            this.invocationClassProvider = invocationClassProvider; // can be null
            this.javaProxyProvider = javaProxyProvider;
            this.declaredReturnTypeProvider = declaredReturnTypeProvider;
        }
        
        /**
         * Factory method for a static method invocation.
         * @param calName CAL name of the foreign function e.g. Prelude.sin
         * @param invocationClassProvider provider for the class from which to invoke a static method. The provider cannot be null, and the provided class cannot be null. 
         * @param javaProxyProvider provider for the accessible object corresponding to the foreign function e.g. "java.lang.Math.sqrt", which must be
         *      a Java method.
         * @param declaredReturnTypeProvider provider for the class corresponding to the declared return type of the foreign function, which cannot be null.
         * @return an Invocation instance.
         */
        static Invocation makeStaticMethod(final QualifiedName calName, final ForeignEntityProvider<Class<?>> invocationClassProvider, final ForeignEntityProvider<Method> javaProxyProvider, final ForeignEntityProvider<Class<?>> declaredReturnTypeProvider) {
            return new Invocation(calName, invocationClassProvider, javaProxyProvider, declaredReturnTypeProvider, JavaKind.STATIC_METHOD);
        }

        /**
         * Factory method for a non-static method invocation.
         * @param calName CAL name of the foreign function e.g. Prelude.sin
         * @param invocationClassProvider provider for the class from which to invoke a non-static method. The provider cannot be null, and the provided class cannot be null. 
         * @param javaProxyProvider provider for the accessible object corresponding to the foreign function e.g. "java.lang.Math.sqrt", which must be
         *      a Java method.
         * @param declaredReturnTypeProvider provider for the class corresponding to the declared return type of the foreign function, which cannot be null.
         * @return an Invocation instance.
         */
        static Invocation makeNonStaticMethod(final QualifiedName calName, final ForeignEntityProvider<Class<?>> invocationClassProvider, final ForeignEntityProvider<Method> javaProxyProvider, final ForeignEntityProvider<Class<?>> declaredReturnTypeProvider) {
            return new Invocation(calName, invocationClassProvider, javaProxyProvider, declaredReturnTypeProvider, JavaKind.METHOD);
        }

        /**
         * Factory method for a static field access.
         * @param calName CAL name of the foreign function e.g. Prelude.sin
         * @param invocationClassProvider provider for the class from which to invoke a static field. The provider cannot be null, and the provided class cannot be null. 
         * @param javaProxyProvider provider for the accessible object corresponding to the foreign function e.g. "java.lang.Math.sqrt", which must be
         *      a Java field.
         * @param declaredReturnTypeProvider provider for the class corresponding to the declared return type of the foreign function, which cannot be null.
         * @return an Invocation instance.
         */
        static Invocation makeStaticField(final QualifiedName calName, final ForeignEntityProvider<Class<?>> invocationClassProvider, final ForeignEntityProvider<Field> javaProxyProvider, final ForeignEntityProvider<Class<?>> declaredReturnTypeProvider) {
            return new Invocation(calName, invocationClassProvider, javaProxyProvider, declaredReturnTypeProvider, JavaKind.STATIC_FIELD);
        }

        /**
         * Factory method for a non-static field access.
         * @param calName CAL name of the foreign function e.g. Prelude.sin
         * @param invocationClassProvider provider for the class from which to invoke a non-static field. The provider cannot be null, and the provided class cannot be null. 
         * @param javaProxyProvider provider for the accessible object corresponding to the foreign function e.g. "java.lang.Math.sqrt", which must be
         *      a Java field.
         * @param declaredReturnTypeProvider provider for the class corresponding to the declared return type of the foreign function, which cannot be null.
         * @return an Invocation instance.
         */
        static Invocation makeNonStaticField(final QualifiedName calName, final ForeignEntityProvider<Class<?>> invocationClassProvider, final ForeignEntityProvider<Field> javaProxyProvider, final ForeignEntityProvider<Class<?>> declaredReturnTypeProvider) {
            return new Invocation(calName, invocationClassProvider, javaProxyProvider, declaredReturnTypeProvider, JavaKind.FIELD);
        }

        /**
         * Factory method for a constructor invocation.
         * @param calName CAL name of the foreign function e.g. Prelude.sin
         * @param javaProxyProvider provider for the accessible object corresponding to the foreign function e.g. "java.lang.Math.sqrt", which must be
         *      a Java constructor.
         * @param declaredReturnTypeProvider provider for the class corresponding to the declared return type of the foreign function, which cannot be null.
         * @return an Invocation instance.
         */
        static Invocation makeConstructor(final QualifiedName calName, final ForeignEntityProvider<Constructor<?>> javaProxyProvider, final ForeignEntityProvider<Class<?>> declaredReturnTypeProvider) {
            return new Invocation(calName, null, javaProxyProvider, declaredReturnTypeProvider, JavaKind.CONSTRUCTOR);
        }
        
        /** 
         * The class from which to invoke a method or field (both static and non-static) which cannot be null,
         * unless this is a constructor invocation.
         *  
         * It is sometimes necessary to invoke a method/field from a class other than which it was defined.
         * For example, if package scope class A defines a static public field f, and public class B extends A, 
         * then B.f in a different package will not result in a compilation error but A.f will.
         * 
         * Or for example, if package scope class A defines a non-static public method m, and public class B extends A, 
         * then in a different package we cannot invoke m on an object of type B if:
         * - the invocation is done via reflection, or
         * - the reference is first cast to the method's declared type, in this case A, i.e. ((A)b).m()
         *     
         * @return the class from which to invoke a method or field (both static and non-static) which cannot be null,
         *         unless this is a constructor invocation. 
         * @throws UnableToResolveForeignEntityException 
         */       
        public Class<?> getInvocationClass() throws UnableToResolveForeignEntityException {
            if (invocationClassProvider == null) {
                return null;
            } else {
                return invocationClassProvider.get();
            }
        }    

        /**
         * Creation date: (June 28, 2002)
         * @return AccessibleObject the field, method or constructor that this foreign function corresponds to.
         * @throws UnableToResolveForeignEntityException 
         */       
        public AccessibleObject getJavaProxy() throws UnableToResolveForeignEntityException {        

            return javaProxyProvider.get();
        } 

        /**
         * {@inheritDoc}
         */       
        @Override
        public synchronized int getNArguments() throws UnableToResolveForeignEntityException {
            
            // We want to avoid recalculating this every time, since getParameterTypes() involves copying a Class array every time.
            // Thus we cache the value on first access.

            // todo-jowong can synchronization be removed if nArguments is made a *volatile* field? 
            if (nArguments == -1) {
                
                final int nArgs;

                final JavaKind javaKind = getJavaKind();

                if (javaKind.isMethod()) {

                    final int nParams = ((Method)getJavaProxy()).getParameterTypes().length;

                    if (javaKind.isStatic()) {                                              
                        nArgs = nParams;
                    } else {
                        nArgs = nParams + 1; // not static, so the invocation target itself is the first arg, and the other params come after  
                    }

                } else if (javaKind.isField()) {

                    if (javaKind.isStatic()) {                            
                        nArgs = 0;                    
                    } else {                                                
                        nArgs = 1; // not static, so the invocation target itself is an argument
                    }

                } else if (javaKind.isConstructor()) {

                    nArgs = ((Constructor<?>)getJavaProxy()).getParameterTypes().length;     

                } else {
                    throw new IllegalStateException();
                }

                // set the calculated value into the cache field
                nArguments = nArgs;
            }

            return nArguments;
        }
        
        /** {@inheritDoc} */
        @Override
        public Class<?> getJavaArgumentType(final int argN) throws UnableToResolveForeignEntityException { 
            if (getJavaProxy() instanceof Method) {  
                                
                return ((Method)getJavaProxy()).getParameterTypes()[argN];  
                
            } else if (getJavaProxy() instanceof Field) {  
                
                throw new IndexOutOfBoundsException();
                
            } else {    
                
                return ((Constructor<?>)getJavaProxy()).getParameterTypes()[argN];
            } 
        }             
        
        /** {@inheritDoc} */
        @Override
        public Class<?> getJavaReturnType() throws UnableToResolveForeignEntityException {
            if (getJavaProxy() instanceof Method) {  
                
                return ((Method)getJavaProxy()).getReturnType();  
                
            } else if (getJavaProxy() instanceof Field) {  
                
                return ((Field)getJavaProxy()).getType();  
                
            } else {    
                
                return ((Constructor<?>)getJavaProxy()).getDeclaringClass();
            }                           
        }                

        /** {@inheritDoc} */
        @Override
        public void resolveForeignEntities() throws UnableToResolveForeignEntityException {
            // Force resolution by calling get() on each of the ForeignEntityProviders
            javaProxyProvider.get();
            if (invocationClassProvider != null) {
                invocationClassProvider.get();
            }
            declaredReturnTypeProvider.get();
        }
        
        /**
         * {@inheritDoc}
         */       
        @Override
        public String toString() {

            final StringBuilder result = new StringBuilder ("foreign ").append(super.calName);

            result.append(" [kind = ").append(super.javaKind).append("]");   
            
            result.append(" [java = ").append(javaProxyProvider).append("]");     
            
            return result.toString();       
        } 
        
        /**
         * {@inheritDoc}
         */
        @Override
        final void write (final RecordOutputStream s) throws IOException, UnableToResolveForeignEntityException {
            s.startRecord(ModuleSerializationTags.FOREIGN_FUNCTION_INFO_INVOCATION, serializationSchema);   
            
            super.write(s);

            if (getJavaKind().isMethod()) {
                final Method m = (Method)getJavaProxy();  
                final Class<?> declaringClassOrInvocationClass = getInvocationClass() != null ? getInvocationClass() : m.getDeclaringClass();            
                s.writeUTF(declaringClassOrInvocationClass.getName());           
                s.writeUTF(m.getName());
                final Class<?>[] pTypes = m.getParameterTypes();
                s.writeShortCompressed(pTypes.length);
                for (int i = 0; i < pTypes.length; ++i) {
                    s.writeUTF(pTypes[i].getName());
                }
                s.writeUTF(declaredReturnTypeProvider.get().getName());

            } else if (getJavaKind().isField()) {
                final Field f = (Field)getJavaProxy();
                final Class<?> declaringClassOrInvocationClass = getInvocationClass() != null ? getInvocationClass() : f.getDeclaringClass();            
                s.writeUTF(declaringClassOrInvocationClass.getName());                        
                s.writeUTF(f.getName());
                s.writeUTF(f.getType().getName());
                s.writeUTF(declaredReturnTypeProvider.get().getName());

            } else if (getJavaKind().isConstructor()) {
                final Constructor<?> c = (Constructor<?>)getJavaProxy();
                s.writeUTF(c.getDeclaringClass().getName());          
                final Class<?>[] pTypes = c.getParameterTypes();
                s.writeShortCompressed(pTypes.length);
                for (int i = 0; i < pTypes.length; ++i) {
                    s.writeUTF(pTypes[i].getName());
                }
                s.writeUTF(declaredReturnTypeProvider.get().getName());
            } else {
                throw new IOException ("Unknown or invalid foreign function kind encountered saving " + getCalName().getQualifiedName());
            }

            s.endRecord();
        }
        
        /**
         * Load an instance of ForeignFunctionInfo from the RecordInputStream.
         * Read position will be before the record header.
         * @param s
         * @param moduleName the name of the module being loaded
         * @param foreignClassLoader the classloader to use to resolve foreign classes.
         * @param msgLogger the logger to which to log deserialization messages.
         * @return an instance of ForeignFunctionInfo, or null if there was a problem resolving classes.
         * @throws IOException
         */
        private static final ForeignFunctionInfo loadInvocation (final RecordInputStream s, final int schema, final ModuleName moduleName, final ClassLoader foreignClassLoader, final CompilerMessageLogger msgLogger) throws IOException {            
            DeserializationHelper.checkSerializationSchema(schema, serializationSchema, moduleName, "ForeignFunctionInfo.Invocation", msgLogger);
            
            //read the superclass fields
            final QualifiedName calName = s.readQualifiedName();
            final JavaKind javaKind = JavaKind.load(s, moduleName, msgLogger);        

            ForeignFunctionInfo foreignFunctionInfo;

            if (javaKind.isMethod()) {
                foreignFunctionInfo = loadMethod(s, calName, javaKind.isStatic(), foreignClassLoader, msgLogger);

            } else if (javaKind.isField()) {
                foreignFunctionInfo = loadField(s, calName, javaKind.isStatic(), foreignClassLoader, msgLogger);

            } else if (javaKind.isConstructor()) {
                foreignFunctionInfo = loadConstructor(s, calName, foreignClassLoader, msgLogger);
          
            } else {
                throw new IOException ("Unknown foreign function kind encountered loading ForeignFunctionInfo " + calName);
            }

            s.skipRestOfRecord();

            return foreignFunctionInfo;
        }        
        
        /**    
         * Read position will be before the containing class name.
         * @param s
         * @param calName the name of the cal function being loaded - for use in error strings
         * @param isStatic true if the Java method is static.
         * @param foreignClassLoader the classloader to use to resolve foreign classes.
         * @param msgLogger the logger to which to log deserialization messages.
         * @return ForeignFunctionInfo
         * @throws IOException
         */
        private static final ForeignFunctionInfo loadMethod(final RecordInputStream s, final QualifiedName calName, final boolean isStatic, final ClassLoader foreignClassLoader, final CompilerMessageLogger msgLogger) throws IOException {
            final String declaringClassOrInvocationClassName = s.readUTF();
            final ForeignEntityProvider<Class<?>> declaringClassOrInvocationClassProvider = DeserializationHelper.classProviderForName(declaringClassOrInvocationClassName, foreignClassLoader, "ForeignFunctionInfo " + calName, CompilerMessage.Identifier.makeFunction(calName), msgLogger);
            if (declaringClassOrInvocationClassProvider == null) {
                return null;
            }

            final String funcName = s.readUTF();

            final int nJArgs = s.readShortCompressed();
            final ForeignEntityProvider<Class<?>> argClassProviders[] = ForeignFunctionChecker.makeForeignEntityProviderClassArray(nJArgs);
            for (int i = 0; i < nJArgs; ++i) {
                final String argClassName = s.readUTF();
                final ForeignEntityProvider<Class<?>> argClassProvider = DeserializationHelper.classProviderForName(argClassName, foreignClassLoader, "ForeignFunctionInfo " + calName, CompilerMessage.Identifier.makeFunction(calName), msgLogger);
                if (argClassProvider == null) {
                    return null;
                }
                argClassProviders[i] = argClassProvider;
            }
            
            final String declaredReturnTypeName = s.readUTF();
            final ForeignEntityProvider<Class<?>> declaredReturnTypeProvider = DeserializationHelper.classProviderForName(declaredReturnTypeName, foreignClassLoader, "ForeignFunctionInfo " + calName, CompilerMessage.Identifier.makeFunction(calName), msgLogger);
            if (declaredReturnTypeProvider == null) {
                return null;
            }

            final SourceRange sourceRange = new SourceRange(calName.getModuleName().toSourceText());
            final String javaName = declaringClassOrInvocationClassName + "." + funcName;

            return ForeignFunctionChecker.makeForeignFunctionInfoForMethod(calName, javaName, isStatic, sourceRange, funcName, declaringClassOrInvocationClassName, declaringClassOrInvocationClassProvider, argClassProviders, declaredReturnTypeProvider, msgLogger, true);
        }

        /**    
         * Read position will be before the declaring (non-static field) or invocation (static field) class name.
         * @param s
         * @param calName the name of the cal function being loaded - for use in error strings
         * @param isStatic true if the Java method is static.
         * @param foreignClassLoader the classloader to use to resolve foreign classes.
         * @param msgLogger the logger to which to log deserialization messages.
         * @return a field instance, or null if there was a problem resolving classes.
         * @throws IOException
         */
        private static final ForeignFunctionInfo loadField(final RecordInputStream s, final QualifiedName calName, final boolean isStatic, final ClassLoader foreignClassLoader, final CompilerMessageLogger msgLogger) throws IOException {
            final String declaringClassOrInvocationClassName = s.readUTF();
            final ForeignEntityProvider<Class<?>> declaringClassOrInvocationClassProvider = DeserializationHelper.classProviderForName(declaringClassOrInvocationClassName, foreignClassLoader, "ForeignFunctionInfo " + calName, CompilerMessage.Identifier.makeFunction(calName), msgLogger);
            if (declaringClassOrInvocationClassProvider == null) {
                return null;
            }

            final String fieldName = s.readUTF();
            final String fieldClassName = s.readUTF();
            final ForeignEntityProvider<Class<?>> fieldClassProvider = DeserializationHelper.classProviderForName(fieldClassName, foreignClassLoader, "ForeignFunctionInfo " + calName, CompilerMessage.Identifier.makeFunction(calName), msgLogger);
            if (fieldClassProvider == null) {
                return null;
            }

            final String declaredReturnTypeName = s.readUTF();
            final ForeignEntityProvider<Class<?>> declaredReturnTypeProvider = DeserializationHelper.classProviderForName(declaredReturnTypeName, foreignClassLoader, "ForeignFunctionInfo " + calName, CompilerMessage.Identifier.makeFunction(calName), msgLogger);
            if (declaredReturnTypeProvider == null) {
                return null;
            }

            final SourceRange sourceRange = new SourceRange(calName.getModuleName().toSourceText());
            final String javaName = declaringClassOrInvocationClassName + "." + fieldName;
            
            return ForeignFunctionChecker.makeForeignFunctionInfoForField(calName, javaName, isStatic, sourceRange, fieldName, fieldClassName, declaringClassOrInvocationClassProvider, declaredReturnTypeProvider, msgLogger, true);
        }

        /**   
         * Read position will be before the containing class name.
         * @param s
         * @param calName the name of the cal function being loaded - for use in error strings
         * @param foreignClassLoader the classloader to use to resolve foreign classes.
         * @param msgLogger the logger to which to log deserialization messages.
         * @return a constructor instance, or null if there was a problem resolving classes.
         * @throws IOException
         */
        private static final ForeignFunctionInfo loadConstructor(final RecordInputStream s, final QualifiedName calName, final ClassLoader foreignClassLoader, final CompilerMessageLogger msgLogger) throws IOException {
            final String declaringClassName = s.readUTF();
            final ForeignEntityProvider<Class<?>> declaringClassProvider = DeserializationHelper.classProviderForName(declaringClassName, foreignClassLoader, "ForeignFunctionInfo " + calName, CompilerMessage.Identifier.makeFunction(calName), msgLogger);
            if (declaringClassProvider == null) {
                return null;
            }              

            final int nJArgs = s.readShortCompressed();
            final ForeignEntityProvider<Class<?>> argClassProviders[] = ForeignFunctionChecker.makeForeignEntityProviderClassArray(nJArgs);
            for (int i = 0; i < nJArgs; ++i) {
                final String argClassName = s.readUTF();
                final ForeignEntityProvider<Class<?>> argClassProvider = DeserializationHelper.classProviderForName(argClassName, foreignClassLoader, "ForeignFunctionInfo " + calName, CompilerMessage.Identifier.makeFunction(calName), msgLogger);
                if (argClassProvider == null) {
                    return null;
                }
                argClassProviders[i] = argClassProvider;
            }

            final String declaredReturnTypeName = s.readUTF();
            final ForeignEntityProvider<Class<?>> declaredReturnTypeProvider = DeserializationHelper.classProviderForName(declaredReturnTypeName, foreignClassLoader, "ForeignFunctionInfo " + calName, CompilerMessage.Identifier.makeFunction(calName), msgLogger);
            if (declaredReturnTypeProvider == null) {
                return null;
            }

            final SourceRange sourceRange = new SourceRange(calName.getModuleName().toSourceText());
            final String javaName = declaringClassName;
            
            return ForeignFunctionChecker.makeForeignFunctionInfoForConstructor(calName, javaName, sourceRange, declaringClassProvider, argClassProviders, declaredReturnTypeProvider, msgLogger, true);
        }
    }
    
    /**
     * Information about a CAL foreign function that is in fact a Java cast. 
     * 
     * @author Bo Ilic
     */
    public static final class Cast extends ForeignFunctionInfo {
        
        private static final int serializationSchema = 0;
               
        /** Provider for the Java type of the expression being cast, which is always non-null. */
        private final ForeignEntityProvider<Class<?>> argumentTypeProvider;

        /** Provider for the Java type of the result of the cast, which is always non-null. */
        private final ForeignEntityProvider<Class<?>> resultTypeProvider;
        
        /**
         * Constructor for a foreign cast.    
         */
        Cast(final QualifiedName calName, final JavaKind kind, final ForeignEntityProvider<Class<?>> castArgumentTypeProvider, final ForeignEntityProvider<Class<?>> castResultTypeProvider) {
            super(calName, kind);
            
            if (!kind.isCast()) {
                throw new IllegalArgumentException();
            }
            
            if (castArgumentTypeProvider == null || castResultTypeProvider == null) {
                throw new NullPointerException();
            }
            
            this.argumentTypeProvider = castArgumentTypeProvider;
            this.resultTypeProvider = castResultTypeProvider;
        }               
              
        /**
         * {@inheritDoc}
         */       
        @Override
        public int getNArguments() {
            return 1;
        }
        
        /** {@inheritDoc} */
        @Override
        public Class<?> getJavaArgumentType(final int argN) throws UnableToResolveForeignEntityException {
            if (argN == 0) {
                return argumentTypeProvider.get();
            }
            
            throw new IndexOutOfBoundsException();
        }           
        
        /** {@inheritDoc} */
        @Override
        public Class<?> getJavaReturnType() throws UnableToResolveForeignEntityException {            
            return resultTypeProvider.get();
        }             

        /** {@inheritDoc} */
        @Override
        public void resolveForeignEntities() throws UnableToResolveForeignEntityException {
            // Force resolution by calling get() on each of the ForeignEntityProviders
            argumentTypeProvider.get();
            resultTypeProvider.get();
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {

            final StringBuilder result = new StringBuilder ("foreign ").append(super.calName);
            result.append(" [kind = ").append(super.javaKind).append("]");           
            result.append(" [cast = ").append(argumentTypeProvider).append(" to ").append(resultTypeProvider).append("]");
           
            return result.toString();       
        } 
        
        /**
         * {@inheritDoc}
         */
        @Override
        final void write (final RecordOutputStream s) throws IOException, UnableToResolveForeignEntityException {
            s.startRecord(ModuleSerializationTags.FOREIGN_FUNCTION_INFO_CAST, serializationSchema);

            super.write(s); 
            
            s.writeUTF(argumentTypeProvider.get().getName());
            s.writeUTF(resultTypeProvider.get().getName());
           
            s.endRecord();
        }   
        
        /**
         * Load an instance of ForeignFunctionInfo.Cast from the RecordInputStream.
         * Read position will be before the record header.
         * @param s
         * @param moduleName the name of the module being loaded
         * @param foreignClassLoader the classloader to use to resolve foreign classes.
         * @param msgLogger the logger to which to log deserialization messages.
         * @return an instance of ForeignFunctionInfo, or null if there was a problem resolving classes.
         * @throws IOException
         */
        private static final ForeignFunctionInfo load (final RecordInputStream s, final int schema, final ModuleName moduleName, final ClassLoader foreignClassLoader, final CompilerMessageLogger msgLogger) throws IOException {            
            DeserializationHelper.checkSerializationSchema(schema, serializationSchema, moduleName, "ForeignFunctionInfo.Cast", msgLogger);
            
            //read the superclass fields
            final QualifiedName calName = s.readQualifiedName();
            final JavaKind javaKind = JavaKind.load(s, moduleName, msgLogger);  
            
            final String castArgumentTypeName = s.readUTF();
            final String castResultTypeName = s.readUTF();
            
            final ClassPairResolver castArgumentAndResultTypeResolver = new ClassPairResolver(castArgumentTypeName, castResultTypeName) {
                @Override
                Pair<Class<?>, Class<?>> resolveClassPair(final ForeignEntityProvider.MessageHandler msgLogger) throws UnableToResolveForeignEntityException {
                    
                    final Class<?> castArgumentType = DeserializationHelper.classForName(castArgumentTypeName, foreignClassLoader, "ForeignFunctionInfo.Cast " + calName, CompilerMessage.Identifier.makeFunction(calName), msgLogger);
                    final Class<?> castResultType = DeserializationHelper.classForName(castResultTypeName, foreignClassLoader, "ForeignFunctionInfo.Cast " + calName, CompilerMessage.Identifier.makeFunction(calName), msgLogger);
                    
                    //the actualJavaKind could have changed from the serialized javaKind because the underlying Java types may have
                    //changed since the CAL cmi file was saved. For example, a Java class could have been declared final, which could make
                    //a previously valid Java cast into a static compile-time error in Java, and thus a static compile-time error in CAL.
                    //Alternatively, changes in the Java class hierarchy can cause a widening reference cast to become a narrowing reference
                    //cast or vice versa.
                    //          
                    //Note that there will never be a change of primitive cast types, since to change a primitive cast type, one must
                    //change the foreign declaration whose implementation type is a primitive Java type, which would automatically
                    //invalidate the compiled CAL file.           
                    final JavaKind actualJavaKind = ForeignFunctionChecker.checkForeignCast(castArgumentType, castResultType);
                    if (javaKind != actualJavaKind) {
                        //"The underlying Java types involved in the CAL foreign function {0} have changed. Recompilation is needed."
                        msgLogger.handleMessage(new CompilerMessage(
                            new SourceRange(moduleName.toSourceText()),
                            CompilerMessage.Identifier.makeFunction(calName),
                            new MessageKind.Error.InvalidJavaTypeChangeOnLoading(calName)));
                    }
                    
                    return new Pair<Class<?>, Class<?>>(castArgumentType, castResultType);
                }
            };
            
            final ForeignEntityProvider<Class<?>> castArgumentTypeProvider = ForeignEntityProvider.make(msgLogger, castArgumentAndResultTypeResolver.getFirstResolver());
            if (castArgumentTypeProvider == null) {
                return null;
            } 
            
            final ForeignEntityProvider<Class<?>> castResultTypeProvider = ForeignEntityProvider.make(msgLogger, castArgumentAndResultTypeResolver.getSecondResolver());
            if (castResultTypeProvider == null) {
                return null;
            }  
           
            s.skipRestOfRecord();

            return new Cast(calName, javaKind, castArgumentTypeProvider, castResultTypeProvider);
        }     
    
    }  
    
    /**
     * Information about a CAL foreign function that is in fact a Java instanceof operator.
     * 
     * These are CAL functions with type: SomeForeignJavaReferenceType -> Boolean;
     * 
     * @author Bo Ilic
     */
    public static final class InstanceOf extends ForeignFunctionInfo {
        
        private static final int serializationSchema = 0;
                
        /** Provider for the Java type of expr in "expr instanceof T", which will not be null. */
        private final ForeignEntityProvider<Class<?>> argumentTypeProvider;

        /** Provider for the Java type T in the expression "expr instanceof T", which will not be null. */
        private final ForeignEntityProvider<Class<?>> instanceOfTypeProvider;
        
        /**
         * Constructor for a foreign instanceof.    
         */
        InstanceOf(final QualifiedName calName, final ForeignEntityProvider<Class<?>> argumentTypeProvider, final ForeignEntityProvider<Class<?>> instanceOfTypeProvider) {
            super(calName, JavaKind.INSTANCE_OF);
                                 
            if (argumentTypeProvider == null || instanceOfTypeProvider == null) {
                throw new NullPointerException();
            }
            
            this.argumentTypeProvider = argumentTypeProvider;
            this.instanceOfTypeProvider = instanceOfTypeProvider;
        }               
        
        /**     
         * @return the Java type T in the expression "expr instanceof T". Will not be null.
         */
        public Class<?> getInstanceOfType() throws UnableToResolveForeignEntityException {
            return instanceOfTypeProvider.get();
        }        
              
        /**
         * {@inheritDoc}
         */       
        @Override
        public int getNArguments() {
            return 1;
        }
        
        /** {@inheritDoc} */
        @Override
        public Class<?> getJavaArgumentType(final int argN) throws UnableToResolveForeignEntityException {
            if (argN == 0) {
                return argumentTypeProvider.get();
            }
            
            throw new IndexOutOfBoundsException();
        }        
        
        /** {@inheritDoc} */
        @Override
        public Class<?> getJavaReturnType() {            
            return boolean.class;
        }             

        /** {@inheritDoc} */
        @Override
        public void resolveForeignEntities() throws UnableToResolveForeignEntityException {
            // Force resolution by calling get() on each of the ForeignEntityProviders
            argumentTypeProvider.get();
            instanceOfTypeProvider.get();
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {

            final StringBuilder result = new StringBuilder ("foreign ").append(super.calName);
            result.append(" [kind = ").append(super.javaKind).append("]");           
            result.append(" [").append(argumentTypeProvider).append(" instanceof ").append(instanceOfTypeProvider).append("]");
           
            return result.toString();       
        } 
        
        /**
         * {@inheritDoc}
         */
        @Override
        final void write (final RecordOutputStream s) throws IOException, UnableToResolveForeignEntityException {
            s.startRecord(ModuleSerializationTags.FOREIGN_FUNCTION_INFO_INSTANCE_OF, serializationSchema);

            super.write(s); 
            
            s.writeUTF(argumentTypeProvider.get().getName());
            s.writeUTF(instanceOfTypeProvider.get().getName());
           
            s.endRecord();
        }   
        
        /**
         * Load an instance of ForeignFunctionInfo.InstanceOf from the RecordInputStream.
         * Read position will be before the record header.
         * @param s
         * @param moduleName the name of the module being loaded
         * @param foreignClassLoader the classloader to use to resolve foreign classes.
         * @param msgLogger the logger to which to log deserialization messages.
         * @return an instance of ForeignFunctionInfo, or null if there was a problem resolving classes.
         * @throws IOException
         */
        private static final ForeignFunctionInfo load (final RecordInputStream s, final int schema, final ModuleName moduleName, final ClassLoader foreignClassLoader, final CompilerMessageLogger msgLogger) throws IOException {            
            DeserializationHelper.checkSerializationSchema(schema, serializationSchema, moduleName, "ForeignFunctionInfo.InstanceOf", msgLogger);
            
            //read the superclass fields
            final QualifiedName calName = s.readQualifiedName();
            //todoBI we currently only have one instanceof JavaKind. However, in the future we may have more if we want to optimize out
            //instanceof no-ops e.g. expr instanceof Object. Typically there is no reason to write such code in CAL so we don't bother
            //optimizing this out.
            final JavaKind javaKind = JavaKind.load(s, moduleName, msgLogger);
            if (!javaKind.isInstanceOf()) {
                throw new IOException();
            }
           
            final String argumentTypeName = s.readUTF();
            final String instanceOfTypeName = s.readUTF();
            
            final ClassPairResolver argumentAndInstanceOfTypeResolver = new ClassPairResolver(argumentTypeName, instanceOfTypeName) {
                @Override
                Pair<Class<?>, Class<?>> resolveClassPair(final ForeignEntityProvider.MessageHandler messageHandler) throws UnableToResolveForeignEntityException {
                    final Class<?> argumentType = DeserializationHelper.classForName(argumentTypeName, foreignClassLoader, "ForeignFunctionInfo.InstanceOf " + calName, CompilerMessage.Identifier.makeFunction(calName), messageHandler);
                    final Class<?> instanceOfType = DeserializationHelper.classForName(instanceOfTypeName, foreignClassLoader, "ForeignFunctionInfo.InstanceOf " + calName, CompilerMessage.Identifier.makeFunction(calName), messageHandler);
                    
                    //It could happen that upon loading, an instanceof check is no longer valid since it would result in a compile-time
                    //error in Java because it no longer corresponds to a possibly valid Java cast. 
                    final JavaKind javaCastKind = ForeignFunctionChecker.checkForeignCast(argumentType, instanceOfType);
                    if (javaCastKind == null) {
                        //"The underlying Java types involved in the CAL foreign function {0} have changed. Recompilation is needed."
                        messageHandler.handleMessage(new CompilerMessage(
                            new SourceRange(moduleName.toSourceText()),
                            CompilerMessage.Identifier.makeFunction(calName),
                            new MessageKind.Error.InvalidJavaTypeChangeOnLoading(calName)));
                    }
                    
                    return new Pair<Class<?>, Class<?>>(argumentType, instanceOfType);
                }
            };
            
            final ForeignEntityProvider<Class<?>> argumentTypeProvider = ForeignEntityProvider.make(msgLogger, argumentAndInstanceOfTypeResolver.getFirstResolver());
            if (argumentTypeProvider == null) {
                return null;
            } 
            
            final ForeignEntityProvider<Class<?>> instanceOfTypeProvider = ForeignEntityProvider.make(msgLogger, argumentAndInstanceOfTypeResolver.getSecondResolver());
            if (instanceOfTypeProvider == null) {
                return null;
            }  
           
            s.skipRestOfRecord();

            return new InstanceOf(calName, argumentTypeProvider, instanceOfTypeProvider);
        }        
    
    } 
      
    /**
     * Information about a CAL foreign function that is in fact a Java null literal (of
     * a specific CAL type)
     * <p>
     * These are CAL functions of type T (i.e. 0 arguments), where T is a foreign CAL type whose implementation type
     * is a Java reference type.
     * 
     * @author Bo Ilic
     */
    public static final class NullLiteral extends ForeignFunctionInfo {
        
        private static final int serializationSchema = 0;
                      
        /** Provider for the Java reference type T of the Java null literal */
        private final ForeignEntityProvider<Class<?>> resultTypeProvider;
        
        /**
         * Constructor for a foreign null.    
         */
        NullLiteral(final QualifiedName calName, final ForeignEntityProvider<Class<?>> resultTypeProvider) {
            super(calName, JavaKind.NULL_LITERAL);
                                 
            if (resultTypeProvider == null) {
                throw new NullPointerException();
            }
            
            this.resultTypeProvider = resultTypeProvider;
        }                   
              
        /**
         * {@inheritDoc}
         */       
        @Override
        public int getNArguments() {
            return 0;
        }
        
        /** {@inheritDoc} */
        @Override
        public Class<?> getJavaArgumentType(final int argN) {           
            throw new IndexOutOfBoundsException();
        }             
                    
        /** {@inheritDoc} */
        @Override
        public Class<?> getJavaReturnType() throws UnableToResolveForeignEntityException {            
            return resultTypeProvider.get();
        }             

        /** {@inheritDoc} */
        @Override
        public void resolveForeignEntities() throws UnableToResolveForeignEntityException {
            // Force resolution by calling get() on each of the ForeignEntityProviders
            resultTypeProvider.get();
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {

            final StringBuilder result = new StringBuilder ("foreign ").append(super.calName);
            result.append(" [kind = ").append(super.javaKind).append("]");           
            result.append(" [null ").append(resultTypeProvider).append("]");
           
            return result.toString();       
        } 
        
        /**
         * {@inheritDoc}
         */
        @Override
        final void write (final RecordOutputStream s) throws IOException, UnableToResolveForeignEntityException {
            s.startRecord(ModuleSerializationTags.FOREIGN_FUNCTION_INFO_NULL_LITERAL, serializationSchema);

            super.write(s); 
            
            s.writeUTF(getJavaReturnType().getName());          
           
            s.endRecord();
        }   
        
        /**
         * Load an instance of ForeignFunctionInfo.NullLiteral from the RecordInputStream.
         * Read position will be before the record header.
         * @param s
         * @param moduleName the name of the module being loaded
         * @param foreignClassLoader the classloader to use to resolve foreign classes.
         * @param msgLogger the logger to which to log deserialization messages.
         * @return an instance of ForeignFunctionInfo, or null if there was a problem resolving classes.
         * @throws IOException
         */
        private static final ForeignFunctionInfo load (final RecordInputStream s, final int schema, final ModuleName moduleName, final ClassLoader foreignClassLoader, final CompilerMessageLogger msgLogger) throws IOException {            
            DeserializationHelper.checkSerializationSchema(schema, serializationSchema, moduleName, "ForeignFunctionInfo.NullLiteral", msgLogger);
            
            //read the superclass fields
            final QualifiedName calName = s.readQualifiedName();          
            final JavaKind javaKind = JavaKind.load(s, moduleName, msgLogger);
            if (javaKind != JavaKind.NULL_LITERAL) {
                throw new IOException();
            }
           
            final String resultTypeName = s.readUTF();
            final ForeignEntityProvider<Class<?>> resultTypeProvider = DeserializationHelper.classProviderForName(resultTypeName, foreignClassLoader, "ForeignFunctionInfo.NullLiteral " + calName, CompilerMessage.Identifier.makeFunction(calName), msgLogger);
            if (resultTypeProvider == null) {
                return null;
            } 
                                  
            s.skipRestOfRecord();

            return new NullLiteral(calName, resultTypeProvider);
        }        
    
    }
    
    /**
     * Information about a CAL foreign function that is in fact a Java Class literal (of
     * the Java type java.lang.Class), referring to a particular Java type R.
     * <p>
     * These are CAL functions of type T (i.e. 0 arguments), where T is a foreign CAL type whose implementation type
     * is java.lang.Class, its superclass java.lang.Object, or one of its superinterfaces.
     * 
     * @author Joseph Wong
     */
    public static final class ClassLiteral extends ForeignFunctionInfo {
        
        private static final int serializationSchema = 0;
                      
        /**
         * Provider for the referent type, i.e. the Java type R where this literal corresponds to R.class, which cannot be null.
         * It may be a primitive type, a reference type, or an array type.
         */
        private final ForeignEntityProvider<Class<?>> referentTypeProvider;
        
        /**
         * Provider for the class corresponding to the declared return type of the foreign function, which cannot be null.
         */
        private final ForeignEntityProvider<Class<?>> declaredReturnTypeProvider;
        
        /**
         * Constructor for a foreign class literal.
         * @param calName CAL name of the foreign function
         * @param referentTypeProvider provider for the referent type, i.e. the Java type R where this literal corresponds to R.class, which cannot be null.
         *          It may be a primitive type, a reference type, or an array type.
         * @param declaredReturnTypeProvider provider for the class corresponding to the declared return type of the foreign function, which cannot be null.
         */
        ClassLiteral(final QualifiedName calName, final ForeignEntityProvider<Class<?>> referentTypeProvider, final ForeignEntityProvider<Class<?>> declaredReturnTypeProvider) {
            super(calName, JavaKind.CLASS_LITERAL);
                                 
            if (referentTypeProvider == null || declaredReturnTypeProvider == null) {
                throw new NullPointerException();
            }
            
            this.referentTypeProvider = referentTypeProvider;
            this.declaredReturnTypeProvider = declaredReturnTypeProvider;
        }                   
              
        /**
         * {@inheritDoc}
         */       
        @Override
        public int getNArguments() {
            return 0;
        }
        
        /**
         * @return the referent type, i.e. the Java type R where this literal corresponds to R.class.
         */
        public Class<?> getReferentType() throws UnableToResolveForeignEntityException {
            return referentTypeProvider.get();
        }
        
        /** {@inheritDoc} */
        @Override
        public Class<?> getJavaArgumentType(final int argN) {           
            throw new IndexOutOfBoundsException();
        }             
                    
        /** {@inheritDoc} */
        @Override
        public Class<?> getJavaReturnType() {            
            return Class.class;
        }             

        /** {@inheritDoc} */
        @Override
        public void resolveForeignEntities() throws UnableToResolveForeignEntityException {
            // Force resolution by calling get() on each of the ForeignEntityProviders
            referentTypeProvider.get();
            declaredReturnTypeProvider.get();
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {

            final StringBuilder result = new StringBuilder ("foreign ").append(super.calName);
            result.append(" [kind = ").append(super.javaKind).append("]");           
            result.append(" [class ").append(referentTypeProvider).append("]");
           
            return result.toString();       
        } 
        
        /**
         * {@inheritDoc}
         */
        @Override
        final void write (final RecordOutputStream s) throws IOException, UnableToResolveForeignEntityException {
            s.startRecord(ModuleSerializationTags.FOREIGN_FUNCTION_INFO_CLASS_LITERAL, serializationSchema);

            super.write(s); 
            
            s.writeUTF(getReferentType().getName());
            
            s.writeUTF(declaredReturnTypeProvider.get().getName());
           
            s.endRecord();
        }   
        
        /**
         * Load an instance of ForeignFunctionInfo.ClassLiteral from the RecordInputStream.
         * Read position will be before the record header.
         * @param s
         * @param moduleName the name of the module being loaded
         * @param foreignClassLoader the classloader to use to resolve foreign classes.
         * @param msgLogger the logger to which to log deserialization messages.
         * @return an instance of ForeignFunctionInfo, or null if there was a problem resolving classes.
         * @throws IOException
         */
        private static final ForeignFunctionInfo load (final RecordInputStream s, final int schema, final ModuleName moduleName, final ClassLoader foreignClassLoader, final CompilerMessageLogger msgLogger) throws IOException {            
            DeserializationHelper.checkSerializationSchema(schema, serializationSchema, moduleName, "ForeignFunctionInfo.NullLiteral", msgLogger);
            
            //read the superclass fields
            final QualifiedName calName = s.readQualifiedName();          
            final JavaKind javaKind = JavaKind.load(s, moduleName, msgLogger);
            if (javaKind != JavaKind.CLASS_LITERAL) {
                throw new IOException();
            }
           
            final String referentTypeName = s.readUTF();
            final ForeignEntityProvider<Class<?>> referentTypeProvider = DeserializationHelper.classProviderForName(referentTypeName, foreignClassLoader, "ForeignFunctionInfo.ClassLiteral " + calName, CompilerMessage.Identifier.makeFunction(calName), msgLogger);
            if (referentTypeProvider == null) {
                return null;
            } 
                                  
            final String declaredReturnTypeName = s.readUTF();
            final ForeignEntityProvider<Class<?>> declaredReturnTypeProvider = DeserializationHelper.classProviderForName(declaredReturnTypeName, foreignClassLoader, "ForeignFunctionInfo " + calName, CompilerMessage.Identifier.makeFunction(calName), msgLogger);
            if (declaredReturnTypeProvider == null) {
                return null;
            }

            s.skipRestOfRecord();
            
            final SourceRange sourceRange = new SourceRange(calName.getModuleName().toSourceText());

            return ForeignFunctionChecker.makeForeignFunctionInfoForClassLiteral(calName, referentTypeName, sourceRange, referentTypeProvider, declaredReturnTypeProvider, msgLogger, true);
        }        
    } 
    
    /**
     * Information about a CAL foreign function that is in fact a Java null check. That is either
     * "javaExpr == null" or "javaExpr != null" where javaExpr has a Java reference type.
     * <p>
     * 
     * As a CAL function, this has the CAL type T -> Prelude.Boolean
     * where T is a foreign type corresponding to a Java reference type.
     * Note that the return type is normally Prelude.Boolean but can also be any foreign type with Java
     * implementation type "boolean".
     * As usual, all types in the foreign function declaration must be visible, and their implementations must 
     * be visible, within the module in which the foreign function is defined.
     * 
     * @author Bo Ilic
     */
    public static final class NullCheck extends ForeignFunctionInfo {
        
        private static final int serializationSchema = 0;
                      
        /** Provider for the Java reference type T of the Java expression to be null checked, which cannot be null. */
        private final ForeignEntityProvider<Class<?>> argumentTypeProvider;
        
        /** true for the isNull check, false for the isNotNull check. */
        private final boolean checkIsNull;
                
        NullCheck(final QualifiedName calName, final ForeignEntityProvider<Class<?>> argumentTypeProvider, final boolean checkIsNull) {
            super(calName, JavaKind.NULL_CHECK);
                                 
            if (argumentTypeProvider == null) {
                throw new NullPointerException();
            }
            
            this.argumentTypeProvider = argumentTypeProvider;
            this.checkIsNull = checkIsNull;
        }                   
              
        /**
         * {@inheritDoc}
         */       
        @Override
        public int getNArguments() {
            return 1;
        }
              
        /** {@inheritDoc} */
        @Override
        public Class<?> getJavaArgumentType(final int argN) throws UnableToResolveForeignEntityException { 
            if (argN == 0) {
                return argumentTypeProvider.get();
            }
            
            throw new IndexOutOfBoundsException();
        }            
        
        /** {@inheritDoc} */
        @Override
        public Class<?> getJavaReturnType() {            
            return boolean.class;
        }

        /** {@inheritDoc} */
        @Override
        public void resolveForeignEntities() throws UnableToResolveForeignEntityException {
            // Force resolution by calling get() on each of the ForeignEntityProviders
            argumentTypeProvider.get();
        }
        
        /**        
         * @return true for the isNull check, false for the isNotNull check.
         */
        public boolean checkIsNull() {
            return checkIsNull;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {

            final StringBuilder result = new StringBuilder ("foreign ").append(super.calName);
            result.append(" [kind = ").append(super.javaKind).append("]");  
            if (checkIsNull) {
                result.append(" [isNull ");
            } else {
                result.append(" [isNotNull ");
            }
            result.append(argumentTypeProvider).append("]");
           
            return result.toString();       
        } 
        
        /**
         * {@inheritDoc}
         */
        @Override
        final void write (final RecordOutputStream s) throws IOException, UnableToResolveForeignEntityException {
            s.startRecord(ModuleSerializationTags.FOREIGN_FUNCTION_INFO_NULL_CHECK, serializationSchema);

            super.write(s); 
            
            s.writeUTF(argumentTypeProvider.get().getName());  
            s.writeBoolean(checkIsNull);
           
            s.endRecord();
        }   
        
        /**
         * Load an instance of ForeignFunctionInfo.NullCheck from the RecordInputStream.
         * Read position will be before the record header.
         * @param s
         * @param moduleName the name of the module being loaded
         * @param foreignClassLoader the classloader to use to resolve foreign classes.
         * @param msgLogger the logger to which to log deserialization messages.
         * @return an instance of ForeignFunctionInfo, or null if there was a problem resolving classes.
         * @throws IOException
         */
        private static final ForeignFunctionInfo load (final RecordInputStream s, final int schema, final ModuleName moduleName, final ClassLoader foreignClassLoader, final CompilerMessageLogger msgLogger) throws IOException {            
            DeserializationHelper.checkSerializationSchema(schema, serializationSchema, moduleName, "ForeignFunctionInfo.NullCheck", msgLogger);
            
            //read the superclass fields
            final QualifiedName calName = s.readQualifiedName();          
            final JavaKind javaKind = JavaKind.load(s, moduleName, msgLogger);
            if (javaKind != JavaKind.NULL_CHECK) {
                throw new IOException();
            }
           
            final String argumentTypeName = s.readUTF();
            final ForeignEntityProvider<Class<?>> argumentTypeProvider = DeserializationHelper.classProviderForName(argumentTypeName, foreignClassLoader, "ForeignFunctionInfo.NullCheck " + calName, CompilerMessage.Identifier.makeFunction(calName), msgLogger);
            if (argumentTypeProvider == null) {
                return null;
            }
            
            final boolean checkIsNull = s.readBoolean();
                                  
            s.skipRestOfRecord();

            return new NullCheck(calName, argumentTypeProvider, checkIsNull);
        }        
    
    }
    
    /**
     * Information about a CAL foreign function that is in fact a Java new array creation.
     * <p>
     * 
     * The Java expression:
     * new T[s1][s2]...[sm][]...[]
     * is considered as a CAL function of m arguments:
     * Int -> Int -> ... -> Int -> CT
     * where CT is a CAL type whose implementation type is an n-dimensional Java array type with element type T.
     * In particular, we require that m >= 1 and m <= n to satisfy the constraints on Java array creation.
     * <p>
     * 
     * Note that the component size arguments can be any foreign type with Java implementation type "int" and not just
     * Prelude.Int.
     * 
     * @author Bo Ilic
     */
    public static final class NewArray extends ForeignFunctionInfo {
        
        private static final int serializationSchema = 0;
        
        /** 
         * the number of supplied int arguments specifying dimension sizes. Must be >= 1 and <= the number of dimensions
         * in the array.
         */
        private final int nSizeArgs;
        
        /**
         * Provider for the Java type of the resulting array
         */
        private final ForeignEntityProvider<Class<?>> newArrayTypeProvider;              
                
        NewArray(final QualifiedName calName, final int nSizeArgs, final ForeignEntityProvider<Class<?>> newArrayTypeProvider) {
            super(calName, JavaKind.NEW_ARRAY);
                                 
            if (newArrayTypeProvider == null) {
                throw new NullPointerException();
            }
            
            this.nSizeArgs = nSizeArgs;
            this.newArrayTypeProvider = newArrayTypeProvider;           
        }                   
              
        /**
         * {@inheritDoc}
         */       
        @Override
        public int getNArguments() {
            return nSizeArgs;
        }              
        
        /** {@inheritDoc} */
        @Override
        public Class<?> getJavaArgumentType(final int argN) { 
            if (argN >= 0 && argN < nSizeArgs) {
                return int.class;
            }
            
            throw new IndexOutOfBoundsException();
        }             
        
        /** {@inheritDoc} */
        @Override
        public Class<?> getJavaReturnType() throws UnableToResolveForeignEntityException {            
            return newArrayTypeProvider.get();
        }

        /** {@inheritDoc} */
        @Override
        public void resolveForeignEntities() throws UnableToResolveForeignEntityException {
            // Force resolution by calling get() on each of the ForeignEntityProviders
            newArrayTypeProvider.get();
        }
        
        /**              
         * For example, if this newArray function returns an int[][][], and 2 sizing arguments are supplied, then this is int[].
         * @return Class. 
         */
        public Class<?> getComponentType() throws UnableToResolveForeignEntityException {
            return ForeignFunctionChecker.getSubscriptedArrayType(getJavaReturnType(), nSizeArgs);
        }
                
        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {

            final StringBuilder result = new StringBuilder ("foreign ").append(super.calName);
            result.append(" [kind = ").append(super.javaKind).append("]");  
            result.append(" [nSizeArgs = ").append(nSizeArgs).append(", type = ");
            result.append(newArrayTypeProvider).append("]");
           
            return result.toString();       
        } 
        
        /**
         * {@inheritDoc}
         */
        @Override
        final void write (final RecordOutputStream s) throws IOException, UnableToResolveForeignEntityException {
            s.startRecord(ModuleSerializationTags.FOREIGN_FUNCTION_INFO_NEW_ARRAY, serializationSchema);

            super.write(s); 
            
            s.writeInt(nSizeArgs);  
            s.writeUTF(getJavaReturnType().getName());  
           
            s.endRecord();
        }   
        
        /**
         * Load an instance of ForeignFunctionInfo.NewArray from the RecordInputStream.
         * Read position will be before the record header.
         * @param s
         * @param moduleName the name of the module being loaded
         * @param foreignClassLoader the classloader to use to resolve foreign classes.
         * @param msgLogger the logger to which to log deserialization messages.
         * @return an instance of ForeignFunctionInfo, or null if there was a problem resolving classes.
         * @throws IOException
         */
        private static final ForeignFunctionInfo load (final RecordInputStream s, final int schema, final ModuleName moduleName, final ClassLoader foreignClassLoader, final CompilerMessageLogger msgLogger) throws IOException {            
            DeserializationHelper.checkSerializationSchema(schema, serializationSchema, moduleName, "ForeignFunctionInfo.NewArray", msgLogger);
            
            //read the superclass fields
            final QualifiedName calName = s.readQualifiedName();          
            final JavaKind javaKind = JavaKind.load(s, moduleName, msgLogger);
            if (javaKind != JavaKind.NEW_ARRAY) {
                throw new IOException();
            }
            
            final int nSizeArgs = s.readInt();
           
            final String newArrayTypeName = s.readUTF();
            final ForeignEntityProvider<Class<?>> newArrayTypeProvider = DeserializationHelper.classProviderForName(newArrayTypeName, foreignClassLoader, "ForeignFunctionInfo.NewArray " + calName, CompilerMessage.Identifier.makeFunction(calName), msgLogger);
            if (newArrayTypeProvider == null) {
                return null;
            }
                                                        
            s.skipRestOfRecord();

            return new NewArray(calName, nSizeArgs, newArrayTypeProvider);
        }        
    
    } 
    
    
    /**
     * Information about a CAL foreign function that is in fact a call to the length
     * field of an array reference type.
     * <p>
     * 
     * As a CAL function it is of type:
     * CT -> Int
     * where CT is a CAL type whose implementation type is a Java array type. The return type is Prelude.Int (or
     * any other foreign type with Java implementation type "int").
     * 
     * @author Bo Ilic
     */
    public static final class LengthArray extends ForeignFunctionInfo {
        
        private static final int serializationSchema = 0;               
        
        private final ForeignEntityProvider<Class<?>> arrayTypeProvider;
                
        LengthArray(final QualifiedName calName, final ForeignEntityProvider<Class<?>> arrayTypeProvider) {
            super(calName, JavaKind.LENGTH_ARRAY);
                                 
            if (arrayTypeProvider == null) {
                throw new NullPointerException();
            }
                        
            this.arrayTypeProvider = arrayTypeProvider;
        }                   
              
        /**
         * {@inheritDoc}
         */       
        @Override
        public int getNArguments() {
            return 1;
        }              
        
        /** {@inheritDoc} */
        @Override
        public Class<?> getJavaArgumentType(final int argN) throws UnableToResolveForeignEntityException { 
            if (argN == 0) {
                return arrayTypeProvider.get();
            }
            
            throw new IndexOutOfBoundsException();
        }             
        
        /** {@inheritDoc} */
        @Override
        public Class<?> getJavaReturnType() {            
            return int.class;
        }

        /** {@inheritDoc} */
        @Override
        public void resolveForeignEntities() throws UnableToResolveForeignEntityException {
            // Force resolution by calling get() on each of the ForeignEntityProviders
            arrayTypeProvider.get();
        }
                
        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {

            final StringBuilder result = new StringBuilder ("foreign ").append(super.calName);
            result.append(" [kind = ").append(super.javaKind).append("]");  
            result.append(" [type = ");
            result.append(arrayTypeProvider).append("]");
           
            return result.toString();       
        } 
        
        /**
         * {@inheritDoc}
         */
        @Override
        final void write (final RecordOutputStream s) throws IOException, UnableToResolveForeignEntityException {
            s.startRecord(ModuleSerializationTags.FOREIGN_FUNCTION_INFO_LENGTH_ARRAY, serializationSchema);

            super.write(s); 
                      
            s.writeUTF(arrayTypeProvider.get().getName());  
           
            s.endRecord();
        }   
        
        /**
         * Load an instance of ForeignFunctionInfo.LengthArray from the RecordInputStream.
         * Read position will be before the record header.
         * @param s
         * @param moduleName the name of the module being loaded
         * @param foreignClassLoader the classloader to use to resolve foreign classes.
         * @param msgLogger the logger to which to log deserialization messages.
         * @return an instance of ForeignFunctionInfo, or null if there was a problem resolving classes.
         * @throws IOException
         */
        private static final ForeignFunctionInfo load (final RecordInputStream s, final int schema, final ModuleName moduleName, final ClassLoader foreignClassLoader, final CompilerMessageLogger msgLogger) throws IOException {            
            DeserializationHelper.checkSerializationSchema(schema, serializationSchema, moduleName, "ForeignFunctionInfo.NewArray", msgLogger);
            
            //read the superclass fields
            final QualifiedName calName = s.readQualifiedName();          
            final JavaKind javaKind = JavaKind.load(s, moduleName, msgLogger);
            if (javaKind != JavaKind.LENGTH_ARRAY) {
                throw new IOException();
            }
                     
            final String arrayTypeName = s.readUTF();
            final ForeignEntityProvider<Class<?>> arrayTypeProvider = DeserializationHelper.classProviderForName(arrayTypeName, foreignClassLoader, "ForeignFunctionInfo.NewArray " + calName, CompilerMessage.Identifier.makeFunction(calName), msgLogger);
            if (arrayTypeProvider == null) {
                return null;
            }
                                                        
            s.skipRestOfRecord();

            return new LengthArray(calName, arrayTypeProvider);
        }        
    
    } 
    
    /**
     * Information about a CAL foreign function that is in fact a Java array subscript operator.
     * <p>
     * 
     * The Java expression:
     * expr[index1][index2]...[index_m]
     * is considered as a CAL function of m + 1 arguments:
     * T1 -> Prelude.Int -> Prelude.Int -> ... -> Prelude.Int -> T2 
     * where T1 is a CAL type whose implementation type is an n-dimensional Java array type
     * and T2 is a CAL type whose implementation type is the Java type resulting in subscripting the array with m indices
     * (or one assignment compatible with this type).
     * In particular, we require that m >= 1 and m <= n to satisfy the constraints on subscripting.
     * <p>
     * 
     * Note that the subscript arguments can be any foreign type with Java implementation type "int" and not just
     * Prelude.Int.
     * 
     * @author Bo Ilic
     */
    public static final class SubscriptArray extends ForeignFunctionInfo {
        
        private static final int serializationSchema = 0;
        
        /** 
         * the number of supplied int arguments specifying dimension sizes. Must be >= 1 and <= the number of dimensions
         * in the array.
         */
        private final int nSubscriptArgs;
        
        /** Provider for the type of the array to be subscripted. */
        private final ForeignEntityProvider<Class<?>> arrayTypeProvider;    
        
        /** Provider for the type returned by the CAL function, which is assignment compatible with the subscripted type. */
        private final ForeignEntityProvider<Class<?>> resultTypeProvider;
                
        SubscriptArray(final QualifiedName calName, final int nSubscriptArgs, final ForeignEntityProvider<Class<?>> arrayTypeProvider, final ForeignEntityProvider<Class<?>> resultTypeProvider) {
            super(calName, JavaKind.SUBSCRIPT_ARRAY);
                                 
            if (arrayTypeProvider == null) {
                throw new NullPointerException();
            }
            
            this.nSubscriptArgs = nSubscriptArgs;
            this.arrayTypeProvider = arrayTypeProvider;
            this.resultTypeProvider = resultTypeProvider;
        }                   
              
        /**
         * {@inheritDoc}
         */       
        @Override
        public int getNArguments() {
            return nSubscriptArgs + 1;
        }              
        
        /** {@inheritDoc} */
        @Override
        public Class<?> getJavaArgumentType(final int argN) throws UnableToResolveForeignEntityException { 
            
            if (argN == 0) {
                
                return arrayTypeProvider.get();
                
            } else if (argN > 0 && argN <= nSubscriptArgs) {
                
                return int.class;
                
            }                      
            
            throw new IndexOutOfBoundsException();
        }             
        
        /** {@inheritDoc} */
        @Override
        public Class<?> getJavaReturnType() throws UnableToResolveForeignEntityException {
            
            return resultTypeProvider.get();
        }

        /** {@inheritDoc} */
        @Override
        public void resolveForeignEntities() throws UnableToResolveForeignEntityException {
            // Force resolution by calling get() on each of the ForeignEntityProviders
            arrayTypeProvider.get();
            resultTypeProvider.get();
        }
                
        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {

            final StringBuilder result = new StringBuilder ("foreign ").append(super.calName);
            result.append(" [kind = ").append(super.javaKind).append("]");  
            result.append(" [nSubscriptArgs = ").append(nSubscriptArgs).append(", arrayType = ");
            result.append(arrayTypeProvider).append(", resultType = ").append(resultTypeProvider).append("]");
           
            return result.toString();       
        } 
        
        /**
         * {@inheritDoc}
         */
        @Override
        final void write (final RecordOutputStream s) throws IOException, UnableToResolveForeignEntityException {
            s.startRecord(ModuleSerializationTags.FOREIGN_FUNCTION_INFO_SUBSCRIPT_ARRAY, serializationSchema);

            super.write(s); 
            
            s.writeInt(nSubscriptArgs);  
            s.writeUTF(arrayTypeProvider.get().getName());
            s.writeUTF(resultTypeProvider.get().getName());
           
            s.endRecord();
        }   
        
        /**
         * Load an instance of ForeignFunctionInfo.SubscriptArray from the RecordInputStream.
         * Read position will be before the record header.
         * @param s
         * @param moduleName the name of the module being loaded
         * @param foreignClassLoader the classloader to use to resolve foreign classes.
         * @param msgLogger the logger to which to log deserialization messages.
         * @return an instance of ForeignFunctionInfo, or null if there was a problem resolving classes.
         * @throws IOException
         */
        private static final ForeignFunctionInfo load (final RecordInputStream s, final int schema, final ModuleName moduleName, final ClassLoader foreignClassLoader, final CompilerMessageLogger msgLogger) throws IOException {            
            DeserializationHelper.checkSerializationSchema(schema, serializationSchema, moduleName, "ForeignFunctionInfo.SubscriptArray", msgLogger);
            
            //read the superclass fields
            final QualifiedName calName = s.readQualifiedName();          
            final JavaKind javaKind = JavaKind.load(s, moduleName, msgLogger);
            if (javaKind != JavaKind.SUBSCRIPT_ARRAY) {
                throw new IOException();
            }

            final int nSubscriptArgs = s.readInt();

            final String arrayTypeName = s.readUTF();
            final String resultTypeName = s.readUTF();
            
            final ClassPairResolver arrayAndResultTypeResolver = new ClassPairResolver(arrayTypeName, resultTypeName) {
                @Override
                Pair<Class<?>, Class<?>> resolveClassPair(final ForeignEntityProvider.MessageHandler messageHandler) throws UnableToResolveForeignEntityException {
                    
                    final Class<?> arrayType = DeserializationHelper.classForName(arrayTypeName, foreignClassLoader, "ForeignFunctionInfo.SubscriptArray " + calName, CompilerMessage.Identifier.makeFunction(calName), messageHandler);
                    final Class<?> resultType = DeserializationHelper.classForName(resultTypeName, foreignClassLoader, "ForeignFunctionInfo.SubscriptArray " + calName, CompilerMessage.Identifier.makeFunction(calName), messageHandler);
                    
                    //We need to check that resultType is still assignment compatible with the subscripted array type after loading.
                    //This could be broken by changes in the inheritence hierarchy of the underlying Java classes.
                    //
                    //note: we should not explicitly check that subscriptedArrayType == null and fail on that case with the message below. Rather we will
                    //fail with a NPE since this would be a CAL implementation bug. This is because if arrayType cannot be subscripted nSubscriptArgs times,
                    //then the type of arrayType must have changed. But that would invalidate the compiled module that this foreign function declaration
                    //depends on.
                    final Class<?> subscriptedArrayType = ForeignFunctionChecker.getSubscriptedArrayType(arrayType, nSubscriptArgs);               
                    if (!resultType.isAssignableFrom(subscriptedArrayType)) {
                         //"The underlying Java types involved in the CAL foreign function {0} have changed. Recompilation is needed."
                        messageHandler.handleMessage(new CompilerMessage(
                            new SourceRange(moduleName.toSourceText()),
                            CompilerMessage.Identifier.makeFunction(calName),
                            new MessageKind.Error.InvalidJavaTypeChangeOnLoading(calName)));
                    }            
                    
                    return new Pair<Class<?>, Class<?>>(arrayType, resultType);
                }
            };
            
            final ForeignEntityProvider<Class<?>> arrayTypeProvider = ForeignEntityProvider.make(msgLogger, arrayAndResultTypeResolver.getFirstResolver());
            if (arrayTypeProvider == null) {
                return null;
            }

            final ForeignEntityProvider<Class<?>> resultTypeProvider = ForeignEntityProvider.make(msgLogger, arrayAndResultTypeResolver.getSecondResolver());
            if (resultTypeProvider == null) {
                return null;
            }
                                                        
            s.skipRestOfRecord();

            return new SubscriptArray(calName, nSubscriptArgs, arrayTypeProvider, resultTypeProvider);
        }        
    
    }  
    
    /**
     * Information about a CAL foreign function that is in fact a Java array update operator.
     * <p>
     * 
     * The Java expression:
     * arrayExpr[index1][index2]...[index_m] = elemExpr
     * is considered as a CAL function of m + 2 arguments:
     * T1 -> Prelude.Int -> Prelude.Int -> ... -> Prelude.Int -> T2 -> T2
     * where T1 is a CAL type whose implementation type is an n-dimensional Java array type
     * and T2 is a CAL type whose implementation type is the Java type resulting in subscripting the array with m indices.
     * In particular, we require that m >= 1 and m <= n to satisfy the constraints on subscripting. Note that the update
     * function just returns the value that is being updated.
     * <p>
     * 
     * Note that the subscript arguments can be any foreign type with Java implementation type "int" and not just
     * Prelude.Int.
     * 
     * @author Bo Ilic
     */
    public static final class UpdateArray extends ForeignFunctionInfo {
        
        private static final int serializationSchema = 0;
        
        /** 
         * the number of supplied int arguments specifying dimension sizes. Must be >= 1 and <= the number of dimensions
         * in the array.
         */
        private final int nSubscriptArgs;
        
        /** the type of the array having one of its elements updated. */
        private final ForeignEntityProvider<Class<?>> arrayTypeProvider; 
        
        /** the type of the element being updated. */
        private final ForeignEntityProvider<Class<?>> elementTypeProvider;
                
        UpdateArray(final QualifiedName calName, final int nSubscriptArgs, final ForeignEntityProvider<Class<?>> arrayTypeProvider, final ForeignEntityProvider<Class<?>> elementTypeProvider) {
            super(calName, JavaKind.UPDATE_ARRAY);
                                 
            if (arrayTypeProvider == null) {
                throw new NullPointerException();
            }
            
            this.nSubscriptArgs = nSubscriptArgs;
            this.arrayTypeProvider = arrayTypeProvider;
            this.elementTypeProvider = elementTypeProvider;
        }                   
              
        /**
         * {@inheritDoc}
         */       
        @Override
        public int getNArguments() {
            return nSubscriptArgs + 2;
        }              
        
        /** {@inheritDoc} */
        @Override
        public Class<?> getJavaArgumentType(final int argN) throws UnableToResolveForeignEntityException { 
            
            if (argN == 0) {
                
                return arrayTypeProvider.get();
                
            } else if (argN > 0 && argN <= nSubscriptArgs) {
                
                return int.class;
                
            } else if (argN == nSubscriptArgs + 1) {
                
                return elementTypeProvider.get();
                
            }
            
            throw new IndexOutOfBoundsException();
        }             
        
        /** {@inheritDoc} */
        @Override
        public Class<?> getJavaReturnType() throws UnableToResolveForeignEntityException {
            
            return elementTypeProvider.get();
        }

        /** {@inheritDoc} */
        @Override
        public void resolveForeignEntities() throws UnableToResolveForeignEntityException {
            // Force resolution by calling get() on each of the ForeignEntityProviders
            arrayTypeProvider.get();
            elementTypeProvider.get();
        }
                
        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {

            final StringBuilder result = new StringBuilder ("foreign ").append(super.calName);
            result.append(" [kind = ").append(super.javaKind).append("]");  
            result.append(" [nUpdateArgs = ").append(nSubscriptArgs).append(", type = ");
            result.append(arrayTypeProvider).append("]");
           
            return result.toString();       
        } 
        
        /**
         * {@inheritDoc}
         */
        @Override
        final void write (final RecordOutputStream s) throws IOException, UnableToResolveForeignEntityException {
            s.startRecord(ModuleSerializationTags.FOREIGN_FUNCTION_INFO_UPDATE_ARRAY, serializationSchema);

            super.write(s); 
            
            s.writeInt(nSubscriptArgs);  
            s.writeUTF(arrayTypeProvider.get().getName());  
            s.writeUTF(elementTypeProvider.get().getName());
           
            s.endRecord();
        }   
        
        /**
         * Load an instance of ForeignFunctionInfo.UpdateArray from the RecordInputStream.
         * Read position will be before the record header.
         * @param s
         * @param moduleName the name of the module being loaded
         * @param foreignClassLoader the classloader to use to resolve foreign classes.
         * @param msgLogger the logger to which to log deserialization messages.
         * @return an instance of ForeignFunctionInfo, or null if there was a problem resolving classes.
         * @throws IOException
         */
        private static final ForeignFunctionInfo load (final RecordInputStream s, final int schema, final ModuleName moduleName, final ClassLoader foreignClassLoader, final CompilerMessageLogger msgLogger) throws IOException {            
            DeserializationHelper.checkSerializationSchema(schema, serializationSchema, moduleName, "ForeignFunctionInfo.UpdateArray", msgLogger);
            
            //read the superclass fields
            final QualifiedName calName = s.readQualifiedName();          
            final JavaKind javaKind = JavaKind.load(s, moduleName, msgLogger);
            if (javaKind != JavaKind.UPDATE_ARRAY) {
                throw new IOException();
            }
            
            final int nSubscriptArgs = s.readInt();
           
            final String arrayTypeName = s.readUTF();
            final String elementTypeName = s.readUTF();
            
            final ClassPairResolver arrayAndElementTypeResolver = new ClassPairResolver(arrayTypeName, elementTypeName) {
                @Override
                Pair<Class<?>, Class<?>> resolveClassPair(final ForeignEntityProvider.MessageHandler messageHandler) throws UnableToResolveForeignEntityException {
                    final Class<?> arrayType = DeserializationHelper.classForName(arrayTypeName, foreignClassLoader, "ForeignFunctionInfo.UpdateArray " + calName, CompilerMessage.Identifier.makeFunction(calName), messageHandler);
                    final Class<?> elementType = DeserializationHelper.classForName(elementTypeName, foreignClassLoader, "ForeignFunctionInfo.UpdateArray " + calName, CompilerMessage.Identifier.makeFunction(calName), messageHandler);
                    
                    //We need to check that elementType is still assignment compatible with the subscripted array type after loading.
                    //This could be broken by changes in the inheritence hierarchy of the underlying Java classes.
                    //
                    //note: we should not explicitly check that subscriptedArrayType == null and fail on that case with the message below. Rather we will
                    //fail with a NPE since this would be a CAL implementation bug. This is because if arrayType cannot be subscripted nSubscriptArgs times,
                    //then the type of arrayType must have changed. But that would invalidate the compiled module that this foreign function declaration
                    //depends on.
                    final Class<?> subscriptedArrayType = ForeignFunctionChecker.getSubscriptedArrayType(arrayType, nSubscriptArgs);               
                    if (!subscriptedArrayType.isAssignableFrom(elementType)) {
                         //"The underlying Java types involved in the CAL foreign function {0} have changed. Recompilation is needed."
                        messageHandler.handleMessage(new CompilerMessage(
                            new SourceRange(moduleName.toSourceText()),
                            CompilerMessage.Identifier.makeFunction(calName),
                            new MessageKind.Error.InvalidJavaTypeChangeOnLoading(calName)));
                    }
                    
                    return new Pair<Class<?>, Class<?>>(arrayType, elementType);
                }
            };

            final ForeignEntityProvider<Class<?>> arrayTypeProvider = ForeignEntityProvider.make(msgLogger, arrayAndElementTypeResolver.getFirstResolver());
            if (arrayTypeProvider == null) {
                return null;
            }
            
            final ForeignEntityProvider<Class<?>> elementTypeProvider = ForeignEntityProvider.make(msgLogger, arrayAndElementTypeResolver.getSecondResolver());
            if (elementTypeProvider == null) {
                return null;
            }
             
            s.skipRestOfRecord();

            return new UpdateArray(calName, nSubscriptArgs, arrayTypeProvider, elementTypeProvider);
        }        
    
    }    
    
           
    private ForeignFunctionInfo(final QualifiedName calName, final JavaKind javaKind) {
        if (calName == null || javaKind == null) {
            throw new NullPointerException();
        }
        this.calName = calName;
        this.javaKind = javaKind;
    }
                  
    
    /**
     * Creation date: (June 28, 2002)
     * @return QualifiedName name of the foreign function in CAL e.g. "Cal.Core.Prelude.isLowerCase"
     */       
    final public QualifiedName getCalName() {              
        return calName;
    }
    
    /**    
     * @return Class the return type of the foreign entity as a Java class. Java methods that return void will
     *     return Void.class here.
     * @throws UnableToResolveForeignEntityException 
     */
    abstract public Class<?> getJavaReturnType() throws UnableToResolveForeignEntityException;
    
    /**    
     * @param argN a zero-based argument index
     * @return Class the Java class corresponding to the argN argument of the CAL foreign function.
     * @throws UnableToResolveForeignEntityException 
     */
    abstract public Class<?> getJavaArgumentType(int argN) throws UnableToResolveForeignEntityException;
        
    /**
     * Creation date: (June 28, 2002)
     * @return int number of arguments as a CAL function.
     * @throws UnableToResolveForeignEntityException 
     */       
    abstract public int getNArguments() throws UnableToResolveForeignEntityException;


    /**
     * Force the resolution of any associated foreign entities.
     * @throws UnableToResolveForeignEntityException
     */
    abstract public void resolveForeignEntities() throws UnableToResolveForeignEntityException;

    /**
     * Creation date: (June 28, 2002)
     * @return whether a method, static method, field, static field, constructor or cast
     */       
    final public JavaKind getJavaKind() {              
        return javaKind;
    }
    
                     
    /**
     * String representation of this value. This function handles inconsistent state ForeignFunctionInfo
     * objects and so should be useful for debugging.
     * Creation date: (May 7, 2002)
     * @return String
     */       
    @Override
    abstract public String toString();
    
    /**
     * Write this instance of ForeignFunctionInfo to the RecordOutputStream.
     * @param s
     * @throws IOException
     * @throws UnableToResolveForeignEntityException 
     */
    void write (final RecordOutputStream s) throws IOException, UnableToResolveForeignEntityException {       
        s.writeQualifiedName(calName);        
        javaKind.write(s);  
    }
    
    /**
     * Load an instance of ForeignFunctionInfo from the RecordInputStream.
     * Read position will be before the record header.
     * @param s
     * @param moduleName the name of the module being loaded
     * @param foreignClassLoader the classloader to use to resolve foreign classes.
     * @param msgLogger the logger to which to log deserialization messages.
     * @return an instance of ForeignFunctionInfo, or null if there was a problem resolving classes.
     * @throws IOException
     */
    static final ForeignFunctionInfo load (final RecordInputStream s, final ModuleName moduleName, final ClassLoader foreignClassLoader, final CompilerMessageLogger msgLogger) throws IOException {
        
        // Load the record header and determine which actual class we are loading.
        final RecordHeaderInfo rhi = s.findRecord(SERIALIZATION_RECORD_TAGS);
        if (rhi == null) {
            throw new IOException ("Unable to find record header for ForeignFunctionInfo.");
        }
       
        switch(rhi.getRecordTag()) {
            case ModuleSerializationTags.FOREIGN_FUNCTION_INFO_INVOCATION:
            {
                return Invocation.loadInvocation(s, rhi.getSchema(), moduleName, foreignClassLoader, msgLogger);
            }
    
            case ModuleSerializationTags.FOREIGN_FUNCTION_INFO_CAST:
            {
                return Cast.load(s, rhi.getSchema(), moduleName, foreignClassLoader, msgLogger);
            }
            
            case ModuleSerializationTags.FOREIGN_FUNCTION_INFO_INSTANCE_OF:
            {
                return InstanceOf.load(s, rhi.getSchema(), moduleName, foreignClassLoader, msgLogger);
            }
            
            case ModuleSerializationTags.FOREIGN_FUNCTION_INFO_NULL_LITERAL:
            {
                return NullLiteral.load(s, rhi.getSchema(), moduleName, foreignClassLoader, msgLogger);
            }
            
            case ModuleSerializationTags.FOREIGN_FUNCTION_INFO_NULL_CHECK:
            {
                return NullCheck.load(s, rhi.getSchema(), moduleName, foreignClassLoader, msgLogger);
            }
            
            case ModuleSerializationTags.FOREIGN_FUNCTION_INFO_NEW_ARRAY:
            {
                return NewArray.load(s, rhi.getSchema(), moduleName, foreignClassLoader, msgLogger);
            }
            
            case ModuleSerializationTags.FOREIGN_FUNCTION_INFO_LENGTH_ARRAY:
            {
                return LengthArray.load(s, rhi.getSchema(), moduleName, foreignClassLoader, msgLogger);
            }
            
            case ModuleSerializationTags.FOREIGN_FUNCTION_INFO_SUBSCRIPT_ARRAY:
            {
                return SubscriptArray.load(s, rhi.getSchema(), moduleName, foreignClassLoader, msgLogger);
            }
            
            case ModuleSerializationTags.FOREIGN_FUNCTION_INFO_UPDATE_ARRAY:
            {
                return UpdateArray.load(s, rhi.getSchema(), moduleName, foreignClassLoader, msgLogger);
            }            
    
            case ModuleSerializationTags.FOREIGN_FUNCTION_INFO_CLASS_LITERAL:
            {
                return ClassLiteral.load(s, rhi.getSchema(), moduleName, foreignClassLoader, msgLogger);
            }
            
            default:
            {
                throw new IOException("Unexpected record tag " + rhi.getRecordTag() + " encountered loading ForeignFunctionInfo.");
            }
        }    
    }
  
}
