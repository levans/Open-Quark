/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_UniqueIdentifier.java)
 * was generated from CAL module: Cal.Utilities.UniqueIdentifier.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Utilities.UniqueIdentifier module from Java code.
 *  
 * Creation date: Tue Aug 28 15:58:42 PDT 2007
 * --!>
 *  
 */

package org.openquark.cal.module.Cal.Utilities;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceModel;

/**
 * <code>Cal.Utilities.UniqueIdentifier.UniqueIdentifier</code> is a type-safe way of representing a unique name for some value.
 * For example, a <code>(UniqueIdentifier Dimension)</code> represents a unique identifier for a <code>Dimension</code>.
 * <p>
 * There are functions to build a <code>Cal.Utilities.UniqueIdentifier.UniqueIdentifier</code> from a unique name <code>Cal.Core.Prelude.String</code>, 
 * and to get the unique name <code>Cal.Core.Prelude.String</code> from a <code>Cal.Utilities.UniqueIdentifier.UniqueIdentifier</code>.
 * The <code>Cal.Utilities.UniqueIdentifier.convertUniqueIdentifier</code> function can be used to coerce one type of identifier to another type (with the same unique name).
 * <p>
 * This module also contains the <code>Cal.Utilities.UniqueIdentifier.UniquelyNamedItem</code> type class which can be implemented by any type that has both a unique identifier and display name.
 * 
 * @author Richard Webster
 */
public final class CAL_UniqueIdentifier {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Utilities.UniqueIdentifier");

	/**
	 * This inner class (TypeClasses) contains constants
	 * and methods related to binding to CAL TypeClasses in the Cal.Utilities.UniqueIdentifier module.
	 */
	public static final class TypeClasses {
		/**
		 * Type class used by types with a display name and a unique names.
		 */
		public static final QualifiedName UniquelyNamedItem = 
			QualifiedName.make(
				CAL_UniqueIdentifier.MODULE_NAME, 
				"UniquelyNamedItem");

	}
	/**
	 * This inner class (TypeConstructors) contains constants
	 * and methods related to binding to CAL TypeConstructors in the Cal.Utilities.UniqueIdentifier module.
	 */
	public static final class TypeConstructors {
		/**
		 * <code>JRefinedUniqueIdentifier</code> Java type
		 */
		public static final QualifiedName JUniqueIdentifier = 
			QualifiedName.make(
				CAL_UniqueIdentifier.MODULE_NAME, 
				"JUniqueIdentifier");

		/** Name binding for TypeConsApp: UniqueIdentifier. */
		public static final QualifiedName UniqueIdentifier = 
			QualifiedName.make(
				CAL_UniqueIdentifier.MODULE_NAME, 
				"UniqueIdentifier");

	}
	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Utilities.UniqueIdentifier module.
	 */
	public static final class Functions {
		/**
		 * Helper binding method for function: appendStringToUniqueIdentifier. 
		 * @param id
		 * @param str
		 * @return the SourceModule.expr representing an application of appendStringToUniqueIdentifier
		 */
		public static final SourceModel.Expr appendStringToUniqueIdentifier(SourceModel.Expr id, SourceModel.Expr str) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.appendStringToUniqueIdentifier), id, str});
		}

		/**
		 * @see #appendStringToUniqueIdentifier(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param id
		 * @param str
		 * @return the SourceModel.Expr representing an application of appendStringToUniqueIdentifier
		 */
		public static final SourceModel.Expr appendStringToUniqueIdentifier(SourceModel.Expr id, java.lang.String str) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.appendStringToUniqueIdentifier), id, SourceModel.Expr.makeStringValue(str)});
		}

		/**
		 * Name binding for function: appendStringToUniqueIdentifier.
		 * @see #appendStringToUniqueIdentifier(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName appendStringToUniqueIdentifier = 
			QualifiedName.make(
				CAL_UniqueIdentifier.MODULE_NAME, 
				"appendStringToUniqueIdentifier");

		/**
		 * Converts a unique ID of one type to a unique ID of another type.
		 * @param uniqueID (CAL type: <code>Cal.Utilities.UniqueIdentifier.UniqueIdentifier a</code>)
		 * @return (CAL type: <code>Cal.Utilities.UniqueIdentifier.UniqueIdentifier b</code>) 
		 */
		public static final SourceModel.Expr convertUniqueIdentifier(SourceModel.Expr uniqueID) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.convertUniqueIdentifier), uniqueID});
		}

		/**
		 * Name binding for function: convertUniqueIdentifier.
		 * @see #convertUniqueIdentifier(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName convertUniqueIdentifier = 
			QualifiedName.make(
				CAL_UniqueIdentifier.MODULE_NAME, 
				"convertUniqueIdentifier");

		/**
		 * Returns <code>Cal.Core.Prelude.True</code> if both objects have the same unique ID.
		 * @param ndf1 (CAL type: <code>Cal.Utilities.UniqueIdentifier.UniquelyNamedItem a => a</code>)
		 * @param ndf2 (CAL type: <code>Cal.Utilities.UniqueIdentifier.UniquelyNamedItem a => a</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 */
		public static final SourceModel.Expr equalUniqueIdentifiers(SourceModel.Expr ndf1, SourceModel.Expr ndf2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.equalUniqueIdentifiers), ndf1, ndf2});
		}

		/**
		 * Name binding for function: equalUniqueIdentifiers.
		 * @see #equalUniqueIdentifiers(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName equalUniqueIdentifiers = 
			QualifiedName.make(
				CAL_UniqueIdentifier.MODULE_NAME, 
				"equalUniqueIdentifiers");

		/**
		 * Helper binding method for function: findMatchingUniquelyNamedItemById. 
		 * @param items
		 * @param id
		 * @return the SourceModule.expr representing an application of findMatchingUniquelyNamedItemById
		 */
		public static final SourceModel.Expr findMatchingUniquelyNamedItemById(SourceModel.Expr items, SourceModel.Expr id) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.findMatchingUniquelyNamedItemById), items, id});
		}

		/**
		 * Name binding for function: findMatchingUniquelyNamedItemById.
		 * @see #findMatchingUniquelyNamedItemById(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName findMatchingUniquelyNamedItemById = 
			QualifiedName.make(
				CAL_UniqueIdentifier.MODULE_NAME, 
				"findMatchingUniquelyNamedItemById");

		/**
		 * Helper binding method for function: getDisplayName. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of getDisplayName
		 */
		public static final SourceModel.Expr getDisplayName(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getDisplayName), arg_1});
		}

		/**
		 * Name binding for function: getDisplayName.
		 * @see #getDisplayName(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getDisplayName = 
			QualifiedName.make(
				CAL_UniqueIdentifier.MODULE_NAME, 
				"getDisplayName");

		/**
		 * Returns the pieces of the qualified name held by unique identifier.
		 * <p>
		 * TODO: add a special data constructor for indentifiers which represent a qualified name as a string.
		 * 
		 * @param indentifier (CAL type: <code>Cal.Utilities.UniqueIdentifier.UniqueIdentifier a</code>)
		 * @return (CAL type: <code>[Cal.Core.Prelude.String]</code>) 
		 */
		public static final SourceModel.Expr getIndentifierNamePieces(SourceModel.Expr indentifier) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getIndentifierNamePieces), indentifier});
		}

		/**
		 * Name binding for function: getIndentifierNamePieces.
		 * @see #getIndentifierNamePieces(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getIndentifierNamePieces = 
			QualifiedName.make(
				CAL_UniqueIdentifier.MODULE_NAME, 
				"getIndentifierNamePieces");

		/**
		 * Helper binding method for function: getUniqueIdentifier. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of getUniqueIdentifier
		 */
		public static final SourceModel.Expr getUniqueIdentifier(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getUniqueIdentifier), arg_1});
		}

		/**
		 * Name binding for function: getUniqueIdentifier.
		 * @see #getUniqueIdentifier(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getUniqueIdentifier = 
			QualifiedName.make(
				CAL_UniqueIdentifier.MODULE_NAME, 
				"getUniqueIdentifier");

		/**
		 * Returns the string representation of the unique identifier
		 * @param identifier (CAL type: <code>Cal.Utilities.UniqueIdentifier.UniqueIdentifier a</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 */
		public static final SourceModel.Expr getUniqueName(SourceModel.Expr identifier) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getUniqueName), identifier});
		}

		/**
		 * Name binding for function: getUniqueName.
		 * @see #getUniqueName(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getUniqueName = 
			QualifiedName.make(
				CAL_UniqueIdentifier.MODULE_NAME, 
				"getUniqueName");

		/**
		 * Helper binding method for function: jObjectToJUniqueIdentifier. 
		 * @param sofaMember
		 * @return the SourceModule.expr representing an application of jObjectToJUniqueIdentifier
		 */
		public static final SourceModel.Expr jObjectToJUniqueIdentifier(SourceModel.Expr sofaMember) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jObjectToJUniqueIdentifier), sofaMember});
		}

		/**
		 * Name binding for function: jObjectToJUniqueIdentifier.
		 * @see #jObjectToJUniqueIdentifier(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jObjectToJUniqueIdentifier = 
			QualifiedName.make(
				CAL_UniqueIdentifier.MODULE_NAME, 
				"jObjectToJUniqueIdentifier");

		/**
		 * Converts from <code>Cal.Utilities.UniqueIdentifier.JUniqueIdentifier</code> to <code>Cal.Utilities.UniqueIdentifier.UniqueIdentifier</code>
		 * @param jUniqueIdentifier (CAL type: <code>Cal.Utilities.UniqueIdentifier.JUniqueIdentifier</code>)
		 * @return (CAL type: <code>Cal.Utilities.UniqueIdentifier.UniqueIdentifier a</code>) 
		 */
		public static final SourceModel.Expr jUniqueIdentifierToUniqueIdentifier(SourceModel.Expr jUniqueIdentifier) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jUniqueIdentifierToUniqueIdentifier), jUniqueIdentifier});
		}

		/**
		 * Name binding for function: jUniqueIdentifierToUniqueIdentifier.
		 * @see #jUniqueIdentifierToUniqueIdentifier(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jUniqueIdentifierToUniqueIdentifier = 
			QualifiedName.make(
				CAL_UniqueIdentifier.MODULE_NAME, 
				"jUniqueIdentifierToUniqueIdentifier");

		/**
		 * Helper binding method for function: makeUniqueIdentifierByName. 
		 * @param name
		 * @return the SourceModule.expr representing an application of makeUniqueIdentifierByName
		 */
		public static final SourceModel.Expr makeUniqueIdentifierByName(SourceModel.Expr name) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeUniqueIdentifierByName), name});
		}

		/**
		 * @see #makeUniqueIdentifierByName(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param name
		 * @return the SourceModel.Expr representing an application of makeUniqueIdentifierByName
		 */
		public static final SourceModel.Expr makeUniqueIdentifierByName(java.lang.String name) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeUniqueIdentifierByName), SourceModel.Expr.makeStringValue(name)});
		}

		/**
		 * Name binding for function: makeUniqueIdentifierByName.
		 * @see #makeUniqueIdentifierByName(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeUniqueIdentifierByName = 
			QualifiedName.make(
				CAL_UniqueIdentifier.MODULE_NAME, 
				"makeUniqueIdentifierByName");

		/**
		 * Constructs a unique ID using the specified name path.
		 * <p>
		 * TODO: use a special data constructor so that it is easy to identify unique IDs based on a name path...
		 * 
		 * @param namePieces (CAL type: <code>[Cal.Core.Prelude.String]</code>)
		 * @return (CAL type: <code>Cal.Utilities.UniqueIdentifier.UniqueIdentifier a</code>) 
		 */
		public static final SourceModel.Expr makeUniqueIdentifierByNamePath(SourceModel.Expr namePieces) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeUniqueIdentifierByNamePath), namePieces});
		}

		/**
		 * Name binding for function: makeUniqueIdentifierByNamePath.
		 * @see #makeUniqueIdentifierByNamePath(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeUniqueIdentifierByNamePath = 
			QualifiedName.make(
				CAL_UniqueIdentifier.MODULE_NAME, 
				"makeUniqueIdentifierByNamePath");

		/**
		 * Contructs a unique ID using the specified name, if it exists in the list of names
		 * specified then name2 is tried and so on till we get a name that's unique.
		 * @param name (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @param identifiers (CAL type: <code>[Cal.Utilities.UniqueIdentifier.UniqueIdentifier a]</code>)
		 * @return (CAL type: <code>Cal.Utilities.UniqueIdentifier.UniqueIdentifier a</code>) 
		 */
		public static final SourceModel.Expr makeUniqueIdentifierWithinList(SourceModel.Expr name, SourceModel.Expr identifiers) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeUniqueIdentifierWithinList), name, identifiers});
		}

		/**
		 * @see #makeUniqueIdentifierWithinList(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param name
		 * @param identifiers
		 * @return the SourceModel.Expr representing an application of makeUniqueIdentifierWithinList
		 */
		public static final SourceModel.Expr makeUniqueIdentifierWithinList(java.lang.String name, SourceModel.Expr identifiers) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeUniqueIdentifierWithinList), SourceModel.Expr.makeStringValue(name), identifiers});
		}

		/**
		 * Name binding for function: makeUniqueIdentifierWithinList.
		 * @see #makeUniqueIdentifierWithinList(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeUniqueIdentifierWithinList = 
			QualifiedName.make(
				CAL_UniqueIdentifier.MODULE_NAME, 
				"makeUniqueIdentifierWithinList");

		/**
		 * Helper binding method for function: outputUniqueIdentifier. 
		 * @param uniqueId
		 * @return the SourceModule.expr representing an application of outputUniqueIdentifier
		 */
		public static final SourceModel.Expr outputUniqueIdentifier(SourceModel.Expr uniqueId) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.outputUniqueIdentifier), uniqueId});
		}

		/**
		 * Name binding for function: outputUniqueIdentifier.
		 * @see #outputUniqueIdentifier(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName outputUniqueIdentifier = 
			QualifiedName.make(
				CAL_UniqueIdentifier.MODULE_NAME, 
				"outputUniqueIdentifier");

		/**
		 * Converts from <code>Cal.Utilities.UniqueIdentifier.UniqueIdentifier</code> to <code>Cal.Utilities.UniqueIdentifier.JUniqueIdentifier</code>
		 * @param uniqueId (CAL type: <code>Cal.Utilities.UniqueIdentifier.UniqueIdentifier a</code>)
		 * @return (CAL type: <code>Cal.Utilities.UniqueIdentifier.JUniqueIdentifier</code>) 
		 */
		public static final SourceModel.Expr uniqueIdentifierToJUniqueIdentifier(SourceModel.Expr uniqueId) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.uniqueIdentifierToJUniqueIdentifier), uniqueId});
		}

		/**
		 * Name binding for function: uniqueIdentifierToJUniqueIdentifier.
		 * @see #uniqueIdentifierToJUniqueIdentifier(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName uniqueIdentifierToJUniqueIdentifier = 
			QualifiedName.make(
				CAL_UniqueIdentifier.MODULE_NAME, 
				"uniqueIdentifierToJUniqueIdentifier");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = 300271022;

}
