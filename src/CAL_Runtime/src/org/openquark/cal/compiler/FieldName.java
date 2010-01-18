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
 * FieldName.java
 * Created: Sept 28, 2004
 * By: Bo Ilic
 */
package org.openquark.cal.compiler;

import java.util.Comparator;


/**
 * Models field names in a CAL record or data constructor argument.
 * Field names are of 2 types: ordinal, such as #1, #2, #145 and textual, such as "name", "orderDate", "shipDate". 
 * @author Bo Ilic
 */
public abstract class FieldName implements Comparable<FieldName> {
            
    /**
     * Provides a comparator for field names in their CAL source form
     * as Strings. Ordinal fields are sorted by their int parts prior
     * to the textual fields, which are sorted alphabetically (via the 
     * default ordering on Strings).
     * @author Bo Ilic    
     */
    public static class CalSourceFormComparator implements Comparator<String> {
    
        public int compare(String s1, String s2) {            
                    
            if (s1.charAt(0) == '#') {
                
                if (s2.charAt(0) == '#') {
                    
                    int ordinal1 = Integer.parseInt(s1.substring(1));
                    int ordinal2 = Integer.parseInt(s2.substring(1));
                    
                    return (ordinal1 < ordinal2) ? -1 : (ordinal1 == ordinal2 ? 0 : 1);                   
                }
                
                return -1;
            }
            
            if (s2.charAt(0) == '#') {
                return 1;
            }
       
            return s1.compareTo(s2);            
        }
    }              

    /**
     * Ordinal field names, which always start with a # followed by a natural number
     * in CAL source.
     * @author Bo Ilic   
     */
    public static final class Ordinal extends FieldName {
        
        static final int serializationSchema = 0;
        
        /** must be greater than 0. */
        private final int ordinal;
                
        private static final Ordinal ORD_1 = new Ordinal(1);
        private static final Ordinal ORD_2 = new Ordinal(2);
        private static final Ordinal ORD_3 = new Ordinal(3);
        private static final Ordinal ORD_4 = new Ordinal(4);
        private static final Ordinal ORD_5 = new Ordinal(5);
        private static final Ordinal ORD_6 = new Ordinal(6);
        private static final Ordinal ORD_7 = new Ordinal(7);
        private static final Ordinal ORD_8 = new Ordinal(8);
        private static final Ordinal ORD_9 = new Ordinal(9);
        private static final Ordinal ORD_10 = new Ordinal(10);
        private static final Ordinal ORD_11 = new Ordinal(11);
        private static final Ordinal ORD_12 = new Ordinal(12);
        private static final Ordinal ORD_13 = new Ordinal(13);
        private static final Ordinal ORD_14 = new Ordinal(14);
        private static final Ordinal ORD_15 = new Ordinal(15);
        
        private Ordinal (int ordinal) {
            if (ordinal <= 0) {
                throw new IllegalArgumentException("ordinal must be greater than 0");
            }
            this.ordinal = ordinal;
        }
        
        private static Ordinal make(int ordinal) {
            
            switch (ordinal) {
                case 1: return ORD_1;
                case 2: return ORD_2;
                case 3: return ORD_3;
                case 4: return ORD_4;
                case 5: return ORD_5;
                case 6: return ORD_6;
                case 7: return ORD_7;
                case 8: return ORD_8;
                case 9: return ORD_9;
                case 10: return ORD_10;
                case 11: return ORD_11;
                case 12: return ORD_12;
                case 13: return ORD_13;
                case 14: return ORD_14;
                case 15: return ORD_15;
                
                default: return new Ordinal(ordinal);
            }
        }
        
        /**         
         * @param ordinalFieldName for example, "#123" or "#17"
         * @return true if ordinalFieldName is of the form ('#') ('1'..'9') ('0'..'9')* and the resulting
         *    int value is <= Integer.MAX_VALUE.
         */
        public static boolean isValidCalSourceForm (String ordinalFieldName) {
            
            if (ordinalFieldName == null ||
                ordinalFieldName.length() < 2 ||
                ordinalFieldName.charAt(0) != '#') {
                return false;
            }
            
            //verify the pattern (1-9)(0-9)*
            
            char c = ordinalFieldName.charAt(1);
            if (c < '1' || c > '9') {
                return false;
            }
            
            for (int i = 2, length = ordinalFieldName.length(); i < length; ++i) {
                c = ordinalFieldName.charAt(i);
                if (c < '0' || c > '9') {
                    return false;
                }
            }
            
            //verify that the resulting integer is in range e.g. <= Integer.MAX_VALUE.
            
            try {
                Integer.parseInt(ordinalFieldName.substring(1));
                return true;
            } catch (NumberFormatException nfe) {
                return false;                
            }           
        }
        
        public int getOrdinal() {
            return ordinal;
        }
        
        @Override
        public String getCalSourceForm() {
            return "#" + ordinal;
        }
        
        @Override
        public String toString () {
            return getCalSourceForm();
        }
        
        @Override
        public boolean equals (Object other) {
                       
            if (other instanceof Ordinal) {
                return ordinal == ((Ordinal)other).ordinal;
            }
            
            return false;                  
        }
        
        @Override
        public int hashCode () {
            return ordinal;
        }
        
        public int compareTo(FieldName other) {

            if (other instanceof Textual) {
                //ordinals are always less than textuals
                return -1;
            }
            
            if (other instanceof Ordinal) {                
                int otherOrdinal = ((Ordinal)other).ordinal;
                return ordinal < otherOrdinal ? -1 : (ordinal == otherOrdinal ? 0 : 1);
            }
            
            //incomparable types must throw a ClassCastException
            throw new ClassCastException();
        }
        
    }
    
    /**
     * Textual field names. These are lowercase starting valid CAL identifiers.
     * @author Bo Ilic    
     */
    public static final class Textual extends FieldName {
        
        private final String name;
        
        private Textual (String name) {
            if (name == null) {
                throw new NullPointerException();
            }
            this.name = name;
        }
        
        @Override
        public String getCalSourceForm() {
            return name;
        }
        
        /**         
         * @param textualFieldName for example, "orderDate"
         * @return true if textualFieldName is a valid textual field name. This is the same condition as being a valid
         *   function name i.e. starts with a lowercase letter, and not a keyword name.
         */
        public static boolean isValidCalSourceForm (String textualFieldName) {            
            return LanguageInfo.isValidFunctionName(textualFieldName);          
        }        
        
        @Override
        public String toString () {
            return getCalSourceForm();
        }
        
        @Override
        public boolean equals(Object other) {
            if (other instanceof Textual) {
                return name.equals(((Textual)other).name);
            }
            
            return false;
        }
        
        @Override
        public int hashCode () {
            return name.hashCode();
        }
        
        public int compareTo(FieldName other) {
            
            if (other instanceof Ordinal) {
                //textuals are always greater than ordinals
                return 1;
            }
            
            if (other instanceof Textual) {
                return name.compareTo(((Textual)other).name);
            }

            //incomparable types must throw a ClassCastException
            throw new ClassCastException();        
        }
    }
    
    /**     
     * @param calSourceFormFieldName fieldName as a String, e.g. "#2", "#454", "orderDate" etc.
     * @return FieldName null if calSourceFormFieldName is not a valid field name.
     */
    public static FieldName make(String calSourceFormFieldName) {
        
        if (!FieldName.isValidCalSourceForm(calSourceFormFieldName)) {
            return null;
        }
        
        if (calSourceFormFieldName.charAt(0) == '#'){           
            return Ordinal.make(Integer.parseInt(calSourceFormFieldName.substring(1)));
        }
               
        return new Textual(calSourceFormFieldName);       
    } 
    
    /**    
     * @return the field name in the form used in CAL source code e.g. #3, orderDate etc.
     */
    abstract public String getCalSourceForm();
        
    
    public static boolean isValidCalSourceForm (String fieldName) {            
        return Ordinal.isValidCalSourceForm(fieldName) || Textual.isValidCalSourceForm(fieldName);              
    }     
    
    public static FieldName.Ordinal makeOrdinalField(int ordinal) {
        return Ordinal.make(ordinal);
    }
    
    public static FieldName.Textual makeTextualField(String name) {
        if (!isValidCalSourceForm(name)) {
            return null;
        }
        return new Textual(name);
    }
}
