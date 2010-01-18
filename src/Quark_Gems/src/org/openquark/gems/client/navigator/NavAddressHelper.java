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
 * NavAddressHelper.java
 * Creation date: Dec 5, 2003
 * By: Frank Worsley
 */
package org.openquark.gems.client.navigator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openquark.cal.compiler.CALDocComment;
import org.openquark.cal.compiler.ClassInstance;
import org.openquark.cal.compiler.FieldName;
import org.openquark.cal.compiler.FunctionalAgent;
import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.PolymorphicVarContext;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.ScopedEntity;
import org.openquark.cal.compiler.ScopedEntityNamingPolicy;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.metadata.ArgumentMetadata;
import org.openquark.cal.metadata.CALFeatureMetadata;
import org.openquark.cal.metadata.FunctionMetadata;
import org.openquark.cal.metadata.FunctionalAgentMetadata;
import org.openquark.cal.metadata.InstanceMethodMetadata;
import org.openquark.cal.services.CALFeatureName;
import org.openquark.cal.services.CALWorkspace;
import org.openquark.cal.services.GemEntity;
import org.openquark.gems.client.CollectorGem;
import org.openquark.gems.client.Gem.PartInput;
import org.openquark.gems.client.navigator.NavAddress.NavAddressMethod;


/**
 * A utility class that provides helper methods for working with NavAddresses and
 * also for working with metadata.
 * @author Frank Worsley
 */
public final class NavAddressHelper {
    //
    // This class is not intended to be instantiated.
    //
    private NavAddressHelper() {
    }
    
    /**
     * Gets metadata for the entity the given address is for. This will also
     * validate the metadata so that it has the correct number of arguments for
     * the entity the address points to.
     * @param owner the navigator owner to use to resolve the address
     * @param address the address to get metadata for
     * @return the metadata associated with the entity the address points to or
     * null if there is no metadata associated with the address
     */
    public static CALFeatureMetadata getMetadata(NavFrameOwner owner, NavAddress address) {
        
        CALFeatureMetadata metadata = null;
        NavAddressMethod method = address.getMethod();

        if (method == NavAddress.WORKSPACE_METHOD || method == NavAddress.SEARCH_METHOD) {
            return null;
            
        } else if (method == NavAddress.COLLECTOR_METHOD) {
            CollectorGem collector = owner.getCollector(address.getBase());
            metadata = collector != null ? collector.getDesignMetadata() : null;
            
        } else {
            CALFeatureName featureName = address.toFeatureName();
            CALWorkspace workspace = owner.getPerspective().getWorkspace();
            
            if (workspace.getMetaModule(featureName.toModuleName()) != null) {
                metadata = workspace.getMetadata(featureName, owner.getLocaleForMetadata());
            } else {
                return null;
            }
        }

        if (metadata == null) {
            return null;
        }
        
        if (!isMetadataValid(owner, address, metadata)) {
            System.out.println("Invalid or missing metadata for "+address);
            return null;
        }
        
        // Check if this refers to an argument and return argument metadata if that is the case.
        if (address.getParameter(NavAddress.ARGUMENT_PARAMETER) != null) {
            
            int argNum = Integer.parseInt(address.getParameter(NavAddress.ARGUMENT_PARAMETER));
            ArgumentMetadata[] arguments;
            if (metadata instanceof InstanceMethodMetadata) {
                arguments = ((InstanceMethodMetadata) metadata).getArguments();
            } else {
                arguments = ((FunctionalAgentMetadata) metadata).getArguments();
            }
            
            if (argNum < 0 || argNum > arguments.length) {
                throw new IllegalArgumentException("argument number is invalid: " + argNum);
            }
            
            return arguments[argNum];
        }
        
        return metadata;
    }
    
    /**
     * Saves the given metadata for the entity the given address is for.
     * @param owner the navigator owner
     * @param address the address of the entity to save the metadata for
     * @param metadata the metadata to save
     * @return true if save successful, false otherwise
     */
    public static boolean saveMetadata(NavFrameOwner owner, NavAddress address, CALFeatureMetadata metadata) {

        NavAddressMethod method = address.getMethod();
        
        if (method == NavAddress.WORKSPACE_METHOD || method == NavAddress.SEARCH_METHOD) {
            return false;
            
        } else if (address.getParameter(NavAddress.ARGUMENT_PARAMETER) != null) {

            // We only want to save changes made to the argument metadata. So get the
            // latest parent metadata available and simply replace its argument metadata.
                
            int argNum = Integer.parseInt(address.getParameter(NavAddress.ARGUMENT_PARAMETER));
            
            if (address.getMethod() == NavAddress.INSTANCE_METHOD_METHOD) {
                NavAddress parentAddress = NavAddress.getAddress(address.toFeatureName()); // this strips out the &argument=n parameter
                
                CALFeatureMetadata parentFeatureMetadata = getMetadata(owner, parentAddress);
                if (parentFeatureMetadata == null) {
                    return false;
                }
                
                InstanceMethodMetadata parentMetadata = (InstanceMethodMetadata)parentFeatureMetadata;
                
                ArgumentMetadata[] arguments = parentMetadata.getArguments();
                arguments[argNum] = (ArgumentMetadata) metadata;
                parentMetadata.setArguments(arguments);
                
                return saveMetadata(owner, parentAddress, parentMetadata);
                
            } else {
                NavAddress parentAddress = address.withAllStripped();
                
                CALFeatureMetadata parentFeatureMetadata = getMetadata(owner, parentAddress);
                if (parentFeatureMetadata == null) {
                    return false;
                }
                
                FunctionalAgentMetadata parentMetadata = (FunctionalAgentMetadata)parentFeatureMetadata;
                
                ArgumentMetadata[] arguments = parentMetadata.getArguments();
                arguments[argNum] = (ArgumentMetadata) metadata;
                parentMetadata.setArguments(arguments);
                
                return saveMetadata(owner, parentAddress, parentMetadata);
            }
            
        } else if (method == NavAddress.COLLECTOR_METHOD) {
            
            CollectorGem collector = owner.getCollector(address.getBase());
            
            if (collector == null) {
                return false;
            }
                
            collector.setDesignMetadata((FunctionMetadata) metadata);
                
            // If a collector is not connected, it means it has no arguments.
            if (!collector.getCollectingPart().isConnected()) {
                return true;
            }
                
            // Set the argument metadata for the PartInputs.
            List<PartInput> inputs = collector.getTargetInputs();
            ArgumentMetadata[] argMetadata = ((FunctionMetadata) metadata).getArguments();
            
            for (int i = 0; i < argMetadata.length; i++) {
                inputs.get(i).setDesignMetadata(argMetadata[i]);
            }
            
            return true;
            
        } else {
            return owner.getPerspective().getWorkspace().saveMetadata(metadata);
        }
    }
    
    /**
     * Refreshes the given metadata with the latest metadata available for the entity
     * this address is for. For a collector this means getting the latest collector
     * metadata and setting the argument metadata to the latest metadata from the inputs.
     * For other entities this simply means loading the latest metadata from the metadata
     * manager.
     * @param owner the navigator owner
     * @param address the address the metadata object is for
     * @param metadata the metadata object to refresh
     * @return true if the metadata was refreshed, false otherwise
     */
    public static boolean refreshMetadata(NavFrameOwner owner, NavAddress address, CALFeatureMetadata metadata) {
        
        CALFeatureMetadata newMetadata = getMetadata(owner, address);
        
        if (metadata == null) {
            return false;
        }
        
        newMetadata.copyTo(metadata);
        
        return true;
    }

    /**
     * Checks that the metadata is valid for the entity that this address is for. If it
     * is not it will modify the metadata to make it valid, if possible. Currently the only 
     * thing this checks for is that the number of arguments in the metadata matches the number
     * of arguments of the entity. If this is not the case the number of arguments stored
     * in the metadata is adjusted.
     * @param owner the navigator owner
     * @param address the address the metadata is for
     * @param metadata the metadata to validate
     * @return true if the metadata exists and was successfully validated, false otherwise
     */
    private static boolean isMetadataValid(NavFrameOwner owner, NavAddress address, CALFeatureMetadata metadata) {
        
        NavAddressMethod method = address.getMethod();
        
        if (method == NavAddress.FUNCTION_METHOD ||
            method == NavAddress.CLASS_METHOD_METHOD ||
            method == NavAddress.DATA_CONSTRUCTOR_METHOD) {

            GemEntity entity = owner.getPerspective().getWorkspace().getGemEntity(address.toFeatureName().toQualifiedName());
            
            if (entity == null) {
                return false;
            }
            
            return isMetadataValid(entity, (FunctionalAgentMetadata) metadata);
            
        } else if (method == NavAddress.INSTANCE_METHOD_METHOD) {

            CALFeatureName featureName = address.toFeatureName();
            QualifiedName typeClassName = featureName.toInstanceIdentifier().getTypeClassName();
            String methodName = featureName.toInstanceMethodName();
            QualifiedName classMethodName = QualifiedName.make(typeClassName.getModuleName(), methodName);
            
            GemEntity entity = owner.getPerspective().getWorkspace().getGemEntity(classMethodName);
            
            if (entity == null) {
                return false;
            }
            
            return isMetadataValid(entity, (InstanceMethodMetadata) metadata);
            
        } else if (method == NavAddress.COLLECTOR_METHOD) {
            
            CollectorGem collector = owner.getCollector(address.getBase());
            
            if (collector == null) {
                return false;
            }
            
            return isMetadataValid(collector, (FunctionMetadata) metadata);
        }
        return true;    // No additional validation for other address types
    }
    
    /**
     * Validates the given metadata object by synchronizing it with the given entity.
     * After calling this method the number of arguments stored by the metadata will
     * match the number of arguments of the entity.
     * @param entity the entity to validate the metadata with
     * @param metadata the metadata to validate
     * @return true if the metadata is valid, false otherwise
     */
    public static boolean isMetadataValid(GemEntity entity, FunctionalAgentMetadata metadata) {
        return isMetadataValid(entity.getFunctionalAgent(), metadata);
    }
    
    /**
     * Validates the given metadata object by synchronizing it with the given entity.
     * After calling this method the number of arguments stored by the metadata will
     * match the number of arguments of the entity.
     * @param entity the entity to validate the metadata with
     * @param metadata the metadata to validate
     * @return true if the metadata is valid, false otherwise
     */
    public static boolean isMetadataValid(FunctionalAgent entity, FunctionalAgentMetadata metadata) {
        
        int numArgs = entity.getTypeExpr().getArity();
        
        ArgumentMetadata[] actualArgs = new ArgumentMetadata[numArgs];
        ArgumentMetadata[] currentArgs = metadata.getArguments();
        
        if (actualArgs.length == currentArgs.length) {
            return true;
        }
        
        int argsToCopy = currentArgs.length > actualArgs.length ? numArgs : currentArgs.length;
        
        System.arraycopy(currentArgs, 0, actualArgs, 0, argsToCopy);
        
        for (int i = argsToCopy; i < actualArgs.length; i++) {
            actualArgs[i] = new ArgumentMetadata(CALFeatureName.getArgumentFeatureName(i), metadata.getLocale());
        }
        
        metadata.setArguments(actualArgs);
        return true;
    }
    
    /**
     * Validates the given metadata object by synchronizing it with the given entity.
     * After calling this method the number of arguments stored by the metadata will
     * match the number of arguments of the entity.
     * @param entity the entity to validate the metadata with
     * @param metadata the metadata to validate
     * @return true if the metadata is valid, false otherwise
     */
    public static boolean isMetadataValid(GemEntity entity, InstanceMethodMetadata metadata) {
        return isMetadataValid(entity.getFunctionalAgent(), metadata);
    }
    
    /**
     * Validates the given metadata object by synchronizing it with the given entity.
     * After calling this method the number of arguments stored by the metadata will
     * match the number of arguments of the entity.
     * @param entity the entity to validate the metadata with
     * @param metadata the metadata to validate
     * @return true if the metadata is valid, false otherwise
     */
    private static boolean isMetadataValid(FunctionalAgent entity, InstanceMethodMetadata metadata) {
        
        int numArgs = entity.getTypeExpr().getArity();
        
        ArgumentMetadata[] actualArgs = new ArgumentMetadata[numArgs];
        ArgumentMetadata[] currentArgs = metadata.getArguments();
        
        if (actualArgs.length == currentArgs.length) {
            return true;
        }
        
        int argsToCopy = currentArgs.length > actualArgs.length ? numArgs : currentArgs.length;
        
        System.arraycopy(currentArgs, 0, actualArgs, 0, argsToCopy);
        
        for (int i = argsToCopy; i < actualArgs.length; i++) {
            actualArgs[i] = new ArgumentMetadata(CALFeatureName.getArgumentFeatureName(i), metadata.getLocale());
        }
        
        metadata.setArguments(actualArgs);
        return true;
    }
    
    /**
     * Validates the given metadata object by synchronizing it with the given collector.
     * After calling this method the number of arguments stored by the metadata will
     * match the number of arguments of the collector. The stored argument metadata will
     * also be updated to the latest metadata stored by the collectors target argument inputs.
     * @param collector the collector to validate the metadata with
     * @param metadata the metadata to validate
     * @return True if the metadata was successfully synchronized with the collector
     */
    public static boolean isMetadataValid(CollectorGem collector, FunctionMetadata metadata) {
        
        // For our purposes, if the collecting part of a collector is not connected,
        // then that means that the collector has no arguments.
        if (!collector.getCollectingPart().isConnected()) {
            metadata.setArguments(new ArgumentMetadata[0]);
            return true;
        }
        
        List<PartInput> inputs = collector.getTargetInputs();
        
        ArgumentMetadata[] actualArgs = new ArgumentMetadata[inputs.size()];
        
        for (int i = 0; i < actualArgs.length; i++) {
            actualArgs[i] = inputs.get(i).getDesignMetadata();
        }
        
        metadata.setArguments(actualArgs);
        return true;
    }
    
    /**
     * If the given address points to an entity with a TypeExpr, then this method will
     * return the String form of the TypeExpr's type pieces. An empty array is returned
     * if this address does not point to an entity with a TypeExpr.
     * @param owner the navigator owner
     * @param address the address to get type strings for
     * @param namingPolicy the naming policy to use when toString'ing the TypeExpr
     * @return string array with the strings for the type pieces or an empty array
     * if the address does not point to an entity with a TypeExpr
     */
    public static String[] getTypeStrings(NavFrameOwner owner, NavAddress address, ScopedEntityNamingPolicy namingPolicy) {
        
        NavAddressMethod method = address.getMethod();
        
        if (method == NavAddress.CLASS_METHOD_METHOD ||
            method == NavAddress.FUNCTION_METHOD ||
            method == NavAddress.DATA_CONSTRUCTOR_METHOD) {

            GemEntity entity = owner.getPerspective().getWorkspace().getGemEntity(address.toFeatureName().toQualifiedName());
            TypeExpr[] typePieces = entity.getTypeExpr().getTypePieces();
            String[] typeStrings = new String[typePieces.length];            
            PolymorphicVarContext polymorphicVarContext = PolymorphicVarContext.make();
            
            for (int i = 0; i < typePieces.length; i++) {
                typeStrings[i] = typePieces[i].toString(polymorphicVarContext, namingPolicy);
            }
            
            return typeStrings;
            
        } else if (method == NavAddress.INSTANCE_METHOD_METHOD) {
            
            CALFeatureName featureName = address.toFeatureName();
            String methodName = featureName.toInstanceMethodName();

            ClassInstance instance = owner.getPerspective().getMetaModule(featureName.toModuleName()).getTypeInfo().getClassInstance(featureName.toInstanceIdentifier());
            
            TypeExpr[] typePieces = instance.getInstanceMethodType(methodName).getTypePieces();
            String[] typeStrings = new String[typePieces.length];            
            PolymorphicVarContext polymorphicVarContext = PolymorphicVarContext.make();
            
            for (int i = 0; i < typePieces.length; i++) {
                typeStrings[i] = typePieces[i].toString(polymorphicVarContext, namingPolicy);
            }
            
            return typeStrings;
                
        } else if (method == NavAddress.COLLECTOR_METHOD) {
            
            CollectorGem collector = owner.getCollector(address.getBase());
            
            if (collector == null || !collector.getCollectingPart().isConnected()) {
                return new String[0];
            }
            
            List<PartInput> inputs = collector.getTargetInputs();
            String[] typeStrings = new String[inputs.size() + 1];
            PolymorphicVarContext polymorphicVarContext = PolymorphicVarContext.make();
            
            for (int i = 0, length = typeStrings.length - 1; i < length; i++) {
                typeStrings[i] = inputs.get(i).getType().toString(polymorphicVarContext, namingPolicy);
            }
            
            typeStrings[typeStrings.length - 1] = collector.getCollectingPart().getType().toString(polymorphicVarContext, namingPolicy);

            return typeStrings;
        }
        
        return new String[0];
    }
    
    /**
     * Determines the best display name to use for the entity the address points to.
     * If the entity is a scoped entity the QUALIFIED naming policy is used.
     * @param owner the navigator owner
     * @param address the address to get display text for
     * @return the display text for this address
     */
    public static String getDisplayText(NavFrameOwner owner, NavAddress address) {
        return getDisplayText(owner, address, ScopedEntityNamingPolicy.FULLY_QUALIFIED);
    }
    
    /**
     * Determines the best display name to use for the entity the address points to.
     * If the entity is a scoped entity then the provided naming policy will be used.
     * @param owner the navigator owner
     * @param address the address to get display text for
     * @param namingPolicy the naming policy
     * @return the display text for this address
     */
    public static String getDisplayText(NavFrameOwner owner, NavAddress address, ScopedEntityNamingPolicy namingPolicy) {

        CALFeatureMetadata metadata = getMetadata(owner, address);
        NavAddressMethod method = address.getMethod();

        // If this feature has no metadata, then there is nothing
        // sensible that we can say about it.  Just return the
        // raw text.
        if (metadata == null) {
            return address.getBase();
        }
        
        if (address.getParameter(NavAddress.ARGUMENT_PARAMETER) != null) {
            
            int argNum = Integer.parseInt(address.getParameter(NavAddress.ARGUMENT_PARAMETER));
            
            if (address.getMethod() == NavAddress.INSTANCE_METHOD_METHOD) {
                NavAddress parentAddress = NavAddress.getAddress(address.toFeatureName()); // this strips out the &argument=n parameter
                CALFeatureMetadata parentFeatureMetadata = getMetadata(owner, parentAddress);
                InstanceMethodMetadata parentMetadata = (InstanceMethodMetadata)parentFeatureMetadata;
                ArgumentMetadata[] argMetadata = parentMetadata.getArguments();
                adjustArgumentNames(owner, address, argMetadata);
                
                return argMetadata[argNum].getDisplayName();
                
            } else {
                FunctionalAgentMetadata parentMetadata = (FunctionalAgentMetadata)getMetadata(owner, address.withAllStripped());
                ArgumentMetadata[] argMetadata = parentMetadata.getArguments();
                adjustArgumentNames(owner, address.withAllStripped(), argMetadata);
                
                return argMetadata[argNum].getDisplayName();
            }
            
        } else if (method == NavAddress.MODULE_METHOD) {
            
            String vault = address.getParameter(NavAddress.VAULT_PARAMETER);
            String name = metadata.getDisplayName() != null ? metadata.getDisplayName() : address.getBase();
            
            if (vault == null) {
                return name; 
                
            } else if (vault.equals(NavAddress.TYPE_VAULT_VALUE)) {
                return name + NavigatorMessages.getString("NAV_ModuleTypeVault");

            } else if (vault.equals(NavAddress.INSTANCE_VAULT_VALUE)) {
                return name + NavigatorMessages.getString("NAV_ModuleInstanceVault");
                
            } else if (vault.equals(NavAddress.CLASS_VAULT_VALUE)) {
                return name + NavigatorMessages.getString("NAV_ModuleClassVault");
                
            } else if (vault.equals(NavAddress.FUNCTION_VAULT_VALUE)) {
                return name + NavigatorMessages.getString("NAV_ModuleFunctionVault");            
            }
            
        } else if (method == NavAddress.COLLECTOR_METHOD) {
            
            // Special case collectors since the CAL name of the metadata always
            // returns the default collector name.
            
            CollectorGem collector = owner.getCollector(address.getBase());
            
            if (metadata.getDisplayName() != null) {
                return metadata.getDisplayName();
            }

            return collector != null ? collector.getUnqualifiedName() : "Unknown Collector";
            
        } else if (method == NavAddress.CLASS_INSTANCE_METHOD) {

            ClassInstance instance = owner.getPerspective().getWorkspace().getClassInstance(address.toFeatureName());

            if (metadata.getDisplayName() != null) {
                return metadata.getDisplayName();
            
            } else if (instance == null) {
                return address.toString();
            
            } else if (namingPolicy != null) {
                return instance.getNameWithContext(namingPolicy);
            
            } else {
                return instance.getNameWithContext();
            }
            
        } else if (method == NavAddress.INSTANCE_METHOD_METHOD) {

            String methodName = address.toFeatureName().toInstanceMethodName();
            
            if (metadata.getDisplayName() != null) {
                return metadata.getDisplayName();
            } else {
                return methodName;
            }
            
        } else {
            
            CALFeatureName featureName = metadata.getFeatureName();
            ScopedEntity entity = owner.getPerspective().getWorkspace().getScopedEntity(featureName); 

            if (metadata.getDisplayName() != null) {
                return metadata.getDisplayName();
                
            } else if (entity == null) {
                return address.toString();
                
            } else if (namingPolicy != null) {
                return entity.getAdaptedName(namingPolicy); 
                
            } else {
                return entity.getName().getQualifiedName();
            }
        }
        
        return address.toString();
    }

    /**
     * Assigns default argument names to the display names of the given argument metadata. If an argument has
     * no name this will either assign the default argument name or try to use another name as a base name.
     * The other name may be a name from CAL code or the original name of an argument. If the argument already
     * has a display name, then that name will not be changed.
     * @param owner the navigator owner
     * @param address the address of the entity the arguments belong to
     * @param arguments the argument metadata objects to adjust
     */
    public static void adjustArgumentNames(NavFrameOwner owner, NavAddress address, ArgumentMetadata[] arguments) {
        
        if (address.getMethod() == NavAddress.COLLECTOR_METHOD) {
            CollectorGem collector = owner.getCollector(address.getBase());
            adjustArgumentNames(collector, arguments);
            
        } else if (address.getMethod() == NavAddress.INSTANCE_METHOD_METHOD) {
            CALFeatureName featureName = address.toFeatureName();
            ModuleName typeClassModuleName = featureName.toInstanceIdentifier().getTypeClassName().getModuleName();
            String methodName = featureName.toInstanceMethodName();
            
            CALWorkspace workspace = owner.getPerspective().getWorkspace();
            GemEntity entity = workspace.getGemEntity(QualifiedName.make(typeClassModuleName, methodName));
            ClassInstance instance = workspace.getClassInstance(CALFeatureName.getClassInstanceFeatureName(featureName.toInstanceIdentifier(), featureName.toModuleName()));
            
            adjustArgumentNames(entity, instance.getMethodCALDocComment(methodName), arguments);
            
        } else {
            GemEntity entity = owner.getPerspective().getWorkspace().getGemEntity(address.toFeatureName().toQualifiedName());
            adjustArgumentNames(entity, arguments);
        }
    }

    /**
     * @see #adjustArgumentNames(NavFrameOwner, NavAddress, ArgumentMetadata[])
     */
    public static void adjustArgumentNames(CollectorGem collector, ArgumentMetadata[] arguments) {
        
        if (!collector.getCollectingPart().isConnected()) {
            return;
        }
        
        List<PartInput> inputs = collector.getTargetInputs();
        String[] originalNames = new String[inputs.size()];
        
        for (int i = 0; i < originalNames.length; i++) {
            originalNames[i] = inputs.get(i).getArgumentName().getCompositeName();
        }
        
        adjustArgumentNames(originalNames, false, null, arguments);
    }
    
    /**
     * @see #adjustArgumentNames(NavFrameOwner, NavAddress, ArgumentMetadata[])
     */
    public static void adjustArgumentNames(GemEntity entity, ArgumentMetadata[] arguments) {
        adjustArgumentNames(entity.getFunctionalAgent(), arguments);
    }
    
    /**
     * @see #adjustArgumentNames(NavFrameOwner, NavAddress, ArgumentMetadata[])
     */
    public static void adjustArgumentNames(GemEntity entity, CALDocComment caldoc, ArgumentMetadata[] arguments) {
        adjustArgumentNames(entity.getFunctionalAgent(), caldoc, arguments);
    }
    
    /**
     * @see #adjustArgumentNames(NavFrameOwner, NavAddress, ArgumentMetadata[])
     */
    public static void adjustArgumentNames(FunctionalAgent entity, ArgumentMetadata[] arguments) {
        adjustArgumentNames(entity, entity.getCALDocComment(), arguments);
    }
    
    /**
     * @see #adjustArgumentNames(NavFrameOwner, NavAddress, ArgumentMetadata[])
     */
    public static void adjustArgumentNames(FunctionalAgent entity, CALDocComment caldoc, ArgumentMetadata[] arguments) {
        
        int numNames = entity.getNArgumentNames();
        String[] codeNames = new String[numNames];
        
        for (int i = 0; i < numNames; i++) {
            codeNames[i] = entity.getArgumentName(i);
        }
        
        adjustArgumentNames(codeNames, true, caldoc, arguments);
    }
    
    /**
     * Assigns default argument display names to the arguments that do not have a display
     * name. Also disambiguates display names of the arguments.
     * @param originalNames original names that can be used if there's no existing name
     * @param originalNamesFromEntity whether the original names came from the gem entity
     * @param caldoc the CALDoc that may contain '@arg' blocks declaring argument names, or null if there is none.
     * @param arguments array of argument metadata for which to adjust the display names
     */
    private static void adjustArgumentNames(String[] originalNames, boolean originalNamesFromEntity, CALDocComment caldoc, ArgumentMetadata[] arguments) {
        
        Map<String, Integer> argNameToFrequencyMap = new HashMap<String, Integer>();
        Map<String, Integer> argNameToSuffixMap = new HashMap<String, Integer>();

        // Assign the original and default names and count the frequency of each argument name.
        for (int i = 0; i < arguments.length; i++) {

            String argName = arguments[i].getDisplayName();
            
            if (argName == null) {
                
                String nameFromCALDoc = null;
                if (caldoc != null && i < caldoc.getNArgBlocks()) {
                    FieldName argNameAsFieldName = caldoc.getNthArgBlock(i).getArgName();
                    if (argNameAsFieldName instanceof FieldName.Textual) {
                        nameFromCALDoc = argNameAsFieldName.getCalSourceForm();
                    }
                }
                
                String originalName = null;
                if (i < originalNames.length) {
                    originalName = originalNames[i];
                }
                
                if (originalNamesFromEntity) {
                    // if the CALDoc name is present, we will use it instead of the name from the gem entity
                    // since the CALDoc name is specified by the user, where the name from the gem entity
                    // may have come from an automatically extracted parameter name for a foreign function
                    
                    if (nameFromCALDoc != null) {
                        argName = nameFromCALDoc;
                    } else if (originalName != null) {
                        argName = originalName;
                    } else {
                        argName = ArgumentMetadata.DEFAULT_ARGUMENT_NAME;
                    }
                } else {
                    // since the original names do not come from the gem entity,
                    // they take precedence over the names in the CALDoc
                    
                    if (originalName != null) {
                        argName = originalName;
                    } else if (nameFromCALDoc != null) {
                        argName = nameFromCALDoc;
                    } else {
                        argName = ArgumentMetadata.DEFAULT_ARGUMENT_NAME;
                    }
                }

                arguments[i].setDisplayName(argName);
            }
                        
            Integer frequency = argNameToFrequencyMap.get(argName);
            frequency = frequency != null ? Integer.valueOf(frequency.intValue() + 1) : Integer.valueOf(1);
            argNameToFrequencyMap.put(argName, frequency);
        }
        
        // Disambiguate the names that occur more than once.
        for (final ArgumentMetadata argumentMetadata : arguments) {

            String argName = argumentMetadata.getDisplayName();
            int frequency = argNameToFrequencyMap.get(argName).intValue();
            
            if (frequency > 1) {

                Integer suffix = argNameToSuffixMap.get(argName);
                suffix = suffix != null ? Integer.valueOf(suffix.intValue() + 1) : Integer.valueOf(1);
                argNameToSuffixMap.put(argName, suffix);
                
                argName = argName + "_" + suffix;
                argumentMetadata.setDisplayName(argName);
            }
        }
    }
}
