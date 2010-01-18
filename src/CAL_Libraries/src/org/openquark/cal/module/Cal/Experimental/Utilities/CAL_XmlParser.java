/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_XmlParser.java)
 * was generated from CAL module: Cal.Experimental.Utilities.XmlParser.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Experimental.Utilities.XmlParser module from Java code.
 *  
 * Creation date: Wed Oct 17 17:18:29 PDT 2007
 * --!>
 *  
 */

package org.openquark.cal.module.Cal.Experimental.Utilities;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceModel;

/**
 * This module implements an XML 1.0 parser based on the functions
 * from the <code>Cal.Utilities.Parser</code> library module.
 * <p>
 * The <code>Cal.Experimental.Utilities.XmlParser.parseXmlDocument</code> function accepts a <code>String</code> containing
 * XML text, parses this, and returns the resulting <code>Cal.Utilities.XmlBuilder.XmlDocument</code> or
 * throws an error if the XML is not well-formed.
 * <p>
 * The <code>Cal.Experimental.Utilities.XmlParser.parseXmlDocumentFromBytes</code> function accepts a <code>Array Byte</code>
 * containing encoded XML text, parses this, and returns the resulting <code>Cal.Utilities.XmlBuilder.XmlDocument</code>
 * or throws an error if the XML is not well-formed.
 * <p>
 * This XML parser is not high performance. If a high performance parser is desired, consider
 * wrapping a Java XML parser using foreign declarations.
 * <p>
 * The XML parser is implemented by these modules:
 * - <code>Cal.Experimental.Utilities.XmlParserEngine</code> defines the individual parsers as well as entry
 * points for parsing strings and encoded byte arrays.
 * - <code>Cal.Experimental.Utilities.XmlParserState</code> defines the XML parser state and functions to operate on it,
 * including related logic, such as general entity expansion and attribute defaults. 
 * <p>
 * The conformance of the parser is tested by <code>org.openquark.cal.foreignsupport.module.Engine_Tests</code>,
 * which drives the XML W3C Conformance Test Suite.
 * 
 * @author Richard Webster
 * @author Malcolm Sharpe
 */
public final class CAL_XmlParser {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Experimental.Utilities.XmlParser");

	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Experimental.Utilities.XmlParser module.
	 */
	public static final class Functions {
		/**
		 * Parses a string into an XML document.
		 * @param xmlText (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @return (CAL type: <code>Cal.Utilities.XmlBuilder.XmlDocument</code>) 
		 */
		public static final SourceModel.Expr parseXmlDocument(SourceModel.Expr xmlText) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.parseXmlDocument), xmlText});
		}

		/**
		 * @see #parseXmlDocument(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param xmlText
		 * @return the SourceModel.Expr representing an application of parseXmlDocument
		 */
		public static final SourceModel.Expr parseXmlDocument(java.lang.String xmlText) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.parseXmlDocument), SourceModel.Expr.makeStringValue(xmlText)});
		}

		/**
		 * Name binding for function: parseXmlDocument.
		 * @see #parseXmlDocument(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName parseXmlDocument = 
			QualifiedName.make(CAL_XmlParser.MODULE_NAME, "parseXmlDocument");

		/**
		 * Parses a byte array into an XML document.
		 * @param xmlBytes (CAL type: <code>Cal.Collections.Array.Array Cal.Core.Prelude.Byte</code>)
		 * @return (CAL type: <code>Cal.Utilities.XmlBuilder.XmlDocument</code>) 
		 */
		public static final SourceModel.Expr parseXmlDocumentFromBytes(SourceModel.Expr xmlBytes) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.parseXmlDocumentFromBytes), xmlBytes});
		}

		/**
		 * Name binding for function: parseXmlDocumentFromBytes.
		 * @see #parseXmlDocumentFromBytes(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName parseXmlDocumentFromBytes = 
			QualifiedName.make(
				CAL_XmlParser.MODULE_NAME, 
				"parseXmlDocumentFromBytes");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = -1278718229;

}
