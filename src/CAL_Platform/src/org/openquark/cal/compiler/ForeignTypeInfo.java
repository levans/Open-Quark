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
 * ForeignTypeInfo.java
 * Created: Oct 22, 2003
 * By: Bo Ilic
 */
package org.openquark.cal.compiler;

import java.io.IOException;

import org.openquark.cal.internal.javamodel.JavaTypeName;
import org.openquark.cal.internal.serialization.ModuleSerializationTags;
import org.openquark.cal.internal.serialization.RecordInputStream;
import org.openquark.cal.internal.serialization.RecordOutputStream;
import org.openquark.cal.internal.serialization.RecordInputStream.RecordHeaderInfo;


/**
 * Provides information about the Java type corresponding to a CAL type via a foreign data declaration.
 * 
 * @author Bo Ilic
 */
public final class ForeignTypeInfo {
    
    private static final int serializationSchema = 0;
    
    /** CAL type name corresponding to this foreign type */
    private final QualifiedName calName;
    
    /** Provider for the Class object corresponding to this type if it was defined using a foreign data declaration. */
    private final ForeignEntityProvider<Class<?>> foreignTypeProvider;    
    
    /** 
     * Determines if foreign functions with a signature involving the given foreign type can be declared
     * outside the module in which the foreign type was defined. In effect, is the foreign type an abstract data
     * type from the point of view of module clients, or is it known to be implemented as a foreign type
     * corresponding to a particular Java class.
     */
    private final Scope implementationVisibility;
    

    ForeignTypeInfo(QualifiedName calName, ForeignEntityProvider<Class<?>> foreignTypeProvider, Scope implementationVisibility) {
        
        if (calName == null || foreignTypeProvider == null || implementationVisibility == null) {
            throw new NullPointerException();
        }
        
        this.calName = calName;
        this.foreignTypeProvider = foreignTypeProvider;
        this.implementationVisibility = implementationVisibility;
    }

    /**
     * @return CAL type name corresponding to this foreign type e.g. "Cal.Core.Prelude.Integer"
     */
    public QualifiedName getCalName() {
        return calName;
    }

    /**
     * @return the Class object corresponding to this type if it was defined using a foreign data declaration.
     *    For example, for Prelude.Integer this would be java.lang.BigInteger.class.
     *    For Prelude.Int this would be int.class.
     */
    public Class<?> getForeignType() throws UnableToResolveForeignEntityException {
        return foreignTypeProvider.get();
    }
    
    /**    
     * @return true if the underlying Java type is one of the Java primitive types 
     *     char, boolean, byte, short, int, long, float or double.
     */
    public boolean isJavaPrimitiveType() throws UnableToResolveForeignEntityException {
        return getForeignType().isPrimitive();
    }

    /**
     * @return Determines if foreign functions with a signature involving the given foreign type can be declared
     * outside the module in which the foreign type was defined. In effect, is the foreign type an abstract data
     * type from the point of view of module clients, or is it known to be implemented as a foreign type
     * corresponding to a particular Java class.
     */
    public Scope getImplementationVisibility() {
        return implementationVisibility;
    }
    
    @Override
    public String toString() {
        String foreignTypeCalSourceName;
        try {
            foreignTypeCalSourceName = getCalSourceName(getForeignType());
        } catch (UnableToResolveForeignEntityException e) {
            // couldn't resolve the type... we will use the provider's display string
            foreignTypeCalSourceName = foreignTypeProvider.toString();
        }
        return implementationVisibility + " " + foreignTypeCalSourceName;
    }
    
    /**
     * The name of the type, in a canonical form suitable for appearing in CAL source in a foreign data declaration.
     * This is similar to what is returned by getFullJavaSourceName(Class) except that inner classes are separated by a $.
     * 
     * For example, it will return:
     * java.lang.String
     * java.util.Map$Entry
     * int
     * void
     * int[]
     * java.lang.String[]
     * 
     * @param type
     * @return a canonical form of the Java type name, suitable for use in a CAL source file.
     */    
    public final static String getCalSourceName(Class<?> type) {
        return JavaTypeName.getCalSourceName(type);
    }
        

    /**
     * Write an instance of ForeignTypeInfo to the RecordOutputStream.
     * @param s
     * @throws IOException
     */
    final void write (RecordOutputStream s) throws IOException, UnableToResolveForeignEntityException {
        s.startRecord(ModuleSerializationTags.FOREIGN_TYPE_INFO, serializationSchema);
        s.writeQualifiedName(calName);
        implementationVisibility.write(s);
        s.writeUTF(getForeignType().getName());
        s.endRecord();
    }
    
    /**
     * Load an instance of ForeignTypeInfo.
     * @param s
     * @param moduleName the name of the module being loaded
     * @param foreignClassLoader the classloader to use to resolve foreign classes.
     * @param msgLogger the logger to which to log deserialization messages.
     * @return an instance of ForeignTypeInfo.
     * @throws IOException
     */
    final static ForeignTypeInfo load (RecordInputStream s, ModuleName moduleName, final ClassLoader foreignClassLoader, CompilerMessageLogger msgLogger) throws IOException {
        RecordHeaderInfo rhi = s.findRecord(ModuleSerializationTags.FOREIGN_TYPE_INFO);
        if (rhi == null) {
            throw new IOException("Unable to find record header for ForeignTypeInfo.");
        }
        DeserializationHelper.checkSerializationSchema(rhi.getSchema(), serializationSchema, moduleName, "ForeignTypeInfo", msgLogger);

        final QualifiedName calName = s.readQualifiedName();
        Scope implementationVisibility = Scope.load(s, moduleName, msgLogger);
        final String className = s.readUTF();
        try {
            ForeignEntityProvider<Class<?>> classProvider = DeserializationHelper.classProviderForName(className, foreignClassLoader, "ForeignTypeInfo", CompilerMessage.Identifier.makeTypeCons(calName), msgLogger);
            return new ForeignTypeInfo (calName, classProvider, implementationVisibility);
        } finally {
            s.skipRestOfRecord();
        }
    }     
}
