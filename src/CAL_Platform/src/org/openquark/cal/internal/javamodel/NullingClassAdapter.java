package org.openquark.cal.internal.javamodel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.BasicInterpreter;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.tree.analysis.Value;

/**
 * A class adapter that rewrites method bytecode so that variables
 * are nulled whenever they are statically determined to be dead.
 * This aids the JVM's bytecode interpreter's GC, since it will not
 * otherwise perform this analysis and so will consider these variables
 * to be live references. Once the JIT is invoked, the nulling
 * assignments are removed, which should result in minimal speed penalty.
 * 
 * Overview of method used:
 * <ol>
 * <li>The control flow graph is built by the ASM library.
 * <li>Forward dataflow analysis is performed to determine when variables are
 *     statically known to be null. (Part of this functionality is provided by
 *     the ASM library.)
 * <li>Liveness analysis is performed to determine when variables are statically
 *     known to be dead.
 * <li>Non-nulling assignments to dead variables are removed.
 * <li>Backwards dataflow analysis is performed from allocations to determine when
 *     nulling a variable would be useful; that is, when nulling it might cause it
 *     to be null during an allocation where it would otherwise be non-null.
 * <li>Nullings are inserted where live variables become dead and we have determined
 *     that nulling the variable at that point is potentially useful.
 * </ol>
 * 
 * For a detailed description of the method, see the comments in visitEnd.
 * 
 * @author Malcolm Sharpe
 */
public class NullingClassAdapter extends ClassAdapter {
    
    /**
     * A flag to control the nulling of the 'this' pointer.
     */
    public static boolean nullThis = true;
    
    /**
     * Constructs a NullingClassAdapter from the given ClassVisitor.
     * @param cv the ClassVisitor to wrap.
     */
    public NullingClassAdapter(ClassVisitor cv) {
        super(cv);
    }

    /**
     * Visit a class header, remembering its internal name for later use.
     */
    @Override public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        internalName = name;
    }
    
    /** The internal name of the class currently being visited. */
    private String internalName;
    
    /**
     * Visit a method, converting it to ASM's tree format, and insert nullings.
     */
    @Override public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        return new MethodNode(access, name, desc, signature, exceptions) {
            @Override public void visitEnd() {
                // Build the control flow graph and determine when variables are known to be
                // null.
                // Analyzer does not initialize some frames before newControlFlowExceptionEdge
                // is called, so remember the edges for later use.
                final ArrayList<Integer> edgeInsns = new ArrayList<Integer>();
                final ArrayList<Integer> edgeSuccessors = new ArrayList<Integer>();
                
                Analyzer a = new Analyzer(new IsNullInterpreter()) {
                    @Override protected Frame newFrame(int nLocals, int nStack) {
                        return new Node(nLocals, nStack);
                    }
                    @Override protected Frame newFrame(Frame src) {
                        return new Node(src);
                    }
                    @Override protected void newControlFlowEdge(int insn, int successor) {
                        edgeInsns.add(insn);
                        edgeSuccessors.add(successor);
                    }
                    @Override protected boolean newControlFlowExceptionEdge(int insn, int successor) {
                        newControlFlowEdge(insn, successor);
                        return true;
                    }
                };
                
                Frame[] frames;
                try {
                    frames = a.analyze(internalName, this);
                } catch (AnalyzerException e) {
                    throw new RuntimeException(e);
                }

                assert frames.length == instructions.size();

                // Use stored edges to build the graph.
                for (int i = 0; i < edgeInsns.size(); i++) {
                    int insn = edgeInsns.get(i);
                    int successor = edgeSuccessors.get(i);
                    
                    Node n = (Node)a.getFrames()[insn];
                    Node m = (Node)a.getFrames()[successor];
                    n.addSucc(m);
                    m.addPred(n);
                }
                edgeInsns.clear();
                edgeSuccessors.clear();
                
                // Retrieve nodes from the node array.
                Node[] nodes = new Node[frames.length];
                for (int i = 0; i < nodes.length; i++) {
                    nodes[i] = (Node)frames[i];
                    
                    // Tell the node which index it has.
                    if (nodes[i] != null) nodes[i].setIndex(i);
                }
                
                // Initialize the 'def', 'use', and 'alloc' sets for each node.
                AbstractInsnNode insn = instructions.getFirst();
                for (int i = 0; i < nodes.length; i++) {
                    if (nodes[i] != null) nodes[i].initInfoSets(insn);
                    
                    insn = insn.getNext();
                }
                assert insn == null;
                
                // Iterate liveness analysis until sets are unchanged.
                // To reduce the number of iterations, we use depth-first
                // search to reverse topologically sort the nodes.
                Node[] updateOrder = getReversePseudoTopologicalOrder(nodes);
                dirty = true;
                while (dirty) {
                    dirty = false;
                    for (int i = 0; i < updateOrder.length; i++) {
                        updateOrder[i].updateLiveness();
                    }
                }

                // Replace dead non-nulling assignments with pops.
                // Note that this does not affect liveness information.
                insn = instructions.getFirst();
                for (int i = 0; i < nodes.length; i++) {
                    AbstractInsnNode next = insn.getNext();
                    if (nodes[i] != null) nodes[i].removeDeadAssignments(instructions, insn);
                    insn = next;
                }
                assert insn == null;
                
                // Find useful locations for nulling.
                //
                // To do this, we perform backward dataflow analysis from allocations. This is
                // similar to liveness analysis, but not exactly the same, since a potential 
                // allocation makes _all_ variables unsafe-in at that node, unlike the corresponding
                // 'use' in liveness analysis.
                //
                // Definitions:
                // - The alloc set of a node contains all local variables if executing that node
                //   could cause an allocation, and otherwise it is empty.
                // - A variable is unsafe on an edge if there is a directed path from that edge
                //   to an alloc that does not go through any def of that variable. A variable is
                //   unsafe-in at a node if it is unsafe on any of the in-edges of that node; it
                //   is unsafe-out at a node if it is unsafe-out on any of the out-edges of the
                //   node.
                //   (TODO: We would like a def that's different from liveness analysis's def. The
                //    reason is that an local object reference might be overwritten with, say, an
                //    integer local, once the object reference goes out of scope. In this case, the
                //    overwriting does our job for us. However, this situation seems unlikely enough
                //    that it is ignored for the moment.)
                //
                // Dataflow equations:
                //   in[n]  = alloc[n] U (out[n] - def[n])
                //   out[n] = U for s in succ[n] of in[s]
                dirty = true;
                while (dirty) {
                    dirty = false;
                    for (int i = 0; i < updateOrder.length; i++) {
                        updateOrder[i].updateSafety();
                    }
                }

                // Null out locals where desirable.
                //
                // For any given local object reference x, the properties we want are:
                // 1. At any node where x is not live-in and an allocation could occur during
                //    the execution of the node, x is null during the execution of the node.
                // 2. x must not be nulled while it is live.
                // 3. When possible, we should prefer to insert few instructions.
                //
                // Safety information is solely used to get better results for property 3.
                //
                // To achieve property 1 while not violating properties 2 & 3, we develop a method
                // by induction on the number of executed nodes in a given run of the program. The
                // inductive hypothesis is similar to property 1:
                // (I.H.) At any node where x is not live-in and x is unsafe-in, x is null during the
                //        execution of the node.
                // Note that any node satisfying this inductive hypothesis also satisfies property 1,
                // since x is unsafe-in at any node that could perform an allocation.
                //
                // We prefer to insert nullings only before the execution of nodes, since inserting
                // them after the execution of nodes is difficult in some cases, such as jumps.
                // (TODO: Determine whether a code size advantage could be gained by sometimes inserting
                //  nullings after the execution of nodes.)
                //
                // Note that we have already performed this pre-processing: for each node v that assigns
                // a possibly non-null value to x, if x is not live-out at v, we replaced v's instruction
                // with a pop instruction. This makes no difference to the meaning of the program, since
                // the stack effect is the same and the stored value is never read. (TODO: However, this
                // does affect debugging, since the variable will appear as null when it would not otherwise.)
                //
                // (Basis)
                //   For the first executed node, nodes[0], arguments are the only locals which
                //   might be non-null. Therefore, before the execution of this node, we null out
                //   x if it is an argument that is not live-in and is unsafe-in, so the I.H. holds.
                // (Inductive Step)
                //   Suppose we have executed at least one node's instruction. Let v be the next
                //   node to be executed. If x is live-in or is not unsafe-in at v, then the I.H. holds
                //   trivially. Otherwise, x is not live-in and is unsafe-in at v. Let u be the previously
                //   executed node.
                //   - If x is live-out at u, then it might be non-null, so we null x before
                //     executing v.
                //   - If x is live-in at u, then it might have been non-null before the execution of
                //     u, and since we only null before the execution of nodes, it might still be
                //     non-null after u is executed. Thus, we null x before executing v.
                //   - If neither of the previous cases holds, then x is neither live-in nor live-out at u.
                //     > If x is not unsafe-in at u, note that x being unsafe-in at v implies x is unsafe-out
                //       at u. Thus, x must be a member of u's def set. Since x is not live-out at u, this
                //       must be a nulling assignment to x, since we removed all other dead assignments.
                //     > Otherwise, x is unsafe-in at u. By the I.H., x was null during the execution of u.
                //       Since we have removed non-nulling assignments to x where x is not live-out, x remained
                //       null after the execution of u.
                //     In either subcase, x is null before the execution of v, so we do not need to set it to
                //     null.
                //   Thus, property 1 holds for v.
                //
                // To apply this method statically, we null x before a node v if any statically-possible
                // execution path requires it. i.e. If x is not live-in and is unsafe-in at v, we null x
                // before v if any predecessor of v has x live-in or live-out.
                //
                // There is a practical complication introduced by labels, since they are considered nodes
                // by the ASM library. Since labels are the target of jumps, it is not possible to null x
                // immediately before the execution of the label. However, since labels do nothing during
                // their execution, it is safe to null x immediately _after_ the execution of the label.
                
                // Calculate which object reference locals were live before each node.
                BitSet objectArguments = getObjectArguments();
                for (int i = 0; i < nodes.length; i++) {
                    if (nodes[i] != null) {
                        nodes[i].calculatePreviouslyLive(objectArguments);
                    }
                }
                
                // Insert nullings.
                insn = instructions.getFirst();
                for (int i = 0; i < nodes.length; i++) {
                    if (nodes[i] != null) insn = nodes[i].insertNullings(instructions, insn, isInstanceMethod());
                    else insn = insn.getNext();
                }
                assert insn == null;

                accept(cv);
            }
            
            /**
             * Returns a set containing all the local variables that are both
             * object references and arguments.
             */
            private BitSet getObjectArguments() {
                Type[] argumentTypes = Type.getArgumentTypes(desc);
                BitSet objectArguments = new BitSet();
                
                int stackOffset = 0;
                
                // Implicit 'this' argument.
                if (isInstanceMethod()) {
                    objectArguments.set(0);
                    stackOffset++;
                }
                
                // Explicit arguments.
                for (int i = 0; i < argumentTypes.length; i++) {
                    int sort = argumentTypes[i].getSort();
                    if (sort == Type.ARRAY || sort == Type.OBJECT) {
                        objectArguments.set(stackOffset);
                    }
                    stackOffset += argumentTypes[i].getSize();
                }
                
                return objectArguments;
            }
            
            /**
             * Returns true if this is an instance method. i.e. the 0'th local
             * is the this pointer. Otherwise returns false.
             */
            private boolean isInstanceMethod() {
                return 0 == (access & Opcodes.ACC_STATIC);
            }
        };
    }

    /**
     * Print debugging information about each node in the control graph.
     * @param nodes the control graph nodes.
     */
    @SuppressWarnings("unused") private static void dumpNodes(Node[] nodes) {
        for (int i = 0; i < nodes.length; i++) {
            System.err.println(nodes[i]);
        }
    }
    
    /** True if some set has changed in this iteration of liveness. False otherwise. */
    private boolean dirty;
    
    /**
     * Sort the nodes in reverse pseudo-topological order using depth-first
     * search, to determine a good update order for reverse data flow analyses.
     * Unreachable nodes are ignored.
     * 
     * @param nodes the nodes as they are ordered in the method.
     * @return the reachable nodes in reverse pseudo-topological order.
     */
    private static Node[] getReversePseudoTopologicalOrder(Node[] nodes) {
        ArrayList<Node> result = new ArrayList<Node>();
        
        // Depth-first search, written iteratively so as not to overrun the Java
        // stack.
        // By adding the nodes in order of finishing time, we get a reverse
        // pseudo-topological order. It is only pseudo-topological since the
        // control graph may be cyclic.
        Set<Node> visited = new HashSet<Node>();
        Stack<Node> nodeStack = new Stack<Node>();
        Stack<NodeCons> succIteratorStack = new Stack<NodeCons>();
        
        nodeStack.push(nodes[0]);
        succIteratorStack.push(nodes[0].succ);
        while (!nodeStack.isEmpty()) {
            if (succIteratorStack.peek() != null) {
                Node n = succIteratorStack.peek().node;
                succIteratorStack.push(succIteratorStack.pop().next);
                if (!visited.contains(n)) {
                    visited.add(n);
                    nodeStack.push(n);
                    succIteratorStack.push(n.succ);
                }
            } else {
                result.add(nodeStack.pop());
                succIteratorStack.pop();
            }
        }
        
        // Avoid ArrayList.toArray since it uses slow reflection.
        Node[] resultArray = new Node[result.size()];
        for (int i = 0; i < resultArray.length; i++) {
            resultArray[i] = result.get(i);
        }
        return resultArray;
    }
    
    /**
     * A node in the control flow graph.
     * 
     * @author Malcolm Sharpe
     */
    private class Node extends Frame {
        
        /** The successors of this node. */
        private NodeCons succ = null;
        
        /** The predecessors of this node. */
        private NodeCons pred = null;
        
        public Node(int nLocals, int nStack) {
            super(nLocals, nStack);
        }
        public Node(Frame src) {
            super(src);
        }
        
        /** Add a successor of this node. */
        public final void addSucc(Node successor) {
            succ = new NodeCons(successor, succ);
        }
        
        /** Add a predecessor of this node. */
        public final void addPred(Node predecessor) {
            pred = new NodeCons(predecessor, pred);
        }
        
        /** Those local variables that are live on at least one in-edge. */
        private BitSet liveIn = new BitSet();
        
        /** Those local variables that are live on at least one out-edge. */
        private BitSet liveOut = new BitSet();
        
        /** Those local variables that are assigned to by this node's instruction. */
        private BitSet def = new BitSet();
        
        /** Those local variables that are read by this node's instruction. */
        private BitSet use = new BitSet();
        
        /**
         * Those local variables that, if non-live, should be null here because an
         * an allocation could occur.
         */
        private BitSet alloc = new BitSet();
        
        /** Those variables that are unsafe on at least one in-edge. */
        private BitSet unsafeIn = new BitSet();
        
        /** Those variables that are unsafe on at least one out-edge. */
        private BitSet unsafeOut = new BitSet();
        
        /** Initialize def, use, and alloc sets based on the instruction corresponding to this node. */
        public final void initInfoSets(AbstractInsnNode insn) {
            if (AbstractInsnNode.VAR_INSN == insn.getType()) {
                // Detect reads and writes to local object references.
                VarInsnNode var = (VarInsnNode)insn;
    
                // We care only about object references and return addresses. (The latter only
                // because it uses the astore instruction.)
                // Read opcodes:
                //   aload, aload_n (for 0 <= n <= 3), ret (for return addresses)
                // Write opcodes:
                //   astore, astore_n (for 0 <= n <= 3)
                // ASM handles the *_n forms automatically.
                switch (var.getOpcode()) {
                case Opcodes.ALOAD:
                case Opcodes.RET:
                    use.set(var.var);
                    break;
                case Opcodes.ASTORE:
                    def.set(var.var);
                    break;
                }
            } else {
                // Detect potential allocations, caused by explicit allocations or occurring in method invocations.
                switch (insn.getOpcode()) {
                case Opcodes.ANEWARRAY:
                case Opcodes.MULTIANEWARRAY:
                case Opcodes.NEW:
                case Opcodes.NEWARRAY:
                case Opcodes.INVOKEINTERFACE:
                case Opcodes.INVOKESPECIAL:
                case Opcodes.INVOKESTATIC:
                case Opcodes.INVOKEVIRTUAL:
                    alloc.set(0, getLocals());
                }
            }
        }
        
        /** Update the 'liveIn' and 'liveOut' sets of this node. */
        public void updateLiveness() {
            int oldOutCardinality = liveOut.cardinality();
            for (NodeCons cons = succ; cons != null; cons = cons.next) {
                Node n = cons.node;
                liveOut.or(n.liveIn);
            }
            if (liveOut.cardinality() != oldOutCardinality) dirty = true;
            
            liveIn.or(liveOut);
            liveIn.andNot(def);
            liveIn.or(use);
        }

        /** Update the 'unsafeIn' and 'unsafeOut' sets of this node. */
        public void updateSafety() {
            int oldOutCardinality = unsafeOut.cardinality();
            for (NodeCons cons = succ; cons != null; cons = cons.next) {
                Node n = cons.node;
                unsafeOut.or(n.unsafeIn);
            }
            if (unsafeOut.cardinality() != oldOutCardinality) dirty = true;
            
            unsafeIn.or(unsafeOut);
            unsafeIn.andNot(def);
            unsafeIn.or(alloc);
        }
        
        /** The index of this node in the array of nodes. */
        private int index = -1;
        
        /** Set the index of this node in the array of nodes. */
        public void setIndex(int index) {
            this.index = index;
        }
        
        /**
         * Print this node in a format that is helpful for debugging.
         */
        public String toString() {
            // Get the ordered indices of the successors of this node.
            int[] succIndices = new int[nodeConsLength(succ)];
            int i = 0;
            for (NodeCons cons = succ; cons != null; cons = cons.next) {
                Node n = cons.node;
                succIndices[i++] = n.index;
            }
            Arrays.sort(succIndices);
            
            // Format the node information.
            StringBuilder sb = new StringBuilder();
            
            sb.append("Node ");
            sb.append(index);
            sb.append(" ->");
            
            for (int index : succIndices) {
                sb.append(" ");
                sb.append(index);
            }
            
            sb.append(";");
            for (int j = 0; j < getStackSize(); j++) {
                sb.append(" ");
                Value v = getStack(j);
                sb.append(v == IsNullInterpreter.NULL ? "null" : v);
            }
            
            return sb.toString();
        }
        
        /** 
         * Those object reference local variables that are live-in or live-out
         * in any predecessor node, or in the case of the first node, all object
         * reference arguments.
         */
        private BitSet previouslyLive = new BitSet();
        
        /**
         * Initialize the previouslyLive set.
         */
        public final void calculatePreviouslyLive(BitSet objectArguments) {
            if (index == 0) {
                // This is the first node, so consider object reference arguments
                // previously live.
                previouslyLive.or(objectArguments);
            }
            
            for (NodeCons cons = pred; cons != null; cons = cons.next) {
                Node n = cons.node;
                previouslyLive.or(n.liveIn);
                previouslyLive.or(n.liveOut);
            }
        }
        
        /**
         * Replace non-nulling assignments to dead local references with pops, since these
         * otherwise cause trouble for our rewriter. Nulling assignments are allowed since
         * they spare us from inserting our own nullings.
         * 
         * The 'def' set is updated for use by later analyses.
         */
        public final void removeDeadAssignments(InsnList instructions, AbstractInsnNode insn) {
            if (insn.getOpcode() != Opcodes.ASTORE) return;
            
            VarInsnNode varInsn = (VarInsnNode)insn;
            
            Value topValue = getStack(getStackSize() - 1);
            if (!liveOut.get(varInsn.var) && topValue != IsNullInterpreter.NULL) {
                InsnNode pop = new InsnNode(Opcodes.POP);
                instructions.insert(insn, pop);
                instructions.remove(insn);
                def.clear();
            }
        }
        
        /**
         * Insert nullings if required.
         */
        public final AbstractInsnNode insertNullings(InsnList instructions, AbstractInsnNode insn, boolean isInstanceMethod) {
            assert insn != null;
            
            // Calculate who we need to null.
            // Object.clone() is slow, so avoid it.
            BitSet toNull = new BitSet();
            toNull.or(previouslyLive);
            toNull.andNot(liveIn);
            toNull.and(unsafeIn);
            if (!nullThis && isInstanceMethod) {
                toNull.clear(0);
            }
            
            AbstractInsnNode next = insn.getNext(); 
            if (toNull.cardinality() > 0) {
                // Construct the nulling instructions.
                InsnList nullingInsns = new InsnList();
                for (int i = 0; i < toNull.length(); i++) {
                    if (toNull.get(i)) {
                        AbstractInsnNode pushNull = new InsnNode(Opcodes.ACONST_NULL);
                        AbstractInsnNode store = new VarInsnNode(Opcodes.ASTORE, i);
                        nullingInsns.add(pushNull);
                        nullingInsns.add(store);
                    }
                }
                
                if (AbstractInsnNode.LABEL == insn.getType()) {
                    instructions.insert(insn, nullingInsns);
                } else {
                    instructions.insertBefore(insn, nullingInsns);
                }
            }
            
            return next;
        }
        
    }

    /**
     * A pair of a Node and another NodeCons. Used for lightweight adjacency lists.
     * 
     * @author Malcolm Sharpe
     */
    private static final class NodeCons {
        
        public Node node;
        public NodeCons next;
        
        public NodeCons(Node node, NodeCons next) {
            this.node = node;
            this.next = next;
        }
        
    }
    
    /**
     * Finds the length of a linked list of nodes.
     * @param cons the linked list of nodes.
     * @return the length of the list.
     */
    private static int nodeConsLength(NodeCons cons) {
        int result = 0;
        while (cons != null) {
            result++;
            cons = cons.next;
        }
        return result;
    }
    
}

/**
 * A BasicInterpreter that determines which values are known to be
 * null, based off the example in the ASM User Guide.
 * 
 * @author Malcolm Sharpe
 */
class IsNullInterpreter extends BasicInterpreter {
    
    /** The value of object references guaranteed to be null. */
    public final static BasicValue NULL = new BasicValue(Type.getObjectType("java/lang/Object"));
    
    @Override public Value newOperation(AbstractInsnNode insn) {
        // Only aconst_null is certain to create a null value.
        if (insn.getOpcode() == Opcodes.ACONST_NULL) {
            return NULL;
        } else {
            return super.newOperation(insn);
        }
    }
    
    @Override public Value unaryOperation(AbstractInsnNode insn, Value value) throws AnalyzerException {
        // Eclipse's Java compiler emits a CHECKCAST instruction when assigning null
        // to a variable. BasicInterpreter does not propagate our null information
        // through, so we must do this ourselves.
        if (insn.getOpcode() == Opcodes.CHECKCAST && NULL == value) {
            return NULL;
        } else {
            return super.unaryOperation(insn, value);
        }
    }
    
    @Override public Value merge(Value v, Value w) {
        // If both values are null, then their merged value is surely null,
        // but otherwise we do not know.
        if (NULL == v && NULL == w) {
            return NULL;
        } else {
            return super.merge(v, w);
        }
    }
    
}