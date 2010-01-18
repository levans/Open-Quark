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
 * TriggerGemFilter.java
 * Created: 8-Nov-2006
 * By: Magnus Byne
 */

package org.openquark.samples.bam;

import org.openquark.cal.compiler.ModuleTypeInfo;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.metadata.ScopedEntityMetadata;
import org.openquark.cal.services.BasicCALServices;
import org.openquark.cal.services.GemEntity;



/**
 * This is used to find gems which can be used as metrics.
 *
 * @author Magnus Byne
 */
public class MetricGemFilter extends MonitorGemFilter {

    public static final String METRIC_ATTRIBUTE_NAME = "com.businessobjects.monitor.MonitorMetric";
    
    /**
     * this defines the signature that a gem must have to be suitable for use as a metric
     */
    private final TypeExpr requiredSignature;
    
    /**
     * get the type info for the BAM run module
     * @return BAm run module type info
     */
    static private ModuleTypeInfo getTargetModuleTypeInfo() {
        BasicCALServices calServices = MonitorApp.getInstance().getCalServices();
        return calServices.getCALWorkspace().getMetaModule(MonitorApp.TARGET_MODULE).getTypeInfo();
    }
    /**
     * Default constructor - filters for all gems that could be metrics
     * i.e. Gems of the form [a] -> [b] and have the appropriate meta data
     */
    public MetricGemFilter() {
        requiredSignature = TypeExpr.makeFunType( 
            TypeExpr.makeListType(TypeExpr.makeParametricType(), getTargetModuleTypeInfo()), 
            TypeExpr.makeListType(TypeExpr.makeParametricType(), getTargetModuleTypeInfo()));
    }
    
    /**
     * Constructs a filter that only includes metrics which can unify
     * with [inputType] -> [outputType] and have the appropriate meta data
     * @param inputType the input type that the metric must accept 
     * @param outputType the output type that the metric must create.
     */
    public MetricGemFilter(TypeExpr inputType, TypeExpr outputType) {
        requiredSignature = TypeExpr.makeFunType( 
            TypeExpr.makeListType(inputType, getTargetModuleTypeInfo()), 
            TypeExpr.makeListType(outputType, getTargetModuleTypeInfo()));
    }
  
    /**
     * This checks that a gem's type is unifiable with the required signature.
     * {@inheritDoc}
     */
    @Override
    protected boolean checkSignature (GemEntity gemEntity) {
        return TypeExpr.canUnifyType(requiredSignature, 
            gemEntity.getTypeExpr(), 
            getTargetModuleTypeInfo());            
    }
    
    /**
     * This checks that the gem has the correct Metric meta data
     * @see org.openquark.samples.bam.MonitorGemFilter#checkMetadata(org.openquark.cal.metadata.ScopedEntityMetadata)
     */
    @Override
    protected boolean checkMetadata (ScopedEntityMetadata metadata) {
        return hasAttribute(metadata, METRIC_ATTRIBUTE_NAME);
    }

}
