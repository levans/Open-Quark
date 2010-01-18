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

   contributed by Mark C. Lewis
*/

import java.text.*;

public final class nbody {
   private static final NumberFormat nf = new DecimalFormat("#0.000000000");

	public static void main(String[] args) {
	   int n = Integer.parseInt(args[0]);
	   
		NBodySystem bodies = new NBodySystem();
		
		System.out.println(nf.format(bodies.energy()) );
		for (int i=0; i<n; ++i) {
		   bodies.advance(0.01);
		}
		System.out.println(nf.format(bodies.energy()) );		
	}
}


final class NBodySystem {		
	private Body[] bodies;
	   	  	
	public NBodySystem(){			
		bodies = new Body[]{
		      Body.sun(),		
		      Body.jupiter(),
		      Body.saturn(),
		      Body.uranus(),
		      Body.neptune()		      		      		      
		   };
		
		double px = 0.0;
		double py = 0.0;	
		double pz = 0.0;				
		for(int i=0; i < bodies.length; ++i) {			   		         					
			px += bodies[i].vx * bodies[i].mass;
			py += bodies[i].vy * bodies[i].mass;		
			pz += bodies[i].vz * bodies[i].mass;				
		}		
		bodies[0].offsetMomentum(px,py,pz);
	}
						   	
	public void advance(double dt) {
	   double dx, dy, dz, distance, mag;	
	
		for(int i=0; i < bodies.length; ++i) {
			for(int j=i+1; j < bodies.length; ++j) {	
				dx = bodies[i].x - bodies[j].x;
				dy = bodies[i].y - bodies[j].y;
				dz = bodies[i].z - bodies[j].z;
				
				distance = Math.sqrt(dx*dx + dy*dy + dz*dz);				   
				mag = dt / (distance * distance * distance);
				
				bodies[i].vx -= dx * bodies[j].mass * mag;
				bodies[i].vy -= dy * bodies[j].mass * mag;
				bodies[i].vz -= dz * bodies[j].mass * mag;
				
				bodies[j].vx += dx * bodies[i].mass * mag;
				bodies[j].vy += dy * bodies[i].mass * mag;
				bodies[j].vz += dz * bodies[i].mass * mag;
			}
		}		
		
		for(int i=0; i < bodies.length; ++i) {
			bodies[i].x += dt * bodies[i].vx;
			bodies[i].y += dt * bodies[i].vy;
			bodies[i].z += dt * bodies[i].vz;
		}		
	}			
	
	public double energy(){		
		double dx, dy, dz, distance;	
		double e = 0.0;		   
		
		for (int i=0; i < bodies.length; ++i) {
			e += 0.5 * bodies[i].mass * 
			   ( bodies[i].vx * bodies[i].vx 
			   + bodies[i].vy * bodies[i].vy 
			   + bodies[i].vz * bodies[i].vz );
			   
			for (int j=i+1; j < bodies.length; ++j) {
				dx = bodies[i].x - bodies[j].x;
				dy = bodies[i].y - bodies[j].y;
				dz = bodies[i].z - bodies[j].z;
				
				distance = Math.sqrt(dx*dx + dy*dy + dz*dz);
				e -= (bodies[i].mass * bodies[j].mass) / distance;
			}
		}
		return e;
	}		      	   	   	      	   		   														           		
}


final class Body {
   static final double PI = 3.141592653589793;	
   static final double SOLAR_MASS = 4 * PI * PI;
	static final double DAYS_PER_YEAR = 365.24;

	public double x, y, z, vx, vy, vz, mass;
	
	public Body(){}	
	
	static Body jupiter(){
	   Body p = new Body();
	   p.x = 4.84143144246472090e+00;
	   p.y = -1.16032004402742839e+00;
	   p.z = -1.03622044471123109e-01;
	   p.vx = 1.66007664274403694e-03 * DAYS_PER_YEAR;
	   p.vy = 7.69901118419740425e-03 * DAYS_PER_YEAR;
	   p.vz = -6.90460016972063023e-05 * DAYS_PER_YEAR;
	   p.mass = 9.54791938424326609e-04 * SOLAR_MASS;	   	   	   
	   return p;
	}	
	
	static Body saturn(){
	   Body p = new Body();
	   p.x = 8.34336671824457987e+00;
	   p.y = 4.12479856412430479e+00;
	   p.z = -4.03523417114321381e-01;
	   p.vx = -2.76742510726862411e-03 * DAYS_PER_YEAR;
	   p.vy = 4.99852801234917238e-03 * DAYS_PER_YEAR;
	   p.vz = 2.30417297573763929e-05 * DAYS_PER_YEAR;
	   p.mass = 2.85885980666130812e-04 * SOLAR_MASS;	   	   	   
	   return p;
	}	
	
	static Body uranus(){
	   Body p = new Body();
	   p.x = 1.28943695621391310e+01;
	   p.y = -1.51111514016986312e+01;
	   p.z = -2.23307578892655734e-01;
	   p.vx = 2.96460137564761618e-03 * DAYS_PER_YEAR;
	   p.vy = 2.37847173959480950e-03 * DAYS_PER_YEAR;
	   p.vz = -2.96589568540237556e-05 * DAYS_PER_YEAR;
	   p.mass = 4.36624404335156298e-05 * SOLAR_MASS;		   	   	   
	   return p;
	}		

	static Body neptune(){
	   Body p = new Body();
	   p.x = 1.53796971148509165e+01;
	   p.y = -2.59193146099879641e+01;
	   p.z = 1.79258772950371181e-01;
	   p.vx = 2.68067772490389322e-03 * DAYS_PER_YEAR;
	   p.vy = 1.62824170038242295e-03 * DAYS_PER_YEAR;
	   p.vz = -9.51592254519715870e-05 * DAYS_PER_YEAR;
	   p.mass = 5.15138902046611451e-05 * SOLAR_MASS;	   	   	   
	   return p;
	}
	
	static Body sun(){
	   Body p = new Body();
	   p.mass = SOLAR_MASS;	   	   	   
	   return p;
	}			
	
	Body offsetMomentum(double px, double py, double pz){
	   vx = -px / SOLAR_MASS;
	   vy = -py / SOLAR_MASS;
	   vz = -pz / SOLAR_MASS;	   
	   return this;   
	}			           			
}

