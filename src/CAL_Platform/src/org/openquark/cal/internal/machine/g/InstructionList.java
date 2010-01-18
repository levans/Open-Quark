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
 * InstructionList.java
 * Creation date: (March 15, 2000)
 * By: Luke Evans
 */

package org.openquark.cal.internal.machine.g;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A list of instructions.
 * Creation date: (3/15/00 4:13:28 PM)
 * @author LWE
 */
class InstructionList extends ArrayList<Instruction> {
        
    private static final long serialVersionUID = -3077079888038980495L;

    /**
     * Default InstructionList constructor.
     */
    public InstructionList() {
        super();
    }
    
    /**
     * Construct InstructionList from an initial instruction sequence.
     * @param is Instruction[] the instructions with which to pre-load the InstructionList
     */
    public InstructionList(Instruction[] is) {
        super(Arrays.asList(is));       
    }
    
    /**
     * Construct InstructionList from an initial capacity.
     * @param initialCapacity int number of items initially sized for
     */
    public InstructionList(int initialCapacity) {
        super(initialCapacity);
    }
            
    /**
     * Construct InstructionList from an initial instruction.
     * @param i Instruction the instruction with which to pre-load the InstructionList
     */
    public InstructionList(Instruction i) {
        this();
        code(i);
    }
    
    /**
     * Code a sequence of instructions into the InstructionList.
     * Creation date: (3/7/00 4:42:16 PM)
     * @param is Instruction[] the instructions
     */
    public InstructionList code(Instruction[] is) {
        // Add these instructions to the instruction list
       
        addAll(Arrays.asList(is));

        // Return self - this is just a convenience to avoid intermediate variables in some cases
        return this;
    }    
    
    /**
     * Code a sequence of instructions (as an object array) into the InstructionList.
     * Creation date: (3/7/00 4:42:16 PM)
     * @param is the instructions
     */
    public InstructionList code(List<Instruction> is) {
        // Add these instructions to the instruction list
        addAll (is);

        // Return self - this is just a convenience to avoid intermediate variables in some cases
        return this;
    }
             
    /**
     * Code a single instruction into the InstructionList.
     * Creation date: (3/7/00 4:42:16 PM)
     * @param i the instruction
     */
    public InstructionList code(Instruction i) {
        // Add the instruction to the instruction list
        add(i);

        // Return self - this is just a convenience to avoid intermediate variables in some cases
        return this;
    }
    
    /**
     * Add the given InstuctionList to the end of this one.
     * Creation date: (3/15/00 4:15:55 PM)
     * @param il the list to append
     * @return InstructionList self
     */
    public InstructionList code(InstructionList il) {
               
        addAll(il);

        // Return self - this is just a convenience to avoid intermediate variables in some cases
        return this;
    }       
}