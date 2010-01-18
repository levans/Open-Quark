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
 * ModuleNameResolver.java
 * Creation date: Nov 6, 2006.
 * By: Joseph Wong
 */
package org.openquark.cal.compiler;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This immutable class encapsulates logic related to resolving module names that appear in CAL source.
 * CAL supports the use of hierarchical module names (i.e. names containing one of more dots),
 * and allows the use of partially qualified module names where they are unambiguous.
 * A partially qualified module name is a proper suffix of a fully qualified module name.
 * For example, B.C and C are partially qualified versions of the module name A.B.C .
 * 
 * <p>
 * Suppose we have a module:
 * <pre>
 * module W.X.Y.Z;
 * 
 * import Y.Z;
 * import Z;
 * import A.B.C.D.E;
 * import P.C.D.E;
 * import D.E;
 * </pre>
 * 
 * <p>
 * Here are the set of name resolutions:
 * <table>
 * <tr><td> <b>Module Name</b> <td> <b>Resolves to...</b>
 * <tr><td> Z                  <td> Z
 * <tr><td> Y.Z                <td> Y.Z
 * <tr><td> X.Y.Z              <td> W.X.Y.Z (the current module)
 * <tr><td> W.X.Y.Z            <td> W.X.Y.Z
 * <tr><td> E                  <td> Ambiguous (A.B.C.D.E, P.C.D.E, D.E)
 * <tr><td> D.E                <td> D.E
 * <tr><td> C.D.E              <td> Ambiguous (A.B.C.D.E, P.C.D.E)
 * <tr><td> B.C.D.E            <td> A.B.C.D.E
 * <tr><td> P.C.D.E            <td> P.C.D.E
 * <tr><td> A.B.C.D.E          <td> A.B.C.D.E
 * </table>
 * 
 * <p>
 * The salient points from the example above are:
 * <ul>
 * <li> The fully qualified name of a module always resolves to that module. 
 * <li> In the case of C.D.E, no preference is given to either A.B.C.D.E or P.C.D.E -- it is considered ambiguous. 
 * <li> Neither Z nor Y.Z resolves to the current module. 
 * <li> Adding a "qualifier" to the front of a resolvable name may make it ambiguous (e.g. D.E -> C.D.E) 
 * </ul>
 * 
 * <p>
 * Each resolvable module name also has a <em>minimally qualified</em> form, i.e. the shortest module name
 * that also resolves to the module unambiguously. For example:
 * <table>
 * <tr><td> <b>Fully Qualified Module Name</b> <td> <b>Minimally Qualified Form</b>
 * <tr><td> Z                                  <td> Z
 * <tr><td> Y.Z                                <td> Y.Z
 * <tr><td> W.X.Y.Z                            <td> X.Y.Z
 * <tr><td> D.E                                <td> D.E
 * <tr><td> P.C.D.E                            <td> P.C.D.E
 * <tr><td> A.B.C.D.E                          <td> B.C.D.E
 * </table>
 *
 * @author Joseph Wong
 */
public final class ModuleNameResolver {

    /**
     * A map mapping each known module name (partially or fully qualified) to its resolution (which
     * may be ambiguous or unambiguous).
     */
    private final Map<ModuleName, ResolutionResult> resolutions;
    
    /**
     * A map mapping each known fully qualified module name to its minimally qualified form.
     * This map does not contain mappings for resolvable partially qualified module names.
     */
    private final Map<ModuleName, ModuleName> minimallyQualifiedModuleNames;
    
    /**
     * This immutable class encapsulates the result of a module name resolution.
     * A module name may be:
     * <ul>
     * <li><em>unambiguous</em> - it resolves to exactly one known module
     * <li><em>ambiguous</em> - it is a proper suffix to two or more module names
     * <li><em>unknown</em> - it is not resolvable to any of the known modules
     * </ul>
     * 
     * Instances of this class class are meant to be constructed only by the {@link ModuleNameResolver}.
     *
     * @author Joseph Wong
     */
    public static final class ResolutionResult {
        
        /**
         * The original module name to be resolved.
         */
        private final ModuleName originalModuleName;
        /**
         * The name of the module the original name resolves to. If the original name is not unambiguously
         * resolvable, then this holds the original name.
         */
        private final ModuleName resolvedModuleName;
        /**
         * The status of the resolution: unambiguous, ambiguous or unknown.
         */
        private final Status status;
        /**
         * The fully qualified names of modules that could potentially be referred to by the original name.
         * If the status is unambiguous, this array should contain just one name. If the status is
         * ambiguous, this array should contain more than one name. If the status is unknown, this array
         * should be empty.
         */
        private final ModuleName[] potentialMatches;
        
        /**
         * An enumerated type for the possible status of a resolution.
         *
         * @author Joseph Wong
         */
        private static final class Status {
            
            /** Represents an unknown module name. */
            private static final Status UNKNOWN = new Status("unknown");
            /** Represents an ambiguous module name. */
            private static final Status AMBIGUOUS = new Status("ambiguous");
            /** Represents an unambiguous module name. */
            private static final Status UNAMBIGUOUS = new Status("unambiguous");
            
            /** A description of the status. */
            private final String description;

            /**
             * Private constructor for the enumerated type.
             * @param description a description of the status.
             */
            private Status(final String description) {
                if (description == null) {
                    throw new NullPointerException();
                }
                
                this.description = description;
            }
            
            /** {@inheritDoc} */
            @Override
            public String toString() {
                return description;
            }
        }
        
        /**
         * Private constructor for use by the {@link ModuleNameResolver}.
         * 
         * @param originalModuleName
         *            the original module name to be resolved. Can be null.
         * @param resolvedModuleName
         *            the name of the module the original name resolves to. If the original name
         *            is not unambiguously resolvable, then this holds the original name. Can be null if originalModuleName is null.
         * @param status
         *            the status of the resolution: unambiguous, ambiguous or unknown.
         * @param potentialMatches
         *            the fully qualified names of modules that could potentially be referred to
         *            by the original name. If the status is unambiguous, this array should
         *            contain just one name. If the status is ambiguous, this array should
         *            contain more than one name. If the status is unknown, this array should
         *            be empty.
         */
        private ResolutionResult(final ModuleName originalModuleName, final ModuleName resolvedModuleName, final Status status, final Set<ModuleName> potentialMatches) {
            
            // resolvedModuleName can only be null if originalModuleName is also null
            if (originalModuleName != null && resolvedModuleName == null) {
                throw new NullPointerException();
            }
            
            if (status == null || potentialMatches == null) {
                throw new NullPointerException();
            }
            
            if (status != Status.UNAMBIGUOUS) {
                if (!areMaybeModuleNamesEqual(originalModuleName, resolvedModuleName)) {
                    throw new IllegalArgumentException("if the original name is ambiguous or unknown, then the resolved name should be the original name");
                }
            }
            
            this.originalModuleName = originalModuleName;
            this.resolvedModuleName = resolvedModuleName;
            this.status = status;
            this.potentialMatches = potentialMatches.toArray(new ModuleName[potentialMatches.size()]);
        }

        /**
         * @return the original module name to be resolved. Can be null.
         */
        public ModuleName getOriginalModuleName() {
            return originalModuleName;
        }

        /**
         * @return the name of the module the original name resolves to. If the original name
         *         is not unambiguously resolvable, then the original name is returned.
         *         Can be null if {@link #getOriginalModuleName()} returns null.
         */
        public ModuleName getResolvedModuleName() {
            return resolvedModuleName;
        }
        
        /**
         * @return true if the original module name is not resolvable to any of the known modules; false otherwise.
         */
        public boolean isUnknown() {
            return status == Status.UNKNOWN;
        }

        /**
         * @return true if the original module name is a proper suffix to two or more module names; false otherwise.
         */
        public boolean isAmbiguous() {
            return status == Status.AMBIGUOUS;
        }
        
        /**
         * @return true if the original module name resolves to exactly one known module; false otherwise.
         */
        public boolean isKnownUnambiguous() {
            return status == Status.UNAMBIGUOUS;
        }

        /**
         * @return the fully qualified names of modules that could potentially be referred to
         *         by the original name. If the status is unambiguous, this array should
         *         contain just one name. If the status is ambiguous, this array should
         *         contain more than one name. If the status is unknown, this array should be empty.
         */
        public ModuleName[] getPotentialMatches() {
            return potentialMatches.clone();
        }

        /**
         * @return true if <code>getOriginalModuleName().equals(getResolvedModuleName())</code>; false otherwise.
         *         In particular, the returned value does not take into account whether the original name is unambiguously
         *         resolvable or not.
         */
        public boolean isResolvedModuleNameEqualToOriginalModuleName() {
            return areMaybeModuleNamesEqual(originalModuleName, resolvedModuleName);
        }
        
        /**
         * Checks the equality of the 2 arguments, which may be null.
         * @param maybeModuleName1 a module name, or null.
         * @param maybeModuleName2 a module name, or null.
         * @return true if the names are equal or if they are both null, false otherwise.
         */
        private static boolean areMaybeModuleNamesEqual(final ModuleName maybeModuleName1, final ModuleName maybeModuleName2) {
            if (maybeModuleName1 == null) {
                return maybeModuleName2 == null;
            } else {
                return maybeModuleName1.equals(maybeModuleName2);
            }
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return
                "[Original: " + getOriginalModuleName() +
                ", Resolved: " + getResolvedModuleName() +
                ", Status: " + status +
                ", Potential matches: " + Arrays.asList(potentialMatches) + "]";
        }
    }
    
    /**
     * Warning- this class should only be used by the CAL compiler implementation. It is not part of the
     * external API of the CAL platform.
     * <P>
     * This class encapsulates a mapping which maps module names that are affected by a module renaming to
     * their corresponding disambiguated names.
     *
     * @author Joseph Wong
     */
    static final class RenameMapping {
        /**
         * A map which maps module names that are affected by a module renaming to
         * their corresponding disambiguated names.
         */
        private final Map<ModuleName, ModuleName> oldToNewMapping;
        
        /**
         * Private constructor for use by the {@link ModuleNameResolver}.
         * 
         * @param oldToNewMapping
         *            a map which maps module names that are affected by a module renaming to their corresponding
         *            disambiguated names.
         */
        private RenameMapping(final Map<ModuleName, ModuleName> oldToNewMapping) {
            if (oldToNewMapping == null) {
                throw new NullPointerException();
            }
            
            this.oldToNewMapping = oldToNewMapping;
        }
        
        /**
         * Returns the new name for the given module. Maps null to null.
         * @param oldName a module name.
         * @return the new name for the module. Maps null to null.
         */
        ModuleName getNewNameForModule(final ModuleName oldName) {
            if (oldName == null) {
                return null;
            }
            
            final ModuleName newName = oldToNewMapping.get(oldName);
            if (newName == null) {
                return oldName;
            } else {
                return newName;
            }
        }
        
        /**
         * Returns whether the given module is mapped to a new name under this mapping.
         * @param oldName a module name.
         * @return true if the given module is mapped to a new name under this mapping; false otherwise.
         */
        boolean hasNewName(final ModuleName oldName) {
            if (oldName == null) {
                return false;
            }
            
            return oldToNewMapping.containsKey(oldName);
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return "[RenameMapping (old module name -> new module name): " + oldToNewMapping.toString() + "]";
        }
    }
    
    /**
     * Private constructor. This class should be instantiated via the factory methods.
     * 
     * @param resolutions
     *            a map mapping each known module name (partially or fully qualified) to its resolution
     *            (which may be ambiguous or unambiguous).
     * @param minimallyQualifiedModuleNames
     *            a map mapping each known fully qualified module name to its minimally qualified form.
     *            This map does not contain mappings for resolvable partially qualified module names.
     */
    private ModuleNameResolver(final Map<ModuleName, ResolutionResult> resolutions, final Map<ModuleName, ModuleName> minimallyQualifiedModuleNames) {
        if (resolutions == null || minimallyQualifiedModuleNames == null) {
            throw new NullPointerException();
        }
        this.resolutions = resolutions;
        this.minimallyQualifiedModuleNames = minimallyQualifiedModuleNames;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return
            "[ModuleNameResolver:\n  resolutions: " + resolutions.toString() +
            "\n  minimallyQualifiedModuleNames: " + minimallyQualifiedModuleNames.toString() + "\n]";
    }
    
    /**
     * Creates a module name resolver for a given module and its imports.
     * 
     * @param currentModuleName the name of the module for which the resolver is to be created.
     * @param importedModuleNames the set of names of another modules imported by the module.
     * @return a new instance of this class.
     */
    public static ModuleNameResolver make(final ModuleName currentModuleName, final Set<ModuleName> importedModuleNames) {
        final Set<ModuleName> visibleModuleNames = new HashSet<ModuleName>(importedModuleNames);
        visibleModuleNames.add(currentModuleName);
        
        return make(visibleModuleNames);
    }
    
    /**
     * Creates a module name resolver from a given set of fully qualified module names.
     * @param visibleModuleNames the set of fully qualified module names.
     * @return a new instance of this class.
     */
    public static ModuleNameResolver make(final Set<ModuleName> visibleModuleNames) {
        
        ////
        /// First we obtain, for each partial module name constructible from the set of visible module names,
        /// a set of potential matches for that partial module name.
        //

        // For example, given the module declaration:
        //
        // module W.X.Y.Z;
        // import Y.Z;
        // import Z;
        // import A.B.C.D.E;
        // import P.C.D.E;
        // import D.E;
        //
        // we build up the following map:
        //
        // Z          -> {Z}
        // Y.Z        -> {Y.Z}
        // X.Y.Z      -> {W.X.Y.Z}
        // W.X.Y.Z    -> {W.X.Y.Z}
        // E          -> {A.B.C.D.E, P.C.D.E, D.E}
        // D.E        -> {D.E}
        // C.D.E      -> {A.B.C.D.E, P.C.D.E}
        // B.C.D.E    -> {A.B.C.D.E}
        // P.C.D.E    -> {P.C.D.E}
        // A.B.C.D.E  -> {A.B.C.D.E}
        
        final Map<ModuleName, Set<ModuleName>> partialModuleNameToPotentialMatches = new HashMap<ModuleName, Set<ModuleName>>();
        
        for (final ModuleName moduleName : visibleModuleNames) {
            String partialModuleNameString = "";
            final int lastComponentIndex = moduleName.getNComponents() - 1;
            
            for (int i = lastComponentIndex; i >= 0; i--) {

                if (i == lastComponentIndex) {
                    partialModuleNameString = moduleName.getNthComponent(i);
                } else {
                    partialModuleNameString = moduleName.getNthComponent(i) + '.' + partialModuleNameString;
                }
                
                final ModuleName partialModuleName = ModuleName.make(partialModuleNameString);
                
                Set<ModuleName> potentialMatches = partialModuleNameToPotentialMatches.get(partialModuleName);
                if (potentialMatches == null) {
                    potentialMatches = new HashSet<ModuleName>();
                    partialModuleNameToPotentialMatches.put(partialModuleName, potentialMatches);
                }
                
                potentialMatches.add(moduleName);
            }
        }
        
        ////
        /// We now turn the mapping into ResolutionResults (which may be ambiguous or not, depending on whether there
        /// is just one match or more than one.)
        //
        
        final Map<ModuleName, ResolutionResult> resolutions = new HashMap<ModuleName, ResolutionResult>();
        
        for (final Map.Entry<ModuleName, Set<ModuleName>> entry : partialModuleNameToPotentialMatches.entrySet()) {
            final ModuleName partialModuleName = entry.getKey();
            final Set<ModuleName> potentialMatches = entry.getValue();
            
            final int nPotentialMatches = potentialMatches.size();
            
            ResolutionResult resolution;
            
            if (nPotentialMatches > 1) {
                
                if (potentialMatches.contains(partialModuleName)) {
                    // the name could resolve to itself, therefore the name is a fully-qualified module name, and is thus not ambiguous
                    resolution = new ResolutionResult(partialModuleName, partialModuleName, ResolutionResult.Status.UNAMBIGUOUS, potentialMatches);
                    
                } else {
                    // the name is ambiguous
                    resolution = new ResolutionResult(partialModuleName, partialModuleName, ResolutionResult.Status.AMBIGUOUS, potentialMatches);
                }
                
            } else if (nPotentialMatches == 1) {
                final ModuleName match = potentialMatches.iterator().next();
                resolution = new ResolutionResult(partialModuleName, match, ResolutionResult.Status.UNAMBIGUOUS, potentialMatches);
                
            } else {
                throw new IllegalStateException();
            }
            
            resolutions.put(partialModuleName, resolution);
        }
        
        ////
        /// Finally, from the ResolutionResults we build a map from fully-qualified module names to their minimally-qualified forms
        //
        
        final Map<ModuleName, ModuleName> minimallyQualifiedModuleNames = new HashMap<ModuleName, ModuleName>();
        
        for (final ResolutionResult resolution : resolutions.values()) {
            if (resolution.isKnownUnambiguous()) {
                final ModuleName potentialMinimalName = resolution.getOriginalModuleName();
                final ModuleName fullyQualifiedName = resolution.getResolvedModuleName();
                
                final ModuleName minimalName = minimallyQualifiedModuleNames.get(fullyQualifiedName);
                
                if (minimalName == null || potentialMinimalName.getNComponents() < minimalName.getNComponents()) {
                    minimallyQualifiedModuleNames.put(fullyQualifiedName, potentialMinimalName);
                }
            }
        }
        
        return new ModuleNameResolver(resolutions, minimallyQualifiedModuleNames);
    }
    
    /**
     * Resolves the given module name and returns a ResolutionResult.
     * @param moduleName the module name to be resolved. Can be null.
     * @return a ResolutionResult representing the result of the resolution.
     */
    public ResolutionResult resolve(final ModuleName moduleName) {
        final ResolutionResult result = resolutions.get(moduleName);
        
        if (result == null) {
            return new ResolutionResult(moduleName, moduleName, ResolutionResult.Status.UNKNOWN, Collections.<ModuleName>emptySet());
        } else {
            return result;
        }
    }
    
    /**
     * Returns the minimally qualified form of the given module name.
     * @param moduleName the module name, which may be fully qualified or partially qualified. Can be null.
     * @return the minimally qualified form of the given module name. If the given name is not resolvable, then it is value returned.
     *         If the given module name is null, then null is returned.
     */
    public ModuleName getMinimallyQualifiedModuleName(final ModuleName moduleName) {
        final ResolutionResult resolution = resolve(moduleName);
        
        final ModuleName minimalName = minimallyQualifiedModuleNames.get(resolution.getResolvedModuleName());
        
        if (minimalName == null) {
            return moduleName;
        } else {
            return minimalName;
        }
    }
    
    /**
     * Returns whether a naming conflict will be introduced by adding an additional import statement.
     * @param additionalImportedModuleName the module name to be checked.
     * @return true if a naming conflict will be introduced by adding an additional import statement for the given module name; false otherwise.
     */
    boolean willAdditionalModuleImportProduceConflict(final ModuleName additionalImportedModuleName) {
        
        ////
        /// For each partial module name constructible from the additional visible module name,
        /// we determine whether it used to be uniquely resolvable but is now in conflict.
        //

        String partialModuleName = "";
        final int lastComponentIndex = additionalImportedModuleName.getNComponents() - 1;

        for (int i = lastComponentIndex; i >= 0; i--) {

            if (i == lastComponentIndex) {
                partialModuleName = additionalImportedModuleName.getNthComponent(i);
            } else {
                partialModuleName = additionalImportedModuleName.getNthComponent(i) + '.' + partialModuleName;
            }

            final ResolutionResult existingResolution = resolve(ModuleName.make(partialModuleName));

            if (existingResolution.isKnownUnambiguous()) {
                // if the existing resolution does not resolve the name to itself (i.e. the name was a fully-qualified module name)
                // then the name becomes ambiguous with the addition of these newly visible module name

                if (!existingResolution.isResolvedModuleNameEqualToOriginalModuleName()) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    /**
     * Returns a new module name resolver for use after a module renaming.
     * @param oldModuleName the original name of the module to be renamed.
     * @param newModuleName the new name of the module to be renamed.
     * @return a new module name resolver for use after a module renaming.
     */
    ModuleNameResolver getResolverAfterRenaming(final ModuleName oldModuleName, final ModuleName newModuleName) {
        final Set<ModuleName> visibleModuleNames = new HashSet<ModuleName>(minimallyQualifiedModuleNames.keySet());
        visibleModuleNames.remove(oldModuleName);
        visibleModuleNames.add(newModuleName);
        return make(visibleModuleNames);
    }
    
    /**
     * @return an empty RenameMapping.
     */
    RenameMapping makeEmptyRenameMapping() {
        return new RenameMapping(Collections.<ModuleName, ModuleName>emptyMap());
    }
    
    /**
     * Constructs a RenameMapping for handling the given module renaming in the scope of the module for which this
     * module name resolver is responsible.
     * @param oldModuleName the original name of the module to be renamed.
     * @param newModuleName the new name of the module to be renamed.
     * @return a RenameMapping that can be used for updating the module for which this module name resolver is responsible.
     */
    RenameMapping makeRenameMapping(final ModuleName oldModuleName, final ModuleName newModuleName) {
        
        // If A.B is renamed to C.D.E.F, and the original module X imports A.B, X.D.E.F and E.F, we build the mapping:
        //
        // E.F -> E.F
        // D.E.F -> X.D.E.F
        //
        // The handling of A.B -> C.D.E.F and B -> C.D.E.F is to be taken care of by the renamer itself
        // (because that should simply be part of its resolution code)
        
        final Map<ModuleName, ModuleName> oldToNewMapping = new HashMap<ModuleName, ModuleName>();
        
        String partialNameFromNewModuleName = "";
        final int lastComponentIndex = newModuleName.getNComponents() - 1;

        for (int i = lastComponentIndex; i >= 0; i--) {

            if (i == lastComponentIndex) {
                partialNameFromNewModuleName = newModuleName.getNthComponent(i);
            } else {
                partialNameFromNewModuleName = newModuleName.getNthComponent(i) + '.' + partialNameFromNewModuleName;
            }

            final ModuleName partialModuleName = ModuleName.make(partialNameFromNewModuleName);
            
            final ResolutionResult existingResolution = resolve(partialModuleName);

            if (existingResolution.isKnownUnambiguous()) {
                
                final ModuleName resolvedModuleName = existingResolution.getResolvedModuleName();
                
                if (resolvedModuleName.equals(oldModuleName)) {
                    oldToNewMapping.put(partialModuleName, newModuleName);
                } else {
                    oldToNewMapping.put(partialModuleName, resolvedModuleName);
                }
            }
        }
        
        return new RenameMapping(oldToNewMapping);
    }
}
