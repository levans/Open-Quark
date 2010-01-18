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
 * ConsoleRunner.java
 * Created: Aug 21, 2007
 * By: Edward Lam
 */

package org.openquark.cal;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openquark.cal.compiler.AdjunctSource;
import org.openquark.cal.compiler.CodeAnalyser;
import org.openquark.cal.compiler.CompilerMessage;
import org.openquark.cal.compiler.CompilerMessageLogger;
import org.openquark.cal.compiler.MessageLogger;
import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.ModuleTypeInfo;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.io.EntryPoint;
import org.openquark.cal.compiler.io.EntryPointSpec;
import org.openquark.cal.compiler.io.InputPolicy;
import org.openquark.cal.compiler.io.OutputPolicy;
import org.openquark.cal.internal.module.Cal.Core.CAL_Exception_internal;
import org.openquark.cal.machine.CALExecutor;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.runtime.CALExecutorException;
import org.openquark.cal.services.ProgramModelManager;


/**
 * Simple abstract class for clients which have a program in memory 
 * and would like to compile a run an adjunct against it and output the result to a logger as text.
 * 
 * @author Edward Lam
 */
public abstract class ConsoleRunner {

    /** An instance of a Logger for cal messages. */
    protected final Logger calLogger;

    /** The runtime instance. */
    private CALExecutor runtime = null;
    private final byte[] runtimeAccessLock = new byte[0];     // used to synchronize access to the runtime field.
    private volatile boolean runtimeIsRunning = false;
    
    /**
     * @param calLogger the logger connected to the console.
     */
    protected ConsoleRunner(Logger calLogger) {
        this.calLogger = calLogger;
    }
    
    /**
     * Terminate execution of the currently running expression if any.
     */
    public void terminateExecution() {
        synchronized (runtimeAccessLock) {
            if (runtimeIsRunning) {
                runtime.requestQuit();
            }
        }
    }
    
    /**
     * @return the associated programModelManager for querying and compilation
     */
    public abstract ProgramModelManager getProgramModelManager();
    
    /** @return whether the source for a module with the given module name is part of the compilable definitions for this runner.
     *  This includes any sources which have compilation errors and are therefore not part of the ProgramModelManager's program.
     * */
    public abstract boolean hasModuleSource(ModuleName moduleName);
    
    /**
     * Run a cal expression with the given text.
     * @param moduleName the name of the module in which to run the expression.
     * @param expressionText the text of the expression to run.
     * @param qualifyExpressionText if true, an attempt will be made to qualify unqualified symbols in expressionText before it is run.
     * False to just run expressionText without attempting qualification.
     * @return whether execution terminated normally.
     */
    public boolean runExpression(ModuleName moduleName, String expressionText, boolean qualifyExpressionText) {
        ModuleTypeInfo moduleTypeInfo = getProgramModelManager().getModuleTypeInfo(moduleName);

        if (moduleTypeInfo == null) {
            if (hasModuleSource(moduleName)) {
                calLogger.severe("Error: Module \"" + moduleName + "\" has compilation errors (or is currently in the process of being compiled).");
            } else {
                calLogger.severe("Error: Module \"" + moduleName + "\" could not be found.");
            }
            return false;
        }

        String maybeQualifiedExpressionText;
        if (qualifyExpressionText) {
            maybeQualifiedExpressionText = qualifyCodeExpression(moduleName, expressionText);
            if (maybeQualifiedExpressionText == null) {
                return false;
            }
        
        } else {
            maybeQualifiedExpressionText = expressionText;
        }

        // pick a name which doesn't exist in the module.
        String targetName = "target";
        int index = 1;
        while (moduleTypeInfo.getFunctionalAgent(targetName) != null) {
            targetName = "target" + index;
            index++;
        }
        EntryPoint entryPoint = compileAdjunct(QualifiedName.make(moduleName, targetName), maybeQualifiedExpressionText);

        if (entryPoint == null) {
            return false;
        }

        calLogger.info("Running: " + expressionText);
        return runTarget(entryPoint);
    }

    /**
     * Qualify unqualified symbols in the given cal expression.
     * @param moduleName the name of the module with respect to which qualification will be performed.
     * @param expressionText the text of the cal expression to qualify
     * @return the qualified code expression, if qualification was performed successfully.  Null if qualification failed.
     */
    public String qualifyCodeExpression(ModuleName moduleName, String expressionText) {
        // Qualify unqualified symbols in code, if unambiguous
        CompilerMessageLogger logger = new MessageLogger();
        CodeAnalyser analyser = new CodeAnalyser(getProgramModelManager().getTypeChecker(), getProgramModelManager().getModuleTypeInfo(moduleName), false, false);
        CodeAnalyser.QualificationResults qualificationResults = analyser.qualifyExpression(expressionText, null, null, logger, true);
        
        if (qualificationResults == null) {
            calLogger.severe("Attempt to qualify expression has failed because of errors: ");
            for (final CompilerMessage message : logger.getCompilerMessages()) {
                calLogger.info("  " + message.toString());
            }
            return null;
        }
        return qualificationResults.getQualifiedCode();
    }

    /**
     * Discard cached CAF results for a given module or the entire program.
     * @param moduleName the name of the module for which the cached CAF results will be discarded.
     * Null to discard for the entire program.
     */
    public void command_resetCachedResults(ModuleName moduleName) {
        if (runtime != null) {
            if (moduleName == null) {
                // Reset cached results in all modules.
                calLogger.info("Resetting cached CAF results in program.");
                getProgramModelManager().resetCachedResults(runtime.getContext());
            
            } else {
                if (getProgramModelManager().getModule(moduleName) == null) {
                    calLogger.info("Module \"" + moduleName + "\" is not in the program.");
                } else {
                    calLogger.info("Resetting cached CAF results in module " + moduleName + ".");
                    getProgramModelManager().resetCachedResults(moduleName, runtime.getContext());
                }
            } 
        }
    }

    /**
     * @param entryPoint the entry point of the target to run.
     * @return whether execution terminated normally.
     */
    private boolean runTarget(EntryPoint entryPoint) {
        synchronized (runtimeAccessLock) {
            if (runtime == null) {
                runtime = getProgramModelManager().makeExecutorWithNewContextAndDefaultProperties();
            }
            runtimeIsRunning = true;
        }

        long startExec = System.currentTimeMillis();

        CALExecutorException error = null;
        Object result = null;
        try {
            result = runtime.exec(entryPoint, new Object[]{});
        
        } catch (CALExecutorException e) {
            calLogger.log(Level.SEVERE, "Error while executing.", e);
            error = e;
        
        } catch (Exception e) {
            calLogger.severe("Unable to execute due to an internal error.  Please contact Business Objects.  " + e.toString ());
            return false;

        } catch (Error e){
            calLogger.severe("Unable to execute due to an internal error.  Please contact Business Objects.  " + e.toString ());
            return false;

        } finally {
            synchronized (runtimeAccessLock) {
                runtimeIsRunning = true;
            }
        }

        long endExec = System.currentTimeMillis();
        calLogger.fine("Finished execution in " + (endExec - startExec) + " ms\n");

        showOutput (result, error);
        
        return result != null && error == null;
    }

    /**
     * Display the output results.
     * @param result the result object.
     * @param error the CALExecutorException if any, or null if none.
     */
    private void showOutput(Object result, CALExecutorException error) {
        try {
            final boolean displayOutput = true;
            
            // Determine the message

            if (error == null) {
                if (displayOutput) {
                    if (result == null) {
                        calLogger.severe("No results to display.");
                    } else {
                        calLogger.info("Output:");
                        calLogger.info(result.toString());
                    }
                }

            } else {
                String messageTitle;
                String message = "No results to display.";
                
                CALExecutorException.Type resultStatus = error.getExceptionType();
                if (resultStatus == CALExecutorException.Type.ERROR_FUNCTION_CALL) {
                    messageTitle = CAL_Prelude.Functions.error.getQualifiedName() + " called:";
                    message = error.getLocalizedMessage();
                } else if (resultStatus == CALExecutorException.Type.PATTERN_MATCH_FAILURE) {
                    messageTitle = "Error during pattern matching:";
                    message = error.getLocalizedMessage();
                } else if (resultStatus == CALExecutorException.Type.PRIM_THROW_FUNCTION_CALLED) {
                    messageTitle = CAL_Exception_internal.Functions.primThrow.getQualifiedName() + " called:";
                    message = error.getLocalizedMessage();
                } else if (resultStatus == CALExecutorException.Type.FOREIGN_OR_PRIMITIVE_FUNCTION_EXCEPTION) {
                    messageTitle = "Error during foreign or primitive function call.";
                    message = error.getLocalizedMessage();
                } else if (resultStatus == CALExecutorException.Type.INTERNAL_RUNTIME_ERROR) {
                    messageTitle = "Internal runtime error.";
                    message = error.getLocalizedMessage();
                } else if (resultStatus == CALExecutorException.Type.USER_TERMINATED) {
                    messageTitle = "User terminated.";
                } else {
                    messageTitle = "Unknown result state.";
                }

                calLogger.severe(messageTitle);
                calLogger.severe(message);
            }

        } catch (NullPointerException e) {
            // No results!
            calLogger.severe("No results to display.");
        }
    }

    /**
     * Create a EntryPoint for the given function definition.
     * @param qualifiedTargetName the name of the target (defined in functionDefn) for which the entry point will be generated.
     * @param functionBodyText the text of the body of the definition.
     * @return a corresponding entry point, or null if there was a problem compiling the definition.
     */
    private EntryPoint compileAdjunct(QualifiedName qualifiedTargetName, String functionBodyText) {
        String unqualifiedTargetName = qualifiedTargetName.getUnqualifiedName();
        String scDef = unqualifiedTargetName + " = \n" + functionBodyText + "\n;";
        CompilerMessageLogger ml = new MessageLogger ();

        calLogger.fine("Compiling adjunct..");
        ModuleName targetModule = qualifiedTargetName.getModuleName();

        // Compile it, indicating that this is an adjunct
        EntryPoint targetEntryPoint = 
            getProgramModelManager().getCompiler().getEntryPoint(
                    new AdjunctSource.FromText(scDef), 
                    EntryPointSpec.make(QualifiedName.make(targetModule, unqualifiedTargetName), new InputPolicy[]{}, OutputPolicy.DEFAULT_OUTPUT_POLICY), 
                    targetModule, 
                    ml);

        writeOutCompileResult(ml);

        return targetEntryPoint;
    }

    
    /**
     * A helper method to log the results of compilation to the console logger.
     * @param messageLogger the logger containing the messages from compilation.
     */
    protected final void writeOutCompileResult(CompilerMessageLogger messageLogger) {
        
        if (messageLogger.getMaxSeverity().compareTo(CompilerMessage.Severity.ERROR) >= 0) {
            // Errors
            calLogger.severe("CAL: Compilation unsuccessful because of errors:");
        
        } else {
            // Compilation successful
            calLogger.finer("CAL: Compilation successful");
        }

        for (final CompilerMessage message : messageLogger.getCompilerMessages()) {
            Level messageLevel;
            if (message.getSeverity().compareTo(CompilerMessage.Severity.ERROR) >= 0) {
                messageLevel = Level.SEVERE;
            
            } else if (message.getSeverity().compareTo(CompilerMessage.Severity.WARNING) >= 0) {
                messageLevel = Level.WARNING;

            } else {
                messageLevel = Level.FINE;
            }
            
            calLogger.log(messageLevel, "  " + message.toString());
        }
    }

}
