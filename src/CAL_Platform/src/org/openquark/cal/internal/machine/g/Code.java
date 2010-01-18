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
 * Code.java
 * Created: Mar 20, 2003Feb 19, 2003 at 12:15:01 PM
 * By: Raymond Cypher
 */

package org.openquark.cal.internal.machine.g;

import java.io.IOException;
import java.util.List;

import org.openquark.cal.internal.serialization.ModuleSerializationTags;
import org.openquark.cal.internal.serialization.RecordOutputStream;


/**
 * This is the Code class/interface.
 *
 * Created: Mar 20, 2003
 * @author RCypher
 */
final class Code {
    private static final int serializationSchema = 0;
    private final Instruction[] instructions;       
    
    /**
     * Construct from array of Instructions.
     * @param is
     */
    Code(Instruction[] is) {      
        instructions = is;
    }

    /**
     * Construct from a List of Instructions.
     * @param is
     */
    Code(List<Instruction> is) {
        this (is.toArray(new Instruction[0]));                      
    }

    /**
     * Construct from a single instruction.
     * @param i
     */
    Code (Instruction i) {
        // Generate array and put in the single instruction!
        this (new Instruction[] {i});                          
    }
        
    /** 
     * Return true if the code sequence is the 'halt' or empty sequence.
     * @return true if this code is the halt code sequence.
     */
    public final boolean isHalt () {
        return instructions.length == 0;
    }

     /**
     * Disassemble the instructions in this Code contribution.
     * Note that this method will not show labels, use Program.toString() instead for this.
     * @return a string representation of the code
     */
    @Override
    public final String toString() {
        return toString(0);
    }

    /**
     * Disassemble this code into a string.
     * Creation date: (3/10/00 11:30:42 AM)
     * @param indent int the amount of indent to emit in the string
     * @return java.lang.String the disassembly
     */
    public final String toString(int indent) {
        StringBuilder sb = new StringBuilder();
        int instructionsLength = instructions.length;
        for (int i = 0; i < instructionsLength; i++) {
            Instruction instr = instructions[i];

            sb.append(instr.toString(indent) + "\n");
        }
        return sb.toString();
    }
    
    public final int getNInstructions(){
        return instructions.length;
    }
    
    public final Instruction getInstruction(int n) {
        return instructions[n];
    }

    /**
     * Return an iterator over the array of instructions.
     * @return the array of Instruction that make up this Code.
     */
    public final Instruction[] getInstructions () {
        return instructions;
    }
    
    /**
     * Write this Code instance to the RecordOutputStream.
     * @param s
     * @throws IOException
     */
    public void write (RecordOutputStream s) throws IOException {
        s.startRecord (ModuleSerializationTags.G_CODE, serializationSchema);
        s.writeInt (instructions.length);
        for (int i = 0; i < instructions.length; ++i) {
            instructions[i].write (s);
        }
        s.endRecord ();
    }
}