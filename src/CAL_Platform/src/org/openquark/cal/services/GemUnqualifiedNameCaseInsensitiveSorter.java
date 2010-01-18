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
 * GemUnqualifiedNameCaseInsensitiveSorter.java
 * Creation date: Oct 30, 2002.
 * By: Edward Lam
 */
package org.openquark.cal.services;

import java.util.Comparator;

import org.openquark.cal.compiler.QualifiedName;


/**
 * This sorter sorts GemEntities based on their unqualified name.  It is case-insensitive.
 * @author Edward Lam
 */
public class GemUnqualifiedNameCaseInsensitiveSorter extends GemComparatorSorter {

    /** A shared instance of the comparator. */
    private static final Comparator<GemEntity> sharedUnqualifiedNameCaseInsensitiveComparator = new UnqualifiedNameCaseInsensitiveComparator();

    /**
     * Constructor for GemUnqualifiedNameCaseInsensitiveSorter.
     */
    public GemUnqualifiedNameCaseInsensitiveSorter() {
        super(sharedUnqualifiedNameCaseInsensitiveComparator);
    }
}

/**
 * A comparator on (case-insensitive) alphabetical order based on the unqualified name of the entity.
 * @author Edward Lam
 */
class UnqualifiedNameCaseInsensitiveComparator implements Comparator<GemEntity> {

    public int compare(GemEntity e1, GemEntity e2) {
        
        QualifiedName name1 = e1.getName();
        QualifiedName name2 = e2.getName();

        return name1.getUnqualifiedName().compareToIgnoreCase(name2.getUnqualifiedName());
    }
}

