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
 * ModuleNameDisplayUtilities.java
 * Creation date: Jan 17, 2007.
 * By: Joseph Wong
 */
package org.openquark.gems.client;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.ModuleNameResolver;

/**
 * This is a utility class containing methods for constructing display strings
 * for module names.
 *
 * @author Joseph Wong
 */
public final class ModuleNameDisplayUtilities {
    
    /**
     * A type-safe enumeration of the different display modes for tree views displaying modules.
     *
     * @author Joseph Wong
     */
    public static final class TreeViewDisplayMode {
        
        public static final TreeViewDisplayMode FLAT_ABBREVIATED = new TreeViewDisplayMode("Flat-Abbreviated");
        public static final TreeViewDisplayMode FLAT_FULLY_QUALIFIED = new TreeViewDisplayMode("Flat-FullyQualified");
        public static final TreeViewDisplayMode HIERARCHICAL = new TreeViewDisplayMode("Hierarchical");
        
        /** The name of the display mode. */
        private final String name;
        
        /** Private constructor. */
        private TreeViewDisplayMode(String name) {
            this.name = name;
        }
        
        /**
         * Returns the enumerated constant of the given name.
         * @param name the name of the display mode.
         * @return the enumerated constant, or null if the name is invalid.
         */
        public static TreeViewDisplayMode fromName(String name) {
            if (name.equals(FLAT_ABBREVIATED.getName())) {
                return FLAT_ABBREVIATED;
            } else if (name.equals(FLAT_FULLY_QUALIFIED.getName())) {
                return FLAT_FULLY_QUALIFIED;
            } else if (name.equals(HIERARCHICAL.getName())) {
                return HIERARCHICAL;
            } else {
                throw new IllegalArgumentException("Unrecognized TreeViewDisplayMode: " + name);
            }
        }
        
        /** @return the name of the display mode. */
        public String getName() {
            return name;
        }
        
        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "[TreeViewDisplayMode: " + name + "]";
        }
    }

    /** This class is not to be instantiated. */
    private ModuleNameDisplayUtilities() {}

    /**
     * Returns the display name of a gem drawer for a module.
     * @param moduleName the name of the module.
     * @param workspaceModuleNameResolver the module name resolver to use to generate an appropriate module name
     * @param treeViewDisplayMode the display mode of the tree.
     * @return the corresponding gem drawer name.
     */
    public static String getDisplayNameForModuleInTreeView(ModuleName moduleName, ModuleNameResolver workspaceModuleNameResolver, TreeViewDisplayMode treeViewDisplayMode) {
        
        final String lastComponent = moduleName.getLastComponent();

        if (treeViewDisplayMode == TreeViewDisplayMode.FLAT_ABBREVIATED) {

            final ModuleName minimallyQualifiedModuleName = workspaceModuleNameResolver.getMinimallyQualifiedModuleName(moduleName);

            if (minimallyQualifiedModuleName.getNComponents() == 1) {
                return lastComponent;
            } else {
                final int nMinusOne = minimallyQualifiedModuleName.getNComponents() - 1;

                final StringBuilder sb = new StringBuilder(lastComponent).append(" (");

                for (int i = 0; i < nMinusOne; i++) {
                    if (i > 0) {
                        sb.append(".");
                    }
                    sb.append(minimallyQualifiedModuleName.getNthComponent(i));
                }
                sb.append(")");
                return sb.toString();
            }
            
        } else if (treeViewDisplayMode == TreeViewDisplayMode.FLAT_FULLY_QUALIFIED) {
            return moduleName.toSourceText();
            
        } else if (treeViewDisplayMode == TreeViewDisplayMode.HIERARCHICAL) {
            return lastComponent;
            
        } else {
            throw new IllegalArgumentException("Unrecognized TreeViewDisplayMode: " + treeViewDisplayMode);
        }
    }
    
}
