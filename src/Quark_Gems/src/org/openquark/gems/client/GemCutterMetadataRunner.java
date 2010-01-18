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
 * GemCutterMetadataRunner.java
 * Created: Jan. 05 / 2003
 * By: David Mosimann
 */
package org.openquark.gems.client;

import org.openquark.cal.valuenode.ValueNode;
import org.openquark.gems.client.valueentry.MetadataRunner;


/**
 * The Gem Cutter's implementation of the metadata runner.  This is used to evaluate metadata
 * particularly for pick lists used by the user supplied arguments and in the explorer tree.
 */
public class GemCutterMetadataRunner implements MetadataRunner {

    /**
     * Constructor for the Gem Cutter metadata runner
     * @param gemCutter the Gem Cutter main class
     * @param gem the gem for which metadata will be calculated
     */
    public GemCutterMetadataRunner(GemCutter gemCutter, Gem gem) {
        // args unused
    }

    /* (non-Javadoc)
     * @see org.openquark.gems.client.valueentry.MetadataRunner#evaluateMetadataExpression(org.openquark.cal.metadata.CALExpression)
     */
    public ValueNode evaluateDefaultValueMetadata(int argN) {
        // Always return null since Gem Cutter doesn't currently run the metadata.
        return null;
    }

    /**
     * Checks the metadata to determine if only the default values should be used or if the user can
     * enter any value.
     * @param argN
     * @return true if only the default values provided from evaluateDefaultValueMetadata() should be
     * used and false if the user can specify any value in addition to the defaults.
     */
    public boolean useDefaultValuesOnly(int argN) {
        // Always return false since we don't actually evaluate the metadata yet
        return false; 
    }
}