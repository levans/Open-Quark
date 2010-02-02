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
 * StandaloneJarBuilder.java
 * Created: May 17, 2007
 * By: Joseph Wong
 */

package org.openquark.cal.internal.machine.lecc;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.EmptyVisitor;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureVisitor;
import org.openquark.cal.caldoc.CALDocToJavaDocUtilities;
import org.openquark.cal.caldoc.CALDocToJavaDocUtilities.JavadocCrossReferenceGenerator;
import org.openquark.cal.compiler.ClassMethod;
import org.openquark.cal.compiler.DataConstructor;
import org.openquark.cal.compiler.ForeignTypeInfo;
import org.openquark.cal.compiler.FunctionalAgent;
import org.openquark.cal.compiler.IdentifierInfo;
import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.ModuleTypeInfo;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.Scope;
import org.openquark.cal.compiler.ScopedEntityNamingPolicy;
import org.openquark.cal.compiler.SourceModelTraverser;
import org.openquark.cal.compiler.TypeConsApp;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.compiler.UnableToResolveForeignEntityException;
import org.openquark.cal.compiler.CALDocComment.ModuleReference;
import org.openquark.cal.compiler.CALDocComment.ScopedEntityReference;
import org.openquark.cal.compiler.SourceModel.Constraint.TypeClass;
import org.openquark.cal.internal.javamodel.AsmJavaBytecodeGenerator;
import org.openquark.cal.internal.javamodel.JavaClassRep;
import org.openquark.cal.internal.javamodel.JavaConstructor;
import org.openquark.cal.internal.javamodel.JavaExpression;
import org.openquark.cal.internal.javamodel.JavaFieldDeclaration;
import org.openquark.cal.internal.javamodel.JavaGenerationException;
import org.openquark.cal.internal.javamodel.JavaMethod;
import org.openquark.cal.internal.javamodel.JavaOperator;
import org.openquark.cal.internal.javamodel.JavaReservedWords;
import org.openquark.cal.internal.javamodel.JavaSourceGenerator;
import org.openquark.cal.internal.javamodel.JavaStatement;
import org.openquark.cal.internal.javamodel.JavaTypeName;
import org.openquark.cal.internal.javamodel.JavaExpression.LiteralWrapper;
import org.openquark.cal.internal.javamodel.JavaStatement.JavaDocComment;
import org.openquark.cal.internal.machine.lecc.LECCModule.FunctionGroupInfo;
import org.openquark.cal.internal.module.Cal.Core.CAL_Prelude_internal;
import org.openquark.cal.internal.runtime.lecc.LECCMachineConfiguration;
import org.openquark.cal.internal.runtime.lecc.RTValue;
import org.openquark.cal.internal.runtime.lecc.StandaloneJarGeneratedCodeInfo;
import org.openquark.cal.internal.runtime.lecc.StandaloneJarResourceAccess;
import org.openquark.cal.machine.MachineFunction;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.runtime.ExecutionContext;
import org.openquark.cal.runtime.ExecutionContextProperties;
import org.openquark.cal.runtime.MachineType;
import org.openquark.cal.runtime.ResourceAccess;
import org.openquark.cal.services.CALWorkspace;
import org.openquark.cal.services.GemEntity;
import org.openquark.cal.services.ModulePackager;
import org.openquark.cal.services.ProgramModelManager;
import org.openquark.cal.services.WorkspaceManager;
import org.openquark.util.FileSystemHelper;
import org.openquark.util.Pair;

/**
 * This class implements a builder for constructing a <em>standalone JAR</em>, which may contain
 * application and library classes.
 * <p>
 * A standalone JAR may package up a CAL application by gathering together all the generated runtime
 * classes necessary for running a specific CAL function, and a class containing a
 * <code>public static void main(String[] args)</code> method which runs the CAL function directly
 * (without having to first initialize a CAL workspace). This makes it possible to package up a CAL
 * function into a command-line application.
 * <p>
 * To run the CAL function using the standalone JAR, simply include the standalone JAR, the CAL
 * platform jars, and any required external jars on the classpath, and specify the name of the
 * generated class as the one to run. Command line arguments will be passed into the CAL function.
 * <p>
 * A standalone JAR may also package up one or more library classes - a library class is a non-instatiatable class
 * containing static methods corresponding to the functions and data constructors defined in a particular
 * CAL module. This makes it possible to expose CAL libraries in Java, by defining API modules in CAL (whose
 * functions may include code marshalling to/from foreign types), from which library classes are
 * generated.
 * <p>
 * This utility only works with the LECC machine.
 * <p>
 * In this version, we support generating standalone applications for CAL functions with the type
 * <code>[String] -> ()</code>. In this case, the command line arguments array will be marshalled
 * into a CAL list of Strings.
 * 
 * @see org.openquark.cal.services.StandaloneJarTool the command-line interface for this JAR builder.
 * 
 * @author Joseph Wong
 */
public final class StandaloneJarBuilder {

    /** The name of the String[] parameter of the main method in the generated main class. */
    private static final String ARGS_PARAMETER_NAME = "args";
    
    /** The default user-friendly name to use for the execution context variable. */
    private static final String EXECUTION_CONTEXT_USER_FRIENDLY_NAME = "executionContext";
    
    /** A debugging flag to enable the generation of private entities in library classes. */
    private static final boolean ENABLE_GENERATION_OF_PRIVATE_ENTITIES_FOR_DEBUGGING = false;
    
    /**
     * This class implements a bytecode visitor (based on the ASM library) which identifies the names of
     * all LECC generated classes that are referenced in the bytecode of a specific class.
     *
     * @author Joseph Wong
     */
    private static final class GeneratedClassDependencyFindingVisitor extends EmptyVisitor {
        
        /**
         * This class implements a signature visitor which identifiers the names of all LECC generated classes
         * that are referenced in a class, method, or field signature.
         *
         * @author Joseph Wong
         */
        private final class GeneratedClassDependencyFindingSignatureVisitor implements SignatureVisitor {
            
            /**
             * The last class name encountered by this visitor. This field is important for building the fully
             * qualified name of an inner class (because the argument to {@link #visitInnerClassType} is only the
             * unqualified name of the inner class).
             */
            String lastClassName;

            /** {@inheritDoc} */
            public SignatureVisitor visitArrayType() {
                return this;
            }

            /** {@inheritDoc} */
            public void visitBaseType(final char descriptor) {}

            /** {@inheritDoc} */
            public SignatureVisitor visitClassBound() {
                return this;
            }

            /** {@inheritDoc} */
            public void visitClassType(final String name) {
                lastClassName = name;
                processInternalClassName(name);
            }

            /** {@inheritDoc} */
            public void visitEnd() {}

            /** {@inheritDoc} */
            public SignatureVisitor visitExceptionType() {
                return this;
            }

            /** {@inheritDoc} */
            public void visitFormalTypeParameter(final String name) {}

            /** {@inheritDoc} */
            public void visitInnerClassType(final String name) {
                final String fullInnerClassName = lastClassName + "." + name;
                lastClassName = fullInnerClassName;
                processInternalClassName(fullInnerClassName);
            }

            /** {@inheritDoc} */
            public SignatureVisitor visitInterface() {
                return this;
            }

            /** {@inheritDoc} */
            public SignatureVisitor visitInterfaceBound() {
                return this;
            }

            /** {@inheritDoc} */
            public SignatureVisitor visitParameterType() {
                return this;
            }

            /** {@inheritDoc} */
            public SignatureVisitor visitReturnType() {
                return this;
            }

            /** {@inheritDoc} */
            public SignatureVisitor visitSuperclass() {
                return this;
            }

            /** {@inheritDoc} */
            public void visitTypeArgument() {}

            /** {@inheritDoc} */
            public SignatureVisitor visitTypeArgument(final char wildcard) {
                return this;
            }

            /** {@inheritDoc} */
            public void visitTypeVariable(final String name) {}
        }

        /**
         * The SignatureVisitor instance for collecting the class names appearing in class/method/field signatures.
         */
        private final SignatureVisitor signatureVisitor = new GeneratedClassDependencyFindingSignatureVisitor();
        
        /**
         * The set of names of generated classes that have been encountered so far. 
         */
        private final SortedSet<String> foundClassNames = new TreeSet<String>();
        
        /**
         * @return the set of names of generated classes that have been encountered so far.
         */
        SortedSet<String> getFoundClassNames() {
            return foundClassNames;
        }
        
        /**
         * Processes a field type signature.
         * @param signature a field type signature. Can be null.
         */
        private void processFieldTypeSignature(final String signature) {
            if (signature != null) {
                new SignatureReader(signature).acceptType(signatureVisitor);
            }
        }
        
        /**
         * Processes a class signature or a method type signature.
         * @param signature a class signature or a method type signature. Can be null.
         */
        private void processClassOrMethodTypeSignature(final String signature) {
            if (signature != null) {
                new SignatureReader(signature).accept(signatureVisitor);
            }
        }
        
        /**
         * Processes an internal class name (internal as in <code>java/lang/Object</code> rather than <code>java.lang.Object</code>).
         * If the class name refers to a generated class, then it is added to the set {@link #foundClassNames}.
         * 
         * @param internalClassName the internal class name.
         */
        private void processInternalClassName(final String internalClassName) {
            if (internalClassName.startsWith("org/openquark/cal_")) {
                foundClassNames.add(internalClassName.replace('/', '.'));
            }
        }

        /** {@inheritDoc} */
        @Override
        public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces) {
            processClassOrMethodTypeSignature(signature);
            processInternalClassName(superName);
            for (final String interfaceName : interfaces) {
                processInternalClassName(interfaceName);
            }
            super.visit(version, access, name, signature, superName, interfaces);
        }

        /** {@inheritDoc} */
        @Override
        public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
            throw new UnsupportedOperationException("We don't currently generate LECC code with annotations.");
            //processClassOrMethodTypeSignature(desc);
            //return super.visitAnnotation(desc, visible);
        }

        /** {@inheritDoc} */
        @Override
        public AnnotationVisitor visitAnnotation(final String name, final String desc) {
            throw new UnsupportedOperationException("We don't currently generate LECC code with annotations.");
            //processClassOrMethodTypeSignature(desc);
            //return super.visitAnnotation(name, desc);
        }

        /** {@inheritDoc} */
        @Override
        public void visitEnum(final String name, final String desc, final String value) {
            throw new UnsupportedOperationException("We don't currently generate LECC code with Java 5 enumerations.");
            //processClassOrMethodTypeSignature(desc);
            //super.visitEnum(name, desc, value);
        }

        /** {@inheritDoc} */
        @Override
        public FieldVisitor visitField(final int access, final String name, final String desc, final String signature, final Object value) {
            processClassOrMethodTypeSignature(desc);
            processFieldTypeSignature(signature);
            return super.visitField(access, name, desc, signature, value);
        }

        /** {@inheritDoc} */
        @Override
        public void visitFieldInsn(final int opcode, final String owner, final String name, final String desc) {
            processInternalClassName(owner);
            processClassOrMethodTypeSignature(desc);
            super.visitFieldInsn(opcode, owner, name, desc);
        }

        /** {@inheritDoc} */
        @Override
        public void visitInnerClass(final String name, final String outerName, final String innerName, final int access) {
            processInternalClassName(name);
            // we don't process the outer class name, because we should have already visited the outer class declaration.
            super.visitInnerClass(name, outerName, innerName, access);
        }

        /** {@inheritDoc} */
        @Override
        public void visitLocalVariable(final String name, final String desc, final String signature, final Label start, final Label end, final int index) {
            processClassOrMethodTypeSignature(desc);
            processFieldTypeSignature(signature);
            super.visitLocalVariable(name, desc, signature, start, end, index);
        }

        /** {@inheritDoc} */
        @Override
        public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
            processClassOrMethodTypeSignature(desc);
            processClassOrMethodTypeSignature(signature);
            return super.visitMethod(access, name, desc, signature, exceptions);
        }

        /** {@inheritDoc} */
        @Override
        public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc) {
            processInternalClassName(owner);
            processClassOrMethodTypeSignature(desc);
            super.visitMethodInsn(opcode, owner, name, desc);
        }

        /** {@inheritDoc} */
        @Override
        public void visitMultiANewArrayInsn(final String desc, final int dims) {
            processClassOrMethodTypeSignature(desc);
            super.visitMultiANewArrayInsn(desc, dims);
        }

        /** {@inheritDoc} */
        @Override
        public void visitOuterClass(final String owner, final String name, final String desc) {
            processInternalClassName(owner);
            processInternalClassName(owner + "$" + name);
            processClassOrMethodTypeSignature(desc);
            super.visitOuterClass(owner, name, desc);
        }

        /** {@inheritDoc} */
        @Override
        public AnnotationVisitor visitParameterAnnotation(final int parameter, final String desc, final boolean visible) {
            throw new UnsupportedOperationException("We don't currently generate LECC code with annotations.");
            //processClassOrMethodTypeSignature(desc);
            //return super.visitParameterAnnotation(parameter, desc, visible);
        }

        /** {@inheritDoc} */
        @Override
        public void visitTypeInsn(final int opcode, final String desc) {
            // The ASM documentation says that the descriptor can either be an array descriptor, or an internal class name.
            if (desc.startsWith("[")) {
                processClassOrMethodTypeSignature(desc);
            } else {
                processInternalClassName(desc);
            }
            super.visitTypeInsn(opcode, desc);
        }
    }
    
    /**
     * This subclass of {@link JarOutputStream} overrides {@link JarOutputStream#putNextEntry} to log the
     * addition of each new entry to a progress monitor.
     *
     * @author Joseph Wong
     */
    private static final class LoggingJarOutputStream extends JarOutputStream {

        /** The progress monitor to be used. */
        private final Monitor monitor;
        
        /**
         * Constructs an instance of this class.
         * @param outputStream the actual output stream.
         * @param manifest the manifest.
         * @param monitor the progress monitor to be used.
         * @throws IOException
         */
        private LoggingJarOutputStream(final OutputStream outputStream, final Manifest manifest, final Monitor monitor) throws IOException {
            super(outputStream);
            
            // First set up the monitor (needed by putNextEntry)
            if (monitor == null) {
                throw new NullPointerException();
            }
            this.monitor = monitor;
            
            // Then, write out the manifest.
            final ZipEntry zipEntry = new ZipEntry(JarFile.MANIFEST_NAME);
            putNextEntry(zipEntry);
            try {
                manifest.write(new BufferedOutputStream(this));
            } finally {
                closeEntry();
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void putNextEntry(final ZipEntry ze) throws IOException {
            super.putNextEntry(ze);
            monitor.addingFile(ze.getName());
        }
    }
    
    /**
     * This subclass of {@link ZipOutputStream} overrides {@link ZipOutputStream#putNextEntry} to log the
     * addition of each new entry to a progress monitor.
     *
     * @author Joseph Wong
     */
    private static final class LoggingSourceZipOutputStream extends ZipOutputStream {

        /** The progress monitor to be used. */
        private final Monitor monitor;
        
        /**
         * Constructs an instance of this class.
         * @param outputStream the actual output stream.
         * @param monitor the progress monitor to be used.
         */
        private LoggingSourceZipOutputStream(final OutputStream outputStream, final Monitor monitor) {
            super(outputStream);
            
            if (monitor == null) {
                throw new NullPointerException();
            }
            this.monitor = monitor;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void putNextEntry(final ZipEntry ze) throws IOException {
            super.putNextEntry(ze);
            monitor.addingSourceFile(ze.getName());
        }
    }
    
    /**
     * This interface specifies a progress monitor to be used with a {@link StandaloneJarBuilder} for
     * monitoring the progress of the JAR building operation.
     *
     * @author Joseph Wong
     */
    public static interface Monitor {
        
        /**
         * Invoked when the {@link StandaloneJarBuilder} has started adding files to the JAR.
         * @param jarName the name of the JAR being built.
         */
        public void jarBuildingStarted(String jarName);
        
        /**
         * Invoked when the {@link StandaloneJarBuilder} is adding a file to the JAR.
         * @param fileName the name of file being added.
         */
        public void addingFile(String fileName);
        
        /**
         * Invoked when the {@link StandaloneJarBuilder} is skipping a function because its type contains type class constraints.
         * @param name the name of the function being skipped.
         */
        public void skippingFunctionWithTypeClassConstraints(QualifiedName name);
        
        /**
         * Invoked when the {@link StandaloneJarBuilder} is skipping a class method because its type contains type class constraints.
         * @param name the name of the function being skipped.
         */
        public void skippingClassMethod(QualifiedName name);
        
        /**
         * Invoked when the {@link StandaloneJarBuilder} is adding a source file to the source zip.
         * @param fileName the name of file being added.
         */
        public void addingSourceFile(String fileName);
        
        /**
         * Invoked when the {@link StandaloneJarBuilder} has finished building a JAR.
         * @param jarName the name of the JAR being built.
         * @param success true if the operation was successful; false otherwise.
         */
        public void jarBuildingDone(String jarName, boolean success);
    }
    
    /**
     * This is a default implementation of the Monitor interface which does nothing.
     * 
     * This class is meant to be used through its singleton instance.
     *
     * @author Joseph Wong
     */
    public static final class DefaultMonitor implements Monitor {
        /** Private constructor. */
        private DefaultMonitor() {}
        
        // default implementation
        public void jarBuildingStarted(final String jarName) {}
        public void addingFile(final String fileName) {}
        public void skippingFunctionWithTypeClassConstraints(final QualifiedName name) {}
        public void skippingClassMethod(final QualifiedName name) {}
        public void addingSourceFile(final String fileName) {}
        public void jarBuildingDone(final String jarName, final boolean success) {}
        
        /** Singleton instance. */ 
        public static final DefaultMonitor INSTANCE = new DefaultMonitor();
    }
    
    /**
     * This exception class represents an invalid configuration for the JAR building operation.
     *
     * @author Joseph Wong
     */
    public static final class InvalidConfigurationException extends Exception {
        
        private static final long serialVersionUID = -69241695569382720L;

        /**
         * Constructs an instance of this exception.
         * @param message the message string.
         */
        private InvalidConfigurationException(final String message) {
            super(message);
        }
    }
    
    /**
     * This exception class represents some internal problem that occurred during the JAR building operation.
     *
     * @author Joseph Wong
     */
    public static final class InternalProblemException extends Exception {
        
        private static final long serialVersionUID = 5489404664329733927L;

        /**
         * Constructs an instance of this exception.
         * @param cause the underlying cause.
         */
        private InternalProblemException(final Exception cause) {
            super(cause);
        }
    }
    
    /**
     * Represents information about an argument or return type for a generated library method.
     *
     * @author Joseph Wong
     */
    private static final class ClassTypeInfo {
        
        /**
         * The underlying Java type represented. Can be null to indicate that the CAL type is not a foreign type.
         */
        private final Class<?> classType;
        
        /**
         * The name of the type as exposed as an argument or return type of the generated library method.
         */
        private final JavaTypeName exposedTypeName;
        
        /**
         * The name of the {@link RTValue} subclass for representing values of this type.
         */
        private final JavaTypeName rtValueTypeName;
        
        /**
         * The name of the {@link RTValue} method for unmarshalling the underlying value. Can be null to indicate that the CAL type is not a foreign type.
         */
        private final String unmarshallingMethodName;

        /**
         * Private constructor.
         * @param classType the underlying Java type represented. Can be null to indicate that the CAL type is not a foreign type.
         * @param exposedTypeName the name of the type as exposed as an argument or return type of the generated library method.
         * @param rtValueTypeName the name of the {@link RTValue} subclass for representing values of this type.
         * @param unmarshallingMethodName the name of the {@link RTValue} method for unmarshalling the underlying value. Can be null to indicate that the CAL type is not a foreign type.
         */
        private ClassTypeInfo(final Class<?> classType, final JavaTypeName exposedTypeName, final JavaTypeName rtValueTypeName, final String unmarshallingMethodName) {
            if (exposedTypeName == null || rtValueTypeName == null) {
                throw new NullPointerException();
            }
            if (classType == null || unmarshallingMethodName == null) {
                if (!(classType == null && unmarshallingMethodName == null)) {
                    throw new IllegalArgumentException("classType and unmarshallingMethodName must be both null, or both non-null");
                }
            }
            this.classType = classType;
            this.exposedTypeName = exposedTypeName;
            this.rtValueTypeName = rtValueTypeName;
            this.unmarshallingMethodName = unmarshallingMethodName;
        }
        
        /**
         * Factory method for creating instances.
         * @param classType the underlying Java type represented. Can be null to indicate that the CAL type is not a foreign type.
         * @return an instance of this class.
         */
        private static ClassTypeInfo make(final Class<?> classType) {
            if (classType == null) {
                return makeNonForeign();
            }
            
            final JavaTypeName rtValueTypeName;
            final String unmarshallingMethodName;
            
            if (classType == char.class) {
                rtValueTypeName = JavaTypeNames.RTDATA_CHAR;
                unmarshallingMethodName = "getCharValue";
                
            } else if (classType == boolean.class) {
                rtValueTypeName = JavaTypeNames.RTDATA_BOOLEAN;
                unmarshallingMethodName = "getBooleanValue";
                
            } else if (classType == byte.class) {
                rtValueTypeName = JavaTypeNames.RTDATA_BYTE;
                unmarshallingMethodName = "getByteValue";
                
            } else if (classType == short.class) {
                rtValueTypeName = JavaTypeNames.RTDATA_SHORT;
                unmarshallingMethodName = "getShortValue";
                
            } else if (classType == int.class) {
                rtValueTypeName = JavaTypeNames.RTDATA_INT;
                unmarshallingMethodName = "getIntValue";
                
            } else if (classType == long.class) {
                rtValueTypeName = JavaTypeNames.RTDATA_LONG;
                unmarshallingMethodName = "getLongValue";
                
            } else if (classType == float.class) {
                rtValueTypeName = JavaTypeNames.RTDATA_FLOAT;
                unmarshallingMethodName = "getFloatValue";
                
            } else if (classType == double.class) {
                rtValueTypeName = JavaTypeNames.RTDATA_DOUBLE;
                unmarshallingMethodName = "getDoubleValue";
                
            } else if (classType == String.class) {
                rtValueTypeName = JavaTypeNames.RTDATA_STRING;
                unmarshallingMethodName = "getStringValue";
                
            } else if (classType == BigInteger.class) {
                rtValueTypeName = JavaTypeNames.RTDATA_INTEGER;
                unmarshallingMethodName = "getIntegerValue";
                
            } else {
                rtValueTypeName = JavaTypeNames.RTDATA_OPAQUE;
                unmarshallingMethodName = "getOpaqueValue";
            }
            
            return new ClassTypeInfo(classType, JavaTypeName.make(classType), rtValueTypeName, unmarshallingMethodName);
        }
        
        /**
         * Factory method for creating an instance representing a non foreign CAL type.
         * @return an instance of this class.
         */
        private static ClassTypeInfo makeNonForeign() {
            return new ClassTypeInfo(null, JavaTypeName.CAL_VALUE, JavaTypeNames.RTVALUE, null);
        }

        /**
         * @return the underlying Java type represented. Can be null to indicate that the CAL type is not a foreign type.
         */
        private Class<?> getClassType() {
            return classType;
        }

        /**
         * @return the name of the type as exposed as an argument or return type of the generated library method.
         */
        private JavaTypeName getExposedTypeName() {
            return exposedTypeName;
        }

        /**
         * @return the name of the {@link RTValue} subclass for representing values of this type.
         */
        private JavaTypeName getRTValueTypeName() {
            return rtValueTypeName;
        }

        /**
         * @return the name of the {@link RTValue} method for unmarshalling the underlying value. Can be null to indicate that the CAL type is not a foreign type.
         */
        private String getUnmarshallingMethodName() {
            return unmarshallingMethodName;
        }
        
        /**
         * @return the return type of the unmarshalling method. Can be null to indicate that the CAL type is not a foreign type.
         */
        private JavaTypeName getReturnTypeOfUnmarshallingMethod() {
            if (unmarshallingMethodName == null) {
                return null;
            } else {
                if (rtValueTypeName.equals(JavaTypeNames.RTDATA_OPAQUE)) {
                    // getOpaqueValue()'s return type is java.lang.Object
                    return JavaTypeName.OBJECT;
                } else {
                    // for other types, the return type is the primitive type represented by classType
                    return JavaTypeName.make(classType);
                }
            }
        }
        
        /**
         * @return the argument type of the marshalling method. Can be null to indicate that the CAL type is not a foreign type.
         */
        private JavaTypeName getArgumentTypeOfMarshallingMethod() {
            // the same handling for return type of the unmarshalling method applies here
            return getReturnTypeOfUnmarshallingMethod();
        }
    }
    
    /**
     * An enum representing the different scopes in Java.
     *
     * @author Joseph Wong
     */
    public static enum JavaScope {
        /**
         * The public scope.
         */
        PUBLIC(Modifier.PUBLIC),
        /**
         * The protected scope.
         */
        PROTECTED(Modifier.PROTECTED),
        /**
         * The package (a.k.a. default) scope.
         */
        PACKAGE(0),
        /**
         * The private scope.
         */
        PRIVATE(Modifier.PRIVATE);
        
        /**
         * The associated modifier value, according to {@link Modifier}.
         */
        private final int modifier;
        
        /**
         * @param modifier the associated modifier value, according to {@link Modifier}.
         */
        JavaScope(final int modifier) {
            this.modifier = modifier;
        }
        
        /**
         * @return the associated modifier value, according to {@link Modifier}.
         */
        int getModifier() {
            return modifier;
        }
    }
    
    /**
     * The specification for a generated main class.
     *
     * @author Joseph Wong
     */
    public static final class MainClassSpec {
        
        /**
         * The name of the main class to be generated.
         */
        private final JavaTypeName className;
        
        /**
         * The name of the entry point function.
         */
        private final QualifiedName entryPointName;

        /**
         * Private constructor.
         * @param className the name of the main class to be generated.
         * @param entryPointName the name of the entry point function.
         */
        private MainClassSpec(final JavaTypeName className, final QualifiedName entryPointName) {
            if (className == null || entryPointName == null) {
                throw new NullPointerException();
            }
            this.className = className;
            this.entryPointName = entryPointName;
        }
        
        /**
         * Factory method for creating an instance of this class.
         * @param className the name of the main class to be generated.
         * @param entryPointName the name of the entry point function.
         * @return an instance of this class.
         * @throws InvalidConfigurationException if an invalid class name was specified.
         */
        public static MainClassSpec make(final String className, final QualifiedName entryPointName) throws InvalidConfigurationException {
            checkClassName(className);
            return new MainClassSpec(JavaTypeName.make(className, false), entryPointName);
        }

        /**
         * @return the name of the main class to be generated.
         */
        public JavaTypeName getClassName() {
            return className;
        }

        /**
         * @return the name of the entry point function.
         */
        public QualifiedName getEntryPointName() {
            return entryPointName;
        }
    }
    
    /**
     * The specification for a generated library class.
     *
     * @author Joseph Wong
     */
    public static final class LibraryClassSpec {
        
        /**
         * The scope of the class.
         */
        private final JavaScope scope;
        
        /**
         * The name of the class.
         */
        private final JavaTypeName className;
        
        /**
         * The name of the module.
         */
        private final ModuleName moduleName;

        /**
         * Private constructor.
         * @param scope the scope of the class.
         * @param className the name of the class.
         * @param moduleName the name of the module.
         */
        private LibraryClassSpec(final JavaScope scope, final JavaTypeName className, final ModuleName moduleName) {
            if (scope == null || className == null || moduleName == null) {
                throw new NullPointerException();
            }
            this.scope = scope;
            this.className = className;
            this.moduleName = moduleName;
        }
        
        /**
         * Factory method for creating an instance of this class.
         * @param scope the scope of the class.
         * @param className the name of the class.
         * @param moduleName the name of the module.
         * @return an instance of this class.
         * @throws InvalidConfigurationException if an invalid class name was specified.
         */
        public static LibraryClassSpec make(final JavaScope scope, final String className, final ModuleName moduleName) throws InvalidConfigurationException {
            checkClassName(className);
            return new LibraryClassSpec(scope, JavaTypeName.make(className, false), moduleName);
        }

        /**
         * @return the scope of the class.
         */
        public JavaScope getScope() {
            return scope;
        }

        /**
         * @return the name of the class.
         */
        public JavaTypeName getClassName() {
            return className;
        }

        /**
         * @return the name of the module.
         */
        public ModuleName getModuleName() {
            return moduleName;
        }
    }
    
    /**
     * Implements a cross-reference generator capable of generating non-hyperlinked text for cross-references
     * with a custom naming policy.
     *
     * @author Joseph Wong
     */
    private static final class JavadocLinkGenerator extends JavadocCrossReferenceGenerator {
        
        /**
         * A mapping from functional agent names to names of the generated methods.
         */
        private final Map<String, String> methodNameMapping;

        /**
         * The scoped entity naming policy to use.
         */
        private final ScopedEntityNamingPolicy.UnqualifiedInCurrentModuleOrInPreludeIfUnambiguous namingPolicy;
        
        /**
         * Constructs an instance of this class.
         * @param methodNameMapping a mapping from functional agent names to names of the generated methods.
         * @param namingPolicy the scoped entity naming policy to use.
         */
        private JavadocLinkGenerator(final Map<String, String> methodNameMapping, final ScopedEntityNamingPolicy.UnqualifiedInCurrentModuleOrInPreludeIfUnambiguous namingPolicy) {
            if (methodNameMapping == null || namingPolicy == null) {
                throw new NullPointerException();
            }
            this.methodNameMapping = methodNameMapping;
            this.namingPolicy = namingPolicy;
        }

        /** {@inheritDoc} */
        @Override
        public String getModuleReferenceHTML(final ModuleReference reference) {
            return reference.getName().toSourceText();
        }

        /** {@inheritDoc} */
        @Override
        public String getTypeConsReferenceHTML(final ScopedEntityReference reference) {
            return namingPolicy.getName(new IdentifierInfo.TopLevel.TypeCons(reference.getName()));
        }

        /** {@inheritDoc} */
        @Override
        public String getDataConsReferenceHTML(final ScopedEntityReference reference) {
            if (reference.getName().getModuleName().equals(namingPolicy.getCurrentModuleName())) {
                return wrapWithEscapedLink(sanitizeMethodNameForJava(reference.getName().getUnqualifiedName(), methodNameMapping));
            } else {
                return namingPolicy.getName(new IdentifierInfo.TopLevel.DataCons(reference.getName()));
            }
        }

        /** {@inheritDoc} */
        @Override
        public String getFunctionOrClassMethodReferenceHTML(final ScopedEntityReference reference) {
            if (reference.getName().getModuleName().equals(namingPolicy.getCurrentModuleName())) {
                return wrapWithEscapedLink(sanitizeMethodNameForJava(reference.getName().getUnqualifiedName(), methodNameMapping));
            } else {
                return namingPolicy.getName(new IdentifierInfo.TopLevel.FunctionOrClassMethod(reference.getName()));
            }
        }

        /** {@inheritDoc} */
        @Override
        public String getTypeClassReferenceHTML(final ScopedEntityReference reference) {
            return namingPolicy.getName(new IdentifierInfo.TopLevel.TypeClass(reference.getName()));
        }
        
        /** {@inheritDoc} */
        @Override
        public String postProcess(final String html) {
            // todo-jowong do we want to generate the label, i.e. {@link #foo foo}, so as to make the generated Javadoc look good
            // or do we want to generate simply {@link #foo}, which makes the code look good but the generated Javadoc ugly
            return html
                .replaceAll("<code><javadocLink>([^<]*)</javadocLink></code>", "{@link #$1 $1}")
                .replaceAll("<javadocLink>([^<]*)</javadocLink>", "{@link #$1 $1}");
        }
        
        /**
         * Wraps a Javadoc reference with an escaped link, to be processed later by {@link #postProcess}.
         * @param referenceName the reference.
         * @return the string for the escaped link.
         */
        private String wrapWithEscapedLink(final String referenceName) {
            return "<javadocLink>" + referenceName + "</javadocLink>";
        }
    }
    
    /**
     * Abstract class for representing a generator of the source zip file.
     *
     * @author Joseph Wong
     */
    private static abstract class SourceGenerator {
        
        /**
         * Implements the null pattern - this is a generator that does nothing.
         *
         * @author Joseph Wong
         */
        private static final class Null extends SourceGenerator {
            /** {@inheritDoc} */
            @Override
            void generateSource(final ZipEntry zipEntry, final JavaClassRep classRep) {}
            /** {@inheritDoc} */
            @Override
            void flushAndClose() {}
        }
        
        /**
         * A generator for generating a source zip file.
         *
         * @author Joseph Wong
         */
        private static final class ZipFile extends SourceGenerator {
            
            /**
             * The zip output stream.
             */
            private final ZipOutputStream zos;
            
            /**
             * Constructs an instance of this class.
             * @param zos the zip output stream.
             */
            private ZipFile(final ZipOutputStream zos) {
                if (zos == null) {
                    throw new NullPointerException();
                }
                this.zos = zos;
            }
            
            /** {@inheritDoc} */
            @Override
            void generateSource(final ZipEntry zipEntry, final JavaClassRep classRep) throws UnsupportedEncodingException, IOException, JavaGenerationException {
                zos.putNextEntry(zipEntry);
                try {
                    zos.write(JavaSourceGenerator.generateSourceCode(classRep).getBytes("UTF-8"));
                } finally {
                    zos.closeEntry();
                }
            }
            
            /** {@inheritDoc} */
            @Override
            void flushAndClose() throws IOException {
                zos.flush();
                zos.close();
            }
        }
        
        /**
         * Generates the source for the given Java class representation and put it in the zip file.
         * @param zipEntry the associated zip entry.
         * @param classRep the Java class representation.
         * @throws UnsupportedEncodingException
         * @throws IOException
         * @throws JavaGenerationException
         */
        abstract void generateSource(ZipEntry zipEntry, JavaClassRep classRep) throws UnsupportedEncodingException, IOException, JavaGenerationException;
        
        /**
         * Flushes and closes the underlying stream.
         * @throws IOException 
         */
        abstract void flushAndClose() throws IOException;
    }
    
    /** For each supported literal type, this maps the Class of the boxed type to the Class of the unboxed type. */
    private static final Map<Class<?>, Class<?>> supportedLiteralTypesBoxedToUnboxedClassMap = new HashMap<Class<?>, Class<?>>();
    static {
        supportedLiteralTypesBoxedToUnboxedClassMap.put(Character.class, char.class);
        supportedLiteralTypesBoxedToUnboxedClassMap.put(Boolean.class, boolean.class);
        supportedLiteralTypesBoxedToUnboxedClassMap.put(Byte.class, byte.class);
        supportedLiteralTypesBoxedToUnboxedClassMap.put(Short.class, short.class);
        supportedLiteralTypesBoxedToUnboxedClassMap.put(Integer.class, int.class);
        supportedLiteralTypesBoxedToUnboxedClassMap.put(Float.class, float.class);
        supportedLiteralTypesBoxedToUnboxedClassMap.put(Long.class, long.class);
        supportedLiteralTypesBoxedToUnboxedClassMap.put(Double.class, double.class);
        // String is special - it's not really a boxed type, so it maps to itself
        supportedLiteralTypesBoxedToUnboxedClassMap.put(String.class, String.class);
        // BigInteger is special - it's not really a boxed type, so it maps to itself
        supportedLiteralTypesBoxedToUnboxedClassMap.put(BigInteger.class, BigInteger.class);
    }
    
    /** Private constructor. This class is not meant to be instantiated. */
    private StandaloneJarBuilder() {}

    /**
     * Builds a standalone JAR based on the specified main class and library class specs, and writing the JAR to the named file location.
     * Optionally a source zip file will also be generated.
     * This method will not build the JAR if the arguments fail to pass the relevant semantic checks.
     * 
     * @param outputFile the location of the output JAR to be written.
     * @param srcZipOutputFile the local of the source zip file to be written. Can be null.
     * @param mainClassSpecs a list of main class specs.
     * @param libClassSpecs a list of library class specs.
     * @param workspaceManager the WorkspaceManager which provides access to the program and the workspace.
     * @param monitor the progress monitor to be used.
     * @throws IOException
     * @throws InvalidConfigurationException if an invalid configuration for the JAR building operation was specified.
     * @throws InternalProblemException if some internal problem occurred during the JAR building operation.
     * @throws UnableToResolveForeignEntityException 
     */
    public static void buildStandaloneJar(final File outputFile, final File srcZipOutputFile, final List<MainClassSpec> mainClassSpecs, final List<LibraryClassSpec> libClassSpecs, final WorkspaceManager workspaceManager, final Monitor monitor) throws IOException, InvalidConfigurationException, InternalProblemException, UnableToResolveForeignEntityException {
        
        final File outputDirectory = outputFile.getParentFile();
        if (outputDirectory != null) {
            // If the file path has only the file name component, the outputDirectory is null
            // and the file is intended to go into the current directory. Otherwise, we ensure
            // the file's containing directory actually exists.
            FileSystemHelper.ensureDirectoryExists(outputDirectory);
        }
        final FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
        
        ////
        /// Set up the source generator, if the source zip file location is not null.
        //
        final FileOutputStream srzZipFileOutputStream;
        final BufferedOutputStream srcZipFileBufferedOutputStream;
        final SourceGenerator sourceGen;
        if (srcZipOutputFile != null) {
            
            final File srcZipOutputDirectory = srcZipOutputFile.getParentFile();
            if (srcZipOutputDirectory != null) {
                // If the file path has only the file name component, the outputDirectory is null
                // and the file is intended to go into the current directory. Otherwise, we ensure
                // the file's containing directory actually exists.
                FileSystemHelper.ensureDirectoryExists(outputDirectory);
            }
            
            srzZipFileOutputStream = new FileOutputStream(srcZipOutputFile);
            srcZipFileBufferedOutputStream = new BufferedOutputStream(srzZipFileOutputStream, 1024);

            final ZipOutputStream zos = new LoggingSourceZipOutputStream(srcZipFileBufferedOutputStream, monitor);
            sourceGen = new SourceGenerator.ZipFile(zos);
            
        } else {
            srzZipFileOutputStream = null;
            srcZipFileBufferedOutputStream = null;
            sourceGen = new SourceGenerator.Null();
        }
        
        // keep track of whether the operation succeeded
        boolean success = false;
        try {
            final BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream, 1024);
        
            try {
                buildStandaloneJarToOutputStream(outputFile.getAbsolutePath(), bufferedOutputStream, sourceGen, mainClassSpecs, libClassSpecs, workspaceManager, monitor);
                
            } finally {
                bufferedOutputStream.flush();
                bufferedOutputStream.close();
                
                if (srcZipFileBufferedOutputStream != null) {
                    srcZipFileBufferedOutputStream.flush();
                    srcZipFileBufferedOutputStream.close();
                }
            }

            // if the operation reaches here without an exception, then it has succeeded
            success = true;
            
        } finally {
            fileOutputStream.flush();
            fileOutputStream.close();
            
            if (srzZipFileOutputStream != null) {
                srzZipFileOutputStream.flush();
                srzZipFileOutputStream.close();
            }
            
            // if the operation failed with an exception, delete the output file
            if (!success) {
                outputFile.delete();
                
                if (srcZipOutputFile != null) {
                    srcZipOutputFile.delete();
                }
            }
        }
    }

    /**
     * Builds a standalone JAR based on the specified main class and library class specs, and writing the JAR to the specified output stream.
     * Optionally a source zip file will also be generated.
     * This method will not build the JAR if the arguments fail to pass the relevant semantic checks.
     * 
     * @param jarName the name of the JAR to be built.
     * @param outputStream the output stream to which the JAR is to be written.
     * @param sourceGen the generator to use for generating source files (into a zip file).
     * @param mainClassSpecs a list of main class specs.
     * @param libClassSpecs a list of library class specs.
     * @param workspaceManager the WorkspaceManager which provides access to the program and the workspace.
     * @param monitor the progress monitor to be used.
     * @throws IOException
     * @throws InvalidConfigurationException if an invalid configuration for the JAR building operation was specified.
     * @throws InternalProblemException if some internal problem occurred during the JAR building operation.
     * @throws UnableToResolveForeignEntityException 
     */
    private static void buildStandaloneJarToOutputStream(final String jarName, final OutputStream outputStream, final SourceGenerator sourceGen, final List<MainClassSpec> mainClassSpecs, final List<LibraryClassSpec> libClassSpecs, final WorkspaceManager workspaceManager, final Monitor monitor) throws IOException, InternalProblemException, InvalidConfigurationException, UnableToResolveForeignEntityException {
        
        // Check the current machine configuration, and throw InvalidConfigurationException if there are problems
        checkCurrentMachineConfiguration(workspaceManager);
        
        // Keep track of whether the operation succeeded
        boolean success = false;
        
        monitor.jarBuildingStarted(jarName);
        
        ////
        /// Create the manifest, and a JarOutputStream with the manifest.
        //
        final Manifest manifest = new Manifest();
        final Attributes mainAttributes = manifest.getMainAttributes();
        mainAttributes.putValue(Attributes.Name.MANIFEST_VERSION.toString(), "1.0");
        if (!mainClassSpecs.isEmpty()) {
            mainAttributes.putValue(Attributes.Name.MAIN_CLASS.toString(), mainClassSpecs.get(0).getClassName().getName());
        }
        
        final JarOutputStream jos = new LoggingJarOutputStream(outputStream, manifest, monitor);

        final Set<String> classesAlreadyAdded = new HashSet<String>();
        final Set<String> sourcesAlreadyAdded = new HashSet<String>();
        final CALWorkspace workspace = workspaceManager.getWorkspace();
        final SortedSet<ModuleName> allRequredModules = new TreeSet<ModuleName>();
        
        try {
            ////
            /// First generate the main classes (and their associated classes and resources)
            //
            for (final MainClassSpec spec : mainClassSpecs) {
                
                final JavaTypeName mainClassName = spec.getClassName();
                final QualifiedName entryPointName = spec.getEntryPointName();

                // Check the main class's configuration, and throw InvalidConfigurationException if there are problems
                checkMainClassConfiguration(entryPointName, workspaceManager);
                
                final ModuleName rootModule = entryPointName.getModuleName();

                ////
                /// Write out the main class and the generated classes required by the class.
                //
                final String mainClassRelativePath = mainClassName.getName().replace('.', '/') + ".class";
                final ZipEntry mainClassEntry = new ZipEntry(mainClassRelativePath);

                final byte[] mainClassBytecode;
                try {
                    final JavaClassRep mainClassRep = makeMainClass(mainClassName, entryPointName, workspaceManager);
                    mainClassBytecode = AsmJavaBytecodeGenerator.encodeClass(mainClassRep);
                    
                    // Write out the source for the class
                    final String javaFileRelativePath = mainClassName.getName().replace('.', '/') + ".java";
                    final ZipEntry javaFileEntry = new ZipEntry(javaFileRelativePath);
                    sourceGen.generateSource(javaFileEntry, mainClassRep);
                    
                    writeClassAndOtherRequiredClassesAndGatherResources(workspaceManager, jos, classesAlreadyAdded, sourcesAlreadyAdded, allRequredModules, rootModule, mainClassEntry, mainClassBytecode, sourceGen);
                    
                } catch (final JavaGenerationException e) {
                    // We should not have problems generating the main class - if there are any then it is an internal problem
                    throw new InternalProblemException(e);
                }
            }

            ////
            /// Then generate the library classes (and their associated classes and resources)
            //
            for (final LibraryClassSpec spec : libClassSpecs) {
                
                final JavaTypeName libClassName = spec.getClassName();
                final ModuleName libModuleName = spec.getModuleName();

                // Check the library class's configuration, and throw InvalidConfigurationException if there are problems
                checkLibraryClassConfiguration(libModuleName, workspaceManager);
                
                ////
                /// Write out the library class and the generated classes required by the class.
                //
                final String libClassRelativePath = libClassName.getName().replace('.', '/') + ".class";
                final ZipEntry libClassEntry = new ZipEntry(libClassRelativePath);

                final byte[] libClassBytecode;
                try {
                    final JavaClassRep libClassRep = makeLibraryClass(spec.getScope(), libClassName, libModuleName, workspaceManager, monitor);
                    libClassBytecode = AsmJavaBytecodeGenerator.encodeClass(libClassRep);
                    
                    // Write out the source for the class
                    final String javaFileRelativePath = libClassName.getName().replace('.', '/') + ".java";
                    final ZipEntry javaFileEntry = new ZipEntry(javaFileRelativePath);
                    sourceGen.generateSource(javaFileEntry, libClassRep);
                    
                    writeClassAndOtherRequiredClassesAndGatherResources(workspaceManager, jos, classesAlreadyAdded, sourcesAlreadyAdded, allRequredModules, libModuleName, libClassEntry, libClassBytecode, sourceGen);
                    
                } catch (final JavaGenerationException e) {
                    // We should not have problems generating the library class - if there are any then it is an internal problem
                    throw new InternalProblemException(e);
                }
            }

            ////
            /// Write out the user resources for the required modules.
            //
            for (final ModuleName moduleName : allRequredModules) {
                ModulePackager.writeUserResourcesToJar(workspace, moduleName, jos);
            }
            
            ////
            /// The End - if the operation reaches here without an exception, then we declare success.
            //
            success = true;
            
        } catch (final RuntimeException e) {
            // Runtime exceptions are really internal problems for us
            throw new InternalProblemException(e);
            
        } finally {
            // Signal that we're done with the JAR building operation (whether it succeeded or failed)
            monitor.jarBuildingDone(jarName, success);
            
            try {
                jos.flush();
                jos.close();
                sourceGen.flushAndClose();
            } catch (final ZipException e) {
                // we will ignore the zip errors on flushing and closing... especially since closing a
                // ZipOutputStream that has not been written to causes an exception to be thrown:
                //   java.util.zip.ZipException: ZIP file must have at least one entry
            }
        }
    }

    /**
     * Writes out the given class bytecode, and any other required classes (as determined by analyzing the bytecode) and
     * required resources.
     * 
     * @param workspaceManager the workspace manager.
     * @param jos the JAR output stream.
     * @param classesAlreadyAdded the classes already added to the JAR.
     * @param sourcesAlreadyAdded the sources already added to the JAR.
     * @param allRequredModules the names of all required modules.
     * @param rootModule the root module.
     * @param classEntry the zip entry for the given class.
     * @param classBytecode the bytecode for the class to be written out and analyzed.
     * @param sourceGen the generator to use for generating source files (into a zip file).
     * @throws IOException
     * @throws InternalProblemException
     * @throws JavaGenerationException 
     */
    private static void writeClassAndOtherRequiredClassesAndGatherResources(
        final WorkspaceManager workspaceManager,
        final JarOutputStream jos,
        final Set<String> classesAlreadyAdded,
        final Set<String> sourcesAlreadyAdded,
        final SortedSet<ModuleName> allRequredModules,
        final ModuleName rootModule,
        final ZipEntry classEntry,
        final byte[] classBytecode,
        final SourceGenerator sourceGen) throws IOException, InternalProblemException, JavaGenerationException {
        
        final CALWorkspace workspace = workspaceManager.getWorkspace();
        
        ////
        /// Write out the class.
        //
        jos.putNextEntry(classEntry);
        try {
            jos.write(classBytecode);
        } finally {
            jos.closeEntry();
        }

        ////
        /// Write out the generated classes required by the given class.
        //
        final Set<String> mainClassDependencies = findClassDependencies(classBytecode);

        try {
            addGeneratedClassesToJar(jos, mainClassDependencies, classesAlreadyAdded, sourcesAlreadyAdded, workspaceManager, sourceGen);
        } catch (final ClassNotFoundException e) {
            // We should not have problems adding the generated classes - if there are any then it is an internal problem
            throw new InternalProblemException(e);
        }

        allRequredModules.addAll(findModuleDependencies(rootModule, workspace));
    }

    /**
     * Checks the machine configuration specified by the user, and throws an exception if the configuration is invalid.
     * @param programModelManager the ProgramModelManager which provides access to the program and machine configuration.
     * 
     * @throws InvalidConfigurationException if an invalid configuration for the JAR building operation was specified.
     */
    public static void checkCurrentMachineConfiguration(final ProgramModelManager programModelManager) throws InvalidConfigurationException {
        // This utility only works with the LECC machine.
        if (programModelManager.getMachineType() != MachineType.LECC) {
            throw new InvalidConfigurationException(StandaloneJarBuilderMessages.getString("leccMachineOnly"));
        }
    }

    /**
     * Checks the main class's configuration specified by the user, and throws an exception if the configuration is invalid.
     * @param entryPointName the name of the entry point function.
     * @param workspaceManager the WorkspaceManager which provides access to the program and the workspace.
     * @throws InvalidConfigurationException if an invalid configuration for the JAR building operation was specified.
     */
    public static void checkMainClassConfiguration(final QualifiedName entryPointName, final WorkspaceManager workspaceManager) throws InvalidConfigurationException {
        
        ////
        /// Run checks on the specified entry points. Currently we only support functional agents with the signature [String]->()
        //
        
        // Check that the entry point exists.
        final GemEntity entryPointEntity = workspaceManager.getWorkspace().getGemEntity(entryPointName);
        if (entryPointEntity == null) {
            throw new InvalidConfigurationException(StandaloneJarBuilderMessages.getString("entryPointDoesNotExist", entryPointName.getQualifiedName()));
        }
        
        // Check that the entry point has the right type.
        final ModuleName entryPointModuleName = entryPointName.getModuleName();
        final TypeExpr entryPointType = entryPointEntity.getTypeExpr();
        
        final TypeExpr requiredType = workspaceManager.getTypeChecker().getTypeFromString("[Prelude.String] -> ()", entryPointModuleName, null);
        
        if (!TypeExpr.canPatternMatch(entryPointType, requiredType, workspaceManager.getModuleTypeInfo(entryPointModuleName))) {
            throw new InvalidConfigurationException(StandaloneJarBuilderMessages.getString("incompatibleType", entryPointName.getQualifiedName(), entryPointType.toString(), requiredType.toString()));
        }
    }
    
    /**
     * Checks the library class's configuration specified by the user, and throws an exception if the configuration is invalid.
     * @param libModuleName the library module name.
     * @param programModelManager the ProgramModelManager which provides access to the program and machine configuration.
     * @throws InvalidConfigurationException if an invalid configuration for the JAR building operation was specified.
     */
    public static void checkLibraryClassConfiguration(final ModuleName libModuleName, final ProgramModelManager programModelManager) throws InvalidConfigurationException {
        
        // Check that the module exists.
        if (programModelManager.getModuleTypeInfo(libModuleName) == null) {
            throw new InvalidConfigurationException(StandaloneJarBuilderMessages.getString("moduleDoesNotExist", libModuleName.toSourceText()));
        }
    }

    /**
     * Checks a generated class name specified by the user, and throws an exception if the name is invalid.
     * @param className the name of the class to be generated.
     * 
     * @throws InvalidConfigurationException if an invalid configuration for the JAR building operation was specified.
     */
    private static void checkClassName(final String className) throws InvalidConfigurationException {
        // Validate that the name is a valid Java class name
        final String[] mainClassNameParts = className.split("\\.");
        final int nMainClassNameParts = mainClassNameParts.length;
        
        for (int i = 0; i < nMainClassNameParts; i++) {
            
            final String mainClassNamePart = mainClassNameParts[i];
            final int len = mainClassNamePart.length();
            
            for (int j = 0; j < len; j++) {
                
                final boolean isValid;
                if (j == 0) {
                    isValid = Character.isJavaIdentifierStart(mainClassNamePart.charAt(j));
                } else {
                    isValid = Character.isJavaIdentifierPart(mainClassNamePart.charAt(j));
                }
                
                if (!isValid) {
                    throw new InvalidConfigurationException(StandaloneJarBuilderMessages.getString("invalidClassName", className));
                }
            }
        }
        
        // We do not support generating classes into the default package
        if (className.indexOf('.') == -1) {
            throw new InvalidConfigurationException(StandaloneJarBuilderMessages.getString("classInDefaultPackageNotSupported", className));
        }
    }
    
    /**
     * Returns a set of all the dependencies of the specified module.
     * @param rootModuleName the name of the module whose dependencies are to be collected.
     * @param workspace the CAL workspace containing the module.
     * @return a set of ModuleNames of the modules which the specified module depends on (directly or indirectly).
     */
    private static SortedSet<ModuleName> findModuleDependencies(final ModuleName rootModuleName, final CALWorkspace workspace) {
        
        final CALWorkspace.DependencyFinder depFinder = workspace.getDependencyFinder(Collections.singletonList(rootModuleName));
        
        final SortedSet<ModuleName> result = new TreeSet<ModuleName>();
        result.addAll(depFinder.getRootSet());
        result.addAll(depFinder.getImportedModulesSet());
        
        return result;
    }

    /**
     * Writes the specified generated classes, and all the transitive closure of other generated classes they depend on,
     * to the specified JarOutputStream.
     * 
     * @param jos the output stream for the standalone JAR being generated.
     * @param namesOfGeneratedClasses the names of the generated classes to be added to the JAR.
     * @param classesAlreadyAdded a set of the names of classes already added to the JAR.
     * @param sourcesAlreadyAdded a set of the sources already added to the JAR.
     * @param programModelManager the ProgramModelManager which provides access to the program.
     * @param sourceGen the generator to use for generating source files (into a zip file).
     * @throws ClassNotFoundException
     * @throws IOException
     * @throws JavaGenerationException 
     */
    private static void addGeneratedClassesToJar(final JarOutputStream jos, final Set<String> namesOfGeneratedClasses, final Set<String> classesAlreadyAdded, final Set<String> sourcesAlreadyAdded, final ProgramModelManager programModelManager, final SourceGenerator sourceGen) throws ClassNotFoundException, IOException, JavaGenerationException {

        final Set<String> classesToAdd = new TreeSet<String>(namesOfGeneratedClasses);

        // We do not loop via an iterator, as entries are both added to and removed from set classesToAdd in each iteration. 
        while (!classesToAdd.isEmpty()) {
            final String className = classesToAdd.iterator().next();

            if (!classesAlreadyAdded.contains(className)) {
                // Fetch the bytecode for the class via the CALClassLoader
                final ModuleName moduleName = CALToJavaNames.getModuleNameFromPackageName(className);
                final LECCModule module = (LECCModule)programModelManager.getModule(moduleName);
                final CALClassLoader classLoader = module.getClassLoader();

                final byte[] bytecode = classLoader.getBytecodeForClass(className);
                
                // Add the class to the JAR
                addClassToJar(jos, className, bytecode);
                
                // Add the source to the JAR (if we are generating a source zip)
                if (!(sourceGen instanceof SourceGenerator.Null)) {
                    final JavaClassRep classRep = classLoader.getClassRepWithInnerClasses(className);
                    final String sourceName = classRep.getClassName().getName();

                    if (!sourcesAlreadyAdded.contains(sourceName)) {
                        final String javaFileRelativePath = sourceName.replace('.', '/') + ".java";
                        final ZipEntry javaFileEntry = new ZipEntry(javaFileRelativePath);
                        sourceGen.generateSource(javaFileEntry, classRep);

                        sourcesAlreadyAdded.add(sourceName);
                    }
                }

                // Now find the dependencies of this class and add it to the set to be processed.
                classesAlreadyAdded.add(className);
                classesToAdd.addAll(findClassDependencies(bytecode));
            }

            classesToAdd.remove(className);
        }
    }
    
    /**
     * Finds the names of the generated classes depended upon by the specified bytecode.
     * @param bytecode the bytecode to be scanned for its dependencies.
     * @return a set of the names the generated classes depended upon by the specified bytecode.
     */
    private static SortedSet<String> findClassDependencies(final byte[] bytecode) {
        final ClassReader classReader = new ClassReader(bytecode);
        final GeneratedClassDependencyFindingVisitor visitor = new GeneratedClassDependencyFindingVisitor();
        classReader.accept(visitor, ClassReader.SKIP_FRAMES);
        return visitor.getFoundClassNames();
    }

    /**
     * Writes a class to a JAR via the specified JarOutputStream.
     * 
     * @param jos the output stream for the standalone JAR being generated.
     * @param className the name of the class to be added.
     * @param bytecode the bytecode of the class to be added.
     * @throws IOException 
     */
    private static void addClassToJar(final JarOutputStream jos, final String className, final byte[] bytecode) throws IOException {
        
        final String relativePath = className.replace('.', '/') + ".class";
        final ZipEntry entry = new ZipEntry(relativePath);
        
        jos.putNextEntry(entry);
        try {
            jos.write(bytecode);
        } finally {
            jos.closeEntry();
        }
    }

    /**
     * Creates the representation for the main class (the class with the <code>main</code> method) from which the
     * pertinent CAL function can be run directly.
     * 
     * @param mainClassName the name of the main class to be generated.
     * @param entryPointName the name of the entry point function.
     * @param programModelManager the ProgramModelManager which provides access to the program.
     * @return the representation of the main class.
     */
    private static JavaClassRep makeMainClass(final JavaTypeName mainClassName, final QualifiedName entryPointName, final ProgramModelManager programModelManager) {
        
        final LECCModule module = (LECCModule)programModelManager.getModule(entryPointName.getModuleName());
        
        final JavaClassRep classRep = new JavaClassRep(mainClassName, JavaTypeName.OBJECT, Modifier.PUBLIC|Modifier.FINAL, new JavaTypeName[0]);
        
        // Code fragment:
        //
        // private {MainClassConstructorName} {}
        //
        
        classRep.addConstructor(makePrivateConstructor(mainClassName));
        
        // Code fragment:
        //
        // public static void main(final String[] args) throws CALExecutorException
        //
        final JavaMethod mainMethod = new JavaMethod(Modifier.PUBLIC|Modifier.STATIC, JavaTypeName.VOID, ARGS_PARAMETER_NAME, JavaTypeName.STRING_ARRAY, true, "main");
        
        mainMethod.addThrows(JavaTypeName.CAL_EXECUTOR_EXCEPTION);
        
        ////
        /// The classes in the standalone JAR are generated with the machine configuration at "build time".
        /// We capture this configuration into a StandaloneJarGeneratedCodeInfo, to be checked at runtime
        /// against the machine configuration then.
        //
        
        final JavaStatement.LocalVariableDeclaration generatedCodeInfoDecl = makeGeneratedCodeInfoDecl();
        final JavaExpression.LocalVariable generatedCodeInfoLocalVar = generatedCodeInfoDecl.getLocalVariable();
        mainMethod.addStatement(generatedCodeInfoDecl);

        //
        // Code fragment:
        //
        // if (!generatedCodeInfo.isCompatibleWithCurrentConfiguration()) {
        //     System.err.println(generatedCodeInfo.getConfigurationHints());
        //     return;
        // }
        //
        final JavaStatement.Block configCheckFailureBody = new JavaStatement.Block();
        
        configCheckFailureBody.addStatement(
            new JavaStatement.ExpressionStatement(
                new JavaExpression.MethodInvocation.Instance(
                    new JavaExpression.JavaField.Static(JavaTypeName.SYSTEM, "err", JavaTypeName.PRINT_STREAM),
                    "println",
                    new JavaExpression.MethodInvocation.Instance(
                        generatedCodeInfoLocalVar,
                        "getConfigurationHints",
                        JavaTypeName.STRING,
                        JavaExpression.MethodInvocation.InvocationType.VIRTUAL),
                    JavaTypeName.STRING,
                    JavaTypeName.VOID,
                    JavaExpression.MethodInvocation.InvocationType.VIRTUAL)));
        
        configCheckFailureBody.addStatement(new JavaStatement.ReturnStatement());
        
        final JavaStatement configCheck =
            new JavaStatement.IfThenElseStatement(
                new JavaExpression.OperatorExpression.Unary(
                    JavaOperator.LOGICAL_NEGATE,
                    new JavaExpression.MethodInvocation.Instance(
                        generatedCodeInfoLocalVar,
                        "isCompatibleWithCurrentConfiguration",
                        JavaTypeName.BOOLEAN,
                        JavaExpression.MethodInvocation.InvocationType.VIRTUAL)),
                configCheckFailureBody);
        
        mainMethod.addStatement(configCheck);
        
        ////
        /// Generate code to obtain the class loader which loaded this main class.
        ///
        /// It is necessary to obtain this class loader and pass it into the execution context and the resource access
        /// because the class loader which loaded the CAL Platform classes may be an *ancestor* of the one which loaded
        /// the standalone JAR (e.g. the bootstrap class loader), and thus may not have access to the foreign classes
        /// and localized resources necessary for the standalone JAR to run.
        //
        
        // Code fragment:
        //
        // ClassLoader classloader = {MainClassName}.class.getClassLoader();
        //
        final JavaTypeName javaTypeName_ClassLoader = JavaTypeName.make(ClassLoader.class);
        
        final JavaExpression classloaderInit =
            new JavaExpression.MethodInvocation.Instance(
                new JavaExpression.ClassLiteral(mainClassName),
                "getClassLoader",
                javaTypeName_ClassLoader,
                JavaExpression.MethodInvocation.InvocationType.VIRTUAL);
        
        final JavaExpression.LocalVariable classloaderLocalVar = new JavaExpression.LocalVariable("classloader", javaTypeName_ClassLoader);
        
        final JavaStatement classloaderDecl = new JavaStatement.LocalVariableDeclaration(classloaderLocalVar, classloaderInit);
        
        mainMethod.addStatement(classloaderDecl);
        
        ////
        /// Generate code to set up the execution context to have access to a standalone-JAR-specific
        /// ResourceAccess implementation.
        //

        // Code fragment:
        //        
        // RTExecutionContext executionContext = new RTExecutionContext(
        //     new ExecutionContextProperties.Builder().toProperties(),
        //     new StandaloneRuntimeEnvironment(
        //         classloader,
        //         new StandaloneJarResourceAccess(classloader)));        
        //
        final JavaTypeName javaTypeName_ExecutionContextProperties = JavaTypeName.make(ExecutionContextProperties.class);
        final JavaTypeName javaTypeName_ResourceAccess = JavaTypeName.make(ResourceAccess.class);
        
        final JavaExpression newRuntimeEnvironment =
            new JavaExpression.ClassInstanceCreationExpression(
                JavaTypeNames.STANDALONE_RUNTIME_ENVIRONMENT,
                new JavaExpression[] {
                    classloaderLocalVar,
                    new JavaExpression.ClassInstanceCreationExpression(
                        JavaTypeName.make(StandaloneJarResourceAccess.class),
                        classloaderLocalVar,
                        javaTypeName_ClassLoader)},
                new JavaTypeName[] {
                    javaTypeName_ClassLoader,
                    javaTypeName_ResourceAccess}                                   
                );
        
        final JavaExpression executionContextInit =
            new JavaExpression.ClassInstanceCreationExpression(
                JavaTypeNames.RTEXECUTION_CONTEXT,
                new JavaExpression[] {
                    new JavaExpression.MethodInvocation.Instance(
                        new JavaExpression.ClassInstanceCreationExpression(JavaTypeName.make(ExecutionContextProperties.Builder.class)),
                        "toProperties",
                        javaTypeName_ExecutionContextProperties,
                        JavaExpression.MethodInvocation.InvocationType.VIRTUAL),
                    newRuntimeEnvironment                   
                },
                new JavaTypeName[] {
                    javaTypeName_ExecutionContextProperties,
                    JavaTypeNames.RUNTIME_ENIVONMENT
                });
        
        final JavaExpression.LocalVariable executionContextLocalVar = new JavaExpression.LocalVariable(EXECUTION_CONTEXT_USER_FRIENDLY_NAME, JavaTypeNames.RTEXECUTION_CONTEXT);
        final JavaStatement executionContextDecl = new JavaStatement.LocalVariableDeclaration(executionContextLocalVar, executionContextInit, true);
        
        mainMethod.addStatement(executionContextDecl);
        
        ////
        /// Generate code to run the entry point.
        //
        
        // Code fragment:
        //
        // {EntryPointClass's instance}
        //     .apply(Input_String_List.$instance.apply(new RTData.CAL_Opaque(args))
        //     .evaluate(executionContext);
        //
        final MachineFunction entryPointMachineFunction = module.getFunction(entryPointName);
        
        final MachineFunction inputStringListMachineFunction = module.getFunction(CAL_Prelude_internal.Functions.inputStringList);
        
        final JavaExpression runExpr =
            makeEvaluateInvocationExpr(
                makeApplyInvocationExpr(
                    getInstanceOfGeneratedClassJavaExpression(entryPointMachineFunction, module, executionContextLocalVar),
                    makeApplyInvocationExpr(
                        getInstanceOfGeneratedClassJavaExpression(inputStringListMachineFunction, module, executionContextLocalVar),
                        new JavaExpression.ClassInstanceCreationExpression(
                            JavaTypeNames.RTDATA_OPAQUE, new JavaExpression.MethodVariable(ARGS_PARAMETER_NAME), JavaTypeName.OBJECT))), executionContextLocalVar);
        
        mainMethod.addStatement(new JavaStatement.ExpressionStatement(runExpr));
        
        classRep.addMethod(mainMethod);
        
        // We are finished with building the class.
        return classRep;
    }

    /**
     * Creates the representation of a private constructor.
     * @param classJavaTypeName the name of the containing class.
     * @return the private constructor for the class.
     */
    private static JavaConstructor makePrivateConstructor(final JavaTypeName classJavaTypeName) {
        // Code fragment:
        //
        // private {ClassConstructorName} {}
        //
        
        // Technically, the name to be used for the constructor is the unqualified portion of the class name.
        // However, JavaTypeName.getUnqualifiedJavaSourceName() assumes that all '$' characters in the name
        // arise from the name-mangling of inner class names, and converts them to '.'. Since the class is
        // a top-level class, and since it is permissible to use '$' in a top-level class name, we have to use
        // JavaTypeName.getInternalUnqualifiedName() so that if the main class name is "com.xyz.Foo$Bar",
        // the constructor's name would end up as "Foo$Bar" rather than just "Bar".
        //
        // Also, this makes the assumption that if the class name has passed validation thus far, the JavaTypeName
        // is in fact a JavaTypeName.Reference.Object
        //
        final String classConstructorName = ((JavaTypeName.Reference.Object)classJavaTypeName).getInternalUnqualifiedName();
        return new JavaConstructor(Modifier.PRIVATE, classConstructorName);
    }
    
    /**
     * Creates the representation for a library class from a CAL module.
     * 
     * @param libClassScope the scope of the generated class.
     * @param libClassName the name of the library class to be generated.
     * @param moduleName the name of the module.
     * @param programModelManager the ProgramModelManager which provides access to the program.
     * @param monitor the progress monitor to be used.
     * @return the representation of the library class.
     * @throws UnableToResolveForeignEntityException
     */
    private static JavaClassRep makeLibraryClass(final JavaScope libClassScope, final JavaTypeName libClassName, final ModuleName moduleName, final ProgramModelManager programModelManager, final Monitor monitor) throws UnableToResolveForeignEntityException {
        
        final ModuleTypeInfo moduleTypeInfo = programModelManager.getModuleTypeInfo(moduleName);
        final LECCModule module = (LECCModule)programModelManager.getModule(moduleName);
        final ScopedEntityNamingPolicy.UnqualifiedInCurrentModuleOrInPreludeIfUnambiguous scopedEntityNamingPolicy =
            new ScopedEntityNamingPolicy.UnqualifiedInCurrentModuleOrInPreludeIfUnambiguous(moduleTypeInfo);
        
        final JavaClassRep classRep = new JavaClassRep(libClassName, JavaTypeName.OBJECT, libClassScope.getModifier()|Modifier.FINAL, new JavaTypeName[0]);
        
        // Code fragment:
        //
        // private {LibClassConstructorName} {}
        //
        
        classRep.addConstructor(makePrivateConstructor(libClassName));

        // We sort the functional agents, because the order returned by the ModuleTypeInfo is neither source order nor alphabetical order
        // Case-sensitive ordering would order data constructors ahead of functions
        final FunctionalAgent[] functionalAgents = moduleTypeInfo.getFunctionalAgents();
        Arrays.sort(functionalAgents, new Comparator<FunctionalAgent>() {
            public int compare(final FunctionalAgent a, final FunctionalAgent b) {
                return a.getName().compareTo(b.getName());
            }});
        
        final Map<String, String> methodNameMapping = sanitizeMethodNamesForJava(functionalAgents);
        final JavadocCrossReferenceGenerator crossRefGen = new JavadocLinkGenerator(methodNameMapping, scopedEntityNamingPolicy);
        
        // Add each functional agent as a method if it has a visible scope and it is not an overloaded function
        for (final FunctionalAgent functionalAgent : functionalAgents) {
            if (isScopeVisibleAsJavaLibraryAPI(functionalAgent.getScope())) {
                // Check that the functional agent has a type that can be handled.
                final TypeExpr functionalAgentType = functionalAgent.getTypeExpr();
                
                if (!hasTypeClassConstraints(functionalAgentType)) {
                    
                    final Pair<List<ClassTypeInfo>, ClassTypeInfo> argTypesAndReturnType = getArgTypesAndReturnTypeForLibraryMethod(functionalAgentType);
                    final List<ClassTypeInfo> argTypes = argTypesAndReturnType.fst();
                    final ClassTypeInfo returnType = argTypesAndReturnType.snd();
                    
                    final JavaMethod unboxedArgsVariant =
                        makeLibraryMethod(module, functionalAgent, functionalAgentType, argTypes, returnType, scopedEntityNamingPolicy, crossRefGen, methodNameMapping);
                    
                    classRep.addMethod(unboxedArgsVariant);
                    
                    if (hasNonCalValueArgument(argTypes)) {
                        // The first method has at least one unboxed/foreign argument, so we create a second version
                        // that takes all arguments as CalValues
                        
                        final int nArgs = argTypes.size();
                        final List<ClassTypeInfo> calValueArgTypes = new ArrayList<ClassTypeInfo>();
                        for (int i = 0; i < nArgs; i++) {
                            calValueArgTypes.add(ClassTypeInfo.makeNonForeign());
                        }
                        
                        final JavaMethod calValueArgsVariant =
                            makeLibraryMethod(module, functionalAgent, functionalAgentType, calValueArgTypes, returnType, scopedEntityNamingPolicy, crossRefGen, methodNameMapping);
                        
                        classRep.addMethod(calValueArgsVariant);
                    }
                } else {
                    // Report that the functional agent was skipped because its type contains type class constraints
                    if (functionalAgent instanceof ClassMethod) {
                        monitor.skippingClassMethod(functionalAgent.getName());
                    } else {
                        // an overloaded functional agent is really either a class method or a function, and never a data cons
                        monitor.skippingFunctionWithTypeClassConstraints(functionalAgent.getName());
                    }
                }
            }
        }
        
        // Add the Javadoc
        classRep.setJavaDoc(
            new JavaDocComment(CALDocToJavaDocUtilities.getJavadocFromCALDocComment(
                moduleTypeInfo.getCALDocComment(), true, null, null, null,
                scopedEntityNamingPolicy, crossRefGen, true, true, false, true)));

        ////
        /// Add the static check
        //
        
        // Code fragment:
        //
        // private static boolean __checkConfig()
        //
        final JavaMethod checkConfigMethod = new JavaMethod(Modifier.PRIVATE|Modifier.STATIC, JavaTypeName.BOOLEAN, "__checkConfig");
        
        ////
        /// The classes in the standalone JAR are generated with the machine configuration at "build time".
        /// We capture this configuration into a StandaloneJarGeneratedCodeInfo, to be checked at runtime
        /// against the machine configuration then.
        //
        
        final JavaStatement.LocalVariableDeclaration generatedCodeInfoDecl = makeGeneratedCodeInfoDecl();
        final JavaExpression.LocalVariable generatedCodeInfoLocalVar = generatedCodeInfoDecl.getLocalVariable();
        checkConfigMethod.addStatement(generatedCodeInfoDecl);

        //
        // Code fragment:
        //
        // if (!generatedCodeInfo.isCompatibleWithCurrentConfiguration()) {
        //     throw new IllegalStateException(generatedCodeInfo.getConfigurationHints());
        // } else {
        //     return true;
        // }
        //
        final JavaStatement.Block configCheckFailureBody = new JavaStatement.Block();
        
        configCheckFailureBody.addStatement(
            new JavaStatement.ThrowStatement(
                new JavaExpression.ClassInstanceCreationExpression(
                    JavaTypeName.make(IllegalStateException.class),
                    new JavaExpression.MethodInvocation.Instance(
                        generatedCodeInfoLocalVar,
                        "getConfigurationHints",
                        JavaTypeName.STRING,
                        JavaExpression.MethodInvocation.InvocationType.VIRTUAL),
                    JavaTypeName.STRING)));
        
        final JavaStatement configCheckSuccessBody = new JavaStatement.ReturnStatement(JavaExpression.LiteralWrapper.TRUE);
        
        final JavaStatement configCheck =
            new JavaStatement.IfThenElseStatement(
                new JavaExpression.OperatorExpression.Unary(
                    JavaOperator.LOGICAL_NEGATE,
                    new JavaExpression.MethodInvocation.Instance(
                        generatedCodeInfoLocalVar,
                        "isCompatibleWithCurrentConfiguration",
                        JavaTypeName.BOOLEAN,
                        JavaExpression.MethodInvocation.InvocationType.VIRTUAL)),
                configCheckFailureBody,
                configCheckSuccessBody);
        
        checkConfigMethod.addStatement(configCheck);
        
        classRep.addMethod(checkConfigMethod);

        // Code fragment:
        //
        // private static final boolean isCompatibleWithCurrentConfiguration = __checkConfig();
        //
        final JavaFieldDeclaration isCompatibleWithCurrentConfigurationField = new JavaFieldDeclaration(
            Modifier.PRIVATE|Modifier.STATIC|Modifier.FINAL,
            JavaTypeName.BOOLEAN,
            "isCompatibleWithCurrentConfiguration",
            new JavaExpression.MethodInvocation.Static(
                libClassName, checkConfigMethod.getMethodName(), new JavaExpression[0], new JavaTypeName[0], JavaTypeName.BOOLEAN));
        
        classRep.addFieldDeclaration(isCompatibleWithCurrentConfigurationField);
        
        // We are finished with building the class.
        return classRep;
    }

    /**
     * Checks whether the given type expression contains a type class constraint (which means that a function having that type
     * would have extra dictionary arguments added to the core function).
     * @param typeExpr a type expression to check.
     * @return true if the type expression contains a type class constraint; false otherwise.
     */
    private static boolean hasTypeClassConstraints(final TypeExpr typeExpr) {
        
        // todo-jowong could TypeExpr expose this functionality directly without having to go through the SourceModel?
        class TypeClassConstraintFinder extends SourceModelTraverser<Void, Void> {
            boolean foundTypeClassConstraint = false;

            @Override
            public Void visit_Constraint_TypeClass(final TypeClass typeClass, final Void arg) {
                foundTypeClassConstraint = true;
                return super.visit_Constraint_TypeClass(typeClass, arg);
            }
        }
        
        final TypeClassConstraintFinder finder = new TypeClassConstraintFinder();
        typeExpr.toSourceModel().accept(finder, null);
        
        return finder.foundTypeClassConstraint;
    }

    /**
     * Creates a representation of a library method in a library class corresponding to the given functional agent.
     * The constraint placed on the agent is that it cannot be overloaded (i.e. take dictionary arguments).
     * 
     * @param module the module containing the functional agent.
     * @param functionalAgent the functional agent.
     * @param functionalAgentType the type of the functional agent.
     * @param argTypes the argument types.
     * @param returnType the return type.
     * @param scopedEntityNamingPolicy the scoped entity naming policy to use for comment generation.
     * @param crossRefGen the cross reference generator for use in comment generation.
     * @param methodNameMapping a mapping from functional agent names to names of the generated methods.
     * @return the representation of the library method.
     */
    private static JavaMethod makeLibraryMethod(final LECCModule module, final FunctionalAgent functionalAgent, final TypeExpr functionalAgentType, final List<ClassTypeInfo> argTypes, final ClassTypeInfo returnType, final ScopedEntityNamingPolicy scopedEntityNamingPolicy, final JavadocCrossReferenceGenerator crossRefGen, final Map<String, String> methodNameMapping) {
        ////
        /// Figure out what the Java types should be for the arguments and return type
        //
        
        final int nFunctionalAgentArgs = argTypes.size();
        
        final List<String> functionalAgentArgNames = new ArrayList<String>();
        for (final String argNameFromCALDoc : CALDocToJavaDocUtilities.getArgumentNamesFromCALDocComment(functionalAgent.getCALDocComment(), functionalAgent, functionalAgentType)) {
            functionalAgentArgNames.add(sanitizeVarNameForJava(argNameFromCALDoc));
        }
        
        final List<String> methodArgNames = new ArrayList<String>();
        methodArgNames.addAll(functionalAgentArgNames);
        
        final List<JavaTypeName> methodArgTypeNames = new ArrayList<JavaTypeName>();
        for (final ClassTypeInfo classTypeInfo : argTypes) {
            methodArgTypeNames.add(classTypeInfo.getExposedTypeName());
        }

        ////
        /// Add the execution context argument
        //
        
        final String executionContextArgName;
        if (methodArgNames.contains(EXECUTION_CONTEXT_USER_FRIENDLY_NAME)) {
            // the user friendly name collides with an existing argument name, so use the internal one - guaranteed not to collide
            executionContextArgName = SCJavaDefn.EXECUTION_CONTEXT_NAME;
        } else {
            executionContextArgName = EXECUTION_CONTEXT_USER_FRIENDLY_NAME;
        }
        
        methodArgNames.add(executionContextArgName);
        // We use the non-internal ExecutionContext type as the arg type, rather than the internal RTExecutionContext
        // ...we will downcast on use
        methodArgTypeNames.add(JavaTypeName.make(ExecutionContext.class));
        
        final int nMethodArgs = nFunctionalAgentArgs + 1;

        ////
        /// Build up the chain of .apply(...) calls, with each argument marshalled properly
        /// We use the multi-argument apply() variants as much as possible
        /// Then build up the evaluation of the application chain.
        //
        
        final MachineFunction functionalAgentMachineFunction = module.getFunction(functionalAgent.getName());
        
        final JavaExpression executionContextArg = new JavaExpression.CastExpression(
            JavaTypeNames.RTEXECUTION_CONTEXT,
            new JavaExpression.MethodVariable(executionContextArgName));
        
        final List<JavaExpression> marshalledArgs = new ArrayList<JavaExpression>();
        for (int i = 0; i < nFunctionalAgentArgs; i++) {
            marshalledArgs.add(makeRTValueMarshallingExpr(
                argTypes.get(i), new JavaExpression.MethodVariable(methodArgNames.get(i))));
        }
        
        final JavaExpression resultExpr;
        final boolean isLiteralResult;
        
        // Special handling for literal value is required, because it may represent either an actual literal value
        // or an enum data cons, or an alias to an enum data cons
        final Object literalValue = functionalAgentMachineFunction.getLiteralValue();
        if (literalValue != null) {
            
            final Class<?> unboxedLiteralType = supportedLiteralTypesBoxedToUnboxedClassMap.get(literalValue.getClass());
            if (unboxedLiteralType == null) {
                throw new IllegalStateException("Unsupported literal type in machine function: " + literalValue.getClass());
            }
            
            isLiteralResult = true;
            
            if (returnType.getExposedTypeName().equals(JavaTypeName.CAL_VALUE)) {
                // an alias of a data constructor, which should be in an enumeration algebraic type
                // so we just marshall the literal as the opaque representation of the enum value
                if (unboxedLiteralType != int.class) {
                    throw new IllegalStateException("Unboxed literal type " + unboxedLiteralType + " is not the primitive type int.");
                }
                
                if (!LECCMachineConfiguration.TREAT_ENUMS_AS_INTS) {
                    throw new IllegalStateException("Enums are not treated as ints, but there is a MachineFunction with a literal value whose return type is CalValue");
                }
                
                resultExpr = makeRTValueMarshallingExpr(ClassTypeInfo.make(unboxedLiteralType), JavaExpression.LiteralWrapper.make(literalValue));
                
            } else {
                // not an aliased data constructor
                if (!returnType.getExposedTypeName().equals(JavaTypeName.make(unboxedLiteralType))) {
                    throw new IllegalStateException("Unboxed literal type " + unboxedLiteralType + " does not match return type " + returnType.getExposedTypeName());
                }

                // emit a literal value directly
                resultExpr = JavaExpression.LiteralWrapper.make(literalValue);
            }
            
        } else if (functionalAgentMachineFunction.isDataConstructor() && isEnumDataConsRepresentedAsPrimitiveInt((DataConstructor)functionalAgent)) {
            // ***Optional optimization***
            // a data constructor in an enumeration algebraic type
            // so we just marshall the literal as the opaque representation of the enum value
            
            if (!returnType.getExposedTypeName().equals(JavaTypeName.CAL_VALUE)) {
                throw new IllegalStateException("The return type " + returnType.getExposedTypeName() + " is not the expected " + JavaTypeName.CAL_VALUE);
            }
            
            final DataConstructor dc = (DataConstructor)functionalAgent;
            isLiteralResult = true;
            resultExpr = makeRTValueMarshallingExpr(ClassTypeInfo.make(int.class), JavaExpression.LiteralWrapper.make(Integer.valueOf(dc.getOrdinal())));
            
        } else {
            isLiteralResult = false;
            resultExpr =
                makeEvaluateInvocationExpr(
                    makeApplyInvocationExprChain(
                        getInstanceOfGeneratedClassJavaExpression(functionalAgentMachineFunction, module, executionContextArg),
                        marshalledArgs),
                    executionContextArg);
        }
        
        ////
        /// Build up the method header, and make the return statement
        //
        
        final int modifier = Modifier.STATIC | mapScope(functionalAgent.getScope());
        final boolean[] finalFlags = new boolean[nMethodArgs];
        Arrays.fill(finalFlags, true);
        final String methodName = sanitizeMethodNameForJava(functionalAgent.getName().getUnqualifiedName(), methodNameMapping);
        
        // Special handling of unit return type in CAL - it gets mapped to a void return type in Java
        final boolean isUnitResultType = functionalAgentType.getResultType().isNonParametricType(CAL_Prelude.TypeConstructors.Unit);
        
        final JavaTypeName methodReturnTypeName;
        if (isUnitResultType) {
            methodReturnTypeName = JavaTypeName.VOID;
        } else {
            methodReturnTypeName = returnType.getExposedTypeName();
        }
        
        final JavaMethod method = new JavaMethod(
            modifier,
            methodReturnTypeName,
            methodArgNames.toArray(new String[nMethodArgs]),
            methodArgTypeNames.toArray(new JavaTypeName[nMethodArgs]),
            finalFlags,
            methodName);
        
        if (isUnitResultType) {
            // Code fragment:
            //
            // {functional agent's instance}
            //     .apply({marshalled arg #1}, ... {marshalled arg #4})
            //       ...
            //     .apply(... {marshalled arg #n})
            //     .evaluate(executionContext);
            method.addStatement(new JavaStatement.ExpressionStatement(resultExpr));
            
        } else if (isLiteralResult) {
            // Code fragment:
            //
            // return {functional agent's literal value};
            method.addStatement(new JavaStatement.ReturnStatement(resultExpr));
            
        } else {
            // Code fragment:
            //
            // return {functional agent's instance}
            //     .apply({marshalled arg #1}, ... {marshalled arg #4})
            //       ...
            //     .apply(... {marshalled arg #n})
            //     .evaluate(executionContext).{unmarshalling method}();
            //
            // OR (if unmarshalling involves casting)
            //
            // return ({result type})(java.lang.Object){functional agent's instance}
            //     .apply({marshalled arg #1}, ... {marshalled arg #4})
            //       ...
            //     .apply(... {marshalled arg #n})
            //     .evaluate(executionContext).getOpaqueValue();
            method.addStatement(new JavaStatement.ReturnStatement(makeRTValueUnmarshallingExpr(returnType, resultExpr)));
        }
        
        // Add the throws declaration
        method.addThrows(JavaTypeName.CAL_EXECUTOR_EXCEPTION);
        
        // Add the Javadoc
        method.setJavaDocComment(
            new JavaDocComment(CALDocToJavaDocUtilities.getJavadocFromCALDocComment(
                functionalAgent.getCALDocComment(), false, functionalAgent, functionalAgentType,
                functionalAgentArgNames.toArray(new String[nFunctionalAgentArgs]),
                scopedEntityNamingPolicy, crossRefGen, isUnitResultType, true, false, true)));
        
        return method;
    }

    /**
     * Returns whether the list of class type info for an argument list contains a non-CalValue type.
     * @param classTypeInfos the list of class type info.
     * @return true if there is at least one non-CalValue type; false otherwise.
     */
    private static boolean hasNonCalValueArgument(final List<ClassTypeInfo> classTypeInfos) {
        for (final ClassTypeInfo classTypeInfo : classTypeInfos) {
            if (!classTypeInfo.getExposedTypeName().equals(JavaTypeName.CAL_VALUE)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Sanitizes the names of functional agents to be suitable for use as Java method names.
     * @param functionalAgents the functional agents.
     * @return a mapping from functional agent names to names of the generated methods.
     */
    private static Map<String, String> sanitizeMethodNamesForJava(final FunctionalAgent[] functionalAgents) {
        final Set<String> origNames = new HashSet<String>();
        for (final FunctionalAgent functionalAgent : functionalAgents) {
            origNames.add(functionalAgent.getName().getUnqualifiedName());
        }
        
        final Map<String, String> result = new HashMap<String, String>();
        for (final String origName : origNames) {
            String newName = origName;
            // turn Java keywords like new into new_ while being careful about name collisions
            while (JavaReservedWords.javaLanguageKeywords.contains(newName)) {
                do {
                    newName += '_';
                } while (origNames.contains(newName) || result.containsValue(newName));
            }
            result.put(origName, newName);
        }
        
        return result;
    }
    
    /**
     * Sanitizes a name to be suitable for use as a Java method name, taking into consideration an existing name mapping.
     * @param name the name to be sanitized.
     * @param methodNameMapping a mapping from functional agent names to names of the generated methods.
     * @return the sanitized name.
     */
    private static String sanitizeMethodNameForJava(final String name, final Map<String, String> methodNameMapping) {
        final String mappedName = methodNameMapping.get(name);
        if (mappedName != null) {
            return mappedName;
        } else {
            return sanitizeVarNameForJava(name);
        }
    }
    
    /**
     * Sanitizes a name to be suitable for use as a Java variable name.
     * @param name the name to be sanitized.
     * @return the sanitized name.
     */
    private static String sanitizeVarNameForJava(final String name) {
        if (JavaReservedWords.javaLanguageKeywords.contains(name)) {
            // turn Java keywords like new into _new (note that _new is not a valid CAL variable name, and thus it is
            // safe to make this substitution without having to consider whether the new name collides with other arg names)
            return "_" + name;
        } else {
            // turn ordinal field names like #13 into _13
            return name.replace('#', '_');
        }
    }

    /**
     * Returns a list of class type info for the arguments of a functional agent, and the class type info for the return value.
     * @param functionalAgentType the type of the functional agent.
     * @return a pair of (a list of class type info for the arguments, the class type info for the return value).
     * @throws UnableToResolveForeignEntityException
     */
    private static Pair<List<ClassTypeInfo>, ClassTypeInfo> getArgTypesAndReturnTypeForLibraryMethod(
        final TypeExpr functionalAgentType) throws UnableToResolveForeignEntityException {
        
        final List<ClassTypeInfo> dataTypes = new ArrayList<ClassTypeInfo>();
        for (final TypeExpr typePiece : functionalAgentType.getTypePieces()) {
            
            if (typePiece instanceof TypeConsApp) {
                final TypeConsApp typeConsApp = (TypeConsApp)typePiece;
                final ForeignTypeInfo foreignTypeInfo = typeConsApp.getForeignTypeInfo();
                
                if (foreignTypeInfo != null && isScopeVisibleAsJavaLibraryAPI(foreignTypeInfo.getImplementationVisibility())) {
                    // we use the implementation type if the type is foreign and the implementation is visible
                    dataTypes.add(ClassTypeInfo.make(foreignTypeInfo.getForeignType()));
                    
                } else if (typeConsApp.isNonParametricType(CAL_Prelude.TypeConstructors.Boolean)) {
                    // Special handling required for Prelude.Boolean - it an algebraic type that has a custom RTData representation
                    dataTypes.add(ClassTypeInfo.make(boolean.class));
                    
                } else {
                    dataTypes.add(ClassTypeInfo.makeNonForeign());
                }
            } else {
                dataTypes.add(ClassTypeInfo.makeNonForeign());
            }
        }
        
        final int returnTypeIndex = dataTypes.size() - 1;
        final ClassTypeInfo returnType = dataTypes.get(returnTypeIndex);
        dataTypes.remove(returnTypeIndex);
        
        return Pair.make(dataTypes, returnType);
    }
    
    /**
     * Constructs a JavaExpression that marshalls a potentially unboxed type into an {@link RTValue}.
     * @param classTypeInfo the class type info for the argument.
     * @param argExpr the argument expression.
     * @return an expression which marshalls the argument.
     */
    private static JavaExpression makeRTValueMarshallingExpr(final ClassTypeInfo classTypeInfo, final JavaExpression argExpr) {
        if (classTypeInfo.getClassType() == null) {
            // a non-foreign type, so cast the CalValue down to an RTValue
            return new JavaExpression.CastExpression(classTypeInfo.getRTValueTypeName(), argExpr);
            
        } else {
            // a foreign type, so run the make method on the appropriate RTData type
            return new JavaExpression.MethodInvocation.Static(
                classTypeInfo.getRTValueTypeName(),
                "make",
                argExpr,
                classTypeInfo.getArgumentTypeOfMarshallingMethod(),
                classTypeInfo.getRTValueTypeName());
        }
    }
    
    /**
     * Constructs a JavaExpression that unmarshalls an {@link RTValue} into a potentially unboxed type.
     * @param classTypeInfo the class type info for the argument.
     * @param argExpr the argument expression.
     * @return an expression which unmarshalls the argument.
     */
    private static JavaExpression makeRTValueUnmarshallingExpr(final ClassTypeInfo classTypeInfo, final JavaExpression argExpr) {
        if (classTypeInfo.getClassType() == null) {
            // a non-foreign type, so just return the RTValue as a CalValue
            return argExpr;
            
        } else if (classTypeInfo.getRTValueTypeName().equals(JavaTypeNames.RTDATA_OPAQUE)) {
            // an unmarshalled opaque value needs a downcast from Object to the expected type
            return new JavaExpression.CastExpression(
                classTypeInfo.getExposedTypeName(),
                new JavaExpression.MethodInvocation.Instance(
                    argExpr,
                    classTypeInfo.getUnmarshallingMethodName(),
                    classTypeInfo.getReturnTypeOfUnmarshallingMethod(),
                    JavaExpression.MethodInvocation.InvocationType.VIRTUAL));
            
        } else {
            // a special RTData type, so just run the unmarshalling method and the returned type should be the expected type
            return new JavaExpression.MethodInvocation.Instance(
                argExpr,
                classTypeInfo.getUnmarshallingMethodName(),
                classTypeInfo.getReturnTypeOfUnmarshallingMethod(),
                JavaExpression.MethodInvocation.InvocationType.VIRTUAL);
        }
    }
    
    /**
     * Returns whether an entity of the given CAL scope should be exposed as part of the library API.
     * @param scope the CAL scope.
     * @return true if the scope is considered visible to the API; false otherwise.
     */
    private static boolean isScopeVisibleAsJavaLibraryAPI(final Scope scope) {
        if (ENABLE_GENERATION_OF_PRIVATE_ENTITIES_FOR_DEBUGGING) {
            return true;
        } else {
            return !scope.isPrivate();
        }
    }
    
    /**
     * Maps a CAL scope to the corresponding Java scope for the purpose of generating API methods.
     * @param scope the CAL scope.
     * @return the corresponding Java scope.
     */
    private static int mapScope(final Scope scope) {
        if (scope.isPublic()) {
            return Modifier.PUBLIC;
        } else if (scope.isProtected()) {
            return Modifier.PROTECTED;
        } else {
            return Modifier.PRIVATE;
        }
    }
    
    /**
     * Constructs a JavaStatement declaring a generated code info local variable for configuration checking.
     * @return the java statement.
     */
    private static JavaStatement.LocalVariableDeclaration makeGeneratedCodeInfoDecl() {
        //
        // Code fragment:
        //
        // StandaloneJarGeneratedCodeInfo generatedCodeInfo = new StandaloneJarGeneratedCodeInfo(
        //     {codeVersion},
        //     {runtimeStats},
        //     {callCounts},
        //     {debugCapable},
        //     {directPrimopCalls},
        //     {ignoringStrictnessAnnotations},
        //     {noninterruptibleRuntime});
        //
        final StandaloneJarGeneratedCodeInfo currentConfigGeneratedCodeInfo = StandaloneJarGeneratedCodeInfo.makeFromCurrentConfiguration();
        final JavaTypeName javaTypeName_StandaloneJarGeneratedCodeInfo = JavaTypeName.make(StandaloneJarGeneratedCodeInfo.class);
        
        final JavaExpression generatedCodeInfoInit =
            new JavaExpression.ClassInstanceCreationExpression(
                javaTypeName_StandaloneJarGeneratedCodeInfo,
                new JavaExpression[] {
                    JavaExpression.LiteralWrapper.make(new Integer(currentConfigGeneratedCodeInfo.getCodeVersion())),
                    JavaExpression.LiteralWrapper.make(Boolean.valueOf(currentConfigGeneratedCodeInfo.generateRuntimeStats())),
                    JavaExpression.LiteralWrapper.make(Boolean.valueOf(currentConfigGeneratedCodeInfo.generateCallCounts())),
                    JavaExpression.LiteralWrapper.make(Boolean.valueOf(currentConfigGeneratedCodeInfo.isDebugCapable())),
                    JavaExpression.LiteralWrapper.make(Boolean.valueOf(currentConfigGeneratedCodeInfo.generateDirectPrimopCalls())),
                    JavaExpression.LiteralWrapper.make(Boolean.valueOf(currentConfigGeneratedCodeInfo.isIgnoringStrictnessAnnotations())),
                    JavaExpression.LiteralWrapper.make(Boolean.valueOf(currentConfigGeneratedCodeInfo.isNonInterruptableRuntime()))
                },
                new JavaTypeName[] {
                    JavaTypeName.INT,
                    JavaTypeName.BOOLEAN,
                    JavaTypeName.BOOLEAN,
                    JavaTypeName.BOOLEAN,
                    JavaTypeName.BOOLEAN,
                    JavaTypeName.BOOLEAN,
                    JavaTypeName.BOOLEAN
                });

        final JavaExpression.LocalVariable generatedCodeInfoLocalVar = new JavaExpression.LocalVariable("generatedCodeInfo", javaTypeName_StandaloneJarGeneratedCodeInfo);
        
        return new JavaStatement.LocalVariableDeclaration(generatedCodeInfoLocalVar, generatedCodeInfoInit, true);
    }

    /**
     * Constructs a JavaExpression for invoking the <code>apply(RTValue)</code> method.
     * @param invocationTarget the target of the invocation.
     * @param argExpr the expression evaluating to the RTValue argument.
     * @return the JavaExpression for invoking the <code>apply</code> method.
     */
    private static JavaExpression makeApplyInvocationExpr(final JavaExpression invocationTarget, final JavaExpression argExpr) {
        return new JavaExpression.MethodInvocation.Instance(
            invocationTarget,
            "apply",
            argExpr,
            JavaTypeNames.RTVALUE,
            JavaTypeNames.RTVALUE,
            JavaExpression.MethodInvocation.InvocationType.VIRTUAL);
    }

    /**
     * Constructs a chain of nested JavaExpression for invoking the maximal-arity variant of the <code>apply</code> method on the list of arguments.
     * @param invocationTarget the target of the invocation.
     * @param argExprs the list of expressions.
     * @return the JavaExpression chain.
     */
    private static JavaExpression makeApplyInvocationExprChain(final JavaExpression invocationTarget, final List<JavaExpression> argExprs) {
        
        JavaExpression applicationChain = invocationTarget;
        List<JavaExpression> remainder = argExprs;
        
        // we will shrink the remainder by a maximum of 4 each iteration
        while (remainder.size() > 0) {
            
            final int remainderSize = remainder.size();
            final int currentFragmentSize = Math.min(remainderSize, 4);
            
            final List<JavaExpression> currentFragment = remainder.subList(0, currentFragmentSize);
            final JavaTypeName[] argTypes = new JavaTypeName[currentFragmentSize];
            Arrays.fill(argTypes, JavaTypeNames.RTVALUE);
            
            applicationChain =
                new JavaExpression.MethodInvocation.Instance(
                    applicationChain,
                    "apply",
                    currentFragment.toArray(new JavaExpression[currentFragmentSize]),
                    argTypes,
                    JavaTypeNames.RTVALUE,
                    JavaExpression.MethodInvocation.InvocationType.VIRTUAL);

            remainder = remainder.subList(currentFragmentSize, remainderSize);
        }
        
        return applicationChain;
    }

    /**
     * Constructs a JavaExpression for invoking the <code>evaluate(RTExecutionContext)</code> method.
     * @param invocationTarget the target of the invocation.
     * @param executionContextExpr the expression representing the execution context in the caller's context.
     * @return the JavaExpression for invoking the <code>evaluate</code> method.
     */
    private static JavaExpression makeEvaluateInvocationExpr(final JavaExpression invocationTarget, final JavaExpression executionContextExpr) {
        return new JavaExpression.MethodInvocation.Instance(
            invocationTarget,
            "evaluate",
            executionContextExpr,
            JavaTypeNames.RTEXECUTION_CONTEXT,
            JavaTypeNames.RTVALUE,
            JavaExpression.MethodInvocation.InvocationType.VIRTUAL);
    }

    /**
     * Create an instance of the generated class corresponding to the entry point supercombinator.
     * @param mf the MachineFunction representing the CAL function for which an instance of the generated class is desired.
     * @param module the module object for the module containing the machine function.
     * @param executionContextExpr the expression representing the execution context in the caller's context.
     * @return the java expression for obtaining the start point instance
     * 
     * @see Executor#getInstanceOfGeneratedClass
     */
    private static JavaExpression getInstanceOfGeneratedClassJavaExpression(final MachineFunction mf, final LECCModule module, final JavaExpression executionContextExpr) {
    
        if (mf == null) {
            throw new NullPointerException("MachineFunction argument cannot be null.");
        }
        
        // The specified target may be an alias of another function, or it may
        // be defined as a literal value
        // (either directly or from following an alias chain). In either case
        // there is no source/class
        // generated. So we need to either return the literal value or follow
        // the alias to a
        // function for which there is a generated source/class file.
        final Object literalValue = mf.getLiteralValue();
        if (literalValue != null) {
            final LiteralWrapper wrappedLiteral = JavaExpression.LiteralWrapper.make(literalValue);
            final Class<?> unboxedLiteralType = supportedLiteralTypesBoxedToUnboxedClassMap.get(wrappedLiteral.getClass());
            
            if (unboxedLiteralType == null) {
                throw new IllegalArgumentException("Unsupported literal type in machine function: " + literalValue.getClass());
            }
            return makeRTValueMarshallingExpr(ClassTypeInfo.make(unboxedLiteralType), wrappedLiteral);
        }
    
        final QualifiedName entryPointSCName;
    
        if (mf.getAliasOf() != null) {
            entryPointSCName = mf.getAliasOf();
        } else {
            entryPointSCName = mf.getQualifiedName();
        }
    
        final LECCModule startModule = (LECCModule)module.findModule(entryPointSCName.getModuleName());
    
        final MachineFunction actualMachineFunction = startModule.getFunction(entryPointSCName);
    
        // Munge the qualified name into a class name.
        // Get local name and capitalised local name
        if (actualMachineFunction.isDataConstructor()) {
    
            // this is a DataConstructor
            final String className = CALToJavaNames.createFullClassNameFromDC(entryPointSCName, startModule);
            
            final DataConstructor dc = startModule.getModuleTypeInfo().getDataConstructor(entryPointSCName.getUnqualifiedName());
    
            if (isEnumDataConsRepresentedAsPrimitiveInt(dc)) {
                // an enum data cons treated as an int - we should just return the boxed ordinal
                return makeRTValueMarshallingExpr(ClassTypeInfo.make(int.class), JavaExpression.LiteralWrapper.make(Integer.valueOf(dc.getOrdinal())));
                
            } else {
                if (className.endsWith("$TagDC")) {
                    if (entryPointSCName.equals(CAL_Prelude.DataConstructors.True)) {
                        return new JavaExpression.JavaField.Static(JavaTypeNames.RTDATA_BOOLEAN, "TRUE", JavaTypeNames.RTDATA_BOOLEAN);

                    } else if (entryPointSCName.equals(CAL_Prelude.DataConstructors.False)) {
                        return new JavaExpression.JavaField.Static(JavaTypeNames.RTDATA_BOOLEAN, "FALSE", JavaTypeNames.RTDATA_BOOLEAN);

                    } else {
                        final String outerClassName = className.substring(0, className.length() - 6);
                        return StandaloneJarBuilder.getTagDCStartPointInstanceJavaExpression(outerClassName, dc.getOrdinal());
                    }
                } else {
                    return getStartPointInstanceJavaExpression(className, startModule, actualMachineFunction, executionContextExpr);
                }
            }
        } else {
            return getStartPointInstanceJavaExpression(
                CALToJavaNames.createFullClassNameFromSC(entryPointSCName, startModule),
                startModule, actualMachineFunction, executionContextExpr);
        }
    }

    /**
     * Returns whether the current machine configuration specifies that given data constructor is represented as an int.
     * @param dataCons the data constructor to check.
     * @return true if the data cons is represented as an int; false otherwise.
     */
    private static boolean isEnumDataConsRepresentedAsPrimitiveInt(final DataConstructor dataCons) {
        return LECCMachineConfiguration.TREAT_ENUMS_AS_INTS && TypeExpr.isEnumType(dataCons.getTypeConstructor());
    }

    /**
     * Returns the java expression for creating an instance through the use of the factory method 'make'.
     * The named class must be derived from RTValue.
     * @param className the name of the class.
     * @param module the module associated with the class.
     * @param machineFunction the machine function associated with the class.
     * @param executionContextExpr the expression representing the execution context in the caller's context.
     * @return the java expression for creating an instance of the class.
     * 
     * @see CALClassLoader#getStartPointInstance
     */
    private static JavaExpression getStartPointInstanceJavaExpression(final String className, final LECCModule module, final MachineFunction machineFunction, final JavaExpression executionContextExpr) {
        
        if (machineFunction == null) {
            throw new NullPointerException ("Invalid MachineFunction in getStartPointInstanceJavaExpression() for " + className + ".");
        }
        
        final JavaTypeName javaTypeName = JavaTypeName.make(className, false);
        
        if (machineFunction.isDataConstructor()) {
            // This is a data constructor.
            // Get the static 'make' method.
            return new JavaExpression.MethodInvocation.Static(
                javaTypeName,
                "make",
                JavaExpression.EMPTY_JAVA_EXPRESSION_ARRAY,
                JavaTypeName.NO_ARGS,
                javaTypeName);
        }
    
        final FunctionGroupInfo fgi = module.getFunctionGroupInfo(machineFunction);
        if (fgi == null) {
            throw new NullPointerException ("Invalid FunctionGroupInfo in getStartPointInstanceJavaExpression() for " + machineFunction.getQualifiedName() + ".");
        }
    
        if (machineFunction.getArity() == 0) {
            // Get the static 'make' method.
            if (fgi.getNCAFs() + fgi.getNZeroArityFunctions() <= 1) {
                return new JavaExpression.MethodInvocation.Static(
                    javaTypeName,
                    "make",
                    executionContextExpr,
                    JavaTypeNames.RTEXECUTION_CONTEXT,
                    JavaTypeNames.RTFUNCTION);
                
            } else {
                final int functionIndex = fgi.getFunctionIndex(machineFunction.getName());
                return new JavaExpression.MethodInvocation.Static(
                    javaTypeName,
                    "make",
                    new JavaExpression[] {JavaExpression.LiteralWrapper.make(new Integer(functionIndex)), executionContextExpr},
                    new JavaTypeName[] {JavaTypeName.INT, JavaTypeNames.RTEXECUTION_CONTEXT},
                    JavaTypeNames.RTFUNCTION);
            }
        }
        
        // Access the static instance field.
        final String instanceFieldName = CALToJavaNames.getInstanceFieldName(machineFunction.getQualifiedName(), module);
        return new JavaExpression.JavaField.Static(javaTypeName, instanceFieldName, javaTypeName);
    }

    /**
     * Returns the java expression for creating an instance through the use of the factory method 'getTagDC'.
     * The named class must be derived from RTValue.
     * @param className the name of the class.
     * @param ordinal the ordinal of the data constructor.
     * @return the java expression for creating an instance of the class.
     * 
     * @see CALClassLoader#getTagDCStartPointInstance
     */
    private static JavaExpression getTagDCStartPointInstanceJavaExpression(final String className, final int ordinal) {
        final JavaTypeName javaTypeName = JavaTypeName.make(className, false);
        final JavaTypeName javaTypeName_TagDC = JavaTypeName.make(className + "$TagDC", false);
        
        return new JavaExpression.MethodInvocation.Static(
            javaTypeName,
            "getTagDC",
            JavaExpression.LiteralWrapper.make(new Integer(ordinal)),
            JavaTypeName.INT,
            javaTypeName_TagDC);
    }
}
