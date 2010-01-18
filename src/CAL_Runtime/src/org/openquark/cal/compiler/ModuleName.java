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
 * ModuleName.java
 * Creation date: Dec 27, 2006.
 * By: Joseph Wong
 */
package org.openquark.cal.compiler;

import java.util.Arrays;
import java.util.Comparator;

/**
 * This class abstracts a module name in CAL. A module name is composed of one or more components separated
 * by dots. For example, <code>Foo</code> and <code>Cal.Core.Prelude</code> are valid module names. Each component
 * must start with an uppercase letter.
 *
 * @author Joseph Wong
 */
public final class ModuleName implements Name, Comparable<ModuleName> {

    /**
     * The components of the module name. Cannot be null.
     */
    private final String[] components;
    
    /**
     * The hash code of the module name. It is initially zero, and is lazily calculated on the first
     * call to {@link #hashCode()}.
     */
    private int hash = 0;
    
    /**
     * A {@link Comparator} which compares {@link ModuleName}s by their fully-qualified form, comparing on
     * a component-by-component basis.
     *
     * @author Joseph Wong
     */
    public static final class FullyQualifiedComparator implements Comparator<ModuleName> {

        /** The singleton instance of this class. */
        public static final FullyQualifiedComparator INSTANCE = new FullyQualifiedComparator();
        
        /** Private constructor. This class is meant to be used by its singleton instance. */
        private FullyQualifiedComparator() {}

        /** {@inheritDoc} */
        public int compare(final ModuleName a, final ModuleName b) {
          
            final int nComponentsInA = a.getNComponents();
            final int nComponentsInB = b.getNComponents();
            final int nComponentsInBoth = Math.min(nComponentsInA, nComponentsInB);
            
            for (int i = 0; i < nComponentsInBoth; i++) {
                final int componentResult = a.getNthComponent(i).compareTo(b.getNthComponent(i));
                if (componentResult != 0) {
                    return componentResult;
                }
            }
            
            // So far, the common prefix match completely... so the longer one sorts after the shorter one
            
            if (nComponentsInA > nComponentsInBoth) {
                return 1;
            } else if (nComponentsInB > nComponentsInBoth) {
                return -1;
            } else {
                // nComponentsInA == nComponentsInB == nComponentsInBoth - the two names are completely equal
                return 0;
            }
        }
    }
    
    /**
     * Private constructor. This class is meant to be instantiated by factory methods.
     * @param components the components of the module name.
     */
    private ModuleName(final String[] components) {
        if (components == null) {
            throw new NullPointerException();
        }
        
        if (components.length == 0) {
            throw new IllegalArgumentException("A ModuleName must have > 0 components");
        }
        
        // no defensive cloning required because that's handled by the factory methods
        this.components = components;
    }
    
    /**
     * Returns the fully-qualified form of the given module name components. 
     * @param components an array of module name components.
     * @return the corresponding fully-qualified form.
     */
    private static String getFullyQualifiedString(final String[] components) {
        final StringBuilder buffer = new StringBuilder();
        final int n = components.length;
        for (int i = 0; i < n; i++) {
            if (i > 0) {
                buffer.append('.');
            }
            buffer.append(components[i]);
        }
        return buffer.toString();
    }
    
    /**
     * Factory method for constructing an instance of this class.
     * @param moduleName a module name. Cannot be null.
     * @return an instance of this class.
     */
    public static ModuleName make(final String moduleName) {
        
        if (moduleName == null) {
            throw new NullPointerException();
        }
        if (moduleName.length() == 0) {
            throw new IllegalArgumentException("The module name cannot be empty.");
        }
        
        final String[] components = moduleName.split("\\.");
        final int nComponents = components.length;
        
        for (int i = 0; i < nComponents; i++) {
            if (!LanguageInfo.isValidModuleNameComponent(components[i])) {
                throw new IllegalArgumentException("'" + components[i] + "', occurring in the module name '" + moduleName + "' is not a valid module name component.");
            }
        }
        
        return new ModuleName(components);
    }
    
    /**
     * Factory method for constructing an instance of this class which accepts nulls and invalid module name strings.
     * @param maybeModuleName a string which may or may not represent a valid module name. <em>Can</em> be null.
     * @return an instance of this class, or null if the argument is null or does not represent a valid module name.
     */
    public static ModuleName maybeMake(final String maybeModuleName) {
        
        if (maybeModuleName == null) {
            return null;
        }
        if (maybeModuleName.length() == 0) {
            return null;
        }

        final String[] components = maybeModuleName.split("\\.");
        final int nComponents = components.length;

        for (int i = 0; i < nComponents; i++) {
            if (!LanguageInfo.isValidModuleNameComponent(components[i])) {
                return null;
            }
        }

        return new ModuleName(components);
    }    
    
    /**
     * Factory method for constructing an instance of this class.
     * @param components an array of the components for the module name. Cannot be null.
     * @return an instance of this class.
     */
    public static ModuleName make(final String[] components) {
        
        final int nComponents = components.length;
        
        for (int i = 0; i < nComponents; i++) {
            if (!LanguageInfo.isValidModuleNameComponent(components[i])) {
                throw new IllegalArgumentException("'" + components[i] + "' is not a valid module name component.");
            }
        }
        
        // defensive cloning required
        return new ModuleName(components.clone());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return toSourceText();
    }
    
    /**
     * {@inheritDoc}
     * Returns the module name as it would appear in source, fully-qualified.
     * @return the module name as it would appear in source, fully-qualified.
     */
    public String toSourceText() {
        return getFullyQualifiedString(components);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object other) {
        if (other == null) {
            return false;
        } else if (other instanceof ModuleName){ // can use instanceof instead of getClass() here because the class is final
            return equals((ModuleName)other);
        } else {
            return false;
        }
    }
    
    /**
     * Compares whether the given module name is equal to this module name.
     * @param other another module name.
     * @return true if the given module name is non-null and equals this one, false otherwise.
     */
    public boolean equals(final ModuleName other) {
        if (other == null) {
            return false;
        } else if (this == other) {
            return true;
        } else {
            return Arrays.equals(components, other.components);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        
        // This method is thread safe in that there is no race condition - multiple threads
        // may be trying to calculate the hash in parallel, but ultimate the value they obtain
        // would be the same, and it may be set into the hash field multiple times without affecting
        // the semantics. This is patterned on how the hash code is cached in the String class.
        
        final int cachedHash = hash;
        if (cachedHash == 0) {
            int calculatedHash = 1;
     
            final String[] comps = components;
            final int nComps = comps.length;
            
            for (int i = 0; i < nComps; i++) {
                calculatedHash = 31 * calculatedHash + comps[i].hashCode();
            }
            
            hash = calculatedHash;
            return calculatedHash;
        } else {
            return cachedHash;
        }
    }

    /**
     * {@inheritDoc}
     */
    public int compareTo(final ModuleName other) {
        return FullyQualifiedComparator.INSTANCE.compare(this, other);
    }
    
    /**
     * Returns the number of components in this module name.
     * @return the number of components in this module name.
     */
    public int getNComponents() {
        return components.length;
    }
    
    /**
     * Returns the component at the position specified by the given index.
     * @param n the index of the component to return.
     * @return the specified component.
     */
    public String getNthComponent(final int n) {
        return components[n];
    }
    
    /**
     * Returns the last component.
     * @return the last component.
     */
    public String getLastComponent() {
        return components[components.length - 1];
    }
    
    /**
     * @return an array of the components in this module name.
     * 
     * @see #getNComponents
     * @see #getNthComponent
     * @see #getComponents(int, int)
     */
    public String[] getComponents() {
        return components.clone();
    }
    
    /**
     * Returns an array of some of the components in this module name, as specified by the given start and end indices. 
     * @param start the start position, inclusive.
     * @param end the end position, exclusive.
     * @return an array of the components indexed from start to (end-1).
     */
    public String[] getComponents(final int start, final int end) {
        if (start > end || start < 0 || end < 0 || start >= components.length || end > components.length) {
            throw new IllegalArgumentException();
        }
        
        final int lengthToCopy = end - start;
        final String[] result = new String[lengthToCopy];
        System.arraycopy(components, start, result, 0, lengthToCopy);
        return result;
    }
    
    /**
     * Returns whether this module name is a proper prefix of the given module name.
     * @param other another module name.
     * @return true if this module name is a proper prefix of the given module name, false otherwise.
     */
    public boolean isProperPrefixOf(final ModuleName other) {
        final int n = components.length;
        if (n >= other.components.length) {
            return false;
        }
        
        for (int i = 0; i < n; i++) {
            if (!components[i].equals(other.components[i])) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Returns a module name that is a prefix of this name.
     * @param nComponents the number of components in the prefix.
     * @return a prefix of this module name, which may be this name if the specified number of components
     *         is equal to the number of components in this name.
     */
    public ModuleName getPrefix(int nComponents) {
        final int n = components.length;
        if (nComponents < 0 || nComponents > n) {
            throw new IllegalArgumentException();
        } else {
            final String[] prefixComponents = getComponents(0, nComponents);
            return new ModuleName(prefixComponents);
        }
    }
    
    /**
     * Returns a module name that is the immediate prefix of this name, i.e. it is this name, but dropping the last component.
     * If this name only has one component, then null is returned.
     * @return the immediate prefix of this module name, or null if this name has only one component.
     */
    public ModuleName getImmediatePrefix() {
        final int n = components.length;
        if (n > 1) {
            final String[] prefixComponents = getComponents(0, n - 1);
            return new ModuleName(prefixComponents);
        } else {
            return null;
        }
    }
    
    /**
     * Returns the common prefix of this module name and the given module name (which may be equal to either one if one
     * is an improper prefix of the other), or null if they do not share a common prefix. 
     * @param other another module name.
     * @return the common prefix, or null.
     */
    public ModuleName getCommonPrefix(final ModuleName other) {
        if (other == null) {
            return null;
        }
        
        final int minLength = Math.min(components.length, other.components.length);
        int i;
        for (i = 0; i < minLength; i++) {
            if (!components[i].equals(other.components[i])) {
                break;
            }
        }
        
        final int commonPrefixLength = i; // since i-1 is the last matching index as a post-condition of the loop
        
        if (commonPrefixLength == 0) {
            return null;
        } else {
            return getPrefix(commonPrefixLength);
        }
    }

    /**
     * {@inheritDoc}
     */
    public ModuleName getModuleName() {
        return this;
    }

}
