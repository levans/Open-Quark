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
 * DisplayedValueGem.java
 * Creation Date: Jan 28, 2003
 * By: Ken Wong
 */
package org.openquark.gems.client;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JOptionPane;

import org.openquark.cal.compiler.FieldName;
import org.openquark.cal.compiler.ModuleTypeInfo;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.gems.client.AutoburnLogic.AutoburnInfo;
import org.openquark.gems.client.AutoburnLogic.AutoburnUnifyStatus;
import org.openquark.gems.client.Gem.PartInput;
import org.openquark.gems.client.utilities.ExtendedUndoableEditSupport;


/**
 * This class handles all the 'burning' associated tasks in the TableTop
 * @author Ken Wong
 * Creation Date: Jan 28th 2003
 */
class TableTopBurnManager {
    
    private final TableTop tableTop;
    
    /** the set of inputs which are manually burnt.  Any other burnt inputs are autoburnt. */
    private final transient Set<PartInput> manuallyBurntInputs;
    
    /*
     * Autoburn state
     */

    /** the last gem on which we tried autoburn */
    private transient Gem autoBurnLastGem;

    /** the last TypeExpr with which we tried to autoburn it */
    private transient TypeExpr autoBurnLastType;

    /** the last result we got from autoburn */
    private transient AutoburnLogic.AutoburnAction autoBurnLastResult = AutoburnLogic.AutoburnAction.NOTHING;
    

    /**
     * Constructor for a TableTopBurnManager.
     * @param tableTop
     */
    TableTopBurnManager(TableTop tableTop) {
        this.tableTop = tableTop;

        manuallyBurntInputs = new HashSet<PartInput>();
    }

    /**
     * Returns the AutoburnAction object that describes the results of the last
     * autoburn attempt
     * @return AutoburnLogic.AutoburnAction
     */
    AutoburnLogic.AutoburnAction getAutoburnLastResult() {
        return autoBurnLastResult;
    }

    /**
     * Returns the last gem that was autoburned
     * @return gem
     */
    Gem getAutoburnLastGem() {
        return autoBurnLastGem;
    }

    /**
     * Returns the type of the last autoburn attempt
     * @return TypeExpr
     */
    TypeExpr getAutoburnLastType() {
        return autoBurnLastType;
    }
    /**
     * Invalidates the existing autoburn state
     */
    void invalidateAutoburnState() {
        autoBurnLastGem = null;
        autoBurnLastType = null;
        autoBurnLastResult = AutoburnLogic.AutoburnAction.NOTHING;
    }
    
    /**
     * Handle the user gesture to autoburn a gem (or undo it).  
     * Autoburning will check possible combinations of burning the given gem's inputs to 
     * see if there is a single possibility (combination of inputs burned) that will unify 
     * with a specific output type.  If there is, then these inputs will be burned.
     * Undoing an autoburn will unburn anything automatically burned (but not manually burned).
     * 
     * @param thisGem Gem the gem whose state to change
     * @param typeToUnify TypeExpr the type with which to unify the output
     * @param burn whether to autoburn the gem as applied, or to undo the autoburn.
     * @return AutoburnAction the action we performed: NOTHING, BURNED, MULTIPLE, IMPOSSIBLE or UNBURNED.
     * AUTOBURN_MULTIPLE means we didn't do anything because there was more than one burn possibility.
     * AUTOBURN_IMPOSSIBLE means we didn't do anything because the gems can't connect even if burned.
     */
    AutoburnLogic.AutoburnAction handleAutoburnGemGesture(Gem thisGem, TypeExpr typeToUnify, boolean burn) {

        ModuleTypeInfo currentModuleTypeInfo = tableTop.getCurrentModuleTypeInfo();

        // If the gem can't have inputs check if it is connectable.
        int numArgs = thisGem.getNInputs();
        if (numArgs == 0 || thisGem instanceof CollectorGem) {
            
            TypeExpr gemType = thisGem.getOutputPart().getType();
            if (TypeExpr.canUnifyType(gemType, typeToUnify, currentModuleTypeInfo)) {
                return AutoburnLogic.AutoburnAction.NOTHING;    
            } else {
                return AutoburnLogic.AutoburnAction.IMPOSSIBLE;
            }
        }

        // keep track of if the gem changed
        boolean gemChanged = false;

        if (burn) {

            // see if we tried this already
            if (autoBurnLastGem == thisGem && autoBurnLastType == typeToUnify && autoBurnLastResult != AutoburnLogic.AutoburnAction.UNBURNED) {
                return autoBurnLastResult;
            }

            // update autoburn state
            autoBurnLastGem = thisGem;
            autoBurnLastType = typeToUnify;
    
            
            if (thisGem instanceof RecordFieldSelectionGem &&
                    !((RecordFieldSelectionGem)thisGem).isFieldFixed() &&
                    !thisGem.isConnected() &&
                    typeToUnify.getArity() > 0) {
                //special case for burning a record field selection gem input pieces
                //we must consider changing the selected field.
                TypeExpr[] typePieces = typeToUnify.getTypePieces(1);
                FieldName fieldName = RecordFieldSelectionGem.pickFieldName(typePieces[0], typePieces[1], currentModuleTypeInfo);

                if (fieldName != null) {
                    //there is a valid field that can be picked if the input is burnt
                    autoBurnLastResult = AutoburnLogic.AutoburnAction.BURNED;
                    gemChanged = true;

                    doAutoburnUserAction(thisGem, new int[] { 0 });                            
                   
                } else if (TypeExpr.canUnifyType(thisGem.getOutputPart().getType(), typeToUnify, currentModuleTypeInfo)) {
                    autoBurnLastResult = AutoburnLogic.AutoburnAction.NOTHING;
                } else {
                    autoBurnLastResult = AutoburnLogic.AutoburnAction.IMPOSSIBLE;                   
                }
            } else {

                AutoburnInfo autoburnInfo = 
                    GemGraph.isAncestorOfBrokenGemForest(thisGem) ? 
                            null : 
                                AutoburnLogic.getAutoburnInfo(typeToUnify, thisGem, tableTop.getTypeCheckInfo());

                AutoburnUnifyStatus autoburnUnifyStatus =
                    autoburnInfo != null ?
                            autoburnInfo.getAutoburnUnifyStatus() :
                                AutoburnLogic.AutoburnUnifyStatus.NOT_POSSIBLE;           

                if (autoburnUnifyStatus == AutoburnUnifyStatus.UNAMBIGUOUS_NOT_NECESSARY) {

                    // This means the gems can be connected either by burning them or by
                    // just connecting them without burning. In this case we want to determine
                    // which action is best and do that.

                    int noBurnCloseness = TypeExpr.getTypeCloseness(thisGem.getOutputPart().getType(), typeToUnify, currentModuleTypeInfo);
                    if (autoburnInfo.getMaxTypeCloseness() > noBurnCloseness) {
                        autoburnUnifyStatus = AutoburnUnifyStatus.UNAMBIGUOUS;
                    } else {
                        autoburnUnifyStatus = AutoburnUnifyStatus.NOT_NECESSARY;
                    }

                    // Now resume checking the basic possibilities.
                }

                if (autoburnUnifyStatus == AutoburnUnifyStatus.UNAMBIGUOUS) {

                    autoBurnLastResult = AutoburnLogic.AutoburnAction.BURNED;
                    gemChanged = true;

                    // Set the inputs according to the unambiguous burning.  
                    // This should trigger burn events on listeners, causing the GemGraph to be re-typed.
                    AutoburnLogic.BurnCombination burnCombination = autoburnInfo.getBurnCombinations().get(0);
                    int[] burnArray = burnCombination.getInputsToBurn();
                    doAutoburnUserAction(thisGem, burnArray);

                } else if (autoburnUnifyStatus == AutoburnUnifyStatus.AMBIGUOUS) {

                    autoBurnLastResult = AutoburnLogic.AutoburnAction.MULTIPLE;

                } else if (autoburnUnifyStatus == AutoburnUnifyStatus.NOT_POSSIBLE) {

                    autoBurnLastResult = AutoburnLogic.AutoburnAction.IMPOSSIBLE;

                } else {

                    // Catches AutoburnUnifyType.NOT_NECESSARY and AutoburnUnifyType.AMBIGUOUS_NOT_NECESSARY.
                    // The last one means that it could be burned but the buring is ambiguous.
                    // In that case just connect without buring.

                    autoBurnLastResult = AutoburnLogic.AutoburnAction.NOTHING;
                }
            }
        } else {

            // undo any autoburn.
            gemChanged = doUnburnAutomaticallyBurnedInputsUserAction(thisGem);

            // set the result type
            if (gemChanged) {
                autoBurnLastResult = AutoburnLogic.AutoburnAction.UNBURNED;
            } else {
                autoBurnLastResult = AutoburnLogic.AutoburnAction.NOTHING;
            }
        }

        return autoBurnLastResult;
    }

    /**
     * Set the burn status of an input and post an undoable edit.
     * @param input the input to burn
     * @param newBurnStatus the new burn status of the input.
     */
    void doSetInputBurnStatusUserAction(Gem.PartInput input, AutoburnLogic.BurnStatus newBurnStatus) {

        // save the old burn status for the undo manager
        AutoburnLogic.BurnStatus oldBurnStatus = getBurnStatus(input);

        // If the status will changed, burn and notify undo managers of the edit..
        if (oldBurnStatus != newBurnStatus) {
            burnInput(input, newBurnStatus);
            tableTop.getUndoableEditSupport().postEdit(new UndoableBurnInputEdit(tableTop, input, oldBurnStatus));
        }
    }

    /**
     * Unburn any inputs which are automatically burnt
     * @param gem Gem the gem on which to burn inputs
     * @return boolean true if any inputs were unburnt
     */
    boolean doUnburnAutomaticallyBurnedInputsUserAction(Gem gem) {

        // Increment the update level to aggregate any burns with connection change edits.
        ExtendedUndoableEditSupport undoableEditSupport = tableTop.getUndoableEditSupport();
        undoableEditSupport.beginUpdate();
        
        boolean anyUnburnt = false;

        int numInputs = gem.getNInputs();
        for (int i = 0; i < numInputs; i++) {
            Gem.PartInput input = gem.getInputPart(i);
            if (getBurnStatus(input) == AutoburnLogic.BurnStatus.AUTOMATICALLY_BURNT) {
                doSetInputBurnStatusUserAction(input, AutoburnLogic.BurnStatus.NOT_BURNT);
                anyUnburnt = true;
            }
        }

        if (anyUnburnt) {
            // Decrement the update level.  This will post the edit if the level is zero.
            undoableEditSupport.endUpdate();
            updateForBurn();
        } else {
            // Discard the edit because nothing happened.
            undoableEditSupport.endUpdateNoPost();
        }

        return anyUnburnt;
    }
    
    /**
     * Update the tabletop after a burn action has taken place.
     */
    private void updateForBurn() {
        for (final Gem gem : tableTop.getGemGraph().getGems()) {
            if (gem instanceof CodeGem) {
                CodeGemEditor codeGemEditor = tableTop.getCodeGemEditor((CodeGem)gem);
                codeGemEditor.updateForBurn(tableTop.getTypeCheckInfo());
            }
        }
        tableTop.updateForGemGraph();
    }

    /**
     * Handle the gesture where the user (un)burns a displayed input.
     * A dialog may pop up asking for input, if the burning breaks a gem tree.
     * 
     * @param dPartToBurn the Displayed part in question
     * @return boolean whether the displayed input was burnt
     */
    boolean handleBurnInputGesture(DisplayedGem.DisplayedPart dPartToBurn){

        // check if burnable:
        //   must be an unconnected input that isn't on a collector gem.
        //   also must not be defined by anything broken.
        Gem gem = dPartToBurn.getDisplayedGem().getGem();
        if (!(dPartToBurn instanceof DisplayedGem.DisplayedPartInput) || 
                ((DisplayedGem.DisplayedPartInput)dPartToBurn).isConnected() ||
                (dPartToBurn.getGem() instanceof CollectorGem) ||
                (GemGraph.isAncestorOfBrokenGemForest(gem.getRootGem()))) {
            return false;
        }

        Gem.PartInput inputToBurn = ((DisplayedGem.DisplayedPartInput)dPartToBurn).getPartInput();

        // see if toggling the input burn state invalidates the tree
        boolean burnOk = !(inputToBurn.burnBreaksGem(tableTop.getTypeCheckInfo()));

        // if the (un)burn ok, toggle burnt state, else warn and display a dialog re: what to do
        if (burnOk) {
            doSetInputBurnStatusUserAction(inputToBurn, inputToBurn.isBurnt() ? AutoburnLogic.BurnStatus.NOT_BURNT : AutoburnLogic.BurnStatus.MANUALLY_BURNT);
            updateForBurn();
            return true;

        } else {
            // burning breaks the gem tree.  Highlight the output connection as potentially disconnecting.
            DisplayedConnection dConn = dPartToBurn.getDisplayedGem().getDisplayedOutputPart().getDisplayedConnection();  // should always be connected
            tableTop.setBadDisplayedConnection(dConn, true);
            tableTop.getTableTopPanel().repaint(dConn.getBounds());

            // Formulate the warning.
            String titleString = GemCutter.getResourceString("WarningDialogTitle");
            String message;
            if (inputToBurn.isBurnt()){
                message = GemCutter.getResourceString("UnburnWarning");
            } else {
                message = GemCutter.getResourceString("BurnWarning");
            }
            
            ExtendedUndoableEditSupport undoableEditSupport = tableTop.getUndoableEditSupport();

            // Show the warning.  Ask what to do..
            int option = JOptionPane.showConfirmDialog(tableTop.getTableTopPanel(), message, titleString, 
                                                       JOptionPane.YES_NO_OPTION, javax.swing.JOptionPane.WARNING_MESSAGE);

            // only do anything if the user answered yes.
            if (option == JOptionPane.YES_OPTION) {
                // Increment the update level to aggregate the edits.
                undoableEditSupport.beginUpdate();

                // disconnect the output and change the burnt state.
                Connection conn = dConn.getConnection();
                tableTop.doDisconnectUserAction(conn);
                doSetInputBurnStatusUserAction(inputToBurn, inputToBurn.isBurnt() ? AutoburnLogic.BurnStatus.NOT_BURNT : AutoburnLogic.BurnStatus.MANUALLY_BURNT);
                updateForBurn();

                // Decrement the update level.  This will post the edit if the level is zero.
                undoableEditSupport.endUpdate();
            
                return true;

            } else {
                // un-highlight the output connection
                tableTop.setBadDisplayedConnection(dConn, false);
                return false;
            }
        }
    }
    
    /**
     * Get the burn status of an input
     * @param input PartInput the input to check
     * @return BurnStatus the burn status of the input.
     */
    final AutoburnLogic.BurnStatus getBurnStatus(Gem.PartInput input){

        if (input.isBurnt()) {

            if (manuallyBurntInputs.contains(input)) {
                return AutoburnLogic.BurnStatus.MANUALLY_BURNT;
            } 

            return AutoburnLogic.BurnStatus.AUTOMATICALLY_BURNT;
        }

        return AutoburnLogic.BurnStatus.NOT_BURNT;
    }

    /**
     * Burn an input.
     * @param input the input to burn
     * @param newBurnStatus the new burn status of the input.
     */
    void burnInput(Gem.PartInput input, AutoburnLogic.BurnStatus newBurnStatus) {

        boolean inputWasBurnt = input.isBurnt();
        boolean inputWillBeBurnt = (newBurnStatus != AutoburnLogic.BurnStatus.NOT_BURNT);
        
        // do nothing if the old and new burnt state is the same..
        // TODO: Do we allow switching between auto- and manually burnt?
        if (inputWasBurnt == inputWillBeBurnt) {
            return;
        }

        if (inputWillBeBurnt) {
            if (newBurnStatus == AutoburnLogic.BurnStatus.MANUALLY_BURNT) {
                manuallyBurntInputs.add(input);
            }
            
        } else {
            // unburn
            manuallyBurntInputs.remove(input);
        }

        // Change the input burn state.
        input.setBurnt(inputWillBeBurnt);

        // Update the arguments.
        if (inputWillBeBurnt) {
            CollectorGem argumentTarget = GemGraph.getInputArgumentTarget(input);
            if (argumentTarget != null) {
                argumentTarget.updateReflectedInputs();
            }

        } else {
            CollectorGem argumentTarget = GemGraph.getInputArgumentTarget(input);
            
            // Add the argument to the target collector if it doesn't have any target, but it should.
            // eg. it was burned before it was connected.
            if (argumentTarget == null && input.getGem().getRootCollectorGem() != null) {
                argumentTarget = tableTop.getTargetCollector();
                argumentTarget.addArguments(tableTop.getTargetCollector().getTargetArguments().size(), Collections.singleton(input));
            }
            
            if (argumentTarget != null) {
                argumentTarget.updateReflectedInputs();
            }
        }

    }

    /**
     * Do the work necessary to carry out a user-initiated action to autoburn a gem.
     * Set the arguments to set "autoburnt" in a gem's tree (without re-typing..).  
     * We only index unburnt args.
     * @param gem the gem whose args to set autoburnt
     * @param argumentsToBurn the array of argument indices to burn, with respect to the inputs of the gem
     */
    private void doAutoburnUserAction(Gem gem, int[] argumentsToBurn) {

        ExtendedUndoableEditSupport undoableEditSupport = tableTop.getUndoableEditSupport();

        // Increment the update level to aggregate any burns.
        undoableEditSupport.beginUpdate();
        
        // set the burnable inputs
        int numBurntArgs = argumentsToBurn.length;
        for (int i = 0; i < numBurntArgs; i++) {
            int argIndex = argumentsToBurn[i];
            Gem.PartInput input = gem.getInputPart(argIndex);
            doSetInputBurnStatusUserAction(input, AutoburnLogic.BurnStatus.AUTOMATICALLY_BURNT);
        }

        if (numBurntArgs > 0) {
            // Decrement the update level.  This will post the edit if the level is zero.
            undoableEditSupport.endUpdate();
            updateForBurn();
        } else {
            // Discard the edit because nothing happened.
            undoableEditSupport.endUpdateNoPost();
        }
    }

    /**
     * Returns the set of inputs that were manually burned by the user in the GemCutter
     * @return set of inputs that were manually burned
     */
    Set<PartInput> getManuallyBurntSet() {
        return manuallyBurntInputs;     
    }

}
