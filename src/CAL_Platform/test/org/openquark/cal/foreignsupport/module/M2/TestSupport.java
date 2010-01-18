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
 * TestSupport.java
 * Created: Feb 2, 2006
 * By: Bo Ilic
 */

package org.openquark.cal.foreignsupport.module.M2;

import java.io.IOException;
import java.util.Random;


/**
 * Ad-hoc holder of some helpful functions to support various test/demo code in the M2 module.
 * @author Bo Ilic
 */
public final class TestSupport {
    private TestSupport() {
    }
    
    /**
     * Collection of static helper functions and fields for working with the Java int array type.    
     * 
     * @author Bo Ilic
     */
    public static final class IntArray {
        
        private IntArray() {}              
        
        public static final void swap (int[] array, int index1, int index2) {
            int temp = array[index2];
            array[index2] = array[index1];
            array[index1] = temp;
        }          
               
    }

    /**
     * Version of QuickSort from Yann Le Biannic's wiki.     
     */
    public static class FastQuicksort {
        static final Random RND = new Random();      
     
        private static int partition(int[] array, int begin, int end) {
            int index = begin + RND.nextInt(end - begin + 1);
            int pivot = array[index];
            int tmp = array[index];
            array[index] = array[end];
            array[end] = tmp;
            for (int i = index = begin; i < end; ++ i) {
                if (array[i] <= pivot) {
                    tmp = array[index];
                    array[index++] = array[i];
                    array[i] = tmp;
                }
            }
            tmp = array[index];
            array[index] = array[end];
            array[end] = tmp;
            return (index);
        }
        private static void qsort(int[] array, int begin, int end) {
            if (end > begin) {
                int index = partition(array, begin, end);
                qsort(array, begin, index - 1);
                qsort(array, index + 1,  end);
            }
        }
        public static int[] sort(int[] array) {
            qsort(array, 0, array.length - 1);    
            return array;
        }
    }  
    
    /**
     * Prime number generator from Yann Le Biannic's wiki.     
     */    
    public static class QuickPrimeGenerator {
        
        static public int generate(int rank) {
            int [] primes = new int [rank+1];
            primes[0] = 3;
            primes[1] = 5;
            int candidate = 7;
            int i = 1; 
            while (i < rank) {
                int maxDivider = (int)java.lang.Math.sqrt(candidate);
                for (int j = 0; j <= i; ++j) {
                    int divider = primes[j];
                    if (candidate % divider == 0) {
                        break;
                    } else if (divider > maxDivider) {
                        // found a prime
                        primes[++i] = candidate;
                        break;
                    }
                }
                candidate += 2;
            }
            return primes[rank];
        }
    }    

    /**
     * A method that returns false always, but is declared to throw {@link IOException}.
     * This is a support method for a regression test for a bytecode generation bug
     * where invalid exception table entries were generated in some circumstances when
     * a foreign call is made inside a tail-recursive loop, and the foreign method
     * is declared to throw a checked exception.
     * 
     * See M2.exceptionHandlerCodeGenTest for details.
     * 
     * @return false always.
     * @throws IOException never thrown, just declared.
     */
    public static final boolean falseWithThrowsDecl() throws IOException {
        // We never throw the exception...
        if (false) {
            throw new IOException();
        }
        // ...we always return false
        return false;
    }
    
    /**
     * A package-scoped class declaring public methods that are not overriden by its
     * subclass (which is public). This is part of a regression test for foreign functions:
     * 
     * It is sometimes necessary to invoke a method/field from a class other than which it was defined.
     * For example, if package scope class A defines a static public field f, and public class B extends A, 
     * then B.f in a different package will not result in a compilation error but A.f will.
     * 
     * Or for example, if package scope class A defines a non-static public method m, and public class B extends A, 
     * then in a different package we cannot invoke m on an object of type B if:
     * - the invocation is done via reflection, or
     * - the reference is first cast to the method's declared type, in this case A, i.e. ((A)b).m()
     *
     * @author Joseph Wong
     */
    static class PackageScopedBaseClass {
        public final int fortyTwo() {
            return 42;
        }
        public static final int sixtyFour() {
            return 64;
        }
        public final double threePointTwo = 3.2;
        
        public static final double SIX_POINT_FOUR = 6.4;
    }
    
    /**
     * A public interface declaring a public method. This is to test foreign functions where the CAL argument type
     * for 'this' is an interface type.
     *
     * @author Joseph Wong
     */
    public static interface PublicInterface {
        public int nine();
    }
    
    /**
     * A public subclass of a package-scoped superclass declaring public methods. This is
     * part of a regression test for foreign functions.
     *
     * @author Joseph Wong
     */
    public static final class PublicSubClass extends PackageScopedBaseClass implements PublicInterface {
        public final int nine() { return 9; }
    }
}
