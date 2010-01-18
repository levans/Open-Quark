/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_Optimizer.java)
 * was generated from CAL module: Cal.Internal.Optimizer.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Internal.Optimizer module from Java code.
 *  
 * Creation date: Fri Mar 16 13:11:56 PST 2007
 * --!>
 *  
 */

package org.openquark.cal.internal.module.Cal.Internal;

import org.openquark.cal.compiler.ModuleName;

/**
 * WARNING WARNING WARNING WARNING WARNING WARNING WARNING WARNING WARNING WARNING WARNING WARNING WARNING WAR
 * <p>
 * This file is part of the compiler and not to be modified. 
 * <p>
 * ING WARNING WARNING WARNING WARNING WARNING WARNING WARNING WARNING WARNING WARNING WARNING WARNING WARNING
 * <p>
 * There are five modules that comprise the optimizer.
 * <p>
 * <code>Cal.Internal.Optimizer</code>
 * This module contains the code that applies the optimizating transformations to the given expression. 
 * <code>Cal.Internal.Optimizer.optimize</code> is the entry point to the optimization process.
 * <p>
 * <code>Cal.Internal.Optimizer_Type</code>
 * This module contains the definitions for type expressions in CAL. This file contains helper functions
 * including all the code for type unifications.
 * <p>
 * <code>Cal.Internal.Optimizer_Expression</code>
 * This module contains all the definition for expression objects in CAL. 
 * <p>
 * <code>Cal.Internal.Optimizer_State</code>
 * This module contains the definition for TransformState and TransformHistory as well as supporting functions. 
 * These data values are used by the traversal and transformation functions.
 * <p>
 * <code>Cal.Internal.Optimizer_Traversers</code>
 * This module contains functions for traversing expressions and performing transformations.
 * <p>
 * <code>Cal.Internal.Optimizer_Transformations</code>
 * This module contains all of the optimizing transformations as well as supporting functions.
 * <p>
 * TODO Add some of the fancy new cal type elements. 
 * 
 * @author Greg McClement
 */
public final class CAL_Optimizer {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Internal.Optimizer");

	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = 2052029017;

}
