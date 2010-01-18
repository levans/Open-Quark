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
 * RecordOutputStream.java
 * Created: Feb 25, 2005
 * By: Raymond Cypher
 */
package org.openquark.cal.internal.serialization;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UTFDataFormatException;
import java.util.LinkedHashMap;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.util.ArrayStack;


/**
 * Stream class for writing records.
 */
public class RecordOutputStream implements DataOutput {

    private static final int recordPoolSchema = 0;
    
    /** The byte which is XORed against each byte in a UTF-8 encoded string. */
    /* @implementation Keep this below 0x7f (to avoid sign extension issues)! Set this to 0 to leave the strings unobfuscated. */
    static final byte OBFUSCATING_BYTE = (byte)0x5c;
    
    // Backing output stream. This isn't actually written to until the
    // RecordOutputStream is closed.
    private final OutputStream output;
    
    // Stack to keep track of the current record.
    private final ArrayStack<LabelledByteArrayOutputStream.Label> recordStack = ArrayStack.make ();
    
    // As strings are saved we build up a pool of unique strings and
    // actually save the key into this pool.  The pool is written
    // at the beginning of the output when close is called.
    private final LinkedHashMap<String, Integer> stringPool = new LinkedHashMap<String, Integer>();
    
    // As QualifiedNames are saved we build up a pool of unique instances and
    // actually save the key into this pool.  The pool is written
    // at the beginning of the output when close is called.
    private final LinkedHashMap<QualifiedName, Integer> qnPool = new LinkedHashMap<QualifiedName, Integer>();
    
    // Pool the schemas for the different record types, keyed by record tag.
    //private LinkedHashMap schemaPool = new LinkedHashMap();
    
    // The fully buffered repositionable backing stream used to write to.
    private final LabelledByteArrayOutputStream out = new LabelledByteArrayOutputStream ();
    private final byte writeBuffer[] = new byte[8];

    
    public RecordOutputStream (OutputStream output) {
        this.output = output;
        
    }
    
    /**
     * Start a record with the given tag and schema.
     * @param tag
     * @param schema
     * @throws IOException
     */
    public void startRecord (short tag, int schema) throws IOException {
        writeShortCompressed(tag);
        writeShortCompressed (schema);
        recordStack.push (out.getLabel());
        
        // Reserve space for the length value.
        writeInt(0);
        
    }
    
    /**
     * End the current record.
     * @throws IOException
     */
    public void endRecord () throws IOException {
        LabelledByteArrayOutputStream.Label label = recordStack.pop ();
        int length = out.size() - label.getStartPosition() - 4;
        int nLengthBytes = RecordOutputStream.writeIntCompressed(length, new DataOutputStream (label));
        if (nLengthBytes == 2) {
            out.shiftBackBy(label.getStartPosition()+4, 2, length);
        }
    }
    
    /**
     * Writes the specified byte (the low eight bits of the argument 
     * <code>b</code>) to the underlying output stream. If no exception 
     * is thrown, the counter <code>written</code> is incremented by 
     * <code>1</code>.
     * <p>
     * Implements the <code>write</code> method of <code>OutputStream</code>.
     *
     * @param      b   the <code>byte</code> to be written.
     * @exception  IOException  if an I/O error occurs.
     * @see        RecordOutputStream#out
     */
    public synchronized void write(int b) throws IOException {
        out.write(b);
    }

    /**
     * Writes <code>len</code> bytes from the specified byte array 
     * starting at offset <code>off</code> to the underlying output stream. 
     * If no exception is thrown, the counter <code>written</code> is 
     * incremented by <code>len</code>.
     *
     * @param      b     the data.
     * @param      off   the start offset in the data.
     * @param      len   the number of bytes to write.
     * @exception  IOException  if an I/O error occurs.
     * @see        RecordOutputStream#out
     */
    public synchronized void write(byte b[], int off, int len) throws IOException {
        out.write(b, off, len);
    }

    public void write(byte b[]) throws IOException {
        write(b, 0, b.length);
    }
    
    /**
     * When we close this stream we want to write the buffered output
     * to the backing output stream.  This involves the pooled values
     * maintained by the RecordOutputStream being written out first.
     * @throws IOException
     */
    public void close () throws IOException {
        try {
            // Write out the pools.
            startRecord(ModuleSerializationTags.POOLED_VALUES, recordPoolSchema);

            // Write out the string pool.
            writeIntCompressed(stringPool.size());
            for (final String key : stringPool.keySet()) {
                writeUTFInternal(key);
            }

            // Write out the QualifiedName pool.
            writeIntCompressed(qnPool.size());
            for (final QualifiedName qn : qnPool.keySet()) {
                writeIntCompressed(getStringIndex(qn.getModuleName().toSourceText()));
                writeIntCompressed(getStringIndex(qn.getUnqualifiedName()));
            }

            endRecord();

            // Now write out the content of the RecordOutputStream.
            out.writeTo(output);
            output.flush();
            
        } finally {
            output.close();
        }
    }
    
    /**
     * Writes a <code>boolean</code> to the underlying output stream as 
     * a 1-byte value. The value <code>true</code> is written out as the 
     * value <code>(byte)1</code>; the value <code>false</code> is 
     * written out as the value <code>(byte)0</code>. If no exception is 
     * thrown, the counter <code>written</code> is incremented by 
     * <code>1</code>.
     *
     * @param      v   a <code>boolean</code> value to be written.
     * @exception  IOException  if an I/O error occurs.
     * @see        RecordOutputStream#out
     */
    public final void writeBoolean(boolean v) throws IOException {
    out.write(v ? 1 : 0);
    }
    
    /**
     * Writes out a <code>byte</code> to the underlying output stream as 
     * a 1-byte value. If no exception is thrown, the counter 
     * <code>written</code> is incremented by <code>1</code>.
     *
     * @param      v   a <code>byte</code> value to be written. 
     * @exception  IOException  if an I/O error occurs.
     * @see        RecordOutputStream#out
     */
    public final void writeByte(int v) throws IOException {
        out.write(v);
    }

    /**
     * Writes a <code>short</code> to the underlying output stream as two
     * bytes, high byte first. If no exception is thrown, the counter 
     * <code>written</code> is incremented by <code>2</code>.
     *
     * @param      v   a <code>short</code> to be written.
     * @exception  IOException  if an I/O error occurs.
     * @see        RecordOutputStream#out
     */
    public final void writeShort(int v) throws IOException {
        out.write((v >>> 8) & 0xFF);
        out.write((v >>> 0) & 0xFF);
    }
    

    /**
     * Writes a <code>char</code> to the underlying output stream as a 
     * 2-byte value, high byte first. If no exception is thrown, the 
     * counter <code>written</code> is incremented by <code>2</code>.
     *
     * @param      v   a <code>char</code> value to be written.
     * @exception  IOException  if an I/O error occurs.
     * @see        RecordOutputStream#out
     */
    public final void writeChar(int v) throws IOException {
        out.write((v >>> 8) & 0xFF);
        out.write((v >>> 0) & 0xFF);
    }

    /**
     * Writes an <code>int</code> to the underlying output stream as four
     * bytes, high byte first. If no exception is thrown, the counter 
     * <code>written</code> is incremented by <code>4</code>.
     *
     * @param      v   an <code>int</code> to be written.
     * @exception  IOException  if an I/O error occurs.
     * @see        RecordOutputStream#out
     */
    public final void writeInt(int v) throws IOException {
        out.write((v >>> 24) & 0xFF);
        out.write((v >>> 16) & 0xFF);
        out.write((v >>>  8) & 0xFF);
        out.write((v >>>  0) & 0xFF);

//        if(((v >>> 24) & 0xFF) == 0 && ((v >>> 16) & 0xFF) == 0) {
//            nWastedBytes2 += 2;
//        }
        
    }

    public final int writeIntCompressed(int v) throws IOException {
        return RecordOutputStream.writeIntCompressed(v, this);
    }
    
    public final void writeShortCompressed(int shortVal) throws IOException {
        RecordOutputStream.writeShortCompressed(shortVal, this);
    }
    
    private static final void writeShortCompressed(int shortVal, DataOutput d) throws IOException {
        if (shortVal < 0) {
            throw new IOException ("Unable to compress 32 bit integer with value < 0.");
        }
        if (shortVal > Short.MAX_VALUE) {
            throw new IOException ("Unable to fit int value of " + shortVal + " into two bytes.");
        } 
        
        if (shortVal > Byte.MAX_VALUE) {
            shortVal |= 0x8000;
            d.writeShort(shortVal);
        } else {
            d.writeByte(shortVal);
        }
        
    }
    
    private static int writeIntCompressed(int v, DataOutput d) throws IOException {
        if (v < 0) {
            throw new IOException ("Unable to compress 32 bit integer with value < 0.");
        }
        if (v > Short.MAX_VALUE) {
            v |= 0x80000000;
            d.writeInt(v);
            return 4;
        } else {
            d.writeShort(v);
            return 2;
        }
    }
    
    /**
     * Writes a <code>long</code> to the underlying output stream as eight
     * bytes, high byte first. In no exception is thrown, the counter 
     * <code>written</code> is incremented by <code>8</code>.
     *
     * @param      v   a <code>long</code> to be written.
     * @exception  IOException  if an I/O error occurs.
     * @see        RecordOutputStream#out
     */
    public final void writeLong(long v) throws IOException {
        writeBuffer[0] = (byte)(v >>> 56);
        writeBuffer[1] = (byte)(v >>> 48);
        writeBuffer[2] = (byte)(v >>> 40);
        writeBuffer[3] = (byte)(v >>> 32);
        writeBuffer[4] = (byte)(v >>> 24);
        writeBuffer[5] = (byte)(v >>> 16);
        writeBuffer[6] = (byte)(v >>>  8);
        writeBuffer[7] = (byte)(v >>>  0);
        out.write(writeBuffer, 0, 8);
    }

    /**
     * Converts the float argument to an <code>int</code> using the 
     * <code>floatToIntBits</code> method in class <code>Float</code>, 
     * and then writes that <code>int</code> value to the underlying 
     * output stream as a 4-byte quantity, high byte first. If no 
     * exception is thrown, the counter <code>written</code> is 
     * incremented by <code>4</code>.
     *
     * @param      v   a <code>float</code> value to be written.
     * @exception  IOException  if an I/O error occurs.
     * @see        RecordOutputStream#out
     * @see        java.lang.Float#floatToIntBits(float)
     */
    public final void writeFloat(float v) throws IOException {
        writeInt(Float.floatToIntBits(v));
    }

    /**
     * Converts the double argument to a <code>long</code> using the 
     * <code>doubleToLongBits</code> method in class <code>Double</code>, 
     * and then writes that <code>long</code> value to the underlying 
     * output stream as an 8-byte quantity, high byte first. If no 
     * exception is thrown, the counter <code>written</code> is 
     * incremented by <code>8</code>.
     *
     * @param      v   a <code>double</code> value to be written.
     * @exception  IOException  if an I/O error occurs.
     * @see        RecordOutputStream#out
     * @see        java.lang.Double#doubleToLongBits(double)
     */
    public final void writeDouble(double v) throws IOException {
        writeLong(Double.doubleToLongBits(v));
    }

    /**
     * Writes out the string to the underlying output stream as a 
     * sequence of bytes. Each character in the string is written out, in 
     * sequence, by discarding its high eight bits. If no exception is 
     * thrown, the counter <code>written</code> is incremented by the 
     * length of <code>s</code>.
     *
     * @param      s   a string of bytes to be written.
     * @exception  IOException  if an I/O error occurs.
     * @see        RecordOutputStream#out
     */
    public final void writeBytes(String s) throws IOException {
        int len = s.length();
        for (int i = 0 ; i < len ; i++) {
            out.write((byte)s.charAt(i));
        }
    }

    /**
     * Writes a string to the underlying output stream as a sequence of 
     * characters. Each character is written to the data output stream as 
     * if by the <code>writeChar</code> method. If no exception is 
     * thrown, the counter <code>written</code> is incremented by twice 
     * the length of <code>s</code>.
     *
     * @param      s   a <code>String</code> value to be written.
     * @exception  IOException  if an I/O error occurs.
     * @see        java.io.DataOutputStream#writeChar(int)
     * @see        RecordOutputStream#out
     */
    public final void writeChars(String s) throws IOException {
        int len = s.length();
        for (int i = 0 ; i < len ; i++) {
            int v = s.charAt(i);
            out.write((v >>> 8) & 0xFF); 
            out.write((v >>> 0) & 0xFF); 
        }
    }

    /**
     * Writes a string to the underlying output stream using Java modified
     * UTF-8 encoding in a machine-independent manner. 
     * <p>
     * First, two bytes are written to the output stream as if by the 
     * <code>writeShort</code> method giving the number of bytes to 
     * follow. This value is the number of bytes actually written out, 
     * not the length of the string. Following the length, each character 
     * of the string is output, in sequence, using the modified UTF-8 encoding
     * for the character. If no exception is thrown, the counter 
     * <code>written</code> is incremented by the total number of 
     * bytes written to the output stream. This will be at least two 
     * plus the length of <code>str</code>, and at most two plus 
     * thrice the length of <code>str</code>.
     *
     * @param      str   a string to be written.
     * @exception  IOException  if an I/O error occurs.
     */
    public final void writeUTF(String str) throws IOException {
        writeIntCompressed(getStringIndex(str));
    }
    
    private int getStringIndex(String str) {
        if (str == null) {
            return 0;
        }
        Integer index = stringPool.get(str);
        if (index == null) {
            index = Integer.valueOf(stringPool.size() + 1);
            stringPool.put(str, index);
        }
        return index.intValue();
    }
    
    /**
     * Serialize a string ignoring the string pooling mechanism.
     * Strings serialized using this function must be read
     * via RecordInputStream.readUTFUnPooled().
     * @param string
     * @throws IOException
     */
    public void writeUTFUnPooled (String string) throws IOException {
        writeUTFInternal(string);
    }
    
    /**
     * Writes a string to the underlying output stream using Java modified
     * UTF-8 encoding in a machine-independent manner. 
     * <p>
     * First, two bytes are written to the output stream as if by the 
     * <code>writeShort</code> method giving the number of bytes to 
     * follow. This value is the number of bytes actually written out, 
     * not the length of the string. Following the length, each character 
     * of the string is output, in sequence, using the modified UTF-8 encoding
     * for the character. If no exception is thrown, the counter 
     * <code>written</code> is incremented by the total number of 
     * bytes written to the output stream. This will be at least two 
     * plus the length of <code>str</code>, and at most two plus 
     * thrice the length of <code>str</code>.
     *
     * @param      str   a string to be written.
     * @exception  IOException  if an I/O error occurs.
     */
    private final void writeUTFInternal(String str) throws IOException {
        writeUTF(str, this);
    }

    /**
     * Writes a string to the specified DataOutput using Java modified UTF-8
     * encoding in a machine-independent manner. 
     * <p>
     * First, two bytes are written to out as if by the <code>writeShort</code>
     * method giving the number of bytes to follow. This value is the number of
     * bytes actually written out, not the length of the string. Following the
     * length, each character of the string is output, in sequence, using the
     * modified UTF-8 encoding for the character. If no exception is thrown, the
     * counter <code>written</code> is incremented by the total number of 
     * bytes written to the output stream. This will be at least two 
     * plus the length of <code>str</code>, and at most two plus 
     * thrice the length of <code>str</code>.
     *
     * @param      str   a string to be written.
     * @param      out   destination to write to
     * @return The number of bytes written out.
     * @exception  IOException  if an I/O error occurs.
     */
    private static int writeUTF(String str, DataOutput out) throws IOException {
        int strlen = str.length();
        int utflen = 0;
        char[] charr = new char[strlen];
        int c, count = 0;
    
        str.getChars(0, strlen, charr, 0);
     
        for (int i = 0; i < strlen; i++) {
            c = charr[i];
            if ((c >= 0x0001) && (c <= 0x007F)) {
            utflen++;
            } else if (c > 0x07FF) {
            utflen += 3;
            } else {
            utflen += 2;
            }
        }
    
        if (utflen > 65535) {
            throw new UTFDataFormatException();
        }
    
        byte[] bytearr = new byte[utflen+2];
        // The first 2 bytes contain the length of the string and does not need to be obfuscated
        bytearr[count++] = (byte) ((utflen >>> 8) & 0xFF);
        bytearr[count++] = (byte) ((utflen >>> 0) & 0xFF);
        for (int i = 0; i < strlen; i++) {
            c = charr[i];
            if ((c >= 0x0001) && (c <= 0x007F)) {
                bytearr[count++] = (byte) (c ^ OBFUSCATING_BYTE);
            } else if (c > 0x07FF) {
                bytearr[count++] = (byte) ((0xE0 | ((c >> 12) & 0x0F)) ^ OBFUSCATING_BYTE);
                bytearr[count++] = (byte) ((0x80 | ((c >>  6) & 0x3F)) ^ OBFUSCATING_BYTE);
                bytearr[count++] = (byte) ((0x80 | ((c >>  0) & 0x3F)) ^ OBFUSCATING_BYTE);
            } else {
                bytearr[count++] = (byte) ((0xC0 | ((c >>  6) & 0x1F)) ^ OBFUSCATING_BYTE);
                bytearr[count++] = (byte) ((0x80 | ((c >>  0) & 0x3F)) ^ OBFUSCATING_BYTE);
            }
        }
        out.write(bytearr);
        return utflen + 2;
    }
    
    /**
     * Writes a module name to the RecordOutputStream.
     * @param moduleName a module name, may not be null.
     * @throws IOException 
     */
    public void writeModuleName(ModuleName moduleName) throws IOException {
        writeUTF(moduleName.toSourceText());
    }

    /**
     * Write a qualified name to the RecordOutputStream.
     * @param qualifiedName may be null.
     * @throws IOException
     */
    public void writeQualifiedName(QualifiedName qualifiedName) throws IOException {
        if (qualifiedName == null) {
            writeIntCompressed (0);
        } else {
            Integer qnIndex = qnPool.get(qualifiedName);
            if (qnIndex == null) {
                qnIndex = Integer.valueOf(qnPool.size() + 1);
                qnPool.put(qualifiedName, qnIndex);
            }
            
            // Force the strings that make up the qualified name to 
            // be added to the string pool.
            getStringIndex(qualifiedName.getModuleName().toSourceText());
            getStringIndex(qualifiedName.getUnqualifiedName());
            
            writeIntCompressed(qnIndex.intValue());
        }
    }

    public static byte[] booleanArrayToBitArray(boolean[] b) {
        int nBytes = (b.length + 7) / 8;
        byte[] bytes = new byte[nBytes];
        for (int i = 0; i < b.length; i++) {
            if (b[i]) {
                int iByte = i / 8;
                int iBit = i % 8;
                bytes[iByte] |= (0x01 << iBit); 
            }
        }
        return bytes;
    }
}
