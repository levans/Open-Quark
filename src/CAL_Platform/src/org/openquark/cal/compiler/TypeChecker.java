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
 * TypeChecker.java
 * Created: Mar 5, 2003Feb 19, 2003 at 12:15:01 PM
 * By: Raymond Cypher
 */

package org.openquark.cal.compiler;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openquark.cal.machine.Module;
import org.openquark.cal.machine.Program;
import org.openquark.util.Pair;


/**
 * This is the TypeChecker class/interface.  It is the public interface of
 * an object that clients use to do type checking.
 *
 * Created: Mar 5, 2003
 * @author rcypher (extracted an interface from an existing class)
 */
public interface TypeChecker {
    /**
     * Get the types of all free gem parts in a graph - free inputs, free outputs, and trees.
     * 
     * This generates a let expression which generates all free input and output types simultaneously when evaluated.  
     * Evaluation produces a type expression with arguments to the expression function yielding the types of the roots
     *  and free arguments in the graph.
     * 
     * @param rootNodes Set the set of rootNodes in the graph to evaluate
     * @param moduleName the name of the module in which the graph exists
     * @return A pair of two maps:
     *   (CompositionNode.CompositionArg -> TypeExpr) Map from argument to its type.
     *   (CompositionNode -> List (of TypeExpr)) map from rootNode to the derived types for its definition..
     *     The types in the List are: argument types first (in order), then output.
     * @throws TypeException
     */
    public Pair<Map<CompositionNode.CompositionArgument, TypeExpr>, Map<CompositionNode, List<TypeExpr>>> checkGraph(Set<? extends CompositionNode> rootNodes, ModuleName moduleName, CompilerMessageLogger logger) throws TypeException;

    /**
     * Assuming that the main module has already been type checked using the check method, this
     * methods checks if a new function definition is well typed. It uses the environment
     * already built up while checking the main module. This method is intended to be called by
     * tools, and so exceptions are suppressed. 
     *
     * @return TypeExpr type of the defined function, or null if it is not well-typed
     * @param function the adjunct source defining the function e.g. "add2 x = x + 2;"
     * @param functionModule the module in which the function should be considered to be defined
     */
    public TypeExpr checkFunction(AdjunctSource function, ModuleName functionModule, CompilerMessageLogger logger);

    /**
     * Get a TypeExpr from its string representation.
     * 
     * Note: any compiler messages held by the compiler will be lost.
     * 
     * @param typeString the string representation.
     * @param workingModule the module in which the type exists.
     * @return TypeExpr a TypeExpr representation of the string, or null if the string does not represent a valid type.
     */
    public TypeExpr getTypeFromString(String typeString, ModuleName workingModule, CompilerMessageLogger logger);

    /**
     * Return a TypeCheckInfo object that bundles information necessary for doing type checking
     * in a given module.
     * @param moduleName
     * @return TypeCheckInfo
     */
    public TypeCheckInfo getTypeCheckInfo (ModuleName moduleName);
    
    /**
     * Parse the code text and return the list of identifiers that occur within it. 
     * This method is used for the initial syntax checking and qualification of 
     * text within a code gem.
     *
     * The identifiers returned include free symbols i.e. the symbols in the expression that
     * are not defined within the expression itself (e.g. by a let definition). The free symbols can
     * be either qualified (Prelude.x, Prelude.Boolean) or unqualified (True, Eq, sin). Those that are
     * variables which do not successfully resolve to a top-level symbol must be arguments of the code
     * expression.   
     * 
     * The list is ordered by increasing source positions of identifiers.
     * 
     * Example: 
     *     " let x :: Boolean; x = and Prelude.True False; 
     *       in and x y ;" 
     *     Returns identifiers : Boolean, and, Prelude.True, False, and, y
     * 
     * Note: Any compiler messages held by the logger will be lost
     * 
     * @param codeGemBodyText the text of the body of the code gem i.e. what the user actually typed
     * @param moduleName the name of the module in which the code gem is considered to exist.
     * @param moduleNameResolver the module name resolver for the module in which the code gem is considered to exist.
     * @param logger the logger to use to log error messages.
     * @return List (SourceIdentifier) the name, type and position of identifiers encountered in 
     *  the expression. Returns null if the expression does not parse.
     */
    public List<SourceIdentifier> findIdentifiersInExpression(String codeGemBodyText, ModuleName moduleName, ModuleNameResolver moduleNameResolver, CompilerMessageLogger logger);

    /**
     * Parses the specified expression and returns a copy of the expression with any references to the renamed entity updated.
     * The changes may include renamings for references to the renamed symbol, and renamings for local variables if any 
     * conflict with the new name.
     * 
     * Example:
     *   Renaming "s" to "r" in the expression
     * 
     *   "let r  = 1.0;
     *        r2 = if (s r) then 2.0 else 0.0;
     *    in
     *       case x of
     *           (r, r3) -> and (s r) (r2 < r3);
     *       ;"
     * 
     *   Will return the following string:
     * 
     *   "let r3 = 1.0;
     *        r2 = if (r r3) then 2.0 else 0.0;
     *    in
     *      case x of
     *          (r4, r3) -> and (r r4) (r2 < r3);
     *      ;"
     * 
     * @param codeExpression The text of the expression to update
     * @param moduleName The name of the module that this expression belongs to
     * @param moduleNameResolver the module name resolver for the module to which this expression belongs.
     * @param qualificationMap The qualificationMap associated with this expression, or null if no such thing exists.
     * @param oldName old name of the identifier
     * @param newName new name of the identifier
     * @param category category of the identifier
     * @param logger message logger to hold any errors encountered
     * @return True if changes were made to the expression
     */
    public String calculateUpdatedCodeExpression (String codeExpression, ModuleName moduleName, ModuleNameResolver moduleNameResolver, CodeQualificationMap qualificationMap, QualifiedName oldName, QualifiedName newName, SourceIdentifier.Category category, CompilerMessageLogger logger);
    
    /**
     * A simple wrapper class to encapsulate the information needed to type check the text of a function.
     * @author Edward Lam
     */
    public final class TypeCheckInfo {
    
        private final ModuleName moduleName;
        private final TypeChecker typeChecker;
        private final Program program;

        /**
         * Constructor for a TypeCheckInfo
         * @param typeChecker the type checker
         * @param moduleName the module name
         * @param program
         */
        TypeCheckInfo(ModuleName moduleName, TypeChecker typeChecker, Program program) {
            if (moduleName == null || typeChecker == null || program == null) {
                throw new NullPointerException();
            }
            this.moduleName = moduleName;
            this.typeChecker = typeChecker;
            this.program = program;
        }

        /**
         * Returns the moduleName.
         * @return the module name
         */
        public ModuleName getModuleName() {
            return moduleName;
        }

        /**
         * Returns the typeChecker.
         * @return TypeChecker the type checker
         */
        public TypeChecker getTypeChecker() {
            return typeChecker;
        }
        
        /**
         * Get the ModuleTypeInfo for the current module in this info object.
         * @return ModuleTypeInfo
         */
        public final ModuleTypeInfo getModuleTypeInfo() {
            Module module = program.getModule(moduleName);
            if (module == null) {
                return null;
            }
            return module.getModuleTypeInfo();
        }
    }
}
