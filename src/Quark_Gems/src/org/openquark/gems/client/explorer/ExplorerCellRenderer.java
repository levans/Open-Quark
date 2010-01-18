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
 * ExplorerCellRenderer.java
 * Created: Feb 13, 2004
 * By: David Mosimann
 */
package org.openquark.gems.client.explorer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.font.TextAttribute;
import java.text.AttributedString;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.TreeUI;
import javax.swing.plaf.basic.BasicLabelUI;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.openquark.cal.compiler.ClassMethod;
import org.openquark.cal.compiler.DataConstructor;
import org.openquark.cal.compiler.Function;
import org.openquark.cal.compiler.FunctionalAgent;
import org.openquark.cal.compiler.ModuleTypeInfo;
import org.openquark.cal.compiler.ScopedEntityNamingPolicy;
import org.openquark.cal.compiler.ScopedEntityNamingPolicy.UnqualifiedUnlessAmbiguous;
import org.openquark.cal.metadata.ArgumentMetadata;
import org.openquark.cal.valuenode.ValueNode;
import org.openquark.gems.client.CodeGem;
import org.openquark.gems.client.CollectorGem;
import org.openquark.gems.client.FunctionalAgentGem;
import org.openquark.gems.client.Gem;
import org.openquark.gems.client.RecordCreationGem;
import org.openquark.gems.client.RecordFieldSelectionGem;
import org.openquark.gems.client.ReflectorGem;
import org.openquark.gems.client.ValueGem;


/**
 * The renderer for the TableTopExplorer tree.
 * @author Ken Wong
 */
public class ExplorerCellRenderer extends DefaultTreeCellRenderer {

    private static final long serialVersionUID = 1131870254758332921L;

    /** The table top exlorer owner of the explorer tree. */
    private final TableTopExplorerOwner tableTopExplorerOwner;

    /** Whether or not the tree is using the banded look and feel. */
    private boolean useBandedLookAndFeel;

    /** Used to store if the current node is focusable or not. */
    public boolean focusable;

    /* the various icons used in the tree */
    private static final ImageIcon constructorIcon;
    private static final ImageIcon supercombinatorIcon;
    private static final ImageIcon codeGemIcon;
    private static final ImageIcon brokenCodeGemIcon;
    private static final ImageIcon collectorIcon;
//  private static final ImageIcon targetCollectorIcon;
    private static final ImageIcon emitterIcon;
    private static final ImageIcon minorTargetIcon;
    private static final ImageIcon reflectorIcon;
    private static final ImageIcon brokenReflectorGemIcon;
    private static final ImageIcon partInputIcon;
    private static final ImageIcon burnIcon;
    private static final ImageIcon valueIcon;
    private static final ImageIcon recordFieldSelectionIcon;
    private static final ImageIcon recordCreationIcon;
    

    static {
        // Make icon objects
        constructorIcon = new ImageIcon(Object.class.getResource("/Resources/dataConstructor.gif"));
        supercombinatorIcon = new ImageIcon(Object.class.getResource("/Resources/supercombinator.gif"));
        codeGemIcon = new ImageIcon(Object.class.getResource("/Resources/code.gif"));
        brokenCodeGemIcon = new ImageIcon(Object.class.getResource("/Resources/codeBroken.gif"));
        collectorIcon = new ImageIcon(Object.class.getResource("/Resources/collector.gif"));
//      targetCollectorIcon = new ImageIcon(Object.class.getResource("/Resources/targetCollector.gif"));
        emitterIcon = new ImageIcon(Object.class.getResource("/Resources/emitter.gif"));
        minorTargetIcon = new ImageIcon(Object.class.getResource("/Resources/targetbw.gif"));
        reflectorIcon = new ImageIcon(Object.class.getResource("/Resources/reflector.gif"));
        brokenReflectorGemIcon = new ImageIcon(Object.class.getResource("/Resources/reflectorBroken.gif"));
        partInputIcon = new ImageIcon(Object.class.getResource("/Resources/partinput.gif"));
        valueIcon = new ImageIcon(Object.class.getResource("/Resources/constant.gif"));
        Image burnedImage = Toolkit.getDefaultToolkit().createImage(Object.class.getResource("/Resources/burn.gif")).getScaledInstance(16,16,Image.SCALE_SMOOTH);
        burnIcon = new ImageIcon(burnedImage);
        recordFieldSelectionIcon = new ImageIcon(Object.class.getResource("/Resources/recordFieldSelectionGem.gif"));
        recordCreationIcon = new ImageIcon(Object.class.getResource("/Resources/recordCreationGem.gif"));
    }

    /**
     * Default Constructor for the cell renderer;
     * @param owner the explorer owner using the tree
     */
    protected ExplorerCellRenderer(TableTopExplorerOwner owner) {
        this.tableTopExplorerOwner = owner;

        // Set the UI to use the hyperlink label UI
        setUI(HyperlinkLabelUI.createUI(this));
    }

    public void setBandedLookAndFeel(boolean useBandedLookAndFeel) {
        this.useBandedLookAndFeel = useBandedLookAndFeel;
    }

    /**
     * Overide this method so that we can explicitly use a special banded look and feel even if a different
     * look and feel is requested.
     * @param newUI
     */
    public void setUI(TreeUI newUI) {
        super.setUI(HyperlinkLabelUI.createUI(this));
    }

    /**
     * @see javax.swing.tree.TreeCellRenderer#getTreeCellRendererComponent(javax.swing.JTree, java.lang.Object, boolean, boolean, boolean, int, boolean)
     */
    @Override
    public Component getTreeCellRendererComponent(JTree tree,
            Object value,
            boolean sel,
            boolean expanded,
            boolean leaf, int row,
            boolean hasFocus) {
        // By default the node is not focussable
        focusable = false;

        if (tableTopExplorerOwner.hasPhotoLook()) {
            setTextNonSelectionColor(Color.BLACK);
            setTextSelectionColor(Color.WHITE);
            setBackgroundSelectionColor(Color.BLUE.darker().darker());
        } else {        
            setTextNonSelectionColor(SystemColor.textText);
            setTextSelectionColor(SystemColor.textHighlightText);
            setBackgroundSelectionColor(SystemColor.textHighlight);
        }

        setBackgroundNonSelectionColor(new Color(1, 1, 1, 1));
        setFont(null);

        ImageIcon customIcon = null;
        String customText = value.toString();

        // Now we 'tweak' the component a bit to get the look we want.
        if (value instanceof ExplorerGemNode) {

            ExplorerGemNode explorerNode = (ExplorerGemNode) value;
            Gem gem = explorerNode.getGem();

            // Get the customized name and icon
            customIcon = getGemDisplayIcon(gem);
            customText = getGemDisplayString(gem);            

        } else if (value instanceof DefaultMutableTreeNode) {

            // Other nodes in the trees are dealt with here
            DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) value;

            if (treeNode.isRoot()) {
                customIcon = ExplorerRootNode.rootNodeIcon;

            } else if (treeNode.getUserObject() instanceof Gem.PartInput) {

                Gem.PartInput input = (Gem.PartInput) treeNode.getUserObject();
                ValueNode valueNode = tableTopExplorerOwner.getValueNode(input);
                ArgumentMetadata metadata = input.getDesignMetadata();

                if (tableTopExplorerOwner.highlightInput(input)) {
                    // TODO: instead of hardcoding the highlighted font style, perhaps the renderer
                    //       should be passed to the highlight fn and let it set whatever is appropriate...
                    setTextNonSelectionColor(Color.RED);
                    setFont(tree.getFont().deriveFont(Font.ITALIC));
                }

                if (tableTopExplorerOwner.canEditInputsAsValues() && valueNode != null && !input.isBurnt()) {
                    customText = input.getArgumentName().getCompositeName() + ":  " + valueNode.getTextValue();

                } else {
                    customIcon = partInputIcon;

                    if (input.isBurnt()) {
                        setTextNonSelectionColor(Color.RED.darker());
                        setTextSelectionColor(Color.WHITE);
                        setBackgroundSelectionColor(Color.RED.darker());
                        customIcon = burnIcon;                      
                    }

                    if (metadata.getShortDescription() != null) {
                        customText = input.getArgumentName().getCompositeName() + " - " + metadata.getShortDescription();
                    } else {
                        customText = input.getArgumentName().getCompositeName();
                    }
                }
            }
        }

        // If we are using the banded look and feel then we don't want the super class to draw focus
        // borders around the objects.  They look funny with the banded background colour.
        if (useBandedLookAndFeel) {
            hasFocus = false;
        }

        super.getTreeCellRendererComponent(tree, customText, sel, expanded, leaf, row, hasFocus);

        if (customIcon != null) {
            setIcon(customIcon);
        }

        return this;
    }       

    /**
     * Returns whether the current node should be rendered as a hyperlink.
     * @return Returns true is the current node is focusable and should be drawn as a hyperlink.
     */
    boolean renderAsHyperlink() {
        return focusable;
    }

    /**
     * Determines if the point in question is over the icon portion of the rendered node.
     * @return whether the point in question is over the icon portion of the rendered node.
     */
    boolean isPointOverIcon(Point p) {
        // Assume all icons are the same size and arbitrarily check the first icon
        return (p.x < constructorIcon.getIconWidth());
    }

    /**
     * Determines the suitable display string for the gem and returns it.  The default behaviour is to 
     * use the unqualified gem name unless ambiguous.  Different subclasses may have different behaviour
     * such as different naming policies or using portions of the metadata.
     * @param gem
     * @return A string that will be rendered as part of a gem node in the explorer tree
     */        
    protected String getGemDisplayString(Gem gem) {
        // different icons and text for each type of gem
        if (gem instanceof FunctionalAgentGem) {
            ModuleTypeInfo currentModuleTypeInfo = tableTopExplorerOwner.getCurrentModuleTypeInfo();
            if (currentModuleTypeInfo == null) {
                return null;
            }
            ScopedEntityNamingPolicy namingPolicy = new UnqualifiedUnlessAmbiguous(currentModuleTypeInfo);
            FunctionalAgentGem functionalAgentGem = (FunctionalAgentGem) gem;
            return functionalAgentGem.getGemEntity().getAdaptedName(namingPolicy);

        } else if (gem instanceof CodeGem) {
            return ((CodeGem)gem).getUnqualifiedName();

        } else if (gem instanceof ValueGem) {
            return ((ValueGem)gem).getTextValue();

        } else if (gem instanceof CollectorGem) {
            // Start with the unqualified collector gem name
            return ((CollectorGem)gem).getUnqualifiedName();

        } else if (gem instanceof ReflectorGem) {
            return ((ReflectorGem)gem).getUnqualifiedName();
            
        } else if (gem instanceof RecordFieldSelectionGem) {
            return ((RecordFieldSelectionGem)gem).getDisplayedText();

        } else if (gem instanceof RecordCreationGem) {
            return ((RecordCreationGem)gem).getDisplayName();

        } else {
            return gem.toString();
        }
    }

    /**
     * Determines the suitable icon for the gem and returns it.
     * @param gem
     * @return An icon that will be rendered as part of a gem node in the explorer tree
     */        
    protected ImageIcon getGemDisplayIcon(Gem gem) {
        // different icons for each type of gem
        if (gem instanceof FunctionalAgentGem) {
            FunctionalAgentGem functionalAgentGem = (FunctionalAgentGem) gem;
            FunctionalAgent functionalAgent = functionalAgentGem.getGemEntity().getFunctionalAgent();

            if (functionalAgent instanceof Function || functionalAgent instanceof ClassMethod) {
                return supercombinatorIcon;
            } else if (functionalAgent instanceof DataConstructor) {
                return constructorIcon;
            } else {
                return null;
            }
        } else if (gem instanceof CodeGem) {
            return gem.isBroken() ? brokenCodeGemIcon : codeGemIcon;

        } else if (gem instanceof ValueGem) {
            return valueIcon;

        } else if (gem instanceof CollectorGem) {
            return ((CollectorGem)gem).getTargetCollector() == null ? minorTargetIcon : collectorIcon;

        } else if (gem instanceof ReflectorGem) {
            return gem.isBroken() ? brokenReflectorGemIcon : 
                (gem.getNInputs() == 0) ? emitterIcon : reflectorIcon;
        
        } else if (gem instanceof RecordFieldSelectionGem) {
            return recordFieldSelectionIcon;
            
        } else if (gem instanceof RecordCreationGem) {
            return recordCreationIcon;
            
        }
        else {
            return null;
        }
    }
}

/**
 * A look and feel for painting JLabel text.  If the label to be painted is an instance of a
 * HyperlinkedExplorerCellRenderer and it is set to render as a hyperlink then the painting will try
 * to mimic the default hyperlink style used in browsers (underlined with blue font colour).   
 */
class HyperlinkLabelUI extends BasicLabelUI {
    /** A singleton object holding onto the UI that can be shared by all label components. */
    private final static HyperlinkLabelUI hyperlinkLabelUI = new HyperlinkLabelUI();

    /**
     * Static method to fetch the UI
     * @param c The component that we are fetching UI for.  This is ignored, but assumed to be an
     * instance of a JLabel
     * @return ComponentUI returns the singleton object for this class.
     */
    public static ComponentUI createUI(JComponent c){
        return hyperlinkLabelUI;
    }

    /**
     * If the label is an instance of HyperlinkedExplorerCellRenderer and it is set to render as a
     * hyperlink then the font colour is changed to blue and the text is underlined.  Otherwise the
     * text is painted as ordinary text.
     * @param l The label that this UI is associated with
     * @param g The graphics used for painting
     * @param s The string that should be painted
     * @param textX The X offset to start painting at
     * @param textY the Y offset to start painting at 
     */
    private void paintHyperlinkedText(JLabel l, Graphics g, String s, int textX, int textY) {
        // The label should actually be a HyperlinkedExplorerCellRenderer
        if (l instanceof ExplorerCellRenderer) {
            ExplorerCellRenderer renderer = (ExplorerCellRenderer)l;
            if (renderer.renderAsHyperlink()) {
                // Create an attributed string so that the characters are underlined and blue
                Map<TextAttribute, Object> attrs = new HashMap<TextAttribute, Object>();
                attrs.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_LOW_ONE_PIXEL);
                attrs.put(TextAttribute.FONT, renderer.getFont());

                // If the label is using the selected colour then render the hyperlink in white text
                // since it will look a lot better than blue given the default dark blue selection colour.
                // Otherwise the text should be rendered blue like a normal hyperlink.
                if (renderer.getForeground() == renderer.getTextSelectionColor()) {
                    attrs.put(TextAttribute.FOREGROUND, Color.WHITE);
                } else {
                    attrs.put(TextAttribute.FOREGROUND, Color.BLUE);
                }

                AttributedString attrString = new AttributedString(s, attrs);

                // Draw the 'hyperlink' string
                g.drawString(attrString.getIterator(), textX, textY);
                return;
            }
        }

        // If this is not the right renderer or not a hyperlink then simply draw the plain text
        g.drawString(s, textX, textY);
    }

    /**
     * Paints the text for an enabled node.
     * @param l The label that this UI is associated with
     * @param g The graphics used for painting
     * @param s The string that should be painted
     * @param textX The X offset to start painting at
     * @param textY the Y offset to start painting at 
     */
    @Override
    protected void paintEnabledText(JLabel l, Graphics g, String s, int textX, int textY) {
        paintHyperlinkedText(l, g, s, textX, textY);
    }

    /**
     * Paints the text for a disabled node.  This currently paints the node exactly the same as an
     * enabled node.
     * @param l The label that this UI is associated with
     * @param g The graphics used for painting
     * @param s The string that should be painted
     * @param textX The X offset to start painting at
     * @param textY the Y offset to start painting at 
     */
    @Override
    protected void paintDisabledText(JLabel l, Graphics g, String s, int textX, int textY) {
        paintHyperlinkedText(l, g, s, textX, textY);
    }
}