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
 * HTMLDocumentationGenerator.java
 * Creation date: Sep 27, 2005.
 * By: Joseph Wong
 */
package org.openquark.cal.caldoc;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.text.html.CSS;
import javax.swing.text.html.HTML;

import org.openquark.cal.caldoc.CALDocToHTMLUtilities.ContentConvertibleToHTML;
import org.openquark.cal.caldoc.CALDocToHTMLUtilities.SimpleStringContent;
import org.openquark.cal.caldoc.CALDocToHTMLUtilities.SingleTextBlockContent;
import org.openquark.cal.caldoc.HTMLBuilder.AttributeList;
import org.openquark.cal.compiler.CALDocComment;
import org.openquark.cal.compiler.ClassInstance;
import org.openquark.cal.compiler.ClassInstanceIdentifier;
import org.openquark.cal.compiler.ClassMethod;
import org.openquark.cal.compiler.DataConstructor;
import org.openquark.cal.compiler.ForeignTypeInfo;
import org.openquark.cal.compiler.Function;
import org.openquark.cal.compiler.FunctionalAgent;
import org.openquark.cal.compiler.IdentifierInfo;
import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.ModuleTypeInfo;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.Scope;
import org.openquark.cal.compiler.ScopedEntity;
import org.openquark.cal.compiler.ScopedEntityNamingPolicy;
import org.openquark.cal.compiler.SourceModel;
import org.openquark.cal.compiler.SourceModelTraverser;
import org.openquark.cal.compiler.TypeClass;
import org.openquark.cal.compiler.TypeConstructor;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.compiler.UnableToResolveForeignEntityException;
import org.openquark.cal.internal.javamodel.JavaTypeName;
import org.openquark.cal.metadata.ArgumentMetadata;
import org.openquark.cal.metadata.CALFeatureMetadata;
import org.openquark.cal.metadata.ClassMethodMetadata;
import org.openquark.cal.metadata.FunctionMetadata;
import org.openquark.cal.metadata.FunctionalAgentMetadata;
import org.openquark.cal.metadata.InstanceMethodMetadata;
import org.openquark.cal.metadata.MetadataManager;
import org.openquark.cal.metadata.ScopedEntityMetadata;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.services.CALFeatureName;
import org.openquark.cal.services.CALWorkspace;
import org.openquark.cal.services.FeatureName;
import org.openquark.cal.services.LocaleUtilities;
import org.openquark.cal.services.ProgramModelManager;
import org.openquark.cal.services.WorkspaceManager;
import org.openquark.util.Pair;


/**
 * This class implements the facility of converting CALDoc and metadata into HTML documentation.
 * <p>
 * 
 * Currently, the files that are generated include:
 * <ul>
 *   <li>one CSS file for formatting for regular display
 *   <li>one CSS file for formatting for printing
 *   <li>a frameset
 *   <li>an overview page
 *   <li>a modules list
 *   <li>a cross-module search page
 *   <li>per module:
 *   <ul>
 *     <li>the module's main documentation page
 *     <li>a types list
 *     <li>a functional agents list
 *     <li>a type classes list
 *     <li>a class instances list
 *   </ul>
 *   <li>one usage indices page per type
 *   <li>one usage indices page per type class
 * </ul>
 * 
 * Not being public, this class is not mean to be accessed directly. To access this generator, go through the
 * public interface encapsulated by CALDocTool.
 * <p>
 * 
 * A substantial portion of the high level traversal logic is encapsulated in the superclass, and as such this
 * class's main focus is implementing the specific details of formatting the output in HTML. The class is organized
 * in a way that is similar to a visitor in the visitor pattern, in that the implementation of the abstract methods
 * are called to "visit" a particular CAL entity to generate its documentation.
 * <p>
 * 
 * The current visitation pattern is:
 * <pre>
 * generateDoc
 *  - for each module: prepareGenerationForModule
 *  
 *  beginDoc
 *  
 *  generateModuleDoc
 *      beginModuleDoc
 *      
 *      generateModuleHeaderSection
 *          generateModuleDescription
 *          beginImportedModulesList
 *          generateImportedModule
 *          endImportedModuleList
 *          beginFriendModulesList
 *          generateFriendModule
 *          endFriendModuleList
 *          beginDirectlyDependentModulesList
 *          generateDirectlyDependentModule
 *          endDirectlyDependentModulesList
 *          beginIndirectlyDependentModulesList
 *          generateIndirectlyDependentModule
 *          endIndirectlyDependentModulesList
 *          
 *      generateModuleOverviewSection
 *          beginModuleOverviewSection
 *          
 *          beginTypeConsOverviewSection
 *          generateTypeConsOverview
 *              generateTypeConsOverviewHeader
 *              beginDataConsOverviewList
 *              generateDataConsOverview
 *              endDataConsOverviewList
 *              generateTypeConsOverviewFooter
 *          endTypeConsOverviewSection
 *          
 *          beginFunctionsOverviewSection
 *          generateFunctionOverview
 *          endFunctionsOverviewSection
 *          
 *          beginTypeClassOverviewSection
 *          generateTypeClassOverview
 *              generateTypeClassOverviewHeader
 *              beginClassMethodOverviewList
 *              generateClassMethodOverview
 *              endClassMethodOverviewList
 *              generateTypeClassOverviewFooter
 *          endTypeClassOverviewSection
 *          
 *          beginClassInstancesOverviewSection
 *          generateClassInstanceOverview
 *              generateClassInstanceOverviewHeader
 *              beginInstanceMethodOverviewList
 *              generateInstanceMethodOverview
 *              endInstanceMethodOverviewList
 *              generateClassInstanceOverviewFooter
 *          endClassInstancesOverviewSection
 *          
 *          endModuleOverviewSection
 *          
 *      generateModuleDetailsSection
 *          generateTypeConsDocSection
 *              beginTypeConsDocSection
 *              
 *              generateTypeConsDoc
 *                  generateTypeConsDocHeader
 *                  
 *                  beginDataConsDocList
 *                  generateDataConsDoc
 *                  endDataConsDocList
 *                  
 *                  beginKnownInstancesList
 *                  generateKnownInstance
 *                  endKnownInstancesList
 *                  
 *                  generateTypeConsDocFooter
 *              
 *              endTypeConsDocSection
 *          
 *          generateFunctionsDocSection
 *              beginFunctionsDocSection
 *              
 *              generateFunctionDoc
 *              
 *              endFunctionsDocSection
 *          
 *          generateTypeClassesDocSection
 *              beginTypeClassesDocSection
 *              
 *              generateTypeClassDoc
 *                  generateTypeClassDocHeader
 *                  
 *                  beginClassMethodDocList
 *                  generateClassMethodDoc
 *                  endClassMethodDocList
 *                  
 *                  beginKnownInstancesList
 *                  generateKnownInstance
 *                  endKnownInstancesList
 *                  
 *                  generateTypeClassDocFooter
 *              
 *              endTypeClassesDocSection
 *          
 *          generateClassInstancesDocSection
 *              beginClassInstancesDocSection
 *              
 *              generateClassInstanceDoc
 *                  generateClassInstanceDocHeader
 *                  
 *                  beginInstanceMethodDocList
 *                  generateInstanceMethodDoc
 *                  endInstanceMethodDocList
 *                  
 *                  generateClassInstanceDocFooter
 *              
 *              endClassInstancesDocSection
 *      
 *      endModuleDoc
 *  
 *  genereateUsageIndices
 *      beginTypeConsUsageDoc
 *      
 *      generateUsageIndiciesForTypeConsOrTypeClass
 *          generateUsageIndiciesForDependentModule
 *              beginUsageDocGroupForDependentModule
 *              
 *              beginUsageDocArgTypeIndex
 *              generateUsageDocArgTypeIndexEntry
 *              endUsageDocArgTypeIndex
 *              
 *              beginUsageDocReturnTypeIndex
 *              generateUsageDocReturnTypeIndexEntry
 *              endUsageDocReturnTypeIndex
 *              
 *              beginUsageDocInstanceIndex
 *              generateUsageDocInstanceIndexEntry
 *              endUsageDocInstanceIndex
 *              
 *              endUsageDocGroupForDependentModule
 *          
 *      endTypeConsUsageDoc
 *      
 *      beginTypeClassUsageDoc
 *      
 *      generateUsageIndiciesForTypeConsOrTypeClass
 *          generateUsageIndiciesForDependentModule
 *              beginUsageDocGroupForDependentModule
 *              
 *              beginUsageDocArgTypeIndex
 *              generateUsageDocArgTypeIndexEntry
 *              endUsageDocArgTypeIndex
 *              
 *              beginUsageDocReturnTypeIndex
 *              generateUsageDocReturnTypeIndexEntry
 *              endUsageDocReturnTypeIndex
 *              
 *              beginUsageDocInstanceIndex
 *              generateUsageDocInstanceIndexEntry
 *              endUsageDocInstanceIndex
 *              
 *              endUsageDocGroupForDependentModule
 *      
 *      endTypeClassUsageDoc
 *  
 *  endDoc
 * </pre>
 * 
 * @see CALDocTool
 *
 * @author Joseph Wong
 */
final class HTMLDocumentationGenerator extends AbstractDocumentationGenerator {

    /** The name of the subdirectory containing the main documentation pages for the modules. */
    private static final String MODULES_SUBDIRECTORY = "modules";
    
    /** The name of the subdirectory containing the usage pages for types. */
    private static final String TYPE_CONS_USAGE_SUBDIRECTORY = "types";
    
    /** The name of the subdirectory containing the usage pages for type classes. */
    private static final String TYPE_CLASS_USAGE_SUBDIRECTORY = "typeClasses";
    
    /** The name of the subdirectory containing instance documentation that is separated from the main documentation pages. */
    private static final String SEPARATE_INSTANCE_DOC_SUBDIRECTORY = "instances";
    
    /** The name of the subdirectory containing the intra-module navigation pages. */
    private static final String NAV_SUBDIRECTORY = "nav";

    /** The dot used in constructing file names */
    private static final String DOT = ".";

    /** The separator between the main part of a file name and the postfix, which comes before the extension. */
    private static final String FILENAME_POSTFIX_SEPARATOR = "-";

    /** The file name postfix for the instance index of a module. */
    private static final String INSTANCE_INDEX_FILENAME_POSTFIX = "instanceIndex";

    /** The file name postfix for the type class index of a module. */
    private static final String TYPE_CLASS_INDEX_FILENAME_POSTFIX = "typeClassIndex";

    /** The file name postfix for the functional agent index of a module. */
    private static final String FUNCTIONAL_AGENT_INDEX_FILENAME_POSTFIX = "functionalAgentIndex";

    /** The file name postfix for the type index of a module. */
    private static final String TYPE_INDEX_FILENAME_POSTFIX = "typeIndex";
    
    /** The file name postfix for the separate instance documentation. */
    private static final String SEPARATE_INSTANCE_DOC_FILENAME_POSTFIX = "instances";

    /** The file extension for HTML. */
    private static final String HTML_FILE_EXTENSION = "html";

    /** The name of the default CSS file. */
    private static final String DEFAULT_CSS_FILENAME = "caldoc.css";

    /** The name of the CSS file for printing. */
    private static final String PRINTED_VERSION_CSS_FILENAME = "caldoc-print.css";
    
    /** The name of the main documentation HTML file. */
    private static final String MAIN_PAGE_FILENAME = "index.html";

    /** The name of the module list HTML file. */
    private static final String MODULE_LIST_FILENAME = "moduleList.html";

    /** The name of the overview page HTML file. */
    private static final String OVERVIEW_PAGE_FILENAME = "overview.html";

    /** The name of the master scoped entity index HTML file. */
    private static final String MASTER_SCOPED_ENTITY_SEARCH_PAGE_FILENAME = "search.html";

    /** The name for the module list frame in its frameset. */
    private static final String MODULE_LIST_FRAME_NAME = "moduleListFrame";
    
    /** The name of the navigation frame in its frameset. */
    private static final String NAV_FRAME_NAME = "navFrame";
    
    /** The name of the main frame in its frameset. */
    private static final String MAIN_FRAME_NAME = "mainFrame";
    
    /** The name of the search frame in its frameset. */
    private static final String SEARCH_FRAME_NAME = "searchFrame";

    /////====================================================================================================
    ////
    /// Fields
    //
    
    /** The configuration for this documentation generator. */
    private final HTMLDocumentationGeneratorConfiguration config;
    
    /** The CALWorkspace for metadata access. Can be null. */
    private final CALWorkspace workspace;
    
    /** Whether to globally disable all hyperlink generation. */
    private final boolean disableAllHyperlinks;
    
    /** Keeps track of the name of the current module whose documentation is being generated. */
    private ModuleName currentModuleName = null;
    
    /** The current HTML page being generated. */
    private HTMLBuilder currentPage = null;
    
    /** The main page being generated (for use to cache the {@link #currentPage} when generating separated instance doc. */
    private HTMLBuilder mainPageContext = null;
    
    /** Whether the generator is currently generating some separate instance documentation. */
    private boolean inSeparateInstanceDoc = false;
    
    /** The set of imported module names for the current module. */
    private Set<ModuleName> importedModules = null;
    
    /** The set of friend module names for the current module. */
    private Set<ModuleName> friendModules = null;
    
    /** The set of directly dependent module names for the current module. */
    private Set<ModuleName> directlyDependentModules = null;

    /** The set of indirectly dependent module names for the current module. */
    private Set<ModuleName> indirectlyDependentModules = null;

    // Some cached statistics about the current module.
    private int nTypeConstructorsInModule = 0;
    private int nFunctionsInModule = 0;
    private int nTypeClassesInModule = 0;
    private int nClassInstancesInModule = 0;
    
    /**
     * A nested map mapping:
     * <p>
     * lowercased module name -&gt; module name -&gt; disambiguated name
     * <p>
     * for determining how a module name should be rendered when used as part of a file name on a file system
     * that is case-insensitive.
     * <p>
     * A disambiguation map works like this: Given a name (with mixed upper and
     * lower case)
     * <ol>
     * <li>get the lowercase version of the name - names that conflict because
     * of case-insensitivity would map to the same lowercased name.
     * 
     * <li>using the lowercased name, get a map mapping the original names to
     * disambiguated names.
     * 
     * <li>if the map is empty, insert the pair (name, name) (the first-comer
     * gets to keep its name without disambiguation).
     * 
     * <li>if the map already has entries, the name needs to be mangled to
     * create a disambiguated version. Then the pair (name, disambiguated name)
     * is added to the map.
     * </ol>
     * 
     * For retrieval, simply repeat the same process as above, except instead of
     * adding an entry to the inner map, the inner map is simply accessed to
     * retrieve the mapping for the name.
     * 
     */
    private final Map<String, Map<String, String>> disambiguationMapForModuleNames = new HashMap<String, Map<String, String>>();
    
    /**
     * A nested map mapping:
     * <p>
     * module name -&gt; lowercased type constructor name -&gt; type constructor name -&gt; disambiguated name
     * <p>
     * for determining how a type constructor name should be rendered when used as part of a file name on a file system
     * that is case-insensitive.
     */
    private final Map<ModuleName, Map<String, Map<String, String>>> disambiguationMapForTypeConsNames = new HashMap<ModuleName, Map<String, Map<String, String>>>();

    /**
     * A nested map mapping:
     * <p>
     * module name -&gt; lowercased type class name -&gt; type class name -&gt; disambiguated name
     * <p>
     * for determining how a type class name should be rendered when used as part of a file name on a file system
     * that is case-insensitive.
     */
    private final Map<ModuleName, Map<String, Map<String, String>>> disambiguationMapForTypeClassNames = new HashMap<ModuleName, Map<String, Map<String, String>>>();
    
    /** The reference generator for use with the conversion of CALDoc text blocks to HTML when the current directory is the modules subdirectory. */
    private final ReferenceGenerator inModulesSubdirectoryReferenceGenerator = new ReferenceGenerator();
    
    
    /////====================================================================================================
    ////
    /// Inner classes
    //
    /////====================================================================================================
    
    /**
     * A type-safe enumeration of the localizable user-visible strings potentially
     * emitted by the documentation generator.
     *
     * @author Joseph Wong
     */
    private static final class LocalizableUserVisibleString {
    
        ////
        /// Stylesheet names
        //
        private static final LocalizableUserVisibleString DEFAULT = new LocalizableUserVisibleString("default");
        private static final LocalizableUserVisibleString FOR_PRINTING = new LocalizableUserVisibleString("forPrinting");
        
        ////
        /// Main documentation
        //
        private static final LocalizableUserVisibleString WINDOW_TITLE_TEMPLATE = new LocalizableUserVisibleString("windowTitleTemplate");
        private static final LocalizableUserVisibleString OVERVIEW = new LocalizableUserVisibleString("overview");
        private static final LocalizableUserVisibleString MODULE_SUMMARY = new LocalizableUserVisibleString("moduleSummary");
        private static final LocalizableUserVisibleString TYPE_CLASSES = new LocalizableUserVisibleString("typeClasses");
        private static final LocalizableUserVisibleString MODULES = new LocalizableUserVisibleString("modules");
        private static final LocalizableUserVisibleString INSTANCES = new LocalizableUserVisibleString("instances");
        private static final LocalizableUserVisibleString TYPES = new LocalizableUserVisibleString("types");
        private static final LocalizableUserVisibleString FUNCTIONS_CLASS_METHODS_AND_DATA_CONSTRUCTORS = new LocalizableUserVisibleString("functionsClassMethodsAndDataConstructors");
        private static final LocalizableUserVisibleString FUNCTIONS = new LocalizableUserVisibleString("functions");
        private static final LocalizableUserVisibleString TYPE_INDEX = new LocalizableUserVisibleString("typeIndex");
        private static final LocalizableUserVisibleString FUNCTIONAL_AGENT_INDEX = new LocalizableUserVisibleString("functionalAgentIndex");
        private static final LocalizableUserVisibleString TYPE_CLASS_INDEX = new LocalizableUserVisibleString("typeClassIndex");
        private static final LocalizableUserVisibleString INSTANCE_INDEX = new LocalizableUserVisibleString("instanceIndex");
        private static final LocalizableUserVisibleString DEPRECATED_COLON = new LocalizableUserVisibleString("deprecatedColon");
        private static final LocalizableUserVisibleString RETURNS_COLON = new LocalizableUserVisibleString("returnsColon");
        private static final LocalizableUserVisibleString COMMA_AND_SPACE = new LocalizableUserVisibleString("commaAndSpace");
        private static final LocalizableUserVisibleString AUTHOR_COLON = new LocalizableUserVisibleString("authorColon");
        private static final LocalizableUserVisibleString VERSION_COLON = new LocalizableUserVisibleString("versionColon");
        private static final LocalizableUserVisibleString SEE_ALSO_COLON = new LocalizableUserVisibleString("seeAlsoColon");
        private static final LocalizableUserVisibleString FOREIGN_TYPE_COLON = new LocalizableUserVisibleString("foreignTypeColon");
        private static final LocalizableUserVisibleString IMPLEMENTATION_VISIBILITY_COLON = new LocalizableUserVisibleString("implementationVisibilityColon");
        private static final LocalizableUserVisibleString CALDOC_INDICATOR = new LocalizableUserVisibleString("caldocIndicator");
        private static final LocalizableUserVisibleString METADATA_INDICATOR = new LocalizableUserVisibleString("metadataIndicator");
        private static final LocalizableUserVisibleString ARGUMENTS_COLON = new LocalizableUserVisibleString("argumentsColon");
        private static final LocalizableUserVisibleString RETURN_VALUE_INDICATOR = new LocalizableUserVisibleString("returnValueIndicator");
        private static final LocalizableUserVisibleString COLON = new LocalizableUserVisibleString("colon");
        private static final LocalizableUserVisibleString IMPORTED_MODULES_COLON = new LocalizableUserVisibleString("importedModulesColon");
        private static final LocalizableUserVisibleString FRIEND_MODULES_COLON = new LocalizableUserVisibleString("friendModulesColon");
        private static final LocalizableUserVisibleString DIRECTLY_DEPENDENT_MODULES_COLON = new LocalizableUserVisibleString("directlyDependentModulesColon");
        private static final LocalizableUserVisibleString INDIRECTLY_DEPENDENT_MODULES_COLON = new LocalizableUserVisibleString("indirectlyDependentModulesColon");
        private static final LocalizableUserVisibleString DATA_CONSTRUCTORS = new LocalizableUserVisibleString("dataConstructors");
        private static final LocalizableUserVisibleString CLASS_METHODS = new LocalizableUserVisibleString("classMethods");
        private static final LocalizableUserVisibleString KNOWN_INSTANCES = new LocalizableUserVisibleString("knownInstances");
        private static final LocalizableUserVisibleString INSTANCE_METHODS = new LocalizableUserVisibleString("instanceMethods");
        private static final LocalizableUserVisibleString NAV_BAR_ITEM_SEPARATOR = new LocalizableUserVisibleString("navBarItemSeparator");
        private static final LocalizableUserVisibleString OPTIONS = new LocalizableUserVisibleString("options");
        private static final LocalizableUserVisibleString SCOPE_FILTER_COLON = new LocalizableUserVisibleString("scopeFilterColon");
        private static final LocalizableUserVisibleString PUBLIC_ITEMS_ONLY = new LocalizableUserVisibleString("publicItemsOnly");
        private static final LocalizableUserVisibleString SHOW_ALL_ITEMS = new LocalizableUserVisibleString("showAllItems");
        private static final LocalizableUserVisibleString REQUIRED_METHOD_INDICATOR = new LocalizableUserVisibleString("requiredMethodIndicator");
        private static final LocalizableUserVisibleString OPTIONAL_METHOD_INDICATOR = new LocalizableUserVisibleString("optionalMethodIndicator");
        
        ////
        /// Module list
        //
        private static final LocalizableUserVisibleString VIEW_COLON = new LocalizableUserVisibleString("viewColon");
        private static final LocalizableUserVisibleString FLAT = new LocalizableUserVisibleString("flat");
        private static final LocalizableUserVisibleString GROUPED = new LocalizableUserVisibleString("grouped");
        private static final LocalizableUserVisibleString HIERARCHICAL = new LocalizableUserVisibleString("hierarchical");
        private static final LocalizableUserVisibleString EXPAND_ALL = new LocalizableUserVisibleString("expandAll");
        private static final LocalizableUserVisibleString COLLAPSE_ALL = new LocalizableUserVisibleString("collapseAll");
        private static final LocalizableUserVisibleString EXPAND_BUTTON_LABEL = new LocalizableUserVisibleString("expandButtonLabel");
        private static final LocalizableUserVisibleString COLLAPSE_BUTTON_LABEL = new LocalizableUserVisibleString("collapseButtonLabel");
        
        ////
        /// Search page
        //
        private static final LocalizableUserVisibleString SEARCH = new LocalizableUserVisibleString("search");
        private static final LocalizableUserVisibleString HIDE = new LocalizableUserVisibleString("hide");
        private static final LocalizableUserVisibleString NO_MATCHES = new LocalizableUserVisibleString("noMatches");
        private static final LocalizableUserVisibleString ENTER_SEARCH_TERM_COLON = new LocalizableUserVisibleString("enterSearchTermColon");
        private static final LocalizableUserVisibleString SEARCH_RESULT_SUMMARY = new LocalizableUserVisibleString("searchResultSummary");
        private static final LocalizableUserVisibleString SEARCH_RESULT_SUMMARY_PUBLIC = new LocalizableUserVisibleString("searchResultSummaryPublic");
        private static final LocalizableUserVisibleString SEARCH_RESULT_SUMMARY_PROTECTED = new LocalizableUserVisibleString("searchResultSummaryProtected");
        private static final LocalizableUserVisibleString SEARCH_RESULT_SUMMARY_PRIVATE = new LocalizableUserVisibleString("searchResultSummaryPrivate");
        
        ////
        /// Links between main documentation and usage indices
        //
        private static final LocalizableUserVisibleString USAGE_INDEX_ENTRY = new LocalizableUserVisibleString("usageIndexEntry");
        private static final LocalizableUserVisibleString MAIN_ENTRY = new LocalizableUserVisibleString("mainEntry");
        
        ////
        /// Usage indices
        //
        private static final LocalizableUserVisibleString USAGE_DOC_ARGUMENT_TYPE_INDEX = new LocalizableUserVisibleString("usageDocArgumentTypeIndex");
        private static final LocalizableUserVisibleString USAGE_DOC_RETURN_TYPE_INDEX = new LocalizableUserVisibleString("usageDocReturnTypeIndex");
        private static final LocalizableUserVisibleString USAGE_DOC_INSTANCE_INDEX_INSTANCES_OF = new LocalizableUserVisibleString("usageDocInstanceIndexInstancesOf");
        private static final LocalizableUserVisibleString USAGE_DOC_INSTANCE_INDEX_INSTANCES_FOR = new LocalizableUserVisibleString("usageDocInstanceIndexInstancesFor");
        
        private static final LocalizableUserVisibleString DEFINING_MODULE_COLON = new LocalizableUserVisibleString("definingModuleColon");
        private static final LocalizableUserVisibleString DEPENDENT_MODULE_COLON = new LocalizableUserVisibleString("dependentModuleColon");
        
        ////
        /// Separate instance documentation
        //
        private static final LocalizableUserVisibleString INSTANCE_DOC_TITLE = new LocalizableUserVisibleString("instanceDocTitle");
        
        ////
        /// CALDoc tooltips
        //
        private static final LocalizableUserVisibleString MODULE = new LocalizableUserVisibleString("module");
        private static final LocalizableUserVisibleString FIELD_OF_DATA_CONSTRUCTORS = new LocalizableUserVisibleString("fieldOfDataConstructors");
        private static final LocalizableUserVisibleString FIELD_OF_DATA_CONSTRUCTOR_AND_SPACE = new LocalizableUserVisibleString("fieldOfDataConstructorAndSpace");
        private static final LocalizableUserVisibleString LOCAL_FUNCTION = new LocalizableUserVisibleString("localFunction");
        private static final LocalizableUserVisibleString LOCAL_PATTERN_MATCH_VARIABLE = new LocalizableUserVisibleString("localPatternMatchVariable");
        private static final LocalizableUserVisibleString CASE_PATTERN_VARIABLE = new LocalizableUserVisibleString("casePatternVariable");
        private static final LocalizableUserVisibleString PARAMETER_OF_AND_SPACE = new LocalizableUserVisibleString("parameterOfAndSpace");
        private static final LocalizableUserVisibleString PARAMETER_OF_LOCAL_FUNCTION_AND_SPACE = new LocalizableUserVisibleString("parameterOfLocalFunctionAndSpace");
        private static final LocalizableUserVisibleString PARAMETER_OF_LAMBDA_EXPRESSION = new LocalizableUserVisibleString("parameterOfLambdaExpression");
        private static final LocalizableUserVisibleString PARAMETER_OF_INSTANCE_METHOD = new LocalizableUserVisibleString("parameterOfInstanceMethod");
        private static final LocalizableUserVisibleString LOCAL_VARIABLE = new LocalizableUserVisibleString("localVariable");
        private static final LocalizableUserVisibleString TYPE_VARIABLE = new LocalizableUserVisibleString("typeVariable");
        private static final LocalizableUserVisibleString RECORD_FIELD_NAME = new LocalizableUserVisibleString("recordFieldName");
        
        /** The key into the caldoc.properties file. */
        private final String propKey;
    
        /** Private constructor. */
        private LocalizableUserVisibleString(String propKey) {
            if (propKey == null) {
                throw new NullPointerException();
            }
            
            this.propKey = propKey;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return "!" + propKey + "!";
        }
        
        /**
         * @return the localized resource string.
         */
        String toResourceString() {
            return CALDocMessages.getString(propKey);
        }
        
        /**
         * @param arg0 the first substitution parameter.
         * @return the localized resource string.
         */
        String toResourceString(String arg0) {
            return CALDocMessages.getString(propKey, arg0);
        }
        
        /**
         * @param arg0 the first substitution parameter.
         * @param arg1 the second substitution parameter.
         * @return the localized resource string.
         */
        String toResourceString(String arg0, String arg1) {
            return CALDocMessages.getString(propKey, arg0, arg1);
        }
    }

    /**
     * A set of the CAL fragment strings employed by the documentation generator.<p>
     * 
     * <em>These are not to be localized!</em>
     *
     * @author Joseph Wong
     */
    private static final class CALFragments {
    
        /** Private constructor. */
        private CALFragments() {}
        
        private static final String COMMA_AND_SPACE = ", ";
        private static final String DATA = "data";
        private static final String CLASS = "class";
        private static final String RARROW = "-&gt;";
        private static final String IMPLIES = "=&gt;";
        private static final String OPEN_PAREN = "(";
        private static final String CLOSE_PAREN = ")";
        private static final String UNIT_TYPE_CONS = "()";
        private static final String CLOSE_BRACKET = "]";
        private static final String OPEN_BRACKET = "[";
        private static final String CLOSE_BRACE = "}";
        private static final String OPEN_BRACE = "{";
        private static final String SEPARATOR_BETWEEN_BASE_RECORD_AND_EXTENSION_FIELDS = " | ";
        private static final String COLON_COLON = "::";
        private static final char BACKSLASH = '\\';
        private static final char DOT = '.';
        
        /** The standard type variable name to use for display purposes. */
        private static final String STANDARD_TYPE_VAR = "a";
    }
    
    /** We use &lt;tt&gt; for formatting code fragments. */
    private static final HTML.Tag CODE_FORMATTING_TAG = HTML.Tag.TT;

    /**
     * A set of the HTML style class constants employed by the documentation generator.
     *
     * @author Joseph Wong
     */
    private static final class StyleClassConstants {
        /** Private constructor. */
        private StyleClassConstants() {}
        
        ////
        /// General style classes
        //
        private static final StyleClass MAJOR_SECTION = new StyleClass("major");
        private static final StyleClass MINOR_SECTION = new StyleClass("minor");
        private static final StyleClass MINOR_SECTION_ALTERNATE = new StyleClass(MINOR_SECTION, "alt");
        
        private static final StyleClass NON_DISPLAYED_HEADER = new StyleClass("nonDisplayedHeader");
    
        private static final StyleClass ABSTRACT_DEPRECATED_BLOCK = new StyleClass("deprecated");
        private static final StyleClass NAME_AND_TYPE = new StyleClass("nameAndType");
        private static final StyleClass CODE_BLOCK = new StyleClass("codeBlock");
        
        private static final StyleClass PAGE_BOTTOM = new StyleClass("pageBottom");
        
        private static final StyleClass MAIN_CONTENT = new StyleClass("mainContent");
        private static final StyleClass WITH_MAIN_CONTENT = new StyleClass("withMainContent");
        
        private static final StyleClass SIDE_BAR_TITLE = new StyleClass("sideBarTitle");
        private static final StyleClass MODULE_LIST_PAGE_TITLE = new StyleClass(SIDE_BAR_TITLE, "moduleList");
        private static final StyleClass MODULE_LIST_SECTION_TITLE = new StyleClass("moduleListSectionTitle");
        private static final StyleClass SIDE_BAR_KHAKI_TITLE = new StyleClass(SIDE_BAR_TITLE, "khaki");
        private static final StyleClass SIDE_BAR_KHAKI_SUPERTITLE = new StyleClass(SIDE_BAR_TITLE, "superkhaki");
        
        ////
        /// Module list
        //
        private static final StyleClass TREE_DIV = new StyleClass("treeDiv");
        private static final StyleClass TREE_NODE_TOGGLE = new StyleClass("treeToggle");
        private static final StyleClass TREE_NODE_TOGGLE_PLACEHOLDER = new StyleClass("treeNoToggle");
        
        ////
        /// Display mode toolbar
        //
        private static final StyleClass TOOLBAR = new StyleClass("toolbar");
        private static final StyleClass SUB_TOOLBAR = new StyleClass(TOOLBAR, "sub");
        private static final StyleClass BUTTON_SELECTED = new StyleClass("btnSelected");
        private static final StyleClass BUTTON_NOT_SELECTED = new StyleClass("btnNotSelected");
        
        ////
        /// Overview page
        //
        private static final StyleClass TREE_DIV_OVERVIEW_PAGE = new StyleClass(TREE_DIV, "ovw");
        private static final StyleClass DL_OVERVIEW_PAGE = new StyleClass("ovwDl");
        
        ////
        /// Search page
        //
        private static final StyleClass SEARCH_BOX_TITLE = new StyleClass(SIDE_BAR_TITLE, "searchBoxTitle");
        private static final StyleClass SEARCH_BOX = new StyleClass("searchBox");
        private static final StyleClass SEARCH_FIELD = new StyleClass("searchField");
        
        ////
        /// Scope style classes
        //
        private static final StyleClass PUBLIC_SCOPE = new StyleClass("pub");
        private static final StyleClass NON_PUBLIC_SCOPE = new StyleClass("nonPub");
        private static final StyleClass PROTECTED_SCOPE = new StyleClass(NON_PUBLIC_SCOPE, "prot");
        private static final StyleClass PRIVATE_SCOPE = new StyleClass(NON_PUBLIC_SCOPE, "priv");

        ////
        /// Module overview style classes
        //
        private static final StyleClass RELATED_MODULES_LIST = new StyleClass("relatedModulesList");
        
        private static final StyleClass OVERVIEW_TABLE = new StyleClass("ovwTbl");
        private static final StyleClass OVERVIEW_TABLE_SCOPE_COLUMN = new StyleClass("ovwTblScope");
        private static final StyleClass OVERVIEW_NESTED_TABLE = new StyleClass("ovwNestedTbl");
        
        private static final StyleClass OVERVIEW_TABLE_DESCRIPTION = new StyleClass("ovwTblDesc");
        private static final StyleClass OVERVIEW_TABLE_OUTER_DESCRIPTION = new StyleClass(OVERVIEW_TABLE_DESCRIPTION, "outer");
        private static final StyleClass OVERVIEW_TABLE_NESTED_DESCRIPTION = new StyleClass(OVERVIEW_TABLE_DESCRIPTION, "nested");
        
        private static final StyleClass OVERVIEW_REFERENCE = new StyleClass("ovwRef");
        private static final StyleClass SHORT_DESCRIPTION_BLOCK = new StyleClass("ovwDesc");
        private static final StyleClass OVERVIEW_DEPRECATED_BLOCK = new StyleClass(ABSTRACT_DEPRECATED_BLOCK, "ovw");
    
        ////
        /// Details section: high-level style classes
        //
        private static final StyleClass DETAILS_LIST = new StyleClass("detailsList");
        private static final StyleClass DATA_CONSTRUCTOR_LIST = new StyleClass(DETAILS_LIST, "dc");
        private static final StyleClass CLASS_METHOD_LIST = new StyleClass(DETAILS_LIST, "cm");
        private static final StyleClass KNOWN_INSTANCE_LIST = new StyleClass(DETAILS_LIST, "ki");
        private static final StyleClass INSTANCE_METHOD_LIST = new StyleClass(DETAILS_LIST, "im");
        
        private static final StyleClass DETAILS_LIST_HEADER = new StyleClass("detailsListHdr");
        private static final StyleClass DATA_CONSTRUCTOR_LIST_HEADER = new StyleClass(DETAILS_LIST_HEADER, "dc");
        private static final StyleClass CLASS_METHOD_LIST_HEADER = new StyleClass(DETAILS_LIST_HEADER, "cm");
        private static final StyleClass KNOWN_INSTANCE_LIST_HEADER = new StyleClass(DETAILS_LIST_HEADER, "ki");
        private static final StyleClass INSTANCE_METHOD_LIST_HEADER = new StyleClass(DETAILS_LIST_HEADER, "im");
        
        ////
        /// Details section: low-level style classes
        //
        
        /// CALDoc/metadata indicator
        //
        private static final StyleClass ABSTRACT_INDICATOR = new StyleClass("ind");
        private static final StyleClass METADATA_INDICATOR = new StyleClass(ABSTRACT_INDICATOR, "metadata");
        private static final StyleClass CALDOC_INDICATOR = new StyleClass(ABSTRACT_INDICATOR, "caldoc");
        
        /// Class method indicator
        //
        private static final StyleClass CLASS_METHOD_INDICATOR = new StyleClass("cmInd");
        
        /// Description/attribute blocks
        //
        private static final StyleClass ATTRIBUTE_HEADER = new StyleClass("attrHdr");
        
        private static final StyleClass DESCRIPTION_BLOCK = new StyleClass("desc");
        private static final StyleClass AUTHOR_BLOCK = new StyleClass("author");
        private static final StyleClass DEPRECATED_BLOCK = new StyleClass(ABSTRACT_DEPRECATED_BLOCK, "details");
        private static final StyleClass VERSION_BLOCK = new StyleClass("version");
        private static final StyleClass ARG_BLOCK = new StyleClass("arg");
        private static final StyleClass RETURN_BLOCK = new StyleClass("return");
        private static final StyleClass SEE_BLOCK = new StyleClass("see");
        
        private static final StyleClass RETURN_VALUE_INDICIATOR = new StyleClass("retval");
        
        /// Definition - header portion
        //
        private static final StyleClass ABSTRACT_DEFINITION_SECTION_START = new StyleClass("defSecStart");
        private static final StyleClass FIRST_DEFINITION_SECTION_START = new StyleClass(ABSTRACT_DEFINITION_SECTION_START, "first");
        private static final StyleClass DEFINITION_SECTION_START = new StyleClass(ABSTRACT_DEFINITION_SECTION_START, "follow");
        
        private static final StyleClass DEFINITION_HEADER = new StyleClass("defHdr");
        
        private static final StyleClass DEFINITION_HEADER_FIRST_LINE = new StyleClass("defHdrFirstLine");
        
        private static final StyleClass DECLARATION = new StyleClass("decl");
        private static final StyleClass SCOPE = new StyleClass("scope");
        private static final StyleClass DECLARED_NAME = new StyleClass("declName");
        
        /// Argument name formatting
        //
        private static final StyleClass ARG_NAME = new StyleClass("argName");
        private static final StyleClass ARG_NAME_FROM_CODE = new StyleClass(ARG_NAME, "fromCode");
        private static final StyleClass ARG_NAME_NOT_FROM_CODE = new StyleClass(ARG_NAME, "notFromCode");
        private static final StyleClass ARG_NAME_FROM_METADATA = new StyleClass(ARG_NAME_NOT_FROM_CODE, "fromMetadata");
        private static final StyleClass ARG_NAME_FROM_CALDOC = new StyleClass(ARG_NAME_NOT_FROM_CODE, "fromCALDoc");
        private static final StyleClass ARG_NAME_ARTIFICIAL = new StyleClass(ARG_NAME_NOT_FROM_CODE, "artificial");
        
        /// Type signature formatting
        //
        private static final StyleClass TYPE_SIGNATURE = new StyleClass("typeSig");
        private static final StyleClass TYPE_CONSTRAINT = new StyleClass("typeConstraint");
        private static final StyleClass CAL_SYMBOL = new StyleClass("calSymbol");
        
        ////
        /// Navigation frames and bars
        //
        private static final StyleClass DISABLED_LINK = new StyleClass("disabledLink");
        
        ////
        /// Navigation frames
        //
        private static final StyleClass IMPORTANT_LINK = new StyleClass("importantLink");
        
        private static final StyleClass INDEX_NAV = new StyleClass("indexNav");
        private static final StyleClass INDEX_NAV_LINK = new StyleClass("indexNavLink");
        private static final StyleClass INDEX_NAV_CURRENT_LINK = new StyleClass("indexNavCurrentLink");
        
        ////
        /// Options Box
        //
        private static final StyleClass OPTIONS_HEADER = new StyleClass("optionsHdr");
        private static final StyleClass OPTIONS_BODY = new StyleClass("optionsBody");
        private static final StyleClass OPTIONS_CHOICE = new StyleClass("optionsChoice");
        
        private static final StyleClass FAKE_LINK = new StyleClass("fakeLink");
        
        ////
        /// Navigation bars
        //
        private static final StyleClass NAV_BAR = new StyleClass("navBar");
        private static final StyleClass NAV_BAR_GLOBAL_ROW = new StyleClass("navBarGlobalRow");
        private static final StyleClass NAV_BAR_LOCAL_ROW = new StyleClass("navBarLocalRow");
        private static final StyleClass NAV_BAR_HEADER_FOOTER = new StyleClass("navBarHeaderFooter");
        
        private static final StyleClass MODULE_LIST_NAV_BAR = new StyleClass("moduleListNavBar");
    
        ////
        /// CSS selectors for pseudo-classes
        //
        private static final CSSBuilder.Selector A_LINK_PSEUDOCLASS_SELECTOR = CSSBuilder.Selector.makePseudoClass(CSSBuilder.Selector.makeType(HTML.Tag.A), "link");
        private static final CSSBuilder.Selector A_HOVER_PSEUDOCLASS_SELECTOR = CSSBuilder.Selector.makePseudoClass(CSSBuilder.Selector.makeType(HTML.Tag.A), "hover");
        private static final CSSBuilder.Selector A_VISITED_PSEUDOCLASS_SELECTOR = CSSBuilder.Selector.makePseudoClass(CSSBuilder.Selector.makeType(HTML.Tag.A), "visited");
        
        ////
        /// Tooltip fragments
        //
        private static final StyleClass TOOLTIP_HEADER = new StyleClass("tooltipHeader");
    }

    /**
     * A set of the HTML element identifiers employed by the documentation generator.
     *
     * @author Joseph Wong
     */
    private static final class ElementID {
        /** Private constructor. */
        private ElementID() {}
        
        private static final String OUTER_FRAMESET = "outerFrameset";
        private static final String SUMMARY_SECTION = "summarySection";
        private static final String TYPES_SECTION = "typesSection";
        private static final String FUNCTIONS_SECTION = "functionsSection";
        private static final String TYPE_CLASSES_SECTION = "typeClassesSection";
        private static final String INSTANCES_SECTION = "instancesSection";
        
        private static final String HIDE_NONPUBLIC_STYLE = "hideNonPub";
        
        private static final String MODULE_LIST_FLAT = "mlFlat";
        private static final String MODULE_LIST_GROUPED = "mlGrouped";
        private static final String MODULE_LIST_HIERARCHICAL = "mlHierarchical";
        
        private static final String SUB_TOOLBAR_MODULE_LIST_FLAT = "tbFlat";
        private static final String SUB_TOOLBAR_MODULE_LIST_GROUPED = "tbGrouped";
        private static final String SUB_TOOLBAR_MODULE_LIST_HIERARCHICAL = "tbHierarchical";
        
        private static final String DISPLAY_MODE_BUTTON_MODULE_LIST_FLAT = "dmFlat";
        private static final String DISPLAY_MODE_BUTTON_MODULE_LIST_GROUPED = "dmGrouped";
        private static final String DISPLAY_MODE_BUTTON_MODULE_LIST_HIERARCHICAL = "dmHierarchical";
        
        /// Search page
        //
        private static final String KEY = "key";
        private static final String RESULTS = "results";
        
        /// Options box
        //
        private static final String HIGHLIGHT_PUBLIC_STYLE = "highlightPub";
        private static final String HIGHLIGHT_NONPUBLIC_STYLE = "highlightNonPub";
        private static final String OPTIONS_BOX = "optionsBox";
    }

    /**
     * A set of the style values employed by the documentation generator.
     *
     * @author Joseph Wong
     */
    private static final class StyleValueConstants {
        /** Private constructor. */
        private StyleValueConstants() {}
    
        ////
        /// Font constants
        //
        private static final String SANS_SERIF_FONT_LIST = "Verdana, Arial, Helvetica, sans-serif";
        private static final String SERIF_FONT_LIST = "palatino, 'Palatino Linotype', 'Book Antiqua', serif";
        private static final String MONOSPACE_FONT_LIST = "'Lucida Console', 'Lucida Sans Typewriter', monospace";
        
        ////
        /// Colour constants
        //
        private static final String OPENQUARK_BLUE = "#325087";
        private static final String OPENQUARK_CREAM = "#ffffe2";
        private static final String OPENQUARK_RED = "#f51e32";
        private static final String OPENQUARK_SECONDARY_LIGHT_BLUE = "#909fc9";
        private static final String OPENQUARK_SECONDARY_YELLOW = "#f9d37c";
        private static final String OPENQUARK_SECONDARY_KHAKI = "#ceca8d";
        private static final String OPENQUARK_WEBSITE_KHAKI = "#f2f1e1";
        private static final String OPENQUARK_WEBSITE_LIGHT_BLUE_HEADER_BACKGROUND = "#e8eff7";
        private static final String OPENQUARK_WEBSITE_LIGHT_BLUE_CONTENT_BACKGROUND = "#f5f9fc";
        private static final String WHITE = "white";
        private static final String DISABLED_LINK_COLOR = "gray";
        private static final String VISITED_LINK_COLOR ="#306";
        private static final String TYPE_CONSTRAINT_BACKGROUND_COLOR = "#eee";
        private static final String OVERVIEW_NESTED_TABLE_BACKGROUND_COLOR = "#fcfcfc";
        private static final String OVERVIEW_NESTED_TABLE_BORDER_COLOR = "#ccc";
        private static final String DEPRECATED_BLOCK_CODE_BLOCK_COLOR = "#fdd";
        private static final String DEPRECATED_BLOCK_BORDER_COLOR = "#fcc";
        private static final String DEPRECATED_BLOCK_BACKGROUND_COLOR = "#fee";
        private static final String METADATA_INDICATOR_COLOR = "green";
        private static final String CALDOC_INDICATOR_COLOR = OPENQUARK_BLUE;
        private static final String CODE_BLOCK_COLOR = "#574022";
        private static final String CODE_BLOCK_BACKGROUND_COLOR = "#f9f8f0";
        private static final String OPENQUARK_WEBSITE_KHAKI_SIDE_BAR_TITLE_COLOR = "#777";
        private static final String OPENQUARK_WEBSITE_KHAKI_SIDE_BAR_TITLE_BACKGROUND_COLOR = "#dfdec6";
        private static final String OPENQUARK_WEBSITE_KHAKI_SIDE_BAR_SUPERTITLE_COLOR = OPENQUARK_WEBSITE_KHAKI_SIDE_BAR_TITLE_COLOR;
        private static final String OPENQUARK_WEBSITE_KHAKI_LIGHTER = "#f9f9f0";
        private static final String OPTIONS_BODY_BACKGROUND = OPENQUARK_WEBSITE_LIGHT_BLUE_CONTENT_BACKGROUND;
        private static final String TREE_NODE_TOGGLE_HOVER_BACKGROUND_COLOR = OPENQUARK_SECONDARY_LIGHT_BLUE;
        private static final String CAL_EDITOR_HOVER_BACKGROUND_COLOR = OPENQUARK_CREAM;
        private static final String TOOLBAR_BACKGROUND_COLOR = "#d3f2eb";
        private static final String TOOLBAR_BUTTON_BACKGROUND_COLOR = "#defff8";
        private static final String TOOLBAR_BUTTON_BORDER_COLOR = "#9dccc2";
        private static final String TOOLBAR_BUTTON_HOVER_BACKGROUND_COLOR = OPENQUARK_WEBSITE_KHAKI;
        private static final String TOOLBAR_BUTTON_SELECTED_BACKGROUND_COLOR = OPENQUARK_SECONDARY_YELLOW;
        private static final String SUB_TOOLBAR_BACKGROUND_COLOR = "#f2fffc";
        
        ////
        /// Text style constants
        //
        private static final String BOLD = "bold";
        private static final String ITALIC = "italic";
        
        ////
        /// Text decoration constants
        //
        private static final String UNDERLINE = "underline";
        
        ////
        /// Text size constants
        //
        private static final String LARGER = "larger";
        private static final String X_LARGE = "x-large";
        private static final String LARGE = "large";
        private static final String MEDIUM = "medium";
        private static final String SMALLER = "smaller";
        private static final String INDICATOR_FONT_SIZE = "9px";
    
        ////
        /// Text alignment constants
        //
        private static final String LEFT_ALIGN = "left";
        private static final String RIGHT_ALIGN = "right";
        
        ////
        /// Border style constants
        //
        private static final String THIN = "thin";
        private static final String TWO_PX_THIN = "2px";
        private static final String SOLID = "solid";
        private static final String DOTTED = "dotted";
        private static final String OUTSET_BORDER_STYLE = "outset";
        private static final String INDEX_NAV_LINK_SEPARATOR_BORDER_STYLE = "1px dotted " + OPENQUARK_WEBSITE_KHAKI_SIDE_BAR_TITLE_BACKGROUND_COLOR;
        private static final String INDEX_NAV_CURRENT_LINK_LEFT_BORDER_STYLE = "3px solid " + OPENQUARK_BLUE;
        private static final String INDEX_NAV_CURRENT_LINK_BORDER_STYLE = "1px solid #999";
        private static final String MODULE_LIST_SECTION_TITLE_LEFT_BORDER_STYLE = "3px solid " + OPENQUARK_RED;
        private static final String TREE_DIV_OVERVIEW_PAGE_LEFT_BORDER_STYLE = "1px dotted " + OPENQUARK_SECONDARY_LIGHT_BLUE;
        private static final String TREE_DIV_OVERVIEW_PAGE_BOTTOM_BORDER_STYLE = "1px solid " + OPENQUARK_SECONDARY_LIGHT_BLUE;
        private static final String DL_OVERVIEW_PAGE_BORDER_STYLE = "1px solid " + OPENQUARK_SECONDARY_KHAKI;
        private static final String TOOLBAR_BUTTON_BORDER_STYLE = "1px solid " + TOOLBAR_BUTTON_BORDER_COLOR;
        private static final String TOOLBAR_BUTTON_BLACK_BORDER_STYLE = "1px solid black";
        private static final String TREE_NODE_TOGGLE_BORDER_STYLE = "1px solid #777";
    
        ////
        /// Width constants
        //
        private static final String ZERO_WIDTH = "0px";
        private static final String ONE_EM_WIDTH = "1em";
        private static final String ONE_POINT_FIVE_EM_WIDTH = "1.5em";
        private static final String TWO_EM_WIDTH = "2em";
        private static final String MAJOR_SECTION_TOP_AND_SIDE_PADDING = "5px";
        private static final String DEPRECATED_BLOCK_CLEARANCE = "0.5em";
        private static final String DETAILS_LIST_MARGIN_TOP = "2.5em";
        private static final String DETAILS_LIST_PADDING = "3px";
        private static final String AUTO_WIDTH = "auto";
        private static final String TYPE_SIGNATURE_HANGING_INDENT = "6em";
        private static final String CODE_BLOCK_SIDE_PADDING = "0px";
        private static final String SEARCH_BOX_TOP_PADDING = "5px";
        private static final String SEARCH_BOX_RIGHT_PADDING = "10px";
        private static final String ONE_HUNDRED_PERCENT = "100%";
        private static final String MAIN_CONTENT_MARGIN = "8px";
        private static final String SIDE_BAR_TITLE_PADDING = "6px";
        private static final String INDEX_NAV_LINK_PADDING = "6px";
        private static final String INDEX_NAV_LINK_PADDING_HALF = "3px";
        private static final String INDEX_NAV_LINK_PADDING_HALF_MINUS_ONE = "2px";
        private static final String NAV_BAR_PADDING = "4px";
        private static final String SCOPE_DISPLAY_SETTINGS_PADDING = "2px";
        private static final String TREE_INDENT = "22px";
        private static final String TREE_DIV_OVERVIEW_PAGE_BOTTOM_MARGIN = "6px";
        private static final String DL_OVERVIEW_PAGE_BOTTOM_PADDING = "3px";
        private static final String TOOLBAR_PADDING = "6px";
        private static final String TOOLBAR_BUTTON_PADDING = "3px";
        private static final String SUB_TOOLBAR_BUTTON_TOP_BOTTOM_PADDING = "1px";
        private static final String RELATED_MODULES_GROUPED_LIST_HANGING_INDENT = "3em";
        
        ////
        /// Display style constants
        //
        private static final String BLOCK = "block";
        private static final String NONE = "none";
        private static final String INLINE = "inline";
    
        ////
        /// Text wrap style constants
        //
        private static final String NO_TEXT_WRAP = "none";
        private static final String SUPPRESS_TEXT_WRAP = "suppress";
        private static final String NOWRAP_WHITE_SPACE = "nowrap";
        
        ////
        /// Cursor style constants
        //
        private static final String POINTER = "pointer";
    }

    /**
     * CSS 2 attributes not in CSS.Attribute class.
     *
     * @author Joseph Wong
     */
    private static final class CSS2Attribute {
        /** Private constructor. */
        private CSS2Attribute() {}
    
        // good advice for preventing word-wrap can be found here: http://www.cs.tut.fi/~jkorpela/html/nobr.html
        private static final String TEXT_WRAP = "text-wrap";
        
        private static final String CURSOR = "cursor";
    }

    /**
     * HTML 4 tags and attributes not in HTML.Tag and HTML.Attribute classes.
     *
     * @author Joseph Wong
     */
    private static final class HTML4 {
        /** Private constructor. */
        private HTML4() {}
    
        private static final HTML.Tag TBODY_TAG = new HTML.Tag("tbody", true, true) {};
        private static final String RULES_ATTRIBUTE = "rules";
        private static final String ONLOAD_ATTRIBUTE = "onload";
        private static final String MEDIA_ATTRIBUTE = "media";
        private static final String ONCLICK_ATTRIBUTE = "onclick";
    }

    /**
     * DocType declarations for HTML.
     *
     * @author Joseph Wong
     */
    private static final class DocTypeDecl {
        /** Private constructor. */
        private DocTypeDecl() {}
    
        /** DocType for HTML 4.01 Strict. */
        private static final String HTML_4_01_STRICT =
            "<!DOCTYPE HTML PUBLIC '-//W3C//DTD HTML 4.01//EN' 'http://www.w3.org/TR/html4/strict.dtd'>\n";
        
        /** DocType for HTML 4.01 Frameset. Used for the master frameset. */
        private static final String HTML_4_01_FRAMESET =
            "<!DOCTYPE HTML PUBLIC '-//W3C//DTD HTML 4.01 Frameset//EN' 'http://www.w3.org/TR/html4/frameset.dtd'>\n";
        
        /**
         * DocType for HTML 4.01 Transitional. Used for the navigation pages in
         * the smaller frames, because the 'target' attribute is <i>not</i> in
         * HTML 4.01 Strict.
         * 
         * Now also used for all documentation pages appearing in the main frame,
         * because of the appearance of the "Search" link targeting the right-hand-side frame. 
         */
        private static final String HTML_4_01_TRANSITIONAL =
            "<!DOCTYPE HTML PUBLIC '-//W3C//DTD HTML 4.01 Transitional//EN' 'http://www.w3.org/TR/html4/loose.dtd'>\n";
    }
    
    /**
     * A comparator for IndexEntry instances that orders them by their display names in a case-insensitive manner.
     * 
     * @author Joseph Wong
     */
    private static final class IndexEntryComparator implements Comparator<IndexEntry> {
        /** {@inheritDoc} */
        public int compare(IndexEntry a, IndexEntry b) {
            int ignoreCaseComparison = a.getDisplayName().compareToIgnoreCase(b.getDisplayName());
            if (ignoreCaseComparison != 0) {
                return ignoreCaseComparison;
            } else {
                return a.getDisplayName().compareTo(b.getDisplayName());
            }
        }
    }
    
    /////====================================================================================================
    ////
    /// Source model traverser for generating a hyperlinked type signature
    //
    
    /**
     * This class implements a traverser which traverses through a type signature and generates its source representation,
     * with type classes and type constructors appropriate hyperlinked if requested.
     *
     * @author Joseph Wong
     */
    private class TypeSignatureHTMLGenerator extends SourceModelTraverser<Void, Void> {
        
        /** Whether hyperlinks should be generated. */
        private final boolean shouldGenerateHyperlinks;
        
        /**
         * Constructs an instance of this traverser/generator.
         * @param shouldGenerateHyperlinks whether hyperlinks should be generated.
         */
        private TypeSignatureHTMLGenerator(boolean shouldGenerateHyperlinks) {
            this.shouldGenerateHyperlinks = shouldGenerateHyperlinks;
        }
        
        /**
         * Generates the source representation of a type application, e.g. Either a b.
         */
        @Override
        public Void visit_TypeExprDefn_Application(SourceModel.TypeExprDefn.Application application, Void arg) {
            verifyArg(application, "application");
            
            /// Loop through each type expression in the application, and generate each one, properly parenthesized as required.
            //
            final int nTypeExpressions = application.getNTypeExpressions();
            for (int i = 0; i < nTypeExpressions; i++) {
                if (i > 0) {
                    currentPage.addText(" ");
                }
                
                SourceModel.TypeExprDefn nthTypeExpr = application.getNthTypeExpression(i);
                
                if (nthTypeExpr instanceof SourceModel.TypeExprDefn.Application || nthTypeExpr instanceof SourceModel.TypeExprDefn.Function) {
                    currentPage.addText(CALFragments.OPEN_PAREN);
                    nthTypeExpr.accept(this, arg);
                    currentPage.addText(CALFragments.CLOSE_PAREN);
                } else {
                    nthTypeExpr.accept(this, arg);
                }
            }
    
            return null;
        }
    
        /**
         * Generates the source representation of a function type, e.g. Int -> String.
         */
        @Override
        public Void visit_TypeExprDefn_Function(SourceModel.TypeExprDefn.Function function, Void arg) {
            verifyArg(function, "function");
            
            //we never need to parenthesize the type on the right hand side of a "->". 
            //This is because "->" is the lowest precedence operator in the type grammar.
            //We only parenthesize the lhs if it is a "->".
            
            SourceModel.TypeExprDefn domain = function.getDomain();
            SourceModel.TypeExprDefn codomain = function.getCodomain();
            
            if (domain instanceof SourceModel.TypeExprDefn.Function) {
                currentPage.addText(CALFragments.OPEN_PAREN);
                domain.accept(this, arg);
                currentPage.addText(CALFragments.CLOSE_PAREN);
            } else {
                domain.accept(this, arg);
            }
            
            currentPage.addText(" ").addTaggedText(HTML.Tag.SPAN, classAttribute(StyleClassConstants.CAL_SYMBOL), CALFragments.RARROW + "&nbsp;");
            codomain.accept(this, arg);
    
            return null;
        }
    
        /**
         * Generates the source representation of a list type, e.g. [Int].
         */
        @Override
        public Void visit_TypeExprDefn_List(SourceModel.TypeExprDefn.List list, Void arg) {
            verifyArg(list, "list");
    
            currentPage.addText(CALFragments.OPEN_BRACKET);
            list.getElement().accept(this, arg);
            currentPage.addText(CALFragments.CLOSE_BRACKET);
            
            return null;
        }
    
        /**
         * Generates the source representation of a record type, e.g. {r | a :: Int, b :: String}.
         */
        @Override
        public Void visit_TypeExprDefn_Record(SourceModel.TypeExprDefn.Record record, Void arg) {
            verifyArg(record, "record");
            
            final int nExtensionFields = record.getNExtensionFields();
            
            currentPage.addText(CALFragments.OPEN_BRACE);
            
            /// Generate the base record
            //
            if (record.getBaseRecordVar() != null) {
                record.getBaseRecordVar().accept(this, arg);
                
                if (nExtensionFields > 0) {
                    currentPage.addText(CALFragments.SEPARATOR_BETWEEN_BASE_RECORD_AND_EXTENSION_FIELDS);
                }
            }
    
            /// Generate the extension fields
            //
            for (int i = 0; i < nExtensionFields; i++) {
                if (i > 0) {
                    currentPage.addText(CALFragments.COMMA_AND_SPACE);
                }
                
                record.getNthExtensionField(i).accept(this, arg);
            }
            
            currentPage.addText(CALFragments.CLOSE_BRACE);
    
            return null;
        }
    
        /**
         * Generates the source representation of a field-type pair in a record type, e.g. a :: Int.
         */
        @Override
        public Void visit_TypeExprDefn_Record_FieldTypePair(SourceModel.TypeExprDefn.Record.FieldTypePair pair, Void arg) {
            verifyArg(pair, "pair");
    
            currentPage.addText(pair.getFieldName().getName().getCalSourceForm() + "&nbsp;" + CALFragments.COLON_COLON + "&nbsp;");
            pair.getFieldType().accept(this, arg);
            
            return null;
        }
    
        /**
         * Generates the source representation of a tuple type, e.g. (Int, String, a).
         */
        @Override
        public Void visit_TypeExprDefn_Tuple(SourceModel.TypeExprDefn.Tuple tuple, Void arg) {
            verifyArg(tuple, "tuple");
    
            currentPage.addText(CALFragments.OPEN_PAREN);
            
            /// Generate the source representation for each component
            //
            final int nComponents = tuple.getNComponents();
            for (int i = 0; i < nComponents; i++) {
                if (i > 0) {
                    currentPage.addText(CALFragments.COMMA_AND_SPACE);
                }
                tuple.getNthComponent(i).accept(this, arg);
            }
            
            currentPage.addText(CALFragments.CLOSE_PAREN);
    
            return null;
        }
    
        /**
         * Generates the source representation of a type constructor type expression, hyperlinked if requested.
         */
        @Override
        public Void visit_TypeExprDefn_TypeCons(SourceModel.TypeExprDefn.TypeCons cons, Void arg) {
            verifyArg(cons, "cons");
    
            // NOTE: We require that the type cons name be fully qualified with a non-null module name.
            SourceModel.Name.TypeCons name = cons.getTypeConsName();
            if (shouldGenerateHyperlinks) {
                generateTypeConsReference(SourceModel.Name.Module.toModuleName(name.getModuleName()), name.getUnqualifiedName());
            } else {
                currentPage.addText(getAppropriatelyQualifiedName(SourceModel.Name.Module.toModuleName(name.getModuleName()), name.getUnqualifiedName()));
            }
            
            return null;
        }
        
        /**
         * Generates a reference to a type constructor, appropriately hyperlinked.<p>
         * 
         * This method is meant to be overridden in subclasses that need to generate the hyperlink differently.
         * 
         * @param moduleName the module name of the type constructor.
         * @param unqualifiedName the unqualified name of the type constructor.
         */
        void generateTypeConsReference(ModuleName moduleName, String unqualifiedName) {
            HTMLDocumentationGenerator.this.generateTypeConsReference(moduleName, unqualifiedName);
        }
    
        /**
         * Generates the source representation of a type variable type expression.
         */
        @Override
        public Void visit_TypeExprDefn_TypeVar(SourceModel.TypeExprDefn.TypeVar var, Void arg) {
            verifyArg(var, "var");
            
            currentPage.addText(var.getTypeVarName().getName());
    
            return null;
        }
    
        /**
         * Generates the source representation of the Unit type, i.e. ().
         */
        @Override
        public Void visit_TypeExprDefn_Unit(SourceModel.TypeExprDefn.Unit unit, Void arg) {
            verifyArg(unit, "unit");
            
            currentPage.addText(CALFragments.UNIT_TYPE_CONS);
    
            return null;
        }
    
        /**
         * Generates the source representation of a lacks constraint, e.g. r\a.
         */
        @Override
        public Void visit_Constraint_Lacks(SourceModel.Constraint.Lacks lacks, Void arg) {
            verifyArg(lacks, "lacks");
            
            currentPage.addText(lacks.getTypeVarName().getName() + CALFragments.BACKSLASH + lacks.getLacksField().getName().getCalSourceForm());
    
            return null;
        }
    
        /**
         * Generates the source representation of a type class constraint, hyperlinked if requested.
         */
        @Override
        public Void visit_Constraint_TypeClass(SourceModel.Constraint.TypeClass typeClass, Void arg) {
            verifyArg(typeClass, "typeClass");
            
            // NOTE: We require that the type class name be fully qualified with a non-null module name.
            SourceModel.Name.TypeClass name = typeClass.getTypeClassName();
            if (shouldGenerateHyperlinks) {
                generateTypeClassReference(SourceModel.Name.Module.toModuleName(name.getModuleName()), name.getUnqualifiedName());
            } else {
                currentPage.addText(getAppropriatelyQualifiedName(SourceModel.Name.Module.toModuleName(name.getModuleName()), name.getUnqualifiedName()));
            }
            currentPage.addText(" " + typeClass.getTypeVarName().getName());
            
            return null;
        }
    
        /**
         * Generates a reference to a type class, appropriately hyperlinked.<p>
         * 
         * This method is meant to be overriden in subclasses that need to generate the hyperlink differently.
         * 
         * @param moduleName the module name of the type class.
         * @param unqualifiedName the unqualified name of the type class.
         */
        void generateTypeClassReference(ModuleName moduleName, String unqualifiedName) {
            HTMLDocumentationGenerator.this.generateTypeClassReference(moduleName, unqualifiedName);
        }
    
        /**
         * Generates the source representation of a type signature.
         */
        @Override
        public Void visit_TypeSignature(SourceModel.TypeSignature signature, Void arg) {
            verifyArg(signature, "signature");
    
            generateConstraintsFromSignature(signature, arg);
            signature.getTypeExprDefn().accept(this, arg);
            
            return null;
        }
        
        /**
         * Generates the source representation of the constraints portion of a type signature.
         * @param signature the type signature.
         * @param arg the argument to pass through the traversal.
         */
        private void generateConstraintsFromSignature(SourceModel.TypeSignature signature, Void arg) {
            final int nConstraints = signature.getNConstraints();
            if (nConstraints > 0) {
                currentPage.openTag(HTML.Tag.SPAN, classAttribute(StyleClassConstants.TYPE_CONSTRAINT));
                
                if (nConstraints == 1) {
                    signature.getNthConstraint(0).accept(this, arg);
                    
                } else {
                    currentPage.addText(CALFragments.OPEN_PAREN);
                    for (int i = 0; i < nConstraints; i++) {
                        if (i > 0) {
                            currentPage.addText(CALFragments.COMMA_AND_SPACE);
                        }
                        
                        signature.getNthConstraint(i).accept(this, arg);
                    }
                    currentPage.addText(CALFragments.CLOSE_PAREN);                                         
                }
                
                currentPage.addText(" ").addTaggedText(HTML.Tag.SPAN, classAttribute(StyleClassConstants.CAL_SYMBOL), CALFragments.IMPLIES).closeTag(HTML.Tag.SPAN).addText(" ");
            }
        }
    }

    /**
     * A subclass of TypeSignatureHTMLGenerator that generates all its hyperlinks as non-local references.
     *
     * @author Joseph Wong
     */
    private final class TypeSignatureHTMLGeneratorUsingNonLocalReferences extends TypeSignatureHTMLGenerator {
        
        /** The relative path to the base directory for documentation generation. */
        private final String relativePathToBaseDirectory;
        
        /**
         * Constructs an instance of this traverser/generator.
         * @param relativePathToBaseDirectory the relative path to the base directory for documentation generation.
         */
        TypeSignatureHTMLGeneratorUsingNonLocalReferences(String relativePathToBaseDirectory) {
            super(true);
            this.relativePathToBaseDirectory = relativePathToBaseDirectory;
        }
    
        /**
         * Generates a reference to a type class, hyperlinked as non-local references.
         * 
         * @param moduleName the module name of the type class.
         * @param unqualifiedName the unqualified name of the type class.
         */
        @Override
        void generateTypeClassReference(ModuleName moduleName, String unqualifiedName) {
            String typeClassLabel = labelMaker.getTypeClassLabel(unqualifiedName);
            String appropriatelyQualifiedName = getAppropriatelyQualifiedName(moduleName, unqualifiedName);
            
            if (!isDocForTypeClassGenerated(moduleName, unqualifiedName)) {
                currentPage.addText(appropriatelyQualifiedName);
            } else {
                generateNonLocalReference(currentPage, relativePathToBaseDirectory, moduleName, typeClassLabel, appropriatelyQualifiedName, getFullyQualifiedNameString(moduleName, unqualifiedName));
            }
        }
    
        /**
         * Generates a reference to a type constructor, hyperlinked as non-local references.
         * 
         * @param moduleName the module name of the type constructor.
         * @param unqualifiedName the unqualified name of the type constructor.
         */
        @Override
        void generateTypeConsReference(ModuleName moduleName, String unqualifiedName) {
            String typeConsLabel = labelMaker.getTypeConsLabel(unqualifiedName);
            String appropriatelyQualifiedName = getAppropriatelyQualifiedName(moduleName, unqualifiedName);
            
            if (!isDocForTypeConsGenerated(moduleName, unqualifiedName)) {
                currentPage.addText(appropriatelyQualifiedName);
            } else {
                generateNonLocalReference(currentPage, relativePathToBaseDirectory, moduleName, typeConsLabel, appropriatelyQualifiedName, getFullyQualifiedNameString(moduleName, unqualifiedName));
            }
        }
    }
    
    /**
     * Implements a cross-reference generator to be used by the text-block-to-HTML generation utility.
     * The implementation simply bridges to existing methods for generating references in the outer class.
     *
     * @author Joseph Wong
     */
    private final class ReferenceGenerator extends CALDocToHTMLUtilities.CrossReferenceHTMLGenerator {
        
        /**
         * The relative directory path for getting to the modules subdirectory. 
         */
        private final String relativeDirectory;
        
        /**
         * Constructs a ReferenceGenerator where the file to receive the cross-references is itself located
         * in the modules directory.
         */
        private ReferenceGenerator() {
            this.relativeDirectory = null;
        }
        
        /**
         * Constructs a ReferenceGenerator with the given relative directory path for getting to the modules subdirectory.
         * @param relativeDirectory the relative directory path for getting to the modules subdirectory.
         */
        private ReferenceGenerator(String relativeDirectory) {
            this.relativeDirectory = relativeDirectory;
        }

        /**
         * Generates HTML for a module cross-reference.
         * @param builder the HTMLBuilder to use for generating the cross-reference.
         * @param reference the module cross-reference.
         */
        @Override
        final void generateModuleReference(HTMLBuilder builder, CALDocComment.ModuleReference reference) {
            HTMLDocumentationGenerator.this.generateModuleReference(builder, relativeDirectory, reference);
        }

        /**
         * Generates HTML for a type constructor cross-reference.
         * @param builder the HTMLBuilder to use for generating the cross-reference.
         * @param reference the type constructor cross-reference.
         */
        @Override
        final void generateTypeConsReference(HTMLBuilder builder, CALDocComment.ScopedEntityReference reference) {
            HTMLDocumentationGenerator.this.generateTypeConsReference(builder, relativeDirectory, reference.getName(), reference.getModuleNameInSource());
        }

        /**
         * Generates HTML for a data constructor cross-reference.
         * @param builder the HTMLBuilder to use for generating the cross-reference.
         * @param reference the data constructor cross-reference.
         */
        @Override
        final void generateDataConsReference(HTMLBuilder builder, CALDocComment.ScopedEntityReference reference) {
            HTMLDocumentationGenerator.this.generateDataConsReference(builder, relativeDirectory, reference.getName(), reference.getModuleNameInSource());
        }

        /**
         * Generates HTML for a function or class method cross-reference.
         * @param builder the HTMLBuilder to use for generating the cross-reference.
         * @param reference the function or class method cross-reference.
         */
        @Override
        final void generateFunctionOrClassMethodReference(HTMLBuilder builder, CALDocComment.ScopedEntityReference reference) {
            HTMLDocumentationGenerator.this.generateFunctionOrClassMethodReference(builder, relativeDirectory, reference.getName(), reference.getModuleNameInSource());
        }

        /**
         * Generates HTML for a type class cross-reference.
         * @param builder the HTMLBuilder to use for generating the cross-reference.
         * @param reference the type class cross-reference.
         */
        @Override
        final void generateTypeClassReference(HTMLBuilder builder, CALDocComment.ScopedEntityReference reference) {
            HTMLDocumentationGenerator.this.generateTypeClassReference(builder, relativeDirectory, reference.getName(), reference.getModuleNameInSource());
        }
    }
    
    /**
     * Encapsulates the information for a node representing a module in a tree view
     * (e.g. in the module list and in the overview page).
     *
     * @author Joseph Wong
     */
    private static final class ModuleHierarchyInfo {
        
        /**
         * A sorted map from the pairs (ModuleName, (Boolean)isActualModule) of children to the corresponding ModuleHierarchyInfos.
         */
        private final SortedMap<Pair<ModuleName, Boolean>, ModuleHierarchyInfo> children =
            new TreeMap<Pair<ModuleName, Boolean>, ModuleHierarchyInfo>(
                new Comparator<Pair<ModuleName, Boolean>>() {
                    public int compare(final Pair<ModuleName, Boolean> a, final Pair<ModuleName, Boolean> b) {
                        final int moduleNameResult = a.fst().compareTo(b.fst());
                        
                        if (moduleNameResult != 0) {
                            // first order by module names
                            return moduleNameResult;
                        } else {
                            // if module names are equal
                            // we want true < false so that actual modules come before namespaces with the same name
                            if (a.snd() == Boolean.TRUE) {
                                if (b.snd() == Boolean.FALSE) {
                                    return -1;
                                } else {
                                    return 0;
                                }
                            } else {
                                if (b.snd() == Boolean.TRUE) {
                                    return 1;
                                } else {
                                    return 0;
                                }
                            }
                        }
                    }});
        
        /**
         * Returns the ModuleHierarchyInfo for the child with the given name, creating it if necessary.
         * @param childName the name of the child.
         * @param isActualModule whether the child represents an actual module.
         * @return the ModuleHierarchyInfo for the child.
         */
        ModuleHierarchyInfo makeChild(final ModuleName childName, final boolean isActualModule) {
            final Pair<ModuleName, Boolean> key = new Pair<ModuleName, Boolean>(childName, Boolean.valueOf(isActualModule));
            final ModuleHierarchyInfo childFromMap = children.get(key);
            if (childFromMap != null) {
                return childFromMap;
            } else {
                final ModuleHierarchyInfo newChild = new ModuleHierarchyInfo();
                children.put(key, newChild);
                return newChild;
            }
        }

        /**
         * Returns the children map.
         * @return the children map.
         */
        SortedMap<Pair<ModuleName, Boolean>, ModuleHierarchyInfo> getChildren() {
            // this method is meant for internal use only, so we return the mutable map without wrapping it
            return children;
        }
    }
    
    /////====================================================================================================
    ////
    /// Constructor and private helper methods
    //
    /////====================================================================================================
    

    /**
     * Package-scoped constructor for creating an instance of this class.
     * @param workspaceManager the workspace manager.
     * @param config the configuration.
     */
    HTMLDocumentationGenerator(WorkspaceManager workspaceManager, HTMLDocumentationGeneratorConfiguration config) {
        this(workspaceManager, workspaceManager.getWorkspace(), config, false);
    }
    
    /**
     * Package-scoped constructor for creating an instance of this class.
     * @param programModelManager the program model manager.
     * @param workspace the CALWorkspace for metadata access. Can be null.
     * @param config the configuration.
     * @param disableAllHyperlinks whether to disable all hyperlinks globally.
     */
    HTMLDocumentationGenerator(final ProgramModelManager programModelManager, final CALWorkspace workspace, final HTMLDocumentationGeneratorConfiguration config, final boolean disableAllHyperlinks) {
        super(programModelManager, config.filter, config.shouldGenerateUsageIndices, config.logger);
        this.workspace = workspace;
        this.config = config;
        this.disableAllHyperlinks = disableAllHyperlinks;
    }
    
    /**
     * @return whether the documentation should include metadata.
     */
    private boolean shouldGenerateFromMetadata() {
        return config.shouldGenerateFromMetadata;
    }
    
    /**
     * @return whether CALDoc should always be included in the documentation regardless of whether metadata is included or not.
     */
    private boolean shouldAlwaysGenerateFromCALDoc() {
        return config.shouldAlwaysGenerateFromCALDoc;
    }
    
    /**
     * @return whether author info should be generated.
     */
    private boolean shouldGenerateAuthorInfo() {
        return config.shouldGenerateAuthorInfo;
    }
    
    /**
     * @return whether version info should be generated.
     */
    private boolean shouldGenerateVersionInfo() {
        return config.shouldGenerateVersionInfo;
    }
    
    /**
     * @return whether Prelude names should always be displayed as unqualified.
     */
    private boolean shouldDisplayPreludeNamesAsUnqualified() {
        return config.shouldDisplayPreludeNamesAsUnqualified;
    }
    
    /**
     * @return the Locale for the generated documentation, in particular the metadata if included.
     */
    private Locale getLocale() {
        return config.locale;
    }
    
    /**
     * Generates a text file using the UTF-8 encoding.
     * @param fileName the name of the file to be generated.
     * @param content the content of the file to be generated.
     */
    private void generateTextFile(String fileName, String content) {
        config.fileGenerator.generateTextFile(fileName, content, FileGenerator.UTF_8_CHARSET);
    }
    
    /**
     * Generates a text file in a particular subdirectory of the base directory using the UTF-8 encoding.
     * The subdirectory will be created if it does not exist.
     * 
     * @param subdirectory the subdirectory name.
     * @param fileName the name of the file to be generated.
     * @param content the content of the file to be generated.
     */
    private void generateTextFile(String subdirectory, String fileName, String content) {
        config.fileGenerator.generateTextFile(subdirectory, fileName, content, FileGenerator.UTF_8_CHARSET);
    }
    
    
    /**
     * Returns the metadata for the given feature.
     * @param featureName the name of the feature.
     * @param locale the locale associated with the metadata.
     * @return the metadata, or which could be empty if the workspace is null or if there is no metadata for the feature.
     */
    private CALFeatureMetadata getMetadata(final CALFeatureName featureName, final Locale locale) {
        if (workspace == null) {
            return MetadataManager.getEmptyMetadata(featureName, locale);
        } else {
            return workspace.getMetadata(featureName, locale);
        }
    }

    /**
     * Returns the metadata for a scoped entity.
     * @param entity the entity.
     * @param locale the locale associated with the metadata.
     * @return the metadata, or which could be empty if the workspace is null or if there is no metadata for the entity.
     */
    private ScopedEntityMetadata getMetadata(final FunctionalAgent entity, final Locale locale) {
        if (workspace == null) {
            return MetadataManager.getEmptyMetadata(entity, locale);
        } else {
            return workspace.getMetadata(entity, locale);
        }
    }

    /**
     * Helper method to create a new HTMLBuilder to start a new page.
     */
    private void startNewCurrentPage() {
        currentPage = new HTMLBuilder();
    }
    
    /**
     * Helper method to create a new HTMLBuilder to start a new page.
     */
    void startNewCurrentPageWithModule(ModuleName moduleName) {
        startNewCurrentPage();
        currentModuleName = moduleName;
    }
    
    /**
     * Helper method (for other clients in this package) to obtain the HTML for the currently generated page.
     * @return the HTML.
     */
    String getCurrentPageHTML() {
        return currentPage.toHTML();
    }

    /////====================================================================================================
    ////
    /// General HTML and CSS generation helpers
    //
    
    /**
     * Constructs the title text of the page, to be displayed in the window title.
     * @param mainTitle the main identifying component of the title.
     * @return the proper title for the page.
     */
    private String makePageTitle(String mainTitle) {
        if (config.windowTitle.length() > 0) {
            return LocalizableUserVisibleString.WINDOW_TITLE_TEMPLATE.toResourceString(mainTitle, config.windowTitle);
        } else {
            return mainTitle;
        }
    }
    
    /**
     * Constructs the HTML attributes for a <tt>link</tt> element for associating
     * the HTML page with an external CSS file.
     * 
     * @param rel the rel attribute.
     * @param title the title attribute.
     * @param href the href attribute.
     * @return the list of HTML attributes for the <tt>link</tt> element.
     */
    private static HTMLBuilder.AttributeList getStylesheetAttributes(String rel, String title, String href) {
        return HTMLBuilder.AttributeList.make(
            HTML.Attribute.REL, rel,
            HTML.Attribute.TYPE, "text/css",
            HTML.Attribute.TITLE, title,
            HTML.Attribute.HREF, href);
    }

    /**
     * Generates the <tt>head</tt> section of the HTML file.
     * 
     * @param title the title of the page.
     * @param relativePathToBaseDirectory the relative path to the base documentation directory.
     * @param linkTargetName if not null, specifies that a <tt>base</tt> element should be included containing the target name. Can be null.
     * @param javascript if not null, specifies a javascript section to be included with the head section.
     * @param additionalCSS if not null, specifies a Map (style id -> css text) of additional style sheets to include.
     */
    private void generateHeadSection(String title, String relativePathToBaseDirectory, String linkTargetName, String javascript, Map<String, String> additionalCSS) {
        currentPage
            .openTag(HTML.Tag.HEAD)
            .addTaggedText(HTML.Tag.TITLE, title)
            .emptyTag(HTML.Tag.META, HTMLBuilder.AttributeList.make(
                HTML.Attribute.HTTPEQUIV, "Content-Type",
                HTML.Attribute.CONTENT, "text/html; charset=UTF-8"));
        
        if (additionalCSS != null) {
            for (final Map.Entry<String, String> entry : additionalCSS.entrySet()) {
                String id = entry.getKey();
                String css = entry.getValue();
            
                currentPage.addTaggedText(HTML.Tag.STYLE, idAttribute(id).concat(HTMLBuilder.AttributeList.make(HTML.Attribute.TYPE, "text/css")), css);
            }
        }
        
        currentPage
            .emptyTag(HTML.Tag.LINK,
                getStylesheetAttributes("stylesheet", LocalizableUserVisibleString.DEFAULT.toResourceString(), relativePathToBaseDirectory + DEFAULT_CSS_FILENAME))
            .emptyTag(HTML.Tag.LINK,
                getStylesheetAttributes("alternate stylesheet", LocalizableUserVisibleString.FOR_PRINTING.toResourceString(), relativePathToBaseDirectory + PRINTED_VERSION_CSS_FILENAME))
            .emptyTag(HTML.Tag.LINK,
                HTMLBuilder.AttributeList.make(HTML4.MEDIA_ATTRIBUTE, "print")
                .concat(getStylesheetAttributes("stylesheet", LocalizableUserVisibleString.FOR_PRINTING.toResourceString(), relativePathToBaseDirectory + PRINTED_VERSION_CSS_FILENAME)));
        
        if (linkTargetName != null) {
            currentPage.emptyTag(HTML.Tag.BASE, HTMLBuilder.AttributeList.make(HTML.Attribute.TARGET, linkTargetName));
        }
        
        if (javascript != null) {
            currentPage.addTaggedText(HTML.Tag.SCRIPT, HTMLBuilder.AttributeList.make(HTML.Attribute.TYPE, "text/javascript"), javascript);
        }
        
        currentPage.closeTag(HTML.Tag.HEAD);
    }
    
    /**
     * Generates the <tt>head</tt> section of the HTML file.
     * 
     * @param title the title of the page.
     * @param relativePathToBaseDirectory the relative path to the base documentation directory.
     * @param linkTargetName if not null, specifies that a <tt>base</tt> element should be included containing the target name. Can be null.
     * @param javascript if not null, specifies a javascript section to be included with the head section.
     */
    private void generateHeadSection(String title, String relativePathToBaseDirectory, String linkTargetName, String javascript) {
        String hideNonPublicStyleCSS =
            new CSSBuilder().addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeClass(StyleClassConstants.NON_PUBLIC_SCOPE))
                .addAttribute(CSS.Attribute.DISPLAY, StyleValueConstants.NONE)).toCSS();
        
        generateHeadSection(title, relativePathToBaseDirectory, linkTargetName, javascript, Collections.singletonMap(ElementID.HIDE_NONPUBLIC_STYLE, hideNonPublicStyleCSS));
    }
    
    /**
     * Generates the javascript function for updating the scope display settings on the current page based on the settings
     * stored in the top level frameset (if it exists).
     */
    private void generateUpdateScopeDisplaySettingsJavascript() {
        String script = getUpdateScopeDisplaySettingsJavascript();
        currentPage.addTaggedText(HTML.Tag.SCRIPT, HTMLBuilder.AttributeList.make(HTML.Attribute.TYPE, "text/javascript"), script);
    }

    /**
     * @return the javascript function for updating the scope display settings on the current page based on the settings
     * stored in the top level frameset (if it exists).
     */
    private String getUpdateScopeDisplaySettingsJavascript() {
        String script =
            "\n" +
            "function getCssRule(styleElem, index) {\n" +
            "    if (styleElem.cssRules) {\n" +
            "        return styleElem.cssRules[index];\n" +
            "    } else if (styleElem.rules) {\n" +
            "        return styleElem.rules[index];\n" +
            "    }\n" +
            "}\n" +
            "var safari = (('' + navigator.vendor).indexOf('Apple') >= 0);\n" +
            "\n" +
            "function changeStyle(styleElem, disabled) {\n" +
            "    if (safari) {\n" +
            "        getCssRule(styleElem, 0).style.cssText = !parent.isPublicOnlyScope() ? '' : 'display: none';\n" +
            "    } else {\n" +
            "        styleElem.disabled = disabled;\n" +
            "    }\n" +
            "}\n" +
            "\n" +
            "function updateScopeDisplaySettings() {\n" +
            "    var styleElement = document.styleSheets[0]; //document.getElementById('" + ElementID.HIDE_NONPUBLIC_STYLE + "');\n" +
            "    if (styleElement) {\n" +
            "        if (parent && parent.isPublicOnlyScope) {\n" +
            "            changeStyle(styleElement, !parent.isPublicOnlyScope());\n" +
            "        } else {\n" +
            "            changeStyle(styleElement, true);\n" +
            "        }\n" +
            "    }\n" +
            "}\n" +
            "\n" +
            "updateScopeDisplaySettings(); // run it immediately\n";
        
        return script;
    }
    
    /**
     * Constructs the text of the default CSS file for display within a small area.
     * @return the CSS file's contents.
     */
    /*
     * @implementation this method is exposed via package scope to allow reuse by the CALDocToTooltipHTMLUtilities
     */
    static String getCompactDisplayCSS() {
        CSSBuilder cssBuilder = new CSSBuilder();
        
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeType(HTML.Tag.BODY))
            .addAttribute(CSS.Attribute.FONT_FAMILY, StyleValueConstants.SANS_SERIF_FONT_LIST)
            .addAttribute(CSS.Attribute.FONT_SIZE, "11px")
            .addAttribute(CSS.Attribute.LINE_HEIGHT, "14px")
            .addAttribute(CSS.Attribute.MARGIN, "3px")
            .addAttribute(CSS.Attribute.PADDING, "0px"));
    
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeType(HTML.Tag.DL))
            .addAttribute(CSS.Attribute.MARGIN_TOP, StyleValueConstants.TWO_PX_THIN));

        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeType(HTML.Tag.DD))
            .addAttribute(CSS.Attribute.MARGIN_LEFT, "10px"));
    
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeDescendant(CSSBuilder.Selector.makeType(HTML.Tag.DD), CSSBuilder.Selector.makeType(HTML.Tag.DD)))
            .addAttribute(CSS.Attribute.MARGIN_LEFT, "20px"));

        return getStandardCSS(cssBuilder, "2px", StyleValueConstants.CAL_EDITOR_HOVER_BACKGROUND_COLOR); 
    }
    
    /**
     * Constructs the text of the default CSS file.
     * @return the CSS file's contents.
     */
    private static String getDefaultCSS() {
        CSSBuilder cssBuilder = new CSSBuilder();
        
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeType(HTML.Tag.BODY))
            .addAttribute(CSS.Attribute.FONT_FAMILY, StyleValueConstants.SANS_SERIF_FONT_LIST)
            .addAttribute(CSS.Attribute.FONT_SIZE, "11px"));
        
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeType(HTML.Tag.BODY))
            .addAttribute(CSS.Attribute.LINE_HEIGHT, "1.7em"));
        
        return getStandardCSS(cssBuilder, StyleValueConstants.ONE_EM_WIDTH,
            StyleValueConstants.WHITE); 
    }
    
    /**
     * Constructs the text of the CSS file for formatting for printing.
     * @return the CSS file's contents.
     */
    private static String getPrintedVersionCSS() {
        CSSBuilder cssBuilder = new CSSBuilder();
        
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeType(HTML.Tag.BODY))
            .addAttribute(CSS.Attribute.FONT_FAMILY, StyleValueConstants.SERIF_FONT_LIST)
            .addAttribute(CSS.Attribute.FONT_SIZE, "11pt"));
        
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeType(HTML.Tag.CODE))
            .addAttribute(CSS.Attribute.FONT_SIZE, "10pt"));
        
        return getStandardCSS(cssBuilder, StyleValueConstants.ONE_EM_WIDTH, StyleValueConstants.WHITE);
    }
    
    /**
     * Constructs the common portion of the CSS files using the specified builder.
     * @param cssBuilder the builder to use for building the CSS.
     * @param defintionLineTopBottomMargin the top/bottom margin surrounding a line containing a definition.
     * @return the CSS file's contents.
     */
    private static String getStandardCSS(final CSSBuilder cssBuilder, 
                                         final String defintionLineTopBottomMargin,
                                         final String bodyBackgroundColor) {
        
        ////
        /// General styles
        //
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeClass(StyleClassConstants.WITH_MAIN_CONTENT))
            .addAttribute(CSS.Attribute.MARGIN, StyleValueConstants.ZERO_WIDTH)
            .addAttribute(CSS.Attribute.PADDING, StyleValueConstants.ZERO_WIDTH));
        
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeClass(StyleClassConstants.MAIN_CONTENT))
            .addAttribute(CSS.Attribute.MARGIN, StyleValueConstants.MAIN_CONTENT_MARGIN));
        
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeType(HTML.Tag.BODY))
            .addAttribute(CSS.Attribute.BACKGROUND_COLOR, bodyBackgroundColor));
        
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeClass(StyleClassConstants.NON_DISPLAYED_HEADER))
            .addAttribute(CSS.Attribute.DISPLAY, StyleValueConstants.NONE));
        
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeType(HTML.Tag.H1))
            .addAttribute(CSS.Attribute.FONT_SIZE, StyleValueConstants.X_LARGE));
        
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeType(HTML.Tag.H2))
            .addAttribute(CSS.Attribute.FONT_SIZE, StyleValueConstants.LARGE));
        
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeType(HTML.Tag.H1), CSSBuilder.Selector.makeType(HTML.Tag.H2))
            .addAttribute(CSS.Attribute.COLOR, StyleValueConstants.OPENQUARK_BLUE)
            .addAttribute(CSS.Attribute.FONT_FAMILY, StyleValueConstants.SANS_SERIF_FONT_LIST));
        
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(StyleClassConstants.A_LINK_PSEUDOCLASS_SELECTOR)
            .addAttribute(CSS.Attribute.COLOR, StyleValueConstants.OPENQUARK_BLUE));
        
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(
            StyleClassConstants.A_HOVER_PSEUDOCLASS_SELECTOR,
            CSSBuilder.Selector.makeDescendant(StyleClassConstants.A_HOVER_PSEUDOCLASS_SELECTOR, CSSBuilder.Selector.makeClass(StyleClassConstants.TYPE_CONSTRAINT)))
            .addAttribute(CSS.Attribute.BACKGROUND_COLOR, StyleValueConstants.OPENQUARK_WEBSITE_KHAKI));
        
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeDescendant(CSSBuilder.Selector.makeType(HTML.Tag.A), CSSBuilder.Selector.makeClass(StyleClassConstants.TYPE_CONSTRAINT)))
            .addAttribute(CSS.Attribute.TEXT_DECORATION, StyleValueConstants.UNDERLINE));
        
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(StyleClassConstants.A_VISITED_PSEUDOCLASS_SELECTOR)
            .addAttribute(CSS.Attribute.COLOR, StyleValueConstants.VISITED_LINK_COLOR));
        
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeClass(StyleClassConstants.IMPORTANT_LINK))
            .addAttribute(CSS.Attribute.FONT_SIZE, StyleValueConstants.LARGER));
        
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(
            CSSBuilder.Selector.makeClass(CSSBuilder.Selector.makeType(HTML.Tag.DIV), StyleClassConstants.PUBLIC_SCOPE),
            CSSBuilder.Selector.makeClass(CSSBuilder.Selector.makeType(HTML.Tag.DIV), StyleClassConstants.NON_PUBLIC_SCOPE))
            .addAttribute(CSS.Attribute.MARGIN, StyleValueConstants.ZERO_WIDTH)
            .addAttribute(CSS.Attribute.PADDING, StyleValueConstants.ZERO_WIDTH));
        
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeClass(StyleClassConstants.MAJOR_SECTION))
            .addAttribute(CSS.Attribute.BACKGROUND_COLOR, StyleValueConstants.OPENQUARK_WEBSITE_KHAKI)
            .addAttribute(CSS.Attribute.PADDING, StyleValueConstants.MAJOR_SECTION_TOP_AND_SIDE_PADDING)
            .addAttribute(CSS.Attribute.PADDING_BOTTOM, StyleValueConstants.ONE_EM_WIDTH));
        
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeClass(StyleClassConstants.MINOR_SECTION))
            .addAttribute(CSS.Attribute.COLOR, StyleValueConstants.OPENQUARK_BLUE)
            .addAttribute(CSS.Attribute.FONT_FAMILY, StyleValueConstants.SANS_SERIF_FONT_LIST)
            .addAttribute(CSS.Attribute.FONT_SIZE, StyleValueConstants.LARGER)
            .addAttribute(CSS.Attribute.FONT_WEIGHT, StyleValueConstants.BOLD)
            .addAttribute(CSS.Attribute.BORDER_BOTTOM, StyleValueConstants.TWO_PX_THIN + " " + StyleValueConstants.SOLID + " " + StyleValueConstants.OPENQUARK_RED));
        
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeClass(StyleClassConstants.MINOR_SECTION_ALTERNATE))
            .addAttribute(CSS.Attribute.BORDER_BOTTOM, StyleValueConstants.TWO_PX_THIN + " " + StyleValueConstants.SOLID + " " + StyleValueConstants.OPENQUARK_SECONDARY_KHAKI));
        
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeClass(StyleClassConstants.PAGE_BOTTOM))
            .addAttribute(CSS.Attribute.FONT_SIZE, StyleValueConstants.SMALLER));
        
        // Create a hanging indent look for a type signature (namd and type).
        // This is done by making the type signature display as a block, with some padding on the left,
        // but with a *negative* text indent by the same amount applied so that the first line juts out to the left.
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeClass(StyleClassConstants.NAME_AND_TYPE))
            .addAttribute(CSS.Attribute.DISPLAY, StyleValueConstants.BLOCK)
            .addAttribute(CSS.Attribute.PADDING_LEFT, StyleValueConstants.TYPE_SIGNATURE_HANGING_INDENT)
            .addAttribute(CSS.Attribute.TEXT_INDENT, "-" + StyleValueConstants.TYPE_SIGNATURE_HANGING_INDENT));
        
        ////
        /// Side bars 
        //
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeClass(StyleClassConstants.MODULE_LIST_PAGE_TITLE))
            .addAttribute(CSS.Attribute.FONT_SIZE, StyleValueConstants.MEDIUM)
            .addAttribute(CSS.Attribute.COLOR, StyleValueConstants.OPENQUARK_BLUE)
            .addAttribute(CSS.Attribute.MARGIN, StyleValueConstants.ZERO_WIDTH)
            .addAttribute(CSS.Attribute.PADDING, StyleValueConstants.NAV_BAR_PADDING)
            .addAttribute(CSS.Attribute.PADDING_BOTTOM, StyleValueConstants.ZERO_WIDTH));
        
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeClass(StyleClassConstants.MODULE_LIST_NAV_BAR))
            .addAttribute(CSS.Attribute.PADDING, StyleValueConstants.NAV_BAR_PADDING)
            .addAttribute(CSS.Attribute.COLOR, StyleValueConstants.OPENQUARK_BLUE));
        
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeClass(StyleClassConstants.MODULE_LIST_SECTION_TITLE))
            .addAttribute(CSS.Attribute.FONT_WEIGHT, StyleValueConstants.BOLD)
            .addAttribute(CSS.Attribute.BACKGROUND_COLOR, StyleValueConstants.OPENQUARK_WEBSITE_KHAKI_LIGHTER)
            .addAttribute(CSS.Attribute.BORDER_LEFT, StyleValueConstants.MODULE_LIST_SECTION_TITLE_LEFT_BORDER_STYLE)
            .addAttribute(CSS.Attribute.PADDING_LEFT, StyleValueConstants.INDEX_NAV_LINK_PADDING_HALF)
            .addAttribute(CSS.Attribute.BORDER_TOP, StyleValueConstants.INDEX_NAV_CURRENT_LINK_BORDER_STYLE)
            .addAttribute(CSS.Attribute.PADDING_TOP, StyleValueConstants.INDEX_NAV_LINK_PADDING_HALF_MINUS_ONE)
            .addAttribute(CSS.Attribute.BORDER_BOTTOM, StyleValueConstants.INDEX_NAV_CURRENT_LINK_BORDER_STYLE)
            .addAttribute(CSS.Attribute.PADDING_BOTTOM, StyleValueConstants.INDEX_NAV_LINK_PADDING_HALF_MINUS_ONE)
            .addAttribute(CSS.Attribute.BORDER_RIGHT, StyleValueConstants.INDEX_NAV_CURRENT_LINK_BORDER_STYLE));

        // we do not indent the <div> for the outermost level, so we use a descendant selector
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeDescendant(CSSBuilder.Selector.makeClass(StyleClassConstants.TREE_DIV), CSSBuilder.Selector.makeClass(StyleClassConstants.TREE_DIV)))
            .addAttribute(CSS.Attribute.PADDING_LEFT, StyleValueConstants.TREE_INDENT));
        
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeClass(StyleClassConstants.TREE_NODE_TOGGLE))
            .addAttribute(CSS2Attribute.CURSOR, StyleValueConstants.POINTER)
            .addAttribute(CSS.Attribute.BORDER, StyleValueConstants.TREE_NODE_TOGGLE_BORDER_STYLE)
            .addAttribute(CSS.Attribute.PADDING_LEFT, StyleValueConstants.TWO_PX_THIN)
            .addAttribute(CSS.Attribute.PADDING_RIGHT, StyleValueConstants.TWO_PX_THIN)
            .addAttribute(CSS.Attribute.FONT_FAMILY, StyleValueConstants.MONOSPACE_FONT_LIST));
    
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makePseudoClass(CSSBuilder.Selector.makeClass(StyleClassConstants.TREE_NODE_TOGGLE), "hover"))
            .addAttribute(CSS.Attribute.BACKGROUND_COLOR, StyleValueConstants.TREE_NODE_TOGGLE_HOVER_BACKGROUND_COLOR));

        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeClass(StyleClassConstants.TREE_NODE_TOGGLE_PLACEHOLDER))
            .addAttribute(CSS.Attribute.COLOR, StyleValueConstants.WHITE)
            .addAttribute(CSS.Attribute.BORDER_COLOR, StyleValueConstants.WHITE)
            .addAttribute(CSS.Attribute.BORDER_STYLE, StyleValueConstants.SOLID)
            .addAttribute(CSS.Attribute.BORDER_WIDTH, StyleValueConstants.THIN)
            .addAttribute(CSS.Attribute.PADDING_LEFT, StyleValueConstants.TWO_PX_THIN)
            .addAttribute(CSS.Attribute.PADDING_RIGHT, StyleValueConstants.TWO_PX_THIN)
            .addAttribute(CSS.Attribute.FONT_FAMILY, StyleValueConstants.MONOSPACE_FONT_LIST));
    
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeClass(StyleClassConstants.SIDE_BAR_KHAKI_TITLE))
            .addAttribute(CSS.Attribute.FONT_SIZE, StyleValueConstants.MEDIUM)
            .addAttribute(CSS.Attribute.COLOR, StyleValueConstants.OPENQUARK_WEBSITE_KHAKI_SIDE_BAR_TITLE_COLOR)
            .addAttribute(CSS.Attribute.BACKGROUND_COLOR, StyleValueConstants.OPENQUARK_WEBSITE_KHAKI_SIDE_BAR_TITLE_BACKGROUND_COLOR)
            .addAttribute(CSS.Attribute.MARGIN, StyleValueConstants.ZERO_WIDTH)
            .addAttribute(CSS.Attribute.PADDING, StyleValueConstants.SIDE_BAR_TITLE_PADDING));
        
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeClass(StyleClassConstants.SIDE_BAR_KHAKI_SUPERTITLE))
            .addAttribute(CSS.Attribute.FONT_WEIGHT, StyleValueConstants.BOLD)
            .addAttribute(CSS.Attribute.COLOR, StyleValueConstants.OPENQUARK_WEBSITE_KHAKI_SIDE_BAR_SUPERTITLE_COLOR)
            .addAttribute(CSS.Attribute.BACKGROUND_COLOR, StyleValueConstants.OPENQUARK_WEBSITE_KHAKI_SIDE_BAR_TITLE_BACKGROUND_COLOR)
            .addAttribute(CSS.Attribute.PADDING, StyleValueConstants.SIDE_BAR_TITLE_PADDING)
            .addAttribute(CSS.Attribute.PADDING_BOTTOM, StyleValueConstants.ZERO_WIDTH));
        
        ////
        /// Display mode toolbar
        //
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeClass(StyleClassConstants.TOOLBAR))
            .addAttribute(CSS.Attribute.BACKGROUND_COLOR, StyleValueConstants.TOOLBAR_BACKGROUND_COLOR)
            .addAttribute(CSS.Attribute.PADDING, StyleValueConstants.TOOLBAR_PADDING));
        
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeDescendant(CSSBuilder.Selector.makeClass(StyleClassConstants.TOOLBAR), CSSBuilder.Selector.makeType(HTML.Tag.A)))
            .addAttribute(CSS2Attribute.CURSOR, StyleValueConstants.POINTER)
            .addAttribute(CSS.Attribute.BORDER, StyleValueConstants.TOOLBAR_BUTTON_BORDER_STYLE)
            .addAttribute(CSS.Attribute.BACKGROUND_COLOR, StyleValueConstants.TOOLBAR_BUTTON_BACKGROUND_COLOR)
            .addAttribute(CSS.Attribute.PADDING, StyleValueConstants.TOOLBAR_BUTTON_PADDING));
    
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeDescendant(CSSBuilder.Selector.makeClass(StyleClassConstants.TOOLBAR), StyleClassConstants.A_HOVER_PSEUDOCLASS_SELECTOR))
            .addAttribute(CSS.Attribute.BORDER, StyleValueConstants.TOOLBAR_BUTTON_BLACK_BORDER_STYLE)
            .addAttribute(CSS.Attribute.BACKGROUND_COLOR, StyleValueConstants.TOOLBAR_BUTTON_HOVER_BACKGROUND_COLOR));

        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeDescendant(CSSBuilder.Selector.makeClass(StyleClassConstants.TOOLBAR), CSSBuilder.Selector.makeClass(CSSBuilder.Selector.makeType(HTML.Tag.A), StyleClassConstants.BUTTON_SELECTED)))
            .addAttribute(CSS.Attribute.BORDER, StyleValueConstants.TOOLBAR_BUTTON_BLACK_BORDER_STYLE)
            .addAttribute(CSS.Attribute.BACKGROUND_COLOR, StyleValueConstants.TOOLBAR_BUTTON_SELECTED_BACKGROUND_COLOR));

        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeClass(StyleClassConstants.SUB_TOOLBAR))
            .addAttribute(CSS.Attribute.TEXT_ALIGN, StyleValueConstants.RIGHT_ALIGN)
            .addAttribute(CSS.Attribute.BACKGROUND_COLOR, StyleValueConstants.SUB_TOOLBAR_BACKGROUND_COLOR)
            .addAttribute(CSS.Attribute.PADDING_TOP, StyleValueConstants.SUB_TOOLBAR_BUTTON_TOP_BOTTOM_PADDING)
            .addAttribute(CSS.Attribute.PADDING_BOTTOM, StyleValueConstants.SUB_TOOLBAR_BUTTON_TOP_BOTTOM_PADDING));
    
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeDescendant(CSSBuilder.Selector.makeClass(StyleClassConstants.SUB_TOOLBAR), CSSBuilder.Selector.makePseudoClass(CSSBuilder.Selector.makeClass(StyleClassConstants.TREE_NODE_TOGGLE), "hover")))
            .addAttribute(CSS.Attribute.BACKGROUND_COLOR, StyleValueConstants.TOOLBAR_BUTTON_HOVER_BACKGROUND_COLOR)
            .addAttribute(CSS.Attribute.BORDER, StyleValueConstants.TOOLBAR_BUTTON_BLACK_BORDER_STYLE));

        ////
        /// Overview Page
        //
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeDescendant(CSSBuilder.Selector.makeClass(StyleClassConstants.TREE_DIV_OVERVIEW_PAGE), CSSBuilder.Selector.makeClass(StyleClassConstants.TREE_DIV_OVERVIEW_PAGE)))
            .addAttribute(CSS.Attribute.BORDER_LEFT, StyleValueConstants.TREE_DIV_OVERVIEW_PAGE_LEFT_BORDER_STYLE)
            .addAttribute(CSS.Attribute.BORDER_BOTTOM, StyleValueConstants.TREE_DIV_OVERVIEW_PAGE_BOTTOM_BORDER_STYLE)
            .addAttribute(CSS.Attribute.PADDING_BOTTOM, StyleValueConstants.ZERO_WIDTH)
            .addAttribute(CSS.Attribute.MARGIN_BOTTOM, StyleValueConstants.TREE_DIV_OVERVIEW_PAGE_BOTTOM_MARGIN));
        
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeClass(StyleClassConstants.DL_OVERVIEW_PAGE))
            .addAttribute(CSS.Attribute.MARGIN_BOTTOM, StyleValueConstants.TREE_DIV_OVERVIEW_PAGE_BOTTOM_MARGIN)
            .addAttribute(CSS.Attribute.PADDING_BOTTOM, StyleValueConstants.DL_OVERVIEW_PAGE_BOTTOM_PADDING)
            .addAttribute(CSS.Attribute.BORDER_BOTTOM, StyleValueConstants.DL_OVERVIEW_PAGE_BORDER_STYLE));
    
        ////
        /// Search Page
        //
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeClass(StyleClassConstants.SEARCH_BOX_TITLE))
            .addAttribute(CSS.Attribute.FONT_SIZE, StyleValueConstants.LARGER)
            .addAttribute(CSS.Attribute.FONT_WEIGHT, StyleValueConstants.BOLD)
            .addAttribute(CSS.Attribute.COLOR, StyleValueConstants.OPENQUARK_WEBSITE_KHAKI_SIDE_BAR_TITLE_COLOR)
            .addAttribute(CSS.Attribute.BACKGROUND_COLOR, StyleValueConstants.OPENQUARK_WEBSITE_KHAKI_SIDE_BAR_TITLE_BACKGROUND_COLOR)
            .addAttribute(CSS.Attribute.MARGIN, StyleValueConstants.ZERO_WIDTH));
        
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeClass(StyleClassConstants.SEARCH_BOX))
            .addAttribute(CSS.Attribute.BACKGROUND_COLOR, StyleValueConstants.OPENQUARK_WEBSITE_KHAKI)
            .addAttribute(CSS.Attribute.PADDING, StyleValueConstants.SEARCH_BOX_TOP_PADDING)
            .addAttribute(CSS.Attribute.PADDING_RIGHT, StyleValueConstants.SEARCH_BOX_RIGHT_PADDING));
        
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeClass(StyleClassConstants.SEARCH_FIELD))
            .addAttribute(CSS.Attribute.WIDTH, StyleValueConstants.ONE_HUNDRED_PERCENT));
        
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeType(HTML.Tag.FORM))
            .addAttribute(CSS.Attribute.MARGIN, StyleValueConstants.ZERO_WIDTH)
            .addAttribute(CSS.Attribute.PADDING, StyleValueConstants.ZERO_WIDTH));
        
        ////
        /// Code blocks
        //
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeClass(StyleClassConstants.CODE_BLOCK))
            .addAttribute(CSS.Attribute.FONT_FAMILY, StyleValueConstants.MONOSPACE_FONT_LIST)
            .addAttribute(CSS.Attribute.PADDING_LEFT, StyleValueConstants.CODE_BLOCK_SIDE_PADDING)
            .addAttribute(CSS.Attribute.PADDING_RIGHT, StyleValueConstants.CODE_BLOCK_SIDE_PADDING)
            .addAttribute(CSS.Attribute.COLOR, StyleValueConstants.CODE_BLOCK_COLOR)
            .addAttribute(CSS.Attribute.BACKGROUND_COLOR, StyleValueConstants.CODE_BLOCK_BACKGROUND_COLOR));
        
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeClass(CSSBuilder.Selector.makeType(HTML.Tag.PRE), StyleClassConstants.CODE_BLOCK))
            .addAttribute(CSS.Attribute.BORDER_STYLE, StyleValueConstants.SOLID)
            .addAttribute(CSS.Attribute.BORDER_WIDTH, StyleValueConstants.THIN)
            .addAttribute(CSS.Attribute.BORDER_COLOR, StyleValueConstants.OPENQUARK_WEBSITE_KHAKI_SIDE_BAR_TITLE_BACKGROUND_COLOR));
        
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeDescendant(CSSBuilder.Selector.makeClass(StyleClassConstants.CODE_BLOCK), CSSBuilder.Selector.makeType(HTML.Tag.A)))
            .addAttribute(CSS.Attribute.COLOR, StyleValueConstants.CODE_BLOCK_COLOR));
        
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeDescendant(CSSBuilder.Selector.makeClass(StyleClassConstants.ABSTRACT_DEPRECATED_BLOCK), CSSBuilder.Selector.makeClass(StyleClassConstants.CODE_BLOCK)))
            .addAttribute(CSS.Attribute.BACKGROUND_COLOR, StyleValueConstants.DEPRECATED_BLOCK_CODE_BLOCK_COLOR));
        
        ////
        /// Deprecated blocks
        //
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeClass(StyleClassConstants.ABSTRACT_DEPRECATED_BLOCK))
            .addAttribute(CSS.Attribute.BACKGROUND_COLOR, StyleValueConstants.DEPRECATED_BLOCK_BACKGROUND_COLOR)
            .addAttribute(CSS.Attribute.BORDER_WIDTH, StyleValueConstants.THIN)
            .addAttribute(CSS.Attribute.BORDER_STYLE, StyleValueConstants.SOLID)
            .addAttribute(CSS.Attribute.BORDER_COLOR, StyleValueConstants.DEPRECATED_BLOCK_BORDER_COLOR)
            .addAttribute(CSS.Attribute.FONT_STYLE, StyleValueConstants.ITALIC)
            .addAttribute(CSS.Attribute.PADDING_LEFT, StyleValueConstants.ONE_EM_WIDTH));
        
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeClass(StyleClassConstants.OVERVIEW_DEPRECATED_BLOCK))
            .addAttribute(CSS.Attribute.MARGIN_BOTTOM, StyleValueConstants.DEPRECATED_BLOCK_CLEARANCE));
        
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeClass(StyleClassConstants.DEPRECATED_BLOCK))
            .addAttribute(CSS.Attribute.MARGIN_TOP, StyleValueConstants.DEPRECATED_BLOCK_CLEARANCE));
        
        ////
        /// Module overview
        //
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeClass(StyleClassConstants.OVERVIEW_TABLE))
            .addAttribute(CSS.Attribute.MARGIN_BOTTOM, StyleValueConstants.TWO_EM_WIDTH));
        
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeDescendant(CSSBuilder.Selector.makeClass(StyleClassConstants.OVERVIEW_TABLE), CSSBuilder.Selector.makeType(HTML.Tag.DL)))
            .addAttribute(CSS.Attribute.MARGIN_TOP, StyleValueConstants.ZERO_WIDTH)
            .addAttribute(CSS.Attribute.MARGIN_BOTTOM, StyleValueConstants.ZERO_WIDTH)
            .addAttribute(CSS.Attribute.PADDING_TOP, StyleValueConstants.ZERO_WIDTH)
            .addAttribute(CSS.Attribute.PADDING_BOTTOM, StyleValueConstants.ZERO_WIDTH));
        
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeDescendant(CSSBuilder.Selector.makeClass(StyleClassConstants.OVERVIEW_TABLE), CSSBuilder.Selector.makeType(HTML.Tag.CAPTION)))
            .addAttribute(CSS.Attribute.WIDTH, StyleValueConstants.AUTO_WIDTH)
            .addAttribute(CSS.Attribute.TEXT_ALIGN, StyleValueConstants.LEFT_ALIGN));
        
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeClass(StyleClassConstants.OVERVIEW_TABLE_SCOPE_COLUMN))
            .addAttribute(CSS.Attribute.WIDTH, StyleValueConstants.TWO_EM_WIDTH));
        
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeClass(StyleClassConstants.OVERVIEW_NESTED_TABLE))
            .addAttribute(CSS.Attribute.MARGIN_TOP, StyleValueConstants.ONE_POINT_FIVE_EM_WIDTH)
            .addAttribute(CSS.Attribute.BACKGROUND_COLOR, StyleValueConstants.OVERVIEW_NESTED_TABLE_BACKGROUND_COLOR)
            .addAttribute(CSS.Attribute.BORDER_COLOR, StyleValueConstants.OVERVIEW_NESTED_TABLE_BORDER_COLOR));
        
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeClass(StyleClassConstants.OVERVIEW_TABLE_DESCRIPTION))
            .addAttribute(CSS.Attribute.MARGIN_LEFT, StyleValueConstants.TWO_EM_WIDTH));
        
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeClass(StyleClassConstants.OVERVIEW_REFERENCE))
            .addAttribute(CSS.Attribute.FONT_WEIGHT, StyleValueConstants.BOLD));
        
        // Create a hanging indent look for a related modules list.
        // This is done by making the display as a block, with some padding on the left,
        // but with a *negative* text indent by the same amount applied so that the first line juts out to the left.
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeClass(StyleClassConstants.RELATED_MODULES_LIST))
            .addAttribute(CSS.Attribute.PADDING_LEFT, StyleValueConstants.RELATED_MODULES_GROUPED_LIST_HANGING_INDENT)
            .addAttribute(CSS.Attribute.TEXT_INDENT, "-" + StyleValueConstants.RELATED_MODULES_GROUPED_LIST_HANGING_INDENT));
        
        ////
        /// Details section: high-level style classes
        //
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeClass(StyleClassConstants.DETAILS_LIST_HEADER))
            .addAttribute(CSS.Attribute.COLOR, StyleValueConstants.OPENQUARK_BLUE)
            .addAttribute(CSS.Attribute.BACKGROUND_COLOR, StyleValueConstants.OPENQUARK_WEBSITE_KHAKI)
            .addAttribute(CSS.Attribute.FONT_FAMILY, StyleValueConstants.SANS_SERIF_FONT_LIST)
            .addAttribute(CSS.Attribute.FONT_WEIGHT, StyleValueConstants.BOLD));
        
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeClass(StyleClassConstants.DETAILS_LIST))
            .addAttribute(CSS.Attribute.BACKGROUND_COLOR, StyleValueConstants.OVERVIEW_NESTED_TABLE_BACKGROUND_COLOR)
            .addAttribute(CSS.Attribute.BORDER_WIDTH, StyleValueConstants.THIN)
            .addAttribute(CSS.Attribute.BORDER_STYLE, StyleValueConstants.OUTSET_BORDER_STYLE)
            .addAttribute(CSS.Attribute.PADDING, StyleValueConstants.DETAILS_LIST_PADDING)
            .addAttribute(CSS.Attribute.MARGIN_TOP, StyleValueConstants.DETAILS_LIST_MARGIN_TOP));
        
        ////
        /// Details section: low-level style classes
        //
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeClass(StyleClassConstants.ATTRIBUTE_HEADER))
            .addAttribute(CSS.Attribute.FONT_WEIGHT, StyleValueConstants.BOLD));
        
        /// CALDoc/metadata indicator
        //
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeClass(StyleClassConstants.ABSTRACT_INDICATOR))
            .addAttribute(CSS.Attribute.FONT_FAMILY, StyleValueConstants.SANS_SERIF_FONT_LIST)
            .addAttribute(CSS.Attribute.FONT_SIZE, StyleValueConstants.INDICATOR_FONT_SIZE));
        
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeClass(StyleClassConstants.METADATA_INDICATOR))
            .addAttribute(CSS.Attribute.COLOR, StyleValueConstants.METADATA_INDICATOR_COLOR));
        
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeClass(StyleClassConstants.CALDOC_INDICATOR))
            .addAttribute(CSS.Attribute.COLOR, StyleValueConstants.CALDOC_INDICATOR_COLOR));
        
        /// Class method indicator
        //
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeClass(StyleClassConstants.CLASS_METHOD_INDICATOR))
            .addAttribute(CSS.Attribute.DISPLAY, StyleValueConstants.BLOCK)
            .addAttribute(CSS.Attribute.FONT_STYLE, StyleValueConstants.ITALIC));
        
        /// Definition - header section
        //
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeClass(StyleClassConstants.ABSTRACT_DEFINITION_SECTION_START))
            .addAttribute(CSS.Attribute.MARGIN_TOP, defintionLineTopBottomMargin));
        
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeClass(StyleClassConstants.DEFINITION_SECTION_START))
            .addAttribute(CSS.Attribute.BORDER_TOP, StyleValueConstants.THIN + " " + StyleValueConstants.SOLID)
            .addAttribute(CSS.Attribute.PADDING_TOP, defintionLineTopBottomMargin));
        
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeClass(StyleClassConstants.DEFINITION_HEADER))
            .addAttribute(CSS.Attribute.FONT_SIZE, StyleValueConstants.LARGER));
        
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeClass(StyleClassConstants.DEFINITION_HEADER_FIRST_LINE))
            .addAttribute(CSS.Attribute.DISPLAY, StyleValueConstants.BLOCK)
            .addAttribute(CSS.Attribute.MARGIN_BOTTOM, defintionLineTopBottomMargin));
        
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeClass(StyleClassConstants.DECLARED_NAME))
            .addAttribute(CSS.Attribute.FONT_WEIGHT, StyleValueConstants.BOLD));
        
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeClass(StyleClassConstants.DESCRIPTION_BLOCK))
            .addAttribute(CSS.Attribute.MARGIN_TOP, StyleValueConstants.ONE_EM_WIDTH)
            .addAttribute(CSS.Attribute.MARGIN_BOTTOM, StyleValueConstants.ONE_EM_WIDTH));
        
        /// Argument names
        //
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeClass(StyleClassConstants.ARG_NAME_NOT_FROM_CODE))
            .addAttribute(CSS.Attribute.FONT_STYLE, StyleValueConstants.ITALIC));
        
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeClass(StyleClassConstants.RETURN_VALUE_INDICIATOR))
            .addAttribute(CSS.Attribute.FONT_STYLE, StyleValueConstants.ITALIC));
        
        /// Type signatures
        //
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeClass(StyleClassConstants.TYPE_SIGNATURE))
            .addAttribute(CSS2Attribute.TEXT_WRAP, StyleValueConstants.SUPPRESS_TEXT_WRAP));
        
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeClass(StyleClassConstants.TYPE_CONSTRAINT))
            .addAttribute(CSS.Attribute.BACKGROUND_COLOR, StyleValueConstants.TYPE_CONSTRAINT_BACKGROUND_COLOR));
        
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeClass(StyleClassConstants.CAL_SYMBOL))
            .addAttribute(CSS2Attribute.TEXT_WRAP, StyleValueConstants.NO_TEXT_WRAP)
            .addAttribute(CSS.Attribute.WHITE_SPACE, StyleValueConstants.NOWRAP_WHITE_SPACE));
        
        ////
        /// Navigation frames
        //
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeClass(StyleClassConstants.INDEX_NAV))
            .addAttribute(CSS.Attribute.BACKGROUND_COLOR, StyleValueConstants.OPENQUARK_WEBSITE_KHAKI)
            .addAttribute(CSS.Attribute.FONT_WEIGHT, StyleValueConstants.BOLD));
        
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeDescendant(CSSBuilder.Selector.makeClass(StyleClassConstants.INDEX_NAV_LINK), CSSBuilder.Selector.makeType(HTML.Tag.A)))
            .addAttribute(CSS.Attribute.DISPLAY, StyleValueConstants.BLOCK)
            .addAttribute(CSS.Attribute.BORDER_TOP, StyleValueConstants.INDEX_NAV_LINK_SEPARATOR_BORDER_STYLE)
            .addAttribute(CSS.Attribute.PADDING_TOP, StyleValueConstants.INDEX_NAV_LINK_PADDING_HALF_MINUS_ONE)
            .addAttribute(CSS.Attribute.PADDING_BOTTOM, StyleValueConstants.INDEX_NAV_LINK_PADDING_HALF)
            .addAttribute(CSS.Attribute.PADDING_LEFT, StyleValueConstants.INDEX_NAV_LINK_PADDING)
            .addAttribute(CSS.Attribute.PADDING_RIGHT, StyleValueConstants.INDEX_NAV_LINK_PADDING));
        
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeClass(StyleClassConstants.INDEX_NAV_CURRENT_LINK))
            .addAttribute(CSS.Attribute.FONT_WEIGHT, StyleValueConstants.BOLD)
            .addAttribute(CSS.Attribute.BACKGROUND_COLOR, StyleValueConstants.OPENQUARK_WEBSITE_KHAKI_LIGHTER)
            .addAttribute(CSS.Attribute.BORDER_LEFT, StyleValueConstants.INDEX_NAV_CURRENT_LINK_LEFT_BORDER_STYLE)
            .addAttribute(CSS.Attribute.PADDING_LEFT, StyleValueConstants.INDEX_NAV_LINK_PADDING_HALF)
            .addAttribute(CSS.Attribute.BORDER_TOP, StyleValueConstants.INDEX_NAV_CURRENT_LINK_BORDER_STYLE)
            .addAttribute(CSS.Attribute.PADDING_TOP, StyleValueConstants.INDEX_NAV_LINK_PADDING_HALF_MINUS_ONE)
            .addAttribute(CSS.Attribute.BORDER_BOTTOM, StyleValueConstants.INDEX_NAV_CURRENT_LINK_BORDER_STYLE)
            .addAttribute(CSS.Attribute.PADDING_BOTTOM, StyleValueConstants.INDEX_NAV_LINK_PADDING_HALF_MINUS_ONE)
            .addAttribute(CSS.Attribute.BORDER_RIGHT, StyleValueConstants.INDEX_NAV_CURRENT_LINK_BORDER_STYLE));
        
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeDescendant(CSSBuilder.Selector.makeClass(StyleClassConstants.INDEX_NAV_LINK), StyleClassConstants.A_HOVER_PSEUDOCLASS_SELECTOR))
            .addAttribute(CSS.Attribute.BACKGROUND_COLOR, StyleValueConstants.OPENQUARK_WEBSITE_LIGHT_BLUE_HEADER_BACKGROUND)
            .addAttribute(CSS.Attribute.BORDER_LEFT, StyleValueConstants.INDEX_NAV_CURRENT_LINK_LEFT_BORDER_STYLE)
            .addAttribute(CSS.Attribute.PADDING_LEFT, StyleValueConstants.INDEX_NAV_LINK_PADDING_HALF)
            .addAttribute(CSS.Attribute.BORDER_TOP, StyleValueConstants.INDEX_NAV_LINK_SEPARATOR_BORDER_STYLE)
            .addAttribute(CSS.Attribute.PADDING_TOP, StyleValueConstants.INDEX_NAV_LINK_PADDING_HALF_MINUS_ONE)
            .addAttribute(CSS.Attribute.BORDER_BOTTOM, StyleValueConstants.INDEX_NAV_LINK_SEPARATOR_BORDER_STYLE)
            .addAttribute(CSS.Attribute.PADDING_BOTTOM, StyleValueConstants.INDEX_NAV_LINK_PADDING_HALF_MINUS_ONE)
            .addAttribute(CSS.Attribute.BORDER_RIGHT, StyleValueConstants.INDEX_NAV_LINK_SEPARATOR_BORDER_STYLE));
        
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeClass(StyleClassConstants.DISABLED_LINK))
            .addAttribute(CSS.Attribute.COLOR, StyleValueConstants.DISABLED_LINK_COLOR));
        
        ////
        /// Options box
        //
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeID(ElementID.OPTIONS_BOX))
            .addAttribute(CSS.Attribute.DISPLAY, StyleValueConstants.NONE));
        
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeClass(StyleClassConstants.OPTIONS_HEADER))
            .addAttribute(CSS.Attribute.BACKGROUND_COLOR, StyleValueConstants.OPENQUARK_WEBSITE_LIGHT_BLUE_HEADER_BACKGROUND)
            .addAttribute(CSS.Attribute.COLOR, StyleValueConstants.OPENQUARK_BLUE)
            .addAttribute(CSS.Attribute.PADDING, StyleValueConstants.SCOPE_DISPLAY_SETTINGS_PADDING));
        
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeClass(StyleClassConstants.OPTIONS_BODY))
            .addAttribute(CSS.Attribute.BACKGROUND_COLOR, StyleValueConstants.OPTIONS_BODY_BACKGROUND)
            .addAttribute(CSS.Attribute.COLOR, StyleValueConstants.OPENQUARK_BLUE)
            .addAttribute(CSS.Attribute.PADDING, StyleValueConstants.SCOPE_DISPLAY_SETTINGS_PADDING));
        
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeClass(StyleClassConstants.OPTIONS_CHOICE))
            .addAttribute(CSS.Attribute.PADDING_LEFT, StyleValueConstants.ONE_EM_WIDTH));
        
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeClass(StyleClassConstants.FAKE_LINK))
            .addAttribute(CSS2Attribute.CURSOR, StyleValueConstants.POINTER)
            .addAttribute(CSS.Attribute.TEXT_DECORATION, StyleValueConstants.UNDERLINE));
        
        ////
        /// Navigation bars
        //
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeClass(StyleClassConstants.NAV_BAR))
            .addAttribute(CSS.Attribute.FONT_SIZE, StyleValueConstants.LARGER));
        
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeDescendant(CSSBuilder.Selector.makeClass(StyleClassConstants.NAV_BAR), CSSBuilder.Selector.makeType(HTML.Tag.DIV)))
            .addAttribute(CSS.Attribute.PADDING, StyleValueConstants.NAV_BAR_PADDING));
        
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeClass(StyleClassConstants.NAV_BAR_GLOBAL_ROW))
            .addAttribute(CSS.Attribute.BACKGROUND_COLOR, StyleValueConstants.OPENQUARK_WEBSITE_LIGHT_BLUE_HEADER_BACKGROUND)
            .addAttribute(CSS.Attribute.COLOR, StyleValueConstants.OPENQUARK_BLUE));
        
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(
            CSSBuilder.Selector.makeDescendant(CSSBuilder.Selector.makeClass(StyleClassConstants.NAV_BAR_GLOBAL_ROW), StyleClassConstants.A_LINK_PSEUDOCLASS_SELECTOR),
            CSSBuilder.Selector.makeDescendant(CSSBuilder.Selector.makeClass(StyleClassConstants.NAV_BAR_GLOBAL_ROW), StyleClassConstants.A_VISITED_PSEUDOCLASS_SELECTOR))
            .addAttribute(CSS.Attribute.BACKGROUND_COLOR, StyleValueConstants.OPENQUARK_WEBSITE_LIGHT_BLUE_HEADER_BACKGROUND)
            .addAttribute(CSS.Attribute.COLOR, StyleValueConstants.OPENQUARK_BLUE));
        
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeDescendant(CSSBuilder.Selector.makeClass(StyleClassConstants.NAV_BAR_GLOBAL_ROW), StyleClassConstants.A_HOVER_PSEUDOCLASS_SELECTOR))
            .addAttribute(CSS.Attribute.BACKGROUND_COLOR, StyleValueConstants.OPENQUARK_WEBSITE_KHAKI)
            .addAttribute(CSS.Attribute.COLOR, StyleValueConstants.OPENQUARK_BLUE));
        
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeClass(StyleClassConstants.NAV_BAR_LOCAL_ROW))
            .addAttribute(CSS.Attribute.BACKGROUND_COLOR, StyleValueConstants.OPENQUARK_WEBSITE_LIGHT_BLUE_CONTENT_BACKGROUND));
        
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeDescendant(CSSBuilder.Selector.makeClass(StyleClassConstants.NAV_BAR_LOCAL_ROW), StyleClassConstants.A_HOVER_PSEUDOCLASS_SELECTOR))
            .addAttribute(CSS.Attribute.BACKGROUND_COLOR, StyleValueConstants.OPENQUARK_WEBSITE_KHAKI));
        
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeClass(StyleClassConstants.NAV_BAR_HEADER_FOOTER))
            .addAttribute(CSS.Attribute.TEXT_ALIGN, StyleValueConstants.RIGHT_ALIGN)
            .addAttribute(CSS.Attribute.FONT_WEIGHT, StyleValueConstants.BOLD));
        
        ////
        /// Tooltip fragments
        //
        cssBuilder.addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeClass(StyleClassConstants.TOOLTIP_HEADER))
            .addAttribute(CSS.Attribute.FONT_STYLE, StyleValueConstants.ITALIC));
        
        return cssBuilder.toCSS();
    }
    
    /**
     * Constructs the contents for an HTML file that is to comply with HTML 4.01 Transitional.
     * @param htmlBuilder the builder containing the body of the HTML file (without the doctype).
     * @return the contents of the file, with the doctype prepended.
     */
    private static String getHTMLFileContentsWithDocTypeForMainFramePage(HTMLBuilder htmlBuilder) {
        return DocTypeDecl.HTML_4_01_TRANSITIONAL + htmlBuilder.toHTML();
    }
    
    /////====================================================================================================
    ////
    /// Generation of auxiliary pages (main frameset, module list, overview, navigational indices)
    //
    
    /**
     * Constructs the contents for the main documentation HTML file, i.e. the frameset.
     * @return the contents of the file.
     */
    private String getMainPageHTML() {
        startNewCurrentPage();
        
        // Create the scope display javascript
        String javascript =
            "\n" +
            "var scopeDisplay = 'publicOnly';\n" +
            "function setPublicOnlyScope() { scopeDisplay = 'publicOnly'; updateFrames(); }\n" +
            "function isPublicOnlyScope() { return scopeDisplay == 'publicOnly'; }\n" +
            "function setDisplayAllScopes() { scopeDisplay = 'all'; updateFrames(); }\n" +
            "function updateFrames() { for (var i = 0; i < frames.length; i++) { var f = frames[i]; if (f.updateScopeDisplaySettings) { f.updateScopeDisplaySettings() } } }";
        
        currentPage.openTag(HTML.Tag.HTML);
        String relativePathToBaseDirectory = ""; // the main page is in the base directory
        generateHeadSection(config.windowTitle, relativePathToBaseDirectory, null, javascript);
        
        currentPage
            .openTag(HTML.Tag.FRAMESET, idAttribute(ElementID.OUTER_FRAMESET).concat(HTMLBuilder.AttributeList.make(HTML.Attribute.COLS, "250, 100%, 0%")))
            .openTag(HTML.Tag.FRAMESET, HTMLBuilder.AttributeList.make(HTML.Attribute.ROWS, "45%, 55%"))
            .emptyTag(HTML.Tag.FRAME, HTMLBuilder.AttributeList.make(HTML.Attribute.NAME, MODULE_LIST_FRAME_NAME, HTML.Attribute.SRC, MODULE_LIST_FILENAME, HTML.Attribute.SCROLLING, "yes"))
            .emptyTag(HTML.Tag.FRAME, HTMLBuilder.AttributeList.make(HTML.Attribute.NAME, NAV_FRAME_NAME, HTML.Attribute.SCROLLING, "yes"))
            .closeTag(HTML.Tag.FRAMESET)
            .emptyTag(HTML.Tag.FRAME, HTMLBuilder.AttributeList.make(HTML.Attribute.NAME, MAIN_FRAME_NAME, HTML.Attribute.SRC, OVERVIEW_PAGE_FILENAME, HTML.Attribute.SCROLLING, "yes"))
            .emptyTag(HTML.Tag.FRAME, HTMLBuilder.AttributeList.make(HTML.Attribute.NAME, SEARCH_FRAME_NAME, HTML.Attribute.SCROLLING, "yes"))
            .closeTag(HTML.Tag.FRAMESET)
            .closeTag(HTML.Tag.HTML);
        
        return DocTypeDecl.HTML_4_01_FRAMESET + currentPage.toHTML();
    }
    
    /**
     * Returns a representation of the modules arranged in a hierarchy.
     * @param moduleNames the names of the modules.
     * @return a representation of the modules arranged in a hierarchy.
     */
    private ModuleHierarchyInfo getModuleHierarchy(final Set<ModuleName> moduleNames) {
        final ModuleHierarchyInfo root = new ModuleHierarchyInfo();
        
        for (final ModuleName moduleName : moduleNames) {
            final int nModuleNameComponents = moduleName.getNComponents();
            
            ModuleHierarchyInfo parent = root;
            for (int i = 0; i < nModuleNameComponents; i++) {
                final ModuleName prefix = moduleName.getPrefix(i+1); // goes from 1 to nComponents
                final boolean isActualModule = (i == nModuleNameComponents - 1);
                final ModuleHierarchyInfo child = parent.makeChild(prefix, isActualModule);
                
                // for next iteration:
                parent = child;
            }
        }
        
        return root;
    }
    
    /**
     * Returns a representation of the modules arranged into groups, categorized by their
     * immediate prefixes.
     * @param moduleNames the names of the modules.
     * @return a representation of the modules arranged into groups.
     */
    private ModuleHierarchyInfo getFlatModuleGrouping(final Set<ModuleName> moduleNames) {
        final ModuleHierarchyInfo root = new ModuleHierarchyInfo();
        
        for (final ModuleName moduleName : moduleNames) {
            final ModuleName immediatePrefix = moduleName.getImmediatePrefix();
            
            if (immediatePrefix == null) {
                root.makeChild(moduleName, true);
            } else {
                final ModuleHierarchyInfo parent = root.makeChild(immediatePrefix, false);
                parent.makeChild(moduleName, true);
            }
        }
        
        return root;
    }
    
    /**
     * Constructs the contents for the module list HTML file.
     * @return the contents of the file.
     */
    private String getModuleListPageHTML() {
        startNewCurrentPage();
        
        currentPage.openTag(HTML.Tag.HTML);
        
        String highlightPublicCSS =
            new CSSBuilder()
                .addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeClass(StyleClassConstants.NON_PUBLIC_SCOPE))
                    .addAttribute(CSS.Attribute.COLOR, StyleValueConstants.OPTIONS_BODY_BACKGROUND))
                .addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeClass(StyleClassConstants.PUBLIC_SCOPE))
                    .addAttribute(CSS.Attribute.COLOR, StyleValueConstants.OPENQUARK_RED))
                .toCSS();
        
        String highlightNonPublicCSS =
            new CSSBuilder()
                .addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeClass(StyleClassConstants.NON_PUBLIC_SCOPE))
                    .addAttribute(CSS.Attribute.COLOR, StyleValueConstants.OPENQUARK_RED))
                .addRuleSet(new CSSBuilder.RuleSet(CSSBuilder.Selector.makeClass(StyleClassConstants.PUBLIC_SCOPE))
                    .addAttribute(CSS.Attribute.COLOR, StyleValueConstants.OPTIONS_BODY_BACKGROUND))
                .toCSS();
        
        LinkedHashMap<String, String> cssMap = new LinkedHashMap<String, String>();
        cssMap.put(ElementID.HIGHLIGHT_PUBLIC_STYLE, highlightPublicCSS);
        cssMap.put(ElementID.HIGHLIGHT_NONPUBLIC_STYLE, highlightNonPublicCSS);
        
        String relativePathToBaseDirectory = ""; // the module list page is in the base directory
        generateHeadSection(
            makePageTitle(LocalizableUserVisibleString.MODULES.toResourceString()),
            relativePathToBaseDirectory,
            MAIN_FRAME_NAME,
            null,
            cssMap);
        
        currentPage.openTag(HTML.Tag.BODY, classAttribute(StyleClassConstants.WITH_MAIN_CONTENT).concat(HTMLBuilder.AttributeList.make(HTML4.ONLOAD_ATTRIBUTE, "setInitialModuleListDisplayMode()")));
        
        /// Set up scope display options box's javascript support
        //
        currentPage
            .addTaggedText(HTML.Tag.SCRIPT, HTMLBuilder.AttributeList.make(HTML.Attribute.TYPE, "text/javascript"),
                "\n" +
                "var pubStyleElement = document.styleSheets[0]; //document.getElementById('" + ElementID.HIGHLIGHT_PUBLIC_STYLE + "');\n" +
                "var nonPubStyleElement = document.styleSheets[1]; //document.getElementById('" + ElementID.HIGHLIGHT_NONPUBLIC_STYLE + "');\n" +
                "\n" +
                "function getCssRule(styleElem, index) {\n" +
                "    if (styleElem.cssRules) {\n" +
                "        return styleElem.cssRules[index];\n" +
                "    } else if (styleElem.rules) {\n" +
                "        return styleElem.rules[index];\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "var allRules = new Array(\n" + 
                "  new Array(\n" + 
                "    getCssRule(pubStyleElement, 0).style.cssText,\n" + 
                "    getCssRule(pubStyleElement, 1).style.cssText),\n" + 
                "  new Array(\n" + 
                "    getCssRule(nonPubStyleElement, 0).style.cssText,\n" + 
                "    getCssRule(nonPubStyleElement, 1).style.cssText));\n" + 
                "\n" +
                "var safari = (('' + navigator.vendor).indexOf('Apple') >= 0);\n" +
                "\n" +
                "function changeStyle(styleElem, rules, disabled) {\n" +
                "    if (safari) {\n" +
                "        for (var i = 0; i < 2; i++) {\n" +
                "            getCssRule(styleElem, i).style.cssText = disabled ? '' : rules[i];\n" +
                "        }\n" +
                "    } else {\n" +
                "        styleElem.disabled = disabled;\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "function updateScopeDisplaySettings() {\n" +
                "    if (pubStyleElement && nonPubStyleElement) {\n" +
                "        if (parent && parent.isPublicOnlyScope) {\n" +
                "            changeStyle(pubStyleElement, allRules[0], !parent.isPublicOnlyScope());\n" +
                "            changeStyle(nonPubStyleElement, allRules[1], parent.isPublicOnlyScope());\n" +
                "        } else {\n" +
                "            changeStyle(pubStyleElement, allRules[0], true);\n" +
                "            changeStyle(nonPubStyleElement, allRules[1], false);\n" +
                "        }\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "updateScopeDisplaySettings(); // run it immediately\n");
        
        currentPage
            .addTaggedText(HTML.Tag.SCRIPT, HTMLBuilder.AttributeList.make(HTML.Attribute.TYPE, "text/javascript"),
                "\n" +
                "function toggleOptionsBox() {\n" +
                "    var optionsBox = document.getElementById('" + ElementID.OPTIONS_BOX + "');\n" +
                "    if (optionsBox && optionsBox.style) {\n" +
                "        if (optionsBox.style.display != 'block') {\n" + // by default the box is hidden, so the first toggle operation should show it
                "            optionsBox.style.display = 'block';\n" +
                "        } else {\n" +
                "            optionsBox.style.display = 'none';\n" +
                "        }\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "function setPublicOnlyScope() {\n" +
                "    if (parent && parent.setPublicOnlyScope) {\n" + 
                "        parent.setPublicOnlyScope();\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "function setDisplayAllScopes() {\n" +
                "    if (parent && parent.setDisplayAllScopes) {\n" + 
                "        parent.setDisplayAllScopes();\n" +
                "    }\n" +
                "}\n");
        
        generateModuleListDisplayModeJavascript(0, "moduleList");
    
        /// Add the main links
        //
        currentPage
            .addTaggedText(HTML.Tag.H2, classAttribute(StyleClassConstants.MODULE_LIST_PAGE_TITLE), config.docTitle)
            .openTag(HTML.Tag.DIV, classAttribute(StyleClassConstants.MODULE_LIST_NAV_BAR))
            .addTaggedText(HTML.Tag.A, classAttribute(StyleClassConstants.IMPORTANT_LINK).concat(nonLocalHrefAttribute(OVERVIEW_PAGE_FILENAME)), LocalizableUserVisibleString.OVERVIEW.toResourceString())
            .addText(LocalizableUserVisibleString.NAV_BAR_ITEM_SEPARATOR.toResourceString())
            .addTaggedText(HTML.Tag.A, classAttribute(StyleClassConstants.IMPORTANT_LINK).concat(nonLocalHrefAttribute(MASTER_SCOPED_ENTITY_SEARCH_PAGE_FILENAME)).concat(HTMLBuilder.AttributeList.make(HTML.Attribute.TARGET, SEARCH_FRAME_NAME)), LocalizableUserVisibleString.SEARCH.toResourceString())
            .addText(LocalizableUserVisibleString.NAV_BAR_ITEM_SEPARATOR.toResourceString())
            .openTag(HTML.Tag.SPAN, classAttribute(StyleClassConstants.IMPORTANT_LINK))
            .addTaggedText(HTML.Tag.A, classAttribute(StyleClassConstants.FAKE_LINK).concat(HTMLBuilder.AttributeList.make(HTML4.ONCLICK_ATTRIBUTE, "toggleOptionsBox();")).concat(HTMLBuilder.AttributeList.make(HTML.Attribute.TARGET, SEARCH_FRAME_NAME)), LocalizableUserVisibleString.OPTIONS.toResourceString())
            .closeTag(HTML.Tag.SPAN)
            .closeTag(HTML.Tag.DIV);
        
        /// Add scope display controls
        //
        currentPage
            .openTag(HTML.Tag.DIV, idAttribute(ElementID.OPTIONS_BOX))
            .openTag(HTML.Tag.DIV, classAttribute(StyleClassConstants.MAIN_CONTENT))
            .addTaggedText(HTML.Tag.DIV, classAttribute(StyleClassConstants.OPTIONS_HEADER), LocalizableUserVisibleString.OPTIONS.toResourceString())
            .openTag(HTML.Tag.DIV, classAttribute(StyleClassConstants.OPTIONS_BODY))
            .addText(LocalizableUserVisibleString.SCOPE_FILTER_COLON.toResourceString())
            .openTag(HTML.Tag.DIV, classAttribute(StyleClassConstants.OPTIONS_CHOICE))
            .addTaggedText(HTML.Tag.SPAN, classAttribute(StyleClassConstants.PUBLIC_SCOPE), "&bull; ")
            .addTaggedText(HTML.Tag.A, classAttribute(StyleClassConstants.FAKE_LINK).concat(HTMLBuilder.AttributeList.make(HTML4.ONCLICK_ATTRIBUTE, "setPublicOnlyScope();")), LocalizableUserVisibleString.PUBLIC_ITEMS_ONLY.toResourceString())
            .closeTag(HTML.Tag.DIV)
            .openTag(HTML.Tag.DIV, classAttribute(StyleClassConstants.OPTIONS_CHOICE))
            .addTaggedText(HTML.Tag.SPAN, classAttribute(StyleClassConstants.NON_PUBLIC_SCOPE), "&bull; ")
            .addTaggedText(HTML.Tag.A, classAttribute(StyleClassConstants.FAKE_LINK).concat(HTMLBuilder.AttributeList.make(HTML4.ONCLICK_ATTRIBUTE, "setDisplayAllScopes();")), LocalizableUserVisibleString.SHOW_ALL_ITEMS.toResourceString())
            .closeTag(HTML.Tag.DIV)
            .closeTag(HTML.Tag.DIV)
            .closeTag(HTML.Tag.DIV)
            .closeTag(HTML.Tag.DIV);
        
        /// Generate a link for each module documented.
        /// The names of the documented modules form the keys in the moduleIndices map.
        //
        
        generateDisplayModeToolbars();
    
        currentPage.openTag(HTML.Tag.DIV, classAttribute(StyleClassConstants.MAIN_CONTENT));
        
        currentPage.openTag(HTML.Tag.DIV, idAttribute(ElementID.MODULE_LIST_FLAT));
        generateFlatModuleList();
        currentPage.closeTag(HTML.Tag.DIV);
        
        currentPage.openTag(HTML.Tag.DIV, idAttribute(ElementID.MODULE_LIST_GROUPED));
        final List<String> groupedNodeElementIDs = generateGroupedModuleList(getFlatModuleGrouping(moduleIndices.keySet()), false, null);
        currentPage.closeTag(HTML.Tag.DIV);
        
        currentPage.openTag(HTML.Tag.DIV, idAttribute(ElementID.MODULE_LIST_HIERARCHICAL));
        final List<String> hierarchicalNodeElementIDs = generateHierarchicalModuleList(getModuleHierarchy(moduleIndices.keySet()), 0, null);
        currentPage.closeTag(HTML.Tag.DIV);
        
        currentPage.closeTag(HTML.Tag.DIV);
        
        generateExpandCollapseAllJavascript(groupedNodeElementIDs, hierarchicalNodeElementIDs);
        
        currentPage.closeTag(HTML.Tag.BODY).closeTag(HTML.Tag.HTML);
        
        return DocTypeDecl.HTML_4_01_TRANSITIONAL + currentPage.toHTML();
    }

    /**
     * Generates the display mode toolbar and the sub-toolbars for expanding and collapsing trees. 
     */
    private void generateDisplayModeToolbars() {
        
        // main toolbar
        currentPage
            .openTag(HTML.Tag.DIV, classAttribute(StyleClassConstants.TOOLBAR))
            .addTaggedText(HTML.Tag.B, LocalizableUserVisibleString.VIEW_COLON.toResourceString())
            .addText(" ")
            .addTaggedText(HTML.Tag.A, idAttribute(ElementID.DISPLAY_MODE_BUTTON_MODULE_LIST_FLAT).concat(HTMLBuilder.AttributeList.make(HTML4.ONCLICK_ATTRIBUTE, "setModuleListDisplayMode(0);")), LocalizableUserVisibleString.FLAT.toResourceString())
            .addText("&nbsp;")
            .addTaggedText(HTML.Tag.A, idAttribute(ElementID.DISPLAY_MODE_BUTTON_MODULE_LIST_GROUPED).concat(HTMLBuilder.AttributeList.make(HTML4.ONCLICK_ATTRIBUTE, "setModuleListDisplayMode(1);")), LocalizableUserVisibleString.GROUPED.toResourceString())
            .addText("&nbsp;")
            .addTaggedText(HTML.Tag.A, idAttribute(ElementID.DISPLAY_MODE_BUTTON_MODULE_LIST_HIERARCHICAL).concat(HTMLBuilder.AttributeList.make(HTML4.ONCLICK_ATTRIBUTE, "setModuleListDisplayMode(2);")), LocalizableUserVisibleString.HIERARCHICAL.toResourceString())
            .closeTag(HTML.Tag.DIV);
        
        // flat tree toolbar (empty)
        currentPage.addTaggedText(HTML.Tag.DIV, idAttribute(ElementID.SUB_TOOLBAR_MODULE_LIST_FLAT), "");
        
        // grouped tree toolbar
        final String callToExpandAllGroupedNodes = "expandAllGroupedNodes();";
        final String callToCollapseAllGroupedNodes = "collapseAllGroupedNodes();";
        generateExpandCollapseAllSubToolbar(ElementID.SUB_TOOLBAR_MODULE_LIST_GROUPED, callToExpandAllGroupedNodes, callToCollapseAllGroupedNodes);
        
        // hierarchical tree toolbar
        final String callToExpandAllHierarchicalNodes = "expandAllHierarchicalNodes();";
        final String callToCollapseAllHierarchicalNodes = "collapseAllHierarchicalNodes();";
        generateExpandCollapseAllSubToolbar(ElementID.SUB_TOOLBAR_MODULE_LIST_HIERARCHICAL, callToExpandAllHierarchicalNodes, callToCollapseAllHierarchicalNodes);
    }

    /**
     * Generates a sub-toolbar containing an "expand all" and a "collapse all" button.
     * @param subToolbarElementID the element ID for the toolbar.
     * @param callToExpandAll the javascript call to the expand all function.
     * @param callToCollapseAll the javascript call to the collapse all function.
     */
    private void generateExpandCollapseAllSubToolbar(final String subToolbarElementID, final String callToExpandAll, final String callToCollapseAll) {
        currentPage
            .openTag(HTML.Tag.DIV, idAndClassAttributes(subToolbarElementID, StyleClassConstants.SUB_TOOLBAR))
            .addTaggedText(HTML.Tag.SPAN, classAttribute(StyleClassConstants.TREE_NODE_TOGGLE).concat(HTMLBuilder.AttributeList.make(HTML4.ONCLICK_ATTRIBUTE, callToExpandAll)), LocalizableUserVisibleString.EXPAND_BUTTON_LABEL.toResourceString())
            .addText("&nbsp;")
            .addText(LocalizableUserVisibleString.EXPAND_ALL.toResourceString().replaceAll(" ", "&nbsp;"))
            .addText(" ")
            .addTaggedText(HTML.Tag.SPAN, classAttribute(StyleClassConstants.TREE_NODE_TOGGLE).concat(HTMLBuilder.AttributeList.make(HTML4.ONCLICK_ATTRIBUTE, callToCollapseAll)), LocalizableUserVisibleString.COLLAPSE_BUTTON_LABEL.toResourceString())
            .addText("&nbsp;")
            .addText(LocalizableUserVisibleString.COLLAPSE_ALL.toResourceString().replaceAll(" ", "&nbsp;"))
            .closeTag(HTML.Tag.DIV);
    }

    /**
     * Generates the javascript for switching the display mode of the module list and the overview page.
     * @param initialDisplayMode the initial display mode to use (if there is no persisted user preference).
     * @param cookiePrefix the prefix to use for the cookie for storing the user preference.
     */
    private void generateModuleListDisplayModeJavascript(int initialDisplayMode, String cookiePrefix) {
        currentPage
            .addTaggedText(HTML.Tag.SCRIPT, HTMLBuilder.AttributeList.make(HTML.Attribute.TYPE, "text/javascript"),
                "\n" +
                "function setModuleListDisplayMode(mode) {\n" +
                "    var moduleLists = new Array(\n" +
                "        document.getElementById('" + ElementID.MODULE_LIST_FLAT + "'),\n" +
                "        document.getElementById('" + ElementID.MODULE_LIST_GROUPED + "'),\n" +
                "        document.getElementById('" + ElementID.MODULE_LIST_HIERARCHICAL + "')\n" +
                "    );\n" +
                "    var subToolbars = new Array(\n" +
                "        document.getElementById('" + ElementID.SUB_TOOLBAR_MODULE_LIST_FLAT + "'),\n" +
                "        document.getElementById('" + ElementID.SUB_TOOLBAR_MODULE_LIST_GROUPED + "'),\n" +
                "        document.getElementById('" + ElementID.SUB_TOOLBAR_MODULE_LIST_HIERARCHICAL + "')\n" +
                "    );\n" +
                "    var displayModeButtons = new Array(\n" +
                "        document.getElementById('" + ElementID.DISPLAY_MODE_BUTTON_MODULE_LIST_FLAT + "'),\n" +
                "        document.getElementById('" + ElementID.DISPLAY_MODE_BUTTON_MODULE_LIST_GROUPED + "'),\n" +
                "        document.getElementById('" + ElementID.DISPLAY_MODE_BUTTON_MODULE_LIST_HIERARCHICAL + "')\n" +
                "    );\n" +
                "    for (var i in moduleLists) {\n" +
                "        var moduleList = moduleLists[i];\n" +
                "        var displayModeButton = displayModeButtons[i];\n" +
                "        var subToolbar = subToolbars[i];\n" +
                "        if (i == mode) {\n" +
                "            moduleList.style.display = 'block';\n" +
                "            displayModeButton.className = '" + StyleClassConstants.BUTTON_SELECTED + "';\n" +
                "            subToolbar.style.display = 'block';\n" +
                "        } else {\n" +
                "            moduleList.style.display = 'none';\n" +
                "            displayModeButton.className = '" + StyleClassConstants.BUTTON_NOT_SELECTED + "';\n" +
                "            subToolbar.style.display = 'none';\n" +
                "        }\n" +
                "    }\n" +
                "    // set the cookie\n" +
                "    var cookie = getCookieName() + '=' + mode + '; expires=' + (new Date(new Date().getTime() + 365*24*3600*1000).toUTCString()) + '; path=/';\n" +
                "    document.cookie = cookie;\n" +
                "}\n" +
                "function getCookieName() {\n" +
                "    return 'org.openquark.cal.caldoc." + cookiePrefix + ".displayMode';\n" +
                "}\n" +
                "function setInitialModuleListDisplayMode() {\n" +
                "    var cookies = document.cookie.split(/;\\s*/);\n" +
                "    var displayMode = " + initialDisplayMode + ";\n" +
                "    for (var i in cookies) {\n" +
                "        var cookie = cookies[i];\n" +
                "        if (cookie.indexOf(getCookieName() + '=') == 0) {\n" +
                "            displayMode = cookie.substr(getCookieName().length + 1);\n" +
                "        }\n" +
                "    }\n" +
                "    setModuleListDisplayMode(displayMode);\n" +
                "}\n" +
                "function setBlockDisplayMode(elementID, mode) {\n" +
                "    var isShown = false;\n" +
                "    var element = document.getElementById(elementID);\n" +
                "    if (element && element.style) {\n" +
                "        element.style.display = mode;\n" +
                "        isShown = (mode == 'block');\n" +
                "    }\n" +
                "    updateToggle(elementID, isShown);\n" +
                "}\n" +
                "function toggleBlockDisplayMode(elementID) {\n" +
                "    var isShown = false;\n" +
                "    var element = document.getElementById(elementID);\n" +
                "    if (element && element.style) {\n" +
                "        if (element.style.display != 'none') {\n" + // we assume that by default the element is displayed, so the first toggle operation should hide it
                "            element.style.display = 'none';\n" +
                "        } else {\n" +
                "            element.style.display = 'block';\n" +
                "            isShown = true;\n" +
                "        }\n" +
                "    }\n" +
                "    updateToggle(elementID, isShown);\n" +
                "}\n" +
                "function updateToggle(elementID, isShown) {\n" +
                "    var toggle = document.getElementById('tog:' + elementID);\n" +
                "    if (toggle) {\n" +
                "        toggle.innerHTML = isShown ? '" + LocalizableUserVisibleString.COLLAPSE_BUTTON_LABEL.toResourceString() + "' : '" + LocalizableUserVisibleString.EXPAND_BUTTON_LABEL.toResourceString() + "';\n" +
                "    }\n" +
                "}\n");
    }
    
    /**
     * Generates the javascript for the "expand all" and "collapse all" functionality.
     * @param groupedNodeElementIDs a list of the element IDs for the nodes in the "grouped" display.
     * @param hierarchicalNodeElementIDs a list of the element IDs for the nodes in the "hierarchical" display.
     */
    private void generateExpandCollapseAllJavascript(final List<String> groupedNodeElementIDs, final List<String> hierarchicalNodeElementIDs) {
        currentPage
            .addTaggedText(HTML.Tag.SCRIPT, HTMLBuilder.AttributeList.make(HTML.Attribute.TYPE, "text/javascript"),
                "\n" +
                "var groupedNodeElementIDs = " + getNewStringArrayJavascript(groupedNodeElementIDs) + ";\n" +
                "var hierarchicalNodeElementIDs = " + getNewStringArrayJavascript(hierarchicalNodeElementIDs) + ";\n" +
                "function expandAllGroupedNodes() {\n" +
                "    for (var i in groupedNodeElementIDs) {\n" +
                "        var groupedNodeElementID = groupedNodeElementIDs[i];\n" +
                "        setBlockDisplayMode(groupedNodeElementID, 'block');\n" +
                "    }\n" +
                "}\n" +
                "function collapseAllGroupedNodes() {\n" +
                "    for (var i in groupedNodeElementIDs) {\n" +
                "        var groupedNodeElementID = groupedNodeElementIDs[i];\n" +
                "        setBlockDisplayMode(groupedNodeElementID, 'none');\n" +
                "    }\n" +
                "}\n" +
                "function expandAllHierarchicalNodes() {\n" +
                "    for (var i in hierarchicalNodeElementIDs) {\n" +
                "        var hierarchicalNodeElementID = hierarchicalNodeElementIDs[i];\n" +
                "        setBlockDisplayMode(hierarchicalNodeElementID, 'block');\n" +
                "    }\n" +
                "}\n" +
                "function collapseAllHierarchicalNodes() {\n" +
                "    for (var i in hierarchicalNodeElementIDs) {\n" +
                "        var hierarchicalNodeElementID = hierarchicalNodeElementIDs[i];\n" +
                "        setBlockDisplayMode(hierarchicalNodeElementID, 'none');\n" +
                "    }\n" +
                "}\n");
    }
    
    /**
     * Returns a javascript expression for creating a new string array.
     * @param elements the elements to be put into the array.
     * @return the javascript expression.
     */
    private String getNewStringArrayJavascript(final List<String> elements) {
        StringBuilder buffer = new StringBuilder("new Array(\n");
        for (int i = 0, n = elements.size(); i < n; i++) {
            if (i > 0) {
                buffer.append(",\n");
            }
            buffer.append(" '").append(elements.get(i)).append("'");
        }
        buffer.append(")");
        return buffer.toString();
    }
    
    /**
     * Generates a hierarchical module list.
     * @param node the current node to process.
     * @param level the current level in the tree.
     * @param moduleNameOrNull the module name corresponding to the node, or null if this is the root of the tree.
     * @return a list of the element IDs for the DIVs generated (except the root).
     */
    private List<String> generateHierarchicalModuleList(final ModuleHierarchyInfo node, final int level, final ModuleName moduleNameOrNull) {
        // just return if there are no children to generate
        if (node.getChildren().isEmpty()) {
            return Collections.emptyList();
        }
        
        final String moduleTreeNodeElementID = getModuleTreeNodeElementID("mlH", moduleNameOrNull);
        
        final List<String> elementIDs = new ArrayList<String>();
        if (moduleNameOrNull != null) {
            // the current node is not the root, so add the elementID
            elementIDs.add(moduleTreeNodeElementID);
        }
        
        currentPage.openTag(HTML.Tag.DIV, idAndClassAttributes(moduleTreeNodeElementID, StyleClassConstants.TREE_DIV));
        
        for (final Map.Entry<Pair<ModuleName, Boolean>, ModuleHierarchyInfo> entry : node.getChildren().entrySet()) {
            final Pair<ModuleName, Boolean> key = entry.getKey();
            final ModuleName childName = key.fst();
            final boolean childIsActualModule = key.snd().booleanValue();
            final ModuleHierarchyInfo child = entry.getValue();
            final String childDisplayName = childName.getNthComponent(level);
            final String fullyQualifiedNameForChildModule = getFullyQualifiedNameForModule(childName);
            
            final String childDivElementID = getModuleTreeNodeElementID("mlH", childName);

            currentPage.openTag(HTML.Tag.DIV);

            generateModuleListTreeNodeToggle(childDivElementID, child.getChildren().isEmpty());
            
            if (childIsActualModule) {
                generateNonLocalReference(currentPage, MODULES_SUBDIRECTORY, childName, labelMaker.getModuleLabel(childName), childDisplayName, fullyQualifiedNameForChildModule);
            } else {
                currentPage.addText(childDisplayName);
            }
            
            currentPage.closeTag(HTML.Tag.DIV);
            
            final List<String> elementIDsFromChild = generateHierarchicalModuleList(child, level+1, childName);
            
            elementIDs.addAll(elementIDsFromChild);
        }
        
        currentPage.closeTag(HTML.Tag.DIV);
        
        return elementIDs;
    }

    /**
     * Generates a grouped module list.
     * @param node the current node to process.
     * @param isChildLevel whether this is the child level.
     * @param moduleNameOrNull the module name corresponding to the node, or null if this is the root of the tree.
     * @return a list of the element IDs for the DIVs generated (except the root).
     */
    private List<String> generateGroupedModuleList(final ModuleHierarchyInfo node, final boolean isChildLevel, final ModuleName moduleNameOrNull) {
        // just return if there are no children to generate
        if (node.getChildren().isEmpty()) {
            return Collections.emptyList();
        }
        
        final String moduleTreeNodeElementID = getModuleTreeNodeElementID("mlG", moduleNameOrNull);
        
        final List<String> elementIDs = new ArrayList<String>();
        if (moduleNameOrNull != null) {
            // the current node is not the root, so add the elementID
            elementIDs.add(moduleTreeNodeElementID);
        }
        
        currentPage.openTag(HTML.Tag.DIV, idAndClassAttributes(moduleTreeNodeElementID, StyleClassConstants.TREE_DIV));
        
        for (final Map.Entry<Pair<ModuleName, Boolean>, ModuleHierarchyInfo> entry : node.getChildren().entrySet()) {
            final Pair<ModuleName, Boolean> key = entry.getKey();
            final ModuleName childName = key.fst();
            final boolean childIsActualModule = key.snd().booleanValue();
            final ModuleHierarchyInfo child = entry.getValue();
            final String childDisplayName = isChildLevel ? childName.getLastComponent() : childName.toSourceText();
            final String fullyQualifiedNameForChildModule = getFullyQualifiedNameForModule(childName);
            
            final String childDivElementID = getModuleTreeNodeElementID("mlG", childName);

            currentPage.openTag(HTML.Tag.DIV);
            
            generateModuleListTreeNodeToggle(childDivElementID, child.getChildren().isEmpty());

            if (childIsActualModule) {
                generateNonLocalReference(currentPage, MODULES_SUBDIRECTORY, childName, labelMaker.getModuleLabel(childName), childDisplayName, fullyQualifiedNameForChildModule);
            } else {
                currentPage.addText(childDisplayName);
            }

            currentPage.closeTag(HTML.Tag.DIV);

            if (!isChildLevel) {
                final List<String> elementIDsFromChild = generateGroupedModuleList(child, true, childName);
                elementIDs.addAll(elementIDsFromChild);
            }
        }
        
        currentPage.closeTag(HTML.Tag.DIV);
        
        return elementIDs;
    }
    
    /**
     * Generate a grouped view of a list of modules that are related to the current module
     * (e.g. the imported modules, the friend modules, the directly and the indirectly
     * dependent modules).
     * @param node the root node of a grouped (two-level) tree.
     */
    private void generateGroupedListOfRelatedModules(final ModuleHierarchyInfo node) {
        
        SortedSet<ModuleName> rootLevelModules = new TreeSet<ModuleName>();
        
        for (final Map.Entry<Pair<ModuleName, Boolean>, ModuleHierarchyInfo> entry : node.getChildren().entrySet()) {
            final Pair<ModuleName, Boolean> key = entry.getKey();
            final ModuleName childName = key.fst();
            final ModuleHierarchyInfo child = entry.getValue();
            
            if (child.getChildren().isEmpty()) {
                rootLevelModules.add(childName);
            }
        }
        
        // first list out the modules with "namespaces" (i.e. their names have >1 components)
        for (final Map.Entry<Pair<ModuleName, Boolean>, ModuleHierarchyInfo> entry : node.getChildren().entrySet()) {
            final Pair<ModuleName, Boolean> key = entry.getKey();
            final ModuleName childName = key.fst();
            final ModuleHierarchyInfo child = entry.getValue();
            
            if (rootLevelModules.contains(childName)) {
                continue;
            }
            
            currentPage
                .openTag(HTML.Tag.DIV, classAttribute(StyleClassConstants.RELATED_MODULES_LIST))
                .addText(childName.toSourceText())
                .addText(LocalizableUserVisibleString.COLON.toResourceString())
                .addText(" ");
            
            int counter2 = 0;
            for (final Pair<ModuleName, Boolean> key2 : child.getChildren().keySet()) {
                final ModuleName childName2 = key2.fst();
                
                if (counter2 > 0) {
                    currentPage.addText(LocalizableUserVisibleString.COMMA_AND_SPACE.toResourceString());
                }

                generateModuleReference(currentPage, null, childName2, childName2.getLastComponent());
                
                counter2++;
            }
            
            currentPage.closeTag(HTML.Tag.DIV);
        }
        
        // now list out the modules under the "root namespace" (i.e. their names have just 1 component)
        int counter = 0;
        for (final ModuleName moduleName : rootLevelModules) {
            
            if (counter > 0) {
                currentPage.addText(LocalizableUserVisibleString.COMMA_AND_SPACE.toResourceString());
            }
            
            generateModuleReference(currentPage, null, moduleName, moduleName.toSourceText());
            counter++;
        }
    }
    
    /**
     * Generates an attribute header and a grouped view of a list of modules that are related to the current module
     * (e.g. the imported modules, the friend modules, the directly and the indirectly
     * dependent modules).
     * @param attributeHeader the attribute header.
     * @param moduleNames a set of ModuleNames.
     */
    private void generateRelatedModulesList(final String attributeHeader, final Set<ModuleName> moduleNames) {
        /// Generate the modules list only if there are some modules to list
        //
        if (moduleNames.size() > 0) {
            currentPage
                .openTag(HTML.Tag.DL)
                .addTaggedText(HTML.Tag.DT, classAttribute(StyleClassConstants.ATTRIBUTE_HEADER), attributeHeader)
                .openTag(HTML.Tag.DD);
            
            generateGroupedListOfRelatedModules(getFlatModuleGrouping(moduleNames));
            
            currentPage
                .closeTag(HTML.Tag.DD)
                .closeTag(HTML.Tag.DL);
        }
    }

    /**
     * Returns the element ID for a node in a module tree.
     * @param idPrefix the prefix to use for the ID.
     * @param moduleNameOrNull the module name corresponding to the node, or null if this is the root of the tree.
     * @return the element ID.
     */
    private String getModuleTreeNodeElementID(final String idPrefix, final ModuleName moduleNameOrNull) {
        if (moduleNameOrNull == null) {
            return idPrefix + ":root";
        } else {
            return idPrefix + ":" + moduleNameOrNull.toSourceText();
        }
    }

    /**
     * Generates a toggle (or a placeholder) for a node in a module tree.
     * @param childDivElementID the element ID for the children block of the node whose display is to be controlled by the toggle.
     * @param isPlaceholderOnly whether this is to be an inactive placeholder for a toggle.
     */
    private void generateModuleListTreeNodeToggle(final String childDivElementID, boolean isPlaceholderOnly) {

        final AttributeList attributes;
        final String toggleText;

        if (isPlaceholderOnly) {
            attributes = classAttribute(StyleClassConstants.TREE_NODE_TOGGLE_PLACEHOLDER);
            toggleText = "&nbsp;";

        } else {
            attributes = idAndClassAttributes("tog:" + childDivElementID, StyleClassConstants.TREE_NODE_TOGGLE).concat(HTMLBuilder.AttributeList.make(HTML4.ONCLICK_ATTRIBUTE, "toggleBlockDisplayMode(\"" + childDivElementID + "\");"));
            toggleText = LocalizableUserVisibleString.COLLAPSE_BUTTON_LABEL.toResourceString();
        }
        
        currentPage
            .addTaggedText(HTML.Tag.SPAN, attributes, toggleText)
            .addText("&nbsp;");
    }
    
    /**
     * Generates a flat module list.
     */
    private void generateFlatModuleList() {

        final SortedMap<String, ModuleName> displayNameToModuleNameMap = getAbbreviatedModuleNameToModuleNameMap();
        
        for (final Map.Entry<String, ModuleName> entry : displayNameToModuleNameMap.entrySet()) {
            final String displayName = entry.getKey();
            final ModuleName moduleName = entry.getValue();
            final String fullyQualifiedNameForModule = moduleName.toSourceText();
            
            generateNonLocalReference(currentPage, MODULES_SUBDIRECTORY, moduleName, labelMaker.getModuleLabel(moduleName), displayName, fullyQualifiedNameForModule);
            currentPage.emptyTag(HTML.Tag.BR).newline();
        }
    }

    /**
     * Constructs a map from abbreviated names for modules to fully-qualified ModuleNames.
     * @return the map.
     */
    private SortedMap<String, ModuleName> getAbbreviatedModuleNameToModuleNameMap() {
        final SortedMap<String, ModuleName> displayNameToModuleNameMap = new TreeMap<String, ModuleName>();
        
        for (final ModuleName moduleName : moduleIndices.keySet()) {
            final String lastComponent = moduleName.getLastComponent();
            final ModuleName minimallyQualifiedModuleName = getModuleNameResolverForDocumentedModules().getMinimallyQualifiedModuleName(moduleName);

            final String displayName;
            
            if (minimallyQualifiedModuleName.getNComponents() == 1) {
                displayName = lastComponent;
                
            } else {
                final int nMinusOne = minimallyQualifiedModuleName.getNComponents() - 1;

                final StringBuilder sb = new StringBuilder(lastComponent).append(" (");

                for (int i = 0; i < nMinusOne; i++) {
                    if (i > 0) {
                        sb.append(CALFragments.DOT);
                    }
                    sb.append(minimallyQualifiedModuleName.getNthComponent(i));
                }
                sb.append(")");
                displayName = sb.toString();
            }
            
            displayNameToModuleNameMap.put(displayName, moduleName);
        }
        return displayNameToModuleNameMap;
    }

    /**
     * Constructs the contents for the overview page HTML file.
     * @return the contents of the file.
     */
    private String getOverviewPageHTML() {
        startNewCurrentPage();
        
        currentPage.openTag(HTML.Tag.HTML);
        String relativePathToBaseDirectory = ""; // the welcome page is in the base directory
        generateHeadSection(makePageTitle(LocalizableUserVisibleString.OVERVIEW.toResourceString()), relativePathToBaseDirectory, null, null);

        /// Generate the Javascript for changing the nav frame.
        //
        currentPage
            .openTag(HTML.Tag.BODY, classAttribute(StyleClassConstants.WITH_MAIN_CONTENT).concat(HTMLBuilder.AttributeList.make(HTML4.ONLOAD_ATTRIBUTE, "initPage()")))
            .addTaggedText(HTML.Tag.SCRIPT, HTMLBuilder.AttributeList.make(HTML.Attribute.TYPE, "text/javascript"),
                "function initPage() {\n" +
                "   clearNav();\n" +
                "   setInitialModuleListDisplayMode();\n" +
                "}\n" +
                "function clearNav() {\n" +
                "   if (parent.frames != undefined && parent.frames[1] != undefined && parent.frames[1].location != undefined) {\n" + 
                "      parent.frames[1].location.replace('about:blank');\n" +
                "   }\n" +
                "}");

        generateModuleListDisplayModeJavascript(1, "overview");
        
        generateGenericNavBar(false, relativePathToBaseDirectory);
        
        /// Wrap the main content with a div tag
        //
        currentPage.openTag(HTML.Tag.DIV, classAttribute(StyleClassConstants.MAIN_CONTENT));
        
        /// Generate the page heading
        //
        currentPage
            .addTaggedText(HTML.Tag.H1, config.docTitle)
            .addTaggedText(HTML.Tag.H2, LocalizableUserVisibleString.OVERVIEW.toResourceString());
        
        ////
        /// For each module documented, generate a link to it and its short description.
        /// The names of the documented modules form the keys in the moduleIndices map.
        //

        /// Close the main content div tag
        //
        currentPage.closeTag(HTML.Tag.DIV);
        
        generateDisplayModeToolbars();
        
        /// Wrap the main content with a div tag
        //
        currentPage.openTag(HTML.Tag.DIV, classAttribute(StyleClassConstants.MAIN_CONTENT));
        
        currentPage.openTag(HTML.Tag.DIV, idAttribute(ElementID.MODULE_LIST_FLAT));
        generateFlatOverview();
        currentPage.closeTag(HTML.Tag.DIV);
        
        currentPage.openTag(HTML.Tag.DIV, idAttribute(ElementID.MODULE_LIST_GROUPED));
        final List<String> groupedNodeElementIDs = generateGroupedOverview(getFlatModuleGrouping(moduleIndices.keySet()), false, null);
        currentPage.closeTag(HTML.Tag.DIV);
        
        currentPage.openTag(HTML.Tag.DIV, idAttribute(ElementID.MODULE_LIST_HIERARCHICAL));
        final List<String> hierarchicalNodeElementIDs = generateHierarchicalOverview(getModuleHierarchy(moduleIndices.keySet()), 0, null);
        currentPage.closeTag(HTML.Tag.DIV);
        
        /// Close the main content div tag
        //
        currentPage.closeTag(HTML.Tag.DIV);
        
        generateGenericNavBar(true, "");
        generatePageBottom();
        
        generateExpandCollapseAllJavascript(groupedNodeElementIDs, hierarchicalNodeElementIDs);
        
        currentPage.closeTag(HTML.Tag.BODY).closeTag(HTML.Tag.HTML);
        
        return getHTMLFileContentsWithDocTypeForMainFramePage(currentPage);
    }

    /**
     * Generates the short description for a module.
     * @param moduleName the module to be described.
     */
    private void generateShortDescriptionForModule(final ModuleName moduleName) {
        ModuleTypeInfo moduleTypeInfo = programModelManager.getModuleTypeInfo(moduleName);
        CALFeatureMetadata metadata = getMetadata(CALFeatureName.getModuleFeatureName(moduleName), getLocale());
        CALDocComment docComment = moduleTypeInfo.getCALDocComment();

        generateShortDescription(metadata, docComment, new ReferenceGenerator(MODULES_SUBDIRECTORY));
    }
    
    /**
     * Generates a hierarchical view for the overview page.
     * @param node the current node to process.
     * @param level the current level in the tree.
     * @param moduleNameOrNull the module name corresponding to the node, or null if this is the root of the tree.
     * @return a list of the element IDs for the DIVs generated (except the root).
     */
    private List<String> generateHierarchicalOverview(final ModuleHierarchyInfo node, final int level, final ModuleName moduleNameOrNull) {
        // just return if there are no children to generate
        if (node.getChildren().isEmpty()) {
            return Collections.emptyList();
        }
        
        final String moduleTreeNodeElementID = getModuleTreeNodeElementID("ovwH", moduleNameOrNull);
        
        final List<String> elementIDs = new ArrayList<String>();
        if (moduleNameOrNull != null) {
            // the current node is not the root, so add the elementID
            elementIDs.add(moduleTreeNodeElementID);
        }
        
        currentPage.openTag(HTML.Tag.DIV, idAndClassAttributes(moduleTreeNodeElementID, StyleClassConstants.TREE_DIV_OVERVIEW_PAGE));
        
        for (final Map.Entry<Pair<ModuleName, Boolean>, ModuleHierarchyInfo> entry : node.getChildren().entrySet()) {
            final Pair<ModuleName, Boolean> key = entry.getKey();
            final ModuleName childName = key.fst();
            final boolean childIsActualModule = key.snd().booleanValue();
            final ModuleHierarchyInfo child = entry.getValue();
            final String childDisplayName = childName.getNthComponent(level);
            final String fullyQualifiedNameForChildModule = getFullyQualifiedNameForModule(childName);
            
            final String childDivElementID = getModuleTreeNodeElementID("ovwH", childName);

            currentPage.openTag(HTML.Tag.DIV);

            if (childIsActualModule) {
                currentPage.openTag(HTML.Tag.DL, classAttribute(StyleClassConstants.DL_OVERVIEW_PAGE)).openTag(HTML.Tag.DT);
                generateModuleListTreeNodeToggle(childDivElementID, child.getChildren().isEmpty());
                generateNonLocalReference(currentPage, MODULES_SUBDIRECTORY, childName, labelMaker.getModuleLabel(childName), childDisplayName, fullyQualifiedNameForChildModule);
                
                currentPage.closeTag(HTML.Tag.DT).openTag(HTML.Tag.DD);
                generateShortDescriptionForModule(childName);
                currentPage.closeTag(HTML.Tag.DD).closeTag(HTML.Tag.DL);
            } else {
                generateModuleListTreeNodeToggle(childDivElementID, child.getChildren().isEmpty());
                currentPage.addText(childDisplayName);
            }
            
            currentPage.closeTag(HTML.Tag.DIV);
            
            final List<String> elementIDsFromChild = generateHierarchicalOverview(child, level+1, childName);
            
            elementIDs.addAll(elementIDsFromChild);
        }
        
        currentPage.closeTag(HTML.Tag.DIV);
        
        return elementIDs;
    }

    /**
     * Generates a grouped view for the overview page.
     * @param node the current node to process.
     * @param isChildLevel whether this is the child level.
     * @param moduleNameOrNull the module name corresponding to the node, or null if this is the root of the tree.
     * @return a list of the element IDs for the DIVs generated (except the root).
     */
    private List<String> generateGroupedOverview(final ModuleHierarchyInfo node, final boolean isChildLevel, final ModuleName moduleNameOrNull) {
        // just return if there are no children to generate
        if (node.getChildren().isEmpty()) {
            return Collections.emptyList();
        }
        
        final String moduleTreeNodeElementID = getModuleTreeNodeElementID("ovwG", moduleNameOrNull);
        
        final List<String> elementIDs = new ArrayList<String>();
        if (moduleNameOrNull != null) {
            // the current node is not the root, so add the elementID
            elementIDs.add(moduleTreeNodeElementID);
        }
        
        currentPage.openTag(HTML.Tag.DIV, idAndClassAttributes(moduleTreeNodeElementID, StyleClassConstants.TREE_DIV_OVERVIEW_PAGE));
        
        for (final Map.Entry<Pair<ModuleName, Boolean>, ModuleHierarchyInfo> entry : node.getChildren().entrySet()) {
            final Pair<ModuleName, Boolean> key = entry.getKey();
            final ModuleName childName = key.fst();
            final boolean childIsActualModule = key.snd().booleanValue();
            final ModuleHierarchyInfo child = entry.getValue();
            final String childDisplayName = isChildLevel ? childName.getLastComponent() : childName.toSourceText();
            final String fullyQualifiedNameForChildModule = getFullyQualifiedNameForModule(childName);
            
            final String childDivElementID = getModuleTreeNodeElementID("ovwG", childName);
            
            currentPage.openTag(HTML.Tag.DIV);

            if (childIsActualModule) {
                currentPage.openTag(HTML.Tag.DL, classAttribute(StyleClassConstants.DL_OVERVIEW_PAGE)).openTag(HTML.Tag.DT);
                generateModuleListTreeNodeToggle(childDivElementID, child.getChildren().isEmpty());
                generateNonLocalReference(currentPage, MODULES_SUBDIRECTORY, childName, labelMaker.getModuleLabel(childName), childDisplayName, fullyQualifiedNameForChildModule);
                
                currentPage.closeTag(HTML.Tag.DT).openTag(HTML.Tag.DD);
                generateShortDescriptionForModule(childName);
                currentPage.closeTag(HTML.Tag.DD).closeTag(HTML.Tag.DL);
            } else {
                generateModuleListTreeNodeToggle(childDivElementID, child.getChildren().isEmpty());
                currentPage.addText(childDisplayName);
            }
            
            currentPage.closeTag(HTML.Tag.DIV);
            
            if (!isChildLevel) {
                final List<String> elementIDsFromChild = generateGroupedOverview(child, true, childName);
                elementIDs.addAll(elementIDsFromChild);
            }
        }
        
        currentPage.closeTag(HTML.Tag.DIV);
        
        return elementIDs;
    }
    
    /**
     * Generates a flat view for the overview page.
     */
    private void generateFlatOverview() {

        final SortedMap<String, ModuleName> displayNameToModuleNameMap = getAbbreviatedModuleNameToModuleNameMap();
        
        for (final Map.Entry<String, ModuleName> entry : displayNameToModuleNameMap.entrySet()) {
            final String displayName = entry.getKey();
            final ModuleName moduleName = entry.getValue();
            final String fullyQualifiedNameForModule = moduleName.toSourceText();
            
            currentPage.openTag(HTML.Tag.DL, classAttribute(StyleClassConstants.DL_OVERVIEW_PAGE)).openTag(HTML.Tag.DT);
            generateNonLocalReference(currentPage, MODULES_SUBDIRECTORY, moduleName, labelMaker.getModuleLabel(moduleName), displayName, fullyQualifiedNameForModule);
            
            currentPage.closeTag(HTML.Tag.DT).openTag(HTML.Tag.DD);
            generateShortDescriptionForModule(moduleName);
            currentPage.closeTag(HTML.Tag.DD).closeTag(HTML.Tag.DL);
        }
    }

    /**
     * Constructs the HTML contents of the master scoped entity search page.
     * @return the contents of the file.
     */
    private String getMasterScopedEntitySearchPageHTML() {
        startNewCurrentPage();
        
        currentPage.openTag(HTML.Tag.HTML);
        String relativePathToBaseDirectory = ""; // the master index page is in the base directory
        generateHeadSection(makePageTitle(LocalizableUserVisibleString.SEARCH.toResourceString()), relativePathToBaseDirectory, MAIN_FRAME_NAME, null);

        ////
        /// Build up the javascript that initializes the index data structures in the search page
        //
        StringBuilder jsMasterIndexSetup = new StringBuilder("var modules = new Array(\n");
        
        List<String> prefixes = new ArrayList<String>();
        
        boolean isFirstTime = true;
        for (final ModuleName moduleName : moduleIndices.keySet()) {
            
            if (!isFirstTime) {
                jsMasterIndexSetup.append(",\n");
            }
            
            isFirstTime = false;
            
            // For each module we generate 4 javascript arrays containing the index entries
            // for the 4 per-module indices. There arrays are assigned as fields to a javascript
            // object representing the module.
            
            List<IndexEntry> typeIndex = getPerModuleTypeIndex(moduleName);
            String jsTypeIndexSetup = getMasterScopedEntitySearchPageJavascriptForPerModuleIndex(typeIndex, prefixes);
            
            List<IndexEntry> typeClassIndex = getPerModuleTypeClassIndex(moduleName);
            String jsTypeClassIndexSetup = getMasterScopedEntitySearchPageJavascriptForPerModuleIndex(typeClassIndex, prefixes);
            
            // make a copy of the functional agent index, so we can sort it in the order we desire, which is case-insensitive lexicographical order
            List<IndexEntry> functionalAgentIndex = new ArrayList<IndexEntry>(getPerModuleFunctionalAgentIndex(moduleName));
            // sort the list of index entries
            Collections.sort(functionalAgentIndex, new IndexEntryComparator());
            
            String jsFunctionalAgentIndexSetup = getMasterScopedEntitySearchPageJavascriptForPerModuleIndex(functionalAgentIndex, prefixes);
            
            jsMasterIndexSetup
                .append("\nmoduleEntry('")
                .append(moduleName)
                .append("',\n")
                .append(jsTypeIndexSetup)
                .append(",\n")
                .append(jsTypeClassIndexSetup)
                .append(",\n")
                .append(jsFunctionalAgentIndexSetup)
                .append(")");
        }
        
        jsMasterIndexSetup.append(");\n\n");
        
        String jsSortMasterIndexCall =
                "modules.sort(\n" +
                "    function (a, b) {\n" +
                "        if (a.moduleName < b.moduleName) {\n" +
                "            return -1;\n" +
                "        } else if (a.moduleName > b.moduleName) {\n" +
                "            return 1;\n" +
                "        } else {\n" +
                "            return 0;\n" +
                "        }\n" +
                "    });\n";
        
        jsMasterIndexSetup.append(jsSortMasterIndexCall);
        
        ////
        /// Now that we have looped through the index entries, we can produce the list of prefixes (which actually needs to
        /// come before the index data structures in the final javascript, because it is referenced by the pf() function).
        //
        StringBuilder jsPrefixesSetup = new StringBuilder();
        jsPrefixesSetup.append("var prefixes = new Array(");
        for (int i = 0, n = prefixes.size(); i < n; i++) {
            if (i > 0) {
                jsPrefixesSetup.append(",");
            }
            jsPrefixesSetup.append("'").append(prefixes.get(i)).append("'");
        }
        jsPrefixesSetup.append(");\n\n");
        
        ////
        /// Build up the javascript helper functions that implement the search functionality
        //
        String jsMakeModuleEntryFunction = "\n" +
                "function moduleEntry(moduleName, typeIndex, typeClassIndex, functionalAgentIndex) {\n" + 
                "    var entry = new Object();\n" + 
                "    entry.moduleName = moduleName;\n" +
                "    entry.indices = new Object();" + 
                "    entry.indices.typeIndex = typeIndex;\n" + 
                "    entry.indices.typeClassIndex = typeClassIndex;\n" + 
                "    entry.indices.functionalAgentIndex = functionalAgentIndex;\n" + 
                "    return entry;\n" + 
                "}\n";
        
        String jsMakePairFunction = "\n" +
                "function pair(name, label) {\n" +
                "    var item = new Object();\n" +
                "    item.name = name;\n" +
                "    item.label = label;\n" +
                "    return item;\n" +
                "}\n" +
                "\n" +
                "function item(scope, name, label) {\n" +
                "    var item = new Object();\n" +
                "    item.scope = scope;\n" +
                "    item.name = name;\n" +
                "    item.label = label;\n" +
                "    return item;\n" +
                "}\n";
        
        String jsMakePfFunction = "\n" +
                "function pf(scope, prefixIndex, name) {\n" +
                "    return item(scope, name, prefixes[prefixIndex] + name);\n" +
                "}\n";

        String jsFindKeyFunction = "\n" +
                "function findKey(key, caseSensitive, useRegexp, includeModuleIndex, indices) {\n" +
                "    var scopeClass = new Array('" + StyleClassConstants.PRIVATE_SCOPE.toHTML() + "', '" + StyleClassConstants.PROTECTED_SCOPE.toHTML() + "', '" + StyleClassConstants.PUBLIC_SCOPE.toHTML() + "');\n" +
                "    var origKey = key;\n" +
                "    key = key.replace(/\\s+$/, '');\n" +
                "    if (key.length == 0) { return ''; }\n" +
                "    if (!useRegexp) { key = key.replace(/([^a-zA-Z0-9-_])/g, '\\\\$1').replace(/\\\\\\*/g, '.*').replace(/\\\\\\?/g, '.'); }\n" +
                "    var re = new RegExp('(' + key + ')', caseSensitive ? '' : 'i');\n" +
                "    var finalResults = '';\n" +
                "    var numResults = 0;\n" +
                "    var privateScope = 0;\n" +
                "    var protectedScope = 1;\n" +
                "    var publicScope = 2;\n" +
                "    var numResultsPerScope = new Array(0, 0, 0);\n" +
                "    if (includeModuleIndex) {\n" +
                "        var moduleResults = '';\n" + 
                "        for (var i in modules) {\n" +
                "            var moduleName = modules[i].moduleName;\n" + 
                "            if (moduleName.match(re)) {\n" + 
                "                numResults++;\n" +
                "                numResultsPerScope[publicScope]++;\n" + 
                "                var highlightedEntry = moduleName.replace(re, '<b>$1<\\/b>');\n" + 
                "                moduleResults += \"&nbsp;&nbsp;<a href='" + MODULES_SUBDIRECTORY + "/\" + moduleName + \".html'>\" + highlightedEntry + \"<\\/a><br>\\n\";\n" + 
                "            }\n" + 
                "        }\n" + 
                "        if (moduleResults.length > 0) {\n" + 
                "            finalResults += '<div class=\"" + StyleClassConstants.INDEX_NAV_CURRENT_LINK.toHTML() + "\">" + LocalizableUserVisibleString.MODULES.toResourceString() + "<\\/div>\\n<div class=\\'" + StyleClassConstants.MAIN_CONTENT.toHTML() + "\\'>' + moduleResults + '<\\/div>';\n" + 
                "        }\n" +
                "    }\n" + 
                "    for (var k in indices) {\n" +
                "        var indexInfo = indices[k];\n" +
                "        var indexName = indexInfo.name;\n" + 
                "        var indexDisplayName = indexInfo.label;\n" + 
                "        var indexResults = '';\n" +
                "        var indexMaxScope = privateScope;\n" + 
                "        for (var j in modules) {\n" +
                "            var moduleName = modules[j].moduleName;\n" +
                "            var results = '';\n" + 
                "            var module = modules[j].indices;\n" + 
                "            var index = module[indexName];\n" +
                "            var moduleHasMatches = false;\n" +
                "            var moduleMaxScope = privateScope;\n" + 
                "            for (var i in index) {\n" + 
                "                var item = index[i];\n" +
                "                var scope = item.scope;\n" +
                "                var entry = item.name;\n" +
                "                var label = item.label;\n" + 
                "                if (entry.match(re)) {\n" +
                "                    if (!moduleHasMatches) {\n" + 
                "                        results += moduleName.bold() + '<br>\\n';\n" + 
                "                    }\n" + 
                "                    moduleHasMatches = true;\n" +
                "                    moduleMaxScope = Math.max(scope, moduleMaxScope);\n" +
                "                    numResults++;\n" +
                "                    numResultsPerScope[scope]++;\n" +
                "                    var highlightedEntry = entry.replace(re, '<b>$1<\\/b>');\n" +
                "                    results += \"<span class='\" + scopeClass[scope] + \"'>&nbsp;&nbsp;<a href='" + MODULES_SUBDIRECTORY + "/\" + moduleName + \".html#\" + label + \"'>\" + highlightedEntry +\"<\\/a><br><\\/span>\\n\";\n" + 
                "                }\n" + 
                "            }\n" +
                "            if (results.length > 0) {\n" +
                "                indexResults += \"<div class='\" + scopeClass[moduleMaxScope] + \"'>\" + results + \"<\\/div>\";\n" +
                "            }\n" +
                "            indexMaxScope = Math.max(moduleMaxScope, indexMaxScope);\n" + 
                "        }\n" + 
                "        if (indexResults.length > 0) {\n" +
                "            indexResults = '<div class=\"' + scopeClass[indexMaxScope] + '\"><div class=\"" + StyleClassConstants.INDEX_NAV_CURRENT_LINK.toHTML() + "\">' + indexDisplayName + '<\\/div>\\n<div class=\\'" + StyleClassConstants.MAIN_CONTENT.toHTML() + "\\'>' + indexResults + '<\\/div><\\/div>';\n" +
                "        }\n" +
                "        finalResults += indexResults;\n" +
                "    }\n" +
                "    if (numResults > 0) {\n" + 
                "        finalResults = '<div class=\\'" + StyleClassConstants.MAIN_CONTENT.toHTML() + "\\'>' + '" + LocalizableUserVisibleString.SEARCH_RESULT_SUMMARY.toResourceString() + "'.replace('\\{0\\}', origKey.italics()).bold() +\n" +
                "                       '<div class=\"' + scopeClass[publicScope] + '\">&nbsp;&nbsp;' + '" + LocalizableUserVisibleString.SEARCH_RESULT_SUMMARY_PUBLIC.toResourceString() + "'.replace('\\{0\\}', numResultsPerScope[publicScope]).bold() + '<\\/div>' +\n" +
                "                       (numResultsPerScope[protectedScope] == 0 ? '' : '<div class=\"' + scopeClass[protectedScope] + '\">&nbsp;&nbsp;' + '" + LocalizableUserVisibleString.SEARCH_RESULT_SUMMARY_PROTECTED.toResourceString() + "'.replace('\\{0\\}', numResultsPerScope[protectedScope]).bold() + '<\\/div>') +\n" +
                "                       (numResultsPerScope[privateScope] == 0 ? '' : '<div class=\"' + scopeClass[privateScope] + '\">&nbsp;&nbsp;' + '" + LocalizableUserVisibleString.SEARCH_RESULT_SUMMARY_PRIVATE.toResourceString() + "'.replace('\\{0\\}', numResultsPerScope[privateScope]).bold() + '<\\/div>') +\n" +
                "                       '<\\/div>\\n' + finalResults;\n" + 
                "    } else {\n" + 
                "        finalResults = '<p class=\\'" + StyleClassConstants.MAIN_CONTENT.toHTML() + "\\'>' + '" + LocalizableUserVisibleString.NO_MATCHES.toResourceString() + "'.italics(); + '<\\/p>'\n" +
                "    }\n" +
                "    return finalResults;\n" +
                "}\n";
        
        String jsPerformSearchFunction = "\n" +
                "function performSearch() {\n" + 
                "    try {\n" + 
                "        var key = document.getElementById('" + ElementID.KEY + "').value;\n" +
                "        var caseSensitive = false;\n" +
                "        var useRegexp = false;\n" +
                "        var includeModuleIndex = true;\n" +
                "        var indices = new Array(\n" +
                "            pair('typeIndex', '" + LocalizableUserVisibleString.TYPES.toResourceString() + "'),\n" +
                "            pair('functionalAgentIndex', '" + LocalizableUserVisibleString.FUNCTIONS_CLASS_METHODS_AND_DATA_CONSTRUCTORS.toResourceString() + "'),\n" +
                "            pair('typeClassIndex', '" + LocalizableUserVisibleString.TYPE_CLASSES.toResourceString() + "')\n" +
                "        );\n" +
                "        var results = findKey(key, caseSensitive, useRegexp, includeModuleIndex, indices);\n" +
                "        if (results.length > 0) {\n" + 
                "            document.getElementById('" + ElementID.RESULTS + "').innerHTML = results;\n" +
                "        }\n" + 
                "    } catch (e) {\n" +
                "        if (e.message != undefined) {\n" +
                "            alert(e.message);\n" +
                "        } else if (e.description != undefined) {\n" +
                "            alert(e.description);\n" +
                "        } else {\n" +
                "            alert(e);\n" +
                "        }\n" +
                "    }\n" + 
                "}\n";
        
        String jsExpandFrameFunction = "\n" +
                "function expandFrame() {\n" + 
                "    if (parent.frames != undefined && parent.frames[3] != undefined) {\n" + 
                "        parent.document.getElementById('" + ElementID.OUTER_FRAMESET + "').cols = '20%, 80%, 0%';\n" + 
                "        parent.document.getElementById('" + ElementID.OUTER_FRAMESET + "').cols = '20%, 60%, 20%';\n" + 
                "    }\n" + 
                "}\n";
        
        String jsHideFrameFunction = "\n" +
                "function hideFrame() {\n" + 
                "    if (parent.frames != undefined && parent.frames[3] != undefined) {\n" + 
                "        parent.document.getElementById('" + ElementID.OUTER_FRAMESET + "').cols = '20%, 80%, 0%';\n" + 
                "    }\n" + 
                "}\n";

        String javascript = "\n" +
            getUpdateScopeDisplaySettingsJavascript() +
            jsPrefixesSetup.toString() +
            jsMasterIndexSetup.toString() +
            jsMakeModuleEntryFunction +
            jsMakePairFunction +
            jsMakePfFunction +
            jsFindKeyFunction +
            jsPerformSearchFunction +
            jsExpandFrameFunction +
            jsHideFrameFunction;
        
        ////
        /// With the javascript built, now build up the HTML.
        //
        currentPage.openTag(HTML.Tag.BODY, classAttribute(StyleClassConstants.WITH_MAIN_CONTENT).concat(HTMLBuilder.AttributeList.make(HTML4.ONLOAD_ATTRIBUTE, "expandFrame()")));
        
        currentPage.addTaggedText(HTML.Tag.SCRIPT, HTMLBuilder.AttributeList.make(HTML.Attribute.TYPE, "text/javascript"), javascript);
        
        // the title and the 'hide' button
        currentPage
            .openTag(HTML.Tag.TABLE, classAttribute(StyleClassConstants.SEARCH_BOX_TITLE).concat(HTMLBuilder.AttributeList.make(HTML.Attribute.WIDTH, StyleValueConstants.ONE_HUNDRED_PERCENT)))
            .openTag(HTML.Tag.TR)
            .addTaggedText(HTML.Tag.TD, HTMLBuilder.AttributeList.make(HTML.Attribute.WIDTH, StyleValueConstants.ONE_HUNDRED_PERCENT), LocalizableUserVisibleString.SEARCH.toResourceString())
            .openTag(HTML.Tag.TD)
            .emptyTag(HTML.Tag.INPUT, HTMLBuilder.AttributeList.make(HTML.Attribute.TYPE, "button", HTML.Attribute.VALUE, LocalizableUserVisibleString.HIDE.toResourceString()).concat(HTMLBuilder.AttributeList.make(HTML4.ONCLICK_ATTRIBUTE, "hideFrame()")))
            .closeTag(HTML.Tag.TD)
            .closeTag(HTML.Tag.TR)
            .closeTag(HTML.Tag.TABLE);
        
        // the search box
        currentPage
            .openTag(HTML.Tag.FORM, HTMLBuilder.AttributeList.make(HTML.Attribute.ACTION, "javascript:performSearch()", HTML.Attribute.TARGET, "_self"))
            .openTag(HTML.Tag.TABLE, classAttribute(StyleClassConstants.SEARCH_BOX).concat(HTMLBuilder.AttributeList.make(HTML.Attribute.WIDTH, StyleValueConstants.ONE_HUNDRED_PERCENT, HTML.Attribute.CELLSPACING, "0", HTML.Attribute.CELLPADDING, "0")))
            .openTag(HTML4.TBODY_TAG)
            .openTag(HTML.Tag.TR)
            .addTaggedText(HTML.Tag.TD, LocalizableUserVisibleString.ENTER_SEARCH_TERM_COLON.toResourceString())
            .closeTag(HTML.Tag.TR)
            .openTag(HTML.Tag.TR)
            .openTag(HTML.Tag.TD)
            .emptyTag(HTML.Tag.INPUT, idAndClassAttributes(ElementID.KEY, StyleClassConstants.SEARCH_FIELD).concat(HTMLBuilder.AttributeList.make(HTML.Attribute.SIZE, "8192")))
            .closeTag(HTML.Tag.TD)
            .closeTag(HTML.Tag.TR)
            .openTag(HTML.Tag.TR)
            .openTag(HTML.Tag.TD, HTMLBuilder.AttributeList.make(HTML.Attribute.ALIGN, StyleValueConstants.RIGHT_ALIGN))
            .emptyTag(HTML.Tag.INPUT, HTMLBuilder.AttributeList.make(HTML.Attribute.TYPE, "submit", HTML.Attribute.VALUE, LocalizableUserVisibleString.SEARCH.toResourceString()))
            .closeTag(HTML.Tag.TD)
            .closeTag(HTML.Tag.TR)
            .closeTag(HTML4.TBODY_TAG)
            .closeTag(HTML.Tag.TABLE)
            .closeTag(HTML.Tag.FORM);
        
        // the results section
        currentPage.addTaggedText(HTML.Tag.DIV, idAttribute(ElementID.RESULTS), "");
        
        currentPage.closeTag(HTML.Tag.BODY).closeTag(HTML.Tag.HTML);
        
        return DocTypeDecl.HTML_4_01_TRANSITIONAL + currentPage.toHTML();
    }
    
    /**
     * Creates the javascript for initializing the an index array from a list of IndexEntry objects representing a per-module index.
     * @param index the list of IndexEntry objects representing a per-module index.
     * @param prefixes the list of label prefixes, to be appended when a new prefix is encountered.
     * @return the required javascript for initializing the index array.
     */
    private String getMasterScopedEntitySearchPageJavascriptForPerModuleIndex(List<IndexEntry> index, List<String> prefixes) {
        StringBuilder jsIndexSetup = new StringBuilder("new Array(");
        
        for (int i = 0, n = index.size(); i < n; i++) {
            if (i > 0) {
                jsIndexSetup.append(",");
            }
            IndexEntry entry = index.get(i);
            
            String displayName = entry.getDisplayName();
            String label = entry.getLabel();
            
            int scopeNum;
            Scope scope = entry.getScope();
            if (scope.isPrivate()) {
                scopeNum = 0;
            } else if (scope.isProtected()) {
                scopeNum = 1;
            } else if (scope.isPublic()) {
                scopeNum = 2;
            } else {
                throw new IllegalStateException("Invalid scope: " + scope);
            }
            
            if (label.endsWith(displayName)) {
                // the label has the display name as its suffix, so extract the prefix and
                // store it in the prefixes list if it's not already in there, and use the index into
                // the list as a shorter representation of the prefix
                String prefix = label.substring(0, label.length() - displayName.length());
                
                int prefixIndex = prefixes.indexOf(prefix);
                if (prefixIndex == -1) {
                    prefixes.add(prefix);
                    prefixIndex = prefixes.size() - 1;
                }
                
                jsIndexSetup.append("\n pf(").append(scopeNum).append(',').append(prefixIndex).append(",'").append(displayName).append("')");
            } else {
                jsIndexSetup.append("\n item(").append(scopeNum).append(",'").append(displayName).append("','").append(label).append("')");
            }
        }
        
        jsIndexSetup.append(")");
        
        return jsIndexSetup.toString();
    }
    
    /**
     * Constructs the HTML contents of the type index for the current module.
     * @param indexFileName the name of the index file.
     * @return the contents of the file.
     */
    private String getTypeIndexPageHTML(String indexFileName) {
        return getIndexPageHTML(LocalizableUserVisibleString.TYPE_INDEX.toResourceString(), LocalizableUserVisibleString.TYPES.toResourceString(), getPerModuleTypeIndex(currentModuleName), indexFileName);
    }
    
    /**
     * Constructs the HTML contents of the functional agent index for the current module.
     * @param indexFileName the name of the index file.
     * @return the contents of the file.
     */
    private String getFunctionalAgentIndexPageHTML(String indexFileName) {
        // make a copy of the index, so we can sort it in the order we desire, which is case-insensitive lexicographical order
        List<IndexEntry> index = new ArrayList<IndexEntry>(getPerModuleFunctionalAgentIndex(currentModuleName));
        
        // sort the list of index entries
        Collections.sort(index, new IndexEntryComparator());
        
        return getIndexPageHTML(LocalizableUserVisibleString.FUNCTIONAL_AGENT_INDEX.toResourceString(), LocalizableUserVisibleString.FUNCTIONS_CLASS_METHODS_AND_DATA_CONSTRUCTORS.toResourceString(), index, indexFileName);
    }
    
    /**
     * Constructs the HTML contents of the type class index for the current module.
     * @param indexFileName the name of the index file.
     * @return the contents of the file.
     */
    private String getTypeClassIndexPageHTML(String indexFileName) {
        return getIndexPageHTML(LocalizableUserVisibleString.TYPE_CLASS_INDEX.toResourceString(), LocalizableUserVisibleString.TYPE_CLASSES.toResourceString(), getPerModuleTypeClassIndex(currentModuleName), indexFileName);
    }
    
    /**
     * Constructs the HTML contents of the instance index for the current module.
     * @param indexFileName the name of the index file.
     * @return the contents of the file.
     */
    private String getInstanceIndexPageHTML(String indexFileName) {
        return getIndexPageHTML(LocalizableUserVisibleString.INSTANCE_INDEX.toResourceString(), LocalizableUserVisibleString.INSTANCES.toResourceString(), getPerModuleInstanceIndex(currentModuleName), indexFileName);
    }

    /**
     * Constructs the HTML contents of one of the four per-module navigational indices for the current module.
     * @param pageTitle the title of the page.
     * @param heading the heading of the page.
     * @param index the List of IndexEntry objects forming the index.
     * @param indexFileName the name of the index file.
     * @return the contents of the file.
     */
    private String getIndexPageHTML(String pageTitle, String heading, List<IndexEntry> index, String indexFileName) {
        startNewCurrentPage();
        
        currentPage.openTag(HTML.Tag.HTML);
        String relativePathToBaseDirectory = "../";
        generateHeadSection(makePageTitle(pageTitle), relativePathToBaseDirectory, MAIN_FRAME_NAME, null);
        
        ////
        /// Generate the heading and the intra-index links.
        //
        currentPage.openTag(HTML.Tag.BODY, classAttribute(StyleClassConstants.WITH_MAIN_CONTENT));
        
        generateUpdateScopeDisplaySettingsJavascript();

        /// If the module name has more than one component, break it up into two pieces: the immediate prefix and the last component
        //
        final ModuleName immediatePrefix = currentModuleName.getImmediatePrefix();
        if (immediatePrefix != null) {
            currentPage.addTaggedText(HTML.Tag.DIV, classAttribute(StyleClassConstants.SIDE_BAR_KHAKI_SUPERTITLE), immediatePrefix.toSourceText());
        }
        currentPage.addTaggedText(HTML.Tag.H2, classAttribute(StyleClassConstants.SIDE_BAR_KHAKI_TITLE), currentModuleName.getLastComponent());
        
        currentPage.openTag(HTML.Tag.DIV, classAttribute(StyleClassConstants.INDEX_NAV));
        
        generateIndexPageNavLink(getPerModuleTypeIndex(currentModuleName), getPerModuleTypeIndexFileName(currentModuleName), indexFileName, LocalizableUserVisibleString.TYPES.toResourceString());
        
        generateIndexPageNavLink(getPerModuleFunctionalAgentIndex(currentModuleName), getPerModuleFunctionalAgentIndexFileName(currentModuleName), indexFileName, LocalizableUserVisibleString.FUNCTIONS_CLASS_METHODS_AND_DATA_CONSTRUCTORS.toResourceString());
        
        generateIndexPageNavLink(getPerModuleTypeClassIndex(currentModuleName), getPerModuleTypeClassIndexFileName(currentModuleName), indexFileName, LocalizableUserVisibleString.TYPE_CLASSES.toResourceString());
        
        generateIndexPageNavLink(getPerModuleInstanceIndex(currentModuleName), getPerModuleInstanceIndexFileName(currentModuleName), indexFileName, LocalizableUserVisibleString.INSTANCES.toResourceString());
        
        currentPage
            .closeTag(HTML.Tag.DIV)
            .openTag(HTML.Tag.DIV, classAttribute(StyleClassConstants.MAIN_CONTENT))
            .addTaggedText(HTML.Tag.H3, classAttribute(StyleClassConstants.NON_DISPLAYED_HEADER), heading);

        ////
        /// Generate the index entries.
        //
        for (int i = 0, n = index.size(); i < n; i++) {
            IndexEntry entry = index.get(i);
            currentPage.openTag(HTML.Tag.DIV, classAttribute(getScopeStyleClass(entry.getScope())));
            
            if (config.shouldSeparateInstanceDoc && entry.getKind() == IndexEntry.Kind.INSTANCE) {
                
                String filePath = relativePathToBaseDirectory + SEPARATE_INSTANCE_DOC_SUBDIRECTORY + "/" + getSeparateInstanceDocFileName(currentModuleName);
                currentPage.addTaggedText(HTML.Tag.A, nonLocalHrefAttribute(filePath, entry.getLabel()), entry.getDisplayName());
                
            } else {
                generateNonLocalReference(currentPage, relativePathToBaseDirectory + MODULES_SUBDIRECTORY, currentModuleName, entry.getLabel(), entry.getDisplayName(), null);
            }
            currentPage.closeTag(HTML.Tag.DIV);
        }
        
        currentPage.closeTag(HTML.Tag.DIV).closeTag(HTML.Tag.BODY).closeTag(HTML.Tag.HTML);
        
        return DocTypeDecl.HTML_4_01_TRANSITIONAL + currentPage.toHTML();
    }
    
    /////====================================================================================================
    ////
    /// Generation of the navigation bar
    //

    /**
     * Generates a navigational index for an intra-index link.
     * @param linkedIndex the List of IndexEntry objects forming the index being linked to.
     * @param linkedFileName the name of the index file being linked to.
     * @param thisFileName the name of the index file being generated.
     * @param displayName the display name for the link.
     */
    private void generateIndexPageNavLink(List<IndexEntry> linkedIndex, String linkedFileName, String thisFileName, String displayName) {
        if (linkedFileName.equals(thisFileName)) {
            currentPage
                .openTag(HTML.Tag.DIV, classAttribute(StyleClassConstants.INDEX_NAV_CURRENT_LINK))
                .addText(displayName)
                .closeTag(HTML.Tag.DIV);
        } else if (linkedIndex.size() > 0) {
            currentPage
                .openTag(HTML.Tag.DIV, classAttribute(StyleClassConstants.INDEX_NAV_LINK))
                .addTaggedText(HTML.Tag.A, nonLocalHrefAttribute(linkedFileName, null).concat(HTMLBuilder.AttributeList.make(HTML.Attribute.TARGET, "_self")), displayName)
                .closeTag(HTML.Tag.DIV);
        }
    }
    
    /**
     * Constructs the HTML attributes for the navigation bar at the top and bottom of each page.
     * @return the list of attributes.
     */
    private HTMLBuilder.AttributeList getNavBarAttributes() {
        return classAttribute(StyleClassConstants.NAV_BAR);
    }
    
    /**
     * Generates a generic navigation bar.
     * @param isFooter true if this bar forms the footer; false if this bar forms the header.
     * @param relativePathToBaseDirectory the relative path to the base directory for documentation generation.
     */
    private void generateGenericNavBar(boolean isFooter, String relativePathToBaseDirectory) {
        currentPage
            .openTag(HTML.Tag.DIV, getNavBarAttributes());
        
        if (isFooter) {
            generateHorizontalNavBarHeaderFooterRow(isFooter);
            generateHorizontalNavBarGlobalNavTableRow(relativePathToBaseDirectory);
        } else {
            generateHorizontalNavBarGlobalNavTableRow(relativePathToBaseDirectory);
            generateHorizontalNavBarHeaderFooterRow(isFooter);
        }
        
        currentPage
            .closeTag(HTML.Tag.DIV);
    }
    
    /**
     * Generates a navigation bar for a module documentation page.
     * @param isFooter true if this bar forms the footer; false if this bar forms the header.
     */
    private void generateModulePageNavBar(boolean isFooter, int nTypeConstructors, int nFunctions, int nTypeClasses, int nClassInstances) {
        String relativePathToBaseDirectory = "../";
        generateModulePageNavBar(isFooter, relativePathToBaseDirectory, nTypeConstructors, nFunctions, nTypeClasses, nClassInstances);
    }

    /**
     * Generates a navigation bar for a module documentation page.
     * @param isFooter true if this bar forms the footer; false if this bar forms the header.
     */
    private void generateModulePageNavBar(boolean isFooter, String relativePathToBaseDirectory, int nTypeConstructors, int nFunctions, int nTypeClasses, int nClassInstances) {
        currentPage
            .openTag(HTML.Tag.DIV, getNavBarAttributes());
        
        if (isFooter) {
            generateHorizontalNavBarHeaderFooterRow(isFooter);
            generateHorizontalNavBarLocalNavTableRow(relativePathToBaseDirectory, nTypeConstructors, nFunctions, nTypeClasses, nClassInstances);
            generateHorizontalNavBarGlobalNavTableRow(relativePathToBaseDirectory);
        } else {
            generateHorizontalNavBarGlobalNavTableRow(relativePathToBaseDirectory);
            generateHorizontalNavBarLocalNavTableRow(relativePathToBaseDirectory, nTypeConstructors, nFunctions, nTypeClasses, nClassInstances);
            generateHorizontalNavBarHeaderFooterRow(isFooter);
        }
            
        currentPage
            .closeTag(HTML.Tag.DIV);
    }

    /**
     * Generates the header/footer row in the navigation bar.
     * @param isFooter true if this bar forms the footer; false if this bar forms the header.
     */
    private void generateHorizontalNavBarHeaderFooterRow(boolean isFooter) {
        currentPage
            .addTaggedText(HTML.Tag.DIV, classAttribute(StyleClassConstants.NAV_BAR_HEADER_FOOTER), isFooter ? config.footer : config.header);
    }

    /**
     * Generates the global navigation portion of the navigation bar.
     * @param relativePathToBaseDirectory the relative path to the base directory for documentation generation.
     */
    private void generateHorizontalNavBarGlobalNavTableRow(String relativePathToBaseDirectory) {
        currentPage
            .openTag(HTML.Tag.DIV, classAttribute(StyleClassConstants.NAV_BAR_GLOBAL_ROW))
            .addTaggedText(HTML.Tag.A, nonLocalHrefAttribute(relativePathToBaseDirectory + OVERVIEW_PAGE_FILENAME), LocalizableUserVisibleString.OVERVIEW.toResourceString())
            .addText(LocalizableUserVisibleString.NAV_BAR_ITEM_SEPARATOR.toResourceString())
            .addTaggedText(HTML.Tag.A, nonLocalHrefAttribute(relativePathToBaseDirectory + MASTER_SCOPED_ENTITY_SEARCH_PAGE_FILENAME).concat(HTMLBuilder.AttributeList.make(HTML.Attribute.TARGET, SEARCH_FRAME_NAME)), LocalizableUserVisibleString.SEARCH.toResourceString())
            .closeTag(HTML.Tag.DIV);
    }

    /**
     * Generates the local navigation portion of the navigation bar.
     */
    private void generateHorizontalNavBarLocalNavTableRow(String relativePathToBaseDirectory, int nTypeConstructors, int nFunctions, int nTypeClasses, int nClassInstances) {
        currentPage
            .openTag(HTML.Tag.DIV, classAttribute(StyleClassConstants.NAV_BAR_LOCAL_ROW));
        
        if (nTypeConstructors > 0 || nFunctions > 0 || nTypeClasses > 0 || nClassInstances > 0) {
            currentPage.addTaggedText(HTML.Tag.A, localHrefAttribute(ElementID.SUMMARY_SECTION), LocalizableUserVisibleString.MODULE_SUMMARY.toResourceString());
        } else {
            currentPage.addTaggedText(HTML.Tag.SPAN, classAttribute(StyleClassConstants.DISABLED_LINK), LocalizableUserVisibleString.MODULE_SUMMARY.toResourceString());
        }
        
        currentPage.addText(LocalizableUserVisibleString.NAV_BAR_ITEM_SEPARATOR.toResourceString());
        
        if (nTypeConstructors > 0) {
            HTMLBuilder.AttributeList hrefAttribute;
            if (inSeparateInstanceDoc) {
                hrefAttribute = nonLocalHrefAttribute(relativePathToBaseDirectory + MODULES_SUBDIRECTORY + "/" + getModuleFileName(currentModuleName), ElementID.TYPES_SECTION);
            } else {
                hrefAttribute = localHrefAttribute(ElementID.TYPES_SECTION);
            }
            currentPage.addTaggedText(HTML.Tag.A, hrefAttribute, LocalizableUserVisibleString.TYPES.toResourceString());
        } else {
            currentPage.addTaggedText(HTML.Tag.SPAN, classAttribute(StyleClassConstants.DISABLED_LINK), LocalizableUserVisibleString.TYPES.toResourceString());
        }
        
        currentPage.addText(LocalizableUserVisibleString.NAV_BAR_ITEM_SEPARATOR.toResourceString());
        
        if (nFunctions > 0) {
            HTMLBuilder.AttributeList hrefAttribute;
            if (inSeparateInstanceDoc) {
                hrefAttribute = nonLocalHrefAttribute(relativePathToBaseDirectory + MODULES_SUBDIRECTORY + "/" + getModuleFileName(currentModuleName), ElementID.FUNCTIONS_SECTION);
            } else {
                hrefAttribute = localHrefAttribute(ElementID.FUNCTIONS_SECTION);
            }
            currentPage.addTaggedText(HTML.Tag.A, hrefAttribute, LocalizableUserVisibleString.FUNCTIONS.toResourceString());
        } else {
            currentPage.addTaggedText(HTML.Tag.SPAN, classAttribute(StyleClassConstants.DISABLED_LINK), LocalizableUserVisibleString.FUNCTIONS.toResourceString());
        }
        
        currentPage.addText(LocalizableUserVisibleString.NAV_BAR_ITEM_SEPARATOR.toResourceString());
        
        if (nTypeClasses > 0) {
            HTMLBuilder.AttributeList hrefAttribute;
            if (inSeparateInstanceDoc) {
                hrefAttribute = nonLocalHrefAttribute(relativePathToBaseDirectory + MODULES_SUBDIRECTORY + "/" + getModuleFileName(currentModuleName), ElementID.TYPE_CLASSES_SECTION);
            } else {
                hrefAttribute = localHrefAttribute(ElementID.TYPE_CLASSES_SECTION);
            }
            currentPage.addTaggedText(HTML.Tag.A, hrefAttribute, LocalizableUserVisibleString.TYPE_CLASSES.toResourceString());
        } else {
            currentPage.addTaggedText(HTML.Tag.SPAN, classAttribute(StyleClassConstants.DISABLED_LINK), LocalizableUserVisibleString.TYPE_CLASSES.toResourceString());
        }
        
        currentPage.addText(LocalizableUserVisibleString.NAV_BAR_ITEM_SEPARATOR.toResourceString());
        
        if (nClassInstances > 0) {
            HTMLBuilder.AttributeList hrefAttribute;
            if (config.shouldSeparateInstanceDoc && !inSeparateInstanceDoc) {
                hrefAttribute = nonLocalHrefAttribute(relativePathToBaseDirectory + SEPARATE_INSTANCE_DOC_SUBDIRECTORY + "/" + getSeparateInstanceDocFileName(currentModuleName), ElementID.INSTANCES_SECTION);
            } else {
                hrefAttribute = localHrefAttribute(ElementID.INSTANCES_SECTION);
            }
            currentPage.addTaggedText(HTML.Tag.A, hrefAttribute, LocalizableUserVisibleString.INSTANCES.toResourceString());
        } else {
            currentPage.addTaggedText(HTML.Tag.SPAN, classAttribute(StyleClassConstants.DISABLED_LINK), LocalizableUserVisibleString.INSTANCES.toResourceString());
        }
        
        currentPage.closeTag(HTML.Tag.DIV);
    }
    
    /**
     * Generates the fine print at the bottom of the page.
     */
    private void generatePageBottom() {
        currentPage.addTaggedText(HTML.Tag.DIV, classAttribute(StyleClassConstants.PAGE_BOTTOM), config.bottom);
    }

    /////====================================================================================================
    ////
    /// File name builders
    //
    
    /**
     * Constructs a file name for the main documentation page from a module name, properly disambiguated.
     * @param moduleName the module name.
     * @return the corresponding file name.
     */
    private String getModuleFileName(ModuleName moduleName) {
        return getDisambiguatedNameForModule(moduleName) + DOT + HTML_FILE_EXTENSION;
    }
    
    /**
     * Constructs a file name prefix for a per-module index from a module name, properly disambiguated.
     * @param moduleName the module name.
     * @return the corresponding file name prefix.
     */
    private String getPerModuleIndexFileNamePrefix(ModuleName moduleName) {
        return getDisambiguatedNameForModule(moduleName) + FILENAME_POSTFIX_SEPARATOR;
    }
    
    /**
     * Constructs a file name for the type index page from a module name, properly disambiguated.
     * @param moduleName the module name.
     * @return the corresponding file name.
     */
    private String getPerModuleTypeIndexFileName(ModuleName moduleName) {
        return getDisambiguatedNameForModule(moduleName) + FILENAME_POSTFIX_SEPARATOR + TYPE_INDEX_FILENAME_POSTFIX + DOT + HTML_FILE_EXTENSION;
    }
    
    /**
     * Constructs a file name for the functional agent index page from a module name, properly disambiguated.
     * @param moduleName the module name.
     * @return the corresponding file name.
     */
    private String getPerModuleFunctionalAgentIndexFileName(ModuleName moduleName) {
        return getDisambiguatedNameForModule(moduleName) + FILENAME_POSTFIX_SEPARATOR + FUNCTIONAL_AGENT_INDEX_FILENAME_POSTFIX + DOT + HTML_FILE_EXTENSION;
    }
    
    /**
     * Constructs a file name for the type class index page from a module name, properly disambiguated.
     * @param moduleName the module name.
     * @return the corresponding file name.
     */
    private String getPerModuleTypeClassIndexFileName(ModuleName moduleName) {
        return getDisambiguatedNameForModule(moduleName) + FILENAME_POSTFIX_SEPARATOR + TYPE_CLASS_INDEX_FILENAME_POSTFIX + DOT + HTML_FILE_EXTENSION;
    }
    
    /**
     * Constructs a file name for the instance index page from a module name, properly disambiguated.
     * @param moduleName the module name.
     * @return the corresponding file name.
     */
    private String getPerModuleInstanceIndexFileName(ModuleName moduleName) {
        return getDisambiguatedNameForModule(moduleName) + FILENAME_POSTFIX_SEPARATOR + INSTANCE_INDEX_FILENAME_POSTFIX + DOT + HTML_FILE_EXTENSION;
    }
    
    /**
     * Constructs a file name for the type usage indices page from a type constructor name, properly disambiguated.
     * @param qualifiedName the qualified name of the type constructor.
     * @return the corresponding file name.
     */
    private String getTypeConsUsageIndexFileName(QualifiedName qualifiedName) {
        ModuleName moduleName = qualifiedName.getModuleName();
        return getDisambiguatedNameForModule(moduleName) + DOT + getDisambiguatedNameForTypeCons(moduleName, qualifiedName.getUnqualifiedName()) + DOT + HTML_FILE_EXTENSION;
    }
    
    /**
     * Constructs a file name for the type class usage indices page from a type class name, properly disambiguated.
     * @param qualifiedName the qualified name of the type class.
     * @return the corresponding file name.
     */
    private String getTypeClassUsageIndexFileName(QualifiedName qualifiedName) {
        ModuleName moduleName = qualifiedName.getModuleName();
        return getDisambiguatedNameForModule(moduleName) + DOT + getDisambiguatedNameForTypeClass(moduleName, qualifiedName.getUnqualifiedName()) + DOT + HTML_FILE_EXTENSION;
    }
    
    /**
     * Constructs a file name for the separate instance documentation from a module name, properly disambiguated.
     * @param moduleName the module name.
     * @return the corresponding file name.
     */
    private String getSeparateInstanceDocFileName(ModuleName moduleName) {
        return getDisambiguatedNameForModule(moduleName) + FILENAME_POSTFIX_SEPARATOR + SEPARATE_INSTANCE_DOC_FILENAME_POSTFIX + DOT + HTML_FILE_EXTENSION;
    }
    
    /////====================================================================================================
    ////
    /// Helpers for building the module summary of a module documentation page, and the usage indices
    //
    
    /**
     * Generates the start of an overview table.
     * @param caption the caption for the table.
     * @param nested whether the table is nested within another table.
     */
    private void beginOverviewTable(String caption, boolean nested) {
        currentPage.openTag(HTML.Tag.TABLE, nested ? overviewNestedTableAttributes() : overviewTableAttributes());
        if (caption != null) {
            currentPage.addTaggedText(HTML.Tag.CAPTION, classAttribute(StyleClassConstants.MINOR_SECTION), caption);
        }
        currentPage.openTag(HTML4.TBODY_TAG);
    }

    /**
     * Generates the end of an overview table.
     */
    private void endOverviewTable() {
        currentPage.closeTag(HTML4.TBODY_TAG).closeTag(HTML.Tag.TABLE);
    }
    
    /**
     * Generates the overview for a functional agent.
     * @param entity the functional agent entity.
     * @param metadata the metadata that may be generated.
     * @param docComment the CALDoc comment to be generated.
     * @param descriptionStyleClass the style class to use for the description.
     */
    private void generateFunctionalAgentOverview(FunctionalAgent entity, String label, FunctionalAgentMetadata metadata, CALDocComment docComment, StyleClass descriptionStyleClass) {
        TypeExpr typeExpr = entity.getTypeExpr();
        
        /// Generate the cell for the scope
        //
        currentPage
            .openTag(HTML.Tag.TR, classAttribute(getScopeStyleClass(entity.getScope())))
            .openTag(HTML.Tag.TD, classAttribute(StyleClassConstants.OVERVIEW_TABLE_SCOPE_COLUMN).concat(HTMLBuilder.AttributeList.make(HTML.Attribute.VALIGN, "baseline")))
            .addTaggedText(HTML.Tag.SPAN, classAttribute(StyleClassConstants.SCOPE), entity.getScope().toString())
            .closeTag(HTML.Tag.TD)
            .openTag(HTML.Tag.TD, HTMLBuilder.AttributeList.make(HTML.Attribute.VALIGN, "baseline"))
            .openTag(HTML.Tag.DL)
            .openTag(HTML.Tag.DT)
            .openTag(HTML.Tag.SPAN, classAttribute(StyleClassConstants.NAME_AND_TYPE))
            .openTag(HTML.Tag.SPAN, classAttribute(StyleClassConstants.OVERVIEW_REFERENCE));

        /// Generate the entity name, hyperlinked
        //
        generateLocalReference(label, entity.getName().getUnqualifiedName(), entity.getName().getQualifiedName());
        
        /// Generate the type signature, hyperlinked
        //
        generateTypeSignature(typeExpr.toSourceModel(true, ScopedEntityNamingPolicy.FULLY_QUALIFIED));
        
        currentPage
            .closeTag(HTML.Tag.SPAN)
            .closeTag(HTML.Tag.SPAN);
            
        maybeGenerateClassMethodIndicator(entity);
        
        currentPage
            .closeTag(HTML.Tag.DT)
            .openTag(HTML.Tag.DD, classAttribute(descriptionStyleClass));
        
        /// Generate the short description
        //
        generateShortDescription(metadata, docComment, inModulesSubdirectoryReferenceGenerator);
        
        currentPage
            .closeTag(HTML.Tag.DD)
            .closeTag(HTML.Tag.DL)
            .closeTag(HTML.Tag.TD)
            .closeTag(HTML.Tag.TR);
    }

    /**
     * If the entity is a class method, then generate the required/optional indicator.
     * @param entity the functional agent entity, which may or may not be a class method.
     */
    private void maybeGenerateClassMethodIndicator(FunctionalAgent entity) {
        
        if (entity instanceof ClassMethod) {
            ClassMethod method = (ClassMethod)entity;
            
            final String text;
            
            if (method.getDefaultClassMethodName() == null) {
                // no default - required
                text = LocalizableUserVisibleString.REQUIRED_METHOD_INDICATOR.toResourceString();
            } else {
                // has default - optional
                text = LocalizableUserVisibleString.OPTIONAL_METHOD_INDICATOR.toResourceString();
            }
            
            currentPage
                .addText(" ")
                .addTaggedText(HTML.Tag.SPAN, classAttribute(StyleClassConstants.CLASS_METHOD_INDICATOR), text);
        }
    }

    /**
     * Generates an entry for a usage index.
     * @param entity the functional agent entity.
     * @param metadata the metadata that may be generated.
     * @param docComment the CALDoc comment to be generated.
     * @param descriptionStyleClass the style class to use for the description.
     */
    private void generateUsageIndexEntry(FunctionalAgent entity, String label, FunctionalAgentMetadata metadata, CALDocComment docComment, StyleClass descriptionStyleClass) {
        TypeExpr typeExpr = entity.getTypeExpr();
        
        /// Generate the cell for the scope
        //
        currentPage
            .openTag(HTML.Tag.TR, classAttribute(getScopeStyleClass(entity.getScope())))
            .openTag(HTML.Tag.TD, classAttribute(StyleClassConstants.OVERVIEW_TABLE_SCOPE_COLUMN).concat(HTMLBuilder.AttributeList.make(HTML.Attribute.VALIGN, "baseline")))
            .addTaggedText(HTML.Tag.SPAN, classAttribute(StyleClassConstants.SCOPE), entity.getScope().toString())
            .closeTag(HTML.Tag.TD)
            .openTag(HTML.Tag.TD, HTMLBuilder.AttributeList.make(HTML.Attribute.VALIGN, "baseline"))
            .openTag(HTML.Tag.DL)
            .openTag(HTML.Tag.DT)
            .openTag(HTML.Tag.SPAN, classAttribute(StyleClassConstants.NAME_AND_TYPE))
            .openTag(HTML.Tag.SPAN, classAttribute(StyleClassConstants.OVERVIEW_REFERENCE));

        /// Generate the entity name, hyperlinked
        //
        String relativePathToModulesSubdirectory = "../" + MODULES_SUBDIRECTORY;
        generateNonLocalReference(currentPage, relativePathToModulesSubdirectory, entity.getName().getModuleName(), label, entity.getName().getUnqualifiedName(), entity.getName().getQualifiedName());
        
        /// Generate the type signature, hyperlinked
        //
        generateTypeSignature(typeExpr.toSourceModel(true, ScopedEntityNamingPolicy.FULLY_QUALIFIED), new TypeSignatureHTMLGeneratorUsingNonLocalReferences(relativePathToModulesSubdirectory));
        
        currentPage
            .closeTag(HTML.Tag.SPAN)
            .closeTag(HTML.Tag.SPAN)
            .closeTag(HTML.Tag.DT)
            .openTag(HTML.Tag.DD, classAttribute(descriptionStyleClass));
        
        /// Generate the short description
        //
        generateShortDescription(metadata, docComment, new ReferenceGenerator(relativePathToModulesSubdirectory));
        
        currentPage
            .closeTag(HTML.Tag.DD)
            .closeTag(HTML.Tag.DL)
            .closeTag(HTML.Tag.TD)
            .closeTag(HTML.Tag.TR);
    }

    ////
    /// Generation of short descriptions
    //
    
    /**
     * Generates the short description - the summary preceded by any deprecated block.
     * @param metadata the metadata that may be generated.
     * @param docComment the CALDoc comment to be generated.
     * @param referenceGenerator the reference generator to use for generating cross references.
     */
    private void generateShortDescription(CALFeatureMetadata metadata, CALDocComment docComment, ReferenceGenerator referenceGenerator) {
        CALDocComment.TextBlock deprecatedBlock = null;

        if (docComment != null) {
            deprecatedBlock = docComment.getDeprecatedBlock();
        }
        
        // Deprecated block
        generateHTMLForAttribute(StyleClassConstants.OVERVIEW_DEPRECATED_BLOCK, LocalizableUserVisibleString.DEPRECATED_COLON.toResourceString(), null, deprecatedBlock, referenceGenerator, deprecatedBlock != null);

        // Main description summary
        generateHTMLForAttribute(
            StyleClassConstants.SHORT_DESCRIPTION_BLOCK,
            null,
            getBestShortDescriptionFromMetadata(metadata),
            CALDocToHTMLUtilities.getSummaryFromCALDoc(docComment, referenceGenerator, getLocale(), LocalizableUserVisibleString.RETURNS_COLON.toResourceString(), StyleClassConstants.CODE_BLOCK, CODE_FORMATTING_TAG),
            referenceGenerator,
            false);
    }
    
    /**
     * Obtains the first sentence from a piece of text.
     * @param text the text from which the first sentence is to be extracted. Can be null.
     * @return the first sentence, or null if none is available.
     */
    private String getFirstSentence(String text) {
        if (text == null) {
            return null;
        }
        
        /// Use BreakIterator to parse the text and identify the first sentence.
        //
        Locale locale = getLocale();
        
        BreakIterator breakIterator;
        if (LocaleUtilities.isInvariantLocale(locale)) {
            breakIterator = BreakIterator.getSentenceInstance();
        } else {
            breakIterator = BreakIterator.getSentenceInstance(locale);
        }
        
        breakIterator.setText(text);
        
        /// The first sentence is from the start of the string to the first sentence boundary.
        //
        int firstSentenceBoundary = breakIterator.next();
        
        if (firstSentenceBoundary == BreakIterator.DONE) {
            return null;
        } else {
            return text.substring(0, firstSentenceBoundary).trim();
        }
    }

    /////====================================================================================================
    ////
    /// Helpers for generating the details section of a module documentation page
    //
    
    /**
     * Generates the standard description - the main description preceded by any deprecated block. 
     * @param metadata the metadata that may be generated.
     * @param docComment the CALDoc comment to be generated.
     * @param referenceGenerator the reference generator to use for generating cross references.
     */
    private void generateStandardDescription(CALFeatureMetadata metadata, CALDocComment docComment, ReferenceGenerator referenceGenerator) {
        // Obtain the various blocks from the CALDoc comment, if docComment is not null
        CALDocComment.TextBlock descriptionBlock = null;
        CALDocComment.TextBlock deprecatedBlock = null;

        if (docComment != null) {
            descriptionBlock = docComment.getDescriptionBlock();
            deprecatedBlock = docComment.getDeprecatedBlock();
        }
        
        // Deprecated block
        generateHTMLForAttribute(StyleClassConstants.DEPRECATED_BLOCK, LocalizableUserVisibleString.DEPRECATED_COLON.toResourceString(), null, deprecatedBlock, referenceGenerator, deprecatedBlock != null);

        // Main description
        generateHTMLForAttribute(StyleClassConstants.DESCRIPTION_BLOCK, null, getBestDescriptionFromMetadata(metadata), descriptionBlock, referenceGenerator, false);
    }

    /**
     * Generates the standard supplementary blocks - the author, version and see blocks.
     * @param metadata the metadata that may be generated.
     * @param docComment the CALDoc comment to be generated.
     * @param referenceGenerator the reference generator to use for generating cross references.
     */
    private void generateStandardSupplementaryBlocks(final CALFeatureMetadata metadata, final CALDocComment docComment, ReferenceGenerator referenceGenerator) {
        
        /// Obtain the various blocks from the CALDoc comment, if docComment is not null
        //
        CALDocComment.TextBlock versionBlock = null;
        ContentConvertibleToHTML valueFromAuthorBlocks = null;
        ContentConvertibleToHTML valueFromSeeBlocks = null;
        
        if (docComment != null) {
            versionBlock = docComment.getVersionBlock();
            
            /// Construct a ContentConvertibleToHTML instance to represent the contents of *all* the author blocks
            //
            valueFromAuthorBlocks = new ContentConvertibleToHTML() {
                private final int nAuthorBlocks = docComment.getNAuthorBlocks();
                /** {@inheritDoc} */
                @Override
                boolean isEmpty() {
                    return nAuthorBlocks == 0;
                }
                /** {@inheritDoc} */
                @Override
                void generateHTML(HTMLBuilder currentPage, CALDocToHTMLUtilities.CrossReferenceHTMLGenerator referenceGenerator) {
                    for (int i = 0; i < nAuthorBlocks; i++) {
                        if (i > 0) {
                            currentPage.addText(LocalizableUserVisibleString.COMMA_AND_SPACE.toResourceString());
                        }
                        CALDocToHTMLUtilities.generateHTMLForCALDocTextBlock(docComment.getNthAuthorBlock(i), currentPage, referenceGenerator, StyleClassConstants.CODE_BLOCK, CODE_FORMATTING_TAG);
                    }
                }
            };
            
            /// Construct a ContentConvertibleToHTML instance to represent the contents of *all* the see blocks
            //
            valueFromSeeBlocks = new ContentConvertibleToHTML() {
                private final int nModuleReferences = docComment.getNModuleReferences();
                private final int nTypeClassModuleReferences = docComment.getNTypeClassReferences();
                private final int nTypeConstructorReferences = docComment.getNTypeConstructorReferences();
                private final int nDataConstructorReferences = docComment.getNDataConstructorReferences();
                private final int nFunctionOrClassMethodReferences = docComment.getNFunctionOrClassMethodReferences();
                /** {@inheritDoc} */
                @Override
                boolean isEmpty() {
                    return nModuleReferences + nTypeClassModuleReferences + nTypeConstructorReferences + nDataConstructorReferences + nFunctionOrClassMethodReferences == 0;
                }
                /** {@inheritDoc} */
                @Override
                void generateHTML(HTMLBuilder currentPage, CALDocToHTMLUtilities.CrossReferenceHTMLGenerator referenceGenerator) {
                    int masterCount = 0;
                    
                    for (int i = 0; i < nModuleReferences; i++, masterCount++) {
                        if (masterCount > 0) {
                            currentPage.addText(LocalizableUserVisibleString.COMMA_AND_SPACE.toResourceString());
                        }
                        currentPage.openTag(HTML.Tag.CODE);
                        generateModuleReference(docComment.getNthModuleReference(i).getName());
                        currentPage.closeTag(HTML.Tag.CODE);
                    }
                    
                    for (int i = 0; i < nTypeClassModuleReferences; i++, masterCount++) {
                        if (masterCount > 0) {
                            currentPage.addText(LocalizableUserVisibleString.COMMA_AND_SPACE.toResourceString());
                        }
                        currentPage.openTag(HTML.Tag.CODE);
                        generateTypeClassReference(docComment.getNthTypeClassReference(i).getName());
                        currentPage.closeTag(HTML.Tag.CODE);
                    }
                    
                    for (int i = 0; i < nTypeConstructorReferences; i++, masterCount++) {
                        if (masterCount > 0) {
                            currentPage.addText(LocalizableUserVisibleString.COMMA_AND_SPACE.toResourceString());
                        }
                        currentPage.openTag(HTML.Tag.CODE);
                        generateTypeConsReference(docComment.getNthTypeConstructorReference(i).getName());
                        currentPage.closeTag(HTML.Tag.CODE);
                    }
                    
                    for (int i = 0; i < nDataConstructorReferences; i++, masterCount++) {
                        if (masterCount > 0) {
                            currentPage.addText(LocalizableUserVisibleString.COMMA_AND_SPACE.toResourceString());
                        }
                        currentPage.openTag(HTML.Tag.CODE);
                        generateDataConsReference(docComment.getNthDataConstructorReference(i).getName());
                        currentPage.closeTag(HTML.Tag.CODE);
                    }
                    
                    for (int i = 0; i < nFunctionOrClassMethodReferences; i++, masterCount++) {
                        if (masterCount > 0) {
                            currentPage.addText(LocalizableUserVisibleString.COMMA_AND_SPACE.toResourceString());
                        }
                        currentPage.openTag(HTML.Tag.CODE);
                        generateFunctionOrClassMethodReference(docComment.getNthFunctionOrClassMethodReference(i).getName());
                        currentPage.closeTag(HTML.Tag.CODE);
                    }
                }
            };
        }
        
        /// Author block
        //
        if (shouldGenerateAuthorInfo()) {
            generateHTMLForAttribute(StyleClassConstants.AUTHOR_BLOCK, LocalizableUserVisibleString.AUTHOR_COLON.toResourceString(), metadata.getAuthor(), valueFromAuthorBlocks, referenceGenerator, false);
        }
        
        /// Version block
        //
        if (shouldGenerateVersionInfo()) {
            generateHTMLForAttribute(StyleClassConstants.VERSION_BLOCK, LocalizableUserVisibleString.VERSION_COLON.toResourceString(), metadata.getVersion(), versionBlock, referenceGenerator, false);
        }
        
        /// See blocks
        //
        ContentConvertibleToHTML relatedReferencesFromMetadata = new ContentConvertibleToHTML() {
            private final int nRelatedFeatures = metadata.getNRelatedFeatures();
            /** {@inheritDoc} */
            @Override
            boolean isEmpty() {
                return nRelatedFeatures == 0;
            }
            /** {@inheritDoc} */
            @Override
            void generateHTML(HTMLBuilder currentPage, CALDocToHTMLUtilities.CrossReferenceHTMLGenerator referenceGenerator) {
                for (int i = 0; i < nRelatedFeatures; i++) {
                    if (i > 0) {
                        currentPage.addText(LocalizableUserVisibleString.COMMA_AND_SPACE.toResourceString());
                    }
                    currentPage.openTag(HTML.Tag.CODE);
                    generateCALFeatureReference(metadata.getNthRelatedFeature(i));
                    currentPage.closeTag(HTML.Tag.CODE);
                }
            }
        };
        
        generateHTMLForAttribute(StyleClassConstants.SEE_BLOCK, LocalizableUserVisibleString.SEE_ALSO_COLON.toResourceString(), relatedReferencesFromMetadata, valueFromSeeBlocks, referenceGenerator, false);
    }
    
    /**
     * Get the "best" short description from metadata. If the short description is available, that is taken.
     * Otherwise the first sentence of the long description is taken.
     * 
     * @param metadata the metadata from which a short description is to be extracted. Can be null.
     * @return the short description, or null if none is available.
     */
    private String getBestShortDescriptionFromMetadata(CALFeatureMetadata metadata) {
        if (metadata == null) {
            return null;
        }
        
        String shortDesc = metadata.getShortDescription();
        if (shortDesc != null && shortDesc.length() > 0) {
            return shortDesc;
        }
        
        String longDesc = metadata.getLongDescription();
        if (longDesc != null && longDesc.length() > 0) {
            return getFirstSentence(longDesc);
        }
        
        return null;
    }
    
    /**
     * Get the "best" description from metadata. If the long description is available, that is taken.
     * Otherwise the short description is taken.
     * 
     * @param metadata the metadata from which a description is to be extracted. Can be null.
     * @return the description, or null if none is available.
     */
    private String getBestDescriptionFromMetadata(CALFeatureMetadata metadata) {
        if (metadata == null) {
            return null;
        }
        
        String longDesc = metadata.getLongDescription();
        if (longDesc != null && longDesc.length() > 0) {
            return longDesc;
        }
        
        String shortDesc = metadata.getShortDescription();
        if (shortDesc != null && shortDesc.length() > 0) {
            return shortDesc;
        }
        
        return null;
    }
    
    /**
     * Generates the HTML for an "attribute block" - a block that starts with an emphasized header followed by the body.
     * @param styleClass the style class to use for the block.
     * @param header the header text. Can be null.
     * @param metadataValue the value obtained from metadata for the attribute block. Can be null.
     * @param textBlock the CALDoc text block for the attribute block. Can be null
     * @param referenceGenerator the reference generator to use for generating cross references.
     * @param generateEvenIfEmpty to generate the attribute block even when it is empty.
     */
    private void generateHTMLForAttribute(StyleClass styleClass, String header, String metadataValue, CALDocComment.TextBlock textBlock, ReferenceGenerator referenceGenerator, boolean generateEvenIfEmpty) {
        generateHTMLForAttribute(styleClass, header, metadataValue, new SingleTextBlockContent(textBlock, StyleClassConstants.CODE_BLOCK, CODE_FORMATTING_TAG), referenceGenerator, generateEvenIfEmpty);
    }

    /**
     * Generates the HTML for an "attribute block" - a block that starts with an emphasized header followed by the body.
     * @param styleClass the style class to use for the block.
     * @param header the header text. Can be null.
     * @param metadataValue the value obtained from metadata for the attribute block. Can be null.
     * @param valueFromCALDoc the value obtained from CALDoc for the attribute block. Can be null
     * @param referenceGenerator the reference generator to use for generating cross references.
     * @param generateEvenIfEmpty to generate the attribute block even when it is empty.
     */
    private void generateHTMLForAttribute(StyleClass styleClass, String header, String metadataValue, ContentConvertibleToHTML valueFromCALDoc, ReferenceGenerator referenceGenerator, boolean generateEvenIfEmpty) {
        generateHTMLForAttribute(styleClass, header, new SimpleStringContent(metadataValue), valueFromCALDoc, referenceGenerator, generateEvenIfEmpty);
    }
    
    /**
     * Generates the HTML for an "attribute block" - a block that starts with an emphasized header followed by the body.
     * @param styleClass the style class to use for the block.
     * @param header the header text. Can be null.
     * @param metadataValue the value obtained from metadata for the attribute block. Can be null.
     * @param valueFromCALDoc the value obtained from CALDoc for the attribute block. Can be null
     * @param referenceGenerator the reference generator to use for generating cross references.
     * @param generateEvenIfEmpty to generate the attribute block even when it is empty.
     */
    private void generateHTMLForAttribute(StyleClass styleClass, String header, ContentConvertibleToHTML metadataValue, ContentConvertibleToHTML valueFromCALDoc, ReferenceGenerator referenceGenerator, boolean generateEvenIfEmpty) {
    
        ////
        /// Figure out whether we need to generate a) metadata and b) CALDoc
        //
        boolean genMetadata = (shouldGenerateFromMetadata() && metadataValue != null && !metadataValue.isEmpty());
        
        boolean genCALDoc = ((shouldAlwaysGenerateFromCALDoc() || !genMetadata) && valueFromCALDoc != null && !valueFromCALDoc.isEmpty());
        
        if (generateEvenIfEmpty || genMetadata || genCALDoc) {
            currentPage.openTag(HTML.Tag.DIV, classAttribute(styleClass));
            
            /// If the header is not null, generate a new definition list and use it as the definition term.
            //
            if (header != null) {
                currentPage.openTag(HTML.Tag.DL).addTaggedText(HTML.Tag.DT, classAttribute(StyleClassConstants.ATTRIBUTE_HEADER), header).openTag(HTML.Tag.DD);
            }
            
            /// Generate the metadata, if required.
            //
            if (genMetadata) {
                currentPage.openTag(HTML.Tag.DIV);
                maybeGenerateMetadataIndicator();
                metadataValue.generateHTML(currentPage, referenceGenerator);
                currentPage.closeTag(HTML.Tag.DIV);
            }
            
            /// Generate the CALDoc, if required.
            //
            if (genCALDoc) {
                currentPage.openTag(HTML.Tag.DIV);
                maybeGenerateCALDocIndicator();
                valueFromCALDoc.generateHTML(currentPage, referenceGenerator);
                currentPage.closeTag(HTML.Tag.DIV);
            }
            
            /// Close the definition list if one was opened.
            //
            if (header != null) {
                currentPage.closeTag(HTML.Tag.DD).closeTag(HTML.Tag.DL);
            }
            
            currentPage.closeTag(HTML.Tag.DIV);
        }
    }

    /**
     * Generates an indicator denoting that the following block came from CALDoc if and only if metadata is to be included with the documentation.
     */
    private void maybeGenerateCALDocIndicator() {
        if (shouldGenerateFromMetadata()) {
            currentPage.addTaggedText(HTML.Tag.SPAN, classAttribute(StyleClassConstants.CALDOC_INDICATOR), LocalizableUserVisibleString.CALDOC_INDICATOR.toResourceString()).newline();
        }
    }

    /**
     * Generates an indicator denoting that the following block came from metadata if and only if metadata is to be included with the documentation.
     */
    private void maybeGenerateMetadataIndicator() {
        if (shouldGenerateFromMetadata()) {
            currentPage.addTaggedText(HTML.Tag.SPAN, classAttribute(StyleClassConstants.METADATA_INDICATOR), LocalizableUserVisibleString.METADATA_INDICATOR.toResourceString()).newline();
        }
    }

    /////====================================================================================================
    ////
    /// Helpers for generating type signatures, appropriately hyperlinked
    //
    
    /**
     * Generates a type signature with the type classes and type constructors contained therein appropriately hyperlinked.
     * @param typeSignature the type signature.
     */
    private void generateTypeSignature(SourceModel.TypeSignature typeSignature) {
        generateTypeSignature(typeSignature, new TypeSignatureHTMLGenerator(true));
    }
    
    /**
     * Generates a type signature with the specified type signature HTML generator.
     * @param typeSignature the type signature.
     * @param typeSigHTMLGenerator the type signature HTML generator to use for the generation.
     */
    private void generateTypeSignature(SourceModel.TypeSignature typeSignature, TypeSignatureHTMLGenerator typeSigHTMLGenerator) {
        currentPage.addTaggedText(HTML.Tag.CODE, " " + CALFragments.COLON_COLON + " ");
        currentPage.openTag(HTML.Tag.SPAN, classAttribute(StyleClassConstants.TYPE_SIGNATURE)).openTag(HTML.Tag.CODE);
        typeSignature.accept(typeSigHTMLGenerator, null);
        currentPage.closeTag(HTML.Tag.CODE).closeTag(HTML.Tag.SPAN);
    }
    
    /////====================================================================================================
    ////
    /// Helpers for constructing commonly used HTML attributes
    //
    
    /**
     * Constructs an HTML 'id' attribute.
     * @param id the id value. Can be null (an empty attribute list is returned).
     * @return a singleton attribute list.
     */
    private HTMLBuilder.AttributeList idAttribute(String id) {
        if (id == null) {
            return HTMLBuilder.AttributeList.make();
        } else {
            return HTMLBuilder.AttributeList.make(HTML.Attribute.ID, id);
        }
    }
    
    /**
     * Constructs an HTML 'class' attribute.
     * @param styleClass the style class value. Can be null (an empty attribute list is returned).
     * @return a singleton attribute list.
     */
    private HTMLBuilder.AttributeList classAttribute(StyleClass styleClass) {
        if (styleClass == null) {
            return HTMLBuilder.AttributeList.make();
        } else {
            return HTMLBuilder.AttributeList.make(HTML.Attribute.CLASS, styleClass.toHTML());
        }
    }
    
    /**
     * Constructs an HTML attribute list from the 'id' and 'class' attributes.
     * @param id the id value.
     * @param styleClass the style class value.
     * @return an attribute list with the two attributes. 
     */
    private HTMLBuilder.AttributeList idAndClassAttributes(String id, StyleClass styleClass) {
        return idAttribute(id).concat(classAttribute(styleClass));
    }

    /**
     * Constructs an HTML 'href' attribute referring to an anchor on the same page.
     * @param id the name of the anchor.
     * @return a singleton attribute list.
     */
    private HTMLBuilder.AttributeList localHrefAttribute(String id) {
        if (disableAllHyperlinks) {
            return HTMLBuilder.AttributeList.make();
        } else {
            return HTMLBuilder.AttributeList.make(HTML.Attribute.HREF, "#" + id);
        }
    }
    
    /**
     * Constructs an HTML 'href' attribute referring to another file.
     * @param filePath the path to the file being linked to.
     * @return a singleton attribute list.
     */
    private HTMLBuilder.AttributeList nonLocalHrefAttribute(String filePath) {
        return nonLocalHrefAttribute(filePath, null);
    }
    
    /**
     * Constructs an HTML 'href' attribute referring to another file, or an anchor in another file.
     * @param filePath the path to the file being linked to.
     * @param id the name of the anchor in the other file. Can be null.
     * @return a singleton attribute list.
     */
    private HTMLBuilder.AttributeList nonLocalHrefAttribute(String filePath, String id) {
        if (disableAllHyperlinks) {
            return HTMLBuilder.AttributeList.make();
        } else {
            if (id != null) {
                return HTMLBuilder.AttributeList.make(HTML.Attribute.HREF, filePath + "#" + id);
            } else {
                return HTMLBuilder.AttributeList.make(HTML.Attribute.HREF, filePath);
            }
        }
    }
    
    /**
     * Constructs an HTML attribute list for an overview table with the specified style class.
     * @param styleClass the style class to use for the table.
     * @return the appropriate attribute list.
     */
    private HTMLBuilder.AttributeList overviewTableAttributesWithStyleClass(StyleClass styleClass) {
        return classAttribute(styleClass)
            .concat(HTMLBuilder.AttributeList.make(HTML4.RULES_ATTRIBUTE, "rows"))
            .concat(HTMLBuilder.AttributeList.make(HTML.Attribute.CELLPADDING, "5"))
            .concat(HTMLBuilder.AttributeList.make(HTML.Attribute.CELLSPACING, "0"))
            .concat(HTMLBuilder.AttributeList.make(HTML.Attribute.BORDER, "1"))
            .concat(HTMLBuilder.AttributeList.make(HTML.Attribute.WIDTH, "100%"));
    }

    /**
     * Constructs an HTML attribute list for an outer overview table.
     * @return the appropriate attribute list.
     */
    private HTMLBuilder.AttributeList overviewTableAttributes() {
        return overviewTableAttributesWithStyleClass(StyleClassConstants.OVERVIEW_TABLE);
    }

    /**
     * Constructs an HTML attribute list for a nested overview table.
     * @return the appropriate attribute list.
     */
    private HTMLBuilder.AttributeList overviewNestedTableAttributes() {
        return overviewTableAttributesWithStyleClass(StyleClassConstants.OVERVIEW_NESTED_TABLE);
    }
    
    /**
     * Generates a link to the usages page if usage indices are to be generated.
     * @param usageSubdirectory the subdirectory for the usage page to link to.
     * @param usageIndexFileName the usage page file name to link to.
     */
    private void maybeGenerateUsageLink(String usageSubdirectory, String usageIndexFileName) {
        if (shouldGenerateUsageIndices) {
            currentPage
                .addText(" &nbsp;&nbsp;&nbsp;[")
                .addTaggedText(HTML.Tag.A, nonLocalHrefAttribute("../" + usageSubdirectory + "/" + usageIndexFileName), LocalizableUserVisibleString.USAGE_INDEX_ENTRY.toResourceString())
                .addText("]");
        }
    }
    
    /////====================================================================================================
    ////
    /// Helpers for generating argument names and hyperlinked references
    //
    
    /**
     * Generates an argument name of an FunctionalAgent. If metadata is to be included in documentation, this gives
     * preference to the name coming from the metadata. Otherwise, it gives preference to the name given in
     * CALDoc, if it is different from the name appearing in the entity itself.
     * 
     * @param entity the FunctionalAgent whose argument name is being generated.
     * @param index the position of the argument in the argument list.
     * @param argMetadata the argument's metadata. Can be null.
     * @param docComment the CALDoc comment of the entity. Can be null.
     * @param setOfArgumentNames the (Set of Strings) of argument names already used (for disambiguation purposes).
     */
    private void generateArgumentName(FunctionalAgent entity, int index, ArgumentMetadata argMetadata, CALDocComment docComment, Set<String> setOfArgumentNames) {
        ////
        /// First fetch the name from the entity. This will mostly be the same name as the one appearing in code, except for
        /// foreign functions, which may have their names extracted from the Java classes' debug info.
        //
        String nameFromEntity = null;
        if (index < entity.getNArgumentNames()) {
            nameFromEntity = entity.getArgumentName(index);
        }
        
        if (shouldGenerateFromMetadata() && argMetadata != null && argMetadata.getDisplayName() != null && argMetadata.getDisplayName().trim().length() > 0) {
            ////
            /// If metadata is to be included, and the metadata yields a non-empty display name for the argument, use it.
            //
            
            String nameFromMetadata = argMetadata.getDisplayName().trim();
            
            currentPage.addTaggedText(HTML.Tag.CODE, classAttribute(StyleClassConstants.ARG_NAME_FROM_METADATA), nameFromMetadata);
            setOfArgumentNames.add(nameFromMetadata);
            
        } else if (docComment != null && index < docComment.getNArgBlocks()) {
            ////
            /// Since either metadata is not to be included, or it is not available, we fallback to the CALDoc.
            //
            
            String nameFromCALDoc = docComment.getNthArgBlock(index).getArgName().getCalSourceForm();
            
            /// We decorate the argument name with the appropriate style class depending on whether the CALDoc argument name
            /// matches the one found in the entity (in which case the name came from code).
            //
            if (nameFromCALDoc.equals(nameFromEntity)) {
                currentPage.addTaggedText(HTML.Tag.CODE, classAttribute(StyleClassConstants.ARG_NAME_FROM_CODE), nameFromEntity);
                setOfArgumentNames.add(nameFromEntity);
                
            } else {
                currentPage.addTaggedText(HTML.Tag.CODE, classAttribute(StyleClassConstants.ARG_NAME_FROM_CALDOC), nameFromCALDoc);
                setOfArgumentNames.add(nameFromCALDoc);
            }
            
        } else if (nameFromEntity != null) {
            ////
            /// If the entity yielded a name, use it.
            //
            
            currentPage.addTaggedText(HTML.Tag.CODE, classAttribute(StyleClassConstants.ARG_NAME_FROM_CODE), nameFromEntity);
            setOfArgumentNames.add(nameFromEntity);
            
        } else {
            ////
            /// Since not even the entity yielded a name, construct an artificial one of the form arg_x, where x is the
            /// 1-based index of the argument in the argument list, or of the form arg_x_y if arg_x, arg_x_1, ...
            /// arg_x_(y-1) have all appeared previously in the argument list.
            //
            
            // the base artificial name we'll attempt to use is arg_x, where x is the 1-based index of this argument
            String baseArtificialName = "arg_" + (index + 1);
            String artificialName = baseArtificialName;
            
            // if the base artificial name already appears in previous arguments, then
            // make the argument name arg_x_y, where y is a supplementary disambiguating number
            // chosen so that the resulting name will not collide with any of the previous argument names
            
            int supplementaryDisambiguator = 1;
            while (setOfArgumentNames.contains(artificialName)) {
                artificialName = baseArtificialName + "_" + supplementaryDisambiguator;
                supplementaryDisambiguator++;
            }
            
            currentPage.addTaggedText(HTML.Tag.CODE, classAttribute(StyleClassConstants.ARG_NAME_ARTIFICIAL), artificialName);
            setOfArgumentNames.add(artificialName);
        }
    }
    
    /**
     * Generates a reference to another module, appropriately hyperlinked.
     * @param moduleName the name of the module to link to.
     */
    private void generateModuleReference(ModuleName moduleName) {
        generateModuleReference(currentPage, moduleName);
    }
    
    /**
     * Generates a reference to another module, appropriately hyperlinked.
     * @param builder the HTMLBuilder for generating the reference.
     * @param moduleName the name of the module to link to.
     */
    private void generateModuleReference(HTMLBuilder builder, ModuleName moduleName) {
        generateModuleReference(builder, null, moduleName, getMinimallyQualifiedNameForModule(moduleName));
    }
    
    /**
     * Generates a reference to another module, appropriately hyperlinked.
     * @param builder the HTMLBuilder for generating the reference.
     * @param relativeDirectory the relative directory path to get to the modules subdirectory. Can be null if the current directory is the modules subdirectory.
     * @param reference the name of the module to link to.
     */
    private void generateModuleReference(HTMLBuilder builder, String relativeDirectory, CALDocComment.ModuleReference reference) {
        ModuleName moduleName = reference.getName();
        String displayName = reference.getModuleNameInSource();
        if (displayName.length() == 0) {
            displayName = getMinimallyQualifiedNameForModule(moduleName);
        }
        generateModuleReference(builder, relativeDirectory, moduleName, displayName);
    }

    /**
     * Generates a reference to another module, appropriately hyperlinked.
     * @param builder the HTMLBuilder for generating the reference.
     * @param relativeDirectory the relative directory path to get to the modules subdirectory. Can be null if the current directory is the modules subdirectory.
     * @param moduleName the name of the module to link to.
     * @param moduleDisplayName the name to be displayed for the module.
     */
    private void generateModuleReference(HTMLBuilder builder, String relativeDirectory, ModuleName moduleName, String moduleDisplayName) {
        if (isDocForModuleGenerated(moduleName)) {
            generateNonLocalReference(builder, relativeDirectory, moduleName, labelMaker.getModuleLabel(moduleName), moduleDisplayName, getFullyQualifiedNameForModule(moduleName));
        } else {
            builder.addText(moduleDisplayName);
        }
    }
    
    /**
     * Generates a reference to a type constructor, appropriately hyperlinked.
     * @param qualifiedName the name of the type constructor to link to.
     */
    private void generateTypeConsReference(QualifiedName qualifiedName) {
        generateTypeConsReference(qualifiedName.getModuleName(), qualifiedName.getUnqualifiedName());
    }
    
    /**
     * Generates a reference to a type constructor, appropriately hyperlinked.
     * @param builder the HTMLBuilder for generating the reference.
     * @param relativeDirectory the relative directory path to get to the modules subdirectory. Can be null if the current directory is the modules subdirectory.
     * @param qualifiedName the name of the type constructor to link to.
     * @param moduleNameInSource how the module name portion of the reference appears in source. Could be the empty string if the reference is unqualified in source.
     */
    private void generateTypeConsReference(HTMLBuilder builder, String relativeDirectory, QualifiedName qualifiedName, String moduleNameInSource) {
        generateTypeConsReference(builder, relativeDirectory, qualifiedName.getModuleName(), qualifiedName.getUnqualifiedName(), moduleNameInSource);
    }
    
    /**
     * Generates a reference to a type constructor, appropriately hyperlinked.
     * @param moduleName the name of the type constructor's module. Can be null.
     * @param unqualifiedName the unqualified name of the type constructor.
     */
    private void generateTypeConsReference(ModuleName moduleName, String unqualifiedName) {
        generateTypeConsReference(currentPage, moduleName, unqualifiedName, getMinimallyQualifiedNameForModule(moduleName));
    }
    
    /**
     * Generates a reference to a type constructor, appropriately hyperlinked.
     * @param builder the HTMLBuilder for generating the reference.
     * @param moduleName the name of the type constructor's module. Can be null.
     * @param unqualifiedName the unqualified name of the type constructor.
     * @param moduleNameInSource how the module name portion of the reference appears in source. Could be the empty string if the reference is unqualified in source.
     */
    private void generateTypeConsReference(HTMLBuilder builder, ModuleName moduleName, String unqualifiedName, String moduleNameInSource) {
        generateTypeConsReference(builder, null, moduleName, unqualifiedName, moduleNameInSource);
    }

    /**
     * Generates a reference to a type constructor, appropriately hyperlinked.
     * @param builder the HTMLBuilder for generating the reference.
     * @param relativeDirectory the relative directory path to get to the modules subdirectory. Can be null if the current directory is the modules subdirectory.
     * @param moduleName the name of the type constructor's module. Can be null.
     * @param unqualifiedName the unqualified name of the type constructor.
     * @param moduleNameInSource how the module name portion of the reference appears in source. Could be the empty string if the reference is unqualified in source.
     */
    private void generateTypeConsReference(HTMLBuilder builder, String relativeDirectory, ModuleName moduleName, String unqualifiedName, String moduleNameInSource) {
        String typeConsLabel = labelMaker.getTypeConsLabel(unqualifiedName);
        String appropriatelyQualifiedName = getAppropriatelyQualifiedName(moduleName, unqualifiedName, moduleNameInSource);
        
        if (!isDocForTypeConsGenerated(moduleName, unqualifiedName)) {
            builder.addText(appropriatelyQualifiedName);
        } else if ((moduleName == null || moduleName.equals(currentModuleName)) && !inSeparateInstanceDoc) {
            generateLocalReference(builder, typeConsLabel, unqualifiedName, getFullyQualifiedNameString(moduleName, unqualifiedName));
        } else {
            generateNonLocalReference(builder, relativeDirectory, moduleName, typeConsLabel, appropriatelyQualifiedName, getFullyQualifiedNameString(moduleName, unqualifiedName));
        }
    }

    /**
     * Generates a reference to a data constructor, appropriately hyperlinked.
     * @param qualifiedName the name of the data constructor to link to.
     */
    private void generateDataConsReference(QualifiedName qualifiedName) {
        generateDataConsReference(qualifiedName.getModuleName(), qualifiedName.getUnqualifiedName());
    }
    
    /**
     * Generates a reference to a data constructor, appropriately hyperlinked.
     * @param builder the HTMLBuilder for generating the reference.
     * @param relativeDirectory the relative directory path to get to the modules subdirectory. Can be null if the current directory is the modules subdirectory.
     * @param qualifiedName the name of the data constructor to link to.
     * @param moduleNameInSource how the module name portion of the reference appears in source. Could be the empty string if the reference is unqualified in source.
     */
    private void generateDataConsReference(HTMLBuilder builder, String relativeDirectory, QualifiedName qualifiedName, String moduleNameInSource) {
        generateDataConsReference(builder, relativeDirectory, qualifiedName.getModuleName(), qualifiedName.getUnqualifiedName(), moduleNameInSource);
    }
    
    /**
     * Generates a reference to a data constructor, appropriately hyperlinked.
     * @param moduleName the name of the data constructor's module. Can be null.
     * @param unqualifiedName the unqualified name of the data constructor.
     */
    private void generateDataConsReference(ModuleName moduleName, String unqualifiedName) {
        generateDataConsReference(currentPage, moduleName, unqualifiedName, getMinimallyQualifiedNameForModule(moduleName));
    }
    
    /**
     * Generates a reference to a data constructor, appropriately hyperlinked.
     * @param builder the HTMLBuilder for generating the reference.
     * @param moduleName the name of the data constructor's module. Can be null.
     * @param unqualifiedName the unqualified name of the data constructor.
     * @param moduleNameInSource how the module name portion of the reference appears in source. Could be the empty string if the reference is unqualified in source.
     */
    private void generateDataConsReference(HTMLBuilder builder, ModuleName moduleName, String unqualifiedName, String moduleNameInSource) {
        generateDataConsReference(builder, null, moduleName, unqualifiedName, moduleNameInSource);
    }
    
    /**
     * Generates a reference to a data constructor, appropriately hyperlinked.
     * @param builder the HTMLBuilder for generating the reference.
     * @param relativeDirectory the relative directory path to get to the modules subdirectory. Can be null if the current directory is the modules subdirectory.
     * @param moduleName the name of the data constructor's module. Can be null.
     * @param unqualifiedName the unqualified name of the data constructor.
     * @param moduleNameInSource how the module name portion of the reference appears in source. Could be the empty string if the reference is unqualified in source.
     */
    private void generateDataConsReference(HTMLBuilder builder, String relativeDirectory, ModuleName moduleName, String unqualifiedName, String moduleNameInSource) {
        String dataConsLabel = labelMaker.getDataConsLabel(unqualifiedName);
        String appropriatelyQualifiedName = getAppropriatelyQualifiedName(moduleName, unqualifiedName, moduleNameInSource);
        
        if (!isDocForDataConsGenerated(moduleName, unqualifiedName)) {
            builder.addText(appropriatelyQualifiedName);
        } else if ((moduleName == null || moduleName.equals(currentModuleName)) && !inSeparateInstanceDoc) {
            generateLocalReference(builder, dataConsLabel, unqualifiedName, getFullyQualifiedNameString(moduleName, unqualifiedName));
        } else {
            generateNonLocalReference(builder, relativeDirectory, moduleName, dataConsLabel, appropriatelyQualifiedName, getFullyQualifiedNameString(moduleName, unqualifiedName));
        }
    }

    /**
     * Generates a reference to a function or class method, appropriately hyperlinked.
     * @param qualifiedName the name of the function or class method to link to.
     */
    private void generateFunctionOrClassMethodReference(QualifiedName qualifiedName) {
        generateFunctionOrClassMethodReference(qualifiedName.getModuleName(), qualifiedName.getUnqualifiedName());
    }
    
    /**
     * Generates a reference to a function or class method, appropriately hyperlinked.
     * @param builder the HTMLBuilder for generating the reference.
     * @param relativeDirectory the relative directory path to get to the modules subdirectory. Can be null if the current directory is the modules subdirectory.
     * @param qualifiedName the name of the function or class method to link to.
     * @param moduleNameInSource how the module name portion of the reference appears in source. Could be the empty string if the reference is unqualified in source.
     */
    private void generateFunctionOrClassMethodReference(HTMLBuilder builder, String relativeDirectory, QualifiedName qualifiedName, String moduleNameInSource) {
        generateFunctionOrClassMethodReference(builder, relativeDirectory, qualifiedName.getModuleName(), qualifiedName.getUnqualifiedName(), moduleNameInSource);
    }
    
    /**
     * Generates a reference to a function or class method, appropriately hyperlinked.
     * @param moduleName the name of the function or class method's module. Can be null.
     * @param unqualifiedName the unqualified name of the function or class method.
     */
    private void generateFunctionOrClassMethodReference(ModuleName moduleName, String unqualifiedName) {
        generateFunctionOrClassMethodReference(currentPage, moduleName, unqualifiedName, getMinimallyQualifiedNameForModule(moduleName));
    }
    
    /**
     * Generates a reference to a function or class method, appropriately hyperlinked.
     * @param builder the HTMLBuilder for generating the reference.
     * @param moduleName the name of the function or class method's module. Can be null.
     * @param unqualifiedName the unqualified name of the function or class method.
     * @param moduleNameInSource how the module name portion of the reference appears in source. Could be the empty string if the reference is unqualified in source.
     */
    private void generateFunctionOrClassMethodReference(HTMLBuilder builder, ModuleName moduleName, String unqualifiedName, String moduleNameInSource) {
        generateFunctionOrClassMethodReference(builder, null, moduleName, unqualifiedName, moduleNameInSource);
    }

    /**
     * Generates a reference to a function or class method, appropriately hyperlinked.
     * @param builder the HTMLBuilder for generating the reference.
     * @param relativeDirectory the relative directory path to get to the modules subdirectory. Can be null if the current directory is the modules subdirectory.
     * @param moduleName the name of the function or class method's module. Can be null.
     * @param unqualifiedName the unqualified name of the function or class method.
     * @param moduleNameInSource how the module name portion of the reference appears in source. Could be the empty string if the reference is unqualified in source.
     */
    private void generateFunctionOrClassMethodReference(HTMLBuilder builder, String relativeDirectory, ModuleName moduleName, String unqualifiedName, String moduleNameInSource) {
        String functionOrClassMethodLabel = labelMaker.getFunctionOrClassMethodLabel(unqualifiedName);
        String appropriatelyQualifiedName = getAppropriatelyQualifiedName(moduleName, unqualifiedName, moduleNameInSource);
        
        if (!isDocForFunctionOrClassMethodGenerated(moduleName, unqualifiedName)) {
            builder.addText(appropriatelyQualifiedName);
        } else if ((moduleName == null || moduleName.equals(currentModuleName)) && !inSeparateInstanceDoc) {
            generateLocalReference(builder, functionOrClassMethodLabel, unqualifiedName, getFullyQualifiedNameString(moduleName, unqualifiedName));
        } else {
            generateNonLocalReference(builder, relativeDirectory, moduleName, functionOrClassMethodLabel, appropriatelyQualifiedName, getFullyQualifiedNameString(moduleName, unqualifiedName));
        }
    }
    
    /**
     * Generates a reference to a type class, appropriately hyperlinked.
     * @param qualifiedName the name of the type class to link to.
     */
    private void generateTypeClassReference(QualifiedName qualifiedName) {
        generateTypeClassReference(qualifiedName.getModuleName(), qualifiedName.getUnqualifiedName());
    }
    
    /**
     * Generates a reference to a type class, appropriately hyperlinked.
     * @param builder the HTMLBuilder for generating the reference.
     * @param relativeDirectory the relative directory path to get to the modules subdirectory. Can be null if the current directory is the modules subdirectory.
     * @param qualifiedName the name of the type class to link to.
     * @param moduleNameInSource how the module name portion of the reference appears in source. Could be the empty string if the reference is unqualified in source.
     */
    private void generateTypeClassReference(HTMLBuilder builder, String relativeDirectory, QualifiedName qualifiedName, String moduleNameInSource) {
        generateTypeClassReference(builder, relativeDirectory, qualifiedName.getModuleName(), qualifiedName.getUnqualifiedName(), moduleNameInSource);
    }
    
    /**
     * Generates a reference to a type class, appropriately hyperlinked.
     * @param moduleName the name of the type class's module. Can be null.
     * @param unqualifiedName the unqualified name of the type class.
     */
    private void generateTypeClassReference(ModuleName moduleName, String unqualifiedName) {
        generateTypeClassReference(currentPage, moduleName, unqualifiedName, getMinimallyQualifiedNameForModule(moduleName));
    }
    
    /**
     * Generates a reference to a type class, appropriately hyperlinked.
     * @param builder the HTMLBuilder for generating the reference.
     * @param moduleName the name of the type class's module. Can be null.
     * @param unqualifiedName the unqualified name of the type class.
     * @param moduleNameInSource how the module name portion of the reference appears in source. Could be the empty string if the reference is unqualified in source.
     */
    private void generateTypeClassReference(HTMLBuilder builder, ModuleName moduleName, String unqualifiedName, String moduleNameInSource) {
        generateTypeClassReference(builder, null, moduleName, unqualifiedName, moduleNameInSource);
    }

    /**
     * Generates a reference to a type class, appropriately hyperlinked.
     * @param builder the HTMLBuilder for generating the reference.
     * @param relativeDirectory the relative directory path to get to the modules subdirectory. Can be null if the current directory is the modules subdirectory.
     * @param moduleName the name of the type class's module. Can be null.
     * @param unqualifiedName the unqualified name of the type class.
     * @param moduleNameInSource how the module name portion of the reference appears in source. Could be the empty string if the reference is unqualified in source.
     */
    private void generateTypeClassReference(HTMLBuilder builder, String relativeDirectory, ModuleName moduleName, String unqualifiedName, String moduleNameInSource) {
        String typeClassLabel = labelMaker.getTypeClassLabel(unqualifiedName);
        String appropriatelyQualifiedName = getAppropriatelyQualifiedName(moduleName, unqualifiedName, moduleNameInSource);
        
        if (!isDocForTypeClassGenerated(moduleName, unqualifiedName)) {
            builder.addText(appropriatelyQualifiedName);
        } else if ((moduleName == null || moduleName.equals(currentModuleName)) && !inSeparateInstanceDoc) {
            generateLocalReference(builder, typeClassLabel, unqualifiedName, getFullyQualifiedNameString(moduleName, unqualifiedName));
        } else {
            generateNonLocalReference(builder, relativeDirectory, moduleName, typeClassLabel, appropriatelyQualifiedName, getFullyQualifiedNameString(moduleName, unqualifiedName));
        }
    }
    
    /**
     * Generates a reference to a class instance, appropriately hyperlinked.
     * @param classInstance the class instance to link to.
     */
    private void generateClassInstanceReference(ClassInstance classInstance) {
        ModuleName definingModuleName = classInstance.getModuleName();
        ClassInstanceIdentifier identifier = classInstance.getIdentifier();
        String classInstanceLabel = labelMaker.getClassInstanceLabel(identifier);
        
        if (!isDocForClassInstanceGenerated(classInstance)) {
            // if the class instance is not documented, no hyperlink can be made.
            generateClassInstanceDeclarationName(classInstance, false);
            
        } else {
            // the instance is indeed documented, so create a hyperlinked reference to it
            
            HTMLBuilder.AttributeList hrefAttribute;
            if (config.shouldSeparateInstanceDoc) {
                if (inSeparateInstanceDoc) {
                    if (definingModuleName == null || definingModuleName.equals(currentModuleName)) {
                        hrefAttribute = localHrefAttribute(classInstanceLabel);
                    } else {
                        hrefAttribute = nonLocalHrefAttribute(getSeparateInstanceDocFileName(definingModuleName), classInstanceLabel);
                    }
                } else {
                    hrefAttribute = nonLocalHrefAttribute("../" + SEPARATE_INSTANCE_DOC_SUBDIRECTORY + "/" + getSeparateInstanceDocFileName(definingModuleName), classInstanceLabel);
                }
            } else {
                if (definingModuleName == null || definingModuleName.equals(currentModuleName)) {
                    hrefAttribute = localHrefAttribute(classInstanceLabel);
                } else {
                    hrefAttribute = nonLocalHrefAttribute(getModuleFileName(definingModuleName), classInstanceLabel);
                }
            }
            
            currentPage.openTag(HTML.Tag.A, hrefAttribute);
            generateClassInstanceDeclarationName(classInstance, false);
            currentPage.closeTag(HTML.Tag.A);
        }
    }
    
    /**
     * Returns an href attribute for a non local reference to a class instance.
     * @param classInstance the class instance to link to.
     * @param relativePathToBaseDirectory the relative path to the base directory for documentation generation.
     * @return the href attribute.
     */
    private HTMLBuilder.AttributeList getHrefAttributeForNonLocalClassInstanceReference(ClassInstance classInstance, String relativePathToBaseDirectory) {
        ModuleName definingModuleName = classInstance.getModuleName();
        ClassInstanceIdentifier identifier = classInstance.getIdentifier();
        String classInstanceLabel = labelMaker.getClassInstanceLabel(identifier);
        
        HTMLBuilder.AttributeList hrefAttribute;
        if (config.shouldSeparateInstanceDoc) {
            hrefAttribute = nonLocalHrefAttribute(relativePathToBaseDirectory + SEPARATE_INSTANCE_DOC_SUBDIRECTORY + "/" + getSeparateInstanceDocFileName(definingModuleName), classInstanceLabel);
        } else {
            hrefAttribute = nonLocalHrefAttribute(relativePathToBaseDirectory + MODULES_SUBDIRECTORY + "/" + getModuleFileName(definingModuleName), classInstanceLabel);
        }
        
        return hrefAttribute;
    }
    
    /**
     * Generates a reference to a class instance, appropriately hyperlinked.
     * @param identifier the class instance identifier.
     * @param definingModuleName the name of the module in which the instance is defined.
     */
    private void generateClassInstanceReference(ClassInstanceIdentifier identifier, ModuleName definingModuleName) {
        ClassInstance classInstance = programModelManager.getModuleTypeInfo(definingModuleName).getClassInstance(identifier);
        generateClassInstanceReference(classInstance);
    }
    
    /**
     * Generates a reference to an instance method, appropriately hyperlinked.
     * @param classInstance the class instance of the instance method.
     * @param methodName the name of the instance method.
     */
    private void generateInstanceMethodReference(ClassInstance classInstance, String methodName) {
        ModuleName definingModuleName = classInstance.getModuleName();
        ClassInstanceIdentifier identifier = classInstance.getIdentifier();
        String instanceMethodLabel = labelMaker.getInstanceMethodLabel(identifier, methodName);
        
        if (!isDocForClassInstanceGenerated(classInstance)) {
            // if the class instance is not documented, no hyperlink can be made.
            generateClassInstanceDeclarationName(classInstance, false);
            currentPage.addText(" ").addText(methodName);
            
        } else {
            // the instance is indeed documented, so create a hyperlinked reference to the instance method
            
            HTMLBuilder.AttributeList hrefAttribute;
            
            if (config.shouldSeparateInstanceDoc) {
                if (inSeparateInstanceDoc) {
                    if (definingModuleName == null || definingModuleName.equals(currentModuleName)) {
                        hrefAttribute = localHrefAttribute(instanceMethodLabel);
                    } else {
                        hrefAttribute = nonLocalHrefAttribute(getSeparateInstanceDocFileName(definingModuleName), instanceMethodLabel);
                    }
                } else {
                    hrefAttribute = nonLocalHrefAttribute("../" + SEPARATE_INSTANCE_DOC_SUBDIRECTORY + "/" + getSeparateInstanceDocFileName(definingModuleName), instanceMethodLabel);
                }
            } else {
                if (definingModuleName == null || definingModuleName.equals(currentModuleName)) {
                    hrefAttribute = localHrefAttribute(instanceMethodLabel);
                } else {
                    hrefAttribute = nonLocalHrefAttribute(getModuleFileName(definingModuleName), instanceMethodLabel);
                }
            }
            
            currentPage.openTag(HTML.Tag.A, hrefAttribute);
            generateClassInstanceDeclarationName(classInstance, false);
            currentPage.addText(" ").addText(methodName).closeTag(HTML.Tag.A);
        }
    }
    
    /**
     * Generates a reference to an instance method, appropriately hyperlinked.
     * @param identifier the class instance identifier.
     * @param definingModuleName the name of the module in which the instance is defined.
     * @param methodName the name of the instance method.
     */
    private void generateInstanceMethodReference(ClassInstanceIdentifier identifier, ModuleName definingModuleName, String methodName) {
        ClassInstance classInstance = programModelManager.getModuleTypeInfo(definingModuleName).getClassInstance(identifier);
        generateInstanceMethodReference(classInstance, methodName);
    }
    
    /**
     * Returns a qualified name constructed from a <em>fully qualified</em> module name and an unqualified name.
     * @param moduleName the name of the module.
     * @param unqualifiedName the unqualified name of the entity.
     * @return the fully qualified name as a string.
     */
    private String getFullyQualifiedNameString(ModuleName moduleName, String unqualifiedName) {
        return getQualifiedNameString(getFullyQualifiedNameForModule(moduleName), unqualifiedName);
    }
    
    /**
     * Returns a qualified name constructed from a module name and an unqualified name.
     * @param moduleNameString the name of the module.
     * @param unqualifiedName the unqualified name of the entity.
     * @return the qualified name as a string.
     */
    private String getQualifiedNameString(String moduleNameString, String unqualifiedName) {
        return moduleNameString + CALFragments.DOT + unqualifiedName;
    }
    
    /**
     * Constructs an appropriately qualified version of the given entity name. For the purpose of documentation generation,
     * all references to entities outside the current module are to be fully qualified. The exception is with Prelude
     * names, which may optionally remain unqualified depending on the configuration of this generator.
     * 
     * @param moduleName the name of the entity's module.
     * @param unqualifiedName the unqualified name of the entity.
     * @return an appropriately qualified version of the given name.
     */
    private String getAppropriatelyQualifiedName(ModuleName moduleName, String unqualifiedName) {
        return getAppropriatelyQualifiedName(moduleName, unqualifiedName, getMinimallyQualifiedNameForModule(moduleName));
    }

    /**
     * Constructs an appropriately qualified version of the given entity name. For the purpose of documentation generation,
     * all references to entities outside the current module are to be fully qualified. The exception is with Prelude
     * names, which may optionally remain unqualified depending on the configuration of this generator.
     * 
     * @param moduleName the name of the entity's module.
     * @param unqualifiedName the unqualified name of the entity.
     * @param moduleNameInSource how the module name portion of the reference appears in source. Could be the empty string if the reference is unqualified in source.
     * @return an appropriately qualified version of the given name.
     */
    private String getAppropriatelyQualifiedName(ModuleName moduleName, String unqualifiedName, String moduleNameInSource) {
        if (moduleName == null || moduleName.equals(currentModuleName)) {
            return unqualifiedName;
        } else if (shouldDisplayPreludeNamesAsUnqualified() && moduleName.equals(CAL_Prelude.MODULE_NAME)) {
            return unqualifiedName;
        } else if (moduleNameInSource == null || moduleNameInSource.length() == 0) {
            return unqualifiedName;
        } else {
            return getQualifiedNameString(moduleNameInSource, unqualifiedName);
        }
    }
    
    /**
     * Returns the minimally-qualified name for a module (with respect to the names of all the documented modules).
     * @param moduleName the name of the module.
     * @return the minimally-qualified form of the name.
     */
    private String getMinimallyQualifiedNameForModule(ModuleName moduleName) {
        return getModuleNameResolverForDocumentedModules().getMinimallyQualifiedModuleName(moduleName).toSourceText();
    }

    /**
     * Returns the fully-qualified name for a module.
     * @param moduleName the name of the module.
     * @return the fully-qualified form of the name.
     */
    private String getFullyQualifiedNameForModule(ModuleName moduleName) {
        return moduleName.toSourceText();
    }

    /**
     * Generates the full name of a type class (i.e. including its parent classes), appropriately hyperlinked if requested. 
     * @param typeClass the type class whose name is to be generated.
     * @param shouldGenerateHyperlinks whether hyperlinks should be generated.
     * @param styleClassForTypeClassName the style class to use for the type class name.
     */
    private void generateTypeClassFullName(TypeClass typeClass, boolean shouldGenerateHyperlinks, StyleClass styleClassForTypeClassName) {
        int nParentClasses = typeClass.getNParentClasses();
        
        if (nParentClasses > 0) {
            ////
            /// Loop through each parent class and generate its name, appropriately hyperlinked.
            //
            currentPage.openTag(HTML.Tag.SPAN, classAttribute(StyleClassConstants.TYPE_CONSTRAINT)).addText(CALFragments.OPEN_PAREN);
            
            for (int i = 0; i < nParentClasses; i++) {
                if (i > 0) {
                    currentPage.addText(CALFragments.COMMA_AND_SPACE);
                }
                
                QualifiedName parentClassName = typeClass.getNthParentClass(i).getName();
                
                if (shouldGenerateHyperlinks) {
                    generateTypeClassReference(parentClassName);
                } else {
                    currentPage.addText(getAppropriatelyQualifiedName(parentClassName.getModuleName(), parentClassName.getUnqualifiedName()));
                }
                
                currentPage.addText("&nbsp;" + CALFragments.STANDARD_TYPE_VAR);
            }
            
            currentPage.addText(CALFragments.CLOSE_PAREN).addText(" ").addTaggedText(HTML.Tag.SPAN, classAttribute(StyleClassConstants.CAL_SYMBOL), CALFragments.IMPLIES).closeTag(HTML.Tag.SPAN).addText(" ");
        }
        
        /// Finally generate the name of the type class itself.
        //
        currentPage.addTaggedText(HTML.Tag.SPAN, classAttribute(styleClassForTypeClassName), typeClass.getName().getUnqualifiedName() + "&nbsp;" + CALFragments.STANDARD_TYPE_VAR);
    }
    
    /**
     * Generates the declaration name for a class instance, appropriately hyperlinked if requested.
     * @param classInstance the class instance whose name is to be generated. 
     * @param shouldGenerateHyperlinks whether hyperlinks should be generated.
     */
    private void generateClassInstanceDeclarationName(ClassInstance classInstance, boolean shouldGenerateHyperlinks) {
        TypeClass typeClass = classInstance.getTypeClass();
        TypeExpr instanceType = classInstance.getType();
        
        QualifiedName typeClassName = typeClass.getName();       
        
        SourceModel.TypeSignature typeSig = instanceType.toSourceModel(true, ScopedEntityNamingPolicy.FULLY_QUALIFIED);
        SourceModel.TypeExprDefn typeExprDefn = typeSig.getTypeExprDefn();
        
        TypeSignatureHTMLGenerator visitor = new TypeSignatureHTMLGenerator(shouldGenerateHyperlinks);
        
        /// First generate the constraints from the instance type signature - they form the constraints on the instance.
        //
        visitor.generateConstraintsFromSignature(typeSig, null);
        
        /// Then generate the instance type class name.
        //
        if (shouldGenerateHyperlinks) {
            generateTypeClassReference(typeClassName);
        } else {
            currentPage.addText(getAppropriatelyQualifiedName(typeClassName.getModuleName(), typeClassName.getUnqualifiedName()));
        }
        
        currentPage.addText(" ");
        
        /// Finally, generate the remaining type expression portion of the instance type, parenthesized as needed
        // (e.g. 'Eq (Maybe a)' or 'Show (a -> b)', but simply 'Ord Int')
        //
        if (typeExprDefn instanceof SourceModel.TypeExprDefn.Application || typeExprDefn instanceof SourceModel.TypeExprDefn.Function) {
            currentPage.addText(CALFragments.OPEN_PAREN);
            typeExprDefn.accept(visitor, null);
            currentPage.addText(CALFragments.CLOSE_PAREN);
        } else {
            typeExprDefn.accept(visitor, null);
        }
    }

    /**
     * Generates a reference to a local label with the given display name for the link.
     * @param label the label for the reference. Must not be null.
     * @param displayName the display name.
     * @param tooltipText text for the tooltip. Can be null.
     */
    private void generateLocalReference(String label, String displayName, String tooltipText) {
        generateLocalReference(currentPage, label, displayName, tooltipText);
    }
    
    /**
     * Generates a reference to a local label with the given display name for the link.
     * @param builder the HTMLBuilder for generating the reference.
     * @param label the label for the reference. Must not be null.
     * @param displayName the display name.
     * @param tooltipText text for the tooltip. Can be null.
     */
    private void generateLocalReference(HTMLBuilder builder, String label, String displayName, String tooltipText) {
        HTMLBuilder.AttributeList attribute = localHrefAttribute(label);
        if (tooltipText != null) {
            attribute = attribute.concat(HTMLBuilder.AttributeList.make(HTML.Attribute.TITLE, tooltipText));
        }
        builder.addTaggedText(HTML.Tag.A, attribute, displayName);
    }
    
    /**
     * Generates a reference to another module, or to a label in another module, with the given display name for the link.
     * @param builder the HTMLBuilder for generating the reference.
     * @param relativeDirectory the relative directory path to get to the modules subdirectory. Can be null if the current directory is the modules subdirectory.
     * @param moduleName the name of the other module.
     * @param label the label for the reference. Can be null if the reference is simple to the module's page.
     * @param displayName the display name.
     * @param tooltipText text for the tooltip. Can be null.
     */
    private void generateNonLocalReference(HTMLBuilder builder, String relativeDirectory, ModuleName moduleName, String label, String displayName, String tooltipText) {
        String filePath;
        
        if (inSeparateInstanceDoc) {
            // fix up the relative directory if we are in separate instance documentation.. we are in the right module,
            // but the wrong subdirectory
            if (relativeDirectory == null) {
                relativeDirectory = "../" + MODULES_SUBDIRECTORY;
            }
        }
        
        if (relativeDirectory != null) {
            filePath = relativeDirectory + "/" + getModuleFileName(moduleName);
        } else {
            filePath = getModuleFileName(moduleName);
        }
        
        HTMLBuilder.AttributeList attribute = nonLocalHrefAttribute(filePath, label);
        if (tooltipText != null) {
            attribute = attribute.concat(HTMLBuilder.AttributeList.make(HTML.Attribute.TITLE, tooltipText));
        }
        builder.addTaggedText(HTML.Tag.A, attribute, displayName);
    }
    
    /**
     * Generates a reference to a CAL feature, appropriately hyperlinked.
     * @param featureName the name of the CAL feature.
     */
    private void generateCALFeatureReference(CALFeatureName featureName) {
        FeatureName.FeatureType type = featureName.getType();
        
        if (type == CALFeatureName.FUNCTION) {
            generateFunctionOrClassMethodReference(QualifiedName.makeFromCompoundName(featureName.getName()));
            
        } else if (type == CALFeatureName.TYPE_CONSTRUCTOR) {
            generateTypeConsReference(QualifiedName.makeFromCompoundName(featureName.getName()));
            
        } else if (type == CALFeatureName.TYPE_CLASS) {
            generateTypeClassReference(QualifiedName.makeFromCompoundName(featureName.getName()));
            
        } else if (type == CALFeatureName.DATA_CONSTRUCTOR) {
            generateDataConsReference(QualifiedName.makeFromCompoundName(featureName.getName()));
            
        } else if (type == CALFeatureName.CLASS_METHOD) {
            generateFunctionOrClassMethodReference(QualifiedName.makeFromCompoundName(featureName.getName()));
            
        } else if (type == CALFeatureName.MODULE) {
            generateModuleReference(featureName.toModuleName());
        
        } else if (type == CALFeatureName.CLASS_INSTANCE) {
            ClassInstanceIdentifier identifier = featureName.toInstanceIdentifier();
            ModuleName definingModuleName = featureName.toModuleName();
            generateClassInstanceReference(identifier, definingModuleName);
            
        } else if (type == CALFeatureName.INSTANCE_METHOD) {
            ClassInstanceIdentifier identifier = featureName.toInstanceIdentifier();
            ModuleName definingModuleName = featureName.toModuleName();
            String methodName = featureName.toInstanceMethodName();
            generateInstanceMethodReference(identifier, definingModuleName, methodName);
            
        } else {
            throw new IllegalArgumentException("feature type not supported: " + type);
        }
    }

    /////====================================================================================================
    ////
    /// Helpers for generating the details documentation for a functional agent
    //
    
    /**
     * Generates the detailed documentation for a functional agent (function, class method, or data constructor).
     * @param entity the FunctionalAgent of the functional agent.
     * @param label the label to be used to label the generated documentation block.
     * @param metadata the entity's metadata. Can be null if not available.
     * @param docComment the entity's CALDoc comment. Can be null if not available.
     * @param indexInList the position of the generated documentation in its enclosing list.
     * @param shouldDisplayReturnBlock whether the return block should be displayed.
     */
    private void generateFunctionalAgentDoc(FunctionalAgent entity, String label, FunctionalAgentMetadata metadata, CALDocComment docComment, int indexInList, boolean shouldDisplayReturnBlock) {
        generateFunctionalAgentDoc(entity.getName().getUnqualifiedName(), entity, label, metadata, docComment, indexInList, shouldDisplayReturnBlock);
    }

    /**
     * Generates the detailed documentation for a functional agent (function, class method, or data constructor).
     * @param unqualifiedName the unqualified name for the functional agent.
     * @param entity the FunctionalAgent of the functional agent.
     * @param label the label to be used to label the generated documentation block.
     * @param metadata the entity's metadata. Can be null if not available.
     * @param docComment the entity's CALDoc comment. Can be null if not available.
     * @param indexInList the position of the generated documentation in its enclosing list.
     * @param shouldDisplayReturnBlock whether the return block should be displayed.
     */
    /*
     * @implementation this method is exposed via package scope to allow reuse by the CALDocToTooltipHTMLUtilities
     */ 
    void generateFunctionalAgentDoc(String unqualifiedName, FunctionalAgent entity, String label, FunctionalAgentMetadata metadata, CALDocComment docComment, int indexInList, boolean shouldDisplayReturnBlock) {
        Scope scope = entity.getScope();
        TypeExpr typeExpr = entity.getTypeExpr();
        ArgumentMetadata[] argMetadataArray = metadata.getArguments();
        
        generateFunctionalAgentOrInstanceMethodDocHeading(scope, unqualifiedName, entity, typeExpr, label, argMetadataArray, docComment, indexInList, false);
        
        generateFunctionalAgentOrInstanceMethodDocBody(scope, entity, typeExpr, metadata, argMetadataArray, docComment, docComment, docComment, shouldDisplayReturnBlock);
    }

    /**
     * Generates the heading portion of the detailed documentation for a functional agent or an instance method.
     * @param scope the scope of the documented functional agent. Can be null in the case of an instance method.
     * @param unqualifiedName the unqualified name of the documented functional agent or instance method.
     * @param entityWithArgumentNames the FunctionalAgent object from which argument names are to be extracted.
     * @param typeExpr the type expression of the functional agent or instance method.
     * @param label the label to be used to label the generated block.
     * @param argMetadataArray the array of argument metadata containing argument names.
     * @param docCommentForArguments the CALDoc comment potentially containing argument names.
     * @param indexInList the position of the generated documentation in its enclosing list.
     * @param isInstanceMethod whether the documentation is for an instance method.
     */
    private void generateFunctionalAgentOrInstanceMethodDocHeading(Scope scope, String unqualifiedName, FunctionalAgent entityWithArgumentNames, TypeExpr typeExpr, String label, ArgumentMetadata[] argMetadataArray, CALDocComment docCommentForArguments, int indexInList, boolean isInstanceMethod) {
        int nArgs = typeExpr.getArity();
        
        ////
        /// Generate the name and type of the functional agent/instance method
        //
        currentPage
            .openTag(HTML.Tag.DT, idAndClassAttributes(label, getDefinitionStartStyleClass(indexInList)))
            .openTag(HTML.Tag.SPAN, classAttribute(getScopeStyleClass(scope)))
            .openTag(HTML.Tag.SPAN, classAttribute(StyleClassConstants.DEFINITION_HEADER_FIRST_LINE))
            .openTag(HTML.Tag.SPAN, classAttribute(StyleClassConstants.NAME_AND_TYPE))
            .addTaggedText(HTML.Tag.SPAN, classAttribute(StyleClassConstants.DEFINITION_HEADER), unqualifiedName);
        
        generateTypeSignature(typeExpr.toSourceModel(true, ScopedEntityNamingPolicy.FULLY_QUALIFIED));
        
        currentPage
            .closeTag(HTML.Tag.SPAN)
            .closeTag(HTML.Tag.SPAN);
        
        if (!isInstanceMethod) {
            maybeGenerateClassMethodIndicator(entityWithArgumentNames);
        }
        
        currentPage
            .openTag(HTML.Tag.CODE, classAttribute(StyleClassConstants.DECLARATION));
        
        ////
        /// Generate the declaration containing the name and the arguments
        //
        if (scope != null) {
            currentPage.addTaggedText(HTML.Tag.SPAN, classAttribute(StyleClassConstants.SCOPE), scope.toString()).addText(" ");
        }
        
        currentPage
            .addTaggedText(HTML.Tag.SPAN, classAttribute(StyleClassConstants.DECLARED_NAME), unqualifiedName);
        
        Set<String> setOfArgumentNames = new HashSet<String>();
        
        for (int i = 0 ; i < nArgs; i++) {
            currentPage.addText(" ");
            ArgumentMetadata argMetadata = (i < argMetadataArray.length) ? argMetadataArray[i] : null;
            generateArgumentName(entityWithArgumentNames, i, argMetadata, docCommentForArguments, setOfArgumentNames);
        }
        
        currentPage.closeTag(HTML.Tag.CODE);
        
        currentPage.closeTag(HTML.Tag.SPAN).closeTag(HTML.Tag.DT);
    }
    /**
     * Generates the body portion of the detailed documentation for a functional agent or an instance method.
     * @param scope the scope of the documented functional agent. Can be null in the case of an instance method.
     * @param entityWithArgumentNames the FunctionalAgent object from which argument names are to be extracted.
     * @param typeExpr the type expression of the functional agent or instance method.
     * @param metadata the metadata from which attributes and their values are to be extracted. 
     * @param argMetadataArray the array of argument metadata containing argument names.
     * @param docComment the CALDoc comment from which documentation blocks are to be extracted.
     * @param docCommentForArguments the CALDoc comment potentially containing argument names.
     * @param docCommentForReturnValue the CALDoc comment from which the return value description is to be extracted.
     * @param shouldDisplayReturnBlock whether the return block should be displayed.
     */
    private void generateFunctionalAgentOrInstanceMethodDocBody(Scope scope, FunctionalAgent entityWithArgumentNames, TypeExpr typeExpr, CALFeatureMetadata metadata, ArgumentMetadata[] argMetadataArray, CALDocComment docComment, CALDocComment docCommentForArguments, CALDocComment docCommentForReturnValue, boolean shouldDisplayReturnBlock) {
        int nArgs = typeExpr.getArity();
        
        currentPage.openTag(HTML.Tag.DD, classAttribute(getScopeStyleClass(scope)));
        
        ////
        /// First, generate the description
        //
        generateStandardDescription(metadata, docComment, inModulesSubdirectoryReferenceGenerator);             
        
        ////
        /// Then generate the arguments
        //
        if (nArgs > 0) {
            currentPage
                .openTag(HTML.Tag.DL)
                .addTaggedText(HTML.Tag.DT, classAttribute(StyleClassConstants.ATTRIBUTE_HEADER), LocalizableUserVisibleString.ARGUMENTS_COLON.toResourceString())
                .openTag(HTML.Tag.DD);
        }
        
        TypeExpr[] typePieces = typeExpr.getTypePieces();
        SourceModel.TypeSignature[] typePieceSignatures = TypeExpr.toSourceModelArray(typePieces, true, ScopedEntityNamingPolicy.FULLY_QUALIFIED);
        
        Set<String> setOfArgumentNames = new HashSet<String>();
        
        for (int i = 0; i < nArgs; i++) {
            ArgumentMetadata argMetadata = (i < argMetadataArray.length) ? argMetadataArray[i] : null;
            CALDocComment.TextBlock argTextBlock = (docCommentForArguments != null && i < docCommentForArguments.getNArgBlocks()) ? docCommentForArguments.getNthArgBlock(i).getTextBlock() : null;
            
            String metadataValue = getBestDescriptionFromMetadata(argMetadata);
            
            ////
            /// For each argument we independently determine whether to generate the metadata, the CALDoc, or both
            /// depending on the configuration and the availability of argument metadata.
            //
            
            boolean genMetadata = (shouldGenerateFromMetadata() && metadataValue != null && metadataValue.length() > 0);
            
            boolean genCALDoc = ((shouldAlwaysGenerateFromCALDoc() || !genMetadata) && argTextBlock != null);
            
            currentPage.openTag(HTML.Tag.DIV, classAttribute(StyleClassConstants.ARG_BLOCK));
            
            currentPage.openTag(HTML.Tag.DL).openTag(HTML.Tag.DT).openTag(HTML.Tag.SPAN, classAttribute(StyleClassConstants.NAME_AND_TYPE));
            
            /// Generate the argument name and type signature
            //
            generateArgumentName(entityWithArgumentNames, i, argMetadata, docCommentForArguments, setOfArgumentNames);
            generateTypeSignature(typePieceSignatures[i]);
            
            currentPage.closeTag(HTML.Tag.SPAN).closeTag(HTML.Tag.DT).openTag(HTML.Tag.DD);
            
            /// Generate the metadata, if required.
            //
            if (genMetadata) {
                // trim the metadata of leading and trailing whitespace
                metadataValue = metadataValue.trim();
                
                currentPage.openTag(HTML.Tag.DIV);
                maybeGenerateMetadataIndicator();
                currentPage.addText(metadataValue);
                currentPage.closeTag(HTML.Tag.DIV);
            }
            
            /// Generate the CALDoc, if required.
            //
            if (genCALDoc) {
                currentPage.openTag(HTML.Tag.DIV);
                maybeGenerateCALDocIndicator();
                CALDocToHTMLUtilities.generateHTMLForCALDocTextBlock(argTextBlock, currentPage, inModulesSubdirectoryReferenceGenerator, StyleClassConstants.CODE_BLOCK, CODE_FORMATTING_TAG);
                currentPage.closeTag(HTML.Tag.DIV);
            }
            
            currentPage.closeTag(HTML.Tag.DD).closeTag(HTML.Tag.DL).closeTag(HTML.Tag.DIV);
        }

        if (nArgs > 0) {
            currentPage.closeTag(HTML.Tag.DD).closeTag(HTML.Tag.DL);
        }
        
        ////
        /// Then generate the return value, if requested.
        //
        if (shouldDisplayReturnBlock) {
            
            /// Generate the return value indicator and the return type
            //
            currentPage
                .openTag(HTML.Tag.DIV, classAttribute(StyleClassConstants.RETURN_BLOCK))
                .openTag(HTML.Tag.DL)
                .addTaggedText(HTML.Tag.DT, classAttribute(StyleClassConstants.ATTRIBUTE_HEADER), LocalizableUserVisibleString.RETURNS_COLON.toResourceString())
                .openTag(HTML.Tag.DD)
                .openTag(HTML.Tag.DL)
                .openTag(HTML.Tag.DT)
                .openTag(HTML.Tag.SPAN, classAttribute(StyleClassConstants.NAME_AND_TYPE))
                .addTaggedText(HTML.Tag.SPAN, classAttribute(StyleClassConstants.RETURN_VALUE_INDICIATOR), LocalizableUserVisibleString.RETURN_VALUE_INDICATOR.toResourceString());
            
            generateTypeSignature(typePieceSignatures[nArgs]);
            
            currentPage.closeTag(HTML.Tag.SPAN).closeTag(HTML.Tag.DT).openTag(HTML.Tag.DD);
            
            /// Generate the return value description
            //
            String metadataValue;
            if (metadata instanceof FunctionMetadata) {
                metadataValue = ((FunctionMetadata)metadata).getReturnValueDescription();
            } else if (metadata instanceof ClassMethodMetadata) {
                metadataValue = ((ClassMethodMetadata)metadata).getReturnValueDescription();
            } else if (metadata instanceof InstanceMethodMetadata) {
                metadataValue = ((InstanceMethodMetadata)metadata).getReturnValueDescription();
            } else {
                metadataValue = null;
            }
            
            CALDocComment.TextBlock returnBlock = (docCommentForReturnValue != null) ? docCommentForReturnValue.getReturnBlock() : null;
            
            boolean genMetadata = (shouldGenerateFromMetadata() && metadataValue != null && metadataValue.length() > 0);
            
            boolean genCALDoc = ((shouldAlwaysGenerateFromCALDoc() || !genMetadata) && returnBlock != null);
            
            if (genMetadata) {
                // trim the metadata of leading and trailing whitespace
                metadataValue = metadataValue.trim();
                
                currentPage.openTag(HTML.Tag.DIV);
                maybeGenerateMetadataIndicator();
                currentPage.addText(metadataValue);
                currentPage.closeTag(HTML.Tag.DIV);
            }
            
            if (genCALDoc) {
                currentPage.openTag(HTML.Tag.DIV);
                maybeGenerateCALDocIndicator();
                CALDocToHTMLUtilities.generateHTMLForCALDocTextBlock(returnBlock, currentPage, inModulesSubdirectoryReferenceGenerator, StyleClassConstants.CODE_BLOCK, CODE_FORMATTING_TAG);
                currentPage.closeTag(HTML.Tag.DIV);
            }
            
            currentPage
                .closeTag(HTML.Tag.DD)
                .closeTag(HTML.Tag.DL)
                .closeTag(HTML.Tag.DD)
                .closeTag(HTML.Tag.DL)
                .closeTag(HTML.Tag.DIV);
        }
        
        ////
        /// Finally, generate the supplementary blocks
        //
        generateStandardSupplementaryBlocks(metadata, docComment, inModulesSubdirectoryReferenceGenerator);
        
        currentPage.closeTag(HTML.Tag.DD).newline();
    }

    /**
     * Returns the appropriate style class for the start of a definition section, depending on the position
     * of the definition in its enclosing list.
     * @param index the position of the definition in its enclosing list.
     * @return the appropriate style class.
     */
    private StyleClass getDefinitionStartStyleClass(int index) {
        return (index == 0) ? StyleClassConstants.FIRST_DEFINITION_SECTION_START : StyleClassConstants.DEFINITION_SECTION_START;
    }
    
    /**
     * Adds a name to a generic disambiguation map of the form
     * <p>
     * (lowercased name -&gt; name -&gt; disambiguated name).
     * 
     * A disambiguation map works like this: Given a name (with mixed upper and
     * lower case)
     * <ol>
     * <li>get the lowercase version of the name - names that conflict because
     * of case-insensitivity would map to the same lowercased name.
     * 
     * <li>using the lowercased name, get a map mapping the original names to
     * disambiguated names.
     * 
     * <li>if the map is empty, insert the pair (name, name) (the first-comer
     * gets to keep its name without disambiguation).
     * 
     * <li>if the map already has entries, the name needs to be mangled to
     * create a disambiguated version. Then the pair (name, disambiguated name)
     * is added to the map.
     * </ol>
     * 
     * For retrieval, simply repeat the same process as above, except instead of
     * adding an entry to the inner map, the inner map is simply accessed to
     * retrieve the mapping for the name.
     * 
     * @param outerMap
     *            a Map<String, Map<String, String>> mapping lowercased name
     *            to a map mapping a name to its disambiguated name.
     * @param name
     *            the name to add to the map.
     */
    private void addNameToGenericDisambiguationMap(Map<String, Map<String, String>> outerMap, String name) {
        /// Get the map from name to disambiguated name by keying the outerMap on the lowercase version of the name
        //
        String lowercaseName = name.toLowerCase(Locale.ENGLISH);
        Map<String, String> nameToDisambiguatedNameMap = outerMap.get(lowercaseName);
        
        boolean noPreviousEntriesWithSameLowercaseName = false;
        if (nameToDisambiguatedNameMap == null) {
            nameToDisambiguatedNameMap = new HashMap<String, String>();
            outerMap.put(lowercaseName, nameToDisambiguatedNameMap);
            noPreviousEntriesWithSameLowercaseName = true;
        }
        
        /// Add the name and its disambiguated form to the inner map.
        //
        String disambiguatedName = nameToDisambiguatedNameMap.get(name);
        if (disambiguatedName == null) {
            
            if (noPreviousEntriesWithSameLowercaseName) {
                // the first entry gets to keep its own name
                nameToDisambiguatedNameMap.put(name, name);
                
            } else {
                // the remaining entries have to use disambiguated names
                String newlyDisambiguatedName = disambiguateName(name);
                
                // make sure that the disambiguated name itself does not collide with other previously
                // disambiguated names
                while (nameToDisambiguatedNameMap.values().contains(newlyDisambiguatedName)) {
                    newlyDisambiguatedName += '-';
                }
                
                nameToDisambiguatedNameMap.put(name, newlyDisambiguatedName);
            }
        }
    }
    
    /**
     * Adds a module name to the module name disambiguation map.
     * @param moduleName the module name to be added.
     */
    private void addModuleNameToDisambiguationMap(ModuleName moduleName) {
        addNameToGenericDisambiguationMap(disambiguationMapForModuleNames, moduleName.toSourceText());
    }
    
    /**
     * Adds a type constructor name to the type constructor name disambiguation map.
     * @param moduleName the name of the type constructor's module.
     * @param unqualifiedName the unqualified name of the type constructor.
     */
    private void addTypeConsNameToDisambiguationMap(ModuleName moduleName, String unqualifiedName) {
        Map<String, Map<String, String>> outerMap = disambiguationMapForTypeConsNames.get(moduleName);
        
        if (outerMap == null) {
            outerMap = new HashMap<String, Map<String, String>>();
            disambiguationMapForTypeConsNames.put(moduleName, outerMap);
        }
        
        addNameToGenericDisambiguationMap(outerMap, unqualifiedName);
    }
    
    /**
     * Adds a type class name to the type class name disambiguation map.
     * @param moduleName the name of the type class's module.
     * @param unqualifiedName the unqualified name of the type class.
     */
    private void addTypeClassNameToDisambiguationMap(ModuleName moduleName, String unqualifiedName) {
        Map<String, Map<String, String>> outerMap = disambiguationMapForTypeClassNames.get(moduleName);
        
        if (outerMap == null) {
            outerMap = new HashMap<String, Map<String, String>>();
            disambiguationMapForTypeClassNames.put(moduleName, outerMap);
        }
        
        addNameToGenericDisambiguationMap(outerMap, unqualifiedName);
    }
    
    /**
     * Constructs a disambiguated version of the given name, by prefixing each character in the range [A-Z] with a '-'.
     * @param name the name to be whose disambiguated version is to be constructed.
     * @return the disambiguated version of the name.
     */
    private String disambiguateName(String name) {
        return name.replaceAll("[A-Z]", "-$0");
    }
    
    /**
     * Fetches an unambiguous version of the given name from a generic disambiguation map of the form (lowercased name -&gt; name -&gt; disambiguated name).
     * @param disambiguationMap a Map<String, Map<String, String>> mapping lowercased name to a map mapping a name to its disambiguated name.
     * @param name the name whose unambiguous version is requested.
     * @return the unambiguous version of the given name.
     */
    private String getDisambiguatedName(Map<String, Map<String, String>> disambiguationMap, String name) {
        String lowercaseName = name.toLowerCase(Locale.ENGLISH);
        Map<String, String> nameToDisambiguatedName = disambiguationMap.get(lowercaseName);
        
        if (nameToDisambiguatedName == null) {
            return name;
        }
        
        String disambiguatedName = nameToDisambiguatedName.get(name);
        if (disambiguatedName == null) {
            return name;
        } else {
            return disambiguatedName;
        }
    }
    
    /**
     * Fetches an unambiguous version of the given module name.
     * @param moduleName the module name whose unambiguous version is requested.
     * @return the unambiguous version of the given name.
     */
    private String getDisambiguatedNameForModule(ModuleName moduleName) {
        return getDisambiguatedName(disambiguationMapForModuleNames, moduleName.toSourceText());
    }
    
    /**
     * Fetches an unambiguous version of the given type constructor name.
     * @param moduleName the name of the type constructor's module.
     * @param unqualifiedName the type constructor name whose unambiguous version is requested.
     * @return the unambiguous version of the given name.
     */
    private String getDisambiguatedNameForTypeCons(ModuleName moduleName, String unqualifiedName) {
        Map<String, Map<String, String>> outerMap = disambiguationMapForTypeConsNames.get(moduleName);
        
        if (outerMap == null) {
            return unqualifiedName;
        }
        
        return getDisambiguatedName(outerMap, unqualifiedName);
    }
    
    /**
     * Fetches an unambiguous version of the given type class name.
     * @param moduleName the name of the type class's module.
     * @param unqualifiedName the type class name whose unambiguous version is requested.
     * @return the unambiguous version of the given name.
     */
    private String getDisambiguatedNameForTypeClass(ModuleName moduleName, String unqualifiedName) {
        Map<String, Map<String, String>> outerMap = disambiguationMapForTypeClassNames.get(moduleName);
        
        if (outerMap == null) {
            return unqualifiedName;
        }
        
        return getDisambiguatedName(outerMap, unqualifiedName);
    }
    
    /////====================================================================================================
    ////
    /// Helpers for scope-related smarts
    //
    
    /**
     * Returns the style class corresponding to the given CAL scope.
     * @param scope the CAL scope. Can be null in the case of an instance method (which defaults to public).
     * @return the corresponding style class.
     */
    private StyleClass getScopeStyleClass(Scope scope) {
        if (scope == null || scope == Scope.PUBLIC) {
            return StyleClassConstants.PUBLIC_SCOPE;
        } else if (scope == Scope.PROTECTED) {
            return StyleClassConstants.PROTECTED_SCOPE;
        } else if (scope == Scope.PRIVATE) {
            return StyleClassConstants.PRIVATE_SCOPE;
        } else {
            throw new IllegalArgumentException();
        }
    }
    
    /////====================================================================================================
    ////
    /// Helpers for tooltips
    //
    
    /**
     * Generates the header and entry heading for a module in a tooltip.
     * @param moduleName the name of the module.
     */
    void tooltip_generateModuleEntryHeader(final ModuleName moduleName) {
        currentPage
            .addTaggedText(HTML.Tag.DIV, classAttribute(StyleClassConstants.TOOLTIP_HEADER), LocalizableUserVisibleString.MODULE.toResourceString())
            .addTaggedText(HTML.Tag.DIV, classAttribute(StyleClassConstants.DEFINITION_HEADER), moduleName.toSourceText());
    }
    
    /**
     * Generates the header for a scoped entity in a tooltip.
     * @param moduleName the module name to be displayed.
     */
    void tooltip_generateScopedEntityHeader(final ModuleName moduleName) {
        currentPage.addTaggedText(HTML.Tag.DIV, classAttribute(StyleClassConstants.TOOLTIP_HEADER), moduleName.toSourceText());
    }
    
    /**
     * Generates the header for a data cons field name in a tooltip.
     * @param identifierInfo the identifier info.
     */
    void tooltip_generateDataConsFieldNameHeader(final IdentifierInfo.DataConsFieldName identifierInfo) {
        
        if (identifierInfo.getAssociatedDataConstructors().size() > 1) {
            currentPage
                .openTag(HTML.Tag.DIV)
                .openTag(HTML.Tag.DL)
                .openTag(HTML.Tag.DT)
                .addTaggedText(HTML.Tag.SPAN, classAttribute(StyleClassConstants.TOOLTIP_HEADER), LocalizableUserVisibleString.FIELD_OF_DATA_CONSTRUCTORS.toResourceString())
                .closeTag(HTML.Tag.DT);

            for (final IdentifierInfo.TopLevel.DataCons dataCons : identifierInfo.getAssociatedDataConstructors()) {
                currentPage
                    .openTag(HTML.Tag.DD)
                    .addTaggedText(HTML.Tag.TT, dataCons.getResolvedName().toSourceText())
                    .closeTag(HTML.Tag.DD);
            }
            
            currentPage.closeTag(HTML.Tag.DL).closeTag(HTML.Tag.DIV);
        } else if (identifierInfo.getAssociatedDataConstructors().size() == 1) {
            currentPage
                .openTag(HTML.Tag.DIV)
                .addTaggedText(HTML.Tag.SPAN, classAttribute(StyleClassConstants.TOOLTIP_HEADER), LocalizableUserVisibleString.FIELD_OF_DATA_CONSTRUCTOR_AND_SPACE.toResourceString())
                .addTaggedText(HTML.Tag.TT, identifierInfo.getFirstAssociatedDataConstructor().getResolvedName().toSourceText())
                .closeTag(HTML.Tag.DIV);
        } else {
            throw new IllegalArgumentException("must have at least one associated data constructor");
        }
    }
    
    /**
     * Generates the header for a local name in a tooltip.
     * @param identifierInfo the identifier info.
     */
    void tooltip_generateLocalNameHeader(final IdentifierInfo.Local identifierInfo) {
        
        if (identifierInfo instanceof IdentifierInfo.Local.Function) {
            currentPage.addTaggedText(HTML.Tag.DIV, classAttribute(StyleClassConstants.TOOLTIP_HEADER), LocalizableUserVisibleString.LOCAL_FUNCTION.toResourceString());
            
        } else if (identifierInfo instanceof IdentifierInfo.Local.PatternMatchVariable) {
            currentPage.addTaggedText(HTML.Tag.DIV, classAttribute(StyleClassConstants.TOOLTIP_HEADER), LocalizableUserVisibleString.LOCAL_PATTERN_MATCH_VARIABLE.toResourceString());
            
        } else if (identifierInfo instanceof IdentifierInfo.Local.CasePatternVariable) {
            currentPage.addTaggedText(HTML.Tag.DIV, classAttribute(StyleClassConstants.TOOLTIP_HEADER), LocalizableUserVisibleString.CASE_PATTERN_VARIABLE.toResourceString());
            
        } else if (identifierInfo instanceof IdentifierInfo.Local.Parameter.TopLevelFunctionOrClassMethod) {
            final IdentifierInfo.Local.Parameter.TopLevelFunctionOrClassMethod info =
                (IdentifierInfo.Local.Parameter.TopLevelFunctionOrClassMethod)identifierInfo;
            
            currentPage
                .openTag(HTML.Tag.DIV)
                .addTaggedText(HTML.Tag.SPAN, classAttribute(StyleClassConstants.TOOLTIP_HEADER), LocalizableUserVisibleString.PARAMETER_OF_AND_SPACE.toResourceString())
                .addTaggedText(HTML.Tag.TT, info.getAssociatedFunction().getResolvedName().toSourceText())
                .closeTag(HTML.Tag.DIV);
            
        } else if (identifierInfo instanceof IdentifierInfo.Local.Parameter.LocalFunction) {
            final IdentifierInfo.Local.Parameter.LocalFunction info =
                (IdentifierInfo.Local.Parameter.LocalFunction)identifierInfo;
            
            currentPage
                .openTag(HTML.Tag.DIV)
                .addTaggedText(HTML.Tag.SPAN, classAttribute(StyleClassConstants.TOOLTIP_HEADER), LocalizableUserVisibleString.PARAMETER_OF_LOCAL_FUNCTION_AND_SPACE.toResourceString())
                .addTaggedText(HTML.Tag.TT, info.getAssociatedFunction().getVarName())
                .closeTag(HTML.Tag.DIV);
            
        } else if (identifierInfo instanceof IdentifierInfo.Local.Parameter.Lambda) {
            currentPage.addTaggedText(HTML.Tag.DIV, classAttribute(StyleClassConstants.TOOLTIP_HEADER), LocalizableUserVisibleString.PARAMETER_OF_LAMBDA_EXPRESSION.toResourceString());
            
        } else if (identifierInfo instanceof IdentifierInfo.Local.Parameter.InstanceMethodCALDoc) {
            currentPage.addTaggedText(HTML.Tag.DIV, classAttribute(StyleClassConstants.TOOLTIP_HEADER), LocalizableUserVisibleString.PARAMETER_OF_INSTANCE_METHOD.toResourceString());
            
        } else {
            currentPage.addTaggedText(HTML.Tag.DIV, classAttribute(StyleClassConstants.TOOLTIP_HEADER), LocalizableUserVisibleString.LOCAL_VARIABLE.toResourceString());
        }
    }
    
    /**
     * Generates the header and entry for a type variable in a tooltip.
     * @param identifierInfo the identifier info.
     */
    void tooltip_generateTypeVariableHeaderAndEntry(final IdentifierInfo.TypeVariable identifierInfo) {
        currentPage
            .addTaggedText(HTML.Tag.DIV, classAttribute(StyleClassConstants.TOOLTIP_HEADER), LocalizableUserVisibleString.TYPE_VARIABLE.toResourceString())
            .addTaggedText(HTML.Tag.DIV, classAttribute(StyleClassConstants.DEFINITION_HEADER), identifierInfo.getTypeVarName());
    }
    
    /**
     * Generates the header and entry for a record field name in a tooltip.
     * @param identifierInfo the identifier info.
     */
    void tooltip_generateRecordFieldNameHeaderAndEntry(final IdentifierInfo.RecordFieldName identifierInfo) {
        currentPage
            .addTaggedText(HTML.Tag.DIV, classAttribute(StyleClassConstants.TOOLTIP_HEADER), LocalizableUserVisibleString.RECORD_FIELD_NAME.toResourceString())
            .addTaggedText(HTML.Tag.DIV, classAttribute(StyleClassConstants.DEFINITION_HEADER), identifierInfo.getFieldName().getCalSourceForm());
    }
    
    /**
     * Generates a horizontal separator in a tooltip.
     */
    void tooltip_generateHorizontalRule() {
        currentPage.emptyTag(HTML.Tag.HR);
    }
    
    /**
     * Generates the entry for a local name (without associated documentation) in a tooltip.
     * @param identifierInfo the identifier info.
     */
    void tooltip_generateSimpleLocalNameEntry(final IdentifierInfo.Local identifierInfo) {
        currentPage.addTaggedText(HTML.Tag.DIV, classAttribute(StyleClassConstants.DEFINITION_HEADER), identifierInfo.getVarName());
    }
    
    /**
     * Generates the entry for a data cons field name in a tooltip.
     * @param identifierInfo the identifier info.
     */
    void tooltip_generateDataConsFieldNameEntry(final IdentifierInfo.DataConsFieldName identifierInfo) {
        currentPage.addTaggedText(HTML.Tag.DIV, classAttribute(StyleClassConstants.DEFINITION_HEADER), identifierInfo.getFieldName().getCalSourceForm());
    }
    
    /**
     * Generates the start tag for a definition list in a tooltip.
     */
    void tooltip_generateDefListOpen() {
        currentPage.openTag(HTML.Tag.DL);
    }
    
    /**
     * Generates the end tag for a definition list in a tooltip.
     */
    void tooltip_generateDefListClose() {
        currentPage.closeTag(HTML.Tag.DL);
    }
    
    /////====================================================================================================
    ////
    /// Implementation of the abstract visitation methods in the superclass
    //
    
    /**
     * {@inheritDoc}
     */
    @Override
    void prepareGenerationForModule(ModuleTypeInfo moduleTypeInfo) {
        addModuleNameToDisambiguationMap(moduleTypeInfo.getModuleName());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    void beginDoc() {
        ////
        /// Generate the CSS files and the main frameset.
        //
        generateTextFile(DEFAULT_CSS_FILENAME, getDefaultCSS());
        generateTextFile(PRINTED_VERSION_CSS_FILENAME, getPrintedVersionCSS());
        generateTextFile(MAIN_PAGE_FILENAME, getMainPageHTML());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void endDoc() {
        ////
        /// Now that all module and usage documentation has been generated, generate the module list, the overview page, and the master search page.
        //
        generateTextFile(MODULE_LIST_FILENAME, getModuleListPageHTML());
        generateTextFile(OVERVIEW_PAGE_FILENAME, getOverviewPageHTML());
        generateTextFile(MASTER_SCOPED_ENTITY_SEARCH_PAGE_FILENAME, getMasterScopedEntitySearchPageHTML());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void beginModuleDoc(ModuleTypeInfo moduleTypeInfo, int nTypeConstructors, int nFunctions, int nTypeClasses, int nClassInstances) {
        /// Start a new page
        //
        startNewCurrentPageWithModule(moduleTypeInfo.getModuleName());
        
        /// Generate the head section
        //
        currentPage.openTag(HTML.Tag.HTML);
        String relativePathToBaseDirectory = "../"; // all module docs are in a subdirectory of the base directory
        String pageTitle = makePageTitle(getFullyQualifiedNameForModule(currentModuleName));
        generateHeadSection(pageTitle, relativePathToBaseDirectory, null, null);
        
        /// Start the body section with a javascript which changes the navigation index in the nav frame,
        /// and which changes the window title (needed if inside a frame).
        //
        currentPage
            .openTag(HTML.Tag.BODY, classAttribute(StyleClassConstants.WITH_MAIN_CONTENT).concat(HTMLBuilder.AttributeList.make(HTML4.ONLOAD_ATTRIBUTE, "showNavChangeTitle()")))
            .addTaggedText(HTML.Tag.SCRIPT, HTMLBuilder.AttributeList.make(HTML.Attribute.TYPE, "text/javascript"),
                "\n" +
                "function showNavChangeTitle() {\n" +
                "   if (parent.frames != undefined && parent.frames[1] != undefined && parent.frames[1].location != undefined) {\n" + 
                "      var urlPieces = new String(parent.frames[1].location).split('/');\n" + 
                "      var filePortion = urlPieces[urlPieces.length - 1];\n" + 
                "      if (filePortion.indexOf('" + getPerModuleIndexFileNamePrefix(currentModuleName) + "') != 0) {\n" +
                "         parent.frames[1].location.replace('" + relativePathToBaseDirectory + NAV_SUBDIRECTORY + "/" + getPerModuleFunctionalAgentIndexFileName(currentModuleName) + "');\n" +
                "      }\n" +
                "   }\n" +
                "   parent.document.title='" + pageTitle + "';\n" +
                "}\n" +
                "\n" +
                "showNavChangeTitle(); // run it immediately\n");
        
        generateUpdateScopeDisplaySettingsJavascript();
        
        /// Cache the statistics of this module
        //
        nTypeConstructorsInModule = nTypeConstructors;
        nFunctionsInModule = nFunctions;
        nTypeClassesInModule = nTypeClasses;
        nClassInstancesInModule = nClassInstances;
        
        /// Generate the navigation bar
        //
        generateModulePageNavBar(false, nTypeConstructors, nFunctions, nTypeClasses, nClassInstances);
        
        /// Wrap the main content with a div tag
        //
        currentPage.openTag(HTML.Tag.DIV, classAttribute(StyleClassConstants.MAIN_CONTENT));
        
        /// Generate the heading of the page using the module name
        //
        currentPage.addTaggedText(HTML.Tag.H1, getFullyQualifiedNameForModule(currentModuleName));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void endModuleDoc(int nTypeConstructors, int nFunctions, int nTypeClasses, int nClassInstances) {
        
        /// Close the main content div tag
        //
        currentPage.closeTag(HTML.Tag.DIV);
        
        /// Generate the navigation bar and the fine print
        //
        generateModulePageNavBar(true, nTypeConstructors, nFunctions, nTypeClasses, nClassInstances);
        generatePageBottom();
        
        currentPage.closeTag(HTML.Tag.BODY).closeTag(HTML.Tag.HTML);
        
        /// Write the completed module documentation out.
        //
        generateTextFile(MODULES_SUBDIRECTORY, getModuleFileName(currentModuleName), getHTMLFileContentsWithDocTypeForMainFramePage(currentPage));
        
        
        ////
        /// Generate the four per-module indices: types, functional agents, type classes and instances 
        //
        
        String typeIndexFileName = getPerModuleTypeIndexFileName(currentModuleName);
        generateTextFile(NAV_SUBDIRECTORY, typeIndexFileName, getTypeIndexPageHTML(typeIndexFileName));
        
        String functionalAgentIndexFileName = getPerModuleFunctionalAgentIndexFileName(currentModuleName);
        generateTextFile(NAV_SUBDIRECTORY, functionalAgentIndexFileName, getFunctionalAgentIndexPageHTML(functionalAgentIndexFileName));
        
        String typeClassIndexFileName = getPerModuleTypeClassIndexFileName(currentModuleName);
        generateTextFile(NAV_SUBDIRECTORY, typeClassIndexFileName, getTypeClassIndexPageHTML(typeClassIndexFileName));
        
        String instanceIndexFileName = getPerModuleInstanceIndexFileName(currentModuleName);
        generateTextFile(NAV_SUBDIRECTORY, instanceIndexFileName, getInstanceIndexPageHTML(instanceIndexFileName));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    void beginImportedModulesList(int nImportedModules) {
        importedModules = new HashSet<ModuleName>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void generateImportedModule(ModuleTypeInfo moduleTypeInfo, int index, int nImportedModules) {
        importedModules.add(moduleTypeInfo.getModuleName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void endImportedModulesList(int nImportedModules) {
        generateRelatedModulesList(LocalizableUserVisibleString.IMPORTED_MODULES_COLON.toResourceString(), importedModules);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void beginFriendModulesList(int nFriendModules) {
        friendModules = new HashSet<ModuleName>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void generateFriendModule(ModuleName moduleName, int index, int nFriendModules) {
        friendModules.add(moduleName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void endFriendModulesList(int nFriendModules) {
        generateRelatedModulesList(LocalizableUserVisibleString.FRIEND_MODULES_COLON.toResourceString(), friendModules);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void beginDirectlyDependentModulesList(int nDependentModules) {
        directlyDependentModules = new HashSet<ModuleName>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void generateDirectlyDependentModule(ModuleName moduleName, int index, int nDependentModules) {
        directlyDependentModules.add(moduleName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void endDirectlyDependentModulesList(int nDependentModules) {
        generateRelatedModulesList(LocalizableUserVisibleString.DIRECTLY_DEPENDENT_MODULES_COLON.toResourceString(), directlyDependentModules);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void beginIndirectlyDependentModulesList(int nDependentModules) {
        indirectlyDependentModules = new HashSet<ModuleName>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void generateIndirectlyDependentModule(ModuleName moduleName, int index, int nDependentModules) {
        indirectlyDependentModules.add(moduleName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void endIndirectlyDependentModulesList(int nDependentModules) {
        generateRelatedModulesList(LocalizableUserVisibleString.INDIRECTLY_DEPENDENT_MODULES_COLON.toResourceString(), indirectlyDependentModules);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    void generateModuleDescription(ModuleTypeInfo moduleTypeInfo) {
        ModuleName moduleName = moduleTypeInfo.getModuleName();
        CALFeatureMetadata metadata = getMetadata(CALFeatureName.getModuleFeatureName(moduleName), getLocale());
        CALDocComment docComment = moduleTypeInfo.getCALDocComment();

        /// For a module, just generate its description and supplementary blocks
        //
        generateStandardDescription(metadata, docComment, inModulesSubdirectoryReferenceGenerator);
        generateStandardSupplementaryBlocks(metadata, docComment, inModulesSubdirectoryReferenceGenerator);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void beginModuleOverviewSection(boolean isOverviewSectionEmpty) {
        /// Generate a heading for the module overview section only if the section is non-empty
        //
        if (!isOverviewSectionEmpty) {
            currentPage.addTaggedText(HTML.Tag.H2, idAndClassAttributes(ElementID.SUMMARY_SECTION, StyleClassConstants.MAJOR_SECTION), LocalizableUserVisibleString.MODULE_SUMMARY.toResourceString());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void endModuleOverviewSection(boolean isOverviewSectionEmpty) {
        // nothing to do
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void beginTypeConsOverviewSection(int nTypeConstructors, Scope maxScopeOfTypeConstructors) {
        /// Generate the start for the types overview section only if the section is non-empty
        //
        if (nTypeConstructors > 0) {
            currentPage.openTag(HTML.Tag.DIV, classAttribute(getScopeStyleClass(maxScopeOfTypeConstructors)));
            beginOverviewTable(LocalizableUserVisibleString.TYPES.toResourceString(), false);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void endTypeConsOverviewSection(int nTypeConstructors) {
        /// Generate the end for the types overview section only if the section is non-empty
        //
        if (nTypeConstructors > 0) {
            endOverviewTable();
            currentPage.closeTag(HTML.Tag.DIV);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void beginFunctionsOverviewSection(int nFunctions, Scope maxScopeOfFunctions) {
        /// Generate the start for the functions overview section only if the section is non-empty
        //
        if (nFunctions > 0) {
            currentPage.openTag(HTML.Tag.DIV, classAttribute(getScopeStyleClass(maxScopeOfFunctions)));
            beginOverviewTable(LocalizableUserVisibleString.FUNCTIONS.toResourceString(), false);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void endFunctionsOverviewSection(int nFunctions) {
        /// Generate the end for the functions overview section only if the section is non-empty
        //
        if (nFunctions > 0) {
            endOverviewTable();
            currentPage.closeTag(HTML.Tag.DIV);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void beginTypeClassOverviewSection(int nTypeClasses, Scope maxScopeOfTypeClasses) {
        /// Generate the start for the type class overview section only if the section is non-empty
        //
        if (nTypeClasses > 0) {
            currentPage.openTag(HTML.Tag.DIV, classAttribute(getScopeStyleClass(maxScopeOfTypeClasses)));
            beginOverviewTable(LocalizableUserVisibleString.TYPE_CLASSES.toResourceString(), false);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void endTypeClassOverviewSection(int nTypeClasses) {
        /// Generate the end for the type class overview section only if the section is non-empty
        //
        if (nTypeClasses > 0) {
            endOverviewTable();
            currentPage.closeTag(HTML.Tag.DIV);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void beginClassInstancesOverviewSection(int nClassInstances, Scope maxScopeOfClassInstances) {
        /// Generate the start for the class instance overview section only if the section is non-empty
        //
        if (nClassInstances > 0) {
            currentPage.openTag(HTML.Tag.DIV, classAttribute(getScopeStyleClass(maxScopeOfClassInstances)));
            beginOverviewTable(LocalizableUserVisibleString.INSTANCES.toResourceString(), false);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void endClassInstancesOverviewSection(int nClassInstances) {
        /// Generate the end for the class instance overview section only if the section is non-empty
        //
        if (nClassInstances > 0) {
            endOverviewTable();
            currentPage.closeTag(HTML.Tag.DIV);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void generateTypeConsOverviewHeader(TypeConstructor typeConstructor) {
        
        ///
        // First, add the type constructor's name to the disambiguation map.
        // By the time the details section is generated, all the colliding type constructors
        // would have had their overview generated and thus the disambiguation map properly
        // populated and ready to serve out disambiguated names.
        //
        addTypeConsNameToDisambiguationMap(typeConstructor.getName().getModuleName(), typeConstructor.getName().getUnqualifiedName());
        
        CALFeatureMetadata metadata = getMetadata(CALFeatureName.getTypeConstructorFeatureName(typeConstructor.getName()), getLocale());
        CALDocComment docComment = typeConstructor.getCALDocComment();
        
        /// Generate the scope and name of the type constructor
        //
        currentPage
            .openTag(HTML.Tag.TR, classAttribute(getScopeStyleClass(typeConstructor.getScope())))
            .openTag(HTML.Tag.TD, classAttribute(StyleClassConstants.OVERVIEW_TABLE_SCOPE_COLUMN).concat(HTMLBuilder.AttributeList.make(HTML.Attribute.VALIGN, "baseline")))
            .addTaggedText(HTML.Tag.SPAN, classAttribute(StyleClassConstants.SCOPE), typeConstructor.getScope().toString())
            .closeTag(HTML.Tag.TD)
            .openTag(HTML.Tag.TD, HTMLBuilder.AttributeList.make(HTML.Attribute.VALIGN, "baseline"))
            .openTag(HTML.Tag.DL)
            .openTag(HTML.Tag.DT)
            .openTag(HTML.Tag.SPAN, classAttribute(StyleClassConstants.OVERVIEW_REFERENCE));

        generateTypeConsReference(typeConstructor.getName());
        
        currentPage
            .closeTag(HTML.Tag.SPAN)
            .closeTag(HTML.Tag.DT)
            .openTag(HTML.Tag.DD, classAttribute(StyleClassConstants.OVERVIEW_TABLE_OUTER_DESCRIPTION));
        
        /// Then generate the short description for the entry
        //
        generateShortDescription(metadata, docComment, inModulesSubdirectoryReferenceGenerator);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void beginDataConsOverviewList(int nDataConstructors, Scope maxScopeOfDataConstructors) {
        /// Generate the start for the data constructor overview list only if the list is non-empty
        //
        if (nDataConstructors > 0) {
            currentPage.openTag(HTML.Tag.DIV, classAttribute(getScopeStyleClass(maxScopeOfDataConstructors)));
            beginOverviewTable(null, true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void generateDataConsOverview(DataConstructor dataConstructor) {
        FunctionalAgentMetadata metadata = (FunctionalAgentMetadata)getMetadata(CALFeatureName.getDataConstructorFeatureName(dataConstructor.getName()), getLocale());
        CALDocComment docComment = dataConstructor.getCALDocComment();
        
        // We delegate the generation to the helper that handles all function agents
        generateFunctionalAgentOverview(dataConstructor, labelMaker.getLabel(dataConstructor), metadata, docComment, StyleClassConstants.OVERVIEW_TABLE_NESTED_DESCRIPTION);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void endDataConsOverviewList(int nDataConstructors) {
        /// Generate the end for the data constructor overview list only if the list is non-empty
        //
        if (nDataConstructors > 0) {
            endOverviewTable();
            currentPage.closeTag(HTML.Tag.DIV);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    void generateTypeConsOverviewFooter() {
        // Just close off the table row
        currentPage
            .closeTag(HTML.Tag.DD)
            .closeTag(HTML.Tag.DL)
            .closeTag(HTML.Tag.TD)
            .closeTag(HTML.Tag.TR);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    void generateFunctionOverview(Function function) {
        FunctionalAgentMetadata metadata = (FunctionalAgentMetadata)getMetadata(CALFeatureName.getFunctionFeatureName(function.getName()), getLocale());
        CALDocComment docComment = function.getCALDocComment();
        
        // We delegate the generation to the helper that handles all function agents
        generateFunctionalAgentOverview(function, labelMaker.getLabel(function), metadata, docComment, StyleClassConstants.OVERVIEW_TABLE_OUTER_DESCRIPTION);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void generateTypeClassOverviewHeader(TypeClass typeClass) {
        
        ///
        // First, add the type class's name to the disambiguation map.
        // By the time the details section is generated, all the colliding type classes
        // would have had their overview generated and thus the disambiguation map properly
        // populated and ready to serve out disambiguated names.
        //
        addTypeClassNameToDisambiguationMap(typeClass.getName().getModuleName(), typeClass.getName().getUnqualifiedName());
        
        CALFeatureMetadata metadata = getMetadata(CALFeatureName.getTypeClassFeatureName(typeClass.getName()), getLocale());
        CALDocComment docComment = typeClass.getCALDocComment();
        
        /// Generate the scope and name of the type class
        //
        currentPage
            .openTag(HTML.Tag.TR, classAttribute(getScopeStyleClass(typeClass.getScope())))
            .openTag(HTML.Tag.TD, classAttribute(StyleClassConstants.OVERVIEW_TABLE_SCOPE_COLUMN).concat(HTMLBuilder.AttributeList.make(HTML.Attribute.VALIGN, "baseline")))
            .addTaggedText(HTML.Tag.SPAN, classAttribute(StyleClassConstants.SCOPE), typeClass.getScope().toString())
            .closeTag(HTML.Tag.TD)
            .openTag(HTML.Tag.TD, HTMLBuilder.AttributeList.make(HTML.Attribute.VALIGN, "baseline"))
            .openTag(HTML.Tag.DL)
            .openTag(HTML.Tag.DT)
            .openTag(HTML.Tag.SPAN, classAttribute(StyleClassConstants.OVERVIEW_REFERENCE));
    
        generateTypeClassReference(typeClass.getName());
        
        currentPage
            .closeTag(HTML.Tag.SPAN)
            .closeTag(HTML.Tag.DT)
            .openTag(HTML.Tag.DD, classAttribute(StyleClassConstants.OVERVIEW_TABLE_OUTER_DESCRIPTION));
        
        /// Then generate the short description for the entry
        //
        generateShortDescription(metadata, docComment, inModulesSubdirectoryReferenceGenerator);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void beginClassMethodOverviewList(int nClassMethods) {
        /// Generate the start for the class method overview list only if the list is non-empty
        //
        if (nClassMethods > 0) {
            beginOverviewTable(null, true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void generateClassMethodOverview(ClassMethod classMethod) {
        FunctionalAgentMetadata metadata = (FunctionalAgentMetadata)getMetadata(CALFeatureName.getClassMethodFeatureName(classMethod.getName()), getLocale());
        CALDocComment docComment = classMethod.getCALDocComment();
        
        // We delegate the generation to the helper that handles all function agents
        generateFunctionalAgentOverview(classMethod, labelMaker.getLabel(classMethod), metadata, docComment, StyleClassConstants.OVERVIEW_TABLE_NESTED_DESCRIPTION);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void endClassMethodOverviewList(int nClassMethods) {
        /// Generate the end for the class method overview list only if the list is non-empty
        //
        if (nClassMethods > 0) {
            endOverviewTable();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    void generateTypeClassOverviewFooter() {
        // Just close off the table row
        currentPage
            .closeTag(HTML.Tag.DD)
            .closeTag(HTML.Tag.DL)
            .closeTag(HTML.Tag.TD)
            .closeTag(HTML.Tag.TR);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void generateClassInstanceOverviewHeader(ClassInstance classInstance) {
        CALFeatureMetadata metadata = getMetadata(CALFeatureName.getClassInstanceFeatureName(classInstance), getLocale());
        CALDocComment docComment = classInstance.getCALDocComment();

        /// Generate the name of the class instance
        //
        currentPage
            .openTag(HTML.Tag.TR, classAttribute(getScopeStyleClass(minScopeForInstanceClassAndInstanceType(classInstance))))
            .openTag(HTML.Tag.TD, classAttribute(StyleClassConstants.OVERVIEW_TABLE_SCOPE_COLUMN).concat(HTMLBuilder.AttributeList.make(HTML.Attribute.VALIGN, "baseline")))
            .closeTag(HTML.Tag.TD)
            .openTag(HTML.Tag.TD, HTMLBuilder.AttributeList.make(HTML.Attribute.VALIGN, "baseline"))
            .openTag(HTML.Tag.DL)
            .openTag(HTML.Tag.DT)
            .openTag(HTML.Tag.SPAN, classAttribute(StyleClassConstants.OVERVIEW_REFERENCE));
    
        generateClassInstanceReference(classInstance);
        
        currentPage
            .closeTag(HTML.Tag.SPAN)
            .closeTag(HTML.Tag.DT)
            .openTag(HTML.Tag.DD, classAttribute(StyleClassConstants.OVERVIEW_TABLE_OUTER_DESCRIPTION));
        
        /// Then generate the short description for the entry
        //
        generateShortDescription(metadata, docComment, inModulesSubdirectoryReferenceGenerator);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void beginInstanceMethodOverviewList(int nInstanceMethods) {
        // nothing to do - we omit the instance method listing in the overview
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void generateInstanceMethodOverview(ClassInstance classInstance, String methodName) {
        // nothing to do - we omit the instance method listing in the overview
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void endInstanceMethodOverviewList(int nInstanceMethods) {
        // nothing to do - we omit the instance method listing in the overview
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    void generateClassInstanceOverviewFooter() {
        // Just close off the table row
        currentPage
            .closeTag(HTML.Tag.DD)
            .closeTag(HTML.Tag.DL)
            .closeTag(HTML.Tag.TD)
            .closeTag(HTML.Tag.TR);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    void beginTypeConsDocSection(int nTypeConstructors, Scope maxScopeOfTypeConstructors) {
        /// Generate a major section heading only if there are type constructors to be documented
        //
        if (nTypeConstructors > 0) {
            currentPage
                .openTag(HTML.Tag.DIV, classAttribute(getScopeStyleClass(maxScopeOfTypeConstructors)))
                .addTaggedText(HTML.Tag.H2, idAndClassAttributes(ElementID.TYPES_SECTION, StyleClassConstants.MAJOR_SECTION), LocalizableUserVisibleString.TYPES.toResourceString());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void generateTypeConsDocHeader(TypeConstructor typeConstructor, int index) {
        CALFeatureMetadata metadata = getMetadata(CALFeatureName.getTypeConstructorFeatureName(typeConstructor.getName()), getLocale());
        CALDocComment docComment = typeConstructor.getCALDocComment();

        /// Generate the name of the type constructor as the header, followed by a link to the usages if necessary
        //
        String unqualifiedName = typeConstructor.getName().getUnqualifiedName();
        
        currentPage
            .openTag(HTML.Tag.DL, classAttribute(getScopeStyleClass(typeConstructor.getScope())))
            .openTag(HTML.Tag.DT, idAndClassAttributes(labelMaker.getLabel(typeConstructor), getDefinitionStartStyleClass(index)))
            .openTag(HTML.Tag.SPAN, classAttribute(StyleClassConstants.DEFINITION_HEADER_FIRST_LINE))
            .addTaggedText(HTML.Tag.SPAN, classAttribute(StyleClassConstants.DEFINITION_HEADER), unqualifiedName);
        
        maybeGenerateUsageLink(TYPE_CONS_USAGE_SUBDIRECTORY, getTypeConsUsageIndexFileName(typeConstructor.getName()));
            
        /// Generate the declaration for the type constructor including its scope
        //
        currentPage
            .closeTag(HTML.Tag.SPAN)
            .openTag(HTML.Tag.CODE, classAttribute(StyleClassConstants.DECLARATION))
            .addText(CALFragments.DATA)
            .addText(" ")
            .addTaggedText(HTML.Tag.SPAN, classAttribute(StyleClassConstants.SCOPE), typeConstructor.getScope().toString())
            .addText(" ")
            .addTaggedText(HTML.Tag.SPAN, classAttribute(StyleClassConstants.DECLARED_NAME), unqualifiedName)
            .closeTag(HTML.Tag.CODE)
            .closeTag(HTML.Tag.DT)
            .openTag(HTML.Tag.DD);
        
        /// Then generate its description, implementation info (if any), and supplementary blocks
        //
        generateStandardDescription(metadata, docComment, inModulesSubdirectoryReferenceGenerator);
        generateForeignTypeInfo(typeConstructor);
        generateStandardSupplementaryBlocks(metadata, docComment, inModulesSubdirectoryReferenceGenerator);
    }

    /**
     * Generate implementation info for foreign type (if the implementation type is visible).
     * @param typeConstructor the type constructor, which may or may not correspond to a foreign type.
     */
    private void generateForeignTypeInfo(TypeConstructor typeConstructor) {
        ForeignTypeInfo foreignTypeInfo = typeConstructor.getForeignTypeInfo();
        if (foreignTypeInfo != null) {
            
            Scope implScope = foreignTypeInfo.getImplementationVisibility();
            if (filter.shouldAcceptBasedOnScopeOnly(implScope)) {
                
                try {
                    String className = JavaTypeName.getFullJavaSourceName(foreignTypeInfo.getForeignType());
                    
                    currentPage
                        .openTag(HTML.Tag.DL, classAttribute(getScopeStyleClass(implScope)))
                        .addTaggedText(HTML.Tag.DT, classAttribute(StyleClassConstants.ATTRIBUTE_HEADER), LocalizableUserVisibleString.IMPLEMENTATION_VISIBILITY_COLON.toResourceString())
                        .addTaggedText(HTML.Tag.DD, implScope.toString())
                        .addTaggedText(HTML.Tag.DT, classAttribute(StyleClassConstants.ATTRIBUTE_HEADER), LocalizableUserVisibleString.FOREIGN_TYPE_COLON.toResourceString())
                        .addTaggedText(HTML.Tag.DD, className)
                        .closeTag(HTML.Tag.DL);
                    
                } catch (UnableToResolveForeignEntityException e) {
                    logger.severe(CALDocMessages.getString("STATUS.cannotResolveForeignTypeInfo", e.getCompilerMessage().toString()));
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void beginDataConsDocList(int nDataConstructors, Scope maxScopeOfDataConstructors) {
        /// Generate the start for the data constructor list only if the list is non-empty
        //
        if (nDataConstructors > 0) {
            currentPage
                .openTag(HTML.Tag.DIV, classAttribute(getScopeStyleClass(maxScopeOfDataConstructors)))
                .openTag(HTML.Tag.DL, classAttribute(StyleClassConstants.DATA_CONSTRUCTOR_LIST))
                .addTaggedText(HTML.Tag.DT, classAttribute(StyleClassConstants.DATA_CONSTRUCTOR_LIST_HEADER), LocalizableUserVisibleString.DATA_CONSTRUCTORS.toResourceString())
                .openTag(HTML.Tag.DD)
                .openTag(HTML.Tag.DL);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void generateDataConsDoc(DataConstructor dataConstructor, int index) {
        FunctionalAgentMetadata metadata = (FunctionalAgentMetadata)getMetadata(CALFeatureName.getDataConstructorFeatureName(dataConstructor.getName()), getLocale());
        CALDocComment docComment = dataConstructor.getCALDocComment();

        // We delegate the generation to the helper that handles all function agents
        generateFunctionalAgentDoc(dataConstructor, labelMaker.getLabel(dataConstructor), metadata, docComment, index, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void endDataConsDocList(int nDataConstructors) {
        /// Generate the end for the data constructor list only if the list is non-empty
        //
        if (nDataConstructors > 0) {
            currentPage.closeTag(HTML.Tag.DL).closeTag(HTML.Tag.DD).closeTag(HTML.Tag.DL).closeTag(HTML.Tag.DIV);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void generateTypeConsDocFooter(TypeConstructor typeConstructor, int index) {
        // Just close the definition list
        currentPage.closeTag(HTML.Tag.DD).closeTag(HTML.Tag.DL);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void endTypeConsDocSection(int nTypeConstructors) {
        if (nTypeConstructors > 0) {
            currentPage.closeTag(HTML.Tag.DIV);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void beginFunctionsDocSection(int nFunctions, Scope maxScopeOfFunctions) {
        /// Generate a major section heading only if there are functions to be documented
        //
        if (nFunctions > 0) {
            currentPage
                .openTag(HTML.Tag.DIV, classAttribute(getScopeStyleClass(maxScopeOfFunctions)))
                .addTaggedText(HTML.Tag.H2, idAndClassAttributes(ElementID.FUNCTIONS_SECTION, StyleClassConstants.MAJOR_SECTION), LocalizableUserVisibleString.FUNCTIONS.toResourceString());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void generateFunctionDoc(Function function, int index) {
        FunctionalAgentMetadata metadata = (FunctionalAgentMetadata)getMetadata(CALFeatureName.getFunctionFeatureName(function.getName()), getLocale());
        CALDocComment docComment = function.getCALDocComment();
        
        // We delegate the generation to the helper that handles all function agents.
        // The helper expects to work in the context of a definition list, so we create one.
        currentPage.openTag(HTML.Tag.DL, classAttribute(getScopeStyleClass(function.getScope())));
        generateFunctionalAgentDoc(function, labelMaker.getLabel(function), metadata, docComment, index, true);
        currentPage.closeTag(HTML.Tag.DL);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void endFunctionsDocSection(int nFunctions) {
        if (nFunctions > 0) {
            currentPage.closeTag(HTML.Tag.DIV);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void beginTypeClassesDocSection(int nTypeClasses, Scope maxScopeOfTypeClasses) {
        /// Generate a major section heading only if there are type classes to be documented
        //
        if (nTypeClasses > 0) {
            currentPage
                .openTag(HTML.Tag.DIV, classAttribute(getScopeStyleClass(maxScopeOfTypeClasses)))
                .addTaggedText(HTML.Tag.H2, idAndClassAttributes(ElementID.TYPE_CLASSES_SECTION, StyleClassConstants.MAJOR_SECTION), LocalizableUserVisibleString.TYPE_CLASSES.toResourceString());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void generateTypeClassDocHeader(TypeClass typeClass, int index) {
        CALFeatureMetadata metadata = getMetadata(CALFeatureName.getTypeClassFeatureName(typeClass.getName()), getLocale());
        CALDocComment docComment = typeClass.getCALDocComment();

        /// Generate the name of the type class as the header, followed by a link to the usages if necessary
        //
        String unqualifiedName = typeClass.getName().getUnqualifiedName();
        
        currentPage
            .openTag(HTML.Tag.DL, classAttribute(getScopeStyleClass(typeClass.getScope())))
            .openTag(HTML.Tag.DT, idAndClassAttributes(labelMaker.getLabel(typeClass), getDefinitionStartStyleClass(index)))
            .openTag(HTML.Tag.SPAN, classAttribute(StyleClassConstants.DEFINITION_HEADER_FIRST_LINE))
            .addTaggedText(HTML.Tag.SPAN, classAttribute(StyleClassConstants.DEFINITION_HEADER), unqualifiedName);
        
        maybeGenerateUsageLink(TYPE_CLASS_USAGE_SUBDIRECTORY, getTypeClassUsageIndexFileName(typeClass.getName()));
        
        /// Generate the full name for the type c
        //
        currentPage
            .closeTag(HTML.Tag.SPAN)
            .openTag(HTML.Tag.CODE, classAttribute(StyleClassConstants.DECLARATION))
            .addTaggedText(HTML.Tag.SPAN, classAttribute(StyleClassConstants.SCOPE), typeClass.getScope().toString())
            .addText(" ")
            .addText(CALFragments.CLASS)
            .addText(" ");
        
        generateTypeClassFullName(typeClass, true, StyleClassConstants.DECLARED_NAME);
            
        currentPage
            .closeTag(HTML.Tag.CODE)
            .closeTag(HTML.Tag.DT)
            .openTag(HTML.Tag.DD);
        
        /// Then generate its description and supplementary blocks
        //
        generateStandardDescription(metadata, docComment, inModulesSubdirectoryReferenceGenerator);
        generateStandardSupplementaryBlocks(metadata, docComment, inModulesSubdirectoryReferenceGenerator);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void beginClassMethodDocList(int nClassMethods) {
        /// Generate the start for the class method list only if the list is non-empty
        //
        if (nClassMethods > 0) {
            currentPage
                .openTag(HTML.Tag.DL, classAttribute(StyleClassConstants.CLASS_METHOD_LIST))
                .addTaggedText(HTML.Tag.DT, classAttribute(StyleClassConstants.CLASS_METHOD_LIST_HEADER), LocalizableUserVisibleString.CLASS_METHODS.toResourceString())
                .openTag(HTML.Tag.DD)
                .openTag(HTML.Tag.DL);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void generateClassMethodDoc(ClassMethod classMethod, int index) {
        FunctionalAgentMetadata metadata = (FunctionalAgentMetadata)getMetadata(CALFeatureName.getClassMethodFeatureName(classMethod.getName()), getLocale());
        CALDocComment docComment = classMethod.getCALDocComment();
        
        // We delegate the generation to the helper that handles all function agents
        generateFunctionalAgentDoc(classMethod, labelMaker.getLabel(classMethod), metadata, docComment, index, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void endClassMethodDocList(int nClassMethods) {
        /// Generate the start for the class method list only if the list is non-empty
        //
        if (nClassMethods > 0) {
            currentPage.closeTag(HTML.Tag.DL).closeTag(HTML.Tag.DD).closeTag(HTML.Tag.DL);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void beginKnownInstancesList(int nKnownInstances, Scope maxScopeOfKnownInstances) {
        /// Generate the start for the known instances list only if the list is non-empty
        //
        if (nKnownInstances > 0) {
            currentPage
                .openTag(HTML.Tag.DIV, classAttribute(getScopeStyleClass(maxScopeOfKnownInstances)))
                .openTag(HTML.Tag.DL, classAttribute(StyleClassConstants.KNOWN_INSTANCE_LIST))
                .addTaggedText(HTML.Tag.DT, classAttribute(StyleClassConstants.KNOWN_INSTANCE_LIST_HEADER), LocalizableUserVisibleString.KNOWN_INSTANCES.toResourceString());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void generateKnownInstance(ClassInstance classInstance, int index) {
        /// For a known instance we generate a reference to the instance in its own item in the definition list
        //
        currentPage.openTag(HTML.Tag.DD, classAttribute(getScopeStyleClass(minScopeForInstanceClassAndInstanceType(classInstance))));
        generateClassInstanceReference(classInstance);
        currentPage.closeTag(HTML.Tag.DD);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void endKnownInstancesList(int nKnownInstances) {
        /// Generate the end for the known instances list only if the list is non-empty
        //
        if (nKnownInstances > 0) {
            currentPage.closeTag(HTML.Tag.DL).closeTag(HTML.Tag.DIV);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void generateTypeClassDocFooter(TypeClass typeClass, int index) {
        // Just close the definition list
        currentPage.closeTag(HTML.Tag.DD).closeTag(HTML.Tag.DL);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void endTypeClassesDocSection(int nTypeClasses) {
        if (nTypeClasses > 0) {
            currentPage.closeTag(HTML.Tag.DIV);
        }
    }
    
    /**
     * Overrides the implementation in the superclass to obtain the name of a class instance,
     * appropriately qualified according to the configuration options.
     *  
     * @param classInstance the class instance.
     * @return the display name for the class instance.
     */
    @Override
    String getClassInstanceDisplayName(ClassInstance classInstance) {
        // Temporarily swap out the current HTML builder with a new one,
        // and use it to generate a class instance name (with no hyperlinks).
        // Then swap the original builder back in, and return the text in the new builder, trimmed.
        HTMLBuilder origCurrentPage = currentPage;
        try {
            startNewCurrentPage();
            generateClassInstanceDeclarationName(classInstance, false);
            return currentPage.toHTML().trim();
        } finally {
            currentPage = origCurrentPage;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void beginClassInstancesDocSection(int nClassInstances, ClassInstance[] classInstances, Scope maxScopeOfClassInstances) {
        
        if (config.shouldSeparateInstanceDoc) {
            /// Switch the current page to a separate instance documentation page
            //
            mainPageContext = currentPage;
            inSeparateInstanceDoc = true;
            
            startNewCurrentPage();
            String pageName = LocalizableUserVisibleString.INSTANCE_DOC_TITLE.toResourceString(getFullyQualifiedNameForModule(currentModuleName));
            
            /// Generate the head section
            //
            currentPage.openTag(HTML.Tag.HTML);
            String relativePathToBaseDirectory = "../"; // all instance docs are in a subdirectory of the base directory
            String pageTitle = makePageTitle(pageName);
            generateHeadSection(pageTitle, relativePathToBaseDirectory, null, null);
            
            /// Start the body section with a javascript which changes the navigation index in the nav frame,
            /// and which changes the window title (needed if inside a frame).
            //
            currentPage
                .openTag(HTML.Tag.BODY, classAttribute(StyleClassConstants.WITH_MAIN_CONTENT).concat(HTMLBuilder.AttributeList.make(HTML4.ONLOAD_ATTRIBUTE, "showNavChangeTitle()")))
                .addTaggedText(HTML.Tag.SCRIPT, HTMLBuilder.AttributeList.make(HTML.Attribute.TYPE, "text/javascript"),
                    "\n" +
                    "function showNavChangeTitle() {\n" +
                    "   if (parent.frames != undefined && parent.frames[1] != undefined && parent.frames[1].location != undefined) {\n" + 
                    "      var urlPieces = new String(parent.frames[1].location).split('/');\n" + 
                    "      var filePortion = urlPieces[urlPieces.length - 1];\n" + 
                    "      if (filePortion.indexOf('" + getPerModuleIndexFileNamePrefix(currentModuleName) + "') != 0) {\n" +
                    "         parent.frames[1].location.replace('" + relativePathToBaseDirectory + NAV_SUBDIRECTORY + "/" + getPerModuleInstanceIndexFileName(currentModuleName) + "');\n" +
                    "      }\n" +
                    "   }\n" +
                    "   parent.document.title='" + pageTitle + "';\n" +
                    "}\n" +
                    "\n" +
                    "showNavChangeTitle(); // run it immediately\n");
            
            generateUpdateScopeDisplaySettingsJavascript();
            
            /// Generate the navigation bar
            //
            generateModulePageNavBar(false, relativePathToBaseDirectory, nTypeConstructorsInModule, nFunctionsInModule, nTypeClassesInModule, nClassInstancesInModule);
            
            /// Wrap the main content with a div tag
            //
            currentPage.openTag(HTML.Tag.DIV, classAttribute(StyleClassConstants.MAIN_CONTENT));
            
            /// Generate the heading of the page using the qualified name
            //
            currentPage.addTaggedText(HTML.Tag.H1, pageName);
            
            /// Generate a dedicated overview section
            //
            boolean isOverviewSectionEmpty = (nClassInstances == 0);
            beginModuleOverviewSection(isOverviewSectionEmpty);
            beginClassInstancesOverviewSection(nClassInstances, maxScopeOfClassInstances);
            for (int i = 0; i < nClassInstances; i++) {
                generateClassInstanceOverview(classInstances[i]);
            }
            endClassInstancesOverviewSection(nClassInstances);
            endModuleOverviewSection(isOverviewSectionEmpty);
        }
        
        /// Generate a major section heading only if there are class instances to be documented
        //
        if (nClassInstances > 0) {
            currentPage
            .openTag(HTML.Tag.DIV, classAttribute(getScopeStyleClass(maxScopeOfClassInstances)))
            .addTaggedText(HTML.Tag.H2, idAndClassAttributes(ElementID.INSTANCES_SECTION, StyleClassConstants.MAJOR_SECTION), LocalizableUserVisibleString.INSTANCES.toResourceString());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void generateClassInstanceDocHeader(ClassInstance classInstance, int index) {
        CALFeatureMetadata metadata = getMetadata(CALFeatureName.getClassInstanceFeatureName(classInstance), getLocale());
        CALDocComment docComment = classInstance.getCALDocComment();

        /// Generate the name of the class instance as the header, followed by a link to the usages if necessary
        //
        currentPage
            .openTag(HTML.Tag.DL, classAttribute(getScopeStyleClass(minScopeForInstanceClassAndInstanceType(classInstance))))
            .openTag(HTML.Tag.DT, idAndClassAttributes(labelMaker.getLabel(classInstance), getDefinitionStartStyleClass(index)))
            .openTag(HTML.Tag.SPAN, classAttribute(StyleClassConstants.DEFINITION_HEADER_FIRST_LINE))
            .openTag(HTML.Tag.SPAN, classAttribute(StyleClassConstants.DEFINITION_HEADER));
        
        generateClassInstanceDeclarationName(classInstance, false);
        
        /// Generate the declaration for the class instance, with its contents appropriately hyperlinked
        //
        currentPage
            .closeTag(HTML.Tag.SPAN)
            .closeTag(HTML.Tag.SPAN)
            .openTag(HTML.Tag.CODE, classAttribute(StyleClassConstants.DECLARATION))
            .addText("instance ")
            .openTag(HTML.Tag.SPAN, classAttribute(StyleClassConstants.DECLARED_NAME));
        
        generateClassInstanceDeclarationName(classInstance, true);
        
        currentPage
            .closeTag(HTML.Tag.SPAN)
            .closeTag(HTML.Tag.CODE)
            .closeTag(HTML.Tag.DT)
            .openTag(HTML.Tag.DD);
        
        /// Then generate its description and supplementary blocks
        //
        generateStandardDescription(metadata, docComment, inModulesSubdirectoryReferenceGenerator);
        generateStandardSupplementaryBlocks(metadata, docComment, inModulesSubdirectoryReferenceGenerator);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    void beginInstanceMethodDocList(int nInstanceMethods) {
        /// Generate the start for the instance method list only if the list is non-empty
        //
        if (nInstanceMethods > 0) {
            currentPage
                .openTag(HTML.Tag.DL, classAttribute(StyleClassConstants.INSTANCE_METHOD_LIST))
                .addTaggedText(HTML.Tag.DT, classAttribute(StyleClassConstants.INSTANCE_METHOD_LIST_HEADER), LocalizableUserVisibleString.INSTANCE_METHODS.toResourceString())
                .openTag(HTML.Tag.DD)
                .openTag(HTML.Tag.DL);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void generateInstanceMethodDoc(ClassInstance classInstance, String methodName, ClassMethod classMethod, int index) {
        InstanceMethodMetadata metadata = (InstanceMethodMetadata)getMetadata(CALFeatureName.getInstanceMethodFeatureName(classInstance, methodName), getLocale());
        CALDocComment docComment = classInstance.getMethodCALDocComment(methodName);
        
        ////
        /// With an instance method, we attempt to figure out which CALDoc comment to refer to for documentation.
        /// Often it is the case that the instance method, or even the class instance, would be undocumented. In such situations,
        /// it would be beneficial to present the documentation on the class method, since after all it is a class method that
        /// ultimately ends up being used in expressions, not the instance method backing it.
        //
        
        Scope scope = null;
        String unqualifiedName = methodName;
        FunctionalAgent entityWithArgumentNames = classMethod;
        CALDocComment classMethodCALDocComment = classMethod.getCALDocComment();
        TypeExpr typeExpr = classInstance.getInstanceMethodType(methodName);
        ArgumentMetadata[] argMetadataArray = metadata.getArguments();
        String label = labelMaker.getLabel(classInstance, methodName);
        
        /// If there are no @arg blocks for the instance method's CALDoc comment, or there is no CALDoc comment for the method at
        /// all, use the CALDoc comment for the class method
        //
        final CALDocComment docCommentForArguments;
        if (docComment != null && docComment.getNArgBlocks() > 0) {
            docCommentForArguments = docComment;
        } else {
            docCommentForArguments = classMethodCALDocComment;
        }
        
        /// If there is no @return block for the instance method's CALDoc comment, or there is no CALDoc comment for the method at
        /// all, use the CALDoc comment for the class method - but only if the class method's arity matches the instance method's arity.
        //
        final CALDocComment docCommentForReturnValue;
        if (docComment != null && docComment.getReturnBlock() != null) {
            docCommentForReturnValue = docComment;
            
        } else if (classMethod.getTypeExpr().getArity() == typeExpr.getArity()) {
            
            // we only default to the class method's comment if the arities match
            // for example: the QuickCheck.Arbitrary instance for the function type a->b has an instance method:
            //    generateInstance :: (Arbitrary a, Arbitrary b) => GenParams -> a -> b
            // but the class method has the type:
            //    generateInstance :: (Arbitrary a) => GenParams -> a
            // so we cannot use the return value description in the class method's comment (it would be describing the wrong thing)
            
            docCommentForReturnValue = classMethodCALDocComment;
            
        } else {
            docCommentForReturnValue = null;
        }
        
        if (docComment == null) {
            docComment = classMethodCALDocComment;
        }
        
        generateFunctionalAgentOrInstanceMethodDocHeading(scope, unqualifiedName, entityWithArgumentNames, typeExpr, label, argMetadataArray, docCommentForArguments, index, true);
        
        generateFunctionalAgentOrInstanceMethodDocBody(scope, entityWithArgumentNames, typeExpr, metadata, argMetadataArray, docComment, docCommentForArguments, docCommentForReturnValue, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void endInstanceMethodDocList(int nInstanceMethods) {
        /// Generate the end for the instance method list only if the list is non-empty
        //
        if (nInstanceMethods > 0) {
            currentPage.closeTag(HTML.Tag.DL).closeTag(HTML.Tag.DD).closeTag(HTML.Tag.DL);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void generateClassInstanceDocFooter(ClassInstance classInstance, int index) {
        // Just close the definition list
        currentPage.closeTag(HTML.Tag.DD).closeTag(HTML.Tag.DL);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void endClassInstancesDocSection(int nClassInstances) {
        if (nClassInstances > 0) {
            currentPage.closeTag(HTML.Tag.DIV);
        }
        
        if (config.shouldSeparateInstanceDoc) {
            /// Close the main content div tag
            //
            currentPage.closeTag(HTML.Tag.DIV);
            
            /// Generate the navigation bar and the fine print
            //
            String relativePathToBaseDirectory = "../"; // all instance docs are in a subdirectory of the base directory
            generateModulePageNavBar(true, relativePathToBaseDirectory, nTypeConstructorsInModule, nFunctionsInModule, nTypeClassesInModule, nClassInstancesInModule);
            generatePageBottom();
            
            currentPage.closeTag(HTML.Tag.BODY).closeTag(HTML.Tag.HTML);
            
            /// Write the completed usage documentation out.
            //
            generateTextFile(SEPARATE_INSTANCE_DOC_SUBDIRECTORY, getSeparateInstanceDocFileName(currentModuleName), getHTMLFileContentsWithDocTypeForMainFramePage(currentPage));
            
            /// Switch from the separate instance documentation page back to the main page
            //
            inSeparateInstanceDoc = false;
            currentPage = mainPageContext;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void beginTypeConsUsageDoc(TypeConstructor typeConstructor) {
        ModuleName moduleName = typeConstructor.getName().getModuleName();
        String typeName = typeConstructor.getName().getUnqualifiedName();
        
        CALFeatureMetadata metadata = getMetadata(CALFeatureName.getTypeConstructorFeatureName(typeConstructor.getName()), getLocale());
        CALDocComment docComment = typeConstructor.getCALDocComment();
        
        String label = labelMaker.getLabel(typeConstructor);
        
        // We delegate the generation of everything before the declaration to a helper method.
        generateUsageDocStartBeforeDeclaration(typeConstructor.getName(), label);
        
        // Just generate the declaration for the type constructor
        currentPage
            .addText(CALFragments.DATA)
            .addText(" ")
            .addTaggedText(HTML.Tag.SPAN, classAttribute(StyleClassConstants.SCOPE), typeConstructor.getScope().toString())
            .addText(" ")
            .addTaggedText(HTML.Tag.SPAN, classAttribute(StyleClassConstants.DECLARED_NAME), typeName);
        
        // We delegate the generation of everything after the declaration to a helper method.
        generateUsageDocStartAfterDeclaration(typeConstructor, moduleName, metadata, docComment, label);
    }

    /**
     * Generates the first half of the start of the usage documentation, before the declaration of the entity.
     * @param qualifiedName the qualified name of the entity whose usage is being documented.
     * @param label the label for the entity.
     */
    private void generateUsageDocStartBeforeDeclaration(QualifiedName qualifiedName, String label) {
        /// Start a new HTML page for the usage documentation
        //
        startNewCurrentPageWithModule(qualifiedName.getModuleName());
        
        /// Generate the head section
        //
        currentPage.openTag(HTML.Tag.HTML);
        String relativePathToBaseDirectory = "../"; // all usage docs are in a subdirectory of the base directory
        String pageTitle = makePageTitle(qualifiedName.getQualifiedName());
        generateHeadSection(pageTitle, relativePathToBaseDirectory, null, null);
        
        /// Start the body section with a javascript which changes the window title (needed if inside a frame)
        //
        currentPage
            .openTag(HTML.Tag.BODY, classAttribute(StyleClassConstants.WITH_MAIN_CONTENT).concat(HTMLBuilder.AttributeList.make(HTML4.ONLOAD_ATTRIBUTE, "changeTitle()")))
            .addTaggedText(HTML.Tag.SCRIPT, HTMLBuilder.AttributeList.make(HTML.Attribute.TYPE, "text/javascript"), "function changeTitle() { parent.document.title='" + pageTitle + "'; }");
        
        generateUpdateScopeDisplaySettingsJavascript();
        
        /// Generate the navigation bar
        //
        generateGenericNavBar(false, relativePathToBaseDirectory);
        
        /// Wrap the main content with a div tag
        //
        currentPage.openTag(HTML.Tag.DIV, classAttribute(StyleClassConstants.MAIN_CONTENT));
        
        /// Generate the heading of the page using the qualified name
        //
        currentPage.addTaggedText(HTML.Tag.H1, qualifiedName.getQualifiedName());

        /// Prepare for the generation of the declaration
        //
        currentPage
            .openTag(HTML.Tag.DL)
            .openTag(HTML.Tag.DT, idAndClassAttributes(label, StyleClassConstants.FIRST_DEFINITION_SECTION_START))
            .openTag(HTML.Tag.CODE, classAttribute(StyleClassConstants.DECLARATION));
    }
    
    /**
     * Generates the second half of the start of the usage documentation, after the declaration of the entity.
     * @param moduleName the name of the entity's module.
     * @param metadata the metadata of the entity. Can be null if not available.
     * @param docComment the CALDoc comment of the entity. Can be null if not available.
     * @param label the label for the entity.
     */
    private void generateUsageDocStartAfterDeclaration(ScopedEntity documentedEntity, ModuleName moduleName, CALFeatureMetadata metadata, CALDocComment docComment, String label) {
        /// Generate a link to the main documentation entry
        //
        currentPage
            .closeTag(HTML.Tag.CODE)
            .addText(" &nbsp;&nbsp;&nbsp;[");
        
        String relativePathToModulesSubdirectory = "../" + MODULES_SUBDIRECTORY;
        generateNonLocalReference(currentPage, relativePathToModulesSubdirectory, moduleName, label, LocalizableUserVisibleString.MAIN_ENTRY.toResourceString(), null);
            
        currentPage
            .addText("]")
            .closeTag(HTML.Tag.DT)
            .openTag(HTML.Tag.DD);
        
        /// Then generate the description and supplementary blocks
        //
        ReferenceGenerator referenceGenerator = new ReferenceGenerator(relativePathToModulesSubdirectory);
        generateStandardDescription(metadata, docComment, referenceGenerator);
        if (documentedEntity instanceof TypeConstructor) {
            generateForeignTypeInfo((TypeConstructor)documentedEntity);
        }
        generateStandardSupplementaryBlocks(metadata, docComment, referenceGenerator);
        
        currentPage.closeTag(HTML.Tag.DD).closeTag(HTML.Tag.DL);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void beginTypeClassUsageDoc(TypeClass typeClass) {
        ModuleName moduleName = typeClass.getName().getModuleName();
        
        CALFeatureMetadata metadata = getMetadata(CALFeatureName.getTypeClassFeatureName(typeClass.getName()), getLocale());
        CALDocComment docComment = typeClass.getCALDocComment();

        String label = labelMaker.getLabel(typeClass);
        
        // We delegate the generation of everything before the declaration to a helper method.
        generateUsageDocStartBeforeDeclaration(typeClass.getName(), label);
            
        // Just generate the declaration for the type class
        currentPage
            .addTaggedText(HTML.Tag.SPAN, classAttribute(StyleClassConstants.SCOPE), typeClass.getScope().toString())
            .addText(" ")
            .addText(CALFragments.CLASS)
            .addText(" ");
        
        generateTypeClassFullName(typeClass, false, StyleClassConstants.DECLARED_NAME);
        
        // We delegate the generation of everything after the declaration to a helper method.
        generateUsageDocStartAfterDeclaration(typeClass, moduleName, metadata, docComment, label);
    }
    
    /**
     * {@inheritDoc}
     */
    void beginUsageDocGroupForDependentModule(ScopedEntity documentedEntity, ModuleName dependentModuleName) {
        ModuleName moduleName = documentedEntity.getName().getModuleName();
        
        ////
        /// Generate a major section heading for the dependent module
        //
        currentPage.openTag(HTML.Tag.H2, classAttribute(StyleClassConstants.MAJOR_SECTION));
        
        // We either display "Defining module:" or "Dependent module:" depending on whether the dependent module
        // is really the defining module of the entity being documented
        if (moduleName.equals(dependentModuleName)) {
            currentPage.addText(LocalizableUserVisibleString.DEFINING_MODULE_COLON.toResourceString()).addText(" ");
        } else {
            currentPage.addText(LocalizableUserVisibleString.DEPENDENT_MODULE_COLON.toResourceString()).addText(" ");
        }
        
        // Generate a hyperlink to the dependent module if its documentation its generated, or otherwise simply generate its name 
        if (isDocForModuleGenerated(dependentModuleName)) {
            generateNonLocalReference(currentPage, "../" + MODULES_SUBDIRECTORY, dependentModuleName, labelMaker.getModuleLabel(dependentModuleName), getFullyQualifiedNameForModule(dependentModuleName), getFullyQualifiedNameForModule(dependentModuleName));
        } else {
            currentPage.addText(getFullyQualifiedNameForModule(dependentModuleName));
        }
        
        currentPage.closeTag(HTML.Tag.H2);
    }

    /**
     * {@inheritDoc}
     */
    void beginUsageDocArgTypeIndex(ScopedEntity documentedEntity, int nArgTypeIndexEntries, Scope maxScopeOfArgTypeIndexEntries) {
        /// Generate the start of the argument type index list only if the list is non-empty
        //
        if (nArgTypeIndexEntries > 0) {
            currentPage.openTag(HTML.Tag.DIV, classAttribute(getScopeStyleClass(maxScopeOfArgTypeIndexEntries)));
            beginOverviewTable(LocalizableUserVisibleString.USAGE_DOC_ARGUMENT_TYPE_INDEX.toResourceString(documentedEntity.getName().getQualifiedName()), false);
        }
    }

    /**
     * {@inheritDoc}
     */
    void generateUsageDocArgTypeIndexEntry(ScopedEntity documentedEntity, FunctionalAgent entity) {
        FunctionalAgentMetadata metadata = (FunctionalAgentMetadata)getMetadata(entity, getLocale());
        CALDocComment docComment = entity.getCALDocComment();
        
        // We delegate to a helper method to generate a usage index entry
        generateUsageIndexEntry(entity, labelMaker.getLabel(entity), metadata, docComment, StyleClassConstants.OVERVIEW_TABLE_OUTER_DESCRIPTION);
    }

    /**
     * {@inheritDoc}
     */
    void endUsageDocArgTypeIndex(ScopedEntity documentedEntity, int nArgTypeIndexEntries) {
        /// Generate the end of the argument type index list only if the list is non-empty
        //
        if (nArgTypeIndexEntries > 0) {
            endOverviewTable();
            currentPage.closeTag(HTML.Tag.DIV);
        }
    }

    /**
     * {@inheritDoc}
     */
    void beginUsageDocReturnTypeIndex(ScopedEntity documentedEntity, int nReturnTypeIndexEntries, Scope maxScopeOfReturnTypeIndexEntries) {
        /// Generate the start of the return type index list only if the list is non-empty
        //
        if (nReturnTypeIndexEntries > 0) {
            currentPage.openTag(HTML.Tag.DIV, classAttribute(getScopeStyleClass(maxScopeOfReturnTypeIndexEntries)));
            beginOverviewTable(LocalizableUserVisibleString.USAGE_DOC_RETURN_TYPE_INDEX.toResourceString(documentedEntity.getName().getQualifiedName()), false);
        }
    }

    /**
     * {@inheritDoc}
     */
    void generateUsageDocReturnTypeIndexEntry(ScopedEntity documentedEntity, FunctionalAgent entity) {
        FunctionalAgentMetadata metadata = (FunctionalAgentMetadata)getMetadata(entity, getLocale());
        CALDocComment docComment = entity.getCALDocComment();
        
        // We delegate to a helper method to generate a usage index entry
        generateUsageIndexEntry(entity, labelMaker.getLabel(entity), metadata, docComment, StyleClassConstants.OVERVIEW_TABLE_OUTER_DESCRIPTION);
    }

    /**
     * {@inheritDoc}
     */
    void endUsageDocReturnTypeIndex(ScopedEntity documentedEntity, int nReturnTypeIndexEntries) {
        /// Generate the end of the return type index list only if the list is non-empty
        //
        if (nReturnTypeIndexEntries > 0) {
            endOverviewTable();
            currentPage.closeTag(HTML.Tag.DIV);
        }
    }

    /**
     * {@inheritDoc}
     */
    void beginUsageDocInstanceIndex(ScopedEntity documentedEntity, int nInstances, Scope maxScopeOfInstanceIndexEntries) {
        /// Generate the start of the instance index list only if the list is non-empty
        //
        if (nInstances > 0) {
            /// If the entity is a type class, we want to display "Instances of <class>", otherwise
            /// we want to display (for a type) "Instances for <type>".
            //
            LocalizableUserVisibleString stringTemplate;
            if (documentedEntity instanceof TypeClass) {
                stringTemplate = LocalizableUserVisibleString.USAGE_DOC_INSTANCE_INDEX_INSTANCES_OF;
            } else {
                stringTemplate = LocalizableUserVisibleString.USAGE_DOC_INSTANCE_INDEX_INSTANCES_FOR;
            }
            currentPage.openTag(HTML.Tag.DIV, classAttribute(getScopeStyleClass(maxScopeOfInstanceIndexEntries)));
            beginOverviewTable(stringTemplate.toResourceString(documentedEntity.getName().getQualifiedName()), false);
        }
    }

    /**
     * {@inheritDoc}
     */
    void generateUsageDocInstanceIndexEntry(ScopedEntity documentedEntity, ClassInstance classInstance) {
        CALFeatureMetadata metadata = getMetadata(CALFeatureName.getClassInstanceFeatureName(classInstance), getLocale());
        CALDocComment docComment = classInstance.getCALDocComment();

        /// Generate the name of the class instance
        //
        currentPage
            .openTag(HTML.Tag.TR, classAttribute(getScopeStyleClass(minScopeForInstanceClassAndInstanceType(classInstance))))
            .openTag(HTML.Tag.TD, classAttribute(StyleClassConstants.OVERVIEW_TABLE_SCOPE_COLUMN).concat(HTMLBuilder.AttributeList.make(HTML.Attribute.VALIGN, "baseline")))
            .closeTag(HTML.Tag.TD)
            .openTag(HTML.Tag.TD, HTMLBuilder.AttributeList.make(HTML.Attribute.VALIGN, "baseline"))
            .openTag(HTML.Tag.DL)
            .openTag(HTML.Tag.DT)
            .openTag(HTML.Tag.SPAN, classAttribute(StyleClassConstants.OVERVIEW_REFERENCE));
    
        /// Generate a reference to the class instance, appropriately hyperlinked.
        //
        String relativePathToBaseDirectory = "../";
        String relativePathToModulesSubdirectory = "../" + MODULES_SUBDIRECTORY;
        
        if (!isDocForClassInstanceGenerated(classInstance)) {
            // if the class instance is not documented, no hyperlink can be made.
            generateClassInstanceDeclarationName(classInstance, false);
            
        } else {
            // the instance is indeed documented, so create a hyperlinked reference to it
            
            HTMLBuilder.AttributeList hrefAttribute = getHrefAttributeForNonLocalClassInstanceReference(classInstance, relativePathToBaseDirectory);
            
            currentPage.openTag(HTML.Tag.A, hrefAttribute);
            generateClassInstanceDeclarationName(classInstance, false);
            currentPage.closeTag(HTML.Tag.A);
        }
        
        currentPage
            .closeTag(HTML.Tag.SPAN)
            .closeTag(HTML.Tag.DT)
            .openTag(HTML.Tag.DD, classAttribute(StyleClassConstants.OVERVIEW_TABLE_OUTER_DESCRIPTION));
        
        /// Then generate the short description for the entry
        //
        generateShortDescription(metadata, docComment, new ReferenceGenerator(relativePathToModulesSubdirectory));
        
        // Close off the table row
        currentPage
            .closeTag(HTML.Tag.DD)
            .closeTag(HTML.Tag.DL)
            .closeTag(HTML.Tag.TD)
            .closeTag(HTML.Tag.TR);
    }

    /**
     * {@inheritDoc}
     */
    void endUsageDocInstanceIndex(ScopedEntity documentedEntity, int nInstances) {
        /// Generate the end of the instance index list only if the list is non-empty
        //
        if (nInstances > 0) {
            endOverviewTable();
            currentPage.closeTag(HTML.Tag.DIV);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    void endUsageDocGroupForDependentModule(ScopedEntity documentedEntity, ModuleName dependentModuleName) {
        // nothing to do
    }

    /**
     * {@inheritDoc}
     */
    void endTypeConsUsageDoc(TypeConstructor documentedEntity) {
        // Delegate to a helper method
        generateUsageDocEnd(TYPE_CONS_USAGE_SUBDIRECTORY, getTypeConsUsageIndexFileName(documentedEntity.getName()));
    }

    /**
     * Generates the end of the usage documentation, and writes out the file.
     * @param usageSubdirectory the usage subdirectory to contain the generated file.
     * @param indexFileName the name of the index file to be written.
     */
    private void generateUsageDocEnd(String usageSubdirectory, String indexFileName) {
        
        /// Close the main content div tag
        //
        currentPage.closeTag(HTML.Tag.DIV);
        
        /// Generate the navigation bar and the fine print
        //
        String relativePathToBaseDirectory = "../"; // all usage docs are in a subdirectory of the base directory
        generateGenericNavBar(true, relativePathToBaseDirectory);
        generatePageBottom();
        
        currentPage.closeTag(HTML.Tag.BODY).closeTag(HTML.Tag.HTML);
        
        /// Write the completed usage documentation out.
        //
        generateTextFile(usageSubdirectory, indexFileName, getHTMLFileContentsWithDocTypeForMainFramePage(currentPage));
    }

    /**
     * {@inheritDoc}
     */
    void endTypeClassUsageDoc(TypeClass documentedEntity) {
        // Delegate to a helper method
        generateUsageDocEnd(TYPE_CLASS_USAGE_SUBDIRECTORY, getTypeClassUsageIndexFileName(documentedEntity.getName()));
    }
}
