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
 * Instruction.java
 * Creation date: (March 3 2000 5:21:47 PM)
 * By: Raymond Cypher
 */
package org.openquark.cal.internal.machine.g;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Map;

import org.openquark.cal.compiler.DataConstructor;
import org.openquark.cal.compiler.ForeignFunctionInfo;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.internal.serialization.ModuleSerializationTags;
import org.openquark.cal.internal.serialization.RecordOutputStream;
import org.openquark.cal.machine.CALExecutor;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.runtime.CALExecutorException;
import org.openquark.cal.runtime.ErrorInfo;
import org.openquark.util.UnsafeCast;


/**
 * A G-Machine instruction.
 * Creation date: (3/3/00 5:21:47 PM)
 * @author Raymond Cypher
 *
 * All G-Machine instructions are inheriting static nested classes of this class
 * The inherited classes should not contain any data members.  If an inherited class
 * is created which contains a data member then the serialization mechanism will
 * need to be updated.
 *
 */

abstract class Instruction {
    
    static final int T_Alloc = 1;
    static final int T_Call = 2;
    static final int T_Cond = 3;
    static final int T_CondJ = 4;
    static final int T_Dispatch = 5;
    static final int T_ForeignFunctionCall = 6;
    static final int T_ClearStack = 7;
    static final int T_Eval = 8;
    static final int T_Instrument = 9;
    static final int T_PushFalse = 10;
    static final int T_PushTrue = 11;
    static final int T_Unwind = 12;
    static final int T_Jump = 13;
    static final int T_MkapN = 14;
    static final int T_PackCons = 15;
    static final int T_PackCons0 = 16;
    static final int T_PackCons2 = 17;
    static final int T_Pop = 18;
    static final int T_PrimOp = 19;
    static final int T_Push = 20;
    static final int T_PushGlobal = 21;
    static final int T_PushGlobalN = 22;
    static final int T_PushList = 23;
    static final int T_PushString = 24;
    static final int T_PushVVal = 25;
    static final int T_Slide = 26;
    static final int T_Split = 27;
    static final int T_Squeeze = 28;
    static final int T_Switch = 29;
    static final int T_SwitchJ = 30;
    static final int T_Update = 31;
    static final int T_CreateRecord = 32;
    static final int T_PutRecordField = 33;
    static final int T_ExtendRecord = 34;
    static final int T_RecordSelection = 35;
    static final int T_LazyRecordSelection = 36;
    static final int T_LazyRecordExtension = 37;
    static final int T_RemoveRecordField = 38;
    static final int T_Println = 39;
    static final int T_CreateList = 40;
    static final int T_PutListValue = 41;
    static final int T_Cast = 42;
    static final int T_PushPrimitiveNode = 43;
    static final int T_SelectDCField = 44;
    static final int T_LazySelectDCField = 45;
    static final int T_DebugProcessing = 46;
    static final int T_LazyRecordUpdate = 47;
    
    static final I_IClearStack I_ClearStack = new I_IClearStack ();
    static final I_IEval I_Eval = new I_IEval ();
    static final I_IUnwind I_Unwind = new I_IUnwind ();
    static final I_IPushTrue I_PushTrue = new I_IPushTrue ();
    static final I_IPushFalse I_PushFalse = new I_IPushFalse ();

    public static final int serializationSchema = 0;

    
    private final int tag;

    private final int n;                // arity
    private final int m;                // dc ordinal
    private final Object info;          // dc runtime tag, PrimOp
    private final boolean bool;
    private final QualifiedName name;

    Instruction (int tag) {
        this.tag = tag;
        this.n = -1;
        this.m = -1;
        this.info = null;
        this.bool = true;
        this.name = null;
    }

    Instruction (int tag, int n) {
        this.tag = tag;
        this.n = n;
        this.m = -1;
        this.info = null;
        this.bool = true;
        this.name = null;
    }

    Instruction (int tag, int n, int m) {
        this.tag = tag;
        this.n = n;
        this.m = m;
        this.info = null;
        this.bool = true;
        this.name = null;
    }

    Instruction (int tag, Node node, boolean bool) {
        this.tag = tag;
        this.n = -1;
        this.m = -1;
        this.info = node;
        this.bool = bool;
        this.name = null;
    }

    Instruction (int tag, Object info) {
        this.tag = tag;
        this.n = -1;
        this.m = -1;
        this.info = info;
        this.bool = true;
        this.name = null;
    }

    Instruction (int tag, int n, Object info) {
        this.tag = tag;
        this.n = n;
        this.m = -1;
        this.info = info;
        this.bool = true;
        this.name = null;
    }

    Instruction (int tag, int n, int m, Object info, QualifiedName name) {
        this.tag = tag;
        this.n = n;
        this.m = m;
        this.info = info;
        this.bool = true;
        this.name = name;
    }

    int getTag () {
        return tag;
    }
    
    @Override
    public abstract String toString();
    
    public abstract String toString(int indent);
    
    public void exec(CALExecutor e) throws CALExecutorException {
    }

    /**
     * @return n (arity)
     */
    final int getN () {
        return n;    
    }

    final Object getInfo () {
        return info;    
    }
    
    final int getX () {
        return n;
    }
    
    final int getY () {
        return m;
    }

    /**
     * @return m (dc ordinal)
     */
    final int getOrd () {
        return m;
    }
        
    /**
     * @return n (arity)
     */
    final int getArity () {
        return n;
    }

    final QualifiedName getName () {
        if (name != null) {
            return name;
        } else {
            return (QualifiedName)info; 
        }
    }
    
    Node getNode () {
        return (Node)info;
    }
    
    final Map<Object, Object> getMap () {
        return UnsafeCast.unsafeCast(info);
    }

    final boolean getBool () {
        return bool;    
    }

    public final Node getGlobalNode () {
        if (getBool()) {
            return ((NGlobal)getNode()).getLeafNode();
        } else {
            return new NGlobal ((NGlobal)getNode());
        }
    }
    
    public void reset (Object o) {}
    
    /**
     * Write this Instruction instance to the RecordOutputStream.
     * @param s
     * @throws IOException
     */
    public void write (RecordOutputStream s) throws IOException {
        s.startRecord (ModuleSerializationTags.G_INSTRUCTION, serializationSchema);
        s.writeInt (tag);
        s.writeBoolean (bool);
        s.writeInt (m);
        s.writeInt (n);
        s.writeBoolean(name != null);
        if (name != null) {
            s.writeQualifiedName(name);
        }
        s.writeBoolean (info != null);
        if (info != null) {
            writeInfo (s);
        }
        s.endRecord ();
    }

    /**
     * Write out the info object based on the actual type.
     * @param s
     * @throws IOException
     */
    private void writeInfo (RecordOutputStream s) throws IOException {
        switch (tag) {
            case T_ForeignFunctionCall:
            {
                // The ForeignFunctionInfo instance is saved as a qualified name
                // which can be used for a lookup into the ModuleTypeInfo at
                // load time.
                ForeignFunctionInfo ffi = (ForeignFunctionInfo)info;
                // CAL name of the foreign function
                s.writeQualifiedName(ffi.getCalName());
            }
            break;
                
            case T_Instrument:
            {
                if (!(info instanceof Executor.CallCountInfo)) {
                    throw new IOException ("Unexpected type " + info.getClass().getName() + " encountered in I_Instrument instruction.");
                }
                
                Executor.CallCountInfo cci = (Executor.CallCountInfo)info;
                s.writeQualifiedName(cci.getName());
                s.writeUTF(cci.getType());
            }
            break;
            
            case T_LazyRecordSelection: 
            {
                if (!(info instanceof String)) {
                    throw new IOException ("Unexpected type encountered in I_LazyRecordSelection: " + info.getClass().getName());
                }
                s.writeUTF ((String)info);
            }
            break;
            
            case T_Println:
            {
                if (!(info instanceof String)) {
                    throw new IOException ("Unexpected type encountered in I_Println: " + info.getClass().getName());
                }
                s.writeUTF ((String)info);
            }
            break;
            
            case T_PushGlobal:
            {
                if (!(info instanceof QualifiedName)) {
                    throw new IOException ("Unexpected type encountered in I_PushGlobal: " + info.getClass().getName());
                }
                s.writeQualifiedName(((QualifiedName)info));
            }
            break;
                
            case T_PushVVal:
            {
                if (!(info instanceof NVal)) {
                    throw new IOException ("Unexpected type encountered in I_PushVVal: " + info.getClass().getName());
                }
                
                if (info instanceof NValBoolean) {
                    s.writeUTF("boolean");
                    s.writeBoolean(((NValBoolean)info).getBooleanValue());
                } else 
                if (info instanceof NValChar) {
                    s.writeUTF("char");
                    s.writeChar (((NValChar)info).getCharValue());
                } else
                if (info instanceof NValDouble) {
                    s.writeUTF("double");
                    s.writeDouble (((NValDouble)info).getDoubleValue());
                } else
                if (info instanceof NValInt) {
                    s.writeUTF("int");
                    s.writeInt (((NValInt)info).getIntValue());
                } else
                if (info instanceof NValLong) {
                    s.writeUTF("long");
                    s.writeLong (((NValLong)info).getLongValue());
                } else
                if (info instanceof NValShort) {
                    s.writeUTF("short");
                    s.writeShort (((NValShort)info).getShortValue());
                } else
                if (info instanceof NValByte) {
                    s.writeUTF("byte");
                    s.writeByte (((NValByte)info).getByteValue());
                } else
                if (info instanceof NValFloat) {
                    s.writeUTF("float");
                    s.writeFloat (((NValFloat)info).getFloatValue());
                } else
                if (info instanceof NValObject) {
                    Object value = ((NValObject)info).getValue();
                    if (value instanceof String) {
                        s.writeUTF("string");
                        s.writeUTF ((String)value);
                    } else
                    if (value instanceof BigInteger) {
                        s.writeUTF("biginteger");
                        String sv = ((BigInteger)value).toString();
                        s.writeUTF (sv);
                    } else {
                        throw new IOException ("Unexpected value type in I_PushVVal: " + ((Node)info).getValue().getClass().getName());
                    }
                } else {
                    throw new IOException ("Unexpected value type in I_PushVVal: " + info.getClass().getName());
                }
            }
            break;
                
            case T_PutRecordField:
            {
                s.writeUTF ((String)info);
            }
            break;
            
            case T_RecordSelection:
            {
                s.writeUTF ((String)info);
            }
            break;
                
            case T_RemoveRecordField:
            {
                s.writeUTF ((String)info);
            }
            break;
                
            case T_SwitchJ:
            {
                Map<Object, Object> m = getMap();
                s.writeInt (m.size ());               
                for (final Map.Entry<Object, Object> entry : m.entrySet()) {                  
                    Object o = entry.getKey();
                    if (!(o instanceof Integer) && !(o instanceof String)) {
                        throw new IOException ("Invalid tag type in I_SwitchJ instruction: " + o.getClass().getName());
                    }
                    s.writeBoolean(o instanceof Integer);
                    if (o instanceof Integer) {
                        s.writeInt(((Integer)o).intValue());
                    } else {
                        s.writeUTF((String)o);
                    }
                    Integer val = (Integer)entry.getValue();
                    s.writeInt(val.intValue());
                }
            }
            break;
                
            case T_PushGlobalN:
            {
                // Save by name.  This will need to be resolved to the node
                // held by the corresponding GMachineFunction at load time.
                s.writeQualifiedName(((NGlobal)info).getName());
            }
            break;
                
            case T_PrimOp:
            {
                // Simply store the primop code and lookup
                // the object at load time.
                PrimOp pop = (PrimOp)info;
                int code = pop.getCode();
                s.writeInt(code);
            }
            break;
                
            case T_PackCons:
            case T_PackCons0:
            case T_PackCons2:
            {
                // We save the data constructor by name.
                DataConstructor dc = (DataConstructor)info;
                s.writeQualifiedName(dc.getName());
            }   
            break;
            
            default:
                if (info != null) {
                    throw new IOException ("Unexpected instruction tag " + tag + " saving instruction info.");
                }
            break;
        }
    }
    
    /**
     * Unwind
     * 
     */
    private static class I_IUnwind extends Instruction {
        
        /**
         * Construct from k.
         */
        I_IUnwind () {
            super (T_Unwind);
        }

        /**
         * Provide description of instruction.
         */
        @Override
        public String toString() {
            return "I_Unwind";
        }

        /**
         * Provide description of instruction with indent threading.
         */
        @Override
        public String toString(int indent) {
            StringBuilder is = new StringBuilder();
            for (int i = 0; i < indent; ++i) {
                is.append(" ");
            }
            return is.toString() + toString();
        }

    }

    /**
     * Slide n
     * 
     */
    static class I_Slide extends Instruction {
      
        /**
         * Construct from n.
         */
        I_Slide (int n) {
            super (T_Slide, n);
        }

        /**
         * Provide description of instruction.
         */
        @Override
        public String toString() {
            return "I_Slide <" + getN() + ">";
        }

        /**
         * Provide description of instruction with indent threading.
         */
        @Override
        public String toString(int indent) {
            StringBuilder is = new StringBuilder();
            for (int i = 0; i < indent; ++i) {
                is.append(" ");
            }
            return is.toString() + toString();
        }
    }
    

    /**
     * Jump n.
     * 
     */
    static class I_Jump extends Instruction {

        /**
         * Default Constructor.
         */
        I_Jump (int n) {
            super (T_Jump, n);
        }
        
        /**
         * Provide description of instruction.
         */
        @Override
        public String toString() {
            return "I_Jump <" + getN() + ">";
        }

        /**
         * Provide description of instruction with indent threading.
         */
        @Override
        public String toString(int indent) {
            StringBuilder is = new StringBuilder();
            for (int i = 0; i < indent; ++i) {
                is.append(" ");
            }
            return is.toString() + toString();
        }
    }

    /**
     * MkapN n
     * 
     */
    static class I_MkapN extends Instruction {
        
        /**
         * Default Constructor.
         */
        I_MkapN () {
            super (T_MkapN, 1);
        }
        
        /**
         * Construct from n.
         */
        I_MkapN (int n) {
            super (T_MkapN, n);
        }

        /**
         * Provide description of instruction.
         */
        @Override
        public String toString() {
            return "I_MkapN <" + getN() + ">";
        }

        /**
         * Provide description of instruction with indent threading.
         */
        @Override
        public String toString(int indent) {
            StringBuilder is = new StringBuilder();
            for (int i = 0; i < indent; ++i) {
                is.append(" ");
            }
            return is.toString() + toString();
        }
    }
    
    /**
     * PushGlobalN
     * Pushes a global sc fetched by index.
     * 
     */
    static class I_PushGlobalN extends Instruction {   
                     
        /**
         * Construct from Code.
         */
        I_PushGlobalN (NGlobal node, boolean safe) {
            super (T_PushGlobalN, node, safe);
        }

        /**
         * Provide description of instruction.
         */
        @Override
        public String toString() {
            String s = getNode().toString ();
            return "I_PushGlobalN <" + s + ">";
        }

        /**
         * Provide description of instruction with indent threading.
         */
        @Override
        public String toString(int indent) {
            StringBuilder is = new StringBuilder();
            for (int i = 0; i < indent; ++i) {
                is.append(" ");
            }
            return is.toString() + toString();
        }
    }

    /**
     * PushPrimitiveNode
     * Pushes a primitive node.
     * 
     */
    static class I_PushPrimitiveNode extends Instruction {

        /**
         * Construct from Code.
         */
        I_PushPrimitiveNode (NPrimitiveFunc node) {
            super (T_PushPrimitiveNode, node);
        }

        /**
         * Provide description of instruction.
         */
        @Override
        public String toString() {
            String s = getNode().toString ();
            return "I_PushPrimitiveNode <" + s + ">";
        }

        /**
         * Provide description of instruction with indent threading.
         */
        @Override
        public String toString(int indent) {
            StringBuilder is = new StringBuilder();
            for (int i = 0; i < indent; ++i) {
                is.append(" ");
            }
            return is.toString() + toString();
        }
    }
    
    /**
     * Push N
     * 
     */
    static class I_Push extends Instruction {
        
        /**
         * Construct from n.
         */
        I_Push (int n) {
            super (T_Push, n);
        }

        /**
         * Provide description of instruction.
         */
        @Override
        public String toString() {
            return "I_Push <" + getN() + ">";
        }

        /**
         * Provide description of instruction with indent threading.
         */
        @Override
        public String toString(int indent) {
            StringBuilder is = new StringBuilder();
            for (int i = 0; i < indent; ++i) {
                is.append(" ");
            }
            return is.toString() + toString();
        }
    }

    /**
     * PushGlobal S
     * 
     */
    static class I_PushGlobal extends Instruction {
      
        /**
         * Construct from n.
         */
        I_PushGlobal (QualifiedName name) {
            super (T_PushGlobal, name);
        }

        /**
         * Provide description of instruction.
         */
        @Override
        public String toString() {
            return "I_PushGlobal <" + getName() + ">";
        }

        /**
         * Provide description of instruction with indent threading.
         */
        @Override
        public String toString(int indent) {
            StringBuilder is = new StringBuilder();
            for (int i = 0; i < indent; ++i) {
                is.append(" ");
            }
            return is.toString() + toString();
        }
    }

    /**
     * PUSHVVAL.
     * Push the given object value directly onto the value stack.
     */
    static class I_PushVVal extends Instruction {
      
        static I_PushVVal makePushVVal(Object v) {
            Node node = null;
            if (v instanceof Node) {
                node = (Node) v;
            } else if (v instanceof Character) {
                node = new NValChar(((Character) v).charValue());
            } else if (v instanceof Double) {
                node = new NValDouble(((Double) v).doubleValue());
            } else if (v instanceof Integer) {
                node = new NValInt(((Integer) v).intValue());
            } else if (v instanceof Long) {
                node = new NValLong(((Long) v).longValue());
            } else if (v instanceof Short) {
                node = new NValShort(((Short) v).shortValue());
            } else if (v instanceof Byte) {
                node = new NValByte(((Byte) v).byteValue());
            } else if (v instanceof Float) {
                node = new NValFloat(((Float) v).floatValue());
            } else if (v instanceof Boolean) {
                node = new NValBoolean(((Boolean)v).booleanValue());
            } else {
                node = new NValObject(v);
            }

            return new I_PushVVal(node);
        }
        
        /**
         * Construct from value object.
         */
        private I_PushVVal(Node node) {
            super (T_PushVVal, node);
        }
        
       /**
         * Provide description of instruction.
         */
        @Override
        public String toString() {
            return "I_PushVVal <" + getNode() + ">";
        }

        /**
         * Provide description of instruction with indent threading.
         */
        @Override
        public String toString(int indent) {
            StringBuilder is = new StringBuilder();
            for (int i = 0; i < indent; ++i) {
                is.append(" ");
            }
            return is + toString();
        }
    }
    
    /**
     * PUSHVVAL.
     * Push the given object value directly onto the value stack.
     */
    private static class I_IPushTrue extends Instruction {
        /**
         * Construct from value object.
         */
        I_IPushTrue (){
            super (T_PushTrue);
        }

        /**
         * Provide description of instruction.
         */
        @Override
        public String toString() {
            return "I_PushTrue <>";
        }

        /**
         * Provide description of instruction with indent threading.
         */
        @Override
        public String toString(int indent) {
            StringBuilder is = new StringBuilder();
            for (int i = 0; i < indent; ++i) {
                is.append(" ");
            }
            return is + toString();
        }
    }

    /**
     * PUSHVVAL.
     * Push the given object value directly onto the value stack.
     */
    private static class I_IPushFalse extends Instruction {
        /**
         * Construct from value object.
         */
        I_IPushFalse (){
            super (T_PushFalse);
        }

        /**
         * Provide description of instruction.
         */
        @Override
        public String toString() {
            return "I_PushFalse <>";
        }

        /**
         * Provide description of instruction with indent threading.
         */
        @Override
        public String toString(int indent) {
            StringBuilder is = new StringBuilder();
            for (int i = 0; i < indent; ++i) {
                is.append(" ");
            }
            return is + toString();
        }
    }

    /**
     * UPDATE n.
     * Overwite the (n + 1) stack item with an indirection to the top stack item.
     */
    static class I_Update extends Instruction {
        
        /**
         * Construct from value object.
         */
        I_Update (int n) {
            super (T_Update, n);
        }
        
        /**
         * Provide description of instruction.
         */
        @Override
        public String toString() {
            return "I_Update <" + getN() + ">";
        }

        /**
         * Provide description of instruction with indent threading.
         */
        @Override
        public String toString(int indent) {
            StringBuilder is = new StringBuilder();
            for (int i = 0; i < indent; ++i) {
                is.append(" ");
            }
            return is + toString();
        }
    }

    static class I_Println extends Instruction {
        /**
         * Construct from value object.
         */
        I_Println (String s) {
            super (T_Println, s);
        }
        
        /**
         * Provide description of instruction.
         */
        @Override
        public String toString() {
            return "I_Println <" + getInfo() + ">";
        }

        /**
         * Provide description of instruction with indent threading.
         */
        @Override
        public String toString(int indent) {
            StringBuilder is = new StringBuilder();
            for (int i = 0; i < indent; ++i) {
                is.append(" ");
            }
            return is + toString();
        }
    }
    

    /**
     * POP n.
     * Pop n items off the stack.
     */
    static class I_Pop extends Instruction {
        
        /**
         * Construct from value object.
         */
        I_Pop (int n) {
            super (T_Pop, n);
        }
        
        /**
         * Provide description of instruction.
         */
        @Override
        public String toString() {
            return "I_Pop <" + getN() + ">";
        }

        /**
         * Provide description of instruction with indent threading.
         */
        @Override
        public String toString(int indent) {
            StringBuilder is = new StringBuilder();
            for (int i = 0; i < indent; ++i) {
                is.append(" ");
            }
            return is + toString();
        }
    }

    /**
     * Instrument.
     * Instrument the code stream with an arbitary object.
     */
    static class I_Instrument extends Instruction {

        /**
         * Construct from instrumenting object.
         */
        I_Instrument(Object object) {
            super (T_Instrument, object);
        }

        /**
         * Provide description of instruction.
         */
        @Override
        public String toString() {
            return "I_Instrument <" + getInfo() + ">";
        }

        /**
         * Provide description of instruction with indent threading.
         */
        @Override
        public String toString(int indent) {
            StringBuilder is = new StringBuilder();
            for (int i = 0; i < indent; ++i) {
                is.append(" ");
            }
            return is + toString();
        }

    }

    /**
     * Instrument.
     * Instrument the code stream with an arbitary object.
     */
    static class I_Alloc extends Instruction {
        
        /**
         * Construct from instrumenting object.
         */
        I_Alloc (int n) {
            super (T_Alloc, n);
        }

        /**
         * Provide description of instruction.
         */
        @Override
        public String toString() {
            return "I_Alloc <" + getN() + ">";
        }

        /**
         * Provide description of instruction with indent threading.
         */
        @Override
        public String toString(int indent) {
            StringBuilder is = new StringBuilder();
            for (int i = 0; i < indent; ++i) {
                is.append(" ");
            }
            return is + toString();
        }

    }

    /**
     * Eval
     * Force the strict evaluation of an expression.
     */
    private static class I_IEval extends Instruction {
       
        /**
         * Construct from instrumenting object.
         */
        I_IEval () {
            super (T_Eval);
        }

        /**
         * Provide description of instruction.
         */
        @Override
        public String toString() {
            return "I_Eval";
        }

        /**
         * Provide description of instruction with indent threading.
         */
        @Override
        public String toString(int indent) {
            StringBuilder is = new StringBuilder();
            for (int i = 0; i < indent; ++i) {
                is.append(" ");
            }
            return is + toString();
        }

    }

    /**
     * Cond.
     * Peform a conditional branch.
     */
    static class I_Cond extends Instruction {
      
        private Code c1;
        private Code c2;       
        
        /**
         * Construct from instrumenting object.
         */
        I_Cond (Code c1, Code c2) {
            super (T_Cond);
            this.c1 = c1;
            this.c2 = c2;          
        }      
        
        public Code getTrueCode () {
            return c1;
        }
        public void setTrueCode (Code c) {
            this.c1 = c;
        }
        public Code getFalseCode () {
            return c2;
        }
        public void setFalseCode (Code c) {
            this.c2 = c;
        }
        
        /**
         * Provide description of instruction.
         */
        @Override
        public String toString() {
            return toString (0);
        }

        /**
         * Provide description of instruction with indent threading.
         */
        @Override
        public String toString(int indent) {
            StringBuilder is = new StringBuilder();
            for (int i = 0; i < indent; ++i) {
                is.append(" ");
            }

            StringBuilder sb = new StringBuilder ();
            sb.append(is);
            sb.append("I_Cond\n");
            sb.append(is + "  Branch 1:\n");
            sb.append (c1.toString (indent + 4));
            sb.append (is + "  Branch 2:\n");
            sb.append (c2.toString (indent + 4));
            return sb.toString ();
        }

    }

    /**
     * CondJ.
     * Peform a conditional jump.
     */
    static class I_CondJ extends Instruction {

        /**
         * Construct from instrumenting object.
         */
        I_CondJ (int n) {
            super (T_CondJ, n);
        }
        
        /**
         * Provide description of instruction.
         */
        @Override
        public String toString() {
            return toString (0);
        }

        /**
         * Provide description of instruction with indent threading.
         */
        @Override
        public String toString(int indent) {
            StringBuilder is = new StringBuilder();
            for (int i = 0; i < indent; ++i) {
                is.append(" ");
            }

            StringBuilder sb = new StringBuilder ();
            sb.append(is);
            sb.append("I_CondJ <" + getN() + ">");
            return sb.toString ();
        }

    }
     
    /**
     * PrimOp.
     * Perform a primitive operation on the value stack.
     */
    static class I_PrimOp extends Instruction {
     
        /**
         * Construct from PrimOp sub-code.
         */
        I_PrimOp(int subCode) {
            super (T_PrimOp, PrimOp.getPrimOpForCode (subCode).getArity(), PrimOp.getPrimOpForCode (subCode));
        }

        /**
         * Provide description of instruction.
         */
        @Override
        public String toString() {
            return "I_PrimOp <" + ((PrimOp)getInfo()).getDescription () + ">";
        }


        /**
         * Provide description of instruction with indent threading.
         */
        @Override
        public String toString(int indent) {
            StringBuilder is = new StringBuilder();
            for (int i = 0; i < indent; ++i) {
                is.append(" ");
            }
            return is + toString();
        }

        
    }

    /**
     * I_PackCons.
     */
    static class I_PackCons extends Instruction {
      
        private I_PackCons(DataConstructor dataConstructor) {
            super (T_PackCons, dataConstructor.getArity(), dataConstructor.getOrdinal(), dataConstructor, dataConstructor.getName());
        }
        
        static Instruction makePackCons(DataConstructor dc) {
            
            int arity = dc.getArity ();            
            
            Instruction instruction;

            if (arity == 0) {
                if (dc.getName().equals(CAL_Prelude.DataConstructors.True)) {
                    instruction = I_PushVVal.makePushVVal(Boolean.TRUE);
                } else
                if (dc.getName().equals(CAL_Prelude.DataConstructors.False)) {
                    instruction = I_PushVVal.makePushVVal(Boolean.FALSE);
                } else {
                    instruction = new Instruction.I_PackCons0 (dc);
                }
            } else
            if (arity == 2) {
                instruction = new Instruction.I_PackCons2 (dc);
            } else {
                instruction = new Instruction.I_PackCons (dc);
            }
            
            return instruction;
        }  
        
        /**
         * Provide description of instruction.
         */
        @Override
        public String toString() {
            return "I_PackCons <" + getInfo() + ", " + getOrd() + ", " + getArity() + ">";
        }

        /**
         * Provide description of instruction with indent threading.
         */
        @Override
        public String toString(int indent) {
            StringBuilder is = new StringBuilder();
            for (int i = 0; i < indent; ++i) {
                is.append(" ");
            }
            return is + toString();
        }

    }
    
    /**
     * I_PackCons0.
     */
    static class I_PackCons0 extends Instruction {  

        private I_PackCons0(DataConstructor dataConstructor) {
            super (T_PackCons0, 0, dataConstructor.getOrdinal(), dataConstructor, dataConstructor.getName());
            if (dataConstructor.getArity() != 0) {
                throw new IllegalArgumentException ("the arity of the dataConstructor must be 0.");
            }               
       }

        /**
         * Provide description of instruction.
         */
        @Override
        public String toString() {
            return "I_PackCons0 <" + getInfo() + ", " + getOrd() + ">";
        }

        /**
         * Provide description of instruction with indent threading.
         */
        @Override
        public String toString(int indent) {
            StringBuilder is = new StringBuilder();
            for (int i = 0; i < indent; ++i) {
                is.append(" ");
            }
            return is + toString();
        }

    }
    
    /**
     * I_PackCons2.
     */
    static class I_PackCons2 extends Instruction {        

        private I_PackCons2(DataConstructor dataConstructor) {
            super (T_PackCons2, 2, dataConstructor.getOrdinal(), dataConstructor, dataConstructor.getName());            
            if (dataConstructor.getArity() != 2) {
                throw new IllegalArgumentException ("the arity of the dataConstructor must be 2.");
            }               
        }           

        /**
         * Provide description of instruction.
         */
        @Override
        public String toString() {
            return "I_PackCons2 <" + getInfo() + ", " + getOrd() + ", " + 2 + ">";
        }

        /**
         * Provide description of instruction with indent threading.
         */
        @Override
        public String toString(int indent) {
            StringBuilder is = new StringBuilder();
            for (int i = 0; i < indent; ++i) {
                is.append(" ");
            }
            return is + toString();
        }

    }

    /**
     * SWITCH cs.
     * Jump to the evaluation of some code from several alternatives.
     */
    static class I_Switch extends Instruction {
    
        // Information about the position in the source of the case statement to 
        // be used to augment error messages. This may be null.
        
        private final ErrorInfo errorInfo;
        
        /**
         * Construct.
         */
        I_Switch(Map<Object, Code> codesMap, ErrorInfo errorInfo) {
            super (T_Switch, codesMap);
            this.errorInfo = errorInfo;
        }

        /**
         * @return The error info to provide the user with for error situations.
         */
        
        ErrorInfo getErrorInfo(){
            return errorInfo;
        }
        
        /**
         * Provide description of instruction.
         */
        @Override
        public String toString() {
            return toString(0);
        }

        /**
         * Provide description of instruction with indent threading.
         */
        @Override
        public String toString(int indent) {
            StringBuilder is = new StringBuilder();
            for (int i = 0; i < indent; ++i) {
                is.append(" ");
            }

            StringBuilder sb = new StringBuilder();
            sb.append(is + "I_Switch <\n");
            java.util.Iterator<Object> keysIterator = getMap().keySet().iterator();
            while (keysIterator.hasNext()) {
                Object altTag = keysIterator.next();
                sb.append (is + "  " + altTag.toString() + " ->\n");
                sb.append(((Code) getMap().get(altTag)).toString(indent + 4));
                sb.append ("\n");
            }

            sb.append(is + ">");
            return sb.toString();

        }

    }

    /**
     * SWITCH cs.
     * Jump to the evaluation of some code from several alternatives.
     */
    static class I_SwitchJ extends Instruction {

        // Information about the position in the source of the case statement to 
        // be used to augment error messages. This may be null.
        
        private final ErrorInfo errorInfo;
        
        /**
         * Construct.
         */
        
        I_SwitchJ(Map<Object, Integer> jumpMap, ErrorInfo errorInfo) {
            super (T_SwitchJ, jumpMap);
            this.errorInfo = errorInfo;
        }

        /**
         * @return The error info to provide the user with for error situations.
         */
        
        ErrorInfo getErrorInfo(){
            return errorInfo;
        }
        
        /**
         * Provide description of instruction.
         */
        @Override
        public String toString() {
            return toString(0);
        }

        /**
         * Provide description of instruction with indent threading.
         */
        @Override
        public String toString(int indent) {
            StringBuilder is = new StringBuilder();
            for (int i = 0; i < indent; ++i) {
                is.append(" ");
            }

            StringBuilder sb = new StringBuilder();
            sb.append(is + "I_Switch <\n");
            java.util.Iterator<?> keysIterator = getMap().keySet().iterator();
            while (keysIterator.hasNext()) {
                Object altTag = keysIterator.next();
                Integer jump = (Integer)getMap().get (altTag);
                sb.append (is + "  " + altTag.toString() + " -> " + jump + "\n");
            }

            sb.append(is + ">");
            return sb.toString();

        }
    }
    
    /**
     * SelectDCField.
     * Pop the DC node off the stack, and push the value of the dc field.
     */
    static class I_SelectDCField extends Instruction {

        /** 
         * Information about the position in the source of the case statement to be used to augment error messages. 
         * This may be null.
         */
        private final ErrorInfo errorInfo;
        
        /**
         * Constructor for a I_SelectDCField.
         * @param dataConstructor the expected data constructor.
         * @param fieldIndex the index of the desired field.
         * @param errorInfo error info for the source position of the selection.
         */
        I_SelectDCField(DataConstructor dataConstructor, int fieldIndex, ErrorInfo errorInfo) {
            super (T_SelectDCField, fieldIndex, dataConstructor);
            this.errorInfo = errorInfo;
        }

        /**
         * @return The error info to provide the user with for error situations.
         */
        ErrorInfo getErrorInfo(){
            return errorInfo;
        }
        
        /**
         * Provide description of instruction.
         */
        @Override
        public String toString() {
            return toString(0);
        }

        /**
         * Provide description of instruction with indent threading.
         */
        @Override
        public String toString(int indent) {
            StringBuilder is = new StringBuilder();
            for (int i = 0; i < indent; ++i) {
                is.append(" ");
            }

            return is + "I_SelectDCField  <" + ((DataConstructor)getInfo()).getName() + ", " + getN() + ">";
        }
    }

    /**
     * SelectDCField.
     * Pop the DC node off the stack, and push the value of the dc field.
     */
    static class I_LazySelectDCField extends Instruction {

        /** 
         * Information about the position in the source of the case statement to be used to augment error messages. 
         * This may be null.
         */
        private final ErrorInfo errorInfo;
        
        /**
         * Constructor for a I_SelectDCField.
         * @param dataConstructor the expected data constructor.
         * @param fieldIndex the index of the desired field.
         * @param errorInfo error info for the source position of the selection.
         */
        I_LazySelectDCField(DataConstructor dataConstructor, int fieldIndex, ErrorInfo errorInfo) {
            super (T_LazySelectDCField, fieldIndex, dataConstructor);
            this.errorInfo = errorInfo;
        }

        /**
         * @return The error info to provide the user with for error situations.
         */
        ErrorInfo getErrorInfo(){
            return errorInfo;
        }
        
        /**
         * Provide description of instruction.
         */
        @Override
        public String toString() {
            return toString(0);
        }

        /**
         * Provide description of instruction with indent threading.
         */
        @Override
        public String toString(int indent) {
            StringBuilder is = new StringBuilder();
            for (int i = 0; i < indent; ++i) {
                is.append(" ");
            }

            return is + "I_LazySelectDCField  <" + ((DataConstructor)getInfo()).getName() + ", " + getN() + ">";
        }
    }
    
    /**
     * Split
     */
    static class I_Split extends Instruction {

        /**
         * Construct.
         */
        I_Split (int n) {
            super (T_Split, n);
        }

        /**
         * Provide description of instruction.
         */
        @Override
        public String toString() {
            return toString(0);
        }

        /**
         * Provide description of instruction with indent threading.
         */
        @Override
        public String toString(int indent) {
            StringBuilder is = new StringBuilder();
            for (int i = 0; i < indent; ++i) {
                is.append(" ");
            }

            return is + "I_Split <" + getN() + ">";
        }
    }

    /**
     * Instruction for calling foreign functions. At this point, it is rather similar to I_PrimOp
     * but will evolve as non-simple types are allowed for arguments and return types.
     */
    static class I_ForeignFunctionCall extends Instruction {
        
        /**
         * @param foreignSCInfo ForeignFunctionInfo 
         */
        I_ForeignFunctionCall(ForeignFunctionInfo foreignSCInfo) {
            super (T_ForeignFunctionCall, foreignSCInfo);
        }

        /**
         * Provide description of instruction.
         */
        @Override
        public String toString() {
            return "I_ForeignFunctionCall <" + getInfo() + ">";
        }

        /**
         * Provide description of instruction with indent threading
         */
        @Override
        public String toString(int indent) {
            StringBuilder is = new StringBuilder();
            for (int i = 0; i < indent; ++i) {
                is.append(" ");
            }
            return is + toString();
        }
    }

    /**
     * ClearStack.
     * Empty the machines stack.  This is used in the event of a programmatic error.
     */
    private static class I_IClearStack extends Instruction {
  
        /**
         * Construct from listener.
         */
        I_IClearStack () {
            super (T_ClearStack);
        }

        /**
         * Provide description of instruction.
         */
        @Override
        public String toString() {
            return "I_ClearStack";
        }

        /**
         * Provide description of instruction with indent threading.
         */
        @Override
        public String toString(int indent) {
            StringBuilder is = new StringBuilder();
            for (int i = 0; i < indent; ++i) {
                is.append(" ");
            }
            return is + toString();
        }
    }


    /**
     * Dispatch n.
     * Performs case analysis on the function on the top of the stack and
     * enters it in an appropriate manner.
     */
    static class I_Dispatch extends Instruction {
        
        /**
         * Construct from an integer.
         */
        I_Dispatch (int n) {
            super (T_Dispatch, n);
        }

        /**
         * Provide description of instruction.
         */
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder ();
            sb.append ("I_Dispatch<" + getN() + ">");        
            return sb.toString ();
        }

        /**
         * Provide description of instruction with indent threading.
         */
        @Override
        public String toString(int indent) {
            StringBuilder is = new StringBuilder();
            for (int i = 0; i < indent; ++i) {
                is.append(" ");
            }
            return is + toString();
        }
    }

    /**
     * Call n.
     * Allocates a root then performs case analysis on the function on the top of the stack and
     * enters it in an appropriate manner.
     */
    static class I_Call extends Instruction {
        
        /**
         * Construct from an integer.
         */
        I_Call (int n) {
            super (T_Call, n);
        }

        /**
         * Provide description of instruction.
         */
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder ();
            sb.append ("I_Call<" + getN() + ">");        
            return sb.toString ();
        }

        /**
         * Provide description of instruction with indent threading.
         */
        @Override
        public String toString(int indent) {
            StringBuilder is = new StringBuilder();
            for (int i = 0; i < indent; ++i) {
                is.append(" ");
            }
            return is + toString();
        }
    }

    /**
     * Squeeze x y.
     * Slide down the top x elements on the stack squeezing out the y elements below.
     */
    static class I_Squeeze extends Instruction {
        
        /**
         * Construct from an integer.
         */
        I_Squeeze (int x, int y) {
            super (T_Squeeze, x, y);
        }

        /**
         * Provide description of instruction.
         */
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder ();
            sb.append ("I_Squeeze<" + getX() + ", " + getY() + ">");        
            return sb.toString ();
        }

        /**
         * Provide description of instruction with indent threading.
         */
        @Override
        public String toString(int indent) {
            StringBuilder is = new StringBuilder();
            for (int i = 0; i < indent; ++i) {
                is.append(" ");
            }
            return is + toString();
        }
    }

    /**
     * I_CreateRecord: instruction to create a new record object.
     * @author RCypher
     * Created: Apr 12, 2004
     */
    static class I_CreateRecord extends Instruction {
        /**
         * Construct from an field map.
         */
        I_CreateRecord (int nFields) {
            super (T_CreateRecord, nFields);    
        }

        /**
         * Provide description of instruction with indent threading.
         */
        @Override
        public String toString(int indent) {
            StringBuilder is = new StringBuilder();
            for (int i = 0; i < indent; ++i) {
                is.append(" ");
            }
            StringBuilder out = new StringBuilder();
            out.append (is);
            out.append ("I_CreateRecord<" + getN() + ">\n");
            return out.toString();
        }
        
        @Override
        public String toString () {
            return toString (0);
        }
    }
    
    /**
     * Place a field value in the record on top of the stack.
     * I_PutRecordField
     * @author RCypher
     * Created: Apr 13, 2004
     */
    static class I_PutRecordField extends Instruction {
        
        I_PutRecordField (String fieldName) {
            super (T_PutRecordField, fieldName);
        }
        /**
         * Provide description of instruction with indent threading.
         */
        @Override
        public String toString(int indent) {
            StringBuilder is = new StringBuilder();
            for (int i = 0; i < indent; ++i) {
                is.append(" ");
            }
            StringBuilder out = new StringBuilder();
            out.append (is);
            out.append ("I_SetRecordField<" + getInfo().toString() + ">\n");
            return out.toString();
        }
        
        @Override
        public String toString () {
            return toString (0);
        }
    }
    
    /**
     * Remove a field value from the record on top of the stack.
     * I_RemoveRecordField
     * @author RCypher
     * Created: Apr 13, 2004
     */
    static class I_RemoveRecordField extends Instruction {
        I_RemoveRecordField (String fieldName) {
            super (T_RemoveRecordField, fieldName);
        }
        /**
         * Provide description of instruction with indent threading.
         */
        @Override
        public String toString(int indent) {
            StringBuilder is = new StringBuilder();
            for (int i = 0; i < indent; ++i) {
                is.append(" ");
            }
            StringBuilder out = new StringBuilder();
            out.append (is);
            out.append ("I_RemoveRecordField<" + getInfo().toString() + ">\n");
            return out.toString();
        }
        
        @Override
        public String toString () {
            return toString (0);
        }
    }
    
    /**
     * Extend the record value on top of the stack.
     * I_ExtendRecord
     * @author RCypher
     * Created: Apr 13, 2004
     */
    static class I_ExtendRecord extends Instruction {
        I_ExtendRecord () {
            super (T_ExtendRecord);
        }
        
        @Override
        public String toString (int indent) {
            StringBuilder is = new StringBuilder();
            for (int i = 0; i < indent; ++i) {
                is.append(" ");
            }
            return is + "I_ExtendRecord<>";
        }
        
        @Override
        public String toString () {
            return toString (0);
        }
    }
    
    /**
     * Select the named field value from the record
     * on top of the stack.
     * I_RecordSelection
     * @author RCypher
     * Created: Apr 13, 2004
     */
    static class I_RecordSelection extends Instruction {
        I_RecordSelection (String fieldName) {
            super (T_RecordSelection, fieldName);
        }
        
        @Override
        public String toString (int indent) {
            StringBuilder is = new StringBuilder();
            for (int i = 0; i < indent; ++i) {
                is.append(" ");
            }
            return is + "I_RecordSelection<" + getInfo().toString() + ">";
        }
        
        @Override
        public String toString () {
            return toString (0);
        }
    }

    /**
     * Creates a node representing the application of a virtual
     * record selection function to the record value on the 
     * top of the stack.
     * I_LazyRecordSelection
     * @author RCypher
     * Created: Apr 13, 2004
     */
    static class I_LazyRecordSelection extends Instruction {
        I_LazyRecordSelection (String fieldName) {
            super (T_LazyRecordSelection, fieldName);
        }
        
        @Override
        public String toString (int indent) {
            StringBuilder is = new StringBuilder();
            for (int i = 0; i < indent; ++i) {
                is.append(" ");
            }
            return is + "I_LazyRecordSelection<" + getInfo().toString() + ">";
        }
        
        @Override
        public String toString () {
            return toString (0);
        }
    }
    
    /**
     * Creates a node representing an application of a virtual record
     * extension function to the record value on the top of the stack. 
     * I_LazyRecordExtension
     * @author Bo Ilic
     * Created: May 4, 2006
     */
    static class I_LazyRecordUpdate extends Instruction {    
        I_LazyRecordUpdate () {
            super (T_LazyRecordUpdate);
        }
        
        @Override
        public String toString (int indent) {
            StringBuilder is = new StringBuilder();
            for (int i = 0; i < indent; ++i) {
                is.append(" ");
            }
            return is + "I_LazyRecordUpdate<>";
        }
        
        @Override
        public String toString () {
            return toString (0);
        }
    }    

    /**
     * Creates a node representing an application of a virtual record
     * extension function to the record value on the top of the stack. 
     * I_LazyRecordExtension
     * @author RCypher
     * Created: Apr 13, 2004
     */
    static class I_LazyRecordExtension extends Instruction {    
        I_LazyRecordExtension () {
            super (T_LazyRecordExtension);
        }
        
        @Override
        public String toString (int indent) {
            StringBuilder is = new StringBuilder();
            for (int i = 0; i < indent; ++i) {
                is.append(" ");
            }
            return is + "I_LazyRecordExtension<>";
        }
        
        @Override
        public String toString () {
            return toString (0);
        }
    }

    /**
     * Create a java list object and push it onto the stack in an NValObject node.
     * @author RCypher
     */
    static class I_CreateList extends Instruction {
        I_CreateList () {
            super (T_CreateList);
        }
        @Override
        public String toString (int indent) {
            StringBuilder is = new StringBuilder();
            for (int i = 0; i < indent; ++i) {
                is.append(" ");
            }
            is.append("I_CreateList<>");
            return is.toString();
        }
        @Override
        public String toString () {
            return toString (0);
        }
    }

    /**
     * Push the opaque value in an NValObject node on the top of the stack into
     * the list immediately above it.
     * @author RCypher
     */
    static class I_PutListValue extends Instruction {
        I_PutListValue () {
            super (T_PutListValue);
        }
        @Override
        public String toString (int indent) {
            StringBuilder is = new StringBuilder();
            for (int i = 0; i < indent; ++i) {
                is.append(" ");
            }
            is.append("I_PutListValue<>");
            return is.toString();
        }
        @Override
        public String toString () {
            return toString (0);
        }
    }

    /**
     * Ensure the opaque value in an NValObject can be cast to the given type.
     * @author RCypher
     */
    static class I_Cast extends Instruction {
        I_Cast (Class<?> castType) {
            super (T_Cast, castType);
        }
        @Override
        public String toString(int indent) {
            StringBuilder is = new StringBuilder();
            for (int i = 0; i < indent; ++i) {
                is.append(" ");
            }
            is.append("I_Cast<" + ((Class<?>)getInfo()).getName() + ">");
            return is.toString();
        }
        @Override
        public String toString() {return toString(0);}
    }

    /**
     * Generate a trace message for the currently executing function.
     * @author rcypher
     */
    static final class I_Debug_Processing extends Instruction {
        // We want to hold the name of the function and the
        // arity.  This lets us generate a trace message
        // with the name of the function and the state of the 
        // argument values.
        I_Debug_Processing (QualifiedName name, int arity) {
            super (T_DebugProcessing, arity, name);
        }
        @Override
        public String toString(int indent) {
            StringBuilder is = new StringBuilder();
            for (int i = 0; i < indent; ++i) {
                is.append(" ");
            }
            is.append("I_Debug_Processing <" + getName() + ">");
            return is.toString();
        }
        @Override
        public String toString() {return toString(0);}
    }


}

