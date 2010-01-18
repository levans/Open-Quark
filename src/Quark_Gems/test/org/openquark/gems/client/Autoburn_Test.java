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
 * Autoburn_Test.java
 * Created: Jan 14, 2005
 * By: Peter Cardwell
 */

package org.openquark.gems.client;

import java.util.Arrays;
import java.util.HashSet;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openquark.cal.CALPlatformTestModuleNames;
import org.openquark.cal.compiler.CodeAnalyser;
import org.openquark.cal.compiler.MessageLogger;
import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.TypeException;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.module.Cal.Collections.CAL_List;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.services.BasicCALServices;
import org.openquark.cal.services.GemEntity;
import org.openquark.gems.client.AutoburnLogic.AutoburnInfo;


/**
 * Tests various uses of AutoburnLogic.getAutoburnInfo().
 * @author Peter Cardwell
 */
public class Autoburn_Test extends TestCase {
    
    static BasicCALServices calServices;
    private static final ModuleName testModule = CALPlatformTestModuleNames.M2;
    
    public Autoburn_Test(String name) {
        super(name);
    }
    
    public Autoburn_Test() {
        super("");
    }

    /**
     * Tests the version of getAutoburnInfo() that takes an array of TypeExpr objects. 
     */
    public void testSimpleAutoburn() {

        // Make the source a function of type a -> b -> c, and the destination a gem of type a -> b
        TypeExpr[] sourceTypes = new TypeExpr[] {
            TypeExpr.makeParametricType(),  // first argument
            TypeExpr.makeParametricType(),  // second argument
            TypeExpr.makeParametricType()   // output type
        };
        TypeExpr destType = calServices.getTypeFromString(testModule, "a -> b");
        
        AutoburnInfo autoburnInfo = AutoburnLogic.getAutoburnInfo(destType, sourceTypes, calServices.getTypeCheckInfo(testModule));
        assertEquals(AutoburnLogic.AutoburnUnifyStatus.AMBIGUOUS_NOT_NECESSARY, autoburnInfo.getAutoburnUnifyStatus());
        
        // Every possible burn combination is valid here, ie leaving both arguments unburned,
        // burning either argument, or burning both arguments.
        assertEquals(3, autoburnInfo.getBurnCombinations().size());        
        int[] argsToBurn = (autoburnInfo.getBurnCombinations().get(0)).getInputsToBurn();
        assertTrue(Arrays.equals(argsToBurn, new int[] {0}));
        argsToBurn = (autoburnInfo.getBurnCombinations().get(1)).getInputsToBurn();
        assertTrue(Arrays.equals(argsToBurn, new int[] {1}));
        argsToBurn = (autoburnInfo.getBurnCombinations().get(2)).getInputsToBurn();
        assertTrue(Arrays.equals(argsToBurn, new int[] {0, 1}));
    }
    
    /**
     * Tests the version of getAutoburnInfo() that takes an array of TypeExpr objects, 
     * in the case where burning should not be necessary. 
     */
    public void testSimpleAutoburnNotNecessary() {

        // Make the source a function of type a -> b, and the destination a gem of type [a].
        TypeExpr[] sourceTypes = new TypeExpr[] {            
            TypeExpr.makeParametricType(),  // second argument
            TypeExpr.makeParametricType()   // output type
        };
        TypeExpr destType = calServices.getTypeFromString(testModule, "[a]");
        
        AutoburnInfo autoburnInfo = AutoburnLogic.getAutoburnInfo(destType, sourceTypes, calServices.getTypeCheckInfo(testModule));

        // The only possibility here is to leave the argument unburned.
        assertEquals(AutoburnLogic.AutoburnUnifyStatus.NOT_NECESSARY, autoburnInfo.getAutoburnUnifyStatus());
        assertEquals(0, autoburnInfo.getBurnCombinations().size());       
    }
    
    /**
     * Tests the version of getAutoburnInfo() that takes a gem entity.
     */
    public void testAutoburnGemEntity() {
        
        // We'll use List.map for the source entity and [a] -> [b] for the destination
        GemEntity gemEntity = calServices.getCALWorkspace().getGemEntity(CAL_List.Functions.map);
        TypeExpr destType = calServices.getTypeFromString(testModule, "[a] -> [b]");
        
        AutoburnInfo autoburnInfo = AutoburnLogic.getAutoburnInfo(destType, gemEntity, calServices.getTypeCheckInfo(testModule));
        assertEquals(AutoburnLogic.AutoburnUnifyStatus.UNAMBIGUOUS, autoburnInfo.getAutoburnUnifyStatus());
        
        // The only way to connect these two types together is by burning the second argument.
        assertEquals(1, autoburnInfo.getBurnCombinations().size());        
        int[] argsToBurn = (autoburnInfo.getBurnCombinations().get(0)).getInputsToBurn();
        assertTrue(Arrays.equals(argsToBurn, new int[] {1}));
    }    
    
    /**
     * Tests getAutoburnInfo() with a two-argument gem where both of the arguments are unburnt.
     */
    public void testAutoburnDualArgGemBothArgsUnburnt() {
        CodeAnalyser codeAnalyser = new CodeAnalyser(calServices.getTypeChecker(),
                                                     calServices.getCALWorkspace().getMetaModule(testModule).getTypeInfo(),
                                                     true,
                                                     false);
        
        // Create the source gem        
        // Output type: Boolean, Input types: Double, Boolean
        String code = "let x = (a :: " + CAL_Prelude.TypeConstructors.Double.getQualifiedName() + ", b :: " + CAL_Prelude.TypeConstructors.Boolean.getQualifiedName() + "); in " + CAL_Prelude.DataConstructors.True.getQualifiedName();
        Gem sourceGem = new CodeGem(codeAnalyser, code, new HashSet<String>());
        
        // Create the destination gem
        // Input type: Double -> Boolean -> a, Output type: a
        code = "f 1.0 " + CAL_Prelude.DataConstructors.True.getQualifiedName();
        Gem destGem = new CodeGem(codeAnalyser, code, new HashSet<String>());
        
        // It should be necessary to burn both inputs of the source gem
        AutoburnLogic.AutoburnInfo autoburnInfo = AutoburnLogic.getAutoburnInfo(destGem.getInputPart(0).getType(), sourceGem, calServices.getTypeCheckInfo(testModule));
        assertEquals(AutoburnLogic.AutoburnUnifyStatus.UNAMBIGUOUS, autoburnInfo.getAutoburnUnifyStatus());
        assertEquals(1, autoburnInfo.getBurnCombinations().size());
        int[] argsToBurn = (autoburnInfo.getBurnCombinations().get(0)).getInputsToBurn();
        assertTrue(Arrays.equals(new int[] {0, 1}, argsToBurn));
    }

    /**
     * Tests getAutoburnInfo() with a two-argument gem where the first argument is pre-burnt.
     */
    public void testAutoburnDualArgGemFirstArgBurnt() {
        CodeAnalyser codeAnalyser = new CodeAnalyser(calServices.getTypeChecker(),
                                                     calServices.getCALWorkspace().getMetaModule(testModule).getTypeInfo(),
                                                     true,
                                                     false);
        
        GemGraph gemGraph = new GemGraph();
        
        // Create the source gem        
        // Output type: Boolean, Input types: Double, Boolean
        String code = "let x = (a :: " + CAL_Prelude.TypeConstructors.Double.getQualifiedName() + ", b :: " + CAL_Prelude.TypeConstructors.Boolean.getQualifiedName() + "); in " + CAL_Prelude.DataConstructors.True.getQualifiedName();
        Gem sourceGem = new CodeGem(codeAnalyser, code, new HashSet<String>());
        gemGraph.addGem(sourceGem);
        
        // Create the destination gem
        // Input type: Double -> Boolean -> a, Output type: a
        code = "f 1.0 " + CAL_Prelude.DataConstructors.True.getQualifiedName();
        Gem destGem = new CodeGem(codeAnalyser, code, new HashSet<String>());
        gemGraph.addGem(destGem);
        
        // Now burn the first input of the gem
        sourceGem.getInputPart(0).setBurnt(true);
        try {
            gemGraph.typeGemGraph(calServices.getTypeCheckInfo(testModule));
        } catch (TypeException e) {
            throw new Error("Error typing gem graph");
        }
        
        // It should be necessary to burn both inputs of the source gem
        AutoburnLogic.AutoburnInfo autoburnInfo = AutoburnLogic.getAutoburnInfo(destGem.getInputPart(0).getType(), sourceGem, calServices.getTypeCheckInfo(testModule));
        assertEquals(AutoburnLogic.AutoburnUnifyStatus.UNAMBIGUOUS, autoburnInfo.getAutoburnUnifyStatus());
        assertEquals(1, autoburnInfo.getBurnCombinations().size());
        int[] argsToBurn = (autoburnInfo.getBurnCombinations().get(0)).getInputsToBurn();
        assertTrue(Arrays.equals(new int[] {1}, argsToBurn));
    }

    /**
     * Tests getAutoburnInfo() with a two-argument gem where the second argument is pre-burnt.
     */
    public void testAutoburnDualArgGemSecondArgBurnt() {
        CodeAnalyser codeAnalyser = new CodeAnalyser(calServices.getTypeChecker(),
                                                     calServices.getCALWorkspace().getMetaModule(testModule).getTypeInfo(),
                                                     true,
                                                     false);
        
        GemGraph gemGraph = new GemGraph();
        // Create the source gem        
        // Output type: Boolean, Input types: Double, Boolean
        String code = "let x = (a :: " + CAL_Prelude.TypeConstructors.Double.getQualifiedName() + ", b :: " + CAL_Prelude.TypeConstructors.Boolean.getQualifiedName() + "); in " + CAL_Prelude.DataConstructors.True.getQualifiedName();
        Gem sourceGem = new CodeGem(codeAnalyser, code, new HashSet<String>());
        gemGraph.addGem(sourceGem);
        
        // Create the destination gem
        // Input type: Double -> Boolean -> a, Output type: a
        code = "f 1.0 " + CAL_Prelude.DataConstructors.True.getQualifiedName();
        Gem destGem = new CodeGem(codeAnalyser, code, new HashSet<String>());
        gemGraph.addGem(destGem);
        
        
        // Now burn the first input of the gem
        sourceGem.getInputPart(1).setBurnt(true);
        try {
            gemGraph.typeGemGraph(calServices.getTypeCheckInfo(testModule));
        } catch (TypeException e) {
            throw new Error("Error typing gem graph");
        }
        
        // It should be necessary to burn both inputs of the source gem
        AutoburnLogic.AutoburnInfo autoburnInfo = AutoburnLogic.getAutoburnInfo(destGem.getInputPart(0).getType(), sourceGem, calServices.getTypeCheckInfo(testModule));
        assertEquals(AutoburnLogic.AutoburnUnifyStatus.UNAMBIGUOUS, autoburnInfo.getAutoburnUnifyStatus());
        assertEquals(1, autoburnInfo.getBurnCombinations().size());
        int[] argsToBurn = (autoburnInfo.getBurnCombinations().get(0)).getInputsToBurn();
        assertTrue(Arrays.equals(new int[] {0}, argsToBurn));
    }

    /**
     * Tests getAutoburnInfo() with a two-argument gem where both arguments are pre-burnt.
     */
    public void testAutoburnDualArgGemBothArgsBurnt() {
        CodeAnalyser codeAnalyser = new CodeAnalyser(calServices.getTypeChecker(),
                                                     calServices.getCALWorkspace().getMetaModule(testModule).getTypeInfo(),
                                                     true,
                                                     false);

        GemGraph gemGraph = new GemGraph();
        
        // Create the source gem        
        // Output type: Boolean, Input types: Double, Boolean
        String code = "let x = (a :: " + CAL_Prelude.TypeConstructors.Double.getQualifiedName() + ", b :: " + CAL_Prelude.TypeConstructors.Boolean.getQualifiedName() + "); in " + CAL_Prelude.DataConstructors.True.getQualifiedName();
        Gem sourceGem = new CodeGem(codeAnalyser, code, new HashSet<String>());
        gemGraph.addGem(sourceGem);
        
        // Create the destination gem
        // Input type: Double -> Boolean -> a, Output type: a
        code = "f 1.0 " + CAL_Prelude.DataConstructors.True.getQualifiedName();
        Gem destGem = new CodeGem(codeAnalyser, code, new HashSet<String>());
        gemGraph.addGem(destGem);
        
        // Now burn both inputs of the gem
        sourceGem.getInputPart(0).setBurnt(true);
        sourceGem.getInputPart(1).setBurnt(true);
        try {
            gemGraph.typeGemGraph(calServices.getTypeCheckInfo(testModule));
        } catch (TypeException e) {
            throw new Error("Error typing gem graph");
        }
        
        // It should be necessary to burn both inputs of the source gem
        AutoburnLogic.AutoburnInfo autoburnInfo = AutoburnLogic.getAutoburnInfo(destGem.getInputPart(0).getType(), sourceGem, calServices.getTypeCheckInfo(testModule));
        assertEquals(AutoburnLogic.AutoburnUnifyStatus.NOT_NECESSARY, autoburnInfo.getAutoburnUnifyStatus());
        assertEquals(0, autoburnInfo.getBurnCombinations().size());
    }

    /**
     * Test autoburning of a gem that has a bound connection.
     */
    public void testAutoburnBoundGem() {
        // Create a new code analyser so we can build code gems
        CodeAnalyser codeAnalyser = new CodeAnalyser(calServices.getTypeChecker(),
                                                     calServices.getCALWorkspace().getMetaModule(testModule).getTypeInfo(),
                                                     true,
                                                     false);
        
        // Create the code gem that will be bound to the primary source gem.
        String code = "(firstSecondaryArg, secondSecondaryArg)";
        Gem secondaryCodeGem = new CodeGem(codeAnalyser, code, new HashSet<String>());
        
        // Create the code gem that will act as the source gem for the autoburn query.
        code = "(firstPrimaryArg::" + CAL_Prelude.TypeConstructors.Double.getQualifiedName() + ", secondPrimaryArg, thirdPrimaryArg::" + CAL_Prelude.TypeConstructors.Int.getQualifiedName() + ", fourth::" + CAL_Prelude.TypeConstructors.Boolean.getQualifiedName() + ", fifth::" + CAL_Prelude.TypeConstructors.Boolean.getQualifiedName() + ")";
        CodeGem primaryCodeGem = new CodeGem(codeAnalyser, code, new HashSet<String>());
        
        // Create the code gem that will act as the destination gem for the autoburn query.
        code = "f 1.0 " + CAL_Prelude.DataConstructors.True.getQualifiedName() + " " + CAL_Prelude.DataConstructors.True.getQualifiedName();
        Gem destinationCodeGem = new CodeGem(codeAnalyser, code, new HashSet<String>());
        
        // Connect secondaryCodeGem to first input of primaryCodeGem
        Connection connection = new Connection(secondaryCodeGem.getOutputPart(), primaryCodeGem.getInputPart(1));
        primaryCodeGem.getInputPart(1).bindConnection(connection);
        secondaryCodeGem.getOutputPart().bindConnection(connection);
                
        AutoburnLogic.AutoburnInfo autoburnInfo = AutoburnLogic.getAutoburnInfo(destinationCodeGem.getInputPart(0).getType(), primaryCodeGem, calServices.getTypeCheckInfo(testModule));
        assertEquals(AutoburnLogic.AutoburnUnifyStatus.UNAMBIGUOUS, autoburnInfo.getAutoburnUnifyStatus());
        assertEquals(1, autoburnInfo.getBurnCombinations().size());
        int[] argsToBurn = (autoburnInfo.getBurnCombinations().get(0)).getInputsToBurn();
        assertTrue(Arrays.equals(new int[] {0, 3, 4}, argsToBurn));
    }
    
    /**
     * Test autoburning of a gem that has a bound connection and one of it's inputs already burnt.
     */
    public void testAutoburnBoundAndBurntGem() {
        //Create a new code analyser so we can build code gems
        CodeAnalyser codeAnalyser = new CodeAnalyser(calServices.getTypeChecker(),
            calServices.getCALWorkspace().getMetaModule(testModule).getTypeInfo(),
            true,
            false);
        GemGraph gemGraph = new GemGraph();
        
        String code = "(firstSecondaryArg, secondSecondaryArg)";
        Gem secondaryCodeGem = new CodeGem(codeAnalyser, code, new HashSet<String>());
        gemGraph.addGem(secondaryCodeGem);
        
        code = "(firstPrimaryArg::" + CAL_Prelude.TypeConstructors.Double.getQualifiedName() + ", secondPrimaryArg, thirdPrimaryArg::" + CAL_Prelude.TypeConstructors.Int.getQualifiedName() + ", fourth::" + CAL_Prelude.TypeConstructors.Boolean.getQualifiedName() + ", fifth::" + CAL_Prelude.TypeConstructors.Boolean.getQualifiedName() + ")";
        CodeGem primaryCodeGem = new CodeGem(codeAnalyser, code, new HashSet<String>());
        gemGraph.addGem(primaryCodeGem);
        
        code = "f 1.0 " + CAL_Prelude.DataConstructors.True.getQualifiedName() + " " + CAL_Prelude.DataConstructors.True.getQualifiedName();
        Gem outputCodeGem = new CodeGem(codeAnalyser, code, new HashSet<String>());
        gemGraph.addGem(outputCodeGem);
        
        // Connect secondaryCodeGem to second input of primaryCodeGem
        Connection connection = new Connection(secondaryCodeGem.getOutputPart(), primaryCodeGem.getInputPart(1));
        primaryCodeGem.getInputPart(1).bindConnection(connection);
        secondaryCodeGem.getOutputPart().bindConnection(connection);
                
        // Burn the fourth input to the primary code gem, and re-set the output type appropriately
        primaryCodeGem.getInputPart(3).setBurnt(true);
        try {
            gemGraph.typeGemGraph(calServices.getTypeCheckInfo(testModule));
        } catch (TypeException e) {
            throw new Error("Error typing gem graph");
        }
        
        AutoburnLogic.AutoburnInfo autoburnInfo = AutoburnLogic.getAutoburnInfo(outputCodeGem.getInputPart(0).getType(), primaryCodeGem, calServices.getTypeCheckInfo(testModule));
        assertEquals(AutoburnLogic.AutoburnUnifyStatus.UNAMBIGUOUS, autoburnInfo.getAutoburnUnifyStatus());
        assertEquals(1, autoburnInfo.getBurnCombinations().size());
        int[] argsToBurn = (autoburnInfo.getBurnCombinations().get(0)).getInputsToBurn();
        assertTrue(Arrays.equals(new int[] {0, 4}, argsToBurn));
    } 
    
    /**
     * Tests autoburning on a code gem that outputs a function even before any arguments are burnt. 
     */
    public void testAutoburnFunctionGem() {
        // Create a new code analyzer so we can build code gems
        CodeAnalyser codeAnalyser = new CodeAnalyser(calServices.getTypeChecker(),
                calServices.getCALWorkspace().getMetaModule(testModule).getTypeInfo(),
                true,
                false);
        
        // Create a gem whose output type is Num a => [a] -> [a]
        String code = CAL_List.Functions.map.getQualifiedName() + " (\\ x -> x + 1)";
        Gem functionCodeGem = new CodeGem(codeAnalyser, code, new HashSet<String>());
        
        // Create another gem whose input is of type Num c => [c] -> b
        code = "f [1, 2]";
        Gem destinationCodeGem = new CodeGem(codeAnalyser, code, new HashSet<String>());
        
        // These gems should be able to connect without having to do any autoburning
        AutoburnLogic.AutoburnInfo autoburnInfo = AutoburnLogic.getAutoburnInfo(destinationCodeGem.getInputPart(0).getType(), functionCodeGem, calServices.getTypeCheckInfo(testModule));
        assertEquals(AutoburnLogic.AutoburnUnifyStatus.NOT_NECESSARY, autoburnInfo.getAutoburnUnifyStatus());
        assertEquals(autoburnInfo.getBurnCombinations().size(), 0);        
    }
    
    /**
     * Second test autoburning a code gem that outputs a function even before any arguments are burnt.
     * In this case, the test is designed so that burning the input is necessary. 
     */
    public void testAutoburnFunctionGem2() {
        // Create a new code analyser so we can build code gems
        CodeAnalyser codeAnalyser = new CodeAnalyser(calServices.getTypeChecker(),
                calServices.getCALWorkspace().getMetaModule(testModule).getTypeInfo(),
                true,
                false);
        
        // Create a gem with output type Num b => [b] -> [b] and input type a 
        String code = "let y = a; in " + CAL_List.Functions.map.getQualifiedName() + " (\\x -> x + 1)";
        Gem functionCodeGem = new CodeGem(codeAnalyser, code, new HashSet<String>());
        
        // Create a gem whose first input is of type Num e => c -> [e] -> d
        code = "f b [1, 2]";
        Gem destinationCodeGem = new CodeGem(codeAnalyser, code, new HashSet<String>());
        
        // In order to connect these gems, we would need to burn the first input. Let's make sure the autoBurner agrees.
        AutoburnLogic.AutoburnInfo autoburnInfo = AutoburnLogic.getAutoburnInfo(destinationCodeGem.getInputPart(0).getType(), functionCodeGem, calServices.getTypeCheckInfo(testModule));
        assertEquals(autoburnInfo.getAutoburnUnifyStatus(), AutoburnLogic.AutoburnUnifyStatus.UNAMBIGUOUS);
        assertEquals(autoburnInfo.getBurnCombinations().size(), 1);        
        int[] argsToBurn = (autoburnInfo.getBurnCombinations().get(0)).getInputsToBurn();
        assertTrue(Arrays.equals(argsToBurn, new int[] {0}));
        
    }
    
    public static Test suite() {

        TestSuite suite = new TestSuite(Autoburn_Test.class);

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
    
    public static void oneTimeSetUp() {
        calServices = BasicCALServices.make(GemCutter.GEMCUTTER_PROP_WORKSPACE_FILE, "cal.platform.test.cws", null);
        calServices.compileWorkspace(null, new MessageLogger());
    }
    
    public static void oneTimeTearDown() {
        calServices = null;
    }
    
}
