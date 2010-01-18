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
 * ModuleSourceMetrics.java
 * Creation date: (Apr 14, 2005)
 * By: Jawright
 */
package org.openquark.cal.compiler;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.openquark.cal.internal.serialization.ModuleSerializationTags;
import org.openquark.cal.internal.serialization.RecordInputStream;
import org.openquark.cal.internal.serialization.RecordOutputStream;
import org.openquark.cal.internal.serialization.RecordInputStream.RecordHeaderInfo;


/**
 * This class contains source metrics associated with a single module.  It
 * aggregates the raw metric data stored in the FunctionEntities of a module.
 * 
 * Currently, only statistics that "occur" in the module are recorded; those that
 * occur in visible modules are not.  So, for example, the reference frequency of 
 * Prelude.add in module Foo refers only to the number of times gems in Foo refer to
 * Prelude.add; references in module Bar are not included, even if Foo includes Bar.  
 * 
 * Creation date: (Apr 14, 2005)
 * @author Jawright
 */
class ModuleSourceMetrics {

    // Serialization schema for use with record stream serialization.
    private static final int serializationSchema = 0;
    
    /** The ModuleTypeInfo object describing the module that this object holds metrics for */
    private ModuleTypeInfo moduleTypeInfo;
    
    /**
     * Map from QualifiedName of a gem K (visible from the current module) to the number 
     * of references to K that occur within functions defined in the current module.
     */
    private final Map<QualifiedName, Integer> gemToLocalReferenceFrequencyMap = new HashMap<QualifiedName, Integer>();
    
    /**
     * Construct a ModuleSourceMetrics object for the module represented
     * by moduleTypeInfo.
     * NOTE: This constructor assumes that the module and its imports have already been
     * successfully compiled.
     * @param moduleTypeInfo ModuleTypeInfo for a module to compute
     */
    ModuleSourceMetrics(ModuleTypeInfo moduleTypeInfo) {
        if (moduleTypeInfo == null) {
            throw new NullPointerException("moduleTypeInfo must not be null in ModuleSourceMetrics constructor");
        }
        
        this.moduleTypeInfo = moduleTypeInfo;
        computeLocalReferenceMetrics();
    }

    /**
     * Parameterless construction for the static load method
     *
     */
    private ModuleSourceMetrics() {
    }
    
    /**
     * Use the raw data contained in the EnvEntities of the current module to
     * fill gemToLocalReferenceFrequencyMap.
     */
    private void computeLocalReferenceMetrics() {
        
        int numFunctions = moduleTypeInfo.getNFunctions();
        for (int i = 0; i < numFunctions; i++) {

            Map<QualifiedName, Integer> dependeeToFrequencyMap = moduleTypeInfo.getNthFunction(i).getDependeeToFrequencyMap();
            for (final Map.Entry<QualifiedName, Integer> entry : dependeeToFrequencyMap.entrySet()) {
                QualifiedName dependeeName = entry.getKey();
                Integer newCount = entry.getValue();
                Integer existingCount = gemToLocalReferenceFrequencyMap.get(dependeeName);
            
                if (existingCount != null) {
                    gemToLocalReferenceFrequencyMap.put(dependeeName, Integer.valueOf(existingCount.intValue() + newCount.intValue()));
                } else {
                    gemToLocalReferenceFrequencyMap.put(dependeeName, newCount);
                }
            }
        }
    }

    /**
     * @return Map from the name of a visible gem to the number
     * of times that it is referenced in the body of some function in this module.
     */
    Map<QualifiedName, Integer> getGemToLocalReferenceFrequencyMap() {
        return Collections.unmodifiableMap(gemToLocalReferenceFrequencyMap);
    }

    void write(RecordOutputStream s) throws IOException {
        s.startRecord(ModuleSerializationTags.MODULE_SOURCE_METRICS, serializationSchema);
        s.writeInt(gemToLocalReferenceFrequencyMap.size());
        for (final QualifiedName key : gemToLocalReferenceFrequencyMap.keySet()) {
            Integer frequency = gemToLocalReferenceFrequencyMap.get(key);
            
            s.writeQualifiedName(key);
            s.writeInt(frequency.intValue());
        }
        s.endRecord();
    }
    
    static ModuleSourceMetrics load(RecordInputStream s, ModuleTypeInfo moduleTypeInfo, CompilerMessageLogger msgLogger) throws IOException {
        // Look for Record header.
        RecordHeaderInfo rhi = s.findRecord(ModuleSerializationTags.MODULE_SOURCE_METRICS);
        if(rhi == null) {
           throw new IOException("Unable to find ForeignFunctionInfo record header.");
        }
        DeserializationHelper.checkSerializationSchema(rhi.getSchema(), serializationSchema, moduleTypeInfo.getModuleName(), "ModuleSourceMetrics", msgLogger);
        
        ModuleSourceMetrics metrics = new ModuleSourceMetrics();
        
        // moduleTypeInfo
        metrics.moduleTypeInfo = moduleTypeInfo;
        
        // gemToLocalReferenceFrequencyMap
        int numReferenceFrequencyEntries = s.readInt();
        for (int i = 0; i < numReferenceFrequencyEntries; i++) {
            QualifiedName key = s.readQualifiedName();
            Integer frequency = Integer.valueOf(s.readInt());
            
            metrics.gemToLocalReferenceFrequencyMap.put(key, frequency);
        }
        
        s.skipRestOfRecord();
        
        return metrics;
    }   
}
