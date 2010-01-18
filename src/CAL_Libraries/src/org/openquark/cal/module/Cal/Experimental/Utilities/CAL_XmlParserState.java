/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_XmlParserState.java)
 * was generated from CAL module: Cal.Experimental.Utilities.XmlParserState.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Experimental.Utilities.XmlParserState module from Java code.
 *  
 * Creation date: Tue Oct 16 15:42:42 PDT 2007
 * --!>
 *  
 */

package org.openquark.cal.module.Cal.Experimental.Utilities;

import org.openquark.cal.compiler.ModuleName;

/**
 * This module implements the state for an XML 1.0 parser written using the
 * <code>Cal.Utilities.Parser</code> library module. Also included are state-related functions,
 * such as general entity expansion and attribute defaults.
 * <p>
 * All the state is kept in a record, <code>Cal.Experimental.Utilities.XmlParserState.XmlParserState</code>,
 * to make it simple to add new variables to the state. Nothing is exposed
 * publically by this module, since it is an implementation detail of the XML
 * parser.
 * 
 * @author Malcolm Sharpe
 */
public final class CAL_XmlParserState {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Experimental.Utilities.XmlParserState");

	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = 745035263;

}
