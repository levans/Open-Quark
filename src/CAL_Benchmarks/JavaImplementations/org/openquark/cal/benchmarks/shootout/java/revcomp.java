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
This code has been taken from the computer language shootout web site
(http://shootout.alioth.debian.org/) under the BSD license stated below. 
It is included with Open Quark only for comparison with 
the Open Quark benchmark implementations.

The modifications from the original are restricted to:
packaging it within the Open Quark package hierarchy,
making the main class/method public so that it can be run from within ICE,
minimal changes to make it compile with Java 1.4.
 
Revised BSD license

This is a specific instance of the Open Source Initiative (OSI) BSD license template
http://www.opensource.org/licenses/bsd-license.php


Copyright 2004,2005,2006 Brent Fulgham
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

   Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

   Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.

   Neither the name of "The Computer Language Shootout Benchmarks" nor the name of "Computer Language Shootout" nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/


package org.openquark.cal.benchmarks.shootout.java;

/*
   The Great Computer Language Shootout
   http://shootout.alioth.debian.org/
   contributed by Mehmet D. AKIN
   For an input size of n MB you need a direct memory of 3*n size.
  To ensure this, you must run java with the option: -XX:MaxDirectMemorySize=<m>M 
  with <m> at least 3*n. Default for <m> is 64MB.
  (see http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4879883)
*/


/*
 * The Computer Language Shootout
 * http://shootout.alioth.debian.org/
 * contributed by Anthony Donnefort
 */

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class revcomp {
    private static final byte[] comp = new byte[128];
    private static final int LINE_LENGTH = 60;
    private static final byte CR = '\n';
    static {
        for (int i = 0; i < comp.length; i++) comp[i] = (byte) i;
        comp['t'] = comp['T'] = 'A';
        comp['a'] = comp['A'] = 'T';
        comp['g'] = comp['G'] = 'C';
        comp['c'] = comp['C'] = 'G';
        comp['v'] = comp['V'] = 'B';
        comp['h'] = comp['H'] = 'D';
        comp['r'] = comp['R'] = 'Y';
        comp['m'] = comp['M'] = 'K';
        comp['y'] = comp['Y'] = 'R';
        comp['k'] = comp['K'] = 'M';
        comp['b'] = comp['B'] = 'V';
        comp['d'] = comp['D'] = 'H';
        comp['u'] = comp['U'] = 'A';
    }

    private static int maxInputLineLength = 0;

    // Will add CR then print the input data
    private static void formatAndPrint(List<byte[]> lineBuffer) {
        byte[] data = null;
        int remainOnLine = 0;
        int totalSize = 0;
        int linePointer = 0;
        byte[] printBuffer = new byte[((maxInputLineLength + 1) * lineBuffer.size())];

        for (int i = lineBuffer.size() - 1; i >= 0 ; i--) {
            data = lineBuffer.get(i);
            if (data.length <= (remainOnLine = LINE_LENGTH - linePointer)) {
                System.arraycopy(data, 0, printBuffer, totalSize, data.length);
                linePointer += data.length;
                totalSize += data.length;
            } else {
                linePointer = data.length - remainOnLine;
                System.arraycopy(data, 0, printBuffer, totalSize, remainOnLine);
                printBuffer[totalSize + remainOnLine] = CR;
                System.arraycopy(data, remainOnLine, printBuffer, totalSize + remainOnLine + 1, linePointer);
                totalSize += data.length + 1;
            }
        }
        if (totalSize > 0) printBuffer[totalSize++] = CR;
        System.out.write(printBuffer, 0, totalSize);
        lineBuffer.clear();
    }

    public static void main(String[] args) throws IOException {
        byte[] revcompLine = null;
        List<byte[]> revcompBuffer = new ArrayList<byte[]>();

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        for (String line; (line = in.readLine()) != null;) {
            if (line.startsWith(">")) {
                formatAndPrint(revcompBuffer);
                System.out.println(line);
            } else {
                // Keep track of the maximum input line length. We will need that later to allocate a buffer that will not need to be resized.
                if (line.length() > maxInputLineLength) maxInputLineLength = line.length();
                revcompLine = new byte[line.length()];
                int j = line.length() - 1;
                // The line is reversed and complements are calculated here.
                for (int i = 0; i < line.length() ; i++) revcompLine[i] = comp[line.charAt(j--)];
                revcompBuffer.add(revcompLine);
            }
        }
        formatAndPrint(revcompBuffer);
    }
}