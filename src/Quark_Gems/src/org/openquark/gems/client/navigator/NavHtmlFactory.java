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
 * NavHtmlFactory.java
 * Creation date: Jul 23, 2003
 * By: Frank Worsley
 */
package org.openquark.gems.client.navigator;

import java.net.URL;
import java.text.ChoiceFormat;
import java.text.DateFormat;
import java.text.Format;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.openquark.cal.caldoc.CALDocToHTMLUtilities;
import org.openquark.cal.compiler.CALDocComment;
import org.openquark.cal.compiler.ClassInstance;
import org.openquark.cal.compiler.ClassInstanceIdentifier;
import org.openquark.cal.compiler.ClassMethod;
import org.openquark.cal.compiler.DataConstructor;
import org.openquark.cal.compiler.FunctionalAgent;
import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.ModuleTypeInfo;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.ScopedEntity;
import org.openquark.cal.compiler.ScopedEntityNamingPolicy;
import org.openquark.cal.compiler.TypeClass;
import org.openquark.cal.compiler.TypeConsApp;
import org.openquark.cal.compiler.TypeConstructor;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.compiler.ScopedEntityNamingPolicy.UnqualifiedUnlessAmbiguous;
import org.openquark.cal.metadata.ArgumentMetadata;
import org.openquark.cal.metadata.CALExample;
import org.openquark.cal.metadata.CALExpression;
import org.openquark.cal.metadata.CALFeatureMetadata;
import org.openquark.cal.metadata.ClassInstanceMetadata;
import org.openquark.cal.metadata.ClassMethodMetadata;
import org.openquark.cal.metadata.DataConstructorMetadata;
import org.openquark.cal.metadata.FunctionMetadata;
import org.openquark.cal.metadata.FunctionalAgentMetadata;
import org.openquark.cal.metadata.InstanceMethodMetadata;
import org.openquark.cal.metadata.ModuleMetadata;
import org.openquark.cal.metadata.ScopedEntityMetadata;
import org.openquark.cal.metadata.TypeClassMetadata;
import org.openquark.cal.metadata.TypeConstructorMetadata;
import org.openquark.cal.services.CALFeatureName;
import org.openquark.cal.services.CALWorkspace;
import org.openquark.cal.services.MetaModule;
import org.openquark.gems.client.GemCutter;
import org.openquark.util.WildcardPatternMatcher;
import org.openquark.util.html.HtmlHelper;


/**
 * This class is a factory for generating the pages displayed in the CAL navigator.
 * It accepts a navigator address and returns the correct HTML formatted page for it.
 * 
 * @author Frank Worsley
 */
public class NavHtmlFactory {

    /* Anchors used in the HTML output. */
    private static final String DESCRIPTION_ANCHOR = "#description";
    private static final String RESULT_ANCHOR = "#result";
    private static final String ARGUMENTS_ANCHOR = "#arguments";
    private static final String REQUIRED_METHOD_ANCHOR = "#requiredMethod";
    private static final String EXAMPLES_ANCHOR = "#examples";
    private static final String GENERAL_ANCHOR = "#general";
    private static final String RELATED_ANCHOR = "#relatedFeatures";
    private static final String CUSTOM_ATTRIBUTES_ANCHOR = "#customAttributes";
    private static final String IMPORTS_ANCHOR = "#importedModules";
    private static final String FRIENDS_ANCHOR = "#friendModules";
    private static final String PARENTS_ANCHOR = "#parentClasses";
    private static final String METHODS_ANCHOR = "#classMethods";
    private static final String CONSTRUCTORS_ANCHOR = "#dataConstructors";
    private static final String FUNCTIONS_ANCHOR = "#functions";
    private static final String TYPES_ANCHOR = "#typeConstructors";
    private static final String CLASSES_ANCHOR = "#typeClasses";
    private static final String INSTANCES_ANCHOR = "#classInstances";
    private static final String MODULES_ANCHOR = "#modules";
    private static final String INSTANCE_TYPE_ANCHOR = "#instanceType";
    private static final String INSTANCE_CLASS_ANCHOR = "#instanceClass";
    private static final String INSTANCE_METHODS_ANCHOR = "#instanceMethods";

    /** A DateFormat for emitting time in the current locale. */
    private static final DateFormat LOCALE_DATE_FORMAT = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.MEDIUM);
    
    /**
     * Implements a cross-reference generator capable of generating hyperlinks for cross-references
     * represented as CALFeatureNames. The implementation simply bridges to existing methods for generating
     * references in the outer class. 
     *
     * @author Joseph Wong
     */
    private static final class CrossReferenceGenerator extends CALDocToHTMLUtilities.CALFeatureCrossReferenceGenerator {
        
        /**
         * The owner of the navigator.
         */
        private final NavFrameOwner owner;
        
        /**
         * Constructs a cross-reference generator capable of generating hyperlinks for cross-references
         * represented as CALFeatureNames.
         * @param owner the owner of the navigator.
         */
        private CrossReferenceGenerator(NavFrameOwner owner) {
            this.owner = owner;
        }

        /**
         * Returns whether the given qualified name is a class method name.
         * @param qualifiedName the name to check.
         * @return true if the given qualified name is a class method name, false otherwise.
         */
        public boolean isClassMethodName(QualifiedName qualifiedName) {
            return isNameForClassMethod(owner, qualifiedName);
        }

        /**
         * Builds a well-formed HTML fragment for the cross-reference, given here as a CALFeatureName.
         * @param featureName the cross-reference.
         * @param moduleNameInSource how the module name portion of the reference appears in source. Could be the empty string if the reference is unqualified in source.
         * @return the HTML fragment for the cross-reference.
         */
        public String getRelatedFeatureLinkHTML(CALFeatureName featureName, String moduleNameInSource) {
            return getRelatedFeatureLinkHtml(owner, featureName, moduleNameInSource);
        }
    }
    
    /**
     * Implements a cross-reference generator capable of generating non-hyperlinked text for cross-references
     * represented as CALFeatureNames.
     *
     * @author Joseph Wong
     */
    private static final class NoHyperlinkCrossReferenceGenerator extends CALDocToHTMLUtilities.CALFeatureCrossReferenceGenerator {
        /**
         * Since whether a qualified name refers to a class method or not is irrelevant for this generator (as it
         * simply returns the display name of any given cross-reference), false is returned for all input values.
         * @param qualifiedName the name.
         * @return false, always.
         */
        public boolean isClassMethodName(QualifiedName qualifiedName) {
            return false;
        }

        /**
         * Builds a well-formed HTML fragment for the cross-reference, given here as a CALFeatureName.
         * @param featureName the cross-reference.
         * @param moduleNameInSource how the module name portion of the reference appears in source. Could be the empty string if the reference is unqualified in source.
         * @return the HTML fragment for the cross-reference.
         */
        public String getRelatedFeatureLinkHTML(CALFeatureName featureName, String moduleNameInSource) {
            if (featureName.getType() == CALFeatureName.MODULE) {
                if (moduleNameInSource.length() == 0) {
                    return featureName.toModuleName().toSourceText();
                } else {
                    return moduleNameInSource;
                }
                
            } else if (featureName.isScopedEntityName()) {
                String unqualifiedName = featureName.toQualifiedName().getUnqualifiedName();
                if (moduleNameInSource.length() == 0) {
                    return unqualifiedName;
                } else {
                    return moduleNameInSource + '.' + unqualifiedName;
                }
            } else {
                return featureName.getName();
            }
        }
    }
    
    /**
     * Returns whether the given qualified name is a class method name.
     * @param owner the navigator owner.
     * @param qualifiedName the name to check.
     * @return true if the given qualified name is a class method name, false otherwise.
     */
    private static boolean isNameForClassMethod(NavFrameOwner owner, QualifiedName qualifiedName) {
        MetaModule metaModule = owner.getPerspective().getMetaModule(qualifiedName.getModuleName());
        if (metaModule == null) {
            return false;
        }
        return metaModule.getTypeInfo().getClassMethod(qualifiedName.getUnqualifiedName()) != null;
    }
    
    /**
     * @param owner the navigator owner
     * @param url the navigator url of the entity to display metadata for
     * @return an HTML formatted description of the metadata stored in the metadata object.
     */
    public static String getPage(NavFrameOwner owner, NavAddress url) {
        
        if (url.getMethod() == NavAddress.WORKSPACE_METHOD) {
            return getWorkspacePage(owner);
        
        } else if (url.getMethod() == NavAddress.SEARCH_METHOD) {
            return getSearchPage(owner, url);
        
        } else if (url.getParameter(NavAddress.VAULT_PARAMETER) != null) {
            return getVaultPage(owner, url);

        } else if (url.getMethod() == NavAddress.MODULE_NAMESPACE_METHOD) {
            // this is a namespace node representing a module name without a corresponding actual module
            return getNamespacePage(owner, url, url.toFeatureName().toModuleName());
            
        } else if (NavAddressHelper.getMetadata(owner, url) != null) {
            return getMetadataPage(owner, url);
        
        } else if (url.getMethod() == NavAddress.COLLECTOR_METHOD) {
            return getCollectorNotFoundPage(url);
        
        } else {
            return getErrorPage(url);
        }
    }
    
    /**
     * @param owner the navigator owner
     * @param url the navigator url to display metadata for
     * @return the HTML for the metadata display page
     */
    private static String getMetadataPage(NavFrameOwner owner, NavAddress url) {
        
        CALFeatureMetadata metadata = NavAddressHelper.getMetadata(owner, url);
        StringBuilder buffer = new StringBuilder();
        
        // display the page header
        buffer.append(getMetadataPageHeader(owner, url));
        
        // display the name as the heading
        final String title = NavAddressHelper.getDisplayText(owner, url, ScopedEntityNamingPolicy.UNQUALIFIED);
        generateTitleWithIcon(buffer, url, title);
        
        if (url.getMethod() == NavAddress.COLLECTOR_METHOD) {
            // if the url refers to a collector, then there is no CALDoc for it, so simply
            // display the metadata
            
            if (metadata instanceof FunctionalAgentMetadata) {
                buffer.append(getBasicMetadataHtml(owner, metadata, null));
                buffer.append(getFunctionalAgentMetadataHtml(owner, (FunctionalAgentMetadata) metadata, url, null, false));
                buffer.append(getAdditionalMetadataHtml(owner, metadata, null));
            }
            
        } else {
            // add the basic metadata, then the metadata specific sections, and finally put the additional sections at the bottom
            CALFeatureName featureName = url.toFeatureName();
            
            // in the following if-else checks, if the metadata is null, it will fail all the instanceof checks
            // and simply fall off the end with no additional HTML generated, which is what is intended in such cases.
            
            if (metadata instanceof FunctionalAgentMetadata) {
                QualifiedName qualifiedName = featureName.toQualifiedName();
                ModuleTypeInfo moduleTypeInfoForFeature = owner.getPerspective().getMetaModule(qualifiedName.getModuleName()).getTypeInfo();
                FunctionalAgent envEntity = moduleTypeInfoForFeature.getFunctionalAgent(qualifiedName.getUnqualifiedName());
                
                boolean isRequiredClassMethod = false;
                if (envEntity instanceof ClassMethod) {
                    ClassMethod method = (ClassMethod)envEntity;
                    isRequiredClassMethod = (method.getDefaultClassMethodName() == null); // no default -> required
                }
                
                CALDocComment caldoc = envEntity.getCALDocComment();
                buffer.append(getBasicMetadataHtml(owner, metadata, caldoc));
                buffer.append(getFunctionalAgentMetadataHtml(owner, (FunctionalAgentMetadata) metadata, url, caldoc, isRequiredClassMethod));
                buffer.append(getAdditionalMetadataHtml(owner, metadata, caldoc));
                
            } else if (metadata instanceof ModuleMetadata) {
                ModuleName moduleName = featureName.toModuleName();
                ModuleTypeInfo moduleTypeInfoForFeature = owner.getPerspective().getMetaModule(moduleName).getTypeInfo();
                
                CALDocComment caldoc = moduleTypeInfoForFeature.getCALDocComment();
                buffer.append(getBasicMetadataHtml(owner, metadata, caldoc));
                buffer.append(getModuleMetadataHtml(owner, (ModuleMetadata) metadata)); 
                buffer.append(getAdditionalMetadataHtml(owner, metadata, caldoc));
                
            } else if (metadata instanceof TypeClassMetadata) {
                QualifiedName qualifiedName = featureName.toQualifiedName();
                ModuleTypeInfo moduleTypeInfoForFeature = owner.getPerspective().getMetaModule(qualifiedName.getModuleName()).getTypeInfo();
                TypeClass typeClass = moduleTypeInfoForFeature.getTypeClass(qualifiedName.getUnqualifiedName());
                
                CALDocComment caldoc = typeClass.getCALDocComment();
                buffer.append(getBasicMetadataHtml(owner, metadata, caldoc));
                buffer.append(getTypeClassMetadataHtml(owner, (TypeClassMetadata) metadata));
                buffer.append(getAdditionalMetadataHtml(owner, metadata, caldoc));
                
            } else if (metadata instanceof TypeConstructorMetadata) {
                QualifiedName qualifiedName = featureName.toQualifiedName();
                ModuleTypeInfo moduleTypeInfoForFeature = owner.getPerspective().getMetaModule(qualifiedName.getModuleName()).getTypeInfo();
                TypeConstructor typeCons = moduleTypeInfoForFeature.getTypeConstructor(qualifiedName.getUnqualifiedName());
                
                CALDocComment caldoc = typeCons.getCALDocComment();
                buffer.append(getBasicMetadataHtml(owner, metadata, caldoc));
                buffer.append(getTypeConstructorMetadataHtml(owner, (TypeConstructorMetadata) metadata));
                buffer.append(getAdditionalMetadataHtml(owner, metadata, caldoc));
                
            } else if (metadata instanceof ClassInstanceMetadata) {
                ClassInstanceIdentifier classInstanceID = featureName.toInstanceIdentifier();
                ModuleTypeInfo moduleTypeInfoForFeature = owner.getPerspective().getMetaModule(featureName.toModuleName()).getTypeInfo();
                ClassInstance instance = moduleTypeInfoForFeature.getClassInstance(classInstanceID);
                
                CALDocComment caldoc = instance.getCALDocComment();
                buffer.append(getBasicMetadataHtml(owner, metadata, caldoc));
                buffer.append(getClassInstanceMetadataHtml(owner, (ClassInstanceMetadata) metadata));
                buffer.append(getAdditionalMetadataHtml(owner, metadata, caldoc));
                
            } else if (metadata instanceof InstanceMethodMetadata) {
                ClassInstanceIdentifier classInstanceID = featureName.toInstanceIdentifier();
                ModuleTypeInfo moduleTypeInfoForFeature = owner.getPerspective().getMetaModule(featureName.toModuleName()).getTypeInfo();
                ClassInstance instance = moduleTypeInfoForFeature.getClassInstance(classInstanceID);
                
                CALDocComment caldoc = instance.getMethodCALDocComment(featureName.toInstanceMethodName());
                buffer.append(getBasicMetadataHtml(owner, metadata, caldoc));
                buffer.append(getInstanceMethodMetadataHtml(owner, (InstanceMethodMetadata) metadata, url, caldoc));
                buffer.append(getAdditionalMetadataHtml(owner, metadata, caldoc));
            }
        }

        return buffer.toString();
    }

    /**
     * Adds an appropriate icon to the HTML buffer.
     * @param buffer the buffer containing HTML.
     * @param url the NavAddress of the entity being documented.
     */
    private static void addAppropriateIcon(final StringBuilder buffer, final NavAddress url) {
        final URL iconURL;
        if (url.getMethod() == NavAddress.MODULE_METHOD) {
            iconURL = GemCutter.class.getResource("/Resources/nav_module_big.png");
        } else if (url.getMethod() == NavAddress.MODULE_NAMESPACE_METHOD) {
            iconURL = GemCutter.class.getResource("/Resources/nav_namespace_big.png");
        } else {
            iconURL = null;
        }
        
        if (iconURL != null) {
            buffer.append("<img src='" + iconURL + "'>");
        }
    }

    /**
     * @param owner the navigator owner
     * @param url the url of the page to get a header for
     * @return the HTML for the link table displayed at the top of metadata pages
     */
    private static String getMetadataPageHeader(NavFrameOwner owner, NavAddress url) {
     
        StringBuilder buffer = new StringBuilder();

        buffer.append(getPageLocationHtml(owner, url));
        buffer.append("<table cellpadding='1' cellspacing='0' width='100%' bgcolor='#E0E0E0'><tr><td nowrap>");
        buffer.append("<font face='arial' size='2'>");
        
        CALFeatureMetadata metadata = NavAddressHelper.getMetadata(owner, url);
        
        // add links to the module vaults
        if (metadata instanceof ModuleMetadata) {
            
            ModuleName moduleName = ModuleName.make(url.getBase());
            MetaModule module = owner.getPerspective().getMetaModule(moduleName);

            if (module.getTypeInfo().getNFunctions() > 0) {
                NavAddress vaultUrl = url.withParameter(NavAddress.VAULT_PARAMETER, NavAddress.FUNCTION_VAULT_VALUE);
                buffer.append(getLinkHtml(vaultUrl, NavigatorMessages.getString("NAV_Functions_Link")));
                buffer.append(" | ");
            }
        
            if (module.getTypeInfo().getNTypeConstructors() > 0) {
                NavAddress vaultUrl = url.withParameter(NavAddress.VAULT_PARAMETER, NavAddress.TYPE_VAULT_VALUE);
                buffer.append(getLinkHtml(vaultUrl, NavigatorMessages.getString("NAV_Types_Link")));
                buffer.append(" | ");
            }
        
            if (module.getTypeInfo().getNTypeClasses() > 0) {
                NavAddress vaultUrl = url.withParameter(NavAddress.VAULT_PARAMETER, NavAddress.CLASS_VAULT_VALUE);
                buffer.append(getLinkHtml(vaultUrl, NavigatorMessages.getString("NAV_Classes_Link")));
                buffer.append(" | ");
            }
        
            if (module.getTypeInfo().getNClassInstances() > 0) {
                NavAddress vaultUrl = url.withParameter(NavAddress.VAULT_PARAMETER, NavAddress.INSTANCE_VAULT_VALUE);
                buffer.append(getLinkHtml(vaultUrl, NavigatorMessages.getString("NAV_Instances_Link")));
                buffer.append(" | ");
            }
        }
        
        // add links to the anchors in the page in the same they appear in the page
        buffer.append(getLinkHtml(DESCRIPTION_ANCHOR, NavigatorMessages.getString("NAV_Description_Link")));
        buffer.append(" | ");
        
        if (metadata instanceof FunctionalAgentMetadata || metadata instanceof InstanceMethodMetadata) {

            buffer.append(getLinkHtml(RESULT_ANCHOR, NavigatorMessages.getString("NAV_ReturnValue_Link")));
            buffer.append(" | ");
            
            buffer.append(getLinkHtml(ARGUMENTS_ANCHOR, NavigatorMessages.getString("NAV_Arguments_Link")));
            buffer.append(" | ");
            
            if (metadata instanceof ClassMethodMetadata) {
                buffer.append(getLinkHtml(REQUIRED_METHOD_ANCHOR, NavigatorMessages.getString("NAV_RequiredMethod_Link")));
                buffer.append(" | ");
            }

            buffer.append(getLinkHtml(EXAMPLES_ANCHOR, NavigatorMessages.getString("NAV_Examples_Link")));
            buffer.append(" | ");            
        
        } else if (metadata instanceof ModuleMetadata) {

            buffer.append(getLinkHtml(IMPORTS_ANCHOR, NavigatorMessages.getString("NAV_Imports_Link")));
            buffer.append(" | ");
        
            buffer.append(getLinkHtml(FRIENDS_ANCHOR, NavigatorMessages.getString("NAV_Friends_Link")));
            buffer.append(" | ");
        
        } else if (metadata instanceof TypeClassMetadata) {

            buffer.append(getLinkHtml(PARENTS_ANCHOR, NavigatorMessages.getString("NAV_Parents_Link")));
            buffer.append(" | ");
            
            buffer.append(getLinkHtml(METHODS_ANCHOR, NavigatorMessages.getString("NAV_Methods_Link")));
            buffer.append(" | ");
            
            buffer.append(getLinkHtml(TYPES_ANCHOR, NavigatorMessages.getString("NAV_Types_Link")));
            buffer.append(" | ");

            buffer.append(getLinkHtml(INSTANCES_ANCHOR, NavigatorMessages.getString("NAV_Instances_Link")));
            buffer.append(" | ");
            
        } else if (metadata instanceof TypeConstructorMetadata) {
            
            buffer.append(getLinkHtml(CONSTRUCTORS_ANCHOR, NavigatorMessages.getString("NAV_Constructors_Link")));
            buffer.append(" | ");

            buffer.append(getLinkHtml(CLASSES_ANCHOR, NavigatorMessages.getString("NAV_Classes_Link")));
            buffer.append(" | ");
        
            buffer.append(getLinkHtml(INSTANCES_ANCHOR, NavigatorMessages.getString("NAV_Instances_Link")));
            buffer.append(" | ");
            
        } else if (metadata instanceof ClassInstanceMetadata) {
            
            buffer.append(getLinkHtml(INSTANCE_CLASS_ANCHOR, NavigatorMessages.getString("NAV_InstanceClass_Link")));
            buffer.append(" | ");
            
            buffer.append(getLinkHtml(INSTANCE_TYPE_ANCHOR, NavigatorMessages.getString("NAV_InstanceType_Link")));
            buffer.append(" | ");
            
            buffer.append(getLinkHtml(INSTANCE_METHODS_ANCHOR, NavigatorMessages.getString("NAV_InstanceMethods_Link")));
            buffer.append(" | ");
        }

        buffer.append(getLinkHtml(GENERAL_ANCHOR, NavigatorMessages.getString("NAV_General_Link")));
        buffer.append(" | ");
        
        buffer.append(getLinkHtml(RELATED_ANCHOR, NavigatorMessages.getString("NAV_Related_Link")));
        buffer.append(" | ");
        
        buffer.append(getLinkHtml(CUSTOM_ATTRIBUTES_ANCHOR, NavigatorMessages.getString("NAV_CustomAttributes_Link")));
        
        buffer.append("</font>");
        buffer.append("</td></tr></table>");        
        
        return buffer.toString();
    }
    
    /**
     * @param url the url to display the not found page for
     * @return the HTML for the collector not found page
     */
    private static String getCollectorNotFoundPage(NavAddress url) {
        
        StringBuilder buffer = new StringBuilder();
        buffer.append(getHeaderHtml());
        buffer.append("<h1>" + NavigatorMessages.getString("NAV_CollectorNotFound_Header") + "</h1>");
        buffer.append(NavigatorMessages.getString("NAV_CollectorNotFound_Message", url.getBase()));
        buffer.append(getFooterHtml());

        return buffer.toString();        
    }
    
    /**
     * @param url the url to display the error page for
     * @return the HTML for the error page
     */
    private static String getErrorPage(NavAddress url) {
        
        StringBuilder buffer = new StringBuilder();
        buffer.append(getHeaderHtml());
        buffer.append("<h1>" + NavigatorMessages.getString("NAV_ErrorDisplayingPage_Header") + "</h1>");
        buffer.append(NavigatorMessages.getString("NAV_ErrorDisplayingPage_Message", url.toString()));
        buffer.append(getFooterHtml());

        return buffer.toString();
    }
    
    /**
     * @param owner the navigator owner
     * @return the HTML for the workspace page
     */
    private static String getWorkspacePage(NavFrameOwner owner) {
        
        StringBuilder buffer = new StringBuilder();
        buffer.append(getHeaderHtml());
        buffer.append("<h1>" + NavigatorMessages.getString("NAV_PropertiesBrowser_Header") + "</h1>");
        buffer.append(NavigatorMessages.getString("NAV_PropertiesBrowser_Message"));
        buffer.append("<h2>" + NavigatorMessages.getString("NAV_AvailableModules_Header") + "</h2>");
        buffer.append(getModuleListHtml(owner));            
        buffer.append(getFooterHtml());
        
        return buffer.toString();
    }
    
    /**
     * @param owner the navigator owner
     * @return the HTML for the namespace page
     */
    private static String getNamespacePage(NavFrameOwner owner, NavAddress url, ModuleName moduleName) {
        
        StringBuilder buffer = new StringBuilder();
        buffer.append(getHeaderHtml());
        final String title = moduleName.toSourceText();
        generateTitleWithIcon(buffer, url, title);
        buffer.append("<h2>" + NavigatorMessages.getString("NAV_AvailableModules_Header") + "</h2>");
        buffer.append(getModuleListInNamespaceHtml(owner, moduleName));            
        buffer.append(getFooterHtml());
        
        return buffer.toString();
    }

    /**
     * Generates a title with an icon.
     * @param buffer the HTML buffer.
     * @param url the NavAddress of the entity documented.
     * @param title the title.
     */
    private static void generateTitleWithIcon(StringBuilder buffer, NavAddress url, final String title) {
        buffer.append("<table border=0 cellpadding=0 cellspacing=0 width=100%><tr><td valign='top'><h1>" + title + "</h1><td align='right'>");
        addAppropriateIcon(buffer, url);
        buffer.append("</tr></table>");
    }
    
    /**
     * @param owner the navigator owner
     * @param url the url to display the search page for
     * @return the HTML for the search page
     */
    private static String getSearchPage(NavFrameOwner owner, NavAddress url) {
        
        StringBuilder buffer = new StringBuilder();
        buffer.append(getHeaderHtml());
        buffer.append(getSearchResultHtml(owner, url));
        buffer.append(getFooterHtml());
            
        return buffer.toString();        
    }
    
    /**
     * @param owner the navigator owner
     * @param url the url to display the vault page for
     * @return the HTML for the vault page
     */
    private static String getVaultPage(NavFrameOwner owner, NavAddress url) {

        String vault = url.getParameter(NavAddress.VAULT_PARAMETER);
        String vaultHtml = null;

        if (vault.equals(NavAddress.CLASS_VAULT_VALUE)) {
            vaultHtml = getClassVaultHtml(owner, url);
            
        } else if (vault.equals(NavAddress.FUNCTION_VAULT_VALUE)) {
            vaultHtml = getFunctionVaultHtml(owner, url);
            
        } else if (vault.equals(NavAddress.INSTANCE_VAULT_VALUE)) {
            vaultHtml = getInstanceVaultHtml(owner, url);
            
        } else if (vault.equals(NavAddress.TYPE_VAULT_VALUE)) {
            vaultHtml = getTypeVaultHtml(owner, url);
        }
        
        if (vaultHtml == null) {
            return getErrorPage(url);
        
        } else {
            
            StringBuilder buffer = new StringBuilder();
            buffer.append(getHeaderHtml());
            buffer.append(getVaultPageHeader(owner, url));
            buffer.append(vaultHtml);
            buffer.append(getFooterHtml());
            
            return buffer.toString();        
        }
    }
    
    /**
     * @param owner the navigator owner
     * @param url the url to get a vault page header for
     * @return the HTML for a vault page header
     */
    private static String getVaultPageHeader(NavFrameOwner owner, NavAddress url) {
        
        StringBuilder buffer = new StringBuilder();
        buffer.append(getPageLocationHtml(owner, url));
        buffer.append("<table cellpadding='1' cellspacing='0' width='100%' bgcolor='#E0E0E0'><tr><td nowrap>");
        buffer.append("<font face='arial' size='2'>");
                
        ModuleName moduleName = ModuleName.make(url.getBase());
        MetaModule module = owner.getPerspective().getMetaModule(moduleName);

        url = url.withAllStripped();
        
        if (module.getTypeInfo().getNFunctions() > 0) {
            NavAddress vaultUrl = url.withParameter(NavAddress.VAULT_PARAMETER, NavAddress.FUNCTION_VAULT_VALUE);
            buffer.append(getLinkHtml(vaultUrl, NavigatorMessages.getString("NAV_Functions_Link")));
            buffer.append(" | ");
        }
        
        if (module.getTypeInfo().getNTypeConstructors() > 0) {
            NavAddress vaultUrl = url.withParameter(NavAddress.VAULT_PARAMETER, NavAddress.TYPE_VAULT_VALUE);
            buffer.append(getLinkHtml(vaultUrl, NavigatorMessages.getString("NAV_Types_Link")));
            buffer.append(" | ");
        }
        
        if (module.getTypeInfo().getNTypeClasses() > 0) {
            NavAddress vaultUrl = url.withParameter(NavAddress.VAULT_PARAMETER, NavAddress.CLASS_VAULT_VALUE);
            buffer.append(getLinkHtml(vaultUrl, NavigatorMessages.getString("NAV_Classes_Link")));
            buffer.append(" | ");
        }
        
        if (module.getTypeInfo().getNClassInstances() > 0) {
            NavAddress vaultUrl = url.withParameter(NavAddress.VAULT_PARAMETER, NavAddress.INSTANCE_VAULT_VALUE);
            buffer.append(getLinkHtml(vaultUrl, NavigatorMessages.getString("NAV_Instances_Link")));
            buffer.append(" | ");
        }
        
        // strip trailing pipe
        buffer.delete(buffer.length() - 3, buffer.length());
        
        buffer.append("</font>");
        buffer.append("</td></tr></table>");
        
        return buffer.toString();
    }

    /**
     * @param owner the navigator owner
     * @return the HTML code for a hyperlinked list of modules from the workspace
     */
    private static String getModuleListHtml(NavFrameOwner owner) {
        return getModuleListInNamespaceHtml(owner, null);
    }

    /**
     * @param owner the navigator owner
     * @param namespaceModuleName the namespace module name, or null if all modules are to be listed.
     * @return the HTML code for a hyperlinked list of modules from the workspace
     */
    private static String getModuleListInNamespaceHtml(final NavFrameOwner owner, final ModuleName namespaceModuleName) {

        // get a sorted list of all modules        
        List<MetaModule> modules = owner.getPerspective().getVisibleMetaModules();
        modules.addAll(owner.getPerspective().getInvisibleMetaModules());
        Collections.sort(modules, new MetaModuleComparator());
        
        StringBuilder buffer = new StringBuilder();
        buffer.append("<tt>");
        
        for (final MetaModule metaModule : modules) {
            if (namespaceModuleName == null || namespaceModuleName.isProperPrefixOf(metaModule.getName())) {
                NavAddress moduleUrl = NavAddress.getAddress(metaModule);
                buffer.append(getLinkHtml(moduleUrl, NavAddressHelper.getDisplayText(owner, moduleUrl)));
                buffer.append(", ");
            }
        }
        
        // delete trailing comma
        buffer.delete(buffer.length() - 2, buffer.length());
        buffer.append("</tt>");
        
        return buffer.toString();
    }

    /**
     * @param owner the navigator owner
     * @param url the url for the search results
     * @return the HTML for displaying the search results the URL points to
     */
    private static String getSearchResultHtml(NavFrameOwner owner, NavAddress url) {

        // group the results by type
        String searchString = url.getBase();
        List<NavAddress> results = owner.searchMetadata(searchString);
        List<NavAddress> functions = new ArrayList<NavAddress>();
        List<NavAddress> types = new ArrayList<NavAddress>();
        List<NavAddress> classes = new ArrayList<NavAddress>();
        List<NavAddress> instances = new ArrayList<NavAddress>();
        List<NavAddress> modules = new ArrayList<NavAddress>();

        for (final NavAddress result : results) {
            
            if (result.getMethod() == NavAddress.FUNCTION_METHOD) {
                functions.add(result);
                
            } else if (result.getMethod() == NavAddress.TYPE_CONSTRUCTOR_METHOD) {
                types.add(result);
            
            } else if (result.getMethod() == NavAddress.TYPE_CLASS_METHOD) {
                classes.add(result);
            
            } else if (result.getMethod() == NavAddress.CLASS_INSTANCE_METHOD) {
                instances.add(result);
                
            } else if (result.getMethod() == NavAddress.MODULE_METHOD) {
                modules.add(result);
            }
        }
        
        // sort the results
        Comparator<NavAddress> urlSorter = new NavAddressComparator(owner);
        Collections.sort(functions, urlSorter);
        Collections.sort(types, urlSorter);
        Collections.sort(classes, urlSorter);
        Collections.sort(instances, urlSorter);
        Collections.sort(modules, urlSorter);

        // add the top bar with links to the anchors
        StringBuilder buffer = new StringBuilder();
        buffer.append(getPageLocationHtml(owner, url));
        
        if (results.size() > 0) {
            buffer.append("<table cellpadding='1' cellspacing='0' width='100%' bgcolor='#E0E0E0'><tr><td nowrap>");
            buffer.append("<font face='arial' size='2'>");
    
            if (functions.size() > 0) {
                buffer.append(getLinkHtml(FUNCTIONS_ANCHOR, NavigatorMessages.getString("NAV_Functions_Link")));
                buffer.append(" | ");
            }
            
            if (types.size() > 0) {
                buffer.append(getLinkHtml(TYPES_ANCHOR, NavigatorMessages.getString("NAV_Types_Link")));
                buffer.append(" | ");
            }
            
            if (classes.size() > 0) {
                buffer.append(getLinkHtml(CLASSES_ANCHOR, NavigatorMessages.getString("NAV_Classes_Link")));
                buffer.append(" | ");
            }
            
            if (instances.size() > 0) {
                buffer.append(getLinkHtml(INSTANCES_ANCHOR, NavigatorMessages.getString("NAV_Instances_Link")));
                buffer.append(" | ");
            }
    
            if (modules.size() > 0) {
                buffer.append(getLinkHtml(MODULES_ANCHOR, NavigatorMessages.getString("NAV_Modules_Link")));
                buffer.append(" | ");
            }
            
            // remove trailing pipe
            buffer.delete(buffer.length() - 3, buffer.length());
            buffer.append("</font>");
            buffer.append("</td></tr></table>");
        }        

        // format the message that indicates the number of search results
        String message = NavigatorMessages.getString("NAV_SearchResults_Message");
        String[] choices = { NavigatorMessages.getString("NAV_SearchResultsNone"),
                             NavigatorMessages.getString("NAV_SearchResultsOne"),
                             NavigatorMessages.getString("NAV_SearchResultsMultiple") };
        
        double[] limits = { 0, 1, 2 };
        Format[] formats = { new ChoiceFormat(limits, choices), null };
        Object[] arguments = { Integer.valueOf(results.size()), url.getBase() };

        MessageFormat messageFormat = new MessageFormat(message);
        messageFormat.setFormats(formats);

        // now print the message and put the search results below it
        buffer.append("<h1>" + NavigatorMessages.getString("NAV_SearchResults_Header") + "</h1>");
        buffer.append(messageFormat.format(arguments));
        
        Pattern searchPattern = Pattern.compile(WildcardPatternMatcher.wildcardPatternToRegExp(searchString), Pattern.CASE_INSENSITIVE);
        
        if (functions.size() > 0) {
            buffer.append("<h2>" + getAnchorHtml(FUNCTIONS_ANCHOR, NavigatorMessages.getString("NAV_Functions_Header")) + "</h2>");
            
            for (final NavAddress result : functions) {
                buffer.append(getLinkHtml(result, getMatchesHighlightedHtml(searchPattern, NavAddressHelper.getDisplayText(owner, result))));
                buffer.append("<br>");
            }
            
            // remove trailing <br>
            buffer.delete(buffer.length() - 3, buffer.length());
        }

        if (types.size() > 0) {
            buffer.append("<h2>" + getAnchorHtml(TYPES_ANCHOR, NavigatorMessages.getString("NAV_Types_Header")) + "</h2>");
            
            for (final NavAddress result : types) {
                buffer.append(getLinkHtml(result, getMatchesHighlightedHtml(searchPattern, NavAddressHelper.getDisplayText(owner, result))));
                buffer.append("<br>");
            }

            // remove trailing <br>
            buffer.delete(buffer.length() - 3, buffer.length());
        }

        if (classes.size() > 0) {
            buffer.append("<h2>" + getAnchorHtml(CLASSES_ANCHOR, NavigatorMessages.getString("NAV_Classes_Header")) + "</h2>");
            
            for (final NavAddress result : classes) {
                buffer.append(getLinkHtml(result, getMatchesHighlightedHtml(searchPattern, NavAddressHelper.getDisplayText(owner, result))));
                buffer.append("<br>");
            }

            // remove trailing <br>
            buffer.delete(buffer.length() - 3, buffer.length());
        }

        if (instances.size() > 0) {
            buffer.append("<h2>" + getAnchorHtml(INSTANCES_ANCHOR, NavigatorMessages.getString("NAV_Instances_Header")) + "</h2>");
            
            for (final NavAddress result : instances) {
                buffer.append(getLinkHtml(result, getMatchesHighlightedHtml(searchPattern, NavAddressHelper.getDisplayText(owner, result))));                    
                buffer.append("<br>");
            }

            // remove trailing <br>
            buffer.delete(buffer.length() - 3, buffer.length());
        }
        
        if (modules.size() > 0) {
            buffer.append("<h2>" + getAnchorHtml(MODULES_ANCHOR, NavigatorMessages.getString("NAV_Modules_Header")) + "</h2>");            
            
            for (final NavAddress result : modules) {
                buffer.append(getLinkHtml(result, getMatchesHighlightedHtml(searchPattern, NavAddressHelper.getDisplayText(owner, result))));
                buffer.append("<br>");
            }

            // remove trailing <br>
            buffer.delete(buffer.length() - 3, buffer.length());
        }

        return buffer.toString();        
    }
    
    /**
     * @param searchPattern the regexp search pattern
     * @param text the text containing the matches to be highlighted
     * @return the HTML code for the text, including the appropriate highlighting tags
     */
    private static String getMatchesHighlightedHtml(Pattern searchPattern, String text) {
        return searchPattern.matcher(text).replaceAll("<b>$0</b>");
    }
    
    /**
     * @param owner the navigator owner
     * @param url the module address to display the vault for
     * @return the HTML code for the module function vault
     */
    private static String getFunctionVaultHtml(NavFrameOwner owner, NavAddress url) {

        StringBuilder buffer = new StringBuilder();
        String moduleLink = getLinkHtml(url.withAllStripped(), NavAddressHelper.getDisplayText(owner, url.withAllStripped()));
        
        buffer.append("<h1>" + NavigatorMessages.getString("NAV_FunctionVault_Header") + "</h1>");
        buffer.append(NavigatorMessages.getString("NAV_FunctionVault_Message", moduleLink));
        buffer.append("<br><br><tt>");
        
        ModuleName moduleName = ModuleName.make(url.getBase());
        MetaModule module = owner.getPerspective().getMetaModule(moduleName);
        SortedSet<ScopedEntity> entities = new TreeSet<ScopedEntity>(new ScopedEntityComparator());
        int count = module.getTypeInfo().getNFunctions();
        
        for (int n = 0; n < count; n++) {
            entities.add(module.getTypeInfo().getNthFunction(n));
        }

        for (final ScopedEntity entity : entities) {
            NavAddress entityAddress = NavAddress.getAddress(entity);
            buffer.append(getLinkHtml(entityAddress, NavAddressHelper.getDisplayText(owner, entityAddress, ScopedEntityNamingPolicy.UNQUALIFIED)));
            buffer.append(", ");
        }
        
        buffer.delete(buffer.length() - 2, buffer.length());   // stip trailing comma
        buffer.append("</tt>");
        
        return buffer.toString();
    }

    /**
     * @param owner the navigator owner
     * @param url the module address to display the vault for
     * @return the HTML code for the module class vault
     */
    private static String getClassVaultHtml(NavFrameOwner owner, NavAddress url) {

        StringBuilder buffer = new StringBuilder();
        String moduleLink = getLinkHtml(url.withAllStripped(), NavAddressHelper.getDisplayText(owner, url.withAllStripped()));
        
        buffer.append("<h1>" + NavigatorMessages.getString("NAV_ClassVault_Header") + "</h1>");
        buffer.append(NavigatorMessages.getString("NAV_ClassVault_Message", moduleLink));
        buffer.append("<br><br><tt>");
        
        ModuleName moduleName = ModuleName.make(url.getBase());
        MetaModule module = owner.getPerspective().getMetaModule(moduleName);
        SortedSet<TypeClass> entities = new TreeSet<TypeClass>(new ScopedEntityComparator());
        int count = module.getTypeInfo().getNTypeClasses();
        
        for (int n = 0; n < count; n++) {
            entities.add(module.getTypeInfo().getNthTypeClass(n));
        }
           
        for (final ScopedEntity entity : entities) {
            NavAddress entityAddress = NavAddress.getAddress(entity);
            buffer.append(getLinkHtml(entityAddress, NavAddressHelper.getDisplayText(owner, entityAddress, ScopedEntityNamingPolicy.UNQUALIFIED)));
            buffer.append(", ");
        }
        
        buffer.delete(buffer.length() - 2, buffer.length());   // stip trailing comma
        buffer.append("</tt>");
        
        return buffer.toString();
    }

    /**
     * @param owner the navigator owner
     * @param url the module address to display the vault for
     * @return the HTML code for the module type vault
     */
    private static String getTypeVaultHtml(NavFrameOwner owner, NavAddress url) {

        StringBuilder buffer = new StringBuilder();
        String moduleLink = getLinkHtml(url.withAllStripped(), NavAddressHelper.getDisplayText(owner, url.withAllStripped()));
        
        buffer.append("<h1>" + NavigatorMessages.getString("NAV_TypeVault_Header") + "</h1>");
        buffer.append(NavigatorMessages.getString("NAV_TypeVault_Message", moduleLink));
        buffer.append("<br><br><tt>");
        
        ModuleName moduleName = ModuleName.make(url.getBase());
        MetaModule module = owner.getPerspective().getMetaModule(moduleName);
        SortedSet<TypeConstructor> entities = new TreeSet<TypeConstructor>(new ScopedEntityComparator());
        int count = module.getTypeInfo().getNTypeConstructors();
        
        for (int n = 0; n < count; n++) {
            entities.add(module.getTypeInfo().getNthTypeConstructor(n));
        }

        for (final ScopedEntity entity : entities) {
            NavAddress entityAddress = NavAddress.getAddress(entity); 
            buffer.append(getLinkHtml(entityAddress, NavAddressHelper.getDisplayText(owner, entityAddress, ScopedEntityNamingPolicy.UNQUALIFIED)));
            buffer.append(", ");
        }
        
        buffer.delete(buffer.length() - 2, buffer.length());   // stip trailing comma
        buffer.append("</tt>");
        
        return buffer.toString();
    }

    /**
     * @param owner the navigator owner
     * @param url the module address to display the vault for
     * @return the HTML code for the module instance vault
     */
    private static String getInstanceVaultHtml(NavFrameOwner owner, NavAddress url) {

        StringBuilder buffer = new StringBuilder();
        String moduleLink = getLinkHtml(url.withAllStripped(), NavAddressHelper.getDisplayText(owner, url.withAllStripped()));
        
        buffer.append("<h1>" + NavigatorMessages.getString("NAV_InstanceVault_Header") + "</h1>");
        buffer.append(NavigatorMessages.getString("NAV_InstanceVault_Message", moduleLink));
        buffer.append("<br><br><tt>");
        
        ModuleName moduleName = ModuleName.make(url.getBase());
        MetaModule module = owner.getPerspective().getMetaModule(moduleName);
        SortedSet<ClassInstance> instances = new TreeSet<ClassInstance>(new ClassInstanceComparator());
        int count = module.getTypeInfo().getNClassInstances();
        
        for (int n = 0; n < count; n++) {
            instances.add(module.getTypeInfo().getNthClassInstance(n));
        }

        ScopedEntityNamingPolicy namingPolicy = new UnqualifiedUnlessAmbiguous(module.getTypeInfo());

        for (final ClassInstance instance : instances) {
            NavAddress address = NavAddress.getAddress(instance);
            buffer.append("&nbsp;&nbsp;" + getLinkHtml(address, instance.getNameWithContext(namingPolicy)));
            buffer.append("<br>");
        }
        
        buffer.append("</tt>");
        
        return buffer.toString();
    }

    
    /**
     * @param owner the navigator owner
     * @param metadata the metadata to get basic HTML for
     * @param caldoc the associated CALDoc comment
     * @return the HTML for the basic metadata information
     */
    private static String getBasicMetadataHtml(NavFrameOwner owner, CALFeatureMetadata metadata, CALDocComment caldoc) {
        
        StringBuilder buffer = new StringBuilder();

        // Display the description for the gem.
        buffer.append("<h2>" + getAnchorHtml(DESCRIPTION_ANCHOR, NavigatorMessages.getString("NAV_Description_Header")) + "</h2>");
        if (metadata.getLongDescription() != null) {
            buffer.append(metadata.getLongDescription());
        } else if (metadata.getShortDescription() != null) {
            buffer.append(metadata.getShortDescription());
        } else {
            if (caldoc != null) {
                String caldocHtml = getHtmlForCALDocTextBlock(caldoc.getDescriptionBlock(), owner, false);
                if (caldocHtml.length() > 0) {
                    buffer.append(caldocHtml);
                } else {
                    buffer.append(NavigatorMessages.getString("NAV_NoValue"));
                }
            } else {
                buffer.append(NavigatorMessages.getString("NAV_NoValue"));
            }
        }
        
        return buffer.toString();        
    }

    /**
     * @param owner the navigator owner
     * @param metadata the metadata to get basic HTML for
     * @param caldoc the associated CALDoc comment
     * @return the HTML for additional metadata information
     */
    private static String getAdditionalMetadataHtml(NavFrameOwner owner, CALFeatureMetadata metadata, CALDocComment caldoc) {
        
        StringBuilder buffer = new StringBuilder();
        
        // Add a General section
        buffer.append("<h2>" + getAnchorHtml(GENERAL_ANCHOR, NavigatorMessages.getString("NAV_General_Header")) + "</h2>");
        
        buffer.append("<table border='0'>");
        buffer.append("<tr><td valign='top'>" + NavigatorMessages.getString("NAV_CreationDate") + "</td><td>");
        Date creationDate = metadata.getCreationDate();
        if (creationDate == null) {
            buffer.append(NavigatorMessages.getString("NAV_UnknownValue"));
        } else {
            buffer.append(LOCALE_DATE_FORMAT.format(creationDate));
        }
                
        buffer.append("</td></tr><tr><td valign='top'>" + NavigatorMessages.getString("NAV_ModificationDate") + "</td><td>");
        Date modificationDate = metadata.getModificationDate();
        if (modificationDate == null) {
            buffer.append(NavigatorMessages.getString("NAV_UnknownValue"));
        } else {
            buffer.append(LOCALE_DATE_FORMAT.format(modificationDate));
        }
        
        buffer.append("</td></tr><tr><td valign='top'>" + NavigatorMessages.getString("NAV_Author") + "</td><td>");
        String author = metadata.getAuthor();
        if (author == null || author.length() == 0) {
            // default to display the information in the CALDoc @author tags
            boolean seenAtLeastOneAuthorTag = false;
            if (caldoc != null) {
                for (int i = 0, nAuthors = caldoc.getNAuthorBlocks(); i < nAuthors; i++) {
                    CALDocComment.TextBlock block = caldoc.getNthAuthorBlock(i);
                    buffer.append(getHtmlForCALDocTextBlock(block, owner, false));
                    if (i < nAuthors - 1) {
                        buffer.append("<br>");
                    }
                    seenAtLeastOneAuthorTag = true;
                }
            }
            
            if (!seenAtLeastOneAuthorTag) {
                buffer.append(NavigatorMessages.getString("NAV_UnknownValue"));
            }
        } else {
            buffer.append(author);
        }
            
        buffer.append("</td></tr><tr><td valign='top'>" + NavigatorMessages.getString("NAV_Version") + "</td><td>");
        String version = metadata.getVersion();
        if (version == null || version.length() == 0) {
            // default to display the information in the CALDoc @version tag
            boolean seenVersionTag = false;
            if (caldoc != null) {
                CALDocComment.TextBlock versionBlock = caldoc.getVersionBlock();
                if (versionBlock != null) {
                    buffer.append(getHtmlForCALDocTextBlock(versionBlock, owner, false));
                    seenVersionTag = true;
                }
            }
            
            if (!seenVersionTag) {
                buffer.append(NavigatorMessages.getString("NAV_UnknownValue"));
            }
        } else {
            buffer.append(version);
        }
        
        if (metadata instanceof ScopedEntityMetadata) {
            CALFeatureName featureName = ((ScopedEntityMetadata) metadata).getFeatureName();
            ScopedEntity entity = owner.getPerspective().getWorkspace().getScopedEntity(featureName);
            
            if (entity != null) {
                buffer.append("</td></tr><tr><td valign='top'>" + NavigatorMessages.getString("NAV_Visibility") + "</td><td>");
                buffer.append(entity.getScope().toString());
            }
        }
        
        if (metadata instanceof FunctionalAgentMetadata) {
    
            buffer.append("</td></tr><tr><td valign='top'>" + NavigatorMessages.getString("NAV_Categories") + "</td><td>");
            String[] categories = ((FunctionalAgentMetadata) metadata).getCategories();
            if (categories.length == 0) {
                buffer.append(NavigatorMessages.getString("NAV_NoValue"));
            
            } else {
                
                for (final String category : categories) {
                    buffer.append(category);
                    buffer.append(", ");
                }
                    
                // remove trailing comma
                buffer.delete(buffer.length() - 2, buffer.length());
            }
        }
        
        buffer.append("</td></tr></table>");
        
        // Put in a section for the related features.
        buffer.append("<h2>" + getAnchorHtml(RELATED_ANCHOR, NavigatorMessages.getString("NAV_RelatedFeatures_Header")) + "</h2>");
        
        int featureCount = metadata.getNRelatedFeatures();
        if (featureCount == 0) {
            if (caldoc != null) {
                // add the CALDoc @see references if the metadata specifies no related features
                
                boolean seenSeeBlock = false;
                
                for (int i = 0, nModuleRefs = caldoc.getNModuleReferences(); i < nModuleRefs; i++) {
                    seenSeeBlock = true;
                    
                    ModuleName name = caldoc.getNthModuleReference(i).getName();
                    CALFeatureName featureName = CALFeatureName.getModuleFeatureName(name);
                    buffer.append(getRelatedFeatureLinkHtml(owner, featureName)).append("<br>");
                }
                
                for (int i = 0, nFuncRefs = caldoc.getNFunctionOrClassMethodReferences(); i < nFuncRefs; i++) {
                    seenSeeBlock = true;
                    
                    QualifiedName name = caldoc.getNthFunctionOrClassMethodReference(i).getName();
                    CALFeatureName featureName;
                    if (isNameForClassMethod(owner, name)) {
                        featureName = CALFeatureName.getClassMethodFeatureName(name);
                    } else {
                        featureName = CALFeatureName.getFunctionFeatureName(name);
                    }
                    buffer.append(getRelatedFeatureLinkHtml(owner, featureName)).append("<br>");
                }
                
                for (int i = 0, nTypeConsRefs = caldoc.getNTypeConstructorReferences(); i < nTypeConsRefs; i++) {
                    seenSeeBlock = true;
                    
                    QualifiedName name = caldoc.getNthTypeConstructorReference(i).getName();
                    CALFeatureName featureName = CALFeatureName.getTypeConstructorFeatureName(name);
                    buffer.append(getRelatedFeatureLinkHtml(owner, featureName)).append("<br>");
                }
                
                for (int i = 0, nDataConsRefs = caldoc.getNDataConstructorReferences(); i < nDataConsRefs; i++) {
                    seenSeeBlock = true;
                    
                    QualifiedName name = caldoc.getNthDataConstructorReference(i).getName();
                    CALFeatureName featureName = CALFeatureName.getDataConstructorFeatureName(name);
                    buffer.append(getRelatedFeatureLinkHtml(owner, featureName)).append("<br>");
                }
                
                for (int i = 0, nTypeClassRefs = caldoc.getNTypeClassReferences(); i < nTypeClassRefs; i++) {
                    seenSeeBlock = true;
                    
                    QualifiedName name = caldoc.getNthTypeClassReference(i).getName();
                    CALFeatureName featureName = CALFeatureName.getTypeClassFeatureName(name);
                    buffer.append(getRelatedFeatureLinkHtml(owner, featureName)).append("<br>");
                }
                
                if (seenSeeBlock) {
                    // remove trailing <br>
                    buffer.delete(buffer.length() - 4, buffer.length());
                } else {
                    buffer.append(NavigatorMessages.getString("NAV_NoValue"));
                }
            } else {
                buffer.append(NavigatorMessages.getString("NAV_NoValue"));
            }
        
        } else {
            
            for (int i = 0; i < featureCount; i++) {
                buffer.append(getRelatedFeatureLinkHtml(owner, metadata.getNthRelatedFeature(i))).append("<br>");
            }
            
            // remove trailing <br>
            buffer.delete(buffer.length() - 4, buffer.length());
        }
        
        // Put in a section for the custom attributes.
        buffer.append("<h2>" + getAnchorHtml(CUSTOM_ATTRIBUTES_ANCHOR, NavigatorMessages.getString("NAV_CustomAttributes_Header")) + "</h2>");
        
        Iterator<String> it = metadata.getAttributeNames();
        if (it.hasNext()) {
            
            buffer.append("<table border='0'>");
            
            while (it.hasNext()) {
                String attributeName = it.next();
                String attributeValue = metadata.getAttribute(attributeName);
                
                buffer.append("<tr><td valign='top'>");
                buffer.append(attributeName);
                buffer.append(NavigatorMessages.getString("NAV_CustomAttributeNameValueSeparator"));
                buffer.append("</td><td valign='top'>");
                if (attributeValue != null) {
                    buffer.append(attributeValue);
                } else {
                    buffer.append(NavigatorMessages.getString("NAV_UnknownValue"));
                }
                buffer.append("</td></tr>");
            }
            
            buffer.append("</table>");
            
        } else {
            buffer.append(NavigatorMessages.getString("NAV_NoValue"));
        }
        
        return buffer.toString();
    }
    
    /**
     * @param owner the navigator owner.
     * @param featureName the feature name of the related feature.
     * @return the HTML for a link to the related feature's page, for a disabled link if the related feature has no metadata.
     */
    private static String getRelatedFeatureLinkHtml(NavFrameOwner owner, CALFeatureName featureName) {
        NavAddress address = NavAddress.getAddress(featureName);
        String displayName = NavAddressHelper.getDisplayText(owner, address);
        return getRelatedFeatureLinkHtml(owner, address, displayName);
    }

    /**
     * @param owner the navigator owner.
     * @param featureName the feature name of the related feature.
     * @param moduleNameInSource how the module name portion of the reference appears in source. Could be the empty string if the reference is unqualified in source.
     * @return the HTML for a link to the related feature's page, for a disabled link if the related feature has no metadata.
     */
    private static String getRelatedFeatureLinkHtml(NavFrameOwner owner, CALFeatureName featureName, String moduleNameInSource) {
        NavAddress address = NavAddress.getAddress(featureName);
        
        String displayName;
        if (featureName.isScopedEntityName()) {
            
            String unqualifiedName = featureName.toQualifiedName().getUnqualifiedName();
            if (moduleNameInSource.length() == 0) {
                displayName = unqualifiedName;
            } else {
                displayName = moduleNameInSource + '.' + unqualifiedName;
            }
            
        } else if (featureName.getType() == CALFeatureName.MODULE && moduleNameInSource.length() > 0) {
            displayName = moduleNameInSource;
            
        } else {
            displayName = NavAddressHelper.getDisplayText(owner, address);
        }
        
        return getRelatedFeatureLinkHtml(owner, address, displayName);
    }

    /**
     * @param owner the navigator owner.
     * @param address the address of the feature to link to.
     * @param displayName the name to be displayed for the link.
     * @return the HTML for a link to the related feature's page, for a disabled link if the related feature has no metadata.
     */
    private static String getRelatedFeatureLinkHtml(NavFrameOwner owner, NavAddress address, String displayName) {
        // If there is metadata for a feature, then provide a link to it.
        // Otherwise, show the name of the feature in a disabled style.
        CALFeatureMetadata featureMetadata = NavAddressHelper.getMetadata(owner, address);
        
        if (featureMetadata != null) {
            return getLinkHtml(address, displayName);
        } else {
            return getDisabledHtml(displayName);
        }
    }

    /**
     * @param owner the navigator owner
     * @param metadata the metadata to get HTML for
     * @return the HTML for module metadata specific information
     */
    private static String getModuleMetadataHtml(NavFrameOwner owner, ModuleMetadata metadata) {
        
        StringBuilder buffer = new StringBuilder();

        // create a list of imported modules      
        buffer.append("<h2>" + getAnchorHtml(IMPORTS_ANCHOR, NavigatorMessages.getString("NAV_ImportedModules_Header")) + "</h2>");

        SortedSet<ModuleTypeInfo> importedModules = new TreeSet<ModuleTypeInfo>(new ModuleTypeInfoComparator());
        
        ModuleName moduleName = metadata.getFeatureName().toModuleName();
        ModuleTypeInfo moduleTypeInfo = owner.getPerspective().getMetaModule(moduleName).getTypeInfo();
        int nImportedModules = moduleTypeInfo.getNImportedModules();
        
        if (nImportedModules == 0) {
            buffer.append(NavigatorMessages.getString("NAV_NoValue"));

        } else {
            for (int n = 0; n < nImportedModules; n++) {
                importedModules.add(moduleTypeInfo.getNthImportedModule(n));
            }
            
            buffer.append("<tt>");
            
            for (final ModuleTypeInfo importedModuleInfo : importedModules) {
                NavAddress moduleAddress = NavAddress.getAddress(importedModuleInfo);
                buffer.append(getLinkHtml(moduleAddress, NavAddressHelper.getDisplayText(owner, moduleAddress)));
                buffer.append(", ");
            }
            
            // remove trailing comma
            buffer.delete(buffer.length() - 2, buffer.length());
            buffer.append("</tt>");
        }
       
        // create a list of friend modules   
        buffer.append("<h2>" + getAnchorHtml(FRIENDS_ANCHOR, NavigatorMessages.getString("NAV_FriendModules_Header")) + "</h2>");

        SortedSet<ModuleName> friendModules = new TreeSet<ModuleName>();
        
        int nFriendModules = moduleTypeInfo.getNFriendModules();
        
        if (nFriendModules == 0) {
            buffer.append(NavigatorMessages.getString("NAV_NoValue"));

        } else {
            for (int n = 0; n < nFriendModules; n++) {
                friendModules.add(moduleTypeInfo.getNthFriendModule(n));
            }
            
            buffer.append("<tt>");
            
            for (final ModuleName friendModule : friendModules) {
                NavAddress moduleAddress = NavAddress.getAddress(CALFeatureName.getModuleFeatureName(friendModule));
                String displayText = NavAddressHelper.getDisplayText(owner, moduleAddress);
                
                CALFeatureMetadata friendMetadata = NavAddressHelper.getMetadata(owner, moduleAddress);
                if (friendMetadata != null) {
                    buffer.append(getLinkHtml(moduleAddress, displayText));
                } else {
                    buffer.append(getDisabledHtml(displayText));
                }
                buffer.append(", ");
            }
            
            // remove trailing comma
            buffer.delete(buffer.length() - 2, buffer.length());
            buffer.append("</tt>");
        }
       
        return buffer.toString();
    }
    
    /**
     * @param owner the navigator owner
     * @param metadata the metadata to get HTML for
     * @return the HTML for type constructor metadata specific information
     */
    private static String getTypeConstructorMetadataHtml(NavFrameOwner owner, TypeConstructorMetadata metadata) {
        
        TypeConstructor typeCons = (TypeConstructor) owner.getPerspective().getWorkspace().getScopedEntity(metadata.getFeatureName());
        StringBuilder buffer = new StringBuilder();
        
        ScopedEntityNamingPolicy namingPolicy = new UnqualifiedUnlessAmbiguous(owner.getPerspective().getWorkingModuleTypeInfo());
        
        // list the data constructors for this type
        buffer.append("<h2>" + getAnchorHtml(CONSTRUCTORS_ANCHOR, NavigatorMessages.getString("NAV_Constructors_Header")) + "</h2>");
        int count = typeCons.getNDataConstructors();
        
        if (count == 0) {
            buffer.append(NavigatorMessages.getString("NAV_NoValue"));
        
        } else {

            buffer.append("<tt>");
                       
            for (int i = 0; i < count; i++) {
                DataConstructor dataCons = typeCons.getNthDataConstructor(i);
                NavAddress dataConsUrl = NavAddress.getAddress(dataCons);
                String dataConsLink = getLinkHtml(dataConsUrl, NavAddressHelper.getDisplayText(owner, dataConsUrl, ScopedEntityNamingPolicy.UNQUALIFIED));
                buffer.append("<b>" + dataConsLink + "</b> :: ");
                buffer.append("<i>" + getTypeStringHtml(owner, dataCons.getTypeExpr(), namingPolicy) + "</i>");
                buffer.append("<br>");
            }
            
            // remove trailing <br>
            buffer.delete(buffer.length() - 4, buffer.length());
            buffer.append("</tt>");
        }
        
        // we have to search all instances in all modules to see if they're an instance of this type
        //todoBI the above comment is false. Need to just search all module imported, either directly or indirectly,
        //into the module in which the instance is defined.
        CALWorkspace workspace = owner.getPerspective().getWorkspace();
        List<TypeClass> classes = new ArrayList<TypeClass>();
        List<ClassInstance> instances = new ArrayList<ClassInstance>();
        
        for (int n = 0, numMods = workspace.getNMetaModules(); n < numMods; n++) {
            
            MetaModule metaModule = workspace.getNthMetaModule(n);
            ModuleTypeInfo moduleTypeInfo = metaModule.getTypeInfo();
            
            for (int i = 0, num = moduleTypeInfo.getNClassInstances(); i < num; i++) {
                
                ClassInstance instance = moduleTypeInfo.getNthClassInstance(i);
                
                if (instance.isTypeConstructorInstance() &&
                    ((ClassInstanceIdentifier.TypeConstructorInstance)instance.getIdentifier()).getTypeConsName().equals(typeCons.getName())) {
                    classes.add(instance.getTypeClass());
                    instances.add(instance);
                }
            }
        }

        // list the implemented classes for this type
        buffer.append("<h2>" + getAnchorHtml(CLASSES_ANCHOR, NavigatorMessages.getString("NAV_InstanceClasses_Header")) + "</h2>");
        
        if (classes.isEmpty()) {
            buffer.append(NavigatorMessages.getString("NAV_NoValue"));
        
        } else {

            Collections.sort(classes, new ScopedEntityComparator());
            
            buffer.append("<tt>");
            
            for (final TypeClass typeClass : classes) {
                NavAddress address = NavAddress.getAddress(typeClass);
                buffer.append(getLinkHtml(address, NavAddressHelper.getDisplayText(owner, address, namingPolicy)));
                buffer.append(", ");
            }
        
            buffer.delete(buffer.length() - 2, buffer.length());   // strip trailing comma
            buffer.append("</tt>");
        }

        // list the implemented instances for this type
        buffer.append("<h2>" + getAnchorHtml(INSTANCES_ANCHOR, NavigatorMessages.getString("NAV_Instances_Header")) + "</h2>");
        
        if (instances.isEmpty()) {
            buffer.append(NavigatorMessages.getString("NAV_NoValue"));
            
        } else {

            Collections.sort(instances, new ClassInstanceComparator());
            
            buffer.append("<tt>");
            
            for (final ClassInstance instance : instances) {
                NavAddress address = NavAddress.getAddress(instance);
                buffer.append("&nbsp;&nbsp;");
                buffer.append(getLinkHtml(address, NavAddressHelper.getDisplayText(owner, address, namingPolicy)));
                buffer.append("<br>");
            }
            
            buffer.delete(buffer.length() - 4, buffer.length());   // strip trailing <br>
            buffer.append("</tt>");
        }
        
        return buffer.toString();
    }
    
    /**
     * @param owner the navigator owner
     * @param metadata the metadata to get HTML for
     * @return the HTML for type class metadata specific information
     */
    private static String getTypeClassMetadataHtml(NavFrameOwner owner, TypeClassMetadata metadata) {

        TypeClass typeClass = (TypeClass) owner.getPerspective().getWorkspace().getScopedEntity(metadata.getFeatureName());
        StringBuilder buffer = new StringBuilder();

        ScopedEntityNamingPolicy namingPolicy = new UnqualifiedUnlessAmbiguous(owner.getPerspective().getWorkingModuleTypeInfo());
        
        // list the parent classes of the class        
        buffer.append("<h2>" + getAnchorHtml(PARENTS_ANCHOR, NavigatorMessages.getString("NAV_ParentClasses_Header")) + "</h2>");
        int count = typeClass.getNParentClasses();
        
        if (count == 0) {
            buffer.append(NavigatorMessages.getString("NAV_NoValue"));
            
        } else {

            SortedSet<TypeClass> parents = new TreeSet<TypeClass>(new ScopedEntityQualifiedComparator());
            
            for (int i = 0; i < count; i++) {
                parents.add(typeClass.getNthParentClass(i));
            }
            
            buffer.append("<tt>");
            for (final TypeClass parentClass : parents) {
                NavAddress parentUrl = NavAddress.getAddress(parentClass);
                buffer.append(getLinkHtml(parentUrl, NavAddressHelper.getDisplayText(owner, parentUrl, namingPolicy)) + ", ");
            }
    
            // remove trailing comma
            buffer.delete(buffer.length() - 2, buffer.length());
            buffer.append("</tt>");
        }
        
        // list the class methods of this class
        buffer.append("<h2>" + getAnchorHtml(METHODS_ANCHOR, NavigatorMessages.getString("NAV_ClassMethods_Header")) + "</h2>");
        
        buffer.append("<tt>");
        
        count = typeClass.getNClassMethods();
        for (int i = 0; i < count; i++) {
            ClassMethod method = typeClass.getNthClassMethod(i);
            NavAddress methodUrl = NavAddress.getAddress(method);
            buffer.append("<b>" + getLinkHtml(methodUrl, NavAddressHelper.getDisplayText(owner, methodUrl, ScopedEntityNamingPolicy.UNQUALIFIED) + "</b> :: "));
            buffer.append("<i>" + getTypeStringHtml(owner, method.getTypeExpr(), namingPolicy) + "</i>");
            buffer.append("<br>");
        }
        
        // remove trailing <br>
        buffer.delete(buffer.length() - 4, buffer.length());
        buffer.append("</tt>");
        
        // we have to search all instances in all modules to see if they're an instance of this class
        CALWorkspace workspace = owner.getPerspective().getWorkspace();
        List<ClassInstance> instances = new ArrayList<ClassInstance>();
        List<TypeConstructor> types = new ArrayList<TypeConstructor>();
        
        for (int n = 0, numMods = workspace.getNMetaModules(); n < numMods; n++) {
            
            MetaModule metaModule = workspace.getNthMetaModule(n);
            ModuleTypeInfo moduleTypeInfo = metaModule.getTypeInfo();
            
            for (int i = 0, num = moduleTypeInfo.getNClassInstances(); i < num; i++) {
                
                ClassInstance instance = moduleTypeInfo.getNthClassInstance(i);
                if (instance.isUniversalRecordInstance()) {
                    continue;
                }
                
                if (instance.getTypeClass().getName().equals(typeClass.getName())) {                   
                    QualifiedName typeConsName = ((TypeConsApp)instance.getType()).getName();
                    MetaModule typeModule = owner.getPerspective().getMetaModule(typeConsName.getModuleName());
                    TypeConstructor typeCons = typeModule.getTypeInfo().getTypeConstructor(typeConsName.getUnqualifiedName());
                    types.add(typeCons);
                    instances.add(instance);
                }
            }
        }

        // list the instance types of this class
        buffer.append("<h2>" + getAnchorHtml(TYPES_ANCHOR, NavigatorMessages.getString("NAV_InstanceTypes_Header")) + "</h2>");
        
        if (types.isEmpty()) {
            buffer.append(NavigatorMessages.getString("NAV_NoValue"));
            
        } else {

            Collections.sort(types, new ScopedEntityComparator());
            
            buffer.append("<tt>");
            
            for (final ScopedEntity entity : types) {
                NavAddress entityAddress = NavAddress.getAddress(entity); 
                buffer.append(getLinkHtml(entityAddress, NavAddressHelper.getDisplayText(owner, entityAddress, namingPolicy)));
                buffer.append(", ");
            }
            
            buffer.delete(buffer.length() - 2, buffer.length());   // strip trailing comma
            buffer.append("</tt>");
        }
        
        // list the instances of this class
        buffer.append("<h2>" + getAnchorHtml(INSTANCES_ANCHOR, NavigatorMessages.getString("NAV_Instances_Header")) + "</h2>");

        if (instances.isEmpty()) {
            buffer.append(NavigatorMessages.getString("NAV_NoValue"));
            
        } else {

            Collections.sort(instances, new ClassInstanceComparator());
            
            buffer.append("<tt>");
            
            for (final ClassInstance instance : instances) {
                NavAddress instanceAddress = NavAddress.getAddress(instance); 
                buffer.append("&nbsp;&nbsp;");
                buffer.append(getLinkHtml(instanceAddress, NavAddressHelper.getDisplayText(owner, instanceAddress, namingPolicy)));
                buffer.append("<br>");
            }
            
            buffer.delete(buffer.length() - 4, buffer.length());   // strip trailing <br>
            buffer.append("</tt>");
        }
        
        return buffer.toString();
    }
    
    /**
     * @param owner the navigator owner
     * @param metadata the metadata to get HTML for
     * @param url the address of the page
     * @param caldoc the CALDoc comment associated with the instance method, or null if there is none 
     * @return the HTML for entity metadata specific information
     */
    private static String getInstanceMethodMetadataHtml(NavFrameOwner owner, InstanceMethodMetadata metadata, NavAddress url, CALDocComment caldoc) {
        return
        getFunctionalAgentOrInstanceMethodArgumentsAndReturnValueHtml(owner, metadata.getArguments(), metadata.getReturnValueDescription(), url, caldoc) +
        getFunctionalAgentOrInstanceMethodExamplesHtml(owner, metadata.getExamples());
    }
    
    /**
     * @param owner the navigator owner
     * @param metadata the metadata to get HTML for
     * @return the HTML for class instance metadata specific information
     */
    private static String getClassInstanceMetadataHtml(NavFrameOwner owner, ClassInstanceMetadata metadata) {
        
        StringBuilder buffer = new StringBuilder();

        ScopedEntityNamingPolicy namingPolicy = new UnqualifiedUnlessAmbiguous(owner.getPerspective().getWorkingModuleTypeInfo());        
        ClassInstanceIdentifier identifier = metadata.getFeatureName().toInstanceIdentifier();
                
        CALFeatureName className = CALFeatureName.getTypeClassFeatureName(identifier.getTypeClassName());        
        TypeClass typeClass = (TypeClass) owner.getPerspective().getWorkspace().getScopedEntity(className);
        
        // list the instance class and type        
        buffer.append("<h2>" + getAnchorHtml(INSTANCE_CLASS_ANCHOR, NavigatorMessages.getString("NAV_InstanceClass_Header")) + "</h2>");
        buffer.append(getLinkHtml(NavAddress.getAddress(typeClass), typeClass.getAdaptedName(namingPolicy)));
        
        if (identifier instanceof ClassInstanceIdentifier.TypeConstructorInstance) {               
            CALFeatureName typeName = CALFeatureName.getTypeConstructorFeatureName(((ClassInstanceIdentifier.TypeConstructorInstance)identifier).getTypeConsName());
            TypeConstructor typeCons = (TypeConstructor) owner.getPerspective().getWorkspace().getScopedEntity(typeName);
            buffer.append("<h2>" + getAnchorHtml(INSTANCE_TYPE_ANCHOR, NavigatorMessages.getString("NAV_InstanceType_Header")) + "</h2>");
            buffer.append(getLinkHtml(NavAddress.getAddress(typeCons), typeCons.getAdaptedName(namingPolicy)));
        }
        
        // list the instance methods of this instance
        buffer.append("<h2>" + getAnchorHtml(INSTANCE_METHODS_ANCHOR, NavigatorMessages.getString("NAV_InstanceMethods_Header")) + "</h2>");
        
        buffer.append("<tt>");
        
        ModuleTypeInfo featureModuleTypeInfo = owner.getPerspective().getMetaModule(metadata.getFeatureName().toModuleName()).getTypeInfo();
        ClassInstance instance = featureModuleTypeInfo.getClassInstance(identifier);
        
        int count = instance.getNInstanceMethods();
        for (int i = 0; i < count; i++) {
            ClassMethod method = typeClass.getNthClassMethod(i);
            String methodName = method.getName().getUnqualifiedName();
            
            NavAddress methodUrl = NavAddress.getAddress(CALFeatureName.getInstanceMethodFeatureName(identifier, metadata.getFeatureName().toModuleName(), methodName));
            buffer.append("<b>" + getLinkHtml(methodUrl, NavAddressHelper.getDisplayText(owner, methodUrl, ScopedEntityNamingPolicy.UNQUALIFIED) + "</b> :: "));
            buffer.append("<i>" + getTypeStringHtml(owner, instance.getInstanceMethodType(i), namingPolicy) + "</i>");
            buffer.append("<br>");
        }
        
        // remove trailing <br>
        buffer.delete(buffer.length() - 4, buffer.length());
        buffer.append("</tt>");
        
        return buffer.toString();
    }
        
    /**
     * @param owner the navigator owner
     * @param metadata the metadata to get HTML for
     * @param url the address of the page
     * @param caldoc the CALDoc comment associated with the entity, or null if there is none 
     * @param isRequiredClassMethod whether the documented entity is a required class method
     * @return the HTML for entity metadata specific information
     */
    private static String getFunctionalAgentMetadataHtml(NavFrameOwner owner, FunctionalAgentMetadata metadata, NavAddress url, CALDocComment caldoc, boolean isRequiredClassMethod) {
        String returnValueDesc;
        
        StringBuilder buffer = new StringBuilder();
        
        if (metadata instanceof FunctionMetadata) {
            returnValueDesc = ((FunctionMetadata)metadata).getReturnValueDescription();
            
            buffer.append(getFunctionalAgentOrInstanceMethodArgumentsAndReturnValueHtml(owner, metadata.getArguments(), returnValueDesc, url, caldoc));
            
        } else if (metadata instanceof ClassMethodMetadata) {
            returnValueDesc = ((ClassMethodMetadata)metadata).getReturnValueDescription();
            
            buffer.append(getFunctionalAgentOrInstanceMethodArgumentsAndReturnValueHtml(owner, metadata.getArguments(), returnValueDesc, url, caldoc));
            // for class method we add the display of whether it is a required method
            buffer.append("<h2>").append(getAnchorHtml(REQUIRED_METHOD_ANCHOR, NavigatorMessages.getString("NAV_RequiredMethod"))).append("</h2>");
            buffer.append("<table border='0'><tr><td>");
            if (isRequiredClassMethod) {
                buffer.append(NavigatorMessages.getString("NAV_RequiredMethodYes"));
            } else {
                buffer.append(NavigatorMessages.getString("NAV_RequiredMethodNo"));
            }
            buffer.append("</td></tr></table>");
            
        } else {
            returnValueDesc = null;
            
            buffer.append(getFunctionalAgentOrInstanceMethodArgumentsAndReturnValueHtml(owner, metadata.getArguments(), returnValueDesc, url, caldoc));
        }
        
        buffer.append(getFunctionalAgentOrInstanceMethodExamplesHtml(owner, metadata.getExamples()));
        
        return buffer.toString();
    }
    
    /**
     * @param argMeta the metadata for the arguments
     * @param returnValueDesc the return value description in the metadata
     * @param url the address of the page
     * @param caldoc the CALDoc comment associated with the entity, or null if there is none
     * @return the HTML for the argument and return value information of the entity
     */
    private static String getFunctionalAgentOrInstanceMethodArgumentsAndReturnValueHtml(NavFrameOwner owner, ArgumentMetadata[] argMeta, String returnValueDesc, NavAddress url, CALDocComment caldoc) {
        StringBuilder buffer = new StringBuilder();
        
        ScopedEntityNamingPolicy namingPolicy = new UnqualifiedUnlessAmbiguous(owner.getPerspective().getWorkingModuleTypeInfo());
        String[] normalStrings = NavAddressHelper.getTypeStrings(owner, url, namingPolicy);
        String[] qualifiedStrings = NavAddressHelper.getTypeStrings(owner, url, ScopedEntityNamingPolicy.FULLY_QUALIFIED);

        if (normalStrings.length > 0) {
            // A collector that is not connected does not have a result type.
            
            // Display the result type.
            buffer.append("<h2>" + getAnchorHtml(RESULT_ANCHOR, NavigatorMessages.getString("NAV_ReturnValue_Header")) + "</h2>");
            
            buffer.append("<table border='0'>");
            buffer.append("<tr><td nowrap valign='top'>");
            
            String typeString = getTypeStringHtml(owner, qualifiedStrings[qualifiedStrings.length - 1], normalStrings[normalStrings.length - 1]);
            
            buffer.append("<tt><b><i>" + NavigatorMessages.getString("NAV_ReturnValueIndicator")
                            + "</i></b> :: <i>" + typeString 
                            + "</i></tt></td>");

            if (returnValueDesc != null) {
                buffer.append("<td nowrap valign='top'> - ");
                buffer.append(returnValueDesc);
                buffer.append("</td></tr></table>");
            } else {
                if (caldoc != null && caldoc.getReturnBlock() != null) {
                    buffer.append("<td nowrap valign='top'> - </td><td valign='top'>");
                    buffer.append(NavHtmlFactory.getHtmlForCALDocTextBlock(caldoc.getReturnBlock(), owner, false));
                    buffer.append("</td></tr></table>");
                }
                buffer.append("</tr></table>");
            }
        }

        // Display the argument information.
        buffer.append("<h2>" + getAnchorHtml(ARGUMENTS_ANCHOR, NavigatorMessages.getString("NAV_Arguments_Header")) + "</h2>");
        
        NavAddressHelper.adjustArgumentNames(owner, url, argMeta);
        
        if (argMeta.length == 0) {
            buffer.append(NavigatorMessages.getString("NAV_NoValue"));

        } else {
            for (int i = 0; i < argMeta.length; i++) {

                buffer.append("<table border='0'>");
                buffer.append("<tr><td nowrap valign='top'>");
                
                String typeString = getTypeStringHtml(owner, qualifiedStrings[i], normalStrings[i]);
                
                buffer.append("<tt><b>" + argMeta[i].getDisplayName() 
                                + "</b> :: <i>" + typeString 
                                + "</i></tt></td>");

                String argDescription = argMeta[i].getShortDescription(); 
                if (argDescription != null) {
                    buffer.append("<td nowrap valign='top'> - ");
                    buffer.append(argDescription);
                    buffer.append("</td></tr></table>");
                } else {
                    if (caldoc != null && i < caldoc.getNArgBlocks()) {
                        buffer.append("<td nowrap valign='top'> - </td><td valign='top'>");
                        buffer.append(NavHtmlFactory.getHtmlForCALDocTextBlock(caldoc.getNthArgBlock(i).getTextBlock(), owner, false));
                        buffer.append("</td></tr></table>");
                    }
                    buffer.append("</tr></table>");
                }

                if (argMeta[i].getDefaultValuesExpression() != null) {
                    
                    buffer.append("<font size='-1'>");
                    buffer.append(NavigatorMessages.getString("NAV_DefaultValues_Message"));
                    
                    if (argMeta[i].useDefaultValuesOnly()) {
                        buffer.append(NavigatorMessages.getString("NAV_DefaultValuesOnly_Message"));
                    }
                    
                    buffer.append("</font>");
                }
            }
        }
        
        return buffer.toString();
    }
    
    /**
     * @param owner the navigator owner
     * @param examples the included examples in the metadata
     * @return the HTML for the entity's example metadata
     */
    private static String getFunctionalAgentOrInstanceMethodExamplesHtml(NavFrameOwner owner, CALExample[] examples) {
        StringBuilder buffer = new StringBuilder();
        
        // Display the examples
        buffer.append("<h2>" + getAnchorHtml(EXAMPLES_ANCHOR, NavigatorMessages.getString("NAV_Examples_Header")) + "</h2>");
        
        if (examples.length == 0) {
            buffer.append(NavigatorMessages.getString("NAV_NoValue"));
        
        } else {
        
            for (int i = 0; i < examples.length; i++) {
                
                CALExpression expression = examples[i].getExpression();
                String description = examples[i].getDescription() != null ? examples[i].getDescription() : NavigatorMessages.getString("NAV_NoValue");

                buffer.append("<h3>" + NavigatorMessages.getString("NAV_ExampleNumber", Integer.valueOf(i+1)) + "</h3>");
                
                buffer.append("<table border='0' width='100%'>");                    
                
                buffer.append("<tr>");
                buffer.append("<td rowspan='3' width='20'>&nbsp;</td>");
                buffer.append("<td width='120'>" + NavigatorMessages.getString("NAV_Description") + "</td>");
                buffer.append("<td>" + description + "</td>");
                buffer.append("</tr>");
                
                buffer.append("<tr>");
                buffer.append("<td>" + NavigatorMessages.getString("NAV_Expression") + "</td>");
                if (expression.getQualifiedExpressionText().equals("")) {
                    buffer.append("<td><tt>" + expression.getExpressionText() + "</tt></td>");
                } else {
                    buffer.append("<td><tt>" + expression.getQualifiedExpressionText() + "</tt></td>");
                }
                buffer.append("</tr>");
                
                if (examples[i].evaluateExample()) {
                    buffer.append("<tr>");
                    buffer.append("<td>" + NavigatorMessages.getString("NAV_Result") + "</td>");
                    buffer.append("<td><tt>" + getExpressionHtml(owner, expression)+ "</tt></td>");
                    buffer.append("</tr>");
                }
                
                buffer.append("</table>");
            }
            
            // remove trailing &nbsp;
            buffer.delete(buffer.length() - 6, buffer.length());
        }
        
        return buffer.toString();
    }

    /**
     * Generate the HTML for the summary of a CALDoc comment.
     * @param caldoc the CALDoc comment.
     * @param locale the locale for the documentation generation.
     * @param alwaysAsNewParagraph whether the generated HTML should always start a new paragraph (e.g. with a &lt;p&gt; block).
     * @return the HTML for the summary of a CALDoc comment.
     */
    public static String getHtmlForCALDocSummaryWithNoHyperlinks(CALDocComment caldoc, Locale locale, boolean alwaysAsNewParagraph) {
        
        String contents = NavigatorMessages.getString(
            "NAV_CALDoc",
            CALDocToHTMLUtilities.getHTMLForCALDocSummary(
                caldoc, new NoHyperlinkCrossReferenceGenerator(), locale, NavigatorMessages.getString("NAV_CALDocReturnsColon")));
        
        if (alwaysAsNewParagraph) {
            return "<p>" + contents;
        } else {
            return contents;
        }
    }
    
    /**
     * Generate the HTML for a text block appearing in a CALDoc comment.
     * @param textBlock the text block to be formatted as HTML.
     * @param alwaysAsNewParagraph whether the generated HTML should always start a new paragraph (e.g. with a &lt;p&gt; block).
     * @return the HTML for the text block.
     */
    public static String getHtmlForCALDocTextBlockWithNoHyperlinks(CALDocComment.TextBlock textBlock, boolean alwaysAsNewParagraph) {
        
        String contents = NavigatorMessages.getString("NAV_CALDoc", CALDocToHTMLUtilities.getHTMLForCALDocTextBlock(textBlock, new NoHyperlinkCrossReferenceGenerator()));
        
        if (alwaysAsNewParagraph) {
            return "<p>" + contents;
        } else {
            return contents;
        }
    }
    
    /**
     * Generate the HTML for the summary of a CALDoc comment.
     * @param caldoc the CALDoc comment.
     * @param owner the navigator owner.
     * @param alwaysAsNewParagraph whether the generated HTML should always start a new paragraph (e.g. with a &lt;p&gt; block).
     * @return the HTML for the summary of a CALDoc comment.
     */
    public static String getHtmlForCALDocSummary(CALDocComment caldoc, NavFrameOwner owner, boolean alwaysAsNewParagraph) {
        
        String contents = NavigatorMessages.getString(
            "NAV_CALDoc",
            CALDocToHTMLUtilities.getHTMLForCALDocSummary(
                caldoc, new CrossReferenceGenerator(owner), owner.getLocaleForMetadata(), NavigatorMessages.getString("NAV_CALDocReturnsColon")));
        
        if (alwaysAsNewParagraph) {
            return "<p>" + contents;
        } else {
            return contents;
        }
    }
    
    /**
     * Generate the HTML for a text block appearing in a CALDoc comment.
     * @param textBlock the text block to be formatted as HTML.
     * @param owner the navigator owner.
     * @param alwaysAsNewParagraph whether the generated HTML should always start a new paragraph (e.g. with a &lt;p&gt; block).
     * @return the HTML for the text block.
     */
    public static String getHtmlForCALDocTextBlock(CALDocComment.TextBlock textBlock, NavFrameOwner owner, boolean alwaysAsNewParagraph) {
        
        String contents = NavigatorMessages.getString("NAV_CALDoc", CALDocToHTMLUtilities.getHTMLForCALDocTextBlock(textBlock, new CrossReferenceGenerator(owner)));
        
        if (alwaysAsNewParagraph) {
            return "<p>" + contents;
        } else {
            return contents;
        }
    }
    
    /**
     * @param owner the navigator owner
     * @param url the address of the page
     * @return the HTML for displaying the location of the page in the page hierarchy
     */
    private static String getPageLocationHtml(NavFrameOwner owner, NavAddress url) {

        String separator = " >> ";
        StringBuilder buffer = new StringBuilder();
        CALFeatureMetadata metadata = NavAddressHelper.getMetadata(owner, url);

        buffer.append("<table cellpadding='1' cellspacing='0' width='100%' bgcolor='#E0E0E0'><tr><td nowrap>");
        buffer.append("<font size='3'><tt>");
        buffer.append(getLinkHtml(NavAddress.getRootAddress(NavAddress.WORKSPACE_METHOD), NavigatorMessages.getString("NAV_Workspace_Location")));
        buffer.append(separator);

        if (url.getMethod() == NavAddress.SEARCH_METHOD) {
            buffer.append(NavigatorMessages.getString("NAV_SearchResults_Location"));
        
        } else if (url.getMethod() == NavAddress.COLLECTOR_METHOD) {
            buffer.append(NavAddressHelper.getDisplayText(owner, url) + NavigatorMessages.getString("NAV_Collector_Location"));
        
        } else if (url.getParameter(NavAddress.VAULT_PARAMETER) != null) {

            // add link to module
            buffer.append(getLinkHtml(url.withAllStripped(), NavAddressHelper.getDisplayText(owner, url.withAllStripped())));
            buffer.append(separator);

            // add a link to the vault
            String vault = url.getParameter(NavAddress.VAULT_PARAMETER);
            if (vault.equals(NavAddress.TYPE_VAULT_VALUE)) {
                buffer.append(NavigatorMessages.getString("NAV_TypeVault_Location"));

            } else if (vault.equals(NavAddress.CLASS_VAULT_VALUE)) {
                buffer.append(NavigatorMessages.getString("NAV_ClassVault_Location"));

            } else if (vault.equals(NavAddress.FUNCTION_VAULT_VALUE)) {
                buffer.append(NavigatorMessages.getString("NAV_FunctionVault_Location"));

            } else if (vault.equals(NavAddress.INSTANCE_VAULT_VALUE)) {
                buffer.append(NavigatorMessages.getString("NAV_InstanceVault_Location"));
            }
        
        } else if (metadata instanceof ScopedEntityMetadata || metadata instanceof ClassInstanceMetadata || metadata instanceof InstanceMethodMetadata) {
                
            String typeString = null;
            CALFeatureName featureName = metadata.getFeatureName();
            
            // add link to module
            ModuleName moduleName = featureName.toModuleName();
            MetaModule module = owner.getPerspective().getMetaModule(moduleName);
            NavAddress moduleUrl = NavAddress.getAddress(module);
            buffer.append(getLinkHtml(NavAddress.getAddress(module), moduleName.toSourceText()));
            buffer.append(separator);

            // add link to parent or vault
            if (metadata instanceof FunctionMetadata) {
                NavAddress vaultUrl = moduleUrl.withParameter(NavAddress.VAULT_PARAMETER, NavAddress.FUNCTION_VAULT_VALUE);
                buffer.append(getLinkHtml(vaultUrl, NavigatorMessages.getString("NAV_FunctionVault_Location")));
                typeString = NavigatorMessages.getString("NAV_Function_Location");

            } else if (metadata instanceof TypeConstructorMetadata) {
                NavAddress vaultUrl = moduleUrl.withParameter(NavAddress.VAULT_PARAMETER, NavAddress.TYPE_VAULT_VALUE);
                buffer.append(getLinkHtml(vaultUrl, NavigatorMessages.getString("NAV_TypeVault_Location")));
                typeString = NavigatorMessages.getString("NAV_Type_Location");
                
            } else if (metadata instanceof TypeClassMetadata) {
                NavAddress vaultUrl = moduleUrl.withParameter(NavAddress.VAULT_PARAMETER, NavAddress.CLASS_VAULT_VALUE);
                buffer.append(getLinkHtml(vaultUrl, NavigatorMessages.getString("NAV_ClassVault_Location")));
                typeString = NavigatorMessages.getString("NAV_Class_Location");
                
            } else if (metadata instanceof ClassInstanceMetadata) {
                NavAddress vaultUrl = moduleUrl.withParameter(NavAddress.VAULT_PARAMETER, NavAddress.INSTANCE_VAULT_VALUE);
                buffer.append(getLinkHtml(vaultUrl, NavigatorMessages.getString("NAV_InstanceVault_Location")));
                typeString = NavigatorMessages.getString("NAV_Instance_Location");                    
            
            } else if (metadata instanceof InstanceMethodMetadata) {
                CALFeatureName methodFeatureName = metadata.getFeatureName();
                NavAddress parentUrl = NavAddress.getAddress(CALFeatureName.getClassInstanceFeatureName(
                    methodFeatureName.toInstanceIdentifier(), methodFeatureName.toModuleName()));
                
                buffer.append(getLinkHtml(parentUrl, NavAddressHelper.getDisplayText(owner, parentUrl, ScopedEntityNamingPolicy.UNQUALIFIED)));
                typeString = NavigatorMessages.getString("NAV_InstanceMethod_Location");                    
            
            } else if (metadata instanceof ClassMethodMetadata) {

                typeString = NavigatorMessages.getString("NAV_ClassMethod_Location");

                // figure out the type class this method belongs to
                TypeClass parentClass = null;
                CALFeatureName methodFeatureName = ((ClassMethodMetadata) metadata).getFeatureName();
                QualifiedName methodName = methodFeatureName.toQualifiedName();
                int classCount = module.getTypeInfo().getNTypeClasses();
                
                for (int i = 0; i < classCount; i++) {
                    TypeClass typeClass = module.getTypeInfo().getNthTypeClass(i);
                    
                    int methodCount = typeClass.getNClassMethods();
                    for (int n = 0; n < methodCount; n++) {
                        if (typeClass.getNthClassMethod(n).getName().equals(methodName)) {
                            parentClass = typeClass;
                            break;
                        }
                    }
                    
                    if (parentClass != null) {
                        break;
                    }
                }
                
                if (parentClass != null) {
                    NavAddress parentUrl = NavAddress.getAddress(parentClass);
                    buffer.append(getLinkHtml(parentUrl, NavAddressHelper.getDisplayText(owner, parentUrl, ScopedEntityNamingPolicy.UNQUALIFIED)));
                } else {
                    buffer.append("<font color='red'>" + NavigatorMessages.getString("NAV_ClassNotFound_Location") + "</font>");
                }
                
            } else if (metadata instanceof DataConstructorMetadata) {
                
                typeString = NavigatorMessages.getString("NAV_Constructor_Location");
                
                // figure out the type constructor this data constructor is for
                CALFeatureName dataConsFeatureName = ((DataConstructorMetadata) metadata).getFeatureName();
                QualifiedName dataConsName = dataConsFeatureName.toQualifiedName();
                TypeConstructor parentCons = null;
                int typeConsCount = module.getTypeInfo().getNTypeConstructors();
                
                for (int i = 0; i < typeConsCount; i++) {
                    TypeConstructor typeCons = module.getTypeInfo().getNthTypeConstructor(i);
                    
                    int dataConsCount = typeCons.getNDataConstructors();
                    for (int n = 0; n < dataConsCount; n++) {
                        if (typeCons.getNthDataConstructor(n).getName().equals(dataConsName)) {
                            parentCons = typeCons;
                            break;
                        }
                    }
                    
                    if (parentCons != null) {
                        break;
                    }
                }
                
                if (parentCons != null) {
                    NavAddress parentUrl = NavAddress.getAddress(parentCons);
                    buffer.append(getLinkHtml(parentUrl, NavAddressHelper.getDisplayText(owner, parentUrl, ScopedEntityNamingPolicy.UNQUALIFIED)));
                } else {
                    buffer.append("<font color='red'>" + NavigatorMessages.getString("NAV_ConstructorNotFound_Location") + "</font>");
                }
            }
            
            buffer.append(separator);

            // add a link to entity itself
            buffer.append(NavAddressHelper.getDisplayText(owner, url, ScopedEntityNamingPolicy.UNQUALIFIED) + typeString);
        
        } else if (metadata instanceof ModuleMetadata) {
            
            // add a link to the module
            buffer.append(NavAddressHelper.getDisplayText(owner, url) + NavigatorMessages.getString("NAV_Module_Location"));
        }

        buffer.append("</tt></font>");
        buffer.append("</td></tr></table>");

        return buffer.toString();
    }

    /**
     * @return the HTML code that is included on top of every page to define style elements
     */
    private static String getHeaderHtml() {

        StringBuilder header = new StringBuilder();

        // Here we define several styles for the HTML rendered. Unfortunately not everything can
        // be defined using styles and we still have to use the <code> tag in the HTML.
        // This is because the Swing HTML widget does not render all CSS styles correctly.        
        header.append("<html><head><style type='text/css'>\n");
        header.append("body { font-family: sans-serif }\n");
        header.append("a { text-decoration: none }\n");
        header.append("code { font-size: larger }\n");
        header.append("</style></head>\n");
        header.append("<body bgcolor='#ffffff'>\n");
        
        return header.toString();
    } 
    
    /**
     * @return the HTML code that forms the footer of every page
     */
    private static String getFooterHtml() {
        return "</body></html>";
    }

    /**
     * A utility function for evaluating a CALExpression and returning the result as
     * an HTML formatted String.
     * @param expression the expression to evaluate
     * @return the HTML formatted result String
     */
    private static String getExpressionHtml(NavFrameOwner owner, CALExpression expression) {
        
        StringBuilder result = new StringBuilder();
        boolean success = EditorHelper.evaluateExpression(owner, expression, result);
        
        // Encode result to html
        result = new StringBuilder(HtmlHelper.htmlEncode(result.toString()));
        
        if (!success) {
            return "<font color='red'>" +  result.toString() + "</font>";
        } else {
            return result.toString();
        }
    }
    
    private static String getTypeStringHtml(NavFrameOwner owner, TypeExpr typeExpr, ScopedEntityNamingPolicy namingPolicy) {

        String qualified = typeExpr.toString(ScopedEntityNamingPolicy.FULLY_QUALIFIED);
        String custom = typeExpr.toString(namingPolicy);
        
        return getTypeStringHtml(owner, qualified, custom);
        
    }
    
    private static String getTypeStringHtml(NavFrameOwner owner, String qualified, String custom) {

        // We include '-' and '>' in the delimiters so that '->' in type expressions
        // doesn't get hyperlinked to 'Prelude.->'. Although technically that is a valid
        // link, it doesn't look very nice if all arrows in the expressions are links.
        String delims = "[](),-> ";
        
        StringBuilder buffer = new StringBuilder();
        StringTokenizer qualifiedTokens = new StringTokenizer(qualified, delims, true);
        StringTokenizer customTokens = new StringTokenizer(custom, delims, true);
        
        // Both tokenizers should returns the same number of tokens.
        while (qualifiedTokens.hasMoreTokens()) {
            
            String qualifiedToken = qualifiedTokens.nextToken();
            String customToken = customTokens.nextToken();
            
            if (delims.indexOf(qualifiedToken) == -1 && QualifiedName.isValidCompoundName(qualifiedToken)) {

                // In a TypeExpr we are either dealing with a type class or type constructor.
                QualifiedName name = QualifiedName.makeFromCompoundName(qualifiedToken);
                MetaModule metaModule = owner.getPerspective().getMetaModule(name.getModuleName());
                    
                if (metaModule != null) {
                    
                    TypeClass typeClass = metaModule.getTypeInfo().getTypeClass(name.getUnqualifiedName());
                    TypeConstructor typeCons = metaModule.getTypeInfo().getTypeConstructor(name.getUnqualifiedName());
                    
                    if (typeCons != null) {
                        customToken = getLinkHtml(NavAddress.getAddress(typeCons), customToken);
                        
                    } else if (typeClass != null) {
                        customToken = getLinkHtml(NavAddress.getAddress(typeClass), customToken);
                    }
                }
            }
            
            buffer.append(customToken);
        }
        
        return buffer.toString();
    }
    
    /**
     * @param link the location the link should point to
     * @param name the display text of the link
     * @return the HTML link code
     */
    private static String getLinkHtml(String link, String name) {
        return (link == null) ? name : "<a href='" + link + "'>" + name + "</a>"; 
    }
    
    /**
     * @param url the url to link to
     * @param name the display name of the link
     * @return the HTML link code
     */
    private static String getLinkHtml(NavAddress url, String name) {
        return getLinkHtml(url.toString(), name);
    }

    /**
     * A utility function to return the HTML code to create an anchor.
     * @param anchor the anchor name (must start with '#')
     * @param name the display text of the anchor
     * @return the HTML anchor code
     */    
    private static String getAnchorHtml(String anchor, String name) {
        return (anchor == null || !anchor.startsWith("#")) ? name : "<a name='" + anchor.substring(1) + "'>" + name + "</a>";
    }

    /** 
     * A utility function to return the HTML code to created a disabled
     * piece of text (eg, a "related feature" that cannot be linked to 
     * because it doesn't actually exist) 
     * @param text Text to be shown in disabled style
     * @return the HTML code showing text in disabled style
     */ 
    private static String getDisabledHtml(String text) {
        return (text == null) ? "" : "<font color='gray'>" + text + "</font>";
    }
}

/**
 * A comparator for sorting a list of ScopedEntity by the unqualified name.
 * @author Frank Worsley
 */
class ScopedEntityComparator implements Comparator<ScopedEntity> {

    public int compare(ScopedEntity e1, ScopedEntity e2) {
        return e1.getName().getUnqualifiedName().compareTo(e2.getName().getUnqualifiedName());
    }
}

/**
 * A comparator for sorting a list of ScopedEntity by the qualified name.
 * @author Frank Worsley
 */
class ScopedEntityQualifiedComparator implements Comparator<ScopedEntity> {

    public int compare(ScopedEntity e1, ScopedEntity e2) {
        return e1.getName().compareTo(e2.getName());
    }
}

/**
 * A comparator for sorting a list of MetaModule by the module name.
 * @author Frank Worsley
 */
class MetaModuleComparator implements Comparator<MetaModule> {

    public int compare(MetaModule m1, MetaModule m2) {
        return m1.getName().compareTo(m2.getName());
    }
}

/**
 * A comparator for sorting a list of ModuleTypeInfo by the module name.
 * @author Frank Worsley
 */
class ModuleTypeInfoComparator implements Comparator<ModuleTypeInfo> {

    public int compare(ModuleTypeInfo m1, ModuleTypeInfo m2) {
        return m1.getModuleName().compareTo(m2.getModuleName());
    }
}

/**
 * A comparator for sorting a list of ClassInstance by the instance name with context.
 * @author Frank Worsley
 */
class ClassInstanceComparator implements Comparator<ClassInstance> {

    public int compare(ClassInstance i1, ClassInstance i2) {
        //compare instance names without their context. Ignore the first (, so that parenthesizing the type constructor doesn't affect order.
        //e.g. Outputable Double is before Outputable (Either a b).
        String name1 = i1.getName().replaceFirst("\\(", "");
        String name2 = i2.getName().replaceFirst("\\(", "");
        return name1.compareTo(name2);
    }
}

/**
 * A comparator for sorting NavAddress instances using the best name of their metadata or
 * their CAL name if there is no metadata.
 * @author Frank Worsley
 */
class NavAddressComparator implements Comparator<NavAddress> {

    private final NavFrameOwner owner;

    public NavAddressComparator(NavFrameOwner owner) {
        this.owner = owner;
    }

    /**
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(NavAddress u1, NavAddress u2) {
        return NavAddressHelper.getDisplayText(owner, u1).compareTo(NavAddressHelper.getDisplayText(owner, u2));
    }
}
