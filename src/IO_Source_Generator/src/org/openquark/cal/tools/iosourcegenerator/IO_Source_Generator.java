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
 * IO_Source_Generator.java 
 *
 * Creation date: May 17, 2007
 * By: Raymond Cypher
 */
package org.openquark.cal.tools.iosourcegenerator;

import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.openquark.cal.compiler.ClassInstance;
import org.openquark.cal.compiler.DataConstructor;
import org.openquark.cal.compiler.FieldName;
import org.openquark.cal.compiler.ForeignTypeInfo;
import org.openquark.cal.compiler.Function;
import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.ModuleTypeInfo;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.RecordType;
import org.openquark.cal.compiler.Scope;
import org.openquark.cal.compiler.SourceModel;
import org.openquark.cal.compiler.SourceModelCopier;
import org.openquark.cal.compiler.TypeApp;
import org.openquark.cal.compiler.TypeConsApp;
import org.openquark.cal.compiler.TypeConstructor;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.compiler.UnableToResolveForeignEntityException;
import org.openquark.cal.compiler.SourceModel.CALDoc;
import org.openquark.cal.compiler.SourceModel.Expr;
import org.openquark.cal.compiler.SourceModel.Friend;
import org.openquark.cal.compiler.SourceModel.FunctionDefn;
import org.openquark.cal.compiler.SourceModel.FunctionTypeDeclaration;
import org.openquark.cal.compiler.SourceModel.Import;
import org.openquark.cal.compiler.SourceModel.InstanceDefn;
import org.openquark.cal.compiler.SourceModel.LocalDefn;
import org.openquark.cal.compiler.SourceModel.ModuleDefn;
import org.openquark.cal.compiler.SourceModel.Name;
import org.openquark.cal.compiler.SourceModel.Parameter;
import org.openquark.cal.compiler.SourceModel.TopLevelSourceElement;
import org.openquark.cal.compiler.SourceModel.TypeConstructorDefn;
import org.openquark.cal.compiler.SourceModel.TypeExprDefn;
import org.openquark.cal.compiler.SourceModel.TypeSignature;
import org.openquark.cal.internal.javamodel.JavaClassRep;
import org.openquark.cal.internal.javamodel.JavaConstructor;
import org.openquark.cal.internal.javamodel.JavaExpression;
import org.openquark.cal.internal.javamodel.JavaFieldDeclaration;
import org.openquark.cal.internal.javamodel.JavaMethod;
import org.openquark.cal.internal.javamodel.JavaOperator;
import org.openquark.cal.internal.javamodel.JavaReservedWords;
import org.openquark.cal.internal.javamodel.JavaStatement;
import org.openquark.cal.internal.javamodel.JavaTypeName;
import org.openquark.cal.internal.javamodel.JavaExpression.Assignment;
import org.openquark.cal.internal.javamodel.JavaExpression.CastExpression;
import org.openquark.cal.internal.javamodel.JavaExpression.JavaField;
import org.openquark.cal.internal.javamodel.JavaExpression.LiteralWrapper;
import org.openquark.cal.internal.javamodel.JavaExpression.LocalVariable;
import org.openquark.cal.internal.javamodel.JavaExpression.MethodInvocation;
import org.openquark.cal.internal.javamodel.JavaExpression.MethodVariable;
import org.openquark.cal.internal.javamodel.JavaExpression.OperatorExpression;
import org.openquark.cal.internal.javamodel.JavaStatement.Block;
import org.openquark.cal.internal.javamodel.JavaStatement.ExpressionStatement;
import org.openquark.cal.internal.javamodel.JavaStatement.IfThenElseStatement;
import org.openquark.cal.internal.javamodel.JavaStatement.JavaDocComment;
import org.openquark.cal.internal.javamodel.JavaStatement.LocalVariableDeclaration;
import org.openquark.cal.internal.javamodel.JavaStatement.MultiLineComment;
import org.openquark.cal.internal.javamodel.JavaStatement.ReturnStatement;
import org.openquark.cal.internal.javamodel.JavaStatement.SwitchStatement;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.runtime.CalValue;

/**
 * This class generates I/O source code in the form of a set of Java classes and a CAL
 * module.  The Java classes are in the form of instances of JavaClassRep and the CAL module 
 * is produced in the form of an instance of SourceModel.Module.  Client code is responsible for
 * converting these forms into source code, byte code, etc.
 * 
 * When working on a project which combines CAL and Java code there is often a need to 
 * marshal data types between CAL and Java.  The mechanism which is generally used for 
 * this is to make a CAL data type an instance of the Inputable and Outputable type classes.
 *
 * To make a user defined type inputable and outputable, you would first decide what the 
 * Java representation of the Quark type will be. The input and output methods will convert 
 * between the Quark type and an appropriate instance of this Java class.
 * A common approach is to define a foreign type for the target Java class, write CAL 
 * functions that convert to and from that type, using foreign functions, then convert 
 * to or from JObject using a foreign function that merely casts between the target Java 
 * class and Object.  For more information please see the two documents ‘CAL User’s Guide’ 
 * and ‘Java Meets Quark’.
 *
 * The Input/Output code generation tool seeks to streamline the process of making a CAL 
 * data type an instance of the Inputable and Outputable type classes by generating the 
 * majority of the necessary CAL and Java code.
 *
 * The code generation tool takes an existing CAL data type and will generate a corresponding 
 * Java class.  It will then generate the necessary CAL code to refer to the generated Java 
 * class as a CAL foreign type, generate the CAL functions needed to convert between the original 
 * CAL data type and the foreign type, and generate the type class instance declarations to make the 
 * original data type an instance of the Inputable and Outputable type classes.
 *
 * @author rcypher
 *
 */

public class IO_Source_Generator {

    private static final JavaTypeName CAL_VALUE = JavaTypeName.make(CalValue.class);

    /** 
     * These are not valid file names in Windows. In addition there is the com1, com2, ..., lpt1, lpt2, ... family of names that are 
     * parameterized by an integer. Note that Windows is case-insensitive, so check that the lower case of your string is not in this
     * set.
     */
    static private final Set<String> windowsReservedWords = new LinkedHashSet<String>();
    static {        
        windowsReservedWords.add ("clock$");
        windowsReservedWords.add ("con");
        windowsReservedWords.add ("prn");
        windowsReservedWords.add ("nul");
        windowsReservedWords.add ("config$");
        windowsReservedWords.add ("aux");
    }
    
    private static final JavaTypeName[] EMPTY_TYPE_NAME_ARRAY = new JavaTypeName[0];
    private static final JavaTypeName UNSUPPORTED_OPERATION_TYPE_NAME = JavaTypeName.make(UnsupportedOperationException.class);
    
    private static final String GET_DC_ORDINAL_METHOD_NAME = "getDCOrdinal";
    private static final String GET_DC_NAME_METHOD_NAME = "getDCName";
    
    private static final String ORDINAL_FIELD_NAME = "ordinal$";
    private static final String DC_NAME_FIELD_NAME = "dcName$";
    private static final String HASH_CODE_FIELD_NAME = "hashCode";

    private static final TypeExprDefn.TypeCons INT_TYPE_EXPR_DEFN =
        TypeExprDefn.TypeCons.make(Name.TypeCons.make(CAL_Prelude.TypeConstructors.Int));

    private static final TypeExprDefn.TypeCons JOBJECT_TYPE_EXPR_DEFN =
        TypeExprDefn.TypeCons.make(Name.TypeCons.make(CAL_Prelude.TypeConstructors.JObject));

    public static final class GeneratedIO {
        /** QualifiedName -> JavaClassRep */
        private final Map<QualifiedName, JavaClassRep> javaClasses;
        
        /** QualifiedName -> (List of LogRecord) */
        private final Map<QualifiedName, List<LogRecord>> javaClassLogMessages;
        
        /** SourceModel.ModuleDefn for CAL I/O code. */
        private final SourceModel.ModuleDefn ioModule;
        
        /** List of LogRecord for the module definition. */
        private final List<LogRecord> moduleLogMessages;
        
        private final List<LogRecord> ioLogMessages;
        
        GeneratedIO (Map<QualifiedName, JavaClassRep> javaClasses, 
                     Map<QualifiedName, List<LogRecord>> javaClassLogMessages, 
                     ModuleDefn ioModule, 
                     List<LogRecord> moduleLogMessages, 
                     List<LogRecord> ioLogMessages) {
            this.javaClasses = javaClasses;
            this.javaClassLogMessages = javaClassLogMessages;
            this.ioModule = ioModule;
            this.moduleLogMessages = moduleLogMessages;
            this.ioLogMessages = ioLogMessages;
        }

        /**
         * @return the ioModule
         */
        public SourceModel.ModuleDefn getIoModule() {
            return ioModule;
        }

        /**
         * @return the javaClasses
         */
        public Map<QualifiedName, JavaClassRep> getJavaClasses() {
            return javaClasses;
        }

        /**
         * @return the ioLogMessages
         */
        public List<LogRecord> getIoLogMessages() {
            return ioLogMessages;
        }

        /**
         * @return the javaClassLogMessages
         */
        public Map<QualifiedName, List<LogRecord>> getJavaClassLogMessages() {
            return javaClassLogMessages;
        }

        /**
         * @return the moduleLogMessages
         */
        public List<LogRecord> getModuleLogMessages() {
            return moduleLogMessages;
        }

    }
    
    final GeneratedIO generateIO (            
            ModuleTypeInfo module, 
            String targetPackage, 
            Set<QualifiedName> typeConstructorsToIgnore,
            Map<QualifiedName, JavaTypeName> typeToClassMappings,
            Map<ModuleName, String>moduleToPackageMappings) throws UnableToResolveForeignEntityException {
        
        /** Maps names of CAL types to the corresponding generated Java class. */
        Map<QualifiedName, JavaClassRep> classReps = 
            new LinkedHashMap<QualifiedName, JavaClassRep>();
        
        Map<QualifiedName, List<LogRecord>> classLogMessages = 
            new LinkedHashMap<QualifiedName, List<LogRecord>>();
        
        List<TopLevelSourceElement> allTopLevelElements = 
            new ArrayList<TopLevelSourceElement>();
        
        List<LogRecord> calLogMessages = new ArrayList<LogRecord>();
        
        Set<ModuleName> mustHaveImports = new LinkedHashSet<ModuleName>();
        Map<String, TypeSignature> castFunctions = new LinkedHashMap<String, TypeSignature>();
        Map<QualifiedName, TypeConstructor> additionalForeignTypeDeclarations = new LinkedHashMap<QualifiedName, TypeConstructor>();
        
        for (int i = 0, n = module.getNTypeConstructors(); i < n; ++i) {
            TypeConstructor tc = module.getNthTypeConstructor(i);
            if (typeConstructorsToIgnore.contains(tc.getName()) ||
                tc.getForeignTypeInfo() != null) {
                logMessage(Level.INFO, "Skipped type constructor " + tc.getName().getUnqualifiedName() + " because of ignore directive.");
                continue;
            }
            
            // If the type constructor is declared as inputable/outputable in the containing module
            // we skip it because generating inputable/outputable instances would conflict.
            boolean alreadyInputableOutputable = false;
            for (int j = 0, k = module.getNClassInstances(); j < k; ++j) {
                ClassInstance ci = module.getNthClassInstance(j);
                if (ci.getTypeClass().getName().equals(CAL_Prelude.TypeClasses.Inputable) ||
                    ci.getTypeClass().getName().equals(CAL_Prelude.TypeClasses.Outputable)) {
                    // Check the instance type
                    TypeExpr instanceTypeExpr = ci.getType();
                    TypeConsApp tca = instanceTypeExpr.rootTypeConsApp();
                    if (tca != null) {
                        if (tca.getRoot().getName().equals(tc.getName())) {
                            alreadyInputableOutputable = true;
                        }
                    }
                }
            }
            if (alreadyInputableOutputable) {
                logMessage(Level.INFO, "Skipped type constructor " + tc.getName().getUnqualifiedName() + " because it is alreayd an instance of Inputable or Outputable.");
                continue;
            }
            
            // Build up info about the type constructor and data constructors. 
            TypeConstructorInfo typeConstructorInfo = 
                getTypeConstructorInfo (tc, typeToClassMappings, moduleToPackageMappings, module, targetPackage);
            
            
            // Now we want to generate the Java class.
            JavaDataClassGenerator javaDataClassGenerator = 
                new JavaDataClassGenerator(
                        targetPackage, 
                        typeConstructorInfo);
            
            JavaClassRep javaClass = javaDataClassGenerator.generateClassForType();
            classReps.put(tc.getName(), javaClass);
            classLogMessages.put(tc.getName(), javaDataClassGenerator.logMessages);
            
            // Generate the CAL I/O
            CAL_IO_Generator ioGenerator = 
                new CAL_IO_Generator (
                        typeConstructorInfo,
                        module, 
                        javaClass);
            
            Collection<TopLevelSourceElement> topLevelElements = ioGenerator.generateCAL_IO ();
            allTopLevelElements.addAll(topLevelElements);
            calLogMessages.addAll(ioGenerator.logMessages);
            
            mustHaveImports.addAll(ioGenerator.getInputableImports());
            castFunctions.putAll(ioGenerator.castFunctionNameToTypeSignature);
            additionalForeignTypeDeclarations.putAll(ioGenerator.additionalForeignTypeDeclarations);
        }
        
        SourceModel.ModuleDefn moduleDefn = null;
        if (allTopLevelElements.size() > 0) {
            
            ModuleName newModuleName = ModuleName.make(module.getModuleName().toString() + "_JavaIO");
            
            for (Map.Entry<String, TypeSignature> entry : castFunctions.entrySet()) {
                String castFunctionName = (String)entry.getKey();
                SourceModel.TypeSignature typeSignature = (TypeSignature)entry.getValue();
                
                TypeExprDefn domain = ((TypeExprDefn.Function)typeSignature.getTypeExprDefn()).getDomain();
                TypeExprDefn coDomain = ((TypeExprDefn.Function)typeSignature.getTypeExprDefn()).getCodomain();

                SourceModel.CALDoc.TextSegment.Plain textSegment =
                    SourceModel.CALDoc.TextSegment.Plain.make(
                            "Cast an instance of " + domain.toString() + " to " + coDomain.toString()); 
                
                SourceModel.CALDoc.TextBlock textBlock =
                    SourceModel.CALDoc.TextBlock.make(new SourceModel.CALDoc.TextSegment.TopLevel[]{textSegment});
                    
                SourceModel.CALDoc.Comment.Function functionComment = 
                    SourceModel.CALDoc.Comment.Function.make(
                            textBlock, 
                            null);
                
                SourceModel.FunctionDefn castFunction = 
                    FunctionDefn.Foreign.make(
                            functionComment,
                            castFunctionName, 
                            Scope.PRIVATE,
                            true,
                            "cast", 
                            typeSignature);
                allTopLevelElements.add(castFunction);
            }

            for (Map.Entry<QualifiedName, TypeConstructor>entry : additionalForeignTypeDeclarations.entrySet()) {
                QualifiedName typeConstructorName = (QualifiedName)entry.getKey();
                TypeConstructor typeConstructor = (TypeConstructor)entry.getValue();
                
                // Declare the java class as a foreign type.
                // ex. data foreign unsafe import jvm "org.olap.CubeType" public JCubeType;
                SourceModel.TypeConstructorDefn.ForeignType foreignType =
                    TypeConstructorDefn.ForeignType.make(
                            typeConstructorName.getUnqualifiedName(), 
                            Scope.PRIVATE, 
                            ForeignTypeInfo.getCalSourceName(typeConstructor.getForeignTypeInfo().getForeignType()), 
                            Scope.PRIVATE, 
                            null);
                allTopLevelElements.add(foreignType);
            }
            
            SourceModel.CALDoc.TextSegment.Plain textSegment =
                SourceModel.CALDoc.TextSegment.Plain.make(
                        "\nThis module, " + newModuleName + ", implements input and output\n" +
                        "functions for the type constructors contained in the " + module.getModuleName() + " module.\n" +
                        "\n" +
                        "***************************************************************************************\n\n" +
                        "NOTE:  The code in this module is automatically generated.\n" +
                        " MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE\n\n" +
                        "***************************************************************************************\n\n" +
                        "The Java classes corresponding to the type constructors are also automatically generated.\n"
                        );
            
            SourceModel.CALDoc.TextBlock textBlock =
                SourceModel.CALDoc.TextBlock.make(new SourceModel.CALDoc.TextSegment.TopLevel[]{textSegment});
                
            SourceModel.CALDoc.Comment.Module moduleComment = 
                SourceModel.CALDoc.Comment.Module.make(
                        textBlock, 
                        null);
            
            moduleDefn = 
                SourceModel.ModuleDefn.make(
                        moduleComment,
                        newModuleName, 
                        new SourceModel.Import[0], 
                        (SourceModel.TopLevelSourceElement[])allTopLevelElements.toArray(new SourceModel.TopLevelSourceElement[0]));
            
            CAL_Module_ImportGenerator<Void> importGenerator = new CAL_Module_ImportGenerator<Void>(moduleDefn, mustHaveImports);
            moduleDefn = importGenerator.generateImports(moduleDefn);
        }
        
        GeneratedIO generatedIO = 
            new GeneratedIO(
                    classReps, 
                    classLogMessages, 
                    moduleDefn, 
                    calLogMessages, 
                    ioLogMessages);
        
        return generatedIO;
    }
    
    /** 
     * Build up information about a type constructor that will be
     * used for generating Java and CAL sources.
     * 
     * @param typeConstructor
     * @param typeToClassMappings
     * @param moduleToPackageMappings
     * @param module
     * @param targetPackage
     * @return the info about the type constructor
     * @throws UnableToResolveForeignEntityException
     */
    private TypeConstructorInfo getTypeConstructorInfo (
            TypeConstructor typeConstructor, 
            Map<QualifiedName, JavaTypeName> typeToClassMappings,
            Map<ModuleName, String> moduleToPackageMappings,
            ModuleTypeInfo module,
            String targetPackage) throws UnableToResolveForeignEntityException {

        Set<FieldName> allFieldNames = new LinkedHashSet<FieldName>();
        Map<FieldName, Set<JavaTypeName>> allFieldTypes = new LinkedHashMap<FieldName, Set<JavaTypeName>>();

        int nEnumDCs = 0;
        for (int i = 0, n = typeConstructor.getNDataConstructors(); i < n; ++i) {
            DataConstructor dc = typeConstructor.getNthDataConstructor(i);
            if (dc.getArity() == 0) {
                nEnumDCs++;
            }
            
            TypeExpr[] fieldTypes = getFieldTypesForDC(dc);
            
            for (int j = 0, k = dc.getArity(); j < k; ++j) {
                FieldName fn = dc.getNthFieldName(j);
                allFieldNames.add(fn);
                
                JavaTypeName jtn = typeExprToTypeName(fieldTypes[j], typeToClassMappings, moduleToPackageMappings, targetPackage);
                Set<JavaTypeName> javaTypeNames = (Set<JavaTypeName>)allFieldTypes.get(fn);
                
                if (javaTypeNames == null) {
                    javaTypeNames = new LinkedHashSet<JavaTypeName>();
                    allFieldTypes.put(fn, javaTypeNames);
                }
                
                javaTypeNames.add(jtn);
            }
        }
        
        boolean enumDataType = nEnumDCs == typeConstructor.getNDataConstructors();
        
        
        Map<FieldName, String> fieldJavaNames = new LinkedHashMap<FieldName, String>();
        Map<FieldName, String> fieldAccessorMethodNames = new LinkedHashMap<FieldName, String>();
        Map<FieldName, Map<JavaTypeName, String>> calFieldForeignTypes = new LinkedHashMap<FieldName, Map<JavaTypeName, String>>();
        
        for (FieldName fn : allFieldNames) {
            String javaName = getJavaFieldNameFromFieldName(fn);
            fieldJavaNames.put(fn, javaName);
            
            Set<JavaTypeName> javaTypes = (Set<JavaTypeName>)allFieldTypes.get(fn);

            String updatedFieldName = javaName.substring(1);
            char[] ln = updatedFieldName.toCharArray();
            ln[0] = Character.toUpperCase(ln[0]);
            updatedFieldName = new String (ln);
            String methodName = "get" + updatedFieldName;
            fieldAccessorMethodNames.put(fn, methodName);

            Map<JavaTypeName, String> calForeignTypes = new LinkedHashMap<JavaTypeName, String>();
            calFieldForeignTypes.put(fn, calForeignTypes);
            
            for (JavaTypeName jtn : javaTypes) {
                String calForeignTypeName = getNameOfCALForeignType(jtn, module);
                calForeignTypes.put(jtn, calForeignTypeName);
            }
        }
        
        // We want to avoid a situation where the outer class and inner class have the same name
        // since in Java a nested type can't hide an enclosing type.  To avoid this we 
        // append an '_' to the outer class name.
        String javaClassName = getClassName(typeConstructor);
        if (!enumDataType) {
            for (int i = 0, n = typeConstructor.getNDataConstructors(); i < n; ++i) {
                DataConstructor dc = typeConstructor.getNthDataConstructor(i);
                String innerClassName = fixupClassName(dc.getName().getUnqualifiedName());
                if (innerClassName.equals(javaClassName)) {
                    javaClassName = javaClassName + "_";
                    break;
                }
            }
        }
        
        Set<FieldName> commonFieldNames = getCommonFieldNames(typeConstructor);
        Set<FieldName> finalCommonFieldNames = new LinkedHashSet<FieldName>();
        for (FieldName fn : commonFieldNames) {
            Set<JavaTypeName> fieldTypes = (Set<JavaTypeName>)allFieldTypes.get(fn);
            if (fieldTypes.size() == 1) {
                finalCommonFieldNames.add(fn);
            }
        }
        
        TypeConstructorInfo tci = 
            new TypeConstructorInfo(
                    typeConstructor, 
                    javaClassName, 
                    enumDataType, 
                    allFieldNames, 
                    allFieldTypes,
                    finalCommonFieldNames, 
                    fieldJavaNames, 
                    fieldAccessorMethodNames,
                    calFieldForeignTypes,
                    typeToClassMappings);
        
        for (int i = 0, n = typeConstructor.getNDataConstructors(); i < n; ++i) {
            DataConstructor dc = typeConstructor.getNthDataConstructor(i);
            DataConstructorInfo dci = getDataConstructorInfo (dc, tci, typeToClassMappings, moduleToPackageMappings, targetPackage);
            tci.dataConstructorInfo.put(dc, dci);
        }
        return tci;
    }
    
    private final DataConstructorInfo getDataConstructorInfo(
            DataConstructor dataConstructor,
            TypeConstructorInfo typeConstructorInfo,
            Map<QualifiedName, JavaTypeName> typeToClassMappings,
            Map<ModuleName, String> moduleToPackageMappings,
            String targetPackage) throws UnableToResolveForeignEntityException {
        
        Map<FieldName, String> fieldJavaAccessorMethodNames = new LinkedHashMap<FieldName, String>();
        Map<FieldName, JavaTypeName> fieldTypeNames = new LinkedHashMap<FieldName, JavaTypeName>();
        
        TypeExpr fieldTypes[] = getFieldTypesForDC(dataConstructor);
        
        boolean containsFields = false;
        
        for (int i = 0, n = dataConstructor.getArity(); i < n; ++i) {
            FieldName fn = dataConstructor.getNthFieldName(i);
            if (!typeConstructorInfo.commonFieldNames.contains(fn)) {
                containsFields = true;
            }
            
            JavaTypeName javaType = typeExprToTypeName(fieldTypes[i], typeToClassMappings, moduleToPackageMappings, targetPackage);
            fieldTypeNames.put(fn, javaType);
            
            String accessorName = (String)typeConstructorInfo.fieldJavaAccessorMethodNames.get(fn);
            fieldJavaAccessorMethodNames.put(fn, accessorName);
        }
        
        return new DataConstructorInfo(
                dataConstructor, 
                fieldJavaAccessorMethodNames, 
                fieldTypeNames,
                containsFields);
    }
    
    private String getNameOfCALForeignType (JavaTypeName javaType, ModuleTypeInfo module) throws UnableToResolveForeignEntityException {
        // First try to find a type constructor for each java type;
        TypeConstructor typeConstructor = findTypeConstructorForJavaClass(javaType, module);
        
        if (typeConstructor != null) {
            if (typeConstructor.getScope() == Scope.PUBLIC) {
                return typeConstructor.getName().toString();
            } else {
                return typeConstructor.getName().getUnqualifiedName();
            }
        } else
        if (javaType.equals(JavaTypeName.BOOLEAN)){
            return CAL_Prelude.TypeConstructors.Boolean.toString();
        } else {
            // Couldn't find a type constructor for the Java class.
            // Build up a Name.TypeCons using our naming pattern.
            
            // try to find a module containing the base CAL type.
            ModuleName moduleName = findModuleContainingType(javaType.getUnqualifiedJavaSourceName(), module);
            if (moduleName == null) {
                return "J" + javaType.getUnqualifiedJavaSourceName();
            } else {
                moduleName = ModuleName.make(moduleName.toString() + "_JavaIO");
                return moduleName.toString() + ".J" + javaType.getUnqualifiedJavaSourceName();
            }
        }
    }

    private final ModuleName findModuleContainingType (String typeName, ModuleTypeInfo module) {
        return findModuleContainingType (new LinkedHashSet<ModuleName>(), typeName, module);
    }
    
    private final ModuleName findModuleContainingType(Set<ModuleName> visitedModules, String typeName, ModuleTypeInfo module) {
        if (visitedModules.contains(module.getModuleName())) {
            return null;
        }
        
        visitedModules.add(module.getModuleName());
        
        for (int i = 0, n = module.getNTypeConstructors(); i < n; ++i) {
            if (module.getNthTypeConstructor(i).getName().getUnqualifiedName().equals(typeName)) {
                return module.getModuleName();
            }
        }
        
        for (int i = 0, n = module.getNImportedModules(); i < n; ++i) {
            ModuleTypeInfo importedModule = module.getNthImportedModule(i);
            ModuleName mn = findModuleContainingType(visitedModules, typeName, importedModule);
            if (mn != null) {
                return mn;
            }
        }
        
        return null;
    }


    /**
     * Try to find a CAL type constructor corresponding to the named java type.
     * @param javaType
     * @param module
     * @return TypeConstructor of foreign type, null if not found.
     */
    private final TypeConstructor findTypeConstructorForJavaClass (JavaTypeName javaType, ModuleTypeInfo module) throws UnableToResolveForeignEntityException {
        return findTypeConstructorForJavaClass (javaType, module, new LinkedHashSet<ModuleTypeInfo>());
    }
    
    /**
     * Try to find a CAL type constructor corresponding to the named java type.
     * @param javaType
     * @param module
     * @param visitedModules
     * @return TypeConstructor of foreign type, null if not found.
     */
    private final TypeConstructor findTypeConstructorForJavaClass (JavaTypeName javaType, ModuleTypeInfo module, Set<ModuleTypeInfo> visitedModules) throws UnableToResolveForeignEntityException {
        
        if (visitedModules.contains(module)) {
            return null;
        }
        
        visitedModules.add(module);
        
        for (int i = 0, n = module.getNTypeConstructors(); i < n; ++i) {
            TypeConstructor tc = module.getNthTypeConstructor(i);
            
            ForeignTypeInfo fti = tc.getForeignTypeInfo();
            if (fti == null) {
                continue;
            }
            
            JavaTypeName foreignType = JavaTypeName.make(fti.getForeignType());
            if (javaType.equals(foreignType)) {
                return tc;
            }
        }
        
        for (int i = 0, n = module.getNImportedModules(); i < n; ++i) {
            ModuleTypeInfo importedModule = module.getNthImportedModule(i);
            TypeConstructor tc = findTypeConstructorForJavaClass(javaType, importedModule, visitedModules);
            if (tc != null) {
                return tc;
            }
        }
        
        return null;
    }
    

    private final class CAL_IO_Generator {

        private ModuleTypeInfo module;
        private final TypeConstructorInfo typeConstructorInfo;
        private final TypeConstructor typeConstructor;
        private final JavaClassRep javaClass;
        private final Set<ModuleName> inputableImports = new LinkedHashSet<ModuleName>();
        
        /* String -> TypeSignature */
        private final Map<String, TypeSignature> 
            castFunctionNameToTypeSignature = new LinkedHashMap<String, TypeSignature>();
        
        /* QualifiedName -> TypeConstructor */
        private final Map<QualifiedName, TypeConstructor> 
            additionalForeignTypeDeclarations = new LinkedHashMap<QualifiedName, TypeConstructor>();
        
        /** List of LogRecord */
        private final List<LogRecord> logMessages = new ArrayList<LogRecord>();
        
        
        private CAL_IO_Generator (
                TypeConstructorInfo typeConstructorInfo,
                ModuleTypeInfo module, 
                JavaClassRep javaClass) {
    
            assert (module != null && typeConstructorInfo != null && javaClass != null) : "Null argument to IO_Source_Generator.generateJavaType().";
            
            this.module = module;
            this.typeConstructorInfo = typeConstructorInfo;
            this.javaClass = javaClass;
            this.typeConstructor = typeConstructorInfo.typeConstructor;
        }
       
        private void logMessage (Level level, String text) {
            logMessages.add(new LogRecord(level, text));
        }

        /**
         * 
         * @return a Collection of SourceModel.TopLevelSourceElement
         */
        Collection<TopLevelSourceElement>  generateCAL_IO () throws UnableToResolveForeignEntityException {
            
            List<TopLevelSourceElement> topLevelDefnsList = new ArrayList<TopLevelSourceElement>();
            
            // We have three different scenarios to deal with:
            // 1) The CAL type is an enum
            // 2) The CAL type has only one data constructor and is not an enum and the dat constructor has the same name as the type constructor.
            // 3) The CAL type has more than one data constructor and is not an enum.
            if (typeConstructorInfo.isEnumerationType) {
                generateEnumerationIO (topLevelDefnsList);
            } else {
                generateMultiClassIO (topLevelDefnsList);
            }
              
            
            return topLevelDefnsList;
            
        }
        
        /**
         * 
         * @param dc
         * @return QualifiedName of the make function, null if none found.
         */
        private QualifiedName findMakeDCName (DataConstructor dc) {
            TypeExpr dcTypeExpr = dc.getTypeExpr();
            String probableFunctionName = "make" + dc.getName().getUnqualifiedName();

            for (int i = 0; i < module.getNFunctions(); ++i) {
                Function function = module.getNthFunction(i);
                if (function.getName().getUnqualifiedName().equals(probableFunctionName)) {
                    if (dcTypeExpr.sameType(function.getTypeExpr())) {
                        return QualifiedName.make(module.getModuleName(), probableFunctionName);
                    }
                }
            }
            
            return null;
        }
        
        
        private void generateFieldAccessor (
                String instanceForeignTypeName, 
                FieldName fn, 
                JavaTypeName fieldType,
                String foreignFieldTypeString,
                DataConstructor dc,
                List<TopLevelSourceElement> topLevelDefns) {

            TypeExprDefn jInstanceClassTypeExpr = TypeExprDefn.TypeCons.make(Name.TypeCons.makeUnqualified(instanceForeignTypeName));

            Name.TypeCons typeConsName;
            if (foreignFieldTypeString.indexOf('.') > -1) {
                typeConsName = Name.TypeCons.make(QualifiedName.makeFromCompoundName(foreignFieldTypeString));
            } else {
                typeConsName = Name.TypeCons.makeUnqualified(foreignFieldTypeString);
            }
            
            TypeExprDefn fieldTypeForGetter = TypeExprDefn.TypeCons.make(typeConsName);

            String functionName = 
                (String)(typeConstructorInfo.calFieldAccessorFunctionNames.get(fn)).get(dc);
            String methodName = 
                (String)typeConstructorInfo.fieldJavaAccessorMethodNames.get(fn);
            
            SourceModel.CALDoc.TextSegment.Plain textSegment =
                SourceModel.CALDoc.TextSegment.Plain.make(
                        "Retrieve the " + fn.toString() + " field from an instance of " + instanceForeignTypeName + "."); 
            
            SourceModel.CALDoc.TextBlock textBlock =
                SourceModel.CALDoc.TextBlock.make(new SourceModel.CALDoc.TextSegment.TopLevel[]{textSegment});
                
            SourceModel.CALDoc.Comment.Function functionComment = 
                SourceModel.CALDoc.Comment.Function.make(
                        textBlock, 
                        null);
            
            SourceModel.FunctionDefn getter =
                FunctionDefn.Foreign.make(
                        functionComment,
                        functionName, 
                        Scope.PRIVATE, 
                        true,
                        "method " + methodName, 
                        TypeSignature.make(TypeExprDefn.Function.make(jInstanceClassTypeExpr, fieldTypeForGetter)));
            
            topLevelDefns.add(getter);

        }
        
        private void generateMultiClassIO (
            List<TopLevelSourceElement> topLevelDefnsList) throws UnableToResolveForeignEntityException {
            
            QualifiedName typeConstructorName = typeConstructorInfo.typeConstructor.getName();
            
            // Import the top level class as a foreign type.
            // ex. data foreign unsafe import jvm "org.olap.CubeType" public JCubeType;
            makeForeignClassDeclaration(topLevelDefnsList);
            
            for (int i = 0, n = typeConstructor.getNDataConstructors(); i < n; ++i) {
                DataConstructor dc = typeConstructor.getNthDataConstructor(i);
                makeForeignClassDeclaration(dc, topLevelDefnsList);
            }
            
            TypeExprDefn.TypeCons jClassTypeExpr = 
                TypeExprDefn.TypeCons.make(Name.TypeCons.makeUnqualified(typeConstructorInfo.calForeignTypeName));
            
            
            // Import the constructors for each inner class (i.e. each data constructor)
            // Pull in the constructor for the Java class.
            for (int i = 0, n = javaClass.getNInnerClasses(); i < n; ++i) {
                JavaClassRep innerClass = javaClass.getInnerClass(i);
                assert (innerClass.getNConstructors() == 1);
                JavaConstructor constructor = innerClass.getConstructor(0);
                topLevelDefnsList.add(importJavaConstructor(constructor, true));
            }
            
            // Create accessors for fields in the top level class.
            for (FieldName fn : typeConstructorInfo.commonFieldNames) {
                Set<JavaTypeName> fieldTypes = typeConstructorInfo.allFieldTypeNames.get(fn);
                
                // There will only be one type for common fields.
                Iterator<JavaTypeName> types = fieldTypes.iterator(); 
                JavaTypeName fieldType = types.next();
                
                String foreignFieldTypeSting = typeConstructorInfo.calFieldForeignTypes.get(fn).get(fieldType);
                
                generateFieldAccessor (
                        typeConstructorInfo.calForeignTypeName,
                        fn,
                        fieldType,
                        foreignFieldTypeSting,
                        typeConstructor.getNthDataConstructor(0),
                        topLevelDefnsList);
            }
            
            // Create accessors for the fields in the inner classes.
            for (int i = 0, n = typeConstructor.getNDataConstructors(); i < n; ++i) {
                DataConstructor dc = typeConstructor.getNthDataConstructor(i);
                DataConstructorInfo dcInfo = (DataConstructorInfo)typeConstructorInfo.dataConstructorInfo.get(dc);
                
                for (FieldName fn : dcInfo.allFieldNames) {
                    if (typeConstructorInfo.commonFieldNames.contains(fn)) {
                        continue;
                    }
                    
                    JavaTypeName fieldType = (JavaTypeName)dcInfo.fieldTypeNames.get(fn);
                    String foreignFieldTypeSting = typeConstructorInfo.calFieldForeignTypes.get(fn).get(fieldType);
                    generateFieldAccessor (
                            dcInfo.calForeignTypeName,
                            fn,
                            fieldType,
                            foreignFieldTypeSting,
                            dc,
                            topLevelDefnsList);
                }
            }
            
            // Bring in the 'getDCOrdinal()' method of the Java class.
            String getDCOrdinalName = "j" + javaClass.getClassName().getUnqualifiedJavaSourceName() + "_getDCOrdinal";
            SourceModel.TypeSignature getDCOrdinalTypeSignature = 
                TypeSignature.make(
                        TypeExprDefn.Function.make(
                                jClassTypeExpr, 
                                INT_TYPE_EXPR_DEFN));
            
            SourceModel.CALDoc.Comment.Function getDCOrdinalComment;
            {
                SourceModel.CALDoc.TextSegment.Plain textSegment =
                    SourceModel.CALDoc.TextSegment.Plain.make(
                            "\nRetrieve the ordinal value from an instance of " + typeConstructorInfo.calForeignTypeName + ".\n" +
                            "The ordinal can be used to determine which data constructor the " + typeConstructorInfo.calForeignTypeName + "\n" +
                            "instance corresponds to."); 
                
                SourceModel.CALDoc.TextBlock textBlock =
                    SourceModel.CALDoc.TextBlock.make(new SourceModel.CALDoc.TextSegment.TopLevel[]{textSegment});
                    
                getDCOrdinalComment = 
                    SourceModel.CALDoc.Comment.Function.make(
                            textBlock, 
                            null);
            }
            
            SourceModel.FunctionDefn getDCOrdinal =
                FunctionDefn.Foreign.make(
                        getDCOrdinalComment,
                        getDCOrdinalName, 
                        Scope.PRIVATE, 
                        true,
                        "method getDCOrdinal", 
                        getDCOrdinalTypeSignature);
            topLevelDefnsList.add(getDCOrdinal);
            
            // Create input function.
            // inputCube :: JCube -> Cube;
            // inputCube jCube = 
            //     case (jCube_getDCOrdinal JCube) of
            //     0 -> ...;
            //     1 -> ...;
            String inputFunctionName = 
                "input" + typeConstructor.getName().getUnqualifiedName();
            String inputFunctionArgName = "j" + typeConstructorInfo.calForeignTypeName.substring(1);
            Expr.Var inputFunctionArg = Expr.Var.make(Name.Function.makeUnqualified(inputFunctionArgName));

            // Do the function type declaration.
            // JCube -> Cube
            TypeExprDefn inputFunctionType =
                TypeExprDefn.Function.make(
                        TypeExprDefn.TypeCons.make(Name.TypeCons.makeUnqualified(typeConstructorInfo.calForeignTypeName)), 
                        TypeExprDefn.TypeCons.make(Name.TypeCons.makeUnqualified(typeConstructorName.getUnqualifiedName())));
            
            SourceModel.CALDoc.Comment.Function inputFunctionComment;
            {
                SourceModel.CALDoc.TextSegment.Plain textSegment =
                    SourceModel.CALDoc.TextSegment.Plain.make(
                            "\nInput an instance of " + typeConstructor.getName().getUnqualifiedName() + ".\n" +
                            "Translates an instance of " + typeConstructorInfo.calForeignTypeName + " to\n" +
                            "an instance of "+ typeConstructor.getName().getUnqualifiedName() + "."); 
                
                SourceModel.CALDoc.TextBlock textBlock =
                    SourceModel.CALDoc.TextBlock.make(new SourceModel.CALDoc.TextSegment.TopLevel[]{textSegment});
                    
                inputFunctionComment = 
                    SourceModel.CALDoc.Comment.Function.make(
                            textBlock, 
                            null);
            }
            
            SourceModel.FunctionTypeDeclaration inputFunctionTypeDecl =
                FunctionTypeDeclaration.make(
                        inputFunctionComment,
                        inputFunctionName, 
                        TypeSignature.make(inputFunctionType));
            
            topLevelDefnsList.add(inputFunctionTypeDecl);

            // build up the function body
            SourceModel.Expr condition = 
                Expr.Application.make(
                        new Expr[]{Expr.Var.make(Name.Function.makeUnqualified(getDCOrdinalName)),
                                inputFunctionArg});
            

            SourceModel.Expr.Case.Alt caseAlts[] = 
                new SourceModel.Expr.Case.Alt[typeConstructor.getNDataConstructors()];
            for (int i = 0, n = caseAlts.length; i < n; ++i) {
                DataConstructor dc = typeConstructor.getNthDataConstructor(i);
                
                // Build up an application of the data constructor.
                SourceModel.Expr dcApplication = 
                    makeDCCall(dc, inputFunctionArg);

                caseAlts[i] = 
                    Expr.Case.Alt.UnpackInt.make(
                            new BigInteger[]{BigInteger.valueOf(dc.getOrdinal())},
                            dcApplication);
            }
            
            SourceModel.Expr body = 
                Expr.Case.make(condition, caseAlts);
            
            SourceModel.FunctionDefn.Algebraic inputFunction = 
                FunctionDefn.Algebraic.make(
                        inputFunctionName, 
                        Scope.PUBLIC, 
                        new SourceModel.Parameter[]{Parameter.make(inputFunctionArgName, false)}, 
                        body);

            topLevelDefnsList.add(inputFunction);
            
            // Create an inputCubeFromJObject function.
            String inputFromJObjectFunctionName = inputFunctionName + "FromJObject";
            createInputFromJObjectFunction(inputFromJObjectFunctionName, inputFunctionName, topLevelDefnsList);
            
            // Create Inputable instance.
            SourceModel.InstanceDefn instanceDefn = 
                InstanceDefn.make(
                        Name.TypeClass.make(CAL_Prelude.TypeClasses.Inputable), 
                        InstanceDefn.InstanceTypeCons.TypeCons.make(Name.TypeCons.make(typeConstructorName), null), 
                        null, 
                        new InstanceDefn.InstanceMethod[]{
                            InstanceDefn.InstanceMethod.make(CAL_Prelude.Functions.input.getUnqualifiedName(), Name.Function.makeUnqualified(inputFromJObjectFunctionName))
                        });
            topLevelDefnsList.add(instanceDefn);
            
            
            // Now we want to make the outputable instance.
            // Build up a function body that converts 
            // from 'Cube' to 'JCube'.

            Expr.Var dcInstance = Expr.Var.makeUnqualified("dcInstance"); 

            caseAlts = 
                new SourceModel.Expr.Case.Alt[typeConstructor.getNDataConstructors()];
            for (int i = 0, n = caseAlts.length; i < n; ++i) {
                DataConstructor dc = typeConstructor.getNthDataConstructor(i);
                
                SourceModel.Pattern patterns[] = new SourceModel.Pattern[dc.getArity()];
                Arrays.fill(patterns, SourceModel.Pattern.Wildcard.make());
                
                caseAlts[i] =
                    Expr.Case.Alt.UnpackDataCons.make(
                            Name.DataCons.make(dc.getName()),
                            patterns,
                            makeConstructorCall(dc, dcInstance, true));
            }

            SourceModel.Expr.Case conversionFunctionBody = 
                Expr.Case.make(dcInstance, caseAlts);

            makeOutputableInstance(typeConstructor, conversionFunctionBody, topLevelDefnsList);

        }
        
        private void makeOutputableInstance (
                TypeConstructor typeConstructor, 
                SourceModel.Expr outputFunctionBody, 
                List<TopLevelSourceElement> topLevelDefnsList) {
            
            // Create an output function.
            // outputCube :: Cube -> JCube;
            // public outputCube dcInstance = 
            //     case dcInstanceOf
            //     DC1 -> jDC1_new dcInstance.DC1.field1 (jObjectToJ... (output (dcInstance.DC1.field2)));
            //     ...
            //
            // outputCubeToJObject :: Cube -> JObject;
            // private outputCubeToJObject dcInstance = 
            //     output (outputCube dcInstance);
            String outputFunctionName = 
                "output" + typeConstructor.getName().getUnqualifiedName();

            // Do the function type declaration.
            TypeExprDefn outputFunctionType =
                TypeExprDefn.Function.make(
                        TypeExprDefn.TypeCons.make(Name.TypeCons.makeUnqualified(typeConstructorInfo.typeConstructor.getName().getUnqualifiedName())),
                        TypeExprDefn.TypeCons.make(Name.TypeCons.makeUnqualified(typeConstructorInfo.calForeignTypeName)));
            
            SourceModel.CALDoc.Comment.Function outputFunctionComment;
            {
                SourceModel.CALDoc.TextSegment.Plain textSegment =
                    SourceModel.CALDoc.TextSegment.Plain.make(
                            "\nOutput an instance of " + typeConstructor.getName().getUnqualifiedName() + ".\n" +
                            "Translates an instance of " + typeConstructor.getName().getUnqualifiedName() + " to\n" +
                            "an instance of "+ typeConstructorInfo.calForeignTypeName + "."); 
                
                SourceModel.CALDoc.TextBlock textBlock =
                    SourceModel.CALDoc.TextBlock.make(new SourceModel.CALDoc.TextSegment.TopLevel[]{textSegment});
                    
                outputFunctionComment = 
                    SourceModel.CALDoc.Comment.Function.make(
                            textBlock, 
                            null);
            }
            
            SourceModel.FunctionTypeDeclaration outputFunctionTypeDecl =
                FunctionTypeDeclaration.make(
                        outputFunctionComment,
                        outputFunctionName, 
                        TypeSignature.make(outputFunctionType));
            
            topLevelDefnsList.add(outputFunctionTypeDecl);
            
            SourceModel.FunctionDefn.Algebraic outputFunction = 
                FunctionDefn.Algebraic.make(
                        outputFunctionName, 
                        Scope.PUBLIC, 
                        new SourceModel.Parameter[]{Parameter.make("dcInstance", true)}, 
                        outputFunctionBody);
            topLevelDefnsList.add(outputFunction);
            
            // Now create the conversion function.  i.e. a private function that outputs to a JObject.
            String conversionFunctionName = outputFunctionName + "ToJObject";
            // Do the function type declaration.
            TypeExprDefn conversionFunctionType =
                TypeExprDefn.Function.make(
                        TypeExprDefn.TypeCons.make(Name.TypeCons.makeUnqualified(typeConstructorInfo.typeConstructor.getName().getUnqualifiedName())),
                        JOBJECT_TYPE_EXPR_DEFN);
            
            SourceModel.CALDoc.Comment.Function conversionFunctionComment;
            {
                SourceModel.CALDoc.TextSegment.Plain textSegment =
                    SourceModel.CALDoc.TextSegment.Plain.make(
                            "\nOutput an instance of " + typeConstructor.getName().getUnqualifiedName() + ".\n" +
                            "Translates an instance of " + typeConstructor.getName().getUnqualifiedName() + " to\n" +
                            "an instance of JObject."); 
                
                SourceModel.CALDoc.TextBlock textBlock =
                    SourceModel.CALDoc.TextBlock.make(new SourceModel.CALDoc.TextSegment.TopLevel[]{textSegment});
                    
                conversionFunctionComment = 
                    SourceModel.CALDoc.Comment.Function.make(
                            textBlock, 
                            null);
            }
            
            SourceModel.FunctionTypeDeclaration conversionFunctionTypeDecl =
                FunctionTypeDeclaration.make(
                        conversionFunctionComment,
                        conversionFunctionName, 
                        TypeSignature.make(conversionFunctionType));
            
            topLevelDefnsList.add(conversionFunctionTypeDecl);

            SourceModel.Expr conversionFunctionBody =
                SourceModel.Expr.Application.make(
                        new SourceModel.Expr[]{
                                SourceModel.Expr.Var.make(Name.Function.makeUnqualified(outputFunctionName)),
                                SourceModel.Expr.Var.make(Name.Function.makeUnqualified("dcInstance"))
                        });

            conversionFunctionBody =
                SourceModel.Expr.Application.make(
                        new SourceModel.Expr[]{
                                SourceModel.Expr.Var.make(Name.Function.make(CAL_Prelude.Functions.output)),
                                conversionFunctionBody
                        });

            // Now do the body of the conversion function.
            SourceModel.FunctionDefn.Algebraic conversionFunction = 
                FunctionDefn.Algebraic.make(
                        conversionFunctionName, 
                        Scope.PRIVATE, 
                        new SourceModel.Parameter[]{Parameter.make("dcInstance", true)}, 
                        conversionFunctionBody);
            topLevelDefnsList.add(conversionFunction);

            // Create Outputable instance.
            SourceModel.InstanceDefn outputInstanceDefn = 
                InstanceDefn.make(
                        Name.TypeClass.make(CAL_Prelude.TypeClasses.Outputable), 
                        InstanceDefn.InstanceTypeCons.TypeCons.make(Name.TypeCons.make(typeConstructorInfo.typeConstructor.getName()), null), 
                        null, 
                        new InstanceDefn.InstanceMethod[]{
                            InstanceDefn.InstanceMethod.make(CAL_Prelude.Functions.output.getUnqualifiedName(), Name.Function.makeUnqualified(conversionFunctionName))
                        });
            topLevelDefnsList.add(outputInstanceDefn);

        }
        
        private SourceModel.Expr makeConstructorCall (
                DataConstructor dc, 
                SourceModel.Expr.Var dcVar,
                boolean isInnerClass) throws UnableToResolveForeignEntityException {
            
            SourceModel.Name.DataCons dataConsSourceModelName = Name.DataCons.make(dc.getName());
            
            String javaConstructorName = fixupClassName(dc.getName().getUnqualifiedName());
            String calConstructorName = "j" + javaConstructorName + "_new";
            
            TypeExpr[] dcFieldTypeExprs = getFieldTypesForDC(dc);

            JavaConstructor javaConstructor = null;
            if (isInnerClass) {
                for (int i = 0, n = javaClass.getNInnerClasses(); i < n; ++i) {
                    JavaClassRep innerClass = javaClass.getInnerClass(i);
                    if (innerClass.getClassName().getUnqualifiedJavaSourceName().endsWith("." + javaConstructorName)) {
                        javaConstructor = innerClass.getConstructor(0);
                        break;
                    }
                }
            } else {
                javaConstructor = javaClass.getConstructor(0);
            }
            
            if (javaConstructor == null) {
                throw new NullPointerException ("Unable to find Java constructor for data constructor " + dc.getName());
            }
            
            int nArgs = javaConstructor.getNParams();
            
            if (nArgs == 0) {
                return SourceModel.Expr.Var.makeUnqualified(calConstructorName);
            }
            
            TypeExprDefn argTypes[] = new TypeExprDefn[nArgs];
            for (int i = 0; i < nArgs; ++i) {
                argTypes[i] = getTypeExprDefn(new JavaTypeName[]{javaConstructor.getParamType(i)});
            }

            SourceModel.Expr applicationExpressions[] = new SourceModel.Expr[nArgs+ 1];
            SourceModel.Expr.Var output = Expr.Var.make(CAL_Prelude.Functions.output);

            applicationExpressions[0] = SourceModel.Expr.Var.makeUnqualified(calConstructorName);

            Expr.Var unsafeCoerce = Expr.Var.make(CAL_Prelude.Functions.unsafeCoerce);
            
            for (int i = 0, n = dc.getArity(); i < n; ++i) {
                FieldName fn = dc.getNthFieldName(i);
                
                SourceModel.Expr argValue = Expr.SelectDataConsField.make(dcVar, dataConsSourceModelName, SourceModel.Name.Field.make(fn));
                
                // If the type of the dc field matches the type of the constructor argument
                // we can simply use the field accessor.  Otherwise we need to apply output to 
                // resulting value.
                TypeExpr fieldTypeExpr = dcFieldTypeExprs[i];
                updateInputableImports(fieldTypeExpr);
                String dcFieldTypeConsName = "";
                TypeConsApp typeConsApp = fieldTypeExpr.rootTypeConsApp();
                if (typeConsApp != null) {
                    TypeConstructor tc = typeConsApp.getRoot();
                    dcFieldTypeConsName = tc.getName().toString();
                }

                String constructorArgTypeConsName = ((TypeExprDefn.TypeCons)argTypes[i]).getTypeConsName().toString();

                
                if (!constructorArgTypeConsName.equals(dcFieldTypeConsName)) {
                    // We don't need to apply 'output' if the result type of the field accessor is CALValue
                    if (!constructorArgTypeConsName.equals(CAL_Prelude.TypeConstructors.CalValue.getQualifiedName())) {
                        argValue = Expr.Application.make(new Expr[]{output, argValue});

                        // We want to cast the result
                        String castFunctionName = makeCastFunctionFromJObject(((TypeExprDefn.TypeCons)argTypes[i]).getTypeConsName().getUnqualifiedName());
                        
                        argValue = Expr.Application.make(new Expr[]{Expr.Var.make(Name.Function.makeUnqualified(castFunctionName)), argValue});
                    }
                    else {
                        argValue = Expr.Application.make(new Expr[]{unsafeCoerce, argValue});
                    }
                }
                
                applicationExpressions[i+1] = argValue; 
            }

            
            return SourceModel.Expr.Application.make(applicationExpressions);
        }
        
        
        private SourceModel.Expr makeDCCall (
                DataConstructor dc,
                Expr.Var argCastToTopLevelJType
                ) throws UnableToResolveForeignEntityException {
            
            // We want to generate something along the lines of:
            // let
            //     jInnerClassName = jObjectToJInnerClassName jObject;
            // in
            //     DataConstructorName
            //         input field 1...
            
            DataConstructorInfo dcInfo = (DataConstructorInfo)typeConstructorInfo.dataConstructorInfo.get(dc);
            
            // Build up the values for each field in the data constructor.
            SourceModel.Expr dcApplicationExpressions[] = new SourceModel.Expr[dc.getArity() + 1];
            
            QualifiedName makeFunctionName = findMakeDCName(dc);
            if (makeFunctionName != null) {
                dcApplicationExpressions[0] = SourceModel.Expr.Var.make (Name.Function.make(makeFunctionName));
                logMessage(Level.INFO, "Using function " + makeFunctionName.getUnqualifiedName() + " instead of data constructor " + dc.getName().getUnqualifiedName());
            } else {
                dcApplicationExpressions[0] = SourceModel.Expr.DataCons.make(dc.getName());
            }
            TypeExpr[] dcFieldTypes = getFieldTypesForDC(dc);

            SourceModel.Expr.Var input = Expr.Var.make(CAL_Prelude.Functions.input);
            
            Expr unsafeCoerce = Expr.Var.make(Name.Function.make(CAL_Prelude.Functions.unsafeCoerce));

            // We need a cast function from JObject to the foreign inner class if there are fields
            // in the inner class.
            SourceModel.LocalDefn localDefns[] = null;
            Expr.Var argCastToInnerJType = null;
            if (dcInfo.containsFields) {
                // We want to create a let var that is the cast of the
                // JObject parameter to the J... foreign type.
                String letVarName = "j" + dcInfo.calForeignTypeName.substring(1);
                
                localDefns = 
                    makeOuterForeignTypeToInnerForeignTypeLetVar(
                            argCastToTopLevelJType.getVarName().getUnqualifiedName(), 
                            typeConstructorInfo.calForeignTypeName, 
                            letVarName, 
                            dcInfo.calForeignTypeName);
                
                argCastToInnerJType = Expr.Var.makeUnqualified(letVarName);
            }
            
            
            for (int i = 0, n = dc.getArity(); i < n; ++i) {
                FieldName fn = dc.getNthFieldName(i);
                JavaTypeName fieldType = (JavaTypeName)dcInfo.fieldTypeNames.get(fn);
                String fieldAccessorName = typeConstructorInfo.calFieldAccessorFunctionNames.get(fn).get(dc);
                
                // If the type of the retrieved field matches the type of the dc field
                // we can simply use the field accessor.  Otherwise we need to apply input to 
                // resulting value.
                SourceModel.Expr.Var accessor = Expr.Var.make(Name.Function.makeUnqualified(fieldAccessorName));
                SourceModel.Expr accessApplication;
                
                if (typeConstructorInfo.commonFieldNames.contains(fn)) {
                    accessApplication = 
                        Expr.Application.make(new SourceModel.Expr[]{accessor, argCastToTopLevelJType});
                } else {
                    accessApplication = 
                        Expr.Application.make(new SourceModel.Expr[]{accessor, argCastToInnerJType});
                }
                
                TypeExpr fieldTypeExpr = dcFieldTypes[i];
                updateInputableImports(fieldTypeExpr);
                
                TypeExprDefn fieldTypeForGetter = getTypeExprDefn(new JavaTypeName[]{fieldType});
                String fieldTypeForGetterTypeConsName = ((TypeExprDefn.TypeCons)fieldTypeForGetter).getTypeConsName().toString();
                String dcFieldTypeConsName = "";
                TypeConsApp typeConsApp = fieldTypeExpr.rootTypeConsApp();
                if (typeConsApp != null) {
                    TypeConstructor tc = typeConsApp.getRoot();
                    dcFieldTypeConsName = tc.getName().toString();
                }
                
                if (!fieldTypeForGetterTypeConsName.equals(dcFieldTypeConsName)) {
                    // apply input and usafe coerce.
                    
                    // We don't need to apply 'input' if the result type of the field accessor is CALValue
                    if (!fieldTypeForGetterTypeConsName.equals(CAL_Prelude.TypeConstructors.CalValue.getQualifiedName())) {
                        String castFunctionName = makeCastFunctionToJobject(((TypeExprDefn.TypeCons)fieldTypeForGetter).getTypeConsName().getUnqualifiedName());
                            
                        accessApplication = Expr.Application.make(new Expr[]{input, Expr.Application.make(new Expr[]{Expr.Var.make(Name.Function.makeUnqualified(castFunctionName)), accessApplication})});
                    } else {
                        // Inputing to a function type.  Use unsafeCoerce.
                        accessApplication = Expr.Application.make(new Expr[]{unsafeCoerce, accessApplication});
                        
                    }
                }
                
                accessApplication = Expr.Application.make(new Expr[]{Expr.Var.make(Name.Function.make(CAL_Prelude.Functions.eager)), accessApplication});
                
                dcApplicationExpressions[i+1] = accessApplication; 
            }
            
            // Now apply the data constructor to each of the arguments.
            SourceModel.Expr dcApplication;
            if (dcApplicationExpressions.length >= 2) {
                dcApplication = Expr.Application.make(dcApplicationExpressions);
            } else {
                dcApplication = dcApplicationExpressions[0];
            }

            if (localDefns != null) {
                dcApplication = SourceModel.Expr.Let.make(localDefns, dcApplication);    
            }
            
            return dcApplication;
        }
        
        
        private SourceModel.FunctionDefn importJavaConstructor (JavaConstructor constructor, boolean innerClass) throws UnableToResolveForeignEntityException {
            int nArgs = constructor.getNParams();
            JavaTypeName argTypes[] = new JavaTypeName[nArgs + 1];
            for (int i = 0; i < nArgs; ++i) {
                argTypes[i] = constructor.getParamType(i);
            }
            argTypes[nArgs] = javaClass.getClassName();
                
            TypeExprDefn argTypeDef = getTypeExprDefn(argTypes);
            TypeSignature declaredType = TypeSignature.make(argTypeDef);
            
            SourceModel.CALDoc.Comment.Function constructorFunctionComment;
            {
                SourceModel.CALDoc.TextSegment.Plain textSegment =
                    SourceModel.CALDoc.TextSegment.Plain.make(
                            "Constructor for " + constructor.getConstructorName() + "."); 
                
                SourceModel.CALDoc.TextBlock textBlock =
                    SourceModel.CALDoc.TextBlock.make(new SourceModel.CALDoc.TextSegment.TopLevel[]{textSegment});
                    
                constructorFunctionComment = 
                    SourceModel.CALDoc.Comment.Function.make(
                            textBlock, 
                            null);
            }
                        
            SourceModel.FunctionDefn jConstructor =
                FunctionDefn.Foreign.make(
                        constructorFunctionComment,
                        "j" + constructor.getConstructorName() + "_new", 
                        Scope.PUBLIC, 
                        true,
                        "constructor " + javaClass.getClassName().getFullJavaSourceName() + (innerClass ? "$" + constructor.getConstructorName() : ""), 
                        declaredType);
            
            return jConstructor;
        }
            
        private String makeCastFunctionToJobject (String foreignTypeName) {
            String castFunctionName = Character.toLowerCase(foreignTypeName.charAt(0)) + foreignTypeName.substring(1) + "ToJObject";
            
            if (castFunctionNameToTypeSignature.get(castFunctionName) == null) {
                SourceModel.TypeSignature castFunctionTypeSignature =
                    TypeSignature.make(
                            TypeExprDefn.Function.make(
                                    TypeExprDefn.TypeCons.make(
                                            Name.TypeCons.makeUnqualified(foreignTypeName)), 
                                            JOBJECT_TYPE_EXPR_DEFN));
                
                castFunctionNameToTypeSignature.put(castFunctionName, castFunctionTypeSignature);
            }
            
            return castFunctionName;
        }
        
        private String makeCastFunction (String fromForeignTypeName, String toForeignTypeName) {
            String castFunctionName = "cast" + fromForeignTypeName + "To" + toForeignTypeName;

            if (castFunctionNameToTypeSignature.get(castFunctionName) == null) {
                SourceModel.TypeSignature castFunctionTypeSignature =
                    TypeSignature.make(
                            TypeExprDefn.Function.make(
                                    TypeExprDefn.TypeCons.make(
                                            Name.TypeCons.makeUnqualified(fromForeignTypeName)),
                                    TypeExprDefn.TypeCons.make(
                                            Name.TypeCons.makeUnqualified(toForeignTypeName))));
    
                castFunctionNameToTypeSignature.put(castFunctionName, castFunctionTypeSignature);
            }
            
            return castFunctionName;
        }
        
        private String makeCastFunctionFromJObject (String foreignTypeName) {
            String castFunctionName = "jObjectTo" + foreignTypeName;

            if (castFunctionNameToTypeSignature.get(castFunctionName) == null) {
                SourceModel.TypeSignature castFunctionTypeSignature =
                    TypeSignature.make(
                            TypeExprDefn.Function.make(
                                    JOBJECT_TYPE_EXPR_DEFN,
                                    TypeExprDefn.TypeCons.make(
                                            Name.TypeCons.makeUnqualified(foreignTypeName))));
    
                castFunctionNameToTypeSignature.put(castFunctionName, castFunctionTypeSignature);
            }
            
            return castFunctionName;
        }
        
        /**
         * Create a LocalDefn for a let var cast from an existing var.
         * @param outerTypeVarName
         * @param outerForeignTypeName
         * @param letVarName
         * @param innerForeignTypeName
         * @return the local definition
         */
        private SourceModel.LocalDefn[] 
            makeOuterForeignTypeToInnerForeignTypeLetVar (
                    String outerTypeVarName,
                    String outerForeignTypeName,
                    String letVarName, 
                    String innerForeignTypeName) {
            
            SourceModel.LocalDefn[] localDefns = new SourceModel.LocalDefn[2];

            TypeExprDefn foreignTypeExprDefn =
                TypeExprDefn.TypeCons.make(Name.TypeCons.makeUnqualified(innerForeignTypeName));
            
            SourceModel.CALDoc.TextSegment.Plain textSegment =
                SourceModel.CALDoc.TextSegment.Plain.make(
                        "Cast the " + outerForeignTypeName + " value to a " + innerForeignTypeName);
            
            SourceModel.CALDoc.TextBlock description =
                SourceModel.CALDoc.TextBlock.make(new SourceModel.CALDoc.TextSegment.TopLevel[]{textSegment});

            CALDoc.Comment.Function caldocComment =
                CALDoc.Comment.Function.make(
                        description, 
                        null);
            
            SourceModel.LocalDefn.Function.TypeDeclaration localTypeDecl =
                LocalDefn.Function.TypeDeclaration.make(
                        caldocComment, 
                        letVarName, 
                        TypeSignature.make(foreignTypeExprDefn));
            
            
            String castFunctionName = makeCastFunction(outerForeignTypeName, innerForeignTypeName);

            Expr letVarDef = 
                Expr.Application.make(
                        new Expr[]{
                                Expr.Var.make(Name.Function.makeUnqualified(castFunctionName)), 
                                Expr.Var.makeUnqualified(outerTypeVarName)});
            
            letVarDef = 
                Expr.Application.make(
                        new Expr[]{
                                Expr.Var.make(CAL_Prelude.Functions.eager), 
                                letVarDef});
            
            SourceModel.LocalDefn.Function.Definition localDefnFctn =
                LocalDefn.Function.Definition.make(letVarName, null, letVarDef);
            
            localDefns[0] = localTypeDecl;
            localDefns[1] = localDefnFctn;
            return localDefns;
        }
        
        /**
         * Build up function type. a -> b -> c ...
         * @param javaTypes
         * @return a TypeExprDefn
         */
        private SourceModel.TypeExprDefn getTypeExprDefn (JavaTypeName[] javaTypes) throws UnableToResolveForeignEntityException {
            // First try to find a type constructor for each java type;
            TypeConstructor typeConstructors[] = new TypeConstructor[javaTypes.length];
            findTypeConstructorForJavaClass(javaTypes, typeConstructors);
            
            Name.TypeCons typeConstructorNames[] = new Name.TypeCons[javaTypes.length];
            for (int i = 0, n = javaTypes.length; i < n; ++i) {
                if (typeConstructors[i] != null) {
                    if (typeConstructors[i].getScope().equals(Scope.PUBLIC)) {
                        typeConstructorNames[i] = Name.TypeCons.make(typeConstructors[i].getName());
                    } else {
                        // Need to reproduce this foreign type declaration
                        // in this module.
                        additionalForeignTypeDeclarations.put(typeConstructors[i].getName(), typeConstructors[i]);
                        typeConstructorNames[i] = Name.TypeCons.makeUnqualified(typeConstructors[i].getName().getUnqualifiedName());
                    }
                } else
                if (javaTypes[i].equals(JavaTypeName.BOOLEAN)){
                    typeConstructorNames[i] = Name.TypeCons.make(CAL_Prelude.TypeConstructors.Boolean);
                } else {
                    // Couldn't find a type constructor for the Java class.
                    // Build up a Name.TypeCons using our naming pattern.
                    
                    // try to find a module containing the base CAL type.
                    String baseCALTypeName = javaTypes[i].getUnqualifiedJavaSourceName();
                    if (baseCALTypeName.endsWith("_")) {
                        baseCALTypeName = baseCALTypeName.substring(0, baseCALTypeName.length()-1);
                    }
                    ModuleName moduleName = findModuleContainingType(baseCALTypeName);
                    if (moduleName == null) {
                        typeConstructorNames[i] = Name.TypeCons.makeUnqualified("J" + javaTypes[i].getUnqualifiedJavaSourceName());
                    } else {
                        moduleName = ModuleName.make(moduleName.toString() + "_JavaIO");
                        typeConstructorNames[i] = Name.TypeCons.make(moduleName, "J" + javaTypes[i].getUnqualifiedJavaSourceName());
                    }
                }
            }
            
            TypeExprDefn codomain = TypeExprDefn.TypeCons.make(typeConstructorNames[typeConstructorNames.length - 1]);
           
            for (int i = typeConstructorNames.length - 2; i >= 0; i--) {
                TypeExprDefn domain = TypeExprDefn.TypeCons.make(typeConstructorNames[i]);
                codomain = TypeExprDefn.Function.make(domain, codomain);
            }
            
            return codomain;
        }
        
        private final void updateInputableImports (TypeExpr typeExpr) {
            TypeConsApp tca = typeExpr.rootTypeConsApp();
            if (tca != null) {
                for (int i = 0, n = tca.getNArgs(); i < n; ++i) {
                    updateInputableImports(tca.getArg(i));
                }
            }
            
            if (typeExpr.isListType()) {
                if (typeExpr.rootTypeConsApp() == null) {
                    ((TypeApp)typeExpr).getOperandType();
                }
            } else if (typeExpr.isTupleType()) {
                RecordType rt = (RecordType)typeExpr;
                Map<FieldName, TypeExpr> hasFieldsMap = rt.getHasFieldsMap();
                for (Iterator<TypeExpr> it = hasFieldsMap.values().iterator(); it.hasNext();) {
                    updateInputableImports(it.next());
                }
            } else if (!typeExpr.isFunctionType()) {
                if (tca != null) {
                    TypeConstructor tc = tca.getRoot();
                    // Ignore functions
                    ModuleTypeInfo m = findModuleWithClassInstance(CAL_Prelude.TypeClasses.Inputable, tc.getName());
                    if (m != null) {
                        inputableImports.add(m.getModuleName());
                    } else if (!tc.getName().getModuleName().equals(module.getModuleName())){
                        // Create a module name based on our naming.
                        ModuleName mn = 
                            ModuleName.make(
                                    tc.getName().getModuleName().toString() + "_JavaIO");
                        inputableImports.add(mn);
                    }
                }                
            }
            
        }

        private final ModuleTypeInfo findModuleWithClassInstance (QualifiedName className, QualifiedName typeConstructorName) {
            return findModuleWithClassInstance (className, typeConstructorName, module, new LinkedHashSet<ModuleTypeInfo>());
        }
        
        private final ModuleTypeInfo findModuleWithClassInstance (QualifiedName className, QualifiedName typeConstructorName, ModuleTypeInfo module, Set<ModuleTypeInfo> visitedModules)  {
            if (visitedModules.contains(module)) {
                return null;
            }
            visitedModules.add(module);
            
            for (int i = 0, n = module.getNClassInstances(); i < n; ++i) {
                ClassInstance ci = module.getNthClassInstance(i);
                if (ci.getTypeClass().getName().equals(className)) {
                    // Check the instance type
                    TypeExpr instanceTypeExpr = ci.getType();
                    TypeConsApp tca = instanceTypeExpr.rootTypeConsApp();
                    if (tca != null) {
                        if (tca.getRoot().getName().equals(typeConstructorName)) {
                            return module;
                        }
                    }
                }
            }

            for (int i = 0, n = module.getNImportedModules(); i < n; ++i) {
                ModuleTypeInfo mti = module.getNthImportedModule(i);
                ModuleTypeInfo ci = findModuleWithClassInstance(className, typeConstructorName, mti, visitedModules);
                if (ci != null) {
                    return ci;
                }
            }
            
            return null;
        }        
        
        private final ModuleName findModuleContainingType (String typeName) {
            return findModuleContainingType (new LinkedHashSet<ModuleName>(), typeName, module);
        }
        
        private final ModuleName findModuleContainingType(Set<ModuleName> visitedModules, String typeName, ModuleTypeInfo module) {
            if (visitedModules.contains(module.getModuleName())) {
                return null;
            }
            
            visitedModules.add(module.getModuleName());
            
            for (int i = 0, n = module.getNTypeConstructors(); i < n; ++i) {
                if (module.getNthTypeConstructor(i).getName().getUnqualifiedName().equals(typeName)) {
                    return module.getModuleName();
                }
            }
            
            for (int i = 0, n = module.getNImportedModules(); i < n; ++i) {
                ModuleTypeInfo importedModule = module.getNthImportedModule(i);
                ModuleName mn = findModuleContainingType(visitedModules, typeName, importedModule);
                if (mn != null) {
                    return mn;
                }
            }
            
            return null;
        }
        
        /**
         * Try to find a CAL type constructor corresponding to the named java type.
         * @param javaTypes
         * @param typeConstructors
         */
        private void findTypeConstructorForJavaClass (JavaTypeName[] javaTypes, TypeConstructor[] typeConstructors) throws UnableToResolveForeignEntityException {
            findTypeConstructorForJavaClass (javaTypes, typeConstructors, module, new LinkedHashSet<ModuleTypeInfo>());
        }
        
        /**
         * Try to find a CAL type constructor corresponding to the named java type.
         * @param javaTypes
         * @param typeConstructors
         * @param module
         * @param visitedModules
         */
        private void findTypeConstructorForJavaClass (JavaTypeName[] javaTypes, TypeConstructor[] typeConstructors, ModuleTypeInfo module, Set<ModuleTypeInfo> visitedModules) throws UnableToResolveForeignEntityException {
            if (visitedModules.contains(module)) {
                return;
            }
            visitedModules.add(module);
            
            for (int i = 0, n = module.getNTypeConstructors(); i < n; ++i) {
                TypeConstructor tc = module.getNthTypeConstructor(i);
                ForeignTypeInfo fti = tc.getForeignTypeInfo();
                if (fti == null) {
                    continue;
                }
                
                JavaTypeName foreignType = JavaTypeName.make(fti.getForeignType());
                for (int j = 0, k = javaTypes.length; j < k; ++j) {
                    if (typeConstructors[j] != null) {
                        continue;
                    }
                    if (foreignType.equals(javaTypes[j])) {
                        typeConstructors[j] = tc;
                    }
                }
            }
            
            for (int i = 0, n = module.getNImportedModules(); i < n; ++i) {
                ModuleTypeInfo importedModule = module.getNthImportedModule(i);
                findTypeConstructorForJavaClass(javaTypes, typeConstructors, importedModule, visitedModules);
            }
        }
        
        private void makeForeignClassDeclaration (List<TopLevelSourceElement> topLevelDefnsList) {
            // Declare the java class as a foreign type.
            // ex. data foreign unsafe import jvm "org.olap.CubeType" public JCubeType;
            
            SourceModel.CALDoc.Comment.TypeCons foreignTypeComment;
            {
                SourceModel.CALDoc.TextSegment.Plain textSegment =
                    SourceModel.CALDoc.TextSegment.Plain.make(
                            "\nForeign type for " + javaClass.getClassName().getFullJavaSourceName() + ".\n" +
                            typeConstructor.getName() + " outputs to and inputs from this type.\n"); 
                
                SourceModel.CALDoc.TextBlock textBlock =
                    SourceModel.CALDoc.TextBlock.make(new SourceModel.CALDoc.TextSegment.TopLevel[]{textSegment});
                    
                foreignTypeComment = 
                    SourceModel.CALDoc.Comment.TypeCons.make(
                            textBlock, 
                            null);
            }

            SourceModel.TypeConstructorDefn.ForeignType foreignType =
                TypeConstructorDefn.ForeignType.make(
                        foreignTypeComment,
                        typeConstructorInfo.calForeignTypeName, 
                        Scope.PUBLIC, 
                        true,
                        javaClass.getClassName().getFullJavaSourceName(), 
                        Scope.PUBLIC,
                        true,
                        new Name.TypeClass[]{Name.TypeClass.make(CAL_Prelude.TypeClasses.Inputable), Name.TypeClass.make(CAL_Prelude.TypeClasses.Outputable)});

            topLevelDefnsList.add (foreignType);

        }
        
        private void makeForeignClassDeclaration (DataConstructor dc, List<TopLevelSourceElement> topLevelDefnsList) {
            
            DataConstructorInfo dcInfo = (DataConstructorInfo)typeConstructorInfo.dataConstructorInfo.get(dc);
            
            SourceModel.CALDoc.Comment.TypeCons foreignTypeComment;
            {
                SourceModel.CALDoc.TextSegment.Plain textSegment =
                    SourceModel.CALDoc.TextSegment.Plain.make(
                            "\nForeign type for " + javaClass.getClassName().getFullJavaSourceName() + "." + dcInfo.innerClassName + ".\n" +
                            "This Java class corresponds to the " + dc.getName().getUnqualifiedName() + " data constructor.\n"); 
                
                SourceModel.CALDoc.TextBlock textBlock =
                    SourceModel.CALDoc.TextBlock.make(new SourceModel.CALDoc.TextSegment.TopLevel[]{textSegment});
                    
                foreignTypeComment = 
                    SourceModel.CALDoc.Comment.TypeCons.make(
                            textBlock, 
                            null);
            }
            
            // Declare the java class as a foreign type.
            // ex. data foreign unsafe import jvm "org.olap.CubeType" public JCubeType;
            SourceModel.TypeConstructorDefn.ForeignType foreignType =
                TypeConstructorDefn.ForeignType.make(
                        foreignTypeComment,
                        dcInfo.calForeignTypeName, 
                        Scope.PUBLIC, 
                        true,
                        javaClass.getClassName().getFullJavaSourceName() + "$" + dcInfo.innerClassName, 
                        Scope.PUBLIC, 
                        true,
                        new Name.TypeClass[]{Name.TypeClass.make(CAL_Prelude.TypeClasses.Inputable), Name.TypeClass.make(CAL_Prelude.TypeClasses.Outputable)});

            topLevelDefnsList.add (foreignType);
        }
        
        private void importGetDCOrdinal (List<TopLevelSourceElement> topLevelDefnsList) {
            // Bring in the 'getDCOrdinal()' method of the Java class.
            SourceModel.TypeSignature getDCOrdinalTypeSignature = 
                TypeSignature.make(
                        TypeExprDefn.Function.make(
                                TypeExprDefn.TypeCons.make(Name.TypeCons.makeUnqualified(typeConstructorInfo.calForeignTypeName)), 
                                INT_TYPE_EXPR_DEFN));
            
            SourceModel.CALDoc.Comment.Function getDCOrdinalComment;
            {
                SourceModel.CALDoc.TextSegment.Plain textSegment =
                    SourceModel.CALDoc.TextSegment.Plain.make(
                            "\nRetrieve the ordinal value from an instance of " + typeConstructorInfo.calForeignTypeName + ".\n" +
                            "The ordinal can be used to determine which data constructor the " + typeConstructorInfo.calForeignTypeName + "\n" +
                            "instance corresponds to."); 
                
                SourceModel.CALDoc.TextBlock textBlock =
                    SourceModel.CALDoc.TextBlock.make(new SourceModel.CALDoc.TextSegment.TopLevel[]{textSegment});
                    
                getDCOrdinalComment = 
                    SourceModel.CALDoc.Comment.Function.make(
                            textBlock, 
                            null);
            }
            
            SourceModel.FunctionDefn getDCOrdinal =
                FunctionDefn.Foreign.make(
                        getDCOrdinalComment,
                        "j" + typeConstructorInfo.calForeignTypeName.substring(1) + "_getDCOrdinal", 
                        Scope.PRIVATE, 
                        true,
                        "method getDCOrdinal", 
                        getDCOrdinalTypeSignature);
            topLevelDefnsList.add(getDCOrdinal);

        }
        
        private void generateEnumerationIO (List<TopLevelSourceElement> topLevelDefnsList) {
            // Declare the java class as a foreign type.
            // ex. data foreign unsafe import jvm  "org.olap.CubeType" public JCubeType;
            makeForeignClassDeclaration(topLevelDefnsList);
            
            TypeExprDefn.TypeCons jClassTypeExpr = 
                TypeExprDefn.TypeCons.make(Name.TypeCons.makeUnqualified(typeConstructorInfo.calForeignTypeName));
               
        
            // String -> String;
            Map<String, String> enumFieldFunctionNames = new LinkedHashMap<String, String>();
            
            // Now we need to bring in each instance of the enumeration class.
            // These will be public static fields of the class.
            for (int i = 0, n = typeConstructor.getNDataConstructors(); i < n; ++i) {
                DataConstructor dc = typeConstructor.getNthDataConstructor(i);
                String enumFieldName = createEnumFieldName(dc.getName().getUnqualifiedName());
                
                // Example:
                // foreign unsafe import jvm "static field org.olap.CubeType.TYPE"
                //     public jCubeType_TYPE :: JCubeType;
                SourceModel.TypeSignature typeSignature = 
                    TypeSignature.make(jClassTypeExpr);

                String functionName = "j" + javaClass.getClassName().getUnqualifiedJavaSourceName() + "_" + enumFieldName;
                
                SourceModel.CALDoc.Comment.Function foreignFieldComment;
                {
                    SourceModel.CALDoc.TextSegment.Plain textSegment =
                        SourceModel.CALDoc.TextSegment.Plain.make(
                                "\nThe Java static field " + javaClass.getClassName().getUnqualifiedJavaSourceName() + "." + enumFieldName + ".\n" +
                                "This field is used to represent instances of the data constructor " + dc.getName().getUnqualifiedName() + ".\n");
                    
                    SourceModel.CALDoc.TextBlock textBlock =
                        SourceModel.CALDoc.TextBlock.make(new SourceModel.CALDoc.TextSegment.TopLevel[]{textSegment});
                        
                    foreignFieldComment = 
                        SourceModel.CALDoc.Comment.Function.make(
                                textBlock, 
                                null);
                }
                                
                SourceModel.FunctionDefn enumInstance =
                    FunctionDefn.Foreign.make(
                            foreignFieldComment,
                            functionName, 
                            Scope.PRIVATE, 
                            true,
                            "static field " + javaClass.getClassName().getFullJavaSourceName() + "." + enumFieldName, 
                                typeSignature);
 
                topLevelDefnsList.add (enumInstance);
                enumFieldFunctionNames.put(dc.getName().getUnqualifiedName(), functionName);    
            }
            
            // Bring in the getDCOrdinal() method.
            importGetDCOrdinal(topLevelDefnsList);
            
            // Create an function that converts a JCubeType_ to a CubeType
            // inputCubeType :: JCubeType_ -> CubeType;
            // public inputCubeType jCubeType_ = 
            //     case (jCubeType_getDCOrdinal jCubeType_) of
            //     0 -> ...;
            //     1 -> ...;
            
            String inputArgumentName = "j" + javaClass.getClassName().getUnqualifiedJavaSourceName();
            String inputFunctionName = "input" + typeConstructor.getName().getUnqualifiedName();

            Expr.Var inputArgument = Expr.Var.make(Name.Function.makeUnqualified(inputArgumentName));
            
            SourceModel.Expr condition = 
                Expr.Application.make(
                        new Expr[]{Expr.Var.make(Name.Function.makeUnqualified("j" + javaClass.getClassName().getUnqualifiedJavaSourceName() + "_getDCOrdinal")),
                                inputArgument});
            
            SourceModel.Expr.Case.Alt caseAlts[] = 
                new SourceModel.Expr.Case.Alt[typeConstructor.getNDataConstructors()];
            for (int i = 0, n = caseAlts.length; i < n; ++i) {
                DataConstructor dc = typeConstructor.getNthDataConstructor(i);
                caseAlts[i] = 
                    Expr.Case.Alt.UnpackInt.make(
                            new BigInteger[]{BigInteger.valueOf(dc.getOrdinal())},
                            Expr.DataCons.make(Name.DataCons.make(dc.getName())));
            }
            
            SourceModel.Expr.Case body = 
                Expr.Case.make(condition, caseAlts);
            
            SourceModel.FunctionDefn.Algebraic inputFunction = 
                FunctionDefn.Algebraic.make(
                        inputFunctionName, 
                        Scope.PUBLIC, 
                        new SourceModel.Parameter[]{Parameter.make(inputArgumentName, false)}, 
                        body);

            TypeExprDefn inputFunctionType =
                TypeExprDefn.Function.make(
                        TypeExprDefn.TypeCons.make(Name.TypeCons.makeUnqualified(typeConstructorInfo.calForeignTypeName)), 
                        TypeExprDefn.TypeCons.make(Name.TypeCons.makeUnqualified(typeConstructor.getName().getUnqualifiedName())));
            
            SourceModel.CALDoc.Comment.Function inputFunctionComment;
            {
                SourceModel.CALDoc.TextSegment.Plain textSegment =
                    SourceModel.CALDoc.TextSegment.Plain.make(
                            "\nInput an instance of " + typeConstructor.getName().getUnqualifiedName() + ".\n" +
                            "Translates an instance of " + typeConstructorInfo.calForeignTypeName + " to\n" +
                            "an instance of "+ typeConstructor.getName().getUnqualifiedName() + "."); 
                
                SourceModel.CALDoc.TextBlock textBlock =
                    SourceModel.CALDoc.TextBlock.make(new SourceModel.CALDoc.TextSegment.TopLevel[]{textSegment});
                    
                inputFunctionComment = 
                    SourceModel.CALDoc.Comment.Function.make(
                            textBlock, 
                            null);
            }
            
            SourceModel.FunctionTypeDeclaration inputFunctionTypeDecl =
                FunctionTypeDeclaration.make(
                        inputFunctionComment,
                        inputFunctionName, 
                        TypeSignature.make(inputFunctionType));
            
            topLevelDefnsList.add(inputFunctionTypeDecl);
            topLevelDefnsList.add(inputFunction);
            
            // Create a function which creates a CubeType from a JObject.
            String inputFromJObjectFunctionName = inputFunctionName + "FromJObject"; 
            createInputFromJObjectFunction(inputFromJObjectFunctionName, inputFunctionName, topLevelDefnsList);
            
            // Now we need to declare an instance of inputable.
            // instance Inputable CubeType where
            //     input = jObjectToCubeType;
            //     ;
            SourceModel.InstanceDefn instanceDefn = 
                InstanceDefn.make(
                        Name.TypeClass.make(CAL_Prelude.TypeClasses.Inputable), 
                        InstanceDefn.InstanceTypeCons.TypeCons.make(Name.TypeCons.make(typeConstructor.getName()), null), 
                        null, 
                        new InstanceDefn.InstanceMethod[]{
                            InstanceDefn.InstanceMethod.make(CAL_Prelude.Functions.input.getUnqualifiedName(), Name.Function.makeUnqualified(inputFromJObjectFunctionName))
                        });
            
            topLevelDefnsList.add(instanceDefn);
            
            // Create a conversion function for CAL type to corresponding foreign type.
            // cubeTypeToJCubeType :: CubeType -> JCubeType_;
            // cubeTypeToJCubeType dcInstance = 
            //     case dcInstance of
            //     DC1 -> unsafeCoerce jCubeType_DC1;
            //     ...
            //     ;
            condition = 
                Expr.Var.makeUnqualified("dcInstance");
            
            caseAlts = 
                new SourceModel.Expr.Case.Alt[typeConstructor.getNDataConstructors()];
            
            for (int i = 0, n = caseAlts.length; i < n; ++i) {
                DataConstructor dc = typeConstructor.getNthDataConstructor(i);
                Expr enumFieldFunction = 
                    Expr.Var.make(
                        Name.Function.makeUnqualified((String)enumFieldFunctionNames.get(dc.getName().getUnqualifiedName())));
                
                caseAlts[i] = 
                    Expr.Case.Alt.UnpackDataCons.make(
                        Name.DataCons.makeUnqualified(dc.getName().getUnqualifiedName()), 
                        enumFieldFunction);
                    
            }
            
            body = 
                Expr.Case.make(condition, caseAlts);
            
            makeOutputableInstance(typeConstructor, body, topLevelDefnsList);
        }

        /**
         * @return the inputableImports
         */
        private Set<ModuleName> getInputableImports() {
            return inputableImports;
        }

        /**
         * Create a wrapper function around the input... function.
         * The wrapper function will take a JObject as argument rather than
         * the foreign type corresponding to the CAL type.
         * This new function will be used for the Inputable class method input.
         * @param inputFromJObjectFunctionName
         * @param inputFunctionName
         * @param topLevelDefnsList
         */
        private void createInputFromJObjectFunction (
                String inputFromJObjectFunctionName,
                String inputFunctionName,
                List<TopLevelSourceElement> topLevelDefnsList) {
            
            // Now create a function that inputs from a JObject.
            // inputCubeTypeFromJObject :: JObject -> CubeType;
            // inputCubeTypeFromJObject jObject = inputCubeType (input jObject);
            String inputArgumentName = "jObject";

            Expr.Var inputArgument = Expr.Var.make(Name.Function.makeUnqualified(inputArgumentName));

            SourceModel.Expr body = CAL_Prelude.Functions.input(inputArgument);
            body = 
                Expr.Application.make(
                        new Expr[]{Expr.Var.make(Name.Function.makeUnqualified(inputFunctionName)),
                                body});
            
            SourceModel.FunctionDefn.Algebraic inputFunction = 
                FunctionDefn.Algebraic.make(
                        inputFromJObjectFunctionName, 
                        Scope.PRIVATE, 
                        new SourceModel.Parameter[]{Parameter.make("jObject", false)}, 
                        body);

            TypeExprDefn inputFunctionType =
                TypeExprDefn.Function.make(
                        JOBJECT_TYPE_EXPR_DEFN, 
                        TypeExprDefn.TypeCons.make(Name.TypeCons.makeUnqualified(typeConstructor.getName().getUnqualifiedName())));
            
            SourceModel.CALDoc.Comment.Function inputFunctionComment;
            {
                SourceModel.CALDoc.TextSegment.Plain textSegment =
                    SourceModel.CALDoc.TextSegment.Plain.make(
                            "\nInput an instance of " + typeConstructor.getName().getUnqualifiedName() + ".\n" +
                            "Translates an instance of JObject to\n" +
                            "an instance of "+ typeConstructor.getName().getUnqualifiedName() + "."); 
                
                SourceModel.CALDoc.TextBlock textBlock =
                    SourceModel.CALDoc.TextBlock.make(new SourceModel.CALDoc.TextSegment.TopLevel[]{textSegment});
                    
                inputFunctionComment = 
                    SourceModel.CALDoc.Comment.Function.make(
                            textBlock, 
                            null);
            }
            
            SourceModel.FunctionTypeDeclaration inputFunctionTypeDecl =
                FunctionTypeDeclaration.make(
                        inputFunctionComment,
                        inputFromJObjectFunctionName, 
                        TypeSignature.make(inputFunctionType));
            
            topLevelDefnsList.add(inputFunctionTypeDecl);
            topLevelDefnsList.add(inputFunction);
            
        }        

    }

    private final class JavaDataClassGenerator {
        
        private final String targetPackage; 
        private final TypeConstructorInfo typeConstructorInfo;

        /** List of LogRecord */
        private final List<LogRecord> logMessages = new ArrayList<LogRecord>();
        
        /**
         * JavaDataClassGenerator constructor.
         * @param targetPackage
         * @param typeConstructorInfo
         */
        private JavaDataClassGenerator (
                String targetPackage,
                TypeConstructorInfo typeConstructorInfo) {
    
            this.typeConstructorInfo = typeConstructorInfo;
            this.targetPackage = targetPackage;
        }

        JavaClassRep generateClassForType () {
            
            JavaTypeName typeConstructorClassName = 
                JavaTypeName.make(targetPackage + "." + typeConstructorInfo.javaClassName, false);
    
            TypeConstructor typeConstructor = typeConstructorInfo.typeConstructor;
            
            JavaClassRep outerTypeDefinition = 
                new OuterTypeDefinitionGenerator().generateOuterTypeDefinition(typeConstructorClassName);
            
            // Now we need to generate inner classes for each data constructor.
            // This is only necessary if the data type is not an enumeration.
            if (!typeConstructorInfo.isEnumerationType) {
                
                for (int i = 0, n = typeConstructor.getNDataConstructors(); i < n; ++i) {
                    DataConstructor dc = typeConstructor.getNthDataConstructor(i);
                    JavaClassRep dcClass = new InnerDCClassGenerator().generateInnerDCClass(dc, typeConstructorInfo, typeConstructorClassName);
                    
                    outerTypeDefinition.addInnerClass(dcClass);
                }
            }
    
            return outerTypeDefinition;
    
        }
    
        private final class InnerDCClassGenerator {
            private final JavaClassRep generateInnerDCClass (
                    DataConstructor dc, 
                    TypeConstructorInfo typeConstructorInfo, 
                    JavaTypeName typeConstructorClassName) {
    
                int classModifiers = Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL;
                
                DataConstructorInfo dcInfo = (DataConstructorInfo)typeConstructorInfo.dataConstructorInfo.get(dc);
                
                JavaTypeName dcClassTypeName = 
                    JavaTypeName.make(
                            typeConstructorClassName.getFullJavaSourceName() + "$" + dcInfo.innerClassName, false);
                
                JavaClassRep dcClass = 
                    new JavaClassRep(
                            dcClassTypeName, 
                            typeConstructorClassName, 
                            classModifiers, 
                            IO_Source_Generator.EMPTY_TYPE_NAME_ARRAY);
    
                JavaDocComment jdc =
                    new JavaDocComment ("This class represents instances of the CAL data constructor " + dc.getName() + ".");
                dcClass.setJavaDoc(jdc);
                
                createFields (dcClass, dc, typeConstructorInfo);
                
                dcClass.addConstructor(createConstructor(dc, typeConstructorInfo, dcClassTypeName));
                
                dcClass.addMethod(createMethod_getDCName(dc.getName().getUnqualifiedName()));
    
                for (FieldName fieldName : dcInfo.fieldTypeNames.keySet()) {
                    if (typeConstructorInfo.commonFieldNames.contains(fieldName)) {
                        continue;
                    }
                    JavaTypeName fieldType = (JavaTypeName)dcInfo.fieldTypeNames.get(fieldName);
                    String javaFieldName = (String)typeConstructorInfo.fieldJavaNames.get(fieldName);
                    
                    String accessorName = (String)typeConstructorInfo.fieldJavaAccessorMethodNames.get(fieldName);
                    JavaMethod getter = createMethod_getField(accessorName, javaFieldName, fieldType, true);
                    
                    dcClass.addMethod(getter);
                }
    
                dcClass.addMethod(createMethod_getDCOrdinal(dc.getOrdinal()));

                dcClass.addMethod (JavaDataClassGenerator.this.createMethod_toString(dcInfo.allFieldNames, dcInfo.fieldTypeNames));

                dcClass.addMethod (
                        JavaDataClassGenerator.this.createMethod_equals(
                                dcClassTypeName, 
                                dcInfo.allFieldNames, 
                                dcInfo.fieldTypeNames));

                dcClass.addMethod (
                        JavaDataClassGenerator.this.createMethod_hashCode(
                                dcInfo.allFieldNames, 
                                dcInfo.fieldTypeNames));

                return dcClass;
            }
            
            private final JavaConstructor createConstructor(
                    DataConstructor dc, 
                    TypeConstructorInfo typeConstructorInfo, 
                    JavaTypeName dcClassTypeName) {
                
                DataConstructorInfo dci = (DataConstructorInfo)typeConstructorInfo.dataConstructorInfo.get(dc);
                
                String[] argNames = new String [dc.getArity()];
                JavaTypeName[] argTypes = new JavaTypeName [argNames.length];
                Block constructorBody = new Block();
                
                for (int i = 0, n = dc.getArity(); i < n; ++i) {
                    FieldName fn = (FieldName)dc.getNthFieldName(i);
                    
                    JavaTypeName type = (JavaTypeName)dci.fieldTypeNames.get(fn);
                    String fieldName = (String)typeConstructorInfo.fieldJavaNames.get(fn);
                    String argName = fieldName+"$";
        
                    argNames[i] = argName;
                    argTypes[i] = type;

                    if (!typeConstructorInfo.commonFieldNames.contains(fn)) {
                        JavaExpression.JavaField.Instance field = 
                            new JavaExpression.JavaField.Instance(null, fieldName, type);
                        JavaExpression assign = new Assignment (field, new MethodVariable(argName));
                        constructorBody.addStatement(new ExpressionStatement(assign));
                    }
                }
                
                String constructorName = dcClassTypeName.getUnqualifiedJavaSourceName();
                int index = constructorName.lastIndexOf('.');
                if (index > -1) {
                    constructorName = constructorName.substring(index + 1);
                }
                
                JavaConstructor constructor;
                if (typeConstructorInfo.commonFieldNames.size() > 0) {
                    JavaExpression superArgValues[] = new JavaExpression[typeConstructorInfo.commonFieldNames.size()];
                    JavaTypeName superArgTypes[] = new JavaTypeName[superArgValues.length];
                    int i = 0;
                    for (FieldName superFieldName : typeConstructorInfo.commonFieldNames) {
                        JavaTypeName fieldType = (JavaTypeName)dci.fieldTypeNames.get(superFieldName);
                        String fieldName = (String)typeConstructorInfo.fieldJavaNames.get(superFieldName);
                        String argName = fieldName+"$";
                        JavaExpression superArgValue = new MethodVariable(argName);
                        superArgValues[i] = superArgValue;
                        superArgTypes[i] = fieldType;
                        ++i;
                    }
                    
                    constructor = new JavaConstructor (Modifier.PUBLIC, argNames, argTypes, constructorName, superArgValues, superArgTypes);
                } else {
                    constructor = new JavaConstructor (Modifier.PUBLIC, argNames, argTypes, constructorName);
                    
                }
                
                constructor.addStatement(constructorBody);
                
                return constructor;

            }
            
            private final void createFields (JavaClassRep dcClass, DataConstructor dc, TypeConstructorInfo typeConstructorInfo) {
                
                DataConstructorInfo dci = (DataConstructorInfo)typeConstructorInfo.dataConstructorInfo.get(dc);
                
                // Add a volatile int field for the hashcode.
                JavaFieldDeclaration hashCodeFieldDecl =
                    new JavaFieldDeclaration(
                            Modifier.PRIVATE | Modifier.VOLATILE,
                            JavaTypeName.INT,
                            HASH_CODE_FIELD_NAME,
                            LiteralWrapper.make(new Integer(0)));
                
                hashCodeFieldDecl.setJavaDoc(new JavaDocComment("Lazily initialized, cached hashCode."));
                
                dcClass.addFieldDeclaration(hashCodeFieldDecl);

                for (int i = 0, n = dc.getArity(); i < n; ++i) {
                    FieldName fieldName = dc.getNthFieldName(i);
                    if (typeConstructorInfo.commonFieldNames.contains(fieldName)) {
                        continue;
                    }
                    
                    JavaTypeName type = (JavaTypeName)dci.fieldTypeNames.get(fieldName);
                    String javaFieldName = (String)typeConstructorInfo.fieldJavaNames.get(fieldName);
        
                    int modifiers = Modifier.FINAL | Modifier.PRIVATE;
                    
                    JavaFieldDeclaration fieldDec = new JavaFieldDeclaration (modifiers, type, javaFieldName, null);
                    dcClass.addFieldDeclaration(fieldDec);
                }
            }
 
            
            private final JavaMethod createMethod_getDCOrdinal(int ordinal) {
                
                JavaMethod javaMethod = new JavaMethod(Modifier.PUBLIC, JavaTypeName.INT, GET_DC_ORDINAL_METHOD_NAME);
                // return dc ordinal.
                javaMethod.addStatement (new ReturnStatement(LiteralWrapper.make (new Integer (ordinal))));

                JavaDocComment jdc = 
                    new JavaDocComment ("@return the ordinal of this instance of " + typeConstructorInfo.javaClassName);
                javaMethod.setJavaDocComment(jdc);

                return javaMethod;
            }
            
            private final JavaMethod createMethod_getDCName(String dcName) {
                int modifiers = Modifier.PUBLIC | Modifier.FINAL;
                JavaMethod javaMethod = new JavaMethod(modifiers, JavaTypeName.STRING, GET_DC_NAME_METHOD_NAME);
                
                javaMethod.addStatement(new ReturnStatement(LiteralWrapper.make(dcName)));

                JavaDocComment jdc = 
                    new JavaDocComment ("@return the name of the data constructor corresponding to this instance of " + typeConstructorInfo.javaClassName);
                javaMethod.setJavaDocComment(jdc);

                return javaMethod;
            }  
        }
        
        private final class OuterTypeDefinitionGenerator {
            /**
             * Generate the java class representation of the data type for which this builder is responsible.
             * The generated representation will be the outermost class definition only -- no inner classes will have been generated.
             * @param typeConstructorClassName
             * @return class rep
             */
            private JavaClassRep generateOuterTypeDefinition(JavaTypeName typeConstructorClassName) {
                 
                int classModifiers = Modifier.PUBLIC;
                
                if (typeConstructorInfo.isEnumerationType) {
                    classModifiers |= Modifier.FINAL;
                } else {
                    classModifiers |= Modifier.ABSTRACT; 
                }
        
                JavaTypeName superClassTypeName = JavaTypeName.OBJECT;
        
                // No interfaces are implemented
                JavaTypeName[] interfaces = IO_Source_Generator.EMPTY_TYPE_NAME_ARRAY;
        
                JavaClassRep javaClassRep = 
                    new JavaClassRep(
                            typeConstructorClassName, 
                            superClassTypeName, 
                            classModifiers, 
                            interfaces);
                
                // Add a comment for the top of the source file. 
                MultiLineComment mlc = new MultiLineComment("<!--");
                mlc.addLine(" ");
                mlc.addLine("**************************************************************");
                mlc.addLine("This Java source has been automatically generated.");
                mlc.addLine("MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE");
                mlc.addLine("**************************************************************");
                mlc.addLine(" ");
                mlc.addLine(" ");
                mlc.addLine("This file (" + javaClassRep.getClassName().getUnqualifiedJavaSourceName() + ".java)");
                mlc.addLine("was generated from CAL type constructor: " + typeConstructorInfo.typeConstructor.getName() + ".");
                mlc.addLine(" ");
                mlc.addLine("Creation date: " + new Date());
                mlc.addLine("--!>");
                mlc.addLine(" ");
                
                javaClassRep.setComment(mlc);
                
                // We want to insert the CALDoc comment for the type as a JavaDoc comment for the class.
                JavaDocComment jdc = 
                    new JavaDocComment ("This class (" + javaClassRep.getClassName().getUnqualifiedJavaSourceName() + ") provides a Java data class corresponding to");
                    jdc.addLine("the CAL type constructor " + typeConstructorInfo.typeConstructor.getName() + ".");
                    jdc.addLine("");
                    
                if (typeConstructorInfo.isEnumerationType) {
                    jdc.addLine("This type constructor is an enumeration. (i.e. all data constructors have no fields)");
                    jdc.addLine("The individual data constructors are represented by instances of " + javaClassRep.getClassName().getUnqualifiedJavaSourceName() + " held ");
                    jdc.addLine("in static final fields.");
                } else {
                    jdc.addLine("Because the type constructor has only one data constructor, with the same name");
                    jdc.addLine("as the type constructor this class also represents instances of the data constructor.");
                }
                
                javaClassRep.setJavaDoc(jdc);
                
                // Generate fields in this class for any fields which are common to all data constructors.
                createFields(javaClassRep, typeConstructorInfo);
        
                if (typeConstructorInfo.isEnumerationType) {
                    // Add instance fields for name and ordinal
                    // Add static final fields for each data constructor
                    createFields_Enumeration(typeConstructorInfo.typeConstructor, javaClassRep, typeConstructorClassName);

                    // Add a function which takes an int value for the
                    // ordinal and returns the corresponding enum instance.
                    javaClassRep.addMethod(createMethod_fromOrdinal (typeConstructorClassName));
                }
                
                if (typeConstructorInfo.commonFieldNames.size() > 0) {
                    javaClassRep.addConstructor(createConstructor(typeConstructorInfo, typeConstructorClassName));
                } else {
                    javaClassRep.addConstructor(createConstructor_default(typeConstructorClassName, typeConstructorInfo.isEnumerationType));
                    
                }
                
                // Create a get_FieldName method for each unique field name in the set of
                // data constructors.  
                // If the field is common to all DCs it will be implemented at this
                // level.  Otherwise the implementation at this level throws an error
                // and the DC classes are expected to override it.
                for (FieldName fieldName : typeConstructorInfo.commonFieldNames) {
                    String javaFieldName = (String)typeConstructorInfo.fieldJavaNames.get(fieldName);
                    Set<JavaTypeName> fieldTypes = typeConstructorInfo.allFieldTypeNames.get(fieldName);
                    
                    for (JavaTypeName type  : fieldTypes) {
                        String accessorMethodName = (String)typeConstructorInfo.fieldJavaAccessorMethodNames.get(fieldName);
                        JavaMethod getter = createMethod_getField (
                                accessorMethodName,
                                javaFieldName, 
                                type,
                                true);
                        
                        javaClassRep.addMethod(getter);
                        
                        getter.setJavaDocComment(new JavaDocComment ("@return " + fieldName));
                    }
                }
                
    
                javaClassRep.addMethod(createMethod_getDCName(typeConstructorInfo));
                
                javaClassRep.addMethod(createMethod_getDCOrdinal(typeConstructorInfo));
                
                JavaMethod toString = createMethod_toString();
                if (toString != null) {
                    javaClassRep.addMethod (toString);
                }
                
                if (typeConstructorInfo.isEnumerationType) {
                    JavaMethod equals = JavaDataClassGenerator.this.createMethod_equals(
                            typeConstructorClassName, 
                            typeConstructorInfo.commonFieldNames, 
                            typeConstructorInfo.commonFieldTypes);

                    javaClassRep.addMethod (equals);
                }
                
                createMethod_hashCode (javaClassRep);
                
                return javaClassRep;
            }
            
            private void createMethod_hashCode (JavaClassRep javaClassRep) {
            
                if (typeConstructorInfo.isEnumerationType) {
                    // We can just return the ordinal.
                    JavaMethod hashCode = 
                        new JavaMethod(
                                Modifier.PUBLIC | Modifier.FINAL,
                                JavaTypeName.INT,
                                "hashCode");
                    
                    JavaExpression call_getOrdinal =
                        new MethodInvocation.Instance(
                                null,
                                GET_DC_ORDINAL_METHOD_NAME,
                                JavaTypeName.INT,
                                MethodInvocation.InvocationType.VIRTUAL);
                    hashCode.addStatement(new ReturnStatement (call_getOrdinal));
                    
                    javaClassRep.addMethod(hashCode);
                }
            }
            
            private JavaMethod createMethod_toString() {

                JavaMethod toString;
                
                if (typeConstructorInfo.isEnumerationType) {
                    toString = 
                        new JavaMethod(
                                Modifier.PUBLIC | Modifier.FINAL, 
                                JavaTypeName.STRING, 
                                "toString");
                    
                    JavaExpression getDCName = 
                        new JavaExpression.MethodInvocation.Instance(
                                null,
                                GET_DC_NAME_METHOD_NAME,
                                JavaTypeName.STRING,
                                MethodInvocation.InvocationType.VIRTUAL);
                    
                    toString.addStatement(new ReturnStatement (getDCName));
                    return toString;
                } else {
                    toString = null;
                }

                if (toString != null) {
                    JavaDocComment jdc = 
                        new JavaDocComment ("@return a String representing this instance of " + typeConstructorInfo.javaClassName);
                    
                    toString.setJavaDocComment(jdc);
                }
                
                return toString;
            }
            
            private JavaMethod createMethod_fromOrdinal (JavaTypeName dataType_TypeName) {
                JavaMethod fromOrdinal = 
                    new JavaMethod(
                            Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL,
                            dataType_TypeName, 
                            "ordinal",
                            JavaTypeName.INT, 
                            true,
                            "fromOrdinal");
                
                // build up a switch.
                JavaStatement.SwitchStatement switchStatement =
                    new JavaStatement.SwitchStatement(new MethodVariable("ordinal"));
                
                for (int i = 0, n = typeConstructorInfo.typeConstructor.getNDataConstructors(); i < n; ++i) {
                    DataConstructor dc = typeConstructorInfo.typeConstructor.getNthDataConstructor(i);
                    
                    String enumFieldName = createEnumFieldName(dc.getName().getUnqualifiedName());
                    JavaField field = new JavaField.Static(dataType_TypeName, enumFieldName, dataType_TypeName);
                    
                    JavaStatement.SwitchStatement.IntCaseGroup intCase =
                        new SwitchStatement.IntCaseGroup(
                                dc.getOrdinal(),
                                new ReturnStatement(field));
                    switchStatement.addCase(intCase);
                }
                
                // Throw an error if ordinal is outside accepted range.
                JavaExpression message = 
                    new JavaExpression.OperatorExpression.Binary(
                            JavaOperator.STRING_CONCATENATION, 
                            LiteralWrapper.make ("Invalid ordinal " ),
                            new MethodVariable("ordinal"));
                
                message = 
                    new JavaExpression.OperatorExpression.Binary(
                            JavaOperator.STRING_CONCATENATION, 
                            message,
                            LiteralWrapper.make(" for data type " + typeConstructorInfo.typeConstructor.getName().getUnqualifiedName()));
                
                JavaExpression createException = 
                    new JavaExpression.ClassInstanceCreationExpression (
                            UNSUPPORTED_OPERATION_TYPE_NAME, 
                            message,
                            JavaTypeName.STRING);
                
                JavaStatement throwStatement = new JavaStatement.ThrowStatement(createException);

                SwitchStatement.DefaultCase defaultCase =
                    new SwitchStatement.DefaultCase(throwStatement);
                switchStatement.addCase(defaultCase);
                
                fromOrdinal.addStatement(switchStatement);
                
                JavaDocComment jdc = 
                    new JavaDocComment ("@param ordinal");
                jdc.addLine("@return the instance of " + typeConstructorInfo.javaClassName + " corresponding to the given ordinal.");
                fromOrdinal.setJavaDocComment(jdc);
                
                return fromOrdinal;
            }
            
            /**
             * Create fields for this class.
             * @param javaClassRep
             * @param typeConstructorInfo
             */
            private void createFields (
                    JavaClassRep javaClassRep, 
                    TypeConstructorInfo typeConstructorInfo) {
                
                for (FieldName fieldName : typeConstructorInfo.commonFieldNames) {
                    Set<JavaTypeName> javaTypeNames = typeConstructorInfo.allFieldTypeNames.get(fieldName);
                    assert (javaTypeNames.size() == 1);
                    
                    Iterator<JavaTypeName> types = javaTypeNames.iterator();
                    JavaTypeName type = types.next();
                    String javaFieldName = typeConstructorInfo.fieldJavaNames.get(fieldName);
        
                    int modifiers = Modifier.FINAL;
                    
                    JavaFieldDeclaration fieldDec = new JavaFieldDeclaration (modifiers, type, javaFieldName, null);
                    javaClassRep.addFieldDeclaration(fieldDec);
                }
                

            }
            
            private final JavaMethod createMethod_getDCOrdinal(TypeConstructorInfo typeConstructorInfo) {
                
                int modifiers = Modifier.PUBLIC;
                
                JavaMethod javaMethod = new JavaMethod(modifiers, JavaTypeName.INT, GET_DC_ORDINAL_METHOD_NAME);
                
                if (typeConstructorInfo.isEnumerationType) {
                    // return instance field.
                    javaMethod.addStatement (new ReturnStatement (new JavaField.Instance (null, ORDINAL_FIELD_NAME, JavaTypeName.INT)));
                } else {
                    javaMethod.addStatement (new ReturnStatement(LiteralWrapper.make (new Integer(-1))));
                }
                
                JavaDocComment jdc = 
                    new JavaDocComment ("@return the ordinal of this instance of " + typeConstructorInfo.javaClassName);
                javaMethod.setJavaDocComment(jdc);
                
                return javaMethod;
            }
            
            private final JavaMethod createMethod_getDCName(TypeConstructorInfo typeConstructorInfo) {
                
                int modifiers = Modifier.PUBLIC;
                
                JavaMethod javaMethod = new JavaMethod(modifiers, JavaTypeName.STRING, GET_DC_NAME_METHOD_NAME);
                
                if (typeConstructorInfo.isEnumerationType) {
                    // return the name$ field
                    javaMethod.addStatement (new ReturnStatement (new JavaField.Instance (null, DC_NAME_FIELD_NAME, JavaTypeName.STRING)));
                } else {
                    javaMethod.addStatement (new ReturnStatement(LiteralWrapper.NULL));
                }
                
                JavaDocComment jdc = 
                    new JavaDocComment ("@return the name of the data constructor corresponding to this instance of " + typeConstructorInfo.javaClassName);
                javaMethod.setJavaDocComment(jdc);

                return javaMethod;

            }
            
            /**
             * Create a constructor for the data type class.
             * @param typeConstructorInfo
             * @param className
             * @return the constructor for the data type class.
             */
            private JavaConstructor createConstructor (
                    TypeConstructorInfo typeConstructorInfo, 
                    JavaTypeName className) {
                
                String[] argNames = new String [typeConstructorInfo.commonFieldNames.size()];
                JavaTypeName[] argTypes = new JavaTypeName [argNames.length];
                Block constructorBody = new Block();
                
                int i = 0;
                for (FieldName fn : typeConstructorInfo.commonFieldNames) {
                    Set<JavaTypeName> javaTypeNames = typeConstructorInfo.allFieldTypeNames.get(fn);
                    assert(javaTypeNames.size() == 1);
                    
                    Iterator<JavaTypeName> types = javaTypeNames.iterator();
                    JavaTypeName type = types.next();
        
                    String fieldName = (String)typeConstructorInfo.fieldJavaNames.get(fn);
                    String argName = fieldName+"$";
        
                    argNames[i] = argName;
                    argTypes[i] = type;
                    
                    JavaExpression.JavaField.Instance field = 
                        new JavaExpression.JavaField.Instance(null, fieldName, type);
                    JavaExpression assign = new Assignment (field, new MethodVariable(argName));
                    constructorBody.addStatement(new ExpressionStatement(assign));
                    i++;
                }
                
                String constructorName = className.getUnqualifiedJavaSourceName();
                int index = constructorName.lastIndexOf('.');
                if (index > -1) {
                    constructorName = constructorName.substring(index + 1);
                }
                
                JavaConstructor constructor;
                if (!typeConstructorInfo.isEnumerationType) {
                    constructor = new JavaConstructor (Modifier.PRIVATE, argNames, argTypes, constructorName);
                } else {
                    constructor = new JavaConstructor (Modifier.PUBLIC, argNames, argTypes, constructorName);
                }
                
                constructor.addStatement(constructorBody);
                
                return constructor;
            }
            

            /**
             * Create the default constructor for the data type class.
             * @param className
             * @param isEnumeration
             * @return the constructor.
             */
            private final JavaConstructor createConstructor_default (
                    JavaTypeName className,
                    boolean isEnumeration) {
                
                JavaConstructor constructor;
                if (isEnumeration) {
                    // We want a private constructor that initializes the name$ and ordinal$ fields.
                    constructor = 
                        new JavaConstructor (
                                Modifier.PRIVATE, 
                                new String[]{"ordinal", "name"}, 
                                new JavaTypeName[]{JavaTypeName.INT, JavaTypeName.STRING}, 
                                className.getUnqualifiedJavaSourceName());
                    
                    JavaExpression assignOrdinal = 
                        new JavaExpression.Assignment (
                                new JavaField.Instance(null, ORDINAL_FIELD_NAME, JavaTypeName.INT), 
                                new MethodVariable("ordinal"));
                    constructor.addStatement(new ExpressionStatement(assignOrdinal));
                    
                    JavaExpression assignName = 
                        new JavaExpression.Assignment (
                                new JavaField.Instance(null, DC_NAME_FIELD_NAME, JavaTypeName.STRING), 
                                new MethodVariable("name"));
                    constructor.addStatement(new ExpressionStatement(assignName));
                    
                } else {
                    constructor = new JavaConstructor (Modifier.PUBLIC, className.getUnqualifiedJavaSourceName());
                }
                
                return constructor;
            }        
             
            private void createFields_Enumeration (
                    TypeConstructor typeConstructor, 
                    JavaClassRep dataTypeClass,
                    JavaTypeName dataType_TypeName) {
    
                JavaTypeName[] constructorArgTypes = new JavaTypeName[]{JavaTypeName.INT, JavaTypeName.STRING};
                
                // For each data constructor create a constant int field for the ordinal.
                for (int i = 0, n = typeConstructor.getNDataConstructors(); i < n; ++i) {
                    DataConstructor dc = typeConstructor.getNthDataConstructor(i);
                    String ordinalFieldName = createEnumFieldName(dc.getName().getUnqualifiedName()) + "_ORDINAL";
                    JavaExpression initializer = LiteralWrapper.make(new Integer(dc.getOrdinal()));
                    JavaFieldDeclaration fieldDec = 
                        new JavaFieldDeclaration (
                                Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL, 
                                JavaTypeName.INT, ordinalFieldName, initializer);
                    dataTypeClass.addFieldDeclaration(fieldDec);
                    
                    JavaDocComment jdc = 
                        new JavaDocComment ("Ordinal value corresponding to the " + dc.getName().getUnqualifiedName() + " data constructor.");
                    fieldDec.setJavaDoc(jdc);
                }
                
                // For each data constructor create a static field 
                for (int i = 0, n = typeConstructor.getNDataConstructors(); i < n; ++i) {
                    DataConstructor dc = typeConstructor.getNthDataConstructor(i);
                    
                    String staticFieldName = createEnumFieldName(dc.getName().getUnqualifiedName());
                    
                    JavaExpression initializer = 
                        new JavaExpression.ClassInstanceCreationExpression(
                                dataType_TypeName,
                                new JavaExpression[]{
                                        new JavaField.Static(
                                                dataType_TypeName, 
                                                staticFieldName + "_ORDINAL", 
                                                JavaTypeName.INT), 
                                        LiteralWrapper.make(dc.getName().getUnqualifiedName())},
                                constructorArgTypes);
                    
                    JavaFieldDeclaration fieldDec = 
                        new JavaFieldDeclaration (
                                Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL, 
                                dataType_TypeName, staticFieldName, initializer);

                    JavaDocComment jdc = 
                        new JavaDocComment ("This instance of " + typeConstructorInfo.javaClassName + " representing the " + dc.getName().getUnqualifiedName() + " data constructor.");
                    fieldDec.setJavaDoc(jdc);

                    dataTypeClass.addFieldDeclaration(fieldDec);
                }
                
                // Add two instance fields.
                // int ordinal$;
                // String name$;
                JavaFieldDeclaration fieldDec = new JavaFieldDeclaration (Modifier.PRIVATE, JavaTypeName.INT, ORDINAL_FIELD_NAME, null);
                dataTypeClass.addFieldDeclaration(fieldDec);
                fieldDec = new JavaFieldDeclaration (Modifier.PRIVATE, JavaTypeName.STRING, DC_NAME_FIELD_NAME, null);
                dataTypeClass.addFieldDeclaration(fieldDec);
    
            }
            
        
        }

        /**
         * Generate an accessor function of the form RTValue get_FieldName() {}
         * @param methodName
         * @param fieldName
         * @param fieldType
         * @param implementAtThisLevel
         * @return JavaMethod
         */
        private final JavaMethod createMethod_getField (
                                                 String methodName,
                                                 String fieldName,
                                                 JavaTypeName fieldType, 
                                                 boolean implementAtThisLevel) {
            int modifiers = Modifier.PUBLIC;
            if (implementAtThisLevel) {
                modifiers = modifiers | Modifier.FINAL;
            }
    
            // Create the method.
            JavaMethod javaMethod = new JavaMethod(modifiers, fieldType, methodName);
    
            if (implementAtThisLevel) {
                JavaField field = new JavaField.Instance (null, fieldName, fieldType);
                javaMethod.addStatement(new ReturnStatement(field));
            } else {
                // This class should throw an error for any access.  The methods will
                // be overridden by derived classes for each data constructor.
                JavaExpression getDCName = 
                    new MethodInvocation.Instance(null, GET_DC_NAME_METHOD_NAME, JavaTypeName.STRING, MethodInvocation.InvocationType.VIRTUAL);
                
                JavaExpression message = 
                    new JavaExpression.OperatorExpression.Binary(
                            JavaOperator.STRING_CONCATENATION, 
                            LiteralWrapper.make ("Cannot access field " + fieldName + " on an instance of "),
                            getDCName);
                
                JavaExpression createException = 
                    new JavaExpression.ClassInstanceCreationExpression (
                            UNSUPPORTED_OPERATION_TYPE_NAME, 
                            message,
                            JavaTypeName.STRING);
                
                javaMethod.addStatement(new JavaStatement.ThrowStatement(createException));
            }
            
            return javaMethod;
        }
    
        private final JavaMethod createMethod_hashCode (
                Set<FieldName> fieldNames, 
                Map<FieldName, JavaTypeName> fieldNameToType) {
        
            JavaMethod hashCode =
                new JavaMethod (
                        Modifier.PUBLIC | Modifier.FINAL, 
                        JavaTypeName.INT, 
                        "hashCode");
            
            JavaField hashCodeField =
                new JavaField.Instance (
                        null, 
                        HASH_CODE_FIELD_NAME, 
                        JavaTypeName.INT);
            
            JavaExpression condition =
                new OperatorExpression.Binary (
                        JavaOperator.EQUALS_INT,
                        hashCodeField,
                        LiteralWrapper.make(new Integer(0)));
            
            JavaStatement.Block thenBlock = new JavaStatement.Block();
            
            IfThenElseStatement ifThen = 
                new IfThenElseStatement (condition, thenBlock);
            
            hashCode.addStatement (ifThen);
            
            // build up the hashcode value.
            JavaExpression.LocalVariable result =
                new LocalVariable ("result", JavaTypeName.INT);
            LocalVariableDeclaration localVarDecl =
                new LocalVariableDeclaration(result, LiteralWrapper.make(new Integer(17)));
            thenBlock.addStatement(localVarDecl);
            
            LiteralWrapper thirtySeven = LiteralWrapper.make (new Integer(37));
            JavaExpression thirtySevenTimesResult =
                new OperatorExpression.Binary(JavaOperator.MULTIPLY_INT, thirtySeven, result);

            LiteralWrapper zero = LiteralWrapper.make (new Integer (0));
            LiteralWrapper one  = LiteralWrapper.make (new Integer (1));

            // Start by including dc name in the hashcode.
            // get objects hashcode
            JavaExpression nameExpression =
                new MethodInvocation.Instance (
                        new MethodInvocation.Instance(
                                null,
                                GET_DC_NAME_METHOD_NAME,
                                JavaTypeName.STRING,
                                MethodInvocation.InvocationType.VIRTUAL), 
                        "hashCode", 
                        JavaTypeName.INT, 
                        MethodInvocation.InvocationType.VIRTUAL);

            JavaExpression newResult =
                new OperatorExpression.Binary (JavaOperator.ADD_INT, thirtySevenTimesResult, nameExpression);
            JavaExpression assignResult = new Assignment (result, newResult);
            thenBlock.addStatement(new ExpressionStatement (assignResult));
            
            // Now get the contribution from each dc field.
            for (FieldName fn : fieldNames) {
                String javaFieldName = typeConstructorInfo.fieldJavaNames.get (fn);
                JavaTypeName fieldType = fieldNameToType.get (fn);
                JavaField field = new JavaField.Instance(null, javaFieldName, fieldType);
                
                JavaExpression fieldExpression;
                if (fieldType instanceof JavaTypeName.Primitive) {
                    if (fieldType instanceof JavaTypeName.Primitive.Boolean) {
                        fieldExpression =
                            new OperatorExpression.Ternary (field, zero, one);
                    } else if (fieldType instanceof JavaTypeName.Primitive.Byte ||
                            fieldType instanceof JavaTypeName.Primitive.Char ||
                            fieldType instanceof JavaTypeName.Primitive.Short) {
                        fieldExpression =
                            new CastExpression(JavaTypeName.INT, field);
                    }else if (fieldType instanceof JavaTypeName.Primitive.Double) {
                        // long f = Double.doubleToLongBits(f);
                        // result = (int) (f ^ (f >>> 32));
                        JavaExpression.LocalVariable f =
                            new LocalVariable ("f", JavaTypeName.LONG);
                        JavaExpression initializeF = 
                            new MethodInvocation.Static (
                                    JavaTypeName.DOUBLE_OBJECT,
                                    "doubleToLongBits",
                                    field,
                                    JavaTypeName.DOUBLE,
                                    JavaTypeName.LONG);
                        LocalVariableDeclaration fVarDecl =
                            new LocalVariableDeclaration(f, initializeF);
                        thenBlock.addStatement(fVarDecl);
                        
                        fieldExpression = 
                            new OperatorExpression.Binary (
                                JavaOperator.SHIFTR_UNSIGNED_LONG,
                                f,
                                LiteralWrapper.make(new Integer (32)));
                        fieldExpression = 
                            new OperatorExpression.Binary (
                                    JavaOperator.BITWISE_XOR_LONG,
                                    f,
                                    fieldExpression);
                        fieldExpression =
                            new CastExpression (JavaTypeName.INT, fieldExpression);
                        
                    } else if (fieldType instanceof JavaTypeName.Primitive.Float) {
                        fieldExpression = 
                            new MethodInvocation.Static (
                                    JavaTypeName.FLOAT_OBJECT,
                                    "floatToIntBits",
                                    field,
                                    JavaTypeName.FLOAT,
                                    JavaTypeName.INT);
                    } else if (fieldType instanceof JavaTypeName.Primitive.Int) {
                        fieldExpression = field;
                    } else if (fieldType instanceof JavaTypeName.Primitive.Long) {
                        fieldExpression = 
                            new OperatorExpression.Binary (
                                JavaOperator.SHIFTR_UNSIGNED_LONG,
                                field,
                                LiteralWrapper.make(new Integer (32)));
                        fieldExpression = 
                            new OperatorExpression.Binary (
                                    JavaOperator.BITWISE_XOR_LONG,
                                    field,
                                    fieldExpression);
                        fieldExpression =
                            new CastExpression (JavaTypeName.INT, fieldExpression);
                    } else {
                        fieldExpression =
                            new MethodInvocation.Instance (field, "hashCode", JavaTypeName.INT, MethodInvocation.InvocationType.VIRTUAL);
                    }
                    
                } else {
                    // get objects hashcode
                    fieldExpression =
                        new MethodInvocation.Instance (field, "hashCode", JavaTypeName.INT, MethodInvocation.InvocationType.VIRTUAL);
                }

                newResult =
                    new OperatorExpression.Binary (JavaOperator.ADD_INT, thirtySevenTimesResult, fieldExpression);
                
                assignResult = new Assignment (result, newResult);
                thenBlock.addStatement(new ExpressionStatement (assignResult));
            }
            
            // Assign the hash code value to the hashCode field.
            JavaExpression assign = new JavaExpression.Assignment(hashCodeField, result);
            thenBlock.addStatement(new ExpressionStatement (assign));
            
            // Return the initialized hashCode field.
            hashCode.addStatement (new ReturnStatement (hashCodeField));
            
            return hashCode;
        }
        
        private final JavaMethod createMethod_equals (
                JavaTypeName typeName, 
                Set<FieldName> fieldNames,  
                Map<FieldName, JavaTypeName> fieldNameToType) {
            
            String argName = "object";
            
            JavaMethod equals = 
                new JavaMethod(
                        Modifier.PUBLIC | Modifier.FINAL, 
                        JavaTypeName.BOOLEAN, 
                        argName,
                        JavaTypeName.OBJECT,
                        false,
                        "equals");

            MethodVariable objectArg = new MethodVariable (argName);
            JavaField thisField = new JavaField.This(typeName);
            
            // if (object == this) {
            //     return true;
            // }
            JavaExpression condition = 
                new JavaExpression.OperatorExpression.Binary(
                        JavaOperator.EQUALS_OBJECT,
                        thisField,
                        objectArg);
            JavaStatement.IfThenElseStatement ifThen =
                new IfThenElseStatement(
                        condition,
                        new ReturnStatement(LiteralWrapper.TRUE));
            
            equals.addStatement(ifThen);

            // if (object == null) {
            //     return false;
            // }
            condition =
                new JavaExpression.OperatorExpression.Binary(JavaOperator.EQUALS_OBJECT, objectArg, LiteralWrapper.NULL);
            ifThen =
                new IfThenElseStatement (condition, new ReturnStatement(LiteralWrapper.FALSE));
            equals.addStatement(ifThen);
            
            // if (!(object instanceOf ThisType)) {
            //     return false;
            // }
            condition = 
                new JavaExpression.InstanceOf(objectArg, typeName);
            condition =
                new OperatorExpression.Unary(JavaOperator.LOGICAL_NEGATE, condition);
            ifThen = 
                new IfThenElseStatement (
                        condition,
                        new ReturnStatement(LiteralWrapper.FALSE));
            equals.addStatement(ifThen);
            
            // ThisType castObject = (ThisType)object;
            LocalVariable localVar =
                new LocalVariable ("cast" + argName, typeName);
            
            JavaStatement localVarDecl = 
                new JavaStatement.LocalVariableDeclaration (
                        localVar,
                        new JavaExpression.CastExpression(typeName, objectArg));
            equals.addStatement (localVarDecl);
            

            // Check DC name
            // if (!getDCName().equals(castObject.getDCName())) {
            //     return false;
            // }
            JavaExpression thisGetDCName = 
                new MethodInvocation.Instance(null, GET_DC_NAME_METHOD_NAME, JavaTypeName.STRING, MethodInvocation.InvocationType.VIRTUAL);
            JavaExpression otherGetDCName =
                new MethodInvocation.Instance(localVar, GET_DC_NAME_METHOD_NAME, JavaTypeName.STRING, MethodInvocation.InvocationType.VIRTUAL);
            JavaExpression compareDCNames = 
                new MethodInvocation.Instance(thisGetDCName, "equals", otherGetDCName, JavaTypeName.OBJECT, JavaTypeName.BOOLEAN, MethodInvocation.InvocationType.VIRTUAL);
            compareDCNames = new OperatorExpression.Unary(JavaOperator.LOGICAL_NEGATE, compareDCNames);

            ifThen = new IfThenElseStatement(compareDCNames, new ReturnStatement(LiteralWrapper.FALSE));
            equals.addStatement(ifThen);
            
            if (fieldNames != null && fieldNames.size() > 0) {
                // Now we need to compare equality for the various fields.
                Iterator<FieldName> fields = fieldNames.iterator();
                FieldName fn = fields.next ();
                JavaTypeName fieldType = (JavaTypeName)fieldNameToType.get(fn);
                JavaExpression compare = makeFieldComparison (fn, fieldType, localVar);
                
                while (fields.hasNext()) {
                    fn = fields.next ();
                    fieldType = (JavaTypeName)fieldNameToType.get(fn);
                    JavaExpression nextCompare = makeFieldComparison (fn, fieldType, localVar);
                    
                    compare =
                        new OperatorExpression.Binary (JavaOperator.CONDITIONAL_AND, compare, nextCompare);
                }
                equals.addStatement(new ReturnStatement(compare));
            } else {
                equals.addStatement(new ReturnStatement(LiteralWrapper.TRUE));
            }
            
            return equals;
        }
        
        private final JavaExpression makeFieldComparison (
                FieldName fieldName, 
                JavaTypeName fieldType,
                JavaExpression other) {

            String javaFieldName = (String)typeConstructorInfo.fieldJavaNames.get(fieldName);

            JavaExpression thisField = new JavaField.Instance (null, javaFieldName, fieldType);
            JavaExpression otherField = new JavaField.Instance (other, javaFieldName, fieldType);
            
            if (fieldType instanceof JavaTypeName.Primitive) {
                // We need to use the correct version of the
                // == operator.
                // this.field == other.field
                
                JavaOperator operator;
                
                if (fieldType instanceof JavaTypeName.Primitive.Boolean) {
                    operator = JavaOperator.CONDITIONAL_AND;
                } else if (fieldType instanceof JavaTypeName.Primitive.Byte) {
                    operator = JavaOperator.EQUALS_BYTE;
                } else if (fieldType instanceof JavaTypeName.Primitive.Char) {
                    operator = JavaOperator.EQUALS_CHAR;
                } else if (fieldType instanceof JavaTypeName.Primitive.Double) {
                    operator = JavaOperator.EQUALS_BYTE;
                } else if (fieldType instanceof JavaTypeName.Primitive.Float) {
                    operator = JavaOperator.EQUALS_FLOAT;
                } else if (fieldType instanceof JavaTypeName.Primitive.Int) {
                    operator = JavaOperator.EQUALS_INT;
                } else if (fieldType instanceof JavaTypeName.Primitive.Long) {
                    operator = JavaOperator.EQUALS_LONG;
                } else if (fieldType instanceof JavaTypeName.Primitive.Short) {
                    operator = JavaOperator.EQUALS_SHORT;
                }else {
                    operator = JavaOperator.EQUALS_OBJECT;
                }
                
                JavaExpression compare =
                    new OperatorExpression.Binary (operator, thisField, otherField);
                
                return compare;
            } else {
                // Invoke the equals method.
                // (this.field == null) ? (other.field == null) | this.field.equals(other.field)
                
                
                JavaExpression condition =
                    new OperatorExpression.Binary (
                            JavaOperator.EQUALS_OBJECT, 
                            thisField,
                            LiteralWrapper.NULL);
                
                JavaExpression then =
                    new OperatorExpression.Binary (
                            JavaOperator.EQUALS_OBJECT,
                            otherField,
                            LiteralWrapper.NULL);
                
                JavaExpression elsePart =
                    new MethodInvocation.Instance (
                            thisField, 
                            "equals", 
                            otherField, 
                            JavaTypeName.OBJECT, 
                            JavaTypeName.BOOLEAN,
                            MethodInvocation.InvocationType.VIRTUAL);
                
                JavaExpression ternary =
                    new OperatorExpression.Ternary (
                            condition,
                            then,
                            elsePart);
                
                return ternary;
            }
        }
        
        private final JavaMethod createMethod_toString (Set<FieldName> fieldNames, Map<FieldName, JavaTypeName> fieldTypes) {
            
            JavaMethod toString = 
                new JavaMethod(
                        Modifier.PUBLIC | Modifier.FINAL, 
                        JavaTypeName.STRING, 
                        "toString");
            
            JavaExpression getDCName = 
                new JavaExpression.MethodInvocation.Instance(
                        null,
                        GET_DC_NAME_METHOD_NAME,
                        JavaTypeName.STRING,
                        MethodInvocation.InvocationType.VIRTUAL);
            
            JavaExpression message = 
                new JavaExpression.OperatorExpression.Binary(
                        JavaOperator.STRING_CONCATENATION, 
                        getDCName,
                        LiteralWrapper.make("\n"));
            
            for (FieldName fn : fieldNames) {
                // There will only be one field type.
                JavaTypeName fieldType = (JavaTypeName)fieldTypes.get(fn);
                String javaFieldName = (String)typeConstructorInfo.fieldJavaNames.get(fn);
                JavaExpression field =
                    new JavaField.Instance(null, javaFieldName, fieldType);
                
                JavaExpression fieldString;
                if (fieldType instanceof JavaTypeName.Primitive ||
                        fieldType.equals(JavaTypeName.STRING)) {
                    fieldString = field; 
                } else {
                    fieldString =
                        new MethodInvocation.Instance(field, "toString", JavaTypeName.STRING, MethodInvocation.InvocationType.VIRTUAL);
                }

                message = 
                    new JavaExpression.OperatorExpression.Binary(
                            JavaOperator.STRING_CONCATENATION, 
                            message,
                            LiteralWrapper.make("    " + fn.toString() + " = "));

                message = 
                    new JavaExpression.OperatorExpression.Binary(
                            JavaOperator.STRING_CONCATENATION, 
                            message,
                            fieldString);

                message = 
                    new JavaExpression.OperatorExpression.Binary(
                            JavaOperator.STRING_CONCATENATION, 
                            message,
                            LiteralWrapper.make("\n"));
            }
            
            toString.addStatement (new ReturnStatement (message));
            
            return toString;
        }
        
//        /**
//         * Convert a CALDocComment instance into JavaDoc.
//         * @param comment
//         * @param bindingEntity
//         * @param isClassComment whether the Javadoc comment is a class comment.
//         * @return the text of the JavaDoc comment.
//         */
//        private final String calDocCommentToJavaComment (CALDocComment comment, FunctionalAgent bindingEntity, boolean isClassComment) {
//            return calDocCommentToJavaComment(comment, bindingEntity, isClassComment, null);
//        }
        
//        /**
//         * Convert a CALDocComment instance into JavaDoc.
//         * @param comment
//         * @param bindingEntity
//         * @param isClassComment whether the Javadoc comment is a class comment.
//         * @param argNames the names of arguments to use. Can be null if the default mechanism for obtaining argument names is to be used.
//         * @return the text of the JavaDoc comment.
//         */
//        private final String calDocCommentToJavaComment (CALDocComment comment, FunctionalAgent bindingEntity, boolean isClassComment, String[] argNames) {
//            String text = CALDocToJavaDocUtilities.getJavadocFromCALDocComment(comment, isClassComment, bindingEntity, argNames);
//            return text;
//        }
    }
    
    /**
     * Use this function to get the field types for a data constructor.
     * @param dc
     * @return an array of TypeExpr
     */
    private final TypeExpr[] getFieldTypesForDC (DataConstructor dc) {
        TypeExpr[] fieldTypes = new TypeExpr[dc.getArity()];
        TypeExpr dcType = dc.getTypeExpr();
        TypeExpr[] tft = dcType.getTypePieces(dc.getArity());
        System.arraycopy(tft, 0, fieldTypes, 0, fieldTypes.length);

        return fieldTypes;
    }
    
    private final String createEnumFieldName(String dcName) {
        
        //Substitute _ for # 
        //Substitute _ for .
        //Substitute _ for $
        
        StringBuffer sb = new StringBuffer();
        char c = dcName.charAt(0);
        //for the first character, don't worry about case or the underscore since the CAL language guarantees that functions start
        //with a lower case letter.
        switch (c) {
            case '#':
            case '$':
            case '.':
                sb.append('_');
                break;
                
            default:
                if (Character.isUpperCase(c)) {
                    sb.append(c);
                } else {
                    sb.append(Character.toUpperCase(c));
                }
            break;               
        }
        
        for (int i = 1, n = dcName.length(); i < n; ++i) {
            
            c = dcName.charAt(i);
            switch (c) {
                case '#':
                case '$':
                case '.':                   
                case '_':
                    sb.append("__"); //escape special characters past the first with 2 underscores.
                    break;
                    
                default:
                {
                    if (Character.isUpperCase(c)) {
                        sb.append('_');
                        sb.append(c);
                    } else {
                        sb.append(Character.toUpperCase(c));
                    }
                    break;
                }
            }        
        }

        dcName = sb.toString();
        if (isReservedWord(dcName)) {
            //there are no reserved words that start with an underscore.
            dcName = dcName + "_";
        }

        return dcName;
    }
    
    /**
     * @param name a simple name
     * @return whether the given name is a reserved word.
     * ie. either a Java language keyword, or a name of a file which cannot be created in the Windows file system.
     */
    private final boolean isReservedWord (String name) {
        if (JavaReservedWords.javaLanguageKeywords.contains(name)) {
            return true;
        }
        
        // Check for names that will conflict with reserved words in the Windows file system.
        // In this case we're looking for COM1, COM2, ... or LPT1, LPT2, ...
        // Also: CLOCK$, CON, AUX, PRN, NUL, CONFIG$
        // Note: we want to look at these in a case insensitive fashion, since in the windows file
        // system con and CON are the same.
        
        name = name.toLowerCase();
        
        // check the windows reserved words with all lower case.
        if (windowsReservedWords.contains(name)) {
            return true;
        }
        
        if (name.startsWith("com") || name.startsWith("lpt")) {
            String rest = name.substring(3);
            boolean isInt = true;
            for (int i = 0; i < rest.length(); ++i) {
                if (!Character.isDigit(rest.charAt(i))) {
                    isInt = false;
                    break;
                }
            }
            if (isInt) {
                return true;
            }
        }       
        
        return false;
    }        
    
    /**
     * Go from a TypeExpr to the corresponding JavaTypeName.
     * @param typeExpr
     * @param typeToClassMappings
     * @param moduleToPackageMappings
     * @param targetPackage
     * @return a JavaTypeName
     */
    private final JavaTypeName typeExprToTypeName (
            TypeExpr typeExpr, 
            Map<QualifiedName, JavaTypeName> typeToClassMappings,
            Map<ModuleName, String> moduleToPackageMappings,
            String targetPackage) throws UnableToResolveForeignEntityException {
        
        if (typeExpr != null) {
            if (typeExpr instanceof RecordType) {
                return JavaTypeName.make(java.util.List.class);
            }
            
            TypeConsApp typeConsApp = typeExpr.rootTypeConsApp();
            if (typeConsApp != null) {
            
                if (typeConsApp.isNonParametricType(CAL_Prelude.TypeConstructors.Boolean)) {
                    return JavaTypeName.BOOLEAN;
                } 
                    
                if(typeConsApp.getForeignTypeInfo() != null) {
                    ForeignTypeInfo fti = typeConsApp.getForeignTypeInfo();
                    return JavaTypeName.make (fti.getForeignType());
                }

                TypeConstructor typeConstructor = typeConsApp.getRoot();

                JavaTypeName typeName = (JavaTypeName)typeToClassMappings.get(typeConstructor.getName());
                if (typeName != null) {
                    return typeName;
                }

                String unqualifiedTypeConstructorName = typeConstructor.getName().getUnqualifiedName(); 
                if (unqualifiedTypeConstructorName.equals("Function")) {
                    return CAL_VALUE;
                }

                // We are going to build up a JavaTypeName based on our naming convention for generated
                // I/O classes.
                
                // Check to see if the CAL module is mapped to a Java package.
                String referencedPackage = moduleToPackageMappings.get(typeConstructor.getName().getModuleName());
                if (referencedPackage == null) {
                    // If not use the target package.
                    referencedPackage = targetPackage;
                }
                if (referencedPackage.endsWith(".")) {
                    referencedPackage = referencedPackage.substring(0, referencedPackage.lastIndexOf('.'));
                }
                if (referencedPackage.lastIndexOf('.') > 0) {
                    referencedPackage = referencedPackage.substring(0, referencedPackage.lastIndexOf('.')+1);
                } else {
                    referencedPackage = referencedPackage + ".";
                }
                
                // Create a Java class name.
                JavaTypeName typeConstructorClassName = 
                    JavaTypeName.make(referencedPackage + typeConstructor.getName().getModuleName().getLastComponent() + "." + getClassName(typeConstructor), false);
                return typeConstructorClassName;
            }
        }
        
        return JavaTypeName.OBJECT;
        
    }
    
    private final class CAL_Module_ImportGenerator<T> extends SourceModelCopier <T> {
        
        private final SourceModel.ModuleDefn moduleDefn;
        
        private final Map<String, Name.DataCons> shortNameToFullNameDataCons = new LinkedHashMap<String, Name.DataCons>();
        private final Map<String, Name.Function> shortNameToFullNameFunction = new LinkedHashMap<String, Name.Function>();
        private final Map<String, Name.TypeClass> shortNameToFullNameTypeClass = new LinkedHashMap<String, Name.TypeClass>();
        private final Map<String, Name.TypeCons> shortNameToFullNameTypeCons = new LinkedHashMap<String, Name.TypeCons>();
        private final Set<ModuleName> mustImportModules;
        
        /**
         * 
         * @param moduleDefn
         * @param mustImportModules - Set of ModuleName
         */
        CAL_Module_ImportGenerator (ModuleDefn moduleDefn, Set<ModuleName> mustImportModules) {
            this.moduleDefn = moduleDefn;
            this.mustImportModules = mustImportModules;
        }
        
        /**
         * @param cons the source model element to be traversed
         * @param arg unused argument
         * @return null
         */
        public Name.DataCons visit_Name_DataCons(
            Name.DataCons cons, T arg) {

            if (cons.isQualified()) {
                if (!cons.getModuleName().toString().equals(moduleDefn.getModuleName().toString())) {
                    Name.DataCons existingMapping = (Name.DataCons)shortNameToFullNameDataCons.get(cons.getUnqualifiedName());
                    if (existingMapping != null) {
                        // Mapping exists for short name.  Is it for this data cons.
                        if (existingMapping.getModuleName().toString().equals(cons.getModuleName().toString())) {
                            // Mapping is valid, return unqualified name.
                            return Name.DataCons.makeUnqualified(cons.getUnqualifiedName());
                        }
                    } else {
                        // Mapping doesn't exist.  Create one.
                        shortNameToFullNameDataCons.put(cons.getUnqualifiedName(), cons);
                        return Name.DataCons.makeUnqualified(cons.getUnqualifiedName());
                    }
                } else {
                    return Name.DataCons.makeUnqualified(cons.getUnqualifiedName());
                }
            }
            
            return super.visit_Name_DataCons(cons, arg);
        }

        /**
         * @param function the source model element to be traversed
         * @param arg unused argument
         * @return null
         */
        public  Name.Function visit_Name_Function(
            Name.Function function, T arg) {

            if (function.isQualified()) {
                if (!function.getModuleName().toString().equals(moduleDefn.getModuleName().toString())) {
                    Name.Function existingMapping = (Name.Function)shortNameToFullNameFunction.get(function.getUnqualifiedName());
                    if (existingMapping != null) {
                        // Mapping exists for short name.  Is it for this data cons.
                        if (existingMapping.getModuleName().toString().equals(function.getModuleName().toString())) {
                            // Mapping is valid, return unqualified name.
                            return Name.Function.makeUnqualified(function.getUnqualifiedName());
                        }
                    } else {
                        // Mapping doesn't exist.  Create one.
                        shortNameToFullNameFunction.put(function.getUnqualifiedName(), function);
                        return Name.Function.makeUnqualified(function.getUnqualifiedName());
                    }
                } else {
                    return Name.Function.makeUnqualified(function.getUnqualifiedName());
                }
            }

            return super.visit_Name_Function(function, arg);
        }

        /**
         * @param typeClass the source model element to be traversed
         * @param arg unused argument
         * @return null
         */
        public Name.TypeClass visit_Name_TypeClass(
            Name.TypeClass typeClass, T arg) {

            if (typeClass.isQualified()) {
                if (!typeClass.getModuleName().toString().equals(moduleDefn.getModuleName().toString())) {
                    Name.TypeClass existingMapping = (Name.TypeClass)shortNameToFullNameTypeClass.get(typeClass.getUnqualifiedName());
                    if (existingMapping != null) {
                        // Mapping exists for short name.  Is it for this data cons.
                        if (existingMapping.getModuleName().toString().equals(typeClass.getModuleName().toString())) {
                            // Mapping is valid, return unqualified name.
                            return Name.TypeClass.makeUnqualified(typeClass.getUnqualifiedName());
                        }
                    } else {
                        // Mapping doesn't exist.  Create one.
                        shortNameToFullNameTypeClass.put(typeClass.getUnqualifiedName(), typeClass);
                        return Name.TypeClass.makeUnqualified(typeClass.getUnqualifiedName());
                    }
                } else {
                    return Name.TypeClass.makeUnqualified(typeClass.getUnqualifiedName());
                }
            }

            return super.visit_Name_TypeClass(typeClass, arg);
        }

        /**
         * @param cons the source model element to be traversed
         * @param arg unused argument
         * @return null
         */
        public Name.TypeCons visit_Name_TypeCons(
            Name.TypeCons cons, T arg) {
            if (cons.isQualified()) {
                if (!cons.getModuleName().toString().equals(moduleDefn.getModuleName().toString())) {
                    Name.TypeCons existingMapping = (Name.TypeCons)shortNameToFullNameTypeCons.get(cons.getUnqualifiedName());
                    if (existingMapping != null) {
                        // Mapping exists for short name.  Is it for this data cons.
                        if (existingMapping.getModuleName().toString().equals(cons.getModuleName().toString())) {
                            // Mapping is valid, return unqualified name.
                            return Name.TypeCons.makeUnqualified(cons.getUnqualifiedName());
                        }
                    } else {
                        // Mapping doesn't exist.  Create one.
                        shortNameToFullNameTypeCons.put(cons.getUnqualifiedName(), cons);
                        return Name.TypeCons.makeUnqualified(cons.getUnqualifiedName());
                    }
                } else {
                    return Name.TypeCons.makeUnqualified(cons.getUnqualifiedName());
                }
            }

            return super.visit_Name_TypeCons(cons, arg);
        }
        
        /**
         * @param defn the source model element to be copied
         * @param arg unused argument
         * @return a deep copy of the source model element
         */
        public ModuleDefn visit_ModuleDefn(
            ModuleDefn defn, T arg) {

            CALDoc.Comment.Module newCALDocComment = null;
            if (defn.getCALDocComment() != null) {
                newCALDocComment = (CALDoc.Comment.Module)defn.getCALDocComment().accept(this, arg);
            }

            final int nFriendModules = defn.getNFriendModules();
            Friend[] newFriendModules = new Friend[nFriendModules];
            for (int i = 0; i < nFriendModules; i++) {
                newFriendModules[i] = (Friend)defn.getNthFriendModule(i).accept(this, arg);
            }

            final int nTopLevelDefns = defn.getNTopLevelDefns();
            TopLevelSourceElement[] newTopLevelDefns = new TopLevelSourceElement[nTopLevelDefns];
            for (int i = 0; i < nTopLevelDefns; i++) {
                newTopLevelDefns[i] = (TopLevelSourceElement)defn.getNthTopLevelDefn(i).accept(this, arg);
            }

            SourceModel.Import newImportedModules[] = buildImports();
            
            return ModuleDefn.make(
                    newCALDocComment, 
                    (Name.Module)defn.getModuleName().accept(this, arg), 
                    newImportedModules, 
                    newFriendModules, 
                    newTopLevelDefns);
        }
        
        private SourceModel.Import[] buildImports () {
            Set<String> moduleNames = new LinkedHashSet<String>();
            
            // Note: SourceModel.Name, and derived classes, do not implement 'equals' or 'hashCode'
            // so we can't use them in hash sets, hash maps, etc.
            
            
            // Build up the set of modules to import.
            for (Name.Qualifiable name : shortNameToFullNameDataCons.values()) {
                String moduleName = name.getModuleName().toString();
                moduleNames.add(moduleName);
            }
            for (Name.Qualifiable name : shortNameToFullNameFunction.values()) {
                String moduleName = name.getModuleName().toString();
                moduleNames.add(moduleName);
            }
            for (Name.Qualifiable name : shortNameToFullNameTypeClass.values()) {
                String moduleName = name.getModuleName().toString();
                moduleNames.add(moduleName);
            }
            for (Name.Qualifiable name : shortNameToFullNameTypeCons.values()) {
                String moduleName = name.getModuleName().toString();
                moduleNames.add(moduleName);
            }
            
            List<SourceModel.Import> allImports = new ArrayList<SourceModel.Import>();
            SourceModel.Import preludeImport = null;
            String preludeModuleName = CAL_Prelude.MODULE_NAME.toString();
            
            // Now build up imports for each module.
            for (String moduleNameString : moduleNames) {

                List<String> dataConsNameStringList = new ArrayList<String>();
                List<String> functionNameStringList = new ArrayList<String>();
                List<String> typeConsNameStringList = new ArrayList<String>();
                List<String> typeClassNameStringList = new ArrayList<String>();
              
                for (String shortName :shortNameToFullNameDataCons.keySet()) {
                    Name.Qualifiable qn = shortNameToFullNameDataCons.get(shortName);
                    if (qn.getModuleName().toString().equals(moduleNameString)) {
                        dataConsNameStringList.add(shortName);
                    }
                }
                for (String shortName : shortNameToFullNameFunction.keySet()) {
                    Name.Qualifiable qn = shortNameToFullNameFunction.get(shortName);
                    if (qn.getModuleName().toString().equals(moduleNameString)) {
                        functionNameStringList.add(shortName);
                    }
                }
                for (String shortName : shortNameToFullNameTypeClass.keySet()) {
                    Name.Qualifiable qn = shortNameToFullNameTypeClass.get(shortName);
                    if (qn.getModuleName().toString().equals(moduleNameString)) {
                        typeClassNameStringList.add(shortName);
                    }
                }         
                for (String shortName : shortNameToFullNameTypeCons.keySet()) {
                    Name.Qualifiable qn = shortNameToFullNameTypeCons.get(shortName);
                    if (qn.getModuleName().toString().equals(moduleNameString)) {
                        typeConsNameStringList.add(shortName);
                    }
                }
                
                SourceModel.Import importUsing = Import.make (
                        ModuleName.make(moduleNameString), 
                        (String[])functionNameStringList.toArray(new String[]{}),
                        (String[])dataConsNameStringList.toArray(new String[]{}),
                        (String[])typeConsNameStringList.toArray(new String[]{}),
                        (String[])typeClassNameStringList.toArray(new String[]{})
                        );
                
                if (moduleNameString.equals(preludeModuleName)) {
                    preludeImport = importUsing;
                } else {
                    allImports.add(importUsing);
                }
            }
            
            if (preludeImport == null) {
                preludeImport = SourceModel.Import.make(CAL_Prelude.MODULE_NAME);
            }
            allImports.add(0, preludeImport);
            
            for (ModuleName moduleName : mustImportModules) {
                String moduleNameString = moduleName.toString();
                
                boolean found = false;
                for (Iterator<String> moduleNamesIterator = moduleNames.iterator(); moduleNamesIterator.hasNext(); ) {
                    if (moduleNameString.equals((String)moduleNamesIterator.next())) {
                        found = true;
                    }
                }
                
                if (!found) {
                    Import imp = Import.make(moduleName);
                    allImports.add(imp);
                }
            }
            
            return (Import[])allImports.toArray(new Import[]{});
        }
        
        ModuleDefn generateImports (SourceModel.ModuleDefn moduleDefn) {
            return (ModuleDefn)moduleDefn.accept(this, null);
        }
    }

    private static final class DataConstructorInfo {
        
        /** DataConstructor associated with this information. */
        final DataConstructor dataConstructor;
    
        /** All the FieldName for this data constructor. */
        final Set<FieldName> allFieldNames;
        
        /** 
         * FieldName -> String
         * Map FieldName to the name of the Java accessor method for
         * that field in the generated Java class. 
         */
        final Map<FieldName, String> fieldJavaAccessorMethodNames;
        
        /** 
         * FieldName -> JavaTypeName
         * Map FieldName to the corresponding Java type. 
         */
        final Map<FieldName, JavaTypeName> fieldTypeNames;
        
        /**
         * Name of the inner class for the data constructor.
         */
        final String innerClassName; 
        
        /** Foreign type name for the inner class. */
        final String calForeignTypeName;
        
        /**
         * True if the data constructor has fields which are not
         * common with the other data constructors for the type.
         */
        final boolean containsFields;
        
        DataConstructorInfo (
                DataConstructor dataConstructor, 
                Map<FieldName, String> fieldAccessorMethodNames, 
                Map<FieldName, JavaTypeName> fieldTypeNames,
                boolean containsFields) {
            this.dataConstructor = dataConstructor;
            this.fieldJavaAccessorMethodNames = fieldAccessorMethodNames;
            this.fieldTypeNames = fieldTypeNames;
            this.innerClassName = fixupClassName(dataConstructor.getName().getUnqualifiedName());
            this.calForeignTypeName = "J" + innerClassName;
            this.containsFields = containsFields;
        
            allFieldNames = new LinkedHashSet<FieldName>();
            for (int i = 0, n = dataConstructor.getNArgumentNames(); i < n; ++i) {
                allFieldNames.add (dataConstructor.getNthFieldName (i));
            }
            
        }
    }

    /**
     * Class used to hold information about a TypeConstructor.
     * This information is used in generating the Java and CAL code
     * necessary to use input/output to move values between Java and
     * CAL.
     * @author rcypher
     *
     */
    private static final class TypeConstructorInfo {
        
        /** The TypeConstructor this information applies to. */
        final TypeConstructor typeConstructor;
        
        /** The name of the generated Java class corresponding to the type constructor. */
        final String javaClassName;
        
        /** The CAL name used for the foreign type declartion of the generated Java class. */
        String calForeignTypeName;
        
        /** Is the type constructor an enumeration. (i.e. all data constructors have zero fields) */
        final boolean isEnumerationType;
        
        /** Set of FieldName.  All field names from all data constructors in the type. */
        final Set<FieldName> allFieldNames; 
        
        /** 
         * FieldName -> (Set of JavaTypeName).
         * Maps a FieldName to the corresponding Java types.
         * Fields with the same names in different data constructors
         * can have different types. 
         */
        final Map<FieldName, Set<JavaTypeName>> allFieldTypeNames;
        
        /** 
         * Set of FieldName.  
         * Fields which appear with same name/type in all data constructors. 
         */
        final Set<FieldName> commonFieldNames;

        /** FieldName -> JavaTypeName */
        final Map<FieldName, JavaTypeName> commonFieldTypes;
        
        /** 
         * FieldName -> String
         * The names of the Java fields corresponding to the CAL fields. 
         */
        final Map<FieldName, String> fieldJavaNames;
        
        /** 
         * FieldName -> String
         * Map FieldName to the name of the Java accessor method
         * in the generated Java class. 
         */
        final Map<FieldName, String> fieldJavaAccessorMethodNames;
        
        
        /** 
         * FieldName -> (Map DataConstructor -> String).
         * The Java field accessor methods are declared as
         * foreign functions in the generated CAL.
         * From a FieldName you can access a Map of the 
         * data constructors containing the field to the name of
         * the foreign function in the generated CAL.
         */
        final Map<FieldName, Map<DataConstructor, String>> calFieldAccessorFunctionNames;
        
        /** 
         * FieldName -> (Map JavaTypeName -> String)
         * For a FieldName a Map of the corresponding Java types
         * to the CAL foreign name for that type. 
         */
        final Map<FieldName, Map<JavaTypeName, String>> calFieldForeignTypes;
        
        /** 
         * DataConstructor -> DataConstructorInfo
         * Information for each data constructor. 
         */
        final Map<DataConstructor, DataConstructorInfo> dataConstructorInfo = new LinkedHashMap<DataConstructor, DataConstructorInfo>();
        
        TypeConstructorInfo (
                TypeConstructor typeConstructor,
                String javaClassName,
                boolean isEnumerationType,
                Set<FieldName> allFieldNames,
                Map<FieldName, Set<JavaTypeName>> allFieldTypeNames,
                Set<FieldName> commonFieldNames,
                Map<FieldName, String> fieldJavaNames,
                Map<FieldName, String> fieldJavaAccessorMethodNames,
                Map<FieldName, Map<JavaTypeName, String>> calFieldForeignTypes,
                Map<QualifiedName, JavaTypeName> typeToClassMappings
                ) {
            this.typeConstructor = typeConstructor;
            this.javaClassName = javaClassName;
            this.isEnumerationType = isEnumerationType;
            this.allFieldNames = allFieldNames;
            this.allFieldTypeNames = allFieldTypeNames;
            this.commonFieldNames = commonFieldNames;
            this.fieldJavaNames = fieldJavaNames;
            this.fieldJavaAccessorMethodNames = fieldJavaAccessorMethodNames;
            this.calFieldForeignTypes = calFieldForeignTypes;
            
            this.calForeignTypeName = "J" + javaClassName;
            this.calFieldAccessorFunctionNames = new LinkedHashMap<FieldName, Map<DataConstructor, String>>();
            
            this.commonFieldTypes = new LinkedHashMap<FieldName, JavaTypeName>();
            for (FieldName fn : commonFieldNames) {
                Set<JavaTypeName> fieldTypes = this.allFieldTypeNames.get(fn);
                assert (fieldTypes.size() == 1);
                commonFieldTypes.put(fn, fieldTypes.iterator().next());
            }
            
            for (int i = 0, n = typeConstructor.getNDataConstructors(); i < n; ++i) {
                DataConstructor dc = typeConstructor.getNthDataConstructor(i);
                for (int j = 0, k = dc.getArity(); j < k; ++j) {
                    FieldName fn = dc.getNthFieldName(j);

                    Map<DataConstructor, String> calAccessorNames = calFieldAccessorFunctionNames.get(fn);
                    if (calAccessorNames == null) {
                        calAccessorNames = new LinkedHashMap<DataConstructor, String>();
                        calFieldAccessorFunctionNames.put(fn, calAccessorNames);
                    }

                    String javaAccessorName = (String)fieldJavaAccessorMethodNames.get(fn);
                    
                    String className;
                    if (commonFieldNames.contains(fn)) {
                        className = javaClassName;
                    } else {
                        className = fixupClassName(dc.getName().getUnqualifiedName());
                    }
                    String calAccessorName = "j" + className + "_" + javaAccessorName;
                    calAccessorNames.put(dc, calAccessorName);
                    
                }
            }
            
        }
    }

    /** List of LogRecord */
    private final List<LogRecord> ioLogMessages = new ArrayList<LogRecord>();
    
    IO_Source_Generator () {
        
    }
    
    private void logMessage (Level level, String message) {
        ioLogMessages.add(new LogRecord(level, message));
    }
    
    private final String getClassName (TypeConstructor typeConstructor) {
        String unqualifiedName = typeConstructor.getName().getUnqualifiedName() + "_";
        return fixupClassName(unqualifiedName);
    }
    
    /**
     * Return the name of the class, in a form that can be used in source code.
     * eg. [[B ==> byte[][].
     *     CALExecutor$ForeignFunctionException ==> CALExecutor.ForiegnFunctionException.
     * @param name
     * @return String
     */
    private static final String fixupClassName (String name) {
        
        // Count the number of array dimensions (if any).
        int i = 0;
        while (name.startsWith("[")) {
            i++;
            name = name.substring(1);
        }
        
        if (name.startsWith ("L") && name.endsWith(";")) {
            // This is a fully qualified class name.
            name = name.substring (1, name.length() - 1);
        } else 
        if (name.equals ("Z")) {
            // boolean
            name = "boolean";
        } else
        if (name.equals ("B")) {
            name = "byte";
        } else
        if (name.equals ("C")) {
            name = "char";
        } else 
        if (name.equals("S")) {
            name = "short";
            
        } else
        if (name.equals ("I")) {
            name = "integer";
        } else
        if (name.equals ("J")) {
            name = "long";
        } else 
        if (name.equals ("F")) {
            name = "float";
        } else 
        if (name.equals("D")) {
            name = "double";
        }
        
        for (int j = 0; j < i; ++j) {
            name = name + "[]";
        }
        
        // Substitute . for $
        name = name.replace ('$', '.');
        
        return name;
    }

    private final Set<FieldName> getCommonFieldNames (TypeConstructor typeConstructor) {
        Set<FieldName> commonFieldNames = new LinkedHashSet<FieldName>();

        int nDCs = typeConstructor.getNDataConstructors();
        
        DataConstructor firstDC = typeConstructor.getNthDataConstructor(0);
        for (int i = 0; i < firstDC.getArity(); ++i) {
            FieldName fn = firstDC.getNthFieldName(i);
            commonFieldNames.add(fn);
        }
        
        for (int i = 1; i < nDCs; ++i) {
            DataConstructor dc = typeConstructor.getNthDataConstructor(i);
            Set<FieldName> commonFieldNamesClone = new LinkedHashSet<FieldName>(commonFieldNames);
            
            for (int j = 0, dcArity = dc.getArity(); j < dcArity; ++j) {
                commonFieldNamesClone.remove(dc.getNthFieldName(j));
            }
            
            commonFieldNames.removeAll(commonFieldNamesClone);
            
        }
        
        return commonFieldNames;
    }

    private final String getJavaFieldNameFromFieldName (FieldName fn) {
        String fieldName = fixupFieldName(fn.toString());
        if (!fieldName.startsWith("_")) {
            fieldName = "_" + fieldName;
        }
        return fieldName;
    }
    
    /**
     * When deconstructing a data type the names assigned to the members
     * can have conflicts with java reserved words.  We fix this by
     * prepending a '$'.  The '$' is used to avoid creating a new conflict
     * with another CAL variable.
     * @param varName
     * @return String
     */
    private final String fixupFieldName(String varName) {
        if (JavaReservedWords.javaLanguageKeywords.contains(varName)) {
            return varName + "_";
        }

        // The optimizer will generate symbols sometimes where the first character
        // of the name between the last two '$' is a digit 
        
        if (Character.isDigit(varName.charAt(0))){
            return "_" + varName;
        }
        
        if (varName.equals("QualifiedName")) {
            varName = varName + "_";
        }
        varName = varName.replace ('.', '_');
        varName = varName.replace ('#', '_');
        
        return varName;
    }
    

}
