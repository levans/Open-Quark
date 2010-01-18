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
 * StyleClass.java
 * Creation date: Oct 13, 2005.
 * By: Joseph Wong
 */
package org.openquark.cal.caldoc;

/**
 * This class encapsulates the notion of a <i>style class</i> in HTML.
 *
 * @author Joseph Wong
 */
final class StyleClass {

    /** The HTML representation of the style class. */
    private final String htmlRep;
    
    /**
     * Constructs a style class based on its HTML representation.
     * @param htmlRep the HTML representation of the style class.
     */
    StyleClass(String htmlRep) {
        if (htmlRep == null) {
            throw new NullPointerException();
        }
        
        this.htmlRep = htmlRep;
    }
    
    /**
     * Constructs a compound style class.
     * @param baseStyleClass the base style class.
     * @param htmlRep the HTML representation of the child style class.
     */
    StyleClass(StyleClass baseStyleClass, String htmlRep) {
        if (baseStyleClass == null || htmlRep == null) {
            throw new NullPointerException();
        }
        
        this.htmlRep = baseStyleClass.htmlRep + " " + htmlRep;
    }
    
    /**
     * @return the HTML representation of the style class.
     */
    String toHTML() {
        return htmlRep;
    }
    
    /**
     * @return the CSS representation of the style class.
     */
    String toCSS() {
        return "." + htmlRep.replace(' ', '.');
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return htmlRep;
    }
}
