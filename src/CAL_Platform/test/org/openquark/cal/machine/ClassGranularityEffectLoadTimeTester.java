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
 * ClassGranularityEffectLoadTimeTester.java
 * Creation date: Jul 17, 2006.
 * By: Edward Lam
 */
package org.openquark.cal.machine;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;

import org.openquark.cal.services.FileSystemResourceHelper;
import org.openquark.util.FileChangeOutputStream;
import org.openquark.util.FileSystemHelper;
import org.openquark.util.TextEncodingUtilities;

/**
 * This is a helper class in determining whether loading many small classes is substantially different from loading the equivalent code from fewer large classes.
 * This class generates java source for classes falling into two cases:
 * 1) Many small classes.
 * 2) One large class.  This class contains the equivalent code to the classes generated in (1).
 * It is the intention that the generated classes for these two cases will have code that is roughly equivalent.
 * 
 * These classes are compiled, and packaged into a .jar file.
 * A single test method for (1) or (2) exercises all the generated code.  This is invoked when benchmarking over a number of runs.
 * 
 * @author Edward Lam
 */
/*
      Results (from July 19/06) -- executing from .jar files, no jvm args.
      System: Dual P4 Xeon, 2.4 Ghz, 2GB RAM, WinXP Pro SP2
      
      file sizes in bytes (not bytes on disk).
      Note: for "one compound", #file = 1; for "many simple", #files = scale + 1.
      
      jdk1.4.2_07
                                one compound                    many simple                     ratios             
        Scale   result          file size       time (ms)       file size       time (ms)       file size       time       
        100     4950            20,471          11              48,748          109             2.38132         9.909091           
        200     19900           40,915          18              97,820          207             2.39081         11.5       
        500     124750          102,415         45              245,120         493             2.393399        10.95556           
        1000    499500          204,915         95              490,620         988             2.394261        10.4       
        2000    1999000         412,915         237             986,620         2007            2.389402        8.468354           
        5000    12497500        1,036,915       835             2,474,620       5793            2.386522        6.937725         

      jdk1.6.0_beta2
        100     4950            20,471          5               48,748          110             2.38132         22         
        200     19900           40,915          9               97,820          207             2.39081         23         
        500     124750          102,415         37              245,120         474             2.393399        12.81081           
        1000    499500          204,915         61              490,620         945             2.394261        15.4918    
        2000    1999000         412,915         124             986,620         1911            2.389402        15.41129           
        5000    12497500        1,036,915       312             2,474,620       4863            2.386522        15.58654         

 */
public class ClassGranularityEffectLoadTimeTester {
    /** The scale of the generated classes.  This is the number of f() methods for each case. 
     *  The max number for this is somewhere between 5000-7500 before the generated Java code is too large. */
    private static final int SCALE = 3;
    
    /** The number of performance runs when benchmarking. */
    private static final int nRuns = 20;
    
    /** If true, the benchmark classloader will load generated class files from disk.  
     *  If false, the benchmark classloader will load generated class files from .jars. */
    private static final boolean benchmarkClassFiles = false;
    
    /** The folder under which the generated files will go. */
    private static final File workingFolder = new File("C:\\classSizeLoadingTest");

    /** Args to javac. */
    private static final String[] JAVAC_ARGS;
    static {
        String currentPath = System.getProperty("java.class.path", ".");

        JAVAC_ARGS = new String[] {
//                "-g:none",  // with this arg, generated class files will not have any debug info.
                "-target",
                "1.4",
                "-source",
                "1.4",
                "-classpath",
                currentPath
        };
    }

    /** System line separator */
    private static final String lineSeparator = System.getProperty("line.separator");
    
    /** The size of the indent in the source code for the generated classes. */
    private static final int INDENT_SIZE = 4;
    
    /** The package for the generated classes.  This *must* be a single package element - it must not contain dots. */
    private static final String packageName = "foo";
    private static final String packageDeclarationString = "package " + packageName + ";";
    
    /** The name of the one large class */
    private static final String compoundClassName = "AddCompound";
    /** The name of the outer class containing the many small inner classes. */
    private static final String smallClassesOuterClassName = "AddWithInner";
    
    /** The method object used to invoke the javac compiler on the generated sources. */
    private static Method compileMethod;
    static {
        // Initialize the compileMethod field.

        // First we need to get the compiler class.  i.e. com.sun.tools.javac.Main
        Class<?> compilerClass = null;

        // Start by trying to get the class from the current class loader.
        try {
            compilerClass = ClassGranularityEffectLoadTimeTester.class.getClassLoader().loadClass ("com.sun.tools.javac.Main");
        
        } catch (ClassNotFoundException e) {
            // The class is not on the current classpath.  Try to locate tools.jar based on the jre home
            // directory and use a URLClassLoader.
            String homeDirectory = System.getProperty("java.home");
            if (homeDirectory != null) {
                // In the standard JDC file layout tools.jar will be in the lib directory that is at the 
                // same level as the JRE home directory.

                String fileSeparator = System.getProperty("file.separator", "\\");
                // Trim the 'jre' off the home directory.
                homeDirectory = homeDirectory.substring(0, homeDirectory.lastIndexOf(fileSeparator));

                // Create the file for tools.jar and check if it exists.
                File toolsFile = new File(homeDirectory + fileSeparator + "lib" + fileSeparator + "tools.jar");
                if (FileSystemHelper.fileExists(toolsFile)) {
                    try {
                        // Since we've located tools.jar create a URLClassLoader with tools.jar as its path and 
                        // attempt to load the Main class.
                        URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{toolsFile.toURI().toURL()}, ClassGranularityEffectLoadTimeTester.class.getClassLoader());
                        compilerClass = urlClassLoader.loadClass("com.sun.tools.javac.Main");
                    } catch (MalformedURLException e2) {
                        // Simply fall through leaving compilerClass as null. 
                    } catch (ClassNotFoundException e2) {
                        // Simply fall through leaving compilerClass as null. 
                    }
                }
            }
        }

        if (compilerClass != null) {
            try {
                // First we try to get the version of compile that takes a PrintWriter to handle output messages.
                ClassGranularityEffectLoadTimeTester.compileMethod = compilerClass.getMethod("compile", new Class[]{(new String[]{}).getClass(), PrintWriter.class});
            } catch (NoSuchMethodException e) {
                // Simply fall through and leave compileMethod as null.
            }
        } else {
            System.err.println("Could not find javac.");
        }
    }
    
    /**
     * A simple container object to hold generated stats.
     * @author Edward Lam
     */
    private static class Stats {
        public final double mean;
        public final double sd;
        public final double sdPercent;
        public final double se;
        Stats(double mean, double sd, double sdPercent, double se) {
            this.mean = mean;
            this.sd = sd;
            this.sdPercent = sdPercent;
            this.se = se;
        }
        
        /**
         * {@inheritDoc}
         */
        
        public String toString() {
            return 
            "Mean:      " + mean + "\n" + 
            "Std dev:   " + sd + " (" + sdPercent + "%)\n" + 
            "Std error: " + se;
        }
    }
    
    /**
     * Calculate statistics for a set of numbers.
     * @param results the set of numbers.
     * @return the stats for those numbers.
     */
    private static Stats calculateStats(long[] results) {
        int nResults = results.length;
        
        double total = 0;
        for (int i = 0; i < nResults; i++) {
            total += results[i];
        }
        
        double mean = total/nResults;
        
        double stDev = 0.0;
        double stdDevPercent; 
        
        long sumDifsSquared = 0;
        for (int i = 0; i < nResults; i++) {
            double diff = ((double)results[i]) - mean;
            sumDifsSquared += (diff * diff);
        }
        if (nResults > 1) {
            stDev = sumDifsSquared / (nResults-1);
            stDev = Math.sqrt(stDev);

            stDev = ((int)(stDev * 100.0)) / 100.0;
            stdDevPercent = (stDev / (mean)) * 100.0;
            stdDevPercent = ((int)(stdDevPercent * 100.0)) / 100.0;
        } else {
            stDev = stdDevPercent = 0.0;            
        }
        double standardError = stDev / Math.sqrt(nResults);
        
        return new Stats(mean, stDev, stdDevPercent, standardError);

    }
    


    /**
     * Helper method to add an indent string to a stringBuilder
     * @param sb the string builder to which INDENT_SIZE spaces will be appended.
     */
    private static void indent(StringBuilder sb) {
        for (int i = 0; i < INDENT_SIZE; i++) {
            sb.append(' ');
        }
    }
    
    /**
     * Append a line of text to a string builder, followed by a newline.
     * 
     * @param sb the stringBuilder to which the line will be added.
     * @param indent the number of indents to precede the line text.
     * @param text the text of the line to be added.
     */
    private static void appendLine(StringBuilder sb, int indent, String text) {
        for (int i = 0; i < indent; i++) {
            indent(sb);
        }
        sb.append(text);
        sb.append(lineSeparator);
    }
    
    /**
     * Append a newline to a stringBuilder
     * @param sb the stringBuilder to which the newline will be appended.
     */
    private static void appendEmptyLine(StringBuilder sb) {
        sb.append(lineSeparator);
    }

    /*
        class AddCompound {
        
            int tag;
        
            static int value0 = 0;
            static int value1 = 1;
            static int value2 = 2;
        
            AddCompound(int tag) {
                this.tag = tag;
            }
        
            static int f0(int n) {
                return n + value0;
            }
            static int f1(int n) {
                return n + value1;
            }
            static int f2(int n) {
                return n + value2;
            }
            int f(int n){
                switch (tag) {
                    case 0: return f0(n);
                    case 1: return f1(n);
                    case 2: return f2(n);
                    default: throw new RuntimeException();
                }
            }
            public static int test() {
                int result = 0;
                result = (new AddCompound(0)).f(result);
                result = (new AddCompound(1)).f(result);
                result = (new AddCompound(2)).f(result);
                return result;
            }

        }
     */
    public static String getLargeClassText() {
        StringBuilder sb = new StringBuilder();
        
        // package declaration
        appendLine(sb, 0, packageDeclarationString);
        appendEmptyLine(sb);
        
        // class declaration
        appendLine(sb, 0, "public class " + compoundClassName + " {");
        appendEmptyLine(sb);

        {
            // int tag;
            appendLine(sb, 1, "private final int tag;");
            appendEmptyLine(sb);

            // static value tags.
            for (int i = 0; i < SCALE; i++) {
                appendLine(sb, 1, "static int value" + i + " = " + i + ";");
            }
            appendEmptyLine(sb);

            // constructor
            appendLine(sb, 1, "AddCompound(int tag) {");
            {
                appendLine(sb, 2, "this.tag = tag;");
            }
            appendLine(sb, 1, "}");
            appendEmptyLine(sb);

            // factory methods
            for (int i = 0; i < SCALE; i++) {
                appendLine(sb, 1, "public static AddCompound makeAddCompound" + i + "() {");
                {
                    appendLine(sb, 2, "return new AddCompound(" + i + ");");
                }
                appendLine(sb, 1, "}");
            }
            
            // f methods
            for (int i = 0; i < SCALE; i++) {
                appendLine(sb, 1, "static int f" + i + "(int n) {");
                {
                    appendLine(sb, 2, "return n + value" + i + ";");
                }
                appendLine(sb, 1, "}");
            }

            // dispatch method
            appendLine(sb, 1, "int f(int n){");
            {
                appendLine(sb, 2, "switch (tag) {");
                {
                    for (int i = 0; i < SCALE; i++) {
                        appendLine(sb, 3, "case " + i + ": return f" + i + "(n);");
                    }
                    appendLine(sb, 3, "default: throw new RuntimeException();");
                }
                appendLine(sb, 2, "}");
            }
            appendLine(sb, 1, "}");
            
            
            {
                // test method
                appendLine(sb, 1, "public static int test() {");
                {
                    appendLine(sb, 2, "int result = 0;");
                    for (int i = 0; i < SCALE; i++) {
                        appendLine(sb, 2, "result = makeAddCompound" + i + "().f(result);");
                    }
                    appendLine(sb, 2, "return result;");
                }
                appendLine(sb, 1, "}");
            }
        }
        
        appendLine(sb, 0, "}");
        
        return sb.toString();
    }
    
    /*
        public class AddWithInner {
        
            static class Add0 {
        
                static int value0 = 0;
        
                Add0() {
                }
        
                int f(int n) {
                    return n + value0;
                }
            }
            static class Add1 {
        
                static int value1 = 1;
        
                Add1() {
                }
        
                int f(int n) {
                    return n + value1;
                }
            }
            static class Add2 {
        
                static int value2 = 2;
        
                Add2() {
                }
        
                int f(int n) {
                    return n + value2;
                }
            }
        }
     */
    public static String getSmallClassesText() {
        StringBuilder sb = new StringBuilder();
        
        // package declaration
        appendLine(sb, 0, packageDeclarationString);
        appendEmptyLine(sb);
        
        // outer class declaration
        appendLine(sb, 0, "public class " + smallClassesOuterClassName + " {");
        appendEmptyLine(sb);

        // Inner classes
        for (int i = 0; i < SCALE; i++) {
            // inner class declaration
            appendLine(sb, 1, "static class Add" + i + " {");
            appendEmptyLine(sb);
            
            {
                // static value tag;
                appendLine(sb, 2, "static int value" + i + " = " + i + ";");
                appendEmptyLine(sb);
                
                // constructor
                appendLine(sb, 2, "Add" + i + "() {");
                appendLine(sb, 2, "}");
                appendEmptyLine(sb);
                
                // f method
                appendLine(sb, 2, "int f(int n) {");
                {
                    appendLine(sb, 3, "return n + value" + i + ";");
                }
                appendLine(sb, 2, "}");
            }
            
            appendLine(sb, 1, "}");
        }
        {
            // test method
            appendLine(sb, 1, "public static int test() {");
            {
                appendLine(sb, 2, "int result = 0;");
                for (int i = 0; i < SCALE; i++) {
                    appendLine(sb, 2, "result = (new Add" + i + "()).f(result);");
                }
                appendLine(sb, 2, "return result;");
            }
            appendLine(sb, 1, "}");
        }
        
        appendLine(sb, 0, "}");
        
        return sb.toString();
    }
    

    /**
     * Create the files and benchmark their performance.
     * @throws Exception
     */
    public static void createAndBenchmarkFiles() throws Exception {
        
        // Create the output folder.
        File outputFolder = new File(workingFolder, packageName);
        FileSystemHelper.delTree(outputFolder);
        outputFolder.mkdirs();
        
        if (!outputFolder.isDirectory()) {
            System.err.println("Could not create output folder: " + outputFolder);
            return;
        }
        
        System.out.println("Writing files to output folder: " + outputFolder);
        
        // Dump the files to the output folder.
        File compoundClassFile = new File(outputFolder, compoundClassName + ".java");
        File smallOuterClassesFile = new File(outputFolder, smallClassesOuterClassName + ".java");
        writeSourceFile(getLargeClassText(), compoundClassFile);
        writeSourceFile(getSmallClassesText(), smallOuterClassesFile);
        
        // Compile the files.
        System.out.println("Compiling files...");
        compileFiles(new File[] {compoundClassFile, smallOuterClassesFile});
        
        // Build the .jar file.
        System.out.println("Building .jar file...");
        File jarFile = new File(workingFolder, "blah.jar");
        buildJar(jarFile, outputFolder);
        
        String fullyQualifiedCompoundClassName = packageName + "." + compoundClassName;
        String fullyQualifiedSmallClassesOuterClassName = packageName + "." + smallClassesOuterClassName;

        long[] resultsFewCompound = new long[nRuns];
        long[] resultsManySimple = new long[nRuns];
        if (benchmarkClassFiles) {
            System.out.println("Executing using class files on disk...");
            URL workingFolderURL = workingFolder.toURI().toURL();
            for (int i = 0; i < nRuns; i++) {
                // Run each of the f methods a few times.
                runTestMethodWithTiming(workingFolderURL, fullyQualifiedCompoundClassName, "Iteration " + i + " few compound");
                runTestMethodWithTiming(workingFolderURL, fullyQualifiedSmallClassesOuterClassName, "Iteration " + i + " many simple ");
                System.out.println();
            }
        } else {
            System.out.println("Executing using jar files...");
            URL jarURL = jarFile.toURI().toURL();
            for (int i = 0; i < nRuns; i++) {
                // Run each of the f methods a few times.
                resultsFewCompound[i] = runTestMethodWithTiming(jarURL, fullyQualifiedCompoundClassName, "Iteration " + i + " few compound");
                resultsManySimple[i] = runTestMethodWithTiming(jarURL, fullyQualifiedSmallClassesOuterClassName, "Iteration " + i + " many simple ");
                System.out.println();
            }
        }
        System.out.println("Done.");
        
        System.out.println("Stats / few compound classes.");
        System.out.println(calculateStats(resultsFewCompound));
        System.out.println();
        System.out.println("Stats / many simple classes.");
        System.out.println(calculateStats(resultsManySimple));
    }
    
    private static long runTestMethodWithTiming(URL classpathURL, String fullyQualifiedClassName, String testCaseName) throws Exception{
        // Create a url classloader pointing to the classpath url.  Its parent is the current classloader.
        ClassLoader classLoader = new URLClassLoader(new URL[] {classpathURL}, ClassGranularityEffectLoadTimeTester.class.getClassLoader());

        // Time and run
        long before = System.currentTimeMillis();

        // Get the static no-arg method "test", and run it.
        Class<?> clazz = classLoader.loadClass(fullyQualifiedClassName);
        Method method = clazz.getMethod("test", (Class[])null);
        Object result = method.invoke(null, (Object[])null);

        long after = System.currentTimeMillis();
        System.out.println(testCaseName + ": "  + (after - before) + " ms.  Result: " + result);
        
        return (after - before);
    }
    
    /**
     * Write the given source into the given file.
     * @param source - String.  The generated source.
     * @param targetFile - File.  The target file.
     * @throws IOException
     */    
    private static void writeSourceFile(String source, File targetFile) throws IOException {
        
        InputStream inputStream = new ByteArrayInputStream(TextEncodingUtilities.getUTF8Bytes(source));

        // Handle null source.

        try {

            FileChangeOutputStream outputStream = null;
            try {
                // throws IOException:
                outputStream = new FileChangeOutputStream(targetFile);
                FileSystemResourceHelper.transferData(inputStream, outputStream);

            } finally {
                try {
                    if (outputStream != null) {
                        outputStream.close();
                    } 
                } catch (IOException ioe) {
                    // Nothing can really be done at this point, and we don't bother reporting the error.
                }                       
            }

        } finally {
            inputStream.close();
        }
    }

    /**
     * Compile source files
     * @param filesToCompile the files to compile.
     * @return the result code returned by the javac compile() method.
     */
    private static int compileFiles(File[] filesToCompile) throws Exception {

        // Set up the classpath for compiling this module.
        String fileNames[] = new String [filesToCompile.length];
        for (int i = 0; i < filesToCompile.length; i++) {
            fileNames[i] = filesToCompile[i].getPath();
        }

        // Set up the classpath arguments for the call to 'compile'. 
        String args[] = new String [fileNames.length + JAVAC_ARGS.length];
        System.arraycopy(JAVAC_ARGS, 0, args, 0, JAVAC_ARGS.length);
        System.arraycopy(fileNames, 0, args, JAVAC_ARGS.length, fileNames.length);

        // Wrap System.out with a print writer, and pass this in to the compile method so that compile errors are dumped.
        PrintWriter pw = new PrintWriter(System.out);

        // Invoke the compiler.
        Object compileResult = compileMethod.invoke(null, new Object[]{args, pw});

        // Close/flush the PrintWriter.
        pw.flush();
//        pw.close();

        // Extract and return the integer return code from the object result of 'invoke'.
        return ((Integer)compileResult).intValue();
    }
    
    /**
     * Build the .jar file from the class files in the output folder.
     * 
     * @param outputJarFile the jar file to create.
     * @param outputFolder the folder containing the class files to put in the jar file.
     * @throws FileNotFoundException
     * @throws IOException
     */
    private static void buildJar(File outputJarFile, File outputFolder) throws FileNotFoundException, IOException {
        FileSystemHelper.ensureDirectoryExists(outputFolder);
        File[] outputFolderFiles = outputFolder.listFiles();
        
        FileOutputStream outputStream = new FileOutputStream(outputJarFile);
        
        BufferedOutputStream bos = new BufferedOutputStream(outputStream, 1024);
        JarOutputStream jos = new JarOutputStream(bos, new Manifest());
        
        try {
            // Set compression level.
            jos.setLevel(Deflater.DEFAULT_COMPRESSION);
            
            if (outputFolderFiles != null) {
                
                for (final File outputFolderFile : outputFolderFiles) {
                    
                    String fileName = outputFolderFile.getName();
                    
                    if (fileName.endsWith(".class")) {
                        
                        // Write the zip entry.
                        ZipEntry zipEntry = new ZipEntry(packageName + "/" + fileName);
                        jos.putNextEntry(zipEntry);
                        
                        // Write the contents corresponding to the entry.
                        InputStream inputStream = new FileInputStream(outputFolderFile);
                        try {
                            FileSystemResourceHelper.transferData(inputStream, jos);
                        } finally {
                            inputStream.close();
                        }
                    }
                }
            }
            
        } finally {
            jos.flush();
            bos.flush();
            
            jos.close();
            bos.close();
        }
    }
    
    /**
     * Run target.
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        if (true) {
            createAndBenchmarkFiles();
        
        } else {
            dumpFilesToStdOut();
        }
    }
    
    /**
     * Dump the files to stdout.
     */
    public static void dumpFilesToStdOut() {
        System.out.println("Scale: " + SCALE);
        System.out.println(getLargeClassText());
        System.out.println(getSmallClassesText());
    }

}


