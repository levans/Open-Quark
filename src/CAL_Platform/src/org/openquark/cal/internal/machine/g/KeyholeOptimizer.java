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
 * KeyholeOptimizer.java
 * Created: Sep 23, 2002 at 3:49:12 PM
 * By: Raymond Cypher
 */
package org.openquark.cal.internal.machine.g;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openquark.cal.compiler.DataConstructor;
import org.openquark.cal.internal.machine.CodeGenerationException;
import org.openquark.cal.machine.Program.ProgramException;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;


/** 
 * This is the KeyholeOptimizer class
 *
 * Does code optimizations by scanning
 * instruction sequences for replaceable
 * patterns.
 * 
 * @author Raymond Cypher
 */
class KeyholeOptimizer {

    /**
     * Create a KeyholeOptimizer
     */
    public KeyholeOptimizer () {

    }
    
    public  Code optimizeCode (Code code) throws CodeGenerationException {
        InstructionList gp = new InstructionList ();
        gp.code (code.getInstructions ());
        gp = optimizeCode (gp);
        return new Code (gp);
    }
    

    public  InstructionList optimizeCode (InstructionList code) throws CodeGenerationException {
        try {
            code = collapseMkaps (code);
            code = collapseSlides (code);
            code = removeTrueFalseFunction (code);
            
            Iterator<Instruction> it = code.iterator ();
            while (it.hasNext ()) {
                Instruction i = it.next ();
                optimizeInstruction (i);
            }

            code = fixupI_Cond (code);
            code = fixupI_Switch (code);
            
        } catch (ProgramException e) {
        }
                
        return code;
    }
    
    
    /**
     * Optimize the code contributions of a given instruction (if any).
     */
    private void optimizeInstruction (Instruction i) throws CodeGenerationException {
        if (i instanceof Instruction.I_Cond) {
            Instruction.I_Cond i_cond = (Instruction.I_Cond)i;
            i_cond.setTrueCode (optimizeCode (i_cond.getTrueCode()));
            i_cond.setFalseCode (optimizeCode (i_cond.getFalseCode()));
            
        } else if (i instanceof Instruction.I_Switch) {
            
            Instruction.I_Switch i_switch = (Instruction.I_Switch)i;
            java.util.Iterator<?> keysIterator = i_switch.getMap().keySet().iterator();
            while (keysIterator.hasNext()) {
                Object altTag = keysIterator.next();
                Code code = (Code)i_switch.getMap().get(altTag);
                code = optimizeCode (code);
                i_switch.getMap().put (altTag, code);                
            }
        }
    }
    
    
    /**
     * Collapse sequential I_Mkap instructions into a single I_MkapN instruction.
     * @param code InstructionList
     * @return InstructionList
     */
    private  InstructionList collapseMkaps (InstructionList code) {
        Iterator<Instruction> it = code.iterator ();
        InstructionList newInstructions = new InstructionList ();
        Instruction lastI = null;
        
        while (it.hasNext ()) {
            Instruction i = it.next ();
                    
            if (i instanceof Instruction.I_MkapN && lastI instanceof Instruction.I_MkapN) {
                Instruction.I_MkapN ni = new Instruction.I_MkapN (lastI.getN() + i.getN());
                newInstructions.remove(newInstructions.size() - 1);
                newInstructions.add (ni);
                lastI = ni;
            } else {
                newInstructions.add (i);
                lastI = i;
            }
        }
        
        return newInstructions;
    }    

    /**
     * Collapse sequential I_Slide instructions into a single I_Slide instruction.
     * @param code InstructionList
     * @return InstructionList
     */
    private  InstructionList collapseSlides (InstructionList code) {
        Iterator<Instruction> it = code.iterator ();
        InstructionList newInstructions = new InstructionList ();
        Instruction lastI = null;
        
        while (it.hasNext ()) {
            Instruction i = it.next ();
                    
            if (i instanceof Instruction.I_Slide && lastI instanceof Instruction.I_Slide) {
                Instruction.I_Slide ni = new Instruction.I_Slide (lastI.getN() + i.getN());
                newInstructions.remove (newInstructions.size() - 1);
                newInstructions.add (ni);
                lastI = ni;
            } else {
                newInstructions.add (i);
                lastI = i;
            }
        }
        
        return newInstructions;
    }    
    
    
    /**
     * Replace the sequence I_PushGlobal <Prelude.False>, I_Eval or the
     * sequence I_PushGlobal <Prelude.True>, I_Eval or I_PackCons0 (boolean tag) 
     * with a simple push of a boolean value node.
     * @param code InstructionList
     * @throws ProgramException
     * @return InstructionList
     */
    private  InstructionList removeTrueFalseFunction (InstructionList code) throws ProgramException {
        Iterator<Instruction> it = code.iterator ();
        InstructionList newInstructions = new InstructionList ();
        boolean dropped = false;

        // Remove the I_PushGlobal I_Eval that resolves to a boolean.
        while (it.hasNext ()) {
            Instruction i = it.next ();
                    
             
            if (i instanceof Instruction.I_PushGlobal && 
                 (i.getName().equals (CAL_Prelude.DataConstructors.False) || 
                  i.getName().equals (CAL_Prelude.DataConstructors.True))) {

                dropped = true;
                if (i.getName().equals (CAL_Prelude.DataConstructors.True)) {
                    newInstructions.add (Instruction.I_PushTrue);
                } else {
                    newInstructions.add (Instruction.I_PushFalse);
                }
            } else {
                if (i != Instruction.I_Eval) {
                    newInstructions.add (i);
                } else {
                    if (!dropped) {
                        newInstructions.add (i);
                    }
                }
                dropped = false;
            }
        }

        // Remove packCons0 instructions that resolve to a boolean.        
        code = newInstructions;
        it = code.iterator ();
        newInstructions = new InstructionList ();
        while (it.hasNext ()) {
            Instruction i = it.next ();
            if (i instanceof Instruction.I_PackCons0) {
                if (((Instruction.I_PackCons0)i).getInfo() == Boolean.TRUE) {
                    newInstructions.add (Instruction.I_PushTrue);
                } else
                if (((Instruction.I_PackCons0)i).getInfo() == Boolean.FALSE) {
                    newInstructions.add (Instruction.I_PushFalse);
                }else
                if (((Instruction.I_PackCons0)i).getInfo() instanceof DataConstructor &&
                    ((DataConstructor)((Instruction.I_PackCons0)i).getInfo()).getName().equals (CAL_Prelude.DataConstructors.True)) {
                    newInstructions.add (Instruction.I_PushTrue);
                } else
                if (((Instruction.I_PackCons0)i).getInfo() instanceof DataConstructor &&
                    ((DataConstructor)((Instruction.I_PackCons0)i).getInfo()).getName().equals (CAL_Prelude.DataConstructors.False)) {
                    newInstructions.add (Instruction.I_PushFalse);
                } else {
                    newInstructions.add (i);
                }
            } else {
                newInstructions.add (i);
            }
        }
        
        return newInstructions;
    }

    private InstructionList fixupI_Cond (InstructionList code) {
        boolean change = true;
        InstructionList oldCode = code;
        
        while (change) {
            change = false;
            Iterator<Instruction> it = oldCode.iterator();
            InstructionList newCode = new InstructionList ();
            
            while (it.hasNext ()) {
                Instruction i = it.next ();
                if (i instanceof Instruction.I_Cond) {
                    change = true;
                    Instruction.I_Cond ic = (Instruction.I_Cond)i;
                    Code thenC = ic.getTrueCode();
                    Code elseC = ic.getFalseCode();
                    
                    Instruction.I_CondJ ij = new Instruction.I_CondJ (elseC.getNInstructions() + 1);
                    Instruction.I_Jump jump = new Instruction.I_Jump (thenC.getNInstructions());
                    
                    newCode.add (ij);
                    newCode.code (elseC.getInstructions());
                    newCode.add (jump);
                    newCode.code (thenC.getInstructions());
                    
                } else {
                    newCode.add (i);
                }
            }
            
            oldCode = newCode;
        }
        
        return oldCode;
    }

    private InstructionList fixupI_Switch (InstructionList code) throws CodeGenerationException {
        boolean change = true;
        InstructionList oldCode = code;
        while (change) {
            change = false;
            Iterator<Instruction> it = oldCode.iterator();
            InstructionList newCode = new InstructionList ();
            
            while (it.hasNext ()) {
                Instruction inst = it.next ();
                if (inst instanceof Instruction.I_Switch) {
                    change = true;
                    Instruction.I_Switch is = (Instruction.I_Switch)inst;
    
                    Map<Object, Integer> jumpMap = new HashMap<Object, Integer> ();
                    Map<Object, Object> codesMap = is.getMap();
                    
                    newCode.add (new Instruction.I_SwitchJ (jumpMap, is.getErrorInfo()));
                    List<Object> tags = new ArrayList<Object> ();
                    List<Code> codes = new ArrayList<Code> ();
                    
                    int totalLengthOfAltCode = 0;
                    for (final Map.Entry<Object, Object> entry : codesMap.entrySet()) {                       
                        Object altTag = entry.getKey();
                        Code altCode = (Code)entry.getValue();
                        tags.add (altTag);
                        codes.add (altCode);
                        totalLengthOfAltCode += altCode.getNInstructions();
                    }
                    
                    totalLengthOfAltCode += (tags.size() - 1);
                    
                    int jump = 0;
                    for (int i = 0, nTags = tags.size(); i < nTags; ++i) {
                        Object tag = tags.get (i);
                        if (tag instanceof Boolean) {
                            jumpMap.put(Integer.valueOf(((Boolean)tag).booleanValue() ? 1 : 0), Integer.valueOf(jump));
                        } else
                        if (tag instanceof DataConstructor) {
                            jumpMap.put(Integer.valueOf(((DataConstructor)tag).getOrdinal()), Integer.valueOf(jump));
                        } else 
                        if (tag instanceof Integer) {
                            jumpMap.put(tag, Integer.valueOf(jump));
                        } else 
                        if (tag instanceof Character) {
                            jumpMap.put(Integer.valueOf(((Character)tag).charValue()), Integer.valueOf(jump));
                        } else {
                            if (!(tag instanceof String)) {
                                throw new CodeGenerationException("Invalid tag type in switch: " + tag.getClass().getName());
                            }
                            jumpMap.put(tag, Integer.valueOf(jump));
                        }
                        
                        jump += (codes.get (i).getNInstructions() + 1);
                        newCode.code (codes.get (i).getInstructions());
                        if (totalLengthOfAltCode - jump > 0) {
                            newCode.code (new Instruction.I_Jump (totalLengthOfAltCode - jump));
                        }
                    }
                    
                } else {
                    newCode.add (inst);
                }
            }
            
            oldCode = newCode;
        }
        
        return oldCode;
    }

}
