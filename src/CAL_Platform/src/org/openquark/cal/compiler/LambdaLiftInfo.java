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
 * LambdaLiftInfo.java
 * Created: Nov 17, 2003
 * By: Bo Ilic
 */
package org.openquark.cal.compiler;

/**
 * Helper class providing information about the local functions, case expressions and lambda
 * expressions that are lifted from a given top-level function definition. Used to give meaninful
 * names to the lifted functions.
 * @author Bo Ilic
 */
class LambdaLiftInfo {

    /** name of the top-level function whose definition is being analyzed for lambda lifting possibilities. */
    private final String topLevelFunctionName;

    /** The number of lambda expressions replaced by calls to newly defined global functions. */
    private int lambdaLiftCount;

    /** 
     * The number of local functions rewritten to be in 0 argument form by introducing a call to
     * a newly defined global function.
     */
    private int localFunctionLiftCount;

    /**
     * The number of case expressions lifted so that the case expression is at the root of a
     * top level function definition.
     */
    private int caseLiftCount;

    /**
     * The number of dc field selections lifted so that the expression is at the root of a
     * top level function definition.
     */
    private int selectionLiftCount;
    
    LambdaLiftInfo(String topLevelFunctionName) {
        if (topLevelFunctionName == null) {
            throw new NullPointerException();
        }

        this.topLevelFunctionName = topLevelFunctionName;
        
        lambdaLiftCount = 0;
        localFunctionLiftCount = 0;
        caseLiftCount = 0;
        selectionLiftCount = 0;
    }
    
    int getLambdaLiftCount() {
        return lambdaLiftCount;
    }
    
    int getLocalFunctionLiftCount() {
        return localFunctionLiftCount;
    }
    
    int getCaseLiftCount() {
        return caseLiftCount;
    }
    
    int getDCSelectionLiftCount() {
        return selectionLiftCount;
    }
    
    String getNextLiftedLambdaName() {
        ++lambdaLiftCount;        
        return "$lambda$" + topLevelFunctionName + "$" + lambdaLiftCount;            
    }    
    
    String getNextLiftedLocalFunctionName(String localFunctionName) {
        ++localFunctionLiftCount; 
        //the localFunctionName is already in unique form
        //i.e. topLevelFunctionName$textualLocalName$intIndex
        //todoBI with Johnsson style lambda lifting, we should not alter the name at all
        //since it may be used in the defining expression of the function.
        return "$localSC$" + localFunctionName + "$"+ localFunctionLiftCount;                    
    }
    
    String getNextLiftedCaseName() {
        ++caseLiftCount;        
        return "$case$" + topLevelFunctionName + "$" + caseLiftCount;        
    }   
    
    String getNextLiftedDCFieldSelectionName() {
        ++selectionLiftCount;        
        return "$select$" + topLevelFunctionName + "$" + selectionLiftCount;
    }
    
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("Lambda lifting for function " + topLevelFunctionName + "\n");
        result.append("\tnumber of local functions lifted = " + localFunctionLiftCount + "\n");
        result.append("\tnumber of lambda definitions lifted = " + lambdaLiftCount + "\n");        
        result.append("\tnumber of case expressions lifted = " + caseLiftCount + "\n");  
        return result.toString();                                  
    }
}
