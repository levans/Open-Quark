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
 * PrettyPrintRefactor.cal
 * Created: July 2007
 * By: Magnus Byne
 */

package org.openquark.cal.compiler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openquark.cal.compiler.SourceModel.CALDoc;
import org.openquark.cal.compiler.SourceModel.FunctionDefn;
import org.openquark.cal.compiler.SourceModel.FunctionTypeDeclaration;
import org.openquark.cal.compiler.SourceModel.Import;
import org.openquark.cal.compiler.SourceModel.InstanceDefn;
import org.openquark.cal.compiler.SourceModel.LocalDefn;
import org.openquark.cal.compiler.SourceModel.ModuleDefn;
import org.openquark.cal.compiler.SourceModel.SourceElement;
import org.openquark.cal.compiler.SourceModel.TypeClassDefn;

/**
 * Represents the Pretty Print refactoring for parts of a module.
 *
 * The parts can be defined by a source range or a set of functions. 
 * 
 * It traverse the source model and reformats certain
 * elements that are within the source range or function set.
 * 
 * Currently the elements are limited to top level elements and
 * local functions. 
 * 
 * @author Magnus Byne
 */
final class PrettyPrintRefactorer extends SourceModelTraverser<Void, Void> {

    final private String source;

    final private SourceRange rangeToFormat;
    
    final private Set<String> functionNames;
    
    final private List<SourceEmbellishment> embellishments = new ArrayList<SourceEmbellishment>();

    //this is used to collect modifications as the source model is traversed
    private SourceModifier modifications = null;

    public PrettyPrintRefactorer(String source, SourceRange range, Set<String> functionNames) {
        this.rangeToFormat = range;
        this.source = source;
        this.functionNames = new HashSet<String>(functionNames);
    }
    
    /** returns true if the element is contained within the refactor's source range*/
    private boolean withinRange(SourceElement element) {
        SourceRange el = element.getSourceRangeOfDefn();
    
        if (rangeToFormat == null || el==null) {
            return false;
        }
        
        return rangeToFormat.contains(el);
    }
    
    /**
     * Constructs and returns a SoruceModification that replaces the text at sourceRange of sourceText with newText. 
     * @param sourceText
     * @param sourceRange
     * @param newText
     * @return A SourceModification.ReplaceText
     */
    private static SourceModification makeReplaceText(String sourceText, SourceRange sourceRange, String newText) {
        SourcePosition startPosition = sourceRange.getStartSourcePosition();
        SourcePosition endPosition = sourceRange.getEndSourcePosition();
        int startIndex = startPosition.getPosition(sourceText);
        int endIndex = endPosition.getPosition(sourceText, startPosition, startIndex);
        String oldText = sourceText.substring(startIndex, endIndex);
        
        return new SourceModification.ReplaceText(oldText, newText, startPosition);
    }  

    /** get the source modifications or null if there is an error*/
    public SourceModifier getModifier() {
        modifications = new SourceModifier();
        
        CompilerMessageLogger mlogger= new MessageLogger();

        embellishments.clear();
        ModuleDefn moduleDefn = SourceModelUtilities.TextParsing.parseModuleDefnIntoSourceModel(source, false, mlogger, embellishments);
                   
        if (moduleDefn == null) {
            //unable to parse module
            return null;
        }
        
        if (rangeToFormat == null && functionNames.isEmpty()) {
            //format the whole file
            String formatted = SourceModelCodeFormatter.formatCode(
                    moduleDefn,
                    SourceModelCodeFormatter.DEFAULT_OPTIONS,
                    embellishments
            );

            modifications.addSourceModification(
                    new SourceModification.ReplaceText(source, formatted, new SourcePosition(1, 1, moduleDefn.getModuleName().toString())));
    
        } else {            
            //visit module elements and format those in range
            moduleDefn.accept(this, null);
        }
        return modifications;
    }
    
    /** used to find the embellishments that fall with a range being reformatted*/
    private List<SourceEmbellishment> filterEmbellishments(List<SourceEmbellishment> embellishments, SourceRange range) {
        List<SourceEmbellishment> results = new ArrayList<SourceEmbellishment>();
        for (final SourceEmbellishment em : embellishments) {
            if (range.contains(em.getSourceRange())) {
                results.add(em);
            }
        }
        return results;
    }
    
    /**
     * Attempt to guess the indent level that a programmer was using in 
     * the given source range. Takes the indent from the first line 
     * in the range that is not all white space.
     * 
     * This is used to put local functions at the correct indent level
     */
    private int guessIndent(SourceRange range) {
        
        for (int line = range.getStartLine(); line <= range.getEndLine(); line++) {
            int startIndex = (new SourcePosition(line, 1)).getPosition(source);
        
            int indent =0;
            
            while (startIndex < source.length() && LanguageInfo.isCALWhitespace(source.charAt(startIndex)))
            {
                indent += SourcePosition.columnWidth(indent + 1, source.charAt(startIndex));
                startIndex++;
            }
            
            if (startIndex < source.length() && !LanguageInfo.isCALWhitespace(source.charAt(startIndex))) {
                return  indent;
            }
        
        }
        
        //failed to find a clue for the correct offset - do not indent.
        return 0;
    }
    
    /** 
     * this expands a source range to include leading white spaces -
     * this is used to make sure we remove any whitespace around an element that we
     * are replacing - the correct white space comes form the formatter.
     */
    private SourceRange expandRange(SourceRange range) {
        
        //consume leading spaces
        int offset = range.getStartSourcePosition().getPosition(source);
        int startcol = range.getStartColumn();
        while (startcol > 1 &&
                LanguageInfo.isCALWhitespace( source.charAt(offset - 1))) {
            offset--;
            startcol--;
        }
        
        return new SourceRange(new SourcePosition(range.getStartLine(), startcol),
                range.getEndSourcePosition());
    }

    
    
    /** format an element if it is in the source range to format
     * 
     * @param elem
     * @return true if the element is formatted, otherwise false.
     */
    private boolean formatElement(SourceElement elem) {
        if (withinRange(elem)) {
            //format
            String formatted = SourceModelCodeFormatter.formatCode(
                    elem,
                    SourceModelCodeFormatter.DEFAULT_OPTIONS,
                    filterEmbellishments(embellishments, elem.getSourceRangeOfDefn())
                );
      
            modifications.addSourceModification(
                    makeReplaceText(source, elem.getSourceRange(), formatted));
            return true;
        } else {
            return false;
        }
    }

    /** {@inheritDoc} */
    @Override
    public Void visit_LocalDefn_Function_Definition(
            LocalDefn.Function.Definition algebraic, Void arg) {
        
        if (withinRange(algebraic) || functionNames.contains(algebraic.getName())) {
            //format
            String formatted = SourceModelCodeFormatter.formatCode(
                    algebraic,
                    SourceModelCodeFormatter.DEFAULT_OPTIONS,
                    filterEmbellishments(embellishments, algebraic.getSourceRangeOfDefn()),
                    guessIndent(algebraic.getSourceRangeOfDefn())
            );
            
            modifications.addSourceModification(
                    makeReplaceText(source, expandRange(algebraic.getSourceRange()), formatted));
            return null;
        } else {
            return super.visit_LocalDefn_Function_Definition(algebraic, arg);            
        }
    }
    
    /** {@inheritDoc} */
    @Override
    public Void visit_FunctionTypeDeclaraction(
            FunctionTypeDeclaration declaration, Void arg) {
        if (!formatElement(declaration)) {
            return super.visit_FunctionTypeDeclaraction(declaration, arg);
        } else {
            return null;
        }
    }
    
    @Override
    public Void visit_LocalDefn_PatternMatch_UnpackDataCons(
            LocalDefn.PatternMatch.UnpackDataCons declaration, Void arg) {
        if (!formatElement(declaration)) {
            return super.visit_LocalDefn_PatternMatch_UnpackDataCons(declaration, arg);
        } else {
            return null;
        }
    }
 
    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit_LocalDefn_PatternMatch_UnpackListCons(
        LocalDefn.PatternMatch.UnpackListCons declaration, Void arg) {
        if (!formatElement(declaration)) {
            return super.visit_LocalDefn_PatternMatch_UnpackListCons(declaration, arg);
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit_LocalDefn_PatternMatch_UnpackRecord(
        LocalDefn.PatternMatch.UnpackRecord declaration, Void arg) {
        if (!formatElement(declaration)) {
            return super.visit_LocalDefn_PatternMatch_UnpackRecord(declaration, arg);
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit_LocalDefn_PatternMatch_UnpackTuple(
        LocalDefn.PatternMatch.UnpackTuple declaration, Void arg) {
        if (!formatElement(declaration)) {
            return super.visit_LocalDefn_PatternMatch_UnpackTuple(declaration, arg);
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit_FunctionDefn_Primitive(
            FunctionDefn.Primitive declaration, Void arg) {
        if (!formatElement(declaration)) {
            return super.visit_FunctionDefn_Primitive(declaration, arg);
        } else {
            return null;
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Void visit_LocalDefn_Function_TypeDeclaration(
        LocalDefn.Function.TypeDeclaration declaration, Void arg) {

        if (!formatElement(declaration)) {
            return super.visit_LocalDefn_Function_TypeDeclaration(declaration, arg);
        } else {
            return null;
        }
    }
 

    /** {@inheritDoc} */
    @Override
    public Void visit_Import(
            Import importStmt, Void arg) {
        if (!formatElement(importStmt)) {
            return super.visit_Import(importStmt, arg);
        } else {
            return null;
        }
    }

    /** {@inheritDoc} */
    @Override
    public Void visit_InstanceDefn(
            InstanceDefn defn, Void arg) {
        if (!formatElement(defn)) {
            return super.visit_InstanceDefn(defn, arg);
        } else {
            return null;
        }

    }

    /** {@inheritDoc} */
    @Override
    public Void visit_TypeClassDefn(
            TypeClassDefn defn, Void arg) {
        if (!formatElement(defn)) {
            return super.visit_TypeClassDefn(defn, arg);
        } else {
            return null;
        }

    }

    /** {@inheritDoc} */
    @Override
    protected Void visit_CALDoc_Comment_Helper(
            CALDoc.Comment comment, Void arg) {
        if (!formatElement(comment)) {
            return super.visit_CALDoc_Comment_Helper(comment, arg);
        } else {
            return null;
        }
    }
    
    /** {@inheritDoc} */
    @Override
    public Void visit_FunctionDefn_Foreign(
            FunctionDefn.Foreign foreign, Void arg) {
        if (!formatElement(foreign)) {
            return super.visit_FunctionDefn_Foreign(foreign, arg);
        } else {
            return null;
        }
    }

    
    /** {@inheritDoc} */
    @Override
    public Void visit_FunctionDefn_Algebraic(
            FunctionDefn.Algebraic algebraic, Void arg) {

        if (withinRange(algebraic) || functionNames.contains(algebraic.getName())) {
            //format
            String formatted = SourceModelCodeFormatter.formatCode(
                    algebraic,
                    SourceModelCodeFormatter.DEFAULT_OPTIONS,
                    filterEmbellishments(embellishments, algebraic.getSourceRangeOfDefn())
                );
      
            modifications.addSourceModification(
                    makeReplaceText(source, algebraic.getSourceRange(), formatted));
            return null;
        } else {
            return super.visit_FunctionDefn_Algebraic(algebraic, arg);            
        }
    }
    
}

