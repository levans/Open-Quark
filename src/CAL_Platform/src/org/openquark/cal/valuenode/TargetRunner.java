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
 * TargetRunner.java
 * Creation date: (5/8/01 5:40:36 PM)
 * By: Bo Ilic
 */
package org.openquark.cal.valuenode;

import java.util.logging.Level;

import org.openquark.cal.compiler.AdjunctSource;
import org.openquark.cal.compiler.CompilerMessage;
import org.openquark.cal.compiler.CompilerMessageLogger;
import org.openquark.cal.compiler.MessageKind;
import org.openquark.cal.compiler.MessageLogger;
import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.ModuleTypeInfo;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.TypeChecker;
import org.openquark.cal.compiler.TypeException;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.compiler.io.EntryPoint;
import org.openquark.cal.compiler.io.EntryPointSpec;
import org.openquark.cal.compiler.io.InputPolicy;
import org.openquark.cal.compiler.io.OutputPolicy;
import org.openquark.cal.machine.StatsGenerator;
import org.openquark.cal.runtime.CALExecutorException;
import org.openquark.cal.services.WorkspaceManager;


/**
 * A TargetRunner runs CAL code and returns a value node.
 * Creation date: (5/8/01 5:40:36 PM)
 * @author Bo Ilic
 */  
public abstract class TargetRunner extends CALRunner {

    /** The target to run. */
    private Target target;
    private EntryPoint entryPoint;
    
    /** The module in which to run the target. */
    private ModuleName targetModule;

    /** The arguments to the target when running. */
    private Object[] entryPointArguments;
    
    // An object to register with the executor to generate
    // running time statistics.
    protected StatsGenerator execTimeGen = null;
    
    private boolean showConsoleInfo = true;
    
    private ValueNode outputValueNode;
    protected CALExecutorException error;
    protected Object executionResult;
    
    /**
     * A ProgramCompileException is an exception which signifies a failure to compile a program.
     * Creation date: (Jun 17, 2002 4:38:12 PM)
     * @author Edward Lam
     */
    public static class ProgramCompileException extends Exception {

        private static final long serialVersionUID = -3009322703048839008L;

        /** The maximum error severity returned by the compile. */
        private final CompilerMessage.Severity maxErrorSeverity;

        /** The first compiler error returned by the compile. */
        private final CompilerMessage  firstError;

        /**
         * Constructor for a ProgramCompileException.
         * @param errSev The error severity returned by the compile.
         * @param firstError The first compiler error returned by the compile.
         */
        public ProgramCompileException(CompilerMessage.Severity errSev, CompilerMessage firstError) {
            
            if (firstError == null) {
                throw new NullPointerException ("ProgramCompileException: firstError must not be null.");
            }
            
            this.maxErrorSeverity = errSev;
            this.firstError = firstError;                                
        }

        /**
         * Get the error severity code returned by the compile in question.
         * @return ErrorMessage.Severity the error severity returned by the compile.
         */
        public CompilerMessage.Severity getMaxErrorSeverity() {
            return maxErrorSeverity;
        }

        /**        
         * @return CompilerMessage The first compiler error returned by the compile.
         */
        public CompilerMessage getFirstError() {
            return firstError;
        }
    }

    /**
     * Constructor for a TargetRunner
     * @param workspaceManager the workspaceManager for this runner.
     */
    public TargetRunner(WorkspaceManager workspaceManager) {
        super(workspaceManager);
    }

    /**
     * Set the target being run by this target runner.
     * @param target the target to run.
     * @param targetModule the module in which the target exists.
     */
    protected void setTarget(Target target, ModuleName targetModule) {
        this.target = target;
        this.targetModule = targetModule;
    }
    
    /**
     * @return the target used by this runner
     */
    protected final Target getTarget() {
        return target;
    }
    
    /**
     * @return the output value node.
     */
    protected final ValueNode getOutputValueNode() {
        return outputValueNode;
    }

    /**
     * Get the type checker associated with this target runner.
     * @return TypeChecker the type checker for this target runner.
     */
    final protected TypeChecker getTypeChecker() {
        return getWorkspaceManager().getTypeChecker();
    }

    /**
     * Get the name of the current target module.
     * @return the name of the current target module.
     */
    final protected ModuleName getTargetModule() {
        return targetModule;
    }

    /**
     * Returns a new TypeExpr for the current target, module, and typeChecker.
     * @return TypeExpr a new TypeExpr for the target sc.
     */
    protected final TypeExpr getNewTargetTypeExpr() {
        return getNewTargetTypeExpr(target, targetModule, getTypeChecker());
    }

    /**
     * Returns a new TypeExpr for the target. 
     * @param target Target the target for which to get a new type expression
     * @param moduleName the name of the module in which the graph exists
     * @param typeChecker TypeChecker the type checker to use to generate the type
     * @return TypeExpr a new TypeExpr for the target sc.
     */
    protected static final TypeExpr getNewTargetTypeExpr(Target target, ModuleName moduleName, TypeChecker typeChecker) {
        AdjunctSource scDefinition = target.getTargetDef(null, typeChecker.getTypeCheckInfo(moduleName).getModuleTypeInfo());
        CompilerMessageLogger logger = new MessageLogger ();
        return typeChecker.checkFunction(scDefinition, moduleName, logger);
    }
    
    /**
     * Prepare to perform a test run by compiling the Gem we're to test.
     */
    protected void buildTestProgram(InputPolicy[] inputPolicies, TypeExpr[] argTypes) throws ProgramCompileException {
        entryPoint = null;

        if (inputPolicies == null) {
            inputPolicies = new InputPolicy[0];
        }
        // Get supercombinator defined at the current target and its name
        ModuleTypeInfo currentModuleTypeInfo = getWorkspaceManager().getWorkspace().getMetaModule(getTargetModule()).getTypeInfo();
        AdjunctSource scDef = target.getTargetDef(null, currentModuleTypeInfo);

        CompilerMessageLogger logger = new MessageLogger ();
        TypeExpr targetTypeExpr = getTypeChecker().checkFunction(scDef, targetModule, logger);
        
        if (targetTypeExpr == null) {
            VALUENODE_LOGGER.log(Level.SEVERE, "Error determining gem execution type.  Text: \n" + scDef);
            throw new ProgramCompileException(CompilerMessage.Severity.ERROR, logger.getFirstError());
        }

        // Determine the overall output TypeExpr of the target.
        int numArgs = inputPolicies.length;
        TypeExpr[] targetTypePieces = targetTypeExpr.getTypePieces(numArgs);
                                            
        TypeExpr outputTypeExpr;
        try { 
                               
            TypeExpr[] specializedTargetTypePieces =
                TypeExpr.patternMatchPieces(argTypes, targetTypePieces, currentModuleTypeInfo); 
            outputTypeExpr = specializedTargetTypePieces[numArgs];   
                                    
        } catch (TypeException e) {
            // What to do?  You really don't want to be throwing an uncaught exception here.
            VALUENODE_LOGGER.log(Level.WARNING, "Error determining gem execution output type.");
            e.printStackTrace();
            outputTypeExpr = targetTypePieces[numArgs];
        }
        
        TypeExpr declaredType = outputTypeExpr;
        for (int i = numArgs - 1; i >= 0 ; i--) {
            declaredType = TypeExpr.makeFunType(argTypes[i], declaredType);
        }

        // Update the scDef with the type declaration.
        scDef = target.getTargetDef(declaredType, currentModuleTypeInfo);

        outputValueNode = valueNodeBuilderHelper.getValueNodeForTypeExpr(outputTypeExpr);
        if (outputValueNode == null) {
            // Unable to create an output value node for type: {declaredType}
            throw new ProgramCompileException(CompilerMessage.Severity.ERROR, 
                    new CompilerMessage(new MessageKind.Error.UnableToCreateOutputValueNode(declaredType.toString())));
        }
        
        OutputPolicy outputPolicy = null;
        outputPolicy = outputValueNode.getOutputPolicy();
        if (outputPolicy == null) {
            // Unable to retrieve an output policy for type: {declaredType}
            throw new ProgramCompileException(CompilerMessage.Severity.ERROR, 
                    new CompilerMessage(new MessageKind.Error.UnableToRetrieveAnOutputPolicy(declaredType.toString())));
        }

        if (getShowConsoleInfo()) {
            System.out.println("Executing:\n" + scDef);
        }
        
        // Compile the definition, indicating that this is an adjunct
        QualifiedName targetName = QualifiedName.make(getTargetModule(), getTarget().getTargetName(currentModuleTypeInfo));
        entryPoint = getWorkspaceManager().getCompiler().getEntryPoint(scDef, EntryPointSpec.make(targetName, inputPolicies, outputPolicy), targetName.getModuleName(), logger);
        if (entryPoint == null) {        
            throw new ProgramCompileException(CompilerMessage.Severity.ERROR, logger.getFirstError());
        }
    }

    protected final void executeTestProgram(Object[] rpArguments, boolean threaded) {
        this.entryPointArguments = new Object[rpArguments.length];
        System.arraycopy(rpArguments, 0, this.entryPointArguments, 0, rpArguments.length);
        
        startExecutingTestProgram(threaded);
    }
    
    private void startExecutingTestProgram(boolean threaded) {
        
        // Instantiate a runtime and execute the program
        runtime = createExecutor(getWorkspaceManager(), getWorkspaceManager().makeExecutionContextWithDefaultProperties());
        
        execTimeGen = new StatsGenerator ();
        runtime.addStatsGenerator(execTimeGen);
        
        if (threaded) {
            // Execute in a new thread
            Thread execThread = new Thread(this, "CAL Execution Thread");
            execThread.start();
        } else {
            // Execute in this thread
            run();
        }
    }

    /**
     * Determine if this gem runner is in the midst of execution.
     * @return boolean true if executing (eg. sleeping while waiting for VEPs to be filled in)
     */
    boolean isExecuting() {
        // executing if there's a runtime
        return (runtime != null);
    }

    /**
     * Simply call the executor to run the program
     */
    @Override
    public void run() {    

        // Call any setup methods before entering run state
        enterRunningState();

        error = null;
        executionResult = null;

        long start = System.currentTimeMillis();
        
        try {
            executionResult = runtime.exec(entryPoint, entryPointArguments);
            
        } catch (CALExecutorException e) {
            // Raise an INFO message
            // DIAG
            VALUENODE_LOGGER.log(Level.FINE, "Executor: " + e);
            error = e;
        
            //
            // If some other error occurs, we could display this to the user in a dialog 
            //
            
        } finally {            
            long end = System.currentTimeMillis();
            
            getWorkspaceManager().resetCachedResults(runtime.getContext());

            if (getShowConsoleInfo()) {
                if (execTimeGen != null) {                    
                    for(final StatsGenerator.StatsObject stat : execTimeGen.getStatistics()) {
                        System.out.println (stat.generateShortMessage());
                    }
                } else {
                    System.out.println ("time: " + (end - start));
                }
            }
            
            // Call any cleanup methods to exit the run state
            try {
                exitRunningState();
            } catch (Throwable t) {
                t.printStackTrace();        // what else to do?
            }
            
            // Clear the runtime
            runtime = null;
        }
    }

    /**
     * This method is called when the runtime is about to start evaluating.
     */
    public void enterRunningState() {
    }

    /**
     * This method is called when the runtime is done evaluation.
     * When this method is called, we are still technically in the "running state" (the runtime is still available).
     */
    public void exitRunningState() {
    }

    /**
     * Terminate an execution.  eg. User presses the Stop button.
     */
    protected void stopExecution() {
        // Notify the runtime that it should quit. (quits if the runtime is in the middle of execution)
        if (runtime != null) {
            runtime.requestQuit();
        }
    }

    public final void setShowConsoleInfo (boolean b) {
        showConsoleInfo = b;
    }
    public final boolean getShowConsoleInfo () {
        return showConsoleInfo;
    }

    /** 
     * Returns true if an  error has been encountered running the program.
     */
    @Override
    public boolean isErrorFlagged() {
        return error != null;
    }
}

