/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_Optimizer_Transformations.java)
 * was generated from CAL module: Cal.Internal.Optimizer_Transformations.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Internal.Optimizer_Transformations module from Java code.
 *  
 * Creation date: Fri Mar 16 13:11:56 PST 2007
 * --!>
 *  
 */

package org.openquark.cal.internal.module.Cal.Internal;

import org.openquark.cal.compiler.ModuleName;

/**
 * WARNING WARNING WARNING WARNING WARNING WARNING WARNING WARNING WARNING
 * WARNING WARNING WARNING WARNING WAR
 * <p>
 * This file is part of the compiler and not to be modified.
 * <p>
 * ING WARNING WARNING WARNING WARNING WARNING WARNING WARNING WARNING WARNING
 * WARNING WARNING WARNING WARNING
 * <p>
 * This module contains all functions that perform optimizing transformations of
 * expressions. Some of the transformations currently defined are
 * <p>
 * performFusion
 * <p>
 * This transformation can decide to create a new function that fuses two other
 * functions and in the process eliminates some data constructors.
 * <p>
 * transform_specialization
 * <p>
 * This transformation can decide to create a new specialized version of a
 * function with more specific types and possible eliminate arguments by
 * embeddeding given values.
 * <p>
 * transform_3_2_2
 * <p>
 * This transformation performs inlining of values.
 * <p>
 * There are a number of other transformations defined in the file, see below.
 * <p>
 * TODO rename all the tranformation so that they have some naming convention
 * that is consistent and so they can be easily found.
 * 
 * @author Greg McClement
 */
public final class CAL_Optimizer_Transformations {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Internal.Optimizer_Transformations");

	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = 1039032637;

}
