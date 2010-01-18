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
 * Attribute.java
 * Created: 5-Feb-2003
 * By: Rick Cameron
 */
package org.openquark.util.attributes;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.openquark.util.CaseInsensitiveMap;
import org.openquark.util.time.Time;
import org.openquark.util.xml.XMLSerializationManager;
import org.w3c.dom.Element;


/**
 * An Attribute is an association of a String (the name) and an Object (the
 * value, or list of values).
 * This class is immutable.
 */
public final class Attribute {

    private static final AttributeComparator attributeComparator = new AttributeComparator();
    
    /** Attribute type constants */
    public enum Type {
        UNKNOWN,
        COLOR,
        BOOLEAN,
        INTEGER,
        DOUBLE,
        STRING,
        ATTRIBUTE_SET,
        TIME
    }
    
    /** The name of the attribute. */
    private final String name;

    /** The attribute value (or list of values). */
    private final Object value;

    /** A method to correctly compare 2 attribute names. */
    public static boolean equalNames (String s1, String s2) {
        return CaseInsensitiveMap.equalsCaseInsensitive(s1, s2);
    }

    /**
     * Attribute constructor.
     * @param name    the attribute name
     * @param colour  the attribute value
     */
    public Attribute(String name, Color colour) {
        this(name, (Object) colour);
    }

    /**
     * Attribute constructor.
     * @param name     the attribute name
     * @param boolVal  the attribute value
     */
    public Attribute(String name, boolean boolVal) {
        this(name, Boolean.valueOf(boolVal));
    }

    /**
     * Attribute constructor.
     * @param name    the attribute name
     * @param intVal  the attribute value
     */
    public Attribute(String name, int intVal) {
        this(name, Integer.valueOf(intVal));
    }

    /**
     * Attribute constructor.
     * @param name       the attribute name
     * @param doubleVal  the attribute value
     */
    public Attribute(String name, double doubleVal) {
        this(name, Double.valueOf(doubleVal));
    }

    /**
     * Attribute constructor.
     * @param name       the attribute name
     * @param stringVal  the attribute value
     */
    public Attribute(String name, String stringVal) {
        this(name, (Object) stringVal);
    }

    /**
     * Attribute constructor.
     * @param name     the attribute name
     * @param timeVal  the attribute value
     */
    public Attribute(String name, Time timeVal) {
        this(name, (Object) timeVal);
    }

    /**
     * Attribute constructor.
     * @param name     the attribute name
     * @param attrSet  the attribute value
     */
    public Attribute(String name, AttributeSet attrSet) {
        this(name, (Object) attrSet);
    }

    /**
     * Attribute constructor.
     * @param name    the attribute name
     * @param values  the attribute values
     */
    public Attribute(String name, Collection<?> values) {
        this(name, (Object) values);
    }

    /**
     * Attribute constructor.
     * @param name   the attribute name
     * @param value  the attribute value
     */
    public Attribute (String name, Object value) {
        this.name = name;

        if (value instanceof Collection) {
            // Check that all items in the collection are the same type.
            // and of a type supported by this class.
            Collection<?> collection = (Collection<?>) value;
            if (collection.isEmpty()) {
                this.value = Collections.EMPTY_LIST;
            } else {
                List<Object> valueList = new ArrayList<Object>();
                Class<?> firstValueType = null;

                // Leave out any null values or values of types not supported by this class.
                // Also, only add values which are of the same type as the first value added.
                for (Object val : collection) {
                    if (val != null) {
                        if (firstValueType == null && isSupportedAttributeType(val.getClass())) {
                            firstValueType = val.getClass();
                            valueList.add(val);
                        }
                        else if (val.getClass().equals(firstValueType)){
                            valueList.add(val);
                        }
                    }
                }
                
                this.value = valueList;
            }
        }
        else {
            this.value = value;
        }
    }

    /**
     * Returns whether the specified value type can be stored as an attribute value.
     * <p>
     * Subclasses can override this method to change the supported attribute type set.
     * @param valueClass  the class of a value
     * @return boolean <code>true</code> if the value type can be stored as an attribute value
     */
    protected boolean isSupportedAttributeType(Class<?> valueClass) {
        // The supported attribute type is primarily determined by the serializer
        XMLSerializationManager serializer = XMLSerializationManager.getDefaultInstance();
        return serializer.getSupportedValueClasses().contains(valueClass);
    }

    /**
     * @see java.lang.Object#toString()
     * @return a string to assist in debugging
     */
    public String toString () {
        return getName () + "=" + valueToString(value); //$NON-NLS-1$
    }

    /**
     * A helper function for converting values to strings.
     * The string is intended for debugging purposes only. 
     * @param value  an attribute value (or list of values)
     * @return a human readable string representing the value
     */
    private static String valueToString (Object value) {
        if (value == null) {
            return "<null>"; //$NON-NLS-1$
        }
        else if (value instanceof Color) {
            Color colour = (Color) value;
            int red = colour.getRed();
            int green = colour.getGreen();
            int blue = colour.getBlue();
            return "R:" + red + " G:" + green + " B:" + blue; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }
        else if (value instanceof List) {
            List<?> list = (List<?>) value;
            StringBuilder sb = new StringBuilder("["); //$NON-NLS-1$

            for (int valueN = 0, nValues = list.size(); valueN < nValues; ++valueN) {
                sb.append (valueToString (list.get (valueN)));
                if (valueN < nValues - 1)
                    sb.append(", "); //$NON-NLS-1$
            }

            sb.append(']');
            return sb.toString();
        }
        else if (value instanceof Boolean) {
            return (((Boolean) value).booleanValue()) ? "True" : "False"; //$NON-NLS-1$ //$NON-NLS-2$
        }
        else {
            return value.toString();
        }
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass())
            return false;

        Attribute other = (Attribute) obj;

        if ((value == null) != (other.value == null))
            return false;

        if (value != null && !value.equals(other.value))
            return false;

        if (!equalNames(getName(), other.getName()))
            return false;

        return true;
    }

    /** Cache the hash code for the attribute, as it is relatively expensive to calculate. */
    private int hash = 0;
    
    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        // Note: ensure any modifications are thread safe.  See java.lang.String for an example.
        
        int h = hash;
        if (h == 0) {
            h = 37 + CaseInsensitiveMap.caseInsensitiveHashCode(getName());

            if (value != null) {
                h = h * 17 + value.hashCode();
            }

            hash = h;
        }
        
        return h;
    }

    /**
     * Returns the attribute name.
     * @return the attribute name
     */
    public String getName () {
        return name;
    }

    /**
     * Returns a single value for the attribute.
     * If there are multiple values, then the last one in the list (if any) will be returned.
     * @return a single value of the attribute
     */
    public Object getSingleValue () {
        if (value instanceof List) {
            List<?> l  = (List<?>) value;
            if (l.isEmpty ())
                return null;

            return l.get (l.size () - 1);
        } else {
            return value;
        }
    }

    /**
     * Returns the list of values for the attribute.
     * If there is only one value, then it will be put into a list.
     * @return the list of the attribute values
     */
    public List<?> getValues () {
        if (value instanceof List) {
            return Collections.unmodifiableList((List<?>)value);
        }
        else if (value instanceof Collection) {
            return new ArrayList<Object> ((Collection<?>)value);
        }
        else {
            return Collections.singletonList (value);
        }
    }

    /**
     * Load the attribute from the specified XML element.
     * @param attrElement  the root element for the attribute XML data
     * @return the new attribute created by loading it from the XML element
     */
    public static Attribute Load (Element attrElement) {
        XMLSerializationManager serializer = XMLSerializationManager.getDefaultInstance();
        return (Attribute) serializer.loadFromElement(attrElement,
                XMLSerializationManager.getAttributeSerializer());
    }

    /**
     * Store the attribute in the specified XML element.
     * @param parentElem  the parent XML element under which the attribute data will be stored
     */
    public void store (Element parentElem) {
        XMLSerializationManager serializer = XMLSerializationManager.getDefaultInstance();
        serializer.storeToElement(parentElem, this,
                XMLSerializationManager.getAttributeSerializer());
    }

    /**
     * Returns the {@link Attribute.Type} enum
     */
    public Type getAttributeType() {
        Object val = getSingleValue();

        if (val instanceof Color)
            return Type.COLOR;
        else if (val instanceof Boolean)
            return Type.BOOLEAN;
        else if (val instanceof Integer)
            return Type.INTEGER;
        else if (val instanceof Double)
            return Type.DOUBLE;
        else if (val instanceof String)
            return Type.STRING;
        else if (val instanceof AttributeSet)
            return Type.ATTRIBUTE_SET;
        else if (val instanceof Time)
            return Type.TIME;

        return Type.UNKNOWN;
    }
    
    /**
     * This is a helper for CAL - as CAL still needs to deal with ordinals
     * @return
     *  0 - Unknown
     *  1 - Colour
     *  2 - Boolean
     *  3 - Integer
     *  4 - Double
     *  5 - String
     *  6 - AttributeSet
     *  7 - Time
     * @deprecated
     */
    public int getAttributeTypeOrdinal() {
        return getAttributeType().ordinal();
    }
    
    /**
     * Creates a new attribute with the specified name and value list.
     * @param name    the attribute name
     * @param values  the value list for the attribute
     * @return a new attribute
     */
    public static Attribute makeAttribute(String name, List<?> values) {
        return new Attribute(name, values);
    }

    /**
     * Returns a <code>Comparator</code> that compares two attributes by their names.
     * @return Comparator
     */
    public static Comparator<Attribute> getNameComparator() {
        return attributeComparator;
    }
       
    /**
     * A <code>Comparator</code> that compares two attributes by their names.
     */
    private static final class AttributeComparator implements Comparator<Attribute> {
        public int compare(Attribute attr1, Attribute attr2) {
            return attr1.getName().compareToIgnoreCase(attr2.getName());
        } 
    }
}
