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
 * WorkspaceSourceMetrics.java
 * Creation date: (Apr 27, 2005)
 * By: Jawright
 */
package org.openquark.cal.compiler;

import java.util.List;

import org.openquark.cal.filter.ModuleFilter;
import org.openquark.cal.filter.QualifiedNameFilter;


/**
 * An interface representing the source metrics associated with a given workspace.
 * The metrics returned by implementations of this interface change as modules are 
 * added to and removed from the workspace.
 * 
 * Clients should be given references to this interface, rather than to the underlying
 * implementation class, wherever possible, since this interface provides only query 
 * methods.
 * 
 * Creation date: (Apr 27, 2005)
 * @author Jawright
 */
public interface SourceMetrics {
    /**
     * Return the reference frequency metric for the specified gem in the context of the workspace
     * @param gemName Name of gem to retrieve frequency for
     * @return Number of times that gem specified by gemName is referenced in the body of functions in modules in containing workspace
     */
    public int getGemReferenceFrequency(QualifiedName gemName);

    /**
     * Returns a string containing the reference frequency for each gem in the context of the workspace.
     * @param moduleFilter Filter for deciding which modules will be processed for the frequency counts. Cannot be null.
     * @param functionFilter Filter for deciding which functions will be processed. Cannot be null.
     * @param traceSkippedFunctions When true, each skipped function will have its name dumped to the console.
     *                               When false, skipped functions will be skipped silently      
     * @return A string containing a line for each gem in the workspace with at least one reference.
     */
    public String dumpReferenceFrequencies(ModuleFilter moduleFilter, QualifiedNameFilter functionFilter, boolean traceSkippedFunctions);
    
    /**
     * @param moduleFilter Filter for deciding which modules will be processed for the frequency counts. Cannot be null.
     * @param functionFilter Filter for deciding which functions will be processed. Cannot be null.
     * @param traceSkippedFunctions When true, each skipped function will have its name dumped to the console.
     *                               When false, skipped functions will be skipped silently      
     * @return A string containing a line for each pair of gems in the workspace where the output of
     * one gem is used directly as the input of another.  See SourceMetricFinder for more details about
     * the compositional frequency metric.
     */
    public String dumpCompositionalFrequencies(ModuleFilter moduleFilter, QualifiedNameFilter functionFilter, boolean traceSkippedFunctions);
    
    /**
     * Dump any lint warnings associated with the workspace to the system console.
     * @param moduleFilter Filter for deciding which modules will be processed with lint. Cannot be null.
     * @param functionFilter Filter for deciding which functions will be processed. Cannot be null.
     * @param traceSkippedModulesAndFunctions If true, every module and function skipped will be dumped to the console.
     *                                         If false, modules/functions that are skipped will be skipped silently.
     * @param includeUnplingedPrimitiveArgs When true, dump warnings about unplinged primitive arguments
     * @param includeRedundantLambdas When true, dump warnings about potentially redundant lambdas 
     * @param includeUnusedPrivates When true, check for unused private top-level functions.
     * @param includeMismatchedAliasPlings When true, check for alias functions whose plinging is not 
     *                                      compatible with that of the underlying data constructor / foreign function. 
     * 
     * The strictness of an alias is compatible with that of an aliased function
     * when the same arguments are plinged, up to the last plinged argument of the
     * alias.  In other words, it's okay for an aliased function to have extra 
     * plinged arguments to the right of the last pling on the alias, but otherwise
     * they must match exactly.  Ex:
     * 
     *   alpha x y z = delta x y z;
     *   
     *   beta x !y z = delta x y z;
     *   
     *   gamma !x y z = delta x y z;
     *   
     *   delta x !y !z = ...;
     *   
     *   
     *   epsilon x y !z = zeta x y z;
     *   
     *   zeta !x y !z = ...;
     *   
     * In the above code, alpha has compatible strictness with delta, because
     * alpha doesn't have any plinged arguments.
     * 
     * beta has compatible strictness with delta, because the first and second
     * arguments have the same plings; the third argument doesn't need to have
     * the same strictness because the second argument is beta's last plinged
     * argument.
     * 
     * gamma and delta do _not_ have compatible strictness, because gamma's 
     * first argument is plinged and delta's is not.
     * 
     * epsilon and zeta also don't have compatible strictness, because the
     * first argument's strictness does not match (which is important because
     * the third argument is epsilon's rightmost strict argument).
     * 
     * @param includeUnreferencedLetVariables When true, check for let definitions that
     *                                         aren't referenced.
     */
    public void dumpLintWarnings(ModuleFilter moduleFilter, QualifiedNameFilter functionFilter, boolean traceSkippedModulesAndFunctions,
                                 boolean includeUnplingedPrimitiveArgs, boolean includeRedundantLambdas, boolean includeUnusedPrivates, boolean includeMismatchedAliasPlings, boolean includeUnreferencedLetVariables);

    /**
     * Find all the occurrences of the specified ScopedEntityImpl.
     * @param targetEntity QualifiedName of the entity to find occurrences of
     * @param messageLogger Used to log messages describing conditions encountered
     *                       during searching (eg, unparseable module file encountered).
     *                       This argument must not be null.
     * @return List of the locations of each found reference
     * TODOJ The QualifiedName-based API is no longer used and may want to be deleted
     */
    public List<SearchResult> findAllOccurrences(QualifiedName targetEntity, CompilerMessageLogger messageLogger); 

    /**
     * Find all occurrences of ScopedEntities with an unqualifiedName of targetName.
     * in the workspace and return a list of SearchResults s of reference to such entities.
     * @param targetName String specifying the name of an entity or entities to search for.  
     *                    The name may be qualified or unqualified, and may or may not contain 
     *                    wildcards. If you know the non-wildcarded fully-qualified name of the 
     *                    entity or entities that you are interested in, it is better 
     *                    (ie, more efficient) to use the version of the function that accepts 
     *                    a QualifiedName.  
     * @param messageLogger Used to log messages describing conditions encountered
     *                       during searching (eg, unparseable module file encountered).
     *                       This argument must not be null.
     * @return List of the locations of each found reference
     */
    public List<SearchResult> findAllOccurrences(String targetName, CompilerMessageLogger messageLogger); 

    /**
     * Find all the references to the specified gem in the workspace and return a list
     * of SearchResults of reference to the gem.
     * @param targetGem QualifiedName of the gem to look for references to
     * @param messageLogger Used to log messages describing conditions encountered
     *                       during searching (eg, unparseable module file encountered).
     *                       This argument must not be null.
     * @return List of the locations of each found reference
     * TODOJ The QualifiedName-based API is no longer used and may want to be deleted
     */
    public List<SearchResult> findReferences(QualifiedName targetGem, CompilerMessageLogger messageLogger); 

    /**
     * Find all the references to gems whose unqualified name is targetName
     * in the workspace and return a list of SearchResults of reference to such gems.
     * @param targetName String specifying the name of an gem or gems to search for.  
     *                    The name may be qualified or unqualified, and may or may not contain 
     *                    wildcards. If you know the non-wildcarded fully-qualified name of the 
     *                    gem or gems that you are interested in, it is better 
     *                    (ie, more efficient) to use the version of the function that accepts 
     *                    a QualifiedName.  
     * @param messageLogger Used to log messages describing conditions encountered
     *                       during searching (eg, unparseable module file encountered).
     *                       This argument must not be null.
     * @return List of the locations of each found reference
     */
    public List<SearchResult> findReferences(String targetName, CompilerMessageLogger messageLogger); 

    /**
     * Find all the instances of the specified type class and return a list of SearchResults
     * of the instances definitions.
     * @param targetGem QualifiedName of the gem to look for references to
     * @param messageLogger Used to log messages describing conditions encountered
     *                       during searching (eg, unparseable module file encountered).
     *                       This argument must not be null.
     * @return List of the locations of each found instance
     * TODOJ The QualifiedName-based API is no longer used and may want to be deleted
     */
    public List<SearchResult> findInstancesOfClass(QualifiedName targetGem, CompilerMessageLogger messageLogger); 

    /**
     * Find all the instances of type classes whose unqualified name is targetName
     * in the workspace and return a list of SearchResults of instances of such classes.
     * @param targetName String specifying the name of a class or classes to search for instances of.  
     *                    The name may be qualified or unqualified, and may or may not contain 
     *                    wildcards. If you know the non-wildcarded fully-qualified name of the 
     *                    entity or entities that you are interested in, it is better 
     *                    (ie, more efficient) to use the version of the function that accepts 
     *                    a QualifiedName.  
     * @param messageLogger Used to log messages describing conditions encountered
     *                       during searching (eg, unparseable module file encountered).
     *                       This argument must not be null.
     * @return List of the locations of each found reference
     */
    public List<SearchResult> findInstancesOfClass(String targetName, CompilerMessageLogger messageLogger); 

    /**
     * Find all the instances associated with the specified type (i.e., find all of the type classes of
     * which the specified type is an instance) and return a list of SearchResults of the relevant 
     * instance definitions.
     * @param targetGem QualifiedName of the gem to look for references to
     * @param messageLogger Used to log messages describing conditions encountered
     *                       during searching (eg, unparseable module file encountered).
     *                       This argument must not be null.
     * @return List of the locations of each found instance
     * TODOJ The QualifiedName-based API is no longer used and may want to be deleted
     */
    public List<SearchResult> findTypeInstances(QualifiedName targetGem, CompilerMessageLogger messageLogger); 

    /**
     * Find all the instances associated with the types whose unqualified name
     * is targetName.
     * @param targetName String specifying the name of a type or types to search for instances of.  
     *                    The name may be qualified or unqualified, and may or may not contain 
     *                    wildcards. If you know the non-wildcarded fully-qualified name of the 
     *                    entity or entities that you are interested in, it is better 
     *                    (ie, more efficient) to use the version of the function that accepts 
     *                    a QualifiedName.  
     * @param messageLogger Used to log messages describing conditions encountered
     *                       during searching (eg, unparseable module file encountered).
     *                       This argument must not be null.
     * @return List of the locations of each found instance
     */
    public List<SearchResult> findTypeInstances(String targetName, CompilerMessageLogger messageLogger); 

    /**
     * Find the position at which the specified typeclass, type, data constructor, function, or method was defined.
     * @param targetGem QualifiedName of the gem to look for references to
     * @param messageLogger Used to log messages describing conditions encountered
     *                       during searching (eg, unparseable module file encountered).
     *                       This argument must not be null.
     * @return List of the found definitions.  Generally this will contain either 0 or 1 elements.
     * TODOJ The QualifiedName-based API is no longer used and may want to be deleted
     */
    public List<SearchResult> findDefinition(Name targetGem, CompilerMessageLogger messageLogger); 

    /**
     * Find the positions at which each typeclass, type, data constructor, function, or method
     * whose unqualified name is targetName was defined. 
     * @param targetName String specifying the name of an entity or entities to search for definition of.  
     *                    The name may be qualified or unqualified, and may or may not contain 
     *                    wildcards. If you know the non-wildcarded fully-qualified name of the 
     *                    entity or entities that you are interested in, it is better 
     *                    (ie, more efficient) to use the version of the function that accepts 
     *                    a QualifiedName.  
     * @param messageLogger Used to log messages describing conditions encountered
     *                       during searching (eg, unparseable module file encountered).
     *                       This argument must not be null.
     * @return List of the locations of each found instance
     */
    public List<SearchResult> findDefinition(String targetName, CompilerMessageLogger messageLogger);
    
    /**
     * Search for the definitions of the selected symbol if one is selected.
     * @param moduleName Name of the module to search
     * @param line The line number of the symbol to search for. The first line in the file is line number one.
     * @param column The column number of the symbol to search for. The first column in the line is column number one.
     * @return A SearchResult.Precise object for the definition of the selected symbol or null if not found.
     */
    public SearchResult findDefinition(ModuleName moduleName, int line, int column, CompilerMessageLogger messageLogger);
    
    /**
     * Finds the symbol at the specified line and column.
     * @param moduleName Name of the module to search
     * @param line The line number of the symbol to search for. The first line in the file is line number one.
     * @param column The column number of the symbol to search for. The first column in the line is column number one.
     * @param messageLogger
     * @return An array of SearchResult.Precise for the symbol at the specified line and column.
     */
    public SearchResult.Precise[] findSymbolAt(ModuleName moduleName, int line, int column, CompilerMessageLogger messageLogger);
    
    /**
     * If the specified target is a constructor then find all constructions of the specified constructor.
     * If the specified target is a type then final all constructions of any constructor that has the
     * given type.  
     * @param targetName QualifiedName of the constructor to look for constructions of 
     * or the type to look for constructions of its constructors. 
     * @param messageLogger Used to log messages describing conditions encountered
     *                       during searching (eg, unparseable module file encountered).
     *                       This argument must not be null.
     */
    public List<SearchResult> findConstructions(String targetName, CompilerMessageLogger messageLogger);
}
