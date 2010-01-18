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
 * JavaReservedWords.java
 * Creation date: Nov 13, 2006.
 * By: Raymond Cypher
 */
package org.openquark.cal.internal.javamodel;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * This is a helper class used to hold the set of reserved Java words.
 * It is used to avoid naming conflicts with the reserved words.
 * @author Raymond Cypher
 */
public final class JavaReservedWords {
    
    /** Keywords in the Java language. These are not valid symbol names in generated Java source files. */
    static public final Set<String> javaLanguageKeywords;
    
    static {
        // We create a temporary Set and populate it with the
        // reserved Java words.  This is then used to create
        // an unmodifiable Set which will be exposed publicly.
        
        Set<String> tempSet = new HashSet<String>();
        tempSet.add ("abstract");
        tempSet.add ("assert"); //added in java 1.4
        tempSet.add ("boolean");
        tempSet.add ("break");
        tempSet.add ("byte");
        tempSet.add ("case");
        tempSet.add ("catch");
        tempSet.add ("char");
        tempSet.add ("class");
        tempSet.add ("const");
        tempSet.add ("continue");
        tempSet.add ("default");
        tempSet.add ("do");
        tempSet.add ("double");
        tempSet.add ("else");
        tempSet.add ("enum"); //added in java 5
        tempSet.add ("extends");
        tempSet.add ("final");
        tempSet.add ("finally");
        tempSet.add ("float");
        tempSet.add ("for");
        tempSet.add ("goto");
        tempSet.add ("if");
        tempSet.add ("implements");
        tempSet.add ("import");
        tempSet.add ("instanceof");
        tempSet.add ("int");
        tempSet.add ("interface");
        tempSet.add ("long");
        tempSet.add ("native");
        tempSet.add ("new");
        tempSet.add ("package");
        tempSet.add ("private");
        tempSet.add ("protected");
        tempSet.add ("public");
        tempSet.add ("return");
        tempSet.add ("short");
        tempSet.add ("static");
        tempSet.add ("strictfp");
        tempSet.add ("super");
        tempSet.add ("switch");
        tempSet.add ("synchronized"); 
        tempSet.add ("this");
        tempSet.add ("throw");
        tempSet.add ("throws");
        tempSet.add ("transient");
        tempSet.add ("try");
        tempSet.add ("void");
        tempSet.add ("volatile");
        tempSet.add ("while");
        
        //true, false, and null are not considered keywords, but they are considered reserved words and also can't be used
        tempSet.add ("true");
        tempSet.add ("false");
        tempSet.add ("null");        
        
        // Now create an immutable set that will be exposed to the public.
        javaLanguageKeywords = Collections.unmodifiableSet(tempSet);
    }  

    /**
     * Private constructor so that this class won't
     * be instantiated.
     */
    private JavaReservedWords () {}
}
