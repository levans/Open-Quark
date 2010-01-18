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
 * RTData.java
 * Created: Jan 22, 2002 at 2:16:12 PM 
 * By: Luke Evans 
 */
package org.openquark.cal.internal.runtime.lecc;

import java.math.BigInteger;

import org.openquark.cal.runtime.CalValue;


/**
 * @author Luke Evans
 *
 * Base class for boxed primitive types.
 */
public abstract class RTData extends RTValue {
    
    /**
     * Add a package scope constructor to limit subclasses.     
     */
    RTData() {
        // Constructor declared to limit subclasses.
    }
    
    /**
     * Unwind this object.
     * @param ec
     * @return RTValue
     */
    @Override
    protected final RTValue reduce(RTExecutionContext ec) {
        // Constructor applications are never reducible!
        return this;
    }          
    
    /**        
     * {@inheritDoc}
     */
    @Override
    public final int debug_getNChildren() {         
        return 0;
    }
    /** 
     * {@inheritDoc}
     */
    @Override
    public final CalValue debug_getChild(int childN) {     
        throw new IndexOutOfBoundsException();        
    }
    /**     
     * {@inheritDoc}
     */
    @Override
    public abstract String debug_getNodeStartText();          
    /**    
     * {@inheritDoc}
     */
    @Override
    public final String debug_getNodeEndText() {       
        return "";
    }
    /**  
     * {@inheritDoc}
     */
    @Override
    public final String debug_getChildPrefixText(int childN) {       
        throw new IndexOutOfBoundsException();            
    }                 
    
    
    // CAL_Short
    public static final class CAL_Short extends RTData {
        static private final CAL_Short[] cachedShorts = makeCache(); //holds -128 through 127 inclusive
        
        static private final CAL_Short[] makeCache() {
            CAL_Short[] cache = new CAL_Short[256];
            for (int i = 0, cacheSize = cache.length; i < cacheSize; i++) {
                cache[i] = new CAL_Short((short)(i - 128));
            }
            return cache;
        }           
        
        private final short shortValue;
        
        private CAL_Short(short value) {
            super();
            this.shortValue = value;
        }
        
        public static final CAL_Short make(short s) {
            if (s >= -128 && s <= 127) {
                return cachedShorts[s + 128];
            }
            return new CAL_Short(s);
        }
        
        @Override
        public final short getShortValue () {
            return shortValue;
        }
                      
        /**
         * See comment in RTValue.getOrdinalValue
         * @return the ordinal value associated with this instance
         */
        @Override
        public final int getOrdinalValue() {
            return shortValue;
        }               
        
        /**     
         * {@inheritDoc}
         */
        @Override
        public final String debug_getNodeStartText() {
            return Short.toString(shortValue);            
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public final DataType getDataType() {
            return DataType.SHORT;
        }        
    }
    
    // CAL_Byte
    public static final class CAL_Byte extends RTData {
        static private final CAL_Byte[] cachedBytes = makeCache(); //holds -128 through 127 inclusive
        
        static private final CAL_Byte[] makeCache() {
            CAL_Byte[] cache = new CAL_Byte[256]; 
            for (int i = 0, cacheSize = cache.length; i < cacheSize; i++) {
                cache[i] = new CAL_Byte((byte)(i - 128));
            }
            return cache;
        }                   
        
        private final byte byteValue;
        
        private CAL_Byte (byte value) {
            super();
            this.byteValue = value;
        }
        
        public final static CAL_Byte make(byte value) {
            return cachedBytes[(int)value + 128];
        }
                          
        @Override
        public final byte getByteValue () {
            return byteValue;
        }
        
        /**
         * See comment in RTValue.getOrdinalValue
         * @return the ordinal value associated with this instance
         */
        @Override
        public final int getOrdinalValue() {
            return byteValue;
        } 
        
        /**     
         * {@inheritDoc}
         */
        @Override
        public final String debug_getNodeStartText() {
            return Byte.toString(byteValue);            
        }                

        /**
         * {@inheritDoc}
         */
        @Override
        public final DataType getDataType() {
            return DataType.BYTE;
        }        
    }
    
    // CAL_Float
    public static final class CAL_Float extends RTData {
        private final float floatValue;
        
        public CAL_Float(float value) {
            super();
            this.floatValue = value;
        }
        
        public final static CAL_Float make(float value) {
            return new CAL_Float (value);
        }
        
        @Override
        public final float getFloatValue () {
            return floatValue;
        }                               
        
        /**     
         * {@inheritDoc}
         */
        @Override
        public final String debug_getNodeStartText() {
            return Float.toString(floatValue);            
        }          

        /**
         * {@inheritDoc}
         */
        @Override
        public final DataType getDataType() {
            return DataType.FLOAT;
        }        
    }
    
    // CAL_Long
    public static final class CAL_Long extends RTData {
        static private final CAL_Long[] cachedLongs = makeCache(); //holds -128 through 127 inclusive
        
        static private final CAL_Long[] makeCache() {
            CAL_Long[] cache = new CAL_Long[256];
            for (int i = 0, cacheSize = cache.length; i < cacheSize; i++) {
                cache[i] = new CAL_Long(i - 128);
            }
            return cache;
        }                 
        
        private final long longValue;
        
        private CAL_Long(long value) {
            super();
            this.longValue = value;
        }
        
        public static final CAL_Long make(long l) {
            if (l >= -128 && l <= 127) { 
                return cachedLongs[(int)l + 128];
            }
            return new CAL_Long(l);
        }
        
        @Override
        public final long getLongValue () {
            return longValue;
        }
                           
        /**     
         * {@inheritDoc}
         */
        @Override
        public final String debug_getNodeStartText() {
            return Long.toString(longValue);            
        }                 

        /**
         * {@inheritDoc}
         */
        @Override
        public final DataType getDataType() {
            return DataType.LONG;
        }        
    }
    
    // CAL_BigInteger
    public static final class CAL_Integer extends RTData {
        private BigInteger bigIntegerValue;
        
        public CAL_Integer (BigInteger value) {
            super();
            this.bigIntegerValue = value;
        }
        
        public final static CAL_Integer make(BigInteger value) {
            return new CAL_Integer (value);
        }
        
        @Override
        public final BigInteger getIntegerValue () {
            return bigIntegerValue;
        }
        
        /**
         * Get the object wrapped by this instance.
         * @return Object
         */
        @Override
        public final Object getOpaqueValue () {
            return bigIntegerValue;
        }
        
        /**     
         * {@inheritDoc}
         */
        @Override
        public final String debug_getNodeStartText() {
            return String.valueOf(bigIntegerValue);            
        }          

        /**
         * {@inheritDoc}
         */
        @Override
        public final DataType getDataType() {
            return DataType.OBJECT;
        }        
    }
    
    // CAL_Boolean
    public static abstract class CAL_Boolean extends RTData {
        
        public static final CAL_Boolean TRUE = new CAL_True();
        public static final CAL_Boolean FALSE = new CAL_False();
        
        @Override
        public abstract boolean getBooleanValue ();
        
        public final static CAL_Boolean make (boolean b) {
            if (b) {
                return TRUE;
            } else {
                return FALSE;
            }
        }
        
        public static final class CAL_True extends CAL_Boolean {
            
            private CAL_True() {
            }              
            
            @Override
            public final boolean getBooleanValue () {
                return true;
            }
                       
            /**
             * See comment in RTValue.getOrdinalValue
             * @return the ordinal value associated with this instance
             */
            @Override
            public final int getOrdinalValue() {
                return 1;
            } 
            
            /**     
             * {@inheritDoc}
             */
            @Override
            public final String debug_getNodeStartText() {
                return "true";            
            }               
        }
        
        public static final class CAL_False extends CAL_Boolean {           
            private CAL_False() {
            }            
            
            @Override
            public final boolean getBooleanValue () {
                return false;
            }           
            
            /**
             * See comment in RTValue.getOrdinalValue
             * @return the ordinal value associated with this instance
             */
            @Override
            public final int getOrdinalValue() {
                return 0;
            }           
            
            /**
             * @see org.openquark.cal.internal.runtime.lecc.RTValue#isLogicalTrue()
             */
            @Override
            public final boolean isLogicalTrue() {
                return false;
            }
            
            /**     
             * {@inheritDoc}
             */
            @Override
            public final String debug_getNodeStartText() {
                return "false";            
            }                                    
        }
        
        /**
         * Add a private constructor to prevent subclasses other than CAL_False and CAL_True.        
         */
        private CAL_Boolean() {
            // Constructor explicitly declared and made private to prevent instantiation.
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public final DataType getDataType() {
            return DataType.BOOLEAN;
        }        
    }
    
    /**
     * CAL_Integer implements integer data
     * @author LEvans
     */
    public static final class CAL_Int extends RTData {
        static private final CAL_Int[] cachedIntegers = makeCache(); //holds -128 through 127 inclusive
        
        static private final CAL_Int[] makeCache() {
            CAL_Int[] cache = new CAL_Int[256];
            for(int i = 0, cacheSize = cache.length; i < cacheSize; i++) {
                cache[i] = new CAL_Int(i - 128);
            }
            return cache;
        }
        
        private final int intValue;
        
        /**
         * Construct an CAL_Integer from its value
         * @param value the integer value
         */
        private CAL_Int(int value) {
            super();
            this.intValue = value;
        }
        
        public static final CAL_Int make(int i) {
            if (i >= -128 && i <= 127) {
                return cachedIntegers[i + 128];
            }
            return new CAL_Int(i);
        }
        
        /**
         * Return the integer value for this object.
         * @return int
         */
        @Override
        public final int getIntValue () {
            return intValue;
        }
        
        /**
         * See comment in RTValue.getOrdinalValue
         * @return the ordinal value associated with this instance
         */
        @Override
        public final int getOrdinalValue() {
            return intValue;
        }
        

        //we do not want the runtime to have smarts about the relation between the java types int and java.lang.Integer
        //(and similarly between the other primitive Java types and their canonical Java boxed representation).
        //We previously added such facilities so that certain constructions using Prelude.unsafeCoerce would work.
        //However, we decided to tighten the semantics of what unsafeCoerce can do- it cannot modify the value in any
        //way, it is only a directive for coercing the compiler to accept a potentially type-unsafe construct.
//        public final Object getOpaqueValue () {              
//            return JavaPrimitives.makeInteger(intValue);
//        }            
        
        /**     
         * {@inheritDoc}
         */
        @Override
        public final String debug_getNodeStartText() {
            return Integer.toString(intValue);            
        }         

        /**
         * {@inheritDoc}
         */
        @Override
        public final DataType getDataType() {
            return DataType.INT;
        }        
    }
    
    /**
     * CAL_String implements string data
     * @author RCypher
     */
    public static final class CAL_String extends RTData {
        private final String stringValue;
        /**
         * Construct an CAL_Integer from its value
         * @param value the integer value
         */
        public CAL_String (String value) {
            super();
            this.stringValue = value;
        }
        
        public static final CAL_String make(String value) {
            return new CAL_String (value);
        }            
        
        /**
         * Get the string value wrapped by this instance.
         * @return String
         */
        @Override
        public final String getStringValue() {
            return stringValue;
        }
        
        /**
         * Get the object wrapped by this instance.
         * @return Object
         */
        @Override
        public final Object getOpaqueValue () {
            return stringValue;
        }
        
        /**     
         * {@inheritDoc}
         */
        @Override
        public final String debug_getNodeStartText() {
            //note that the reason that we are not putting double quotes around the String i.e. "abc", is that the CAL_String type
            //does not *only* represent Prelude.String, but rather any type whose Java implementation type is the primitive type
            //java.lang.String. We highlight this difference by using the default java implementation of String.valueOf().
            //Note: if you want to change this in the future, to be consistent, one must change all the other places where
            //StringBuilder.append(String) is called, such as in the custom generated debug_getNodeStartText methods for data types and 
            //strict application nodes. Also, CAL_Opaque can sometimes hold a String value, so one needs to check and update that
            //case as well.            
            return String.valueOf(stringValue);            
        }         

        /**
         * {@inheritDoc}
         */
        @Override
        public final DataType getDataType() {
            return DataType.OBJECT;
        }        
    }
    
    /**
     * CAL_Double implements double precision floating point number data
     * @author LEvans
     */
    public static final class CAL_Double extends RTData {
               
        private final double doubleValue;
        
        /**
         * Construct an CAL_Integer from its value
         * @param value the integer value
         */
        public CAL_Double(double value) {
            super();
            this.doubleValue = value;
        }
        
        public static final CAL_Double make(double value) {
            return new CAL_Double(value);
        }
        
        /**
         * Get the double value wrapped by this instance.
         * @return double
         */
        @Override
        public final double getDoubleValue () {
            return doubleValue;
        }
               
        /**     
         * {@inheritDoc}
         */
        @Override
        public final String debug_getNodeStartText() {           
            return Double.toString(doubleValue);            
        }            

        /**
         * {@inheritDoc}
         */
        @Override
        public final DataType getDataType() {
            return DataType.DOUBLE;
        }        
    }
    
    public static final class CAL_Char extends RTData {
        
        static private final CAL_Char[] cachedCharacters = makeCache(); //holds 0 through 127 inclusive
        
        static private final CAL_Char[] makeCache() {
            CAL_Char[] cache = new CAL_Char[128];
            for (int i = 0, cacheSize = cache.length; i < cacheSize; i++) {
                cache[i] = new CAL_Char((char)i);
            }
            return cache;
        }
        
        private final char charValue;
        
        /**
         * Construct an CAL_Integer from its value
         * @param value the integer value
         */
        private CAL_Char (char value) {
            super();
            this.charValue = value;
        }
        
        public static final CAL_Char make(char c) {
            if (c <= 127) {
                return cachedCharacters[(int)c];
            }
            return new CAL_Char(c);
        }
        
        /**
         * Get the char value wrapped by this instance.
         * @return char
         */
        @Override
        public final char getCharValue () {
            return charValue;
        }
        
        /**
         * See comment in RTValue.getOrdinalValue
         * @return the ordinal value associated with this instance
         */
        @Override
        public final int getOrdinalValue () {
            return charValue;
        }                   
             
        /**     
         * {@inheritDoc}
         */
        @Override
        public final String debug_getNodeStartText() { 
            //note that the reason that we are not putting single quotes around the char i.e. 'a', is that the CAL_Char type
            //does not *only* represent Prelude.Char, but rather any type whose Java implementation type is the primitive type
            //char. We highlight this difference by using the default java.lang.Character.toString() implementation.
            //Note: if you want to change this in the future, to be consistent, one must change all the other places where
            //StringBuilder.append(char) is called, such as in the custom generated debug_getNodeStartText methods for data types and 
            //strict application nodes.            
            return Character.toString(charValue);            
        }         

        /**
         * {@inheritDoc}
         */
        @Override
        public final DataType getDataType() {
            return DataType.CHAR;
        }        
    }
    
    /**
     * CAL_Opaque implements the wrapper for opaque data types.
     * @author RCypher
     */
    public static final class CAL_Opaque extends RTData {
        private Object value;
        
        /**
         * Construct an CAL_Integer from its value
         * @param value the integer value
         */
        public CAL_Opaque (Object value) {
            super();
            this.value = value;
        }
        
        public static final CAL_Opaque make(Object value) {
            return new CAL_Opaque (value);
        }             
        
        /**
         * Get the object wrapped by this instance.
         * @return Object
         */
        @Override
        public final Object getOpaqueValue () {
            return value;
        }
        
        /**
         * The BigInteger value held by this CAL_Opaque. Normally these are cloaked in
         * CAL_Integer values, but sometimes not.
         * @return BigInteger
         */
        @Override
        public final BigInteger getIntegerValue () {
            return (BigInteger)value;
        } 
        
        /**
         * The BigInteger value held by this CAL_Opaque. Normally these are cloaked in
         * CAL_String values, but sometimes not.
         * @return BigInteger
         */
        @Override
        public final String getStringValue () {
            return (String)value;
        }
        
        /**     
         * {@inheritDoc}
         */
        @Override
        public final String debug_getNodeStartText() { 
            //note that the reason that we are not putting single quotes around the char i.e. 'a', is that the CAL_Char type
            //does not *only* represent Prelude.Char, but rather any type whose Java implementation type is the primitive type
            //char. We highlight this difference by using the default java.lang.Character.toString() implementation.
            //Note: if you want to change this in the future, to be consistent, one must change all the other places where
            //StringBuilder.append(char) is called, such as in the custom generated debug_getNodeStartText methods for data types and 
            //strict application nodes.            
            return String.valueOf(value);            
        }           

        /**
         * {@inheritDoc}
         */
        @Override
        public final DataType getDataType() {
            return DataType.OBJECT;
        }        
    }    
}
