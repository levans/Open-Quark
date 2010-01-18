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
 * LocalFunctionIdentifierGenerator.java
 * Creation date: (Mar 10, 2006)
 * By: James Wright
 */
package org.openquark.cal.compiler;

import java.util.HashMap;
import java.util.Map;

/**
 * A helper class for generating new LocalFunctionIdentifiers.  It does the
 * bookkeeping of tracking the current index associated with a given local function
 * name.
 * 
 * This class is cloneable, so that subclasses of BindingTrackingSourceModelTraverser
 * can clone a private copy to use for predicting the identifiers that will be assigned
 * to local functions by the superclass. 
 * 
 * @author James Wright
 */
final class LocalFunctionIdentifierGenerator {

    private static final Integer ZERO = Integer.valueOf(0);
    
    /** Name of the currently in-scope toplevel function, or null if none. */
    private String currentFunction;
    
    private final Map<String, Integer> localFunctionCounterMap = new HashMap<String, Integer>();
    
    LocalFunctionIdentifierGenerator() {
        currentFunction = null;
    }
    
    /** Internal constructor for use by clone() */
    private LocalFunctionIdentifierGenerator(String currentFunction, Map<String, Integer> localFunctionCounterMap) {
        if(localFunctionCounterMap == null) {
            throw new NullPointerException();
        }
        this.currentFunction = currentFunction;
        this.localFunctionCounterMap.putAll(localFunctionCounterMap);
    }
    
    /**
     * Reset the counters for local function names and set the current toplevel function name
     * to currentFunction.
     * @param currentFunction String name of the new current toplevel function.
     */
    void reset(String currentFunction) {
        this.currentFunction = currentFunction;
        localFunctionCounterMap.clear();
    }
    
    /**
     * Generate a new LocalFunctionIdentifier in the current toplevel function.
     * @param moduleName String name of the current module
     * @param localFunctionName String name of the local function to generate an identifier for
     * @return the new LocalFunctionIdentifier
     */
    LocalFunctionIdentifier generateLocalFunctionIdentifier(ModuleName moduleName, String localFunctionName) {
        Integer nextIndexObj = localFunctionCounterMap.get(localFunctionName);
        if(nextIndexObj == null) {
            nextIndexObj = ZERO;
        }
        int nextIndex = nextIndexObj.intValue();
        
        localFunctionCounterMap.put(localFunctionName, Integer.valueOf(nextIndex + 1));
        return new LocalFunctionIdentifier(QualifiedName.make(moduleName, currentFunction), localFunctionName, nextIndex);
    }
    
    /** @return name of the current toplevel function */
    String getCurrentFunction() {
        return currentFunction;
    }
    
    /** 
     * @return A new LocalFunctionIdentifierGenerator whose internal counters match this instance's current state.
     *          Operations on the new generator won't affect the state of this generator.
     */
    @Override
    public LocalFunctionIdentifierGenerator clone() {
        return new LocalFunctionIdentifierGenerator(currentFunction, localFunctionCounterMap);
    }
}
