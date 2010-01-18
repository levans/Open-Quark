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
 * AttributeSet.java
 * Created: 22-Apr-2004
 * By: Richard Webster
 */
package org.openquark.util.attributes;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openquark.util.CaseInsensitiveMap;
import org.openquark.util.ObjectUtils;
import org.openquark.util.time.Time;
import org.openquark.util.xml.XMLSerializationManager;
import org.w3c.dom.Element;


/**
 * A collection of attributes which can be accessed by name.
 */
public final class AttributeSet {

    /** This map hold the attribute values. */
    private final Map<String, Attribute> attributes;

    /**
     * AttributeSet constructor.
     */
    public AttributeSet() {
        this(true);
    }

    /**
     * AttributeSet constructor.
     */
    public AttributeSet(boolean caseInsensitive) {
        super();
        if (caseInsensitive) {
            attributes = new CaseInsensitiveMap<Attribute>();
        } else {
            attributes = new HashMap<String, Attribute>();
        }
    }

    /**
     * AttributeSet constructor.
     * @param other  another attribute set
     */
    public AttributeSet (AttributeSet other) {
        if (other.attributes instanceof CaseInsensitiveMap) {
            this.attributes = new CaseInsensitiveMap<Attribute>(other.attributes);
        } else {
            this.attributes = new HashMap<String, Attribute>(other.attributes);
        }
    }

    /**
     * AttributeSet constructor.
     * @param attributes  a collection of attributes.
     */
    public AttributeSet (List<Attribute> attributes) {
        this();
        setAttributes(attributes);
    }

    /**
     * @see java.lang.Object#toString()
     * @return a string representation to assist in debugging
     */
    public String toString () {
        StringBuilder s = new StringBuilder ();

        for (Attribute attr : attributes.values()) {
            if (s.length () > 0)
                s.append (", "); //$NON-NLS-1$

            s.append (attr.toString ());
        }
        return s.toString ();
    }

    /**
     * @see java.lang.Object#equals(Object)
     * @param obj   the reference object with which to compare.
     * @return <code>true</code> if this object is the same as the obj
     *              argument; <code>false</code> otherwise.
     */
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass())
            return false;

        AttributeSet other = (AttributeSet) obj;
        return attributes.equals(other.attributes);
    }

    /**
     * @see java.lang.Object#hashCode()
     * @return a hash code value for this object.
     */
    public int hashCode() {
        return attributes.hashCode();
    }

    /**
     * Returns True if there are no attributes in the set.
     * @return True if there are no attributes in the set
     */
    public boolean isEmpty () {
        return attributes.isEmpty ();
    }
    
    /**
     * Removes all attributes from this set.
     */
    public void clear() {
        attributes.clear();
    }

    /**
     * Returns whether an attribute with the specified name exists in the set.
     * Note that the name lookup is not case sensitive.
     * @param attributeName  the attribute name
     * @return True if an attribute with the specified name exists in the set
     */
    public boolean containsAttribute(String attributeName) {
        return attributes.containsKey(attributeName);
    }

    /**
     * Returns the attribute with the specified name, or null if none exists.
     * Note that the name lookup is not case sensitive.
     * @param attributeName  the attribute name
     * @return the attribute with the specified name, or null if none exists
     */
    public Attribute getAttribute (String attributeName) {
        return attributes.get(attributeName);
    }

    /**
     * Returns a collection of all the attributes in the set.
     * @return a collection of all the attributes in the set
     */
    public Collection<Attribute> getAllAttributes () {
        return Collections.unmodifiableCollection(attributes.values());
    }
    
    /**
     * Returns a collection of all the keys found in the set.
     * @return Collection
     */
    public Collection<String> getAllAttributeNames() {
        return Collections.unmodifiableSet(attributes.keySet());
    }

    /**
     * Set the specified attribute in the set.
     * If an attribute with this name already exists in the set, then the new attribute
     * will replace it.
     * @param newAttr  the new attribute value
     */
    public void setAttribute (Attribute newAttr) {
        // Replace the attribute in the map.
        attributes.put (newAttr.getName(), newAttr);
    }

    /**
     * Appends the specified value(s) to the list of values for the specified attribute.
     * If it is not possible to append the value, then the attribute value will be
     * replaced by the specified attribute.
     * @param newAttr  the attribute value(s) to append
     */
    public void appendListAttribute (Attribute newAttr) {
        // Don't change anything if the new attribute has no values.
        List<?> newAttrValues = newAttr.getValues();
        if (newAttrValues.isEmpty()) {
            return;
        }

        // Check whether there is an existing attribute with this name.
        Attribute existingAttr = attributes.get(newAttr.getName ());
        if (existingAttr == null) {
            setAttribute(newAttr);
            return;
        }

        List<?> oldAttrValues = existingAttr.getValues();
        if (oldAttrValues.isEmpty()) {
            setAttribute(newAttr);
            return;
        }

        // Check whether the new and old attributes are of the same type.
        // Handle numeric types specially.
        Object firstOldValue = oldAttrValues.get(0);
        Object firstNewValue = newAttrValues.get(0);

        Class<?> oldAttrType = (firstOldValue instanceof Number) ? Number.class : firstOldValue.getClass();
        Class<?> newAttrType = (firstNewValue instanceof Number) ? Number.class : firstNewValue.getClass();

        // If the types are compatible, then merge the value lists.
        // Otherwise, just use the new attribute values.
        if (oldAttrType.equals(newAttrType)) {
            List<Object> mergedValues = new ArrayList<Object>(oldAttrValues);
            mergedValues.addAll (newAttrValues);

            Attribute mergedAttr = new Attribute (newAttr.getName(), mergedValues);
            setAttribute(mergedAttr);
        }
        else {
            setAttribute(newAttr);
        }
    }

    /**
     * Sets the specified attributes.
     * @param attrSet  another set of attributes
     */
    public void setAttributes(AttributeSet attrSet) {
        setAttributes(attrSet.attributes.values());
    }

    /**
     * Sets the specified attributes
     * @param attrs  a collection of attributes to be added
     */
    public void setAttributes(Collection<Attribute> attrs) {
        for (Attribute attribute : attrs) {
            setAttribute(attribute);
        }
    }

    /**
     * Removes the specified attribute from the set.
     * @param attributeName  the name of the attribute to remove
     */
    public void removeAttribute(String attributeName) {
        attributes.remove (attributeName);
    }

    /**
     * Removes any attributes from the set which appear in the specified set with the same value.
     */
    private void removeMatchingAttributes(AttributeSet attrsToRemove) {
        Set<String> matchingAttrKeys = new HashSet<String>(attributes.keySet());
        matchingAttrKeys.retainAll(attrsToRemove.attributes.keySet());

        for (String matchingAttrName : matchingAttrKeys) {
            Attribute thisAttr = attributes.get(matchingAttrName);
            Attribute otherAttr = attrsToRemove.attributes.get(matchingAttrName);
            
            if (ObjectUtils.equals(thisAttr, otherAttr)) {
                removeAttribute(matchingAttrName);
            }
        }
    }

    /**
     * Returns a single value for the attribute, or null if none exists.
     * @param attributeName  the name of the attribute for which the value should be fetched
     * @return the value for the attribute, or null if none exists
     */
    public Object getAttributeSingleValue(String attributeName) {
        Attribute attribute = attributes.get(attributeName);
        if (attribute == null)
            return null;

        return attribute.getSingleValue();
    }

    /**
     * Returns the values for the attribute, or an empty list if none exists.
     * @param attributeName  the name of the attribute for which the value should be fetched
     * @return the values for the attribute, or an empty list if none exists
     */
    public List<?> getAttributeValues(String attributeName) {
        Attribute attribute = attributes.get(attributeName);
        if (attribute == null)
            return Collections.emptyList();

        return attribute.getValues();
    }

    /**
     * Returns the value of the colour attribute with the specified name.
     * The name and default value from the attribute description will be used.
     * If there are multiple values for this attribute, the last one applied will be returned.
     * If a matching property is not found, then the defaultValue is returned.
     * 
     * @param attrDesc  the attribute description
     * @return the colour value of the attribute, or the default value from the attribute description
     */
    public Color getColourAttributeValue (AttributeDescription attrDesc) {
        Color defaultValue = (attrDesc.defaultValue instanceof Color) ? (Color) attrDesc.defaultValue : null;
        return getColourAttributeValue(attrDesc.name, defaultValue);
    }

    /**
     * Returns the value of the colour attribute with the specified name.
     * If there are multiple values for this attribute, the last one applied will be returned.
     * If a matching property is not found, then the defaultValue is returned.
     * 
     * @param attrName      the name of the attribute
     * @param defaultValue  the default value to return if there is no attribute value
     * @return the colour value of the attribute, or the default value if none is found
     */
    public Color getColourAttributeValue (String attrName, Color defaultValue) {
        Object value = getAttributeSingleValue(attrName);
        
        if (value instanceof Color)
            return (Color) value;
        
        return defaultValue;
    }

    /**
     * Returns the value of the specified integer attribute.
     * The name and default value from the attribute description will be used.
     * If there are multiple values for this attribute, the last one applied will be returned.
     * If a matching property is not found, then the defaultValue is returned.
     * 
     * @param attrDesc  the attribute description
     * @return the integer value of the attribute, or the default value from the attribute description
     */
    public int getIntegerAttributeValue (AttributeDescription attrDesc) {
        int defaultValue = (attrDesc.defaultValue instanceof Number) ? ((Number) attrDesc.defaultValue).intValue() : 0;
        return getIntegerAttributeValue(attrDesc.name, defaultValue);
    }

    /**
     * Returns the value of the integer attribute with the specified name.
     * If there are multiple values for this attribute, the last one applied will be returned.
     * If a matching property is not found, then the defaultValue is returned.
     * 
     * @param attrName      the name of the attribute
     * @param defaultValue  the default value to return if there is no attribute value
     * @return the integer value of the attribute, or the default value if none is found
     */
    public int getIntegerAttributeValue (String attrName, int defaultValue) {
        Object value = getAttributeSingleValue(attrName);

        if (value instanceof Number)
            return ((Number) value).intValue();

        return defaultValue;
    }

    /**
     * Returns the value of the specified double attribute.
     * The name and default value from the attribute description will be used.
     * If there are multiple values for this attribute, the last one applied will be returned.
     * If a matching property is not found, then the defaultValue is returned.
     * 
     * @param attrDesc  the attribute description
     * @return the double value of the attribute, or the default value from the attribute description
     */
    public double getDoubleAttributeValue (AttributeDescription attrDesc) {
        double defaultValue = (attrDesc.defaultValue instanceof Number) ? ((Number) attrDesc.defaultValue).doubleValue() : 0.0;

        return getDoubleAttributeValue(attrDesc.name, defaultValue);
    }

    /**
     * Returns the value of the double attribute with the specified name.
     * If there are multiple values for this attribute, the last one applied will be returned.
     * If a matching property is not found, then the defaultValue is returned.
     * 
     * @param attrName      the name of the attribute
     * @param defaultValue  the default value to return if there is no attribute value
     * @return the double value of the attribute, or the default value if none is found
     */
    public double getDoubleAttributeValue (String attrName, double defaultValue) {
        Object value = getAttributeSingleValue(attrName);
        
        if (value instanceof Number)
            return ((Number) value).doubleValue();

        return defaultValue;
    }

    /**
     * Returns the value of the specified boolean attribute.
     * The name and default value from the attribute description will be used.
     * If there are multiple values for this attribute, the last one applied will be returned.
     * If a matching property is not found, then the defaultValue is returned.
     * 
     * @param attrDesc  the attribute description
     * @return the boolean value of the attribute, or the default value from the attribute description
     */
    public boolean getBooleanAttributeValue (AttributeDescription attrDesc) {
        boolean defaultValue = (attrDesc.defaultValue instanceof Boolean) ? ((Boolean) attrDesc.defaultValue).booleanValue() : false;
        return getBooleanAttributeValue(attrDesc.name, defaultValue);
    }

    /**
     * Returns the value of the boolean attribute with the specified name.
     * If there are multiple values for this attribute, the last one applied will be returned.
     * If a matching property is not found, then the defaultValue is returned.
     * 
     * @param attrName      the name of the attribute
     * @param defaultValue  the default value to return if there is no attribute value
     * @return the boolean value of the attribute, or the default value if none is found
     */
    public boolean getBooleanAttributeValue (String attrName, boolean defaultValue) {
        Object value = getAttributeSingleValue(attrName);
        
        if (value instanceof Boolean)
            return ((Boolean) value).booleanValue();
        
        return defaultValue;
    }

    /**
     * Returns the value of the string attribute with the specified name.
     * The name and default value from the attribute description will be used.
     * If there are multiple values for this attribute, the last one applied will be returned.
     * If a matching property is not found, then the defaultValue is returned.
     * 
     * @param attrDesc  the attribute description
     * @return the string value of the attribute, or the default value from the attribute description
     */
    public String getStringAttributeValue (AttributeDescription attrDesc) {
        String defaultValue = (attrDesc.defaultValue instanceof String) ? (String) attrDesc.defaultValue : ""; //$NON-NLS-1$
        if (defaultValue == null) {
            defaultValue = ""; //$NON-NLS-1$
        }
        return getStringAttributeValue(attrDesc.name, defaultValue);
    }

    /**
     * Returns the value of the string attribute with the specified name.
     * If there are multiple values for this attribute, the last one applied will be returned.
     * If a matching property is not found, then the defaultValue is returned.
     * 
     * @param attrName      the name of the attribute
     * @param defaultValue  the default value to return if there is no attribute value
     * @return the string value of the attribute, or the default value if none is found
     */
    public String getStringAttributeValue (String attrName, String defaultValue) {
        Object value = getAttributeSingleValue(attrName);
        
        if (value instanceof String)
            return (String) value;
        
        return defaultValue;
    }

    /**
     * Returns the value of the time attribute with the specified name.
     * The name and default value from the attribute description will be used.
     * If there are multiple values for this attribute, the last one applied will be returned.
     * If a matching property is not found, then the defaultValue is returned.
     * 
     * @param attrDesc  the attribute description
     * @return the time value of the attribute, or the default value if none is found
     */
    public Time getTimeAttributeValue (AttributeDescription attrDesc) {
        Time defaultValue = (attrDesc.defaultValue instanceof Time) ? (Time) attrDesc.defaultValue : null;
        return getTimeAttributeValue(attrDesc.name, defaultValue);
    }

    /**
     * Returns the value of the time attribute with the specified name.
     * If there are multiple values for this attribute, the last one applied will be returned.
     * If a matching property is not found, then the defaultValue is returned.
     * 
     * @param attrName      the name of the attribute
     * @param defaultValue  the default value to return if there is no attribute value
     * @return the time value of the attribute, or the default value if none is found
     */
    public Time getTimeAttributeValue (String attrName, Time defaultValue) {
        Object value = getAttributeSingleValue(attrName);
        
        if (value instanceof Time)
            return (Time) value;
        
        return defaultValue;
    }

    /**
     * Returns the value of the attribute set attribute with the specified name.
     * The name and default value from the attribute description will be used.
     * If there are multiple values for this attribute, the last one applied will be returned.
     * If a matching property is not found, then the defaultValue is returned.
     * 
     * @param attrDesc  the attribute description
     * @return the attribute set value of the attribute, or the default value if none is found
     */
    public AttributeSet getAttributeSetValue (AttributeDescription attrDesc) {
        AttributeSet defaultValue = (attrDesc.defaultValue instanceof AttributeSet) ? (AttributeSet) attrDesc.defaultValue : new AttributeSet();
        if (defaultValue == null) {
            defaultValue = new AttributeSet();
        }
        return getAttributeSetValue(attrDesc.name, defaultValue);
    }

    /**
     * Returns the value of the attribute set attribute with the specified name.
     * If there are multiple values for this attribute, the last one applied will be returned.
     * If a matching property is not found, then the defaultValue is returned.
     * 
     * @param attrName      the name of the attribute
     * @param defaultValue  the default value to return if there is no attribute value
     * @return the attribute set value of the attribute, or the default value if none is found
     */
    public AttributeSet getAttributeSetValue (String attrName, AttributeSet defaultValue) {
        Object value = getAttributeSingleValue(attrName);

        if (value instanceof AttributeSet)
            return (AttributeSet) value;

        return defaultValue;
    }

    /**
     * Returns the value of the attribute set attribute with the specified name.
     * If there are multiple values for this attribute, the last one applied will be returned.
     * If a matching property is not found, then the defaultValue is returned.
     * 
     * @param attrName      the name of the attribute
     * @return the attribute set value of the attribute, or ab empty attribute set if none is found
     */
    public AttributeSet getAttributeSetValue (String attrName) {
        Object value = getAttributeSingleValue(attrName);

        if (value instanceof AttributeSet)
            return (AttributeSet) value;

        return new AttributeSet();
    }

    /**
     * Returns the colour values for the attribute, or an empty list if none exists.
     * @param attributeName  the name of the attribute for which the value should be fetched
     * @return the values for the attribute, or an empty list if none exists
     */
    public List<Color> getColourAttributeValues(String attributeName) {
        List<Color> colors = new ArrayList<Color>();
        filterValuesByType(getAttributeValues(attributeName), Color.class, colors);
        return colors;
    }

    /**
     * Returns the boolean values for the attribute, or an empty list if none exists.
     * @param attributeName  the name of the attribute for which the value should be fetched
     * @return the values for the attribute, or an empty list if none exists
     */
    public List<Boolean> getBooleanAttributeValues(String attributeName) {
        List<Boolean> booleans = new ArrayList<Boolean>();
        filterValuesByType(getAttributeValues(attributeName), Boolean.class, booleans);
        return booleans;
    }

    /**
     * Returns the int values for the attribute, or an empty list if none exists.
     * @param attributeName  the name of the attribute for which the value should be fetched
     * @return the values for the attribute, or an empty list if none exists
     */
    public List<Integer> getIntegerAttributeValues(String attributeName) {
        List<Number> numValues = new ArrayList<Number>(); 
        filterValuesByType(getAttributeValues(attributeName), Number.class, numValues);

        // Convert the Numbers to Integers, if necessary.
        List<Integer> intValues = new ArrayList<Integer>();
        for (Number numVal : numValues) {
            if (numVal instanceof Integer) {
                intValues.add((Integer)numVal);
            }
            else {
                intValues.add(Integer.valueOf(numVal.intValue()));
            }
        }
        return intValues;
    }

    /**
     * Returns the double values for the attribute, or an empty list if none exists.
     * @param attributeName  the name of the attribute for which the value should be fetched
     * @return the values for the attribute, or an empty list if none exists
     */
    public List<Double> getDoubleAttributeValues(String attributeName) {
        List<Number> numValues = new ArrayList<Number>(); 
        filterValuesByType(getAttributeValues(attributeName), Number.class, numValues);

        // Convert the Numbers to Doubles, if necessary.
        List<Double> dblValues = new ArrayList<Double>();
        for (Object numVal : numValues) {
            if (numVal instanceof Double) {
                dblValues.add((Double)numVal);
            } else if (numVal instanceof Number) {
                dblValues.add(Double.valueOf((((Number)numVal).doubleValue())));
            }
        }
        return dblValues;
    }

    /**
     * Returns the string values for the attribute, or an empty list if none exists.
     * @param attributeName  the name of the attribute for which the value should be fetched
     * @return the values for the attribute, or an empty list if none exists
     */
    public List<String> getStringAttributeValues(String attributeName) {
        List<String> strings = new ArrayList<String>();
        filterValuesByType(getAttributeValues(attributeName), String.class, strings);
        return strings;
    }

    /**
     * Returns the time values for the attribute, or an empty list if none exists.
     * @param attributeName  the name of the attribute for which the value should be fetched
     * @return the values for the attribute, or an empty list if none exists
     */
    public List<Time> getTimeAttributeValues(String attributeName) {
        List<Time> times = new ArrayList<Time>();
        filterValuesByType(getAttributeValues(attributeName), Time.class, times);
        return times;
    }

    /**
     * Returns the attribute set values for the attribute, or an empty list if none exists.
     * @param attributeName  the name of the attribute for which the value should be fetched
     * @return the values for the attribute, or an empty list if none exists
     */
    public List<AttributeSet> getAttributeSetValues(String attributeName) {
        List<AttributeSet> attributeSets = new ArrayList<AttributeSet>();
        filterValuesByType(getAttributeValues(attributeName), AttributeSet.class, attributeSets);
        return attributeSets;
    }

    /**
     * Returns the attribute values from the list which are of the specified type.
     * @param attrValues  a list of attribute values
     * @param filterType  the type of value to keep
     * @param filtered the list that will have the filtered contents
     */
    @SuppressWarnings({ "unchecked" })
    private static void filterValuesByType(List attrValues, Class filterType, List filtered) {

        filtered.clear();

        if (!attrValues.isEmpty()) {

            // Just check the first item in the list, since they should all be compatible anyway.
            Object firstValue = attrValues.get(0);
            if (filterType.isInstance(firstValue)) {
                filtered.addAll(attrValues);
            }
        }
    }
    
    /**
     * Sets the value of an attribute to an integer value. 
     * If an attribute with this name already exists in the set, then the new attribute
     * will replace it.
     * 
     * @param attrName  the attribute name
     * @param intVal    the new attribute value
     */
    public void setAttribute (String attrName, int intVal) {
        setAttribute(new Attribute(attrName, intVal));
    }

    /**
     * Sets the value of an attribute to a boolean value. 
     * If an attribute with this name already exists in the set, then the new attribute
     * will replace it.
     * 
     * @param attrName  the attribute name
     * @param boolVal   the new attribute value
     */
    public void setAttribute (String attrName, boolean boolVal) {
        setAttribute(new Attribute(attrName, boolVal));
    }

    /**
     * Sets the value of an attribute to a color value. 
     * If an attribute with this name already exists in the set, then the new attribute
     * will replace it.
     * 
     * @param attrName  the attribute name
     * @param colorVal  the new attribute value
     */
    public void setAttribute (String attrName, Color colorVal) {
        setAttribute(new Attribute(attrName, colorVal));
    }

    /**
     * Sets the value of an attribute to a double value. 
     * If an attribute with this name already exists in the set, then the new attribute
     * will replace it.
     * 
     * @param attrName  the attribute name
     * @param dblVal    the new attribute value
     */
    public void setAttribute (String attrName, double dblVal) {
        setAttribute(new Attribute(attrName, dblVal));
    }

    /**
     * Sets the value of an attribute to a string value. 
     * If an attribute with this name already exists in the set, then the new attribute
     * will replace it.
     * 
     * @param attrName  the attribute name
     * @param strVal    the new attribute value
     */
    public void setAttribute (String attrName, String strVal) {
        setAttribute(new Attribute(attrName, strVal));
    }    

    /**
     * Sets the value of an attribute to a time value. 
     * If an attribute with this name already exists in the set, then the new attribute
     * will replace it.
     * 
     * @param attrName  the attribute name
     * @param timeVal   the new attribute value
     */
    public void setAttribute (String attrName, Time timeVal) {
        setAttribute(new Attribute(attrName, timeVal));
    }  

    /**
     * Sets the value of an attribute to an attribute set value. 
     * If an attribute with this name already exists in the set, then the new attribute
     * will replace it.
     * 
     * @param attrName  the attribute name
     * @param attrSet   the new attribute value
     */
    public void setAttribute (String attrName, AttributeSet attrSet) {
        setAttribute(new Attribute(attrName, attrSet));
    } 

    /**
     * Sets the value of an attribute to a list of values. 
     * If an attribute with this name already exists in the set, then the new attribute
     * will replace it.
     * 
     * @param attrName    the attribute name
     * @param attrValues  the new attribute values
     */
    public void setAttribute (String attrName, List<?> attrValues) {
        setAttribute(new Attribute(attrName, attrValues));
    }

    /**
     * Appends an integer value to the list of values for the attribute. 
     * If the value cannot be appended, then the new value will replace the attribute value.
     * 
     * @param attrName  the attribute name
     * @param intVal    the attribute value to append
     */
    public void appendListAttribute (String attrName, int intVal) {
        appendListAttribute(new Attribute(attrName, intVal));
    }

    /**
     * Appends a boolean value to the list of values for the attribute. 
     * If the value cannot be appended, then the new value will replace the attribute value.
     * 
     * @param attrName  the attribute name
     * @param boolVal   the attribute value to append
     */
    public void appendListAttribute (String attrName, boolean boolVal) {
        appendListAttribute(new Attribute(attrName, boolVal));
    }

    /**
     * Appends a color value to the list of values for the attribute. 
     * If the value cannot be appended, then the new value will replace the attribute value.
     * 
     * @param attrName  the attribute name
     * @param colorVal  the attribute value to append
     */
    public void appendListAttribute (String attrName, Color colorVal) {
        appendListAttribute(new Attribute(attrName, colorVal));
    }

    /**
     * Appends a double value to the list of values for the attribute. 
     * If the value cannot be appended, then the new value will replace the attribute value.
     * 
     * @param attrName  the attribute name
     * @param dblVal    the attribute value to append
     */
    public void appendListAttribute (String attrName, double dblVal) {
        appendListAttribute(new Attribute(attrName, dblVal));
    }

    /**
     * Appends a string value to the list of values for the attribute. 
     * If the value cannot be appended, then the new value will replace the attribute value.
     * 
     * @param attrName  the attribute name
     * @param strVal    the attribute value to append
     */
    public void appendListAttribute (String attrName, String strVal) {
        appendListAttribute(new Attribute(attrName, strVal));
    }

    /**
     * Appends a time value to the list of values for the attribute. 
     * If the value cannot be appended, then the new value will replace the attribute value.
     * 
     * @param attrName  the attribute name
     * @param timeVal   the attribute value to append
     */
    public void appendListAttribute (String attrName, Time timeVal) {
        appendListAttribute(new Attribute(attrName, timeVal));
    }

    /**
     * Appends an attribute set value to the list of values for the attribute. 
     * If the value cannot be appended, then the new value will replace the attribute value.
     * 
     * @param attrName  the attribute name
     * @param attrSet   the attribute value to append
     */
    public void appendListAttribute (String attrName, AttributeSet attrSet) {
        appendListAttribute(new Attribute(attrName, attrSet));
    }

    /**
     * Compares the 2 attribute sets and returns the names of the attributes which are different
     * or are only in one set.
     * @param attrSet1  one attribute set
     * @param attrSet2  another attribute set
     * @return the set of attribute names which are different.
     */
    public static Set<String> getDifferingAttributeNames(AttributeSet attrSet1, AttributeSet attrSet2) {
        Set<String> differingAttrName = new HashSet<String>();

        // For each attribute in the first set, check whether there is an equivalent one in the second set.
        for (Attribute attr1 : attrSet1.attributes.values()) {
            Attribute attr2 = attrSet2.getAttribute(attr1.getName());

            if (attr2 == null || !attr1.equals(attr2)) {
                differingAttrName.add(attr1.getName());
            }
        }

        // For each attribute in the second set, check whether there is an attribute with the same name in the first set.
        // It isn't necessary to check whether the attributes are the same since this would have been done earlier.
        for (Attribute attr2 : attrSet2.attributes.values()) {
            Attribute attr1 = attrSet1.getAttribute(attr2.getName());

            if (attr1 == null) {
                differingAttrName.add(attr2.getName());
            }
        }
        return differingAttrName;
    }

    /**
     * Loads the attribute set from the specified XML element.
     * @param attributesElem  the root XML element for the attribute set data
     * @return a new attribute set
     */
    public static AttributeSet Load (Element attributesElem) {
        XMLSerializationManager serializer = XMLSerializationManager.getDefaultInstance();
        return (AttributeSet) serializer.loadFromElement(attributesElem,
                XMLSerializationManager.getAttributeSetSerializer());
    }

    /**
     * Stores the attribute set into the specified XML element.
     * @param attributesElem  the XML element into which the attribute set will be saved
     */
    public void store (Element attributesElem) {
        XMLSerializationManager serializer = XMLSerializationManager.getDefaultInstance();
        serializer.storeToElement(attributesElem, this,
                XMLSerializationManager.getAttributeSetSerializer());
    }

    /**
     * Returns a list containing all the attributes from the set.
     * @return a list containing all the attributes from the set
     */
    public List<Attribute> getAttributesAsList () {
        return new ArrayList<Attribute>(attributes.values());
    }

    /**
     * Creates a copy of the attribute set and sets the specified attribute in the copy.
     * @param attrName  the name of the attribute to set
     * @param value     the attribute value to set
     * @return a copy of the attribute set with the new attribute set
     */
    public AttributeSet setAttributeInCopy(String attrName, Color value) {
        AttributeSet attrSet = new AttributeSet(this);
        attrSet.setAttribute(new Attribute(attrName, value));
        return attrSet;
    }

    /**
     * Creates a copy of the attribute set and sets the specified attribute in the copy.
     * @param attrName  the name of the attribute to set
     * @param value     the attribute value to set
     * @return a copy of the attribute set with the new attribute set
     */
    public AttributeSet setAttributeInCopy(String attrName, int value) {
        AttributeSet attrSet = new AttributeSet(this);
        attrSet.setAttribute(new Attribute(attrName, value));
        return attrSet;
    }

    /**
     * Creates a copy of the attribute set and sets the specified attribute in the copy.
     * @param attrName  the name of the attribute to set
     * @param value     the attribute value to set
     * @return a copy of the attribute set with the new attribute set
     */
    public AttributeSet setAttributeInCopy(String attrName, double value) {
        AttributeSet attrSet = new AttributeSet(this);
        attrSet.setAttribute(new Attribute(attrName, value));
        return attrSet;
    }

    /**
     * Creates a copy of the attribute set and sets the specified attribute in the copy.
     * @param attrName  the name of the attribute to set
     * @param value     the attribute value to set
     * @return a copy of the attribute set with the new attribute set
     */
    public AttributeSet setAttributeInCopy(String attrName, boolean value) {
        AttributeSet attrSet = new AttributeSet(this);
        attrSet.setAttribute(new Attribute(attrName, value));
        return attrSet;
    }

    /**
     * Creates a copy of the attribute set and sets the specified attribute in the copy.
     * @param attrName  the name of the attribute to set
     * @param value     the attribute value to set
     * @return a copy of the attribute set with the new attribute set
     */
    public AttributeSet setAttributeInCopy(String attrName, String value) {
        AttributeSet attrSet = new AttributeSet(this);
        attrSet.setAttribute(new Attribute(attrName, value));
        return attrSet;
    }

    /**
     * Creates a copy of the attribute set and sets the specified attribute in the copy.
     * @param attrName  the name of the attribute to set
     * @param value     the attribute value to set
     * @return a copy of the attribute set with the new attribute set
     */
    public AttributeSet setAttributeInCopy(String attrName, Time value) {
        AttributeSet attrSet = new AttributeSet(this);
        attrSet.setAttribute(new Attribute(attrName, value));
        return attrSet;
    }

    /**
     * Creates a copy of the attribute set and sets the specified attribute in the copy.
     * @param attrName  the name of the attribute to set
     * @param value     the attribute value to set
     * @return a copy of the attribute set with the new attribute set
     */
    public AttributeSet setAttributeInCopy(String attrName, AttributeSet value) {
        AttributeSet attrSet = new AttributeSet(this);
        attrSet.setAttribute(new Attribute(attrName, value));
        return attrSet;
    }

    /**
     * Creates a copy of the attribute set and sets the specified attribute values in the copy.
     * @param attrName  the name of the attribute to set
     * @param value     the attribute values to set
     * @return a copy of the attribute set with the new attribute values set
     */
    public AttributeSet setAttributeInCopy(String attrName, List<?> value) {
        AttributeSet attrSet = new AttributeSet(this);
        attrSet.setAttribute(new Attribute(attrName, value));
        return attrSet;
    }

    /**
     * Creates a copy of the attribute set and sets the specified attributes.
     * @param attributes  the attributes to set
     * @return a copy of the attribute set with the new attributes set
     */
    public AttributeSet setAttributesInCopy(List<Attribute> attributes) {
        AttributeSet attrSet = new AttributeSet(this);
        attrSet.setAttributes(attributes);
        return attrSet;
    }

    /**
     * Creates a copy of the attribute set and sets the specified attributes.
     * @param attributes  the attributes to set
     * @return a copy of the attribute set with the new attributes set
     */
    public AttributeSet setAttributesInCopy(AttributeSet attributes) {
        AttributeSet attrSet = new AttributeSet(this);
        attrSet.setAttributes(attributes);
        return attrSet;
    }

    /**
     * Creates a copy of the attribute set and appends the specified attribute in the copy.
     * @param attrName  the name of the attribute to append
     * @param value     the attribute value to append
     * @return a copy of the attribute set with the new attribute set
     */
    public AttributeSet appendAttributeInCopy(String attrName, Color value) {
        AttributeSet attrSet = new AttributeSet(this);
        attrSet.appendListAttribute(new Attribute(attrName, value));
        return attrSet;
    }

    /**
     * Creates a copy of the attribute set and appends the specified attribute in the copy.
     * @param attrName  the name of the attribute to append
     * @param value     the attribute value to append
     * @return a copy of the attribute set with the new attribute set
     */
    public AttributeSet appendAttributeInCopy(String attrName, int value) {
        AttributeSet attrSet = new AttributeSet(this);
        attrSet.appendListAttribute(new Attribute(attrName, value));
        return attrSet;
    }

    /**
     * Creates a copy of the attribute set and appends the specified attribute in the copy.
     * @param attrName  the name of the attribute to append
     * @param value     the attribute value to append
     * @return a copy of the attribute set with the new attribute set
     */
    public AttributeSet appendAttributeInCopy(String attrName, double value) {
        AttributeSet attrSet = new AttributeSet(this);
        attrSet.appendListAttribute(new Attribute(attrName, value));
        return attrSet;
    }

    /**
     * Creates a copy of the attribute set and appends the specified attribute in the copy.
     * @param attrName  the name of the attribute to append
     * @param value     the attribute value to append
     * @return a copy of the attribute set with the new attribute set
     */
    public AttributeSet appendAttributeInCopy(String attrName, boolean value) {
        AttributeSet attrSet = new AttributeSet(this);
        attrSet.appendListAttribute(new Attribute(attrName, value));
        return attrSet;
    }

    /**
     * Creates a copy of the attribute set and appends the specified attribute in the copy.
     * @param attrName  the name of the attribute to append
     * @param value     the attribute value to append
     * @return a copy of the attribute set with the new attribute set
     */
    public AttributeSet appendAttributeInCopy(String attrName, String value) {
        AttributeSet attrSet = new AttributeSet(this);
        attrSet.appendListAttribute(new Attribute(attrName, value));
        return attrSet;
    }

    /**
     * Creates a copy of the attribute set and appends the specified attribute in the copy.
     * @param attrName  the name of the attribute to append
     * @param value     the attribute value to append
     * @return a copy of the attribute set with the new attribute set
     */
    public AttributeSet appendAttributeInCopy(String attrName, Time value) {
        AttributeSet attrSet = new AttributeSet(this);
        attrSet.appendListAttribute(new Attribute(attrName, value));
        return attrSet;
    }

    /**
     * Creates a copy of the attribute set and appends the specified attribute in the copy.
     * @param attrName  the name of the attribute to append
     * @param value     the attribute value to append
     * @return a copy of the attribute set with the new attribute set
     */
    public AttributeSet appendAttributeInCopy(String attrName, AttributeSet value) {
        AttributeSet attrSet = new AttributeSet(this);
        attrSet.appendListAttribute(new Attribute(attrName, value));
        return attrSet;
    }

    /**
     * Creates a copy of the attribute set and appends the specified attributes in the copy.
     * @param attrName  the name of the attribute to append
     * @param value     the attribute values to append
     * @return a copy of the attribute set with the new attribute values set
     */
    public AttributeSet appendAttributeInCopy(String attrName, List<?> value) {
        AttributeSet attrSet = new AttributeSet(this);
        attrSet.appendListAttribute(new Attribute(attrName, value));
        return attrSet;
    }
    
    /**
     * Method removeAttributeInCopy
     * 
     * @param attrName
     * @return Returns a copy of this AttributeSet, minus the Attribute with the given name 
     */
    public AttributeSet removeAttributeInCopy (String attrName) {
        AttributeSet attrSet = new AttributeSet(this);
        attrSet.removeAttribute(attrName);
        return attrSet;
    }

    /**
     * Returns a copy of this attribute set, minus any attributes in the specified set with the same values.
     */
    public AttributeSet removeMatchingAttributesInCopy(AttributeSet attrsToRemove) {
        AttributeSet attrSet = new AttributeSet(this);
        attrSet.removeMatchingAttributes(attrsToRemove);
        return attrSet;
    }
}
