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
 * NullingBytecodeRewriterTest.java
 * Created: October 15, 2007
 * By: Malcolm Sharpe
 */

package org.openquark.cal.internal.javamodel;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.openquark.cal.internal.javamodel.NullingBytecodeRewriter;
import org.openquark.cal.internal.javamodel.NullingClassAdapter;

import junit.framework.TestCase;

public class NullingBytecodeRewriterTest extends TestCase {

    private static String rewriterPackage = "org.openquark.cal.internal.javamodel";
    private static String packagePath = "org/openquark/cal/internal/javamodel";
    
    /**
     * Test that the given class crashes when run without rewriting.
     * @param className the name of the class to test.
     */
    private void failureTest(String className) {
        try {
            Runtime rt = Runtime.getRuntime();
            Process p = rt.exec("java -Xmx128m -cp test "+rewriterPackage+"."+className);
            
            assertTrue("program completed successfully", 0 != p.waitFor());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private void writeBytes(byte[] bytes, String path) throws IOException {
        File f = new File(path);
        File d = f.getParentFile();
        
        if (!d.exists()) {
            assertTrue("failed to create test binary directory", d.mkdirs());
        }
        
        FileOutputStream out = new FileOutputStream(path);
        out.write(bytes);
        out.close();
    }
    
    /**
     * Test that the given class runs successfully with rewriting and produces
     * the given line of output on stdout.
     * @param className the name of the class to test.
     * @param expectedOutput the expected output line.
     */
    private void successTest(String className, String expectedOutput) {
        try {
            byte[] result = NullingBytecodeRewriter.rewriteClass(new FileInputStream("test/"+packagePath+"/"+className+".class"));
            
            writeBytes(result, "testbin/"+packagePath+"/"+className+".class");
    
            Runtime rt = Runtime.getRuntime();
            Process p = rt.exec("java -Xmx128m -cp testbin "+rewriterPackage+"."+className);
            
            InputStreamReader esr = new InputStreamReader(p.getErrorStream());
            BufferedReader ebr = new BufferedReader(esr);
            
            String line = ebr.readLine();
            if (line != null) {
                fail(line);
            }
            
            InputStreamReader isr = new InputStreamReader(p.getInputStream());
            BufferedReader br = new BufferedReader(isr);
            assertEquals(expectedOutput, br.readLine());
            assertEquals(-1, br.read());
            
            // Ensure that the program completes successfully.
            assertEquals(0, p.waitFor());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private void assertBytesEqual(byte[] expected, byte[] actual) {
        assertEquals("lengths differ;", expected.length, actual.length);
        
        for (int i = 0; i < expected.length; i++) {
            assertEquals("difference at offset "+i+";", expected[i], actual[i]);
        }
    }
    
    /**
     * Test that the given class, when rewritten, is a fixed point for the
     * rewriter.
     * @param className the name of the class to test. 
     */
    private void fixedPointTest(String className) {
        try {
            byte[] result1 = NullingBytecodeRewriter.rewriteClass(new FileInputStream("test/"+packagePath+"/"+className+".class"));
            
            ByteArrayInputStream bais = new ByteArrayInputStream(result1);
            byte[] result2 = NullingBytecodeRewriter.rewriteClass(bais);
            
            assertBytesEqual(result1, result2);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Test that the given class, when rewritten without nulling this, is not any different.
     * @param className the name of the class to test.
     */
    private void unchangedTest(String className) {
        try {
            String path = "test/"+packagePath+"/"+className+".class";
            
            byte[] original = new byte[(int)new File(path).length()];
            
            InputStream is = new FileInputStream(path);
            assertEquals(original.length, is.read(original));
            is.close();
            
            NullingClassAdapter.nullThis = false;
            byte[] result = NullingBytecodeRewriter.rewriteClass(new FileInputStream(path));
            NullingClassAdapter.nullThis = true;
            
            writeBytes(result, "testbin/"+packagePath+"/"+className+".class");

            // For now, do not compare the individual bytes, since the ASM library
            // reorders various things in the class file without changing the meaning
            // or size of the file.
            assertEquals("lengths differ;", original.length, result.length);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public void testNoChange() {
        // Test that, when nulling is not required, no change is made.
        successTest("NoChange", "Hello World");
    }
    
    // A simple case with only local array references holding space.
    public void testLocalsOnly() {
        failureTest("LocalsOnly");
    }
    
    public void testLocalsOnlyRewritten() {
        successTest("LocalsOnly", Integer.valueOf(SIZE*2).toString());
    }
    
    public void testLocalsOnlyFixedPoint() {
        fixedPointTest("LocalsOnly");
    }
    
    // Object references.
    public void testObjectReferences() {
        failureTest("ObjectReferences");
    }
    
    public void testObjectReferencesRewritten() {
        successTest("ObjectReferences", Integer.valueOf(SIZE*2).toString());
    }
    
    public void testObjectReferencesFixedPoint() {
        fixedPointTest("ObjectReferences");
    }
    
    // A primitive argument in use.
    public void testPrimitiveArg() {
        failureTest("PrimitiveArg");
    }
    
    public void testPrimitiveArgRewritten() {
        successTest("PrimitiveArg", Integer.valueOf(SIZE*2+1).toString());
    }
    
    public void testPrimitiveArgFixedPoint() {
        fixedPointTest("PrimitiveArg");
    }
    
    // A 2-stack-slot wide primitive argument in use.
    public void testWidePrimitiveArg() {
        failureTest("WidePrimitiveArg");
    }

    public void testWidePrimitiveArgRewritten() {
        successTest("WidePrimitiveArg", Integer.valueOf(SIZE*2+1).toString());
    }
    
    public void testWidePrimitiveArgFixedPoint() {
        fixedPointTest("WidePrimitiveArg");
    }
    
    // An instance method.
    public void testInstanceMethod() {
        failureTest("InstanceMethod");
    }
    
    public void testInstanceMethodRewritten() {
        successTest("InstanceMethod", Integer.valueOf(SIZE*2).toString());
    }
    
    public void testInstanceMethodFixedPoint() {
        fixedPointTest("InstanceMethod");
    }
    
    // A case where 'this' must be nulled.
    public void testNullThis() {
        failureTest("NullThis");
    }
    
    public void testNullThisRewritten() {
        successTest("NullThis", Integer.valueOf(SIZE*2).toString());
    }
    
    public void testNullThisFixedPoint() {
        fixedPointTest("NullThis");
    }
    
    // A dead assignment, which requires some special case handling in the rewriter.
    public void testDeadAssignment() {
        failureTest("DeadAssignment");
    }
    
    public void testDeadAssignmentRewritten() {
        successTest("DeadAssignment", Integer.valueOf(SIZE+1).toString());
    }
    
    public void testDeadAssignmentFixedPoint() {
        fixedPointTest("DeadAssignment");
    }
    
    // A nulling occurring at the beginning of an if branch.
    public void testIfBranch() {
        failureTest("IfBranch");
    }
    
    public void testIfBranchRewritten() {
        successTest("IfBranch", Integer.valueOf(SIZE*2).toString());
    }
    
    public void testIfBranchFixedPoint() {
        fixedPointTest("IfBranch");
    }
    
    // A nulling occurring at the beginning of an else branch.
    // This is tricky since the nulling occurs at a label, but inserting
    // the nulling instructions _before_ the label will not work.
    public void testElseBranch() {
        failureTest("ElseBranch");
    }
    
    public void testElseBranchRewritten() {
        successTest("ElseBranch", Integer.valueOf(SIZE*2).toString());
    }
    
    public void testElseBranchFixedPoint() {
        fixedPointTest("ElseBranch");
    }
    
    // Check that the rewriter does not break when faced with try-catch blocks.
    public void testTryCatch() {
        failureTest("TryCatch");
    }
    
    public void testTryCatchRewritten() {
        successTest("TryCatch", Integer.valueOf(SIZE*3).toString());
    }
    
    public void testTryCatchFixedPoint() {
        fixedPointTest("TryCatch");
    }
    
    // Check that the rewriter does not break when faced with try-finally blocks.
    public void testTryFinally() {
        failureTest("TryFinally");
    }
    
    public void testTryFinallyRewritten() {
        successTest("TryFinally", Integer.valueOf(SIZE*2).toString());
    }
    
    public void testTryFinallyFixedPoint() {
        fixedPointTest("TryFinally");
    }
    
    // A loop.
    public void testLoop() {
        failureTest("Loop");
    }
    
    public void testLoopRewritten() {
        successTest("Loop", Integer.valueOf(SIZE*4).toString());
    }
    
    public void testLoopFixedPoint() {
        fixedPointTest("Loop");
    }
    
    // A simple program where the nulling-out work has already been done.
    public void testNullingDoneSimple() {
        unchangedTest("NullingDoneSimple");
    }
    
    // A leaf method where nulling-out is not required since no allocation might occur.
    public void testLeafMethod() {
        unchangedTest("LeafMethod");
    }
    
    // TODO: A test where javac reuses a local slot, if there exists such a program.
    //       I failed to create a test case for this.
    // TODO: A test where a method branches to its first instruction, such as having
    //       a do-while loop as the first statement in the method, with a non-argument object
    //       reference requiring nulling at the beginning of the loop. I created such a
    //       test case, but oddly it did not run out of memory when run without rewriting.
    
    // With a 128-megabyte heap, one int array of this size fits
    // in memory, but not two.
    public static final int SIZE = 30000000;
    
}

class NoChange {
    
    public static void main(String[] args) {
        System.out.println("Hello World");
    }
    
}

class LocalsOnly {
    
    public static void main(String[] args) {
        int result = 0;
        
        int[] a1 = new int[NullingBytecodeRewriterTest.SIZE];
        result += a1.length;

        int[] a2 = new int[NullingBytecodeRewriterTest.SIZE];
        result += a2.length;
        
        System.out.println(result);
    }
    
}

class ObjectReferences {
    
    public static void main(String[] args) {
        helper(new int[NullingBytecodeRewriterTest.SIZE]);
    }
    
    private static void helper(Object o1) {
        int result = 0;
        
        result += ((int[])o1).length;
        
        Object o2 = new int[NullingBytecodeRewriterTest.SIZE];
        result += ((int[])o2).length;
        
        System.out.println(result);
    }
    
}

class PrimitiveArg {
    
    public static void main(String[] args) {
        helper(1, new int[NullingBytecodeRewriterTest.SIZE]);
    }
    
    private static void helper(int result, int[] a1) {
        result += a1.length;
        
        int[] a2 = new int[NullingBytecodeRewriterTest.SIZE];
        result += a2.length;
        
        System.out.println(result);
    }
    
}

class WidePrimitiveArg {
    
    public static void main(String[] args) {
        helper(1.5, new int[NullingBytecodeRewriterTest.SIZE]);
    }
    
    private static void helper(double result, int[] a1) {
        result += a1.length;
        
        int[] a2 = new int[NullingBytecodeRewriterTest.SIZE];
        result += a2.length;
        
        System.out.println((int)result);
    }
    
}

class InstanceMethod {
    
    public static void main(String[] args) {
        new InstanceMethod().helper(new int[NullingBytecodeRewriterTest.SIZE]);
    }
    
    private void helper(int[] a1) {
        int result = 0;
        result += a1.length;
        
        int[] a2 = new int[NullingBytecodeRewriterTest.SIZE];
        result += a2.length;
        
        System.out.println(result);
    }
    
}

class NullThis {
    
    public static void main(String[] args) {
        new NullThis(new int[NullingBytecodeRewriterTest.SIZE]).helper();
    }
    
    private int[] a1;
    
    private NullThis(int[] a1) {
        this.a1 = a1;
    }
    
    private void helper() {
        int result = 0;
        result += a1.length;
        
        int[] a2 = new int[NullingBytecodeRewriterTest.SIZE];
        result += a2.length;
        
        System.out.println(result);
    }
    
}

class DeadAssignment {
    
    public static void main(String[] args) {
        int result = 0;
        
        int[] a1 = new int[NullingBytecodeRewriterTest.SIZE];

        int[] a2 = new int[NullingBytecodeRewriterTest.SIZE];
        result += a2.length;
        
        a1 = new int[1];
        result += a1.length;
        
        System.out.println(result);
    }
    
}

class IfBranch {
    
    public static void main(String[] args) {
        helper(true);
    }
    
    private static void helper(boolean cond) {
        int result = 0;
        
        int[] a1 = new int[NullingBytecodeRewriterTest.SIZE];
        result += a1.length;
        
        if (cond) {
            int[] a2 = new int[NullingBytecodeRewriterTest.SIZE];
            result += a2.length;
        } else {
            result += a1.length;
        }
        
        System.out.println(result);
    }
    
}

class ElseBranch {
    
    public static void main(String[] args) {
        helper(false);
    }
    
    private static void helper(boolean cond) {
        int result = 0;
        
        int[] a1 = new int[NullingBytecodeRewriterTest.SIZE];
        result += a1.length;
        
        if (cond) {
            result += a1.length;
        } else {
            int[] a2 = new int[NullingBytecodeRewriterTest.SIZE];
            result += a2.length;
        }
        
        System.out.println(result);
    }
    
}

class TryCatch {
    
    public static void main(String[] args) {
        int result = 0;
        int[] a1 = null;
        
        try {
            a1 = new int[NullingBytecodeRewriterTest.SIZE];
            result += a1.length;
            throw new Exception("foo");
        } catch (Exception e) {
            result += a1.length;
        }
        
        int[] a2 = new int[NullingBytecodeRewriterTest.SIZE];
        result += a2.length;
        
        System.out.println(result);
    }
    
}

class TryFinally {
    
    public static void main(String[] args) {
        int result = 0;
        int[] a1 = null;
        
        try {
            int[] a2 = new int[NullingBytecodeRewriterTest.SIZE];
            result += a2.length;
            a1 = new int[NullingBytecodeRewriterTest.SIZE];
        } finally {
            result += a1.length;
        }
        
        System.out.println(result);
    }
    
}

class Loop {
    
    public static void main(String[] args) {
        int result = 0;
        int[] a1 = new int[NullingBytecodeRewriterTest.SIZE];

        for (int i = 0; i < 2; i++) {
            result += a1.length;
            
            int[] a2 = new int[NullingBytecodeRewriterTest.SIZE];
            result += a2.length;
            
            a1 = new int[NullingBytecodeRewriterTest.SIZE];
        }
        
        System.out.println(result);
    }
    
}

class NullingDoneSimple {
    
    public static void main(String[] args) {
        args = null;
    }
    
}

class LeafMethod {
    
    public static void main(String[] args) {
        args = null;
        int foo = leaf(null);
        System.out.println(foo);
    }
    
    private static int leaf(int[] arg) {
        return 2;
    }
    
}