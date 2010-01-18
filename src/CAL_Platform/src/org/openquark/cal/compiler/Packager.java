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
 * Packager.java
 * Creation date: (March 14th 2000 7:32:25 PM)
 * By: Luke Evans
 */
package org.openquark.cal.compiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openquark.cal.compiler.ExpressionAnalyzer.Visitor;
import org.openquark.cal.compiler.io.EntryPoint;
import org.openquark.cal.compiler.io.EntryPointSpec;
import org.openquark.cal.foreignsupport.module.Prelude.AlgebraicValue;
import org.openquark.cal.internal.machine.MachineFunctionImpl;
import org.openquark.cal.internal.module.Cal.Internal.CAL_Optimizer;
import org.openquark.cal.internal.module.Cal.Internal.CAL_Optimizer_internal;
import org.openquark.cal.machine.CALExecutor;
import org.openquark.cal.machine.CodeGenerator;
import org.openquark.cal.machine.MachineFunction;
import org.openquark.cal.machine.Module;
import org.openquark.cal.machine.Program;
import org.openquark.cal.machine.StatusListener;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.runtime.CALExecutorException;
import org.openquark.cal.runtime.ExecutionContext;
import org.openquark.cal.services.BasicCALServices;
import org.openquark.cal.services.WorkspaceManager;
import org.openquark.util.UnsafeCast;


/**
 * Warning- this class should only be used by the CAL compiler implementation. It is not part of the
 * external API of the CAL platform.
 * <P>
 * The Packager currently is used to instrument analysis phases that occur after the compiler
 * generates Expression values for the core functions in a module. This includes applying the global CAL optimizer,
 * anti-alias transformations (and other optimizations done in the ExpressionAnalyzer, such as simple inlining of a
 * few well known functions). Its end output can be used by the machines for code generation.
 * @author LWE
 */
public abstract class Packager {

    public static final String OPTIMIZER_LEVEL = "org.openquark.cal.optimizer_level";
    
    /** Percentage of process completed by packaging a module **/
    private double moduleIncrement = 0.5;
    
    /**
     * Exception raised on a packaging error
     */
    public static final class PackagerException extends Exception {
       
        private static final long serialVersionUID = 4401713179185214061L;

        /**
         * Construct a PackagerException from a message
         */
        public PackagerException(String message) {
            super(message);
        }
    }
    
    private Module currentModule;
    /**
     * If this flag is false the inliner will not be used no matter what the client requests. Otherwise 
     * this inliner will be used if requested. This is temporary until I add a command line flag.
     */
    private static int optimizerLevel = 0;
    {
        String ol = System.getProperty(OPTIMIZER_LEVEL);
        if (ol == null){
            // leave it at zero
        }
        else{
            // Let the error propagate up. 
            optimizerLevel = Integer.parseInt(ol);
        }        
    }
    
    /**
     * This is needed to access functions defined in the current module since 
     * the .cmi file is not yet created.
     * 
     * QualifiedName -> CoreFunction
     */
    
    private Map<QualifiedName, CoreFunction> inlinableFunctions = new HashMap<QualifiedName, CoreFunction>();

    private final Program program;
    private final CodeGenerator codeGenerator;
    private final Set<StatusListener> statusListeners = new HashSet<StatusListener>();
    /**
     * The CAL optimizer.
     */
    private CALExecutor optimizer_executor = null;

    private EntryPoint optimizer_entryPoint = null;

    /**
     * Construct a RunPackager which packages directly to a Program then executes it.
     * @param program Program. 
     */
    public Packager(Program program, CodeGenerator cg) {
        this.program = program;
        this.codeGenerator = cg;
        if (program.useOptimizer()) {
            useOptimizer();
        }
    }
        
    /**
     * Abort this package.
     * @param logger the logger to use for logging error messages.
     */
    public void abort(CompilerMessageLogger logger) {
        // Wait for the code generator to finish any file i/o.
        codeGenerator.finishedGeneratingCode(logger);
    }
    
    /**
     * Close the package.
     * @param logger the logger to use for logging error messages.
     */
    public void close(CompilerMessageLogger logger) throws PackagerException {
        // Wait for the code generator to finish any file i/o.
        codeGenerator.finishedGeneratingCode(logger);
    }
    
    /**
     * Gets the ModuleTypeInfo for modules that have already been wrapped by the Packager. Returns null
     * if the ModuleTypeInfo doesn't exist.
     * @return ModuleTypeInfo
     * @param moduleName
     */
    public ModuleTypeInfo getModuleTypeInfo(ModuleName moduleName) {

        Module requestedModule = program.getModule(moduleName);
        if (requestedModule == null) {
            return null;
        }

        return requestedModule.getModuleTypeInfo();
    }
    
    /**
     * Create a new module object into which future 'stores' will place program objects.
     * @param foreignClassLoader the classloader to use to resolve foreign classes for the module.
     */
    public void newModule(ModuleName name, ClassLoader foreignClassLoader) throws PackagerException {
        
        // Add it to the Program
        program.addModule(name, foreignClassLoader);
        
        for (final StatusListener l : statusListeners) {
            l.setModuleStatus(StatusListener.SM_NEWMODULE, name);
            l.incrementCompleted(moduleIncrement);
        }
    }

    /**
     * Adds an existing module to the program.
     * Used by the serialization code to set up the packager
     * since the compiler accesses module type info via the packager.
     * @param m
     */
    void addModule(Module m) {
        program.addModule(m);
        
        // This module has been successfully comiled/packaged.  Update the associated timestamp in
        // the program.  This prevents re-compilation if the module hasn't changed.
        program.putCompilationTimestamp(m.getName(), new Long(System.currentTimeMillis()));
        // Inform any status listeners that this module is now loaded.
        for (final StatusListener statusListener : statusListeners) {
            statusListener.setModuleStatus(StatusListener.SM_LOADED, m.getName());
        }
    }
    
    /**
     * Store this core function in the package into the current module.
     * @param coreFunction - the function to be stored
     * @throws PackagerException
     */
    public void store(CoreFunction coreFunction) throws PackagerException {
        // save function info for inlining.
        if (optimizerLevel > 0) {
            inlinableFunctions.put(coreFunction.getName(), coreFunction);
        }
        storeItem(coreFunction);
    }

    /**
     * Store this core function in the package into the current module.
     * @param coreFunction
     * @throws Packager.PackagerException
     */   
    private void storeItem(CoreFunction coreFunction) throws Packager.PackagerException {
        
         // Save code objects (currently only one for a supercombinator - labels may refer to offsets)
         try {
             coreFunction.setForAdjunct(codeGenerator.isForAdjunct());
             // At this point we want to package the expression form of the super combinator, along
             // with a description of the initial environment (i.e. parameters).
             MachineFunction machineFunction = getMachineFunction(coreFunction);
             getCurrentModule().addFunction (machineFunction);

             for (final StatusListener l : getStatusListeners()) {
                 l.setEntityStatus(StatusListener.SM_COMPILED, machineFunction.getName());
             }
            
         } catch (NullPointerException e) {
             // No module to copy into (or something worse)
             throw new Packager.PackagerException("No active module in which to store the core function " + coreFunction);
         }
     }
     
    /**
     * @param coreFunction a machine-independent representation of the function.
     * @return the machine-specific implementation for the core function
     */    
    public abstract MachineFunction getMachineFunction(CoreFunction coreFunction);

    /**
     * Switches the current module to be the specified module. 
     * The module must have already been added to the package.
     * @param name the name of the module to switch to
     */
    public void switchModule(ModuleName name) throws PackagerException {
        currentModule = program.getModule(name);
        if (currentModule == null) {
            throw new PackagerException("switchModule: module " + name + " does not exist.");
        }
    }
    
    /**
     * Return the compiled program object.
     */
    public Program getProgram() {
        return program;
    }


    /**
     * The packager will load the optimizer so that for all modules subsequently
     * compiled the optimizer will be run.
     */

    public void useOptimizer() {
        if (optimizerLevel < 1) {
            return;
        }


        final String defaultWorkspaceFile = "cal.optimizer.cws";

        BasicCALServices calServices = BasicCALServices.make(defaultWorkspaceFile);
        calServices.getWorkspaceManager().useOptimizer(false);
        CompilerMessageLogger messageLogger = new MessageLogger();
        if (!calServices.compileWorkspace(null, messageLogger)) {
            System.err.println(messageLogger.toString());
        }

        final ExecutionContext executionContext = calServices.getWorkspaceManager().makeExecutionContextWithDefaultProperties();

        WorkspaceManager workspaceManager = calServices.getWorkspaceManager();
        Compiler compiler = calServices.getCompiler();
        optimizer_entryPoint = compiler.getEntryPoint(
                EntryPointSpec.make(CAL_Optimizer_internal.Functions.optimize), 
                CAL_Optimizer.MODULE_NAME, messageLogger);
        if (messageLogger.getNMessages() > 0) {
            System.err.println(messageLogger.toString());
        }
        optimizer_executor = workspaceManager.makeExecutor(executionContext);
        
        // Initialize preludeTypeConstants
        {
            ModuleTypeInfo moduleTypeInfo = workspaceManager.getModuleTypeInfo(CAL_Prelude.MODULE_NAME);
            preludeTypeConstants = new PreludeTypeConstants(moduleTypeInfo);
            OptimizerHelper.setPreludeTypeConstants(preludeTypeConstants);
        }    
    }

    /**
     * Return the current module.
     * @return Module
     */
    public Module getCurrentModule() {
        return currentModule;
    }
    
    public Set<StatusListener> getStatusListeners() {
        return statusListeners;    
    }
            
    public void addStatusListener (StatusListener listener) {
        if (!statusListeners.contains (listener)) {
            statusListeners.add (listener);
        }
    }
    
    public void removeStatusListener (StatusListener listener) {
        statusListeners.remove (listener);
    }
    
    public void setModuleIncrement (double d) {
        moduleIncrement = d;
   }
   
   /**
    * Determine which functions can be treated as alias to another function.
    * e.g. foo x y = blah x y;   foo is an alias of blah.
    * @param functionsList
    */
   private void determineFunctionAliases (List<MachineFunction> functionsList) {
       // The first step is to determine aliases based on function definitions
       // for all the code labels.
       // Once this is done we can do a deep alias resolution.  i.e. handle cases
       // like a is an alias of b is an alias of c.
       // Once the deep alias resolution is done we can check for incompatibilities
       // caused by argument strictness.
       
       // Step 1.  Set aliasOf members based on function definitions.
       Iterator<MachineFunction> functions = functionsList.iterator();
       while (functions.hasNext()) {
           MachineFunction mf = functions.next();
           // Get the name of the aliased function. (may be null)
           QualifiedName aliasOf = aliasOf (mf); 
           mf.setAliasOf(aliasOf);
       }

       // Steps 2 and 3.  Do deep resolution of aliases and
       // check strictness compatability.
       functions = functionsList.iterator();
       while (functions.hasNext()) {
           MachineFunction mf = functions.next();

           // Get the name of the aliased function. (may be null)
           QualifiedName aliasOf = compatibleStrictnessWithAliasedFunction(mf);
           
           if (aliasOf != null) {
               // Follow any alias chains to the end.
               MachineFunction aliasFunction = getCurrentModule().getFunction(aliasOf);
               while (aliasFunction != null && aliasFunction.getAliasOf() != null) {
                   aliasOf = aliasFunction.getAliasOf();
                   aliasFunction = getCurrentModule().getFunction(aliasOf);
               }
    
               // The function which is being aliased may represent a literal value
               if (aliasFunction != null && aliasFunction.getLiteralValue() != null) {
                   // Set the expression of the current function to be the literal.
                   mf.setExpression(new Expression.Literal(aliasFunction.getLiteralValue()));
               } else {
                   // Mark the current function as being an alias.
                   mf.setAliasOf(aliasOf);
               }
           } else {
               // Just null out the alias field of the current function
               mf.setAliasOf(null);
           }
           
//           if (aliasOf != null) {
//               
//               // Need to update the strongly connected components
//               Set ccc = mf.getStronglyConnectedComponents();
//               if (ccc != null) {
//                   Set newSet = new HashSet();
//                   Iterator it = ccc.iterator();
//                   while (it.hasNext()) {
//                       String name = (String)it.next();
//                       MachineFunction cmf = getCurrentModule().getFunction(name);
//                       if (cmf.getAliasOf() != null) {
//                           newSet.add(cmf.getAliasOf().getUnqualifiedName());
//                       } else {
//                           newSet.add(name);
//                       }
//                   }
//                   
//                   it = newSet.iterator();
//                   while (it.hasNext ()) {
//                       String name = (String)it.next();
//                       MachineFunction cmf = getCurrentModule().getFunction(name);
//                       if (cmf != null) {
//                           cmf.getCoreFunction().setStronglyConnectedComponents(newSet);
//                       }
//                   }
//               }
//           }
       }
       
   }
   
    /**
     * If the given function is simply an alias for another function return the name
     * of the other function.
     * 
     * One function is an alias of another if all references to the alias can be 
     * safely replaced by references to the aliased function.  This is true when:
     * 
     *   1. the body of the alias function consists solely of a call to the aliased
     *      function.
     *   2. All the arguments to the alias are passed to the aliased function in the
     *      same order as the alias's argument list 
     *   3. the aliased function and the alias have the same arity
     *   4. the aliased function and the alias have compatible strictness (see comment
     *      for compatibleStrictnessWithAliasedFunction for details of strictness
     *      compatibility).
     *   5. the aliased function and the alias aren't defined in terms of each other
     *      (ie, there is no cycle between the two)
     *   6. the aliased function is not a 0-arity foreign function
     * 
     * Ex, in the following:
     *   
     *   foo x y = bar x y;
     *   
     *   bar a b = baz a b;
     *   
     *   baz i j k = quux i j k;
     *   
     *   quux m n o = quux2 m n o;
     *   
     *   quux2 p q r = baz p q r + baz r q p;
     *   
     * foo is an alias of bar.
     * bar is _not_ an alias of baz, because bar and baz have different arities.
     * baz is _not_ an alias of quux, because quux is defined in terms of quux2,
     * which is defined in terms of baz.
     * 
     * @param cl
     * @return the name of the function the code label is an alias of, may be null. 
     */
    private QualifiedName aliasOf(MachineFunction cl) {
        Expression e = cl.getExpressionForm();
        int arity = cl.getArity();
        QualifiedName aliasOf = null;

        if (e != null) {
            // Check for simplest mapping (e.g.  foo = blah;)
            if (arity == 0 && e.asVar() != null && !e.asVar().getName().equals(cl.getQualifiedName())) {
                    aliasOf =  e.asVar().getName();
            } else
            // Check for: foo x y z = blah x y z;
            if (e.asAppl() != null) {
                
                // Break the application chain into an array of nodes.
                List<Expression> nodes = new ArrayList<Expression>();
                Expression node = e;
                while (node.asAppl() != null) {
                    if (node.asAppl().getE2().asVar() == null) {
                        // The function body contains something other than the arguments and another supercombinator.
                        return null;
                    }
                    nodes.add(0, node.asAppl().getE2());
                    node = node.asAppl().getE1();
                }
                
                // Left hand side of chain must be a function.
                if (node.asVar() == null) {
                    return null;
                }
                
                MachineFunction aliasFunction = getCurrentModule().getFunction(node.asVar().getName());
                if (aliasFunction == null) {
                    return null;
                }
                
                // The aliased function must have the same arity and be fully saturated.
                if (arity != aliasFunction.getArity() || arity != nodes.size()) {
                    return null;
                }
                
                // The arguments to the aliased function must consist soley of the arguments to 
                // this function, in the same order.
                String argNames[] = cl.getParameterNames();
                for (int i = 0; i < argNames.length; ++i) {
                    QualifiedName qn = nodes.get(i).asVar().getName();
                    if (!qn.getUnqualifiedName().equals(argNames[i]) ||
                        !qn.getModuleName().equals(getCurrentModule().getName())) {
                        return null;
                    }
                }
                
                // This function is an alias of another function.
                aliasOf = node.asVar().getName();
                
                // Some functions, notably built-in functions have and expression form
                // of foo x y = foo x y;  This isn't really an alias.
                if (aliasOf.equals(cl.getQualifiedName())) {
                    return null;
                }
                
            }
        }        

        // Don't want to use an alias that is strongly connected to this function.
        if (aliasOf != null && aliasOf.getModuleName().equals(cl.getQualifiedName().getModuleName()) && cl.isStronglyConnectedTo (aliasOf.getUnqualifiedName())) {
            return null;
        }
        
        return aliasOf;
    }
    
    /**
     * Check aliased function for compatability of argument strictness.
     * 
     * The strictness of an alias is compatible with that of an aliased function
     * when the same arguments are plinged, up to the last plinged argument of the
     * alias.  In other words, it's okay for an aliased function to have extra 
     * plinged arguments to the right of the last pling on the alias, but otherwise
     * they must match exactly.  Ex:
     * 
     *   alpha x y z = delta x y z;
     *   
     *   beta x !y z = delta x y z;
     *   
     *   gamma !x y z = delta x y z;
     *   
     *   delta x !y !z = ...;
     *   
     *   
     *   epsilon x y !z = zeta x y z;
     *   
     *   zeta !x y !z = ...;
     *   
     * In the above code, alpha has compatible strictness with delta, because
     * alpha doesn't have any plinged arguments.
     * 
     * beta has compatible strictness with delta, because the first and second
     * arguments have the same plings; the third argument doesn't need to have
     * the same strictness because the second argument is beta's last plinged
     * argument.
     * 
     * gamma and delta do _not_ have compatible strictness, because gamma's 
     * first argument is plinged and delta's is not.
     * 
     * epsilon and zeta also don't have compatible strictness, because the
     * first argument's strictness does not match (which is important because
     * the third argument is epsilon's rightmost strict argument).
     * 
     * @param mf
     * @return name of the aliased function.  May be null.
     */
    private QualifiedName compatibleStrictnessWithAliasedFunction (MachineFunction mf) {
        // Now we need to check issues of differences in argument strictness between the two functions.
        // If the order of argument evaluation is changed by substituting the aliased function then
        // it can't be considered an alias.
        QualifiedName aliasOf = mf.getAliasOf();
        if (aliasOf == null) {
            return null;
        }
        
        // Follow any alias chains to the end.
        MachineFunction aliasFunction = getCurrentModule().getFunction(aliasOf);
        while (aliasFunction != null && aliasFunction.getAliasOf() != null) {
            aliasOf = aliasFunction.getAliasOf();
            aliasFunction = getCurrentModule().getFunction(aliasOf);
        }
        
//        System.out.println ("second stage alias " + cl.getQualifiedName() + " -> " + aliasOf);
        
        if (aliasFunction == null || aliasOf == null) {
            return null;
        } else {
            // Get the strictness of the two functions.
            boolean thisStrictness[] = mf.getParameterStrictness();
            boolean aliasStrictness[] = aliasFunction.getParameterStrictness();
            
            // If the current function is a CAF.
            if (mf.getArity() == 0) {
                if (aliasFunction.getArity() == 0) {
                    // Check for a CAF which is defined as a zero arity foreign function.
                    // This is not an alias as a CAF has different behaviour than
                    // a zero arity foreign function.
                    if (aliasFunction.isForeignFunction()) {
                        return null;
                    }
                    
                    // If the aliasFunction is a CAF defined as a literal value
                    // set the current function to be defined as a literal.
                    Expression expressionForm = aliasFunction.getExpressionForm();
                    if (expressionForm != null && expressionForm.asLiteral() != null) {
                        mf.setExpression(expressionForm);
                        return null;
                    }
                }
            }

            
            // Count the number of strict arguments for each function.
            int thisStrictCount = 0;
            int aliasStrictCount = 0;
            for (int i = 0; i < thisStrictness.length; ++i) {
                if (thisStrictness[i]) {
                    thisStrictCount++;
                }
                if (aliasStrictness[i]) {
                    aliasStrictCount++;
                }
            }

            if (thisStrictCount == 0) {
                // This function makes no guarantees of order of argument evaluation
                // so it doesn't matter what the aliased function does.
                return aliasOf;
            }
            
            // Right trim non-strict arguments from this function.
            int lastStrictIndex = thisStrictness.length - 1;
            for (; lastStrictIndex >= 0; lastStrictIndex--) {
                if (thisStrictness[lastStrictIndex]) {
                    break;
                }
            }
            
            // If the sub-arrays match then this is an alias.
            for (int i = 0; i <= lastStrictIndex; ++i) {
                if (thisStrictness[i] != aliasStrictness[i]) {
                    return null;
                }
            }
            
        }
        
        return aliasOf;
    }

    /**
     * Wrap the current module: do any cleanup processing on the entire packaged contents.
     * @param logger
     * @throws Packager.PackagerException
     */
    public void wrapModule (CompilerMessageLogger logger) throws Packager.PackagerException, UnableToResolveForeignEntityException {
        doTransformOptimizations (logger);
        
        // Since the module is complete we can now generate machine specific code for it.
        CompilerMessage.Severity resultCode = codeGenerator.generateSCCode(getCurrentModule(), logger);
        
        if (resultCode.equals(CompilerMessage.Severity.INFO) || resultCode.equals(CompilerMessage.Severity.INFO)) {
            // This module has been successfully comiled/packaged.  Update the associated timestamp in
            // the program.  This prevents re-compilation if the module hasn't changed.
            program.putCompilationTimestamp(currentModule.getName(), new Long(System.currentTimeMillis()));
        }
        
        // Inform any status listeners that this module is now loaded.
        for (final StatusListener statusListener : statusListeners) {
            statusListener.setModuleStatus(StatusListener.SM_LOADED, currentModule.getName());
        }
    }
    
    /**
     * Initialize packaging of the current adjunct.
     * @param adjunctModuleName
     * @throws PackagerException
     */
    public void initAdjunct (ModuleName adjunctModuleName) throws PackagerException {
        // Switch to the adjunct module.
        switchModule(adjunctModuleName);
        
        // Remove any previous adjunct functions.
        currentModule.clearAdjunctFunctions();
    }
    
    /**
     * Wrap the current adjunct: do any cleanup processing on the entire packaged contents.
     * @param logger
     * @throws PackagerException
     */
    public void wrapAdjunct (CompilerMessageLogger logger) throws Packager.PackagerException, UnableToResolveForeignEntityException {
        doTransformOptimizations (logger);
    }

    /**
     * @return the current level of optimization. Zero means the Global Optimizer is not run.
     */
    public static int getOptimizerLevel(){
        return optimizerLevel;
    }

    /**
     * Build a set of the module names in the given expression. 
     * 
     * @param expr The expression to get all the modules in.
     * @param modules
     */

    private static void modules(Expression expr, Set<ModuleName> modules) {
        class ModuleCollector extends Visitor {
            private final Set<ModuleName> modules;

            ModuleCollector(Set<ModuleName> modules){
                this.modules = modules;
            }
            
            @Override
            void enterVar(Expression.Var var) {
                QualifiedName name = var.getName();
                if (name != null) {
                    modules.add(name.getModuleName());
                }
            }
        }

        ModuleCollector collector = new ModuleCollector(modules);
        expr.walk(collector);
    }

    /**
     * This is thrown when the function being optimized access (directly or indirectly) too many functions.
     * 
     * @author GMCCLEMENT
     *
     */
    
    static class TooManyHelperFunctionsException extends IllegalStateException{

        /**
         * Make a warning go away.
         */
        private static final long serialVersionUID = 3042774494441022983L;        
    }

    static class HasBadStructureException extends IllegalStateException{
        
        private static final long serialVersionUID = 7405717617059083752L;
    }

    /**
     * This is thrown when the function being optimized is too deep and so would generate code that
     * could not be compiled by the java compiler.
     * 
     * @author GMCCLEMENT
     *
     */
    
    static class TooDeepException extends IllegalStateException{
        
        private static final long serialVersionUID = 3642636632176914397L;        
    }

    /**
     * This is thrown when the function being optimized is too deep and so would generate code that
     * could not be compiled by the java compiler.
     * 
     * @author GMCCLEMENT
     *
     */
 
    static class TooMuchTimeException extends IllegalStateException{

        private static final long serialVersionUID = 9184516831783875647L;

    }
    
    /**
     * This will only be initialized if the optimizer is enabled.
     */
    private PreludeTypeConstants preludeTypeConstants = null; 
    
    /**
     * This function applies to CAL based optimized to the given expression. If
     * the optimizer is not available then the expression is returned unchanged.
     * 
     * @param timeStamp
     * @param cf
     * @param moreFunctions
     * @return The optimized version of the expression if the optimizer is on.
     * @throws Throwable 
     */

    private Expression applyCALOptimizer(long timeStamp, CoreFunction cf, List<MachineFunction> moreFunctions) throws Throwable {
        Expression body = cf.getExpression();
        
        if (OptimizerHelper.hasExcludedTypes(body)){
            return body;
        }
        
        final QualifiedName name = cf.getName();

        if (optimizer_executor != null) {
            // Don't optimize the commands from the console. This just makes things look slower
            // and has no practical benefit since the code is already optimized.
            if (name.getUnqualifiedName().indexOf("iceruntarget") != -1){
                return body;
            }

            try {
                /**
                 * Type information for the optimizer.
                 */
                List<List<Object>> nameToTypeExpr = new LinkedList<List<Object>>();
                
                // Get the functions that are references by the expression being
                // optimized. These will
                // be passed to the CAL optimizer for use during optimization.

                LinkedList<AlgebraicValue> helperFunctions = new LinkedList<AlgebraicValue>();
                List<QualifiedName> nonCalFunctions = new LinkedList<QualifiedName>();

                OptimizerHelper.getHelperFunctions(program, null, inlinableFunctions, cf, nameToTypeExpr, helperFunctions, nonCalFunctions);

                // TODO GREGM, Fix this limitation
                
                if (helperFunctions.size() > 200){
                    throw new TooManyHelperFunctionsException();
                }                

                // Convert the parameters into a list instead of an array.
                ArrayList<QualifiedName> formalParametersList = new ArrayList<QualifiedName>();
                {
                    String[] formalParametersArray = cf.getFormalParameters();
                    formalParametersList.ensureCapacity(formalParametersArray.length);
                    for(int i = 0; i < formalParametersArray.length; ++i){
                        formalParametersList.add(i, QualifiedName.make(name.getModuleName(), formalParametersArray[i]));
                    }
                }
                
                // Convert the parameter strictness into a list instead of an array.
                ArrayList<Boolean> formalParametersStrictnessList = new ArrayList<Boolean>();
                {
                    boolean[] formalParametersStrictnessArray = cf.getParameterStrictness();
                    formalParametersStrictnessList.ensureCapacity(formalParametersStrictnessArray.length);
                    for(int i = 0; i < formalParametersStrictnessArray.length; ++i){
                        formalParametersStrictnessList.add(i, Boolean.valueOf(formalParametersStrictnessArray[i]));
                    }
                }
                
                long startTime = System.currentTimeMillis();

                List<Object> result = UnsafeCast.unsafeCast(optimizer_executor.exec(
                        optimizer_entryPoint, 
                        new Object[] { 
                            name, 
                            formalParametersList, 
                            formalParametersStrictnessList,
                            new Long(startTime), 
                            preludeTypeConstants, 
                            nameToTypeExpr, 
                            helperFunctions,
                            nonCalFunctions,
                            body }));

                List<AlgebraicValue> newFunctionDefs = UnsafeCast.unsafeCast(result.get(0));
                List<Boolean> strictnessList = UnsafeCast.unsafeCast(result.get(1));

                // Convert the list of booleans to an array. The optimizer does not know 
                // what the original strictness is so 'or' that into the final result.
                {
                    boolean strictnessArray[] = new boolean[strictnessList.size()];
                    boolean extantStrictnessArray[] = cf.getParameterStrictness();
                    {
                        Iterator<Boolean> iList = strictnessList.iterator();
                        for(int iArray = 0; iArray < strictnessArray.length; ++iArray){
                            Boolean bValue = iList.next();
                            strictnessArray[iArray] = bValue.booleanValue() || extantStrictnessArray[iArray];
                        }
                    }
                    
                    cf.setParameterStrictness(strictnessArray);
                }
                
                Expression resultExpr = (Expression) result.get(2);

                // Convert lambda expression into top level functions
                {
                    for (final AlgebraicValue coreFunction : newFunctionDefs) {
                        QualifiedName newFunctionName = (QualifiedName) coreFunction.getNthArgument(0);
                
//                        System.out.println("    NewFunctionName: " + newFunctionName);
                        final String[] formalParameters;
                        {
                            List<QualifiedName> calFormalParameters = UnsafeCast.unsafeCast(coreFunction.getNthArgument(1));
                            formalParameters = new String[calFormalParameters.size()];
                            Iterator<QualifiedName> iList = calFormalParameters.iterator(); 
                            for(int i = 0; i < formalParameters.length; ++i){
                                QualifiedName qualifiedName = iList.next();
                                formalParameters[i] = qualifiedName.getUnqualifiedName();
                            }
                        }

                        Expression newFunctionDef = (Expression) coreFunction.getNthArgument(2);
                        // can be overridden in the if below
                        Expression newFunctionBody = newFunctionDef;
                        
                        // Initialize the return and arg types
                        TypeExpr[] argTypes = {};
                        TypeExpr resultType;
                        {
                            List<TypeExpr> calTypesList = UnsafeCast.unsafeCast(coreFunction.getNthArgument(3));
                            argTypes = new TypeExpr[calTypesList.size() - 1];
                            Iterator<TypeExpr> icalTypesList = calTypesList.iterator();
                            for (int iargTypes = 0; iargTypes < argTypes.length; ++iargTypes) {
                                argTypes[iargTypes] = icalTypesList.next();
                            }
                            resultType = icalTypesList.next();
                        }

                        // Initialize strictness array
                        final boolean[] argStrictness;
                        {
                            List<Boolean> calStrictness = UnsafeCast.unsafeCast(coreFunction.getNthArgument(4));
                            argStrictness = new boolean[calStrictness.size()];
                            Iterator<Boolean> iNextStrictness = calStrictness.iterator();
                            for(int i = 0; i < argStrictness.length; ++i){
                                argStrictness[i] = iNextStrictness.next().booleanValue();
                            }
                        }
                        
                        CoreFunction newCF = CoreFunction.makeCALCoreFunction(newFunctionName,
                                formalParameters, argStrictness, argTypes, resultType,
                                timeStamp);

                        newCF.setExpression(newFunctionBody);

                        MachineFunction mf = getMachineFunction(newCF);
                        moreFunctions.add(mf);
                        
                        // in case functions in the same module use the definition.
                        inlinableFunctions.put(newCF.getName(), newCF);
                    }
                }
                return resultExpr;
            } catch (CALExecutorException executorException) {
                // TODO this is for nested cases that need to be lifted.
//                System.out.println(executorException);
                if (executorException.getCause() instanceof OptimizerHelper.UnsupportedExpressionTypeException){
                    throw executorException.getCause();
                }
                if (executorException.getCause().toString().indexOf("Has bad structure") != -1){
                    throw new HasBadStructureException(); 
                }
                if (executorException.getCause().toString().indexOf("Too deep") != -1){
                    throw new TooDeepException(); 
                }
                if (executorException.getCause().toString().indexOf("Too much time") != -1){
                    throw new TooMuchTimeException(); 
                }
                throw new IllegalStateException();
            }
        } else {
            return body;
        }
    }

    private void doTransformOptimizations(CompilerMessageLogger logger) throws Packager.PackagerException, UnableToResolveForeignEntityException {
        
        // First build up a list of functions that haven't already been optimized.
        List<MachineFunction> functionsList = new ArrayList<MachineFunction>();
        Module m = getCurrentModule();
        
        for (final MachineFunction mf : m.getFunctions()) {
            if (!mf.isOptimized()) {
                functionsList.add (mf);
            }
        }

        // Run the CAL optimizer on the functions before other optimization are
        // performed.
        if (optimizer_executor != null) {            
            Iterator<MachineFunction> functions = functionsList.iterator();
            List<MachineFunction> moreFunctions = new LinkedList<MachineFunction>();
            // List of modules used by the module. Inlining expression can cause name from
            // non-imported module to be used. The type info has to be updated with these new
            // modules.
            Set<ModuleName> moreModules = new HashSet<ModuleName>();
            int skipped_otherCounter = 0;
            int skipped_tooManyHelperFunctionsCounter = 0;
            int skipped_unsupportedExpressionType = 0;
            int skipped_hasBadStructure = 0;
            int skipped_tooDeep = 0;
            int skipped_tooMuchTime = 0;
            int changedCounter = 0;
            while (functions.hasNext()) {
                MachineFunctionImpl cl = (MachineFunctionImpl) functions.next();
                CoreFunction coreFunction = cl.getCoreFunction();
                
                if (cl.getExpressionForm() instanceof Expression.PackCons) {
                    continue; // can't be optimized so skip these
                }
                if (cl.getName().startsWith("$")) {
                    continue; // skip compiler generated helper functions
                }

                try {
                    Expression newBody = applyCALOptimizer(cl.getTimeStamp(), coreFunction, moreFunctions);
                    if (!newBody.toString().equals(cl.getExpressionForm().toString())) {
                        ++changedCounter;
                    }
                    modules(newBody, moreModules);
                    cl.setExpression(newBody);
                } catch(TooManyHelperFunctionsException e){
                    skipped_tooManyHelperFunctionsCounter++;
                } catch (OptimizerHelper.UnsupportedExpressionTypeException e) {
                    skipped_unsupportedExpressionType++;
                } catch (HasBadStructureException e){
                    skipped_hasBadStructure++;
                } catch (TooDeepException e){
                    skipped_tooDeep++;
                } catch (TooMuchTimeException e){
                    skipped_tooMuchTime++;
                } catch (IllegalStateException e) {
                    // TODO fix this so all the types of expressions are
                    // handled.
                    skipped_otherCounter++;
                } catch (UnsupportedOperationException e){
                    skipped_unsupportedExpressionType++;
                } catch (IllegalArgumentException e) {
                    // TODO figure out why this is happening and fix it.
                    skipped_otherCounter++;
                } catch (Throwable e) {
                    // TODO figure out why this is happening and fix it.
                    skipped_otherCounter++;
                }
            }

            if (moreFunctions.size() > 0 || 
                    skipped_otherCounter > 0 ||
                    skipped_hasBadStructure > 0 ||
                    skipped_tooDeep > 0 ||
                    skipped_tooMuchTime > 0 ||
                    skipped_tooManyHelperFunctionsCounter > 0 ||
                    skipped_unsupportedExpressionType > 0 ||
                    changedCounter > 0) {
                System.out.print(m.getName());
                System.out.println(": ");
                if (moreFunctions.size() > 0) {
                    System.out.println("    Added " + moreFunctions.size()
                            + " more functions.");
                }
                if (changedCounter > 0) {
                    System.out.println("    Changed " + changedCounter + " of "
                            + functionsList.size() + " expressions.");
                }
                if (
                        skipped_unsupportedExpressionType > 0 || 
                        skipped_tooManyHelperFunctionsCounter > 0 ||
                        skipped_hasBadStructure > 0 ||
                        skipped_tooDeep > 0 ||
                        skipped_tooMuchTime > 0 || 
                        skipped_otherCounter > 0) {
                    System.out.println("    Skipped " + (skipped_hasBadStructure + skipped_otherCounter + skipped_tooManyHelperFunctionsCounter + skipped_unsupportedExpressionType) + " of " + functionsList.size() + " expressions.");
                    if (skipped_tooManyHelperFunctionsCounter > 0) {
                        System.out.println("        Too many helpers " + skipped_tooManyHelperFunctionsCounter + " of " + functionsList.size() + " expressions.");
                    }
                    if (skipped_unsupportedExpressionType > 0) {
                        System.out.println("        Unsupported expression types " + skipped_unsupportedExpressionType + " of " + functionsList.size() + " expressions.");
                    }
                    if (skipped_hasBadStructure > 0) {
                        System.out.println("        Incomplete optimization " + skipped_hasBadStructure + " of " + functionsList.size() + " expressions.");
                    }
                    if (skipped_tooDeep > 0) {
                        System.out.println("        Optimization too deep " + skipped_tooDeep + " of " + functionsList.size() + " expressions.");
                    }
                    if (skipped_tooMuchTime > 0) {
                        System.out.println("        Too much time " + skipped_tooMuchTime + " of " + functionsList.size() + " expressions.");
                    }                    
                    if (skipped_otherCounter > 0) {
                        System.out.println("        Other " + skipped_otherCounter + " of " + functionsList.size() + " expressions.");
                    }                    
                }
            }

            // add the helper functions to the list of functions for the module.
            {
                for (final MachineFunction mf : moreFunctions) {
                    m.addFunction(mf);
                    functionsList.add(mf);
                }
            }
            
        }
        
        Iterator<MachineFunction> functions = functionsList.iterator();
        Map<String, Expression> nameToExpressionMap = new HashMap<String, Expression>();
        while (functions.hasNext()) {
            MachineFunction cl = functions.next ();
            nameToExpressionMap.put (cl.getName(), cl.getExpressionForm());
        }
        
        // Determine the strongly connected components and put the information into the code labels.
        List<Set<String>> sets = ExpressionAnalyzer.determineStronglyConnectedComponents(m.getName(), nameToExpressionMap);
        for (int i = 0; i < sets.size(); ++i) {
            Set<String> set = sets.get(i);
            Iterator<String> items = set.iterator();
            while (items.hasNext()) {
                String name = items.next();
                MachineFunctionImpl mf = (MachineFunctionImpl)m.getFunction(name);
                if (mf != null) {
                    mf.setStronglyConnectedComponents(set);
                }
            }
        }

        // Now that we've determined the closely connected components we can use
        // an ExpressionAnalyzer to do optimizing transformations to the expression.
        functions = functionsList.iterator();
        while (functions.hasNext()) {
            
            MachineFunction cl = functions.next ();
            ExpressionAnalyzer ea = new ExpressionAnalyzer (m.getModuleTypeInfo(), cl);
            
            cl.setExpression(ea.transformExpression(cl.getExpressionForm()));
            if (ea.getHadUnsafeCoerce()){
                cl.setHadUnsafeCoerce();
            }
            cl.setIsTailRecursive(ExpressionAnalyzer.isTailRecursive(cl.getExpressionForm()));
        }        
        
        
        // At this point we want to figure out whether any functions are
        // simply an alias for another function and put the name of the aliased function
        //in the MachineFunction.
        determineFunctionAliases(functionsList);
        
        functions = functionsList.iterator();
        while (functions.hasNext ()) {
            MachineFunction mf = functions.next ();
            ExpressionAnalyzer.antiAlias (mf, m, logger);
            mf.setOptimized();
        }
        
    }
    
}
