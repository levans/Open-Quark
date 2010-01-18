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
 * AutoburnLogic.java
 * Creation date: Dec 20, 2002
 * By: Ken Wong
 */
package org.openquark.gems.client;

import java.util.ArrayList;
import java.util.List;

import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.compiler.TypeChecker.TypeCheckInfo;
import org.openquark.cal.services.GemEntity;


/**
 * A class that holds all of the autoburn logic that the tabletop uses.
 * This was refactored out from the original version in the TableTop.
 * @author Ken Wong
 * Creation Date: Dec 20th 2002
 */
public class AutoburnLogic {
    
    /** The maximum burn depth which will be considered.
     *  If there are n possible pieces which can be considered for burning, a burn combination will be considered if
     *  the number of burns is (<= MAX_BURN_DEPTH) or (>= (n - MAX_BURN_DEPTH)) */
    public static final int MAX_BURN_DEPTH = 2;

    
    /**
     * Autoburn action enum pattern.
     * Creation date: (12/04/01 12:00:43 PM)
     * @author Edward Lam
     */
    static final class AutoburnAction {
        static final AutoburnAction NOTHING = new AutoburnAction(); // last autoburn: nothing happened
        static final AutoburnAction BURNED = new AutoburnAction();  // last autoburn: burned
        static final AutoburnAction MULTIPLE = new AutoburnAction();// last autoburn: nothing happened because ambiguous
        static final AutoburnAction IMPOSSIBLE = new AutoburnAction();// last autoburn: nothing happened because not possible
        static final AutoburnAction UNBURNED = new AutoburnAction();// last autoburn: unburned
        
        /**
         * Constructor for an autoburn action
         * Creation date: (03/07/2002 6:09:00 PM)
         */
        private AutoburnAction() {
        }
    }
        
    /**
     * Burn status enum pattern.
     * Creation date: (04/01/2002 11:56:43 AM)
     * @author Edward Lam
     */
    static final class BurnStatus {
        private final String typeString;

        private BurnStatus(String s) {
            typeString = s;
        }
        @Override
        public String toString() {
            return typeString;
        }

        /** Not burnt. */
        public static final BurnStatus NOT_BURNT = new BurnStatus("NOT_BURNT");
        
        /** Automatically burnt. */
        public static final BurnStatus AUTOMATICALLY_BURNT = new BurnStatus("AUTOMATICALLY_BURNT");
        
        /** Manually burnt. */
        public static final BurnStatus MANUALLY_BURNT = new BurnStatus("MANUALLY_BURNT");
    }

    /**
     * Enum pattern used to summarize how a gem can be connected to another gem, allowing for the
     * possibility of autoburning.
     * @author Edward Lam
     */
    public static final class AutoburnUnifyStatus {
        private final String typeString;

        private AutoburnUnifyStatus(String s) {
            typeString = s;
        }
        @Override
        public String toString() {
            return typeString;
        }

        /**
         * Returns true if this unify status indicates that two gems are always connectable.
         * Always connectable means that the autoburn logic will be able to connect the gems without
         * requiring manual user intervention. This is true when autoburning is either unambiguous or
         * not necessary and also in the case where burning is ambiguous but the connection can be made
         * without burning. In that last case the burn logic would simply make the connection without burning.
         * @return true if gems are connectable automatically, false otherwise
         */
        public boolean isAutoConnectable() {
            return (this == UNAMBIGUOUS ||
                    this == UNAMBIGUOUS_NOT_NECESSARY ||
                    this == AMBIGUOUS_NOT_NECESSARY ||
                    this == NOT_NECESSARY);
        }
        
        /**
         * Returns true if this unify status indicates that the possible autoburn combinations are unambiguous.
         * This means there is only one possible combination that can be burned and therefore autoburning is
         * possible without requiring user interaction.
         * @return true if unambiguous autoburning is possible, false otherwise
         */
        public boolean isUnambiguous() {
            return (this == UNAMBIGUOUS ||
                    this == UNAMBIGUOUS_NOT_NECESSARY);
        }
        
        /**
         * Returns true if this unify status indicates that two gems are connectable without burning.
         * @return true if gems can be connected without burning, false otherwise
         */
        public boolean isConnectableWithoutBurning() {
            return (this != AMBIGUOUS &&
                    this != UNAMBIGUOUS &&
                    this != NOT_POSSIBLE);
        }

        /**
         * Returns true if this unify status indicates that two gems are connectable using burning.
         * @return true if gems can be connected using burning, false otherwise
         */
        public boolean isConnectableWithBurning() {
            return (this != NOT_NECESSARY &&
                    this != NOT_POSSIBLE);
        }

        /** Unifies without burning and there are is no way to unify with burning. */
        public static final AutoburnUnifyStatus NOT_NECESSARY = new AutoburnUnifyStatus("NOT_NECESSARY");
        
        /** Unification cannot be enabled by burning and is not possible without burning. */
        public static final AutoburnUnifyStatus NOT_POSSIBLE = new AutoburnUnifyStatus("NOT_POSSIBLE");
        
        /** It's ambiguous which inputs to burn to unify and it is impossible to unify without burning. */
        public static final AutoburnUnifyStatus AMBIGUOUS = new AutoburnUnifyStatus("AMBIGUOUS");
        
        /** It's unambiguous which inputs to burn to unify and it is impossible to unify without burning. */
        public static final AutoburnUnifyStatus UNAMBIGUOUS = new AutoburnUnifyStatus("UNAMBIGUOUS");
        
        /** It's ambiguous which inputs to burn to unify and you can also unify without burning. */
        public static final AutoburnUnifyStatus AMBIGUOUS_NOT_NECESSARY = new AutoburnUnifyStatus("AMBIGUOUS_NOT_NECESSARY");
        
        /** It's umambiguous which inputs to burn to unify and you can also unify without burning. */
        public static final AutoburnUnifyStatus UNAMBIGUOUS_NOT_NECESSARY = new AutoburnUnifyStatus("UNAMBIGUOUS_NOT_NECESSARY");
    }

    /**
     * A helper class to hold information about a single burn combination.
     * This holds information about which inputs to burn as well as the type closeness for this burning combination.
     * Note that the arguments are represented as integer indices relative to the arguments of the gem and not the gem tree.
     * @author Richard Webster
     */
    public static class BurnCombination {
        /** An array of indexes of the arguments to burn. */
        private final int[] inputsToBurn;

        /** The type closeness associated with this burning combination. */
        private final int burnTypeCloseness;

        /**
         * BurnCombination constructor.
         */
        BurnCombination(int[] inputsToBurn, int burnTypeCloseness) {
            this.inputsToBurn = inputsToBurn;
            this.burnTypeCloseness = burnTypeCloseness;
        }

        /**
         * Returns the type closeness of this burning combination.
         */
        public int getBurnTypeCloseness() {
            return burnTypeCloseness;
        }

        /**
         * Returns the argument indexes to burn.
         * Each int is the index of an arg to burn with respect to the gem.
         */
        public int[] getInputsToBurn() {
            return inputsToBurn;
        }
    }

    /**
     * A helper class to hold the status of a query as to whether a particular connection between gems can be made,
     * including the possibility of various sorts of burning.
     * @author Bo Ilic
     */    
    public static class AutoburnInfo {
        
        private final transient AutoburnUnifyStatus status;
        
        /**
         * Each element of this list is a BurnCombinations which contains information about
         * which arguments should be burnt and the type closeness. 
         */
        private final List<BurnCombination> burnCombinations;
        
        /** 
         * every possible configuration of burnings (e.g. for a gem with 2 arguments, can burn none of the arguments,
         * the first argument only, the second argument only, or both arguments) has an associated type closeness for its
         * unification with the destination type. This is the maximum value and represents the "best possible" connection.
         */
        private final int maxTypeCloseness;
        
        /**
         * the type closeness when no burning is attempted.
         */
        private final int noBurnTypeCloseness;
        
        private AutoburnInfo (AutoburnUnifyStatus status, List<BurnCombination> burnCombinations, int maxTypeCloseness, int noBurnTypeCloseness) {
            
            if (status == null) {
                throw new NullPointerException(); 
            }
            
            if (status == AutoburnLogic.AutoburnUnifyStatus.NOT_POSSIBLE) {
                if (burnCombinations != null || maxTypeCloseness != -1) {
                    throw new IllegalArgumentException();
                }
            } else if (status == AutoburnLogic.AutoburnUnifyStatus.NOT_NECESSARY) {
                if (burnCombinations.size() != 0) {
                    throw new IllegalArgumentException();
                }
            } else {
                if (burnCombinations.size() < 1) {
                    throw new IllegalArgumentException();
                }
            }
            
            //todoBI unfortunately, the case where *no* arguments are burnt is not represented as the empty burn combination.
            //i.e. the possibilities for burning a 2 argument gem could be:
            //[[], [0], [1], [0,1]] but instead are listed as [[0], [1], [0,1]]
            //if this were so however, the status value could be calculated from burnCombinations.
            //As it is, we can't distinguish e.g. [[], [0]] from [[0]].

            this.status = status;
            this.burnCombinations = burnCombinations;
            this.maxTypeCloseness = maxTypeCloseness; 
            this.noBurnTypeCloseness = noBurnTypeCloseness;
        }
        
        static private AutoburnInfo makeNoUnificationPossibleAutoburnInfo() {
            return new AutoburnInfo(AutoburnUnifyStatus.NOT_POSSIBLE, null, -1, -1);
        }
        
        /**
         * @return a summary of the type of unifications possible (including the possibility of burning).
         */
        public AutoburnUnifyStatus getAutoburnUnifyStatus() {
            return status;
        }

        /**
         * @return each element of this list is a BurnCombination which indicate which arguments should be burnt.
         */
        public List<BurnCombination> getBurnCombinations() {
            return burnCombinations;
        }

        /**
         * @return every possible configuration of burnings (e.g. for a gem with 2 arguments, can burn none of the arguments,
         *   the first argument only, the second argument only, or both arguments) has an associated type closeness for its
         *   unification with the destination type. This is the maximum value and represents the "best possible" connection.
         */
        public int getMaxTypeCloseness() {
            return maxTypeCloseness;
        }
        
        /**         
         * @return the type closeness for the case where no burning occurs. This is used by clients to see if the no burning
         *   situation is the most "natural" and we shouldn't bother presenting autoburn possibilities.
         */
        public int getNoBurnTypeCloseness() {
            return noBurnTypeCloseness;
        }
    }
    
    /**
     * Return whether autoburning will result in a unification.
     * @param destType The destination type to unify with
     * @param burnEntity on which to attempt "autoburning"   
     * @param info the info to use
     * @return AutoburnLogic.AutoburnInfo autoburnable result
     */
    public static AutoburnInfo getAutoburnInfo(TypeExpr destType, GemEntity burnEntity, TypeCheckInfo info) {
    
        // see if we have to do anything
        TypeExpr entityType;
        if (burnEntity == null || (entityType = burnEntity.getTypeExpr()) == null) {
            return AutoburnInfo.makeNoUnificationPossibleAutoburnInfo();
        }
        
        return getAutoburnInfoWorker(destType, entityType.getTypePieces(), null, info, null);
    }

    /**
     * Return whether autoburning will result in a unification.
     * @param destType The destination type to unify with
     * @param sourcePieces the type pieces of the entity on which to attempt "autoburning"   
     * @param info the info to use
     * @return AutoburnLogic.AutoburnInfo autoburnable result
     */
    public static AutoburnInfo getAutoburnInfo(TypeExpr destType, TypeExpr[] sourcePieces, TypeCheckInfo info) {
    
        // see if we have to do anything
        if (sourcePieces == null || sourcePieces.length == 0) {
            return AutoburnInfo.makeNoUnificationPossibleAutoburnInfo();
        }
        
        return getAutoburnInfoWorker(destType, sourcePieces, null, info, null);
    }
    
    /**
     * Return whether autoburning will result in a unification.
     * Only inputs on the given gem will be considered for burning.
     * @param destType The destination type to unify with.
     * @param gem The gem on which to attempt "autoburning".
     * @param info the typeCheck info to use.
     * @return AutoburnLogic.AutoburnInfo autoburnable result.
     */
    public static AutoburnInfo getAutoburnInfo(TypeExpr destType, Gem gem, TypeCheckInfo info) {
        // see if we got a real type
        if (destType == null) {
            return AutoburnInfo.makeNoUnificationPossibleAutoburnInfo();
        }
        
        int[] burnableArgIndices;
        List<TypeExpr> sourceTypeList = new ArrayList<TypeExpr>();  // input types, then output type.
        
        TypeExpr outType = (gem instanceof CollectorGem) ? ((CollectorGem)gem).getCollectingPart().getType() : gem.getOutputPart().getType();
        TypeExpr[] outTypePieces = outType.getTypePieces();
        
        // A collector gem's collecting part is not burnable.
        if (gem instanceof CollectorGem && !((CollectorGem)gem).isConnected()) {
            burnableArgIndices = new int[0];

        } else {
            // declare the burnt arg indices array
            burnableArgIndices = new int[gem.getNInputs()];

            if (burnableArgIndices.length != 0) {
                // get the unbound input parts of the gem and keep track of which ones are burnable
                int unburnedCount = 0;
                int burnedCount = 0;
                for (int i = 0, n = gem.getNInputs(); i < n; i++) {
                    Gem.PartInput input = gem.getInputPart(i);
                    
                    if (!input.isConnected()) {
                        if (input.isBurnt()) {
                            sourceTypeList.add(outTypePieces[burnedCount]);
                            burnedCount++;
                        } else {
                            sourceTypeList.add(input.getType());
                            burnableArgIndices[unburnedCount] = sourceTypeList.size() - 1;
                            unburnedCount++;
                        }
                    }
                }
                
                // Calculate what the output type would be if none of the arguments were burned.
                TypeExpr newOutType = outTypePieces[outTypePieces.length-1];
                for (int i = outTypePieces.length-2; i >= burnedCount; i--) {
                    newOutType = TypeExpr.makeFunType(outTypePieces[i], newOutType);
                }
                outType = newOutType;

                // Assign to a new trimmed down array
                int[] newIndices = new int[unburnedCount];
                System.arraycopy(burnableArgIndices, 0, newIndices, 0, unburnedCount);
                burnableArgIndices = newIndices;
            }
        }
        
        // Add the output type to the source types.
        sourceTypeList.add(outType);
        
        return getAutoburnInfoWorker(destType, sourceTypeList.toArray(new TypeExpr[0]), burnableArgIndices, info, gem);
    }
    
    /**
     * Translate from an array of indices of the burnable inputs to an array of indices of all inputs to the gem
     * @param unboundArgsToBurn An array of indices relative to the burnable (ie unburned, unbound) inputs of the gem
     * @param sourceGem The gem that these are the inputs of
     * @return An array of indices of inputs relative to ALL inputs of the gem, corresponding to the contents of unburnedArgsToBurn.
     */
    private static int[] translateBurnCombo (int[] unboundArgsToBurn, Gem sourceGem) {
        int[] argsToBurn = new int[unboundArgsToBurn.length];
        
        // loop indices:
        // i - index of the current input
        // j - counter to keep track of how many unbound and unburned inputs we have passed
        // k - index of the next element of unboudnArgsToBurn to check
        for (int i = 0, j = 0, k = 0; i < sourceGem.getNInputs() && k < unboundArgsToBurn.length; i++) {
            Gem.PartInput currentInput = sourceGem.getInputPart(i);
            
            if (currentInput.isConnected() || currentInput.isBurnt()) {
                // Skip over unburnable inputs
                continue;
            }
            
            // This is the j'th burnable input
            
            // Check to see if this argument should be burned, and if so, add its index relative to the list of all inputs
            // to the argsToBurn array
            if (unboundArgsToBurn[k] == j) {
                argsToBurn[k] = i;
                k++;
            }
            j++;
        }
        return argsToBurn;
    }
    
    /**
     * Create the first burn combination of size numBurns.
     * Note: We assume that numBurns <= numBurnable.
     * @return An array of burn positions from zero up to (numBurns-1).
     */
    private static int[] getFirstCombination(int numBurns) {
        int[] burnArray = new int[numBurns];
        for (int i = 0; i < numBurns; i++) {
            burnArray[i] = i;        
        }
        return burnArray;
    }
    
    /**
     * Works along with getFirstCombination to iterate over all burn combinations of a fixed size.
     * For example, the following code: <br>
     * <code>
     *   boolean isValidCombo = true;<br>
     *   for(int[] array = getFirstCombination(3); isValidCombo; isValidCombo = getNextCombination(array, 5)) {...}
     * </code><br>
     * would genereate the following sequence: <br>
     * <ol>
     *   <li>[0, 1, 2] 
     *   <li>[0, 1, 3] 
     *   <li>[0, 1, 4] 
     *   <li>[0, 2, 3] 
     *   <li>[0, 2, 4] 
     *   <li>[0, 3, 4] 
     *   <li>[1, 2, 3] 
     *   <li>[1, 2, 4] 
     *   <li>[1, 3, 4] 
     *   <li>[2, 3, 4]
     * </ol> 
     * 
     * For fixed values of numBurnable and numBurns, this algorithm will iterate over
     * (numBurnable CHOOSE numBurns) arrays. 
     *  
     * @param burnArray An array of burn positions representing the current burn combination. 
     *        Upon return this array will have the next burn combiantion stored in it if possible.
     * @param numBurnable The number of burnable inputs.
     * @return true if the iteration succeeded, false if there are no more valid combinations 
     */
    private static boolean getNextCombination(int[] burnArray, int numBurnable){
        if (burnArray.length == 0) {
            // There is only one combination for a burn size of zero: []
            return false; 
        }
        
        // The algorithm we use is analagous to a counting algorithm in a numbering system with radix 
        // numBurnable where each digit is represented by an element of burnArray, with the added restriction
        // that the digits always form a strictly increasing sequence. Hence our strategy is to iterate over
        // the possible combinations in "numerical" order. 
        
        // We start by incrmenting burnArray[n-1] (ie. the rightmost digit) and then checking to see if 
        // burnArray[n-1] is still less than numBurnable.
        // If so, we have a valid combination. If not, we continue on to burnArray[n-2] (the next least significant digit)
        // and try again. Note that for i < (n-1), it must actually be the case that burnArray[i] < numBurnable-((n-i),
        // since the (n-1)-i elements that follow burnArray[i] must be strictly increasing and all less than numBurnable.
        final int n = burnArray.length-1;
        
        int i = n;
        while (i >= 0) {       
            burnArray[i]++;
            if (burnArray[i] >= (numBurnable - (n - i))) {
                i--;
            } else {
                break;
            }
        }
        
        // At this point, i is the first index for which the value can be safely incremented.
        
        // If i < 0, this means we have already counted all the valid combinations, so we are done.
        if (i < 0) {
            return false; 
        }       
        
        // Otherwise we make set the elements in positions [i+1..n] to be        
        // the smallest possible increasing sequence.
        for (; i < n; i++) {
            burnArray[i+1] = burnArray[i] + 1;            
        }      
        
        return true;
    }
    
    /**
     * Note that the types of combinations considered is subject to MAX_BURN_DEPTH.
     * 
     * TODOEL: it seems like it would be better if all of the input types in source type pieces were burnable, and it were up to the
     *   caller to figure out how the resulting indices mapped onto its arguments.
     * @param destType for example, if connecting to the first argument of the map gem, this would be a -> b
     * @param sourceTypePieces a list of the relevant source type pieces (ie inputs and output). 
     *      This includes the burnable and already burnt inputs in proper order, as well as the output piece (which should appear at the end).
     *      It does NOT include any inputs that are already bound.
     *      For example, in the following case:
     * 
     *                   Int --\
     *                        | \
     *         bound ----------  \
     *                        |  |- a
     *     Boolean (burned)  >-  /
     *                        | /
     *                Double --/
     *      
     *     This would be [Int, Boolean, Double, a]
     *          
     * @param unburnedArguments This is an array of indices into sourceTypePieces that should be considered for burning sites.
     *      This can be null, in which case *every* argument is interpreted as a possible burn site i.e. equivalent to 
     *      a value of [0, 1, 2, ..., sourceTypePieces.length - 1]. 
     *      For example, if the take gem were the source, and it had no arguments burnt and none bound, this would be [0, 1].
     *      If its 0th argument were burnt this would be [1]. If both arguments were burnt, this would be [].
     *      Note that since these are indices into sourceTypePieces, if one or more of the arguments are bound, they are not counted.
     *      So, for example, if the first argument of take was bound to another gem and the second one was unburned, this would be [0].
     *      In the situation illustrated above, this would be [0, 2].
     * @param info the typeCheck info to use
     * @param sourceGem the source gem, if one exists. Should be null if otherwise.
     */
    private static AutoburnInfo getAutoburnInfoWorker(TypeExpr destType, TypeExpr[] sourceTypePieces, int[] unburnedArguments, TypeCheckInfo info, Gem sourceGem) {
        
        // Possible improvements:
        //   Check matchability of the nth argument burn to the nth dest type piece.
        //   Use the fact that some clients do not want to know all burn combos in the ambiguous case.
        //   Use type piece identity (eg. source type pieces which are the same type var..).  Hash for gem types already tried.
        //   Group types when calculating (eg. if try burning one Double, trying to burn the other double will give the same result).
        //   Specialize as burns are applied (eg. makeTransform is Typeable a => a -> a.  If one a is specialized, so is the other).

        // initialize our return type
        AutoburnUnifyStatus unificationStatus = AutoburnUnifyStatus.NOT_POSSIBLE;
   
        int numUnburned;
        if (unburnedArguments == null) {
            //all positions are burnable
            numUnburned = sourceTypePieces.length - 1;
            unburnedArguments = new int[numUnburned];
            for (int i = 0; i < numUnburned; i++) {
                unburnedArguments[i] = i;
            }
        } else {
            //only some positions are burnable. This corresponds to a gem graph where some arguments have been already burnt
            //by the user, or where some of the arguments do not correspond to arguments in the root gem.
            numUnburned =  unburnedArguments.length;
        }
        
        TypeExpr outputType = sourceTypePieces[sourceTypePieces.length - 1];        
        
        boolean doNotTryBurning = false;
        if (!destType.isFunctionType()) {
            //Don't try burning if the destination type isn't a function.  
            
            //This is a not a logically required restriction. For example, any burnt gem can be connected to the Prelude.id gem.
            //A less trivial example is that Prelude.sin with argument burnt can be connected to the Prelude.typeOf gem. This is because
            //Double -> Double can unify with Typeable a => a.
            //However, it is not a situation that the end user would "likely" want to see in the list and we found that it results in
            //many rare situations.            
            
            doNotTryBurning = true;
        } else {
                          
            //If the destination result type is a type constructor, perform a basic check against the source result type.
            
            //The fact used here:
            //a. for 2 types to unify, their result types must unify.
            //b. burning doesn't change the result type (in the technical sense of CAL, and not in the sense of the output type displayed
            //   as the result of a gem in the GemCutter. For example, burning the first argument of take gem changes the gem to
            //   in the GemCutter to display as a 1 argument gem with output type Int -> [a], but the overall type of the burnt take gem is
            //   is [a] -> (Int -> [a]) which is just [a] -> Int -> [a] (-> is right associative) and has result type [a], as with the unburnt
            //   take gem.           
            
            TypeExpr destResultTypeExpr = destType.getResultType();
            if (destResultTypeExpr.rootTypeVar() == null && !TypeExpr.canUnifyType(destResultTypeExpr, outputType.getResultType(), info.getModuleTypeInfo())) {
                //only do the check if the destination result type is a type constructor or record type.
                //the reason for this is that if it is a parameteric type, we are likely to succeed here...
                doNotTryBurning = true;
            }
        }
        
        
        int maxTypeCloseness = -1;
        
        //the type closeness with no burning
        int noBurnTypeCloseness = Integer.MIN_VALUE;

        // The lower bound on the number of burns in the upper range.
        int upperRangeMinBurns =  Math.max(numUnburned - MAX_BURN_DEPTH, MAX_BURN_DEPTH+1);

        // List of all burn combinations for which unification can take place.
        List<BurnCombination> burnCombos = new ArrayList<BurnCombination>();
        
        // Our combination generator will create all combinations of a fixed size.
        // So, we iterate over all possible sizes.
        // However, we skip over sizes between MAX_BURN_DEPTH and upperRangeMinBurns
        // so that our algorithm runs in polynomial ( specifically O(n^MAX_BURN_DEPTH) ) time
        // rather than exponential ( O(2^n) ).
        for (int numBurns = 0; numBurns <= numUnburned; numBurns++) {
            if (doNotTryBurning && numBurns > 0) {
                break;
            }
            
            // If necessary, skip from MAX_BURN_DEPTH to upperRangeMinBurns.
            if (numBurns == MAX_BURN_DEPTH + 1) {
                numBurns = upperRangeMinBurns;
            }
            
            // Iterate through all the combinations of burning inputs of size numBurns.
            boolean isValidCombo = true;
            for (int[] burnComboArray = getFirstCombination(numBurns); isValidCombo; isValidCombo = getNextCombination(burnComboArray, numUnburned)) {
                
                // calculate what the corresponding output type would be.
                // Note that we are assuming that the elements of burnComboArray are in
                // ascending order.
                int currentSourceTypePiece = sourceTypePieces.length - 2;
                int nextUnburnedArg = numUnburned - 1;
                int nextArgToBurn = numBurns - 1;

                // Loop over currentSourceTypePieces.
                // These represent all the burned and unburned arguments.
                
                TypeExpr newOutputType = outputType;
                while (currentSourceTypePiece >= 0) {
                    boolean shouldBurn = false;
                    if (nextUnburnedArg >= 0 && currentSourceTypePiece == unburnedArguments[nextUnburnedArg]) {
                        // This is an unburned argument.
                        if (nextArgToBurn >= 0 && nextUnburnedArg == burnComboArray[nextArgToBurn]) {
                            // This is a burnable argument that we want to try burning
                            shouldBurn = true;
                            nextArgToBurn--;
                        }
                        nextUnburnedArg--;
                    } else { 
                        // This is a burned argument. 
                        shouldBurn = true;
                    }
                    if (shouldBurn) {
                        // We need to add the corresponding type to the output type
                        TypeExpr argType = sourceTypePieces[currentSourceTypePiece];
                        newOutputType = TypeExpr.makeFunType(argType, newOutputType);
                    }
                    currentSourceTypePiece--;
                }
    
                // Would the resulting output type unify with the type we want?
                int typeCloseness = TypeExpr.getTypeCloseness(destType, newOutputType, info.getModuleTypeInfo());
                if (numBurns == 0) {
                    assert (noBurnTypeCloseness == Integer.MIN_VALUE);
                    noBurnTypeCloseness = typeCloseness;
                }
                
                if (typeCloseness >= 0) {
                    if (typeCloseness > maxTypeCloseness) {
                        maxTypeCloseness = typeCloseness;
                    }
    
                    boolean autoburnable = false;
                    if (numBurns == 0) {
    
                        // We didn't have to autoburn.
                        unificationStatus = AutoburnUnifyStatus.NOT_NECESSARY;
    
                    } else if (unificationStatus == AutoburnUnifyStatus.NOT_POSSIBLE) {
    
                        // We have our first valid combo and it is not possible to unify without burning.
                        autoburnable = true;
                        unificationStatus = AutoburnUnifyStatus.UNAMBIGUOUS;
    
                    } else if (unificationStatus == AutoburnUnifyStatus.NOT_NECESSARY) {
                        
                        // We have our first valid combo and it is possible to unify without burning.
                        autoburnable = true;
                        unificationStatus = AutoburnUnifyStatus.UNAMBIGUOUS_NOT_NECESSARY;
                        
                    } else {
                        // We got > 1 valid combos.
                        autoburnable = true;
                        
                        if (unificationStatus == AutoburnUnifyStatus.UNAMBIGUOUS) {
                            unificationStatus = AutoburnUnifyStatus.AMBIGUOUS;
                            
                        } else if (unificationStatus == AutoburnUnifyStatus.UNAMBIGUOUS_NOT_NECESSARY) {
                            unificationStatus = AutoburnUnifyStatus.AMBIGUOUS_NOT_NECESSARY;
                        }
                    }
    
                    // If unify can take place, copy the array and store it in burn combos.
                    if (autoburnable) {
                        int[] autoburnableCombo;
                        if (sourceGem != null) {
                            autoburnableCombo = translateBurnCombo(burnComboArray, sourceGem);
                        } else {
                            autoburnableCombo = new int[numBurns];
                            System.arraycopy(burnComboArray, 0, autoburnableCombo, 0, numBurns);                            
                        }
                                                
                        burnCombos.add(new BurnCombination(autoburnableCombo, typeCloseness));
                    }
                }
            }
        }

        if (unificationStatus == AutoburnUnifyStatus.NOT_POSSIBLE) {
            burnCombos = null;
        }        

        return new AutoburnInfo(unificationStatus, burnCombos, maxTypeCloseness, noBurnTypeCloseness);

    }
}
