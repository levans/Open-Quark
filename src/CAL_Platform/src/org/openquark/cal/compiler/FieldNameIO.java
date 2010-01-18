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

package org.openquark.cal.compiler;

import java.io.IOException;

import org.openquark.cal.compiler.FieldName.Ordinal;
import org.openquark.cal.compiler.FieldName.Textual;
import org.openquark.cal.internal.serialization.ModuleSerializationTags;
import org.openquark.cal.internal.serialization.RecordInputStream;
import org.openquark.cal.internal.serialization.RecordOutputStream;
import org.openquark.cal.internal.serialization.RecordInputStream.RecordHeaderInfo;

/**
 * Handles serialization for the FieldName class.
 * 
 * @author Bo Ilic
 */
final class FieldNameIO {
    
    private FieldNameIO() {};
    
    /**
     * The array of possible record tags used in calls to {@link RecordInputStream#findRecord(short[])} by
     * the {@link #load} method.
     */
    private static final short[] FIELD_NAME_RECORD_TAGS = new short[] {
        ModuleSerializationTags.FIELD_NAME_ORDINAL,
        ModuleSerializationTags.FIELD_NAME_TEXTUAL
    };
    
    private static final int ordinalSerializationSchema = 0;
    
    private static final int textualSerializationSchema = 0;

    static void writeFieldName(FieldName fieldName, RecordOutputStream s) throws IOException {
        if (fieldName == null) {
            throw new NullPointerException("Can't serialize a null FieldName");
        }

        if (fieldName instanceof FieldName.Ordinal) {
            writeOrdinalFieldName((FieldName.Ordinal)fieldName, s);
        } else {
            writeTextualFieldName((FieldName.Textual)fieldName, s);
        }

    }

    private static void writeOrdinalFieldName (FieldName.Ordinal ordinalFieldName, RecordOutputStream s) throws IOException {
        s.startRecord(ModuleSerializationTags.FIELD_NAME_ORDINAL, ordinalSerializationSchema);
        s.writeShortCompressed(ordinalFieldName.getOrdinal());
        s.endRecord();
    }

    private static void writeTextualFieldName (FieldName.Textual textualFieldName, RecordOutputStream s) throws IOException {
        s.startRecord(ModuleSerializationTags.FIELD_NAME_TEXTUAL, textualSerializationSchema);
        s.writeUTF(textualFieldName.getCalSourceForm());
        s.endRecord();
    }

    /**
     * Load an instance of FieldName from the RecordInputstream>
     * Read position will be before the record header.
     * @param s
     * @param moduleName the name of the module being loaded
     * @param msgLogger the logger to which to log deserialization messages.
     * @return and instance of FieldName.
     * @throws IOException
     */
    static FieldName load (RecordInputStream s, ModuleName moduleName, CompilerMessageLogger msgLogger) throws IOException {
        RecordHeaderInfo rhi = s.findRecord(FieldNameIO.FIELD_NAME_RECORD_TAGS);
        if (rhi == null) {
            throw new IOException ("Unable to find FieldName record header.");
        }
        
        // Now that we have the record tag load the appropriate type of child.
        switch (rhi.getRecordTag()) {
            case ModuleSerializationTags.FIELD_NAME_ORDINAL:
            {
                return FieldNameIO.loadOrdinalFieldName(s, rhi.getSchema(), moduleName, msgLogger);
            }
            
            case ModuleSerializationTags.FIELD_NAME_TEXTUAL:
            {
                return FieldNameIO.loadTextualFieldName(s, rhi.getSchema(), moduleName, msgLogger);
            }
            
            default:
                throw new IOException("Unexpected record tag " + rhi.getRecordTag() + " encountered while loading FieldName.");
        }
    }

    /**
     * Load an instance of Textual from the RecordInputStream.
     * Read position will be directly after the record header.
     * @param s
     * @param schema
     * @param moduleName the name of the module being loaded
     * @param msgLogger the logger to which to log deserialization messages.
     * @return an instance of Textual
     * @throws IOException
     */
    final static FieldName.Textual loadTextualFieldName (RecordInputStream s, int schema, ModuleName moduleName, CompilerMessageLogger msgLogger) throws IOException {
        DeserializationHelper.checkSerializationSchema(schema, textualSerializationSchema, moduleName, "FieldName.Textual", msgLogger);
        String name = s.readUTF();
        s.skipRestOfRecord();
        return FieldName.makeTextualField(name);
    }

    /**
     * Load an instance of ordinal from the RecordInputStream.
     * Read position will be directly after the record header.
     * @param s
     * @param schema
     * @param moduleName the name of the module being loaded
     * @param msgLogger the logger to which to log deserialization messages.
     * @return an instance of Ordinal.
     * @throws IOException
     */
    final static FieldName.Ordinal loadOrdinalFieldName (RecordInputStream s, int schema, ModuleName moduleName, CompilerMessageLogger msgLogger) throws IOException {
        DeserializationHelper.checkSerializationSchema(schema, ordinalSerializationSchema, moduleName, "FieldName.Ordinal", msgLogger);
        int ord = s.readShortCompressed();
        s.skipRestOfRecord();
        return FieldName.makeOrdinalField(ord);
    }

}
