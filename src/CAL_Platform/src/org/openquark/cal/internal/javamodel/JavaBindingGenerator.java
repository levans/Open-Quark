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
 * JavaBindingGenerator.java
 * Creation date: Jul 12, 2006
 * By: RCypher
 */

package org.openquark.cal.internal.javamodel;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.openquark.cal.caldoc.CALDocToJavaDocUtilities;
import org.openquark.cal.compiler.CALDocComment;
import org.openquark.cal.compiler.ClassMethod;
import org.openquark.cal.compiler.DataConstructor;
import org.openquark.cal.compiler.ForeignTypeInfo;
import org.openquark.cal.compiler.Function;
import org.openquark.cal.compiler.FunctionalAgent;
import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.ModuleTypeInfo;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceModel;
import org.openquark.cal.compiler.TypeClass;
import org.openquark.cal.compiler.TypeConsApp;
import org.openquark.cal.compiler.TypeConstructor;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.compiler.UnableToResolveForeignEntityException;
import org.openquark.cal.internal.javamodel.JavaExpression.JavaField;
import org.openquark.cal.internal.javamodel.JavaExpression.LiteralWrapper;
import org.openquark.cal.internal.javamodel.JavaExpression.MethodInvocation;
import org.openquark.cal.internal.javamodel.JavaExpression.MethodVariable;
import org.openquark.cal.internal.javamodel.JavaStatement.JavaDocComment;
import org.openquark.cal.internal.javamodel.JavaStatement.MultiLineComment;
import org.openquark.cal.internal.javamodel.JavaStatement.ReturnStatement;
import org.openquark.cal.machine.Module;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;


/**
 * This class is used to generate the Java source code for a binding class
 * for a CAL module.
 * 
 * This class is the preferred means for client code to refer to CAL entities.
 * For example rather than referring to List.sort by using literal strings the
 * client code would use the field 'QualifiedName sort = new QualifiedName("List", "sort");'
 * If at some point the name of the function is changed in the CAL code the
 * binding class will be regenerated and compilation errors will show anywhere that
 * client code was referring to this function.
 * 
 * A binding class contains a field with the name of the corresponding CAL module.
 * It contains four inner classes: Functions, DataConstructors, TypeConstructors, and TypeClasses.
 * 
 * The inner classes contain a QualifiedName field corresponding to each entity of that type.
 * In addition the Functions and DataConstructors classes contain helper methods which make
 * it easier to generate SourceModel for applications of the function/data constructor.
 *  
 * 
 * @author RCypher
 *
 */
public class JavaBindingGenerator {
    
    static final JavaTypeName[] EMPTY_TYPE_NAME_ARRAY = new JavaTypeName[0];

    /** 
     * The suffix to append to the binding class name when it is the binding class for
     * non public entities.
     */
    private static final String PRIVATE_BINDING_SUFFIX = "_internal";
    
    /**
     * The prefix applied to the name of the generated java binding class.
     */
    private static final String BINDING_CLASS_PREFIX = "CAL_";
    
    /** The name of the inner class for data constructors. */
    private static final String DATA_CONSTRUCTOR_CLASS_NAME = "DataConstructors";
    
    /** The name of the inner class for functions. */
    private static final String FUNCTION_CLASS_NAME = "Functions";
    
    /** The name of the inner class for type classes. */
    private static final String TYPECLASS_CLASS_NAME = "TypeClasses";
    
    /** The name of the inner classes for type constructors (i.e. data types). */
    private static final String TYPECONSTRUCTOR_CLASS_NAME = "TypeConstructors";
    
    /** The name of the field which will hold the module name. */
    private static final String MODULE_NAME_FIELD_NAME = "MODULE_NAME";
    
    /** Constant corresponding to Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL. */
    private static final int PUBLIC_STATIC_FINAL = Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL;
    
    /** The JavaTypeName for QualifiedName. */
    private static final JavaTypeName QUALIFIED_NAME_TYPE_NAME = JavaTypeName.make(QualifiedName.class);
    
    /** The JavaTypeName for SourceModel.Expr. */
    private static final JavaTypeName SOURCE_MODEL_EXPR_TYPE_NAME = JavaTypeName.make(SourceModel.Expr.class);
    
    /** The JavaTypeName for SourceModel.Expr.Var. */
    private static final JavaTypeName SOURCE_MODEL_EXPR_VAR_TYPE_NAME = JavaTypeName.make(SourceModel.Expr.Var.class);
    
    /** The JavaTypeName for sourceModel.Expr.DataCons. */
    private static final JavaTypeName SOURCE_MODEL_EXPR_DATA_CONS_TYPE_NAME = JavaTypeName.make(SourceModel.Expr.DataCons.class);
    
    /** The JavaTypeName for SourceModel.Expr.Application. */
    private static final JavaTypeName SOURCE_MODEL_EXPR_APPLICATION_TYPE_NAME =  JavaTypeName.make(SourceModel.Expr.Application.class);
    
    /** The name of the field used to store the hash value for the concatenated JavaDoc. */
    private static final String JAVADOC_HASH_FIELD_NAME = "javaDocHash";
    
    /** Set this flag to true if debugging output is desired. */
    private static final boolean SHOW_DEBUGGING_OUTPUT = false;
    
    /** The module for which a binding class is being generated. */
    private final Module module;
    
    /** The module type info for the module for which a binding class is being generated. */
    private final ModuleTypeInfo moduleTypeInfo;
    
    /** Flag indicating that the binding class is for public (vs. non public) entities. */
    private final boolean publicEntities;
    
    /** JavaField for referring to the module name field in the generated binding class. */
    private final JavaField moduleNameField;
    
    /** The JavaTypeName for the generated binding class. */
    private final JavaTypeName bindingClassTypeName;
    
    /** The unqualified name of the Java binding class. */
    private final String bindingClassName;

    /** The package in which generated binding classes are placed. */
    private final String bindingClassPackage;

    /**
     * Generate the source code for the Java binding class for the named module. 
     * @param moduleTypeInfo
     * @param targetPackage
     * @param publicEntities
     * @return The source of the generated class.
     */
    public static final String getJavaBinding (ModuleTypeInfo moduleTypeInfo, String targetPackage, boolean publicEntities) throws UnableToResolveForeignEntityException {
        JavaBindingGenerator instance = 
            new JavaBindingGenerator (moduleTypeInfo, targetPackage, publicEntities);
        
        return instance.buildJavaBinding();
    }

    /**
     * For the given module check that a binding class exists and that it
     * is current with the content of the CAL module.  
     * @param moduleTypeInfo
     * @param targetPackage
     * @param publicEntities
     * @param showFailureInfo if true, extra debug info will be output for any failed binding check
     * @return true if the binding class exists and is up-to-date, false otherwise.
     */
    public static final boolean checkJavaBindingClass (ModuleTypeInfo moduleTypeInfo, String targetPackage, boolean publicEntities, boolean showFailureInfo) throws UnableToResolveForeignEntityException {
        boolean showDebugOrFailureInfo = showFailureInfo || SHOW_DEBUGGING_OUTPUT;
        try {
            // Generate the Java model for a Java binding class based on the current CAL
            // module.
            JavaBindingGenerator instance = 
                new JavaBindingGenerator (moduleTypeInfo, targetPackage, publicEntities);

            JavaClassRep bindingClassModel = instance.buildJavaBindingModel();

            // Check to see if the Java binding class already exists.
            Class<?> existingBindingClass = Class.forName(bindingClassModel.getClassName().getFullJavaSourceName(), true, moduleTypeInfo.getModule().getForeignClassLoader());

            if (bindingClassModel.getNInnerClasses() != existingBindingClass.getDeclaredClasses().length) {
                if (showDebugOrFailureInfo) {
                    System.out.println("bindingClassModel.getNInnerClasses() : " + bindingClassModel.getNInnerClasses() + " != existingBindingClass.getDeclaredClasses().length : " + existingBindingClass.getDeclaredClasses().length);
                }
                return false;
            }
            
            // Walk the Java model building up information about the QualifiedName
            // fields in the class.
            JavaBindingClassChecker bindingClassChecker = new JavaBindingClassChecker();
            bindingClassModel.accept(bindingClassChecker, null);
            
            if (!checkInnerBindingClass(
                    existingBindingClass, 
                    FUNCTION_CLASS_NAME, 
                    bindingClassChecker.functionFields,
                    bindingClassChecker.functions,
                    null,
                    showDebugOrFailureInfo)) {
                if (showDebugOrFailureInfo) {
                    System.out.println("Inner function binding class mismatch.");
                }
                return false;
            }

            if (!checkInnerBindingClass(
                    existingBindingClass, 
                    DATA_CONSTRUCTOR_CLASS_NAME, 
                    bindingClassChecker.dataConsFields,
                    bindingClassChecker.dataConsFunctions,
                    bindingClassChecker.dataConsOrdinalFields,
                    showDebugOrFailureInfo)) {
                if (showDebugOrFailureInfo) {
                    System.out.println("Inner data constructor binding class mismatch.");
                }
                return false;
            }

            if (!checkInnerBindingClass(
                    existingBindingClass, 
                    TYPECLASS_CLASS_NAME, 
                    bindingClassChecker.typeClassFields,
                    null,
                    null,
                    showDebugOrFailureInfo)) {
                if (showDebugOrFailureInfo) {
                    System.out.println("Inner type class binding class mismatch.");
                }
                return false;
            }

            if (!checkInnerBindingClass(
                    existingBindingClass, 
                    TYPECONSTRUCTOR_CLASS_NAME, 
                    bindingClassChecker.typeConstructorFields,
                    null,
                    null,
                    showDebugOrFailureInfo)) {
                if (showDebugOrFailureInfo) {
                    System.out.println("Inner type constructor binding class mismatch.");
                }
                return false;
            }
            
            // Check the JavaDoc has value computed for the generated class.
            int newJavaDocHash = 0;
            for (int i = 0, n = bindingClassModel.getNFieldDeclarations(); i < n; ++i) {
                JavaFieldDeclaration jfd = bindingClassModel.getFieldDeclaration(i);
                if (jfd.getFieldName().equals(JavaBindingGenerator.JAVADOC_HASH_FIELD_NAME)) {
                    newJavaDocHash = ((Integer)((LiteralWrapper)jfd.getInitializer()).getLiteralObject()).intValue();
                    break;
                }
            }

            try {
                Field existingJavaDocHashField = 
                    existingBindingClass.getDeclaredField(JavaBindingGenerator.JAVADOC_HASH_FIELD_NAME);
                int existingJavaDocHash = existingJavaDocHashField.getInt(null);
                if (newJavaDocHash != existingJavaDocHash) {
                    if (showDebugOrFailureInfo) {
                        System.out.println("newJavaDocHash : " + newJavaDocHash + " != existingJavaDocHash : " + existingJavaDocHash);
                    }
                    return false;
                }
            } catch (NoSuchFieldException e) {
                if (showDebugOrFailureInfo) {
                    System.out.println("Exception: " + e);
                }
                return false;
            } catch (IllegalAccessException e) {
                if (showDebugOrFailureInfo) {
                    System.out.println("Exception: " + e);
                }
                return false;
            }
            
        } catch (ClassNotFoundException e) {
            if (showDebugOrFailureInfo) {
                System.out.println("Exception: " + e);
            }
            return false;
        }
        
        return true;
    }
    
    /**
     * Compare the existing inner binding class to what it should actually be. 
     * @param bindingClass - the existing binding class.
     * @param innerClassName - the name of the inner class to check.
     * @param expectedQualifiedNameFields - Map of String -> JavaFieldDeclaration. (i.e. field name to field declaration)
     * @param expectedFunctions - Set of JavaMethod. (i.e. method name to method)
     * @param expectedIntFields - Map of String -> JavaFieldDeclaration. (i.e. field name to field declaration)
     * @param showDebugOrFailureInfo if true, extra debug info will be output for any failed binding check
     * @return true if the existing and expected inner classes match.
     */
    private static final boolean checkInnerBindingClass (
            Class<?> bindingClass, 
            String innerClassName,
            Map<String, JavaFieldDeclaration> expectedQualifiedNameFields,
            Set<JavaMethod> expectedFunctions,
            Map<String, JavaFieldDeclaration> expectedIntFields,
            boolean showDebugOrFailureInfo) {

        // Get the inner classes from the existing binding class.
        Class<?> declaredClasses[] = bindingClass.getDeclaredClasses();
        Class<?> innerClass = null;
        for (int i = 0; i < declaredClasses.length; ++i) {
            String dcName = declaredClasses[i].getName();
            dcName = dcName.substring(dcName.lastIndexOf("$") + 1);
            if (dcName.equals(innerClassName)) {
                innerClass = declaredClasses[i];
                break;
            }
        }

        Map<String, Field> declaredQualifiedNameFields = new HashMap<String, Field>();
        SortedSet<String> declaredQualifiedNameFieldsNames = new TreeSet<String>();
        Map<String, Field> declaredIntFields = new HashMap<String, Field>();
        if (innerClass != null) {
            Field declaredFields[] = innerClass.getDeclaredFields();
            for (int i = 0, n = declaredFields.length; i < n; ++i) {
                Field declaredField = declaredFields[i];
                if (declaredField.getType().equals(QualifiedName.class)) {
                    declaredQualifiedNameFields.put(declaredField.getName(), declaredField);
                    declaredQualifiedNameFieldsNames.add(declaredField.getName());
                }
                if (declaredField.getType().equals(int.class)) {
                    declaredIntFields.put(declaredField.getName(), declaredField);
                }
            }
        }
        
        SortedSet<String> expectedQualifiedNameFieldsNames = new TreeSet<String>();
        for (final String expectedQualifiedNameFieldName : expectedQualifiedNameFields.keySet()) {
            expectedQualifiedNameFieldsNames.add(expectedQualifiedNameFieldName);
        }
        
        // Check the qualified name fields.
        if (!expectedQualifiedNameFieldsNames.equals(declaredQualifiedNameFieldsNames)) {
            // Different number of entities in the existing class and
            // the current module.
            if (showDebugOrFailureInfo) {
                System.out.println("expectedQualifiedNameFieldsNames: " + expectedQualifiedNameFieldsNames);
                System.out.println("... does not equal ...");
                System.out.println("declaredQualifiedNameFieldsNames: " + declaredQualifiedNameFieldsNames);
            }
            return false;
        }
        
        // check the ordinal fields.
        if (expectedIntFields == null) {
            expectedIntFields = new HashMap<String, JavaFieldDeclaration>();
        }
        if (expectedIntFields.size() != declaredIntFields.size()) {
            if (showDebugOrFailureInfo) {
                System.out.println("expectedIntFields.size() : " + expectedIntFields.size() + " != declaredIntFields.size() : " + declaredIntFields.size());
            }
            return false;
        }
        // Check the field names and values.
        for (final String fieldName : expectedIntFields.keySet()) {
            JavaFieldDeclaration jfd = expectedIntFields.get(fieldName);
            
            Field existingField = declaredIntFields.get(fieldName);
            if (existingField == null) {
                if (showDebugOrFailureInfo) {
                    System.out.println("Missing field named: " + fieldName);
                }
                return false;
            }
            try {
                int existingValue = existingField.getInt(null);
                
                JavaExpression init = jfd.getInitializer();
                if (!(init instanceof LiteralWrapper)) {
                    if (showDebugOrFailureInfo) {
                        System.out.println("Field " + fieldName + " does not equal " + existingValue);
                    }
                    return false;
                }
                Object value = ((LiteralWrapper)init).getLiteralObject();
                if (!(value instanceof Integer)) {
                    if (showDebugOrFailureInfo) {
                        System.out.println("Field " + fieldName + " does not equal " + existingValue);
                    }
                    return false;
                }
                
                if (existingValue != ((Integer)value).intValue()) {
                    if (showDebugOrFailureInfo) {
                        System.out.println("Field " + fieldName + " does not equal " + existingValue);
                    }
                    return false;
                }
            } catch (IllegalAccessException e) {
                if (showDebugOrFailureInfo) {
                    System.out.println("Exception: " + e);
                }
                return false;
            }
        }
        
        
        if (expectedFunctions == null) {
            expectedFunctions = new HashSet<JavaMethod>();
        }
        
        // Now check the functions.
        int nDeclaredMethods = 0;
        if (innerClass != null) {
            nDeclaredMethods = innerClass.getDeclaredMethods().length;
        }
        
        if (nDeclaredMethods != expectedFunctions.size()) {
            if (showDebugOrFailureInfo) {
                System.out.println("nDeclaredMethods : " + nDeclaredMethods + " != expectedFunctions.size() : " + expectedFunctions.size());
            }
            return false;
        }
        
        // Now we need to confirm that the functions are the same.  
        // i.e. same name and type.
        for (final JavaMethod bindingClassMethod : expectedFunctions) {
            String bindingMethodName = bindingClassMethod.getMethodName();
            int nParams = bindingClassMethod.getNParams();
            Class<?> bindingMethodParamTypes[] = new Class<?>[nParams];
            for (int iParam = 0; iParam < nParams; ++iParam) {
                JavaTypeName paramType = bindingClassMethod.getParamType(iParam);
                if (paramType instanceof JavaTypeName.Primitive) {
                    if (paramType instanceof JavaTypeName.Primitive.Boolean) {
                        bindingMethodParamTypes[iParam] = boolean.class;
                    } else
                    if (paramType instanceof JavaTypeName.Primitive.Byte) {
                        bindingMethodParamTypes[iParam] = byte.class;
                    } else
                    if (paramType instanceof JavaTypeName.Primitive.Char) {
                        bindingMethodParamTypes[iParam] = char.class;
                    } else
                    if (paramType instanceof JavaTypeName.Primitive.Double) {
                        bindingMethodParamTypes[iParam] = double.class;
                    } else
                    if (paramType instanceof JavaTypeName.Primitive.Float) {
                        bindingMethodParamTypes[iParam] = float.class;
                    } else
                    if (paramType instanceof JavaTypeName.Primitive.Int) {
                        bindingMethodParamTypes[iParam] = int.class;
                    } else
                    if (paramType instanceof JavaTypeName.Primitive.Long) {
                        bindingMethodParamTypes[iParam] = long.class;
                    } else 
                    if (paramType instanceof JavaTypeName.Primitive.Short) {
                        bindingMethodParamTypes[iParam] = short.class;
                    } else {
                        if (showDebugOrFailureInfo) {
                            System.out.println("Unrecognized primitive param type: " + paramType);
                        }
                        return false;
                    }
                } else
                if (paramType.equals(JavaBindingGenerator.SOURCE_MODEL_EXPR_TYPE_NAME)) {
                    bindingMethodParamTypes[iParam] = SourceModel.Expr.class;
                } else
                if (paramType.equals(JavaTypeName.STRING)) {
                    bindingMethodParamTypes[iParam] = java.lang.String.class;
                } else {
                    if (showDebugOrFailureInfo) {
                        System.out.println("Unrecognized param type: " + paramType);
                    }
                    return false;
                }
            }
            
            if (innerClass != null) {
                try {
                    Method declaredMethod = innerClass.getDeclaredMethod(bindingMethodName, bindingMethodParamTypes);
                    if (declaredMethod == null) {
                        if (showDebugOrFailureInfo) {
                            System.out.println("Missing method: " + bindingMethodName + ", params: " + Arrays.asList(bindingMethodParamTypes));
                        }
                        return false;
                    }
                } catch (NoSuchMethodException e) {
                    // Mismatch so return false.
                    if (showDebugOrFailureInfo) {
                        System.out.println("Exception: " + e);
                    }
                    return false;
                }
            }
        }
        
        return true;
    }

    /**
     * Returns the full name of the package for the class generated for the given module.
     * For example, if the target package is foo.bar and the module's name is A.B.C, the returned
     * package name would be foo.bar.A.B (into which the class CAL_C would go).
     * 
     * @param targetPackage the name of the target package.
     * @param moduleName the module name.
     * @return the full name of the package for the class generated for the given module.
     */
    public static String getPackageName(String targetPackage, ModuleName moduleName) {
        String basePackage = targetPackage == null ? "" : targetPackage;
        StringBuilder packageName = new StringBuilder(basePackage);
        
        // to the base package we add all the components but the last to the package name
        // the last component is to be transformed to a class name
        for (int i = 0, nMinusOne = moduleName.getNComponents() - 1; i < nMinusOne; i++) {
            if (packageName.length() > 0) {
                packageName.append('.');
            }
            packageName.append(moduleName.getNthComponent(i));
        }
        
        return packageName.toString();
    }
    
    /**
     * Returns the name of the class generated for the given module.
     * @param moduleName the module name.
     * @param publicEntities whether only references to public entities are to be included in the generated class. 
     * @return the name of the class generated for the given module.
     */
    public static final String getClassName (ModuleName moduleName, boolean publicEntities) {
        String unqualifiedModuleName = moduleName.getLastComponent();
        return JavaSourceGenerator.fixupClassName(publicEntities ? (BINDING_CLASS_PREFIX + unqualifiedModuleName) : (BINDING_CLASS_PREFIX + unqualifiedModuleName + PRIVATE_BINDING_SUFFIX));
    }
    
    /**
     * Create an instance of JavaBindingGenerator.
     * @param moduleTypeInfo
     * @param targetPackage
     * @param publicEntities
     */
    private JavaBindingGenerator (ModuleTypeInfo moduleTypeInfo, String targetPackage, boolean publicEntities) {
        this.moduleTypeInfo = moduleTypeInfo;
        this.module = moduleTypeInfo.getModule();
        this.publicEntities = publicEntities;

        // First create the top level class.
        // This will be in the package org.openquark.cal.module.
        // The binding class for public members will have the same name as the module.
        // The binding class for non-public members will be named with the name of the 
        // module with "_internal" appended.
        this.bindingClassName = 
            JavaBindingGenerator.getClassName(moduleTypeInfo.getModuleName(), publicEntities);
        
        this.bindingClassPackage = getPackageName(targetPackage, moduleTypeInfo.getModuleName());
        
        this.bindingClassTypeName =
            JavaTypeName.make(bindingClassPackage + "." + bindingClassName, false);

        this.moduleNameField = new JavaExpression.JavaField.Static (this.bindingClassTypeName, MODULE_NAME_FIELD_NAME, JavaTypeName.MODULE_NAME);
    }
    
    /**
     * Generate the Java model for the Java binding class for the named module. 
     * @return The model of the generated class.
     */
    private final JavaClassRep buildJavaBindingModel () throws UnableToResolveForeignEntityException {
        // We want to generate a Java class that makes it easier to bind
        // client Java code to the Java code generated from the CAL source 
        // in a safe fashion.
        
        // First create the top level class.
        // This will be in the package org.openquark.cal.module.
        // The binding class for public members will have the same name as the module.
        // The binding class for non-public members will be named with the name of the 
        // module with "_internal" appended.
        JavaClassRep bindingClass = 
            new JavaClassRep (bindingClassTypeName, JavaTypeName.OBJECT, Modifier.PUBLIC | Modifier.FINAL, EMPTY_TYPE_NAME_ARRAY);
        
        // We want to insert the CALDoc comment for the module as a comment for the class.
        JavaDocComment jdc;
        CALDocComment moduleComment = moduleTypeInfo.getCALDocComment();
        if (moduleComment != null) {
            String convertedComment = calDocCommentToJavaComment (moduleComment, null, true);
            jdc = new JavaDocComment (convertedComment);
        } else {
            // If there is no module comment from the CAL source we should just
            // create a generic one.
            jdc = new JavaDocComment ("This class (" + bindingClassName + ") provides Java constants, methods, etc.");
            jdc.addLine("which make it easier to bind client Java code to the Java code generated");
            jdc.addLine("from CAL source in a safe fashion");
        }
        
        bindingClass.setJavaDoc(jdc);

        // Add a comment for the top of the source file. 
        MultiLineComment mlc = new MultiLineComment("<!--");
        mlc.addLine(" ");
        mlc.addLine("**************************************************************");
        mlc.addLine("This Java source has been automatically generated.");
        mlc.addLine("MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE");
        mlc.addLine("**************************************************************");
        mlc.addLine(" ");
        mlc.addLine(" ");
        mlc.addLine("This file (" + bindingClass.getClassName().getUnqualifiedJavaSourceName() + ".java)");
        mlc.addLine("was generated from CAL module: " + module.getName() + ".");
        mlc.addLine("The constants and methods provided are intended to facilitate accessing the");
        mlc.addLine(module.getName() + " module from Java code.");
        mlc.addLine(" ");
        mlc.addLine("Creation date: " + new Date());
        mlc.addLine("--!>");
        mlc.addLine(" ");
        bindingClass.setComment(mlc);
        
        // Add the module name field.
        JavaFieldDeclaration moduleNameDeclaration = 
            new JavaFieldDeclaration (PUBLIC_STATIC_FINAL, JavaTypeName.MODULE_NAME, MODULE_NAME_FIELD_NAME,
                new JavaExpression.MethodInvocation.Static(
                    JavaTypeName.MODULE_NAME,
                    "make",
                    new JavaExpression[]{JavaExpression.LiteralWrapper.make(moduleTypeInfo.getModuleName().toSourceText())},
                    new JavaTypeName[]{JavaTypeName.STRING},
                    JavaTypeName.MODULE_NAME));
        
        bindingClass.addFieldDeclaration(moduleNameDeclaration);
        
        // Add the inner class for type classes.
        buildTypeClassInnerClass(bindingClass, moduleTypeInfo, bindingClassName);
        
        // Add the inner class for type constructors. 
        buildTypeConstructorsInnerClass(bindingClass, moduleTypeInfo, bindingClassName);
        
        // Add the inner class for data constructors. 
        buildDataConstructorsInnerClass(bindingClass, moduleTypeInfo, bindingClassName);
        
        // Add the inner class for functions.
        buildFunctionsInnerClass(bindingClass, moduleTypeInfo, bindingClassName);
        
        int javaDocHash = computeJavaDocHash(bindingClass);
        
        JavaFieldDeclaration javaDocHashField = 
            new JavaFieldDeclaration(PUBLIC_STATIC_FINAL, JavaTypeName.INT, JavaBindingGenerator.JAVADOC_HASH_FIELD_NAME, LiteralWrapper.make(Integer.valueOf(javaDocHash)));
        JavaDocComment javaDocHashFieldComment = new JavaDocComment("A hash of the concatenated JavaDoc for this class (including inner classes).");
        javaDocHashFieldComment.addLine("This value is used when checking for changes to generated binding classes.");
        javaDocHashField.setJavaDoc(javaDocHashFieldComment);
        bindingClass.addFieldDeclaration(javaDocHashField);
        
        return bindingClass;
    }
    
    /**
     * Generate the source code for the Java binding class for the named module. 
     * @return The source of the generated class.
     */
    private final String buildJavaBinding () throws UnableToResolveForeignEntityException {
    
        JavaClassRep bindingClass = buildJavaBindingModel();
        
        // Generate the source.
        try {
            String source = JavaSourceGenerator.generateSourceCode(bindingClass);
            return source;
        } catch (JavaGenerationException e) {
            throw new NullPointerException(e.getLocalizedMessage());
        }
    }

    /**
     * Generate the inner class for constants and helper methods relating to CAL functions.
     * @param bindingClass
     * @param moduleTypeInfo
     * @param bindingClassName
     */
    private final void buildFunctionsInnerClass (
            JavaClassRep bindingClass,
            ModuleTypeInfo moduleTypeInfo, 
            String bindingClassName) throws UnableToResolveForeignEntityException {
        
        if (moduleTypeInfo.getNFunctions() + moduleTypeInfo.getNTypeClasses() == 0) {
            return;
        }
        
        // Create the Functions class.
        JavaTypeName functionsClassTypeName = JavaTypeName.make (bindingClassPackage + "." + bindingClassName + "." + FUNCTION_CLASS_NAME, false);
        JavaClassRep functionsClass = 
            new JavaClassRep (
                    functionsClassTypeName, 
                    JavaTypeName.OBJECT, 
                    PUBLIC_STATIC_FINAL,
                    EMPTY_TYPE_NAME_ARRAY);

        // Add the class JavaDoc
        JavaDocComment jdc = new JavaDocComment("This inner class (" + FUNCTION_CLASS_NAME + ") contains constants");
        jdc.addLine("and methods related to binding to CAL functions in the " + moduleTypeInfo.getModuleName() + " module.");
        functionsClass.setJavaDoc(jdc);
        
        // Build up and sort the list of function names.
        Map<String, FunctionalAgent> nameToFuncInfo = new TreeMap<String, FunctionalAgent>();
        for (int i = 0, n = moduleTypeInfo.getNFunctions(); i < n; ++i) {
            Function fe = moduleTypeInfo.getNthFunction(i);
            if (fe.getScope().isPublic() != publicEntities) {
                continue;
            }
            nameToFuncInfo.put(fe.getName().getUnqualifiedName(), fe);
        }
        
        for (int i = 0, n = moduleTypeInfo.getNTypeClasses(); i < n; ++i) {
            TypeClass tc = moduleTypeInfo.getNthTypeClass(i);

            
            for (int j = 0, k = tc.getNClassMethods(); j < k; ++j) {
                ClassMethod cm = tc.getNthClassMethod(j);
                if (cm.getScope().isPublic() == publicEntities) {
                    nameToFuncInfo.put(cm.getName().getUnqualifiedName(), cm);
                }
            }
        }

        if (nameToFuncInfo.size() == 0) {
            return;
        }
        
        bindingClass.addInnerClass(functionsClass);
        
        
        //  Now go over the function names and generate the helper functions and
        // QualifiedName constant.
        for (final Map.Entry<String, FunctionalAgent> entry : nameToFuncInfo.entrySet()) {
           
            String calFuncName = entry.getKey();
            FunctionalAgent functionalAgent = entry.getValue();

            CALDocComment cdc = functionalAgent.getCALDocComment();

            // We need to make sure the name for the helper function/qualified name field is
            // a valid java name.
            String javaFuncName = fixupVarName(calFuncName);
            
            // Add a binding method for the CAL function.
            // Essentially this method builds an application 
            // of the function to the supplied arguments using our
            // source model.
            
            // Build up the argument names and types.
            TypeExpr te = functionalAgent.getTypeExpr();
            int nArgs = te.getArity();
            int nNamedArgs = functionalAgent.getNArgumentNames();
            
            String paramNames[] = CALDocToJavaDocUtilities.getArgumentNamesFromCALDocComment(cdc, functionalAgent);
            if (paramNames.length != nArgs)  {
                throw new NullPointerException ("Mismatch between arity and number of arguments for " + calFuncName);
            }
            String origParamNames[] = new String[paramNames.length]; 
            System.arraycopy(paramNames, 0, origParamNames, 0, origParamNames.length);
            
            JavaTypeName paramTypes[] = new JavaTypeName [paramNames.length];
            
            // If we have named arguments we should use those names.
            Set<String> paramNamesSet = new HashSet<String>();
            for (int i = 0; i < paramNames.length; ++i) {
                String name = paramNames[i];

                if (name == null) {
                    if (i < nNamedArgs) {
                        name = functionalAgent.getArgumentName(i);
                    }
                    
                    if (name == null || name.equals("")) {
                        name = null;
                        // There is no name for this argument in the functional entity.  This
                        // usually occurs with foreign functions and class methods because 
                        // you don't have to explicitly name the arguments.
                        
                        // Check to see if there was a CALDoc comment that named the arguments.
                        if (cdc != null && cdc.getNArgBlocks() == paramNames.length) {
                            name = cdc.getNthArgBlock(i).getArgName().getCalSourceForm();
                        }
                        
                        // If we still don't have an argument name just build one.
                        if (name == null) {
                            name = "arg_" + (i+1);
                        }
                    }
                    
                    // Make sure name is unique.
                    String baseName = name;
                    int suffix = 0;
                    while (paramNamesSet.contains(name)) {
                        name = baseName + "_" + suffix++;
                    }
                }
                
                origParamNames[i] = name;
                paramNames[i] = fixupVarName(name);
                paramTypes[i] = SOURCE_MODEL_EXPR_TYPE_NAME;
            }
            
            // Get the JavaDoc for the helper function.  If there is CALDoc
            // for the function we translate it to JavaDoc.  
            JavaDocComment funcComment;
            if (cdc != null) {
                funcComment = new JavaDocComment(calDocCommentToJavaComment(cdc, functionalAgent, false));
                funcComment = fixupJavaDoc(funcComment, origParamNames, paramNames);
            } else {
                funcComment = new JavaDocComment("Helper binding method for function: " + calFuncName + ". ");
                for (int iName = 0; iName < paramNames.length; ++iName) {
                    funcComment.addLine("@param " + paramNames[iName]);
                }
                funcComment.addLine("@return the SourceModule.expr representing an application of " + calFuncName);
            }
            
            
            // Create the method.
            JavaMethod bindingMethod =
                new JavaMethod(PUBLIC_STATIC_FINAL,
                               SOURCE_MODEL_EXPR_TYPE_NAME,
                               paramNames,
                               paramTypes,
                               null, javaFuncName);
            functionsClass.addMethod(bindingMethod);
            
            // Add the JavaDoc
            bindingMethod.setJavaDocComment(funcComment);
            
            // Build up the body of the method.
            
            // We want to us the QualifiedName field.
            JavaField nameField = new JavaField.Static(functionsClassTypeName, javaFuncName, QUALIFIED_NAME_TYPE_NAME);
            
            // Create an instance of SourceModel.Expr.Var for the CAL function.
            JavaExpression sourceModelVarCreation =
                new MethodInvocation.Static(
                        SOURCE_MODEL_EXPR_VAR_TYPE_NAME, 
                        "make", 
                        nameField,
                        QUALIFIED_NAME_TYPE_NAME, 
                        SOURCE_MODEL_EXPR_VAR_TYPE_NAME);
            
            // For the comment for this method we want an @see referring to the previous method.
            String atSee = "@see #" + javaFuncName + "(";
            for (int iArg = 0; iArg < paramNames.length; ++iArg) {
                if (iArg == 0) {
                    atSee = atSee + paramTypes[iArg].getFullJavaSourceName();
                } else {
                    atSee = atSee + ", " + paramTypes[iArg].getFullJavaSourceName();
                }
            }
            atSee = atSee + ")";
            
            if (paramNames.length == 0) {
                // Simply return the Expr.Var
                bindingMethod.addStatement(new ReturnStatement(sourceModelVarCreation));
            } else {
                // Need to create an application.
                
                // Create an array of SourceModel.Expr.  The first element is the SourceModel.Expr.Var
                // for the CAL function the remaining elements are the function arguments.
                JavaExpression arrayElements[] = new JavaExpression[paramNames.length + 1];
                arrayElements[0] = sourceModelVarCreation; 
                
                for (int i = 1; i <= paramNames.length; ++i) {
                    arrayElements[i] = new JavaExpression.MethodVariable(paramNames[i-1]);
                }
                
                // Build the array creation expression.
                JavaExpression arrayCreation = 
                    new JavaExpression.ArrayCreationExpression(SOURCE_MODEL_EXPR_TYPE_NAME, arrayElements);
                
                // Invoke SourceModel.Expr.Application.make()
                MethodInvocation makeApply =
                    new MethodInvocation.Static(
                            SOURCE_MODEL_EXPR_APPLICATION_TYPE_NAME,
                            "make",
                            arrayCreation,
                            JavaTypeName.makeArrayType(SOURCE_MODEL_EXPR_TYPE_NAME),
                            SOURCE_MODEL_EXPR_APPLICATION_TYPE_NAME);
                
                bindingMethod.addStatement(new ReturnStatement (makeApply));
            
                // If any of the argument types correspond to a Java type.
                // (eg. Prelude.Int, Prelude.Long, etc. we can generate a version
                // of the binding function that takes these argument types.
                boolean primArgs = false;

                TypeExpr[] pieces = te.getTypePieces();
                for (int i = 0; i < paramNames.length; ++i) {
                    if (canTypeBeUnboxed(pieces[i])) {
                        primArgs = true;
                        // Change the param type.
                        paramTypes[i] = typeExprToTypeName(pieces[i]);
                        
                        // Since the parameter will now be an int, boolean, long, etc. we
                        // need to create the appropriate SourceModel wrapper around the 
                        // value.
                        arrayElements[i+1] = wrapArgument(paramNames[i], pieces[i]);
                    }
                }
                
                if (primArgs) {
                    // Create the alternate helper method.
                    bindingMethod =
                        new JavaMethod(PUBLIC_STATIC_FINAL,
                                       SOURCE_MODEL_EXPR_TYPE_NAME,
                                       paramNames,
                                       paramTypes,
                                       null, javaFuncName);
                    functionsClass.addMethod(bindingMethod);
                    
                    JavaStatement.JavaDocComment comment = new JavaStatement.JavaDocComment(atSee);
                    for (int iArg = 0; iArg < paramNames.length; ++iArg) {
                        comment.addLine("@param " + paramNames[iArg]);
                    }                        
                    comment.addLine("@return the SourceModel.Expr representing an application of " + calFuncName);
                    bindingMethod.setJavaDocComment(comment);
                    
                    arrayCreation = 
                        new JavaExpression.ArrayCreationExpression(SOURCE_MODEL_EXPR_TYPE_NAME, arrayElements);
                    
                    makeApply =
                        new MethodInvocation.Static(
                                SOURCE_MODEL_EXPR_APPLICATION_TYPE_NAME,
                                "make",
                                arrayCreation,
                                JavaTypeName.makeArrayType(SOURCE_MODEL_EXPR_TYPE_NAME),
                                SOURCE_MODEL_EXPR_APPLICATION_TYPE_NAME);
                    
                    bindingMethod.addStatement(new ReturnStatement (makeApply));
    
                }
            }
            
            // Add a field for the function name.
            // 'static final QualifiedName functionName = "functionName";'
            // We need to check for conflict between the functionName 
            // and java keywords.  It is valid to have a CAL
            // function called assert but not valid to have java code
            // 'static final Qualifiedname assert = ...
            JavaFieldDeclaration jfd = 
                makeQualifiedNameDeclaration(javaFuncName, calFuncName);
            JavaDocComment qfComment = new JavaDocComment("Name binding for function: " + calFuncName + ".");
            qfComment.addLine(atSee);
            jfd.setJavaDoc(qfComment);

            
            functionsClass.addFieldDeclaration(jfd);
        }
    }
    
    /**
     * Fixup the @param tags in a JavaDoc comment.  This is sometimes necessary when the original 
     * param name in CAL conflicts with a reserved/key word in java, resulting in the actual
     * param name being updated.  ex. char would be changed to char_.
     * @param comment
     * @param originalNames
     * @param updatedNames
     * @return the updated JavaDocComment
     */
    private final JavaDocComment fixupJavaDoc (JavaDocComment comment, String[] originalNames, String[] updatedNames) {

        List<String> lines = new ArrayList<String> ();  
        lines.addAll(comment.getCommentLines());
       
        boolean hasReturn = false;
        boolean explicitComment = false;
        for (int i = 0; i < lines.size(); ++i) {
            String line = lines.get(i);
            if (i == 0 && line.trim().startsWith("/*")) {
                explicitComment = true;
            }
            if (line.indexOf("@return") >= 0) {
                hasReturn = true;
            }
            for (int iName = 0; iName < originalNames.length; ++iName) {
                String originalName = originalNames[iName];
                String updatedName = updatedNames[iName];
                if (originalName.equals(updatedName)) {
                    continue;
                }
                
                String find = "@param " + originalName + " ";
                String replace = "@param " + updatedName + " ";
                lines.set(i, line.replaceFirst(find, replace));
            }
        }

        if (!hasReturn) {
            if (explicitComment) {
                lines.add(lines.size()-1, " * @return SourceModel.Expr");
            } else {
                lines.add("@return SourceModel.Expr");
            }
        }
        
        return new JavaDocComment(lines);
    }
    
    /**
     * Build the TypeConstructors inner class.
     * @param bindingClass
     * @param moduleTypeInfo
     * @param bindingClassName
     */
    private final void buildTypeConstructorsInnerClass (
            JavaClassRep bindingClass,
            ModuleTypeInfo moduleTypeInfo, 
            String bindingClassName) {
        
        if (moduleTypeInfo.getNTypeConstructors() == 0) {
            return;
        }
        
        // Create the inner class.
        JavaTypeName typeConstructorsClassTypeName = JavaTypeName.make (bindingClassPackage + "." + bindingClassName + "." + TYPECONSTRUCTOR_CLASS_NAME, false);
        JavaClassRep typeConstructorsClass = 
            new JavaClassRep (
                    typeConstructorsClassTypeName, 
                    JavaTypeName.OBJECT, 
                    PUBLIC_STATIC_FINAL,
                    EMPTY_TYPE_NAME_ARRAY);
        
        
        // Create the JavaDoc for the inner class.
        JavaDocComment jdc = new JavaDocComment("This inner class (" + TYPECONSTRUCTOR_CLASS_NAME + ") contains constants");
        jdc.addLine("and methods related to binding to CAL TypeConstructors in the " + moduleTypeInfo.getModuleName() + " module.");
        typeConstructorsClass.setJavaDoc(jdc);

        // Build up a list of type constructors and sort by name.
        List<String> typeConstructorNames = new ArrayList<String>();
        for (int i = 0, n = moduleTypeInfo.getNTypeConstructors(); i < n; ++i) {
            TypeConstructor typeCons = moduleTypeInfo.getNthTypeConstructor(i);
            if (typeCons.getScope().isPublic() != publicEntities) {
                continue;
            }
            typeConstructorNames.add(typeCons.getName().getUnqualifiedName());
        }
        Collections.sort(typeConstructorNames);
        
        if (typeConstructorNames.size() == 0) {
            return;
        }
        
        bindingClass.addInnerClass(typeConstructorsClass);
        
        // For each type constructor create a QualifiedName field.
        // The field will have the same name as the type constructor.
        for (final String typeConstructorName : typeConstructorNames) {
            
            TypeConstructor typeCons = moduleTypeInfo.getTypeConstructor(typeConstructorName);
            // Add a field for the typeConstructor name.
            // 'static final QualifiedName typeConstructorName = QualifiedName.make(moduleName, typeConstructorName);
            // We need to check for conflict between the functionName 
            // and java keywords.  It is valid to have a CAL
            // function called assert but not valid to have java code
            // 'static final String assert = "assert";
            String fieldName = fixupVarName(typeConstructorName);
            
            // Since TypeConsApp names are capitalized it is possible to have
            // a conflict between the name of the field and the name of the top
            // level class.
            if (fieldName.equals(this.bindingClassName)) {
                fieldName = fieldName + "_";
            }
            
            JavaFieldDeclaration jfd = 
                makeQualifiedNameDeclaration(fieldName, typeConstructorName);

            // Add JavaDoc for the field declaration.  We use the CALDoc if there is any.
            JavaDocComment typeConstructorComment;
            CALDocComment cdc = typeCons.getCALDocComment();
            if (cdc != null) {
                typeConstructorComment = new JavaDocComment(calDocCommentToJavaComment(cdc, null, false));
            } else {
                typeConstructorComment = new JavaDocComment("/** Name binding for TypeConsApp: " + typeConstructorName + ". */");
            }
            jfd.setJavaDoc(typeConstructorComment);
            
            typeConstructorsClass.addFieldDeclaration(jfd);
            
        }
    }
    
    /**
     * Create the inner class for data constructors.
     * @param bindingClass
     * @param moduleTypeInfo
     * @param bindingClassName
     */
    private final void buildDataConstructorsInnerClass (
            JavaClassRep bindingClass,
            ModuleTypeInfo moduleTypeInfo, 
            String bindingClassName) throws UnableToResolveForeignEntityException {
        
        if (moduleTypeInfo.getNTypeConstructors() == 0) {
            return;
        }
        
        // Create the class.
        JavaTypeName dataConstructorsClassTypeName = JavaTypeName.make (bindingClassPackage + "." + bindingClassName + "." + DATA_CONSTRUCTOR_CLASS_NAME, false);
        JavaClassRep dataConstructorsClass = 
            new JavaClassRep (
                    dataConstructorsClassTypeName, 
                    JavaTypeName.OBJECT, 
                    PUBLIC_STATIC_FINAL,
                    EMPTY_TYPE_NAME_ARRAY);
               

        
        // Add the class JavaDoc.
        JavaDocComment jdc = new JavaDocComment("/**");
        jdc.addLine(" * This inner class (" + DATA_CONSTRUCTOR_CLASS_NAME + ") contains constants");
        jdc.addLine(" * and methods related to binding to CAL DataConstructors in the " + moduleTypeInfo.getModuleName() + " module.");
        jdc.addLine(" */");
        dataConstructorsClass.setJavaDoc(jdc);

        // We want to build up and sort a list of type constructor names.
        List<String> typeConstructorNames = new ArrayList<String>();
        for (int i = 0, n = moduleTypeInfo.getNTypeConstructors(); i < n; ++i) {
            TypeConstructor typeCons = moduleTypeInfo.getNthTypeConstructor(i);
            typeConstructorNames.add(typeCons.getName().getUnqualifiedName());
        }
        Collections.sort(typeConstructorNames);

        
        /*
         * Create a method to generate a SourceModel expression which will result
         * in the creation of an instance of the DC.
         * Create a QualifiedName field for the name of the DC.
         * 
         * The ordering will be by name of the type constructor and then by dc ordinal.
         */
        boolean addClass = false;
        
        // Set of String used to avoid naming conflicts in the generated fields.
        // We want to build up a set of all the names that will be used for the methods and 
        // QualifiedName fields.  This will allow us to avoid naming conflicts when creating
        // the ordinal fields.
        Set<String> javaNames = new HashSet<String>();
        for (final String typeConstructorName : typeConstructorNames) {
            TypeConstructor typeCons = moduleTypeInfo.getTypeConstructor(typeConstructorName);
            for (int i = 0, n = typeCons.getNDataConstructors(); i < n; ++i) {
                DataConstructor dc = typeCons.getNthDataConstructor(i);
                
                // If we are showing public entities we only car about public data constructors.
                if (dc.getScope().isPublic() != publicEntities) {
                    continue;
                }

                // This is the name used to name the java function and the QualfiedName field
                // associated with this data constructor.
                String javaFuncName = fixupVarName(dc.getName().getUnqualifiedName());
                javaNames.add(javaFuncName);
            }
        }
        
        for (final String typeConstructorName : typeConstructorNames) {
            TypeConstructor typeCons = moduleTypeInfo.getTypeConstructor(typeConstructorName);
            boolean commentAdded = false;
            for (int i = 0, n = typeCons.getNDataConstructors(); i < n; ++i) {
                DataConstructor dc = typeCons.getNthDataConstructor(i);
                
                // If we are showing public entities we only car about public data constructors.
                if (dc.getScope().isPublic() != publicEntities) {
                    continue;
                }
                
                addClass = true;
                
                // If this is the first DC for the data type we want a non JavaDoc comment
                // indicating the data type.
                if (!commentAdded) {
                    MultiLineComment mlc = new MultiLineComment("DataConstructors for the " + typeCons.getName().getQualifiedName() + " data type.");
                    dataConstructorsClass.addComment(mlc);
                    commentAdded = true;
                }

                // Get the types of the data constructor fields.
                TypeExpr[] fieldTypes = getFieldTypesForDC(dc);
                
                
                String javaFuncName = fixupVarName(dc.getName().getUnqualifiedName());
                
                // Since data constructors are capitalized its possible to have a conflict between the name 
                // of our field/helper function and the name of the containing class.
                if (javaFuncName.equals(this.bindingClassName)) {
                    javaFuncName = javaFuncName + "_";
                }
                
                // First generate a method that takes SourceModel.Expr instances for
                // each field value.
                
                // Build up the argument names and types.
                int nArgs = dc.getArity();
                JavaTypeName argTypes[] = new JavaTypeName[nArgs];
                Arrays.fill(argTypes, SOURCE_MODEL_EXPR_TYPE_NAME);
                String argNames[] = new String[nArgs];
                String origArgNames[] = new String[nArgs];
                for (int j = 0, k = argNames.length; j < k; ++j) {
                    argNames[j] = fixupVarName(dc.getArgumentName(j));
                    origArgNames[j] = dc.getArgumentName(j);
                }
                
                // Create the method.
                JavaMethod bindingFunction = 
                    new JavaMethod(PUBLIC_STATIC_FINAL,
                                   SOURCE_MODEL_EXPR_TYPE_NAME,
                                   argNames,
                                   argTypes,
                                   null, javaFuncName);
                dataConstructorsClass.addMethod(bindingFunction);
                
                // Add JavaDoc for the method.
                JavaDocComment funcComment;
                CALDocComment cdc = dc.getCALDocComment();
                if (cdc != null) {
                    funcComment = new JavaDocComment(calDocCommentToJavaComment(cdc, dc, false, argNames));
                    funcComment = fixupJavaDoc(funcComment, origArgNames, argNames);
                } else {
                    funcComment = new JavaDocComment("Binding for DataConstructor: " + dc.getName().getQualifiedName() + ".");
                    for (int iName = 0; iName < argNames.length; ++iName) {
                        funcComment.addLine("@param " + argNames[iName]);
                    }
                    funcComment.addLine("@return the SourceModule.Expr representing an application of " + dc.getName().getQualifiedName());
                }
                bindingFunction.setJavaDocComment(funcComment);
                
                // Now we need to fill in the body.
                
                // Create an instance of SourceModel.Expr.DataCons for the data constructor.
                JavaField nameField = new JavaField.Static(dataConstructorsClassTypeName, javaFuncName, QUALIFIED_NAME_TYPE_NAME);
                JavaExpression sourceModelDataConsCreation =
                    new MethodInvocation.Static(
                            SOURCE_MODEL_EXPR_DATA_CONS_TYPE_NAME, 
                            "make", 
                            nameField,
                            QUALIFIED_NAME_TYPE_NAME, 
                            SOURCE_MODEL_EXPR_DATA_CONS_TYPE_NAME);
                
                // Build up an @see tag for the function just created.
                String atSee = "@see #" + javaFuncName + "(";
                for (int iArg = 0; iArg < argNames.length; ++iArg) {
                    if (iArg == 0) {
                        atSee = atSee + argTypes[iArg].getFullJavaSourceName();
                    } else {
                        atSee = atSee + ", " + argTypes[iArg].getFullJavaSourceName();
                    }
                }
                atSee = atSee + ")";
                
                if (dc.getArity() == 0) {
                    // Simply return the Expr.DataCons.
                    bindingFunction.addStatement(new ReturnStatement(sourceModelDataConsCreation));
                } else {
                    // Need to build up an application.
                    
                    // Create an array of SourceModel.Expr where the first element is the 
                    // SourceModel.Expr.DataCons instance and the following elements are
                    // the function arguments.
                    JavaExpression arrayElements[] = new JavaExpression[dc.getArity() + 1];
                    arrayElements[0] = sourceModelDataConsCreation; 
                    
                    for (int j = 1; j <= argNames.length; ++j) {
                        arrayElements[j] = new JavaExpression.MethodVariable(argNames[j-1]);
                    }
                    
                    JavaExpression arrayCreation = 
                        new JavaExpression.ArrayCreationExpression(SOURCE_MODEL_EXPR_TYPE_NAME, arrayElements);
                    
                    // Invoke SourceModle.Expr.Application.make()
                    MethodInvocation makeApply =
                        new MethodInvocation.Static(
                                SOURCE_MODEL_EXPR_APPLICATION_TYPE_NAME,
                                "make",
                                arrayCreation,
                                JavaTypeName.makeArrayType(SOURCE_MODEL_EXPR_TYPE_NAME),
                                SOURCE_MODEL_EXPR_APPLICATION_TYPE_NAME);
                    
                    bindingFunction.addStatement(new ReturnStatement (makeApply));
                
                    // If any of the argument types correspond to a Java type.
                    // (eg. Prelude.Int, Prelude.Long, etc. we can generate a version
                    // of the binding function that takes these argument types.
                    boolean primArgs = false;
                    for (int j = 0, k = fieldTypes.length; j < k; ++j) {
                        if (canTypeBeUnboxed(fieldTypes[j])) {
                            primArgs = true;
                            // Update the type of the argument.
                            argTypes[j] = typeExprToTypeName(fieldTypes[j]);
                            
                            // The argument to Application.make needs to be updated.
                            // We need to wrap the raw value (i.e. int, boolean, etc.)
                            // in the appropriate SourceModel construct.
                            arrayElements[j+1] = wrapArgument(argNames[j], fieldTypes[j]);
                        }
                    }

                    
                    if (primArgs) {
                        bindingFunction =
                            new JavaMethod(PUBLIC_STATIC_FINAL,
                                           SOURCE_MODEL_EXPR_TYPE_NAME,
                                           argNames,
                                           argTypes,
                                           null, javaFuncName);
                        dataConstructorsClass.addMethod(bindingFunction);
                        
                        // For the comment for this method we want an @see referring to the previous method.
                        
                        JavaStatement.JavaDocComment comment = new JavaStatement.JavaDocComment(atSee);
                        for (int iArg = 0; iArg < argNames.length; ++iArg) {
                            comment.addLine("@param " + argNames[iArg]);
                        }                        
                        comment.addLine("@return " + SOURCE_MODEL_EXPR_TYPE_NAME.getFullJavaSourceName());
                        bindingFunction.setJavaDocComment(comment);
                        
                        arrayCreation = 
                            new JavaExpression.ArrayCreationExpression(SOURCE_MODEL_EXPR_TYPE_NAME, arrayElements);
                        
                        makeApply =
                            new MethodInvocation.Static(
                                    SOURCE_MODEL_EXPR_APPLICATION_TYPE_NAME,
                                    "make",
                                    arrayCreation,
                                    JavaTypeName.makeArrayType(SOURCE_MODEL_EXPR_TYPE_NAME),
                                    SOURCE_MODEL_EXPR_APPLICATION_TYPE_NAME);
                        
                        bindingFunction.addStatement(new ReturnStatement (makeApply));
        
                    }
                    
                }
                
                // Create a field declaration for the QualifiedName field.
                JavaFieldDeclaration jfd = 
                    makeQualifiedNameDeclaration(javaFuncName, dc.getName().getUnqualifiedName());
                
                // If there is CALDoc for the DC add it as JavaDoc.
                JavaDocComment comment = new JavaDocComment("Name binding for DataConstructor: " + dc.getName().getQualifiedName() + ".");
                comment.addLine(atSee);
                
                jfd.setJavaDoc(comment);
                
                dataConstructorsClass.addFieldDeclaration(jfd);                
             
                // Now add an int field which is the ordinal of the data constructor.
                String ordinalFieldName = javaFuncName + "_ordinal";
                while (javaNames.contains(ordinalFieldName)) {
                    ordinalFieldName = ordinalFieldName + "_";
                }
                JavaFieldDeclaration ordinalFieldDec = 
                    new JavaFieldDeclaration(PUBLIC_STATIC_FINAL, JavaTypeName.INT, ordinalFieldName, JavaExpression.LiteralWrapper.make(Integer.valueOf(dc.getOrdinal())));
                javaNames.add(ordinalFieldName);
                JavaDocComment ordinalComment = new JavaDocComment("Ordinal of DataConstructor " + dc.getName().getQualifiedName() + ".");
                ordinalComment.addLine(atSee);
                ordinalFieldDec.setJavaDoc(ordinalComment);
                
                dataConstructorsClass.addFieldDeclaration(ordinalFieldDec);
            }
        }
        
        if (addClass) {
            bindingClass.addInnerClass(dataConstructorsClass);
        }
    }
    
    /**
     * Build the inner class for TypeClasses
     * @param bindingClass
     * @param moduleTypeInfo
     * @param bindingClassName
     */
    private final void buildTypeClassInnerClass (
            JavaClassRep bindingClass,
            ModuleTypeInfo moduleTypeInfo, 
            String bindingClassName) {
        
        if (moduleTypeInfo.getNTypeClasses() == 0) {
            return;
        }
        
        // Create the inner class.
        JavaTypeName typeClassClassTypeName = 
            JavaTypeName.make (bindingClassPackage + "." + bindingClassName + "." + TYPECLASS_CLASS_NAME, false);
        
        JavaClassRep typeClassClass = 
            new JavaClassRep (
                    typeClassClassTypeName, 
                    JavaTypeName.OBJECT, 
                    PUBLIC_STATIC_FINAL,
                    EMPTY_TYPE_NAME_ARRAY);
                
        
        // Set the JavaDoc for the class.
        JavaDocComment jdc = new JavaDocComment("This inner class (" + TYPECLASS_CLASS_NAME + ") contains constants");
        jdc.addLine("and methods related to binding to CAL TypeClasses in the " + moduleTypeInfo.getModuleName() + " module.");
        typeClassClass.setJavaDoc(jdc);

        // Build up a list of TypeClass names and sort.
        List<String> typeClassNames = new ArrayList<String>();
        for (int i = 0, n = moduleTypeInfo.getNTypeClasses(); i < n; ++i) {
            TypeClass tc = moduleTypeInfo.getNthTypeClass(i);
            if (tc.getScope().isPublic() != publicEntities) {
                continue;
            }
            typeClassNames.add(tc.getName().getUnqualifiedName());
        }
        Collections.sort(typeClassNames);
        
        if (typeClassNames.size() == 0) {
            return;
        }
        
        bindingClass.addInnerClass(typeClassClass);

        // Generate a QualifiedName field for each type class.
        for (final String typeClassName : typeClassNames) {
            TypeClass tc = moduleTypeInfo.getTypeClass(typeClassName);
            // Add a field for the TypeClass name.
            // 'static final String typeClassName = "typeClassName";'
            // We need to check for conflict between the functionName 
            // and java keywords.  It is valid to have a CAL
            // function called assert but not valid to have java code
            // 'static final QualifiedName assert = ...
            String fieldName = fixupVarName(typeClassName);
            
            // Since TypeClass names are capitalized it's possible to have a conflict
            // between the name of the field and the name of the top level class.
            if (fieldName.equals(this.bindingClassName)) {
                fieldName = fieldName + "_";
            }
            
            JavaFieldDeclaration jfd = 
                makeQualifiedNameDeclaration(fieldName, typeClassName);

            // Add JavaDoc.  We use any CALDoc for the type class if available.
            JavaDocComment comment;
            CALDocComment cdc = tc.getCALDocComment();
            if (cdc != null) {
                comment = new JavaDocComment(calDocCommentToJavaComment(cdc, null, false));
            } else {
                comment = new JavaDocComment("/** Name binding for TypeClass: " + tc.getName().getQualifiedName() + ". */");
            }
            jfd.setJavaDoc(comment);
            
            typeClassClass.addFieldDeclaration(jfd);
        }
    }
    
    /**
     * Convert a CALDocComment instance into JavaDoc.
     * @param comment
     * @param bindingEntity
     * @param isClassComment whether the Javadoc comment is a class comment.
     * @return the text of the JavaDoc comment.
     */
    private final String calDocCommentToJavaComment (CALDocComment comment, FunctionalAgent bindingEntity, boolean isClassComment) {
        return calDocCommentToJavaComment(comment, bindingEntity, isClassComment, null);
    }
    
    /**
     * Convert a CALDocComment instance into JavaDoc.
     * @param comment
     * @param bindingEntity
     * @param isClassComment whether the Javadoc comment is a class comment.
     * @param argNames the names of arguments to use. Can be null if the default mechanism for obtaining argument names is to be used.
     * @return the text of the JavaDoc comment.
     */
    private final String calDocCommentToJavaComment (CALDocComment comment, FunctionalAgent bindingEntity, boolean isClassComment, String[] argNames) {
        String text = CALDocToJavaDocUtilities.getJavadocFromCALDocComment(comment, isClassComment, bindingEntity, argNames);
        return text;
    }
    
    /**
     * Generate a JavaFieldDeclaration for a QualifiedName field.
     * @param fieldName
     * @param entityName
     * @return the field declaration.
     */
    private final JavaFieldDeclaration makeQualifiedNameDeclaration (String fieldName, String entityName) {
        JavaExpression initializer = 
            new JavaExpression.MethodInvocation.Static (
                    QUALIFIED_NAME_TYPE_NAME,
                    "make",
                    new JavaExpression[]{moduleNameField, LiteralWrapper.make(entityName)},
                    new JavaTypeName[]{JavaTypeName.MODULE_NAME, JavaTypeName.STRING},
                    QUALIFIED_NAME_TYPE_NAME);
        
        return new JavaFieldDeclaration(PUBLIC_STATIC_FINAL, QUALIFIED_NAME_TYPE_NAME, fieldName, initializer);
    }
    
    /**
     * Returns true if the provided type expression corresponds
     * to an unboxable type.  Currently these types consist of 
     * the CAL types corresponding to Java primitive types, excluding
     * void, and java.lang.String.
     * @param typeExpr
     * @return true if the type can be unboxed.
     */
    static private boolean canTypeBeUnboxed (TypeExpr typeExpr) throws UnableToResolveForeignEntityException {
        if (typeExpr == null) {
            return false;
        }
        
        TypeConsApp typeConsApp = typeExpr.rootTypeConsApp();
        if (typeConsApp != null && typeConsApp.getNArgs() == 0) {
        
            if (typeConsApp.isNonParametricType(CAL_Prelude.TypeConstructors.Boolean)) {                
                return true;
            }
    
            if(typeConsApp.getForeignTypeInfo() != null) {
                ForeignTypeInfo fti = typeConsApp.getForeignTypeInfo();
                Class<?> foreignClass = fti.getForeignType();
                if(foreignClass.isPrimitive() && foreignClass != void.class) {
                    return true;
                }
                
                if (foreignClass == java.lang.String.class) {
                    return true;
                }
            }               
        }
        
        return false;
    }
    
    /**
     * Generate the JavaModel which will wrap an unboxed value in the 
     * appropriate SourceModel construct.
     * @param argName
     * @param argType
     * @return the JavaExpression which wraps the unboxed value.
     */
    static private JavaExpression wrapArgument (String argName, TypeExpr argType) throws UnableToResolveForeignEntityException {
        TypeConsApp typeConsApp = argType.rootTypeConsApp();
        if (typeConsApp != null && typeConsApp.getNArgs() == 0) {
            
            // boolean 
            if (typeConsApp.isNonParametricType(CAL_Prelude.TypeConstructors.Boolean)) {                
                JavaExpression makeBoolean = 
                    new MethodInvocation.Static(
                            SOURCE_MODEL_EXPR_TYPE_NAME, 
                            "makeBooleanValue", 
                            new MethodVariable(argName), 
                            JavaTypeName.BOOLEAN, 
                            SOURCE_MODEL_EXPR_TYPE_NAME);
                
                return makeBoolean;
            }

            if(typeConsApp.getForeignTypeInfo() != null) {
                ForeignTypeInfo fti = typeConsApp.getForeignTypeInfo();
                Class<?> foreignClass = fti.getForeignType();
                
                // We handle the primitive Java types, excluding void.
                if(foreignClass.isPrimitive() && foreignClass != void.class) {
                    MethodVariable mv = new MethodVariable(argName);
                    String funcName;
                    JavaTypeName valueType;
                    if (foreignClass == boolean.class) {
                        funcName = "makeBooleanValue";
                        valueType = JavaTypeName.BOOLEAN;
                    } else if (foreignClass == byte.class) {
                        funcName = "makeByteValue";
                        valueType = JavaTypeName.BYTE;
                    } else if (foreignClass == char.class) {
                        funcName = "makeCharValue";
                        valueType = JavaTypeName.CHAR;
                    } else if (foreignClass == double.class) {
                        funcName = "makeDoubleValue";
                        valueType = JavaTypeName.DOUBLE;
                    } else if (foreignClass == float.class) {
                        funcName = "makeFloatValue";
                        valueType = JavaTypeName.FLOAT;
                    } else if (foreignClass == int.class) {
                        funcName = "makeIntValue";
                        valueType = JavaTypeName.INT;
                    } else if (foreignClass == long.class) {
                        funcName = "makeLongValue";
                        valueType = JavaTypeName.LONG;
                    } else if (foreignClass == short.class) {
                        funcName = "makeShortValue";
                        valueType = JavaTypeName.SHORT;
                    } else {
                        throw new IllegalStateException("Unhandled primitive type " + foreignClass.getName());
                    }
                    
                    JavaExpression makeValue = 
                        new MethodInvocation.Static(
                                SOURCE_MODEL_EXPR_TYPE_NAME, 
                                funcName, 
                                mv, 
                                valueType, 
                                SOURCE_MODEL_EXPR_TYPE_NAME);
                    
                    return makeValue;
                }
                
                // Handle java.lang.String.
                if (foreignClass == java.lang.String.class) {
                    return new MethodInvocation.Static(
                                    SOURCE_MODEL_EXPR_TYPE_NAME,
                                    "makeStringValue",
                                    new MethodVariable(argName),
                                    JavaTypeName.STRING,
                                    SOURCE_MODEL_EXPR_TYPE_NAME);
                }
            }               
        }
        
        // Not a type we handle.
        throw new IllegalStateException("Unable to wrap argument of type: " + argType.toString());
    }

    /**
     * Translate a TypeExpr to the corresponding JavaTypeName.
     * @param argType
     * @return the JavaTypeName.
     */
    private final static JavaTypeName typeExprToTypeName (TypeExpr argType) throws UnableToResolveForeignEntityException {
        TypeConsApp typeConsApp = argType.rootTypeConsApp();
        if (typeConsApp != null && typeConsApp.getNArgs() == 0) {
            if (typeConsApp.isNonParametricType(CAL_Prelude.TypeConstructors.Boolean)) { 
                return JavaTypeName.BOOLEAN;
            }

            if(typeConsApp.getForeignTypeInfo() != null) {
                ForeignTypeInfo fti = typeConsApp.getForeignTypeInfo();
                Class<?> foreignClass = fti.getForeignType();
                if(foreignClass.isPrimitive() && foreignClass != void.class) {
                    if (foreignClass == boolean.class) {
                        return JavaTypeName.BOOLEAN;
                    } else if (foreignClass == byte.class) {
                        return JavaTypeName.BYTE;
                    } else if (foreignClass == char.class) {
                        return JavaTypeName.CHAR;
                    } else if (foreignClass == double.class) {
                        return JavaTypeName.DOUBLE;
                    } else if (foreignClass == float.class) {
                        return JavaTypeName.FLOAT;
                    } else if (foreignClass == int.class) {
                        return JavaTypeName.INT;
                    } else if (foreignClass == long.class) {
                        return JavaTypeName.LONG;
                    } else if (foreignClass == short.class) {
                        return JavaTypeName.SHORT;
                    } else {
                        throw new IllegalStateException("Unhandled primitive type " + foreignClass.getName());
                    }
                }
                
                if (foreignClass == java.lang.String.class) {
                    return JavaTypeName.STRING;
                }
            }               
        }
        
        throw new IllegalStateException("Unable to convert type: " + argType.toString());
        
    }
    
    /**
     * Use this function to get the field types for a data constructor.
     * Appropriate conversions are done based on whether or not unboxed DC fields are being used
     * and whether enums are treated as ints.
     * @param dc
     * @return an array of TypeExpr
     */
    private static TypeExpr[] getFieldTypesForDC (DataConstructor dc) {
        TypeExpr[] fieldTypes = new TypeExpr[dc.getArity()];
        TypeExpr dcType = dc.getTypeExpr();
        TypeExpr[] tft = dcType.getTypePieces(dc.getArity());
        System.arraycopy(tft, 0, fieldTypes, 0, fieldTypes.length);

        return fieldTypes;
    }
    
    /**
     * When deconstructing a data type the names assigned to the members
     * can have conflicts with java reserved words.  We fix this by
     * prepending a '$'.  The '$' is used to avoid creating a new conflict
     * with another CAL variable.
     * @param varName
     * @return String
     */
    private static String fixupVarName(String varName) {
        if (JavaReservedWords.javaLanguageKeywords.contains(varName)) {
            return varName + "_";
        } 
        
        if (varName.equals("QualifiedName")) {
            varName = varName + "_";
        }
        varName = varName.replace ('.', '_');
        varName = varName.replace ('#', '_');
        
        return varName;
    } 
    
    /**
     * @param classRep
     * @return a has code for the concatenated JavaDoc in the classRep.
     */
    private int computeJavaDocHash (JavaClassRep classRep){
        // We want to traverse the class collecting all the
        // JavaDoc comments.
        
        JavaDocCollector jdc = new JavaDocCollector();
        List<JavaDocComment> comments = new ArrayList<JavaDocComment>();
        classRep.accept(jdc, comments);
        
        StringBuilder sb = new StringBuilder();
        for (final JavaDocComment comment : comments) {
           
            for (final String line : comment.getCommentLines()) {
                sb.append(line);
            }
        }
        
        return sb.toString().hashCode();
    }
    
    private static class JavaBindingClassChecker extends JavaModelTraverser<Void, Void> {
        /** Map of String -> JavaFieldDeclaration.  Data constructor QualifiedName binding fields.*/
        Map<String, JavaFieldDeclaration> dataConsFields = new HashMap<String, JavaFieldDeclaration>();
        
        /** Map of String -> JavaFieldDeclaration.  DataConstructor ordinal value fields. */
        Map<String, JavaFieldDeclaration> dataConsOrdinalFields = new HashMap<String, JavaFieldDeclaration>();
        
        /** Set of JavaMethod. Methods in the DC inner class. */
        Set<JavaMethod> dataConsFunctions = new HashSet<JavaMethod>();
        
        /** Map of String -> JavaFieldDeclaration.  Function QualifiedName binding fields. */
        Map<String, JavaFieldDeclaration> functionFields = new HashMap<String, JavaFieldDeclaration>();
        
        /** Set of JavaMethod. Helper methods in the Functions inner class. */
        Set<JavaMethod> functions = new HashSet<JavaMethod>();
        
        /** Map of String -> JavaFieldDeclaration.  TypeClass QualifiedName binding fields. */
        Map<String, JavaFieldDeclaration> typeClassFields = new HashMap<String, JavaFieldDeclaration>();
        
        /** Map of String -> JavaFieldDeclaration.  TypeConstructor QualfiedName binding Fields. */
        Map<String, JavaFieldDeclaration> typeConstructorFields = new HashMap<String, JavaFieldDeclaration>();
        
        Map<String, JavaFieldDeclaration> currentFieldSet;
        Set<JavaMethod> currentFunctionSet;
        Map<String, JavaFieldDeclaration> currentIntFieldSet;
        
        /**
         * @param fieldDeclaration
         *          the java model element to be visited
         * @param arg
         *          additional argument for the visitation
         * @return the result from visiting the java model element
         */
        @Override
        public Void visitJavaFieldDeclaration (
                JavaFieldDeclaration fieldDeclaration, Void arg) {
            if (currentFieldSet != null && fieldDeclaration.getFieldType().equals(JavaTypeName.QUALIFIED_NAME)) {
                    currentFieldSet.put(fieldDeclaration.getFieldName(), fieldDeclaration);
            }
            
            if (currentIntFieldSet != null && fieldDeclaration.getFieldType().equals(JavaTypeName.INT)) {
                currentIntFieldSet.put(fieldDeclaration.getFieldName(), fieldDeclaration);
            }
            return null;
        }
        
        /**
         * @param javaMethod
         *          the java model element to be visited
         * @param arg
         *          additional argument for the visitation
         * @return the result from visiting the java model element
         */
        @Override
        public Void visitJavaMethod (
                JavaMethod javaMethod, Void arg) {
            if (currentFunctionSet != null) {
                currentFunctionSet.add(javaMethod);
            }
            return super.visitJavaMethod(javaMethod, arg);
        }
     
        /**
         * @param classRep
         *          the java model element to be visited
         * @param arg
         *          additional argument for the visitation
         * @return the result from visiting the java model element
         */
        @Override
        public Void visitJavaClassRep (
                JavaClassRep classRep, Void arg) {
            
            String className = classRep.getClassName().getUnqualifiedJavaSourceName();
            if (className.indexOf("$") >= 0) {
                className = className.substring(className.lastIndexOf("$") + 1);
            } 
            
            if (className.equals(FUNCTION_CLASS_NAME)) {
                currentFieldSet = functionFields;
                currentFunctionSet = functions;
                currentIntFieldSet = null;
            } else
            if (className.equals(DATA_CONSTRUCTOR_CLASS_NAME)) {
                currentFieldSet = dataConsFields;
                currentFunctionSet = dataConsFunctions;
                currentIntFieldSet = dataConsOrdinalFields;
            } else
            if (className.equals(TYPECLASS_CLASS_NAME)) {
                currentFieldSet = typeClassFields;
                currentFunctionSet = null;
                currentIntFieldSet = null;
            } else
            if (className.equals(TYPECONSTRUCTOR_CLASS_NAME)) {
                currentFieldSet = typeConstructorFields;
                currentFunctionSet = null;
                currentIntFieldSet = null;
            } else {
                currentFieldSet = null;
                currentFunctionSet = null;
                currentIntFieldSet = null;
            }
            
            return super.visitJavaClassRep(classRep, arg);
        }
    }
    
    private static class JavaDocCollector extends JavaModelTraverser<List<JavaDocComment>, Void> {
        /* (non-Javadoc)
         * @see org.openquark.cal.internal.runtime.lecc.JavaModelVisitor#visitJavaDocCommentStatement(org.openquark.cal.internal.runtime.lecc.JavaStatement.JavaDocComment, java.lang.Object)
         */
        @Override
        public Void visitJavaDocCommentStatement(
                JavaStatement.JavaDocComment comment, List<JavaDocComment> arg) {
            arg.add(comment);
            return null;
        }
    }

}
