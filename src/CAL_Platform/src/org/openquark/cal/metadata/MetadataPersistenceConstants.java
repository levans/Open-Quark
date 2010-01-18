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
 * XMLPersistenceConstants.java
 * Creation date: (22-Jul-03 3:35:36 PM)
 * By: Frank Worsley
 */
package org.openquark.cal.metadata;

/**
 * Constants used when persisting metadata.
 * @author Frank Worsley
 */
final class MetadataPersistenceConstants {
    
    // Prevent this class from being instatiated
    private MetadataPersistenceConstants() {}

    /* Metadata namespace. */
    static final String METADATA_NS =                           "http://www.businessobjects.com/cal/metadata";
    static final String METADATA_NS_PREFIX =                    "metadata";
    
    /* Metadata schema. */
    static final String METADATA_SCHEMA_LOCATION =              "file:metadata.xsd";

    /* 
     * Top level (or almost top level) tag names
     */
    static final String CAL_METADATA_TAG =                      "calMetadata";
    static final String FEATURE_METADATA_TAG =                  "featureMetadata";
    static final String SCOPED_ENTITY_METADATA_TAG =            "scopedEntityMetadata";
    static final String FUNCTIONAL_AGENT_METADATA_TAG =         "functionalAgentMetadata";
    static final String MODULE_METADATA_TAG =                   "moduleMetadata";
    static final String FUNCTION_METADATA_TAG =                 "functionMetadata";
    static final String CLASS_METHOD_METADATA_TAG =             "classMethodMetadata";
    static final String DATA_CONSTRUCTOR_METADATA_TAG =         "dataConstructorMetadata";
    static final String TYPE_CLASS_METADATA_TAG =               "typeClassMetadata";
    static final String TYPE_CONSTRUCTOR_METADATA_TAG =         "typeConstructorMetadata";
    static final String ARGUMENT_METADATA_TAG =                 "argumentMetadata";
    static final String CLASS_INSTANCE_METADATA_TAG =           "classInstanceMetadata";
    static final String INSTANCE_METHOD_METADATA_TAG =          "instanceMethodMetadata";

    /*
     * Common value tag names
     */
    static final String FEATURE_NAME_TAG =                      "featureName";
    static final String DISPLAY_NAME_TAG =                      "displayName";
    static final String VERSION_TAG =                           "version";
    static final String DESCRIPTION_TAG =                       "description";
    static final String LONG_DESCRIPTION_TAG =                  "longDescription";
    static final String SHORT_DESCRIPTION_TAG =                 "shortDescription";
    static final String AUTHOR_TAG =                            "author";
    static final String CREATION_DATE_TAG =                     "creationDate";
    static final String MODIFICATION_DATE_TAG =                 "modificationDate";    
    static final String EXPERT_FEATURE_TAG =                    "expertFeature";    
    static final String HIDDEN_FEATURE_TAG =                    "hiddenFeature";
    static final String PREFERRED_FEATURE_TAG =                 "preferredFeature";

    /*
     * Section tag names
     */
    static final String GENERAL_SECTION_TAG =                   "general";
    static final String CATEGORIES_SECTION_TAG =                "categories";
    static final String EXAMPLES_SECTION_TAG =                  "examples";
    static final String ARGUMENTS_SECTION_TAG =                 "arguments";
    static final String SEE_ALSO_SECTION_TAG =                  "seeAlso";
    static final String ATTRIBUTES_SECTION_TAG =                "attributes";
    static final String EXPRESSION_SECTION_TAG =                "expression";
    static final String EXAMPLE_SECTION_TAG =                   "example";
    static final String ATTRIBUTE_SECTION_TAG =                 "attribute";
    static final String RELATED_FEATURES_SECTION_TAG =          "relatedFeatures";
    static final String RETURN_VALUE_TAG =                      "returnValue";
    
    /*
     * Argument specific tags
     */
    static final String ARGUMENT_DEFAULTS_ONLY_TAG =            "defaultValuesOnly";
    static final String ARGUMENT_DEFAULTS_EXPRESSION_TAG =      "defaultValuesExpression";
    static final String ARGUMENT_PROMPTING_EXPRESSION_TAG =     "promptingTextExpression";
    
    /*
     * Expression specific tags
     */
    static final String EXPRESSION_MODULE_CONTEXT_TAG =         "moduleContext";
    static final String EXPRESSION_CAL_TEXT_TAG =               "calExpressionText";
    static final String EXPRESSION_QUALIFIED_CAL_TEXT_TAG =     "qualifiedCalExpressionText";
    
    /*
     * Example specific tags
     */
    static final String EXAMPLE_EVALUATE_TAG =                  "evaluateExpression";
    
    /*
     * Attribute specific tags
     */
    static final String ATTRIBUTE_KEY_TAG =                     "key";
    static final String ATTRIBUTE_VALUE_TAG =                   "value";
}
