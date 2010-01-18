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
 * Scope.java
 * Creation date: Dec 22, 2005.
 * By: Joseph Wong
 */
package org.openquark.cal.compiler;

import java.io.IOException;

import org.openquark.cal.internal.serialization.ModuleSerializationTags;
import org.openquark.cal.internal.serialization.RecordInputStream;
import org.openquark.cal.internal.serialization.RecordOutputStream;
import org.openquark.cal.internal.serialization.RecordInputStream.RecordHeaderInfo;


/**
 * Models the scope modifier in CAL. Currently allowed scopes are private, protected and public.
 * entity scope enum pattern.
 * Creation date: (6/4/01 1:12:46 PM)
 * @author Bo Ilic
 */
public final class Scope implements Comparable<Scope> {
    
    private static final int serializationSchema = 0;
    
    private static final int PRIVATE_ORDINAL = 0;
    private static final int PROTECTED_ORDINAL = 1;
    private static final int PUBLIC_ORDINAL = 2;
    
    private final String name;
    private final int ordinal;
    
    /**
     * private entities are useable only within their module of definition.
     */
    public static final Scope PRIVATE = new Scope("private", Scope.PRIVATE_ORDINAL);
    
    /**
     * protected entities are useable with their module of definition, as well as within friend modules.
     */
    public static final Scope PROTECTED = new Scope("protected", Scope.PROTECTED_ORDINAL);
    
    /**
     * public entities are useable with their module of definition, as well as in modules importing
     * the module defining the entity.
     */
    public static final Scope PUBLIC = new Scope("public", Scope.PUBLIC_ORDINAL);
                                    
    private Scope(String name, int ordinal) {
        this.name = name;
        this.ordinal = ordinal;
    }

    /**
     * @return The CAL source form of the scope. Currently one of "private", "protected" or "public".
     */
    @Override
    public String toString() {
        return name;
    }
    
    public boolean isPublic() {
        return this == PUBLIC;
    }
    
    public boolean isProtected() {
        return this == PROTECTED;
    }
    
    public boolean isPrivate() {
        return this == PRIVATE;
    }
    
    @Override
    public int hashCode() {
        //note: it is not necessary to override equals, since Object.equals is correct and efficient for a type-safe enum pattern.
        //however, overriding hashCode is efficient.
        return ordinal;
    }
    
    /**
     * The ordering is defined by PRIVATE &lt; PROTECTED &lt; PUBLIC.
     * {@inheritDoc}
     */
    public int compareTo(Scope other) {            
        int otherOrdinal = other.ordinal;
        return ordinal < otherOrdinal ? -1 : (ordinal == otherOrdinal ? 0 : 1);            
    }
    
    /**                 
     * @return the most restrictive of the 2 scopes. i.e. the minimum of this scope and the other scope.
     */
    Scope min(Scope other) {
        if (this.compareTo(other) <= 0) {
            return this;
        } else {
            return other;
        }                
    }

    /**
     * Write this Scope instance to the RecordOutputStream.
     * @param s
     * @throws IOException
     */
    final void write (RecordOutputStream s) throws IOException {
        s.startRecord(ModuleSerializationTags.SCOPE, serializationSchema);
        s.writeByte(ordinal);
        s.endRecord();
    }
    
    /**
     * Load a Scope instance from the record input stream.
     * The read position is after the record header.
     * @param s
     * @param moduleName the name of the module being loaded
     * @param msgLogger the logger to which to log deserialization messages.
     * @return a Scope instance.
     * @throws IOException
     */
    static final Scope load (RecordInputStream s, ModuleName moduleName, CompilerMessageLogger msgLogger) throws IOException {
        RecordHeaderInfo rhi = s.findRecord(ModuleSerializationTags.SCOPE);
        if (rhi == null) {
            throw new IOException ("Unable to find Scope record header.");
        }
        DeserializationHelper.checkSerializationSchema(rhi.getSchema(), serializationSchema, moduleName, "Scope", msgLogger);
        
        byte ordinalValue = s.readByte();
        s.skipRestOfRecord();
        
        switch (ordinalValue)
        {
            case PRIVATE_ORDINAL:
                return PRIVATE;
                
            case PROTECTED_ORDINAL:
                return PROTECTED;
                
            case PUBLIC_ORDINAL:
                return PUBLIC;
        }
                               
        throw new IOException ("Unknown scope ordinal: " + ordinalValue);
    }             
}