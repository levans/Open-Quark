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
 * ValueEditorDirector.java
 * Creation date: (12/07/01 8:54:00 AM)
 * By: Michael Cheng
 */
package org.openquark.gems.client.valueentry;

import java.util.List;

import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.valuenode.ValueNode;


/**
 * A ValueEditorLaunchDirector basically directs which ValueEditor (if any)
 * is used when a new child ValueEditor is to be launched.
 * Creation date: (12/07/01 8:54:00 AM)
 * @author Michael Cheng
 */
public interface ValueEditorDirector {

    /**
     * Returns the ValueEditor (intended for the root level) most suited to handle the indicated situation.
     * Note: If there is no corresponding Entity (Eg: Value Gem or it's just Output), then entityName is null, and
     * the argumentNumber is to be disregarded.
     * Note: This method must return a ValueEntryPanel (no null).
     * @param valueEditorHierarchyManager
     * @param valueNode 
     * @param entityName 
     * @param argumentNumber
     * @param metadataRunner Needed to evaluate CAL expressions such as default value list from the metadata
     * @return valueEditor
     */
    public ValueEditor getRootValueEditor(ValueEditorHierarchyManager valueEditorHierarchyManager,
                                          ValueNode valueNode,
                                          QualifiedName entityName,
                                          int argumentNumber,
                                          MetadataRunner metadataRunner);

    /**
     * Returns the ValueEditor (intended for the root level) most suited to handle the indicated situation.
     * Note: If there is no corresponding Entity (Eg: Value Gem or it's just Output), then entityName is null, and
     * the argumentNumber is to be disregarded.
     * Note: This method must return a ValueEntryPanel (no null).
     * @param valueEditorHierarchyManager
     * @param typeExpr 
     * @param entityName 
     * @param argumentNumber 
     * @param metadataRunner Needed to evaluate CAL expressions such as default value list from the metadata
     * @return valueEditor
     */
    public ValueEditor getRootValueEditor(ValueEditorHierarchyManager valueEditorHierarchyManager,
                                          TypeExpr typeExpr,
                                          QualifiedName entityName,
                                          int argumentNumber,
                                          MetadataRunner metadataRunner);

    /**
     * Returns the ValueEditor most suitable for handling the situation indicated by the parameters.
     * The director can either use one of the recommended ValueEditors, or it can use its own preferred ValueEditor for the indicated situation.
     * Note: If no ValueEditor is deemed suitable, then the director can return null to indicate this.
     * Note: The recommendedValueEditorList has the java.lang.reflect.Constructor of the recommended ValueEditors.
     * In addition, order in the list matters (in order of decreasing recommendation).  
     * So, the first element in the list is the most highly recommended, while the last element is the least recommended.
     * Also note that the list may be empty if there are no recommendations.
     * A warning: It is almost always the case that the director should choose the most recommended over a lesser recommended ValueEditor.
     * This is because the ValueNode passed in will most likely be most compatible with the most recommended.  
     * Errors could result with using the lesser recommended ValueEditor.
     * Note: The returned ValueEditor (if there is one) must use the given ValueNode and TypeColourManager to ensure consistency 
     * with the rest of the ValueEntry mechanism.
     * Note: If there is no corresponding Entity (Eg: Value Gem or it's just Output), then entityName is null, and
     * the argumentNumber is to be disregarded.
     * @param valueNode 
     * @param recommendedValueEditorList 
     * @param valueEditorHierarchyManager
     * @return ValueEditor
     */
    public ValueEditor getValueEditor(ValueNode valueNode, List<ValueEditorProvider<?>> recommendedValueEditorList, ValueEditorHierarchyManager valueEditorHierarchyManager);

}
