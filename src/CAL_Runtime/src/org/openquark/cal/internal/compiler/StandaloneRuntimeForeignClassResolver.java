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
 * StandaloneRuntimeForeignClassResolver.java
 * Created: Oct 16, 2007
 * By: Joseph Wong
 */

package org.openquark.cal.internal.compiler;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * This class provides utility methods for resolving foreign classes in the standalone runtime environment.
 *
 * @author Joseph Wong
 */
public final class StandaloneRuntimeForeignClassResolver {

    /** Map from class name to class for method classForName() */
    public static final Map<String, Class<?>> nameToClassMap = new HashMap<String, Class<?>>();
    static {
        nameToClassMap.put("char", char.class);
        nameToClassMap.put("boolean", boolean.class);
        nameToClassMap.put("int", int.class);
        nameToClassMap.put("double", double.class);
        nameToClassMap.put("byte", byte.class);
        nameToClassMap.put("short", short.class);
        nameToClassMap.put("float", float.class);
        nameToClassMap.put("long", long.class);
        nameToClassMap.put("void", void.class);
    }

    /**
     * This exception class represents a failure to resolve a class for a CAL foreign type at
     * runtime (e.g. when running with a standalone JAR).
     *
     * @author Joseph Wong
     */
    public static final class UnableToResolveForeignEntityException extends Exception {
        
        private static final long serialVersionUID = -2097574651595819019L;

        /**
         * Constructs an instance of this exception.
         * @param resolution the bad resolution. Cannot be null.
         */
        UnableToResolveForeignEntityException(final String className, final ForeignEntityResolver.ResolutionResult<Class<?>> resolution) {
            super(makeMessage(className, resolution), resolution.getThrowable());
        }

        private static String makeMessage(final String className, final ForeignEntityResolver.ResolutionResult<Class<?>> classResolution) {
            
            final ForeignEntityResolver.ResolutionStatus resolutionStatus = classResolution.getStatus();
            
            if (resolutionStatus == ForeignEntityResolver.ResolutionStatus.NO_SUCH_ENTITY) {
                return MessageFormat.format("The Java class {0} was not found while loading foreign type.", className);
                
            } else if (resolutionStatus == ForeignEntityResolver.ResolutionStatus.DEPENDEE_CLASS_NOT_FOUND) {
                return MessageFormat.format("The Java class {0} was not found.  This class is required by {1}.", classResolution.getAssociatedMessage(), className);
                
            } else if (resolutionStatus == ForeignEntityResolver.ResolutionStatus.CANNOT_LOAD_CLASS) {
                return MessageFormat.format("The definition of Java class {0} could not be loaded.", className);
                
            } else if (resolutionStatus == ForeignEntityResolver.ResolutionStatus.CANNOT_INITIALIZE_CLASS) {
                return MessageFormat.format("The Java class {0} could not be initialized.", className);
                
            } else if (resolutionStatus == ForeignEntityResolver.ResolutionStatus.LINKAGE_ERROR) {
                return MessageFormat.format(
                    "The java class {0} was found, but there were problems with using it.\n" +
                    "Class:   {1}\n" +
                    "Message: {2}",
                    className, classResolution.getThrowable().getClass(), classResolution.getThrowable().getMessage());

            } else if (resolutionStatus == ForeignEntityResolver.ResolutionStatus.NOT_ACCESSIBLE) {
                return MessageFormat.format("The Java type \"{0}\" is not accessible. It does not have public scope.", classResolution.getResolvedEntity());
                
            } else {
                // Some other unexpected status
                return "Unexpected status: " + resolutionStatus;
            }
        }
    }
    
    /** Private constructor. */
    private StandaloneRuntimeForeignClassResolver() {}
    
    /**
     * Resolve the Java class for a CAL foreign type based on the given class name.
     * 
     * @param className the name of the class.
     * @param foreignClassLoader the classloader to use to resolve foreign classes.
     * @return the class corresponding to the given name, or null if there is an error in class resolution.
     */
    public static final Class<?> resolveClassForForeignType(final String className, final ClassLoader foreignClassLoader) throws UnableToResolveForeignEntityException {
        
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
            // throw an exception on a bad resolution attempt
            throw new UnableToResolveForeignEntityException(className, classResolution);
        }
        
        return foreignType;
    }

}
