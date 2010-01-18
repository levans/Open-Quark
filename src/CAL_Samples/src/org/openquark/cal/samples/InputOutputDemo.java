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
 * InputOutputDemo.java
 * Creation date: Dec 13, 2006.
 * By: Magnus Byne
 */
package org.openquark.cal.samples;

import java.util.List;

import org.openquark.cal.CALPlatformTestModuleNames;
import org.openquark.cal.compiler.CompilerMessageLogger;
import org.openquark.cal.compiler.MessageLogger;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceModel;
import org.openquark.cal.compiler.io.EntryPointSpec;
import org.openquark.cal.compiler.io.InputPolicy;
import org.openquark.cal.compiler.io.OutputPolicy;
import org.openquark.cal.module.Cal.Collections.CAL_List;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.services.BasicCALServices;


/**
 * Sample showing how input and output policies are used
 * for calling CAL functions with polymorphic types.
 *
 * @author Magnus Byne
 */
public class InputOutputDemo {

    private static final String WORKSPACE_FILE_NAME = "cal.platform.test.cws";
    
 
    public static void main(String[] args) {
 
        CompilerMessageLogger messageLogger = new MessageLogger();
        BasicCALServices calServices = BasicCALServices.makeCompiled(WORKSPACE_FILE_NAME, messageLogger);        

        try {

            {
                //Example 1: Inferring output type
                //This example shows how to use the Prelude.add function
                //Prelude.add is a polymorphic function - it works for all types of numbers
                //Therefore we must provide some type information to make it concrete
                //We provide a typed (Int) input policy for the first input. This will
                //allow CAL to infer the types of the second input and the output, so default
                //policies can be used there. 
                
                //Note 1. If add was not a polymorphic function it would be possible to use just
                //default input and output policies as the types would not be ambiguous 
                
                //Note 2. If the input was of a type for which there was no special typed policy
                //we could use InputPolicy.makeTypedDefaultInputPolicy to create one, e.g.
                //InputPolicy.makeTypedDefaultInputPolicy(SourceModel.TypeExprDefn.TypeCons.make(CAL_Prelude.TypeConstructors.Int))

                EntryPointSpec addSpec = EntryPointSpec.make(QualifiedName.make(CAL_Prelude.MODULE_NAME, "add"), new InputPolicy[] {
                    InputPolicy.INT_INPUT_POLICY,
                    InputPolicy.DEFAULT_INPUT_POLICY
                }, OutputPolicy.DEFAULT_OUTPUT_POLICY);
                
                Object addResult = calServices.runFunction(addSpec, new Object[] { new Integer(1), new Integer(2) });
                System.out.println("Example 1 output: " + addResult.toString());
            }

            {
                //Example 2: Inferring input types
                //This example again shows how the Prelude.add function
                //can be invoked. We provide a type (Int) for the output. This will
                //allow CAL to infer the types of the inputs, so default input
                //policies can be used.
                EntryPointSpec addSpec = EntryPointSpec.make(QualifiedName.make(CAL_Prelude.MODULE_NAME, "add"), new InputPolicy[] {
                    InputPolicy.DEFAULT_INPUT_POLICY,
                    InputPolicy.DEFAULT_INPUT_POLICY
                }, OutputPolicy.INT_OUTPUT_POLICY);
                
                Object addResult = calServices.runFunction(addSpec, new Object[] { new Integer(3), new Integer(4) });
                System.out.println("Example 2 output: " + addResult.toString());
            }

            {
                //Example 3: CAL Value Output
                //This example shows the use of CAL_VALUE_OUTPUT to output a CAL value as an opaque type in Java.
                //the opaque output is then used as an input to another CAL function.
               
                //Call the allPrimes and get the resulting infinite list as an opaque CAL Value 
                EntryPointSpec allPrimesSpec = EntryPointSpec.make(QualifiedName.make(CALPlatformTestModuleNames.M1, "allPrimes"), new InputPolicy[0], OutputPolicy.CAL_VALUE_OUTPUT_POLICY);

                Object allPrimesResult = calServices.runFunction(allPrimesSpec, new Object[0]);

                //Use List.Take to get 5 values from the primes list.
                //As List.take is a polymorphic function we must specify the type of the CAL input 
                EntryPointSpec primesSpec = EntryPointSpec.make(QualifiedName.make(CAL_List.MODULE_NAME, "take"), new InputPolicy[] { 
                    InputPolicy.DEFAULT_INPUT_POLICY, 
                    InputPolicy.makeTypedCalValueInputPolicy(SourceModel.TypeExprDefn.List.make(SourceModel.TypeExprDefn.TypeCons.make(CAL_Prelude.TypeConstructors.Int)))}, OutputPolicy.DEFAULT_OUTPUT_POLICY);

                List<?> primesList = (List<?>) calServices.runFunction(primesSpec, new Object[] { new Integer(5), allPrimesResult});

                System.out.println("Example 3 output: " + primesList.toString());
            } 
            
            
        } catch (Exception e) {
            System.err.println("Exception: (" + e + ") log:" + messageLogger);
            e.printStackTrace();
        }
    }

}
