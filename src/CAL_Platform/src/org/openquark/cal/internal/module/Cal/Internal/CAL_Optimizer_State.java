/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_Optimizer_State.java)
 * was generated from CAL module: Cal.Internal.Optimizer_State.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Internal.Optimizer_State module from Java code.
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
 * The file contains data objects that hold state for the traversal functions. There are two main types. 
 * <p>
 * TransformState -&gt; This contains the information about the context of the traversal. For 
 * example when processing a given expression this will contain information about the 
 * type of given symbols if known. This variable is not threaded but maintained on the stack.
 * <p>
 * TransformHistory -&gt; This is threaded throughout the call and contains information about the history of the
 * current traversal. For example, if a fusion of two functions has been attempted but had failed then
 * there is information in the history about this failure to avoid repeating the attempt.
 * <p>
 * This file also contains helper functions that involve types, expression and the current state. For example,
 * getting the type an expression requires the current state that contains information mapping symbols to types.
 * 
 * @author Greg McClement
 */
public final class CAL_Optimizer_State {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Internal.Optimizer_State");

	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = -1132511771;

}
