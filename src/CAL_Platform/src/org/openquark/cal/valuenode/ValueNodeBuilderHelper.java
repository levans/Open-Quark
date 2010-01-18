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
 * ValueNodeBuilderHelper.java
 * Created: June 11, 2001
 * By: Michael Cheng
 */

package org.openquark.cal.valuenode;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import org.openquark.cal.compiler.DataConstructor;
import org.openquark.cal.compiler.ModuleTypeInfo;
import org.openquark.cal.compiler.PreludeTypeConstants;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.TypeConsApp;
import org.openquark.cal.compiler.TypeConstructor;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.services.Perspective;
import org.openquark.util.UnsafeCast;


/**
 * This class is used to help during the construction of ValueNodes (primarily during output mechanism).
 * @author Michael Cheng
 */
public class ValueNodeBuilderHelper {

    /** The perspective from which to gather info to help build stuff. */
    private final Perspective perspective;

    /** Instances of ValueNodeProviders registered with this builder helper. */
    private final List<ValueNodeProvider<?>> providerList = new ArrayList<ValueNodeProvider<?>>();
     
    /** handy TypeExpr constants for common Prelude types. */
    private PreludeTypeConstants preludeTypeConstants;    
    
    /** The module type info for the prelude which the prelude type constants were constructed.
     *  Used to check that the returned constants object is not out of date (eg. after a recompile) .*/
    private ModuleTypeInfo preludeTypeInfo;
    
    /**
     * ValueNodeBuilderHelper constructor.
     * @param perspective the Perspective this builder helper is used in
     */
    public ValueNodeBuilderHelper(Perspective perspective) {

        if (perspective == null) {
            throw new NullPointerException();
        }
        
        this.perspective = perspective;
    }
    
    /**     
     * @return handy TypeExpr constants for common Prelude types.
     */
    public PreludeTypeConstants getPreludeTypeConstants() {
        
        ModuleTypeInfo currentPreludeTypeInfo;
        ModuleTypeInfo workingModuleTypeInfo = perspective.getWorkingModuleTypeInfo();
        
        if (workingModuleTypeInfo.getModuleName().equals(CAL_Prelude.MODULE_NAME)){
            currentPreludeTypeInfo = workingModuleTypeInfo;
        
        } else {
            currentPreludeTypeInfo = workingModuleTypeInfo.getDependeeModuleTypeInfo(CAL_Prelude.MODULE_NAME);
        }
        
        if (currentPreludeTypeInfo != preludeTypeInfo) {   // also handles if info == null.
            this.preludeTypeInfo = currentPreludeTypeInfo;
            this.preludeTypeConstants = new PreludeTypeConstants(preludeTypeInfo);
        }
        
        return preludeTypeConstants;
    }
        
    /**
     * Get a default value node for the given type expr.
     * @param typeExpr the type expression to get a value node for
     * @return the value node most appropriate for the given type expression, or null if a value could not be constructed.
     */
    public ValueNode getValueNodeForTypeExpr(TypeExpr typeExpr) {
        return buildValueNode(null, null, typeExpr);
    }

    /**
     * Builds a new value node to represent the given information.
     * @param value the value for the value node, or null to return a default value for the given data constructor and type expr.
     * @param dataConstructor the data constructor for the value node
     * @param typeExpr the type expression for the value node
     * @return a new value node for the given information, or null if a value node could not be built to represent the given value.
     */
    public ValueNode buildValueNode(Object value, DataConstructor dataConstructor, TypeExpr typeExpr) {
        // Go in reverse order since more important providers are added last.
        for (int i = providerList.size() - 1; i >= 0; i--) {
            
            ValueNodeProvider<?> provider = providerList.get(i);
            ValueNode nodeInstance = provider.getNodeInstance(value, dataConstructor, typeExpr);
            
            if (nodeInstance != null) {
                return nodeInstance;
            }
        }
        
        return null;
    }    
    
    /**
     * Returns the class of the value node associated with a TypeExpr.
     * @param typeExpr the typeExpr to ask about.
     * @return the class of the provider which provides the value node which would handle a value of the given type.
     */
    public Class<? extends ValueNode> getValueNodeClass(TypeExpr typeExpr) {
        // Go in reverse order since more important providers are added last.
        for (int i = providerList.size() - 1; i >= 0; i--) {
            
            ValueNodeProvider<? extends ValueNode> provider = providerList.get(i);
            ValueNode nodeInstance = provider.getNodeInstance(typeExpr);
            
            if (nodeInstance != null) {
                return provider.getValueNodeClass();
            }
        }
        
        return null;
    }
    
    /**
     * @param typeExpr the type expression to check
     * @return true if the type can be handled for output. 
     * This is true for everything except function types and if the type expression is null.
     */
    public static boolean canHandleOutput(TypeExpr typeExpr) {
        return typeExpr != null && !typeExpr.isFunctionType();
    }
    
    /**
     * @return the perspective used by this builder helper
     */
    public Perspective getPerspective() {
        return perspective;
    }

    /**
     * Registers a new value node provider with this builder helper and adds it to the internal
     * list of available providers. A newly registered provider takes precedence over previously
     * registered providers.
     * @param providerClass the class of the provider
     */
    public void registerValueNodeProvider(Class<? extends ValueNodeProvider<?>> providerClass) {

        if (!ValueNodeProvider.class.isAssignableFrom(providerClass)) {
            throw new IllegalArgumentException("given class is not a ValueNodeProvider: " + providerClass);
        }

        try {
            Constructor<? extends ValueNodeProvider<?>> providerConstructor = providerClass.getConstructor(new Class[] {ValueNodeBuilderHelper.class});
                    
            providerConstructor.setAccessible(true);
                
            ValueNodeProvider<?> editorProvider = providerConstructor.newInstance(new Object[] {this});
                
            providerList.add(editorProvider);
        
        } catch (Exception ex) {
            throw new IllegalArgumentException("error instantiating ValueEditorProvider: " + providerClass);
        } catch (LinkageError ex) {
            throw new IllegalArgumentException("error instantiating ValueEditorProvider: " + providerClass);
        }
    }

    /**
     * Get the provider to provide value nodes of a given class.
     * @param valueNodeClass the class of the value node.
     * @return the corresponding registered ValueNodeProvider, or null if no corresponding provider is registered.
     */
    public <T extends ValueNode> ValueNodeProvider<T> getValueNodeProvider(Class<T> valueNodeClass) {
        // Iterate through the provider list, asking each one whether it handles the given class.
        for (final ValueNodeProvider<?> editorProvider : providerList) {
            if (editorProvider.getValueNodeClass() == valueNodeClass) {
                return UnsafeCast.unsafeCast(editorProvider);
            }
        }
        return null;
    }
    
    /**
     * @param typeCons the entity to get a type constructor for
     * @return the type constructor for the entity or null if the entity is not supported
     * An entity is not supported if it is a function type.
     */
    public TypeConsApp getTypeConstructorForEntity(TypeConstructor typeCons) {
        
        if (typeCons.getNDataConstructors() > 0) {
            return typeCons.getNthDataConstructor(0).getTypeConsApp();

            // For everything below, there are no data constructors
            
        } else if (typeCons.getTypeArity() == 0) {
            // This is a non-parametric type i.e. does not involve type parameters.
            return TypeExpr.makeNonParametricType(typeCons).rootTypeConsApp();

        } else if (typeCons.getName().equals(CAL_Prelude.TypeConstructors.Function)) {

            // We don't allow the user to choose the function type for a value gem.
            // However, it is trivial to enable the functionality using the line below.

            //return (TypeConsApp) TypeExpr.makeFunType(TypeExpr.makeParametricType(), TypeExpr.makeParametricType());

            return null;
            
        } else {

            // This is an unknown built-in type and is not handled.
            return null;
        }
    }
    
    /**
     * @param typeConsName the name of the type constructor to return
     * @return the type constructor with the given name if it is visible and supported, null otherwise
     */
    public TypeConsApp getTypeConstructorForName(QualifiedName typeConsName) {
        
        TypeConstructor typeCons = perspective.getTypeConstructor(typeConsName);
        
        if (typeCons != null) {
            return getTypeConstructorForEntity(typeCons);
        }
        
        return null;
    }
}
