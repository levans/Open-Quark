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
 * ValueEditorProvider.java
 * Created: 11-Apr-2003
 * By: David Mosimann
 */
package org.openquark.gems.client.valueentry;

import java.util.HashSet;
import java.util.Set;

import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.TypeConsApp;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.services.Perspective;
import org.openquark.cal.valuenode.ValueNode;
import org.openquark.cal.valuenode.ValueNodeBuilderHelper;


/**
 * A value editor provider is used to provide information about a particular value editor and to provide
 * methods to get an instance of the editor.
 */
public abstract class ValueEditorProvider<T extends ValueEditor> {

    /** The editor manager this provider is being used with. */
    private final ValueEditorManager valueEditorManager;
    
    /**
     * Contains info about which types are supported during value editor handleability calculation.
     * This object is useful in calculating support for algebraic data types, where a given type would be supported only
     *   if all of its children are also supported.  If the type is recursive, this class is needed to avoid infinite loops.
     * 
     * Sample case:
     * data public TypeRep = public TypeRep String [TypeRep];
     * 
     * @author Edward Lam
     */
    public static class SupportInfo {
        /** TypeConsApp.getName() */
        private final Set<QualifiedName> supportedTypeConstructors = new HashSet<QualifiedName>();
        
        /**
         * Notify this info object that the given type is supported.
         * @param typeExpr the type in question.
         */
        public void markSupported(TypeExpr typeExpr) {
            TypeConsApp typeConsApp = typeExpr.rootTypeConsApp();
            if (typeConsApp != null) {
                supportedTypeConstructors.add(typeConsApp.getName());
            }
        }
        
        /**
         * Ask this info object whether the given type has been marked as supported by previous callers.
         * @param typeExpr the type in question
         * @return whether the given type has been marked as supported by previous callers.
         */
        public boolean isSupported(TypeExpr typeExpr) {
            TypeConsApp typeConsApp = typeExpr.rootTypeConsApp();
            return typeConsApp != null && supportedTypeConstructors.contains(typeConsApp.getName());
        }
    }
    
    /**
     * Constructor for a value editor provider.
     * @param valueEditorManager the editor manager this provider is being used with
     */
    public ValueEditorProvider(ValueEditorManager valueEditorManager) {
        
        if (valueEditorManager == null) {
            throw new NullPointerException();
        }
        
        this.valueEditorManager = valueEditorManager;
    }

    /**
     * @return the value editor manager the provider is being used with
     */
    protected ValueEditorManager getValueEditorManager() {
        return valueEditorManager;
    }
    
    /**
     * @return the perspective from the value editor manager the provider is being used with
     */
    protected Perspective getPerspective() {
        return valueEditorManager.getPerspective();
    }
    
    /**
     * Checks if the value editor provided by this provider can handle a given value node.
     * @param valueNode the value node to check
     * @param providerSupportInfo the support info object threaded through nested calls.
     * @return true if the provider can handle the value node
     */
    public abstract boolean canHandleValue(ValueNode valueNode, SupportInfo providerSupportInfo);

    /**
     * Returns an instance of the provided editor.  A new instance will be returned every time this method is
     * called and null will never be returned.  This method will take construct the value editor and set it's
     * value node, however, it will not call setInitialValue() on the editor.  Technically this method should
     * be called after the editor has become the current editor meaning it should be called by the value
     * editor hierarchy manager which determines the current editor. 
     * @param valueEditorHierarchyManager
     * @param valueNode
     * @return ValueEditor
     */    
    public abstract T getEditorInstance(ValueEditorHierarchyManager valueEditorHierarchyManager,
                                                  ValueNode valueNode);
    
    /**
     * @see #getEditorInstance(ValueEditorHierarchyManager, ValueNode)
     * @param valueEditorHierarchyManager
     * @param valueNodeBuilderHelper
     * @param valueNode
     * @return ValueEditor
     */
    public T getEditorInstance(ValueEditorHierarchyManager valueEditorHierarchyManager,
                                         ValueNodeBuilderHelper valueNodeBuilderHelper,
                                         ValueNode valueNode) {
                                             
        return this.getEditorInstance(valueEditorHierarchyManager, valueNode);
    }
    
    /**
     * @see #getEditorInstance(ValueEditorHierarchyManager, ValueNode)
     * @param valueEditorHierarchyManager
     * @param valueNodeBuilderHelper
     * @param dragManager
     * @param valueNode
     * @return ValueEditor
     */
    public T getEditorInstance(ValueEditorHierarchyManager valueEditorHierarchyManager,
                                 ValueNodeBuilderHelper valueNodeBuilderHelper,
                                 ValueEditorDragManager dragManager,
                                 ValueNode valueNode) {
                                             
        return this.getEditorInstance(valueEditorHierarchyManager, valueNodeBuilderHelper, valueNode);
    }
    
    /**
     * Returns the drag point handler used for the value editor, if the return value
     * is <code>null</code>, then the value editor does not support any drag operation.
     * The caller to the drag point handler (in most cases, the value editor itself is
     * the caller) is expected to cast this handler to a more specific type before
     * using it.
     * @param dragManager
     * @return ValueEditorDragPointHandler
     */
    public ValueEditorDragPointHandler getDragPointHandler(ValueEditorDragManager dragManager) {
        return null;
    }

    /**
     * Indicates whether the editor for this type can be launched when
     * viewing output (non-editable).
     */
    public boolean usableForOutput() {
        // Default to false.
        return false;
    }
}
