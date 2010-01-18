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
 * DataConstructor.java
 * Created: Sept 16, 2002
 * By: Bo Ilic
 */
package org.openquark.cal.compiler;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openquark.cal.internal.serialization.ModuleSerializationTags;
import org.openquark.cal.internal.serialization.RecordInputStream;
import org.openquark.cal.internal.serialization.RecordOutputStream;
import org.openquark.cal.internal.serialization.RecordInputStream.RecordHeaderInfo;


/**
 * Represents data constructors in CAL. Data constructors are the uppercase symbols
 * introduced on the right hand side of a data declaration in CAL and represent a way
 * to construct a value of an algebraic data type.
 * 
 * @author Bo Ilic
 */
public final class DataConstructor extends FunctionalAgent {
       
    /** the 0-based ordinal within the type. For example, if data Season = Winter | Spring | Summer | Fall; then Summer has ordinal 2.*/
    private int ordinal;
    
    /** the i-th element of this array indicates whether the i-th data constructor arg has a strictness annotation */
    private boolean[] argStrictness;

    /** the names of the data constructor fields, for any fields which are named. 
     *  The length of this array should be the same as the length of the argStrictness array (ie. length == arity). */
    private FieldName[] fieldNames;
    
    /** (FieldName->Integer) map from field name to the index of the argument with that field name in this data constructor. */
    private final Map<FieldName, Integer> fieldNameToIndexMap = new HashMap<FieldName, Integer>();
    
    /** indicates whether any arguments are strict. Computed from argStrictness. DO NOT SERIALIZE. */
    transient private boolean hasStrictArgs;
     
    private static final int serializationSchema = 0;

    /**
     * Construct a DataConstructor. This must remain package scope. Construction of DataConstructors is controlled so that
     * only 1 DataConstructor object is created to represent a given data constructor i.e. DataConstructor objects can be compared for
     * equality using ==.
     *   
     * @param dataConsName the data constructor's name
     * @param scope the scope of the data constructor
     * @param fieldNames the names of the data constructor fields, for any fields which are named.
     *   Null array values for unnamed args.
     *   The array itself can be null if this data constructor has no arguments.
     * @param typeExpr the type of the data constructor
     * @param argStrictness indicates whether the corresponding argument is strict of not.
     * @param ordinal the 0-based ordinal within the type
     */

    DataConstructor(QualifiedName dataConsName, Scope scope, FieldName[] fieldNames,
                    TypeExpr typeExpr, boolean[] argStrictness, int ordinal) {
         
        super(dataConsName, scope, getArgumentNamesFromFieldNames(fieldNames), typeExpr, null);
                
        if (!isValidInternalDataConstructorName(dataConsName)) {
            throw new IllegalArgumentException("DataConstructor constructor: the argument 'dataConsName' is invalid.");             
        }
      
        this.ordinal = ordinal;
        this.argStrictness = argStrictness;
        
        int arity = argStrictness.length;        // throws an NPE if argStrictness is null.
        
        boolean shouldHaveStrictArgs = false;
        for (int i = 0; i < arity; ++i) {
            if (argStrictness[i]) {
                shouldHaveStrictArgs = true;
                break;
            }
        }
        this.hasStrictArgs = shouldHaveStrictArgs;
        
        if (fieldNames == null) {
            this.fieldNames = new FieldName[0];
            if (arity != 0) {
                throw new IllegalArgumentException("DataConstructor constructor: fieldNames is null, but data constructor has arguments.");
            }
        } else {
            int fieldNamesLength = fieldNames.length;
            this.fieldNames = new FieldName[fieldNamesLength];
            System.arraycopy(fieldNames, 0, this.fieldNames, 0, fieldNamesLength);
            
            if (arity != fieldNamesLength) {
                String errorMessage = "DataConstructor constructor: argStrictness.length != fieldNames.length " + 
                                      "(" + arity + " != " + fieldNamesLength + ")";
                throw new IllegalArgumentException(errorMessage);
            }
            for (int i = 0; i < fieldNamesLength; i++) {
                fieldNameToIndexMap.put(fieldNames[i], Integer.valueOf(i));
            }
        }
    }
    
    // Zero argument constructor used for serialization.
    private DataConstructor () {
        hasStrictArgs = false;
    }
    
    /**
     * Helper function to return "reasonable" argument names from an array of field names.
     * This is necessary because ordinal field names such as #1 are not valid argument names.
     * 
     * The argument name for a textual field name is simply its cal source form.
     * The argument name for an ordinal field name is "field"+ordinal, plus a disambiguating
     *   suffix if this causes a collision with an existing name.
     * 
     * For instance, 
     *   field names {foo, #1}      becomes  ["foo", "field1"]
     *   field names {field10, #10} becomes  ["field10", "field10_1"]
     * 
     * @param fieldNames an array of field names, or null for no field names.  Should not contain nulls.
     * @return null if fieldNames is null, otherwise an array of argument names, 
     *   where the argument name at index i corresponds to the ith field name in the array.
     */
    private static String[] getArgumentNamesFromFieldNames(FieldName[] fieldNames) {
        if (fieldNames == null) {
            return null;
        }
        
        // The set of argument names used so far.
        Set<String> argNamesSet = new HashSet<String>();
        
        String[] argumentNames = new String[fieldNames.length];
        
        // Iterate over the array, assigning argument names directly from the textual form of the field names.
        for (int i = 0; i < fieldNames.length; i++) {
            FieldName fieldName = fieldNames[i];

            if (fieldName instanceof FieldName.Textual) {
                String argName = fieldName.getCalSourceForm();
                argumentNames[i] = argName;

                if (!argNamesSet.add(argName)) {
                    throw new IllegalArgumentException("Duplicate field name: " + argName);
                }
            
            } else if (fieldName == null) {
                throw new NullPointerException("Null field name.");
            }
        }
        
        // Iterate again over the array, calculating argument names from the ordinal form of the field names.
        for (int i = 0; i < fieldNames.length; i++) {
            FieldName fieldName = fieldNames[i];

            if (fieldName instanceof FieldName.Ordinal) {
                FieldName.Ordinal ordinalFieldName = (FieldName.Ordinal)fieldName;
                
                // Calculate a base name.
                String baseName = "field" + ordinalFieldName.getOrdinal();
                
                // Disambiguate the name.
                String argName = baseName;
                int index = 1;
                while (!argNamesSet.add(argName)) {
                    argName = baseName + "_" + index;
                    index++;
                }
                
                // Assign.
                argumentNames[i] = argName;
            }
        }
        
        return argumentNames;
    }
    
    /**
     * Returns true if the identifier is a lexically valid CAL data constructor name or
     * a valid internal data constructor name.   
     * @param identifier
     * @return boolean true if the indentifier is a lexically valid CAL data constructor name.
     */
    static private boolean isValidInternalDataConstructorName (QualifiedName identifier) {
        String name = identifier.getUnqualifiedName();
        
        if (LanguageInfo.isValidDataConstructorName(name)) {
            return true;
        }
                    
        return name != null && name.startsWith("$");          
    }        
        
    /**
     * @see org.openquark.cal.compiler.FunctionalAgent#getForm()
     */
    @Override
    public FunctionalAgent.Form getForm() {
        return FunctionalAgent.Form.DATA_CONSTRUCTOR;
    }
     
    /**
     * Get the arity of this DataConstructor.
     * This is the number of arguments that this DataConstructor, considered as a function
     * accepts. For example, it is 2 for Cons (i.e. ":") and 0 for Nil (i.e. "[]").
     * Creation date: (3/20/01 1:45:23 PM)
     * @return int    
     */
    public int getArity() {  
        return argStrictness.length;  
        //the below is also correct, but less efficient    
        //return getTypeExpr().getNApplications();
    }
    
    /**
     * @param n index into field names.  This should be in the range [0, arity - 1].
     * @return the nth field name in this data constructor.  Null if the nth field is not named.
     */
    public FieldName getNthFieldName(int n) {
        return fieldNames[n];
    }
    

    /**
     * @param fieldName a field name
     * @return the 0-based index of the data constructor argument with that field name, 
     *   or -1 if this data constructor does not have a field with that name.
     */
    public int getFieldIndex(FieldName fieldName) {
        Integer fieldIndex = fieldNameToIndexMap.get(fieldName);
        if (fieldIndex != null) {
            return fieldIndex.intValue();
        }
        return -1;
    }
    
    /**     
     * The strictness annotations referred to here are indicated by ! in the data declaration source syntax.
     * @param argN 0-based index into the arguments of this data constructor
     * @return boolean whether the argN-th argument has a strictness annotation.
     */
    public final boolean isArgStrict(int argN) {
        return argStrictness[argN];    
    }

    /**
     * @return true if any of the arguments are strict.
     */
    public final boolean hasStrictArgs () {
        return hasStrictArgs;
    }
    
    /**
     * @return a boolean array where each value indicates the strictness of the corresponding argument.
     */
    public final boolean[] getArgStrictness () {
        boolean[] retVal = new boolean [argStrictness.length];
        System.arraycopy(argStrictness, 0, retVal, 0, retVal.length);
        return retVal;
    }
    
    /**
     * @return int the 0-based ordinal within the type.
     */
    public final int getOrdinal() {
        return ordinal;
    }              
    
    /**
     * Get the type constructor to which this data constructor belongs.
     * For example, for Prelude.Left, which has type a -> Either a b, this
     * will return "Either a b".
     * 
     * @return TypeConsApp
     */
    public final TypeConsApp getTypeConsApp() {
        return getTypeExpr().getResultType().rootTypeConsApp();
    }
    
    /**
     * Get the type constructor to which this data constructor belongs. 
     * For example, for Prelude.Left, which has type a -> Either a b, this
     * will return the type constructor "Either".    
     * 
     * @return TypeConstructor
     */
    public final TypeConstructor getTypeConstructor() {
        return getTypeConsApp().getRoot();
    }     
    
    /**
     * Write this DataConstructor to a RecordOutputStream.
     * @param s
     * @throws IOException
     */
    @Override
    final void write (RecordOutputStream s) throws IOException {
        s.startRecord(ModuleSerializationTags.DATA_CONSTRUCTOR, serializationSchema);
        super.writeContent(s);
        s.writeShortCompressed(ordinal);
        s.writeShortCompressed(argStrictness.length);
        byte[] bas = RecordOutputStream.booleanArrayToBitArray(argStrictness);
        s.write(bas);
        
        // write the field names
        for (final FieldName fieldName : fieldNames) {
            if (fieldName != null) {
                s.writeBoolean(true);
                FieldNameIO.writeFieldName(fieldName, s);
            
            } else {
                s.writeBoolean(false);
            }
        }
        
        s.endRecord();
    }
    
    /**
     * Load an instance of a DataConstructor from the RecordInputStream.
     * The read position will be before the record header.
     * @param s
     * @param mti
     * @param msgLogger the logger to which to log deserialization messages.
     * @return an instance of DataConstructor.
     * @throws IOException
     */
    static final DataConstructor load (RecordInputStream s, ModuleTypeInfo mti, CompilerMessageLogger msgLogger) throws IOException {
        // Look for Record header.
        RecordHeaderInfo rhi = s.findRecord(ModuleSerializationTags.DATA_CONSTRUCTOR);
        if(rhi == null) {
           throw new IOException("Unable to find DataConstructor record header.");
        }
        DeserializationHelper.checkSerializationSchema(rhi.getSchema(), serializationSchema, mti.getModuleName(), "DataConstructor", msgLogger);

        DataConstructor dc = new DataConstructor();
        try {
            dc.read(s, mti, msgLogger);
        } catch (IOException e) {
            // Add some context to the error message.
            QualifiedName dcName = dc.getName();
            throw new IOException ("Error loading DataConstructor " + (dcName == null ? "" : dcName.getQualifiedName()) + ": " + e.getLocalizedMessage());
        }
        return dc; 
    }
    
    /**
     * Read the content of a DataConstructor from the RecordInputStream.
     * The read position will be after the record header.
     * @param s
     * @param mti
     * @param msgLogger the logger to which to log deserialization messages.
     * @throws IOException
     */
    private void read (RecordInputStream s, ModuleTypeInfo mti, CompilerMessageLogger msgLogger) throws IOException {
        super.readContent(s, mti, msgLogger);
        
        ordinal = s.readShortCompressed();
        int nArgs = s.readShortCompressed();
        int nBytes = (nArgs + 7) /8;
        byte[] bas = new byte[nBytes];
        for (int i = 0; i < bas.length; ++i) {
            bas[i] = s.readByte();
        }
        argStrictness = RecordInputStream.bitArrayToBooleans(bas, nArgs);
        for (int i = 0; i < nArgs; ++i) {
            if(argStrictness[i]) {
                hasStrictArgs = true;
                break;
            }
        }
        
        // Read the field names.
        this.fieldNames = new FieldName[nArgs];
        for (int i = 0; i < nArgs; i++) {
            boolean hasFieldName = s.readBoolean();
            if (hasFieldName) {
                FieldName fieldName = FieldNameIO.load(s, mti.getModuleName(), msgLogger);
                fieldNames[i] = fieldName;
                fieldNameToIndexMap.put(fieldName, Integer.valueOf(i));
            }
        }
        
        s.skipRestOfRecord();
    }
}
