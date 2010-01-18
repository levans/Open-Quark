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
 * ClassMethod.java
 * Creation date: (April 4, 2001)
 * By: Bo Ilic
 */
package org.openquark.cal.compiler;

import java.io.IOException;

import org.openquark.cal.internal.serialization.ModuleSerializationTags;
import org.openquark.cal.internal.serialization.RecordInputStream;
import org.openquark.cal.internal.serialization.RecordOutputStream;
import org.openquark.cal.internal.serialization.RecordInputStream.RecordHeaderInfo;


/**
 * ClassMethod models a method in a type class. In other words, it has a name and a type, but not
 * a definition.
 * <p>
 * Some examples of class methods are Prelude.equals, Prelude.lessThan, Prelude.typeOf, and Debug.show.
 * 
 * @author Bo Ilic
 */
public final class ClassMethod extends FunctionalAgent{
    
    private static final int serializationSchema = 0;
    
    /** the 0-indexed method number of the method within the type class in which it is defined. */
    private int ordinal;
    
    /**
     * the index for the class method in the dictionary of its type class. This has a special value
     * of -1 if the class method is the single class method of a type class with no ancestors.
     * Otherwise it is equal to the number of ancestors classes (i.e. including grandparents etc) +
     * the ordinal of the method. 
     */
    private int dictionaryIndex;
    
    /**
     * the index of the class type var in the ordered list of overloaded record and type
     * vars in the class method's type. This is used during overload resolution.
     */
    private int classTypeVarPolymorphicIndex;
    
    /**
     * the name of the default class method i.e. the function to use for the class method at a particular instance
     * if the instance does not provide its own definition. May be null, in which case there is no default for the
     * method.
     * 
     * Note that this is the name of a FuncitionEntity (and cannot be a DataConstructor or ClassMethod itself).
     */
    private QualifiedName defaultClassMethodName; 
             
    /**
     * Construct a ClassMethod.    
     *   
     * @param methodName the name of the class method
     * @param scope the scope of the class method     
     * @param namedArguments the explicitly named arguments of the class method (can be null).
     * @param typeExpr the type of the class method     
     * @param ordinal the 0-indexed method number of the method within the type class in which it is defined.
     * @param dictionaryIndex the index of the class method in the dictionary for the type class.
     * @param classTypeVarPolymorphicIndex the index of the class type var in the ordered list of overloaded record and type
     *      vars in the class method's type. This is used during overload resolution.
     * @param defaultClassMethodName  the name of the default class method i.e. the function to use for the class
     *       method at a particular instance if the instance does not provide its own definition. May be null,
     *        in which case there is no default for the method.
     */
    ClassMethod(
        QualifiedName methodName,
        Scope scope,
        String[] namedArguments,
        TypeExpr typeExpr,
        int ordinal,
        int dictionaryIndex,   
        int classTypeVarPolymorphicIndex,
        QualifiedName defaultClassMethodName) {
         
        super(methodName, scope, namedArguments, typeExpr, null); 
        
        if (!LanguageInfo.isValidClassMethodName(methodName.getUnqualifiedName())) {
            throw new IllegalArgumentException("ClassMethod constructor: the argument 'methodName' is invalid.");             
        } 
        
        if (classTypeVarPolymorphicIndex < 0) {
            throw new IllegalArgumentException("classTypeVarPolymorphicIndex must be non-negative");
        }
        
        this.ordinal = ordinal;
        this.dictionaryIndex = dictionaryIndex;
        this.classTypeVarPolymorphicIndex = classTypeVarPolymorphicIndex;
        this.defaultClassMethodName = defaultClassMethodName;
    }  
    
    /** Zero argument constructor for serialization. */
    private ClassMethod() {
        
    }
    
    /**
     * Returns the kind of entity this is. "Form" is used as a synonym for "kind" or "type"
     * because of the overloaded meanings of the last 2 terms in the type checker!
     * Creation date: (6/4/01 1:49:57 PM)
     * @return FunctionalAgent.Form
     */
    @Override
    public final FunctionalAgent.Form getForm() {
        return FunctionalAgent.Form.CLASS_METHOD;
    }     
    
    /**
     * @return the 0-indexed method number of the method within the type class in which it is defined.
     */
    public final int getOrdinal() {
        return ordinal;
    }

    /**
     * Note the dictionayIndex is for internal compiler use only.   
     * @return the index for the class method in the dictionary of its type class. This has a special value
     * of -1 if the class method is the single class method of a type class with no ancestors.
     * Otherwise it is equal to the number of ancestors classes (i.e. including grandparents etc) +
     * the ordinal of the method. 
     */
    public final int internal_getDictionaryIndex() {
       return dictionaryIndex; 
    }
    
    /**
     * For internal compiler use only.     
     * @return the index of the class type var in the ordered list of overloaded record and type
     *      vars in the class method's type. This is used during overload resolution.
     */
    int getClassTypeVarPolymorphicIndex() {
        return classTypeVarPolymorphicIndex;
    }
    
    /**     
     * @return true if this class method has a default. In particular, what this means is that an instance is not required to
     *   implement an instance method for this class method.
     */
    public final boolean hasDefaultClassMethod() {
        return defaultClassMethodName != null;
    }
    
    /**
     * the name of the default class method i.e. the function to use for the class method at a particular instance
     * if the instance does not provide its own definition. May be null, in which case there is no default for the
     * method.
     * 
     * Note that this is the name of a FuncitionEntity (and cannot be a DataConstructor or ClassMethod itself).
     */    
    public final QualifiedName getDefaultClassMethodName() {
        return defaultClassMethodName;
    }
    
    /**
     * Write an instance of ClassMethod to the RecordOutputStream.
     * @param s
     * @throws IOException
     */
    @Override
    final void write (RecordOutputStream s) throws IOException {
        s.startRecord(ModuleSerializationTags.CLASS_METHOD, serializationSchema);
        super.writeContent(s);
        // Can't compress since dictionaryIndex can be < 0.
        s.writeShort(dictionaryIndex);
        s.writeShortCompressed(ordinal); 
        s.writeShortCompressed(classTypeVarPolymorphicIndex);
        s.writeQualifiedName(defaultClassMethodName);        
        s.endRecord();
    }
    
    /**
     * Load an instance of ClassMethod from the RecordInputStream.
     * Read position will be before the record header.
     * @param s
     * @param mti
     * @param msgLogger the logger to which to log deserialization messages.
     * @return an instance of ClassMethod.
     * @throws IOException
     */
    static final ClassMethod load (RecordInputStream s, ModuleTypeInfo mti, CompilerMessageLogger msgLogger) throws IOException {
        // Look for Record header.
        RecordHeaderInfo rhi = s.findRecord(ModuleSerializationTags.CLASS_METHOD);
        if(rhi == null) {
           throw new IOException("Unable to find ClassMethod record header.");
        }
        DeserializationHelper.checkSerializationSchema(rhi.getSchema(), serializationSchema, mti.getModuleName(), "ClassMethod", msgLogger);

        ClassMethod cm = new ClassMethod();
        try {
            cm.read(s, mti, msgLogger);
        } catch (IOException e) {
            // Add context to the error message.
            QualifiedName classMethodName = cm.getName();
            throw new IOException ("Error loading ClassMethod " + (classMethodName == null ? "" : classMethodName.getQualifiedName()) + ": " + e.getLocalizedMessage());
        }
        return cm;
    }
    
    /**
     * Load the content of an instance of ClassMethod from the RecordInputStream.
     * Read position will be after the record header.
     * @param s
     * @param mti    
     * @param msgLogger the logger to which to log deserialization messages.
     * @throws IOException
     */
    private final void read (RecordInputStream s, ModuleTypeInfo mti, CompilerMessageLogger msgLogger) throws IOException {

        super.readContent(s, mti, msgLogger);
        
        dictionaryIndex = s.readShort();
        ordinal = s.readShortCompressed();
        classTypeVarPolymorphicIndex = s.readShortCompressed();
        defaultClassMethodName = s.readQualifiedName();
        
        s.skipRestOfRecord();
    }
    
    /** {@inheritDoc} */
    @Override
    public String toString() {
        if (defaultClassMethodName != null) {
            return new StringBuilder(super.toString()).append(" default ").append(defaultClassMethodName).toString();
        }
        return super.toString();
    }
}