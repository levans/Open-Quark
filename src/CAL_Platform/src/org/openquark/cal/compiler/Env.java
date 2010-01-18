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
 * Env.java
 * Created: July 17th 2000
 * By: Bo Ilic
 */
package org.openquark.cal.compiler;

import java.util.HashSet;
import java.util.Set;

/**
 * A class for environments which associates identifiers to type expressions. Note that Env is 
 * implemented as a persistent linked list. For example, after a call to extend, we have full access to both
 * the original environment and the extended one as well.
 * All entities within the environment should be defined within the same module.
 *
 * Creation date: (7/17/00 4:15:14 PM)
 * @author Bo Ilic
 */
final class Env {

    /** the entity at the head of this Env. */
    private FunctionalAgent headEntity;

    /** the rest of the environment. */
    private Env tail;
    
    /**
     * Construct an environment from an existing environment and a new head entity
     * Creation date: (7/21/00 2:33:25 PM)
     * @param headEntity the entity at the head of this Env
     * @param tail the original environment (to be augmented)
     */
    private Env(FunctionalAgent headEntity, Env tail) {

        if (headEntity == null) {
            throw new IllegalArgumentException("Env constructor: headEntity must not be null.");
        }

        if (tail != null && !headEntity.getName().getModuleName().equals(tail.headEntity.getName().getModuleName())) {
            throw new IllegalArgumentException("Env constructor: all entities in the environment must be in the same module.");
        }

        this.headEntity = headEntity;
        this.tail = tail;
    }
    
    /**
     * Extend an environment with an identifier-type pair, specifying non-default type.
     *
     * Creation date: (7/17/00 4:21:26 PM)
     * @return Env
     * @param existingEnv the Env to extend. May be null.
     * @param envEntity
     */
    static Env extend(Env existingEnv, FunctionalAgent envEntity) {

        if (existingEnv == null) {
            return new Env(envEntity, null);
        }

        return existingEnv.extend(envEntity);
    }
    
    /**
     * Extend an environment with an identifier-type pair, specifying non-default type.
     *
     * Creation date: (7/17/00 4:21:26 PM)
     * @return Env
     * @param envEntity
     */
    Env extend(FunctionalAgent envEntity) {

        return new Env(envEntity, this);
    }
    
    /**
     * Indicates that all entities in the Env have had their defining expressions typechecked.
     * Creation date: (4/9/01 4:18:48 PM)
     */
    void finishedTypeChecking() {
      
        headEntity.setTypeCheckingDone();   

        if (tail != null) {
            tail.finishedTypeChecking();
        }
    }
    
    /**
     * Indicates that the topmost nEnties entities in the Env have had their defining expressions typechecked.
     * Creation date: (4/9/01 4:18:48 PM)
     * @param nEntities number of entities to mark as finished
     */
    void finishedTypeChecking(int nEntities) {

        if (nEntities <= 0) {
            return;
        }
        
        headEntity.setTypeCheckingDone();       

        if (tail != null) {
            tail.finishedTypeChecking(nEntities - 1);
        }
    }
    
    /**
     * Returns true if the name is in this environment.
     *
     * Creation date: (9/5/00 11:21:52 AM)
     * @return boolean whether this identifier was found
     * @param name the unqualified name to search for
     */
    boolean isIdentifier(String name) {

        for (Env env = this; env != null; env = env.tail) {
            if (name.equals(env.headEntity.getName().getUnqualifiedName())) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Returns a set consisting of the distinct identifiers in the environment. 
     * Creation date: (2/5/01 11:47:18 AM)
     * @return Set
     */
    Set<String> makeSet() {

        Set<String> set = new HashSet<String>();

        Env env = this;
        do {
            set.add(env.headEntity.getName().getUnqualifiedName());
            env = env.tail;
        } while (env != null);

        return set;
    }
    
    /**
     * Search for an entity in an environment. The entity must be defined in the same module
     * as the module for the environment. Returns null if the entity is not found.
     *
     * Creation date: (6/4/01 3:47:15 PM)
     * @return FunctionalAgent
     * @param entityName unqualified name of the entity
     */
    FunctionalAgent retrieveEntity(String entityName) {

        for (Env env = this; env != null; env = env.tail) {
            if (entityName.equals(env.headEntity.getName().getUnqualifiedName())) {

                return env.headEntity;
            }
        }

        //Entity not found
        return null;
    }
    
    /**
     * Writes out a string representation of the environment.
     * Creation date: (1/23/01 12:10:57 PM)
     * @return String
     */
    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        if (tail != null) {
            sb.append(tail.toString());
        }
        sb.append(headEntity.toString());
        sb.append("\n");

        return sb.toString();
    }
    
    /**
     * Forwards the elements of the environment to the packager. This should only be done after
     * all elements of the environment have been fully type checked.
     * Creation date: (6/12/01 11:48:51 AM)
     * @param moduleTypeInfo
     */
    void wrap(ModuleTypeInfo moduleTypeInfo) {

        for (Env env = this; env != null; env = env.tail) {
            
            //internal functions such as the derived instance function $equalsMaybe should
            //not be added to the ModuleTypeInfo
            FunctionalAgent headEntity = env.headEntity;
            if (headEntity instanceof Function && 
                headEntity.getName().getUnqualifiedName().charAt(0) != '$') {                
                moduleTypeInfo.addFunction((Function)headEntity);
            }
        }
    }
}