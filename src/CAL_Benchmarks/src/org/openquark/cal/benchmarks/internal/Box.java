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
 * Created on Feb 20, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.openquark.cal.benchmarks.internal;

import java.util.Iterator;
import java.util.List;

/**
 * @author RCypher
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Box {
    String stringVal;
    int intVals[];
    List<?> children;
    
    public Box (String s) {
        this.stringVal = s;
    }
    public Box (int[] i) {
        this.intVals = i;
    }
    public Box (List<?> l) {
        this.children = l;
    }
    
    static public Object makeStringBox(String s){
        return new Box(s);
    }
    static public Object makeIntBox(List<Integer> l) {
        int vals[] = new int[l.size()];
        int i = 0;
        for (Iterator<Integer> it = l.iterator(); it.hasNext(); i++) {
            vals[i] = (it.next()).intValue();
        }
        return new Box(vals);
    }
    static public Object makeBoxesBox(List<?> l) {
        return new Box(l);
    }
    
    @Override
    public String toString() {
        if (intVals != null) {
            StringBuffer sb = new StringBuffer();
            sb.append("IntBox [");
            for (int i = 0; i < intVals.length; ++i) {
                sb.append(intVals[i]);
                if (i < intVals.length-1) {
                    sb.append(", ");
                }
            }
            sb.append("]");
            return sb.toString();
        } else
        if (stringVal != null) {
            return "StringBox [" + stringVal + "]";
        } else 
        if (children != null) {
            StringBuffer sb = new StringBuffer();
            sb.append ("BoxesBox [");
            Iterator<?> it = children.iterator();
            while (it.hasNext()) {
                sb.append(it.next().toString());
                if (it.hasNext()) {
                    sb.append (", ");
                }
            }
            sb.append("] ");
            return sb.toString();
        } else {
            return "Bad Box Type";
        }
    }
}
