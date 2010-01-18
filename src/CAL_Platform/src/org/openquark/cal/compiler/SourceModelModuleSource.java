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
 * SourceModelModuleSource.java
 * Created: Nov 26, 2004
 * By: Bo Ilic
 */

package org.openquark.cal.compiler;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.openquark.cal.services.Status;
import org.openquark.util.TextEncodingUtilities;


/**
 * Constructs the source for a module from a SourceModel.
 * This should be used whenever you want to compile a SourceModel, because
 * the compiler will be able to bypass the parser and generate a parse tree
 * directly from the SourceModel.
 * @author Bo Ilic
 */
public class SourceModelModuleSource extends ModuleSourceDefinition {
    
    private final SourceModel.ModuleDefn moduleDefn;
    
    // Timestamp for the 'module source', this is used when determining if
    // previously generated compiled code is still valid or needs to 
    // be regenerated.
    private final long timeStamp;
    
    public SourceModelModuleSource(SourceModel.ModuleDefn moduleDefn) {
        super(SourceModel.Name.Module.toModuleName(moduleDefn.getModuleName()));     
        this.moduleDefn = moduleDefn;
        this.timeStamp = System.currentTimeMillis();
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.services.ModuleResource#getInputStream(org.openquark.cal.services.Status)
     */
    @Override
    public InputStream getInputStream(Status status) {     
        return new ByteArrayInputStream(TextEncodingUtilities.getUTF8Bytes(moduleDefn.toSourceText()));
    }
    
    ParseTreeNode getParseTreeNode() {
        return moduleDefn.toParseTreeNode();
    }

    /* (non-Javadoc)
     * @see org.openquark.cal.services.WorkspaceResource#getTimeStamp()
     */
    @Override
    public long getTimeStamp() {
        return timeStamp;
    }

    /** 
     * @return The underlying SourceModel.ModuleDefn for this module
     */
    public SourceModel.ModuleDefn getModuleDefn() {
        return moduleDefn;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDebugInfo() {
        return "constructed prorgammatically via a source model";
    }
}
