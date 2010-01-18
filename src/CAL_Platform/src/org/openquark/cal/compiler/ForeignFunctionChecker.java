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
 * ForeignFunctionChecker.java
 * Created: July 9, 2002
 * By: Bo Ilic
 */

package org.openquark.cal.compiler;

import java.io.IOException;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.EmptyVisitor;
import org.openquark.cal.internal.compiler.ForeignEntityResolver;
import org.openquark.cal.internal.javamodel.JavaTypeName;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.services.IdentifierUtils;


/**
 * Used for type checking the foreign function declarations within a module.
 * Creation date: (July 9, 2002).
 * @author Bo Ilic
 */
final class ForeignFunctionChecker {
       
    /** 
     * The default name to assign to arguments corresponding to object references.
     * eg. the first arg of a foreign function representing an instance method. 
     */
    private static final String DEFAULT_OBJECT_REF_ARG_NAME = "objectRef";        // "instance" is a keyword in cal.
    
    /** 
     * (String->URL) Cache -- used by getClassReader().
     * Map from fully-qualified class name to the URL calculated to correspond to the class file data for that class.
     * 
     * Note: this is probably ok as a static synchronized map.
     */
    private final Map<String, URL> classNameToURLCacheMap = new HashMap<String, URL>();
    
    private final CALCompiler compiler;
    private final ModuleTypeInfo currentModuleTypeInfo;
    
    /**
     * Constructor ForeignFunctionChecker.
     * @param compiler
     * @param currentModuleTypeInfo
     */
    ForeignFunctionChecker(final CALCompiler compiler, final ModuleTypeInfo currentModuleTypeInfo) {
        if (compiler == null || currentModuleTypeInfo == null) {
            throw new NullPointerException();
        }
        this.compiler = compiler;
        this.currentModuleTypeInfo = currentModuleTypeInfo;
    }
    
    /**
     * Adds the foreign function declarations to the environment.
     * @param functionEnv
     * @param foreignFunctionDefnNodes
     */
    Env calculateForeignFunctionTypes(Env functionEnv, final List<ParseTreeNode> foreignFunctionDefnNodes) throws UnableToResolveForeignEntityException {
           
        for (final ParseTreeNode foreignFunctionNode : foreignFunctionDefnNodes) {
                       
            foreignFunctionNode.verifyType(CALTreeParserTokenTypes.FOREIGN_FUNCTION_DECLARATION);
                
            final Function function = calculateForeignEntity(foreignFunctionNode);
                           
            functionEnv = Env.extend(functionEnv, function);                                                                                                        
        }
       
        return functionEnv;      
    }        
     
    /**
     * Analyzes an individual foreign function declaration.
     * Creation date: (July 9, 2002)
     * @param foreignFunctionDeclarationNode  
     */     
    private Function calculateForeignEntity (final ParseTreeNode foreignFunctionDeclarationNode) throws UnableToResolveForeignEntityException {

        foreignFunctionDeclarationNode.verifyType(CALTreeParserTokenTypes.FOREIGN_FUNCTION_DECLARATION);
                
        final ParseTreeNode optionalCALDocNode = foreignFunctionDeclarationNode.firstChild();
        optionalCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);
        
        final ParseTreeNode externalNameNode = optionalCALDocNode.nextSibling();
        externalNameNode.verifyType(CALTreeParserTokenTypes.STRING_LITERAL);
        final String externalName = StringEncoder.unencodeString(externalNameNode.getText ());
        
        SourceRange errorRange = externalNameNode.getSourceRange();
        final ModuleName currentModuleName = currentModuleTypeInfo.getModuleName();    
        if (errorRange == null) {
            // Default to the current module if no source range available.
            errorRange = new SourceRange(currentModuleName.toSourceText());
        }
                                      
        final ParseTreeNode accessModifierNode = externalNameNode.nextSibling();
        final Scope scope = CALTypeChecker.getScopeModifier(accessModifierNode);                 
        
        final ParseTreeNode typeDeclarationNode = accessModifierNode.nextSibling();                
        final TypeExpr typeExpr = compiler.getTypeChecker().calculateDeclaredType(typeDeclarationNode); 
            
        final Class<?>[] signatureTypes = calculateForeignTypeSignature(typeExpr, errorRange);    
                                                                                                         
        final ParseTreeNode functionNameNode = typeDeclarationNode.firstChild();
        functionNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID);
        
        final String functionName = functionNameNode.getText();                  
                                                                  
        final ForeignFunctionInfo foreignFunctionInfo = makeForeignFunctionInfo (QualifiedName.make(currentModuleName, functionName), externalName, signatureTypes, errorRange);
        
        // Get the argument names from the line number table if any.
        final String[] namedArguments = getArgumentNames(foreignFunctionInfo, typeExpr);
        
        final Function function = Function.makeTopLevelFunction(QualifiedName.make(currentModuleName, functionName), namedArguments, typeExpr, scope);
        
        function.setForeignFunctionInfo(foreignFunctionInfo);
        function.setTypeCheckingDone();                 
                
        return function;
    }
    
    /**
     * For the special JVM operators foreign function declarations (i.e. not corresponding to Java method, field or constructor invocations 
     * such as "lengthArray" etc) it is possible to come up with some reasonable argument names in most cases.
     * @param foreignFunctionInfo
     * @return String[] 
     */
    private String[] getArgumentNamesForNonInvocationJavaKinds(final ForeignFunctionInfo foreignFunctionInfo) throws UnableToResolveForeignEntityException {
        
        //todoBI handle the other non-invocation Java Kinds
        
        final ForeignFunctionInfo.JavaKind javaKind = foreignFunctionInfo.getJavaKind();
        
        if (javaKind == ForeignFunctionInfo.JavaKind.NEW_ARRAY) {
         
            final int nArgs = foreignFunctionInfo.getNArguments();
            if (nArgs == 1) {
                return new String[] {"size"};
            } else {
                final String[] args = new String[nArgs];                              
                for (int i = 0; i < nArgs; ++i) {
                    args[i] = "size" + (i + 1);
                }
                return args;
            }            
        
        } else if (javaKind == ForeignFunctionInfo.JavaKind.LENGTH_ARRAY) {
            
            return new String[] {"array"};
            
        } else if (javaKind == ForeignFunctionInfo.JavaKind.SUBSCRIPT_ARRAY) { 

            final int nArgs = foreignFunctionInfo.getNArguments();
            if (nArgs == 2) {
                return new String[] {"array", "index"};
            } else {
                final String[] args = new String[nArgs];
                args[0] = "array";                
                for (int i = 1; i < nArgs; ++i) {
                    args[i] = "index" + i;
                }
                return args;
            }
            
        } else if (javaKind == ForeignFunctionInfo.JavaKind.UPDATE_ARRAY) {
            
            final int nArgs = foreignFunctionInfo.getNArguments();
            if (nArgs == 3) {
                return new String[] {"array", "index", "newValue"};
            } else {
                final String[] args = new String[nArgs];
                args[0] = "array";
                args[nArgs - 1] = "newValue";
                for (int i = 1; i < nArgs - 1; ++i) {
                    args[i] = "index" + i;
                }
                return args;
            }
        }
        
        return null;
    }
    
    /**
     * Get argument names for a foreign function.
     * <p> 
     * For fields and instance methods, the first name in the returned array will be a generated name based on the name of the entity.
     * <p>
     * For methods and constructors, other arg names are calculated by visiting the bytecode for the foreign class using ASM, 
     *   and using the names from the local variable table of the relevant method if any.
     * If the foreign class does not contain this table (for instance, if it were not compiled with debug attributes), 
     *   the returned array will be empty, except for the first argument for instance methods as mentioned above.
     * <p>
     * @param foreignFunctionInfo
     * @param typeExpr the type of the entity.
     * 
     * @return the arguments calculated for the foreign function object.
     *   null if none could be calculated.
     *   For Functions and Constructors, names will be calculated from the line number table of the relevant class' compiled debug info, if any.
     *   For non-static Fields and Methods, the name of the first argument will be generated based on a default name.
     *   Names can contain nulls for parameters which don't have the relevant debug info (eg. if the line number table is not present).
     */
    private String[] getArgumentNames(final ForeignFunctionInfo foreignFunctionInfo, final TypeExpr typeExpr) throws UnableToResolveForeignEntityException {
        
        if (!foreignFunctionInfo.getJavaKind().isInvocation()) {
            return getArgumentNamesForNonInvocationJavaKinds(foreignFunctionInfo);
        }
        
        final AccessibleObject accessibleObject = ((ForeignFunctionInfo.Invocation)foreignFunctionInfo).getJavaProxy();
        final String entityName = foreignFunctionInfo.getCalName().getUnqualifiedName();
        
        if (accessibleObject instanceof Field) {
            final Field field = (Field)accessibleObject;
            
            // If an instance, the only argument is the object ref.
            // Otherwise, no arguments.
            final boolean isStatic = Modifier.isStatic(field.getModifiers());
            if (isStatic) {
                return null;
            } else {
                final String objectRefName = getObjectRefName(typeExpr);
                return new String[]{objectRefName};
            }
        }
        
        // ao is not a Field, so it should be a Constructor or a Method.
        Class<?> classToRead;
        final String methodName;
        Class<?>[] parameterClasses;
        Class<?> returnClass;
        
        // Actually three possibilities - cannot both be true.
        boolean isStaticMethod;
        final boolean isConstructor;
        
        if (accessibleObject instanceof Constructor) {
            final Constructor<?> constructor = (Constructor<?>)accessibleObject;
            classToRead = constructor.getDeclaringClass();
            methodName = "<init>";
            parameterClasses = constructor.getParameterTypes();
            returnClass = Void.TYPE;
            
            isStaticMethod = false;
            isConstructor = true;
            
        } else {
            // Must be a method.
            final Method method = (Method)accessibleObject;
            classToRead = method.getDeclaringClass();
            methodName = method.getName();
            parameterClasses = method.getParameterTypes();
            returnClass = method.getReturnType();
            
            isStaticMethod = Modifier.isStatic(method.getModifiers());
            isConstructor = false;
        }
        
        
        // Calculate the method descriptor.
        // Note: if we upgrade to Java 5.0, this will need to be updated for formal type parameters (ie. for generics..).
        
        final Type[] parameterTypes = new Type[parameterClasses.length];
        
        for (int i = 0; i < parameterClasses.length; i++) {
            parameterTypes[i] = Type.getType(parameterClasses[i]);
        }
        
        final Type returnType = Type.getType(returnClass);
        
        final String methodDescriptor = Type.getMethodDescriptor(returnType, parameterTypes);

        // Instantiate a class reader to read the bytecode of the class with the relevant method's name.
        ClassReader classReader;
        try {
            classReader = getClassReader(classToRead.getName());
        } catch (final IOException ioe) {
            // Class not found.
            return null;
        }
        
        // Start with an empty array for the arguments.
        // Note that for an instance method, the first arg will be the object reference.
        final String[] argumentNames = new String[parameterTypes.length + ((isStaticMethod || isConstructor) ? 0 : 1)];
        
        // Create a class visitor which will create an appropriate method visitor when it encounters the desired constructor.
        final ClassVisitor classVisitor = new EmptyVisitor() {
            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {

                // Check the method name.
                if (!name.equals(methodName)) {
                    return null;
                }
                
                // Check for the expected method descriptor.
                if (!desc.equals(methodDescriptor)) {
                    return null;
                }
                
                // Found the desired method.
                // Return a new method visitor.
                
                return new EmptyVisitor() {
                    @Override
                    public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
                        
                        // For constructors:
                        //   Objects are created by calling <init> on an uninitialized class instance.
                        //   The first arg is the instance, the rest of the args are the constructor args in order.
                        //   ie. args are: (objectRef, arg0, arg1, ...).
                        //   The object ref isn't an arg to the actual constructor, so shift all args left one.
                        if (isConstructor) {
                            index -= 1;
                            
                            // The object ref.
                            if (index < 0) {
                                return;
                            }
                        }
                        
                        // Check for a method variable. 
                        if (index >= argumentNames.length) {
                            // Must be a local variable, not a method variable.
                            return;
                        }
                        
                        // Set the name in the array.
                        argumentNames[index] = IdentifierUtils.makeIdentifierName(name);
                    }
                };
            }
        };
        
        // Visit the class.
        final int flags = ClassReader.SKIP_FRAMES;  // Don't skip debug info or code.
        classReader.accept(classVisitor, flags);
        
        // For an instance method, if there are var names, the name of var index 0 seems to come out as "this".
        // This doesn't make much sense in the context of entities, so rename to a name based on the type if possible.
        if (!(isStaticMethod || isConstructor)) {
            final String objectRefName = getObjectRefName(typeExpr);
            
            // Check that there isn't already an argument with the name we want to give it..
            if (!entityName.equals(objectRefName) && !Arrays.asList(argumentNames).subList(1, argumentNames.length).contains(objectRefName)) {
                argumentNames[0] = objectRefName;
            
            } else {
                // try again.
                if (!entityName.equals(DEFAULT_OBJECT_REF_ARG_NAME) && !Arrays.asList(argumentNames).subList(1, argumentNames.length).contains(DEFAULT_OBJECT_REF_ARG_NAME)) {
                    argumentNames[0] = DEFAULT_OBJECT_REF_ARG_NAME;
                } else {
                    // Oh well, just leave the argument as "this".
                }
            }
        }
            
        return argumentNames;
    }
    
    /**
     * Get a ClassReader for the indicated class.
     * @param name the fully-qualified name of the class.  eg. "java.lang.String"
     * @return a ClassReader for the class.
     * @throws IOException if a problem occurs during creation of the ClassReader, eg. the class doesn't exist on the classpath.
     */
    private ClassReader getClassReader(final String name) throws IOException {
        
        // Check for an existing entry.
        URL classURL = classNameToURLCacheMap.get(name);
        
        if (classURL == null) {
            // slashify and convert to a file name.  eg. "java.lang.String" -> "/java/lang/String.class"
            final String slashifiedFileName = name.replace('.','/') + ".class";
            
            // Get the url.
            classURL = currentModuleTypeInfo.getModule().getForeignClassLoader().getResource(slashifiedFileName);
            
            if (classURL == null) {
                throw new IOException("Could not find class named " + name + " on the classpath.");
            }
            
            // Add the cache entry.
            classNameToURLCacheMap.put(name, classURL);
        }
        
        return new ClassReader(classURL.openStream());
    }


    /**
     * Get the argument name for an object reference.
     * @param typeExpr the type of the generated foreign function.
     * @return the name for the object reference.
     * This is calculated by getting the first type piece in the type expr, and returning its unqualified name if the 
     * type piece is a type constructor.
     */
    private static String getObjectRefName(final TypeExpr typeExpr) {
        
        final TypeExpr firstTypePiece = typeExpr.getTypePieces()[0];
        if (firstTypePiece instanceof TypeConsApp) {
            return IdentifierUtils.makeIdentifierName(((TypeConsApp)firstTypePiece).getName().getUnqualifiedName(), false);
        }
        
        return DEFAULT_OBJECT_REF_ARG_NAME;
    }
    
    /**
     * Creates the ForeignFunctionInfo for the foreign function, which is the information necessary to call the foreign function
     * at run-time. In the process, it does a bunch of checks to verify that the foreign function does indeed exist.
     * 
     * Creation date: (June 28, 2002) 
     * 
     * @param calName QualifiedName name of the foreign function in CAL
     * @param externalName String string describing the java entity called by the foreign function
     *        e.g. "static method java.lang.Character.isUpperCase"
     * @param signatureTypes Class<?>[] the type signature, mapped to Java types. For example, if
     *        the foreign function has type Int->Boolean this is [Integer.TYPE, Boolean.TYPE]
     * @param errorRange position in the source stream to attribute user errors found during this call
     * @return ForeignFunctionInfo info needed to call the foreign function
     */    
    private ForeignFunctionInfo makeForeignFunctionInfo(
        final QualifiedName calName,
        final String externalName,
        final Class<?>[] signatureTypes,
        final SourceRange errorRange) {
                     
        //parse the externalName to determine what kind of foreign entity this CAL foreign function will map onto.
        
        //valid patterns are:
        //method externalUnqualifiedMethodName
        //static method externalQualifiedMethodName
        //field externalUnqualifiedFieldName
        //static field externalQualifiedFieldName
        //constructor externalQualifiedTypeName?
        //cast
        //instanceof externalQualifiedTypeName
        //null
        //isNull
        //isNotNull
        //class externalQualifiedTypeName
        
        final StringTokenizer tokenizer = new StringTokenizer (externalName, " \t");
        final int nTokens = tokenizer.countTokens();
        
        ForeignFunctionInfo.JavaKind kind = null;
             
        String token;
              
        if (nTokens == 1) {
            
            token = tokenizer.nextToken();
            
            if (token.equals("constructor")) {
                return makeForeignConstructor (calName, null, signatureTypes, errorRange);
                
            } else if (token.equals("cast")) {    
                
                return makeForeignCast(calName, signatureTypes, errorRange);
                
            } else if (token.equals("null")) {
                
                return makeForeignNull(calName, signatureTypes, errorRange);
                
            } else if (token.equals("isNull")) {
                
                return makeForeignNullCheck(calName, signatureTypes, errorRange, true);
               
            } else if (token.equals("isNotNull")) {
                
                return makeForeignNullCheck(calName, signatureTypes, errorRange, false);
                
            } else if (token.equals("newArray")) {
                
                return makeForeignNewArray(calName, signatureTypes, errorRange);
                
            } else if (token.equals("lengthArray")) {
                
                return makeForeignLengthArray(calName, signatureTypes, errorRange);
                
            } else if (token.equals("subscriptArray")) {
                
                return makeForeignSubscriptArray(calName, signatureTypes, errorRange);
                
            } else if (token.equals("updateArray")) {
                
                return makeForeignUpdateArray(calName, signatureTypes, errorRange);
                
            } else {
                compiler.logMessage(new CompilerMessage(errorRange, new MessageKind.Error.InvalidExternalNameStringFormat(externalName)));
            }
            
        } else if (nTokens == 2) {
            
            token = tokenizer.nextToken();
            
            if (token.equals("method")) {
                              
                kind = ForeignFunctionInfo.JavaKind.METHOD;
                
            } else if (token.equals("field")) {
                
                kind = ForeignFunctionInfo.JavaKind.FIELD;           
                
            } else if (token.equals("constructor")) {
                 
                kind = ForeignFunctionInfo.JavaKind.CONSTRUCTOR;
                
            } else if (token.equals("instanceof")) {
                
                kind = ForeignFunctionInfo.JavaKind.INSTANCE_OF;
                
            } else if (token.equals("class")) {
                
                kind = ForeignFunctionInfo.JavaKind.CLASS_LITERAL;
                
            } else {                
                // 'method', 'field', 'constructor', 'instanceof' or 'class' is expected rather than '{token}'.
                compiler.logMessage(new CompilerMessage(errorRange, new MessageKind.Error.ExpectingMethodFieldOrConstructor(token)));
            }                       
                 
        } else if (nTokens == 3) {
            
            token = tokenizer.nextToken();
            
            if (!token.equals("static")) {

                // 'static' is expected rather than '{token}'.
                compiler.logMessage(new CompilerMessage(errorRange, new MessageKind.Error.ExpectingStatic(token)));
            }
            
            token = tokenizer.nextToken ();
            
            if (token.equals("method")) {
                              
                kind = ForeignFunctionInfo.JavaKind.STATIC_METHOD;
                
            } else if (token.equals("field")) {
                
                kind = ForeignFunctionInfo.JavaKind.STATIC_FIELD;
                
            } else {
                
                // 'method' or 'field' is expected rather than '{token}'.
                compiler.logMessage(new CompilerMessage(errorRange, new MessageKind.Error.ExpectingMethodOrField(token)));
            }                          
            
        } else {
            // Invalid external name string format "{externalName}". A valid example: "static method java.lang.isUpperCase".
            compiler.logMessage(new CompilerMessage(errorRange, new MessageKind.Error.InvalidExternalNameStringFormat(externalName)));
        }
        
        final String javaName = tokenizer.nextToken();               
               
        if (kind.isMethod ()) {
            
            return makeForeignMethod (calName, javaName, kind.isStatic(), signatureTypes, errorRange);
            
        } else if (kind.isField ()) {
                              
            return makeForeignField (calName, javaName, kind.isStatic(), signatureTypes, errorRange);
                        
        } else if (kind.isConstructor ()) {
            
            return makeForeignConstructor (calName, javaName, signatureTypes, errorRange);
            
        }  else if (kind.isInstanceOf()) {
            
             return makeForeignInstanceOf (calName, javaName, signatureTypes, errorRange);
             
        } else if (kind.isClassLiteral()) {
            
            return makeForeignClassLiteral(calName, javaName, signatureTypes, errorRange);
        }
        
        throw new IllegalStateException();
                                        
    }
    
    /**
     * Converts an array to a string by applying toString to all the elements. 
     * Handy for error messages.
     * @param array array to convert to a string displaying all elements
     * @return representation of the array as a string
     */
    static String makeClassArrayString (final Class<?>[] array) {
        
        final StringBuilder result = new StringBuilder("[");
        
        for (int i = 0; i < array.length; ++i) {
            
            if (i > 0) {
                result.append(", ");
            }
                       
            result.append(makeJavaSourceClassName(array[i]));
        }
        
        result.append (']');
        
        return result.toString();
    }
    
    /**  
     * A helper function for displaying class names in error messages.   
     * @param type
     * @return friendly Java source name for the type. For example, instead of "[Z" we get "int[]"
     */
    private static String makeJavaSourceClassName(final Class<?> type) {
        return JavaTypeName.getFullJavaSourceName(type);
    }
    
    /**
     * Make a ForeignFunctionInfo object from its description in a CAL foreign function declaration
     * as a Java method or report an error if this can't be done.
     * 
     * Creation date: (June 26, 2002). 
     * @param calName name of the foreign function in CAL.
     * @param javaName name of the Java method e.g. "java.lang.Character.isUpperCase". Unqualified for non-static methods and qualified for static methods.
     * @param isStatic true if the Java method is static.
     * @param signatureTypes declared CAL signature of the method, mapped to Java classes.
     * @param errorRange CAL source position to refer to when reporting compilation errors arising from this call.
     * @return make a ForeignFunctionInfo object from its description in a CAL foreign function declaration
     *      as a Java method or report an error if this can't be done.
     */               
    private ForeignFunctionInfo makeForeignMethod (final QualifiedName calName, final String javaName, final boolean isStatic, final Class<?>[] signatureTypes, final SourceRange errorRange) {
                         
        final int periodPosition = javaName.lastIndexOf('.');      
       
        //the unqualified name of the Java method e.g. "cos"
        final String methodName;
        //the qualified name of the Java class on which the method is invoked
        final String qualifiedClassName;
        //the provider for the class on which the method is invoked
        final ForeignEntityProvider<Class<?>> objectTypeProvider; 
        
        final int signatureLength = signatureTypes.length;                                          
        
        //check that the method exists with the specified arguments
        
        ForeignEntityProvider<Class<?>>[] argumentTypeProviders;
        if (isStatic) {
                        
            if (periodPosition == -1) {                
                //"The Java name {0} must be fully qualified (i.e. include the package name)."          
                compiler.logMessage(new CompilerMessage(
                    errorRange,
                    CompilerMessage.Identifier.makeFunction(calName),
                    new MessageKind.Error.JavaNameMustBeFullyQualified(javaName)));
            }                   
            
            argumentTypeProviders = makeForeignEntityProviderClassArray(signatureLength - 1);
            for (int i = 0, n = signatureLength - 1; i < n; i++) {
                argumentTypeProviders[i] = ForeignEntityProvider.makeStrict(signatureTypes[i]);
            }
            
            qualifiedClassName = javaName.substring(0, periodPosition);
            methodName = javaName.substring(periodPosition + 1);
            objectTypeProvider = ForeignEntityProvider.makeStrict(getObjectType(qualifiedClassName, errorRange, calName));   

        } else {
            
            //must have an argument for the instance object to apply the non-static method on
            if (signatureLength < 2) {
                // Non-static methods must have an object instance and a return type.
                compiler.logMessage(new CompilerMessage(
                    errorRange,
                    CompilerMessage.Identifier.makeFunction(calName),
                    new MessageKind.Error.NonStaticMethodsMustHaveInstanceAndReturnType()));
            }
            
            if (periodPosition != -1) {
                //"The Java name {0} must be unqualified (i.e. omit the package name)."           
                compiler.logMessage(new CompilerMessage(
                    errorRange,
                    CompilerMessage.Identifier.makeFunction(calName),
                    new MessageKind.Error.JavaNameMustBeUnqualified(javaName)));                
            }
            
            final Class<?> instanceType = signatureTypes[0];
            argumentTypeProviders = makeForeignEntityProviderClassArray(signatureLength - 2);                                       
            for (int i = 0, n = signatureLength - 2; i < n; i++) {
                argumentTypeProviders[i] = ForeignEntityProvider.makeStrict(signatureTypes[i + 1]);
            }
                        
            //the method descriptor is unqualified e.g. "method equals"
            qualifiedClassName = instanceType.getName();
            methodName = javaName;
            objectTypeProvider = ForeignEntityProvider.makeStrict(instanceType);                                                        
        }
        
        final ForeignEntityProvider<Class<?>> declaredReturnTypeProvider = ForeignEntityProvider.makeStrict(signatureTypes [signatureLength - 1]);

        return makeForeignFunctionInfoForMethod(calName, javaName, isStatic, errorRange, methodName, qualifiedClassName, objectTypeProvider, argumentTypeProviders, declaredReturnTypeProvider, compiler.getMessageLogger(), false);                            
    }

    /**
     * Make a ForeignFunctionInfo object for a method.
     * @param calName name of the foreign function in CAL.
     * @param javaName name of the Java method e.g. "java.lang.Character.isUpperCase". Unqualified for non-static methods and qualified for static methods.
     * @param isStatic true if the Java method is static.
     * @param errorRange CAL source position to refer to when reporting compilation errors arising from this call.
     * @param methodName the unqualified name of the method.
     * @param qualifiedClassName the qualified name of the class containing the method.
     * @param objectTypeProvider the provider object for the class containing the method.
     * @param argumentTypeProviders an array of provider objects corresponding to the types of the arguments.
     * @param declaredReturnTypeProvider the provider object corresponding to the declared return type of the foreign function.
     * @param msgLogger the message logger for logging error messages.
     * @param isLoading whether this is done as part of loading a serialized ForeignFunctionInfo object.
     * @return a ForeignFunctionInfo object for the named method.
     */
    static ForeignFunctionInfo makeForeignFunctionInfoForMethod(final QualifiedName calName, final String javaName, final boolean isStatic, final SourceRange errorRange, final String methodName, final String qualifiedClassName, final ForeignEntityProvider<Class<?>> objectTypeProvider, final ForeignEntityProvider<Class<?>>[] argumentTypeProviders, final ForeignEntityProvider<Class<?>> declaredReturnTypeProvider, final CompilerMessageLogger msgLogger, final boolean isLoading) {
        
        final String foreignEntityNameDisplayString = qualifiedClassName + "." + methodName;
        
        final ForeignEntityProvider.Resolver<Method> methodResolver = new ForeignEntityProvider.Resolver<Method>(foreignEntityNameDisplayString) {

            @Override
            Method resolve(final ForeignEntityProvider.MessageHandler messageHandler) throws UnableToResolveForeignEntityException {
                
                final Class<?> objectType = objectTypeProvider.get();
                if (objectType == null) {
                    return null;
                }
                
                final Class<?>[] argumentTypes = getArrayOfClassesFromProviders(argumentTypeProviders);
                if (argumentTypes == null) {
                    return null;
                }
                
                final Class<?> declaredReturnType = declaredReturnTypeProvider.get();
                if (declaredReturnType == null) {
                    return null;
                }
                
                final ForeignEntityResolver.ResolutionResult<Method> methodResolution = ForeignEntityResolver.resolveMethod(isStatic, objectType, methodName, argumentTypes, declaredReturnType);
                final ForeignEntityResolver.ResolutionStatus resolutionStatus = methodResolution.getStatus();

                final Method method = methodResolution.getResolvedEntity();

                if (resolutionStatus == ForeignEntityResolver.ResolutionStatus.SUCCESS) {
                    // the resolution was successful, so no need to report errors 

                } else if (resolutionStatus == ForeignEntityResolver.ResolutionStatus.NO_SUCH_ENTITY) {
                    // Could not find the method {javaName} with given argument types {makeArrayString(argumentTypes)}.
                    messageHandler.handleMessage(new CompilerMessage(
                        errorRange,
                        CompilerMessage.Identifier.makeFunction(calName),
                        new MessageKind.Error.CouldNotFindMethodWithGivenArgumentTypes(javaName, makeClassArrayString(argumentTypes))));

                } else if (resolutionStatus == ForeignEntityResolver.ResolutionStatus.SECURITY_VIOLATION) {
                    // Security violation trying to access {javaName}.
                    messageHandler.handleMessage(new CompilerMessage(
                        errorRange,
                        CompilerMessage.Identifier.makeFunction(calName),
                        new MessageKind.Error.SecurityViolationTryingToAccess(javaName)));

                } else if (resolutionStatus == ForeignEntityResolver.ResolutionStatus.DEPENDEE_CLASS_NOT_FOUND) {
                    // The Java class {notFoundClass} was not found.  This class is required by {javaName}.
                    messageHandler.handleMessage(new CompilerMessage(
                        errorRange,
                        CompilerMessage.Identifier.makeFunction(calName),
                        new MessageKind.Error.DependeeJavaClassNotFound(methodResolution.getAssociatedMessage(), javaName)));

                } else if (resolutionStatus == ForeignEntityResolver.ResolutionStatus.CANNOT_LOAD_CLASS) {
                    // The definition of Java class {qualifiedClassName} could not be loaded.
                    messageHandler.handleMessage(new CompilerMessage(
                        errorRange,
                        CompilerMessage.Identifier.makeFunction(calName),
                        new MessageKind.Error.JavaClassDefinitionCouldNotBeLoaded(qualifiedClassName)));

                } else if (resolutionStatus == ForeignEntityResolver.ResolutionStatus.LINKAGE_ERROR) {
                    // The java class {qualifiedClassName} was found, but there were problems with using it.
                    // Class:   {LinkageError.class}
                    // Message: {e.getMessage()}
                    messageHandler.handleMessage(new CompilerMessage(
                        errorRange,
                        CompilerMessage.Identifier.makeFunction(calName),
                        new MessageKind.Error.ProblemsUsingJavaClass(qualifiedClassName, (LinkageError)methodResolution.getThrowable())));

                } else if (resolutionStatus == ForeignEntityResolver.ResolutionStatus.EXPECT_STATIC_FOUND_NON_STATIC) {
                    // Could not find the static method {javaName} with given argument types {makeClassArrayString(argumentTypes)}.
                    messageHandler.handleMessage(new CompilerMessage(
                        errorRange,
                        CompilerMessage.Identifier.makeFunction(calName),
                        new MessageKind.Error.CouldNotFindStaticMethodWithGivenArgumentTypes(javaName, makeClassArrayString(argumentTypes))));

                } else if (resolutionStatus == ForeignEntityResolver.ResolutionStatus.EXPECT_NON_STATIC_FOUND_STATIC) {
                    // Could not find the non-static method {javaName} with given argument types {makeClassArrayString(argumentTypes)}.
                    messageHandler.handleMessage(new CompilerMessage(
                        errorRange,
                        CompilerMessage.Identifier.makeFunction(calName),
                        new MessageKind.Error.CouldNotFindNonStaticMethodWithGivenArgumentTypes(javaName, makeClassArrayString(argumentTypes))));                                                      

                } else if (resolutionStatus == ForeignEntityResolver.ResolutionStatus.ACTUAL_RETURN_TYPE_DOES_NOT_MATCH_DECLARED_RETURN_TYPE) {
                    // Foreign function {javaName} cannot return a value of type {declaredReturnType}.
                    messageHandler.handleMessage(new CompilerMessage(
                        errorRange,
                        CompilerMessage.Identifier.makeFunction(calName),
                        new MessageKind.Error.ForeignFunctionReturnsWrongType(javaName, makeJavaSourceClassName(declaredReturnType))));

                } else {
                    // Some other unexpected status
                    throw new IllegalStateException("Unexpected status: " + resolutionStatus);
                }        

                if (isLoading && resolutionStatus != ForeignEntityResolver.ResolutionStatus.SUCCESS) {
                    //"The underlying Java types involved in the CAL foreign function {0} have changed. Recompilation is needed."
                    messageHandler.handleMessage(new CompilerMessage(
                        errorRange,
                        CompilerMessage.Identifier.makeFunction(calName),
                        new MessageKind.Error.InvalidJavaTypeChangeOnLoading(calName)));
                }

                return method;
            }
        };
        
        final ForeignEntityProvider<Method> methodProvider;
        // If we are compiling, always resolve the method eagerly. If we are loading from serialized form, we defer the decision to the machine configuration.
        if (isLoading) {
            methodProvider = ForeignEntityProvider.make(msgLogger, methodResolver);
        } else {
            methodProvider = ForeignEntityProvider.makeStrict(msgLogger, methodResolver);
        }
        
        if (methodProvider == null) {
            return null;
        }
        
        if (isStatic) {
            return ForeignFunctionInfo.Invocation.makeStaticMethod(calName, objectTypeProvider, methodProvider, declaredReturnTypeProvider);
        } else {
            return ForeignFunctionInfo.Invocation.makeNonStaticMethod(calName, objectTypeProvider, methodProvider, declaredReturnTypeProvider);
        }
    }
    
    /**
     * Make a ForeignFunctionInfo object from its description in a CAL foreign function declaration
     * as a Java field or report an error if this can't be done.
     * 
     * Creation date: (June 26, 2002).
     * @param calName name of the foreign function in CAL.      
     * @param javaName name of the Java field e.g. "java.lang.Math.E". Unqualified for non-static fields and qualified for static fields.
     * @param isStatic true if the Java field is static.
     * @param signatureTypes declared CAL signature of the field, mapped to Java classes.
     * @param errorRange CAL source position to refer to when reporting compilation errors arising from this call.   
     * @return make a ForeignFunctionInfo object from its description in a CAL foreign function declaration
     *     as a Java field or report an error if this can't be done.
     */           
    private ForeignFunctionInfo makeForeignField (final QualifiedName calName, final String javaName, final boolean isStatic, final Class<?>[] signatureTypes, final SourceRange errorRange) {
                         
        final int periodPosition = javaName.lastIndexOf('.'); 
        
        //the unqualified name of the field in Java
        final String fieldName; 
        //the qualified name of the Java class on which this field is invoked
        final String qualifiedClassName;
        //the provider for the class on which the field is invoked
        final ForeignEntityProvider<Class<?>> objectTypeProvider;  
        
        final int signatureLength = signatureTypes.length;                                          
        
        //check that the field exists with the specified arguments
        
        if (isStatic) {
            if (signatureLength != 1) {
                // The static field {0} must have no arguments.
                compiler.logMessage(new CompilerMessage(
                    errorRange,
                    CompilerMessage.Identifier.makeFunction(calName),
                    new MessageKind.Error.StaticFieldMustHaveNoArguments(javaName)));
            }
            
            if (periodPosition == -1) {                
                //"The Java name {0} must be fully qualified (i.e. include the package name)."          
                compiler.logMessage(new CompilerMessage(
                    errorRange,
                    CompilerMessage.Identifier.makeFunction(calName),
                    new MessageKind.Error.JavaNameMustBeFullyQualified(javaName)));
            }                                          
            
            qualifiedClassName = javaName.substring(0, periodPosition);
            fieldName = javaName.substring(periodPosition + 1);
            objectTypeProvider = ForeignEntityProvider.makeStrict(getObjectType(qualifiedClassName, errorRange, calName)); 
            
        } else {
            
            //must have an argument for the instance object to apply the non-static field on
            if (signatureLength != 2) {
                // Non-static fields must have an object instance and a return type.
                compiler.logMessage(new CompilerMessage(
                    errorRange,
                    CompilerMessage.Identifier.makeFunction(calName),
                    new MessageKind.Error.NonStaticFieldsMustHaveInstanceAndReturnType()));
            }
            
            if (periodPosition != -1) {
                //"The Java name {0} must be unqualified (i.e. omit the package name)."           
                compiler.logMessage(new CompilerMessage(
                    errorRange,
                    CompilerMessage.Identifier.makeFunction(calName),
                    new MessageKind.Error.JavaNameMustBeUnqualified(javaName)));                
            }            
            
            final Class<?> instanceType = signatureTypes[0];
                      
            //the field descriptor is unqualified
            qualifiedClassName = instanceType.getName();
            fieldName = javaName;
            objectTypeProvider = ForeignEntityProvider.makeStrict(instanceType);           
        }
        
        final ForeignEntityProvider<Class<?>> declaredReturnTypeProvider = ForeignEntityProvider.makeStrict(signatureTypes [signatureLength - 1]);
        
        return makeForeignFunctionInfoForField(calName, javaName, isStatic, errorRange, fieldName, qualifiedClassName, objectTypeProvider, declaredReturnTypeProvider, compiler.getMessageLogger(), false);                            
    }

    /**
     * Make a ForeignFunctionInfo object for a field.
     * @param calName name of the foreign function in CAL.      
     * @param javaName name of the Java field e.g. "java.lang.Math.E". Unqualified for non-static fields and qualified for static fields.
     * @param isStatic true if the Java field is static.
     * @param errorRange CAL source position to refer to when reporting compilation errors arising from this call.
     * @param fieldName the unqualified name of the field.
     * @param qualifiedClassName the qualified name of the class containing the field.
     * @param objectTypeProvider the provider object for the class containing the field.
     * @param declaredReturnTypeProvider the provider object corresponding to the declared return type of the foreign function.
     * @param msgLogger the message logger for logging error messages.
     * @param isLoading whether this is done as part of loading a serialized ForeignFunctionInfo object.
     * @return a ForeignFunctionInfo object for the named field.
     */
    static ForeignFunctionInfo makeForeignFunctionInfoForField(final QualifiedName calName, final String javaName, final boolean isStatic, final SourceRange errorRange, final String fieldName, final String qualifiedClassName, final ForeignEntityProvider<Class<?>> objectTypeProvider, final ForeignEntityProvider<Class<?>> declaredReturnTypeProvider, final CompilerMessageLogger msgLogger, final boolean isLoading) {

        final String foreignEntityNameDisplayString = qualifiedClassName + "." + fieldName;
        
        final ForeignEntityProvider.Resolver<Field> fieldResolver = new ForeignEntityProvider.Resolver<Field>(foreignEntityNameDisplayString) {

            @Override
            Field resolve(final ForeignEntityProvider.MessageHandler messageHandler) throws UnableToResolveForeignEntityException {

                final Class<?> objectType = objectTypeProvider.get();
                if (objectType == null) {
                    return null;
                }

                final Class<?> declaredReturnType = declaredReturnTypeProvider.get();
                if (declaredReturnType == null) {
                    return null;
                }

                final ForeignEntityResolver.ResolutionResult<Field> fieldResolution = ForeignEntityResolver.resolveField(isStatic, objectType, fieldName, declaredReturnType);
                final ForeignEntityResolver.ResolutionStatus resolutionStatus = fieldResolution.getStatus();

                final Field field = fieldResolution.getResolvedEntity();

                if (resolutionStatus == ForeignEntityResolver.ResolutionStatus.SUCCESS) {
                    // the resolution was successful, so no need to report errors 

                } else if (resolutionStatus == ForeignEntityResolver.ResolutionStatus.NO_SUCH_ENTITY) {
                    // Could not find the field {javaName}.
                    messageHandler.handleMessage(
                        new CompilerMessage(
                            errorRange,
                            CompilerMessage.Identifier.makeFunction(calName),
                            new MessageKind.Error.CouldNotFindField(javaName)));

                } else if (resolutionStatus == ForeignEntityResolver.ResolutionStatus.SECURITY_VIOLATION) {
                    // Security violation trying to access {javaName}.
                    messageHandler.handleMessage(new CompilerMessage(
                        errorRange,
                        CompilerMessage.Identifier.makeFunction(calName),
                        new MessageKind.Error.SecurityViolationTryingToAccess(javaName)));

                } else if (resolutionStatus == ForeignEntityResolver.ResolutionStatus.DEPENDEE_CLASS_NOT_FOUND) {
                    // The Java class {notFoundClass} was not found.  This class is required by {javaName}.
                    messageHandler.handleMessage(new CompilerMessage(
                        errorRange,
                        CompilerMessage.Identifier.makeFunction(calName),
                        new MessageKind.Error.DependeeJavaClassNotFound(fieldResolution.getAssociatedMessage(), javaName)));

                } else if (resolutionStatus == ForeignEntityResolver.ResolutionStatus.CANNOT_LOAD_CLASS) {
                    // The definition of Java class {qualifiedClassName} could not be loaded.
                    messageHandler.handleMessage(new CompilerMessage(
                        errorRange,
                        CompilerMessage.Identifier.makeFunction(calName),
                        new MessageKind.Error.JavaClassDefinitionCouldNotBeLoaded(qualifiedClassName)));

                } else if (resolutionStatus == ForeignEntityResolver.ResolutionStatus.LINKAGE_ERROR) {
                    // The java class {qualifiedClassName} was found, but there were problems with using it.
                    // Class:   {LinkageError.class}
                    // Message: {e.getMessage()}
                    messageHandler.handleMessage(new CompilerMessage(
                        errorRange,
                        CompilerMessage.Identifier.makeFunction(calName),
                        new MessageKind.Error.ProblemsUsingJavaClass(qualifiedClassName, (LinkageError)fieldResolution.getThrowable())));

                } else if (resolutionStatus == ForeignEntityResolver.ResolutionStatus.EXPECT_STATIC_FOUND_NON_STATIC) {
                    // Could not find the static field {javaName}.
                    messageHandler.handleMessage(new CompilerMessage(
                        errorRange,
                        CompilerMessage.Identifier.makeFunction(calName),
                        new MessageKind.Error.CouldNotFindStaticField(javaName)));

                } else if (resolutionStatus == ForeignEntityResolver.ResolutionStatus.EXPECT_NON_STATIC_FOUND_STATIC) {
                    // Could not find the non-static field {javaName}.
                    messageHandler.handleMessage(new CompilerMessage(
                        errorRange,
                        CompilerMessage.Identifier.makeFunction(calName),
                        new MessageKind.Error.CouldNotFindNonStaticField(javaName)));

                } else if (resolutionStatus == ForeignEntityResolver.ResolutionStatus.ACTUAL_RETURN_TYPE_DOES_NOT_MATCH_DECLARED_RETURN_TYPE) {
                    // Field {javaName} cannot return a value of type {declaredReturnType}.
                    messageHandler.handleMessage(new CompilerMessage(
                        errorRange,
                        CompilerMessage.Identifier.makeFunction(calName),
                        new MessageKind.Error.FieldReturnsWrongType(javaName, makeJavaSourceClassName(declaredReturnType))));

                } else {
                    // Some other unexpected status
                    throw new IllegalStateException("Unexpected status: " + resolutionStatus);
                }

                if (isLoading && resolutionStatus != ForeignEntityResolver.ResolutionStatus.SUCCESS) {
                    //"The underlying Java types involved in the CAL foreign function {0} have changed. Recompilation is needed."
                    messageHandler.handleMessage(new CompilerMessage(
                        errorRange,
                        CompilerMessage.Identifier.makeFunction(calName),
                        new MessageKind.Error.InvalidJavaTypeChangeOnLoading(calName)));
                }
                
                return field;
            }
        };
        
        final ForeignEntityProvider<Field> fieldProvider;
        // If we are compiling, always resolve the field eagerly. If we are loading from serialized form, we defer the decision to the machine configuration.
        if (isLoading) {
            fieldProvider = ForeignEntityProvider.make(msgLogger, fieldResolver);
        } else {
            fieldProvider = ForeignEntityProvider.makeStrict(msgLogger, fieldResolver);
        }
        
        if (fieldProvider == null) {
            return null;
        }
        
        if (isStatic) {
            return ForeignFunctionInfo.Invocation.makeStaticField(calName, objectTypeProvider, fieldProvider, declaredReturnTypeProvider);
        } else {
            return ForeignFunctionInfo.Invocation.makeNonStaticField(calName, objectTypeProvider, fieldProvider, declaredReturnTypeProvider);
        }
    }    
    
    /**
     * Make a ForeignFunctionInfo object from its description in a CAL foreign function declaration
     * as a Java constructor or report an error if this can't be done.
     * 
     * Creation date: (June 26, 2002). 
     * @param calName name of the foreign function in CAL.     
     * @param maybeJavaName name of the Java type to construct e.g. "java.lang.StringBuilder". Can be null, in which case the CAL function's return type is used.    
     * @param signatureTypes declared CAL signature of the constructor, mapped to Java classes.
     * @param errorRange CAL source position to refer to when reporting compilation errors arising from this call. 
     * @return make a ForeignFunctionInfo object from its description in a CAL foreign function declaration
     *     as a Java constructor or report an error if this can't be done.
     */       
    private ForeignFunctionInfo makeForeignConstructor(
        final QualifiedName calName,
        final String maybeJavaName,
        final Class<?>[] signatureTypes,
        final SourceRange errorRange) {        
        
        final int signatureLength = signatureTypes.length;  
        final ForeignEntityProvider<Class<?>>[] argumentTypeProviders = makeForeignEntityProviderClassArray(signatureLength - 1);
        for (int i = 0, n = signatureLength - 1; i < n; i++) {
            argumentTypeProviders[i] = ForeignEntityProvider.makeStrict(signatureTypes[i]);
        }
                
        //the Class in which the constructor is defined
        final ForeignEntityProvider<Class<?>> objectTypeProvider;        
        final String javaName;
        final ForeignEntityProvider<Class<?>> declaredReturnTypeProvider;
        
        if (maybeJavaName != null) {
            javaName = maybeJavaName;
            //get the object type from javaName. This might be necessary because the actual CAL function's return type may be a supertype
            //e.g. foreign unsafe import jvm "constructor java.util.ArrayList" :: Int -> JList;
            
            objectTypeProvider = ForeignEntityProvider.makeStrict(getObjectType(javaName, errorRange, calName));
            declaredReturnTypeProvider = ForeignEntityProvider.makeStrict(signatureTypes [signatureLength - 1]);
            
        } else {
            //get the object type from the CAL function's signature.
            //e.g. foreign unsafe import jvm "constructor" :: Int -> JArrayList;
            //the we invoke on java.util.ArrayList
            
            objectTypeProvider = ForeignEntityProvider.makeStrict(signatureTypes [signatureLength - 1]);
            try {
                javaName = makeJavaSourceClassName(objectTypeProvider.get()); //must not be null for error messages
            } catch (final UnableToResolveForeignEntityException e) {
                // this should never happen with a strict ForeignEntityProvider
                final IllegalStateException illegalStateException = new IllegalStateException("An UnableToResolveForeignEntityException should not be thrown by a strict ForeignEntityProvider");
                illegalStateException.initCause(e);
                throw illegalStateException;
            }
            declaredReturnTypeProvider = objectTypeProvider;
        }

        return makeForeignFunctionInfoForConstructor(calName, javaName, errorRange, objectTypeProvider, argumentTypeProviders, declaredReturnTypeProvider, compiler.getMessageLogger(), false);
    }

    /**
     * Make a ForeignFunctionInfo object for a constructor.
     * @param calName name of the foreign function in CAL.
     * @param javaName name of the Java type to construct e.g. "java.lang.StringBuilder". <b>Cannot</b> be null.
     * @param errorRange CAL source position to refer to when reporting compilation errors arising from this call.
     * @param objectTypeProvider the provider object for the class containing the constructor.
     * @param argumentTypeProviders an array of provider objects corresponding to the types of the arguments.
     * @param declaredReturnTypeProvider the provider object corresponding to the declared return type of the foreign function.
     * @param msgLogger the message logger for logging error messages.
     * @param isLoading whether this is done as part of loading a serialized ForeignFunctionInfo object.
     * @return a ForeignFunctionInfo object for the named constructor.
     */
    static ForeignFunctionInfo makeForeignFunctionInfoForConstructor(final QualifiedName calName, final String javaName, final SourceRange errorRange, final ForeignEntityProvider<Class<?>> objectTypeProvider, final ForeignEntityProvider<Class<?>>[] argumentTypeProviders, final ForeignEntityProvider<Class<?>> declaredReturnTypeProvider, final CompilerMessageLogger msgLogger, final boolean isLoading) {

        final ForeignEntityProvider.Resolver<Constructor<?>> constructorResolver = new ForeignEntityProvider.Resolver<Constructor<?>>(javaName) {

            @Override
            Constructor<?> resolve(final ForeignEntityProvider.MessageHandler messageHandler) throws UnableToResolveForeignEntityException {

                final Class<?> objectType = objectTypeProvider.get();
                if (objectType == null) {
                    return null;
                }

                final Class<?>[] argumentTypes = getArrayOfClassesFromProviders(argumentTypeProviders);
                if (argumentTypes == null) {
                    return null;
                }

                final Class<?> declaredReturnType = declaredReturnTypeProvider.get();
                if (declaredReturnType == null) {
                    return null;
                }

                //check that the constructor exists with the specified arguments

                final ForeignEntityResolver.ResolutionResult<Constructor<?>> constructorResolution = ForeignEntityResolver.resolveConstructor(objectType, argumentTypes, declaredReturnType);
                final ForeignEntityResolver.ResolutionStatus resolutionStatus = constructorResolution.getStatus();

                final Constructor<?> constructor = constructorResolution.getResolvedEntity();

                if (resolutionStatus == ForeignEntityResolver.ResolutionStatus.SUCCESS) {
                    // the resolution was successful, so no need to report errors 

                } else if (resolutionStatus == ForeignEntityResolver.ResolutionStatus.NO_SUCH_ENTITY) {
                    // Could not find the constructor {javaName} with the given argument types {makeArrayString(argumentTypes)}."));
                    messageHandler.handleMessage(
                        new CompilerMessage(
                            errorRange,
                            CompilerMessage.Identifier.makeFunction(calName),
                            new MessageKind.Error.CouldNotFindConstructorWithGivenArgumentTypes(javaName, makeClassArrayString(argumentTypes))));

                } else if (resolutionStatus == ForeignEntityResolver.ResolutionStatus.SECURITY_VIOLATION) {
                    // Security violation trying to access {javaName}.
                    messageHandler.handleMessage(new CompilerMessage(
                        errorRange,
                        CompilerMessage.Identifier.makeFunction(calName),
                        new MessageKind.Error.SecurityViolationTryingToAccess(javaName)));

                } else if (resolutionStatus == ForeignEntityResolver.ResolutionStatus.DEPENDEE_CLASS_NOT_FOUND) {
                    // The Java class {notFoundClass} was not found.  This class is required by {javaName}.
                    messageHandler.handleMessage(new CompilerMessage(
                        errorRange,
                        CompilerMessage.Identifier.makeFunction(calName),
                        new MessageKind.Error.DependeeJavaClassNotFound(constructorResolution.getAssociatedMessage(), javaName)));

                } else if (resolutionStatus == ForeignEntityResolver.ResolutionStatus.CANNOT_LOAD_CLASS) {
                    // The definition of Java class {javaName} could not be loaded.
                    messageHandler.handleMessage(new CompilerMessage(
                        errorRange,
                        CompilerMessage.Identifier.makeFunction(calName),
                        new MessageKind.Error.JavaClassDefinitionCouldNotBeLoaded(javaName)));

                } else if (resolutionStatus == ForeignEntityResolver.ResolutionStatus.LINKAGE_ERROR) {
                    // The java class {javaName} was found, but there were problems with using it.
                    // Class:   {LinkageError.class}
                    // Message: {e.getMessage()}
                    messageHandler.handleMessage(new CompilerMessage(
                        errorRange,
                        CompilerMessage.Identifier.makeFunction(calName),
                        new MessageKind.Error.ProblemsUsingJavaClass(javaName, (LinkageError)constructorResolution.getThrowable())));

                } else if (resolutionStatus == ForeignEntityResolver.ResolutionStatus.ACTUAL_RETURN_TYPE_DOES_NOT_MATCH_DECLARED_RETURN_TYPE) {
                    // Constructor {javaName} cannot return a value of type {declaredReturnType}.
                    messageHandler.handleMessage(new CompilerMessage(
                        errorRange,
                        CompilerMessage.Identifier.makeFunction(calName),
                        new MessageKind.Error.ConstructorReturnsWrongType(javaName, makeJavaSourceClassName(declaredReturnType))));

                } else {
                    // Some other unexpected status
                    throw new IllegalStateException("Unexpected status: " + resolutionStatus);
                }

                if (isLoading && resolutionStatus != ForeignEntityResolver.ResolutionStatus.SUCCESS) {
                    //"The underlying Java types involved in the CAL foreign function {0} have changed. Recompilation is needed."
                    messageHandler.handleMessage(new CompilerMessage(
                        errorRange,
                        CompilerMessage.Identifier.makeFunction(calName),
                        new MessageKind.Error.InvalidJavaTypeChangeOnLoading(calName)));
                }
                
                return constructor;
            }
        };

        final ForeignEntityProvider<Constructor<?>> constructorProvider;
        // If we are compiling, always resolve the constructor eagerly. If we are loading from serialized form, we defer the decision to the machine configuration.
        if (isLoading) {
            constructorProvider = ForeignEntityProvider.make(msgLogger, constructorResolver);
        } else {
            constructorProvider = ForeignEntityProvider.makeStrict(msgLogger, constructorResolver);
        }
        
        if (constructorProvider == null) {
            return null;
        }

        return ForeignFunctionInfo.Invocation.makeConstructor(calName, constructorProvider, declaredReturnTypeProvider);
    } 
    
    /**
     * Make a ForeignFunctionInfo object from its description in a CAL foreign function declaration
     * as a Java instanceof or report an error if this can't be done.
     * 
     * @param calName name of the foreign function in CAL.  
     * @param instanceOfTypeName the name of the Java type T in "expr instanceof T".      
     * @param signatureTypes declared CAL signature of the cast, mapped to Java classes.
     * @param errorRange CAL source position to refer to when reporting compilation errors arising from this call. 
     * @return make a ForeignFunctionInfo object from its description in a CAL foreign function declaration
     *     as a Java instanceof or report an error if this can't be done.
     */       
    private ForeignFunctionInfo makeForeignInstanceOf(final QualifiedName calName, final String instanceOfTypeName, final Class<?>[] signatureTypes, final SourceRange errorRange) {
                
        if (signatureTypes.length != 2) {
            //"A foreign function declaration for an ''instanceof'' must have exactly 1 argument(s)."
            compiler.logMessage(new CompilerMessage(errorRange,
                new MessageKind.Error.ForeignFunctionDeclarationMustHaveExactlyNArgs("instanceof", 1))); 
            return null;
        }
                           
        final Class<?> argumentType = signatureTypes[0];
        final Class<?> returnType = signatureTypes[1];
        
        if (returnType != boolean.class) {
            //"A foreign function declaration for an ''instanceof'' must return Cal.Core.Prelude.Boolean (or a foreign type with Java implementation type boolean)."
            compiler.logMessage(new CompilerMessage(errorRange,
                new MessageKind.Error.ForeignFunctionDeclarationMustReturnBoolean("instanceof"))); 
        }
        
        // getObjectType handles array types such as "java.lang.String[]" and "int[][]"
        final Class<?> instanceOfType = getObjectType (instanceOfTypeName, errorRange, calName); 
        
        //can't do an instanceof with primitive types
        if (argumentType.isPrimitive()) {
            //"The implementation type must be a Java reference type and not the Java primitive type ''{0}''."
            compiler.logMessage(new CompilerMessage(errorRange,
                new MessageKind.Error.ForeignFunctionDeclarationReferenceTypeExpected(argumentType))); 
            return null;
        }        
        if (instanceOfType.isPrimitive()) {
            //"The implementation type must be a Java reference type and not the Java primitive type ''{0}''."
            compiler.logMessage(new CompilerMessage(errorRange,
                new MessageKind.Error.ForeignFunctionDeclarationReferenceTypeExpected(instanceOfType))); 
            return null;
        }                               
                        
        final ForeignFunctionInfo.JavaKind kind = checkForeignCast(argumentType, instanceOfType);    
        if (kind == null) {
            //"If ''expr'' has Java type ''{0}'', then ''expr instanceof {1}'' is an invalid Java expression." 
            compiler.logMessage(new CompilerMessage(errorRange,
                new MessageKind.Error.ForeignFunctionDeclarationInvalidInstanceOf(argumentType, instanceOfType))); 
            return null;
        }
        
        return new ForeignFunctionInfo.InstanceOf(calName, ForeignEntityProvider.makeStrict(argumentType), ForeignEntityProvider.makeStrict(instanceOfType));
    } 
    
    /**
     * Creates, after checking, foreign "null" functions such as:
     * 
     * foreign unsafe import jvm "null" nullString :: String;
     */
    private ForeignFunctionInfo makeForeignNull(final QualifiedName calName, final Class<?>[] signatureTypes, final SourceRange errorRange) {

        if (signatureTypes.length != 1) {
            //"A foreign function declaration for ''{0}'' must have exactly {1} argument(s)."
            compiler.logMessage(new CompilerMessage(errorRange,
                new MessageKind.Error.ForeignFunctionDeclarationMustHaveExactlyNArgs("null", 0)));  
            return null;
        }
        
        final Class<?> returnType = signatureTypes[0];
       
        //check that the null is for a reference type, and not a primitive type
        if (returnType.isPrimitive()) {
            //"The implementation type must be a Java reference type and not the Java primitive type ''{0}''."
            compiler.logMessage(new CompilerMessage(errorRange,
                new MessageKind.Error.ForeignFunctionDeclarationReferenceTypeExpected(returnType))); 
            return null;
        }
        
        return new ForeignFunctionInfo.NullLiteral(calName, ForeignEntityProvider.makeStrict(returnType));
    }
    
    /**
     * Creates, after checking, foreign "isNull" and "isNotNull" functions such as:
     * 
     * foreign unsafe import jvm "isNull" isNullString :: String -> Boolean;
     * foreign unsafe import jvm "isNotNull" isNotNullJObject :: JObject -> Boolean;
     */    
    private ForeignFunctionInfo makeForeignNullCheck(final QualifiedName calName, final Class<?>[] signatureTypes, final SourceRange errorRange, final boolean checkIsNull) {

        final String nullCheckOp = checkIsNull ? "isNull" : "isNotNull";
        
        if (signatureTypes.length != 2) {
            //"A foreign function declaration for ''{0}'' must have exactly {1} argument(s)."
            compiler.logMessage(new CompilerMessage(errorRange,
                new MessageKind.Error.ForeignFunctionDeclarationMustHaveExactlyNArgs(nullCheckOp, 1)));  
            return null;
        }

        final Class<?> argumentType = signatureTypes[0];
        final Class<?> returnType = signatureTypes[1];

        //check that the null check is for a reference type, and not a primitive type
        if (argumentType.isPrimitive()) {
            //"The implementation type must be a Java reference type and not the Java primitive type ''{0}''."
            compiler.logMessage(new CompilerMessage(errorRange,
                new MessageKind.Error.ForeignFunctionDeclarationReferenceTypeExpected(argumentType))); 
            return null;
        }
          
        if (returnType != boolean.class) {
            //"A foreign function declaration for ''{0}'' must return Cal.Core.Prelude.Boolean (or a foreign type with Java implementation type boolean)."
            compiler.logMessage(new CompilerMessage(errorRange,
                new MessageKind.Error.ForeignFunctionDeclarationMustReturnBoolean(nullCheckOp))); 
        }                
               
        return new ForeignFunctionInfo.NullCheck(calName, ForeignEntityProvider.makeStrict(argumentType), checkIsNull);
    }
    
    /**
     * Creates, after checking, a foreign class literal function such as:
     * <pre>
     * foreign unsafe import jvm "class void" voidClass :: JClass;
     * foreign unsafe import jvm "class int" intClass :: JClass;
     * foreign unsafe import jvm "class java.util.List" listClass :: JClass;
     * foreign unsafe import jvm "class int[]" intArrayClass :: JClass;
     * foreign unsafe import jvm "class java.lang.String[][]" stringArrayArrayClass :: JClass;
     * </pre>
     * 
     * @param calName the name of the CAL foreign function.
     * @param referentTypeName the referent type, i.e. the Java type R where this literal corresponds to R.class.
     * @param signatureTypes declared CAL signature of the foreign function, mapped to Java classes.
     * @param errorRange CAL source range to refer to when reporting compilation errors arising from this call.
     * @return a ForeignFunctionInfo representing the foreign class literal function.
     */
    private ForeignFunctionInfo makeForeignClassLiteral(final QualifiedName calName, final String referentTypeName, final Class<?>[] signatureTypes, final SourceRange errorRange) {
        
        if (signatureTypes.length != 1) {
            //"A foreign function declaration for ''{0}'' must have exactly {1} argument(s)."
            compiler.logMessage(new CompilerMessage(
                errorRange,
                CompilerMessage.Identifier.makeFunction(calName),
                new MessageKind.Error.ForeignFunctionDeclarationMustHaveExactlyNArgs("class", 0)));  
            return null;
        }
        
        final ForeignEntityProvider<Class<?>> declaredReturnTypeProvider = ForeignEntityProvider.makeStrict(signatureTypes[0]);
        
        // the referent type, i.e. the Java type R where this literal corresponds to R.class.
        final Class<?> referentType;
        
        if (referentTypeName.equals("void")) {
            // the pseudo-type void is explicitly allowed for use with a foreign class literal declaration
            referentType = void.class;
            
        } else {
            // check whether the named type is a primitive type
            final Class<?> primitiveType = ForeignEntityResolver.getPrimitiveType(referentTypeName);
            
            if (primitiveType != null) {
                referentType = primitiveType;
            } else {
                // not a primitive type, so it may be an array type or a reference type
                referentType = getObjectType(referentTypeName, errorRange, calName);
            }
        }
        
        final ForeignEntityProvider<Class<?>> referentTypeProvider = ForeignEntityProvider.makeStrict(referentType);
        
        return makeForeignFunctionInfoForClassLiteral(calName, referentTypeName, errorRange, referentTypeProvider, declaredReturnTypeProvider, compiler.getMessageLogger(), false);
    }

    /**
     * Make a ForeignFunctionInfo object for a class literal.
     * @param calName name of the foreign function in CAL.
     * @param referentTypeName the referent type, i.e. the Java type R where this literal corresponds to R.class.
     * @param errorRange CAL source range to refer to when reporting compilation errors arising from this call.
     * @param referentTypeProvider the provider object corresponding to the referent type.
     * @param declaredReturnTypeProvider the provider object corresponding to the declared return type of the foreign function.
     * @param msgLogger the message logger for logging error messages.
     * @param isLoading whether this is done as part of loading a serialized ForeignFunctionInfo object.
     * @return a ForeignFunctionInfo object for the class literal.
     */
    static ForeignFunctionInfo makeForeignFunctionInfoForClassLiteral(final QualifiedName calName, final String referentTypeName, final SourceRange errorRange, final ForeignEntityProvider<Class<?>> referentTypeProvider, final ForeignEntityProvider<Class<?>> declaredReturnTypeProvider, final CompilerMessageLogger msgLogger, final boolean isLoading) {
        
        final String foreignEntityNameDisplayString = referentTypeName + ".class";

        final ForeignEntityProvider.Resolver<Class<?>> checkedReferentTypeResolver = new ForeignEntityProvider.Resolver<Class<?>>(foreignEntityNameDisplayString) {

            @Override
            Class<?> resolve(final ForeignEntityProvider.MessageHandler messageHandler) throws UnableToResolveForeignEntityException {

                final Class<?> declaredReturnType = declaredReturnTypeProvider.get();

                // The declared return type must be java.lang.Class, or its superclass java.lang.Object, or one of its superinterfaces.
                if (!declaredReturnType.isAssignableFrom(Class.class)) {

                    final String javaName = foreignEntityNameDisplayString;

                    // Foreign function {javaName} cannot return a value of type {declaredReturnType}.
                    messageHandler.handleMessage(new CompilerMessage(
                        errorRange,
                        CompilerMessage.Identifier.makeFunction(calName),
                        new MessageKind.Error.ForeignFunctionReturnsWrongType(javaName, makeJavaSourceClassName(declaredReturnType)))); 
                }

                return referentTypeProvider.get();
            }
        };
        
        final ForeignEntityProvider<Class<?>> checkedReferentTypeProvider;
        // If we are compiling, always check the referent type eagerly. If we are loading from serialized form, we defer the decision to the machine configuration.
        if (isLoading) {
            checkedReferentTypeProvider = ForeignEntityProvider.make(msgLogger, checkedReferentTypeResolver);
        } else {
            checkedReferentTypeProvider = ForeignEntityProvider.makeStrict(msgLogger, checkedReferentTypeResolver);
        }
        
        if (checkedReferentTypeProvider == null) {
            return null;
        }
        
        return new ForeignFunctionInfo.ClassLiteral(calName, referentTypeProvider, declaredReturnTypeProvider);
    }
    
    /** 
     * A helper function to calculate the number of dimensions in array given its Java type   
     * @param c
     * @return 0 if not an array type, otherwise the dimension of the array.
     */
    private static int getArrayDimension(final Class<?> c) {
        int dim = 0;
        
        for (Class<?> elemClass = c.getComponentType(); elemClass != null; elemClass = elemClass.getComponentType()) {
            ++dim;
        }
        
        return dim;
    }
    
    /**
     * For example, if c is the class for int[][][] and nSubscripts == 2, this will return int[].
     * @param c
     * @param nSubscripts
     * @return Class of the subscripted array type or null if can't subscript c to the desired level.
     */
    static Class<?> getSubscriptedArrayType(final Class<?> c, final int nSubscripts) {
        
        Class<?> subscriptedArrayType = c;
        for (int i = 0; i < nSubscripts; i++) {
            subscriptedArrayType = subscriptedArrayType.getComponentType();
            if (subscriptedArrayType == null) {
                return null;
            }
        }
        return subscriptedArrayType;
    }
    
    /**
     * Creates, after checking, foreign "newArray" functions such as:
     * foreign unsafe import jvm "newArray" :: Int -> Int -> JIntIntIntArray;
     * 
     * This creates a foreign type for a 3 dimensional int array, supplying the sizes of the first 2 dimensions
     * 
     */
    private ForeignFunctionInfo makeForeignNewArray(final QualifiedName calName, final Class<?>[] signatureTypes, final SourceRange errorRange) {
          
        final int signatureLength = signatureTypes.length;
                
        //must have at least an array and an argument for the size of its first dimension
        if (signatureLength < 2) {
            //"The type signature is incompatible with the ''{0}'' external descriptor."
            compiler.logMessage(new CompilerMessage(errorRange,
                new MessageKind.Error.ForeignFunctionDeclarationInvalidCalType("newArray")));  
            return null;
        }
        
        //check that the arguments specifying the sizes of dimensions are all ints
        final int nSizeArgs = signatureLength - 1;
        for (int i = 0; i < nSizeArgs; ++i) {
            
            if (signatureTypes[i] != int.class) {
                
                //"The type signature is incompatible with the ''{0}'' external descriptor."
                compiler.logMessage(new CompilerMessage(errorRange,
                    new MessageKind.Error.ForeignFunctionDeclarationInvalidCalType("newArray")));  
                return null;                
            }            
        }

        final Class<?> newArrayType = signatureTypes[signatureLength - 1];
        //can't supply the sizes of more dimensions than the array actually has
        if (nSizeArgs > getArrayDimension(newArrayType)) {
            //"The type signature is incompatible with the ''{0}'' external descriptor."
            compiler.logMessage(new CompilerMessage(errorRange,
                new MessageKind.Error.ForeignFunctionDeclarationInvalidCalType("newArray")));  
            return null;            
        }
                     
               
        return new ForeignFunctionInfo.NewArray(calName, nSizeArgs, ForeignEntityProvider.makeStrict(newArrayType));
    }
    
    /**
     * Creates, after checking, foreign "lengthArray" functions such as:
     * foreign unsafe import jvm "lengthArray" :: JIntArrayInt -> Int;
     * 
     */
    private ForeignFunctionInfo makeForeignLengthArray(final QualifiedName calName, final Class<?>[] signatureTypes, final SourceRange errorRange) {
          
      
        if (signatureTypes.length != 2) {
            //"A foreign function declaration for ''{0}'' must have exactly {1} argument(s)."
            compiler.logMessage(new CompilerMessage(errorRange,
                new MessageKind.Error.ForeignFunctionDeclarationMustHaveExactlyNArgs("lengthArray", 1)));  
            return null;
        }
        
        final Class<?> arrayType = signatureTypes[0];
        final Class<?> returnType = signatureTypes[1];
        
        //must have signature arrayType -> int
        if (!arrayType.isArray() 
            || returnType != int.class) {
            
            //"The type signature is incompatible with the ''{0}'' external descriptor."
            compiler.logMessage(new CompilerMessage(errorRange,
                new MessageKind.Error.ForeignFunctionDeclarationInvalidCalType("newArray")));  
            return null;                 
            
        }
               
        return new ForeignFunctionInfo.LengthArray(calName, ForeignEntityProvider.makeStrict(arrayType));
    } 
    
    /**
     * Creates, after checking, foreign "subscriptArray" functions such as:
     * foreign unsafe import jvm "subscriptArray" :: JInt5Array -> Int -> Int -> JInt3Array;
     * where JInt4Array has Java type int[][][][][]
     * and JInt2Array has Java type int[][][]
     */
    private ForeignFunctionInfo makeForeignSubscriptArray(final QualifiedName calName, final Class<?>[] signatureTypes, final SourceRange errorRange) {
          
        final int signatureLength = signatureTypes.length;
                
        //must have at least an array subscripted by at least 1 dimension, and the result
        if (signatureLength < 3) {
            //"The type signature is incompatible with the ''{0}'' external descriptor."
            compiler.logMessage(new CompilerMessage(errorRange,
                new MessageKind.Error.ForeignFunctionDeclarationInvalidCalType("subscriptArray")));  
            return null;
        }
        
        final int nSubscriptArgs = signatureLength - 2;
                       
        //check that the arguments specifying the subscipting are all ints       
        for (int i = 1; i < signatureLength - 1; ++i) {
            
            if (signatureTypes[i] != int.class) {
                
                //"The type signature is incompatible with the ''{0}'' external descriptor."
                compiler.logMessage(new CompilerMessage(errorRange,
                    new MessageKind.Error.ForeignFunctionDeclarationInvalidCalType("subscriptArray")));  
                return null;                
            }            
        }
        
        final Class<?> arrayType = signatureTypes[0];
        final Class<?> resultType = signatureTypes[signatureLength - 1];  
              
        final Class<?> subscriptedArrayType = getSubscriptedArrayType(arrayType, nSubscriptArgs);
       
        //1) must not subscipt by more dimensions than the array has
        //
        //2) the CAL type of the function need not match perfectly, but must be assignable e.g.
        //foreign unsafe import jvm "subscriptArray" :: JStringArray -> Int -> JObject;
        //is OK. This is consistent with our treatment of foreign functions corresponding to Java methods, fields and constructors.
        if (subscriptedArrayType == null
            || !resultType.isAssignableFrom(subscriptedArrayType)) {
            //"The type signature is incompatible with the ''{0}'' external descriptor."
            compiler.logMessage(new CompilerMessage(errorRange,
                new MessageKind.Error.ForeignFunctionDeclarationInvalidCalType("subscriptArray")));  
            return null;  
        }        
                                    
        return new ForeignFunctionInfo.SubscriptArray(calName, nSubscriptArgs, ForeignEntityProvider.makeStrict(arrayType), ForeignEntityProvider.makeStrict(resultType));
    }
    
    /**
     * Creates, after checking, foreign "updateArray" functions such as:
     * foreign unsafe import jvm "updateArray" :: JInt5Array -> Int -> Int -> JInt3Array -> JInt3Array;
     * where JInt4Array has Java type int[][][][][]
     * and JInt2Array has Java type int[][][]
     */
    private ForeignFunctionInfo makeForeignUpdateArray(final QualifiedName calName, final Class<?>[] signatureTypes, final SourceRange errorRange) {
          
        final int signatureLength = signatureTypes.length;
                
        //must have an array to update, an index, the updated value and a returned value
        if (signatureLength < 4) {
            //"The type signature is incompatible with the ''{0}'' external descriptor."
            compiler.logMessage(new CompilerMessage(errorRange,
                new MessageKind.Error.ForeignFunctionDeclarationInvalidCalType("updateArray")));  
            return null;
        }
        
        final int nSubscriptArgs = signatureLength - 3;
                       
        //check that the arguments specifying the subscipting are all ints       
        for (int i = 1; i < signatureLength - 2; ++i) {
            
            if (signatureTypes[i] != int.class) {
                
                //"The type signature is incompatible with the ''{0}'' external descriptor."
                compiler.logMessage(new CompilerMessage(errorRange,
                    new MessageKind.Error.ForeignFunctionDeclarationInvalidCalType("updateArray")));  
                return null;                
            }            
        }
        
        final Class<?> arrayType = signatureTypes[0];
        final Class<?> elementType = signatureTypes[signatureLength - 2];
        final Class<?> resultType = signatureTypes[signatureLength - 1];
                       
        final Class<?> subscriptedArrayType = getSubscriptedArrayType(arrayType, nSubscriptArgs);
        
        //1) must not subscipt by more dimensions than the array has
        //
        //2) the CAL type of the function need not match perfectly, but must be assignable e.g.
        //foreign unsafe import jvm "subscriptArray" :: JObjectArray -> Int -> String -> ();
        //is OK.
        if (subscriptedArrayType == null
            || !subscriptedArrayType.isAssignableFrom(elementType)) {
            //"The type signature is incompatible with the ''{0}'' external descriptor."
            compiler.logMessage(new CompilerMessage(errorRange,
                new MessageKind.Error.ForeignFunctionDeclarationInvalidCalType("updateArray")));  
            return null;  
        } 
          
        if (resultType != elementType) {
            //"The type signature is incompatible with the ''{0}'' external descriptor."
            compiler.logMessage(new CompilerMessage(errorRange,
                new MessageKind.Error.ForeignFunctionDeclarationInvalidCalType("updateArray")));  
            return null;            
        }
                                    
        return new ForeignFunctionInfo.UpdateArray(calName, nSubscriptArgs, ForeignEntityProvider.makeStrict(arrayType), ForeignEntityProvider.makeStrict(elementType));
    }    
    
    /**
     * Make a ForeignFunctionInfo object from its description in a CAL foreign function declaration
     * as a Java conversion (cast) or report an error if this can't be done.
     * 
     * @param calName name of the foreign function in CAL.        
     * @param signatureTypes declared CAL signature of the cast, mapped to Java classes.
     * @param errorRange CAL source position to refer to when reporting compilation errors arising from this call. 
     * @return make a ForeignFunctionInfo object from its description in a CAL foreign function declaration
     *     as a Java cast or report an error if this can't be done.
     */       
    private ForeignFunctionInfo makeForeignCast(final QualifiedName calName, final Class<?>[] signatureTypes, final SourceRange errorRange) {
                
        if (signatureTypes.length != 2) {
            compiler.logMessage(new CompilerMessage(errorRange,
                new MessageKind.Error.ForeignFunctionDeclarationMustHaveExactlyNArgs("cast", 1)));  
            return null;
        }
                           
        final Class<?> argumentType = signatureTypes[0];
        final Class<?> returnType = signatureTypes[1];
        
        final ForeignFunctionInfo.JavaKind kind = checkForeignCast(argumentType, returnType);    
        if (kind == null) {
            compiler.logMessage(new CompilerMessage(errorRange,
                new MessageKind.Error.ForeignFunctionDeclarationInvalidCast(argumentType, returnType))); 
            return null;
        }
        
        return new ForeignFunctionInfo.Cast(calName, kind, ForeignEntityProvider.makeStrict(argumentType), ForeignEntityProvider.makeStrict(returnType));
    }
    
    /**
     * Helper function to determine if there is a legal cast between 2 Java types, and if so, what kind of
     * cast is it e.g. an identity cast, a widening primitive cast, a narrowing primitive cast, a widening
     * reference cast or a narrowing reference cast. 
     * @param argumentType
     * @param returnType
     * @return the JavaKind of the cast, or null if there is no legal Java cast between these types.
     */
    static ForeignFunctionInfo.JavaKind checkForeignCast(final Class<?> argumentType, final Class<?> returnType) {
        
        if (argumentType == null || returnType == null) {
            throw new NullPointerException();
        }
        
        //classify the kind of cast. Note that some of the tests here are order dependent.
                
        if (argumentType == void.class || returnType == void.class) {
            //void is not a first-class Java type and doesn't even qualify for the identity cast                       
            return null;
            
        } else if (argumentType == returnType) {
            
            return ForeignFunctionInfo.JavaKind.IDENTITY_CAST;
                                     
        } else if (argumentType.isPrimitive()) {
            
            //can't cast:
            //1) from a primitive Java type to a non-primitive Java type
            //2) if any type is void (this is caught above however to exclude treating void -> void as an identity cast)
            //3) from a boolean to any other primitive type
            //4) to a boolean from any other primitive type
            
            if (!returnType.isPrimitive()              
                || (argumentType == boolean.class && returnType != boolean.class)
                || (argumentType != boolean.class && returnType == boolean.class)) {
                               
                return null;
                
            } else if (
                   (argumentType == float.class 
                    && returnType == double.class)
                 
                || (argumentType == long.class
                    && (returnType == float.class || returnType == double.class))
                    
                || (argumentType == int.class
                    && (returnType == long.class || returnType == float.class || returnType == double.class))
                    
                || ((argumentType == char.class || argumentType == short.class)
                    && (returnType == int.class || returnType == long.class || returnType == float.class || returnType == double.class))
                    
                || (argumentType == byte.class 
                    && (returnType == short.class || returnType == int.class || returnType == long.class || returnType == float.class || returnType == double.class))) {
                
                return ForeignFunctionInfo.JavaKind.WIDENING_PRIMITIVE_CAST;
                
            } else {
                
                return ForeignFunctionInfo.JavaKind.NARROWING_PRIMITIVE_CAST;                
            }
                                              
        } else {            
            
            //can't cast:
            //1) from a reference type to a primitive type
            //2) if the cast is neither a widening nor a narrowing reference conversion
            
            if (returnType.isPrimitive()) {                
                return null;
                
            } else if (returnType.isAssignableFrom(argumentType)) {
                
                return ForeignFunctionInfo.JavaKind.WIDENING_REFERENCE_CAST;
                
            } else if (isNarrowingReferenceCast(argumentType, returnType)) {
                
                return ForeignFunctionInfo.JavaKind.NARROWING_REFERENCE_CAST;
                
            } else {
                
                return null;
            }                                              
        }        
    }
    
    /**     
     * This helper method assumes that argumentType cannot be cast to returnType by other means
     * (primitive casts, widening casts, identity cast).
     * 
     * The more complicated tests here are as specified in section 2.6.5 of the JVM specification.
     * Java gives compile time erros for casts that the compiler can guarantee will always cause
     * ClassCastExceptions. This is the cause of the subtlety of some of the conditions below.
     * 
     * @param argumentType
     * @param returnType
     * @return true if argumentType can be cast to returnType via a narrowing reference cast.
     */
    private static boolean isNarrowingReferenceCast(final Class<?> argumentType, final Class<?> returnType) {
                
        if (argumentType.isAssignableFrom(returnType)) {

            //covers the following cases explicitly mentioned in section 2.6.5 of the JVM spec:
            //from any class type S to any class type T, provided that S is a superclass of T
            //from Object to any array type
            //from Object to any interface type
            //from any interface type J to any class type T that is final, provided T implements J

            return true;
            
        } else if (
            (isClassType(argumentType))
            && !Modifier.isFinal(argumentType.getModifiers())
            && returnType.isInterface()) {

            //we should also check that argumentType does not implement returnType, in which case it would be a widening cast
            //but this is caught by an earlier test
            return true;

        } else if (argumentType.isInterface() && isClassType(returnType) && !Modifier.isFinal(returnType.getModifiers())) {

           return true;

        } else if (interfacesWithoutConflictingMethods(argumentType, returnType)) {

           return true;

        } else if (argumentType.isArray() && returnType.isArray()) {
            
            final Class<?> argumentElementType = argumentType.getComponentType();
            final Class<?> returnElementType = returnType.getComponentType();

            if (argumentElementType.isPrimitive() || returnElementType.isPrimitive()) {
                return false;
            }
            
            return isNarrowingReferenceCast (argumentElementType, returnElementType);
            
        } 
        
        return false;        
    }
    
    private static boolean isClassType(final Class<?> c) {
        return !c.isInterface() && !c.isArray() && !c.isPrimitive();
    }
    
    /**    
     * @param class1
     * @param class2
     * @return true if class1 and class2 are both interfaces, and they do not declare a method with the same name
     *     and signature but different return types
     */
    private static boolean interfacesWithoutConflictingMethods(final Class<?> class1, final Class<?> class2) {
        
        //to generalize this method to work for non-interfaces would require more work. This is 
        //because Class.getMethods only returns public methods, which is all that interfaces can have.
        
        if (!class1.isInterface() || !class2.isInterface()) {
            return false;
        }
                 
        final Method[] methods1;
        try {
            methods1 = class1.getMethods();
        } catch (final SecurityException securityException) {
            //todoBI handle this. Should log an error
            throw securityException;
        } catch (final NoClassDefFoundError noClassDefFoundError) {
            //todoBI handle this. Should log an error
            throw noClassDefFoundError;
        } catch (final LinkageError linkageError) {
            //todoBI handle this. Should log an error
            throw linkageError;
        }
                
        for (int i = 0, nMethods1 = methods1.length; i < nMethods1; ++i) {
            final Method method1 = methods1[i];
            final String methodName = method1.getName(); 
            final Class<?>[] paramTypes = method1.getParameterTypes();

            Method method2 = null;
            try {
                method2 = class2.getMethod(methodName, paramTypes);                
            } catch (final NoSuchMethodException noSuchMethodException) {
                //OK- class2 does not have the method
            } catch (final SecurityException securityException) {
                //todoBI handle this. Should log an error
                throw securityException;
            } catch (final NoClassDefFoundError noClassDefFoundError) {
                //todoBI handle this. Should log an error
                throw noClassDefFoundError;
            } catch (final LinkageError linkageError) {
                //todoBI handle this. Should log an error
                throw linkageError;
            }  
            
            if (method2 != null && (method1.getReturnType() != method2.getReturnType())) {
                //the 2 interfaces have a method with the same name and signature but different return types
                return false;                
            }
        }
        
        return true;
    }
       
    /**
     * Helper function to instantiate a Java class object arising from foreign function.
     * The Java class must be public in scope or else an error is logged.
     * 
     * @param qualifiedClassName fully qualified class name e.g. "java.lang.String"
     * @param errorRange line in CAL source to refer to in the event of an error
     * @param calFunctionName the name of the associated CAL function.
     * @return Class
     */   
    private Class<?> getObjectType (final String qualifiedClassName, final SourceRange errorRange, final QualifiedName calFunctionName) {
        
        final ForeignEntityResolver.ResolutionResult<Class<?>> classResolution = ForeignEntityResolver.resolveClass(ForeignEntityResolver.javaSourceReferenceNameToJvmInternalName(qualifiedClassName), currentModuleTypeInfo.getModule().getForeignClassLoader());
        final ForeignEntityResolver.ResolutionStatus resolutionStatus = classResolution.getStatus();
        
        final Class<?> objectType = classResolution.getResolvedEntity();
        
        if (resolutionStatus == ForeignEntityResolver.ResolutionStatus.SUCCESS) {
            // the resolution was sucecssful, so no need to report errors 
            
        } else if (resolutionStatus == ForeignEntityResolver.ResolutionStatus.NO_SUCH_ENTITY) {
            // The Java class {qualifiedClassName} was not found.
            compiler.logMessage(new CompilerMessage(
                errorRange,
                CompilerMessage.Identifier.makeFunction(calFunctionName),
                new MessageKind.Error.JavaClassNotFound(qualifiedClassName)));
            
        } else if (resolutionStatus == ForeignEntityResolver.ResolutionStatus.DEPENDEE_CLASS_NOT_FOUND) {
            // The Java class {notFoundClass} was not found.  This class is required by {qualifiedClassName}.
            compiler.logMessage(new CompilerMessage(
                errorRange,
                CompilerMessage.Identifier.makeFunction(calFunctionName),
                new MessageKind.Error.DependeeJavaClassNotFound(classResolution.getAssociatedMessage(), qualifiedClassName)));
            
        } else if (resolutionStatus == ForeignEntityResolver.ResolutionStatus.CANNOT_LOAD_CLASS) {
            // The definition of Java class {qualifiedClassName} could not be loaded.
            compiler.logMessage(new CompilerMessage(
                errorRange,
                CompilerMessage.Identifier.makeFunction(calFunctionName),
                new MessageKind.Error.JavaClassDefinitionCouldNotBeLoaded(qualifiedClassName)));
            
        } else if (resolutionStatus == ForeignEntityResolver.ResolutionStatus.CANNOT_INITIALIZE_CLASS) {
            // The Java class {qualifiedClassName} could not be initialized.
            compiler.logMessage(new CompilerMessage(
                errorRange,
                CompilerMessage.Identifier.makeFunction(calFunctionName),
                new MessageKind.Error.JavaClassCouldNotBeInitialized(qualifiedClassName)));
            
        } else if (resolutionStatus == ForeignEntityResolver.ResolutionStatus.LINKAGE_ERROR) {
            // The java class {qualifiedClassName} was found, but there were problems with using it.
            // Class:   {LinkageError.class}
            // Message: {e.getMessage()}
            compiler.logMessage(new CompilerMessage(
                errorRange,
                CompilerMessage.Identifier.makeFunction(calFunctionName),
                new MessageKind.Error.ProblemsUsingJavaClass(qualifiedClassName, (LinkageError)classResolution.getThrowable())));

        } else if (resolutionStatus == ForeignEntityResolver.ResolutionStatus.NOT_ACCESSIBLE) {
            //"The Java type ''{0}'' is not accessible. It does not have public scope or is in an unnamed package."
            //the parameter {0} will be replaced by "class java.lang.Foo" or "interface java.lang.Foo" as appropriate.
            compiler.logMessage(
                new CompilerMessage(
                    errorRange,
                    CompilerMessage.Identifier.makeFunction(calFunctionName),
                    new MessageKind.Error.ExternalClassNotAccessible(objectType)));
            
        } else {
            // Some other unexpected status
            throw new IllegalStateException("Unexpected status: " + resolutionStatus);
        }
            
        return objectType;                
    }   

    /**
     * Maps the CAL type signature to Java types, if possible.
     * For example Int -> Char -> JString is mapped to [int.class, char.class, String.class].
     * @param typeExpr typeExpression of the foreign function withing CAL
     * @param errorRange CAL source position to refer to when reporting compilation errors arising from this call. 
     */
    private Class<?>[] calculateForeignTypeSignature(final TypeExpr typeExpr, final SourceRange errorRange) throws UnableToResolveForeignEntityException {

        final TypeExpr[] typePieces = typeExpr.getTypePieces();
        final int nTypePieces = typePieces.length;
        final Class<?>[] signatureTypes = new Class[nTypePieces];

        for (int i = 0; i < nTypePieces; ++i) {
            
            Class<?> javaType = null;

            //the argument and return types of the function must resolve to
            //one of the built-in types that map to the standard Java primitive types.
            //i.e. char, boolean, byte, short, int, long, float, double, void
            //or one of the visible types introduced by foreign data declarations.

            final TypeExpr calType = typePieces[i].prune();            
            if (calType instanceof TypeConsApp) {

                final QualifiedName typeConsName = ((TypeConsApp)calType).getName();
                
                //Prelude.Unit and Prelude.Boolean are special cases in that they are algebraic types that however correspond
                //to foreign types for the purposes of declaring foreign functions.
                if (typeConsName.equals(CAL_Prelude.TypeConstructors.Unit)) {
                    javaType = void.class;                    
                } else if (typeConsName.equals(CAL_Prelude.TypeConstructors.Boolean)) {
                    javaType = boolean.class;                
                } else {
               
                    final TypeConstructor typeCons = currentModuleTypeInfo.getVisibleTypeConstructor(typeConsName);
                    ForeignTypeInfo foreignTypeInfo;
                    if (typeCons != null && (foreignTypeInfo = typeCons.getForeignTypeInfo()) != null) {

                        //check that the implementation of the foreign CAL type as a Java type is visible to the client code
                        final ModuleName currentModuleName = currentModuleTypeInfo.getModuleName();
                        final ModuleName entityModuleName = typeConsName.getModuleName();
                        final Scope implementationVisibility = foreignTypeInfo.getImplementationVisibility();
                        if (currentModuleName.equals(entityModuleName) || 
                            implementationVisibility.isPublic() ||
                            (implementationVisibility.isProtected() && currentModuleTypeInfo.getImportedModule(entityModuleName).hasFriendModule(currentModuleName))) {

                            javaType = foreignTypeInfo.getForeignType();

                        } else {
                            // The implementation of {calType} as a foreign type is not visible within this module.
                            compiler.logMessage(new CompilerMessage(errorRange, new MessageKind.Error.ImplementationAsForeignTypeNotVisible(calType.toString())));
                        }
                        
                    } else {

                        //argument type was not resolved to a built in primitive type with a mapping to Java built-in primitive type.
                        //or to a visible foreign data type with visible implementation.

                        compiler.logMessage(new CompilerMessage(errorRange, new MessageKind.Error.TypeNotSupportedForForeignCalls(calType.toString())));
                    }                
                }
            } else if (calType instanceof RecordType ||
                       calType instanceof TypeVar ||
                       calType instanceof TypeApp) {
                         
                compiler.logMessage(new CompilerMessage(errorRange, new MessageKind.Error.TypeNotSupportedForForeignCalls(calType.toString())));
                
            } else {
                throw new IllegalStateException();
            }

            signatureTypes[i] = javaType;
        }

        return signatureTypes;
    }
    
    /**
     * Obtains the classes provided by an array of class providers, and returns them in an array.
     * @param classProviders an array of class providers.
     * @return the corresponding array of Class objects, or null if any of the providers returned a null value.
     * @throws UnableToResolveForeignEntityException if any of the providers could not resolve a Class object.
     */
    private static Class<?>[] getArrayOfClassesFromProviders(final ForeignEntityProvider<Class<?>>[] classProviders) throws UnableToResolveForeignEntityException {
        final int nProviders = classProviders.length;
        final Class<?>[] classes = new Class[nProviders];
        for (int i = 0; i < nProviders; i++) {
            final Class<?> theClass = classProviders[i].get();
            classes[i] = theClass;
            if (theClass == null) {
                return null;
            }
        }
        return classes;
    }
    
    /**
     * A helper method to create an array with the type {@code ForeignEntityProvider<Class<?>>[]} via an unchecked cast.
     * @param length the length of the array.
     * @return the array.
     */
    @SuppressWarnings("unchecked")
    static ForeignEntityProvider<Class<?>>[] makeForeignEntityProviderClassArray(final int length) {
        return new ForeignEntityProvider[length];
    }
}
