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
 * MemoCache.java
 * Created: Apr 2, 2004
 * By: RCypher
 */

package org.openquark.cal.internal.foreignsupport.module.Memoize;

import org.openquark.cal.runtime.CalValue;

/**
 * NOTE: This class is for internal use only.
 * MemoCache is the class for holding memoized values for
 * non-CAF functions.
 *  The map field will always actually be an instance of Map.Map.
 *  
 * @author RCypher
 * Created: Apr 2, 2004
 *
 */
public class MemoCache {
    /** A CAL map of argument value to result value. */
    private CalValue map;
    
    /**
     * Create a MemoCache with an initial map.
     * @param map
     */
    public MemoCache (CalValue map) {
        this.map = map;
    }
    
    /**
     * @return the current CAL map
     */
    public CalValue getMap () {return map;}
    
    /**
     * Set a new CAL map.
     * @param map
     */
    public void setMap (CalValue map) {
        this.map = map;
    }
}
