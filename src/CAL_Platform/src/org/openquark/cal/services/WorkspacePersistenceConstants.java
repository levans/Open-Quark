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
 * WorkspacePersistenceConstants.java
 * Creation date: Jul 28, 2004.
 * By: Edward Lam
 */
package org.openquark.cal.services;

/**
 * Constants used when persisting workspace-related objects.
 * @author Edward Lam
 */
final class WorkspacePersistenceConstants {
    
    // Prevents this class from being instantiated
    private WorkspacePersistenceConstants() {}

    /* Workspace namespace. */
    static final String WORKSPACE_NS =                          "http://www.businessobjects.com/cal/workspace";
    static final String WORKSPACE_NS_PREFIX =                   "workspace";
    
    /* Workspace schema. */
    static final String WORKSPACE_DESCRIPTION_SCHEMA_LOCATION = "/Resources/workspace.description.xsd";

    /* The top-level element for the workspace description. */
    static final String WORKSPACE_DESCRIPTION_TAG =             "WorkspaceDescription";

    //Description tag names.
    static final String MODULE_TAG =                            "module";
    static final String MODULE_NAME_ATTR =                      "name";
    
    // Vault Info
    static final String VAULT_INFO_TAG =                        "vaultInfo";

    // Vault type info
    static final String VAULT_TYPE_TAG =                        "type";

    // Vault location
    static final String VAULT_LOCATION_TAG =                    "location";

    // Resource info
    static final String RESOURCE_INFO_TAG =                     "resourceInfo";

    // Sync time
    static final String SYNC_TIME_TAG =                         "syncTime";

    // Revisions
    static final String MODULE_REVISION_TAG =                   "revision";
    static final String RESOURCE_REVISION_TAG =                 "revision";

    // A resource's associated feature
    static final String RESOURCE_FEATURE_TAG =                  "feature";
    
    
    // Qualified name
    static final String QUALIFIED_NAME_TAG =                    "name";
    static final String QUALIFIED_NAME_MODULE_ATTR =            "modulename";
    static final String QUALIFIED_NAME_UNQUALIFIED_ATTR =       "unqualifiedname";

    // Feature name
    static final String FEATURE_NAME_TAG =                      "featureName";
    static final String FEATURE_NAME_TYPE_TAG =                 "type";
    static final String FEATURE_NAME_NAME_TAG =                 "name";
    
    // Resource name
    static final String RESOURCE_NAME_TAG =                     "resourceName";
    static final String RESOURCE_NAME_TYPE_TAG =                "type";
    static final String RESOURCE_NAME_LOCALE_LANGUAGE_TAG =     "localeLanguage";
    static final String RESOURCE_NAME_LOCALE_COUNTRY_TAG =      "localeCountry";
    static final String RESOURCE_NAME_LOCALE_VARIANT_TAG =      "localeVariant";
}
