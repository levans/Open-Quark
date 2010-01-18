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
 * ApplicationInfo.java
 * Created: Jun 3, 2003
 * By: Bo Ilic
 */
package org.openquark.cal.compiler;

import java.util.Set;

/**
 * Information on an application occurring within the definition of the function whose definition
 * we are patching up to augment with a dictionary variable or dictionary literal value.
 * Creation date: (April 9, 2001)
 * @author Bo Ilic
 */
class ApplicationInfo {
    
    /** entity of the function (or class method) being applied. */
    private final FunctionalAgent functionalAgent;

    /** parse tree of the applied function */
    private final ParseTreeNode appliedFunctionNode;

    /**
     * (PolymorphicVar Set)
     * the set of generic, type-class constrained, and uninstantiated type or record variables that occur within
     * the type of the applied function ordered by their initial occurrence within the type. 
     */
    private Set<PolymorphicVar> genericClassConstrainedPolymorphicVars;

    /**
     * the type expression of the function that is being applied. The type variables
     * will be those used in establishing the type of the function that is being defined (the enclosing
     * function definition). 
     */
    private final TypeExpr typeExpr;
      
    /**
     * true if the function that is being applied has had its defining expression type checked, and all the defining
     * expressions of the functions in its declaration group type checked. More concretely, this means if:
     * let f1 x1 = e1; f2 x2 = e2; in e3, then this is true for f1 if e1 and e2 have been type checked. 
     * After this point, we know what the generic type and record variables in the type expression of the function are.
     */
    private final boolean polymorphic;

    /**
     * Constructs an ApplicationInfo object.
     *
     * @param functionalAgent entity of the applied function
     * @param appliedFunctionNode parse tree of the applied function     
     * @param typeExpr the type expression of the function that is being applied. The type variables
     *      will be those used in establishing the type of the function that is being defined (the enclosing
     *      function definition).
     * @param nonGenericVars the non-generic type variables at the point at which this application is being used.     
     * @param polymorphic true if the function that is being applied has had its defining expression type checked, and all the defining
     * expressions of the functions in its declaration group type checked. More concretely, this means if:
     * let f1 x1 = e1; f2 x2 = e2; in e3, then this is true for f1 if e1 and e2 have been type checked. 
     * After this point, we know what the generic type and record variables in the type expression of the function are.   
     */
    ApplicationInfo(FunctionalAgent functionalAgent, ParseTreeNode appliedFunctionNode, TypeExpr typeExpr, NonGenericVars nonGenericVars, boolean polymorphic) {

        if (functionalAgent == null || appliedFunctionNode == null || typeExpr == null) {
            throw new NullPointerException();
        }

        this.functionalAgent = functionalAgent;
        this.appliedFunctionNode = appliedFunctionNode;
        this.typeExpr = typeExpr;        
        this.polymorphic = polymorphic;         
        
        if (polymorphic) {
            finishedTypeCheckingFunction(nonGenericVars);
        }             
    }

    ParseTreeNode getAppliedFunctionNode() {
        return appliedFunctionNode;
    }

    /**
     * If the application function is "f" this can be called after the defining expressions of the declaration group involving f
     * have been type checked.
     * @param nonGenericVars 
     */
    void finishedTypeCheckingFunction(NonGenericVars nonGenericVars) {  
            
        if (genericClassConstrainedPolymorphicVars != null) {
            throw new IllegalStateException(); 
        }
                              
        genericClassConstrainedPolymorphicVars = typeExpr.getGenericClassConstrainedPolymorphicVars(nonGenericVars);                         
    }

    /**
     * Should not be called until after the top-level declaration group in which this application function is defined has been
     * type checked.
     * @return Set (PolymorphicVar Set i.e. each element either a TypeVar or RecordVar) the generic, type-class
     *     constrained type and record variables occurring within the type expression. These are the ones for which
     *     dictionary variables or dictionary constants must be added to the application in order to call the overloaded function.
     *     The set is ordered by first occurrence of the type variable within the type. 
     */
    Set<PolymorphicVar> getGenericClassConstrainedPolymorphicVars() {
  
        if (genericClassConstrainedPolymorphicVars == null) {
            throw new IllegalStateException();
        }
        return genericClassConstrainedPolymorphicVars;
    }
    
    /** 
     * the level at which the function is defined at. 0 for top level functions. 1 for functions in
     * a first level let definition etc.
     */    
    int getNestingLevel() {
        return functionalAgent.getNestingLevel();
    }

    boolean isPolymorphic() {
        return polymorphic;
    }

    /**
     * @return boolean true if the function being applied is actually a class method.
     */
    boolean isClassMethod() {
        return functionalAgent.getForm() == FunctionalAgent.Form.CLASS_METHOD;
    } 
    
    FunctionalAgent getAppliedFunctionalAgent() {
        return functionalAgent;
    }
}
