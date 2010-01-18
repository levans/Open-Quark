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
 * IntellicutListRenderer.java
 * Creation date: Jun 16, 2003
 * By: Frank Worsley
 */
package org.openquark.gems.client;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.SwingUtilities;

import org.openquark.cal.services.GemEntity;
import org.openquark.gems.client.AutoburnLogic.AutoburnUnifyStatus;
import org.openquark.gems.client.IntellicutListModelAdapter.IntellicutListEntry;
import org.openquark.gems.client.IntellicutManager.IntellicutInfo;
import org.openquark.gems.client.IntellicutManager.IntellicutMode;
import org.openquark.util.ui.UIUtilities;


/**
 * The cell renderer for the intellicut list.
 *
 *  @author Frank Worsley
 */
public class IntellicutListRenderer extends DefaultListCellRenderer {

    private static final long serialVersionUID = -9074143759683342266L;

    /** The background color of list items that are not selected. */
    static final Color NORMAL_BACKGROUND_COLOR = Color.WHITE;
    
    /** The background color of list items that are selected. */
    static final Color SELECTED_BACKGROUND_COLOR = Color.BLUE.darker().darker();
    
    /** The background color if the list item is transparent and not selected. */
    static final Color TRANSPARENT_BACKGROUND_COLOR = new Color(0, 0, 0, 0);

    /** The background color if the list item is transparent and selected. */
    static final Color SELECTED_TRANSPARENT_BACKGROUND_COLOR = new Color(100, 150, 255, 100);
    
    /** The text highlight colour for non selected items. */
    static final Color HIGHLIGHT_COLOR = Color.MAGENTA.darker();
    
    /** The text highlight colour for selected items. */
    static final Color SELECTED_HIGHTLIGHT_COLOR = Color.ORANGE;
    
    /** The text color for normal items. */
    static final Color TEXT_COLOR = Color.BLACK;
    
    /** The text color for selected items. */
    static final Color SELECTED_TEXT_COLOR = Color.WHITE;
    
    /** The text color for transparent items. */
    static final Color TRANSPARENT_TEXT_COLOR = Color.BLACK;
    
    /** The text color for selected transparent items. */
    static final Color SELECTED_TRANSPARENT_TEXT_COLOR = Color.WHITE;
    
    /** The outline color for transparent non selected items. */
    static final Color OUTLINE_COLOR = new Color(250, 250, 250, 100);

    /** The outline color for transparent selected items. */
    static final Color SELECTED_OUTLINE_COLOR = new Color(80, 130, 255, 100);
    
    /** The size of the glowing outline drawn around text if the list is transparent. */
    private static final int OUTLINE_SIZE = 2;
        
    /** The list entry this renderer is rendering. */
    private IntellicutListEntry listEntry;
       
    /**
     * The list this renderer is used for. This may not be an IntellicutList since
     * this renderer is also used for the normal JList of the IntellicutComboBox.
     */
    private JList intellicutList;

    /** Whether or not this item is selected. */
    private boolean selected;
        
    /** Whether or not this item is transparent. */
    private boolean transparent;

    /** Scope decal */
    private static final ImageIcon SCOPE_PRIVATE_ICON = new ImageIcon(TableTop.class.getResource("/Resources/intellicut_private.gif"));
    private static final ImageIcon SCOPE_PROTECTED_ICON = new ImageIcon(TableTop.class.getResource("/Resources/intellicut_protected.gif"));
    private static final ImageIcon SCOPE_PUBLIC_ICON = new ImageIcon(TableTop.class.getResource("/Resources/intellicut_public.gif"));
    
    // Icons for when only displaying scope icons
    private ImageIcon emitterIcon = null;
    private ImageIcon reflectorIcon = null;
    
    private ImageIcon scopePrivateIcon = null;
    private ImageIcon scopeProtectedIcon = null;
    private ImageIcon scopePublicIcon = null;
   
    // Icons for when displaying both burning status and scope 
    private ImageIcon burnImageIcon = null;
    private ImageIcon burnQuestionIcon = null;
    private ImageIcon blankImageIcon = null;
    
    private ImageIcon emitterIcon_shifted = null;
    private ImageIcon reflectorIcon_shifted = null;
   
    private ImageIcon scopePrivateIcon_shifted = null;
    private ImageIcon scopeProtectedIcon_shifted = null;
    private ImageIcon scopePublicIcon_shifted = null;
    
    /** Cache for the scope decorated icons */
    private final Map<Icon, Icon> imageCache_public = new HashMap<Icon, Icon>();
    private final Map<Icon, Icon> imageCache_protected = new HashMap<Icon, Icon>();
    private final Map<Icon, Icon> imageCache_private = new HashMap<Icon, Icon>();

    /** Where the intellicut is activated */
    private final IntellicutMode intellicutMode; 
  

    /**
     * @param list the IntellicutList the renderer will be used for
     * @param value the list entry item that should be rendered
     * @param index the index of the item in the model
     * @param isSelected true if the item is selected
     * @param cellHasFocus true if the item has focus
     * @return Component the component to use for rendering the list cell with the given values.
     */
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
         
        this.listEntry = (IntellicutListEntry) value;
        this.intellicutList = list;
        this.selected = isSelected;
        
        if (list instanceof IntellicutList) {
            transparent = ((IntellicutList) list).isTransparent();
        } else {
            // If the list is actually the JList in the IntellicutComboBox
            transparent = false;
        }

        setText(listEntry.getDisplayString());
        
        // Choose the correct background color
        if (transparent) {
            setBackground(selected ? SELECTED_TRANSPARENT_BACKGROUND_COLOR : TRANSPARENT_BACKGROUND_COLOR);
        } else {
            setBackground(selected ? SELECTED_BACKGROUND_COLOR : NORMAL_BACKGROUND_COLOR);
        }
        
        // Looks better with a narrower gap (default is 4)
        setIconTextGap(2);
        setIcon(getIconForEntry(listEntry));
        
        return this;
    }
    
    /** Constructor */
    public IntellicutListRenderer(IntellicutMode mode) {
        createIcons();
        intellicutMode = mode;
    }
    
    /** 
     * Creates images for the icons used by this list
     */
    private void createIcons() {
        // Icons for when only displaying scope icons
        emitterIcon = createCenteredIcon(TableTopPanel.emitterImageIconSmall);
        reflectorIcon = createCenteredIcon(TableTopPanel.reflectorImageIconSmall);
        
        scopePrivateIcon = createCenteredIcon (SCOPE_PRIVATE_ICON);
        scopeProtectedIcon = createCenteredIcon (SCOPE_PROTECTED_ICON);
        scopePublicIcon = createCenteredIcon (SCOPE_PUBLIC_ICON);
        
        // Icons for when displaying both burning status and scope 
        blankImageIcon = createCroppedAndCenteredIcon(TableTopPanel.blankImageIconSmall);        
        burnImageIcon = createCroppedAndCenteredIcon(TableTopPanel.burnImageIconSmall);
        burnQuestionIcon = createCroppedAndCenteredIcon(TableTopPanel.burnQuestionImageIconSmall);
        
        emitterIcon_shifted = createShiftedIcon(TableTopPanel.emitterImageIconSmall);
        reflectorIcon_shifted = createShiftedIcon(TableTopPanel.reflectorImageIconSmall);
        
        scopePrivateIcon_shifted = createShiftedIcon (SCOPE_PRIVATE_ICON);
        scopeProtectedIcon_shifted = createShiftedIcon (SCOPE_PROTECTED_ICON);
        scopePublicIcon_shifted = createShiftedIcon (SCOPE_PUBLIC_ICON);

    }
    
    /**
     * Takes a small icon image, and vertically centers it so that the new icon fits within the dimensions of a row label. 
     * @param icon
     * @return new icon
     */
    private ImageIcon createCenteredIcon(ImageIcon icon) {
        int height = this.getFontMetrics(this.getFont()).getHeight();
        // The image will be shifted down so that it is vertically centered, then padded at the bottom to match the required height
        Image newImage = UIUtilities.cropImage(UIUtilities.shiftImage(icon.getImage(), 0, ((height - icon.getIconHeight())/2 - 1)), 0, 0, 0, -1 * ((height - icon.getIconHeight())/2 + 1));
        return new ImageIcon(newImage);
    }
    
    /**
     * Takes a small icon image, vertically centers it so that the new icon fits within the dimensions of a row label. 
     * Then crop 2 pixels on the left side of the image for better appearance
     * @param icon
     * @return new icon
     */
    private ImageIcon createCroppedAndCenteredIcon(ImageIcon icon) {
        int height = this.getFontMetrics(this.getFont()).getHeight();
        // The image will be shifted down so that it is vertically centered, then padded at the bottom to match the required height
        Image newImage = UIUtilities.cropImage(UIUtilities.shiftImage(icon.getImage(), 0, ((height - icon.getIconHeight())/2 - 1)), 2, 0, 0, -1 * ((height - icon.getIconHeight())/2 + 1));
        return new ImageIcon(newImage);
    }
    
    
    /** 
     * Takes a small icon image and make room on the left for combining with the burning status icons.
     * @param icon
     * @return new icon the icon that has enough padding on the left to fit another icon
     */
    private ImageIcon createShiftedIcon(ImageIcon icon) {
        Image newImage = UIUtilities.cropImage(icon.getImage(), -10, 0, 0, 0 );
        return new ImageIcon(newImage);
    }

    /**
     * Determines the burning icon that should be used for a list entry. 
     * @param listEntry the intellicut entry to get the icon for
     * @return the burning icon to use or a blank image for no burning icon
     */
    private Icon getBurnImageForEntry(IntellicutListEntry listEntry) {

        IntellicutInfo intellicutInfo = listEntry.getIntellicutInfo();
        AutoburnUnifyStatus burnStatus = intellicutInfo.getAutoburnUnifyStatus();

        if (burnStatus == AutoburnUnifyStatus.UNAMBIGUOUS) {
            return burnImageIcon;

        } else if (burnStatus == AutoburnUnifyStatus.NOT_NECESSARY) {
            return blankImageIcon;

        } else if (burnStatus == AutoburnUnifyStatus.AMBIGUOUS) {
            return burnQuestionIcon;

        } else if (burnStatus == AutoburnUnifyStatus.UNAMBIGUOUS_NOT_NECESSARY) {
            if (intellicutInfo.getBurnTypeCloseness() > intellicutInfo.getNoBurnTypeCloseness()) {
                return burnImageIcon;
            }

        } else if (burnStatus == AutoburnUnifyStatus.AMBIGUOUS_NOT_NECESSARY) {
            if (intellicutInfo.getBurnTypeCloseness() > intellicutInfo.getNoBurnTypeCloseness()) {
                return burnQuestionIcon;
            }
        }
        return blankImageIcon;
    }        

    /**
     * Determines the icon for the entry. The scoped entry icon could be a scope icon only or scope icon plus burn status icon, 
     * depending on which part the intellicut was activated on. 
     * @param listEntry the intellicut entry to get the icon for
     * @return icon with scope (and burn status) or the appropriate icon for non-scoped entry
     */
    public Icon getIconForEntry(IntellicutListEntry listEntry) {
        
        // Overlays the burn status image and scope icon only if intellicut is activated on an input part
        if (intellicutMode == IntellicutMode.PART_INPUT) {
            return getIconWithBurnStatus(listEntry);

        } else {// (intellicutMode == IntellicutMode.PART_OUTPUT || intellicutMode == IntellicutMode.NOTHING
            return getIconWithoutBurnStatus(listEntry);

        }
    }
    
    /**
     * Determines the icon for the entry where the left column is the burn status icon and the right column is the scope icon.
     * Non-scoped entry returns the appropriate gem type icon. 
     * @param listEntry
     * @return Icon an combined icon with burn status and scope 
     */
    private Icon getIconWithBurnStatus(IntellicutListEntry listEntry) {

        Object listData = listEntry.getData();

        // Retrieve the top layer of the icon
        Icon overlayImage = getBurnImageForEntry(listEntry);

        // Add scope decal (public, protected, private) underneath if entry is scoped
        if (listData instanceof GemEntity) {
            // Uses cache to store icons already combined
            GemEntity entity = (GemEntity) listData;
            Icon baseGemIcon = null;
            Icon overlayIcon = overlayImage;
            Map<Icon, Icon> cache = null;

            if (entity.getScope().isPublic()) {
                cache = imageCache_public;
                baseGemIcon = scopePublicIcon_shifted;

            } else if (entity.getScope().isProtected()) {
                cache = imageCache_protected;
                baseGemIcon = scopeProtectedIcon_shifted;

            } else { // entity.getScope().isPrivate()
                cache = imageCache_private;
                baseGemIcon = scopePrivateIcon_shifted;
            }

            Icon cachedImage = cache.get(overlayIcon);
            if (cachedImage != null) {
                return cachedImage;

            } else {
                Icon newIcon = UIUtilities.combineIcons(baseGemIcon, overlayIcon);
                cache.put(overlayIcon, newIcon);
                return newIcon;
            }

        } else if (listData instanceof CollectorGem) {
            return ((CollectorGem) listData).getReflectedInputs().isEmpty() ? emitterIcon_shifted : reflectorIcon_shifted;
        }

        return blankImageIcon;
    }
    
    /**
     * Determines the icon for the list entry without any burn status icons. Non-scoped entry returns the appropriate gem type icon.
     * @param listEntry
     * @return Icon Either a scope icon or the appropriate gem type icon
     */
    private Icon getIconWithoutBurnStatus(IntellicutListEntry listEntry) {

        Object listData = listEntry.getData();

        if (listData instanceof GemEntity) {
            GemEntity entity = (GemEntity) listData;
            if (entity.getScope().isPublic()) {
                return scopePublicIcon;
            } else if (entity.getScope().isProtected()) {
                return scopeProtectedIcon;
            } else {
                return scopePrivateIcon;
            }
        } else if (listData instanceof CollectorGem) {
            return ((CollectorGem) listData).getReflectedInputs().isEmpty() ? emitterIcon : reflectorIcon;
        }

        return blankImageIcon;
    }
    
    
    /**
     * This is needed so that drawing the label works correctly. We have to always
     * fill in our background so that the list updates correctly if we scroll.
     * @param g the graphics object to draw with
     */
    @Override
    public void paintComponent(Graphics g) {
        
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
        
        super.paintComponent(g);
    }
    
    /**
     * Overloaded paint function to draw our custom highlighted text.
     * @param g the graphics object to draw with
     */
    @Override
    public void paint(Graphics g) {
        
        Insets insets = getInsets();
        Icon icon = getIcon();
        int iconWidth = icon != null ? icon.getIconWidth() : 0;
        
        String text1 = listEntry.getDisplayString();
        String text2 = " (" + listEntry.getSecondaryDisplayString() + ")";
        if (text2.length() == 3) {
            text2 = "";
        }
        Graphics2D g2 = (Graphics2D) g;
        
        /////////////////////
        // TODO: Module name should be de-emphasized from regular name, 
        //       but still attract attention. The following variables are
        //       here to assist in selection of this font.

        Font textFont = getFont().deriveFont(Font.PLAIN ).deriveFont(getFont().getSize2D()-0.0f);
        Font text2Font = (getFont().deriveFont( Font.PLAIN ).deriveFont(getFont().getSize2D()-0.0f));
        //LinkedHashMap attributes = new LinkedHashMap();
        //attributes.put(TextAttribute.WIDTH, TextAttribute.WIDTH_CONDENSED);
        //attributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_ULTRABOLD );
        //text2Font = text2Font.deriveFont(attributes);
        //text2Font = text2Font.deriveFont(new AffineTransform(0.9,0.0,0.0,1.0,0.0,0.0));
        Object textRenderHint = (RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        Object text2RenderHint = (RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        Color text2Color = selected ? SELECTED_TEXT_COLOR : new Color(10, 10, 10, 190); 
        Color outline2Color = selected ? SELECTED_OUTLINE_COLOR : OUTLINE_COLOR;//new Color(240, 240, 240, 100); // 100
            
        ////////////////////

        // draw in the background
        g2.setColor(getBackground());
        g2.fillRect(0, 0, getWidth(), getHeight());

        // draw in the icon
        if (icon != null) {
            icon.paintIcon(this, g2, insets.left, insets.top);
        }

        // figure out the text color
        Color textColor = selected ? SELECTED_TEXT_COLOR : TEXT_COLOR;
        if (transparent) {
            textColor = selected ? SELECTED_TRANSPARENT_TEXT_COLOR : TRANSPARENT_TEXT_COLOR;
        }

        // get ready to draw the text            
        g2.setFont(textFont);
        g2.setColor(textColor);
        
        final int xOffset = insets.left + iconWidth + getIconTextGap();
        final int yOffset = getHeight() - getFontMetrics(textFont).getDescent() - insets.bottom + OUTLINE_SIZE;
        final int xOffset2 = xOffset + SwingUtilities.computeStringWidth(getFontMetrics(textFont), text1+" ");
        
        // draw an outline for non-selected text if the item is transparent.
        if (transparent) {

            Color outlineColor = selected ? SELECTED_OUTLINE_COLOR : OUTLINE_COLOR;
            g2.setColor(outlineColor);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            
            for (int x = 0, xmax = 2 * OUTLINE_SIZE; x <= xmax; x++) {
                for (int y = 0, ymax = 2 * OUTLINE_SIZE; y <= ymax; y++) {
                    g2.drawString(text1, xOffset + x, yOffset - y);                            
                }
            }
            
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setColor(outline2Color);
            g2.setFont(text2Font);
            for (int x = 0, xmax = 0+2*OUTLINE_SIZE; x <= xmax; x++) {
                for (int y = 0, ymax = 0+2*OUTLINE_SIZE; y <= ymax; y++) {
                    g2.drawString(text2, xOffset2 + x, yOffset - y);
                }
            }
            
            g2.setFont(textFont);
            g2.setColor(textColor);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, textRenderHint);
        }

        // draw the main string
        g2.drawString(text1, xOffset + OUTLINE_SIZE, yOffset - OUTLINE_SIZE);
        
        g2.setColor(text2Color);
        g2.setFont(text2Font);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, text2RenderHint);
        g2.drawString(text2, xOffset2 + OUTLINE_SIZE, yOffset - OUTLINE_SIZE);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        g2.setFont(textFont);
        g2.setColor(textColor);
        
        // draw the letters the user has entered in a different color
        String userInput = ((IntellicutListModel) intellicutList.getModel()).getUserInput();
        String textToMatch = listEntry.getDisplayString();
        
        if (userInput.length() > 0 && userInput.length() <= textToMatch.length() &&
            textToMatch.substring(0, userInput.length()).equalsIgnoreCase(userInput)) { 
            
            Color typedCharactersColor = selected ? SELECTED_HIGHTLIGHT_COLOR : HIGHLIGHT_COLOR;
            g2.setColor(typedCharactersColor);
            
            int index = text1.length() - textToMatch.length();
            int prefixWidth = 0;
            
            if (index > 0) {
                String prefix = getText().substring(0, index);
                prefixWidth = SwingUtilities.computeStringWidth(getFontMetrics(getFont()), prefix);
            }
            
            String highlight = text1.substring(index, index + userInput.length());
            g2.drawString(highlight, xOffset + OUTLINE_SIZE + prefixWidth, yOffset - OUTLINE_SIZE);
            
        }            
    }
}
