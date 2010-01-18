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
 * ScopedEntityImpl.java
 * Creation date (Oct 23, 2002).
 * By: Bo Ilic
 */

package org.openquark.cal.compiler;

import java.io.IOException;

import org.openquark.cal.internal.serialization.ModuleSerializationTags;
import org.openquark.cal.internal.serialization.RecordInputStream;
import org.openquark.cal.internal.serialization.RecordOutputStream;
import org.openquark.cal.internal.serialization.RecordInputStream.RecordHeaderInfo;


/**
 * Models an entity in CAL with a qualified name and a scope.
 * Examples are type classes, functions, type constructors, class methods,
 * data constructors and functions.
 * <P>
 * Creation date (Oct 23, 2002).
 * @author Bo Ilic
 */
public abstract class ScopedEntity 
        {

    private static final int serializationSchema = 0;
    
   /** the name of the entity. */
    private QualifiedName name;
       
    /** the visibility of the entity within the module system. */
    private Scope scope;
    
    /** The CALDoc comment for this function, or null if there is none. */
    private CALDocComment calDocComment;
    
    /**
     * Construct a ScopedEntity.    
     *   
     * @param entityName the name of the entity
     * @param scope the scope of the entity
     * @param calDocComment the CALDoc comment associated with the entity, or null if there is none.          
     */
    ScopedEntity(QualifiedName entityName, Scope scope, CALDocComment calDocComment) {
       
        if (entityName == null || scope == null) {
            throw new NullPointerException ("ScopedEntityImpl constructor: the arguments 'entityName' and 'scope' cannot be null.");
        }
                                                                                                          
        this.name = entityName;
        this.scope = scope;
        this.calDocComment = calDocComment;
    }
    
    /** Zero argument constructor used for serialization. */
    ScopedEntity () {}
    
    /**     
     * @return QualifiedName the name of the entity. In the case where an entity has
     *    a textual name and an operator name (such as Prelude.add or +), the name will 
     *    be the textual name. Will never be null.
     */
    public final QualifiedName getName() {
        return name;
    }
    
    /**
     * @param namingPolicy
     * @return a String representing the name of the entity with respect to a particular naming policy.
     *     For example, one such naming policy might be to always return the fully qualified name.
     */
    public final String getAdaptedName(ScopedEntityNamingPolicy namingPolicy) {
        return namingPolicy.getName(this);
    }
       
    /**     
     * @return Scope the scope of the entity. Will never be null.
     */
    public final Scope getScope() {
        return scope;
    }  

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();       
        sb.append(scope.toString()).append(' ').append(name.getQualifiedName());
       
        return sb.toString();
    }  
    
    /**
     * @return the CALDoc comment for this scoped entity, or null if there is none.
     */
    public final CALDocComment getCALDocComment() {
        return calDocComment;
    }
    
    /**
     * Set the CALDoc comment associated with this scoped entity. Note that the CALDoc comment
     * cannot be set more than once.
     * 
     * @param comment the CALDoc comment for this scoped entity, or null if there is none.
     */
    final void setCALDocComment(CALDocComment comment) {
        if (calDocComment != null) {
            throw new IllegalStateException(); 
        }
        calDocComment = comment;
    }
    
    /**
     * @return whether this scoped entity is deprecated (via a CALDoc deprecated block).
     */
    public final boolean isDeprecated() {
        if (calDocComment == null) {
            return false;
        } else {
            return calDocComment.getDeprecatedBlock() != null;
        }
    }
    
    /**
     * Implemented by concrete classes to serialize to a RecordOutputStream.
     * @param s
     * @throws IOException
     */
    abstract void write (RecordOutputStream s) throws IOException;

    /**
     * Serialize the content owned by this class to a RecordOutputStream.
     * @param s
     * @throws IOException
     */
    void writeContent (RecordOutputStream s) throws IOException {
        s.startRecord(ModuleSerializationTags.SCOPED_ENTITY, serializationSchema);
        s.writeQualifiedName(name);
        scope.write(s);
        
        // CALDoc comment
        boolean hasCALDocComment = (calDocComment != null);
        s.writeBoolean(hasCALDocComment);
        if (hasCALDocComment) {
            calDocComment.write(s);
        }
        
        s.endRecord();
    }
    
    /**
     * Read the content of this ScopedEntity instance from the RecordInputStream.
     * The read position in the stream will be before the record header.
     * @param s
     * @param mti
     * @param msgLogger the logger to which to log deserialization messages.
     * @throws IOException
     */
    void readContent (RecordInputStream s, ModuleTypeInfo mti, CompilerMessageLogger msgLogger) throws IOException {
        // Look for record header.
        RecordHeaderInfo rhi = s.findRecord(ModuleSerializationTags.SCOPED_ENTITY);
        if (rhi == null) {
            throw new IOException ("Unable to find ScopedEntity record header.");
        }
        DeserializationHelper.checkSerializationSchema(rhi.getSchema(), serializationSchema, mti.getModuleName(), "ScopedEntity", msgLogger);
        
        name = s.readQualifiedName();
        scope = Scope.load(s, mti.getModuleName(), msgLogger);
        
        // CALDoc comment
        boolean hasCALDocComment = s.readBoolean();
        if (hasCALDocComment) {
            calDocComment = CALDocComment.load(s, mti.getModuleName(), msgLogger);
        }
        
        s.skipRestOfRecord();
    }
}
