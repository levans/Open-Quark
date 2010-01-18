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
 * RecordCreationGem.java
 * Creation date: July 27, 2007.
 * By: Jennifer Chen
 */

package org.openquark.gems.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openquark.cal.compiler.CompositionNode;
import org.openquark.cal.compiler.FieldName;
import org.openquark.cal.compiler.LanguageInfo;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.metadata.ArgumentMetadata;
import org.openquark.cal.services.CALFeatureName;
import org.openquark.util.Pair;
import org.openquark.util.xml.BadXMLDocumentException;
import org.openquark.util.xml.XMLPersistenceHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * An RecordCreatonGem is a Gem which creates a record.
 * @author Jennifer Chen
 */

public class RecordCreationGem extends Gem implements CompositionNode.RecordCreationNode {

    // Used for making a valid ordinal field name
    private static final String DEFAULT_NEW_FIELD_PREFIX = "#";

    private static final int DEFAULT_NUM_FIELDS = 2;

    /** Used to suggest the new field and argument name to be created, needs to be checked for uniqueness */
    private int nextPossibleFieldName = 1;

    /** The current list of record fields in order of addition */
    private List<String> fields = new ArrayList<String>();

    /**
     * Constructor. A default number of fields are created.
     */
    public RecordCreationGem() {
        super(0);
        addNewFields(DEFAULT_NUM_FIELDS);
    }

    /**
     * Add new fields to the end of the fields list with a default name then updates the inputs
     * @param numFields the number of fields to add
     */
    public void addNewFields(int numFields) {

        // Save the state before adding new fields
        Map<String, PartInput> fieldNameToInputMap = getFieldNameToInputMap();

        for (int i = 0; i < numFields; i++) {
            fields.add(getUniqueFieldName(fields));
            nextPossibleFieldName++;
        }

        // Update the gem inputs
        updateInputs(fieldNameToInputMap);

    }

    /**
     * Delete the specified field
     * @param fieldToDelete the field to delete from record
     * @throws IllegalArgumentException
     */
    public void deleteField(String fieldToDelete) {

        // Save the state before deleting
        Map<String, PartInput> fieldNameToInputMap = getFieldNameToInputMap();

        if (fields.remove(fieldToDelete)) {
            updateInputs(fieldNameToInputMap);
        } else {
            throw new IllegalArgumentException("The field to delete does not exist.");
        }
    }

    /**
     * Rename an existing field with a valid new field name which does not already exist. Renaming an field to 
     * its original name (essentially no change) is allowed
     * @param fieldIndex the index of the field to rename
     * @param newName the new field name
     * @throws IllegalArgumentException if newName is null or newName is a duplicate of an existing name other than itself. 
     */
    public void renameRecordField(int fieldIndex, String newName) {

        String oldName = fields.get(fieldIndex);

        if (newName == null) {
            throw new IllegalArgumentException("The field cannot be renamed if the new name is full");
        }

        fields.set(fieldIndex, newName);

        PartInput input = this.getInputPart(fieldIndex);
        String newInputName = getArgumentString(newName);
        // this is the displayed name
        input.setOriginalInputName(newInputName);
        // but setting the argument name is necessary to change the display instantly.
        input.setArgumentName(new ArgumentName(newInputName));

        // Update the metadata as well. For now user-set names will be lost when renaming happens
        // this line: input.getDesignMetadata().setDisplayName(newInputName); does not overwrite the existing metadata.
        ArgumentMetadata metadata = new ArgumentMetadata(CALFeatureName.getArgumentFeatureName(fieldIndex), input.getDesignMetadata().getLocale());
        metadata.setDisplayName(newInputName);
        input.setDesignMetadata(metadata);
        
        if (nameChangeListener != null) {
            nameChangeListener.nameChanged(new NameChangeEvent(this, oldName));
        }

    }

    /**
     * Updates the input according the current list of fields. 
     * The updated inputs will be in the order of their corresponding fields.
     * 
     * @param fieldnameToInputMap the map of field to input before the fields were updated, 
     * used to preserve the correct match up for fields and inputs
     */
    void updateInputs(Map<String, PartInput> fieldnameToInputMap) {

        int nNewInputs = fields.size();
        PartInput[] newInputArray = new PartInput[nNewInputs];

        for (int i = 0; i < nNewInputs; i++) {
            String name = fields.get(i);
            PartInput input = fieldnameToInputMap.get(name);
            if (input != null) {
                // existing input
                input.setInputNum(i);
                input.setOriginalInputName(getArgumentString(name));

            } else {
                // new input
                input = createInputPart(i);
                input.setType(TypeExpr.makeParametricType());

                String argName = getArgumentString(name);
                input.setArgumentName(new ArgumentName(argName));
                input.setOriginalInputName(argName);

                // Needs to set metadata because of locale
                ArgumentMetadata metadata = new ArgumentMetadata(CALFeatureName.getArgumentFeatureName(i), input.getDesignMetadata().getLocale());
                metadata.setDisplayName(argName);
                input.setDesignMetadata(metadata);
            }

            newInputArray[i] = input;
        }

        setInputParts(newInputArray);
    }

    /**
     * Get a list of deletable fields
     * @param tabletop
     * @return fields that can be deleted
     */
    public List<String> getDeletableFields(TableTop tabletop) {

        int nArgs = getNInputs();
        List<String> deletableFields = new ArrayList<String>(nArgs);

        // Get all the inputs that are not connected
        for (int i = 0; i < nArgs; i++) {
            PartInput input = this.getInputPart(i);
            if (!input.isConnected()) {
                deletableFields.add(fields.get(i));
            }
        }

        return deletableFields;
    }

    /**
     * Get a list of renamable field 
     * @param tabletop
     * @return fields that can be renamed
     */
    public List<String> getRenamableFields(TableTop tabletop) {
        // For now return all fields are renamable
        return this.getCopyOfFieldsList();

    }

    /**
     * Generates a string for the argument display
     * @param fieldName the field to generate a string for
     * @return a string representing a field's corresponding input
     */
    private String getArgumentString(String fieldName) {
    
        StringBuilder fieldNameStr = new StringBuilder(fieldName);
        if (fieldName.length() > 0 && fieldName.charAt(0) == '#') {
            fieldNameStr.deleteCharAt(0);
        }
    
        return "fld_" + fieldNameStr;
    
    }

    /**
     * Helper method to find an unique new field name
     * @param hasFields the field names that already exists for this gem
     * @return a new field name
     */
    private String getUniqueFieldName(Collection<String> hasFields) {

        for (int i = nextPossibleFieldName; true; i++) {
            String name = DEFAULT_NEW_FIELD_PREFIX + i;

            if (LanguageInfo.isValidFieldName(name) && !hasFields.contains(name)) {
                return name;
            }
        }
    }

    /**
     * Retrieve the most up-to-date fields and their corresponding PartInputs in order of the fields list
     * @return mapping of field names to PartInputs
     */
    public Map<String, PartInput> getFieldNameToInputMap() {

        Map<String, PartInput> fieldToInputMap = new LinkedHashMap<String, PartInput>();
        int nArgs = getNInputs();

        for (int i = 0; i < nArgs; i++) {
            PartInput input = getInputPart(i);
            fieldToInputMap.put(fields.get(i), input);
        }
        
        return fieldToInputMap;
    }

    /**
     * @return a copy of the fields list
     */
    public List<String> getCopyOfFieldsList() {
        List<String> newFieldsList = new ArrayList<String>(fields.size());
        for (String fieldToCopy : fields) {
            newFieldsList.add(fieldToCopy);
        }
        return newFieldsList;
    }

    /**
     * Sets the fields list for this gem. Replaces all existing fields. 
     * Note: this does not update the gem's inputs
     * @param newFields
     */
    void setFieldNames(List<String> newFields) {
        this.fields.clear();

        fields.addAll(newFields);
    }

    /**
     * Sets the next potential ordinal field name to be created
     * @param num the next possible field name, should be > 0
     */
    void setNextPossibleFieldName(int num) {
        if (num > 0) {
            this.nextPossibleFieldName = num;
        }
    }

    /**
     * Gets the NextPossibleFieldName which suggests the ordinal name for next field created 
     * @return int the integer part of the potential ordinal field name
     */
    int getNextPossibleFieldName() {
        return nextPossibleFieldName;
    }

    /**
     * @param index the index of the field in the list
     * @return the field name at the specified index in the field list
     */
    public FieldName getFieldName(int index) {
        return FieldName.make(fields.get(index));
    }

    /**
     * @param field the field to retrieve index for
     * @return the index of the field in question
     */
    public int getFieldIndex(FieldName field) {
        return fields.indexOf(field.getCalSourceForm());

    }

    /**
     * @return the string representation of the field names
     */
    public String getDisplayName() {

        StringBuilder displayName = new StringBuilder("{ ");

        boolean isFirstField = true;
        for (String field : fields) {
            if (isFirstField) {
                isFirstField = false;
            } else {
                displayName.append(", ");
            }
            displayName.append(field);
        }

        displayName.append(" }");
        return displayName.toString();
    }

    /*
     * Methods supporting XMLPersistable ********************************************
     */

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveXML(Node parentNode, GemContext gemContext) {

        if (fields.isEmpty()) {
            throw new IllegalStateException("RecordCreationGem cannot be saved while fields are empty");
        }

        Document document = (parentNode instanceof Document) ? (Document)parentNode : parentNode.getOwnerDocument();

        // Create the RecordCreationGem element
        Element resultElement = document.createElementNS(GemPersistenceConstants.GEM_NS, GemPersistenceConstants.RECORD_CREATION_GEM_TAG);
        resultElement.setPrefix(GemPersistenceConstants.GEM_NS_PREFIX);
        parentNode.appendChild(resultElement);

        // Add info for the superclass gem.
        super.saveXML(resultElement, gemContext);

        // Now add RecordCreationGem specific info

        // Element for the arguments
        Element argumentsElement = document.createElement(GemPersistenceConstants.ARGUMENTS_TAG);
        resultElement.appendChild(argumentsElement);

        // Get and add the element for each argument
        for (final PartInput input : this.getInputParts()) {
            Element argumentElement = GemCutterPersistenceHelper.inputToArgumentElement(input, document, gemContext);
            argumentsElement.appendChild(argumentElement);
        }

        // Element for the field names
        Element fieldNamesElement = document.createElement(GemPersistenceConstants.RECORD_CREATION_GEM_FIELDS_TAG);
        resultElement.appendChild(fieldNamesElement);

        for (final String name : this.fields) {
            XMLPersistenceHelper.addTextElement(fieldNamesElement, GemPersistenceConstants.RECORD_CREATION_GEM_FIELD_TAG, name);
        }
        resultElement.setAttribute(GemPersistenceConstants.RECORD_CREATION_GEM_NEXT_FIELDNAME_ATTR, String.valueOf(nextPossibleFieldName));
    }

    /**
     * Create a new RecordCreationGem and loads its state from the specified XML element.
     * @param gemElement Element the element representing the structure to deserialize.
     * @param gemContext the context in which the gem is being instantiated.
     * @param loadInfo the argument info for this load session.
     * @return RecordCreationGem
     * @throws BadXMLDocumentException
     */
    public static RecordCreationGem getFromXML(Element gemElement, GemContext gemContext, Argument.LoadInfo loadInfo) throws BadXMLDocumentException {
        RecordCreationGem gem = new RecordCreationGem();
        gem.loadXML(gemElement, gemContext, loadInfo);
        return gem;
    }

    /**
     * Load this object's state.
     * @param gemElement Element the element representing the structure to deserialize.
     * @param gemContext the context in which the gem is being instantiated.
     * @param loadInfo loadInfo for the arguments
     */
    void loadXML(Element gemElement, GemContext gemContext, Argument.LoadInfo loadInfo) throws BadXMLDocumentException {

        XMLPersistenceHelper.checkTag(gemElement, GemPersistenceConstants.RECORD_CREATION_GEM_TAG);
        XMLPersistenceHelper.checkPrefix(gemElement, GemPersistenceConstants.GEM_NS_PREFIX);

        List<Element> childElems = XMLPersistenceHelper.getChildElements(gemElement);
        int nChildElems = childElems.size();

        // Get info for the underlying gem.
        Element superGemElem = (childElems.size() < 1) ? null : (Element)childElems.get(0);
        XMLPersistenceHelper.checkIsElement(superGemElem);
        super.loadXML(superGemElem, gemContext);

        // Figure out the record node

        // Get the arguments
        Element argumentsElement = (nChildElems < 2) ? null : (Element)childElems.get(1);
        XMLPersistenceHelper.checkIsElement(argumentsElement);

        List<Element> argumentsChildElems = XMLPersistenceHelper.getChildElements(argumentsElement);
        int nArgumentsChildElems = argumentsChildElems.size();

        // Get the field names
        Element fieldNamesElement = (nChildElems < 3) ? null : (Element)childElems.get(2);
        XMLPersistenceHelper.checkIsElement(fieldNamesElement);

        List<Element> fieldsChildElems = XMLPersistenceHelper.getChildElements(fieldNamesElement);

        if (fieldsChildElems.size() != nArgumentsChildElems) {
            throw new BadXMLDocumentException(null, "Number of fields and inputs do not correspond.");
        }

        // Erase the default fields
        fields.clear();

        for (int i = 0; i < nArgumentsChildElems; i++) {
            Element inputChildElem = argumentsChildElems.get(i);
            Pair<String, Integer> inputInfoPair = GemCutterPersistenceHelper.argumentElementToInputInfo(inputChildElem);

            // Add the argument info the load info.
            loadInfo.addArgument(this, inputInfoPair.fst(), inputInfoPair.snd(), null);

            Element fieldChildElem = fieldsChildElems.get(i);
            String name = XMLPersistenceHelper.getChildText(fieldChildElem);

            // Update the fields list by appending the name to the end of the list
            if (name != null) {
                fields.add(name);
            }
        }

        try {
            nextPossibleFieldName = XMLPersistenceHelper.getIntegerAttributeWithDefault(gemElement, GemPersistenceConstants.RECORD_CREATION_GEM_NEXT_FIELDNAME_ATTR, nArgumentsChildElems);
        } catch (BadXMLDocumentException e) {
            nextPossibleFieldName = nArgumentsChildElems;
        }
    }

    /**
     * Makes a copy of the specified gem.
     * @return a copy of the current RecordCreationGem
     */
    RecordCreationGem makeCopy() {

        if (fields.isEmpty()) {
            throw new IllegalStateException("Record Creation Gem cannot be copied while fields are empty");
        }

        // create a new gem with a copy of the current fields list
        RecordCreationGem newGem = new RecordCreationGem();
        newGem.fields = this.getCopyOfFieldsList();
        newGem.nextPossibleFieldName = this.nextPossibleFieldName;
        newGem.updateInputs(newGem.getFieldNameToInputMap());

        return newGem;
    }

}
