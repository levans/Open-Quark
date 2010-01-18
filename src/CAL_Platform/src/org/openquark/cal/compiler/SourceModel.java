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
 * SourceModel.java
 * Created: Nov 18, 2004
 * By: Bo Ilic
 */

package org.openquark.cal.compiler;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import org.openquark.cal.compiler.SourceModel.CALDoc.TextSegment.TopLevel;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;



/**
 * Models syntactically valid CAL entities, in particular, a syntactically valid CAL module.
 * What this means (by definition) is that the source model's CAL text representation
 * is guaranteed to be parseable by CAL.  And conversely, anything that can be parsed by CAL
 * can be represented as a SourceModel.
 * <p>
 * 
 * SourceModel can be used to create a CAL module that does not need to be parsed, and can go directly
 * to later stages of compilation such as static analysis, type-checking, and code generation. This is
 * an efficiency benefit.
 * <p>
 * 
 * All parts of the source model are immutable. 
 * The reason for this is that we want to preserve the invariant that the models constructed produce
 * CAL source that is parseable.
 * <p>
 * 
 * As a general rule, all the methods below follow the following guidelines, except where explicitly commented:
 * <ul>
 *   <li> Arguments are explictly checked for non-nullness and array element arguments are checked for non-nullness.
 *   <li> Various names, such as for functions, data constructors etc. are checked for lexical validity e.g. start with 
 *        the appropriate case, not a CAL keyword etc. See the LanguageInfo class for how to ensure your names are OK.
 *   <li> Array arguments passed in during construction of some of the SourceModel objects are not copied, so the
 *        constructor of these arrays should not reset elements in them.
 * </ul>
 * <p>
 * 
 * The source model is useful for programmatically creating CAL modules or parts of modules.
 * 
 * @author Bo Ilic
 */
public abstract class SourceModel {
    
    /**
     * Base class for all pieces in the source model of CAL.
     * @author Bo Ilic
     */
    public static abstract class SourceElement {
        
        /** A SourceRange that specifies the range occupied by this source element in the
         * original source text.  This field may be null. 
         */
        private final SourceRange sourceRange;
        
        /**
         * SourceElement classes should all be defined within the SourceModel since we need to
         * guarantee that the resulting code fragments are parseable when toSourceText is called.         
         */
        private SourceElement() {
            sourceRange = null;
        }
        
        /**
         * SourceElement classes should all be defined within the SourceModel since we need to
         * guarantee that the resulting code fragments are parseable when toSourceText is called.         
         * @param sourceRange The position that this SourceElement begins at in the source text
         *                        for this module.
         */
        private SourceElement(SourceRange sourceRange) {
            this.sourceRange = sourceRange; 
        }
        
        @Override
        public final String toString() {
            return toSourceText();
        } 
        
        /**
         * Build a string representation of this source element
         * This is equivalent to toString.
         * @return the formatted source element
         */
        public final String toSourceText() {
            return SourceModelCodeFormatter.formatCode(this, SourceModelCodeFormatter.DEFAULT_OPTIONS, Collections.<SourceEmbellishment>emptyList());
        }        
        
        /**    
         * Build a string representation of this source element and append it
         * to the supplied string builder.    
         * @param stringBuilder StringBuilder accumulate the source text in this StringBuilder.         
         */
        public final void toSourceText(StringBuilder stringBuilder) {
            SourceModelCodeFormatter.formatCode(this, stringBuilder, SourceModelCodeFormatter.DEFAULT_OPTIONS, Collections.<SourceEmbellishment>emptyList());
        }
        
        /**
         * Converts a SourceModel representation to the internal ParseTreeNode representation.
         * SourceModelBuilder does the inverse operation.
         * The ParseTreeNode format is an internal format and this method must remain package scope.
         * The ParseTreeNode produces by this method (for example for the ModuleDefn) is guaranteed to
         * satisfy the tree grammar of CAL as specified in antlr grammar/CALTreeParser.g.
         * The main purpose of this method is to allow for the generation of programmatically
         * generated modules that bypass the parser for efficiency reasons.
         * 
         * @return A ParseTreeNode generated from this source element. 
         */
        abstract ParseTreeNode toParseTreeNode(); 
        
        
        /**
         * Accepts the visitation of a visitor, which implements the
         * SourceModelVisitor interface. This abstract method is to be overridden
         * by each concrete subclass so that the correct visit method on the
         * visitor may be called based upon the type of the element being
         * visited. Each concrete subclass of SourceElement should correspond
         * one-to-one with a visit method declaration in the SourceModelVisitor
         * interface.
         * <p>
         * 
         * As the SourceModelVisitor follows a more general visitor pattern
         * where arguments can be passed into the visit methods and return
         * values obtained from them, this method passes through the argument
         * into the visit method, and returns as its return value the return
         * value of the visit method.
         * <p>
         * 
         * Nonetheless, for a significant portion of the common cases, the state of the
         * visitation can simply be kept as member variables within the visitor itself,
         * thereby eliminating the need to use the argument and return value of the
         * visit methods. In these scenarios, the recommended approach is to use
         * {@link Void} as the type argument for both <code>T</code> and <code>R</code>, and
         * pass in null as the argument, and return null as the return value.
         * <p>
         * 
         * @see SourceModelVisitor
         * 
         * @param <T> the argument type. If the visitation argument is not used, specify {@link Void}.
         * @param <R> the return type. If the return value is not used, specify {@link Void}.
         * 
         * @param visitor
         *            the visitor
         * @param arg
         *            the argument to be passed to the visitor's visitXXX method
         * @return the return value of the visitor's visitXXX method
         */
        public abstract <T, R> R accept(SourceModelVisitor<T, R> visitor, T arg);
        
        /**
         * @return An optional SourcePosition specifying the start of this SourceElement in the
         *          corresponding source text.  This method may return null.
         */
        final SourcePosition getSourcePosition() {
            if(sourceRange != null) {
                return sourceRange.getStartSourcePosition();
            } else {
                return null;
            }
        }
        
        /**
         * @return An optional SourceRange specifying the range occupied by this source
         *          element in the original source text.  May return null.
         */
        // todo-jowong make this final again, when all the offending non-entire-range source ranges in subclasses are refactored
        SourceRange getSourceRange() {
            return sourceRange;
        }
        
        /**
         * Most of the source model elements return this as the getSourceRange but a few do not.
         * Since we are close to shipping I do not want to change the definition of getSourceRange
         * for these function due to possible unforeseen consequences. So I made a new function
         * that specifies the source range scope more explicitly.
         * 
         * @return the source range of the entire statement excluding the CAL doc
         */
        // todo-jowong this should refactored, so that getSourceRange is *always* the source range of the definition
        SourceRange getSourceRangeOfDefn(){
            return getSourceRange();
        }

    }
    
    /**
     * Base class for all source elements that can appear anywhere in the top level of a module.
     * This includes type constructor definitions, type class definitions, instance definitions,
     * function definitions and function type declarations. It does NOT include import declarations,
     * since these are a special type of declaration in that they must be at the top of the module and
     * describe an attribute of the module rather than forming part of the module definition. 
     * @author Peter Cardwell
     */
    public static abstract class TopLevelSourceElement extends SourceElement {
        private TopLevelSourceElement() {}
        
        /** 
         * A version of the constructor that passes along a sourceRange to the SourceElement constructor. 
         * @param sourceRange
         */
        private TopLevelSourceElement(SourceRange sourceRange) {
            super(sourceRange);
        }
    }
    
    /**
     * Models a CAL module, with its various constituents:
     * 
     * module imports
     *
     * type constructor definitions:
     *     data type definitions (i.e. "data declarations")
     *     foreign data type definitions (i.e. "foreign data declarations")
     *
     * type class definitions
     * 
     * (class) instance definitions
     *         
     * function definitions
     *     algebraic function definition
     *     foreign function definitions
     *     primitive function definitions
     *     
     * function type declarations
     *  
     * @author Bo Ilic
     */    
    public static final class ModuleDefn extends SourceElement {
        
        /** The CALDoc comment associated with this module definition, or null if there is none. */
        private final CALDoc.Comment.Module caldocComment;
        
        /** The name of the module. */
        private final Name.Module moduleName;
        
        /** External modules imported by this module */
        private final Import[] importedModules;
        public static final Import[] NO_IMPORTED_MODULES = new Import[0];
        
        /** Friend modules to this module */
        private final Friend[] friendModules;
        public static final Friend[] NO_FRIEND_MODULES = new Friend[0];
        
        /** Top-level definitions in the module (not including imports) */
        private final TopLevelSourceElement[] topLevelDefns;             
        public static final TopLevelSourceElement[] NO_TOP_LEVEL_DEFNS = new TopLevelSourceElement[0];
        
        private ModuleDefn(CALDoc.Comment.Module caldocComment, Name.Module moduleName, Import[] importedModules, Friend[] friendModules, TopLevelSourceElement[] topLevelDefns, SourceRange sourceRange) {
            super(sourceRange);
            
            this.caldocComment = caldocComment;

            verifyArg(moduleName, "moduleName");
            this.moduleName = moduleName; 
            
            if (importedModules == null || importedModules.length == 0) {
                this.importedModules = NO_IMPORTED_MODULES;
            } else {
                this.importedModules = importedModules.clone();
                verifyArrayArg(this.importedModules, "importedModules");                                       
            }
            
            if (friendModules == null || friendModules.length == 0) {
                this.friendModules = NO_FRIEND_MODULES;
            } else {
                this.friendModules = friendModules.clone();
                verifyArrayArg(this.friendModules, "friendModules");
            }
            
            if (topLevelDefns == null || topLevelDefns.length == 0) {
                this.topLevelDefns = NO_TOP_LEVEL_DEFNS;
            } else {
                this.topLevelDefns = topLevelDefns.clone();
                verifyArrayArg(this.topLevelDefns, "topLevelDefns");                                       
            }
        }
        
        /**
         * Create an instance of ModuleDefn without an associated CALDoc comment.
         * @param moduleName the name of the module.
         * @param importedModules external modules imported by this module.
         * @param topLevelDefns top-level definitions in the module.
         * @return an instance of ModuleDefn without an associated CALDoc comment.
         */
        public static ModuleDefn make(Name.Module moduleName, Import[] importedModules, TopLevelSourceElement[] topLevelDefns) {
            return new ModuleDefn(null, moduleName, importedModules, null, topLevelDefns, null);
        }
        
        /**
         * Create an instance of ModuleDefn without an associated CALDoc comment.
         * @param moduleName the name of the module.
         * @param importedModules external modules imported by this module.
         * @param topLevelDefns top-level definitions in the module.
         * @return an instance of ModuleDefn without an associated CALDoc comment.
         */
        public static ModuleDefn make(ModuleName moduleName, Import[] importedModules, TopLevelSourceElement[] topLevelDefns) {
            return new ModuleDefn(null, Name.Module.make(moduleName), importedModules, null, topLevelDefns, null);
        }
        
        /**
         * Create an instance of ModuleDefn with an associated CALDoc comment.
         * @param caldocComment the associated CALDoc comment. May be null if there is no CALDoc comment.
         * @param moduleName the name of the module.
         * @param importedModules external modules imported by this module.
         * @param topLevelDefns top-level definitions in the module.
         * @return an instance of ModuleDefn with an associated CALDoc comment.
         */
        public static ModuleDefn make(CALDoc.Comment.Module caldocComment, Name.Module moduleName, Import[] importedModules, TopLevelSourceElement[] topLevelDefns) {
            return new ModuleDefn(caldocComment, moduleName, importedModules, null, topLevelDefns, null);
        }
        
        /**
         * Create an instance of ModuleDefn with an associated CALDoc comment.
         * @param caldocComment the associated CALDoc comment. May be null if there is no CALDoc comment.
         * @param moduleName the name of the module.
         * @param importedModules external modules imported by this module.
         * @param topLevelDefns top-level definitions in the module.
         * @return an instance of ModuleDefn with an associated CALDoc comment.
         */
        public static ModuleDefn make(CALDoc.Comment.Module caldocComment, ModuleName moduleName, Import[] importedModules, TopLevelSourceElement[] topLevelDefns) {
            return new ModuleDefn(caldocComment, Name.Module.make(moduleName), importedModules, null, topLevelDefns, null);
        }
        
        /**
         * Create an instance of ModuleDefn with an associated CALDoc comment.
         * @param caldocComment the associated CALDoc comment. May be null if there is no CALDoc comment.
         * @param moduleName the name of the module.
         * @param importedModules external modules imported by this module.
         * @param friendModules friend modules of this module.
         * @param topLevelDefns top-level definitions in the module.
         * @return an instance of ModuleDefn with an associated CALDoc comment.
         */
        public static ModuleDefn make(CALDoc.Comment.Module caldocComment, Name.Module moduleName, Import[] importedModules, Friend[] friendModules, TopLevelSourceElement[] topLevelDefns) {
            return new ModuleDefn(caldocComment, moduleName, importedModules, friendModules, topLevelDefns, null);
        }

        /**
         * Create an instance of ModuleDefn with an associated CALDoc comment.
         * @param caldocComment the associated CALDoc comment. May be null if there is no CALDoc comment.
         * @param moduleName the name of the module.
         * @param importedModules external modules imported by this module.
         * @param friendModules friend modules of this module.
         * @param topLevelDefns top-level definitions in the module.
         * @return an instance of ModuleDefn with an associated CALDoc comment.
         */
        public static ModuleDefn make(CALDoc.Comment.Module caldocComment, ModuleName moduleName, Import[] importedModules, Friend[] friendModules, TopLevelSourceElement[] topLevelDefns) {
            return new ModuleDefn(caldocComment, Name.Module.make(moduleName), importedModules, friendModules, topLevelDefns, null);
        }

        static ModuleDefn makeAnnotated(CALDoc.Comment.Module caldocComment, Name.Module moduleName, Import[] importedModules, Friend[] friendModules, TopLevelSourceElement[] topLevelDefns, SourceRange sourceRange) {
            return new ModuleDefn(caldocComment, moduleName, importedModules, friendModules, topLevelDefns, sourceRange);
        }
        
        /**
         * @return the CALDoc comment associated with this module definition, or null if there is none.
         */
        public CALDoc.Comment.Module getCALDocComment() {
            return caldocComment;
        }
        
        /**
         * @return the name of the module.
         */
        public Name.Module getModuleName() {
            return moduleName;        
        }
        
        /**
         * @deprecated this is deprecated to signify that the returned source range is not the entire definition, but
         * rather a portion thereof.
         */
        // todo-jowong refactor this so that the primary source range of the source element is its entire source range
        @Deprecated
        SourceRange getSourceRange() {
            return getSourceRangeOfModuleName();
        }

        SourceRange getSourceRangeOfModuleName() {
            return super.getSourceRange();
        }
        
                  
        /** {@inheritDoc} */
        @Override
        ParseTreeNode toParseTreeNode() {
            
            ParseTreeNode moduleDefnNode = new ParseTreeNode (CALTreeParserTokenTypes.MODULE_DEFN, "MODULE_DEFN");
            ParseTreeNode optionalCALDocNode = makeOptionalCALDocNode(caldocComment);
            ParseTreeNode moduleNameNode = moduleName.toParseTreeNode();
            ParseTreeNode importDeclarationListNode = new ParseTreeNode(CALTreeParserTokenTypes.IMPORT_DECLARATION_LIST, "IMPORT_DECLARATION_LIST");
            ParseTreeNode friendDeclarationListNode = new ParseTreeNode(CALTreeParserTokenTypes.FRIEND_DECLARATION_LIST, "FRIEND_DECLARATION_LIST");
            ParseTreeNode outerDefnListNode = new ParseTreeNode(CALTreeParserTokenTypes.OUTER_DEFN_LIST, "OUTER_DEFN_LIST");
            
            ParseTreeNode[] importedModuleNodes = new ParseTreeNode[importedModules.length];        
            for (int i = 0; i < importedModules.length; i++) {
                importedModuleNodes[i] = importedModules[i].toParseTreeNode();                
            }
            importDeclarationListNode.addChildren(importedModuleNodes);
            
            ParseTreeNode[] friendModuleNodes = new ParseTreeNode[friendModules.length];
            for (int i = 0; i < friendModules.length; ++i) {
                friendModuleNodes[i] = friendModules[i].toParseTreeNode();                
            }
            friendDeclarationListNode.addChildren(friendModuleNodes);
            
            ParseTreeNode[] outerDefnNodes = new ParseTreeNode[topLevelDefns.length];            
            for (int i = 0; i < topLevelDefns.length; i++) {
                outerDefnNodes[i] = topLevelDefns[i].toParseTreeNode();
            }
            outerDefnListNode.addChildren(outerDefnNodes);
            
            moduleDefnNode.setFirstChild(optionalCALDocNode);
            optionalCALDocNode.setNextSibling(moduleNameNode);
            moduleNameNode.setNextSibling(importDeclarationListNode);
            importDeclarationListNode.setNextSibling(friendDeclarationListNode);
            friendDeclarationListNode.setNextSibling(outerDefnListNode);
            
            return moduleDefnNode;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
            return visitor.visit_ModuleDefn(this, arg);
        }
        
        /**
         * @return the imported modules
         */
        public Import[] getImportedModules() {
            
            if (importedModules.length == 0) {
                return NO_IMPORTED_MODULES;
            }
            
            return importedModules.clone();
        }
        
        /**
         * Get the number of imported modules.
         * @return the number of imported modules.
         */
        public int getNImportedModules() {
            return importedModules.length;
        }
        
        /**
         * Get the nth imported module.
         * @param n the index of the imported module to return.
         * @return the nth imported module.
         */
        public Import getNthImportedModule(int n) {
            return importedModules[n];
        }
        
        
        /**
         * @return the friend modules
         */
        public Friend[] getFriendModules() {
            
            if (friendModules.length == 0) {
                return NO_FRIEND_MODULES;
            }
            
            return friendModules.clone();
        }
        
        /**
         * Get the number of friend modules.
         * @return the number of friend modules.
         */
        public int getNFriendModules() {
            return friendModules.length;
        }
        
        /**
         * Get the nth friend module.
         * @param n the index of the friend module to return.
         * @return the nth friend module.
         */
        public Friend getNthFriendModule(int n) {
            return friendModules[n];
        } 
        

        /**
         * @return the top level definitions
         */
        public TopLevelSourceElement[] getTopLevelDefns() {
            if (topLevelDefns.length == 0) {
                return NO_TOP_LEVEL_DEFNS;
            }
            
            return topLevelDefns.clone();
        }

        /**
         * Get the number of top level definitions.
         * @return the number of top level definitions.
         */
        public int getNTopLevelDefns() {
            return topLevelDefns.length;
        }
        
        /**
         * Get the nth top level definition.
         * @param n the index of the top level definition to return.
         * @return the nth top level definition.
         */
        public TopLevelSourceElement getNthTopLevelDefn(int n) {
            return topLevelDefns[n];
        }
    }
    
    /**
     * Models a "friend" declaration in a CAL module. 
     * At present these take a very simple form e.g. "friend List;"
     * @author Bo Ilic
     */
    public static final class Friend extends SourceElement {
        
        /** The name of the friend module. */
        private final Name.Module friendModuleName;
        
        private Friend(Name.Module friendModuleName, SourceRange range) {
            super(range);
            verifyArg(friendModuleName, "friendModuleName");
            this.friendModuleName = friendModuleName;
        }
        
        public static Friend make(Name.Module friendModuleName) {
            return new Friend(friendModuleName, null);
        }
        
        static Friend makeAnnotated(Name.Module friendModuleName, SourceRange range) {
            return new Friend(friendModuleName, range);
        }
        
        public static Friend make(ModuleName friendModuleName) {
            return new Friend(Name.Module.make(friendModuleName), null);
        }
        
        /**
         * @return the name of the friend module.
         */
        public Name.Module getFriendModuleName() {
            return friendModuleName;
        }
        

        @Override
        ParseTreeNode toParseTreeNode() {
            ParseTreeNode friendNode = new ParseTreeNode(CALTreeParserTokenTypes.LITERAL_friend, "friend");
            ParseTreeNode moduleNameNode = friendModuleName.toParseTreeNode();
            
            friendNode.setFirstChild(moduleNameNode);
            
            return friendNode;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
            return visitor.visit_Friend(this, arg);
        }        
    }
    
    /**
     * Models an "import" statement in a CAL module, either with or without a "using" clause.
     * @author Bo Ilic
     */
    public static final class Import extends SourceElement {
        
        public static final UsingItem[] NO_USING_ITEMS = new UsingItem[0];
        
        /** The name of the imported module. */
        private final Name.Module importedModuleName;
        
        /** Using items in this import statement's using clause, if any */
        private final UsingItem[] usingItems;
        
        /**
         * Models an item in an import statement's using clause.  Eg, the following statement
         * 
         *     import Prelude using
         *         typeClass = Eq, Ord;
         *         typeConstructor = List;
         *         dataConstructor = Nil, Cons;
         *         function = abs, negate;
         *         function = concat, append;
         *         ;
         * 
         * contains 5 UsingItems.
         * 
         * @author James Wright
         */
        public abstract static class UsingItem extends SourceElement {

            public static final SourceRange[] NO_SOURCE_RANGES = new SourceRange[0];

            /** The names that this using item imports */
            private final String[] usingNames;
            
            /** SourceRanges of the names in the using item */
            private final SourceRange[] usingNameSourceRanges;
            
            /**
             * Models a using item that contains function names.  Eg, the following statement
             * 
             *     import Prelude using
             *         typeClass = Eq, Ord;
             *         typeConstructor = List;
             *         dataConstructor = Nil, Cons;
             *         function = abs, negate, signum;
             *         function = concat, append, empty, isEmpty;
             *         ;
             * 
             * contains 2 UsingItem.Functions.
             * 
             * @author James Wright
             */
            public static final class Function extends UsingItem {
                
                static final String CATEGORY_NAME = "function";
                static final int CATEGORY_TOKEN_TYPE = CALTreeParserTokenTypes.LITERAL_function;
                
                private Function(String[] usingNames, SourceRange sourceRange, SourceRange[] usingNameSourceRanges) {
                    super(usingNames, sourceRange, usingNameSourceRanges);
                }
                
                public static Function make(String[] usingNames) {
                    return new Function(usingNames, null, null);
                }
                
                static Function makeAnnotated(String[] usingNames, SourceRange sourceRange, SourceRange[] usingNameSourceRanges) {
                    return new Function(usingNames, sourceRange, usingNameSourceRanges);
                }
                
                /** {@inheritDoc} */
                @Override
                boolean isValidUsingName(String usingName) {
                    return LanguageInfo.isValidFunctionName(usingName);
                }
                
                /** {@inheritDoc} */
                @Override
                String getUsingItemCategoryName() {
                    return CATEGORY_NAME;
                }
                
                /** {@inheritDoc} */
                @Override
                int getUsingItemCategoryTokenType() {
                    return CATEGORY_TOKEN_TYPE;
                }
                
                /** {@inheritDoc} */
                @Override
                public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                    return visitor.visit_Import_UsingItem_Function(this, arg);
                }   
            }

            /**
             * Models a using item that contains data constructor names.  Eg, the following statement
             * 
             *     import Prelude using
             *         typeClass = Eq, Ord;
             *         typeConstructor = List;
             *         dataConstructor = Nil, Cons;
             *         function = abs, negate, signum;
             *         function = concat, append, empty, isEmpty;
             *         ;
             * 
             * contains 1 UsingItem.DataConstructor.
             * 
             * @author James Wright
             */
            public static final class DataConstructor extends UsingItem {
                
                static final String CATEGORY_NAME = "dataConstructor";
                static final int CATEGORY_TOKEN_TYPE = CALTreeParserTokenTypes.LITERAL_dataConstructor;
                
                private DataConstructor(String[] usingNames, SourceRange sourceRange, SourceRange[] usingNameSourceRanges) {
                    super(usingNames, sourceRange, usingNameSourceRanges);
                }
                
                public static DataConstructor make(String[] usingNames) {
                    return new DataConstructor(usingNames, null, null);
                }
                
                static DataConstructor makeAnnotated(String[] usingNames, SourceRange sourceRange, SourceRange[] usingNameSourceRanges) {
                    return new DataConstructor(usingNames, sourceRange, usingNameSourceRanges);
                }
                
                /** {@inheritDoc} */
                @Override
                boolean isValidUsingName(String usingName) {
                    return LanguageInfo.isValidDataConstructorName(usingName);
                }
                
                /** {@inheritDoc} */
                @Override
                String getUsingItemCategoryName() {
                    return CATEGORY_NAME;
                }
                
                /** {@inheritDoc} */
                @Override
                int getUsingItemCategoryTokenType() {
                    return CATEGORY_TOKEN_TYPE;
                }
                
                /** {@inheritDoc} */
                @Override
                public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                    return visitor.visit_Import_UsingItem_DataConstructor(this, arg);
                }   
            }

            /**
             * Models a using item that contains type constructor names.  Eg, the following statement
             * 
             *     import Prelude using
             *         typeClass = Eq, Ord;
             *         typeConstructor = List;
             *         dataConstructor = Nil, Cons;
             *         function = abs, negate, signum;
             *         function = concat, append, empty, isEmpty;
             *         ;
             * 
             * contains 1 UsingItem.TypeConstructor.
             * 
             * @author James Wright
             */
            public static final class TypeConstructor extends UsingItem {
                
                static final String CATEGORY_NAME = "typeConstructor";
                static final int CATEGORY_TOKEN_TYPE = CALTreeParserTokenTypes.LITERAL_typeConstructor;
                
                private TypeConstructor(String[] usingNames, SourceRange sourceRange, SourceRange[] usingNameSourceRanges) {
                    super(usingNames, sourceRange, usingNameSourceRanges);
                }
                
                public static TypeConstructor make(String[] usingNames) {
                    return new TypeConstructor(usingNames, null, null);
                }
                
                static TypeConstructor makeAnnotated(String[] usingNames, SourceRange sourceRange, SourceRange[] usingNameSourceRanges) {
                    return new TypeConstructor(usingNames, sourceRange, usingNameSourceRanges);
                }
                
                /** {@inheritDoc} */
                @Override
                boolean isValidUsingName(String usingName) {
                    return LanguageInfo.isValidTypeConstructorName(usingName);
                }
                
                /** {@inheritDoc} */
                @Override
                String getUsingItemCategoryName() {
                    return CATEGORY_NAME;
                }
                
                /** {@inheritDoc} */
                @Override
                int getUsingItemCategoryTokenType() {
                    return CATEGORY_TOKEN_TYPE;
                }
                
                /** {@inheritDoc} */
                @Override
                public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                    return visitor.visit_Import_UsingItem_TypeConstructor(this, arg);
                }   
            }

            /**
             * Models a using item that contains type class names.  Eg, the following statement
             * 
             *     import Prelude using
             *         typeClass = Eq, Ord;
             *         typeConstructor = List;
             *         dataConstructor = Nil, Cons;
             *         function = abs, negate, signum;
             *         function = concat, append, empty, isEmpty;
             *         ;
             * 
             * contains 1 UsingItem.TypeClass.
             * 
             * @author James Wright
             */
            public static final class TypeClass extends UsingItem {
                
                static final String CATEGORY_NAME = "typeClass";
                static final int CATEGORY_TOKEN_TYPE = CALTreeParserTokenTypes.LITERAL_typeClass;
                
                private TypeClass(String[] usingNames, SourceRange sourceRange, SourceRange[] usingNameSourceRanges) {
                    super(usingNames, sourceRange, usingNameSourceRanges);
                }
                
                public static TypeClass make(String[] usingNames) {
                    return new TypeClass(usingNames, null, null);
                }
                
                static TypeClass makeAnnotated(String[] usingNames, SourceRange sourceRange, SourceRange[] usingNameSourceRanges) {
                    return new TypeClass(usingNames, sourceRange, usingNameSourceRanges);
                }
                
                /** {@inheritDoc} */
                @Override
                boolean isValidUsingName(String usingName) {
                    return LanguageInfo.isValidTypeClassName(usingName);
                }
                
                /** {@inheritDoc} */
                @Override
                String getUsingItemCategoryName() {
                    return CATEGORY_NAME;
                }
                
                /** {@inheritDoc} */
                @Override
                int getUsingItemCategoryTokenType() {
                    return CATEGORY_TOKEN_TYPE;
                }
                
                /** {@inheritDoc} */
                @Override
                public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                    return visitor.visit_Import_UsingItem_TypeClass(this, arg);
                }   
            }
            
            private UsingItem(String[] usingNames, SourceRange sourceRange, SourceRange[] usingNameSourceRanges) {
                super(sourceRange);
                
                if(usingNames == null) {
                    throw new NullPointerException();
                }

                if(usingNames.length == 0) {
                    throw new IllegalArgumentException("a using clause must contain at least one name");
                }
                
                for(int i = 0, nNames = usingNames.length; i < nNames; i++) {
                    if(!isValidUsingName(usingNames[i])) {
                        throw new IllegalArgumentException(usingNames[i] + " is not a valid name for this using item");
                    }
                }
                
                this.usingNames = usingNames.clone();
                
                if(usingNameSourceRanges == null || usingNameSourceRanges.length == 0) {
                    this.usingNameSourceRanges = NO_SOURCE_RANGES;
                } else {
                    this.usingNameSourceRanges = usingNameSourceRanges.clone();
                }
            }
            
            /**
             * Helper function for verifying the names passed into the constructor            
             * @param usingName String representing a name that should be valid for usingCategory
             * @return true if usingName is a valid name for this kind of UsingItem, or false otherwise
             */
            abstract boolean isValidUsingName(String usingName);
            
            /** 
             * @return representing the category of the names in this UsingItem.  
             *          It will be one of ["function", "dataConstructor", "typeConstructor", "typeClass"].
             */ 
            abstract String getUsingItemCategoryName();
            
            /**
             * @return Token type for the category node of this UsingItem
             */
            abstract int getUsingItemCategoryTokenType();
            
            /** {@inheritDoc} */
            @Override
            ParseTreeNode toParseTreeNode() {                

                final int tokenType = getUsingItemCategoryTokenType();
                ParseTreeNode usingItemNode = new ParseTreeNode(tokenType, getUsingItemCategoryName());
                
                ParseTreeNode[] nameNodes = new ParseTreeNode[usingNames.length]; 
                if(tokenType == CALTreeParserTokenTypes.LITERAL_function) {
                    for(int i = 0, nNames = usingNames.length; i < nNames; i++) {
                        nameNodes[i] = new ParseTreeNode(CALTreeParserTokenTypes.VAR_ID, usingNames[i]);
                    }
                
                } else {
                    for(int i = 0, nNames = usingNames.length; i < nNames; i++) {
                        nameNodes[i] = new ParseTreeNode(CALTreeParserTokenTypes.CONS_ID, usingNames[i]);
                    }
                }
                
                usingItemNode.addChildren(nameNodes);
                return usingItemNode;
            }
            
            public String[] getUsingNames() {
                return usingNames.clone();
            }
            
            // todo-jowong each using item may want to be encapsulated in its own source element
            SourceRange[] getUsingNameSourceRanges() {
                if(usingNameSourceRanges == null || usingNameSourceRanges.length == 0) {
                    return NO_SOURCE_RANGES;
                } else {
                    return usingNameSourceRanges.clone();
                }
            }
            
           

        }
        
        private Import(Name.Module importedModuleName, UsingItem[] usingClauses, SourceRange sourceRange) {
            super(sourceRange);
            
            verifyArg(importedModuleName, "importedModuleName");
            this.importedModuleName = importedModuleName;

            if(usingClauses == null || usingClauses.length == 0) {
                this.usingItems = NO_USING_ITEMS;
            } else {
                this.usingItems = usingClauses;
            }
        }
        
        public static Import make(Name.Module importedModuleName, UsingItem[] usingClauses) {
            return new Import(importedModuleName, usingClauses, null);
        }
        
        public static Import make(ModuleName importedModuleName, UsingItem[] usingClauses) {
            return new Import(Name.Module.make(importedModuleName), usingClauses, null);
        }
        
        public static Import make(Name.Module importedModuleName,
            String[] usingFunctionsOrClassMethods, 
            String[] usingDataConstructors, 
            String[] usingTypeConstructors, 
            String[] usingTypeClasses) {
            
            List<UsingItem> usingItems = new ArrayList<UsingItem>();
            
            if(usingFunctionsOrClassMethods != null && usingFunctionsOrClassMethods.length != 0) {
                usingItems.add(UsingItem.Function.make(usingFunctionsOrClassMethods));
            }
            
            if(usingDataConstructors != null && usingDataConstructors.length != 0) {
                usingItems.add(UsingItem.DataConstructor.make(usingDataConstructors));
            }
            
            if(usingTypeConstructors != null && usingTypeConstructors.length != 0) {
                usingItems.add(UsingItem.TypeConstructor.make(usingTypeConstructors));
            }
            
            if(usingTypeClasses != null && usingTypeClasses.length != 0) {
                usingItems.add(UsingItem.TypeClass.make(usingTypeClasses));
            }
            
            if(usingItems.size() == 0) {
                return new Import(importedModuleName, NO_USING_ITEMS, null);
            }
            
            return new Import(importedModuleName, usingItems.toArray(NO_USING_ITEMS), null);
        }
        
        public static Import make(ModuleName importedModuleName,
            String[] usingFunctionsOrClassMethods, 
            String[] usingDataConstructors, 
            String[] usingTypeConstructors, 
            String[] usingTypeClasses) {
            
            return make(Name.Module.make(importedModuleName), usingFunctionsOrClassMethods, usingDataConstructors, usingTypeConstructors, usingTypeClasses);
        }
        
        public static Import make(Name.Module importedModuleName) {
            return new Import(importedModuleName, null, null);
        }

        public static Import make(ModuleName importedModuleName) {
            return new Import(Name.Module.make(importedModuleName), null, null);
        }

        static Import makeAnnotated(Name.Module importedModuleName, UsingItem[] usingClauses, SourceRange sourceRange) {
            return new Import(importedModuleName, usingClauses, sourceRange);
        }
        
        /**
         * @return the name of the imported module.
         */
        public Name.Module getImportedModuleName() {
            return importedModuleName;
        }
        
        private boolean hasUsingClause () {
            return usingItems.length > 0;
        }
        

        
        @Override
        ParseTreeNode toParseTreeNode() {
            ParseTreeNode importDeclarationNode = new ParseTreeNode(CALTreeParserTokenTypes.LITERAL_import, "import");
            ParseTreeNode moduleNameNode = importedModuleName.toParseTreeNode();
            
            if(hasUsingClause()) {
                ParseTreeNode usingClauseNode = new ParseTreeNode(CALTreeParserTokenTypes.LITERAL_using, "using");
                for (final UsingItem usingItem : usingItems) {
                    usingClauseNode.addChild(usingItem.toParseTreeNode());
                }
                moduleNameNode.setNextSibling(usingClauseNode);
            }
            
            importDeclarationNode.setFirstChild(moduleNameNode);
            return importDeclarationNode;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
            return visitor.visit_Import(this, arg);
        }

        /**
         * @return An array containing the items of the using clause of this import statement
         */
        public UsingItem[] getUsingItems() {
            
            if(usingItems.length == 0) {
                return NO_USING_ITEMS;
            }
            return usingItems.clone();
        }
    }
    
    /**
     * Base class for modeling CAL local definitions (i.e. defined within a let expression).
     * Currently these are local function definitions, local function type declarations,
     * and pattern match declarations.
     * <p>
     * Note that a definition such as:
     * <pre>
     * let
     *     x = foo 1.0 "bar";
     * in
     *     ...
     * </pre>
     * is considered a local function (and hence modeled with {@link SourceModel.LocalDefn.Function.Definition})
     * rather than a local pattern match declaration.
     * 
     * @author Bo Ilic
     */
    public static abstract class LocalDefn extends SourceElement {

        /**
         * Base class for modeling CAL local definitions related to local functions.
         * Currently these are local function definitions and local function type declarations.
         * 
         * @author Bo Ilic
         */
        public static abstract class Function extends LocalDefn {
            
            /** The CALDoc comment associated with this local definition, or null if there is none. */
            private final SourceModel.CALDoc.Comment.Function caldocComment;
            
            /** 
             * CAL name of the local definition. The module name is not given, because it is assumed to be defined in the module in
             * which it is defined in. In general, within CAL source, if the module name *must* be the name of the module in which
             * the element is defined, then it is illegal to supply a qualified name.          
             */
            private final String name;
            
            /**
             * The optional position of this.name in the source code. May be null.
             */
            private SourceRange functionNameSourceRange;
            
            /**
             * Models local function definitions i.e. enclosed within the scope of a let expression.
             * For example, the "f x = x + 1" part of
             * let
             *   f x = x + 1;
             * in 
             *   f 2;
             * 
             * @author Bo Ilic
             */
            public static final class Definition extends Function {
                
                private final SourceRange sourceRangeExcludingCaldoc;
                
                private final SourceModel.Parameter[] parameters;
                
                private final SourceModel.Expr definingExpr;
                
                public static final SourceModel.Parameter[] NO_PARAMETERS = new SourceModel.Parameter[0];                     
                
                private Definition (SourceModel.CALDoc.Comment.Function caldocComment, String functionName, SourceRange functionNameSourceRange, SourceModel.Parameter[] parameters, SourceModel.Expr definingExpr, SourceRange sourceRange, SourceRange sourceRangeExcludingCaldoc) {
                    super(caldocComment, functionName, functionNameSourceRange, sourceRange);
                    
                    this.sourceRangeExcludingCaldoc = sourceRangeExcludingCaldoc;
                    
                    if (parameters == null || parameters.length == 0) {
                        this.parameters = NO_PARAMETERS;
                    } else {
                        this.parameters = parameters.clone();
                        SourceModel.verifyArrayArg(this.parameters, "parameters");                                
                    }
                    
                    SourceModel.verifyArg(definingExpr, "definingExpr");                    
                    
                    this.definingExpr = definingExpr; 
                }
        
                public static Definition make(String functionName, SourceModel.Parameter[] parameters, SourceModel.Expr definingExpr) {
                    return new Definition(null, functionName, null, parameters, definingExpr, null, null);
                }
                
                public static Definition make(SourceModel.CALDoc.Comment.Function caldocComment, String functionName, SourceModel.Parameter[] parameters, SourceModel.Expr definingExpr) {
                    return new Definition(caldocComment, functionName, null, parameters, definingExpr, null, null);
                }
                
                static Definition makeAnnotated(String functionName, SourceRange functionNameSourceRange, SourceModel.Parameter[] parameters, SourceModel.Expr definingExpr, SourceRange sourceRange, SourceRange sourceRangeExcludingCaldoc) {
                    return new Definition(null, functionName, functionNameSourceRange, parameters, definingExpr, sourceRange, sourceRangeExcludingCaldoc);
                }
                
                static Definition makeAnnotated(SourceModel.CALDoc.Comment.Function caldocComment, String functionName, SourceRange functionNameSourceRange, SourceModel.Parameter[] parameters, SourceModel.Expr definingExpr, SourceRange sourceRange, SourceRange sourceRangeExcludingCaldoc) {
                    return new Definition(caldocComment, functionName, functionNameSourceRange, parameters, definingExpr, sourceRange, sourceRangeExcludingCaldoc);
                }
                            
                public SourceModel.Parameter[] getParameters() {
                    if (parameters.length == 0) {
                        return NO_PARAMETERS;
                    }
                    
                    return parameters.clone();
                }
                
                /**
                 * Get the number of parameters.
                 * @return the number of parameters.
                 */
                public int getNParameters() {
                    return parameters.length;
                }
                
                /**
                 * Get the nth parameter.
                 * @param n the index of the parameter to return.
                 * @return the nth parameter.
                 */
                public SourceModel.Parameter getNthParameter(int n) {
                    return parameters[n];
                }
                
                public SourceModel.Expr getDefiningExpr() {
                    return definingExpr;
                }
                
                SourceRange getSourceRangeExcludingCaldoc() {
                    return sourceRangeExcludingCaldoc;
                }
                
                @Override
                ParseTreeNode toParseTreeNode() {
                    ParseTreeNode localFunctionNode = new ParseTreeNode(CALTreeParserTokenTypes.LET_DEFN, "LET_DEFN");
                    ParseTreeNode optionalCALDocNode = SourceModel.makeOptionalCALDocNode(getCALDocComment());
                    ParseTreeNode functionNameNode = new ParseTreeNode(CALTreeParserTokenTypes.VAR_ID, getName());
                    ParseTreeNode paramListNode = new ParseTreeNode(CALTreeParserTokenTypes.FUNCTION_PARAM_LIST, "FUNCTION_PARAM_LIST");
                    
                    ParseTreeNode[] paramNodes = new ParseTreeNode[parameters.length];
                    for (int i = 0; i < parameters.length; i++) {
                        paramNodes[i] = parameters[i].toParseTreeNode();
                    }
                    paramListNode.addChildren(paramNodes);
                    
                    ParseTreeNode exprNode = definingExpr.toParseTreeNode();
                    
                    localFunctionNode.setFirstChild(optionalCALDocNode);
                    optionalCALDocNode.setNextSibling(functionNameNode);
                    functionNameNode.setNextSibling(paramListNode);
                    paramListNode.setNextSibling(exprNode);
                    return localFunctionNode;
                }
                
                /**
                 * {@inheritDoc}
                 */
                @Override
                public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                    return visitor.visit_LocalDefn_Function_Definition(this, arg);
                }
            }
            
            /**
             * Models local function type declarations i.e. the type declaration is enclosed within the scope of a let. For example,
             * the "f :: Int -> Int;" in
             * let
             *   f :: Int -> Int;
             *   f x = x + 1;
             * in 
             *   f 2;
             * @author Bo Ilic
             */
            public static final class TypeDeclaration extends Function {
                
                private final SourceModel.TypeSignature declaredType;
                
               
                private TypeDeclaration(SourceModel.CALDoc.Comment.Function caldocComment, String functionName, SourceRange functionNameSourceRange, SourceModel.TypeSignature declaredType, SourceRange sourceRange) {
                    super(caldocComment, functionName, functionNameSourceRange, sourceRange);
                    
                    SourceModel.verifyArg(declaredType, "declaredType");
                    this.declaredType = declaredType;
                }
                
                public static TypeDeclaration make(String functionName, SourceModel.TypeSignature declaredType) {
                    return new TypeDeclaration(null, functionName, null, declaredType, null);
                }
                
                public static TypeDeclaration make(SourceModel.CALDoc.Comment.Function caldocComment, String functionName, SourceModel.TypeSignature declaredType) {
                    return new TypeDeclaration(caldocComment, functionName, null, declaredType, null);
                }
                
                static TypeDeclaration makeAnnotated(String functionName, SourceRange functionNameSourceRange, SourceModel.TypeSignature declaredType, SourceRange sourceRange) {
                    return new TypeDeclaration(null, functionName, functionNameSourceRange, declaredType, sourceRange);
                }
                
                static TypeDeclaration makeAnnotated(SourceModel.CALDoc.Comment.Function caldocComment, String functionName, SourceRange functionNameSourceRange, SourceModel.TypeSignature declaredType, SourceRange sourceRange) {
                    return new TypeDeclaration(caldocComment, functionName, functionNameSourceRange, declaredType, sourceRange);
                }
                
                public SourceModel.TypeSignature getDeclaredType() {
                    return declaredType;
                }
                
                @Override
                ParseTreeNode toParseTreeNode() {
                    ParseTreeNode localDefnNode = new ParseTreeNode(CALTreeParserTokenTypes.LET_DEFN_TYPE_DECLARATION, "LET_DEFN_TYPE_DECLARATION");
                    ParseTreeNode optionalCALDocNode = SourceModel.makeOptionalCALDocNode(getCALDocComment());
                    ParseTreeNode typeDeclarationNode = new ParseTreeNode(CALTreeParserTokenTypes.TYPE_DECLARATION, "::");
                    ParseTreeNode functionNameNode = new ParseTreeNode(CALTreeParserTokenTypes.VAR_ID, getName());
                    ParseTreeNode declaredTypeNode = declaredType.toParseTreeNode();
        
                    localDefnNode.setFirstChild(optionalCALDocNode);
                    optionalCALDocNode.setNextSibling(typeDeclarationNode);
                    typeDeclarationNode.setFirstChild(functionNameNode);
                    functionNameNode.setNextSibling(declaredTypeNode);
                    return localDefnNode;                
                }
                
                /**
                 * {@inheritDoc}
                 */
                @Override
                public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                    return visitor.visit_LocalDefn_Function_TypeDeclaration(this, arg);
                }
            }
            
            private Function(SourceModel.CALDoc.Comment.Function caldocComment, String name, SourceRange functionNameSourceRange, SourceRange sourceRange) {
        
                super(sourceRange);
        
                this.caldocComment = caldocComment;
                
                if (!LanguageInfo.isValidFunctionName(name)) {
                    throw new IllegalArgumentException();
                }
                
                this.name = name;
                this.functionNameSourceRange = functionNameSourceRange;
            }
            
            /**
             * @return the CALDoc comment associated with this function definition, or null if there is none.
             */
            public SourceModel.CALDoc.Comment.Function getCALDocComment() {
                return caldocComment;
            }
            
            /**         
             * @return the name of the function being defined or whose type is being declared. 
             */
            public String getName() {
                return name;
            }
            
            /**
             * @return the position of the name symbol in the source file. This may be null.
             */
            // todo-jowong maybe the name can be encapsulated by its own source element
            public SourceRange getNameSourceRange(){
                return functionNameSourceRange;
            }
        }
        
        /**
         * Base class for modeling local pattern match declarations. A local pattern match
         * declaration is used to introduce one or more pattern-bound variables in the
         * scope of a let definition. For example, the follwoing is one such declaration (using
         * a tuple pattern):
         * <pre>
         * let (x, y, z) = List.unzip3 listOfTriples; in ... 
         * </pre>
         *
         * @author Joseph Wong
         */
        public static abstract class PatternMatch extends LocalDefn {
            
            /**
             * The expression that occurs on the right hand side of the "=" in a pattern match declaration.
             */
            private final Expr definingExpr;
            
            /**
             * Models a local pattern match declaration using a data constructor pattern. For example, the following
             * are all declarations of this kind:
             * <pre>
             * let Prelude.Cons x y = ['a', 'b', 'c']; in ...
             * </pre>
             * <pre>
             * let Prelude.Cons {head, tail=y} = ['a', 'b', 'c']; in ...
             * </pre>
             *
             * @author Joseph Wong
             */
            public static final class UnpackDataCons extends PatternMatch {
                
                /**
                 * The name of the data constructor used in the pattern.
                 */
                private final Name.DataCons dataConsName;
                
                /**
                 * The pattern bindings (either positional or field-value pairs).
                 */
                private final ArgBindings argBindings;
                
                /**
                 * Creates a new source model element for representing a local pattern match declaration using a data constructor pattern.
                 * @param dataConsName the name of the data constructor used in the pattern.
                 * @param argBindings the pattern bindings (either positional or field-value pairs).
                 * @param definingExpr the expression that occurs on the right hand side of the "=" in a pattern match declaration.
                 * @param sourceRange
                 */
                private UnpackDataCons(final Name.DataCons dataConsName, final ArgBindings argBindings, final Expr definingExpr, final SourceRange sourceRange) {
                    super(definingExpr, sourceRange);
                    
                    this.argBindings = (argBindings == null) ? ArgBindings.NO_BINDINGS : argBindings;
                    
                    verifyArg(dataConsName, "dataConsName");
                    this.dataConsName = dataConsName;
                }
                
                /**
                 * Factory method for constructing a new source model element for representing a local pattern match declaration using a data constructor pattern.
                 * @param dataConsName the name of the data constructor used in the pattern.
                 * @param argBindings the pattern bindings (either positional or field-value pairs).
                 * @param definingExpr the expression that occurs on the right hand side of the "=" in a pattern match declaration.
                 * @return a new instance of this class.
                 */
                public static UnpackDataCons make(final Name.DataCons dataConsName, final ArgBindings argBindings, final Expr definingExpr) {
                    return new UnpackDataCons(dataConsName, argBindings, definingExpr, null);
                }

                /**
                 * Factory method for constructing a new source model element for representing a local pattern match declaration using a data constructor pattern.
                 * @param dataConsName the name of the data constructor used in the pattern.
                 * @param argBindings the pattern bindings (either positional or field-value pairs).
                 * @param definingExpr the expression that occurs on the right hand side of the "=" in a pattern match declaration.
                 * @param sourceRange
                 * @return a new instance of this class.
                 */
                static UnpackDataCons makeAnnotated(final Name.DataCons dataConsName, final ArgBindings argBindings, final Expr definingExpr, final SourceRange sourceRange) {
                    return new UnpackDataCons(dataConsName, argBindings, definingExpr, sourceRange);
                }

                
                /**
                 * Returns the name of the data constructor used in the pattern.
                 * @return the name of the data constructor used in the pattern.
                 */
                public Name.DataCons getDataConsName() {
                    return dataConsName;
                }
                
                /**
                 * Returns the pattern variable bindings (either positional or field-value pairs).
                 * @return the pattern variable bindings (either positional or field-value pairs).
                 */
                public ArgBindings getArgBindings() {
                    return argBindings;
                }
                
                /**
                 * {@inheritDoc}
                 */
                @Override
                ParseTreeNode buildPatternParseTreeNode() {
                    final ParseTreeNode fullPatternNode = new ParseTreeNode(CALTreeParserTokenTypes.PATTERN_CONSTRUCTOR, "PATTERN_CONSTURCTOR");
                    {
                        final ParseTreeNode dataConsNameListNode = new ParseTreeNode(CALTreeParserTokenTypes.DATA_CONSTRUCTOR_NAME_LIST, "DATA_CONSTRUCTOR_NAME_LIST");
                        fullPatternNode.setFirstChild(dataConsNameListNode);
                        
                        dataConsNameListNode.setFirstChild(dataConsName.toParseTreeNode());
                        
                        final ParseTreeNode argBindingsNode = argBindings.toParseTreeNode();
                        dataConsNameListNode.setNextSibling(argBindingsNode);
                    }
                    
                    return fullPatternNode;
                }
                
                /**
                 * {@inheritDoc}
                 */
                @Override
                public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                    return visitor.visit_LocalDefn_PatternMatch_UnpackDataCons(this, arg);
                }
            }
            
            /**
             * Models a local pattern match declaration using a tuple pattern. For example, the following
             * are all declarations of this kind:
             * <pre>
             * let (x, y, z) = List.unzip3 listOfTriples; in ... 
             * </pre>
             * <pre>
             * let (_, y, _) = List.unzip3 listOfTriples; in ... 
             * </pre>
             *
             * @author Joseph Wong
             */
            public static final class UnpackTuple extends PatternMatch {
                
                /**
                 * The pattern bindings.
                 */
                private final Pattern[] patterns;
                
                /**
                 * Creates a source model element for representing a local pattern match declaration using a tuple pattern.
                 * @param patterns the pattern bindings.
                 * @param definingExpr the expression that occurs on the right hand side of the "=" in a pattern match declaration.
                 * @param sourceRange
                 */
                private UnpackTuple(Pattern[] patterns, Expr definingExpr, SourceRange sourceRange) {
                    super(definingExpr, sourceRange);
                    
                    if (patterns == null || patterns.length == 0) {
                        this.patterns = Pattern.NO_PATTERNS;
                    } else {
                        this.patterns = patterns.clone();
                        verifyArrayArg(this.patterns, "patterns");                                       
                    }                                                  
                }
                
                /**
                 * Factory method for constructing a source model element for representing a local pattern match declaration using a tuple pattern.
                 * @param patterns the pattern bindings.
                 * @param definingExpr the expression that occurs on the right hand side of the "=" in a pattern match declaration.
                 * @return a new instance of this class.
                 */
                public static UnpackTuple make(final Pattern[] patterns, final Expr definingExpr) {
                    return new UnpackTuple(patterns, definingExpr, null);
                }
                
                /**
                 * Factory method for constructing a source model element for representing a local pattern match declaration using a tuple pattern.
                 * @param patterns the pattern bindings.
                 * @param definingExpr the expression that occurs on the right hand side of the "=" in a pattern match declaration.
                 * @param sourceRange
                 * @return a new instance of this class.
                 */
                static UnpackTuple makeAnnotated(final Pattern[] patterns, final Expr definingExpr, final SourceRange sourceRange) {
                    return new UnpackTuple(patterns, definingExpr, sourceRange);
                }
                
             
                /**
                 * Returns the pattern bindings.
                 * @return the pattern bindings.
                 */
                public Pattern[] getPatterns() {
                    if (patterns.length == 0) {
                        return Pattern.NO_PATTERNS;
                    }
                    
                    return patterns.clone();
                }           
                
                /**
                 * Returns the number of pattern bindings.
                 * @return the number of pattern bindings.
                 */
                public int getNPatterns() {
                    return patterns.length;
                }
                
                /**
                 * Returns the pattern at the specified position in the array of pattern bindings.
                 * @param n the index of the pattern to return.
                 * @return the pattern at the specified position in the array of pattern bindings.
                 */
                public Pattern getNthPattern(int n) {
                    return patterns[n];
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                ParseTreeNode buildPatternParseTreeNode() {
                    final ParseTreeNode fullPatternNode = new ParseTreeNode(CALTreeParserTokenTypes.TUPLE_CONSTRUCTOR, "Tuple");                        
                    
                    final ParseTreeNode[] patternNodes = new ParseTreeNode[patterns.length];
                    for (int i = 0; i < patterns.length; i++) {
                        patternNodes[i] = patterns[i].toParseTreeNode();
                    }
                    fullPatternNode.addChildren(patternNodes);
                    
                    return fullPatternNode;
                }
                
                /**
                 * {@inheritDoc}
                 */
                @Override
                public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                    return visitor.visit_LocalDefn_PatternMatch_UnpackTuple(this, arg);
                }
            }
            
            /**
             * Models a local pattern match declaration using a list constructor pattern (:). For example, the following
             * are all declarations of this kind:
             * <pre>
             * let x:y = ['a', 'b', 'c']; in ...
             * </pre>
             * <pre>
             * let _:y = ['a', 'b', 'c']; in ...
             * </pre>
             * <pre>
             * let x:_ = ['a', 'b', 'c']; in ...
             * </pre>
             *
             * @author Joseph Wong
             */
            public static final class UnpackListCons extends PatternMatch {
                
                /** The pattern to the left of the ":". */
                private final Pattern headPattern;       
                
                /** The pattern to the right of the ":". */
                private final Pattern tailPattern;
                
                /** The SourceRange of the ":". */
                private final SourceRange operatorSourceRange;
                
                /**
                 * Creates a new source model element for representing a local pattern match declaration using a list constructor pattern (:).
                 * @param headPattern the pattern to the left of the ":".
                 * @param tailPattern the pattern to the right of the ":".
                 * @param definingExpr the expression that occurs on the right hand side of the "=" in a pattern match declaration.
                 * @param sourceRange
                 * @param operatorSourceRange the SourceRange of the ":".
                 */
                private UnpackListCons(final Pattern headPattern, final Pattern tailPattern, final Expr definingExpr, final SourceRange sourceRange, final SourceRange operatorSourceRange) {
                    super (definingExpr, sourceRange);
                    
                    verifyArg(headPattern, "headPattern");
                    verifyArg(tailPattern, "tailPattern");
                    
                    this.headPattern = headPattern;
                    this.tailPattern = tailPattern;
                    this.operatorSourceRange = operatorSourceRange;
                }
                
                /**
                 * Factory method for constructing a new source model element for representing a local pattern match declaration using a list constructor pattern (:).
                 * @param headPattern the pattern to the left of the ":".
                 * @param tailPattern the pattern to the right of the ":".
                 * @param definingExpr the expression that occurs on the right hand side of the "=" in a pattern match declaration.
                 * @return a new instance of this class.
                 */
                public static UnpackListCons make(final Pattern headPattern, final Pattern tailPattern, final Expr definingExpr) {
                    return new UnpackListCons(headPattern, tailPattern, definingExpr, null, null);
                }
                
                /**
                 * Factory method for constructing a new source model element for representing a local pattern match declaration using a list constructor pattern (:).
                 * @param headPattern the pattern to the left of the ":".
                 * @param tailPattern the pattern to the right of the ":".
                 * @param definingExpr the expression that occurs on the right hand side of the "=" in a pattern match declaration.
                 * @param sourceRange
                 * @param operatorSourceRange the SourceRange of the ":".
                 * @return a new instance of this class.
                 */
                static UnpackListCons makeAnnotated(final Pattern headPattern, final Pattern tailPattern, final Expr definingExpr, final SourceRange sourceRange, final SourceRange operatorSourceRange) {
                    return new UnpackListCons(headPattern, tailPattern, definingExpr, sourceRange, operatorSourceRange);
                }
                                
                /**
                 * Returns the pattern to the left of the ":".
                 * @return the pattern to the left of the ":".
                 */
                public Pattern getHeadPattern() {
                    return headPattern;
                }
                
                /**
                 * Returns the pattern to the right of the ":".
                 * @return the pattern to the right of the ":".
                 */
                public Pattern getTailPattern() {
                    return tailPattern;
                }
                
                /**
                 * Returns the SourceRange of the ":".
                 * @return the SourceRange of the ":".
                 */
                // todo-jowong maybe the operator can be encapsulated by its own source element
                SourceRange getOperatorSourceRange() {
                    return operatorSourceRange;
                }
                
                /**
                 * {@inheritDoc}
                 */
                @Override
                ParseTreeNode buildPatternParseTreeNode() {
                    final ParseTreeNode fullPatternNode = new ParseTreeNode(CALTreeParserTokenTypes.COLON, ":");
                    final ParseTreeNode headPatternNode = headPattern.toParseTreeNode();
                    final ParseTreeNode tailPatternNode = tailPattern.toParseTreeNode();
                    
                    fullPatternNode.setFirstChild(headPatternNode);
                    headPatternNode.setNextSibling(tailPatternNode);
                    return fullPatternNode;
                }
                
                /**
                 * {@inheritDoc}
                 */
                @Override
                public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                    return visitor.visit_LocalDefn_PatternMatch_UnpackListCons(this, arg);
                }
            }
            
            /**
             * Models a local pattern match declaration using a record pattern. For example, the following
             * are all declarations of this kind:
             * <pre>
             * let {x, y} = someRecord; in ...
             * </pre>
             * <pre>
             * let {x=a, y=b} = someRecord; in ...
             * </pre>
             * <pre>
             * let {_ | x=_, y} = someRecord; in ...
             * </pre>
             *
             * Note that "{s | } = ..." is not equivalent to "{s} = ..."
             * the first is doing a record polymorphic record match, the second is doing a case on a record with 1 field named "s"
             * and using punning on the field name s.
             * 
             * @author Joseph Wong
             */
            public static final class UnpackRecord extends PatternMatch {

                /**
                 * The base record pattern (to the left of the | in the pattern).
                 * <p>
                 * For example, the "r" in "{r | #1 = f1, #2 = f2} -> ...". 
                 * May be null, to represent a non-record polymorphic pattern match e.g. "{#1 = f1, #2 = f3} -> ..."
                 * <p>
                 * Note that while it is syntactically valid to use a variable for the base record pattern, the compiler only permits
                 * the use of the local pattern match syntax with a wildcard (_) base record pattern.
                 */
                private final Pattern baseRecordPattern;
                
                /**
                 * The field pattern bindings.
                 */
                private final FieldPattern[] fieldPatterns;
                
                /**
                 * Creates a source model element for representing a local pattern match declaration using a record pattern.
                 * @param baseRecordPattern the base record pattern.
                 * @param fieldPatterns the field pattern bindings.
                 * @param definingExpr the expression that occurs on the right hand side of the "=" in a pattern match declaration.
                 * @param sourceRange
                 */
                private UnpackRecord(Pattern baseRecordPattern, FieldPattern[] fieldPatterns, Expr definingExpr, SourceRange sourceRange) {                         
                    super(definingExpr, sourceRange);
                    
                    if (fieldPatterns == null || fieldPatterns.length == 0) {
                        this.fieldPatterns = FieldPattern.NO_FIELD_PATTERNS;
                    } else {
                        this.fieldPatterns = fieldPatterns.clone();
                        verifyArrayArg(this.fieldPatterns, "fieldPatterns");                                       
                    }
                    
                    this.baseRecordPattern = baseRecordPattern;
                }                     

                /**
                 * Factory method for constructing a source model element for representing a local pattern match declaration using a record pattern.
                 * @param baseRecordPattern the base record pattern.
                 * @param fieldPatterns the field pattern bindings.
                 * @param definingExpr the expression that occurs on the right hand side of the "=" in a pattern match declaration.
                 * @return a new instance of this class.
                 */
                public static UnpackRecord make(Pattern baseRecordPattern, FieldPattern[] fieldPatterns, Expr definingExpr) {
                    return new UnpackRecord(baseRecordPattern, fieldPatterns, definingExpr, null);
                }
                
                /**
                 * Factory method for constructing a source model element for representing a local pattern match declaration using a record pattern.
                 * @param baseRecordPattern the base record pattern.
                 * @param fieldPatterns the field pattern bindings.
                 * @param definingExpr the expression that occurs on the right hand side of the "=" in a pattern match declaration.
                 * @param sourceRange
                 * @return a new instance of this class.
                 */
                static UnpackRecord makeAnnotated(Pattern baseRecordPattern, FieldPattern[] fieldPatterns, Expr definingExpr, SourceRange sourceRange) {
                    return new UnpackRecord(baseRecordPattern, fieldPatterns, definingExpr, sourceRange);
                }
                
         
                /**
                 * Returns the base record pattern (to the left of the | in the pattern).
                 * @return the base record pattern.
                 */
                public Pattern getBaseRecordPattern() {
                    return baseRecordPattern;
                }
                
                /**
                 * Returns the field pattern bindings.
                 * @return the field pattern bindings.
                 */
                public FieldPattern[] getFieldPatterns() {
                    if (fieldPatterns.length == 0) {
                        return FieldPattern.NO_FIELD_PATTERNS;
                    }
                    
                    return fieldPatterns.clone();
                }
                
                /**
                 * Returns the number of field pattern bindings.
                 * @return the number of field pattern bindings.
                 */
                public int getNFieldPatterns() {
                    return fieldPatterns.length;
                }
                
                /**
                 * Returns the field pattern at the specified position in the array of field pattern bindings.
                 * @param n the index of the field pattern to return.
                 * @return the field pattern at the specified position in the array of field pattern bindings.
                 */
                public FieldPattern getNthFieldPattern(final int n) {
                    return fieldPatterns[n];
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                ParseTreeNode buildPatternParseTreeNode() {
                    final ParseTreeNode fullPatternNode = new ParseTreeNode(CALTreeParserTokenTypes.RECORD_PATTERN, "RECORD_PATTERN");
                    final ParseTreeNode baseRecordPatternNode = new ParseTreeNode(CALTreeParserTokenTypes.BASE_RECORD_PATTERN, "BASE_RECORD_PATTERN");
                    final ParseTreeNode fieldBindingVarAssignmentListNode = new ParseTreeNode(CALTreeParserTokenTypes.FIELD_BINDING_VAR_ASSIGNMENT_LIST, "FIELD_BINDING_VAR_ASSIGNMENT_LIST");
                    
                    if(baseRecordPattern != null) {
                        baseRecordPatternNode.setFirstChild(baseRecordPattern.toParseTreeNode());
                    }
                    
                    final ParseTreeNode[] fieldPatternNodes = new ParseTreeNode[fieldPatterns.length];
                    for (int i = 0; i < fieldPatterns.length; i++) {
                        fieldPatternNodes[i] = fieldPatterns[i].toParseTreeNode();
                    }
                    fieldBindingVarAssignmentListNode.addChildren(fieldPatternNodes);
                    
                    fullPatternNode.setFirstChild(baseRecordPatternNode);
                    baseRecordPatternNode.setNextSibling(fieldBindingVarAssignmentListNode);
                    return fullPatternNode;
                }
                
                /**
                 * {@inheritDoc}
                 */
                @Override
                public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                    return visitor.visit_LocalDefn_PatternMatch_UnpackRecord(this, arg);
                }
            }
            
            /**
             * Private constructor for this base class for local pattern match declarations. Intended only
             * to be invoked by subclass constructors.
             *
             * @param definingExpr the expression that occurs on the right hand side of the "=" in a pattern match declaration.
             * @param sourceRange
             */
            private PatternMatch(final Expr definingExpr, final SourceRange sourceRange) {
                super(sourceRange);
                
                verifyArg(definingExpr, "definingExpr");
                this.definingExpr = definingExpr;
            }
            
            /**
             * Returns the expression that occurs on the right hand side of the "=" in a pattern match declaration.
             * @return the expression that occurs on the right hand side of the "=" in a pattern match declaration.
             */
            public Expr getDefiningExpr() {
                return definingExpr;
            }
            
            /**
             * @return a ParseTreeNode generated from the pattern associated with the pattern expression. 
             */
            abstract ParseTreeNode buildPatternParseTreeNode();
            
            /**
             * {@inheritDoc}
             */
            @Override
            final ParseTreeNode toParseTreeNode() {
                final ParseTreeNode patternMatchDeclNode = new ParseTreeNode(CALTreeParserTokenTypes.LET_PATTERN_MATCH_DECL, "LET_PATTERN_MATCH_DECL");                        
                final ParseTreeNode patternMatchPatternNode = buildPatternParseTreeNode();                        
                final ParseTreeNode exprNode = getDefiningExpr().toParseTreeNode();
                
                patternMatchDeclNode.addChild(patternMatchPatternNode);
                patternMatchPatternNode.setNextSibling(exprNode);                        
                return patternMatchDeclNode;
            }                 
        }
        
        /**
         * Private constructor for this base class for modeling CAL local definitions. Intended only
         * to be invoked by subclass constructors.
         * @param sourceRange
         */
        private LocalDefn(final SourceRange sourceRange) {
            super(sourceRange);
        }

    }
    
    /**
     * Base class for modeling CAL top-level function definitions.
     * There are 3 kinds of CAL function definitions:
     * -native CAL functions defined via CAL expressions.
     * -foreign functions
     * -primitive functions (i.e. built-in functions which have an explicit "primitive" declaration in the Prelude
     * 
     * @author Bo Ilic
     */    
    public static abstract class FunctionDefn extends TopLevelSourceElement {
        
        /** The CALDoc comment associated with this function definition, or null if there is none. */
        private final CALDoc.Comment.Function caldocComment;
        
        /** The SourceRange of the name of this function */
        private final SourceRange nameSourceRange;
        
        /** The SourceRange of the entire function decl, excluding any caldoc */
        private final SourceRange sourceRangeExcludingCaldoc; 
        
        /** 
         * CAL name of the function. The module name is not given, because the function is assume to be defined in the module in
         * which it is defined in. In general, within CAL source, if the module name *must* be the name of the module in which
         * the element is defined, then it is illegal to supply a qualified name.          
         */
        private final String name;
        
        /** The scope of the definition. */
        private final Scope scope;
        
        /** Whether the scope is explicitly specified in the source. */
        private final boolean isScopeExplicitlySpecified;
        
        /**
         * Models a "regular" CAL function i.e. one defined by an expression in CAL rather than a foreign or primitive function.
         * These are called algebraic because they use the algebra of CAL's expression syntax in their definitions.
         * For example:
         * 
         * f x y = x + y;
         * 
         * @author Bo Ilic
         */
        public static final class Algebraic extends FunctionDefn {
            
            private final Parameter[] parameters;
            private final Expr definingExpr;
            
            public static final Parameter[] NO_PARAMETERS = new Parameter[0];                     
            
            private Algebraic (CALDoc.Comment.Function caldocComment, String functionName, Scope scope, boolean isScopeExplicitlySpecified, Parameter[] parameters, Expr definingExpr, boolean internalFunction, SourceRange sourceRange, SourceRange sourceRangeExcludingCaldoc, SourceRange functionNameSourceRange) {
                super (caldocComment, functionName, scope, isScopeExplicitlySpecified, internalFunction, sourceRange, sourceRangeExcludingCaldoc, functionNameSourceRange);
                
                if (parameters == null || parameters.length == 0) {
                    this.parameters = NO_PARAMETERS;
                } else {
                    this.parameters = parameters.clone();
                    verifyArrayArg(this.parameters, "parameters");                                       
                }
                
                verifyArg(definingExpr, "definingExpr");               
                this.definingExpr = definingExpr;
            }

            /**
             * Create an instance of this class without an associated CALDoc comment.
             * @param functionName the name of the CAL function.
             * @param scope the scope of the function.
             * @param parameters the parameters of the function.
             * @param definingExpr the defining expression of the function.
             * @return a new instance of this class.
             */
            public static Algebraic make (String functionName, Scope scope, Parameter[] parameters, Expr definingExpr) {
                return new Algebraic(null, functionName, scope, true, parameters, definingExpr, false, null, null, null);
            }
            
            /**
             * Create an instance of this class with an associated CALDoc comment.
             * @param caldocComment the CALDoc comment.
             * @param functionName the name of the CAL function.
             * @param scope the scope of the function.
             * @param isScopeExplicitlySpecified whether the scope is explicitly specified in the source.
             * @param parameters the parameters of the function.
             * @param definingExpr the defining expression of the function.
             * @return a new instance of this class.
             */
            public static Algebraic make (CALDoc.Comment.Function caldocComment, String functionName, Scope scope, boolean isScopeExplicitlySpecified, Parameter[] parameters, Expr definingExpr) {
                return new Algebraic(caldocComment, functionName, scope, isScopeExplicitlySpecified, parameters, definingExpr, false, null, null, null);
            }
            
            /**
             * Create an instance of this class with an associated CALDoc comment and a source position.
             * @param caldocComment the CALDoc comment.
             * @param functionName the name of the CAL function.
             * @param scope the scope of the function.
             * @param isScopeExplicitlySpecified whether the scope is explicitly specified in the source.
             * @param parameters the parameters of the function.
             * @param definingExpr the defining expression of the function.
             * @param sourceRange the source position associated with this element.
             * @param sourceRangeExcludingCaldoc
             * @param functionNameSourceRange
             * @return a new instance of this class.
             */
            static Algebraic makeAnnotated (CALDoc.Comment.Function caldocComment, String functionName, Scope scope, boolean isScopeExplicitlySpecified, Parameter[] parameters, Expr definingExpr, SourceRange sourceRange, SourceRange sourceRangeExcludingCaldoc, SourceRange functionNameSourceRange) {
                return new Algebraic(caldocComment, functionName, scope, isScopeExplicitlySpecified, parameters, definingExpr, false, sourceRange, sourceRangeExcludingCaldoc, functionNameSourceRange);
            }
            
            /**
             * Do not make this method public!!!
             * An internal function in the source model for internal use by the compiler only.
             * The main difference is that functionName is validated differently.
             * 
             * @param functionName the name of the CAL function.
             * @param scope the scope of the function.
             * @param parameters the parameters of the function.
             * @param definingExpr the defining expression of the function.
             * @return Algebraic
             */
            static Algebraic makeInternal (String functionName, Scope scope, Parameter[] parameters, Expr definingExpr) {
                return new Algebraic(null, functionName, scope, true, parameters, definingExpr, true, null, null, null);
            }            
            
            /**
             * Do not make this method public!!!
             * An internal function in the source model for internal use by the compiler only.
             * The main difference is that functionName is validated differently.
             * 
             * @param caldocComment the CALDoc comment.
             * @param functionName the name of the CAL function.
             * @param scope the scope of the function.
             * @param parameters the parameters of the function.
             * @param definingExpr the defining expression of the function.
             * @return Algebraic
             */
            static Algebraic makeInternal (CALDoc.Comment.Function caldocComment, String functionName, Scope scope, Parameter[] parameters, Expr definingExpr) {
                return new Algebraic(caldocComment, functionName, scope, true, parameters, definingExpr, true, null, null, null);
            }            
            
     
            public Parameter[] getParameters() {
                if (parameters.length == 0) {
                    return NO_PARAMETERS;
                }
                
                return parameters.clone();
            }
            
            /**
             * Get the number of parameters.
             * @return the number of parameters.
             */
            public int getNParameters() {
                return parameters.length;
            }
            
            /**
             * Get the nth parameter.
             * @param n the index of the parameter to return.
             * @return the nth parameter.
             */
            public Parameter getNthParameter(int n) {
                return parameters[n];
            }
            
            public Expr getDefiningExpr() {
                return definingExpr;
            }
            
            @Override
            ParseTreeNode toParseTreeNode() {
                ParseTreeNode topLevelFunctionDefnNode = new ParseTreeNode(CALTreeParserTokenTypes.TOP_LEVEL_FUNCTION_DEFN, "TOP_LEVEL_FUNCTION_DEFN");
                ParseTreeNode optionalCALDocNode = makeOptionalCALDocNode(getCALDocComment());
                ParseTreeNode accessModifierNode = SourceModel.makeAccessModifierNode(getScope(), isScopeExplicitlySpecified());                
                ParseTreeNode functionNameNode = new ParseTreeNode(CALTreeParserTokenTypes.VAR_ID, getName());
                ParseTreeNode paramListNode = new ParseTreeNode(CALTreeParserTokenTypes.FUNCTION_PARAM_LIST, "FUNCTION_PARAM_LIST");
                ParseTreeNode exprNode = getDefiningExpr().toParseTreeNode();
                
                ParseTreeNode[] paramNodes = new ParseTreeNode[parameters.length];
                for (int i = 0; i < parameters.length; i++) {
                    paramNodes[i] = parameters[i].toParseTreeNode();
                }
                paramListNode.addChildren(paramNodes);
                
                topLevelFunctionDefnNode.setFirstChild(optionalCALDocNode);
                optionalCALDocNode.setNextSibling(accessModifierNode);
                accessModifierNode.setNextSibling(functionNameNode);
                functionNameNode.setNextSibling(paramListNode);
                paramListNode.setNextSibling(exprNode);
                return topLevelFunctionDefnNode;
            }
            
            /**
             * {@inheritDoc}
             */
            @Override
            public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                return visitor.visit_FunctionDefn_Algebraic(this, arg);
            }
        }
        
        /**
         * Models a CAL foreign function definition such as:
         * foreign unsafe import jvm "method getBlue" private getBlueFromColour :: Colour -> Int;
         * @author Bo Ilic
         */
        public static final class Foreign extends FunctionDefn {
            
            /** for example, something like "java.lang.Math.sin" */
            private final String externalName;
            /** The position in the source of the external name. This maybe be null. */
            private final SourceRange externalNameSourceRange;
            
            private final TypeSignature declaredType;
            
            private Foreign(CALDoc.Comment.Function caldocComment, String functionName, Scope scope, boolean isScopeExplicitlySpecified, String externalName, SourceRange externalNameSourceRange, TypeSignature declaredType, SourceRange sourceRange, SourceRange sourceRangeExcludingCaldoc, SourceRange functionNameSourceRange) {
                super(caldocComment, functionName, scope, isScopeExplicitlySpecified, false, sourceRange, sourceRangeExcludingCaldoc, functionNameSourceRange);
                
                verifyArg(externalName, "externalName");
                verifyArg(declaredType, "declaredType");               
                
                this.externalName = externalName;
                this.externalNameSourceRange = externalNameSourceRange;
                this.declaredType = declaredType;
            }

            /**
             * Create an instance of this class without an associated CALDoc comment.
             * @param functionName the name of the function.
             * @param scope the scope of the function.
             * @param externalName the external name of the function.
             * @param declaredType the declared type of the function.
             * @return a new instance of this class.
             */
            public static Foreign make(String functionName, Scope scope, String externalName, TypeSignature declaredType) {
                return new Foreign(null, functionName, scope, true, externalName, null, declaredType, null, null, null);
            }
            
            /**
             * Create an instance of this class with an associated CALDoc comment.
             * @param caldocComment the CALDoc comment.
             * @param functionName the name of the function.
             * @param scope the scope of the function.
             * @param isScopeExplicitlySpecified whether the scope is explicitly specified in the source.
             * @param externalName the external name of the function.
             * @param declaredType the declared type of the function.
             * @return a new instance of this class.
             */
            public static Foreign make(CALDoc.Comment.Function caldocComment, String functionName, Scope scope, boolean isScopeExplicitlySpecified, String externalName, TypeSignature declaredType) {
                return new Foreign(caldocComment, functionName, scope, isScopeExplicitlySpecified, externalName, null, declaredType, null, null, null);
            }
            
            static Foreign makeAnnotated(String functionName, Scope scope, boolean isScopeExplicitlySpecified, String externalName, SourceRange externalNameSourceRange, TypeSignature declaredType, SourceRange sourceRange, SourceRange sourceRangeExcludingCaldoc, SourceRange functionNameSourceRange) {
                return new Foreign(null, functionName, scope, isScopeExplicitlySpecified, externalName, externalNameSourceRange, declaredType, sourceRange, sourceRangeExcludingCaldoc, functionNameSourceRange);
            }
            
            static Foreign makeAnnotated(CALDoc.Comment.Function caldocComment, String functionName, Scope scope, boolean isScopeExplicitlySpecified, String externalName, SourceRange externalNameSourceRange, TypeSignature declaredType, SourceRange sourceRange, SourceRange sourceRangeExcludingCaldoc, SourceRange functionNameSourceRange) {
                return new Foreign(caldocComment, functionName, scope, isScopeExplicitlySpecified, externalName, externalNameSourceRange, declaredType, sourceRange, sourceRangeExcludingCaldoc, functionNameSourceRange);
            }

       
            public String getExternalName() {
                return externalName;
            }

            // todo-jowong maybe the name can be encapsulated by its own source element
            public SourceRange getExternalNameSourceRange() {
                return externalNameSourceRange;
            }
            
            public TypeSignature getDeclaredType() {
                return declaredType;
            }
            
            @Override
            ParseTreeNode toParseTreeNode() {
                ParseTreeNode foreignFunctionDeclarationNode = new ParseTreeNode(CALTreeParserTokenTypes.FOREIGN_FUNCTION_DECLARATION, "foreignFunction");
                ParseTreeNode optionalCALDocNode = makeOptionalCALDocNode(getCALDocComment());
                ParseTreeNode externalNameNode = new ParseTreeNode(CALTreeParserTokenTypes.STRING_LITERAL, StringEncoder.encodeString(externalName));
                ParseTreeNode accessModifierNode = SourceModel.makeAccessModifierNode(getScope(), isScopeExplicitlySpecified());
                ParseTreeNode typeDeclarationNode = new ParseTreeNode(CALTreeParserTokenTypes.TYPE_DECLARATION, "::");  
                ParseTreeNode functionNameNode = new ParseTreeNode(CALTreeParserTokenTypes.VAR_ID, getName());
                ParseTreeNode typeSignatureNode = declaredType.toParseTreeNode();
                
                foreignFunctionDeclarationNode.setFirstChild(optionalCALDocNode);
                optionalCALDocNode.setNextSibling(externalNameNode);
                externalNameNode.setNextSibling(accessModifierNode);
                accessModifierNode.setNextSibling(typeDeclarationNode);
                typeDeclarationNode.setFirstChild(functionNameNode);
                functionNameNode.setNextSibling(typeSignatureNode);
                return foreignFunctionDeclarationNode;
            }
            
            /**
             * 
             * @param accessibleObject A Method, Constructor, or Field, which you would like to refer to from CAL
             * @return The CAL string that should be passed into the externalName argument of the FunctionDefn.Foreign constructor
             */
            public static String makeExternalName(AccessibleObject accessibleObject) {
                StringBuilder buf = new StringBuilder();
                if (accessibleObject instanceof Method) {
                    Method method = (Method)accessibleObject;
                    if(Modifier.isStatic(method.getModifiers())) {
                        buf.append("static ");
                    }
                    buf.append("method ");
                    buf.append(method.getDeclaringClass().getName());
                    buf.append(".");
                    buf.append(method.getName());
                } else if (accessibleObject instanceof Constructor) {
                    Constructor<?> constructor = (Constructor<?>)accessibleObject;
                    buf.append("constructor ");
                    buf.append(constructor.getName());
                } else if (accessibleObject instanceof Field) {
                    Field field = (Field)accessibleObject;
                    if(Modifier.isStatic(field.getModifiers())) {
                        buf.append("static ");
                    }
                    buf.append("field ");
                    buf.append(field.getDeclaringClass().getName());
                    buf.append(".");
                    buf.append(field.getName());
                } else {
                    throw new IllegalArgumentException();
                }
                return buf.toString();
            }
            
            /**
             * {@inheritDoc}
             */
            @Override
            public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                return visitor.visit_FunctionDefn_Foreign(this, arg);
            }
            
        }
        
        /**
         * Models a CAL primitive function definition. These should never be created by external clients.
         * An example is:
         * primitive private equalsInt :: Int -> Int -> Boolean;
         * @author Bo Ilic
         */
        public static final class Primitive extends FunctionDefn {
            
            private final TypeSignature declaredType;
            
            private Primitive(CALDoc.Comment.Function caldocComment, String functionName, Scope scope, boolean isScopeExplicitlySpecified, TypeSignature declaredType, SourceRange sourceRange, SourceRange sourceRangeExcludingCaldoc, SourceRange functionNameSourceRange) {
                super(caldocComment, functionName, scope, isScopeExplicitlySpecified, false, sourceRange, sourceRangeExcludingCaldoc, functionNameSourceRange);
                
                verifyArg(declaredType, "declaredType");                  
                this.declaredType = declaredType;
            }

            /**
             * Create an instance of this class without an associated CALDoc comment.
             * @param functionName the name of the function.
             * @param scope the scope of the function.
             * @param declaredType the declared type of the function.
             * @return a new instance of this class.
             */
            static Primitive make(String functionName, Scope scope, TypeSignature declaredType) {
                return new Primitive(null, functionName, scope, true, declaredType, null, null, null);
            }

            /**
             * Create an instance of this class with an associated CALDoc comment.
             * @param caldocComment the CALDoc comment.
             * @param functionName the name of the function.
             * @param scope the scope of the function.
             * @param isScopeExplicitlySpecified whether the scope is explicitly specified in the source.
             * @param declaredType the declared type of the function.
             * @return a new instance of this class.
             */
            static Primitive make(CALDoc.Comment.Function caldocComment, String functionName, Scope scope, boolean isScopeExplicitlySpecified, TypeSignature declaredType) {
                return new Primitive(caldocComment, functionName, scope, isScopeExplicitlySpecified, declaredType, null, null, null);
            }

            static Primitive makeAnnotated(String functionName, Scope scope, boolean isScopeExplicitlySpecified, TypeSignature declaredType, SourceRange sourceRange, SourceRange sourceRangeExcludingCaldoc, SourceRange functionNameSourceRange) {
                return new Primitive(null, functionName, scope, isScopeExplicitlySpecified, declaredType, sourceRange, sourceRangeExcludingCaldoc, functionNameSourceRange);
            }
            
            static Primitive makeAnnotated(CALDoc.Comment.Function caldocComment, String functionName, Scope scope, boolean isScopeExplicitlySpecified, TypeSignature declaredType, SourceRange sourceRange, SourceRange sourceRangeExcludingCaldoc, SourceRange functionNameSourceRange) {
                return new Primitive(caldocComment, functionName, scope, isScopeExplicitlySpecified, declaredType, sourceRange, sourceRangeExcludingCaldoc, functionNameSourceRange);
            }

         
            public TypeSignature getDeclaredType() {
                return declaredType;
            }
            
            @Override
            ParseTreeNode toParseTreeNode() {
                ParseTreeNode primitiveFunctionNode = new ParseTreeNode(CALTreeParserTokenTypes.PRIMITIVE_FUNCTION_DECLARATION, "primitiveFunc");
                ParseTreeNode optionalCALDocNode = makeOptionalCALDocNode(getCALDocComment());
                ParseTreeNode accessModifierNode = SourceModel.makeAccessModifierNode(getScope(), isScopeExplicitlySpecified());
                ParseTreeNode typeDeclarationNode = new ParseTreeNode(CALTreeParserTokenTypes.TYPE_DECLARATION, "::");
                ParseTreeNode functionNameNode = new ParseTreeNode(CALTreeParserTokenTypes.VAR_ID, getName());                
                ParseTreeNode typeSignatureNode = declaredType.toParseTreeNode();
                
                primitiveFunctionNode.setFirstChild(optionalCALDocNode);
                optionalCALDocNode.setNextSibling(accessModifierNode);
                accessModifierNode.setNextSibling(typeDeclarationNode);
                typeDeclarationNode.setFirstChild(functionNameNode);
                functionNameNode.setNextSibling(typeSignatureNode);
                return primitiveFunctionNode;
            }
            
            /**
             * {@inheritDoc}
             */
            @Override
            public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                return visitor.visit_FunctionDefn_Primitive(this, arg);
            }
        }
        
        private FunctionDefn(CALDoc.Comment.Function caldocComment, String name, Scope scope, boolean isScopeExplicitlySpecified, boolean internalFunction, SourceRange sourceRange, SourceRange sourceRangeExcludingCaldoc, SourceRange nameSourceRange) {
            super(sourceRange);
            
            this.nameSourceRange = nameSourceRange;
            this.sourceRangeExcludingCaldoc = sourceRangeExcludingCaldoc;
            
            this.caldocComment = caldocComment;
            
            //internal functions are validated differently. This allows internal functions such as the instance
            //functions for derived instances to use user-hidden names starting with a $ such as $equalsMaybe
            if (internalFunction) {
                if (name.charAt(0) != '$') {
                    throw new IllegalArgumentException("internal function names must start with a $.");
                }
            } else {                
                if (!LanguageInfo.isValidFunctionName(name)) {
                    throw new IllegalArgumentException();
                }
            }
            
            verifyScopeArg(scope, isScopeExplicitlySpecified, "scope");
            
            this.name = name;
            this.scope = scope;
            this.isScopeExplicitlySpecified = isScopeExplicitlySpecified;
        }
        
        /**
         * @return the CALDoc comment associated with this function definition, or null if there is none.
         */
        public CALDoc.Comment.Function getCALDocComment() {
            return caldocComment;
        }
        
        public String getName() {
            return name;
        }
        
        public Scope getScope() {
            return scope;
        }
        
        /**
         * @return whether the scope is explicitly specified in the source.
         */
        public boolean isScopeExplicitlySpecified() {
            return isScopeExplicitlySpecified;
        }
        
        /**
         * @return An optional SourceRange specifying the range occupied by the name of the definition
         *          element in the original source text.  May return null.
         */
        // todo-jowong maybe the name can be encapsulated by its own source element
        SourceRange getNameSourceRange() {
            return nameSourceRange;
        }
        SourceRange getSourceRangeExcludingCaldoc() {
            return sourceRangeExcludingCaldoc;
        }
    }
    
    /**
     * Models a formal parameter to a function (top-level, local or lambda),
     * with a possible strictness annotation (indicated in CAL source by a pling !).
     * @author Bo Ilic
     */
    public static final class Parameter extends SourceElement {
        
        private final String name;
        private final boolean isStrict;
        
        private Parameter(String name, boolean isStrict, SourceRange sourceRange) {
            super(sourceRange);

            if (!LanguageInfo.isValidFunctionName(name)) {
                throw new IllegalArgumentException();
            }
            
            this.name = name;
            this.isStrict = isStrict;
        }

        public static Parameter make(String name, boolean isStrict) {
            return new Parameter(name, isStrict, null);
        }
        
        static Parameter makeAnnotated(String name, boolean isStrict, SourceRange sourceRange) {
            return new Parameter(name, isStrict, sourceRange);
        }
               
        public String getName() {
            return name;
        }
        
        public boolean isStrict() {
            return isStrict;
        }
        
        /**
         * @deprecated this is deprecated to signify that the returned source range is not the entire definition, but
         * rather a portion thereof.
         */
        // todo-jowong refactor this so that the primary source range of the source element is its entire source range
        @Deprecated
        SourceRange getSourceRange() {
            return getSourceRangeOfNameNotIncludingPotentialPling();
        }

        SourceRange getSourceRangeOfNameNotIncludingPotentialPling() {
            return super.getSourceRange();
        }

        @Override
        ParseTreeNode toParseTreeNode() {
            ParseTreeNode parameterNode;
            
            if(isStrict) {
                parameterNode = new ParseTreeNode(CALTreeParserTokenTypes.STRICT_PARAM, name);
            } else {
                parameterNode = new ParseTreeNode(CALTreeParserTokenTypes.LAZY_PARAM, name);
            }
            
            return parameterNode;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
            return visitor.visit_Parameter(this, arg);
        }
    }
    
    /**
     * Models a CAL expression occurring in the definition of regular CAL function. Note: type expressions are handled
     * by a different hierarchy.
     * @author Bo Ilic
     */
    public static abstract class Expr extends SourceElement {
        
        /**
         * A helper class to mark expressions as either left, right or non-associative.
         * 
         * @author Bo Ilic
         */
        static final class Associativity {
            
            private final String description;
            
            static final Associativity LEFT =  new Associativity("left associative");
            static final Associativity NON = new Associativity("non associative");
            static final Associativity RIGHT = new Associativity("right associative");
            
            
            private Associativity(String description) {
                this.description = description;
            }
            
            @Override
            public String toString() {
                return description;
            }
        }
        
        /**
         * Models a parenthesized expression
         *
         * @author Magnus Byne
         */
        public static final class Parenthesized extends Expr {
            private final Expr expr;
            
            private Parenthesized(Expr expr) {
                this(expr, null);
            }
            
            private Parenthesized(Expr expr, SourceRange sourceRange) {
                super(sourceRange);
                
                if (expr == null) {
                    throw new IllegalArgumentException("argument 'expr' cannot be null.");
                }
                this.expr = expr;
            }
            
            public static Parenthesized make(Expr expr) {
                return new Parenthesized(expr);
            }

            static Parenthesized makeAnnotated(Expr expr, SourceRange sourceRange) {
                return new Parenthesized(expr, sourceRange);
            }
            
            /**
             * {@inheritDoc}
             */
            @Override
            boolean neverNeedsParentheses() {  
                return true;
            }
            
            /**
             * {@inheritDoc}
             */
            @Override
            int precedenceLevel() {
                return 100;
            } 
            
            /**
             * {@inheritDoc}
             */
            @Override
            Associativity associativity() {
                return Associativity.NON;
            }
            
            /**
             * Get the expression that is contained within Parentheses
             * @return contents of the Parentheses
             */
            public Expr getExpression() {
                return expr;
            }
            
            /**
             * {@inheritDoc}
             */
            @Override
            ParseTreeNode toParseTreeNode() {
                ParseTreeNode parenExprNode = new ParseTreeNode (CALTreeParserTokenTypes.TUPLE_CONSTRUCTOR, "Tuple");
                
                parenExprNode.addChildren(new ParseTreeNode[] {expr.toParseTreeNode()});
                
                return parenExprNode;
            }
            
            /**
             * {@inheritDoc}
             */
            @Override
            public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                return visitor.visit_Expr_Parenthesized(this, arg);
            }
            
        }
        
        
        /**
         * Models a function application expression. The application may have 1 or more arguments. Some examples: "f x", "List.map sin [1.0, 2.0]".
         * @author Bo Ilic
         */         
        public static final class Application extends Expr {
            
            /** for example, if the application is f x y this is [f, x, y]. */
            private final Expr[] expressions;
            
            /**
             * If expressions is [e1, e2, e3], this is the application e1 e2 e3 i.e. (((e1) e2) e3)
             * @param expressions the expressions defining the application. Must have length >=2, with each element non-null.            
             */
            private Application(Expr[] expressions) {
                this(expressions, null);
            }
            
            /**
             * If expressions is [e1, e2, e3], this is the application e1 e2 e3 i.e. (((e1) e2) e3)
             * @param expressions the expressions defining the application. Must have length >=2, with each element non-null.
             * @param sourceRange SourceRange that the application occurs at.  May be null.            
             */
            private Application(Expr[] expressions, SourceRange sourceRange) {
                super(sourceRange);
                
                if (expressions == null || expressions.length < 2) {
                    throw new IllegalArgumentException("argument 'expressions' must have length >= 2.");
                }                
                this.expressions = expressions.clone();
                verifyArrayArg(this.expressions, "expressions");
            }
            
            /**
             * If expressions is [e1, e2, e3], this is the application e1 e2 e3 i.e. (((e1) e2) e3)
             * @param expressions the expressions defining the application. Must have length >=2, with each element non-null.
             * @return an instance of Application            
             */
            public static Application make(Expr[] expressions) {
                return new Application(expressions);
            }
            
            /**
             * If expressions is [e1, e2, e3], this is the application e1 e2 e3 i.e. (((e1) e2) e3)
             * @param expressions the expressions defining the application. Must have length >=2, with each element non-null.
             * @param sourceRange Optional position that this application occurs at      
             * @return an instance of Application            
             */
            static Application makeAnnotated(Expr[] expressions, SourceRange sourceRange) {
                return new Application(expressions, sourceRange);
            }
            
            @Override
            int precedenceLevel() {
                return 80;
            } 
            
            @Override
            Associativity associativity() {
                return Associativity.LEFT;
            }             
            
         
            @Override
            boolean neverNeedsParentheses() {  
                //for example, 'f x y' is the same as (f x) y
                //so if we want f (x y) then parentheses are needed.
                return false;
            }
            
            /**
             * @return Returns the expressions. For example, if the application is f x y this is [f, x, y]
             */
            public Expr[] getExpressions() {
                return expressions.clone();
            }
            
            /**
             * Get the number of expressions.
             * @return the number of expressions.
             */
            public int getNExpressions() {
                return expressions.length;
            }
            
            /**
             * Get the nth expression.
             * @param n the index of the expression to return.
             * @return the nth expression.
             */
            public Expr getNthExpression(int n) {
                return expressions[n];
            }
            
            @Override
            ParseTreeNode toParseTreeNode() {
                ParseTreeNode applicationExprNode = new ParseTreeNode (CALTreeParserTokenTypes.APPLICATION, "@");
                
                ParseTreeNode[] exprNodes = new ParseTreeNode[expressions.length];
                for (int i = 0; i < expressions.length; i++) {
                    exprNodes[i] = expressions[i].toParseTreeNode();
                }
                applicationExprNode.addChildren(exprNodes);
                
                return applicationExprNode;
            }
            
            /**
             * {@inheritDoc}
             */
            @Override
            public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                return visitor.visit_Expr_Application(this, arg);
            }
            
        }
        
        /**
         * Models a data constructor reference occurring in an expression in textual form. These can be
         * either qualified or unqualified. If possible, supply the module qualified form- this speeds up later processing.          
         * 
         * @author Bo Ilic
         */
        public static final class DataCons extends Expr {
            
            private final Name.DataCons dataConsName;
            
            private DataCons(Name.DataCons dataConsName, SourceRange sourceRange) {
                super(sourceRange);
                verifyArg(dataConsName, "dataConsName");
                this.dataConsName = dataConsName;
            }
            
            public static DataCons make(Name.DataCons dataConsName) {
                return new DataCons(dataConsName, null);
            }
            
            public static DataCons make(Name.Module moduleName, String dataConsName) {
                return make(Name.DataCons.make(moduleName, dataConsName));
            }
            
            public static DataCons make(ModuleName moduleName, String dataConsName) {
                return make(Name.Module.make(moduleName), dataConsName);
            }
            
            public static DataCons make(QualifiedName dataConsName) {
                return make(dataConsName.getModuleName(), dataConsName.getUnqualifiedName());
            }
            
            /**
             * @param unqualifiedDataConsName the unqualified data constructor name.
             * @return an unqualified data constructor reference occurring in an expression.
             */
            public static DataCons makeUnqualified(String unqualifiedDataConsName) {
                return make((Name.Module)null, unqualifiedDataConsName);
            }
            
            static DataCons makeAnnotated(Name.DataCons dataConsName, SourceRange sourceRange) {
                return new DataCons(dataConsName, sourceRange);
            }
            
            @Override
            int precedenceLevel() {
                return 100;
            }
            
            @Override
            Associativity associativity() {
                return Associativity.NON;
            }             
            
            
            @Override
            boolean neverNeedsParentheses() {
                return true;
            }
            
            public Name.DataCons getDataConsName() {
                return dataConsName;
            }
            
            @Override
            ParseTreeNode toParseTreeNode() {
                return dataConsName.toParseTreeNode();
            }
            
            /**
             * {@inheritDoc}
             */
            @Override
            public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                return visitor.visit_Expr_DataCons(this, arg);
            }
        }
        
        /**
         * Models a variable, function or class method reference occurring in an expression in textual form. These can be
         * either qualified or unqualified. If possible, supply the module qualified form- this speeds up later processing.          
         * 
         * @author Bo Ilic
         */         
        public static final class Var extends Expr {
            
            private final Name.Function varName;
            
            private Var(Name.Function varName, SourceRange sourceRange) {
                super(sourceRange);
                verifyArg(varName, "varName");
                this.varName = varName;
            }
            
            public static Var make(Name.Function varName) {
                return new Var(varName, null);
            }
            
            public static Var make(Name.Module moduleName, String varName) {
                return make(Name.Function.make(moduleName, varName));
            }
            
            public static Var make(ModuleName moduleName, String varName) {
                return make(Name.Module.make(moduleName), varName);
            }
            
            public static Var make(QualifiedName varName) {
                return make(varName.getModuleName(), varName.getUnqualifiedName());
            }
            
            /**
             * @param unqualifiedVarName the unqualified name for the variable, function or class method.
             * @return an unqualified variable, function or class method reference occurring in an expression.
             */
            public static Var makeUnqualified(String unqualifiedVarName) {
                return make((Name.Module)null, unqualifiedVarName);
            }
            
            static Var makeAnnotated(Name.Function varName, SourceRange sourceRange) {
                return new Var(varName, sourceRange);
            }
            
            /**
             * Do not make this method public!!!
             * An internal function in the source model for internal use by the compiler only.
             * The main difference is that varName is validated differently.
             * 
             * @param moduleName the name of the module.
             * @param varName the name of the internal variable or function.
             * 
             * @return a new Var instance representing a reference to an internal variable or function.
             */
            static Var makeInternal(Name.Module moduleName, String varName) {
                Name.Function name = new Name.Function(moduleName, varName, true, null, null);
                return make(name);
            }
            
            @Override
            int precedenceLevel() {
                return 100;
            }
            
            @Override
            Associativity associativity() {
                return Associativity.NON;
            }             
            
           
            @Override
            boolean neverNeedsParentheses() {
                return true;
            }              
            
            @Override
            ParseTreeNode toParseTreeNode() {
                return varName.toParseTreeNode();
            }
            
            /**
             * {@inheritDoc}
             */
            @Override
            public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                return visitor.visit_Expr_Var(this, arg);
            }
            
            /**
             * @return the variable name
             */
            public Name.Function getVarName() {
                return varName;
            }
        }
        
        /**
         * Models a let x in exp; expression in CAL source.
         * @author Bo Ilic
         */
        public static final class Let extends Expr {
            
            private final LocalDefn[] localDefns;
            
            /** expression following the 'in' part of the let. */
            private final Expr inExpr;
            
            private Let (LocalDefn[] localDefns, Expr inExpr) {
                if (localDefns == null || localDefns.length < 1) {
                    throw new IllegalArgumentException("must have at least 1 local definition.");
                }                 
                this.localDefns = localDefns.clone();
                verifyArrayArg(this.localDefns, "localFunctions");                 
                
                verifyArg(inExpr, "inExpr");                 
                
                this.inExpr = inExpr;                 
            }
            
            public static Let make(LocalDefn[] localDefns, Expr inExpr) {
                return new Let(localDefns, inExpr);
            }
            
            @Override
            int precedenceLevel() {
                return 0;
            }
            
            @Override
            Associativity associativity() {
                return Associativity.NON;
            }             
              
            @Override
            boolean neverNeedsParentheses() {                
                return false;
            }
            
            /**
             * @return Returns the array of local definition defined within the let.
             */
            public LocalDefn[] getLocalDefinitions() {
                return localDefns.clone();
            }                  
            
            /**
             * Get the number of local definitions.
             * @return the number of local definitions.
             */
            public int getNLocalDefinitions() {
                return localDefns.length;
            }
            
            /**
             * Get the nth local definition.
             * @param n the index of the local definition to return.
             * @return the nth local definition.
             */
            public LocalDefn getNthLocalDefinition(int n) {
                return localDefns[n];
            }

            /**
             * @return Returns expression following the 'in' part of the let.
             */
            public Expr getInExpr() {
                return inExpr;
            }                    
            
            @Override
            ParseTreeNode toParseTreeNode() {
                ParseTreeNode letExprNode = new ParseTreeNode(CALTreeParserTokenTypes.LITERAL_let, "let");
                ParseTreeNode defnListNode = new ParseTreeNode(CALTreeParserTokenTypes.LET_DEFN_LIST, "LET_DEFN_LITS");
                ParseTreeNode inExprNode = inExpr.toParseTreeNode();
                
                ParseTreeNode[] defnNodes = new ParseTreeNode[localDefns.length];
                for (int i = 0; i < localDefns.length; i++) {
                    defnNodes[i] = localDefns[i].toParseTreeNode();
                }
                defnListNode.addChildren(defnNodes);
                
                letExprNode.setFirstChild(defnListNode);
                defnListNode.setNextSibling(inExprNode);
                return letExprNode;
            }
            
            /**
             * {@inheritDoc}
             */
            @Override
            public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                return visitor.visit_Expr_Let(this, arg);
            }
        }
        
        /**
         * Models a case expression in CAL source.
         * These are used for inspecting the value of a CAL expression and unpacking its components depending
         * upon its particular format. For example, we can unpack data constructor or record values. 
         * 
         * @author Bo Ilic
         */
        public static final class Case extends Expr {
            
            private final Expr conditionExpr;
            
            /** must have 1 or more alternatives. */
            private final Alt[] caseAlts;
            
            /**
             * Base class for case alteratives. This consists of the pattern matching the particular case, along with the expression
             * on the right hand side of the "->".
             *               
             * @author Bo Ilic
             */
            public static abstract class Alt extends SourceElement {
                
                /** the expression that occurs on the right hand side of the "->" in a case alternative. */
                private final Expr altExpr;
                
                /**
                 * Models the unpacking of a group of general data constructors, where the data constructor's name is textual.
                 * (ie. not supported by special CAL syntax).
                 * For example, anything except an underscore on the lhs of an of an arrow in the following:
                 * 
                 * public operatorPrecedence func = 
                 *     case func of    
                 *     (OpEq | OpLt | OpLtEq | OpGt | OpGtEq) -> 4;  
                 *     OpNotEq -> 4;
                 *     (OpIsNull | OpIsNotNull) -> 8;
                 *     _ -> 0; // Not an operator.
                 * 
                 * @author Bo Ilic
                 */
                public static final class UnpackDataCons extends Alt {
                    
                    private final Name.DataCons[] dataConsNames;
                    
                    private final ArgBindings argBindings;
                    
                    /** true if the datacons is parenthesized*/
                    private final boolean parenthesized;
                    
                    private UnpackDataCons(Name.DataCons[] dataConsNames, ArgBindings argBindings, Expr altExpr, SourceRange sourceRange, boolean parenthesized) {
                        super(altExpr, sourceRange);
                        
                        this.parenthesized = parenthesized;
                        this.argBindings = (argBindings == null) ? ArgBindings.NO_BINDINGS : argBindings;
                        
                        verifyArrayArg(dataConsNames, "dataConsNames");
                        if (dataConsNames.length == 0) {
                            throw new IllegalArgumentException("dataConsNames must have at least one element.");
                        }
                        this.dataConsNames = dataConsNames.clone();
                    }
                    
                    /** @return true if the data cons should be parenthesized*/
                    public boolean getParenthesized() {
                        return parenthesized;
                    }
                    
                    public static UnpackDataCons make(Name.DataCons dataConsName, Expr altExpr) {
                        return new UnpackDataCons(new Name.DataCons[]{dataConsName}, null, altExpr, null, false);
                    }

                    public static UnpackDataCons make(Name.DataCons dataConsName, ArgBindings argBindings, Expr altExpr) {
                        return new UnpackDataCons(new Name.DataCons[]{dataConsName}, argBindings, altExpr, null, false);
                    }

                    public static UnpackDataCons make(Name.DataCons dataConsName, Pattern[] patterns, Expr altExpr) {
                        ArgBindings argBindings = ArgBindings.Positional.make(patterns);
                        return new UnpackDataCons(new Name.DataCons[]{dataConsName}, argBindings, altExpr, null, false);
                    }
                    
                    public static UnpackDataCons make(Name.DataCons dataConsName, FieldPattern[] fieldPatterns, Expr altExpr) {
                        ArgBindings argBindings = ArgBindings.Matching.make(fieldPatterns);
                        return new UnpackDataCons(new Name.DataCons[]{dataConsName}, argBindings, altExpr, null, false);
                    }
                    
                    public static UnpackDataCons make(Name.DataCons[] dataConsNames, Expr altExpr) {
                        return new UnpackDataCons(dataConsNames, null, altExpr, null, false);
                    }

                    public static UnpackDataCons make(Name.DataCons[] dataConsNames, ArgBindings argBindings, Expr altExpr) {
                        return new UnpackDataCons(dataConsNames, argBindings, altExpr, null, false);
                    }

                    public static UnpackDataCons make(Name.DataCons[] dataConsNames, Pattern[] patterns, Expr altExpr) {
                        ArgBindings argBindings = ArgBindings.Positional.make(patterns);
                        return new UnpackDataCons(dataConsNames, argBindings, altExpr, null, false);
                    }
                    
                    public static UnpackDataCons make(Name.DataCons[] dataConsNames, FieldPattern[] fieldPatterns, Expr altExpr) {
                        ArgBindings argBindings = ArgBindings.Matching.make(fieldPatterns);
                        return new UnpackDataCons(dataConsNames, argBindings, altExpr, null, false);
                    }
                    
                    static UnpackDataCons makeAnnotated(Name.DataCons dataConsName, Expr altExpr, SourceRange sourceRange) {
                        return new UnpackDataCons(new Name.DataCons[]{dataConsName}, null, altExpr, sourceRange, false);
                    }

                    static UnpackDataCons makeAnnotated(Name.DataCons[] dataConsNames, ArgBindings argBindings, Expr altExpr, SourceRange sourceRange) {
                        return new UnpackDataCons(dataConsNames, argBindings, altExpr, sourceRange, false);
                    }

                    static UnpackDataCons makeAnnotated(Name.DataCons[] dataConsNames, ArgBindings argBindings, Expr altExpr, SourceRange sourceRange, boolean parenthesized) {
                        return new UnpackDataCons(dataConsNames, argBindings, altExpr, sourceRange, parenthesized);
                    }

                    static UnpackDataCons makeAnnotated(Name.DataCons[] dataConsNames, Pattern[] patterns, Expr altExpr, SourceRange sourceRange) {
                        ArgBindings argBindings = ArgBindings.Positional.make(patterns);
                        return new UnpackDataCons(dataConsNames, argBindings, altExpr, sourceRange, false);
                    }
                    
                    static UnpackDataCons makeAnnotated(Name.DataCons[] dataConsNames, FieldPattern[] fieldPatterns, Expr altExpr, SourceRange sourceRange) {
                        ArgBindings argBindings = ArgBindings.Matching.make(fieldPatterns);
                        return new UnpackDataCons(dataConsNames, argBindings, altExpr, sourceRange, false);
                    }
 
                    static UnpackDataCons makeAnnotated(Name.DataCons[] dataConsNames, Pattern[] patterns, Expr altExpr, SourceRange sourceRange, boolean parenthesized) {
                        ArgBindings argBindings = ArgBindings.Positional.make(patterns);
                        return new UnpackDataCons(dataConsNames, argBindings, altExpr, sourceRange, parenthesized);
                    }
                    
                    static UnpackDataCons makeAnnotated(Name.DataCons[] dataConsNames, FieldPattern[] fieldPatterns, Expr altExpr, SourceRange sourceRange, boolean parenthesized) {
                        ArgBindings argBindings = ArgBindings.Matching.make(fieldPatterns);
                        return new UnpackDataCons(dataConsNames, argBindings, altExpr, sourceRange, parenthesized);
                    }
  
                    public Name.DataCons[] getDataConsNames() {
                        return dataConsNames.clone();
                    }
                    
                    public int getNDataConsNames() {
                        return dataConsNames.length;
                    }
                    
                    public Name.DataCons getNthDataConsName(int n) {
                        return dataConsNames[n];
                    }
                    
                    public ArgBindings getArgBindings() {
                        return argBindings;
                    }
                    
                    @Override
                    ParseTreeNode buildPatternParseTreeNode() {
                        ParseTreeNode fullPatternNode = new ParseTreeNode(CALTreeParserTokenTypes.PATTERN_CONSTRUCTOR, "PATTERN_CONSTURCTOR");
                        {
                            ParseTreeNode dataConsNameListNode;
                            if (parenthesized || dataConsNames.length > 1) {
                                dataConsNameListNode = new ParseTreeNode(CALTreeParserTokenTypes.DATA_CONSTRUCTOR_NAME_LIST, "DATA_CONSTRUCTOR_NAME_LIST");
                            } else {
                                dataConsNameListNode = new ParseTreeNode(CALTreeParserTokenTypes.DATA_CONSTRUCTOR_NAME_SINGLETON, "DATA_CONSTRUCTOR_NAME_SINGLETON");
                            }
                            
                            fullPatternNode.setFirstChild(dataConsNameListNode);
                            {
                                ParseTreeNode[] dataConsNameNodes = new ParseTreeNode[dataConsNames.length];
                                for (int i = 0; i < dataConsNames.length; i++) {
                                    dataConsNameNodes[i] = dataConsNames[i].toParseTreeNode();
                                }
                                dataConsNameListNode.addChildren(dataConsNameNodes);
                            }
                            
                            ParseTreeNode argBindingsNode = argBindings.toParseTreeNode();
                            dataConsNameListNode.setNextSibling(argBindingsNode);
                        }
                        
                        return fullPatternNode;
                    }
                    
                    /**
                     * {@inheritDoc}
                     */
                    @Override
                    public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                        return visitor.visit_Expr_Case_Alt_UnpackDataCons(this, arg);
                    }
                }
                
                /**
                 * Models the unpacking of int values. For example,
                 * this is the "1 -> True;" or "(2|3) -> True;" part of the following function:               
                 * 
                 * public isTwoOrThree !intVal =
                 * case intVal of
                 *     1 -> True
                 *     (2|3) -> True;
                 *     _ -> False;
                 *     ;
                 * 
                 * @author Edward Lam
                 */
                public static final class UnpackInt extends Alt {
                    
                    /** the int values */
                    private final BigInteger[] bigIntValues;
                    
                    private UnpackInt(BigInteger[] intValues, Expr altExpr) {
                        super (altExpr, null);
                        this.bigIntValues = intValues.clone();
                        verifyArrayArg(intValues, "intValues");
                        if (intValues.length == 0) {
                            throw new IllegalArgumentException("intValues must have at least one element.");
                        }

                    }
                    
                    public static UnpackInt make(BigInteger[] bigIntValues, Expr altExpr) {
                        return new UnpackInt(bigIntValues, altExpr);
                    }
                    
             
                    public BigInteger[] getIntValues() {
                        return bigIntValues.clone();
                    }
                    
                    @Override
                    ParseTreeNode buildPatternParseTreeNode() {
                        ParseTreeNode fullPatternNode = new ParseTreeNode(CALTreeParserTokenTypes.INT_PATTERN, "INT_PATTERN");
                        {
                            ParseTreeNode intListNode = new ParseTreeNode(CALTreeParserTokenTypes.MAYBE_MINUS_INT_LIST, "MAYBE_MINUS_INT_LIST");
                            fullPatternNode.setFirstChild(intListNode);
                            {
                                ParseTreeNode maybeMinusNodes[] = new ParseTreeNode[bigIntValues.length];
                                for (int i = 0; i < bigIntValues.length; i++) {
                                    BigInteger bigIntValue = bigIntValues[i];
                                    if (bigIntValue.signum() < 0) {
                                        maybeMinusNodes[i] = new ParseTreeNode(CALTreeParserTokenTypes.MINUS, "-");
                                        maybeMinusNodes[i].setFirstChild(
                                                new ParseTreeNode(CALTreeParserTokenTypes.INTEGER_LITERAL, bigIntValue.abs().toString()));
                                        
                                    } else {
                                        maybeMinusNodes[i] = new ParseTreeNode(CALTreeParserTokenTypes.INTEGER_LITERAL, bigIntValue.toString());
                                    }
                                }
                                intListNode.addChildren(maybeMinusNodes);
                            }
                        }
                        
                        return fullPatternNode;
                    }
                    
                    /**
                     * {@inheritDoc}
                     */
                    @Override
                    public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                        return visitor.visit_Expr_Case_Alt_UnpackInt(this, arg);
                    }
                }
                
                /**
                 * Models the unpacking of char values. For example,
                 * this is the "('A'|'B') -> True;" or "'d' -> True;" part of the following function:               
                 * 
                 * public isAorB !charVal =
                 * case charVal of
                 *     ('A'|'B') -> True;
                 *     'd' -> True;
                 *     _ -> False;
                 *     ;
                 * 
                 * @author Edward Lam
                 */
                public static final class UnpackChar extends Alt {
                    
                    /** the char values */
                    private final char[] charValues;
                    
                    private UnpackChar(char[] charValues, Expr altExpr) {
                        super (altExpr, null);
                        this.charValues = charValues.clone();      // npe if null.
                        if (charValues.length == 0) {
                            throw new IllegalArgumentException("charValues must have at least one element.");
                        }
                    }
                    
                    public static UnpackChar make(char[] charValues, Expr altExpr) {
                        return new UnpackChar(charValues, altExpr);
                    }
                    
          
                    public char[] getCharValues() {
                        return charValues.clone();
                    }
                    
                    @Override
                    ParseTreeNode buildPatternParseTreeNode() {
                        ParseTreeNode fullPatternNode = new ParseTreeNode(CALTreeParserTokenTypes.CHAR_PATTERN, "CHAR_PATTERN");
                        {
                            ParseTreeNode intListNode = new ParseTreeNode(CALTreeParserTokenTypes.CHAR_LIST, "CHAR_LIST");
                            fullPatternNode.setFirstChild(intListNode);
                            {
                                ParseTreeNode charNodes[] = new ParseTreeNode[charValues.length];
                                for (int i = 0; i < charValues.length; i++) {
                                    char charValue = charValues[i];
                                    String symbolText = StringEncoder.encodeChar(charValue);
                                    charNodes[i] = new ParseTreeNode(CALTreeParserTokenTypes.CHAR_LITERAL, symbolText);
                                }
                                intListNode.addChildren(charNodes);
                            }
                        }
                        
                        return fullPatternNode;
                    }
                    
                    /**
                     * {@inheritDoc}
                     */
                    @Override
                    public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                        return visitor.visit_Expr_Case_Alt_UnpackChar(this, arg);
                    }
                }
                
                /**
                 * Models the unpacking of a tuple data constructor in operator form.
                 * For example, the "(_, x, _) -> x;" part of the following function:
                 * 
                 * public tuple3Field2 !t =
                 *     case t of
                 *     (_, x, _) -> x;
                 *     ;
                 * 
                 * @author Bo Ilic
                 */
                public static final class UnpackTuple extends Alt {
                    
                    private final Pattern[] patterns;
                    
                    private UnpackTuple(Pattern[] patterns, Expr altExpr) {
                        super(altExpr, null);
                        
                        if (patterns == null || patterns.length == 0) {
                            this.patterns = Pattern.NO_PATTERNS;
                        } else {
                            this.patterns = patterns.clone();
                            verifyArrayArg(this.patterns, "patterns");                                       
                        }                                                  
                    }
                    
                    public static UnpackTuple make(Pattern[] patterns, Expr altExpr) {
                        return new UnpackTuple(patterns, altExpr);
                    }
                    
                 
                    public Pattern[] getPatterns() {
                        if (patterns.length == 0) {
                            return Pattern.NO_PATTERNS;
                        }
                        
                        return patterns.clone();
                    }           
                    
                    /**
                     * Get the number of patterns.
                     * @return the number of patterns.
                     */
                    public int getNPatterns() {
                        return patterns.length;
                    }
                    
                    /**
                     * Get the nth pattern.
                     * @param n the index of the pattern to return.
                     * @return the nth pattern.
                     */
                    public Pattern getNthPattern(int n) {
                        return patterns[n];
                    }

                    @Override
                    ParseTreeNode buildPatternParseTreeNode() {
                        ParseTreeNode fullPatternNode = new ParseTreeNode(CALTreeParserTokenTypes.TUPLE_CONSTRUCTOR, "Tuple");                        
                        
                        ParseTreeNode[] patternNodes = new ParseTreeNode[patterns.length];
                        for (int i = 0; i < patterns.length; i++) {
                            patternNodes[i] = patterns[i].toParseTreeNode();
                        }
                        fullPatternNode.addChildren(patternNodes);
                        
                        return fullPatternNode;
                    }
                    
                    /**
                     * {@inheritDoc}
                     */
                    @Override
                    public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                        return visitor.visit_Expr_Case_Alt_UnpackTuple(this, arg);
                    }
                }
                
                /**
                 * Models the unpacking of the "()" data constructor (this is the operator form of Prelude.Unit).
                 * For example, the "() -> "unit"" part of the following function:
                 * unitToString x =
                 *     case x of
                 *     () -> "unit";
                 *     ;
                 * 
                 * @author Bo Ilic
                 */                 
                public static final class UnpackUnit extends Alt {
                    
                    private UnpackUnit(Expr altExpr, SourceRange sourceRange) {
                        super(altExpr, sourceRange);
                    }
                    
                    public static UnpackUnit make(Expr altExpr) {
                        return new UnpackUnit(altExpr, null);
                    }
                    
                    static UnpackUnit makeAnnotated(Expr altExpr, SourceRange sourceRange) {
                        return new UnpackUnit(altExpr, sourceRange);
                    }
                    
                    
                    @Override
                    ParseTreeNode buildPatternParseTreeNode() {
                        ParseTreeNode fullPatternNode = new ParseTreeNode(CALTreeParserTokenTypes.TUPLE_CONSTRUCTOR, "Tuple");                       
                        return fullPatternNode;
                    }
                    
                    /**
                     * {@inheritDoc}
                     */
                    @Override
                    public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                        return visitor.visit_Expr_Case_Alt_UnpackUnit(this, arg);
                    }
                }        
                
                /**
                 * Models the unpacking of the "[]" data constructor (this is the operator form of Prelude.Nil). For example,
                 * this is the "[] -> error "List.init: empty list.";" part of the following function:                  
                 * 
                 * public init !xs =
                 * case xs of
                 *     a : as -> if isEmpty as then [] else a : init as;
                 *     []     -> error "List.init: empty list.";
                 *     ;
                 * 
                 * @author Bo Ilic
                 */                 
                public static final class UnpackListNil extends Alt {
                    
                    private UnpackListNil(Expr altExpr, SourceRange sourceRange) {
                        super(altExpr, sourceRange);
                    }
                    
                    public static UnpackListNil make(Expr altExpr) {
                        return new UnpackListNil(altExpr, null);
                    }
                    
                    static UnpackListNil makeAnnotated(Expr altExpr, SourceRange sourceRange) {
                        return new UnpackListNil(altExpr, sourceRange);
                    }
                    
                    
                    @Override
                    ParseTreeNode buildPatternParseTreeNode() {
                        ParseTreeNode fullPatternNode = new ParseTreeNode(CALTreeParserTokenTypes.LIST_CONSTRUCTOR, CAL_Prelude.TypeConstructors.List.getUnqualifiedName());                        
                        return fullPatternNode;
                    }
                    
                    /**
                     * {@inheritDoc}
                     */
                    @Override
                    public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                        return visitor.visit_Expr_Case_Alt_UnpackListNil(this, arg);
                    }
                }
                
                /**
                 * Models the unpacking of the ":" data constructor (this is the operator form of Prelude.Cons). For example,
                 * this is the "a : as -> if isEmpty as then [] else a : init as;" part of the following function:               
                 * 
                 * public init !xs =
                 * case xs of
                 *     a : as -> if isEmpty as then [] else a : init as;
                 *     []     -> error "List.init: empty list.";
                 *     ;
                 * 
                 * @author Bo Ilic
                 */
                public static final class UnpackListCons extends Alt {
                    
                    /** the pattern to the left of the ":" */
                    private final Pattern headPattern;       
                    
                    /** the pattern to the right of the ":" */
                    private final Pattern tailPattern;
                    
                    /** The SourceRange of the ":" */
                    private final SourceRange operatorSourceRange;
                    
                    private UnpackListCons(Pattern headPattern, Pattern tailPattern, Expr altExpr, SourceRange sourceRange, SourceRange operatorSourceRange) {
                        super (altExpr, sourceRange);
                        
                        verifyArg(headPattern, "headPattern");
                        verifyArg(tailPattern, "tailPattern");
                        
                        this.headPattern = headPattern;
                        this.tailPattern = tailPattern;
                        this.operatorSourceRange = operatorSourceRange;
                    }
                    
                    public static UnpackListCons make(Pattern headPattern, Pattern tailPattern, Expr altExpr) {
                        return new UnpackListCons(headPattern, tailPattern, altExpr, null, null);
                    }
                    
                    static UnpackListCons makeAnnotated(Pattern headPattern, Pattern tailPattern, Expr altExpr, SourceRange sourceRange, SourceRange operatorSourceRange) {
                        return new UnpackListCons(headPattern, tailPattern, altExpr, sourceRange, operatorSourceRange);
                    }
                    
                     
                    public Pattern getHeadPattern() {
                        return headPattern;
                    }
                    
                    public Pattern getTailPattern() {
                        return tailPattern;
                    }
                    
                    // todo-jowong maybe the operator can be encapsulated by its own source element
                    SourceRange getOperatorSourceRange() {
                        return operatorSourceRange;
                    }
                    
                    @Override
                    ParseTreeNode buildPatternParseTreeNode() {
                        ParseTreeNode fullPatternNode = new ParseTreeNode(CALTreeParserTokenTypes.COLON, ":");
                        ParseTreeNode headPatternNode = headPattern.toParseTreeNode();
                        ParseTreeNode tailPatternNode = tailPattern.toParseTreeNode();
                        
                        fullPatternNode.setFirstChild(headPatternNode);
                        headPatternNode.setNextSibling(tailPatternNode);
                        return fullPatternNode;
                    }
                    
                    /**
                     * {@inheritDoc}
                     */
                    @Override
                    public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                        return visitor.visit_Expr_Case_Alt_UnpackListCons(this, arg);
                    }
                }
                
                /**
                 * Models unpacking record values using a case expression. 
                 * 
                 * For example, the "{_ | #1 = f1, #2 = f2} -> (f1, f2);" part of the following function:
                 * 
                 * public recordToTuple2 !r =
                 *     case r of
                 *     {_ | #1 = f1, #2 = f2} -> (f1, f2);
                 *     ;
                 *     
                 * Note that "case r of {s | } -> ..." is not equivalent to "case r of {s} -> ..."
                 * the first is doing a record polymorphic record match, the second is doing a case on a record with 1 field named "s"
                 * and using punning on the field name s.
                 * 
                 * @author Bo Ilic
                 */
                public static final class UnpackRecord extends Alt {
                    
                    /**
                     * The pattern to the left of the | in a record case.
                     * For example, the "r" in "{r | #1 = f1, #2 = f2} -> ...". 
                     * May be null, to represent a non-record polymorphic pattern match e.g. "{#1 = f1, #2 = f3} -> ..." 
                     */
                    private final Pattern baseRecordPattern;
                    
                    private final FieldPattern[] fieldPatterns;
                    
                    private UnpackRecord(Pattern baseRecordPattern, FieldPattern[] fieldPatterns, Expr altExpr) {                         
                        super(altExpr, null);
                        
                        if (fieldPatterns == null || fieldPatterns.length == 0) {
                            this.fieldPatterns = FieldPattern.NO_FIELD_PATTERNS;
                        } else {
                            this.fieldPatterns = fieldPatterns.clone();
                            verifyArrayArg(this.fieldPatterns, "fieldPatterns");                                       
                        }
                        
                        this.baseRecordPattern = baseRecordPattern;
                    }                     

                    public static UnpackRecord make(Pattern baseRecordPattern, FieldPattern[] fieldPatterns, Expr altExpr) {
                        return new UnpackRecord(baseRecordPattern, fieldPatterns, altExpr);
                    }
                    
                    public Pattern getBaseRecordPattern() {
                        return baseRecordPattern;
                    }
                    
                    public FieldPattern[] getFieldPatterns() {
                        if (fieldPatterns.length == 0) {
                            return FieldPattern.NO_FIELD_PATTERNS;
                        }
                        
                        return fieldPatterns.clone();
                    }
                    
                    /**
                     * Get the number of field patterns.
                     * @return the number of field patterns.
                     */
                    public int getNFieldPatterns() {
                        return fieldPatterns.length;
                    }
                    
                    /**
                     * Get the nth field pattern.
                     * @param n the index of the field pattern to return.
                     * @return the nth field pattern.
                     */
                    public FieldPattern getNthFieldPattern(int n) {
                        return fieldPatterns[n];
                    }

                    @Override
                    ParseTreeNode buildPatternParseTreeNode() {
                        ParseTreeNode fullPatternNode = new ParseTreeNode(CALTreeParserTokenTypes.RECORD_PATTERN, "RECORD_PATTERN");
                        ParseTreeNode baseRecordPatternNode = new ParseTreeNode(CALTreeParserTokenTypes.BASE_RECORD_PATTERN, "BASE_RECORD_PATTERN");
                        ParseTreeNode fieldBindingVarAssignmentListNode = new ParseTreeNode(CALTreeParserTokenTypes.FIELD_BINDING_VAR_ASSIGNMENT_LIST, "FIELD_BINDING_VAR_ASSIGNMENT_LIST");
                        
                        if(baseRecordPattern != null) {
                            baseRecordPatternNode.setFirstChild(baseRecordPattern.toParseTreeNode());
                        }
                        
                        ParseTreeNode[] fieldPatternNodes = new ParseTreeNode[fieldPatterns.length];
                        for (int i = 0; i < fieldPatterns.length; i++) {
                            fieldPatternNodes[i] = fieldPatterns[i].toParseTreeNode();
                        }
                        fieldBindingVarAssignmentListNode.addChildren(fieldPatternNodes);
                        
                        fullPatternNode.setFirstChild(baseRecordPatternNode);
                        baseRecordPatternNode.setNextSibling(fieldBindingVarAssignmentListNode);
                        return fullPatternNode;
                    }
                    
                    /**
                     * {@inheritDoc}
                     */
                    @Override
                    public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                        return visitor.visit_Expr_Case_Alt_UnpackRecord(this, arg);
                    }
                }
                
                /**
                 * Models the default or wildcard pattern "_ -> expr" that matches any of the cases not given explicitly as a case alternative.
                 * 
                 * @author Bo Ilic
                 */
                public static final class Default extends Alt {
                    
                    private Default(Expr altExpr, SourceRange range) {
                        super(altExpr, range);
                    }
                    
                    public static Default make(Expr altExpr) {
                        return new Default(altExpr, null);
                    }
                    static Default makeAnnotated(Expr altExpr, SourceRange range) {
                        return new Default(altExpr, range);
                    }
            
                   
                    @Override
                    ParseTreeNode buildPatternParseTreeNode() {
                        ParseTreeNode fullPatternNode = new ParseTreeNode(CALTreeParserTokenTypes.UNDERSCORE, "_");                        
                        return fullPatternNode;
                    }
                    
                    /**
                     * {@inheritDoc}
                     */
                    @Override
                    public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                        return visitor.visit_Expr_Case_Alt_Default(this, arg);
                    }
                }
                
                private Alt(Expr altExpr, SourceRange sourceRange) {
                    super(sourceRange);
                    verifyArg(altExpr, "altExpr");
                    this.altExpr = altExpr;
                }
                
               /**                  
                 * @return Expr the expression that occurs on the right hand side of the "->" in a case alternative. 
                 */
                public Expr getAltExpr() {
                    return altExpr;
                }
                
                /**
                 * @return A ParseTreeNode generated from the pattern associated with the pattern expression. 
                 */
                abstract ParseTreeNode buildPatternParseTreeNode();
                
                @Override
                ParseTreeNode toParseTreeNode() {
                    ParseTreeNode altNode = new ParseTreeNode(CALTreeParserTokenTypes.ALT, "ALT");                        
                    ParseTreeNode fullPatternNode = buildPatternParseTreeNode();                        
                    ParseTreeNode altExprNode = getAltExpr().toParseTreeNode();
                    
                    altNode.addChild(fullPatternNode);
                    fullPatternNode.setNextSibling(altExprNode);                        
                    return altNode;
                }                 
            }
            
            private Case(Expr conditionExpr, Alt[] caseAlts, SourceRange sourceRange) {
                super(sourceRange);
                
                if (caseAlts == null || caseAlts.length < 1) {
                    throw new IllegalArgumentException("the array argument 'caseAlts' must have at least 1 element.");
                }
                
                this.caseAlts = caseAlts.clone();
                verifyArrayArg(this.caseAlts, "caseAlts");
                
                verifyArg(conditionExpr, "conditionExpr");
                this.conditionExpr = conditionExpr;
            }
            
            public static Case make(Expr conditionExpr, Alt[] caseAlts) {
                return new Case(conditionExpr, caseAlts, null);
            }
            
            static Case makeAnnotated(Expr conditionExpr, Alt[] caseAlts, SourceRange sourceRange) {
                return new Case(conditionExpr, caseAlts, sourceRange);
            }
            
            @Override
            int precedenceLevel() {
                return 0;
            } 
            
            @Override
            Associativity associativity() {
                return Associativity.NON;
            }
            
            public Expr getConditionExpr() {
                return conditionExpr;
            }
            
            public Case.Alt[] getCaseAlts() {
                return caseAlts.clone();
            }
            
            /**
             * Get the number of case alternatives.
             * @return the number of case alternatives.
             */
            public int getNCaseAlts() {
                return caseAlts.length;
            }
            
            /**
             * Get the nth case alternative.
             * @param n the index of the case alternative to return.
             * @return the nth case alternative.
             */
            public Case.Alt getNthCaseAlt(int n) {
                return caseAlts[n];
            }

            @Override
            boolean neverNeedsParentheses() {
                return false;
            }             
            
            @Override
            ParseTreeNode toParseTreeNode() {
                ParseTreeNode caseExprNode = new ParseTreeNode(CALTreeParserTokenTypes.LITERAL_case, "case");
                ParseTreeNode conditionExprNode = conditionExpr.toParseTreeNode();
                ParseTreeNode altListNode = new ParseTreeNode(CALTreeParserTokenTypes.ALT_LIST, "ALT_LIST");
                
                ParseTreeNode[] altNodes = new ParseTreeNode[caseAlts.length];
                for(int i = 0; i < caseAlts.length; i++) {
                    altNodes[i] = caseAlts[i].toParseTreeNode();
                }
                altListNode.addChildren(altNodes);
                
                caseExprNode.setFirstChild(conditionExprNode);
                conditionExprNode.setNextSibling(altListNode);
                return caseExprNode;
            }
            
            /**
             * {@inheritDoc}
             */
            @Override
            public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                return visitor.visit_Expr_Case(this, arg);
            }
        }
        
        /**
         * Models a lambda expression such as:
         * \x y z  -> x + y + z
         * or
         * \!x x1 !x2 x3 -> map x [x1, x2, x3]
         * @author Bo Ilic
         */
        public static final class Lambda extends Expr {
            private final Parameter[] parameters;
            private final Expr definingExpr;
            
            private Lambda (Parameter[] parameters, Expr definingExpr, SourceRange sourceRange) {                
                super(sourceRange);
                
                if (parameters == null || parameters.length < 1) {
                    throw new IllegalArgumentException("lambda expressions cannot have 0 arguments.");
                } 
                
                this.parameters = parameters.clone();
                verifyArrayArg(this.parameters, "parameters");                 
                
                verifyArg(definingExpr, "definingExpr");
                this.definingExpr = definingExpr; 
            }
            
            public static Lambda make(Parameter[] parameters, Expr definingExpr) {
                return new Lambda(parameters, definingExpr, null);
            }
            
            static Lambda makeAnnotated(Parameter[] parameters, Expr definingExpr, SourceRange sourceRange) {
                return new Lambda(parameters, definingExpr, sourceRange);
            }

            @Override
            int precedenceLevel() {
                return 0;
            }
            
            @Override
            Associativity associativity() {
                return Associativity.NON;
            }             
            
            
            @Override
            boolean neverNeedsParentheses() {
                return false;
            }
            
            public Parameter[] getParameters() {
                return parameters.clone();
            }
            
            /**
             * Get the number of parameters.
             * @return the number of parameters.
             */
            public int getNParameters() {
                return parameters.length;
            }
            
            /**
             * Get the nth parameter.
             * @param n the index of the parameter to return.
             * @return the nth parameter.
             */
            public Parameter getNthParameter(int n) {
                return parameters[n];
            }
            
            public Expr getDefiningExpr() {
                return definingExpr;
            }
            
            @Override
            ParseTreeNode toParseTreeNode() {
                ParseTreeNode lambdaExprNode = new ParseTreeNode(CALTreeParserTokenTypes.LAMBDA_DEFN, "\\");
                ParseTreeNode paramListNode = new ParseTreeNode(CALTreeParserTokenTypes.FUNCTION_PARAM_LIST, "FUNCTION_PARAM_LIST");
                ParseTreeNode exprNode = definingExpr.toParseTreeNode();
                
                ParseTreeNode[] paramNodes = new ParseTreeNode[parameters.length];
                for (int i = 0; i < parameters.length; i++) {
                    paramNodes[i] = parameters[i].toParseTreeNode();
                }
                paramListNode.addChildren(paramNodes);
                
                lambdaExprNode.setFirstChild(paramListNode);
                paramListNode.setNextSibling(exprNode);
                return lambdaExprNode;
            }
            
            /**
             * {@inheritDoc}
             */
            @Override
            public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                return visitor.visit_Expr_Lambda(this, arg);
            }
        }
        
        /**
         * Models an if-then-else expression.
         * @author Bo Ilic
         */
        public static final class If extends Expr {
            
            private final Expr conditionExpr;
            private final Expr thenExpr;
            private final Expr elseExpr;
            
            private If(Expr conditionExpr, Expr thenExpr, Expr elseExpr, SourceRange sourceRange) {                
                super(sourceRange);
                
                verifyArg(conditionExpr, "conditionExpr");
                verifyArg(thenExpr, "thenExpr");
                verifyArg(elseExpr, "elseExpr");
                
                this.conditionExpr = conditionExpr;
                this.thenExpr = thenExpr;
                this.elseExpr = elseExpr;
            }
                        
            public static If make(Expr conditionExpr, Expr thenExpr, Expr elseExpr) {
                return new If(conditionExpr, thenExpr, elseExpr, null);
            }
            
            static If makeAnnotated(Expr conditionExpr, Expr thenExpr, Expr elseExpr, SourceRange sourceRange) {
                return new If(conditionExpr, thenExpr, elseExpr, sourceRange);
            }
            
            int precedenceLevel() {
                return 0;
            }
            
            Associativity associativity() {
                return Associativity.NON;
            }             
                      
            boolean neverNeedsParentheses() {                
                return false;
            }  
            
            public Expr getConditionExpr() {
                return conditionExpr;
            }
            
            public Expr getThenExpr() {
                return thenExpr;
            }
            
            public Expr getElseExpr() {
                return elseExpr;
            }
            
            ParseTreeNode toParseTreeNode() {                
                ParseTreeNode conditionExprNode = conditionExpr.toParseTreeNode();
                ParseTreeNode thenExprNode = thenExpr.toParseTreeNode();
                ParseTreeNode elseExprNode = elseExpr.toParseTreeNode();
                
                ParseTreeNode ifExprNode = new ParseTreeNode(CALTreeParserTokenTypes.LITERAL_if, "if");
                ifExprNode.setFirstChild(conditionExprNode);
                conditionExprNode.setNextSibling(thenExprNode);
                thenExprNode.setNextSibling(elseExprNode);
                
                return ifExprNode;                
            }
            
            /**
             * {@inheritDoc}
             */
            public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                return visitor.visit_Expr_If(this, arg);
            }
        }
        
        /**
         * Base class for representing literals in CAL source.
         * @author Bo Ilic
         */
        public abstract static class Literal extends Expr {
            
            private Literal(SourceRange sourceRange) {
                super(sourceRange);
            }

            /**
             * Models overloaded values of type "Num a => a" in CAL source. For example, 123. These look like integers, but
             * in CAL, constants such as these really are shorthand for "Prelude.fromInteger 123" and so are overloaded values
             * that can be specialized according to context as a Prelude.Int, Prelude.Integer, Prelude.Double etc.
             * @author Bo Ilic
             */
            public static final class Num extends Literal {
                private final BigInteger value;
                
                private Num(BigInteger value, SourceRange range) {
                    super(range);
                    verifyArg(value, "value");                     
                    this.value = value;

                }
                
                private Num(long value) {
                    this (BigInteger.valueOf(value), null);
                }
                
                public static Num make(BigInteger value) {
                    return new Num(value, null);
                }
                
                static Num makeAnnotated(BigInteger value, SourceRange range) {
                    return new Num(value, range);
                }

                
                public static Num make(long value) {
                    return new Num(value);
                }
                
                int precedenceLevel() {
                    if (value.compareTo(BigInteger.ZERO) < 0) {
                        //negative values have the precedence of unary negation.
                        return 70;
                    }
                    
                    return 100;
                }                   
                
                
                public BigInteger getNumValue() {
                    return value;
                }
                
                boolean neverNeedsParentheses() {      
                    //negative values may need to be parenthesized
                    return value.compareTo(BigInteger.ZERO) >= 0;
                }       
                
                ParseTreeNode toParseTreeNode() {
                    if (value.compareTo(BigInteger.ZERO) < 0) {
                        Expr expr = Expr.UnaryOp.Negate.make(new Num(value.negate(), null));
                        ParseTreeNode exprNode = expr.toParseTreeNode();
                        return exprNode;
                    }
                    
                    ParseTreeNode numLiteralNode = new ParseTreeNode(CALTreeParserTokenTypes.INTEGER_LITERAL, value.toString());
                    return numLiteralNode;                     
                }
                
                /**
                 * {@inheritDoc}
                 */
                public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                    return visitor.visit_Expr_Literal_Num(this, arg);
                }
            }
            
            /**
             * Models literal values of type Prelude.Float in CAL source.
             * @author Ray Cypher
             */
            public static final class Float extends Literal {
                private final float value;
                
                private Float (float value, SourceRange range) {
                    super(range);
                    this.value = value;
                }
                
                public static Float make (float value) {
                    return new Float (value, null);
                }
                
                static Float makeAnnotated (float value, SourceRange range) {
                    return new Float (value, range);
                }
                
                int precedenceLevel() {
                    if (value < 0) {
                        //negative values have the precedence of unary negation.
                        return 70;
                    }
                    
                    return 100;
                }                  
             
                
                public float getFloatValue() {
                    return value;
                }
                
                boolean neverNeedsParentheses() {      
                    // because we are generating an application of Prelude.doubleToFloat
                    // it may need parentheses
                    return false;
                }         
                
                ParseTreeNode toParseTreeNode() {      
                    ParseTreeNode valNode;
                    
                    if (java.lang.Double.isNaN(value)) {
                        Expr expr = Expr.Var.make(CAL_Prelude.MODULE_NAME, CAL_Prelude.Functions.isNotANumber.getUnqualifiedName());
                        valNode = expr.toParseTreeNode();
                    } else
                    if (value == java.lang.Double.POSITIVE_INFINITY) {
                        Expr expr = Expr.Var.make(CAL_Prelude.MODULE_NAME, CAL_Prelude.Functions.positiveInfinity.getUnqualifiedName());
                        valNode = expr.toParseTreeNode();
                    } else
                    if (value == java.lang.Double.NEGATIVE_INFINITY) {
                        Expr expr = Expr.Var.make(CAL_Prelude.MODULE_NAME, CAL_Prelude.Functions.negativeInfinity.getUnqualifiedName());
                        valNode = expr.toParseTreeNode();
                    } else
                    if (value < 0) {
                        Expr expr = Expr.UnaryOp.Negate.make(makeDoubleValue(-value));
                        valNode = expr.toParseTreeNode();
                    } else {
                        valNode = new ParseTreeNode(CALTreeParserTokenTypes.FLOAT_LITERAL, java.lang.Double.toString(value));
                    }
                    
                    ParseTreeNode appNode = new ParseTreeNode(CALTreeParserTokenTypes.APPLICATION, "@");
                    ParseTreeNode children[] = 
                        new ParseTreeNode[]{
                            (Expr.Var.make(CAL_Prelude.MODULE_NAME, CAL_Prelude.Functions.toFloat.getUnqualifiedName())).toParseTreeNode(),
                            valNode};
                    
                    appNode.addChildren(children);
                    
                    return appNode;                     
                }
                
                /**
                 * {@inheritDoc}
                 */
                public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                    return visitor.visit_Expr_Literal_Float(this, arg);
                }

            }
            
            /**
             * Models literal values of type Prelude.Double in CAL source e.g. 1.23
             * @author Bo Ilic
             */
            public static final class Double extends Literal {
                private final double value;
                
                private Double(double value, SourceRange range) {
                    super(range);
                    this.value = value;
                }
                
                public static Double make(double value) {
                    return new Double(value, null);
                }

                static Double makeAnnotated(double value, SourceRange range) {
                    return new Double(value, range);
                }

                int precedenceLevel() {
                    if (value < 0) {
                        //negative values have the precedence of unary negation.
                        return 70;
                    }
                    
                    return 100;
                }                  
                
               
                public double getDoubleValue() {
                    return value;
                }
                
                boolean neverNeedsParentheses() {      
                    //negative values may need to be parenthesized
                    return value >= 0;
                }         
                
                ParseTreeNode toParseTreeNode() {                     
                    if (java.lang.Double.isNaN(value)) {
                        Expr expr = Expr.Var.make(CAL_Prelude.MODULE_NAME, CAL_Prelude.Functions.isNotANumber.getUnqualifiedName());
                        ParseTreeNode exprNode = expr.toParseTreeNode();
                        return exprNode;
                    }
                    
                    if (value == java.lang.Double.POSITIVE_INFINITY) {
                        Expr expr = Expr.Var.make(CAL_Prelude.MODULE_NAME, CAL_Prelude.Functions.positiveInfinity.getUnqualifiedName());
                        ParseTreeNode exprNode = expr.toParseTreeNode();
                        return exprNode;
                    }
                    
                    if (value == java.lang.Double.NEGATIVE_INFINITY) {
                        Expr expr = Expr.Var.make(CAL_Prelude.MODULE_NAME, CAL_Prelude.Functions.negativeInfinity.getUnqualifiedName());
                        ParseTreeNode exprNode = expr.toParseTreeNode();
                        return exprNode;
                    }
                    
                    if (value < 0) {
                        Expr expr = Expr.UnaryOp.Negate.make(makeDoubleValue(-value));
                        ParseTreeNode exprNode = expr.toParseTreeNode();
                        return exprNode;
                    }
                    
                    ParseTreeNode doubleLiteralNode = new ParseTreeNode(CALTreeParserTokenTypes.FLOAT_LITERAL, java.lang.Double.toString(value));
                    return doubleLiteralNode;                     
                }
                
                /**
                 * {@inheritDoc}
                 */
                public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                    return visitor.visit_Expr_Literal_Double(this, arg);
                }
            }
            
            /**
             * Models literal values of type Prelude.Char in CAL source e.g. 'a', '\n'
             * @author Bo Ilic
             */
            public static final class Char extends Literal {
                private final char value;
                
                private Char(char value, SourceRange range) {
                    super(range);
                    this.value = value;
                }
                
                public static Char make(char value) {
                    return new Char(value, null);
                }
                
                static Char makeAnnotated(char value, SourceRange range) {
                    return new Char(value, range);
                }
                
                int precedenceLevel() {
                    return 100;
                }                 
                
                
                public char getCharValue() {
                    return value;
                }
                
                boolean neverNeedsParentheses() {                
                    return true;
                }         
                
                ParseTreeNode toParseTreeNode() {
                    String c = StringEncoder.encodeChar(value);
                    ParseTreeNode charLiteralNode = new ParseTreeNode(CALTreeParserTokenTypes.CHAR_LITERAL, c);
                    return charLiteralNode;                     
                }
                
                /**
                 * {@inheritDoc}
                 */
                public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                    return visitor.visit_Expr_Literal_Char(this, arg);
                }
            }
            
            /**
             * Models literal values of type Prelude.String in CAL source e.g. "abc", "abc\ndef"             
             * @author Bo Ilic
             */
            public static final class StringLit extends Literal {
                private final String value;
                
                private StringLit(String value, SourceRange range) {
                    super(range);
                    verifyArg(value, "value");                    
                    this.value = value;
                }

                public static StringLit make(String value) {
                    return new StringLit(value, null);
                }
                
                static StringLit makeAnnotated(String value, SourceRange range) {
                    return new StringLit(value, range);
                }

                
                int precedenceLevel() {
                    return 100;
                }                  
                
                
                public String getStringValue() {
                    return value;
                }
                
                boolean neverNeedsParentheses() {                
                    return true;
                }    
                
                ParseTreeNode toParseTreeNode() {
                    String encodedString = StringEncoder.encodeString(value);
                    ParseTreeNode stringLiteralNode = new ParseTreeNode(CALTreeParserTokenTypes.STRING_LITERAL, encodedString);
                    return stringLiteralNode;                     
                }
                
                /**
                 * {@inheritDoc}
                 */
                public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                    return visitor.visit_Expr_Literal_StringLit(this, arg);
                }
            }
            
            private Literal(){
            }             
            
            Associativity associativity() {
                return Associativity.NON;
            }             
        }             
        
        /**
         * Models a unary operator in operator form e.g. -2.0. 
         * Currently the only one is unary negate.         
         * @author Bo Ilic
         */
        public abstract static class UnaryOp extends Expr {
            
            /** the single argument expression of the unary operator */
            private final Expr expr;
            
            private final SourceRange operatorSourceRange;
            
            /**
             * Models the - unary operator (which is Prelude.negate in its textual form).
             * @author Bo Ilic
             */
            public static final class Negate extends UnaryOp { 
                
                private Negate(Expr expr, SourceRange sourceRange, SourceRange operatorSourceRange) {
                    super(expr, sourceRange, operatorSourceRange);
                }
                
                public static Negate make(Expr expr) {
                    return new Negate(expr, null, null);
                }
                
                static Negate makeAnnotated(Expr expr, SourceRange sourceRange, SourceRange operatorSourceRange) {
                    return new Negate(expr, sourceRange, operatorSourceRange);
                }
                
                int precedenceLevel() {
                    return 70;
                }
                
                Associativity associativity() {
                    return Associativity.NON;
                }                 
                
                public String getOpText() {
                    return "-";
                }
                
                ParseTreeNode toParseTreeNode() {
                    
                    ParseTreeNode exprNode = new ParseTreeNode(CALTreeParserTokenTypes.UNARY_MINUS, getOpText());                  
                    exprNode.setFirstChild(getExpr().toParseTreeNode());                 
                    return exprNode;                                       
                }
                
                /**
                 * {@inheritDoc}
                 */
                public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                    return visitor.visit_Expr_UnaryOp_Negate(this, arg);
                }
            }
            
            private UnaryOp(Expr expr, SourceRange sourceRange, SourceRange operatorSourceRange) {
                super(sourceRange);
                verifyArg(expr, "expr");                               
                this.expr = expr;
                this.operatorSourceRange = operatorSourceRange;
            }
            
            /** @return The source range of the operator */
            // todo-jowong maybe the operator can be encapsulated by its own source element
            SourceRange getOperatorSourceRange() {
                return operatorSourceRange;
            }
            
            /**              
             * @return representing the operator form in CAL source without its arguments e.g. "-".
             */
            public abstract String getOpText();
            
          
            boolean neverNeedsParentheses() { 
                //-3 + 7 is not the same as - (3 + 7)
                return false;
            }
            
            /** @return the single argument expression of the unary operator */
            public Expr getExpr() {
                return expr;
            }             
        }
        
        /**
         * Models binary operator expressions in operator form e.g. 2.0 + 3.0 (and not something like Prelude.add 2.0 3.0).
         * @author Bo Ilic
         */
        public abstract static class BinaryOp extends Expr {
            
            /** the argument on the left hand side of the operator. */
            private final Expr leftExpr;
            
            /** the argument on the right hand side of the operator. */
            private final Expr rightExpr;
            
            /** The SourceRange of the operator */
            private final SourceRange operatorSourceRange;
            
            /**
             * Models the && operator (which is Prelude.and in its textual form).
             * @author Bo Ilic
             */
            public static final class And extends BinaryOp { 
                
                private And(Expr leftExpr, Expr rightExpr, SourceRange sourceRange, SourceRange operatorSourceRange) {
                    super(leftExpr, rightExpr, sourceRange, operatorSourceRange);
                }

                public static And make(Expr leftExpr, Expr rightExpr) {
                    return new And(leftExpr, rightExpr, null, null);
                }
                
                static And makeAnnotated(Expr leftExpr, Expr rightExpr, SourceRange sourceRange, SourceRange operatorSourceRange) {
                    return new And(leftExpr, rightExpr, sourceRange, operatorSourceRange);
                }
                
                public String getOpText() {
                    return "&&";
                }
                
                int precedenceLevel() {
                    return 20;
                }
                
                Associativity associativity() {
                    return Associativity.RIGHT;
                }
                
                int getTokenType() {
                    return CALTreeParserTokenTypes.AMPERSANDAMPERSAND;                     
                }
                
                /**
                 * {@inheritDoc}
                 */
                public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                    return visitor.visit_Expr_BinaryOp_And(this, arg);
                }
            }
            
            /**
             * Models the || operator (which is Prelude.or in its textual form).
             * @author Bo Ilic
             */
            public static final class Or extends BinaryOp { 
                
                private Or(Expr leftExpr, Expr rightExpr, SourceRange sourceRange, SourceRange operatorSourceRange) {
                    super(leftExpr, rightExpr, sourceRange, operatorSourceRange);
                }
                
                public static Or make(Expr leftExpr, Expr rightExpr) {
                    return new Or(leftExpr, rightExpr, null, null);
                }
                
                static Or makeAnnotated(Expr leftExpr, Expr rightExpr, SourceRange sourceRange, SourceRange operatorSourceRange) {
                    return new Or(leftExpr, rightExpr, sourceRange, operatorSourceRange);
                }
                
                public String getOpText() {
                    return "||";
                }
                
                int precedenceLevel() {
                    return 10;
                } 
                
                Associativity associativity() {
                    return Associativity.RIGHT;
                }
                
                int getTokenType() {
                    return CALTreeParserTokenTypes.BARBAR;                     
                }
                
                /**
                 * {@inheritDoc}
                 */
                public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                    return visitor.visit_Expr_BinaryOp_Or(this, arg);
                }
            }
            
            /**
             * Models the == operator (which is Prelude.equals in its textual form).
             * @author Bo Ilic
             */
            public static final class Equals extends BinaryOp { 
                
                private Equals(Expr leftExpr, Expr rightExpr, SourceRange sourceRange, SourceRange operatorSourceRange) {
                    super(leftExpr, rightExpr, sourceRange, operatorSourceRange);
                }
                
                public static Equals make(Expr leftExpr, Expr rightExpr) {
                    return new Equals(leftExpr, rightExpr, null, null);
                }
                
                static Equals makeAnnotated(Expr leftExpr, Expr rightExpr, SourceRange sourceRange, SourceRange operatorSourceRange) {
                    return new Equals(leftExpr, rightExpr, sourceRange, operatorSourceRange);
                }
                
                int precedenceLevel() {
                    return 30;
                }
                
                Associativity associativity() {
                    return Associativity.NON;
                }                 
                
                public String getOpText() {
                    return "==";
                }
                
                int getTokenType() {
                    return CALTreeParserTokenTypes.EQUALSEQUALS;                     
                }
                
                /**
                 * {@inheritDoc}
                 */
                public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                    return visitor.visit_Expr_BinaryOp_Equals(this, arg);
                }
            }
            
            /**
             * Models the != operator (which is Prelude.notEquals in its textual form).
             * @author Bo Ilic
             */
            public static final class NotEquals extends BinaryOp { 
                
                private NotEquals(Expr leftExpr, Expr rightExpr, SourceRange sourceRange, SourceRange operatorSourceRange) {
                    super(leftExpr, rightExpr, sourceRange, operatorSourceRange);
                }
                
                public static NotEquals make(Expr leftExpr, Expr rightExpr) {
                    return new NotEquals(leftExpr, rightExpr, null, null);
                }
                
                static NotEquals makeAnnotated(Expr leftExpr, Expr rightExpr, SourceRange sourceRange, SourceRange operatorSourceRange) {
                    return new NotEquals(leftExpr, rightExpr, sourceRange, operatorSourceRange);
                }
                
                int precedenceLevel() {
                    return 30;
                }
                
                Associativity associativity() {
                    return Associativity.NON;
                }                 
                
                public String getOpText() {
                    return "!=";
                }
                
                int getTokenType() {
                    return CALTreeParserTokenTypes.NOT_EQUALS;                     
                }
                
                /**
                 * {@inheritDoc}
                 */
                public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                    return visitor.visit_Expr_BinaryOp_NotEquals(this, arg);
                }
            }
            
            /**
             * Models the < operator (which is Prelude.lessThan in its textual form).
             * @author Bo Ilic
             */
            public static final class LessThan extends BinaryOp { 
                
                private LessThan(Expr leftExpr, Expr rightExpr, SourceRange sourceRange, SourceRange operatorSourceRange) {
                    super(leftExpr, rightExpr, sourceRange, operatorSourceRange);
                }
                
                public static LessThan make(Expr leftExpr, Expr rightExpr) {
                    return new LessThan(leftExpr, rightExpr, null, null);
                }
                
                static LessThan makeAnnotated(Expr leftExpr, Expr rightExpr, SourceRange sourceRange, SourceRange operatorSourceRange) {
                    return new LessThan(leftExpr, rightExpr, sourceRange, operatorSourceRange);
                }
                
                int precedenceLevel() {
                    return 30;
                }
                
                Associativity associativity() {
                    return Associativity.NON;
                }                 
                
                public String getOpText() {
                    return "<";
                }
                
                int getTokenType() {
                    return CALTreeParserTokenTypes.LESS_THAN;                     
                }
                
                /**
                 * {@inheritDoc}
                 */
                public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                    return visitor.visit_Expr_BinaryOp_LessThan(this, arg);
                }
            } 
            
            /**
             * Models the <= operator (which is Prelude.lessThanEquals in its textual form).
             * @author Bo Ilic
             */
            public static final class LessThanEquals extends BinaryOp { 
                
                private LessThanEquals(Expr leftExpr, Expr rightExpr, SourceRange sourceRange, SourceRange operatorSourceRange) {
                    super(leftExpr, rightExpr, sourceRange, operatorSourceRange);
                }
                
                public static LessThanEquals make(Expr leftExpr, Expr rightExpr) {
                    return new LessThanEquals(leftExpr, rightExpr, null, null);
                }
                
                static LessThanEquals makeAnnotated(Expr leftExpr, Expr rightExpr, SourceRange sourceRange, SourceRange operatorSourceRange) {
                    return new LessThanEquals(leftExpr, rightExpr, sourceRange, operatorSourceRange);
                }
                
                int precedenceLevel() {
                    return 30;
                }
                
                Associativity associativity() {
                    return Associativity.NON;
                }                 
                
                public String getOpText() {
                    return "<=";
                }                 
                
                int getTokenType() {
                    return CALTreeParserTokenTypes.LESS_THAN_OR_EQUALS;                     
                }
                
                /**
                 * {@inheritDoc}
                 */
                public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                    return visitor.visit_Expr_BinaryOp_LessThanEquals(this, arg);
                }
            }
            
            /**
             * Models the >= operator (which is Prelude.greaterThanEquals in its textual form).
             * @author Bo Ilic
             */
            public static final class GreaterThanEquals extends BinaryOp { 
                
                private GreaterThanEquals(Expr leftExpr, Expr rightExpr, SourceRange sourceRange, SourceRange operatorSourceRange) {
                    super(leftExpr, rightExpr, sourceRange, operatorSourceRange);
                }
                
                public static GreaterThanEquals make(Expr leftExpr, Expr rightExpr) {
                    return new GreaterThanEquals(leftExpr, rightExpr, null, null);
                }
                
                static GreaterThanEquals makeAnnotated(Expr leftExpr, Expr rightExpr, SourceRange sourceRange, SourceRange operatorSourceRange) {
                    return new GreaterThanEquals(leftExpr, rightExpr, sourceRange, operatorSourceRange);
                }
                
                int precedenceLevel() {
                    return 30;
                }
                
                Associativity associativity() {
                    return Associativity.NON;
                }                 
                
                public String getOpText() {
                    return ">=";
                }
                
                int getTokenType() {
                    return CALTreeParserTokenTypes.GREATER_THAN_OR_EQUALS;                     
                }
                
                /**
                 * {@inheritDoc}
                 */
                public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                    return visitor.visit_Expr_BinaryOp_GreaterThanEquals(this, arg);
                }
            }
            
            /**
             * Models the < operator (which is Prelude.lessThan in its textual form).
             * @author Bo Ilic
             */
            public static final class GreaterThan extends BinaryOp { 
                
                private GreaterThan(Expr leftExpr, Expr rightExpr, SourceRange sourceRange, SourceRange operatorSourceRange) {
                    super(leftExpr, rightExpr, sourceRange, operatorSourceRange);
                }
                
                public static GreaterThan make(Expr leftExpr, Expr rightExpr) {
                    return new GreaterThan(leftExpr, rightExpr, null, null);
                }
                
                static GreaterThan makeAnnotated(Expr leftExpr, Expr rightExpr, SourceRange sourceRange, SourceRange operatorSourceRange) {
                    return new GreaterThan(leftExpr, rightExpr, sourceRange, operatorSourceRange);
                }
                
                public String getOpText() {
                    return ">";
                }
                
                int precedenceLevel() {
                    return 30;
                }
                
                Associativity associativity() {
                    return Associativity.NON;
                }
                
                int getTokenType() {
                    return CALTreeParserTokenTypes.GREATER_THAN;                     
                }
                
                /**
                 * {@inheritDoc}
                 */
                public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                    return visitor.visit_Expr_BinaryOp_GreaterThan(this, arg);
                }
            }
            
            /**
             * Models the + operator (which is Prelude.add in its textual form).
             * @author Bo Ilic
             */
            public static final class Add extends BinaryOp { 
                
                private Add(Expr leftExpr, Expr rightExpr, SourceRange sourceRange, SourceRange operatorSourceRange) {
                    super(leftExpr, rightExpr, sourceRange, operatorSourceRange);
                }
                
                public static Add make(Expr leftExpr, Expr rightExpr) {
                    return new Add(leftExpr, rightExpr, null, null);
                }
                
                static Add makeAnnotated(Expr leftExpr, Expr rightExpr, SourceRange sourceRange, SourceRange operatorSourceRange) {
                    return new Add(leftExpr, rightExpr, sourceRange, operatorSourceRange);
                }
                
                int precedenceLevel() {
                    return 50;
                }
                
                Associativity associativity() {
                    return Associativity.LEFT;
                }                  
                
                public String getOpText() {
                    return "+";
                }
                
                int getTokenType() {
                    return CALTreeParserTokenTypes.PLUS;                     
                }
                
                /**
                 * {@inheritDoc}
                 */
                public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                    return visitor.visit_Expr_BinaryOp_Add(this, arg);
                }
            }
            
            /**
             * Models the - binary operator (which is Prelude.subtract in its textual form).
             * @author Bo Ilic
             */
            public static final class Subtract extends BinaryOp { 
                
                private Subtract(Expr leftExpr, Expr rightExpr, SourceRange sourceRange, SourceRange operatorSourceRange) {
                    super(leftExpr, rightExpr, sourceRange, operatorSourceRange);
                }
                
                public static Subtract make(Expr leftExpr, Expr rightExpr) {
                    return new Subtract(leftExpr, rightExpr, null, null);
                }
                
                static Subtract makeAnnotated(Expr leftExpr, Expr rightExpr, SourceRange sourceRange, SourceRange operatorSourceRange) {
                    return new Subtract(leftExpr, rightExpr, sourceRange, operatorSourceRange);
                }

                int precedenceLevel() {
                    return 50;
                }
                
                Associativity associativity() {
                    return Associativity.LEFT;
                }                 
                
                public String getOpText() {
                    return "-";
                }
                
                int getTokenType() {
                    return CALTreeParserTokenTypes.MINUS;                     
                }
                
                /**
                 * {@inheritDoc}
                 */
                public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                    return visitor.visit_Expr_BinaryOp_Subtract(this, arg);
                }
            }
            
            /**
             * Models the * operator (which is Prelude.multiply in its textual form).
             * @author Bo Ilic
             */
            public static final class Multiply extends BinaryOp { 
                
                private Multiply(Expr leftExpr, Expr rightExpr, SourceRange sourceRange, SourceRange operatorSourceRange) {
                    super(leftExpr, rightExpr, sourceRange, operatorSourceRange);
                }
                
                public static Multiply make(Expr leftExpr, Expr rightExpr) {
                    return new Multiply(leftExpr, rightExpr, null, null);
                }
                
                static Multiply makeAnnotated(Expr leftExpr, Expr rightExpr, SourceRange sourceRange, SourceRange operatorSourceRange) {
                    return new Multiply(leftExpr, rightExpr, sourceRange, operatorSourceRange);
                }
                
                int precedenceLevel() {
                    return 60;
                }
                
                Associativity associativity() {
                    return Associativity.LEFT;
                }                 
                
                public String getOpText() {
                    return "*";
                }                 
                
                int getTokenType() {
                    return CALTreeParserTokenTypes.ASTERISK;                     
                }
                
                /**
                 * {@inheritDoc}
                 */
                public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                    return visitor.visit_Expr_BinaryOp_Multiply(this, arg);
                }
            }
            
            /**
             * Models the / operator (which is Prelude.divide in its textual form).
             * @author Bo Ilic
             */
            public static final class Divide extends BinaryOp { 
                
                private Divide(Expr leftExpr, Expr rightExpr, SourceRange sourceRange, SourceRange operatorSourceRange) {
                    super(leftExpr, rightExpr, sourceRange, operatorSourceRange);
                }
                
                public static Divide make(Expr leftExpr, Expr rightExpr) {
                    return new Divide(leftExpr, rightExpr, null, null);
                }
                
                static Divide makeAnnotated(Expr leftExpr, Expr rightExpr, SourceRange sourceRange, SourceRange operatorSourceRange) {
                    return new Divide(leftExpr, rightExpr, sourceRange, operatorSourceRange);
                }

                int precedenceLevel() {
                    return 60;
                }
                
                Associativity associativity() {
                    return Associativity.LEFT;
                }                 
                
                public String getOpText() {
                    return "/";
                }
                
                int getTokenType() {
                    return CALTreeParserTokenTypes.SOLIDUS;                     
                }
                
                /**
                 * {@inheritDoc}
                 */
                public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                    return visitor.visit_Expr_BinaryOp_Divide(this, arg);
                }
                
            }
            
            /**
             * Models the % operator (which is Prelude.remainder in its textual form).
             * @author Bo Ilic
             */
            public static final class Remainder extends BinaryOp { 
                
                private Remainder(Expr leftExpr, Expr rightExpr, SourceRange sourceRange, SourceRange operatorSourceRange) {
                    super(leftExpr, rightExpr, sourceRange, operatorSourceRange);
                }
                
                public static Remainder make(Expr leftExpr, Expr rightExpr) {
                    return new Remainder(leftExpr, rightExpr, null, null);
                }
                
                static Remainder makeAnnotated(Expr leftExpr, Expr rightExpr, SourceRange sourceRange, SourceRange operatorSourceRange) {
                    return new Remainder(leftExpr, rightExpr, sourceRange, operatorSourceRange);
                }

                int precedenceLevel() {
                    return 60;
                }
                
                Associativity associativity() {
                    return Associativity.LEFT;
                }                 
                
                public String getOpText() {
                    return "%";
                }
                
                int getTokenType() {
                    return CALTreeParserTokenTypes.PERCENT;                     
                }
                
                /**
                 * {@inheritDoc}
                 */
                public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                    return visitor.visit_Expr_BinaryOp_Remainder(this, arg);
                }
                
            }            
            
            /**
             * Models the `{Operator}` operator the back quoted operator application
             * @author Greg McClement
             * @author Joseph Wong
             */
            public static abstract class BackquotedOperator extends BinaryOp 
            {
                /**
                 * Models a backquoted operator containing a function/class method
                 * name between the backquotes.
                 * @author Joseph Wong
                 */
                public static final class Var extends BackquotedOperator {
                    
                    /**
                     * The variable, function or class method reference appearing between the backquotes.
                     */
                    private final Expr.Var operatorVarExpr;
                    
                    /**
                     * Creates a source model element that represents a backquoted operator containing
                     * a function/class method name between the backquotes.
                     * 
                     * @param operatorVarExpr the variable, function or class method reference appearing between the backquotes.
                     * @param leftExpr the left-hand-side expression.
                     * @param rightExpr the right-hand-side expression.
                     * @param sourceRange the position that this SourceElement begins at in the source text
                     *                       for this module.
                     * @param operatorSourceRange
                     */
                    private Var(Expr.Var operatorVarExpr, Expr leftExpr, Expr rightExpr, SourceRange sourceRange, SourceRange operatorSourceRange) {
                        super(leftExpr, rightExpr, sourceRange, operatorSourceRange);
                        verifyArg(operatorVarExpr, "operatorVarExpr");
                        this.operatorVarExpr = operatorVarExpr;
                    }
                    
                    /**
                     * Factory method for constructing a source model element that represents a backquoted operator containing
                     * a function/class method name between the backquotes.
                     * 
                     * @param operatorVarExpr the variable, function or class method reference appearing between the backquotes.
                     * @param leftExpr the left-hand-side expression.
                     * @param rightExpr the right-hand-side expression.
                     * @return a new instance of this class.
                     */
                    public static Var make(Expr.Var operatorVarExpr, Expr leftExpr, Expr rightExpr) {
                        return new Var(operatorVarExpr, leftExpr, rightExpr, null, null);
                    }
                    
                    /**
                     * Factory method for constructing a source model element that represents a backquoted operator containing
                     * a function/class method name between the backquotes.
                     * 
                     * @param operatorVarExpr the variable, function or class method reference appearing between the backquotes.
                     * @param leftExpr the left-hand-side expression.
                     * @param rightExpr the right-hand-side expression.
                     * @param sourceRange the position that this SourceElement begins at in the source text
                     *                       for this module.
                     * @param operatorSourceRange                       
                     * @return a new instance of this class.
                     */
                    static Var makeAnnotated(Expr.Var operatorVarExpr, Expr leftExpr, Expr rightExpr, SourceRange sourceRange, SourceRange operatorSourceRange) {
                        return new Var(operatorVarExpr, leftExpr, rightExpr, sourceRange, operatorSourceRange);
                    }
                    
                    /**
                     * @return the variable, function or class method reference appearing between the backquotes.
                     */
                    public Expr.Var getOperatorVarExpr() {
                        return operatorVarExpr;
                    }
                    
                    /**
                     * @return the string representing the operator form in CAL source without its arguments e.g. "`seq`".
                     */
                    public String getOpText() {
                        return "`" + operatorVarExpr.toSourceText() + "`";
                    }
                    
                    /**
                     * {@inheritDoc}
                     */
                    ParseTreeNode makeOperatorNode() {
                        return operatorVarExpr.toParseTreeNode();
                    }

                    /**
                     * {@inheritDoc}
                     */
                    public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                        return visitor.visit_Expr_BinaryOp_BackquotedOperator_Var(this, arg);
                    }
                }
                
                /**
                 * Models a backquoted operator containing a data constructor
                 * name between the backquotes.
                 * @author Joseph Wong
                 */
                public static final class DataCons extends BackquotedOperator {
                    
                    /**
                     * The data constructor reference appearing between the backquotes.
                     */
                    private final Expr.DataCons operatorDataConsExpr;
                    
                    /**
                     * Creates a source model element that represents a backquoted operator containing
                     * a data constructor name between the backquotes.
                     * 
                     * @param operatorDataConsExpr the data constructor reference appearing between the backquotes.
                     * @param leftExpr the left-hand-side expression.
                     * @param rightExpr the right-hand-side expression.
                     * @param sourceRange the position that this SourceElement begins at in the source text
                     *                       for this module.
                     * @param operatorSourceRange                      
                     */
                    private DataCons(Expr.DataCons operatorDataConsExpr, Expr leftExpr, Expr rightExpr, SourceRange sourceRange, SourceRange operatorSourceRange) {
                        super(leftExpr, rightExpr, sourceRange, operatorSourceRange);
                        verifyArg(operatorDataConsExpr, "operatorDataConsExpr");
                        this.operatorDataConsExpr = operatorDataConsExpr;
                    }
                    
                    /**
                     * Factory method for constructing a source model element that represents a backquoted operator containing
                     * a data constructor name between the backquotes.
                     * 
                     * @param operatorDataConsExpr the data constructor reference appearing between the backquotes.
                     * @param leftExpr the left-hand-side expression.
                     * @param rightExpr the right-hand-side expression.
                     * @return a new instance of this class.
                     */
                    public static DataCons make(Expr.DataCons operatorDataConsExpr, Expr leftExpr, Expr rightExpr) {
                        return new DataCons(operatorDataConsExpr, leftExpr, rightExpr, null, null);
                    }
                    
                    /**
                     * Factory method for constructing a source model element that represents a backquoted operator containing
                     * a data constructor name between the backquotes.
                     * 
                     * @param operatorDataConsExpr the data constructor reference appearing between the backquotes.
                     * @param leftExpr the left-hand-side expression.
                     * @param rightExpr the right-hand-side expression.
                     * @param sourceRange the position that this SourceElement begins at in the source text
                     *                       for this module.
                     * @param operatorSourceRange                       
                     * @return a new instance of this class.
                     */
                    static DataCons makeAnnotated(Expr.DataCons operatorDataConsExpr, Expr leftExpr, Expr rightExpr, SourceRange sourceRange, SourceRange operatorSourceRange) {
                        return new DataCons(operatorDataConsExpr, leftExpr, rightExpr, sourceRange, operatorSourceRange);
                    }
                    
                    /**
                     * @return the data constructor reference appearing between the backquotes.
                     */
                    public Expr.DataCons getOperatorDataConsExpr() {
                        return operatorDataConsExpr;
                    }
                    
                    /**
                     * @return the string representing the operator form in CAL source without its arguments e.g. "`Cons`".
                     */
                    public String getOpText() {
                        return "`" + operatorDataConsExpr.toString() + "`";
                    }
                    
                    /**
                     * {@inheritDoc}
                     */
                    ParseTreeNode makeOperatorNode() {
                        return operatorDataConsExpr.toParseTreeNode();
                    }
                    
                    /**
                     * {@inheritDoc}
                     */
                    public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                        return visitor.visit_Expr_BinaryOp_BackquotedOperator_DataCons(this, arg);
                    }
                }
                
                /**
                 * Private constructor for this base class for backquoted operator. Intended
                 * only to be invoked by subclass constructors.
                 * 
                 * @param leftExpr the left-hand-side expression.
                 * @param rightExpr the right-hand-side expression.
                 * @param sourceRange the position that this SourceElement begins at in the source text
                 *                       for this module.
                 * @param operatorSourceRange                       
                 */
                private BackquotedOperator(Expr leftExpr, Expr rightExpr, SourceRange sourceRange, SourceRange operatorSourceRange) {
                    super(leftExpr, rightExpr, sourceRange, operatorSourceRange);
                }

                /**
                 * {@inheritDoc}
                 */
                int precedenceLevel()  {
                    return 75;   // higher than unary negation, same as compose, lower than application
                }
                
                /**
                 * {@inheritDoc}
                 */
                Associativity associativity() {
                    return Associativity.LEFT;
                }                 
                
                /**
                 * {@inheritDoc}
                 */
                int getTokenType() {
                    return CALTreeParserTokenTypes.BACKQUOTE;
                }
                
                /**
                 * Creates an application node wrapping an expression.
                 * @param exprNode the ParseTreeNode of the expression.
                 * @return an application node wrapping the expression.
                 */
                private ParseTreeNode makeApplicationNode(ParseTreeNode exprNode){
                    ParseTreeNode applicationExprNode = new ParseTreeNode (CALTreeParserTokenTypes.APPLICATION, "@");
                    applicationExprNode.addChild( exprNode );
                    return applicationExprNode;
                }
                
                /**
                 * @return the qualified cons/var node for the backquoted operator.
                 */
                abstract ParseTreeNode makeOperatorNode();
                
                /**
                 * @deprecated this is deprecated to signify that the returned source range is not the entire operator, but
                 * rather a portion thereof.
                 */
                // todo-jowong refactor this so that the operator source range of the source element is its entirety, including backticks
                // todo-jowong maybe the operator can be encapsulated by its own source element
                @Deprecated
                SourceRange getOperatorSourceRange() {
                    return getSourceRangeOfNameBetweenBackticks();
                }

                SourceRange getSourceRangeOfNameBetweenBackticks() {
                    return super.getOperatorSourceRange();
                }

                /**
                 * {@inheritDoc}
                 */
                ParseTreeNode toParseTreeNode() {
                    
                    ParseTreeNode operatorNode = makeOperatorNode();                                        
                    ParseTreeNode leftExprNode = makeApplicationNode(getLeftExpr().toParseTreeNode());
                    ParseTreeNode rightExprNode = makeApplicationNode(getRightExpr().toParseTreeNode());
                    
                    ParseTreeNode applicationExprNode = new ParseTreeNode (CALTreeParserTokenTypes.APPLICATION, "@");
                    applicationExprNode.addChild( operatorNode );
                    applicationExprNode.addChild( leftExprNode );
                    applicationExprNode.addChild( rightExprNode );

                    ParseTreeNode exprNode = new ParseTreeNode (CALTreeParserTokenTypes.BACKQUOTE, "`");
                    exprNode.addChild( applicationExprNode );

                    return exprNode; 
                }
                
            }              
            
            /**
             * Models the # operator (which is Prelude.compose in its textual form).
             * @author Joseph Wong
             */
            public static final class Compose extends BinaryOp { 
                
                /**
                 * Private constructor for this class.
                 * @param leftExpr the left-hand-sidce operand.
                 * @param rightExpr the right-hand-sidce operand.
                 * @param sourceRange
                 * @param operatorSourceRange
                 */
                private Compose(Expr leftExpr, Expr rightExpr, SourceRange sourceRange, SourceRange operatorSourceRange) {
                    super(leftExpr, rightExpr, sourceRange, operatorSourceRange);
                }
                
                /**
                 * Factory method for creating a new instance of this source model element.
                 * @param leftExpr the left-hand-sidce operand.
                 * @param rightExpr the right-hand-sidce operand.
                 * @return a new instance of SourceModel.Expr.BinaryOp.Compose.
                 */
                public static Compose make(Expr leftExpr, Expr rightExpr) {
                    return new Compose(leftExpr, rightExpr, null, null);
                }
                
                /**
                 * Internal factory method for creating a new instance of this source model element
                 * along with a SourceRange annotation.
                 * @param leftExpr the left-hand-sidce operand.
                 * @param rightExpr the right-hand-sidce operand.
                 * @param sourceRange The position in the source of the beginning of this element
                 * @param operatorSourceRange 
                 * @return a new instance of SourceModel.Expr.BinaryOp.Compose.
                 */
                static Compose makeAnnotated(Expr leftExpr, Expr rightExpr, SourceRange sourceRange, SourceRange operatorSourceRange) {
                    return new Compose(leftExpr, rightExpr, sourceRange, operatorSourceRange);
                }

                /**
                 * The higher the value, the greater the precedence level. For example, * would be higher than +.
                 * @return int precedence level. Has no absolute significance, only relative to the precedence
                 *     level for other expressions.
                 */
                int precedenceLevel() {
                    return 75;   // same as backquote operator, lower than application
                }
                
                /**
                 * @return the associativity of this operator.
                 */
                Associativity associativity() {
                    return Associativity.RIGHT;
                }                 
                
                /**              
                 * @return representing the operator form in CAL source without its arguments e.g. "+".
                 */
                public String getOpText() {
                    return "#";
                }
                
                /**
                 * @return Int representing the token type of the operator.
                 */
                int getTokenType() {
                    return CALTreeParserTokenTypes.POUND;                     
                }
                
                /**
                 * {@inheritDoc}
                 */
                public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                    return visitor.visit_Expr_BinaryOp_Compose(this, arg);
                }
                
            }
            
            /**
             * Models the $ operator (which is Prelude.apply in its textual form).
             * @author Joseph Wong
             */
            public static final class Apply extends BinaryOp { 
                
                /**
                 * Private constructor for this class.
                 * @param leftExpr the left-hand-sidce operand.
                 * @param rightExpr the right-hand-sidce operand.
                 * @param sourceRange
                 * @param operatorSourceRange
                 */
                private Apply(Expr leftExpr, Expr rightExpr, SourceRange sourceRange, SourceRange operatorSourceRange) {
                    super(leftExpr, rightExpr, sourceRange, operatorSourceRange);
                }
                
                /**
                 * Factory method for creating a new instance of this source model element.
                 * @param leftExpr the left-hand-sidce operand.
                 * @param rightExpr the right-hand-sidce operand.
                 * @return a new instance of SourceModel.Expr.BinaryOp.Apply.
                 */
                public static Apply make(Expr leftExpr, Expr rightExpr) {
                    return new Apply(leftExpr, rightExpr, null, null);
                }
                
                /**
                 * Internal factory method for creating a new instance of this source model element
                 * along with a SourceRange annotation.
                 * @param leftExpr the left-hand-sidce operand.
                 * @param rightExpr the right-hand-sidce operand.
                 * @param sourceRange The position in the source of the beginning of this element
                 * @param operatorSourceRange
                 * @return a new instance of SourceModel.Expr.BinaryOp.Apply.
                 */
                static Apply makeAnnotated(Expr leftExpr, Expr rightExpr, SourceRange sourceRange, SourceRange operatorSourceRange) {
                    return new Apply(leftExpr, rightExpr, sourceRange, operatorSourceRange);
                }

                /**
                 * The higher the value, the greater the precedence level. For example, * would be higher than +.
                 * @return int precedence level. Has no absolute significance, only relative to the precedence
                 *     level for other expressions.
                 */
                int precedenceLevel() {
                    return 5;   // higher than :: operator, lower than || operator
                }
                
                /**
                 * @return the associativity of this operator.
                 */
                Associativity associativity() {
                    return Associativity.RIGHT;
                }                 
                
                /**              
                 * @return representing the operator form in CAL source without its arguments e.g. "+".
                 */
                public String getOpText() {
                    return "$";
                }
                
                /**
                 * @return Int representing the token type of the operator.
                 */
                int getTokenType() {
                    return CALTreeParserTokenTypes.DOLLAR;                     
                }
                
                /**
                 * {@inheritDoc}
                 */
                public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                    return visitor.visit_Expr_BinaryOp_Apply(this, arg);
                }
                
            }
            
            /**
             * Models the : operator (which is Prelude.Cons in its textual form).
             * @author Bo Ilic
             */
            public static final class Cons extends BinaryOp { 
                
                private Cons(Expr leftExpr, Expr rightExpr, SourceRange sourceRange, SourceRange operatorSourceRange) {
                    super(leftExpr, rightExpr, sourceRange, operatorSourceRange);
                }
                
                public static Cons make(Expr leftExpr, Expr rightExpr) {
                    return new Cons(leftExpr, rightExpr, null, null);
                }
                
                static Cons makeAnnotated(Expr leftExpr, Expr rightExpr, SourceRange sourceRange, SourceRange operatorSourceRange) {
                    return new Cons(leftExpr, rightExpr, sourceRange, operatorSourceRange);
                }

                int precedenceLevel() {
                    return 40;
                }
                
                Associativity associativity() {
                    return Associativity.RIGHT;
                }                  
                
                public String getOpText() {
                    return ":";
                }
                
                int getTokenType() {
                    return CALTreeParserTokenTypes.COLON;
                }
                
                /**
                 * {@inheritDoc}
                 */
                public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                    return visitor.visit_Expr_BinaryOp_Cons(this, arg);
                }
            } 
            
            /**
             * Models the ++ operator (which is Prelude.append in its textual form).
             * @author Bo Ilic
             */
            public static final class Append extends BinaryOp { 
                
                private Append(Expr leftExpr, Expr rightExpr, SourceRange sourceRange, SourceRange operatorSourceRange) {
                    super(leftExpr, rightExpr, sourceRange, operatorSourceRange);
                }
                
                public static Append make(Expr leftExpr, Expr rightExpr) {
                    return new Append(leftExpr, rightExpr, null, null);
                }
                
                static Append makeAnnotated(Expr leftExpr, Expr rightExpr, SourceRange sourceRange, SourceRange operatorSourceRange) {
                    return new Append(leftExpr, rightExpr, sourceRange, operatorSourceRange);
                }

                int precedenceLevel() {
                    return 40;
                }
                
                Associativity associativity() {
                    return Associativity.RIGHT;
                }                 
                
                public String getOpText() {
                    return "++";
                }
                
                int getTokenType() {
                    return CALTreeParserTokenTypes.PLUSPLUS;
                }
                
                /**
                 * {@inheritDoc}
                 */
                public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                    return visitor.visit_Expr_BinaryOp_Append(this, arg);
                }
                
            }              
            
            private BinaryOp(Expr leftExpr, Expr rightExpr, SourceRange sourceRange, SourceRange operatorSourceRange) {
                super(sourceRange);
                verifyArg(leftExpr, "leftExpr");
                verifyArg(rightExpr, "rightExpr");
                
                this.leftExpr = leftExpr;
                this.rightExpr = rightExpr;
                this.operatorSourceRange = operatorSourceRange;
            }
            
            /** @return The SourceRange of the operator (may be null) */
            // todo-jowong maybe the operator can be encapsulated by its own source element
            SourceRange getOperatorSourceRange() {
                return operatorSourceRange;
            }
            
            
            ParseTreeNode toParseTreeNode() {
                ParseTreeNode exprNode = new ParseTreeNode(getTokenType(), getOpText());
                ParseTreeNode leftExprNode = getLeftExpr().toParseTreeNode();
                ParseTreeNode rightExprNode = getRightExpr().toParseTreeNode();
                
                exprNode.setFirstChild(leftExprNode);
                leftExprNode.setNextSibling(rightExprNode);
                return exprNode; 
            }
            
            boolean neverNeedsParentheses() {  
                //for example, 'x + y * z' is the same as x + (y * z)
                //so if we want (x + y) * z then parentheses are needed.
                return false;
            } 
            
            /**              
             * @return representing the operator form in CAL source without its arguments e.g. "+".
             */
            public abstract String getOpText(); 
            
            /**
             * @return Int representing the token type of the operator.
             */
            abstract int getTokenType();
            
            /**              
             * @return the argument on the left hand side of the operator.
             */
            public Expr getLeftExpr() {
                return leftExpr;
            }
            
            /**              
             * @return the argument on the right hand side of the operator.
             */             
            public Expr getRightExpr() {
                return rightExpr;
            }
        }
        
        /**
         * Models the notational form of the Prelude.Unit data constructor i.e. ().
         * @author Bo Ilic
         */
        public static final class Unit extends Expr {
            
            private static final Unit UNIT = new Unit(null);
            
            private Unit(SourceRange sourceRange) {
                super(sourceRange);
            }
            
            public static Unit make() {
                return UNIT;             
            }
            
            static Unit makeAnnotated(SourceRange sourceRange) {
                return new Unit(sourceRange);
            }
            
            int precedenceLevel() {
                return 100;
            }
            
            Associativity associativity() {
                return Associativity.NON;
            }             
            
           
            boolean neverNeedsParentheses() {                  
                return true;
            } 
            
            ParseTreeNode toParseTreeNode() {
                ParseTreeNode exprNode = new ParseTreeNode(CALTreeParserTokenTypes.TUPLE_CONSTRUCTOR, "Tuple");
                return exprNode;
            }
            
            /**
             * {@inheritDoc}
             */
            public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                return visitor.visit_Expr_Unit(this, arg);
            }
            
        }
        
        /**
         * Models the notational form of tuples in CAL source (dimension 2 or greater) e.g. (2.0, 'a'), ("hello", sin 2.0, True)
         * @author Bo Ilic
         */
        public static final class Tuple extends Expr {
            
            private final Expr[] components;
            
            private Tuple(Expr[] components, SourceRange sourceRange) {
                super(sourceRange);
                if (components == null || components.length < 2) {
                    throw new IllegalArgumentException("tuples must have 2 or more components.");
                }                          
                this.components = components.clone();
                verifyArrayArg(components, "components");                
            }
            
            public static Tuple make(Expr[] components) {
                return new Tuple(components, null);
            }
            
            static Tuple makeAnnotated(Expr[] components, SourceRange sourceRange) {
                return new Tuple(components, sourceRange);
            }
            
            int precedenceLevel() {
                return 100;
            }
            
            Associativity associativity() {
                return Associativity.NON;
            }             
            
            
            boolean neverNeedsParentheses() {                  
                return true;
            } 
            
            public Expr[] getComponents() {
                return components.clone();
            }
            
            /**
             * Get the number of components.
             * @return the number of components.
             */
            public int getNComponents() {
                return components.length;
            }
            
            /**
             * Get the nth component.
             * @param n the index of the component to return.
             * @return the nth component.
             */
            public Expr getNthComponent(int n) {
                return components[n];
            }
            
            ParseTreeNode toParseTreeNode() {
                ParseTreeNode tupleNode = new ParseTreeNode(CALTreeParserTokenTypes.TUPLE_CONSTRUCTOR, "Tuple");
                
                ParseTreeNode[] componentNodes = new ParseTreeNode[components.length];
                for (int i = 0; i < components.length; i++) {
                    componentNodes[i] = components[i].toParseTreeNode();                
                }
                tupleNode.addChildren(componentNodes);
                
                return tupleNode;
            }
            
            /**
             * {@inheritDoc}
             */
            public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                return visitor.visit_Expr_Tuple(this, arg);
            }
            
        }
        
        /**
         * Models the notational form a list in CAL source e.g. ["dog", "cat", "horse"]
         * @author Bo Ilic
         */
        public static final class List extends Expr {
            private final Expr[] elements;
            private static final Expr[] NO_ELEMENTS = new Expr[0];
            
            public static final List EMPTY_LIST = new List (NO_ELEMENTS, null);
            
            private List(Expr[] elements, SourceRange sourceRange) {
                super(sourceRange);
                if (elements == null || elements.length == 0) {
                    this.elements = NO_ELEMENTS;
                } else {
                    this.elements = elements.clone();
                    verifyArrayArg(this.elements, "elements");                                       
                }
            }
            
            private List(java.util.List<Expr> elementList, SourceRange sourceRange) {
                super(sourceRange);
                if (elementList == null || elementList.isEmpty()) {
                    this.elements = NO_ELEMENTS;
                } else {
                    this.elements = elementList.toArray(new Expr[elementList.size()]);
                    verifyArrayArg(this.elements, "elements");                                       
                }
            }
            
            public static List make(Expr[] elements) {
                return new List(elements, null);
            }
            
            public static List make (java.util.List<Expr> elementList) {
                return new List(elementList, null);
            }
            
            static List makeAnnotated(Expr[] elements, SourceRange sourceRange) {
                return new List(elements, sourceRange);
            }
            
            int precedenceLevel() {
                return 100;
            }
            
            Associativity associativity() {
                return Associativity.NON;
            }             
                        
            boolean neverNeedsParentheses() {                  
                return true;
            }
            
            public Expr[] getElements() {
                if (elements.length == 0) {
                    return NO_ELEMENTS;
                }
                
                return elements.clone();
            }
            
            /**
             * Get the number of elements.
             * @return the number of elements.
             */
            public int getNElements() {
                return elements.length;
            }
            
            /**
             * Get the nth element.
             * @param n the index of the element to return.
             * @return the nth element.
             */
            public Expr getNthElement(int n) {
                return elements[n];
            }

            ParseTreeNode toParseTreeNode() {
                ParseTreeNode listNode = new ParseTreeNode(CALTreeParserTokenTypes.LIST_CONSTRUCTOR, CAL_Prelude.TypeConstructors.List.getUnqualifiedName());
                
                ParseTreeNode[] elementNodes = new ParseTreeNode[elements.length];
                for (int i = 0; i < elements.length; i++) {
                    elementNodes[i] = elements[i].toParseTreeNode();
                }
                listNode.addChildren(elementNodes);
                
                return listNode;
            }
            
            /**
             * {@inheritDoc}
             */
            public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                return visitor.visit_Expr_List(this, arg);
            }
        }
        
        /**
         * Models record expressions using the brace notation such as 
         * {r | field1 = "abc", #2 = 100.0}
         * {}
         * {f x | }
         * 
         * This includes both record literals, as well as record modifications (which can include field extensions or
         * field value updates) e.g.
         * {r | #1 := "abc", foo = "bar", name := "Fred", age = 2.0}
         * 
         * @author Bo Ilic
         */
        public static final class Record extends Expr {
            
            private final Expr baseRecordExpr;
            
            private final FieldModification[] fieldModifications;
            public static final FieldModification[] NO_FIELD_MODIFICATIONS = new FieldModification[0];             
            
            /**
             * Models an element of the field modification list (the right hand side of the "|" in a record expression.
             * Currently these are either field extensions such as "foo = 2.0" or field value updates such as "bar := 10.0".
             * 
             * @author Bo Ilic
             */
            public static abstract class FieldModification extends SourceElement {
                private final Name.Field fieldName;
                private final Expr valueExpr;
                
                public static final class Extension extends FieldModification {
                    private Extension(Name.Field fieldName, Expr valueExpr) {
                        super(fieldName, valueExpr);
                    }
                    
                    public static Extension make(Name.Field fieldName, Expr valueExpr) {
                        return new Extension(fieldName, valueExpr);
                    } 
                                                            
                    /**
                     * {@inheritDoc}
                     */
                    public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                        return visitor.visit_Expr_Record_FieldModification_Extension(this, arg);
                    } 
                    
                    /** {@inheritDoc} */
                    ParseTreeNode makeFieldModificationNode () {
                        return new ParseTreeNode(CALTreeParserTokenTypes.FIELD_EXTENSION, "FIELD_EXTENSION");
                    }
                                                                
                }
                
                public static final class Update extends FieldModification {
                    private Update(Name.Field fieldName, Expr valueExpr) {
                        super(fieldName, valueExpr);
                    }
                    
                    public static Update make(Name.Field fieldName, Expr valueExpr) {
                        return new Update(fieldName, valueExpr);
                    } 
                                       
                    /**
                     * {@inheritDoc}
                     */
                    public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                        return visitor.visit_Expr_Record_FieldModification_Update(this, arg);
                    }  
                    
                    /** {@inheritDoc} */
                    ParseTreeNode makeFieldModificationNode () {
                        return new ParseTreeNode(CALTreeParserTokenTypes.FIELD_VALUE_UPDATE, "FIELD_VALUE_UPDATE");
                    }                    
                }
                                              
                private FieldModification(Name.Field fieldName, Expr valueExpr) {
                    verifyArg(fieldName, "fieldName");
                    verifyArg(valueExpr, "valueExpr");
                    
                    this.fieldName = fieldName;
                    this.valueExpr = valueExpr;
                }                
                
                public Name.Field getFieldName() {
                    return fieldName;
                }
                
                public Expr getValueExpr() {
                    return valueExpr;
                }
                
                                               
                abstract ParseTreeNode makeFieldModificationNode();
                
                /** {@inheritDoc} */
                ParseTreeNode toParseTreeNode() {
                    ParseTreeNode fieldModificationNode = makeFieldModificationNode();
                    ParseTreeNode fieldNameNode = fieldName.toParseTreeNode();
                    ParseTreeNode assignmentNode = valueExpr.toParseTreeNode();
                    
                    fieldModificationNode.setFirstChild(fieldNameNode);
                    fieldNameNode.setNextSibling(assignmentNode);
                    return fieldModificationNode;
                }
                
            }
            
            /**              
             * If extensionFields is [(field1, expr1), ..., (fieldN, exprN)] then this models the record extension
             * {baseRecordExpr | field1 = expr1, field2 = expr2, ..., fieldN = exprN}
             * 
             * if baseRecordExpr is null, this is just
             * {field1 = expr1, field2 = expr2, ..., fieldN = exprN}
             * 
             * if extensionField is empty and baseRecordExpr is null, this is the empty record {}.
             * 
             * @param baseRecordExpr Expr may be null to indicate a record with a non-polymorphic 
             * @param extensionFields May not be null, nor can any of its elements be null.
             */
            private Record (Expr baseRecordExpr, FieldModification[] extensionFields) {
                if (extensionFields == null || extensionFields.length == 0) {
                    this.fieldModifications = NO_FIELD_MODIFICATIONS;
                } else {
                    this.fieldModifications = extensionFields.clone();
                    verifyArrayArg(this.fieldModifications, "extensionFields");                                       
                }
                
                this.baseRecordExpr = baseRecordExpr;
            }
            
            /**              
             * If extensionFields is [(field1, expr1), ..., (fieldN, exprN)] then this models the record extension
             * {baseRecordExpr | field1 = expr1, field2 = expr2, ..., fieldN = exprN}
             * 
             * if baseRecordExpr is null, this is just
             * {field1 = expr1, field2 = expr2, ..., fieldN = exprN}
             * 
             * if extensionField is empty and baseRecordExpr is null, this is the empty record {}.
             * 
             * @param baseRecordExpr Expr may be null to indicate a record with a non-polymorphic 
             * @param extensionFields May not be null, nor can any of its elements be null.
             * @return an instance of Record
             */
            public static Record make(Expr baseRecordExpr, FieldModification[] extensionFields) {
                return new Record(baseRecordExpr, extensionFields);
            }
            
            int precedenceLevel() {
                return 100;
            }
            
            Associativity associativity() {
                return Associativity.NON;
            }             
            
          
            
            boolean neverNeedsParentheses() {                  
                return true;
            }  
            
            public Expr getBaseRecordExpr() {
                return baseRecordExpr;
            }
            
            public FieldModification[] getExtensionFields() {
                if (fieldModifications.length == 0) {
                    return NO_FIELD_MODIFICATIONS;
                }
                
                return fieldModifications.clone();
            }
            
            /**
             * Get the number of extension fields.
             * @return the number of extension fields.
             */
            public int getNExtensionFields() {
                return fieldModifications.length;
            }
            
            /**
             * Get the nth extension field.
             * @param n the index of the extension field to return.
             * @return the nth extension field.
             */
            public FieldModification getNthExtensionField(int n) {
                return fieldModifications[n];
            }
            
            ParseTreeNode toParseTreeNode() {
                ParseTreeNode recordExprNode = new ParseTreeNode(CALTreeParserTokenTypes.RECORD_CONSTRUCTOR, "Record");
                ParseTreeNode baseRecordNode = new ParseTreeNode(CALTreeParserTokenTypes.BASE_RECORD, "BASE_RECORD");
                ParseTreeNode fieldModificationListNode = new ParseTreeNode(CALTreeParserTokenTypes.FIELD_MODIFICATION_LIST, "FIELD_MODIFICATION_LIST");
                
                if (baseRecordExpr != null) {
                    ParseTreeNode baseRecordExprNode = baseRecordExpr.toParseTreeNode();
                    baseRecordNode.setFirstChild(baseRecordExprNode);
                }
                
                ParseTreeNode[] fieldValueAssignmentNodes = new ParseTreeNode[fieldModifications.length];
                for (int i = 0; i < fieldModifications.length; i++) {
                    fieldValueAssignmentNodes[i] = fieldModifications[i].toParseTreeNode();
                }
                fieldModificationListNode.addChildren(fieldValueAssignmentNodes);
                
                recordExprNode.setFirstChild(baseRecordNode);
                baseRecordNode.setNextSibling(fieldModificationListNode);
                return recordExprNode;
            }
            
            /**
             * {@inheritDoc}
             */
            public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                return visitor.visit_Expr_Record(this, arg);
            }            
        }
        
        /**
         * Models field selection from a record or record expression (the . operator) e.g. "r.name", "r.#2"
         * @author Bo Ilic
         */
        public static final class SelectRecordField extends Expr {
            
            private final Expr recordValuedExpr;
            private final Name.Field fieldName;
            
            /**
             * Constructs the model for "recordValuedExpr.fieldName".
             * @param recordValuedExpr Expr
             * @param fieldName FieldName
             */
            private SelectRecordField(Expr recordValuedExpr, Name.Field fieldName) {
                if (recordValuedExpr == null || fieldName == null) {
                    throw new IllegalArgumentException();
                }
                this.recordValuedExpr = recordValuedExpr;
                this.fieldName = fieldName;                 
            }
            
            /**
             * Constructs the model for "recordValuedExpr.fieldName".
             * @param recordValuedExpr Expr
             * @param fieldName FieldName
             * @return an instance of SelectRecordField
             */
            public static SelectRecordField make(Expr recordValuedExpr, Name.Field fieldName) {
                return new SelectRecordField(recordValuedExpr, fieldName);
            }
            
            int precedenceLevel() {
                return 90;
            }
            
            Associativity associativity() {
                return Associativity.LEFT;
            }             
            
            
            boolean neverNeedsParentheses() {                
                return true;
            } 
            
            public Expr getRecordValuedExpr() {
                return recordValuedExpr;
            }
            
            public Name.Field getFieldName() {
                return fieldName;
            }
            
            ParseTreeNode toParseTreeNode() {
                ParseTreeNode selectFieldNode = new ParseTreeNode(CALTreeParserTokenTypes.SELECT_RECORD_FIELD, "SELECT_RECORD_FIELD");
                ParseTreeNode exprNode = recordValuedExpr.toParseTreeNode();
                ParseTreeNode fieldNameNode = fieldName.toParseTreeNode();
                
                selectFieldNode.setFirstChild(exprNode);
                exprNode.setNextSibling(fieldNameNode);
                return selectFieldNode;
            }
            
            /**
             * {@inheritDoc}
             */
            public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                return visitor.visit_Expr_SelectRecordField(this, arg);
            }
        }
        
        /**
         * Models field selection from an expression which evaluates to a data constructor (the . operator) e.g. "(expr).Cons.head".
         * @author Edward Lam
         */
        public static final class SelectDataConsField extends Expr {
            
            private final Expr dataConsValuedExpr;
            private final Name.DataCons dataConsName;
            private final Name.Field fieldName;
            
            /**
             * Constructs the model for "dataConsValuedExpr.dataConsName.fieldName".
             * @param dataConsValuedExpr
             * @param dataConsName
             * @param fieldName
             * @param sourceRange
             */
            private SelectDataConsField(Expr dataConsValuedExpr, Name.DataCons dataConsName, Name.Field fieldName, SourceRange sourceRange) {
                super(sourceRange);
                
                if (dataConsValuedExpr == null || dataConsName == null || fieldName == null) {
                    throw new IllegalArgumentException();
                }
                this.dataConsValuedExpr = dataConsValuedExpr;
                this.dataConsName = dataConsName;
                this.fieldName = fieldName;                 
            }
            
            /**
             * Constructs the model for "dataConsValuedExpr.dataConsName.fieldName".
             * @param dataConsValuedExpr
             * @param dataConsName
             * @param fieldName
             * @return an instance of SelectDataConsField
             */
            public static SelectDataConsField make(Expr dataConsValuedExpr, Name.DataCons dataConsName, Name.Field fieldName) {
                return new SelectDataConsField(dataConsValuedExpr, dataConsName, fieldName, null);
            }
            
            static SelectDataConsField makeAnnotated(Expr dataConsValuedExpr, Name.DataCons dataConsName, Name.Field fieldName, SourceRange sourceRange) {
                return new SelectDataConsField(dataConsValuedExpr, dataConsName, fieldName, sourceRange);
            }

            int precedenceLevel() {
                return 90;
            }
            
            Associativity associativity() {
                return Associativity.LEFT;
            }             
            
         
            boolean neverNeedsParentheses() {                
                return true;
            } 
            
            public Expr getDataConsValuedExpr() {
                return dataConsValuedExpr;
            }
            
            public Name.DataCons getDataConsName() {
                return dataConsName;
            }
            
            public Name.Field getFieldName() {
                return fieldName;
            }
            
            /**
             * @deprecated this is deprecated to signify that the returned source range is not the entire definition, but
             * rather a portion thereof.
             */
            // todo-jowong refactor this so that the primary source range of the source element is its entire source range
            @Deprecated
            SourceRange getSourceRange() {
                return getSourceRangeOfName();
            }

            SourceRange getSourceRangeOfName() {
                return super.getSourceRange();
            }

            ParseTreeNode toParseTreeNode() {
                ParseTreeNode selectFieldNode = new ParseTreeNode(CALTreeParserTokenTypes.SELECT_DATA_CONSTRUCTOR_FIELD, "SELECT_DATA_CONSTRUCTOR_FIELD");
                ParseTreeNode exprNode = dataConsValuedExpr.toParseTreeNode();
                ParseTreeNode dataConsNameNode = dataConsName.toParseTreeNode();
                ParseTreeNode fieldNameNode = fieldName.toParseTreeNode();
                
                selectFieldNode.setFirstChild(exprNode);
                exprNode.setNextSibling(dataConsNameNode);
                dataConsNameNode.setNextSibling(fieldNameNode);
                return selectFieldNode;
            }
            
            /**
             * {@inheritDoc}
             */
            public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                return visitor.visit_Expr_SelectDataConsField(this, arg);
            }
        }
        
        /**
         * Models expression type signatures such as "1 + 3 :: Prelude.Int", "[] :: [Char]".
         * @author Bo Ilic
         */
        public static final class ExprTypeSignature extends Expr {
            
            private final Expr expr;
            private final TypeSignature typeSignature;
            
            private ExprTypeSignature (Expr expr, TypeSignature typeSignature) {
                verifyArg(expr, "expr");
                verifyArg(typeSignature, "typeSignature");                 
                
                this.expr = expr;
                this.typeSignature = typeSignature;
            }
            
            public static ExprTypeSignature make (Expr expr, TypeSignature typeSignature) {
                return new ExprTypeSignature(expr, typeSignature);
            }
            
            int precedenceLevel() {
                return 0;
            }
            
            Associativity associativity() {
                return Associativity.NON;
            }             
            
           
            boolean neverNeedsParentheses() {                  
                return false;
            } 
            
            public Expr getExpr() {
                return expr;
            }
            
            public TypeSignature getTypeSignature() {
                return typeSignature;
            }
            
            ParseTreeNode toParseTreeNode() {
                ParseTreeNode selectFieldNode = new ParseTreeNode(CALTreeParserTokenTypes.EXPRESSION_TYPE_SIGNATURE, "::");
                ParseTreeNode exprNode = expr.toParseTreeNode();
                ParseTreeNode signatureNode = typeSignature.toParseTreeNode();
                
                selectFieldNode.setFirstChild(exprNode);
                exprNode.setNextSibling(signatureNode);
                return selectFieldNode;
            }
            
            /**
             * {@inheritDoc}
             */
            public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                return visitor.visit_Expr_ExprTypeSignature(this, arg);
            }
        }
        
        private Expr () {
        }
        
        private Expr(SourceRange sourceRange) {
            super(sourceRange);
        }
        
        /**
         * The higher the value, the greater the precedence level. For example, * would be higher than +.
         * @return int precedence level. Has no absolute significance, only relative to the precedence
         *     level for other expressions.
         */
        abstract int precedenceLevel();
        
        abstract Associativity associativity();
        
               
        abstract boolean neverNeedsParentheses(); 
       
        
        /**
         * Makes a literal-like value of type Prelude.Boolean.
         * @param value
         * @return Expr
         */
        public static Expr makeBooleanValue(boolean value) {
            QualifiedName name = value ? CAL_Prelude.DataConstructors.True : CAL_Prelude.DataConstructors.False;
            return Expr.makeGemCall(name);
        }         
        
        /**
         * Makes a literal-like value of type Prelude.Int.
         * 
         * For example, for the argument value 3, this produces the expression "3 :: Prelude.Int". Effectively, this is the
         * way to make Int literal values in CAL since the raw value 3 is an overloaded value of type Num a => a.
         * 
         * @param value int
         * @return Expr value of type Prelude.Int
         */
        public static Expr makeIntValue(int value) {            
            return new ExprTypeSignature(new Literal.Num(value), new TypeSignature(TypeExprDefn.TypeCons.make(CAL_Prelude.MODULE_NAME, CAL_Prelude.TypeConstructors.Int.getUnqualifiedName())));
        }
        
        /**
         * Makes a literal-like value of type Prelude.Short.
         * 
         * For example, for the argument value 3, this produces the expression "3 :: Prelude.Short". Effectively, this is the
         * way to make Int literal values in CAL since the raw value 3 is an overloaded value of type Num a => a.
         * 
         * @param value short
         * @return Expr value of type Prelude.Short
         */
        public static Expr makeShortValue (short value) {
            return new ExprTypeSignature(new Literal.Num(value), new TypeSignature(TypeExprDefn.TypeCons.make(CAL_Prelude.MODULE_NAME, CAL_Prelude.TypeConstructors.Short.getUnqualifiedName())));
        }

        /**
         * Makes a literal-like value of type Prelude.Byte.
         * 
         * For example, for the argument value 3, this produces the expression "3 :: Prelude.Byte". Effectively, this is the
         * way to make Int literal values in CAL since the raw value 3 is an overloaded value of type Num a => a.
         * 
         * @param value byte
         * @return Expr value of type Prelude.Byte
         */
        public static Expr makeByteValue (byte value) {
            return new ExprTypeSignature(new Literal.Num(value), new TypeSignature(TypeExprDefn.TypeCons.make(CAL_Prelude.MODULE_NAME, CAL_Prelude.TypeConstructors.Byte.getUnqualifiedName())));
        }

        /**
         * Makes a literal-like value of type Prelude.Long.
         * 
         * For example, for the argument value 3, this produces the expression "3 :: Prelude.Long". Effectively, this is the
         * way to make Long literal values in CAL since the raw value 3 is an overloaded value of type Num a => a.
         * 
         * @param value long
         * @return Expr value of type Prelude.Long
         */
        public static Expr makeLongValue(long value) {
            return new ExprTypeSignature(new Literal.Num(value), new TypeSignature(TypeExprDefn.TypeCons.make(CAL_Prelude.MODULE_NAME, CAL_Prelude.TypeConstructors.Long.getUnqualifiedName())));
        }
        
        /**
         * Makes a literal-like value of type Prelude.Integer (which is the arbitrary precision integer type in CAL).
         * 
         * For example, for the argument value 3, this produces the expression "3 :: Prelude.Integer". Effectively, this is the
         * way to make Integer literal values in CAL since the raw value 3 is an overloaded value of type Num a => a.
         * 
         * @param value BigInteger
         * @return Expr value of type Prelude.Integer
         */
        public static Expr makeIntegerValue(BigInteger value) {
            return new ExprTypeSignature(new Literal.Num(value, null), new TypeSignature(TypeExprDefn.TypeCons.make(CAL_Prelude.MODULE_NAME, CAL_Prelude.TypeConstructors.Integer.getUnqualifiedName())));
        }         
        
        /**
         * A convenience method for creating a Double literal value instead of calling the Expr.Literal.Double constructor.
         * 
         * @param value double
         * @return Expr value of type Prelude.Double
         */
        public static Expr makeDoubleValue(double value) {
            return new Literal.Double(value, null);
        }
        
        /**
         * A convenience method for creating a Float literal value instead of calling the Expr.Literal.Float constructor.
         * 
         * @param value float
         * @return Expr value of type Prelude.Float
         */
        public static Expr makeFloatValue(float value) {
            return new Literal.Float(value, null);
        }
        
        /**
         * A convenience method for creating a String literal value instead of calling the Expr.Literal.StringLit constructor.
         * 
         * @param value String
         * @return Expr value of type Prelude.String
         */         
        public static Expr makeStringValue(String value) {
            return new Literal.StringLit(value, null);
        }
        
        /**
         * A convenience method for creating a Char literal value instead of calling the Expr.Literal.Char constructor.
         * 
         * @param value char
         * @return Expr value of type Prelude.Char
         */
        public static Expr makeCharValue(char value) {
            return new Literal.Char(value, null);
        }
        
        /**
         * A helper used to create a call to Prelude.error with an error message
         * 
         * @param msg the error message
         * @return Expr a call to Prelude.error with the supplied error message
         */
        public static Expr makeErrorCall(String msg) {
            return makeGemCall(CAL_Prelude.Functions.error, new Literal.StringLit(msg, null));
        }
        
        /** 
         * A helper used to create a reference to a gem in an expression, where no arguments are supplied e.g.
         * Prelude.Nothing
         * Prelude.pi
         * 
         * In this context, "gem" means a function, class method or data constructor.
         *          
         * @param gemName a non-null lexically valid gem name.
         * @return Expr reference to the gem as an Expr.
         */
        public static Expr makeGemCall(QualifiedName gemName) {
            if (gemName.lowercaseFirstChar()) {
                return Expr.Var.make(gemName);
            } else {
                return Expr.DataCons.make(gemName);
            }
        }
        
        /**
         * Used to create an application of a gem to a single argument such as
         * Prelude.sin 2.0
         * or
         * Prelude.Just "abc"
         *           
         * In this context, "gem" means a function, class method or data constructor.
         *           
         * @param gemName a non-null lexically valid gem name.
         * @param arg1 non-null argument to be applied.
         * @return the application of the gem to the argument arg1.
         */
        public static Expr makeGemCall(QualifiedName gemName, Expr arg1) {
            return makeGemCall(gemName, new Expr[] {arg1});
        }
        
        /**
         * Used to create an application of a gem to a single argument such as
         * Prelude.add 2.0 3.0
         * or
         * Prelude.Tuple2 "abc" 'a'
         *           
         * In this context, "gem" means a function, class method or data constructor.
         *           
         * @param gemName a non-null lexically valid gem name.
         * @param arg1 non-null argument to be applied.
         * @param arg2 non-null argument to be applied.
         * @return the application of the gem to the argument arg1.
         */         
        public static Expr makeGemCall(QualifiedName gemName, Expr arg1, Expr arg2) {
            return makeGemCall(gemName, new Expr[] {arg1, arg2});
        }
        
        public static Expr makeGemCall(QualifiedName gemName, Expr arg1, Expr arg2, Expr arg3) {
            return makeGemCall(gemName, new Expr[] {arg1, arg2, arg3});
        }
        
        public static Expr makeGemCall(QualifiedName gemName, Expr arg1, Expr arg2, Expr arg3, Expr arg4) {
            return makeGemCall(gemName, new Expr[] {arg1, arg2, arg3, arg4});
        }                                
        
        public static Expr makeGemCall(QualifiedName gemName, Expr[] args) {
            
            if (args == null) {
                return makeGemCall(gemName);
            }
            
            int nArgs = args.length;
            if (nArgs == 0) {
                return makeGemCall(gemName);
            }
            
            Expr[] exprs = new Expr[nArgs + 1];
            exprs[0] = makeGemCall(gemName);
            
            System.arraycopy(args, 0, exprs, 1, nArgs);
            
            return new Application(exprs);
        }
        
    }
    
    /**
     * Models a type expression with a context (possible empty) such as 
     * Num a => a -> a -> a
     * r\field1 => {r : field1 :: Int} -> Int   
     * 
     * the context part is the part of the type signature to the left of the "=>" in the CAL source. 
     * type typeExprDefn part is the part of the type signature to the right of the "=>".
     * 
     * @author Bo Ilic
     */
    public static final class TypeSignature extends SourceElement {
        
        private final Constraint[] constraints;
        private final TypeExprDefn typeExprDefn;

        /** this field is used to indicate if the constraints should be contained within parens*/
        private final boolean parenthesizedConstraints;
        
        public static final Constraint[] NO_CONSTRAINTS = new Constraint[0];         
        
        private TypeSignature(Constraint[] constraints, TypeExprDefn typeExprDefn, boolean parenthesizedConstraints) {
            if (constraints == null || constraints.length == 0) {
                this.constraints = NO_CONSTRAINTS;
            } else {
                this.constraints = constraints.clone();
                verifyArrayArg(this.constraints, "constraints");                                       
            }
            this.parenthesizedConstraints = parenthesizedConstraints;
            
            verifyArg(typeExprDefn, "typeExprDefn");             
            
            this.typeExprDefn = typeExprDefn;
        }
        
        /**
         * Constructs a type expression definition with no context part (i.e. no type class or lacks constraints).
         * @param typeExprDefn TypeExprDefn
         */
        private TypeSignature(TypeExprDefn typeExprDefn, boolean parenthesizedConstraints) {
            this (NO_CONSTRAINTS, typeExprDefn, parenthesizedConstraints);               
        }
        
        /**
         * Constructs a type expression definition with no context part (i.e. no type class or lacks constraints).
         * @param typeExprDefn TypeExprDefn
         */
        private TypeSignature(TypeExprDefn typeExprDefn) {
            this (NO_CONSTRAINTS, typeExprDefn, false);               
        }
        
        public static TypeSignature make(Constraint[] constraints, TypeExprDefn typeExprDefn) {
            return new TypeSignature(constraints, typeExprDefn, false);
        }
        
        static TypeSignature make(Constraint[] constraints, TypeExprDefn typeExprDefn, boolean parens) {
            return new TypeSignature(constraints, typeExprDefn, parens);
        }
        
        /**
         * Constructs a type expression definition with no context part (i.e. no type class or lacks constraints).
         * @param typeExprDefn TypeExprDefn
         * @return an instance of TypeSignature
         */
        public static TypeSignature make(TypeExprDefn typeExprDefn) {
            return new TypeSignature(typeExprDefn, false);
        }
        

        
        public Constraint[] getConstraints() {
            if (constraints.length == 0) {
                return NO_CONSTRAINTS;
            }
            
            return constraints.clone();
        }
        
        /**this is true if the constraints are parenthesized*/
        public boolean getConstraintsHaveParen() {
            return parenthesizedConstraints;
        }
        
        /**
         * Get the number of constraints.
         * @return the number of constraints.
         */
        public int getNConstraints() {
            return constraints.length;
        }
        
        /**
         * Get the nth constraint.
         * @param n the index of the constraint to return.
         * @return the nth constraint.
         */
        public Constraint getNthConstraint(int n) {
            return constraints[n];
        }
        
        public TypeExprDefn getTypeExprDefn() {
            return typeExprDefn;
        }       
        
        ParseTreeNode toParseTreeNode() {
            ParseTreeNode typeSignatureNode = new ParseTreeNode(CALTreeParserTokenTypes.TYPE_SIGNATURE, "TYPE_SIGNATURE");
            final ParseTreeNode contextListNode;
            if (parenthesizedConstraints || constraints.length > 1) {
                contextListNode = new ParseTreeNode(CALTreeParserTokenTypes.TYPE_CONTEXT_LIST, "TYPE_CONTEXT_LIST"); 
            } else if (constraints.length == 1) {
                contextListNode = new ParseTreeNode(CALTreeParserTokenTypes.TYPE_CONTEXT_SINGLETON, "TYPE_CONTEXT_SINGLETON");
            } else {
                contextListNode = new ParseTreeNode(CALTreeParserTokenTypes.TYPE_CONTEXT_NOTHING, "TYPE_CONTEXT_NOTHING");
            }
            ParseTreeNode typeExprNode = typeExprDefn.toParseTreeNode();
            
            ParseTreeNode[] contextNodes = new ParseTreeNode[constraints.length];
            for (int i = 0; i < constraints.length; i++) {
                contextNodes[i] = constraints[i].toParseTreeNode();
            }
            contextListNode.addChildren(contextNodes);
            
            typeSignatureNode.setFirstChild(contextListNode);
            contextListNode.setNextSibling(typeExprNode);
            return typeSignatureNode;
        }
        
        /**
         * {@inheritDoc}
         */
        public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
            return visitor.visit_TypeSignature(this, arg);
        }
    }
    
    /**
     * Models the part of a type definition to the right of the context symbol (=>). For example:
     * Int -> Char 
     * (a -> b) -> a -> b
     * [(Int, String)]
     * {#1 :: [a], #2 :: Boolean} -> Maybe Char
     * 
     * @author Bo Ilic
     */
    public static abstract class TypeExprDefn extends SourceElement {
        
        /**
         * Models a parenthesized type expression
         *
         * @author Magnus Byne
         */
        public static final class Parenthesized extends TypeExprDefn {
            private final TypeExprDefn expr;
            
            private Parenthesized(TypeExprDefn typeExpr) {
                this(typeExpr, null);
            }
            
            private Parenthesized(TypeExprDefn typeExpr, SourceRange range) {
                super(range);
                expr=typeExpr;
            }
            
            /**
             * Get the type definition that has been parenthesized
             */
            public TypeExprDefn getTypeExprDefn() {
                return expr;
            }
            
            public static Parenthesized make(TypeExprDefn typeExpr) {
                return new Parenthesized(typeExpr);
            }
            
            static Parenthesized makeAnnotated(TypeExprDefn typeExpr, SourceRange sourceRange) {
                return new Parenthesized(typeExpr, sourceRange);
            }
            
            /**
             * {@inheritDoc}
             */
            boolean neverNeedsParentheses() {              
                return true;
            }
            
            /**
             * {@inheritDoc}
             */
            ParseTreeNode toParseTreeNode() {
                ParseTreeNode parenthesized = new ParseTreeNode(CALTreeParserTokenTypes.TUPLE_TYPE_CONSTRUCTOR, "Tuple");
                parenthesized.addChildren(new ParseTreeNode[] {expr.toParseTreeNode()});
                
                return parenthesized;
            }
            
            /**
             * {@inheritDoc}
             */
            public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                return visitor.visit_TypeExprDefn_Parenthesized(this, arg);
            }
        }
        
        /**
         * Models types made out of "->", which is the Prelude.Function type constructor in operator form.
         * In particular, Prelude.Function must be fully saturated.
         * 
         * @author Bo Ilic
         */
        public static final class Function extends TypeExprDefn {
            private final TypeExprDefn domain;
            private final TypeExprDefn codomain;
            private final SourceRange operatorSourceRange;
            
            private Function (TypeExprDefn domain, TypeExprDefn codomain, SourceRange sourceRange, SourceRange operatorSourceRange) {
                super(sourceRange);
                
                verifyArg(domain, "domain");
                verifyArg(codomain, "codomain");
                
                this.domain = domain;
                this.codomain = codomain;
                this.operatorSourceRange = operatorSourceRange;
            }
            
            public static Function make(TypeExprDefn domain, TypeExprDefn codomain) {
                return new Function(domain, codomain, null, null);
            }
            
            static Function makeAnnotated(TypeExprDefn domain, TypeExprDefn codomain, SourceRange sourceRange, SourceRange operatorSourceRange) {
                return new Function(domain, codomain, sourceRange, operatorSourceRange);
            }
            
            // todo-jowong maybe the operator can be encapsulated by its own source element
            SourceRange getOperatorSourceRange() {
                return operatorSourceRange;
            }
          
            
            boolean neverNeedsParentheses() {              
                return false;
            }
            
            public TypeExprDefn getDomain() {
                return domain;
            }
            
            public TypeExprDefn getCodomain() {
                return codomain;
            }
            
            ParseTreeNode toParseTreeNode() {
                ParseTreeNode functionTypeDefnNode = new ParseTreeNode(CALTreeParserTokenTypes.FUNCTION_TYPE_CONSTRUCTOR, "->");
                ParseTreeNode domainNode = domain.toParseTreeNode();
                ParseTreeNode codomainNode = codomain.toParseTreeNode();
                
                functionTypeDefnNode.setFirstChild(domainNode);
                domainNode.setNextSibling(codomainNode);
                return functionTypeDefnNode;
            }
            
            /**
             * {@inheritDoc}
             */
            public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                return visitor.visit_TypeExprDefn_Function(this, arg);
            }
        }
        
        /**
         * Models the notational form of the Prelude.Unit type constructor i.e. ().
         * Note this is different from the Prelude.Unit data constructor, which is also
         * indicated by () in CAL source, but is used differently in the CAL grammar.
         *
         * @author Bo Ilic
         */
        public static final class Unit extends TypeExprDefn {
            
            private static final Unit UNIT = new Unit(null);
            
            private Unit(SourceRange sourceRange) {
                super(sourceRange);
            }
            
            public static Unit make() {
                return UNIT;
            }
            
            static Unit makeAnnotated(SourceRange sourceRange) {
                return new Unit(sourceRange);
            }
            
                                              
            
            boolean neverNeedsParentheses() {                  
                return true;
            }  
            
            ParseTreeNode toParseTreeNode() {
                ParseTreeNode parseTree = new ParseTreeNode(CALTreeParserTokenTypes.TUPLE_TYPE_CONSTRUCTOR, "Tuple");
                return parseTree;
            }
            
            /**
             * {@inheritDoc}
             */
            public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                return visitor.visit_TypeExprDefn_Unit(this, arg);
            }
        }         
        
        public static final class Tuple extends TypeExprDefn {
            private final TypeExprDefn[] components;
            
            private Tuple(TypeExprDefn[] components, SourceRange sourceRange) {
                super(sourceRange);
                
                if (components == null || components.length < 2) {
                    throw new IllegalArgumentException("tuple types must have 2 or more components.");
                }
                this.components = components.clone();
                verifyArrayArg(this.components, "components");                 
            }
            
            public static Tuple make(TypeExprDefn[] components) {
                return new Tuple(components, null);
            }
            
            static Tuple makeAnnotated(TypeExprDefn[] components, SourceRange sourceRange) {
                return new Tuple(components, sourceRange);
            }
            
                                             
            
            boolean neverNeedsParentheses() {                  
                return true;
            }  
            
            public TypeExprDefn[] getComponents() {
                return components.clone();
            }
            
            /**
             * Get the number of components.
             * @return the number of components.
             */
            public int getNComponents() {
                return components.length;
            }
            
            /**
             * Get the nth component.
             * @param n the index of the component to return.
             * @return the nth component.
             */
            public TypeExprDefn getNthComponent(int n) {
                return components[n];
            }
            
            ParseTreeNode toParseTreeNode() {
                ParseTreeNode tupleTypeDefnNode = new ParseTreeNode(CALTreeParserTokenTypes.TUPLE_TYPE_CONSTRUCTOR, "Tuple");
                
                ParseTreeNode[] componentNodes = new ParseTreeNode[components.length];
                for (int i = 0; i < components.length; i++) {
                    componentNodes[i] = components[i].toParseTreeNode();
                }
                tupleTypeDefnNode.addChildren(componentNodes);
                
                return tupleTypeDefnNode;
            }
            
            /**
             * {@inheritDoc}
             */
            public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                return visitor.visit_TypeExprDefn_Tuple(this, arg);
            }
        }
        
        /**
         * Models types made out of "[]", which is the Prelude.List type constructor in notational form.
         * In particular, Prelude.List must be fully saturated i.e. applied to a type argument.
         * 
         * @author Bo Ilic
         */        
        public static final class List extends TypeExprDefn {
            private final TypeExprDefn element;
            
            private List(TypeExprDefn element, SourceRange sourceRange) {
                super(sourceRange);
                
                verifyArg(element, "element");                                 
                this.element = element;
            }
            
            public static List make(TypeExprDefn element) {
                return new List(element, null);
            }
            
            static List makeAnnotated(TypeExprDefn element, SourceRange sourceRange) {
                return new List(element, sourceRange);
            }
            

            
            boolean neverNeedsParentheses() {                  
                return true;
            }  
            
            public TypeExprDefn getElement() {
                return element;
            }
            
            ParseTreeNode toParseTreeNode() {
                ParseTreeNode listTypeNode = new ParseTreeNode(CALTreeParserTokenTypes.LIST_TYPE_CONSTRUCTOR, CAL_Prelude.TypeConstructors.List.getUnqualifiedName());
                
                listTypeNode.setFirstChild(element.toParseTreeNode());
                
                return listTypeNode;
            }
            
            /**
             * {@inheritDoc}
             */
            public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                return visitor.visit_TypeExprDefn_List(this, arg);
            }
        }
        
        public static final class Record extends TypeExprDefn {
            private final TypeVar baseRecordVar;
            
            private final FieldTypePair[] extensionFields;
            public static final FieldTypePair[] NO_EXTENSION_FIELDS = new FieldTypePair[0];
            
            public static final class FieldTypePair extends SourceElement {
                private final Name.Field fieldName;
                private final TypeExprDefn fieldType;
                
                private FieldTypePair (Name.Field fieldName, TypeExprDefn fieldType) {
                    verifyArg(fieldName, "fieldName");
                    verifyArg(fieldType, "fieldType");
                    
                    this.fieldName = fieldName;
                    this.fieldType = fieldType;
                }
                
                public static FieldTypePair make(Name.Field fieldName, TypeExprDefn fieldType) {
                    return new FieldTypePair(fieldName, fieldType);
                }
                
                public Name.Field getFieldName() {
                    return fieldName;
                }
                
                public TypeExprDefn getFieldType() {
                    return fieldType;
                }
                
               
                
                ParseTreeNode toParseTreeNode() {
                    ParseTreeNode fieldTypeAssignmentNode = new ParseTreeNode(CALTreeParserTokenTypes.FIELD_TYPE_ASSIGNMENT, "FIELD_TYPE_ASSIGNMENT");
                    ParseTreeNode fieldNameNode = fieldName.toParseTreeNode();
                    ParseTreeNode typeNode = fieldType.toParseTreeNode();
                    
                    fieldTypeAssignmentNode.setFirstChild(fieldNameNode);
                    fieldNameNode.setNextSibling(typeNode);
                    return fieldTypeAssignmentNode;
                }
                
                /**
                 * {@inheritDoc}
                 */
                public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                    return visitor.visit_TypeExprDefn_Record_FieldTypePair(this, arg);
                }
                
            }
            
            /**              
             * If extensionFields is [(field1, type1), ..., (fieldN, typeN)] then this models the record type
             * {baseRecordVar | field1 :: type1, field2 :: type2, ..., fieldN :: typeN}
             * 
             * if baseRecordVar is null, this is just
             * {field1 :: type1, field2 :: type2, ..., fieldN :: typeN}
             * 
             * if extensionFields is empty and baseRecordVar is null, this is the empty record type {}.
             * 
             * @param baseRecordVar Expr may be null to indicate a record with a non-polymorphic 
             * @param extensionFields May not be null, nor can any of its elements be null.
             * @param sourceRange May be null, indicates the source position of this source element 
             */
            private Record (TypeVar baseRecordVar, FieldTypePair[] extensionFields, SourceRange sourceRange) {
                super(sourceRange);
                
                if (extensionFields == null || extensionFields.length == 0) {
                    this.extensionFields = NO_EXTENSION_FIELDS;
                } else {
                    this.extensionFields = extensionFields.clone();
                    verifyArrayArg(this.extensionFields, "extensionFields");                                       
                }
                
                this.baseRecordVar = baseRecordVar;
            }
            
            /**              
             * If extensionFields is [(field1, type1), ..., (fieldN, typeN)] then this models the record type
             * {baseRecordVar | field1 :: type1, field2 :: type2, ..., fieldN :: typeN}
             * 
             * if baseRecordVar is null, this is just
             * {field1 :: type1, field2 :: type2, ..., fieldN :: typeN}
             * 
             * if extensionFields is empty and baseRecordVar is null, this is the empty record type {}.
             * 
             * @param baseRecordVar Expr may be null to indicate a record with a non-polymorphic 
             * @param extensionFields May not be null, nor can any of its elements be null.
             * @return an instance of Record 
             */
            public static Record make(TypeVar baseRecordVar, FieldTypePair[] extensionFields) {
                return new Record(baseRecordVar, extensionFields, null);
            }
            
            static Record makeAnnotated(TypeVar baseRecordVar, FieldTypePair[] extensionFields, SourceRange sourceRange) {
                return new Record(baseRecordVar, extensionFields, sourceRange);
            }
           
           
            
            boolean neverNeedsParentheses() {                  
                return true;
            } 
            
            public TypeExprDefn.TypeVar getBaseRecordVar() {
                return baseRecordVar;
            }
            
            public FieldTypePair[] getExtensionFields() {
                if (extensionFields.length == 0) {
                    return NO_EXTENSION_FIELDS;
                }
                
                return extensionFields.clone();
            }
            
            /**
             * Get the number of extension fields.
             * @return the number of extension fields.
             */
            public int getNExtensionFields() {
                return extensionFields.length;
            }
            
            /**
             * Get the nth extension field.
             * @param n the index of the extension field to return.
             * @return the nth extension field.
             */
            public FieldTypePair getNthExtensionField(int n) {
                return extensionFields[n];
            }
            
            ParseTreeNode toParseTreeNode() {
                ParseTreeNode recordTypeNode = new ParseTreeNode(CALTreeParserTokenTypes.RECORD_TYPE_CONSTRUCTOR, "Record");
                ParseTreeNode recordVarNode = new ParseTreeNode(CALTreeParserTokenTypes.RECORD_VAR, "RECORD_VAR");
                ParseTreeNode fieldTypeAssignmentListNode = new ParseTreeNode(CALTreeParserTokenTypes.FIELD_TYPE_ASSIGNMENT_LIST, "FIELD_TYPE_ASSIGNMENT_LIST");
                
                if (baseRecordVar != null) {
                    ParseTreeNode recordVarNameNode = baseRecordVar.toParseTreeNode();
                    recordVarNode.setFirstChild(recordVarNameNode);
                }
                
                ParseTreeNode[] fieldTypeAssignmentNodes = new ParseTreeNode[extensionFields.length];
                for (int i = 0; i < extensionFields.length; i++) {
                    fieldTypeAssignmentNodes[i] = extensionFields[i].toParseTreeNode();
                }
                fieldTypeAssignmentListNode.addChildren(fieldTypeAssignmentNodes);
                
                recordTypeNode.setFirstChild(recordVarNode);
                recordVarNode.setNextSibling(fieldTypeAssignmentListNode);
                return recordTypeNode;
            }
            
            /**
             * {@inheritDoc}
             */
            public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                return visitor.visit_TypeExprDefn_Record(this, arg);
            }
        }
        
        public static final class Application extends TypeExprDefn {
            private final TypeExprDefn[] typeExpressions;
            
            /**
             * If expressions is [e1, e2, e3], this is the application e1 e2 e3 i.e. (((e1) e2) e3)
             * @param typeExpressions the expressions defining the application. Must have length >=2, with each element non-null.
             * @param sourceRange may be null.  Represents the source position of this source element.            
             */
            private Application(TypeExprDefn[] typeExpressions, SourceRange sourceRange) {
                super(sourceRange);
                
                if (typeExpressions == null || typeExpressions.length < 2) {
                    throw new IllegalArgumentException("argument 'typeExpressions' must have length >= 2.");
                }
                this.typeExpressions = typeExpressions.clone();
                verifyArrayArg(this.typeExpressions, "typeExpressions");
                
            }
            
            /**
             * If expressions is [e1, e2, e3], this is the application e1 e2 e3 i.e. (((e1) e2) e3)
             * @param typeExpressions the expressions defining the application. Must have length >=2, with each element non-null.
             * @return an instance of Application            
             */
            public static Application make(TypeExprDefn[] typeExpressions) {
                return new Application(typeExpressions, null);
            }
            
            static Application makeAnnotated(TypeExprDefn[] typeExpressions, SourceRange sourceRange) {
                return new Application(typeExpressions, sourceRange);
            }
            

            
            boolean neverNeedsParentheses() {  
                //for example, 'Maybe Maybe Int is the same as (Maybe Maybe) Int
                //so if we want Maybe (Maybe Int) then parentheses are needed.
                return false;
            } 
            
            public TypeExprDefn[] getTypeExpressions() {
                return typeExpressions.clone();
            }
            
            /**
             * Get the number of type expressions.
             * @return the number of type expressions.
             */
            public int getNTypeExpressions() {
                return typeExpressions.length;
            }
            
            /**
             * Get the nth type expression.
             * @param n the index of the type expression to return.
             * @return the nth type expression.
             */
            public TypeExprDefn getNthTypeExpression(int n) {
                return typeExpressions[n];
            }

            ParseTreeNode toParseTreeNode() {
                ParseTreeNode applicationTypeNode = new ParseTreeNode(CALTreeParserTokenTypes.TYPE_APPLICATION, "@");
                
                ParseTreeNode[] exprNodes = new ParseTreeNode[typeExpressions.length];
                for (int i = 0; i < exprNodes.length; i++) {
                    exprNodes[i] = typeExpressions[i].toParseTreeNode();
                }
                applicationTypeNode.addChildren(exprNodes);
                
                return applicationTypeNode;
            }
            
            /**
             * {@inheritDoc}
             */
            public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                return visitor.visit_TypeExprDefn_Application(this, arg);
            }
        }
        
        /**
         * Reference to a non-operator form type constructor (such as Prelude.Just or Prelude.Left)
         * within a type expression definition. 
         * 
         * @author Bo Ilic
         */
        public static final class TypeCons extends TypeExprDefn {
            
            private final Name.TypeCons typeConsName;  
            
            private TypeCons(Name.TypeCons typeConsName, SourceRange sourceRange) {
                super(sourceRange);
                
                verifyArg(typeConsName, "typeConsName");
                this.typeConsName = typeConsName;
            }
            
            public static TypeCons make(Name.TypeCons typeConsName) {
                return new TypeCons(typeConsName, null);
            }
            
            /**                  
             * @param moduleName String may be null, in which case the type constructor name is unqualified. For efficiency, it is better to supply the
             *     explicit qualification if available. If non-null, it must be a syntactically valid module name.
             * @param typeConsName String must be a non-null syntactically valid type constructor name.
             * @return an instance of TypeCons
             */
            public static TypeCons make(ModuleName moduleName, String typeConsName) {
                return new TypeCons(Name.TypeCons.make(moduleName, typeConsName), null);
            }
            
            public static TypeCons make(QualifiedName typeConsName) {
                return make(typeConsName.getModuleName(), typeConsName.getUnqualifiedName());
            }
            
            static TypeCons makeAnnotated(Name.TypeCons typeConsName, SourceRange sourceRange) {
                return new TypeCons(typeConsName, sourceRange);
            }
            

            
            boolean neverNeedsParentheses() {
                return true;
            } 
            
            public Name.TypeCons getTypeConsName() {
                return typeConsName;
            }
            
            ParseTreeNode toParseTreeNode() {
                ParseTreeNode qualifiedTypeConsNameNode = typeConsName.toParseTreeNode();
                return qualifiedTypeConsNameNode;
            }
            
            /**
             * {@inheritDoc}
             */
            public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                return visitor.visit_TypeExprDefn_TypeCons(this, arg);
            }
        }
        
        public static final class TypeVar extends TypeExprDefn {
            
            private final Name.TypeVar typeVarName;
            
            /**                                
             * @param typeVarName String must be a non-null syntactically valid type variable name.
             * @param sourceRange
             */
            private TypeVar(Name.TypeVar typeVarName, SourceRange sourceRange) {                     
                super(sourceRange);
                this.typeVarName = typeVarName;
            }             
            
            /**                                
             * @param typeVarName String must be a non-null syntactically valid type variable name.
             * @return an instance of TypeVar
             */
            public static TypeVar make(Name.TypeVar typeVarName) {
                return new TypeVar(typeVarName, null);
            }
            
            static TypeVar makeAnnotated(Name.TypeVar typeVarName, SourceRange sourceRange) {
                return new TypeVar(typeVarName, sourceRange);
            }
            

            
            boolean neverNeedsParentheses() {
                return true;
            } 
            
            public Name.TypeVar getTypeVarName() {
                return typeVarName;
            }
            
            ParseTreeNode toParseTreeNode() {
                return typeVarName.toParseTreeNode();
            }
            
            /**
             * {@inheritDoc}
             */
            public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                return visitor.visit_TypeExprDefn_TypeVar(this, arg);
            }
        }
        
        private TypeExprDefn(SourceRange sourceRange) {
            super(sourceRange);
        }
        
        abstract boolean neverNeedsParentheses(); 
        
  
    }
    
    /**
     * Models a CAL type class definition. For example, below is the definition of the Ord type class
     * in the Prelude module.     
     * 
     * public class Eq a => Ord a where
     *     public lessThan :: a -> a -> Boolean;          
     *     public lessThanEquals :: a -> a -> Boolean;    
     *     public greaterThanEquals :: a -> a -> Boolean; 
     *     public greaterThan :: a -> a -> Boolean;  
     *     public compare :: a -> a -> Ordering;
     *     public max :: a -> a -> a;
     *     public min :: a -> a -> a;    
     *     ;
     * 
     * Currently, type classes define a condition on a single type variable ("a" above) and we think of
     * a type as "belonging to the set of types defined by the class". However, in the future we may support
     * multi-parameter type classes such as:
     * class (Ord a, Outputable b) => MyMap a b where ...
     * in which we then think of the MyMap type class as being a relationship satisfied by 2 types which take the place
     * of the type variables "a" and "b".     
     * 
     * @author Bo Ilic
     */
    public static final class TypeClassDefn extends TopLevelSourceElement {
        
        /** The CALDoc comment associated with this type class definition, or null if there is none. */
        private final CALDoc.Comment.TypeClass caldocComment;
        
        /** this field is used to indicate if the constraints should be contained within parens*/
        private final boolean parenthesizeConstraints;

        private final String typeClassName;
        
        /** For example, for "class Eq a => Ord a where..." this is the "a" following the Ord. */
        private final Name.TypeVar typeVar;
        
        /** The scope of the definition. */
        private final Scope scope;
        
        /** Whether the scope is explicitly specified in the source. */
        private final boolean isScopeExplicitlySpecified;
        
        /** For example, for "class Eq a => Ord a where..." this is "Eq a". A type class may have 0 or more parent class constraints. */
        private final Constraint.TypeClass[] parentClassConstraints;
        public static final Constraint.TypeClass[] NO_CONSTRAINTS = new Constraint.TypeClass[0];
        
        private final ClassMethodDefn[] classMethodDefns;
        public static final ClassMethodDefn[] NO_CLASS_METHOD_DEFNS = new ClassMethodDefn[0];

        /** The source range for the entire type def statement. */
        private final SourceRange sourceRangeOfDefn;
        
        /**
         * Models a class method definition in CAL source. For example, in the definition of the "Ord" type class above there are 7
         * class methods, one of which is:
         * "public greaterThan :: a -> a -> Boolean"
         * 
         * @author Bo Ilic
         */
        public static final class ClassMethodDefn extends SourceElement {                        
            
            /** The CALDoc comment associated with this class method definition, or null if there is none. */
            private final CALDoc.Comment.ClassMethod caldocComment;
            
            private final String methodName;
            
            /** The scope of the definition. */
            private final Scope scope;
            
            /** Whether the scope is explicitly specified in the source. */
            private final boolean isScopeExplicitlySpecified;
            
            private final TypeSignature typeSignature;
            
            /** the default class method name, if there is one. If the method does not have a default, this is null. */
            private final Name.Function defaultClassMethodName;
            
            private final SourceRange sourceRangeOfClassDefn; 
            
            private ClassMethodDefn(
                CALDoc.Comment.ClassMethod caldocComment,
                String methodName,
                Scope scope, boolean isScopeExplicitlySpecified,
                TypeSignature typeSignature,
                Name.Function defaultClassMethodName, 
                SourceRange sourceRange, 
                SourceRange sourceRangeOfClassDefn) {
                
                super(sourceRange);
                
                this.caldocComment = caldocComment;
                this.sourceRangeOfClassDefn = sourceRangeOfClassDefn;
                
                if (!LanguageInfo.isValidClassMethodName(methodName)) {
                    throw new IllegalArgumentException();
                }
                
                verifyScopeArg(scope, isScopeExplicitlySpecified, "scope");
                verifyArg(typeSignature, "typeSignature");
                
                this.methodName = methodName;
                this.scope = scope;
                this.isScopeExplicitlySpecified = isScopeExplicitlySpecified;
                this.typeSignature = typeSignature;
                this.defaultClassMethodName = defaultClassMethodName;
            }
            
            /**
             * Create an instance of this class without an associated CALDoc comment.
             * @param methodName the name of the class method.
             * @param scope the scope of the class method.
             * @param typeSignature the type signature of the class method.
             * @param defaultClassMethodName
             * @return a new instance of this class.
             */
            public static ClassMethodDefn make(String methodName, Scope scope, TypeSignature typeSignature, Name.Function defaultClassMethodName) {
                return new ClassMethodDefn(null, methodName, scope, true, typeSignature, defaultClassMethodName, null, null);
            }
            
            /**
             * Create an instance of this class with an associated CALDoc comment.
             * @param caldocComment the CALDoc comment.
             * @param methodName the name of the class method.
             * @param scope the scope of the class method.
             * @param isScopeExplicitlySpecified whether the scope is explicitly specified in the source.
             * @param typeSignature the type signature of the class method.
             * @param defaultClassMethodName
             * @return a new instance of this class.
             */
            public static ClassMethodDefn make(CALDoc.Comment.ClassMethod caldocComment, String methodName, Scope scope, boolean isScopeExplicitlySpecified, TypeSignature typeSignature, Name.Function defaultClassMethodName) {
                return new ClassMethodDefn(caldocComment, methodName, scope, isScopeExplicitlySpecified, typeSignature, defaultClassMethodName, null, null);
            }
            
            static ClassMethodDefn makeAnnotated(String methodName, Scope scope, boolean isScopeExplicitlySpecified, TypeSignature typeSignature, Name.Function defaultClassMethodName, SourceRange sourceRange, SourceRange sourceRangeOfClassDefn) {
                return new ClassMethodDefn(null, methodName, scope, isScopeExplicitlySpecified, typeSignature, defaultClassMethodName, sourceRange, sourceRangeOfClassDefn);
            }

            static ClassMethodDefn makeAnnotated(CALDoc.Comment.ClassMethod caldocComment, String methodName, Scope scope, boolean isScopeExplicitlySpecified, TypeSignature typeSignature, Name.Function defaultClassMethodName, SourceRange sourceRange, SourceRange sourceRangeOfClassDefn) {
                return new ClassMethodDefn(caldocComment, methodName, scope, isScopeExplicitlySpecified, typeSignature, defaultClassMethodName, sourceRange, sourceRangeOfClassDefn);
            }
            
            // todo-jowong this should be refactored - the class method should not have knowledge about its enclosing type class definition!!
            SourceRange getSourceRangeOfClassDefn(){
                return sourceRangeOfClassDefn;
            }
            
            /**
             * @deprecated this is deprecated to signify that the returned source range is not the entire definition, but
             * rather a portion thereof.
             */
            // todo-jowong refactor this so that the primary source range of the source element is its entire source range
            @Deprecated
            SourceRange getSourceRange() {
                return getSourceRangeOfName();
            }

            SourceRange getSourceRangeOfName() {
                return super.getSourceRange();
            }

            /**
             * @return the CALDoc comment associated with this class method definition, or null if there is none.
             */
            public CALDoc.Comment.ClassMethod getCALDocComment() {
                return caldocComment;
            }
            
            public String getMethodName() {
                return methodName;
            }
            
            public Scope getScope() {
                return scope;
            }
            
            /**
             * @return whether the scope is explicitly specified in the source.
             */
            public boolean isScopeExplicitlySpecified() {
                return isScopeExplicitlySpecified;
            }
            
            public TypeSignature getTypeSignature() {
                return typeSignature;
            }
            
            /**            
             * @return the default class method name, or null if there is no default.
             */
            public Name.Function getDefaultClassMethodName() {
                return defaultClassMethodName;
            }
            
            ParseTreeNode toParseTreeNode() {
                ParseTreeNode classMethodNode = new ParseTreeNode(CALTreeParserTokenTypes.CLASS_METHOD, "CLASS_METHOD");
                ParseTreeNode optionalCALDocNode = makeOptionalCALDocNode(caldocComment);
                ParseTreeNode accessModifierNode = SourceModel.makeAccessModifierNode(getScope(), isScopeExplicitlySpecified());
                ParseTreeNode classMethodNameNode = new ParseTreeNode(CALTreeParserTokenTypes.VAR_ID, methodName);
                ParseTreeNode typeSignatureNode = typeSignature.toParseTreeNode();
                                                
                classMethodNode.setFirstChild(optionalCALDocNode);
                optionalCALDocNode.setNextSibling(accessModifierNode);
                accessModifierNode.setNextSibling(classMethodNameNode);
                classMethodNameNode.setNextSibling(typeSignatureNode);
                if (defaultClassMethodName != null) {                   
                    typeSignatureNode.setNextSibling(defaultClassMethodName.toParseTreeNode());
                } 
                                
                return classMethodNode;
            }
            
            /**
             * {@inheritDoc}
             */
            public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                return visitor.visit_TypeClassDefn_ClassMethodDefn(this, arg);
            }
        }
        
        private TypeClassDefn(CALDoc.Comment.TypeClass caldocComment, String typeClassName, Name.TypeVar typeVar, Scope scope, boolean isScopeExplicitlySpecified,
            Constraint.TypeClass[] parentClassConstraints, ClassMethodDefn[] classMethodDefns,
            SourceRange sourceRange,
            SourceRange sourceRangeOfDefn,
            boolean parenthesizeConstraints) {
            
            super(sourceRange);
            
            this.caldocComment = caldocComment;
            this.sourceRangeOfDefn = sourceRangeOfDefn;
            
            if (!LanguageInfo.isValidTypeClassName(typeClassName)) {
                throw new IllegalArgumentException();
            }
            
            if (parentClassConstraints == null || parentClassConstraints.length == 0) {
                this.parentClassConstraints = NO_CONSTRAINTS;
            } else {
                this.parentClassConstraints = parentClassConstraints.clone();
                verifyArrayArg(this.parentClassConstraints, "parentClassConstraints");                                       
            }
            
            if (classMethodDefns == null || classMethodDefns.length == 0) {
                this.classMethodDefns = NO_CLASS_METHOD_DEFNS;
            } else {
                this.classMethodDefns = classMethodDefns.clone();
                verifyArrayArg(this.classMethodDefns, "classMethodDefns");                                       
            }
            
            verifyScopeArg(scope, isScopeExplicitlySpecified, "scope");
            
            this.typeClassName = typeClassName;
            this.scope = scope;
            this.isScopeExplicitlySpecified = isScopeExplicitlySpecified;
            this.typeVar = typeVar;
            this.parenthesizeConstraints = parenthesizeConstraints;
        }                          
        
        /**
         * Create an instance of this class without an associated CALDoc comment.
         * @param typeClassName the name of the type class.
         * @param typeVar the type variable.
         * @param scope the scope of the type class.
         * @param parentClassConstraints the parent class constraints of the type class.
         * @param classMethodDefns the class methods for the type class.
         * @return a new instance of this class.
         */
        public static TypeClassDefn make(String typeClassName, Name.TypeVar typeVar, Scope scope,
            Constraint.TypeClass[] parentClassConstraints, ClassMethodDefn[] classMethodDefns) {
            
            return new TypeClassDefn(null, typeClassName, typeVar, scope, true, parentClassConstraints, classMethodDefns, null, null, false);
        }
        
        /**
         * Create an instance of this class with an associated CALDoc comment.
         * @param caldocComment the CALDoc comment.
         * @param typeClassName the name of the type class.
         * @param typeVar the type variable.
         * @param scope the scope of the type class.
         * @param isScopeExplicitlySpecified scope is explicitly specified in the source.
         * @param parentClassConstraints the parent class constraints of the type class.
         * @param classMethodDefns the class methods for the type class.
         * @return a new instance of this class.
         */
        public static TypeClassDefn make(CALDoc.Comment.TypeClass caldocComment, String typeClassName, Name.TypeVar typeVar, Scope scope, boolean isScopeExplicitlySpecified,
            Constraint.TypeClass[] parentClassConstraints, ClassMethodDefn[] classMethodDefns) {
            
            return new TypeClassDefn(caldocComment, typeClassName, typeVar, scope, isScopeExplicitlySpecified, parentClassConstraints, classMethodDefns, null, null, false);
        }
        
        static TypeClassDefn makeAnnotated(CALDoc.Comment.TypeClass caldocComment, String typeClassName, Name.TypeVar typeVar, Scope scope, boolean isScopeExplicitlySpecified,
            Constraint.TypeClass[] parentClassConstraints, ClassMethodDefn[] classMethodDefns, SourceRange sourceRange, SourceRange sourceRangeOfStatement, boolean parenthesizeConstraints) {
            
            return new TypeClassDefn(caldocComment, typeClassName, typeVar, scope, isScopeExplicitlySpecified, parentClassConstraints, classMethodDefns, sourceRange, sourceRangeOfStatement, parenthesizeConstraints);
        }

        /**
         * @return the range of the entire type def statement.
         */
        SourceRange getSourceRangeOfDefn(){
            return sourceRangeOfDefn;
        }
        
        /**
         * @deprecated this is deprecated to signify that the returned source range is not the entire definition, but
         * rather a portion thereof.
         */
        // todo-jowong refactor this so that the primary source range of the source element is its entire source range
        @Deprecated
        SourceRange getSourceRange() {
            return getSourceRangeOfName();
        }

        SourceRange getSourceRangeOfName() {
            return super.getSourceRange();
        }

        
        /**
         * @return the CALDoc comment associated with this type class definition, or null if there is none.
         */
        public CALDoc.Comment.TypeClass getCALDocComment() {
            return caldocComment;
        }
        
        public String getTypeClassName() {
            return typeClassName;
        }
        
        public Name.TypeVar getTypeVar() {
            return typeVar;
        }
        
        public Scope getScope() {
            return scope;
        }
        
        /**
         * @return whether the scope is explicitly specified in the source.
         */
        public boolean isScopeExplicitlySpecified() {
            return isScopeExplicitlySpecified;
        }
        
        public Constraint.TypeClass[] getParentClassConstraints() {
            if (parentClassConstraints.length == 0) {
                return NO_CONSTRAINTS;
            }
            
            return parentClassConstraints.clone();
        }
        
        /**
         * Get the number of parent class constraints.
         * @return the number of parent class constraints.
         */
        public int getNParentClassConstraints() {
            return parentClassConstraints.length;
        }
        
        /**
         * @return True if the constraints are parenthesized
         */
        public boolean getParenthesizeConstraints() {
            return parenthesizeConstraints;
        }
        
        /**
         * Get the nth parent class constraint.
         * @param n the index of the parent class constraint to return.
         * @return the nth parent class constraint.
         */
        public Constraint.TypeClass getNthParentClassConstraint(int n) {
            return parentClassConstraints[n];
        }

        public ClassMethodDefn[] getClassMethodDefns() {
            if (classMethodDefns.length == 0) {
                return NO_CLASS_METHOD_DEFNS;
            }
            
            return classMethodDefns.clone();
        }                 
        
        /**
         * Get the number of class method definitions.
         * @return the number of class method definitions.
         */
        public int getNClassMethodDefns() {
            return classMethodDefns.length;
        }
        
        /**
         * Get the nth class method definition.
         * @param n the index of the class method definition to return.
         * @return the nth class method definition.
         */
        public ClassMethodDefn getNthClassMethodDefn(int n) {
            return classMethodDefns[n];
        }

        ParseTreeNode toParseTreeNode() {
            ParseTreeNode typeClassDefnNode = new ParseTreeNode(CALTreeParserTokenTypes.TYPE_CLASS_DEFN, "class");
            ParseTreeNode optionalCALDocNode = makeOptionalCALDocNode(caldocComment);
            ParseTreeNode scopeNode = SourceModel.makeAccessModifierNode(getScope(), isScopeExplicitlySpecified());
            final ParseTreeNode contextListNode;
            if (parenthesizeConstraints || parentClassConstraints.length > 1) {
                contextListNode = new ParseTreeNode(CALTreeParserTokenTypes.CLASS_CONTEXT_LIST, "CLASS_CONTEXT_LIST"); 
            } else if (parentClassConstraints.length == 1) {
                contextListNode = new ParseTreeNode(CALTreeParserTokenTypes.CLASS_CONTEXT_SINGLETON, "CLASS_CONTEXT_SINGLETON");
            } else {
                contextListNode = new ParseTreeNode(CALTreeParserTokenTypes.CLASS_CONTEXT_NOTHING, "CLASS_CONTEXT_NOTHING");
            }
            ParseTreeNode typeClassNameNode = new ParseTreeNode(CALTreeParserTokenTypes.CONS_ID, typeClassName);
            ParseTreeNode typeVarNode = typeVar.toParseTreeNode();
            ParseTreeNode classMethodList = new ParseTreeNode(CALTreeParserTokenTypes.CLASS_METHOD_LIST, "CLASS_METHOD_LIST");
            
            ParseTreeNode[] contextNodes = new ParseTreeNode[parentClassConstraints.length]; 
            for (int i = 0; i < parentClassConstraints.length; i++) {
                contextNodes[i] = parentClassConstraints[i].toParseTreeNode();
            }
            contextListNode.addChildren(contextNodes);
            
            ParseTreeNode[] classMethodNodes = new ParseTreeNode[classMethodDefns.length];
            for (int i = 0; i < classMethodDefns.length; i++) {
                classMethodNodes[i] = classMethodDefns[i].toParseTreeNode();
            }
            classMethodList.addChildren(classMethodNodes);
            
            typeClassDefnNode.setFirstChild(optionalCALDocNode);
            optionalCALDocNode.setNextSibling(scopeNode);
            scopeNode.setNextSibling(contextListNode);
            contextListNode.setNextSibling(typeClassNameNode);
            typeClassNameNode.setNextSibling(typeVarNode);
            typeVarNode.setNextSibling(classMethodList);
            return typeClassDefnNode;
        }
        
        /**
         * {@inheritDoc}
         */
        public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
            return visitor.visit_TypeClassDefn(this, arg);
        }
    }
    
    /**
     * Base class for all CAL constructs that define new type constructors.
     * Currently this can be done through data declarations or through foreign data declarations.
     * 
     * @author Bo Ilic
     */
    public static abstract class TypeConstructorDefn extends TopLevelSourceElement {
        
        /** The CALDoc comment associated with this type constructor definition, or null if there is none. */
        private final CALDoc.Comment.TypeCons caldocComment;
        
        /** name of the type constructor. Some examples from the core are "Maybe", "Map" and "JList". */
        private final String typeConsName;
        
        /** the scope of the type constructor */
        private final Scope scope;
        
        /** Whether the scope is explicitly specified in the source. */
        private final boolean isScopeExplicitlySpecified;

        /** The source range of the definition body */ 
        private final SourceRange sourceRangeOfDefn;
        
        /**
         * a foreign or algebraic data type definition can have a "deriving" clause which specifies a list of
         * type classes for which an instance is automatically generated by the compiler for the type.       
         */
        private final Name.TypeClass[] derivingClauseTypeClassNames;
        
        public static final Name.TypeClass[] NO_DERIVING_CLAUSE = new Name.TypeClass[0];
        
        /**
         * Models a CAL foreign data declaration such as:
         * 
         * data foreign unsafe import jvm public "java.util.List" public JList;
         * 
         * In CAL source, the scope in front of the external name ("java.util.List") is the implementation scope while
         * the scope in from of the CAL name (JList) is the scope of the type constructor (JList).
         * 
         * @author Bo Ilic
         */
        public static final class ForeignType extends TypeConstructorDefn {
            
            /** external name for the type e.g. "java.util.List" */
            private final String externalName;
            /** The position in the source of the external name. This maybe be null. */
            private final SourceRange externalNameSourceRange;
            
            /**
             * the scope of the fact that this type is in fact a foreign type e.g. if the implementation scope is private
             * then other modules cannot declare a foreign function that has arguments of this CAL type.
             */
            private final Scope implementationScope;
            
            /** Whether the implementation scope is explicitly specified in the source. */
            private final boolean isImplementationScopeExplicitlySpecified;
            
            private ForeignType(CALDoc.Comment.TypeCons caldocComment, String typeConsName, Scope scope, boolean isScopeExplicitlySpecified, String externalName, SourceRange externalNameSourceRange, Scope implementationScope, boolean isImplementationScopeExplicitlySpecified, Name.TypeClass[] derivingClauseTypeClassNames, SourceRange sourceRange, SourceRange sourceRangeOfBody) {
                super(caldocComment, typeConsName, scope, isScopeExplicitlySpecified, derivingClauseTypeClassNames, sourceRange, sourceRangeOfBody);
                
                verifyArg(externalName, "externalName");
                verifyScopeArg(implementationScope, isImplementationScopeExplicitlySpecified, "implementationScope");
                
                this.externalName = externalName;
                this.externalNameSourceRange = externalNameSourceRange;
                this.implementationScope = implementationScope;
                this.isImplementationScopeExplicitlySpecified = isImplementationScopeExplicitlySpecified;
            }
            
            /**
             * Create an instance of this class without an associated CALDoc comment.
             * @param typeConsName the name of the type constructor.
             * @param scope the scope of the type constructor.
             * @param externalName the external name of the type constructor.
             * @param implementationScope the scope of the fact that this type is in fact a foreign type.
             * @param derivingClauseTypeClassNames the class names in the deriving clause.
             * @return a new instance of this class.
             */
            public static ForeignType make(String typeConsName, Scope scope, String externalName, Scope implementationScope, Name.TypeClass[] derivingClauseTypeClassNames) {
                return new ForeignType(null, typeConsName, scope, true, externalName, null, implementationScope, true, derivingClauseTypeClassNames, null, null);
            }
            
            /**
             * Create an instance of this class with an associated CALDoc comment.
             * @param caldocComment the CALDoc comment.
             * @param typeConsName the name of the type constructor.
             * @param scope the scope of the type constructor.
             * @param isScopeExplicitlySpecified whether the scope is explicitly specified in the source.
             * @param externalName the external name of the type constructor.
             * @param implementationScope the scope of the fact that this type is in fact a foreign type.
             * @param derivingClauseTypeClassNames the class names in the deriving clause.
             * @param isImplementationScopeExplicitlySpecified
             * @return a new instance of this class.
             */
            public static ForeignType make(CALDoc.Comment.TypeCons caldocComment, String typeConsName, Scope scope, boolean isScopeExplicitlySpecified, String externalName, Scope implementationScope, boolean isImplementationScopeExplicitlySpecified, Name.TypeClass[] derivingClauseTypeClassNames) {
                return new ForeignType(caldocComment, typeConsName, scope, isScopeExplicitlySpecified, externalName, null, implementationScope, isImplementationScopeExplicitlySpecified, derivingClauseTypeClassNames, null, null);
            }
            
            static ForeignType makeAnnotated(String typeConsName, Scope scope, boolean isScopeExplicitlySpecified, String externalName, SourceRange externalNameSourceRange, Scope implementationScope, boolean isImplementationScopeExplicitlySpecified, Name.TypeClass[] derivingClauseTypeClassNames, SourceRange sourceRange, SourceRange sourceRangeOfBody) {
                return new ForeignType(null, typeConsName, scope, isScopeExplicitlySpecified, externalName, externalNameSourceRange, implementationScope, isImplementationScopeExplicitlySpecified, derivingClauseTypeClassNames, sourceRange, sourceRangeOfBody);
            }

            static ForeignType makeAnnotated(CALDoc.Comment.TypeCons caldocComment, String typeConsName, Scope scope, boolean isScopeExplicitlySpecified, String externalName, SourceRange externalNameSourceRange, Scope implementationScope, boolean isImplementationScopeExplicitlySpecified, Name.TypeClass[] derivingClauseTypeClassNames, SourceRange sourceRange, SourceRange sourceRangeOfBody) {
                return new ForeignType(caldocComment, typeConsName, scope, isScopeExplicitlySpecified, externalName, externalNameSourceRange, implementationScope, isImplementationScopeExplicitlySpecified, derivingClauseTypeClassNames, sourceRange, sourceRangeOfBody);
            }
            
            public String getExternalName() {
                return externalName;
            }
            
            // todo-jowong maybe the name can be encapsulated by its own source element
            public SourceRange getExternalNameSourceRange() {
                return externalNameSourceRange;
            }
            
            public Scope getImplementationScope() {
                return implementationScope;
            }

            /**
             * @return whether the implementation scope is explicitly specified in the source.
             */
            public boolean isImplementationScopeExplicitlySpecified() {
                return isImplementationScopeExplicitlySpecified;
            }
            
            /**
             * @deprecated this is deprecated to signify that the returned source range is not the entire definition, but
             * rather a portion thereof.
             */
            // todo-jowong refactor this so that the primary source range of the source element is its entire source range
            @Deprecated
            SourceRange getSourceRange() {
                return getSourceRangeOfName();
            }

            SourceRange getSourceRangeOfName() {
                return super.getSourceRange();
            }

            ParseTreeNode toParseTreeNode() {
                ParseTreeNode foreignTypeDefnNode = new ParseTreeNode(CALTreeParserTokenTypes.FOREIGN_DATA_DECLARATION, "foreignData");
                ParseTreeNode optionalCALDocNode = makeOptionalCALDocNode(getCALDocComment());
                ParseTreeNode implementationScopeNode = SourceModel.makeAccessModifierNode(implementationScope, isImplementationScopeExplicitlySpecified);
                ParseTreeNode externalNameNode = new ParseTreeNode(CALTreeParserTokenTypes.STRING_LITERAL, StringEncoder.encodeString(externalName));
                ParseTreeNode accessModifierNode = SourceModel.makeAccessModifierNode(getScope(), isScopeExplicitlySpecified());
                ParseTreeNode typeNameNode = new ParseTreeNode(CALTreeParserTokenTypes.CONS_ID, getTypeConsName());
                
                foreignTypeDefnNode.setFirstChild(optionalCALDocNode);
                optionalCALDocNode.setNextSibling(implementationScopeNode);
                implementationScopeNode.setNextSibling(externalNameNode);
                externalNameNode.setNextSibling(accessModifierNode);
                accessModifierNode.setNextSibling(typeNameNode);
                typeNameNode.setNextSibling(derivingClauseToParseTreeNode());
                return foreignTypeDefnNode;
            }
            
            /**
             * Generates the required "externalName" string necessary in order to refer to this class as a foreign data type in CAL
             * @param classObject The class that is desired to be used as a foreign type
             * @return The CAL string that should be passed into the externalName argument of the ForeignType constructor
             */
            public static String makeExternalName(Class<?> classObject) {
                return classObject.getName();
            }
            
            /**
             * {@inheritDoc}
             */
            public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                return visitor.visit_TypeConstructorDefn_ForeignType(this, arg);
            }
            
        }
        
        /**
         * Models a CAL algebraic type definition i.e. a "regular" data declaration such as:
         * 
         * data public Maybe a = public Nothing | public Just a;
         * 
         * data public Map k a =
         *     private Tip |
         *     private Bin
         *         size      :: !Int
         *         key       :: !k
         *         value     :: a
         *         leftMap   :: !(Map k a)
         *         rightMap  :: !(Map k a);
         * 
         * @author Bo Ilic
         */
        public static final class AlgebraicType extends TypeConstructorDefn { 
            
            /** for example, for "data public Either a b = ..." this would be ["a", "b"]. */
            private final Name.TypeVar[] typeParameters;
            public static final Name.TypeVar[] NO_TYPE_PARAMETERS = new Name.TypeVar[0];
            
            private final DataConsDefn[] dataConstructors;
            
            /**
             * Models the part of an algebraic type definition where the data constructor is being defined. 
             * 
             * For example, the "public Nothing" and "public Just a" parts of
             * data public Maybe a = public Nothing | public Just a;    
             * 
             * For example, the "private Tip" and "private Bin !Int !k a !(Map k a) !(Map k a)"
             * parts of the type definition of the Map type:              
             * data public Map k a =
             *     private Tip |
             *     private Bin
             *         size      :: !Int
             *         key       :: !k
             *         value     :: a
             *         leftMap   :: !(Map k a)
             *         rightMap  :: !(Map k a);
             * 
             * @author Bo Ilic
             */
            public static final class DataConsDefn extends SourceElement {
                
                /** The CALDoc comment associated with this data constructor definition, or null if there is none. */
                private final CALDoc.Comment.DataCons caldocComment;
                
                private final String dataConsName;
                
                /** The scope of the definition. */
                private final Scope scope;
                
                /** Whether the scope is explicitly specified in the source. */
                private final boolean isScopeExplicitlySpecified;
                
                private final TypeArgument[] typeArgs;
                public static final TypeArgument[] NO_TYPE_ARGS = new TypeArgument[0];
                
                private final SourceRange sourceRangeOfDefn;
                
                /**
                 * Models the arguments of a data constructor, including whether they are annotated as strict.
                 * 
                 * For example, Tip has 0 type arguments.
                 * Bin has 5 type arguments: "size :: !Int", "key :: !k", "value :: a", "leftMap :: !(Map k a)", "rightMap :: !(Map k a)" 
                 *   of which all but "a" are strict. 
                 * 
                 * data public Map k a =
                 *     private Tip |
                 *     private Bin
                 *         size      :: !Int
                 *         key       :: !k
                 *         value     :: a
                 *         leftMap   :: !(Map k a)
                 *         rightMap  :: !(Map k a);
                 * 
                 * @author Bo Ilic
                 */
                public static final class TypeArgument extends SourceElement {
                    
                    private final Name.Field fieldName;
                    private final TypeExprDefn typeExprDefn;
                    private final boolean isStrict;
                    
                    private TypeArgument(Name.Field fieldName, TypeExprDefn typeExprDefn, boolean isStrict, SourceRange range) {
                        super(range);
                        verifyArg(typeExprDefn, "typeExprDefn");
                        verifyArg(fieldName, "fieldName");
                        
                        this.fieldName = fieldName;
                        this.typeExprDefn = typeExprDefn;
                        this.isStrict = isStrict;
                    }
                    
                    public static TypeArgument make(Name.Field fieldName, TypeExprDefn typeExprDefn, boolean isStrict) {
                        return new TypeArgument(fieldName, typeExprDefn, isStrict, null);
                    }

                    static TypeArgument makeAnnotated(Name.Field fieldName, TypeExprDefn typeExprDefn, boolean isStrict, SourceRange range) {
                        return new TypeArgument(fieldName, typeExprDefn, isStrict, range);
                    }
           
                    public Name.Field getFieldName() {
                        return fieldName;
                    }
                    
                    public TypeExprDefn getTypeExprDefn() {
                        return typeExprDefn;
                    }
                    
                    public boolean isStrict() {
                        return isStrict;
                    }
                    
                    ParseTreeNode toParseTreeNode() {
                        ParseTreeNode typeExprNode = typeExprDefn.toParseTreeNode();
                        
                        ParseTreeNode maybePlingTypeExprNode;
                        if (isStrict) {
                            maybePlingTypeExprNode = new ParseTreeNode(CALTreeParserTokenTypes.STRICT_ARG, "!");
                            maybePlingTypeExprNode.setFirstChild(typeExprNode);
                        } else {
                            maybePlingTypeExprNode = typeExprNode;
                        }
                        
                        ParseTreeNode typeArgumentNode = new ParseTreeNode(CALTreeParserTokenTypes.DATA_CONSTRUCTOR_NAMED_ARG, "::");
                        
                        ParseTreeNode dcArgNameNode = fieldName.toParseTreeNode(); 
                        typeArgumentNode.setFirstChild(dcArgNameNode);
                        
                        dcArgNameNode.setNextSibling(maybePlingTypeExprNode);
                        
                        return typeArgumentNode;
                    }
                    
                    /**
                     * {@inheritDoc}
                     */
                    public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                        return visitor.visit_TypeConstructorDefn_AlgebraicType_DataConsDefn_TypeArgument(this, arg);
                    }
                }
                
                private DataConsDefn(CALDoc.Comment.DataCons caldocComment, String dataConsName, Scope scope, boolean isScopeExplicitlySpecified, TypeArgument[] typeArgs, SourceRange sourceRange, SourceRange sourceRangeOfDefn) {
                    
                    super(sourceRange);

                    this.caldocComment = caldocComment;
                    this.sourceRangeOfDefn = sourceRangeOfDefn;
                    
                    if (!LanguageInfo.isValidDataConstructorName(dataConsName)) {
                        throw new IllegalArgumentException();
                    }
                    
                    if (typeArgs == null || typeArgs.length == 0) {
                        this.typeArgs = NO_TYPE_ARGS;
                    } else {
                        this.typeArgs = typeArgs.clone();
                        verifyArrayArg(this.typeArgs, "typeArgs");                                       
                    }
                    
                    verifyScopeArg(scope, isScopeExplicitlySpecified, "scope");
                    
                    this.dataConsName = dataConsName;
                    this.scope = scope;
                    this.isScopeExplicitlySpecified = isScopeExplicitlySpecified;
                }                 
                
                /**
                 * Create an instance of this class without an associated CALDoc comment.
                 * @param dataConsName the name of the data constructor.
                 * @param scope the scope of the data constructor.
                 * @param typeArgs the type arguments of the data constructor.
                 * @return a new instance of this class.
                 */
                public static DataConsDefn make(String dataConsName, Scope scope, TypeArgument[] typeArgs) {
                    return new DataConsDefn(null, dataConsName, scope, true, typeArgs, null, null);
                }
                
                /**
                 * Create an instance of this class with an associated CALDoc comment.
                 * @param caldocComment the CALDoc comment.
                 * @param dataConsName the name of the data constructor.
                 * @param scope the scope of the data constructor.
                 * @param isScopeExplicitlySpecified whether the scope is explicitly specified in the source.
                 * @param typeArgs the type arguments of the data constructor.
                 * @return a new instance of this class.
                 */
                public static DataConsDefn make(CALDoc.Comment.DataCons caldocComment, String dataConsName, Scope scope, boolean isScopeExplicitlySpecified, TypeArgument[] typeArgs) {
                    return new DataConsDefn(caldocComment, dataConsName, scope, isScopeExplicitlySpecified, typeArgs, null, null);
                }
                
                static DataConsDefn makeAnnotated(String dataConsName, Scope scope, boolean isScopeExplicitlySpecified, TypeArgument[] typeArgs, SourceRange sourceRange, SourceRange sourceRangeOfDefn) {
                    return new DataConsDefn(null, dataConsName, scope, isScopeExplicitlySpecified, typeArgs, sourceRange, sourceRangeOfDefn);
                }
                
                static DataConsDefn makeAnnotated(CALDoc.Comment.DataCons caldocComment, String dataConsName, Scope scope, boolean isScopeExplicitlySpecified, TypeArgument[] typeArgs, SourceRange sourceRange, SourceRange sourceRangeOfDefn) {
                    return new DataConsDefn(caldocComment, dataConsName, scope, isScopeExplicitlySpecified, typeArgs, sourceRange, sourceRangeOfDefn);
                }

                /**
                 * @return get the source range of the definition.
                 */
                SourceRange getSourceRangeOfDefn(){
                    return sourceRangeOfDefn;
                }
                

                /**
                 * @return the CALDoc comment associated with this data constructor definition, or null if there is none.
                 */
                public CALDoc.Comment.DataCons getCALDocComment() {
                    return caldocComment;
                }
                
                public String getDataConsName() {
                    return dataConsName;
                }
                
                public Scope getScope() {
                    return scope;
                }
                
                /**
                 * @return whether the scope is explicitly specified in the source.
                 */
                public boolean isScopeExplicitlySpecified() {
                    return isScopeExplicitlySpecified;
                }
                
                public TypeArgument[] getTypeArgs() {
                    if (typeArgs.length == 0) {
                        return NO_TYPE_ARGS;
                    }
                    
                    return typeArgs.clone();
                }
                
                /**
                 * Get the number of type arguments.
                 * @return the number of type arguments.
                 */
                public int getNTypeArgs() {
                    return typeArgs.length;
                }
                
                /**
                 * Get the nth type argument.
                 * @param n the index of the type argument to return.
                 * @return the nth type argument.
                 */
                public TypeArgument getNthTypeArg(int n) {
                    return typeArgs[n];
                }

                /**
                 * @deprecated this is deprecated to signify that the returned source range is not the entire definition, but
                 * rather a portion thereof.
                 */
                // todo-jowong refactor this so that the primary source range of the source element is its entire source range
                @Deprecated
                SourceRange getSourceRange() {
                    return getSourceRangeOfName();
                }

                SourceRange getSourceRangeOfName() {
                    return super.getSourceRange();
                }

                ParseTreeNode toParseTreeNode() {
                    ParseTreeNode dataConsDefnNode = new ParseTreeNode(CALTreeParserTokenTypes.DATA_CONSTRUCTOR_DEFN, "DATA_CONSTRUCTOR_DEFN");
                    ParseTreeNode optionalCALDocNode = makeOptionalCALDocNode(caldocComment);
                    ParseTreeNode scopeNode = SourceModel.makeAccessModifierNode(getScope(), isScopeExplicitlySpecified());
                    ParseTreeNode dataConsNameNode = new ParseTreeNode(CALTreeParserTokenTypes.CONS_ID, dataConsName);
                    ParseTreeNode dataConsArgListNode = new ParseTreeNode(CALTreeParserTokenTypes.DATA_CONSTRUCTOR_ARG_LIST, "DATA_CONSTRUCTOR_ARG_LIST");
                    
                    ParseTreeNode[] dataConsArgNodes = new ParseTreeNode[typeArgs.length];
                    for (int i = 0; i < typeArgs.length; i++) {
                        dataConsArgNodes[i] = typeArgs[i].toParseTreeNode();
                    }
                    dataConsArgListNode.addChildren(dataConsArgNodes);
                    
                    dataConsDefnNode.setFirstChild(optionalCALDocNode);
                    optionalCALDocNode.setNextSibling(scopeNode);
                    scopeNode.setNextSibling(dataConsNameNode);
                    dataConsNameNode.setNextSibling(dataConsArgListNode);                     
                    return dataConsDefnNode;
                }
                
                /**
                 * {@inheritDoc}
                 */
                public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                    return visitor.visit_TypeConstructorDefn_AlgebraicType_DataConsDefn(this, arg);
                }
            }
            
            private AlgebraicType(CALDoc.Comment.TypeCons caldocComment, String typeConsName, Scope scope, boolean isScopeExplicitlySpecified, Name.TypeVar[] typeParameters, DataConsDefn[] dataConstructors, Name.TypeClass[] derivingClauseTypeClassNames, SourceRange sourceRange, SourceRange sourceRangeOfBody) {
                super(caldocComment, typeConsName, scope, isScopeExplicitlySpecified, derivingClauseTypeClassNames, sourceRange, sourceRangeOfBody);
                
                if (typeParameters == null || typeParameters.length == 0) {
                    this.typeParameters = NO_TYPE_PARAMETERS;
                } else {
                    this.typeParameters = typeParameters.clone();
                    verifyArrayArg(this.typeParameters, "typeParameters");                                       
                }
                
                if (dataConstructors == null || dataConstructors.length < 1) {
                    throw new IllegalArgumentException("An algebraic type definition must define at least 1 data constructor.");
                }
                this.dataConstructors = dataConstructors.clone();            
                verifyArrayArg(dataConstructors, "dataConstructors");
                
            }
            
            /**
             * Create an instance of this class without an associated CALDoc comment.
             * @param typeConsName the name of the type constructor.
             * @param scope the scope of the type constructor.
             * @param typeParameters the type parameters of the type constructor.
             * @param dataConstructors the data constructor definitions.
             * @param derivingClauseTypeClassNames the class names in the deriving clause.
             * @return a new isntance of this class.
             */
            public static AlgebraicType make(String typeConsName, Scope scope, Name.TypeVar[] typeParameters, DataConsDefn[] dataConstructors, Name.TypeClass[] derivingClauseTypeClassNames) {
                return new AlgebraicType(null, typeConsName, scope, true, typeParameters, dataConstructors, derivingClauseTypeClassNames, null, null);
            }
            
            /**
             * Create an instance of this class with an associated CALDoc comment.
             * @param caldocComment the CALDoc comment.
             * @param typeConsName the name of the type constructor.
             * @param scope the scope of the type constructor.
             * @param isScopeExplicitlySpecified whether the scope is explicitly specified in the source.
             * @param typeParameters the type parameters of the type constructor.
             * @param dataConstructors the data constructor definitions.
             * @param derivingClauseTypeClassNames the class names in the deriving clause.
             * @return a new instance of this class.
             */
            public static AlgebraicType make(CALDoc.Comment.TypeCons caldocComment, String typeConsName, Scope scope, boolean isScopeExplicitlySpecified, Name.TypeVar[] typeParameters, DataConsDefn[] dataConstructors, Name.TypeClass[] derivingClauseTypeClassNames) {
                return new AlgebraicType(caldocComment, typeConsName, scope, isScopeExplicitlySpecified, typeParameters, dataConstructors, derivingClauseTypeClassNames, null, null);
            }
            
            static AlgebraicType makeAnnotated(String typeConsName, Scope scope, boolean isScopeExplicitlySpecified, Name.TypeVar[] typeParameters, DataConsDefn[] dataConstructors, Name.TypeClass[] derivingClauseTypeClassNames, SourceRange sourceRange, SourceRange sourceRangeOfBody) {
                return new AlgebraicType(null, typeConsName, scope, isScopeExplicitlySpecified, typeParameters, dataConstructors, derivingClauseTypeClassNames, sourceRange, sourceRangeOfBody);
            }

            static AlgebraicType makeAnnotated(CALDoc.Comment.TypeCons caldocComment, String typeConsName, Scope scope, boolean isScopeExplicitlySpecified, Name.TypeVar[] typeParameters, DataConsDefn[] dataConstructors, Name.TypeClass[] derivingClauseTypeClassNames, SourceRange sourceRange, SourceRange sourceRangeOfBody) {
                return new AlgebraicType(caldocComment, typeConsName, scope, isScopeExplicitlySpecified, typeParameters, dataConstructors, derivingClauseTypeClassNames, sourceRange, sourceRangeOfBody);
            }

            public Name.TypeVar[] getTypeParameters() {
                if (typeParameters.length == 0) {
                    return NO_TYPE_PARAMETERS;
                }
                
                return typeParameters.clone();
            }
            
            /**
             * Get the number of type parameters.
             * @return the number of type parameters.
             */
            public int getNTypeParameters() {
                return typeParameters.length;
            }
            
            /**
             * Get the nth type parameter.
             * @param n the index of the type parameter to return.
             * @return the nth type parameter.
             */
            public Name.TypeVar getNthTypeParameter(int n) {
                return typeParameters[n];
            }

            public DataConsDefn[] getDataConstructors() {
                return dataConstructors.clone();
            }
            
            /**
             * Get the number of data constructors.
             * @return the number of data constructors.
             */
            public int getNDataConstructors() {
                return dataConstructors.length;
            }
            
            /**
             * Get the nth data constructor.
             * @param n the index of the data constructor to return.
             * @return the nth data constructor.
             */
            public DataConsDefn getNthDataConstructor(int n) {
                return dataConstructors[n];
            }


            
            /**
             * @deprecated this is deprecated to signify that the returned source range is not the entire definition, but
             * rather a portion thereof.
             */
            // todo-jowong refactor this so that the primary source range of the source element is its entire source range
            @Deprecated
            SourceRange getSourceRange() {
                return getSourceRangeOfName();
            }

            SourceRange getSourceRangeOfName() {
                return super.getSourceRange();
            }

            ParseTreeNode toParseTreeNode() {
                ParseTreeNode algebraicTypeDefnNode = new ParseTreeNode(CALTreeParserTokenTypes.DATA_DECLARATION, "data");
                ParseTreeNode optionalCALDocNode = makeOptionalCALDocNode(getCALDocComment());
                ParseTreeNode accessModifierNode = SourceModel.makeAccessModifierNode(getScope(), isScopeExplicitlySpecified());
                ParseTreeNode typeNameNode = new ParseTreeNode(CALTreeParserTokenTypes.CONS_ID, getTypeConsName());
                ParseTreeNode typeParamListNode = new ParseTreeNode(CALTreeParserTokenTypes.TYPE_CONS_PARAM_LIST, "TYPE_CONS_PARAM_LIST");
                ParseTreeNode dataConsDefnListNode = new ParseTreeNode(CALTreeParserTokenTypes.DATA_CONSTRUCTOR_DEFN_LIST, "DATA_CONSTRUCTOR_DEFN_LIST");
                
                ParseTreeNode[] typeVarNodes = new ParseTreeNode[typeParameters.length];
                for (int i = 0; i < typeParameters.length; i++) {
                    typeVarNodes[i] = typeParameters[i].toParseTreeNode();
                }
                typeParamListNode.addChildren(typeVarNodes);
                
                ParseTreeNode[] dataConsDefnNodes = new ParseTreeNode[dataConstructors.length];
                for (int i = 0; i < dataConstructors.length; i++) {
                    dataConsDefnNodes[i] = dataConstructors[i].toParseTreeNode();
                }
                dataConsDefnListNode.addChildren(dataConsDefnNodes);
                
                algebraicTypeDefnNode.setFirstChild(optionalCALDocNode);
                optionalCALDocNode.setNextSibling(accessModifierNode);
                accessModifierNode.setNextSibling(typeNameNode);
                typeNameNode.setNextSibling(typeParamListNode);
                typeParamListNode.setNextSibling(dataConsDefnListNode);
                
                dataConsDefnListNode.setNextSibling(derivingClauseToParseTreeNode());
                                                
                return algebraicTypeDefnNode;
            }
            
            /**
             * {@inheritDoc}
             */
            public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                return visitor.visit_TypeConstructorDefn_AlgebraicType(this, arg);
            }
        }
        
        private TypeConstructorDefn(CALDoc.Comment.TypeCons caldocComment, String typeConsName, Scope scope, boolean isScopeExplicitlySpecified, Name.TypeClass[] derivingClauseTypeClassNames, SourceRange sourceRange, SourceRange sourceRangeOfDefn) {
            
            super(sourceRange);

            this.caldocComment = caldocComment;
            this.sourceRangeOfDefn = sourceRangeOfDefn;
            
            if (!LanguageInfo.isValidTypeConstructorName(typeConsName)) {
                throw new IllegalArgumentException();
            }
            
            verifyScopeArg(scope, isScopeExplicitlySpecified, "scope");
            
            if (derivingClauseTypeClassNames == null || derivingClauseTypeClassNames.length == 0) {
                this.derivingClauseTypeClassNames = NO_DERIVING_CLAUSE;
            } else {
                this.derivingClauseTypeClassNames = derivingClauseTypeClassNames.clone();
                verifyArrayArg(this.derivingClauseTypeClassNames, "derivingClauseTypeClassNames");
            }
            
            this.typeConsName = typeConsName;
            this.scope = scope;
            this.isScopeExplicitlySpecified = isScopeExplicitlySpecified;
        } 

        /**
         * @return An optional SourceRange specifying the range occupied by the body
         *          element in the original source text.  May return null.
         */
        final SourceRange getSourceRangeOfDefn() {
            return sourceRangeOfDefn;
        }
        
        /**
         * @return the CALDoc comment associated with this type constructor definition, or null if there is none.
         */
        public CALDoc.Comment.TypeCons getCALDocComment() {
            return caldocComment;
        }
        
        public String getTypeConsName() {
            return typeConsName;
        }
        
        // todo-jowong maybe the name can be encapsulated by its own source element
        abstract SourceRange getSourceRangeOfName();
        
        public Scope getScope() {
            return scope;
        }
        
        /**
         * @return whether the scope is explicitly specified in the source.
         */
        public boolean isScopeExplicitlySpecified() {
            return isScopeExplicitlySpecified;
        }
        
        public int getNDerivingClauseTypeClassNames() {
            return derivingClauseTypeClassNames.length;
        }
        
        public Name.TypeClass getDerivingClauseTypeClassName(int n) {
            return derivingClauseTypeClassNames[n];
        }
        
        public Name.TypeClass[] getDerivingClauseTypeClassNames() {
            if (derivingClauseTypeClassNames.length == 0) {
                return NO_DERIVING_CLAUSE;
            }
            
            return derivingClauseTypeClassNames.clone();
        }
        
        
        ParseTreeNode derivingClauseToParseTreeNode() {
            
            int nDerivingClauseTypeClassNames = getNDerivingClauseTypeClassNames();
            if (nDerivingClauseTypeClassNames == 0) {
                return null;
            }
                       
            ParseTreeNode derivingClauseNode = new ParseTreeNode(CALTreeParserTokenTypes.LITERAL_deriving, "deriving");
            ParseTreeNode[] derivingClauseTypeClassNames = new ParseTreeNode[nDerivingClauseTypeClassNames];
            for (int i = 0; i < nDerivingClauseTypeClassNames; ++i) {
                derivingClauseTypeClassNames[i] = getDerivingClauseTypeClassName(i).toParseTreeNode();
            }
            derivingClauseNode.addChildren(derivingClauseTypeClassNames); 
            
            return derivingClauseNode;                    
        }
    }     
    
    /**
     * Models a class instance definition in CAL.
     * This includes constrained instances.
     * 
     * An example of a instance declaration:
     * instance Eq Integer where
     *     equals = equalsInteger;
     *     notEquals = notEqualsInteger;
     *     ;
     * This can be read as saying "the type Integer is a member of the set of types Eq"
     * 
     * An example of a constrained instance declaration:
     * instance Eq a => Eq (Maybe a) where
     *     equals = equalsMaybe;
     *     notEquals = notEqualsMaybe;
     *     ;
     * This can be read as saying "the type 'Maybe a' is a member of the set of types Eq provided that
     *  'a' is a member of the set of types Eq".
     *          
     * @author Bo Ilic
     */
    public static final class InstanceDefn extends TopLevelSourceElement {                        
        
        /** The CALDoc comment associated with this instance definition, or null if there is none. */
        private final CALDoc.Comment.Instance caldocComment;
        
        private final Name.TypeClass typeClassName;
        
        private final InstanceTypeCons instanceTypeCons;
        
        /** used for defining constrained instances such as "instance Eq a => Eq [a] where ..." or "instance (Ord a, Ord b) => Ord (a, b) where ..." */
        private final Constraint.TypeClass[] constraints;
        
        /** used to indicate if the constraints should be parenthesized*/
        private final boolean parenthesizeConstraints;
        
        public static final Constraint.TypeClass[] NO_CONSTRAINTS = new Constraint.TypeClass[0];
        
        private final InstanceMethod[] instanceMethods;
        public static final InstanceMethod[] NO_INSTANCE_METHODS = new InstanceMethod[0];
        
        /**
         * An instance declaration must name a type constructor that it is being applied to, along with
         * its type variables. These type variables then may be constrained by the constraints. In the
         * instance "instance Eq Integer where ..." the instance type cons is "Integer". In the instance
         * "instance Eq a => Eq (Maybe a) where ..." the instance type cons is "Maybe a".         
         * 
         * @author Bo Ilic
         */
        public static abstract class InstanceTypeCons extends SourceElement {
            
            public static final class TypeCons extends InstanceTypeCons {
                
                private final Name.TypeCons typeConsName;
                private final Name.TypeVar[] typeVars;
                public static final Name.TypeVar[] NO_TYPE_VARS = new Name.TypeVar[0];
                private final SourceRange sourceRangeOfDefn;
                
                /** This is true if the typecons is explicitly parenthesized*/
                private final boolean parenthesised;
                
                private TypeCons(Name.TypeCons typeConsName, Name.TypeVar[] typeVars, SourceRange sourceRange, SourceRange sourceRangeOfDefn, boolean parenthesized) {

                    super(sourceRange);
                    this.sourceRangeOfDefn = sourceRangeOfDefn;
                    verifyArg(typeConsName, "typeConsName");
                    
                    this.typeConsName = typeConsName;
                    
                    this.parenthesised = parenthesized;
                    
                    if (typeVars == null || typeVars.length == 0) {
                        this.typeVars = NO_TYPE_VARS;
                    } else {
                        this.typeVars = typeVars.clone();
                        verifyArrayArg(this.typeVars, "typeVars");                                       
                    }
                }
                
                public static TypeCons make(Name.TypeCons typeConsName, Name.TypeVar[] typeVars) {
                    return new TypeCons(typeConsName, typeVars, null, null, false);
                }
                
                static TypeCons makeAnnotated(Name.TypeCons typeConsName, Name.TypeVar[] typeVars, SourceRange sourceRange, SourceRange sourceRangeOfDefn, boolean parenthesized) {
                    return new TypeCons(typeConsName, typeVars, sourceRange, sourceRangeOfDefn, parenthesized);
                }
                
                /** @return true if the type constructor is explicitly parenthesized*/
                boolean getParenthesized() {
                    return parenthesised;
                }

                /**
                 * @deprecated this is deprecated to signify that the returned source range is not the entire definition, but
                 * rather a portion thereof.
                 */
                // todo-jowong refactor this so that the primary source range of the source element is its entire source range
                @Deprecated
                SourceRange getSourceRange() {
                    return getSourceRangeOfName();
                }

                SourceRange getSourceRangeOfName() {
                    return super.getSourceRange();
                }

                ParseTreeNode toParseTreeNode() {
                    ParseTreeNode instanceTypeConsNode;
                    if (!parenthesised && typeVars.length == 0) {
                        instanceTypeConsNode = new ParseTreeNode(CALTreeParserTokenTypes.UNPARENTHESIZED_TYPE_CONSTRUCTOR, "UNPARENTHESIZED_TYPE_CONSTRUCTOR");
                    } else {
                        instanceTypeConsNode = new ParseTreeNode(CALTreeParserTokenTypes.GENERAL_TYPE_CONSTRUCTOR, "GENERAL_TYPE_CONSTRUCTOR");
                    }
                    
                    ParseTreeNode typeConsNameNode = typeConsName.toParseTreeNode();
                    
                    ParseTreeNode[] typeVarNodes = new ParseTreeNode[typeVars.length];
                    for (int i = 0; i < typeVars.length; i++) {
                        typeVarNodes[i] = typeVars[i].toParseTreeNode();
                    }
                    
                    instanceTypeConsNode.setFirstChild(typeConsNameNode);
                    instanceTypeConsNode.addChildren(typeVarNodes);
                    return instanceTypeConsNode;                     
                }
                
                /**
                 * {@inheritDoc}
                 */
                public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                    return visitor.visit_InstanceDefn_InstanceTypeCons_TypeCons(this, arg);
                }
                
                /**
                 * @return the type constructor name
                 */
                public Name.TypeCons getTypeConsName() {
                    return typeConsName;
                }
                
                SourceRange getSourceRangeOfDefn(){
                    return sourceRangeOfDefn;
                }
                
                /**
                 * @return the type variables
                 */
                public Name.TypeVar[] getTypeVars() {
                    if (typeVars.length == 0) {
                        return NO_TYPE_VARS;
                    }
                    
                    return typeVars.clone();
                }
            }
            
            public static final class Function extends InstanceTypeCons {
                
                private final Name.TypeVar domainTypeVar;
                private final Name.TypeVar codomainTypeVar;
                private final SourceRange operatorSourceRange;
                
                private Function(Name.TypeVar domainTypeVar, Name.TypeVar codomainTypeVar, SourceRange sourceRange, SourceRange operatorSourceRange) {

                    super(sourceRange);
                    this.domainTypeVar = domainTypeVar;
                    this.codomainTypeVar = codomainTypeVar;
                    this.operatorSourceRange = operatorSourceRange;
                }                 
                
                public static Function make(Name.TypeVar domainTypeVar, Name.TypeVar codomainTypeVar) {
                    return new Function(domainTypeVar, codomainTypeVar, null, null);
                }
                
                static Function makeAnnotated(Name.TypeVar domainTypeVar, Name.TypeVar codomainTypeVar, SourceRange sourceRange, SourceRange operatorSourceRange) {
                    return new Function(domainTypeVar, codomainTypeVar, sourceRange, operatorSourceRange);
                }
                
                // todo-jowong maybe the operator can be encapsulated by its own source element
                SourceRange getOperatorSourceRange() {
                    return operatorSourceRange;
                }
                                
                ParseTreeNode toParseTreeNode() {
                    ParseTreeNode instanceTypeConsNode = new ParseTreeNode(CALTreeParserTokenTypes.FUNCTION_TYPE_CONSTRUCTOR, "->");
                    ParseTreeNode domainTypeVarNode = domainTypeVar.toParseTreeNode();
                    ParseTreeNode codomainTypeVarNode = codomainTypeVar.toParseTreeNode();
                    
                    instanceTypeConsNode.setFirstChild(domainTypeVarNode);
                    domainTypeVarNode.setNextSibling(codomainTypeVarNode);
                    return instanceTypeConsNode;                     
                }
                
                /**
                 * {@inheritDoc}
                 */
                public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                    return visitor.visit_InstanceDefn_InstanceTypeCons_Function(this, arg);
                }
                
                /**
                 * @return the domain type variable
                 */
                public Name.TypeVar getDomainTypeVar() {
                    return domainTypeVar;
                }
                
                /**
                 * @return the codomain type variable
                 */
                public Name.TypeVar getCodomainTypeVar() {
                    return codomainTypeVar;
                }
            }
            
            public static final class Unit extends InstanceTypeCons {
                
                private static final Unit UNIT = new Unit(null); 
                
                private Unit (SourceRange sourceRange) {
                    super(sourceRange);
                }
                
                public static Unit make() {
                    return UNIT;
                }
                
                static Unit makeAnnotated(SourceRange sourceRange) {
                    return new Unit(sourceRange);
                }
                
                ParseTreeNode toParseTreeNode() {
                    ParseTreeNode instanceTypeConsNode = new ParseTreeNode(CALTreeParserTokenTypes.UNIT_TYPE_CONSTRUCTOR, "Unit");                     
                    return instanceTypeConsNode;                     
                }
                
                /**
                 * {@inheritDoc}
                 */
                public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                    return visitor.visit_InstanceDefn_InstanceTypeCons_Unit(this, arg);
                }            
            }                                          
            
            public static final class List extends InstanceTypeCons {
                
                private final Name.TypeVar elemTypeVar;
                
                private List(Name.TypeVar elemTypeVar, SourceRange sourceRange) {
                    
                    super(sourceRange);
                    this.elemTypeVar = elemTypeVar;                     
                }                 
                
                public static List make(Name.TypeVar elemTypeVar) {
                    return new List(elemTypeVar, null);
                }
                
                static List makeAnnotated(Name.TypeVar elemTypeVar, SourceRange sourceRange) {
                    return new List(elemTypeVar, sourceRange);
                }
                
                ParseTreeNode toParseTreeNode() {
                    ParseTreeNode instanceTypeConsNode = new ParseTreeNode(CALTreeParserTokenTypes.LIST_TYPE_CONSTRUCTOR, CAL_Prelude.TypeConstructors.List.getUnqualifiedName());
                    ParseTreeNode elemTypeVarNode = elemTypeVar.toParseTreeNode();                     
                    
                    instanceTypeConsNode.setFirstChild(elemTypeVarNode);
                    return instanceTypeConsNode;                     
                }
                
                /**
                 * {@inheritDoc}
                 */
                public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                    return visitor.visit_InstanceDefn_InstanceTypeCons_List(this, arg);
                }
                
                /**
                 * @return the element's type variable
                 */
                public Name.TypeVar getElemTypeVar() {
                    return elemTypeVar;
                }
            }
            
            public static final class Record extends InstanceTypeCons {
                
                private final Name.TypeVar elemTypeVar;
                private final SourceRange sourceRangeOfDefn;
                
                private Record(Name.TypeVar elemTypeVar, SourceRange sourceRange, SourceRange sourceRangeOfDefn) {
                    
                    super(sourceRange);
                    this.sourceRangeOfDefn = sourceRangeOfDefn;
                    this.elemTypeVar = elemTypeVar;                     
                }                 
                
                public static Record make(Name.TypeVar elemTypeVar) {
                    return new Record(elemTypeVar, null, null);
                }
                
                static Record makeAnnotated(Name.TypeVar elemTypeVar, SourceRange sourceRange, SourceRange sourceRangeOfDefn) {
                    return new Record(elemTypeVar, sourceRange, sourceRangeOfDefn);
                }
                
                SourceRange getSourceRangeOfDefn(){
                    return sourceRangeOfDefn;
                }
                
                ParseTreeNode toParseTreeNode() {
                    ParseTreeNode instanceTypeConsNode = new ParseTreeNode(CALTreeParserTokenTypes.RECORD_TYPE_CONSTRUCTOR, "Record");
                    ParseTreeNode elemTypeVarNode = elemTypeVar.toParseTreeNode();                     
                    
                    instanceTypeConsNode.setFirstChild(elemTypeVarNode);
                    return instanceTypeConsNode;                     
                }
                
                /**
                 * {@inheritDoc}
                 */
                public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                    return visitor.visit_InstanceDefn_InstanceTypeCons_Record(this, arg);
                }
                
                /**
                 * @return the element's type variable
                 */
                public Name.TypeVar getElemTypeVar() {
                    return elemTypeVar;
                }
            }                
            
            private InstanceTypeCons(SourceRange sourceRange) {
                super(sourceRange);
            }
        }
        
        /**
         * Associates a class method to a particular resolving function for the type that the
         * instance is an instance for.
         * 
         * For example, in the Eq Int instance in the Prelude, one of the instance methods is
         * "equals = equalsInt;"
         * 
         * @author Bo Ilic
         */
        public static final class InstanceMethod extends SourceElement {
            
            /** The CALDoc comment associated with this instance method definition, or null if there is none. */
            private final CALDoc.Comment.InstanceMethod caldocComment;
            
            /**
             * name of the instance method. This belongs to the same module as the module of the
             * instance type constructor of the enclosing instance definition.
             */
            private final String classMethodName;
            
            private final SourceRange classMethodNameSourceRange;
            
            private final Name.Function resolvingFunctionName;
            
            private InstanceMethod(CALDoc.Comment.InstanceMethod caldocComment, String classMethodName, Name.Function resolvingFunctionName, SourceRange sourceRange, SourceRange classMethodNameSourceRange) {
                
                super(sourceRange);
                this.caldocComment = caldocComment;
                
                if (!LanguageInfo.isValidClassMethodName(classMethodName)) {
                    throw new IllegalArgumentException();
                }
                verifyArg(resolvingFunctionName, "resolvingFunctionName");
                
                this.classMethodName = classMethodName;
                this.classMethodNameSourceRange = classMethodNameSourceRange;
                this.resolvingFunctionName = resolvingFunctionName;
            }             
            
            /**
             * Create an instance of this class without an associated CALDoc comment.
             * @param classMethodName the name of the class method.
             * @param resolvingFunctionName the name of the resolving function.
             * @return a new instance of this class.
             */
            public static InstanceMethod make(String classMethodName, Name.Function resolvingFunctionName) {
                return new InstanceMethod(null, classMethodName, resolvingFunctionName, null, null);
            }
            
            /**
             * Create an instance of this class with an associated CALDoc comment.
             * @param caldocComment the CALDoc comment.
             * @param classMethodName the name of the class method.
             * @param resolvingFunctionName the name of the resolving function.
             * @return a new instance of this class.
             */
            public static InstanceMethod make(CALDoc.Comment.InstanceMethod caldocComment, String classMethodName, Name.Function resolvingFunctionName) {
                return new InstanceMethod(caldocComment, classMethodName, resolvingFunctionName, null, null);
            }
            
            static InstanceMethod makeAnnotated(String classMethodName, Name.Function resolvingFunctionName, SourceRange sourceRange, SourceRange classMethodNameSourceRange) {
                return new InstanceMethod(null, classMethodName, resolvingFunctionName, sourceRange, classMethodNameSourceRange);
            }
            
            static InstanceMethod makeAnnotated(CALDoc.Comment.InstanceMethod caldocComment, String classMethodName, Name.Function resolvingFunctionName, SourceRange sourceRange, SourceRange classMethodNameSourceRange) {
                return new InstanceMethod(caldocComment, classMethodName, resolvingFunctionName, sourceRange, classMethodNameSourceRange);
            }
            
           
            ParseTreeNode toParseTreeNode() {
                ParseTreeNode instanceMethodNode = new ParseTreeNode(CALTreeParserTokenTypes.INSTANCE_METHOD, "INSTANCE_METHOD");
                ParseTreeNode optionalCALDocNode = makeOptionalCALDocNode(caldocComment);
                ParseTreeNode instanceMethodNameNode = new ParseTreeNode(CALTreeParserTokenTypes.VAR_ID, classMethodName);
                ParseTreeNode instanceMethodDefnNode = resolvingFunctionName.toParseTreeNode();
                
                instanceMethodNode.setFirstChild(optionalCALDocNode);
                optionalCALDocNode.setNextSibling(instanceMethodNameNode);
                instanceMethodNameNode.setNextSibling(instanceMethodDefnNode);
                return instanceMethodNode;
            }
            
            /**
             * {@inheritDoc}
             */
            public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                return visitor.visit_InstanceDefn_InstanceMethod(this, arg);
            }
            
            /**
             * @return the CALDoc comment associated with this instance method definition, or null if there is none.
             */
            public CALDoc.Comment.InstanceMethod getCALDocComment() {
                return caldocComment;
            }
            
            /**
             * @return the resolving function name
             */
            public Name.Function getResolvingFunctionName() {
                return resolvingFunctionName;
            }
            
            /**
             * @return the class method name
             */
            public String getClassMethodName() {
                return classMethodName;
            }
            
            /**
             * @deprecated this is deprecated to signify that the returned source range is not the entire definition, but
             * rather a portion thereof.
             */
            // todo-jowong refactor this so that the primary source range of the source element is its entire source range
            @Deprecated
            SourceRange getSourceRange() {
                return getSourceRangeOfResolvingFunctionName();
            }

            SourceRange getSourceRangeOfResolvingFunctionName() {
                return super.getSourceRange();
            }

            /**
             * @return SourceRange of the class method name (may be null)
             */
            // todo-jowong maybe the name can be encapsulated by its own source element
            SourceRange getClassMethodNameSourceRange() {
                return classMethodNameSourceRange;
            }
        }
        
        private InstanceDefn(CALDoc.Comment.Instance caldocComment, Name.TypeClass typeClassName, InstanceTypeCons instanceTypeCons, 
            Constraint.TypeClass[] constraints, InstanceMethod[] instanceMethods, SourceRange sourceRange, boolean parenthesizeConstraints) {

            super(sourceRange);
            
            this.caldocComment = caldocComment;
            
            verifyArg(typeClassName, "typeClassName");
            verifyArg(instanceTypeCons, "instanceTypeCons");
            
            if (constraints == null || constraints.length == 0) {
                this.constraints = NO_CONSTRAINTS;
            } else {
                this.constraints = constraints.clone();
                verifyArrayArg(this.constraints, "constraints");                                       
            }
            
            if (instanceMethods == null || instanceMethods.length == 0) {
                this.instanceMethods = NO_INSTANCE_METHODS;
            } else {
                this.instanceMethods = instanceMethods.clone();
                verifyArrayArg(this.instanceMethods, "instanceMethods");                                       
            }
            
            
            this.typeClassName = typeClassName;
            this.instanceTypeCons = instanceTypeCons;
            this.parenthesizeConstraints = parenthesizeConstraints;
        }                          
        
        /**
         * Create an instance of this class without an associated CALDoc comment.
         * @param typeClassName the name of the type class.
         * @param instanceTypeCons the instance type.
         * @param constraints the constraints on the instance.
         * @param instanceMethods the instance methods associated with the instance.
         * @return a new instance of this class.
         */
        public static InstanceDefn make(Name.TypeClass typeClassName, InstanceTypeCons instanceTypeCons,
            Constraint.TypeClass[] constraints, InstanceMethod[] instanceMethods) {
            
            return new InstanceDefn(null, typeClassName, instanceTypeCons, constraints, instanceMethods, null, false);
        }
                
        /**
         * Create an instance of this class with an associated CALDoc comment.
         * @param caldocComment the CALDoc comment.
         * @param typeClassName the name of the type class.
         * @param instanceTypeCons the instance type.
         * @param constraints the constraints on the instance.
         * @param instanceMethods the instance methods associated with the instance.
         * @return a new instance of this class.
         */
        public static InstanceDefn make(CALDoc.Comment.Instance caldocComment, Name.TypeClass typeClassName, InstanceTypeCons instanceTypeCons,
            Constraint.TypeClass[] constraints, InstanceMethod[] instanceMethods) {
            
            return new InstanceDefn(caldocComment, typeClassName, instanceTypeCons, constraints, instanceMethods, null, false);
        }
                
        static InstanceDefn makeAnnotated(Name.TypeClass typeClassName, InstanceTypeCons instanceTypeCons,
            Constraint.TypeClass[] constraints, InstanceMethod[] instanceMethods, SourceRange sourceRange) {
            
            return new InstanceDefn(null, typeClassName, instanceTypeCons, constraints, instanceMethods, sourceRange, false);
        }
                
        static InstanceDefn makeAnnotated(CALDoc.Comment.Instance caldocComment, Name.TypeClass typeClassName, InstanceTypeCons instanceTypeCons,
            Constraint.TypeClass[] constraints, InstanceMethod[] instanceMethods, SourceRange sourceRange) {
            
            return new InstanceDefn(caldocComment, typeClassName, instanceTypeCons, constraints, instanceMethods, sourceRange, false);
        }
        
        static InstanceDefn makeAnnotated(CALDoc.Comment.Instance caldocComment, Name.TypeClass typeClassName, InstanceTypeCons instanceTypeCons,
            Constraint.TypeClass[] constraints, InstanceMethod[] instanceMethods, SourceRange sourceRange, boolean parenthesizeConstraints) {
            
            return new InstanceDefn(caldocComment, typeClassName, instanceTypeCons, constraints, instanceMethods, sourceRange, parenthesizeConstraints);
        }
        
        ParseTreeNode toParseTreeNode() {
            ParseTreeNode instanceDefnNode = new ParseTreeNode(CALTreeParserTokenTypes.INSTANCE_DEFN, "instance");
            ParseTreeNode optionalCALDocNode = makeOptionalCALDocNode(caldocComment);
            ParseTreeNode instanceNameNode = new ParseTreeNode(CALTreeParserTokenTypes.INSTANCE_NAME, "INSTANCE_NAME");
            ParseTreeNode instanceMethodListNode = new ParseTreeNode(CALTreeParserTokenTypes.INSTANCE_METHOD_LIST, "INSTANCE_METHOD_LIST");
            
            //ParseTreeNode contextListNode = new ParseTreeNode(CALTreeParserTokenTypes.CLASS_CONTEXT_LIST, "CLASS_CONTEXT_LIST");
            final ParseTreeNode contextListNode;
            if (parenthesizeConstraints || constraints.length > 1) {
                contextListNode = new ParseTreeNode(CALTreeParserTokenTypes.CLASS_CONTEXT_LIST, "CLASS_CONTEXT_LIST"); 
            } else if (constraints.length == 1) {
                contextListNode = new ParseTreeNode(CALTreeParserTokenTypes.CLASS_CONTEXT_SINGLETON, "CLASS_CONTEXT_SINGLETON");
            } else {
                contextListNode = new ParseTreeNode(CALTreeParserTokenTypes.CLASS_CONTEXT_NOTHING, "CLASS_CONTEXT_NOTHING");
            }
            
            ParseTreeNode typeClassNameNode = typeClassName.toParseTreeNode();
            ParseTreeNode instanceTypeConsNode = instanceTypeCons.toParseTreeNode();
            
            ParseTreeNode[] contextNodes = new ParseTreeNode[constraints.length];
            for (int i = 0; i < constraints.length; i++) {
                contextNodes[i] = constraints[i].toParseTreeNode();
            }
            contextListNode.addChildren(contextNodes);
            
            instanceNameNode.setFirstChild(contextListNode);
            contextListNode.setNextSibling(typeClassNameNode);
            typeClassNameNode.setNextSibling(instanceTypeConsNode);
            
            ParseTreeNode[] instanceMethodNodes = new ParseTreeNode[instanceMethods.length];
            for (int i = 0; i < instanceMethods.length; i++) {
                instanceMethodNodes[i] = instanceMethods[i].toParseTreeNode();
            }
            instanceMethodListNode.addChildren(instanceMethodNodes);
            
            instanceDefnNode.setFirstChild(optionalCALDocNode);
            optionalCALDocNode.setNextSibling(instanceNameNode);
            instanceNameNode.setNextSibling(instanceMethodListNode);
            return instanceDefnNode;
        }
        
        /**
         * {@inheritDoc}
         */
        public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
            return visitor.visit_InstanceDefn(this, arg);
        }        
        
        // todo-jowong maybe the name can be encapsulated by its own source element
        SourceRange getSourceRangeOfName(){
            if (typeClassName.getSourceRange() == null || instanceTypeCons.getSourceRangeOfDefn() ==null) {
                return null;
            }
            
            SourcePosition start = this.typeClassName.getSourceRange().getStartSourcePosition();
            SourcePosition end = this.instanceTypeCons.getSourceRangeOfDefn().getEndSourcePosition();
            return new SourceRange(start, end);
        }
        /**
         * @return the constraints
         */
        public Constraint.TypeClass[] getConstraints() {
            if (constraints.length == 0) {
                return NO_CONSTRAINTS;
            }
            
            return constraints.clone();
        }
        
        /**
         * Get the number of constraints.
         * @return the number of constraints.
         */
        public int getNConstraints() {
            return constraints.length;
        }
        
        /**
         * @return True if the constraints are parenthesized
         */
        public boolean getParenthesizeConstraints() {
            return parenthesizeConstraints;
        }
        
        /**
         * Get the nth constraint.
         * @param n the index of the constraint to return.
         * @return the nth constraint.
         */
        public Constraint.TypeClass getNthConstraint(int n) {
            return constraints[n];
        }
        
        /**
         * @return the instance methods
         */
        public InstanceMethod[] getInstanceMethods() {
            if (instanceMethods.length == 0) {
                return NO_INSTANCE_METHODS;
            }
            
            return instanceMethods.clone();
        }
        
        /**
         * Get the number of instance methods.
         * @return the number of instance methods.
         */
        public int getNInstanceMethods() {
            return instanceMethods.length;
        }
        
        /**
         * Get the nth instance method.
         * @param n the index of the instance method to return.
         * @return the nth instance method.
         */
        public InstanceMethod getNthInstanceMethod(int n) {
            return instanceMethods[n];
        }

        /**
         * @return the instance type constructor
         */
        public InstanceTypeCons getInstanceTypeCons() {
            return instanceTypeCons;
        }
        
        /**
         * @return the CALDoc comment associated with this instance definition, or null if there is none.
         */
        public CALDoc.Comment.Instance getCALDocComment() {
            return caldocComment;
        }
        
        /**
         * @return the type class name
         */
        public Name.TypeClass getTypeClassName() {
            return typeClassName;
        }
    }
    
    /**
     * Models a type declaration for a top-level (algebraic) function.
     * @author Bo Ilic
     */
    public static final class FunctionTypeDeclaration extends TopLevelSourceElement {
        
        /** The CALDoc comment associated with this function type declaration, or null if there is none. */
        private final CALDoc.Comment.Function caldocComment;
        
        private final String functionName;
        private final TypeSignature typeSignature;
        
        private final SourceRange sourceRangeOfDefn;
        
        private FunctionTypeDeclaration(CALDoc.Comment.Function caldocComment, String functionName, TypeSignature typeSignature, SourceRange sourceRange, SourceRange sourceRangeOfDefn) {
            
            super(sourceRange);

            this.caldocComment = caldocComment;
            this.sourceRangeOfDefn = sourceRangeOfDefn;
            
            verifyArg(functionName, "functionName");
            verifyArg(typeSignature, "typeSignature");
            
            this.functionName = functionName;
            this.typeSignature = typeSignature;
        }        
        
        /**
         * Create an instance of this class without an associated CALDoc comment.
         * @param functionName the name of the function.
         * @param typeSignature the declared type signature of the function.
         * @return a new instance of this class.
         */
        public static FunctionTypeDeclaration make(String functionName, TypeSignature typeSignature) {
            return new FunctionTypeDeclaration(null, functionName, typeSignature, null, null);
        }
        
        /**
         * Create an instance of this class with an associated CALDoc comment.
         * @param caldocComment the CALDoc comment.
         * @param functionName the name of the function.
         * @param typeSignature the declared type signature of the function.
         * @return a new instance of this class.
         */
        public static FunctionTypeDeclaration make(CALDoc.Comment.Function caldocComment, String functionName, TypeSignature typeSignature) {
            return new FunctionTypeDeclaration(caldocComment, functionName, typeSignature, null, null);
        }
        
        static FunctionTypeDeclaration makeAnnotated(String functionName, TypeSignature typeSignature, SourceRange sourceRange, SourceRange sourceRangeOfDeclaration) {
            return new FunctionTypeDeclaration(null, functionName, typeSignature, sourceRange, sourceRangeOfDeclaration);
        }
        
        static FunctionTypeDeclaration makeAnnotated(CALDoc.Comment.Function caldocComment, String functionName, TypeSignature typeSignature, SourceRange sourceRange, SourceRange sourceRangeOfDeclaration) {
            return new FunctionTypeDeclaration(caldocComment, functionName, typeSignature, sourceRange, sourceRangeOfDeclaration);
        }
        

        
        /**
         * @return the CALDoc comment associated with this function definition, or null if there is none.
         */
        public CALDoc.Comment.Function getCALDocComment() {
            return caldocComment;
        }
        
        public String getFunctionName() {
            return functionName;
        }
        
        public TypeSignature getTypeSignature() {
            return typeSignature;
        }
        
        SourceRange getSourceRangeOfDefn(){
            return sourceRangeOfDefn;
        }
        
        /**
         * @deprecated this is deprecated to signify that the returned source range is not the entire definition, but
         * rather a portion thereof.
         */
        // todo-jowong refactor this so that the primary source range of the source element is its entire source range
        @Deprecated
        SourceRange getSourceRange() {
            return getSourceRangeOfName();
        }

        SourceRange getSourceRangeOfName() {
            return super.getSourceRange();
        }

        ParseTreeNode toParseTreeNode() {
            ParseTreeNode topLevelTypeDeclarationNode = new ParseTreeNode(CALTreeParserTokenTypes.TOP_LEVEL_TYPE_DECLARATION, "TOP_LEVEL_TYPE_DECLARATION");
            ParseTreeNode optionalCALDocNode = makeOptionalCALDocNode(caldocComment);
            ParseTreeNode typeDeclarationNode = new ParseTreeNode(CALTreeParserTokenTypes.TYPE_DECLARATION, "::");
            ParseTreeNode functionNameNode = new ParseTreeNode(CALTreeParserTokenTypes.VAR_ID, functionName);
            ParseTreeNode declaredTypeNode = typeSignature.toParseTreeNode();
            
            topLevelTypeDeclarationNode.setFirstChild(optionalCALDocNode);
            optionalCALDocNode.setNextSibling(typeDeclarationNode);
            typeDeclarationNode.setFirstChild(functionNameNode);
            functionNameNode.setNextSibling(declaredTypeNode);
            return topLevelTypeDeclarationNode;
        }
        
        /**
         * {@inheritDoc}
         */
        public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
            return visitor.visit_FunctionTypeDeclaraction(this, arg);
        }
    } 
    
    /**
     * Models a constraint on a type variable. Currently there are type class constraints such as "Eq a" and
     * lacks constraints such as "r\orderDate".
     * 
     * Constraints are used in the context part of a type declaration, and in type class and class instance definitions.
     * 
     * @author Bo Ilic
     */
    public static abstract class Constraint extends SourceElement {
        
        private final Name.TypeVar typeVarName;
        
        /**
         * Models the constraint that a type variable belongs to a particular type class.
         * For example,
         * Prelude.Ord a
         * Eq b
         * 
         * @author Bo Ilic
         */
        public static final class TypeClass extends Constraint {
            
            private final Name.TypeClass typeClassName;             
            
            private TypeClass(Name.TypeClass typeClassName, Name.TypeVar typeVarName, SourceRange sourceRange) {
                super(typeVarName, sourceRange);
                
                verifyArg(typeClassName, "typeClassName");                                         
                this.typeClassName = typeClassName;
            }
            
            public static TypeClass make(Name.TypeClass typeClassName, Name.TypeVar typeVarName) {
                return new TypeClass(typeClassName, typeVarName, null);
            }
            
            static TypeClass makeAnnotated(Name.TypeClass typeClassName, Name.TypeVar typeVarName, SourceRange sourceRange) {
                return new TypeClass(typeClassName, typeVarName, sourceRange);
            }
            
            public Name.TypeClass getTypeClassName() {
                return typeClassName;
            }
            
            ParseTreeNode toParseTreeNode() {
                ParseTreeNode contextNode = new ParseTreeNode(CALTreeParserTokenTypes.CLASS_CONTEXT, "CLASS_CONTEXT");
                ParseTreeNode typeClassNameNode = typeClassName.toParseTreeNode();
                ParseTreeNode varNameNode = getTypeVarName().toParseTreeNode();
                
                contextNode.setFirstChild(typeClassNameNode);
                typeClassNameNode.setNextSibling(varNameNode);
                return contextNode;
            }
            
            /**
             * {@inheritDoc}
             */
            public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                return visitor.visit_Constraint_TypeClass(this, arg);
            }
        }
        
        /**
         * Models the constraint that a record variable lacks a specific field e.g
         * r\orderDate
         * s\#4
         * 
         * @author Bo Ilic
         */
        public static final class Lacks extends Constraint {
            
            private final Name.Field lacksField;
            
            private Lacks(Name.TypeVar typeVarName, Name.Field lacksField, SourceRange sourceRange) {
                super (typeVarName, sourceRange);
                
                verifyArg(lacksField, "lacksField");                                          
                this.lacksField = lacksField;                     
            }
            
            public static Lacks make(Name.TypeVar typeVarName, Name.Field lacksField) {
                return new Lacks(typeVarName, lacksField, null);
            }
            
            static Lacks makeAnnotated(Name.TypeVar typeVarName, Name.Field lacksField, SourceRange sourceRange) {
                return new Lacks(typeVarName, lacksField, sourceRange);
            }
            
            public Name.Field getLacksField() {
                return lacksField;
            }             
            
            ParseTreeNode toParseTreeNode() {
                ParseTreeNode contextNode = new ParseTreeNode(CALTreeParserTokenTypes.LACKS_FIELD_CONTEXT, "LACKS_FIELD_CONTEXT");
                ParseTreeNode recordVarNameNode = getTypeVarName().toParseTreeNode();
                ParseTreeNode lacksFieldNameNode = lacksField.toParseTreeNode();
                
                contextNode.setFirstChild(recordVarNameNode);
                recordVarNameNode.setNextSibling(lacksFieldNameNode);
                return contextNode;
            }
            
            /**
             * {@inheritDoc}
             */
            public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                return visitor.visit_Constraint_Lacks(this, arg);
            }
        }
        
        private Constraint(Name.TypeVar typeVarName, SourceRange sourceRange) {
            super(sourceRange);
            
            this.typeVarName = typeVarName;
        }
        
        public Name.TypeVar getTypeVarName() {
            return typeVarName;
        }
    }
    
    /**
     * Models a name in CAL source.     
     * 
     * @author Bo Ilic
     */
    public static abstract class Name extends SourceElement {
        
        /**
         * Models a module name in CAL source. Note that a module name may contain zero
         * or more dots, and whitespace (including comments) can appear on either side
         * of these dots. For example, it is possible for a module name to span multiple lines.
         *
         * @author Joseph Wong
         */
        public static final class Module extends Name {
            
            /**
             * The qualifier portion of the module name. This cannot be null, but
             * can represent an empty qualifier if it has zero components.
             */
            private final Qualifier qualifier;
            
            /**
             * The unqualified portion of the module name - the component after the last dot.
             */
            private final String unqualifiedModuleName;
            
            /**
             * Models a module name qualifier. A module name may have an empty qualifier
             * (e.g. the module name Gamma), or it may have a non-empty qualifier
             * (e.g. the qualifier Beta in the module name Beta.Gamma, or the
             * qualifier Alpha.Beta in the module name Alpha.Beta.Gamma).
             *
             * @author Joseph Wong
             */
            public static final class Qualifier extends SourceElement {
                
                /**
                 * The components of the qualifier which are lexically separated by dots.
                 * This array can be empty to represent an empty qualifier.
                 */
                private final String[] components;
                public static final String[] NO_COMPONENTS = new String[0];
                
                /**
                 * Creates a source model element for representing a module name qualifier.
                 * @param components the components of the qualifier which are lexically separated by dots.
                 * @param sourceRange the source range which encompasses the entire qualifier.
                 */
                private Qualifier(String[] components, SourceRange sourceRange) {
                    
                    super(sourceRange);
                    
                    if (components == null || components.length == 0) {
                        this.components = NO_COMPONENTS;
                    } else {
                        this.components = components.clone();
                        verifyArrayArg(this.components, "components");
                    }
                    
                    for (int i = 0, n = this.components.length; i < n; i++) {
                        if (!LanguageInfo.isValidModuleNameComponent(this.components[i])) {
                            throw new IllegalArgumentException();
                        }
                    }
                }
                
                /**
                 * Factory method for constructing a source model element for representing a module name qualifier.
                 * 
                 * @param qualifier
                 *            the qualifier as a string, with the components separated by dots (no whitespace allowed
                 *            on either side of the dots).
                 * @return a new instance of this class.
                 */
                public static Qualifier make(String qualifier) {
                    return new Qualifier(qualifier.split("\\."), null);
                }
                
                /**
                 * Factory method for constructing a source model element for representing a module name qualifier.
                 * @param components the components of the qualifier which are lexically separated by dots.
                 * @return a new instance of this class.
                 */
                public static Qualifier make(String[] components) {
                    return new Qualifier(components, null);
                }
                
                /**
                 * Factory method for constructing a source model element for representing a module name qualifier from
                 * the module name qualifier of a {@link ModuleName}.
                 * @param moduleName the module name whose qualifier is to be represented.
                 * @return a new instance of this class.
                 */
                public static Qualifier makeFromQualifierOfModuleName(ModuleName moduleName) {
                    return new Qualifier(moduleName.getComponents(0, moduleName.getNComponents() - 1), null);
                }
                
                /**
                 * Factory method for constructing a source model element for representing a module name qualifier.
                 * @param components the components of the qualifier which are lexically separated by dots.
                 * @param sourceRange the source range which encompasses the entire qualifier.
                 * @return a new instance of this class.
                 */
                static Qualifier makeAnnotated(String[] components, SourceRange sourceRange) {
                    return new Qualifier(components, sourceRange);
                }
                
                /**
                 * @return the components of the qualifier which are lexically separated by dots.
                 *         This array can be empty to represent an empty qualifier.
                 */
                public String[] getComponents() {
                    if (components.length == 0) {
                        return NO_COMPONENTS;
                    }
                    
                    return components.clone();
                }
                
                /**
                 * @return the number of components in the qualifier.
                 */
                public int getNComponents() {
                    return components.length;
                }
                
                /**
                 * Returns the component at the specified position in the array
                 * of components.
                 * 
                 * @param n the index of the component to return.
                 * @return the component at the specified position in the array
                 *         of components.
                 */
                public String getNthComponents(int n) {
                    return components[n];
                }
                
                
                /**
                 * {@inheritDoc}
                 */
                ParseTreeNode toParseTreeNode() {
                    ParseTreeNode hierarchicalModuleNameNode = new ParseTreeNode(CALTreeParserTokenTypes.HIERARCHICAL_MODULE_NAME_EMPTY_QUALIFIER, "HIERARCHICAL_MODULE_NAME_EMPTY_QUALIFIER");
                    
                    for (final String component : components) {
                        ParseTreeNode qualifierNode = hierarchicalModuleNameNode;
                        hierarchicalModuleNameNode = new ParseTreeNode(CALTreeParserTokenTypes.HIERARCHICAL_MODULE_NAME, "HIERARCHICAL_MODULE_NAME");
                        ParseTreeNode unqualifiedModuleNameNode = new ParseTreeNode(CALTreeParserTokenTypes.CONS_ID, component);
                        
                        hierarchicalModuleNameNode.setFirstChild(qualifierNode);
                        qualifierNode.setNextSibling(unqualifiedModuleNameNode);
                    }
                    
                    return hierarchicalModuleNameNode;
                }

                /**
                 * {@inheritDoc}
                 */
                public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                    return visitor.visit_Name_Module_Qualifier(this, arg);
                }
            }
            
            /**
             * Creates a source model element for representing a module name.
             * 
             * @param qualifier
             *            the qualifier portion of the module name. This cannot be null, but can represent
             *            an empty qualifier if it has zero components.
             * @param unqualifiedModuleName
             *            the unqualified portion of the module name - the component after the last dot.
             * @param sourceRange
             *            the source range which encompasses the entire module name including the qualifier.
             */
            private Module(Qualifier qualifier, String unqualifiedModuleName, SourceRange sourceRange) {
                
                super(sourceRange);
                
                verifyArg(qualifier, "qualifier");
                verifyArg(unqualifiedModuleName, "unqualifiedName");
                
                if (!LanguageInfo.isValidModuleNameComponent(unqualifiedModuleName)) {
                    throw new IllegalArgumentException();
                }
                
                this.qualifier = qualifier;
                this.unqualifiedModuleName = unqualifiedModuleName;
            }
            
            /**
             * Factory method for constructing a source model element for representing a module name.
             * 
             * @param qualifiedModuleName
             *            the qualified module name as a ModuleName, with the components separated by dots.
             * @return a new instance of this class.
             */
            public static Module make(ModuleName qualifiedModuleName) {
                if (qualifiedModuleName == null) {
                    return null;
                }
                
                return new Module(
                    Qualifier.makeFromQualifierOfModuleName(qualifiedModuleName),
                    qualifiedModuleName.getLastComponent(),
                    null);
            }
            
            /**
             * Factory method for constructing a source model element for representing a module name.
             * 
             * @param qualifier
             *            the qualifier portion of the module name. This cannot be null, but can represent
             *            an empty qualifier if it has zero components.
             * @param unqualifiedModuleName
             *            the unqualified portion of the module name - the component after the last dot.
             * @return a new instance of this class.
             */
            public static Module make(Qualifier qualifier, String unqualifiedModuleName) {
                return new Module(qualifier, unqualifiedModuleName, null);
            }
            
            /**
             * Factory method for constructing a source model element for representing a module name.
             * 
             * @param qualifier
             *            the qualifier portion of the module name. This cannot be null, but can represent
             *            an empty qualifier if it has zero components.
             * @param unqualifiedModuleName
             *            the unqualified portion of the module name - the component after the last dot.
             * @param sourceRange
             *            the source range which encompasses the entire module name including the qualifier.
             * @return a new instance of this class.
             */
            static Module makeAnnotated(Qualifier qualifier, String unqualifiedModuleName, SourceRange sourceRange) {
                return new Module(qualifier, unqualifiedModuleName, sourceRange);
            }
            
            /**
             * Factory method for constructing an instance of ModuleName.
             * @param module a Name.Module. Cannot be null.
             * @return an instance of ModuleName.
             */
            public static ModuleName toModuleName(Module module) {
                String[] moduleQualifierComponents = module.qualifier.components;
                
                String[] components = new String[moduleQualifierComponents.length + 1];
                System.arraycopy(moduleQualifierComponents, 0, components, 0, moduleQualifierComponents.length);
                components[moduleQualifierComponents.length] = module.unqualifiedModuleName;
                
                return ModuleName.make(components);
            }

            /**
             * Factory method for constructing an instance of ModuleName which accepts nulls.
             * @param maybeModuleName a module name. <em>Can</em> be null.
             * @return an instance of ModuleName. If moduleName is null, then null is returned.
             */
            public static ModuleName maybeToModuleName(final Module maybeModuleName) {
                if (maybeModuleName == null) {
                    return null;
                } else {
                    return toModuleName(maybeModuleName);
                }
            }

            /**
             * @return the qualifier portion of the module name. This cannot be null, but
             *         can represent an empty qualifier if it has zero components.
             */
            public Qualifier getQualifier() {
                return qualifier;
            }
            
            /**
             * @return true if the qualifier of this module name is non-empty (i.e. the module name contains
             *         dots, such as Foo.Bar); false otherwise.
             */
            public boolean isQualified() {
                return qualifier.getNComponents() > 0;
            }
            
            /**
             * @return the unqualified portion of the module name - the component after the last dot.
             */
            public String getUnqualifiedModuleName() {
                return unqualifiedModuleName;
            }
            

            /**
             * {@inheritDoc}
             */
            ParseTreeNode toParseTreeNode() {
                ParseTreeNode hierarchicalModuleNameNode = new ParseTreeNode(CALTreeParserTokenTypes.HIERARCHICAL_MODULE_NAME, "HIERARCHICAL_MODULE_NAME");
                ParseTreeNode qualifierNode = qualifier.toParseTreeNode();
                ParseTreeNode unqualifiedModuleNameNode = new ParseTreeNode(CALTreeParserTokenTypes.CONS_ID, unqualifiedModuleName);
                
                hierarchicalModuleNameNode.setFirstChild(qualifierNode);
                qualifierNode.setNextSibling(unqualifiedModuleNameNode);
                return hierarchicalModuleNameNode;
            }

            /**
             * {@inheritDoc}
             */
            public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                return visitor.visit_Name_Module(this, arg);
            }
        }
        
        /**
         * Models a potentially qualified name in CAL source. What this means is that it is a name where an explicit
         * module qualification is able to be given, but may or may not have actually been given.
         * <p>
         * In general in CAL, user can only provide a qualification if and only if it is possible for the symbol to be defined in another module.
         * That is why for a function definition such as:
         * <pre>
         * f x y = x + y;
         * </pre>
         * it is not legal to supply qualifications to any of the 3 symbols on the left hand side "f", "x", "y", but it is legal
         * to qualify the x or y on the right hand side.
         * Note that <code>f x y = M.x + M.y;</code>
         * the x and y on the right hand side do not refer to the local arguments x and y but rather to the top-level symbols M.x and M.y.     
         * 
         * @author Bo Ilic
         */
        public static abstract class Qualifiable extends Name {
            
            /**          
             * Name of the module in which the name is defined. May be null, in which case the name is unqualified.
             * For efficiency, it is better to supply the explicit qualification if available. 
             * Local names must have null module part.
             * If non-null, it must be a syntactically valid module name.
             */
            private final Name.Module moduleName;
            
            private Qualifiable(Name.Module moduleName, SourceRange sourceRange) {
                super(sourceRange);
                
                this.moduleName = moduleName;
            }
            
            /**
             * @return the module name, or null if the name is unqualified.
             */
            public Name.Module getModuleName() {
                return moduleName;
            }
            
            /**
             * @return true if this name is qualified (i.e. it has a module name); false otherwise. 
             */
            public boolean isQualified() {
                return moduleName != null;
            }
            
            /**         
             * @return the part of the name not including the module qualification. For example, for "List.filter" this is "filter".
             */
            public abstract String getUnqualifiedName();
            

            
            /**
             * Constructs a parse tree for a potentially qualified name in CAL source.
             * @param qualifiedNameNodeType the node type for the qualified name.
             * @param qualifiedNameNodeTypeString the string representation of the node type for the qualified name.
             * @param unqualifiedNameNodeType the node type for the unqualified name.
             * @param unqualifiedName the unqualified name as a string.
             * @return the corresponding parse tree.
             */
            final ParseTreeNode toParseTreeNode(int qualifiedNameNodeType, String qualifiedNameNodeTypeString, int unqualifiedNameNodeType, String unqualifiedName) {
                ParseTreeNode qualifiedNode = new ParseTreeNode(qualifiedNameNodeType, qualifiedNameNodeTypeString);
                ParseTreeNode moduleNameNode;
                ParseTreeNode functionNameNode = new ParseTreeNode(unqualifiedNameNodeType, unqualifiedName);
                
                if (moduleName == null) {
                    moduleNameNode = new ParseTreeNode(CALTreeParserTokenTypes.HIERARCHICAL_MODULE_NAME_EMPTY_QUALIFIER, "HIERARCHICAL_MODULE_NAME_EMPTY_QUALIFIER");
                } else {
                    moduleNameNode = moduleName.toParseTreeNode();
                }
                
                qualifiedNode.setFirstChild(moduleNameNode);
                moduleNameNode.setNextSibling(functionNameNode);
                return qualifiedNode;
            }
        }
        
        /**
         * Models a potentially qualified function name in CAL source. It also is used for modeling class method and
         * local variable names when they can be potentially qualified (i.e. for any symbol that shares the namespace
         * of CAL functions).
         * 
         * @author Bo Ilic
         */
        public static final class Function extends Qualifiable {
            
            private final String functionName;
            
            /** SourceRange of the unqualifiedName portion */
            private final SourceRange unqualifiedNameSourceRange;
            
            /**                  
             * @param moduleName may be null, in which case Var is unqualified. For efficiency, it is better to supply the
             *     explicit qualification if available. Local names must have null module part. If non-null, it must be a syntactically valid module name.
             * @param functionName must be a non-null syntactically valid function, class method or local variable name, or an internal function name.
             * @param internalName whether the name is one for an internal function.
             * @param sourceRange
             * @param unqualifiedNameSourceRange
             */
            private Function(Name.Module moduleName, String functionName, boolean internalName, SourceRange sourceRange, SourceRange unqualifiedNameSourceRange) {
                super(moduleName, sourceRange);
                
                //internal names are validated differently. This allows internal functions such as the instance
                //functions for derived instances to use user-hidden names starting with a $ such as $equalsMaybe
                if (internalName) {
                    if (functionName.charAt(0) != '$') {
                        throw new IllegalArgumentException("internal function names must start with a $.");
                    }
                } else {
                    if (!LanguageInfo.isValidFunctionName(functionName)) {
                        throw new IllegalArgumentException("Invalid function name: " + functionName);
                    }
                }                      
                
                this.functionName = functionName;
                this.unqualifiedNameSourceRange = unqualifiedNameSourceRange;
            }
            
            /**                  
             * @param moduleName may be null, in which case Var is unqualified. For efficiency, it is better to supply the
             *     explicit qualification if available. Local names must have null module part. If non-null, it must be a syntactically valid module name.
             * @param functionName must be a non-null syntactically valid function, class method or local variable name, or an internal function name.
             * @param internalName whether the name is one for an internal function.
             * @param sourceRange
             * @param unqualifiedNameSourceRange
             */
            private Function(ModuleName moduleName, String functionName, boolean internalName, SourceRange sourceRange, SourceRange unqualifiedNameSourceRange) {
                this(Name.Module.make(moduleName), functionName, internalName, sourceRange, unqualifiedNameSourceRange);
            }
            
            private Function(QualifiedName functionName, SourceRange sourceRange, SourceRange unqualifiedNameSourceRange) {
                this(functionName.getModuleName(), functionName.getUnqualifiedName(), false, sourceRange, unqualifiedNameSourceRange);
            }
            
            /**                  
             * @param moduleName may be null, in which case Var is unqualified. For efficiency, it is better to supply the
             *     explicit qualification if available. Local names must have null module part. If non-null, it must be a syntactically valid module name.
             * @param functionName must be a non-null syntactically valid function, class method or local variable name.
             * @return Function
             */
            public static Function make(Name.Module moduleName, String functionName) {
                return new Function(moduleName, functionName, false, null, null);
            }
            
            /**                  
             * @param moduleName may be null, in which case Var is unqualified. For efficiency, it is better to supply the
             *     explicit qualification if available. Local names must have null module part. If non-null, it must be a syntactically valid module name.
             * @param functionName must be a non-null syntactically valid function, class method or local variable name.
             * @return Function
             */
            public static Function make(ModuleName moduleName, String functionName) {
                return new Function(moduleName, functionName, false, null, null);
            }
            
            public static Function make(QualifiedName functionName) {
                return new Function(functionName, null, null);
            }
            
            /**
             * @param unqualifiedFunctionName the unqualified name for a function or class method.
             * @return an unqualified function or class method name.
             */
            public static Function makeUnqualified(String unqualifiedFunctionName) {
                return make((Name.Module)null, unqualifiedFunctionName);
            }
            
            static Function makeAnnotated(Name.Module moduleName, String functionName, SourceRange sourceRange, SourceRange unqualifiedNameSourceRange) {
                return new Function(moduleName, functionName, false, sourceRange, unqualifiedNameSourceRange);
            }
            
            public String getUnqualifiedName() {
                return functionName;
            }        
            
            // todo-jowong maybe the unqualified name can be encapsulated by its own source element
            SourceRange getUnqualifiedNameSourceRange() {
                return unqualifiedNameSourceRange;
            }
            
            ParseTreeNode toParseTreeNode() {
                return toParseTreeNode(CALTreeParserTokenTypes.QUALIFIED_VAR, "QUALIFIED_VAR", CALTreeParserTokenTypes.VAR_ID, functionName);
            }
            
            /**
             * {@inheritDoc}
             */
            public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                return visitor.visit_Name_Function(this, arg);
            }
        }
        
        /**
         * Models a potentially qualified data constructor name in CAL source.
         * @author Bo Ilic
         */
        public static final class DataCons extends Qualifiable {
            
            private final String dataConsName;
            
            /**                  
             * @param moduleName may be null, in which case Cons is unqualified. For efficiency, it is better to supply the
             *     explicit qualification if available. If non-null, it must be a syntactically valid module name.
             * @param dataConsName must be a non-null syntactically valid data constructor name.
             * @param sourceRange may be null.
             */
            private DataCons(Name.Module moduleName, String dataConsName, SourceRange sourceRange) {                     
                super(moduleName, sourceRange);
                
                if (!LanguageInfo.isValidDataConstructorName(dataConsName)) {
                    throw new IllegalArgumentException();
                }               
                
                this.dataConsName = dataConsName;
            }
            
            /**                  
             * @param moduleName may be null, in which case Cons is unqualified. For efficiency, it is better to supply the
             *     explicit qualification if available. If non-null, it must be a syntactically valid module name.
             * @param dataConsName must be a non-null syntactically valid data constructor name.
             * @param sourceRange may be null.
             */
            private DataCons(ModuleName moduleName, String dataConsName, SourceRange sourceRange) {                     
                this(Name.Module.make(moduleName), dataConsName, sourceRange);
            }
            
            private DataCons(QualifiedName dataConsName, SourceRange sourceRange) {
                this(dataConsName.getModuleName(), dataConsName.getUnqualifiedName(), sourceRange);
            }
            
            /**                  
             * @param moduleName may be null, in which case Cons is unqualified. For efficiency, it is better to supply the
             *     explicit qualification if available. If non-null, it must be a syntactically valid module name.
             * @param dataConsName must be a non-null syntactically valid data constructor name.
             * @return an instance of DataCons
             */
            public static DataCons make(Name.Module moduleName, String dataConsName) {
                return new DataCons(moduleName, dataConsName, null);
            }
            
            /**                  
             * @param moduleName may be null, in which case Cons is unqualified. For efficiency, it is better to supply the
             *     explicit qualification if available. If non-null, it must be a syntactically valid module name.
             * @param dataConsName must be a non-null syntactically valid data constructor name.
             * @return an instance of DataCons
             */
            public static DataCons make(ModuleName moduleName, String dataConsName) {
                return new DataCons(moduleName, dataConsName, null);
            }
            
            public static DataCons make(QualifiedName dataConsName) {
                return new DataCons(dataConsName, null);
            }
            
            /**
             * @param unqualifiedDataConsName the unqualified name for a data constructor.
             * @return an unqualified data constructor name.
             */
            public static DataCons makeUnqualified(String unqualifiedDataConsName) {
                return make((Name.Module)null, unqualifiedDataConsName);
            }
            
            static DataCons makeAnnotated(Name.Module moduleName, String dataConsName, SourceRange sourceRange) {
                return new DataCons(moduleName, dataConsName, sourceRange);
            }

            public String getUnqualifiedName() {
                return dataConsName;
            }              
            
            ParseTreeNode toParseTreeNode() {
                return toParseTreeNode(CALTreeParserTokenTypes.QUALIFIED_CONS, "QUALIFIED_CONS", CALTreeParserTokenTypes.CONS_ID, dataConsName);
            }
            
            /**
             * {@inheritDoc}
             */
            public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                return visitor.visit_Name_DataCons(this, arg);
            }
        }         
        
        /**
         * Models a potentially qualified type class name such as "Prelude.Eq" or "Ord".
         * 
         * @author Bo Ilic
         */
        public static final class TypeClass extends Qualifiable {
            
            private final String typeClassName;
            
            /**                  
             * @param moduleName may be null, in which case the type class name is unqualified. For efficiency, it is better to supply the
             *     explicit qualification if available. If non-null, it must be a syntactically valid module name.
             * @param typeClassName must be a non-null syntactically valid type constructor name.
             * @param sourceRange the position of the beginning of this TypeClass name
             */
            private TypeClass(Name.Module moduleName, String typeClassName, SourceRange sourceRange) { 
                super(moduleName, sourceRange);
                
                if (!LanguageInfo.isValidTypeClassName(typeClassName)) {
                    throw new IllegalArgumentException();
                }
                
                this.typeClassName = typeClassName;
            }
            
            /**                  
             * @param moduleName may be null, in which case the type class name is unqualified. For efficiency, it is better to supply the
             *     explicit qualification if available. If non-null, it must be a syntactically valid module name.
             * @param typeClassName must be a non-null syntactically valid type constructor name.
             * @param sourceRange the position of the beginning of this TypeClass name
             */
            private TypeClass(ModuleName moduleName, String typeClassName, SourceRange sourceRange) { 
                this(Name.Module.make(moduleName), typeClassName, sourceRange);
            }
            
            private TypeClass(QualifiedName typeClassName, SourceRange sourceRange) {
                this(typeClassName.getModuleName(), typeClassName.getUnqualifiedName(), sourceRange);
            }
            
            /**                  
             * @param moduleName may be null, in which case the type class name is unqualified. For efficiency, it is better to supply the
             *     explicit qualification if available. If non-null, it must be a syntactically valid module name.
             * @param typeClassName must be a non-null syntactically valid type constructor name.
             * @return an instance of TypeClass
             */
            public static TypeClass make(Name.Module moduleName, String typeClassName) {
                return new TypeClass(moduleName, typeClassName, null);
            }
            
            /**                  
             * @param moduleName may be null, in which case the type class name is unqualified. For efficiency, it is better to supply the
             *     explicit qualification if available. If non-null, it must be a syntactically valid module name.
             * @param typeClassName must be a non-null syntactically valid type constructor name.
             * @return an instance of TypeClass
             */
            public static TypeClass make(ModuleName moduleName, String typeClassName) {
                return new TypeClass(moduleName, typeClassName, null);
            }
            
            public static TypeClass make(QualifiedName typeClassName) {
                return new TypeClass(typeClassName, null);
            }
            
            /**
             * @param unqualifiedTypeClassName the unqualified name for a type class.
             * @return an unqualified type class name.
             */
            public static TypeClass makeUnqualified(String unqualifiedTypeClassName) {
                return make((Name.Module)null, unqualifiedTypeClassName);
            }
            
            static TypeClass makeAnnotated(Name.Module moduleName, String typeClassName, SourceRange sourceRange) {
                return new TypeClass(moduleName, typeClassName, sourceRange);
            }
            
            public String getUnqualifiedName() {
                return typeClassName;
            }
            
            ParseTreeNode toParseTreeNode() {
                return toParseTreeNode(CALTreeParserTokenTypes.QUALIFIED_CONS, "QUALIFIED_CONS", CALTreeParserTokenTypes.CONS_ID, typeClassName);
            }
            
            /**
             * {@inheritDoc}
             */
            public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                return visitor.visit_Name_TypeClass(this, arg);
            }
        }
        
        public static final class TypeCons extends Qualifiable {
            
            private final String typeConsName;
            
            /**                  
             * @param moduleName may be null, in which case the type constructor name is unqualified. For efficiency, it is better to supply the
             *     explicit qualification if available. If non-null, it must be a syntactically valid module name.
             * @param typeConsName must be a non-null syntactically valid type constructor name.
             * @param sourceRange
             */
            private TypeCons(Name.Module moduleName, String typeConsName, SourceRange sourceRange) { 
                super(moduleName, sourceRange);
                
                if (!LanguageInfo.isValidTypeConstructorName(typeConsName)) {
                    throw new IllegalArgumentException();
                }
                
                this.typeConsName = typeConsName;
            }
            
            /**                  
             * @param moduleName may be null, in which case the type constructor name is unqualified. For efficiency, it is better to supply the
             *     explicit qualification if available. If non-null, it must be a syntactically valid module name.
             * @param typeConsName must be a non-null syntactically valid type constructor name.
             * @param sourceRange
             */
            private TypeCons(ModuleName moduleName, String typeConsName, SourceRange sourceRange) { 
                this(Name.Module.make(moduleName), typeConsName, sourceRange);
            }
            
            private TypeCons(QualifiedName typeConsName, SourceRange sourceRange) {
                this(typeConsName.getModuleName(), typeConsName.getUnqualifiedName(), sourceRange);
            }
            
            /**                  
             * @param moduleName may be null, in which case the type constructor name is unqualified. For efficiency, it is better to supply the
             *     explicit qualification if available. If non-null, it must be a syntactically valid module name.
             * @param typeConsName must be a non-null syntactically valid type constructor name.
             * @return an instance of TypeCons
             */
            public static TypeCons make(Name.Module moduleName, String typeConsName) {
                return new TypeCons(moduleName, typeConsName, null);
            }
            
            /**                  
             * @param moduleName may be null, in which case the type constructor name is unqualified. For efficiency, it is better to supply the
             *     explicit qualification if available. If non-null, it must be a syntactically valid module name.
             * @param typeConsName must be a non-null syntactically valid type constructor name.
             * @return an instance of TypeCons
             */
            public static TypeCons make(ModuleName moduleName, String typeConsName) {
                return new TypeCons(moduleName, typeConsName, null);
            }
            
            public static TypeCons make(QualifiedName typeConsName) {
                return new TypeCons(typeConsName, null);
            }
            
            /**
             * @param unqualifiedTypeConsName the unqualified name for a type constructor.
             * @return an unqualified type constructor name.
             */
            public static TypeCons makeUnqualified(String unqualifiedTypeConsName) {
                return make((Name.Module)null, unqualifiedTypeConsName);
            }
            
            static TypeCons makeAnnotated(Name.Module moduleName, String typeConsName, SourceRange sourceRange) {
                return new TypeCons(moduleName, typeConsName, sourceRange);
            }
            
            public String getUnqualifiedName() {
                return typeConsName;
            }        
            
            ParseTreeNode toParseTreeNode() {
                return toParseTreeNode(CALTreeParserTokenTypes.QUALIFIED_CONS, "QUALIFIED_CONS", CALTreeParserTokenTypes.CONS_ID, typeConsName);
            }
            
            /**
             * {@inheritDoc}
             */
            public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                return visitor.visit_Name_TypeCons(this, arg);
            }
        }         
        
        /**
         * Models a potentially qualified name such as "Prelude", "Prelude.Eq", "Maybe", or "Nothing",
         * appearing in the source where the 'context' of the name is undetermined: it may be a
         * module name, a type constructor name, a data constructor name, or a type class name.
         * 
         * Such a name may appear in a CALDoc comment, as in:
         * {at-link Cal.Prelude at-} - a module reference without context
         * {at-link Nothing at-} - a data constructor reference without context
         * at-see Prelude.Int - a type constructor reference without context
         * at-see Eq - a type class reference without context
         * 
         * (Replace at- with @ in the descriptions above)
         *
         * @author Joseph Wong
         */
        public static final class WithoutContextCons extends Qualifiable {
            
            /**
             * The "unqualified name" portion of the name. Note that if the name represents a module name,
             * the module name's unqualified portion will in fact be stored in this field, and the moduleName field in the
             * superclass will store the module name's qualifier. This is a consequence of the inability to tell the context
             * of the name at parsing-time.
             * <p>
             * This must be a non-null syntactically valid constructor name, i.e. one that starts with an uppercase character.
             */
            private final String unqualifiedName;
            
            /**                  
             * @param moduleName the "module name", which represents the module name portion of a qualified name. If the
             *        instance is meant to represent a true module name, this parameter represents the module name's qualifier.
             *        If non-null, it must be a syntactically valid module name.
             * @param unqualifiedName must be a non-null syntactically valid constructor name, i.e. one that starts with an uppercase character.
             * @param sourceRange
             */
            private WithoutContextCons(Name.Module moduleName, String unqualifiedName, SourceRange sourceRange) { 
                super(moduleName, sourceRange);
                
                if (!LanguageInfo.isValidTypeConstructorName(unqualifiedName)) {
                    throw new IllegalArgumentException();
                }
                
                this.unqualifiedName = unqualifiedName;
            }
            
            /**                  
             * @param moduleName the "module name", which represents the module name portion of a qualified name. If the
             *        instance is meant to represent a true module name, this parameter represents the module name's qualifier.
             *        If non-null, it must be a syntactically valid module name.
             * @param unqualifiedName must be a non-null syntactically valid constructor name, i.e. one that starts with an uppercase character.
             * @param sourceRange
             */
            private WithoutContextCons(ModuleName moduleName, String unqualifiedName, SourceRange sourceRange) { 
                this(Name.Module.make(moduleName), unqualifiedName, sourceRange);
            }
            
            /**
             * Constructs an instance of this class using the module and unqualified names from the specified QualifiedName.
             * @param qualifiedName the qualified name whose components will form the "module name" and "unqualified name"
             *        of this WithoutContextCons instance.
             * @param sourceRange
             */
            private WithoutContextCons(QualifiedName qualifiedName, SourceRange sourceRange) {
                this(qualifiedName.getModuleName(), qualifiedName.getUnqualifiedName(), sourceRange);
            }
            
            /**
             * Factory method for constructing a new source model element for representing a potentially qualified name
             * such as "Prelude", "Prelude.Eq", "Maybe", or "Nothing", appearing in the source where the 'context' of the
             * name is undetermined.
             * 
             * @param maybeQualifiedName a string representing either a qualified name or an unqualified name.
             * @return a new instance of this class.
             */
            public static WithoutContextCons make(String maybeQualifiedName) {
                int lastDotPos = maybeQualifiedName.lastIndexOf('.');
                if (lastDotPos < 0) {
                    // no dot - so no qualifier
                    return new WithoutContextCons((Name.Module)null, maybeQualifiedName, null);
                } else if (lastDotPos == 0) {
                    // dot as the first character - illegal
                    throw new IllegalArgumentException();
                } else {
                    // there is a dot, after the first character
                    String qualifier = maybeQualifiedName.substring(0, lastDotPos);
                    String unqualifiedName = maybeQualifiedName.substring(lastDotPos + 1);
                    return new WithoutContextCons(Name.Module.make(ModuleName.make(qualifier)), unqualifiedName, null);
                }
            }
            
            /**                  
             * @param moduleName the "module name", which represents the module name portion of a qualified name. If the
             *        instance is meant to represent a true module name, this parameter represents the module name's qualifier.
             *        If non-null, it must be a syntactically valid module name.
             * @param unqualifiedName must be a non-null syntactically valid constructor name, i.e. one that starts with an uppercase character.
             * @return an instance of WithoutContextCons
             */
            public static WithoutContextCons make(Name.Module moduleName, String unqualifiedName) {
                return new WithoutContextCons(moduleName, unqualifiedName, null);
            }
            
            /**                  
             * @param moduleName the "module name", which represents the module name portion of a qualified name. If the
             *        instance is meant to represent a true module name, this parameter represents the module name's qualifier.
             *        If non-null, it must be a syntactically valid module name.
             * @param unqualifiedName must be a non-null syntactically valid constructor name, i.e. one that starts with an uppercase character.
             * @return an instance of WithoutContextCons
             */
            public static WithoutContextCons make(ModuleName moduleName, String unqualifiedName) {
                return new WithoutContextCons(moduleName, unqualifiedName, null);
            }
            
            /**
             * Factory method for constructing an instance of this class using the module and unqualified names from the specified QualifiedName.
             * @param qualifiedName the qualified name whose components will form the "module name" and "unqualified name"
             *        of this WithoutContextCons instance.
             * @return an instance of WithoutContextCons
             */
            public static WithoutContextCons make(QualifiedName qualifiedName) {
                return new WithoutContextCons(qualifiedName, null);
            }
            
            static WithoutContextCons makeAnnotated(Name.Module moduleName, String unqualifiedName, SourceRange sourceRange) {
                return new WithoutContextCons(moduleName, unqualifiedName, sourceRange);
            }
            
            /**
             * @return the part of the name not including the module qualification. For example, for "List.filter" this is "filter".
             */
            public String getUnqualifiedName() {
                return unqualifiedName;
            }        
            
            /**
             * {@inheritDoc}
             */
            ParseTreeNode toParseTreeNode() {
                return toParseTreeNode(CALTreeParserTokenTypes.QUALIFIED_CONS, "QUALIFIED_CONS", CALTreeParserTokenTypes.CONS_ID, unqualifiedName);
            }
            
            /**
             * {@inheritDoc}
             */
            public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                return visitor.visit_Name_WithoutContextCons(this, arg);
            }
        }
        
        /**
         * Models a field name in CAL, e.g. {@code value} (a textual field name) or {@code #3}
         * (an ordinal field name). 
         *
         * @author Joseph Wong
         */
        public static final class Field extends Name {
            
            /** The name of the field. */
            private final FieldName fieldName;
            
            /**
             * Constructs a new source model element for representing a field name.
             * @param fieldName the name of the field.
             * @param sourceRange the associated source range.
             */
            private Field(final FieldName fieldName, final SourceRange sourceRange) {
                super(sourceRange);
                verifyArg(fieldName, "fieldName");
                this.fieldName = fieldName;
            }
            
            /**
             * Factory method for constructing a new source model element for representing a field name.
             * @param fieldName the name of the field.
             * @return a new instance of this class.
             */
            public static Field make(final FieldName fieldName) {
                return new Field(fieldName, null);
            }
            
            /**
             * Factory method for constructing a new source model element for representing a field name.
             * @param fieldName the name of the field.
             * @param sourceRange the associated source range.
             * @return a new instance of this class.
             */
            static Field makeAnnotated(final FieldName fieldName, final SourceRange sourceRange) {
                return new Field(fieldName, sourceRange);
            }
            
            /**
             * @return the name of the field.
             */
            public FieldName getName() {
                return fieldName;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                return visitor.visit_Name_Field(this, arg);
            }

            /**
             * {@inheritDoc}
             */
            @Override
            ParseTreeNode toParseTreeNode() {
                return SourceModel.makeFieldNameExpr(fieldName);
            }
        }
        
        /**
         * Models a type variable name in CAL.
         *
         * @author Joseph Wong
         */
        public static final class TypeVar extends Name {
            
            /** The name of the type variable. */
            private final String typeVarName;
            
            /**
             * Constructs a new source model element for representing a type variable name.
             * @param typeVarName the name of the type variable
             * @param sourceRange the associated source range.
             */
            private TypeVar(final String typeVarName, final SourceRange sourceRange) {
                super(sourceRange);
                verifyArg(typeVarName, "typeVarName");
                if (!LanguageInfo.isValidTypeVariableName(typeVarName)) {
                    throw new IllegalArgumentException();
                }
                this.typeVarName = typeVarName;
            }
            
            /**
             * Factory method for constructing a new source model element for representing a type variable name.
             * @param typeVarName the name of the type variable
             * @return a new instance of this class.
             */
            public static TypeVar make(final String typeVarName) {
                return new TypeVar(typeVarName, null);
            }
            
            /**
             * Factory method for constructing a new source model element for representing a type variable name.
             * @param typeVarName the name of the type variable
             * @param sourceRange the associated source range.
             * @return a new instance of this class.
             */
            static TypeVar makeAnnotated(final String typeVarName, final SourceRange sourceRange) {
                return new TypeVar(typeVarName, sourceRange);
            }
            
            /**
             * @return the name of the type variable.
             */
            public String getName() {
                return typeVarName;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                return visitor.visit_Name_TypeVar(this, arg);
            }

            /**
             * {@inheritDoc}
             */
            @Override
            ParseTreeNode toParseTreeNode() {
                return new ParseTreeNode(CALTreeParserTokenTypes.VAR_ID, typeVarName);
            }
        }

        /**
         * @param sourceRange
         */
        private Name(SourceRange sourceRange) {
            super(sourceRange);
        }
    }
    
    /**
     * Base class for all source elements that constitute a CALDoc comment. CALDoc is the documentation comment
     * format for CAL.
     * 
     * @author Joseph Wong
     */
    public static abstract class CALDoc extends SourceElement {
        
        private CALDoc(SourceRange sourceRange) {
            super(sourceRange);
        }
        
        /**
         * Models a CALDoc comment, which consists of a description block followed by a list
         * of tagged blocks.
         *
         * @author Joseph Wong
         */
        public static abstract class Comment extends CALDoc {
            
            /** The description block at the start of the comment. */
            private final TextBlock description;
            
            /**
             * The tagged blocks in the latter part of the comment. This can be
             * an empty array if the comment contains no tags.
             */
            private final TaggedBlock[] taggedBlocks;
            public static final TaggedBlock[] NO_TAGGED_BLOCKS = new TaggedBlock[0];
            
            /**
             * Private constructor for this base class for CALDoc comments. Intended
             * only to be invoked by subclass constructors.
             * 
             * @param description the description block at the start of the comment.
             * @param taggedBlocks an array of tagged blocks in the latter part of the comment.
             */
            private Comment(TextBlock description, TaggedBlock[] taggedBlocks, SourceRange range) {
                super(range);
                
                verifyArg(description, "description");
                this.description = description;
                
                if (taggedBlocks == null || taggedBlocks.length == 0) {
                    this.taggedBlocks = NO_TAGGED_BLOCKS;
                } else {
                    this.taggedBlocks = taggedBlocks.clone();
                    verifyArrayArg(this.taggedBlocks, "taggedBlocks");                                       
                }
            }
                        
            /**
             * @return the description block at the start of the comment. 
             */
            public TextBlock getDescription() {
                return description;
            }
            
            /**
             * @return the tagged blocks in the latter part of the comment. This
             *         can be an empty array if the comment contains no tags.
             */
            public TaggedBlock[] getTaggedBlocks() {

                if (taggedBlocks.length == 0) {
                    return NO_TAGGED_BLOCKS;
                }
                
                return taggedBlocks.clone();
            }
            
            /**
             * @return the number of tagged blocks contained this the comment.
             */
            public int getNTaggedBlocks() {
                return taggedBlocks.length;
            }
            
            /**
             * Returns the tagged block at the specified position in the array
             * of tagged blocks.
             * 
             * @param n
             *            the index of the tagged block to return
             * @return the tagged block at the specified position in the array
             *         of tagged blocks.
             */
            public TaggedBlock getNthTaggedBlock(int n) {
                return taggedBlocks[n];
            }

            /**
             * {@inheritDoc}
             */
            ParseTreeNode toParseTreeNode() {
                ParseTreeNode commentNode = new ParseTreeNode(CALTreeParserTokenTypes.CALDOC_COMMENT, "CALDOC_COMMENT");
                ParseTreeNode descriptionNode = new ParseTreeNode(CALTreeParserTokenTypes.CALDOC_DESCRIPTION_BLOCK, "CALDOC_DESCRIPTION_BLOCK");
                ParseTreeNode descriptionTextNode = description.toParseTreeNode();
                ParseTreeNode taggedBlocksNode = new ParseTreeNode(CALTreeParserTokenTypes.CALDOC_TAGGED_BLOCKS, "CALDOC_TAGGED_BLOCKS");
                
                ParseTreeNode[] taggedBlockNodeList = new ParseTreeNode[taggedBlocks.length];
                for (int i = 0; i < taggedBlocks.length; i++) {
                    taggedBlockNodeList[i] = taggedBlocks[i].toParseTreeNode();
                }
                taggedBlocksNode.addChildren(taggedBlockNodeList);
                
                commentNode.setFirstChild(descriptionNode);
                descriptionNode.setFirstChild(descriptionTextNode);
                descriptionNode.setNextSibling(taggedBlocksNode);
                
                return commentNode;
            }


            /**
             * Models a CALDoc comment associated with a module definition.
             *
             * @author Joseph Wong
             */
            public static final class Module extends Comment {
                
                /**
                 * Creates a new source model element for representing a CALDoc comment associated
                 * with a module definition.
                 * 
                 * @param description the description block at the start of the comment.
                 * @param taggedBlocks an array of tagged blocks in the latter part of the comment.
                 * @param range the source range for this element
                 */
                private Module(TextBlock description, TaggedBlock[] taggedBlocks, SourceRange range) {
                    super(description, taggedBlocks, range);
                }
                
                /**
                 * Factory method for constructing a new source model element for representing a CALDoc comment associated
                 * with a module definition.
                 * 
                 * @param description the description block at the start of the comment.
                 * @param taggedBlocks an array of tagged blocks in the latter part of the comment.
                 * @return a new instance of this class.
                 */
                public static Module make(TextBlock description, TaggedBlock[] taggedBlocks) {
                    return new Module(description, taggedBlocks, null);
                }
                
                /**
                 * Factory method for constructing a new source model element for representing a CALDoc comment associated
                 * with a module definition.
                 * 
                 * @param description the description block at the start of the comment.
                 * @param taggedBlocks an array of tagged blocks in the latter part of the comment.
                 * @param range the source range of this element
                 * @return a new instance of this class.
                 */
  
                static Module makeAnnotated(TextBlock description, TaggedBlock[] taggedBlocks, SourceRange range) {
                    return new Module(description, taggedBlocks, range);
                }
                
                /**
                 * {@inheritDoc}
                 */
                public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                    return visitor.visit_CALDoc_Comment_Module(this, arg);
                }
            }
            
            /**
             * Models a CALDoc comment associated with a function definition.
             *
             * @author Joseph Wong
             */
            public static final class Function extends Comment {
                
                /**
                 * Creates a new source model element for representing a CALDoc comment associated
                 * with a function definition.
                 * 
                 * @param description the description block at the start of the comment.
                 * @param taggedBlocks an array of tagged blocks in the latter part of the comment.
                 * @param range the source range of this element
                 */
                private Function(TextBlock description, TaggedBlock[] taggedBlocks, SourceRange range) {
                    super(description, taggedBlocks, range);
                }
                
                /**
                 * Factory method for constructing a new source model element for representing a CALDoc comment associated
                 * with a function definition.
                 * 
                 * @param description the description block at the start of the comment.
                 * @param taggedBlocks an array of tagged blocks in the latter part of the comment.
                 * @return a new instance of this class.
                 */
                public static Function make(TextBlock description, TaggedBlock[] taggedBlocks) {
                    return new Function(description, taggedBlocks, null);
                }
 
                /**
                 * Factory method for constructing a new source model element for representing a CALDoc comment associated
                 * with a function definition.
                 * 
                 * @param description the description block at the start of the comment.
                 * @param taggedBlocks an array of tagged blocks in the latter part of the comment.
                 * @param range the source range of this element
                 * @return a new instance of this class.
                 */
                static Function makeAnnotated(TextBlock description, TaggedBlock[] taggedBlocks, SourceRange range) {
                    return new Function(description, taggedBlocks, range);
                }
 
                /**
                 * {@inheritDoc}
                 */
                public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                    return visitor.visit_CALDoc_Comment_Function(this, arg);
                }
            }
            
            /**
             * Models a CALDoc comment associated with a type constructor definition.
             *
             * @author Joseph Wong
             */
            public static final class TypeCons extends Comment {
                
                /**
                 * Creates a new source model element for representing a CALDoc comment associated
                 * with a type constructor definition.
                 * 
                 * @param description the description block at the start of the comment.
                 * @param taggedBlocks an array of tagged blocks in the latter part of the comment.
                 */
                private TypeCons(TextBlock description, TaggedBlock[] taggedBlocks, SourceRange range) {
                    super(description, taggedBlocks, range);
                }
                
                /**
                 * Factory method for constructing a new source model element for representing a CALDoc comment associated
                 * with a type constructor definition.
                 * 
                 * @param description the description block at the start of the comment.
                 * @param taggedBlocks an array of tagged blocks in the latter part of the comment.
                 * @return a new instace of this class.
                 */
                public static TypeCons make(TextBlock description, TaggedBlock[] taggedBlocks) {
                    return new TypeCons(description, taggedBlocks, null);
                }
                
                /**
                 * Factory method for constructing a new source model element for representing a CALDoc comment associated
                 * with a type constructor definition.
                 * 
                 * @param description the description block at the start of the comment.
                 * @param taggedBlocks an array of tagged blocks in the latter part of the comment.
                 * @return a new instace of this class.
                 */
                static TypeCons makeAnnotated(TextBlock description, TaggedBlock[] taggedBlocks, SourceRange range) {
                    return new TypeCons(description, taggedBlocks, range);
                }
                
                /**
                 * {@inheritDoc}
                 */
                public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                    return visitor.visit_CALDoc_Comment_TypeCons(this, arg);
                }
            }
            
            /**
             * Models a CALDoc comment associated with a data constructor definition.
             *
             * @author Joseph Wong
             */
            public static final class DataCons extends Comment {
                
                /**
                 * Creates a new source model element for representing a CALDoc comment associated
                 * with a data constructor definition.
                 * 
                 * @param description the description block at the start of the comment.
                 * @param taggedBlocks an array of tagged blocks in the latter part of the comment.
                 * @param range the source range of this element
                 */
                private DataCons(TextBlock description, TaggedBlock[] taggedBlocks, SourceRange range) {
                    super(description, taggedBlocks, range);
                }
                
                /**
                 * Factory method for constructing a new source model element for representing a CALDoc comment associated
                 * with a data constructor definition.
                 * 
                 * @param description the description block at the start of the comment.
                 * @param taggedBlocks an array of tagged blocks in the latter part of the comment.
                 * @return a new instance of this class.
                 */
                public static DataCons make(TextBlock description, TaggedBlock[] taggedBlocks) {
                    return new DataCons(description, taggedBlocks, null);
                }
                
                /**
                 * Factory method for constructing a new source model element for representing a CALDoc comment associated
                 * with a data constructor definition.
                 * 
                 * @param description the description block at the start of the comment.
                 * @param taggedBlocks an array of tagged blocks in the latter part of the comment.
                 * @param range the source range of this element
                 * @return a new instance of this class.
                 */
                static DataCons makeAnnotated(TextBlock description, TaggedBlock[] taggedBlocks, SourceRange range) {
                    return new DataCons(description, taggedBlocks, range);
                }
                
                /**
                 * {@inheritDoc}
                 */
                public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                    return visitor.visit_CALDoc_Comment_DataCons(this, arg);
                }
            }
            
            /**
             * Models a CALDoc comment associated with a type class definition.
             *
             * @author Joseph Wong
             */
            public static final class TypeClass extends Comment {
                
                /**
                 * Creates a new source model element for representing a CALDoc comment associated
                 * with a type class definition.
                 * 
                 * @param description the description block at the start of the comment.
                 * @param taggedBlocks an array of tagged blocks in the latter part of the comment.
                 * @param range the source range of this element
                 */
                private TypeClass(TextBlock description, TaggedBlock[] taggedBlocks, SourceRange range) {
                    super(description, taggedBlocks, range);
                }
                
                /**
                 * Factory method for constructing a new source model element for representing a CALDoc comment associated
                 * with a type class definition.
                 * 
                 * @param description the description block at the start of the comment.
                 * @param taggedBlocks an array of tagged blocks in the latter part of the comment.
                 * @return a new instance of this class.
                 */
                public static TypeClass make(TextBlock description, TaggedBlock[] taggedBlocks) {
                    return new TypeClass(description, taggedBlocks, null);
                }
                
                /**
                 * Factory method for constructing a new source model element for representing a CALDoc comment associated
                 * with a type class definition.
                 * 
                 * @param description the description block at the start of the comment.
                 * @param taggedBlocks an array of tagged blocks in the latter part of the comment.
                 * @param range the source element range
                 * @return a new instance of this class.
                 */
                static TypeClass makeAnnotated(TextBlock description, TaggedBlock[] taggedBlocks, SourceRange range) {
                    return new TypeClass(description, taggedBlocks, range);
                }
               
                
                /**
                 * {@inheritDoc}
                 */
                public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                    return visitor.visit_CALDoc_Comment_TypeClass(this, arg);
                }
            }
            
            /**
             * Models a CALDoc comment associated with a class method definition.
             *
             * @author Joseph Wong
             */
            public static final class ClassMethod extends Comment {
                
                /**
                 * Creates a new source model element for representing a CALDoc comment associated
                 * with a class method definition.
                 * 
                 * @param description the description block at the start of the comment.
                 * @param taggedBlocks an array of tagged blocks in the latter part of the comment.
                 */
                private ClassMethod(TextBlock description, TaggedBlock[] taggedBlocks, SourceRange range) {
                    super(description, taggedBlocks, range);
                }
                
                /**
                 * Factory method for constructing a new source model element for representing a CALDoc comment associated
                 * with a class method definition.
                 * 
                 * @param description the description block at the start of the comment.
                 * @param taggedBlocks an array of tagged blocks in the latter part of the comment.
                 * @return a new instance of this class.
                 */
                public static ClassMethod make(TextBlock description, TaggedBlock[] taggedBlocks) {
                    return new ClassMethod(description, taggedBlocks, null);
                }
              
                /**
                 * Factory method for constructing a new source model element for representing a CALDoc comment associated
                 * with a class method definition.
                 * 
                 * @param description the description block at the start of the comment.
                 * @param taggedBlocks an array of tagged blocks in the latter part of the comment.
                 * @return a new instance of this class.
                 */
                static ClassMethod makeAnnotated(TextBlock description, TaggedBlock[] taggedBlocks, SourceRange range) {
                    return new ClassMethod(description, taggedBlocks, range);
                }
       
                /**
                 * {@inheritDoc}
                 */
                public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                    return visitor.visit_CALDoc_Comment_ClassMethod(this, arg);
                }
            }
            
            /**
             * Models a CALDoc comment associated with an instance definition.
             *
             * @author Joseph Wong
             */
            public static final class Instance extends Comment {
                
                /**
                 * Creates a new source model element for representing a CALDoc comment associated
                 * with an instance definition.
                 * 
                 * @param description the description block at the start of the comment.
                 * @param taggedBlocks an array of tagged blocks in the latter part of the comment.
                 */
                private Instance(TextBlock description, TaggedBlock[] taggedBlocks, SourceRange range) {
                    super(description, taggedBlocks, range);
                }
                
                /**
                 * Factory method for constructing a new source model element for representing a CALDoc comment associated
                 * with an instance definition.
                 * 
                 * @param description the description block at the start of the comment.
                 * @param taggedBlocks an array of tagged blocks in the latter part of the comment.
                 * @return a new instance of this class.
                 */
                public static Instance make(TextBlock description, TaggedBlock[] taggedBlocks) {
                    return new Instance(description, taggedBlocks, null);
                }
  
                /**
                 * Factory method for constructing a new source model element for representing a CALDoc comment associated
                 * with an instance definition.
                 * 
                 * @param description the description block at the start of the comment.
                 * @param taggedBlocks an array of tagged blocks in the latter part of the comment.
                 * @return a new instance of this class.
                 */
                static Instance makeAnnotated(TextBlock description, TaggedBlock[] taggedBlocks, SourceRange range) {
                    return new Instance(description, taggedBlocks, range);
                }
     
                /**
                 * {@inheritDoc}
                 */
                public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                    return visitor.visit_CALDoc_Comment_Instance(this, arg);
                }
            }
            
            /**
             * Models a CALDoc comment associated with an instance method definition.
             *
             * @author Joseph Wong
             */
            public static final class InstanceMethod extends Comment {
                
                /**
                 * Creates a new source model element for representing a CALDoc comment associated
                 * with an instance method definition.
                 * 
                 * @param description the description block at the start of the comment.
                 * @param taggedBlocks an array of tagged blocks in the latter part of the comment.
                 */
                private InstanceMethod(TextBlock description, TaggedBlock[] taggedBlocks, SourceRange range) {
                    super(description, taggedBlocks, range);
                }
                
                /**
                 * Factory method for constructing a new source model element for representing a CALDoc comment associated
                 * with an instance method definition.
                 * 
                 * @param description the description block at the start of the comment.
                 * @param taggedBlocks an array of tagged blocks in the latter part of the comment.
                 * @return a new instance of this class.
                 */
                public static InstanceMethod make(TextBlock description, TaggedBlock[] taggedBlocks) {
                    return new InstanceMethod(description, taggedBlocks, null);
                }
   
                /**
                 * Factory method for constructing a new source model element for representing a CALDoc comment associated
                 * with an instance method definition.
                 * 
                 * @param description the description block at the start of the comment.
                 * @param taggedBlocks an array of tagged blocks in the latter part of the comment.
                 * @return a new instance of this class.
                 */
                static InstanceMethod makeAnnotated(TextBlock description, TaggedBlock[] taggedBlocks, SourceRange range) {
                    return new InstanceMethod(description, taggedBlocks, range);
                }
 
                /**
                 * {@inheritDoc}
                 */
                public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                    return visitor.visit_CALDoc_Comment_InstanceMethod(this, arg);
                }
            }
        }
        
        /**
         * Models a reference in a list of references in a CALDoc "@see" block or "@link" inline block.
         * A reference can either be checked or unchecked. An unchecked reference
         * is represented in source by surrounding the name in double quotes.
         *
         * @author Joseph Wong
         */
        public static abstract class CrossReference extends SourceElement {
            
            /**
             * Base class for a reference in a CALDoc "@see" block or "@link" inline block that can appear without
             * the context keyword.
             *
             * @author Joseph Wong
             */
            public static abstract class CanAppearWithoutContext extends CrossReference {
                
                /**
                 * Private constructor for this base class for a reference in a CALDoc "@see" block or "@link"
                 * inline block that can appear without the context keyword. Intended to be invoked only by subclass
                 * constructors.
                 * 
                 * @param checked
                 *          whether the reference is to be statically checked and
                 *          resolved during compilation.
                 * @param sourceRange
                 */ 
                private CanAppearWithoutContext(boolean checked, SourceRange sourceRange) {
                    super(checked, sourceRange);
                }
            }
            
            /**
             * Models a (checked/unchecked) function name reference in a CALDoc "@see function = ..." block.
             *
             * @author Joseph Wong
             */
            public static final class Function extends CanAppearWithoutContext {
                
                /** The function name encapsulated by this reference. */
                private final Name.Function name;
                
                /**
                 * Creates a source model element for representing a function name reference
                 * in a CALDoc "@see function = ..." block.
                 * 
                 * @param name the function name.
                 * @param checked whether the name is to be checked and resolved statically during compilation.
                 */
                private Function(Name.Function name, boolean checked) {
                    super(checked, null);
                    
                    verifyArg(name, "name");
                    this.name = name;
                }
                
                /**
                 * Factory method for constructing a source model element for representing a function name reference
                 * in a CALDoc "@see function = ..." block.
                 * 
                 * @param name the function name.
                 * @param checked whether the name is to be checked and resolved statically during compilation.
                 * @return a new instance of this class.
                 */
                public static Function make(Name.Function name, boolean checked) {
                    return new Function(name, checked);
                }
                
                /**
                 * @return the function name encapsulated by this reference.
                 */
                public Name.Function getName() {
                    return name;
                }
                
                /**
                 * {@inheritDoc}
                 */
                public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                    return visitor.visit_CALDoc_CrossReference_Function(this, arg);
                }
            
                /**
                 * {@inheritDoc}
                 */
                ParseTreeNode toParseTreeNode() {
                    
                    ParseTreeNode refNode;
                    
                    if (isChecked()) {
                        refNode = new ParseTreeNode(CALTreeParserTokenTypes.CALDOC_CHECKED_QUALIFIED_VAR, "CALDOC_CHECKED_QUALIFIED_VAR");
                    } else {
                        refNode = new ParseTreeNode(CALTreeParserTokenTypes.CALDOC_UNCHECKED_QUALIFIED_VAR, "CALDOC_UNCHECKED_QUALIFIED_VAR");
                    }
                    
                    refNode.setFirstChild(name.toParseTreeNode());
                    return refNode;                            
                }
            

            }

            /**
             * Models a (checked/unchecked) module name reference in a CALDoc "@see module = ..." block.
             *
             * @author Joseph Wong
             */
            public static final class Module extends CrossReference {
                
                /** The module name encapsulated by this reference. */
                private final Name.Module name;
                
                /**
                 * Creates a source model element for representing a module name reference
                 * in a CALDoc "@see module = ..." block.
                 * 
                 * @param name the module name.
                 * @param checked whether the name is to be checked and resolved statically during compilation.
                 * @param sourceRange
                 */
                private Module(Name.Module name, boolean checked, SourceRange sourceRange) {
                    super(checked, sourceRange);
                    
                    verifyArg(name, "name");
                    this.name = name;
                }
                
                /**
                 * Factory method for constructing a source model element for representing a module name reference
                 * in a CALDoc "@see module = ..." block.
                 * 
                 * @param name the module name.
                 * @param checked whether the name is to be checked and resolved statically during compilation.
                 * @return a new instance of this class.
                 */
                public static Module make(Name.Module name, boolean checked) {
                    return new Module(name, checked, null);
                }
                
                /**
                 * @return the module name encapsulated by this reference.
                 */
                public Name.Module getName() {
                    return name;
                }
                
                /**
                 * {@inheritDoc}
                 */
                public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                    return visitor.visit_CALDoc_CrossReference_Module(this, arg);
                }
            
                /**
                 * {@inheritDoc}
                 */
                ParseTreeNode toParseTreeNode() {
                    
                    ParseTreeNode refNode;
                    
                    if (isChecked()) {
                        refNode = new ParseTreeNode(CALTreeParserTokenTypes.CALDOC_CHECKED_MODULE_NAME, "CALDOC_CHECKED_MODULE_NAME");
                    } else {
                        refNode = new ParseTreeNode(CALTreeParserTokenTypes.CALDOC_UNCHECKED_MODULE_NAME, "CALDOC_UNCHECKED_MODULE_NAME");
                    }
                    
                    refNode.setFirstChild(name.toParseTreeNode());
                    return refNode;                            
                }
            
            }

            /**
             * Models a (checked/unchecked) type constructor name reference in a CALDoc "@see typeConstructor = ..." block.
             *
             * @author Joseph Wong
             */
            public static final class TypeCons extends CrossReference {
                
                /** The type constructor name encapsulated by this reference. */
                private final Name.TypeCons name;
                
                /**
                 * Creates a source model element for representing a type constructor name reference
                 * in a CALDoc "@see typeConstructor = ..." block.
                 * 
                 * @param name the type constructor name.
                 * @param checked whether the name is to be checked and resolved statically during compilation.
                 */
                private TypeCons(Name.TypeCons name, boolean checked) {
                    super(checked, null);
                    
                    verifyArg(name, "name");
                    this.name = name;
                }
                
                /**
                 * Factory method for constructing a source model element for representing a type constructor name reference
                 * in a CALDoc "@see typeConstructor = ..." block.
                 * 
                 * @param name the type constructor name.
                 * @param checked whether the name is to be checked and resolved statically during compilation.
                 * @return a new instance of this class.
                 */
                public static TypeCons make(Name.TypeCons name, boolean checked) {
                    return new TypeCons(name, checked);
                }
                
                /**
                 * @return the tyep constructor name encapsulated by this reference.
                 */
                public Name.TypeCons getName() {
                    return name;
                }
                
                /**
                 * {@inheritDoc}
                 */
                public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                    return visitor.visit_CALDoc_CrossReference_TypeCons(this, arg);
                }
            
                /**
                 * {@inheritDoc}
                 */
                ParseTreeNode toParseTreeNode() {
                    
                    ParseTreeNode refNode;
                    
                    if (isChecked()) {
                        refNode = new ParseTreeNode(CALTreeParserTokenTypes.CALDOC_CHECKED_QUALIFIED_CONS, "CALDOC_CHECKED_QUALIFIED_CONS");
                    } else {
                        refNode = new ParseTreeNode(CALTreeParserTokenTypes.CALDOC_UNCHECKED_QUALIFIED_CONS, "CALDOC_UNCHECKED_QUALIFIED_CONS");
                    }
                    
                    refNode.setFirstChild(name.toParseTreeNode());
                    return refNode;                            
                }
            
 
            }

            /**
             * Models a (checked/unchecked) data constructor name reference in a CALDoc "@see dataConstructor = ..." block.
             *
             * @author Joseph Wong
             */
            public static final class DataCons extends CrossReference {
                
                /** The data constructor name encapsulated by this reference. */
                private final Name.DataCons name;
                
                /**
                 * Creates a source model element for representing a data constructor name reference
                 * in a CALDoc "@see dataConstructor = ..." block.
                 * 
                 * @param name the data constructor name.
                 * @param checked whether the name is to be checked and resolved statically during compilation.
                 */
                private DataCons(Name.DataCons name, boolean checked) {
                    super(checked, null);
                    
                    verifyArg(name, "name");
                    this.name = name;
                }
                
                /**
                 * Factory method for constructing a source model element for representing a data constructor name reference
                 * in a CALDoc "@see dataConstructor = ..." block.
                 * 
                 * @param name the data constructor name.
                 * @param checked whether the name is to be checked and resolved statically during compilation.
                 * @return a new instance of this class.
                 */
                public static DataCons make(Name.DataCons name, boolean checked) {
                    return new DataCons(name, checked);
                }
                
                /**
                 * @return the data constructor name encapsulated by this reference.
                 */
                public Name.DataCons getName() {
                    return name;
                }
                
                /**
                 * {@inheritDoc}
                 */
                public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                    return visitor.visit_CALDoc_CrossReference_DataCons(this, arg);
                }
            
                /**
                 * {@inheritDoc}
                 */
                ParseTreeNode toParseTreeNode() {
                    
                    ParseTreeNode refNode;
                    
                    if (isChecked()) {
                        refNode = new ParseTreeNode(CALTreeParserTokenTypes.CALDOC_CHECKED_QUALIFIED_CONS, "CALDOC_CHECKED_QUALIFIED_CONS");
                    } else {
                        refNode = new ParseTreeNode(CALTreeParserTokenTypes.CALDOC_UNCHECKED_QUALIFIED_CONS, "CALDOC_UNCHECKED_QUALIFIED_CONS");
                    }
                    
                    refNode.setFirstChild(name.toParseTreeNode());
                    return refNode;                            
                }
            
            }

            /**
             * Models a (checked/unchecked) type class name reference in a CALDoc "@see typeClass = ..." block.
             *
             * @author Joseph Wong
             */
            public static final class TypeClass extends CrossReference {
                
                /** The type class name encapsulated by this reference. */
                private final Name.TypeClass name;
                
                /**
                 * Creates a source model element for representing a type class name reference
                 * in a CALDoc "@see typeClass = ..." block.
                 * 
                 * @param name the type class name.
                 * @param checked whether the name is to be checked and resolved statically during compilation.
                 */
                private TypeClass(Name.TypeClass name, boolean checked) {
                    super(checked, null);
                    
                    verifyArg(name, "name");
                    this.name = name;
                }
                
                /**
                 * Factory method for constructing a source model element for representing a type class name reference
                 * in a CALDoc "@see typeClass = ..." block.
                 * 
                 * @param name the type class name.
                 * @param checked whether the name is to be checked and resolved statically during compilation.
                 * @return a new instance of this class.
                 */
                public static TypeClass make(Name.TypeClass name, boolean checked) {
                    return new TypeClass(name, checked);
                }
                
                /**
                 * @return the type class name encapsulated by this reference.
                 */
                public Name.TypeClass getName() {
                    return name;
                }
                
                /**
                 * {@inheritDoc}
                 */
                public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                    return visitor.visit_CALDoc_CrossReference_TypeClass(this, arg);
                }
            
                /**
                 * {@inheritDoc}
                 */
                ParseTreeNode toParseTreeNode() {
                    
                    ParseTreeNode refNode;
                    
                    if (isChecked()) {
                        refNode = new ParseTreeNode(CALTreeParserTokenTypes.CALDOC_CHECKED_QUALIFIED_CONS, "CALDOC_CHECKED_QUALIFIED_CONS");
                    } else {
                        refNode = new ParseTreeNode(CALTreeParserTokenTypes.CALDOC_UNCHECKED_QUALIFIED_CONS, "CALDOC_UNCHECKED_QUALIFIED_CONS");
                    }
                    
                    refNode.setFirstChild(name.toParseTreeNode());
                    return refNode;                            
                }
            
 
            }
            
            /**
             * Models a (checked/unchecked) constructor/module name reference in a CALDoc "@see"/"@link" block without
             * the context keyword.
             *
             * @author Joseph Wong
             */
            public static final class WithoutContextCons extends CanAppearWithoutContext {
                
                /** The constructor/module name encapsulated by this reference. */
                private final Name.WithoutContextCons name;
                
                /**
                 * Creates a source model element for representing a constructor/module name reference in a CALDoc
                 * "@see"/"@link" block without the context keyword.
                 * 
                 * @param name the type class name.
                 * @param checked whether the name is to be checked and resolved statically during compilation.
                 * @param sourceRange
                 */
                private WithoutContextCons(Name.WithoutContextCons name, boolean checked, SourceRange sourceRange) {
                    super(checked, sourceRange);
                    
                    verifyArg(name, "name");
                    this.name = name;
                }
                
                /**
                 * Factory method for constructing a source model element for representing a constructor/module name
                 * reference in a CALDoc "@see"/"@link" block without the context keyword.
                 * 
                 * @param name the type class name.
                 * @param checked whether the name is to be checked and resolved statically during compilation.
                 * @return a new instance of this class.
                 */
                public static WithoutContextCons make(Name.WithoutContextCons name, boolean checked) {
                    return new WithoutContextCons(name, checked, null);
                }
                
                static WithoutContextCons makeAnnotated(Name.WithoutContextCons name, boolean checked, SourceRange sourceRange) {
                    return new WithoutContextCons(name, checked, sourceRange);
                }
                
                /**
                 * @return the type class name encapsulated by this reference.
                 */
                public Name.WithoutContextCons getName() {
                    return name;
                }
                
                /**
                 * {@inheritDoc}
                 */
                public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                    return visitor.visit_CALDoc_CrossReference_WithoutContextCons(this, arg);
                }
            
                /**
                 * @deprecated this is deprecated to signify that the returned source range is not the entire definition, but
                 * rather a portion thereof.
                 */
                // todo-jowong refactor this so that the primary source range of the source element is its entire source range
                @Deprecated
                SourceRange getSourceRange() {
                    return getSourceRangeOfReference();
                }

                SourceRange getSourceRangeOfReference() {
                    return super.getSourceRange();
                }

                /**
                 * {@inheritDoc}
                 */
                ParseTreeNode toParseTreeNode() {
                    
                    ParseTreeNode refNode;
                    
                    if (isChecked()) {
                        refNode = new ParseTreeNode(CALTreeParserTokenTypes.CALDOC_CHECKED_QUALIFIED_CONS, "CALDOC_CHECKED_QUALIFIED_CONS");
                    } else {
                        refNode = new ParseTreeNode(CALTreeParserTokenTypes.CALDOC_UNCHECKED_QUALIFIED_CONS, "CALDOC_UNCHECKED_QUALIFIED_CONS");
                    }
                    
                    refNode.setFirstChild(name.toParseTreeNode());
                    return refNode;                            
                }
            
 
            }

            /** Indicates whether the reference is to be statically checked and resolved during compilation. */
            private final boolean checked;

            /**
             * Private constructor for this base class for a reference in a list of
             * references in an "@see"/"@link" block. Intended to be invoked only by subclass
             * constructors.
             * 
             * @param checked
             *          whether the reference is to be statically checked and
             *          resolved during compilation.
             * @param sourceRange          
             */
            private CrossReference(boolean checked, SourceRange sourceRange) {
                super(sourceRange);
                this.checked = checked;
            }

            /**
             * @return true if the reference is to be statically checked and resolved during compilation; false otherwise. 
             */
            public boolean isChecked() {
                return checked;
            }
            
        }
        
        /**
         * Base class for CALDoc comment elements that can be indented in the source on the
         * right hand side of the decorative '*' on the left-edge of the comment.
         *
         * @author Joseph Wong
         */
        public static abstract class InteriorIndentable extends CALDoc {
            
            private InteriorIndentable(SourceRange sourceRange) {
                super(sourceRange);
            }
            
 
            
        }
        
        /**
         * Base class for a text segment in a CALDoc comment.
         *
         * @author Joseph Wong
         */
        public static abstract class TextSegment extends InteriorIndentable {
            
            /**
             * Base class for a top-level text segment in a CALDoc comment, i.e. one that
             * can be a child of a text paragraph. In contrast, a text segment that is not
             * top-level cannot be a direct child of a text paragraph. 
             *
             * @author Joseph Wong
             */
            public static abstract class TopLevel extends TextSegment {
                
                private TopLevel(SourceRange sourceRange) {
                    super(sourceRange);
                }
                
                /** An empty TopLevel array. */
                public static final TopLevel[] NO_SEGMENTS = new TopLevel[0];
            }
            
            /**
             * Models a plain text segment in a CALDoc comment, i.e. a segment consisting of
             * a simple string of characters.
             *
             * @author Joseph Wong
             */
            public static final class Plain extends TopLevel {
                /**
                 * The text that constitutes this plain text segment.
                 */
                private final String text;
                
                /**
                 * Creates a new source model element for representing a plain text segment in a CALDoc comment.
                 * 
                 * @param text the text that constitues this plain text segment.
                 */
                private Plain(String text) {
                    super(null);
                    verifyArg(text, "text");
                    this.text = text;
                }
                
                /**
                 * Factory method for constructing a new source model element for representing a plain text 
                 * segment in a CALDoc comment.
                 * 
                 * @param text the text that constitues this plain text segment.
                 * @return a new instance of this class.
                 */
                public static Plain make(String text) {
                    return new Plain(text);
                }
                
                /**
                 * @return the text that constitues this plain text segment.
                 */
                public String getText() {
                    return text;
                }

                
                /**
                 * Escapes the specified string for inclusion in the source representation of a CALDoc comment.
                 * @param string the stirng to be escaped.
                 * @return the escaped version of the specified string.
                 */
                private static String caldocEscape(String string) {
                    String augmentedString = '\n' + string; // augment the string so that the @-escaping can be uniformly applied to even the first @ after leading spaces
                    String augmentedStringWithEscapedAtSigns = augmentedString.replaceAll("@", "\\\\@");
                    String augmentedEscapedString = augmentedStringWithEscapedAtSigns.replaceAll("\\{@", "\\\\{@"); // the first arg is the regular expression '{@', the second arg the replacement string '\{@'
                    return augmentedEscapedString.substring(1);
                }

                /**
                 * {@inheritDoc}
                 */
                void addToParseTree(ParseTreeNode parentNode) {
                    String escapedText = caldocEscape(text);
                    
                    String textWithNormalizedNewlines = escapedText.replaceAll("\r\n|\r", "\n");
                    StringTokenizer tokenizer = new StringTokenizer(textWithNormalizedNewlines, "\n", true);
                    
                    while (tokenizer.hasMoreTokens()) {
                        String token = tokenizer.nextToken();
                        if (token.equals("\n")) {
                            parentNode.addChild(new ParseTreeNode(CALTreeParserTokenTypes.CALDOC_TEXT_LINE_BREAK, "CALDOC_TEXT_LINE_BREAK"));
                        } else {
                            if (CALDocLexer.isCALDocWhitespaceString(token)) {
                                parentNode.addChild(new ParseTreeNode(CALTreeParserTokenTypes.CALDOC_BLANK_TEXT_LINE, token));
                            } else {
                                parentNode.addChild(new ParseTreeNode(CALTreeParserTokenTypes.CALDOC_TEXT_LINE, token));
                            }
                        }
                    }
                }

                /**
                 * {@inheritDoc}
                 */
                boolean spansMultipleLines() {
                    return (text.indexOf('\n') >= 0) || (text.indexOf('\r') >= 0);
                }

                /**
                 * {@inheritDoc}
                 */
                public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                    return visitor.visit_CALDoc_TextSegment_Plain(this, arg);
                }
            }
            
            /**
             * Models a preformatted text segment in a CALDoc comment. Whitespace characters
             * contained within such a segment is respected and preserved in the formatted output
             * (e.g. when converted to HTML).
             *
             * @author Joseph Wong
             */
            public static final class Preformatted extends TextSegment {
                
                /**
                 * The segments which constitute this preformatted segment. Note that inline tag
                 * segments can appear within a preformatted segment.
                 */
                private final TopLevel[] segments;
                
                /**
                 * Creates a new source model element for representing a preformatted text segment in a CALDoc comment.
                 * 
                 * @param segments the segments which constitute this preformatted segment.
                 */
                private Preformatted(TopLevel[] segments) {
                    super(null);
                    
                    if (segments == null || segments.length == 0) {
                        this.segments = TopLevel.NO_SEGMENTS;
                    } else {
                        this.segments = segments.clone();
                        verifyArrayArg(this.segments, "segments");                                       
                    }
                }
                
                /**
                 * Factory method for constructing a new source model element for representing a preformatted text segment 
                 * in a CALDoc comment.
                 * 
                 * @param segments the segments which constitute this preformatted segment.
                 * @return a new instance of this class.
                 */
                public static Preformatted make(TopLevel[] segments) {
                    return new Preformatted(segments);
                }
                
                /**
                 * @return the segments which constitute this preformatted segment.
                 */
                public TextSegment.TopLevel[] getSegments() {
                    
                    if (segments.length == 0) {
                        return TextSegment.TopLevel.NO_SEGMENTS;
                    }
                    
                    return segments.clone();
                }
                
                /**
                 * @return the number of segments which constitute this preformatted segment.
                 */
                public int getNSegments() {
                    return segments.length;
                }
                
                /**
                 * Returns the segment at the specified position in the array of constituent segments.
                 * @param n the index of the segment to return.
                 * @return the segment at the specified position in the array of constituent segments.
                 */
                public TextSegment.TopLevel getNthSegment(int n) {
                    return segments[n];
                }

                /**
                 * {@inheritDoc}
                 */
                void addToParseTree(ParseTreeNode parentNode) {
                    ParseTreeNode wrapperNode = new ParseTreeNode(CALTreeParserTokenTypes.CALDOC_TEXT_PREFORMATTED_BLOCK, "CALDOC_TEXT_PREFORMATTED_BLOCK");
                    
                    parentNode.addChild(wrapperNode);
                    for (final TopLevel segment : segments) {
                        segment.addToParseTree(wrapperNode);
                    }
                }

 

                /**
                 * {@inheritDoc}
                 */
                boolean spansMultipleLines() {
                    for (final TopLevel segment : segments) {
                        if (segment.spansMultipleLines()) {
                            return true;
                        }
                    }
                    return false;
                }

                /**
                 * {@inheritDoc}
                 */
                public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                    return visitor.visit_CALDoc_TextSegment_Preformatted(this, arg);
                }
            }
            
            /**
             * Base class for an inline tag segment in a CALDoc comment.
             *
             * @author Joseph Wong
             */
            public static abstract class InlineTag extends TopLevel {
                
                /**
                 * Base class for an inline tag segment whose content is simply a TextBlock. This represents
                 * a common kind of inline tag segments in CALDoc.
                 *
                 * @author Joseph Wong
                 */
                public static abstract class InlineTagWithTextBlockContent extends InlineTag {
                    /**
                     * The text block which forms the content of this segment.
                     */
                    private final TextBlock content;
                    
                    /**
                     * Private constructor for this base class for inline tag segments whose content is simply 
                     * a TextBlock. Intended only to be invoked by subclass constructors.
                     * 
                     * @param content the text block which forms the content of this segment.
                     */
                    private InlineTagWithTextBlockContent(TextBlock content) {
                        super(null);
                        verifyArg(content, "content");
                        this.content = content;
                    }
                    
                    /**
                     * @return the text block which forms the content of this segment.
                     */
                    public final TextBlock getContent() {
                        return content;
                    }
                    
                    /**
                     * @return the name of the inline tag.
                     */
                    public abstract String getTagName();
                    
                    /**
                     * @return the parse tree node for the inline tag itself.
                     * (In particular the first child of the CALDOC_TEXT_INLINE_BLOCK node.)
                     */
                    abstract ParseTreeNode makeInlineTagNode();
                    

                    /**
                     * {@inheritDoc}
                     */
                    final void addToParseTree(ParseTreeNode parentNode) {
                        ParseTreeNode inlineBlockNode = new ParseTreeNode(CALTreeParserTokenTypes.CALDOC_TEXT_INLINE_BLOCK, "CALDOC_TEXT_INLINE_BLOCK");
                        ParseTreeNode inlineTagNode = makeInlineTagNode();
                        ParseTreeNode contentNode = content.toParseTreeNode();
                        
                        parentNode.addChild(inlineBlockNode);
                        inlineBlockNode.setFirstChild(inlineTagNode);
                        inlineTagNode.setFirstChild(contentNode);
                    }

                    /**
                     * {@inheritDoc}
                     */
                    final boolean spansMultipleLines() {
                        return content.spansMultipleLines();
                    }
                }
                
                /**
                 * Base class for an inline tag segment whose content is simply a preformatted text segment.
                 *
                 * @author Joseph Wong
                 */
                public static abstract class InlineTagWithPreformattedContent extends InlineTag {
                    /**
                     * The preformatted text segment which forms the content of this segment.
                     */
                    private final Preformatted content;
                    
                    /**
                     * Private constructor for this base class for inline tag segments whose content is simply 
                     * a preformatted text segment. Intended only to be invoked by subclass constructors.
                     * 
                     * @param content the preformatted text segment which forms the content of this segment.
                     */
                    private InlineTagWithPreformattedContent(Preformatted content) {
                        super(null);
                        verifyArg(content, "content");
                        this.content = content;
                    }
                    
                    /**
                     * @return the preformatted text segment which forms the content of this segment.
                     */
                    public final Preformatted getContent() {
                        return content;
                    }
                    
                    /**
                     * @return the name of the inline tag.
                     */
                    public abstract String getTagName();
                    
                    /**
                     * @return the parse tree node for the inline tag itself.
                     * (In particular the first child of the CALDOC_TEXT_INLINE_BLOCK node.)
                     */
                    abstract ParseTreeNode makeInlineTagNode();


                    /**
                     * {@inheritDoc}
                     */
                    final void addToParseTree(ParseTreeNode parentNode) {
                        ParseTreeNode inlineBlockNode = new ParseTreeNode(CALTreeParserTokenTypes.CALDOC_TEXT_INLINE_BLOCK, "CALDOC_TEXT_INLINE_BLOCK");
                        ParseTreeNode inlineTagNode = makeInlineTagNode();
                        
                        parentNode.addChild(inlineBlockNode);
                        inlineBlockNode.setFirstChild(inlineTagNode);
                        content.addToParseTree(inlineTagNode);
                    }

                    /**
                     * {@inheritDoc}
                     */
                    final boolean spansMultipleLines() {
                        return content.spansMultipleLines();
                    }
                }
                
                /**
                 * Models a "@url" CALDoc inline tag segment. This represents a hyperlinkable URL in a CALDoc comment.
                 *
                 * @author Joseph Wong
                 */
                public static final class URL extends InlineTag {
                    /**
                     * The plain text content of the inline tag segment.
                     */
                    private final Plain content;
                    
                    /**
                     * Creates a new source model element for representing a "@url" CALDoc inline tag segment.
                     * 
                     * @param content the plain text content of the inline tag segment.
                     */
                    private URL(Plain content) {
                        super(null);
                        verifyArg(content, "content");
                        this.content = content;
                    }
                    
                    /**
                     * Factory method for constructing a new source model element for representing a "@url" CALDoc
                     * inline tag segment.
                     * 
                     * @param content the plain text content of the inline tag segment.
                     * @return a new instance of this class.
                     */
                    public static URL make(Plain content) {
                        return new URL(content);
                    }
                    
                    /**
                     * @return the plain text content of the inline tag segment.
                     */
                    public Plain getContent() {
                        return content;
                    }

                    /**
                     * {@inheritDoc}
                     */
                    void addToParseTree(ParseTreeNode parentNode) {
                        ParseTreeNode inlineBlockNode = new ParseTreeNode(CALTreeParserTokenTypes.CALDOC_TEXT_INLINE_BLOCK, "CALDOC_TEXT_INLINE_BLOCK");
                        ParseTreeNode urlNode = new ParseTreeNode(CALTreeParserTokenTypes.CALDOC_TEXT_URL, "CALDOC_TEXT_URL");
                        ParseTreeNode withoutInlineTagsNode = new ParseTreeNode(CALTreeParserTokenTypes.CALDOC_TEXT_BLOCK_WITHOUT_INLINE_TAGS, "CALDOC_TEXT_BLOCK_WITHOUT_INLINE_TAGS");
                        
                        parentNode.addChild(inlineBlockNode);
                        inlineBlockNode.setFirstChild(urlNode);
                        urlNode.setFirstChild(withoutInlineTagsNode);
                        content.addToParseTree(withoutInlineTagsNode);
                    }

                    /**
                     * {@inheritDoc}
                     */
                    final boolean spansMultipleLines() {
                        return content.spansMultipleLines();
                    }

                    /**
                     * {@inheritDoc}
                     */
                    public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                        return visitor.visit_CALDoc_TextSegment_InlineTag_URL(this, arg);
                    }
                }
                
                /**
                 * Models a "@link" CALDoc inline tag segment. This represents an inline cross-reference in a CALDoc comment.
                 * There are several different varieties of this inline tag segment. For "@link" segments with context,
                 * each of the varieties correspond to a different context under which the name is to be resolved, e.g. "{at-link module = Prelude at-}",
                 * or "{at-link typeClass = Prelude.Eq at-}". (Replace 'at-' with '@' in preceding examples.)
                 *
                 * @author Joseph Wong
                 */
                public static abstract class Link extends InlineTag {
                    
                    /**
                     * Models a "@link" CALDoc inline tag segment without a context.
                     * 
                     * e.g {at-link Nothing at-}, {at-link Prelude.Int at-}, {at-link compare at-}, {at-link "List.map" at-} 
                     */
                    public static abstract class WithoutContext extends Link {

                        private WithoutContext(SourceRange sourceRange) {
                            super(sourceRange);
                        }
                        
                        /**
                         * {@inheritDoc}
                         */
                        final void addToParseTree(ParseTreeNode parentNode) {
                            ParseTreeNode inlineBlockNode = new ParseTreeNode(CALTreeParserTokenTypes.CALDOC_TEXT_INLINE_BLOCK, "CALDOC_TEXT_INLINE_BLOCK");
                            ParseTreeNode linkNode = new ParseTreeNode(CALTreeParserTokenTypes.CALDOC_TEXT_LINK, "CALDOC_TEXT_LINK");
                            ParseTreeNode contextNode = new ParseTreeNode(CALTreeParserTokenTypes.CALDOC_TEXT_LINK_WITHOUT_CONTEXT, "CALDOC_TEXT_LINK_WITHOUT_CONTEXT");
                            ParseTreeNode refNode = getAbstractCrossReference().toParseTreeNode(); 
                            
                            parentNode.addChild(inlineBlockNode);
                            inlineBlockNode.setFirstChild(linkNode);
                            linkNode.setFirstChild(contextNode);
                            contextNode.setFirstChild(refNode);
                        }
                        
                        /**
                         * {@inheritDoc}
                         */
                        final boolean spansMultipleLines() {
                            return false;
                        }
                        
                        /**
                         * @return the cross-reference encapsualted by this instance.
                         */
                        abstract CrossReference getAbstractCrossReference();
                    }
                    
                    /**
                     * Models a "@link" CALDoc inline tag segment without a context and containing an identifier
                     * starting with an uppercase character.
                     * 
                     * e.g {at-link Nothing at-}, {at-link Prelude.Int at-}, {at-link "Dynamic.Dynamic" at-}
                     *
                     * @author Joseph Wong
                     */
                    public static final class ConsNameWithoutContext extends WithoutContext {
                        
                        /**
                         * The cross-reference encapsulated by this instance.
                         */
                        private final CrossReference.WithoutContextCons reference;
                        
                        /**
                         * Creates a new source model element for representing a "@link" CALDoc inline tag segment
                         * without a context and containing an identifier starting with an uppercase character.
                         * 
                         * @param reference the cross-reference encapsulated by this instance.
                         * @param sourceRange
                         */
                        private ConsNameWithoutContext(CrossReference.WithoutContextCons reference, SourceRange sourceRange) {
                            super(sourceRange);
                            verifyArg(reference, "reference");
                            this.reference = reference;
                        }
                        
                        /**
                         * Factory method for constructing a new source model element for representing a
                         * "@link" CALDoc inline tag segment without a context and containing an identifier
                         * starting with an uppercase character.
                         * 
                         * @param reference the cross-reference encapsulated by this instance.
                         * @return a new instance of this class.
                         */
                        public static ConsNameWithoutContext make(CrossReference.WithoutContextCons reference) {
                            return new ConsNameWithoutContext(reference, null);
                        }
                        
                        static ConsNameWithoutContext makeAnnotated(CrossReference.WithoutContextCons reference, SourceRange sourceRange) {
                            return new ConsNameWithoutContext(reference, sourceRange);
                        }
                        
                        /**
                         * @return the cross-reference encapsualted by this instance.
                         */
                        public CrossReference.WithoutContextCons getReference() {
                            return reference;
                        }

                        /**
                         * {@inheritDoc}
                         */
                        CrossReference getAbstractCrossReference() {
                            return reference;
                        }

                        /**
                         * {@inheritDoc}
                         */
                        public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                            return visitor.visit_CALDoc_TextSegment_InlineTag_Link_ConsNameWithoutContext(this, arg);
                        }
                    }
                    
                    /**
                     * Models a "@link" CALDoc inline tag segment without a context and containing an identifier
                     * starting with a lowercase character.
                     * 
                     * e.g {at-link compare at-}, {at-link Prelude.greaterThan at-}, {at-link "Debug.trace" at-}
                     *
                     * @author Joseph Wong
                     */
                    public static final class FunctionWithoutContext extends WithoutContext {
                        
                        /**
                         * The cross-reference encapsulated by this instance.
                         */
                        private final CrossReference.Function reference;
                        
                        /**
                         * Creates a new source model element for representing a "@link" CALDoc inline tag segment
                         * without a context and containing an identifier starting with a lowercase character.
                         * 
                         * @param reference the cross-reference encapsulated by this instance.
                         */
                        private FunctionWithoutContext(CrossReference.Function reference) {
                            super(null);
                            verifyArg(reference, "reference");
                            this.reference = reference;
                        }
                        
                        /**
                         * Factory method for constructing a new source model element for representing a
                         * "@link" CALDoc inline tag segment without a context and containing an identifier
                         * starting with a lowercase character.
                         * 
                         * @param reference the cross-reference encapsulated by this instance.
                         * @return a new instance of this class.
                         */
                        public static FunctionWithoutContext make(CrossReference.Function reference) {
                            return new FunctionWithoutContext(reference);
                        }
                        
                        /**
                         * @return the cross-reference encapsualted by this instance.
                         */
                        public CrossReference.Function getReference() {
                            return reference;
                        }

                        /**
                         * {@inheritDoc}
                         */
                        CrossReference getAbstractCrossReference() {
                            return reference;
                        }

                        /**
                         * {@inheritDoc}
                         */
                        public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                            return visitor.visit_CALDoc_TextSegment_InlineTag_Link_FunctionWithoutContext(this, arg);
                        }
                    }
                    
                    /**
                     * Models a "@link" CALDoc inline tag segment with a context, i.e. currently one of:
                     * module, function, typeConstructor, dataConstructor, typeClass.
                     * 
                     * There are several different varieties of this inline tag segment, each corresponding to a different context
                     * under which the name is to be resolved, e.g. "{at-link module = Prelude at-}",
                     * or "{at-link typeClass = Prelude.Eq at-}". (Replace 'at-' with '@' in preceding examples.)
                     *
                     * @author Joseph Wong
                     */
                    public static abstract class WithContext extends Link {

                        private WithContext() {
                            super(null);
                        }
                        
                        /**
                         * {@inheritDoc}
                         */
                        final void addToParseTree(ParseTreeNode parentNode) {
                            ParseTreeNode inlineBlockNode = new ParseTreeNode(CALTreeParserTokenTypes.CALDOC_TEXT_INLINE_BLOCK, "CALDOC_TEXT_INLINE_BLOCK");
                            ParseTreeNode linkNode = new ParseTreeNode(CALTreeParserTokenTypes.CALDOC_TEXT_LINK, "CALDOC_TEXT_LINK");
                            ParseTreeNode contextNode = makeContextNode();
                            ParseTreeNode refNode = getAbstractCrossReference().toParseTreeNode(); 
                            
                            parentNode.addChild(inlineBlockNode);
                            inlineBlockNode.setFirstChild(linkNode);
                            linkNode.setFirstChild(contextNode);
                            contextNode.setFirstChild(refNode);
                        }
                        
                        /**
                         * {@inheritDoc}
                         */
                        final boolean spansMultipleLines() {
                            return false;
                        }


                        
                        /**
                         * @return the parse tree node for the context.
                         */
                        abstract ParseTreeNode makeContextNode();
                        
                        /**
                         * @return the context keyword.
                         */
                        abstract String getContextKeyword();
                        
                        /**
                         * @return the cross-reference encapsualted by this instance.
                         */
                        abstract CrossReference getAbstractCrossReference();
                    }
                    
                    /**
                     * Models a "@link function = ..." CALDoc inline tag segment.
                     *
                     * @author Joseph Wong
                     */
                    public static final class Function extends WithContext {
                        
                        /**
                         * The cross-reference encapsulated by this instance.
                         */
                        private final CrossReference.Function reference;
                        
                        /**
                         * Creates a new source model element for representing a "@link function = ..." CALDoc inline tag segment.
                         * 
                         * @param reference the cross-reference encapsulated by this instance.
                         */
                        private Function(CrossReference.Function reference) {
                            verifyArg(reference, "reference");
                            this.reference = reference;
                        }
                        
                        /**
                         * Factory method for constructing a new source model element for representing a 
                         * "@link function = ..." CALDoc inline tag segment.
                         * 
                         * @param reference the cross-reference encapsulated by this instance.
                         * @return a new instance of this class.
                         */
                        public static Function make(CrossReference.Function reference) {
                            return new Function(reference);
                        }
                        
                        /**
                         * @return the cross-reference encapsualted by this instance.
                         */
                        public CrossReference.Function getReference() {
                            return reference;
                        }

                        /**
                         * {@inheritDoc}
                         */
                        ParseTreeNode makeContextNode() {
                            return new ParseTreeNode(CALTreeParserTokenTypes.CALDOC_TEXT_LINK_FUNCTION, "CALDOC_TEXT_LINK_FUNCTION");
                        }

                        /**
                         * {@inheritDoc}
                         */
                        String getContextKeyword() {
                            return "function";
                        }

                        /**
                         * {@inheritDoc}
                         */
                        CrossReference getAbstractCrossReference() {
                            return reference;
                        }

                        /**
                         * {@inheritDoc}
                         */
                        public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                            return visitor.visit_CALDoc_TextSegment_InlineTag_Link_Function(this, arg);
                        }
                    }
                    
                    /**
                     * Models a "@link module = ..." CALDoc inline tag segment.
                     *
                     * @author Joseph Wong
                     */
                    public static final class Module extends WithContext {
                        
                        /**
                         * The cross-reference encapsulated by this instance.
                         */
                        private final CrossReference.Module reference;
                        
                        /**
                         * Creates a new source model element for representing a "@link module = ..." CALDoc inline tag segment.
                         * 
                         * @param reference the cross-reference encapsulated by this instance.
                         */
                        private Module(CrossReference.Module reference) {
                            verifyArg(reference, "reference");
                            this.reference = reference;
                        }
                        
                        /**
                         * Factory method for constructing a new source model element for representing a 
                         * "@link module = ..." CALDoc inline tag segment.
                         * 
                         * @param reference the cross-reference encapsulated by this instance.
                         * @return a new instance of this class.
                         */
                        public static Module make(CrossReference.Module reference) {
                            return new Module(reference);
                        }
                        
                        /**
                         * @return the cross-reference encapsualted by this instance.
                         */
                        public CrossReference.Module getReference() {
                            return reference;
                        }

                        /**
                         * {@inheritDoc}
                         */
                        ParseTreeNode makeContextNode() {
                            return new ParseTreeNode(CALTreeParserTokenTypes.CALDOC_TEXT_LINK_MODULE, "CALDOC_TEXT_LINK_MODULE");
                        }

                        /**
                         * {@inheritDoc}
                         */
                        String getContextKeyword() {
                            return "module";
                        }

                        /**
                         * {@inheritDoc}
                         */
                        CrossReference getAbstractCrossReference() {
                            return reference;
                        }

                        /**
                         * {@inheritDoc}
                         */
                        public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                            return visitor.visit_CALDoc_TextSegment_InlineTag_Link_Module(this, arg);
                        }
                    }
                    
                    /**
                     * Models a "@link typeConstructor = ..." CALDoc inline tag segment.
                     *
                     * @author Joseph Wong
                     */
                    public static final class TypeCons extends WithContext {
                        
                        /**
                         * The cross-reference encapsulated by this instance.
                         */
                        private final CrossReference.TypeCons reference;
                        
                        /**
                         * Creates a new source model element for representing a "@link typeConstructor = ..." CALDoc inline tag segment.
                         * 
                         * @param reference the cross-reference encapsulated by this instance.
                         */
                        private TypeCons(CrossReference.TypeCons reference) {
                            verifyArg(reference, "reference");
                            this.reference = reference;
                        }
                        
                        /**
                         * Factory method for constructing a new source model element for representing a 
                         * "@link typeConstructor = ..." CALDoc inline tag segment.
                         * 
                         * @param reference the cross-reference encapsulated by this instance.
                         * @return a new instance of this class.
                         */
                        public static TypeCons make(CrossReference.TypeCons reference) {
                            return new TypeCons(reference);
                        }
                        
                        /**
                         * @return the cross-reference encapsualted by this instance.
                         */
                        public CrossReference.TypeCons getReference() {
                            return reference;
                        }

                        /**
                         * {@inheritDoc}
                         */
                        ParseTreeNode makeContextNode() {
                            return new ParseTreeNode(CALTreeParserTokenTypes.CALDOC_TEXT_LINK_TYPECONS, "CALDOC_TEXT_LINK_TYPECONS");
                        }

                        /**
                         * {@inheritDoc}
                         */
                        String getContextKeyword() {
                            return "typeConstructor";
                        }

                        /**
                         * {@inheritDoc}
                         */
                        CrossReference getAbstractCrossReference() {
                            return reference;
                        }

                        /**
                         * {@inheritDoc}
                         */
                        public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                            return visitor.visit_CALDoc_TextSegment_InlineTag_Link_TypeCons(this, arg);
                        }
                    }
                    
                    /**
                     * Models a "@link dataConstructor = ..." CALDoc inline tag segment.
                     *
                     * @author Joseph Wong
                     */
                    public static final class DataCons extends WithContext {
                        
                        /**
                         * The cross-reference encapsulated by this instance.
                         */
                        private final CrossReference.DataCons reference;
                        
                        /**
                         * Creates a new source model element for representing a "@link dataConstructor = ..." CALDoc inline tag segment.
                         * 
                         * @param reference the cross-reference encapsulated by this instance.
                         */
                        private DataCons(CrossReference.DataCons reference) {
                            verifyArg(reference, "reference");
                            this.reference = reference;
                        }
                        
                        /**
                         * Factory method for constructing a new source model element for representing a 
                         * "@link dataConstructor = ..." CALDoc inline tag segment.
                         * 
                         * @param reference the cross-reference encapsulated by this instance.
                         * @return a new instance of this class.
                         */
                        public static DataCons make(CrossReference.DataCons reference) {
                            return new DataCons(reference);
                        }
                        
                        /**
                         * @return the cross-reference encapsualted by this instance.
                         */
                        public CrossReference.DataCons getReference() {
                            return reference;
                        }

                        /**
                         * {@inheritDoc}
                         */
                        ParseTreeNode makeContextNode() {
                            return new ParseTreeNode(CALTreeParserTokenTypes.CALDOC_TEXT_LINK_DATACONS, "CALDOC_TEXT_LINK_DATACONS");
                        }

                        /**
                         * {@inheritDoc}
                         */
                        String getContextKeyword() {
                            return "dataConstructor";
                        }

                        /**
                         * {@inheritDoc}
                         */
                        CrossReference getAbstractCrossReference() {
                            return reference;
                        }

                        /**
                         * {@inheritDoc}
                         */
                        public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                            return visitor.visit_CALDoc_TextSegment_InlineTag_Link_DataCons(this, arg);
                        }
                    }
                    
                    /**
                     * Models a "@link typeClass = ..." CALDoc inline tag segment.
                     *
                     * @author Joseph Wong
                     */
                    public static final class TypeClass extends WithContext {
                        
                        /**
                         * The cross-reference encapsulated by this instance.
                         */
                        private final CrossReference.TypeClass reference;
                        
                        /**
                         * Creates a new source model element for representing a "@link typeClass = ..." CALDoc inline tag segment.
                         * 
                         * @param reference the cross-reference encapsulated by this instance.
                         */
                        private TypeClass(CrossReference.TypeClass reference) {
                            verifyArg(reference, "reference");
                            this.reference = reference;
                        }
                        
                        /**
                         * Factory method for constructing a new source model element for representing a 
                         * "@link typeClass = ..." CALDoc inline tag segment.
                         * 
                         * @param reference the cross-reference encapsulated by this instance.
                         * @return a new instance of this class.
                         */
                        public static TypeClass make(CrossReference.TypeClass reference) {
                            return new TypeClass(reference);
                        }
                        
                        /**
                         * @return the cross-reference encapsualted by this instance.
                         */
                        public CrossReference.TypeClass getReference() {
                            return reference;
                        }

                        /**
                         * {@inheritDoc}
                         */
                        ParseTreeNode makeContextNode() {
                            return new ParseTreeNode(CALTreeParserTokenTypes.CALDOC_TEXT_LINK_TYPECLASS, "CALDOC_TEXT_LINK_TYPECLASS");
                        }

                        /**
                         * {@inheritDoc}
                         */
                        String getContextKeyword() {
                            return "typeClass";
                        }

                        /**
                         * {@inheritDoc}
                         */
                        CrossReference getAbstractCrossReference() {
                            return reference;
                        }

                        /**
                         * {@inheritDoc}
                         */
                        public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                            return visitor.visit_CALDoc_TextSegment_InlineTag_Link_TypeClass(this, arg);
                        }
                    }
                
                    private Link(SourceRange sourceRange) {
                        super(sourceRange);
                    }
                }
                
                /**
                 * Base class for an inline tag segment for text formatting in CALDoc.
                 *
                 * @author Joseph Wong
                 */
                public static abstract class TextFormatting extends InlineTagWithTextBlockContent {
                    
                    /**
                     * Private constructor for this base class for inline tag segments for text formatting.
                     * Intended only to be invoked by subclass constructors.
                     * 
                     * @param content the text block which forms the content of this segment.
                     */
                    private TextFormatting(TextBlock content) {
                        super(content);
                    }
                    
                    /**
                     * Models an "@em" CALDoc inline tag segment. This represents an emphasized piece of text.
                     *
                     * @author Joseph Wong
                     */
                    public static final class Emphasized extends TextFormatting {
                        
                        /**
                         * The name of the "@em" tag.
                         */
                        public static final String EM_TAG = "em";
                        
                        /**
                         * Creates a new source model element for representing an "@em" CALDoc inline tag segment.
                         * 
                         * @param content the text block which forms the content of this segment.
                         */
                        private Emphasized(TextBlock content) {
                            super(content);
                        }
                        
                        /**
                         * Factory method for constructing a new source model element for representing 
                         * an "@em" CALDoc inline tag segment.
                         * 
                         * @param content the text block which forms the content of this segment.
                         * @return a new instance of this class.
                         */
                        public static Emphasized make(TextBlock content) {
                            return new Emphasized(content);
                        }

                        /**
                         * {@inheritDoc}
                         */
                        public String getTagName() {
                            return EM_TAG;
                        }
                        
                        /**
                         * {@inheritDoc}
                         */
                        ParseTreeNode makeInlineTagNode() {
                            return new ParseTreeNode(CALTreeParserTokenTypes.CALDOC_TEXT_EMPHASIZED_TEXT, "CALDOC_TEXT_EMPHASIZED_TEXT");
                        }

                        /**
                         * {@inheritDoc}
                         */
                        public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                            return visitor.visit_CALDoc_TextSegment_InlineTag_TextFormatting_Emphasized(this, arg);
                        }
                    }
                    
                    /**
                     * Models a "@strong" CALDoc inline tag segment. This represents a strongly emphasized piece of text.
                     *
                     * @author Joseph Wong
                     */
                    public static final class StronglyEmphasized extends TextFormatting {
                        
                        /**
                         * The name of the "@strong" tag.
                         */
                        public static final String STRONG_TAG = "strong";
                        
                        /**
                         * Creates a new source model element for representing a "@strong" CALDoc inline tag segment.
                         * 
                         * @param content the text block which forms the content of this segment.
                         */
                        private StronglyEmphasized(TextBlock content) {
                            super(content);
                        }
                        
                        /**
                         * Factory method for constructing a new source model element for representing 
                         * a "@strong" CALDoc inline tag segment.
                         * 
                         * @param content the text block which forms the content of this segment.
                         * @return a new instance of this class.
                         */
                        public static StronglyEmphasized make(TextBlock content) {
                            return new StronglyEmphasized(content);
                        }

                        /**
                         * {@inheritDoc}
                         */
                        public String getTagName() {
                            return STRONG_TAG;
                        }
                        
                        /**
                         * {@inheritDoc}
                         */
                        ParseTreeNode makeInlineTagNode() {
                            return new ParseTreeNode(CALTreeParserTokenTypes.CALDOC_TEXT_STRONGLY_EMPHASIZED_TEXT, "CALDOC_TEXT_STRONGLY_EMPHASIZED_TEXT");
                        }

                        /**
                         * {@inheritDoc}
                         */
                        public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                            return visitor.visit_CALDoc_TextSegment_InlineTag_TextFormatting_StronglyEmphasized(this, arg);
                        }
                    }
                    
                    /**
                     * Models a "@sup" CALDoc inline tag segment. This represents a superscripted piece of text.
                     *
                     * @author Joseph Wong
                     */
                    public static final class Superscript extends TextFormatting {
                        
                        /**
                         * The name of the "@sup" tag.
                         */
                        public static final String SUP_TAG = "sup";
                        
                        /**
                         * Creates a new source model element for representing a "@sup" CALDoc inline tag segment.
                         * 
                         * @param content the text block which forms the content of this segment.
                         */
                        private Superscript(TextBlock content) {
                            super(content);
                        }
                        
                        /**
                         * Factory method for constructing a new source model element for representing 
                         * a "@sup" CALDoc inline tag segment.
                         * 
                         * @param content the text block which forms the content of this segment.
                         * @return a new instance of this class.
                         */
                        public static Superscript make(TextBlock content) {
                            return new Superscript(content);
                        }

                        /**
                         * {@inheritDoc}
                         */
                        public String getTagName() {
                            return SUP_TAG;
                        }
                        
                        /**
                         * {@inheritDoc}
                         */
                        ParseTreeNode makeInlineTagNode() {
                            return new ParseTreeNode(CALTreeParserTokenTypes.CALDOC_TEXT_SUPERSCRIPT_TEXT, "CALDOC_TEXT_SUPERSCRIPT_TEXT");
                        }

                        /**
                         * {@inheritDoc}
                         */
                        public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                            return visitor.visit_CALDoc_TextSegment_InlineTag_TextFormatting_Superscript(this, arg);
                        }
                    }
                    
                    /**
                     * Models a "@sub" CALDoc inline tag segment. This represents a subscripted piece of text.
                     *
                     * @author Joseph Wong
                     */
                    public static final class Subscript extends TextFormatting {
                        
                        /**
                         * The name of the "@sub" tag.
                         */
                        public static final String SUB_TAG = "sub";
                        
                        /**
                         * Creates a new source model element for representing a "@sub" CALDoc inline tag segment.
                         * 
                         * @param content the text block which forms the content of this segment.
                         */
                        private Subscript(TextBlock content) {
                            super(content);
                        }
                        
                        /**
                         * Factory method for constructing a new source model element for representing 
                         * a "@sub" CALDoc inline tag segment.
                         * 
                         * @param content the text block which forms the content of this segment.
                         * @return a new instance of this class.
                         */
                        public static Subscript make(TextBlock content) {
                            return new Subscript(content);
                        }

                        /**
                         * {@inheritDoc}
                         */
                        public String getTagName() {
                            return SUB_TAG;
                        }
                        
                        /**
                         * {@inheritDoc}
                         */
                        ParseTreeNode makeInlineTagNode() {
                            return new ParseTreeNode(CALTreeParserTokenTypes.CALDOC_TEXT_SUBSCRIPT_TEXT, "CALDOC_TEXT_SUBSCRIPT_TEXT");
                        }

                        /**
                         * {@inheritDoc}
                         */
                        public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                            return visitor.visit_CALDoc_TextSegment_InlineTag_TextFormatting_Subscript(this, arg);
                        }
                    }
                }
                
                /**
                 * Models a "@summary" CALDoc inline tag segment. This represents a piece of text to be included in the
                 * CALDoc comment's summary.
                 *
                 * @author Joseph Wong
                 */
                public static final class Summary extends InlineTagWithTextBlockContent {
                    
                    /**
                     * The name of the "@summary" tag.
                     */
                    public static final String SUMMARY_TAG = "summary";

                    /**
                     * Creates a new source model element for representing a "@summary" CALDoc inline tag segment.
                     * 
                     * @param content the text block which forms the content of this segment.
                     */
                    private Summary(TextBlock content) {
                        super(content);
                    }
                    
                    /**
                     * Factory method for constructing a new source model element for representing 
                     * a "@summary" CALDoc inline tag segment.
                     * 
                     * @param content the text block which forms the content of this segment.
                     * @return a new instance of this class.
                     */
                    public static Summary make(TextBlock content) {
                        return new Summary(content);
                    }
                    
                    /**
                     * {@inheritDoc}
                     */
                    public String getTagName() {
                        return SUMMARY_TAG;
                    }

                    /**
                     * {@inheritDoc}
                     */
                    ParseTreeNode makeInlineTagNode() {
                        return new ParseTreeNode(CALTreeParserTokenTypes.CALDOC_TEXT_SUMMARY, "CALDOC_TEXT_SUMMARY");
                    }

                    /**
                     * {@inheritDoc}
                     */
                    public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                        return visitor.visit_CALDoc_TextSegment_InlineTag_Summary(this, arg);
                    }
                }
                
                /**
                 * Models a "@code" CALDoc inline tag segment. This represents a block of preformatted code in a CALDoc comment.
                 *
                 * @author Joseph Wong
                 */
                public static final class Code extends InlineTagWithPreformattedContent {
                    
                    /**
                     * The name of the "@code" tag.
                     */
                    public static final String CODE_TAG = "code";
                    
                    /**
                     * Creates a new source model element for representing a "@code" CALDoc inline tag segment.
                     * 
                     * @param content the preformatted text segment which forms the content of this segment.
                     */
                    private Code(Preformatted content) {
                        super(content);
                    }
                    
                    /**
                     * Factory method for constructing a new source model element for representing 
                     * a "@code" CALDoc inline tag segment.
                     * 
                     * @param content the preformatted text segment which forms the content of this segment.
                     * @return a new instance of this class.
                     */
                    public static Code make(Preformatted content) {
                        return new Code(content);
                    }

                    /**
                     * {@inheritDoc}
                     */
                    public String getTagName() {
                        return CODE_TAG;
                    }

                    /**
                     * {@inheritDoc}
                     */
                    ParseTreeNode makeInlineTagNode() {
                        return new ParseTreeNode(CALTreeParserTokenTypes.CALDOC_TEXT_CODE_BLOCK, "CALDOC_TEXT_CODE_BLOCK");
                    }

                    /**
                     * {@inheritDoc}
                     */
                    public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                        return visitor.visit_CALDoc_TextSegment_InlineTag_Code(this, arg);
                    }
                    
                }
                
                /**
                 * Base class for an inline tag segment for representing a list in a CALDoc comment.
                 *
                 * @author Joseph Wong
                 */
                public static abstract class List extends InlineTag {
                    
                    /**
                     * The items contained in the list.
                     */
                    private final Item[] items;
                    
                    /** An empty Item array. */
                    public static final Item[] NO_ITEMS = new Item[0];
                    
                    /**
                     * Models an unordered list in a CALDoc comment.
                     *
                     * @author Joseph Wong
                     */
                    public static final class Unordered extends List {
                        
                        /**
                         * The name of the inline tag.
                         */
                        public static final String UNORDERED_LIST_TAG = "unorderedList";
                        
                        /**
                         * Creates a new source model element for representing an unordered list in a CALDoc comment.
                         * 
                         * @param items the items contained in the list.
                         */
                        private Unordered(Item[] items) {
                            super(items);
                        }
                        
                        /**
                         * Factory method for constructing a new source model element for representing an unordered list
                         * in a CALDoc comment.
                         * 
                         * @param items the items contained in the list.
                         * @return a new instance of this class.
                         */
                        public static Unordered make(Item[] items) {
                            return new Unordered(items);
                        }

                        /**
                         * {@inheritDoc}
                         */
                        public String getTagName() {
                            return UNORDERED_LIST_TAG;
                        }

                        /**
                         * {@inheritDoc}
                         */
                        ParseTreeNode makeInlineTagNode() {
                            return new ParseTreeNode(CALTreeParserTokenTypes.CALDOC_TEXT_UNORDERED_LIST, "CALDOC_TEXT_UNORDERED_LIST");
                        }

                        /**
                         * {@inheritDoc}
                         */
                        public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                            return visitor.visit_CALDoc_TextSegment_InlineTag_List_Unordered(this, arg);
                        }
                    }
                    
                    /**
                     * Models an ordered list in a CALDoc comment.
                     *
                     * @author Joseph Wong
                     */
                    public static final class Ordered extends List {
                        
                        /**
                         * The name of the inline tag.
                         */
                        public static final String ORDERED_LIST_TAG = "orderedList";
                        
                        /**
                         * Creates a new source model element for representing an ordered list in a CALDoc comment.
                         * 
                         * @param items the items contained in the list.
                         */
                        private Ordered(Item[] items) {
                            super(items);
                        }
                        
                        /**
                         * Factory method for constructing a new source model element for representing an ordered list
                         * in a CALDoc comment.
                         * 
                         * @param items the items contained in the list.
                         * @return a new instance of this class.
                         */
                        public static Ordered make(Item[] items) {
                            return new Ordered(items);
                        }

                        /**
                         * {@inheritDoc}
                         */
                        public String getTagName() {
                            return ORDERED_LIST_TAG;
                        }

                        /**
                         * {@inheritDoc}
                         */
                        ParseTreeNode makeInlineTagNode() {
                            return new ParseTreeNode(CALTreeParserTokenTypes.CALDOC_TEXT_ORDERED_LIST, "CALDOC_TEXT_ORDERED_LIST");
                        }

                        /**
                         * {@inheritDoc}
                         */
                        public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                            return visitor.visit_CALDoc_TextSegment_InlineTag_List_Ordered(this, arg);
                        }
                    }
                    
                    /**
                     * Models a list item in a CALDoc comment.
                     *
                     * @author Joseph Wong
                     */
                    public static final class Item extends TextSegment {
                        /**
                         * The text block which forms the content of this list item.
                         */
                        private final TextBlock content;
                        
                        /**
                         * Creates a new source model element for representing a list item in a CALDoc comment.
                         * 
                         * @param content the text block which forms the content of this list item.
                         */
                        private Item(TextBlock content) {
                            super(null);
                            verifyArg(content, "content");
                            this.content = content;
                        }
                        
                        /**
                         * Factory method for constructing a new source model element for representing a list item
                         * in a CALDoc comment.
                         * 
                         * @param content the text block which forms the content of this list item.
                         * @return a new instance of this class.
                         */
                        public static Item make(TextBlock content) {
                            return new Item(content);
                        }
                        
                        /**
                         * @return the text block which forms the content of this segment.
                         */
                        public TextBlock getContent() {
                            return content;
                        }
                        
                        /**
                         * {@inheritDoc}
                         */
                        void addToParseTree(ParseTreeNode parentNode) {
                            ParseTreeNode itemNode = new ParseTreeNode(CALTreeParserTokenTypes.CALDOC_TEXT_LIST_ITEM, "CALDOC_TEXT_LIST_ITEM");
                            ParseTreeNode contentNode = content.toParseTreeNode();
                            
                            parentNode.addChild(itemNode);
                            itemNode.setFirstChild(contentNode);
                        }

                        /**
                         * {@inheritDoc}
                         */
                        boolean spansMultipleLines() {
                            return content.spansMultipleLines();
                        }

                        /**
                         * {@inheritDoc}
                         */
                        public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                            return visitor.visit_CALDoc_TextSegment_InlineTag_List_Item(this, arg);
                        }
                    }
                    
                    /**
                     * Private constructor for this base class for inline tag segments representing lists
                     * in a CALDoc comment. Intended only to be invoked by subclass constructors.
                     * 
                     * @param items the items contained in the list.
                     */
                    private List(Item[] items) {
                        super(null);
                        if (items == null || items.length == 0) {
                            this.items = NO_ITEMS;
                        } else {
                            this.items = items.clone();
                            verifyArrayArg(this.items, "items");                                       
                        }
                    }
                    
                    /**
                     * @return the items in the list.
                     */
                    public final Item[] getItems() {
                        
                        if (items.length == 0) {
                            return NO_ITEMS;
                        }
                        
                        return items.clone();
                    }
                    
                    /**
                     * @return the number of items in the list.
                     */
                    public final int getNItems() {
                        return items.length;
                    }
                    
                    /**
                     * Returns the item at the specified position in the array of items.
                     * @param n the index of the item to return.
                     * @return the item at the specified position in the array of items.
                     */
                    public final Item getNthItem(int n) {
                        return items[n];
                    }

                    /**
                     * @return the name of the inline tag.
                     */
                    public abstract String getTagName();
                    
                    /**
                     * @return the parse tree node for the inline tag itself.
                     * (In particular the first child of the CALDOC_TEXT_INLINE_BLOCK node.)
                     */
                    abstract ParseTreeNode makeInlineTagNode();
                    
 
                    /**
                     * {@inheritDoc}
                     */
                    final void addToParseTree(ParseTreeNode parentNode) {
                        ParseTreeNode inlineBlockNode = new ParseTreeNode(CALTreeParserTokenTypes.CALDOC_TEXT_INLINE_BLOCK, "CALDOC_TEXT_INLINE_BLOCK");
                        ParseTreeNode inlineTagNode = makeInlineTagNode();
                        
                        parentNode.addChild(inlineBlockNode);
                        inlineBlockNode.setFirstChild(inlineTagNode);
                        for (final Item item : items) {
                            item.addToParseTree(inlineTagNode);
                        }
                    }

                    /**
                     * {@inheritDoc}
                     */
                    final boolean spansMultipleLines() {
                        return true;
                    }
                }

                private InlineTag(SourceRange sourceRange) {
                    super(sourceRange);
                }
            }
            
            private TextSegment(SourceRange sourceRange) {
                super(sourceRange);
            }
            
            /**
             * {@inheritDoc}
             */
            final ParseTreeNode toParseTreeNode() {
                ParseTreeNode listNode = new ParseTreeNode(CALTreeParserTokenTypes.CALDOC_TEXT, "CALDOC_TEXT");
                addToParseTree(listNode);
                return listNode.firstChild();
            }
            
            /**
             * Adds to the given parse tree node the subtree representing this instance.
             * @param parentNode the node to which the subtree is to be added as a child.
             */
            abstract void addToParseTree(ParseTreeNode parentNode);
            
            /**
             * @return whether the comment text spans multiple source lines.
             */
            abstract boolean spansMultipleLines();
        }
        
        /**
         * Models a block of text segments within a CALDoc comment. The description block of
         * a CALDoc comment is a TextBlock, and so are the descriptions contained with
         * a number of the tagged blocks.
         *
         * @author Joseph Wong
         */
        public static final class TextBlock extends InteriorIndentable {
            
            /**
             * The text segments which constitute this text block.
             */
            private final TextSegment.TopLevel[] segments;
            
            /**
             * Creates a new source model element for representing a block of text segments within a CALDoc comment.
             * 
             * @param segments the segments which constitute this text block.
             */
            private TextBlock(TextSegment.TopLevel[] segments) {
                super(null);
                if (segments == null || segments.length == 0) {
                    this.segments = TextSegment.TopLevel.NO_SEGMENTS;
                } else {
                    this.segments = segments.clone();
                    verifyArrayArg(this.segments, "segments");                                       
                }
            }
            
            /**
             * Factory method for constructing a new source model element for representing a block of text segments
             * within a CALDoc comment.
             * 
             * @param segments the segments which constitute this text block.
             * @return a new instance of this class.
             */
            public static TextBlock make(TextSegment.TopLevel[] segments) {
                return new TextBlock(segments);
            }
            
            /**
             * @return the text segments which constitute this text block.
             */
            public TextSegment.TopLevel[] getSegments() {
                
                if (segments.length == 0) {
                    return TextSegment.TopLevel.NO_SEGMENTS;
                }
                
                return segments.clone();
            }
            
            /**
             * @return the number of text segments in this text block.
             */
            public int getNSegments() {
                return segments.length;
            }
            
            /**
             * Returns the text segment at the specified position in the array of constituent segments.
             * @param n the index of the segment to return.
             * @return the text segment at the specified position in the array of constituent segments.
             */
            public TextSegment.TopLevel getNthSegment(int n) {
                return segments[n];
            }
            
            /**
             * @return whether the comment text spans multiple source lines.
             */
            public boolean spansMultipleLines() {
                for (final TopLevel segment : segments) {
                    if (segment.spansMultipleLines()) {
                        return true;
                    }
                }
                return false;
            }

            /**
             * {@inheritDoc}
             */
            ParseTreeNode toParseTreeNode() {
                ParseTreeNode textNode = new ParseTreeNode(CALTreeParserTokenTypes.CALDOC_TEXT, "CALDOC_TEXT");
                
                for (final TopLevel segment : segments) {
                    segment.addToParseTree(textNode);
                }
                
                return textNode;
            }

            /**
             * {@inheritDoc}
             */
            public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                return visitor.visit_CALDoc_TextBlock(this, arg);
            }
        }
        
        /**
         * Models a tagged block in a CALDoc comment. A tagged block is a special block
         * which starts with a tag, and a tag is one of a few special keywords within the
         * CALDoc grammar, and all tags start with the character '@'.
         *
         * @author Joseph Wong
         */
        public static abstract class TaggedBlock extends CALDoc {
            
            private TaggedBlock(SourceRange sourceRange) {
                super(sourceRange);
            }
            
            /**
             * @return the string representation of the tag encapsulated by this class.
             */
            public abstract String getTagName();
            
            /**
             * Models a common kind of tagged blocks in CALDoc: one which ends with a text block.
             *
             * @author Joseph Wong
             */
            public static abstract class TaggedBlockWithText extends TaggedBlock {
                
                /** The text block that forms the trailing portion of this tagged block. */
                private final TextBlock textBlock;
                
                /**
                 * Private constructor for this base class for tagged blocks in
                 * CALDoc that ends with a text block. Intended only to be
                 * invoked by subclass constructors.
                 * 
                 * @param textBlock
                 *            the text block that forms the trailing portion of
                 *            this tagged block.
                 */
                private TaggedBlockWithText(TextBlock textBlock) {
                    super(null);
                    verifyArg(textBlock, "textBlock");
                    this.textBlock = textBlock;
                }
                
                /**
                 * @return the text block that forms the trailing portion of this tagged block. 
                 */
                public TextBlock getTextBlock() {
                    return textBlock;
                }
            }
            
            /**
             * Models the "@author" CALDoc tag.
             *
             * @author Joseph Wong
             */
            public static final class Author extends TaggedBlockWithText {
                
                /** The string representation of the "@author" tag. */
                public static final String AUTHOR_TAG = "@author";

                /**
                 * Creates a source model element that represents an "@author"
                 * block in a CALDoc comment.
                 * 
                 * @param textBlock
                 *            the text block that forms the trailing portion of
                 *            this tagged block.
                 */
                private Author(TextBlock textBlock) {
                    super(textBlock);
                }
                
                /**
                 * Factory method for constructing a source model element that represents an "@author"
                 * block in a CALDoc comment.
                 * 
                 * @param textBlock
                 *            the text block that forms the trailing portion of
                 *            this tagged block.
                 * @return a new instance of this class.
                 */
                public static Author make(TextBlock textBlock) {
                    return new Author(textBlock);
                }
                
                /**
                 * {@inheritDoc}
                 */
                public String getTagName() {
                    return AUTHOR_TAG;
                }

                /**
                 * {@inheritDoc}
                 */
                public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                    return visitor.visit_CALDoc_TaggedBlock_Author(this, arg);
                }


                /**
                 * {@inheritDoc}
                 */
                ParseTreeNode toParseTreeNode() {
                    ParseTreeNode authorNode = new ParseTreeNode(CALTreeParserTokenTypes.CALDOC_AUTHOR_BLOCK, "CALDOC_AUTHOR_BLOCK");
                    authorNode.setFirstChild(getTextBlock().toParseTreeNode());
                    
                    return authorNode;
                }
            }
            
            /**
             * Models the "@deprecated" CALDoc tag.
             *
             * @author Joseph Wong
             */
            public static final class Deprecated extends TaggedBlockWithText {
                
                /** The string representation of the "@deprecated" tag. */
                public static final String DEPRECATED_TAG = "@deprecated";

                /**
                 * Creates a source model element that represents an "@deprecated"
                 * block in a CALDoc comment.
                 * 
                 * @param textBlock
                 *            the text block that forms the trailing portion of
                 *            this tagged block.
                 */
                private Deprecated(TextBlock textBlock) {
                    super(textBlock);
                }
                
                /**
                 * Factory method for constructing a source model element that represents an "@deprecated"
                 * block in a CALDoc comment.
                 * 
                 * @param textBlock
                 *            the text block that forms the trailing portion of
                 *            this tagged block.
                 * @return a new instance of this class.
                 */
                public static Deprecated make(TextBlock textBlock) {
                    return new Deprecated(textBlock);
                }
                
                /**
                 * {@inheritDoc}
                 */
                public String getTagName() {
                    return DEPRECATED_TAG;
                }

                /**
                 * {@inheritDoc}
                 */
                public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                    return visitor.visit_CALDoc_TaggedBlock_Deprecated(this, arg);
                }

 

                /**
                 * {@inheritDoc}
                 */
                ParseTreeNode toParseTreeNode() {
                    ParseTreeNode deprecatedNode = new ParseTreeNode(CALTreeParserTokenTypes.CALDOC_DEPRECATED_BLOCK, "CALDOC_DEPRECATED_BLOCK");
                    deprecatedNode.setFirstChild(getTextBlock().toParseTreeNode());
                    
                    return deprecatedNode;
                }
            }
            
            /**
             * Models the "@return" CALDoc tag.
             *
             * @author Joseph Wong
             */
            public static final class Return extends TaggedBlockWithText {
                
                /** The string representation of the "@return" tag. */
                public static final String RETURN_TAG = "@return";

                /**
                 * Creates a source model element that represents an "@return"
                 * block in a CALDoc comment.
                 * 
                 * @param textBlock
                 *            the text block that forms the trailing portion of
                 *            this tagged block.
                 */
                private Return(TextBlock textBlock) {
                    super(textBlock);
                }
                
                /**
                 * Factory method for constructing a source model element that represents an "@return"
                 * block in a CALDoc comment.
                 * 
                 * @param textBlock
                 *            the text block that forms the trailing portion of
                 *            this tagged block.
                 * @return a new instance of this class.
                 */
                public static Return make(TextBlock textBlock) {
                    return new Return(textBlock);
                }
                
                /**
                 * {@inheritDoc}
                 */
                public String getTagName() {
                    return RETURN_TAG;
                }

                /**
                 * {@inheritDoc}
                 */
                public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                    return visitor.visit_CALDoc_TaggedBlock_Return(this, arg);
                }


                /**
                 * {@inheritDoc}
                 */
                ParseTreeNode toParseTreeNode() {
                    ParseTreeNode returnNode = new ParseTreeNode(CALTreeParserTokenTypes.CALDOC_RETURN_BLOCK, "CALDOC_RETURN_BLOCK");
                    returnNode.setFirstChild(getTextBlock().toParseTreeNode());
                    
                    return returnNode;
                }
            }
            
            /**
             * Models the "@version" CALDoc tag.
             *
             * @author Joseph Wong
             */
            public static final class Version extends TaggedBlockWithText {
                
                /** The string representation of the "@version" tag. */
                public static final String VERSION_TAG = "@version";
                
                /**
                 * Creates a source model element that represents an "@version"
                 * block in a CALDoc comment.
                 * 
                 * @param textBlock
                 *            the text block that forms the trailing portion of
                 *            this tagged block.
                 */
                private Version(TextBlock textBlock) {
                    super(textBlock);
                }
                
                /**
                 * Factory method for constructing a source model element that represents an "@version"
                 * block in a CALDoc comment.
                 * 
                 * @param textBlock
                 *            the text block that forms the trailing portion of
                 *            this tagged block.
                 * @return a new instance of this class.
                 */
                public static Version make(TextBlock textBlock) {
                    return new Version(textBlock);
                }
                
                /**
                 * {@inheritDoc}
                 */
                public String getTagName() {
                    return VERSION_TAG;
                }

                /**
                 * {@inheritDoc}
                 */
                public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                    return visitor.visit_CALDoc_TaggedBlock_Version(this, arg);
                }

 
                /**
                 * {@inheritDoc}
                 */
                ParseTreeNode toParseTreeNode() {
                    ParseTreeNode versionNode = new ParseTreeNode(CALTreeParserTokenTypes.CALDOC_VERSION_BLOCK, "CALDOC_VERSION_BLOCK");
                    versionNode.setFirstChild(getTextBlock().toParseTreeNode());
                    
                    return versionNode;
                }
            }
            
            /**
             * Models the "@arg" CALDoc tag. The tagged block contains a reference to an argument name,
             * and is therefore valid only for a CALDoc function comment or a CALDoc data constructor
             * comment.
             *
             * @author Joseph Wong
             */
            public static final class Arg extends TaggedBlockWithText {
                
                /** The string representation of the "@arg" tag. */
                public static final String ARG_TAG = "@arg";
                
                /** The name of the argument referenced by this "@arg" tag. */
                private final Name.Field argName;
                
                /**
                 * Creates a source model element that represents an "@arg"
                 * block in a CALDoc comment.
                 * 
                 * @param argName
                 *            the name of the argument referenced by this tagged block.
                 * @param textBlock
                 *            the text block that forms the trailing portion of
                 *            this tagged block.
                 */
                private Arg(Name.Field argName, TextBlock textBlock) {
                    super(textBlock);
                    
                    this.argName = argName;
                }
                
                /**
                 * Factory method for constructing a source model element that represents an "@arg"
                 * block in a CALDoc comment.
                 * 
                 * @param argName
                 *            the name of the argument referenced by this tagged block.
                 * @param textBlock
                 *            the text block that forms the trailing portion of
                 *            this tagged block.
                 * @return a new instance of this class.
                 */
                public static Arg make(Name.Field argName, TextBlock textBlock) {
                    return new Arg(argName, textBlock);
                }
                
                /**
                 * {@inheritDoc}
                 */
                public String getTagName() {
                    return ARG_TAG;
                }
                
                /**
                 * @return the name of the argument referenced by this "@arg" tag. 
                 */
                public Name.Field getArgName() {
                    return argName;
                }

                /**
                 * {@inheritDoc}
                 */
                public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                    return visitor.visit_CALDoc_TaggedBlock_Arg(this, arg);
                }

                /**
                 * {@inheritDoc}
                 */
                ParseTreeNode toParseTreeNode() {
                    ParseTreeNode argNode = new ParseTreeNode(CALTreeParserTokenTypes.CALDOC_ARG_BLOCK, "CALDOC_ARG_BLOCK");
                    ParseTreeNode argNameNode = argName.toParseTreeNode();
                    
                    argNode.setFirstChild(argNameNode);
                    argNameNode.setNextSibling(getTextBlock().toParseTreeNode());
                    
                    return argNode;
                }
            }
            
            /**
             * Models the "@see" CALDoc tag. There are several different varieties of this
             * tagged block, each corresponding to a different context under which
             * the listed names are to be resolved, e.g. "@see module = Prelude, Debug",
             * or "@see typeClass = Prelude.Eq, Debug.Show". A "@see" block can also appear
             * without the context keyword, e.g. "@see List.map".
             *
             * @author Joseph Wong
             */
            public static abstract class See extends TaggedBlock {
                
                private See(SourceRange sourceRange) {
                    super(sourceRange);
                }
                
                /** The string representation of the "@see" tag. */
                public static final String SEE_TAG = "@see";
                
                /**
                 * {@inheritDoc}
                 */
                public String getTagName() {
                    return SEE_TAG;
                }
                
                
                /**
                 * Models a CALDoc "@see function = ..." block.
                 *
                 * @author Joseph Wong
                 */
                public static final class Function extends See {
                    
                    /**
                     * The non-empty array of function names.
                     */
                    private final CrossReference.Function[] functionNames;
                    
                    /**
                     * Creates a source model element for representing a CALDoc "@see function = ..." block.
                     * 
                     * @param functionNames a non-empty array of function names referenced by this "@see" block.
                     */
                    private Function(CrossReference.Function[] functionNames) {
                        super(null);
                        
                        if (functionNames.length == 0) {
                            throw new IllegalArgumentException("Must have at least one name in the @see function list");
                        }
                        this.functionNames = functionNames.clone();
                        verifyArrayArg(this.functionNames, "functionNames");
                    }
                    
                    /**
                     * Factory method for constructing a source model element for representing a CALDoc "@see function = ..." block.
                     * 
                     * @param functionNames a non-empty array of function names referenced by this "@see" block.
                     * @return a new instance of this class.
                     */
                    public static Function make(CrossReference.Function[] functionNames) {
                        return new Function(functionNames);
                    }
                    
                    /**
                     * @return the non-empty array of function names referenced by this "@see" block.
                     */
                    public CrossReference.Function[] getFunctionNames() {
                        return functionNames.clone();
                    }
                    
                    /**
                     * @return the number of function names referenced by this "@see" block.
                     */
                    public int getNFunctionNames() {
                        return functionNames.length;
                    }
                    
                    /**
                     * Returns the function name at the specified position in this "@see" block.
                     * @param n index of the function name to return.
                     * @return the function name at the specified position in this "@see" block.
                     */
                    public CrossReference.Function getNthFunctionName(int n) {
                        return functionNames[n];
                    }

                    /**
                     * {@inheritDoc}
                     */
                    public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                        return visitor.visit_CALDoc_TaggedBlock_See_Function(this, arg);
                    }


                    /**
                     * {@inheritDoc}
                     */
                    ParseTreeNode toParseTreeNode() {
                        ParseTreeNode seeNode = new ParseTreeNode(CALTreeParserTokenTypes.CALDOC_SEE_BLOCK, "CALDOC_SEE_BLOCK");
                        ParseTreeNode functionNode = new ParseTreeNode(CALTreeParserTokenTypes.CALDOC_SEE_FUNCTION_BLOCK, "CALDOC_SEE_FUNCTION_BLOCK");
                        
                        ParseTreeNode[] children = new ParseTreeNode[functionNames.length];
                        for (int i = 0; i < functionNames.length; i++) {
                            children[i] = functionNames[i].toParseTreeNode();
                        }
                        functionNode.addChildren(children);
                        
                        seeNode.setFirstChild(functionNode);
                        
                        return seeNode;
                    }
                }
                
                /**
                 * Models a CALDoc "@see module = ..." block.
                 *
                 * @author Joseph Wong
                 */
                public static final class Module extends See {
                    
                    /**
                     * The non-empty array of module names.
                     */
                    private final CrossReference.Module[] moduleNames;
                    
                    /**
                     * Creates a source model element for representing a CALDoc "@see module = ..." block.
                     * 
                     * @param moduleNames a non-empty array of module names referenced by this "@see" block.
                     */
                    private Module(CrossReference.Module[] moduleNames) {
                        super(null);

                        if (moduleNames.length == 0) {
                            throw new IllegalArgumentException("Must have at least one name in the @see module list");
                        }
                        this.moduleNames = moduleNames.clone();
                        verifyArrayArg(this.moduleNames, "moduleNames");
                    }
                    
                    /**
                     * Factory method for constructing a source model element for representing a CALDoc "@see module = ..." block.
                     * 
                     * @param moduleNames a non-empty array of module names referenced by this "@see" block.
                     * @return a new instance of this class.
                     */
                    public static Module make(CrossReference.Module[] moduleNames) {
                        return new Module(moduleNames);
                    }
                    
                    /**
                     * @return the non-empty array of module names referenced by this "@see" block.
                     */
                    public CrossReference.Module[] getModuleNames() {
                        return moduleNames.clone();
                    }
                    
                    /**
                     * @return the number of module names referenced by this "@see" block.
                     */
                    public int getNModuleNames() {
                        return moduleNames.length;
                    }
                    
                    /**
                     * Returns the module name at the specified position in this "@see" block.
                     * @param n index of the module name to return.
                     * @return the module name at the specified position in this "@see" block.
                     */
                    public CrossReference.Module getNthModuleName(int n) {
                        return moduleNames[n];
                    }
                    
                    /**
                     * {@inheritDoc}
                     */
                    public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                        return visitor.visit_CALDoc_TaggedBlock_See_Module(this, arg);
                    }



                    /**
                     * {@inheritDoc}
                     */
                    ParseTreeNode toParseTreeNode() {
                        ParseTreeNode seeNode = new ParseTreeNode(CALTreeParserTokenTypes.CALDOC_SEE_BLOCK, "CALDOC_SEE_BLOCK");
                        ParseTreeNode moduleNode = new ParseTreeNode(CALTreeParserTokenTypes.CALDOC_SEE_MODULE_BLOCK, "CALDOC_SEE_MODULE_BLOCK");
                        
                        ParseTreeNode[] children = new ParseTreeNode[moduleNames.length];
                        for (int i = 0; i < moduleNames.length; i++) {
                            children[i] = moduleNames[i].toParseTreeNode();
                        }
                        moduleNode.addChildren(children);
                        
                        seeNode.setFirstChild(moduleNode);
                        
                        return seeNode;
                    }
                }
                
                /**
                 * Models a CALDoc "@see typeConstructor = ..." block.
                 *
                 * @author Joseph Wong
                 */
                public static final class TypeCons extends See {
                    
                    /**
                     * The non-empty array of type constructor names.
                     */
                    private final CrossReference.TypeCons[] typeConsNames;
                    
                    /**
                     * Creates a source model element for representing a CALDoc "@see typeConstructor = ..." block.
                     * 
                     * @param typeConsNames a non-empty array of type constructor names referenced by this "@see" block.
                     */
                    private TypeCons(CrossReference.TypeCons[] typeConsNames) {
                        super(null);

                        if (typeConsNames.length == 0) {
                            throw new IllegalArgumentException("Must have at least one name in the @see typeConstructor list");
                        }
                        this.typeConsNames = typeConsNames.clone();
                        verifyArrayArg(this.typeConsNames, "typeConsNames");
                    }
                    
                    /**
                     * Factory method for constructing a source model element for representing a CALDoc "@see typeConstructor = ..." block.
                     * 
                     * @param typeConsNames a non-empty array of type constructor names referenced by this "@see" block.
                     * @return a new instance of this class.
                     */
                    public static TypeCons make(CrossReference.TypeCons[] typeConsNames) {
                        return new TypeCons(typeConsNames);
                    }
                    
                    /**
                     * @return the non-empty array of type constructor names referenced by this "@see" block.
                     */
                    public CrossReference.TypeCons[] getTypeConsNames() {
                        return typeConsNames.clone();
                    }
                    
                    /**
                     * @return the number of type constructor names referenced by this "@see" block.
                     */
                    public int getNTypeConsNames() {
                        return typeConsNames.length;
                    }
                    
                    /**
                     * Returns the type constructor name at the specified position in this "@see" block.
                     * @param n index of the type constructor name to return.
                     * @return the type constructor name at the specified position in this "@see" block.
                     */
                    public CrossReference.TypeCons getNthTypeConsName(int n) {
                        return typeConsNames[n];
                    }
                    
                    /**
                     * {@inheritDoc}
                     */
                    public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                        return visitor.visit_CALDoc_TaggedBlock_See_TypeCons(this, arg);
                    }


                    /**
                     * {@inheritDoc}
                     */
                    ParseTreeNode toParseTreeNode() {
                        ParseTreeNode seeNode = new ParseTreeNode(CALTreeParserTokenTypes.CALDOC_SEE_BLOCK, "CALDOC_SEE_BLOCK");
                        ParseTreeNode typeConsNode = new ParseTreeNode(CALTreeParserTokenTypes.CALDOC_SEE_TYPECONS_BLOCK, "CALDOC_SEE_TYPECONS_BLOCK");
                        
                        ParseTreeNode[] children = new ParseTreeNode[typeConsNames.length];
                        for (int i = 0; i < typeConsNames.length; i++) {
                            children[i] = typeConsNames[i].toParseTreeNode();
                        }
                        typeConsNode.addChildren(children);
                        
                        seeNode.setFirstChild(typeConsNode);
                        
                        return seeNode;
                    }
                }
                
                /**
                 * Models a CALDoc "@see dataConstructor = ..." block.
                 *
                 * @author Joseph Wong
                 */
                public static final class DataCons extends See {
                    
                    /**
                     * The non-empty array of data constructor names.
                     */
                    private final CrossReference.DataCons[] dataConsNames;
                    
                    /**
                     * Creates a source model element for representing a CALDoc "@see dataConstructor = ..." block.
                     * 
                     * @param dataConsNames a non-empty array of data constructor names referenced by this "@see" block.
                     */
                    private DataCons(CrossReference.DataCons[] dataConsNames) {
                        super(null);

                        if (dataConsNames.length == 0) {
                            throw new IllegalArgumentException("Must have at least one name in the @see dataCons list");
                        }
                        this.dataConsNames = dataConsNames.clone();
                        verifyArrayArg(this.dataConsNames, "dataConsNames");
                    }
                    
                    /**
                     * Factory method for constructing a source model element for representing a CALDoc "@see dataConstructor = ..." block.
                     * 
                     * @param dataConsNames a non-empty array of data constructor names referenced by this "@see" block.
                     * @return a new instance of this class.
                     */
                    public static DataCons make(CrossReference.DataCons[] dataConsNames) {
                        return new DataCons(dataConsNames);
                    }
                    
                    /**
                     * @return the non-empty array of data constructor names referenced by this "@see" block.
                     */
                    public CrossReference.DataCons[] getDataConsNames() {
                        return dataConsNames.clone();
                    }
                    
                    /**
                     * @return the number of data constructor names referenced by this "@see" block.
                     */
                    public int getNDataConsNames() {
                        return dataConsNames.length;
                    }
                    
                    /**
                     * Returns the data constructor name at the specified position in this "@see" block.
                     * @param n index of the data constructor name to return.
                     * @return the data constructor name at the specified position in this "@see" block.
                     */
                    public CrossReference.DataCons getNthDataConsName(int n) {
                        return dataConsNames[n];
                    }

                    /**
                     * {@inheritDoc}
                     */
                    public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                        return visitor.visit_CALDoc_TaggedBlock_See_DataCons(this, arg);
                    }

                    /**
                     * {@inheritDoc}
                     */
                    ParseTreeNode toParseTreeNode() {
                        ParseTreeNode seeNode = new ParseTreeNode(CALTreeParserTokenTypes.CALDOC_SEE_BLOCK, "CALDOC_SEE_BLOCK");
                        ParseTreeNode dataConsNode = new ParseTreeNode(CALTreeParserTokenTypes.CALDOC_SEE_DATACONS_BLOCK, "CALDOC_SEE_DATACONS_BLOCK");
                        
                        ParseTreeNode[] children = new ParseTreeNode[dataConsNames.length];
                        for (int i = 0; i < dataConsNames.length; i++) {
                            children[i] = dataConsNames[i].toParseTreeNode();
                        }
                        dataConsNode.addChildren(children);
                        
                        seeNode.setFirstChild(dataConsNode);
                        
                        return seeNode;
                    }
                }
                
                /**
                 * Models a CALDoc "@see typeClass = ..." block.
                 *
                 * @author Joseph Wong
                 */
                public static final class TypeClass extends See {
                    
                    /**
                     * The non-empty array of type class names.
                     */
                    private final CrossReference.TypeClass[] typeClassNames;
                    
                    /**
                     * Creates a source model element for representing a CALDoc "@see typeClass = ..." block.
                     * 
                     * @param typeClassNames a non-empty array of type class names referenced by this "@see" block.
                     */
                    private TypeClass(CrossReference.TypeClass[] typeClassNames) {
                        super(null);

                        if (typeClassNames.length == 0) {
                            throw new IllegalArgumentException("Must have at least one name in the @see typeClass list");
                        }
                        this.typeClassNames = typeClassNames.clone();
                        verifyArrayArg(this.typeClassNames, "typeClassNames");
                    }
                    
                    /**
                     * Factory method for constructing a source model element for representing a CALDoc "@see typeClass = ..." block.
                     * 
                     * @param typeClassNames a non-empty array of type class names referenced by this "@see" block.
                     * @return a new instance of this class.
                     */
                    public static TypeClass make(CrossReference.TypeClass[] typeClassNames) {
                        return new TypeClass(typeClassNames);
                    }
                    
                    /**
                     * @return the non-empty array of type class names referenced by this "@see" block.
                     */
                    public CrossReference.TypeClass[] getTypeClassNames() {
                        return typeClassNames.clone();
                    }
                    
                    /**
                     * @return the number of type class names referenced by this "@see" block.
                     */
                    public int getNTypeClassNames() {
                        return typeClassNames.length;
                    }
                    
                    /**
                     * Returns the type class name at the specified position in this "@see" block.
                     * @param n index of the type class name to return.
                     * @return the type class name at the specified position in this "@see" block.
                     */
                    public CrossReference.TypeClass getNthTypeClassName(int n) {
                        return typeClassNames[n];
                    }

                    /**
                     * {@inheritDoc}
                     */
                    public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                        return visitor.visit_CALDoc_TaggedBlock_See_TypeClass(this, arg);
                    }

                    /**
                     * {@inheritDoc}
                     */
                    ParseTreeNode toParseTreeNode() {
                        ParseTreeNode seeNode = new ParseTreeNode(CALTreeParserTokenTypes.CALDOC_SEE_BLOCK, "CALDOC_SEE_BLOCK");
                        ParseTreeNode typeClassNode = new ParseTreeNode(CALTreeParserTokenTypes.CALDOC_SEE_TYPECLASS_BLOCK, "CALDOC_SEE_TYPECLASS_BLOCK");
                        
                        ParseTreeNode[] children = new ParseTreeNode[typeClassNames.length];
                        for (int i = 0; i < typeClassNames.length; i++) {
                            children[i] = typeClassNames[i].toParseTreeNode();
                        }
                        typeClassNode.addChildren(children);
                        
                        seeNode.setFirstChild(typeClassNode);
                        
                        return seeNode;
                    }
                }
                
                /**
                 * Models a CALDoc "@see" block without the context keyword.
                 *
                 * @author Joseph Wong
                 */
                public static final class WithoutContext extends See {
                    
                    /**
                     * The non-empty array of referenced names.
                     */
                    private final CrossReference.CanAppearWithoutContext[] referencedNames;
                    
                    /**
                     * Creates a source model element for representing a CALDoc "@see" block without the context keyword.
                     * 
                     * @param referencedNames a non-empty array of names referenced by this "@see" block.
                     * @param sourceRange
                     */
                    private WithoutContext(CrossReference.CanAppearWithoutContext[] referencedNames, SourceRange sourceRange) {
                        super(sourceRange);
                        
                        if (referencedNames.length == 0) {
                            throw new IllegalArgumentException("Must have at least one name in the @see list");
                        }
                        this.referencedNames = referencedNames.clone();
                        verifyArrayArg(this.referencedNames, "referencedNames");
                    }
                    
                    /**
                     * Factory method for constructing a source model element for representing a CALDoc "@see" block without the context keyword.
                     * 
                     * @param referencedNames a non-empty array of names referenced by this "@see" block.
                     * @return a new instance of this class.
                     */
                    public static WithoutContext make(CrossReference.CanAppearWithoutContext[] referencedNames) {
                        return new WithoutContext(referencedNames, null);
                    }
                    
                    static WithoutContext makeAnnotated(CrossReference.CanAppearWithoutContext[] referencedNames, SourceRange sourceRange) {
                        return new WithoutContext(referencedNames, sourceRange);
                    }
                    
                    /**
                     * @return the non-empty array of names referenced by this "@see" block.
                     */
                    public CrossReference.CanAppearWithoutContext[] getReferencedNames() {
                        return referencedNames.clone();
                    }
                    
                    /**
                     * @return the number of names referenced by this "@see" block.
                     */
                    public int getNReferencedNames() {
                        return referencedNames.length;
                    }
                    
                    /**
                     * Returns the referenced name at the specified position in this "@see" block.
                     * @param n index of the function name to return.
                     * @return the referenced name at the specified position in this "@see" block.
                     */
                    public CrossReference.CanAppearWithoutContext getNthReferencedName(int n) {
                        return referencedNames[n];
                    }

                    /**
                     * {@inheritDoc}
                     */
                    public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                        return visitor.visit_CALDoc_TaggedBlock_See_WithoutContext(this, arg);
                    }

                    /**
                     * {@inheritDoc}
                     */
                    ParseTreeNode toParseTreeNode() {
                        ParseTreeNode seeNode = new ParseTreeNode(CALTreeParserTokenTypes.CALDOC_SEE_BLOCK, "CALDOC_SEE_BLOCK");
                        ParseTreeNode withoutContextNode = new ParseTreeNode(CALTreeParserTokenTypes.CALDOC_SEE_BLOCK_WITHOUT_CONTEXT, "CALDOC_SEE_BLOCK_WITHOUT_CONTEXT");
                        
                        ParseTreeNode[] children = new ParseTreeNode[referencedNames.length];
                        for (int i = 0; i < referencedNames.length; i++) {
                            children[i] = referencedNames[i].toParseTreeNode();
                        }
                        withoutContextNode.addChildren(children);
                        
                        seeNode.setFirstChild(withoutContextNode);
                        
                        return seeNode;
                    }
                }
            }
        }
    }
    
    /**
     * Models the arguments in the unpacking of a general data constructor.
     * @author Edward Lam
     */
    public static abstract class ArgBindings extends SourceElement {
        
        public static final ArgBindings NO_BINDINGS = ArgBindings.Positional.make(Pattern.NO_PATTERNS);
        
        /**
         * Models the arguments in the unpacking of a general data constructor, where the argument notation is matching.
         * 
         * For instance, if we have the data type Foo:
         * data public Foo a b = Bar bar::a barbar::a | Baz baz::b bazbaz::b;
         * 
         * ... and the function blah:
         * public blah !foo =
         *     case foo of
         *     Bar bar barbar -> 1.0;
         *     Baz {baz} -> 2.0;
         *     ;
         * 
         * ... in function "blah", the unpacking of Bar is positional, and the unpacking of Baz is matching.
         *     
         * @author Edward Lam
         */
        public static final class Matching extends ArgBindings {
            
            private final FieldPattern[] fieldPatterns;
            
            private Matching(FieldPattern[] fieldPatterns) {
                if (fieldPatterns == null || fieldPatterns.length == 0) {
                    this.fieldPatterns = FieldPattern.NO_FIELD_PATTERNS;
                } else {
                    this.fieldPatterns = fieldPatterns.clone();
                    verifyArrayArg(this.fieldPatterns, "patterns");                                       
                }
                
            }
            
            public static Matching make(FieldPattern[] patterns) {
                return new Matching(patterns);
            }
            
            
            public FieldPattern[] getFieldPatterns() {
                if (fieldPatterns.length == 0) {
                    return FieldPattern.NO_FIELD_PATTERNS;
                }
                
                return fieldPatterns.clone();
            }
            
            /**
             * Get the number of field patterns.
             * @return the number of field patterns.
             */
            public int getNFieldPatterns() {
                return fieldPatterns.length;
            }
            
            /**
             * Get the nth field pattern.
             * @param n the index of the field pattern to return.
             * @return the nth field pattern.
             */
            public FieldPattern getNthFieldPattern(int n) {
                return fieldPatterns[n];
            }
            
            ParseTreeNode toParseTreeNode() {
                ParseTreeNode fieldBindingVarAssignmentListNode = 
                    new ParseTreeNode(CALTreeParserTokenTypes.FIELD_BINDING_VAR_ASSIGNMENT_LIST, "FIELD_BINDING_VAR_ASSIGNMENT_LIST");
                
                ParseTreeNode[] fieldPatternNodes = new ParseTreeNode[fieldPatterns.length];
                for (int i = 0; i < fieldPatterns.length; i++) {
                    fieldPatternNodes[i] = fieldPatterns[i].toParseTreeNode();
                }
                fieldBindingVarAssignmentListNode.addChildren(fieldPatternNodes);
                
                return fieldBindingVarAssignmentListNode;
            }
            
            /**
             * {@inheritDoc}
             */
            public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                return visitor.visit_ArgBindings_Matching(this, arg);
            }                     
            
        }
        
        /**
         * Models the unpacking of a general data constructor, where the argument notation is positional.
         * 
         * For instance, if we have the data type Foo:
         * data public Foo a b = Bar bar::a barbar::a | Baz baz::b bazbaz::b;
         * 
         * ... and the function blah:
         * public blah !foo =
         *     case foo of
         *     Bar bar barbar -> 1.0;
         *     Baz {baz} -> 2.0;
         *     ;
         * 
         * ... in function "blah", the unpacking of Bar is positional, and the unpacking of Baz is matching.
         *     
         * @author Bo Ilic
         * @author Edward Lam
         */
        public static final class Positional extends ArgBindings {
            private final Pattern[] patterns;
            
            private Positional(Pattern[] patterns) {
                if (patterns == null || patterns.length == 0) {
                    this.patterns = Pattern.NO_PATTERNS;
                } else {
                    this.patterns = patterns.clone();
                    verifyArrayArg(this.patterns, "patterns");                                       
                }
                
            }
            
            public static Positional make(Pattern[] patterns) {
                return new Positional(patterns);
            }
            

            
            public Pattern[] getPatterns() {
                if (patterns.length == 0) {
                    return Pattern.NO_PATTERNS;
                }
                
                return patterns.clone();
            }
            
            /**
             * Get the number of patterns.
             * @return the number of patterns.
             */
            public int getNPatterns() {
                return patterns.length;
            }
            
            /**
             * Get the nth pattern.
             * @param n the index of the pattern to return.
             * @return the nth pattern.
             */
            public Pattern getNthPattern(int n) {
                return patterns[n];
            }
            
            ParseTreeNode toParseTreeNode() {
                ParseTreeNode patternListNode = new ParseTreeNode(CALTreeParserTokenTypes.PATTERN_VAR_LIST, "PATTERN_VAR_LIST");
                
                ParseTreeNode[] patternNodes = new ParseTreeNode[patterns.length];
                for (int i = 0; i < patterns.length; i++) {
                    patternNodes[i] = patterns[i].toParseTreeNode();
                }
                patternListNode.addChildren(patternNodes);
    
                return patternListNode;
            }
            
            /**
             * {@inheritDoc}
             */
            public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                return visitor.visit_ArgBindings_Positional(this, arg);
            }                     
            
        }
    }

    /**
     * Models a variable that occurs in a case pattern (i.e. on the left hand side of the "->"). These
     * correspond either to actual variable or the wildcard pattern "_" which is used to indicate
     * that the variable will not be used on the right hand side of the "->" and so a name is not
     * explicitly given.
     * 
     * @author Bo Ilic
     */
    public static abstract class Pattern extends SourceElement {
        
        public static final Pattern[] NO_PATTERNS = new Pattern[0];
        
        public static final class Var extends Pattern {
            private final String name;
            
            private Var(final String name, final SourceRange sourceRange) {
                super(sourceRange);
                
                if (!LanguageInfo.isValidFunctionName(name)) {
                    throw new IllegalArgumentException();
                }
                
                this.name = name;                             
            }
            
            public static Var make(String name) {
                return new Var(name, null);
            }
            
            static Var makeAnnotated(final String name, final SourceRange sourceRange) {
                return new Var(name, sourceRange);
            }
            

            
            public String getName() {
                return name;
            }
            
            ParseTreeNode toParseTreeNode() {
                ParseTreeNode patternVar = new ParseTreeNode(CALTreeParserTokenTypes.VAR_ID, name);
                return patternVar;
            }
            
            /**
             * {@inheritDoc}
             */
            public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                return visitor.visit_Pattern_Var(this, arg);
            }
        }
        
        /**
         * Models the wildcard pattern variable, which is "_" in CAL text. Note that this
         * is distinct from the default pattern, which is also "_" in CAL text but stands in
         * for the whole pattern and not just a pattern variable.
         * 
         * @author Bo Ilic
         */
        public static final class Wildcard extends Pattern {
            
            private static final Wildcard WILDCARD = new Wildcard();
            
            private Wildcard() {
                super(null);
            }
            
            public static Wildcard make() {
                return WILDCARD;
            }
            
            
            ParseTreeNode toParseTreeNode() {
                ParseTreeNode patternVar = new ParseTreeNode(CALTreeParserTokenTypes.UNDERSCORE, "_");
                return patternVar;
            }
            
            /**
             * {@inheritDoc}
             */
            public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
                return visitor.visit_Pattern_Wildcard(this, arg);
            }
        }
        
        private Pattern(final SourceRange sourceRange) {
            super(sourceRange);
        }
    }

    /**
     * Models a fieldName/pattern pair, where the patternVar may be missing due to punning. For example,
     * 
     * the parts "#1 = x", "#2", "#3 = _", "orderDate = od", "shipDate", and "receivedDate = _" in the fragement below:
     * 
     * case r of
     * {#1 = x, #2, #3 = _, orderDate = od, shipDate, receivedDate = _} -> ...
     *                                             
     * @author Bo Ilic
     */
    public static final class FieldPattern extends SourceElement {
        
        public static final FieldPattern[] NO_FIELD_PATTERNS = new FieldPattern[0];
        private final Name.Field fieldName;
        
        /**
         * may be null to indicate punning where the fieldName is used to supply a "default" pattern.
         * 
         * In the case of textual field names:
         * orderDate 
         * is semantically equivalent to
         * orderDate = orderDate 
         * 
         * In the case of ordinal field names:
         * #1
         * is semantically equivalent to
         * #1 = _
         *                      
         */
        private final Pattern pattern;
        
        /**                          
         * @param fieldName FieldName
         * @param pattern Pattern may be null to indicate punning where the fieldName is used to supply a "default" pattern.
         */
        private FieldPattern(Name.Field fieldName, Pattern pattern) {
            verifyArg(fieldName, "fieldName");
            
            this.fieldName = fieldName;
            this.pattern = pattern;
        }
        
        /**                          
         * @param fieldName FieldName
         * @param pattern Pattern may be null to indicate punning where the fieldName is used to supply a "default" pattern.
         * @return an instance of FieldPattern
         */
        public static FieldPattern make(Name.Field fieldName, Pattern pattern) {
            return new FieldPattern(fieldName, pattern);
        }
        
        /**                          
         * @param fieldName FieldName
         * @param pattern Pattern may be null to indicate punning where the fieldName is used to supply a "default" pattern.
         * @return an instance of FieldPattern
         */
        static FieldPattern makeAnnotated(Name.Field fieldName, Pattern pattern) {
            return new FieldPattern(fieldName, pattern);
        }
       
        public Name.Field getFieldName() {
            return fieldName;
        }
        
        public Pattern getPattern() {
            return pattern;
        }
        
        ParseTreeNode toParseTreeNode() {
            ParseTreeNode fieldPatternNode = new ParseTreeNode(CALTreeParserTokenTypes.FIELD_BINDING_VAR_ASSIGNMENT, "FIELD_BINDING_VAR_ASSIGNMENT");
            ParseTreeNode fieldNameNode = fieldName.toParseTreeNode(); 
            
            if (pattern != null) {
                ParseTreeNode patternNode = pattern.toParseTreeNode();
                fieldNameNode.setNextSibling(patternNode);
            }
            
            fieldPatternNode.setFirstChild(fieldNameNode);
            return fieldPatternNode;
        }
        
        /**
         * {@inheritDoc}
         */
        public <T, R> R accept(final SourceModelVisitor<T, R> visitor, final T arg) {
            return visitor.visit_FieldPattern(this, arg);
        }
    }

    /**
     * Throws an exception if the array is null or has any null elements.
     * @param array Object[]
     * @param argName String name of the array for display in the error text of any generated exception
     * @throws NullPointerException if array is null, or any of its elements are null      
     */
    static void verifyArrayArg(Object[] array, String argName) {
        
        verifyArg(array, argName);
        
        for (int i = 0, nElems = array.length; i < nElems; ++i) {
            if (array[i] == null) {                 
                throw new NullPointerException(argName + "[" + i + "] is null.");                   
            }
        }                  
    }
    
    /**
     * Throws an exception if arg is null.
     * @param arg Object
     * @param argName String name of the arg to display in the error text of any generated exception
     * @throws NullPointerException if 'arg' is null
     */
    static void verifyArg(Object arg, String argName) {
        if (arg == null) {
            throw new NullPointerException("The argument '" + argName + "' cannot be null.");
        }
    }

    /**
     * Verifies the scope argument as non-null and as valid (either explicitly specified, or implicitly private).
     * @param scope the scope argument to check.
     * @param isScopeExplicitlySpecified whether the scope is explicitly specified in the source.
     * @param argName name of the arg to display in the error text of any generated exception.
     */
    static void verifyScopeArg(Scope scope, boolean isScopeExplicitlySpecified, String argName) {
        verifyArg(scope, argName);
        if (!isScopeExplicitlySpecified && scope != Scope.PRIVATE) {
            throw new IllegalArgumentException("For the argument '" + argName + "', the non-private scope " + scope + " must be explicitly specified in the source.");
        }
    }
    
    static String space(int nSpaces) {
        StringBuilder sb = new StringBuilder(nSpaces);
        for (int i = 0; i < nSpaces; ++i) {
            sb.append(' ');
        }
        
        return sb.toString();
    }
    
    /**
     * A helper function that creates the parseTree for a scope expression.
     * Creation date: (12/06/04)    
     * @param scope
     * @param isScopeExplicitlySpecified whether the scope is explicitly specified in the source.
     * @return ParseTreeNode
     */
    static ParseTreeNode makeAccessModifierNode(Scope scope, boolean isScopeExplicitlySpecified) {
        ParseTreeNode accessModifierNode = new ParseTreeNode(CALTreeParserTokenTypes.ACCESS_MODIFIER, "ACCESS_MODIFIER");
        
        ParseTreeNode scopeNode;
        if (scope.isPublic()) {
            scopeNode = new ParseTreeNode(CALTreeParserTokenTypes.LITERAL_public, "public");
        } else if (scope.isPrivate()) {
            scopeNode = new ParseTreeNode(CALTreeParserTokenTypes.LITERAL_private, "private");
        } else if (scope.isProtected()) {
            scopeNode = new ParseTreeNode(CALTreeParserTokenTypes.LITERAL_protected, "protected");
        } else {
            throw new IllegalArgumentException("unrecognized scope " + scope);
        }
        
        if (isScopeExplicitlySpecified) {
            accessModifierNode.setFirstChild(scopeNode);
        }
        
        return accessModifierNode;
    }
    
    /**
     * A helper function that creates the parseTree for a FieldName expression.
     * Creation date: (5/31/01 7:43:14 AM)    
     * @param fieldName
     *  @return ParseTreeNode
     */
    static ParseTreeNode makeFieldNameExpr(FieldName fieldName) {
        
        if(fieldName instanceof FieldName.Ordinal) {
            return new ParseTreeNode(CALTreeParserTokenTypes.ORDINAL_FIELD_NAME, fieldName.getCalSourceForm());
        } else {
            return new ParseTreeNode(CALTreeParserTokenTypes.VAR_ID, fieldName.getCalSourceForm());
        }
    }
    
    /**
     * A helper function that creates the parseTree for an optional CALDoc comment.
     * @param commentOrNull the comment to be represented by the parse tree, or null.
     * @return ParseTreeNode a parse tree whose root is of the type OPTIONAL_CALDOC_COMMENT.
     */
    static ParseTreeNode makeOptionalCALDocNode(CALDoc.Comment commentOrNull) {
        ParseTreeNode optionalCALDocNode = new ParseTreeNode(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT, "OPTIONAL_CALDOC_COMMENT");
        
        if (commentOrNull != null) {
            optionalCALDocNode.setFirstChild(commentOrNull.toParseTreeNode());
        }
        
        return optionalCALDocNode;
    }
}

