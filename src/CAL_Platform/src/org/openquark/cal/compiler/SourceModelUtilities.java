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
 * SourceModelUtilities.java
 * Created: Dec 17, 2004
 * By: Peter Cardwell
 */

package org.openquark.cal.compiler;

import java.io.StringReader;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.openquark.cal.compiler.CompilerMessage.AbortCompilation;
import org.openquark.cal.compiler.SourceModel.CALDoc;
import org.openquark.cal.compiler.SourceModel.Import;
import org.openquark.cal.compiler.SourceModel.ModuleDefn;
import org.openquark.cal.compiler.SourceModel.Name;
import org.openquark.cal.compiler.SourceModel.TopLevelSourceElement;
import org.openquark.cal.compiler.SourceModel.TypeConstructorDefn;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;


/**
 * A collection of helper classes to supplement the functionality provided by the source model class.
 * 
 * @author Peter Cardwell
 */
public class SourceModelUtilities {
    
    /**
     * Represents an enumeration data type in cal. 
     * 
     * An Enumeration is a non-polymorphic type containing a
     * list of constructors with 0-arity. Data types generated 
     * using this class are made derived instances of the Prelude type classes Eq, Ord 
     * Enum, and Bounded. 
     * 
     * @author Peter Cardwell
     */
    public final static class Enumeration {
                
        /** the type constructor name */
        private final QualifiedName name;
        
        /** the scope of the type constructor and all of its data constructors */
        private final Scope scope;        
        
        /** an array of 0-arity data constructor names defining the enumeration values. */
        private final String[] enumValues;
        
        /** whether to generate a derived Eq instance only, or the full complement of derived instances (Eq, Ord, Bounded, Enum). */
        private final boolean eqInstanceOnly;
        
        /**
         * Constructor for an Enumeration. 
         * 
         * @param name The name of the enumeration data type (type constructor).
         * @param scope Determines whether the data type is public or private to the module it is in.
         * @param enumValues (List of String) The different possible values that the enumeration can take on.
         * @param eqInstanceOnly Determines whether only a derived Eq instance is to be generated,
         *        or the full complement of derived instances (Eq, Ord, Bounded, Enum).
         */
        public Enumeration (QualifiedName name, Scope scope, String[] enumValues, boolean eqInstanceOnly) {
            if (!LanguageInfo.isValidDataConstructorName(name.getUnqualifiedName())) {
                throw new IllegalArgumentException();
            }
            
            if (scope == null) {
                throw new NullPointerException("The argument 'scope' cannot be null.");
            }
            
            if (enumValues == null || enumValues.length < 1) {
                throw new IllegalArgumentException("argument 'enumValues' must have length >= 1.");
            }
            
            this.name = name;
            this.scope = scope;
            this.enumValues = enumValues;
            this.eqInstanceOnly = eqInstanceOnly;
        }
        
        /**
         * Creates an array of source elements that represent this enumeration type. 
         * @return TopLevelSourceElement[] 
         */
        public TopLevelSourceElement[] toSourceElements() {
            return new TopLevelSourceElement[] {          
                // Add data constructor
                getTypeConstructorDefn()
            };
        }

        /**
         * Returns the type constructor definition defining this enumeration
         * 
         * Sample:
         * data public {name} = 
         *     public {enumValues[0]} | 
         *     public {enumValues[1]} | 
         *     ... 
         *     public {enumValues[n-1]} 
         *     deriving Prelude.Eq, Prelude.Ord, Prelude.Bounded, Prelude.Enum;
         */
        private TypeConstructorDefn getTypeConstructorDefn() {
            TypeConstructorDefn.AlgebraicType.DataConsDefn[] dcDefns = new TypeConstructorDefn.AlgebraicType.DataConsDefn[enumValues.length];
            for (int i = 0, n = enumValues.length; i < n; i++) {
                dcDefns[i] = TypeConstructorDefn.AlgebraicType.DataConsDefn.make(enumValues[i], scope, null);                
            }
            
            Name.TypeClass[] derivingClauseTypeClassNames;
            
            if (eqInstanceOnly) {
                // if eqInstanceOnly is true, then we only generate the derived Eq instance
                derivingClauseTypeClassNames = new Name.TypeClass[] {
                    Name.TypeClass.make(CAL_Prelude.TypeClasses.Eq)
                };
            } else {
                // otherwise, we generate the full complement of derived instances
                derivingClauseTypeClassNames = new Name.TypeClass[] {
                    Name.TypeClass.make(CAL_Prelude.TypeClasses.Eq),
                    Name.TypeClass.make(CAL_Prelude.TypeClasses.Ord),
                    Name.TypeClass.make(CAL_Prelude.TypeClasses.Bounded),
                    Name.TypeClass.make(CAL_Prelude.TypeClasses.Enum)
                };
            }
            
            TypeConstructorDefn typeConstructorDefn = TypeConstructorDefn.AlgebraicType.make(
                name.getUnqualifiedName(), scope, null, dcDefns, derivingClauseTypeClassNames);
            
            return typeConstructorDefn;
        }
    }
    
    /**
     * Encapsulates a collection of helper methods for converting CAL source
     * from text form to source model form.
     * 
     * @author Joseph Wong
     */
    public static final class TextParsing {

        /**
         * Helper method to convert a string containing a CAL expression into the
         * corresponding source model. Exceptions thrown by the compiler are caught
         * by this method, and it returns null when the parsing fails.
         * 
         * @param exprStr
         *            the string containing the CAL expression
         * @return the corresponding source model, or null if the compiler cannot
         *         parse the supplied argument as an expression
         */
        public static final SourceModel.Expr parseExprIntoSourceModel(String exprStr) {
            return parseExprIntoSourceModel(exprStr, new MessageLogger());
        }

        /**
         * Helper method to convert a string containing a CAL expression into the
         * corresponding source model. Exceptions thrown by the compiler are caught
         * by this method, and it returns null when the parsing fails.
         * 
         * @param exprStr
         *            the string containing the CAL expression
         * @param aLogger
         *            the message logger for logging any parser errors
         * @return the corresponding source model, or null if the compiler cannot
         *         parse the supplied argument as an expression
         */
        public static final SourceModel.Expr parseExprIntoSourceModel(String exprStr, CompilerMessageLogger aLogger) {
        
            CALCompiler compiler = new CALCompiler();
            
            // Use a compiler-specific logger, so that fatal errors abort compilation.
            CompilerMessageLogger compileLogger = new MessageLogger(true);
            compiler.setCompilerMessageLogger(compileLogger);
            
            CALParser parser = freshParser(compiler, new StringReader(exprStr), null);
            CALTreeParser treeParser = new CALTreeParser(compiler);
            try {
                try {
                    parser.expr(null, null);
                    ParseTreeNode exprNode = (ParseTreeNode)parser.getAST();

                    //only invoke the tree parser if there are no errors when parsing the module. Otherwise the
                    //tree parser is sure to fail (in a fatal error) and so there is no point doing this.
                    if (exprNode != null && compileLogger.getNErrors() == 0) {
                        treeParser.expr(exprNode);

                        return SourceModelBuilder.buildExpr(exprNode);
                    }

                } catch (antlr.RecognitionException e) {           
                    // Recognition (syntax) error
                    final SourceRange sourceRange = CALParser.makeSourceRangeFromException(e);
                    compileLogger.logMessage(new CompilerMessage(sourceRange, new MessageKind.Error.SyntaxError(), e));
                
                } catch (antlr.TokenStreamException e) {
                    // Bad token stream.  Maybe a bad token (eg. a stray "$" sign)
                    compileLogger.logMessage(new CompilerMessage(new MessageKind.Error.BadTokenStream(), e));
                }
            
            } catch (AbortCompilation e) {
                //compilation aborted    
                             
            } catch (Exception e) {
                // Major failure - internal coding error
                try {                
                    if (compileLogger.getNErrors() > 0 || e instanceof UnableToResolveForeignEntityException) {

                        // If the exception is an UnableToResolveForeignEntityException, there is
                        // a CompilerMessage inside that we should be logging.
                        if (e instanceof UnableToResolveForeignEntityException) {
                            try {
                                compileLogger.logMessage(((UnableToResolveForeignEntityException)e).getCompilerMessage());
                            } catch (AbortCompilation ace) {
                                //logMessage can throw a AbortCompilation if a FATAL message was sent.
                            }
                        }
                        
                        //if an error occurred previously, we continue to compile the program to try to report additional
                        //meaningful compilation errors. However, this can produce spurious exceptions related to the fact
                        //that the program state does not satisfy preconditions because of the initial error(s). We don't
                        //report the spurious exception as an internal coding error.
                        compileLogger.logMessage(new CompilerMessage(new MessageKind.Info.UnableToRecover()));
                    } else {
                        compileLogger.logMessage(new CompilerMessage(new MessageKind.Fatal.InternalCodingError(), e));
                    }
                } catch (AbortCompilation ace) {
                    // Yeah, yeah, we know
                    //raiseError will throw a AbortCompilation since a FATAL message was sent.
                }
            } finally {
                // Copy messages to the passed-in logger.
                aLogger.logMessages(compileLogger);
            }
            
            return null;
        }

        /**
         * Helper method to convert a string containing a CAL algebraic function
         * definition into the corresponding source model. Exceptions thrown by the
         * compiler are caught by this method, and it returns null when the parsing
         * fails.
         * 
         * @param funcDefnStr
         *            the string containing the CAL algebraic function definition
         * @return the corresponding source model, or null if the compiler cannot
         *         parse the supplied argument as an algebraic function definition
         */
        public static final SourceModel.FunctionDefn parseAlgebraicFunctionDefnIntoSourceModel(String funcDefnStr) {
            return parseAlgebraicFunctionDefnIntoSourceModel(funcDefnStr, new MessageLogger());
        }

        /**
         * Helper method to convert a string containing a CAL algebraic function
         * definition into the corresponding source model. Exceptions thrown by the
         * compiler are caught by this method, and it returns null when the parsing
         * fails.
         * 
         * @param funcDefnStr
         *            the string containing the CAL algebraic function definition
         * @param aLogger
         *            the message logger for logging any parser errors
         * @return the corresponding source model, or null if the compiler cannot
         *         parse the supplied argument as an algebraic function definition
         */
        public static final SourceModel.FunctionDefn parseAlgebraicFunctionDefnIntoSourceModel(String funcDefnStr, CompilerMessageLogger aLogger) {
        
            CALCompiler compiler = new CALCompiler();
            
            // Use a compiler-specific logger, so that fatal errors abort compilation.
            CompilerMessageLogger compileLogger = new MessageLogger(true);
            compiler.setCompilerMessageLogger(compileLogger);
            
            CALParser parser = freshParser(compiler, new StringReader(funcDefnStr), null);
            CALTreeParser treeParser = new CALTreeParser(compiler);
            try {
                try {
                    parser.topLevelFunction(null, null);
                    ParseTreeNode funcDefnNode = (ParseTreeNode)parser.getAST();

                    //only invoke the tree parser if there are no errors when parsing the module. Otherwise the
                    //tree parser is sure to fail (in a fatal error) and so there is no point doing this.
                    if (funcDefnNode != null && compileLogger.getNErrors() == 0) {
                        treeParser.topLevelFunction(funcDefnNode);

                        return SourceModelBuilder.buildAlgebraicFunctionDefn(funcDefnNode);
                    }

                } catch (antlr.RecognitionException e) {           
                    // Recognition (syntax) error
                    final SourceRange sourceRange = CALParser.makeSourceRangeFromException(e);
                    compileLogger.logMessage(new CompilerMessage(sourceRange, new MessageKind.Error.SyntaxError(), e));
                
                } catch (antlr.TokenStreamException e) {
                    // Bad token stream.  Maybe a bad token (eg. a stray "$" sign)
                    compileLogger.logMessage(new CompilerMessage(new MessageKind.Error.BadTokenStream(), e));
                }
            
            } catch (AbortCompilation e) {
                //compilation aborted    
                             
            } catch (Exception e) {
                // Major failure - internal coding error
                try {                
                    if (compileLogger.getNErrors() > 0 || e instanceof UnableToResolveForeignEntityException) {

                        // If the exception is an UnableToResolveForeignEntityException, there is
                        // a CompilerMessage inside that we should be logging.
                        if (e instanceof UnableToResolveForeignEntityException) {
                            try {
                                compileLogger.logMessage(((UnableToResolveForeignEntityException)e).getCompilerMessage());
                            } catch (AbortCompilation ace) {
                                //logMessage can throw a AbortCompilation if a FATAL message was sent.
                            }
                        }
                        
                        //if an error occurred previously, we continue to compile the program to try to report additional
                        //meaningful compilation errors. However, this can produce spurious exceptions related to the fact
                        //that the program state does not satisfy preconditions because of the initial error(s). We don't
                        //report the spurious exception as an internal coding error.
                        compileLogger.logMessage(new CompilerMessage(new MessageKind.Info.UnableToRecover()));
                    } else {
                        compileLogger.logMessage(new CompilerMessage(new MessageKind.Fatal.InternalCodingError(), e));
                    }
                } catch (AbortCompilation ace) {
                    // Yeah, yeah, we know
                    //raiseError will throw a AbortCompilation since a FATAL message was sent.
                }
            } finally {
                // Copy messages to the passed-in logger.
                aLogger.logMessages(compileLogger);
            }
            
            return null;
        }
        
        /**
         * Helper method to convert a string containing a CAL module definition
         * into the corresponding source model. Exceptions thrown by the
         * compiler are caught by this method, and it returns null when the
         * parsing fails.
         * 
         * @param moduleDefnStr
         *            the string containing the CAL module definition
         * @return the corresponding source model, or null if the compiler
         *         cannot parse the supplied argument as a module definition
         */
        public static final SourceModel.ModuleDefn parseModuleDefnIntoSourceModel(String moduleDefnStr) {
            return parseModuleDefnIntoSourceModel(moduleDefnStr, false, new MessageLogger());
        }

        /**
         * Helper method to convert a string containing a CAL module definition
         * into the corresponding source model. Exceptions thrown by the
         * compiler are caught by this method, and it returns null when the
         * parsing fails.
         * 
         * @param moduleDefnStr
         *            the string containing the CAL module definition
         * @param aLogger
         *            the message logger for logging any parser errors
         * @return the corresponding source model, or null if the compiler
         *         cannot parse the supplied argument as a module definition
         */
        public static final SourceModel.ModuleDefn parseModuleDefnIntoSourceModel(String moduleDefnStr, CompilerMessageLogger aLogger) {
            return parseModuleDefnIntoSourceModel(moduleDefnStr, false, aLogger);
        }
        
        public static final SourceModel.ModuleDefn parseModuleDefnIntoSourceModel(String moduleDefnStr, boolean ignoreErrors, CompilerMessageLogger aLogger) {
            return parseModuleDefnIntoSourceModel(moduleDefnStr, ignoreErrors, aLogger, null);
        
        }
        
        /**
         * Helper method to convert a string containing a CAL module definition
         * into the corresponding source model. Exceptions thrown by the
         * compiler are caught by this method, and it returns null when the
         * parsing fails.
         * 
         * @param moduleDefnStr
         *            the string containing the CAL module definition
         * @param ignoreErrors
         *            if set then whatever kind of model that can be built from the parse will be returned. 
         *            This is currently used by the CAL Eclipse UI in order to support traversal even under
         *            bad conditions.
         * @param aLogger
         *            the message logger for logging any parser errors
         * @param embellishments a collection to store source embellishments
         * @return the corresponding source model, or null if the compiler
         *         cannot parse the supplied argument as a module definition
         */
        public static final SourceModel.ModuleDefn parseModuleDefnIntoSourceModel(String moduleDefnStr, boolean ignoreErrors, CompilerMessageLogger aLogger, Collection<SourceEmbellishment> embellishments) {
            
            CALCompiler compiler = new CALCompiler();
            
            // Use a compiler-specific logger, so that fatal errors abort compilation.
            CompilerMessageLogger compileLogger = new MessageLogger(true);
            compiler.setCompilerMessageLogger(compileLogger);
            
            CALParser parser = freshParser(compiler, new StringReader(moduleDefnStr), embellishments);
            CALTreeParser treeParser = new CALTreeParser(compiler);
            try {
                try {
                    parser.module();
                    ParseTreeNode moduleDefnNode = (ParseTreeNode)parser.getAST();

                    //only invoke the tree parser if there are no errors when parsing the module. Otherwise the
                    //tree parser is sure to fail (in a fatal error) and so there is no point doing this.
                    if (moduleDefnNode != null && (compileLogger.getNErrors() == 0 || ignoreErrors)) {
                        treeParser.module(moduleDefnNode);

                        return SourceModelBuilder.buildModuleDefn(moduleDefnNode);
                    }

                } catch (antlr.RecognitionException e) {           
                    // Recognition (syntax) error
                    final SourceRange sourceRange = CALParser.makeSourceRangeFromException(e);
                    compileLogger.logMessage(new CompilerMessage(sourceRange, new MessageKind.Error.SyntaxError(), e));
                
                } catch (antlr.TokenStreamException e) {
                    // Bad token stream.  Maybe a bad token (eg. a stray "$" sign)
                    compileLogger.logMessage(new CompilerMessage(new MessageKind.Error.BadTokenStream(), e));
                }
            
            } catch (AbortCompilation e) {
                //compilation aborted    
                
            } catch (Exception e) {
                // Major failure - internal coding error
                try {                
                    if (compileLogger.getNErrors() > 0 || e instanceof UnableToResolveForeignEntityException) {

                        // If the exception is an UnableToResolveForeignEntityException, there is
                        // a CompilerMessage inside that we should be logging.
                        if (e instanceof UnableToResolveForeignEntityException) {
                            try {
                                compileLogger.logMessage(((UnableToResolveForeignEntityException)e).getCompilerMessage());
                            } catch (AbortCompilation ace) {
                                //logMessage can throw a AbortCompilation if a FATAL message was sent.
                            }
                        }
                        
                        //if an error occurred previously, we continue to compile the program to try to report additional
                        //meaningful compilation errors. However, this can produce spurious exceptions related to the fact
                        //that the program state does not satisfy preconditions because of the initial error(s). We don't
                        //report the spurious exception as an internal coding error.
                        compileLogger.logMessage(new CompilerMessage(new MessageKind.Info.UnableToRecover()));
                    } else {
                        compileLogger.logMessage(new CompilerMessage(new MessageKind.Fatal.InternalCodingError(), e));
                    }
                } catch (AbortCompilation ace) {
                    // Yeah, yeah, we know
                    //raiseError will throw a AbortCompilation since a FATAL message was sent.
                }
            
            } finally {
                aLogger.logMessages(compileLogger);
            }
            
            return null;
        }

        /**
         * Construct a new parser state from the given reader. The returned parser
         * will have its lexer and stream selector set, and will be configured for
         * ASTNodes of type ParseTreeNode. Note that tree nodes created by this
         * parser will not have any source name (filename) info.
         * 
         * @param compiler
         *            The compiler. This will be used for message logging and for
         *            parser access to its stream selector.
         * @param reader
         *            the reader from which to parse.
         * @param embellishmentStore 
         *             this is a collection to which all filtered tokens are added
         *             if it is null the tokens are simply discarded
         * @return CALParser a new parser configured for the given args.
         * (see CALTypeChecker.freshParser(CALCompiler, java.io.Reader) )
         */
        private static final CALParser freshParser(CALCompiler compiler, java.io.Reader reader, Collection<SourceEmbellishment> embellishmentStore) {
        
            // Make a multiplexed lexer
            CALMultiplexedLexer lexer = new CALMultiplexedLexer(compiler, reader, embellishmentStore);
            
            // Create a parser, it gets its tokens from the multiplexed lexer
            CALParser parser = new CALParser(compiler, lexer);
        
            String treeNodeClassName = ParseTreeNode.class.getName();
            parser.setASTNodeClass(treeNodeClassName);
        
            return parser;
        }
        
    }

    /**
     * Encapsulates a helper method to augment a module definition with import
     * declarations for additional modules referenced by fully qualified names
     * within the module definition.
     * 
     * @author Joseph Wong
     */
    public static final class ImportAugmenter {
        
        /**
         * Augments a module definition with import declarations for additional modules
         * referenced by fully qualified variable names within the module definition. This
         * method takes care to preserve any existing import declarations already appearing
         * within the module definition, especially those that have "using" clauses.
         * 
         * @param origModuleDefn the original module definition
         * @return the transformed module definition
         */
        public static final SourceModel.ModuleDefn augmentWithImports(SourceModel.ModuleDefn origModuleDefn) {
            
            SourceModel.Import[] origImports = origModuleDefn.getImportedModules();
            
            // First, we collect the existing imports of the module definition and place them
            // in a map, keyed by the imported module names. This is to facilitate the
            // identification of existing imports in the subsequent augmenting stage so as
            // to prevent a module from being imported twice, and also to prevent existing
            // "using" clauses from being clobbered.
            
            LinkedHashMap<ModuleName, Import> moduleNamesToImports = new LinkedHashMap<ModuleName, Import>();
            
            for (final Import origImport : origImports) {
                moduleNamesToImports.put(SourceModel.Name.Module.toModuleName(origImport.getImportedModuleName()), origImport);
            }
            
            // Run the module names extractor to retrieve a set of all the module names
            // appearing in fully qualified names within the module definition
            
            ModuleNamesExtractor extractor = new ModuleNamesExtractor();
            origModuleDefn.accept(extractor, null);
            
            Set<ModuleName> importsNeeded = extractor.unimportedModuleNames;
            
            // The Prelude module needs to be imported by all modules other than
            // the Prelude module itself. So add it to the set of imports needed.
            
            importsNeeded.add(CAL_Prelude.MODULE_NAME);
            
            // We need to make sure that we do not import the module that is being
            // defined by the module definition, so we remove the definition's module name
            // from the set of referenced module names.
            
            importsNeeded.remove(SourceModel.Name.Module.toModuleName(origModuleDefn.getModuleName()));
            
            // Now, we check each referenced module name. For any module that is not
            // already imported, create a new Import object for it (with no "using" clauses).
            
            boolean additionalImportsRequired = false;
            
            for (final ModuleName referencedModuleName : importsNeeded) {
                
                if (!moduleNamesToImports.containsKey(referencedModuleName)) {
                    
                    moduleNamesToImports.put(referencedModuleName, SourceModel.Import.make(referencedModuleName));
                    additionalImportsRequired = true;
                }
            }
            
            if (additionalImportsRequired) {
                
                // We have identified the need to add more import declarations, so
                // gather all the Import objects into a new array, and construct
                // a new SourceModel.ModuleDefn with it.
                
                SourceModel.Import[] newImports = new SourceModel.Import[moduleNamesToImports.size()];
                
                int index = 0;
                for (Iterator<Import> it = moduleNamesToImports.values().iterator(); it.hasNext(); ) {
                    newImports[index] = it.next();
                    index++;
                }
                
                return SourceModel.ModuleDefn.make(origModuleDefn.getCALDocComment(), origModuleDefn.getModuleName(), newImports, origModuleDefn.getTopLevelDefns());
                
            } else {
                
                // No new import declarations need to be added, so just return
                // the original module definition.
                
                return origModuleDefn;
            }
        }
        
        /**
         * Returns an array of Imports for modules that are referenced by fully qualified
         * variable names within the specified source model element, excluding the
         * specified targetModule. These are the imports that are required for
         * the code fragment represented by the source model to run in the context
         * of the target module.
         * 
         * @param targetModule the target module for the specified source model
         * @param element the source model element
         * @return an array of Imports for the modules required by the specified source model
         */
        public static final SourceModel.Import[] getRequiredImports(ModuleName targetModule, SourceModel.SourceElement element) {
            
            // Run the module names extractor to retrieve a set of all the module names
            // appearing in fully qualified names within the source model
            
            ModuleNamesExtractor extractor = new ModuleNamesExtractor();
            element.accept(extractor, null);
            
            Set<ModuleName> importsNeeded = extractor.unimportedModuleNames;
            
            // The Prelude module needs to be imported by all modules other than
            // the Prelude module itself. So add it to the set of imports needed.
            
            importsNeeded.add(CAL_Prelude.MODULE_NAME);
            
            // We need to make sure that we do not import the target module,
            // so we remove the target module name from the set of referenced module names.
            
            importsNeeded.remove(targetModule);
            
            // Gather all the Import objects into a new array
            
            SourceModel.Import[] newImports = new SourceModel.Import[importsNeeded.size()];
            
            int index = 0;
            for (final ModuleName moduleName : importsNeeded) {
                newImports[index] = SourceModel.Import.make(moduleName);
                index++;
            }
            
            return newImports;
        }

        /**
         * A helper visitor which traverses a source model and extracts all
         * unimported module names appearing in fully qualified names appearing within.
         * 
         * @author Joseph Wong
         */
        private static final class ModuleNamesExtractor extends SourceModelTraverser<Void, Void>  {

            /**
             * The module name resolver to use in resolving module names. This resolver is built up as
             * import statements are visited. If no import statements are traversed, then this resolver
             * is an empty resolver (where all module names are unknown).
             */
            private ModuleNameResolver moduleNameResolver = ModuleNameResolver.make(Collections.<ModuleName>emptySet());
            
            /**
             * The names of modules imported by import statements. This set is empty if no import statements are traversed.
             */
            private final Set<ModuleName> importedModuleNames = new HashSet<ModuleName>();
            
            /**
             * The set of unimported module names encountered during the visitation. A
             * TreeSet is used to allow iteration in alphabetical order.
             */
            private final SortedSet<ModuleName> unimportedModuleNames = new TreeSet<ModuleName>();
            
            /** Captures the imports in the module definition and construct a module name resolver based on them. */
            @Override
            public Void visit_ModuleDefn(ModuleDefn defn, Void arg) {
                
                defn.getModuleName().accept(this, arg);
                
                final int nImportedModules = defn.getNImportedModules();
                for (int i = 0; i < nImportedModules; i++) {
                    defn.getNthImportedModule(i).accept(this, arg);
                }
                
                final int nFriendModules = defn.getNFriendModules();
                for (int i = 0; i < nFriendModules; i++) {
                    defn.getNthFriendModule(i).accept(this, arg);
                }
                
                Set<ModuleName> visibleModuleNames = new HashSet<ModuleName>();
                visibleModuleNames.add(SourceModel.Name.Module.toModuleName(defn.getModuleName()));
                visibleModuleNames.addAll(importedModuleNames);
                
                moduleNameResolver = ModuleNameResolver.make(visibleModuleNames);

                // We check the module-level CALDoc comment only after the module name resolver has been built
                if (defn.getCALDocComment() != null) {
                    defn.getCALDocComment().accept(this, arg);
                }
                
                final int nTopLevelDefns = defn.getNTopLevelDefns();
                for (int i = 0; i < nTopLevelDefns; i++) {
                    defn.getNthTopLevelDefn(i).accept(this, arg);
                }
                
                return null;
            }
            
            /** Captures the imports in the module definition and construct a module name resolver based on them. */
            @Override
            public Void visit_Import(Import importStmt, Void arg) {
                importedModuleNames.add(SourceModel.Name.Module.toModuleName(importStmt.getImportedModuleName()));
                return super.visit_Import(importStmt, arg);
            }

            /** Records the module name portion of the name being visited. */
            private void visitName(Name.Qualifiable name) {
                ModuleName moduleName = SourceModel.Name.Module.maybeToModuleName(name.getModuleName()); // may be null
                if (moduleName != null) {
                    if (moduleNameResolver.resolve(moduleName).isUnknown()) {
                        unimportedModuleNames.add(moduleName);
                    }
                }
            }
            
            /** Records the module name portion of the name being visited. */
            @Override
            public Void visit_Name_DataCons(Name.DataCons dataConsName, Void arg) {
                visitName(dataConsName);
                return super.visit_Name_DataCons(dataConsName, arg);
            }
            
            /** Records the module name portion of the name being visited. */
            @Override
            public Void visit_Name_Function(Name.Function functionName, Void arg) {
                visitName(functionName);
                return super.visit_Name_Function(functionName, arg);
            }
            
            /** Records the module name portion of the name being visited. */
            @Override
            public Void visit_Name_TypeClass(Name.TypeClass typeClassName, Void arg) {
                visitName(typeClassName);
                return super.visit_Name_TypeClass(typeClassName, arg);
            }
            
            /** Records the module name portion of the name being visited. */
            @Override
            public Void visit_Name_TypeCons(Name.TypeCons typeConsName, Void arg) {
                visitName(typeConsName);
                return super.visit_Name_TypeCons(typeConsName, arg);
            }

            /** Records the module name portion of the name being visited. */
            @Override
            public Void visit_Name_WithoutContextCons(Name.WithoutContextCons withoutContextConsName, Void arg) {
                visitName(withoutContextConsName);
                return super.visit_Name_WithoutContextCons(withoutContextConsName, arg);
            }

            /** Stops the traversal into an unchecked CALDoc "@see" reference. */
            @Override
            public Void visit_CALDoc_CrossReference_DataCons(CALDoc.CrossReference.DataCons reference, Void arg) {
                if (reference.isChecked()) {
                    return super.visit_CALDoc_CrossReference_DataCons(reference, arg);
                } else {
                    return null;
                }
            }
            
            /** Stops the traversal into an unchecked CALDoc "@see" reference. */
            @Override
            public Void visit_CALDoc_CrossReference_Function(CALDoc.CrossReference.Function reference, Void arg) {
                if (reference.isChecked()) {
                    return super.visit_CALDoc_CrossReference_Function(reference, arg);
                } else {
                    return null;
                }
            }
            
            /** Stops the traversal into an unchecked CALDoc "@see" reference. */
            @Override
            public Void visit_CALDoc_CrossReference_Module(CALDoc.CrossReference.Module reference, Void arg) {
                if (reference.isChecked()) {
                    return super.visit_CALDoc_CrossReference_Module(reference, arg);
                } else {
                    return null;
                }
            }
            
            /** Stops the traversal into an unchecked CALDoc "@see" reference. */
            @Override
            public Void visit_CALDoc_CrossReference_TypeClass(CALDoc.CrossReference.TypeClass reference, Void arg) {
                if (reference.isChecked()) {
                    return super.visit_CALDoc_CrossReference_TypeClass(reference, arg);
                } else {
                    return null;
                }
            }
            
            /** Stops the traversal into an unchecked CALDoc "@see" reference. */
            @Override
            public Void visit_CALDoc_CrossReference_TypeCons(CALDoc.CrossReference.TypeCons reference, Void arg) {
                if (reference.isChecked()) {
                    return super.visit_CALDoc_CrossReference_TypeCons(reference, arg);
                } else {
                    return null;
                }
            }
            
            /**
             * Stops the traversal into <em>all</em> CALDoc "@see" reference, because it cannot be determined
             * whether a reference A.B refers to entity B in module A or the module A.B.
             */
            @Override
            public Void visit_CALDoc_CrossReference_WithoutContextCons(CALDoc.CrossReference.WithoutContextCons reference, Void arg) {
                return null;
            }
        }
    }
    
    /**
     * Encapsulates the helper method {@link SourceModelUtilities.UnnecessaryQualificationRemover#removeUnnecessaryQualifications}
     * for removing unnecessary qualifications from qualified names in a source model, based on the visibility in a particular
     * context module.
     * 
     * Note that this utility does not remove qualification for qualified names appearing within see/link CALDoc blocks without
     * a 'context' keyword (aka short form see/link blocks).
     *
     * @author Joseph Wong
     */
    public static final class UnnecessaryQualificationRemover extends SourceModelCopier<Void> {
        
        /** The module type info of the context module (which determines the visibility of names). */
        private final ModuleTypeInfo contextModuleTypeInfo;
        
        /**
         * Private constructor for UnnecessaryQualificationRemover.
         * @param contextModuleTypeInfo the module type info of the context module (which determines the visibility of names).
         */
        private UnnecessaryQualificationRemover(ModuleTypeInfo contextModuleTypeInfo) {
            this.contextModuleTypeInfo = contextModuleTypeInfo;
        }
        
        /**
         * Removes unnecessary qualifications from qualified names in a source model, based on the visibility in a particular
         * context module.
         * 
         * @param contextModuleTypeInfo the module type info of the context module (which determines the visibility of names).
         * @param element the source model element on which the transformation is to be applied.
         * @return a copy of the source model, with unnecessary qualifications removed.
         */
        public static SourceModel.SourceElement removeUnnecessaryQualifications(ModuleTypeInfo contextModuleTypeInfo, SourceModel.SourceElement element) {
            return element.accept(new UnnecessaryQualificationRemover(contextModuleTypeInfo), null);
        }

        /**
         * @return a copy of the name, with the module name removed if qualification for this name is unnecessary.
         */
        @Override
        public Name.DataCons visit_Name_DataCons(Name.DataCons dataConsName, Void arg) {
            
            ModuleName rawModuleName = SourceModel.Name.Module.maybeToModuleName(dataConsName.getModuleName()); // may be null
            
            if (rawModuleName != null) {
                ModuleName resolvedModuleName = contextModuleTypeInfo.getModuleNameResolver().resolve(rawModuleName).getResolvedModuleName();
                
                String unqualifiedName = dataConsName.getUnqualifiedName();
                
                if (contextModuleTypeInfo.getModuleName().equals(resolvedModuleName)) {
                    return Name.DataCons.makeUnqualified(unqualifiedName);
                    
                } else {
                    ModuleName moduleOfUsingClauseOrNull = contextModuleTypeInfo.getModuleOfUsingDataConstructor(unqualifiedName);
                    
                    if (resolvedModuleName.equals(moduleOfUsingClauseOrNull)) {
                        return Name.DataCons.makeUnqualified(unqualifiedName);
                    }
                }
            }
            
            return super.visit_Name_DataCons(dataConsName, arg);
        }

        /**
         * @return a copy of the name, with the module name removed if qualification for this name is unnecessary.
         */
        @Override
        public Name.Function visit_Name_Function(Name.Function functionName, Void arg) {
            
            ModuleName rawModuleName = SourceModel.Name.Module.maybeToModuleName(functionName.getModuleName()); // may be null
            
            if (rawModuleName != null) {
                ModuleName resolvedModuleName = contextModuleTypeInfo.getModuleNameResolver().resolve(rawModuleName).getResolvedModuleName();
                
                String unqualifiedName = functionName.getUnqualifiedName();
                
                if (contextModuleTypeInfo.getModuleName().equals(resolvedModuleName)) {
                    return Name.Function.makeUnqualified(unqualifiedName);
                    
                } else {
                    ModuleName moduleOfUsingClauseOrNull = contextModuleTypeInfo.getModuleOfUsingFunctionOrClassMethod(unqualifiedName);
                    
                    if (resolvedModuleName.equals(moduleOfUsingClauseOrNull)) {
                        return Name.Function.makeUnqualified(unqualifiedName);
                    }
                }
            }
            
            return super.visit_Name_Function(functionName, arg);
        }

        /**
         * @return a copy of the name, with the module name removed if qualification for this name is unnecessary.
         */
        @Override
        public Name.TypeClass visit_Name_TypeClass(Name.TypeClass typeClassName, Void arg) {
            
            ModuleName rawModuleName = SourceModel.Name.Module.maybeToModuleName(typeClassName.getModuleName()); // may be null
            
            if (rawModuleName != null) {
                ModuleName resolvedModuleName = contextModuleTypeInfo.getModuleNameResolver().resolve(rawModuleName).getResolvedModuleName();
                
                String unqualifiedName = typeClassName.getUnqualifiedName();
                
                if (contextModuleTypeInfo.getModuleName().equals(resolvedModuleName)) {
                    return Name.TypeClass.makeUnqualified(unqualifiedName);
                    
                } else {
                    ModuleName moduleOfUsingClauseOrNull = contextModuleTypeInfo.getModuleOfUsingTypeClass(unqualifiedName);
                    
                    if (resolvedModuleName.equals(moduleOfUsingClauseOrNull)) {
                        return Name.TypeClass.makeUnqualified(unqualifiedName);
                    }
                }
            }
            
            return super.visit_Name_TypeClass(typeClassName, arg);
        }

        /**
         * @return a copy of the name, with the module name removed if qualification for this name is unnecessary.
         */
        @Override
        public Name.TypeCons visit_Name_TypeCons(Name.TypeCons typeConsName, Void arg) {
            
            ModuleName rawModuleName = SourceModel.Name.Module.maybeToModuleName(typeConsName.getModuleName()); // may be null
            
            if (rawModuleName != null) {
                ModuleName resolvedModuleName = contextModuleTypeInfo.getModuleNameResolver().resolve(rawModuleName).getResolvedModuleName();
                
                String unqualifiedName = typeConsName.getUnqualifiedName();
                
                if (contextModuleTypeInfo.getModuleName().equals(resolvedModuleName)) {
                    return Name.TypeCons.makeUnqualified(unqualifiedName);
                    
                } else {
                    ModuleName moduleOfUsingClauseOrNull = contextModuleTypeInfo.getModuleOfUsingTypeConstructor(unqualifiedName);
                    
                    if (resolvedModuleName.equals(moduleOfUsingClauseOrNull)) {
                        return Name.TypeCons.makeUnqualified(unqualifiedName);
                    }
                }
            }
            
            return super.visit_Name_TypeCons(typeConsName, arg);
        }
    }
}
