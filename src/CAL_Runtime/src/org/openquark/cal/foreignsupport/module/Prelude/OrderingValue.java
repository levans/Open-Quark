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
 * OrderingValue.java
 * Created: Dec 13, 2005
 * By: Bo Ilic
 */

package org.openquark.cal.foreignsupport.module.Prelude;

/**
 * Base class for supporting the instances of Inputable and Outputable for the Prelude.Ordering type.
 * 
 * This is a type-safe enum pattern with values LT, EQ, GT.
 * 
 * @author Bo Ilic
 */
public final class OrderingValue implements Comparable<OrderingValue> {
    
    private final String dataConstructorNameAsString;
    
    /** -1 for LT, 0 for EQ, 1 for GT */
    private final int orderingAsInt;
    
     
    public static final OrderingValue LT = new OrderingValue("Cal.Core.Prelude.LT", -1);
    public static final OrderingValue EQ = new OrderingValue("Cal.Core.Prelude.EQ", 0);
    public static final OrderingValue GT = new OrderingValue("Cal.Core.Prelude.GT", 1);
           
    private OrderingValue(String dataConstructorName, int orderingAsInt) {       
        this.dataConstructorNameAsString = dataConstructorName;
        this.orderingAsInt = orderingAsInt;        
    }
    
    public static OrderingValue fromInt(int intValue) {
        return (intValue < 0) ? LT : (intValue == 0 ? EQ : GT);       
    }
    
    /**     
     * @return -1 for LT, 0 for EQ, 1 for GT.
     */
    public int toInt() {
        return orderingAsInt;
    }          
     
    /**
     * {@inheritDoc}
     * @return representation of the Ordering value suitable for use for debugging. 
     */ 
    @Override
    public String toString() {
        return dataConstructorNameAsString;
    }
     
    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return orderingAsInt;
    }

    /** {@inheritDoc} */
    public int compareTo(OrderingValue o) {
        int otherOrderingAsInt = o.orderingAsInt;        
        return (orderingAsInt < otherOrderingAsInt) ? -1 : (orderingAsInt == otherOrderingAsInt ? 0 : 1);               
    }
        
}
