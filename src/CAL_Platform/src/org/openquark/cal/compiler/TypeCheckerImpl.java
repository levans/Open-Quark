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
 * TypeCheckerImpl.java
 * Created: Feb 19, 2003 at 12:15:01 PM
 * By: Raymond Cypher
 */
package org.openquark.cal.compiler;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openquark.cal.machine.CodeGenerator;
import org.openquark.cal.machine.Program;
import org.openquark.util.Pair;


/**
 * Warning- this class should only be used by the CAL compiler implementation. It is not part of the
 * external API of the CAL platform.
 * <p>
 * This is the TypeCheckerImpl class.
 *
 * This class is used to type-check CAL functions and composition node graphs. 
 * <p>
 * Created: Feb 19, 2003 at 12:15:01 PM
 * @author Raymond Cypher
 */
public abstract class TypeCheckerImpl implements TypeChecker {

    private final CALCompiler compiler;

    private final Program program;

    /**
     * Constructor for TypeCheckerImpl.
     * @param program
     */
    protected TypeCheckerImpl(Program program) {
        compiler = new CALCompiler();

        if (program == null) {
            throw new NullPointerException("Argument 'program' cannot be null.");
        }
        this.program = program;
        
        // makes sure that the CALCompiler has a copy of the packager with the program
        // from the very beginning
        compiler.setPackager(makePackager(program));
    }
    
    abstract protected Packager makePackager(Program program);
    
    abstract protected CodeGenerator makeCodeGenerator();

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
     * Note: Any compiler messages held by the logger will be lost.
     * 
     * @param codeGemBodyText the text of the body of the code gem i.e. what the user actually typed
     * @param moduleName the name of the module in which the code gem is considered to exist.
     * @param logger logger of compiler messages
     * @return List (SourceIdentifier) the name, type and position of identifiers encountered in 
     *  the expression. Returns null if the expression does not parse.
     */
    public List<SourceIdentifier> findIdentifiersInExpression(String codeGemBodyText, ModuleName moduleName, ModuleNameResolver moduleNameResolver, CompilerMessageLogger logger) {

        compiler.setCompilerMessageLogger(logger);

        List<SourceIdentifier> identifiers = compiler.findIdentifiersInExpression(codeGemBodyText, moduleName, moduleNameResolver);
        compiler.setCompilerMessageLogger(null);
        return identifiers;
    }
    
    /**
     * Parse the specified module and return all identifier references which need to be affected
     * in order to rename the specified top-level identifier without conflicts.
     * 
     * The list of objects returned (RenameData) indicates the changes which must occur in order 
     * to properly rename the specified symbol (ie: the results will include renamings for references 
     * to the requested symbol, and may include renamings for local variables if any conflict with the 
     * new name).
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
     *   Will return renamings to "r" for references to "s",
     *               renamings to "r4" for case variable "r" ("r2" is bound in let, "r3" bound in case)
     *               renamings to "r3" for let variable "r" ("r2" is bound in let)
     * 
     * Notes: The list returned is ordered by increasing source position of identifier names. 
     *        Any compiler messages held by this compiler will be lost
     * 
     * @param sourceDef source to parse
     * @param logger message logger to hold any errors encountered
     * @param oldName old name of the identifier
     * @param newName new name of the identifier
     * @param category category of the identifier
     * @return the name and position of identifiers encountered in 
     *  the expression. Returns null if the expression does not parse.
     */
    List<SourceModification> findRenamingsInModule(ModuleSourceDefinition sourceDef, CompilerMessageLogger logger, QualifiedName oldName, QualifiedName newName, SourceIdentifier.Category category) {
        compiler.setCompilerMessageLogger(logger);

        List<SourceModification> identifiers = compiler.findRenamingsInModule(sourceDef, oldName, newName, category);
        compiler.setCompilerMessageLogger(null);
        return identifiers;
    }
    
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
     * @param qualificationMap The qualificationMap associated with this expression, or null if no such thing exists.
     * @param oldName old name of the identifier
     * @param newName new name of the identifier
     * @param category category of the identifier
     * @param logger message logger to hold any errors encountered
     * @return True if changes were made to the expression
     */
    public String calculateUpdatedCodeExpression (String codeExpression, ModuleName moduleName, ModuleNameResolver moduleNameResolver, CodeQualificationMap qualificationMap, QualifiedName oldName, QualifiedName newName, SourceIdentifier.Category category, CompilerMessageLogger logger) {
        
        compiler.setCompilerMessageLogger(logger);
        if (logger == null) {
            logger = compiler.getMessageLogger();
        }
        int nOldErrors = logger.getNErrors();
        
        List<SourceModification> identifierPositions =
            compiler.findRenamingsInExpression(codeExpression, moduleName, moduleNameResolver, qualificationMap, oldName, newName, category);
        
        compiler.setCompilerMessageLogger(null);
        
        if (logger.getNErrors() > nOldErrors) {
            return codeExpression;
        
        } else if (identifierPositions == null) {
            logger.logMessage(new CompilerMessage(new MessageKind.Error.CouldNotParseExpressionInModule(moduleName)));
            return codeExpression;
        }
        
        if (identifierPositions.size() == 0) {
            // Nothing to rename in this expression
            return codeExpression;
        }
        
        SourceModifier renamer = new SourceModifier();
        for (final SourceModification sourceModification : identifierPositions) {
            renamer.addSourceModification(sourceModification);
        }
        
        return renamer.apply(codeExpression);        
    }
    
    /**
     * {@inheritDoc}
     */
    public Pair<Map<CompositionNode.CompositionArgument, TypeExpr>, Map<CompositionNode, List<TypeExpr>>> checkGraph(Set<? extends CompositionNode> rootNodes, ModuleName moduleName, CompilerMessageLogger logger) throws TypeException {
        compiler.setCompilerMessageLogger(logger);
        Pair<Map<CompositionNode.CompositionArgument, TypeExpr>, Map<CompositionNode, List<TypeExpr>>> retVal = compiler.getTypeChecker().checkGraph(rootNodes, moduleName);
        compiler.setCompilerMessageLogger(null);
        
        return retVal;
    }

    /**
     * {@inheritDoc}
     */
    public TypeExpr checkFunction(AdjunctSource scText, ModuleName scModule, CompilerMessageLogger logger) {
        compiler.setCompilerMessageLogger(logger);
        TypeExpr retVal = compiler.getTypeChecker().checkFunction(scText, scModule);
        compiler.setCompilerMessageLogger(null);

        return retVal;
    }

    /**
     * {@inheritDoc}
     */
    public TypeExpr getTypeFromString(String typeString, ModuleName workingModule, CompilerMessageLogger logger) {
        compiler.setCompilerMessageLogger(logger);
        TypeExpr retVal = compiler.getTypeChecker().getTypeFromString(typeString, workingModule);
        compiler.setCompilerMessageLogger(null);
        
        return retVal;
    }

    /**
     * {@inheritDoc}
     */
    public TypeCheckInfo getTypeCheckInfo(ModuleName moduleName) {
        return new TypeCheckInfo(moduleName, this, program);
    }

}
