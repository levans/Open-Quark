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
 * DeserializationHelper.java
 * Creation date: Jan 13, 2006.
 * By: Edward Lam
 */
package org.openquark.cal.compiler;

import java.util.Map;

import org.openquark.cal.compiler.CompilerMessage.Identifier;
import org.openquark.cal.internal.compiler.ForeignEntityResolver;
import org.openquark.cal.internal.compiler.StandaloneRuntimeForeignClassResolver;


/**
 * Warning- this class should only be used by the CAL compiler implementation. It is not part of the
 * external API of the CAL platform.
 * <p>
 * A helper class containing utility methods used in module deserialization.
 * @author Edward Lam
 */
public abstract class DeserializationHelper {
    
    /** Map from class name to class for method classForName() */
    static final Map<String, Class<?>> nameToClassMap = StandaloneRuntimeForeignClassResolver.nameToClassMap;
    
    /*
     * Not intended to be instantiated.
     */
    private DeserializationHelper() {
    }

    /**
     * Check the schema from the serialized record against the current schema.
     * @param savedSchema the schema of the saved record.
     * @param currentSchema the currently-known schema number.
     * @param moduleName the name of the module being deserialized.
     * @param internalName the internal name of the record being deserialized.
     * @param msgLogger the logger to which to log deserialization messages.
     * @throws CompilerMessage.AbortCompilation if the saved schema is incompatible with the current schema.
     * For the time being, this is used to abort loading the rest of the module.
     */
    public static void checkSerializationSchema(final int savedSchema, final int currentSchema, final ModuleName moduleName, final String internalName, final CompilerMessageLogger msgLogger) {
    
        if (savedSchema > currentSchema) {
            final SourcePosition startSourcePosition = new SourcePosition(0, 0, moduleName.toSourceText());
            final SourcePosition endSourcePosition = new SourcePosition(0, 1, moduleName.toSourceText());
            final SourceRange sourceRange = new SourceRange(startSourcePosition, endSourcePosition);
            final MessageKind messageKind = new MessageKind.Error.DeserializedIncompatibleSchema(savedSchema, currentSchema, internalName);
            msgLogger.logMessage(new CompilerMessage(sourceRange, messageKind));
        }
    }
    
    /**
     * Constructs a ForeignEntityProvider encapsulating the resolution logic of {@link #classForName}.
     * @param className
     * @param foreignClassLoader the classloader to use to resolve foreign classes.
     * @param recordLoadName the name of the record being loaded.
     * This will be used if an error occurs finding the java class.
     * @param associatedEntity the name of the associated CAL entity. Can be null an associated entity cannot be provided.
     * @param msgLogger the logger to which to log deserialization messages.
     * @return a ForeignEntityProvider instance.
     */
    static ForeignEntityProvider<Class<?>> classProviderForName(final String className, final ClassLoader foreignClassLoader, final String recordLoadName, final Identifier associatedEntity, final CompilerMessageLogger msgLogger) {
        return ForeignEntityProvider.make(msgLogger, new ForeignEntityProvider.Resolver<Class<?>>(className) {
            @Override
            public Class<?> resolve(final ForeignEntityProvider.MessageHandler messageHandler) throws UnableToResolveForeignEntityException {
                return classForName(className, foreignClassLoader, recordLoadName, associatedEntity, messageHandler);
            }
        });
    }

    /**
     * @param associatedEntity the name of the associated CAL entity. Can be null an associated entity cannot be provided.
     * @return null if associatedEntity is null.
     * Otherwise, a source range for the module from the provided name, consisting of source name only (no positions).
     */
    private static SourceRange getSourceRangeForAssociatedEntity(final CompilerMessage.Identifier associatedEntity) {
        if (associatedEntity == null) {
            return null;
        }
        
        return new SourceRange(associatedEntity.getModuleName().toSourceText());
    }
    
    /**
     * Load a class based on the given class name.
     * @param className
     * @param foreignClassLoader the classloader to use to resolve foreign classes.
     * @param recordLoadName the name of the record being loaded.
     * This will be used if an error occurs finding the java class.
     * @param associatedEntity the name of the associated CAL entity. Can be null an associated entity cannot be provided.
     * @param messageHandler the message handler for handling deserialization messages.
     * @return the class corresponding to the given name, or null if there is an error in class resolution.
     */
    static final Class<?> classForName (final String className, final ClassLoader foreignClassLoader, final String recordLoadName, final CompilerMessage.Identifier associatedEntity, final ForeignEntityProvider.MessageHandler messageHandler) throws UnableToResolveForeignEntityException {
        
        final Class<?> primitiveClass = nameToClassMap.get(className);
        if (primitiveClass != null) {
            return primitiveClass;
        }
        
        final ForeignEntityResolver.ResolutionResult<Class<?>> classResolution = ForeignEntityResolver.resolveClass(ForeignEntityResolver.javaSourceReferenceNameToJvmInternalName(className), foreignClassLoader);
        final ForeignEntityResolver.ResolutionStatus resolutionStatus = classResolution.getStatus();
        
        final Class<?> foreignType = classResolution.getResolvedEntity();
        
        if (resolutionStatus == ForeignEntityResolver.ResolutionStatus.SUCCESS) {
            // the resolution was successful, so no need to report errors
            
        } else {
            final SourceRange sourceRangeForAssociatedEntity = getSourceRangeForAssociatedEntity(associatedEntity);
            
            Throwable throwable = classResolution.getThrowable();
            Exception exception = throwable instanceof Exception ? (Exception)throwable : null;
            
            if (resolutionStatus == ForeignEntityResolver.ResolutionStatus.NO_SUCH_ENTITY) {
                // The Java class {qualifiedClassName} was not found while loading {recordLoadName}.
                messageHandler.handleMessage(new CompilerMessage(
                    sourceRangeForAssociatedEntity,
                    associatedEntity,
                    new MessageKind.Error.JavaClassNotFoundWhileLoading(className, recordLoadName), exception));
                
            } else if (resolutionStatus == ForeignEntityResolver.ResolutionStatus.DEPENDEE_CLASS_NOT_FOUND) {
                // The Java class {notFoundClass} was not found.  This class is required by {externalName}.
                messageHandler.handleMessage(new CompilerMessage(
                    sourceRangeForAssociatedEntity,
                    associatedEntity,
                    new MessageKind.Error.DependeeJavaClassNotFound(classResolution.getAssociatedMessage(), className), exception));
                
            } else if (resolutionStatus == ForeignEntityResolver.ResolutionStatus.CANNOT_LOAD_CLASS) {
                // The definition of Java class {externalName} could not be loaded.
                messageHandler.handleMessage(new CompilerMessage(
                    sourceRangeForAssociatedEntity,
                    associatedEntity,
                    new MessageKind.Error.JavaClassDefinitionCouldNotBeLoaded(className), exception));
                
            } else if (resolutionStatus == ForeignEntityResolver.ResolutionStatus.CANNOT_INITIALIZE_CLASS) {
                // The Java class {qualifiedClassName} could not be initialized.
                messageHandler.handleMessage(new CompilerMessage(
                    sourceRangeForAssociatedEntity,
                    associatedEntity,
                    new MessageKind.Error.JavaClassCouldNotBeInitialized(className), exception));
                
            } else if (resolutionStatus == ForeignEntityResolver.ResolutionStatus.LINKAGE_ERROR) {
                // The java class {qualifiedClassName} was found, but there were problems with using it.
                // Class:   {LinkageError.class}
                // Message: {e.getMessage()}
                messageHandler.handleMessage(new CompilerMessage(
                    sourceRangeForAssociatedEntity,
                    associatedEntity,
                    new MessageKind.Error.ProblemsUsingJavaClass(className, (LinkageError)throwable)));

            } else if (resolutionStatus == ForeignEntityResolver.ResolutionStatus.NOT_ACCESSIBLE) {
                //"The Java type ''{0}'' is not accessible. It does not have public scope or is in an unnamed package."
                //the parameter {0} will be replaced by "class java.lang.Foo" or "interface java.lang.Foo" as appropriate.
                messageHandler.handleMessage(new CompilerMessage(
                    sourceRangeForAssociatedEntity,
                    associatedEntity,
                    new MessageKind.Error.ExternalClassNotAccessible(foreignType), exception));
                
            } else {
                // Some other unexpected status
                throw new IllegalStateException("Unexpected status: " + resolutionStatus);
            }
        }
        
        return foreignType;
    }
}
