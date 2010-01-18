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
 * FixedSizeList_Test.java
 * Creation date: (Apr 27, 2005)
 * By: Jawright
 */
package org.openquark.cal.util;

import java.util.HashSet;
import java.util.List;

import junit.framework.TestCase;

import org.openquark.cal.util.FixedSizeList;
import org.openquark.util.FileSystemHelper;


/**
 * 
 * Creation date: (Apr 27, 2005)
 * @author Jawright
 */
public class FixedSizeList_Test extends TestCase {

    /**
     * Constructor for FixedSizeList_Test.
     * @param name the name of the test.
     */
    public FixedSizeList_Test(String name) {
        super(name);
    }

    /** Test that serialization and deserialization of FixedSizeLists works */
    public void testSerialization() {
        List<?> pair = FixedSizeList.make(new Integer(5), "five");
        List<?> triple = FixedSizeList.make(new Integer(6), "six", new Double("44"));
        List<?> tuple4 = FixedSizeList.make(new java.awt.Color(10,10,10), null, null, null);
        List<?> tuple5 = FixedSizeList.make(new java.awt.Color(10,10,10), null, null, null, new HashSet<Object>());
        List<?> tuple6 = FixedSizeList.make(new java.awt.Color(10,10,10), null, null, null, new HashSet<Object>(), null);
        List<?> tuple7 = FixedSizeList.make(new java.awt.Color(10,10,10), null, null, null, new HashSet<Object>(), null, new Integer(55));
        
        try {
            String tmpDir = System.getProperty("java.io.tmpdir");
            java.io.File d = new java.io.File(tmpDir, "FixedSizeList_Test.testSerialization");
            FileSystemHelper.ensureDirectoryExists(d);
            
            try {
                java.io.File f = new java.io.File(d, "serialization-test.bin");
                
                java.io.FileOutputStream fos = new java.io.FileOutputStream(f, false);
                try {
                    java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(fos);
                    oos.writeObject(pair);
                    oos.writeObject(triple);
                    oos.writeObject(tuple4);
                    oos.writeObject(tuple5);
                    oos.writeObject(tuple6);
                    oos.writeObject(tuple7);
                    oos.flush();
                    oos.close();
                    fos.flush();
                } finally {
                    fos.close();
                }
                
                java.io.FileInputStream fis = new java.io.FileInputStream(f);
                try {
                    java.io.ObjectInputStream ois = new java.io.ObjectInputStream(fis);
                    List<?> newPair = (List<?>)ois.readObject();
                    List<?> newTriple = (List<?>)ois.readObject();
                    List<?> newTuple4 = (List<?>)ois.readObject();
                    List<?> newTuple5 = (List<?>)ois.readObject();
                    List<?> newTuple6 = (List<?>)ois.readObject();
                    List<?> newTuple7 = (List<?>)ois.readObject();
                    
                    assertEquals(pair, newPair);
                    assertEquals(triple, newTriple);
                    assertEquals(tuple4, newTuple4);
                    assertEquals(tuple5, newTuple5);
                    assertEquals(tuple6, newTuple6);
                    assertEquals(tuple7, newTuple7);

                    ois.close();
                } finally {
                    fis.close();
                }
            } finally {
                FileSystemHelper.delTree(d);
            }

        } catch (java.io.FileNotFoundException e) {
            e.printStackTrace();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
