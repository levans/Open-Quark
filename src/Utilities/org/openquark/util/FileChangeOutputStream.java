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
 * FileChangeOutputStream.java
 * Creation date: Aug 11, 2005.
 * By: Edward Lam
 */
package org.openquark.util;


import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Reads a file until a difference is noticed, then writes starting from the difference.
 * If there is no difference between the file and the stream, the file is not modified.
 * 
 * Typically, reading from a file is much faster than writing to it, so this class can yield big 
 * savings in situations where there is little or no change in a file's data.
 */
public final class FileChangeOutputStream extends OutputStream {
    
    /** The size of the byte buffer into which file data will be read. */
    private static final int BUF_SIZE = 4096;           // Most classes are <4K.
    
    /** The file channel backing this stream. */
    private final FileChannel fileChannel;
    
    /** Whether a difference has been observed between the file and the data to write. */
    private boolean sawDifference;

    /**
     * The thread-local byte buffer, into which file data will be read.
     * Used by lookupClassData() and loadFileData().
     */
    private static ThreadLocal<ByteBuffer> threadLocalByteBuffer = new ThreadLocal<ByteBuffer>() {
        @Override
        protected synchronized ByteBuffer initialValue() {
            // allocateDirect() doesn't help, since we have to copy the bytes out to an array in order to perform comparisons anyways.
            return ByteBuffer.allocate(BUF_SIZE);
        }
    };
    
    /**
     * Constructor for a FileChangeOutputStream.
     * @param fileName the name of the file on which to create a stream.
     * @throws IOException
     */
    public FileChangeOutputStream(String fileName) throws IOException {
        this(new File(fileName));
    }

    /**
     * Constructor for a FileChangeOutputStream.
     * @param file the file on which to create a stream.
     * @throws IOException
     */
    public FileChangeOutputStream(File file) throws IOException {
        // "rws" and "rwd" are also available.
        fileChannel = new RandomAccessFile(file, "rw").getChannel();          // Creates the file if the file doesn't exist.
    }

    /**
     * @return whether a difference has been observed between the data and the file.
     */
    public boolean isDifferent() {
        return sawDifference;
    }

    /**
     * {@inheritDoc}
     */
//    synchronized 
    @Override
    public void write(byte[] data, int offset, int length) throws IOException {
        if (sawDifference) {
            // Really write the data.
            reallyWrite(data, offset, length);

        } else {
        
            ByteBuffer byteBuffer = threadLocalByteBuffer.get();
            
            int nBytesRead = 0;
            do {
                // read some bytes.
                int readBytes = fileChannel.read(byteBuffer);
                
                try {
                    if (readBytes < 0) {
                        // We are a point in the file where there are no bytes to read.
                        reallyWrite(data, offset, length);
                        
                        sawDifference = true;
                        
                        return;
                    } 

                    if (!equalsArray(data, offset + nBytesRead, byteBuffer.array(), readBytes)) {
                        // We got to a point in the file where the bytes are different.
                        
                        // Seek to the original position and write.
                        long currentPosition = fileChannel.position();
                        fileChannel.position(currentPosition - (nBytesRead + readBytes));
                        
                        reallyWrite(data, offset, length);
                        
                        sawDifference = true;
                        
                        return;
                    }

                } finally {
                    // Make sure the byte buffer is ready for reading again.
                    byteBuffer.clear();
                }
                
                // Haven't found a difference yet.
                
                nBytesRead += readBytes;
                
            } while (nBytesRead < length);
        }
    }

    /**
     * Write data to the file channel.
     * @param data the data to write.
     * @param offset the offset into the data array from which to write.
     * @param length the length of the data to write.
     * @throws IOException
     */
    private void reallyWrite(byte[] data, int offset, int length) throws IOException {
        // Really write the data.
        ByteBuffer bufToWrite = ByteBuffer.wrap(data, offset, length);
        do {
            fileChannel.write(bufToWrite);
        } while (bufToWrite.hasRemaining());
    }

    /**
     * Check whether array elements are equal.
     * 
     * @param a1 the first array to be tested.
     * @param offset the offset into a1 from which to start testing.
     * @param a2 the second array to be tested.
     * @param len the number of elements to test for equality.
     * @return true if the elements in the two arrays are equal.
     */
    private static boolean equalsArray(byte[] a1, int offset, byte[] a2, int len) {
        for (int i = 0, aIndex = offset; i < len; i++) {
            if (a1[aIndex] != a2[i]) {
                return false;
            }
            aIndex++;
        }
        return true;
    }
    
    /**
     * {@inheritDoc}
     */
//    synchronized 
    @Override
    public void write(byte[] data) throws IOException {
        write(data, 0, data.length);
    }

    /**
     * {@inheritDoc}
     */
//    synchronized 
    @Override
    public void write(int data) throws IOException {
        write(new byte[]{(byte)data});
    }

    /**
     * {@inheritDoc}
     */
//    synchronized 
    @Override
    public void close() throws IOException {
        long pos = fileChannel.position();
        if (fileChannel.size() != pos) {
            fileChannel.truncate(pos);
            sawDifference = true;
        }
        fileChannel.close();
    }
}
