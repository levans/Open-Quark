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
 * Executor.java
 * Creation date: Jun 18, 2002
 * By: rcypher
 */
package org.openquark.cal.internal.machine.g;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Pattern;

import org.openquark.cal.compiler.DataConstructor;
import org.openquark.cal.compiler.Expression;
import org.openquark.cal.compiler.ForeignFunctionInfo;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.UnableToResolveForeignEntityException;
import org.openquark.cal.compiler.io.EntryPoint;
import org.openquark.cal.internal.machine.DynamicRuntimeEnvironment;
import org.openquark.cal.internal.machine.EntryPointImpl;
import org.openquark.cal.internal.runtime.ExecutionContextImpl;
import org.openquark.cal.machine.CALExecutor;
import org.openquark.cal.machine.MachineFunction;
import org.openquark.cal.machine.Program;
import org.openquark.cal.machine.StatsGenerator;
import org.openquark.cal.machine.StatsGenerator.StatsObject;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.runtime.CALExecutorException;
import org.openquark.cal.runtime.CalValue;
import org.openquark.cal.runtime.ExecutionContext;
import org.openquark.cal.runtime.ExecutionContextProperties;
import org.openquark.cal.runtime.ResourceAccess;
import org.openquark.cal.util.ArrayStack;
import org.openquark.util.UnsafeCast;


/**
 * The Executor executes instruction streams.
 * Creation date: Jun 18, 2002
 * @author rcypher
 *
 */
public class Executor implements CALExecutor { 
    
    /** State diagnostics. */ 
    // Turn on execution diagnostics.
    static boolean EXEC_DIAG = false;  // At some point we will want to make this a final value.
    static boolean RUNTIME_STATS = System.getProperty (GMachineConfiguration.RUNTIME_STATISTICS_PROP) != null;
    static final boolean CALL_COUNTS = System.getProperty(GMachineConfiguration.CALL_COUNTS_PROP) != null;
    
    // Turn on space use diagnostics.
    static final boolean SPACE_DIAG = false;
    private int maxStackSize = 0;
    private int maxSimultaneousNodes = 0;
    
    /** Executor state **/
    // The program is the repository of defined functions.
    final Program program;
    
    /** Provides access to the resources of the current environment (e.g. from the workspace, or from Eclipse). */
    final ResourceAccess resourceAccess;
    
    // The stack used to represent the program graph.
    GStack stack; 
    
    // The dump stack is used to hold suspended function evaluations.
    private ArrayStack<DumpItem> dump;
    
    // Macrocode
    // Static instruction lists
    private static final Instruction[] is_HaltTrap = {};
    
    // The code itself
    static Code m_HaltTrap = new Code(is_HaltTrap);
    
    private static Code m_Eval = new Code (Instruction.I_Eval);    
    
    // The instruction pointer.  This offsets into the currently executing
    // instruction stream.
    Instruction[] currentCode;
    int currentOffset;
    
    // Metrics
    long iCount = 0;
    
    private int nPushedArguments = 0;
    
    // Output print value.
    String printVal = "";
    
    //boolean running = false;
    
    // A collection of registered statistics generators.
    private List<StatsGenerator> statsGenerators = new ArrayList<StatsGenerator> ();
    
    
    /**
     * Keep track of whether this executor was asked to quit or suspend.   
     * Used by the client to tell the executor to stop prematurely.
     * Must be marked as volatile (or have access to it synchronized) even though read/writes of
     * booleans are guaranteed to be atomic. See Effective Java item 48 pg 191.     
     */    
    private volatile int continueAction = ACTION_CONTINUE;
    private static final int ACTION_CONTINUE = 0;
    private static final int ACTION_QUIT = 1;

    /** Execution context associated with this executor. */
    private ExecutionContextImpl executionContext;
    
    /**
     * Construct Executor from a Program and context.
     * @param program
     * @param resourceAccess
     *            the ResourceAccess instance to provide access to the resources of the current
     *            environment (e.g. from the workspace, or from Eclipse).
     * @param context
     */
    public Executor(Program program, ResourceAccess resourceAccess, ExecutionContext context) {
        super();
        
        stack = new GStack ();
        dump = ArrayStack.make ();
        
        this.program = program;
        
        if (resourceAccess == null) {
            throw new NullPointerException("g.Executor: resourceAccess can't be null.");
        }
        if (context == null) {
            throw new NullPointerException("g.Executor: context can't be null.");
        }
        
        this.resourceAccess = resourceAccess;
        this.executionContext = (GExecutionContext)context;
    }
    
    /** {@inheritDoc} */
    public ExecutionContextImpl getContext () {
        return executionContext;
    }
    
    /**
     * Do the NOOP transition.
     * This is used for diagnostics and instrumentation.
     * @param object Object
     */
    void i_instrument (StatsGenerator.ProfileObj object) {
        if (SPACE_DIAG) {        
            if (object instanceof ExecTimeInfo) {
                ((ExecTimeInfo)object).nodeCount = 0;//Node.nodeOrdCount;
                ((ExecTimeInfo)object).maxSimultaneousNodes = maxSimultaneousNodes;
                ((ExecTimeInfo)object).maxStackSize = maxStackSize;
            }
        }
        
        //System.out.println ("Instrument -> " + object.toString ());
        for (final StatsGenerator gen : statsGenerators) {
            gen.i_instrument(object);
        }
    }
    
    /**
     * Do a switch state transition.
     * SWITCH does the following:
     * - Pop a value from the value stack 
     * - Enter the code for the alternative, indexed by this value in the map
     * @param inst The switch instruction currently being executed.
     * @throws CALExecutorException
     */
    void i_switchJ (Instruction inst) throws CALExecutorException {
        Map<?, ?> jumpMap = inst.getMap();
        try {
            Node n = stack.peek ();
            int tag = n.getOrdinalValue();
            
            Integer jump = (Integer)jumpMap.get (Integer.valueOf(tag));
            if (jump == null) {
                
                //check for the wildcard pattern
                jump = (Integer)jumpMap.get(Expression.Switch.SwitchAlt.WILDCARD_TAG);
                if (jump == null) {
                    
                    StringBuilder sb = new StringBuilder ();
                    String tagString;
                    if (n instanceof NConstr) {
                        tagString = ((NConstr)n).getTag().getName().getQualifiedName();
                    } else {
                        tagString = n.toString();  
                    }
                    
                    sb.append ("Unhandled case for " + tagString + ".");
                    //for example, if f x = case Nothing -> "abc";;
                    //then evaluating
                    //f (Just 'a')
                    //will trigger this pattern match exception
                    
                    throw new CALExecutorException.ExternalException.PatternMatchFailure (((Instruction.I_SwitchJ) inst).getErrorInfo(), sb.toString(), null);
                }
            }
            
            currentOffset += jump.intValue (); 
            
        } catch (ClassCastException e) {
            throw new CALExecutorException.InternalException("i_switch: bad node type on stack.", e);
        }
    }
    
    /**
     * Do the ForeignSCCall state transition.
     * Creation date: (May 3, 2002)
     * @param foreignFunctionInfo ForeignFunctionInfo
     * @throws CALExecutorException
     */
    void i_foreignSCCall(ForeignFunctionInfo foreignFunctionInfo) throws CALExecutorException {

        // NOTE: Generally speaking arguments to foreign functions need to be unboxed before the
        // foreign function is invoked.  However, some foreign functions take arguments of
        // type CalValue.  In this case the value should not be unboxed.
        // When retrieving foreign argument values from the g-machine stack the 
        // functions getForeignArgument() and getForeignArguments() should be used.
        // These will correctly handle unboxing the value.
        
        try {
            
            ForeignFunctionInfo.JavaKind kind = foreignFunctionInfo.getJavaKind();
            
            if (kind.isMethod()) {
                
                final ForeignFunctionInfo.Invocation invocationInfo = (ForeignFunctionInfo.Invocation)foreignFunctionInfo;
                
                Method method = (Method)invocationInfo.getJavaProxy();                    
                
                Object instanceObject = null;
                
                int nJavaArgs = foreignFunctionInfo.getNArguments();
                Object [] args;
                Class<?>[] argTypes = method.getParameterTypes();
                
                if (invocationInfo.getInvocationClass() != method.getDeclaringClass()) {
                    //if package scope class A defines public method f (static or non-static), and public class B extends A,
                    //we want to call B.f and not A.f. There is no way to do this via reflection, so we explicitly disable
                    //Java language access controls in this case.
                    method.setAccessible(true);
                }
                // NOTE: getForeignArguments() uses the argument type information to handle correctly
                // unboxing the arguments to the foreign function.  i.e. it doesn't unbox arguments
                // of type CalObject .
                if (kind.isStatic()){
                    args = getForeignArguments (nJavaArgs, argTypes);
                } else {
                    args = getForeignArguments (nJavaArgs - 1, argTypes);
                    instanceObject = stack.pop ().getValue ();
                }
                
                Object result = method.invoke(instanceObject, args);
                pushForeignResult (method.getReturnType(), result);
                
            } else if (kind.isField()) {
                
                final ForeignFunctionInfo.Invocation invocationInfo = (ForeignFunctionInfo.Invocation)foreignFunctionInfo;
                
                Field field = (Field)invocationInfo.getJavaProxy();
                
                if (invocationInfo.getInvocationClass() != field.getDeclaringClass()) {
                    //if package scope class A defines public field f (static or non-static), and public class B extends A,
                    //we want to call B.f and not A.f. There is no way to do this via reflection, so we explicitly disable
                    //Java language access controls in this case.
                    field.setAccessible(true);
                }                    
                Object result;
                if (kind.isStatic()) {
                    result = field.get(null);
                } else {
                    // NOTE: use getForeignArgument() to correctly handle unboxing.
                    Object instance = getForeignArgument(field.getDeclaringClass());
                    result = field.get(instance);
                }
                
                pushForeignResult (field.getType(), result);
                
            } else if (kind.isConstructor()) {
                // NOTE: As with a call to a foreign method the arguments to a Constructor can be of 
                // type CalValue.  In this case the argument shouldn't be unboxed.  This is handled by
                // getArguments().
                final ForeignFunctionInfo.Invocation invocationInfo = (ForeignFunctionInfo.Invocation)foreignFunctionInfo;
                
                Constructor<?> constructor = (Constructor<?>)invocationInfo.getJavaProxy();
                Class<?>[] argTypes = constructor.getParameterTypes();
                Object result = constructor.newInstance(getForeignArguments(foreignFunctionInfo.getNArguments(), argTypes));
                pushForeignResult (constructor.getDeclaringClass(), result);
                
            } else if (kind.isCast()) {
                
                final ForeignFunctionInfo.Cast castInfo = (ForeignFunctionInfo.Cast)foreignFunctionInfo;
                
                if (kind == ForeignFunctionInfo.JavaKind.IDENTITY_CAST || kind == ForeignFunctionInfo.JavaKind.WIDENING_REFERENCE_CAST) {
                    
                    //do nothing
                    
                } else if (kind == ForeignFunctionInfo.JavaKind.WIDENING_PRIMITIVE_CAST || kind == ForeignFunctionInfo.JavaKind.NARROWING_PRIMITIVE_CAST) {
                                      
                   performJavaPrimitiveCast (castInfo.getJavaReturnType(), getForeignArgument(castInfo.getJavaArgumentType(0)));
                    
                } else if (kind == ForeignFunctionInfo.JavaKind.NARROWING_REFERENCE_CAST) {
                    
                    // We don't want' to pop the value off the stack in this case so
                    // we need to manually do the unboxing normally performed in
                    // getForeignArgument().
                    NValObject nvo = (NValObject)stack.peek();
                    Class<?> castResultType = castInfo.getJavaReturnType();
                    Class<?> argType = castInfo.getJavaArgumentType(0);
                    Object valueToCast;
                    if (argType.equals(CalValue.class)) {
                        valueToCast = nvo;
                    } else {
                        valueToCast = nvo.getValue();
                    }
                    if (!castResultType.isInstance(valueToCast)) {
                        throw new ClassCastException ("Cannot cast type " + nvo.getValue().getClass().getName() + " to type " + castResultType.getName());
                    }
                    
                } else {
                    throw new IllegalStateException("unrecognized cast kind " + kind);
                }
                
            } else if (kind.isInstanceOf()) {
                
                final ForeignFunctionInfo.InstanceOf instanceOfInfo = (ForeignFunctionInfo.InstanceOf)foreignFunctionInfo;
                
                // Make sure to use getForeignArgument() so that correct unboxing takes place.
                final Object object = getForeignArgument(instanceOfInfo.getJavaArgumentType(0));
                final Class<?> instanceOfType = instanceOfInfo.getInstanceOfType();
                stack.pushBoolean(instanceOfType.isInstance(object));
                
            } else if (kind.isNullLiteral()) {
                
                stack.push (new NValObject(null));
             
            } else if (kind.isNullCheck()) {
                
                final ForeignFunctionInfo.NullCheck nullCheckInfo = (ForeignFunctionInfo.NullCheck)foreignFunctionInfo;
                
                // Make sure to use getForeignArgument() so that correct unboxing takes place.
                final Object object = getForeignArgument(nullCheckInfo.getJavaArgumentType(0));
                stack.pushBoolean (nullCheckInfo.checkIsNull() ? object == null : object != null);
                
            } else if (kind.isClassLiteral()) {
                
                final ForeignFunctionInfo.ClassLiteral classLiteral = (ForeignFunctionInfo.ClassLiteral)foreignFunctionInfo;
                
                stack.push(new NValObject(classLiteral.getReferentType()));
                
            } else if (kind == ForeignFunctionInfo.JavaKind.NEW_ARRAY) {
                
                final ForeignFunctionInfo.NewArray newArrayInfo = (ForeignFunctionInfo.NewArray)foreignFunctionInfo;
                final int nSizeArgs = foreignFunctionInfo.getNArguments();
                final Object newArray;
                //we use the more efficient overload of newInstance in the case where only 1 sizing dimension is supplied.
                if (nSizeArgs == 1) {
                    final int size = stack.popInt();
                    newArray = Array.newInstance(newArrayInfo.getComponentType(), size);                    
                } else {
                    final int[] sizes = popIntArgs(nSizeArgs);
                    newArray = Array.newInstance(newArrayInfo.getComponentType(), sizes);                    
                }
                stack.push(new NValObject(newArray));                
                
            } else if (kind == ForeignFunctionInfo.JavaKind.LENGTH_ARRAY) {
                
                //we know that object will be an array. However, it may be an int[] or an Object[] etc.
                // Make sure to use getForeignArgument() so that correct unboxing takes place.
                final Object object = getForeignArgument(foreignFunctionInfo.getJavaArgumentType(0));
                stack.pushInt(Array.getLength(object));
                
            } else if (kind == ForeignFunctionInfo.JavaKind.SUBSCRIPT_ARRAY) {
                
                final int nSubscriptArgs = foreignFunctionInfo.getNArguments() - 1;
                
                final Object result;
                if (nSubscriptArgs == 1) {
                    
                    final int index = stack.popInt();
                    
                    // Make sure to use getForeignArgument() so that correct unboxing takes place.
                    final Object array = getForeignArgument(foreignFunctionInfo.getJavaArgumentType(0));
                    result = Array.get(array, index);                    
                    
                } else {
                    
                    final int[] indices = popIntArgs(nSubscriptArgs);
                    
                    // Make sure to use getForeignArgument() so that correct unboxing takes place.
                    final Object array = getForeignArgument(foreignFunctionInfo.getJavaArgumentType(0));
                    
                    Object subscriptedArray = array;
                    for (int i = 0; i < nSubscriptArgs; ++i) {
                        subscriptedArray = Array.get(subscriptedArray, indices[i]);                        
                    }
                    result = subscriptedArray;
                }
                pushForeignResult(foreignFunctionInfo.getJavaReturnType(), result);
                                              
            } else if (kind == ForeignFunctionInfo.JavaKind.UPDATE_ARRAY) {
                
                final int nSubscriptArgs = foreignFunctionInfo.getNArguments() - 2;
                
                // Make sure to use getForeignArgument() so that correct unboxing takes place.
                final Object element = getForeignArgument(foreignFunctionInfo.getJavaArgumentType(nSubscriptArgs+1));
                
                if (nSubscriptArgs == 1) {
                    int index = stack.popInt();

                    // Since this object will always be an array, and never an instance of CalValue, we can simply pop
                    // the element and call 'getValue()'.
                    Object array = stack.pop().getValue();
                    Array.set(array, index, element);
                } else {
                    int[] indices = popIntArgs(nSubscriptArgs);
                    
                    // Since this object will always be an array, and never an instance of CalValue, we can simply pop
                    // the element and call 'getValue()'.
                    Object array = stack.pop().getValue();
                    Object subscriptedArray = array;
                    for (int i = 0; i < nSubscriptArgs - 1; ++i) {
                        subscriptedArray = Array.get(subscriptedArray, indices[i]);                        
                    }
                    Array.set(subscriptedArray, indices[nSubscriptArgs - 1], element);                    
                }
                                               
                pushForeignResult(void.class, null);                               
                
            } else {
                throw new IllegalStateException("unrecognized kind " + kind);
            }
            
        } catch (java.lang.reflect.InvocationTargetException e) {
            
            Throwable targetException = e.getCause();
            throw new CALExecutorException.ExternalException.ForeignOrPrimitiveFunctionException("The exception " + targetException.getClass().getName() + " occurred while calling " + foreignFunctionInfo.getCalName() + ".", targetException);         
            
        } catch (IllegalAccessException e) {
            
            throw new CALExecutorException.ExternalException.ForeignOrPrimitiveFunctionException("The foreign function " + foreignFunctionInfo.getCalName() + " is not accessible.", e);
            
        } catch (InstantiationException e) {
            
            throw new CALExecutorException.ExternalException.ForeignOrPrimitiveFunctionException("An exception occurred while calling " + foreignFunctionInfo.getCalName() + ".", e);
            
        } catch (ClassCastException e) {
            
            throw new CALExecutorException.ExternalException.ForeignOrPrimitiveFunctionException("An exception occurred while calling " + foreignFunctionInfo.getCalName() + ".", e);
            
        } catch (UnableToResolveForeignEntityException e) {
            
            throw new CALExecutorException.ExternalException.ForeignOrPrimitiveFunctionException("An exception occurred while calling " + foreignFunctionInfo.getCalName() + ".", e);
            
        } catch (Exception e) {
            
            throw new CALExecutorException.ExternalException.ForeignOrPrimitiveFunctionException("An exception occurred while calling " + foreignFunctionInfo.getCalName() + ".", e);
            
        }
    }
    
    /**
     * Handles the various primitive casts allowed by Java as specified in section 2.6 of the JVM spec.
     * Note: it is assumed that this method is not called for identity casts, or other cast types such
     * as reference casts.
     * @param returnType
     * @param valueToCast
     */
    private void performJavaPrimitiveCast (Class<?> returnType, Object valueToCast) {
        
        if (returnType == int.class) {
            
            if (valueToCast instanceof Number) {
                stack.pushInt(((Number)valueToCast).intValue());
            } else {
                stack.pushInt((int)((Character)valueToCast).charValue());
            }           
            
        } else if (returnType == double.class) {
            
            if (valueToCast instanceof Number) {
                stack.pushDouble(((Number)valueToCast).doubleValue());
            } else {
                stack.pushDouble((double)((Character)valueToCast).charValue());
            }              
        } else if (returnType == char.class) {    
            
            stack.pushChar((char)((Number)valueToCast).intValue());
                   
        } else if (returnType == long.class) {
            
            if (valueToCast instanceof Number) {
                stack.pushLong(((Number)valueToCast).longValue());
            } else {
                stack.pushLong((long)((Character)valueToCast).charValue());
            }    
        
        } else if (returnType == byte.class) {
            
            if (valueToCast instanceof Number) {
                stack.pushByte(((Number)valueToCast).byteValue());
            } else {
                stack.pushByte((byte)((Character)valueToCast).charValue());
            }      
            
        } else if (returnType == short.class) {
            
            if (valueToCast instanceof Number) {
                stack.pushShort(((Number)valueToCast).shortValue());
            } else {
                stack.pushShort((short)((Character)valueToCast).charValue());
            }                            
            
        } else if (returnType == float.class) {
            
            if (valueToCast instanceof Number) {
                stack.pushFloat(((Number)valueToCast).floatValue());
            } else {
                stack.pushFloat((float)((Character)valueToCast).charValue());
            }                
        } else {
            
            throw new IllegalArgumentException("unexpected Java primitive cast type");
        }
        
    }
    
    /**
     * Wrap the result of a foreign call in the appropriate node type
     * and push onto stack.
     * @param returnType
     * @param result
     */
    private void pushForeignResult (Class<?> returnType, Object result) {
        if (result == null && (returnType == Void.TYPE || returnType.getName().equals("void"))) {
            // We treat Unit as an enum data type so it is simply the int value of zero.
            stack.push(new NValInt(0));
            return;
        }
        
        if (returnType == boolean.class) {
            stack.pushBoolean(((Boolean)result).booleanValue());           
        } else 
        if (returnType == double.class) {
            stack.pushDouble (((Double)result).doubleValue());
        } else 
        if (returnType == char.class) {
            stack.pushChar (((Character)result).charValue());
        } else 
        if (returnType == int.class) {
            stack.pushInt (((Integer)result).intValue ());
        } else 
        if (returnType == long.class) {
            stack.pushLong (((Long)result).longValue());
        } else 
        if (returnType == short.class) {
            stack.pushShort(((Short)result).shortValue());
        } else 
        if (returnType == byte.class) {
            stack.pushByte (((Byte)result).byteValue());
        } else 
        if (returnType == float.class) {
            stack.pushFloat (((Float)result).floatValue());
        } else 
        if (returnType == CalValue.class) {
            stack.push ((Node)result);
        } else {
            stack.push (new NValObject(result));
        }
    }
    
    /**    
     * Creation date: (June 28, 2002)
     * @param nArgs number of arguments to pop of the value stack
     * @param argTypes
     * @return Object[] popped arguments wrapped in an array     
     */ 
    private Object[] getForeignArguments (int nArgs, Class<?>[] argTypes) {
        // Note: arguments on stack are in reverse to the order that they are
        // expected.
        
        Object[] arguments = new Object [nArgs];
        
        for (int i = 0; i < nArgs; ++i) {
            arguments[nArgs-i-1] = getForeignArgument(argTypes[nArgs-i-1]);
        } 
        
        return arguments;      
    }

    /**
     * Retrieve a foreign argument off the top of the stack.
     * The argType is used to perform any necessary unboxing
     * of the value.
     * @param argType
     * @return the foreign argument value.
     */
    private Object getForeignArgument(Class<?> argType) {
        Node n = stack.pop();
        Object argValue = n;
        if (argType != CalValue.class) {
            if (n instanceof NVal) {
                argValue = n.getValue ();
            }
            
            if (argValue instanceof DataConstructor) {
                if (((DataConstructor)argValue).getName().equals(CAL_Prelude.TypeConstructors.CalValue)) {
                    argValue = n;
                }
            }
        }
        
        return argValue;
    }
      
    /**    
     * @param nArgs
     * @return pops nArgs int args from the stack, putting them into an int array, and in the expected order (i.e. the reverse of
     *    stack order)
     */
    private int[] popIntArgs (int nArgs) {
        // Note: arguments on stack are in reverse to the order that they are
        // expected.

        int[] arguments = new int [nArgs];

        for (int i = 0; i < nArgs; ++i) {                       
            arguments [nArgs - i - 1] = stack.popInt();
        } 

        return arguments;      
    }    
    
    void executeInstruction (Instruction inst) throws CALExecutorException {
        
        switch (inst.getTag()) {
            case Instruction.T_Alloc:
                for (int i = 0; i < inst.getN(); ++i) {
                    stack.push (new NInd ());
                }
            break;
            
            case Instruction.T_Call:
                int n = inst.getN();
                pushDumpItem (n + 2);
                stack.peek ().i_dispatch (this, n);
                break;
            
            case Instruction.T_CondJ:
                if (stack.popBoolean()) {
                    currentOffset += inst.getN();
                }
            break;
            
            case Instruction.T_Dispatch:
                stack.peek().i_dispatch (this, inst.getN());
            break;
            
            case Instruction.T_ForeignFunctionCall:
                i_foreignSCCall((ForeignFunctionInfo)inst.getInfo());
            break;
            
            case Instruction.T_ClearStack:
                stack.clear ();
            break;
            
            case Instruction.T_Eval:
                stack.peek().i_eval(this);
            break;
            
            case Instruction.T_Instrument:
                Object object = ((Instruction.I_Instrument)inst).getInfo();
            if (object instanceof ExecTimeInfo) {
                ((ExecTimeInfo)object).instructionCount = getInstructionCount();
                if (SPACE_DIAG) {        
                    ((ExecTimeInfo)object).nodeCount = 0;//Node.nodeOrdCount;
                    ((ExecTimeInfo)object).maxSimultaneousNodes = maxSimultaneousNodes;
                    ((ExecTimeInfo)object).maxStackSize = maxStackSize;
                }
            }
            //System.out.println ("Instrument -> " + object.toString ());
            for (final StatsGenerator gen : statsGenerators) {
                    gen.i_instrument((StatsGenerator.ProfileObj)object);
                }
            break;
            
            case Instruction.T_PushFalse:
                stack.pushBoolean (false);
                //stack.push (NValBoolean.FALSE);
            break;
            
            case Instruction.T_PushTrue:
                stack.pushBoolean (true);
                //stack.push (NValBoolean.TRUE);
            break;
            
            case Instruction.T_Unwind:
                stack.peek().i_unwind(this);
            break;
            
            case Instruction.T_Jump:
                currentOffset += inst.getN();
            break;
            
            case Instruction.T_MkapN:
                Node n1 = stack.pop ();
            
                for (int i = 0; i < inst.getN(); ++i) {
                    Node n2 = stack.pop ();
                    Node nap = n1.apply (n2);//new NAp (n1, n2);
                    n1 = nap;
                }
                stack.push (n1);
            break;
            
            case Instruction.T_PackCons:
                int arity = inst.getArity();
                int ord = inst.getOrd();
                DataConstructor tag = (DataConstructor)inst.getInfo();
                
                if (CALL_COUNTS) {
                    i_instrument (new Executor.CallCountInfo((tag).getName(), "DataConstructor instance counts"));
                }
                
                Node [] nodes = new Node [arity];
                for (int i = 0; i < arity; ++i) {
                    nodes [i] = stack.pop ().getLeafNode();
                }
                
                Node nc = new NConstrGeneral (tag, ord, nodes);
                stack.push (nc);
            break;
            
            case Instruction.T_PackCons0:
                ord = inst.getOrd();
                tag = (DataConstructor)inst.getInfo();
                if (CALL_COUNTS) {
                    i_instrument (new Executor.CallCountInfo((tag).getName(), "DataConstructor instance counts"));
                }
                
                stack.push (new NConstr0(tag, ord));
            break;
            
            case Instruction.T_PackCons2:
                ord = inst.getOrd();
                tag = (DataConstructor)inst.getInfo();
                if (CALL_COUNTS) {
                    i_instrument (new Executor.CallCountInfo((tag).getName(), "DataConstructor instance counts"));
                }
                
                Node n0 = stack.pop ();
                n1 = stack.pop ();
                
                nc = new NConstr2(tag, ord, n0, n1);
                
                stack.push (nc);
            break;
            
            case Instruction.T_Pop:
                n = inst.getN();
                for (int i = 0; i < n; ++i) {
                    stack.pop ();
                }
            break;
            
            case Instruction.T_PrimOp:
                try {
                    ((PrimOp)inst.getInfo()).perform(this);
                } catch (ClassCastException e1) {
                    throw new CALExecutorException.InternalException ("PRIMOP: Bad argument type on stack for subcode <" + ((PrimOp)inst.getInfo()).getDescription () + ">", e1);
                } catch (java.util.EmptyStackException e1) {
                    throw new CALExecutorException.InternalException("PRIMOP: Too few value arguments on stack for subcode <" + ((PrimOp)inst.getInfo()).getDescription () + ">", e1);
                } catch (java.lang.ArithmeticException e1) {
                    throw new CALExecutorException.ExternalException.ForeignOrPrimitiveFunctionException("PRIMOP: Arithmetic exception for subcode <" + ((PrimOp)inst.getInfo()).getDescription () + ">", e1);
                }                
                break;
                
            case Instruction.T_Push:
                stack.push (stack.get (stack.size () - 1 - inst.getN()));
            break;
            
            case Instruction.T_PushGlobal:
                QualifiedName name = inst.getName();
                try {
                    GMachineFunction cl = (GMachineFunction)program.getCodeLabel(name);
                    if (cl != null) {
                        NGlobal ng = cl.makeNGlobal(executionContext);
                        stack.push (ng);
                    }                
                } catch (Program.ProgramException e) {
                    throw new CALExecutorException.InternalException ("Push Global: " + name + ": ", e);
                }
            break;
            
            case Instruction.T_PushGlobalN:
                stack.push (inst.getGlobalNode());
            break;
            
            case Instruction.T_PushPrimitiveNode:
                stack.push (inst.getNode());
            break;
            
            case Instruction.T_PushList:
                stack.push (inst.getNode());
            break;
            
            case Instruction.T_PushString:
                stack.push (inst.getNode());
            break;
            
            case Instruction.T_PushVVal:
                stack.push (inst.getNode());
            break;
            
            case Instruction.T_SelectDCField: {
                Node dcField = stack.peek().i_selectDCField((DataConstructor)inst.getInfo(), inst.getN(), ((Instruction.I_SelectDCField)inst).getErrorInfo());
                if (dcField != null) {
                    int updatePosition = stack.size () - 1;
                    stack.set(updatePosition, dcField);
                }
            }
            break;
            
            case Instruction.T_LazySelectDCField: {
                // create a lazy dc field selection node
                Node dcExpression = stack.pop ();
                Node lazyDCFieldSelection = new NDataConsFieldSelection(dcExpression, (DataConstructor)inst.getInfo(), inst.getN(), ((Instruction.I_LazySelectDCField)inst).getErrorInfo());
                stack.push (lazyDCFieldSelection);
            }
            break;
            
            case Instruction.T_Slide:
                n = inst.getN();
                Node o = stack.pop ();
                for (int i = 0; i < n; ++i) {
                    stack.pop ();
                }
                
                stack.push (o);
            break;
            
            case Instruction.T_Split:
                stack.peek ().i_split (this, inst.getN());
            break;
            
            case Instruction.T_Squeeze:
                stack.squeeze (inst.getX(), inst.getY());
            break;
            
            case Instruction.T_Switch:
                break;
            
            case Instruction.T_SwitchJ:
                i_switchJ (inst);
            break;
            
            case Instruction.T_Update:
                Node top = stack.pop ();
                int updatePosition = stack.size () - 1 - inst.getN();
                stack.get (updatePosition).setIndirectionNode(top);
                stack.set(updatePosition, top);
            break;
            
            // Create a new record node and push it onto the stack.
            case Instruction.T_CreateRecord:
                Node recordVal = new NRecordValue (inst.getN());
                stack.push (recordVal); 
            break;
            
            // Modify the record on top of the stack.  It is
            // safe to modify the actual node because this instruction
            // only occurs as part of record creation/extension.
            case Instruction.T_PutRecordField:
                String fieldName = (String)inst.getInfo();
                Node fieldVal = stack.pop ();
                NRecordValue record = (NRecordValue)stack.peek();
                record.putValue(fieldName, fieldVal);
            break;
            
            // Replace the record on top of the stack with a copy 
            // that can be modified.
            case Instruction.T_ExtendRecord:
                o = stack.pop();
                record = (NRecordValue)o;
                record = new NRecordValue (record);
                stack.push (record);
            break;
            
            // Replace the record value on top of the stack with
            // the value of a field in the record.
            case Instruction.T_RecordSelection:
                fieldName = (String)inst.getInfo();
                record = (NRecordValue)stack.pop();
                stack.push(record.getValue(fieldName));
            break;
            
            // Create a node representing the
            // application of record selection.
            case Instruction.T_LazyRecordSelection:
                fieldName = (String)inst.getInfo();
                o = stack.pop();
                stack.push (new NRecordSelection(fieldName, o));    
            break;
            
            case Instruction.T_LazyRecordUpdate:
            {
                o = stack.pop();
                record = new NRecordUpdate(o);
                stack.push (record);
                break;
            }
            
            // Create a node representing the application of
            // record extension
            case Instruction.T_LazyRecordExtension:
                o = stack.pop ();
                record = new NRecordExtension (o);
                stack.push (record); 
            break;
            
            // Remove a field value from the record node on top
            // of the stack.  It is safe to modify the actual node
            // as this only happens as part of creating a new record instance.
            case Instruction.T_RemoveRecordField:
            {
                fieldName = (String)inst.getInfo();
                record = (NRecordValue)stack.peek();
                record.removeValue(fieldName);
                break;
            }
            
            case Instruction.T_Println:
                System.out.println(inst.getInfo().toString());
            break;
            
            case Instruction.T_CreateList:
                stack.push (new NValObject (new ArrayList<Object>()));
            break;
            
            case Instruction.T_PutListValue: 
            {
                Node listNode = stack.pop ();
                Object listValue = listNode; 
                if (listNode instanceof NVal) {
                    listValue = listNode.getValue();
                }
                List<Object> fl = UnsafeCast.unsafeCast(stack.peek().getValue());
                fl.add(listValue);
                break;
            }
            
            case Instruction.T_Cast:
            {
                i_Cast((Class<?>)inst.getInfo());
                break;
            }
            
            case Instruction.T_DebugProcessing:
            {
                // If tracing is turned on we want to generate a trace message for 
                // the named function.
                QualifiedName functionName = inst.getName();
                if (executionContext.isDebugProcessingNeeded(functionName.getQualifiedName())) {
                    arity = inst.getArity();
                    Node args[] = new Node[arity];
                    // The arguments are on the top of the stack.
                    for (int i = 0; i < arity; ++i) {
                        args[i] = stack.get(stack.size() - i - 1);
                    }

                    executionContext.debugProcessing(functionName.getQualifiedName(), args);
                }                
                break;
            }
            
            
            default:
                //programming error. Instruction not added to the list above.
                throw new IllegalStateException();
            
        }                
        
        
        
    }
    
    private final void i_Cast (Class<?> castType) {
        NValObject nvo = (NValObject)stack.peek();
        if (!castType.isInstance(nvo.getValue())) {
            throw new ClassCastException ("Cannot cast type " + nvo.getValue().getClass().getName() + " to type " + castType.getName());
        }
    }
    
    /**
     * Execute the program at the IP
     * Creation date: (3/3/00 3:57:46 PM)
     * @param c
     * @throws CALExecutorException
     */
    void exec(Code c) throws CALExecutorException {        
        setIP (c);
        
        // preallocate exception for VM errors, since in this case, we may not be able to allocate more memory.
        CALExecutorException.InternalException.VMException vmException = CALExecutorException.InternalException.VMException.makeInitial();           
        
        // Continue a fetch-execute cycle until halted by an exception
        try {
            for (;;) {
                if (continueAction != ACTION_CONTINUE) {
                    if (continueAction == ACTION_QUIT) {
                        throw (new CALExecutorException.ExternalException.TerminatedByClientException("Evaluation halted by client.", null));
                    }
                }
                if (currentOffset >= currentCode.length) {
                    setIP(m_HaltTrap);
                    return;
                }
                
                Instruction inst = currentCode [currentOffset++];
                executeInstruction (inst);
                
                iCount++;
                
                if (EXEC_DIAG) {
                    showState ();
                }
                
                if (SPACE_DIAG) {
                    calcSpaceUsage ();
                }
                
            }
            
        } catch (NullPointerException e) {
            // Illegal 'address' (i.e. ip was null)
            throw new CALExecutorException.InternalException("Illegal address trap: IP was 0", e);
            
        } catch (CALExecutorException e) {
            throw e;
            
        } catch (Error e) {
            // Assume that something really bad has happened: reset the state of the
            // machine and force a finalization/gc.
            reset ();
            for (int i = 0; i < 2; ++i) {
                //System.out.println ("trying to free memory " + i);
                System.runFinalization();
                System.gc();
            }
            
            vmException.initCause(e);
            throw vmException;
            
        } catch (Exception e) {
            throw new CALExecutorException.InternalException("An Exception was encountered: ", e);
        }        
    }
    
    /**
     * Run the function specified by the EntryPoint using the given Java arguments.
     * @param entryPoint
     * @param arguments (null is accepted and used as the 0-length Object array).
     * @return the resulting value
     * @throws CALExecutorException
     */
    public Object exec(EntryPoint entryPoint, Object[] arguments) throws CALExecutorException {
        reset ();
        if (entryPoint == null) {
            throw new CALExecutorException.InternalException ("null EntryPoint.", null);
        }
        
        final QualifiedName entryPointSCName = ((EntryPointImpl)entryPoint).getFunctionName(); 
                   
        if (arguments != null) {
            nPushedArguments = arguments.length;
            for (int i = nPushedArguments - 1; i >= 0; --i) {
                stack.push (new NValObject (arguments[i]));
            }
        } else {
            nPushedArguments = 0;
        }
        
        Code c = setupStartPoint2 (entryPointSCName);
        
        exec (c);      
        
        // Clear the program field of the execution context.  This prevents
        // an out-of date instance of Program from being held.
        ((GExecutionContext)executionContext).clearProgram();
        
        return stack.peek ().getValue ();
    }
    
    /**
     * Set the machine's state to start executing the named SC assuming that the pushed arguments are on the stack.
     * @param entryPointSCName
     * @return Code
     * @throws CALExecutorException
     */
    Code setupStartPoint2 (QualifiedName entryPointSCName) throws CALExecutorException {
        
        try {
              GProgram.GModule startModule = (GProgram.GModule)program.getModule(entryPointSCName.getModuleName());
               MachineFunction mf = startModule.getFunction(entryPointSCName);
               // The specified target may be an alias of another function, or it may be defined as a literal value 
               // (either directly or from following an alias chain).  In either case there is no source/class
               // generated.  So we need to either return the literal value or follow the alias to a
               // function for which there is a generated source/class file.
               if (mf.getLiteralValue() != null) {
                   Instruction[] enterInst = new Instruction [4];
                   enterInst [0] = new Instruction.I_Instrument (new ExecTimeInfo(true, getInstructionCount()));
                   enterInst [1] = Instruction.I_PushVVal.makePushVVal(mf.getLiteralValue());
                   enterInst [2] = Instruction.I_Eval;
                   enterInst [3] = new Instruction.I_Instrument (new ExecTimeInfo(false, getInstructionCount()));
                   
                   Code enterCode = new Code (enterInst);
                   
                   return (enterCode);
               }
               
               if (mf.getAliasOf() != null) {
                   entryPointSCName = mf.getAliasOf();
               }
            
            if (nPushedArguments != 0) {
                Instruction[] enterInst = new Instruction [nPushedArguments + 3];
                enterInst [0] = new Instruction.I_PushGlobal (entryPointSCName);
                for (int i = 1; i <= nPushedArguments; i++) {
                    enterInst [i] = new Instruction.I_MkapN (1);
                }
                
                enterInst [nPushedArguments + 1] =  Instruction.I_Eval;
                enterInst [nPushedArguments + 2] =  new Instruction.I_Instrument (new ExecTimeInfo (false, getInstructionCount()));
                
                Instruction iStartInst[] = new Instruction [1];
                iStartInst [0] = new Instruction.I_Instrument (new ExecTimeInfo (true, getInstructionCount()));
                InstructionList il = new InstructionList (iStartInst);
                il.code (enterInst);
                
                Code istart = new Code (il);
                
                return (istart);
                
            }   
            // No supplied args.  We should be executing a CAF.
            // Check to be sure and throw an error if necessary.
            GMachineFunction cl = (GMachineFunction)program.getCodeLabel(entryPointSCName);
            NGlobal ng = cl.makeNGlobal(executionContext);
            int arity = ng.getArity();
            if (arity != 0) {
                throw new CALExecutorException.InternalException("Expecting arguments but didn't get any.", null);
            }
            
            Instruction[] enterInst = new Instruction [4];
            enterInst [0] = new Instruction.I_Instrument (new ExecTimeInfo(true, getInstructionCount()));
            enterInst [1] = new Instruction.I_PushGlobal (entryPointSCName);
            enterInst [2] = Instruction.I_Eval;
            enterInst [3] = new Instruction.I_Instrument (new ExecTimeInfo(false, getInstructionCount()));
            
            Code enterCode = new Code (enterInst);
            
            return (enterCode);
            
        } catch (Program.ProgramException e) {
            throw new CALExecutorException.InternalException("Unable to fetch Global '" + entryPointSCName + "': ", e);
        }
        
    }
    
    /**
     * Reset the executor
     * This will perform a warm-restart, clearing out all critical runtime states, 
     * such that the runtime can be reused without interference of the previous
     * session in the subsequent session.
     * NOTE: The GlobalFrame and Program are not cleared.  Reset is designed to allow
     * a Program to be re-executed in various ways.  During execution the Program code
     * becomes 'attuned' to the developing GlobalFrame by code rewriting optimizations
     * and this makes it necessary to preserve this too.
     * Creation date: (4/6/01 8:35:37 PM)
     */
    void reset() {
        continueAction = ACTION_CONTINUE;
        
        stack = new GStack ();
        
        dump = ArrayStack.make ();
        
        // Reset the IP to the start
        setIP (m_HaltTrap);
        
        iCount = 0;
        
        maxStackSize = 0;
        
        ((GExecutionContext)executionContext).reset ((GProgram)program, resourceAccess);
    }
    
    
    /**
     * Return the number of instructions run since the count was last reset.
     * Creation date: (4/4/00 6:39:54 PM)
     * @return long the number of instructions
     */
    long getInstructionCount() {
        return iCount;
    }
    /**
     * Ask the runtime to quit.
     * Note that you can only ask the runtime to quit; you can't un-quit - we may want to have something like this later
     * if we want to implement "restart."
     * Creation date: (3/21/02 1:39:40 PM)
     */
    public void requestQuit() {
        continueAction = ACTION_QUIT;
    }
    
    public void requestSuspend () {
        executionContext.setStepAllThreads(true);
        executionContext.setStepping(true);
    }

    /**
     * Set the regular expression that the tracing code uses to filter the traces.
     * 
     * @param patterns The list of regular expressions to use when filtering traces.
     */
    
    public void setTraceFilters(List<Pattern> patterns){
        executionContext.setTraceFilters(patterns);
    }

    /**
     * showState() 
     * Sends a description of the g-machines internal state to
     * the message dispatcher.
     * Original Author: rcypher
     */
    void showState () {
        StringBuilder sb = new StringBuilder ();
        sb.append(iCount + ")   " + printVal + "\n");
        sb.append ("Stack [\n");
        sb.append (showStack (7));
        sb.append ("          ]\n");
        sb.append (showDump (7) + "\n");
        sb.append ("Code:\n");
        
        for (int i = currentOffset; i < currentCode.length; ++i) {
            sb.append("    ");
            sb.append(currentCode[i].toString());
            sb.append ("\n");
        }
        
        //System.out.println(sb.toString());
        CodeGenerator.MACHINE_LOGGER.log(Level.FINE, sb.toString());
    }
    
    /**
     * showStack()
     * Creates a string showing the state of the stack.
     * the message dispatcher.
     * 
     * Original Author: rcypher
     * @param indent int The number of spaces to indent the stack description.
     * @return A string containing the stack description.
     */
    String showStack (int indent) {
        StringBuilder sb = new StringBuilder ();
        sb.append ("stack size = " + stack.size () + "\n");
        for (int i = 0; i < stack.size (); ++i) {
            Node n = stack.get (i);
            sb.append (n.toString (indent + 4) + "\n");
        }
        return sb.toString ();
    }
    
    /**
     * showDump()
     * Create a string containing a description of the dump stack.
     * Original Author: rcypher
     * @param indent int The number of spaces to indent the dump stack description.
     * @return A string containing the dump stack description.
     */
    String showDump (int indent) {
        StringBuilder sb = new StringBuilder ();
        
        sb.append("Dump(" + dump.size() +"): \n");
        for (int i = dump.size () - 1; i >= 0; --i) {
            DumpItem di = dump.get (i);
            sb.append (di.toString (indent));
            sb.append ("\n");
        }
        
        return sb.toString ();
    }
          
    
    /**
     * Register a stats generator.
     * @param gen
     */
    public void addStatsGenerator (StatsGenerator gen) {
        if (!statsGenerators.contains (gen)) {
            statsGenerators.add (gen);
        }
    }
    
    /**
     * Remove a stats generator.
     * @param gen
     */
    public void removeStatsGenerator (StatsGenerator gen) {
        statsGenerators.remove (gen);
    }
    
    
    public static void setExecDiag (boolean b) {
        EXEC_DIAG = b;
    }
    
    void calcSpaceUsage () {
        if (iCount < 1000 || (iCount % 10000) == 0) {
            // Determine the number of existing nodes.
            List<Node> v = new ArrayList<Node> ();
            for (int j = 0; j < stack.getFullStack().size (); ++j) {
                Node n = stack.getFullStack().get (j);
                if (!v.contains (n)) {
                    v.add (n);
                    n.addChildren (v);
                }
            }
            
            if (v.size () > maxSimultaneousNodes) {
                maxSimultaneousNodes = v.size ();
                //System.out.println ("max stack size = " + maxStackSize);
            }
        }
        
        if (stack.stack.size() > maxStackSize) {
            maxStackSize = stack.stack.size ();
        }
    }
    
    
    /**
     * Pop an item off of the dump stack, while
     * preserving the top item on the current stack. 
     */
    void popDumpItem () {
        DumpItem di = dump.pop ();
        Node o = stack.pop ();
        
        stack.unSegment(di.stackBottom);
        stack.push (o);
        
        setIP (di.getCode(), di.getOffset());
    }
    
    /**
     * Pop an item off the dump stack, while
     * preserving the bottom item of the 
     * current stack.
     */
    void popDumpItemBottom () {
        DumpItem di = dump.pop ();
        Node o = stack.get (0);
        
        stack.unSegment(di.stackBottom);
        stack.push (o);
        
        setIP (di.getCode(), di.getOffset());
    }
    
    /**
     * Push a dump item and start a new stack with the
     * top of the existing stack.
     */
    void pushDumpItem () {    
        dump.push(new DumpItem(stack.segmentOffset(1), currentCode, currentOffset));
    }
    
    void pushDumpItem (int n) {    
        dump.push(new DumpItem(stack.segmentOffset(n), currentCode, currentOffset));
    }
    
    int getDumpSize () {
        return dump.size ();
    }
    
    void setIP (Instruction[] code, int offset) {
        currentCode = code;
        currentOffset = offset;
    }
    
    void setIP (Code code) {
        currentCode = code.getInstructions();
        currentOffset = 0;
    }
    
    /*
     * Note: This function is public so that classes in the g.functions package can use it.
     * It should not be used directly by client code!
     */
    public final Node internalEvaluate (Node n) throws CALExecutorException {
        // Create a new Executor instance using the same program and execution context.
        Executor executor = new Executor(this.program, resourceAccess, this.executionContext);
        
        // Push the node to be evaluated onto the stack and start execution.
        executor.stack.push(n);
        executor.exec(m_Eval);
        Node result = executor.stack.peek();
        
        return result.getLeafNode();
    }

    
    /**
     * @return the Program associated with this Executor instance
     */
    public Program getProgram () {return program;}

    static final class DumpItem {
        private final Instruction[] code;
        private final int offset;
        final int stackBottom;
        
        DumpItem (int stackBottom, Instruction[] code, int offset) {
            this.stackBottom = stackBottom;
            this.code = code;
            this.offset = offset;
        }
        
        public final int getStackBottom () {
            return stackBottom;
        }
        
        public final int getOffset () {
            return offset;
        }
        
        public final Instruction[] getCode () {
            return code;
        }
        
        @Override
        public final String toString () {
            return toString (0);
        }
        
        public final String toString (int indent) {
            StringBuilder sp = new StringBuilder ();
            for (int i = 0; i < indent; ++i) {
                sp.append (" ");
            }
            StringBuilder sb = new StringBuilder ();
            sb.append (sp.toString ());
            sb.append ("c: ");
            //            Offset no = ip.newOffset(ip.getIP());
            //            for (int i = 0; i < 3; ++i) {
            //                try {
            //                    Instruction in = (Instruction)no.fetch ();
            //                    sb.append (in.toString ().substring(0, 20));
            //                } catch (Exception e) {
            //                    break;
            //                }
            //            }
            sb.append (" s: ");
            //            for (int i = 0; i < stack.size (); i++) {
            //                sb.append (" #" + ((Node)stack.get (i)).ord);
            //            }
            return sb.toString ();
        }
    }
    
    static final class MArrayStack extends ArrayStack<Node> {
        
        private static final long serialVersionUID = -2647651286807650427L;

        void rr (int start, int end) {
            removeRange (start, end);
        }
    }
    
    static final class GStack {
        
        private int bottom = 0;
        MArrayStack stack = new MArrayStack ();
        
        public Node pop () throws EmptyStackException {
            if (stack.size () <= bottom) {
                throw new EmptyStackException ();
            }
            
            return stack.pop ();
        }        
        public boolean popBoolean() throws EmptyStackException {
            return ((NValBoolean)pop()).getBooleanValue();
        }        
        public char popChar() throws EmptyStackException {
            return ((NValChar)pop()).getCharValue();
        }        
        public double popDouble() throws EmptyStackException {
            return ((NValDouble)pop()).getDoubleValue();
        }               
        public int popInt() throws EmptyStackException {
            return ((NValInt)pop()).getIntValue();
        }
        public long popLong() throws EmptyStackException {
            return ((NValLong)pop()).getLongValue();
        }
        public short popShort() throws EmptyStackException {
            return ((NValShort)pop()).getShortValue();
        }
        public byte popByte() throws EmptyStackException {
            return ((NValByte)pop()).getByteValue();
        }
        public float popFloat() throws EmptyStackException {
            return ((NValFloat)pop()).getFloatValue();
        }
        
        public String popString () throws EmptyStackException {
            return (String)((NValObject)pop()).getValue();                
        }
        
        public Node peek () throws EmptyStackException {
            if (stack.size () <= bottom) {
                throw new EmptyStackException ();
            }
            return stack.peek ();
        }
        
        public int size () {
            return (stack.size () - bottom);
        }
        
        public boolean empty () {
            return stack.size() <= bottom;
        }
        
        public int segment () {
            int b = bottom;
            bottom = stack.size ();
            return b;
        }
        
        public int segmentOffset (int offset) {
            int b = bottom;
            bottom = stack.size() - offset;
            return b;
        }
        
        public int getBottom () {
            return bottom;
        }
        
        public Node get (int index) throws ArrayIndexOutOfBoundsException {
            return stack.get (index + bottom);
        }
        
        public void add (int index, Node obj) {
            stack.add (index + bottom, obj);
        }
        
        public void push (Node item) {
            stack.push (item.getLeafNode());
        }
        
        public void pushBoolean(boolean b) {
            push (new NValBoolean (b));
        }
        public void pushChar(char c) {
            push (new NValChar(c));
        }
        public void pushDouble(double d) {
            push (new NValDouble(d));
        }       
        public void pushInt(int i) {
            push (new NValInt(i));
        }
        public void pushLong(long d) {
            push (new NValLong(d));
        }
        public void pushByte(byte b) {
            push (new NValByte(b));
        }
        public void pushShort(short s) {
            push (new NValShort(s));
        }
        public void pushFloat(float f) {
            push (new NValFloat(f));
        }
        public void pushString(String s) {
            push (new NValObject(s));
        }
        
        public void clear () {
            for (int n = stack.size() - bottom; n > 0; --n) {
                stack.pop();
            }
            //this pattern doesn't seem to help, but it is cute.
            //stack.subList(bottom, stack.size()).clear();            
        }
        
        public boolean isEmpty () {
            return stack.size () <= bottom;
        }
        
        public Node set (int index, Node element) throws ArrayIndexOutOfBoundsException {
            return stack.set (index + bottom, element);
        }
        
        public void unSegment (int n) {
            clear ();
            bottom = n;
        }
        
        MArrayStack getFullStack() {
            return stack;
        }
        
        public void remove (int i) {
            stack.remove (i+bottom);
        }
        
        public void squeeze (int x, int y) {
            int start = stack.size() - (x + y);
            int end = start + y;
            stack.rr (start, end);            
        }
    }
    
    /**
     * Class used to communicate execution time info to the StatsGenerator.
     * 
     * @author rcypher
     */
    static class ExecTimeInfo implements StatsGenerator.ProfileObj {
        /** True if this is the start of execution. */
        private boolean start;

        /** Instruction count for this event. */
        long instructionCount;
        
        /** Maximum stack size for this event. */
        private long maxStackSize;
        
        /** The largest number of program nodes in simultaneous existence. */
        private long maxSimultaneousNodes;
        
        /** The total number of nodes created. */
        private long nodeCount;
        
        ExecTimeInfo (boolean start, long instructionCount) {
            this.start = start;
            this.instructionCount = instructionCount;
        }
        
        /**
         * If the given StatsObject corresponds to this instance 
         * update it.
         * @param stats
         * @return the updated StatsObject, null if it didn't match.
         */
        public StatsObject updateStats (StatsObject stats) {
            
            if (stats == null) {
                // Simply create a new one to hold the info
                stats = new ExecTimeStats ();
            }
            
            if (!(stats instanceof ExecTimeStats)) {
                // Didn't match return null.
                return null;
            }
            
            //Update the info in the StatsObject with the information in
            // this instance.
            ExecTimeStats mStats = (ExecTimeStats)stats;
            
            if (mStats.maxStackSize < maxStackSize) {
                mStats.maxStackSize = maxStackSize;
            }
            
            if (mStats.maxSimultaneousNodes < maxSimultaneousNodes) {
                mStats.maxSimultaneousNodes = maxSimultaneousNodes;
            }
            
            if (start) {
                mStats.executionStart = System.currentTimeMillis();
                mStats.executionStartInstructionCount = instructionCount;
                mStats.execStarted = true;
            } else {
                if (!mStats.execStarted) {
                    if (mStats.execTimes.isEmpty()) {
                        return mStats;
                    }
                    mStats.execTimes.remove(mStats.execTimes.size() - 1);
                    mStats.execCounts.remove(mStats.execCounts.size() - 1);
                }
                
                mStats.execTimes.add (new Long (System.currentTimeMillis() - mStats.executionStart));
                
                mStats.execCounts.add (new Long (instructionCount - mStats.executionStartInstructionCount));
                
                mStats.execStarted = false;
            }
            
            if (mStats.executionNodes == -1) {
                mStats.executionNodesStart = nodeCount;
            }
            
            mStats.executionNodes = nodeCount - mStats.executionNodesStart;
            
            return stats;
        }
        
        /**
         * Class used by the StatsGenerator to accumulate execution performance information.
         * 
         * @author rcypher
         */
        class ExecTimeStats extends StatsGenerator.PerformanceStatsObject {
            /** The total execution time in ms. */
            long executionTime = -1;
            
            /** The time at which execution started, in ms. */
            long executionStart = 0;
            
            /** The number of instructions in the execution. */
            long executionInstructionCount = -1;
            
            /** The number of instructions at the beginning of execution. */
            long executionStartInstructionCount = 0;
            
            /** Vector to hold individual contributions to the time. */
            final List<Long> execTimes = new ArrayList<Long> ();
            
            /** Vector to hold individual contributions to the instruction count. */
            final List<Long> execCounts = new ArrayList<Long> ();
            
            // These members are only set if space use diagnostics are enabled in
            // the executor.
            /** Maximum stack size while executing. */
            long maxStackSize = 0;
            
            /** Maximum number of simultaneously existing program nodes. */
            long maxSimultaneousNodes = 0;
            
            /** Number of nodes allocated while executing. */ 
            long executionNodes = -1;
            
            /** The number of nodes already allocated at the start of exection. */
            long executionNodesStart = 0;
            
            /** True if execution has started. */    
            boolean execStarted = false;
            
            /**
             * Generate a short message describing the information in this object.
             * @return the description
             */
            public String generateShortMessage() {
                DecimalFormat numFormat = new DecimalFormat("###,###.###");
                
                StringBuilder sb = new StringBuilder ();
                
                if (Executor.RUNTIME_STATS) {
                    sb.append ("    " + numFormat.format(getRunInstructionCount()) + " instructions in " + numFormat.format(getRunTime()) + "ms, fips = " + numFormat.format(getRunFIPS()));
                } else {
                    sb.append ("Time = " + numFormat.format(getRunTime()) + "ms.");
                }
                return sb.toString ();
            }
            
            /**
             * Generate a long description of the information in this object.
             * @return the description
             */
            public String generateLongMessage() {
                DecimalFormat numFormat = new DecimalFormat("###,###.###");
                StringBuilder sb = new StringBuilder ();
                
                if (Executor.RUNTIME_STATS) {
                    sb.append ("\n" + numFormat.format(getRunInstructionCount()) + " instructions in " +  numFormat.format(getRunTime()) + "ms, fips = " +  numFormat.format(getRunFIPS()));
                    sb.append ("\n");
                } else {
                    sb.append ("Time = " + numFormat.format(getRunTime()) + "ms.");
                }
                return sb.toString();
            }
            
            /**
             * Summarize a list of ExecTimeStats into a single SummarizedExecTimes object.
             * @param v
             * @return the summarized info
             * {@inheritDoc}
             */
            public StatsObject summarize (List<StatsObject> v) {
                
                // If there are more than 3 runs we will discard the first run
                // before calculating average, standard deviation, etc.
                int nRuns = v.size();
                int nstart = (nRuns >= 3) ? 1 : 0;
                int nend = nRuns;
                int ncount = nend - nstart;
                
                long allExecTimes[] = new long [nRuns];
                long allExecFips[] = new long [nRuns];
               
                {
                    int i = 0;
                    for (final StatsObject statsObject : v) {
                        ExecTimeStats stats = (ExecTimeStats) statsObject;
                        allExecTimes [i] = stats.getRunTime();
                        allExecFips [i] = stats.getRunFIPS();
                        i++;
                    }
                }
                
                long firstExecTime = allExecTimes [0];
                long firstExecFips = allExecFips [0];
                
                SummarizedExecTimes ptr = new SummarizedExecTimes ();
                ptr.allExecTimes = new long[allExecTimes.length];
                System.arraycopy(allExecTimes, 0, ptr.allExecTimes, 0, allExecTimes.length);
                              
                long avgExecTime = 0;
                long avgExecFips = 0;
                
                for (int i = nstart; i < nend; ++i) {
                    avgExecTime += allExecTimes [i];
                    avgExecFips += allExecFips [i];
                    
                }
                
                avgExecTime /= (ncount);
                avgExecFips /= (ncount);
                
                // calculate standard deviation.
                double stDev = 0.0;
                long avg = 0;
                for (int i = nstart; i < nend; ++i) {
                    avg += allExecTimes [i];
                }
                
                avg /= ncount;
                
                long summ = 0;
                for (int i = nstart; i < nend; ++i) {
                    long diff = allExecTimes [i] - avg;
                    summ += (diff * diff);
                }
                
                if (ncount > 1) {
                    stDev = ((double)summ) / ((double)(ncount-1));
                    stDev = Math.sqrt(stDev);
                } else {
                    stDev = 0.0;
                }
                stDev = ((int)(stDev * 100.0)) / 100.0;
                double stdDevPercent = (stDev / avgExecTime) * 100.0;
                stdDevPercent = ((int)(stdDevPercent * 100.0)) / 100.0;
                
                //calculate the standard error:
                double standardError = stDev / Math.sqrt(ncount);                
                
                ptr.frRunTime = firstExecTime;
                ptr.frRunFips = firstExecFips;
                ptr.avgRunTime = avgExecTime;
                ptr.avgRunFips = avgExecFips;
                ptr.runInstructions = ((ExecTimeStats)v.get(0)).getRunInstructionCount();
                ptr.stdDev = stDev;
                ptr.stdDevPercent = stdDevPercent;
                ptr.standardError = standardError;
                
                return ptr;
            }
            
            /**
             * Return the execution time (i.e. time spent running before reaching initial WHNF).
             * @return the run time
             */
            public long getRunTime () {
                
                if (executionTime == -1) {
                    executionTime = 0;
                    for (final Long l : execTimes) {
                        executionTime += l.longValue();
                    }
                }
                
                return executionTime;
            }
            
            /**
             * Return the number of instructions executed while running.
             * @return the instruction count
             */
            public long getRunInstructionCount () {
                if (executionInstructionCount == -1) {
                    executionInstructionCount = 0;
                    for (final Long l : execCounts) {
                        executionInstructionCount += l.longValue();
                    }
                }
                return executionInstructionCount;
            }
            
            /**
             * @return long the run fips (functional instructions per second) value.
             */
            public long getRunFIPS () {
                return (getRunTime() == 0) ? -1 : (getRunInstructionCount() * 1000) / getRunTime();
            }
            
            /** 
             * @return the run time
             */
            @Override
            public long getAverageRunTime() {
                return getRunTime();
            }
            
            @Override
            public long getMedianRunTime() {
                return getRunTime();
            }
            @Override
            public double getStdDeviation() {
                return 0;
            }
            @Override
            public int getNRuns() {
                return 1;
            }
            @Override
            public long[] getRunTimes() {
                return new long[]{getRunTime()};
            }
            
        }
        
        /**
         * Class used to hold the summarization of a set of run times.
         * 
         * @author rcypher
         */
        static class SummarizedExecTimes extends StatsGenerator.PerformanceStatsObject {
            /** Time of first run. */
            long frRunTime;
            
            /** Instructions per second of first run. */
            long frRunFips;
            
            /** Average run time. */
            long avgRunTime;
            
            /** Average instructions per second. */
            long avgRunFips;
            
            /** Number of instructions in a single run. */
            long runInstructions;
            
            /** Standard deviation of the run times. */
            double stdDev;    

            /** Standard deviation of the run times as a percentage of the average run time. */
            double stdDevPercent;
            
            /** Standard error of the mean. */
            double standardError;
            
            /** The run times for all runs. */
            long allExecTimes[];
            
            /** 
             * Generate a short message describing the summarized run times. 
             * @return the description.
             */
            public String generateShortMessage() {
                DecimalFormat numFormat = new DecimalFormat("###,###.###");
                StringBuilder sb = new StringBuilder ();
                
                if (Executor.RUNTIME_STATS) {
                    String s = makeDisplayLine ("Average time = ", avgRunTime, runInstructions, avgRunFips);
                    sb.append (s + "\n");
                    sb.append ("standard deviation of runs = " + numFormat.format(stdDev) + "ms or " + numFormat.format(stdDevPercent) + "% of average\n");
                } else {
                    sb.append ("Average time = " + numFormat.format(avgRunTime) + " ms.");
                }
                return sb.toString();
            }

            /**
             * Generate a long message describing the summarized run times.
             * 
             * @return the description.
             */
            public String generateLongMessage() {
                DecimalFormat numFormat = new DecimalFormat("###,###.###");
                
                StringBuilder sb = new StringBuilder ();
                if (allExecTimes.length >= 3) {
                    sb.append("Individual runs: (first run discarded)\n");
                    
                    for (int i = 0; i < allExecTimes.length; ++i) {
                        sb.append ("run " + i + ":\t" + numFormat.format(allExecTimes[i]));
                        if (i == 0) {
                            sb.append(" (discarded)\n");
                        } else {
                            sb.append('\n');
                        }
                    }     
                    
                } else {
                    sb.append("Individual runs: \n");
                    for (int i = 0; i < allExecTimes.length; ++i) {
                        sb.append ("run " + i + ":\t" + numFormat.format(allExecTimes[i]) + "\n");
                    }
                }
                
                sb.append("First Run:\n");
                String s = makeDisplayLine ("Execution Time = ", frRunTime, runInstructions, frRunFips);
                sb.append(s + "\n");
                
                
                sb.append("Summary:\n");
                s = makeDisplayLine ("Average time = ", avgRunTime, runInstructions, avgRunFips);
                sb.append(s + "\n");
                sb.append("Standard deviation of runs = " + numFormat.format(stdDev) + "ms or " + numFormat.format(stdDevPercent) + "% of average\n");
                sb.append("Standard error of mean     = " + numFormat.format(standardError) + "ms or " + numFormat.format(standardError/avgRunTime*100) + "% of average\n");
                sb.append("Minimum run time time      = " + numFormat.format(getMinTime()) + "ms\n");

                return sb.toString();
            }
            
            public long getRunTime () {
                return avgRunTime;
            }
            
            /** {@inheritDoc} */
            public StatsObject summarize (List<StatsObject> runs) {
                // There is no way to summarize summarized objects.
                return null;
            }
            
            private String makeDisplayLine (String start, long time, long instructions, long fips) {
                DecimalFormat numFormat = new DecimalFormat("###,###.###");
                
                StringBuilder sb = new StringBuilder ();
                sb.append (start);
                sb.append (padString (6, numFormat.format(time)));
                sb.append ("ms  Instructions = ");
                sb.append (padString (6, numFormat.format(instructions)));
                sb.append ("  Fips = ");
                sb.append (padString (6, numFormat.format(fips)));
                return sb.toString ();
            }
            
            private String padString (int n, String s) {
                StringBuilder sb = new StringBuilder ();
                for (int i = 0; i < (n - s.length ()); ++i) {
                    sb.append (" ");
                }
                sb.append (s);
                return sb.toString ();
            }
            
            @Override
            public long getAverageRunTime() {
                return avgRunTime;
            }
            @Override
            public long getMedianRunTime() {
                if (allExecTimes.length == 1) {
                    return allExecTimes[0];
                } 

                long times[] = new long[allExecTimes.length];
                System.arraycopy(allExecTimes, 0, times, 0, times.length);
                Arrays.sort(times);
                if ((times.length % 2) == 0) {
                    return (times[times.length/2] + times[(times.length/2)-1]) / 2;
                } 
                
                return times[(times.length/2)];
            }
            
            @Override
            public double getStdDeviation() {
                return stdDev;
            }
            
            /**
             * @return the standard error of the mean
             */
            public double getStandardError() {
                return standardError;
            }                   
            
            /**
             * @return the number of runs used to calculate the average run time and standard deviation 
             */
            @Override
            public int getNRuns() {
                return allExecTimes.length >= 3 ? allExecTimes.length - 1 : allExecTimes.length; 
            }
            
            /**
             * Return the times for all runs in the order the runs occurred.
             * Note: there may be more values than indicated by getNRuns()
             * because the fastest/slowest times are discarded when calculating
             * average time and standard deviations.
             * @return the individual run times.
             */
            @Override
            public long[] getRunTimes() {
                long times[] = new long[allExecTimes.length];
                System.arraycopy(allExecTimes, 0, times, 0, times.length);
                return times;
            }
            
        }
        
    }
    
    /**
     * A ProfileObj used to communicate information about the number of calls
     * to various CAL entities to the StatsGenerator.
     * 
     * @author rcypher
     */
    static class CallCountInfo implements StatsGenerator.ProfileObj {
        /** Qualified name of the CAL entity which has been called. */
        private QualifiedName name;
        
        /** A description of the type of counts being recorded. e.g. supercombinator, data constructor, etc. */
        private String type;
        
        CallCountInfo (QualifiedName name, String type) {
            this.name = name;
            this.type = type;
        }

        /**
         * @return the qualified name of the entity we are counting 
         */
        QualifiedName getName() {
            return name;
        }
        
        /**
         * @return the description of the type of  count.  e.g. supercombinator, data constructor, etc. 
         */
        String getType() {
            return type;
        }
        
        /**
         * If the given StatsObject is of the corresponding type
         * update it with the information in this object.
         * @param stats - the StatsObject to update.
         * @return the updated StatsObject, null if it didn't match this object.
         */
        public StatsObject updateStats (StatsObject stats) {
            
            /*
             * If the StatsObject is null we simply create a new one
             * which will hold the information.
             */
            if (stats == null) {
                stats = new CallCountStats (type);
            }
            
            // If the StatsObject is the wrong type return null
            if (!(stats instanceof CallCountStats)) {
                return null;
            }
            
            // Update the StatsObject with this count. 
            CallCountStats ccStats = (CallCountStats)stats;
            if (!ccStats.type.equals(type)) {
                return null;
            }
            
            Map<QualifiedName, CallCount> counts = ccStats.getCounts();
            
            CallCount count = counts.get (name);
            if (count == null) {
                count = new CallCount (name, 1);
                counts.put (name, count);
            } else {
                count.count += 1;
            }
            
            return stats;
        }
        
        /**
         * A StatsObject class used to accumulate call count information.
         * 
         * @author rcypher
         */
        static class CallCountStats implements StatsGenerator.StatsObject {
            
            private Map<QualifiedName, CallCount> counts = new HashMap<QualifiedName, CallCount>();
            
            /** A description of the type of counts being accumulated. e.g. supercombinator, data constructor, etc. */
            String type;
            
            public CallCountStats (String type) {
                this.type = type;
            }
            
            public Map<QualifiedName, CallCount> getCounts () {
                return counts;
            }
            
            /** 
             * Generate a short message describing the information in this object.
             * @return string containing the description.
             */
            public String generateShortMessage() {
                // There is no short form simply get the long form.
                return generateLongMessage();
            }
            
            /**
             * Generate the long description of the information in this object.
             * @return the long description of this object
             */
            public String generateLongMessage() {
                // Sort the CallCount instances by frequency.
                List<CallCount> sortedCounts = new ArrayList<CallCount> ();
               
                for (final Map.Entry<QualifiedName, CallCount> entry : counts.entrySet()) {
                    sortedCounts.add (entry.getValue());
                }
                Collections.sort (sortedCounts);
                
                StringBuilder sb = new StringBuilder();
                sb.append(type + ":\n");
                
                long totalCounts = 0;
                for (final CallCount count : sortedCounts) {                    
                    sb.append (count.count + "\t\t-> " + count.name + "\n");
                    totalCounts += count.count;
                }
                sb.append ("    total counts = " + totalCounts + "\n\n");
                
                return sb.toString();
            }
            
            /** {@inheritDoc} */
            public StatsObject summarize(List<StatsObject> runs) {
                return this;
            }
            
        }
        
        /** 
         * A utility class used to hold the count for a named function.
         * 
         * @author rcypher
         */
        static class CallCount implements Comparable<CallCount> {
            /** The name of the CAL entity that is being counted. */
            final QualifiedName name;
            
            /** The number of times the entity is invoked, referenced, etc. */
            int count;
            
            CallCount (QualifiedName name, int count) {
                this.name = name;
                this.count = count;
            }
            
            /**
             * Allow for sorting by number of calls.
             * @param cc
             * @return 0 if equal, > 0 if greater, < 0 if less.
             */
            public int compareTo (CallCount cc) {
                if (cc == null) {
                    return 1;
                }
                                
                int compareNames = name.compareTo(cc.name);
                if (compareNames != 0) {
                    return compareNames;
                }

                if (cc.count > count) {
                    return 1;
                } else if (cc.count == count) {
                    return 0;                  
                } else {
                    return -1;
                }          
            }
                              
            @Override
            public boolean equals (Object o) {
                if (o == null || !(o instanceof CallCount)) {
                    return false;
                }
                
                CallCount cc = (CallCount)o;
                return name.equals(cc.name) && count == cc.count; 
            }
            
            @Override
            public int hashCode () {
                return name.hashCode() + count;
            }
        }
        
        
    }
    
    /** 
     * Since for the g-machine the execution context has not state and functionality
     * other than being an identifier to associate CAF instances it is implemented as an
     * empty class.
     */
    static class GExecutionContext extends ExecutionContextImpl {
        
        GExecutionContext (ExecutionContextProperties properties) {
            super (properties);
        }
        
        void reset (GProgram program, ResourceAccess resourceAccess) {
            assert (program != null) : "Invalid Program reference in GExecutionContext";
            setRuntimeEnvironment(new DynamicRuntimeEnvironment(program, resourceAccess));
        }
        
        /**
         * Null out the program field.
         * This prevents the execution context from holding on to
         * an out-of-date instance of Program (as held by the DynamicRuntimeEnvironment.
         */
        void clearProgram () {
            setRuntimeEnvironment (null);
        }
                
    }

    GExecutionContext getExecutionContext() {
        return (GExecutionContext)executionContext;
    }
    
}
