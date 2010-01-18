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
 * BytecodeDebuggingUtilities.java
 * Created: Jan 24, 2005
 * By: Bo Ilic
 */

package org.openquark.cal.internal.javamodel;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.BasicVerifier;
import org.objectweb.asm.util.ASMifierClassVisitor;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.TraceClassVisitor;
import org.openquark.util.FileSystemHelper;


/**
 * Contains various helpers for debugging Java bytecode. The bytecode could have been
 * generated via a variety of means e.g. using the ASMJavaByteCodeGenerator, or the javac compiler, 
 * (or the now-removed bcel based java bytecode generator).
 * 
 * These are essentially wrappers around the bytecode debugging facilities provided by ASM.
 * 
 * The use of these facilities are controlled by AsmJavaByteCodeGenerator.DEBUG_GENERATED_BYTECODE,
 * JavaBytecodeGenerator.DEBUG_GENERATED_BYTECODE and JavaSourceGenerator.DEBUG_GENERATED_BYTECODE.
 * 
 * A useful technique is to compare the disassembly produced by javac and (say) asm, on a folder by folder
 * basis using a differencing tools such as BeyondCompare.
 * 
 * @author Bo Ilic
 */
public class BytecodeDebuggingUtilities {
    
    /**
     * An ASMifierClassVisitor that can skip over the inner class annotations (denoted by visitInnerClass calls).
     * javac and the java compiler in eclipse compiler inner class annotations differently.
     * 
     * javac annotates for every inner class reference occurring within the class file e.g. a field declared of inner class type,
     * an instance of expression of inner class type, a throws declaration on a method where an inner class is thrown.
     * 
     * eclipse's java compiler annotates the class itself if it is an inner class, and all top-level inner classes within the class itself.
     * 
     * The problem with the javac approach is that one of the attributes passed is the modifiers that the inner class was declared with in its
     * enclosing class. These are not easily accessible from our java source model!
     * 
     * @author Bo Ilic
     */
    private static class FilteringASMifierClassVisitor extends ASMifierClassVisitor {
        
        private final boolean skipInnerClassAttributes;
        
        FilteringASMifierClassVisitor (PrintWriter pw, boolean skipInnerClassAttributes) {
            super(pw);
            
            this.skipInnerClassAttributes = skipInnerClassAttributes;
        }
        
        /* (non-Javadoc)
         * @see org.objectweb.asm.util.ASMifierClassVisitor#visitInnerClass(java.lang.String, java.lang.String, java.lang.String, int)
         */
        @Override
        public void visitInnerClass(String arg0, String arg1, String arg2, int arg3) {
            
            if (!skipInnerClassAttributes) {                
                super.visitInnerClass(arg0, arg1, arg2, arg3);
            }
        }
    }
    
    /**
     * A TraceClassVisitor that can skip over the inner class annotations.
     * javac and the java compiler in eclipse compiler inner class annotations differently.
     * 
     * @author Bo Ilic
     */
    private static class FilteringTraceClassVisitor extends TraceClassVisitor {
        
        private final boolean skipInnerClassAttributes;
        
        FilteringTraceClassVisitor (ClassVisitor cv, PrintWriter pw, boolean skipInnerClassAttributes) {
            super(cv, pw);
            
            this.skipInnerClassAttributes = skipInnerClassAttributes;
        }
        
        /* (non-Javadoc)
         * @see org.objectweb.asm.util.ASMifierClassVisitor#visitInnerClass(java.lang.String, java.lang.String, java.lang.String, int)
         */
        @Override
        public void visitInnerClass(String arg0, String arg1, String arg2, int arg3) {
            
            if (!skipInnerClassAttributes) {                
                super.visitInnerClass(arg0, arg1, arg2, arg3);
            }
        }
    }        
    
    /**
     * Generates a text file whose contents are the sequence of calls to the ASM library neeeded to generate the
     * given byteCode. Note: the bytecode need not have been generated using the ASM library.
     * @param pathName where to save the resulting asmified text
     * @param byteCode
     * @param skipDebugOpCodes it is useful to set this to true when comparing the output of javac, which contains considerable
     *          debug info, with that of direct bytecode generators, which contains very little.
     * @param skipInnerClassAttributes skip visiting the inner class attributes. These appear to be essentially ignored by the java
     *    runtime. javac and eclipse's java compiler handle them differently. ASM follows the pattern of eclipse's java compiler, which
     *    is easier to implement and also seems to make more sense.
     */
    public static void dumpAsmifiedText(String pathName, byte[] byteCode, boolean skipDebugOpCodes, boolean skipInnerClassAttributes) {
        
        ClassReader cr = new ClassReader(byteCode);
        
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(byteArrayOutputStream);               
        
        int flags = ClassReader.SKIP_FRAMES;
        if (skipDebugOpCodes) {
            flags |= ClassReader.SKIP_DEBUG;
        }
        cr.accept(new FilteringASMifierClassVisitor(pw, skipInnerClassAttributes), flags);
        
        pw.flush();        
        pw.close();  
        
        writeFile(pathName, byteArrayOutputStream.toByteArray());        
    }
    
    /**
     * Generates a text file containing the disassembly (into a byte-code assembly language) of some bytecode.
     * @param pathName where to save the resulting disassembly text
     * @param byteCode
     * @param skipDebugOpCodes  it is useful to set this to true when comparing the output of javac, which contains considerable
     *          debug info, with that of direct bytecode generators, which contains very little.
     * @param skipInnerClassAttributes skip visiting the inner class attributes. These appear to be essentially ignored by the java
     *    runtime. javac and eclipse's java compiler handle them differently. ASM follows the pattern of eclipse's java compiler, which
     *    is easier to implement and also seems to make more sense.     
     */
    public static void dumpDisassembledText(String pathName, byte[] byteCode, boolean skipDebugOpCodes, boolean skipInnerClassAttributes) {
        
        ClassReader cr = new ClassReader(byteCode);
        
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(byteArrayOutputStream);
               
        int flags = ClassReader.SKIP_FRAMES;
        if (skipDebugOpCodes) {
            flags |= ClassReader.SKIP_DEBUG;
        }
        cr.accept(new FilteringTraceClassVisitor(null, pw, skipInnerClassAttributes), flags);
        
        pw.flush();        
        pw.close();  
        
        writeFile(pathName, byteArrayOutputStream.toByteArray());        
    }
    
    private static void writeFile(String pathName, byte[] contents) {
        
        File file = new File(pathName);       
        
        OutputStream out = null;
        try {  
            
            File folder = file.getParentFile();
            FileSystemHelper.ensureDirectoryExists(folder);
            
            file.createNewFile();
            
            out = new BufferedOutputStream(new FileOutputStream(file));
            out.write(contents);                           
            
        } catch (FileNotFoundException e) {
            throw new RuntimeException ("Unable to find file: " + file.toString());
            
        } catch (IOException e) {
            throw new RuntimeException ("Error writing to file: " + file.toString());
            
        } finally {
            // The dump() method also closes the output stream, but this is bad practise.
            // Let's do the right thing and call close() here as well.
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ioe) {
                    // Nothing can really be done at this point, and we don't bother reporting the error.
                }
            }
        }
    }
    
    /**
     * Performs some basic verification tests on bytecode using ASMs BasicVerifier class.
     * Will generate a variety of exceptions if the verification tests fail.
     * 
     * One handy feature is that the dependee .class files do not need to actually exist, so this
     * verifier can be used in a dynamic context, however, it therefore doesn't do the full
     * amount of verification that, for example, ASM's SimpleVerifier class does or that
     * Sun's runtime does.
     * 
     * One interesting fact is that it *does* do some verification that Sun's verifier doesn't do. 
     * For example, Sun's verifier allows you to set the static modifier on a class access flag. This
     * is technically illegal (although it naturally arises when static inner classes are encoded).
     * 
     * @param pathName used only for display in error messages.
     * @param bytecode
     */
    public static void verifyClassFileFormat(String pathName, byte[] bytecode) {
        
        ClassReader cr = new ClassReader(bytecode);
                
        ClassNode classNode = new ClassNode();
        final int flags = 0;  // Don't skip anything.
        cr.accept(new CheckClassAdapter(classNode), flags);
        
        List<?> methods = classNode.methods;
        for (int i = 0; i < methods.size(); ++i) {
            MethodNode method = (MethodNode)methods.get(i);
            if (method.instructions.size() > 0) {
                Analyzer a = new Analyzer(new BasicVerifier());
                try {
                    a.analyze(classNode.name, method);
                    continue;
                } catch (Exception e) {
                    System.out.println("Problems found checking: " + pathName + ".");
                    e.printStackTrace();                    
                }        
            }
        }        
    }

}
