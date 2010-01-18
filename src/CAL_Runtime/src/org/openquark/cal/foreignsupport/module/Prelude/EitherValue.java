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
 * EitherValue.java
 * Created: Dec 13, 2005
 * By: Bo Ilic
 */

package org.openquark.cal.foreignsupport.module.Prelude;


/**
 * Class for supporting the instances of Inputable and Outputable for the Prelude.Either type.
 * 
 * @author Bo Ilic
 */
public abstract class EitherValue<L, R>  {
        
    private EitherValue() {}  
    
    public static <L, R> EitherValue<L, R> makeLeft(L value) {
        return new Left<L, R>(value);
    }
    
    public static <L, R> EitherValue<L, R> makeRight(R value) {
        return new Right<L, R>(value);
    }    
                
    public abstract boolean isLeft();    
     
    public abstract boolean isRight();
    
    /**     
     * @return the value field from either the left or right value. Use isLeft or isRight to tell which it is.
     */           
    public abstract Object getValueField();
    
    /**
     * {@inheritDoc}
     * @return representation of the Either value suitable for use for debugging. 
     */
    @Override
    public abstract String toString();  
    
    private static final class Left<L, R> extends EitherValue<L, R> {
        
        private final L leftValue;
               
        private Left(L leftValue) {
            this.leftValue = leftValue;
        }             

        /** {@inheritDoc} */
        @Override
        public boolean isLeft() {          
            return true;
        }

        /** {@inheritDoc} */
        @Override
        public boolean isRight() {           
            return false;
        }
        
        /** {@inheritDoc} */        
        @Override
        public final L getValueField() {
            return leftValue;
        }            

        /** {@inheritDoc} */
        @Override
        public String toString() {            
            return new StringBuilder("(Cal.Core.Prelude.Left ").append(leftValue).append(')').toString();
        }        
    }
    
    private static final class Right<L, R> extends EitherValue<L, R> {
        
        private final R rightValue;
              
        private Right(R rightValue) {
            this.rightValue = rightValue;
        }
        
        /** {@inheritDoc} */
        @Override
        public boolean isLeft() {           
            return false;
        }

        /** {@inheritDoc} */
        @Override
        public boolean isRight() {          
            return true;
        } 
        
        /** {@inheritDoc} */          
        @Override
        public final R getValueField() {
            return rightValue;
        }            
        
        /** {@inheritDoc} */
        @Override
        public String toString() {            
            return new StringBuilder("(Cal.Core.Prelude.Right ").append(rightValue).append(')').toString();
        }                     
    }
}
