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
 * SingleApplicationTypeFilter.java
 * Creation date: Jan 22, 2003.
 * By: Edward Lam
 */
package org.openquark.cal.services;

import org.openquark.cal.compiler.ModuleTypeInfo;
import org.openquark.cal.compiler.TypeExpr;

/**
 * A filter that filters on the gem's type expression.
 * A gem passes through the filter if
 *   1) its type is of the form "x -> y", (where x could equal y, and both x and y do not themselves represent
 *      functional types (ie. are not themselves of the form "x -> y"))
 *   2) x and y are at least as general or as specific (optionally) as given input and output types.
 * 
 * Caveat:
 * One must be careful when constructing this filter that one maintains the desired referential semantics.
 *   eg. If one desires to filter on gem type "Num a => a -> a", this is accomplished by passing in the same reference to the
 *         type "Num a => a" as both the input and the output type.
 *       If separate instances of the type "Num a => a" were passed in as the arguments, the resulting filter would filter
 *         on the type "(Num a, Num b) => a -> b".
 *       The type "Int -> Double" would not pass through the filter in the first case, but it would in the second.
 * 
 * @author Edward Lam
 */
public class SingleApplicationTypeFilter extends GemFilter {

    private final TypeExpr inputType;
    private final TypeExpr outputType;
    private final ModuleTypeInfo currentModuleTypeInfo;
    private final boolean selectMoreGeneralized;

    /**
     * Constructor for a SingleApplicationTypeFilter.
     * 
     * @param inputType the input type to match, or null if any (non-functional) types match.
     * @param outputType the output type to match, or null if any (non-functional) types match.
     * @param currentModuleTypeInfo the current module.
     * @param selectMoreGeneralized in the case where type variables are involved, whether a gem should be accepted if
     *   its types are -at most- or -at least- as specialized as the filter types (true == at most).
     * 
     *   For example, for the gem Prelude.id (type: a -> a), if we pass in the arguments (Num a => a, null):
     *     if true, the filter would select any gem whose input type could be specialized to Num a => a
     *        this means that Prelude.id would be returned.
     *     if false, the filter would select any gem whose input type was at least as specialized as Num a => a
     *        this means that Prelude.id would not be returned
     */
    public SingleApplicationTypeFilter(TypeExpr inputType, TypeExpr outputType, ModuleTypeInfo currentModuleTypeInfo, boolean selectMoreGeneralized) {
        
        if (inputType == null) {
            inputType = TypeExpr.makeParametricType();
        }
        if (outputType == null) {
            outputType = TypeExpr.makeParametricType();
        }

        if (inputType.getArity() != 0 || outputType.getArity() != 0) {
            throw new IllegalArgumentException("Input and output types must not be functional.");
        }

        // Copy the type expressions, since they are not immutable..
        TypeExpr[] typeCopies = TypeExpr.copyTypeExprs(new TypeExpr[]{inputType, outputType});
        this.inputType = typeCopies[0];
        this.outputType = typeCopies[1];
        
        this.currentModuleTypeInfo = currentModuleTypeInfo;
        this.selectMoreGeneralized = selectMoreGeneralized;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean select(GemEntity gemEntity) {

         TypeExpr gemType = gemEntity.getTypeExpr();
         if (gemType.getArity() != 1) {
             return false;
         }
                
         TypeExpr[] filterTypePieces = new TypeExpr[]{inputType, outputType};
         
         TypeExpr[] gemTypePieces = gemType.getTypePieces();
        
         // See if unification/pattern matching works.
         if (selectMoreGeneralized) {
             return TypeExpr.canUnifyTypePieces(gemTypePieces, filterTypePieces, currentModuleTypeInfo);
         } else {
             return TypeExpr.canPatternMatchPieces(gemTypePieces, filterTypePieces, currentModuleTypeInfo);
            
         }
                 
    }

}
