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
 * RecordInputStream.java
 * Created: Feb 25, 2005
 * By: Raymond Cypher
 */

package org.openquark.cal.internal.serialization;

import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UTFDataFormatException;
import java.security.InvalidParameterException;
import java.util.IdentityHashMap;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.util.ArrayStack;


/**
 * RecordInputStream
 * A class for reading records from the input stream.  
 * The records start with a header containing the record tag, schema and content length.
 * @author RCypher
 */
public class RecordInputStream {

    // Stack to keep track of the current record.
    private ArrayStack<RecordHeaderInfo> recordStack = ArrayStack.make();
    
    // The backing input stream.
    private final FullyBufferedInputStream in;

    private String[] stringPool;
    private QualifiedName[] qualifiedNamePool;
    private boolean poolsInitialized = false;
    
    /**
     * An IdentityHashMap mapping module name strings to ModuleName instances. We use an
     * identity-based map because we know that the module names are pooled in the string pool
     * and therefore two module names that are equal (in the sense of equals()) are in fact the same object.
     */
    private final IdentityHashMap<String, ModuleName> moduleNameCache = new IdentityHashMap<String, ModuleName>();
    
    public RecordInputStream (InputStream s) {
        this.in = new FullyBufferedInputStream(s);
    }

    private final void setupPools () throws IOException {
        if (poolsInitialized) {
            return;
        }
        poolsInitialized = true;
        
        // Get the current position.
        Bookmark returnTo = bookmark();
        
        // Reposition to the beginning of the stream.
        // Since the pooled values record is a top level record we
        // want to temporarily reset the record stack.
        reposition(new Bookmark(0));
        ArrayStack<RecordHeaderInfo> oldRecordStack = recordStack;
        recordStack = ArrayStack.make();
        
        RecordHeaderInfo poolRecordHeader = findRecord(ModuleSerializationTags.POOLED_VALUES);
        if (poolRecordHeader == null) {
            throw new IOException ("Unable to find pooled values record in RecordInputStream.");
        }
        
        int nStrings = readIntCompressed();
        stringPool = new String[nStrings];
        for (int i = 0; i < nStrings; ++i) {
            stringPool[i] = readUTFInternal();
        }
        
        int nQualifiedNames = readIntCompressed();
        qualifiedNamePool = new QualifiedName[nQualifiedNames];
        for (int i = 0; i < nQualifiedNames; ++i) {
            String moduleName = readUTF();
            String name = readUTF();
            qualifiedNamePool[i] = QualifiedName.make(ModuleName.make(moduleName), name);
        }
        
        // Be sure to return to the original position
        reposition(returnTo);
        recordStack = oldRecordStack;
    }
    
    /**
     * Search for a record with the given tag until the end
     * of the containing record or the end of the file
     * is reached.
     * If the requested record is not found the read position is
     * left unchanged.
     * If the requested record is found the read position will be
     * at the beginning of the record content.
     * @param requestedTag
     * @return RecordHeaderInfo if the record is found, otherwise null.
     * @throws IOException
     */
    public RecordHeaderInfo findRecord(short requestedTag) throws IOException {
        return findRecord(new short[]{requestedTag});
    }
    
    /**
     * Search for a record with one of the given tags until the end
     * of the containing record of the end of the file
     * is reached.
     * If the requested record is not found the read position is
     * left unchanged.
     * If the requested record is found the read position will be
     * at the beginning of the record content.
     * @param requestedTags
     * @return RecordHeaderInfo if the record is found, otherwise null.
     * @throws IOException
     */
    public RecordHeaderInfo findRecord(short[] requestedTags) throws IOException {
        int startPosition = in.position;
        RecordHeaderInfo rhi = null;
        if (recordStack.size() > 0) {
            rhi = recordStack.peek();
        }   
        
        int nRequestedTags = requestedTags.length;
        
        while (true) {
            // If we are past the end of the contaiing record return false;
            if (rhi != null && in.position >= rhi.getNextRecordStart()) {
                in.position = startPosition;
                return null;
            }
            
            // Read the next record header.
            try {
                short tag = readShortCompressed();
                short schema = readShortCompressed();
                int length = readIntCompressed();
                
                for (int i = 0; i < nRequestedTags; ++i) {
                    if (tag == requestedTags[i]) {
                        RecordHeaderInfo newHeader = new RecordHeaderInfo(tag, schema, length, in.position);
                        recordStack.push(newHeader);
                        in.fillBuffer(newHeader.length);
                        return newHeader;
                    }
                }
                
                // Skip to the end of the record.
                in.position += length;
            } catch (EOFException e) {
                // We've gone past the end of the file trying to find the record.
                in.position = startPosition;
                return null;
            }
        }
    }
    
    /**
     * Skip to the end of the current record.
     */
    public void skipRestOfRecord() {
        if (recordStack.size() == 0) {
            return;
        }

        // Pop the current record off the stack and reset our position.
        RecordHeaderInfo rhi = recordStack.pop();
        in.position = rhi.getNextRecordStart(); 
    }
    
    public boolean atEndOfRecord () {
        RecordHeaderInfo rhi = recordStack.peek();
        return in.position == rhi.getNextRecordStart();
    }
    
    final int read () throws IOException {
        if (recordStack.size() > 0) {
            RecordHeaderInfo rhi = recordStack.peek();
            if (in.position >= rhi.getNextRecordStart()) {
                throw new IOException("Attempt to read past end of record: tag = " + rhi.getRecordTag() + " schema = " + rhi.getSchema());
            }
        }
        return in.read();
    }
    
    /**
     * See the general contract of the <code>readFully</code> method of
     * <code>DataInput</code>.
     * <p>
     * Bytes for this operation are read from the contained input stream.
     * 
     * @param b
     *            the buffer into which the data is read.
     * @exception EOFException
     *                if this input stream reaches the end before reading all
     *                the bytes.
     * @exception IOException
     *                if an I/O error occurs.
     * @see RecordInputStream#in
     */
    final void readFully(byte b[]) throws IOException {
        readFully(b, 0, b.length);
    }

    /**
     * See the general contract of the <code>readFully</code> method of
     * <code>DataInput</code>.
     * <p>
     * Bytes for this operation are read from the contained input stream.
     * 
     * @param b
     *            the buffer into which the data is read.
     * @param off
     *            the start offset of the data.
     * @param len
     *            the number of bytes to read.
     * @exception EOFException
     *                if this input stream reaches the end before reading all
     *                the bytes.
     * @exception IOException
     *                if an I/O error occurs.
     * @see RecordInputStream#in
     */
    final void readFully(byte b[], int off, int len) throws IOException {
        if (len < 0) {
            throw new IndexOutOfBoundsException();
        }
        
        if (recordStack.size() > 0) {
            RecordHeaderInfo rhi = recordStack.peek();
            if (in.position + len > rhi.getNextRecordStart()) {
                throw new IOException("Attempt to read past end of record: tag = " + rhi.getRecordTag() + " schema = " + rhi.getSchema());
            }
        }
        int n = 0;
        while (n < len) {
            int count = in.read(b, off + n, len - n);
            if (count < 0) {
                throw new EOFException();
            }
            n += count;
        }
    }


    /**
     * See the general contract of the <code>readBoolean</code> method of
     * <code>DataInput</code>.
     * <p>
     * Bytes for this operation are read from the contained input stream.
     * 
     * @return the <code>boolean</code> value read.
     * @exception EOFException
     *                if this input stream has reached the end.
     * @exception IOException
     *                if an I/O error occurs.
     * @see RecordInputStream#in
     */
    public final boolean readBoolean() throws IOException {
        int ch = in.read();
        if (ch < 0) {
            throw new EOFException();
        }
        return (ch != 0);
    }

    /**
     * See the general contract of the <code>readByte</code> method of
     * <code>DataInput</code>.
     * <p>
     * Bytes for this operation are read from the contained input stream.
     * 
     * @return the next byte of this input stream as a signed 8-bit
     *         <code>byte</code>.
     * @exception EOFException
     *                if this input stream has reached the end.
     * @exception IOException
     *                if an I/O error occurs.
     * @see RecordInputStream#in
     */
    public final byte readByte() throws IOException {
        int ch = in.read();
        if (ch < 0) {
            throw new EOFException();
        }
        return (byte) (ch);
    }
    
    /**
     * See the general contract of the <code>readUnsignedByte</code>
     * method of <code>DataInput</code>.
     * <p>
     * Bytes
     * for this operation are read from the contained
     * input stream.
     *
     * @return the next byte of this input stream, interpreted as an
     *             unsigned 8-bit number.
     * @exception  EOFException  if this input stream has reached the end.
     * @exception  IOException   if an I/O error occurs.
     * @see         RecordInputStream#in
     */
    public final int readUnsignedByte() throws IOException {
        int ch = in.read();
        if (ch < 0) {
            throw new EOFException();
        }
        return ch;
    }
    

    /**
     * See the general contract of the <code>readShort</code> method of
     * <code>DataInput</code>.
     * <p>
     * Bytes for this operation are read from the contained input stream.
     * 
     * @return the next two bytes of this input stream, interpreted as a signed
     *         16-bit number.
     * @exception EOFException
     *                if this input stream reaches the end before reading two
     *                bytes.
     * @exception IOException
     *                if an I/O error occurs.
     * @see RecordInputStream#in
     */
    public final short readShort() throws IOException {
        int ch1 = in.read();
        int ch2 = in.read();
        if ((ch1 | ch2) < 0) {
            throw new EOFException();
        }
        return (short) ((ch1 << 8) + (ch2 << 0));
    }

    /**
     * See the general contract of the <code>readUnsignedShort</code> method
     * of <code>DataInput</code>.
     * <p>
     * Bytes for this operation are read from the contained input stream.
     * 
     * @return the next two bytes of this input stream, interpreted as an
     *         unsigned 16-bit integer.
     * @exception EOFException
     *                if this input stream reaches the end before reading two
     *                bytes.
     * @exception IOException
     *                if an I/O error occurs.
     * @see RecordInputStream#in
     */
    public final int readUnsignedShort() throws IOException {
        int ch1 = in.read();
        int ch2 = in.read();
        if ((ch1 | ch2) < 0) {
            throw new EOFException();
        }
        return (ch1 << 8) + (ch2 << 0);
    }

    /**
     * See the general contract of the <code>readChar</code> method of
     * <code>DataInput</code>.
     * <p>
     * Bytes for this operation are read from the contained input stream.
     * 
     * @return the next two bytes of this input stream as a Unicode character.
     * @exception EOFException
     *                if this input stream reaches the end before reading two
     *                bytes.
     * @exception IOException
     *                if an I/O error occurs.
     * @see RecordInputStream#in
     */
    public final char readChar() throws IOException {
        int ch1 = in.read();
        int ch2 = in.read();
        if ((ch1 | ch2) < 0) {
            throw new EOFException();
        }
        return (char) ((ch1 << 8) + (ch2 << 0));
    }

    /**
     * See the general contract of the <code>readInt</code> method of
     * <code>DataInput</code>.
     * <p>
     * Bytes for this operation are read from the contained input stream.
     * 
     * @return the next four bytes of this input stream, interpreted as an
     *         <code>int</code>.
     * @exception EOFException
     *                if this input stream reaches the end before reading four
     *                bytes.
     * @exception IOException
     *                if an I/O error occurs.
     * @see RecordInputStream#in
     */
    public final int readInt() throws IOException {
        int ch1 = in.read();
        int ch2 = in.read();
        int ch3 = in.read();
        int ch4 = in.read();
        if ((ch1 | ch2 | ch3 | ch4) < 0) {
            throw new EOFException();
        }
        return ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));
    }

    public int readIntCompressed () throws IOException {
        int value = readUnsignedShort ();
        if ((value & 0x8000) != 0) {
            value = ((value & 0x7fff) << 16) + readUnsignedShort ();
        }
        return value;
    }
    
    public short readShortCompressed () throws IOException {
        int value = readUnsignedByte ();
        if ((value & 0x80) != 0) {
            value = ((value & 0x7f) << 8) + readUnsignedByte ();
        }
        return (short)value;
    }
    
    private final byte readBuffer[] = new byte[8];

    /**
     * See the general contract of the <code>readLong</code> method of
     * <code>DataInput</code>.
     * <p>
     * Bytes for this operation are read from the contained input stream.
     * 
     * @return the next eight bytes of this input stream, interpreted as a
     *         <code>long</code>.
     * @exception EOFException
     *                if this input stream reaches the end before reading eight
     *                bytes.
     * @exception IOException
     *                if an I/O error occurs.
     * @see RecordInputStream#in
     */
    public final long readLong() throws IOException {
        readFully(readBuffer, 0, 8);
        return (((long) readBuffer[0] << 56)
                + ((long) (readBuffer[1] & 255) << 48)
                + ((long) (readBuffer[2] & 255) << 40)
                + ((long) (readBuffer[3] & 255) << 32)
                + ((long) (readBuffer[4] & 255) << 24)
                + ((readBuffer[5] & 255) << 16) + ((readBuffer[6] & 255) << 8) + ((readBuffer[7] & 255) << 0));
    }

    /**
     * See the general contract of the <code>readFloat</code> method of
     * <code>DataInput</code>.
     * <p>
     * Bytes for this operation are read from the contained input stream.
     * 
     * @return the next four bytes of this input stream, interpreted as a
     *         <code>float</code>.
     * @exception EOFException
     *                if this input stream reaches the end before reading four
     *                bytes.
     * @exception IOException
     *                if an I/O error occurs.
     * @see java.io.DataInputStream#readInt()
     * @see java.lang.Float#intBitsToFloat(int)
     */
    public final float readFloat() throws IOException {
        return Float.intBitsToFloat(readInt());
    }

    /**
     * See the general contract of the <code>readDouble</code> method of
     * <code>DataInput</code>.
     * <p>
     * Bytes for this operation are read from the contained input stream.
     * 
     * @return the next eight bytes of this input stream, interpreted as a
     *         <code>double</code>.
     * @exception EOFException
     *                if this input stream reaches the end before reading eight
     *                bytes.
     * @exception IOException
     *                if an I/O error occurs.
     * @see java.io.DataInputStream#readLong()
     * @see java.lang.Double#longBitsToDouble(long)
     */
    public final double readDouble() throws IOException {
        return Double.longBitsToDouble(readLong());
    }
    /**
     * See the general contract of the <code>readUTF</code> method of
     * <code>DataInput</code>.
     * <p>
     * Bytes for this operation are read from the contained input stream.
     * 
     * @return a Unicode string.
     * @exception EOFException
     *                if this input stream reaches the end before reading all
     *                the bytes.
     * @exception IOException
     *                if an I/O error occurs.
     * @exception UTFDataFormatException
     *                if the bytes do not represent a valid UTF-8 encoding of a
     *                string.
     * @see java.io.DataInputStream#readUTF(java.io.DataInput)
     */
    public final String readUTF() throws IOException {
        int index = readIntCompressed();
        if (index == 0) {
            return null;
        }
        
        // Make sure the pool of strings is initialised.
        setupPools();
        
        return stringPool[index - 1];
    }
    
    /**
     * This function reads Strings serialized via
     * RecordOutputStream.writeUTFUnpooled().
     * Normally strings are written as an index into a pool of unique strings
     * for the stream content.  This is for reading strings that were
     * serialized directly.   
     * @return the serialized String.
     * @throws IOException
     */
    public final String readUTFUnPooled () throws IOException {
        return readUTFInternal();
    }
    
    /**
     * See the general contract of the <code>readUTF</code> method of
     * <code>DataInput</code>.
     * <p>
     * Bytes for this operation are read from the contained input stream.
     * 
     * @return a Unicode string.
     * @exception EOFException
     *                if this input stream reaches the end before reading all
     *                the bytes.
     * @exception IOException
     *                if an I/O error occurs.
     * @exception UTFDataFormatException
     *                if the bytes do not represent a valid UTF-8 encoding of a
     *                string.
     * @see java.io.DataInputStream#readUTF(java.io.DataInput)
     */
    private final String readUTFInternal() throws IOException {
        int utflen = this.readUnsignedShort();
        StringBuilder str = new StringBuilder(utflen);
        byte bytearr[] = new byte[utflen];
        int c, char2, char3;
        int count = 0;

        this.readFully(bytearr, 0, utflen);

        while (count < utflen) {
            c = (bytearr[count] ^ RecordOutputStream.OBFUSCATING_BYTE) & 0xff;
            switch (c >> 4) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
                /* 0xxxxxxx */
                count++;
                str.append((char) c);
                break;
            case 12:
            case 13:
                /* 110x xxxx 10xx xxxx */
                count += 2;
                if (count > utflen) {
                    throw new UTFDataFormatException();
                }
                char2 = (bytearr[count - 1] ^ RecordOutputStream.OBFUSCATING_BYTE);
                if ((char2 & 0xC0) != 0x80) {
                    throw new UTFDataFormatException();
                }
                str.append((char) (((c & 0x1F) << 6) | (char2 & 0x3F)));
                break;
            case 14:
                /* 1110 xxxx 10xx xxxx 10xx xxxx */
                count += 3;
                if (count > utflen) {
                    throw new UTFDataFormatException();
                }
                char2 = (bytearr[count - 2] ^ RecordOutputStream.OBFUSCATING_BYTE);
                char3 = (bytearr[count - 1] ^ RecordOutputStream.OBFUSCATING_BYTE);
                if (((char2 & 0xC0) != 0x80) || ((char3 & 0xC0) != 0x80)) {
                    throw new UTFDataFormatException();
                }
                str
                        .append((char) (((c & 0x0F) << 12)
                                | ((char2 & 0x3F) << 6) | ((char3 & 0x3F) << 0)));
                break;
            default:
                /* 10xx xxxx,  1111 xxxx */
                throw new UTFDataFormatException();
            }
        }
        // The number of chars produced may be less than utflen
        return new String(str);
    }

    /**
     * Bookmark the current read position.
     * @return a Bookmark.
     */
    public Bookmark bookmark () {
        return new Bookmark(in.position);
    }
    
    /**
     * Reset the read position to a bookmark.
     * @param bookmark
     * @throws IOException
     */
    public void reposition(Bookmark bookmark) throws IOException {
        if (bookmark.position < 0 || bookmark.position > in.nValidBytes) {
            throw new IOException ("Attempt to set read position to invalid value: " + bookmark.position);
        }
        
        in.position = bookmark.position;
    }
    
    /**
     * Reads a module name from the stream.
     * @return the module name read from the stream as a ModuleName.
     */
    public final ModuleName readModuleName() throws IOException {
        final String moduleNameString = readUTF();
        final ModuleName cachedModuleName = moduleNameCache.get(moduleNameString);
        
        if (cachedModuleName != null) {
            return cachedModuleName;
            
        } else {
            final ModuleName moduleName = ModuleName.make(moduleNameString);
            moduleNameCache.put(moduleNameString, moduleName);
            return moduleName;
        }
    }

    /**
     * Read a qualified name from the stream. It handles null QualifiedName values.
     * Should not be called except by QualifiedName.load, as the later method also
     * makes use of the static QualifiedName constants.
     * @return a QualifiedName. May be null.
     * @throws IOException
     */
    public final QualifiedName readQualifiedName () throws IOException {
        int qnIndex = readIntCompressed();
        if (qnIndex == 0) {
            return null;
        }
        
        // Make sure the pool is initialized.
        setupPools();
        
        return qualifiedNamePool[qnIndex - 1];
    }
    
    /**
     * Closes the underlying input stream.
     * @throws IOException
     */
    public void close() throws IOException {
        in.close();
    }
    
    
    /**
     * Class to encapsulate the record header information. 
     */
    public static final class RecordHeaderInfo {
        private final short recordTag;
        private final short schema;
        private final int length;
        private final int nextRecordStart;
        
        RecordHeaderInfo (short recordTag, short schema, int length, int startPos) {
            this.recordTag = recordTag;
            this.schema = schema;
            this.length = length;
            this.nextRecordStart = startPos + length;
        }
        
        public short getRecordTag () {
            return recordTag;
        }
        public short getSchema () {
            return schema;
        }
        int getLength () {
            return length;
        }
        int getNextRecordStart () {
            return nextRecordStart;
        }
    }
    
    /**
     * This is an InputStream which fully buffers its contents.
     * The full buffering allows for rewinding the stream to a previous point.
     */
    private static final class FullyBufferedInputStream extends FilterInputStream {
        /**
         * The binary data that is buffered. Only the first nValidBytes bytes of this array are valid - the length
         * of this array can be longer than nValidBytes.
         */
        byte[] data;
        /**
         * The current read position within the buffered data. Its value is always less than nValidBytes.
         */
        private int position = 0;
        /**
         * The number of bytes in the data array which are validly read from the underlying stream.
         */
        int nValidBytes = 0;
        
        /**
         * Constructs a FullyBufferedInputStream wrapping an underlying input stream.
         * @param s the underlying input stream.
         */
        FullyBufferedInputStream (InputStream s) {
            super(s);
            data = new byte[0];
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public int read () throws IOException {
            fillBuffer(1);
            if (position >= nValidBytes) {
                return -1;
            }
            return (data[position++] & 0xff);
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public int read (byte[] b) throws IOException {
            fillBuffer(b.length);
            if (position >= nValidBytes) {
                return -1;
            }
            int nToRead = Math.min(b.length, nValidBytes - position);
            System.arraycopy(data, position, b, 0, nToRead);
            position += nToRead;
            return nToRead;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public int read (byte[] b, int off, int len) throws IOException {
            if (b.length - off < len) {
                throw new IOException ("Insufficient sized buffer.");
            }
            fillBuffer(len);
            if (position >= nValidBytes) {
                return -1;
            }
            int nToRead = Math.min(len, nValidBytes - position);
            System.arraycopy(data, position, b, off, nToRead);
            position += nToRead;
            return nToRead;
        }
        
        /**
         * Attempts to fill the data buffer to (position + lengthFromCurrentPos). It may read fewer bytes than
         * requested if the underlying stream ends before then.
         * 
         * @param lengthFromCurrentPos the number of bytes requested past the current position.
         * @throws IOException
         */
        private void fillBuffer(int lengthFromCurrentPos) throws IOException {
            
            int nDesiredValidBytes = position + lengthFromCurrentPos;
            
            // we do not need to read more bytes into the buffer if we have enough to satisfy the request
            if (nDesiredValidBytes <= nValidBytes) {
                return;
            }
            
            if (data.length < nDesiredValidBytes) {
                // need to grow the buffer - make its length (1.5 * nDesiredValidBytes), and at least 4KB beyond what we desire
                int newLength = nDesiredValidBytes + Math.max(4096, nDesiredValidBytes / 2);
                
                byte[] newData = new byte[newLength];
                System.arraycopy(data, 0, newData, 0, data.length);
                data = newData;
            }
            
            // read the bytes in a loop - we use a loop because while InputStream.read() attempts to read as many bytes
            // as requested (in our case nBytesLeftToRead), it is allowed to read fewer bytes than requested --
            // even if there are more bytes to be read. [This is the case with input streams obtained from zip file
            // entries, for example.]
            int nBytesLeftToRead = nDesiredValidBytes - nValidBytes;
            
            while (nBytesLeftToRead > 0) {
                int nRead = in.read(data, nValidBytes, nBytesLeftToRead);
                
                // the only accurate way of determining that the input stream has reached the end is
                // to check that it returns -1 on a read() call.
                if (nRead == -1) {
                    break;
                }
                
                nValidBytes += nRead;
                nBytesLeftToRead -= nRead;
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int available() throws IOException {
            int availFromStream = in.available();
            if (position < nValidBytes) {
                return (nValidBytes - position) + availFromStream;
            } else {
                return availFromStream;
            }
        }

        /**
         * mark/reset is not supported.
         * 
         * Doing nothing is the default behaviour of the {@link InputStream} base class.
         */
        @Override
        public void mark(int readlimit) {}

        /**
         * mark/reset is not supported.
         * 
         * Returning false is the default behaviour of the {@link InputStream} base class.
         */
        @Override
        public boolean markSupported() {
            return false;
        }

        /**
         * mark/reset is not supported.
         * 
         * Doing nothing is the default behaviour of the {@link InputStream} base class.
         */
        @Override
        public synchronized void reset() throws IOException {
            throw new IOException("mark/reset not supported");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public long skip(long n) throws IOException {
            fillBuffer((int)n);
            int nToSkip = Math.min((int)n, nValidBytes - position);
            position += nToSkip;
            return nToSkip;
        }
    }

    public static final boolean[] bitArrayToBooleans (byte[] b) {
        return bitArrayToBooleans(b, b.length);
    }
    
    public static final boolean[] bitArrayToBooleans (byte[] b, int nBooleans) {
        if (nBooleans >  (b.length * 8)) {
            throw new InvalidParameterException("Invalid nBooleans in RecordInputStream.bitArrayToBooleans().");
        }
        
        boolean booleans[] = new boolean[nBooleans];
        for (int i = 0; i < nBooleans; ++i) {
            int iByte = i / 8;
            int iBit = i % 8;
            booleans[i] = (b[iByte] & (0x01 << iBit)) > 0;
        }
        
        return booleans;
    }
    
    public static final boolean booleanFromBitArray(byte b, int index) {
        int iBit = index % 8;
        return (b & (0x01 << iBit)) != 0;
    }
    
    public static final boolean booleanFromBitArray(byte[] b, int index) {
        int iByte = index / 8;
        int iBit = index % 8;
        if (iByte > b.length) {
            throw new InvalidParameterException("Invalid index in RecordInputStream.booleanFromBitArray().");
        }
        return (b[iByte] & (0x01 << iBit)) != 0;
    }
    
    /** 
     * The bookmark class marks a position in the RecordInputStream
     * and allows rewinding to that point. 
     */
    public static final class Bookmark {
        private final int position;

        Bookmark(int position) {
            this.position = position;
        }
    }
}
