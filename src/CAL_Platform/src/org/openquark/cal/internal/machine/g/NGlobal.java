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
 * NGlobal.java
 * Created: Sep 23, 2002 at 2:51:22 PM
 * By: Raymond Cypher
 */
package org.openquark.cal.internal.machine.g;

import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.runtime.CALExecutorException;
import org.openquark.cal.runtime.CalValue;


/** 
 * This is the NGlobal class
 *
 * Represents a global supercombinator node in the
 * program graph.
 * Creation: Sep 23, 2002 at 2:51:22 PM
 * @author Raymond Cypher
 */

public class NGlobal extends NInd {
    
    private final QualifiedName name;
    private final int arity;
    private Code code;
       
    NGlobal (int arity, Code code, QualifiedName name) {
        super ();
        this.arity = arity;
        this.code = code;
        this.name = name;
    }
    NGlobal (NGlobal ng) {
        super ();
        this.arity = ng.arity;
        this.code = ng.code;
        this.name = ng.name;
    }
    
    protected int getArity() {
        return arity;
    }
    
    protected QualifiedName getName() {
        return name;
    }
    
    protected void setCode (Code code) {
        this.code = code;
    }

    @Override
    public String toString (int indent) {
        if (getIndirectionNode() != null) {
            StringBuilder sp = new StringBuilder ();
            for (int i = 0; i < indent; ++i) {
                sp.append (" ");
            }
            
            return sp.toString() + idString(0) + " <" + name + " / " + arity + ">" + indString(0);
        } else {
            StringBuilder sp = new StringBuilder ();
            for (int i = 0; i < indent; ++i) {
                sp.append (" ");
            }
            
            return sp.toString() + idString(0) + " <" + name + " / " + arity + ">";
        }
    }
    
    /**     
     * {@inheritDoc}
     */
    @Override
    public final int debug_getNChildren() {  
        if (hasIndirection()) {
            return super.debug_getNChildren();           
        }        
     
        return 0;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public final CalValue debug_getChild(int childN) {
        if (hasIndirection()) {
            return super.debug_getChild(childN);           
        }
        
        throw new IndexOutOfBoundsException();        
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public final String debug_getNodeStartText() {
        if (hasIndirection()) {
            return super.debug_getNodeStartText();           
        }        
        
        return name.getQualifiedName();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public final String debug_getNodeEndText() {
        if (hasIndirection()) {
            return super.debug_getNodeEndText();           
        } 
        
        return "";
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public final String debug_getChildPrefixText(int childN) {
        if (hasIndirection()) {
            return super.debug_getChildPrefixText(childN);           
        }
        
        throw new IndexOutOfBoundsException();            
    }         
        
    @Override
    protected void setIndirectionNode (Node n) {
        super.setIndirectionNode(n);
        code = null;
        if (arity != 0) {
            System.out.println ("indirection node in non-CAF global");
        }
    }
    
    /*
     * Functions to perform state transitions
     */

    /**
     * Do the Eval state transition.
     */
    @Override
    protected void i_eval (Executor e) throws CALExecutorException {
        
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
    protected void i_dispatch (Executor e, int k) throws CALExecutorException {
        if (arity == 0) {
            super.i_dispatch (e, k);
        } else
        if (arity == k) {
            // Tail call case.
            // <f:S, G[f = FUN k C], Dispatch k:[], D>
            // =>
            // <S, G, C, D>
            
            // Pop this global node off of the stack.
            e.stack.pop ();
    
            // Set the instruction pointer.
            //e.setIP (new CodeOffset (code));
            e.setIP (code);
        } else 
       if (arity < k) {
            // <f:n1:n2:...:nk:r:S, G[f=FUN a C], Dispatch k:[], D>
            // a < k
            // <n1:...:na:va:...:v(k-1):r:S, G[va = HOLE                ], C, D>
            //                                [vi = AP v(i-1) ni (a<i<k)]
            //                                [r = AP v(k-1) nk         ]
            
            
            e.stack.pop ();
            int si = e.stack.size() - (arity + 1);
          
            Node n = e.stack.get (si);
            e.stack.set (si, new NInd ());
          
            for (int i = arity + 1; i < k; ++i) {
                Node na = new NAp (e.stack.get (si--), n);
                n = e.stack.get (si);
                e.stack.set (si,na);
            }
            
            Node na = new NAp (e.stack.get (si--), n);
            e.stack.get (si).setIndirectionNode (na);
            e.stack.set (si, na);

            // Set the instruction pointer.
            //e.setIP (new CodeOffset (code));
            e.setIP (code);
            
        } else
        if (arity > k) {
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
            
            if (e.stack.size() < (arity + 2)) {
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
                        
                for (int i = k; i < arity; ++i) {
                    e.stack.set (ir, ((NAp)e.stack.get(--ir)).getN2());
                }
                

//              System.out.println ("---------------");
//              System.out.println (e.showStack (0));
//              System.out.println ("---------------");
                
                // Set the instruction pointer.
                e.setIP (code);
                
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
    protected void i_unwind (Executor e) throws CALExecutorException {
        
        Executor.GStack stack = e.stack;
        
        if (getLeafNode() != this) {
            stack.pop ();
            stack.push (getLeafNode());
            //ip.setIP (ip.getIP() - 1);
            getLeafNode().i_unwind (e);
            return;
        }
                            
        // Determine the number of necessary args.
        int nArgs = arity;
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
            
            // Set the instruction pointer.
            //e.setIP (new CodeOffset (code));
            e.setIP (code);

        }
    } 
}

