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
 * EntryPointGenerator.java
 * Created: Feb 19, 2003 at 12:15:01 PM
 * By: Raymond Cypher
 */
package org.openquark.cal.compiler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openquark.cal.compiler.SourceModel.Parameter;
import org.openquark.cal.compiler.io.EntryPoint;
import org.openquark.cal.compiler.io.EntryPointSpec;
import org.openquark.cal.compiler.io.InputPolicy;
import org.openquark.cal.compiler.io.OutputPolicy;
import org.openquark.cal.internal.machine.EntryPointImpl;
import org.openquark.cal.machine.CodeGenerator;
import org.openquark.cal.machine.Module;
import org.openquark.cal.machine.Program;
import org.openquark.cal.machine.StatusListener;


/**
 * Warning- this class should only be used by the CAL compiler implementation. It is not part of the
 * external API of the CAL platform.
 * <p>
 * This is the EntryPointGenerator class.
 *
 * This class is used to generate entry points for adjuncts. 
 * <p>
 * Created: Feb 19, 2003 at 12:15:01 PM
 * @author Raymond Cypher
 */
public abstract class EntryPointGenerator implements Compiler {

    public static final String SHOW_ADJUNCT_PROP = "org.openquark.cal.show_adjunct";
   
    private final CALCompiler compiler;

    private final Program program;
    
    /** Collection of status listeners */
    private final List<StatusListener> statusListeners = new ArrayList<StatusListener>();
    
    /** Flag used to indicate that all code should be regenerated. */
    private boolean forceCodeRegen;
    
    /** Flag to indicate that the code being compiled will be executing immediately.
     * this allows for optimizations in generating/loading.
     */
    private boolean forImmediateUse;
    
    /**
     * Constructor for EntryPointGenerator.
     * @param program
     */
    protected EntryPointGenerator(Program program) {
        compiler = new CALCompiler();
        
        if (program == null) {
            throw new NullPointerException("Argument 'program' cannot be null.");
        }
        this.program = program;
        
        // makes sure that the CALCompiler has a copy of the packager with the program
        // from the very beginning
        compiler.setPackager(makePackager(program));
    }
    
    /**
     * Accessor so that the CALCompiler can be used by unit tests. Should remain package scope.  
     */
    CALCompiler getCompiler() {
        return compiler;
    }
    
    abstract protected Packager makePackager(Program program);
    
    abstract protected CodeGenerator makeCodeGenerator();

    private final CompilerMessage.Severity compileAdjunct(AdjunctSource adjunctSource, ModuleName adjunctModule, CompilerMessageLogger logger) {

        // Use a compiler-specific logger.
        CompilerMessageLogger compilerLogger = new MessageLogger(true);
        
        compiler.setCompilerMessageLogger(compilerLogger);

        Packager pkgr = makePackager(program);

        for (final StatusListener sl : statusListeners) {          
            pkgr.addStatusListener(sl);
        }

        try {
            // Compile it, indicating that this is an adjunct
            compiler.compileAdjunct(adjunctSource, pkgr, adjunctModule);

            compiler.setCompilerMessageLogger(null);

            if (compilerLogger.getNErrors() > 0) {
                // Errors
                return compilerLogger.getMaxSeverity();
            }

            // Now we want to generate code for the program.
            boolean bfor = forImmediateUse;
            forImmediateUse = true;

            CodeGenerator cg = makeCodeGenerator();

            forImmediateUse = bfor;

            Module module = program.getModule(adjunctModule);
            cg.generateSCCode(module, compilerLogger);

            cg.finishedGeneratingCode(compilerLogger);
        
        } catch (CompilerMessage.AbortCompilation e) {
            // Aborted compilation.
        
        } finally {
            // Log any messages from the compiler-specific logger.
            logger.logMessages(compilerLogger);
        }

        return compilerLogger.getMaxSeverity();
    }
    
    /**
     * Generate an entry point for the entryPointSpec.
     * @param entryPointSpec - Information about the entry point.
     * @param currentModule - name of the current module
     * @param logger
     * @return an EntryPoint
     * @see EntryPointSpec
     */
    public final EntryPoint getEntryPoint (EntryPointSpec entryPointSpec, ModuleName currentModule, CompilerMessageLogger logger) {
        if (entryPointSpec == null  || currentModule == null) {
            throw new IllegalArgumentException("Invalid argument in EntryPointGenerator.(EntryPointSpec EntryPointSpec, String currentModule, CompilerMessageLogger logger).");
        }
        
        List<EntryPoint> entryPoints = getEntryPoints( Collections.singletonList(entryPointSpec), currentModule, logger);

        if (entryPoints == null) {
            return null;
        }
        return entryPoints.get(0);
    }

    /**
     * Generate a list of entry points, one for each of the input entryPointSpecs
     * @param entryPointSpecs - list of EntryPointSpecs
     * @param currentModule - name of the current module
     * @param logger
     * @return a list of EntryPoints
     */
    public final List<EntryPoint> getEntryPoints (List<EntryPointSpec> entryPointSpecs, ModuleName currentModule, CompilerMessageLogger logger) {
        if (entryPointSpecs == null || entryPointSpecs.size() == 0 || currentModule == null) {
            throw new IllegalArgumentException ("Invalid argument in EntryPointGenerator.getEntryPoint(List targetInfo, String currentModule, CompilerMessageLogger logger)");
        }
        
        return getEntryPointsFromEntryPointSpecs (entryPointSpecs, currentModule, logger);
    }
        
    /**
     * Generate an entry point for each of the entry point specs
     * @param entryPointSpecs - A list of EntryPointSpec.
     * @param currentModule - name of the current module
     * @param logger
     * @return a list of EntryPoint
     */
    private final List<EntryPoint> getEntryPointsFromEntryPointSpecs (List<EntryPointSpec> entryPointSpecs, ModuleName currentModule, CompilerMessageLogger logger) {
        Module cmod = program.getModule(currentModule);
        ArrayList<SourceModel.FunctionDefn.Algebraic> adjunctSource = new ArrayList<SourceModel.FunctionDefn.Algebraic>();
        int targetCount = 0;
        ArrayList<EntryPointSpec> adjuntEntryPointSpecs = new ArrayList<EntryPointSpec> (entryPointSpecs.size());
        
        for (final EntryPointSpec entryPointSpec : entryPointSpecs) {
            
            String adjunctName = "rt_" + entryPointSpec.getFunctionalAgentName().getUnqualifiedName() + "_" + (targetCount++) + "_";
            String baseAdjunctName = adjunctName;
            int i = 0;
            while (cmod.getFunction(adjunctName) != null) {
                adjunctName = baseAdjunctName  + (i++);
            }
                        
            adjunctSource.add(
                SourceModel.FunctionDefn.Algebraic.make(
                    adjunctName, Scope.PRIVATE, null, SourceModel.Expr.makeGemCall(entryPointSpec.getFunctionalAgentName())));
            
            adjuntEntryPointSpecs.add (EntryPointSpec.make(QualifiedName.make(currentModule, adjunctName), entryPointSpec.getInputPolicies(), entryPointSpec.getOutputPolicy()));
        }
        
        return generateEntryPoints(
            adjuntEntryPointSpecs,
            new AdjunctSource.FromSourceModel(
                adjunctSource.toArray(new SourceModel.FunctionDefn[0])),
            currentModule,
            logger);
    }

    /**
     * {@inheritDoc}
     */
    public final EntryPoint getEntryPoint (AdjunctSource adjunctSource, EntryPointSpec entryPointSpec, ModuleName targetModuleName, CompilerMessageLogger logger) {
        if (entryPointSpec == null || adjunctSource == null) {
            throw new IllegalArgumentException("Invalid argument in EntryPointGenerator.getEntryPoint (AdjunctSource adjunctSource, EntryPointSpec entryPointSpec, String targetModuleName, CompilerMessageLogger logger).");
        }
        
        List<EntryPoint> entryPoints = getEntryPoints (adjunctSource, 
                                           Collections.singletonList(entryPointSpec), 
                                           targetModuleName, logger);
        if (entryPoints == null) {
            return null; 
        }
        return entryPoints.get(0);
    }
    
    /**
     * {@inheritDoc}
     */
    public final List<EntryPoint> getEntryPoints (AdjunctSource adjunctSource, List<EntryPointSpec> entryPointSpecs, ModuleName targetModuleName, CompilerMessageLogger logger) {
        if (adjunctSource == null || entryPointSpecs == null || entryPointSpecs.size() == 0 || targetModuleName == null) {
            throw new IllegalArgumentException("Invalid argument in EntryPointGenerator.getEntryPoint (AdjunctSource adjunctSource, List info, String targetModuleName, CompilerMessageLogger logger).");
        }
        
        return generateEntryPoints (entryPointSpecs, adjunctSource, targetModuleName, logger);// getEntryPointsHelper (adjunctSource, entryPointSpecs, targetModuleName, logger);
    }
    
    private final List<EntryPoint> generateEntryPoints (List<EntryPointSpec> entryPointSpecs, AdjunctSource adjunctSource, ModuleName targetModuleName, CompilerMessageLogger logger) {
        compiler.setCompilerMessageLogger(logger);
        if (logger == null) {
            logger = compiler.getMessageLogger();
        }
        
        List<EntryPoint> entryPoints = new ArrayList<EntryPoint>(entryPointSpecs.size()); 
        AdjunctAugmenter aug = new AdjunctAugmenter();
        for (final EntryPointSpec entryPointSpec : entryPointSpecs) {            
            AugmentedAdjunct aa = aug.augmentAdjunct (entryPointSpec, adjunctSource);
            
            if (aa == null || aa.getEntryPoint() == null || aa.getSource() == null) {
                return null;
            }
            
            adjunctSource = aa.getSource();
            entryPoints.add (aa.getEntryPoint());
        }
                   
        CompilerMessage.Severity sev = compileAdjunct (adjunctSource, targetModuleName, logger);
        
        if (sev.compareTo(CompilerMessage.Severity.ERROR) >= 0) {
            return null;
        }
        
        return entryPoints;
    }
    
    /**
     * Add a status listener.
     * @param listener StatusListener
     */
    public final void addStatusListener(StatusListener listener) {
        if (!statusListeners.contains(listener)) {
            statusListeners.add(listener);
        }
    }

    /**
     * Remove a status listener.
     * @param listener StatusListener
     */
    public final void removeStatusListener(StatusListener listener) {
        statusListeners.remove(listener);
    }

    /**
     * {@inheritDoc}
     */
    public final void setForceCodeRegen (boolean b) {
        forceCodeRegen = b;
    }
    
    /**
     * {@inheritDoc}
     */
    public final boolean getForceCodeRegen () {
        return forceCodeRegen;
    }
    
    /**
     * {@inheritDoc}
     */
    public final void setForImmediateUse (boolean b) {
        forImmediateUse = b;
    }
    
    /**
     * {@inheritDoc}
     */
    public final boolean isForImmediateUse() {
        return forImmediateUse;
    }      

    /**
     * @return an iterator over the registered status listeners.
     */
    protected final List<StatusListener> getStatusListeners () {
        return statusListeners;
    }
    
    private final class AdjunctAugmenter {
        int adjunctCount = 0;
        
        /**
         * Augment the adjunct source with marshalers and Inputable/Outputable instances.
         * @param entryPointSpec - The information about the run target.
         * @param adjunctSource - The source for the adjunct.
         * @return An AugmentedAdjunct. i.e. Augmented source and a EntryPoint
         */
        public AugmentedAdjunct augmentAdjunct (EntryPointSpec entryPointSpec, AdjunctSource adjunctSource){
            
            QualifiedName adjunctTargetName = entryPointSpec.getFunctionalAgentName();
            OutputPolicy outputPolicy = entryPointSpec.getOutputPolicy();
            InputPolicy[] inputPolicies = entryPointSpec.getInputPolicies();
            
            
            if (adjunctTargetName == null || adjunctSource == null || outputPolicy == null) {
                throw new IllegalArgumentException ("Null argument in AdjunctAugmenter.augmentAdjunct.");
            }

            //if the input policies are null - create an array of default input policies
            if (inputPolicies == null) {
                // get the arity of the target function
                CALTypeChecker.AdjunctInfo adjunctInfo = compiler.getTypeChecker().checkAdjunct(adjunctSource, adjunctTargetName.getModuleName(), adjunctTargetName.getUnqualifiedName());
                if (adjunctInfo == null || adjunctInfo.getTargetType() == null) {
                    return null;
                }
                TypeExpr targetType = adjunctInfo.getTargetType();
                
                inputPolicies = new InputPolicy[targetType.getArity()];
                
                for(int i=0; i<inputPolicies.length; i++) {
                    inputPolicies[i] = InputPolicy.DEFAULT_INPUT_POLICY;
                }

            }             
            
            // Add a function which will be the new execution target.  This function 
            // marshals the input arguments and then calls the original target functionalAgent,
            // and finally marshals the output. 
            QualifiedName policyTargetName = generateIOPolicyAdjunctName(adjunctTargetName, adjunctTargetName.getModuleName());
            AdjunctSource target = generateIOPolicyAdjunctSource(adjunctTargetName, policyTargetName, outputPolicy, inputPolicies); 
            if (target == null) {
                return null;
            }
            adjunctSource = adjunctSource.concat(target);
            
            if (System.getProperty(SHOW_ADJUNCT_PROP) != null) {
                System.out.println ("\n\nAugmented Adjunct:\n" + adjunctSource + "\n\n");
            }
                            
            return new AugmentedAdjunct (adjunctSource, new EntryPointImpl (policyTargetName));
        }

    
        /**
         * Given a functional agent name (i.e. a function, data constructor or class method) and a target module name generate a name for 
         * a corresponding hidden adjunct in the target module.
         * @param functionalAgentToRun
         * @param targetModuleName
         * @return The new function name.
         */
        private QualifiedName generateIOPolicyAdjunctName (QualifiedName functionalAgentToRun, ModuleName targetModuleName) {
        
            Module targetModule = program.getModule(targetModuleName);    
            String baseName = "io_" + functionalAgentToRun.getUnqualifiedName() + "_";
            int z = 0;
            while (targetModule.getModuleTypeInfo().getFunctionalAgent(baseName + z) != null) {
                z++;
            }
            String targetName = baseName + z + "_" + adjunctCount++;
            return QualifiedName.make(targetModuleName, targetName);
        }


        /**
         * Generate 'wrapper' that marshals arguments from java to CAL and calls
         * the client supplied functionalAgent.
         * @param functionalAgentName - name of the client supplied functionalAgent
         * @param ioAdjunctQN - name of the generated IO policy wrapper
         * @param outputPolicy - output policy for the result
         * @param inputPolicies - input policies for the inputs
         * @return The adjunct source of the wrapper function.
         */
        private AdjunctSource generateIOPolicyAdjunctSource (QualifiedName functionalAgentName, QualifiedName ioAdjunctQN, OutputPolicy outputPolicy, InputPolicy[] inputPolicies) {            
            //this creates a source model function of the following form:
            // ioAdjunctQN arg0 arg1 ... = outputPolicy.marshaler ( functionalAgentName (inputPolicy1.marshaler arg0 arg1 ... argN) (inputPolicy1.marshaler argN+1 ...) ... )
            
            List/*SourceModel.Parameter*/<Parameter> ioPolicyParams= new ArrayList<Parameter>();
            SourceModel.Expr[] args= new SourceModel.Expr[inputPolicies.length];

            //this counter is used to number all the arguments - normally each input policy has 1 argument
            //but more complex input polices can have 0 or more arguments.
            int policyArgCount = 0;
            
            //create an application for each input policy
            for(int i=0; i < args.length; i++) {
                InputPolicy inputPolicy = inputPolicies[i];
                int nArgs = inputPolicy.getNArguments();
                SourceModel.Expr[] inputPolicyParts = new SourceModel.Expr[inputPolicy.getNArguments() + 1];
                inputPolicyParts[0] = inputPolicy.getMarshaler();
                
                //this loop is from 1 as the first element is always the inputPolicy itself, and then any arguments
                for (int j = 1; j <= nArgs; ++j) {
                    String argName = "arg_" + (policyArgCount);
                    ioPolicyParams.add(SourceModel.Parameter.make(argName, false));                    
                    policyArgCount++;
                    
                    inputPolicyParts[j] =  SourceModel.Expr.Var.makeUnqualified(argName);                        
                }
                
                if (inputPolicyParts.length >= 2) {
                    args[i] = SourceModel.Expr.Application.make(inputPolicyParts);
                } else {
                    args[i] = inputPolicyParts[0];
                }
            }
            
            SourceModel.Expr ioPolicyExpression = SourceModel.Expr.makeGemCall(functionalAgentName, args);
            
            //create an application for the output policy if non-null
            if (outputPolicy != null) {
                ioPolicyExpression = SourceModel.Expr.Application.make(new SourceModel.Expr[] {outputPolicy.getMarshaler(), ioPolicyExpression});
            }
            
            //create the function definition
            return new AdjunctSource.FromSourceModel( 
                SourceModel.FunctionDefn.Algebraic.make(ioAdjunctQN.getUnqualifiedName(), Scope.PRIVATE, ioPolicyParams.toArray(new SourceModel.Parameter[ioPolicyParams.size()]), ioPolicyExpression));            
        }
        
    }

    /**
     * A class to encapsulate the source of an augmented adjunct
     * (i.e. an adjunct with additional marshaling, etc) and
     * the associated EntryPoint.
     * 
     * Created: Mar 19, 2004
     * @author RCypher
     */    
    private static final class AugmentedAdjunct {
        /** The augmented adjunct source. */
        private final AdjunctSource source;
        
        /** The EntryPoint associated with the target in the adjunct. */
        private final EntryPoint entryPoint;
        
        public AugmentedAdjunct (AdjunctSource source, EntryPoint entryPoint) {
            this.source = source;
            this.entryPoint = entryPoint;
        }
        
        public AdjunctSource getSource () {
            return source;
        }
        
        public EntryPoint getEntryPoint () {
            return entryPoint;
        }
    }
    
}
