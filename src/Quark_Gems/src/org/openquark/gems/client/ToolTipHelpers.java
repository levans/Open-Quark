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
 * ToolTipHelpers.java
 * Creation date: Jan 21st 2003.
 * By: Ken Wong
 */

package org.openquark.gems.client;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import javax.swing.JComponent;

import org.openquark.cal.compiler.CALDocComment;
import org.openquark.cal.compiler.FunctionalAgent;
import org.openquark.cal.compiler.PolymorphicVarContext;
import org.openquark.cal.compiler.ScopedEntity;
import org.openquark.cal.compiler.ScopedEntityNamingPolicy;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.metadata.ArgumentMetadata;
import org.openquark.cal.metadata.CALFeatureMetadata;
import org.openquark.cal.metadata.ClassMethodMetadata;
import org.openquark.cal.metadata.FunctionMetadata;
import org.openquark.cal.metadata.FunctionalAgentMetadata;
import org.openquark.cal.services.CALWorkspace;
import org.openquark.cal.services.GemEntity;
import org.openquark.gems.client.navigator.NavAddressHelper;
import org.openquark.gems.client.navigator.NavHtmlFactory;


/**
 * This class provides methods for constructing commonly used tooltips and general helper methods
 * for formatting text to display inside tooltips.
 * 
 * @author Ken Wong
 * @author Frank Worsley
 */
public class ToolTipHelpers {
    
    /** The width in pixels at which tooltip lines will be wrapped at. */
    private static final int TOOLTIP_MAX_WIDTH = 250; 
    
    /**
     * @param gem the functional agent gem for which to get a tooltip
     * @param parent the component to get the tooltip for
     * @param locale the locale for the CALDoc summary (for identifying sentence breaks using BreakIterator).
     * @return the HTML formatted tooltip text for a functional agent gem
     */
    public static String getFunctionalAgentToolTip(FunctionalAgentGem gem, JComponent parent, Locale locale) {
        
        GemEntity gemEntity = gem.getGemEntity();
        FunctionalAgentMetadata metadata = gemEntity.getMetadata(GemCutter.getLocaleFromPreferences());

        // Add the name and the description.
        // Note: we want to show the fully qualified name in the title, so don't use the naming policy
        GemEntity entity = gem.getGemEntity();
        // todo-jowong modify below if tooltips are to display minimally qualified module names
        String name = metadata.getDisplayName() != null ? entity.getName().getModuleName() + "." + metadata.getDisplayName()
                                                        : entity.getName().getQualifiedName();
        StringBuilder text = new StringBuilder();
        text.append("<html><body><b>");
        text.append(name);
        text.append("</b>");

        if (metadata.getShortDescription() != null) {
            text.append("<br>");
            text.append(metadata.getShortDescription());
        
        } else if (metadata.getLongDescription() != null) {
            text.append("<br>").append(wrapTextToHTMLLines(metadata.getLongDescription(), parent));            
        } else {
            // If there is neither a short nor a long description, display the CALDoc description instead.
            CALDocComment caldoc = gem.getGemEntity().getFunctionalAgent().getCALDocComment();
            if (caldoc != null) {
                String caldocDesc = NavHtmlFactory.getHtmlForCALDocSummaryWithNoHyperlinks(caldoc, locale, true);
                if (caldocDesc.length() > 0) {
                    text.append(caldocDesc);
                } else {
                    text.append("<br>");
                }
            }
        }
        
        text.append("</body></html>");

        return text.toString();
    }
    
    /**
     * @param gemEntity the entity for which to get a tooltip
     * @param namingPolicy the naming policy to use for displayed scoped entity names
     * @param parent the component to get a tooltip for
     * @return the HTML formatted tooltip text describing the given entity
     */
    public static String getEntityToolTip(GemEntity gemEntity, ScopedEntityNamingPolicy namingPolicy, JComponent parent) {
        Locale locale = GemCutter.getLocaleFromPreferences();
        CALFeatureMetadata metadata = gemEntity.getMetadata(locale);
        return getEntityToolTip(gemEntity.getFunctionalAgent(), namingPolicy, metadata, parent, locale);
    }

    /**
     * @param scopedEntity the entity for which to get a tooltip
     * @param namingPolicy the naming policy to use for displayed scoped entity names
     * @param workspace the metamodule in which the entity exists.
     * @param parent the component to get a tooltip for
     * @return the HTML formatted tooltip text describing the given entity
     */
    public static String getEntityToolTip(ScopedEntity scopedEntity, ScopedEntityNamingPolicy namingPolicy, CALWorkspace workspace, JComponent parent) {
        Locale locale = GemCutter.getLocaleFromPreferences();
        CALFeatureMetadata metadata = workspace.getMetadata(scopedEntity, locale);
        return getEntityToolTip(scopedEntity, namingPolicy, metadata, parent, locale);
    }

    /**
     * @param scopedEntity the entity for which to get a tooltip
     * @param namingPolicy the naming policy to use for displayed scoped entity names
     * @param metadata the metadata for the entity.
     * @param parent the component to get a tooltip for
     * @param locale the locale for the documentation generation.
     * @return the HTML formatted tooltip text describing the given entity
     */
    private static String getEntityToolTip(ScopedEntity scopedEntity, ScopedEntityNamingPolicy namingPolicy, CALFeatureMetadata metadata, JComponent parent, Locale locale) {
        
        StringBuilder text = new StringBuilder();
        
        // Start the tool tip with the name.
        // Note: we want to show the fully qualified name in the title, so don't use the naming policy
        // todo-jowong modify below if tooltips are to display minimally qualified module names
        String name = metadata.getDisplayName() != null ? scopedEntity.getName().getModuleName() + "." + metadata.getDisplayName()
                                                        : scopedEntity.getName().getQualifiedName();
        text.append("<html><body><b>");
        text.append(name);
        text.append("</b>");

        // Add the description
        if (metadata.getShortDescription() != null) {
            text.append("<br>");
            text.append(wrapTextToHTMLLines(metadata.getShortDescription(), parent));
            text.append("<br>");
        
        } else if (metadata.getLongDescription() != null) {
            
            // wrap the long description to a maximum line length
            text.append("<br>");
            text.append(wrapTextToHTMLLines(metadata.getLongDescription(), parent));
            text.append("<br>");
        } else {
            // If there is neither a short nor a long description, display the CALDoc description instead.
            CALDocComment caldoc = scopedEntity.getCALDocComment();
            if (caldoc != null) {
                String caldocDesc = getHTMLForBoundedToolTipText(
                    NavHtmlFactory.getHtmlForCALDocSummaryWithNoHyperlinks(caldoc, locale, true), TOOLTIP_MAX_WIDTH, 0, true, parent);
                
                if (caldocDesc.length() > 0) {
                    text.append(caldocDesc);
                } else {
                    text.append("<br>");
                }
            } else {
                text.append("<br>");
            }
        }

        if (metadata instanceof FunctionalAgentMetadata) {

            FunctionalAgent envEntity = (FunctionalAgent)scopedEntity;
            PolymorphicVarContext polymorphicVarContext = PolymorphicVarContext.make();
            TypeExpr[] typePieces = envEntity.getTypeExpr().getTypePieces();

            CALDocComment caldoc = envEntity.getCALDocComment();

            // Add the result type.            
            text.append("<br>");
            text.append("<b>");
            text.append(GemCutter.getResourceString("ToolTipHelper_Returns"));
            text.append("</b>");
            text.append("<br>");
            String returnValueIndicator = "<i>" + GemCutter.getResourceString("ToolTipHelper_ReturnValueIndicator") + "</i>";
            String returnType = typePieces[typePieces.length - 1].toString(polymorphicVarContext, namingPolicy);
            String returnValueMetadata;
            if (metadata instanceof FunctionMetadata) {
                returnValueMetadata = ((FunctionMetadata)metadata).getReturnValueDescription();
            } else if (metadata instanceof ClassMethodMetadata) {
                returnValueMetadata = ((ClassMethodMetadata)metadata).getReturnValueDescription();
            } else {
                returnValueMetadata = null;
            }
            CALDocComment.TextBlock returnValueCALDoc;
            if (caldoc != null) {
                returnValueCALDoc = caldoc.getReturnBlock();
            } else {
                returnValueCALDoc = null;
            }
            buildTypeSignatureAndDescription(parent, text, returnValueIndicator, returnType, returnValueMetadata, returnValueCALDoc, true);
    
            // Add the argument descriptions
            NavAddressHelper.isMetadataValid(envEntity, (FunctionalAgentMetadata) metadata);
            ArgumentMetadata[] arguments = ((FunctionalAgentMetadata) metadata).getArguments();
            NavAddressHelper.adjustArgumentNames(envEntity, arguments);

            if (arguments.length > 0) {
                text.append("<br>");                
                text.append("<b>");
                text.append(GemCutter.getResourceString("ToolTipHelper_Arguments"));
                text.append("</b>");
    
                // Process the tagged blocks in the CALDoc to extract the @arg blocks.
                text.append("<br>");
                for (int i = 0; i < arguments.length; i++) {
                    String displayName = arguments[i].getDisplayName();
                    String type = typePieces[i].toString(polymorphicVarContext, namingPolicy);
                    String descriptionFromMetadata = arguments[i].getShortDescription();
                    CALDocComment.TextBlock descriptionFromCALDoc;
                    if (caldoc != null && i < caldoc.getNArgBlocks()) {
                        descriptionFromCALDoc = caldoc.getNthArgBlock(i).getTextBlock();
                    } else {
                        descriptionFromCALDoc = null;
                    }
                    
                    boolean shouldEmitLineBreakSuffix = i < arguments.length - 1;
                    
                    buildTypeSignatureAndDescription(parent, text, displayName, type, descriptionFromMetadata, descriptionFromCALDoc, shouldEmitLineBreakSuffix);
                }
            }
        }
        
        text.append("</body></html>");
        
        return text.toString();
    }

    /**
     * Builds up a type signature and accompanying description in the given StringBuilder.
     * @param parent the parent JComponent (used for measuring the displayed width of strings).
     * @param buffer the StirngBuffer to be filled.
     * @param displayName the name of the documented item.
     * @param type the type of the documented item.
     * @param descriptionFromMetadata the description from metadata. Can be null.
     * @param descriptionFromCALDoc the description from CALDoc. Can be null.
     * @param shouldEmitLineBreakSuffix whether a line break should be emitted at the end.
     */
    private static void buildTypeSignatureAndDescription(JComponent parent, StringBuilder buffer, String displayName, String type, String descriptionFromMetadata, CALDocComment.TextBlock descriptionFromCALDoc, boolean shouldEmitLineBreakSuffix) {
        String textOfArgName = displayName + " :: ";
        int widthOfTextOfArgName = getDisplayedTextWidth(textOfArgName, parent);
        int widthOfArgNameAndType = getDisplayedTextWidth(textOfArgName + type + " - ", parent);
        
        buffer.append(textOfArgName);
        
        String htmlForType = "<i>" + type + "</i>";

        if (descriptionFromMetadata != null) {
            String description = descriptionFromMetadata;
            int maxWidthOfDescription = TOOLTIP_MAX_WIDTH - widthOfArgNameAndType;
            boolean multilineDescription = getDisplayedTextWidth(description, parent) > maxWidthOfDescription;
            buffer.append(getHTMLForBoundedToolTipText(htmlForType + (multilineDescription ? "" : " - "), TOOLTIP_MAX_WIDTH - widthOfTextOfArgName, 10, false, parent));
            buffer.append(getHTMLForBoundedToolTipText(description, maxWidthOfDescription, 20, shouldEmitLineBreakSuffix, parent));
        } else {
            // If the argument has no short description but has a CALDoc description, display the CALDoc description.
            if (descriptionFromCALDoc != null) {
                String description = NavHtmlFactory.getHtmlForCALDocTextBlockWithNoHyperlinks(descriptionFromCALDoc, false);
                int maxWidthOfDescription = TOOLTIP_MAX_WIDTH - widthOfArgNameAndType;
                boolean multilineDescription = getDisplayedTextWidth(description, parent) > maxWidthOfDescription;
                buffer.append(getHTMLForBoundedToolTipText(htmlForType + (multilineDescription ? "" : " - "), TOOLTIP_MAX_WIDTH - widthOfTextOfArgName, 10, false, parent));
                buffer.append(getHTMLForBoundedToolTipText(description, maxWidthOfDescription, 20, shouldEmitLineBreakSuffix, parent));
            } else {
                buffer.append(getHTMLForBoundedToolTipText(htmlForType, TOOLTIP_MAX_WIDTH - widthOfTextOfArgName, 10, shouldEmitLineBreakSuffix, parent));
            }
        }
    }
    
    /**
     * Return HTML for a properly word-wrapped piece of text.
     * @param text the text to be converted into word-wrapped HTML.
     * @param maxWidth the maximum width allowed on the first line before wrapping occurs.
     * @param indentByPixels the number of pixels to indent if the text needs to be wrapped onto multiple lines.
     * @param parent the parent component for the tooltip.
     * @return HTML for the text, properly word-wrapped.
     */
    private static String getHTMLForBoundedToolTipText(String text, int maxWidth, int indentByPixels, boolean shouldEmitLineBreakSuffixIfNotWrapped, JComponent parent) {
        if (getDisplayedTextWidth(text, parent) > maxWidth) {
            return "<table cellpadding='1' cellspacing='0' valign='top' width=" + TOOLTIP_MAX_WIDTH + "><tr><td width=" + indentByPixels + "></td><td>" + text + "</td></tr></table>";
        } else {
            return text + (shouldEmitLineBreakSuffixIfNotWrapped ? "<br>" : "");
        }
    }

    /**
     * @param part the PartConnectable to get a tooltip for
     * @param typeStringProvider the type string provider from which to get type strings
     * @param namingPolicy rules to use in determining the type string
     * @param parent the component requesting the tooltip
     * @return the HTML formatted tooltip for the PartConnectable
     */
    public static String getPartToolTip(Gem.PartConnectable part, TypeStringProvider typeStringProvider, ScopedEntityNamingPolicy namingPolicy, JComponent parent) {
        
        String argName = null;
        String argDesc = null;
        String argCALDocDesc = null;
        String argType = null;
        String argTarget = null;
        
        if (part instanceof Gem.PartInput) {
            
            // Show name and description for input parts.

            Gem gem = part.getGem();
            Gem.PartInput inputPart = (Gem.PartInput)part;
    
            if (inputPart.isConnected() || inputPart.isBurnt()) {
                // Don't show a description for connected or burnt inputs.
                argDesc = null;
                
            } else {
                
                // First load the information from the design metadata.
                ArgumentMetadata designMetadata = inputPart.getDesignMetadata();
                argName = inputPart.getArgumentName().getCompositeName();
                argDesc = designMetadata.getShortDescription();
                
                if (argDesc == null && gem instanceof FunctionalAgentGem) {
                    
                    // If there is no design description of the input, 
                    // show the description of the gem argument instead.
                    
                    FunctionalAgentGem faGem = (FunctionalAgentGem) gem;
                    FunctionalAgentMetadata gemMetadata = faGem.getGemEntity().getMetadata(GemCutter.getLocaleFromPreferences());
                    ArgumentMetadata[] gemArguments = gemMetadata.getArguments();
                    
                    int argNum = inputPart.getInputNum();
                    argDesc = argNum < gemArguments.length ? gemArguments[argNum].getShortDescription() : null;
                    
                    // If there is no description of the gem argument, display the CALDoc instead.
                    if (argDesc == null) {
                        CALDocComment caldoc = faGem.getGemEntity().getFunctionalAgent().getCALDocComment();
                        
                        if (caldoc != null) {
                            if (argNum < caldoc.getNArgBlocks()) {
                                argCALDocDesc = NavHtmlFactory.getHtmlForCALDocTextBlockWithNoHyperlinks(caldoc.getNthArgBlock(argNum).getTextBlock(), true);
                            }
                        }
                    }
                }
                
                CollectorGem argTargetGem = GemGraph.getInputArgumentTarget(inputPart);
                if (argTargetGem != null) {
                    argTarget = argTargetGem.getUnqualifiedName();
                }
            }
        } else if (part instanceof Gem.PartOutput) {
            
            Gem gem = part.getGem();
            Gem.PartOutput outputPart = (Gem.PartOutput)part;
            
            if (!outputPart.isConnected() && gem instanceof FunctionalAgentGem) {
                FunctionalAgentGem faGem = (FunctionalAgentGem) gem;
                FunctionalAgentMetadata gemMetadata = faGem.getGemEntity().getMetadata(GemCutter.getLocaleFromPreferences());

                if (gemMetadata instanceof FunctionMetadata) {
                    argDesc = ((FunctionMetadata)gemMetadata).getReturnValueDescription();
                } else if (gemMetadata instanceof ClassMethodMetadata) {
                    argDesc = ((ClassMethodMetadata)gemMetadata).getReturnValueDescription();
                } else {
                    argDesc = null;
                }
                
                // If there is no description of the return value, display the CALDoc instead.
                if (argDesc == null) {
                    CALDocComment caldoc = faGem.getGemEntity().getFunctionalAgent().getCALDocComment();
                    
                    if (caldoc != null) {
                        CALDocComment.TextBlock returnBlock = caldoc.getReturnBlock();
                        if (returnBlock != null) {
                            argCALDocDesc = NavHtmlFactory.getHtmlForCALDocTextBlockWithNoHyperlinks(returnBlock, true);
                        }
                    }
                }
            }
        }

        // Determine the type of the part connectable
        Gem rootGem = part.getGem().getRootGem();

        if (part.isConnected()) {
            argName = null;
            argType = "&lt;<i>" + GemCutter.getResourceString("ToolTipHelper_Connected") + "</i>&gt;";
        } else if ((part instanceof Gem.PartInput) && ((Gem.PartInput) part).isBurnt()) {
            argType = "&lt;<i>" + GemCutter.getResourceString("ToolTipHelper_Burnt") + "</i>&gt;";
        } else if ((rootGem != null && GemGraph.isAncestorOfBrokenGemForest(rootGem)) || part.getType() == null) {
            argType = "&lt;" + GemCutter.getResourceString("ToolTipHelper_UndefinedType") + "&gt;";
        } else {
            argType = "<i>" + wrapTextToHTMLLines(typeStringProvider.getTypeString(part.getType(), namingPolicy), parent) + "</i>";
        }
    

        // Build the tooltip using the name, type and description
        StringBuilder toolTip = new StringBuilder("<html><body>");
        
        if (part instanceof Gem.PartInput) {
            
            if (argName != null) {
                toolTip.append("<b>" + argName + "</b> :: ");
            }
            
            toolTip.append(argType);

            if (argTarget != null) {
                toolTip.append("<br>" + GemCutterMessages.getString("ArgTargetToolTip", argTarget));
            }
            
            if (argDesc != null) {
                toolTip.append("<br>" + argDesc);
            }
            
            if (argCALDocDesc != null) {
                toolTip.append(argCALDocDesc);
            }
            
        } else if (part instanceof Gem.PartOutput) {
            
            toolTip.append(argType);
            
            if (argDesc != null) {
                toolTip.append("<br>" + argDesc);
            }
            
            if (argCALDocDesc != null) {
                toolTip.append(argCALDocDesc);
            }            
            
        } else {
            toolTip.append(argType);
        }

        toolTip.append("</body></html>");
        
        return toolTip.toString();
    }
    
    /**
     * Takes a string of text and splits it into lines of a maximum length. Lines longer than the maximum length
     * will be wrapped at word boundaries. Words longer than the maximum line length will be truncated.
     * An array of strings is returned where each element string is one line. The lines in the array will not
     * have linebreaks at the end.
     * 
     * NOTE: An assumption is made that the original text contains only text for display
     *       and that there are no html tags taking up space.
     * 
     * @param originalText - the text that needs to be broken into lines that will fit into the tool tip.
     * @param maxLineLength - the max width of the lines to create when breaking up the text.
     * @param font - the font of the component that will display the tooltip.
     * @param context - the font render context of the component that will display the tooltip.
     * @return String[] - array of lines without line breaks.
     */
    public static String[] splitTextIntoLines(String originalText, int maxLineLength, Font font, FontRenderContext context) {
          
        List<String> result = new ArrayList<String> ();
        
        /* We use split and not a StringTokenizer since we don't want the linebreaks
         * but we do want empty lines between two adjacent line breaks.
         */
        String lines[] = originalText.split("\n", -1);
        
        for (final String line : lines) {
            
            int lineLength = 0;
            StringBuilder lineBuffer = new StringBuilder();
            
            /* Here we use a StringTokenizer since we want each word and the spaces
             * between the individual words.
             */
            StringTokenizer words = new StringTokenizer (line, " ", true);

            while (words.hasMoreTokens ()) {

                String word = words.nextToken ();
                int wordLength = (int) font.getStringBounds(word, context).getWidth();
                
                if (wordLength > maxLineLength) {
                    // This word by itself is too long to fit on one line so we truncate the word.
                
                    AttributedString attrString = new AttributedString (word);
                    LineBreakMeasurer measurer = new LineBreakMeasurer(attrString.getIterator(), context);
                    int index = measurer.nextOffset(maxLineLength);
                
                    // Add the previous line if there is any
                    if (lineBuffer.length() > 0) {
                        result.add (lineBuffer.toString ());
                        lineBuffer = new StringBuilder ();
                        lineLength = 0;
                    }
                    
                    // Truncate the word at index and add it as its own line
                    result.add (word.substring(0, index) + "...");
            
                } else if (wordLength + lineLength > maxLineLength) {
                    // The current line would be too long with this word appended.
                    // So start a new line with this word.
                
                    result.add (lineBuffer.toString ());
                    lineBuffer = new StringBuilder (word);
                    lineLength = wordLength;
                
                } else {
                    // This word fits on the current line
                
                    lineBuffer.append(word);
                    lineLength += wordLength;
                }
            }
            
            // Add the last line
            if (lineBuffer.length() > 0 || line.equals("")) {
                result.add (lineBuffer.toString());
            }
        }
            
        // Combine the list back into an array
        String[] resultArray = new String[result.size()];
        result.toArray(resultArray);
        
        return resultArray;
    }

    /**
     * Converts an arbitrary string into a properly formatted multiline tooltip. If lines are too wide they will
     * be wrapped. If the total text is too long and exceeds the specified height it will be cut off and "more..."
     * will be displayed on the last line.
     * 
     * @param text the original text which should be displayed in the tooltip
     * @param maxWidth the maximum width of the tooltip in pixels
     * @param maxHeight the maximum height of the tooltip in pixels
     * @param font the font that will be used for the tooltip
     * @param context the context in which the tooltip will be rendered
     * @return the HTML formatted tooltip text
     */   
    public static String stringToHtmlToolTip (String text, int maxWidth, int maxHeight, Font font, FontRenderContext context) {

        // Get a properly formatted tooltip that is not too large
        String[] toolTipLines = ToolTipHelpers.splitTextIntoLines(text, maxWidth, font, context);

        // Combine the lines back into an HTML tooltip
        int totalHeight = 0;
        String toolTipText = "<html>";
        for (int i = 0; i < toolTipLines.length; i++) {
            
            totalHeight += font.getStringBounds(toolTipLines[i], context).getHeight();
            if (totalHeight > maxHeight) {
                break;
            }
            
            if (i > 0) {
                toolTipText += "<br>";
            }

            toolTipText += toolTipLines[i];
        }

        // We want 20 lines max, so if there is more then add a ...
        if (totalHeight > maxHeight) {
            toolTipText += "<br><i>" + GemCutter.getResourceString("ToolTipHelper_More") + "</i>";
        }
            
        toolTipText += "</html>";
        
        return toolTipText;    
    }
    
    /**
     * Takes a string of text and splits it into lines of a maximum length of TOOLTIP_MAX_WIDTH pixels. 
     * The lines will be separated by HTML &lt;br&gt; tags. Lines longer than the maximum length will 
     * be wrapped at word boundaries. Words longer than the maximum line length will be truncated.
     * 
     * NOTE: An assumption is made that the original text contains only text for display
     *       and that there are no html tags taking up space.
     * 
     * @param originalText - the text that needs to be broken into lines that will fit into the tool tip.
     * @param component - the component whose font is used to calculate the line split locations 
     */
    public static String wrapTextToHTMLLines(String originalText, JComponent component) {
        if (originalText == null) {
            return "";
        }
        
        String text = "";
        Graphics2D parentGraphics = (Graphics2D) component.getGraphics();
        if (parentGraphics == null) {
            // Cannot determine tooltip size because the parent component is not displayed
            return originalText;
        }
        
        String[] lines = splitTextIntoLines(originalText, TOOLTIP_MAX_WIDTH, component.getFont(), parentGraphics.getFontRenderContext());
        for (int i = 0; i < lines.length; i++) {
            if (i > 0) {
                text += "<br>";
            }
            text += lines[i];
        }
        return text;
    }
    
    /**
     * Computes the width of the text if displayed as is in the component.
     * @param text the text to check.
     * @param component the component to check with.
     * @return the width of the text if displayed as is in the component.
     */
    public static int getDisplayedTextWidth(String text, JComponent component) {
        if (text == null) {
            return 0;
        }
        
        Graphics2D parentGraphics = (Graphics2D) component.getGraphics();
        if (parentGraphics == null) {
            // Cannot determine tooltip size because the parent component is not displayed
            return 0;
        }
        
        return (int) component.getFont().getStringBounds(text, parentGraphics.getFontRenderContext()).getWidth();
    }
}
