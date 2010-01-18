/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_BlockingQueue.java)
 * was generated from CAL module: Cal.Experimental.Concurrent.BlockingQueue.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Experimental.Concurrent.BlockingQueue module from Java code.
 *  
 * Creation date: Wed Sep 19 17:19:04 PDT 2007
 * --!>
 *  
 */

package org.openquark.cal.module.Cal.Experimental.Concurrent;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceModel;

/**
 * This module defines a fixed length blocking queue, that can be used in 
 * conjunction with the <code>Cal.Experimental.Concurrent.Parallel</code> 
 * module to pass items between threads.
 * <p>
 * CAL must be started with the system property
 * org.openquark.cal.runtime.lecc.concurrent_runtime for concurrent use.
 * 
 * @author Magnus Byne
 */
public final class CAL_BlockingQueue {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Experimental.Concurrent.BlockingQueue");

	/**
	 * This inner class (TypeConstructors) contains constants
	 * and methods related to binding to CAL TypeConstructors in the Cal.Experimental.Concurrent.BlockingQueue module.
	 */
	public static final class TypeConstructors {
		/**
		 * A blocking queue has a predefined size. When it reaches 
		 * this size, an attempt to put more items will block.
		 */
		public static final QualifiedName BlockingQueue = 
			QualifiedName.make(CAL_BlockingQueue.MODULE_NAME, "BlockingQueue");

	}
	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Experimental.Concurrent.BlockingQueue module.
	 */
	public static final class Functions {
		/**
		 * This creates a blocking queue that can hold at most size elements.
		 * If you attempt to put elements into the queue when it is full
		 * the put will block.
		 * @param maxSize (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the maximum size of the queue
		 * @return (CAL type: <code>Cal.Experimental.Concurrent.BlockingQueue.BlockingQueue a</code>) 
		 */
		public static final SourceModel.Expr makeBlockingQueue(SourceModel.Expr maxSize) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeBlockingQueue), maxSize});
		}

		/**
		 * @see #makeBlockingQueue(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param maxSize
		 * @return the SourceModel.Expr representing an application of makeBlockingQueue
		 */
		public static final SourceModel.Expr makeBlockingQueue(int maxSize) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeBlockingQueue), SourceModel.Expr.makeIntValue(maxSize)});
		}

		/**
		 * Name binding for function: makeBlockingQueue.
		 * @see #makeBlockingQueue(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeBlockingQueue = 
			QualifiedName.make(
				CAL_BlockingQueue.MODULE_NAME, 
				"makeBlockingQueue");

		/**
		 * Put an element on the queue. If the queue already contains size elements
		 * this function will block until another thread removes an item from the queue
		 * @param queue (CAL type: <code>Cal.Experimental.Concurrent.BlockingQueue.BlockingQueue a</code>)
		 *          the queue to add to
		 * @param item (CAL type: <code>a</code>)
		 *          the item to add
		 * @return (CAL type: <code>()</code>) 
		 */
		public static final SourceModel.Expr put(SourceModel.Expr queue, SourceModel.Expr item) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.put), queue, item});
		}

		/**
		 * Name binding for function: put.
		 * @see #put(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName put = 
			QualifiedName.make(CAL_BlockingQueue.MODULE_NAME, "put");

		/**
		 * Takes an item from a queue. If the queue is empty this function will block
		 * until an item is inserted by another thread.
		 * @param queue (CAL type: <code>Cal.Experimental.Concurrent.BlockingQueue.BlockingQueue a</code>)
		 *          the queue to take an item from.
		 * @return (CAL type: <code>a</code>) 
		 */
		public static final SourceModel.Expr take(SourceModel.Expr queue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.take), queue});
		}

		/**
		 * Name binding for function: take.
		 * @see #take(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName take = 
			QualifiedName.make(CAL_BlockingQueue.MODULE_NAME, "take");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = 1430896302;

}
