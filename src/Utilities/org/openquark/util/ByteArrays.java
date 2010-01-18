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
 * ByteArrays.java
 * Created: Nov 23, 2005
 * By: Richard Webster
 */
package org.openquark.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

/**
 * Utility methods for working with byte arrays.
 */
public final class ByteArrays {

    // This class should not be instantiated.
    private ByteArrays() {}

    /**
     * Returns a compressed version of a byte array.
     */
    public static byte[] compressByteArray(byte[] byteArray) throws IOException {
        InputStream inputStream = new ByteArrayInputStream(byteArray);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        OutputStream compressedOutputStream = new DeflaterOutputStream(outputStream);

        IOStreams.transferData(inputStream, compressedOutputStream);
        compressedOutputStream.close();

        return outputStream.toByteArray();
    }

    /**
     * Returns a decompressed version of a byte array.
     */
    public static byte[] decompressByteArray(byte[] byteArray) throws IOException {
        InputStream inputStream = new ByteArrayInputStream(byteArray);
        InputStream decompressedInputStream = new InflaterInputStream(inputStream);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        IOStreams.transferData(decompressedInputStream, outputStream);
        outputStream.close();

        return outputStream.toByteArray();
    }

    /**
     * Returns a string representing the data in the byte array.
     * Each byte in the array will be represented by 2 hex digit in the string.
     * A space will be added between the value for each byte.
     */
    public static String byteArrayToHexString(byte[] byteArray) {
        final int nBytes = byteArray.length;
        StringBuilder sb = new StringBuilder(nBytes * 3);

        for (int byteN = 0; byteN < nBytes; ++byteN) {
            byte val = byteArray[byteN];

            // Convert the byte to its hex value.
            // Be careful to handle negative byte values correctly.
            String hexVal = Integer.toHexString(val & 0xff);

            // If the hex string is only one character long, then add a leading zero.
            // The hex value should either be one or two characters in length.
            if (hexVal.length() == 1) {
                sb.append('0');
            }
            sb.append(hexVal);
            sb.append(' ');
        }
        return sb.toString();
    }
}
