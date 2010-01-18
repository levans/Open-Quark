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
 * OverloadingResolver.java
 * Created: July 20, 2001
 * By: Bo Ilic
 */

package org.openquark.cal.compiler;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.openquark.cal.internal.module.Cal.Core.CAL_Prelude_internal;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.module.Cal.Core.CAL_Record;


/**
 * This class holds the methods needed to resolve overloading in the functions defined 
 * in a single module by
 * <ol>
 *   <li> adding hidden dictionary arguments to overloaded functions
 *   <li> supplying the extra arguments required when applying such functions
 *   <li> adding the hidden helper dictionary functions necessary to make this all work
 * </ol>
 * 
 * @author Bo Ilic
 */
final class OverloadingResolver {

    /** Set to true to have debug info printed while performing overload resolution. */
    static final private boolean DEBUG_INFO = false;

    private final CALCompiler compiler;

    private final ModuleTypeInfo currentModuleTypeInfo;

    /** the number of hidden dictionary variables added to resolve overloading */
    private int dictionaryVarCount;
    
    /**
     * The selector variable in a dictionary function such as $dictEq#Int. This will be the last argument of the
     * function, and it will have Int type.
     */
    static final String DICTIONARY_FUNCTION_INDEX = "$i";
    
    /**
     * OverloadingResolver constructor comment.
     */
    OverloadingResolver(CALCompiler compiler, ModuleTypeInfo currentModuleTypeInfo) {
        
        if (compiler == null || currentModuleTypeInfo == null) {
            throw new NullPointerException();
        }

        this.compiler = compiler;
        this.currentModuleTypeInfo = currentModuleTypeInfo;    
    }
    
    /**
     * Creates the parse tree: #(ALT index @(instanceMethodName dictionaryVariables)).
     * This is used in generating the hidden code for entries in an instance dictionary.
     * Note: this version does not work for default class methods.
     *
     * @param index
     * @param instanceMethodName
     * @param dictionaryVariables
     * @return ParseTreeNode
     */
    private ParseTreeNode addDictionaryMethodEntry(int index, QualifiedName instanceMethodName, ParseTreeNode[] dictionaryVariables) {        
        
        //dictionaryVariables is an array of VAR nodes. We need to make into an array of QUALIFIED_VAR nodes.        
        ModuleName currentModuleName = currentModuleTypeInfo.getModuleName();
        int nVars = dictionaryVariables.length;        
        ParseTreeNode[] arguments = new ParseTreeNode[nVars];
        for (int i = 0; i < nVars; ++i) {
            arguments[i] = ParseTreeNode.makeQualifiedVarNode(QualifiedName.make(currentModuleName, dictionaryVariables[i].getText()), null);
        }

        return addDictionaryEntry(index, instanceMethodName, arguments);       
    }
    
    /**
     * Creates the parse tree: #(ALT index @(defaultClassMethodName @(dictionaryFunctionName dictionaryVariables))).
     * This is used in generating the hidden code for entries in an instance dictionary where the instance does
     * not define an instance method, and so the default class method is used instead.
     *     
     * @param index
     * @param defaultClassMethodName
     * @param dictionaryVariables
     * @return ParseTreeNode
     */
    private ParseTreeNode addDictionaryMethodEntryForDefaultClassMethods(int index, QualifiedName defaultClassMethodName, String dictionaryFunctionName, ParseTreeNode[] dictionaryVariables) {        
                
        ParseTreeNode altNode = new ParseTreeNode(CALTreeParserTokenTypes.ALT, "ALT");        

        ParseTreeNode indexNode = ParseTreeNode.makeIntLiteralNodeWithIntegerValue(index);
        altNode.setFirstChild(indexNode);
        
        ParseTreeNode applicationNode = new ParseTreeNode(CALTreeParserTokenTypes.APPLICATION, "@");
        indexNode.setNextSibling(applicationNode);

        ParseTreeNode defaultClassMethodNameNode = ParseTreeNode.makeQualifiedVarNode(defaultClassMethodName, null);        
        applicationNode.addChild(defaultClassMethodNameNode);
        
        //dictionaryVariables is an array of VAR nodes. We need to make into an array of QUALIFIED_VAR nodes.        
        ModuleName currentModuleName = currentModuleTypeInfo.getModuleName();
        int nVars = dictionaryVariables.length;        
        ParseTreeNode[] arguments = new ParseTreeNode[nVars + 1];
        arguments[0] = ParseTreeNode.makeQualifiedVarNode(currentModuleName, dictionaryFunctionName, null);
        for (int i = 0; i < nVars; ++i) {
            arguments[i + 1] = ParseTreeNode.makeQualifiedVarNode(QualifiedName.make(currentModuleName, dictionaryVariables[i].getText()), null);
        }
                
        ParseTreeNode applicationNode2 = new ParseTreeNode(CALTreeParserTokenTypes.APPLICATION, "@");
        defaultClassMethodNameNode.setNextSibling(applicationNode2);
        applicationNode2.addChildren(arguments);
                             
        return altNode;               
    }    
        
    private ParseTreeNode addDictionarySwitchingEntry(int index, QualifiedName ancestorDictionaryName, ParseTreeNode[] arguments) {        
        return addDictionaryEntry(index, ancestorDictionaryName, arguments);                 
    }
    
    private ParseTreeNode addDictionaryEntry(int index, QualifiedName dictionaryEntryName, ParseTreeNode[] arguments) {
        
        ParseTreeNode altNode = new ParseTreeNode(CALTreeParserTokenTypes.ALT, "ALT");        

        ParseTreeNode indexNode = ParseTreeNode.makeIntLiteralNodeWithIntegerValue(index);
        altNode.setFirstChild(indexNode);
        
        ParseTreeNode applicationNode = new ParseTreeNode(CALTreeParserTokenTypes.APPLICATION, "@");
        indexNode.setNextSibling(applicationNode);

        ParseTreeNode dictionaryEntryNode = ParseTreeNode.makeQualifiedVarNode(dictionaryEntryName, null);        
        applicationNode.addChild(dictionaryEntryNode);
        
        applicationNode.addChildren(arguments);                    
                      
        return altNode;              
    }
    
    /**
     * Add hidden helper functions to the parse tree that are used when resolving overloading.
     * Creation date: (4/4/01 2:39:56 PM)
     * @param outerDefnListNode 
     */
    void addOverloadingHelperFunctions(ParseTreeNode outerDefnListNode) {

        outerDefnListNode.verifyType(CALTreeParserTokenTypes.OUTER_DEFN_LIST);
                     
        //get the last top level node, to which we attach other top level nodes
        ParseTreeNode attachingNode = outerDefnListNode.lastChild();
        if (attachingNode == null) {
            //There are no functions defined in this module, and thus no overloading to resolve.
            return;
        }

        if (DEBUG_INFO) {
            System.out.println("Adding hidden overloading helper functions.\n");
        }      
        
        addDictionaryFunctions(attachingNode);

        if (DEBUG_INFO) {
            System.out.println("Finished adding hidden overloading helper functions.\n");
        }
    }
    
    /**
     * Add the dictionary functions. There is a dictionary for each instance for a type
     * constructor (that is not an instance for a type class with only 1 class method and
     * no parent classes) and it is defined in the module in which the instance is defined.                      
     * @param attachingNode
     */    
    private void addDictionaryFunctions(ParseTreeNode attachingNode) {

        for (int instanceN = 0, nInstances = currentModuleTypeInfo.getNTopLevelClassInstances();
             instanceN < nInstances;
             ++instanceN) {

            ClassInstance instance = currentModuleTypeInfo.getNthTopLevelClassInstance(instanceN); //for example, Eq Int

            ParseTreeNode dictFunctionNode = makeDictionaryFunction(instance);

            if (dictFunctionNode != null) {

                attachingNode.setNextSibling(dictFunctionNode);
                attachingNode = dictFunctionNode;

                if (DEBUG_INFO) {
                    System.out.println(attachingNode.toStringTree());
                    System.out.println("");
                }
            }
        }
    }
    
    /**
     * @param instance
     *            the class instance to make a dictionary for
     * @return ParseTreeNode the (hidden) function that defines the dictionary
     *         used by clients of the class instance or null if this instance
     *         does not require a hidden dictionary function.
     */
    private ParseTreeNode makeDictionaryFunction(ClassInstance instance) {

        //For each instance type T of class A add a function $dictA#T
        //where if A1, A2, ..., An are superclasses (in a fixed order) of A and f1, f2, ... are instance methods that implement
        //the class methods of class A then
        //$dictA#T $i = case $i of
        //    0 -> $dictA1#T;
        //    1 -> $dictA2#T;
        //   ..
        //    n -> f1;
        //    n+1 -> f2;
        //    ...
        //
        //For example,
        //$dictNum#Int $i = case $i of
        //    0 -> $dictEq#Int;
        //    1 -> $dictOrd#Int;
        //    2 -> addInt;
        //    3 -> subtractInt;
        //    4 -> multiplyInt;
        //    5 -> divideInt;;

        //For the case of constrained instances, extra dictionary formal arguments need to be added for the constraints
        //and those extra arguments must be transformed appropriately when doing dictionary switching.
        //For example,
        //$dictOrd#Tuple2 $dictvarOrd#0 $dictvarOrd#1 $i = case $i of
        //    0 -> $dictEq#Tuple2 ($getEq#Ord $dictvarOrd#0) ($getEq#Ord $dictvarOrd#1);
        //    1 -> greaterThan $dictvarOrd#0 $dictvarOrd#1;        
        //    2 -> greaterThanEquals $dictvarOrd#0 $dictvarOrd#1;
        //    3 -> lessThan $dictvarOrd#0 $dictvarOrd#1;
        //    4 -> lessThanEquals $dictvarOrd#0 $dictvarOrd#1;; 
          

        TypeClass typeClass = instance.getTypeClass(); //in the case of Eq Int, this is Eq 
        
        //There is an optimization in the case of type classes with 1 class method and no superclasses.
        //Then,
        //$dictClass#TypeCons = theSingleInstanceMethod
        //e.g. $dictOutputable#Int = outputInt;
        //Thus we can just inline the dictionary function, and so we don't need to generate a separate
        //dictionary function for the runtime.             
        if (typeClass.internal_isSingleMethodRootClass()) {
            //no dictionary function is generated since it should be inlined in this case
            return null;          
        }        
        
        TypeExpr classInstanceType = instance.getType(); //in the case of Eq Int, this is Int                 
        final String dictionaryName = instance.getDictionaryFunctionName().getUnqualifiedName();
                
        ParseTreeNode dictFunctionNode = new ParseTreeNode(CALTreeParserTokenTypes.TOP_LEVEL_FUNCTION_DEFN, "TOP_LEVEL_FUNCTION_DEFN");

        ParseTreeNode optionalCALDocNode = new ParseTreeNode(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT, "OPTIONAL_CALDOC_COMMENT");
        dictFunctionNode.setFirstChild(optionalCALDocNode);
        
        ParseTreeNode accessModifierNode = new ParseTreeNode(CALTreeParserTokenTypes.ACCESS_MODIFIER, "ACCESS_MODIFIER");
        optionalCALDocNode.setNextSibling(accessModifierNode);

        ParseTreeNode publicNode = new ParseTreeNode(CALTreeParserTokenTypes.LITERAL_public, "public");
        accessModifierNode.setFirstChild(publicNode);

        ParseTreeNode dictFunctionNameNode = new ParseTreeNode(CALTreeParserTokenTypes.VAR_ID, dictionaryName);
        accessModifierNode.setNextSibling(dictFunctionNameNode);

        ParseTreeNode paramListNode = new ParseTreeNode(CALTreeParserTokenTypes.FUNCTION_PARAM_LIST, "FUNCTION_PARAM_LIST");
        dictFunctionNameNode.setNextSibling(paramListNode);       
         
        ParseTreeNode[] dictionaryParameters = makeDictionaryParameters(instance);       
        paramListNode.addChildren(dictionaryParameters);

        //the parameter $i will always be examined by the case and so is strict
        ParseTreeNode index1Node = new ParseTreeNode(CALTreeParserTokenTypes.STRICT_PARAM, DICTIONARY_FUNCTION_INDEX);
        paramListNode.addChild(index1Node);

        ParseTreeNode caseNode = new ParseTreeNode(CALTreeParserTokenTypes.VIRTUAL_DATA_CONSTRUCTOR_CASE, "case");
        paramListNode.setNextSibling(caseNode);
       
        ParseTreeNode index2Node = ParseTreeNode.makeQualifiedVarNode(currentModuleTypeInfo.getModuleName(), DICTIONARY_FUNCTION_INDEX, null);
        caseNode.setFirstChild(index2Node);

        ParseTreeNode altListNode = new ParseTreeNode(CALTreeParserTokenTypes.ALT_LIST, "ALT_LIST");
        index2Node.setNextSibling(altListNode);
        
        //todoBI addChild is not optimal.
        List<TypeClass> ancestorList = typeClass.calculateAncestorClassList();
        int nAncestors = ancestorList.size();

        for (int i = 0; i < nAncestors; ++i) {

            TypeClass ancestorClass = ancestorList.get(i);
            ClassInstance ancestorInstance;
            if (instance.isUniversalRecordInstance()) {
                ancestorInstance = currentModuleTypeInfo.getVisibleClassInstance(new ClassInstanceIdentifier.UniversalRecordInstance(ancestorClass.getName()));
            } else {
                ancestorInstance = currentModuleTypeInfo.getVisibleClassInstance(ancestorClass, ((TypeConsApp)classInstanceType).getRoot());
            }
            //the dictionary for the ancestor instance is defined in the module in which the ancestor instance is defined.
            QualifiedName ancestorDictName = ancestorInstance.getDictionaryFunctionName();
            ParseTreeNode[] arguments = makeDictionarySwitchingArguments(instance, ancestorInstance);
            ParseTreeNode altNode = addDictionarySwitchingEntry(i, ancestorDictName, arguments);
            altListNode.addChild(altNode);
        }

        if (instance.getNInstanceMethods() != typeClass.getNClassMethods()) {
            throw new IllegalStateException("OverloadingResolver: wrong number of instance methods for instance " + instance);
        }

        for (int i = 0, nMethods = instance.getNInstanceMethods(); i < nMethods; ++i) {

            ParseTreeNode altNode;
            QualifiedName instanceMethod = instance.getInstanceMethod(i);
            if (instanceMethod == null) {   
                //use the default class method, since the instance method was not defined
                
                ClassMethod classMethod = typeClass.getNthClassMethod(i);
                if (!classMethod.hasDefaultClassMethod()) {
                    throw new IllegalStateException("expecting a default class method");
                }
                QualifiedName defaultClassMethod = classMethod.getDefaultClassMethodName(); 
                
                altNode = addDictionaryMethodEntryForDefaultClassMethods(nAncestors + i, defaultClassMethod, dictionaryName, dictionaryParameters);
                
            } else {
           
                altNode = addDictionaryMethodEntry(nAncestors + i, instanceMethod, dictionaryParameters);
            }
            altListNode.addChild(altNode);
        }

        return dictFunctionNode;
    }
    
    /**
     * The dictionary function for childInstance is (essentially) a case expression on an integer index with the a different
     * option for each class method describing the actual instance method that implements the class method, and an option for
     * for each ancestor class of the class that lets one obtain the dictionary function for the ancestor instance.
     * The ancestor instance has certain dictionary variables that it requires, and these are supplied from the dictionary
     * variables of the childInstance via a series of transformation functions. This is a helper method to generate those
     * transforming functions. 
     * @param childInstance
     * @param ancestorInstance
     * @return ParseTreeNode[]
     */
    private ParseTreeNode[] makeDictionarySwitchingArguments(final ClassInstance childInstance, final ClassInstance ancestorInstance) {
        
        //For example, for the (Ord a, Ord b) => Ord (Tuple2 a  b) instance, whose dictionary function starts as:
        //$dictOrd#Tuple2 $dictvarOrd#0 $dictvarOrd#1 $i = case $i of
        //       0 -> $dictEq#Tuple2 ($getEq#Ord $dictvarOrd#0) ($getEq#Ord $dictvarOrd#1);
        //This function returns [($getEq#Ord $dictvarOrd#0), ($getEq#Ord $dictvarOrd#1)].

        List<ParseTreeNode> dictionarySwitchingArgs = new ArrayList<ParseTreeNode>();
        
        List<SortedSet<TypeClass>> childConstraints = childInstance.getDeclaredPolymorphicVarConstraints();               
        List<SortedSet<TypeClass>> ancestorConstraints = ancestorInstance.getDeclaredPolymorphicVarConstraints();
        
        for (int i = 0, nAncestorConstrainedVars = ancestorConstraints.size(); i < nAncestorConstrainedVars; ++i) {
           
            //the constraints on the ith type var in the ancestor instance
            SortedSet<TypeClass> ancestorVarConstraints = ancestorConstraints.get(i);
            
            SortedSet<TypeClass> childVarConstraints = childConstraints.get(i);
            
            //constrainingClassForAncestor is the constraining class in the ancestor's context on the ith type variable   
            for (final TypeClass constrainingClassForAncestor : ancestorVarConstraints) {                                            
                
                //the constraint on the ancestor must be implied by a constraint from the child instance.
                //we must now find the first implying constraint, which can be assumed to exist because of
                //earlier static analysis.                
                TypeClass classImplyingConstraint = getClassImplyingConstraint(childVarConstraints, constrainingClassForAncestor);
 
                //this is the dictionary variable that is defined within the dictionary for the childInstance
                //from which one can extract the dictionary needed to for the ancestor instance.
                String dictionaryVarName = getDictionaryVarName(classImplyingConstraint, i);
                            
                //this is the transforming function that transforms the dictionary argument taken by the childInstance into the dictionary
                //argument required by the ancestor instance.
                final ParseTreeNode dictionarySwitchingArg;                
                if (constrainingClassForAncestor != classImplyingConstraint) {                
                    dictionarySwitchingArg = getDictionarySwitchingNode(constrainingClassForAncestor, classImplyingConstraint, dictionaryVarName);
                } else {
                    //no switching necessary, this is the "null" dictionary switch.
                    //This can happen e.g. both the Foo and Bar constrained instances have their variables
                    //constrained by Foo. This is rather an odd pattern though.
                    //public class Foo a => Bar a where
                    //instance Foo a => Bar [a] where   
                    //instance Foo a => Foo [a] where                    
                    dictionarySwitchingArg = ParseTreeNode.makeQualifiedVarNode(currentModuleTypeInfo.getModuleName(), dictionaryVarName, null);
                }                           
                
                dictionarySwitchingArgs.add(dictionarySwitchingArg);                
            }                        
        }
               
        return dictionarySwitchingArgs.toArray(new ParseTreeNode[]{});
    }
    
    /**
     * A helper function for finding the type class in the child instance constraints that implies the constraint given by 
     * the type class 'constrainingClassForAncestor' in the ancestor instance constraints.
     * @param childVarConstraints
     * @param constrainingClassForAncestor
     * @return TypeClass
     */
    private TypeClass getClassImplyingConstraint(SortedSet<TypeClass> childVarConstraints, TypeClass constrainingClassForAncestor) {
        for (final TypeClass constrainingClassForChild : childVarConstraints) {            
             if (constrainingClassForChild.isSpecializationOf(constrainingClassForAncestor)) {
                 return constrainingClassForChild;
             }
         }
         
         return null;
    }
    
    /**
     * For a constrained instance declaration, the hidden dictionary function takes the resolving index parameter '$i',
     * as well as one extra parameter for each constraint in the context. 
     * @param instance
     * @return ParseTreeNode[] nodes holding the names of the extra argument variables needed in the dictionary for the instance. This will be of
     *      length 0 if the instance is not a constrained instance.
     */
    private ParseTreeNode[] makeDictionaryParameters (ClassInstance instance) {
                       
        //Given the instance declaration
        //"instance (C1 a1, C2 a2, ..., Cn an) => C (T a1 ... an) where ..."
        //where Ci = (Ci1, Ci2, .. Cij) where j is a function of i, and the ai are distinct and j_i can be 0 for an unconstrained variable,
        //then there will be a j1 + j2 + ... + jn dictionary variables added to resolve overloading.
        //j_i of the variables resolve overloading for type variable i.        
                
        List<ParseTreeNode> dictionaryParams = new ArrayList<ParseTreeNode>();
        
        List<SortedSet<TypeClass>> declaredConstraints = instance.getDeclaredPolymorphicVarConstraints();
        
        for (int i = 0, nConstrainedVars = declaredConstraints.size(); i < nConstrainedVars; ++i) {
            
            //the constraints on the ith constrained type variable
            SortedSet<TypeClass> varConstraints = declaredConstraints.get(i);
            
            for (final TypeClass constrainingClass : varConstraints) {
                
                //e.g. $dictvarPrelude.Ord#0
                String dictionaryVarName = getDictionaryVarName(constrainingClass, i);
                dictionaryParams.add(new ParseTreeNode(CALTreeParserTokenTypes.LAZY_PARAM, dictionaryVarName));
            }
            
        }
        
        return dictionaryParams.toArray(new ParseTreeNode[]{});        
    }
    
    /**
     * A helper function to get the name of the variable used in dictionaries to pass along the
     * dictionaries specified by the class constraints.
     * For example, for instance (Eq a, Foo b, Zap b) => Harold (Tuple2 a b) then
     * for the constraining class Foo, and type variable b, of index 1, the dictionary variable name
     * is $dictvarPrelude.Foo#1. 
     * @param constrainingClass
     * @param typeVarIndex
     * @return String
     */
    private static String getDictionaryVarName(TypeClass constrainingClass, int typeVarIndex) {
        return "$dictvar" + constrainingClass.getName() + "#" + typeVarIndex;        
    }    
    
    /**
     * Returns the type class constraint on inContextTypeVar that is a specialization of 
     * applicationTypeClass.
     * Creation date: (4/12/01 6:54:23 PM)
     * @return TypeClass
     * @param applicationTypeClass 
     * @param inContextTypeVar 
     */
    static private TypeClass getFunctionTypeClass(TypeClass applicationTypeClass, TypeVar inContextTypeVar) {

        
        for (final TypeClass functionTypeClass : inContextTypeVar.getTypeClassConstraintSet()) {
            
            if (functionTypeClass.isSpecializationOf(applicationTypeClass)) {
                return functionTypeClass;
            }
        }

        return null;
    } 
    
    /**
     * Returns the type class constraint on inContextRecordVar that is a specialization of 
     * applicationTypeClass.
     * Creation date: (4/12/01 6:54:23 PM)
     * @return TypeClass
     * @param applicationTypeClass 
     * @param inContextRecordVar 
     */
    static private TypeClass getFunctionTypeClass(TypeClass applicationTypeClass, RecordVar inContextRecordVar) {
        
        for (final TypeClass functionTypeClass : inContextRecordVar.getTypeClassConstraintSet()) {
         
            if (functionTypeClass.isSpecializationOf(applicationTypeClass)) {
                return functionTypeClass;
            }
        }

        return null;
    }       
    
    /**
      * Fixes the definitions of the functions specified in the overloadingInfoList to
      * add hidden dictionary arguments, and fix up applications.
      *
      * Creation date: (7/20/01)
      * @param overloadingInfoList List 
      */
     void resolveOverloading(List<OverloadingInfo> overloadingInfoList) {

         //now add dictionary arguments to the function defined in this declaration group, and fix
         //up applications within the function definitions.
         int nOverloadingInfos = overloadingInfoList.size();
         for (int i = 0; i < nOverloadingInfos; ++i) {
             OverloadingInfo overloadingInfo = overloadingInfoList.get(i);
             resolveFunctionOverloading(overloadingInfo);
         }

         if (DEBUG_INFO) {            
             for (int i = 0; i < nOverloadingInfos; ++i) {
                 OverloadingInfo overloadingInfo = overloadingInfoList.get(i);
                 ParseTreeNode functionNode = overloadingInfo.getFunctionNode();
                 System.out.println("overloading for: " + overloadingInfo.getFunctionName());
                 System.out.println(functionNode.toStringTree());
                 System.out.println("");
             }
         }
     } 
     
    /**
     * Fixes the definition of a single function as specified by overloadingInfo to take into
     * account overloading introduced by the user of type classes. Warning: not all applications
     * occurring within the function's definition will be resolved by this function. For example,
     * equalsTest :: Eq a => a -> a -> Boolean;
     * equalsTest x y = let result = equals x y; in result;  
     * The application at "equals" is resolved by adding arguments to "equalsTest".
     *
     * Creation date: (4/12/01 6:17:19 PM)
     * @param overloadingInfo information necessary to patch up the definition of a given function to resolve overloading
     */
    private void resolveFunctionOverloading(OverloadingInfo overloadingInfo) {

        //Add extra dictionary arguments to the function, one for each class constraint on each type
        //(or record) variable. For example, if the constraints are Num a, Enum a, Ord b, and so far there were
        //14 hidden dictionary variables added, then there would be 3 dictionary variables added
        //$dictvar15Enum, $dictvar16Num and $dictvar17Ord. The indices are added to ensure no conflict
        //between duplicated variable names.

        List<String> dictVars = new ArrayList<String>();
        TypeClass firstTypeClass = null;
        
        Set<PolymorphicVar> genericConstrainedPolyVars = overloadingInfo.getGenericClassConstrainedPolymorphicVars();
        int nPolyVars = genericConstrainedPolyVars.size();

        Map<PolymorphicVar, Map<TypeClass, String>> dictionaryVariablesMap = new HashMap<PolymorphicVar, Map<TypeClass, String>>(); //PolymorphicVar -> (TypeClass -> String)

        if (nPolyVars > 0) {

            ParseTreeNode headNode = new ParseTreeNode();
            ParseTreeNode lastNode = headNode;

            for (final PolymorphicVar polyVar : genericConstrainedPolyVars) {
                                   
                SortedSet<TypeClass> typeClassConstraintSet = OverloadingResolver.getTypeClassConstraintSet(polyVar);                         

                Map<TypeClass, String> typeClassToVarNameMap = new HashMap<TypeClass, String>(); //TypeClass -> String
                dictionaryVariablesMap.put(polyVar, typeClassToVarNameMap);
               
                for (final TypeClass typeClass : typeClassConstraintSet) {
                    
                    String dictVarName = getDictionaryVarName (typeClass, dictionaryVarCount);
                    typeClassToVarNameMap.put(typeClass, dictVarName);

                    dictVars.add(dictVarName);
                    if(firstTypeClass == null)
                        firstTypeClass = typeClass;
                    
                    ++dictionaryVarCount;

                    lastNode.setNextSibling(new ParseTreeNode(CALTreeParserTokenTypes.LAZY_PARAM, dictVarName));
                    lastNode = lastNode.nextSibling();
                }
            }

            ParseTreeNode paramListNode = overloadingInfo.getFunctionArgsNode();
            paramListNode.verifyType(CALTreeParserTokenTypes.FUNCTION_PARAM_LIST);
            ParseTreeNode firstVisibleArgNode = paramListNode.firstChild();
            if (firstVisibleArgNode != null) {            
                lastNode.setNextSibling(firstVisibleArgNode);
            }
            paramListNode.setFirstChild(headNode.nextSibling());
        }
        
        overloadingInfo.setDictionaryVariablesMap(dictionaryVariablesMap);

        // Fix up the applications within the definition of the function

        for (int i = 0, nApplications = overloadingInfo.getNApplications(); i < nApplications; ++i) {

            ApplicationInfo apInfo = overloadingInfo.getApplication(i);
            
            if (!optimizeClassMethodOverloading(overloadingInfo, apInfo)) {  
                
                if (apInfo.getAppliedFunctionalAgent().getName().equals(CAL_Record.Functions.dictionary)) {
                    //we must mangle the dictionary function's arguments so that it is passed the dictionary
                    if (dictVars.size() != 1 || 
                            overloadingInfo.getGenericClassConstrainedPolymorphicVars().size() != 1 ||
                            !(overloadingInfo.getGenericClassConstrainedPolymorphicVars().iterator().next() instanceof RecordVar)) {
                        compiler.logMessage(new CompilerMessage(apInfo.getAppliedFunctionNode(), new MessageKind.Error.RecordDictionaryMisused()));
                    } else {
                        mangleDictionaryArgs(apInfo, dictVars.get(0), firstTypeClass);
                    }
                } else
                    resolveApplicationOverloading(apInfo, overloadingInfo);
            }
        }
    }     
    
    /**
     * Class methods applied to dictionary literals can be evaluated at compile time. For example,
     * we can replace Prelude.add $dictNum#Int by Prelude.addInt at compile time..
     *    
     * @param overloadingInfo
     * @param apInfo 
     * @return boolean true if the class method overloading was resolved at compile time by this call
     */
    private boolean optimizeClassMethodOverloading(OverloadingInfo overloadingInfo, ApplicationInfo apInfo) {              

        if (!apInfo.isClassMethod()) {
            //this optimization is only for class method calls such as 'lessThan', and not for 
            //overloaded function calls such as 'sort'. 
            return false;
        }
        
        Set<PolymorphicVar> constrainedTypeVars = apInfo.getGenericClassConstrainedPolymorphicVars();

        int nConstrainedTypeVars = constrainedTypeVars.size();

        if (nConstrainedTypeVars > 1) {
            //this can theoretically happen if a class method is overloaded in a type variable that is not
            //part of the class hierarchy for the type class being defined.
            return false;
        }
       
        TypeVar typeVar = (TypeVar) constrainedTypeVars.iterator().next();

        //the typeVar was specialized for the context in which it is being used in the
        //definition of this function.
        TypeExpr inContextTypeExpr = typeVar.prune();
        
        if (inContextTypeExpr instanceof TypeVar || inContextTypeExpr instanceof RecordType) {
            return false;            
        }

        //the overloading has been resolved to a concrete type.  
        
        TypeConsApp inContextTypeConsApp = (TypeConsApp)inContextTypeExpr;                     
                
        TypeClass typeClass = typeVar.getTypeClassConstraintSet().iterator().next();
        
        ClassInstance classInstance = currentModuleTypeInfo.getVisibleClassInstance(typeClass, inContextTypeConsApp.getRoot());

        ParseTreeNode appliedFunctionNode = apInfo.getAppliedFunctionNode();

        final QualifiedName classMethodName = appliedFunctionNode.toQualifiedName();    
        
        final QualifiedName resolvingFunctionName = classInstance.getInstanceMethod(classMethodName);
        if (resolvingFunctionName == null) {
            //if a default class method is used, do not attempt to optimize
            //todoBI we could optimize this case more in the future
            return false;           
        }

        //A common case is when Prelude.fromInt, Prelude.fromLong or Prelude.fromInteger (the functions used internally
        //when compiling integer literals) have resolved to the resolving function for 
        //Byte, Short, Int, Long, Float, Double, Integer or Decimal       
        //We'll explicitly perform the function evaluations here in the common cases.

        ParseTreeNode argNode = appliedFunctionNode.nextSibling();
        if (argNode != null) {
            if (resolvingFunctionName.equals(CAL_Prelude.Functions.id)) {
                    
                //we explicitly replace id x by x.
            
                appliedFunctionNode.copyContentsFrom(argNode);
                return true;
                
            } else if (argNode.getType() == CALTreeParserTokenTypes.INTEGER_LITERAL && resolvingFunctionName.getModuleName().equals(CAL_Prelude.MODULE_NAME)) {
               
                final Number numberValue = argNode.getLiteralValueForMaybeMinusIntLiteral();                      
                                                               
                if (resolvingFunctionName.equals(CAL_Prelude_internal.Functions.intToInt)
                    || resolvingFunctionName.equals(CAL_Prelude_internal.Functions.longToInt)
                    || resolvingFunctionName.equals(CAL_Prelude_internal.Functions.integerToInt)) {
                    
                    appliedFunctionNode.copyContentsFrom(argNode);
                    appliedFunctionNode.setText("int value stored in data field");                    
                    appliedFunctionNode.setIntegerValueForMaybeMinusIntLiteral(Integer.valueOf(numberValue.intValue()));
                    appliedFunctionNode.setType(CALTreeParserTokenTypes.INTEGER_LITERAL);
                    return true;
                    
                } else if (
                    resolvingFunctionName.equals(CAL_Prelude_internal.Functions.intToByte)
                    || resolvingFunctionName.equals(CAL_Prelude_internal.Functions.longToByte)
                    || resolvingFunctionName.equals(CAL_Prelude_internal.Functions.integerToByte)) { 
                    
                    appliedFunctionNode.copyContentsFrom(argNode);
                    appliedFunctionNode.setType(CALTreeParserTokenTypes.INTEGER_LITERAL);
                    appliedFunctionNode.setText("byte value stored in data field");
                    appliedFunctionNode.setByteValueForMaybeMinusIntLiteral(Byte.valueOf(numberValue.byteValue()));                  
                    return true;
                    
                } else if (
                    resolvingFunctionName.equals(CAL_Prelude_internal.Functions.intToShort)
                    || resolvingFunctionName.equals(CAL_Prelude_internal.Functions.longToShort)
                    || resolvingFunctionName.equals(CAL_Prelude_internal.Functions.integerToShort)) {   
                    
                    appliedFunctionNode.copyContentsFrom(argNode);
                    appliedFunctionNode.setType(CALTreeParserTokenTypes.INTEGER_LITERAL);
                    appliedFunctionNode.setText("short value stored in data field");
                    appliedFunctionNode.setShortValueForMaybeMinusIntLiteral(Short.valueOf(numberValue.shortValue()));                  
                    return true;
                    
                } else if (
                    resolvingFunctionName.equals(CAL_Prelude_internal.Functions.intToLong)
                    || resolvingFunctionName.equals(CAL_Prelude_internal.Functions.longToLong)
                    || resolvingFunctionName.equals(CAL_Prelude_internal.Functions.integerToLong)) {
                    
                    appliedFunctionNode.copyContentsFrom(argNode);
                    appliedFunctionNode.setType(CALTreeParserTokenTypes.INTEGER_LITERAL);
                    appliedFunctionNode.setText("long value stored in data field");                    
                    appliedFunctionNode.setLongValueForMaybeMinusIntLiteral(Long.valueOf(numberValue.longValue()));
                    return true;
                    
                } else if (
                    resolvingFunctionName.equals(CAL_Prelude_internal.Functions.intToInteger)
                    || resolvingFunctionName.equals(CAL_Prelude_internal.Functions.longToInteger)
                    || resolvingFunctionName.equals(CAL_Prelude_internal.Functions.integerToInteger)) {
                    
                    appliedFunctionNode.copyContentsFrom(argNode);
                    appliedFunctionNode.setType(CALTreeParserTokenTypes.INTEGER_LITERAL);
                    appliedFunctionNode.setText("BigInteger value stored in data field");
                    final BigInteger bigIntegerValue;
                    if (numberValue instanceof BigInteger) {
                        bigIntegerValue = (BigInteger)numberValue;
                    } else {
                        bigIntegerValue = BigInteger.valueOf(numberValue.longValue());
                    }
                    appliedFunctionNode.setBigIntegerValueForMaybeMinusIntLiteral(bigIntegerValue);                  
                    return true;
                    
                } else if (
                    resolvingFunctionName.equals(CAL_Prelude_internal.Functions.intToFloat)
                    || resolvingFunctionName.equals(CAL_Prelude_internal.Functions.longToFloat)
                    || resolvingFunctionName.equals(CAL_Prelude_internal.Functions.integerToFloat)) {    
                    
                    appliedFunctionNode.copyContentsFrom(argNode);
                    appliedFunctionNode.setType(CALTreeParserTokenTypes.FLOAT_LITERAL);
                    appliedFunctionNode.setText("float value stored in data field");
                    appliedFunctionNode.setFloatValueForFloatLiteral(new Float(numberValue.floatValue()));                  
                    return true;
                    
                } else if (
                    resolvingFunctionName.equals(CAL_Prelude_internal.Functions.intToDouble)
                    || resolvingFunctionName.equals(CAL_Prelude_internal.Functions.longToDouble)
                    || resolvingFunctionName.equals(CAL_Prelude_internal.Functions.integerToDouble)) {
                    
                    appliedFunctionNode.copyContentsFrom(argNode);
                    appliedFunctionNode.setType(CALTreeParserTokenTypes.FLOAT_LITERAL);
                    appliedFunctionNode.setText("double value stored in data field");
                    appliedFunctionNode.setDoubleValueForFloatLiteral(new Double(numberValue.doubleValue()));                  
                    return true;
                } 
                
            } 
            
            //todoBI there are more cases that could be simplified here                             
        }

        //all we can do is replace the class method by an explicit instance method call. This
        //is still very good indeed!        
        ParseTreeNode moduleNameNode = appliedFunctionNode.firstChild();
        ModuleNameUtilities.setModuleNameIntoParseTree(moduleNameNode, resolvingFunctionName.getModuleName());
        ParseTreeNode scNameNode = moduleNameNode.nextSibling();
        scNameNode.setText(resolvingFunctionName.getUnqualifiedName());       
        
        if (inContextTypeConsApp.getNArgs() > 0) {
            
            //handle the "inside" constraints e.g.
            //equals [(1.0, 'a')] [(1.0, 'b')] 
            //resolves to the following because of the optimization
            //equalsList (dictEq#Pair dictEq#Double dictEq#Char) [(1.0, 'a')] [(1.0, 'b')]
            //instead of
            //equals (dictEq#List (dictEq#Pair dictEq#Double dictEq#Char)) [(1.0, 'a')] [(1.0, 'b')]               
            
            //here is how it would be resolved without optimizations
            ParseTreeNode overloadingArgument = getOverloadingArgument(inContextTypeExpr, typeClass, overloadingInfo, apInfo);
            overloadingArgument.verifyType(CALTreeParserTokenTypes.APPLICATION);
            overloadingArgument.firstChild().verifyType(CALTreeParserTokenTypes.QUALIFIED_VAR);
            //this will be of the form (APPLICATION $dictTypeClass#InContextTypeCons arg1 arg2 ... argn) 
            //where the argi supply evidence for the overloading on the args of inContextTypeCons implied by the instance.
            ParseTreeNode extraDictionaryArguments = overloadingArgument.getChild(1);
               
            ParseTreeNode instanceFunctionNode = new ParseTreeNode();
            instanceFunctionNode.copyContentsFrom(appliedFunctionNode);
            appliedFunctionNode.setType(CALTreeParserTokenTypes.APPLICATION);
            appliedFunctionNode.setText("@");               
            appliedFunctionNode.setFirstChild(instanceFunctionNode);            
            instanceFunctionNode.setNextSibling(extraDictionaryArguments);            
                      
        } 
                  
        return true;
    }
    
    /**
     * This function mangles the arguments to the dictionary function,
     * so that they are transformed to the enclosing functions dictionary arg
     * and the index of the named method function.
     * @param apInfo
     * @param dictVar - the name of the first dictionary in the enclosing function
     * @param typeClass - the type class that the dictionary represents
     */
    private void mangleDictionaryArgs(ApplicationInfo apInfo, String dictVar, TypeClass typeClass) {
        ParseTreeNode appliedFunctionNode = apInfo.getAppliedFunctionNode();
        
        if (appliedFunctionNode.nextSibling().nextSibling().getType() != CALTreeParserTokenTypes.STRING_LITERAL) {
            //the second argument to dictionary must be a string literal   
            compiler.logMessage(new CompilerMessage(apInfo.getAppliedFunctionNode(), new MessageKind.Error.RecordDictionaryArgumentsInvalid()));         
            return;
   
        }
        
        //get the name of method 
        String fname = appliedFunctionNode.nextSibling().nextSibling().getText();  

        //trim the quotes from the string literal
        if (fname.length() > 2)
            fname = fname.substring(1, fname.length() -1);
        
        //find the index of the typeclass function.
        int index = typeClass.getClassMethodIndex(fname);
        
        if (index < 0) {
            compiler.logMessage(new CompilerMessage(apInfo.getAppliedFunctionNode(), new MessageKind.Error.DictionaryMethodDoesNotExist(typeClass.getName().toSourceText(), fname)));         
            return;
        }

        if (typeClass.internal_isSingleMethodRootClass())
            index = -1;
        else {
            index += typeClass.calculateAncestorClassList().size();
        }

        ParseTreeNode indexNode = ParseTreeNode.makeIntLiteralNodeWithIntegerValue(index);
        
        //mangle the parse tree so we change the args to be the dictionary function
        //rather than the record and the index rather
        //than the name of the method.
        appliedFunctionNode.nextSibling().nextSibling().copyContentsFrom(indexNode);
        appliedFunctionNode.nextSibling().getChild(1).setText(dictVar);        
    }
    
    /**
     * Adds extra arguments as required to resolve overloading in a particular application of
     * a function. For example, if
     * f :: Num a => a -> Int
     * Then "f 2.0" would be converted to "f $dictNum#Double 2.0" and
     * "f x" may be converted to "f $dictvar2Num x".
     *
     * Creation date: (4/12/01 6:18:32 PM)
     * @param apInfo information needed to resolve one particular application of a function
     * @param overloadingInfo the OverloadingInfo object corresponding to the function in whose
     *          definition the apInfo above occurs.    
     */
    private void resolveApplicationOverloading(ApplicationInfo apInfo, OverloadingInfo overloadingInfo) {        

        final ParseTreeNode headNode = new ParseTreeNode();

        final ParseTreeNode lastNode = resolveApplicationOverloadingHelper(apInfo, overloadingInfo, headNode);        

        if (headNode == lastNode) {
            //no arguments need to be added.
            return;
        }
        
        //In principle, adding the extra overloading expressions is done differently depending on whether the
        //applied function is an operator or a textually named function. This is simply because the structure of the
        //parse trees are different. In the operator case, the operator is at the root of its operands. In the textually
        //named function case, the applied function's first argument is its next sibling.
        //Since at this point, all operators have been converted to textual applicative forms, we need only handle the
        //textual case.

        ParseTreeNode appliedFunctionNode = apInfo.getAppliedFunctionNode();
        
        SourcePosition sourcePosition = appliedFunctionNode.getChild(1).getSourcePosition();
        ParseTreeNode applicationNode = new ParseTreeNode(CALTreeParserTokenTypes.APPLICATION, "@", sourcePosition);
        
        if (apInfo.isClassMethod()) {
            
            //class method functions such as:
            //equals d = d 0;
            //notEquals d = d 1;
            //multiply d = d 9;
            //and in general, classMethodName d = d dictionaryIndex;
            //always occur in saturated form, and hence can be inlined as well as not generated as runtime entities.             
            //
            //if headNode points to extra dictionary arguments d1, d2, ..., dn then replace the var f by
            //(@ d1 dictionaryIndexOfF d2 ... dn).
            
            //a special case is if dictionaryIndex is -1, in which case the class method function is of the form:
            //output d = d;
            //
            //if headNode points to extra dictionary arguments d1, d2, ..., dn then replace the var f by
            //(@ d1 d2 ... dn).
            
            //The situation is more complicated if a class method is overloaded, such as:
            //class B b where methodB :: (A a, C c) -> a -> b -> c; ... (class B has more than 1 method)
            //then 
            //methodB dA dB dC = (dB 0) dA dC;
            //or equivalently (dropping trailing arguments)
            //methodB dA dB = (dB 0) dA; //0 is the dictionary index of methodB
            //
            //and if classB had only a single class method, then
            //methodB dA dB dC = dB dA dC;
            //or equivalently
            //methodB dA dB = dB dA;
            
            ClassMethod classMethod = (ClassMethod)apInfo.getAppliedFunctionalAgent();
            final int dictionaryIndex = classMethod.internal_getDictionaryIndex();
            final int classTypeVarPolymorphicIndex = classMethod.getClassTypeVarPolymorphicIndex();
            
            //If the dictionary nodes are (d1 d2 ... dn), then move the node with index classTypeVarPolymorphicIndex to the front
            //for example, if classTypeVarPolymorphicIndex = 2 then
            //d1 d2 d3 d4 d5 .... ---> d3 d1 d2 d4 d5 ...
            if (classTypeVarPolymorphicIndex != 0) {
                
                ParseTreeNode previousToClassDictNode = headNode;
                for (int i = 0; i < classTypeVarPolymorphicIndex; ++i) {
                    previousToClassDictNode = previousToClassDictNode.nextSibling();
                }
                ParseTreeNode classDictNode = previousToClassDictNode.nextSibling();
                previousToClassDictNode.setNextSibling(classDictNode.nextSibling());
                classDictNode.setNextSibling(headNode.nextSibling());
                headNode.setNextSibling(classDictNode);
            }
            
            ParseTreeNode firstArgNode = headNode.nextSibling();
            
            if (dictionaryIndex != -1) {
                
                ParseTreeNode dictionaryIndexNode = ParseTreeNode.makeIntLiteralNodeWithIntegerValue(dictionaryIndex);                                
                
                ParseTreeNode secondArgNode = firstArgNode.nextSibling(); //could be null   
                
                firstArgNode.setNextSibling(dictionaryIndexNode);
                dictionaryIndexNode.setNextSibling(secondArgNode);
            }
                                  
            applicationNode.setFirstChild(firstArgNode);
            applicationNode.setNextSibling(appliedFunctionNode.nextSibling());
            appliedFunctionNode.copyContentsFrom(applicationNode);
            
            return;                            
        } 
        
        //if headNode points to extra dictionary arguments d1, d2, ..., dn then replace the var f by
        //(@ f d1 d2 ... dn).

        QualifiedName functionName = appliedFunctionNode.toQualifiedName();            

        ParseTreeNode varNode = ParseTreeNode.makeQualifiedVarNode(functionName, sourcePosition);
        varNode.setNextSibling(headNode.nextSibling());
            
        applicationNode.setFirstChild(varNode);
        applicationNode.setNextSibling(appliedFunctionNode.nextSibling());

        appliedFunctionNode.copyContentsFrom(applicationNode);        
    }      
          
    /**
     * Adds extra arguments as required to resolve overloading in a particular application of
     * a function. The extra overloading arguments can be dictionaries, dictionary
     * variables or dictionary switching functions applied to dictionary variables,
     * (or some more complicated expression involving the above in the case of constrained instances)
     *
     * Creation date: (4/12/01)
     * @param apInfo
     * @param overloadingInfo
     * @param headNode add extra dictionary argument nodes as siblings to this node
     * @return last dictionary node added to resolve this overloading 
     */
    private ParseTreeNode resolveApplicationOverloadingHelper(ApplicationInfo apInfo, OverloadingInfo overloadingInfo, ParseTreeNode headNode) {

        //there is an overloading argument expression required for each constraint in the applied function's type (prior to specialization
        //in this context). For example, if the type is (Eq a, Foo a, Bar b, Zap b, Ord c) => ..., then 5 argument expressions are needed.

        Set<PolymorphicVar> genericConstrainedPolyVars = apInfo.getGenericClassConstrainedPolymorphicVars();

        ParseTreeNode lastNode = headNode;

        for (final PolymorphicVar polyVar : genericConstrainedPolyVars) {
         
            for (final TypeClass typeClass : OverloadingResolver.getTypeClassConstraintSet(polyVar)) {
               
                //this is a ParseTreeNode for an expression that needs to be inserted as an argument at the application site to
                //resolve the overloading constraint imposed by typeClass-polyVar.
                ParseTreeNode overloadingArgumentNode;
                if (polyVar instanceof TypeVar) {                
                    overloadingArgumentNode = getOverloadingArgument((TypeVar)polyVar, typeClass, overloadingInfo, apInfo);
                } else if (polyVar instanceof RecordVar) {
                    //the dictionary for the wrapperRecordType may be something like:
                    //getEq#Record {field1 = dictEqInt, field2 = dictEqChar}
                    //which is then stripped to get 
                    //{field1 = dictEqInt, field2 = dictEqChar}
                    //the dictionary of the underlying recordVar.
                    RecordType wrapperRecordType = new RecordType((RecordVar)polyVar, Collections.<FieldName, TypeExpr>emptyMap());
                    overloadingArgumentNode = getOverloadingArgument(wrapperRecordType, typeClass, overloadingInfo, apInfo).getChild(1);                   
                } else {
                    throw new IllegalStateException();
                }

                lastNode.setNextSibling(overloadingArgumentNode);
                lastNode = lastNode.nextSibling();
            }
        }

        return lastNode;
    }
    
    /**
     * Converts the node @(classMethodDefault arg1 ... argn) to @(classMethodDefault @(Prelude.unsafeCoerce arg1 ... argn).
     * 
     * @param applicationNode
     * @return the patched application node
     */
    static private ParseTreeNode patchApplicationNode(ParseTreeNode applicationNode) {
        applicationNode.verifyType(CALTreeParserTokenTypes.APPLICATION);
        ParseTreeNode apNode = new ParseTreeNode(CALTreeParserTokenTypes.APPLICATION, "@", null);
        ParseTreeNode classMethodDefaultNode = applicationNode.firstChild();
        apNode.setFirstChild(classMethodDefaultNode);
        ParseTreeNode unsafeCoerceNode = ParseTreeNode.makeQualifiedVarNode(CAL_Prelude.Functions.unsafeCoerce, null);
        classMethodDefaultNode.setNextSibling(unsafeCoerceNode);
        return apNode;       
    }
    
    /**
     * Provides the expression that provides the evidence that the overloading constraint given by the pair 
     * typeClass-(the typeVar instantiated to inContextTypeExpr) is actually satisfied.
     * 
     * @param typeExpr
     * @param typeClass
     * @param overloadingInfo
     * @param apInfo
     * @return ParseTreeNode
     */   
    private ParseTreeNode getOverloadingArgument(TypeExpr typeExpr, TypeClass typeClass, OverloadingInfo overloadingInfo, ApplicationInfo apInfo) {
                                
        //Here are some examples showing how resolving adds an overloading argument that is a complex expression.
        //
        //equals (1.0, 'a') (2.0, 'b')
        //after overloading helpers are added this is:
        //(equals (dictEq#Pair dictEq#Double dictEq#Char)) (1.0, 'a') (2.0, 'b')    
        //the overloading for Eq a => a is resolved by the overloading argument (dictEq#Pair dictEq#Double dictEq#Char)
        //
        //equals [(1.0, 'a')] [(2.0, 'b')]
        //after overloading helpers are added this is:
        //(equals (dictEq#List (dictEq#Pair dictEq#Double dictEq#Char))) (1.0, 'a') (2.0, 'b')    
        //the overloading for Eq a => a is resolved by the overloading argument (dictEq#List (dictEq#Pair dictEq#Double dictEq#Char))
        //
        //if x and y has type Ord a => a, then
        //equals [(1.0, x)] [(2.0, y)]
        //after overloading helpers are added this is (where we pick 14 for the dictionary variable number- this will vary):
        //(equals (dictEq#List (dictEq#Pair dictEq#Double (getEq#Ord dictvarOrd14)))) (1.0, x) (2.0, x)                      
        
        //the typeVar was specialized for the context in which it is being used in the
        //definition of this function.        
        TypeExpr inContextTypeExpr = typeExpr.prune();
        
        if (inContextTypeExpr instanceof TypeConsApp) {

            //the overloading has been resolved to a concrete type. For example, if the type
            //variable a has constraints Enum a and Num a and it is instantiated to an Int
            //then we add $dictEnum#Int and $dictNum#Int (dictionary function valued) arguments.

            TypeConsApp typeConsApp = (TypeConsApp) inContextTypeExpr;
            
            ClassInstance classInstance = currentModuleTypeInfo.getVisibleClassInstance(typeClass, typeConsApp.getRoot());

            //The dictionary is defined in the module in which the instance is declared.
            QualifiedName dictName = classInstance.getDictionaryFunctionName();
            
            final int nArgs = typeConsApp.getNArgs();
            ParseTreeNode dictionaryNode = ParseTreeNode.makeQualifiedVarNode(dictName, null);
            
            //For example, Debug.show has a single class method with a default. If the instance uses this default,
            //then the dictionary resolution takes on a special form.
            boolean isSingleMethodRootClassUsingDefaultClassMethod =
                typeClass.internal_isSingleMethodRootClass() && classInstance.getInstanceMethod(0) == null;
            
            //as a compile-time optimization, treat the case of 0 type arguments separately
            
            if (nArgs == 0) {
                if (isSingleMethodRootClassUsingDefaultClassMethod) {                         
                    ParseTreeNode apNode = new ParseTreeNode(CALTreeParserTokenTypes.APPLICATION, "@", null);
                    apNode.setFirstChild(dictionaryNode);
                    ParseTreeNode unsafeCoerceNode = ParseTreeNode.makeQualifiedVarNode(CAL_Prelude.Functions.unsafeCoerce, null);
                    dictionaryNode.setNextSibling(unsafeCoerceNode);
                    return apNode;                             
                }
                return dictionaryNode;                        
            } 
              
            //we must supply the evidence for the constraints on any of the type arguments.                     
            ParseTreeNode applicationNode = new ParseTreeNode(CALTreeParserTokenTypes.APPLICATION, "@", null);                        
            applicationNode.setFirstChild(dictionaryNode);
            
            //todoBI can make getDeclaredVarConstraints faster.
            List<SortedSet<TypeClass>> varConstraintsArray = classInstance.getDeclaredPolymorphicVarConstraints();
            
            for (int i = 0; i < nArgs; ++i) {
                
                //the evidence needed for argument i depends on what constraints are imposed on argument i in the instance 'classInstance'.
                
                SortedSet<TypeClass> varConstraints = varConstraintsArray.get(i);
                for (final TypeClass constrainingTypeClassForArg : varConstraints) {
                                        
                    ParseTreeNode argumentDictionaryNode = getOverloadingArgument (typeConsApp.getArg(i), constrainingTypeClassForArg, overloadingInfo, apInfo);
                    applicationNode.addChild(argumentDictionaryNode);
                }
            }  
            
            if (isSingleMethodRootClassUsingDefaultClassMethod) {
                //for single method root classes that are in fact using the default class method,
                //the dictionary node must be modified to include an ignored extra argument. e.g.
                //Show Coin ---> Debug.showDefault Prelude.unsafeCoerce
                //Show Tuple2 ---> Debug.showDefault (Prelude.unsafeCoerce arg1Dict arg2Dict) 
                //Prelude.unsafeCoerce is just chosen as a convenient argument to be ignored.
               
                //Modify @(classMethodDefault arg1 ... argn) to @(classMethodDefault @(unsafeCoerce arg1 ... argn)
                
                return patchApplicationNode(applicationNode);                                   
            }
            
            return applicationNode;
                        
        } else if (inContextTypeExpr instanceof RecordType) { 
        
            RecordType recordType = (RecordType) inContextTypeExpr;
            
            ClassInstance classInstance = currentModuleTypeInfo.getVisibleClassInstance(new ClassInstanceIdentifier.UniversalRecordInstance(typeClass.getName()));
            
            //The dictionary is defined in the module in which the instance is declared.
            QualifiedName dictName;
            if (classInstance != null) {
                dictName = classInstance.getDictionaryFunctionName();
            } else {
                //the class instance may be null when the record is not
                //a member of the type class that is used to constrain the
                //record fields.
                dictName = QualifiedName.make("Unused", "$dummyDict");
            }

            ParseTreeNode dictionaryNode = ParseTreeNode.makeQualifiedVarNode(dictName, null);
            
            boolean isSingleMethodRootClassUsingDefaultClassMethod =
                typeClass.internal_isSingleMethodRootClass() && 
                (classInstance != null &&
                classInstance.getInstanceMethod(0) == null);
            
            ParseTreeNode applicationNode = new ParseTreeNode(CALTreeParserTokenTypes.APPLICATION, "@", null);                        
            applicationNode.setFirstChild(dictionaryNode);
            
            RecordVar recordVar = recordType.getPrunedRecordVar();
            Map<FieldName, TypeExpr> hasFieldsMap = recordType.getHasFieldsMap();
            
            ParseTreeNode recordVarNode;
            if (!recordVar.isNoFields()) {
                //this is similar to the case where a type variable has been instantiated to another type variable.
                //the record variable must occur in the constrained polymorphic variables of the Overloading info
                //object or one of its ancestors.
                OverloadingInfo resolvingOverloadingInfo = overloadingInfo.getResolvingOverloadingInfo(recordVar);
                if (resolvingOverloadingInfo == null) {
                    //ambiguous type signature in inferred type                    
                    compiler.logMessage(new CompilerMessage(apInfo.getAppliedFunctionNode(), new MessageKind.Error.AmbiguousTypeSignatureInInferredType(inContextTypeExpr.toString())));
                }
                              
                TypeClass functionTypeClass = getFunctionTypeClass(typeClass, recordVar);
                   
                String dictVarName = resolvingOverloadingInfo.getDictionaryVariableName(recordVar, functionTypeClass); 
                
                if (functionTypeClass == typeClass) {
    
                    //no need for dictionary switching                     
                    recordVarNode = ParseTreeNode.makeQualifiedVarNode(currentModuleTypeInfo.getModuleName(), dictVarName, null);  
                    
                } else {
        
                    //add an argument for dictionary switching        
                    recordVarNode = getDictionarySwitchingNode (typeClass, functionTypeClass, dictVarName); 
                }
      
                if (hasFieldsMap.size() == 0) {
                    dictionaryNode.setNextSibling(recordVarNode);
                    
                    //if typeClass = Eq the dictionary node will be something like:
                    //dictEq#Record dictvarEq17                    
                    if (isSingleMethodRootClassUsingDefaultClassMethod) {
                        return patchApplicationNode(applicationNode);
                    }
                    return applicationNode;                            
                }
                
            } else {                              
                recordVarNode = null;
            }
            
            //if typeClass is Eq the dictionary will be
            //dictEq#Record {dictvarEq17 | fieldName1 = EqDictionaryForFieldName1, fieldName2 = EqDictionaryForFieldName2, ...}
                                     
            ParseTreeNode recordConstructorNode = new ParseTreeNode(CALTreeParserTokenTypes.RECORD_CONSTRUCTOR, "RECORD_CONSTRUCTOR");
            dictionaryNode.setNextSibling(recordConstructorNode);
            ParseTreeNode baseRecordNode = new ParseTreeNode(CALTreeParserTokenTypes.BASE_RECORD, "BASE_RECORD");
            recordConstructorNode.setFirstChild(baseRecordNode);
            baseRecordNode.setFirstChild(recordVarNode);
           
            ParseTreeNode fieldModificationListNode = new ParseTreeNode(CALTreeParserTokenTypes.FIELD_MODIFICATION_LIST, "FIELD_MODIFICATION_LIST");
            baseRecordNode.setNextSibling(fieldModificationListNode);
            
            Set<TypeClass> varConstraints; 
            if (classInstance == null) {
                //here we handle the case were a record is not itself a member
                //of the type class constraint on its fields.
                varConstraints = Collections.singleton(typeClass);
            } else {
                varConstraints = classInstance.getDeclaredPolymorphicVarConstraints().get(0);              
            }    
            
            for (final Map.Entry<FieldName, TypeExpr> entry : hasFieldsMap.entrySet()) {
                
                FieldName hasField = entry.getKey();
                TypeExpr hasFieldTypeExpr = entry.getValue();
                                
                ParseTreeNode fieldExtensionNode = new ParseTreeNode(CALTreeParserTokenTypes.FIELD_EXTENSION, "FIELD_EXTENSION");
                ParseTreeNode hasFieldNameNode;
                int nodeKind;
                if (hasField instanceof FieldName.Textual) {
                    nodeKind = CALTreeParserTokenTypes.VAR_ID;
                } else if (hasField instanceof FieldName.Ordinal){
                    nodeKind = CALTreeParserTokenTypes.ORDINAL_FIELD_NAME;
                } else {
                    throw new IllegalStateException();
                }
                hasFieldNameNode = new ParseTreeNode(nodeKind, hasField.getCalSourceForm());
                fieldExtensionNode.setFirstChild(hasFieldNameNode);
                
                //in most cases record instances have the simple form instance C r => C {r} where ... i.e. the class constraining the fields in a 
                //record is exactly the same as the class constraining the record. However, we do have instance ChartItem r => Chartable {r} and
                //there may be a potential for some other cases in the future where varConstraints has cardinality other than 1. 
                for (final TypeClass constrainingTypeClass : varConstraints) {
                                       
                    ParseTreeNode argumentDictionaryNode = 
                        getOverloadingArgument (hasFieldTypeExpr, constrainingTypeClass, overloadingInfo, apInfo);
                    fieldExtensionNode.addChild(argumentDictionaryNode);
                }        
                                                                                   
                fieldModificationListNode.addChild(fieldExtensionNode);                
            }
            
            if (isSingleMethodRootClassUsingDefaultClassMethod) {
                return patchApplicationNode(applicationNode);
            }
                        
            return applicationNode;
        
        } else if (inContextTypeExpr instanceof TypeVar) { 
                     
            TypeVar inContextTypeVar = (TypeVar) inContextTypeExpr;
            
            //if typeVar was instantiated to another TypeVar, then this other TypeVar must
            //occur in the constrained type variables of the OverloadingInfo object or one of its
            //ancestors. Otherwise, this is an ambiguous function declaration.
            //A simple example of this is:
            //read :: Read a => String -> a;
            //show :: Show a => a -> String;
            //readShow :: String -> String;
            //readShow s = show (read s);
            //If Boolean and Int are both instances of Read and Show, then it is unclear how to
            //resolve the overloading. The constraining type information is lost in the type
            //of readShow, and it is also not supplied by the application context (from which
            //one can only infer that a is in the classes Read and Show).        
    
            //the interesting point here is that the overloading at equals must be resolved by adding
            //a dictionary argument to equalsTest and not to result. This is why we must consider ancestors
            //as well.
            //equalsTest :: Eq a => a -> a -> Boolean;
            //equalsTest x y =   
            //  let
            //    result = equals x y;
            //  in
            //    result;      
    
            OverloadingInfo resolvingOverloadingInfo = overloadingInfo.getResolvingOverloadingInfo(inContextTypeVar);
            if (resolvingOverloadingInfo == null) {
                //ambiguous type signature in inferred type                    
                compiler.logMessage(new CompilerMessage(apInfo.getAppliedFunctionNode(), new MessageKind.Error.AmbiguousTypeSignatureInInferredType(inContextTypeExpr.toString())));
            }
                    
            TypeClass functionTypeClass = getFunctionTypeClass(typeClass, inContextTypeVar);
    
            String dictVarName = resolvingOverloadingInfo.getDictionaryVariableName(inContextTypeVar, functionTypeClass);
    
            if (functionTypeClass == typeClass) {
    
                //no need for dictionary switching                  
                return ParseTreeNode.makeQualifiedVarNode(currentModuleTypeInfo.getModuleName(), dictVarName, null);
    
            } else {
    
                //add an argument for dictionary switching   
                return getDictionarySwitchingNode (typeClass, functionTypeClass, dictVarName); 
            }
        }
        
        throw new UnsupportedOperationException("unexpected typeExpr for overload resolution");
    }
    
    /**
     * Returns an expression that takes a dictionary variable for the descendant type class and returns a dictionary of the
     * ancestor type class.
     * @param ancestorTypeClass
     * @param descendantTypeClass
     * @param dictVarName
     * @return ParseTreeNode
     */
    private ParseTreeNode getDictionarySwitchingNode(TypeClass ancestorTypeClass, TypeClass descendantTypeClass, String dictVarName) {
        
        //For each class A and subclass B there is conceptually a hidden function "$getA#B"
        //that gets the A dictionary from the B dictionary. This function takes an Int
        //and returns a dictionary (which is not a well-typed entity, but one that makes
        //sense to the evaluator). Suppose that A is the nth class in the traversal of
        //superclasses of B. Then: "$getA#B $dictvarB = $dictvarB n;" 
        
        //Since the dictionary switching function's implementation is so simple, and it is only ever 
        //used in a fully saturated form, we always inline this. Thus this function returns:
        //(@ dictVarName n).                           
        
        int index = descendantTypeClass.getAncestorClassIndex(ancestorTypeClass);
        if (index == -1) {
            throw new IllegalStateException();
        }
        
        ParseTreeNode dictionaryVarNode = ParseTreeNode.makeQualifiedVarNode(currentModuleTypeInfo.getModuleName(), dictVarName, null);       
        ParseTreeNode switchIndexNode = ParseTreeNode.makeIntLiteralNodeWithIntegerValue(index);        
        dictionaryVarNode.setNextSibling(switchIndexNode);
              
        ParseTreeNode applicationNode = new ParseTreeNode(CALTreeParserTokenTypes.APPLICATION, "@");
        applicationNode.setFirstChild(dictionaryVarNode);
        
        return applicationNode;
    }
    
    private static SortedSet<TypeClass> getTypeClassConstraintSet(PolymorphicVar polyVar) {
        //todoBI get rid of this when PolymorphicVar becomes an abstract base class of
        //TypeVar and RecordVar, and we can then make getTypeClassConstraintSet virtual
        //without making its scope public.
            
        if (polyVar instanceof TypeVar) {
            return ((TypeVar)polyVar).getTypeClassConstraintSet();                                        
        } else if (polyVar instanceof RecordVar){                
            return ((RecordVar)polyVar).getTypeClassConstraintSet();
        }  
        
        throw new UnsupportedOperationException();      
    }
}
