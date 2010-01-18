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
 * UIUtilities.java
 * Created: Dec. 8 / 2003
 * By: David Mosimann
 */
package org.openquark.util.ui;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.SystemColor;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.plaf.OptionPaneUI;
import javax.swing.plaf.basic.BasicBorders;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicOptionPaneUI;
import javax.swing.plaf.basic.BasicToggleButtonUI;
import javax.swing.text.JTextComponent;
import javax.swing.text.Keymap;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

/**
 * A collection of user interfaces helper methods.
 */
public class UIUtilities {

    /** An action property for specifying whether a regualar or toggle button should be created. */
    public static final String TOGGLE_BUTTON = "TOGGLE_BUTTON"; //$NON-NLS-1$

    /** Rendering hints - render at highest quality*/
    private static final RenderingHints RENDERING_HINTS_HIGH_QUALITY = new RenderingHints(null);
    static {
        RENDERING_HINTS_HIGH_QUALITY.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        RENDERING_HINTS_HIGH_QUALITY.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        RENDERING_HINTS_HIGH_QUALITY.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        RENDERING_HINTS_HIGH_QUALITY.put(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        RENDERING_HINTS_HIGH_QUALITY.put(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        RENDERING_HINTS_HIGH_QUALITY.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        RENDERING_HINTS_HIGH_QUALITY.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        RENDERING_HINTS_HIGH_QUALITY.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }
    
    /** Enumeration indicating image flip orientation */
    public static final class FlipOrientation {
        
        /** Vertical Flip */
        public static final FlipOrientation Vertical = new FlipOrientation("Vertical Flip"); //$NON-NLS-1$
        /** Horizontal Flip */
        public static final FlipOrientation Horizontal = new FlipOrientation("Horizontal Flip"); //$NON-NLS-1$
        /** Vertical and Horizontal Flip */
        public static final FlipOrientation BiDirectional = new FlipOrientation("Vertical and Horizontal Flip"); //$NON-NLS-1$
        
        private final String enumType;
        private FlipOrientation(String type) {
            if (type == null) {
                throw new NullPointerException();
            }
            enumType = type;
        }
        
        @Override
        public String toString() {
            return enumType;
        }
    }
    
    /**
     * This class should never be constructed.  It's only purpose is to collect useful static methods
     * similar to how the SwingUtilities class works.
     */
    private UIUtilities() {
        super();
    }

    /**
     * Creates a new JButton with the flat appearance.  It uses the same rollover effect as flat buttons
     * in a toolbar. 
     * @return a JButton with the roll over property set and the border customized to show the rollover effect
     */
    public static JButton CreateFlatButton() {
        JButton button = new JButton();
        makeButtonFlat(button);
        return button;
    }

    /**
     * Creates a new JToggleButton with the flat appearance.  
     * It uses the same rollover effect as flat buttons in a toolbar.
     * @return a JButton with the roll over property set and the border customized to show the rollover effect
     */
    public static JToggleButton CreateFlatToggleButton() {
        JToggleButton button = new JToggleButton();
        makeButtonFlat(button);
        return button;
    }
    
    /**
     * Creates a new JButton with the flat appearance.  
     * It uses the same rollover effect as flat buttons in a toolbar.
     * The TOGGLE_BUTTON boolean property is checked to determine whether to create a toggle
     * button or a regular one (default). 
     * @param action  the action to be hooked up to the button
     * @return a JButton with the roll over property set and the border customized to show the rollover effect
     */
    public static AbstractButton CreateFlatButton(Action action) {
        // Create the button
        boolean toggleButton = false;
        if (action != null) {
            Object toggleButtonObj = action.getValue(TOGGLE_BUTTON);
            toggleButton = (toggleButtonObj instanceof Boolean) ? ((Boolean) toggleButtonObj).booleanValue() : false;
        }
        
        AbstractButton button = toggleButton?(AbstractButton)CreateFlatToggleButton():CreateFlatButton();
        
        if (action != null) {
            button.setAction(action);
        }
        
        return button;
    }
    
    public static void makeButtonFlat(AbstractButton button) {
        // Override the default look and feel: use basic L&F because some L&F (esp. WinXP)
        // ignores the border property
        if(button instanceof JToggleButton) {
            button.setUI(new BasicToggleButtonUI());
        } else {
            button.setUI(new BasicButtonUI());
        }
        
        button.setRolloverEnabled(true);
        
        UIDefaults table = UIManager.getLookAndFeelDefaults();
        button.setBorder(new BasicBorders.RolloverButtonBorder(
                table.getColor("controlShadow"), //$NON-NLS-1$
                table.getColor("controlDkShadow"), //$NON-NLS-1$
                table.getColor("controlHighlight"), //$NON-NLS-1$
                table.getColor("controlLtHighlight"))); //$NON-NLS-1$
    }

    /**
     * A helper class for creating actions for a toggle button.
     */
    public static abstract class AbstractToggleAction extends AbstractAction {
        /**
         * AbstractToggleAction constructor.
         */
        public AbstractToggleAction(String name, Icon icon) {
            super(name, icon);
            putValue(TOGGLE_BUTTON, Boolean.TRUE);
        }
    }

    /** 
     * Shifts the image to the specified coordinates, growing or
     * shrinking the image size as required.
     * 
     * @param originalImage
     * @param x horizontal position of top left corner
     * @param y vertical position of top left corner 
     * @return image containing the original at the specified position
     */
    public static Image shiftImage(Image originalImage, int x, int y) {
        
        // Create a blank image and initialize graphics painter
        
        BufferedImage newImage = new BufferedImage(
            originalImage.getWidth(null) + x, 
            originalImage.getHeight(null) + y, 
            BufferedImage.TYPE_INT_ARGB_PRE
        );
        Graphics2D g2d = newImage.createGraphics();
        g2d.setRenderingHints(RENDERING_HINTS_HIGH_QUALITY);

        // Now paint the original on the new image
        
        g2d.drawImage(originalImage, x, y, null);
        g2d.dispose();
        
        return newImage;
    }
    
    /**
     * Crops the edges of the image, as specified. If the crop dimensions
     * are negative, the image is grown.
     * 
     * @param originalImage
     * @param left number of pixels to trim from the the left edge
     * @param top pixels to trim from the top edge 
     * @param right pixels to trim from the right edge
     * @param bottom pixels to trim from the bottom edge
     * @return cropped image
     */
    public static Image cropImage(Image originalImage, int left, int top, int right, int bottom) {
        BufferedImage newImage = new BufferedImage(
                originalImage.getWidth(null) - left - right,
                originalImage.getHeight(null) - top - bottom,
                BufferedImage.TYPE_INT_ARGB_PRE
        );
        Graphics2D g2d = newImage.createGraphics();
        g2d.setRenderingHints(RENDERING_HINTS_HIGH_QUALITY);
        
        g2d.drawImage(originalImage, -1 * left, -1 * top, null);
        g2d.dispose();
        
        return newImage;
    }
    
    /**
     * Rotates the image around its center by the specified angle. The original image  
     * size is maintained, so cropping may occur as a result of the rotation.
     * 
     * @param originalImage
     * @param theta angle in radians (a positive angle indicates clockwise rotation)
     * @return rotated image
     */
    public static Image rotateImage (Image originalImage, double theta) {
        BufferedImage newImage = new BufferedImage(
                originalImage.getWidth(null),
                originalImage.getHeight(null),
                BufferedImage.TYPE_INT_ARGB_PRE
        );
        Graphics2D g2d = newImage.createGraphics();
        g2d.setRenderingHints(RENDERING_HINTS_HIGH_QUALITY);
        
        g2d.rotate(theta, originalImage.getWidth(null) / (double)2, originalImage.getHeight(null) / (double)2);
        g2d.drawImage( originalImage, 0, 0, null);
        g2d.dispose();
        
        return newImage;
    }
    
    /**
     * Flips the image vertically and/or horizontally. 
     * 
     * @param originalImage
     * @param flipType flip direction
     * @return flipped image
     */
    public static Image flipImage (Image originalImage, FlipOrientation flipType) {
        BufferedImage newImage = new BufferedImage(
                originalImage.getWidth(null),
                originalImage.getHeight(null),
                BufferedImage.TYPE_INT_ARGB_PRE
        );
        Graphics2D g2d = newImage.createGraphics();
        g2d.setRenderingHints(RENDERING_HINTS_HIGH_QUALITY);
       
        AffineTransform flipTransform = new AffineTransform(
                (flipType == FlipOrientation.Horizontal || flipType == FlipOrientation.BiDirectional)? -1.0 : 1.0, 
                0.0, 0.0,
                (flipType == FlipOrientation.Vertical   || flipType == FlipOrientation.BiDirectional)? -1.0 : 1.0, 
                0.0, 0.0);
        flipTransform.translate(
                (flipType == FlipOrientation.Horizontal || flipType == FlipOrientation.BiDirectional)? -originalImage.getWidth(null) : 0.0,
                (flipType == FlipOrientation.Vertical   || flipType == FlipOrientation.BiDirectional)? -originalImage.getHeight(null) : 0.0);
        
        g2d.setTransform(flipTransform);
        g2d.drawImage(originalImage, 0, 0, null);
        g2d.dispose();
        
        return newImage;
    }

    // Helper fields for fixMenuItem()
    private static final String lowerCaseOSName = System.getProperty("os.name").toLowerCase();
    private static final boolean JUSTIFY_MENU_ITEM_HACK = 
        System.getProperty("java.version").startsWith("1.5") && 
        !(lowerCaseOSName.startsWith("mac os x") || lowerCaseOSName.endsWith("vista"));
    
    /**
     * Fix up the given menu item so that it is justified correctly, has the associated icon, has no tooltip.
     * @param menuItem the menu item to fix.
     * @return the menu item that was fixed.  ie. the menuItem param.
     */
    public static JMenuItem fixMenuItem(JMenuItem menuItem) {
    
        // HACK: Workaround for left-justifying icons. Sun Bug ID's: 4199382, 4203899.
        // Hack seems to bugger up mac os x
        // On Vista it makes the icon shift left enough so that you can't see most of it.
        // Fixed in Java 6.
        
        if (JUSTIFY_MENU_ITEM_HACK) {
            // Get the icon, calculate its width.
            Icon buttonIcon = menuItem.getIcon() != null ? menuItem.getIcon() :
                            menuItem.getAction() != null ? (Icon) menuItem.getAction().getValue(Action.SMALL_ICON) : null;
            int iconWidth = (buttonIcon == null) ? -(menuItem.getIconTextGap()) : buttonIcon.getIconWidth();
            
            Insets insets = menuItem.getInsets();
            insets.left -= iconWidth;
            menuItem.setMargin(insets);
        }
        
        // Menu items should not have tooltips, so clear it in case the action specifies one
        menuItem.setToolTipText(null);
        
        return menuItem;
    }
    
    /**
     * Creates a new JMenuItem and configures it for use in a popup menu.
     * @param action Action - the action used to configure the JMenuItem
     * @return JMenuItem a new menu item for the action.
     */
    public static JMenuItem makeNewMenuItem(Action action) {
        JMenuItem newMenuItem = new JMenuItem(action);
        return fixMenuItem(newMenuItem);
    }

    /**
     * A helper function to paint a watermark for a component.
     * This should be called in the paintComponent method before calling the super class method.
     * @param g
     */
    public static void paintWithWatermarkHelper(JComponent component, ImageIcon watermark, Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        Paint oldPaint = g2.getPaint();
        g2.setPaint(component.getBackground());

        try {
            // Fill in the background of the component.
            g2.fillRect(0, 0, component.getWidth(), component.getHeight());
    
            // Draw the background image, if any.
            if (watermark != null) {

                // draw the image centred in the visible area, and scaled to fit
                Rectangle viewRect = new Rectangle (0, 0, component.getWidth(), component.getHeight());

                Component parent = component.getParent();
                if (parent instanceof JViewport) {
                    JViewport viewport = (JViewport) parent;
                    viewRect = viewport.getViewRect();
                }

                int imageWidth  = watermark.getIconWidth();
                int imageHeight = watermark.getIconHeight();

                double xZoom = viewRect.getWidth() / imageWidth;
                double yZoom = viewRect.getHeight() / imageHeight;

                double zoom = Math.min (xZoom, yZoom);

                int drawnWidth = (int)(imageWidth * zoom);
                int drawnHeight = (int)(imageHeight * zoom);

                int x = viewRect.x + (viewRect.width - drawnWidth) / 2;
                int y = viewRect.y + (viewRect.height - drawnHeight) / 2;

                Composite oldComposite = g2.getComposite();

                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f));

                g2.drawImage(watermark.getImage(), x, y, x + drawnWidth, y + drawnHeight, 0, 0, imageWidth, imageHeight, null);

                g2.setComposite(oldComposite);
            }
        }
        finally {
            g2.setPaint(oldPaint);
        }
    }
    
    /**
     * Returns the parent component of the component c that has the given type,
     * or <code>null</code> if such parent does not exist. 
     * @param c
     * @param type
     * @return Component
     */
    public static Component getParent(Component c, Class<? extends Container> type) {
        c = c.getParent();
        while (c != null) {
            // assume that they are loaded by the same classloader
            if (c.getClass() == type) {
                return c;
            }
            c = c.getParent();
        }
        return null;
    }
    
    /**
     * A helper method for setting the bold font attribute in the given component.
     * @param c
     * @param useBold
     */
    public static void setFontBold(Component c, boolean useBold) {
        Font f = c.getFont ();
        int style = f.getStyle();
        if (useBold) {
            style |= Font.BOLD;
        } else {
            style &= ~Font.BOLD;
        }
        c.setFont(f.deriveFont(style));
    }
    
    /**
     * A helper method for setting the italic font attribute in the given component.
     * @param c
     * @param useItalic
     */
    public static void setFontItalic(Component c, boolean useItalic) {
        Font f = c.getFont ();
        int style = f.getStyle();
        if (useItalic) {
            style |= Font.ITALIC;
        } else {
            style &= ~Font.ITALIC;
        }
        c.setFont(f.deriveFont(style));
    }
    
    /**
     * Brightens each of the RGB components of color by the specified factor.
     * Note: This is identical to the java.awt.Color.brighten() method, using a variable factor.
     * 
     * @param color color to brighten
     * @param factor luminosity factor; if negative, the color is darkened  
     * @return brightened color
     */
    public static Color brightenColor(Color color, double factor) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        
        int i = (int) (1.0 / (1.0 - factor));
        if ( r == 0 && g == 0 && b == 0) {
           return new Color(i, i, i);
        }
        
        if ( r > 0 && r < i ) {
            r = i;
        }
        if ( g > 0 && g < i ) {
            g = i;
        }
        if ( b > 0 && b < i ) {
            b = i;
        }

        return new Color(Math.min((int)(r/factor), 255),
                         Math.min((int)(g/factor), 255),
                         Math.min((int)(b/factor), 255));
    }

    /**
     * Returns an icon that combines the 2 specified icons.
     * One will be drawn over top of the base icon.
     * The resulting icon will be large enough to hold both icons.
     * @param underIcon  the icon to be drawn first
     * @param overIcon   the icon to be drawn over top
     * @return the combined icon
     */
    public static Icon combineIcons(Icon underIcon, Icon overIcon) {
        int newWidth = Math.max(underIcon.getIconWidth(), overIcon.getIconWidth());
        int newHeight = Math.max(underIcon.getIconHeight(), overIcon.getIconHeight());

        BufferedImage combinedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = combinedImage.createGraphics();
        underIcon.paintIcon(null, g2d, 0, 0);
        overIcon.paintIcon(null, g2d, 0, 0);
        g2d.dispose();

        return new ImageIcon(combinedImage);
    }

    /**
     * Changes the specified text component to support undo and redo using Ctrl-Z and Ctrl-Y.
     * @param textComponent  the text component to be modified
     */
    public static void makeTextComponentUndoable(JTextComponent textComponent) {
        final UndoManager undoManager = new UndoManager();
        final Action undoAction = new AbstractAction(UIMessages.instance.getString("GenericUndoName")) { //$NON-NLS-1$
            private static final long serialVersionUID = -2135822508998640523L;

                public void actionPerformed(ActionEvent e) {
                    try {
                        if (undoManager.canUndo()) {
                            undoManager.undo();
                        }
                    }
                    catch (CannotUndoException ex) {
                    }
                }
            };
        final Action redoAction = new AbstractAction(UIMessages.instance.getString("GenericRedoName")) { //$NON-NLS-1$
            private static final long serialVersionUID = 2928127985356997903L;

                public void actionPerformed(ActionEvent e) {
                    try {
                        if (undoManager.canRedo()) {
                            undoManager.redo();
                        }
                    }
                    catch (CannotUndoException ex) {
                    }
                }
            };

        UndoableEditListener undoListener = new UndoableEditListener() {
                public void undoableEditHappened(UndoableEditEvent e) {
                    undoManager.addEdit(e.getEdit());
                }
            };

        textComponent.getDocument().addUndoableEditListener(undoListener);

        Keymap keymap = JTextComponent.addKeymap("TextUndoRedoKeymap", textComponent.getKeymap()); //$NON-NLS-1$
        keymap.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK), undoAction);
        keymap.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_MASK), redoAction);
        textComponent.setKeymap(keymap);
    }

    /**
     * Loads an <code>Icon</code> from the specified icon path.  The <code>ClassLoader</code>
     * of the given class will be used to load the icon.
     * @param iconUrl
     * @return Icon
     */
    public static Icon LoadImageIcon(URL iconUrl) {
        if (iconUrl != null) {
            return new ImageIcon(iconUrl);
        }
        return null;
    }
    
    /**
     * Loads an <code>Icon</code> from the specified icon path.  This method will use
     * the <code>ClassLoader</code> that loaded this class to load the icon.
     * @param iconPath
     * @return Icon
     */
    public static Icon LoadImageIcon(String iconPath) {
        return LoadImageIcon(UIUtilities.class, iconPath);
    }

    /**
     * Loads an <code>Icon</code> from the specified icon path.  This method will use
     * the <code>ClassLoader</code> that loaded this class to load the icon.
     * @param c the class to get the resource from / for
     * @param iconPath
     * @return Icon
     */
    public static Icon LoadImageIcon(Class<?> c, String iconPath) {
        if (iconPath != null && c != null) {
            URL imagePath = c.getResource(iconPath);
            return LoadImageIcon(imagePath);
        }
        return null;
    }
    
    /**
     * Method makeLocalObjectDataFlavor
     * 
     * @param representationClass
     * @param name
     * 
     * @return Returns a DataFlavour that represents a Java object local to this VM
     */
    public static DataFlavor makeLocalObjectDataFlavor (Class<?> representationClass, String name) {
        return new DataFlavor (DataFlavor.javaJVMLocalObjectMimeType + ";class=" + representationClass.getName(), name); //$NON-NLS-1$
    }
    
    /**
     * Create a label that looks a bit like a post-it note, nice for giving 
     * feedback without makeing people think they've made an error.
     * @param text
     * @return JLabel
     */
    public static JLabel createInfoLabel(String text) {
        JLabel label = new JLabel(text);
        label.setOpaque(true);

        Border inner = BorderFactory.createEmptyBorder(2,4,2,4);
        Border outer = BorderFactory.createLineBorder(SystemColor.info.darker());
        label.setBorder(BorderFactory.createCompoundBorder(outer, inner));
        label.setBackground(SystemColor.info);

        return label; 
    }
    
    /**
     * Add a search box to a UtilTree.
     * @param tree A searchable UtilTree.
     * @return A panel containing the given UtilTree and a search box.
     */
    public static JPanel makeSearchablePanel(UtilTree tree) {
        JPanel panel = new JPanel(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(tree);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        SearchTextField searchField = new SearchTextField(tree);
        panel.add(searchField, BorderLayout.SOUTH);
        
        return panel;
    }
    
    
    /**
     * Used by getJOptionPaneIcon().
     * Cache from message type (JOptionPane int constant representing message type)
     * to icon representing that message type.
     */
    private static final Map<Integer, Icon> jOptionPaneMessageTypeToIconMap = new HashMap<Integer, Icon>();
    
    /**
     * Get the icon used in the JOptionPane to signify its message type.
     * Note that this is almost the same as a call to something like: UIManager.getIcon("OptionPane.questionIcon")
     *  but that doesn't work on GTK look-and-feel since there is no corresponding entry in its laf defaults map.
     *  
     * @param jOptionPaneMessageType One of the message types constants defined in the javax.swing.JOptionPane class.
     * @return the corresponding icon, or null if the icon is undefined for the provided message type.
     */
    public static Icon getJOptionPaneIcon(int jOptionPaneMessageType) {
        /*
         * HACK - workaround for GTK.
         * We create a new JOptionPane, get its UI (which should be a BasicOptionPaneUI), then call BasicOptionPaneUI.getIconForType().
         * GTK uses SynthOptionPaneUI (which is package protected), which eventually defers to SynthDefaultLookup to look
         * up default values for ui items such as icons.
         * However using this with non-Synth UIs causes lookup to defer to the basic (non-synth) lookup scheme.
         * Since we can't instantiate SynthOptionPaneUI (which is the SynthUI that we want) we are stuck calling the protected
         * method getIconForType() reflectively after unprotecting it.
         */

        Integer messageTypeInteger = new Integer(jOptionPaneMessageType);

        Icon icon = jOptionPaneMessageTypeToIconMap.get(messageTypeInteger);

        if (icon == null) {

            // Get the icon from a new JOptionPane's UI.
            OptionPaneUI ui = (new JOptionPane()).getUI();
            
            // The code below only works for BasicOptionPaneUIs.
            if (!(ui instanceof BasicOptionPaneUI)) {
                ui = new BasicOptionPaneUI();
                
                // Have to use a UI with a JOptionPane for it to initialize its icons.
                JOptionPane optionPane = new JOptionPane();
                optionPane.setUI(ui);
            }
            
            BasicOptionPaneUI optionPaneUI = (BasicOptionPaneUI)ui;

            try {
                Method method = BasicOptionPaneUI.class.getDeclaredMethod("getIconForType", new Class[] {int.class}); //$NON-NLS-1$
                boolean oldAccessible = method.isAccessible();
                method.setAccessible(true);
                try {
                    icon = (Icon)method.invoke(optionPaneUI, new Object[] {new Integer(jOptionPaneMessageType)});
                } finally {
                    method.setAccessible(oldAccessible);
                }

            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }

            jOptionPaneMessageTypeToIconMap.put(messageTypeInteger, icon);
        }

        return icon;
    }

}
