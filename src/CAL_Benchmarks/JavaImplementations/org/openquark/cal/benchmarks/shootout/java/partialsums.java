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
 * The Computer Language Shootout http://shootout.alioth.debian.org/ contributed
 * by Josh Goldfoot based on the D version by Dave Fladebo modified by Isaac
 * Gouy
 */

// The Computer Language Shootout
// http://shootout.alioth.debian.org/
// contributed by Isaac Gouy
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class partialsums {
	static final double twothirds = 2.0 / 3.0;
	private static final NumberFormat formatter = new DecimalFormat("#0.000000000");

	public static void main(String[] args) {
		int n = Integer.parseInt(args[0]);

		
		double a1 = 0.0, a2 = 0.0, a3 = 0.0, a4 = 0.0, a5 = 0.0;
		double a6 = 0.0, a7 = 0.0, a8 = 0.0, a9 = 0.0, alt = -1.0;

		for (int k = 1; k <= n; k++) {
			double k2 = Math.pow(k, 2), k3 = k2 * k;
			double sk = Math.sin(k), ck = Math.cos(k);
			alt = -alt;

			a1 += Math.pow(twothirds, k - 1);
			a2 += Math.pow(k, -0.5);
			a3 += 1.0 / (k * (k + 1.0));
			a4 += 1.0 / (k3 * sk * sk);
			a5 += 1.0 / (k3 * ck * ck);
			a6 += 1.0 / k;
			a7 += 1.0 / k2;
			a8 += alt / k;
			a9 += alt / (2.0 * k - 1.0);
		}

		/*	Java 1.5
		System.out.printf("%.9f\t(2/3)^k\n", a1);
		System.out.printf("%.9f\tk^-0.5\n", a2);
		System.out.printf("%.9f\t1/k(k+1)\n", a3);
		System.out.printf("%.9f\tFlint Hills\n", a4);
		System.out.printf("%.9f\tCookson Hills\n", a5);
		System.out.printf("%.9f\tHarmonic\n", a6);
		System.out.printf("%.9f\tRiemann Zeta\n", a7);
		System.out.printf("%.9f\tAlternating Harmonic\n", a8);
		System.out.printf("%.9f\tGregory\n", a9);
		*/

		System.out.println(formatter.format(a1) + "\t(2/3)^k");
		System.out.println(formatter.format(a2) + "\tk^-0.5");
		System.out.println(formatter.format(a3) + "\t1/k(k+1)");
		System.out.println(formatter.format(a4) + "\tFlint Hills");
		System.out.println(formatter.format(a5) + "\tCookson Hills");
		System.out.println(formatter.format(a6) + "\tHarmonic");
		System.out.println(formatter.format(a7) + "\tRiemann Zeta");
		System.out.println(formatter.format(a8) + "\tAlternating Harmonic");
		System.out.println(formatter.format(a9) + "\tGregory");
		
	}
}
