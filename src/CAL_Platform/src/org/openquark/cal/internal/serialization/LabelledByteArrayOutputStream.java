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
 * LabelledByteArrayOutputStream.java
 * Created: Feb 15, 2005
 * By: Raymond Cypher
 */
package org.openquark.cal.internal.serialization;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Output stream that writes to a byte array.
 * Labelling allows for writing content at an earlier point in the stream.
 */
class LabelledByteArrayOutputStream extends ByteArrayOutputStream {
    
    LabelledByteArrayOutputStream() {
        super(1024);
    }
    
    /**
     * Create a Label at the current write point.  
     * A Label is itself an OutputStream, backed by this LabelledByteArrayOutputStream
     * starting at the current write point.
     * @return a Label instance.
     */
    public Label getLabel() {
        return new Label(count);
    }
    
    void shiftBackBy(int startPos, int shiftBackBy, int length) {
        System.arraycopy(buf, startPos, buf, startPos - shiftBackBy, length);
        count -= 2;
    }
    
    /**
     * Write a byte at a specific position.
     * @param pos
     * @param b
     * @throws IOException
     */
    private void writeAt(int pos, int b) throws IOException {
        if (pos > count || pos < 0) {
            throw new IllegalArgumentException("Invalid write position: " + pos);
        }
        int tempCount = count;
        count = pos;
        write(b);
        if (tempCount > count) {
            count = tempCount;
        }
    }
    
    /**
     * Writes the specified byte to this byte array output stream. 
     *
     * @param   b   the byte to be written.
     */
    @Override
    public synchronized void write(int b) {
        int newcount = count + 1;
        if (newcount > buf.length) {
            byte newbuf[] = new byte[Math.max(buf.length << 1, newcount)];
            System.arraycopy(buf, 0, newbuf, 0, count);
            buf = newbuf;
        }
        buf[count] = (byte) b;
        count = newcount;
    }
    
    /**
     * Write the set of bytes to the specific position.
     * @param pos
     * @param b
     * @param off
     * @param len
     * @throws IOException
     */
    private void writeAt(int pos, byte[] b, int off, int len) throws IOException {
        if (pos > count || pos < 0) {
            throw new IllegalArgumentException("Invalid write position: " + pos);
        }
        int tempCount = count;
        count = pos;
        write(b, off, len);
        if (tempCount > count) {
            count = tempCount;
        }
    }
    
    /**
     * This is an output stream backed by the associated LabelledByteArrayOutputStream instance.
     * Writing starts at the specified position in the backing stream.  
     * @author RCypher
     */
    class Label extends OutputStream {
        int startPosition;
        
        int position;
        
        private Label(int startPosition) {
            this.startPosition = startPosition;
            this.position = startPosition;
        }
        
        @Override
        public void write(int b) throws IOException {
            LabelledByteArrayOutputStream.this.writeAt(position, b);
            position++;
        }
        
        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            LabelledByteArrayOutputStream.this.writeAt(position, b, off, len);
            position += len;
        }
        
        public void reset() {
            position = startPosition;
        }
        
        public int getStartPosition() {
            return startPosition;
        }
    }
}
