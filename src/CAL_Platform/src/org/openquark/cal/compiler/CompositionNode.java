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
 * CompositionNode.java
 * Created: December 11th 2000
 * By: Bo Ilic
 */
package org.openquark.cal.compiler;

import java.util.List;

/**
 * Provides an interface for describing pieces in a composition tree. An example
 * of a composition tree would be a Gem graph. This is used by the type checker
 * to provide another way to describe the typing info associated to the inputs
 * and outputs of the Gem graph.
 * 
 * This should be considered an "abstract" interface - implementors should implement one of
 * the subinterfaces.
 * 
 * Creation date: (12/11/00 11:44:13 AM)
 * @author Bo Ilic
 */
public interface CompositionNode {

    /**
     * A representation of an argument to a CompositionNode.
     * @author Edward Lam
     */
    static interface CompositionArgument {
        /**
         * Returns the CompositionNode that is connected to this argument, or null if there isn't any.
         * @return CompositionNode
         */
        CompositionNode getConnectedNode();

        /**
         * Return true if the argument is burnt or false otherwise
         * @return boolean - true if the 'argN'th argument is burnt, false otherwise
         */
        boolean isBurnt();

        /**
         * Returns the name of this argument.  Argument names are always unqualified.
         * NOTE: The name will only be unique within the argument names of the forest to which this argument belongs.
         * It may conflict with Gem names or argument names belonging to other forests in a given set of trees.
         * @return the name of the argument
         */
        String getNameString();
    }
    
    /**
     * A composition node which represents the definition of a let.
     * @author Edward Lam
     */
    static interface Collector extends CompositionNode {
        /**
         * Returns the (unqualified) name of the composition node.
         * @return the (unqualified) name of the node
         */
        String getUnqualifiedName();
        
        /**
         * Get the declared type for the (possibly local) function whose definition is rooted at this collector.
         * @return TypeExpr the declared type. or null if a type has not been declared.
         */
        TypeExpr getDeclaredType();
        
        /**
         * Returns the target collector for which this collector is defined.
         * @return the target collector for which this collector is defined, or null if this is a top-level collector.
         *   The target collector defines the scope of a collector -- the collector definition is visible for other subtrees
         *   targeting the same collector, or any collections occurring within those subtrees.
         */
        Collector getTargetCollector();
        
        /**
         * Get the arguments targeting this collector.
         * @return the arguments targeting this collector.  Never null.
         */
        List<? extends CompositionArgument> getTargetArguments();
    }

    /**
     * A composition node which represents a use of the definition of a let as an application.
     * @author Edward Lam
     */
    static interface Emitter extends CompositionNode.Application {
        /**
         * Obtain the collector corresponding to the definition of the let.
         * @return the collector corresponding to the definition of the let.
         */
        CompositionNode.Collector getCollectorNode();
    }

    /**
     * A composition node which represents an expression which can be applied to its arguments.
     * This should be considered an "abstract" interface - implementors should implement one of
     * the subinterfaces.
     * @author Edward Lam
     */
    static interface Application extends CompositionNode {
    }

    /**
     * A composition node which represents a lambda expression
     * Creation date: (12/12/01 3:50:13 PM)
     * @author Edward Lam
     */
    static interface Lambda extends Application {
        /**
         * Obtain the code contribution of this CompositionNode, assuming this node is of
         * an appropriate code contributing type.
         * @return java.lang.String the code contribution
         */
        String getLambda();
        
        /**
         * @return the code qualification map used for this lambda expression.
         */
        CodeQualificationMap getQualificationMap();
    }

    /**
     * A composition node which represents an GemEntity
     * @author Edward Lam
     */
    static interface GemEntityNode extends Application {
        /**
         * Returns the name of the composition node.
         * @return QualifiedName the name of the GemEntity.
         */
        QualifiedName getName();
    }
    
    /**
     * A composition node which represents a record extraction
     * @author Neil Corkum
     */
    static interface RecordFieldSelectionNode extends CompositionNode {
        /**
         * Gets the name of the field to be extracted from a record.
         * @return the name of the field
         */
        FieldName getFieldName();
    }
    
    /**
     * A composition node which represents a record
     * @author Jennifer Chen
     */
    static interface RecordCreationNode extends CompositionNode {
        /**
         * Gets the name of the field
         * @return the name of the field
         */
        FieldName getFieldName(int index);
    
    }
    
    /**
     * A composition node which represents a value
     * Creation date: (12/12/01 5:50:13 PM)
     * @author Edward Lam
     */
    static interface Value extends CompositionNode {
        /**
         * Obtain the string value of this CompositionNode
         * @return java.lang.String the string value
         */
        String getStringValue();

        /**
         * Obtain the source model of this CompositionNode
         * @return the source model
         */
        SourceModel.Expr getSourceModel();
    }

    /**
     * Returns the number of arguments that this node accepts as inputs. 
     * Note that this includes both bound and free arguments.
     * @return int
     */
    int getNArguments();
    
    /**
     * Get an argument to this node.
     * @param i the index of the argument.
     * @return the argument.
     */
    CompositionArgument getNodeArgument(int i);
}
