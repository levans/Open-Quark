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
 *     * Neither the name of Business Ts nor the names of its contributors
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
 * FixedSizeList.java
 * Creation date: May 26, 2005.
 * By: Joseph Wong
 */
package org.openquark.cal.foreignsupport.module.LegacyTuple;

import java.util.List;

/**
 * Provides factory methods for creating fixed sized lists with sizes
 * varying from 1 to 7.
 * 
 * @author Bo Ilic
 */
public abstract class FixedSizeList  {
          
   /**       
    * @return a two element fixed-size modifiable random-access list. The returned list is Serializable.
    */      
    public static final <T> List<T> make(T e0) {
        return org.openquark.cal.util.FixedSizeList.make(e0);
    }    
    
   /**       
    * @return a two element fixed-size modifiable random-access list. The returned list is Serializable.
    */      
    public static final <T> List<T> make(T e0, T e1) {
        return org.openquark.cal.util.FixedSizeList.make(e0, e1);
    }
    
    /**       
     * @return a three element fixed-size modifiable random-access list. The returned list is Serializable.
     */    
    public static final <T> List<T> make(T e0, T e1, T e2) {
        return org.openquark.cal.util.FixedSizeList.make(e0, e1, e2);
    }
    
    /**       
     * @return a four element fixed-size modifiable random-access list. The returned list is Serializable.
     */    
    public static final <T> List<T> make(T e0, T e1, T e2, T e3) {
        return org.openquark.cal.util.FixedSizeList.make(e0, e1, e2, e3);
    }
    
    /**       
     * @return a five element fixed-size modifiable random-access list. The returned list is Serializable.
     */    
    public static final <T> List<T> make(T e0, T e1, T e2, T e3, T e4) {
        return org.openquark.cal.util.FixedSizeList.make(e0, e1, e2, e3, e4);
    }
    
    /**       
     * @return a six element fixed-size modifiable random-access list. The returned list is Serializable.
     */    
    public static final <T> List<T> make(T e0, T e1, T e2, T e3, T e4, T e5) {
        return org.openquark.cal.util.FixedSizeList.make(e0, e1, e2, e3, e4, e5);
    }
    
    /**       
     * @return a seven element fixed-size modifiable random-access list. The returned list is Serializable.
     */    
    public static final <T> List<T> make(T e0, T e1, T e2, T e3, T e4, T e5, T e6) {
        return org.openquark.cal.util.FixedSizeList.make(e0, e1, e2, e3, e4, e5, e6);
    }   
}
