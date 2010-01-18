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
 * CodeGenerator.java
 * Created: Dec 3, 2002 at 7:24:06 PM
 * By: Raymond Cypher
 */
package org.openquark.cal.internal.machine.g;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

import org.openquark.cal.compiler.CompilerMessage;
import org.openquark.cal.compiler.CompilerMessageLogger;
import org.openquark.cal.compiler.DataConstructor;
import org.openquark.cal.compiler.Expression;
import org.openquark.cal.compiler.FieldName;
import org.openquark.cal.compiler.MessageKind;
import org.openquark.cal.compiler.MessageLogger;
import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.compiler.UnableToResolveForeignEntityException;
import org.openquark.cal.compiler.Expression.Switch.SwitchAlt;
import org.openquark.cal.internal.machine.BasicOpTuple;
import org.openquark.cal.internal.machine.CodeGenerationException;
import org.openquark.cal.internal.machine.CondTuple;
import org.openquark.cal.internal.machine.ConstructorOpTuple;
import org.openquark.cal.internal.machine.g.functions.NAppendRecordPrimitive;
import org.openquark.cal.internal.machine.g.functions.NArbitraryRecordPrimitive;
import org.openquark.cal.internal.machine.g.functions.NBuildList;
import org.openquark.cal.internal.machine.g.functions.NBuildRecord;
import org.openquark.cal.internal.machine.g.functions.NCoArbitraryRecordPrimitive;
import org.openquark.cal.internal.machine.g.functions.NCompareRecord;
import org.openquark.cal.internal.machine.g.functions.NDeepSeq;
import org.openquark.cal.internal.machine.g.functions.NEqualsRecord;
import org.openquark.cal.internal.machine.g.functions.NError;
import org.openquark.cal.internal.machine.g.functions.NInsertOrdinalRecordFieldPrimitive;
import org.openquark.cal.internal.machine.g.functions.NInsertTextualRecordFieldPrimitive;
import org.openquark.cal.internal.machine.g.functions.NNotEqualsRecord;
import org.openquark.cal.internal.machine.g.functions.NOrdinalValue;
import org.openquark.cal.internal.machine.g.functions.NPrimCatch;
import org.openquark.cal.internal.machine.g.functions.NPrimThrow;
import org.openquark.cal.internal.machine.g.functions.NRecordFieldTypePrimitive;
import org.openquark.cal.internal.machine.g.functions.NRecordFieldValuePrimitive;
import org.openquark.cal.internal.machine.g.functions.NRecordFromJListPrimitive;
import org.openquark.cal.internal.machine.g.functions.NRecordFromJMapPrimitive;
import org.openquark.cal.internal.machine.g.functions.NRecordToJListPrimitive;
import org.openquark.cal.internal.machine.g.functions.NRecordToJRecordValuePrimitive;
import org.openquark.cal.internal.machine.g.functions.NRecordTypeDictionary;
import org.openquark.cal.internal.machine.g.functions.NSeq;
import org.openquark.cal.internal.machine.g.functions.NShowRecord;
import org.openquark.cal.internal.machine.g.functions.NStrictRecordPrimitive;
import org.openquark.cal.internal.machine.primitiveops.PrimOps;
import org.openquark.cal.machine.MachineFunction;
import org.openquark.cal.machine.Module;
import org.openquark.cal.machine.StatusListener;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.runtime.ErrorInfo;
import org.openquark.cal.runtime.MachineConfiguration;


/**
 * Convert Expression instances into g-machine instruction sequences.
 *
 * <p>
 * Created: Dec 3, 2002 at 7:24:05 PM
 * @author Raymond Cypher
 */
class CodeGenerator extends org.openquark.cal.machine.CodeGenerator {

    /** The namespace for log messages from the G machine. */
    public static final String MACHINE_LOGGER_NAMESPACE = "org.openquark.cal.internal.runtime.g";

    /** An instance of a Logger for G machine messages. */
    static final Logger MACHINE_LOGGER = Logger.getLogger(MACHINE_LOGGER_NAMESPACE);

    /** A map of QualifiedName -> NPrimitiveFunc for primitive functions. */
    private static final Map<QualifiedName, NPrimitiveFunc> primitiveFuncMap = new HashMap<QualifiedName, NPrimitiveFunc> ();

    /** Flag indicating that function tracing and breakpoints are enabled and */
    private static final boolean GENERATE_DEBUG_CODE = System.getProperty(MachineConfiguration.MACHINE_DEBUG_CAPABLE_PROP) != null;

    static {

        // Initialise the primitive function map.
        primitiveFuncMap.put (NDeepSeq.name, NDeepSeq.instance);
        primitiveFuncMap.put (NOrdinalValue.name, NOrdinalValue.instance);
        primitiveFuncMap.put (NShowRecord.name, NShowRecord.instance);
        primitiveFuncMap.put (NRecordFieldValuePrimitive.name, NRecordFieldValuePrimitive.instance);
        primitiveFuncMap.put (NRecordFieldTypePrimitive.name, NRecordFieldTypePrimitive.instance);
        primitiveFuncMap.put (NInsertTextualRecordFieldPrimitive.name, NInsertTextualRecordFieldPrimitive.instance);
        primitiveFuncMap.put (NInsertOrdinalRecordFieldPrimitive.name, NInsertOrdinalRecordFieldPrimitive.instance);
        primitiveFuncMap.put (NAppendRecordPrimitive.name, NAppendRecordPrimitive.instance);
        primitiveFuncMap.put (NRecordTypeDictionary.name, NRecordTypeDictionary.instance);
        primitiveFuncMap.put (NRecordToJListPrimitive.name, NRecordToJListPrimitive.instance);
        primitiveFuncMap.put (NRecordFromJListPrimitive.name, NRecordFromJListPrimitive.instance);
        primitiveFuncMap.put (NRecordFromJMapPrimitive.name, NRecordFromJMapPrimitive.instance);
        primitiveFuncMap.put (NRecordToJRecordValuePrimitive.name, NRecordToJRecordValuePrimitive.instance);
        primitiveFuncMap.put (NStrictRecordPrimitive.name, NStrictRecordPrimitive.instance);
        primitiveFuncMap.put (NCompareRecord.name, NCompareRecord.instance);
        primitiveFuncMap.put (NNotEqualsRecord.name, NNotEqualsRecord.instance);
        primitiveFuncMap.put (NEqualsRecord.name, NEqualsRecord.instance);
        primitiveFuncMap.put (NArbitraryRecordPrimitive.name, NArbitraryRecordPrimitive.instance);
        primitiveFuncMap.put (NCoArbitraryRecordPrimitive.name, NCoArbitraryRecordPrimitive.instance);
        primitiveFuncMap.put (NError.name, NError.instance);
        primitiveFuncMap.put (NSeq.name, NSeq.instance);
        primitiveFuncMap.put (NPrimCatch.name, NPrimCatch.instance);
        primitiveFuncMap.put (NPrimThrow.name, NPrimThrow.instance);

        primitiveFuncMap.put (NBuildList.name, NBuildList.instance);
        primitiveFuncMap.put (NBuildRecord.name, NBuildRecord.instance);

        
        MACHINE_LOGGER.setLevel(Level.FINEST);
        MACHINE_LOGGER.setLevel(Level.FINEST);
        MACHINE_LOGGER.setUseParentHandlers(false);

        StreamHandler consoleHandler = new StreamHandler(System.out, new ConsoleFormatter()) {

            /** Override this to always flush the stream. */
            @Override
            public void publish(LogRecord record) {
                super.publish(record);
                flush();
            }

            /** Override to just flush the stream, we don't want to close System.out. */
            @Override
            public void close() {
                flush();
            }
        };

        consoleHandler.setLevel(Level.ALL);
        MACHINE_LOGGER.addHandler(consoleHandler);
    }

    protected GMachineFunction currentMachineFunction;

    //protected CompilerMessageLogger logger;

    /** Show code generation diagnostics. */
    public static boolean CODEGEN_DIAG = false;

    private KeyholeOptimizer ko = new KeyholeOptimizer ();

    private Module currentModule;


    /**
     * Construct CodeGenerator from compiler.
     * @param isForAdjunct
     */
    CodeGenerator(boolean isForAdjunct) {
        super (isForAdjunct);
    }

    /**
     * Generate g-machine code for all the supercombinators in the program.
     * @param module
     * @param logger
     * @return CompilerMessage.Severity
     */
    @Override
    public CompilerMessage.Severity generateSCCode (Module module, CompilerMessageLogger logger) {

        if (module == null) {
            throw new IllegalArgumentException("g.CodeGenerator.generateSCCode() cannot have a null module.");
        }

        CompilerMessageLogger generateLogger = new MessageLogger();
        
        try {

            informStatusListeners(StatusListener.SM_GENCODE, module.getName());
            currentModule = module;

            for (final MachineFunction mf : module.getFunctions()) {
                
                GMachineFunction gmf = (GMachineFunction)mf;
                
                if (gmf.isCodeGenerated()) {
                    continue;
                }
                if (gmf.getAliasOf() != null || gmf.getLiteralValue() != null) {
                    gmf.setCodeGenerated(true);
                    continue;
                }

                try {
                    generateSCCode (gmf);
                } catch (CodeGenerationException e) {
                    try {
                        // Note: The code generation could potentially have failed because a foreign type or a foreign function's corresponding Java entity
                        // could not be resolved. (In this case the CodeGenerationException would be wrapping an UnableToResolveForeignEntityException)
                        
                        final Throwable cause = e.getCause();
                        if (cause instanceof UnableToResolveForeignEntityException) {
                            generateLogger.logMessage(((UnableToResolveForeignEntityException)cause).getCompilerMessage());
                        }
                        
                        // Code generation aborted. Error generating code for: {cl.getQualifiedName()}
                        generateLogger.logMessage(new CompilerMessage(new MessageKind.Error.CodeGenerationAborted(gmf.getQualifiedName().getQualifiedName()), e));
                    } catch (CompilerMessage.AbortCompilation e2) {/* Ignore exceptions generated by the act of logging. */}
                    return generateLogger.getMaxSeverity();
                }
            }


            fixupPushGlobals (module);

        } catch (Exception e) {
            try {
                if (generateLogger.getNErrors() > 0) {
                    //if an error occurred previously, we continue to compile the program to try to report additional
                    //meaningful compilation errors. However, this can produce spurious exceptions related to the fact
                    //that the program state does not satisfy preconditions because of the initial error(s). We don't
                    //report the spurious exception as an internal coding error.
                    generateLogger.logMessage(new CompilerMessage(new MessageKind.Fatal.UnableToRecoverFromCodeGenErrors(module.getName())));
                } else {                               
                    generateLogger.logMessage(new CompilerMessage(new MessageKind.Fatal.CodeGenerationAbortedDueToInternalCodingError(module.getName()), e));
                }                                                                            
            } catch (CompilerMessage.AbortCompilation ace) {
                /* Ignore exceptions generated by the act of logging. */
            }
        } catch (Error e) {
            try {
                generateLogger.logMessage(new CompilerMessage(new MessageKind.Error.CodeGenerationAbortedWithException(module.getName(), e)));
            } catch (CompilerMessage.AbortCompilation ace) {
                /* Ignore exceptions generated by the act of logging. */
            }
        } finally {
            if (logger != null) {
                // Log messages to the passed-in logger.
                try {
                    logger.logMessages(generateLogger);
                } catch (CompilerMessage.AbortCompilation e) {
                    /* Ignore exceptions generated by the act of logging. */
                }
            }
        }

        return generateLogger.getMaxSeverity(); 
    }

    /**
     * Generate supercombinator code
     * @param gmf the code label for which the supercombinator code should be generated.
     * @throws CodeGenerationException
     */
    private void generateSCCode(GMachineFunction gmf) throws CodeGenerationException {
        // We are generating a supercombinator.

        // Show diagnostics if turned on
        if (CODEGEN_DIAG) {
            // DIAG
            MACHINE_LOGGER.log(Level.FINE, "CodeGen: SC = " + gmf.getName ());
        }

        // Save the supercombinator away in the object to save stack if we recurse
        this.currentMachineFunction = gmf;
        InstructionList gp = null;

        Expression e = gmf.getExpressionForm();

        // If this is a DataConstructor for an enumeration data type
        // we want to simply return as these are treates as int. 
        Expression.PackCons packCons = e.asPackCons();
        if (packCons != null) {
            DataConstructor dc = packCons.getDataConstructor();
            if (TypeExpr.isEnumType(dc.getTypeConstructor())) {
                gmf.setCodeGenerated(true);
                return;
            }
        }

        // Call the top level scheme
        // Recursive code generation
        try {
            gp = schemeSC(e);
        } catch (StackOverflowError excp) {
            // Blown the Java call stack - raise compiler error
            throw new CodeGenerationException ("Code generation stack recursion too deeply nested, use an iterative code generator", excp);
        }


        gp = ko.optimizeCode (gp);

        // Put instuctions into the MachineFunction
        Code code = new Code (gp);
        gmf.setCode(code);
        gmf.setCodeGenerated(true);

        if (CODEGEN_DIAG) {
            MACHINE_LOGGER.log(Level.FINE, "\n");
            MACHINE_LOGGER.log(Level.FINE, gmf.toString ());
            MACHINE_LOGGER.log(Level.FINE, gp.toString ());
            MACHINE_LOGGER.log(Level.FINE, "\n");
        }
    }

    /**
     * Execute the supercombinator compilation scheme.
     * Creation date: (12/03/02 9:26:31 PM)
     * @param e Expression the expression
     * @return InstructionList the instructions and other data compiled by this scheme
     * @throws CodeGenerationException
     */
    private InstructionList schemeSC(Expression e) throws CodeGenerationException {

        InstructionList body = new InstructionList ();

        // Show diagnostics if turned on
        if (CODEGEN_DIAG) {
            // DIAG
            MACHINE_LOGGER.log(Level.FINE, "\nCodeGen: Entering SC compilation scheme with intermediate code:\n" + e);
        }

        // Is e a pack constructor?
        Expression.PackCons packCons = e.asPackCons();
        if (packCons != null) {

            // Generate the pack constructor code and return.
            DataConstructor dc = packCons.getDataConstructor();
            // If this is a supercombinator and we are instrumenting the code.
            if (System.getProperty("org.openquark.cal.machine.g.call_counts") != null) {
                body.code (new Instruction.I_Instrument (new Executor.CallCountInfo(currentMachineFunction.getQualifiedName (), "DataConstructor function form counts")));
            }

            // Force the evaluation of any strict arguments.
            if (dc.hasStrictArgs()) {
                for (int i = 0; i < dc.getArity(); ++i) {
                    if (dc.isArgStrict(i)) {
                        body.code(new Instruction.I_Push(i));
                        body.code(Instruction.I_Eval);
                        body.code(new Instruction.I_Pop(1));
                    }
                }
            }

            if (GENERATE_DEBUG_CODE) {
                // Add an instruction that will suspend execution if a breakpoint is set
                // trace a function message, etc.
                Instruction inst = 
                    new Instruction.I_Debug_Processing(currentMachineFunction.getQualifiedName(),
                            currentMachineFunction.getArity());
                body.code(inst);
            }


            Instruction instruction = Instruction.I_PackCons.makePackCons(dc);
            body.code (instruction);

            body.code (new Instruction.I_Update (0));
            body.code (Instruction.I_Unwind);
        } else {

            // If this is a supercombinator and we are instrumenting the code.
            if (System.getProperty("org.openquark.cal.machine.g.call_counts") != null) {
                body.code (new Instruction.I_Instrument (new Executor.CallCountInfo(currentMachineFunction.getQualifiedName (), "Call counts")));
            }

            int arity = currentMachineFunction.getArity();
            Map<QualifiedName, Integer> env = new HashMap<QualifiedName, Integer> ();
            String parameterNames[] = currentMachineFunction.getParameterNames();
            for (int i = 0; i < parameterNames.length; ++i) {
                String parameterName = parameterNames[i];                                     
                QualifiedName qn = QualifiedName.make(currentModule.getName(), parameterName);               
                env.put (qn, Integer.valueOf((arity - i)));              
            }

            //body.code (new Instruction.I_Println("Entering: " + cl.getQualifiedName().toString()));

            for (int i = 0; i < currentMachineFunction.getArity(); ++i) {
                if (currentMachineFunction.getParameterStrictness()[i]) {
                    body.code(new Instruction.I_Push(i));
                    body.code(Instruction.I_Eval);
                    body.code(new Instruction.I_Pop(1));
                }
            }

            if (GENERATE_DEBUG_CODE) {
                // Add in an instruction that will generate a trace message with the name of the current function 
                // and the state of its arguments.
                body.code (new Instruction.I_Debug_Processing(currentMachineFunction.getQualifiedName(), currentMachineFunction.getArity()));
            }

            // Invoke the R scheme to compile the body
            body.code (schemeR(e, env, arity));
        }

        return body;
    }

    /**
     * Execute the R compilation scheme.  This generates code to apply a supercombinator to 
     * its arguments.
     * Creation date: (12/04/02 9:32:17 AM)
     * @param e Expression the expression
     * @param p Map: a table linking variable names to stack offsets.
     * @param d int: the depth of the current execution context minus one (i.e. at this point the number of arguments).
     * @return InstructionList the instructions and other data compiled by this scheme
     * @throws CodeGenerationException
     */   
    private InstructionList schemeR(Expression e, Map<QualifiedName, Integer> p, int d) throws CodeGenerationException {

        // R[[ i ]] p d = PUSHVVAL i; UPDATE d; POP d; UNWIND;
        // R[[ f ]] p d = PUSHGLOBAL f; EVAL; UPDATE d; POP d; UNWIND;
        // R[[ x ]] p d = PUSH (d - p(x)); EVAL; UPDATE d; POP d; UNWIND;
        // R[[ Cons E1 E2]] p d = C[[ E2 ]] p d; C[[ E1 ]] p (d+1); CONS; UPDATE d; POP d; UNWIND;
        // R[[ if Ec Et Ef]] p d = C[[ Ec ]] p d; EVAL; I_COND (R[[ Et ]] p d) (R[[ Ef ]] p d);
        // R[[ E1 E2]] p d = C[[ E1 E2]] p d; UPDATE d; POP d; UNWIND;
        // R[[letrec D in E]] p d = CLetrec[[ D ]] p1 d1; R[[ E ]] p1 d1;
        //      where
        //      (p1, d1) = Xr[[ D ]] p d;


        // Show diagnostics if turned on
        if (CODEGEN_DIAG) {
            // DIAG
            MACHINE_LOGGER.log(Level.FINE, "\nCodeGen: Entering R compilation scheme with intermediate code:\n" + e);   
            
            for (final Map.Entry<QualifiedName, Integer> entry : p.entrySet()) {               
                MACHINE_LOGGER.log(Level.FINE, "    " + entry.getKey() + ": " + entry.getValue());
            }
        }

        InstructionList gp = new InstructionList ();

        // Is e a literal?
        Expression.Literal literal = e.asLiteral();
        if (literal != null) {
            // Code a I_PushVVal instruction to push the literal onto the stack.

            if (CODEGEN_DIAG) {
                MACHINE_LOGGER.log(Level.FINE, "    Literal:");
            }

            Object val = literal.getLiteral ();
            if (val instanceof Boolean) {
                // Booleans are handled as a special case.
                if (((Boolean)val).booleanValue()) {
                    gp.code (Instruction.I_PushTrue);
                } else {
                    gp.code (Instruction.I_PushFalse);
                }
            } else {
                gp.code(Instruction.I_PushVVal.makePushVVal(literal.getLiteral()));
            }

            appendUpdateCode(gp, d);

            return gp;
        }

        BasicOpTuple  basicOpExpressions = BasicOpTuple.isBasicOp(e);
        if (GENERATE_DEBUG_CODE) {
            //When we have function tracing enabled, we want to force all primitive operations to be
            //done as function calls. This will have the effect of ensuring that they get traced when called.  
            if (basicOpExpressions != null && !basicOpExpressions.getName().equals(currentMachineFunction.getQualifiedName())) {
                basicOpExpressions = null;
            }
        }

        // Is e a basic operation (arithmetic, comparative etc.)?  Test only - don't get tuple
        if (basicOpExpressions != null) {
            // Unpack the basic op into subexpressions
            if (CODEGEN_DIAG) {
                MACHINE_LOGGER.log(Level.FINE, "    basic:");
            }



            // Code a basic operation
            int op = basicOpExpressions.getPrimitiveOp ();

            Instruction instruction = null;
            if (op == PrimOps.PRIMOP_EAGER) {
                return schemeR (basicOpExpressions.getArgument(0), p, d);
            } else
                if (op == PrimOps.PRIMOP_FOREIGN_FUNCTION) {
                    instruction = new Instruction.I_ForeignFunctionCall (basicOpExpressions.getForeignFunctionInfo());
                } else {
                    instruction = new Instruction.I_PrimOp(op);
                }

            int nArgs = basicOpExpressions.getNArguments ();

            if (nArgs < 0) {
                throw new CodeGenerationException ("Internal Coding Error: Invalid basic operator arity");   
            }

            if (op == PrimOps.PRIMOP_CAL_VALUE_TO_OBJECT) {
                //Prelude.calValueToObject is non-strict in its first argument.        
                gp.code (schemeC (basicOpExpressions.getArgument(0), p, d));
                gp.code (instruction);
            } else 
            {
                for (int i = 0; i < nArgs; ++i) {
                    gp.code (schemeE (basicOpExpressions.getArgument(i), p, d + i));
                }
                gp.code (instruction);
            }

            appendUpdateCode(gp, d);

            return gp;
        }

        // Is e an application of a saturated constructor?
        if (ConstructorOpTuple.isConstructorOp(e, true) != null) {
            // Unpack the basic op into subexpressions
            if (CODEGEN_DIAG) {
                MACHINE_LOGGER.log(Level.FINE, "    basic:");
            }

            ConstructorOpTuple  constructorOpExpressions = ConstructorOpTuple.isConstructorOp(e, false);

            DataConstructor dc = constructorOpExpressions.getDataConstructor ();

            Instruction instruction = Instruction.I_PackCons.makePackCons(dc);

            int nArgs = constructorOpExpressions.getNArguments ();

            if (nArgs < 0) {
                throw new CodeGenerationException ("Internal Coding Error: Invalid constructor operator arity");   
            }

            for (int i = 0; i < nArgs; ++i) {
                gp.code (schemeC (constructorOpExpressions.getArgument(nArgs - i - 1), p, d + i));
            }

            // Force the evaluation of any strict arguments.
            if (dc.hasStrictArgs()) {
                for (int i = 0; i < dc.getArity(); ++i) {
                    if (dc.isArgStrict(i)) {
                        gp.code(new Instruction.I_Push(i));
                        gp.code(Instruction.I_Eval);
                        gp.code(new Instruction.I_Pop(1));
                    }
                }
            }

            gp.code (instruction);
            appendUpdateCode (gp, d);

            return gp;
        }


        // Is e a variable?
        Expression.Var var = e.asVar();
        if (var != null) {
            if (CODEGEN_DIAG) {
                MACHINE_LOGGER.log(Level.FINE, "    Var:");
            }

            // e is a variable, possible addressing modes are:
            // Push <k> for an argument
            // PushGlobal <l> for a label (e.g. supercombinator)

            // Code an Push <k> if we find it's an argument
            gp.code (schemeC (e, p, d));
            appendUpdateCode(gp, d);

            Integer ei = p.get(var.getName());
            if (CODEGEN_DIAG) {
                if (ei == null) {
                    MACHINE_LOGGER.log(Level.FINE, "        Global:");
                } else {
                    MACHINE_LOGGER.log(Level.FINE, "        local:");
                }
            }
            return gp;
        }

        // Is e a conditional op (if <cond expr> <then expr> <else expr>)?
        CondTuple conditionExpressions = CondTuple.isCondOp(e);
        if (conditionExpressions != null) {
            if (CODEGEN_DIAG) {
                MACHINE_LOGGER.log(Level.FINE, "    condition:");
            }

            // This is a conditional op.  The conditionExpressions tuple holds (kCond, kThen, kElse) expressions
            // Generate the code for kThen and kElse, as arguments to a new I_Cond instruction

            gp.code (schemeE (conditionExpressions.getConditionExpression(), p, d));

            InstructionList thenPart = schemeR (conditionExpressions.getThenExpression(), p, d);
            InstructionList elsePart = schemeR (conditionExpressions.getElseExpression(), p, d);

            Instruction i = new Instruction.I_Cond (new Code (thenPart), new Code(elsePart));

            gp.code (i);
            return gp;          
        }

        // Is e a switch?
        Expression.Switch sw = e.asSwitch();
        if (sw != null) {
            if (CODEGEN_DIAG) {
                MACHINE_LOGGER.log(Level.FINE, "    switch:");
            }

            gp.code (schemeE (sw.getSwitchExpr (), p, d));

            // Get the alternatives
            Expression.Switch.SwitchAlt[] alts = sw.getAlts();

            Map<Object, Code> altTagToCodeMap = new HashMap<Object, Code>();

            ModuleName moduleName = currentMachineFunction.getQualifiedName().getModuleName();
            // Build the code for each branch, save the variable requirement of each
            // branch as an alternative in gp, for later resolution
            for (final SwitchAlt alt : alts) {

                // For now, generate code for each tag.
                for (final Object altTag : alt.getAltTags()) {                   

                    String[] vars = getVars(alt, altTag);
                    Map<QualifiedName, Integer> newEnv = argOffset (0, p);

                    for (int j = 0; j < vars.length; ++j) {
                        QualifiedName qn = QualifiedName.make(moduleName, vars [j]);
                        newEnv.put (qn, Integer.valueOf(d + 1 + j));
                    }

                    // i_split: takes a dc object, tells it to push all (vars.length) fields onto the stack
                    InstructionList altGP = new InstructionList ();
                    altGP.code (new Instruction.I_Split (vars.length));

                    altGP.code (schemeR(alt.getAltExpr(), newEnv, d + vars.length));

                    Code code = new Code(altGP);
                    altTagToCodeMap.put(altTag, code);
                }
            }

            ErrorInfo errorInfo = sw.getErrorInfo() == null ? null : toRuntimeErrorInfo(sw.getErrorInfo());
            gp.code (new Instruction.I_Switch (altTagToCodeMap, errorInfo));

            return gp;
        }

        // Is e a data constructor field selection?
        Expression.DataConsSelection dataConsSelection = e.asDataConsSelection();
        if (dataConsSelection != null) {
            if (CODEGEN_DIAG) {
                MACHINE_LOGGER.log(Level.FINE, "    selectDC:");
            }

            gp.code (schemeC(dataConsSelection.getDCValueExpr(), p, d));
            gp.code (new Instruction.I_LazySelectDCField(dataConsSelection.getDataConstructor(),
                    dataConsSelection.getFieldIndex(),
                    toRuntimeErrorInfo (dataConsSelection.getErrorInfo()))); 

//          // Evaluate the code for the dc-valued expr.
//gp.code (schemeE (dataConsSelection.getDCValueExpr(), p, d));

//          // Extract the field value onto the stack.
//          int fieldIndex = dataConsSelection.getFieldIndex();
//          ErrorInfo errorInfo = dataConsSelection.getErrorInfo() == null ? null : new ErrorInfo(dataConsSelection.getErrorInfo());
//          gp.code (new Instruction.I_SelectDCField (dataConsSelection.getDataConstructor(), fieldIndex, errorInfo));

//          // Add a var to the env, and generate code for that var.
//          Expression.Var varName = dataConsSelection.getVarName();
//          QualifiedName varQualifiedName = varName.getName();

//          Map newEnv = argOffset (0, p);
//          newEnv.put (varQualifiedName, JavaPrimitives.makeInteger (d + 1));

//          gp.code (schemeR(varName, newEnv, d + 1));

            appendUpdateCode (gp, d);

            return gp;
        }

        // Is e a let expression?
        Expression.Let let = e.asLet();
        if (let != null) {
            // Currently the compiler doesn't differentialte between let and letrec scenarios.
            // As such we have to treat all lets as letrecs.

            Expression.Let.LetDefn[] defs = let.getDefns();

            EnvAndDepth ead = schemeXr (defs, p, d);            

            InstructionList gprecs = schemeCLetrec (defs, ead.env, ead.depth);

            gp.code (gprecs);

            gp.code (schemeR (let.getBody (), ead.env, ead.depth));

            return gp;  
        }

        // Is e a tail recursive call?
        if (e.asTailRecursiveCall() != null) {
            // The g-machine doesn't have a specific optimization for tail recursive calls
            // so we simply handle it as the original fully saturated application and let
            // the general tail call optimization handle it.
            return schemeR (e.asTailRecursiveCall().getApplForm(), p, d);
        }

        // Is e an application?
        Expression.Appl appl = e.asAppl();
        if (appl != null) {
            if (CODEGEN_DIAG) {
                MACHINE_LOGGER.log(Level.FINE, "    Application:");
            }

            InstructionList il = schemeRS (e, p, d, 0);
            if (il != null) { 
                gp.code (il);
                return gp;
            }

            gp.code (schemeC (e, p, d));
            appendUpdateCode(gp, d);

            return gp;
        } 

        // Is e a record update
        // e is a record update
        Expression.RecordUpdate recordUpdate = e.asRecordUpdate();
        if (recordUpdate != null) {
            if (CODEGEN_DIAG) {
                MACHINE_LOGGER.log(Level.FINE, "    Record update:");
            }
            gp.code (schemeE (e, p, d));
            appendUpdateCode (gp, d);
            return gp; 
        }          

        // Is e a record extension
        // e is a record extension
        Expression.RecordExtension recordExtension = e.asRecordExtension();
        if (recordExtension != null) {
            if (CODEGEN_DIAG) {
                MACHINE_LOGGER.log(Level.FINE, "    Record extension:");
            }
            gp.code (schemeE (e, p, d));
            appendUpdateCode (gp, d);
            return gp; 
        }  

        // e is a record selection
        Expression.RecordSelection recordSelection = e.asRecordSelection();
        if (recordSelection != null) {
            if (CODEGEN_DIAG) {
                MACHINE_LOGGER.log(Level.FINE, "    Record selection:");
            }
            gp.code (schemeE (e, p, d));
            appendUpdateCode (gp, d); 
            return gp; 
        }  

        // e is a record case
        Expression.RecordCase recordCase = e.asRecordCase();
        if (recordCase != null) {
            // Strictly compile the condition expression
            Expression conditionExpr = recordCase.getConditionExpr();
            gp.code (schemeE (conditionExpr, p, d));

            Map<QualifiedName, Integer> newEnv = argOffset (0, p);
            QualifiedName recordName = QualifiedName.make(currentMachineFunction.getQualifiedName().getModuleName(), "$recordCase");
            newEnv.put (recordName, Integer.valueOf(++d));

            //FieldName -> String
            SortedMap<FieldName, String> fieldBindingVarMap = recordCase.getFieldBindingVarMap();

            int recordPos = 0;

            // This creates, if necessary, a record equivalent to the original record minus the bound fields.          
            String baseRecordPatternVarName = recordCase.getBaseRecordPatternVarName();        
            if (baseRecordPatternVarName != null &&
                    !baseRecordPatternVarName.equals(Expression.RecordCase.WILDCARD_VAR)) {
                recordPos++;

                // Create a new record that is the original record minus the bound fields.
                QualifiedName qn = QualifiedName.make(currentMachineFunction.getQualifiedName().getModuleName(), baseRecordPatternVarName); 
                newEnv.put (qn, Integer.valueOf(++d));

                // push the original record
                gp.code (new Instruction.I_Push(0));

                // consume the record on top of the stack and replace with an extended version 
                gp.code (new Instruction.I_ExtendRecord());

                // Now remove fields from the record as appropriate.
                for (final FieldName fieldName : fieldBindingVarMap.keySet()) {
                   
                    gp.code (new Instruction.I_RemoveRecordField(fieldName.getCalSourceForm()));
                }

            }

            // Now push the values for the bound fields onto the stack. 
            for (final Map.Entry<FieldName, String> entry : fieldBindingVarMap.entrySet()) {

                FieldName fieldName = entry.getKey();
                String bindingVarName = entry.getValue();

                //ignore anonymous pattern variables. These are guaranteed not to be used
                //by the result expression, and so don't need to be extracted from the condition record.
                if (!bindingVarName.equals(Expression.RecordCase.WILDCARD_VAR)) {

                    QualifiedName qn = QualifiedName.make(currentMachineFunction.getQualifiedName().getModuleName(), bindingVarName);
                    newEnv.put(qn, Integer.valueOf(++d));

                    gp.code (new Instruction.I_Push (recordPos));
                    gp.code (new Instruction.I_RecordSelection (fieldName.getCalSourceForm()));
                    recordPos++;
                }
            }

            //encode the result expression in the context of the extended variable scope.
            Expression resultExpr = recordCase.getResultExpr();
            gp.code (schemeR (resultExpr, newEnv, d));
            appendUpdateCode(gp, d);
            return gp;
        }

        Expression.Cast cast = e.asCast();
        if (cast != null) {
            gp.code (schemeE(cast.getVarToCast(), p, d));
            gp.code (new Instruction.I_Cast(getCastType(cast)));
            appendUpdateCode(gp, d);
            return gp;
        }


        MACHINE_LOGGER.log(Level.FINE,
                "\nCodeGen: Bad exit of R compilation scheme with intermediate code:\n"
                + e);
        logEnvironment(p);        

        throw new CodeGenerationException ("Internal Coding Error: unrecognized expression " + e +".");
    }

    /** Execute the RS compilation scheme.  Completes the evaluation of an expression,
     * the top n ribs of which have already been put on the stack.
     * RS constructs instances of the ribs of E, putting them on the stack and
     * then unwinds in the same fashion as the R scheme.
     * @param e
     * @param p
     * @param d
     * @param n
     * @return null if the given Expression is not handled by the ES scheme, otherwise the IntructionList of generated instructions.
     * @throws CodeGenerationException
     */
    private InstructionList schemeRS (Expression e, Map<QualifiedName, Integer> p, int d, int n) throws CodeGenerationException {
        // RS[[ f ]] p d n = PUSHGLOBAL f; MKAP n; UPDATE (d-n); POP (d-n); UNWIND;
        // RS[[ x ]] p d n = PUSH (d - px); MKAP n; UPDATE (d-n); POP (d-n); UNWIND;
        // RS[[ HEAD E]] p d n = E[[ E ]] p d; HEAD; MKAP n; UPDATE (d-n); POP (d-n); UNWIND;
        // RS[[ If Ec Et Ef]] p d n = E[[ Ec]] p d; I_Cond (RS[[ Et ]] p d n, RS[[ Ef ]] p d n);
        // RS[[ E1 E2 ]] = C[[ E2 ]] p d; RS [[ E1 ]] p (d+1) (n+1);

        InstructionList gp = new InstructionList ();

        // Is e a variable?
        Expression.Var var = e.asVar();
        if (var != null) {
            if (CODEGEN_DIAG) {
                MACHINE_LOGGER.log(Level.FINE, "    Var:");
            }

            // e is a variable, possible addressing modes are:
            // Push <k> for an argument
            // PushGlobal <l> for a label (e.g. supercombinator)

            // Code an Push <k> if we find it's an argument
            Integer ei = p.get(var.getName());
            if (ei == null) {

                gp.code(new Instruction.I_PushGlobal(var.getName())); 

                //appendRSUpdate (gp, d, n);
                gp.code (new Instruction.I_Squeeze (n+1, d-n));
                gp.code (new Instruction.I_Dispatch (n));

                if (CODEGEN_DIAG) {
                    MACHINE_LOGGER.log(Level.FINE, "        Global:");
                }
            } else {
                gp.code (new Instruction.I_Push (d - ei.intValue()));

                //appendRSUpdate (gp, d, n);
                gp.code (new Instruction.I_Squeeze (n+1, d-n));
                gp.code (new Instruction.I_Dispatch (n));

                if (CODEGEN_DIAG) {
                    MACHINE_LOGGER.log(Level.FINE, "        local:");
                }
            }
            return gp;
        }

        // Is e a conditional op (if <cond expr> <then expr> <else expr>)?
        CondTuple conditionExpressions = CondTuple.isCondOp(e);
        if (conditionExpressions != null) {
            if (CODEGEN_DIAG) {
                MACHINE_LOGGER.log(Level.FINE, "    condition:");
            }

            // This is a conditional op.  The conditionExpressions tuple holds (kCond, kThen, kElse) expressions
            // Generate the code for kThen and kElse, as arguments to a new I_Cond instruction

            gp.code (schemeE (conditionExpressions.getConditionExpression(), p, d));

            InstructionList thenPart = schemeRS (conditionExpressions.getThenExpression(), p, d, n);
            InstructionList elsePart = schemeRS (conditionExpressions.getElseExpression(), p, d, n);
            if (thenPart == null || elsePart == null) {
                // One of the sub expressions could not be handled by schemeRS.  Return null and 
                // let the calling code either abort or use an alternate compilation scheme.
                return null;
            }

            Instruction i = new Instruction.I_Cond (new Code (thenPart), new Code(elsePart));

            gp.code (i);
            return gp;          
        }

        // Is e a switch?
        Expression.Switch sw = e.asSwitch();
        if (sw != null) {
            if (CODEGEN_DIAG) {
                MACHINE_LOGGER.log(Level.FINE, "    switch:");
            }

            gp.code (schemeE (sw.getSwitchExpr (), p, d));

            // Get the alternatives
            Expression.Switch.SwitchAlt[] alts = sw.getAlts();

            Map<Object, Code> altTagToCodeMap = new HashMap<Object, Code>();

            ModuleName moduleName = currentMachineFunction.getQualifiedName().getModuleName();
            // Build the code for each branch, save the variable requirement of each
            // branch as an alternative in gp, for later resolution
            for (final SwitchAlt alt : alts) {

                // For now, generate code for each tag.
                for (final Object altTag : alt.getAltTags()) {
                   
                    String[] vars = getVars(alt, altTag);
                    Map<QualifiedName, Integer> newEnv = argOffset (0, p);

                    for (int j = 0; j < vars.length; ++j) {
                        QualifiedName qn = QualifiedName.make(moduleName, vars [j]);
                        newEnv.put (qn, Integer.valueOf(d + 1 + j));
                    }

                    InstructionList altGP = new InstructionList ();
                    altGP.code (new Instruction.I_Split (vars.length));

                    InstructionList il = schemeRS(alt.getAltExpr(), newEnv, d + vars.length, n);
                    if (il == null) {
                        // The alternate could not be compiled by SchemeRS.  Return null and let the calling code
                        // decide whether to abort or use an alternate compilation scheme.
                        return null; 
                    }
                    altGP.code (il);

                    Code code = new Code(altGP);
                    altTagToCodeMap.put(altTag, code);
                }
            }

            ErrorInfo errorInfo = sw.getErrorInfo() == null ? null : toRuntimeErrorInfo(sw.getErrorInfo());
            gp.code (new Instruction.I_Switch (altTagToCodeMap, errorInfo));

            return gp;
        }


        // Is e an application?
        Expression.Appl appl = e.asAppl();
        if (appl != null) {
            if (CODEGEN_DIAG) {
                MACHINE_LOGGER.log(Level.FINE, "    Application:");
            }

            // e is an application
            // Get e1 (LHS) and e2 (RHS) expressions
            Expression e1 = appl.getE1();
            Expression e2 = appl.getE2();

            InstructionList gpe2 = schemeC (e2, p, d);
            InstructionList gpe1 = schemeRS (e1, p, d + 1, n + 1);
            if (gpe1 == null) {
                // The RHS could not be handled by schemeRC.  Return null and let the calling code
                // decide whether to abort or use an alternate compilation scheme.
                return null;
            }

            gp.code (gpe2);
            gp.code (gpe1);

            return gp;
        }   

        // The given Expression cannot be handled by the RS scheme.  Return null and let the
        // calling code decide whether to abort or use an alternate scheme.
        return null;
    }



    /**
     * Execute the E compilation scheme.  This generates code to evaluate the
     * given expression and leave the result on top of the stack.
     * Creation date: (12/04/02 9:32:17 AM)
     * @param e Expression the expression
     * @param p Map: a table linking variable names to stack offsets.
     * @param d int: the depth of the current execution context minus one (i.e. at this point the number of arguments).
     * @return InstructionList the instructions and other data compiled by this scheme
     * @throws CodeGenerationException
     * @throws CodeGenerationException
     */
    private InstructionList schemeE (Expression e, Map<QualifiedName, Integer> p, int d) throws CodeGenerationException {
        // E[[ i ]] p d = PUSHVVAL e;
        // E[[ f ]] p d = PUSHGLOBAL f; EVAL;
        // E[[ x ]] p d = PUSH (d - p(x)); EVAL;
        // E[[ Cons E1 E2 ]] p d = C[[ E2 ]] p d; C[[ E1 ]] p (d+1); CONS;
        // E[[ if Ec Et Ef]] p d = E[[ Ec ]] p d; I_COND (E[[ Et ]] p d) (E[[ Ef ]] p d);
        // E[[ letrec D in E ]] p d = CLetrec[[ D ]] p' d'; E[[ E ]] p'd'; SLIDE (d'-d);
        //      where
        //      (p', d') = Xr[[ D ]] p d;
        // E[[ E1 E2 ]] p d = C[[ E1 E2]] p d; EVAL;

        // Show diagnostics if turned on
        if (CODEGEN_DIAG) {
            // DIAG
            MACHINE_LOGGER.log(Level.FINE, "\nCodeGen: Entering E compilation scheme with intermediate code:\n" + e);
            logEnvironment(p);            
        }

        InstructionList gp = new InstructionList ();

        // Is e a literal?
        Expression.Literal literal = e.asLiteral();
        if (literal != null) {
            // Code a I_PushVVal instruction to push the literal onto the stack.

            if (CODEGEN_DIAG) {
                MACHINE_LOGGER.log(Level.FINE, "    Literal:");
            }

            Object val = literal.getLiteral ();
            if (val instanceof Boolean) {
                // Booleans are handled as a special case.
                if (((Boolean)val).booleanValue()) {
                    gp.code (Instruction.I_PushTrue);
                } else {
                    gp.code (Instruction.I_PushFalse);
                }
            } else {
                gp.code(Instruction.I_PushVVal.makePushVVal(literal.getLiteral()));
            }
            return gp;
        }

        BasicOpTuple  basicOpExpressions = BasicOpTuple.isBasicOp(e);
        if (GENERATE_DEBUG_CODE) {
            //When we have function tracing enabled, we want to force all primitive operations to be
            //done as function calls. This will have the effect of ensuring that they get traced when called.  
            if (basicOpExpressions != null && !basicOpExpressions.getName().equals(currentMachineFunction.getQualifiedName())) {
                basicOpExpressions = null;
            }
        }

        // Is e a basic operation (arithmetic, comparative etc.)?  Test only - don't get tuple
        if (basicOpExpressions != null) {
            // Unpack the basic op into subexpressions
            if (CODEGEN_DIAG) {
                MACHINE_LOGGER.log(Level.FINE, "    basic:");
            }

            // Code a basic operation
            int op = basicOpExpressions.getPrimitiveOp ();         

            Instruction instruction = null;

            if (op == PrimOps.PRIMOP_EAGER) {
                return schemeE (basicOpExpressions.getArgument(0), p, d);
            } else
                if (op == PrimOps.PRIMOP_FOREIGN_FUNCTION) {
                    instruction = new Instruction.I_ForeignFunctionCall (basicOpExpressions.getForeignFunctionInfo());
                } else {
                    instruction = new Instruction.I_PrimOp(op);
                }

            int nArgs = basicOpExpressions.getNArguments ();

            if (nArgs < 0) {
                throw new CodeGenerationException("Internal Coding Error: Invalid basic operator arity");   
            }

            if (op == PrimOps.PRIMOP_CAL_VALUE_TO_OBJECT) {
                //Prelude.calValueToObject is non-strict in its first argument.
                
                gp.code (schemeC (basicOpExpressions.getArgument(0), p, d));
                gp.code (instruction);
            } else 
            {
                for (int i = 0; i < nArgs; ++i) {
                    gp.code (schemeE (basicOpExpressions.getArgument(i), p, d + i));
                }
                gp.code (instruction);
            }

            return gp;
        }

        // Is e an application of a saturated constructor?
        if (ConstructorOpTuple.isConstructorOp(e, true) != null) {
            // Unpack the basic op into subexpressions
            if (CODEGEN_DIAG) {
                MACHINE_LOGGER.log(Level.FINE, "    basic:");
            }

            ConstructorOpTuple  constructorOpExpressions = ConstructorOpTuple.isConstructorOp(e, false);

            DataConstructor dc = constructorOpExpressions.getDataConstructor ();

            Instruction instruction = Instruction.I_PackCons.makePackCons(dc);

            int nArgs = constructorOpExpressions.getNArguments ();

            if (nArgs < 0) {
                throw new CodeGenerationException ("Internal Coding Error: Invalid constructor operator arity");   
            }

            for (int i = 0; i < nArgs; ++i) {
                gp.code (schemeC (constructorOpExpressions.getArgument(nArgs - i - 1), p, d + i));
            }

            // Force the evaluation of any strict arguments.
            if (dc.hasStrictArgs()) {
                for (int i = 0; i < dc.getArity(); ++i) {
                    if (dc.isArgStrict(i)) {
                        gp.code(new Instruction.I_Push(i));
                        gp.code(Instruction.I_Eval);
                        gp.code(new Instruction.I_Pop(1));
                    }
                }
            }


            gp.code (instruction);

            return gp;
        }


        // Is e a variable?
        Expression.Var var = e.asVar();
        if (var != null) {
            if (CODEGEN_DIAG) {
                MACHINE_LOGGER.log(Level.FINE, "    Var:");
            }

            // e is a variable, possible addressing modes are:
            // Push <k> for an argument
            // PushGlobal <l> for a label (e.g. supercombinator)

            // Code an Push <k> if we find it's an argument
            Integer ei = p.get(var.getName());
            if (ei == null) {
                // No argument, ENTER LABEL instead - this has to be resolved at runtime
                gp.code(new Instruction.I_PushGlobal(var.getName())); 

                // If the global is a non-zero arity SC we can skip the I_Eval instruction
                // since it won't do anything.
                MachineFunction mf = currentModule.getFunction(var.getName());
                if (mf == null || mf.getArity() == 0) {
                    gp.code (Instruction.I_Eval);
                }

                if (CODEGEN_DIAG) {
                    MACHINE_LOGGER.log(Level.FINE, "        Global:");
                }
            } else {
                gp.code (new Instruction.I_Push (d - ei.intValue()));
                gp.code (Instruction.I_Eval);

                if (CODEGEN_DIAG) {
                    MACHINE_LOGGER.log(Level.FINE, "        local:");
                }
            }
            return gp;
        }

        // Is e a conditional op (if <cond expr> <then expr> <else expr>)?
        CondTuple conditionExpressions = CondTuple.isCondOp(e);
        if (conditionExpressions != null) {
            if (CODEGEN_DIAG) {
                MACHINE_LOGGER.log(Level.FINE, "    condition:");
            }

            // This is a conditional op.  The conditionExpressions tuple holds (kCond, kThen, kElse) expressions
            // Generate the code for kThen and kElse, as arguments to a new I_Cond instruction

            gp.code (schemeE (conditionExpressions.getConditionExpression(), p, d));

            InstructionList thenPart = schemeE (conditionExpressions.getThenExpression(), p, d);
            InstructionList elsePart = schemeE (conditionExpressions.getElseExpression(), p, d);

            Instruction i = new Instruction.I_Cond (new Code (thenPart), new Code(elsePart));

            gp.code (i);
            return gp;          
        }

        // Is e a switch?
        Expression.Switch sw = e.asSwitch();
        if (sw != null) {
            throw new CodeGenerationException  ("Encountered a case statement at an inner level.  schemeE.");
        }

        // Is e a data constructor field selection?
        Expression.DataConsSelection dataConsSelection = e.asDataConsSelection();
        if (dataConsSelection != null) {
            if (CODEGEN_DIAG) {
                MACHINE_LOGGER.log(Level.FINE, "    selectDC:");
            }

            gp.code (schemeE(dataConsSelection.getDCValueExpr(), p, d));
            gp.code (
                    new Instruction.I_SelectDCField(dataConsSelection.getDataConstructor(), 
                            dataConsSelection.getFieldIndex(), 
                            toRuntimeErrorInfo (dataConsSelection.getErrorInfo())));
            gp.code(Instruction.I_Eval);
            return gp;
        }

        // Is e a let expression?
        Expression.Let let = e.asLet();
        if (let != null) {
            // Currently the compiler doesn't differentiate between let and letrec scenarios.
            // As such we have to treat all lets as letrecs.

            Expression.Let.LetDefn[] defs = let.getDefns();

            EnvAndDepth ead = schemeXr (defs, p, d);            

            InstructionList gprecs = schemeCLetrec (defs, ead.env, ead.depth);

            gp.code (gprecs);

            gp.code (schemeE (let.getBody (), ead.env, ead.depth));

            if (ead.depth - d > 0) {
                gp.code (new Instruction.I_Slide (ead.depth - d));
            }

            return gp;  
        }

        // Is e a tail recursive call?
        if (e.asTailRecursiveCall() != null) {
            // The g-machine doesn't have a specific optimization for tail recursive calls
            // so we simply handle it as the original fully saturated application and let
            // the general tail call optimization handle it.
            return schemeE (e.asTailRecursiveCall().getApplForm(), p, d);
        }

        // Is e an application?
        Expression.Appl appl = e.asAppl();
        if (appl != null) {
            if (CODEGEN_DIAG) {
                MACHINE_LOGGER.log(Level.FINE, "    Application:");
            }

            InstructionList il = schemeES (e, p, d+1, 0);
            if (il != null) { 
                gp.code (new Instruction.I_Alloc (1));
                gp.code (il);
            } else {
                gp.code (schemeC (e, p, d));
                gp.code (Instruction.I_Eval);
            }

            return gp;
        } 

        // Is e a record update
        Expression.RecordUpdate recordUpdateExpr = e.asRecordUpdate();
        if (recordUpdateExpr  != null) {
            if (CODEGEN_DIAG) {
                MACHINE_LOGGER.log(Level.FINE, "    Record extension:");
            }

            Expression baseRecordExpr = recordUpdateExpr .getBaseRecordExpr();

            //FieldName -> Expression
            Map<FieldName, Expression> updateFieldValuesMap = recordUpdateExpr .getUpdateFieldValuesMap();

            // Strictly evaluate the base record.
            gp.code(schemeE (baseRecordExpr, p, d));

            // Create a new record that is a copy of the base record. 
            gp.code(new Instruction.I_ExtendRecord());           

            // Put the field values into the new record instance.
            for (final Map.Entry<FieldName, Expression> entry : updateFieldValuesMap.entrySet()) {               
                FieldName fieldName = entry.getKey();
                Expression valueExpr = entry.getValue();
                gp.code (schemeC (valueExpr, p, d+1));
                gp.code (new Instruction.I_PutRecordField(fieldName.getCalSourceForm()));
            }

            return gp;
        }          

        // Is e a record extension
        Expression.RecordExtension recordExtensionExpr = e.asRecordExtension();
        if (recordExtensionExpr != null) {
            if (CODEGEN_DIAG) {
                MACHINE_LOGGER.log(Level.FINE, "    Record extension:");
            }

            Expression baseRecordExpr = recordExtensionExpr.getBaseRecordExpr();

            //FieldName -> Expression
            Map<FieldName, Expression> extensionFieldsMap = recordExtensionExpr.getExtensionFieldsMap();

            if (baseRecordExpr == null) {
                // No base record so create a new one.
                gp.code(new Instruction.I_CreateRecord(extensionFieldsMap.size()));       
            } else {
                // Strictly evaluate the base record.
                gp.code(schemeE (baseRecordExpr, p, d));

                // Create a new record that is a copy of the base record. 
                gp.code(new Instruction.I_ExtendRecord());
            }

            // Put the field values into the new record instance.
            for (final Map.Entry<FieldName, Expression> entry : extensionFieldsMap.entrySet()) {              
                FieldName fieldName = entry.getKey();
                Expression valueExpr = entry.getValue();
                gp.code (schemeC (valueExpr, p, d+1));
                gp.code (new Instruction.I_PutRecordField(fieldName.getCalSourceForm()));
            }

            return gp;
        }  

        // e is a record selection
        Expression.RecordSelection recordSelection = e.asRecordSelection();
        if (recordSelection != null) {
            if (CODEGEN_DIAG) {
                MACHINE_LOGGER.log(Level.FINE, "    Record selection:");
            }

            Expression recordExpr = recordSelection.getRecordExpr();
            String fieldName = recordSelection.getFieldName().getCalSourceForm();

            // Evaluate the record to WHNF.            
            gp.code(schemeE(recordExpr, p, d));

            // Get the field value from the record.
            gp.code(new Instruction.I_RecordSelection(fieldName));
            gp.code(Instruction.I_Eval);
            return gp;
        }  

        // e is a record case
        Expression.RecordCase recordCase = e.asRecordCase();
        if (recordCase != null) {
            MACHINE_LOGGER.log(Level.FINE, "\nCodeGen: Error, encountered a switch statement in the E scheme:\n" + e);
            throw new CodeGenerationException("CodeGen: Error, encountered a switch statement in the E scheme:\n" + e);
        }    


        MACHINE_LOGGER.log(Level.FINE, "\nCodeGen: Bad exit of E compilation scheme with intermediate code: " + e);
        logEnvironment(p);        

        throw new CodeGenerationException("CodeGen: Bad exit of E compilation scheme with intermediate code: " + e);
    }

    /** 
     * Execute the ES compilation scheme.  Completes the evaluation of an expression,
     * the top n ribs of which have already been put on the stack.
     * ES constructs instances of the ribs of E, putting them on the stack and
     * then completes the evaluation in the same was as schemeE.
     * @param e Expression
     * @param p Map: a table linking variable names to stack offsets.
     * @param d int: the depth of the current execution context minus one (i.e. at this point the number of arguments).
     * @param n int: number of ribs already on stack.
     * @return null if the given Expression is not handled by the ES scheme. Otherwise: InstructionList the instructions and other data compiled by this scheme
     * @throws CodeGenerationException
     */
    private InstructionList schemeES (Expression e, Map<QualifiedName, Integer> p, int d, int n) throws CodeGenerationException {
        // RS[[ f ]] p d n = PUSHGLOBAL f; MKAP n; UPDATE (d-n); POP (d-n); UNWIND;
        // RS[[ x ]] p d n = PUSH (d - px); MKAP n; UPDATE (d-n); POP (d-n); UNWIND;
        // RS[[ HEAD E]] p d n = E[[ E ]] p d; HEAD; MKAP n; UPDATE (d-n); POP (d-n); UNWIND;
        // RS[[ If Ec Et Ef]] p d n = E[[ Ec]] p d; I_Cond (RS[[ Et ]] p d n, RS[[ Ef ]] p d n);
        // RS[[ E1 E2 ]] = C[[ E2 ]] p d; RS [[ E1 ]] p (d+1) (n+1);


        InstructionList gp = new InstructionList ();

        // Is e a variable?
        Expression.Var var = e.asVar();
        if (var != null) {
            if (CODEGEN_DIAG) {
                MACHINE_LOGGER.log(Level.FINE, "    Var:");
            }

            // e is a variable, possible addressing modes are:
            // Push <k> for an argument
            // PushGlobal <l> for a label (e.g. supercombinator)

            // Code an Push <k> if we find it's an argument
            Integer ei = p.get(var.getName());
            if (ei == null) {

                gp.code(new Instruction.I_PushGlobal(var.getName())); 

                gp.code (new Instruction.I_Call (n));

                if (CODEGEN_DIAG) {
                    MACHINE_LOGGER.log(Level.FINE, "        Global:");
                }
            } else {
                gp.code (new Instruction.I_Push (d - ei.intValue()));

                gp.code (new Instruction.I_Call (n));

                if (CODEGEN_DIAG) {
                    MACHINE_LOGGER.log(Level.FINE, "        local:");
                }
            }
            return gp;
        }

        // Is e a conditional op (if <cond expr> <then expr> <else expr>)?
        CondTuple conditionExpressions = CondTuple.isCondOp(e);
        if (conditionExpressions != null) {
            if (CODEGEN_DIAG) {
                MACHINE_LOGGER.log(Level.FINE, "    condition:");
            }

            // This is a conditional op.  The conditionExpressions tuple holds (kCond, kThen, kElse) expressions
            // Generate the code for kThen and kElse, as arguments to a new I_Cond instruction

            gp.code (schemeE (conditionExpressions.getConditionExpression(), p, d));

            InstructionList thenPart = schemeES (conditionExpressions.getThenExpression(), p, d, n);
            InstructionList elsePart = schemeES (conditionExpressions.getElseExpression(), p, d, n);
            if (thenPart == null || elsePart == null) {
                // Either the then or else part could not be handled by the RS scheme. Return null and
                // let the calling code decide whether to abort or use an alternate scheme.
                return null;
            }

            Instruction i = new Instruction.I_Cond (new Code (thenPart), new Code(elsePart));

            gp.code (i);
            return gp;          
        }

        // Is e a switch?
        Expression.Switch sw = e.asSwitch();
        if (sw != null) {
            throw new CodeGenerationException  ("Encountered a case statement at an inner level.  schemeES.");
        }

        // Is e an application?
        Expression.Appl appl = e.asAppl();
        if (appl != null) {
            if (CODEGEN_DIAG) {
                MACHINE_LOGGER.log(Level.FINE, "    Application:");
            }

            // e is an application
            // Get e1 (LHS) and e2 (RHS) expressions
            Expression e1 = appl.getE1();
            Expression e2 = appl.getE2();

            InstructionList gpe2 = schemeC (e2, p, d);
            InstructionList gpe1 = schemeES (e1, p, d + 1, n + 1);
            if (gpe1 == null) {
                // The RHS could not be compiled by the RS scheme.  Return null and let
                // the calling code decide whether to abort or use an alternate scheme.
                return null;
            }

            gp.code (gpe2);
            gp.code (gpe1);

            return gp;
        }   

        // At this point we know that the given Expression cannot be handled by the ES scheme.
        // Return null and leave it to the calling code to deal with things appropriately.
        return null;
    }

    /**
     * Execute the C compilation scheme.  This generates code construct an
     * instance of the given expression.
     * Creation date: (12/04/02 9:32:17 AM)
     * @param e Expression the expression
     * @param p Map: a table linking variable names to stack offsets.
     * @param d int: the depth of the current execution context minus one (i.e. at this point the number of arguments).
     * @return InstructionList the instructions and other data compiled by this scheme
     * @throws CodeGenerationException
     */
    private InstructionList schemeC (Expression e, Map<QualifiedName, Integer> p, int d) throws CodeGenerationException {
        // C[[ i ]] p d = PUSHVVAL i;
        // C[[ f ]] p d = PUSHGLOBAL f;
        // C[[ x ]] p d = PUSH (d - p(x));
        // C[[ E1 E2 ]] p d = C[[ E2 ]] p d; C[[ E1 ]] p (d + 1); MKAP;
        // C[[ letrec D in Eb ]] p d = CLetrec[[ D ]] p1 d1; C[[ Eb ]] p1 d1; SLIDE (d1 - d);
        //    where
        //    (p1, d1) = Xr[[ D ]] p d;

        // Show diagnostics if turned on
        if (CODEGEN_DIAG) {
            // DIAG
            MACHINE_LOGGER.log(Level.FINE, "\nCodeGen: Entering C compilation scheme with intermediate code:\n" + e);
            
            logEnvironment(p);            
        }

        if (canIgnoreLaziness(e, p)) {
            return schemeE(e, p, d);
        }

        InstructionList gp = new InstructionList ();

        // Is e a literal?
        Expression.Literal literal = e.asLiteral();
        if (literal != null) {
            // Code a I_PushVVal instruction to push the literal onto the stack.

            if (CODEGEN_DIAG) {
                MACHINE_LOGGER.log(Level.FINE, "    Literal:");
            }

            Object val = literal.getLiteral ();
            if (val instanceof Boolean) {
                // Booleans are handled as a special case.
                if (((Boolean)val).booleanValue()) {
                    gp.code (Instruction.I_PushTrue);
                } else {
                    gp.code (Instruction.I_PushFalse);
                }
            } else {
                gp.code(Instruction.I_PushVVal.makePushVVal(literal.getLiteral()));
            }
            return gp;
        }

        // Is e a basic operation?
        // There is one basic operation, Prelude.eager, which is treated specially in the C scheme.
        if (BasicOpTuple.isBasicOp(e) != null) {
            BasicOpTuple basicOpExpressions = BasicOpTuple.isBasicOp(e);

            // Code a basic operation
            int op = basicOpExpressions.getPrimitiveOp ();         

            if (op == PrimOps.PRIMOP_EAGER) {
                return schemeE (basicOpExpressions.getArgument(0), p, d);
            }
        }


        // Is e a variable?
        Expression.Var var = e.asVar();
        if (var != null) {
            if (CODEGEN_DIAG) {
                MACHINE_LOGGER.log(Level.FINE, "    Var:");
            }

            // e is a variable, possible addressing modes are:
            // Push <k> for an argument
            // PushGlobal <l> for a label (e.g. supercombinator)

            // Code an Push <k> if we find it's an argument
            Integer ei = p.get(var.getName());
            if (ei == null) {
                // No argument, ENTER LABEL instead - this has to be resolved at runtime
                gp.code(new Instruction.I_PushGlobal(var.getName()));                         

                if (CODEGEN_DIAG) {
                    MACHINE_LOGGER.log(Level.FINE, "        Global:");
                }
            } else {
                gp.code(new Instruction.I_Push (d - ei.intValue()));

                if (CODEGEN_DIAG) {
                    MACHINE_LOGGER.log(Level.FINE, "        local:");
                }
            }
            return gp;
        }


        // Is e a switch?
        Expression.Switch sw = e.asSwitch();
        if (sw != null) {
            throw new CodeGenerationException  ("Encountered a case statement at an inner level.  schemeC.");
        }

        // Is e a data cons selection?
        Expression.DataConsSelection dataConsSelection = e.asDataConsSelection();
        if (dataConsSelection != null) {
            if (CODEGEN_DIAG) {
                MACHINE_LOGGER.log(Level.FINE, "    selectDC:");
            }

            gp.code (schemeC(dataConsSelection.getDCValueExpr(), p, d));
            gp.code (new Instruction.I_LazySelectDCField(dataConsSelection.getDataConstructor(),
                    dataConsSelection.getFieldIndex(),
                    toRuntimeErrorInfo (dataConsSelection.getErrorInfo()))); 
            return gp;
        }

        // Is e a tail recursive call?
        if (e.asTailRecursiveCall() != null) {
            // The g-machine doesn't have a specific optimization for tail recursive calls
            // so we simply handle it as the original fully saturated application and let
            // the general tail call optimization handle it.
            return schemeC (e.asTailRecursiveCall().getApplForm(), p, d);
        }

        // Is e an application?
        Expression.Appl appl = e.asAppl();
        if (appl != null) {
            if (CODEGEN_DIAG) {
                MACHINE_LOGGER.log(Level.FINE, "    Application:");
            }

            // e is an application
            // Get e1 (LHS) and e2 (RHS) expressions
            Expression e1 = appl.getE1();
            Expression e2 = appl.getE2();

            InstructionList gpe2 = schemeC (e2, p, d);
            InstructionList gpe1 = schemeC (e1, p, d + 1);

            gp.code (gpe2);
            gp.code (gpe1);
            gp.code  (new Instruction.I_MkapN ());

            return gp;
        }   

        // Is e a let expression?
        Expression.Let let = e.asLet();
        if (let != null) {
            // Currently the compiler doesn't differentialte between let and letrec scenarios.
            // As such we have to treat all lets as letrecs.

            Expression.Let.LetDefn[] defs = let.getDefns();

            EnvAndDepth ead = schemeXr (defs, p, d);            

            InstructionList gprecs = schemeCLetrec (defs, ead.env, ead.depth);

            gp.code (gprecs);

            gp.code (schemeC (let.getBody (), ead.env, ead.depth));

            if (ead.depth - d > 0) {
                gp.code (new Instruction.I_Slide (ead.depth - d));
            }

            return gp;  
        }

        Expression.RecordUpdate recordUpdateExpr = e.asRecordUpdate();
        if (recordUpdateExpr != null) {
            if (CODEGEN_DIAG) {
                MACHINE_LOGGER.log(Level.FINE, "    Record update:");
            }

            Expression baseRecordExpr = recordUpdateExpr.getBaseRecordExpr();

            //FieldName -> Expression
            Map<FieldName, Expression> updateFieldValuesMap = recordUpdateExpr.getUpdateFieldValuesMap();

            // Lazy evaluation of the base record.
            gp.code(schemeC (baseRecordExpr, p, d));

            // Create an application of the virtual update function to the base record.
            gp.code(new Instruction.I_LazyRecordUpdate());

            // Add field values.  In the case of extending an existing record these are
            // added to the record extension node as if they were further arguments in 
            // the application chain for the virtual function.
            for (final Map.Entry<FieldName, Expression> entry : updateFieldValuesMap.entrySet()) {             
                FieldName fieldName = entry.getKey();
                Expression valueExpr = entry.getValue();
                gp.code (schemeC (valueExpr, p, d+1));
                gp.code (new Instruction.I_PutRecordField(fieldName.getCalSourceForm()));
            }

            return gp;            
        }

        // Is e a record extension
        // e is a record extension
        Expression.RecordExtension recordExtensionExpr = e.asRecordExtension();
        if (recordExtensionExpr != null) {
            if (CODEGEN_DIAG) {
                MACHINE_LOGGER.log(Level.FINE, "    Record extension:");
            }

            Expression baseRecordExpr = recordExtensionExpr.getBaseRecordExpr();

            //FieldName -> Expression
            Map<FieldName, Expression> extensionFieldsMap = recordExtensionExpr.getExtensionFieldsMap();

            if (baseRecordExpr == null) {
                // No base record so we create a new one.
                gp.code(new Instruction.I_CreateRecord(extensionFieldsMap.size()));       
            } else {
                // Lazy evaluation of the base record.
                gp.code(schemeC (baseRecordExpr, p, d));

                // Create an application of the virtual extension function to the base record.
                gp.code(new Instruction.I_LazyRecordExtension());
            }

            // Add field values.  In the case of extending an existing record these are
            // added to the record extension node as if they were further arguments in 
            // the application chain for the virtual function.
            for (final Map.Entry<FieldName, Expression> entry : extensionFieldsMap.entrySet()) {                 
                FieldName fieldName = entry.getKey();
                Expression valueExpr = entry.getValue();
                gp.code (schemeC (valueExpr, p, d+1));
                gp.code (new Instruction.I_PutRecordField(fieldName.getCalSourceForm()));
            }

            return gp;
        }  

        // e is a record selection
        Expression.RecordSelection recordSelection = e.asRecordSelection();
        if (recordSelection != null) {
            if (CODEGEN_DIAG) {
                MACHINE_LOGGER.log(Level.FINE, "    Record selection:");
            }

            Expression recordExpr = recordSelection.getRecordExpr();
            String fieldName = recordSelection.getFieldName().getCalSourceForm();

            // If we can ignore laziness for the base record (i.e. it can be safely
            // forced to WHNF) then we can just select the field value, but don't force
            // the field value to WHNF
            if (canIgnoreLaziness(recordExpr, p)) {
                // Evaluate the record to WHNF.            
                gp.code(schemeE(recordExpr, p, d));

                // Get the field value from the record.
                gp.code(new Instruction.I_RecordSelection(fieldName));
            } else { 

                // Lazy evaluation of the record.
                gp.code(schemeC(recordExpr, p, d));

                // Create a node representing the application of a virtual selection function to the
                // record.
                gp.code(new Instruction.I_LazyRecordSelection(fieldName));
            }

            return gp;
        }  

        Expression.ErrorInfo errorInfo = e.asErrorInfo();
        if (errorInfo != null){
            gp.code(Instruction.I_PushVVal.makePushVVal(new ErrorInfo(errorInfo.getTopLevelFunctionName(), errorInfo.getLine(), errorInfo.getColumn())));
            return gp;
        }

        // e is a record case
        Expression.RecordCase recordCase = e.asRecordCase();
        if (recordCase != null) {
            MACHINE_LOGGER.log(Level.FINE, "\nCodeGen: Error, encountered a switch statement in the E scheme:\n" + e);
            throw new CodeGenerationException("CodeGen: Error, encountered a switch statement in the E scheme: " + e);
        }    

        MACHINE_LOGGER.log(
                Level.FINE,
                "\nCodeGen: bad exit of C compilation scheme with intermediate code:\n"
                + e);
        logEnvironment(p);

        throw new CodeGenerationException("CodeGen: bad exit of C compilation scheme with intermediate code: " + e);
    }

    /**
     * Generate code to build up the graphs for a set of letrecs.
     * @param defs Expression.Let.LetDefn[]: the set of letrec definitions.
     * @param env Map: the current environment.
     * @param d int: the depth of the current context.
     * @return InstructionList
     * @throws CodeGenerationException
     */
    private InstructionList schemeCLetrec (Expression.Let.LetDefn[] defs, Map<QualifiedName, Integer> env, int d) throws CodeGenerationException {
        // Show diagnostics if turned on
        if (CODEGEN_DIAG) {
            // DIAG
            MACHINE_LOGGER.log(Level.FINE, "\nCodeGen: Entering CLetrec compilation scheme with intermediate code:\n");
            for (int i = 0; i < defs.length; ++i) {
                MACHINE_LOGGER.log(Level.FINE, currentMachineFunction.getQualifiedName().getModuleName() + "." + defs[i].getVar() + " = " + defs[i].getExpr() + "\n");
            }

            logEnvironment(env);           
        }

        InstructionList gp = new InstructionList ();

        gp.code (new Instruction.I_Alloc (defs.length));
        for (int i = 0; i < defs.length; ++i) {
            gp.code (schemeC (defs [i].getExpr (), env, d));
            gp.code (new Instruction.I_Update (defs.length - i - 1));
        }

        return gp;
    }

    /**
     * Create an autmented environment and context depth for a set of
     * letrec definitions.
     * @param defs Expression.Let.LetDefn[]: the set of letrec definitions.
     * @param env Map: the environment to be augmented.
     * @param d int: the depth to be augmented.
     * @return EnvAndDepth
     */
    private EnvAndDepth schemeXr (Expression.Let.LetDefn[] defs, Map<QualifiedName, Integer> env, int d) {
        // Xr [[ x1 = E1; x2 = E2; ... xn = En; ]] p d = (p[x1 = d + 1; x2 = d + 2; ... xn = d + n;], d + n);

        EnvAndDepth retVal = new EnvAndDepth ();

        retVal.depth = d + defs.length;
        Map<QualifiedName, Integer> newEnv = argOffset (0, env);

        for (int i = 0; i < defs.length; ++i) {
            Expression.Let.LetDefn def = defs [i];
            QualifiedName qn = QualifiedName.make(currentMachineFunction.getQualifiedName().getModuleName(), def.getVar());

            newEnv.put (qn, Integer.valueOf(d + i + 1));
        }

        retVal.env = newEnv;

        return retVal;        
    }

    /**
     * Create a new environment in which all the elements of the given 
     * environment are offset by n.
     * @param n int: amount to offset by.
     * @param oldEnv Map: the existing environment.
     * @return Map.
     */
    private Map<QualifiedName, Integer> argOffset (int n, Map<QualifiedName, Integer> oldEnv) {
        Map<QualifiedName, Integer> env = new HashMap<QualifiedName, Integer> ();
        Iterator<Map.Entry<QualifiedName, Integer>> entries = oldEnv.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<QualifiedName, Integer> entry = entries.next();
            env.put (entry.getKey(), Integer.valueOf((entry.getValue()).intValue() + n));
        }

        return env;
    }

    private void appendUpdateCode (InstructionList gp, int d) {
        gp.code (new Instruction.I_Update (d));
        if (d > 0) {
            gp.code (new Instruction.I_Pop (d));
        }
        gp.code (Instruction.I_Unwind);
    }

    /**
     * Get the vars for an alt
     * @param alt the alt for which to retrieve the vars.
     * @param altTag the altTag for the alt
     * @return the array of vars, in the order in which they appear in the corresponding data constructor (if any).
     *   0-length array if the alt's tag is not a data constructor.
     */
    private String[] getVars(Expression.Switch.SwitchAlt alt, Object altTag) {

        if (altTag instanceof DataConstructor) {
            DataConstructor dataCons = (DataConstructor)altTag;
            String[] vars = new String[dataCons.getArity()];
            Arrays.fill(vars, Expression.Switch.SwitchAlt.WILDCARD_VAR);

            if (alt instanceof Expression.Switch.SwitchAlt.Positional) {
                Map<Integer, String> positionToVarNameMap = ((Expression.Switch.SwitchAlt.Positional)alt).getPositionToVarNameMap();
                for (final Map.Entry<Integer, String> entry : positionToVarNameMap.entrySet()) {
                   
                    Integer positionInteger = entry.getKey();
                    String varName = entry.getValue();

                    int fieldIndex = positionInteger.intValue();
                    vars[fieldIndex] = varName;
                }
            } else {
                // Must be matching.
                Map<FieldName, String> fieldNameToVarNameMap = ((Expression.Switch.SwitchAlt.Matching)alt).getFieldNameToVarNameMap();
                for (final Map.Entry<FieldName, String> entry : fieldNameToVarNameMap.entrySet()) {                   
                    FieldName fieldName = entry.getKey();
                    String varName = entry.getValue();

                    int fieldIndex = dataCons.getFieldIndex(fieldName);
                    vars[fieldIndex] = varName;
                }
            }

            return vars;

        } else {
            return new String[0];
        }
    }

    /**
     * Replace all I_PushGlobal instructions with I_PushGlobalN so that
     * running code doesn't require lookups into the Program.
     * @param module
     * @throws CodeGenerationException
     */
    private void fixupPushGlobals (Module module) throws CodeGenerationException {

        for (final MachineFunction mf : module.getFunctions()) {
            GMachineFunction gmf = (GMachineFunction)mf;

            Code code = gmf.getCode ();
            if (code == null) {
                continue;
            }

            Instruction[] instructions = code.getInstructions();
            for (int i = 0; i < instructions.length; ++i) {

                Instruction inst = instructions [i];

                if (inst instanceof Instruction.I_PushGlobal) {
                    Instruction.I_PushGlobal pg = (Instruction.I_PushGlobal)inst;
                    // We need to handle the supercombinators that are hand-coded and
                    // therefore not in the actual Program object.
                    QualifiedName globalName = pg.getName();
                    NPrimitiveFunc npf = primitiveFuncMap.get (globalName);
                    if (npf != null) {
                        instructions[i] = new Instruction.I_PushPrimitiveNode(npf);
                    } else {
                        // We can do a fixup of the instruction if the function being pushed is not a CAF.
                        // Non-CAF functions have a single instance of NGlobal associated with them.
                        // CAF functions have multiple instances of NGlobal associated with them, keyed by 
                        // the execution context.
                        MachineFunction calledFunction = module.getFunction(globalName);
                        if (calledFunction != null) {
                            if (calledFunction.isForeignFunction() || calledFunction.getArity() > 0) {                    
                                NGlobal node = ((GMachineFunction)calledFunction).makeNGlobal(null);
                                instructions [i] = new Instruction.I_PushGlobalN (node, !calledFunction.isForeignFunction());
                            }
                        } else {
                            throw new CodeGenerationException ("Unable to find code label for " + globalName + " when fixing up I_PushGlobal instructions.");
                        }
                    }
                }
            }
        }                
    }      

    /**
     * Determine if we can ignore laziness for the provided expression.
     * @param e
     * @param env
     * @return true if laziness can be ignored.
     * @throws CodeGenerationException 
     */
    private boolean canIgnoreLaziness (Expression e, Map<QualifiedName, Integer> env) throws CodeGenerationException {
        // Literal values are already in WHNF
        if (e.asLiteral() != null) {
            return true;
        }

        // If a var is a non-zero arity SC, a DC, or is already evaluated we can shortcut it.
        if (e.asVar() != null) {
            Expression.Var var = e.asVar();

            // Data constructors are already in WHNF.
            if (var.getDataConstructor() != null) {
                return true;
            }

            // If the variable is a strict function argument it is already in WHNF.
            Integer ei = env.get(var.getName());
            if (ei != null) {
                // This is a function argument check if it is strict.

            } else {
                // This is an SC
                MachineFunction mf = currentModule.getFunction(var.getName());
                if (mf != null && mf.getArity() > 0) {
                    return true;
                }

            }
        }

        // Is e an application of a saturated constructor?
        ConstructorOpTuple constructorOpTuple = ConstructorOpTuple.isConstructorOp(e, false); 
        if (constructorOpTuple != null) {
            DataConstructor dc = constructorOpTuple.getDataConstructor ();

            boolean[] fieldStrictness = new boolean [dc.getArity()];
            boolean dcHasStrictFields = false;
            for (int i = 0; i < dc.getArity(); ++i) {
                fieldStrictness[i] = dc.isArgStrict(i);
                if (fieldStrictness[i]) {
                    dcHasStrictFields = true;
                }
            }

            // If there are no strict arguments we can simply create an instance of the DC class.
            // The simplest way to do this is to treat this DC application as if it were in a strict context.
            if (!dcHasStrictFields) {
                return true;
            } else {
                // If all strict arguments are already evaluated, or we consider them safe to evaluate (i.e. cheap and
                // with no side effects) we can treat this as strict.
                boolean allOK = true;
                for (int i = 0; i < dc.getArity(); ++i) {
                    if (dc.getArgStrictness()[i] && !canIgnoreLaziness(constructorOpTuple.getArgument(i), env)) {
                        allOK = false;
                        break;
                    }
                }

                if (allOK) {
                    return true;
                }
            }        
        }

        // We can shortcut a basic op if it is marked as allowed to 
        // be eagerly evaluated and all arguments can all be shortcut.
        // Also if the op is Prelude.eager we can shortcut.
        BasicOpTuple basicOpExpressions = BasicOpTuple.isBasicOp(e);
        if (basicOpExpressions != null) {
            if (basicOpExpressions.getPrimitiveOp() == PrimOps.PRIMOP_EAGER) {
                return true;
            }

            QualifiedName opName = basicOpExpressions.getName();
            MachineFunction mf = currentModule.getFunction(opName);
            if (mf == null) {
                return false;
            }

            if (mf.canFunctionBeEagerlyEvaluated()) {
                int nArgs = basicOpExpressions.getNArguments();
                int nWHNFArgs = 0;
                for (int i = 0; i < nArgs; ++i) {
                    Expression eArg = basicOpExpressions.getArgument(i);
                    if (canIgnoreLaziness(eArg, env)) {
                        nWHNFArgs++;
                    }
                }
                if (nArgs == nWHNFArgs) {
                    // All the args are in WHNF so ideally we can ignore laziness for
                    // this primitive operation.  However, there are some primitive 
                    // ops where an additional condition, that the second argument is
                    // known to not be zero, is required.
                    String unqualifiedOpName = opName.getUnqualifiedName();
                    if (opName.getModuleName().equals(CAL_Prelude.MODULE_NAME) &&
                            (unqualifiedOpName.equals("divideLong") ||
                                    unqualifiedOpName.equals("remainderLong") ||
                                    unqualifiedOpName.equals("divideInt") ||
                                    unqualifiedOpName.equals("remainderInt") ||
                                    unqualifiedOpName.equals("divideShort") ||
                                    unqualifiedOpName.equals("remainderShort") ||
                                    unqualifiedOpName.equals("divideByte") ||
                                    unqualifiedOpName.equals("remainderByte"))) {

                        // Check that the second argument is a non zero literal.

                        Expression arg = basicOpExpressions.getArgument(1);
                        if (arg.asLiteral() != null) {

                            if (unqualifiedOpName.equals("divideLong") || unqualifiedOpName.equals("remainderLong")) {

                                Long l = (Long)arg.asLiteral().getLiteral();
                                return l.longValue() != 0;

                            } else if (unqualifiedOpName.equals("divideInt") || unqualifiedOpName.equals("remainderInt")) {

                                Integer i = (Integer)arg.asLiteral().getLiteral();
                                return i.intValue() != 0;

                            } else if (unqualifiedOpName.equals("divideShort") || unqualifiedOpName.equals("remainderShort")) {

                                Short shortValue = (Short)arg.asLiteral().getLiteral();
                                return shortValue.shortValue() != 0;

                            } else if (unqualifiedOpName.equals("divideByte") || unqualifiedOpName.equals("remainderByte")) {

                                Byte byteValue = (Byte)arg.asLiteral().getLiteral();
                                return byteValue.byteValue() != 0;

                            } else {
                                throw new IllegalStateException();
                            }
                        } else {
                            return false;
                        }
                    } else {
                        return true;
                    }
                } else {
                    return false;
                }
            }
        }

        basicOpExpressions = BasicOpTuple.isAndOr (e);
        if (basicOpExpressions != null) {

            // Code a basic operation
            int nArgs = basicOpExpressions.getNArguments ();
            int nWHNFArgs = 0;
            for (int i = 0; i < nArgs; ++i) {
                Expression eArg = basicOpExpressions.getArgument(i);
                if (canIgnoreLaziness(eArg, env)) {
                    nWHNFArgs++;
                }
            }
            if (nArgs == nWHNFArgs) {
                return true;
            }
        }

        // If e is a fully saturated application of a function tagged for optimization and
        // all the arguments are in WHNF or can have laziness ignored we can 
        // ignore laziness for the application.
        if (e.asAppl() != null) {
            Expression[] chain = appChain(e.asAppl());
            if (chain[0].asVar() != null) {
                // Get the supercombinator on the left end of the chain.
                Expression.Var scVar = chain[0].asVar();
                if (scVar != null) {
                    // Check if this supercombinator is one we should try to optimize.
                    MachineFunction mf = currentModule.getFunction(scVar.getName());
                    if (mf != null && mf.canFunctionBeEagerlyEvaluated()) {

                        // Now determine the arity of the SC.
                        int calledArity = mf.getArity();

                        // Check to see if we can ignore laziness for all the arguments.
                        if (chain.length - 1 == calledArity) {
                            int nWHNFArgs = 0;
                            for (int i = 0; i < calledArity; ++i) {
                                if (canIgnoreLaziness(chain[i+1], env)) {
                                    nWHNFArgs++;
                                }
                            }

                            if (nWHNFArgs == calledArity) {
                                return true;
                            }
                        }
                    }
                }
            }
        }

        // Is e an application of a zero arity constructor.
        if (ConstructorOpTuple.isConstructorOp(e, true) != null) {
            ConstructorOpTuple  constructorOpExpressions = ConstructorOpTuple.isConstructorOp(e, false);

            DataConstructor dc = constructorOpExpressions.getDataConstructor ();

            if (dc.getArity() == 0){
                return true;
            }
        }

        // Is e a DataConsFieldSelection where the laziness can be ignored for the data constructor 
        // expression and the field is strict.
        if (e.asDataConsSelection() != null) {
            Expression.DataConsSelection dcs = (Expression.DataConsSelection)e;
            if (dcs.getDataConstructor().isArgStrict(dcs.getFieldIndex()) && canIgnoreLaziness(dcs.getDCValueExpr(), env)) {
                return true;
            }
        }

        // 'if a then b else c' where laziness can be ignore for a, b, and c.
        CondTuple conditionExpressions = CondTuple.isCondOp(e);
        if (conditionExpressions != null) {
            Expression condExpr = conditionExpressions.getConditionExpression();
            Expression thenExpr = conditionExpressions.getThenExpression();
            Expression elseExpr = conditionExpressions.getElseExpression();
            if (canIgnoreLaziness(condExpr, env) && 
                    canIgnoreLaziness(thenExpr, env) && 
                    canIgnoreLaziness(elseExpr, env)) {
                return true;
            }
        }

        // We can compile a record extension strictly if the base record is null or
        // laziness can be ignored for the base record.  This is safe because while
        // strict compilation generates code that creates a new record object none
        // of the fields will be comiled differently, thus preserving laziness.
        if (e.asRecordExtension() != null) {
            Expression.RecordExtension re = (Expression.RecordExtension)e;
            return re.getBaseRecordExpr() == null || canIgnoreLaziness(re.getBaseRecordExpr(), env);
        }

        // We can compile a record update strictly if we can ignore laziness for the base
        // record.   This is safe because while
        // strict compilation generates code that creates a new record object none
        // of the fields will be comiled differently, thus preserving laziness.
        if (e.asRecordUpdate() != null) {
            Expression.RecordUpdate ru = (Expression.RecordUpdate)e;
            return canIgnoreLaziness(ru.getBaseRecordExpr(), env);
        }

        ///////////////////
        // Note: we can't ignore laziness for a record selection/case even if 
        // laziness can be ignored for the base record expression since strict
        // compilation would force the evaluation of the field value and change
        // laziness.
        // However there is a less general optimization that can be applied in this
        // situations.  See schemeC().

        return false;
    }

    /**
     * Place an application chain into a more easily manageable format.
     * @param root
     * @return Expression[]
     */
    private Expression[] appChain (Expression.Appl root) {

        // Walk down the left branch.
        Expression c = root;
        int nArgs = 0;
        while (c instanceof Expression.Appl) {
            nArgs++;
            c = ((Expression.Appl)c).getE1();
        }

        Expression[] chain = new Expression [nArgs + 1];
        chain[0] = c;
        c = root;
        for (int i = nArgs; i >= 1; i--) {
            chain[i] = ((Expression.Appl)c).getE2();
            c = ((Expression.Appl)c).getE1();
        }

        return chain;        
    }

    private static class EnvAndDepth {
        int depth;
        Map<QualifiedName, Integer> env;
    }


    /**
     * A log message formatter that simply outputs the message of the log record.
     * Used by ICE to print message to the console without any additional info.
     * @author Frank Worsley
     */
    private static class ConsoleFormatter extends Formatter {

        /**
         * @see java.util.logging.Formatter#format(java.util.logging.LogRecord)
         */
        @Override
        public String format(LogRecord record) {
            return record.getMessage() + "\n";
        }
    }
    
    private static ErrorInfo toRuntimeErrorInfo(final Expression.ErrorInfo errorInfo) {        
        return new ErrorInfo(errorInfo.getTopLevelFunctionName(), errorInfo.getLine(), errorInfo.getColumn());         
    }

    /**
     * Returns the Class object corresponding to the cast type in a cast expression. If the Class object could not be resolved,
     * a CodeGenerationException is thrown.
     * @param castExpression the cast expression.
     * @return the Class object corresponding to the cast type in a cast expression.
     * @throws CodeGenerationException if the Class object could not be resolved.
     */
    private static Class<?> getCastType(final Expression.Cast castExpression) throws CodeGenerationException {
        try {
            return castExpression.getCastType();
        } catch (UnableToResolveForeignEntityException e) {
            throw new CodeGenerationException("Failed to resolve foreign type for Expression.Cast.", e);
        }
    }
    
    private static void logEnvironment (Map<QualifiedName, Integer> env) {
        for (final Map.Entry<QualifiedName, Integer> entry : env.entrySet()) {
            
            QualifiedName key = entry.getKey();
            Integer val = entry.getValue();
            MACHINE_LOGGER.log(Level.FINE, "    " + key + ": " + val);
        }
    }
}
