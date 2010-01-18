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
 * UnorderedTypeMatchFilter.java
 * Creation date: Jan 28, 2003.
 * By: Edward Lam
 */
package org.openquark.cal.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.Bag;
import org.apache.commons.collections.bag.HashBag;
import org.openquark.cal.compiler.ModuleTypeInfo;
import org.openquark.cal.compiler.TypeExpr;


/**
 * A filter that matches against a subset of the gem's input types, and optionally against a range of output types.
 * <p>
 * 
 * Where a gem's type is t1 -> t2 -> ... -> tn
 * <ul>
 *   <li>the input types are t1, t2, ..., t(n-1)
 *   <li>the output type is tn
 * </ul>
 * 
 * A gem passes through the filter if
 * <ol>
 *   <li>the gem has input types that match the filter's input types, in any order.
 *       The gem may have other input types which are not matched.
 *   <li>the gem's output type matches one of the filter's output types.
 * </ol>
 * 
 * For example,
 * <dl>
 * <dd>
 *   If this is instantiated with inputs types: {a, a}
 * <br>
 *                            and output types: {Num b => b, [c]}
 * </dl>
 *   Gems that would pass through this filter would be any gem with a pair of identical inputs,
 *     and an output that was either a Num or a list.
 * <p>
 * Caveat:
 * One must be careful when constructing this filter that one maintains the desired referential semantics.
 * <p>
 * For example:
 * <ul>
 *   <li>If one desires to filter on gem type "Num a => a -> a", this is accomplished by passing in the same reference to the
 *         type "Num a => a" as both the input and the output type.
 *   <li>If separate instances of the type "Num a => a" were passed in as the arguments, the resulting filter would filter
 *         on the type "(Num a, Num b) => a -> b".
 *   <li>The type "Int -> Double" would not pass through the filter in the first case, but it would in the second.
 * </ul>
 * 
 * @author Edward Lam
 */
public class UnorderedTypeMatchFilter extends GemFilter {

    private static final TypeExpr[] EMPTY_TYPE_ARRAY = new TypeExpr[0];

    private final TypeExpr[] filterInputTypes;
    private final TypeExpr[] filterOutputTypes;
    private final ModuleTypeInfo currentModuleTypeInfo;
    private final boolean selectMoreGeneralized;

    /**
     * A trivial helper class two hold two arrays of TypeExpr.
     * Warning: these arrays are not immutable.
     * @author Edward Lam
     */
    private static class TypeArraysPair {

        private final TypeExpr[] array1;
        private final TypeExpr[] array2;

        private TypeArraysPair(TypeExpr[] array1, TypeExpr[] array2) {
            this.array1 = array1;
            this.array2 = array2;
        }
        
        TypeExpr[] getArray1() {
            return array1;
        }
        
        TypeExpr[] getArray2() {
            return array2;
        }
    }

    /**
     * Constructor for a UnorderedTypeMatchFilter.
     * 
     * @param inputTypes the input types to match, or null if any (non-functional) types match.
     * @param outputTypes the output types to match, or null if any (non-functional) types match.
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
    public UnorderedTypeMatchFilter(TypeExpr[] inputTypes, TypeExpr[] outputTypes, ModuleTypeInfo currentModuleTypeInfo, boolean selectMoreGeneralized) {
        
        if (inputTypes == null) {
            inputTypes = new TypeExpr[0];
        }
        if (outputTypes == null) {
            outputTypes = new TypeExpr[] {TypeExpr.makeParametricType()};
        }

        for (final TypeExpr element : outputTypes) {
            if (element.getArity() != 0) {
                throw new IllegalArgumentException("Output type must not be functional.");
            }
        }

        // Copy the type expressions, since they are not immutable.
        TypeArraysPair copiedTypeArrays = copyTypeArrays(inputTypes, outputTypes);
        this.filterInputTypes = copiedTypeArrays.getArray1();
        this.filterOutputTypes = copiedTypeArrays.getArray2();
        
        this.currentModuleTypeInfo = currentModuleTypeInfo;
        this.selectMoreGeneralized = selectMoreGeneralized;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean select(GemEntity gemEntity) {

        TypeExpr gemType = gemEntity.getTypeExpr();

        // no need to check if the gem doesn't have enough inputs.
        if (gemType.getArity() < filterInputTypes.length) {
            return false;
        }
        
        // Instantiate members for the other select()
        TypeExpr[] typePieces = gemType.getTypePieces();
        TypeExpr outputType = typePieces[typePieces.length - 1];

        Bag remainingInputTypes = new HashBag(Arrays.asList(typePieces));
        remainingInputTypes.remove(outputType, 1);

        return select(new ArrayList<TypeExpr>(), remainingInputTypes, outputType);
    }
    
    /**
     * Returns true if any of the filter output types matches with the outputTypeToTry. This is a necessary condition
     * for the filter to match a type.
     * @param outputTypeToTry
     * @return boolean
     */
    private boolean anyFilterOutputTypesMatch(TypeExpr outputTypeToTry) {

        for (final TypeExpr filterOutputType : filterOutputTypes) {
                
            if (selectMoreGeneralized) {
                if (TypeExpr.canUnifyType (outputTypeToTry, filterOutputType, currentModuleTypeInfo)) {
                    return true;
                }
            } else {
                if (TypeExpr.canPatternMatch (outputTypeToTry, filterOutputType, currentModuleTypeInfo)) {
                    return true;
                }
            }
        }
            
        return false;
    }

    /**
     * Helper method for the other select().
     * 
     * What happens:
     *   We initially call this method with an empty list, with all the input types in the remaining input types set.
     *   We build up the input types to try by iterating over the remaining input types, and for each iteration
     *     calling this method recursively with the remaining inputs.
     *   When we get to the point where the number of input types to try is the same as the number of input types in the
     *     filter, we carry out our test.
     * 
     * @param inputTypesToTry the list of type expr to attempt to match against the input types in this filter.
     *   The nth type in this list will be matched against the nth type in the filter.
     * @param remainingInputTypes the type expr which are unmatched.
     * @param outputTypeToTry the output type to match against the types in this filter
     * @return boolean
     */
    private boolean select(List<TypeExpr> inputTypesToTry, Bag remainingInputTypes, TypeExpr outputTypeToTry) {

        int numInputsToTry = inputTypesToTry.size();

        // If this is the first call to this method, check that any output types match.
        // If not, we don't need to waste our time with the recursive calls.
        // Note: it will be more efficient to thread through the set of filter output types which match on to successive calls,
        //   and have the test for filter output types only test for types which pass through here, since we perform all the
        //   necessary calculation here.
        if (numInputsToTry == 0) {
            
            if (!anyFilterOutputTypesMatch(outputTypeToTry)) {
                return false;
            }
        }

        // Now check if we should call ourselves recursively..
        if (numInputsToTry < filterInputTypes.length) {

            // Create a set that represents the remaining input types that will be passed down to the next level.
            Bag remainingInputTypesArg = new HashBag(remainingInputTypes);

            for (Iterator it = remainingInputTypes.iterator(); it.hasNext(); ) {

                TypeExpr nextType = (TypeExpr)it.next();

                inputTypesToTry.add(nextType);
                remainingInputTypesArg.remove(nextType, 1);

                // recursive call.
                if (select(inputTypesToTry, remainingInputTypesArg, outputTypeToTry)) {
                    return true;
                }

                inputTypesToTry.remove(numInputsToTry);
                remainingInputTypesArg.add(nextType);
            }

            // none of the matching succeeded.
            return false;

        } else {

            // We have enough inputs to try.
            // Create a list/array of types to try: [inputType1, inputType2, ..., inputTypeN, outputType]
            List<TypeExpr> typesToTryList = new ArrayList<TypeExpr>(inputTypesToTry);
            typesToTryList.add(outputTypeToTry);
            TypeExpr[] typesToTryArray = typesToTryList.toArray(EMPTY_TYPE_ARRAY);
            
            // Note: here we are performing the matching/unifying all the inputs with the output for each filter output type.

            // Iterate over the filter's output types.

            for (final TypeExpr element : filterOutputTypes) {
                
                List<TypeExpr> filterTypesList = new ArrayList<TypeExpr> (Arrays.asList(filterInputTypes));
                filterTypesList.add(element);
                TypeExpr[] filterTypesArray = filterTypesList.toArray(EMPTY_TYPE_ARRAY);
                
                if (selectMoreGeneralized) {
                    if (TypeExpr.canUnifyTypePieces(typesToTryArray, filterTypesArray, currentModuleTypeInfo)) {
                        return true;
                    }
                } else {
                    if (TypeExpr.canPatternMatchPieces(typesToTryArray, filterTypesArray, currentModuleTypeInfo)) {
                        return true;
                    }
                }
            }

            // Doesn't pass matching/unifying for any of the filter output types.
            return false;
        }
    }

    /**
     * A helper method to copy two arrays of TypeExpr.
     * @param typesArray1 the first array to copy.
     * @param typesArray2 the second array to copy.
     * @return TypeArraysPair the copied arrays.  TypeExpr among these arrays maintain referential equality exhibited in the arguments.
     */
    private static TypeArraysPair copyTypeArrays(TypeExpr[] typesArray1, TypeExpr[] typesArray2) {

        // Create one unified array.
        TypeExpr[] allTypesArray = new TypeExpr[typesArray1.length + typesArray2.length];
        System.arraycopy(typesArray1, 0, allTypesArray, 0, typesArray1.length);
        System.arraycopy(typesArray2, 0, allTypesArray, typesArray1.length, typesArray2.length);

        // Copy the array
        TypeExpr[] allTypesArrayCopy = TypeExpr.copyTypeExprs(allTypesArray);

        // Break up the copied array into two smaller arrays
        TypeExpr[] returnArray1 = new TypeExpr[typesArray1.length];
        System.arraycopy(allTypesArrayCopy, 0, returnArray1, 0, returnArray1.length);

        TypeExpr[] returnArray2 = new TypeExpr[typesArray2.length];
        System.arraycopy(allTypesArrayCopy, returnArray1.length, returnArray2, 0, returnArray2.length);
        
        // Return the two arrays.
        return new TypeArraysPair(returnArray1, returnArray2);
    }
}
