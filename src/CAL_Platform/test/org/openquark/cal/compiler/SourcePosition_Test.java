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
 * SourcePosition_Test.java
 * Creation date: (Mar 4, 2006)
 * By: James Wright
 */
package org.openquark.cal.compiler;

import junit.framework.TestCase;

public class SourcePosition_Test extends TestCase {

    /**
     * Test that column handling for tabs works regardless of line number.
     */
    public void testLineIndependentColumnHandling() {
        
        SourcePosition firstStartPos = new SourcePosition(1, 28);
        SourcePosition secondStartPos = new SourcePosition(2, 28);
        SourcePosition firstEndPos = new SourcePosition(1, 54);
        SourcePosition secondEndPos = new SourcePosition(2, 54);
        
        String lineText = "instance Ident            (a\t\t\t->\t\t\tb) where";
        String lines = lineText + "\n" + lineText;
        
        assertEquals(lines.substring(firstStartPos.getPosition(lines), firstEndPos.getPosition(lines)),
                     lines.substring(secondStartPos.getPosition(lines), secondEndPos.getPosition(lines)));
    }
    
    /**
     * Test that getPosition will report a position that equals the length of a string
     * without throwing an exception.
     */
    public void testFinalPositionBoundsChecks() {
        
        SourcePosition endPosSimple = new SourcePosition(2, 4);
        assertEquals(24, endPosSimple.getPosition("123456789-123456789-\n123"));
        
        SourcePosition endPosWithTab = new SourcePosition(1, 5);
        assertEquals(1, endPosWithTab.getPosition("\t"));
        assertEquals(2, endPosWithTab.getPosition(" \t"));
        assertEquals(3, endPosWithTab.getPosition("  \t"));
        assertEquals(4, endPosWithTab.getPosition("   \t"));
        
    }
    
    /**
     * Test that getPosition will throw an error for SourcePositions that point beyond
     * the end of a string.
     */
    public void testOutOfBoundsChecks() {
        
        SourcePosition endPosFirstLine = new SourcePosition(1, 10);
        try {
            endPosFirstLine.getPosition("1234567");
            fail("this call should have thrown an IllegalArgumentException");
        } catch(IllegalArgumentException e) {
        }

        SourcePosition endPosSecondLine = new SourcePosition(2, 4);

        try {
            endPosSecondLine.getPosition("123456789-123456789-\n");
            fail("this call should have thrown an IllegalArgumentException");
        } catch(IllegalArgumentException e) {
        }
        
        try {
            endPosSecondLine.getPosition("123456789-");
            fail("this call should have thrown an IllegalArgumentException");
        } catch(IllegalArgumentException e) {
        }
    }
}
