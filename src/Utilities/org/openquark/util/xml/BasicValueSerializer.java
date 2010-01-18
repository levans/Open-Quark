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
 * BasicValueSerializer.java
 * Created: Feb 11, 2005
 * By: ksit
 */
package org.openquark.util.xml;

import java.awt.Color;
import java.net.MalformedURLException;
import java.net.URL;

import org.openquark.util.Messages;
import org.openquark.util.time.Time;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * Standard implementation of the <code>XMLElementSerializer</code> interface
 * for serializing basic types of attribute values.  Basic attribute values
 * are values that can be easily converted to or from strings.
 */
public abstract class BasicValueSerializer implements XMLElementSerializer {
    
    /** Use this message bundle to dig up localized messages */
    private static final Messages messages = PackageMessages.instance;
    
    /** The name of the attribute that holds the value of the attribute */
    public static final String VALUE_ATTRIBUTE = "Value"; //$NON-NLS-1$
    
    /** {@inheritDoc} */
    public Object loadFromElement(XMLSerializationManager manager, Element element) {
        String text = element.getAttribute(VALUE_ATTRIBUTE);
        return text == null ? null : stringToObject(text);
    }
    
    /** {@inheritDoc} */
    public void storeToElement(XMLSerializationManager manager, Element element, Object value) {
        String text = objectToString(value);
        if (text != null) {
            Document doc = element.getOwnerDocument();
            Element attrElement = doc.createElement(getTagName());
            element.appendChild(attrElement);
            attrElement.setAttribute(VALUE_ATTRIBUTE, text);
        }
    }
    
    /**
     * Returns the tag name that stores this element.
     * @return String
     */
    protected abstract String getTagName();
    
    /**
     * Converts a string to an attribute value of a specific type.
     * <p>
     * Subclasses must override this method.
     * @param valueString
     * @return Object
     */
    protected abstract Object stringToObject(String valueString);
    
    /**
     * Converts an object to its string presentation.  By default, this method
     * returns <code>null</code> if the given object is <code>null</code>.  Otherwise,
     * this simply method invokes <code>toString()</code> on the object.
     * <p>
     * Subclasses can override this method.
     * @param object
     * @return String
     */
    protected String objectToString(Object object) {
        return object == null ? null : object.toString();
    }
    
    /**
     * This serializer serializes a string attribute value as XML element.
     */
    public static class StringValueSerializer extends BasicValueSerializer {
        
        /** The "root" tag name of a string attribute node */
        public static final String ROOT_TAG = "StringAttribute"; //$NON-NLS-1$

        /** {@inheritDoc} */
        protected String getTagName() {
            return ROOT_TAG;
        }
        
        /** {@inheritDoc} */
        protected Object stringToObject(String valueString) {
            return valueString;
        }
    }
    
    /**
     * This serializer serializes a string attribute value as XML element.
     */
    public static class HyperlinkValueSerializer extends BasicValueSerializer {
        
        /** The "root" tag name of a string attribute node */
        public static final String ROOT_TAG = "HyperlinkAttribute"; //$NON-NLS-1$
        
        /** {@inheritDoc} */
        protected String getTagName() {
            return ROOT_TAG;
        }
        
        /** {@inheritDoc} */
        protected Object stringToObject(String valueString) {
            try {
                return new URL(valueString);
            } catch (MalformedURLException ex) {
                return null;
            }
        }
    }
    
    /**
     * This serializer serializes a boolean attribute value as XML element.
     */
    public static class BooleanValueSerializer extends BasicValueSerializer {
        
        /** The "root" tag name of a boolean attribute node */
        public static final String ROOT_TAG = "BooleanAttribute"; //$NON-NLS-1$
        
        /** {@inheritDoc} */
        protected String getTagName() {
            return ROOT_TAG;
        }
        
        /** {@inheritDoc} */
        protected Object stringToObject(String valueString) {
            return Boolean.valueOf(valueString);
        }
    }
    
    /**
     * This serializer serializes an integer attribute value as XML element.
     */
    public static class IntegerValueSerializer extends BasicValueSerializer {
        
        /** The "root" tag name of an integer attribute node */
        public static final String ROOT_TAG = "IntegerAttribute"; //$NON-NLS-1$
        
        /** {@inheritDoc} */
        protected String getTagName() {
            return ROOT_TAG;
        }
        
        /** {@inheritDoc} */
        protected Object stringToObject(String valueString) {
            try {
                return new Integer(valueString);
            } catch (NumberFormatException nfe) {
                return null;
            }
        }
    }
    
    /**
     * This serializer serializes a double attribute value as XML element.
     */
    public static class DoubleValueSerializer extends BasicValueSerializer {
        
        /** The "root" tag name of a double attribute node */
        public static final String ROOT_TAG = "DoubleAttribute"; //$NON-NLS-1$
        
        /** {@inheritDoc} */
        protected String getTagName() {
            return ROOT_TAG;
        }
        
        /** {@inheritDoc} */
        protected Object stringToObject(String valueString) {
            try {
                return new Double(valueString);
            } catch (NumberFormatException nfe) {
                return null;
            }
        }
    }
    
    /**
     * This serializer serializes a color attribute value as XML element.
     */
    public static class ColorValueSerializer extends BasicValueSerializer {
        
        /** The "root" tag name of a time attribute node */
        public static final String ROOT_TAG = "ColorAttribute"; //$NON-NLS-1$
        
        /** {@inheritDoc} */
        protected String getTagName() {
            return ROOT_TAG;
        }
        
        /** {@inheritDoc} */
        protected Object stringToObject(String valueString) {
            // must be in the format of "#XXXXXX"
            if (valueString == null || valueString.length() != 7) {
                // TODO discuss about error handling and propagation
                throw new IllegalArgumentException("Invalid serialized color value: " + valueString); //$NON-NLS-1$
            }

            // Watch out for invalid hex value
            try {
                int red = Integer.parseInt(valueString.substring(1, 3), 16 /* radix */);            
                int green = Integer.parseInt(valueString.substring(3, 5), 16 /* radix */);            
                int blue = Integer.parseInt(valueString.substring(5, 7), 16 /* radix */);
                return new Color(red, green, blue);
            } catch (NumberFormatException nfe) {
                throw new IllegalArgumentException(messages.getString("InvalidSerializedColourVal", valueString)); //$NON-NLS-1$
            }
        }
        
        /** {@inheritDoc} */
        protected String objectToString(Object object) {
            Color color = (Color) object;
            StringBuilder sb = new StringBuilder("#"); //$NON-NLS-1$
            sb.append(ToHexString(color.getRed()));
            sb.append(ToHexString(color.getGreen()));
            sb.append(ToHexString(color.getBlue()));
            return sb.toString();
        }
        
        /**
         * Converts the given value to a hex string.
         * @param value
         * @return String
         */
        private static String ToHexString(int value) {
            // If the value is less than 16, then we need to pad a zero in front
            String hexString = Integer.toHexString(value);
            if (value < 16) {
                hexString = "0" + hexString; //$NON-NLS-1$
            }
            return hexString;
        }
    }
    
    /**
     * This serializer serializes a time attribute value as XML element.
     */
    public static class TimeValueSerializer extends BasicValueSerializer {
        
        /** The "root" tag name of a time attribute node */
        public static final String ROOT_TAG = "TimeAttribute"; //$NON-NLS-1$
        
        /** {@inheritDoc} */
        protected String getTagName() {
            return ROOT_TAG;
        }
        
        /** {@inheritDoc} */
        protected Object stringToObject(String valueString) {
            return Time.fromSerializedForm(valueString);
        }
        
        /** {@inheritDoc} */
        protected String objectToString(Object object) {
            return ((Time) object).toSerializedForm();
        }
    }
    
}