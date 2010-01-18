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
 * ValueRunner.java
 * Creation date: (Jun 18, 2002 2:49:59 PM)
 * By: Edward Lam
 */
package org.openquark.gems.client;

import java.util.ArrayList;
import java.util.logging.Level;

import org.openquark.cal.compiler.AdjunctSource;
import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.ModuleTypeInfo;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceModel;
import org.openquark.cal.compiler.TypeException;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.compiler.io.InputPolicy;
import org.openquark.cal.services.GemEntity;
import org.openquark.cal.services.WorkspaceManager;
import org.openquark.cal.valuenode.GemEntityValueNode;
import org.openquark.cal.valuenode.Target;
import org.openquark.cal.valuenode.TargetRunner;
import org.openquark.cal.valuenode.ValueNode;
import org.openquark.cal.valuenode.ValueNodeBuilderHelper;


/**
 * A value runner runs a CAL definition and returns a corresponding value node.
 * This is used to get the value node for some CAL text.
 * Note: this runs in immediate mode, and the CAL definition being run should not require extra args to be set.
 * Note 2: there's no way to terminate a computation - don't run anything non-terminating!
 * 
 * Some current uses:
 * a. running the code examples in the metadata.
 * b. loading a value gem from its saved CAL text.
 * 
 * @author Edward Lam
 */
public class ValueRunner extends TargetRunner {

    /**
     * Constructor for a ValueRunner
     * @param workspaceManager the workspace manager on which this runner is based.
     */
    public ValueRunner(WorkspaceManager workspaceManager) {
        super(workspaceManager);
    }

    /**
     * Execute (in this thread) the supercombinator defined by the Target, and return the resulting value.
     * A new execution context will be used.
     * @param target Target the target which defines what to run.
     * @param targetModule the module in which the target exists.
     * @return ValueNode the resulting Value.  Null if we can't handle the output, or if evaluation failed.
     */
    public ValueNode getValue(Target target, ModuleName targetModule) throws ProgramCompileException {
    
        return getValue(target, new ValueNode[0], targetModule);
    }

    /**
     * Execute (in this thread) the supercombinator defined by the Target, and return the resulting value.
     * @param target Target the target which defines what to run.
     * @param argValues the valuenode arguments to the target.  These must *not* be parametric.
     * @param targetModule the module in which the target exists.
     * @return ValueNode the resulting Value.  null if we can't handle the output, or if evaluation failed.
     */
    public ValueNode getValue(Target target, ValueNode[] argValues, ModuleName targetModule) throws ProgramCompileException {
    
        setTarget(target, targetModule);
    
        // The target:
        // Note that if we can't get a target type then we return null.
        TypeExpr targetGemTypeExpr = getNewTargetTypeExpr();
        if (targetGemTypeExpr == null) {
            return null;
        }
                 
        int nArgs = argValues.length;
        TypeExpr[] targetTypePieces = targetGemTypeExpr.getTypePieces(nArgs);

        // Use the arguments to specialize the target type.
        TypeExpr valueNodeArgTypes[] = new TypeExpr[nArgs];
        for (int i = 0; i < nArgs; i++) {
            valueNodeArgTypes[i] = argValues[i].getTypeExpr();
        }
        
        ModuleTypeInfo currentModuleTypeInfo = getWorkspaceManager().getWorkspace().getMetaModule(targetModule).getTypeInfo();
        TypeExpr[] specializedTargetTypePieces;
        try {        
            specializedTargetTypePieces = TypeExpr.patternMatchPieces(valueNodeArgTypes, targetTypePieces, currentModuleTypeInfo);
        } catch (TypeException te){
            // What to do?
            GemCutter.CLIENT_LOGGER.log(Level.SEVERE, "Could not determine gem execution output type.");
            return null;            
        }       

        // The result:
        TypeExpr resultType = specializedTargetTypePieces[nArgs];

        if (resultType.isFunctionType()) {
            
            // todoFW: HACK! Handle loading function values better.
            // The function value will be the last item in the text inside brackets.
            // Stip the brackets and get the text which should give us the qualified name
            // of the function.

            /* old string-based hack
             
            String scDef = getTarget().getTargetDef(null, currentModuleTypeInfo).toString();
            
            int firstEquals = scDef.indexOf(" = ");
            scDef = scDef.substring(firstEquals + 3);
            scDef = scDef.replaceAll(";|\n", "");  // replace semicolon, newline
            
            if (QualifiedName.isValidCompoundName(scDef)) {
                
                QualifiedName functionName = QualifiedName.makeFromCompoundName(scDef);
                GemEntity gemEntity = getWorkspaceManager().getWorkspace().getGemEntity(functionName);
                
                if (gemEntity != null) {
                    return new GemEntityValueNode(gemEntity, resultType);
                }
            }
            */
            
            // HACK: assumes that the target definition is of the form:
            //           private cdInternal_runTarget = Module.function;
            //       where "Module.function" is the function value in question
            
            AdjunctSource.FromSourceModel scDef = getTarget().getTargetDef(null, currentModuleTypeInfo);
            
            if (scDef.getNElements() == 1) {
                
                SourceModel.TopLevelSourceElement element = scDef.getElement(0);
                
                if (element instanceof SourceModel.FunctionDefn.Algebraic) {
                    
                    SourceModel.FunctionDefn.Algebraic func =
                        (SourceModel.FunctionDefn.Algebraic)element;
                    
                    if (func.getParameters().length == 0) {
                        
                        SourceModel.Expr definingExpr = func.getDefiningExpr();
                        
                        if (definingExpr instanceof SourceModel.Expr.Var) {
                            
                            SourceModel.Expr.Var var = (SourceModel.Expr.Var)definingExpr;
                            
                            String name = var.toSourceText();
                            
                            if (QualifiedName.isValidCompoundName(name)) {
                                
                                QualifiedName functionName = QualifiedName.makeFromCompoundName(name);
                                GemEntity gemEntity = getWorkspaceManager().getWorkspace().getGemEntity(functionName);
                                
                                if (gemEntity != null) {
                                    return new GemEntityValueNode(gemEntity, resultType);
                                }
                            }                            
                        }
                    }
                }
            }
            
            // Something went wrong, that means we don't support the type.
            return null;
            
        } else {
            
            // Everything else is supported normally.
            // Set the typeExpr for the target, the result, and the overloading resolver.

            ArrayList<Object> valueList = new ArrayList<Object>();
            InputPolicy inputPolicies[] = new InputPolicy[nArgs];
            TypeExpr[] argTypes = new TypeExpr[nArgs];
            
            for (int i = 0; i < nArgs; i++) {
                Object[] values = argValues[i].getInputJavaValues();
                inputPolicies[i] = argValues[i].getInputPolicy();
                argTypes[i] = argValues[i].getTypeExpr();
                for (int j = 0; j < values.length; ++j) {
                    valueList.add(values[j]);
                }
            }

            Object[] inputValues = valueList.toArray();
            // Build and execute the test program in non-threaded mode.
            buildTestProgram(inputPolicies, argTypes);
            executeTestProgram(inputValues, false);

            // The returned value node won't have a type expression set, so it's up to us to do it.
            ValueNode outputValueNode = getOutputValueNode();
            if (outputValueNode == null) {
                String errorString = "Null value executing text: " + getTarget().getTargetDef(null, currentModuleTypeInfo).toString();
                GemCutter.CLIENT_LOGGER.log(Level.WARNING, errorString);
                
                return null;
            }
            
            if (error != null) {
                String errorString = "Null value executing text: " + getTarget().getTargetDef(null, currentModuleTypeInfo).toString();
                GemCutter.CLIENT_LOGGER.log(Level.WARNING, errorString, error);
                
                return null;
            }
            
            ValueNode returnNode = outputValueNode.copyValueNode();
            returnNode.setOutputJavaValue(executionResult);
            return returnNode;
        } 
    }

    /**
     * Returns whether the target's output type can be handled.
     * @param target the target to consider.
     * @param targetModule the module in which the target exists.
     * @return boolean whether the target's output type can be handled.  Note that this will be in the absence
     * of any arguments being provided.
     */
    private boolean canHandleTargetOutput(Target target, ModuleName targetModule) {
        TypeExpr outputTypeExpr = getNewTargetTypeExpr(target, targetModule, getTypeChecker());
        return ValueNodeBuilderHelper.canHandleOutput(outputTypeExpr);
    }

        
    /** 
     * Returns the basic error message in string format
     * @param target the target.
     * @param targetModule the module in which the target exists.
     * @return the error message
     */
    public String getErrorMessage(Target target, ModuleName targetModule) {
        
        if (canHandleTargetOutput(target, targetModule)) {
            String errorMessage = "";
            if (error != null) {
                errorMessage = error.getMessage();
            }
            
            return "Error: " + errorMessage;

        } else {
            return GemCutter.getResourceString("OutputNotHandled");
        }
    }
    
}
