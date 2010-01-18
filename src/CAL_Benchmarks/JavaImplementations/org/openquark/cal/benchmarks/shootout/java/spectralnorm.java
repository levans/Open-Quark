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
 
 contributed by Java novice Jarkko Miettinen
 modified ~3 lines of the original C#-version 
 by Isaac Gouy
 */
 
import java.text.DecimalFormat;
import java.text.NumberFormat; 

public class spectralnorm
{
	
	private static final NumberFormat nf = new DecimalFormat("#0.000000000");
	
	public static void main(String[] args) {
		int n = 100;
		if (args.length > 0) n = Integer.parseInt(args[0]);

		System.out.println(nf.format(new spectralnorm().Approximate(n)));
	}

	private final double Approximate(int n) {
		// create unit vector
		double[] u = new double[n];
		for (int i=0; i<n; i++) u[i] =  1;
		
		// 20 steps of the power method
		double[] v = new double[n];
		for (int i=0; i<n; i++) v[i] = 0;
		
		for (int i=0; i<10; i++) {
			MultiplyAtAv(n,u,v);
			MultiplyAtAv(n,v,u);
		}
		
		// B=AtA         A multiplied by A transposed
		// v.Bv /(v.v)   eigenvalue of v
		double vBv = 0, vv = 0;
		for (int i=0; i<n; i++) {
			vBv += u[i]*v[i];
			vv  += v[i]*v[i];
		}
		
		return Math.sqrt(vBv/vv);
	}
	
	
	/* return element i,j of infinite matrix A */
	private final double A(int i, int j){
		return 1.0/((i+j)*(i+j+1)/2 +i+1);
	}
	
	/* multiply vector v by matrix A */
	private final void MultiplyAv(int n, double[] v, double[] Av){
		for (int i=0; i<n; i++){
			Av[i] = 0;
			for (int j=0; j<n; j++) Av[i] += A(i,j)*v[j];
		}
	}
	
	/* multiply vector v by matrix A transposed */
	private final void MultiplyAtv(int n, double[] v, double[] Atv){
		for (int i=0;i<n;i++){
			Atv[i] = 0;
			for (int j=0; j<n; j++) Atv[i] += A(j,i)*v[j];
		}
	}
	
	/* multiply vector v by matrix A and then by matrix A transposed */
	private final void MultiplyAtAv(int n, double[] v, double[] AtAv){
		double[] u = new double[n];
		MultiplyAv(n,v,u);
		MultiplyAtv(n,u,AtAv);
	}
}