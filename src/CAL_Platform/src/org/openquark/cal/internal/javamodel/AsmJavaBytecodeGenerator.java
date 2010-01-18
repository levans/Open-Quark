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
 * AsmJavaBytecodeGenerator.java
 * Created: Dec 21, 2004
 * By: Bo Ilic
 */

package org.openquark.cal.internal.javamodel;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.openquark.cal.internal.javamodel.JavaExpression.ArrayAccess;
import org.openquark.cal.internal.javamodel.JavaExpression.ArrayCreationExpression;
import org.openquark.cal.internal.javamodel.JavaExpression.ArrayLength;
import org.openquark.cal.internal.javamodel.JavaExpression.Assignment;
import org.openquark.cal.internal.javamodel.JavaExpression.CastExpression;
import org.openquark.cal.internal.javamodel.JavaExpression.ClassInstanceCreationExpression;
import org.openquark.cal.internal.javamodel.JavaExpression.ClassLiteral;
import org.openquark.cal.internal.javamodel.JavaExpression.InstanceOf;
import org.openquark.cal.internal.javamodel.JavaExpression.JavaField;
import org.openquark.cal.internal.javamodel.JavaExpression.LiteralWrapper;
import org.openquark.cal.internal.javamodel.JavaExpression.LocalName;
import org.openquark.cal.internal.javamodel.JavaExpression.LocalVariable;
import org.openquark.cal.internal.javamodel.JavaExpression.MethodInvocation;
import org.openquark.cal.internal.javamodel.JavaExpression.MethodVariable;
import org.openquark.cal.internal.javamodel.JavaExpression.OperatorExpression;
import org.openquark.cal.internal.javamodel.JavaExpression.PlaceHolder;
import org.openquark.cal.internal.javamodel.JavaStatement.AssertStatement;
import org.openquark.cal.internal.javamodel.JavaStatement.Block;
import org.openquark.cal.internal.javamodel.JavaStatement.Comment;
import org.openquark.cal.internal.javamodel.JavaStatement.ExpressionStatement;
import org.openquark.cal.internal.javamodel.JavaStatement.IfThenElseStatement;
import org.openquark.cal.internal.javamodel.JavaStatement.LabelledContinue;
import org.openquark.cal.internal.javamodel.JavaStatement.LocalVariableDeclaration;
import org.openquark.cal.internal.javamodel.JavaStatement.ReturnStatement;
import org.openquark.cal.internal.javamodel.JavaStatement.SwitchStatement;
import org.openquark.cal.internal.javamodel.JavaStatement.SynchronizedMethodInvocation;
import org.openquark.cal.internal.javamodel.JavaStatement.ThrowStatement;
import org.openquark.cal.internal.javamodel.JavaStatement.UnconditionalLoop;
import org.openquark.cal.internal.javamodel.JavaStatement.SwitchStatement.IntCaseGroup;
import org.openquark.cal.internal.runtime.lecc.LECCMachineConfiguration;



/**
 * A class to generate Java bytecode given an object model representation of Java source.
 * This class makes use of the ASM bytecode generator library (http://asm.objectweb.org/). This is
 * a BSD-licensed library and is thread safe (see the FAQ on the ASM website for the precise limitations
 * of this).
 * 
 * <P>The original Java source model to bytecode generator was written by Edward Lam used the BCEL library.
 * Some differences between ASM and BCEL:
 * -ASM is lower level, requiring us to specify the precise byte-code instruction to encode.
 * -ASM is thread-safe, BCEL is not.
 * -ASM requires that byte codes be generated in order. BCEL creates intermediate instruction list objects that can be 
 *   reordered and further manipulated before being converted to byte codes.
 * -ASM is significantly faster than BCEL
 * -After its implementation, the ASM bytecode generator was further refined to make the byte code it
 * produces closer to what javac produces. There were quite a few optimizations added. While in principle
 * these could also be done with the BCEL generator, they were not. 
 * 
 * <P>The BCEL generator was then deleted to remove the necessity of including the BCEL jars in the
 * distribution of CAL. It can be restored from the file JavaByteCodeGenerator.java if needed.
 * 
 * @author Bo Ilic
 */
public final class AsmJavaBytecodeGenerator {
     
    /**
     * Holds information for describing the context for code generation for a method. 
     * For example, it contains information about method variables, local variables, and labels (to continue to) in scope.
     * @author Bo Ilic
     */
    private static final class GenerationContext {
        
        /** The parent context. */
        private final GenerationContext parentContext;
        
        /** use this MethodVisitor for encoding the generated byte codes. */
        private final MethodVisitor mv;              
        
        /** the next local variable will be at this index in the JVM frame. It may take 1 or 2 slots, depending on it type. */
        private int nextAvailableVarIndex;
        
        /** map from local var name to its index in the JVM frame. */
        private final Map<String, Integer> localVarNameToIndexMap;
        
        /** map from method var name to its index in the JVM frame. */
        private final Map<String, Integer> methodVarToIndexMap;
        
        /** map from method var name to its type. Includes "this" for non-static methods. */
        private final Map<String, JavaTypeName> methodVarToTypeMap;
        
        /** map from a label name to the corresponding Label object. Used to implement labelled continue statements. */
        private final Map<String, Label> labelNameToLabelMap;
        
        /** Labels in this context, or in descendant contexts. */
        private final Set<Label> containedStatementEscapeLabelSet;
        
        /** (List of JumpReturnLabelInfo) Info about relevant return and jump instruction labels enclosed by this context, 
         *  in the order in which they appear.  */
        private final List<JumpReturnLabelInfo> jumpReturnLabelList = new ArrayList<JumpReturnLabelInfo>();
        
        /** JavaTypeName of the class being generated. */
        private final JavaTypeName classTypeName;

        /**
         * Constructor for the outermost GenerationContext for a method.
         * @param methodVarToTypeMap
         * @param methodVarToIndexMap
         * @param nextAvailableVarIndex
         * @param mv
         * @param classTypeName
         */
        GenerationContext(Map<String, JavaTypeName> methodVarToTypeMap, Map<String, Integer> methodVarToIndexMap, int nextAvailableVarIndex, MethodVisitor mv, JavaTypeName classTypeName) {
            this.parentContext = null;
            this.methodVarToTypeMap = methodVarToTypeMap;
            this.methodVarToIndexMap = methodVarToIndexMap;
            this.nextAvailableVarIndex = nextAvailableVarIndex;
            this.mv = mv;
            this.classTypeName = classTypeName;
            
            this.localVarNameToIndexMap = new HashMap<String, Integer>();
            this.labelNameToLabelMap = new HashMap<String, Label>();
            this.containedStatementEscapeLabelSet = new HashSet<Label>();
        }
        
        /**
         * Constructor for a GenerationContext from a parent context.
         * @param parentContext
         */
        GenerationContext(GenerationContext parentContext) {            
            this.parentContext = parentContext;
            this.methodVarToTypeMap = parentContext.methodVarToTypeMap; 
            this.methodVarToIndexMap = parentContext.methodVarToIndexMap;                      
            this.nextAvailableVarIndex = parentContext.nextAvailableVarIndex;                       
            this.mv = parentContext.mv;
            this.classTypeName = parentContext.classTypeName;
            
            this.localVarNameToIndexMap = new HashMap<String, Integer>(parentContext.localVarNameToIndexMap);
            this.labelNameToLabelMap = new HashMap<String, Label>(parentContext.labelNameToLabelMap);
            this.containedStatementEscapeLabelSet = new HashSet<Label>();
        }                      
        
        int addLocalVar(String localVarName, JavaTypeName localVarType) {
            
            if (localVarNameToIndexMap.put(localVarName, Integer.valueOf(nextAvailableVarIndex)) != null) {
                throw new IllegalArgumentException("Duplicate local variable declaration: " + localVarName);
            }
            
            int indexOfVar = nextAvailableVarIndex;              
            nextAvailableVarIndex += getTypeSize(localVarType);
            
            return indexOfVar;
        }
        
        int getLocalVarIndex(String varName) {
            Integer index = localVarNameToIndexMap.get(varName);
            if (index != null) {
                return index.intValue();
            }
            
            return -1;
        }            

        /**
         * Add a label at the scope of this context for a jump instruction generated for the purposes of statement control flow.
         * (not for exception labels, nor for jumps resulting from boolean valued operator evaluation, etc.).
         * @param label the label to add
         */
        void addStatementJumpLabel(Label label) {
            // add the label to this context and parent contexts.
            for (GenerationContext context = GenerationContext.this; context != null; context = context.parentContext) {
                context.containedStatementEscapeLabelSet.add(label);
            }
        }
        
        /**
         * Add a named label at the scope of this context for a jump instruction generated for the purposes of statement control flow.
         * (not for exception labels, nor for jumps resulting from boolean valued operator evaluation, etc.).
         * @param labelName the name of the label
         * @param label the label to add
         */
        void addStatementJumpLabel(String labelName, Label label) {
            if (labelNameToLabelMap.put(labelName, label) != null) {
                throw new IllegalArgumentException("Repeated addition of a label in a single context: " + labelName);
            }
            addStatementJumpLabel(label);
        }
        
        Label getNamedStatementJumpLabel(String labelName) {
            return labelNameToLabelMap.get(labelName);
        }
        
        /**
         * @param label the label to find
         * @return true if both of these conditions apply:
         * 1) it exists within the scope of this context (ie. in this context or child contexts).
         * 2) it is a label for a jump instruction whose destination lies outside the scope of the associated statement.
         */
        boolean containsStatementJumpLabel(Label label) {
            return containedStatementEscapeLabelSet.contains(label);
        }
        
        /**
         * Add a JumpReturnLabelInfo generated (in order) within the scope of this context.
         * @param jumpReturnLabelInfo the info to add
         */
        void addJumpReturnLabelInfo(JumpReturnLabelInfo jumpReturnLabelInfo) {
            jumpReturnLabelList.add(jumpReturnLabelInfo);
        }
        
        /**
         * Add any JumpReturnLabelInfo generated (in order) by a child context.
         * @param childContext the child context whose labels to add.
         */
        void addJumpReturnLabelInfo(GenerationContext childContext) {
            jumpReturnLabelList.addAll(childContext.jumpReturnLabelList);
        }
        
        /**
         * @return (List of JumpReturnLabelInfo) 
         * The list of relevant return and jump instruction labels generated within this context, in the order in which they appear.  
         */
        List<JumpReturnLabelInfo> getJumpReturnLabelInfoList() {
            return Collections.unmodifiableList(jumpReturnLabelList);
        }
        
                
        MethodVisitor getMethodVisitor() {
            return mv;
        }
              
        int getMethodVarIndex(String varName) {
            Integer index = methodVarToIndexMap.get(varName);
            if (index != null) {
                return index.intValue();
            }
            
            return -1;
        }
        
        JavaTypeName getMethodVarType(String varName) {
            return methodVarToTypeMap.get(varName);           
        }
    }    
    
    /**
     * Info about a jump or return instruction which has been labeled in order to handle exception table entries 
     *   for try-catch statements with try blocks which span the code generating this instruction.
     * 
     * Such instructions are excluded from any exception handling blocks if they cause execution to escape the bounds of the try statement.
     * eg. if there is a return statement in a try block in a try-catch statement, the corresponding return instruction will
     *     be excluded from the exception table entry.
     *     if there is a jump (eg. goto) statement in a try block in a try-catch statement, the jump instruction will be
     *     excluded from the exception table entry if the jump destination lies outside the try block.
     * 
     * @author Edward Lam
     */
    private static class JumpReturnLabelInfo {
        private final Label instructionLabel;
        private final Label afterInstructionLabel;
        private final Label destinationLabel;

        /**
         * Constructor for a JumpReturnLabelInfo.
         * @param instructionLabel the label for the jump or return instruction.
         * @param afterInstructionLabel the label after the jump or return instruction.
         * @param destinationLabel the destination label for a jump instruction, or null if it's a return instruction.
         */
        private JumpReturnLabelInfo(Label instructionLabel, Label afterInstructionLabel, Label destinationLabel) {
            this.instructionLabel = instructionLabel;
            this.afterInstructionLabel = afterInstructionLabel;
            this.destinationLabel = destinationLabel;
        }
        
        /**
         * @return the label for the instruction.
         */
        public Label getInstructionLabel() {
            return instructionLabel;
        }
        
        /**
         * @return the label after the instruction.
         */
        public Label getAfterInstructionLabel() {
            return afterInstructionLabel;
        }
        
        /**
         * @return the destination label of the jump or null if it's a return instruction.
         */
        public Label getDestinationLabel() {
            return destinationLabel;
        }
    }
            
    
    /**
     * Constructor for a JavaBytecodeGenerator
     */
    AsmJavaBytecodeGenerator() {
    }    

    
    /**
     * Encodes bytecode for a given class.
     * @param classRep the representation for the top-level class.
     * @return byte[] the byte code for the class.  
     * @throws JavaGenerationException
     */
    public static byte[] encodeClass(JavaClassRep classRep) throws JavaGenerationException {
        
        //get ASM to compute max stack and max locals.  Don't compute stack map frames yet.
        final ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        
        encodeClassAcceptingVisitor(classRep, cw);
        
        byte[] result = cw.toByteArray();
        
        // TODO: It would be faster to not have to read the bytecode back out of the array.
        // We do this since we require ClassWriter to computer the maximum locals and stack
        // before the analysis, which is required since the analysis's Frame class needs to
        // know these maximums since it stores the values of locals and the stack in an array.
        if (LECCMachineConfiguration.bytecodeSpaceOptimization()) {
            final ClassReader cr = new ClassReader(result);
            final ClassWriter cw2 = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            final ClassVisitor cv = new NullingClassAdapter(cw2);
            cr.accept(cv, 0);
            
            result = cw2.toByteArray();
        }
                
        return result;
    }       
    
    /**
     * Accepts a visitor and fills it with information for a given class.
     * @param classRep the representation for the top-level class.
     * @param cv the class visitor to accept.
     * @throws JavaGenerationException
     */
    private static void encodeClassAcceptingVisitor(JavaClassRep classRep, ClassVisitor cv) throws JavaGenerationException {
        
        // Get the fully-qualified internal class and superclass names.    
        final JavaTypeName classRepTypeName = classRep.getClassName();
        final String className = classRepTypeName.getJVMInternalName();
        final String superclassName = classRep.getSuperclassName().getJVMInternalName();

        // Determine if the class or any inner class contains assert statements.
        int assertPresence = classRep.getAssertionContainment();
        if ((assertPresence & JavaClassRep.ASSERTS_UNKNOWN) > 0) {
            assertPresence = AsmJavaBytecodeGenerator.containsAsserts(classRep);
        }
        
        // Create the interfaces[] array.
        final int nInterfaces = classRep.getNInterfaces();
        final String[] interfaces = new String[nInterfaces];
        for (int i = 0; i < nInterfaces; i++) {
            interfaces[i] = classRep.getInterface(i).getJVMInternalName();
        }        
                       
        //ACC_SUPER flag should always be set for the flags defining a class file.
        //(see the Java language specification under ACC_SUPER in the index. The flag is not set only
        //by older Java compilers and exists for backwards compatibility reasons).
        int classModifiers = classRep.getModifiers() | Opcodes.ACC_SUPER;      
        //static inner classes are marked with the static modifier, but this is not a valid access flag for a class.
        classModifiers &= ~Modifier.STATIC;
        
        // We aren't generating or using generics, so the signature can be null
        String classSignature = null;
        cv.visit(Opcodes.V1_5, classModifiers, className, classSignature, superclassName, interfaces);
        
        //sourcefileName = null, since this class was not compiled from a Java source file.
        //However, if we are debugging byte codes, use a "fake" source file name as if this class were generated from a Java source file.
        //This eliminates a trivial difference between the byte code generated by ASM and that of javac and makes inspecting the
        //differences in a differencing tool easier.
        String sourceFileName = null;
//        if (AsmJavaBytecodeGenerator.DEBUG_GENERATED_BYTECODE) {               
            String unqualifiedName = classRepTypeName.getUnqualifiedJavaSourceName();
            int dotPosition = unqualifiedName.indexOf('.');
            if (dotPosition != -1) {
                //get the top level class name.
                unqualifiedName = unqualifiedName.substring(0, dotPosition);
            }
            sourceFileName = unqualifiedName + ".java";
//        }
        
        cv.visitSource(sourceFileName, null);
        
        //add the fields        
        for (int i = 0, nFields = classRep.getNFieldDeclarations(); i < nFields; ++i) {
            
            JavaFieldDeclaration fieldDeclaration = classRep.getFieldDeclaration(i);            
            
            //todoBI it may be more efficient to handle initializers for static-fields here in the cases where it is possible
            //(int, long, float, double, String).
            cv.visitField(fieldDeclaration.getModifiers(),
                fieldDeclaration.getFieldName(),
                fieldDeclaration.getFieldType().getJVMDescriptor(),
                null,
                null);
        }  
        
        /*
         * When dealing with assert statements there is possibly an additional field that needs to be
         * added.
         * If a class contains an assert statement a static final synthetic boolean field called '$assertionsDisabled' is
         * added.  This field is initialized in the class static initializer and is used to determine whether to
         * check or skip assertions.
         */
        if (assertPresence != JavaClassRep.ASSERTS_NONE) {
            
            if ((assertPresence & JavaClassRep.ASSERTS_IN_CLASS) > 0) {
                // We need to add a static final synthetic boolean field to indicate the enabled/disabled state of assertions.
                cv.visitField(Opcodes.ACC_FINAL + Opcodes.ACC_STATIC + Opcodes.ACC_SYNTHETIC, "$assertionsDisabled", "Z", null, null);
            }
        }
        
        //add the constructors
        final int nConstructors = classRep.getNConstructors();
        if (nConstructors == 0) {
            //if empty, add the default constructor.             
            
            JavaConstructor defaultConstructor = new JavaConstructor(Modifier.PUBLIC, ((JavaTypeName.Reference.Object)classRepTypeName).getBaseName());
            defaultConstructor.addStatement(new JavaStatement.ReturnStatement());                                   
            encodeConstructor(classRep, defaultConstructor, cv);
            
        } else {
            
            for (int i = 0; i < nConstructors; ++i) {                
                encodeConstructor(classRep, classRep.getConstructor(i), cv);                              
            }            
        }
        
        //add the methods
        for (int i = 0, nMethods = classRep.getNMethods(); i < nMethods; ++i) {
                        
            encodeMethod(classRep, classRep.getMethod(i), cv);                       
        }
        
        //add the initializers for the static fields
        encodeClassInitializer(classRep, cv, (assertPresence & JavaClassRep.ASSERTS_IN_CLASS) > 0);  
        
        //add the inner classes (these are basically just references to the inner classes)
        
        //if classRep itself is an inner class, call visitInnerClass on itself. This is what the eclipse java compiler does.
        //javac annotates for every inner class reference occurring within the class file e.g. a field declared of inner class type,
        //an instance of expression of inner class type, a throws declaration on a method where an inner class is thrown.
        if (classRepTypeName.isInnerClass()) {
            
            JavaTypeName.Reference.Object classTypeName = (JavaTypeName.Reference.Object)classRepTypeName;            
            String internalImportName = classTypeName.getImportName().replace('.', '/');
            
            cv.visitInnerClass(classTypeName.getJVMInternalName(), internalImportName, classTypeName.getBaseName(), classRep.getModifiers());
        }
        
        /*
         * Previously we would call visitInnerClass for any inner classes associated with this class.  However,
         * we are no longer doing this.  
         * Bytecode is generated in different scenarios (i.e. static generation, dynamic generation, etc).  In some
         * scenarios inner classes are generated separately from the containing class.  In order to keep the generated
         * bytecode consistent between the dynamic and static scenarios wer are not adding the attributes for contained 
         * inner classes to the bytecode.
         * 
         for (int i = 0, nInnerClasses = classRep.getNInnerClasses(); i < nInnerClasses; ++i) {
            
            JavaClassRep innerClass = classRep.getInnerClass(i);
            JavaTypeName.Reference.Object innerClassTypeName = (JavaTypeName.Reference.Object)innerClass.getClassName();            
            
            cw.visitInnerClass(innerClassTypeName.getJVMInternalName(), className, innerClassTypeName.getBaseName(), innerClass.getModifiers());
        }               
        
        */
        
        
        cv.visitEnd();
    }       
    
    /**
     * Add the <init> method for the given constructor. This also includes initializing the non-static fields that 
     *   have initializers.
     * @param classRep 
     * @param javaConstructor
     * @param cv
     * @throws JavaGenerationException
     */
    private static void encodeConstructor(JavaClassRep classRep, JavaConstructor javaConstructor, ClassVisitor cv) throws JavaGenerationException {               
        
        // gather info on the thrown exceptions
        final int nThrownExceptions = javaConstructor.getNThrownExceptions();
        String[] thrownExceptions = new String[nThrownExceptions];
        for (int i = 0; i < nThrownExceptions; ++i) {            
            thrownExceptions[i] = javaConstructor.getThrownException(i).getJVMInternalName();
        }              
                
        MethodVisitor mv = cv.visitMethod(javaConstructor.getModifiers(), "<init>", javaConstructor.getJVMMethodDescriptor(), null, thrownExceptions);
        
        final Map<String, JavaTypeName> methodVarToTypeMap = new HashMap<String, JavaTypeName>();
        final Map<String, Integer> methodVarToIndexMap = new HashMap<String, Integer>();        
        methodVarToTypeMap.put("this", classRep.getClassName());
        methodVarToIndexMap.put("this", Integer.valueOf(0));
        int indexOfNextSlot = 1;
        
        for (int i = 0, nParams = javaConstructor.getNParams(); i < nParams; ++i) {
            JavaTypeName varType = javaConstructor.getParamType(i);
            final int typeSize = getTypeSize(varType);
            String varName = javaConstructor.getParamName(i);
            methodVarToTypeMap.put(varName, varType);
            methodVarToIndexMap.put(varName, Integer.valueOf(indexOfNextSlot));
            indexOfNextSlot += typeSize;
        }                
     
        // Start the method's code.
        mv.visitCode();
        
        // Invoke the superclass initializer.     
                      
        if (javaConstructor.getSuperConstructorParamTypes().length == 0) {
            //push the "this" reference on the stack
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, classRep.getSuperclassName().getJVMInternalName(), "<init>", "()V");
        } else {
            GenerationContext context = new GenerationContext(methodVarToTypeMap, methodVarToIndexMap, indexOfNextSlot, mv, classRep.getClassName());            
            MethodInvocation mi = 
                new MethodInvocation.Instance(null, "<init>", classRep.getSuperclassName(), javaConstructor.getSuperConstructorParamValues(), javaConstructor.getSuperConstructorParamTypes(), JavaTypeName.VOID, MethodInvocation.InvocationType.SPECIAL);
            encodeMethodInvocationExpr(mi, context);
        }

        // Add initializers for the non-statically-initialized fields.        
        final int nFields = classRep.getNFieldDeclarations();
        for (int i = 0; i < nFields; ++i) {
            
            JavaFieldDeclaration fieldDecl = classRep.getFieldDeclaration(i);
            JavaExpression initializer = fieldDecl.getInitializer();
            
            if (initializer != null && !Modifier.isStatic(fieldDecl.getModifiers())) {
                
                GenerationContext context = new GenerationContext(methodVarToTypeMap, methodVarToIndexMap, indexOfNextSlot, mv, classRep.getClassName());
                
                //push the "this" reference
                mv.visitVarInsn(Opcodes.ALOAD, 0);
                
                //evaluate the initializer
                encodeExpr(initializer, context);
                
                 //do the assignment
                mv.visitFieldInsn(Opcodes.PUTFIELD, classRep.getClassName().getJVMInternalName(), fieldDecl.getFieldName(), fieldDecl.getFieldType().getJVMDescriptor());                
            }                     
        }
              
        // Add the body code for the constructor
        GenerationContext context = new GenerationContext(methodVarToTypeMap, methodVarToIndexMap, indexOfNextSlot, mv, classRep.getClassName());
        boolean isTerminating = encodeStatement(javaConstructor.getBodyCode(), context);
              
        // Add any implicit "return" statement.
        if (!isTerminating) {
            mv.visitInsn(Opcodes.RETURN);
        }
       
        //mark the end of encoding the constructor
        mv.visitMaxs(0, 0);
        
        // End the method.
        mv.visitEnd();
    }    
    
    /**
     * Add the <clinit> method. This initializes the static fields that have initializers.
     * @param classRep
     * @param cv
     * @param initializeForAsserts
     * @throws JavaGenerationException
     */
    private static void encodeClassInitializer(JavaClassRep classRep, 
                                               ClassVisitor cv, 
                                               boolean initializeForAsserts) throws JavaGenerationException {  
        
        // Add initializers for the statically-initialized fields.
        final int nFields = classRep.getNFieldDeclarations();
        
        if (!classRep.hasInitializedStaticField() && !initializeForAsserts) {
            //we don't need to bother adding a static initializer if there are no static fields to initialize.
            //note that javac also has this optimization.
            return;
        }
        
        
        
        MethodVisitor mv = cv.visitMethod(Opcodes.ACC_STATIC, "<clinit>", "()V", null, null);        
        
        // Start the method's code.
        mv.visitCode();

        final JavaTypeName classRepTypeName = classRep.getClassName();

        /*
         * If this class contains assert statements we need to initialize the static final synthetic boolean field
         * '$assertionsDisabled'.
         * This is done by loading the Class constant for the top level class. The method 'desiredAssertionStatus()'
         * is then invoked on this Class object and the returned value is used to initialize $assertionsDisabled. 
         */
        if (initializeForAsserts) {
            // Get the fully-qualified internal class  name.    
            final String className = classRepTypeName.getJVMInternalName();
            
            // Get the top level class name.
            String topLevelClassName = getTopLevelClassJVMInternalName(className);
            
            // Load the Class constant for the top level class
            mv.visitLdcInsn(Type.getType("L" + topLevelClassName + ";"));
            
            // Now that we have the Class constant for the top level class we invoke the method
            // desiredAssertionStatus on it and use the resulting value to initialize the static field $assertionsDisabled in this class.
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Class", "desiredAssertionStatus", "()Z");
            Label l0 = new Label();
            mv.visitJumpInsn(Opcodes.IFNE, l0);
            mv.visitInsn(Opcodes.ICONST_1);
            Label l1 = new Label();
            mv.visitJumpInsn(Opcodes.GOTO, l1);
            mv.visitLabel(l0);
            mv.visitInsn(Opcodes.ICONST_0);
            mv.visitLabel(l1);
            mv.visitFieldInsn(Opcodes.PUTSTATIC, className, "$assertionsDisabled", "Z");            
        }
        
        for (int i = 0; i < nFields; ++i) {
            
            JavaFieldDeclaration fieldDecl = classRep.getFieldDeclaration(i);
            JavaExpression initializer = fieldDecl.getInitializer();
            
            if (initializer != null && Modifier.isStatic(fieldDecl.getModifiers())) {
                
                GenerationContext context = new GenerationContext(new HashMap<String, JavaTypeName>(), new HashMap<String, Integer>(), 0, mv, classRepTypeName);
                
                //evaluate the initializer
                encodeExpr(initializer, context);
                
                 //do the assignment
                mv.visitFieldInsn(Opcodes.PUTSTATIC, classRep.getClassName().getJVMInternalName(), fieldDecl.getFieldName(), fieldDecl.getFieldType().getJVMDescriptor());                
            }                     
        }
        
        // return.
        mv.visitInsn(Opcodes.RETURN);
      
        //mark the end of the method with a call to visitMaxs
        mv.visitMaxs(0, 0);        
        
        // End the method.
        mv.visitEnd();
    }


    /**
     * @param jvmInternalClassName the internal name of the class (potentially containing '$' if an inner class)
     * @return the internal name of the top-level class containing the named class.
     */
    private static String getTopLevelClassJVMInternalName(final String jvmInternalClassName) {
        String topLevelClassName = jvmInternalClassName;
        if (jvmInternalClassName.indexOf('$') >= 0) {
            topLevelClassName = jvmInternalClassName.substring(0, jvmInternalClassName.indexOf('$'));
        }
        return topLevelClassName;
    }       
    
    /**
     * Encode java byte code for the given method.
     * @param classRep model for the class in which the method is defined.
     * @param javaMethod
     * @param cv
     * @throws JavaGenerationException
     */
    private static void encodeMethod(JavaClassRep classRep, JavaMethod javaMethod, ClassVisitor cv) throws JavaGenerationException {
        
        // gather info on the thrown exceptions
        final int nThrownExceptions = javaMethod.getNThrownExceptions();
        String[] thrownExceptions = new String[nThrownExceptions];
        for (int i = 0; i < nThrownExceptions; ++i) {            
            thrownExceptions[i] = javaMethod.getThrownException(i).getJVMInternalName();
        }        
        
        //visit the method itself
        MethodVisitor mv = cv.visitMethod(javaMethod.getModifiers(), javaMethod.getMethodName(), javaMethod.getJVMMethodDescriptor(), null, thrownExceptions);
                
        // Add the code.
        mv.visitCode();
        final Map<String, JavaTypeName> methodVarToTypeMap = new HashMap<String, JavaTypeName>();
        final Map<String, Integer> methodVarToIndexMap = new HashMap<String, Integer>();
        int indexOfNextSlot = 0;
        if (!Modifier.isStatic(javaMethod.getModifiers())) {
            methodVarToTypeMap.put("this", classRep.getClassName());
            methodVarToIndexMap.put("this", Integer.valueOf(0));
            indexOfNextSlot = 1;
        }
        for (int i = 0, nParams = javaMethod.getNParams(); i < nParams; ++i) {
            JavaTypeName varType = javaMethod.getParamType(i);
            final int typeSize = getTypeSize(varType);
            String varName = javaMethod.getParamName(i);
            methodVarToTypeMap.put(varName, varType);
            methodVarToIndexMap.put(varName, Integer.valueOf(indexOfNextSlot));
            indexOfNextSlot += typeSize;
        }        
        GenerationContext context = new GenerationContext(methodVarToTypeMap, methodVarToIndexMap, indexOfNextSlot, mv, classRep.getClassName());
        
        boolean isTerminating = encodeStatement(javaMethod.getBodyCode(), context);
       
        // Add any implicit "return" statement.
        if (!isTerminating && javaMethod.getReturnType().equals(JavaTypeName.VOID)) {
            context.getMethodVisitor().visitInsn(Opcodes.RETURN);            
        }
        
        // Apply peep hole optimizations.
        //todoBI no peephole optimizations with ASM...
        //PeepHoleOptimizer.peepHoleOptimize(il);
        
        //The ClassWriter was constructed with a parameter to automatically compute max stack and max locals.
        //However, we must still call visitMaxs to mark off the end of the method. The arguments are ignored.
        mv.visitMaxs(0, 0);
        
        // End the method.
        mv.visitEnd();
    }    
    
    private static boolean encodeLocalVariableDeclaration(JavaStatement.LocalVariableDeclaration declaration, GenerationContext context) throws JavaGenerationException{
        
        MethodVisitor mv = context.getMethodVisitor();
        
        LocalVariable localVariable = declaration.getLocalVariable();
        
        // encode the instructions for the initializer if any.       
        JavaExpression initializer = declaration.getInitializer();
        if (initializer != null) {
            encodeExpr(initializer, context);            
        }
        
        int localVarIndex = context.addLocalVar(localVariable.getName(), localVariable.getTypeName());
               
        // Store the value from the initializer (if any) into the local var.
        if (initializer != null) {
           
            //encode the store instruction
            mv.visitVarInsn(getStoreOpCode(localVariable.getTypeName()), localVarIndex);            
        }
              
        return false;
    }
    
    private static boolean encodeExprStatement(JavaStatement.ExpressionStatement expressionStatement, GenerationContext context) throws JavaGenerationException {              
        
        MethodVisitor mv = context.getMethodVisitor();
        
        // get the result of evaluating the expression.  The value should not be left on the stack.
        JavaExpression expr = expressionStatement.getJavaExpression();       
        
        if (expr instanceof Assignment) {
            //assign without leaving a value on the stack (since it will be immediately popped off by the statement terminating semicolon.
            encodeAssignmentExpr((Assignment)expr, context, false);
                       
        } else {
            
            JavaTypeName exprType = encodeExpr(expr, context);
            
            //pop the resulting value off the stack
            if (!exprType.equals(JavaTypeName.VOID)) {           
                mv.visitInsn(getPopOpCode(exprType)); 
            }                      
        }
        
        return false;
    }
    
    /**    
     * @param type
     * @return the POP or POP2 instruction, depending on type.
     */
    private static int getPopOpCode (JavaTypeName type) {
        
       switch (type.getTag()) {            
            case JavaTypeName.BOOLEAN_TAG:                     
            case JavaTypeName.BYTE_TAG:               
            case JavaTypeName.SHORT_TAG:              
            case JavaTypeName.CHAR_TAG:                             
            case JavaTypeName.INT_TAG:
            case JavaTypeName.FLOAT_TAG:              
            case JavaTypeName.ARRAY_TAG:           
            case JavaTypeName.OBJECT_TAG:  
                return Opcodes.POP;
                        
            case JavaTypeName.LONG_TAG:            
            case JavaTypeName.DOUBLE_TAG:
                return Opcodes.POP2; 
            
            case JavaTypeName.VOID_TAG:                                                                  
            default:
            {
                throw new IllegalArgumentException("invalid type: " + type);
            }
        }                               
    }      
    
    private static boolean encodeReturnStatement(JavaStatement.ReturnStatement returnStatement, GenerationContext context) throws JavaGenerationException {
         
        MethodVisitor mv = context.getMethodVisitor();
        
        JavaExpression returnExpression = returnStatement.getReturnExpression();
        
        // Label the return instruction and the instruction after the return.
        // This instruction will be excluded from relevant ranges covered in the exception table.
        
        int returnOpCode;
        if (returnExpression == null) {
            
            returnOpCode = Opcodes.RETURN;
            
        } else {
            // encode the result of evaluating the return expression.
            JavaTypeName returnType = encodeExpr(returnExpression, context);
            
            returnOpCode = getReturnOpCode(returnType);
        }
        
        // Add a label for the return instruction.
        Label returnLabel = new Label();
        mv.visitLabel(returnLabel);
        
        // Visit the return instruction.
        mv.visitInsn(returnOpCode);
        
        // Add a label following the return instruction.
        Label afterReturnLabel = new Label();
        mv.visitLabel(afterReturnLabel);

        // Add info about the generated label.
        context.addJumpReturnLabelInfo(new JumpReturnLabelInfo(returnLabel, afterReturnLabel, null));
        
        // return statements are terminating.
        return true;
    }       
    
    /**    
     * @param type
     * @return the RETURN, IRETURN,... op code, depending on type.
     */
    private static int getReturnOpCode (JavaTypeName type) {
        
        switch (type.getTag()) { 
            case JavaTypeName.VOID_TAG:  
                return Opcodes.RETURN;
                
            case JavaTypeName.BOOLEAN_TAG:                     
            case JavaTypeName.BYTE_TAG:               
            case JavaTypeName.SHORT_TAG:              
            case JavaTypeName.CHAR_TAG:                             
            case JavaTypeName.INT_TAG:
                return Opcodes.IRETURN;

            case JavaTypeName.LONG_TAG:  
                return Opcodes.LRETURN;
            
            case JavaTypeName.DOUBLE_TAG:
                return Opcodes.DRETURN; 
            
            case JavaTypeName.FLOAT_TAG:
                return Opcodes.FRETURN;
            
            case JavaTypeName.ARRAY_TAG:           
            case JavaTypeName.OBJECT_TAG:  
                return Opcodes.ARETURN;                       
            
            default:
            {
                throw new IllegalArgumentException("invalid type: " + type);
            }
        }                               
    }          
    
    private static boolean encodeIfThenElseStatement(JavaStatement.IfThenElseStatement iteStatement, GenerationContext context) throws JavaGenerationException {
         
        MethodVisitor mv = context.getMethodVisitor();
        
        JavaExpression conditionExpr = iteStatement.getCondition();
        JavaStatement thenStatement = iteStatement.getThenStatement();
        JavaStatement elseStatement = iteStatement.getElseStatement();
        
        if (conditionExpr instanceof JavaExpression.OperatorExpression) {
            
            //generate more efficient code in the case of if (boolean-valued-operator)...
            
            //This case exists to handle the special case where an operator occurs as the child of an if-then-else (or ternary operator)
            //conditional. For example, in the situation:
            //
            //if (x != null) {...} else {...}
            // 
            // we do not want to evaluate x != null to a boolean value, push that value on the stack,
            // and then test it prior to selecting the correct branch. Rather, we can combine the evaluation
            // and jump operations into a single step.
            
            JavaExpression.OperatorExpression operatorExpr = (JavaExpression.OperatorExpression)conditionExpr;                                     
            JavaOperator operator = operatorExpr.getJavaOperator();           
                                  
            if (operator.isLogicalOp() || operator.isRelationalOp()) {
                
                Label trueContinuation = new Label();
                context.addStatementJumpLabel(trueContinuation);
                
                Label falseContinuation = new Label();
                context.addStatementJumpLabel(falseContinuation);
                
                encodeBooleanValuedOperatorHelper(operatorExpr, context, trueContinuation, falseContinuation);                    
                return encodeThenStatementElseStatement(trueContinuation, falseContinuation, thenStatement, elseStatement, context);                
            }
            
            throw new JavaGenerationException("Unrecognized boolean-valued conditional operator " + operator.getSymbol() + ".");                       
        }
                           
        //encode the condition. It will be boolean valued.
        encodeExpr(iteStatement.getCondition(), context);
        
        //if false, jump to falseContinuation
        Label falseContinuation = new Label();
        mv.visitJumpInsn(Opcodes.IFEQ, falseContinuation);  
        
        return encodeThenStatementElseStatement(null, falseContinuation, thenStatement, elseStatement, context);          
    } 
    
    private static boolean encodeThenStatementElseStatement(Label trueContinuation, Label falseContinuation, JavaStatement thenStatement, JavaStatement elseStatement, GenerationContext context) throws JavaGenerationException {
        
        MethodVisitor mv = context.getMethodVisitor();
        
        if (trueContinuation != null) {
            mv.visitLabel(trueContinuation);
        }
        
        //in general the "then" part will have its own inner scope, but we optimize this out in the case of ExpressionStatements which
        //can't introduce new variables.
        GenerationContext thenContext = (thenStatement instanceof JavaStatement.ExpressionStatement) ? context : new GenerationContext(context);
        
        boolean thenIsTerminating = encodeStatement(thenStatement, thenContext);
        context.addJumpReturnLabelInfo(thenContext);
                
        if (elseStatement.emptyStatement()) {
            //don't bother to encode the goto skipping over the else part if there is not else part.
            
            mv.visitLabel(falseContinuation);
            
            //the if-then-else is not terminating, because the else part, which is just {}, is not terminating
            return false;
        } 
        
        Label label2 = null;
        if (!thenIsTerminating) {               
            label2 = new Label();
            context.addStatementJumpLabel(label2);

            mv.visitJumpInsn(Opcodes.GOTO, label2);
        }
        
        mv.visitLabel(falseContinuation);
        
        //in general the "else" part will have its own inner scope, but we optimize this out in the case of ExpressionStatements which
        //can't introduce new variables.
        GenerationContext elseContext = (elseStatement instanceof JavaStatement.ExpressionStatement) ? context : new GenerationContext(context);        
        boolean elseIsTerminating = encodeStatement(elseStatement, elseContext);
        context.addJumpReturnLabelInfo(elseContext);
        
        if (!thenIsTerminating) {
            mv.visitLabel(label2);
        }
        
        return thenIsTerminating && elseIsTerminating;
    }   
    
    private static boolean encodeSwitchStatement(JavaStatement.SwitchStatement switchStatement, GenerationContext context) throws JavaGenerationException {       
        
        MethodVisitor mv = context.getMethodVisitor();
        
                
        // Append the instructions to evaluate the condition, leaving the result on the operand stack.
        JavaExpression condition = switchStatement.getCondition();
        encodeExpr(condition, context);                
        
        // Follow (what seems to be) javac's rule for deciding between a lookup switch and a table switch:
        //      Use a table switch if allowed by maxTableSize, where
        //          maxTableSize = (# cases - 2) * 5  , (# cases) doesn't include the default.
        //
        // eg. if there are 2 case alternatives (+ default): 
        //          maxTableSize = ((2 - 2) * 5) = 0, so always use a lookup switch.
        //     if there are cases {1, 2, 3, 4, 15}:
        //          maxTableSize = ((5 - 2) * 5) = 15.  A table would contain 15 entries {1, 2, .., 15}, so use a table switch.
        //     if there are cases {1, 2, 3, 4, 16}:
        //          A table would contain 16 entries {1, 2, .., 16}, so use a lookup switch.
        List<IntCaseGroup> caseGroups = switchStatement.getCaseGroups();
        int nTotalCases = 0;
        
        // Calculate the first and last values.
        int firstValue = Integer.MAX_VALUE;
        int lastValue = Integer.MIN_VALUE;
        for (int caseN = 0; caseN < caseGroups.size(); ++caseN) {
            SwitchStatement.IntCaseGroup switchCaseGroup = caseGroups.get(caseN);
            for (int i = 0, nCaseLabels = switchCaseGroup.getNCaseLabels(); i < nCaseLabels; i++) {
                nTotalCases++;
                int caseLabel = switchCaseGroup.getNthCaseLabel(i);
                if (firstValue > caseLabel) {
                    firstValue = caseLabel;
                }
                
                if (lastValue < caseLabel) {
                    lastValue = caseLabel;
                }
            }
        }
        
        if (nTotalCases == 0) {
            // If the switch statement contains no cases, javac's behaviour is to generate a pop instruction
            // to pop the condition value off the operand stack.
            mv.visitInsn(Opcodes.POP);
            
        } else {
            
            int nSpannedCases = lastValue - firstValue + 1;

            int maxTableSize = (nTotalCases - 2) * 5;
            boolean useTableSwitch = !(nSpannedCases > maxTableSize);

            // Create the default label and declare the other case labels.
            Label defaultLabel = new Label();

            // (SwitchStatement.IntCaseGroup->Label) A map from case group to label.
            Map<IntCaseGroup, Label> caseGroupToLabelMap = new HashMap<IntCaseGroup, Label>();

            if (!useTableSwitch) {
                // A lookup switch.  Each case match gets its own label.

                // Map from case label to label, sorted by case label.
                SortedMap<Integer, Label> caseLabelToLabelMap = new TreeMap<Integer, Label>();

                // A count of the number of case labels.
                int nCaseLabels = 0;

                // Create the labels, and populate the map.
                for (int caseN = 0; caseN < caseGroups.size(); ++caseN) {
                    SwitchStatement.IntCaseGroup switchCaseGroup = caseGroups.get(caseN);

                    // The label for this case group.
                    Label label = new Label();
                    caseGroupToLabelMap.put(switchCaseGroup, label);

                    for (int i = 0, nCaseGroupLabels = switchCaseGroup.getNCaseLabels(); i < nCaseGroupLabels; i++) {
                        int caseLabel = switchCaseGroup.getNthCaseLabel(i);
                        caseLabelToLabelMap.put(Integer.valueOf(caseLabel), label);
                        nCaseLabels++;
                    }
                }

                // Create the array for case matches and the corresponding labels.
                int[] caseMatches = new int[nCaseLabels];
                Label[] labels = new Label[nCaseLabels];

                int index = 0;
                for (final Map.Entry<Integer, Label> entry : caseLabelToLabelMap.entrySet()) {                  
                    Integer caseLabelInteger = entry.getKey();
                    caseMatches[index] = caseLabelInteger.intValue();
                    labels[index] = entry.getValue();
                    index++;
                }

                // Visit the lookup switch instruction.
                mv.visitLookupSwitchInsn(defaultLabel, caseMatches, labels);

            } else {
                // A table switch 
                // The cases which aren't given should be set to the default case.

                Label[] labels = new Label[nSpannedCases];

                // Initially set all elements to the default label, if they won't all be set to something else later.
                if (nSpannedCases != nTotalCases) {
                    Arrays.fill(labels, defaultLabel);
                }

                // For each switch case provided, create a new label.
                for (int caseN = 0; caseN < caseGroups.size(); ++caseN) {
                    SwitchStatement.IntCaseGroup switchCaseGroup = caseGroups.get(caseN);

                    // The label for this case group.
                    Label label = new Label();
                    caseGroupToLabelMap.put(switchCaseGroup, label);

                    for (int i = 0, nCaseGroupLabels = switchCaseGroup.getNCaseLabels(); i < nCaseGroupLabels; i++) {
                        int caseLabel = switchCaseGroup.getNthCaseLabel(i);
                        int labelArrayIndex = caseLabel - firstValue;

                        labels[labelArrayIndex] = label;
                    }
                }

                // Visit the table switch instruction.
                mv.visitTableSwitchInsn(firstValue, lastValue, defaultLabel, labels);            
            }

            // Iterate over the cases.       
            for (int caseN = 0; caseN < caseGroups.size(); ++caseN) {
                SwitchStatement.IntCaseGroup switchCase = caseGroups.get(caseN);
                JavaStatement caseStatementGroup = switchCase.getStatement();

                // Add a local scope..
                GenerationContext innerContext = new GenerationContext(context);

                // Get the label to visit..
                Label labelToVisit = caseGroupToLabelMap.get(switchCase);

                //mark the location of the code for the i'th case.
                mv.visitLabel(labelToVisit);

                // get the instructions for the case statement.
                boolean caseStatementIsTerminating = encodeStatement(caseStatementGroup, innerContext);
                context.addJumpReturnLabelInfo(innerContext);

                // We don't (yet) handle non-terminating code in a case block.
                if (!caseStatementIsTerminating) {
                    throw new JavaGenerationException("Can't (yet) handle non-terminating code in a case block.");
                }                       
            }

            JavaStatement defaultStatementGroup = switchStatement.getDefaultStatement();
            //mark the location of the default statements (even if there are none, this means just progress to the next instruction..)
            mv.visitLabel(defaultLabel);
            if (defaultStatementGroup != null) {
                // Add a local scope..
                GenerationContext innerContext = new GenerationContext(context);

                boolean defaultStatementIsTerminating = encodeStatement(defaultStatementGroup, innerContext);
                context.addJumpReturnLabelInfo(innerContext);

                // We don't (yet) handle non-terminating code in a case block.
                if (!defaultStatementIsTerminating) {
                    throw new JavaGenerationException("Can't (yet) handle non-terminating code in a case block.");
                }                     
            }
        }
               
        // Note: we should add GOTO instructions to each case block to jump to after the switch statement if necessary.
        //   But for now it's not necessary since all cases are terminated.
        boolean isTerminating = true; 
        
        return isTerminating;
    }
    
    private static boolean encodeBlockStatement(JavaStatement.Block block, GenerationContext context) throws JavaGenerationException {

        List<JavaExceptionHandler> exceptionHandlers = block.getExceptionHandlers();
        
        if (!exceptionHandlers.isEmpty()) {
            return encodeTryCatchStatement(block, context);
        }
        
        //do not spawn a new context for the block.
        //todoBI it would probably be better to spawn a new context for a block to limit the scoping of introduced variables.
        //however, then one would not use a block in place of a list of statements...                     
        boolean isTerminating = false;  // starts out false (for the case with no statements..)
        
        // Append the instructions comprising the block's component statements.
        int nStatements = block.getNBlockStatements();
        for (int i = 0; i < nStatements; i++) {
            JavaStatement blockStatement = block.getNthBlockStatement(i);
            
            // skip comments.
            if (blockStatement instanceof Comment) {
                continue;
            }
                
            // is terminating if the last statement is terminating. 
            isTerminating = encodeStatement(blockStatement, context);
        }        
        
        return isTerminating;
    }
    
    private static boolean encodeTryCatchStatement(JavaStatement.Block block, GenerationContext context) throws JavaGenerationException {
      
        MethodVisitor mv = context.getMethodVisitor();
        
        List<JavaExceptionHandler> exceptionHandlers = block.getExceptionHandlers();
        
        if (exceptionHandlers.isEmpty()) {
            throw new IllegalStateException();
        }
        
        // Spawn a child context for the try block.
        GenerationContext tryBlockContext = new GenerationContext(context);
        
        boolean isTerminating = false;  // starts out false (for the case with no statements..)
        
        Label tryStartLabel = new Label();
        mv.visitLabel(tryStartLabel);
        
        // Append the instructions comprising the block's component statements.
        int nStatements = block.getNBlockStatements();
        for (int i = 0; i < nStatements; i++) {
            JavaStatement blockStatement = block.getNthBlockStatement(i);
            
            // skip comments.
            if (blockStatement instanceof Comment) {
                continue;
            }
                
            // is terminating if the last statement is terminating. 
            isTerminating = encodeStatement(blockStatement, tryBlockContext);
            context.addJumpReturnLabelInfo(tryBlockContext);
        }
        
        // Add an instruction to jump to after the exception handlers, if the statement block isn't terminating.
        Label tryCatchEndLabel = new Label();
        context.addStatementJumpLabel(tryCatchEndLabel);
        if (!isTerminating) {
            mv.visitJumpInsn(Opcodes.GOTO, tryCatchEndLabel);            
        }               
        
        Label tryEndLabel = new Label();
        mv.visitLabel(tryEndLabel);               
        
        // Add exception handlers as necessary.      
        
        // Now set handlers to handle any exceptions.
        for (int i = 0, nExceptionHandlers = exceptionHandlers.size(); i < nExceptionHandlers; ++i) {
            
            JavaExceptionHandler eh = exceptionHandlers.get(i); 
            
            JavaTypeName exceptionType = JavaTypeName.make(eh.getExceptionClass());  // exception class is a non-array reference type.  
            
            //encode the start of the catch block.
            Label catchLabel = new Label();
            mv.visitLabel(catchLabel);
            
            // Create a child context for the catch block.
            GenerationContext catchContext = new GenerationContext(context);
            
            // The thrown exception object will be the only item on the stack.  
            // Store it into a new local variable.
            String exceptionVarName = eh.getExceptionVarName();
            int exceptionVarIndex = catchContext.addLocalVar(exceptionVarName, exceptionType);
            mv.visitVarInsn(Opcodes.ASTORE, exceptionVarIndex);
           
            boolean catchIsTerminating = encodeStatement(eh.getHandlerCode(), catchContext);
            context.addJumpReturnLabelInfo(catchContext);
            
            if (!catchIsTerminating) {
                mv.visitJumpInsn(Opcodes.GOTO, tryCatchEndLabel);
            }           
            
            // In the end, we're only terminating if all the catch blocks are terminating.
            isTerminating &= catchIsTerminating;
            
            //encode the try/catch block. This can be done in any order, any time after all labels passed as arguments have been visited,
            //  between visitCode() and visitMaxs().
            encodeTryCatchBlock(tryStartLabel, tryEndLabel, catchLabel, exceptionType, tryBlockContext);
        }
        
        //mark the end of the whole try/catch code
        mv.visitLabel(tryCatchEndLabel);              
                
        return isTerminating;
    }
    
    /**
     * Generate the exception table entry(/entries) for a try/catch block.
     * This can be done in any order, any time after all labels passed as arguments have been visited.
     * 
     * Jump and return instructions are excluded from any exception handling blocks if they cause the next executed instruction 
     *   to escape the bounds of the try statement.
     * 
     * eg. if there is a return statement in a try block in a try-catch statement, the corresponding return instruction will
     *     be excluded from the exception table entry.
     *     if there is a jump (eg. goto) statement in a try block of a try-catch statement, the jump instruction will be
     *     excluded from the exception table entry if the jump destination lies outside the try statement.
     * 
     * Exclusion of the instruction will cause the exception table entry to be split or reduced:
     *   one entry will include the part before the excluded instruction, 
     *   the other will include the part after it, if it is not the instruction at the end of the block.
     * 
     * 
     * An interesting case to consider is a try/catch nested with respect to a while(true) statement, with a continue statement in the body:
     * 
     *     try {
     *         while (true) {
     *             (statements)
     *             continue;
     *             (more statements)
     *         }
     *     } catch (Exception e) {
     *     }
     * 
     *   versus
     * 
     *     while (true) {
     *         try {
     *             (statements)
     *             continue;
     *             (more statements)
     *         } catch (Exception e) {
     *         }
     *     }
     * 
     * In both cases, the continue is encoded as a goto instruction whose destination is the first instruction in (statements).
     * In the first case, the target "while" statement is nested within the try block, therefore the goto is not excluded.
     * In the second case, the target "while" statement is outside of the try block, therefore the goto is excluded.
     * ie. even though the instructions for the try body are the same, and the instructions for (statements) lies both within
     *   the while block and the try block, a jump to the first statement may or may not be viewed as escaping the try block,
     *   depending on which case was compiled.
     * 
     * 
     * @param tryStartLabel
     * @param tryEndLabel
     * @param catchLabel
     * @param exceptionType
     * @param tryBlockContext
     */
    private static void encodeTryCatchBlock(Label tryStartLabel, Label tryEndLabel, Label catchLabel, JavaTypeName exceptionType, 
                                            GenerationContext tryBlockContext) {
        
        MethodVisitor mv = tryBlockContext.getMethodVisitor();
        
        // The start label for the next table entry to be visited.
        Label exceptionEntryStartLabel = tryStartLabel;
        
        for (final JumpReturnLabelInfo jumpReturnLabelInfo : tryBlockContext.getJumpReturnLabelInfoList()) {            
            
            // Only exclude the jump/return instruction (ie. split the exception block) 
            //   if the destination label is outside the scope of the try block
            Label destinationLabel = jumpReturnLabelInfo.getDestinationLabel();
            if (destinationLabel != null && tryBlockContext.containsStatementJumpLabel(destinationLabel)) {
                continue;
            }
            
            // The next entry goes from the start label (inclusive) to the return or continue label (exclusive).
            Label returnContinueLabel = jumpReturnLabelInfo.getInstructionLabel();
            if (exceptionEntryStartLabel.getOffset() != returnContinueLabel.getOffset()) {
                // When there are consecutive instructions which jump/return outside the scope of the try block,
                // after the first iteration of the loop we will have the exception entry start label and the
                // return continue label both pointing to the same offset (ie. the offset of the nth consecutive
                // jump/return instruction on iteration n).
                mv.visitTryCatchBlock(exceptionEntryStartLabel, returnContinueLabel, catchLabel, exceptionType.getJVMInternalName());
            }
            
            // Set the start label for the next table entry to be visited.
            exceptionEntryStartLabel = jumpReturnLabelInfo.getAfterInstructionLabel();    // afterReturnLabel
        }
        
        // Check for the case where the last return instruction comes at the end of the try block.
        if (exceptionEntryStartLabel.getOffset() != tryEndLabel.getOffset()) {
            // Add the final entry, which ends at the tryEndLabel.
            mv.visitTryCatchBlock(exceptionEntryStartLabel, tryEndLabel, catchLabel, exceptionType.getJVMInternalName());
        }
    }
    
    private static boolean encodeThrowStatement(JavaStatement.ThrowStatement throwStatement, GenerationContext context) throws JavaGenerationException {
        
        MethodVisitor mv = context.getMethodVisitor();
        
        // get the result of evaluating the thrown expression.
        encodeExpr(throwStatement.getThrownExpression(), context);
               
        // throw the result.
        mv.visitInsn(Opcodes.ATHROW);
        
        // throw statements are terminating.       
        return true;
    }
    
    private static boolean encodeUnconditionalLoop(JavaStatement.UnconditionalLoop ul, GenerationContext context) throws JavaGenerationException {
        
        //implemented as:
        //label label1:
        //{ body statements}
        //goto label1
        
        MethodVisitor mv = context.getMethodVisitor();
        Label label1 = new Label();
        mv.visitLabel(label1);
        
        context.addStatementJumpLabel(ul.getLabel(), label1);
        
        //the body defines a new scope.
        GenerationContext bodyContext = new GenerationContext(context);
        
        boolean bodyIsTerminating = encodeStatement(ul.getBody(), bodyContext);
        context.addJumpReturnLabelInfo(bodyContext);
        
        if (!bodyIsTerminating) {
            mv.visitJumpInsn(Opcodes.GOTO, label1);
        }
              
        return true;
    }
    
    /**
     * Generate the bytecode for an assert statement.
     * Basically an assert statement like: assert conditionExpr : onFailureExpr;
     * is encoded as:
     * if (!$assertionsDisabled) {
     *     if (!conditionExpr) {
     *         throw new AssertionError (onFailureExpr);
     *     }
     * }
     * @param assertStatement
     * @param context
     * @return true if the statement is terminating.
     * @throws JavaGenerationException
     */
    private static boolean encodeAssertStatement (JavaStatement.AssertStatement assertStatement, GenerationContext context) throws JavaGenerationException {
        
        // Build up a ClassInstanceCreationExpression for the AssertionError instance.
        JavaExpression createAssertionError = null;
        if (assertStatement.getOnFailureExpr() != null) {
            JavaTypeName onFailureExprType = assertStatement.getOnFailureExprType();
            if (onFailureExprType instanceof JavaTypeName.Reference) {
                onFailureExprType = JavaTypeName.OBJECT;
            }
            createAssertionError =
                new ClassInstanceCreationExpression (JavaTypeName.ASSERTION_ERROR, 
                                                     assertStatement.getOnFailureExpr(),
                                                     onFailureExprType);
        } else {
            createAssertionError = 
                new ClassInstanceCreationExpression(JavaTypeName.ASSERTION_ERROR);
        }
        JavaStatement.ThrowStatement throwAssertError = 
            new ThrowStatement(createAssertionError);
        
        // if (!conditionExpr) {throw new AssertionError();}
        IfThenElseStatement ifThen = 
            new IfThenElseStatement(new OperatorExpression.Unary(JavaOperator.LOGICAL_NEGATE, assertStatement.getConditionExpr()), throwAssertError);
        
        // if (!$assertionsDisabled) {if (!conditionExpr) {throw new AssertionError;}}
        JavaExpression assertionsDisabledField = new JavaExpression.JavaField.Static(context.classTypeName, "$assertionsDisabled", JavaTypeName.BOOLEAN);
        JavaExpression checkAssertionEnabled = new JavaExpression.OperatorExpression.Unary(JavaOperator.LOGICAL_NEGATE, assertionsDisabledField);
        ifThen = new IfThenElseStatement (checkAssertionEnabled, ifThen);
        
        return encodeIfThenElseStatement(ifThen, context);
    }
    
    /**
     * Encode a method invocation that is wrapped in a synchronized block.
     *   See Sun bug id #4414101 for a discussion of this generated code.
     * 
     * @param smi - the SynchronizedMethodInvocation object.
     * @param context - the context of the code generation.
     * @return - true if the SynchronizedMethodInvocation is terminating.
     * @throws JavaGenerationException
     */
    private static boolean encodeSynchronizedMethodInvocation (JavaStatement.SynchronizedMethodInvocation smi, GenerationContext context) throws JavaGenerationException {

        MethodVisitor mv = context.getMethodVisitor();

        // Get the JavaExpression for the object to synchronize on and generate the bytecode. 
        JavaExpression objectToSynchOn = smi.getSynchronizingObject();
        encodeExpr(objectToSynchOn, context);

        // The object to synchronize on is now on the stack.  Duplicate it, 
        // move one to storage as a local variable, and to MONITORENTER on the other. 
        mv.visitInsn(Opcodes.DUP);

        int mutexIndex = context.addLocalVar("$mutex", JavaTypeName.OBJECT);
        mv.visitVarInsn(Opcodes.ASTORE, mutexIndex);
        
        mv.visitInsn(Opcodes.MONITORENTER);
        
        // We need to wrap the method invocation in a try/catch block so 
        // the monitor can be exited properly if the method invocation throws
        // an exception.
        Label tryCatchStart = new Label();
        mv.visitLabel(tryCatchStart);

        // Note: if this is generalized to handle any synchronized statement (for example, a synchronized block), 
        //   then the scope of entries in the exception table here will have to be modified to exclude return instructions.
        //   See encodeTryCatchStatement() for how to do this.
        //   Here, the only statement in the try block is a single expressionStatement, which has no return instructions, 
        //     so we don't have to worry about handling this case.
        
        // Get the method invocation and generate the corresponding bytecode.
        MethodInvocation methodInvocation = smi.getMethodInvocation();
        encodeExpr (methodInvocation, context);
     
        // Load the mutex object back onto the stack and do MonitorExit.
        mv.visitVarInsn(Opcodes.ALOAD, mutexIndex);
        mv.visitInsn(Opcodes.MONITOREXIT);
        
        // Label the end of the try block around the method invocation.
        Label tryEnd = new Label();
        mv.visitLabel(tryEnd);

        // At this point we want to code an instruction to jump past the exception handling
        // code.
        Label tryCatchEnd = new Label();
        mv.visitJumpInsn(Opcodes.GOTO, tryCatchEnd);
        
        // Label the start of the exception handling code.
        Label catchStart = new Label();
        mv.visitLabel(catchStart);
        
        // The exception is on the stack.  Store it as a local.
        int exceptionIndex = context.addLocalVar("exception", JavaTypeName.OBJECT);        
        mv.visitVarInsn(Opcodes.ASTORE, exceptionIndex);
        
        // Retrieve the mutex object and do MONITOREXIT.
        mv.visitVarInsn(Opcodes.ALOAD, mutexIndex);
        mv.visitInsn(Opcodes.MONITOREXIT);
        
        // Label the end of the exception handling code that deals with the monitor.
        Label exceptionMonitorExitEnd = new Label();
        mv.visitLabel(exceptionMonitorExitEnd);
        
        // Retrieve the exception and throw it.
        mv.visitVarInsn(Opcodes.ALOAD, exceptionIndex);
        mv.visitInsn(Opcodes.ATHROW);
        
        // Vist the label to mark the end of the try/catch.
        mv.visitLabel(tryCatchEnd);
        
        // Set up the try/catch block to catch exceptions thrown by the method invocation
        // and handle exiting the monitor.
        mv.visitTryCatchBlock(tryCatchStart, tryEnd, catchStart, null);
        
        // Set up a try catch block so that if an exception is thrown by trying to exit 
        // the monitor in the case of an exception from the method invocation we will keep trying to 
        // exit the monitor.
        mv.visitTryCatchBlock(catchStart, exceptionMonitorExitEnd, catchStart, null);
        
        return false;
    }
    
    private static boolean encodeLabelledContinue(JavaStatement.LabelledContinue lc, GenerationContext context) {
        
        MethodVisitor mv = context.getMethodVisitor();
        Label label = context.getNamedStatementJumpLabel(lc.getLabel());
        if (label == null) {
            throw new IllegalStateException("label to continue to cannot be null.");
        }
        
        // Label the goto instruction and the instruction after the goto.
        // This instruction will be excluded from relevant ranges covered in the exception table.
        
        // Add a label for the goto instruction.
        Label gotoLabel = new Label();
        mv.visitLabel(gotoLabel);
        
        // Visit the goto instruction.
        mv.visitJumpInsn(Opcodes.GOTO, label);
        
        // Add a label following the goto instruction.
        Label afterGotoLabel = new Label();
        mv.visitLabel(afterGotoLabel);
        
        context.addJumpReturnLabelInfo(new JumpReturnLabelInfo(gotoLabel, afterGotoLabel, label));
        
        return true;
    }
    
    /**
     * Get the Java code for a given java statement.
     * @param statement the java statement for which to generate source.
     * @param context the generation context.
     * @return boolean whether the statement is terminating. 
     * @throws JavaGenerationException
     */
    private static boolean encodeStatement(JavaStatement statement, GenerationContext context) throws JavaGenerationException {
        
        if (statement instanceof LocalVariableDeclaration) {
            return encodeLocalVariableDeclaration((LocalVariableDeclaration)statement, context);            

        } else if (statement instanceof ExpressionStatement) {
            return encodeExprStatement((ExpressionStatement)statement, context);           

        } else if (statement instanceof ReturnStatement) {
            return encodeReturnStatement((ReturnStatement)statement, context);          
                
        } else if (statement instanceof IfThenElseStatement) {            
            return encodeIfThenElseStatement((IfThenElseStatement)statement, context);            

        } else if (statement instanceof SwitchStatement) {            
            return encodeSwitchStatement((SwitchStatement)statement, context);
                           
        } else if (statement instanceof Block) {
            return encodeBlockStatement((Block)statement, context);           
                
        } else if (statement instanceof ThrowStatement) {
            return encodeThrowStatement((ThrowStatement)statement, context);
            
        } else if (statement instanceof UnconditionalLoop) {
            return encodeUnconditionalLoop((UnconditionalLoop)statement, context);
            
        } else if (statement instanceof LabelledContinue) {
            return encodeLabelledContinue((LabelledContinue)statement, context);
           
        } else if (statement instanceof SynchronizedMethodInvocation) {
            return encodeSynchronizedMethodInvocation((SynchronizedMethodInvocation)statement, context);
            
        } else if (statement instanceof Comment) {
            throw new JavaGenerationException("Attempt to generate bytecode for a comment.");
            
        } else if (statement instanceof AssertStatement) {
            return encodeAssertStatement ((AssertStatement) statement, context);
        } else {        
            throw new JavaGenerationException("Unrecognized statement type: " + statement.getClass());
        }
    } 
   
    
    /**
     * Creates instructions to evaluate the expression, and push the result onto the operand stack.
     * @param expression the java expression 
     * @param context   
     * @return JavaTypeName the type of the result on the operand stack.
     * @throws JavaGenerationException
     */
    private static JavaTypeName encodeExpr(JavaExpression expression, GenerationContext context) throws JavaGenerationException {
                     
        if (expression instanceof PlaceHolder) {
            return encodeExpr (((PlaceHolder)expression).getActualExpression(), context);
            
        } else if (expression instanceof MethodInvocation) {
            return encodeMethodInvocationExpr((MethodInvocation)expression, context);            

        } else if (expression instanceof OperatorExpression) {
            return encodeOperatorExpr((OperatorExpression)expression, context);

        } else if (expression instanceof Assignment) {
            return encodeAssignmentExpr((Assignment)expression, context, true);           

        } else if (expression instanceof LiteralWrapper) {
            return encodeLiteralExpr((LiteralWrapper)expression, context);
            
        } else if (expression instanceof ArrayCreationExpression) {
            return encodeArrayCreationExpr((ArrayCreationExpression)expression, context);

        } else if (expression instanceof ClassInstanceCreationExpression) {                
            return encodeClassInstanceCreationExpr((ClassInstanceCreationExpression)expression, context);

        } else if (expression instanceof JavaField) {
            return encodeJavaFieldExpr((JavaField)expression, context);
            
        } else if (expression instanceof MethodVariable) {
            return encodeMethodVariableExpr((MethodVariable)expression, context);
            
        } else if (expression instanceof LocalVariable) {
            return encodeLocalVariableExpr((LocalVariable)expression, context);
            
        } else if (expression instanceof ArrayAccess) {
            return encodeArrayAccessExpr((ArrayAccess)expression, context);
            
        } else if (expression instanceof ArrayLength) {
            return encodeArrayLengthExpr((ArrayLength)expression, context);
            
        } else if (expression instanceof LocalName) {
            return encodeLocalNameExpr((LocalName)expression, context);
                
        } else if (expression instanceof InstanceOf) {                
            return encodeInstanceOfExpr((InstanceOf)expression, context);

        } else if (expression instanceof CastExpression) {
            return encodeCastExpr((CastExpression)expression, context);

        } else if (expression instanceof ClassLiteral) {                
            return encodeClassLiteralExpr((ClassLiteral)expression, context);

        } else {
            throw new JavaGenerationException("Unrecognized expression type: " + expression.getClass());
        }               
    }
    
    /**
     * Creates the code that references a Java field within an expression. 
     * @param javaField the java field 
     * @param context   
     * @return JavaTypeName the type of the javaField argument.
     * @throws JavaGenerationException
     */
    private static JavaTypeName encodeJavaFieldExpr(JavaField javaField, GenerationContext context) throws JavaGenerationException {
        
        if (javaField instanceof JavaField.This) {            
            //the special 'this' field.
            return encodeThis(context);                    
        }
        
        MethodVisitor mv = context.getMethodVisitor();
        
        if (javaField instanceof JavaField.Instance) {
            
            JavaField.Instance javaInstanceField = (JavaField.Instance)javaField;
            JavaExpression instance = javaInstanceField.getInstance();
            JavaTypeName instanceType;
            if (instance == null) {
                //use 'this' as the instance expression, so if the field is 'foo', then it is 'this.foo'.
                instanceType = encodeThis(context);               
                
            } else {
                instanceType = encodeExpr(instance, context);               
            }
            
            JavaTypeName fieldType = javaField.getFieldType();
                                   
            mv.visitFieldInsn(Opcodes.GETFIELD, instanceType.getJVMInternalName(), javaField.getFieldName(), fieldType.getJVMDescriptor());
            
            return fieldType;                        
        }
        
        if (javaField instanceof JavaField.Static) {
            
           JavaField.Static javaStaticField = (JavaField.Static)javaField;
           JavaTypeName fieldType = javaField.getFieldType();
           
           mv.visitFieldInsn(Opcodes.GETSTATIC, javaStaticField.getInvocationClass().getJVMInternalName(), javaField.getFieldName(), fieldType.getJVMDescriptor());
           
           return fieldType;
        }
        
        throw new IllegalStateException();        
    }        
      
    /**
     * Pushes the value of the local name (which is either a local variable, method variable,
     * or reference to a field using the this object.
     * @param localName
     * @param context
     * @return JavaTypeName the type of the result on the operand stack.
     * @throws JavaGenerationException
     */
    private static JavaTypeName encodeLocalNameExpr(JavaExpression.LocalName localName, GenerationContext context) throws JavaGenerationException {
        
        // Determine what type of creature this name represents.
        //   Note that because of the order of lookups, this takes scoping into account.
        String varName = localName.getName();
        if (context.getLocalVarIndex(varName) != -1) {
            return encodeLocalVariableExpr(new LocalVariable(varName, localName.getTypeName()), context);

        } else if (context.getMethodVarIndex(varName) != -1) {
            return encodeMethodVariableExpr(new MethodVariable(varName), context);

        } else {
            return encodeJavaFieldExpr(new JavaField.Instance(null, varName, localName.getTypeName()), context);
        }              
    }

    /**
     * Encodes instructions to push the value of the local variable onto the operand stack.
     * @param variable
     * @param context
     * @return JavaTypeName the type of the result on the operand stack.
     */
    private static JavaTypeName encodeLocalVariableExpr(JavaExpression.LocalVariable variable, GenerationContext context) {
        
        MethodVisitor mv = context.getMethodVisitor();
        
        //get the local variable index from the context.       
        int localVarIndex = context.getLocalVarIndex(variable.getName());
        
        JavaTypeName varType = variable.getTypeName();
        
        mv.visitVarInsn(getLoadOpCode(varType), localVarIndex);
        
        return varType;
    }
    
    private static int getLoadOpCode(JavaTypeName varType) {
        
        //ASM automatically handles replacing ILOAD 0, ILOAD 1, ILOAD 2 and ILOAD 3 by the special
        //0 argument op codes ILOAD_0, ILOAD_1, ILOAD_2, and ILOAD_3 and similarly for the other
        //types.
        
        switch (varType.getTag()) {
        
            case JavaTypeName.VOID_TAG:            
                throw new IllegalArgumentException("Cannot load a local variable of void type.");
            
            case JavaTypeName.BOOLEAN_TAG:      
            case JavaTypeName.BYTE_TAG:               
            case JavaTypeName.SHORT_TAG:              
            case JavaTypeName.CHAR_TAG:                             
            case JavaTypeName.INT_TAG:
                return Opcodes.ILOAD;          
            
            case JavaTypeName.LONG_TAG:
                return Opcodes.LLOAD;
            
            case JavaTypeName.DOUBLE_TAG:
                return Opcodes.DLOAD;
            
            case JavaTypeName.FLOAT_TAG:
                return Opcodes.FLOAD;
            
            case JavaTypeName.ARRAY_TAG:           
            case JavaTypeName.OBJECT_TAG:  
                return Opcodes.ALOAD;
            
            default:
            {
                throw new IllegalArgumentException("Cannot load a local variable of type " + varType);
            }
        }                               
    }
    
    private static int getStoreOpCode(JavaTypeName varType) {
        
        //ASM automatically handles replacing ISTORE 0, ISTORE 1, ISTORE 2 and ISTORE 3 by the special
        //0 argument op codes ISTORE_0, ISTORE_1, ISTORE_2, and ISTORE_3 and similarly for the other
        //types.
        
        switch (varType.getTag()) {
        
            case JavaTypeName.VOID_TAG:            
                throw new IllegalArgumentException("Cannot load a local variable of void type.");
            
            case JavaTypeName.BOOLEAN_TAG:      
            case JavaTypeName.BYTE_TAG:               
            case JavaTypeName.SHORT_TAG:              
            case JavaTypeName.CHAR_TAG:                             
            case JavaTypeName.INT_TAG:
                return Opcodes.ISTORE;          
            
            case JavaTypeName.LONG_TAG:
                return Opcodes.LSTORE;
            
            case JavaTypeName.DOUBLE_TAG:
                return Opcodes.DSTORE;
            
            case JavaTypeName.FLOAT_TAG:
                return Opcodes.FSTORE;
            
            case JavaTypeName.ARRAY_TAG:           
            case JavaTypeName.OBJECT_TAG:  
                return Opcodes.ASTORE;
            
            default:
            {
                throw new IllegalArgumentException("Cannot load a local variable of type " + varType);
            }
        }                               
    }    

    /**
     * @param variable
     * @param context
     * @return the type of the result on the operand stack.
     */
    private static JavaTypeName encodeMethodVariableExpr(JavaExpression.MethodVariable variable, GenerationContext context) {
        
        MethodVisitor mv = context.getMethodVisitor();
        String varName = variable.getName();
        
        //get the local variable index from the context.       
        int methodVarIndex = context.getMethodVarIndex(varName);
        
        JavaTypeName varType = context.getMethodVarType(varName);
        
        mv.visitVarInsn(getLoadOpCode(varType), methodVarIndex);
        
        return varType;  
    }
       

    /**
     * @param assignment
     * @param context
     * @param retainValue  whether to leave on the stack the result of evaluating the expression. This is an optimization. 
     *    The normal case for an assignment expression is with retainValue = true. However, often assignments are used in
     *    statement form in which case the resulting value is immediately popped off the stack. Hence there is no point to
     *    push it onto the stack in the first place.
     * @return the type of the result on the operand stack (assuming retainValue is true).
     * @throws JavaGenerationException
     */
    private static JavaTypeName encodeAssignmentExpr(JavaExpression.Assignment assignment, GenerationContext context, boolean retainValue) throws JavaGenerationException {       

        MethodVisitor mv = context.getMethodVisitor();
        
        JavaExpression.Nameable lhs = assignment.getLeftHandSide();
                                    
        if (lhs instanceof LocalName) {
            // Determine what type of creature this name represents.
            //   Note that because of the order of lookups, this takes scoping into account.
            LocalName localName = (LocalName)lhs;
            String varName = localName.getName();
            if (context.getLocalVarIndex(varName) != -1) {
                lhs = new LocalVariable(varName, localName.getTypeName());
            } else if (context.getMethodVarIndex(varName) != -1) {
                lhs = new MethodVariable(varName);
            } else {
                lhs = new JavaField.Instance(null, varName, localName.getTypeName());
            }
        }
        
        // assign the value
        if (lhs instanceof JavaField.Instance) {                       
                        
            JavaField.Instance javaInstanceField = (JavaField.Instance)lhs;            
                  
            //push the reference to the Java field itself. We can't just call encodeJavaField because we need the type of the instance object.
            JavaExpression instance = javaInstanceField.getInstance();
            JavaTypeName instanceType;
            if (instance == null) {
                //use 'this' as the instance expression, so if the field is 'foo', then it is 'this.foo'.
                instanceType = encodeThis(context);               
                
            } else {
                instanceType = encodeExpr(instance, context);               
            }
                       
            //push the rhs expression
            // the type of the assignment takes on the type of the value (not the assigned variable).       
            JavaTypeName returnType = encodeExpr(assignment.getValue(), context);
            
            if (retainValue) {
                //duplicate the value so that a copy will be left over after assignment
                //field, value --->
                //value, field, value
                mv.visitInsn(getDupX1OpCode(returnType));
            }
            
            //do the assignment
            mv.visitFieldInsn(Opcodes.PUTFIELD, instanceType.getJVMInternalName(), javaInstanceField.getFieldName(), javaInstanceField.getFieldType().getJVMDescriptor());
            
            return returnType;
            
        } else if (lhs instanceof JavaField.Static) {
            
            JavaField.Static javaStaticField = (JavaField.Static)lhs;
                                   
            //push the rhs expression
            // the type of the assignment takes on the type of the value (not the assigned variable).       
            JavaTypeName returnType = encodeExpr(assignment.getValue(), context);
            
            if (retainValue) {
                //duplicate the value so that a copy will be left over after assignment
                //value --->
                //value, value
                mv.visitInsn(getDupOpCode(returnType));
            } 
            
            //do the assignment
            mv.visitFieldInsn(Opcodes.PUTSTATIC, javaStaticField.getInvocationClass().getJVMInternalName(), javaStaticField.getFieldName(), javaStaticField.getFieldType().getJVMDescriptor());            
            
            return returnType;
                   
        } else if (lhs instanceof LocalVariable) {
            LocalVariable localVariable = (LocalVariable)lhs;
                        
            int localVarIndex = context.getLocalVarIndex(localVariable.getName());
    
            //push the rhs expression
            JavaTypeName returnType = encodeExpr(assignment.getValue(), context);
            
            if (retainValue) {
                //duplicate the value so that a copy will be left over after assignment
                //value --->
                //value, value
                mv.visitInsn(getDupOpCode(returnType));
            }
                            
            //encode the store instruction
            mv.visitVarInsn(getStoreOpCode(localVariable.getTypeName()), localVarIndex);
            
            return returnType;
    
        } else if (lhs instanceof MethodVariable) {
            MethodVariable methodVariable = (MethodVariable)lhs;
            String varName = methodVariable.getName();
                        
            int methodVarIndex = context.getMethodVarIndex(varName);
            JavaTypeName methodVarType = context.getMethodVarType(varName);
    
            //push the rhs expression
            JavaTypeName returnType = encodeExpr(assignment.getValue(), context);
            
            if (retainValue) {
                //duplicate the value so that a copy will be left over after assignment
                //value --->
                //value, value
                mv.visitInsn(getDupOpCode(returnType));
            }
                            
            //encode the store instruction
            mv.visitVarInsn(getStoreOpCode(methodVarType), methodVarIndex);
            
            return returnType;                          
    
        } else if (lhs instanceof ArrayAccess) {
            ArrayAccess arrayAccess = (ArrayAccess)lhs;

            // Add the instructions to get the array ref.
            encodeExpr(arrayAccess.getArrayReference(), context);            
            
            // Add the instructions to evaluate the array index.
            encodeExpr(arrayAccess.getArrayIndex(), context);
                                      
            // add the instructions to evaluate the expression to assign.
            JavaTypeName returnType = encodeExpr(assignment.getValue(), context);           
            
            if (retainValue) {  
                //duplicate the value so that a copy will be left over after assignment
                //arrayRef index value --->
                //value arrayRef index value
                mv.visitInsn(getDupX2OpCode(returnType));               
            }   
            
            //encode the store array element instruction
            mv.visitInsn(getArrayStoreOpCode(returnType));
            
            return returnType;                       
        } 
        
        throw new JavaGenerationException("Cannot assign to this type of expression: " + lhs);       
    }
    
    /**    
     * @param type
     * @return the DUP or DUP2 instruction, depending on type.
     */
    private static int getDupOpCode (JavaTypeName type) {
        
       switch (type.getTag()) {                                
            case JavaTypeName.BOOLEAN_TAG:                     
            case JavaTypeName.BYTE_TAG:               
            case JavaTypeName.SHORT_TAG:              
            case JavaTypeName.CHAR_TAG:                             
            case JavaTypeName.INT_TAG:
            case JavaTypeName.FLOAT_TAG:              
            case JavaTypeName.ARRAY_TAG:           
            case JavaTypeName.OBJECT_TAG:  
                return Opcodes.DUP;
            
            case JavaTypeName.LONG_TAG:            
            case JavaTypeName.DOUBLE_TAG:
                return Opcodes.DUP2;
            
            case JavaTypeName.VOID_TAG:                                                                   
            default:
            {
                throw new IllegalArgumentException("invalid type: " + type);
            }
        }                               
    }    
    
    /**     
     * @param type
     * @return the DUP_X1 or DUP2_X1 instruction, depending on type.
     */
    private static int getDupX1OpCode (JavaTypeName type) {
        
       switch (type.getTag()) {                                
            case JavaTypeName.BOOLEAN_TAG:                     
            case JavaTypeName.BYTE_TAG:               
            case JavaTypeName.SHORT_TAG:              
            case JavaTypeName.CHAR_TAG:                             
            case JavaTypeName.INT_TAG:
            case JavaTypeName.FLOAT_TAG:              
            case JavaTypeName.ARRAY_TAG:           
            case JavaTypeName.OBJECT_TAG:  
                return Opcodes.DUP_X1;
            
            case JavaTypeName.LONG_TAG:            
            case JavaTypeName.DOUBLE_TAG:
                return Opcodes.DUP2_X1;
            
            case JavaTypeName.VOID_TAG:                                                                   
            default:
            {
                throw new IllegalArgumentException("invalid type: " + type);
            }
        }                               
    }
    
    /**     
     * @param type
     * @return the DUP_X2 or DUP2_X2 instruction, depending on type.
     */
    private static int getDupX2OpCode (JavaTypeName type) {
        
       switch (type.getTag()) {                                
            case JavaTypeName.BOOLEAN_TAG:                     
            case JavaTypeName.BYTE_TAG:               
            case JavaTypeName.SHORT_TAG:              
            case JavaTypeName.CHAR_TAG:                             
            case JavaTypeName.INT_TAG:
            case JavaTypeName.FLOAT_TAG:              
            case JavaTypeName.ARRAY_TAG:           
            case JavaTypeName.OBJECT_TAG:  
                return Opcodes.DUP_X2;
            
            case JavaTypeName.LONG_TAG:            
            case JavaTypeName.DOUBLE_TAG:
                return Opcodes.DUP2_X2;
            
            case JavaTypeName.VOID_TAG:                                                                   
            default:
            {
                throw new IllegalArgumentException("invalid type: " + type);
            }
        }                               
    }    

    /**
     * Encodes the Java code for a given Java operator expression.
     *   
     * @param operatorExpr the java operator expression  
     * @param context
     * @return JavaTypeName
     * @throws JavaGenerationException
     */    
    private static JavaTypeName encodeOperatorExpr(JavaExpression.OperatorExpression operatorExpr, GenerationContext context) throws JavaGenerationException{
       
        MethodVisitor mv = context.getMethodVisitor();
        
        JavaOperator operator = operatorExpr.getJavaOperator();
        String symbol = operator.getSymbol();
        JavaTypeName valueType = operator.getValueType();              
                      
        // Now carry out the operation according to its type.
        if (operator.isArithmeticOp()) {
            
            // Add the instructions to evaluate the first argument.
            JavaTypeName arg1Type = encodeExpr(operatorExpr.getArgument(0), context);  
            
            if (operatorExpr instanceof OperatorExpression.Unary) {
                
                if (symbol.equals("-")) {  // number negation
            
                    mv.visitInsn(getNegateOpCode(arg1Type));              
                    return arg1Type;
                }
                
                throw new JavaGenerationException("Unknown unary arithmetic operator " + symbol + ".");                
            }
            
            // Add an instruction to widen the argument if necessary.
            int wideningOpCode = getWideningOpCode(arg1Type, valueType);
            if (wideningOpCode != Opcodes.NOP) {
                mv.visitInsn(wideningOpCode);
            }
            
            // Add the instructions to evaluate the second argument.
            JavaTypeName arg2Type = encodeExpr(operatorExpr.getArgument(1), context);
            
            // Add an instruction to widen the argument if necessary.
            wideningOpCode = getWideningOpCode(arg2Type, valueType);
            if (wideningOpCode != Opcodes.NOP) {
                mv.visitInsn(wideningOpCode);
            }
            
            // Evaluate.
            mv.visitInsn(getArithmeticBinaryOpCode(symbol, valueType)); 
            
            return valueType;            
        }
        
        if (operator.isBitOp()) {
            // Add the instructions to evaluate the first argument.
            JavaTypeName arg1Type = encodeExpr(operatorExpr.getArgument(0), context);  
            
            // Add an instruction to widen the argument if necessary.
            int wideningOpCode = getWideningOpCode(arg1Type, valueType);
            if (wideningOpCode != Opcodes.NOP) {
                mv.visitInsn(wideningOpCode);
            }
            
            if (operatorExpr instanceof OperatorExpression.Unary) {
                if (symbol.equals("~")) {  // number negation
                    if (valueType == JavaTypeName.INT) {
                        mv.visitInsn(Opcodes.ICONST_M1);
                        mv.visitInsn(Opcodes.IXOR);
                        return valueType;
                    } else
                    if (valueType == JavaTypeName.LONG) {
                        encodePushLongValue (new Long(-1), context);
                        mv.visitInsn(Opcodes.LXOR);
                        return valueType;
                    }
                }
                
                throw new JavaGenerationException("Unknown unary arithmetic operator " + symbol + ".");                
            }
            
            // Add the instructions to evaluate the second argument.
            JavaTypeName arg2Type = encodeExpr(operatorExpr.getArgument(1), context);
            
            // If this is >>, >>>, or << we may need to narrow the second argument to an int
            if (symbol.equals(">>") || symbol.equals(">>>") || symbol.equals("<<")) {
                if (arg2Type == JavaTypeName.LONG) {
                    mv.visitInsn(Opcodes.L2I);
                }
            } else {
                // Add an instruction to widen the argument if necessary.
                wideningOpCode = getWideningOpCode(arg2Type, valueType);
                if (wideningOpCode != Opcodes.NOP) {
                    mv.visitInsn(wideningOpCode);
                }
            }
            
            // Evaluate.
            mv.visitInsn(getArithmeticBinaryOpCode(symbol, valueType)); 
            
            return valueType;            
            
        }
        
        if (operator.isLogicalOp() || operator.isRelationalOp()) {
            
            // Logical op:    {"!", "&&", "||"}
            // Relational op: {">", ">=", "<", "<=", "==", "!="}
            
            Label trueContinuation = new Label();
            Label falseContinuation = new Label();
                                                 
            encodeBooleanValuedOperatorHelper(operatorExpr, context, trueContinuation, falseContinuation);         
            return encodeThenTrueElseFalse(trueContinuation, falseContinuation, context);           
        }
                              
        if (operator == JavaOperator.STRING_CONCATENATION) {
                                 
            // Create an uninitialized StringBuilder, duplicate the reference (so we can invoke the initializer).
            mv.visitTypeInsn(Opcodes.NEW, "java/lang/StringBuilder");
            mv.visitInsn(Opcodes.DUP);
                        
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V");

            // append the first arg.
            JavaTypeName firstArgType = encodeExpr(operatorExpr.getArgument(0), context);                       
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", getAppendJVMDescriptor(firstArgType));
            
            // Append the results of evaluation of the second arg.
            // Note that, conveniently, StringBuilder has an append() method for all the different types.
            JavaTypeName secondArgType = encodeExpr(operatorExpr.getArgument(1), context);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", getAppendJVMDescriptor(secondArgType));
            
            // Call toString() on the result.           
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;");                         
            
            return JavaTypeName.STRING;
        }
        
        return encodeTernaryOperatorExpr((OperatorExpression.Ternary)operatorExpr, context);                           
    }
    
    private static JavaTypeName encodeThenTrueElseFalse(Label trueContinuation, Label falseContinuation, GenerationContext context) {
        
        MethodVisitor mv = context.getMethodVisitor();
        
        mv.visitLabel(trueContinuation);
        mv.visitInsn(Opcodes.ICONST_1);
        Label nextLabel = new Label();
        mv.visitJumpInsn(Opcodes.GOTO, nextLabel);
        mv.visitLabel(falseContinuation);
        mv.visitInsn(Opcodes.ICONST_0);
        mv.visitLabel(nextLabel);
        
        return JavaTypeName.BOOLEAN;
    }
    
    /**     
     * @param expr
     * @return true if JavaExpression represents the int literal 0.
     */
    private static boolean isLiteralIntZeroExpr(JavaExpression expr) {
        
        if (expr instanceof JavaExpression.LiteralWrapper) {
            
            Object literalValue = ((JavaExpression.LiteralWrapper)expr).getLiteralObject();
            return literalValue instanceof Integer && ((Integer)literalValue).intValue() == 0;            
        }
        
        return false;
    }  
    
    /**     
     * @param expr
     * @return true if expr is an operator expression for one of !, &&, ||, ==, !=, <, <=, > or >=.
     */
    private static boolean isBooleanValuedOperatorExpr(JavaExpression expr) {
        if (expr instanceof JavaExpression.OperatorExpression) {
            JavaOperator operator = ((JavaExpression.OperatorExpression)expr).getJavaOperator();
            return operator.isLogicalOp() || operator.isRelationalOp();
        }
        
        return false;        
    }
    
    private static void encodeBooleanValuedOperatorHelper(JavaExpression.OperatorExpression operatorExpr, GenerationContext context,
            Label trueContinuation, Label falseContinuation) throws JavaGenerationException{
        encodeBooleanValuedOperatorHelper(operatorExpr, context, trueContinuation, falseContinuation, true);
    }
    
    /**  
     * Boolean valued operators (!, &&, ||, ==, !=, <, <=, > and >=) are highly optimized during compilation to bytecode.
     * Here is a quick outline of the optimizations used:
     * -not (e1 && e2) is compiled as a single notAnd operator
     * -not (e1 || e2) is compiled as a single notOr operator
     * -not (not e) is optimized out.
     * -not (x < y) is compiled as x >= y for integral comparisons. A similar thing is done for not (double <), but it is not quite double >= because
     *  of NaN. However, there is special java bytecode support for treatment of this.
     * -Comparisons where the right-hand-side is an int 0 are treated more efficiently i.e. x > 0.
     * -Comparisons to null are treated specially i.e. x != null, x == null.  
     * -if the result of a boolean valued operator is used by the condition part of an if-then-else statement (or ternary operator) then
     *  the resulting true or false value is not pushed onto the stack and then tested. Rather we directly branch to the appropriate
     *  continuation.
     * -the most complicated optimization is that "trees" of boolean valued operators are effectively compiled as a single operator. 
     *  What this means is that the resulting "true" and "false" values are not popped onto the stack and consumed by subsequent operators
     *  but rather a "continuation style" is employed where we just jump to the correct next comparison.
     *  This saves an extra comparison per operator, as well as unecessary pushes of trues and falses compared to the naive compilation scheme. 
     *  The precise bytecode instructions used in the compilation schemes varies depending on context (see the endsWithTrueForm argument).
     * 
     * @param operatorExpr
     * @param context
     * @param trueContinuation label to jump to if the expression has a true value
     * @param falseContinuation label to jump to if the expression has a false value 
     * @param endsWithTrueForm operators are encoded as a series of tests with jumps where if none of the jumps are taken the operator slips
     *    through to the default case. This is usually "true" but if the "endsWithTrueForm" flag is set to false, then the default case will
     *    be false. For example, this is useful when encoding a boolean-valued operator that is the left argument of the || operator. 
     *    In that case we want the default case to proceed to evaluation of the second argument of ||.
     * @throws JavaGenerationException
     */
    private static void encodeBooleanValuedOperatorHelper(JavaExpression.OperatorExpression operatorExpr, GenerationContext context,
            Label trueContinuation, Label falseContinuation, boolean endsWithTrueForm) throws JavaGenerationException{
        
        MethodVisitor mv = context.getMethodVisitor();               
    
        JavaOperator operator = operatorExpr.getJavaOperator();
        String symbol = operator.getSymbol();
        JavaTypeName valueType = operator.getValueType();            
                    
        if (operator.isLogicalOp()) {
                         
            // Logical op:    {"!", "&&", "||"}
            // Note: conditional statements should not be handled here..
            //   eg. "if" conditional evaluation happens during "if" source generation.
            //   We can get here if, eg. printing the result of a conditional.            
            
            // boolean negation
            if (symbol.equals("!")) {
                                              
                JavaExpression arg0Expr = operatorExpr.getArgument(0);
                
                //attempt to optimize a variety of cases where not is composed with another boolean valued operator.
                
                if (arg0Expr instanceof JavaExpression.OperatorExpression) {
                    
                    if (arg0Expr instanceof JavaExpression.OperatorExpression.Binary) {
                                             
                        JavaExpression.OperatorExpression.Binary arg0BinaryOperatorExpr = (JavaExpression.OperatorExpression.Binary)arg0Expr;
                        JavaOperator arg0BinaryOperator = arg0BinaryOperatorExpr.getJavaOperator();
                                               
                        //not (expr1 && expr2) is encoded in a special way. Effectively there is a notAnd operator.
                                                                     
                        if (arg0BinaryOperator == JavaOperator.CONDITIONAL_AND) {
                                                                                  
                            //x notAnd y                    
                            //is encoded as                    
                            //if x == false then goto trueContinuation
                            //if y == true then goto falseContinuation
                            
                            ////what follows is a sample continuation in the case when a literal value is pushed onto the stack
                            //label trueContinuation:
                            //push true
                            //goto next
                            //label falseContinuation:
                            //push false
                            //next: 
                                                       
                            JavaExpression andOpArg0Expr = arg0BinaryOperatorExpr.getArgument(0); 
                            if (isBooleanValuedOperatorExpr(andOpArg0Expr)) {  
                                Label innerTrueContinuation = new Label();
                                encodeBooleanValuedOperatorHelper((JavaExpression.OperatorExpression)andOpArg0Expr, context, innerTrueContinuation, trueContinuation);                              
                                mv.visitLabel(innerTrueContinuation);
                            } else {
                                encodeExpr(andOpArg0Expr, context);               
                                mv.visitJumpInsn(Opcodes.IFEQ, trueContinuation);
                            }
                            
                            JavaExpression andOpArg1Expr = arg0BinaryOperatorExpr.getArgument(1);
                            if (isBooleanValuedOperatorExpr(andOpArg1Expr)) {
                                encodeBooleanValuedOperatorHelper((JavaExpression.OperatorExpression)andOpArg1Expr, context, falseContinuation, trueContinuation, !endsWithTrueForm);
                            } else {
                                encodeExpr(andOpArg1Expr, context);
                                if (endsWithTrueForm) {                                       
                                    mv.visitJumpInsn(Opcodes.IFNE, falseContinuation);                                    
                                } else {
                                    mv.visitJumpInsn(Opcodes.IFEQ, trueContinuation);                                   
                                }
                            }                              
                            
                            return;                                                                                                     
                        }
                        
                        //not (expr1 || expr2) is encoded in a special way. Effectively there is a notOr operator.
                        
                        if (arg0BinaryOperator == JavaOperator.CONDITIONAL_OR) {
                            
                            //x notOr y                    
                            //is encoded as                    
                            //if x == true then goto falseContinuation
                            //if y == true then goto falseContinuation
                            
                            ////what follows is a sample continuation in the case when a literal value is pushed onto the stack                           
                            //label trueContinuation:
                            //push true
                            //goto next
                            //label falseContinuation:
                            //push false
                            //next: 
                                                       
                            JavaExpression orOpArg0Expr = arg0BinaryOperatorExpr.getArgument(0); 
                            if (isBooleanValuedOperatorExpr(orOpArg0Expr)) {  
                                Label innerFalseContinuation = new Label();
                                //if x evaluates to false, we want to continue with evaluating y, this is why the "endsWithTrueForm" argument is false here.
                                //if x evaluates to false, then x notOr y returns true without needing to evaluate y. That is why the trueContinuation for x, is
                                //the falseContinuation for the call that encodes x.
                                encodeBooleanValuedOperatorHelper((JavaExpression.OperatorExpression)orOpArg0Expr, context, falseContinuation, innerFalseContinuation, false);                              
                                mv.visitLabel(innerFalseContinuation);
                            } else {
                                encodeExpr(orOpArg0Expr, context);               
                                mv.visitJumpInsn(Opcodes.IFNE, falseContinuation);
                            }
                            
                            JavaExpression orOpArg1Expr = arg0BinaryOperatorExpr.getArgument(1);
                            if (isBooleanValuedOperatorExpr(orOpArg1Expr)) {
                                encodeBooleanValuedOperatorHelper((JavaExpression.OperatorExpression)orOpArg1Expr, context, falseContinuation, trueContinuation, !endsWithTrueForm);
                            } else {
                                encodeExpr(orOpArg1Expr, context);   
                                if (endsWithTrueForm) {
                                    mv.visitJumpInsn(Opcodes.IFNE, falseContinuation);                                   
                                } else {
                                    mv.visitJumpInsn(Opcodes.IFEQ, trueContinuation);                                   
                                }
                            }
                                                                                   
                            return;                                                                          
                        }
                        
                        //try to optimize not composed with a boolean valued operator as a single operation
                        //for example, for int operators, not (x < y) is actually encoded as x >= y.                        
                        
                        JavaExpression.OperatorExpression.Binary notComposedOperatorExpr = arg0BinaryOperatorExpr.getNotComposedOperatorExpr();
                        if (notComposedOperatorExpr != null) {
                                                       
                            encodeBooleanValuedOperatorHelper(notComposedOperatorExpr, context, trueContinuation, falseContinuation, endsWithTrueForm);
                            return;
                        }
                                                
                        //not (x Double.< y) is encoded like x Double.>= y except that the opposite DCMP instruction is used.                           
                        //this is to handle NAN. Similar for the others.
                        
                        if (arg0BinaryOperator == JavaOperator.LESS_THAN_DOUBLE ||
                            arg0BinaryOperator == JavaOperator.LESS_THAN_EQUALS_DOUBLE ||
                            arg0BinaryOperator == JavaOperator.GREATER_THAN_DOUBLE ||
                            arg0BinaryOperator == JavaOperator.GREATER_THAN_EQUALS_DOUBLE) {
                                                                                   
                            //encode the first argument
                            JavaTypeName firstArgType = encodeExpr(arg0BinaryOperatorExpr.getArgument(0), context);
                            
                            // Add instructions to widen the first argument if necessary.
                            int wideningOpCode = getWideningOpCode(firstArgType, JavaTypeName.DOUBLE);
                            if (wideningOpCode != Opcodes.NOP) {
                                mv.visitInsn(wideningOpCode);                        
                            }
                                                  
                            //endcode the second argument
                            JavaExpression secondArgExpr = arg0BinaryOperatorExpr.getArgument(1);
                            JavaTypeName secondArgType = encodeExpr(secondArgExpr, context);
                            wideningOpCode = getWideningOpCode(secondArgType, JavaTypeName.DOUBLE);
                            if (wideningOpCode != Opcodes.NOP) {
                                mv.visitInsn(wideningOpCode);
                            }
                            
                            if (arg0BinaryOperator == JavaOperator.LESS_THAN_DOUBLE) {
                                
                                mv.visitInsn(Opcodes.DCMPG); 
                                if (endsWithTrueForm) {
                                    mv.visitJumpInsn(Opcodes.IFLT, falseContinuation);
                                } else {
                                    mv.visitJumpInsn(Opcodes.IFGE, trueContinuation);
                                }
                                
                            } else if (arg0BinaryOperator == JavaOperator.LESS_THAN_EQUALS_DOUBLE) {
                                
                                mv.visitInsn(Opcodes.DCMPG);
                                if (endsWithTrueForm) {
                                    mv.visitJumpInsn(Opcodes.IFLE, falseContinuation);
                                } else {
                                    mv.visitJumpInsn(Opcodes.IFGT, trueContinuation);
                                }
                                                          
                            } else if (arg0BinaryOperator == JavaOperator.GREATER_THAN_DOUBLE) {
                                
                                mv.visitInsn(Opcodes.DCMPL);
                                if (endsWithTrueForm) {
                                    mv.visitJumpInsn(Opcodes.IFGT, falseContinuation);
                                } else {
                                    mv.visitJumpInsn(Opcodes.IFLE, trueContinuation);
                                }
                               
                            } else if (arg0BinaryOperator == JavaOperator.GREATER_THAN_EQUALS_DOUBLE) {  
                                
                                mv.visitInsn(Opcodes.DCMPL);  
                                if (endsWithTrueForm) {
                                    mv.visitJumpInsn(Opcodes.IFGE, falseContinuation);
                                } else {
                                    mv.visitJumpInsn(Opcodes.IFLT, trueContinuation);
                                }
                                
                            } else {
                                
                                throw new JavaGenerationException("Expecting one of the double operators <, >, <= or >=.");
                            }
                            
                            return;
                        }                                              
                                                                                             
                        //fall through to the unoptimized case...
                                                       
                        
                    } else if (arg0Expr instanceof JavaExpression.OperatorExpression.Unary) {
                        
                        //"not (not expr)" is encoded as "id expr"
                        
                        JavaExpression.OperatorExpression.Unary arg0UnaryOperatorExpr = (JavaExpression.OperatorExpression.Unary)arg0Expr;
                        if (arg0UnaryOperatorExpr.getJavaOperator() != JavaOperator.LOGICAL_NEGATE) {
                            throw new JavaGenerationException("Unary logical negation expected.");
                        }
                        
                        JavaExpression expr = arg0UnaryOperatorExpr.getArgument(0);
                        if (isBooleanValuedOperatorExpr(expr)) {
                            encodeBooleanValuedOperatorHelper((JavaExpression.OperatorExpression)expr, context, trueContinuation, falseContinuation, endsWithTrueForm);
                        } else {
                            encodeExpr(expr, context);
                            if (endsWithTrueForm) {
                                mv.visitJumpInsn(Opcodes.IFEQ, falseContinuation);
                            } else {
                                mv.visitJumpInsn(Opcodes.IFNE, trueContinuation);
                            }
                        }
                        
                        return;
                    }                    
                }
                
                //!x 
                //is encoded as
                //if x == true then goto falseContinuation;
                
                ////what follows is a sample continuation in the case when a literal value is pushed onto the stack
                //push true;
                //goto next;
                //falseContinuation:
                //push false;
                //label next:                                   
                 
                encodeExpr(arg0Expr, context); 
                if (endsWithTrueForm) {
                    //Note that IFNE consumes a value on the stack.
                    mv.visitJumpInsn(Opcodes.IFNE, falseContinuation);
                } else {
                    mv.visitJumpInsn(Opcodes.IFEQ, trueContinuation);
                }
                
                return;               
            }           
            
            if (symbol.equals("&&")) {
                
                //x && y                    
                //is encoded as                    
                //if x == false then goto falseContinuation
                //if y == false then goto falseContinuation
                
                ////what follows is a sample continuation in the case when a literal value is pushed onto the stack
                //push true
                //goto next
                //label falseContinuation:
                //push false
                //label next:
                                
                JavaExpression arg0Expr = operatorExpr.getArgument(0);                
                if (isBooleanValuedOperatorExpr(arg0Expr)) {  
                    Label innerTrueContinuation = new Label();
                    encodeBooleanValuedOperatorHelper((JavaExpression.OperatorExpression)arg0Expr, context, innerTrueContinuation, falseContinuation);                              
                    mv.visitLabel(innerTrueContinuation);
                } else {
                    encodeExpr(arg0Expr, context);               
                    mv.visitJumpInsn(Opcodes.IFEQ, falseContinuation);
                }
                
                JavaExpression arg1Expr = operatorExpr.getArgument(1);
                if (isBooleanValuedOperatorExpr(arg1Expr)) {
                    encodeBooleanValuedOperatorHelper((JavaExpression.OperatorExpression)arg1Expr, context, trueContinuation, falseContinuation, endsWithTrueForm);
                } else {
                    encodeExpr(arg1Expr, context); 
                    if (endsWithTrueForm) {
                        mv.visitJumpInsn(Opcodes.IFEQ, falseContinuation);
                    } else {
                        mv.visitJumpInsn(Opcodes.IFNE, trueContinuation);
                    }
                }  
                
                return;
            }
            
            if (symbol.equals("||")) {
                
                //x || y
                //is encoded as
                //if x == true then goto trueContinuation
                //if y == false then goto falseContinuation
                
                ////what follows is a sample continuation in the case when a literal value is pushed onto the stack
                //push true
                //goto next
                //label falseContinuation:
                //push false
                //label next:
                
                JavaExpression arg0Expr = operatorExpr.getArgument(0);                
                if (isBooleanValuedOperatorExpr(arg0Expr)) {  
                    Label innerFalseContinuation = new Label();
                    //if x evaluates to false, we want to continue with evaluating y, this is why the "endsWithTrueForm" argument is false here.
                    encodeBooleanValuedOperatorHelper((JavaExpression.OperatorExpression)arg0Expr, context, trueContinuation, innerFalseContinuation, false);                                  
                    mv.visitLabel(innerFalseContinuation);
                } else {                
                    encodeExpr(arg0Expr, context);               
                    mv.visitJumpInsn(Opcodes.IFNE, trueContinuation);
                }
                
                JavaExpression arg1Expr = operatorExpr.getArgument(1);
                if (isBooleanValuedOperatorExpr(arg1Expr)){
                    encodeBooleanValuedOperatorHelper((JavaExpression.OperatorExpression)arg1Expr, context, trueContinuation, falseContinuation, endsWithTrueForm);
                } else {               
                    encodeExpr(arg1Expr, context);
                    if (endsWithTrueForm) {
                        mv.visitJumpInsn(Opcodes.IFEQ, falseContinuation);
                    } else {
                        mv.visitJumpInsn(Opcodes.IFNE, trueContinuation);
                    }
                }                                  
                
                return;
            }
            
            throw new JavaGenerationException("Unknown logical operator " + symbol + ".");
            
        } // if(operator.isLogicalOp()) 
        
        // A relational operator
        
        //one comment on the bytecode sequences: there is some subtle points here because of the treatment of special values e.g. such
        //as not a number, plus infinity, minus 0 etc in the double and float types. The code below is based on copying what the Java
        //compiler generates for simple functions such as:
        //double foo(double x, double y) {double z = x < y; return z;}
        
        //encode the first argument
        JavaTypeName firstArgType = encodeExpr(operatorExpr.getArgument(0), context);
        
        // Add instructions to widen the first argument if necessary.
        int wideningOpCode = getWideningOpCode(firstArgType, valueType);
        if (wideningOpCode != Opcodes.NOP) {
            mv.visitInsn(wideningOpCode);                        
        }
        
        //Deal with comparisons to null as a special case. Don't push the second argument, since the null is 
        //implicit in the bytecode instruction.
        JavaExpression secondArgExpr = operatorExpr.getArgument(1);
        final boolean compareToNull = secondArgExpr == LiteralWrapper.NULL;
        
        //Deal with comparisons to int zero as a special case. There are special 1 argument operators for this case.
        //javac makes use of this optimization. Interestingly, javac does not optimize the case when the first argument
        //is a literal int zero i.e. 0 < x, is not converted to x > 0 which then can make use of the 1 argument comparison.        
        final boolean compareToIntZero = isInternalIntType(valueType) && isLiteralIntZeroExpr(secondArgExpr);        
        
        if (!compareToNull && !compareToIntZero) {                        
            //endcode the second argument
            JavaTypeName secondArgType = encodeExpr(secondArgExpr, context);
            wideningOpCode = getWideningOpCode(secondArgType, valueType);
            if (wideningOpCode != Opcodes.NOP) {
                mv.visitInsn(wideningOpCode);
            }
        }
             
        // relational symbols: {">", ">=", "<", "<=", "==", "!="}
        if (symbol.equals(">")) {                                
            
            switch (valueType.getTag()) {
                case JavaTypeName.BYTE_TAG:
                case JavaTypeName.SHORT_TAG:
                case JavaTypeName.CHAR_TAG:                        
                case JavaTypeName.INT_TAG:
                {
                    if (endsWithTrueForm) {
                        if (compareToIntZero) {
                            mv.visitJumpInsn(Opcodes.IFLE, falseContinuation);
                        } else {
                            mv.visitJumpInsn(Opcodes.IF_ICMPLE, falseContinuation);
                        }
                    } else {
                        if (compareToIntZero) {
                            mv.visitJumpInsn(Opcodes.IFGT, trueContinuation);
                        } else {
                            mv.visitJumpInsn(Opcodes.IF_ICMPGT, trueContinuation);
                        }                        
                    }
                    break;
                }
                
                case JavaTypeName.LONG_TAG:
                {
                    mv.visitInsn(Opcodes.LCMP); 
                    if (endsWithTrueForm) {                                              
                        mv.visitJumpInsn(Opcodes.IFLE, falseContinuation);
                    } else {                                         
                        mv.visitJumpInsn(Opcodes.IFGT, trueContinuation);                        
                    }
                    break;
                }
                
                case JavaTypeName.DOUBLE_TAG:
                {
                    mv.visitInsn(Opcodes.DCMPL); 
                    if (endsWithTrueForm) {
                        mv.visitJumpInsn(Opcodes.IFLE, falseContinuation);
                    } else {
                        mv.visitJumpInsn(Opcodes.IFGT, trueContinuation);
                    }
                    break;
                }
                
                case JavaTypeName.FLOAT_TAG:
                {
                    mv.visitInsn(Opcodes.FCMPL);  
                    if (endsWithTrueForm) {
                        mv.visitJumpInsn(Opcodes.IFLE, falseContinuation);
                    } else {
                        mv.visitJumpInsn(Opcodes.IFGT, trueContinuation);
                    }
                    break;
                }
                
                default:
                    throw new IllegalArgumentException("Unsupported operand type for JVM > operator.");
            }                                                                                    
            
        } else if (symbol.equals(">=")) {              
            
            switch (valueType.getTag()) {
                case JavaTypeName.BYTE_TAG:
                case JavaTypeName.SHORT_TAG:
                case JavaTypeName.CHAR_TAG:                
                case JavaTypeName.INT_TAG:
                {
                    if (endsWithTrueForm) {
                        if (compareToIntZero) {
                            mv.visitJumpInsn(Opcodes.IFLT, falseContinuation);
                        } else {
                            mv.visitJumpInsn(Opcodes.IF_ICMPLT, falseContinuation);
                        }
                    } else {
                        if (compareToIntZero) {
                            mv.visitJumpInsn(Opcodes.IFGE, trueContinuation);
                        } else {
                            mv.visitJumpInsn(Opcodes.IF_ICMPGE, trueContinuation);
                        }                        
                    }
                    break;
                }
                
                case JavaTypeName.LONG_TAG:
                {
                    mv.visitInsn(Opcodes.LCMP);
                    if (endsWithTrueForm) {                                               
                        mv.visitJumpInsn(Opcodes.IFLT, falseContinuation);
                    } else {                                       
                        mv.visitJumpInsn(Opcodes.IFGE, trueContinuation);                        
                    }
                    break;
                }
                
                case JavaTypeName.DOUBLE_TAG:
                {
                    mv.visitInsn(Opcodes.DCMPL);
                    if (endsWithTrueForm) {
                        mv.visitJumpInsn(Opcodes.IFLT, falseContinuation);                        
                    } else {
                        mv.visitJumpInsn(Opcodes.IFGE, trueContinuation);
                    }
                    break;
                }
                
                case JavaTypeName.FLOAT_TAG:
                {
                    mv.visitInsn(Opcodes.FCMPL);
                    if (endsWithTrueForm) {
                        mv.visitJumpInsn(Opcodes.IFLT, falseContinuation);
                    } else {
                        mv.visitJumpInsn(Opcodes.IFGE, trueContinuation);
                    }
                    break;
                }
                
                default:
                    throw new IllegalArgumentException("Unsupported operand type for JVM >= operator.");
            }                                                                                        
                    
        } else if (symbol.equals("<")) {
            
            switch (valueType.getTag()) {
                 case JavaTypeName.BYTE_TAG:
                 case JavaTypeName.SHORT_TAG:
                 case JavaTypeName.CHAR_TAG:                 
                 case JavaTypeName.INT_TAG:
                 {
                     if (endsWithTrueForm) {
                         if (compareToIntZero) {
                             mv.visitJumpInsn(Opcodes.IFGE, falseContinuation);
                         } else {
                             mv.visitJumpInsn(Opcodes.IF_ICMPGE, falseContinuation);
                         }
                     } else {
                         if (compareToIntZero) {
                             mv.visitJumpInsn(Opcodes.IFLT, trueContinuation);
                         } else {
                             mv.visitJumpInsn(Opcodes.IF_ICMPLT, trueContinuation);
                         }                         
                     }
                     break;
                 }
                     
                 case JavaTypeName.LONG_TAG:
                 {
                     mv.visitInsn(Opcodes.LCMP);
                     if (endsWithTrueForm) {
                         mv.visitJumpInsn(Opcodes.IFGE, falseContinuation);
                     } else {
                         mv.visitJumpInsn(Opcodes.IFLT, trueContinuation);                         
                     }
                     break;
                 }
                     
                 case JavaTypeName.DOUBLE_TAG:
                 {
                     mv.visitInsn(Opcodes.DCMPG);
                     if (endsWithTrueForm) {
                         mv.visitJumpInsn(Opcodes.IFGE, falseContinuation);
                     } else {
                         mv.visitJumpInsn(Opcodes.IFLT, trueContinuation);
                     }                     
                     break;
                 }
                     
                 case JavaTypeName.FLOAT_TAG:
                 {
                     mv.visitInsn(Opcodes.FCMPG);
                     if (endsWithTrueForm) {
                         mv.visitJumpInsn(Opcodes.IFGE, falseContinuation);
                     } else {
                         mv.visitJumpInsn(Opcodes.IFLT, trueContinuation);                         
                     }
                     break;
                 }
                     
                 default:
                     throw new IllegalArgumentException("Unsupported operand type for JVM < operator.");
            }                                                                            
                
        } else if (symbol.equals("<=")) {
                                         
            switch (valueType.getTag()) {
                case JavaTypeName.BYTE_TAG:
                case JavaTypeName.SHORT_TAG:
                case JavaTypeName.CHAR_TAG:                 
                case JavaTypeName.INT_TAG:
                {
                    if (endsWithTrueForm) {
                        if (compareToIntZero) {
                            mv.visitJumpInsn(Opcodes.IFGT, falseContinuation);
                        } else {
                            mv.visitJumpInsn(Opcodes.IF_ICMPGT, falseContinuation);
                        }
                    } else {
                        if (compareToIntZero) {
                            mv.visitJumpInsn(Opcodes.IFLE, trueContinuation);
                        } else {
                            mv.visitJumpInsn(Opcodes.IF_ICMPLE, trueContinuation);
                        }                        
                    }
                    break;
                }
                
                case JavaTypeName.LONG_TAG:
                {
                    mv.visitInsn(Opcodes.LCMP);
                    if (endsWithTrueForm) {
                        mv.visitJumpInsn(Opcodes.IFGT, falseContinuation);
                    } else {
                        mv.visitJumpInsn(Opcodes.IFLE, trueContinuation);
                    }
                    break;
                }
                
                case JavaTypeName.DOUBLE_TAG:
                {
                    mv.visitInsn(Opcodes.DCMPG);
                    if (endsWithTrueForm) {
                        mv.visitJumpInsn(Opcodes.IFGT, falseContinuation);
                    } else {
                        mv.visitJumpInsn(Opcodes.IFLE, trueContinuation);
                    }
                    break;
                }
                
                case JavaTypeName.FLOAT_TAG:
                {
                    mv.visitInsn(Opcodes.FCMPG); 
                    if (endsWithTrueForm) {
                        mv.visitJumpInsn(Opcodes.IFGT, falseContinuation);
                    } else {
                        mv.visitJumpInsn(Opcodes.IFLE, trueContinuation);
                    }
                    break;
                }
                
                default:
                    throw new IllegalArgumentException("Unsupported operand type for JVM <= operator.");
            }                                                                                          

        } else if (symbol.equals("==")) {
            
            switch (valueType.getTag()) {
                case JavaTypeName.BOOLEAN_TAG:
                case JavaTypeName.BYTE_TAG:
                case JavaTypeName.SHORT_TAG:
                case JavaTypeName.CHAR_TAG:                
                case JavaTypeName.INT_TAG:
                {
                    if (endsWithTrueForm) {
                        if (compareToIntZero) {
                            mv.visitJumpInsn(Opcodes.IFNE, falseContinuation);
                        } else {
                            mv.visitJumpInsn(Opcodes.IF_ICMPNE, falseContinuation);
                        }
                    } else {
                        if (compareToIntZero) {
                            mv.visitJumpInsn(Opcodes.IFEQ, trueContinuation);
                        } else {
                            mv.visitJumpInsn(Opcodes.IF_ICMPEQ, trueContinuation);
                        }                        
                    }
                    break;
                }
                
                case JavaTypeName.LONG_TAG:
                {
                    mv.visitInsn(Opcodes.LCMP);
                    if (endsWithTrueForm) {
                        mv.visitJumpInsn(Opcodes.IFNE, falseContinuation);
                    } else {
                        mv.visitJumpInsn(Opcodes.IFEQ, trueContinuation);
                    }
                    break;
                }
                
                case JavaTypeName.DOUBLE_TAG:
                {
                    mv.visitInsn(Opcodes.DCMPL);  
                    if (endsWithTrueForm) {
                        mv.visitJumpInsn(Opcodes.IFNE, falseContinuation);
                    } else {
                        mv.visitJumpInsn(Opcodes.IFEQ, trueContinuation);
                    }
                    break;
                }
                
                case JavaTypeName.FLOAT_TAG:
                {
                    mv.visitInsn(Opcodes.FCMPL);
                    if (endsWithTrueForm) {
                        mv.visitJumpInsn(Opcodes.IFNE, falseContinuation);
                    } else {
                        mv.visitJumpInsn(Opcodes.IFEQ, trueContinuation);
                    }
                    break;
                }
                    
                case JavaTypeName.ARRAY_TAG:
                case JavaTypeName.OBJECT_TAG: 
                {
                    if (endsWithTrueForm) {
                        if (compareToNull) {
                            mv.visitJumpInsn(Opcodes.IFNONNULL, falseContinuation);
                        } else {
                            mv.visitJumpInsn(Opcodes.IF_ACMPNE, falseContinuation);
                        }
                    } else {
                        if (compareToNull) {
                            mv.visitJumpInsn(Opcodes.IFNULL, trueContinuation);
                        } else {
                            mv.visitJumpInsn(Opcodes.IF_ACMPEQ, trueContinuation);
                        }                        
                    }
                    break;
                }
                
                default:
                    throw new IllegalArgumentException("Unsupported operand type for JVM == operator.");
            }
                             
        } else if (symbol.equals("!=")) {
            
            switch (valueType.getTag()) {
                case JavaTypeName.BOOLEAN_TAG:
                case JavaTypeName.BYTE_TAG:
                case JavaTypeName.SHORT_TAG:
                case JavaTypeName.CHAR_TAG:                 
                case JavaTypeName.INT_TAG:
                {
                    if (endsWithTrueForm) {
                        if (compareToIntZero) {
                            mv.visitJumpInsn(Opcodes.IFEQ, falseContinuation);
                        } else {
                            mv.visitJumpInsn(Opcodes.IF_ICMPEQ, falseContinuation);
                        }
                    } else {
                        if (compareToIntZero) {
                            mv.visitJumpInsn(Opcodes.IFNE, trueContinuation);
                        } else {
                            mv.visitJumpInsn(Opcodes.IF_ICMPNE, trueContinuation);
                        }                        
                    }
                    break;
                }
                
                case JavaTypeName.LONG_TAG:
                {
                    mv.visitInsn(Opcodes.LCMP);  
                    if (endsWithTrueForm) {                                              
                        mv.visitJumpInsn(Opcodes.IFEQ, falseContinuation);
                    } else {
                        mv.visitJumpInsn(Opcodes.IFNE, trueContinuation);
                    }
                    break;
                }
                
                case JavaTypeName.DOUBLE_TAG:
                {
                    mv.visitInsn(Opcodes.DCMPL);                        
                    if (endsWithTrueForm) {                                              
                        mv.visitJumpInsn(Opcodes.IFEQ, falseContinuation);
                    } else {
                        mv.visitJumpInsn(Opcodes.IFNE, trueContinuation);
                    }
                    break;
                }
                
                case JavaTypeName.FLOAT_TAG:
                {
                    mv.visitInsn(Opcodes.FCMPL);                        
                    if (endsWithTrueForm) {                                              
                        mv.visitJumpInsn(Opcodes.IFEQ, falseContinuation);
                    } else {
                        mv.visitJumpInsn(Opcodes.IFNE, trueContinuation);
                    }
                    break;
                }
                    
                case JavaTypeName.ARRAY_TAG:
                case JavaTypeName.OBJECT_TAG:
                {
                    if (endsWithTrueForm) {
                        if (compareToNull) {
                            mv.visitJumpInsn(Opcodes.IFNULL, falseContinuation);
                        } else {
                            mv.visitJumpInsn(Opcodes.IF_ACMPEQ, falseContinuation);
                        }
                    } else {
                        if (compareToNull) {
                            mv.visitJumpInsn(Opcodes.IFNONNULL, trueContinuation);
                        } else {
                            mv.visitJumpInsn(Opcodes.IF_ACMPNE, trueContinuation);
                        }                        
                    }
                    break;
                }
                
                default:
                    throw new IllegalArgumentException("Unsupported operand type for JVM != operator.");
            }                
           
        } else {
            throw new JavaGenerationException("Unknown relational operator " + symbol + ".");
        }         
    }
    
    /**
     * A helper function to get the JVM descriptor of the right overload of StringBuilder.append().
     * @param typeToAppend the type of the argument supplied to the StringBuilder.append method.
     * @return the JVM descriptor of the appropriate append overload.
     */
    private static String getAppendJVMDescriptor(JavaTypeName typeToAppend) {
              
        switch (typeToAppend.getTag()) {  
        
            case JavaTypeName.VOID_TAG:
                throw new IllegalArgumentException();
            
            case JavaTypeName.BOOLEAN_TAG: return "(Z)Ljava/lang/StringBuilder;";                     
            case JavaTypeName.BYTE_TAG:    return "(B)Ljava/lang/StringBuilder;";          
            case JavaTypeName.SHORT_TAG:   return "(S)Ljava/lang/StringBuilder;";           
            case JavaTypeName.CHAR_TAG:    return "(C)Ljava/lang/StringBuilder;";                         
            case JavaTypeName.INT_TAG:     return "(I)Ljava/lang/StringBuilder;";              
            case JavaTypeName.LONG_TAG:    return "(J)Ljava/lang/StringBuilder;";              
            case JavaTypeName.DOUBLE_TAG:  return "(D)Ljava/lang/StringBuilder;";               
            case JavaTypeName.FLOAT_TAG:   return "(F)Ljava/lang/StringBuilder;";  
            
            case JavaTypeName.ARRAY_TAG: 
            {
                //there is a more efficient overload for char[] arguments (it does not copy the argument).
                if (typeToAppend.equals(JavaTypeName.CHAR_ARRAY)) {
                    return "([C)Ljava/lang/StringBuilder;";
                }
                
                return "(Ljava/lang/Object;)Ljava/lang/StringBuilder;";                
            }
            
            case JavaTypeName.OBJECT_TAG:
            {
                //there are more efficient overloads for String and StringBuilder arguments
                if (typeToAppend.equals(JavaTypeName.STRING)) {
                    return "(Ljava/lang/String;)Ljava/lang/StringBuilder;";
                    
                } else if (typeToAppend.equals(JavaTypeName.STRING_BUILDER)) {
                    return "(Ljava/lang/StringBuilder;)Ljava/lang/StringBuilder;";
                }
                
                return "(Ljava/lang/Object;)Ljava/lang/StringBuilder;";                
            }
                              
            default:                
                throw new IllegalArgumentException();                
        }                                         
    }
    
    private static JavaTypeName encodeTernaryOperatorExpr(JavaExpression.OperatorExpression.Ternary ternaryOperatorExpr, GenerationContext context) throws JavaGenerationException{
         
        MethodVisitor mv = context.getMethodVisitor();
        
        JavaExpression conditionExpr = ternaryOperatorExpr.getArgument(0);
        JavaExpression thenExpr = ternaryOperatorExpr.getArgument(1);
        JavaExpression elseExpr = ternaryOperatorExpr.getArgument(2);
        
        if (conditionExpr instanceof JavaExpression.OperatorExpression) {
            
            //generate more efficient code in the case of (boolean-valued-operator) ? thenExpr : elseExpr 
            
            //This case exists to handle the special case where an operator occurs as the child of an if-then-else (or ternary operator)
            //conditional. For example, in the situation:
            //
            // (x != null) ? thenExpr : elseExpr
            // 
            // we do not want to evaluate x != null to a boolean value, push that value on the stack,
            // and then test it prior to selecting the correct branch. Rather, we can combine the evaluation
            // and jump operations into a single step.
            
            JavaExpression.OperatorExpression operatorExpr = (JavaExpression.OperatorExpression)conditionExpr;
                                     
            JavaOperator operator = operatorExpr.getJavaOperator();
            String symbol = operator.getSymbol();
            
            Label trueContinuation = new Label();
            Label falseContinuation =  new Label();
            
            if (operator.isLogicalOp() || operator.isRelationalOp()) {
                
                encodeBooleanValuedOperatorHelper(operatorExpr, context, trueContinuation, falseContinuation);                                    
                return encodeThenExprElseExpr(trueContinuation, falseContinuation, thenExpr, elseExpr, context);                            
            } 
            
            throw new JavaGenerationException("Unrecognized boolean-valued conditional operator " + symbol + ".");                       
        }
                           
        //encode the boolean conditional
        encodeExpr(conditionExpr, context); 
               
        Label falseContinuation = new Label();
        mv.visitJumpInsn(Opcodes.IFEQ, falseContinuation); 
        
        return encodeThenExprElseExpr(null, falseContinuation, thenExpr, elseExpr, context);
    }
    
    private static JavaTypeName encodeThenExprElseExpr(Label trueContinuation, Label falseContinuation, JavaExpression thenExpr, JavaExpression elseExpr, GenerationContext context) throws JavaGenerationException {
        
        MethodVisitor mv = context.getMethodVisitor();
        
        if (trueContinuation != null) {
            mv.visitLabel(trueContinuation);
        }
        
        //encode the then-part expression
        JavaTypeName thenType = encodeExpr(thenExpr, context);        
        Label nextLabel = new Label();
        mv.visitJumpInsn(Opcodes.GOTO, nextLabel);
        mv.visitLabel(falseContinuation);
        
        //encode the else-part expression
        JavaTypeName elseType = encodeExpr(elseExpr, context);      
        mv.visitLabel(nextLabel);
                            
        //The Java language specification section 15.25 defines the rules for the return types of ternary operators.
        //These are complicated, and we don't support this. The basic case supported is when both static types are the same.
        //There is a hack to support the case when the 2 types are different:
        //This case happens a bit in our code generation. The proper fix would be to not create this situation during code generation.
              
        if (thenType.equals(elseType)) {
            return thenType;
          
        } else if (thenType.equals(JavaTypeName.RTVALUE)) {
            //this is a hack, see the above comment
            return thenType;
            
        } else if (elseType.equals(JavaTypeName.RTVALUE)) {
            //this is a hack, see the above comment
            return elseType;
            
        } else {
            throw new JavaGenerationException("The '?' operator must have then parts and else parts of exactly the same static types.");
        }                             
    }    

    /**
     * Creates the byte code for a given array access expression.
     *   The returned instruction list will cause the expression result to be pushed onto the stack.
     * @param arrayAccess the array access    
     * @param context
     * @return ExpressionCode the type of the result on the operand stack.
     * @throws JavaGenerationException
     */
    private static JavaTypeName encodeArrayAccessExpr(JavaExpression.ArrayAccess arrayAccess, GenerationContext context) throws JavaGenerationException {
        
        //encode the instructions to evaluate the array reference
        JavaTypeName.Reference.Array arrayType = (JavaTypeName.Reference.Array)encodeExpr(arrayAccess.getArrayReference(), context);
        
        //encode the instructions to evaluate the array index
        encodeExpr(arrayAccess.getArrayIndex(), context);
                       
        JavaTypeName elementType = arrayType.getIncrementalElementType();
        
        //load the array element
        context.getMethodVisitor().visitInsn(getArrayLoadOpCode(elementType));

        return elementType;
    } 
    
    /**
     * Creates the byte code for a given array length expression.
     *   The returned instruction list will cause the expression result to be pushed onto the stack.
     * @param arrayLength the array length expression    
     * @param context
     * @return ExpressionCode the type of the result on the operand stack.
     * @throws JavaGenerationException
     */
    private static JavaTypeName encodeArrayLengthExpr(JavaExpression.ArrayLength arrayLength, GenerationContext context) throws JavaGenerationException {

        //encode the instructions to evaluate the array reference
        encodeExpr(arrayLength.getArrayReference(), context);  
        
        //encode the arrayLength instruction
        context.getMethodVisitor().visitInsn(Opcodes.ARRAYLENGTH);

        return JavaTypeName.INT;
    }     
        
    /**
     * Pushes a reference to "this" onto the stack.
     * @param context
     * @return JavaTypeName the type of the result on the operand stack.     
     */
    private static JavaTypeName encodeThis(GenerationContext context) {        
        context.getMethodVisitor().visitVarInsn(Opcodes.ALOAD, 0);
        return context.getMethodVarType("this");
    }
    
    /**
     * Create the Java code for a given method invocation.
     *   The expression result to be pushed onto the stack.
     * @param mi the method invocation     
     * @param context
     * @return JavaTypeName the type of the result on the operand stack. 
     * @throws JavaGenerationException
     */
    private static JavaTypeName encodeMethodInvocationExpr(JavaExpression.MethodInvocation mi, GenerationContext context) throws JavaGenerationException {
                
        MethodInvocation.InvocationType invocationType = mi.getInvocationType();
        
        int invocationCode;
        JavaTypeName invocationClassType;
                        
        if (invocationType != MethodInvocation.InvocationType.STATIC) {
            
            JavaExpression invocationTarget = ((MethodInvocation.Instance)mi).getInvocationTarget();
            if (invocationTarget == null) {
                //push a reference to 'this' onto the stack.
                invocationClassType = encodeThis(context);               
            } else {
                //push a reference to the invoking expression onto the stack
                invocationClassType = encodeExpr(invocationTarget, context);                
            }  
            
            // The MethodInvocation may contain an explicit invoking class type.  If it does
            // use it in preference to the type of the invocation target.
            if (mi instanceof MethodInvocation.Instance && ((MethodInvocation.Instance)mi).getDeclaringClass() != null) {
                invocationClassType = ((MethodInvocation.Instance)mi).getDeclaringClass();
            }            
            
            if (invocationType == MethodInvocation.InvocationType.VIRTUAL) {
                invocationCode = Opcodes.INVOKEVIRTUAL;
                
            } else if (invocationType == MethodInvocation.InvocationType.INTERFACE) {
                if (invocationClassType.isInterface()) {
                    invocationCode = Opcodes.INVOKEINTERFACE;
                } else {
                    //if we invoke an interface method on a reference that is known to be a Class, then we must use invoke virtual
                    //to avoid getting a IncompatibleClassChangeError.
                    invocationCode = Opcodes.INVOKEVIRTUAL;
                }
                
            } else if (invocationType == MethodInvocation.InvocationType.SPECIAL) {
                invocationCode = Opcodes.INVOKESPECIAL;
                
            } else {
                throw new JavaGenerationException("Unknown invocation type: " + invocationType);
            }            
            
        } else {            
            //static invocation. No object reference needs to be pushed.            
            invocationCode = Opcodes.INVOKESTATIC;           
            invocationClassType = ((MethodInvocation.Static)mi).getInvocationClass();                          
        }
        
        //push the arguments onto the stack
        
        for (int i = 0, nArgs = mi.getNArgs(); i < nArgs; ++i) {
            encodeExpr(mi.getArg(i), context);           
        }
        
        //invoke the method                            
        context.getMethodVisitor().visitMethodInsn(invocationCode, invocationClassType.getJVMInternalName(), mi.getMethodName(), mi.getJVMMethodDescriptor());
        
        return mi.getReturnType();
    }  
          
    /**
     * Create the Java code for a given cast expression. This will cause the resulting casted object reference to
     * be pushed onto the operand stack.
     *  
     * @param castExpression the cast expression
     * @param context  
     * @return JavaTypeName the type of the result on the operand stack.        
     * @throws JavaGenerationException
     */
    private static JavaTypeName encodeCastExpr(JavaExpression.CastExpression castExpression, GenerationContext context) throws JavaGenerationException {
        
        MethodVisitor mv = context.getMethodVisitor();
        
        final JavaTypeName expressionToCastType = encodeExpr(castExpression.getExpressionToCast(), context);        
        final JavaTypeName castType = castExpression.getCastType();
        
        if (expressionToCastType.equals(castType)) {
            //no operation needed if the types are the same.
            return castType;
        }
        
        if (castType instanceof JavaTypeName.Reference) {
            //when the cast type is a object or array type, use the CHECKCAST instruction. This will fail bytecode verification if 
            //the expressionToCast type is a primitive type
          
            mv.visitTypeInsn(Opcodes.CHECKCAST, castType.getJVMInternalName());
            return castType;
        }                   
        
        //casting between primitive types.
        //There are 15 supported primitive conversions: I2L, I2F, I2D, L2I, L2F, L2D, F2I, F2L, F2D, D2I, D2L, D2F, I2B, I2C, I2S
        //Depending upon the expressionToCastType and castType, choose the appropriate instruction.
        
        final int conversionOpCode;
        switch (expressionToCastType.getTag()) {
    
            case JavaTypeName.VOID_TAG:              
            case JavaTypeName.BOOLEAN_TAG:
                throw new JavaGenerationException("Unsupported primitive cast.");
                
            case JavaTypeName.BYTE_TAG:   
            {
                switch (castType.getTag()) {
        
                    case JavaTypeName.VOID_TAG:              
                    case JavaTypeName.BOOLEAN_TAG:                                                                                                          
                        throw new JavaGenerationException("Unsupported primitive cast.");                        
                    
                    case JavaTypeName.BYTE_TAG:                        
                        //should be handled above as a no-op.
                        throw new IllegalArgumentException();   
                    
                    case JavaTypeName.SHORT_TAG:
                        conversionOpCode = Opcodes.I2S;
                        break;
                    
                    case JavaTypeName.CHAR_TAG:                    
                        conversionOpCode = Opcodes.I2C;
                        break;                    
                    
                    case JavaTypeName.INT_TAG:  
                        //no-op
                        return castType;                 
                    
                    case JavaTypeName.LONG_TAG:
                        conversionOpCode = Opcodes.I2L;
                        break;
                        
                    case JavaTypeName.DOUBLE_TAG:
                        conversionOpCode = Opcodes.I2D;
                        break;                        
                        
                    case JavaTypeName.FLOAT_TAG:
                        conversionOpCode = Opcodes.I2F;
                        break;
                        
                    case JavaTypeName.ARRAY_TAG:            
                    case JavaTypeName.OBJECT_TAG:
                        throw new JavaGenerationException("Cannot cast a primitive type to a reference type.");                                                       
                    
                    default:
                    {
                        throw new IllegalArgumentException();
                    }
                }
                
                break;
            }
                
            case JavaTypeName.SHORT_TAG:    
            {
                switch (castType.getTag()) {
        
                    case JavaTypeName.VOID_TAG:              
                    case JavaTypeName.BOOLEAN_TAG:                                                                                                          
                        throw new JavaGenerationException("Unsupported primitive cast.");                        
                    
                    case JavaTypeName.BYTE_TAG: 
                        conversionOpCode = Opcodes.I2B;
                        break;    
                    
                    case JavaTypeName.SHORT_TAG:
                        //should be handled above as a no-op.
                        throw new IllegalArgumentException(); 
                    
                    case JavaTypeName.CHAR_TAG:                    
                        conversionOpCode = Opcodes.I2C;
                        break;                    
                    
                    case JavaTypeName.INT_TAG:    
                        //no-op
                        return castType;                 
                    
                    case JavaTypeName.LONG_TAG:
                        conversionOpCode = Opcodes.I2L;
                        break;
                        
                    case JavaTypeName.DOUBLE_TAG:
                        conversionOpCode = Opcodes.I2D;
                        break;                        
                        
                    case JavaTypeName.FLOAT_TAG:
                        conversionOpCode = Opcodes.I2F;
                        break;
                        
                    case JavaTypeName.ARRAY_TAG:            
                    case JavaTypeName.OBJECT_TAG:
                        throw new JavaGenerationException("Cannot cast a primitive type to a reference type.");                                                       
                    
                    default:
                    {
                        throw new IllegalArgumentException();
                    }
                }
                
                break;
            }                
                
            case JavaTypeName.CHAR_TAG: 
            {
                switch (castType.getTag()) {
        
                    case JavaTypeName.VOID_TAG:              
                    case JavaTypeName.BOOLEAN_TAG:                                                                                                          
                        throw new JavaGenerationException("Unsupported primitive cast.");                        
                    
                    case JavaTypeName.BYTE_TAG: 
                        conversionOpCode = Opcodes.I2B;
                        break;    
                    
                    case JavaTypeName.SHORT_TAG:
                        conversionOpCode = Opcodes.I2S;
                        break;    
                    
                    case JavaTypeName.CHAR_TAG:                    
                        //should be handled above as a no-op.
                        throw new IllegalArgumentException();                    
                    
                    case JavaTypeName.INT_TAG:    
                        //no-op
                        return castType;                 
                    
                    case JavaTypeName.LONG_TAG:
                        conversionOpCode = Opcodes.I2L;
                        break;
                        
                    case JavaTypeName.DOUBLE_TAG:
                        conversionOpCode = Opcodes.I2D;
                        break;                        
                        
                    case JavaTypeName.FLOAT_TAG:
                        conversionOpCode = Opcodes.I2F;
                        break;
                        
                    case JavaTypeName.ARRAY_TAG:            
                    case JavaTypeName.OBJECT_TAG:
                        throw new JavaGenerationException("Cannot cast a primitive type to a reference type.");                                                       
                    
                    default:
                    {
                        throw new IllegalArgumentException();
                    }
                }
                
                break;
            }                
                
                
            case JavaTypeName.INT_TAG:
            {
                switch (castType.getTag()) {
        
                    case JavaTypeName.VOID_TAG:              
                    case JavaTypeName.BOOLEAN_TAG:                                                                                                          
                        throw new JavaGenerationException("Unsupported primitive cast.");                        
                    
                    case JavaTypeName.BYTE_TAG: 
                        conversionOpCode = Opcodes.I2B;
                        break;
                    
                    case JavaTypeName.SHORT_TAG:
                        conversionOpCode = Opcodes.I2S;
                        break;
                    
                    case JavaTypeName.CHAR_TAG:                    
                        conversionOpCode = Opcodes.I2C;
                        break;                    
                    
                    case JavaTypeName.INT_TAG:                    
                        //should be handled above as a no-op.
                        throw new IllegalArgumentException();                    
                    
                    case JavaTypeName.LONG_TAG:
                        conversionOpCode = Opcodes.I2L;
                        break;
                        
                    case JavaTypeName.DOUBLE_TAG:
                        conversionOpCode = Opcodes.I2D;
                        break;                        
                        
                    case JavaTypeName.FLOAT_TAG:
                        conversionOpCode = Opcodes.I2F;
                        break;
                        
                    case JavaTypeName.ARRAY_TAG:            
                    case JavaTypeName.OBJECT_TAG:
                        throw new JavaGenerationException("Cannot cast a primitive type to a reference type.");                                                       
                    
                    default:
                    {
                        throw new IllegalArgumentException();
                    }
                }
                
                break;
            }
                
            case JavaTypeName.LONG_TAG:
            {
                switch (castType.getTag()) {
        
                    case JavaTypeName.VOID_TAG:              
                    case JavaTypeName.BOOLEAN_TAG: 
                        throw new JavaGenerationException("Unsupported primitive cast.");
                        
                    case JavaTypeName.BYTE_TAG:  
                        mv.visitInsn(Opcodes.L2I);
                        conversionOpCode = Opcodes.I2B;
                        break;
                        
                    case JavaTypeName.SHORT_TAG: 
                        mv.visitInsn(Opcodes.L2I);
                        conversionOpCode = Opcodes.I2S;
                        break;
                        
                    case JavaTypeName.CHAR_TAG:    
                        mv.visitInsn(Opcodes.L2I);
                        conversionOpCode = Opcodes.I2C;
                        break;                   
                    
                    case JavaTypeName.INT_TAG:                    
                        conversionOpCode = Opcodes.L2I;
                        break;
                    
                    case JavaTypeName.LONG_TAG:
                        //should be handled above as a no-op.
                        throw new IllegalArgumentException();
                        
                    case JavaTypeName.DOUBLE_TAG:
                        conversionOpCode = Opcodes.L2D;
                        break;                        
                        
                    case JavaTypeName.FLOAT_TAG:
                        conversionOpCode = Opcodes.L2F;
                        break;
                        
                    case JavaTypeName.ARRAY_TAG:            
                    case JavaTypeName.OBJECT_TAG:
                        throw new JavaGenerationException("Cannot cast a primitive type to a reference type.");
                                      
                    default:
                    {
                        throw new IllegalArgumentException();
                    }
                }
                
                break;
            }
                
            case JavaTypeName.DOUBLE_TAG:
            {
                switch (castType.getTag()) {
        
                    case JavaTypeName.VOID_TAG:              
                    case JavaTypeName.BOOLEAN_TAG:  
                        throw new JavaGenerationException("Unsupported primitive cast.");
                        
                    case JavaTypeName.BYTE_TAG: 
                        mv.visitInsn(Opcodes.D2I);
                        conversionOpCode = Opcodes.I2B;
                        break;
                        
                    case JavaTypeName.SHORT_TAG: 
                        mv.visitInsn(Opcodes.D2I);
                        conversionOpCode = Opcodes.I2S;
                        break;
                        
                    case JavaTypeName.CHAR_TAG:                                           
                        mv.visitInsn(Opcodes.D2I);
                        conversionOpCode = Opcodes.I2C;
                        break;              
                    
                    case JavaTypeName.INT_TAG:                    
                        conversionOpCode = Opcodes.D2I;
                        break;
                    
                    case JavaTypeName.LONG_TAG:
                        conversionOpCode = Opcodes.D2L;
                        break;
                        
                    case JavaTypeName.DOUBLE_TAG:
                         //should be handled above as a no-op.
                        throw new IllegalArgumentException();                  
                        
                    case JavaTypeName.FLOAT_TAG:
                        conversionOpCode = Opcodes.D2F;
                        break;
                        
                    case JavaTypeName.ARRAY_TAG:            
                    case JavaTypeName.OBJECT_TAG:
                        throw new JavaGenerationException("Cannot cast a primitive type to a reference type.");                                                           
                    
                    default:
                    {
                        throw new IllegalArgumentException();
                    }
                }
                
                break;
            }
                
            case JavaTypeName.FLOAT_TAG:
            {
                switch (castType.getTag()) {
        
                    case JavaTypeName.VOID_TAG:              
                    case JavaTypeName.BOOLEAN_TAG:
                        throw new JavaGenerationException("Unsupported primitive cast.");
                        
                    case JavaTypeName.BYTE_TAG:  
                        mv.visitInsn(Opcodes.F2I);
                        conversionOpCode = Opcodes.I2B;
                        break;
                        
                    case JavaTypeName.SHORT_TAG:  
                        mv.visitInsn(Opcodes.F2I);
                        conversionOpCode = Opcodes.I2S;
                        break;
                        
                    case JavaTypeName.CHAR_TAG:                                           
                        mv.visitInsn(Opcodes.F2I);
                        conversionOpCode = Opcodes.I2C;
                        break;                      
                    
                    case JavaTypeName.INT_TAG:                    
                        conversionOpCode = Opcodes.F2I;
                        break;
                    
                    case JavaTypeName.LONG_TAG:
                        conversionOpCode = Opcodes.F2L;
                        break;
                        
                    case JavaTypeName.DOUBLE_TAG:
                        conversionOpCode = Opcodes.F2D;
                        break;                        
                        
                    case JavaTypeName.FLOAT_TAG:
                        //should be handled above as a no-op.
                        throw new IllegalArgumentException();
                        
                    case JavaTypeName.ARRAY_TAG:            
                    case JavaTypeName.OBJECT_TAG:
                        throw new JavaGenerationException("Cannot cast a primitive type to a reference type.");
                                   
                    default:
                    {
                        throw new IllegalArgumentException();
                    }
                }
                
                break;
            }
                
            case JavaTypeName.ARRAY_TAG:            
            case JavaTypeName.OBJECT_TAG:
                throw new JavaGenerationException("Cannot cast a reference type to a primitive type.");                                         
            
            default:
            {
                throw new IllegalArgumentException();
            }                
        }       
        
        mv.visitInsn(conversionOpCode);
        return castType;      
    }       
    
    
    /**
     * Creates and initializes a 1-dimensional array (as specified by arrayCreationExpr) and then pushes a reference
     * to the array onto the stack.
     * 
     * @param arrayCreationExpr
     * @param context
     * @return JavaTypeName the type of the result on the operand stack. 
     * @throws JavaGenerationException
     */
    private static JavaTypeName encodeArrayCreationExpr(JavaExpression.ArrayCreationExpression arrayCreationExpr, GenerationContext context) throws JavaGenerationException {
        
        MethodVisitor mv = context.getMethodVisitor();
        final int nArrayElements = arrayCreationExpr.getNElementValues();
                
        //push the n of elements of the array onto the stack.       
        encodePushIntValue(nArrayElements, context);        
                                     
        JavaTypeName arrayElementType = arrayCreationExpr.getArrayElementTypeName();
        switch (arrayElementType.getTag()) {     
           
            case JavaTypeName.VOID_TAG:
                throw new JavaGenerationException("Cannot have an array of with void element types.");
                
            case JavaTypeName.BOOLEAN_TAG:           
            case JavaTypeName.BYTE_TAG:                      
            case JavaTypeName.SHORT_TAG:           
            case JavaTypeName.CHAR_TAG:           
            case JavaTypeName.INT_TAG:           
            case JavaTypeName.LONG_TAG:           
            case JavaTypeName.DOUBLE_TAG:             
            case JavaTypeName.FLOAT_TAG:
            {                               
                //push the instruction to create a 1-dimensonal array of primitive values
                mv.visitIntInsn(Opcodes.NEWARRAY, getNewArrayArgCode(arrayElementType));    
                break;
            }
                
            case JavaTypeName.ARRAY_TAG:
            {
                throw new JavaGenerationException("JavaExpression.ArrayCreationExpression supports only 1 dimensional arrays.");                
            }
            
            case JavaTypeName.OBJECT_TAG:
            {                                                                                   
                //push the instruction to create a 1-dimensonal array of reference values
                mv.visitTypeInsn(Opcodes.ANEWARRAY, arrayElementType.getJVMInternalName());                   
                break;
            }                              
            
            default:
            {
                throw new IllegalArgumentException();
            }
        }
                    
        final int arrayElemStoreCode = getArrayStoreOpCode(arrayElementType);
        
        //now initialize the elements of the array
        for (int i = 0; i < nArrayElements; ++i) {                    
            //duplicate the array reference on the stack
            mv.visitInsn(Opcodes.DUP);
            
            //push i
            encodePushIntValue(i, context);
            
            //push the code to evalaute the ith element expression
            encodeExpr(arrayCreationExpr.getElementValue(i), context);
            
            //array[i] = elementExpr           
            mv.visitInsn(arrayElemStoreCode);                    
        }        
                                
        return arrayElementType.makeArrayType();
    }
  
    /**
     * Create the Java code for a given class instance creation expression. The new object reference will be pushed onto
     * the operand stack. 
     * 
     * @param cice the class instance creation expression
     * @param context 
     * @return JavaTypeName    
     * @throws JavaGenerationException
     */
    private static JavaTypeName encodeClassInstanceCreationExpr(final JavaExpression.ClassInstanceCreationExpression cice, final GenerationContext context) throws JavaGenerationException {
        
        final MethodVisitor mv = context.getMethodVisitor();
        final JavaTypeName classType = cice.getClassName();        
        if (classType instanceof JavaTypeName.Reference.Array) {
            
            final JavaTypeName.Reference.Array arrayType = (JavaTypeName.Reference.Array)classType;
            final int nSizedDims = cice.getNArgs();
            if (nSizedDims == 1) {
                
                //for example, new String[10][][] will hit this case since it has 1 sized dimension (even though a multi-dimensional array is 
                //being created
                
                //push the size of the dimension
                encodeExpr(cice.getArg(0), context);
                                
                final JavaTypeName arrayElementType = arrayType.getIncrementalElementType();
                switch (arrayElementType.getTag()) {     
                   
                    case JavaTypeName.VOID_TAG:
                        throw new JavaGenerationException("Cannot have an array of with void element types.");
                        
                    case JavaTypeName.BOOLEAN_TAG:                    
                    case JavaTypeName.BYTE_TAG:                              
                    case JavaTypeName.SHORT_TAG:                   
                    case JavaTypeName.CHAR_TAG:                   
                    case JavaTypeName.INT_TAG:                   
                    case JavaTypeName.LONG_TAG:                 
                    case JavaTypeName.DOUBLE_TAG:                    
                    case JavaTypeName.FLOAT_TAG:
                    {                       
                        //push the instruction to create a 1-dimensonal array of primitive values
                        mv.visitIntInsn(Opcodes.NEWARRAY, getNewArrayArgCode(arrayElementType));                           
                        break;
                    }
                                                
                    case JavaTypeName.ARRAY_TAG:                   
                    case JavaTypeName.OBJECT_TAG:
                    {                                                              
                        //push the instruction to create a 1-dimensonal array of reference values
                        mv.visitTypeInsn(Opcodes.ANEWARRAY, arrayElementType.getJVMInternalName());                        
                        break;
                    }                                               
                    
                    default:
                    {
                        throw new IllegalArgumentException();
                    }
                }                
                
                return arrayType;
                
            } else {
                //the case of multi-dimensional arrays where more than 1 sizing dimension is supplied.
                                
                // push args onto the stack
                for (int i = 0; i < nSizedDims; i++) {
                    encodeExpr(cice.getArg(i), context);               
                }
                
                mv.visitMultiANewArrayInsn(arrayType.getJVMInternalName(), nSizedDims);
                
                return arrayType;
            }
                                                                     
        } else if (classType instanceof JavaTypeName.Reference.Object) {
            
            String internalClassName = classType.getJVMInternalName();
            
            // create uninitialized object, duplicate the ref.
            mv.visitTypeInsn(Opcodes.NEW, internalClassName);
            mv.visitInsn(Opcodes.DUP);
            
             // push args onto the stack
            for (int i = 0, nArgs = cice.getNArgs(); i < nArgs; i++) {
                encodeExpr(cice.getArg(i), context);               
            }
            
            //descriptor for the constructor
            StringBuilder descriptor = new StringBuilder("(");
            for (int i = 0, nArgs = cice.getNArgs(); i < nArgs; ++i) {
                descriptor.append(cice.getParamType(i).getJVMDescriptor());
            }
            descriptor.append(")V");
            
            // initialize - consumes the args and the duplicate reference.
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, internalClassName, "<init>", descriptor.toString()); 
            
            return classType;
            
        } else {
            throw new JavaGenerationException("cannot create a new instance of a primitive type.");
        }              
    }       
    
    /**
     * Create the Java code for a given instanceof expression, pushing the result onto the operand stack.
     *   
     * @param instanceOf the instanceof expression
     * @param context 
     * @return JavaTypeName the type of the result on the operand stack.
     * @throws JavaGenerationException               
     */
    private static JavaTypeName encodeInstanceOfExpr(JavaExpression.InstanceOf instanceOf, GenerationContext context) throws JavaGenerationException {
        
        //push the expression to test onto the operand stack
        encodeExpr(instanceOf.getJavaExpression(), context);
        
        //endcode the INSTANCEOF instruction
        context.getMethodVisitor().visitTypeInsn(Opcodes.INSTANCEOF, instanceOf.getReferenceType().getJVMInternalName());
        
        return JavaTypeName.BOOLEAN;
    }    

    /**
     * Creates the Java code for a given class literal expression, pushing the result onto the operand stack.
     * @param classLiteral the class literal expression.
     * @param context the generation context.
     * @return the type of the result on the operand stack.
     */
    private static JavaTypeName encodeClassLiteralExpr(ClassLiteral classLiteral, GenerationContext context) {

        final JavaTypeName referentType = classLiteral.getReferentType();

        final MethodVisitor methodVisitor = context.getMethodVisitor();
        
        switch (referentType.getTag()) {

        // The primitive types (and void) are handled specially by javac - it generates code to access the static TYPE field
        // in the corresponding boxed type.
        case JavaTypeName.VOID_TAG:
            methodVisitor.visitFieldInsn(Opcodes.GETSTATIC,
                JavaTypeName.VOID_OBJECT.getJVMInternalName(), "TYPE", JavaTypeName.CLASS.getJVMDescriptor());
            break;

        case JavaTypeName.BOOLEAN_TAG:
            methodVisitor.visitFieldInsn(Opcodes.GETSTATIC,
                JavaTypeName.BOOLEAN_OBJECT.getJVMInternalName(), "TYPE", JavaTypeName.CLASS.getJVMDescriptor());
            break;

        case JavaTypeName.BYTE_TAG:
            methodVisitor.visitFieldInsn(Opcodes.GETSTATIC,
                JavaTypeName.BYTE_OBJECT.getJVMInternalName(), "TYPE", JavaTypeName.CLASS.getJVMDescriptor());
            break;

        case JavaTypeName.SHORT_TAG:
            methodVisitor.visitFieldInsn(Opcodes.GETSTATIC,
                JavaTypeName.SHORT_OBJECT.getJVMInternalName(), "TYPE", JavaTypeName.CLASS.getJVMDescriptor());
            break;

        case JavaTypeName.CHAR_TAG:
            methodVisitor.visitFieldInsn(Opcodes.GETSTATIC,
                JavaTypeName.CHARACTER_OBJECT.getJVMInternalName(), "TYPE", JavaTypeName.CLASS.getJVMDescriptor());
            break;

        case JavaTypeName.INT_TAG:
            methodVisitor.visitFieldInsn(Opcodes.GETSTATIC,
                JavaTypeName.INTEGER_OBJECT.getJVMInternalName(), "TYPE", JavaTypeName.CLASS.getJVMDescriptor());
            break;

        case JavaTypeName.LONG_TAG:
            methodVisitor.visitFieldInsn(Opcodes.GETSTATIC,
                JavaTypeName.LONG_OBJECT.getJVMInternalName(), "TYPE", JavaTypeName.CLASS.getJVMDescriptor());
            break;

        case JavaTypeName.DOUBLE_TAG:
            methodVisitor.visitFieldInsn(Opcodes.GETSTATIC,
                JavaTypeName.DOUBLE_OBJECT.getJVMInternalName(), "TYPE", JavaTypeName.CLASS.getJVMDescriptor());
            break;

        case JavaTypeName.FLOAT_TAG:
            methodVisitor.visitFieldInsn(Opcodes.GETSTATIC,
                JavaTypeName.FLOAT_OBJECT.getJVMInternalName(), "TYPE", JavaTypeName.CLASS.getJVMDescriptor());
            break;

        case JavaTypeName.ARRAY_TAG:           
        case JavaTypeName.OBJECT_TAG:
            // For array and reference types, we generate the Class constant via Java 5's upgraded ldc opcode
            methodVisitor.visitLdcInsn(Type.getType(referentType.getJVMDescriptor()));
            break;

        default:
            throw new IllegalArgumentException("Unrecognized java type: " + referentType);
        }
        
        return JavaTypeName.CLASS;
    }

    /**
     * Get the Java code for a given literal wrapper.
     *   The returned instruction list will cause the expression result to be pushed onto the stack.
     * @param literalWrapper the literal wrapper
     * @param context the generation context
     * @return JavaTypeName the type of the result on the operand stack.
     * @throws JavaGenerationException 
     */
    private static JavaTypeName encodeLiteralExpr(JavaExpression.LiteralWrapper literalWrapper, GenerationContext context) throws JavaGenerationException {
       
        // Integer, Double, String, Character, Boolean, Byte, Short, Float, Long, null;        
        //note that byte, short, char, and boolean are implicitly converted and pushed as int values on the stack.
        
        Object literalObject = literalWrapper.getLiteralObject();
        if (literalObject instanceof Integer) {
            return encodePushIntegerValue((Integer)literalObject, context);           

        } else if (literalObject instanceof Boolean) {
            return encodePushBooleanValue((Boolean)literalObject, context);           

        } else if (literalObject instanceof Double) {
            return encodePushDoubleValue((Double)literalObject, context);            

        } else if (literalObject instanceof String) {
            return encodePushStringValue((String)literalObject, context);            

        } else if (literalObject instanceof Character) {
            encodePushIntValue(((Character)literalObject).charValue(), context);
            return JavaTypeName.CHAR;

        } else if (literalObject instanceof Long) {            
            return encodePushLongValue((Long)literalObject, context);           

        } else if (literalObject instanceof Byte) {
            encodePushIntValue(((Byte)literalObject).byteValue(), context);
            return JavaTypeName.BYTE;

        } else if (literalObject instanceof Short) {
            encodePushIntValue(((Short)literalObject).shortValue(), context); 
            return JavaTypeName.SHORT;

        } else if (literalObject instanceof Float) {
            return encodePushFloatValue((Float)literalObject, context);           

        } else if (literalObject == null) {
            return encodePushNullValue(context);          

        } else {
            throw new JavaGenerationException("Unrecognized literal type: " + literalObject.getClass());
        }
    } 
    
    /**
     * Encodes instructions to push an int value onto the operand stack.
     * Does not reallocate the wrapper argument 'value'.
     * @param value to be pushed 
     * @param context
     * @return JavaTypeName the type of the result on the operand stack.      
     */
    private static JavaTypeName encodePushIntegerValue(Integer value, GenerationContext context) {
        
        MethodVisitor mv = context.getMethodVisitor();
        
        int v = value.intValue();
        
        if (v >= -1 && v <= 5) {          
            // Use ICONST_n
            mv.visitInsn(Opcodes.ICONST_0 + v);
            
        } else if (v >= Byte.MIN_VALUE && v <= Byte.MAX_VALUE) {          
            // Use BIPUSH
            mv.visitIntInsn(Opcodes.BIPUSH, (byte)v);
            
        } else if (v >= Short.MIN_VALUE && v <= Short.MAX_VALUE) {          
            // Use SIPUSH
            mv.visitIntInsn(Opcodes.SIPUSH, (short)v);
            
        } else {          
            // If everything fails create a Constant pool entry
            mv.visitLdcInsn(value);         
        }
        
        return JavaTypeName.INT;
    }
    
    /**
     * Encodes instructions to push an int value onto the operand stack.
     * May need to box the argument 'value' if it is sufficiently large that
     * it needs to go into the constant pool.
     * @param value to be pushed 
     * @param context
     * @return JavaTypeName the type of the result on the operand stack. 
     */
    private static JavaTypeName encodePushIntValue(int value, GenerationContext context) { 
        
        MethodVisitor mv = context.getMethodVisitor();
        
        if (value >= -1 && value <= 5) {          
            // Use ICONST_n
            mv.visitInsn(Opcodes.ICONST_0 + value);
            
        } else if (value >= Byte.MIN_VALUE && value <= Byte.MAX_VALUE) {          
            // Use BIPUSH
            mv.visitIntInsn(Opcodes.BIPUSH, (byte)value);
            
        } else if (value >= Short.MIN_VALUE && value <= Short.MAX_VALUE) {          
            // Use SIPUSH
            mv.visitIntInsn(Opcodes.SIPUSH, (short)value);
            
        } else {          
            // If everything fails create a Constant pool entry
            mv.visitLdcInsn(Integer.valueOf(value));         
        }
        
        return JavaTypeName.INT;
    }    
    
    /**
     * Encodes instructions to push a boolean value onto the operand stack.
     * @param value to be pushed    
     * @param context
     * @return JavaTypeName the type of the result on the operand stack.      
     */
    private static JavaTypeName encodePushBooleanValue(Boolean value, GenerationContext context) {
        MethodVisitor mv = context.getMethodVisitor();
        
        boolean v = value.booleanValue();
        if (v) {
            mv.visitInsn(Opcodes.ICONST_1);
        } else {
            mv.visitInsn(Opcodes.ICONST_0);
        }
                
        return JavaTypeName.BOOLEAN;
    }
    
    /**
     * Encodes instructions to push a float value onto the operand stack.
     * @param value to be pushed
     * @param context
     * @return JavaTypeName the type of the result on the operand stack.        
     */    
    private static JavaTypeName encodePushFloatValue(Float value, GenerationContext context) {
        
        MethodVisitor mv = context.getMethodVisitor();
        
        float v = value.floatValue();
        
        if (v == 0.0) {
            mv.visitInsn(Opcodes.FCONST_0);
            
        } else if (v == 1.0) {
            mv.visitInsn(Opcodes.FCONST_1);
            
        } else if (v == 2.0) {
            mv.visitInsn(Opcodes.FCONST_2);
            
        } else {
            //Create a Constant pool entry
            mv.visitLdcInsn(value);
        }
        
        return JavaTypeName.FLOAT;
    }
    
    /**
     * Encodes instructions to push a long value onto the operand stack.
     * @param value to be pushed
     * @param context
     * @return JavaTypeName the type of the result on the operand stack.      
     */    
    private static JavaTypeName encodePushLongValue(Long value, GenerationContext context) {
        
        MethodVisitor mv = context.getMethodVisitor();
        
        long v = value.longValue();
        
        if (v == 0) {
            mv.visitInsn(Opcodes.LCONST_0);
            
        } else if (v == 1) {
            mv.visitInsn(Opcodes.LCONST_1);
            
        } else {
            // Create a Constant pool entry
            mv.visitLdcInsn(value);            
        }
        
        return JavaTypeName.LONG;
    }
    
    /**
     * Encodes instructions to push a double value onto the operand stack.
     * @param value to be pushed
     * @param context
     * @return JavaTypeName the type of the result on the operand stack.     
     */    
    private static JavaTypeName encodePushDoubleValue(Double value, GenerationContext context) {
        
        MethodVisitor mv = context.getMethodVisitor();

        double v = value.doubleValue();

        if (v == 0.0) {
            mv.visitInsn(Opcodes.DCONST_0);           
            
        } else if (v == 1.0) {
            mv.visitInsn(Opcodes.DCONST_1);         
            
        } else {
            // Create a Constant pool entry
            mv.visitLdcInsn(value);           
        }
        
        return JavaTypeName.DOUBLE;
    }
    
    /**
     * Encodes instructions to push a String value onto the operand stack.
     * @param value to be pushed. Must not be null.
     * @param context
     * @return JavaTypeName the type of the result on the operand stack.          
     */    
    private static JavaTypeName encodePushStringValue(String value, GenerationContext context) {
        
        MethodVisitor mv = context.getMethodVisitor();
        
        // Create a Constant pool entry
        mv.visitLdcInsn(value);
        
        return JavaTypeName.STRING;
    }
    
    /**
     * Encodes instructions to push a null object reference onto the operand stack.
     * @param context 
     * @return JavaTypeName the type of the result on the operand stack.
     */     
    private static JavaTypeName encodePushNullValue(GenerationContext context) {
        MethodVisitor mv = context.getMethodVisitor();
        mv.visitInsn(Opcodes.ACONST_NULL);
        
        return JavaTypeName.OBJECT;
    } 
    
    /**
     * @param elemType element type of the array.
     * @return java op-code to use for loading arrays with elements of the specified type.
     */
    private static int getArrayLoadOpCode(JavaTypeName elemType) {
        
        switch (elemType.getTag()) {
           
            case JavaTypeName.VOID_TAG:
                throw new IllegalArgumentException();                
                
            case JavaTypeName.BOOLEAN_TAG:               
            case JavaTypeName.BYTE_TAG:
                return Opcodes.BALOAD;
                
            case JavaTypeName.SHORT_TAG:
                return Opcodes.SALOAD;
                
            case JavaTypeName.CHAR_TAG:
                return Opcodes.CALOAD;
                
            case JavaTypeName.INT_TAG:
                return Opcodes.IALOAD;
                
            case JavaTypeName.LONG_TAG:
                return Opcodes.LALOAD;
                
            case JavaTypeName.DOUBLE_TAG:
                return Opcodes.DALOAD;
                
            case JavaTypeName.FLOAT_TAG:
                return Opcodes.FALOAD;
                
            case JavaTypeName.ARRAY_TAG:           
            case JavaTypeName.OBJECT_TAG:                                              
                return Opcodes.AALOAD;
                              
            default:
            {
                throw new IllegalArgumentException();
            }
        }            
    } 
    
    /**
     * @param elemType element type of the array.
     * @return java op-code to use for storing into arrays with elements of the specified type.
     */    
    private static int getArrayStoreOpCode(JavaTypeName elemType) {
        
        switch (elemType.getTag()) {
           
            case JavaTypeName.VOID_TAG:
                throw new IllegalArgumentException();                
                
            case JavaTypeName.BOOLEAN_TAG:               
            case JavaTypeName.BYTE_TAG:
                return Opcodes.BASTORE;
                
            case JavaTypeName.SHORT_TAG:
                return Opcodes.SASTORE;
                
            case JavaTypeName.CHAR_TAG:
                return Opcodes.CASTORE;
                
            case JavaTypeName.INT_TAG:
                return Opcodes.IASTORE;
                
            case JavaTypeName.LONG_TAG:
                return Opcodes.LASTORE;
                
            case JavaTypeName.DOUBLE_TAG:
                return Opcodes.DASTORE;
                
            case JavaTypeName.FLOAT_TAG:
                return Opcodes.FASTORE;
                
            case JavaTypeName.ARRAY_TAG:           
            case JavaTypeName.OBJECT_TAG:                                              
                return Opcodes.AASTORE;
                           
            default:
            {
                throw new IllegalArgumentException();
            }
        }            
    }
    
    /**
     * @param elemType element type of the array. Must be a primitive type.
     * @return java op-code to use as the argument for the NEWARRAY op.
     */    
    private static int getNewArrayArgCode(JavaTypeName elemType) {
        
        switch (elemType.getTag()) {
           
            case JavaTypeName.VOID_TAG:
                throw new IllegalArgumentException();                
                
            case JavaTypeName.BOOLEAN_TAG:
                return Opcodes.T_BOOLEAN;
            
            case JavaTypeName.BYTE_TAG:
                return Opcodes.T_BYTE;
                
            case JavaTypeName.SHORT_TAG:
                return Opcodes.T_SHORT;
                
            case JavaTypeName.CHAR_TAG:
                return Opcodes.T_CHAR;
                
            case JavaTypeName.INT_TAG:
                return Opcodes.T_INT;
                
            case JavaTypeName.LONG_TAG:
                return Opcodes.T_LONG;
                
            case JavaTypeName.DOUBLE_TAG:
                return Opcodes.T_DOUBLE;
                
            case JavaTypeName.FLOAT_TAG:
                return Opcodes.T_FLOAT;
                
            case JavaTypeName.ARRAY_TAG:           
            case JavaTypeName.OBJECT_TAG:                                                                      
            default:
            {
                throw new IllegalArgumentException();
            }
        }            
    }
    
    /**
     * @param elemType element type to negate
     * @return java op-code to use for numerical negation.
     */    
    private static int getNegateOpCode(JavaTypeName elemType) {
        
        switch (elemType.getTag()) {
           
            case JavaTypeName.VOID_TAG:              
            case JavaTypeName.BOOLEAN_TAG:               
            case JavaTypeName.BYTE_TAG:               
            case JavaTypeName.SHORT_TAG:              
            case JavaTypeName.CHAR_TAG:
                throw new IllegalArgumentException();
                
            case JavaTypeName.INT_TAG:
                return Opcodes.INEG;
                
            case JavaTypeName.LONG_TAG:
                return Opcodes.LNEG;
                
            case JavaTypeName.DOUBLE_TAG:
                return Opcodes.DNEG;
                
            case JavaTypeName.FLOAT_TAG:
                return Opcodes.FNEG;
                
            case JavaTypeName.ARRAY_TAG:           
            case JavaTypeName.OBJECT_TAG:                                                                      
            default:
            {
                throw new IllegalArgumentException();
            }
        }            
    } 
    
    private static final int getBinaryIntOpCode(String op) {
        switch(op.charAt(0)) {
            case '-' : return Opcodes.ISUB;
            case '+' : return Opcodes.IADD;
            case '%' : return Opcodes.IREM;
            case '*' : return Opcodes.IMUL;
            case '/' : return Opcodes.IDIV;
            case '&' : return Opcodes.IAND;
            case '|' : return Opcodes.IOR;
            case '^' : return Opcodes.IXOR;
            case '<' : return Opcodes.ISHL;
            case '>' : return op.equals(">>>")? Opcodes.IUSHR : Opcodes.ISHR;
            default: throw new IllegalArgumentException("Invalid operand " + op);
        }
    }

    private static final int getBinaryLongOpCode(String op) {
        switch(op.charAt(0)) {
            case '-' : return Opcodes.LSUB;
            case '+' : return Opcodes.LADD;
            case '%' : return Opcodes.LREM;
            case '*' : return Opcodes.LMUL;
            case '/' : return Opcodes.LDIV;
            case '&' : return Opcodes.LAND;
            case '|' : return Opcodes.LOR;
            case '^' : return Opcodes.LXOR;
            case '<' : return Opcodes.LSHL;
            case '>' : return op.equals(">>>")? Opcodes.LUSHR : Opcodes.LSHR;
            default: throw new IllegalArgumentException("Invalid operand " + op);
        }
    }

    private static final int getBinaryFloatOpCode(String op) {          
        switch(op.charAt(0)) {
            case '-' : return Opcodes.FSUB;
            case '+' : return Opcodes.FADD;
            case '*' : return Opcodes.FMUL;
            case '/' : return Opcodes.FDIV;
            case '%' : return Opcodes.FREM;
            default: throw new IllegalArgumentException("Invalid operand " + op);
        }
    }

    private static final int getBinaryDoubleOpCode(String op) {
        switch(op.charAt(0)) {
            case '-' : return Opcodes.DSUB;
            case '+' : return Opcodes.DADD;
            case '*' : return Opcodes.DMUL;
            case '/' : return Opcodes.DDIV;
            case '%' : return Opcodes.DREM;
            default: throw new IllegalArgumentException("Invalid operand " + op);
        }
    }

    /**
     * Gets the constant representing the Java op code for the binary numeric operators
     *
     * @param op operation, such as "+", "*", "<<", etc.
     * @param type result type of the operation
     * @return java op code, as defined in org.objectweb.asm.Opcodes. 
     */
    private static int getArithmeticBinaryOpCode(String op, JavaTypeName type) {
        
        switch (type.getTag()) {
        
            case JavaTypeName.VOID_TAG:
            case JavaTypeName.BOOLEAN_TAG:      
                throw new IllegalArgumentException("Invalid type for getNumericBinaryOpCode" + type);
            
            case JavaTypeName.BYTE_TAG:               
            case JavaTypeName.SHORT_TAG:              
            case JavaTypeName.CHAR_TAG:                             
            case JavaTypeName.INT_TAG:
                return getBinaryIntOpCode(op);
            
            case JavaTypeName.LONG_TAG:
                return getBinaryLongOpCode(op);
            
            case JavaTypeName.DOUBLE_TAG:
                return getBinaryDoubleOpCode(op);
            
            case JavaTypeName.FLOAT_TAG:
                return getBinaryFloatOpCode(op);
            
            case JavaTypeName.ARRAY_TAG:           
            case JavaTypeName.OBJECT_TAG:                                                                      
            default:
            {
                throw new IllegalArgumentException("Invalid type for getNumericBinaryOpCode" + type);
            }
        }                       
    }
    
    /**
     * Determine whether the given type is handled internally (by the JVM) as an int.
     * @param type the type in question.
     * @return whether the type is handled internally as an int.
     */
    private static boolean isInternalIntType(JavaTypeName type) {
        //note that boolean is not an internal int type.        
        switch (type.getTag()) {                  
            case JavaTypeName.BYTE_TAG:               
            case JavaTypeName.SHORT_TAG:              
            case JavaTypeName.CHAR_TAG:                             
            case JavaTypeName.INT_TAG:
                return true;
                                                                                       
            default:
                return false;
        }                                  
    }    
    
    /**
     * Gets the op-code to widen a value of a given type to a value of another type.
     * @param typeToWiden the type to widen.
     * @param valueType the type to which the typeToWiden should be widened.
     * @return int the widening op code, as defined in org.objectweb.asm.Opcodes. Opcodes.NOP us used for the no-op.     
     */
    private static int getWideningOpCode(JavaTypeName typeToWiden, JavaTypeName valueType) {

        if (typeToWiden.equals(valueType)) {
            return Opcodes.NOP;
        }
            
        // Widen from int-type values -> float, long, double
        if (isInternalIntType(typeToWiden)) {
            
            switch (valueType.getTag()) {
                case JavaTypeName.BYTE_TAG:               
                case JavaTypeName.SHORT_TAG:              
                case JavaTypeName.CHAR_TAG:                             
                case JavaTypeName.INT_TAG:
                    return Opcodes.NOP;
            
                case JavaTypeName.LONG_TAG:
                    return Opcodes.I2L;
                
                case JavaTypeName.DOUBLE_TAG:
                    return Opcodes.I2D;
                
                case JavaTypeName.FLOAT_TAG:
                    return Opcodes.I2F;     
               
                default:
                    throw new IllegalArgumentException("Invalid widening conversion.");                
            }
                           
        // Widen from long -> float, double
        } else if (typeToWiden.equals(JavaTypeName.LONG)) {
            
            switch (valueType.getTag()) {
               
                case JavaTypeName.DOUBLE_TAG:
                    return Opcodes.L2D;
                
                case JavaTypeName.FLOAT_TAG:
                    return Opcodes.L2F;     
               
                default:
                    throw new IllegalArgumentException("Invalid widening conversion.");                
            }
            
        // Widen from float -> double
        } else if (typeToWiden.equals(JavaTypeName.FLOAT)) {

            if (valueType.equals(JavaTypeName.DOUBLE)) {
                return Opcodes.F2D;
            }
            
            throw new IllegalArgumentException("Invalid widening conversion.");
        }
            
        //throw new IllegalArgumentException("Invalid widening conversion.");
        return Opcodes.NOP;
    }  
    
    /**     
     * @param type
     * @return number of slots required for a variable of the given type (1 or 2)
     */
    private static int getTypeSize(JavaTypeName type) {
        switch (type.getTag()) {
        
            case JavaTypeName.BOOLEAN_TAG:                       
            case JavaTypeName.BYTE_TAG:               
            case JavaTypeName.SHORT_TAG:              
            case JavaTypeName.CHAR_TAG:                             
            case JavaTypeName.INT_TAG:
            case JavaTypeName.FLOAT_TAG:              
            case JavaTypeName.ARRAY_TAG:           
            case JavaTypeName.OBJECT_TAG:     
                return 1;
            
            case JavaTypeName.LONG_TAG:                 
            case JavaTypeName.DOUBLE_TAG:
                return 2;
            
            case JavaTypeName.VOID_TAG:
            default:
            {
                throw new IllegalArgumentException();
            } 
        }
    }  
    
    /**
     * Determine if a class rep or any inner classes contains any assert statements.
     * @param classRep
     * @return one or a combination of the constants: ASSERTS_UNKNOWN, NO_ASSERTS, ASSERTS_IN_CLASS, ASSERTS_IN_INNER_CLASS 
     *         from JavaClassRep
     */
    private static int containsAsserts (JavaClassRep classRep) {
        final class AssertFinder extends JavaModelTraverser<Void, Void> {
            boolean classContainsAsserts = false;
            boolean innerClassContainsAsserts = false;
            boolean inInnerClass = false;
            
            @Override
            public Void visitAssertStatement(AssertStatement assertStatement,
                    Void arg) {
                
                setContinueTraversal(false);
                
                if (inInnerClass) {
                    innerClassContainsAsserts = true;
                } else {
                    classContainsAsserts = true;
                }
                return null;
            }
            @Override
            public Void visitJavaClassRep(JavaClassRep classRep, Void arg) {

                for (int i = 0, n = classRep.getNFieldDeclarations(); i < n && getContinueTraversal(); ++i) {
                    JavaFieldDeclaration fieldDeclaration = classRep.getFieldDeclaration(i);
                    fieldDeclaration.accept(this, arg);
                }
                
                for (int i = 0, n = classRep.getNConstructors(); i < n && getContinueTraversal(); ++i) {
                    JavaConstructor javaConstructor = classRep.getConstructor(i);
                    javaConstructor.accept(this, arg);
                }
                
                for (int i = 0, n = classRep.getNMethods(); i < n && getContinueTraversal(); ++i) {
                    JavaMethod method = classRep.getMethod(i);
                    method.accept(this, arg);
                }
                
                inInnerClass = true;
                setContinueTraversal(true);
                for (int i = 0, n = classRep.getNInnerClasses(); i < n && getContinueTraversal(); ++i) {
                    JavaClassRep innerClass = classRep.getInnerClass(i);
                    innerClass.accept(this, arg);
                    setContinueTraversal(true);
                }
                return null;
            }
            
        }
        
        AssertFinder assertFinder = new AssertFinder();
        assertFinder.visitJavaClassRep(classRep, null);
        if (assertFinder.classContainsAsserts && assertFinder.innerClassContainsAsserts) {
            return JavaClassRep.ASSERTS_IN_CLASS | JavaClassRep.ASSERTS_IN_INNER_CLASS;
        } else if (assertFinder.classContainsAsserts) {
            return JavaClassRep.ASSERTS_IN_CLASS;
        } else if (assertFinder.innerClassContainsAsserts) {
            return JavaClassRep.ASSERTS_IN_INNER_CLASS;
        } else {
            return JavaClassRep.ASSERTS_NONE;
        }
    }
}
