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
 * ForeignImportGenerator.java
 * Creation date: Sep 14, 2005.
 * By: Edward Lam
 */
package org.openquark.gems.client.jfit;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.openquark.cal.compiler.ForeignTypeInfo;
import org.openquark.cal.compiler.LanguageInfo;
import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.ModuleTypeInfo;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.Scope;
import org.openquark.cal.compiler.SourceModel;
import org.openquark.cal.compiler.TypeConstructor;
import org.openquark.cal.compiler.UnableToResolveForeignEntityException;
import org.openquark.cal.compiler.SourceModel.CALDoc;
import org.openquark.cal.compiler.SourceModel.ModuleDefn;
import org.openquark.cal.compiler.SourceModel.TopLevelSourceElement;
import org.openquark.cal.compiler.SourceModel.TypeExprDefn;
import org.openquark.cal.compiler.SourceModel.TypeSignature;
import org.openquark.cal.compiler.SourceModel.FunctionDefn.Foreign;
import org.openquark.cal.compiler.SourceModel.Name.TypeClass;
import org.openquark.cal.compiler.SourceModel.TypeConstructorDefn.ForeignType;
import org.openquark.cal.compiler.SourceModelUtilities.ImportAugmenter;
import org.openquark.cal.module.Cal.Core.CAL_Debug;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.services.CALWorkspace;
import org.openquark.cal.services.IdentifierUtils;
import org.openquark.cal.services.MetaModule;
import org.openquark.util.Pair;



/**
 * This class generates CAL modules containing foreign functions and data types, when presented with desired Java classes and Java class members.
 * 
 * @author Edward Lam
 */
public final class ForeignImportGenerator {

    /** A DateFormat for emitting time in the current locale. */
    private static final DateFormat LOCALE_DATE_FORMAT = DateFormat.getDateInstance(DateFormat.MEDIUM);
    
    /** The type classes in the auto-generated using clause for the Prelude module. */
    private static final String[] preludeUsingTypeClasses = {
        CAL_Prelude.TypeClasses.Inputable.getUnqualifiedName(),
        CAL_Prelude.TypeClasses.Outputable.getUnqualifiedName(),
        CAL_Prelude.TypeClasses.Eq.getUnqualifiedName(),
        CAL_Prelude.TypeClasses.Ord.getUnqualifiedName()
    };
    
    /** The type classes in the auto-generated using clause for the Debug module. */
    private static final String[] debugUsingTypeClasses = {
        CAL_Debug.TypeClasses.Show.getUnqualifiedName()
    };
    
  
    /**
     * Generation scope enum pattern.
     * @author Edward Lam
     */
    public static final class GenerationScope {
        private String typeString;

        private GenerationScope(String s) {
            typeString = s;
        }
        @Override
        public String toString() {
            return typeString;
        }

        /** All public. */
        public static final GenerationScope ALL_PUBLIC = new GenerationScope("ALL_PUBLIC");
        
        /** Functions and types public.  Type implementations private. */
        public static final GenerationScope PARTIAL_PUBLIC = new GenerationScope("PARTIAL_PUBLIC");
        
        /** All private. */
        public static final GenerationScope ALL_PRIVATE = new GenerationScope("ALL_PRIVATE");
    }

    /**
     * A class to hold info about the source as it is being generated..
     * @author Edward Lam
     */
    private static final class GenerationInfo {
        
        /** The default name for generated types. */
        private static final String DEFAULT_BASE_NAME = "ForeignType";

        /** The name of the module for which the imports are being generated. */
        private final ModuleName currentModuleName;

        /** Map from Class to the type constructor name given to that class, 
         *  for types previously existing in the workspace. */
        private final Map<Class<?>, QualifiedName> existingClassToTypeConsMap;

        /** Map from Class to the type constructor name given to that class. */
        private final Map<Class<?>, QualifiedName> classToTypeConsNameMap;
        
        /** The set of unqualified names that have been used for functions in the module being generated. */
        private final Set<String> existingUnqualifiedFunctionNamesSet;
        
        /** The set of unqualified names that have been used for types in the module being generated. */
        private final Set<String> existingUnqualifiedTypeConsNamesSet;
        
        /** Map from (java member, java class pair) to the unqualified function name assigned to that member, 
         *  for members for which names have been generated. */
        private final Map<Pair<Member, Class<?>>, String> memberToUnqualifiedFunctionNameMap;
        
        /** the unqualified names of the types in the generated module. */
        private final Set<String> preludeUsingTypes = new HashSet<String>();

        /** The scope for generated functions and types. */
        private final GenerationScope generationScope;

        GenerationInfo(ModuleName currentModuleName, CALWorkspace workspace, GenerationScope generationScope) throws UnableToResolveForeignEntityException {
            this.currentModuleName = currentModuleName;
            this.generationScope = generationScope;
            this.existingClassToTypeConsMap = getExistingTypeMap(workspace);
            this.classToTypeConsNameMap = new HashMap<Class<?>, QualifiedName>(existingClassToTypeConsMap);
            this.existingUnqualifiedFunctionNamesSet = new HashSet<String>();
            this.existingUnqualifiedTypeConsNamesSet = new HashSet<String>();
            this.memberToUnqualifiedFunctionNameMap = new HashMap<Pair<Member, Class<?>>, String>();
        }
        
        /**
         * Mark a name as being the name of a type in the generated module.
         * @param unqualifiedTypeName the name of the type.
         * @return false if the name is already assigned to a type, true otherwise.
         */
        boolean addTypeToPreludeUsingSet(String unqualifiedTypeName) {
            // If a type with the same unqualified name also exists in the current module, we must
            //   qualify for the type in the Prelude.
            if (existingUnqualifiedTypeConsNamesSet.contains(unqualifiedTypeName)) {
                return false;
            }
            
            preludeUsingTypes.add(unqualifiedTypeName);
            return true;
        }
        
        /**
         * @return the unqualified names of the types in the generated module.
         */
        String[] getPreludeUsingTypes() {
            return preludeUsingTypes.toArray(new String[preludeUsingTypes.size()]);
        }
        
        /**
         * @return the name of the module being generated.
         */
        public ModuleName getCurrentModuleName() {
            return currentModuleName;
        }
        
        /** 
         * @return the scope for generated functions and types. 
         */
        public GenerationScope getGenerationScope() {
            return generationScope;
        }
        
        /**
         * @param javaClass a java class.
         * @return the name of the generated type constructor mapped to the java class, or null if this has not happened.
         */
        QualifiedName getForeignTypeName(Class<?> javaClass) {
            return classToTypeConsNameMap.get(javaClass);
        }
        
        /**
         * Create a new valid function name
         * @param javaMember the Java member for which to generate the function.
         * @param javaClass the class containing the member.
         * @return a valid function name based on the provided name.  This will be added to the set of existing function names.
         */
        String getNewFunctionName(Member javaMember, Class<?> javaClass) {
            generateFunctionNames(new Member[]{javaMember}, javaClass);
            return memberToUnqualifiedFunctionNameMap.get(new Pair<Member, Class<?>>(javaMember, javaClass));
        }
        
        /**
         * Populates the internal map from java member to unqualified name of the foreign function referencing that member.
         * The generated names will not collide with any previously-generated names.
         * @param members the Java members for which to generate the function.
         * @param javaClass the class containing the member.
         */
        public void generateFunctionNames(Member[] members, Class<?> javaClass) {
            // (String->Set of Member) Map from base name to the members with that name.  Sorted by base name.
            //  Only contains mappings for members for which names have not already been generated.
            Map<String, Set<Member>> baseNameToMembersMap = new TreeMap<String, Set<Member>>();
            
            // Map member to base name, for any members for which names have not already been generated.
            for (final Member javaMember : members) {
                Class<?> memberClass = javaMember instanceof Constructor<?> ? javaMember.getDeclaringClass() : javaClass;
                
                // Check that a name hasn't already been generated for this member.
                if (memberToUnqualifiedFunctionNameMap.containsKey(new Pair<Member, Class<?>>(javaMember, javaClass))) {
                    continue;
                }
                
                // Get the type constructor name for the class.
                String classTypeConsName = getTypeConsName(memberClass);
                
                // Get the name to convert to a suggested name.
                // Constructor.getName() is fully qualified, but Method.getName() and Field.getName() are not.
                String nameToConvert = 
                    javaMember instanceof Constructor<?> ? classTypeConsName + "_new" :
                        javaMember instanceof Field ? /*"get." + */ classTypeConsName + "_" + javaMember.getName():
                            classTypeConsName + "_" + javaMember.getName();         // Method
                
                // If the class is generated, consume the first letter, which is the prepended "J".
                if (!existingClassToTypeConsMap.containsKey(memberClass)) {
                    nameToConvert = nameToConvert.substring(1);
                }
                        
                String baseFunctionName = IdentifierUtils.makeIdentifierName(nameToConvert, false);
                
                // Check for no valid CAL characters.  This should be rare.
                if (baseFunctionName.length() == 0) {
                    baseFunctionName = "generatedFunction";
                }
                
                // Add to the members map.
                Set<Member> baseNameMembers = baseNameToMembersMap.get(baseFunctionName);
                if (baseNameMembers == null) {
                    baseNameMembers = new HashSet<Member>();
                    baseNameToMembersMap.put(baseFunctionName, baseNameMembers);
                }
                baseNameMembers.add(javaMember);
            }

            // Iterate over the previously-constructed map.
            for (final Map.Entry<String, Set<Member>> mapEntry : baseNameToMembersMap.entrySet()) {
                String baseName = mapEntry.getKey();
                Set<Member> baseNameMembers = mapEntry.getValue();
                
                int nMembers = baseNameMembers.size();
                
                // Check for the case of only one member with the given base name.
                if (nMembers == 1) {
                    // Disambiguate the name.
                    String functionName = baseName;
                    
                    int index = 2;
                    while (!existingUnqualifiedFunctionNamesSet.add(functionName)) {
                        functionName = baseName + "_" + index;
                        index++;
                    }

                    memberToUnqualifiedFunctionNameMap.put(new Pair<Member, Class<?>>(baseNameMembers.iterator().next(), javaClass), functionName);
                    continue;
                }
                
                // If here, more than one member with the same base name.
                
                // Disambiguate with respect to parameters if possible.
                for (final Member member : baseNameMembers) {
                    StringBuilder newBaseNameSB = new StringBuilder(baseName);
                    
                    Class<?>[] paramTypes;
                    if (member instanceof Field) {
                        // Can't do much about this..
                        paramTypes = new Class<?>[0];
                        
                    } else if (member instanceof Constructor<?>) {
                        paramTypes = ((Constructor<?>)member).getParameterTypes();
                    
                    } else if (member instanceof Method) {
                        paramTypes = ((Method)member).getParameterTypes();
                    
                    } else {
                        throw new IllegalArgumentException("Unknown member type: " + member.getClass());
                    }
                    
                    // Append (underscore + param type) for each param.
                    for (final Class<?> paramType : paramTypes) {
                        newBaseNameSB.append("_" + getTypeConsName(paramType));
                    }

                    String newBaseName = newBaseNameSB.toString();
                    
                    // Disambiguate the name.
                    String functionName = newBaseName;
                    
                    int index = 2;
                    while (!existingUnqualifiedFunctionNamesSet.add(functionName)) {
                        functionName = newBaseName + "_" + index;
                        index++;
                    }

                    memberToUnqualifiedFunctionNameMap.put(new Pair<Member, Class<?>>(member, javaClass), functionName);
                }
            }
        }
        
        /**
         * Get a map from class to type constructor name, for classes for which foreign types are known to exist.
         * @param workspace the workspace 
         * @return (Class->QualifiedName) map from class to type cons name.
         */
        private static Map<Class<?>, QualifiedName> getExistingTypeMap(CALWorkspace workspace) throws UnableToResolveForeignEntityException {
            // (Class->QualifiedName) the return value.
            Map<Class<?>, QualifiedName> classToTypeConsNameMap = new HashMap<Class<?>, QualifiedName>();
            
            // Iterate over the modules.
            for (int i = 0, nMetaModules = workspace.getNMetaModules(); i < nMetaModules; i++) {
                MetaModule metaModule = workspace.getNthMetaModule(i);
                
                // We're only interested in modules which are compiled.
                ModuleTypeInfo moduleTypeInfo = metaModule.getTypeInfo();
                if (moduleTypeInfo == null) {
                    // Not compiled.
                    continue;
                }
                
                int nImports = moduleTypeInfo.getNImportedModules();
                
                // Iterate over the type constructors in the module.
                int nTypeConstructors = moduleTypeInfo.getNTypeConstructors();
                for (int j = 0; j < nTypeConstructors; j++) {
                    TypeConstructor typeCons = moduleTypeInfo.getNthTypeConstructor(j);
                    
                    QualifiedName calName = typeCons.getName();
                     
                    //Prelude.Unit and Prelude.Boolean are special cases in that they are algebraic types that however correspond
                    //to foreign types for the purposes of declaring foreign functions.
                    
                    if (calName.equals(CAL_Prelude.TypeConstructors.Unit)) {
                        classToTypeConsNameMap.put(void.class, calName);
                        continue;
                    }
                    if (calName.equals(CAL_Prelude.TypeConstructors.Boolean)) {
                        classToTypeConsNameMap.put(boolean.class, calName);
                        continue;
                    }
                    
                    // Check if the type is foreign.
                    ForeignTypeInfo fti = typeCons.getForeignTypeInfo();
                    
                    if (fti != null && fti.getImplementationVisibility() == Scope.PUBLIC) {
                        Class<?> foreignType = fti.getForeignType();
                        
                        // In cases where a single class maps to multiple type constructors, 
                        //   we choose the type in the module which is "closest" to the Prelude.
                        // For now, this distance is approximated by the number of imports in the module.
                        // TODOEL: Later, we can figure out the size of the import graph.
                        QualifiedName existingMappedName = classToTypeConsNameMap.get(foreignType);
                        if (existingMappedName == null) {
                            // Add a new mapping.
                            classToTypeConsNameMap.put(foreignType, calName);
                            continue;
                        }
                        // a mapping already exists.
                        ModuleTypeInfo existingMappingModuleTypeInfo = workspace.getMetaModule(existingMappedName.getModuleName()).getTypeInfo();
                        if (existingMappingModuleTypeInfo == null) {
                            // Existing mapping's type info not compiled.
                            continue;
                        }
                        int existingMappingNImports = existingMappingModuleTypeInfo.getNImportedModules();
                        
                        if (nImports < existingMappingNImports) {
                            // override the existing mapping.
                            classToTypeConsNameMap.put(foreignType, calName);
                            continue;
                        }
                    }
                }
            }
            
            return classToTypeConsNameMap;
        }
        
        /**
         * Get a "reasonable" type constructor name for a foreign class.
         * If the class was previously mapped using calculateRequiredClassNames(), the previously-calculated name will be returned.
         * Otherwise a new one will be calculated and mapped.
         * 
         * @param foreignClass the class for which a type constructor name will be generated.
         * This class' mapping will be augmented with a mapping for the generated name.
         * @return the new generated name.
         */
        String getTypeConsName(Class<?> foreignClass) {

            {
                QualifiedName previouslyCalculatedName = classToTypeConsNameMap.get(foreignClass);
                if (previouslyCalculatedName != null) {
                    return previouslyCalculatedName.getUnqualifiedName();
                }
            }
            
            String baseName = makeTypeConsName(foreignClass, -1);
            
            // Disambiguate.
            int index = 2;
            String typeConsName = baseName;
            while (!existingUnqualifiedTypeConsNamesSet.add(typeConsName)) {
                typeConsName = baseName + index;
                index++;
            }
            
            // Add a mapping for the generated name.
            QualifiedName qualifiedTypeConsName = QualifiedName.make(currentModuleName, typeConsName);
            classToTypeConsNameMap.put(foreignClass, qualifiedTypeConsName);
            
            return typeConsName;
        }
        
        /**
         * Generate a type constructor name for the provided foreign class, with characters from the suggested number 
         * of qualifications from the class' fully-qualified name.
         *  
         * Examples: 
         *  ("java.lang.String", -1) -> JavaLangString
         *  ("java.lang.String", 0)  -> String
         *  ("java.lang.String", 1)  -> LangString
         *  ("java.lang.String", 2)  -> JavaLangString
         *  ("java.lang.String", 3)  -> (null)
         *  
         *  ("[Ljava.lang.String;", -1) -> JavaLangString_Array
         * 
         * Note: for Java class names with no valid cal characters, 
         * "ForeignType" is returned if numQualifications < 0, otherwise null is returned.
         * 
         * Examples:
         *  ("$", -1) -> ForeignType
         *  ("$", 0)  -> (null)
         *  
         *  
         * @param foreignClass the class for which the name should be generated.
         * @param numQualifications the number of qualifications.  0 for base name only, -1 for fully qualified..
         * @return the suggested name, or null if the class does not have the suggested number of qualifications.
         */
        private static String makeTypeConsName(Class<?> foreignClass, int numQualifications) {

            // If we have an array, reduce to the innermost component type and calculate the dimension
            int arrayDim = 0;
            
            Class<?> componentClass = foreignClass;
            while (componentClass.isArray()) {
                componentClass = componentClass.getComponentType();
                arrayDim++;
            }

            // Get the full name, use this to calculate the class name (without the package).
            String componentClassFullName = componentClass.getName();

            String componentClassName;
            if (numQualifications < 0) {
                // Use the fully qualified name.
                componentClassName = componentClassFullName;
                
            } else {
                // Split at the dots.
                String[] componentClassComponents = componentClassFullName.split("[.]");
                
                // Check if there are too many qualifications.
                if (numQualifications > componentClassComponents.length - 1) {
                    return null;
                }

                // Start off with the last component, which is the base name.
                componentClassName = componentClassComponents[componentClassComponents.length - 1];
                
                // For each qualification, prepend the (i + 1)th last component plus a dot.
                for (int i = 0; i < numQualifications; i++) {
                    componentClassName = componentClassComponents[componentClassComponents.length - 1 - i] + "." + componentClassName;
                }
                
            }
            
            char[] classNameChars = componentClassName.toCharArray();
            
            int firstOkCharIndex = 0;
            
            // To make sure the first char is upper case, iterate over the array,
            // finding the first letter which is ok when upper cased.  Set that char to upper case
            while (firstOkCharIndex < classNameChars.length) {
                char c = Character.toUpperCase(classNameChars[0]);
                if (LanguageInfo.isCALConsPart(c)) {
                    classNameChars[firstOkCharIndex] = c;
                    break;
                }
                firstOkCharIndex++;
            }
            
            // Iterate over the remaining array, replacing invalid chars with '_'
            for (int i = firstOkCharIndex + 1; i < classNameChars.length; i++) {
                char c = classNameChars[i];
                if (!LanguageInfo.isCALConsPart(c)) {
                    classNameChars[i] = '_';
                }
            }

            // Create the base name.  If there are no valid chars (should be rare.., eg. class name is "$"), just use a default.
            // Note: Prepend a "J" to follow convention for foreign types.
            String baseName = firstOkCharIndex < classNameChars.length ?
                    "J" + new String(classNameChars, firstOkCharIndex, classNameChars.length - firstOkCharIndex) :
                        DEFAULT_BASE_NAME;
            
            // Tweak name for arrays.
            if (arrayDim > 0) {
                baseName += "Array";
                
                if (arrayDim > 1) {
                    baseName += arrayDim;
                }
            }

            return baseName;
        }

        /**
         * Populates the internal map from required class to type name.
         * @param requiredClasses (Set of java.lang.Class) the class objects representing the required classes.
         */
        public void calculateRequiredClassNames(Set<Class<?>> requiredClasses) {
            
            // (String->Set of Class) map from unqualified java name to the classes encountered with that base name.
            Map<String, Set<Class<?>>> baseNameToClassesMap = new HashMap<String, Set<Class<?>>>();
            
            // Steps:
            // Find base names, classes with conflicting base name.
            // Disambiguate classes with conflicting base names.
            
            // Iterate over required classes, calculating which of these would have a suggested base name conflict with
            //  other required classes.
            for (final Class<?> javaClass : requiredClasses) {
                // Skip generating a type for this class if a mapping already exists.
                if (classToTypeConsNameMap.containsKey(javaClass)) {
                    continue;
                }
                
                // Note: this can return null.
                //  It's ok to have a null key though.  We just have to handle this later on.
                String baseTypeConsName = makeTypeConsName(javaClass, 0);
                
                Set<Class<?>> baseNameClasses = baseNameToClassesMap.get(baseTypeConsName);
                if (baseNameClasses == null) {
                    baseNameClasses = new HashSet<Class<?>>();
                    baseNameToClassesMap.put(baseTypeConsName, baseNameClasses);
                }
                
                baseNameClasses.add(javaClass);
            }
            
            // Iterate over the sets of classes with the given base names.
            for (final Map.Entry<String, Set<Class<?>>> mapEntry : baseNameToClassesMap.entrySet()) {
                String baseName = mapEntry.getKey();            // can be null.
                Set<Class<?>> classSet = mapEntry.getValue();
                
                // Default if no valid cal chars..
                if (baseName == null) {
                    baseName = DEFAULT_BASE_NAME;
                }
                
                if (classSet.size() == 1) {
                    Class<?> conflictingClass = classSet.iterator().next();
                    
                    // Disambiguate.
                    int index = 2;
                    String typeConsName = baseName;
                    while (!existingUnqualifiedTypeConsNamesSet.add(typeConsName)) {
                        typeConsName = baseName + index;
                        index++;
                    }
                    
                    // Only one class with this base name.
                    QualifiedName qualifiedTypeConsName = QualifiedName.make(currentModuleName, baseName);
                    classToTypeConsNameMap.put(conflictingClass, qualifiedTypeConsName);
                    
                } else {
                    // More than one class with the same base name.
                    // Iterate over the classes in the set, figuring out how many qualifications are necessary to disambiguate.
                    // Names are qualified in reverse order.
                    // eg. for org.openquark.gems.client.jfit.ForeignImportGenerator, 
                    //   base name       : ForeignImportGenerator
                    //   1 qualification : jfit.ForeignImportGenerator
                    //   2 qualifications: client.jfit.ForeignImportGenerator
                    //   etc.
                    
                    int numQualifications = 1;
                    while (numQualifications > 0) {
                        // (Set of String) names of classes, with the given number of qualifications.
                        Set<String> namesWithQualification = new HashSet<String>();
                        
                        boolean foundConflict = false;
                        
                        for (final Class<?> conflictingClass : classSet) {
                            String nameWithQualification = makeTypeConsName(conflictingClass, numQualifications);
                            
                            if (nameWithQualification == null) {
                                // Not enough qualifications exist in the name.
                                // foundConflict is false, so we will break out of the while.. loop.
                                numQualifications = -1;
                                break;
                            }
                            
                            if (!namesWithQualification.add(nameWithQualification)) {
                                // This number of qualifications is not enough to disambiguate with respect to the other classes.
                                foundConflict = true;
                                break;
                            } else {
                                // Didn't find a conflict.  Try the next name.
                            }
                        }
                        if (foundConflict) {
                            // Try more qualifications.
                            numQualifications++;
                        } else {
                            // Found a sufficient number of qualifications.
                            break;
                        }
                    }

                    // Now populate the classToTypeConsNameMap
                    for (final Class<?> conflictingClass : classSet) {
                        String nameWithQualification = makeTypeConsName(conflictingClass, numQualifications);
                        
                        // Disambiguate.
                        int index = 2;
                        String typeConsName = nameWithQualification;
                        while (!existingUnqualifiedTypeConsNamesSet.add(typeConsName)) {
                            typeConsName = baseName + index;
                            index++;
                        }
                        
                        // Add a mapping for the generated name.
                        QualifiedName qualifiedTypeConsName = QualifiedName.make(currentModuleName, typeConsName);
                        classToTypeConsNameMap.put(conflictingClass, qualifiedTypeConsName);
                    }
                }
            }
        }
    }
    
    /**
     * A comparator which compares classes by generated foreign type name.
     * 
     * @author Edward Lam
     */
    private static class ClassTypeNameComparator implements Comparator<Class<?>> {

        private final GenerationInfo generationInfo;

        /**
         * Constructor for a ClassTypeNameComparator.
         * @param generationInfo
         */
        ClassTypeNameComparator(GenerationInfo generationInfo) {
            this.generationInfo = generationInfo;
        }
        
        /**
         * {@inheritDoc}
         */
        public int compare(Class<?> o1, Class<?> o2) {
            QualifiedName foreignTypeName1 = generationInfo.getForeignTypeName(o1);
            QualifiedName foreignTypeName2 = generationInfo.getForeignTypeName(o2);
            
            if (foreignTypeName1 == null) {
                throw new IllegalStateException("No name generated for foreignType: " + o1);
            }
            
            if (foreignTypeName2 == null) {
                throw new IllegalStateException("No name generated for foreignType: " + o2);
            }

            return foreignTypeName1.compareTo(foreignTypeName2);
        }
    }
    
    /**
     * A comparator which compares implementors of java.lang.reflect.Member.
     * 
     * The primary sort is by member.getClass().  Constructors first, then fields then methods.
     * The secondary sort is by member name.
     *   Note: the name returned by Constructor.getName() is fully-qualified, while for methods and fields it is unqualified.
     * The tertiary sort is by member parameter type, for members which have param types.
     * 
     * @author Edward Lam
     */
    private static class MemberComparator implements Comparator<Member> {

        private final GenerationInfo generationInfo;

        /**
         * Constructor for a MemberComparator.
         * @param generationInfo
         */
        MemberComparator(GenerationInfo generationInfo) {
            this.generationInfo = generationInfo;
        }
        
        /**
         * {@inheritDoc}
         */
        public int compare(Member member1, Member member2) {
            
            if (member1 == member2) {
                return 0;
            }

            // Primary sort = member class.
            if (member1.getClass() != member2.getClass()) {
                if (member1 instanceof Constructor<?>) {
                    return -1;
                }
                if (member1 instanceof Field) {
                    // If member2 not a constructor, must be a method.
                    return member2 instanceof Constructor<?> ? 1 : -1;
                }
                
                // member1 must be a method.
                return 1;
            }
            
            // Secondary sort: member name.
            // Note: fully qualified for constructors, unqualified for methods and fields
            int compareResult = member1.getName().compareTo(member2.getName());
            if (compareResult == 0) {
                Class<?>[] parameterTypes1 = null;
                Class<?>[] parameterTypes2 = null;
                if (member1 instanceof Constructor<?>) {
                    parameterTypes1 = ((Constructor<?>)member1).getParameterTypes();
                    parameterTypes2 = ((Constructor<?>)member2).getParameterTypes();
                }
                
                if (member1 instanceof Method) {
                    parameterTypes1 = ((Method)member1).getParameterTypes();
                    parameterTypes2 = ((Method)member2).getParameterTypes();
                }
                
                if (parameterTypes1 == null || parameterTypes2 == null) {
                    // If here, logically member1 and member2 are both fields with the same name.
                    // This can happen if they are from different classes.
                    return 0;
                }
                
                // Tertiary sort: member param types.
                
                // The member with fewer params comes first.
                int nParams1 = parameterTypes1.length;
                int nParams2 = parameterTypes2.length;
                
                if (nParams1 < nParams2) {
                    return -1;
                
                } else if (nParams1 > nParams2) {
                    return 1;
                }
                
                // If here, the same number of params.
                // Sort according to the types of the params.
                for (int i = 0; i < nParams1; i++) {
                    Class<?> paramType1 = parameterTypes1[i];
                    Class<?> paramType2 = parameterTypes2[i];
                    
                    String typeConsName1 = generationInfo.getTypeConsName(paramType1);
                    String typeConsName2 = generationInfo.getTypeConsName(paramType2);
                    
                    compareResult = typeConsName1.compareTo(typeConsName2);
                    if (compareResult != 0) {
                        return compareResult;
                    }
                }
            }
            
            return compareResult;
        }
    }
    
    /**
     * Get the source model for a default module definition.
     * 
     * @param moduleName the name of the module.
     * @param classNames (Set of String) the names of the classes for which functions will be generated.
     * @param requiredClasses (Set of Class) the classes for which types will be generated.
     *   These should include any classes required by generated functions.
     * @param generationScope The scope for generated functions and types.
     * @param excludeMap (Class->Set of String) map from class to method names for methods to exclude.
     * @param workspace the cal workspace.
     * @return the source model for a default module definition using the above.
     */
    public static SourceModel.ModuleDefn makeDefaultModuleDefn(ModuleName moduleName, Set<String> classNames, Set<Class<?>> requiredClasses, 
            GenerationScope generationScope, Map<Class<?>, Set<String>> excludeMap, CALWorkspace workspace) throws UnableToResolveForeignEntityException {
        
        // Generate map (String->Class) from class name to class for required classes.
        Map<String, Class<?>> requiredClassNameToClassMap = new HashMap<String, Class<?>>();
        for (final Class<?> requiredClass : requiredClasses) {
            requiredClassNameToClassMap.put(requiredClass.getName(), requiredClass);
        }
        
        GenerationInfo generationInfo = new GenerationInfo(moduleName, workspace, generationScope);
        
        // (List of SourceModel.TopLevelSourceElement)
        List<TopLevelSourceElement> topLevelSourceElementList = new ArrayList<TopLevelSourceElement>();
        
        // Calculated the required class names.
        generationInfo.calculateRequiredClassNames(requiredClasses);
        
        // Create a sorted list of classes by name.
        List<Class<?>> requiredClassList = new ArrayList<Class<?>>(requiredClasses);
        Collections.sort(requiredClassList, new ClassTypeNameComparator(generationInfo));
        
        // Generate the foreign types corresponding to the required classes.
        for (final Class<?> javaClass : requiredClassList) {
            // Skip generating a type for this class if a mapping already exists.
            if (!generationInfo.getForeignTypeName(javaClass).getModuleName().equals(generationInfo.currentModuleName)) {
                continue;
            }
            
            // Generate the foreign type.
            ForeignType foreignType = generateForeignType(javaClass, generationInfo);
            topLevelSourceElementList.add(foreignType);
        }
        
        // Create a comparator to compare the class members.
        MemberComparator memberComparator = new MemberComparator(generationInfo);
        
        // For each of the required classes, generate fields, constructors, methods.
        for (final String className : classNames) {
            Class<?> javaClass = requiredClassNameToClassMap.get(className);

            if (javaClass == null) {
                throw new IllegalStateException("Error: can't find class: " + className);
            }
            
            Field[] fields = javaClass.getFields();                             // This can throw a NoClassDefFoundError
            Arrays.sort(fields, memberComparator);
            generationInfo.generateFunctionNames(fields, javaClass);
            for (final Field field : fields) {
                topLevelSourceElementList.add(generateForeignFunction(javaClass, field, generationInfo));
            }
            
            Constructor<?>[] constructors = javaClass.getConstructors();           // This can throw a NoClassDefFoundError
            Arrays.sort(constructors, memberComparator);
            generationInfo.generateFunctionNames(constructors, javaClass);
            for (final Constructor<?> constructor : constructors) {
                topLevelSourceElementList.add(generateForeignFunction(javaClass, constructor, generationInfo));
            }
            
            // Calculate the names of the methods to exclude for this class.
            Set<String> methodExcludeNames = new HashSet<String>();
            for (final Map.Entry<Class<?>, Set<String>> mapEntry : excludeMap.entrySet()) {
                Class<?> excludeMapClass = mapEntry.getKey();
                
                // Check if (javaClass instanceof excludeMapClass).
                if (excludeMapClass.isAssignableFrom(javaClass)) {
                    methodExcludeNames.addAll(mapEntry.getValue());
                }
            }

            Method[] methods = javaClass.getMethods();                          // This can throw a NoClassDefFoundError
            Arrays.sort(methods, memberComparator);
            generationInfo.generateFunctionNames(methods, javaClass);
            for (final Method method : methods) {
                String methodName = method.getName();
                
                // Check if this is a method we should exclude.
                if (methodExcludeNames.contains(methodName)) {
                    continue;
                }

                // Only generate clone() if Cloneable.
                if (method.getName().equals("clone") && method.getParameterTypes().length == 0) {
                    if (!Cloneable.class.isAssignableFrom(javaClass)) {
                        continue;
                    }
                }
                
                topLevelSourceElementList.add(generateForeignFunction(javaClass, method, generationInfo));
            }
        }
         
        // Create the array of top-level source elements.
        SourceModel.TopLevelSourceElement[] topLevelSourceElements = 
            topLevelSourceElementList.toArray(new SourceModel.TopLevelSourceElement[topLevelSourceElementList.size()]);
        
        // Generate a comment.
        SourceModel.CALDoc.Comment.Module moduleComment = getModuleCALDoc(moduleName);
        
        // Calculate the imports.
        SourceModel.Import[] imports = new SourceModel.Import[] {
                SourceModel.Import.make(CAL_Prelude.MODULE_NAME, null, null, generationInfo.getPreludeUsingTypes(), preludeUsingTypeClasses),
                SourceModel.Import.make(CAL_Debug.MODULE_NAME, null, null, null, debugUsingTypeClasses)
        };
        
        // Create the source model for the module.
        ModuleDefn moduleDefn = SourceModel.ModuleDefn.make(moduleComment, moduleName, imports, topLevelSourceElements);
        
        // Add imports
        moduleDefn = ImportAugmenter.augmentWithImports(moduleDefn);
        
        return moduleDefn;
    }

    /**
     * Generate a boilerplate module comment.
     * @param moduleName the name of the module
     */
    private static SourceModel.CALDoc.TextBlock getModuleCommentBoilerplate(ModuleName moduleName) {
        String commentText1 =
            "\n" +
            " " + moduleName + ".cal.\n" +
            " \n" +
            " ";
        
        SourceModel.CALDoc.TextSegment.InlineTag.Summary commentSegment2 =
            SourceModel.CALDoc.TextSegment.InlineTag.Summary.make(
                SourceModel.CALDoc.TextBlock.make(
                    new SourceModel.CALDoc.TextSegment.TopLevel[] {
                        SourceModel.CALDoc.TextSegment.Plain.make("This module was automatically generated by JFit.")
                    }));
        
        String commentText3 =
            "\n" +
            " \n" +
            " Creation Date: " + LOCALE_DATE_FORMAT.format(new Date()) + "\n";
        
        return SourceModel.CALDoc.TextBlock.make(
            new SourceModel.CALDoc.TextSegment.TopLevel[] {
                SourceModel.CALDoc.TextSegment.Plain.make(commentText1),
                commentSegment2,
                SourceModel.CALDoc.TextSegment.Plain.make(commentText3)
            });
    }
    
    /**
     * Generate the CALDoc comment for a module.
     * @param moduleName the name of the module.
     * @return the source model for the auto-generated comment.
     */
    private static SourceModel.CALDoc.Comment.Module getModuleCALDoc(ModuleName moduleName) {
        
        // Get the boilerplate strings.
        SourceModel.CALDoc.TextBlock moduleText = getModuleCommentBoilerplate(moduleName);
        
        // Add a tagged block for the author tag, if the "user.name" property is defined.
        SourceModel.CALDoc.TaggedBlock taggedBlocks[] = null;
        
        String userNameProperty = System.getProperty("user.name");
        if (userNameProperty != null) {
            SourceModel.CALDoc.TextBlock authorBlock =
                SourceModel.CALDoc.TextBlock.make(
                    new SourceModel.CALDoc.TextSegment.TopLevel[] {
                        SourceModel.CALDoc.TextSegment.Plain.make(userNameProperty)
                    });
            
            taggedBlocks = new SourceModel.CALDoc.TaggedBlock[] {
                    SourceModel.CALDoc.TaggedBlock.Author.make(authorBlock)
            };
        }
        
        return SourceModel.CALDoc.Comment.Module.make(moduleText, taggedBlocks);
    }
    
    /**
     * Generate the source for a foreign type.
     * 
     * @param foreignClass the class for which to generate a type.
     * @param generationInfo
     * @return the source for the type.
     */
    public static ForeignType generateForeignType(Class<?> foreignClass, GenerationInfo generationInfo) {
              
        // Convert the java name to a valid type cons name.
        // For now, use the fully qualified name.  This will be ugly though.
        String typeConsName = generationInfo.getTypeConsName(foreignClass);
        
        // Determine the generated scopes.
        GenerationScope generationScope = generationInfo.getGenerationScope();
        Scope typeScope = generationScope == GenerationScope.ALL_PRIVATE ? Scope.PRIVATE : Scope.PUBLIC;
        Scope implementationScope = generationScope == GenerationScope.ALL_PUBLIC ? Scope.PUBLIC : Scope.PRIVATE;
        
        /*
         * Possible type classes:
         * 
         * Always ok:
         *   Debug.Show
         *   Prelude.Inputable
         *   Prelude.Outputable
         *   Prelude.Eq
         *   
         * If Comparable:
         *   Prelude.Ord
         * 
         * Only for enumerated types (not automatically generated for foreign types):
         *   Prelude.Enum
         *   Prelude.IntEnum
         *   Prelude.Bounded
         */
        
        //
        // NOTE: if changing the below, change the static XXXUsingTypeClasses String arrays in this class.
        //
        
        List<TypeClass> derivingClauseTypeClassNameList = new ArrayList<TypeClass>(Arrays.asList(new TypeClass[]{
                TypeClass.makeUnqualified(CAL_Debug.TypeClasses.Show.getUnqualifiedName()),
                TypeClass.makeUnqualified(CAL_Prelude.TypeClasses.Inputable.getUnqualifiedName()),
                TypeClass.makeUnqualified(CAL_Prelude.TypeClasses.Outputable.getUnqualifiedName()),
                TypeClass.makeUnqualified(CAL_Prelude.TypeClasses.Eq.getUnqualifiedName())
        }));
        
        if (foreignClass.isPrimitive() || Comparable.class.isAssignableFrom(foreignClass)) {
            derivingClauseTypeClassNameList.add(TypeClass.makeUnqualified(CAL_Prelude.TypeClasses.Ord.getUnqualifiedName()));
        }
        
        TypeClass[] derivingClauseTypeClassNames = 
            derivingClauseTypeClassNameList.toArray(new TypeClass[derivingClauseTypeClassNameList.size()]);
        
        //
        // Create the cal doc comment.
        //
        
        final String calSourceNameOfJavaType = ForeignTypeInfo.getCalSourceName(foreignClass);
       
        return ForeignType.make((CALDoc.Comment.TypeCons)null, typeConsName, typeScope, true, calSourceNameOfJavaType, implementationScope, true, derivingClauseTypeClassNames);
    }
    
    /**
     * Generate the source for a foreign function representing a method.
     * 
     * @param javaClass the class containing the method.
     * @param javaMethod the method.
     * @param generationInfo
     * @return the foreign function definition to call the method.
     */
    public static SourceModel.FunctionDefn.Foreign generateForeignFunction(Class<?> javaClass, Method javaMethod, GenerationInfo generationInfo) {
        return generateForeignFunction(
                javaMethod, "method", 
                javaClass, javaMethod.getReturnType(), javaMethod.getParameterTypes(), 
                generationInfo);
    }
    
    /**
     * Generate the source for a foreign function representing a field.
     * 
     * @param javaClass the class containing the field.
     * @param javaField the field.
     * @param generationInfo
     * @return the foreign function definition to get the field value.
     */
    public static SourceModel.FunctionDefn.Foreign generateForeignFunction(Class<?> javaClass, Field javaField, GenerationInfo generationInfo) {
        return generateForeignFunction(
                javaField, "field", 
                javaClass, javaField.getType(), null, 
                generationInfo);
    }

    /**
     * Generate the source for a foreign function representing a constructor.
     * 
     * @param javaClass the class containing the method.
     * @param javaConstructor the constructor.
     * @param generationInfo
     * @return the foreign function definition invoking the constructor.
     */
    public static SourceModel.FunctionDefn.Foreign generateForeignFunction(Class<?> javaClass, Constructor<?> javaConstructor, GenerationInfo generationInfo) {
        return generateForeignFunction(
                javaConstructor, "constructor", 
                javaClass, javaConstructor.getDeclaringClass(), javaConstructor.getParameterTypes(), 
                generationInfo);
    }

    /**
     * Generate a foreign function for a member of a Java class.
     * 
     * Note: it would be convenient to be able to make javaClass the same as the member's declaring class.
     * However, because of type signatures, we need to generate separate foreign functions for everywhere a function is defined.
     * 
     * eg. for the hashCode method, we want to end up with type signatures something like:
     * JObject_hashCode :: JObject -> Int;
     * JFoo_hashCode :: JFoo -> Int;
     * 
     * @param javaMember the member.
     * @param memberKindString the string describing the member.  "method", "field", or "constructor"
     * @param javaClass the class containing the member.
     * @param returnType
     * @param paramTypes
     * @param generationInfo
     * 
     * @return the source model for the foreign function definition. 
     */
    private static SourceModel.FunctionDefn.Foreign generateForeignFunction(
            final Member javaMember, final String memberKindString, 
            final Class<?> javaClass, final Class<?> returnType, final Class<?>[] paramTypes, 
            final GenerationInfo generationInfo) {
        
        final boolean isStatic = Modifier.isStatic(javaMember.getModifiers());
        
        // Create a valid function name.
        final String functionName = generationInfo.getNewFunctionName(javaMember, javaClass);
        
        // Determine scope.
        final Scope scope = (generationInfo.getGenerationScope() == GenerationScope.ALL_PRIVATE) ? Scope.PRIVATE : Scope.PUBLIC;
        
        // Create the external name.
        final String externalName;
        {
            final StringBuilder externalNameSB = new StringBuilder();
            if (isStatic) {
                externalNameSB.append("static ");
            }
            
            externalNameSB.append(memberKindString);
            if (!(javaMember instanceof Constructor<?>)) {
                final String memberName = javaMember.getName();                                 
                // Constructor.getName() is fully qualified, but Method.getName() and Field.getName() are not.
                final String qualifiedMemberName = javaClass.getName() + "." + memberName;
                
                externalNameSB.append(" ");
                if (isStatic) {
                    externalNameSB.append(qualifiedMemberName);
                } else {
                    externalNameSB.append(memberName);
                }
            }
            externalName = externalNameSB.toString();
        }
                    
        // Create the type signature.
        final Class<?> instanceMemberClass = isStatic  || javaMember instanceof Constructor<?> ? null : javaClass;
        
        final TypeSignature typeSignature = 
            getTypeSignature(paramTypes, returnType, instanceMemberClass, generationInfo);
        
        
        return Foreign.make((SourceModel.CALDoc.Comment.Function)null, functionName, scope, true, externalName, typeSignature);
    }
    
    /**
     * Generate a type signature.
     * 
     * @param paramTypes if not null, the types of any parameters to a function or constructor.
     * @param returnType the return type for a function or constructor.  The field type for a field.
     * @param instanceMemberClass if the member is a non-static method or field, the the class containing the member.  Otherwise null.
     * @param generationInfo
     * @return the type signature representing the provided args.
     */
    private static TypeSignature getTypeSignature(Class<?>[] paramTypes, Class<?> returnType, Class<?> instanceMemberClass, GenerationInfo generationInfo) {
        
        // Build the type expr from right to left..
        
        // The typeExpr defn ends with the return type.
        TypeExprDefn typeExprDefn = getTypeExprForClass(returnType, generationInfo);
        
        // Add the argument types in reverse order.
        if (paramTypes != null) {

            Class<?>[] methodArgTypes = paramTypes;
            for (int i = methodArgTypes.length - 1; i >= 0; i--) {
                
                Class<?> methodArgType = methodArgTypes[i];
                
                TypeExprDefn argTypeCalNameTypeExprDefn = getTypeExprForClass(methodArgType, generationInfo);
                typeExprDefn = TypeExprDefn.Function.make(argTypeCalNameTypeExprDefn, typeExprDefn);
            }
        }
        
        // If a method is not static, the class CAL type has to precede the argument list.
        if (instanceMemberClass != null) {
            
            TypeExprDefn argTypeCalNameTypeExprDefn = getTypeExprForClass(instanceMemberClass, generationInfo);
            typeExprDefn = TypeExprDefn.Function.make(argTypeCalNameTypeExprDefn, typeExprDefn);
        }
        
        // Finally, create and return the type signature.
        return TypeSignature.make(typeExprDefn);
    }
    
    /**
     * Generate a type expr to represent a class.
     * 
     * @param javaClass the class for which a type expr should be calculated.
     * @param generationInfo
     * @return the source model for a type expr defn for the class.
     */
    private static TypeExprDefn getTypeExprForClass(Class<?> javaClass, GenerationInfo generationInfo) {

        // If this is an array, it would be nice to make an array or list from the component types...
//        if (clazz.isArray()) {
//            Class componentType = clazz.getComponentType();
//            return TypeExprDefn.List.make(getTypeExprForClass(componentType, generationInfo));
//        }
        
        QualifiedName typeConsName = generationInfo.getForeignTypeName(javaClass);
        
        // For the unit, make a Unit type.
        if (typeConsName.equals(CAL_Prelude.TypeConstructors.Unit)) {
            return TypeExprDefn.Unit.make();
        }
        
        // Just a regular type cons.
        
        // If in the current module, we can use in unqualified form.
        ModuleName typeConsModuleName = typeConsName.getModuleName();
        if (typeConsModuleName.equals(generationInfo.getCurrentModuleName())) {
            return TypeExprDefn.TypeCons.make(null, typeConsName.getUnqualifiedName());
        
        
        } else if (typeConsModuleName.equals(CAL_Prelude.MODULE_NAME)) {
            // If in the Prelude, may also be able to use the unqualified form, but this means we need to add to the using clause.
            // We cannot use in an unqualifed way if a type with the same unqualifed name exists in the current module.
            String unqualifiedTypeName = typeConsName.getUnqualifiedName();
            
            if (generationInfo.addTypeToPreludeUsingSet(unqualifiedTypeName)) {
                return TypeExprDefn.TypeCons.make(null, unqualifiedTypeName);
            }
        }            
        
        // Use the qualified form.
        return TypeExprDefn.TypeCons.make(typeConsName);
    }
    
}
