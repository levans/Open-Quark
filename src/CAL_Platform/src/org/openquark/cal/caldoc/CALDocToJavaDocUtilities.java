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
 * CALDocToJavaDocUtilities.java
 * Creation date: Jul 14, 2006
 * By: RCypher
 */
package org.openquark.cal.caldoc;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openquark.cal.compiler.CALDocComment;
import org.openquark.cal.compiler.ClassMethod;
import org.openquark.cal.compiler.DataConstructor;
import org.openquark.cal.compiler.Function;
import org.openquark.cal.compiler.FunctionalAgent;
import org.openquark.cal.compiler.ScopedEntityNamingPolicy;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.compiler.CALDocComment.ArgBlock;
import org.openquark.cal.compiler.CALDocComment.ModuleReference;
import org.openquark.cal.compiler.CALDocComment.ScopedEntityReference;
import org.openquark.cal.compiler.CALDocComment.TextBlock;


/**
 * This is a utility class containing helper methods for converting CALDoc into Javadoc.
 * 
 * @author RCypher (original version based on {@link CALDocToTextUtilities})
 * @author Joseph Wong (revised HTML-based version)
 */
public class CALDocToJavaDocUtilities {
    
    /**
     * An abstract class representing a cross-reference generator that can generate Javadoc references.
     *
     * @author Joseph Wong
     */
    public static abstract class JavadocCrossReferenceGenerator extends CALDocToHTMLUtilities.CrossReferenceHTMLStringGenerator {
        
        /**
         * Runs any post-processing (e.g. unescaping) required.
         * @param html the HTML to be procsseed.
         * @return the processed result.
         */
        public String postProcess(final String html) {
            return html;
        }
    }
    
    /**
     * Implements a cross-reference generator capable of generating non-hyperlinked text for cross-references.
     *
     * @author Joseph Wong
     */
    private static final class NoHyperlinkCrossReferenceGenerator extends JavadocCrossReferenceGenerator {

        /** {@inheritDoc} */
        @Override
        public String getModuleReferenceHTML(final ModuleReference reference) {
            return reference.getName().toSourceText();
        }

        /** {@inheritDoc} */
        @Override
        public String getTypeConsReferenceHTML(final ScopedEntityReference reference) {
            return reference.getName().getQualifiedName();
        }

        /** {@inheritDoc} */
        @Override
        public String getDataConsReferenceHTML(final ScopedEntityReference reference) {
            return reference.getName().getQualifiedName();
        }

        /** {@inheritDoc} */
        @Override
        public String getFunctionOrClassMethodReferenceHTML(final ScopedEntityReference reference) {
            return reference.getName().getQualifiedName();
        }

        /** {@inheritDoc} */
        @Override
        public String getTypeClassReferenceHTML(final ScopedEntityReference reference) {
            return reference.getName().getQualifiedName();
        }
    }
    
    /** Private constructor. */
    private CALDocToJavaDocUtilities() {}
    
    /**
     * Generates the argument names from the CALDoc comment.
     * @param caldoc the CALDoc comment of the entity. Can be null.
     * @return the argument names from the CALDoc comment.
     */
    public static String[] getArgumentNamesFromCALDocComment(final CALDocComment caldoc) {
        return getArgumentNamesFromCALDocComment(caldoc, null);
    }
    
    /**
     * Generates the argument names from the CALDoc comment.
     * @param caldoc the CALDoc comment of the entity. Can be null.
     * @param envEntity the FunctionalAgent being documented. Can be null if the comment is not for an FunctionalAgent.
     * @return the argument names from the CALDoc comment.
     */
    public static String[] getArgumentNamesFromCALDocComment(final CALDocComment caldoc, final FunctionalAgent envEntity) {
        final TypeExpr typeExpr = envEntity == null ? null : envEntity.getTypeExpr(); 
        return getArgumentNamesFromCALDocComment(caldoc, envEntity, typeExpr);
    }
    
    /**
     * Generates the argument names from the CALDoc comment.
     * @param caldoc the CALDoc comment of the entity. Can be null.
     * @param envEntity the FunctionalAgent being documented. Can be null if the comment is not for an FunctionalAgent.
     * @param typeExpr the type of the entity. Can be null if envEntity is null.
     * @return the argument names from the CALDoc comment.
     */
    public static String[] getArgumentNamesFromCALDocComment(final CALDocComment caldoc, final FunctionalAgent envEntity, final TypeExpr typeExpr) {
        /// The arguments
        //
        final int nArgsInCALDoc = (caldoc == null) ? -1 : caldoc.getNArgBlocks();
        final int arity = (typeExpr == null) ? -1 : typeExpr.getArity();
        
        final int nArgs = Math.max(nArgsInCALDoc, arity);
        
        if (nArgs > 0) {
            final String argNames[] = new String[nArgs];
            final Set<String> setOfArgumentNames = new HashSet<String>();
            
            for (int i = 0; i < nArgs; i++) {
                argNames[i] = getNthArgumentName(caldoc, envEntity, i, setOfArgumentNames);
            }
            
            return argNames;
        }
        
        return new String[]{};
    }
    
    /**
     * Generates a plain text representation for a CALDoc comment.
     * @param caldoc the CALDoc comment.
     * @param isClassComment whether the Javadoc comment is a class comment.
     * @return a plain text representation for the comment.
     */
    public static String getJavadocFromCALDocComment(final CALDocComment caldoc, final boolean isClassComment) {
        // even though we specify ScopedEntityNamingPolicy.QUALIFIED here, it has no effect on the generated
        // text as there is no FunctionalAgent from which a type signature (potentially containing types in other modules)
        // can be extracted.
        return getJavadocFromCALDocComment(caldoc, isClassComment, null, null);
    }
    
    /**
     * Generates a plain text representation for a CALDoc comment.
     * @param caldoc the CALDoc comment.
     * @param isClassComment whether the Javadoc comment is a class comment.
     * @param envEntity the FunctionalAgent being documented. Can be null if the comment is not for an FunctionalAgent.
     * @param argNames the names of arguments to use. Can be null if the default mechanism for obtaining argument names is to be used.
     * @return a plain text representation for the comment.
     */
    public static String getJavadocFromCALDocComment(final CALDocComment caldoc, final boolean isClassComment, final FunctionalAgent envEntity, final String[] argNames) {
        TypeExpr typeExpr;
        if (envEntity == null) {
            typeExpr = null;
        } else {
            typeExpr = envEntity.getTypeExpr();
        }
        
        return getJavadocFromCALDocComment(caldoc, isClassComment, envEntity, typeExpr, argNames, ScopedEntityNamingPolicy.FULLY_QUALIFIED);
    }
    
    /**
     * Generates a plain text representation for a CALDoc comment.
     * @param caldoc the CALDoc comment.
     * @param isClassComment whether the Javadoc comment is a class comment.
     * @param envEntity the FunctionalAgent being documented. Can be null if the comment is not for an FunctionalAgent.
     * @param typeExpr the type of the entity. Can be null if envEntity is null.
     * @param argNames the names of arguments to use.
     * @param scopedEntityNamingPolicy the naming policy to use for generating qualified/unqualified names.
     * @return a plain text representation for the comment.
     */
    public static String getJavadocFromCALDocComment(final CALDocComment caldoc, final boolean isClassComment, final FunctionalAgent envEntity, final TypeExpr typeExpr, final String[] argNames, final ScopedEntityNamingPolicy scopedEntityNamingPolicy) {
        return getJavadocFromCALDocComment(caldoc, isClassComment, envEntity, typeExpr, argNames, scopedEntityNamingPolicy, new NoHyperlinkCrossReferenceGenerator(), false, false, true, false);
    }

    /**
     * Generates a plain text representation for a CALDoc comment.
     * @param caldoc the CALDoc comment.
     * @param isClassComment whether the Javadoc comment is a class comment.
     * @param envEntity the FunctionalAgent being documented. Can be null if the comment is not for an FunctionalAgent.
     * @param typeExpr the type of the entity. Can be null if envEntity is null.
     * @param argNames the names of arguments to use. Can be null if the default mechanism for obtaining argument names is to be used.
     * @param scopedEntityNamingPolicy the naming policy to use for generating qualified/unqualified names.
     * @param crossRefGen the cross reference generator to use.
     * @param omitReturnBlock whether to omit the return block.
     * @param allowReturnBlockForDataCons whether to allow return block for data cons.
     * @param disallowJavadocCodeBlock whether to disallow the Java 5 code block, and always use the HTML version.
     * @param trimBlankFirstLine whether to trim the first line if blank.
     * @return a plain text representation for the comment.
     */
    public static String getJavadocFromCALDocComment(final CALDocComment caldoc, final boolean isClassComment, final FunctionalAgent envEntity, final TypeExpr typeExpr, final String[] argNames, final ScopedEntityNamingPolicy scopedEntityNamingPolicy, final JavadocCrossReferenceGenerator crossRefGen, final boolean omitReturnBlock, final boolean allowReturnBlockForDataCons, final boolean disallowJavadocCodeBlock, final boolean trimBlankFirstLine) {
        final StringBuilder buffer = new StringBuilder();
        
        if (caldoc != null) {
            /// The main description
            //
            buffer.append(getJavadocFriendlyHTMLForCALDocTextBlock(caldoc.getDescriptionBlock(), crossRefGen));

            /// The "See Also" references - not generated as Javadoc @see blocks
            //
            final StringBuilder seeBuffer = new StringBuilder();

            // module cross references
            final int nModuleRefs = caldoc.getNModuleReferences();
            if (nModuleRefs > 0) {
                seeBuffer.append("\n").append("<dd><b>").append(CALDocMessages.getString("modulesColon")).append("</b> ");
                for (int i = 0; i < nModuleRefs; i++) {
                    if (i > 0) {
                        seeBuffer.append(CALDocMessages.getString("commaAndSpace"));
                    }
                    seeBuffer.append(crossRefGen.postProcess(crossRefGen.getModuleReferenceHTML(caldoc.getNthModuleReference(i))));
                }
            }

            // function and class method references
            final int nFuncRefs = caldoc.getNFunctionOrClassMethodReferences();
            if (nFuncRefs > 0) {
                seeBuffer.append("\n").append("<dd><b>").append(CALDocMessages.getString("functionsAndClassMethodsColon")).append("</b> ");
                for (int i = 0; i < nFuncRefs; i++) {
                    if (i > 0) {
                        seeBuffer.append(CALDocMessages.getString("commaAndSpace"));
                    }
                    seeBuffer.append(crossRefGen.postProcess(crossRefGen.getFunctionOrClassMethodReferenceHTML(caldoc.getNthFunctionOrClassMethodReference(i))));
                }
            }

            // type constructor references
            final int nTypeConsRefs = caldoc.getNTypeConstructorReferences();
            if (nTypeConsRefs > 0) {
                seeBuffer.append("\n").append("<dd><b>").append(CALDocMessages.getString("typeConstructorsColon")).append("</b> ");
                for (int i = 0; i < nTypeConsRefs; i++) {
                    if (i > 0) {
                        seeBuffer.append(CALDocMessages.getString("commaAndSpace"));
                    }
                    seeBuffer.append(crossRefGen.postProcess(crossRefGen.getTypeConsReferenceHTML(caldoc.getNthTypeConstructorReference(i))));
                }
            }

            // data constructor references
            final int nDataConsRefs = caldoc.getNDataConstructorReferences();
            if (nDataConsRefs > 0) {
                seeBuffer.append("\n").append("<dd><b>").append(CALDocMessages.getString("dataConstructorsColon")).append("</b> ");
                for (int i = 0; i < nDataConsRefs; i++) {
                    if (i > 0) {
                        seeBuffer.append(CALDocMessages.getString("commaAndSpace"));
                    }
                    seeBuffer.append(crossRefGen.postProcess(crossRefGen.getDataConsReferenceHTML(caldoc.getNthDataConstructorReference(i))));
                }
            }

            // type class references
            final int nTypeClassRefs = caldoc.getNTypeClassReferences();
            if (nTypeClassRefs > 0) {
                seeBuffer.append("\n").append("<dd><b>").append(CALDocMessages.getString("typeClassesColon")).append("</b> ");
                for (int i = 0; i < nTypeClassRefs; i++) {
                    if (i > 0) {
                        seeBuffer.append(CALDocMessages.getString("commaAndSpace"));
                    }
                    seeBuffer.append(crossRefGen.postProcess(crossRefGen.getTypeClassReferenceHTML(caldoc.getNthTypeClassReference(i))));
                }
            }

            if (seeBuffer.length() > 0) {
                buffer.append("\n\n<dl><dt><b>").append(CALDocMessages.getString("seeAlsoColon")).append("</b>").append(seeBuffer.toString()).append("\n</dl>\n");
            }
        }
        
        /// The arguments and the return value
        //
        buffer.append(getJavadocFragmentForArgumentsAndReturnValue(caldoc, envEntity, typeExpr, argNames, scopedEntityNamingPolicy, crossRefGen, omitReturnBlock, allowReturnBlockForDataCons, disallowJavadocCodeBlock));
        
        if (caldoc != null) {
            // The @author and @version tags are only allowed on overview, package and class documentation, per the Javadoc spec
            if (isClassComment) {
                /// The authors
                //
                final int nAuthors = caldoc.getNAuthorBlocks();
                if (nAuthors > 0) {
                    for (int i = 0; i < nAuthors; i++) {
                        buffer.append("\n").append("@author ");
                        final TextBlock authorBlock = caldoc.getNthAuthorBlock(i);
                        buffer.append(getJavadocFriendlyHTMLForCALDocTextBlock(authorBlock, crossRefGen));
                    }
                }

                /// The version
                //
                final TextBlock versionBlock = caldoc.getVersionBlock();
                if (versionBlock != null) {
                    buffer.append("\n").append("@version ");
                    buffer.append(getJavadocFriendlyHTMLForCALDocTextBlock(versionBlock, crossRefGen));
                }
            }

            /// The deprecated block comes after all other blocks, according to the Javadoc style guide.
            //
            final TextBlock deprecatedBlock = caldoc.getDeprecatedBlock();
            if (deprecatedBlock != null) {
                buffer.append("\n").append("@deprecated ");
                buffer.append(getJavadocFriendlyHTMLForCALDocTextBlock(deprecatedBlock, crossRefGen));
            }
        }
        
        // Wrap the comment text with comment delimiters
        if (buffer.length() > 0 && buffer.charAt(0) == '\n') {
            if (trimBlankFirstLine) {
                // If the first char is a newline, trim it so that we don't get a blank line as the first line
                buffer.deleteCharAt(0);
            }
        }
        
        final String newline = System.getProperty("line.separator", "\r\n");
        String comment =
            "/**" + newline +
            " * " + buffer.toString().replaceAll("[\r\n]", newline + " * ").replaceAll("[\r\n] \\* $", "") + newline +
            " */" + newline;
        
        // Check for invalid unicode escapes that people might
        // have inadvertently put into CALDoc comments.
        final Pattern p = Pattern.compile("\\\\u[^a-fA-F0-9]");
        Matcher m = p.matcher(comment);
        while (m.find()) {
            final int start = m.start();
            comment = comment.substring(0, start + 1) + " " + comment.substring(start+1);
            m = p.matcher(comment);
        }
        
        return comment;
    }

    /**
     * Generates the HTML for a text block appearing in a CALDoc comment, with appropriate escaping for Javadoc.
     * @param textBlock the text block to be formatted as HTML.
     * @param crossRefGen the cross reference generator to use.
     * @return the HTML for the text block.
     */
    private static String getJavadocFriendlyHTMLForCALDocTextBlock(final CALDocComment.TextBlock textBlock, final JavadocCrossReferenceGenerator crossRefGen) {
        String html = CALDocToHTMLUtilities.getHTMLForCALDocTextBlock(textBlock, crossRefGen);
        
        // if we start a new paragraph with <p>, we should have a newline right after the tag
        html = html.replaceAll("<p>", "<p>\n");
        
        // need to escape out the '@' character, which is always invalid in Javadoc
        html = html.replaceAll("@", "&#64;");
        
        // need also to escape out the string "*/"
        html = html.replaceAll("\\*/", "*&#47;");
        
        // finally unescape anything escaped by the cross reference generator
        html = crossRefGen.postProcess(html);
        
        return html;
    }
    
    /**
     * Generates a plain text representation for the arguments and return value of a function, class method, or data constructor.
     * @param caldoc the associated CALDoc. Can be null.
     * @param envEntity the associated entity. Can be null.
     * @param typeExpr the type of the entity. Can be null if envEntity is null.
     * @param argNames the names of arguments to use. Can be null if the default mechanism for obtaining argument names is to be used.
     * @param scopedEntityNamingPolicy the naming policy to use for generating qualified/unqualified names.
     * @param crossRefGen the cross reference generator to use.
     * @param omitReturnBlock whether to omit the return block.
     * @param allowReturnBlockForDataCons whether to allow return block for data cons.
     * @param disallowJavadocCodeBlock whether to disallow the Java 5 code block, and always use the HTML version.
     * @return the JavaDoc text for the arguments and return value.
     */
    private static String getJavadocFragmentForArgumentsAndReturnValue(final CALDocComment caldoc, final FunctionalAgent envEntity, final TypeExpr typeExpr, String[] argNames, final ScopedEntityNamingPolicy scopedEntityNamingPolicy, final JavadocCrossReferenceGenerator crossRefGen, boolean omitReturnBlock, final boolean allowReturnBlockForDataCons, final boolean disallowJavadocCodeBlock) {
        
        final StringBuilder buffer = new StringBuilder();
        
        /// The arguments
        //
        if (argNames == null) {
            argNames = getArgumentNamesFromCALDocComment(caldoc, envEntity, typeExpr);
        }
        TypeExpr[] typePieces = null;
        String[] typePieceStrings = null;
        if (typeExpr != null) {
            typePieces = typeExpr.getTypePieces();
            typePieceStrings = TypeExpr.toStringArray(typePieces, true, scopedEntityNamingPolicy); 
        }
        
        if (argNames.length > 0) {
            for (int i = 0; i < argNames.length; i++) {
                buffer.append("\n@param ").append(argNames[i]).append(" ");
                
                if (typePieces != null) {
                    final String argTypeHTML = getTypeAsFormattedString(typePieceStrings[i], disallowJavadocCodeBlock);
                    buffer.append(CALDocMessages.getString("calTypeIndicator", argTypeHTML));
                }
                
                if (caldoc != null && i < caldoc.getNArgBlocks()) {
                    final ArgBlock argBlock = caldoc.getNthArgBlock(i);
                    final String argDescription = getJavadocFriendlyHTMLForCALDocTextBlock(argBlock.getTextBlock(), crossRefGen);
                    
                    if (typePieces != null && argDescription.trim().length() > 0) {
                        // if there is both a type description and an arg description, separate them
                        // with a newline and some indentation
                        buffer.append("\n         ");
                    }
                    
                    buffer.append(argDescription);
                }
            }
        }
        
        if (!omitReturnBlock) {
            /// The return value
            //
            final boolean hasReturnBlock = (caldoc != null) && (caldoc.getReturnBlock() != null);

            // Note that we generate a return block not only for functions and class methods, but also for data constructors
            // as well (even though they *cannot* be documented with a @return block in CALDoc) because we are generating
            // *Javadoc*, and the CAL type of the data constructor is extremely important and should be documented!
            if (hasReturnBlock || envEntity instanceof Function || envEntity instanceof ClassMethod || (envEntity instanceof DataConstructor && allowReturnBlockForDataCons)) {
                buffer.append("\n").append("@return ");

                if (typePieces != null) {
                    final String returnTypeHTML = getTypeAsFormattedString(typePieceStrings[argNames.length], disallowJavadocCodeBlock);
                    buffer.append(CALDocMessages.getString("calTypeIndicator", returnTypeHTML)).append(" ");
                }

                if (hasReturnBlock) {
                    final TextBlock returnBlock = caldoc.getReturnBlock();
                    final String returnDescription = getJavadocFriendlyHTMLForCALDocTextBlock(returnBlock, crossRefGen);

                    if (typePieces != null && returnDescription.trim().length() > 0) {
                        // if there is both a type description and an return description, separate them
                        // with a newline and some indentation
                        buffer.append("\n         ");
                    }

                    buffer.append(returnDescription);
                }
            }
        }
        
        return buffer.toString();
    }

    /**
     * Converts a CAL type into a formatted Javadoc string.
     * @param typeString the type as a string.
     * @param disallowJavadocCodeBlock whether to disallow the Java 5 code block, and always use the HTML version.
     * @return the formatted version.
     */
    private static String getTypeAsFormattedString(final String typeString, final boolean disallowJavadocCodeBlock) {
        // We do not use CALDocToHTMLUtilities.htmlEscape() to escape the type string
        // since we know it will not contain tag-like sequences <...>, and the result
        // of turning => and -> to =&gt; and -&gt; is ugly.
        
        // To make the type look nice, if the type does not contain { or }, we can use the Java 5 {@code},
        // otherwise we wrap the type with a <code> block.
        if (typeString.contains("{") || typeString.contains("}") || disallowJavadocCodeBlock) {
            return "<code>" + typeString + "</code>";
        } else {
            return "{@code " + typeString + "}";
        }
    }
    
    /**
     * Generates an argument name of an FunctionalAgent. It gives preference to the name given in
     * CALDoc, if it is different from the name appearing in the entity itself.
     * 
     * @param caldoc the CALDoc comment of the entity. Can be null.
     * @param envEntity the FunctionalAgent whose argument name is being generated.
     * @param index the position of the argument in the argument list.
     * @param setOfArgumentNames the (Set of Strings) of argument names already used (for disambiguation purposes).
     * @return the appropriate argument name.
     */
    private static String getNthArgumentName(final CALDocComment caldoc, final FunctionalAgent envEntity, final int index, final Set<String> setOfArgumentNames) {
        ////
        /// First fetch the name from the entity. This will mostly be the same name as the one appearing in code, except for
        /// foreign functions, which may have their sames extracted from the Java classes' debug info.
        //
        String nameFromEntity = null;
        if (index < envEntity.getNArgumentNames()) {
            nameFromEntity = envEntity.getArgumentName(index);
        }
        
        String result;
        
        if (caldoc != null && index < caldoc.getNArgBlocks()) {
            ////
            /// Get the CALDoc name, if available.
            //
            
            final String nameFromCALDoc = caldoc.getNthArgBlock(index).getArgName().getCalSourceForm();
            
            result = nameFromCALDoc;
            setOfArgumentNames.add(nameFromCALDoc);
            
        } else if (nameFromEntity != null) {
            ////
            /// If the entity yielded a name, use it.
            //
            
            result = nameFromEntity;
            setOfArgumentNames.add(nameFromEntity);
            
        } else {
            ////
            /// Since not even the entity yielded a name, construct an artificial one of the form arg_x, where x is the
            /// 1-based index of the argument in the argument list, or of the form arg_x_y if arg_x, arg_x_1, ...
            /// arg_x_(y-1) have all appeared previously in the argument list.
            //
            
            // the base artificial name we'll attempt to use is arg_x, where x is the 1-based index of this argument
            final String baseArtificialName = "arg_" + (index + 1);
            String artificialName = baseArtificialName;
            
            // if the base artificial name already appears in previous arguments, then
            // make the argument name arg_x_y, where y is a supplementary disambiguating number
            // chosen so that the resulting name will not collide with any of the previous argument names
            
            int supplementaryDisambiguator = 1;
            while (setOfArgumentNames.contains(artificialName)) {
                artificialName = baseArtificialName + "_" + supplementaryDisambiguator;
                supplementaryDisambiguator++;
            }
            
            result = artificialName;
            setOfArgumentNames.add(artificialName);
        }
        
        return result;
    }
    
}
