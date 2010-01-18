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
timing has been added so that the time without startup can be reported.
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

contributed by Luzius Meisser
based on a contribution by Keenan Tims
that was modified by Michael Barker
*/

public class chameneos {

   public enum Colour {
       RED, BLUE, YELLOW, FADED;

       public Colour complement(Colour other) {
           if (this == other) {
               return this;
           } else if (this == Colour.BLUE) {
               return other == Colour.RED ? Colour.YELLOW : Colour.RED;
           } else if (this == Colour.YELLOW) {
               return other == Colour.BLUE ? Colour.RED : Colour.BLUE;
           } else {
               return other == Colour.YELLOW ? Colour.BLUE : Colour.YELLOW;
           }
       }
   }

   public class Future<T> {

       private volatile T t;

       public T getItem() {
           while (t == null) {
               Thread.yield();
           }
           return t;
       }

       // no synchronization necessary as assignment is atomic
       public void setItem(T t) {
           this.t = t;
       }
   }

   class Creature extends Thread {

       private MeetingPlace mp;
       private Colour colour;
       private int met;

       public Creature(Colour initialColour, MeetingPlace mp) {
           this.colour = initialColour;
           this.mp = mp;
           this.met = 0;
       }

       public void run() {
           try {
               while (true) {
                   colour = mp.meet(colour);
                   met++;
               }
           } catch (InterruptedException e) {
               colour = Colour.FADED;
           }
       }

       public int getCreaturesMet() {
           return met;
       }

       public Colour getColour() {
           return colour;
       }

   }

   public class MeetingPlace {

       private int meetingsLeft;
       private Colour first = null;
       private Future<Colour> current;

       public MeetingPlace(int meetings) {
           this.meetingsLeft = meetings;
       }

       public Colour meet(Colour myColor) throws InterruptedException {
           Future<Colour> newColor;
           synchronized (this) {
               if (meetingsLeft == 0) {
                   throw new InterruptedException();
               } else {
                   if (first == null) {
                       first = myColor;
                       current = new Future<Colour>();
                   } else {
                       current.setItem(myColor.complement(first));
                       first = null;
                       meetingsLeft--;
                   }
                   newColor = current;
               }
           }
           return newColor.getItem();
       }

   }

   public static final Colour[] COLOURS = { Colour.BLUE, Colour.RED, Colour.YELLOW, Colour.BLUE };

   private MeetingPlace mp;
   private Creature[] creatures;

   public chameneos(int meetings) {
       this.mp = new MeetingPlace(meetings);
       this.creatures = new Creature[COLOURS.length];
   }

   public void run() throws InterruptedException {
       for (int i = 0; i < COLOURS.length; i++) {
           creatures[i] = new Creature(COLOURS[i], mp);
           creatures[i].start();
       }

       for (int i = 0; i < COLOURS.length; i++) {
           creatures[i].join();
       }
   }

   public void printResult() {
       int meetings = 0;
       for (int i = 0; i < COLOURS.length; i++) {
           meetings += creatures[i].getCreaturesMet();
           // System.out.println(creatures[i].getCreaturesMet() + ", " +
           // creatures[i].getColour());
       }
       System.out.println(meetings);
   }

   public static void main(String[] args) throws Exception {
       if (args.length < 1) {
           throw new IllegalArgumentException();
       } else {
//           long t0 = System.nanoTime();
           chameneos cham = new chameneos(Integer.parseInt(args[0]));
           cham.run();
           cham.printResult();
//           long t1 = System.nanoTime();
//           System.out.println((t1 - t0) / 1000000);
       }
   }
}
