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
 * CALFeatureName.java
 * Creation date: Dec 3, 2003
 * By: Frank Worsley
 */
package org.openquark.cal.services;

import java.util.Iterator;
import java.util.List;

import org.openquark.cal.compiler.ClassInstance;
import org.openquark.cal.compiler.ClassInstanceIdentifier;
import org.openquark.cal.compiler.ClassMethod;
import org.openquark.cal.compiler.DataConstructor;
import org.openquark.cal.compiler.Function;
import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.ScopedEntity;
import org.openquark.cal.compiler.TypeClass;
import org.openquark.cal.compiler.TypeConstructor;
import org.openquark.util.xml.BadXMLDocumentException;
import org.openquark.util.xml.XMLPersistenceHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * A class that can be used to uniquely identify any CAL feature.
 * This class is serializable to XML.
 * @author Frank Worsley
 */
public final class CALFeatureName extends FeatureName {

    /** The feature type for a module feature name. */
    public static final FeatureType MODULE = new FeatureType("MODULE");
    
    /** The feature type for a function feature name. */
    public static final FeatureType FUNCTION = new FeatureType("FUNCTION");
    
    /** The feature type for a class method feature name. */
    public static final FeatureType CLASS_METHOD = new FeatureType("CLASS_METHOD");
    
    /** The feature type for a type constructor feature name. */
    public static final FeatureType TYPE_CONSTRUCTOR = new FeatureType("TYPE_CONSTRUCTOR");
    
    /** The feature type for a data constructor feature name. */
    public static final FeatureType DATA_CONSTRUCTOR = new FeatureType("DATA_CONSTRUCTOR");
    
    /** The feature type for a type class feature name. */
    public static final FeatureType TYPE_CLASS = new FeatureType("TYPE_CLASS");
    
    /** The feature type for a class instance feature name. */
    public static final FeatureType CLASS_INSTANCE = new FeatureType("CLASS_INSTANCE");
    
    /** The feature type for an argument feature name. */
    public static final FeatureType ARGUMENT = new FeatureType("ARGUMENT");
    
    /** The feature type for an instance method feature name. */
    public static final FeatureType INSTANCE_METHOD = new FeatureType("INSTANCE_METHOD");
    
    /** The string used to separate several parts in a feature name. */
    private static final String PART_SEPARATOR = "#";
    
    /**
     * Constructor for a CALFeatureName.
     * @param type
     * @param name
     */
    public CALFeatureName(FeatureType type, String name) {
        super(type, name);
    }

    /**
     * @return true if this feature name is for a scoped entity, false otherwise
     */
    public boolean isScopedEntityName() {
        return isScopedEntityFeatureType(getType());
    }
    
    /**
     * @param type the feature type to check.
     * @return true if the feature type is for a scoped entity, false otherwise
     */
    public static boolean isScopedEntityFeatureType(FeatureType type) {
        return type == FUNCTION ||
               type == CLASS_METHOD ||
               type == TYPE_CLASS ||
               type == TYPE_CONSTRUCTOR ||
               type == DATA_CONSTRUCTOR;
    }
    
    /**
     * @return this feature name converted to a QualifiedName
     * @throws UnsupportedOperationException if this feature name cannot be converted to a QualifiedName
     */
    public QualifiedName toQualifiedName() {
        
        if (this.isScopedEntityName()) {
            return QualifiedName.makeFromCompoundName(getName());
        }
        
        throw new UnsupportedOperationException("invalid feature type: " + getType());
    }
    
    /**
     * @return this feature name converted to a ClassInstanceIdentifier
     * @throws UnsupportedOperationException if this feature name cannot be converted to a ClassInstanceIdentifier
     */
    public ClassInstanceIdentifier toInstanceIdentifier() {
        
        FeatureType type = getType();
        
        if (type == CLASS_INSTANCE || type == INSTANCE_METHOD) {

            String[] pieces = getName().split(PART_SEPARATOR);
            
            QualifiedName typeClassName = QualifiedName.makeFromCompoundName(pieces[0]);
            String typeIdentifier = pieces[1];
            
            return ClassInstanceIdentifier.make(typeClassName, typeIdentifier);
        }
        
        throw new UnsupportedOperationException("invalid feature type: " + type);
    }
    
    /**
     * @return this feature name converted to an unqualified instance method name
     * @throws UnsupportedOperationException if this feature name cannot be converted to an unqualified instance method name
     */
    public String toInstanceMethodName() {
        
        FeatureType type = getType();
        
        if (type == INSTANCE_METHOD) {

            String[] pieces = getName().split(PART_SEPARATOR);
            return pieces[3];
        }
        
        throw new UnsupportedOperationException("invalid feature type: " + type);
    }
    
    /**
     * @return whether this feature name has a module name.
     */
    public boolean hasModuleName() {
        FeatureType type = getType();
        return isScopedEntityName() || type == CLASS_INSTANCE || type == INSTANCE_METHOD || type == MODULE;
    }
    
    /**
     * @return this feature name converted to a module name
     * @throws UnsupportedOperationException if this feature name cannot be converted to a module name
     */
    public ModuleName toModuleName() {
        
        FeatureType type = getType();
        
        if (this.isScopedEntityName()) {
            return this.toQualifiedName().getModuleName();
            
        } else if (type == CLASS_INSTANCE || type == INSTANCE_METHOD) {
            String[] pieces = getName().split(PART_SEPARATOR);
            return ModuleName.make(pieces[2]);
            
        } else if (type == MODULE) {
            return ModuleName.make(getName());
        }
        
        throw new UnsupportedOperationException("invalid feature type: " + type);
    }
    
    /**
     * Serialized this feature name to an XML representation that will be attached to the given parent node.
     * @param parentNode the parent node to attach the XML representation to
     */
    public void saveXML(Node parentNode) {

        Document document = (parentNode instanceof Document) ? (Document) parentNode : parentNode.getOwnerDocument();
        Element nameElement = document.createElementNS(WorkspacePersistenceConstants.WORKSPACE_NS,
                                                       WorkspacePersistenceConstants.FEATURE_NAME_TAG);
        parentNode.appendChild(nameElement);
        
        XMLPersistenceHelper.addTextElement(nameElement, WorkspacePersistenceConstants.FEATURE_NAME_TYPE_TAG, getType().getTypeName());
        XMLPersistenceHelper.addTextElement(nameElement, WorkspacePersistenceConstants.FEATURE_NAME_NAME_TAG, getName());
    }
    
    /**
     * Deserialize a CALFeatureName from its XML definition.
     * @param parentNode the node which contains the serialized xml definition, or null if the node does not correspond
     *   to the definition of a CALFeatureName.
     * @return the corresponding CALFeatureName.
     */
    public static CALFeatureName getFromXML(Node parentNode) {
        try {
            
            XMLPersistenceHelper.checkIsTagElement(parentNode, WorkspacePersistenceConstants.FEATURE_NAME_TAG);
            
            List<Element> elements = XMLPersistenceHelper.getChildElements(parentNode);
            Iterator<Element> it = elements.iterator();
            
            Node element = it.next();
            XMLPersistenceHelper.checkIsTagElement(element, WorkspacePersistenceConstants.FEATURE_NAME_TYPE_TAG);
            String typeString = XMLPersistenceHelper.getElementStringValue(element);

            element = it.next();
            XMLPersistenceHelper.checkIsTagElement(element, WorkspacePersistenceConstants.FEATURE_NAME_NAME_TAG);
            
            String name = XMLPersistenceHelper.getElementStringValue(element);
            FeatureType type;
            
            // Convert the type string to an actual type.
            if (typeString.equals(MODULE.getTypeName())) {
                type = MODULE;
                
            } else if (typeString.equals(FUNCTION.getTypeName())) {
                type = FUNCTION;

            } else if (typeString.equals(CLASS_METHOD.getTypeName())) {
                type = CLASS_METHOD;
            
            } else if (typeString.equals(TYPE_CONSTRUCTOR.getTypeName())) {
                type = TYPE_CONSTRUCTOR;
            
            } else if (typeString.equals(DATA_CONSTRUCTOR.getTypeName())) {
                type = DATA_CONSTRUCTOR;
            
            } else if (typeString.equals(TYPE_CLASS.getTypeName())) {
                type = TYPE_CLASS;
            
            } else if (typeString.equals(CLASS_INSTANCE.getTypeName())) {
                type = CLASS_INSTANCE;
            
            } else if (typeString.equals(INSTANCE_METHOD.getTypeName())) {
                type = INSTANCE_METHOD;
            
            } else if (typeString.equals(ARGUMENT.getTypeName())) {
                type = ARGUMENT;
            
            } else {
                throw new BadXMLDocumentException(parentNode, "unknown feature name type: " + typeString);
            }

            return new CALFeatureName(type, name);

        } catch (BadXMLDocumentException e) {
            // What to do??
            return null;
        }
    }
    
    /**
     * @param entity the scoped entity to a get a feature name for
     * @return the feature name for the given scoped entity
     */
    public static CALFeatureName getScopedEntityFeatureName(ScopedEntity entity) {
        
        if (entity instanceof Function) {
            return new CALFeatureName(FUNCTION, entity.getName().getQualifiedName());
            
        } else if (entity instanceof ClassMethod) {
            return new CALFeatureName(CLASS_METHOD, entity.getName().getQualifiedName());
            
        } else if (entity instanceof DataConstructor) {
            return new CALFeatureName(DATA_CONSTRUCTOR, entity.getName().getQualifiedName());
            
        } else if (entity instanceof TypeClass) {
            return new CALFeatureName(TYPE_CLASS, entity.getName().getQualifiedName());
            
        } else if (entity instanceof TypeConstructor) {
            return new CALFeatureName(TYPE_CONSTRUCTOR, entity.getName().getQualifiedName());
        }
        
        throw new IllegalArgumentException("entity type not supported: " + entity);
    }
    
    /**
     * @param name name of the function to get a feature name for
     * @return a feature name for a function with the given name
     */
    public static CALFeatureName getFunctionFeatureName(QualifiedName name) {
        return new CALFeatureName(FUNCTION, name.getQualifiedName());
    }
    
    /**
     * @param name name of the class method to get a feature name for
     * @return a feature name for a class method with the given name
     */
    public static CALFeatureName getClassMethodFeatureName(QualifiedName name) {
        return new CALFeatureName(CLASS_METHOD, name.getQualifiedName());
    }
    
    /**
     * @param name name of the type class to get a feature name for
     * @return a feature name for a type class with the given name
     */
    public static CALFeatureName getTypeClassFeatureName(QualifiedName name) {
        return new CALFeatureName(TYPE_CLASS, name.getQualifiedName());
    }
    
    /**
     * @param name name of the type constructor to get a feature name for
     * @return a feature name for a type constructor with the given name
     */
    public static CALFeatureName getTypeConstructorFeatureName(QualifiedName name) {
        return new CALFeatureName(TYPE_CONSTRUCTOR, name.getQualifiedName());
    }
    
    /**
     * @param name name of the data constructor to get a feature name for
     * @return a feature name for a data constructor with the given name
     */
    public static CALFeatureName getDataConstructorFeatureName(QualifiedName name) {
        return new CALFeatureName(DATA_CONSTRUCTOR, name.getQualifiedName());
    }
    
    /**
     * @param instance the class instance to get a feature name for
     * @return a feature name for the given class instance
     */
    public static CALFeatureName getClassInstanceFeatureName(ClassInstance instance) {
        return getClassInstanceFeatureName(instance.getIdentifier(), instance.getModuleName());
    }
    
    /**
     * @param identifier the class instance identifier
     * @param moduleName the module name the class instance is declared in
     * @return a feature name for a class instance with the given identifier which is declared in the given module
     */
    public static CALFeatureName getClassInstanceFeatureName(ClassInstanceIdentifier identifier, ModuleName moduleName) {
        return new CALFeatureName(CLASS_INSTANCE, identifier.getTypeClassName() + PART_SEPARATOR + identifier.getTypeIdentifier() + PART_SEPARATOR + moduleName);
    }
    
    /**
     * @param instance the class instance
     * @param methodName the name of the instance method to get a feature name for
     * @return a feature name for the given instance method
     */
    public static CALFeatureName getInstanceMethodFeatureName(ClassInstance instance, String methodName) {
        return getInstanceMethodFeatureName(instance.getIdentifier(), instance.getModuleName(), methodName);
    }
    
    /**
     * @param identifier the class instance identifier
     * @param moduleName the module name the class instance is declared in
     * @param methodName the name of the instance method to get a feature name for
     * @return a feature name for an instance method with the given identifier which is declared in the given module
     */
    public static CALFeatureName getInstanceMethodFeatureName(ClassInstanceIdentifier identifier, ModuleName moduleName, String methodName) {
        return new CALFeatureName(INSTANCE_METHOD, identifier.getTypeClassName() + PART_SEPARATOR + identifier.getTypeIdentifier() + PART_SEPARATOR + moduleName + PART_SEPARATOR + methodName);
    }
    
    /**
     * @param moduleName name of the module to get a feature name for
     * @return a feature name for a module with the given name
     */
    public static CALFeatureName getModuleFeatureName(ModuleName moduleName) {
        return new CALFeatureName(MODULE, moduleName.toSourceText());
    }
    
    /**
     * @param module module to get a feature name for
     * @return a feature name for the given module
     */
    public static CALFeatureName getModuleFeatureName(MetaModule module) {
        return new CALFeatureName(MODULE, module.getName().toSourceText());
    }
    
    /**
     * @param argNum the argument number to get a feature name for
     * @return a feature name for an argument with the given number
     */
    public static CALFeatureName getArgumentFeatureName(int argNum) {
        return new CALFeatureName(ARGUMENT, Integer.toString(argNum));
    }
}
