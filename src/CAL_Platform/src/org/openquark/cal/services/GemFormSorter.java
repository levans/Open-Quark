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
 * GemFormSorter.java
 * Creation date: Oct 30, 2002.
 * By: Edward Lam
 */
package org.openquark.cal.services;

import java.util.Comparator;

import org.openquark.cal.compiler.ClassMethod;
import org.openquark.cal.compiler.DataConstructor;
import org.openquark.cal.compiler.Function;
import org.openquark.cal.compiler.FunctionalAgent;


/**
 * This sorter sorts GemEntities based on their form (ie. whether they are a supercombinator,
 * class method, data constructor, ...)
 * @author Edward Lam
 */
public class GemFormSorter extends GemComparatorSorter {

    /** A shared instance of the comparator. */
    private static final Comparator<GemEntity> sharedFormComparator = new FormComparator();

    /**
     * Constructor for GemFormSorter.
     */
    public GemFormSorter() {
        super(sharedFormComparator);
    }
}

/**
 * Comparator class used to compare form.
 * Note: In addition, the two groups will be sorted alphabetically by QualifiedName.
 */
class FormComparator implements Comparator<GemEntity> {

    public int compare(GemEntity e1, GemEntity e2) {

        // return the difference in form if not the same
        int formVal1 = getFormValue(e1.getFunctionalAgent());
        int formVal2 = getFormValue(e2.getFunctionalAgent());
        
        if (formVal1 != formVal2) {
            return formVal2 - formVal1;
        }
        
        // the secondary sort is on qualified name.
        String textValue1 = e1.getName().getQualifiedName();
        String textValue2 = e2.getName().getQualifiedName();

        return textValue1.compareTo(textValue2);
    }
        
    private int getFormValue(FunctionalAgent functionalAgent) {
        if (functionalAgent instanceof DataConstructor) {
            return 20;
        } else if (functionalAgent instanceof ClassMethod) {
            return 10;
        } else if (functionalAgent instanceof Function) {
            return 5;
        }
        return 0;
    }
}

