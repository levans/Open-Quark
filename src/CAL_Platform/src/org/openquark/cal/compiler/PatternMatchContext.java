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
 * PatternMatchContext.java
 * Created: Apr 16, 2004
 * By: Bo Ilic
 */
package org.openquark.cal.compiler;

/**
 * Context information passed while doing pattern matching.
 * 
 * We need to prevent type and record variables in the declared type from being altered. Otherwise
 * declarations such as the following will not report a pattern matching error.
 * funnyHead :: [a] -> Prelude.Char;
 * public funnyHead = List.head;
 *
 * Another situation where this can occur is if the declared and inferred type share
 * type variables from the beginning.
 * For example, patternMatch((a, b) -> b, (a', b') -> a') should fail because 
 * we instantiate a' to a, b' to b and then need to try to instantiate a to b to finish
 * the pattern matching. But the instantiation of a alters the declared type, and so
 * must be disallowed.          
 * 
 * @author Bo Ilic
 */
final class PatternMatchContext {
    
    /**
     * Used to hold the type and record variables in the declared type. These variables
     * should not be instantiated during pattern matching, since otherwise the declared
     * type could be altered!
     */
    private final PolymorphicVarContext polymorphicVarContext;
    
    /** list of TypeVar's that cannot be specialized, except to a type not-involving typeVars. **/
    private final NonGenericVars nonGenericVars;
    
    private final ModuleTypeInfo moduleTypeInfo;

    PatternMatchContext(TypeExpr declaredTypeExpr, NonGenericVars nonGenericVars, ModuleTypeInfo moduleTypeInfo) {
        if (declaredTypeExpr == null || moduleTypeInfo == null) {
            throw new NullPointerException();
        }
        
        this.polymorphicVarContext = declaredTypeExpr.indexPolymorphicVars(null);
        this.nonGenericVars = nonGenericVars;
        this.moduleTypeInfo = moduleTypeInfo;
    }

    NonGenericVars getNonGenericVars() {
        return nonGenericVars;
    }

    ModuleTypeInfo getModuleTypeInfo() {
        return moduleTypeInfo;
    }
    
    boolean isUninstantiableTypeVar(TypeVar typeVar) {
        return polymorphicVarContext.containsPolymorphicVar(typeVar);
    }
    
    boolean isUninstantiableRecordVar(RecordVar recordVar) {
        return polymorphicVarContext.containsPolymorphicVar(recordVar);
    }    
}
