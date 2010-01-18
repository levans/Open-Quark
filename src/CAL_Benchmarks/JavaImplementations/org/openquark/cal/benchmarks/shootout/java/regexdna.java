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

/* The Computer Language Shootout
   http://shootout.alioth.debian.org/
   contributed by Josh Goldfoot
   based on the Nice entry by Isaac Guoy
*/

import java.io.*;
import java.lang.*;
import java.util.regex.*;

public class regexdna {
    
    public regexdna() {
    }

    public static void main(String[] args) {
        BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
        StringBuffer sb = new StringBuffer(10240);
        char[] cbuf = new char[10240];
        int charsRead = 0;
        try {
            while ((charsRead = r.read(cbuf, 0, 10240)) != -1) 
                sb.append(cbuf, 0, charsRead);
        } catch (java.io.IOException e) {
            return;
        }
        String sequence = sb.toString();
        
        int initialLength = sequence.length();
        sequence = Pattern.compile(">.*\n|\n").matcher(sequence).replaceAll("");
        int codeLength = sequence.length();
        
        String[] variants = { "agggtaaa|tttaccct" ,"[cgt]gggtaaa|tttaccc[acg]", "a[act]ggtaaa|tttacc[agt]t", 
                 "ag[act]gtaaa|tttac[agt]ct", "agg[act]taaa|ttta[agt]cct", "aggg[acg]aaa|ttt[cgt]ccct",                     
                 "agggt[cgt]aa|tt[acg]accct", "agggta[cgt]a|t[acg]taccct", "agggtaa[cgt]|[acg]ttaccct" };
        for (int i = 0; i < variants.length; i++) {
            int count = 0;
            Matcher m = Pattern.compile(variants[i]).matcher(sequence);
            while (m.find())
                count++;
            System.out.println(variants[i] + " " + count);
        }
        
        sequence = Pattern.compile("B").matcher(sequence).replaceAll("(c|g|t)");
        sequence = Pattern.compile("D").matcher(sequence).replaceAll("(a|g|t)");
        sequence = Pattern.compile("H").matcher(sequence).replaceAll("(a|c|t)");
        sequence = Pattern.compile("K").matcher(sequence).replaceAll("(g|t)");
        sequence = Pattern.compile("M").matcher(sequence).replaceAll("(a|c)");
        sequence = Pattern.compile("N").matcher(sequence).replaceAll("(a|c|g|t)");
        sequence = Pattern.compile("R").matcher(sequence).replaceAll("(a|g)");
        sequence = Pattern.compile("S").matcher(sequence).replaceAll("(c|g)");
        sequence = Pattern.compile("V").matcher(sequence).replaceAll("(a|c|g)");
        sequence = Pattern.compile("W").matcher(sequence).replaceAll("(a|t)");
        sequence = Pattern.compile("Y").matcher(sequence).replaceAll("(c|t)");
        
        System.out.println();
        System.out.println(initialLength);
        System.out.println(codeLength);
        System.out.println(sequence.length());
    }
}
