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
 * LocalFunctionIdentifier.java
 * Creation date: (Mar 10, 2006)
 * By: James Wright
 */
package org.openquark.cal.compiler;

import java.io.IOException;

import org.openquark.cal.internal.serialization.ModuleSerializationTags;
import org.openquark.cal.internal.serialization.RecordInputStream;
import org.openquark.cal.internal.serialization.RecordOutputStream;
import org.openquark.cal.internal.serialization.RecordInputStream.RecordHeaderInfo;


/**
 * Represents the name of a local function, disambiguated by index within
 * a toplevel function.
 * <p>
 * The index is constructed by a pre-order traversal of a toplevel function's
 * local definitions.  ie, we process all local function names in a let expression,
 * then we recursively process each local function in order.  
 * So, for example, the following code:
 * <pre>  
 *   toplevelFunction =
 *       let
 *           foo =
 *               let
 *                   foo = 10;
 *                   bar = 20;
 *               in
 *                   foo + bar;
 *           
 *           bar =
 *               let
 *                   foo = 20;
 *                   bar = 25;
 *               in
 *                   foo + bar;
 *       in
 *           foo + bar;
 * </pre>          
 * produces the following LocalFunctionIdentifiers:
 * <pre>
 *   toplevelFunction =
 *       let
 *           toplevelFunction@foo@0 = 
 *               let
 *                   toplevelFunction@foo@1 = 10;
 *                   toplevelFunction@bar@1 = 20;
 *               in
 *                   toplevelFunction@foo@1 + toplevelFunction@bar@1;
 *           
 *           toplevelFunction@bar@0 = 
 *              let
 *                  toplevelFunction@foo@2 = 20;
 *                  toplevelFunction@bar@2 = 25;
 *              in
 *                  toplevelFunction@foo@2 + toplevelFunction@bar@2;
 *       in
 *           toplevelFunction@foo@0 + toplevelFunction@foo@0;
 * </pre>
 * @author James Wright
 */
public final class LocalFunctionIdentifier implements Comparable<LocalFunctionIdentifier> {

    private static final int serializationSchema = 0;

    /** Name of the toplevel function in which this local function occurs. */
    private final QualifiedName toplevelFunctionName;
    
    /** Name of the local function */
    private final String localFunctionName;
    
    /** 
     * 0-based index of local functions with this name in the current toplevel
     * function.  This is nearly always zero, because very few toplevel functions
     * contain local functions that have the same names as each other.
     */
    private final int index;
    
    /**
     * @param toplevelFunctionName QualifiedName of the toplevel function that the local function is defined within
     * @param localFunctionName String name of the local function
     * @param index pre-order index of the local function (see class-level comment for details)
     */
    LocalFunctionIdentifier(QualifiedName toplevelFunctionName, String localFunctionName, int index) {
        
        if(toplevelFunctionName == null || localFunctionName == null) {
            throw new NullPointerException("no null arguments allowed for LocalFunctionIdentifier's constructor");
        }
        
        if(index < 0) {
            throw new IllegalArgumentException("index must be >= 0");
        }
        
        this.toplevelFunctionName = toplevelFunctionName;
        this.localFunctionName = localFunctionName;
        this.index = index;
    }
    
    /** @return QualifiedName of the toplevel function that the local function was declared in */
    public QualifiedName getToplevelFunctionName() {
        return toplevelFunctionName;
    }
    
    /** @return name of the local function */
    public String getLocalFunctionName() {
        return localFunctionName;
    }
    
    /** @return pre-order index of the local function (details on this index are available in LocalFunctionIdentifier's class comment) */
    public int getIndex() {
        return index;
    }
    
    /** {@inheritDoc} */
    @Override
    public String toString() {
        return toplevelFunctionName + "@" + localFunctionName + "@" + index; 
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return toString().hashCode(); 
    }

    /** {@inheritDoc} */
    public int compareTo(LocalFunctionIdentifier other) {
        
        int topLevelRelationship = toplevelFunctionName.compareTo(other.toplevelFunctionName);
        if(topLevelRelationship != 0) {
            return topLevelRelationship;
        }
        
        int localNameRelationship = localFunctionName.compareTo(other.localFunctionName);
        if(localNameRelationship != 0) {
            return localNameRelationship;
        }
        
        if(index < other.index) {
            return -1;
        }
        
        if(index > other.index) {
            return 1;
        }
        
        return 0;
    }
    
    /** {@inheritDoc} */
    @Override
    public boolean equals(Object otherObj) {
        if(!(otherObj instanceof LocalFunctionIdentifier)) {
            return false;
        }
        
        return compareTo((LocalFunctionIdentifier)otherObj) == 0;
    }
    
    /**
     * Write this instance of LocalFunctionIdentifier to the RecordOutputStream.
     * @param s
     * @throws IOException
     */
    final void write (RecordOutputStream s) throws IOException {
        s.startRecord(ModuleSerializationTags.LOCAL_FUNCTION_IDENTIFIER, serializationSchema);
        
        s.writeIntCompressed(index);
        s.writeUTF(localFunctionName);
        s.writeQualifiedName(toplevelFunctionName);

        s.endRecord();
    }
    
    /**
     * Load an instance of LocalFunctionIdentifier from the RecordInputStream.
     * Read position will be before the record header.
     * @param s
     * @param moduleName String name of the module that we are reading the identifier for
     * @param msgLogger the logger to which to log deserialization messages.
     * @return an instance of LocalFunction, or null if there was a problem resolving classes.
     * @throws IOException
     */
    static final LocalFunctionIdentifier load (RecordInputStream s, ModuleName moduleName, CompilerMessageLogger msgLogger) throws IOException {
        int nErrorsBeforeLoad = msgLogger.getNErrors();
        
        try {
            // Look for Record header.
            RecordHeaderInfo rhi = s.findRecord(ModuleSerializationTags.LOCAL_FUNCTION_IDENTIFIER);
            if (rhi == null) {
                throw new IOException("Unable to find LocalFunction record header.");
            }
            DeserializationHelper.checkSerializationSchema(rhi.getSchema(), serializationSchema, moduleName, "LocalFunction", msgLogger);

            int index = s.readIntCompressed();
            String localFunctionName = s.readUTF();
            QualifiedName toplevelFunctionName = s.readQualifiedName();
            
            s.skipRestOfRecord();
            
            // Check if state would be inconsistent by looking for logger errors.
            if (msgLogger.getNErrors() > nErrorsBeforeLoad) {
                return null;
            }

            return new LocalFunctionIdentifier(toplevelFunctionName, localFunctionName, index); 
        
        } catch(IOException e) {
            throw new IOException("Error loading LocalFunctionIdentifier: " + e.getLocalizedMessage());
        }
    }
}
