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
 * CALToJavaNames.java
 * Created: Oct 28, 2003 2:52:51 PM
 * By: RCypher
 */
package org.openquark.cal.internal.machine.lecc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.openquark.cal.compiler.DataConstructor;
import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.TypeConstructor;
import org.openquark.cal.internal.javamodel.JavaReservedWords;
import org.openquark.cal.internal.javamodel.JavaTypeName;
import org.openquark.cal.internal.machine.lecc.LECCModule.FunctionGroupInfo;
import org.openquark.cal.internal.module.Cal.Core.CAL_Debug_internal;
import org.openquark.cal.internal.module.Cal.Core.CAL_Dynamic_internal;
import org.openquark.cal.internal.module.Cal.Core.CAL_Exception_internal;
import org.openquark.cal.internal.module.Cal.Core.CAL_Prelude_internal;
import org.openquark.cal.internal.module.Cal.Core.CAL_Record_internal;
import org.openquark.cal.internal.module.Cal.Utilities.CAL_QuickCheck_internal;
import org.openquark.cal.internal.runtime.lecc.LECCMachineConfiguration;
import org.openquark.cal.machine.Module;
import org.openquark.cal.machine.ProgramResourceLocator;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;


/**
 * This class is used to manage the conversion of CAL names
 * to java names.
 * All conversion should be done through the members of this class.
 * Creation: Oct 28, 2003
 * @author RCypher
 */
final class CALToJavaNames {
    
    private static final String TYPE_PREFIX = "TYPE_";
    private static final String DATACONSTRUCTOR_PREFIX = "CAL_";
    private static final boolean TRUNCATE_LONG_CLASS_NAMES = LECCMachineConfiguration.isLeccRuntimeStatic();
    
    private static final int OUTER_CLASS_NAME_LENGTH_LIMIT = 80;
    private static final int INNER_CLASS_NAME_LENGTH_LIMIT = OUTER_CLASS_NAME_LENGTH_LIMIT + 15;
    
    /** Track number of supercombinator names that have been truncated because they are too long. */
    private static final AtomicInteger nTruncatedNames = new AtomicInteger(0);
    
    /**
     * (String -> JavaTypeName)
     * Map of names of primitive functions that are implemented in lecc in the lecc.functions package
     * to their defining JavaTypeName constant.
     * Basically these are the primitive functions that are not simply equivalent to Java primitives
     * (as are things like Prelude.addInt).
     * If this map is modified by code outside of the static initialization block it will need
     * to be synchronized.
     */
    private static final Map<QualifiedName, JavaTypeName> primitiveFunctionsMap = new HashMap<QualifiedName, JavaTypeName>();
    static {
        primitiveFunctionsMap.put(CAL_Prelude_internal.Functions.equalsRecord, JavaTypeNames.RTEQUALS_RECORD);
        primitiveFunctionsMap.put(CAL_Prelude_internal.Functions.notEqualsRecord, JavaTypeNames.RTNOT_EQUALS_RECORD);
        primitiveFunctionsMap.put(CAL_Prelude_internal.Functions.compareRecord, JavaTypeNames.RTCOMPARE_RECORD);
        primitiveFunctionsMap.put(CAL_Prelude_internal.Functions.recordToJListPrimitive, JavaTypeNames.RTRECORD_TO_JLIST_PRIMITIVE);
        primitiveFunctionsMap.put(CAL_Record_internal.Functions.recordToJRecordValuePrimitive, JavaTypeNames.RTRECORD_TO_JRECORDVALUE_PRIMITIVE);
        primitiveFunctionsMap.put(CAL_Record_internal.Functions.strictRecordPrimitive, JavaTypeNames.RTSTRICT_RECORD_PRIMITIVE);
        primitiveFunctionsMap.put(CAL_Prelude_internal.Functions.recordFromJMapPrimitive, JavaTypeNames.RTRECORD_FROM_JMAP_PRIMITIVE);
        primitiveFunctionsMap.put(CAL_Prelude_internal.Functions.recordFromJListPrimitive, JavaTypeNames.RTRECORD_FROM_JLIST_PRIMITIVE);
        primitiveFunctionsMap.put(CAL_Dynamic_internal.Functions.recordFieldTypePrimitive, JavaTypeNames.RTRECORD_FIELD_TYPE_PRIMITIVE);
        primitiveFunctionsMap.put(CAL_Dynamic_internal.Functions.recordFieldValuePrimitive, JavaTypeNames.RTRECORD_FIELD_VALUE_PRIMITIVE);
        primitiveFunctionsMap.put(CAL_Dynamic_internal.Functions.insertOrdinalRecordFieldPrimitive, JavaTypeNames.RTINSERT_ORDINAL_RECORD_FIELD_PRIMITIVE);
        primitiveFunctionsMap.put(CAL_Dynamic_internal.Functions.insertTextualRecordFieldPrimitive, JavaTypeNames.RTINSERT_TEXTUAL_RECORD_FIELD_PRIMITIVE);
        primitiveFunctionsMap.put(CAL_Dynamic_internal.Functions.appendRecordPrimitive, JavaTypeNames.RTAPPEND_RECORD_PRIMITIVE);
        primitiveFunctionsMap.put(CAL_Prelude_internal.Functions.recordTypeDictionary, JavaTypeNames.RTRECORD_TYPE_DICTIONARY);
        primitiveFunctionsMap.put(CAL_Prelude.Functions.seq, JavaTypeNames.RTSEQ);        
        primitiveFunctionsMap.put(CAL_Prelude.Functions.deepSeq, JavaTypeNames.RTDEEP_SEQ);        
        primitiveFunctionsMap.put(CAL_Prelude_internal.Functions.ordinalValue, JavaTypeNames.RTORDINAL_VALUE);
        primitiveFunctionsMap.put(CAL_Prelude.Functions.error, JavaTypeNames.RTERROR);
        primitiveFunctionsMap.put(CAL_Exception_internal.Functions.primThrow, JavaTypeNames.RTTHROW);
        primitiveFunctionsMap.put(CAL_Exception_internal.Functions.primCatch, JavaTypeNames.RTCATCH);
        primitiveFunctionsMap.put(CAL_QuickCheck_internal.Functions.arbitraryRecordPrimitive, JavaTypeNames.RTARBITRARY_RECORD_PRIMITIVE);
        primitiveFunctionsMap.put(CAL_QuickCheck_internal.Functions.coarbitraryRecordPrimitive, JavaTypeNames.RTCOARBITRARY_RECORD_PRIMITIVE);
        primitiveFunctionsMap.put(CAL_Debug_internal.Functions.showRecord, JavaTypeNames.RTSHOW_RECORD);

        primitiveFunctionsMap.put(CAL_Record_internal.Functions.buildListPrimitive, JavaTypeNames.RTRECORD_TO_LIST_PRIMITIVE);
        primitiveFunctionsMap.put(CAL_Record_internal.Functions.buildRecordPrimitive, JavaTypeNames.RTLIST_TO_RECORD_PRIMITIVE);
    }
    
    /** 
     * These are not valid file names in Windows. In addition there is the com1, com2, ..., lpt1, lpt2, ... family of names that are 
     * parameterized by an integer. Note that Windows is case-insensitive, so check that the lower case of your string is not in this
     * set.
     */
    static private final Set<String> windowsReservedWords = new HashSet<String>();
    static {        
        windowsReservedWords.add ("clock$");
        windowsReservedWords.add ("con");
        windowsReservedWords.add ("prn");
        windowsReservedWords.add ("nul");
        windowsReservedWords.add ("config$");
        windowsReservedWords.add ("aux");
    }
    
    /**
     * Take a supercombinator name and do the appropriate
     * transformations to create a valid java class name.
     * Note: this is an unqualified class name.
     * @param scModule The module that the symbol is being defined in.
     * @param scName
     * @param module the LECCModule instance corresponding to either the module defining the entity, or one of its dependent modules.
     *               This is used for obtaining the appropriate {@link LECCModule.ClassNameMapper} for use in mapping names.
     * @return String
     */
    private static String createClassNameFromSC (ModuleName scModule, String scName, LECCModule module) {
        scName = module.getFunctionGroupInfo(QualifiedName.make(scModule, scName)).getFunctionGroupName();
        scName = fixupUnqualifiedName(scModule, scName, module);
        char[] ln = scName.toCharArray();
        ln[0] = Character.toUpperCase(ln[0]);
        return new String(ln);
    }
    
    /**
     * @param moduleName the name of the module in which the cal entity exists.
     * @param unqualifiedClassName the name previously returned by a call to createClassNameFromSC().
     * @param module the LECCModule instance corresponding to either the module defining the entity, or one of its dependent modules.
     *               This is used for obtaining the appropriate {@link LECCModule.ClassNameMapper} for use in mapping names.
     * @return the unqualified name of the function, or null if the class name does not represent a function
     *   whose name was generated by this class.
     */
    static String getUnqualifiedFunctionNameFromClassName(ModuleName moduleName, String unqualifiedClassName, LECCModule module) {
        if (moduleName.equals(CAL_Prelude.MODULE_NAME) && unqualifiedClassName.equals("If")) {
            return "if";
        }
        char[] ln = unqualifiedClassName.toCharArray();
        ln[0] = Character.toLowerCase(ln[0]);
        String lowerCasedFixedUpName = new String(ln);
        return module.getClassNameMapper().getOriginalName(lowerCasedFixedUpName);
    }
    
    /**
     * @param scName
     * @param module
     * @return The name of the Java field used for an instance of the named supercombinator.
     */
    static String getInstanceFieldName (QualifiedName scName, LECCModule module) {
        // Check to see if this is the only sc in the group.
        LECCModule.FunctionGroupInfo fgi = module.getFunctionGroupInfo(scName);
        if (fgi.getNFunctions() <= 1) {
            return "$instance";
        }
        
        return "$instance_" + cleanSCName(scName.getUnqualifiedName());
    }
    
    /**
     * Take a qualified supercombinator name and do the appropriate
     * transformations to create a valid java class name.
     * Note: this is an unqualified class name.
     * @param scName
     * @param module the LECCModule instance corresponding to either the module defining the entity, or one of its dependent modules.
     *               This is used for obtaining the appropriate {@link LECCModule.ClassNameMapper} for use in mapping names.
     * @return String
     */
    static String createClassNameFromSC (QualifiedName scName, LECCModule module) {
        JavaTypeName primitiveFunctionType = primitiveFunctionsMap.get(scName);
        if (primitiveFunctionType != null) {            
            return primitiveFunctionType.getUnqualifiedJavaSourceName();
        }
        
        return createClassNameFromSC(scName.getModuleName(), scName.getUnqualifiedName(), module);        
    }
    
    /**
     * Take a CAL module name and do any necessary transformations
     * to create a valid java package name.
     * @param moduleName
     * @return String
     */
    static String createPackageNameFromModule (ModuleName moduleName) {
        return ProgramResourceLocator.MODULE_PREFIX + moduleName.toSourceText().replaceAll("_", "__").replace('.', '_');
    }
    
    /**
     * Take a CAL qualified name and do any necessary transformations
     * to create a valid java package name.
     * @param qn
     * @return String
     */
    private static String createPackageNameFromModule (QualifiedName qn) {
        return createPackageNameFromModule(qn.getModuleName());
    }
    
    /**
     * Take a CAL qualified name and do any necessary transformations
     * to create a valid java package name.
     * @param module
     * @return String
     */
    static String createPackageNameFromModule (Module module) {
        return createPackageNameFromModule(module.getName());
    }
    
    /**
     * Reverse the transformation applied by createPackageNameFromModule().
     * @param packageName a valid java package name, created from createPackageNameFromModule().
     * @return the name of the module for that package.
     */
    static ModuleName createModuleNameFromPackageName (String packageName) {
        return ProgramResourceLocator.createModuleNameFromPackageNameSegment(packageName);
    }
    
    /**
     * Create a fully qualified package name from a module name.
     * @param moduleName
     * @return String
     */
    private static String createFullPackageNameFromModule (ModuleName moduleName) {
        return createFullPackageName (createPackageNameFromModule(moduleName));
    }
    
    /**
     * Return the fully qualified package name corresponding to a type.
     * @param typeCons
     * @return String
     */
    private static String createFullPackageName (TypeConstructor typeCons) {
        return createFullPackageNameFromModule(typeCons.getName().getModuleName());       
    }
    
    /**
     * Create a fully qualified package name from a qualified name.
     * @param qn
     * @return String
     */
    protected static String createFullPackageNameFromModule (QualifiedName qn) {
        return createFullPackageName (createPackageNameFromModule(qn));
    }
    
    /**
     * Create a fully qualified package name from the given package name.
     * @param packageName
     * @return String
     */
    static String createFullPackageName (String packageName) {
        return LECCMachineConfiguration.ROOT_PACKAGE + "." + packageName;
    }
    
    /**
     * Create a fully qualified class name from a module and supercombinator
     * name.
     * @param moduleName
     * @param scName
     * @param module the LECCModule instance corresponding to either the module defining the entity, or one of its dependent modules.
     *               This is used for obtaining the appropriate {@link LECCModule.ClassNameMapper} for use in mapping names.
     * @return String;
     */
    private static String createFullClassNameFromSC(ModuleName moduleName, String scName, LECCModule module) {
        String className = createClassNameFromSC(moduleName, scName, module);
        String packageName = createPackageNameFromModule(moduleName);
        return createFullClassName (packageName, className);        
    }
    
    /**
     * Create a fully qualified class name from the qualified name of a supercombinator.
     * @param scName
     * @param module the LECCModule instance corresponding to either the module defining the entity, or one of its dependent modules.
     *               This is used for obtaining the appropriate {@link LECCModule.ClassNameMapper} for use in mapping names.
     * @return String;
     */
    static String createFullClassNameFromSC (QualifiedName scName, LECCModule module) {
        JavaTypeName primitiveFunctionType = primitiveFunctionsMap.get(scName);
        if (primitiveFunctionType != null) {
            return primitiveFunctionType.getFullJavaSourceName();
        }
        return createFullClassNameFromSC(scName.getModuleName(), scName.getUnqualifiedName(), module);
    }
    
    /**
     * @param moduleName the name of the module in which the cal entity exists.
     * @param classNameForType the name previously returned by a call to createClassNameFromType().
     * @param module the LECCModule instance corresponding to either the module defining the entity, or one of its dependent modules.
     *               This is used for obtaining the appropriate {@link LECCModule.ClassNameMapper} for use in mapping names.
     * @return the original name before it was fixed up, or null if the class name does not represent a type
     *   whose name was generated by this class.
     */
    static String getUnqualifiedTypeNameFromClassName(ModuleName moduleName, String classNameForType, LECCModule module) {
        if (!classNameForType.startsWith(TYPE_PREFIX)) {
            return null;
        }
        String noPrefixTypeName = classNameForType.substring(TYPE_PREFIX.length());
        module = (LECCModule)module.findModule(moduleName);
        return module.getClassNameMapper().getOriginalName(noPrefixTypeName);
    }
    
    /**
     * Given a TypeConstructor generate a corresponding unqualified class name.
     * @param typeCons
     * @param module the LECCModule instance corresponding to either the module defining the entity, or one of its dependent modules.
     *               This is used for obtaining the appropriate {@link LECCModule.ClassNameMapper} for use in mapping names.
     * @return String
     */
    static String createClassNameFromType (TypeConstructor typeCons, LECCModule module) {
        String typeName = typeCons.getName().getUnqualifiedName();
        return TYPE_PREFIX + fixupUnqualifiedName(typeCons.getName().getModuleName(), typeName, module);
    }
    
    /**
     * Takes a data constructor and returns the name of the corresponding class.
     * i.e. TypeClass$DataConstructorClass
     * @param dc
     * @param module the LECCModule instance corresponding to either the module defining the entity, or one of its dependent modules.
     *               This is used for obtaining the appropriate {@link LECCModule.ClassNameMapper} for use in mapping names.
     * @return String
     */
    static String createClassName (DataConstructor dc, LECCModule module) {
        String typeClassName = createClassNameFromType (dc.getTypeConstructor(), module);
        String consClassName = createInnerClassNameFromDC_internal(dc, module);

        //inner classes must use a $ as a separator.
        String innerClassName = typeClassName + "$" + consClassName;
        
        return doClassNameTruncation(innerClassName, module.getName(), module, true);
    }
    
    /**
     * Internal helper for creating the name of the corresponding inner class from a data constructor.
     * @param dc
     * @param module the LECCModule instance corresponding to either the module defining the entity, or one of its dependent modules.
     *               This is used for obtaining the appropriate {@link LECCModule.ClassNameMapper} for use in mapping names.
     * @return String
     */
    private static String createInnerClassNameFromDC_internal (DataConstructor dc, LECCModule module) {
        return DATACONSTRUCTOR_PREFIX + createClassNameFromSC (dc.getName().getModuleName(), dc.getName().getUnqualifiedName(), module);
    }
    
    /**
     * Given a DataConstructor creates the name of the corresponding inner class.
     * @param dc
     * @param module the LECCModule instance corresponding to either the module defining the entity, or one of its dependent modules.
     *               This is used for obtaining the appropriate {@link LECCModule.ClassNameMapper} for use in mapping names.
     * @return String
     */
    static String createInnerClassNameFromDC (DataConstructor dc, LECCModule module) {
        // This method *cannot* directly call createInnerClassNameFromDC_internal, as the returned name needs to be
        // in sync with the name returned by createClassName (which performs name truncation on the entire class name),
        // and thus we delegate to createClassName and extract the inner class name portion from the entire class name.
        
        final String unqualifiedClassName = createClassName(dc, module);
        final int posOfLastDollarChar = unqualifiedClassName.lastIndexOf('$');
        if (posOfLastDollarChar == -1) {
            throw new IllegalStateException("The unqualified name of the inner class for a data constructor does not contain a '$'.");
        }
        return unqualifiedClassName.substring(posOfLastDollarChar + 1);
    }
    
    /**
     * Create a fully qualified class name corresponding to the given TypeConstructor.
     * @param typeCons
     * @param module the LECCModule instance corresponding to either the module defining the entity, or one of its dependent modules.
     *               This is used for obtaining the appropriate {@link LECCModule.ClassNameMapper} for use in mapping names.
     * @return String
     */
    static String createFullClassNameFromType (TypeConstructor typeCons, LECCModule module) {
        String className = createClassNameFromType (typeCons, module);
        String packageName = createFullPackageNameFromModule(typeCons.getName().getModuleName());
        return compound(packageName, className);
    }
    
    /**
     * Create a JavaTypeName corresponding to a TypeConsApp.
     * @param typeCons the name of the module that the type is defined in.
     * @param module the LECCModule instance corresponding to either the module defining the entity, or one of its dependent modules.
     *               This is used for obtaining the appropriate {@link LECCModule.ClassNameMapper} for use in mapping names.
     * @return JavaTypeName
     */
    static JavaTypeName createTypeNameFromType (TypeConstructor typeCons, LECCModule module) {
        String packageName = createFullPackageName (typeCons);
        String className = createClassNameFromType (typeCons, module);
        return JavaTypeName.make(compound(packageName, className), false);
    }
    
    /**
     * Create a JavaTypeName corresponding to the class used to 
     * represent multiple zero arity DCs for a data type.
     * @param typeCons
     * @param module the LECCModule instance corresponding to either the module defining the entity, or one of its dependent modules.
     *               This is used for obtaining the appropriate {@link LECCModule.ClassNameMapper} for use in mapping names.
     * @return JavaTypeName
     */
    static JavaTypeName createTypeNameForTagDCFromType (TypeConstructor typeCons, LECCModule module) {
        String packageName = createFullPackageName(typeCons);
        String unqualifiedClassName = createUnqualifiedClassNameForTagDCFromType(typeCons, module);
        return JavaTypeName.make(compound(packageName, unqualifiedClassName), false);
    }
    
    /**
     * @param typeCons a type constructor entity.
     * @param module the LECCModule instance corresponding to either the module defining the entity, or one of its dependent modules.
     *               This is used for obtaining the appropriate {@link LECCModule.ClassNameMapper} for use in mapping names.
     * @return the unqualified class name to use for the tag DC class for that type.
     */
    static String createUnqualifiedClassNameForTagDCFromType (TypeConstructor typeCons, LECCModule module) {
        String typeClassName = createClassNameFromType (typeCons, module);
        return typeClassName + "$TagDC";
    }
    
    /**
     * Create a JavaTypeName corresponding to a data constructor.
     * @param dc
     * @param module the LECCModule instance corresponding to either the module defining the entity, or one of its dependent modules.
     *               This is used for obtaining the appropriate {@link LECCModule.ClassNameMapper} for use in mapping names.
     * @return JavaTypeName
     */
    static JavaTypeName createTypeNameFromDC (DataConstructor dc, LECCModule module) {
        return JavaTypeName.make(createFullClassNameFromDC(dc, module), false);
    }
    
    /**
     * Create a JavaTypeName corresponding to a data constructor.
     * @param dcName
     * @param module the LECCModule instance corresponding to either the module defining the entity, or one of its dependent modules.
     * @return full class name
     */
    static String createFullClassNameFromDC (QualifiedName dcName, LECCModule module) {
        // Determine whether this DC is going to be in a shared class.
        module = (LECCModule)module.findModule(dcName.getModuleName());
        DataConstructor dc = module.getModuleTypeInfo().getDataConstructor(dcName.getUnqualifiedName());
        return createFullClassNameFromDC(dc, module);
    }
    
    /**
     * Create a class name, without package, for the generated class for 
     * a data constructor.
     * @param dc
     * @param module the LECCModule instance corresponding to either the module defining the entity, or one of its dependent modules.
     *               This is used for obtaining the appropriate {@link LECCModule.ClassNameMapper} for use in mapping names.
     * @return the class name
     */
    private static String createClassNameFromDC (DataConstructor dc, LECCModule module) {
        // Determine whether this DC is going to be in a shared class.
        TypeConstructor typeCons = dc.getTypeConstructor();
        
        boolean inSharedClass = false;
        if (dc.getArity() == 0) {
            int nTagDCs = 0;
            for (int i = 0, nDCs = typeCons.getNDataConstructors(); i < nDCs; ++i) {
                if (typeCons.getNthDataConstructor(i).getArity() == 0) {
                    nTagDCs++;

                    if (nTagDCs > 1) {
                        inSharedClass = true;
                        break;
                    }
                }
            }
        }
        
        String className; 
        if (inSharedClass) {
            className = createUnqualifiedClassNameForTagDCFromType(typeCons, module);
        } else {
            className = createClassName(dc, module);
        }
        
        return className;
    }
    
    /**
     * Create a full class name corresponding to a data constructor.
     * @param dc
     * @param module the LECCModule instance corresponding to either the module defining the entity, or one of its dependent modules.
     *               This is used for obtaining the appropriate {@link LECCModule.ClassNameMapper} for use in mapping names.
     * @return full class name
     */
    private static String createFullClassNameFromDC (DataConstructor dc, LECCModule module) {
        String packageName = createFullPackageNameFromModule(dc.getName().getModuleName());
        String className = createClassNameFromDC (dc, module);
        return compound(packageName, className);
    }
    
    /**
     * Create an unqualified (no package) class name for the inner FieldSelection
     * class associated with the given DC.
     * @param dc
     * @param module the LECCModule instance corresponding to either the module defining the entity, or one of its dependent modules.
     *               This is used for obtaining the appropriate {@link LECCModule.ClassNameMapper} for use in mapping names.
     * @return the FieldSelection class name.
     */
    static String createFieldSelectionClassNameFromDC (DataConstructor dc, LECCModule module) {
        String className = createClassNameFromDC (dc, module);
        className = className + "$FieldSelection";
        return className;
    }
    
    /**
     * @param dc
     * @param module the LECCModule instance corresponding to either the module defining the entity, or one of its dependent modules.
     *               This is used for obtaining the appropriate {@link LECCModule.ClassNameMapper} for use in mapping names.
     * @return a JavaTypeName for the inner FieldSelection class.
     */
    static JavaTypeName createFieldSelectionClassTypeNameFromDC (DataConstructor dc, LECCModule module) {
        String className = createFullClassNameFromDC (dc, module);
        className = className + "$FieldSelection";
        return JavaTypeName.make(className, false);
    }
    
    /**
     * Create a JavaTypeName corresponding to the named supercombinator.
     * @param scName
     * @param module the LECCModule instance corresponding to either the module defining the entity, or one of its dependent modules.
     *               This is used for obtaining the appropriate {@link LECCModule.ClassNameMapper} for use in mapping names.
     * @return JavaTypeName
     */
    static JavaTypeName createTypeNameFromSC (QualifiedName scName, LECCModule module) {
        
        JavaTypeName primitiveFunctionType = primitiveFunctionsMap.get(scName);
        if (primitiveFunctionType != null) {
            return primitiveFunctionType;
        }
        
        String className = createClassNameFromSC(scName, module);
        String packageName = createFullPackageNameFromModule(scName);
        return JavaTypeName.make(compound(packageName, className), false);
    }
    
    /**
     * @param scName the name of a function.
     * @param module the LECCModule instance corresponding to either the module defining the entity, or one of its dependent modules.
     *               This is used for obtaining the appropriate {@link LECCModule.ClassNameMapper} for use in mapping names.
     * @return the unqualified inner class name to use to represent a lazy app node for that function.
     */
    static String createLazyInnerClassNameFromSC (QualifiedName scName, LECCModule module) {
        return createInnerClassNameFromSC(scName, module, false);
    }
    
    /**
     * @param scName the name of a function.
     * @param module the LECCModule instance corresponding to either the module defining the entity, or one of its dependent modules.
     *               This is used for obtaining the appropriate {@link LECCModule.ClassNameMapper} for use in mapping names.
     * @return the unqualified inner class name to use to represent a strict app node for that function.
     */
    static String createStrictInnerClassNameFromSC (QualifiedName scName, LECCModule module) {
        return createInnerClassNameFromSC(scName, module, true);
    }
    
    /**
     * @param scName the name of a function.
     * @param module the LECCModule instance corresponding to either the module defining the entity, or one of its dependent modules.
     *               This is used for obtaining the appropriate {@link LECCModule.ClassNameMapper} for use in mapping names.
     * @param strict true if the class name is for a strict application node              
     * @return the unqualified inner class name to use to represent a strict app node for that function.
     */
    static String createInnerClassNameFromSC (QualifiedName scName, LECCModule module, boolean strict) {
        // First get the name of the outer class.  createClassNameFromSC handles mapping scName to the
        // appropriate function group.
        String outerClassName = createClassNameFromSC(scName, module);
        String innerClassName = outerClassName + (strict ? "$RTAppS" : "$RTAppL");
        
        // If this is a primitive function we can stop there.  Otherwise we append the name
        // of the function which the application node is specific to.  This is to handle 
        // function groups which can contain multiple inner application node classes.
        // Since primitive functions are never grouped there is no need to disambiguate. 
        boolean needToDisambiguate = primitiveFunctionsMap.get(scName) == null;
        
        if (needToDisambiguate) {
            // If this is not a primitive we can check to see if the function is alone in
            // its function group.  If there is only one function in the function group we
            // don't need to disambiguate.
            ModuleName containingModuleName = scName.getModuleName();
            LECCModule containingModule = (LECCModule)module.findModule(containingModuleName);
            if (containingModule != null) {
                FunctionGroupInfo fgi = containingModule.getFunctionGroupInfo(scName);
                if(fgi.getNFunctions() == 1) {
                    needToDisambiguate = false;
                }
            }
        }
        
        if (needToDisambiguate) {
            
            String suffix = fixupUnqualifiedName(scName.getModuleName(), scName.getUnqualifiedName(), module);
            char[] ln = suffix.toCharArray();
            ln[0] = Character.toUpperCase(ln[0]);
            
            innerClassName = innerClassName + "_" + new String(ln);
        }
        
        // Now we need to check for excessive length.
        return doClassNameTruncation(innerClassName, scName.getModuleName(), module, true);

    }

    /**
     * Java versions 1.4 and previous for Windows do not handle long file paths (i.e. greater than 256 characters).
     * This is a problem when accessing files using the standard Java APIs.  Also it is a problem when using 
     * the Java compiler (jcc) to compiler automatically generated Java source.
     * In an attemp to ameliorate this problem we truncate excessively long class names. 
     * @param className - class name to be truncated.
     * @param moduleName - name of the containing module.
     * @param module - the containing module or a module dependent on the containing module.
     * @param innerClass - true if the class name is for an inner class.
     * @return the truncated class name.
     */
    private static String doClassNameTruncation (final String className, ModuleName moduleName, LECCModule module, boolean innerClass) {
        if (moduleName == null){
            // if the symbol is not in the map then the caller must have passed in
            // the module name. Only callers that are making the object corresponding
            // to the symbol should pass in the module name.
            throw new IllegalArgumentException();            
        }
        
        // Find the module that is named by moduleName.
        //
        // Note that by the precondition on the module parameter (namely that the specified module
        // must either be the one named by moduleName, or one of its dependent modules), 
        // this call to findModule should always succeed.
        module = (LECCModule)module.findModule(moduleName);
        
        LECCModule.ClassNameMapper classNameMapper = module.getClassNameMapper();
        
        // We synchronize on the classNameMapper because we want to query it and potentially add a mapping to it
        // all atomically.
        synchronized (classNameMapper) {
            String fixedName = classNameMapper.getFixedName(className);
            if (fixedName != null) {
                return fixedName;
            }
            
            fixedName = className;
            
            if (CALToJavaNames.TRUNCATE_LONG_CLASS_NAMES) {
                // If the fixed up name is too long we want to truncate it and make it unique.
                if (fixedName.length() > (innerClass ? INNER_CLASS_NAME_LENGTH_LIMIT : OUTER_CLASS_NAME_LENGTH_LIMIT)) {
                    fixedName = fixedName.substring(0, INNER_CLASS_NAME_LENGTH_LIMIT) + getNextTruncationDisambiguator() + "_";
                    
                }
            }        

            classNameMapper.addMapping(className, fixedName);
            
            return fixedName;
        }
        
    }
    
    
    /**
     * @param scName the name of a function.
     * @param module the LECCModule instance corresponding to either the module defining the entity, or one of its dependent modules.
     *               This is used for obtaining the appropriate {@link LECCModule.ClassNameMapper} for use in mapping names.
     * @return the type name to use to represent a lazy app node for that function.
     */
    static JavaTypeName createLazyInnerTypeNameFromSC (QualifiedName scName, LECCModule module) {
        String fullOuter = createFullClassNameFromSC(scName, module);
        String innerClassName = createLazyInnerClassNameFromSC(scName, module);
        String fullInnerClassName = fullOuter.substring(0, fullOuter.lastIndexOf('.') + 1) + innerClassName;  
        return JavaTypeName.make(fullInnerClassName, false);

    }
    
    /**
     * @param scName the name of a function.
     * @param module the LECCModule instance corresponding to either the module defining the entity, or one of its dependent modules.
     *               This is used for obtaining the appropriate {@link LECCModule.ClassNameMapper} for use in mapping names.
     * @return the type name to use to represent a strict app node for that function.
     */
    static JavaTypeName createStrictInnerTypeNameFromSC (QualifiedName scName, LECCModule module) {
        String fullOuter = createFullClassNameFromSC(scName, module);
        String innerClassName = createStrictInnerClassNameFromSC(scName, module);
        String fullInnerClassName = fullOuter.substring(0, fullOuter.lastIndexOf('.') + 1) + innerClassName;  
        return JavaTypeName.make(fullInnerClassName, false);

    }
    
    /**
     * Create a fully qualified class name from the unqualified class name and containing package.
     * @param packageName
     * @param className
     * @return String 
     */
    private static String createFullClassName (String packageName, String className) {
        return compound(createFullPackageName(packageName), className);     
    }
    
    /**
     * If className represents a class for a lecc module, return the module name.  If not, return null.
     * 
     * @param qualifiedClassName the name of the class.
     * @return the last component in the package name, if the class represents a class in a lecc module.
     * If not, return null.
     */
    static ModuleName getModuleNameFromPackageName(String qualifiedClassName) {
        
        // Get the package of the request class.
        int lastPeriodIndex = qualifiedClassName.lastIndexOf('.');
        int secondLastPeriodIndex = qualifiedClassName.lastIndexOf('.', lastPeriodIndex - 1);  // lastIndexOf() handles second arg < 0
        
        // Check that there are at least two periods.
        if (secondLastPeriodIndex < 0) {
            return null;
        }
        
        // Check the package name, minus the last package segment.
        String subPackageName = qualifiedClassName.substring(0, secondLastPeriodIndex);
        if (!subPackageName.equals(LECCMachineConfiguration.ROOT_PACKAGE)) {
            return null;
        }
        
        // Analyze the last package segment.
        String lastPackageNameSegment = qualifiedClassName.substring(secondLastPeriodIndex + 1, lastPeriodIndex); 
        return ProgramResourceLocator.createModuleNameFromPackageNameSegment(lastPackageNameSegment);
    }
    
    /**
     * Return the name of the class, in a form that can be used in source code.
     * eg. [[B ==> byte[][].
     *     CALExecutor$ForeignFunctionException ==> CALExecutor.ForiegnFunctionException.
     * @param name
     * @return String
     */
    static String fixupClassName (String name) {
        
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
    
    /**
     * Some CAL identifier names are not valid java identifiers and need to be adjusted.
     * 
     * Note this produces a name that is safe to save as a Windows filename. In other words, case mangling occurs so
     * that upper case letters are replaced by an underscore followed by an upper case letter.
     * 
     * @param moduleName The name of the module that the name symbol is defined in.
     * @param name the unqualified sc or type name.
     * @param module the LECCModule instance corresponding to either the module defining the entity, or one of its dependent modules.
     *               This is used for obtaining the appropriate {@link LECCModule.ClassNameMapper} for use in mapping names.
     * @return String
     */
    /* @implementation if you change this method, there's a good chance you need to change fixupInnerClassName as well. */
    private static String fixupUnqualifiedName (ModuleName moduleName, String name, LECCModule module) {
        if (name.equals ("$if") || name.equals ("if")) {
            return "if";
        }
        
        if (moduleName == null){
            // if the symbol is not in the map then the caller must have passed in
            // the module name. Only callers that are making the object corresponding
            // to the symbol should pass in the module name.
            throw new IllegalArgumentException();            
        }
        
        // Find the module that is named by moduleName.
        //
        // Note that by the precondition on the module parameter (namely that the specified module
        // must either be the one named by moduleName, or one of its dependent modules), 
        // this call to findModule should always succeed.
        module = (LECCModule)module.findModule(moduleName);
        
        LECCModule.ClassNameMapper classNameMapper = module.getClassNameMapper();
        
        final String originalName = name;
        
        // We synchronize on the classNameMapper because we want to query it and potentially add a mapping to it
        // all atomically.
        synchronized (classNameMapper) {
            String fixedName = classNameMapper.getFixedName(originalName);
            if (fixedName != null) {
                return fixedName;
            }
            
            name = smartNameShorten(name);

            // Remove invalid java characters (ex. '.' or '$') etc.
            fixedName = cleanSCName(name);
            
            if (CALToJavaNames.TRUNCATE_LONG_CLASS_NAMES) {
                // If the fixed up name is too long we want to truncate it and make it unique.
                if (fixedName.length() > OUTER_CLASS_NAME_LENGTH_LIMIT) {
                    fixedName = fixedName.substring(0, OUTER_CLASS_NAME_LENGTH_LIMIT) + getNextTruncationDisambiguator() + "_";
                }
            }        
            
            classNameMapper.addMapping(originalName, fixedName);
            return fixedName;
        }
    }

    /**
     * This function attempts to do intelligent shortening of CAL
     * names before they are converted to Java names.
     * 
     * @param name
     * @return the shortened name.
     */
    private static String smartNameShorten(String name) {
        // Currently we only handle the case of shortening dictionary function
        // names.  The intention is to add handling for other recognizable
        // situations where a name can be shortened in an intelligent fashion.
        if (name.startsWith("$dict")) {
            name = shortenDictName(name);
        } 
        
        return name;
    }
    
    /**
     * Shorten the name of a dictionary function.
     * @param name - the original name of the dictionary function.
     * @return - the shortened name.
     */
    private static String shortenDictName (String name) {
        // dictionary functions have names with the pattern:
        // $dictClassModuleName.ClassName#DataTypeModuleName.DataTypeName
        // For example for the class Enum and the data type Byte, both in
        // the module CAL.Core.Prelude the name would be:
        // $dictCal.Core.Prelude.Enum#Cal.Core.Prelude.Byte
        //
        // This generated class will be in the package corresponding to 
        // the data type module.  Since there can only be one data type
        // with a given name in a module it is safe to shorten the 
        // name be removing the module names.
        // i.e. the above name would become: $dict.Enum#Byte
        int hashIndex = name.indexOf('#');
        if (hashIndex > 0) {
            String className = name.substring(5, hashIndex);
            className = className.substring(className.lastIndexOf('.'));
            
            String instanceName = name.substring(name.lastIndexOf('.'));
            name = "$dict" + className + instanceName;
        }

        return name;
    }
    
 
    /**
     * A synchronized static method for obtaining the next disambiguator to use for a truncated class name.
     * @return the next disambiguator.
     */
    private static int getNextTruncationDisambiguator() {        
        return nTruncatedNames.incrementAndGet();
    }
    
    /**
     * Clean up a supercombinator name so that it is
     * a valid java name.
     * @param name
     * @return the cleaned name.
     */
    static String cleanSCName(String name) {
        if (isReservedWord(name)) {
            //there are no reserved words that start with an underscore.
            name = name + "_";
        }
        
        //Substitute _ for # 
        //Substitute _ for .
        //Substitute _ for $
        
        StringBuilder sb = new StringBuilder();
        char c = name.charAt(0);
        //for the first character, don't worry about case or the underscore since the CAL language guarantees that functions start
        //with a lower case letter.
        switch (c) {
            case '#':
            case '$':
            case '.':
                sb.append('_');
                break;
                
            default:
                sb.append(c);
            break;               
        }
        
        for (int i = 1, n = name.length(); i < n; ++i) {
            
            c = name.charAt(i);
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
                    }
                    sb.append(c);
                    break;
                }
            }        
        }
        
        return sb.toString();
    }
    
    /**
     * @param name a simple name
     * @return whether the given name is a reserved word.
     * ie. either a Java language keyword, or a name of a file which cannot be created in the Windows file system.
     */
    static boolean isReservedWord (String name) {
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
     * When deconstructing a data type the names assigned to the members
     * can have conflicts with java reserved words.  We fix this by
     * prepending a '$'.  The '$' is used to avoid creating a new conflict
     * with another CAL variable.
     * @param varName
     * @return String
     */
    static String fixupVarName(String varName) {
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
    
    /**     
     * @param packageName
     * @param className
     * @return packageName + "." + className.
     */
    private static String compound(String packageName, String className) {
        return new StringBuilder(packageName).append('.').append(className).toString();
    }
    
    /**
     * Build the java name for a let variable definition function.
     * @param qualifiedDefFunctionName
     * @param module
     * @return the Java name for the let variable definition function.
     */
    static String makeLetVarDefFunctionJavaName (QualifiedName qualifiedDefFunctionName, LECCModule module) {
        String defFunctionName = qualifiedDefFunctionName.getUnqualifiedName();
        FunctionGroupInfo fgi = module.getFunctionGroupInfo(qualifiedDefFunctionName);
        
        if (fgi != null && fgi.getNFunctions() == 1) {
            int firstDollarIndex = defFunctionName.indexOf('$');
            if (firstDollarIndex > 0) {
                int secondDollarIndex = defFunctionName.indexOf('$', firstDollarIndex + 1);
                if (secondDollarIndex > firstDollarIndex) {
                    defFunctionName = defFunctionName.substring(firstDollarIndex + 1);
                }
            }
        }        
        
        return CALToJavaNames.fixupVarName(defFunctionName);
    }
}
