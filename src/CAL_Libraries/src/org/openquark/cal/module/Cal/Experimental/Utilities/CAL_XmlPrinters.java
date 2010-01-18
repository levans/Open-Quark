/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_XmlPrinters.java)
 * was generated from CAL module: Cal.Experimental.Utilities.XmlPrinters.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Experimental.Utilities.XmlPrinters module from Java code.
 *  
 * Creation date: Tue Oct 16 15:42:35 PDT 2007
 * --!>
 *  
 */

package org.openquark.cal.module.Cal.Experimental.Utilities;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceModel;

/**
 * This module implements functions for printing XML to strings.
 * <p>
 * The forms provided are:
 * <p>
 * - A canonical form for testing, which is loosely based on Canonical XML 1.0.
 * The purpose is to have stable, predictable output for tests.
 * The public functions implementing this form are <code>Cal.Experimental.Utilities.XmlPrinters.xmlDocumentToCanonicalString</code>,
 * <code>Cal.Experimental.Utilities.XmlPrinters.xmlNodeToCanonicalString</code>, and <code>Cal.Experimental.Utilities.XmlPrinters.xmlAttributeToCanonicalString</code>.
 * <p>
 * - The First and Second XML Canonical Forms, as defined by
 * <a href='http://dev.w3.org/cvsweb/2001/XML-Test-Suite/xmlconf/sun/cxml.html?rev=1.3'>http://dev.w3.org/cvsweb/2001/XML-Test-Suite/xmlconf/sun/cxml.html?rev=1.3</a>.
 * These are used for running the XML W3C Conformance Test Suite.
 * The public functions implementing these forms include <code>Cal.Experimental.Utilities.XmlPrinters.xmlDocumentToFirstXmlCanonicalForm</code>
 * and <code>Cal.Experimental.Utilities.XmlPrinters.xmlDocumentToSecondXmlCanonicalForm</code>. 
 * 
 * @author Malcolm Sharpe
 */
public final class CAL_XmlPrinters {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Experimental.Utilities.XmlPrinters");

	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Experimental.Utilities.XmlPrinters module.
	 */
	public static final class Functions {
		/**
		 * Helper binding method for function: pubidLiteralToSecondXmlCanonicalForm. 
		 * @param pubid
		 * @return the SourceModule.expr representing an application of pubidLiteralToSecondXmlCanonicalForm
		 */
		public static final SourceModel.Expr pubidLiteralToSecondXmlCanonicalForm(SourceModel.Expr pubid) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.pubidLiteralToSecondXmlCanonicalForm), pubid});
		}

		/**
		 * @see #pubidLiteralToSecondXmlCanonicalForm(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param pubid
		 * @return the SourceModel.Expr representing an application of pubidLiteralToSecondXmlCanonicalForm
		 */
		public static final SourceModel.Expr pubidLiteralToSecondXmlCanonicalForm(java.lang.String pubid) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.pubidLiteralToSecondXmlCanonicalForm), SourceModel.Expr.makeStringValue(pubid)});
		}

		/**
		 * Name binding for function: pubidLiteralToSecondXmlCanonicalForm.
		 * @see #pubidLiteralToSecondXmlCanonicalForm(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName pubidLiteralToSecondXmlCanonicalForm = 
			QualifiedName.make(
				CAL_XmlPrinters.MODULE_NAME, 
				"pubidLiteralToSecondXmlCanonicalForm");

		/**
		 * Helper binding method for function: sysidLiteralToSecondXmlCanonicalForm. 
		 * @param sysid
		 * @return the SourceModule.expr representing an application of sysidLiteralToSecondXmlCanonicalForm
		 */
		public static final SourceModel.Expr sysidLiteralToSecondXmlCanonicalForm(SourceModel.Expr sysid) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.sysidLiteralToSecondXmlCanonicalForm), sysid});
		}

		/**
		 * @see #sysidLiteralToSecondXmlCanonicalForm(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param sysid
		 * @return the SourceModel.Expr representing an application of sysidLiteralToSecondXmlCanonicalForm
		 */
		public static final SourceModel.Expr sysidLiteralToSecondXmlCanonicalForm(java.lang.String sysid) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.sysidLiteralToSecondXmlCanonicalForm), SourceModel.Expr.makeStringValue(sysid)});
		}

		/**
		 * Name binding for function: sysidLiteralToSecondXmlCanonicalForm.
		 * @see #sysidLiteralToSecondXmlCanonicalForm(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName sysidLiteralToSecondXmlCanonicalForm = 
			QualifiedName.make(
				CAL_XmlPrinters.MODULE_NAME, 
				"sysidLiteralToSecondXmlCanonicalForm");

		/**
		 * Convert an XML attribute to a canonical string.
		 * @param attribute (CAL type: <code>Cal.Utilities.XmlBuilder.XmlAttribute</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 */
		public static final SourceModel.Expr xmlAttributeToCanonicalString(SourceModel.Expr attribute) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.xmlAttributeToCanonicalString), attribute});
		}

		/**
		 * Name binding for function: xmlAttributeToCanonicalString.
		 * @see #xmlAttributeToCanonicalString(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName xmlAttributeToCanonicalString = 
			QualifiedName.make(
				CAL_XmlPrinters.MODULE_NAME, 
				"xmlAttributeToCanonicalString");

		/**
		 * Helper binding method for function: xmlAttributeToFirstXmlCanonicalForm. 
		 * @param attr
		 * @return the SourceModule.expr representing an application of xmlAttributeToFirstXmlCanonicalForm
		 */
		public static final SourceModel.Expr xmlAttributeToFirstXmlCanonicalForm(SourceModel.Expr attr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.xmlAttributeToFirstXmlCanonicalForm), attr});
		}

		/**
		 * Name binding for function: xmlAttributeToFirstXmlCanonicalForm.
		 * @see #xmlAttributeToFirstXmlCanonicalForm(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName xmlAttributeToFirstXmlCanonicalForm = 
			QualifiedName.make(
				CAL_XmlPrinters.MODULE_NAME, 
				"xmlAttributeToFirstXmlCanonicalForm");

		/**
		 * Helper binding method for function: xmlDocumentDtdToSecondXmlCanonicalForm. 
		 * @param doc
		 * @return the SourceModule.expr representing an application of xmlDocumentDtdToSecondXmlCanonicalForm
		 */
		public static final SourceModel.Expr xmlDocumentDtdToSecondXmlCanonicalForm(SourceModel.Expr doc) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.xmlDocumentDtdToSecondXmlCanonicalForm), doc});
		}

		/**
		 * Name binding for function: xmlDocumentDtdToSecondXmlCanonicalForm.
		 * @see #xmlDocumentDtdToSecondXmlCanonicalForm(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName xmlDocumentDtdToSecondXmlCanonicalForm = 
			QualifiedName.make(
				CAL_XmlPrinters.MODULE_NAME, 
				"xmlDocumentDtdToSecondXmlCanonicalForm");

		/**
		 * Convert an XML document to a canonical string.
		 * @param doc (CAL type: <code>Cal.Utilities.XmlBuilder.XmlDocument</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 */
		public static final SourceModel.Expr xmlDocumentToCanonicalString(SourceModel.Expr doc) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.xmlDocumentToCanonicalString), doc});
		}

		/**
		 * Name binding for function: xmlDocumentToCanonicalString.
		 * @see #xmlDocumentToCanonicalString(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName xmlDocumentToCanonicalString = 
			QualifiedName.make(
				CAL_XmlPrinters.MODULE_NAME, 
				"xmlDocumentToCanonicalString");

		/**
		 * Convert an XML document to the First XML Canonical Form, as defined by
		 * <a href='http://dev.w3.org/cvsweb/2001/XML-Test-Suite/xmlconf/sun/cxml.html?rev=1.3'>http://dev.w3.org/cvsweb/2001/XML-Test-Suite/xmlconf/sun/cxml.html?rev=1.3</a>.
		 * @param doc (CAL type: <code>Cal.Utilities.XmlBuilder.XmlDocument</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 */
		public static final SourceModel.Expr xmlDocumentToFirstXmlCanonicalForm(SourceModel.Expr doc) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.xmlDocumentToFirstXmlCanonicalForm), doc});
		}

		/**
		 * Name binding for function: xmlDocumentToFirstXmlCanonicalForm.
		 * @see #xmlDocumentToFirstXmlCanonicalForm(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName xmlDocumentToFirstXmlCanonicalForm = 
			QualifiedName.make(
				CAL_XmlPrinters.MODULE_NAME, 
				"xmlDocumentToFirstXmlCanonicalForm");

		/**
		 * Convert an XML document to the Second XML Canonical Form, as defined by
		 * <a href='http://dev.w3.org/cvsweb/2001/XML-Test-Suite/xmlconf/sun/cxml.html?rev=1.3'>http://dev.w3.org/cvsweb/2001/XML-Test-Suite/xmlconf/sun/cxml.html?rev=1.3</a>.
		 * <p>
		 * This form differs from the first form only in that notations are printed.
		 * 
		 * @param doc (CAL type: <code>Cal.Utilities.XmlBuilder.XmlDocument</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 */
		public static final SourceModel.Expr xmlDocumentToSecondXmlCanonicalForm(SourceModel.Expr doc) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.xmlDocumentToSecondXmlCanonicalForm), doc});
		}

		/**
		 * Name binding for function: xmlDocumentToSecondXmlCanonicalForm.
		 * @see #xmlDocumentToSecondXmlCanonicalForm(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName xmlDocumentToSecondXmlCanonicalForm = 
			QualifiedName.make(
				CAL_XmlPrinters.MODULE_NAME, 
				"xmlDocumentToSecondXmlCanonicalForm");

		/**
		 * Convert an XML node to a canonical string.
		 * @param node (CAL type: <code>Cal.Utilities.XmlBuilder.XmlNode</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 */
		public static final SourceModel.Expr xmlNodeToCanonicalString(SourceModel.Expr node) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.xmlNodeToCanonicalString), node});
		}

		/**
		 * Name binding for function: xmlNodeToCanonicalString.
		 * @see #xmlNodeToCanonicalString(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName xmlNodeToCanonicalString = 
			QualifiedName.make(
				CAL_XmlPrinters.MODULE_NAME, 
				"xmlNodeToCanonicalString");

		/**
		 * Helper binding method for function: xmlNodeToFirstXmlCanonicalForm. 
		 * @param node
		 * @return the SourceModule.expr representing an application of xmlNodeToFirstXmlCanonicalForm
		 */
		public static final SourceModel.Expr xmlNodeToFirstXmlCanonicalForm(SourceModel.Expr node) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.xmlNodeToFirstXmlCanonicalForm), node});
		}

		/**
		 * Name binding for function: xmlNodeToFirstXmlCanonicalForm.
		 * @see #xmlNodeToFirstXmlCanonicalForm(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName xmlNodeToFirstXmlCanonicalForm = 
			QualifiedName.make(
				CAL_XmlPrinters.MODULE_NAME, 
				"xmlNodeToFirstXmlCanonicalForm");

		/**
		 * Helper binding method for function: xmlNotationToSecondXmlCanonicalForm. 
		 * @param notation
		 * @return the SourceModule.expr representing an application of xmlNotationToSecondXmlCanonicalForm
		 */
		public static final SourceModel.Expr xmlNotationToSecondXmlCanonicalForm(SourceModel.Expr notation) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.xmlNotationToSecondXmlCanonicalForm), notation});
		}

		/**
		 * Name binding for function: xmlNotationToSecondXmlCanonicalForm.
		 * @see #xmlNotationToSecondXmlCanonicalForm(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName xmlNotationToSecondXmlCanonicalForm = 
			QualifiedName.make(
				CAL_XmlPrinters.MODULE_NAME, 
				"xmlNotationToSecondXmlCanonicalForm");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = 2021296981;

}
