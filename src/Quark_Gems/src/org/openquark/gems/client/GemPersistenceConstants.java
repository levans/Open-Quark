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
 * GemPersistenceConstants.java
 * Creation date: (23-May-02 3:35:36 PM)
 * By: Edward Lam
 */
package org.openquark.gems.client;

/**
 * Strings used in the persistence of Gem client objects.
 * @author Edward Lam
 */
public final class GemPersistenceConstants {
    
    // Prevent this class from being instantiated.
    private GemPersistenceConstants () {}
    
    /** Namespace for all gems-related stuff. */
    public static final String GEMS_NS =                               "http://www.businessobjects.com/gems";
    
    /** Namespace URI reserved for gems. */
    public static final String GEM_NS =                                "http://www.businessobjects.com/gems/gem";
    public static final String GEM_NS_PREFIX =                         "gem";

    /** Location of XML schema. */
    public static final String GEMS_SCHEMA_LOCATION =                  "/Resources/gemcutter.xsd"; // "file:gemcutter.xsd";

    /* 
     * Info for top-level elements
     */
    public static final String GEMCUTTER_TAG =                         "GemCutter";
    public static final String TABLETOP_TAG =                          "TableTop";

    /* 
     * Gem info
     */
     
    // General gem info
    public static final String DISPLAYED_GEM_TAG =                     "DisplayedGem";
    public static final String GEM_TAG =                               "Gem";
    
    public static final String CODE_GEM_TAG =                          "CodeGem";
    public static final String COLLECTOR_GEM_TAG =                     "CollectorGem";
    public static final String EMITTER_GEM_TAG =                       "EmitterGem";
    public static final String FUNCTIONAL_AGENT_GEM_TAG =              "FunctionalAgentGem";
    public static final String VALUE_GEM_TAG =                         "ValueGem";
    public static final String RECORD_FIELD_SELECTION_GEM_TAG =        "RecordFieldSelectionGem";
    public static final String RECORD_CREATION_GEM_TAG =               "RecordCreationGem";
    

    // Inputs
    public static final String INPUTS_TAG =                            "inputs";
    public static final String INPUT_TAG =                             "input";
    public static final String INPUT_BURNT_ATTR =                      "burnt";

    // Input name
    public static final String INPUT_NAME_TAG =                        "inputname";
    public static final String INPUT_NAME_BASE_NAME_ATTR =             "basename";
    public static final String INPUT_NAME_USER_SAVED_ATTR =            "usersaved";

    // Gem id
    public static final String GEM_ID_ATTR =                           "id";
    
    // Gem location
    public static final String LOCATION_TAG =                          "location";
    public static final String LOCATION_X_ATTR =                       "x";
    public static final String LOCATION_Y_ATTR =                       "y";

    // Code gems
    public static final String CODE_GEM_CODE_TAG =                     "code";
    public static final String CODE_GEM_ARGUMENTS_TAG =                "arguments";
    public static final String CODE_GEM_ARGUMENT_TAG =                 "argument";
    public static final String CODE_GEM_ARGUMENT_NAME_ATTR =           "name";
    public static final String CODE_GEM_BROKEN_ATTR =                  "broken";
    
    // Code qualification map
    public static final String CODE_GEM_QUALIFICATION_MAP_TAG =        "qualificationMap";
    public static final String CODE_GEM_MAP_FUNCTIONS_TAG =            "functions";
    public static final String CODE_GEM_MAP_CONSTRUCTORS_TAG =         "constructors";
    public static final String CODE_GEM_MAP_TYPES_TAG =                "types";
    public static final String CODE_GEM_MAP_CLASSES_TAG =              "classes";
    public static final String CODE_GEM_MAPPING_TAG =                  "mapping";
    public static final String CODE_GEM_UNQUALIFIED_ATTR =             "unqualifiedName";
    public static final String CODE_GEM_MODULE_ATTR =                  "moduleName";

    // Collector gems
    public static final String COLLECTOR_GEM_TARGET_COLLECTOR_ATTR =   "targetCollector";
    
    // Emitter gems
    public static final String REFLECTOR_GEM_ORPHANED_INPUTS_TAG =     "orphanedInputs";
    public static final String REFLECTOR_GEM_COLLECTOR_ID_ATTR =       "collectorId";
    
    // Value gems
    public static final String VALUE_GEM_VALUE_TAG =                   "value";
   
    
    // Record Creation gems
    public static final String RECORD_CREATION_GEM_FIELDS_TAG =         "fields";
    public static final String RECORD_CREATION_GEM_FIELD_TAG =          "field";
    public static final String RECORD_CREATION_GEM_NEXT_FIELDNAME_ATTR = "nextPotentialFieldName";
    
    // Arguments
    public static final String ARGUMENTS_TAG =                         "arguments";
    public static final String ARGUMENT_TAG =                          "argument";
    public static final String ARGUMENT_GEM_ATTR =                     "gem";
    public static final String ARGUMENT_INPUT_ATTR =                   "input";
    public static final String ARGUMENT_REFLECTED_ATTR =               "reflected";

    // Connections
    public static final String CONNECTION_TAG =                        "connection";
    public static final String CONNECTION_FROM_TAG =                   "from";
    public static final String CONNECTION_TO_TAG =                     "to";
    public static final String CONNECTION_GEM_ATTR =                   "gem";
    public static final String CONNECTION_INPUT_ATTR =                 "input";

    /*
     * Gem metadata constants
     */
    public static final String ARGUMENT_DESIGN_METADATA_TAG =          "argumentDesignMetadata";
    public static final String COLLECTOR_DESIGN_METADATA_TAG =         "collectorDesignMetadata";
    public static final String ORIGINAL_NAME_TAG =                     "originalName";
}
