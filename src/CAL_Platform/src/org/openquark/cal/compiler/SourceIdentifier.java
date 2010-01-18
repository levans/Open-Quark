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
 * SourceIdentifier.java
 * Creation date: Feb 10, 2004
 * By: Iulian Radu
 */
package org.openquark.cal.compiler;

import java.util.Comparator;

/**
 * Tracks the category and position of an identifier and its module name (if any) within a CAL source stream.
 * 
 * @author Iulian Radu
 */

public abstract class SourceIdentifier {
    
    /**
     * A qualifiable source identifier is an identifier that may (but does not neccessarily) have a module name associated with it.
     * Note that in some cases having an unqualified qualifiable identifier may mean that if you were to add a module name to the
     * identifier, the category would be changed. For example, a function definition can not actually be have a module name associated
     * with it, but a function reference may, so for simplicity we consider both to be "qualifiable".
     * @author Peter Cardwell
     */
    static final class Qualifiable extends SourceIdentifier {
        
        /**
         * Name of the module the identifier is qualified to.
         * <p>
         * This field is null for unqualified identifiers.
         */
        private final ModuleName moduleName;
        
        /**
         * If {@link #moduleName} is resolvable, then this is the fully qualified module name it resolves to.
         * Otherwise, this holds the same value as {@link #moduleName}.
         * <p>
         * This field is null for unqualified identifiers.
         */
        private final ModuleName resolvedModuleName;
        
        /**
         * If {@link #moduleName} is resolvable, then this is the minimally qualified module name that resolves to the same module.
         * Otherwise, this holds the same value as {@link #moduleName}.
         * <p>
         * This field is null for unqualified identifiers.
         */
        private final ModuleName minimallyQualifiedModuleName;
        
        /**
         * Source range of the module name. Note that CAL allows for module name and identifier name to be
         * separated by whitespace, including newlines, so for convenience we track both positions. Note also that
         * a module name itself could have multiple components separated by dots (which can have whitespace around them). 
         * <p>
         * This field is null for unqualified identifiers.
         */
        private final SourceRange moduleSourceRange;
        
        /**
         * Symbol representing the definition of the current identifier (null if this is not known)
         */
        private final SourceIdentifier definitionIdentifier;
        
        
        
        /** @return the name of the module containing the identifier (null if none was specified)*/
        @Override
        public ModuleName getRawModuleName() {
            return moduleName;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public ModuleName getResolvedModuleName() {
            return resolvedModuleName;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public ModuleName getMinimallyQualifiedModuleName() {
            return minimallyQualifiedModuleName;
        }
        
        /** @return the source range of the module name (null if none was specified)*/
        @Override
        public SourceRange getRawModuleSourceRange() {
            return moduleSourceRange;
        }
        
        /** @return whether the module name has a source position associated with it */
        @Override
        public boolean hasRawModuleSourceRange() {
            return moduleSourceRange != null;
        }
        
        /** @return symbol representing the definition of this identifier */
        @Override
        public SourceIdentifier getDefinition() {
            return definitionIdentifier;
        }
        
        public Qualifiable(String identifierName, SourceRange range, Category category, ModuleName rawModuleName, ModuleName resolvedModuleName, ModuleName minimallyQualifiedModuleName, SourceRange moduleRange, SourceIdentifier definitionIdentifier) {
            super(identifierName, range, category);
            
            this.moduleName = rawModuleName;
            this.resolvedModuleName = resolvedModuleName;
            this.minimallyQualifiedModuleName = minimallyQualifiedModuleName;
            this.moduleSourceRange = moduleRange;
            this.definitionIdentifier = definitionIdentifier;
        }
        
        public Qualifiable(String identifierName, SourceRange range, Category category, ModuleName rawModuleName, ModuleName resolvedModuleName, ModuleName minimallyQualifiedModuleName, SourceRange moduleRange) {
            this (identifierName, range, category, rawModuleName, resolvedModuleName, minimallyQualifiedModuleName, moduleRange, null);
        }
        
    }
    
    /**
     * Class enumerating category of a CAL identifier stored in an SourceIdentifier.
     * The category can be a top-level function, data constructor, type constructor or type class 
     * 
     * @author Iulian Radu
     */
    public static final class Category {
        
        private final String enumType;
        private Category(String type) {
            if (type == null) {
                throw new NullPointerException();
            }
            enumType = type;
        }
        
        /** Identifier is a data constructor (eg: True) */
        public static final Category
            DATA_CONSTRUCTOR = new Category("DATA_CONSTRUCTOR");
        
        /** Identifier is a type constructor (eg: Boolean) */
        public static final Category
            TYPE_CONSTRUCTOR = new Category("TYPE_CONSTRUCTOR");
        
        /** Identifier is a type class (eg: Ordering) */
        public static final Category
            TYPE_CLASS = new Category("TYPE_CLASS");
        
        /** Identifier is a top-level function or class method (eg: not) */
        public static final Category
            TOP_LEVEL_FUNCTION_OR_CLASS_METHOD = new Category("TOP_LEVEL_FUNCTION_OR_CLASS_METHOD");
        
        /** Identifies the definition of a local variable (eg: let myVar = 1.0 in ..) */
        public static final Category
            LOCAL_VARIABLE_DEFINITION = new Category("LOCAL_DEFINITION");
        
        /** Identifier is a reference to a local variable (eg. second myVar in: let myVar =... in myVar) */
        public static final Category
            LOCAL_VARIABLE = new Category("LOCAL_VARIABLE");
        
        /** Identifier is a reference to a module */
        public static final Category
            MODULE_NAME = new Category("MODULE_NAME");

        /** the identifier is a field name in a data constructor */
        public static final Category
            DATA_CONSTRUCTOR_FIELD_NAME = new Category("DATA_CONSTRUCTOR_FIELD_NAME");
        
        /**
         * Converts enumeration to string.
         */
        @Override
        public String toString() {
            return enumType;
        }
    }
    

    /** Name of the identifier we are referring to */
    private final String identifierName;
    
    /** Source position of identifier */
    private final SourceRange sourceRange;
    
    /** Category of the identifier */
    private final Category category;
   
    /**
     * Comparator object to order Source Identifier by increasing start source position of
     * the identifier unqualified name.
     */
    public static final Comparator<SourceIdentifier> compareByStartPosition = new CompareByStartPosition();
    private static class CompareByStartPosition implements Comparator<SourceIdentifier> {
        public int compare(SourceIdentifier o1, SourceIdentifier o2) {
            if ((o1 == null) || (o2 == null)) {
                throw new IllegalArgumentException();
            }
            return SourcePosition.compareByPosition.compare(
                o1.getSourceRange().getStartSourcePosition(),
                o2.getSourceRange().getStartSourcePosition());
        }
    }
    
    /**
     * Constructor
     * 
     * @param identifierName unqualified name of the identifier
     * @param range source range of the identifier
     * @param category category of the identifier
     */
    private SourceIdentifier(String identifierName, SourceRange range, Category category) {
        if ((identifierName == null) || (category == null) || (range == null)) {
            throw new IllegalArgumentException();
        }
        
        this.category = category;
        this.identifierName = identifierName;
        this.sourceRange = range;
        
    }
    
    /** @return the name of the module containing the identifier (null if none was specified) */
    abstract public ModuleName getRawModuleName();
    
    /**
     * @return if the value returned by {@link #getRawModuleName()} is resolvable, then this is the fully qualified module name it
     *         resolves to. Otherwise, this is the same value as {@link #getRawModuleName()}.
     */
    abstract public ModuleName getResolvedModuleName();
    
    /**
     * @return if the value returned by {@link #getRawModuleName()} is resolvable, then this is the minimally qualified module name that
     *         resolves to the same module. Otherwise, this is the same value as {@link #getRawModuleName()}.
     */
    abstract public ModuleName getMinimallyQualifiedModuleName();
    
    /** @return the source range of the module name (null if none was specified)*/
    abstract public SourceRange getRawModuleSourceRange();
    
    /** @return whether the module name has a source range associated with it */
    abstract public boolean hasRawModuleSourceRange();
    
    /** @return symbol representing the definition of this identifier */
    abstract public SourceIdentifier getDefinition();
    
    /** @return the unqualified name of identifier */
    public String getName() {
        return identifierName;
    }
    
    /** @return range of identifier */
    public SourceRange getSourceRange() {
        return sourceRange;
    }
    
    /** @return category of identifier */
    public Category getCategory() {
        return category;
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getName() + " (" + category + ") at line: " + getSourceRange().getStartLine() + " col: " + getSourceRange().getStartColumn() + " of source: " + getSourceRange().getSourceName();
    }
}
