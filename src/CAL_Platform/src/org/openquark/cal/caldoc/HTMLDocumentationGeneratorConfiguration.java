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
 * HTMLDocumentationGeneratorConfiguration.java
 * Creation date: Oct 14, 2005.
 * By: Joseph Wong
 */
package org.openquark.cal.caldoc;

import java.util.Locale;
import java.util.logging.Logger;

import org.openquark.cal.filter.ModuleFilter;
import org.openquark.cal.filter.ScopedEntityFilter;


/**
 * This class encapsulates the configuration options for the HTML documentation generator.
 *
 * @author Joseph Wong
 */
public class HTMLDocumentationGeneratorConfiguration {
    /** The file generator to be used by the documentation generator for producing files. */
    final FileGenerator fileGenerator;
    
    /** The filter for determining whether documentation is to be generated for a module or a scoped entity. */
    final DocumentationGenerationFilter filter;
    
    /** The logger to be used for logging status messages. */
    final Logger logger;

    /** Whether the documentation should include metadata. */
    final boolean shouldGenerateFromMetadata;

    /** Whether CALDoc should always be included in the documentation regardless of whether metadata is included or not. */
    final boolean shouldAlwaysGenerateFromCALDoc;

    /** Whether author info should be generated. */
    final boolean shouldGenerateAuthorInfo;

    /** Whether version info should be generated. */
    final boolean shouldGenerateVersionInfo;

    /** Whether Prelude names should always be displayed as unqualified. */
    final boolean shouldDisplayPreludeNamesAsUnqualified;
    
    /** Whether the usage indices shold be generated. */
    final boolean shouldGenerateUsageIndices;
    
    /** Whether instance documentation should be separated from the main documentation pages. */
    final boolean shouldSeparateInstanceDoc;
    
    /** The window title text. */
    final String windowTitle;
    
    /** The documentation title in HTML. */
    final String docTitle;
    
    /** The header in HTML. */
    final String header;
    
    /** The footer in HTML. */
    final String footer;
    
    /** The HTML for the fine print at the bottom of a page. */
    final String bottom;

    /** The Locale for the generated documentation, in particular the metadata if included. */
    final Locale locale;

    /**
     * Constructs an instance of HTMLDocumentationGeneratorConfiguration.
     * 
     * @param fileGenerator
     *            the file generator to be used by the documentation generator for producing files.
     * @param moduleFilter
     *            the module filter to be employed.
     * @param scopedEntityFilter
     *            the scoped entity filter to be employed.
     * @param shouldGenerateFromMetadata
     *            whether the documentation should include metadata.
     * @param shouldAlwaysGenerateFromCALDoc
     *            whether CALDoc should always be included in the documentation
     *            regardless of whether metadata is included or not.
     * @param shouldGenerateAuthorInfo
     *            whether author info should be generated.
     * @param shouldGenerateVersionInfo
     *            whether version info should be generated.
     * @param shouldDisplayPreludeNamesAsUnqualified
     *            whether Prelude names should always be displayed as
     *            unqualified.
     * @param shouldGenerateUsageIndices
     *            whether the usage indices should be generated.
     * @param shouldSeparateInstanceDoc
     *            winstance documentation should be separated from the main documentation pages.
     * @param windowTitle
     *            the window title text.
     * @param docTitle
     *            the documentation title HTML.
     * @param header
     *            the header HTML.
     * @param footer
     *            the footer HTML.
     * @param bottom
     *            the HTML for the fine print at the bottom of a page.
     * @param locale
     *            the Locale for the generated documentation, in particular the
     *            metadata if included.
     * @param logger
     *            the logger to be used for logging status messages.
     */
    public HTMLDocumentationGeneratorConfiguration(
        FileGenerator fileGenerator,
        ModuleFilter moduleFilter,
        ScopedEntityFilter scopedEntityFilter,
        boolean shouldGenerateFromMetadata,
        boolean shouldAlwaysGenerateFromCALDoc,
        boolean shouldGenerateAuthorInfo,
        boolean shouldGenerateVersionInfo,
        boolean shouldDisplayPreludeNamesAsUnqualified,
        boolean shouldGenerateUsageIndices,
        boolean shouldSeparateInstanceDoc,
        String windowTitle,
        String docTitle,
        String header,
        String footer,
        String bottom,
        Locale locale,
        Logger logger) {
        
        AbstractDocumentationGenerator.verifyArg(fileGenerator, "fileGenerator");
        this.fileGenerator = fileGenerator;
        
        AbstractDocumentationGenerator.verifyArg(moduleFilter, "moduleFilter");
        AbstractDocumentationGenerator.verifyArg(scopedEntityFilter, "scopedEntityFilter");
        this.filter = new DocumentationGenerationFilter(moduleFilter, scopedEntityFilter);
        
        this.shouldGenerateFromMetadata = shouldGenerateFromMetadata;
        this.shouldAlwaysGenerateFromCALDoc = shouldAlwaysGenerateFromCALDoc;
        this.shouldGenerateAuthorInfo = shouldGenerateAuthorInfo;
        this.shouldGenerateVersionInfo = shouldGenerateVersionInfo;
        this.shouldDisplayPreludeNamesAsUnqualified = shouldDisplayPreludeNamesAsUnqualified;
        
        this.shouldGenerateUsageIndices = shouldGenerateUsageIndices;
        
        this.shouldSeparateInstanceDoc = shouldSeparateInstanceDoc;
        
        AbstractDocumentationGenerator.verifyArg(windowTitle, "windowTitle");
        this.windowTitle = windowTitle;
        
        AbstractDocumentationGenerator.verifyArg(docTitle, "docTitle");
        this.docTitle = docTitle;
        
        AbstractDocumentationGenerator.verifyArg(header, "header");
        this.header = header;
        
        AbstractDocumentationGenerator.verifyArg(footer, "footer");
        this.footer = footer;
        
        AbstractDocumentationGenerator.verifyArg(bottom, "bottom");
        this.bottom = bottom;
        
        AbstractDocumentationGenerator.verifyArg(locale, "locale");
        this.locale = locale;
        
        AbstractDocumentationGenerator.verifyArg(logger, "logger");
        this.logger = logger;
    }
}