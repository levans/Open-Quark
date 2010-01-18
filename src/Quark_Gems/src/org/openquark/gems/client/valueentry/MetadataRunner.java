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
 * MetadataRunner.java
 * Created: Jan. 05 / 2003
 * By: David Mosimann
 */
package org.openquark.gems.client.valueentry;

import org.openquark.cal.valuenode.ValueNode;

/**
 * This interface allows the Gem Cutter to implement a mechanism to evaluate metadata
 * expressions.
 */
public interface MetadataRunner {

    /**
     * Evaluates a given metadata expressions to produce a value node.  If any errors are encountered
     * then a null value node is returned.  Implementors must correctly deal with arguments in the
     * metadata expression.
     * @param argN the argument number for which the default values metadata will be evaluated.
     * @return Returns the value of the expression or null if any error is encountered.
     */
    public ValueNode evaluateDefaultValueMetadata(int argN);
    
    /**
     * Checks the metadata to determine if only the default values should be used or if the user can
     * enter any value.
     * @param argN the argument number for which the use default values only metadata will be returned
     * @return true if only the default values provided from evaluateDefaultValueMetadata() should be
     * used and false if the user can specify any value in addition to the defaults.
     */
    public boolean useDefaultValuesOnly(int argN);
}