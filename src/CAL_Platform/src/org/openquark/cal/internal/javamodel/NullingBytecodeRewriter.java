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
 * NullingBytecodeRewriter.java
 * Created: October 15, 2007
 * By: Malcolm Sharpe
 */

package org.openquark.cal.internal.javamodel;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

/**
 * A command-line tool for rewriting Java bytecode to reduce memory usage
 * in certain cases. Supports rewriting individual class files and JARs.
 * See NullingClassAdapter for internal details.
 * 
 * @author Malcolm Sharpe
 */
public final class NullingBytecodeRewriter {

    /**
     * Run the NullingBytecodeRewriter from the command line.
     * This supports rewriting single class files or entire JAR
     * files.
     * 
     * @param args
     */
    public static void main(String[] args) throws Exception {
        if (args.length != 2) usage();
        
        if (args[0].endsWith(".class")) {
            rewriteClass(args[0], args[1]);
        } else if (args[0].endsWith(".jar")) {
            rewriteJar(args[0], args[1]);
        } else {
            usage();
        }
    }
    
    /**
     * Print usage information for running NullingBytecodeRewriter from
     * the command line.
     */
    private static void usage() {
        System.err.println("Usage: java org.openquark.cal.internal.javamodel.NullingBytecodeRewriter in_path out_path");
        System.err.println("  where");
        System.err.println("    in_path and out_path are paths to either class files or JAR files");
        System.exit(1);
    }
    
    /**
     * Rewrite a JAR file.
     * 
     * @param inputPath the path to the input JAR file.
     * @param outputPath the path to which to write the rewritten JAR file.
     * @throws IOException if there was an error reading or writing the JAR files.
     */
    public static void rewriteJar(String inputPath, String outputPath) throws IOException {
        ZipFile zf = new ZipFile(inputPath);
        Enumeration<? extends ZipEntry> e = zf.entries();
        
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(outputPath));
        
        while (e.hasMoreElements()) {
            ZipEntry entry = e.nextElement();
            ZipEntry newEntry = new ZipEntry(entry);
            
            InputStream is = zf.getInputStream(entry);
            
            if (entry.getName().endsWith(".class")) {
                byte[] rewritten = rewriteClass(is);
                
                newEntry.setSize(rewritten.length);
                newEntry.setCompressedSize(-1);
                zos.putNextEntry(newEntry);
                
                zos.write(rewritten);
            } else {
                newEntry.setCompressedSize(-1);
                zos.putNextEntry(newEntry);
                
                int b;
                while (-1 != (b = is.read())) {
                    zos.write(b);
                }
                is.close();
            }
        }
        
        zos.close();
    }
    
    /**
     * Rewrite a class file.
     * 
     * @param inputPath the path to the input class file.
     * @param outputPath the path to which to write the rewritten class file.
     * @throws IOException if there was an error reading or writing the class files.
     */
    public static void rewriteClass(String inputPath, String outputPath) throws IOException {
        byte[] result = rewriteClass(new FileInputStream(inputPath));
        
        FileOutputStream out = new FileOutputStream(outputPath);
        out.write(result);
        out.close();
    }

    /**
     * Rewrite a class read from the given input stream and return the rewritten bytes.
     * 
     * @param source a stream containing the input class.
     * @return the rewritten class bytecode.
     * @throws IOException if there was an error reading the class from the input stream. 
     */
    public static byte[] rewriteClass(InputStream source) throws IOException {
        ClassReader cr = new ClassReader(source);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS/* | ClassWriter.COMPUTE_FRAMES*/);
        ClassAdapter ca = new NullingClassAdapter(cw);
        cr.accept(ca, 0);
        
        return cw.toByteArray();
    }
    
}