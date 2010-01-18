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
 * SerializationTags.java
 * Created: Feb 25, 2005
 * By: Raymond Cypher
 */

package org.openquark.cal.internal.serialization;

/**
 * Record tags for serializing Modules and their content.
 */
public class ModuleSerializationTags {
    public static final short MODULE = 100;
    public static final short MACHINE_FUNCTION = 101;
    public static final short G_MACHINE_FUNCTION = 102;
    public static final short G_CODE = 103;
    public static final short G_MODULE = 104;
    public static final short G_INSTRUCTION = 105;
    public static final short LECC_MODULE = 106;
    public static final short CORE_FUNCTION = 107;
    public static final short QUALIFIED_NAME = 108;
    public static final short MODULE_TYPE_INFO = 109;
    public static final short MODULE_IMPORTS = 110;    
    public static final short TYPE_CONSTRUCTOR_ENTITY = 112;
    public static final short SCOPED_ENTITY = 113;
    public static final short SCOPE = 114;
    public static final short DATA_CONSTRUCTOR = 115;
    public static final short FOREIGN_TYPE_INFO = 116;
    public static final short TYPE_CLASS = 117;
    public static final short FUNCTIONAL_AGENT = 118;
    public static final short CLASS_METHOD = 119;
    public static final short FUNCTION_ENTITY = 120;
    //public static final short ENV_ENTITY_FORM = 121;
    public static final short CLASS_INSTANCE = 123;
    public static final short CLASS_INSTANCE_IDENTIFIER = 124;
    public static final short AD_HOC_RECORD_INSTANCE = 125;
    public static final short TYPE_CONSTRUCTOR_INSTANCE = 126;
    public static final short UNIVERSAL_RECORD_INSTANCE = 127;
    public static final short FIELD_NAME_ORDINAL = 128;
    public static final short FIELD_NAME_TEXTUAL = 129;
    //we should not be serializing kind variables.
    //public static final short KIND_EXPR_KIND_VAR = 130;
    public static final short KIND_EXPR_KIND_FUNCTION = 131;
    public static final short KIND_EXPR_KIND_CONSTANT = 132;
    public static final short TYPE_VAR = 133;
    public static final short TYPE_CONSTRUCTOR = 134;
    public static final short RECORD_TYPE = 135;
    public static final short ALREADY_VISITED_TYPE_EXPR = 136;
    public static final short RECORD_VAR = 137;
    public static final short CONSTANT_TYPE_EXPR = 138;
    public static final short CONSTANT_TYPE_CONSTRUCTOR_ENTITY = 139;
    //we should not be serializing kind variables.
    //public static final short KIND_EXPR_KIND_VAR_WITH_INSTANCE = 140;
    public static final short FOREIGN_TYPE_CONSTRUCTOR_ENTITY = 141;
    public static final short TYPE_VAR_WITH_INSTANCE = 142;
    public static final short MODULE_SOURCE_METRICS = 143;
    public static final short STATIC_CONSTANT_TYPE_EXPR = 144;
    public static final short CALDOC_COMMENT = 145;
    public static final short EXPRESSION_APPL = 146;
    public static final short EXPRESSION_CAST = 147;
    public static final short EXPRESSION_DATACONS_SELECTION = 148;
    public static final short EXPRESSION_ERROR_INFO = 149;
    public static final short EXPRESSION_LET = 150;
    public static final short EXPRESSION_LET_DEFN = 151;
    public static final short EXPRESSION_LET_REC = 152;
    public static final short EXPRESSION_LET_NONREC = 153;
    public static final short EXPRESSION_LITERAL = 154;
    public static final short EXPRESSION_PACKCONS = 155;
    public static final short EXPRESSION_RECORD_CASE = 156;
    public static final short EXPRESSION_RECORD_EXTENSION = 157;
    public static final short EXPRESSION_RECORD_SELECTION = 158;
    public static final short EXPRESSION_SWITCH = 159;
    public static final short EXPRESSION_SWITCHALT = 160;
    public static final short EXPRESSION_SWITCHALT_MATCHING = 161;
    public static final short EXPRESSION_SWITCHALT_POSITIONAL = 162;
    public static final short EXPRESSION_TAIL_RECURSIVE_CALL = 163;
    public static final short EXPRESSION_VAR = 164;
    public static final short CALDOC_TEXT_PARAGRAPH = 165;
    public static final short CALDOC_LIST_PARAGRAPH = 166;
    public static final short CALDOC_PLAIN_TEXT_SEGMENT = 167;
    public static final short CALDOC_URL_SEGMENT = 168;
    public static final short CALDOC_MODULE_LINK_SEGMENT = 169;
    public static final short CALDOC_FUNCTION_OR_CLASS_METHOD_LINK_SEGMENT = 170;
    public static final short CALDOC_TYPE_CONS_LINK_SEGMENT = 171;
    public static final short CALDOC_DATA_CONS_LINK_SEGMENT = 172;
    public static final short CALDOC_TYPE_CLASS_LINK_SEGMENT = 173;
    public static final short CALDOC_CODE_SEGMENT = 174;
    public static final short CALDOC_EMPHASIZED_SEGMENT = 175;
    public static final short POOLED_VALUES = 176;
    public static final short SERIALIZATION_INFO = 177;
    public static final short CALDOC_STRONGLY_EMPHASIZED_SEGMENT = 178;
    public static final short CALDOC_SUPERSCRIPT_SEGMENT = 179;
    public static final short CALDOC_SUBSCRIPT_SEGMENT = 180;
    public static final short ALREADY_VISITED_RECORD_VAR = 181;
    public static final short LECC_GENERATED_CODE_INFO = 182;
    public static final short G_GENERATED_CODE_INFO = 183;
    public static final short LOCAL_FUNCTION_IDENTIFIER = 184;
    public static final short EXPRESSION_RECORD_UPDATE = 185;
    public static final short TYPE_APP = 186;    
    public static final short FOREIGN_FUNCTION_INFO_JAVA_KIND = 188;
    public static final short FOREIGN_FUNCTION_INFO_INVOCATION = 189;
    public static final short FOREIGN_FUNCTION_INFO_CAST = 190;
    public static final short FOREIGN_FUNCTION_INFO_INSTANCE_OF = 191;
    public static final short FOREIGN_FUNCTION_INFO_NULL_LITERAL = 192;
    public static final short FOREIGN_FUNCTION_INFO_NULL_CHECK = 193;   
    public static final short FOREIGN_FUNCTION_INFO_NEW_ARRAY = 194;
    public static final short FOREIGN_FUNCTION_INFO_LENGTH_ARRAY = 195;
    public static final short FOREIGN_FUNCTION_INFO_SUBSCRIPT_ARRAY = 196;
    public static final short FOREIGN_FUNCTION_INFO_UPDATE_ARRAY = 197;
    public static final short LECC_MACHINE_FUNCTION = 198;
    public static final short FOREIGN_FUNCTION_INFO_CLASS_LITERAL = 199;
}
