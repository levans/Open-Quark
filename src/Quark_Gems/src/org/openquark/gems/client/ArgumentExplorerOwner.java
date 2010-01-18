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
 * ArgumentExplorerOwner.java
 * Creation date: May 26, 2004.
 * By: Edward Lam
 */
package org.openquark.gems.client;

import org.openquark.cal.compiler.TypeExpr;
import org.openquark.gems.client.valueentry.ValueEditorManager;


/**
 * This class is used as an intermediatary between the Explorer representation of arguments and the owner frame of
 * the explorer.  Since the explorer is only a UI shell, all of the dirty work must be done by the owner.
 * @author Edward Lam
 */
public interface ArgumentExplorerOwner {
    /**
     * Returns a string that describes the specified input.
     * @param input the input to describe.
     * @return the description of the input.
     */
    String getHTMLFormattedMetadata(Gem.PartInput input);

    /**
     * Retarget an input argument to a new position or target.
     * @param argument the input argument to retarget.
     * @param newTarget the new target for the argument.
     * @param addIndex the index at which the argument will appear in the new target's reflected arguments.
     */
    void retargetInputArgument(Gem.PartInput argument, CollectorGem newTarget, int addIndex);

    /**
     * @return the manager used to create the value editor hierarchy manager for value editors in run mode.
     */
    ValueEditorManager getValueEditorManager();
    
    /**
     * @param typeExpr
     * @return the string representation of the type, using the appropriate naming policy and polymorphic var context.
     */
    String getTypeString(TypeExpr typeExpr);
}
