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
 * PolymorphicVarContext.java
 * Created: Mar 19, 2004
 * By: Bo Ilic
 */
package org.openquark.cal.compiler;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * A helper class intended to hold the uninstantiated type and record variables occurring in a 
 * collection of (one or more) type expressions. This is so that they can toString
 * properly, with equal type and record variables having the same string representation.
 * 
 * @author Bo Ilic
 */
public class PolymorphicVarContext {
    
    /**
     * (PolymorphicVar -> Integer) map from uninstantiated type or record variables to their index of occurrence in
     * a type (or list of types), which we thread through toString() invocations to keep track of what text should be 
     * assigned to the record or type variable in its string representation. 
     * Keys are ordered by index.
     */
    private final Map<PolymorphicVar, Integer> polymorphicVarToIndexMap;
    
    /**
     * if true, then the preferred names of type are record variables will be used if possible. The definition of 'if possible'
     * is subject to change so this is more of a preference to display prettier type strings in client code.
     */
    private final boolean favorPreferredNames;
          
    /**
     * (String Set) of preferred names. Distinct type or record variables may have the same preferred name, which makes
     * using the preferred name for both impossible.
     */
    private final Set<String> preferredNames;
    
    private PolymorphicVarContext(boolean favorPreferredNames) {
        
        this.favorPreferredNames = favorPreferredNames;              
        this.polymorphicVarToIndexMap = new LinkedHashMap<PolymorphicVar, Integer>();
        
        if (favorPreferredNames) {
            this.preferredNames = new HashSet<String>();
        } else {
            this.preferredNames = null;
        }
        
    }
    
    static public PolymorphicVarContext make() {
        return new PolymorphicVarContext(false);
    }
    
    /**  
     * WARNING- DO NOT MAKE PUBLIC. The reason for this is that toString-ing type expressions cannot be done consistently
     * with 'favorPreferredName = true' in an incremental fashion. That is why there are no public TypeExpr.toString methods
     * that take both a PolymorphicVarContext and a favorPreferredNames argument.
     *   
     * @param favorPreferredNames if true, then the preferred names of type are record variables will be used if possible. The definition of 'if possible'
     *      is subject to change so this is more of a preference to display prettier type strings in client code.
     * @return a new PolymorphicVarContext
     */
    static PolymorphicVarContext make(boolean favorPreferredNames) {
        return new PolymorphicVarContext(favorPreferredNames);
    }    
      
    /**
     * Adds a type or record variable to this PolymorphicVarContext, if it has not already been added.
     * 
     * Note that the type or record variable must be uninstantiated otherwise an IllegalArgumentException is thrown.
     * 
     * @param polymorphicVar
     */    
    void addPolymorphicVar(PolymorphicVar polymorphicVar) {
        
        if (polymorphicVar == null) {
            throw new NullPointerException("argument 'polymorphicVar' cannot be null");
        }
        
        //todoBI the type casting can be cleaned up when we move PolymorphicVar to an internal compiler package.
        //we cannot expose e.g. getInstance as a public method.        
        if (polymorphicVar instanceof TypeVar) {
            TypeVar typeVar = (TypeVar)polymorphicVar;
            //should only accept uninstantiated TypeVars.
            if (typeVar.getInstance() != null) {            
                throw new IllegalArgumentException("cannot add an instantiated type variable");
            }           
        } else if (polymorphicVar instanceof RecordVar) {
            
            RecordVar recordVar = (RecordVar)polymorphicVar;
            
            if (recordVar.getInstance() != null || recordVar.isNoFields()) {
                throw new IllegalArgumentException("cannot add an instantiated record variable");
            }           
        } else {
            throw new IllegalArgumentException("unexpected PolymorphicVar subtype");
        }
        
        Object indexObject = polymorphicVarToIndexMap.get(polymorphicVar);
        
        if (indexObject == null) {
            
            int index = polymorphicVarToIndexMap.size() + 1;
            polymorphicVarToIndexMap.put(polymorphicVar, Integer.valueOf(index));
            
            String preferredName = polymorphicVar.getPreferredName();
            if (favorPreferredNames && preferredName != null) {
                preferredNames.add(preferredName);
            }
        }                
    }

    /**
     * Maps the ints 1, 2, 3,... to a, b, c, ..., 
     * @param index must be >= 1 to not generate an IllegalArgumentException.
     * @return name to use for the variable
     */
    static String indexToVarName(int index) {
        
        if (index <= 0) {
            throw new IllegalArgumentException("argument 'index' must be positive");
        }
    
        if (index > 0 && index <= 26) {
            return String.valueOf((char) ('a' + index - 1));
        }
    
        return "a" + String.valueOf(index);
    }
    
    private int getPolymorphicVarIndex (PolymorphicVar polymorphicVar) {
        return polymorphicVarToIndexMap.get(polymorphicVar).intValue();
    }
    
    /**
     * Gets the name of the polymorphic var. This takes into account naming preferences represent by the 
     * PolymorphicVarContext object.
     * @param polymorphicVar
     * @return name to use for this particular polymorphic var in the context of all the other polymorphic vars
     */
    String getPolymorphicVarName(PolymorphicVar polymorphicVar) {
               
        
        if (usePreferredName()) {
            if (!polymorphicVarToIndexMap.containsKey(polymorphicVar)) {
                throw new IllegalArgumentException("polymorphicVar is not in this PolymorphicVarContext");
            }
            return polymorphicVar.getPreferredName();
        }
        
        
        return indexToVarName(getPolymorphicVarIndex(polymorphicVar));
    }
    
    private boolean usePreferredName() {
        //we only use preferred names if 
        //a) we are in favorPreferredNames mode
        //b) every polymorphic var has a preferred name, and distinct polymorphic vars
        //have distinct preferred names. 
        //This is a very conservative policy.
        return favorPreferredNames && polymorphicVarToIndexMap.size() == preferredNames.size();
    }
    
    /**
     * The set of constrained uninstantiated record and type variables determined by this PolymorphicVarContext,
     * ordered by their index. This is used as a helper function for toString'ing type expressions.
     * 
     * Note: a constrained record variable is one that has lacks constraints or has type class constraints.     
     * A constrained type variable is one that has type class constraints.
     * 
     * @return Set (Object Set, where each Object is either a TypeVar or RecordVar) set of the constrained
     *    polymorphic variables in this context, ordered by their index. 
     */
    Set<PolymorphicVar> getConstrainedPolymorphicVars () {
        
        Set<PolymorphicVar> constrainedPolymorphicVars = new LinkedHashSet<PolymorphicVar>(); //Set of PolymorphicVars   
       
        for (final PolymorphicVar polymorphicVar : polymorphicVarToIndexMap.keySet()) {
                                   
            if (polymorphicVar instanceof TypeVar) {
                
                TypeVar typeVar = (TypeVar)polymorphicVar;
            
                if (!typeVar.noClassConstraints()) {
                    constrainedPolymorphicVars.add(typeVar);                                            
                }  
                
            } else if (polymorphicVar instanceof RecordVar) {
                
                RecordVar recordVar = (RecordVar)polymorphicVar;
                
                if (!recordVar.noClassConstraints() ||
                    !recordVar.isNoFields() && !recordVar.getLacksFieldsSet().isEmpty()) { 
                    
                    constrainedPolymorphicVars.add(recordVar);                    
                }     
            }  else {
                throw new IllegalStateException("Unexpected PolymorphicVar subtype");
            }
        }
                                                       
        return constrainedPolymorphicVars;
    }
    
    /**     
     * @param polymorphicVar
     * @return true if polymorphicVar is contained in this PolymorphicVarContext
     */
    boolean containsPolymorphicVar(PolymorphicVar polymorphicVar) {
        return polymorphicVarToIndexMap.containsKey(polymorphicVar);
    }
    
    /**    
     * @return if true, then the preferred names of type are record variables will be used if possible. 
     * The definition of 'if possible' is subject to change so this is more of a preference to display
     * prettier type strings in client code.
     */
    boolean getFavorPreferredNames() {
        return favorPreferredNames;
    }
    
    @Override
    public String toString() {
        
        StringBuilder sb = new StringBuilder("[");
        
        for (Iterator<PolymorphicVar> it = polymorphicVarToIndexMap.keySet().iterator(); it.hasNext(); ) {
                                    
            PolymorphicVar polyVar = it.next();
            
            sb.append(indexToVarName(getPolymorphicVarIndex(polyVar)));
            
            if (usePreferredName()) {                
                sb.append(" = ");
                sb.append(getPolymorphicVarName(polyVar));
            }
                                    
            if (it.hasNext()) {
                sb.append(", ");
            }               
        }
                       
        sb.append("]");
        
        return sb.toString();
    }
}
