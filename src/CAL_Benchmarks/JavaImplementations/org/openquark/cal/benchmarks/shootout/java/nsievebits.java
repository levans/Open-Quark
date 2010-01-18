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

/* The Computer Language Benchmarks Game
http://shootout.alioth.debian.org/

contributed by Alkis Evlogimenos
slightly modified by Pierre-Olivier Gaillard
*/


public class nsievebits
{
   private static class MyBitSet {
      private int[] bits;
      int length;
      private static final int mask = 31;
      private static final int shift = 5;
      public MyBitSet(int m) {
         bits = new int[m/8+1];
         length = m;
      }

      public void setRange() {
         for (int i =0; i < ((length >> shift)+1); i++ ){
               bits[i] = -1;
            }
      }
      public boolean get(int i){
         return  ((((int) bits[i >> shift]) >>>  (i & mask)) & 1) != 0;
      }
      public void set(int i) {
         bits[i >> shift] |= (1 << (i & mask));
      }

      public void clear(int i) {
         bits[i >> shift] &= ~(1 << (i & mask));
      }

      public static void test() {
         MyBitSet bs = new MyBitSet(128);
         bs.setRange();
         bs.clear(5);
         System.out.println("Position 5 : " + bs.get(5));
         System.out.println("Position 6 : " + bs.get(6));
      }
   }
   private static int nsieve(int m, MyBitSet bits) {



     bits.setRange();
      int count = 0;
      for (int i = 2; i <= m; ++i) {
         if (bits.get(i)) {
         //System.err.println("Found prime : " + i);
         for (int j = i + i; j <=m; j += i)
            bits.clear(j);
            ++count;
         }
      }
      return count;
   }

   public static String padNumber(int number, int fieldLen)
   {
      StringBuffer sb = new StringBuffer();
      String bareNumber = "" + number;
      int numSpaces = fieldLen - bareNumber.length();

      for (int i = 0; i < numSpaces; i++)
         sb.append(" ");

      sb.append(bareNumber);

      return sb.toString();
   }

   public static void main(String[] args)
   {
     //MyBitSet.test();
      int n = 2;
      if (args.length > 0)
         n = Integer.parseInt(args[0]);
      if (n < 2)
         n = 2;

      int m = (1 << n) * 10000;
      MyBitSet bits = new MyBitSet(m+1);
      System.out.println("Primes up to " + padNumber(m, 8) + " "
                            + padNumber(nsieve(m,bits), 8));

      m = (1 << n-1) * 10000;
      System.out.println("Primes up to " + padNumber(m, 8) + " "
                            + padNumber(nsieve(m,bits), 8));

      m = (1 << n-2) * 10000;
      System.out.println("Primes up to " + padNumber(m, 8) + " "
                            + padNumber(nsieve(m,bits), 8));
   }
}

