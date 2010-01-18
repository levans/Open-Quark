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

This version of PiDigits is the fastest pure Java version available on the shootout website. 
A faster Java version based on the GMP library does exist.
 
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
 
   contributed by Isaac Gouy
*/

import java.math.BigInteger;

public class pidigits {
   static final int L = 10;

   public static void main(String args[]) { 
      int n = Integer.parseInt(args[0]);
      int j = 0;
   
      PiDigitSpigot digits = new PiDigitSpigot();
      
      while (n > 0){
         if (n >= L){
            for (int i=0; i<L; i++) System.out.print( digits.next() );
            j += L;
         } else {
            for (int i=0; i<n; i++) System.out.print( digits.next() );
            for (int i=n; i<L; i++) System.out.print(" ");  
            j += n;   
         }
         System.out.print("\t:"); System.out.println(j);
         n -= L;           
      }               
   }
}


class PiDigitSpigot {
   Transformation z, x, inverse;            
       
   public PiDigitSpigot(){
      z = new Transformation(1,0,0,1);
      x = new Transformation(0,0,0,0);
      inverse = new Transformation(0,0,0,0);
   }   
   
   public int next(){
      int y = digit();
      if (isSafe(y)){ 
         z = produce(y); return y;
      } else {
         z = consume( x.next() ); return next();   
      }
   }    
      
   public int digit(){
      return z.extract(3);
   }        
   
   public boolean isSafe(int digit){
      return digit == z.extract(4);
   }   
   
   public Transformation produce(int i){
      return ( inverse.qrst(10,-10*i,0,1) ).compose(z);
   }     
      
   public Transformation consume(Transformation a){
      return z.compose(a);
   }                   
} 


class Transformation {
   BigInteger q, r, s, t;
   int k;              
       
   public Transformation(int q, int r, int s, int t){
      this.q = BigInteger.valueOf(q);
      this.r = BigInteger.valueOf(r);
      this.s = BigInteger.valueOf(s);
      this.t = BigInteger.valueOf(t);                  
      k = 0;
   }
   
   public Transformation(BigInteger q, BigInteger r, BigInteger s, BigInteger t){
      this.q = q;
      this.r = r;
      this.s = s;
      this.t = t;                  
      k = 0;
   }        
   
   public Transformation next(){
      k++;
      q = BigInteger.valueOf(k);
      r = BigInteger.valueOf(4 * k + 2);
      s = BigInteger.valueOf(0);
      t = BigInteger.valueOf(2 * k + 1); 
      return this;                 
   }      
   
   public int extract(int j){
      BigInteger bigj = BigInteger.valueOf(j);
      BigInteger numerator = (q.multiply(bigj)).add(r);
      BigInteger denominator = (s.multiply(bigj)).add(t);                  
      return ( numerator.divide(denominator) ).intValue();                    
   }     
   
   public Transformation qrst(int q, int r, int s, int t){
      this.q = BigInteger.valueOf(q);
      this.r = BigInteger.valueOf(r);
      this.s = BigInteger.valueOf(s);
      this.t = BigInteger.valueOf(t); 
      k = 0;  
      return this;                             
   }         
  
   public Transformation compose(Transformation a){      
      return new Transformation(
         q.multiply(a.q)
         ,(q.multiply(a.r)).add( (r.multiply(a.t)) ) 
         ,(s.multiply(a.q)).add( (t.multiply(a.s)) ) 
         ,(s.multiply(a.r)).add( (t.multiply(a.t)) )                   
         );                    
   }          
}


  
