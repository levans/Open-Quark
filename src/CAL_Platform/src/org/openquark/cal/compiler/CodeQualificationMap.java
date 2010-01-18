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
 * CodeQualificationMap.java
 * Creation date: (February 10, 2004)
 * By: Iulian Radu
 */
package org.openquark.cal.compiler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openquark.util.xml.BadXMLDocumentException;
import org.openquark.util.xml.XMLPersistenceHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * Mapping from unqualified to qualified symbol names,
 * used in automatic qualification of code.
 * Creation date: (2/13/04 9:45:37 AM)
 * 
 * @author Iulian Radu
 */
public class CodeQualificationMap {
    
    /*
     * Qualification mappings (unqualifiedName String-> QualifiedName) for various symbol types
     */ 
    
    private Map<String, QualifiedName> constructorMap;
    private Map<String, QualifiedName> typeMap;
    private Map<String, QualifiedName> classMap;
    private Map<String, QualifiedName> functionMap;
    
    /* Constant names of XML tags and attributes for persistent map storage */
    public  static final String QUALIFICATION_MAP_TAG = "qualificationMap";
    private static final String FUNCTIONS_TAG = "functions";
    private static final String CONSTRUCTORS_TAG = "constructors";
    private static final String TYPES_TAG = "types";
    private static final String CLASSES_TAG = "classes";
    private static final String MAPPING_TAG = "mapping";
    private static final String UNQUALIFIED_ATTR = "unqualifiedName";
    private static final String MODULE_ATTR = "module";
    
    /** Construct object with no mappings */
    public CodeQualificationMap() {
        constructorMap  = new HashMap<String, QualifiedName>();
        typeMap         = new HashMap<String, QualifiedName>();
        classMap        = new HashMap<String, QualifiedName>();
        functionMap     = new HashMap<String, QualifiedName>();
    }
    
    /** Creates a clone of this map*/
    public CodeQualificationMap makeCopy() {
        CodeQualificationMap qualificationMap = new CodeQualificationMap();
        
        qualificationMap.constructorMap  = new HashMap<String, QualifiedName>(this.constructorMap);
        qualificationMap.typeMap         = new HashMap<String, QualifiedName>(this.typeMap);
        qualificationMap.classMap        = new HashMap<String, QualifiedName>(this.classMap);
        qualificationMap.functionMap     = new HashMap<String, QualifiedName>(this.functionMap);
        
        return qualificationMap;
    }

    /**
     * Retrieve the qualified name for the specified type
     * 
     * @param unqualifiedName
     * @param type
     * @return qualified name; null if none exists
     */
    public QualifiedName getQualifiedName(String unqualifiedName, SourceIdentifier.Category type) {
        
        if ((unqualifiedName == null) || (type == null)) {
            throw new IllegalArgumentException();
        }
        Map<String, QualifiedName> map = getMapForType(type);
        return map.get(unqualifiedName);
    }
    
    /**
     * Add qualified name mapping for the specified type
     * 
     * @param unqualifiedName
     * @param moduleName
     * @param type
     */
    public void putQualification(String unqualifiedName, ModuleName moduleName, SourceIdentifier.Category type) {
        
        if ((unqualifiedName == null) || (moduleName == null) || (type == null)) {
            throw new IllegalArgumentException();
        }
        Map<String, QualifiedName> map = getMapForType(type);
        map.put(unqualifiedName, QualifiedName.make(moduleName, unqualifiedName));
    }

    
    /**
     * Remove qualified name mapping for the specified type
     * 
     * @param unqualifiedName
     * @param type
     * @return previous qualified name associated with entry; null if none
     */
    public QualifiedName removeQualification(String unqualifiedName, SourceIdentifier.Category type) {
        
        if ((unqualifiedName == null) || (type == null)) {
            throw new IllegalArgumentException();
        }
        Map<String, QualifiedName> map = getMapForType(type);
        return map.remove(unqualifiedName);
    }
    
    /**
     * @param type
     * @return the set (String) of unqualified names appearing in the map for the specific type
     */
    public Set<String> getUnqualifiedNames(SourceIdentifier.Category type) {
        
        Map<String, QualifiedName> map = getMapForType(type);
        return new HashSet<String>(map.keySet());
    }
    
    /**
     * @param type
     * @return the set (QualifiedName) of qualified names appearing in the map for the specific type
     */
    Set<QualifiedName> getQualifiedNames(SourceIdentifier.Category type) {
        Map<String, QualifiedName> map = getMapForType(type);
        return new HashSet<QualifiedName>(map.values());
    }
    
    
    /**
     * Returns a reference to the map containing qualifications for
     * the specified type.
     * 
     * @param type
     * @return map (String->String)
     */
    private Map<String, QualifiedName> getMapForType(SourceIdentifier.Category type)
    {
        if (type == SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD) {
            return functionMap;
            
        } else if (type == SourceIdentifier.Category.DATA_CONSTRUCTOR) {
            return constructorMap;
            
        } else if (type == SourceIdentifier.Category.TYPE_CONSTRUCTOR) {
            return typeMap;
            
        } else if (type == SourceIdentifier.Category.TYPE_CLASS) {
            return classMap;
            
        } else {
            throw new IllegalArgumentException();
        }
    }
    
    /** @return user-readable form of map */    
    @Override
    public String toString() {
        String result = "";
        result += "\n";
        result += "    FUNCTION MAP: ";
        result += functionMap.toString() + "\n";
        result += " CONSTRUCTOR MAP: ";
        result += constructorMap.toString() + "\n";
        result += "        TYPE MAP: ";
        result += typeMap.toString() + "\n";
        result += "       CLASS MAP: ";
        result += classMap.toString() + "\n";
        result += "\n";
        return result;
    }
    
    /**
     * Clear all mappings.
     */
    private void clearMap() {
        functionMap.clear();
        constructorMap.clear();
        classMap.clear();
        typeMap.clear();
    }
    
    /**
     * Saves elements of specified map into the specified XML element
     * 
     * @param map
     * @param parentNode
     * @param document
     */
    private static void saveElementsToXML(Map<String, QualifiedName> map, Element parentNode, Document document) {
        for (final Map.Entry<String, QualifiedName> entry : map.entrySet()) {           
            String unqualifiedName = entry.getKey();
            ModuleName moduleName = entry.getValue().getModuleName();
            // The value's unqualified name is identical to its key
            
            Element mappingElement = document.createElement(MAPPING_TAG);
            mappingElement.setAttribute(UNQUALIFIED_ATTR, unqualifiedName);
            mappingElement.setAttribute(MODULE_ATTR, moduleName.toSourceText());
            parentNode.appendChild(mappingElement);
        }
    }
    
    /**
     * Saves the map into the specified XML element
     * 
     * @param parentElement node which will contain qualification map entry
     */
    public void saveToXML (Element parentElement) {

        Document document = (parentElement instanceof Document) ? (Document)parentElement : parentElement.getOwnerDocument();
        
        Element qualificationMapElement = document.createElement(QUALIFICATION_MAP_TAG);
        parentElement.appendChild(qualificationMapElement);
        
        Element functionsElement = document.createElement(FUNCTIONS_TAG);
        qualificationMapElement.appendChild(functionsElement);
        saveElementsToXML(functionMap, functionsElement, document);
        
        Element constructorsElement = document.createElement(CONSTRUCTORS_TAG);
        qualificationMapElement.appendChild(constructorsElement);
        saveElementsToXML(constructorMap, constructorsElement, document);
        
        Element typesElement = document.createElement(TYPES_TAG);
        qualificationMapElement.appendChild(typesElement);
        saveElementsToXML(typeMap, typesElement, document);
        
        Element classesElement = document.createElement(CLASSES_TAG);
        qualificationMapElement.appendChild(classesElement);
        saveElementsToXML(classMap, classesElement, document);
    }
    
    /**
     * Loads map elements from the specified XML element
     * 
     * @param map
     * @param parentNode 
     */
    private static void loadElementsFromXML (Map<String, QualifiedName> map, Element parentNode) {
        
        List<Element> entryNodes = XMLPersistenceHelper.getChildElements(parentNode, MAPPING_TAG);
        for (int i = 0; i < entryNodes.size(); i++) {
            Element entryNode = entryNodes.get(i);
            String unqualifiedName = entryNode.getAttribute(UNQUALIFIED_ATTR);
            String moduleName = entryNode.getAttribute(MODULE_ATTR);
            
            map.put(unqualifiedName,QualifiedName.make(ModuleName.make(moduleName), unqualifiedName));
        }
    }
    
    /**
     * Loads the map from the specified XML element
     * @param parentElement element containing the qualification map entry
     */
    public void loadFromXML (Element parentElement) throws BadXMLDocumentException {
        
        clearMap();
        
        XMLPersistenceHelper.checkIsElement(parentElement);
        
        Element qualificationMapElement = XMLPersistenceHelper.getChildElement(parentElement, QUALIFICATION_MAP_TAG);
        XMLPersistenceHelper.checkIsElement(qualificationMapElement);
        
        Element functionsElement = XMLPersistenceHelper.getChildElement(qualificationMapElement, FUNCTIONS_TAG);
        loadElementsFromXML(functionMap, functionsElement);
        
        Element constructorsElement = XMLPersistenceHelper.getChildElement(qualificationMapElement, CONSTRUCTORS_TAG);
        loadElementsFromXML(constructorMap, constructorsElement);
        
        Element typesElement = XMLPersistenceHelper.getChildElement(qualificationMapElement, TYPES_TAG);
        loadElementsFromXML(typeMap, typesElement);
        
        Element classesElement = XMLPersistenceHelper.getChildElement(qualificationMapElement, CLASSES_TAG);
        loadElementsFromXML(classMap, classesElement);
    }
}