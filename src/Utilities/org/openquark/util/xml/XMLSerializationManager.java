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
 * XMLSerializationManager.java
 * Created: Jul 29, 2004
 * By: Kevin Sit
 */
package org.openquark.util.xml;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openquark.util.Messages;
import org.openquark.util.attributes.Attribute;
import org.openquark.util.attributes.AttributeSet;
import org.openquark.util.time.Time;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * The XML serialization manager takes care of 2 things.  First, it maintains a set of XMLElementSerializer
 * objects.  These objects know how to do the actual serialization / deserialization work.  The manager
 * can lookup the appropriate serializer based on the type of object being stored or the type of XML tag
 * being read.
 * Second, the manager maintains contextual information in the form of an attribute set.  At various points
 * during loading or saving it may be necessary to temporarily hold onto some context information in order
 * to successfully load or store objects.
 */
public final class XMLSerializationManager {
    
    /** Use this message bundle to dig up localized messages */
    private static final Messages messages = PackageMessages.instance;
    
    /**
     * The default tag name for an <code>AttributeSet</code>.
     */
    public static final String ATTRIBUTE_SET_TAG_NAME = "AttributeSet"; //$NON-NLS-1$
    
    /**
     * The default tag name for an <code>Attribute</code>.
     */
    public static final String ATTRIBUTE_TAG_NAME = "Attribute"; //$NON-NLS-1$
    
    /**
     * Stores the name of an attribute object (in Java) in a XML element attribute.
     */
    public static final String NAME_ATTR_NAME = "Name"; //$NON-NLS-1$
    
    /**
     * Stores the value of an attribute object (in Java) under this name.  This name
     * is usually used as a tag name only.
     */
    public static final String VALUE_ATTR_NAME = "Value"; //$NON-NLS-1$
    
    /**
     * Stores an integer value under this name.  This name can be used as a tag name
     * or an attribute name.
     */
    public static final String INTEGER_ATTR_NAME = "IntValue"; //$NON-NLS-1$
    
    /**
     * Stores a double value under this name.  This name can be used as a tag name
     * or an attribute name.
     */
    public static final String DOUBLE_ATTR_NAME = "DoubleValue"; //$NON-NLS-1$
    
    /**
     * Stores a boolean value under this name.  This name can be used as a tag name
     * or an attribute name.
     */
    public static final String BOOLEAN_ATTR_NAME = "BoolValue";  //$NON-NLS-1$
    
    /**
     * Stores a string value under this name.  This name can be used as a tag name
     * or an attribute name.
     */
    public static final String STRING_ATTR_NAME = "StringValue";  //$NON-NLS-1$
    
    /**
     * Stores a color value under this name.  This name can be used as a tag name
     * or an attribute name.
     */
    public static final String COLOR_ATTR_NAME = "ColourValue";  //$NON-NLS-1$
    
    /**
     * Stores a time value under this name.  This name can be used as a tag name
     * or an attribute name.
     */
    public static final String TIME_ATTR_NAME = "TimeValue"; //$NON-NLS-1$
    
    /**
     * The default instance of this class, initialized with a default set of
     * element/attribute serializers.  This variable will be initialized when
     * <code>getDefaultInstance()</code> is called.
     */
    private static XMLSerializationManager defaultSerializer;
    
    /**
     * The parent serializer (optional).
     */
    private XMLSerializationManager parent;
    
    /**
     * This structure maps a tag name to a <code>XMLElementSerializer</code>.
     */
    private Map<String,XMLElementSerializer> elementSerializers = new HashMap<String,XMLElementSerializer>();
    
    /**
     * This structure maps a class to a <code>XMLElementSerializer</code>.
     * 
     * TODO it is a little bit ugly to keep new hash maps: one for tag name
     *   (used mainly for loading) and one for class (used mainly for saving).
     *   But let's leave it for now.
     */
    private Map<Class<?>, XMLElementSerializer> classSerializers = new LinkedHashMap<Class<?>, XMLElementSerializer>();
    
    /**
     * This structure holds a list of attribute serializers.
     */
    private List<XMLAttributeSerializer> attributeSerializers = new ArrayList<XMLAttributeSerializer>();
    
    /**
     * Attributes of the serializer.  Callers can push extra information to
     * the serializer as attributes, so that the element serializer can make
     * use of these extra information.
     */
    private AttributeSet attributes = new AttributeSet();
    
    /**
     * This class is not intended for subclassing and it should never be
     * instantiated directly.  Use the <code>newInstance()</code> method
     * instead.
     */
    private XMLSerializationManager() {
    }
    
    /**
     * Sets the parent serializer.  The parent serializer will be used if an attribute
     * cannot be found in this instance.  The parent is also useful for finding
     * <code>XMLElementSerializer</code> if there is no suitable serializer registered
     * with this instance.
     * @param parent
     */
    public void setParent(XMLSerializationManager parent) {
        this.parent = parent;
    }
    
    /**
     * Sets an attribute in the serializer.  Use this carefully since this serializer
     * can be a shared instance.
     * @param name
     * @param value
     */
    public void setAttribute(String name, Object value) {
        attributes.setAttribute(new Attribute(name, value));
    }
    
    /**
     * Unsets an attribute in the serializer.  Use this carefully since this serializer
     * can be a shared instance.
     * @param name
     */
    public void unsetAttribute(String name) {
        attributes.removeAttribute(name);
    }
    
    /**
     * Returns an attribute value associated with the given name.
     * @param name
     * @return Object
     */
    public Object getAttribute(String name) {
        // Always start with this instance
        Object value = attributes.getAttributeSingleValue(name);
        if (value != null) {
            return value;
        }
        // Then, ask its parent, if there is one
        return parent == null ? null : parent.getAttribute(name);
    }
    
    /**
     * Associates a tag name with the given serializer.  This method should be
     * called before the XML serializer is first used.  Existing serializer mapped
     * to the tag name will be replaced.
     * @param tagName XML tag name
     * @param klass object value class
     * @param serializer
     */
    public void addElementSerializer(String tagName, Class<?> klass,
            XMLElementSerializer serializer) {
        elementSerializers.put(tagName, serializer);
        classSerializers.put(klass, serializer);
    }
    
    /**
     * Adds a new <code>XMLAttributeSerializer</code> implementation.
     * @param serializer
     */
    public void addAttributeSerializer(XMLAttributeSerializer serializer) {
        attributeSerializers.add(serializer);
    }
    
    /**
     * Returns a set of tag names that are supported by this serializer instance.
     * @return Set
     */
    public Set<String> getSupportedTagNames() {
        return Collections.unmodifiableSet(elementSerializers.keySet());
    }
    
    /**
     * Returns a set of classes (or types) that are supported by this serializer instance.
     * @return Set
     */
    public Set<Class<?>> getSupportedValueClasses() {
        return Collections.unmodifiableSet(classSerializers.keySet());
    }
    
    /**
     * Loads the given XML element and return a value.  This method uses the element's
     * tag name to find a suitable serializer to deserialize the XML element.
     * @param element
     * @return Object
     */
    public Object loadFromElement(Element element) {
        // The local name is only valid if the element was created with a prefix and namespace, or
        // if the element was loaded from disk.  If the element was created with createElement()
        // then the local name is null and the node name should be used (needed for the unit tests).
        String tagName = element.getLocalName();
        if (tagName == null) {
            tagName = element.getNodeName();
        }
        
        return loadFromElement(element, getValueSerializer(tagName));
    }
    
    /**
     * Deserializes the input XML element with the given serializer.
     * @param element
     * @param serializer
     * @return Object
     */
    public Object loadFromElement(Element element, XMLElementSerializer serializer) {
        if (serializer != null) {
            return serializer.loadFromElement(this, element);
        }
        return null;
    }
    
    /**
     * Loads a collection of objects from the children of the given element.
     * This method uses the internal element serializers.
     * @param element
     * @return Collection
     */
    public List<?> loadFromChildElements(Element element) {
        List<Object> result = new ArrayList<Object>();
        List<Element> children = XMLPersistenceHelper.getChildElements(element);
        for (Element childElem : children) {
            Object o = loadFromElement(childElem);
            if (o != null) {
                result.add(o);
            }
        }
        return result;
    }

    /**
     * Loads a collection of objects from the children of the given element.
     * This method uses the internal element serializers.
     * @param element
     * @param loaded the list into which to add the loaded objects
     */
    @SuppressWarnings("unchecked")
    public void loadFromChildElements(Element element, List loaded) {
        List<Element> children = XMLPersistenceHelper.getChildElements(element);
        for (Element childElem : children) {
            Object o = loadFromElement(childElem);
            if (o != null) {
                loaded.add(o);
            }
        }
    }
    
    /**
     * Loads a single value from the element, using the internal list of
     * <code>XMLAttributeSerializer</code>.
     * @param element
     * @return Object
     */
    public Object loadValueFromAttribute(Element element) {
        return loadValueFromAttribute(element, attributeSerializers);
    }
    
    /**
     * Loads a single value from the element, using the given list of <code>XMLAttributeSerializer</code>.
     * @param element
     * @param serializers
     * @return Object
     */
    public Object loadValueFromAttribute(Element element, List<XMLAttributeSerializer> serializers) {
        for (XMLAttributeSerializer serializer : serializers) {
            Object val = serializer.loadFromAttribute(element);
            if (val != null) {
                return val;
            }
        }
        return null;
    }
    
    /**
     * Stores the given value to the input element.  This method delegates the task
     * to the serializer that matches the given value's type (or class).  The
     * serializer may choose to create sub-elements under the input element.
     * @param element
     * @param value
     * @return boolean
     */
    public boolean storeToElement(Element element, Object value) {
        if (value != null) {
            XMLElementSerializer serializer = getValueSerializer(value.getClass());
            if (serializer != null) {
                return storeToElement(element, value, serializer);
            }
        }
        return false;
    }
    
    /**
     * Stores the value to the input element, using the given <code>XMLElementSerializer</code>.
     * @param element
     * @param value
     * @param serializer
     * @return boolean
     */
    public boolean storeToElement(Element element, Object value, XMLElementSerializer serializer) {
        serializer.storeToElement(this, element, value);
        return true;
    }
    
    /**
     * Stores all the given values under the given parent element.  This method
     * delegates the task to the serializer that matches the value's type (class).  
     * @param element
     * @param values
     * @return boolean
     */
    public boolean storeToElement(Element element, Collection<?> values) {
        boolean success = true;
        for (Object v : values) {
            success &= storeToElement(element, v);
        }
        return success;
    }
    
    /**
     * Stores the given value as an attribute to the specified element.
     * @param element
     * @param value
     * @return boolean
     */
    public boolean storeAsAttribute(Element element, Object value) {
        return storeAsAttribute(element, value, attributeSerializers);
    }
    
    /**
     * Stores the given value as an attribute to the specified element, using
     * the given list of attribute serializers.
     * @param element
     * @param value
     * @param serializers
     * @return boolean
     */
    public boolean storeAsAttribute(Element element, Object value, List<XMLAttributeSerializer> serializers) {
        if (value != null) {
            Class<?> cls = value.getClass();
            for (XMLAttributeSerializer serializer : serializers) {
                if (serializer.isSerializable(cls)) {
                    serializer.storeToAttribute(element, value);
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Returns a <code>XMLElementSerializer</code> that is mapped to the given
     * tag name.
     * @param tagName
     * @return XMLElementSerializer
     */
    private XMLElementSerializer getValueSerializer(String tagName) {
        XMLElementSerializer serializer = elementSerializers.get(tagName);
        if (serializer == null) {
            if (parent != null) {
                serializer = parent.getValueSerializer(tagName);
            }
            if (serializer == null) {
                // TODO use proper logging
                System.err.println(messages.getString("NoSerializerForTag", tagName)); //$NON-NLS-1$
            }
        }
        return serializer;
    }
    
    /**
     * Returns a <code>XMLElementSerializer</code> that is mapped to the given
     * class.  If there no serializer mapped directly to the given class,
     * then the entire map will be checked to find the first serializer
     * that is associated to the superclass(es) of the given class.
     * @param cls
     * @return XMLElementSerializer
     * 
     * TODO it is possible to derive a mechanism to find the best match:
     * 1) calculate the "closeness" between the given class and the class
     *    used as the key and pick the one with the closest one 
     * 2) search the list from the end
     */
    private XMLElementSerializer getValueSerializer(Class<?> cls) {
        // First search for an exact match, which should handle most of the cases
        XMLElementSerializer serializer = classSerializers.get(cls);
        if (serializer != null) {
            return serializer;
        }
        
        // can't find a match, look for the first superclass
        for (Class<?> c : classSerializers.keySet()) {
            if (c.isAssignableFrom(cls)) {
                return classSerializers.get(c);
            }
        }

        // ask the parent
        if (parent != null) {
            serializer = parent.getValueSerializer(cls);
        }
        
        // log an error if we cannot find a suitable serializer: most likely an error
        if (serializer == null) {
            // TODO use proper logging
            System.err.println(messages.getString("NoSerializerForClass", cls.getName())); //$NON-NLS-1$
        }
        return serializer;
    }
    
    /**
     * Returns the shared instance of the <code>XMLSerializer</code>.  This serializer
     * is initialized with the default set of element and attribute serializers.
     * Since this instance is shared, callers should never add any new serializers
     * to the return value.
     * @return XMLSerializer
     */
    public static XMLSerializationManager getDefaultInstance() {
        if (defaultSerializer == null) {
            defaultSerializer = newInstance();
        }
        return defaultSerializer;
    }
    
    /**
     * A convenient method for returning a new instance of the XML serializer
     * with a default set of <code>XMLElementSerializer</code>.
     * @return XMLSerializer
     */
    public static XMLSerializationManager newInstance() {
        XMLSerializationManager serializer = newEmptyInstance();
        
        // Registers all built-in element serializers
        serializer.addElementSerializer(ATTRIBUTE_SET_TAG_NAME, AttributeSet.class,
                AttributeSetValueSerializer.SINGLETON);
        serializer.addElementSerializer(ATTRIBUTE_TAG_NAME, Attribute.class,
                AttributeValueSerializer.SINGLETON);
        serializer.addElementSerializer(INTEGER_ATTR_NAME, Integer.class,
                IntegerValueSerializer.SINGLETON);
        serializer.addElementSerializer(DOUBLE_ATTR_NAME, Double.class,
                DoubleValueSerializer.SINGLETON);
        serializer.addElementSerializer(BOOLEAN_ATTR_NAME, Boolean.class,
                BooleanValueSerializer.SINGLETON);
        serializer.addElementSerializer(STRING_ATTR_NAME, String.class,
                StringValueSerializer.SINGLETON);
        serializer.addElementSerializer(COLOR_ATTR_NAME, Color.class,
                ColorValueSerializer.SINGLETON);
        serializer.addElementSerializer(TIME_ATTR_NAME, Time.class,
                TimeValueSerializer.SINGLETON);
        
        // Registers all built-in attribute serializers
        serializer.addAttributeSerializer(IntegerValueSerializer.SINGLETON);
        serializer.addAttributeSerializer(DoubleValueSerializer.SINGLETON);
        serializer.addAttributeSerializer(BooleanValueSerializer.SINGLETON);
        serializer.addAttributeSerializer(StringValueSerializer.SINGLETON);
        serializer.addAttributeSerializer(ColorValueSerializer.SINGLETON);
        serializer.addAttributeSerializer(TimeValueSerializer.SINGLETON);

        return serializer;
    }
    
    /**
     * A convenient method for returning a new instance of the XML serializer
     * without any built-in <code>XMLElementSerializer</code>.
     * @return XMLSerializer
     */
    public static XMLSerializationManager newEmptyInstance() {
        return new XMLSerializationManager();
    }
    
    /**
     * A convenient method for returning a shared instance of the <code>XMLElementSerializer</code>
     * that can serialize/deserialize an <code>AttributeSet</code>.
     * @return XMLElementSerializer
     */
    public static XMLElementSerializer getAttributeSetSerializer() {
        return AttributeSetValueSerializer.SINGLETON;
    }
    
    /**
     * A convenient method for returning a shared instance of the <code>XMLElementSerializer</code>
     * that can serialize/deserialize an <code>Attribute</code>.
     * @return XMLElementSerializer
     */
    public static XMLElementSerializer getAttributeSerializer() {
        return AttributeValueSerializer.SINGLETON;
    }

    /**
     * An implementation of the <code>XMLElementSerializer</code> interface for
     * serializing and deserializing an <code>AttributeSet</code> instance.
     */
    public static class AttributeSetValueSerializer implements XMLElementSerializer, Cloneable {
        
        /**
         * A shared instance of this serializer.
         */
        public static final AttributeSetValueSerializer SINGLETON = new AttributeSetValueSerializer();
        
        /**
         * The tag to give elements created
         */
        public String tagName;
        
        /**
         * 
         */
        public AttributeSetValueSerializer() {
            this(null);
        }
        
        /**
         * Stores the attributes in this set under this tag name.  If the name is <code>null</code>,
         * the attributes will be stored as a flat list, without an enclosing tag.
         * @param tagName
         */
        public AttributeSetValueSerializer(String tagName) {
            this.tagName = tagName;
        }
        
        /**
         * @see org.openquark.util.xml.XMLElementSerializer#loadFromElement(org.openquark.util.xml.XMLSerializationManager, org.w3c.dom.Element)
         */
        public Object loadFromElement(XMLSerializationManager manager, Element element) {
            AttributeSet attributes = createNewAttributeSet();
            List<Element> children = XMLPersistenceHelper.getChildElements(element);
            for (Element childElem : children) {
                Object data = manager.loadFromElement(childElem);
                if (data instanceof Attribute) {
                    attributes.setAttribute((Attribute) data);
                }
            }
            return attributes;
        }
        
        /**
         * @see org.openquark.util.xml.XMLElementSerializer#storeToElement(org.openquark.util.xml.XMLSerializationManager, org.w3c.dom.Element, java.lang.Object)
         */
        public void storeToElement(XMLSerializationManager manager, Element element, Object value) {
            // Create the enclosing tag if necessary
            if (tagName != null) {
                Document document = element.getOwnerDocument ();
                Element attributeSetElement = document.createElement(tagName);
                element.appendChild(attributeSetElement);
                element = attributeSetElement;
            }
            
            // Sort the attributes alphabetically so that they will be saved in a consistent
            // order each time
            AttributeSet attributes = (AttributeSet)value;
            List<Attribute> sortedAttrs = attributes.getAttributesAsList();
            Collections.sort(sortedAttrs, Attribute.getNameComparator());
            for (Attribute attribute : sortedAttrs) {
                manager.storeToElement(element, attribute);
            }
        }
        
        /**
         * Returns a copy of this instance.
         * @return AttributeSetValueSerializer
         */
        public AttributeSetValueSerializer copyInstance() {
            try {
                return (AttributeSetValueSerializer) clone();
            } catch (CloneNotSupportedException cnse) {
                // should not happen
                return new AttributeSetValueSerializer(tagName);
            }
        }
        
        /**
         * Creates a new <code>AttributeSet</code> instance.
         * <p>
         * Subclasses can override this method.
         * @return AttributeSet
         */
        protected AttributeSet createNewAttributeSet() {
            return new AttributeSet();
        }
    }
    
    /**
     * An implementation of the <code>XMLElementSerializer</code> interface for
     * serializing and deserializing an <code>Attribute</code> instance.
     */
    public static class AttributeValueSerializer implements XMLElementSerializer, Cloneable {
        
        /**
         * A shared instance of this serializer.
         */
        public static final AttributeValueSerializer SINGLETON = new AttributeValueSerializer();
        
        /**
         * Use this tag name for the name of the XML tag.
         */
        public final String tagName;
        
        /**
         * Default constructor, tag set to ATTRIBUTE_TAG_NAME
         */
        public AttributeValueSerializer() {
            this(ATTRIBUTE_TAG_NAME);
        }
        
        /**
         * Construtor with a specfic tag.
         * @param tagName
         */
        public AttributeValueSerializer(String tagName) {
            this.tagName = tagName;
        }
        
        /**
         * @see org.openquark.util.xml.XMLElementSerializer#loadFromElement(org.openquark.util.xml.XMLSerializationManager, org.w3c.dom.Element)
         */
        public Object loadFromElement(XMLSerializationManager manager, Element element) {
            // Load the attribute name.
            // First try to load this from the Name attribute.
            String attributeName = element.getAttribute(NAME_ATTR_NAME);

            // If there is no Name attribute, then attempt to load the name from a child element.
            if (attributeName == null || attributeName.length() == 0) {
                Element nameElement = XMLPersistenceHelper.getChildElement(element, NAME_ATTR_NAME);
                if (nameElement != null)
                    attributeName = XMLPersistenceHelper.getChildText(nameElement);
            }

            // Make sure that an attribute name was specified.
            if (attributeName == null || attributeName.length() == 0)
                return null;

            // Check whether a value is stored as an attribute.
            Object attrValue = manager.loadValueFromAttribute(element);
            if (attrValue != null) {
                return createAttribute(attributeName, attrValue);
            }
            
            // Load the attribute values from the Value child element.
            Element valueNode = XMLPersistenceHelper.getChildElement(element, VALUE_ATTR_NAME);
            if (valueNode == null) {
                return null;
            }

            // If there is a "Value" node, loads the child element as a collection
            // of values
            Collection<?> values = manager.loadFromChildElements(valueNode);
            if (values.isEmpty()) {
                return null;
            } else {
                return createAttribute(attributeName, values);
            }
        }
        
        /**
         * @see org.openquark.util.xml.XMLElementSerializer#storeToElement(org.openquark.util.xml.XMLSerializationManager, org.w3c.dom.Element, java.lang.Object)
         */
        public void storeToElement(XMLSerializationManager manager, Element element, Object value) {
            // Don't store the attribute if it has no values.
            Attribute attribute = (Attribute) value;
            List<?> values = attribute.getValues();
            if (values.isEmpty()) {
                return;
            }
            
            Document document = element.getOwnerDocument ();
            Element attrElement = document.createElement(tagName);
            element.appendChild(attrElement);

            // Store the name member as an attribute.
            attrElement.setAttribute(NAME_ATTR_NAME, attribute.getName());

            // Store the value member.
            if (values.size() == 1) {
                // attempts to store the single value as an attribute, if it does not work, attempt
                // to store it as an element
                if (manager.storeAsAttribute(attrElement, attribute.getSingleValue())) {
                    return;
                }
            }

            // If the value cannot be stored as an attribute or there are multiple values,
            // then store these under a Value child element.
            Element valueElement = document.createElement(VALUE_ATTR_NAME);
            attrElement.appendChild(valueElement);
            for (Object valueObject : values) {
                if (valueObject instanceof AttributeSet) {
                    // We need special handling for attribute set.  Instead of
                    // storing the attributes as a plain list, create an attribute set
                    // element and use it to wrap around the attributes.  This is necessary
                    // because when we deserialize the values, we want to deserialize it
                    // as an AttributeSet instead of a Collection of Attribute's
                    XMLElementSerializer s = manager.getValueSerializer(valueObject.getClass());
                    if (s instanceof AttributeSetValueSerializer) {
                        AttributeSetValueSerializer valueSerializer = (AttributeSetValueSerializer) s;
                        if (valueSerializer.tagName == null) {
                            valueSerializer = valueSerializer.copyInstance();
                            valueSerializer.tagName = ATTRIBUTE_SET_TAG_NAME;
                        }
                        manager.storeToElement(valueElement, valueObject, valueSerializer);
                    }
                } else {
                    manager.storeToElement(valueElement, valueObject);
                }
            }
        }
        
        /**
         * Returns a new attribute with the given name-value pair.
         * @param name
         * @param value
         * @return Attribute
         */
        protected Attribute createAttribute(String name, Object value) {
            return new Attribute(name, value);
        }
        
        /**
         * Returns a new attribute with the given name, and the associated value collection.
         * @param name
         * @param values
         * @return Attribute
         */
        protected Attribute createAttribute(String name, Collection<?> values) {
            return new Attribute(name, values);
        }
        
        /**
         * Returns a copy of this serializer instance.
         * @return AttributeValueSerializer
         */
        public AttributeValueSerializer copyInstance() {
            try {
                return (AttributeValueSerializer) clone();
            } catch (CloneNotSupportedException cnse) {
                return new AttributeValueSerializer(tagName);
            }
        }
        
    }
    
    /**
     * An implementation of the <code>XMLElementSerializer</code> interface for
     * serializing and deserializing a <code>Number</code> object.
     */
    public static abstract class NumberValueSerializer
            implements XMLElementSerializer, XMLAttributeSerializer {
        
        /**
         * @see org.openquark.util.xml.XMLElementSerializer#loadFromElement(org.openquark.util.xml.XMLSerializationManager, org.w3c.dom.Element)
         */
        public Object loadFromElement(XMLSerializationManager manager, Element element) {
            return parse(XMLPersistenceHelper.getChildText(element));
        }

        /**
         * @see org.openquark.util.xml.XMLElementSerializer#storeToElement(org.openquark.util.xml.XMLSerializationManager, org.w3c.dom.Element, java.lang.Object)
         */
        public void storeToElement(XMLSerializationManager manager, Element element,
                Object value) {
            Document document = element.getOwnerDocument();

            // Store the integer value.
            Element numberElement = document.createElement(getName());
            numberElement.appendChild(document.createTextNode(format((Number) value)));
            element.appendChild(numberElement);
        }

        /**
         * @see org.openquark.util.xml.XMLAttributeSerializer#loadFromAttribute(org.w3c.dom.Element)
         */
        public Object loadFromAttribute(Element element) {
            String str = element.getAttribute(getName());
            if (str != null && str.length() != 0) {
                return parse(str);
            }
            return null;
        }
        
        /**
         * @see org.openquark.util.xml.XMLAttributeSerializer#storeToAttribute(org.w3c.dom.Element, java.lang.Object)
         */
        public void storeToAttribute(Element element, Object value) {
            element.setAttribute(getName(), format((Number) value));
        }
        
        /**
         * Subclasses must override this method to provide the tag/attribute name.
         * @return String
         */
        protected abstract String getName();
        
        /**
         * Subclasses must override this to provide an implementation for converting
         * a string to a number.
         * @param string
         * @return Number
         */
        protected abstract Number parse(String string);
        
        /**
         * Subclasses must override this to provide an implementation for formatting
         * a number to a string.  By default, this method invokes <code>toString()</code>
         * on the number.
         * @param number
         * @return String
         */
        protected String format(Number number) {
            return number.toString();
        }
        
    }

    /**
     * An implementation of the <code>XMLElementSerializer</code> interface and
     * the <code>XMLAttributeSerializer</code> interface for serializing and
     * deserializing an <code>Integer</code>.
     */
    public static class IntegerValueSerializer extends NumberValueSerializer {
        
        /**
         * A shared instance of this serializer.
         */
        public static final IntegerValueSerializer SINGLETON = new IntegerValueSerializer();
        
        /**
         * @see org.openquark.util.xml.XMLAttributeSerializer#isSerializable(java.lang.Class)
         */
        public boolean isSerializable(Class<?> c) {
            return Integer.class.isAssignableFrom(c);
        }
        
        /**
         * @see org.openquark.util.xml.XMLSerializationManager.NumberValueSerializer#getName()
         */
        protected String getName() {
            return INTEGER_ATTR_NAME;
        }
        
        /**
         * @see org.openquark.util.xml.XMLSerializationManager.NumberValueSerializer#parse(java.lang.String)
         */
        protected Number parse(String string) {
            return Integer.valueOf(string);
        }
    }
    
    /**
     * An implementation of the <code>XMLElementSerializer</code> interface and
     * the <code>XMLAttributeSerializer</code> interface for serializing and
     * deserializing a <code>Double</code>.
     */
    public static class DoubleValueSerializer extends NumberValueSerializer {
        
        /**
         * A shared instance of this serializer.
         */
        public static final DoubleValueSerializer SINGLETON = new DoubleValueSerializer();
        
        /**
         * @see org.openquark.util.xml.XMLAttributeSerializer#isSerializable(java.lang.Class)
         */
        public boolean isSerializable(Class<?> c) {
            return Double.class.isAssignableFrom(c);
        }

        /**
         * @see org.openquark.util.xml.XMLSerializationManager.NumberValueSerializer#getName()
         */
        protected String getName() {
            return DOUBLE_ATTR_NAME;
        }
        
        /**
         * @see org.openquark.util.xml.XMLSerializationManager.NumberValueSerializer#parse(java.lang.String)
         */
        protected Number parse(String string) {
            return Double.valueOf(string);
        }
    }
    
    /**
     * An implementation of the <code>XMLElementSerializer</code> interface and
     * the <code>XMLAttributeSerializer</code> interface for serializing and
     * deserializing a <code>Boolean</code>.
     */
    public static class BooleanValueSerializer implements XMLElementSerializer, XMLAttributeSerializer {
        
        private static final String FALSE = "false"; //$NON-NLS-1$
        private static final String TRUE = "true"; //$NON-NLS-1$
        
        /**
         * A shared instance of this serializer.
         */
        public static final BooleanValueSerializer SINGLETON = new BooleanValueSerializer();
        
        /**
         * @see org.openquark.util.xml.XMLAttributeSerializer#isSerializable(java.lang.Class)
         */
        public boolean isSerializable(Class<?> c) {
            return Boolean.class.isAssignableFrom(c);
        }
        
        /**
         * @see org.openquark.util.xml.XMLElementSerializer#loadFromElement(org.openquark.util.xml.XMLSerializationManager, org.w3c.dom.Element)
         */
        public Object loadFromElement(XMLSerializationManager manager, Element element) {
            String boolText = XMLPersistenceHelper.getChildText(element);
            return Boolean.valueOf(boolText.equalsIgnoreCase(TRUE)); 
        }

        /**
         * @see org.openquark.util.xml.XMLElementSerializer#storeToElement(org.openquark.util.xml.XMLSerializationManager, org.w3c.dom.Element, java.lang.Object)
         */
        public void storeToElement(XMLSerializationManager manager, Element element,
                Object value) {
            Document document = element.getOwnerDocument();

            // Store the boolean value.
            Element boolElement = document.createElement(BOOLEAN_ATTR_NAME);
            String booleanString = ((Boolean) value).booleanValue() ? TRUE : FALSE;  
            boolElement.appendChild(document.createTextNode(booleanString));
            element.appendChild (boolElement);
        }

        /**
         * @see org.openquark.util.xml.XMLAttributeSerializer#loadFromAttribute(org.w3c.dom.Element)
         */
        public Object loadFromAttribute(Element element) {
            String str = element.getAttribute(BOOLEAN_ATTR_NAME);
            if (str != null && str.length() != 0) {
                return Boolean.valueOf(str.equalsIgnoreCase (TRUE)); 
            }
            return null;
        }

        /**
         * @see org.openquark.util.xml.XMLAttributeSerializer#storeToAttribute(org.w3c.dom.Element, java.lang.Object)
         */
        public void storeToAttribute(Element element, Object value) {
            Boolean boolVal = (Boolean) value;
            element.setAttribute(BOOLEAN_ATTR_NAME, boolVal.booleanValue() ? TRUE : FALSE); 
        }
    }
    
    /**
     * An implementation of the <code>XMLElementSerializer</code> interface and
     * the <code>XMLAttributeSerializer</code> interface for serializing and
     * deserializing a <code>String</code>.
     */
    public static class StringValueSerializer implements XMLElementSerializer, XMLAttributeSerializer {
        
        /**
         * A shared instance of this serializer.
         */
        public static final StringValueSerializer SINGLETON = new StringValueSerializer();
        
        /**
         * @see org.openquark.util.xml.XMLAttributeSerializer#isSerializable(java.lang.Class)
         */
        public boolean isSerializable(Class<?> c) {
            return String.class.isAssignableFrom(c);
        }
        
        /**
         * @see org.openquark.util.xml.XMLElementSerializer#loadFromElement(org.openquark.util.xml.XMLSerializationManager, org.w3c.dom.Element)
         */
        public Object loadFromElement(XMLSerializationManager manager, Element element) {
            return XMLPersistenceHelper.getChildText(element);
        }

        /**
         * @see org.openquark.util.xml.XMLElementSerializer#storeToElement(org.openquark.util.xml.XMLSerializationManager, org.w3c.dom.Element, java.lang.Object)
         */
        public void storeToElement(XMLSerializationManager manager, Element element, Object value) {
            Document document = element.getOwnerDocument();

            // Store the string value.
            Element stringElement = document.createElement(STRING_ATTR_NAME);
            stringElement.appendChild(document.createTextNode((String) value));
            element.appendChild(stringElement);
        }
        
        /**
         * @see org.openquark.util.xml.XMLAttributeSerializer#loadFromAttribute(org.w3c.dom.Element)
         */
        public Object loadFromAttribute(Element element) {
            if (element.hasAttribute(STRING_ATTR_NAME)) {
                return element.getAttribute(STRING_ATTR_NAME);
            }
            return null;
        }
        
        /**
         * @see org.openquark.util.xml.XMLAttributeSerializer#storeToAttribute(org.w3c.dom.Element, java.lang.Object)
         */
        public void storeToAttribute(Element element, Object value) {
            element.setAttribute(STRING_ATTR_NAME, value.toString ());
        }
    }

    /**
     * An implementation of the <code>XMLElementSerializer</code> interface and
     * the <code>XMLAttributeSerializer</code> interface for serializing and
     * deserializing a <code>Color</code> object.
     */
    public static class ColorValueSerializer implements XMLElementSerializer, XMLAttributeSerializer {

        private static final String RED = "r"; //$NON-NLS-1$
        private static final String GREEN = "g"; //$NON-NLS-1$
        private static final String BLUE = "b"; //$NON-NLS-1$
        
        /**
         * A shared instance of this serializer.
         */
        public static final ColorValueSerializer SINGLETON = new ColorValueSerializer();
        
        /**
         * @see org.openquark.util.xml.XMLAttributeSerializer#isSerializable(java.lang.Class)
         */
        public boolean isSerializable(Class<?> c) {
            return Color.class.isAssignableFrom(c);
        }
        
        /**
         * @see org.openquark.util.xml.XMLElementSerializer#loadFromElement(org.openquark.util.xml.XMLSerializationManager, org.w3c.dom.Element)
         */
        public Object loadFromElement(XMLSerializationManager manager, Element element) {
            Element childElem;

            childElem = XMLPersistenceHelper.getChildElement(element, RED);
            String rText = (childElem == null) ? null : XMLPersistenceHelper.getChildText(childElem);

            childElem = XMLPersistenceHelper.getChildElement(element, GREEN);
            String gText = (childElem == null) ? null : XMLPersistenceHelper.getChildText(childElem);

            childElem = XMLPersistenceHelper.getChildElement(element, BLUE); 
            String bText = (childElem == null) ? null : XMLPersistenceHelper.getChildText(childElem);

            int r = (rText == null) ? 0 : Integer.parseInt(rText);
            int g = (gText == null) ? 0 : Integer.parseInt(gText);
            int b = (bText == null) ? 0 : Integer.parseInt(bText);

            return new Color (r, g, b); 
        }

        /**
         * @see org.openquark.util.xml.XMLElementSerializer#storeToElement(org.openquark.util.xml.XMLSerializationManager, org.w3c.dom.Element, java.lang.Object)
         */
        public void storeToElement(XMLSerializationManager manager, Element element,
                Object value) {
            Document document = element.getOwnerDocument ();

            Color c = (Color) value;
            Element colourElement = document.createElement ("Colour"); //$NON-NLS-1$
            colourElement.setAttribute(RED, Integer.toString(c.getRed ()));
            colourElement.setAttribute(GREEN, Integer.toString(c.getGreen ()));
            colourElement.setAttribute(BLUE, Integer.toString(c.getBlue ()));

            element.appendChild(colourElement);
        }
        
        /**
         * @see org.openquark.util.xml.XMLAttributeSerializer#loadFromAttribute(org.w3c.dom.Element)
         */
        public Object loadFromAttribute(Element element) {
            String valueText = element.getAttribute(COLOR_ATTR_NAME);
            if (valueText != null && valueText.length() != 0) {
                // The 3 colour components (rgb) should be separated by spaces.
                String [] rgbValues = valueText.split (" "); //$NON-NLS-1$

                if (rgbValues != null && rgbValues.length >= 3) {
                    int r = Integer.parseInt (rgbValues[0]);
                    int g = Integer.parseInt (rgbValues[1]);
                    int b = Integer.parseInt (rgbValues[2]);
                    return new Color (r, g, b);
                }
            }
            return null;
        }
        
        /**
         * @see org.openquark.util.xml.XMLAttributeSerializer#storeToAttribute(org.w3c.dom.Element, java.lang.Object)
         */
        public void storeToAttribute(Element element, Object value) {
            Color c = (Color) value;
            String colString = Integer.toString (c.getRed ()) + " " +  //$NON-NLS-1$
                               Integer.toString (c.getGreen ()) + " " +  //$NON-NLS-1$
                               Integer.toString (c.getBlue ());
            element.setAttribute(COLOR_ATTR_NAME, colString);
        }
    }
    
    /**
     * An implementation of the <code>XMLElementSerializer</code> interface and
     * the <code>XMLAttributeSerializer</code> interface for serializing and
     * deserializing a <code>Time</code> object.
     */
    public static class TimeValueSerializer implements XMLElementSerializer, XMLAttributeSerializer {

        /**
         * A shared instance of this serializer.
         */
        public static final TimeValueSerializer SINGLETON = new TimeValueSerializer();
        
        /**
         * @see org.openquark.util.xml.XMLAttributeSerializer#isSerializable(java.lang.Class)
         */
        public boolean isSerializable(Class<?> c) {
            return Time.class.isAssignableFrom(c);
        }
        
        /**
         * @see org.openquark.util.xml.XMLElementSerializer#loadFromElement(org.openquark.util.xml.XMLSerializationManager, org.w3c.dom.Element)
         */
        public Object loadFromElement(XMLSerializationManager manager, Element element) {
            String valueText = XMLPersistenceHelper.getChildText(element);
            return Time.fromSerializedForm(valueText);
        }
        
        /**
         * @see org.openquark.util.xml.XMLElementSerializer#storeToElement(org.openquark.util.xml.XMLSerializationManager, org.w3c.dom.Element, java.lang.Object)
         */
        public void storeToElement(XMLSerializationManager manager, Element element,
                Object value) {
            Document document = element.getOwnerDocument();

            // Store the time value.
            Time time = (Time) value;
            Element timeElement = document.createElement(TIME_ATTR_NAME);
            timeElement.appendChild(document.createTextNode(time.toSerializedForm()));
            element.appendChild(timeElement);
        }
        
        /**
         * @see org.openquark.util.xml.XMLAttributeSerializer#loadFromAttribute(org.w3c.dom.Element)
         */
        public Object loadFromAttribute(Element element) {
            String valueText = element.getAttribute(TIME_ATTR_NAME);
            if (valueText != null && valueText.length() != 0) {
                return Time.fromSerializedForm(valueText);
            }
            return null;
        }
        
        /**
         * @see org.openquark.util.xml.XMLAttributeSerializer#storeToAttribute(org.w3c.dom.Element, java.lang.Object)
         */
        public void storeToAttribute(Element element, Object value) {
            Time time = (Time) value;
            element.setAttribute(TIME_ATTR_NAME, time.toSerializedForm());
        }
    }
    
}