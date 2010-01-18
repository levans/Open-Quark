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
 * CALDocToTooltipHTMLUtilities.java
 * Created: Feb 12, 2007
 * By: Joseph Wong
 */

package org.openquark.cal.caldoc;

import java.nio.charset.Charset;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openquark.cal.compiler.CALDocComment;
import org.openquark.cal.compiler.DataConstructor;
import org.openquark.cal.compiler.Function;
import org.openquark.cal.compiler.FunctionalAgent;
import org.openquark.cal.compiler.IdentifierInfo;
import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.ModuleTypeInfo;
import org.openquark.cal.compiler.ScopedEntity;
import org.openquark.cal.compiler.TypeClass;
import org.openquark.cal.compiler.TypeConstructor;
import org.openquark.cal.filter.AcceptAllModulesFilter;
import org.openquark.cal.filter.AcceptAllScopedEntitiesFilter;
import org.openquark.cal.metadata.FunctionalAgentMetadata;
import org.openquark.cal.metadata.MetadataManager;
import org.openquark.cal.services.LocaleUtilities;
import org.openquark.cal.services.ProgramModelManager;

/**
 * This is a utility class containing helper methods for converting CALDoc into
 * properly formatted HTML for use in tooltips in IDEs.
 *
 * @author Joseph Wong
 */
public class CALDocToTooltipHTMLUtilities {
    
    /** Private constructor. This class is not meant to be instantiated. */
    private CALDocToTooltipHTMLUtilities() {}

    /**
     * Generates the HTML representation for the documentation of a ScopedEntity.
     * @param programModelManager the ProgramModelManager for the program containing the entity.
     * @param entity the ScopedEntity to be documented.
     * @return the HTML for the documentation.
     */
    public static String getHTMLForCALDocCommentOfScopedEntity(final ProgramModelManager programModelManager, final ScopedEntity entity) {
        final CALDocComment caldoc = entity.getCALDocComment();
        
        final HTMLDocumentationGeneratorConfiguration config = makeBareboneConfiguration();
        final HTMLDocumentationGenerator docGenerator = new HTMLDocumentationGenerator(programModelManager, null, config, true);
        docGenerator.startNewCurrentPageWithModule(entity.getName().getModuleName());
        
        docGenerator.tooltip_generateScopedEntityHeader(entity.getName().getModuleName());
        
        // keep track of whether a DL block is needed to wrap around the generated HTML
        final boolean needWrappingDL;
        if (entity instanceof FunctionalAgent) {
            final boolean isDataCons = entity instanceof DataConstructor;
            final String unqualifiedName;
            if (entity instanceof Function) {
                // We want to get the display name, in case the entity is a local function with a unique name like f$x$7
                unqualifiedName = ((Function)entity).getUnqualifiedDisplayName();
            } else {
                unqualifiedName = entity.getName().getUnqualifiedName();
            }
            docGenerator.generateFunctionalAgentDoc(unqualifiedName, (FunctionalAgent)entity, "", (FunctionalAgentMetadata)MetadataManager.getEmptyMetadata(entity, LocaleUtilities.INVARIANT_LOCALE), caldoc, 0, !isDataCons);
            needWrappingDL = true;
            
        } else if (entity instanceof TypeClass){
            docGenerator.generateTypeClassDocHeader((TypeClass)entity, 0);
            needWrappingDL = false;
            
        } else if (entity instanceof TypeConstructor){
            docGenerator.generateTypeConsDocHeader((TypeConstructor)entity, 0);
            needWrappingDL = false;
            
        } else {
            return null;
        }
        
        final String style = getHTMLStyle();
        
        if (needWrappingDL) {
            return style + "<dl>" + docGenerator.getCurrentPageHTML() + "</dl>";
        } else {
            return style + docGenerator.getCurrentPageHTML();
        }
    }
    
    /**
     * Generates the HTML representation for the documentation of a local function.
     * @param programModelManager the ProgramModelManager for the program containing the entity.
     * @param entity the ScopedEntity of the local function to be documented.
     * @param localFunction the local function to be documented.
     * @return the HTML for the documentation.
     */
    public static String getHTMLForCALDocCommentOfLocalFunction(final ProgramModelManager programModelManager, final Function entity, final IdentifierInfo.Local.Function localFunction) {
        return getHTMLForCALDocCommentOfLocalFunctionOrPatternMatchVar(programModelManager, entity, localFunction);
    }
    
    /**
     * Generates the HTML representation for the documentation of a local pattern match variable.
     * @param programModelManager the ProgramModelManager for the program containing the entity.
     * @param entity the ScopedEntity of the local pattern match variable to be documented.
     * @param localPatternMatchVar the variable to be documented.
     * @return the HTML for the documentation.
     */
    public static String getHTMLForCALDocCommentOfLocalPatternMatchVar(final ProgramModelManager programModelManager, final Function entity, final IdentifierInfo.Local.PatternMatchVariable localPatternMatchVar) {
        return getHTMLForCALDocCommentOfLocalFunctionOrPatternMatchVar(programModelManager, entity, localPatternMatchVar);
    }

    /**
     * Generates the HTML representation for the documentation of a local function or pattern match variable.
     * @param programModelManager the ProgramModelManager for the program containing the entity.
     * @param entity the ScopedEntity to be documented.
     * @param localFunctionOrPatternMatchVar the variable to be documented.
     * @return the HTML for the documentation.
     */
    private static String getHTMLForCALDocCommentOfLocalFunctionOrPatternMatchVar(final ProgramModelManager programModelManager, final Function entity, final IdentifierInfo.Local localFunctionOrPatternMatchVar) {
        final CALDocComment caldoc = entity.getCALDocComment();
        
        final HTMLDocumentationGeneratorConfiguration config = makeBareboneConfiguration();
        final HTMLDocumentationGenerator docGenerator = new HTMLDocumentationGenerator(programModelManager, null, config, true);
        docGenerator.startNewCurrentPageWithModule(entity.getName().getModuleName());
        
        docGenerator.tooltip_generateLocalNameHeader(localFunctionOrPatternMatchVar);

        docGenerator.generateFunctionalAgentDoc(localFunctionOrPatternMatchVar.getVarName(), entity, "", (FunctionalAgentMetadata)MetadataManager.getEmptyMetadata(entity, LocaleUtilities.INVARIANT_LOCALE), caldoc, 0, true);

        final String style = getHTMLStyle();
        
        return style + "<dl>" + docGenerator.getCurrentPageHTML() + "</dl>";
    }
    
    /**
     * Generates the HTML representation for the documentation of a function or class method parameter.
     * @param programModelManager the ProgramModelManager for the program containing the entity.
     * @param entity the ScopedEntity of the associated top-level/local function or class method.
     * @param parameter the parameter to be documented.
     * @return the HTML for the documentation.
     */
    public static String getHTMLForCALDocCommentOfFunctionParameter(final ProgramModelManager programModelManager, final FunctionalAgent entity, final IdentifierInfo.Local.Parameter parameter) {
        final CALDocComment caldoc = entity.getCALDocComment();
        
        final HTMLDocumentationGeneratorConfiguration config = makeBareboneConfiguration();
        final HTMLDocumentationGenerator docGenerator = new HTMLDocumentationGenerator(programModelManager, null, config, true);
        docGenerator.startNewCurrentPageWithModule(entity.getName().getModuleName());
        
        docGenerator.tooltip_generateLocalNameHeader(parameter);
        docGenerator.tooltip_generateSimpleLocalNameEntry(parameter);
        docGenerator.tooltip_generateHorizontalRule();

        // We want to get the display name, in case the entity is a local function with a unique name like f$x$7
        final String unqualifiedName;
        if (entity instanceof Function) {
            // We want to get the display name, in case the entity is a local function with a unique name like f$x$7
            unqualifiedName = ((Function)entity).getUnqualifiedDisplayName();
        } else {
            unqualifiedName = entity.getName().getUnqualifiedName();
        }
        docGenerator.generateFunctionalAgentDoc(unqualifiedName, entity, "", (FunctionalAgentMetadata)MetadataManager.getEmptyMetadata(entity, LocaleUtilities.INVARIANT_LOCALE), caldoc, 0, true);

        final String style = getHTMLStyle();
        
        return style + "<dl>" + docGenerator.getCurrentPageHTML() + "</dl>";
    }

    /**
     * Generates the HTML representation for the simple tooltip of a local variable without associated documentation.
     * @param programModelManager the ProgramModelManager for the program containing the entity.
     * @param moduleName the name of the associated module.
     * @param localVariable the variable to be documented.
     * @return the HTML for the tooltip.
     */
    public static String getHTMLForSimpleLocalVariable(final ProgramModelManager programModelManager, final ModuleName moduleName, final IdentifierInfo.Local localVariable) {
        final HTMLDocumentationGeneratorConfiguration config = makeBareboneConfiguration();
        final HTMLDocumentationGenerator docGenerator = new HTMLDocumentationGenerator(programModelManager, null, config, true);
        docGenerator.startNewCurrentPageWithModule(moduleName);
        
        docGenerator.tooltip_generateLocalNameHeader(localVariable);
        docGenerator.tooltip_generateSimpleLocalNameEntry(localVariable);

        final String style = getHTMLStyle();
        
        return style + docGenerator.getCurrentPageHTML();
    }

    /**
     * Generates the HTML representation for the simple tooltip of a type variable.
     * @param programModelManager the ProgramModelManager for the program containing the entity.
     * @param moduleName the name of the associated module.
     * @param typeVariable the variable to be documented.
     * @return the HTML for the tooltip.
     */
    public static String getHTMLForTypeVariable(final ProgramModelManager programModelManager, final ModuleName moduleName, final IdentifierInfo.TypeVariable typeVariable) {
        final HTMLDocumentationGeneratorConfiguration config = makeBareboneConfiguration();
        final HTMLDocumentationGenerator docGenerator = new HTMLDocumentationGenerator(programModelManager, null, config, true);
        docGenerator.startNewCurrentPageWithModule(moduleName);
        
        docGenerator.tooltip_generateTypeVariableHeaderAndEntry(typeVariable);

        final String style = getHTMLStyle();
        
        return style + docGenerator.getCurrentPageHTML();
    }

    /**
     * Generates the HTML representation for the simple tooltip of a record field name.
     * @param programModelManager the ProgramModelManager for the program containing the entity.
     * @param moduleName the name of the associated module.
     * @param fieldName the record field name to be documented.
     * @return the HTML for the tooltip.
     */
    public static String getHTMLForRecordFieldName(final ProgramModelManager programModelManager, final ModuleName moduleName, final IdentifierInfo.RecordFieldName fieldName) {
        final HTMLDocumentationGeneratorConfiguration config = makeBareboneConfiguration();
        final HTMLDocumentationGenerator docGenerator = new HTMLDocumentationGenerator(programModelManager, null, config, true);
        docGenerator.startNewCurrentPageWithModule(moduleName);
        
        docGenerator.tooltip_generateRecordFieldNameHeaderAndEntry(fieldName);

        final String style = getHTMLStyle();
        
        return style + docGenerator.getCurrentPageHTML();
    }
    
    /**
     * Generates the HTML representation for the documentation of a data constructor field name.
     * @param programModelManager the ProgramModelManager for the program containing the entity.
     * @param entities a list of the ScopedEntity instances of the associated data cons(es).
     * @param dataConsField the data constructor field to be documented.
     * @return the HTML for the documentation.
     */
    public static String getHTMLForCALDocCommentOfDataConsFieldName(final ProgramModelManager programModelManager, final List<DataConstructor> entities, final IdentifierInfo.DataConsFieldName dataConsField) {
        
        if (entities.isEmpty()) {
            throw new IllegalArgumentException("must have at least one data cons");
        }
        
        final HTMLDocumentationGeneratorConfiguration config = makeBareboneConfiguration();
        final HTMLDocumentationGenerator docGenerator = new HTMLDocumentationGenerator(programModelManager, null, config, true);
        docGenerator.startNewCurrentPageWithModule(entities.get(0).getName().getModuleName());
        
        docGenerator.tooltip_generateDataConsFieldNameHeader(dataConsField);
        docGenerator.tooltip_generateDataConsFieldNameEntry(dataConsField);

        for (final DataConstructor dataCons : entities) {
            docGenerator.tooltip_generateHorizontalRule();

            final CALDocComment caldoc = dataCons.getCALDocComment();
            final String unqualifiedName = dataCons.getName().getUnqualifiedName();
            
            docGenerator.tooltip_generateDefListOpen();
            docGenerator.generateFunctionalAgentDoc(unqualifiedName, (FunctionalAgent)dataCons, "", (FunctionalAgentMetadata)MetadataManager.getEmptyMetadata(dataCons, LocaleUtilities.INVARIANT_LOCALE), caldoc, 0, false);
            docGenerator.tooltip_generateDefListClose();
        }
        
        final String style = getHTMLStyle();
        
        return style + docGenerator.getCurrentPageHTML();
    }
    
    /**
     * Generates the HTML representation for the documentation of a ScopedEntity.
     * @param programModelManager the ProgramModelManager for the program containing the entity.
     * @param moduleTypeInfo the type info for the module to be documented.
     * @return the HTML for the documentation.
     */
    public static String getHTMLForCALDocCommentOfModule(final ProgramModelManager programModelManager, ModuleTypeInfo moduleTypeInfo) {
        final HTMLDocumentationGeneratorConfiguration config = makeBareboneConfiguration();
        final HTMLDocumentationGenerator docGenerator = new HTMLDocumentationGenerator(programModelManager, null, config, true);
        docGenerator.startNewCurrentPageWithModule(moduleTypeInfo.getModuleName());
        
        docGenerator.tooltip_generateModuleEntryHeader(moduleTypeInfo.getModuleName());
        docGenerator.generateModuleDescription(moduleTypeInfo);
        
        final String style = getHTMLStyle();
        return style + docGenerator.getCurrentPageHTML();
    }

    /**
     * @return a barebone configuration for use in tooltip generation.
     */
    private static HTMLDocumentationGeneratorConfiguration makeBareboneConfiguration() {
        final Logger logger = Logger.getAnonymousLogger();
        logger.setLevel(Level.ALL);
        logger.setUseParentHandlers(false);
        
        // Create a barebone configuration
        final HTMLDocumentationGeneratorConfiguration config = new HTMLDocumentationGeneratorConfiguration(
            new FileGenerator() {
                public void generateTextFile(String fileName, String content, Charset charset) {}
                public void generateTextFile(String subdirectory, String fileName, String content, Charset charset) {}
                },
            new AcceptAllModulesFilter(),
            new AcceptAllScopedEntitiesFilter(),
            false,
            true,
            true,
            true,
            true,
            false,
            false,
            "",
            "",
            "",
            "",
            "",
            LocaleUtilities.INVARIANT_LOCALE,
            logger);
        return config;
    }

    /**
     * @return the style parameters used by the CALDoc functions. 
     */
    public static String getHTMLStyle(){
        return "<style type='text/css'>\n" + HTMLDocumentationGenerator.getCompactDisplayCSS() + "\n</style>\n";    
    }
}
