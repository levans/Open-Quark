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
 * ClassInfo.java
 * Creation date: Jan 4, 2006.
 * By: Edward Lam
 */
package org.openquark.util;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;


/**
 * A helper class to return class-related information based on the current execution environment.
 * @author Edward Lam
 */
public final class ClassInfo {

    /** Singleton finder instance. */
    private static final CallingClassFinder FINDER = new CallingClassFinder();

    /**
     * A class to determine the class of a method in the current execution stack.
     * @author Edward Lam
     */
    private static class CallingClassFinder extends SecurityManager {

        /**
         * {@inheritDoc}
         * Calls superclass but exposes as public visibility.
         */
        @Override
        public Class<?>[] getClassContext() {
            return super.getClassContext();
        }

        /**
         * Get a class in the currently executing call chain.
         * @param callDepth the call depth.
         * @return the class callDepth-levels higher in the currently-executing call chain.
         * 0 == the class of the method calling this method.
         * 1 == the class of the method one level higher.
         */
        public Class<?> getCallingClass(int callDepth) {
            Class<?> stack[] = getClassContext();

            // stack[0] == class for method getCallingClass(callDepth)  ie. CallingClassFinder.
            // stack[1] == class of method calling this method
            // ...
            return stack[callDepth + 1];
        }
    }

    /**
     * Constructor for a ClassInfo.
     * Not intended to be instantiated.
     */
    private ClassInfo() {
    }

    /**
     * Get a class in the currently executing call chain.
     * @param callDepth the call depth.
     * @return the class callDepth-levels higher in the currently-executing call chain.
     * 0 == the class of the method calling this method.
     * 1 == the class of the method one level higher.
     */
    public static Class<?> getCallingClass(int callDepth) {
        // pass in 0: this method's class.
        // pass in 1: the caller's class.
        return FINDER.getCallingClass(callDepth + 1);
    }

    /**
     * @return the classes in the currently executing call chain.
     *   Note that this will include the ClassInfo class itself.
     */
    public static Class<?>[] getClassContext() {
        return FINDER.getClassContext();
    }

    /**
     * Load a class.
     * If there is a context classloader, this is first used to attempt to load the class.
     * If this fails then the caller's classloader is used.
     * 
     * @param name the fully-qualified name of the class.
     * @return the corresponding class.
     * @throws ClassNotFoundException if the class could not be found.
     */

    public static Class<?> loadClass(String name) throws ClassNotFoundException {
        // Note about implementation: this is how antlr.Utils.loadClass() does it,
        //  except that it calls the one-arg Class.forName().
        try {
            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            if (contextClassLoader != null) {
                return contextClassLoader.loadClass(name);
            }
            // fall through
            
        } catch (Exception e) {
            // fall through
        }

        return Class.forName(name, true, getCallingClass(1).getClassLoader());
    }

    /**
     * Get a resource by name.
     * If there is a context classloader, this is first used to attempt to load the resource.
     * If this fails then the classloader of the calling class will be used.
     *
     * @param name the name of the resource
     * @return the URL to that resource, or null if that resource could not be found.
     */
    public static URL getResource(final String name) {
        try {
            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            if (contextClassLoader != null) {
                URL resource = contextClassLoader.getResource(name);
                if (resource != null) {
                    return resource;
                }
            }
            // fall through

        } catch (Exception e) {
            // fall through
        }
        
        return getCallingClass(1).getClassLoader().getResource(name);
    }

    /**
     * Get all resources with a given name.
     * The returned enumeration will have all resources available to both the caller's classloader and the context classloader (if any).
     *
     * @param name the name of the resource.
     * @return the URLs for the found resources.
     * @throws IOException
     */
    public static Enumeration<URL> getResources(final String name) throws IOException {
        
        // Use a set to avoid duplicates.
        //  Might as well make it an ordered set to make things potentially easier for clients.
        Set<URL> resourceSet = new LinkedHashSet<URL>();
        try {
            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            if (contextClassLoader != null) {
                resourceSet.addAll(Collections.list(contextClassLoader.getResources(name)));
            }
            // fall through

        } catch (Exception e) {
            // fall through
        }

        resourceSet.addAll(Collections.list(getCallingClass(1).getClassLoader().getResources(name)));
        return Collections.enumeration(resourceSet);
    }
}
