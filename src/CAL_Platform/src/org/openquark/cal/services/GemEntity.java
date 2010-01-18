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
 * GemEntity.java
 * Creation date: Oct 3, 2002.
 * By: Edward Lam
 */
package org.openquark.cal.services;

import java.util.List;
import java.util.Locale;

import org.openquark.cal.compiler.DataConstructor;
import org.openquark.cal.compiler.FunctionalAgent;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.Scope;
import org.openquark.cal.compiler.ScopedEntityNamingPolicy;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.metadata.FunctionalAgentMetadata;
import org.openquark.cal.metadata.MetadataManager;


/**
 * A GemEntity encapsulates information for an envEntity, including its source, design, and metadata.
 * @author Edward Lam
 */
public class GemEntity {

    /** The FunctionalAgent encapsulated by this GemEntity. */
    private final FunctionalAgent functionalAgent;
    
    /**
     * The type of the entity, for fast lookup.
     * FunctionalAgent.getTypeExpr() always returns a copy, but its type doesn't change when type checking is done.
     * 
     * This field is lazily computed on first access.
     */
    private TypeExpr entityType;
    
    /** The resource manager to provide metadata and designs for this entity. */
    private final VirtualResourceManager virtualResourceManager;

    /**
     * Constructor for a GemEntity.
     * @param envEntity
     * @param virtualResourceManager
     */
    public GemEntity(FunctionalAgent envEntity, VirtualResourceManager virtualResourceManager) {
        this.virtualResourceManager = virtualResourceManager;
        Assert.isNotNullArgument(envEntity, "envEntity");
        this.functionalAgent = envEntity;
    }

    /**
     * Return the FunctionalAgent backing this GemEntity
     * @return FunctionalAgent
     */
    public FunctionalAgent getFunctionalAgent() {
        return functionalAgent;
    }

    /**
     * Get the name of this entity.
     * @return QualifiedName the name of the entity.
     */
    public final QualifiedName getName() {
        return functionalAgent.getName();
    }

    /**
     * Get the metadata for a scoped entity in this module.
     * @param locale the locale associated with the metadata.
     * @return the metadata for this entity. If the entity has no metadata, then default metadata is returned.
     */
    public FunctionalAgentMetadata getMetadata(Locale locale) {
        MetadataManager metadataManager = virtualResourceManager.getMetadataManager(functionalAgent.getName().getModuleName());
        if (metadataManager == null) {
            return (FunctionalAgentMetadata)MetadataManager.getEmptyMetadata(functionalAgent, locale);
        }
        return (FunctionalAgentMetadata)metadataManager.getMetadata(functionalAgent, locale);
    }
    
    /**
     * @return an array of the metadata objects for this gem, across all locales.
     */
    public FunctionalAgentMetadata[] getMetadataForAllLocales() {
        MetadataManager metadataManager = virtualResourceManager.getMetadataManager(functionalAgent.getName().getModuleName());
        if (metadataManager == null) {
            return new FunctionalAgentMetadata[0];
        }
        
        CALFeatureName featureName = CALFeatureName.getScopedEntityFeatureName(functionalAgent);
        List<ResourceName> listOfResourceNames = metadataManager.getMetadataResourceNamesForAllLocales(featureName);
        
        int n = listOfResourceNames.size();
        FunctionalAgentMetadata[] result = new FunctionalAgentMetadata[n];
        
        for (int i = 0; i < n; i++) {
            ResourceName resourceName = listOfResourceNames.get(i);
            result[i] = (FunctionalAgentMetadata)metadataManager.getMetadata(featureName, LocalizedResourceName.localeOf(resourceName));
        }
        
        return result;
    }

    /**
     * @return true if there is a saved design associated with this entity.
     */
    public boolean hasDesign() {
        GemDesignManager designManager = virtualResourceManager.getDesignManager(functionalAgent.getName().getModuleName());
        if (designManager == null) {
            return false;
        }
        return designManager.hasGemDesign(this);
    }
    
    /**
     * @return the gem design for this entity, if any.
     */
    public GemDesign getDesign(Status loadStatus) {
        GemDesignManager designManager = virtualResourceManager.getDesignManager(functionalAgent.getName().getModuleName());
        if (designManager == null) {
            return null;
        }
        return designManager.getGemDesign(getName(), loadStatus);
    }

    /**
     * Returns the scope of the entity.
     * @return FunctionalAgent.Scope
     */
    public Scope getScope() {
        return functionalAgent.getScope();
    }

    /**
     * The type arity is defined to be the number of arguments that this
     * gem appears to accept when placed on the GemCutter tabletop. This is equal to the
     * number of application nodes in the type of the gem. It is one of several notion of what the
     * "arity" of a gem means. Note, because of hidden overloaded arguments, the actual number
     * of arguments required to fully saturate and evaluate a gem can be greater than this
     * number. For example, the add gem has type Num a => a -> a -> a. Its type arity is 2,
     * but 3 arguments are required to fully saturate it because of the hidden dictionary argument.
     * Also, there is yet another notion of arity: If we say "f = sin" then f has runtime arity 0
     * since it is a CAF, even though the nApplications (type arity) is 1.
     * 
     * @return int
     */
    public int getTypeArity() {
        return getTypeExpr().getArity();
    }

    /**
     * Returns a copy of the TypeExpr of this entity. For example, if the TypeExpr held
     * by the entity is (a -> (Int, b)) -> (a, b) then the returned TypeExpr is
     * (a' -> (Int, b')) -> (a', b').
     *
     * @return TypeExpr
     */
    public synchronized TypeExpr getTypeExpr() {
        if (entityType == null) {
            entityType = functionalAgent.getTypeExpr();
        }
        return entityType;
    }
    
    /**
     * Return whether this entity is a data constructor.
     * @return boolean true if this is a data constructor, false otherwise.
     */
    public boolean isDataConstructor() {
        return functionalAgent instanceof DataConstructor;
    }

    /**
     * Returns the number of arguments of the entity that have names, for example, as specified
     * in the CAL source. The number of named arguments will be less than or equal to the number
     * of actual arguments for the entity. If an entity (such as a function) takes 5 arguments,
     * and has 3 named arguments, then the 4th and 5th argument are unnamed.
     * @return int number of named arguments
     */
    public int getNNamedArguments() {
        return functionalAgent.getNArgumentNames();
    }

    /**
     * Returns the name of the given named argument.
     * @param argN int index into the named arguments.
     * @return unqualified name of the named argument
     */
    public String getNamedArgument(int argN) {
        return functionalAgent.getArgumentName(argN);
    }
    
    /**
     * Returns a String representing the name of the entity with respect to a particular naming policy.
     * For example, one such naming policy might be to always return the fully qualified name.
     * @param namingPolicy
     * @return String
     */
    public String getAdaptedName(ScopedEntityNamingPolicy namingPolicy) {
        return functionalAgent.getAdaptedName(namingPolicy);
    }
       
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "GemEntity: " + functionalAgent.toString();
    }
}