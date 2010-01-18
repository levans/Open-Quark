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
 * Optimizer_Test.java
 * Creation date: (August 4th 2005 7:32:25 PM)
 * By: Greg McClement
 */
package org.openquark.cal.compiler;

import java.util.TreeMap;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openquark.cal.compiler.Expression.RecordSelection;
import org.openquark.cal.compiler.io.EntryPoint;
import org.openquark.cal.compiler.io.EntryPointSpec;
import org.openquark.cal.internal.machine.lecc.JavaPackager.LECCMachineFunction;
import org.openquark.cal.internal.module.Cal.Core.CAL_Prelude_internal;
import org.openquark.cal.internal.module.Cal.Internal.CAL_Optimizer;
import org.openquark.cal.machine.CALExecutor;
import org.openquark.cal.machine.MachineFunction;
import org.openquark.cal.module.Cal.Collections.CAL_List;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.runtime.CALExecutorException;
import org.openquark.cal.runtime.ExecutionContext;
import org.openquark.cal.services.BasicCALServices;
import org.openquark.cal.services.CALServicesTestUtilities;
import org.openquark.cal.services.WorkspaceManager;


/**
 * @author Greg McClement
 *
 * This code tests the optimizer related code paths in the compiler. There are two main 
 * areas. 
 * 
 * 1. The Java based optimizer code. This is tested by the function testJavaOptimizer. 
 * 2. The CAL based optimizer code. This is tested by the function testCALOptimizer. Currently this
 * test is turned off because the data structure keep changing a little and I was tired of updating
 * the code. Once the data strucures are stable I will fix the test up and enabled it. 
 */

public class Optimizer_Test extends TestCase {
    
    private final static Expression.Var prelude_id =  new Expression.Var(CAL_Prelude.Functions.id);
    private final static Expression.Var prelude_compose =  new Expression.Var(CAL_Prelude.Functions.compose);    
    
    /**
     * The CAL optimizer. 
     */
    private static CALExecutor optimizer_executor = null;
    private static EntryPoint optimizer_entryPoint = null;
    private static WorkspaceManager workspaceManager = null;
    
    /**
     * This will only be initialized if the optimizer is enabled.
     */
    private static PreludeTypeConstants preludeTypeConstants = null;
    
    private static DataConstructor nilDataCons = null;
    private static DataConstructor consDataCons = null;
    private static ModuleTypeInfo currentModuleTypeInfo = null;
    
    
    /**
     * @return a test suite containing all the test cases for testing CAL source
     *         generation.
     */
    public static Test suite() {
        
        TestSuite suite = new TestSuite(Optimizer_Test.class);
        
        return new TestSetup(suite) {
            
            @Override
            protected void setUp() {
                oneTimeSetUp();                
            }
            
            @Override
            protected void tearDown() {
                oneTimeTearDown();
            }
        };
    }
    
    /**
     * Performs the setup for the test suite.
     */
    private static void oneTimeSetUp() {
        initOptimizer();
    }
    
    /**
     * Performs the tear down for the test suite.
     */
    private static void oneTimeTearDown() {
        optimizer_executor = null;
        optimizer_entryPoint = null;
        workspaceManager = null;
        nilDataCons = null;
        consDataCons = null;
        currentModuleTypeInfo = null;
    }
    
    private Expression makeIdCall(Expression e){
        return new Expression.Appl( prelude_id, e );
    }
    
    private Expression makeComposeCall(Expression g, Expression f, Expression x){
        return new Expression.Appl(new Expression.Appl(new Expression.Appl(prelude_compose, g), f), x);                
    }
    
    /**
     * Load the CAL optimizer so I can run some expression through it for checking
     * the effectiveness
     */
    
    public static void initOptimizer(){

        final ModuleName targetModule = CAL_Optimizer.MODULE_NAME;
        final String defaultWorkspaceFile = "cal.optimizer.cws";
        
        BasicCALServices calServices = CALServicesTestUtilities.makeUnsharedUnitTestCALServices(null, defaultWorkspaceFile, null, false);
        calServices.getWorkspaceManager().useOptimizer(false);
        CompilerMessageLogger messageLogger = new MessageLogger();        
        if (!calServices.compileWorkspace(null, messageLogger)) {
            System.err.println(messageLogger.toString());
        }
               
        final ExecutionContext executionContext = calServices.getWorkspaceManager().makeExecutionContextWithDefaultProperties();        

        workspaceManager = calServices.getWorkspaceManager();
        Compiler compiler = calServices.getCompiler();   
        
        optimizer_entryPoint = compiler.getEntryPoint(
            EntryPointSpec.make(QualifiedName.make(ModuleName.make("Cal.Test.Internal.Optimizer_Test"), "unitTests")), 
            ModuleName.make("Cal.Test.Internal.Optimizer_Test"), messageLogger);
        if (messageLogger.getNMessages() > 0) {
            System.err.println(messageLogger.toString());
        }
        optimizer_executor = workspaceManager.makeExecutor(executionContext);

        {
            ModuleTypeInfo moduleTypeInfo = workspaceManager.getModuleTypeInfo(CAL_Prelude.MODULE_NAME);
            preludeTypeConstants = new PreludeTypeConstants(moduleTypeInfo);
        }

        currentModuleTypeInfo = calServices.getCALWorkspace().getMetaModule(targetModule).getTypeInfo();
        nilDataCons = currentModuleTypeInfo.getVisibleDataConstructor(CAL_Prelude.DataConstructors.Nil);
        consDataCons = currentModuleTypeInfo.getVisibleDataConstructor(CAL_Prelude.DataConstructors.Cons);
    }

    Expression makeCall( Expression arg1, Expression arg2 ){
        return new Expression.Appl(arg1, arg2);
    }
    
    Expression makeCall( Expression arg1, Expression arg2, Expression arg3 ){
        return makeCall(makeCall(arg1, arg2), arg3);
    }

    Expression makeCall( Expression arg1, Expression arg2, Expression arg3, Expression arg4 ){
        return makeCall(makeCall(makeCall(arg1, arg2), arg3), arg4);
    }

    Expression makeVar(ModuleName module, String function){
        return new Expression.Var(QualifiedName.make(module, function));
    }

    Expression makeVar(FunctionalAgent entity){
        return new Expression.Var(entity);
    }
    
    Expression makeInt(int value){
        return new Expression.Literal( new Integer(value) );
    }

    /**
     * Run the tests for the CAL transformation optimizer
     */
    public void testCALOptimizer(){        
        try {
            Object result = optimizer_executor.exec(optimizer_entryPoint, new Object[] { workspaceManager, preludeTypeConstants });
            assertTrue(((Boolean) result).booleanValue());
        } catch (CALExecutorException e) {
            fail(e.toString());
        }                
    }

    /**
     * Run the tests for the Java transformation optimizer.
     */
    public void testJavaOptimizer() throws UnableToResolveForeignEntityException{
               
        // test compose and id optimizations
        
        {
            Expression.Literal literal_dogdogdog = new Expression.Literal("dogdogdog");
            Expression.Appl appl_compose_id = new Expression.Appl( prelude_compose, prelude_id );
            
            Expression tuple_1_2;
            {
                TreeMap<FieldName, Expression> fieldMap1 = new TreeMap<FieldName, Expression>();
                fieldMap1.put(FieldName.makeOrdinalField(1), new Expression.Literal(new Integer(1)));
                fieldMap1.put(FieldName.makeOrdinalField(2), new Expression.Literal(new Integer(2)));
                tuple_1_2 = new Expression.RecordExtension(null, fieldMap1);
            }
            
            Expression exprs[] = {
                    // 0. id id id "dogdogdog"
                    new Expression.Appl(
                            
                            new Expression.Appl(
                                    new Expression.Appl(prelude_id, prelude_id),
                                    prelude_id
                            ),
                            literal_dogdogdog
                    ), 
                    // 1. ((compose id) id id) "dogdogdog";
                    
                    new Expression.Appl(
                            new Expression.Appl( new Expression.Appl( appl_compose_id, prelude_id ), prelude_id ),
                            literal_dogdogdog
                    ), 
                    
                    // 2. ((compose id) id ((compose id) id id)) ((compose id) id "dogdogdog");
                    new Expression.Appl(
                            new Expression.Appl(new Expression.Appl( prelude_compose, prelude_id ), new Expression.Appl( new Expression.Appl( appl_compose_id, prelude_id ), prelude_id )),
                            new Expression.Appl(new Expression.Appl( appl_compose_id, prelude_id ), literal_dogdogdog)
                    ),
                    
                    // 3. emptyList => []
                    new Expression.Var(CAL_Prelude_internal.Functions.emptyList),
                    
                    // 4. emptyString => ""
                    new Expression.Var(CAL_Prelude_internal.Functions.emptyString),
                    
                    // 5. list0 => []
                    new Expression.Var(CAL_List.Functions.list0),
                    
                    // 6. fst (1::Int, 2::Int) => (1::Int, 2::Int).#1
                    new Expression.Appl( 
                            new Expression.Var(CAL_Prelude.Functions.fst),
                            tuple_1_2
                    ),
                    
                    // 7. asTypeOf 2 1::Int => 2
                    new Expression.Appl(
                            new Expression.Appl(
                                    new Expression.Var(CAL_Prelude.Functions.asTypeOf),
                                    new Expression.Literal( new Integer(2) ) ),
                            new Expression.Literal( new Integer(1) ) ),
                                    
                    // 8. const 2 1::Int => 2
                    new Expression.Appl(
                            new Expression.Appl(
                                    new Expression.Var(CAL_Prelude.Functions.const_),
                                    new Expression.Literal( new Integer(2) ) ),
                            new Expression.Literal( new Integer(1) ) ),
                                        
                    // 9. flip Cons Nil 1::Int => Cons 1::Int Nil
                    new Expression.Appl(
                    new Expression.Appl(
                    new Expression.Appl( 
                            new Expression.Var(CAL_Prelude.Functions.flip),
                            new Expression.Var(consDataCons)),
                            new Expression.Var(nilDataCons)),
                            new Expression.Literal( new Integer(1) ) ),
                            
                    // 10. flip (flip Cons) 1::Int Nil => Cons 1::Int Nil
                    new Expression.Appl(
                    new Expression.Appl(
                    new Expression.Appl( 
                            new Expression.Var(CAL_Prelude.Functions.flip),
                            new Expression.Appl(new Expression.Var(CAL_Prelude.Functions.flip),new Expression.Var(consDataCons))),
                            new Expression.Literal( new Integer(1) )),
                            new Expression.Var(nilDataCons) ),
                            
                    // 11. id( (id flip) (id ((id flip) (id Cons))) (id 1::Int) (id Nil)) => Cons 1::Int Nil
                    makeIdCall(new Expression.Appl(
                    makeIdCall(new Expression.Appl(
                    makeIdCall(new Expression.Appl( 
                            makeIdCall(new Expression.Var(CAL_Prelude.Functions.flip)),
                            makeIdCall(new Expression.Appl(new Expression.Var(CAL_Prelude.Functions.flip),new Expression.Var(consDataCons))))),
                            makeIdCall(new Expression.Literal( new Integer(1) )))),
                            makeIdCall(new Expression.Var(nilDataCons) ))),
                            
                    // 12. (compose id id flip) (compose id flip Cons) 1::Int Nil => Cons 1::Int Nil
                    new Expression.Appl(
                    new Expression.Appl(
                    new Expression.Appl( 
                            makeComposeCall(prelude_id, prelude_id, new Expression.Var(CAL_Prelude.Functions.flip)),
                            makeComposeCall(prelude_id, new Expression.Var(CAL_Prelude.Functions.flip),new Expression.Var(consDataCons))),
                            new Expression.Literal( new Integer(1) )),
                            new Expression.Var(nilDataCons) ),
                            
                    // 13. snd (1::Int, 2::Int) => (1::Int, 2::Int).#2
                    new Expression.Appl( 
                            new Expression.Var(CAL_Prelude.Functions.snd ),
                            tuple_1_2),
                            
                    // 14. field1 (1::Int, 2::Int) => (1::Int, 2::Int).#1
                    new Expression.Appl( 
                            new Expression.Var( CAL_Prelude.Functions.field1 ),
                            tuple_1_2),
                            
                    // 15. field2 (1::Int, 2::Int) => (1::Int, 2::Int).#2
                    new Expression.Appl( 
                            new Expression.Var( CAL_Prelude.Functions.field2 ),
                            tuple_1_2),
            };
            
            for(int i = 0; i < exprs.length; ++i){
                String[] formalParameters = {};
                boolean[] parameterStrictness = {};
                TypeExpr[] parameterTypes = {};
                CoreFunction cf = CoreFunction.makeCALCoreFunction(
                        QualifiedName.make(CAL_Prelude.MODULE_NAME, "dogdogdog1"),
                        formalParameters,
                        parameterStrictness,
                        parameterTypes,
                        new TypeVar(),
                        1123191938810L
                );
                MachineFunction mf = new LECCMachineFunction(cf);
                ExpressionAnalyzer analyzer = new ExpressionAnalyzer(currentModuleTypeInfo, mf);
                
                Expression exprOptimized = analyzer.transformExpression(exprs[i]);
                switch(i){
                case 0:
                case 1:
                case 2:
                    assertTrue( literal_dogdogdog.equals(exprOptimized) );
                    break;
                    
                case 3:
                case 5:
                    assertTrue( exprOptimized.asVar() != null );
                    assertTrue( exprOptimized.asVar().getFunctionalAgent() == nilDataCons);
                    break;
                    
                case 4:
                    assertTrue( exprOptimized.asLiteral() != null );
                    assertTrue( exprOptimized.asLiteral().getLiteral().equals(""));
                    break;
                    
                case 6:
                case 14:
                {
                    RecordSelection rs = exprOptimized.asRecordSelection();
                    assertTrue( rs != null );
                    FieldName.Ordinal ordinal = (FieldName.Ordinal) rs.getFieldName();
                    assertTrue(ordinal.getOrdinal() == 1);
                    assertTrue(rs.getRecordExpr() == tuple_1_2);
                }
                break;                    
                
                case 7:
                case 8:
                    assertTrue( exprOptimized.asLiteral() != null );
                    assertTrue(((Integer) exprOptimized.asLiteral().getLiteral()).intValue() == 2);
                    break;
                    
                case 9:
                case 10:
                case 11:
                case 12:
                {
                    Expression.Appl a = exprOptimized.asAppl();
                    assertTrue( a != null );
                    Expression.Appl a_1 = a.getE1().asAppl();
                    assertTrue( a_1 != null );
                    Expression.Var v_1_1 = a_1.getE1().asVar();
                    assertTrue( v_1_1 != null );
                    assertTrue( v_1_1.getFunctionalAgent() == consDataCons);
                    Expression.Literal v_1_2 = a_1.getE2().asLiteral();
                    assertTrue( v_1_2 != null );
                    assertTrue(((Integer) v_1_2.getLiteral()).intValue() == 1);                    
                    Expression.Var v_2 = a.getE2().asVar();
                    assertTrue( v_2 != null );
                    assertTrue( v_2.getFunctionalAgent() == nilDataCons);
                }
                break;
                
                case 13:
                case 15:
                {
                    RecordSelection rs = exprOptimized.asRecordSelection();
                    assertTrue( rs != null );
                    FieldName.Ordinal ordinal = (FieldName.Ordinal) rs.getFieldName();
                    assertTrue(ordinal.getOrdinal() == 2);
                    assertTrue(rs.getRecordExpr() == tuple_1_2);
                }
                break;                  
                
                }
            }
            
        }
        
    }
}
