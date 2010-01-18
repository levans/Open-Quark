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
 * MaybeValue.java
 * Created: Dec 9, 2005
 * By: Bo Ilic
 */

package org.openquark.cal.foreignsupport.module.Prelude;

/**
 * Class for supporting the instances of Inputable and Outputable for the Prelude.Maybe type.
 * 
 * @author Bo Ilic
 */
public abstract class MaybeValue<T> {
    
    /** to ensure no subclasses other than Nothing and Just */
    private MaybeValue() {}  
    
    /**     
     * @return makes a Nothing value.     
     */
    @SuppressWarnings("unchecked")
    public static <T> MaybeValue<T> makeNothing() {
        return (MaybeValue<T>)Nothing.NOTHING;
    }
    
    /**     
     * @param value
     * @return makes a Just value.
     */
    public static <T> MaybeValue<T> makeJust(T value) {
        return new Just<T>(value);
    }
    
    /**     
     * @param value
     * @return a Nothing value if value is null, otherwise a Just value holding the value argument.
     */   
    public static <T> MaybeValue<T> makeMaybe(T value) {
        if (value == null) {
            return makeNothing();
        } else {
            return makeJust(value);
        }
    }
     
    /**     
     * @return true for a Prelude.Nothing value, false for a Prelude.Just value.
     */
    public abstract boolean isNothing();
    
    /**     
     * @return true for a Prelude.Just value, false for a Prelude.Nothing value.
     */
    public abstract boolean isJust();
    
    /**     
     * @return null for a Nothing value and the held value for a Maybe value. Note that a Maybe value might hold a null so
     *   we can't use this method to distinguish between Nothing and Maybe null. Call isNothing or isJust to tell this.
     */
    public abstract T getValueField();
    
    /**
     * {@inheritDoc}
     * @return representation of the Maybe value suitable for use for debugging. 
     */    
    @Override
    public abstract String toString();
    
    /**
     * Supports the Inputable and Outputable instances of the Prelude.Maybe type in
     * their handling of the Prelude.Nothing data constructor.    
     * 
     * @author Bo Ilic
     */
    private static final class Nothing<T> extends MaybeValue<T> {
                             
        /** singleton instance of Nothing */
        private static final Nothing<Object> NOTHING = new Nothing<Object>();
        
        private Nothing () {
        }

        /** {@inheritDoc} */
        @Override
        public boolean isNothing() {           
            return true;
        }

        /** {@inheritDoc} */
        @Override
        public boolean isJust() {            
            return false;
        }

        /** {@inheritDoc} */
        @Override
        public T getValueField() {           
            return null;
        }
        
        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "Cal.Core.Prelude.Nothing";
        }
    }
    
    /**
     * Supports the Inputable and Outputable instances of the Prelude.Maybe type in
     * their handling of the Prelude.Just data constructor.
     *    
     * @author Bo Ilic
     */    
    private static final class Just<T> extends MaybeValue<T> {
              
        private final T value;
                       
        private Just(T value) {
            this.value = value;
        }
        
        /** {@inheritDoc} */
        @Override
        public boolean isNothing() {            
            return false;
        }

        /** {@inheritDoc} */       
        @Override
        public boolean isJust() {            
            return true;
        }        
                       
        /** {@inheritDoc} */
        @Override
        public T getValueField() {
            return value;
        }
        
        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "(Cal.Core.Prelude.Just " + value + ')';
        }        
    }

}