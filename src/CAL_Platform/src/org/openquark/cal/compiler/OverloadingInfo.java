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
 * OverloadingInfo.java
 * Creation date: (April 9, 2001)
 * By: Bo Ilic
 */
package org.openquark.cal.compiler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openquark.cal.module.Cal.Core.CAL_Record;

/**
 * This class is a helper class used during type checking. Its purpose is to store information needed
 * to add the extra dictionary arguments to functions and applications necessary to resolve overloading.
 *
 * Creation date: (4/9/01 4:35:56 PM)
 * @author Bo Ilic
 */
class OverloadingInfo {
    
    /** entity for the function whose defining equation is undergoing overload resolution. */
    private final Function function;

    /**      
     * the top-level or local function that is being defined. Extra dictionary arguments may
     * need to be added here.
     */
    private final ParseTreeNode functionNode;      
    
    /** 
     * (PolymorphicVar Set i.e. each element either a TypeVar or RecordVar)    
     * the generic, type-class constrained type and record variables occurring in the type expression of the function after it
     * has been type checked. Note that for a local function, this can be tricky. For example,
     * g x = let f y = x + y; in (f 3.0, f 5.0)
     * f cannot be used polymorphically in the "in" part. Replacing the call to f 5.0 by f (idInt 5)
     * results in a type checking error. So, in particular, the type for f for the purpose of
     * overload resolution is Double -> Double and not Num a => a -> a.
     * The basic idea is that overload resolution must add dictionary variables only for the *generic*
     * type-class constrained type and record variables.
     * The set is ordered by first occurrence of the type var in the type expression.
     */ 
    private Set<PolymorphicVar> genericClassConstrainedPolymorphicVars;
    
    private final TypeExpr functionTypeExpr;    

    /**      
     * (ApplicationInfo) information on applications of functions that occur within the definition of the
     * function given by functionNode. Extra arguments may need to be added at the application site to
     * call the operator of the application.
     */
    private final List<ApplicationInfo> applicationsList;
    
    /** 
     * OverloadingInfo object associated with the parent function of this OverloadingInfo object.
     * null if this is a top level function.
     */
    private final OverloadingInfo parent;
    
    /**
     * (PolymorphicVar -> TypeClass -> String) For example, for an (Eq a) type constraint on the type variable a,
     * the name of the dictionary variable will be something like $dictvar14Eq.
     */
    private Map<PolymorphicVar, Map<TypeClass, String>> dictionaryVariablesMap;
    
    /**
     * OverloadingInfo constructor comment.
     * @param function
     * @param functionNode 
     * @param functionTypeExpr    
     * @param parent
     */
    OverloadingInfo(Function function, ParseTreeNode functionNode, TypeExpr functionTypeExpr, OverloadingInfo parent) {
    
        if (function == null || functionTypeExpr == null) {
            throw new NullPointerException();
        }
   
        switch (functionNode.getType()) {
            case CALTreeParserTokenTypes.TOP_LEVEL_FUNCTION_DEFN :
            case CALTreeParserTokenTypes.LET_DEFN :
                break;

            default :
                throw new IllegalArgumentException("functionNode has an invalid node type.");
        }

        this.function = function;       
        this.functionNode = functionNode;
        this.functionTypeExpr = functionTypeExpr;      
        this.parent = parent;

        applicationsList = new ArrayList<ApplicationInfo>();
    }
    
    /**
     * Add the application info if it may be needed in the future for overloading resolution.
     * Creation date: (4/9/01 4:59:40 PM)
     * @param apInfo
     */
    void addApplication(ApplicationInfo apInfo) {

        //there is no need to add applications for overload resolution if we know for sure that
        //it will not be needed.
        if (apInfo.isPolymorphic()) {
            
            if (apInfo.getGenericClassConstrainedPolymorphicVars().isEmpty() &&
                // the overloading resolver must take action for the uses of the dictionary function
                !apInfo.getAppliedFunctionalAgent().getName().equals(CAL_Record.Functions.dictionary)) {
                    return;
            }
             
            applicationsList.add(apInfo);
            return;
        }
         
        //add the application to the overloading info object having the same nesting level.
        int apNestingLevel = apInfo.getNestingLevel();
        OverloadingInfo oi = this;
        while (oi.function.getNestingLevel() > apNestingLevel) {
            oi = oi.getParent();
        }
        oi.applicationsList.add(apInfo);
       
    }
    
    /**
     * Call this method when type checking for this function is finished, and then
     * constrainedTypeVarList needs to be calculated.
     * Creation date: (4/19/01 10:01:38 AM)
     * @param nonGenericVars the non-generic variables at the point where the body of
     *       all functions in the function's declaration group have been type checked.
     *       Can be null to indicate an empty list.
     */
    void finishedTypeCheckingFunction(NonGenericVars nonGenericVars) {
        genericClassConstrainedPolymorphicVars = functionTypeExpr.getGenericClassConstrainedPolymorphicVars(nonGenericVars); 
                
        for (int i = 0, nApplications = getNApplications(); i < nApplications; ++i) {
            ApplicationInfo ai = getApplication(i);
            if (!ai.isPolymorphic()) {            
                ai.finishedTypeCheckingFunction(nonGenericVars);
            }          
        }        
    }
         
    
    /**
     * Insert the method's description here.
     * Creation date: (4/9/01 5:01:43 PM)
     * @return OverloadingInfo.ApplicationInfo
     * @param index
     */
    ApplicationInfo getApplication(int index) {
        return applicationsList.get(index);
    }
    
    /** 
     * Note that finishedTypeCheckingFunction must be called before this method is called.
     * @return Set the generic, class constrained type or record variables occurring within the type expression. These are the ones for which
     *     dictionary variables constants must be added to the function's definition in order to resolve overloading. 
     */
    Set<PolymorphicVar> getGenericClassConstrainedPolymorphicVars() {
        //finishedTypeCheckingFunction must be called before calling this method is valid.
        if (genericClassConstrainedPolymorphicVars == null) {
            throw new IllegalStateException();
        }
        
        return genericClassConstrainedPolymorphicVars;
    }
    
    /**
     * Insert the method's description here.
     * Creation date: (5/31/01 11:48:20 AM)
     * @return ParseTreeNode
     */
    ParseTreeNode getFunctionArgsNode() {

        switch (functionNode.getType()) {
            case CALTreeParserTokenTypes.LET_DEFN :
                return functionNode.getChild(2);
            case CALTreeParserTokenTypes.TOP_LEVEL_FUNCTION_DEFN :
                return functionNode.getChild(3);
            default :
                throw new IllegalStateException("Programming error in OverloadingInfo.getFunctionArgsNode.");
        }
    }
    
    /**
     * Insert the method's description here.
     * Creation date: (5/31/01 12:15:51 PM)
     * @return String
     */
    String getFunctionName() {
        switch (functionNode.getType()) {
            case CALTreeParserTokenTypes.LET_DEFN :
                return functionNode.getChild(1).getText();
            case CALTreeParserTokenTypes.TOP_LEVEL_FUNCTION_DEFN :
                return functionNode.getChild(2).getText();
            default :
                throw new IllegalStateException("Programming error in OverloadingInfo.getFunctionName.");
        }
    }
    
    /**   
     * @return ParseTreeNode the top-level or local function that is being defined.
     */
    ParseTreeNode getFunctionNode() {
        return functionNode;
    }
    
    /**    
     * @return TypeExpr
     */
    TypeExpr getFunctionTypeExpr() {
        return functionTypeExpr;
    }
    
    /**     
     * @return int number of applications that may need overload resolution occurring in the definition of this function.
     *      Note: all these applications will be such that the operator is a simple function rather than an application itself.
     */
    int getNApplications() {
        return applicationsList.size();
    }       
    
    /**    
     * @return OverloadingInfo object associated with the parent function of this OverloadingInfo object.
     *      null if this is a top level function.
     */       
    OverloadingInfo getParent() {
        return parent;
    }
    
    void setDictionaryVariablesMap(Map<PolymorphicVar, Map<TypeClass, String>> dictionaryVariablesMap) {
        this.dictionaryVariablesMap = dictionaryVariablesMap;
    }
    
    String getDictionaryVariableName(PolymorphicVar polymorphicVar, TypeClass typeClass) {        
        return dictionaryVariablesMap.get(polymorphicVar).get(typeClass);              
    }
    
    /**
     * @param polymorphicVar
     * @return OverloadingInfo the dictionary variable needed to satisfy the polymorphicVar constraint or null
     *      if there is not any (which implies an ambiguous type). 
     */    
    OverloadingInfo getResolvingOverloadingInfo(PolymorphicVar polymorphicVar) {
        
        if (genericClassConstrainedPolymorphicVars.contains(polymorphicVar)) {
            return this;
        }
        
        if (parent == null) {
            return null;
        }
        
        return parent.getResolvingOverloadingInfo(polymorphicVar);
    }
    
    @Override
    public String toString() {
        return getFunctionName();
    }
    
}