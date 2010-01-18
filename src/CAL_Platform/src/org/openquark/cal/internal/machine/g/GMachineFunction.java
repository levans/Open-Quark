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
 * GMachineFunction
 * Created: Feb 20, 2003 at 3:45:20 PM
 * By: Raymond Cypher
 */
package org.openquark.cal.internal.machine.g;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

import org.openquark.cal.compiler.CompilerMessageLogger;
import org.openquark.cal.compiler.CoreFunction;
import org.openquark.cal.compiler.DeserializationHelper;
import org.openquark.cal.compiler.ModuleTypeInfo;
import org.openquark.cal.internal.machine.MachineFunctionImpl;
import org.openquark.cal.internal.serialization.ModuleSerializationTags;
import org.openquark.cal.internal.serialization.RecordInputStream;
import org.openquark.cal.internal.serialization.RecordOutputStream;
import org.openquark.cal.machine.MachineFunction;
import org.openquark.cal.runtime.ExecutionContext;


/**
 * This is the GMachineFunction class.
 * This class adds machine specific information to a MachineFunction.
 * <p>
 * Created: Feb 20, 2003 at 3:45:18 PM
 * @author Raymond Cypher
 */
public final class GMachineFunction extends MachineFunctionImpl {
    
    private static final int serializationSchema = 0;
    private NGlobal node;
    
    /** (CALExecutor.Context->NGlobal) This is the map used to contain the different instances of a supercombinator node for CAFs.  
     * It is synchronized as will be accessed and modified by multiple threads simultaneously. */
    private final Map<ExecutionContext, NGlobal> nodeMap = new Hashtable<ExecutionContext, NGlobal>();
    
    private Code code;
    
    /**
     * Construct a label from name and offset
     */
    public GMachineFunction(CoreFunction coreFunction) {
        super (coreFunction);
    }

    private GMachineFunction () {
        
    }

    /**
     * Return the global node associated with this code label.
     * The node is created if necessary.
     * @param context
     * @return NGlobal 
     */
    NGlobal makeNGlobal (ExecutionContext context) {
        if (!isForeignFunction() && getArity() == 0) {
            // This is a CAF, want to return node specific to context.
            NGlobal cnode = nodeMap.get(context);
            if (cnode == null) {
                cnode = new NGlobal (getArity(), (getCode() != null) ? getCode() : null, getQualifiedName());
                nodeMap.put (context, cnode);
            }
            return cnode;
        } else {
            if (node == null) {
                node = new NGlobal (getArity(), (getCode() != null) ? getCode() : null, getQualifiedName());
            }
            return node;
        }
    }
    
    /**
     * Reset the cached global node to null.  This has the effect of releasing cached
     * CAF results.
     */
    void resetNGlobal (ExecutionContext context) {
        if (!isForeignFunction() && getArity() == 0) {
            nodeMap.remove(context);
        } else {
            if (node != null && node.getIndirectionNode() != null) {
                node.clearIndirectionNode();
                node.setCode(getCode());
            }
        }
    }
    
    /**
     * Set the code for this label.
     * @param code Code
     */
    public void setCode (Code code) {
        this.code = code;
        if (node != null) {
            node.setCode (code);
        }
    }
    
    /**
     * @return Returns the code.
     */
    public Code getCode() {
        return code;
    }
    
    /**
     * @return the disassembled form of the function.
     */
    @Override
    public String getDisassembly () {
        if (getCode() == null) {
            return "No code available for " + getQualifiedName();
        }
        return getCode().toString();
    }
    
    @Override
    public final void write (RecordOutputStream s) throws IOException {
        s.startRecord (ModuleSerializationTags.G_MACHINE_FUNCTION, serializationSchema);
        super.write(s);
        code.write (s);
        s.endRecord ();
    }
    
    /**
     * Load an instance of GMachineFunciton.
     * Read position in stream is after record header.
     * @param s
     * @param schema
     * @param mti
     * @param msgLogger the logger to which to log deserialization messages.
     * @throws IOException
     * @return an instance of GMachineFunction
     */
    public static final MachineFunction load (RecordInputStream s, int schema, ModuleTypeInfo mti, CompilerMessageLogger msgLogger) throws IOException {
        DeserializationHelper.checkSerializationSchema(schema, serializationSchema, mti.getModuleName(), "GMachineFunction", msgLogger);
        GMachineFunction gmf = new GMachineFunction();
        gmf.readContent(s, mti, msgLogger);
        return gmf;
    }
    
    /**
     * Read the content of a GMachineFunciton.
     * Read position in stream is after record header.
     * @param s
     * @param mti
     * @param msgLogger the logger to which to log deserialization messages.
     * @throws IOException
     */
    private void readContent (RecordInputStream s, ModuleTypeInfo mti, CompilerMessageLogger msgLogger) throws IOException {
        // Read the MachineFunction content.
        super.read(s, mti, msgLogger);
        
        //code = Code.load(s, mti);
        s.skipRestOfRecord();
    }
    
}
