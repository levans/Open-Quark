/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_XmlBuilder.java)
 * was generated from CAL module: Cal.Utilities.XmlBuilder.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Utilities.XmlBuilder module from Java code.
 *  
 * Creation date: Wed Oct 17 14:04:57 PDT 2007
 * --!>
 *  
 */

package org.openquark.cal.module.Cal.Utilities;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceModel;

/**
 * This module contains types and functions for creating XML documents.
 * <p>
 * An <code>Cal.Utilities.XmlBuilder.XmlDocument</code> consists of a list of top-level <code>Cal.Utilities.XmlBuilder.XmlNode</code>s. 
 * <p>
 * An <code>Cal.Utilities.XmlBuilder.XmlNode</code> is either an <code>Cal.Utilities.XmlBuilder.XmlElement</code>, an <code>Cal.Utilities.XmlBuilder.XmlText</code>, an <code>Cal.Utilities.XmlBuilder.XmlCDataSection</code> or an <code>Cal.Utilities.XmlBuilder.XmlComment</code>. 
 * <p>
 * The function <code>Cal.Utilities.XmlBuilder.xmlDocumentToString</code> can be used to turn an <code>Cal.Utilities.XmlBuilder.XmlDocument</code> into a <code>Cal.Core.Prelude.String</code>.
 * <p>
 * The type classes <code>Cal.Utilities.XmlBuilder.XmlOutputable</code> and <code>Cal.Utilities.XmlBuilder.XmlInputable</code> provide a framework for serializing any type to XML.
 * <p>
 * This module provides implementations of these classes for several common types:
 * <ul>
 *  <li>
 *   <code>Cal.Core.Prelude.Boolean</code>
 *  </li>
 *  <li>
 *   <code>Cal.Graphics.Color.Color</code>
 *  </li>
 *  <li>
 *   <code>Cal.Core.Prelude.Double</code>
 *  </li>
 *  <li>
 *   <code>Cal.Core.Prelude.Int</code>
 *  </li>
 *  <li>
 *   <code>Cal.Core.Prelude.Integer</code>
 *  </li>
 *  <li>
 *   <code>Cal.Core.Prelude.List</code>
 *  </li>
 *  <li>
 *   <code>Cal.Core.Prelude.Long</code>
 *  </li>
 *  <li>
 *   <code>Cal.Utilities.Range.Range</code>
 *  </li>
 *  <li>
 *   <code>Cal.Core.Prelude.String</code>
 *  </li>
 *  <li>
 *   <code>Cal.Utilities.Time.Time</code>
 *  </li>
 * </ul>
 * <p>
 * Lists and records of serializable types are also handled.
 * 
 * @author Tom Haggie
 */
public final class CAL_XmlBuilder {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Utilities.XmlBuilder");

	/**
	 * This inner class (TypeClasses) contains constants
	 * and methods related to binding to CAL TypeClasses in the Cal.Utilities.XmlBuilder module.
	 */
	public static final class TypeClasses {
		/**
		 * This type class if for types that wish to be deserialized from XML Attributes (rather than Elements ) - so simple types
		 */
		public static final QualifiedName XmlAttributeInputable = 
			QualifiedName.make(
				CAL_XmlBuilder.MODULE_NAME, 
				"XmlAttributeInputable");

		/**
		 * This type class if for types that wish to be serialized as XML Attributes (rather than Elements) - so simple types
		 */
		public static final QualifiedName XmlAttributeOutputable = 
			QualifiedName.make(
				CAL_XmlBuilder.MODULE_NAME, 
				"XmlAttributeOutputable");

		/**
		 * This type class if for types that wish to be deserialized from XML Elements (rather than Attributes) - so complex types
		 */
		public static final QualifiedName XmlElementInputable = 
			QualifiedName.make(
				CAL_XmlBuilder.MODULE_NAME, 
				"XmlElementInputable");

		/**
		 * This type class if for types that wish to be serialized as XML Elements (rather than Attributes) - so complex types
		 */
		public static final QualifiedName XmlElementOutputable = 
			QualifiedName.make(
				CAL_XmlBuilder.MODULE_NAME, 
				"XmlElementOutputable");

		/**
		 * This type class is for any type that can be input from xml
		 */
		public static final QualifiedName XmlInputable = 
			QualifiedName.make(CAL_XmlBuilder.MODULE_NAME, "XmlInputable");

		/**
		 * This type class of for any type that can produce XML Elements or Attributes
		 */
		public static final QualifiedName XmlOutputable = 
			QualifiedName.make(CAL_XmlBuilder.MODULE_NAME, "XmlOutputable");

	}
	/**
	 * This inner class (TypeConstructors) contains constants
	 * and methods related to binding to CAL TypeConstructors in the Cal.Utilities.XmlBuilder module.
	 */
	public static final class TypeConstructors {
		/**
		 * XML attributes.
		 */
		public static final QualifiedName XmlAttribute = 
			QualifiedName.make(CAL_XmlBuilder.MODULE_NAME, "XmlAttribute");

		/**
		 * A CAL representation of an XML document.
		 */
		public static final QualifiedName XmlDocument = 
			QualifiedName.make(CAL_XmlBuilder.MODULE_NAME, "XmlDocument");

		/**
		 * An XML namespace, with an optional prefix.
		 * If a prefix is specified, then the generated XML text will include a namespace
		 * declaration which associates the namespace URI with the prefix, and the prefix will
		 * be used to qualify the names of elements and attributes to which it applies.
		 * If no prefix is specified, the the namespace will be explicitly specified for any 
		 * elements to which it applies (unless the same namespace is the default from an ancestor element). 
		 * Note that a prefix must be specified for any namespace applied to an attribute.
		 */
		public static final QualifiedName XmlNamespace = 
			QualifiedName.make(CAL_XmlBuilder.MODULE_NAME, "XmlNamespace");

		/**
		 * XML nodes (element, text, comments, etc...).
		 * <p>
		 * TODO: add other node types, as needed...
		 */
		public static final QualifiedName XmlNode = 
			QualifiedName.make(CAL_XmlBuilder.MODULE_NAME, "XmlNode");

		/**
		 * XML notations.
		 */
		public static final QualifiedName XmlNotation = 
			QualifiedName.make(CAL_XmlBuilder.MODULE_NAME, "XmlNotation");

	}
	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Utilities.XmlBuilder module.
	 */
	public static final class Functions {
		/**
		 * Adds the specified attributes to an XML element.
		 * An error will be thrown if the specified node is not an Element.
		 * @param original (CAL type: <code>Cal.Utilities.XmlBuilder.XmlNode</code>)
		 * @param newAttributes (CAL type: <code>[Cal.Utilities.XmlBuilder.XmlAttribute]</code>)
		 * @return (CAL type: <code>Cal.Utilities.XmlBuilder.XmlNode</code>) 
		 */
		public static final SourceModel.Expr addAttributesToXmlElement(SourceModel.Expr original, SourceModel.Expr newAttributes) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.addAttributesToXmlElement), original, newAttributes});
		}

		/**
		 * Name binding for function: addAttributesToXmlElement.
		 * @see #addAttributesToXmlElement(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName addAttributesToXmlElement = 
			QualifiedName.make(
				CAL_XmlBuilder.MODULE_NAME, 
				"addAttributesToXmlElement");

		/**
		 * Returns the local name of an XML attribute.
		 * This name will not include any namespace prefix.
		 * @param attr (CAL type: <code>Cal.Utilities.XmlBuilder.XmlAttribute</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 */
		public static final SourceModel.Expr attributeLocalName(SourceModel.Expr attr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.attributeLocalName), attr});
		}

		/**
		 * Name binding for function: attributeLocalName.
		 * @see #attributeLocalName(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName attributeLocalName = 
			QualifiedName.make(CAL_XmlBuilder.MODULE_NAME, "attributeLocalName");

		/**
		 * Returns the namespace (if any) for the attribute.
		 * @param attr (CAL type: <code>Cal.Utilities.XmlBuilder.XmlAttribute</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Maybe Cal.Utilities.XmlBuilder.XmlNamespace</code>) 
		 */
		public static final SourceModel.Expr attributeNamespace(SourceModel.Expr attr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.attributeNamespace), attr});
		}

		/**
		 * Name binding for function: attributeNamespace.
		 * @see #attributeNamespace(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName attributeNamespace = 
			QualifiedName.make(CAL_XmlBuilder.MODULE_NAME, "attributeNamespace");

		/**
		 * Returns the value of an XML attribute.
		 * @param attr (CAL type: <code>Cal.Utilities.XmlBuilder.XmlAttribute</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 */
		public static final SourceModel.Expr attributeValue(SourceModel.Expr attr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.attributeValue), attr});
		}

		/**
		 * Name binding for function: attributeValue.
		 * @see #attributeValue(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName attributeValue = 
			QualifiedName.make(CAL_XmlBuilder.MODULE_NAME, "attributeValue");

		/**
		 * Returns the notations declared in the XML document.
		 * @param document (CAL type: <code>Cal.Utilities.XmlBuilder.XmlDocument</code>)
		 * @return (CAL type: <code>[Cal.Utilities.XmlBuilder.XmlNotation]</code>) 
		 */
		public static final SourceModel.Expr documentNotations(SourceModel.Expr document) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.documentNotations), document});
		}

		/**
		 * Name binding for function: documentNotations.
		 * @see #documentNotations(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName documentNotations = 
			QualifiedName.make(CAL_XmlBuilder.MODULE_NAME, "documentNotations");

		/**
		 * Returns the root element of the XML document.
		 * @param document (CAL type: <code>Cal.Utilities.XmlBuilder.XmlDocument</code>)
		 * @return (CAL type: <code>Cal.Utilities.XmlBuilder.XmlNode</code>) 
		 */
		public static final SourceModel.Expr documentRootElement(SourceModel.Expr document) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.documentRootElement), document});
		}

		/**
		 * Name binding for function: documentRootElement.
		 * @see #documentRootElement(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName documentRootElement = 
			QualifiedName.make(
				CAL_XmlBuilder.MODULE_NAME, 
				"documentRootElement");

		/**
		 * Returns the first attribute with the given name
		 * An error is thrown if the node is not an XML element.
		 * @param xmlElement (CAL type: <code>Cal.Utilities.XmlBuilder.XmlNode</code>)
		 * @param attrName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Maybe Cal.Utilities.XmlBuilder.XmlAttribute</code>) 
		 */
		public static final SourceModel.Expr elementAttributeByName(SourceModel.Expr xmlElement, SourceModel.Expr attrName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.elementAttributeByName), xmlElement, attrName});
		}

		/**
		 * @see #elementAttributeByName(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param xmlElement
		 * @param attrName
		 * @return the SourceModel.Expr representing an application of elementAttributeByName
		 */
		public static final SourceModel.Expr elementAttributeByName(SourceModel.Expr xmlElement, java.lang.String attrName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.elementAttributeByName), xmlElement, SourceModel.Expr.makeStringValue(attrName)});
		}

		/**
		 * Name binding for function: elementAttributeByName.
		 * @see #elementAttributeByName(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName elementAttributeByName = 
			QualifiedName.make(
				CAL_XmlBuilder.MODULE_NAME, 
				"elementAttributeByName");

		/**
		 * Returns the deserialized value from the specified attribute for an XML element.
		 * An error is thrown if the attribute is not present.
		 * An error is thrown if the node is not an XML element.
		 * @param xmlElement (CAL type: <code>Cal.Utilities.XmlBuilder.XmlNode</code>)
		 * @param attrName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @return (CAL type: <code>Cal.Utilities.XmlBuilder.XmlAttributeInputable a => a</code>) 
		 */
		public static final SourceModel.Expr elementAttributeValue(SourceModel.Expr xmlElement, SourceModel.Expr attrName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.elementAttributeValue), xmlElement, attrName});
		}

		/**
		 * @see #elementAttributeValue(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param xmlElement
		 * @param attrName
		 * @return the SourceModel.Expr representing an application of elementAttributeValue
		 */
		public static final SourceModel.Expr elementAttributeValue(SourceModel.Expr xmlElement, java.lang.String attrName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.elementAttributeValue), xmlElement, SourceModel.Expr.makeStringValue(attrName)});
		}

		/**
		 * Name binding for function: elementAttributeValue.
		 * @see #elementAttributeValue(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName elementAttributeValue = 
			QualifiedName.make(
				CAL_XmlBuilder.MODULE_NAME, 
				"elementAttributeValue");

		/**
		 * Returns the deserialized value from the specified attribute for an XML element.
		 * If the attribute is not present, then <code>Cal.Core.Prelude.Nothing</code> will be returned.
		 * An error is thrown if the node is not an XML element.
		 * @param xmlElement (CAL type: <code>Cal.Utilities.XmlBuilder.XmlNode</code>)
		 * @param attrName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @return (CAL type: <code>Cal.Utilities.XmlBuilder.XmlAttributeInputable a => Cal.Core.Prelude.Maybe a</code>) 
		 */
		public static final SourceModel.Expr elementAttributeValueMaybe(SourceModel.Expr xmlElement, SourceModel.Expr attrName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.elementAttributeValueMaybe), xmlElement, attrName});
		}

		/**
		 * @see #elementAttributeValueMaybe(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param xmlElement
		 * @param attrName
		 * @return the SourceModel.Expr representing an application of elementAttributeValueMaybe
		 */
		public static final SourceModel.Expr elementAttributeValueMaybe(SourceModel.Expr xmlElement, java.lang.String attrName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.elementAttributeValueMaybe), xmlElement, SourceModel.Expr.makeStringValue(attrName)});
		}

		/**
		 * Name binding for function: elementAttributeValueMaybe.
		 * @see #elementAttributeValueMaybe(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName elementAttributeValueMaybe = 
			QualifiedName.make(
				CAL_XmlBuilder.MODULE_NAME, 
				"elementAttributeValueMaybe");

		/**
		 * Returns the attributes for an XML element.
		 * An error is thrown if the node is not an XML element.
		 * @param xmlElement (CAL type: <code>Cal.Utilities.XmlBuilder.XmlNode</code>)
		 * @return (CAL type: <code>[Cal.Utilities.XmlBuilder.XmlAttribute]</code>) 
		 */
		public static final SourceModel.Expr elementAttributes(SourceModel.Expr xmlElement) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.elementAttributes), xmlElement});
		}

		/**
		 * Name binding for function: elementAttributes.
		 * @see #elementAttributes(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName elementAttributes = 
			QualifiedName.make(CAL_XmlBuilder.MODULE_NAME, "elementAttributes");

		/**
		 * Returns the fist child elements with a specified name (if any) for an XML element.
		 * Any child nodes that are not elements will be ignored.
		 * An error is thrown if the node is not an XML element.
		 * @param xmlElement (CAL type: <code>Cal.Utilities.XmlBuilder.XmlNode</code>)
		 * @param elemName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Maybe Cal.Utilities.XmlBuilder.XmlNode</code>) 
		 */
		public static final SourceModel.Expr elementChildElementByName(SourceModel.Expr xmlElement, SourceModel.Expr elemName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.elementChildElementByName), xmlElement, elemName});
		}

		/**
		 * @see #elementChildElementByName(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param xmlElement
		 * @param elemName
		 * @return the SourceModel.Expr representing an application of elementChildElementByName
		 */
		public static final SourceModel.Expr elementChildElementByName(SourceModel.Expr xmlElement, java.lang.String elemName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.elementChildElementByName), xmlElement, SourceModel.Expr.makeStringValue(elemName)});
		}

		/**
		 * Name binding for function: elementChildElementByName.
		 * @see #elementChildElementByName(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName elementChildElementByName = 
			QualifiedName.make(
				CAL_XmlBuilder.MODULE_NAME, 
				"elementChildElementByName");

		/**
		 * Returns the child elements for an XML element.
		 * Any child nodes that are not elements will be ignored.
		 * An error is thrown if the node is not an XML element.
		 * @param xmlElement (CAL type: <code>Cal.Utilities.XmlBuilder.XmlNode</code>)
		 * @return (CAL type: <code>[Cal.Utilities.XmlBuilder.XmlNode]</code>) 
		 */
		public static final SourceModel.Expr elementChildElements(SourceModel.Expr xmlElement) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.elementChildElements), xmlElement});
		}

		/**
		 * Name binding for function: elementChildElements.
		 * @see #elementChildElements(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName elementChildElements = 
			QualifiedName.make(
				CAL_XmlBuilder.MODULE_NAME, 
				"elementChildElements");

		/**
		 * Returns the child elements with a specified name for an XML element.
		 * Any child nodes that are not elements will be ignored.
		 * An error is thrown if the node is not an XML element.
		 * @param xmlElement (CAL type: <code>Cal.Utilities.XmlBuilder.XmlNode</code>)
		 * @param elemName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @return (CAL type: <code>[Cal.Utilities.XmlBuilder.XmlNode]</code>) 
		 */
		public static final SourceModel.Expr elementChildElementsByName(SourceModel.Expr xmlElement, SourceModel.Expr elemName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.elementChildElementsByName), xmlElement, elemName});
		}

		/**
		 * @see #elementChildElementsByName(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param xmlElement
		 * @param elemName
		 * @return the SourceModel.Expr representing an application of elementChildElementsByName
		 */
		public static final SourceModel.Expr elementChildElementsByName(SourceModel.Expr xmlElement, java.lang.String elemName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.elementChildElementsByName), xmlElement, SourceModel.Expr.makeStringValue(elemName)});
		}

		/**
		 * Name binding for function: elementChildElementsByName.
		 * @see #elementChildElementsByName(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName elementChildElementsByName = 
			QualifiedName.make(
				CAL_XmlBuilder.MODULE_NAME, 
				"elementChildElementsByName");

		/**
		 * Returns the child text for an XML element.
		 * Any child nodes that are not text will be ignored.
		 * If there are no child text nodes, then an empty string will be returned.
		 * If there are multiple child text nodes, then their text will be concatenated.
		 * An error is thrown if the node is not an XML element.
		 * @param xmlElement (CAL type: <code>Cal.Utilities.XmlBuilder.XmlNode</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 */
		public static final SourceModel.Expr elementChildText(SourceModel.Expr xmlElement) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.elementChildText), xmlElement});
		}

		/**
		 * Name binding for function: elementChildText.
		 * @see #elementChildText(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName elementChildText = 
			QualifiedName.make(CAL_XmlBuilder.MODULE_NAME, "elementChildText");

		/**
		 * Returns the deserialized value from the specified child for an XML element.
		 * An error is thrown if the element is not present.
		 * An error is thrown if the node is not an XML element.
		 * @param xmlElement (CAL type: <code>Cal.Utilities.XmlBuilder.XmlNode</code>)
		 * @param attrName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @return (CAL type: <code>Cal.Utilities.XmlBuilder.XmlElementInputable a => a</code>) 
		 */
		public static final SourceModel.Expr elementChildValue(SourceModel.Expr xmlElement, SourceModel.Expr attrName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.elementChildValue), xmlElement, attrName});
		}

		/**
		 * @see #elementChildValue(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param xmlElement
		 * @param attrName
		 * @return the SourceModel.Expr representing an application of elementChildValue
		 */
		public static final SourceModel.Expr elementChildValue(SourceModel.Expr xmlElement, java.lang.String attrName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.elementChildValue), xmlElement, SourceModel.Expr.makeStringValue(attrName)});
		}

		/**
		 * Name binding for function: elementChildValue.
		 * @see #elementChildValue(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName elementChildValue = 
			QualifiedName.make(CAL_XmlBuilder.MODULE_NAME, "elementChildValue");

		/**
		 * Returns the deserialized value from the specified child for an XML element.
		 * If the element is not present, then <code>Cal.Core.Prelude.Nothing</code> will be returned.
		 * An error is thrown if the node is not an XML element.
		 * @param xmlElement (CAL type: <code>Cal.Utilities.XmlBuilder.XmlNode</code>)
		 * @param elementName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @return (CAL type: <code>Cal.Utilities.XmlBuilder.XmlElementInputable a => Cal.Core.Prelude.Maybe a</code>) 
		 */
		public static final SourceModel.Expr elementChildValueMaybe(SourceModel.Expr xmlElement, SourceModel.Expr elementName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.elementChildValueMaybe), xmlElement, elementName});
		}

		/**
		 * @see #elementChildValueMaybe(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param xmlElement
		 * @param elementName
		 * @return the SourceModel.Expr representing an application of elementChildValueMaybe
		 */
		public static final SourceModel.Expr elementChildValueMaybe(SourceModel.Expr xmlElement, java.lang.String elementName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.elementChildValueMaybe), xmlElement, SourceModel.Expr.makeStringValue(elementName)});
		}

		/**
		 * Name binding for function: elementChildValueMaybe.
		 * @see #elementChildValueMaybe(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName elementChildValueMaybe = 
			QualifiedName.make(
				CAL_XmlBuilder.MODULE_NAME, 
				"elementChildValueMaybe");

		/**
		 * Returns the child nodes (not necessarily elements) for an XML element.
		 * An error is thrown if the node is not an XML element.
		 * @param xmlElement (CAL type: <code>Cal.Utilities.XmlBuilder.XmlNode</code>)
		 * @return (CAL type: <code>[Cal.Utilities.XmlBuilder.XmlNode]</code>) 
		 */
		public static final SourceModel.Expr elementChildren(SourceModel.Expr xmlElement) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.elementChildren), xmlElement});
		}

		/**
		 * Name binding for function: elementChildren.
		 * @see #elementChildren(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName elementChildren = 
			QualifiedName.make(CAL_XmlBuilder.MODULE_NAME, "elementChildren");

		/**
		 * Returns the first child element for an XML element.
		 * Any child nodes that are not elements will be ignored.
		 * An error is thrown if the node is not an XML element or there is no child element.
		 * @param xmlElement (CAL type: <code>Cal.Utilities.XmlBuilder.XmlNode</code>)
		 * @return (CAL type: <code>Cal.Utilities.XmlBuilder.XmlNode</code>) 
		 */
		public static final SourceModel.Expr elementFirstChildElement(SourceModel.Expr xmlElement) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.elementFirstChildElement), xmlElement});
		}

		/**
		 * Name binding for function: elementFirstChildElement.
		 * @see #elementFirstChildElement(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName elementFirstChildElement = 
			QualifiedName.make(
				CAL_XmlBuilder.MODULE_NAME, 
				"elementFirstChildElement");

		/**
		 * Returns the first child element with a specified name for an XML element.
		 * An error is thrown if the node is not an XML element or there is no first child.
		 * @param xmlElement (CAL type: <code>Cal.Utilities.XmlBuilder.XmlNode</code>)
		 * @param elemName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @return (CAL type: <code>Cal.Utilities.XmlBuilder.XmlNode</code>) 
		 */
		public static final SourceModel.Expr elementFirstChildElementByName(SourceModel.Expr xmlElement, SourceModel.Expr elemName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.elementFirstChildElementByName), xmlElement, elemName});
		}

		/**
		 * @see #elementFirstChildElementByName(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param xmlElement
		 * @param elemName
		 * @return the SourceModel.Expr representing an application of elementFirstChildElementByName
		 */
		public static final SourceModel.Expr elementFirstChildElementByName(SourceModel.Expr xmlElement, java.lang.String elemName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.elementFirstChildElementByName), xmlElement, SourceModel.Expr.makeStringValue(elemName)});
		}

		/**
		 * Name binding for function: elementFirstChildElementByName.
		 * @see #elementFirstChildElementByName(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName elementFirstChildElementByName = 
			QualifiedName.make(
				CAL_XmlBuilder.MODULE_NAME, 
				"elementFirstChildElementByName");

		/**
		 * This is a helper function for the occasional time when during serialization something that's normally serialized as an 
		 * attribute needs to be stored as an element.
		 * @param elementName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @param attributeName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @param value (CAL type: <code>Cal.Utilities.XmlBuilder.XmlAttributeOutputable a => a</code>)
		 * @return (CAL type: <code>Cal.Utilities.XmlBuilder.XmlNode</code>) 
		 */
		public static final SourceModel.Expr elementForXmlAttributeOutputable(SourceModel.Expr elementName, SourceModel.Expr attributeName, SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.elementForXmlAttributeOutputable), elementName, attributeName, value});
		}

		/**
		 * @see #elementForXmlAttributeOutputable(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param elementName
		 * @param attributeName
		 * @param value
		 * @return the SourceModel.Expr representing an application of elementForXmlAttributeOutputable
		 */
		public static final SourceModel.Expr elementForXmlAttributeOutputable(java.lang.String elementName, java.lang.String attributeName, SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.elementForXmlAttributeOutputable), SourceModel.Expr.makeStringValue(elementName), SourceModel.Expr.makeStringValue(attributeName), value});
		}

		/**
		 * Name binding for function: elementForXmlAttributeOutputable.
		 * @see #elementForXmlAttributeOutputable(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName elementForXmlAttributeOutputable = 
			QualifiedName.make(
				CAL_XmlBuilder.MODULE_NAME, 
				"elementForXmlAttributeOutputable");

		/**
		 * Returns the local name of an XML element.
		 * This name will not include any namespace prefix.
		 * An error is thrown if the node is not an XML element.
		 * @param xmlElement (CAL type: <code>Cal.Utilities.XmlBuilder.XmlNode</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 */
		public static final SourceModel.Expr elementLocalName(SourceModel.Expr xmlElement) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.elementLocalName), xmlElement});
		}

		/**
		 * Name binding for function: elementLocalName.
		 * @see #elementLocalName(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName elementLocalName = 
			QualifiedName.make(CAL_XmlBuilder.MODULE_NAME, "elementLocalName");

		/**
		 * Returns the namespace (if any) for an XML Element.
		 * An error is thrown if the node is not an XML element.
		 * @param xmlElement (CAL type: <code>Cal.Utilities.XmlBuilder.XmlNode</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Maybe Cal.Utilities.XmlBuilder.XmlNamespace</code>) 
		 */
		public static final SourceModel.Expr elementNamespace(SourceModel.Expr xmlElement) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.elementNamespace), xmlElement});
		}

		/**
		 * Name binding for function: elementNamespace.
		 * @see #elementNamespace(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName elementNamespace = 
			QualifiedName.make(CAL_XmlBuilder.MODULE_NAME, "elementNamespace");

		/**
		 * Returns the qualified name of an XML element.
		 * This will include the namespace prefix, if any.
		 * An error is thrown if the node is not an XML element.
		 * @param xmlElement (CAL type: <code>Cal.Utilities.XmlBuilder.XmlNode</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 */
		public static final SourceModel.Expr elementQualifiedName(SourceModel.Expr xmlElement) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.elementQualifiedName), xmlElement});
		}

		/**
		 * Name binding for function: elementQualifiedName.
		 * @see #elementQualifiedName(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName elementQualifiedName = 
			QualifiedName.make(
				CAL_XmlBuilder.MODULE_NAME, 
				"elementQualifiedName");

		/**
		 * Replaces the chars <code>'&amp;'</code>, <code>'&lt;'</code>, and <code>'&gt;'</code> with the appropriate XML escape sequences.
		 * @param txt (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 */
		public static final SourceModel.Expr escapeXmlChars(SourceModel.Expr txt) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.escapeXmlChars), txt});
		}

		/**
		 * @see #escapeXmlChars(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param txt
		 * @return the SourceModel.Expr representing an application of escapeXmlChars
		 */
		public static final SourceModel.Expr escapeXmlChars(java.lang.String txt) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.escapeXmlChars), SourceModel.Expr.makeStringValue(txt)});
		}

		/**
		 * Name binding for function: escapeXmlChars.
		 * @see #escapeXmlChars(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName escapeXmlChars = 
			QualifiedName.make(CAL_XmlBuilder.MODULE_NAME, "escapeXmlChars");

		/**
		 * Helper binding method for function: fromXml. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of fromXml
		 */
		public static final SourceModel.Expr fromXml(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fromXml), arg_1});
		}

		/**
		 * Name binding for function: fromXml.
		 * @see #fromXml(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fromXml = 
			QualifiedName.make(CAL_XmlBuilder.MODULE_NAME, "fromXml");

		/**
		 * Helper binding method for function: fromXmlAttribute. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of fromXmlAttribute
		 */
		public static final SourceModel.Expr fromXmlAttribute(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fromXmlAttribute), arg_1});
		}

		/**
		 * Name binding for function: fromXmlAttribute.
		 * @see #fromXmlAttribute(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fromXmlAttribute = 
			QualifiedName.make(CAL_XmlBuilder.MODULE_NAME, "fromXmlAttribute");

		/**
		 * Deserializes a value from the root XML element of the specified document.
		 * @param document (CAL type: <code>Cal.Utilities.XmlBuilder.XmlDocument</code>)
		 * @return (CAL type: <code>Cal.Utilities.XmlBuilder.XmlElementInputable a => a</code>) 
		 */
		public static final SourceModel.Expr fromXmlDocument(SourceModel.Expr document) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fromXmlDocument), document});
		}

		/**
		 * Name binding for function: fromXmlDocument.
		 * @see #fromXmlDocument(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fromXmlDocument = 
			QualifiedName.make(CAL_XmlBuilder.MODULE_NAME, "fromXmlDocument");

		/**
		 * Helper binding method for function: fromXmlElement. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of fromXmlElement
		 */
		public static final SourceModel.Expr fromXmlElement(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fromXmlElement), arg_1});
		}

		/**
		 * Name binding for function: fromXmlElement.
		 * @see #fromXmlElement(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fromXmlElement = 
			QualifiedName.make(CAL_XmlBuilder.MODULE_NAME, "fromXmlElement");

		/**
		 * Helper for implementing fromXml from XmlAttributeInputable types
		 * @param either (CAL type: <code>Cal.Core.Prelude.Either Cal.Utilities.XmlBuilder.XmlAttribute Cal.Utilities.XmlBuilder.XmlNode</code>)
		 * @return (CAL type: <code>Cal.Utilities.XmlBuilder.XmlAttributeInputable a => a</code>) 
		 */
		public static final SourceModel.Expr inputFromXmlAttributeInputable(SourceModel.Expr either) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.inputFromXmlAttributeInputable), either});
		}

		/**
		 * Name binding for function: inputFromXmlAttributeInputable.
		 * @see #inputFromXmlAttributeInputable(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName inputFromXmlAttributeInputable = 
			QualifiedName.make(
				CAL_XmlBuilder.MODULE_NAME, 
				"inputFromXmlAttributeInputable");

		/**
		 * Helper for implementing fromXml from XmlAttributeOutputable types
		 * @param either (CAL type: <code>Cal.Core.Prelude.Either Cal.Utilities.XmlBuilder.XmlAttribute Cal.Utilities.XmlBuilder.XmlNode</code>)
		 * @return (CAL type: <code>Cal.Utilities.XmlBuilder.XmlElementInputable a => a</code>) 
		 */
		public static final SourceModel.Expr inputFromXmlElementInputable(SourceModel.Expr either) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.inputFromXmlElementInputable), either});
		}

		/**
		 * Name binding for function: inputFromXmlElementInputable.
		 * @see #inputFromXmlElementInputable(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName inputFromXmlElementInputable = 
			QualifiedName.make(
				CAL_XmlBuilder.MODULE_NAME, 
				"inputFromXmlElementInputable");

		/**
		 * Returns whether the XML node is TEXT.
		 * @param node (CAL type: <code>Cal.Utilities.XmlBuilder.XmlNode</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 */
		public static final SourceModel.Expr isTextNode(SourceModel.Expr node) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isTextNode), node});
		}

		/**
		 * Name binding for function: isTextNode.
		 * @see #isTextNode(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isTextNode = 
			QualifiedName.make(CAL_XmlBuilder.MODULE_NAME, "isTextNode");

		/**
		 * Returns whether the specified character is well-formed as the first character in an XML name.
		 * @param firstChar (CAL type: <code>Cal.Core.Prelude.Char</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 */
		public static final SourceModel.Expr isWellFormedFirstXmlNameChar(SourceModel.Expr firstChar) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isWellFormedFirstXmlNameChar), firstChar});
		}

		/**
		 * @see #isWellFormedFirstXmlNameChar(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param firstChar
		 * @return the SourceModel.Expr representing an application of isWellFormedFirstXmlNameChar
		 */
		public static final SourceModel.Expr isWellFormedFirstXmlNameChar(char firstChar) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isWellFormedFirstXmlNameChar), SourceModel.Expr.makeCharValue(firstChar)});
		}

		/**
		 * Name binding for function: isWellFormedFirstXmlNameChar.
		 * @see #isWellFormedFirstXmlNameChar(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isWellFormedFirstXmlNameChar = 
			QualifiedName.make(
				CAL_XmlBuilder.MODULE_NAME, 
				"isWellFormedFirstXmlNameChar");

		/**
		 * Returns whether the specified character is well-formed as a character in an XML name (not the first character).
		 * @param char_ (CAL type: <code>Cal.Core.Prelude.Char</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 */
		public static final SourceModel.Expr isWellFormedXmlNameChar(SourceModel.Expr char_) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isWellFormedXmlNameChar), char_});
		}

		/**
		 * @see #isWellFormedXmlNameChar(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param char_
		 * @return the SourceModel.Expr representing an application of isWellFormedXmlNameChar
		 */
		public static final SourceModel.Expr isWellFormedXmlNameChar(char char_) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isWellFormedXmlNameChar), SourceModel.Expr.makeCharValue(char_)});
		}

		/**
		 * Name binding for function: isWellFormedXmlNameChar.
		 * @see #isWellFormedXmlNameChar(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isWellFormedXmlNameChar = 
			QualifiedName.make(
				CAL_XmlBuilder.MODULE_NAME, 
				"isWellFormedXmlNameChar");

		/**
		 * Creates a new XML attribute with the specified name and value.
		 * @param name (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @param txt (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @return (CAL type: <code>Cal.Utilities.XmlBuilder.XmlAttribute</code>) 
		 */
		public static final SourceModel.Expr makeXmlAttribute(SourceModel.Expr name, SourceModel.Expr txt) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeXmlAttribute), name, txt});
		}

		/**
		 * @see #makeXmlAttribute(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param name
		 * @param txt
		 * @return the SourceModel.Expr representing an application of makeXmlAttribute
		 */
		public static final SourceModel.Expr makeXmlAttribute(java.lang.String name, java.lang.String txt) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeXmlAttribute), SourceModel.Expr.makeStringValue(name), SourceModel.Expr.makeStringValue(txt)});
		}

		/**
		 * Name binding for function: makeXmlAttribute.
		 * @see #makeXmlAttribute(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeXmlAttribute = 
			QualifiedName.make(CAL_XmlBuilder.MODULE_NAME, "makeXmlAttribute");

		/**
		 * Creates a new XML attribute with the specified name, value, and namespace.
		 * The namespace provided must have a non-empty prefix.
		 * @param namespace (CAL type: <code>Cal.Utilities.XmlBuilder.XmlNamespace</code>)
		 * @param name (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @param txt (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @return (CAL type: <code>Cal.Utilities.XmlBuilder.XmlAttribute</code>) 
		 */
		public static final SourceModel.Expr makeXmlAttributeWithNamespace(SourceModel.Expr namespace, SourceModel.Expr name, SourceModel.Expr txt) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeXmlAttributeWithNamespace), namespace, name, txt});
		}

		/**
		 * @see #makeXmlAttributeWithNamespace(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param namespace
		 * @param name
		 * @param txt
		 * @return the SourceModel.Expr representing an application of makeXmlAttributeWithNamespace
		 */
		public static final SourceModel.Expr makeXmlAttributeWithNamespace(SourceModel.Expr namespace, java.lang.String name, java.lang.String txt) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeXmlAttributeWithNamespace), namespace, SourceModel.Expr.makeStringValue(name), SourceModel.Expr.makeStringValue(txt)});
		}

		/**
		 * Name binding for function: makeXmlAttributeWithNamespace.
		 * @see #makeXmlAttributeWithNamespace(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeXmlAttributeWithNamespace = 
			QualifiedName.make(
				CAL_XmlBuilder.MODULE_NAME, 
				"makeXmlAttributeWithNamespace");

		/**
		 * Creates a new XML CDATA section with the specific text context.  Data in the CDATA
		 * section will not be validated by the parser.
		 * @param txt (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @return (CAL type: <code>Cal.Utilities.XmlBuilder.XmlNode</code>) 
		 */
		public static final SourceModel.Expr makeXmlCDataNode(SourceModel.Expr txt) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeXmlCDataNode), txt});
		}

		/**
		 * @see #makeXmlCDataNode(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param txt
		 * @return the SourceModel.Expr representing an application of makeXmlCDataNode
		 */
		public static final SourceModel.Expr makeXmlCDataNode(java.lang.String txt) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeXmlCDataNode), SourceModel.Expr.makeStringValue(txt)});
		}

		/**
		 * Name binding for function: makeXmlCDataNode.
		 * @see #makeXmlCDataNode(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeXmlCDataNode = 
			QualifiedName.make(CAL_XmlBuilder.MODULE_NAME, "makeXmlCDataNode");

		/**
		 * Creates a new XML element with the specified text content wrapped in
		 * in a CDATA section.
		 * A namespace can optionally be specified for the resulting element.
		 * @param namespace (CAL type: <code>Cal.Core.Prelude.Maybe Cal.Utilities.XmlBuilder.XmlNamespace</code>)
		 * @param name (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @param txt (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @return (CAL type: <code>Cal.Utilities.XmlBuilder.XmlNode</code>) 
		 */
		public static final SourceModel.Expr makeXmlCDataSectionElement(SourceModel.Expr namespace, SourceModel.Expr name, SourceModel.Expr txt) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeXmlCDataSectionElement), namespace, name, txt});
		}

		/**
		 * @see #makeXmlCDataSectionElement(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param namespace
		 * @param name
		 * @param txt
		 * @return the SourceModel.Expr representing an application of makeXmlCDataSectionElement
		 */
		public static final SourceModel.Expr makeXmlCDataSectionElement(SourceModel.Expr namespace, java.lang.String name, java.lang.String txt) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeXmlCDataSectionElement), namespace, SourceModel.Expr.makeStringValue(name), SourceModel.Expr.makeStringValue(txt)});
		}

		/**
		 * Name binding for function: makeXmlCDataSectionElement.
		 * @see #makeXmlCDataSectionElement(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeXmlCDataSectionElement = 
			QualifiedName.make(
				CAL_XmlBuilder.MODULE_NAME, 
				"makeXmlCDataSectionElement");

		/**
		 * Creates a new XML comment node with the specified comment text.
		 * @param txt (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @return (CAL type: <code>Cal.Utilities.XmlBuilder.XmlNode</code>) 
		 */
		public static final SourceModel.Expr makeXmlComment(SourceModel.Expr txt) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeXmlComment), txt});
		}

		/**
		 * @see #makeXmlComment(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param txt
		 * @return the SourceModel.Expr representing an application of makeXmlComment
		 */
		public static final SourceModel.Expr makeXmlComment(java.lang.String txt) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeXmlComment), SourceModel.Expr.makeStringValue(txt)});
		}

		/**
		 * Name binding for function: makeXmlComment.
		 * @see #makeXmlComment(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeXmlComment = 
			QualifiedName.make(CAL_XmlBuilder.MODULE_NAME, "makeXmlComment");

		/**
		 * Creates an XML document with the specified top-level nodes (one of which must be an element).
		 * @param topLevelNodes (CAL type: <code>[Cal.Utilities.XmlBuilder.XmlNode]</code>)
		 * @return (CAL type: <code>Cal.Utilities.XmlBuilder.XmlDocument</code>) 
		 */
		public static final SourceModel.Expr makeXmlDocument(SourceModel.Expr topLevelNodes) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeXmlDocument), topLevelNodes});
		}

		/**
		 * Name binding for function: makeXmlDocument.
		 * @see #makeXmlDocument(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeXmlDocument = 
			QualifiedName.make(CAL_XmlBuilder.MODULE_NAME, "makeXmlDocument");

		/**
		 * Creates a new XML element.
		 * This will check the inputs for well-formedness.
		 * @param namespace (CAL type: <code>Cal.Core.Prelude.Maybe Cal.Utilities.XmlBuilder.XmlNamespace</code>)
		 * @param name (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @param attributes (CAL type: <code>[Cal.Utilities.XmlBuilder.XmlAttribute]</code>)
		 * @param childElements (CAL type: <code>[Cal.Utilities.XmlBuilder.XmlNode]</code>)
		 * @return (CAL type: <code>Cal.Utilities.XmlBuilder.XmlNode</code>) 
		 */
		public static final SourceModel.Expr makeXmlElement(SourceModel.Expr namespace, SourceModel.Expr name, SourceModel.Expr attributes, SourceModel.Expr childElements) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeXmlElement), namespace, name, attributes, childElements});
		}

		/**
		 * @see #makeXmlElement(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param namespace
		 * @param name
		 * @param attributes
		 * @param childElements
		 * @return the SourceModel.Expr representing an application of makeXmlElement
		 */
		public static final SourceModel.Expr makeXmlElement(SourceModel.Expr namespace, java.lang.String name, SourceModel.Expr attributes, SourceModel.Expr childElements) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeXmlElement), namespace, SourceModel.Expr.makeStringValue(name), attributes, childElements});
		}

		/**
		 * Name binding for function: makeXmlElement.
		 * @see #makeXmlElement(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeXmlElement = 
			QualifiedName.make(CAL_XmlBuilder.MODULE_NAME, "makeXmlElement");

		/**
		 * Construct an XML namespace with an optional prefix.
		 * An empty string can be provided to indicate that no prefix should be used for the namespace.
		 * @param prefix (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @param uri (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @return (CAL type: <code>Cal.Utilities.XmlBuilder.XmlNamespace</code>) 
		 */
		public static final SourceModel.Expr makeXmlNamespace(SourceModel.Expr prefix, SourceModel.Expr uri) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeXmlNamespace), prefix, uri});
		}

		/**
		 * @see #makeXmlNamespace(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param prefix
		 * @param uri
		 * @return the SourceModel.Expr representing an application of makeXmlNamespace
		 */
		public static final SourceModel.Expr makeXmlNamespace(java.lang.String prefix, java.lang.String uri) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeXmlNamespace), SourceModel.Expr.makeStringValue(prefix), SourceModel.Expr.makeStringValue(uri)});
		}

		/**
		 * Name binding for function: makeXmlNamespace.
		 * @see #makeXmlNamespace(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeXmlNamespace = 
			QualifiedName.make(CAL_XmlBuilder.MODULE_NAME, "makeXmlNamespace");

		/**
		 * Creates a new XML notation with the specified name, system ID, and public ID.
		 * @param name (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @param publicId (CAL type: <code>Cal.Core.Prelude.Maybe Cal.Core.Prelude.String</code>)
		 * @param systemId (CAL type: <code>Cal.Core.Prelude.Maybe Cal.Core.Prelude.String</code>)
		 * @return (CAL type: <code>Cal.Utilities.XmlBuilder.XmlNotation</code>) 
		 */
		public static final SourceModel.Expr makeXmlNotation(SourceModel.Expr name, SourceModel.Expr publicId, SourceModel.Expr systemId) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeXmlNotation), name, publicId, systemId});
		}

		/**
		 * @see #makeXmlNotation(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param name
		 * @param publicId
		 * @param systemId
		 * @return the SourceModel.Expr representing an application of makeXmlNotation
		 */
		public static final SourceModel.Expr makeXmlNotation(java.lang.String name, SourceModel.Expr publicId, SourceModel.Expr systemId) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeXmlNotation), SourceModel.Expr.makeStringValue(name), publicId, systemId});
		}

		/**
		 * Name binding for function: makeXmlNotation.
		 * @see #makeXmlNotation(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeXmlNotation = 
			QualifiedName.make(CAL_XmlBuilder.MODULE_NAME, "makeXmlNotation");

		/**
		 * Creates a new XML processing instruction node.
		 * The target must be a well-formed XML name and cannot have the text 'xml' (with any case).
		 * @param target (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @param content (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @return (CAL type: <code>Cal.Utilities.XmlBuilder.XmlNode</code>) 
		 */
		public static final SourceModel.Expr makeXmlProcessingInstruction(SourceModel.Expr target, SourceModel.Expr content) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeXmlProcessingInstruction), target, content});
		}

		/**
		 * @see #makeXmlProcessingInstruction(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param target
		 * @param content
		 * @return the SourceModel.Expr representing an application of makeXmlProcessingInstruction
		 */
		public static final SourceModel.Expr makeXmlProcessingInstruction(java.lang.String target, java.lang.String content) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeXmlProcessingInstruction), SourceModel.Expr.makeStringValue(target), SourceModel.Expr.makeStringValue(content)});
		}

		/**
		 * Name binding for function: makeXmlProcessingInstruction.
		 * @see #makeXmlProcessingInstruction(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeXmlProcessingInstruction = 
			QualifiedName.make(
				CAL_XmlBuilder.MODULE_NAME, 
				"makeXmlProcessingInstruction");

		/**
		 * Creates a new XML stylesheet node with the specified url and type.
		 * This is a special case of a processing instruction.
		 * @param href (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @param type (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @return (CAL type: <code>Cal.Utilities.XmlBuilder.XmlNode</code>) 
		 */
		public static final SourceModel.Expr makeXmlStyleSheet(SourceModel.Expr href, SourceModel.Expr type) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeXmlStyleSheet), href, type});
		}

		/**
		 * @see #makeXmlStyleSheet(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param href
		 * @param type
		 * @return the SourceModel.Expr representing an application of makeXmlStyleSheet
		 */
		public static final SourceModel.Expr makeXmlStyleSheet(java.lang.String href, java.lang.String type) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeXmlStyleSheet), SourceModel.Expr.makeStringValue(href), SourceModel.Expr.makeStringValue(type)});
		}

		/**
		 * Name binding for function: makeXmlStyleSheet.
		 * @see #makeXmlStyleSheet(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeXmlStyleSheet = 
			QualifiedName.make(CAL_XmlBuilder.MODULE_NAME, "makeXmlStyleSheet");

		/**
		 * Creates a new XML element with the specified text content.
		 * A namespace can optionally be specified for the resulting element.
		 * @param namespace (CAL type: <code>Cal.Core.Prelude.Maybe Cal.Utilities.XmlBuilder.XmlNamespace</code>)
		 * @param name (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @param txt (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @return (CAL type: <code>Cal.Utilities.XmlBuilder.XmlNode</code>) 
		 */
		public static final SourceModel.Expr makeXmlTextElement(SourceModel.Expr namespace, SourceModel.Expr name, SourceModel.Expr txt) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeXmlTextElement), namespace, name, txt});
		}

		/**
		 * @see #makeXmlTextElement(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param namespace
		 * @param name
		 * @param txt
		 * @return the SourceModel.Expr representing an application of makeXmlTextElement
		 */
		public static final SourceModel.Expr makeXmlTextElement(SourceModel.Expr namespace, java.lang.String name, java.lang.String txt) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeXmlTextElement), namespace, SourceModel.Expr.makeStringValue(name), SourceModel.Expr.makeStringValue(txt)});
		}

		/**
		 * Name binding for function: makeXmlTextElement.
		 * @see #makeXmlTextElement(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeXmlTextElement = 
			QualifiedName.make(CAL_XmlBuilder.MODULE_NAME, "makeXmlTextElement");

		/**
		 * Creates a new XML TEXT node with the specified text content.
		 * @param txt (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @return (CAL type: <code>Cal.Utilities.XmlBuilder.XmlNode</code>) 
		 */
		public static final SourceModel.Expr makeXmlTextNode(SourceModel.Expr txt) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeXmlTextNode), txt});
		}

		/**
		 * @see #makeXmlTextNode(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param txt
		 * @return the SourceModel.Expr representing an application of makeXmlTextNode
		 */
		public static final SourceModel.Expr makeXmlTextNode(java.lang.String txt) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeXmlTextNode), SourceModel.Expr.makeStringValue(txt)});
		}

		/**
		 * Name binding for function: makeXmlTextNode.
		 * @see #makeXmlTextNode(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeXmlTextNode = 
			QualifiedName.make(CAL_XmlBuilder.MODULE_NAME, "makeXmlTextNode");

		/**
		 * Returns the prefix associated with a namespace.
		 * An empty string means that the namespace has no prefix associated with it.
		 * @param namespace (CAL type: <code>Cal.Utilities.XmlBuilder.XmlNamespace</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 */
		public static final SourceModel.Expr namespacePrefix(SourceModel.Expr namespace) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.namespacePrefix), namespace});
		}

		/**
		 * Name binding for function: namespacePrefix.
		 * @see #namespacePrefix(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName namespacePrefix = 
			QualifiedName.make(CAL_XmlBuilder.MODULE_NAME, "namespacePrefix");

		/**
		 * Returns the URI for a namespace.
		 * @param namespace (CAL type: <code>Cal.Utilities.XmlBuilder.XmlNamespace</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 */
		public static final SourceModel.Expr namespaceURI(SourceModel.Expr namespace) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.namespaceURI), namespace});
		}

		/**
		 * Name binding for function: namespaceURI.
		 * @see #namespaceURI(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName namespaceURI = 
			QualifiedName.make(CAL_XmlBuilder.MODULE_NAME, "namespaceURI");

		/**
		 * Returns the name of an XML notation.
		 * @param notation (CAL type: <code>Cal.Utilities.XmlBuilder.XmlNotation</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 */
		public static final SourceModel.Expr notationName(SourceModel.Expr notation) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.notationName), notation});
		}

		/**
		 * Name binding for function: notationName.
		 * @see #notationName(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName notationName = 
			QualifiedName.make(CAL_XmlBuilder.MODULE_NAME, "notationName");

		/**
		 * Returns the public ID of an XML notation.
		 * @param notation (CAL type: <code>Cal.Utilities.XmlBuilder.XmlNotation</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Maybe Cal.Core.Prelude.String</code>) 
		 */
		public static final SourceModel.Expr notationPublicId(SourceModel.Expr notation) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.notationPublicId), notation});
		}

		/**
		 * Name binding for function: notationPublicId.
		 * @see #notationPublicId(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName notationPublicId = 
			QualifiedName.make(CAL_XmlBuilder.MODULE_NAME, "notationPublicId");

		/**
		 * Returns the system ID of an XML notation.
		 * @param notation (CAL type: <code>Cal.Utilities.XmlBuilder.XmlNotation</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Maybe Cal.Core.Prelude.String</code>) 
		 */
		public static final SourceModel.Expr notationSystemId(SourceModel.Expr notation) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.notationSystemId), notation});
		}

		/**
		 * Name binding for function: notationSystemId.
		 * @see #notationSystemId(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName notationSystemId = 
			QualifiedName.make(CAL_XmlBuilder.MODULE_NAME, "notationSystemId");

		/**
		 * Helper for implementing toXml from XmlAttributeOutputable types
		 * @param name (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @param value (CAL type: <code>Cal.Utilities.XmlBuilder.XmlAttributeOutputable a => a</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Either Cal.Utilities.XmlBuilder.XmlAttribute Cal.Utilities.XmlBuilder.XmlNode</code>) 
		 */
		public static final SourceModel.Expr outputFromXmlAttributeOutputable(SourceModel.Expr name, SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.outputFromXmlAttributeOutputable), name, value});
		}

		/**
		 * @see #outputFromXmlAttributeOutputable(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param name
		 * @param value
		 * @return the SourceModel.Expr representing an application of outputFromXmlAttributeOutputable
		 */
		public static final SourceModel.Expr outputFromXmlAttributeOutputable(java.lang.String name, SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.outputFromXmlAttributeOutputable), SourceModel.Expr.makeStringValue(name), value});
		}

		/**
		 * Name binding for function: outputFromXmlAttributeOutputable.
		 * @see #outputFromXmlAttributeOutputable(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName outputFromXmlAttributeOutputable = 
			QualifiedName.make(
				CAL_XmlBuilder.MODULE_NAME, 
				"outputFromXmlAttributeOutputable");

		/**
		 * Helper for implementing toXml from XmlElementOutputable types
		 * @param name (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @param value (CAL type: <code>Cal.Utilities.XmlBuilder.XmlElementOutputable a => a</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Either Cal.Utilities.XmlBuilder.XmlAttribute Cal.Utilities.XmlBuilder.XmlNode</code>) 
		 */
		public static final SourceModel.Expr outputFromXmlElementOutputable(SourceModel.Expr name, SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.outputFromXmlElementOutputable), name, value});
		}

		/**
		 * @see #outputFromXmlElementOutputable(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param name
		 * @param value
		 * @return the SourceModel.Expr representing an application of outputFromXmlElementOutputable
		 */
		public static final SourceModel.Expr outputFromXmlElementOutputable(java.lang.String name, SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.outputFromXmlElementOutputable), SourceModel.Expr.makeStringValue(name), value});
		}

		/**
		 * Name binding for function: outputFromXmlElementOutputable.
		 * @see #outputFromXmlElementOutputable(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName outputFromXmlElementOutputable = 
			QualifiedName.make(
				CAL_XmlBuilder.MODULE_NAME, 
				"outputFromXmlElementOutputable");

		/**
		 * Sets the notations declared in an XML document.
		 * @param notations (CAL type: <code>[Cal.Utilities.XmlBuilder.XmlNotation]</code>)
		 * @param document (CAL type: <code>Cal.Utilities.XmlBuilder.XmlDocument</code>)
		 * @return (CAL type: <code>Cal.Utilities.XmlBuilder.XmlDocument</code>) 
		 */
		public static final SourceModel.Expr setDocumentNotations(SourceModel.Expr notations, SourceModel.Expr document) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.setDocumentNotations), notations, document});
		}

		/**
		 * Name binding for function: setDocumentNotations.
		 * @see #setDocumentNotations(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName setDocumentNotations = 
			QualifiedName.make(
				CAL_XmlBuilder.MODULE_NAME, 
				"setDocumentNotations");

		/**
		 * Returns the text of an XML text node.
		 * An error is thrown if the node is not an XML text node.
		 * @param textNode (CAL type: <code>Cal.Utilities.XmlBuilder.XmlNode</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 */
		public static final SourceModel.Expr textNodeText(SourceModel.Expr textNode) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.textNodeText), textNode});
		}

		/**
		 * Name binding for function: textNodeText.
		 * @see #textNodeText(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName textNodeText = 
			QualifiedName.make(CAL_XmlBuilder.MODULE_NAME, "textNodeText");

		/**
		 * Helper binding method for function: toXml. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of toXml
		 */
		public static final SourceModel.Expr toXml(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.toXml), arg_1, arg_2});
		}

		/**
		 * @see #toXml(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of toXml
		 */
		public static final SourceModel.Expr toXml(java.lang.String arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.toXml), SourceModel.Expr.makeStringValue(arg_1), arg_2});
		}

		/**
		 * Name binding for function: toXml.
		 * @see #toXml(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName toXml = 
			QualifiedName.make(CAL_XmlBuilder.MODULE_NAME, "toXml");

		/**
		 * Helper binding method for function: toXmlAttribute. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of toXmlAttribute
		 */
		public static final SourceModel.Expr toXmlAttribute(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.toXmlAttribute), arg_1, arg_2});
		}

		/**
		 * @see #toXmlAttribute(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of toXmlAttribute
		 */
		public static final SourceModel.Expr toXmlAttribute(java.lang.String arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.toXmlAttribute), SourceModel.Expr.makeStringValue(arg_1), arg_2});
		}

		/**
		 * Name binding for function: toXmlAttribute.
		 * @see #toXmlAttribute(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName toXmlAttribute = 
			QualifiedName.make(CAL_XmlBuilder.MODULE_NAME, "toXmlAttribute");

		/**
		 * Helper binding method for function: toXmlElement. 
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModule.expr representing an application of toXmlElement
		 */
		public static final SourceModel.Expr toXmlElement(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.toXmlElement), arg_1, arg_2});
		}

		/**
		 * @see #toXmlElement(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @return the SourceModel.Expr representing an application of toXmlElement
		 */
		public static final SourceModel.Expr toXmlElement(java.lang.String arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.toXmlElement), SourceModel.Expr.makeStringValue(arg_1), arg_2});
		}

		/**
		 * Name binding for function: toXmlElement.
		 * @see #toXmlElement(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName toXmlElement = 
			QualifiedName.make(CAL_XmlBuilder.MODULE_NAME, "toXmlElement");

		/**
		 * Generates a string representation of the XML document.
		 * The output will be indented and <code>'&lt;'</code> and <code>'&gt;'</code> chars will be replaced by the
		 * appropriate escape sequences.
		 * @param document (CAL type: <code>Cal.Utilities.XmlBuilder.XmlDocument</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 */
		public static final SourceModel.Expr xmlDocumentToString(SourceModel.Expr document) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.xmlDocumentToString), document});
		}

		/**
		 * Name binding for function: xmlDocumentToString.
		 * @see #xmlDocumentToString(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName xmlDocumentToString = 
			QualifiedName.make(
				CAL_XmlBuilder.MODULE_NAME, 
				"xmlDocumentToString");

		/**
		 * Generates a string representation of an XML document with the specified root node.
		 * @param rootElem (CAL type: <code>Cal.Utilities.XmlBuilder.XmlNode</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 */
		public static final SourceModel.Expr xmlElementToString(SourceModel.Expr rootElem) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.xmlElementToString), rootElem});
		}

		/**
		 * Name binding for function: xmlElementToString.
		 * @see #xmlElementToString(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName xmlElementToString = 
			QualifiedName.make(CAL_XmlBuilder.MODULE_NAME, "xmlElementToString");

		/**
		 * Generates a string representation of the XML node (element, comment, etc...).
		 * The output will be indented and <code>'&lt;'</code> and <code>'&gt;'</code> chars will be replaced by the
		 * appropriate escape sequences.
		 * @param arg_1 (CAL type: <code>Cal.Utilities.XmlBuilder.XmlNode</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 */
		public static final SourceModel.Expr xmlNodeToString(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.xmlNodeToString), arg_1});
		}

		/**
		 * Name binding for function: xmlNodeToString.
		 * @see #xmlNodeToString(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName xmlNodeToString = 
			QualifiedName.make(CAL_XmlBuilder.MODULE_NAME, "xmlNodeToString");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = -174283828;

}
