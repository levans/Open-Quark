/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_Optimizer_Traversers.java)
 * was generated from CAL module: Cal.Internal.Optimizer_Traversers.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Internal.Optimizer_Traversers module from Java code.
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
 * This module contains all functions that are used to traverse and modify expressions. The two 
 * traversers are transformAcc and transform. <code>Cal.Internal.Optimizer_Traversers.transformAcc</code> traverses and the expression bottom up 
 * and maintains an accumulator throughout the traversal. <code>Cal.Internal.Optimizer_Traversers.transform</code> traverses the expression bottom up 
 * and applies the given transformers. The difference is that transformAcc only maintains TransformState
 * but transform maintains TransformHistory as well.
 * <p>
 * Also included in this file are some functions for combining transformations. For example, combineTransforms 
 * takes two transformation functions and returns a single transformation function.
 * <p>
 * TODO: Get rid of transformAcc. 
 * 
 * @author Greg McClement
 */
public final class CAL_Optimizer_Traversers {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Internal.Optimizer_Traversers");

	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = -1256776636;

}
