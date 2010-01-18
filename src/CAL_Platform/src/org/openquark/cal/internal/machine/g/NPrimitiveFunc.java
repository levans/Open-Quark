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
 * NPrimitiveFunc.java
 * Created: July 28, 2005 
 * By: Raymond Cypher 
 */
package org.openquark.cal.internal.machine.g;

import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.internal.machine.g.Executor.GExecutionContext;
import org.openquark.cal.runtime.CALExecutorException;
import org.openquark.cal.runtime.CalValue;
import org.openquark.cal.runtime.MachineConfiguration;


/**
 * The base class for primitive functions.
 */
public abstract class NPrimitiveFunc extends NInd {
    /** Flag indicating that function tracing and breakpoints are enabled. */
    private static final boolean GENERATE_DEBUG_CODE = System.getProperty(MachineConfiguration.MACHINE_DEBUG_CAPABLE_PROP) != null;

    private Code unwind = new Code(Instruction.I_Unwind);
   
    /**
     * @return the arity of the function implemented.
     */
    protected abstract int getArity();
    
    /**
     * @return the qualified name of the function being implemented.
     */
    protected abstract QualifiedName getName();
    
    /**     
     * {@inheritDoc}
     */
    @Override
    public int debug_getNChildren() {  
        if (hasIndirection()) {
            return super.debug_getNChildren();           
        }        
     
        return 0;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public CalValue debug_getChild(int childN) {
        if (hasIndirection()) {
            return super.debug_getChild(childN);           
        }
        
        throw new IndexOutOfBoundsException();        
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String debug_getNodeStartText() {
        if (hasIndirection()) {
            return super.debug_getNodeStartText();           
        }        
        
        return getName().getQualifiedName();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String debug_getNodeEndText() {
        if (hasIndirection()) {
            return super.debug_getNodeEndText();           
        } 
        
        return "";
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String debug_getChildPrefixText(int childN) {
        if (hasIndirection()) {
            return super.debug_getChildPrefixText(childN);           
        }
        
        throw new IndexOutOfBoundsException();            
    }
    
    /**
     * The indented string representation of this function node.
     * @param indent
     * @return indented string.
     */
    @Override
    public String toString (int indent) {
        StringBuilder indentSb = new StringBuilder();
        for (int i =0; i < indent; ++i) {
            indentSb.append (" ");
        }
        
        return indentSb.toString() + idString(0) + " <" + getName() + " / " + getArity() + ">";
    }
    
    
    /**
     * Perform the function logic.
     * @param arguments - the arguments to the function as an array of Node.
     * @param executor
     * @return the result of the evaluation
     * @throws CALExecutorException
     */
    public abstract Node doEvaluation (Node[] arguments, Executor executor)  throws CALExecutorException ;
    
    /**
     * Does the evaluation and cleanup, delegating to doEvaluation for actual function logic.
     * @param executor
     * @throws CALExecutorException
     */
    private final void evaluateInternal (Executor executor) throws CALExecutorException {
        Executor.GStack stack = executor.stack;
        GExecutionContext executionContext = (GExecutionContext)executor.getContext();
        
        if (GENERATE_DEBUG_CODE && executionContext.isDebugProcessingNeeded(getName().getQualifiedName())) {
            int arity = getArity();
            Node args[] = new Node[arity];

            // The arguments are on the top of the stack.
            for (int i = 0; i < arity; ++i) {
                args[i] = stack.get(stack.size() - i - 1);
            }
            
            // Let the executionContext do the debug processing.
            executionContext.debugProcessing(getName().getQualifiedName(), args);
           
        }
        
        // Pop the arguments off the stack and put in an array.
        Node[] args = new Node[getArity()];
        for (int i = 0; i < getArity(); ++i) {
            args[i] = stack.pop ();
        }
        
        // Pass the arguments to doEvaluation() and get the result.
        Node result = doEvaluation (args, executor);

        // Update the root on the top of the stack.
        int updatePosition = stack.size () - 1;
        stack.get (updatePosition).setIndirectionNode(result);
        stack.set(updatePosition, result);

        // Set the code pointer to 'unwind' and return.
        executor.setIP (unwind);
    }
    
    /*
     * Functions to perform state transitions
     */

    /**
     * Do the Eval state transition.
     */
    @Override
    protected final void i_eval (Executor e) throws CALExecutorException {
        
        if (getLeafNode() != this) {
            e.stack.pop ();
            e.stack.push (getLeafNode());
            getLeafNode().i_eval(e);
            return; 
        }
        
        
        e.pushDumpItem();
        
        i_unwind (e);
        
    }

    @Override
    protected final void i_dispatch (Executor e, int k) throws CALExecutorException {
        if (getArity() == 0) {
            super.i_dispatch (e, k);
        } else
        if (getArity() == k) {
            // Tail call case.
            // <f:S, G[f = FUN k C], Dispatch k:[], D>
            // =>
            // <S, G, C, D>
            
            // Pop this global node off of the stack.
            e.stack.pop ();
    
            // Do the evaluation.
            evaluateInternal (e);
        } else 
       if (getArity() < k) {
            // <f:n1:n2:...:nk:r:S, G[f=FUN a C], Dispatch k:[], D>
            // a < k
            // <n1:...:na:va:...:v(k-1):r:S, G[va = HOLE                ], C, D>
            //                                [vi = AP v(i-1) ni (a<i<k)]
            //                                [r = AP v(k-1) nk         ]
            
            
            e.stack.pop ();
            int si = e.stack.size() - (getArity() + 1);
          
            Node n = e.stack.get (si);
            e.stack.set (si, new NInd ());
          
            for (int i = getArity() + 1; i < k; ++i) {
                Node na = new NAp (e.stack.get (si--), n);
                n = e.stack.get (si);
                e.stack.set (si,na);
            }
            
            Node na = new NAp (e.stack.get (si--), n);
            e.stack.get (si).setIndirectionNode (na);
            e.stack.set (si, na);

            // Do the evaluation.
            evaluateInternal (e);
            
        } else
        if (getArity() > k) {
            // Two different transitions at this point:
            //
            // Not enough arguments on stack for f:
            // <f:n1:n2:...:nk:r:v(k+1):...:vd:[], G[f = FUN a C], Dispatch k:[], (S, C1):D>
            // (k < d < a)  =>
            // <vd:S, G[v1 = AP f n1                 ], C1, D>
            //         [vi = AP v(i-1) ni (1 < i < k)]
            //         [r = AP v(k-1) nk             ]
            //
            // Enough arguments on stack for f:
            // <f:n1:n2:...:nk:r:v(k+1):...:vd:S, G[f = FUN a C                          ], Dispatch k:[], D>
            //                                     [v(k+1) = AP r n(k+1)                 ]
            //                                     [vi = AP v(i-1) ni     ((k+1) < i <=a)]
            //
            // (k < a) =>
            // <n1:n2:...:nk:n(k+1):...:na:va:S, G[v1 = AP f n1              ], C, D>
            //                                    [vi = AP v(i-1) ni  (1<i<k)]
            //                                    [r = AP v(k-1) nk          ]
            
            if (e.stack.size() < (getArity() + 2)) {
                super.i_dispatch (e, k);
            } else {
                
//              System.out.println ("---------------");
//              System.out.println (e.showStack (0));
//              System.out.println ("---------------");

                
                NAp vi = new NAp (e.stack.pop (), e.stack.peek());
                for (int i = 2; i < k; ++i) {
                    vi = new NAp (vi, e.stack.get (e.stack.size() - i));  
                }

                // root index
                int ir = e.stack.size() - (k + 1);
                
                if (k == 1) {
                    ((NInd)e.stack.get(ir)).setIndirectionNode(vi);
                } else {
                    NAp nr = new NAp (vi, e.stack.get (e.stack.size() - k));
                    ((NInd)e.stack.get(ir)).setIndirectionNode(nr);
                }
                        
                for (int i = k; i < getArity(); ++i) {
                    e.stack.set (ir, ((NAp)e.stack.get(--ir)).getN2());
                }
                

//              System.out.println ("---------------");
//              System.out.println (e.showStack (0));
//              System.out.println ("---------------");
                
                // Do the evaluation.
                evaluateInternal (e);
                
            }
            
        } else {
            // default case
            super.i_dispatch (e, k);
        }
    }
    
    /**
     * Do Unwind state transition.
     */
    @Override
    protected final void i_unwind (Executor e) throws CALExecutorException {
        
        Executor.GStack stack = e.stack;
        
        if (getLeafNode() != this) {
            stack.pop ();
            stack.push (getLeafNode());
            //ip.setIP (ip.getIP() - 1);
            getLeafNode().i_unwind (e);
            return;
        }
                            
        // Determine the number of necessary args.
        int nArgs = getArity ();
        int stackSize = stack.size ();
        
        if (stackSize - 1 < nArgs) {
  
            e.popDumpItemBottom ();
        } else {
            // Re-arrange the stack to expose the arguments.
            if (nArgs > 0) {
                // Pop off the NGlobal
                stack.pop ();
                //System.out.println ("\n" + showStack(0) + "\n");
                
                // insert a second instance of the nth node
                stackSize--;
                
                Node argN = stack.get (stackSize - nArgs);
                stack.add(stackSize - nArgs, argN);
                //System.out.println ("\n" + showStack(0) + "\n");
                
                int stackSizeMinusOne = stackSize;  
                //stackSize increases by one, but it is not neccessary to do this computation           
                //stackSize++;
                
                for (int i = 0; i < nArgs; ++i) {
                    int rearrangePosition = stackSizeMinusOne  - i;
                    NAp ap = (NAp)stack.get (rearrangePosition);
                    stack.set(rearrangePosition, ap.getN2());
                    //System.out.println ("\n" + showStack(0) + "\n");
                }   
                
                ((NAp)argN).clear();

            }
            
            evaluateInternal (e);
        }
    } 

    /**   
     * A helper function used to ensure the continued validity of the hand-written NPrimitiveFunc subclasses.  
     * @param executor
     * @param typeClassName
     * @return true if the typeClassName is a type class with only a single class method and
     *   no superclasses.
     */
    protected static boolean isSingleMethodRootClass(Executor executor, QualifiedName typeClassName) {
        return (executor.getProgram().getModule(typeClassName.getModuleName()).
                getModuleTypeInfo().getTypeClass(typeClassName.getUnqualifiedName())).internal_isSingleMethodRootClass();
    }
    
    /**
     * A helper function used to ensure the continued validity of the hand-written NPrimitiveFunc subclasses. 
     * @param executor
     * @param classMethodName
     * @return the index within the run-time dictionary of the given class method.
     */    
    protected static int classMethodDictionaryIndex(Executor executor, QualifiedName classMethodName) {
        return (executor.getProgram ().getModule(classMethodName.getModuleName()).
                getModuleTypeInfo().getClassMethod(classMethodName.getUnqualifiedName())).internal_getDictionaryIndex();
    }
}
