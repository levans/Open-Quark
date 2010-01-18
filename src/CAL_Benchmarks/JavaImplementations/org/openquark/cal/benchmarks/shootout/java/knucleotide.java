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
/* The Great Computer Language Shootout
   http://shootout.alioth.debian.org/
 
   contributed by James McIlree
*/

import java.util.*;
import java.io.*;
import java.text.*;

public class knucleotide {
  String sequence;
  int count = 1;

  knucleotide(String s) {
    sequence = s;
  }

  public static void main(String[] args) throws Exception
  {
    StringBuffer sbuffer = new StringBuffer();
    String line;
    
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    while ((line = in.readLine()) != null) {
      if (line.startsWith(">THREE")) break;
    }
    
    while ((line = in.readLine()) != null) {
      char c = line.charAt(0);
      if (c == '>')
        break;
      else if (c != ';')
        sbuffer.append(line.toUpperCase());
    }
    
    knucleotide kn = new knucleotide(sbuffer.toString());
    kn.writeFrequencies(1);
    kn.writeFrequencies(2);

    kn.writeCount("GGT");
    kn.writeCount("GGTA");
    kn.writeCount("GGTATT");
    kn.writeCount("GGTATTTTAATT");
    kn.writeCount("GGTATTTTAATTTATAGT");
  }

  void writeFrequencies(int nucleotideLength) {
    Map frequencies = calculateFrequencies(nucleotideLength);
    ArrayList list = new ArrayList(frequencies.size());
    Iterator it = frequencies.entrySet().iterator();

    while (it.hasNext()) {
      knucleotide fragment = (knucleotide)((Map.Entry)it.next()).getValue();
      list.add(fragment);
    }

    Collections.sort(list, new Comparator() {
        public int compare(Object o1, Object o2) {
          int c = ((knucleotide)o2).count - ((knucleotide)o1).count;
          if (c == 0) {
            c = ((knucleotide)o1).sequence.compareTo(((knucleotide)o2).sequence);
          }
          return c;
        }
      });

    NumberFormat nf = NumberFormat.getInstance();
    nf.setMaximumFractionDigits(3);
    nf.setMinimumFractionDigits(3);

    int sum = sequence.length() - nucleotideLength + 1;

    for (int i=0; i<list.size(); i++) {
      knucleotide fragment = (knucleotide)list.get(i);
      double percent = (double)fragment.count/(double)sum * 100.0;
      System.out.println(fragment.sequence + " " + nf.format(percent) );
    }
    System.out.println("");
  }

  void writeCount(String nucleotideFragment) {
    Map frequencies = calculateFrequencies(nucleotideFragment.length());

    knucleotide found = (knucleotide)frequencies.get(nucleotideFragment);
    int count = (found == null) ? 0 : found.count;
    System.out.println(count + "\t" + nucleotideFragment);
  }

  Map calculateFrequencies(int fragmentLength) {
    HashMap map = new HashMap();
    for (int offset=0; offset<fragmentLength; offset++)
      calculateFrequencies(map, offset, fragmentLength);

    return map;
  }

  // Is this method really needed? The benchmark specification seems to
  // indicate so, but it is not entirely clear. This method could easily
  // be folded up.
  void calculateFrequencies(Map map, int offset, int fragmentLength) {
    int lastIndex = sequence.length() - fragmentLength + 1;
    for (int index=offset; index<lastIndex; index+=fragmentLength) {
      String temp = sequence.substring(index, index + fragmentLength);
      knucleotide fragment = (knucleotide)map.get(temp);
      if (fragment != null)
        fragment.count++;
      else
        map.put(temp, new knucleotide(temp));
    }
  }
}

