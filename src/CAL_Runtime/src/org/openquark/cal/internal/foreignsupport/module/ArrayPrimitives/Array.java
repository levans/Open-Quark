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
 * Array.java
 * Created: Sep 28, 2005
 * By: Bo Ilic
 */

package org.openquark.cal.internal.foreignsupport.module.ArrayPrimitives;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.List;
import java.util.RandomAccess;

import org.openquark.cal.runtime.CalValue;
import org.openquark.util.ByteArrays;


/**
 * A collection of helpers for the Array module.
 *  
 * @author Bo Ilic
 */
public final class Array {
    
    private Array(){}
    
    /**
     * The CharArray subclass is hand-written. The other subclasses are largely boiler-plated via running this main method.
     * 
     * @param args
     */
    public static void main(String[] args) {
        
        System.out.println("Array.main inFileName outFileName");
        System.out.println("Utility to boilerplate char functions to the other primitive types and Object.");
        
        if (args == null || args.length != 2) {
            throw new IllegalArgumentException("invalid number of command-line arguments.");            
        }

        String inputFileName = args[0];
        File inputFile = new File(inputFileName);
        if (!inputFile.exists()) {
            throw new IllegalArgumentException("invalid input file name " + inputFileName);
        }
        
        String inputString = readFile(inputFile);
        
        String boilerplate = createBoilerplate(inputString);
                      
        String outputFileName = args[1];                       
        File outputFile = new File(outputFileName);
        
        writeFile(outputFile, boilerplate);                 
    }
    
    private static String createBoilerplate(String charBasedCode) {
        
        String[] javaBoxedNames = {"Character", "Boolean", "Byte", "Short", "Integer", "Long", "Float", "Double", "Object", "CalValue"};
        String[] javaUnboxedNames = {"char", "boolean", "byte", "short", "int", "long", "float", "double", "Object", "CalValue"};
        
        String heading = "\n    ////////////////////////////////////////////////////////////////////////////////////\n";
        StringBuilder result = new StringBuilder(heading).append("    //BEGIN AUTOMATICALLY GENERATED CODE- DO NOT MODIFY\n\n");
        
        //start at 1 to skip "char".
        for (int i = 1; i < javaUnboxedNames.length; ++i) {
            String typeName = javaUnboxedNames[i];
            char firstChar = typeName.charAt(0);
            String properTypeName = typeName.replace(firstChar, Character.toUpperCase(firstChar));
            String typeBasedCode =
                charBasedCode.replaceAll("Character", javaBoxedNames[i])
                    .replaceAll("char", typeName)
                    .replaceAll("Char", properTypeName);
            result.append(typeBasedCode).append('\n');
        }
        
        result.append("\n    //END AUTOMATICALLY GENERATED CODE").append(heading).append("\n\n");
        return result.toString();
        
    }
    
    private static String readFile (File fileName) {
        
        try {
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            StringBuilder resultString = new StringBuilder();
            
            try {
                int c;
                while ((c = in.read()) != -1) {
                    resultString.append((char)c);
                }
            } finally {
                in.close();
            }
            return resultString.toString();
            
        } catch (IOException e) {
            throw new RuntimeException("unable to read file " + fileName);
        }  
    }
    
    private static void writeFile (File fileName, String contents) {
        try {
            FileWriter writer = new FileWriter(fileName);
            try {
                writer.write(contents);
            } finally {
                writer.close();
            }
            
        } catch (IOException e) {
            throw new RuntimeException("unable to write file " + fileName);
        }        
    }
    
    /**
     * Non-boilerplated sort for boolean arrays. Java doesn't consider boolean to be orderable,
     * but in CAL, false < true.
     */
    static private boolean[] sortBooleanArray(boolean[] array) {
        int len = array.length;
        if (len == 0) {
            return array;
        }
        
        int nFalse = 0;
        for (int i = 0; i < len; ++i) {
            if (!array[i]) {
                ++nFalse;
            }
        }
        
        boolean[] result = new boolean[len]; //by default, initialized to all false.
        Arrays.fill(result, nFalse, len, true);
        return result;
    } 
    
    private static int compareInt(int x, int y) {
        return x < y ? -1 : (x == y ? 0 : 1); 
    }    
    
    /**
     * Collection of static helper functions and fields for working with empty arrays.    
     * 
     * Note, that this class is primarily intended to support the Array.cal module. In particular, most
     * of the operations below do not do destructive updates but rather make copies. Also, we can assume (in
     * most cases) that the array is not a null array.
     * 
     * @author Bo Ilic
     * @author Joseph Wong
     */
    public static final class EmptyArray {
        
        private EmptyArray() {}
    
        public static final void throwArrayIndexOutOfBoundsException(int index) {
            throw new ArrayIndexOutOfBoundsException(index);
        } 
        
        /**         
         * @param fromIndex inclusive 0-based index
         * @param toIndex exclusive 0-based index
         * @return true if the subarray is the empty array
         */
        public static boolean isSubArrayOK(int fromIndex, int toIndex) {
            
            int sourceArrayLength = 0;
            if (fromIndex < 0) {
                throw new IndexOutOfBoundsException("fromIndex(" + fromIndex + ") < 0.");
            }
            if (toIndex > sourceArrayLength) {
                throw new IndexOutOfBoundsException("toIndex(" + toIndex + ") > array.length(" + sourceArrayLength + ").");
            }
            if (fromIndex > toIndex) {
                throw new IllegalArgumentException("fromIndex(" + fromIndex + ") >  toIndex(" + toIndex + ").");
            }
            
            int newArrayLength = toIndex - fromIndex;
            return newArrayLength == 0;
        } 
        
        /** 
         * Efficiently removes a range of elements from an array.
         * The returned array has (toIndex - fromIndex) fewer elements. 
         *  
         * @param fromIndex inclusive index of the array to start removal from
         * @param toIndex exclusive index of the array to stop removal at
         * @return true if the array with indices from fromIndex to toIndex removed is the empty array
         */
        public static boolean isRemoveRangeOK(int fromIndex, int toIndex) {
            int sourceArrayLength = 0;
            
            if (fromIndex < 0) {
                throw new IndexOutOfBoundsException("fromIndex(" + fromIndex + ") < 0.");
            }
            if (toIndex > sourceArrayLength) {
                throw new IndexOutOfBoundsException("toIndex(" + toIndex + ") > array.length(" + sourceArrayLength + ").");
            }            
            if (fromIndex > toIndex) {
                throw new IllegalArgumentException("fromIndex(" + fromIndex + ") >  toIndex(" + toIndex + ").");
            }
            
            int nElementsRemoved = toIndex - fromIndex;
            return nElementsRemoved == 0;
        }
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////    
    /*
     * IMPORTANT: the char array primitives are hand written. The others are a copy-paste where 
     * char/Char/Character are replaced with int/Int/Integer etc. respectively.
     * Run the main method of the Array class to get the boilerplate generated.
     *      
     * In a few cases manual modification is needed for the particular types. Use a diff tool to see
     * where...
     */ 
    
    /**
     * Collection of static helper functions and fields for working with the Java char array type.    
     * 
     * Note, that this class is primarily intended to support the Array.cal module. In particular, most
     * of the operations below do not do destructive updates but rather make copies. Also, we can assume (in
     * most cases) that the array is not a null array.
     * 
     * @author Bo Ilic
     */
    public static final class CharArray {
        
        private CharArray() {}
    
        public static final char[] empty = new char[0];
                      
        /**         
         * @param array
         * @return representation of the array for debugging purposes.
         */
        public static final String toString(char[] array) {
            if (array == null) {             
                return "null";
            }
            
            int len = array.length;
            if (len == 0) {
                return "[]";
            }
 
            StringBuilder result = new StringBuilder("[");           
            result.append(array[0]); 
            for (int i = 1; i < len; ++i) {
                result.append(", ");
                result.append(array[i]);
            }
            result.append("]");
            return result.toString();
        }              
        
        public static final char[] array1(char value) {
            return new char[] {value};
        }        
        
        public static final char[] array2(char value1, char value2) {
            return new char[] {value1, value2};
        }
        
        public static final char[] array3(char value1, char value2, char value3) {
            return new char[] {value1, value2, value3};
        }
        
        public static final char[] array4(char value1, char value2, char value3, char value4) {
            return new char[] {value1, value2, value3, value4};
        }
        
        public static final char[] array5(char value1, char value2, char value3, char value4, char value5) {
            return new char[] {value1, value2, value3, value4, value5};
        }
        
        public static final char[] array6(char value1, char value2, char value3, char value4, char value5, char value6) {
            return new char[] {value1, value2, value3, value4, value5, value6};
        }
        
        public static final char[] array7(char value1, char value2, char value3, char value4, char value5, char value6, char value7) {
            return new char[] {value1, value2, value3, value4, value5, value6, value7};
        }                                                       
        
        /**
         * Clones the array, replacing a null array with a zero-length array.
         * @param array may be null
         * @return will not be null
         */
        public static final char[] cloneReplacingNullArray (char[] array) {
            if (array == null) {
                return CharArray.empty;
            }
            return array.clone();
        }              
        
        public static final char[] listToArray(List<Character> list) {
            int size = list.size();
            if (size == 0) {
                return CharArray.empty;
            }
            
            char[] resultArray = new char[size];
            
            for (int i = 0; i < size; ++i) {
                resultArray[i] = list.get(i).charValue();
            }
            
            return resultArray;
        }
        
        /**
         * @param firstElement the first element of the array to be constructed.
         * @param list the remainder of the elements of the array.
         * @return the array.
         */
        public static final char[] listToArrayWithFirstElement(char firstElement, List<Character> list) {
            int size = list.size() + 1;
            
            char[] resultArray = new char[size];
            
            resultArray[0] = firstElement;
            
            for (int i = 1; i < size; ++i) {
                resultArray[i] = list.get(i-1).charValue();
            }
            
            return resultArray;
        }
        
        /**     
         * @param array
         * @return the returned List is backed by the char array and hence any modifications 
         *      to the char array will modify the list.
         */
        public static final List<Character> arrayToList(final char[] array) {
                  
            class CharList extends AbstractList<Character> implements RandomAccess {
                          
                @Override
                public Character get(int index) {
                    return Character.valueOf(array[index]);                  
                }
                    
                @Override
                public int size() {        
                    return array.length;
                }                           
            }
            
            return new CharList();        
        }    
        
        /** 
         * Note: in certain circumstances, this function does not return a copy of
         * the argument arrays, and hence should not be used in situations where
         * the argument arrays may be mutated.
         *         
         * @param array1
         * @param array2
         * @return char[] the concatenation of array1 with array2.
         *      the function return array2 if array1 is empty, and vice-versa.  
         * @throws NullPointerException if either array1 or array2 are null.
         */
        public static char[] concat(char[] array1, char[] array2) {
            
            int length1 = array1.length;
            if (length1 == 0)  {                
                return array2;
            } 
            
            final int length2 = array2.length;
            if (length2 == 0) {
                return array1;
            }
            
            final char[] resultArray = new char[length1 + length2];
            System.arraycopy(array1, 0, resultArray, 0, length1);
            System.arraycopy(array2, 0, resultArray, length1, length2);
            return resultArray;
        }
            
        /** 
         * Note: in certain circumstances, this function does not return a copy of
         * the argument arrays, and hence should not be used in situations where
         * the argument arrays may be mutated.
         *       
         * @param list of char[] Objects
         * @return concatenation of the char arrays in the List
         */
        public static char[] concatList(List<char[]> list) {            
            final int nArrays = list.size();
            
            switch (nArrays) {
            case 0:                          
                return CharArray.empty;
                
            case 1:                          
                return list.get(0);
                
            case 2:
                return concat(list.get(0), list.get(1));
                
            default:
                break;
            }
            
            int resultSize = 0;
            for (int i = 0; i < nArrays; ++i) {
                resultSize += list.get(i).length;
            }
            
            final char[] result = new char[resultSize];
            int copyStartPos = 0;
            for (int i = 0; i < nArrays; ++i) {
                final char[] currentcharArray = list.get(i);
                final int currentcharArrayLength = currentcharArray.length;
                System.arraycopy(currentcharArray, 0, result, copyStartPos, currentcharArrayLength);
                copyStartPos += currentcharArrayLength;
            }
            
            return result;
        }
        
        public static char[] reverse(char[] array) {
            int len = array.length;
            if (len == 0) {
                return CharArray.empty;
            }
            
            char[] resultArray = new char[len];            
            int lastIndex = len - 1;
            for (int i = 0; i < len; ++i) {
                resultArray[i] = array[lastIndex - i];
            }
            
            return resultArray;            
        }
        
        /**         
         * @param array
         * @param fromIndex inclusive 0-based index
         * @param toIndex exclusive 0-based index
         * @return copy of the array from fromIndex (inclusive) to toIndex (exclusive). 
         */
        public static char[] subArray(char[] array, int fromIndex, int toIndex) {
            
            int sourceArrayLength = array.length;
            if (fromIndex < 0) {
                throw new IndexOutOfBoundsException("fromIndex(" + fromIndex + ") < 0.");
            }
            if (toIndex > sourceArrayLength) {
                throw new IndexOutOfBoundsException("toIndex(" + toIndex + ") > array.length(" + sourceArrayLength + ").");
            }
            if (fromIndex > toIndex) {
                throw new IllegalArgumentException("fromIndex(" + fromIndex + ") >  toIndex(" + toIndex + ").");
            }
            
            int newArrayLength = toIndex - fromIndex;
            if (newArrayLength == 0) {
                return CharArray.empty;
            }
            
            if (newArrayLength == sourceArrayLength) {
                return array;
            }
            
            char[] newArray = new char[newArrayLength];
            System.arraycopy(array, fromIndex, newArray, 0, toIndex - fromIndex);
                      
            return newArray;
        } 
        
        /** 
         * Efficiently removes a range of elements from an array.
         * The returned array has (toIndex - fromIndex) fewer elements. 
         *  
         * @param array
         * @param fromIndex inclusive index of the array to start removal from
         * @param toIndex exclusive index of the array to stop removal at
         * @return a copy (if necessary) of array with indices from fromIndex to toIndex removed
         */
        public static char[] removeRange(char[] array, int fromIndex, int toIndex) {
            int sourceArrayLength = array.length;
            
            if (fromIndex < 0) {
                throw new IndexOutOfBoundsException("fromIndex(" + fromIndex + ") < 0.");
            }
            if (toIndex > sourceArrayLength) {
                throw new IndexOutOfBoundsException("toIndex(" + toIndex + ") > array.length(" + sourceArrayLength + ").");
            }            
            if (fromIndex > toIndex) {
                throw new IllegalArgumentException("fromIndex(" + fromIndex + ") >  toIndex(" + toIndex + ").");
            }
            
            int nElementsRemoved = toIndex - fromIndex;
            if (nElementsRemoved == 0) {
                return array;
            }
            
            if (nElementsRemoved == sourceArrayLength) {
                return CharArray.empty;
            }
                       
            char[] newArray = new char[sourceArrayLength - nElementsRemoved];
            if (fromIndex > 0) {
                System.arraycopy(array, 0, newArray, 0, fromIndex);
            }
            if (toIndex < sourceArrayLength) {
                System.arraycopy(array, toIndex, newArray, fromIndex, sourceArrayLength - toIndex);
            }
                      
            return newArray;                                  
        }
        
        public static char[] replicate(int nCopies, char valueToReplicate) {
            if (nCopies <= 0) {
                return CharArray.empty;
            }
            
            char[] result = new char[nCopies];
            Arrays.fill(result, valueToReplicate);
            
            return result;
        }
        
        /**        
         * Support for defining the Eq a => Eq (Array a) instance when a is in fact Char. This relies on the 
         * fact that the implementation of Eq Char is just java primitive char equality.
         */
        public static boolean equals(char[] array1, char[] array2) {
            return Arrays.equals(array1, array2);
        }
                    
        public static boolean notEquals(char[] array1, char[] array2) {
            return !equals(array1, array2);
        }
        
        private static int compareElem(char x, char y) {
            return x < y ? -1 : (x == y ? 0 : 1); 
        }
        
        /**
         * Support for defining the Ord a => Ord (Array a) instance when a is in fact Char. This relies on the 
         * fact that the implementation of Ord Char is just java primitive char comparison.
         * 
         * Comparison using lexicographic ordering with Ord Char used on elements.         
         * @param array1
         * @param array2
         * @return -1, 0, or 1.
         */
        public static int compare(char[] array1, char[] array2) {
            if (array1 == array2) {
                return 0;
            }
            
            int n = Math.min(array1.length, array2.length);
            
            for (int i = 0; i < n; ++i) {
                
                int elementCompare = compareElem(array1[i], array2[i]);
                if (elementCompare != 0) {
                    return elementCompare;
                }                                     
            }
            
            return compareInt(array1.length, array2.length);
        }        
        
        public static boolean lessThan(char[] array1, char[] array2) {
            return compare(array1, array2) < 0;
        }
        
        public static boolean lessThanEquals(char[] array1, char[] array2) {
            return compare(array1, array2) <= 0;
        }
        
        public static boolean greaterThanEquals(char[] array1, char[] array2) {
            return compare(array1, array2) >= 0;
        }
        
        public static boolean greaterThan(char[] array1, char[] array2) {
            return compare(array1, array2) > 0;
        }        
                                       
        public static char[] max(char[] array1, char[] array2) {
            return lessThanEquals(array1, array2) ? array2 : array1;                       
        }
        
        public static char[] min(char[] array1, char[] array2) {
            return lessThanEquals(array1, array2) ? array1 : array2;
        }                
        
        /** 
         * Note: this method relies on the fact that we know that Eq Char is java primitive comparison on chars.
         *    
         * @param array array to search for element in.     
         * @param element char to search for.
         * @return the (0-based) index of the first occurrence of element in the array or -1 if element does not occur.
         */
        public static int indexOf(char[] array, char element) {
            return indexOf(array, element, 0);           
        }

        /**         
         * @param array array to search for element in.
         * @param element char to search for.
         * @param fromIndex the (0-based) index to inclusively start the search from. There are no invalid values of fromIndex.
         *      If fromIndex < 0, 0 is assumed. If fromIndex >= length.array -1 will be returned.
         * @return the (0-based) index of the first occurrence of element in the array with index >= fromIndex or -1 if 
         *      element does not occur.
         */
        public static int indexOf(char[] array, char element, int fromIndex) {
                       
            for (int i = (fromIndex < 0 ? 0 : fromIndex), len = array.length; i < len; ++i) {
                
                if (array[i] == element) {
                    return i;
                }
            }
            return -1;
        }
        
        public static int lastIndexOf(char[] array, char element) {
            return lastIndexOf(array, element, array.length - 1);            
        }
        
        /**                
         * @param array array to search for element in.
         * @param element char to search for.
         * @param fromIndex There are no invalid values for fromIndex. If fromIndex < 0, then -1 is always returned.
         * @return the 0-based index of the last occurrence of element within array whose index is less than or equal to fromIndex
         *     or -1 if element does not occur.
         */
        public static int lastIndexOf(char[] array, char element, int fromIndex) {              
            if (fromIndex < 0) {
                return -1;
            }
                                               
            int actualFromIndex = Math.min(fromIndex, array.length -1);
            
            for (int i = actualFromIndex; i >= 0; --i) {
                if (array[i] == element) {
                    return i;
                }
            }
            
            return -1;
        }
        
        public static char[] replace(char[] array, char oldElementValue, char newElementValue) {
            
            if (oldElementValue == newElementValue) {
                return array;
            }
            
            int len = array.length;
            if (len == 0) {
                return CharArray.empty;
            }
            
            char[] newArray = new char[len];
            
            for (int i = 0; i < len; ++i) {
                
                char currentVal = array[i];                
                newArray[i] = (currentVal == oldElementValue) ? newElementValue : currentVal;                
            }
            
            return newArray;
        }        
        
        public static char[] sort(char[] array) {
            if (array.length == 0) {
                return array;
            }
            
            char[] newArray = array.clone();            
            Arrays.sort(newArray);
            return newArray;
        }        
               
    }
    

    ////////////////////////////////////////////////////////////////////////////////////
    //BEGIN AUTOMATICALLY GENERATED CODE- DO NOT MODIFY

    
    /**
     * Collection of static helper functions and fields for working with the Java boolean array type.    
     * 
     * Note, that this class is primarily intended to support the Array.cal module. In particular, most
     * of the operations below do not do destructive updates but rather make copies. Also, we can assume (in
     * most cases) that the array is not a null array.
     * 
     * @author Bo Ilic
     */
    public static final class BooleanArray {
        
        private BooleanArray() {}
    
        public static final boolean[] empty = new boolean[0];
                      
        /**         
         * @param array
         * @return representation of the array for debugging purposes.
         */
        public static final String toString(boolean[] array) {
            if (array == null) {             
                return "null";
            }
            
            int len = array.length;
            if (len == 0) {
                return "[]";
            }
 
            StringBuilder result = new StringBuilder("[");           
            result.append(array[0]); 
            for (int i = 1; i < len; ++i) {
                result.append(", ");
                result.append(array[i]);
            }
            result.append("]");
            return result.toString();
        }               
        
        public static final boolean[] array1(boolean value) {
            return new boolean[] {value};
        }        
        
        public static final boolean[] array2(boolean value1, boolean value2) {
            return new boolean[] {value1, value2};
        }
        
        public static final boolean[] array3(boolean value1, boolean value2, boolean value3) {
            return new boolean[] {value1, value2, value3};
        }
        
        public static final boolean[] array4(boolean value1, boolean value2, boolean value3, boolean value4) {
            return new boolean[] {value1, value2, value3, value4};
        }
        
        public static final boolean[] array5(boolean value1, boolean value2, boolean value3, boolean value4, boolean value5) {
            return new boolean[] {value1, value2, value3, value4, value5};
        }
        
        public static final boolean[] array6(boolean value1, boolean value2, boolean value3, boolean value4, boolean value5, boolean value6) {
            return new boolean[] {value1, value2, value3, value4, value5, value6};
        }
        
        public static final boolean[] array7(boolean value1, boolean value2, boolean value3, boolean value4, boolean value5, boolean value6, boolean value7) {
            return new boolean[] {value1, value2, value3, value4, value5, value6, value7};
        }                                                          
        
        /**
         * Clones the array, replacing a null array with a zero-length array.
         * @param array may be null
         * @return will not be null
         */
        public static final boolean[] cloneReplacingNullArray (boolean[] array) {
            if (array == null) {
                return BooleanArray.empty;
            }
            return array.clone();
        }               
        
        public static final boolean[] listToArray(List<Boolean> list) {
            int size = list.size();
            if (size == 0) {
                return BooleanArray.empty;
            }
            
            boolean[] resultArray = new boolean[size];
            
            for (int i = 0; i < size; ++i) {
                resultArray[i] = list.get(i).booleanValue();
            }
            
            return resultArray;
        }
        
        /**
         * @param firstElement the first element of the array to be constructed.
         * @param list the remainder of the elements of the array.
         * @return the array.
         */
        public static final boolean[] listToArrayWithFirstElement(boolean firstElement, List<Boolean> list) {
            int size = list.size() + 1;
            
            boolean[] resultArray = new boolean[size];
            
            resultArray[0] = firstElement;
            
            for (int i = 1; i < size; ++i) {
                resultArray[i] = list.get(i-1).booleanValue();
            }
            
            return resultArray;
        }
        
        /**     
         * @param array
         * @return the returned List is backed by the boolean array and hence any modifications 
         *      to the boolean array will modify the list.
         */
        public static final List<Boolean> arrayToList(final boolean[] array) {
                  
            class BooleanList extends AbstractList<Boolean> implements RandomAccess {
                          
                @Override
                public Boolean get(int index) {
                    return Boolean.valueOf(array[index]);                  
                }
                    
                @Override
                public int size() {        
                    return array.length;
                }                           
            }
            
            return new BooleanList();        
        }    
        
        /** 
         * Note: in certain circumstances, this function does not return a copy of
         * the argument arrays, and hence should not be used in situations where
         * the argument arrays may be mutated.
         *         
         * @param array1
         * @param array2
         * @return boolean[] the concatenation of array1 with array2.
         *      the function return array2 if array1 is empty, and vice-versa.  
         * @throws NullPointerException if either array1 or array2 are null.
         */
        public static boolean[] concat(boolean[] array1, boolean[] array2) {
            
            int length1 = array1.length;
            if (length1 == 0)  {                
                return array2;
            } 
            
            final int length2 = array2.length;
            if (length2 == 0) {
                return array1;
            }
            
            final boolean[] resultArray = new boolean[length1 + length2];
            System.arraycopy(array1, 0, resultArray, 0, length1);
            System.arraycopy(array2, 0, resultArray, length1, length2);
            return resultArray;
        }
            
        /** 
         * Note: in certain circumstances, this function does not return a copy of
         * the argument arrays, and hence should not be used in situations where
         * the argument arrays may be mutated.
         *       
         * @param list of boolean[] Objects
         * @return concatenation of the boolean arrays in the List
         */
        public static boolean[] concatList(List<boolean[]> list) {            
            final int nArrays = list.size();
            
            switch (nArrays) {
            case 0:                          
                return BooleanArray.empty;
                
            case 1:                          
                return list.get(0);
                
            case 2:
                return concat(list.get(0), list.get(1));
                
            default:
                break;
            }
            
            int resultSize = 0;
            for (int i = 0; i < nArrays; ++i) {
                resultSize += list.get(i).length;
            }
            
            final boolean[] result = new boolean[resultSize];
            int copyStartPos = 0;
            for (int i = 0; i < nArrays; ++i) {
                final boolean[] currentbooleanArray = list.get(i);
                final int currentbooleanArrayLength = currentbooleanArray.length;
                System.arraycopy(currentbooleanArray, 0, result, copyStartPos, currentbooleanArrayLength);
                copyStartPos += currentbooleanArrayLength;
            }
            
            return result;
        }
        
        public static boolean[] reverse(boolean[] array) {
            int len = array.length;
            if (len == 0) {
                return BooleanArray.empty;
            }
            
            boolean[] resultArray = new boolean[len];            
            int lastIndex = len - 1;
            for (int i = 0; i < len; ++i) {
                resultArray[i] = array[lastIndex - i];
            }
            
            return resultArray;            
        }
        
        /**         
         * @param array
         * @param fromIndex inclusive 0-based index
         * @param toIndex exclusive 0-based index
         * @return copy of the array from fromIndex (inclusive) to toIndex (exclusive). 
         */
        public static boolean[] subArray(boolean[] array, int fromIndex, int toIndex) {
            
            int sourceArrayLength = array.length;
            if (fromIndex < 0) {
                throw new IndexOutOfBoundsException("fromIndex(" + fromIndex + ") < 0.");
            }
            if (toIndex > sourceArrayLength) {
                throw new IndexOutOfBoundsException("toIndex(" + toIndex + ") > array.length(" + sourceArrayLength + ").");
            }
            if (fromIndex > toIndex) {
                throw new IllegalArgumentException("fromIndex(" + fromIndex + ") >  toIndex(" + toIndex + ").");
            }
            
            int newArrayLength = toIndex - fromIndex;
            if (newArrayLength == 0) {
                return BooleanArray.empty;
            }
            
            if (newArrayLength == sourceArrayLength) {
                return array;
            }
            
            boolean[] newArray = new boolean[newArrayLength];
            System.arraycopy(array, fromIndex, newArray, 0, toIndex - fromIndex);
                      
            return newArray;
        } 
        
        /** 
         * Efficiently removes a range of elements from an array.
         * The returned array has (toIndex - fromIndex) fewer elements. 
         *  
         * @param array
         * @param fromIndex inclusive index of the array to start removal from
         * @param toIndex exclusive index of the array to stop removal at
         * @return a copy (if necessary) of array with indices from fromIndex to toIndex removed
         */
        public static boolean[] removeRange(boolean[] array, int fromIndex, int toIndex) {
            int sourceArrayLength = array.length;
            
            if (fromIndex < 0) {
                throw new IndexOutOfBoundsException("fromIndex(" + fromIndex + ") < 0.");
            }
            if (toIndex > sourceArrayLength) {
                throw new IndexOutOfBoundsException("toIndex(" + toIndex + ") > array.length(" + sourceArrayLength + ").");
            }            
            if (fromIndex > toIndex) {
                throw new IllegalArgumentException("fromIndex(" + fromIndex + ") >  toIndex(" + toIndex + ").");
            }
            
            int nElementsRemoved = toIndex - fromIndex;
            if (nElementsRemoved == 0) {
                return array;
            }
            
            if (nElementsRemoved == sourceArrayLength) {
                return BooleanArray.empty;
            }
                       
            boolean[] newArray = new boolean[sourceArrayLength - nElementsRemoved];
            if (fromIndex > 0) {
                System.arraycopy(array, 0, newArray, 0, fromIndex);
            }
            if (toIndex < sourceArrayLength) {
                System.arraycopy(array, toIndex, newArray, fromIndex, sourceArrayLength - toIndex);
            }
                      
            return newArray;                                  
        }
        
        public static boolean[] replicate(int nCopies, boolean valueToReplicate) {
            if (nCopies <= 0) {
                return BooleanArray.empty;
            }
            
            boolean[] result = new boolean[nCopies];
            Arrays.fill(result, valueToReplicate);
            
            return result;
        }
        
        /**        
         * Support for defining the Eq a => Eq (Array a) instance when a is in fact Boolean. This relies on the 
         * fact that the implementation of Eq Boolean is just java primitive boolean equality.
         */
        public static boolean equals(boolean[] array1, boolean[] array2) {
            return Arrays.equals(array1, array2);
        }
                    
        public static boolean notEquals(boolean[] array1, boolean[] array2) {
            return !equals(array1, array2);
        }
        
        private static int compareElem(boolean x, boolean y) {
            //false < true in CAL, but Java does not define an ordering on booleans.
            return x ? (y ? 0 : 1) : (y ? -1 : 0);
        }
        
        /**
         * Support for defining the Ord a => Ord (Array a) instance when a is in fact Boolean. This relies on the 
         * fact that the implementation of Ord Boolean is just java primitive boolean comparison.
         * 
         * Comparison using lexicographic ordering with Ord Boolean used on elements.         
         * @param array1
         * @param array2
         * @return -1, 0, or 1.
         */
        public static int compare(boolean[] array1, boolean[] array2) {
            if (array1 == array2) {
                return 0;
            }
            
            int n = Math.min(array1.length, array2.length);
            
            for (int i = 0; i < n; ++i) {
                
                int elementCompare = compareElem(array1[i], array2[i]);
                if (elementCompare != 0) {
                    return elementCompare;
                }                                     
            }
            
            return compareInt(array1.length, array2.length);
        }        
        
        public static boolean lessThan(boolean[] array1, boolean[] array2) {
            return compare(array1, array2) < 0;
        }
        
        public static boolean lessThanEquals(boolean[] array1, boolean[] array2) {
            return compare(array1, array2) <= 0;
        }
        
        public static boolean greaterThanEquals(boolean[] array1, boolean[] array2) {
            return compare(array1, array2) >= 0;
        }
        
        public static boolean greaterThan(boolean[] array1, boolean[] array2) {
            return compare(array1, array2) > 0;
        }        
                                       
        public static boolean[] max(boolean[] array1, boolean[] array2) {
            return lessThanEquals(array1, array2) ? array2 : array1;                       
        }
        
        public static boolean[] min(boolean[] array1, boolean[] array2) {
            return lessThanEquals(array1, array2) ? array1 : array2;
        }                
        
        /** 
         * Note: this method relies on the fact that we know that Eq Boolean is java primitive comparison on booleans.
         *    
         * @param array array to search for element in.     
         * @param element boolean to search for.
         * @return the (0-based) index of the first occurrence of element in the array or -1 if element does not occur.
         */
        public static int indexOf(boolean[] array, boolean element) {
            return indexOf(array, element, 0);           
        }
        
        /**         
         * @param array array to search for element in.
         * @param element boolean to search for.
         * @param fromIndex the (0-based) index to inclusively start the search from. There are no invalid values of fromIndex.
         *      If fromIndex < 0, 0 is assumed. If fromIndex >= length.array -1 will be returned.
         * @return the (0-based) index of the first occurrence of element in the array with index >= fromIndex or -1 if 
         *      element does not occur.
         */
        public static int indexOf(boolean[] array, boolean element, int fromIndex) {
                       
            for (int i = (fromIndex < 0 ? 0 : fromIndex), len = array.length; i < len; ++i) {
                
                if (array[i] == element) {
                    return i;
                }
            }
            return -1;
        }
        
        public static int lastIndexOf(boolean[] array, boolean element) {
            return lastIndexOf(array, element, array.length - 1);            
        }
        
        /**                
         * @param array array to search for element in.
         * @param element boolean to search for.
         * @param fromIndex There are no invalid values for fromIndex. If fromIndex < 0, then -1 is always returned.
         * @return the 0-based index of the last occurrence of element within array whose index is less than or equal to fromIndex
         *     or -1 if element does not occur.
         */
        public static int lastIndexOf(boolean[] array, boolean element, int fromIndex) {              
            if (fromIndex < 0) {
                return -1;
            }
                                               
            int actualFromIndex = Math.min(fromIndex, array.length -1);
            
            for (int i = actualFromIndex; i >= 0; --i) {
                if (array[i] == element) {
                    return i;
                }
            }
            
            return -1;
        }
        
        public static boolean[] replace(boolean[] array, boolean oldElementValue, boolean newElementValue) {
            
            if (oldElementValue == newElementValue) {
                return array;
            }
            
            int len = array.length;
            if (len == 0) {
                return BooleanArray.empty;
            }
            
            boolean[] newArray = new boolean[len];
            
            for (int i = 0; i < len; ++i) {
                
                boolean currentVal = array[i];                
                newArray[i] = (currentVal == oldElementValue) ? newElementValue : currentVal;                
            }
            
            return newArray;
        }        
        
        public static boolean[] sort(boolean[] array) {
            return Array.sortBooleanArray(array);         
        }        
               
    }
    
    
    /**
     * Collection of static helper functions and fields for working with the Java byte array type.    
     * 
     * Note, that this class is primarily intended to support the Array.cal module. In particular, most
     * of the operations below do not do destructive updates but rather make copies. Also, we can assume (in
     * most cases) that the array is not a null array.
     * 
     * @author Bo Ilic
     */
    public static final class ByteArray {
        
        private ByteArray() {}
    
        public static final byte[] empty = new byte[0];                   
        
        /**         
         * @param array
         * @return representation of the array for debugging purposes.
         */
        public static final String toString(byte[] array) {
            if (array == null) {             
                return "null";
            }
            
            int len = array.length;
            if (len == 0) {
                return "[]";
            }
 
            StringBuilder result = new StringBuilder("[");           
            result.append(array[0]); 
            for (int i = 1; i < len; ++i) {
                result.append(", ");
                result.append(array[i]);
            }
            result.append("]");
            return result.toString();
        }              
        
        public static final byte[] array1(byte value) {
            return new byte[] {value};
        }        
        
        public static final byte[] array2(byte value1, byte value2) {
            return new byte[] {value1, value2};
        }
        
        public static final byte[] array3(byte value1, byte value2, byte value3) {
            return new byte[] {value1, value2, value3};
        }
        
        public static final byte[] array4(byte value1, byte value2, byte value3, byte value4) {
            return new byte[] {value1, value2, value3, value4};
        }
        
        public static final byte[] array5(byte value1, byte value2, byte value3, byte value4, byte value5) {
            return new byte[] {value1, value2, value3, value4, value5};
        }
        
        public static final byte[] array6(byte value1, byte value2, byte value3, byte value4, byte value5, byte value6) {
            return new byte[] {value1, value2, value3, value4, value5, value6};
        }
        
        public static final byte[] array7(byte value1, byte value2, byte value3, byte value4, byte value5, byte value6, byte value7) {
            return new byte[] {value1, value2, value3, value4, value5, value6, value7};
        }                                            
        
        /**
         * Clones the array, replacing a null array with a zero-length array.
         * @param array may be null
         * @return will not be null
         */
        public static final byte[] cloneReplacingNullArray (byte[] array) {
            if (array == null) {
                return ByteArray.empty;
            }
            return array.clone();
        }              
        
        public static final byte[] listToArray(List<Byte> list) {
            int size = list.size();
            if (size == 0) {
                return ByteArray.empty;
            }
            
            byte[] resultArray = new byte[size];
            
            for (int i = 0; i < size; ++i) {
                resultArray[i] = list.get(i).byteValue();
            }
            
            return resultArray;
        }
        
        /**
         * @param firstElement the first element of the array to be constructed.
         * @param list the remainder of the elements of the array.
         * @return the array.
         */
        public static final byte[] listToArrayWithFirstElement(byte firstElement, List<Byte> list) {
            int size = list.size() + 1;
            
            byte[] resultArray = new byte[size];
            
            resultArray[0] = firstElement;
            
            for (int i = 1; i < size; ++i) {
                resultArray[i] = list.get(i-1).byteValue();
            }
            
            return resultArray;
        }

        /**     
         * @param array
         * @return the returned List is backed by the byte array and hence any modifications 
         *      to the byte array will modify the list.
         */
        public static final List<Byte> arrayToList(final byte[] array) {
                  
            class ByteList extends AbstractList<Byte> implements RandomAccess {
                          
                @Override
                public Byte get(int index) {
                    return Byte.valueOf(array[index]);                  
                }
                    
                @Override
                public int size() {        
                    return array.length;
                }                           
            }
            
            return new ByteList();        
        }    
        
        /** 
         * Note: in certain circumstances, this function does not return a copy of
         * the argument arrays, and hence should not be used in situations where
         * the argument arrays may be mutated.
         *         
         * @param array1
         * @param array2
         * @return byte[] the concatenation of array1 with array2.
         *      the function return array2 if array1 is empty, and vice-versa.  
         * @throws NullPointerException if either array1 or array2 are null.
         */
        public static byte[] concat(byte[] array1, byte[] array2) {
            
            int length1 = array1.length;
            if (length1 == 0)  {                
                return array2;
            } 
            
            final int length2 = array2.length;
            if (length2 == 0) {
                return array1;
            }
            
            final byte[] resultArray = new byte[length1 + length2];
            System.arraycopy(array1, 0, resultArray, 0, length1);
            System.arraycopy(array2, 0, resultArray, length1, length2);
            return resultArray;
        }
            
        /** 
         * Note: in certain circumstances, this function does not return a copy of
         * the argument arrays, and hence should not be used in situations where
         * the argument arrays may be mutated.
         *       
         * @param list of byte[] Objects
         * @return concatenation of the byte arrays in the List
         */
        public static byte[] concatList(List<byte[]> list) {            
            final int nArrays = list.size();
            
            switch (nArrays) {
            case 0:                          
                return ByteArray.empty;
                
            case 1:                          
                return list.get(0);
                
            case 2:
                return concat(list.get(0), list.get(1));
                
            default:
                break;
            }
            
            int resultSize = 0;
            for (int i = 0; i < nArrays; ++i) {
                resultSize += list.get(i).length;
            }
            
            final byte[] result = new byte[resultSize];
            int copyStartPos = 0;
            for (int i = 0; i < nArrays; ++i) {
                final byte[] currentbyteArray = list.get(i);
                final int currentbyteArrayLength = currentbyteArray.length;
                System.arraycopy(currentbyteArray, 0, result, copyStartPos, currentbyteArrayLength);
                copyStartPos += currentbyteArrayLength;
            }
            
            return result;
        }
        
        public static byte[] reverse(byte[] array) {
            int len = array.length;
            if (len == 0) {
                return ByteArray.empty;
            }
            
            byte[] resultArray = new byte[len];            
            int lastIndex = len - 1;
            for (int i = 0; i < len; ++i) {
                resultArray[i] = array[lastIndex - i];
            }
            
            return resultArray;            
        }
        
        /**         
         * @param array
         * @param fromIndex inclusive 0-based index
         * @param toIndex exclusive 0-based index
         * @return copy of the array from fromIndex (inclusive) to toIndex (exclusive). 
         */
        public static byte[] subArray(byte[] array, int fromIndex, int toIndex) {
            
            int sourceArrayLength = array.length;
            if (fromIndex < 0) {
                throw new IndexOutOfBoundsException("fromIndex(" + fromIndex + ") < 0.");
            }
            if (toIndex > sourceArrayLength) {
                throw new IndexOutOfBoundsException("toIndex(" + toIndex + ") > array.length(" + sourceArrayLength + ").");
            }
            if (fromIndex > toIndex) {
                throw new IllegalArgumentException("fromIndex(" + fromIndex + ") >  toIndex(" + toIndex + ").");
            }
            
            int newArrayLength = toIndex - fromIndex;
            if (newArrayLength == 0) {
                return ByteArray.empty;
            }
            
            if (newArrayLength == sourceArrayLength) {
                return array;
            }
            
            byte[] newArray = new byte[newArrayLength];
            System.arraycopy(array, fromIndex, newArray, 0, toIndex - fromIndex);
                      
            return newArray;
        } 
        
        /** 
         * Efficiently removes a range of elements from an array.
         * The returned array has (toIndex - fromIndex) fewer elements. 
         *  
         * @param array
         * @param fromIndex inclusive index of the array to start removal from
         * @param toIndex exclusive index of the array to stop removal at
         * @return a copy (if necessary) of array with indices from fromIndex to toIndex removed
         */
        public static byte[] removeRange(byte[] array, int fromIndex, int toIndex) {
            int sourceArrayLength = array.length;
            
            if (fromIndex < 0) {
                throw new IndexOutOfBoundsException("fromIndex(" + fromIndex + ") < 0.");
            }
            if (toIndex > sourceArrayLength) {
                throw new IndexOutOfBoundsException("toIndex(" + toIndex + ") > array.length(" + sourceArrayLength + ").");
            }            
            if (fromIndex > toIndex) {
                throw new IllegalArgumentException("fromIndex(" + fromIndex + ") >  toIndex(" + toIndex + ").");
            }
            
            int nElementsRemoved = toIndex - fromIndex;
            if (nElementsRemoved == 0) {
                return array;
            }
            
            if (nElementsRemoved == sourceArrayLength) {
                return ByteArray.empty;
            }
                       
            byte[] newArray = new byte[sourceArrayLength - nElementsRemoved];
            if (fromIndex > 0) {
                System.arraycopy(array, 0, newArray, 0, fromIndex);
            }
            if (toIndex < sourceArrayLength) {
                System.arraycopy(array, toIndex, newArray, fromIndex, sourceArrayLength - toIndex);
            }
                      
            return newArray;                                  
        }
        
        public static byte[] replicate(int nCopies, byte valueToReplicate) {
            if (nCopies <= 0) {
                return ByteArray.empty;
            }
            
            byte[] result = new byte[nCopies];
            Arrays.fill(result, valueToReplicate);
            
            return result;
        }
        
        /**        
         * Support for defining the Eq a => Eq (Array a) instance when a is in fact Byte. This relies on the 
         * fact that the implementation of Eq Byte is just java primitive byte equality.
         */
        public static boolean equals(byte[] array1, byte[] array2) {
            return Arrays.equals(array1, array2);
        }
                    
        public static boolean notEquals(byte[] array1, byte[] array2) {
            return !equals(array1, array2);
        }
        
        private static int compareElem(byte x, byte y) {
            return x < y ? -1 : (x == y ? 0 : 1);
        }            
        
        /**
         * Support for defining the Ord a => Ord (Array a) instance when a is in fact Byte. This relies on the 
         * fact that the implementation of Ord Byte is just java primitive byte comparison.
         * 
         * Comparison using lexicographic ordering with Ord Byte used on elements.         
         * @param array1
         * @param array2
         * @return -1, 0, or 1.
         */
        public static int compare(byte[] array1, byte[] array2) {
            if (array1 == array2) {
                return 0;
            }
            
            int n = Math.min(array1.length, array2.length);
            
            for (int i = 0; i < n; ++i) {
                
                int elementCompare = compareElem(array1[i], array2[i]);
                if (elementCompare != 0) {
                    return elementCompare;
                }                                     
            }
            
            return compareInt(array1.length, array2.length);
        }        
        
        public static boolean lessThan(byte[] array1, byte[] array2) {
            return compare(array1, array2) < 0;
        }
        
        public static boolean lessThanEquals(byte[] array1, byte[] array2) {
            return compare(array1, array2) <= 0;
        }
        
        public static boolean greaterThanEquals(byte[] array1, byte[] array2) {
            return compare(array1, array2) >= 0;
        }
        
        public static boolean greaterThan(byte[] array1, byte[] array2) {
            return compare(array1, array2) > 0;
        }        
                                       
        public static byte[] max(byte[] array1, byte[] array2) {
            return lessThanEquals(array1, array2) ? array2 : array1;                       
        }
        
        public static byte[] min(byte[] array1, byte[] array2) {
            return lessThanEquals(array1, array2) ? array1 : array2;
        }                
        
        /** 
         * Note: this method relies on the fact that we know that Eq Byte is java primitive comparison on bytes.
         *    
         * @param array array to search for element in.     
         * @param element byte to search for.
         * @return the (0-based) index of the first occurrence of element in the array or -1 if element does not occur.
         */
        public static int indexOf(byte[] array, byte element) {
            return indexOf(array, element, 0);           
        }
        
        /**         
         * @param array array to search for element in.
         * @param element byte to search for.
         * @param fromIndex the (0-based) index to inclusively start the search from. There are no invalid values of fromIndex.
         *      If fromIndex < 0, 0 is assumed. If fromIndex >= length.array -1 will be returned.
         * @return the (0-based) index of the first occurrence of element in the array with index >= fromIndex or -1 if 
         *      element does not occur.
         */
        public static int indexOf(byte[] array, byte element, int fromIndex) {
                       
            for (int i = (fromIndex < 0 ? 0 : fromIndex), len = array.length; i < len; ++i) {
                
                if (array[i] == element) {
                    return i;
                }
            }
            return -1;
        }
        
        public static int lastIndexOf(byte[] array, byte element) {
            return lastIndexOf(array, element, array.length - 1);            
        }
        
        /**                
         * @param array array to search for element in.
         * @param element byte to search for.
         * @param fromIndex There are no invalid values for fromIndex. If fromIndex < 0, then -1 is always returned.
         * @return the 0-based index of the last occurrence of element within array whose index is less than or equal to fromIndex
         *     or -1 if element does not occur.
         */
        public static int lastIndexOf(byte[] array, byte element, int fromIndex) {              
            if (fromIndex < 0) {
                return -1;
            }
                                               
            int actualFromIndex = Math.min(fromIndex, array.length -1);
            
            for (int i = actualFromIndex; i >= 0; --i) {
                if (array[i] == element) {
                    return i;
                }
            }
            
            return -1;
        }
        
        public static byte[] replace(byte[] array, byte oldElementValue, byte newElementValue) {
            
            if (oldElementValue == newElementValue) {
                return array;
            }
            
            int len = array.length;
            if (len == 0) {
                return ByteArray.empty;
            }
            
            byte[] newArray = new byte[len];
            
            for (int i = 0; i < len; ++i) {
                
                byte currentVal = array[i];                
                newArray[i] = (currentVal == oldElementValue) ? newElementValue : currentVal;                
            }
            
            return newArray;
        }        
        
        public static byte[] sort(byte[] array) {
            if (array.length == 0) {
                return array;
            }
            
            byte[] newArray = array.clone();            
            Arrays.sort(newArray);
            return newArray;
        }

        /**
         * Returns a compressed version of a byte array.
         */
        public static byte[] compress(byte[] byteArray) throws IOException {
            return ByteArrays.compressByteArray(byteArray);
        }

        /**
         * Returns a decompressed version of a byte array.
         */
        public static byte[] decompress(byte[] byteArray) throws IOException {
            return ByteArrays.decompressByteArray(byteArray);
        }
    }
    
    
    /**
     * Collection of static helper functions and fields for working with the Java short array type.    
     * 
     * Note, that this class is primarily intended to support the Array.cal module. In particular, most
     * of the operations below do not do destructive updates but rather make copies. Also, we can assume (in
     * most cases) that the array is not a null array.
     * 
     * @author Bo Ilic
     */
    public static final class ShortArray {
        
        private ShortArray() {}
    
        public static final short[] empty = new short[0];
                      
        /**         
         * @param array
         * @return representation of the array for debugging purposes.
         */
        public static final String toString(short[] array) {
            if (array == null) {             
                return "null";
            }
            
            int len = array.length;
            if (len == 0) {
                return "[]";
            }
 
            StringBuilder result = new StringBuilder("[");           
            result.append(array[0]); 
            for (int i = 1; i < len; ++i) {
                result.append(", ");
                result.append(array[i]);
            }
            result.append("]");
            return result.toString();
        }              
        
        public static final short[] array1(short value) {
            return new short[] {value};
        }        
        
        public static final short[] array2(short value1, short value2) {
            return new short[] {value1, value2};
        }
        
        public static final short[] array3(short value1, short value2, short value3) {
            return new short[] {value1, value2, value3};
        }
        
        public static final short[] array4(short value1, short value2, short value3, short value4) {
            return new short[] {value1, value2, value3, value4};
        }
        
        public static final short[] array5(short value1, short value2, short value3, short value4, short value5) {
            return new short[] {value1, value2, value3, value4, value5};
        }
        
        public static final short[] array6(short value1, short value2, short value3, short value4, short value5, short value6) {
            return new short[] {value1, value2, value3, value4, value5, value6};
        }
        
        public static final short[] array7(short value1, short value2, short value3, short value4, short value5, short value6, short value7) {
            return new short[] {value1, value2, value3, value4, value5, value6, value7};
        }                                              
        
        /**
         * Clones the array, replacing a null array with a zero-length array.
         * @param array may be null
         * @return will not be null
         */
        public static final short[] cloneReplacingNullArray (short[] array) {
            if (array == null) {
                return ShortArray.empty;
            }
            return array.clone();
        }               
        
        public static final short[] listToArray(List<Short> list) {
            int size = list.size();
            if (size == 0) {
                return ShortArray.empty;
            }
            
            short[] resultArray = new short[size];
            
            for (int i = 0; i < size; ++i) {
                resultArray[i] = list.get(i).shortValue();
            }
            
            return resultArray;
        }
        
        /**
         * @param firstElement the first element of the array to be constructed.
         * @param list the remainder of the elements of the array.
         * @return the array.
         */
        public static final short[] listToArrayWithFirstElement(short firstElement, List<Short> list) {
            int size = list.size() + 1;
            
            short[] resultArray = new short[size];
            
            resultArray[0] = firstElement;
            
            for (int i = 1; i < size; ++i) {
                resultArray[i] = list.get(i-1).shortValue();
            }
            
            return resultArray;
        }
        
        /**     
         * @param array
         * @return the returned List is backed by the short array and hence any modifications 
         *      to the short array will modify the list.
         */
        public static final List<Short> arrayToList(final short[] array) {
                  
            class ShortList extends AbstractList<Short> implements RandomAccess {
                          
                @Override
                public Short get(int index) {
                    return Short.valueOf(array[index]);                  
                }
                    
                @Override
                public int size() {        
                    return array.length;
                }                           
            }
            
            return new ShortList();        
        }    
        
        /** 
         * Note: in certain circumstances, this function does not return a copy of
         * the argument arrays, and hence should not be used in situations where
         * the argument arrays may be mutated.
         *         
         * @param array1
         * @param array2
         * @return short[] the concatenation of array1 with array2.
         *      the function return array2 if array1 is empty, and vice-versa.  
         * @throws NullPointerException if either array1 or array2 are null.
         */
        public static short[] concat(short[] array1, short[] array2) {
            
            int length1 = array1.length;
            if (length1 == 0)  {                
                return array2;
            } 
            
            final int length2 = array2.length;
            if (length2 == 0) {
                return array1;
            }
            
            final short[] resultArray = new short[length1 + length2];
            System.arraycopy(array1, 0, resultArray, 0, length1);
            System.arraycopy(array2, 0, resultArray, length1, length2);
            return resultArray;
        }
            
        /** 
         * Note: in certain circumstances, this function does not return a copy of
         * the argument arrays, and hence should not be used in situations where
         * the argument arrays may be mutated.
         *       
         * @param list of short[] Objects
         * @return concatenation of the short arrays in the List
         */
        public static short[] concatList(List<short[]> list) {            
            final int nArrays = list.size();
            
            switch (nArrays) {
            case 0:                          
                return ShortArray.empty;
                
            case 1:                          
                return list.get(0);
                
            case 2:
                return concat(list.get(0), list.get(1));
                
            default:
                break;
            }
            
            int resultSize = 0;
            for (int i = 0; i < nArrays; ++i) {
                resultSize += list.get(i).length;
            }
            
            final short[] result = new short[resultSize];
            int copyStartPos = 0;
            for (int i = 0; i < nArrays; ++i) {
                final short[] currentshortArray = list.get(i);
                final int currentshortArrayLength = currentshortArray.length;
                System.arraycopy(currentshortArray, 0, result, copyStartPos, currentshortArrayLength);
                copyStartPos += currentshortArrayLength;
            }
            
            return result;
        }
        
        public static short[] reverse(short[] array) {
            int len = array.length;
            if (len == 0) {
                return ShortArray.empty;
            }
            
            short[] resultArray = new short[len];            
            int lastIndex = len - 1;
            for (int i = 0; i < len; ++i) {
                resultArray[i] = array[lastIndex - i];
            }
            
            return resultArray;            
        }
        
        /**         
         * @param array
         * @param fromIndex inclusive 0-based index
         * @param toIndex exclusive 0-based index
         * @return copy of the array from fromIndex (inclusive) to toIndex (exclusive). 
         */
        public static short[] subArray(short[] array, int fromIndex, int toIndex) {
            
            int sourceArrayLength = array.length;
            if (fromIndex < 0) {
                throw new IndexOutOfBoundsException("fromIndex(" + fromIndex + ") < 0.");
            }
            if (toIndex > sourceArrayLength) {
                throw new IndexOutOfBoundsException("toIndex(" + toIndex + ") > array.length(" + sourceArrayLength + ").");
            }
            if (fromIndex > toIndex) {
                throw new IllegalArgumentException("fromIndex(" + fromIndex + ") >  toIndex(" + toIndex + ").");
            }
            
            int newArrayLength = toIndex - fromIndex;
            if (newArrayLength == 0) {
                return ShortArray.empty;
            }
            
            if (newArrayLength == sourceArrayLength) {
                return array;
            }
            
            short[] newArray = new short[newArrayLength];
            System.arraycopy(array, fromIndex, newArray, 0, toIndex - fromIndex);
                      
            return newArray;
        } 
        
        /** 
         * Efficiently removes a range of elements from an array.
         * The returned array has (toIndex - fromIndex) fewer elements. 
         *  
         * @param array
         * @param fromIndex inclusive index of the array to start removal from
         * @param toIndex exclusive index of the array to stop removal at
         * @return a copy (if necessary) of array with indices from fromIndex to toIndex removed
         */
        public static short[] removeRange(short[] array, int fromIndex, int toIndex) {
            int sourceArrayLength = array.length;
            
            if (fromIndex < 0) {
                throw new IndexOutOfBoundsException("fromIndex(" + fromIndex + ") < 0.");
            }
            if (toIndex > sourceArrayLength) {
                throw new IndexOutOfBoundsException("toIndex(" + toIndex + ") > array.length(" + sourceArrayLength + ").");
            }            
            if (fromIndex > toIndex) {
                throw new IllegalArgumentException("fromIndex(" + fromIndex + ") >  toIndex(" + toIndex + ").");
            }
            
            int nElementsRemoved = toIndex - fromIndex;
            if (nElementsRemoved == 0) {
                return array;
            }
            
            if (nElementsRemoved == sourceArrayLength) {
                return ShortArray.empty;
            }
                       
            short[] newArray = new short[sourceArrayLength - nElementsRemoved];
            if (fromIndex > 0) {
                System.arraycopy(array, 0, newArray, 0, fromIndex);
            }
            if (toIndex < sourceArrayLength) {
                System.arraycopy(array, toIndex, newArray, fromIndex, sourceArrayLength - toIndex);
            }
                      
            return newArray;                                  
        }
        
        public static short[] replicate(int nCopies, short valueToReplicate) {
            if (nCopies <= 0) {
                return ShortArray.empty;
            }
            
            short[] result = new short[nCopies];
            Arrays.fill(result, valueToReplicate);
            
            return result;
        }
        
        /**        
         * Support for defining the Eq a => Eq (Array a) instance when a is in fact Short. This relies on the 
         * fact that the implementation of Eq Short is just java primitive short equality.
         */
        public static boolean equals(short[] array1, short[] array2) {
            return Arrays.equals(array1, array2);
        }
                    
        public static boolean notEquals(short[] array1, short[] array2) {
            return !equals(array1, array2);
        }
        
        private static int compareElem(short x, short y) {
            return x < y ? -1 : (x == y ? 0 : 1);    
        }
        
        /**
         * Support for defining the Ord a => Ord (Array a) instance when a is in fact Short. This relies on the 
         * fact that the implementation of Ord Short is just java primitive short comparison.
         * 
         * Comparison using lexicographic ordering with Ord Short used on elements.         
         * @param array1
         * @param array2
         * @return -1, 0, or 1.
         */
        public static int compare(short[] array1, short[] array2) {
            if (array1 == array2) {
                return 0;
            }
            
            int n = Math.min(array1.length, array2.length);
            
            for (int i = 0; i < n; ++i) {
                
                int elementCompare = compareElem(array1[i], array2[i]);
                if (elementCompare != 0) {
                    return elementCompare;
                }                                     
            }
            
            return compareInt(array1.length, array2.length);
        }        
        
        public static boolean lessThan(short[] array1, short[] array2) {
            return compare(array1, array2) < 0;
        }
        
        public static boolean lessThanEquals(short[] array1, short[] array2) {
            return compare(array1, array2) <= 0;
        }
        
        public static boolean greaterThanEquals(short[] array1, short[] array2) {
            return compare(array1, array2) >= 0;
        }
        
        public static boolean greaterThan(short[] array1, short[] array2) {
            return compare(array1, array2) > 0;
        }        
                                       
        public static short[] max(short[] array1, short[] array2) {
            return lessThanEquals(array1, array2) ? array2 : array1;                       
        }
        
        public static short[] min(short[] array1, short[] array2) {
            return lessThanEquals(array1, array2) ? array1 : array2;
        }                
        
        /** 
         * Note: this method relies on the fact that we know that Eq Short is java primitive comparison on shorts.
         *    
         * @param array array to search for element in.     
         * @param element short to search for.
         * @return the (0-based) index of the first occurrence of element in the array or -1 if element does not occur.
         */
        public static int indexOf(short[] array, short element) {
            return indexOf(array, element, 0);           
        }
        
        /**         
         * @param array array to search for element in.
         * @param element short to search for.
         * @param fromIndex the (0-based) index to inclusively start the search from. There are no invalid values of fromIndex.
         *      If fromIndex < 0, 0 is assumed. If fromIndex >= length.array -1 will be returned.
         * @return the (0-based) index of the first occurrence of element in the array with index >= fromIndex or -1 if 
         *      element does not occur.
         */
        public static int indexOf(short[] array, short element, int fromIndex) {
                       
            for (int i = (fromIndex < 0 ? 0 : fromIndex), len = array.length; i < len; ++i) {
                
                if (array[i] == element) {
                    return i;
                }
            }
            return -1;
        }
        
        public static int lastIndexOf(short[] array, short element) {
            return lastIndexOf(array, element, array.length - 1);            
        }
        
        /**                
         * @param array array to search for element in.
         * @param element short to search for.
         * @param fromIndex There are no invalid values for fromIndex. If fromIndex < 0, then -1 is always returned.
         * @return the 0-based index of the last occurrence of element within array whose index is less than or equal to fromIndex
         *     or -1 if element does not occur.
         */
        public static int lastIndexOf(short[] array, short element, int fromIndex) {              
            if (fromIndex < 0) {
                return -1;
            }
                                               
            int actualFromIndex = Math.min(fromIndex, array.length -1);
            
            for (int i = actualFromIndex; i >= 0; --i) {
                if (array[i] == element) {
                    return i;
                }
            }
            
            return -1;
        }
        
        public static short[] replace(short[] array, short oldElementValue, short newElementValue) {
            
            if (oldElementValue == newElementValue) {
                return array;
            }
            
            int len = array.length;
            if (len == 0) {
                return ShortArray.empty;
            }
            
            short[] newArray = new short[len];
            
            for (int i = 0; i < len; ++i) {
                
                short currentVal = array[i];                
                newArray[i] = (currentVal == oldElementValue) ? newElementValue : currentVal;                
            }
            
            return newArray;
        }        
        
        public static short[] sort(short[] array) {
            if (array.length == 0) {
                return array;
            }
            
            short[] newArray = array.clone();            
            Arrays.sort(newArray);
            return newArray;
        }        
               
    }
    
    
    /**
     * Collection of static helper functions and fields for working with the Java int array type.    
     * 
     * Note, that this class is primarily intended to support the Array.cal module. In particular, most
     * of the operations below do not do destructive updates but rather make copies. Also, we can assume (in
     * most cases) that the array is not a null array.
     * 
     * @author Bo Ilic
     */
    public static final class IntArray {
        
        private IntArray() {}
    
        public static final int[] empty = new int[0];                      
        
        /**         
         * @param array
         * @return representation of the array for debugging purposes.
         */
        public static final String toString(int[] array) {
            if (array == null) {             
                return "null";
            }
            
            int len = array.length;
            if (len == 0) {
                return "[]";
            }
 
            StringBuilder result = new StringBuilder("[");           
            result.append(array[0]); 
            for (int i = 1; i < len; ++i) {
                result.append(", ");
                result.append(array[i]);
            }
            result.append("]");
            return result.toString();
        }               
        
        public static final int[] array1(int value) {
            return new int[] {value};
        }        
        
        public static final int[] array2(int value1, int value2) {
            return new int[] {value1, value2};
        }
        
        public static final int[] array3(int value1, int value2, int value3) {
            return new int[] {value1, value2, value3};
        }
        
        public static final int[] array4(int value1, int value2, int value3, int value4) {
            return new int[] {value1, value2, value3, value4};
        }
        
        public static final int[] array5(int value1, int value2, int value3, int value4, int value5) {
            return new int[] {value1, value2, value3, value4, value5};
        }
        
        public static final int[] array6(int value1, int value2, int value3, int value4, int value5, int value6) {
            return new int[] {value1, value2, value3, value4, value5, value6};
        }
        
        public static final int[] array7(int value1, int value2, int value3, int value4, int value5, int value6, int value7) {
            return new int[] {value1, value2, value3, value4, value5, value6, value7};
        }                                            
        
        /**
         * Clones the array, replacing a null array with a zero-length array.
         * @param array may be null
         * @return will not be null
         */
        public static final int[] cloneReplacingNullArray (int[] array) {
            if (array == null) {
                return IntArray.empty;
            }
            return array.clone();
        }               
        
        public static final int[] listToArray(List<Integer> list) {
            int size = list.size();
            if (size == 0) {
                return IntArray.empty;
            }
            
            int[] resultArray = new int[size];
            
            for (int i = 0; i < size; ++i) {
                resultArray[i] = list.get(i).intValue();
            }
            
            return resultArray;
        }
        
        /**
         * @param firstElement the first element of the array to be constructed.
         * @param list the remainder of the elements of the array.
         * @return the array.
         */
        public static final int[] listToArrayWithFirstElement(int firstElement, List<Integer> list) {
            int size = list.size() + 1;
            
            int[] resultArray = new int[size];
            
            resultArray[0] = firstElement;
            
            for (int i = 1; i < size; ++i) {
                resultArray[i] = list.get(i-1).intValue();
            }
            
            return resultArray;
        }
        
        /**     
         * @param array
         * @return the returned List is backed by the int array and hence any modifications 
         *      to the int array will modify the list.
         */
        public static final List<Integer> arrayToList(final int[] array) {
                  
            class IntList extends AbstractList<Integer> implements RandomAccess {
                          
                @Override
                public Integer get(int index) {
                    return Integer.valueOf(array[index]);                  
                }
                    
                @Override
                public int size() {        
                    return array.length;
                }                           
            }
            
            return new IntList();        
        }    
        
        /** 
         * Note: in certain circumstances, this function does not return a copy of
         * the argument arrays, and hence should not be used in situations where
         * the argument arrays may be mutated.
         *         
         * @param array1
         * @param array2
         * @return int[] the concatenation of array1 with array2.
         *      the function return array2 if array1 is empty, and vice-versa.  
         * @throws NullPointerException if either array1 or array2 are null.
         */
        public static int[] concat(int[] array1, int[] array2) {
            
            int length1 = array1.length;
            if (length1 == 0)  {                
                return array2;
            } 
            
            final int length2 = array2.length;
            if (length2 == 0) {
                return array1;
            }
            
            final int[] resultArray = new int[length1 + length2];
            System.arraycopy(array1, 0, resultArray, 0, length1);
            System.arraycopy(array2, 0, resultArray, length1, length2);
            return resultArray;
        }
            
        /** 
         * Note: in certain circumstances, this function does not return a copy of
         * the argument arrays, and hence should not be used in situations where
         * the argument arrays may be mutated.
         *       
         * @param list of int[] Objects
         * @return concatenation of the int arrays in the List
         */
        public static int[] concatList(List<int[]> list) {            
            final int nArrays = list.size();
            
            switch (nArrays) {
            case 0:                          
                return IntArray.empty;
                
            case 1:                          
                return list.get(0);
                
            case 2:
                return concat(list.get(0), list.get(1));
                
            default:
                break;
            }
            
            int resultSize = 0;
            for (int i = 0; i < nArrays; ++i) {
                resultSize += list.get(i).length;
            }
            
            final int[] result = new int[resultSize];
            int copyStartPos = 0;
            for (int i = 0; i < nArrays; ++i) {
                final int[] currentintArray = list.get(i);
                final int currentintArrayLength = currentintArray.length;
                System.arraycopy(currentintArray, 0, result, copyStartPos, currentintArrayLength);
                copyStartPos += currentintArrayLength;
            }
            
            return result;
        }
        
        public static int[] reverse(int[] array) {
            int len = array.length;
            if (len == 0) {
                return IntArray.empty;
            }
            
            int[] resultArray = new int[len];            
            int lastIndex = len - 1;
            for (int i = 0; i < len; ++i) {
                resultArray[i] = array[lastIndex - i];
            }
            
            return resultArray;            
        }
        
        /**         
         * @param array
         * @param fromIndex inclusive 0-based index
         * @param toIndex exclusive 0-based index
         * @return copy of the array from fromIndex (inclusive) to toIndex (exclusive). 
         */
        public static int[] subArray(int[] array, int fromIndex, int toIndex) {
            
            int sourceArrayLength = array.length;
            if (fromIndex < 0) {
                throw new IndexOutOfBoundsException("fromIndex(" + fromIndex + ") < 0.");
            }
            if (toIndex > sourceArrayLength) {
                throw new IndexOutOfBoundsException("toIndex(" + toIndex + ") > array.length(" + sourceArrayLength + ").");
            }
            if (fromIndex > toIndex) {
                throw new IllegalArgumentException("fromIndex(" + fromIndex + ") >  toIndex(" + toIndex + ").");
            }
            
            int newArrayLength = toIndex - fromIndex;
            if (newArrayLength == 0) {
                return IntArray.empty;
            }
            
            if (newArrayLength == sourceArrayLength) {
                return array;
            }
            
            int[] newArray = new int[newArrayLength];
            System.arraycopy(array, fromIndex, newArray, 0, toIndex - fromIndex);
                      
            return newArray;
        } 
        
        /** 
         * Efficiently removes a range of elements from an array.
         * The returned array has (toIndex - fromIndex) fewer elements. 
         *  
         * @param array
         * @param fromIndex inclusive index of the array to start removal from
         * @param toIndex exclusive index of the array to stop removal at
         * @return a copy (if necessary) of array with indices from fromIndex to toIndex removed
         */
        public static int[] removeRange(int[] array, int fromIndex, int toIndex) {
            int sourceArrayLength = array.length;
            
            if (fromIndex < 0) {
                throw new IndexOutOfBoundsException("fromIndex(" + fromIndex + ") < 0.");
            }
            if (toIndex > sourceArrayLength) {
                throw new IndexOutOfBoundsException("toIndex(" + toIndex + ") > array.length(" + sourceArrayLength + ").");
            }            
            if (fromIndex > toIndex) {
                throw new IllegalArgumentException("fromIndex(" + fromIndex + ") >  toIndex(" + toIndex + ").");
            }
            
            int nElementsRemoved = toIndex - fromIndex;
            if (nElementsRemoved == 0) {
                return array;
            }
            
            if (nElementsRemoved == sourceArrayLength) {
                return IntArray.empty;
            }
                       
            int[] newArray = new int[sourceArrayLength - nElementsRemoved];
            if (fromIndex > 0) {
                System.arraycopy(array, 0, newArray, 0, fromIndex);
            }
            if (toIndex < sourceArrayLength) {
                System.arraycopy(array, toIndex, newArray, fromIndex, sourceArrayLength - toIndex);
            }
                      
            return newArray;                                  
        }
        
        public static int[] replicate(int nCopies, int valueToReplicate) {
            if (nCopies <= 0) {
                return IntArray.empty;
            }
            
            int[] result = new int[nCopies];
            Arrays.fill(result, valueToReplicate);
            
            return result;
        }
        
        /**        
         * Support for defining the Eq a => Eq (Array a) instance when a is in fact Int. This relies on the 
         * fact that the implementation of Eq Int is just java primitive int equality.
         */
        public static boolean equals(int[] array1, int[] array2) {
            return Arrays.equals(array1, array2);
        }
                    
        public static boolean notEquals(int[] array1, int[] array2) {
            return !equals(array1, array2);
        }
        
        /**
         * Support for defining the Ord a => Ord (Array a) instance when a is in fact Int. This relies on the 
         * fact that the implementation of Ord Int is just java primitive int comparison.
         * 
         * Comparison using lexicographic ordering with Ord Int used on elements.         
         * @param array1
         * @param array2
         * @return -1, 0, or 1.
         */
        public static int compare(int[] array1, int[] array2) {
            if (array1 == array2) {
                return 0;
            }
            
            int n = Math.min(array1.length, array2.length);
            
            for (int i = 0; i < n; ++i) {
                
                int elementCompare = compareInt(array1[i], array2[i]);
                if (elementCompare != 0) {
                    return elementCompare;
                }                                     
            }
            
            return compareInt(array1.length, array2.length);
        }        
        
        public static boolean lessThan(int[] array1, int[] array2) {
            return compare(array1, array2) < 0;
        }
        
        public static boolean lessThanEquals(int[] array1, int[] array2) {
            return compare(array1, array2) <= 0;
        }
        
        public static boolean greaterThanEquals(int[] array1, int[] array2) {
            return compare(array1, array2) >= 0;
        }
        
        public static boolean greaterThan(int[] array1, int[] array2) {
            return compare(array1, array2) > 0;
        }        
                                       
        public static int[] max(int[] array1, int[] array2) {
            return lessThanEquals(array1, array2) ? array2 : array1;                       
        }
        
        public static int[] min(int[] array1, int[] array2) {
            return lessThanEquals(array1, array2) ? array1 : array2;
        }                
        
        /** 
         * Note: this method relies on the fact that we know that Eq Int is java primitive comparison on ints.
         *    
         * @param array array to search for element in.     
         * @param element int to search for.
         * @return the (0-based) index of the first occurrence of element in the array or -1 if element does not occur.
         */
        public static int indexOf(int[] array, int element) {
            return indexOf(array, element, 0);           
        }
        
        /**         
         * @param array array to search for element in.
         * @param element int to search for.
         * @param fromIndex the (0-based) index to inclusively start the search from. There are no invalid values of fromIndex.
         *      If fromIndex < 0, 0 is assumed. If fromIndex >= length.array -1 will be returned.
         * @return the (0-based) index of the first occurrence of element in the array with index >= fromIndex or -1 if 
         *      element does not occur.
         */
        public static int indexOf(int[] array, int element, int fromIndex) {
                       
            for (int i = (fromIndex < 0 ? 0 : fromIndex), len = array.length; i < len; ++i) {
                
                if (array[i] == element) {
                    return i;
                }
            }
            return -1;
        }
        
        public static int lastIndexOf(int[] array, int element) {
            return lastIndexOf(array, element, array.length - 1);            
        }
        
        /**                
         * @param array array to search for element in.
         * @param element int to search for.
         * @param fromIndex There are no invalid values for fromIndex. If fromIndex < 0, then -1 is always returned.
         * @return the 0-based index of the last occurrence of element within array whose index is less than or equal to fromIndex
         *     or -1 if element does not occur.
         */
        public static int lastIndexOf(int[] array, int element, int fromIndex) {              
            if (fromIndex < 0) {
                return -1;
            }
                                               
            int actualFromIndex = Math.min(fromIndex, array.length -1);
            
            for (int i = actualFromIndex; i >= 0; --i) {
                if (array[i] == element) {
                    return i;
                }
            }
            
            return -1;
        }
        
        public static int[] replace(int[] array, int oldElementValue, int newElementValue) {
            
            if (oldElementValue == newElementValue) {
                return array;
            }
            
            int len = array.length;
            if (len == 0) {
                return IntArray.empty;
            }
            
            int[] newArray = new int[len];
            
            for (int i = 0; i < len; ++i) {
                
                int currentVal = array[i];                
                newArray[i] = (currentVal == oldElementValue) ? newElementValue : currentVal;                
            }
            
            return newArray;
        }        
        
        public static int[] sort(int[] array) {
            if (array.length == 0) {
                return array;
            }
            
            int[] newArray = array.clone();            
            Arrays.sort(newArray);
            return newArray;
        }        
               
    }
    
    
    /**
     * Collection of static helper functions and fields for working with the Java long array type.    
     * 
     * Note, that this class is primarily intended to support the Array.cal module. In particular, most
     * of the operations below do not do destructive updates but rather make copies. Also, we can assume (in
     * most cases) that the array is not a null array.
     * 
     * @author Bo Ilic
     */
    public static final class LongArray {
        
        private LongArray() {}
    
        public static final long[] empty = new long[0];                     
        
        /**         
         * @param array
         * @return representation of the array for debugging purposes.
         */
        public static final String toString(long[] array) {
            if (array == null) {             
                return "null";
            }
            
            int len = array.length;
            if (len == 0) {
                return "[]";
            }
 
            StringBuilder result = new StringBuilder("[");           
            result.append(array[0]); 
            for (int i = 1; i < len; ++i) {
                result.append(", ");
                result.append(array[i]);
            }
            result.append("]");
            return result.toString();
        }             
        
        public static final long[] array1(long value) {
            return new long[] {value};
        }        
        
        public static final long[] array2(long value1, long value2) {
            return new long[] {value1, value2};
        }
        
        public static final long[] array3(long value1, long value2, long value3) {
            return new long[] {value1, value2, value3};
        }
        
        public static final long[] array4(long value1, long value2, long value3, long value4) {
            return new long[] {value1, value2, value3, value4};
        }
        
        public static final long[] array5(long value1, long value2, long value3, long value4, long value5) {
            return new long[] {value1, value2, value3, value4, value5};
        }
        
        public static final long[] array6(long value1, long value2, long value3, long value4, long value5, long value6) {
            return new long[] {value1, value2, value3, value4, value5, value6};
        }
        
        public static final long[] array7(long value1, long value2, long value3, long value4, long value5, long value6, long value7) {
            return new long[] {value1, value2, value3, value4, value5, value6, value7};
        }                                             
        
        /**
         * Clones the array, replacing a null array with a zero-length array.
         * @param array may be null
         * @return will not be null
         */
        public static final long[] cloneReplacingNullArray (long[] array) {
            if (array == null) {
                return LongArray.empty;
            }
            return array.clone();
        }              
        
        public static final long[] listToArray(List<Long> list) {
            int size = list.size();
            if (size == 0) {
                return LongArray.empty;
            }
            
            long[] resultArray = new long[size];
            
            for (int i = 0; i < size; ++i) {
                resultArray[i] = list.get(i).longValue();
            }
            
            return resultArray;
        }
        
        /**
         * @param firstElement the first element of the array to be constructed.
         * @param list the remainder of the elements of the array.
         * @return the array.
         */
        public static final long[] listToArrayWithFirstElement(long firstElement, List<Long> list) {
            int size = list.size() + 1;
            
            long[] resultArray = new long[size];
            
            resultArray[0] = firstElement;
            
            for (int i = 1; i < size; ++i) {
                resultArray[i] = list.get(i-1).longValue();
            }
            
            return resultArray;
        }
        
        /**     
         * @param array
         * @return the returned List is backed by the long array and hence any modifications 
         *      to the long array will modify the list.
         */
        public static final List<Long> arrayToList(final long[] array) {
                  
            class LongList extends AbstractList<Long> implements RandomAccess {
                          
                @Override
                public Long get(int index) {
                    return Long.valueOf(array[index]);                  
                }
                    
                @Override
                public int size() {        
                    return array.length;
                }                           
            }
            
            return new LongList();        
        }    
        
        /** 
         * Note: in certain circumstances, this function does not return a copy of
         * the argument arrays, and hence should not be used in situations where
         * the argument arrays may be mutated.
         *         
         * @param array1
         * @param array2
         * @return long[] the concatenation of array1 with array2.
         *      the function return array2 if array1 is empty, and vice-versa.  
         * @throws NullPointerException if either array1 or array2 are null.
         */
        public static long[] concat(long[] array1, long[] array2) {
            
            int length1 = array1.length;
            if (length1 == 0)  {                
                return array2;
            } 
            
            final int length2 = array2.length;
            if (length2 == 0) {
                return array1;
            }
            
            final long[] resultArray = new long[length1 + length2];
            System.arraycopy(array1, 0, resultArray, 0, length1);
            System.arraycopy(array2, 0, resultArray, length1, length2);
            return resultArray;
        }
            
        /** 
         * Note: in certain circumstances, this function does not return a copy of
         * the argument arrays, and hence should not be used in situations where
         * the argument arrays may be mutated.
         *       
         * @param list of long[] Objects
         * @return concatenation of the long arrays in the List
         */
        public static long[] concatList(List<long[]> list) {            
            final int nArrays = list.size();
            
            switch (nArrays) {
            case 0:                          
                return LongArray.empty;
                
            case 1:                          
                return list.get(0);
                
            case 2:
                return concat(list.get(0), list.get(1));
                
            default:
                break;
            }
            
            int resultSize = 0;
            for (int i = 0; i < nArrays; ++i) {
                resultSize += list.get(i).length;
            }
            
            final long[] result = new long[resultSize];
            int copyStartPos = 0;
            for (int i = 0; i < nArrays; ++i) {
                final long[] currentlongArray = list.get(i);
                final int currentlongArrayLength = currentlongArray.length;
                System.arraycopy(currentlongArray, 0, result, copyStartPos, currentlongArrayLength);
                copyStartPos += currentlongArrayLength;
            }
            
            return result;
        }
        
        public static long[] reverse(long[] array) {
            int len = array.length;
            if (len == 0) {
                return LongArray.empty;
            }
            
            long[] resultArray = new long[len];            
            int lastIndex = len - 1;
            for (int i = 0; i < len; ++i) {
                resultArray[i] = array[lastIndex - i];
            }
            
            return resultArray;            
        }
        
        /**         
         * @param array
         * @param fromIndex inclusive 0-based index
         * @param toIndex exclusive 0-based index
         * @return copy of the array from fromIndex (inclusive) to toIndex (exclusive). 
         */
        public static long[] subArray(long[] array, int fromIndex, int toIndex) {
            
            int sourceArrayLength = array.length;
            if (fromIndex < 0) {
                throw new IndexOutOfBoundsException("fromIndex(" + fromIndex + ") < 0.");
            }
            if (toIndex > sourceArrayLength) {
                throw new IndexOutOfBoundsException("toIndex(" + toIndex + ") > array.length(" + sourceArrayLength + ").");
            }
            if (fromIndex > toIndex) {
                throw new IllegalArgumentException("fromIndex(" + fromIndex + ") >  toIndex(" + toIndex + ").");
            }
            
            int newArrayLength = toIndex - fromIndex;
            if (newArrayLength == 0) {
                return LongArray.empty;
            }
            
            if (newArrayLength == sourceArrayLength) {
                return array;
            }
            
            long[] newArray = new long[newArrayLength];
            System.arraycopy(array, fromIndex, newArray, 0, toIndex - fromIndex);
                      
            return newArray;
        } 
        
        /** 
         * Efficiently removes a range of elements from an array.
         * The returned array has (toIndex - fromIndex) fewer elements. 
         *  
         * @param array
         * @param fromIndex inclusive index of the array to start removal from
         * @param toIndex exclusive index of the array to stop removal at
         * @return a copy (if necessary) of array with indices from fromIndex to toIndex removed
         */
        public static long[] removeRange(long[] array, int fromIndex, int toIndex) {
            int sourceArrayLength = array.length;
            
            if (fromIndex < 0) {
                throw new IndexOutOfBoundsException("fromIndex(" + fromIndex + ") < 0.");
            }
            if (toIndex > sourceArrayLength) {
                throw new IndexOutOfBoundsException("toIndex(" + toIndex + ") > array.length(" + sourceArrayLength + ").");
            }            
            if (fromIndex > toIndex) {
                throw new IllegalArgumentException("fromIndex(" + fromIndex + ") >  toIndex(" + toIndex + ").");
            }
            
            int nElementsRemoved = toIndex - fromIndex;
            if (nElementsRemoved == 0) {
                return array;
            }
            
            if (nElementsRemoved == sourceArrayLength) {
                return LongArray.empty;
            }
                       
            long[] newArray = new long[sourceArrayLength - nElementsRemoved];
            if (fromIndex > 0) {
                System.arraycopy(array, 0, newArray, 0, fromIndex);
            }
            if (toIndex < sourceArrayLength) {
                System.arraycopy(array, toIndex, newArray, fromIndex, sourceArrayLength - toIndex);
            }
                      
            return newArray;                                  
        }
        
        public static long[] replicate(int nCopies, long valueToReplicate) {
            if (nCopies <= 0) {
                return LongArray.empty;
            }
            
            long[] result = new long[nCopies];
            Arrays.fill(result, valueToReplicate);
            
            return result;
        }
        
        /**        
         * Support for defining the Eq a => Eq (Array a) instance when a is in fact Long. This relies on the 
         * fact that the implementation of Eq Long is just java primitive long equality.
         */
        public static boolean equals(long[] array1, long[] array2) {
            return Arrays.equals(array1, array2);
        }
                    
        public static boolean notEquals(long[] array1, long[] array2) {
            return !equals(array1, array2);
        }
        
        public static int compareElem(long x, long y) {
            return x < y ? -1 : (x == y ? 0 : 1);
        } 
        
        /**
         * Support for defining the Ord a => Ord (Array a) instance when a is in fact Long. This relies on the 
         * fact that the implementation of Ord Long is just java primitive long comparison.
         * 
         * Comparison using lexicographic ordering with Ord Long used on elements.         
         * @param array1
         * @param array2
         * @return -1, 0, or 1.
         */
        public static int compare(long[] array1, long[] array2) {
            if (array1 == array2) {
                return 0;
            }
            
            int n = Math.min(array1.length, array2.length);
            
            for (int i = 0; i < n; ++i) {
                
                int elementCompare = compareElem(array1[i], array2[i]);
                if (elementCompare != 0) {
                    return elementCompare;
                }                                     
            }
            
            return compareInt(array1.length, array2.length);
        }        
        
        public static boolean lessThan(long[] array1, long[] array2) {
            return compare(array1, array2) < 0;
        }
        
        public static boolean lessThanEquals(long[] array1, long[] array2) {
            return compare(array1, array2) <= 0;
        }
        
        public static boolean greaterThanEquals(long[] array1, long[] array2) {
            return compare(array1, array2) >= 0;
        }
        
        public static boolean greaterThan(long[] array1, long[] array2) {
            return compare(array1, array2) > 0;
        }        
                                       
        public static long[] max(long[] array1, long[] array2) {
            return lessThanEquals(array1, array2) ? array2 : array1;                       
        }
        
        public static long[] min(long[] array1, long[] array2) {
            return lessThanEquals(array1, array2) ? array1 : array2;
        }                
        
        /** 
         * Note: this method relies on the fact that we know that Eq Long is java primitive comparison on longs.
         *    
         * @param array array to search for element in.     
         * @param element long to search for.
         * @return the (0-based) index of the first occurrence of element in the array or -1 if element does not occur.
         */
        public static int indexOf(long[] array, long element) {
            return indexOf(array, element, 0);           
        }
        
        /**         
         * @param array array to search for element in.
         * @param element long to search for.
         * @param fromIndex the (0-based) index to inclusively start the search from. There are no invalid values of fromIndex.
         *      If fromIndex < 0, 0 is assumed. If fromIndex >= length.array -1 will be returned.
         * @return the (0-based) index of the first occurrence of element in the array with index >= fromIndex or -1 if 
         *      element does not occur.
         */
        public static int indexOf(long[] array, long element, int fromIndex) {
                       
            for (int i = (fromIndex < 0 ? 0 : fromIndex), len = array.length; i < len; ++i) {
                
                if (array[i] == element) {
                    return i;
                }
            }
            return -1;
        }
        
        public static int lastIndexOf(long[] array, long element) {
            return lastIndexOf(array, element, array.length - 1);            
        }
        
        /**                
         * @param array array to search for element in.
         * @param element long to search for.
         * @param fromIndex There are no invalid values for fromIndex. If fromIndex < 0, then -1 is always returned.
         * @return the 0-based index of the last occurrence of element within array whose index is less than or equal to fromIndex
         *     or -1 if element does not occur.
         */
        public static int lastIndexOf(long[] array, long element, int fromIndex) {              
            if (fromIndex < 0) {
                return -1;
            }
                                               
            int actualFromIndex = Math.min(fromIndex, array.length -1);
            
            for (int i = actualFromIndex; i >= 0; --i) {
                if (array[i] == element) {
                    return i;
                }
            }
            
            return -1;
        }
        
        public static long[] replace(long[] array, long oldElementValue, long newElementValue) {
            
            if (oldElementValue == newElementValue) {
                return array;
            }
            
            int len = array.length;
            if (len == 0) {
                return LongArray.empty;
            }
            
            long[] newArray = new long[len];
            
            for (int i = 0; i < len; ++i) {
                
                long currentVal = array[i];                
                newArray[i] = (currentVal == oldElementValue) ? newElementValue : currentVal;                
            }
            
            return newArray;
        }        
        
        public static long[] sort(long[] array) {
            if (array.length == 0) {
                return array;
            }
            
            long[] newArray = array.clone();            
            Arrays.sort(newArray);
            return newArray;
        }        
               
    }
    
    
    /**
     * Collection of static helper functions and fields for working with the Java float array type.    
     * 
     * Note, that this class is primarily intended to support the Array.cal module. In particular, most
     * of the operations below do not do destructive updates but rather make copies. Also, we can assume (in
     * most cases) that the array is not a null array.
     * 
     * @author Bo Ilic
     */
    public static final class FloatArray {
        
        private FloatArray() {}
    
        public static final float[] empty = new float[0];                  
        
        /**         
         * @param array
         * @return representation of the array for debugging purposes.
         */
        public static final String toString(float[] array) {
            if (array == null) {             
                return "null";
            }
            
            int len = array.length;
            if (len == 0) {
                return "[]";
            }
 
            StringBuilder result = new StringBuilder("[");           
            result.append(array[0]); 
            for (int i = 1; i < len; ++i) {
                result.append(", ");
                result.append(array[i]);
            }
            result.append("]");
            return result.toString();
        }              
        
        public static final float[] array1(float value) {
            return new float[] {value};
        }        
        
        public static final float[] array2(float value1, float value2) {
            return new float[] {value1, value2};
        }
        
        public static final float[] array3(float value1, float value2, float value3) {
            return new float[] {value1, value2, value3};
        }
        
        public static final float[] array4(float value1, float value2, float value3, float value4) {
            return new float[] {value1, value2, value3, value4};
        }
        
        public static final float[] array5(float value1, float value2, float value3, float value4, float value5) {
            return new float[] {value1, value2, value3, value4, value5};
        }
        
        public static final float[] array6(float value1, float value2, float value3, float value4, float value5, float value6) {
            return new float[] {value1, value2, value3, value4, value5, value6};
        }
        
        public static final float[] array7(float value1, float value2, float value3, float value4, float value5, float value6, float value7) {
            return new float[] {value1, value2, value3, value4, value5, value6, value7};
        }                                   
                
        /**
         * Clones the array, replacing a null array with a zero-length array.
         * @param array may be null
         * @return will not be null
         */
        public static final float[] cloneReplacingNullArray (float[] array) {
            if (array == null) {
                return FloatArray.empty;
            }
            return array.clone();
        }               
        
        public static final float[] listToArray(List<Float> list) {
            int size = list.size();
            if (size == 0) {
                return FloatArray.empty;
            }
            
            float[] resultArray = new float[size];
            
            for (int i = 0; i < size; ++i) {
                resultArray[i] = list.get(i).floatValue();
            }
            
            return resultArray;
        }
        
        /**
         * @param firstElement the first element of the array to be constructed.
         * @param list the remainder of the elements of the array.
         * @return the array.
         */
        public static final float[] listToArrayWithFirstElement(float firstElement, List<Float> list) {
            int size = list.size() + 1;
            
            float[] resultArray = new float[size];
            
            resultArray[0] = firstElement;
            
            for (int i = 1; i < size; ++i) {
                resultArray[i] = list.get(i-1).floatValue();
            }
            
            return resultArray;
        }
        
        /**     
         * @param array
         * @return the returned List is backed by the float array and hence any modifications 
         *      to the float array will modify the list.
         */
        public static final List<Float> arrayToList(final float[] array) {
                  
            class FloatList extends AbstractList<Float> implements RandomAccess {
                          
                @Override
                public Float get(int index) {
                    return Float.valueOf(array[index]);                  
                }
                    
                @Override
                public int size() {        
                    return array.length;
                }                           
            }
            
            return new FloatList();        
        }    
        
        /** 
         * Note: in certain circumstances, this function does not return a copy of
         * the argument arrays, and hence should not be used in situations where
         * the argument arrays may be mutated.
         *         
         * @param array1
         * @param array2
         * @return float[] the concatenation of array1 with array2.
         *      the function return array2 if array1 is empty, and vice-versa.  
         * @throws NullPointerException if either array1 or array2 are null.
         */
        public static float[] concat(float[] array1, float[] array2) {
            
            int length1 = array1.length;
            if (length1 == 0)  {                
                return array2;
            } 
            
            final int length2 = array2.length;
            if (length2 == 0) {
                return array1;
            }
            
            final float[] resultArray = new float[length1 + length2];
            System.arraycopy(array1, 0, resultArray, 0, length1);
            System.arraycopy(array2, 0, resultArray, length1, length2);
            return resultArray;
        }
            
        /** 
         * Note: in certain circumstances, this function does not return a copy of
         * the argument arrays, and hence should not be used in situations where
         * the argument arrays may be mutated.
         *       
         * @param list of float[] Objects
         * @return concatenation of the float arrays in the List
         */
        public static float[] concatList(List<float[]> list) {            
            final int nArrays = list.size();
            
            switch (nArrays) {
            case 0:                          
                return FloatArray.empty;
                
            case 1:                          
                return list.get(0);
                
            case 2:
                return concat(list.get(0), list.get(1));
                
            default:
                break;
            }
            
            int resultSize = 0;
            for (int i = 0; i < nArrays; ++i) {
                resultSize += list.get(i).length;
            }
            
            final float[] result = new float[resultSize];
            int copyStartPos = 0;
            for (int i = 0; i < nArrays; ++i) {
                final float[] currentfloatArray = list.get(i);
                final int currentfloatArrayLength = currentfloatArray.length;
                System.arraycopy(currentfloatArray, 0, result, copyStartPos, currentfloatArrayLength);
                copyStartPos += currentfloatArrayLength;
            }
            
            return result;
        }
        
        public static float[] reverse(float[] array) {
            int len = array.length;
            if (len == 0) {
                return FloatArray.empty;
            }
            
            float[] resultArray = new float[len];            
            int lastIndex = len - 1;
            for (int i = 0; i < len; ++i) {
                resultArray[i] = array[lastIndex - i];
            }
            
            return resultArray;            
        }
        
        /**         
         * @param array
         * @param fromIndex inclusive 0-based index
         * @param toIndex exclusive 0-based index
         * @return copy of the array from fromIndex (inclusive) to toIndex (exclusive). 
         */
        public static float[] subArray(float[] array, int fromIndex, int toIndex) {
            
            int sourceArrayLength = array.length;
            if (fromIndex < 0) {
                throw new IndexOutOfBoundsException("fromIndex(" + fromIndex + ") < 0.");
            }
            if (toIndex > sourceArrayLength) {
                throw new IndexOutOfBoundsException("toIndex(" + toIndex + ") > array.length(" + sourceArrayLength + ").");
            }
            if (fromIndex > toIndex) {
                throw new IllegalArgumentException("fromIndex(" + fromIndex + ") >  toIndex(" + toIndex + ").");
            }
            
            int newArrayLength = toIndex - fromIndex;
            if (newArrayLength == 0) {
                return FloatArray.empty;
            }
            
            if (newArrayLength == sourceArrayLength) {
                return array;
            }
            
            float[] newArray = new float[newArrayLength];
            System.arraycopy(array, fromIndex, newArray, 0, toIndex - fromIndex);
                      
            return newArray;
        } 
        
        /** 
         * Efficiently removes a range of elements from an array.
         * The returned array has (toIndex - fromIndex) fewer elements. 
         *  
         * @param array
         * @param fromIndex inclusive index of the array to start removal from
         * @param toIndex exclusive index of the array to stop removal at
         * @return a copy (if necessary) of array with indices from fromIndex to toIndex removed
         */
        public static float[] removeRange(float[] array, int fromIndex, int toIndex) {
            int sourceArrayLength = array.length;
            
            if (fromIndex < 0) {
                throw new IndexOutOfBoundsException("fromIndex(" + fromIndex + ") < 0.");
            }
            if (toIndex > sourceArrayLength) {
                throw new IndexOutOfBoundsException("toIndex(" + toIndex + ") > array.length(" + sourceArrayLength + ").");
            }            
            if (fromIndex > toIndex) {
                throw new IllegalArgumentException("fromIndex(" + fromIndex + ") >  toIndex(" + toIndex + ").");
            }
            
            int nElementsRemoved = toIndex - fromIndex;
            if (nElementsRemoved == 0) {
                return array;
            }
            
            if (nElementsRemoved == sourceArrayLength) {
                return FloatArray.empty;
            }
                       
            float[] newArray = new float[sourceArrayLength - nElementsRemoved];
            if (fromIndex > 0) {
                System.arraycopy(array, 0, newArray, 0, fromIndex);
            }
            if (toIndex < sourceArrayLength) {
                System.arraycopy(array, toIndex, newArray, fromIndex, sourceArrayLength - toIndex);
            }
                      
            return newArray;                                  
        }
       
        public static float[] replicate(int nCopies, float valueToReplicate) {
            if (nCopies <= 0) {
                return FloatArray.empty;
            }
            
            float[] result = new float[nCopies];
            Arrays.fill(result, valueToReplicate);
            
            return result;
        }
        
        /**        
         * Support for defining the Eq a => Eq (Array a) instance when a is in fact Float. This relies on the 
         * fact that the implementation of Eq Float is just java primitive float equality.
         */
        public static boolean equals(float[] array1, float[] array2) {
            return Arrays.equals(array1, array2);
        }
                    
        public static boolean notEquals(float[] array1, float[] array2) {
            return !equals(array1, array2);
        }
        
        /**
         * Support for defining the Ord a => Ord (Array a) instance when a is in fact Float. This relies on the 
         * fact that the implementation of Ord Float is just java primitive float comparison.
         * 
         * Comparison using lexicographic ordering with Ord Float used on elements.         
         * @param array1
         * @param array2
         * @return -1, 0, or 1.
         */
        public static int compare(float[] array1, float[] array2) {
            if (array1 == array2) {
                return 0;
            }
            
            int n = Math.min(array1.length, array2.length);
            
            for (int i = 0; i < n; ++i) {
                
                //note that Float.compare handles NaN, -0 etc properly
                int elementCompare = Float.compare(array1[i], array2[i]);
                if (elementCompare != 0) {
                    return elementCompare;
                }                                     
            }
            
            return compareInt(array1.length, array2.length);
        }        
        
        public static boolean lessThan(float[] array1, float[] array2) {
            return compare(array1, array2) < 0;
        }
        
        public static boolean lessThanEquals(float[] array1, float[] array2) {
            return compare(array1, array2) <= 0;
        }
        
        public static boolean greaterThanEquals(float[] array1, float[] array2) {
            return compare(array1, array2) >= 0;
        }
        
        public static boolean greaterThan(float[] array1, float[] array2) {
            return compare(array1, array2) > 0;
        }        
                                       
        public static float[] max(float[] array1, float[] array2) {
            return lessThanEquals(array1, array2) ? array2 : array1;                       
        }
        
        public static float[] min(float[] array1, float[] array2) {
            return lessThanEquals(array1, array2) ? array1 : array2;
        }                
        
        /** 
         * Note: this method relies on the fact that we know that Eq Float is java primitive comparison on floats.
         *    
         * @param array array to search for element in.     
         * @param element float to search for.
         * @return the (0-based) index of the first occurrence of element in the array or -1 if element does not occur.
         */
        public static int indexOf(float[] array, float element) {
            return indexOf(array, element, 0);           
        }
        
        /**         
         * @param array array to search for element in.
         * @param element float to search for.
         * @param fromIndex the (0-based) index to inclusively start the search from. There are no invalid values of fromIndex.
         *      If fromIndex < 0, 0 is assumed. If fromIndex >= length.array -1 will be returned.
         * @return the (0-based) index of the first occurrence of element in the array with index >= fromIndex or -1 if 
         *      element does not occur.
         */
        public static int indexOf(float[] array, float element, int fromIndex) {
                       
            for (int i = (fromIndex < 0 ? 0 : fromIndex), len = array.length; i < len; ++i) {
                
                if (array[i] == element) {
                    return i;
                }
            }
            return -1;
        }
        
        public static int lastIndexOf(float[] array, float element) {
            return lastIndexOf(array, element, array.length - 1);            
        }
        
        /**                
         * @param array array to search for element in.
         * @param element float to search for.
         * @param fromIndex There are no invalid values for fromIndex. If fromIndex < 0, then -1 is always returned.
         * @return the 0-based index of the last occurrence of element within array whose index is less than or equal to fromIndex
         *     or -1 if element does not occur.
         */
        public static int lastIndexOf(float[] array, float element, int fromIndex) {              
            if (fromIndex < 0) {
                return -1;
            }
                                               
            int actualFromIndex = Math.min(fromIndex, array.length -1);
            
            for (int i = actualFromIndex; i >= 0; --i) {
                if (array[i] == element) {
                    return i;
                }
            }
            
            return -1;
        }
        
        public static float[] replace(float[] array, float oldElementValue, float newElementValue) {
            
            if (oldElementValue == newElementValue) {
                return array;
            }
            
            int len = array.length;
            if (len == 0) {
                return FloatArray.empty;
            }
            
            float[] newArray = new float[len];
            
            for (int i = 0; i < len; ++i) {
                
                float currentVal = array[i];                
                newArray[i] = (currentVal == oldElementValue) ? newElementValue : currentVal;                
            }
            
            return newArray;
        }        
        
        public static float[] sort(float[] array) {
            if (array.length == 0) {
                return array;
            }
            
            float[] newArray = array.clone();            
            Arrays.sort(newArray);
            return newArray;
        }        
               
    }
    
    
    /**
     * Collection of static helper functions and fields for working with the Java double array type.    
     * 
     * Note, that this class is primarily intended to support the Array.cal module. In particular, most
     * of the operations below do not do destructive updates but rather make copies. Also, we can assume (in
     * most cases) that the array is not a null array.
     * 
     * @author Bo Ilic
     */
    public static final class DoubleArray {
        
        private DoubleArray() {}
    
        public static final double[] empty = new double[0];                    
        
        /**         
         * @param array
         * @return representation of the array for debugging purposes.
         */
        public static final String toString(double[] array) {
            if (array == null) {             
                return "null";
            }
            
            int len = array.length;
            if (len == 0) {
                return "[]";
            }
 
            StringBuilder result = new StringBuilder("[");           
            result.append(array[0]); 
            for (int i = 1; i < len; ++i) {
                result.append(", ");
                result.append(array[i]);
            }
            result.append("]");
            return result.toString();
        }              
        
        public static final double[] array1(double value) {
            return new double[] {value};
        }        
        
        public static final double[] array2(double value1, double value2) {
            return new double[] {value1, value2};
        }
        
        public static final double[] array3(double value1, double value2, double value3) {
            return new double[] {value1, value2, value3};
        }
        
        public static final double[] array4(double value1, double value2, double value3, double value4) {
            return new double[] {value1, value2, value3, value4};
        }
        
        public static final double[] array5(double value1, double value2, double value3, double value4, double value5) {
            return new double[] {value1, value2, value3, value4, value5};
        }
        
        public static final double[] array6(double value1, double value2, double value3, double value4, double value5, double value6) {
            return new double[] {value1, value2, value3, value4, value5, value6};
        }
        
        public static final double[] array7(double value1, double value2, double value3, double value4, double value5, double value6, double value7) {
            return new double[] {value1, value2, value3, value4, value5, value6, value7};
        }                                                    
        
        /**
         * Clones the array, replacing a null array with a zero-length array.
         * @param array may be null
         * @return will not be null
         */
        public static final double[] cloneReplacingNullArray (double[] array) {
            if (array == null) {
                return DoubleArray.empty;
            }
            return array.clone();
        }               
        
        public static final double[] listToArray(List<Double> list) {
            int size = list.size();
            if (size == 0) {
                return DoubleArray.empty;
            }
            
            double[] resultArray = new double[size];
            
            for (int i = 0; i < size; ++i) {
                resultArray[i] = list.get(i).doubleValue();
            }
            
            return resultArray;
        }
        
        /**
         * @param firstElement the first element of the array to be constructed.
         * @param list the remainder of the elements of the array.
         * @return the array.
         */
        public static final double[] listToArrayWithFirstElement(double firstElement, List<Double> list) {
            int size = list.size() + 1;
            
            double[] resultArray = new double[size];
            
            resultArray[0] = firstElement;
            
            for (int i = 1; i < size; ++i) {
                resultArray[i] = list.get(i-1).doubleValue();
            }
            
            return resultArray;
        }
        
        /**     
         * @param array
         * @return the returned List is backed by the double array and hence any modifications 
         *      to the double array will modify the list.
         */
        public static final List<Double> arrayToList(final double[] array) {
                  
            class DoubleList extends AbstractList<Double> implements RandomAccess {
                          
                @Override
                public Double get(int index) {
                    return Double.valueOf(array[index]);                  
                }
                    
                @Override
                public int size() {        
                    return array.length;
                }                           
            }
            
            return new DoubleList();        
        }    
        
        /** 
         * Note: in certain circumstances, this function does not return a copy of
         * the argument arrays, and hence should not be used in situations where
         * the argument arrays may be mutated.
         *         
         * @param array1
         * @param array2
         * @return double[] the concatenation of array1 with array2.
         *      the function return array2 if array1 is empty, and vice-versa.  
         * @throws NullPointerException if either array1 or array2 are null.
         */
        public static double[] concat(double[] array1, double[] array2) {
            
            int length1 = array1.length;
            if (length1 == 0)  {                
                return array2;
            } 
            
            final int length2 = array2.length;
            if (length2 == 0) {
                return array1;
            }
            
            final double[] resultArray = new double[length1 + length2];
            System.arraycopy(array1, 0, resultArray, 0, length1);
            System.arraycopy(array2, 0, resultArray, length1, length2);
            return resultArray;
        }
            
        /** 
         * Note: in certain circumstances, this function does not return a copy of
         * the argument arrays, and hence should not be used in situations where
         * the argument arrays may be mutated.
         *       
         * @param list of double[] Objects
         * @return concatenation of the double arrays in the List
         */
        public static double[] concatList(List<double[]> list) {            
            final int nArrays = list.size();
            
            switch (nArrays) {
            case 0:                          
                return DoubleArray.empty;
                
            case 1:                          
                return list.get(0);
                
            case 2:
                return concat(list.get(0), list.get(1));
                
            default:
                break;
            }
            
            int resultSize = 0;
            for (int i = 0; i < nArrays; ++i) {
                resultSize += list.get(i).length;
            }
            
            final double[] result = new double[resultSize];
            int copyStartPos = 0;
            for (int i = 0; i < nArrays; ++i) {
                final double[] currentdoubleArray = list.get(i);
                final int currentdoubleArrayLength = currentdoubleArray.length;
                System.arraycopy(currentdoubleArray, 0, result, copyStartPos, currentdoubleArrayLength);
                copyStartPos += currentdoubleArrayLength;
            }
            
            return result;
        }
        
        public static double[] reverse(double[] array) {
            int len = array.length;
            if (len == 0) {
                return DoubleArray.empty;
            }
            
            double[] resultArray = new double[len];            
            int lastIndex = len - 1;
            for (int i = 0; i < len; ++i) {
                resultArray[i] = array[lastIndex - i];
            }
            
            return resultArray;            
        }
        
        /**         
         * @param array
         * @param fromIndex inclusive 0-based index
         * @param toIndex exclusive 0-based index
         * @return copy of the array from fromIndex (inclusive) to toIndex (exclusive). 
         */
        public static double[] subArray(double[] array, int fromIndex, int toIndex) {
            
            int sourceArrayLength = array.length;
            if (fromIndex < 0) {
                throw new IndexOutOfBoundsException("fromIndex(" + fromIndex + ") < 0.");
            }
            if (toIndex > sourceArrayLength) {
                throw new IndexOutOfBoundsException("toIndex(" + toIndex + ") > array.length(" + sourceArrayLength + ").");
            }
            if (fromIndex > toIndex) {
                throw new IllegalArgumentException("fromIndex(" + fromIndex + ") >  toIndex(" + toIndex + ").");
            }
            
            int newArrayLength = toIndex - fromIndex;
            if (newArrayLength == 0) {
                return DoubleArray.empty;
            }
            
            if (newArrayLength == sourceArrayLength) {
                return array;
            }
            
            double[] newArray = new double[newArrayLength];
            System.arraycopy(array, fromIndex, newArray, 0, toIndex - fromIndex);
                      
            return newArray;
        } 
        
        /** 
         * Efficiently removes a range of elements from an array.
         * The returned array has (toIndex - fromIndex) fewer elements. 
         *  
         * @param array
         * @param fromIndex inclusive index of the array to start removal from
         * @param toIndex exclusive index of the array to stop removal at
         * @return a copy (if necessary) of array with indices from fromIndex to toIndex removed
         */
        public static double[] removeRange(double[] array, int fromIndex, int toIndex) {
            int sourceArrayLength = array.length;
            
            if (fromIndex < 0) {
                throw new IndexOutOfBoundsException("fromIndex(" + fromIndex + ") < 0.");
            }
            if (toIndex > sourceArrayLength) {
                throw new IndexOutOfBoundsException("toIndex(" + toIndex + ") > array.length(" + sourceArrayLength + ").");
            }            
            if (fromIndex > toIndex) {
                throw new IllegalArgumentException("fromIndex(" + fromIndex + ") >  toIndex(" + toIndex + ").");
            }
            
            int nElementsRemoved = toIndex - fromIndex;
            if (nElementsRemoved == 0) {
                return array;
            }
            
            if (nElementsRemoved == sourceArrayLength) {
                return DoubleArray.empty;
            }
                       
            double[] newArray = new double[sourceArrayLength - nElementsRemoved];
            if (fromIndex > 0) {
                System.arraycopy(array, 0, newArray, 0, fromIndex);
            }
            if (toIndex < sourceArrayLength) {
                System.arraycopy(array, toIndex, newArray, fromIndex, sourceArrayLength - toIndex);
            }
                      
            return newArray;                                  
        }
        
        public static double[] replicate(int nCopies, double valueToReplicate) {
            if (nCopies <= 0) {
                return DoubleArray.empty;
            }
            
            double[] result = new double[nCopies];
            Arrays.fill(result, valueToReplicate);
            
            return result;
        }
        
        /**        
         * Support for defining the Eq a => Eq (Array a) instance when a is in fact Double. This relies on the 
         * fact that the implementation of Eq Double is just java primitive double equality.
         */
        public static boolean equals(double[] array1, double[] array2) {
            return Arrays.equals(array1, array2);
        }
                    
        public static boolean notEquals(double[] array1, double[] array2) {
            return !equals(array1, array2);
        }
        
        /**
         * Support for defining the Ord a => Ord (Array a) instance when a is in fact Double. This relies on the 
         * fact that the implementation of Ord Double is just java primitive double comparison.
         * 
         * Comparison using lexicographic ordering with Ord Double used on elements.         
         * @param array1
         * @param array2
         * @return -1, 0, or 1.
         */
        public static int compare(double[] array1, double[] array2) {
            if (array1 == array2) {
                return 0;
            }
            
            int n = Math.min(array1.length, array2.length);
            
            for (int i = 0; i < n; ++i) {
                
                //note that Double.compare handles NaN, -0 etc properly
                int elementCompare = Double.compare(array1[i], array2[i]);
                if (elementCompare != 0) {
                    return elementCompare;
                }                                     
            }
            
            return compareInt(array1.length, array2.length);
        }        
        
        public static boolean lessThan(double[] array1, double[] array2) {
            return compare(array1, array2) < 0;
        }
        
        public static boolean lessThanEquals(double[] array1, double[] array2) {
            return compare(array1, array2) <= 0;
        }
        
        public static boolean greaterThanEquals(double[] array1, double[] array2) {
            return compare(array1, array2) >= 0;
        }
        
        public static boolean greaterThan(double[] array1, double[] array2) {
            return compare(array1, array2) > 0;
        }        
                                       
        public static double[] max(double[] array1, double[] array2) {
            return lessThanEquals(array1, array2) ? array2 : array1;                       
        }
        
        public static double[] min(double[] array1, double[] array2) {
            return lessThanEquals(array1, array2) ? array1 : array2;
        }                
        
        /** 
         * Note: this method relies on the fact that we know that Eq Double is java primitive comparison on doubles.
         *    
         * @param array array to search for element in.     
         * @param element double to search for.
         * @return the (0-based) index of the first occurrence of element in the array or -1 if element does not occur.
         */
        public static int indexOf(double[] array, double element) {
            return indexOf(array, element, 0);           
        }
        
        /**         
         * @param array array to search for element in.
         * @param element double to search for.
         * @param fromIndex the (0-based) index to inclusively start the search from. There are no invalid values of fromIndex.
         *      If fromIndex < 0, 0 is assumed. If fromIndex >= length.array -1 will be returned.
         * @return the (0-based) index of the first occurrence of element in the array with index >= fromIndex or -1 if 
         *      element does not occur.
         */
        public static int indexOf(double[] array, double element, int fromIndex) {
                       
            for (int i = (fromIndex < 0 ? 0 : fromIndex), len = array.length; i < len; ++i) {
                
                if (array[i] == element) {
                    return i;
                }
            }
            return -1;
        }
        
        public static int lastIndexOf(double[] array, double element) {
            return lastIndexOf(array, element, array.length - 1);            
        }
        
        /**                
         * @param array array to search for element in.
         * @param element double to search for.
         * @param fromIndex There are no invalid values for fromIndex. If fromIndex < 0, then -1 is always returned.
         * @return the 0-based index of the last occurrence of element within array whose index is less than or equal to fromIndex
         *     or -1 if element does not occur.
         */
        public static int lastIndexOf(double[] array, double element, int fromIndex) {              
            if (fromIndex < 0) {
                return -1;
            }
                                               
            int actualFromIndex = Math.min(fromIndex, array.length -1);
            
            for (int i = actualFromIndex; i >= 0; --i) {
                if (array[i] == element) {
                    return i;
                }
            }
            
            return -1;
        }
        
        public static double[] replace(double[] array, double oldElementValue, double newElementValue) {
            
            if (oldElementValue == newElementValue) {
                return array;
            }
            
            int len = array.length;
            if (len == 0) {
                return DoubleArray.empty;
            }
            
            double[] newArray = new double[len];
            
            for (int i = 0; i < len; ++i) {
                
                double currentVal = array[i];                
                newArray[i] = (currentVal == oldElementValue) ? newElementValue : currentVal;                
            }
            
            return newArray;
        }        
        
        public static double[] sort(double[] array) {
            if (array.length == 0) {
                return array;
            }
            
            double[] newArray = array.clone();            
            Arrays.sort(newArray);
            return newArray;
        }        
               
    }
    
    
    /**
     * Collection of static helper functions and fields for working with the Java Object array type.    
     * 
     * Note, that this class is primarily intended to support the Array.cal module. In particular, most
     * of the operations below do not do destructive updates but rather make copies. Also, we can assume (in
     * most cases) that the array is not a null array.
     * 
     * @author Bo Ilic
     */
    public static final class ObjectArray {
        
        private ObjectArray() {}
    
        public static final Object[] empty = new Object[0];                    
        
        /**         
         * @param array
         * @return representation of the array for debugging purposes.
         */
        public static final String toString(Object[] array) {
            if (array == null) {             
                return "null";
            }
            
            int len = array.length;
            if (len == 0) {
                return "[]";
            }
 
            StringBuilder result = new StringBuilder("[");           
            result.append(array[0]); 
            for (int i = 1; i < len; ++i) {
                result.append(", ");
                result.append(array[i]);
            }
            result.append("]");
            return result.toString();
        }
               
        public static final Object[] array1(Object value) {
            return new Object[] {value};
        }        
        
        public static final Object[] array2(Object value1, Object value2) {
            return new Object[] {value1, value2};
        }
        
        public static final Object[] array3(Object value1, Object value2, Object value3) {
            return new Object[] {value1, value2, value3};
        }
        
        public static final Object[] array4(Object value1, Object value2, Object value3, Object value4) {
            return new Object[] {value1, value2, value3, value4};
        }
        
        public static final Object[] array5(Object value1, Object value2, Object value3, Object value4, Object value5) {
            return new Object[] {value1, value2, value3, value4, value5};
        }
        
        public static final Object[] array6(Object value1, Object value2, Object value3, Object value4, Object value5, Object value6) {
            return new Object[] {value1, value2, value3, value4, value5, value6};
        }
        
        public static final Object[] array7(Object value1, Object value2, Object value3, Object value4, Object value5, Object value6, Object value7) {
            return new Object[] {value1, value2, value3, value4, value5, value6, value7};
        }                                                       
        
        /**
         * Clones the array, replacing a null array with a zero-length array.
         * @param array may be null
         * @return will not be null
         */
        public static final Object[] cloneReplacingNullArray (Object[] array) {
            if (array == null) {
                return ObjectArray.empty;
            }
            return array.clone();
        }              
        
        public static final Object[] listToArray(List<Object> list) {
            return list.toArray(ObjectArray.empty);            
        }
        
        /**
         * @param firstElement the first element of the array to be constructed.
         * @param list the remainder of the elements of the array.
         * @return the array.
         */
        public static final Object[] listToArrayWithFirstElement(Object firstElement, List<Object> list) {
            int size = list.size() + 1;
            
            Object[] resultArray = new Object[size];
            
            resultArray[0] = firstElement;
            
            for (int i = 1; i < size; ++i) {
                resultArray[i] = list.get(i-1);
            }
            
            return resultArray;
        }
        
        /**     
         * @param array
         * @return the returned List is backed by the Object array and hence any modifications 
         *      to the Object array will modify the list.
         */
        public static final List<Object> arrayToList(final Object[] array) {
            return Arrays.asList(array);              
        }    
        
        /** 
         * Note: in certain circumstances, this function does not return a copy of
         * the argument arrays, and hence should not be used in situations where
         * the argument arrays may be mutated.
         *         
         * @param array1
         * @param array2
         * @return Object[] the concatenation of array1 with array2.
         *      the function return array2 if array1 is empty, and vice-versa.  
         * @throws NullPointerException if either array1 or array2 are null.
         */
        public static Object[] concat(Object[] array1, Object[] array2) {
            
            int length1 = array1.length;
            if (length1 == 0)  {                
                return array2;
            } 
            
            final int length2 = array2.length;
            if (length2 == 0) {
                return array1;
            }
            
            final Object[] resultArray = new Object[length1 + length2];
            System.arraycopy(array1, 0, resultArray, 0, length1);
            System.arraycopy(array2, 0, resultArray, length1, length2);
            return resultArray;
        }
            
        /** 
         * Note: in certain circumstances, this function does not return a copy of
         * the argument arrays, and hence should not be used in situations where
         * the argument arrays may be mutated.
         *       
         * @param list of Object[] Objects
         * @return concatenation of the Object arrays in the List
         */
        public static Object[] concatList(List<Object[]> list) {            
            final int nArrays = list.size();
            
            switch (nArrays) {
            case 0:                          
                return ObjectArray.empty;
                
            case 1:                          
                return list.get(0);
                
            case 2:
                return concat(list.get(0), list.get(1));
                
            default:
                break;
            }
            
            int resultSize = 0;
            for (int i = 0; i < nArrays; ++i) {
                resultSize += list.get(i).length;
            }
            
            final Object[] result = new Object[resultSize];
            int copyStartPos = 0;
            for (int i = 0; i < nArrays; ++i) {
                final Object[] currentObjectArray = list.get(i);
                final int currentObjectArrayLength = currentObjectArray.length;
                System.arraycopy(currentObjectArray, 0, result, copyStartPos, currentObjectArrayLength);
                copyStartPos += currentObjectArrayLength;
            }
            
            return result;
        }
        
        public static Object[] reverse(Object[] array) {
            int len = array.length;
            if (len == 0) {
                return ObjectArray.empty;
            }
            
            Object[] resultArray = new Object[len];            
            int lastIndex = len - 1;
            for (int i = 0; i < len; ++i) {
                resultArray[i] = array[lastIndex - i];
            }
            
            return resultArray;            
        }
        
        /**         
         * @param array
         * @param fromIndex inclusive 0-based index
         * @param toIndex exclusive 0-based index
         * @return copy of the array from fromIndex (inclusive) to toIndex (exclusive). 
         */
        public static Object[] subArray(Object[] array, int fromIndex, int toIndex) {
            
            int sourceArrayLength = array.length;
            if (fromIndex < 0) {
                throw new IndexOutOfBoundsException("fromIndex(" + fromIndex + ") < 0.");
            }
            if (toIndex > sourceArrayLength) {
                throw new IndexOutOfBoundsException("toIndex(" + toIndex + ") > array.length(" + sourceArrayLength + ").");
            }
            if (fromIndex > toIndex) {
                throw new IllegalArgumentException("fromIndex(" + fromIndex + ") >  toIndex(" + toIndex + ").");
            }
            
            int newArrayLength = toIndex - fromIndex;
            if (newArrayLength == 0) {
                return ObjectArray.empty;
            }
            
            if (newArrayLength == sourceArrayLength) {
                return array;
            }
            
            Object[] newArray = new Object[newArrayLength];
            System.arraycopy(array, fromIndex, newArray, 0, toIndex - fromIndex);
                      
            return newArray;
        } 
        
        /** 
         * Efficiently removes a range of elements from an array.
         * The returned array has (toIndex - fromIndex) fewer elements. 
         *  
         * @param array
         * @param fromIndex inclusive index of the array to start removal from
         * @param toIndex exclusive index of the array to stop removal at
         * @return a copy (if necessary) of array with indices from fromIndex to toIndex removed
         */
        public static Object[] removeRange(Object[] array, int fromIndex, int toIndex) {
            int sourceArrayLength = array.length;
            
            if (fromIndex < 0) {
                throw new IndexOutOfBoundsException("fromIndex(" + fromIndex + ") < 0.");
            }
            if (toIndex > sourceArrayLength) {
                throw new IndexOutOfBoundsException("toIndex(" + toIndex + ") > array.length(" + sourceArrayLength + ").");
            }            
            if (fromIndex > toIndex) {
                throw new IllegalArgumentException("fromIndex(" + fromIndex + ") >  toIndex(" + toIndex + ").");
            }
            
            int nElementsRemoved = toIndex - fromIndex;
            if (nElementsRemoved == 0) {
                return array;
            }
            
            if (nElementsRemoved == sourceArrayLength) {
                return ObjectArray.empty;
            }
                       
            Object[] newArray = new Object[sourceArrayLength - nElementsRemoved];
            if (fromIndex > 0) {
                System.arraycopy(array, 0, newArray, 0, fromIndex);
            }
            if (toIndex < sourceArrayLength) {
                System.arraycopy(array, toIndex, newArray, fromIndex, sourceArrayLength - toIndex);
            }
                      
            return newArray;                                  
        }

        
        public static Object[] replicate(int nCopies, Object valueToReplicate) {
            if (nCopies <= 0) {
                return ObjectArray.empty;
            }
            
            Object[] result = new Object[nCopies];
            Arrays.fill(result, valueToReplicate);
            
            return result;
        }       
    }
    
    
    /**
     * Collection of static helper functions and fields for working with the Java CalValue array type.    
     * 
     * Note, that this class is primarily intended to support the Array.cal module. In particular, most
     * of the operations below do not do destructive updates but rather make copies. Also, we can assume (in
     * most cases) that the array is not a null array.
     * 
     * @author Bo Ilic
     */
    public static final class CalValueArray {
        
        private CalValueArray() {}
    
        public static final CalValue[] empty = new CalValue[0];                    
        
        /**         
         * @param array
         * @return representation of the array for debugging purposes.
         */
        public static final String toString(CalValue[] array) {
            if (array == null) {             
                return "null";
            }
            
            int len = array.length;
            if (len == 0) {
                return "[]";
            }
 
            StringBuilder result = new StringBuilder("[");           
            result.append(array[0]); 
            for (int i = 1; i < len; ++i) {
                result.append(", ");
                result.append(array[i]);
            }
            result.append("]");
            return result.toString();
        }              
        
        public static final CalValue[] array1(CalValue value) {
            return new CalValue[] {value};
        }        
        
        public static final CalValue[] array2(CalValue value1, CalValue value2) {
            return new CalValue[] {value1, value2};
        }
        
        public static final CalValue[] array3(CalValue value1, CalValue value2, CalValue value3) {
            return new CalValue[] {value1, value2, value3};
        }
        
        public static final CalValue[] array4(CalValue value1, CalValue value2, CalValue value3, CalValue value4) {
            return new CalValue[] {value1, value2, value3, value4};
        }
        
        public static final CalValue[] array5(CalValue value1, CalValue value2, CalValue value3, CalValue value4, CalValue value5) {
            return new CalValue[] {value1, value2, value3, value4, value5};
        }
        
        public static final CalValue[] array6(CalValue value1, CalValue value2, CalValue value3, CalValue value4, CalValue value5, CalValue value6) {
            return new CalValue[] {value1, value2, value3, value4, value5, value6};
        }
        
        public static final CalValue[] array7(CalValue value1, CalValue value2, CalValue value3, CalValue value4, CalValue value5, CalValue value6, CalValue value7) {
            return new CalValue[] {value1, value2, value3, value4, value5, value6, value7};
        }                                                        
        
        /**
         * Clones the array, replacing a null array with a zero-length array.
         * @param array may be null
         * @return will not be null
         */
        public static final CalValue[] cloneReplacingNullArray (CalValue[] array) {
            if (array == null) {
                return CalValueArray.empty;
            }
            return array.clone();
        }              
        
        public static final CalValue[] listToArray(List<CalValue> list) {
            return list.toArray(CalValueArray.empty);          
        }
        
        /**
         * @param firstElement the first element of the array to be constructed.
         * @param list the remainder of the elements of the array.
         * @return the array.
         */
        public static final CalValue[] listToArrayWithFirstElement(CalValue firstElement, List<CalValue> list) {
            int size = list.size() + 1;
            
            CalValue[] resultArray = new CalValue[size];
            
            resultArray[0] = firstElement;
            
            for (int i = 1; i < size; ++i) {
                resultArray[i] = list.get(i-1);
            }
            
            return resultArray;
        }
        
        /**     
         * @param array
         * @return the returned List is backed by the CalValue array and hence any modifications 
         *      to the CalValue array will modify the list.
         */
        public static final List<CalValue> arrayToList(final CalValue[] array) {
            return Arrays.asList(array);                               
        }    
        
        /** 
         * Note: in certain circumstances, this function does not return a copy of
         * the argument arrays, and hence should not be used in situations where
         * the argument arrays may be mutated.
         *         
         * @param array1
         * @param array2
         * @return CalValue[] the concatenation of array1 with array2.
         *      the function return array2 if array1 is empty, and vice-versa.  
         * @throws NullPointerException if either array1 or array2 are null.
         */
        public static CalValue[] concat(CalValue[] array1, CalValue[] array2) {
            
            int length1 = array1.length;
            if (length1 == 0)  {                
                return array2;
            } 
            
            final int length2 = array2.length;
            if (length2 == 0) {
                return array1;
            }
            
            final CalValue[] resultArray = new CalValue[length1 + length2];
            System.arraycopy(array1, 0, resultArray, 0, length1);
            System.arraycopy(array2, 0, resultArray, length1, length2);
            return resultArray;
        }
            
        /** 
         * Note: in certain circumstances, this function does not return a copy of
         * the argument arrays, and hence should not be used in situations where
         * the argument arrays may be mutated.
         *       
         * @param list of CalValue[] Objects
         * @return concatenation of the CalValue arrays in the List
         */
        public static CalValue[] concatList(List<CalValue[]> list) {            
            final int nArrays = list.size();
            
            switch (nArrays) {
            case 0:                          
                return CalValueArray.empty;
                
            case 1:                          
                return list.get(0);
                
            case 2:
                return concat(list.get(0), list.get(1));
                
            default:
                break;
            }
            
            int resultSize = 0;
            for (int i = 0; i < nArrays; ++i) {
                resultSize += list.get(i).length;
            }
            
            final CalValue[] result = new CalValue[resultSize];
            int copyStartPos = 0;
            for (int i = 0; i < nArrays; ++i) {
                final CalValue[] currentCalValueArray = list.get(i);
                final int currentCalValueArrayLength = currentCalValueArray.length;
                System.arraycopy(currentCalValueArray, 0, result, copyStartPos, currentCalValueArrayLength);
                copyStartPos += currentCalValueArrayLength;
            }
            
            return result;
        }
        
        public static CalValue[] reverse(CalValue[] array) {
            int len = array.length;
            if (len == 0) {
                return CalValueArray.empty;
            }
            
            CalValue[] resultArray = new CalValue[len];            
            int lastIndex = len - 1;
            for (int i = 0; i < len; ++i) {
                resultArray[i] = array[lastIndex - i];
            }
            
            return resultArray;            
        }
        
        /**         
         * @param array
         * @param fromIndex inclusive 0-based index
         * @param toIndex exclusive 0-based index
         * @return copy of the array from fromIndex (inclusive) to toIndex (exclusive). 
         */
        public static CalValue[] subArray(CalValue[] array, int fromIndex, int toIndex) {
            
            int sourceArrayLength = array.length;
            if (fromIndex < 0) {
                throw new IndexOutOfBoundsException("fromIndex(" + fromIndex + ") < 0.");
            }
            if (toIndex > sourceArrayLength) {
                throw new IndexOutOfBoundsException("toIndex(" + toIndex + ") > array.length(" + sourceArrayLength + ").");
            }
            if (fromIndex > toIndex) {
                throw new IllegalArgumentException("fromIndex(" + fromIndex + ") >  toIndex(" + toIndex + ").");
            }
            
            int newArrayLength = toIndex - fromIndex;
            if (newArrayLength == 0) {
                return CalValueArray.empty;
            }
            
            if (newArrayLength == sourceArrayLength) {
                return array;
            }
            
            CalValue[] newArray = new CalValue[newArrayLength];
            System.arraycopy(array, fromIndex, newArray, 0, toIndex - fromIndex);
                      
            return newArray;
        } 
        
        /** 
         * Efficiently removes a range of elements from an array.
         * The returned array has (toIndex - fromIndex) fewer elements. 
         *  
         * @param array
         * @param fromIndex inclusive index of the array to start removal from
         * @param toIndex exclusive index of the array to stop removal at
         * @return a copy (if necessary) of array with indices from fromIndex to toIndex removed
         */
        public static CalValue[] removeRange(CalValue[] array, int fromIndex, int toIndex) {
            int sourceArrayLength = array.length;
            
            if (fromIndex < 0) {
                throw new IndexOutOfBoundsException("fromIndex(" + fromIndex + ") < 0.");
            }
            if (toIndex > sourceArrayLength) {
                throw new IndexOutOfBoundsException("toIndex(" + toIndex + ") > array.length(" + sourceArrayLength + ").");
            }            
            if (fromIndex > toIndex) {
                throw new IllegalArgumentException("fromIndex(" + fromIndex + ") >  toIndex(" + toIndex + ").");
            }
            
            int nElementsRemoved = toIndex - fromIndex;
            if (nElementsRemoved == 0) {
                return array;
            }
            
            if (nElementsRemoved == sourceArrayLength) {
                return CalValueArray.empty;
            }
                       
            CalValue[] newArray = new CalValue[sourceArrayLength - nElementsRemoved];
            if (fromIndex > 0) {
                System.arraycopy(array, 0, newArray, 0, fromIndex);
            }
            if (toIndex < sourceArrayLength) {
                System.arraycopy(array, toIndex, newArray, fromIndex, sourceArrayLength - toIndex);
            }
                      
            return newArray;                                  
        }

        
        public static CalValue[] replicate(int nCopies, CalValue valueToReplicate) {
            if (nCopies <= 0) {
                return CalValueArray.empty;
            }
            
            CalValue[] result = new CalValue[nCopies];
            Arrays.fill(result, valueToReplicate);
            
            return result;
        }          
               
    }
    

    //END AUTOMATICALLY GENERATED CODE
    ////////////////////////////////////////////////////////////////////////////////////

}