/*
 * Copyright (c) 2007 BUSINESS OBJECTS SOFTWARE LIMITED
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *  
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *  
 *     * Neither the name of Business Objects nor the names of its contributors
 *       may be used to endorse or promote products derived from this software
 *       without specific prior written permission.
 *  
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */


/*
 * DebugSupport.java
 * Created: Mar 14, 2006
 * By: Bo Ilic
 */

package org.openquark.cal.runtime;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.openquark.cal.util.ArrayStack;


/**
 * Contains various helper methods implementing machine-independent debugging support.
 * In particular there are methods for displaying a textual debug string from a CalValue, as well as for
 * calculating various useful statistics based on the CalValue. These methods all have in common that they
 * don't modify the CalValue in any way, and thus are useful, when combined with tracing (such as with the
 * Debug.trace function), for viewing the actual state of values in CAL as execution is occurring, without altering
 * the execution's reduction sequence.
 * 
 * @author Bo Ilic
 */
public final class DebugSupport {
        
    
    /**
     * Immutable value class holding statistical information gathered from a CalValue
     * 
     * The statistics/information gathered by this class are for debugging purposes only and should 
     * not be relied upon in production code. They are subject to change and their precise value will
     * in general depend on the particular machine type (lecc or g) used.
     * 
     * @author Bo Ilic
     */
    public static final class InternalValueStats {
        
        /** 
         * The number of distinct nodes in the graph of nodes represented by a given CalValue 
         * The distinct node count provides a measure of how much space the CalValue is taking. Nodes which
         * are shared and appear multiple times within the graph are only counted once.
         */
        private final int nDistinctNodes; 
        
        /**
         * The number of distinct indirection nodes in the graph of nodes represented by a given CalValue. 
         * The distinct indirection node count provides a measure of how much uncompacted or wasted space the 
         * CalValue is taking. Indirection nodes which are shared and appear multiple times within the graph
         * are only counted once.
         */
        private final int nDistinctIndirectionNodes;    
        
        /**
         * The number of nodes in the graph a given CalValue that are shared within the graph itself.
         */
        private final int nSharedNodes;
        
        private InternalValueStats(int nDistinctNodes, int nDistinctIndirectionNodes, int nSharedNodes) {
            this.nDistinctNodes = nDistinctNodes;
            this.nDistinctIndirectionNodes = nDistinctIndirectionNodes;
            this.nSharedNodes = nSharedNodes;
        }
        
        /** 
         * This value is for debugging purposes only and is subject to change.
         * @return The number of distinct nodes in the graph of nodes represented by a given CalValue. 
         * The distinct node count provides a measure of how much space the CalValue is taking. Nodes which
         * are shared and appear multiple times within the graph are only counted once.
         */        
        public int getNDistinctNodes() {
            return nDistinctNodes;        
        }
        
        /**
         * This value is for debugging purposes only and is subject to change.
         * @return the number of distinct indirection nodes in the graph of nodes represented by a given CalValue. 
         * The distinct indirection node count provides a measure of how much uncompacted or wasted space the 
         * CalValue is taking. Indirection nodes which are shared and appear multiple times within the graph
         * are only counted once.
         */        
        public int getNDistinctIndirectionNodes() {
            return nDistinctIndirectionNodes;
        }
        
        /**
         * This value is for debugging purposes only and is subject to change.
         * @return the number of nodes in the graph a given CalValue that are shared within the graph itself.
         */        
        public int getNSharedNodes() {
            return nSharedNodes;
        }
        
        /**
         * Creates a DebugNodeInfo object which gathers statistics from the argument CalValue.
         * The CalValue node is not modified in any way by calling this method.
         * 
         * @param calValue 
         * @return a DebugNodeInfo object gathering statistics based from the given argument CalValue.
         */
        public static final InternalValueStats make(CalValue calValue) {
            
            if (calValue == null) {
                throw new NullPointerException();
            }
            
            //do a stackless preorder traversal of the CalValue node graph gathering the statistics as we go along.
            //The stackless traversal is so that we are guaranteed not to exhaust the rather limited default Java stack
            //while traversing a deeply recursive CAL value.
            
            //Note: we only traverse nodes not already previously encountered. We could have previously encountered a node
            //due to sharing in the value graph. This can happen due to a number of reasons:
            //a) a let variable or argument variable being reused with an expression
            //b) caching of certain simple values e.g. the lecc machine caches commonly used boxed int values (i.e. RTData.CAL_Int
            //   objects are shared if the underlying int value falls in the caching range).
            //c) recursively defined variables creating cyclic value graphs
                    
            Set<CalValue> distinctNodeSet = new HashSet<CalValue>(); //set of CalValue objects  
            
            int nDistinctIndirectionNodes = 0;
            int nSharedNodes = 0;
            
            //the stack will hold elements of type CalValue
            ArrayStack<CalValue> stack = ArrayStack.make();
                       
            CalValue node = unwrapOpaque(calValue);
            
            stack.push(node); 
            distinctNodeSet.add(node);
            if (node.debug_isIndirectionNode()) {
                ++nDistinctIndirectionNodes;
            }
                    
            while (!stack.isEmpty()) {
                        
                node = stack.pop();
                          
                final int nChildren = node.debug_getNChildren();
                                     
                for (int i = nChildren - 1; i >= 0; --i) {   
                    
                    CalValue childNode = node.debug_getChild(i);                    
                    if (childNode != null) {  
                        
                        if (distinctNodeSet.add(childNode)) {                   
                            stack.push(childNode);
                            if (childNode.debug_isIndirectionNode()) {
                                ++nDistinctIndirectionNodes;
                            }                        
                        } else {
                            ++nSharedNodes;
                        }
                    }
                }                          
            } 
                    
            return new InternalValueStats(distinctNodeSet.size(), nDistinctIndirectionNodes, nSharedNodes);              
        }
                             
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("(nDistinctNodes = ");
            sb.append(nDistinctNodes);
            sb.append(", nDistinctIndirectionNodes = ");
            sb.append(nDistinctIndirectionNodes);
            sb.append(", nSharedNodes = ");
            sb.append(nSharedNodes);
            sb.append(')');
            return sb.toString();
        }
                       
    }
    
    private DebugSupport() {
        //make non-instantiable.
    }
    
    /**
     * Returns a representation of the argument CalValue useful for debugging purposes. This representation
     * is subject to change and should not be relied upon in production code. Does not do any 
     * evaluation or modify the CalValue in any way.
     * 
     * <P>showInternal does not traverse the CalValue graph by using the
     * Java call stack, and hence will not throw a java.lang.StackOverflowError. For example,
     * it can handle displaying evaluated CAL lists that are thousands of elements long.
     * Also, showInternal will successfully display cyclic value graphs.
     * 
     * <P>showInternal will show some of the sharing of graph nodes in the graph structure of the CalValue.
     * If a node having one or more children is shared, its first appearance will be marked 
     * e.g. <@nodeNumber = nodeText>, and subsequent appearances will just display as <@nodeNumber>.
     * Nodes having zero children that are shared (such as function nodes or simple value nodes) are not
     * shown as being shared. This makes the output easier to read while showing the most important sharing.
     * To see more of the graph structure, use showInternalGraph.
     * 
     * <P>Any exceptions (subclasses of Throwable) are caught and rethrown as a java.lang.RuntimeException with
     * cause set to the caught exception, and a message displaying a partial showInternal result string
     * if one is available. The main cause of exceptions will be an exception in Object.toString() on one of the
     * foreign objects held onto by the CalValue graph.
     * 
     * @param calValue CalValue whose textual representation is to be displayed. Cannot be null.
     * @return a textual representation of the CalValue for debugging purposes.    
     */
    public static final String showInternal(CalValue calValue) {        
        return showInternalGraph(new CalValue[]{calValue}, false, false)[0];
    }
    
    /**
     * Returns representations of the argument CalValue elements useful for debugging purposes. This representation
     * is subject to change and should not be relied upon in production code. Does not do any 
     * evaluation or modify the CalValue in any way.
     * 
     * <P>showInternal does not traverse the CalValue graph by using the
     * Java call stack, and hence will not throw a java.lang.StackOverflowError. For example,
     * it can handle displaying evaluated CAL lists that are thousands of elements long.
     * Also, showInternal will successfully display cyclic value graphs.
     * 
     * <P>showInternal will show some of the sharing of graph nodes across the graph structures of the CalValue instances.
     * If a node having one or more children is shared, its first appearance will be marked 
     * e.g. <@nodeNumber = nodeText>, and subsequent appearances will just display as <@nodeNumber>.
     * Nodes having zero children that are shared (such as function nodes or simple value nodes) are not
     * shown as being shared. This makes the output easier to read while showing the most important sharing.
     * To see more of the graph structure, use showInternalGraph.
     * 
     * <P>Any exceptions (subclasses of Throwable) are caught and rethrown as a java.lang.RuntimeException with
     * cause set to the caught exception, and a message displaying a partial showInternal result string
     * if one is available. The main cause of exceptions will be an exception in Object.toString() on one of the
     * foreign objects held onto by the CalValue graph.
     * 
     * @param calValues array of CalValue whose textual representations are to be displayed. Cannot be null.
     * @return a textual representation of the each of the CalValue for debugging purposes.    
     */
    public static final String[] showInternal(CalValue[] calValues) {        
        return showInternalGraph(calValues, false, false);
    }
    
    /**
     * Returns a representation of the CalValue argument useful for debugging purposes. This representation
     * is subject to change and should not be relied upon in production code. Does not do any 
     * evaluation or modify the CalValue in any way.
     * 
     * <P>showInternalGraph does not traverse the CalValue graph by using the
     * Java call stack, and hence will not throw a java.lang.StackOverflowError. For example,
     * it can handle displaying evaluated CAL lists that are thousands of elements long.
     * Also, showInternalGraph will successfully display cyclic value graphs.
     *
     * <P>showInternalGraph attempts to show more of the graph structure of the CalValue. If a node is shared, 
     * its first appearance will be marked e.g. <@nodeNumber = nodeText>, and subsequent appearances will just
     * display as <@nodeNumber>. Indirections are shown using an asterisk *. 
     * To see less of the graph structure, and potentially a more readable output, use showInternal.
     * 
     * <P>Any exceptions (subclasses of Throwable) are caught and rethrown as a java.lang.RuntimeException with
     * cause set to the caught exception, and a message displaying a partial showInternalGraph result string
     * if one is available. The main cause of exceptions will be an exception in Object.toString() on one of the
     * foreign objects held onto by the CalValue graph.
     *
     * @param calValue    
     * @return a textual representation of the CalValue argument for debugging purposes.   
     */
    public static final String showInternalGraph(CalValue calValue) {
        return showInternalGraph(new CalValue[]{calValue}, true, true)[0];
    }
        
     
    /**
     * @param calValue
     * @return if the opaque value held onto by a CAL_Opaque or NValObject is in fact an CalValue, then return that 
     * CalValue, otherwise just return value.          
     */
    private static CalValue unwrapOpaque(CalValue calValue) {
        //todoBI this is a hack that can be taken out when we can find a way so that Debug.distinctNodeCount
        //does not wrap its CalValue in a CALOpaque.        
        return calValue.internalUnwrapOpaque();
    }            
     
    /**
     * Returns a representation of the argument CalValue useful for debugging purposes. This representation
     * is subject to change and should not be relied upon in production code. Does not do any 
     * evaluation or modify the CalValue in any way.
     * 
     * <P>This method is mainly for illustrating the meaning of the contract that the abstract methods
     * CalValue.debug_getNChildren, CalValue.debug_getChild, CalValue.debug_getNodeStartText,
     * CalValue.debug_getNodeEndText and CalValue.debugGetChildPrefixText
     * must adhere to in their implementations. Real code should call DebugSupport.showInternal or showInternalGraph. 
     *
     * <P>As an implementation note, this version traverses the CalValue graph by using the
     * Java call stack, and hence will throw a java.lang.StackOverflowError even for some moderately sized
     * structures, such as displaying an evaluated CAL list of 4000 elements.     
     * 
     * @param calValue cannot be null
     * @return a textual representation of calValue for debugging purposes.
     */
    @SuppressWarnings(value={"unused"})
    private static final String javaStackShowInternal(CalValue calValue) {
        
        if (calValue == null) {
            throw new NullPointerException("calValue cannot be null.");
        }
        
        calValue = unwrapOpaque(calValue);        
        
        //javaStackShowInternal uses the java stack and will blow stack for CAL lists of size a few thousand.       
        StringBuilder sb = new StringBuilder();  
        javaStackShowInternalHelper(calValue, sb);
        return sb.toString();        
    } 
    
    private static final void javaStackShowInternalHelper(CalValue node, StringBuilder sb) {
        sb.append(node.debug_getNodeStartText());
        for (int i = 0, nChildren = node.debug_getNChildren(); i < nChildren; ++i) {
            sb.append(node.debug_getChildPrefixText(i));
            CalValue childNode = node.debug_getChild(i);
            if (childNode != null) {
                javaStackShowInternalHelper(childNode, sb);
            } else {
                sb.append("null");
            }
        }
        sb.append(node.debug_getNodeEndText());
    }  
    
    /**
     * Returns a representation of the CalValue argument useful for debugging purposes. This representation
     * is subject to change and should not be relied upon in production code. Does not do any 
     * evaluation or modify the CalValue in any way.
     * 
     * <P>showInternalGraph does not traverse the CalValue graph by using the
     * Java call stack, and hence will not (in general) throw a java.lang.StackOverflowError. For example,
     * it can handle displaying evaluated CAL lists that are thousands of elements long. showInternalGraph
     * can also handle cyclic value graphs without failure.
     * 
     * <P>Any exceptions (subclasses of Throwable) are caught and rethrown as a java.lang.RuntimeException with
     * cause set to the caught exception, and a message displaying a partial showInternalGraph result string
     * if one is available. The main cause of exceptions will be an exception in Object.toString() on one of the
     * foreign objects held onto by the CalValue graph.
     * 
     * <P>showInternalGraph attempts to show more of the graph structure of the CalValue. If a node is shared, 
     * its first appearance will be marked e.g. <@nodeNumber = nodeText>, and subsequent appearances will just
     * display as <@nodeNumber>. Indirections are shown using an asterisk *. 
     * The extent to which this extra information is displayed is controllable by the showIndirectionNodes and
     * showAllSharedNodes parameters.
     *
     * @param calValues
     * @param showIndirectionNodes 
     *          if true, then indirection nodes will be displayed with an asterisk (*). If false, indirection nodes
     *          will not display any text.
     * @param showAllSharedNodes 
     *          if true, then all nodes, including nodes with zero children such as Int value nodes and function nodes,
     *          that are shared are displayed. Otherwise, only nodes having more than one child that are shared are shown.
     *          Not showing all sharing hides certain trivial sharings and may make the output a bit easier to read in general.
     * @return a textual representation of the CalValue argument for debugging purposes.   
     */
    private static final String[] showInternalGraph(final CalValue[] calValues,
            final boolean showIndirectionNodes,
            final boolean showAllSharedNodes) {
    
        if (calValues == null) {
            throw new NullPointerException("calValues cannot be null.");
        }
        
        
        //showInternalGraph does not use the Java program stack in order to avoid running out of stack space.
        //for example, calling showInternal on a CAL list of a few thousand elements will run out of stack space.
              
        final String displayValues[] = new String [calValues.length];
        StringBuilder sb = null;
        
        try {

            // We want to find out all the shared nodes across the array of CalValue.
            // If there is only one element in the array we can simply find the set for it.
            // Otherwise we need to bundle the array elements into a single graph.
            final CalValue calValue;
            if (calValues.length == 1) {
                calValue = unwrapOpaque(calValues[0]);
                calValues[0] = calValue;
            } else {
                //a helper class used to consider the argumentValues array as a single internal value, so that references
                //shared between elements of the array will be properly identified as being shared in the showInternal call.
                calValue = new CalValue () {
                    
                    @Override
                    public int debug_getNChildren() {               
                        return calValues.length;
                    }

                    @Override
                    public CalValue debug_getChild(int childN) {                
                        return calValues[childN];
                    }

                    @Override
                    public String debug_getNodeStartText() {                
                        return "";
                    }

                    @Override
                    public String debug_getNodeEndText() {               
                        return "";
                    }

                    @Override
                    public String debug_getChildPrefixText(int childN) {                
                        return " ";
                    }

                    @Override
                    public boolean debug_isIndirectionNode() {                
                        return false;
                    }
                    
                    /**
                     * {@inheritDoc}
                     */
                    @Override
                    public final DataType getDataType() {
                        return DataType.OTHER;
                    }

                    @Override
                    public final CalValue internalUnwrapOpaque() {                       
                        return this;
                    }

                    @Override
                    public MachineType debug_getMachineType() {                       
                        throw new UnsupportedOperationException("debug_getMachineType should never be called on this virtual CalValue");
                    }                    
                };        
                
            }
            
            //(CalValue -> Integer) nodes that occur more than once in the graph of the internal value mapped to 
            //the node number to use when displaying them textually.
            final Map<CalValue, Integer> sharedNodeMap = calculateSharedNodeMap(calValue, showAllSharedNodes);
            
            //subset of the keys of sharedNodeMap that have already been visited so we don't need to display the value text
            //<@nodeNumber = nodeText> but only <@nodeNumber>
            final Set<CalValue> alreadyVisitedSharedNodeSet = new HashSet<CalValue>();
        
            
            for (int z = 0; z < calValues.length; ++z) {
                
                sb = new StringBuilder();  

                //the stack will hold elements of type CalValue, as well as elements of type String.
                final ArrayStack<Object> stack = ArrayStack.make();
                stack.push(calValues[z]);
                
                while (!stack.isEmpty()) {
                    
                    final Object value = stack.pop();
                    
                    if (value == null) {
                        //this can happen, since CalValue.debug_getChild can return null.
                        //The reason for this is that RTValue.clearMembers() method may be called before setResult() is called so
                        //the node is no longer an unmodified application node, but not yet an indirection.
                        //For example, in a tail recursive function, the f method calls clearMembers right away, but setResult is
                        //only called at the end. If some of the arguments to f refer to the application node themselves, then when
                        //those are traced, they will encounter null children.
                        sb.append("null");
                        
                    } else if (value instanceof String) {
                        sb.append(value);
                        
                    } else if (value instanceof CalValue) {
                     
                        final CalValue node = (CalValue)value;
                                           
                        Object sharedNodeMapValue = sharedNodeMap.get(node);
                        boolean isShared = sharedNodeMapValue != null;
                        boolean secondOrMoreOccurrence = isShared && alreadyVisitedSharedNodeSet.contains(node);
                        int sharedNodeNumber = isShared ? ((Integer)sharedNodeMapValue).intValue() : 0;
                        
                        if (isShared) {
                            if (secondOrMoreOccurrence) {
                                sb.append("<@").append(sharedNodeNumber).append('>');
                                //do not push children of shared nodes that have already been visited
                                continue;
                            } else {
                                alreadyVisitedSharedNodeSet.add(node);
                            }
                        }
                                              
                        final int nChildren = node.debug_getNChildren();
                        
                        boolean isIndirectionNode = node.debug_isIndirectionNode();
                        if (isIndirectionNode && nChildren != 1) {
                            throw new IllegalStateException("indirection nodes must have exactly 1 child");
                        }
                    
                        if (nChildren == 0) {
                            //optimize the case of nChildren == 0 to avoid pushing getEndNodeText as a String, only to be immediately popped.
                            if (isShared) {
                                sb.append("<@").append(sharedNodeNumber).append(" = ");
                            }
                            sb.append(node.debug_getNodeStartText());
                            sb.append(node.debug_getNodeEndText());
                            if (isShared) {
                                sb.append('>');
                            }
                            
                        } else {
                            
                            if (isShared) {
                                sb.append("<@").append(sharedNodeNumber).append(" = ");
                            } 
                            if (showIndirectionNodes && isIndirectionNode) {
                                sb.append('*');
                            }
                            sb.append(node.debug_getNodeStartText());
                        
                            String nodeEndText;
                            if (isShared) {
                                nodeEndText = node.debug_getNodeEndText() + ">";    
                            } else {
                                nodeEndText = node.debug_getNodeEndText();                            
                            }
                            
                            if (nodeEndText.length() > 0) {
                                stack.push(nodeEndText);
                            }
                                                                                    
                            for (int i = nChildren - 1; i >= 0; --i) {
                                CalValue childNode = node.debug_getChild(i);
                                //note that childNode may be null. This is OK- we still push onto the stack.
                                stack.push(childNode);
                                String childPrefixText = node.debug_getChildPrefixText(i);
                                if (childPrefixText.length() > 0) {
                                    stack.push(childPrefixText);
                                }
                            }
                        }
                    }                          
                }
                
                displayValues[z] = sb.toString();
            }            
        } catch (Throwable exception) {
            
            //catch any exception generated in calling showInternalGraph, in order to create a new exception that captures
            //any partially generated error text.
            
            StringBuilder message = new StringBuilder("(showInternalGraph failed with a ").append(exception.getClass().getName());
                  
            boolean partialResultFound = false;
            for (int i = 0; i < displayValues.length; ++i) {
                String s = displayValues[i];
                if (s != null) {
                    if (!partialResultFound) {
                        message.append(". Partial result: ");
                        partialResultFound = true;
                    }
                    message.append(s);
                }
            }
            
            if (sb != null && sb.length() > 0) {
                if (!partialResultFound) {
                    message.append(". Partial result: ");
                    partialResultFound = true;
                }
                message.append(sb.toString());
            }
            
            if (partialResultFound) { 
                message.append(")");
            } else {
                message.append(".)");
            }
        }
            
        return displayValues;    
   }
    
   /**   
    * @param calValue
    * @param showAllSharedNodes if true, all shared nodes are included in the returned map. Otherwise, only those
    *    nodes with 1 or more children and included.
    * @return (CalValue -> Int). Map from nodes occurring multiple times in the calValue graph to an ordinal.
    *   1 will be for the first node in a pre-order traversal of calValue occurring more than once.
    *   2 will be for the second node in a pre-order traversal occurring twice etc.
    */
   private static final Map<CalValue, Integer> calculateSharedNodeMap(CalValue calValue, boolean showAllSharedNodes) {
        
        // (CalValue -> Boolean) map from referentially distinct CalValue objects to a Boolean indicating
        // whether the CalValue occurs more than once in the graph. The order on the keys is according to a 
        // pre-order traversal of the first occurrence of distinct CalValue objects in the graph.       
        LinkedHashMap<CalValue, Boolean> distinctNodeMap = new LinkedHashMap<CalValue, Boolean>();       
       
        //the stack will hold elements of type CalValue
        ArrayStack<CalValue> stack = ArrayStack.make();        
        stack.push(calValue);         
               
        while (!stack.isEmpty()) {
            
            CalValue node = stack.pop();
                        
            Object previousOccursMoreThanOnce = distinctNodeMap.get(node);
            boolean occursMoreThanOnce;
            if (previousOccursMoreThanOnce == null) {
                occursMoreThanOnce = false;
                distinctNodeMap.put(node, Boolean.FALSE);                
            } else {
                occursMoreThanOnce = true;
                if (!((Boolean)previousOccursMoreThanOnce).booleanValue()) {
                    distinctNodeMap.put(node, Boolean.TRUE);
                }                
            }
            
            if (!occursMoreThanOnce) {
                final int nChildren = node.debug_getNChildren();
                
                for (int i = nChildren - 1; i >= 0; --i) {   
                    
                    CalValue childNode = node.debug_getChild(i);  
                    if (childNode != null) {
                        stack.push(childNode);
                    }
                }                         
            }
        }
        
        //(CalValue -> Int). Map from nodes occurring multiple times to an ordinal. 1 will be for the first
        //node in a pre-order traversal occurring more than once. 2 will be for the second node in a pre-order traversal
        //occurring more than once etc.
        //For example, if distinctNodeMap is [(n1, False), (n2, False), (n3, True), (n4, False), (n5, True)]
        //then multipleNodeMap is [(n3, 1), (n5, 2)]
        Map<CalValue, Integer> multipleNodeMap = new HashMap<CalValue, Integer>();
        
        int multipleNodeNumber = 1;
        for (final Map.Entry<CalValue, Boolean> entry : distinctNodeMap.entrySet()) {  
            
            CalValue node = entry.getKey();
            
            if ((showAllSharedNodes || node.debug_getNChildren() > 0)
                && entry.getValue().booleanValue()) {   
                
                multipleNodeMap.put(node, Integer.valueOf(multipleNodeNumber));
                ++multipleNodeNumber;                      
            }
        }
        
        return multipleNodeMap;
    }
     
    /**
     * A helper function for tracing support.
     * 
     * This is equivalent to displaying the individual space-separated arguments by just
     * calling showInternal, except that reference shared between separate arguments are displayed.
     * For example, in the trace for:
     * let x = [20, 30, 40 :: Int]; in deepSeq x x
     * The 2 arguments of deepSeq will show sharing in the trace such as:
     * Prelude.deepSeq <@1 = (Prelude.Cons 20 (Prelude.Cons 30 (Prelude.Cons 40 Prelude.Nil)))> <@1>
     * 
     * @param argumentValues   
     * @return String
     */
    public static String showInternalForArgumentValues(final CalValue[] argumentValues) {
        
        if (argumentValues == null) {
            throw new NullPointerException();
        }
        
        
        String[] results = showInternal(argumentValues);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < results.length; ++i) {
            sb.append(" ");
            sb.append(results[i]);
        }
        
        return sb.toString();
    }

}
