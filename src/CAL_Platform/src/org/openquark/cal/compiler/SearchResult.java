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
 * SearchResult.java
 * Creation date: (Dec 5, 2005)
 * By: Jawright
 */
package org.openquark.cal.compiler;

/**
 * Represents a single search hit from a search performed by the SourceMetricFinder.
 * @author Jawright
 */
public abstract class SearchResult {
    
    /**
     * Represents a precise search hit (with source range) from a search performed by the SourceMetricFinder.
     *
     * @author James Wright
     */
    public static class Precise extends SearchResult {
        
        /** SourceRange of the search hit */
        private final SourceRange sourceRange;
        
        /** The category of the result. This maybe null */
        private final SourceIdentifier.Category category;
        
        /** Source file line containing this hit */
        private final String contextLine;
        
        /** 
         * index into contextLine of the hit; note that this is not necessarily derivable from
         * sourcePosition due to tabbing issues.
         */
        private final int contextLineIndex;

        /**
         * The result refers to the java source corresponding to the object.
         */
        private final boolean refersToJavaSource;
        
        /**
         * @param sourceRange SourceRange of the search hit; this parameter must not be null.
         * @param name QualifiedName that was found at sourcePosition; this parameter must not be null.
         * @param contextLine String containing the line that the SearchResult occurs on.  This parameter may be null.
         * @param contextLineIndex index into contextLine of the hit; note that this is not necessarily derivable
         *                          from sourcePosition alone, due to tab-translation issues.  This parameter may be
         *                          negative (to indicate that it is not present).
         */
        Precise(SourceRange sourceRange, Name name, SourceIdentifier.Category category, String contextLine, int contextLineIndex, boolean refersToJavaSource) {            
            super(name);
            
            if(sourceRange== null) {
                throw new NullPointerException();
            }
            
            this.category = category;
            this.sourceRange = sourceRange;
            this.contextLine = contextLine;
            this.contextLineIndex = contextLineIndex;
            this.refersToJavaSource = refersToJavaSource;
        }
        
        /**
         * @param sourceRange SourceRange of the search hit; this parameter must not be null.
         * @param name QualifiedName that was found at sourcePosition
         */
        Precise(SourceRange sourceRange, Name name, SourceIdentifier.Category category, boolean refersToJavaSource) {
            this(sourceRange, name, category, null, -1, refersToJavaSource);
        }
        
        /**
         * @return Category of the search hit. This may return null.
         */
        public SourceIdentifier.Category getCategory(){
            return category;
        }
        
        /**
         * @return SourcePosition of the search hit.  Will never return null.
         */
        public SourcePosition getSourcePosition() {
            return sourceRange.getStartSourcePosition();
        }
        
        /**
         * @return SourceRange of the search hit.  Will never return null.
         */
        public SourceRange getSourceRange() {
            return sourceRange;
        }
        
        /**
         * @return containing the line that the SearchResult occurs on if available;
         *          may be null.
         */
        public String getContextLine() {
            return contextLine;
        }
        
        /**
         * @return index into contextLine of the start of the search hit.  May return a negative value.
         */
        public int getContextLineIndex() {
            return contextLineIndex;
        }
        
        /**
         * @return index into contextLine of the (exclusive) end of the search hit.  May return a negative value.
         *          If the hit range extends beyond the contextLine, then returns the length of the contextLine.
         */
        public int getContextLineEndIndex() {
            
            if(contextLine == null) {
                return -1;
            }
            
            SourcePosition endPosition = sourceRange.getEndSourcePosition();
            if(endPosition.getLine() > getSourcePosition().getLine()) {
                return contextLine.length();
            }
            
            if(endPosition.getColumn() == 1) {
                return 0;
            }
            
            // Find the inclusive end position (to avoid falling off the end of the line) and then add 1
            // to make it exclusive.
            SourcePosition firstLineEndPosition = new SourcePosition(1, endPosition.getColumn() - 1);
            int inclusiveEnd = firstLineEndPosition.getPosition(contextLine); 
            return inclusiveEnd + 1;
        }
        
        @Override
        public String toString() {
            return getName() + " at " + sourceRange.toString();
        }
        
        public boolean refersToJavaSource(){
            return refersToJavaSource;
        }
    }
    
    /**
     * Represents a search hit based only on frequency information.
     *
     * @author Joseph Wong
     */
    public static final class Frequency extends SearchResult {
        /** The name of the module where the search hit occurs. */
        private final ModuleName moduleName;
        /** How many times the search criteria matches. */
        private final int frequency;
        /** The type of the search hit. */
        private final Type type;
        
        /** Typesafe enumeration for the type of the search hit. */
        public static final class Type {
            /** The display name of the type. */
            private final String name;
            /** Private constructor. */
            private Type(String name) { this.name = name; }
            /** {@inheritDoc} */
            @Override
            public String toString() { return name; }
            
            public static final Type FUNCTION_REFERENCES = new Type("references in functions");
            public static final Type DEFINITION = new Type("declarations");
            public static final Type INSTANCES = new Type("instances for class");
            public static final Type CLASSES = new Type("instances for type");
            public static final Type INSTANCE_METHOD_REFERENCES = new Type("instances method reference");
            public static final Type CLASS_CONSTRAINTS = new Type("class constraints reference");
            public static final Type IMPORT = new Type("import statement references");
        }
        
        /**
         * @param moduleName the name of the module where the search hit occurs.
         * @param name QualifiedName that was found; this parameter must not be null.
         * @param frequency how many times the search criteria matches.
         * @param type the type of the search hit.
         */
        Frequency(ModuleName moduleName, Name name, int frequency, Type type) {
            
            super(name);
            
            if (moduleName == null || type == null) {
                throw new NullPointerException();
            }
            
            this.moduleName = moduleName;
            this.frequency = frequency;
            this.type = type;
        }
        
        /**
         * @return the name of the module where the search hit occurs.
         */
        public ModuleName getModuleName() {
            return moduleName;
        }
        
        /**
         * @return how many times the search criteria matches.
         */
        public int getFrequency() {
            return frequency;
        }
        
        /**
         * @return the type of the search hit.
         */
        public Type getType() {
            return type;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return moduleName + ": " + frequency + " " + type + " - " + getName();
        }
    }

    /** QualifiedName that was found at sourcePosition */
    private final Name name;
    
    /**
     * @param qualifiedName QualifiedName that was found; this parameter must not be null.
     */
    private SearchResult(Name qualifiedName) {
        if (qualifiedName == null) {
            throw new NullPointerException();
        }

        this.name = qualifiedName;
    }
    
    /**
     * @return QualifiedName that was found.  Will never return null.
     */
    public Name getName() {
        return name;
    }
    
}
