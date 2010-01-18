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
 * ForeignEntityProvider.java
 * Created: May 18, 2007
 * By: Joseph Wong
 */

package org.openquark.cal.compiler;

import java.lang.reflect.AccessibleObject;

import org.openquark.cal.internal.runtime.lecc.LECCMachineConfiguration;

/**
 * A ForeignEntityProvider represents a "thunk" whose {@link #get} method returns the same object on
 * each invocation. This object corresponds to a CAL foreign type or foreign function, and can be a
 * Class object, or a Method, Field, or Constructor object.
 * <p>
 * 
 * The main purpose of this class (and its subclasses) is to facilitate the <em>lazy loading</em> of
 * classes which are serialized as part of {@link ForeignTypeInfo} and {@link ForeignFunctionInfo}.
 * By default, such references to classes, methods, fields and constructors are not loaded and resolved until
 * they are needed (this is the lazy mode). By changing the machine configuration, it is possible for these entities
 * to be loaded eagerly at deserialization time.
 * <p>
 * 
 * The lazy mode makes it possible to write and deploy CAL modules where
 * some of the foreign dependencies are not available at runtime. As long as the associated foreign types and foreign
 * functions are not accessed at runtime, no errors will be reported. Running in lazy mode may also bring about
 * a minor performance improvement in the time required to initialize a CAL workspace (as classloading of foreign
 * classes are deferred). Strict mode is mainly meant for debugging and for development time, when it is useful
 * to catch resolution failures early.
 * <p>
 * 
 * The ForeignEntityProvider class itself is an abstract base class with two subclasses:
 * {@link org.openquark.cal.compiler.ForeignEntityProvider.Lazy Lazy} and
 * {@link org.openquark.cal.compiler.ForeignEntityProvider.Strict Strict}.
 * <ul>
 *   <li>
 *   The Lazy variant takes a {@link org.openquark.cal.compiler.ForeignEntityProvider.Resolver Resolver}
 *   on construction. The Resolver represents a piece of logic for resolving the underlying object from its specs.
 *   On the first call to {@link org.openquark.cal.compiler.ForeignEntityProvider.Lazy#get() get()}, the
 *   underlying object is resolved, and is then cached in a field. Subsequent calls would return the
 *   cached value.
 * 
 *   <li>
 *   The Strict variant, on the other hand, requires that the underlying object be available on construction, stores
 *   it directly, and returns it on calls to
 *   {@link org.openquark.cal.compiler.ForeignEntityProvider.Strict#get() get()}.
 * </ul>
 * This abstract base class is not meant to have other subclasses.
 * <p>
 * 
 * The inner class {@link org.openquark.cal.compiler.ForeignEntityProvider.MessageHandler MessageHandler}
 * is an abstract base class for providing different mechanisms for handling {@link CompilerMessage}s
 * that are generated during the resolution process, including error messages corresponding to resolution failure.
 * Once again, there are two variants
 * {@link org.openquark.cal.compiler.ForeignEntityProvider.MessageHandler.Lazy Lazy} and
 * {@link org.openquark.cal.compiler.ForeignEntityProvider.MessageHandler.Strict Strict}.
 * <ul>
 *   <li>
 *   The Lazy variant, which is used with the Lazy provider, turns a message of severity error or above into an
 *   {@link UnableToResolveForeignEntityException} and throws it.
 * 
 *   <li>
 *   The Strict variant, which is used with the Strict provider, handles a compiler
 *   message by immediately logging it to a {@link CompilerMessageLogger}.
 * </ul>
 * 
 * Typically, a client defines an implementation of
 * {@link org.openquark.cal.compiler.ForeignEntityProvider.Resolver Resolver}, and then calls the
 * static {@link #make} method to construct an instance of this class. Depending on the current
 * machine configuration, the {@link #make} method returns either a Strict or a Lazy variant. If it
 * is to return the Strict variant, it first uses the Resolver to resolve the underlying object.
 * Otherwise, it passes the Resolver directly to the Lazy variant.
 * 
 * @param <T> the type of entity provided by the provider - should be a {@link Class} or an {@link AccessibleObject}.
 * 
 * @author Joseph Wong
 */
abstract class ForeignEntityProvider<T> {
    
    /**
     * A debugging flag controlling whether each lazy entity resolution should be displayed.
     */
    private static final boolean DISPLAY_LAZY_LOADING_DEBUG_INFO = false;
    
    /**
     * The Resolver is an abstract base class representing a piece of logic for resolving an object from its specs.
     *
     * @author Joseph Wong
     */
    static abstract class Resolver<T> {
        
        /**
         * A display string to be returned by {@link #toString()}.
         */
        private final String displayString;
        
        /**
         * Constructor for this abstract base class.
         * @param displayString a display string to be returned by {@link #toString()}
         */
        Resolver(final String displayString) {
            if (displayString == null) {
                throw new NullPointerException();
            }
            this.displayString = displayString;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return displayString;
        }
        
        /**
         * Resolves an object from its specs.
         * @param messageHandler the MessageHandler to use for handling {@link CompilerMessage}s.
         * @return the resolved object.
         * @throws UnableToResolveForeignEntityException if this exception is thrown by the message handler, it is propagated.
         */
        abstract T resolve(ForeignEntityProvider.MessageHandler messageHandler) throws UnableToResolveForeignEntityException;
    }
    
    /**
     * An abstract base class for providing different mechanisms for handling {@link CompilerMessage}s
     * that are generated during the resolution process, including error messages corresponding to resolution failure.
     *
     * @author Joseph Wong
     */
    static abstract class MessageHandler {
        
        /**
         * A message handler that handles a compiler message by immediately logging it to a {@link CompilerMessageLogger}.
         * 
         * @author Joseph Wong
         */
        private static final class Strict extends ForeignEntityProvider.MessageHandler {
            
            /**
             * The message logger to which compiler messages will be logged.
             */
            private final CompilerMessageLogger logger;
    
            /**
             * Constructs an instance of this class.
             * @param logger the message logger to which compiler messages will be logged.
             */
            private Strict(final CompilerMessageLogger logger) {
                if (logger == null) {
                    throw new NullPointerException();
                }
                this.logger = logger;
            }
            
            /**
             * {@inheritDoc}
             */
            @Override
            void handleMessage(final CompilerMessage message) {
                logger.logMessage(message);
            }
        }
        
        /**
         * A message handler that turns a message of severity error or above into an
         * {@link UnableToResolveForeignEntityException} and throws it.
         * 
         * @author Joseph Wong
         */
        private static final class Lazy extends ForeignEntityProvider.MessageHandler {
            
            /** Private constructor. */
            private Lazy() {}
            
            /**
             * {@inheritDoc}
             */
            @Override
            void handleMessage(final CompilerMessage message) throws UnableToResolveForeignEntityException {
                if (message.getSeverity().compareTo(CompilerMessage.Severity.ERROR) >= 0) {
                    throw new UnableToResolveForeignEntityException(message);
                }
            }
        }
        
        /** Package-scoped constructor. */
        MessageHandler() {}
        
        /**
         * Handles a compiler message arising during the resolution of an object from its specs.
         * @param message the compiler message.
         * @throws UnableToResolveForeignEntityException an implementation can choose to throw this exception for error messages.
         */
        abstract void handleMessage(CompilerMessage message) throws UnableToResolveForeignEntityException;
    }

    /**
     * A ForeignEntityProvider implementation that requires the underlying object be available on
     * construction, stores it directly, and returns it on calls to {@link #get()}.
     * 
     * @author Joseph Wong
     */
    private static final class Strict<T> extends ForeignEntityProvider<T> {
        /**
         * The underlying object to be provided via {@link #get()}. Cannot be null.
         */
        private final T foreignEntity;
        
        /**
         * Constructs an instance of this class.
         * @param foreignEntity the underlying object to be provided via {@link #get()}. Cannot be null.
         */
        private Strict(final T foreignEntity) {
            
            if (foreignEntity == null) {
                throw new NullPointerException("foreignEntity must not be null");
            }
            
            this.foreignEntity = foreignEntity;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        T get() {
            return foreignEntity;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return String.valueOf(foreignEntity); // String.valueOf handles the case of null
        }
    }
    
    /**
     * A ForeignEntityProvider implementation that takes a
     * {@link org.openquark.cal.compiler.ForeignEntityProvider.Resolver Resolver} on construction.
     * The Resolver represents a piece of logic for resolving the underlying object from its specs.
     * On the first call to {@link #get()}, the underlying object is resolved, and is then cached
     * in a field. Subsequent calls would return the cached value.
     * 
     * @author Joseph Wong
     */
    private static final class Lazy<T> extends ForeignEntityProvider<T> {
        /**
         * A cache of the underlying object. Initially null. After the first call to {@link #get()}, this
         * field holds the resolved value (which cannot be null). This field must be accessed in a thread-safe manner.
         */
        private T foreignEntity;
        
        /**
         * The associated resolver. Initially non-null. This resolver is used in the first call to {@link #get()},
         * which nulls out this field in the process. If the resolver is null, it means that resolution has occurred,
         * and the value in {@link #foreignEntity} is the result of the resolution.
         */
        private ForeignEntityProvider.Resolver<T> resolver;
        
        /**
         * Constructs an instance of this class.
         * @param resolver the associated resolver. Cannot be null.
         */
        private Lazy(final ForeignEntityProvider.Resolver<T> resolver) {
            if (resolver == null) {
                throw new NullPointerException();
            }
            this.resolver = resolver;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        synchronized T get() throws UnableToResolveForeignEntityException {
            if (resolver != null) {
                foreignEntity = resolver.resolve(new MessageHandler.Lazy());
                resolver = null;
                
                // Now that we have resolved the entity, perform the checks
                if (foreignEntity == null) {
                    throw new NullPointerException("foreignEntity must not be null");
                }
                
                if (DISPLAY_LAZY_LOADING_DEBUG_INFO) {
                    System.out.println("Lazy-loaded foreign entity: " + foreignEntity);
                }
            }
            return foreignEntity;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public synchronized String toString() {
            if (resolver != null) {
                return resolver.toString();
            } else {
                return String.valueOf(foreignEntity); // String.valueOf handles the case of null
            }
        }
    }
    /**
     * Factory method for constructing an instance of ForeignEntityProvider suitable for the current mode (strict or lazy).
     * @param logger the message logger to use for logging compiler messages, if we are in strict mode.
     * @param resolver the resolver encapsulating the entity resolution logic.
     * @return a ForeignEntityProvider instance, or null if we are in strict mode and the resolved foreign entity is null.
     */
    static final <T> ForeignEntityProvider<T> make(final CompilerMessageLogger logger, final ForeignEntityProvider.Resolver<T> resolver) {
        
        if (LECCMachineConfiguration.useLazyForeignEntityLoading()) {
            // If we are in lazy mode, just wrap the resolver with a Lazy instance.
            return new Lazy<T>(resolver);
        } else {
            // Otherwise resolve the entity and return a Strict instance.
            return makeStrict(logger, resolver);
        }
    }

    /**
     * Factory method for constructing a strict instance of ForeignEntityProvider.
     * @param logger the message logger to use for logging compiler messages.
     * @param resolver the resolver encapsulating the entity resolution logic.
     * @return a ForeignEntityProvider instance, or null if the resolved foreign entity is null.
     */
    static <T> ForeignEntityProvider<T> makeStrict(final CompilerMessageLogger logger, final ForeignEntityProvider.Resolver<T> resolver) {
        // Attempt to resolve the entity.
        final T resolvedEntity;
        try {
            resolvedEntity = resolver.resolve(new MessageHandler.Strict(logger));
            
        } catch (final UnableToResolveForeignEntityException e) {
            // this should never happen with a logger-based MessageHandler
            final IllegalStateException illegalStateException = new IllegalStateException("An UnableToResolveForeignEntityException should not be thrown by a logger-based MessageHandler");
            illegalStateException.initCause(e);
            throw illegalStateException;
        }
        
        // If the resolved entity is null, we just return null. Otherwise, we wrap the entity with a Strict instance.
        if (resolvedEntity == null) {
            return null;
        } else {
            return new Strict<T>(resolvedEntity);
        }
    }
    
    /**
     * Factory method for constructing a strict instance of ForeignEntityProvider with a resolved foreign class.
     * @param foreignClass the resolved foreign class.
     * @return a ForeignEntityProvider instance encapsulating the resolved class.
     */
    static final ForeignEntityProvider<Class<?>> makeStrict(final Class<?> foreignClass) {
        return new Strict<Class<?>>(foreignClass);
    }
    
    /** Private constructor. */
    private ForeignEntityProvider() {}
    
    /**
     * Returns the resolved entity.
     * @return the resolved entity.
     * @throws UnableToResolveForeignEntityException an implementation can choose to throw this exception if the entity cannot be resolved.
     */
    abstract T get() throws UnableToResolveForeignEntityException;
    
    /**
     * {@inheritDoc}
     */
    /* Subclasses must declare explicit toString() implementations. */
    @Override
    public abstract String toString();
}