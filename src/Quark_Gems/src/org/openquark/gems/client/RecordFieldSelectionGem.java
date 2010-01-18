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
 * RecordFieldSelectionGem.java
 * Creation date: Nov 28, 2006.
 * By: Neil Corkum
 */
package org.openquark.gems.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.openquark.cal.compiler.CompositionNode;
import org.openquark.cal.compiler.FieldName;
import org.openquark.cal.compiler.ModuleTypeInfo;
import org.openquark.cal.compiler.RecordType;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.services.CALPersistenceHelper;
import org.openquark.util.xml.BadXMLDocumentException;
import org.openquark.util.xml.XMLPersistenceHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * An RecordFieldSelectionGem is a Gem which extracts a field from a record.
 * The field that the Gem will extract is specified by the user.
 * @author Neil Corkum
 * @author mbyne
 */
public final class RecordFieldSelectionGem extends Gem implements CompositionNode.RecordFieldSelectionNode {

    /** Field name to select from record.  
     */
    private String fieldToSelect = GemCutter.getResourceString("DefaultRecordSelectionField");
    
    /**
     * This value indicates whether or not the field name can be changed.
     * The Field name is not fixed initially and can be set when a gem is connected
     * However, once a gem is connected and the field is choose, it can no longer be modified.
     */
    private boolean isFixed = false;
    
    /**
     * Constructor for RecordFieldSelectionGem.
     */
    public RecordFieldSelectionGem() {
        super(1); // RecordFieldSelectionGem gem always has 1 argument
    }

    /**
     * Gets the input part of the RecordFieldSelectionGem. RecordFieldSelectionGems always have
     * exactly one input part.
     * @return the input part of the gem
     */
    public PartInput getInputPart() {
        return getInputPart(0);
    }
    
    /**
     * Sets the name of the field that this gem will extract. Should only be
     * set to null as a temporary measure - setting the field name to null will
     * result in an inconsistent state for the gem.
     * @param name new extracted field name
     */
    public void setFieldName(String name) {
        String oldName = fieldToSelect;

        if (!oldName.equals(name)) {
            fieldToSelect = name;

            if (nameChangeListener != null) {
                nameChangeListener.nameChanged(new NameChangeEvent(this, oldName != null ? oldName.toString() : null));
            }
        }
    }

    /**
     * Sets the name of the field that this gem will select. 
     * @param name new extracted field name
     */
    public void setFieldName(FieldName name) {
        setFieldName(name.getCalSourceForm());       
    }
    
    /**
     * Gets the field to be selected from a record.
     * @return the field this Gem will select
     */
    public FieldName getFieldName() {
        return FieldName.make(fieldToSelect);
    }
    
    /**
     * Gets the field to be selected from a record.
     * @return the field this Gem will select
     */
    public String getFieldNameString() {
        return fieldToSelect;
    }
    
    
    /**
     * @return true iff a field should not be modified to facilitate connections
     */
    public boolean isFieldFixed() {
        return isFixed;
    }
    
    
    /**
     * Set whether or not this field is fixed. If the field is not fixed
     * it may be modified in order to make connections
     * @param fixed set whether or not the field is fixed
     */
    public void setFieldFixed(boolean fixed) {
        isFixed = fixed;
    }
    
    /**
     * Returns the name of the first field in the sourceType (record) which is 
     * unifiable with the requiredOutputType - or null if there is no field that satisfies the type constraints.
     * @param sourceType
     * @param requiredOutputType
     * @param currentModuleTypeInfo
     * @return the first potential field name
     */
    public static FieldName pickFieldName(TypeExpr sourceType, 
                                          TypeExpr requiredOutputType, 
                                          ModuleTypeInfo currentModuleTypeInfo) {
    
        //cannot find a field if the source is not even a record
        if (!(sourceType instanceof RecordType)) {
            return null;
        }
        
        for (final FieldName fieldName : ((RecordType)sourceType).getHasFieldNames()) {
            RecordType requiredRecordType = TypeExpr.makePolymorphicRecordType(Collections.singletonMap(fieldName, requiredOutputType));
    
            if (TypeExpr.canUnifyType (requiredRecordType, sourceType, currentModuleTypeInfo)) {
                return fieldName;
            }
        }
        
        //if the record to match is polymorphic and none of the specified fields match
        //choose the first available field name
        if (((RecordType)sourceType).isRecordPolymorphic()) {
            //make a field name that is not restricted
            Set<FieldName> lacksFields = ((RecordType)sourceType).getLacksFieldsSet();
            int i=0;
            do {
                i++;
            } while (lacksFields.contains(FieldName.makeOrdinalField(i)));
            return FieldName.makeOrdinalField(i);
        }
            
        return null;
    }

    /**
     * Returns a set of fields that are members of the sourceType (record) and that are unifiable with 
     * the requiredOutputType - the set maybe empty if there are no such fields.
     * @param sourceType
     * @param requiredOutputType
     * @param currentModuleTypeInfo
     * @return List of possible field names
     */
    public static List<FieldName> possibleFieldNames(RecordType sourceType, 
        TypeExpr requiredOutputType, 
        ModuleTypeInfo currentModuleTypeInfo) {
    
        List<FieldName> fieldNames = new ArrayList<FieldName>();
        for (final FieldName fieldName : sourceType.getHasFieldNames()) {
    
            RecordType requiredRecordType = TypeExpr.makePolymorphicRecordType(Collections.singletonMap(fieldName, requiredOutputType));
    
            if (TypeExpr.canUnifyType (requiredRecordType, sourceType, currentModuleTypeInfo)) {
                fieldNames.add( fieldName);
            }
        }
        return fieldNames;
    }

    /**
     * Gets the displayed text of the Gem.
     * @return text displayed on gem
     */
    public String getDisplayedText() {
        if (fieldToSelect == null) {
            return "";
        } else {
            if (isFixed) {
                return fieldToSelect;
            } else {
                return GemCutter.getResourceString("RecordFieldUnfixedDisplay", fieldToSelect);
            }
        }
    }
    
    /*
     * Methods supporting XMLPersistable                 ********************************************
     */

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveXML(Node parentNode, GemContext gemContext) {
        
        String fieldName = getFieldNameString();
        if (fieldName == null) {
            throw new IllegalStateException("RecordFieldSelectionGem cannot be saved while field to extract is null");
        }
        
        Document document = (parentNode instanceof Document) ? (Document)parentNode : parentNode.getOwnerDocument();

        // Create the RecordFieldSelectionGem element
        Element resultElement = document.createElementNS(GemPersistenceConstants.GEM_NS, GemPersistenceConstants.RECORD_FIELD_SELECTION_GEM_TAG);
        resultElement.setPrefix(GemPersistenceConstants.GEM_NS_PREFIX);
        parentNode.appendChild(resultElement);

        // Add info for the superclass gem.
        super.saveXML(resultElement, gemContext);


        // Now add RecordFieldSelectionGem-specific info

        // Add an element for the field name.
        Element nameElement = CALPersistenceHelper.unqualifiedNameToElement(fieldName.toString(), document);
        resultElement.appendChild(nameElement);
    }

    /**
     * Create a new RecordFieldSelectionGem and loads its state from the specified XML element.
     * @param gemElement Element the element representing the structure to deserialize.
     * @param gemContext the context in which the gem is being instantiated.
     * @param loadInfo the argument info for this load session.
     * @return RecordFieldSelectionGem
     * @throws BadXMLDocumentException
     */
    public static RecordFieldSelectionGem getFromXML(Element gemElement, GemContext gemContext, Argument.LoadInfo loadInfo) throws BadXMLDocumentException {
        RecordFieldSelectionGem gem = new RecordFieldSelectionGem();
        gem.loadXML(gemElement, gemContext, loadInfo);
        return gem;
    }

    /**
     * Load this object's state.
     * @param gemElement Element the element representing the structure to deserialize.
     * @param gemContext the context in which the gem is being instantiated.
     * @param loadInfo the argument info for this load session.
     */
    void loadXML(Element gemElement, GemContext gemContext, Argument.LoadInfo loadInfo) throws BadXMLDocumentException {

        XMLPersistenceHelper.checkTag(gemElement, GemPersistenceConstants.RECORD_FIELD_SELECTION_GEM_TAG);
        XMLPersistenceHelper.checkPrefix(gemElement, GemPersistenceConstants.GEM_NS_PREFIX);

        List<Element> childElems = XMLPersistenceHelper.getChildElements(gemElement);
        int nChildElems = childElems.size();

        // Get info for the underlying gem.        
        Element superGemElem = (nChildElems < 1) ? null : (Element) childElems.get(0);
        XMLPersistenceHelper.checkIsElement(superGemElem);
        super.loadXML(superGemElem, gemContext);

        // Get the field name
        Element fieldNameElem = (nChildElems < 2) ? null : (Element) childElems.get(1);
        XMLPersistenceHelper.checkIsElement(fieldNameElem);
        String fieldName = CALPersistenceHelper.elementToUnqualifiedName(fieldNameElem);
        fieldToSelect = fieldName;
        isFixed = true;
    }
    
    
    /**
     * Makes a copy of the specified gem. 
     * @return a copy of the current RecordFieldSelectionGem 
     */
    RecordFieldSelectionGem makeCopy() {
        
        // Create a new RecordFieldSelectionGem
        RecordFieldSelectionGem recordFieldSelectionGem = new RecordFieldSelectionGem();
        
        String fieldName = getFieldNameString();
        if (fieldName == null) {
            throw new IllegalStateException("Record Field Selection Gem cannot be copied while field to extract is null");
        }
        
        String name = getFieldName().toString();
        recordFieldSelectionGem.setFieldName(name);
        
        return recordFieldSelectionGem;
     }
}
