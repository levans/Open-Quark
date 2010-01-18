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
 * DisplayedGemRunner.java
 * Creation date: (Jun 14, 2002 4:48:45 PM)
 * By: Edward Lam
 */
package org.openquark.gems.client;

import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import org.openquark.cal.compiler.CompilerMessage;
import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.ModuleTypeInfo;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.TypeConsApp;
import org.openquark.cal.compiler.TypeException;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.compiler.io.InputPolicy;
import org.openquark.cal.machine.StatsGenerator;
import org.openquark.cal.runtime.CALExecutorException;
import org.openquark.cal.services.WorkspaceManager;
import org.openquark.cal.valuenode.ForeignValueNode;
import org.openquark.cal.valuenode.TargetRunner;
import org.openquark.cal.valuenode.ValueNode;
import org.openquark.cal.valuenode.ValueNodeBuilderHelper;
import org.openquark.gems.client.Gem.PartInput;
import org.openquark.gems.client.valueentry.ValueEditor;
import org.openquark.gems.client.valueentry.ValueEditorAdapter;
import org.openquark.gems.client.valueentry.ValueEditorContext;
import org.openquark.gems.client.valueentry.ValueEditorDirector;
import org.openquark.gems.client.valueentry.ValueEditorEvent;
import org.openquark.gems.client.valueentry.ValueEditorHierarchyManager;
import org.openquark.gems.client.valueentry.ValueEditorManager;
import org.openquark.gems.client.valueentry.ValueEntryPanel;


/**
 * The GemCutter class delegates to DisplayedGemRunner to coordinate the actual work of running a displayed gem.
 * Creation date: (Jun 14, 2002 4:48:45 PM)
 * @author Edward Lam
 */  
class DisplayedGemRunner extends TargetRunner {    

    private final GemCutter gemCutter;
    
    /** Since the displayed GemRunner essentially has its own ValueEditor hierarchy, it makes sense that it has its own ValueEditorHierarchyManager */
    private final ValueEditorHierarchyManager valueEditorHierarchyManager;

    /** The displayed gem to run */
    private DisplayedGem targetDisplayedGem;
    
    /** The thread responsible for gathering arguments and running.  Null if not waiting for arguments. */
    private RunSetupThread runSetupThread = null;

    /**
     * An ArgumentValueCommitter is responsible for listening to changes in the value of an argument, and
     *   propagating any type changes to other arguments.
     * @author Edward Lam
     */
    private static class ArgumentValueCommitter extends ValueEditorAdapter {

        /** The value editor manager in which the context exists.  
         * Used for access to its value node builder helper and value node transformer. */
        private final ValueEditorManager valueEditorManager;

        /** map from part input to the value editor whose value it's editing. */
        private final Map<PartInput, ValueEditor> inputToEditorMap;
        
        /**
         * The default constructor for this ArgumentValueContext
         * @param valueEditorManager the editor manager for this listener
         * @param inputToEditorMap map from input to the editor for the value of that input.
         */
        public ArgumentValueCommitter(ValueEditorManager valueEditorManager, Map<PartInput, ValueEditor> inputToEditorMap) {
            this.inputToEditorMap = new HashMap<PartInput, ValueEditor>(inputToEditorMap);
            this.valueEditorManager = valueEditorManager;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public void valueCommitted(ValueEditorEvent evt) {
            ValueEditor committedEditor = (ValueEditor)evt.getSource();
            
            ValueNode oldValue = evt.getOldValue();
            ValueNode newValue = committedEditor.getValueNode();
            
            if (!oldValue.sameValue(newValue)) {
                switchValueNodeTypeExpr(oldValue, newValue);
                committedEditor.setSize(committedEditor.getPreferredSize());
            }
        }
        
        /**
         * This is used for switching the type of a ValueNode within a given context.
         * Basically, a switch is made between valueNode and newValueNode, but their types are different.
         * Consequently, other valueNodes may be modified.
         * 
         * For example:
         * 
         * Let's say you have "add 1 2", where 1, 2 are of type Integer and are represented by ValueNodes,
         * then to switch '1' to '1.0' (a Double), you must switch '2' to '2.0' to avoid type clashes.
         * 
         * This method will handle the switching of the associated types within a type context, so it should so if you pass 1, 1.0
         * it'll convert 2 to 2.0 .
         * 
         * Note that you are able to pass in '5.0' as the new value if you'd like.     
         * 
         * @param oldValueNode your original ValueNode
         * @param newValueNode the valueNode that you want to arrive at
         */
        private void switchValueNodeTypeExpr(ValueNode oldValueNode, ValueNode newValueNode) {
        
            // Create the map from input to value node
            Map<PartInput, ValueNode> inputToValueNodeMap = new HashMap<PartInput, ValueNode>();
            for (final PartInput input : inputToEditorMap.keySet()) {
                ValueEditor editor = inputToEditorMap.get(input);
                inputToValueNodeMap.put(input, editor.getOwnerValueNode());
            }
        
            // Create the type switcher
            InputValueTypeSwitchHelper switcher = new InputValueTypeSwitchHelper(valueEditorManager.getValueNodeCommitHelper());

            // Get the switched values
            Map<PartInput, ValueNode> inputNewValueMap = switcher.getInputSwitchValues(oldValueNode, newValueNode, inputToValueNodeMap);
            
            // Now update with the switched values.
            for (final Map.Entry<PartInput, ValueNode> mapEntry : inputNewValueMap.entrySet()) {
                Gem.PartInput input = mapEntry.getKey();
                ValueNode newInputValue = mapEntry.getValue();
            
                ValueEditor editor = inputToEditorMap.get(input);

                editor.changeOwnerValue(newInputValue);
                editor.setSize(editor.getPreferredSize());
            }
        }
    }

    /**
     * A class responsible for putting up argument controls, gathering arguments, and executing with those args.
     * When this thread is started, it either executes straight away (if no args are required) or 
     * sets up the gui to enter arguments, then waits for notification that args are entered.
     * To execute, this thread starts another executor thread to actually perform the evaluation.
     * @author Edward Lam
     */
    private class RunSetupThread extends Thread {

        /**
         * The run setup thread may display input panels and wait on this lock.
         * If so, on notify, the thread gathers the user-entered arguments and then executes.
         */
        private final ExecuteLock runSetupLock = new ExecuteLock();

        /** On notification, whether the thread should run with arguments.  False results in termination. */
        private boolean shouldRun;

        /**
         * Trivial class to implement a lock.
         * @author Edward Lam
         */
        private class ExecuteLock {
            
            private volatile boolean shouldWait = false;

            public synchronized void executeSleep() {
                shouldWait = true;
                try {
                    while (shouldWait) {
                        wait();
                    }
                } catch (InterruptedException e) {
                }
            }

            public synchronized void executeWake(boolean shouldRun) {
                RunSetupThread.this.shouldRun = shouldRun;
                shouldWait = false;
                notify();
            }
        }

        /**
         * {@inheritDoc}
         * This Runnable causes the GemCutter to enter the GUI state, and either
         * 1) Put up argument controls, wait for notification that args are entered, and executes with those args, or
         * 2) Executes without args if no args are necessary.
         * 
         * Notification is given by calling executeWake() on the executeLock.
         */
        @Override
        public void run() {

            // Use input policies for arguments.
            // We're going into the run GUI state
            gemCutter.enterGUIState(GemCutter.GUIState.RUN);

            InputPolicy[] inputPolicies = new InputPolicy[0];
            TypeExpr[] argTypes = new TypeExpr[0];
            Object[] args = new Object[0];

            // Display arg controls and wait if there are args to enter.  Otherwise, execute straight away.
            if (targetHasArgs()) {
                // Set transport controls status for running
                gemCutter.setTargetRunningButtons(true);

                // Set the status message saying we're at the start.
                gemCutter.getStatusMessageDisplayer().setMessageFromResource(this, "SM_AtStart", StatusMessageDisplayer.MessageType.DEFERENTIAL);

                // Put up the argument controls
                displayArgumentControls();

                // Wait for the user to enter the arguments.
                waitForInput();

                // set the setup thread to null.  Do this first in case the next call fails.
                runSetupThread = null;

                if (!shouldRun) {
                    // User pressed stop.
                    clearGUIRunState();
                    return;
                }

                // get the args
                inputPolicies = getInputPolicies();
                argTypes = getArgTypes();
                
                args = getRuntimeArgumentValues();
                
                if (args == null || inputPolicies == null) {
                    // There was an error getting args.
                    clearGUIRunState();
                    return;
                }
            } 

            // set the setup thread to null
            runSetupThread = null;

            // Build the test program
            try {
                buildTestProgram(inputPolicies, argTypes);
            } catch (ProgramCompileException pce) {
                // A problem occurred
                CompilerMessage.Severity errorSeverity = pce.getMaxErrorSeverity();
                   
                if (errorSeverity.compareTo (CompilerMessage.Severity.FATAL) < 0) {                   
                    // Some error in compiling
                    String msgText = GemCutter.getResourceString("TestHadErrors") + "\n" + GemCutter.getResourceString("CompileErrorIntro") + "\n" + pce.getFirstError();
                    JOptionPane.showMessageDialog(gemCutter, msgText, GemCutter.getResourceString("WindowTitle"), JOptionPane.WARNING_MESSAGE);
        
                } else {
                    // BIG problems
                    String msgText = GemCutter.getResourceString("CompileGemError") + "\n" + GemCutter.getResourceString("CompileErrorIntro") + "\n" + pce.getFirstError();
                    JOptionPane.showMessageDialog(gemCutter, msgText, GemCutter.getResourceString("WindowTitle"), JOptionPane.ERROR_MESSAGE);
                }
            
                gemCutter.getTableTop().setRunning(targetDisplayedGem, false);
                clearGUIRunState();
                return;
            }

            // Execute the new SC with the arguments.
            executeTestProgram(args, true);
        }

        /**
         * Display value entry controls for all unbound arguments.
         * Precondition: the target must be set.
         */
        private void displayArgumentControls() {

            TableTopPanel tableTopPanel = getTableTopPanel();

            // Build the list of value entry panels and place them onto the TableTop

            // Get the list of unbound arguments
            List<Gem.PartInput> argParts = targetDisplayedGem.getTargetArguments();

            // see if it's ok to use the cached argument values
            boolean canUseCachedArgs = canUseCachedArguments();

            // Figure out the types of the argument panels.  Note that we must account for cached values
            int numArgs = argParts.size();

            // make copies of the input types.
            TypeExpr[] inputTypes = new TypeExpr[numArgs];
            for (int i = 0; i < numArgs; i++) {
                // Get this input, and its type
                Gem.PartInput inputPart = argParts.get(i);
                inputTypes[i] = inputPart.getType();
            }
            
            TypeExpr[] specializedInputTypes;
                        
            if (canUseCachedArgs) {
                 
                //this is the array of types cached for each argument. If there is no cached type, we just use the argument type.               
                TypeExpr[] cachedTypes = new TypeExpr[numArgs]; 
                for (int i = 0; i < numArgs; ++i) {
                    // What we do next depends on whether we use cached sink values
                    Gem.PartInput inputPart = argParts.get(i);
                    ValueNode cachedVN = gemCutter.getTableTop().getCachedValue(inputPart);
                    if (cachedVN != null) {
                        cachedTypes[i] = cachedVN.getTypeExpr();
                    }                                                    
                }
                
                try { 
                    ModuleTypeInfo contextModuleTypeInfo = gemCutter.getPerspective().getWorkingModuleTypeInfo();                                             
                    specializedInputTypes = TypeExpr.patternMatchPieces(cachedTypes, inputTypes, contextModuleTypeInfo);
                } catch (TypeException te) {
                    throw new IllegalStateException("Error reusing cached args.");
                }                            
            } else {
                specializedInputTypes = TypeExpr.copyTypeExprs(inputTypes);
            }

            ValueEditorManager valueEditorManager = gemCutter.getValueEditorManager();
            ValueEditorDirector valueEditorDirector = valueEditorManager.getValueEditorDirector();

            Map<PartInput, ValueEditor> inputToEditorMap = new LinkedHashMap<PartInput, ValueEditor>();

            // Step through args, create a new value input panel for each, with the correct value type
            for (int i = 0; i < numArgs; i++) {

                // Get the gem and input
                final Gem.PartInput inputPart = argParts.get(i);
                final Gem inputGem = inputPart.getGem();
                
                int argumentNumber = inputPart.getInputNum();
                QualifiedName scName = null;
                
                if (inputGem instanceof FunctionalAgentGem) {
                    scName = ((FunctionalAgentGem) inputGem).getName();
                }

                // What we do next depends on whether we use cached sink values
                ValueNode cachedVN;
                final ValueEditor editor;
                if (canUseCachedArgs && (cachedVN = gemCutter.getTableTop().getCachedValue(inputPart)) != null) {
                    // use cached sink value to generate the VEP

                    // get a copy of the cached VN but with the new type expr
                    ValueNode newVN = cachedVN.copyValueNode();

                    // instantiate the VEP with the new VN (which has the old cached value)
                    editor = valueEditorDirector.getRootValueEditor(valueEditorHierarchyManager,
                                                                    newVN,
                                                                    scName,
                                                                    argumentNumber,
                                                                    new GemCutterMetadataRunner(gemCutter, inputGem));

                } else {
                    // don't use cached sink value.  Generate the default VEP for this sink.
                    TypeExpr inputType = specializedInputTypes[i];
                    editor = valueEditorDirector.getRootValueEditor(valueEditorHierarchyManager,
                                                                    inputType,
                                                                    scName,
                                                                    argumentNumber,
                                                                    new GemCutterMetadataRunner(gemCutter, inputGem));
                }

                // If this is a value entry panel then set the name of the input for use in tooltips
                if (editor instanceof ValueEntryPanel) {
                    ((ValueEntryPanel)editor).setArgumentName(inputPart.getArgumentName().getCompositeName());
                }
                
                // Position the editor next to the connection point.
                positionEditor(editor, inputPart);
                
                // Now add the editor to the table top.
                tableTopPanel.add(editor, 0);                

                // Add to the list of active entry panels
                valueEditorHierarchyManager.addTopValueEditor(editor);

                // Update the context and editor map
                editor.setContext(new ValueEditorContext() {
                    public TypeExpr getLeastConstrainedTypeExpr() {
                        return inputPart.getType();
                    }
                });
                inputToEditorMap.put(inputPart, editor);
                
                // If the editor resizes because it's value changes, make sure it stays aligned to the connection
                editor.addComponentListener(new ComponentAdapter() {
                    @Override
                    public void componentResized(ComponentEvent e) {
                        positionEditor((ValueEditor) e.getComponent(), inputPart);
                    }
                });
            }

            // Must prevent ValueGems from being editable.
            tableTopPanel.setValueGemsEnabled(false);

            // Need to activate the value editor hierarchy to ensure correct highlighting.
            valueEditorHierarchyManager.activateCurrentEditor();
            
            // Set the context and type switch listener for each new editor.
            ArgumentValueCommitter argumentValueCommitter = new ArgumentValueCommitter(valueEditorManager, inputToEditorMap);

            for (final ValueEditor argumentEditor : getArgumentPanels()) {
                argumentEditor.addValueEditorListener(argumentValueCommitter);
            }

            // May need to show argument controls.
            if (numArgs > 0) {
                gemCutter.showArgumentControls(inputToEditorMap);
                gemCutter.getResetAction().setEnabled(true);
            } else {
                gemCutter.getResetAction().setEnabled(false);
            }
        }
        
        /**
         * Positions the given value editor next to the connection point for the given input.
         * @param editor the editor to position
         * @param inputPart the input part to position it next to
         */
        private void positionEditor(ValueEditor editor, Gem.PartInput inputPart) {
            
            // Determine position of the panel and make sure it will be within the parent
            Point xy = gemCutter.getTableTop().getDisplayedPartConnectable(inputPart).getConnectionPoint(); 

            editor.setSize(editor.getPreferredSize());
            
            // Make sure this is OK
            xy.x -= editor.getWidth();
            xy.y -= editor.getHeight()/2;
            if (xy.x <= 0) {
                xy.x = 0;
            }
            if (xy.y <= 0) {
                xy.y = 0;
            }

            editor.setLocation(xy);
        }
        
        /**
         * See if we can reasonably use the values cached in the target's argument parts.
         * For now this just means if we can use the cached panels without performing any type 
         * conversions of any sort.
         * Precondition: target and inputToTypeExprMap are set
         * Postcondition: none.
         * @return boolean true if it's ok to use the values cached in the target's arguments.
         */
        private boolean canUseCachedArguments() {
            List<Gem.PartInput> argParts = targetDisplayedGem.getTargetArguments();
    
            // Get the input types.
            TypeExpr[] targetPieces = new TypeExpr[argParts.size()];
            for (int i = 0; i < targetPieces.length; i++) {
                targetPieces[i] = argParts.get(i).getType();
            }         
                        
            TypeExpr[] typesFromCachedArgs = new TypeExpr[targetPieces.length];
    
            // Step through args.  i'th arg corresponds to the i'th piece.
            // Note that there may be more pieces than args if the output type is a function            
            for (int i = 0; i < targetPieces.length; i++) {
                // Get this input, and any cached value
                Gem.PartInput sinkPart = argParts.get(i);            
                ValueNode cachedVN = gemCutter.getTableTop().getCachedValue(sinkPart);
    
                // there may be no cached value for this argument
                if (cachedVN != null) {
                    typesFromCachedArgs[i] = cachedVN.getTypeExpr(); 
                }                                   
            }
            
            ModuleTypeInfo contextModuleTypeInfo = gemCutter.getPerspective().getWorkingModuleTypeInfo();
            
            return TypeExpr.canPatternMatchPieces(typesFromCachedArgs, targetPieces, contextModuleTypeInfo);            
        }

        private InputPolicy[] getInputPolicies() {
            List<ValueEditor> argPanelList = getArgumentPanels();
            InputPolicy[] ips = new InputPolicy[argPanelList.size()];
            for (int i = 0, nArgs = argPanelList.size(); i < nArgs; i++) {
                ValueEditor argPanel = argPanelList.get(i);
                ips[i] = argPanel.getValueNode().getInputPolicy();
            }
            
            return ips;
        }
        
        private TypeExpr[] getArgTypes() {
            List<ValueEditor> argPanelList = getArgumentPanels();
            TypeExpr[] argTypes = new TypeExpr[argPanelList.size()];
            for (int i = 0, nArgs = argPanelList.size(); i < nArgs; i++) {
                ValueEditor argPanel = argPanelList.get(i);
                argTypes[i] = argPanel.getValueNode().getTypeExpr();
            }
            
            return argTypes;
        }
        
        private Object[] getRuntimeArgumentValues() {

            List<ValueEditor> argPanelList = getArgumentPanels();
            ArrayList<Object> inputJavaValues = new ArrayList<Object>();
            
            for (int i = 0, nArgs = argPanelList.size(); i < nArgs; i++) {
                ValueEditor argPanel = argPanelList.get(i);
                Object[] values = argPanel.getValueNode().getInputJavaValues();
                for (final Object value: values) {
                    inputJavaValues.add(value);
                }
            }
            return inputJavaValues.toArray();
        }

        
        /**
         * Wait for user input.
         */
        synchronized void waitForInput() {
            runSetupLock.executeSleep();
        }
    }

    /**
     * Constructor for a displayed gem runner.
     * @param workspaceManager the workspaceManager on which this runner is based.
     * @param gemCutter the reference to the GemCutter
     */
    public DisplayedGemRunner(WorkspaceManager workspaceManager, GemCutter gemCutter) {
        super(workspaceManager);

        if (gemCutter == null) {
            throw new NullPointerException("Argument 'gemCutter' cannot be null.");
        }
        this.gemCutter = gemCutter;
        
        valueEditorHierarchyManager = new ValueEditorHierarchyManager(gemCutter.getValueEditorManager());
    }

    /**
     * Notify the setup thread to run with the arguments from current user input.
     */
    private synchronized void gatherArgumentsAndRun() {
        runSetupThread.runSetupLock.executeWake(true);
    }

    /**
     * Execute the supercombinator defined by the Target.
     * @param targetDisplayedGem the gem to run. Pass null if just continuing execution.
     */
    public void runTargetDisplayedGem(DisplayedGem targetDisplayedGem) {
    
        // Clear away any children value editors.  This commits values from argument panels and ValueGem VEPs.
        commitActiveEntryPanels();

        if (targetDisplayedGem == null) {
            // Either a start from argument entry, or a continue.  (or an error..)
            if (runSetupThread != null) {
                // We're waiting for arguments.  Get the setup thread to gather args and start execution
                gatherArgumentsAndRun();

            } else {
                // Already executing, just return and let the executor to continue running
            }
            
            return;
        }

        setTargetDisplayedGem(targetDisplayedGem, gemCutter.getWorkingModuleName());
    
        // If we're not executing, get going.  We should be starting from edit mode...
        if (runtime == null) {
    
            TableTop tableTop = gemCutter.getTableTop();
            
            // Must stop Intellicut mode.
            tableTop.getIntellicutManager().stopIntellicut();
        
            // Must get rid of any open code gem editors.
            tableTop.hideAllCodeGemEditors();
        
            // ensure the target is visible
            getTableTopPanel().scrollRectToVisible(this.targetDisplayedGem.getBounds());
    
            // First, check that we can handle the output.
            // Then, check if this is can be sensibly tested (by supplying unbound args)    
            if (!targetHasHandleableOutput()) {
    
                String msgText = GemCutter.getResourceString("OutputTypeNotHandled");
                JOptionPane.showMessageDialog(gemCutter, msgText, GemCutter.getResourceString("CannotExec"), JOptionPane.INFORMATION_MESSAGE);
    
            } else if (targetHasProvidableArgs()) {                        
    
                // Target is running..
                gemCutter.getTableTop().setRunning(targetDisplayedGem, true);

                // Setup the run.
                runSetupThread = new RunSetupThread();
                runSetupThread.start();

            } else {
                // Can't allow execution as we cannot provide default or user provided
                // arguments of the correct type to satisfy all unbound arguments
                String msgText = GemCutter.getResourceString("CannotProvideExecArgs");
                JOptionPane.showMessageDialog(gemCutter, msgText, GemCutter.getResourceString("CannotExec"), JOptionPane.INFORMATION_MESSAGE);
            }        
    
        } else {
            // For some reason, this is called with a target but we're already running.
            // Already executing, just return and let the executor to continue running
        }
    }

    /**
     * Set the displayed gem being run by this target runner.
     * @param targetDisplayedGem the displayed gem to run.
     * @param targetModule the module in which the target exists.
     */
    void setTargetDisplayedGem(DisplayedGem targetDisplayedGem, ModuleName targetModule) {
        setTarget(targetDisplayedGem.getTarget(), targetModule);
        this.targetDisplayedGem = targetDisplayedGem;
    }

    /**
     * Clears the data types and values of the arguments of the running gem
     */
    void resetArgumentValues() {
        // Loop thru the ValueEditors and reset their ValueNodes.
        for (final ValueEditor editor : valueEditorHierarchyManager.getTopValueEditors()) {

            // build the new ValueNode and give it to the argument panel
            TypeExpr leastConstrainedType = editor.getContext().getLeastConstrainedTypeExpr();
            ValueNode newNode = valueEditorHierarchyManager.getValueEditorManager().getValueNodeBuilderHelper().getValueNodeForTypeExpr(leastConstrainedType);

            editor.changeOwnerValue(newNode);
        }
    }
    
    /**
     * Cache the values entered by the user in VEPs dynamically-displayed on VM start.
     */
    void cacheArgumentValues() {
    
        // Collect the value entry panels which correspond to target arguments in the TableTop
        List<ValueEditor> argPanels = getArgumentPanels();
    
        List<Gem.PartInput> argParts = targetDisplayedGem.getTargetArguments();
        int numArgs = argParts.size();
        
        // sanity check
        if (numArgs != argPanels.size()) {
            throw new IllegalStateException("Programming Error: matching " + numArgs + " arguments and " + argPanels.size() + " argument panels.");
        }
    
        for (int i = 0; i < numArgs; i++) {
            Gem.PartInput argPart = argParts.get(i);
            ValueEditor argPanel = argPanels.get(i);
            ValueNode argNode = argPanel.getValueNode();
            gemCutter.getTableTop().cacheArgValue(argPart, argNode);
        }
    }

    /**
     * Remove any value entry controls for unbound arguments.
     */
    private void removeArgumentControls() {

        // Remove the argument controls from the TableTop
        valueEditorHierarchyManager.removeValueEditors(getArgumentPanels(), true);
        
        // No longer need the argument controls.
        gemCutter.hideArgumentControls();
        
        TableTopPanel tableTopPanel = getTableTopPanel();
        
        // Re-enable ValueGems.
        tableTopPanel.setValueGemsEnabled(true);
        
        tableTopPanel.repaint();
    }
    
    /**
     * Display execution results.  Call this to display the results of the run when execution terminates.
     * Note: the runtime must still be available
     */
    private void displayResults() {

        int messageIcon = -1;
        String messageTitle = null;
        String errorMessage = null;
        
        
        // Set up the icon and the title
        if (error == null) {
            messageIcon = JOptionPane.INFORMATION_MESSAGE;
            messageTitle = GemCutter.getResourceString("GemResults");
            errorMessage = null;
        } else {
            errorMessage = error.getMessage();
            CALExecutorException.Type resultStatus = error.getExceptionType();
            if (resultStatus == CALExecutorException.Type.PRIM_THROW_FUNCTION_CALLED) {
                messageIcon = JOptionPane.ERROR_MESSAGE;
                messageTitle = GemCutter.getResourceString("GemError");
                
            } else if (resultStatus == CALExecutorException.Type.FOREIGN_OR_PRIMITIVE_FUNCTION_EXCEPTION) {
                messageIcon = JOptionPane.ERROR_MESSAGE;
                messageTitle = GemCutter.getResourceString("ForeignFunctionError");
    
            } else if (resultStatus == CALExecutorException.Type.ERROR_FUNCTION_CALL) {
                messageIcon = JOptionPane.ERROR_MESSAGE;
                messageTitle = GemCutter.getResourceString("GemError");
    
            } else if (resultStatus == CALExecutorException.Type.PATTERN_MATCH_FAILURE) {
                messageIcon = JOptionPane.ERROR_MESSAGE;
                messageTitle = GemCutter.getResourceString("PatternMatchFailure");
    
            } else if (resultStatus == CALExecutorException.Type.INTERNAL_RUNTIME_ERROR) {
                messageIcon = JOptionPane.ERROR_MESSAGE;
                messageTitle = GemCutter.getResourceString("RuntimeError");
    
            } else if (resultStatus == CALExecutorException.Type.USER_TERMINATED) {
                messageIcon = JOptionPane.INFORMATION_MESSAGE;
                messageTitle = GemCutter.getResourceString("GemResults");
            } else {
                throw new IllegalStateException("Unrecognized execution result status: " + resultStatus);
            }
        }
        
        if (executionResult != null) {
            try {
                executionResult.toString ();
            } catch (Exception e) {
                executionResult = null;
                if (errorMessage == null) {
                    errorMessage = "Error trying to display result value.";
                }
            }
        }
        
        // If we're not in debug output mode, and there's no scary weirdness, then we use the
        // value entry mechanism.  Else, just use the debug output.
        if (!gemCutter.isDebugOutputMode()// && (numValues > 0) //&& !programmaticErrorFlagged 
                /*&& valueNodeBuilderHelper.isConstructorNodeArgumentStackEmpty()*/) {
    
            // If there's an extra message, print it out.
            ValueNode vn = getOutputValueNode();
            if (executionResult == null) {
                
                // If we got a null result and no error, check for the case where the null represents a null foreign value.
                
                // foreignTypeConsApp will hold the TypeConsApp for the output value 
                // if the value is a foreign value and the result is null.
                TypeConsApp foreignTypeConsApp = null;
                if (errorMessage == null && vn != null) {
                    TypeConsApp typeConsApp = vn.getTypeExpr().rootTypeConsApp();
                    if (typeConsApp != null && typeConsApp.getForeignTypeInfo() != null) {
                        foreignTypeConsApp = typeConsApp;
                    }
                }

                if (foreignTypeConsApp != null) {
                    // Make sure that the value node is a ForeignValueNode.

                    // An example of a problem this prevents:
                    //   The ColorValueNode is encapsulates a foreign Java value java.awt.Color, and so has a foreign type.
                    //   However, the color output editor may assume that it has a non-null editor.
                    if (!(vn instanceof ForeignValueNode)) {
                        vn = new ForeignValueNode(null, foreignTypeConsApp);
                    }
                    vn.setOutputJavaValue(null);
                
                } else {
                    vn = null;
                }
                
            } else {
                vn.setOutputJavaValue(executionResult);
            }
            
            // Make sure target is visible before we display results 
            gemCutter.getTableTop().getTableTopPanel().scrollRectToVisible(targetDisplayedGem.getBounds());
            gemCutter.displayOutput(vn, targetDisplayedGem, messageTitle, errorMessage);
            
        } else {
            // The main return is the 0th output value
            String message = (executionResult != null) ? executionResult.toString() : GemCutter.getResourceString("NoResults");                            
    
            // Display the output, debug style.
            JOptionPane.showMessageDialog(gemCutter, message, messageTitle, messageIcon);
        }
    }

    /**
     * This method is called when the runtime is about to start evaluating.
     */
    @Override
    public void enterRunningState() {
        List<ValueEditor> argPanelList = getArgumentPanels();

        // If there are argument panels, we use these to specialize the target
        // type.
        int nPanels = argPanelList.size ();
        TypeExpr[] argPanelTypes = new TypeExpr[nPanels];
        for (int i = 0; i < nPanels; i++) {
            ValueEditor argPanel = argPanelList.get (i);
            argPanelTypes[i] = argPanel.getValueNode ().getTypeExpr ();
        }

        gemCutter.getStatusMessageDisplayer ().clearMessage (this);
        gemCutter.setTargetRunningButtons (true);
        
        // System.out.println ("DisplayedGemRunner.run()");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void exitRunningState() {

        // Can't run forward (can only restart or quit)
        gemCutter.setTargetRunningButtons(true);
            
        // Show results
        try {
            displayResults();
        } catch (Throwable t) {
            t.printStackTrace();
        }

        clearGUIRunState();

        // Set the termination message
        String terminationMessage = getTerminationMessage();
        gemCutter.getStatusMessageDisplayer().clearMessage(this);
        gemCutter.getStatusMessageDisplayer().setMessage(this, terminationMessage, StatusMessageDisplayer.MessageType.DEFERENTIAL);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void stopExecution() {

        if (runSetupThread != null) {
            // waiting for arguments
            runSetupThread.runSetupLock.executeWake(false);
            runSetupThread = null;
            return;
        }
        
        // Executing.
        super.stopExecution();
    }

    /**
     * Clear the GUI elements associated with the run state.
     */
    private void clearGUIRunState() {

        // We're leaving the run state
        gemCutter.enterGUIState(GemCutter.GUIState.EDIT);
        
        gemCutter.getTableTop().setRunning(targetDisplayedGem, false);

        // cache the values entered in the VEPs in their corresponding parts
        cacheArgumentValues();

        // Clear away any children value editors (but not top level ValueEditors).
        commitActiveEntryPanels();

        // Remove the argument controls
        removeArgumentControls();
    }

    /**
     * Get the termination message from the runtime.
     * This should only be called once the current runtime is terminated (but still available).
     * @return the related termination message
     */
    private String getTerminationMessage() {
        StringBuilder terminationMessage = new StringBuilder ();

        if (error != null && error.getExceptionType() == CALExecutorException.Type.USER_TERMINATED) {
            // externally-requested termination
            terminationMessage.append (GemCutter.getResourceString("SM_TerminatedPrematurely"));
        } else {
            // runtime stopped of its own accord
            terminationMessage.append (GemCutter.getResourceString("SM_Terminated"));

        }

        if (execTimeGen != null) {            
            for (final StatsGenerator.StatsObject stat : execTimeGen.getStatistics()) {
                terminationMessage.append (stat.generateShortMessage());
            }
        }
        
        return terminationMessage.toString ();
    }
    
    /**
     * Get the TableTopPanel associated with this runner.
     * @return TableTopPanel
     */
    private TableTopPanel getTableTopPanel() {
        return gemCutter.getTableTopPanel();
    }

    /**
     * Returns whether the target's output type can be handled.
     * @return boolean
     */
    private boolean targetHasHandleableOutput() {
        
        TypeExpr outputTypeExpr = targetDisplayedGem.getGem().getResultType();
        return ValueNodeBuilderHelper.canHandleOutput(outputTypeExpr);
    }

    /**
     * Determine if the current target Gem has any arguments.
     * @return boolean whether the current target has any arguments.
     */
    private boolean targetHasArgs() {
        return (!targetDisplayedGem.getTargetArguments().isEmpty());
    }

    /**
     * Determine if the current target Gem only has arguments that we can provide editors for.
     * @return boolean true if we can provide all the arguments, false if we can't
     */
    private boolean targetHasProvidableArgs() {
        // Get the target's arguments first
        List<Gem.PartInput> args = targetDisplayedGem.getTargetArguments();
    
        // If we have no arguments, then we at least have providable args (we can easily
        // provided none at all!)
        if (args == null) {
            return true;
        }
    
        // Check all the argument types for being ones that we can provide
        int numArgs = args.size();
        Gem.PartInput sinkPart;
        for (int i = 0; i < numArgs; i++) {
            // Get this part
            sinkPart = args.get(i);
    
            // Can we deal with this type?
            if (!valueEditorHierarchyManager.getValueEditorManager().canInputDefaultValue(sinkPart.getType())) {
                // Oops, can't provide an editor for this type
                return false;
            }    
        }    
        
        // If we haven't found any dodgy ones, then we can provide all of them!
        return true;
    }

    /**
     * Returns the top level ValueEditors for arguments managed by this displayed gem runner.
     * @return The top-level argument panels
     */
    private List<ValueEditor> getArgumentPanels() {
        return valueEditorHierarchyManager.getTopValueEditors();
    }

    /**
     * Closes all children of the editors for arguments and value gems and commits their values.
     */
    private void commitActiveEntryPanels() {
        
        valueEditorHierarchyManager.commitHierarchy(true);

        ValueEditorHierarchyManager gemCutterHierarchyManager = gemCutter.getValueEditorHierarchyManager();
        gemCutterHierarchyManager.commitHierarchy(true);
        
        ValueEditorHierarchyManager explorerHierarchyManager = gemCutter.getTableTopExplorer().getValueEditorHierarchyManager();
        explorerHierarchyManager.commitHierarchy(true);
    }
}

