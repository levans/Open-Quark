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
 * TableTopGemPainter.java
 * Creation date: (03/12/02 11:11:00 AM)
 * By: Edward Lam
 */
package org.openquark.gems.client;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.TexturePaint;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import javax.swing.ImageIcon;

import org.openquark.gems.client.DisplayedGem.DisplayedPartOutput;
import org.openquark.gems.client.IntellicutManager.IntellicutMode;
import org.openquark.gems.client.internal.effects.GaussianKernel;
import org.openquark.gems.client.internal.effects.SimpleEffects;
import org.openquark.gems.client.internal.effects.Voronoi;
import org.openquark.util.Pair;


/**
 * A GemPainter takes a gem and paints it into the tabletop
 * @author Edward Lam
 */
class TableTopGemPainter implements DisplayedGemLocationListener, DisplayedGemSizeListener {
    
    /*
     * There are a number of potential performance improvements to this class:
     * 
     * Coalescing of line drawing
     * - calling Graphics.drawLine() many times in succession is much faster than drawLine() calls interspersed with other Graphics draw calls.
     * 
     * Calling specialized Graphics draw() methods.
     * - a number of Graphics.drawLine() calls is much faster than unspecialized Graphics2D.draw(shape).
     * - drawOval(), drawRoundRect(), fillXXX(), are faster than the unspecialized draw() and fill() methods.
     * - One drawback is that the specialized draw() methods take int arguments, however in some cases the coordinates we want
     *   to specify cannot be expressed in these terms (eg. x = 1.5, y = 0.3).
     * 
     * Caching of image data
     * - more caching could be used.
     * - for instance, the halo image for a rectangle (round or not) could be broken into corner sections and edge sections.
     *   The image could be constructed by adding the corner sections to appropriately scaled edge sections.
     *   Probably the same could be done for triangles, rotating the edge section data, if we could figure out how to do the corners.
     * 
     * Creation of new Graphics objects using Graphics.create() (then dispose()).
     * - Can be used instead of saving and resetting Graphics object properties (such as clip or color).
     * - This is heavily optimized, as it is used extensively within AWT/Swing.
     * 
     * We might also be able to save some work painting triangles, if we check for intersection using Graphics2D.hit().
     * 
     */
    
    /** The tabletop in which to paint. */
    private final TableTop tableTop;

    /** Crazy paving cache **/
    private final transient Map<DisplayedGem, Voronoi> crazyPavingCache = new WeakHashMap<DisplayedGem, Voronoi>();
    
    /** Cached gem tape label */
    private static final transient Map<DisplayedGem, BufferedImage> tapeLabelCache = new WeakHashMap<DisplayedGem, BufferedImage>();
    
    /** Cached gem field name tape label, used for RecordCreationGems */
    private static final transient Map<DisplayedGem, Map<String, BufferedImage>> fieldNameTagLabelCache = new WeakHashMap<DisplayedGem, Map<String, BufferedImage>>();
    
    /** Run halo cache */
    private final transient Map<DisplayedGem, BufferedImage> runHaloCache = new WeakHashMap<DisplayedGem, BufferedImage>();
    
    /** Select halo cache */
    private final transient Map<DisplayedGem, Pair<Color, BufferedImage>> selectHaloCache = new WeakHashMap<DisplayedGem, Pair<Color, BufferedImage>>();

    /** The image to use to indicate that a code gem's code editor is open but not selected. */
    private static final Image codeEditorOpenImage;
    
    /** The image to use to indicate that a code gem's code editor is open and selected. */
    private static final Image codeEditorOpenAndSelectedImage;
    
    /** The crack color for non-photolook tabletops. */
    private static final Color PLAIN_CRACK_COLOUR = new Color(200, 200, 200);
    
    /** The text outline colour for target gem text. */
    private static final Color TARGET_TEXT_OUTLINE_COLOUR = new Color(5, 5, 5, 100);

    // Initialize the open code editor image.
    static {
        codeEditorOpenImage = new ImageIcon(TableTopGemPainter.class.getResource("/Resources/codeEditorOpen.gif")).getImage();
        codeEditorOpenAndSelectedImage = new ImageIcon(TableTopGemPainter.class.getResource("/Resources/selectedCodeEditorOpen.gif")).getImage();
    }

    /**
     * Constructor for a tabletop gem painter.
     */
    TableTopGemPainter(TableTop tableTop) {
        this.tableTop = tableTop;
    }
    
    /**
     * The paint method for a Gem.
     * @param g2d Graphics2D the graphics context 
     * @param displayedGem DisplayedGem the gem to paint
     */
    public void paintGem(DisplayedGem displayedGem, Graphics2D g2d) {
        
        Color prevColour = g2d.getColor();

        // Draw the halo image if necessary.
        paintHalo(displayedGem, g2d);
        
        // Check for an intersection with the paint region
        if (displayedGem.getBounds().intersects(g2d.getClipBounds())) {

            // Paint the body.
            paintBody(displayedGem, g2d);
            
            // Paint the brokenness indicators as necessary
            paintCrazyPaving(displayedGem, g2d);

            // Paint the focus outline
            paintFocus(displayedGem, g2d);

            // Draw the output line and bind point if output connected (colour based on output type)
            paintOutputPart(displayedGem, g2d);

            // Draw the input lines and bind points
            int scArity = displayedGem.getNDisplayedArguments();
            for (int i = 0; i < scArity; i++) {
                paintInputPart(displayedGem, i, g2d);
            }
            
            displayedGem.getDisplayedGemShape().getInnerComponentInfo().paint(tableTop, g2d);
        }

        g2d.setColor(prevColour);
    }
    
    /**
     * Paint the focus outline (if any) for the given displayed gem.
     * @param displayedGem 
     * @param g2d
     */
    private void paintFocus(DisplayedGem displayedGem, Graphics2D g2d) {

        if (tableTop.getFocusedDisplayedGem() == displayedGem) {

            BasicStroke oldStroke = (BasicStroke)g2d.getStroke();
            BasicStroke highlightStroke;
            
            if (displayedGem.getDisplayedGemShape() instanceof DisplayedGemShape.Triangular && tableTop.getTableTopPanel().isPhotoLook()) {
                highlightStroke = new BasicStroke(oldStroke.getLineWidth() * 4, BasicStroke.CAP_ROUND, oldStroke.getLineJoin(),
                                                  oldStroke.getMiterLimit());
                g2d.setColor(getTableTopBaseColour());
                
            } else {
                highlightStroke = new BasicStroke(oldStroke.getLineWidth(), BasicStroke.CAP_ROUND, oldStroke.getLineJoin(),
                                                  oldStroke.getMiterLimit(), new float[]{1, 12}, 0);
                g2d.setColor(Color.yellow);
            }
            
            g2d.setStroke(highlightStroke);
            g2d.draw(displayedGem.getBodyShape());
            
            // Restore the original Stroke object
            g2d.setStroke(oldStroke);
        } 
        
    } 

    /**
     * Paint the halo for the given displayed gem, as necessary.
     * @param displayedGem the displayed gem in question.
     * @param g2d the graphics context into which to draw.
     */
    private void paintHalo(DisplayedGem displayedGem, Graphics2D g2d) {

        // If this Gem is selected or running, check for an intersection with the halo
        Rectangle haloRect = getHaloBounds(displayedGem);
        if (haloRect.intersects(g2d.getClipBounds())) {

            // Grab the appropriate halo image (if any)
            BufferedImage haloImage = null;
            if (tableTop.isRunning(displayedGem)) {
                haloImage = getRunHaloImage(displayedGem);

            } else if (tableTop.isSelected(displayedGem)){
                haloImage = getSelectHaloImage(displayedGem, getGemHaloColour(displayedGem));
            }

            // If there is a halo image, draw this buffer with the correct offset
            if (haloImage != null) {
                g2d.drawImage(haloImage, null, haloRect.x, haloRect.y);
            }
        }
    }
    
    /**
     * Determine the Gem's halo bounds.
     * This is bounds of the selection halo which appears when the gem is selected.
     * @param displayedGem
     * @return Rectangle the bounds of the body section
     */
    private Rectangle getHaloBounds(DisplayedGem displayedGem){
        Rectangle haloRect = new Rectangle(displayedGem.getDisplayedBodyPart().getBounds());
        int haloPlusBlur = DisplayConstants.HALO_SIZE + DisplayConstants.HALO_BLUR_SIZE;
        haloRect.x -= haloPlusBlur;
        haloRect.y -= haloPlusBlur;
        haloRect.width += haloPlusBlur * 2;
        haloRect.height += haloPlusBlur * 2;
        return haloRect;
    }

    /**
     * Return a Gem's halo, in the given highlight colour.
     * @param displayedGem
     * @param gemHighlightColour Color the colour of the halo
     * @return BufferedImage 
     */
    private BufferedImage getHaloImage(DisplayedGem displayedGem, Color gemHighlightColour) {
        
        Rectangle haloRect = getHaloBounds(displayedGem);
        
        // Build blurred shape in buffered image
        BufferedImage haloImage = new BufferedImage(haloRect.width, haloRect.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D bg = haloImage.createGraphics();
        bg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        bg.setPaint(gemHighlightColour);
        
        Shape blurShape;
        
        // Define and draw shape
        if (displayedGem.getDisplayedGemShape() instanceof DisplayedGemShape.Triangular) {
            // triangular
            Polygon blurPoly = new Polygon();
            int LHSPerpendicular = (int) Math.sqrt(DisplayConstants.HALO_SIZE * DisplayConstants.HALO_SIZE - DisplayConstants.HALO_BEVEL_SIZE * DisplayConstants.HALO_BEVEL_SIZE);
            Point blur1 = new Point(DisplayConstants.HALO_SIZE + DisplayConstants.HALO_BLUR_SIZE - DisplayConstants.HALO_BEVEL_SIZE, DisplayConstants.HALO_SIZE + DisplayConstants.HALO_BLUR_SIZE - LHSPerpendicular);
            Point blur2 = new Point(haloRect.width, haloRect.height / 2);
            Point blur3 = new Point(DisplayConstants.HALO_SIZE + DisplayConstants.HALO_BLUR_SIZE - DisplayConstants.HALO_BEVEL_SIZE, haloRect.height - (DisplayConstants.HALO_SIZE + DisplayConstants.HALO_BLUR_SIZE - LHSPerpendicular));
            blurPoly.addPoint(blur1.x, blur1.y);
            blurPoly.addPoint(blur2.x, blur2.y);
            blurPoly.addPoint(blur3.x, blur3.y);
            blurShape = blurPoly;
            
        } else {
            // rectangular
            double haloX = DisplayConstants.HALO_SIZE + DisplayConstants.HALO_BLUR_SIZE - DisplayConstants.HALO_BEVEL_SIZE;
            double haloY = DisplayConstants.HALO_SIZE + DisplayConstants.HALO_BLUR_SIZE - DisplayConstants.HALO_BEVEL_SIZE;
            double haloWidth = haloRect.width - 2 * haloX;
            double haloHeight = haloRect.height - 2 * haloY;

            if (displayedGem.getDisplayedGemShape() instanceof DisplayedGemShape.Oval) {
                double arcSize = haloRect.getHeight()/2.0;
                blurShape = new RoundRectangle2D.Double(haloX, haloY, haloWidth, haloHeight, arcSize, arcSize);
            
            } else if (displayedGem.getDisplayedGemShape() instanceof DisplayedGemShape.Rectangular) {
                blurShape = new Rectangle2D.Double(haloX, haloY, haloWidth, haloHeight);
                
            } else {
                throw new IllegalArgumentException("Don't know how to paint this gem shape: " + displayedGem.getDisplayedGemShape().getClass());
            }
        }
        
        bg.fill(blurShape);
        bg.dispose();

        // Blur shape
        ConvolveOp blur = new ConvolveOp(new GaussianKernel(DisplayConstants.HALO_BLUR_SIZE));
        haloImage = blur.filter(haloImage, null);

        return haloImage;
    }

    /**
     * Return a Gem's run halo
     * @param displayedGem 
     * @return BufferedImage the run halo
     */
    private BufferedImage getRunHaloImage(DisplayedGem displayedGem) {
        BufferedImage cachedRunHaloImage = runHaloCache.get(displayedGem);
        if (cachedRunHaloImage == null) {
            cachedRunHaloImage = getHaloImage(displayedGem, DisplayConstants.RUN_HALO_COLOUR);
            runHaloCache.put(displayedGem, cachedRunHaloImage);
        }
        return cachedRunHaloImage;
    }

    /**
     * Return a Gem's selection halo
     * @param displayedGem 
     * @param haloColour
     * @return BufferedImage the selection halo
     */
    private BufferedImage getSelectHaloImage(DisplayedGem displayedGem, Color haloColour) {

        BufferedImage cachedSelectHaloImage;
        Pair<Color, BufferedImage> cachedColourImagePair = selectHaloCache.get(displayedGem);
        
        if (cachedColourImagePair == null || !cachedColourImagePair.fst().equals(haloColour)) {
            cachedSelectHaloImage = getHaloImage(displayedGem, haloColour);
            selectHaloCache.put(displayedGem, new Pair<Color, BufferedImage>(haloColour, cachedSelectHaloImage));

        } else {
            cachedSelectHaloImage = cachedColourImagePair.snd();
        }

        return cachedSelectHaloImage;
    }

    /**
     * Paint broken cracks to show that a gem is broken.
     * @param displayedGem
     * @param g2d
     */
    private void paintCrazyPaving(DisplayedGem displayedGem, Graphics2D g2d) {
        // If this Gem is 'broken' draw the 'crazy paving'
        if (displayedGem.getGem().isBroken()) {
            Voronoi cachedCrazyPaving = crazyPavingCache.get(displayedGem);

            if (cachedCrazyPaving == null) {
                // Generate an appropriate crazy paving effect
                cachedCrazyPaving = Voronoi.makeRandomAreaVoronoi(displayedGem.getDisplayedBodyPart().getBounds(), DisplayConstants.CRACK_POINTS_PER_QTR);
                crazyPavingCache.put(displayedGem, cachedCrazyPaving);
            }   
            
            // Clip to triangle region
            Shape oldClip = g2d.getClip();
            g2d.setClip(displayedGem.getBodyShape());
            
            // Draw the crazy paving!
            g2d.setColor(getCrackColour(displayedGem));
            cachedCrazyPaving.show(g2d, true, false);

            // Restore clipping
            g2d.setClip(oldClip);
        }   
    }
    
    /**
     * Get an InnerComponentInfo representing the painting of a tape label.
     * @param displayedGem the displayed gem into which to paint.
     * @return the corresponding InnerComponentInfo.
     */
    static DisplayedGemShape.InnerComponentInfo getNameTapeLabelInfo(final DisplayedGem displayedGem) {
        
        return new DisplayedGemShape.InnerComponentInfo() {
            Font font = GemCutterPaintHelper.getTitleFont();

            public Rectangle getBounds() {
                Graphics2D g2d = (Graphics2D)DisplayedGemShape.getGraphics();
              
                return paintNameTapeLabel(displayedGem, font, g2d);
            }


            public List<Rectangle> getInputNameLabelBounds(){
                if(! (displayedGem.getGem() instanceof RecordCreationGem)){
                    return null;
                }

                final Graphics2D g2d = (Graphics2D)DisplayedGemShape.getGraphics();
                return paintFieldNameTag(displayedGem, font, g2d);
            }

            public void paint(TableTop tableTop, Graphics2D g2d) {

                Gem gem = displayedGem.getGem();

                // Draw the field names instead if the gem is a recordCreationGem
                if (gem instanceof RecordCreationGem){
                    paintFieldNameTag(displayedGem, font, g2d);                   

                } else {
                    Rectangle labelBounds = paintNameTapeLabel(displayedGem, font, g2d);

                    // If the gem is a code gem and its code editor is open then draw the appropriate indication of this.
                    if (gem instanceof CodeGem && tableTop.isCodeEditorVisible((CodeGem)gem)) {

                        // If one of the editor's children has focus then we want to use the selected open
                        // editor image to indicate this editor is "activated"
                        Image editorImage = null;
                        if (tableTop.getCodeGemEditor((CodeGem)gem).isFocused()) {
                            editorImage = codeEditorOpenAndSelectedImage;
                        } else {
                            editorImage = codeEditorOpenImage;
                        }

                        // Draw the image just below the left side of the name text
                        g2d.drawImage(editorImage, labelBounds.x, labelBounds.y + labelBounds.height - 1, null);
                    }
                }
            }

        };
    }
    
    /**
     * Paint a label with the gem's display text, in the middle of the gem.
     * @param displayedGem
     * @param font
     * @param g2d
     * @return the string bounds of the tape label.
     */
    private static Rectangle paintNameTapeLabel(DisplayedGem displayedGem, Font font, Graphics2D g2d) {
        boolean isLetGem = displayedGem.getGem() instanceof CollectorGem;
        
        // Set the text font
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();
        
        // Get the gem text
        String gemText = displayedGem.getDisplayText();
        
        // Get the label (create as necessary).
        BufferedImage labelImage = tapeLabelCache.get(displayedGem);
        if (labelImage == null) {
            // Create label image
            int tearMarginMedian = (DisplayConstants.INPUT_OUTPUT_LABEL_MARGIN - 1) / 2;
            Rectangle fullLabelSize = fm.getStringBounds(gemText, g2d).getBounds();
            labelImage = SimpleEffects.makeRippedTapeStripLabel(fullLabelSize.getSize(), tearMarginMedian, DisplayConstants.LABEL_COLOUR);
            tapeLabelCache.put(displayedGem, labelImage);
        }

        // Clip to triangle region, this _could_ be the inner triangle for the label...
        Shape oldClip = g2d.getClip();
        g2d.clip(displayedGem.getBodyShape());

        // Get the bounds of the body.
        Rectangle bodyBounds = displayedGem.getDisplayedBodyPart().getBounds();

        // Draw the label image.
        int labelX = isLetGem ? bodyBounds.x + (bodyBounds.width - labelImage.getWidth()) / 2 : 
                                bodyBounds.x + DisplayConstants.BEVEL_WIDTH_X + 1;
        int labelY = bodyBounds.y + ((bodyBounds.height - labelImage.getHeight()) / 2);
        g2d.drawImage(labelImage, null, labelX, labelY);
        
        // Set the text colour
        g2d.setColor(Color.black);

        // Determine the baseline for vertically centered text
        float baselineToCentre = (float)((fm.getAscent() - fm.getDescent()) / 2.0);
        float centredBaseline = (float)bodyBounds.getCenterY() + baselineToCentre;
        
        // Draw the title
        float labelMargin = isLetGem ? DisplayConstants.LET_LABEL_MARGIN : (DisplayConstants.BEVEL_WIDTH_X + DisplayConstants.INPUT_OUTPUT_LABEL_MARGIN + 1);
        g2d.drawString(gemText, bodyBounds.x + labelMargin, centredBaseline - 1);
        
        // Restore clip
        g2d.setClip(oldClip);
        
        Rectangle bounds = fm.getStringBounds(gemText, g2d).getBounds();
        
        return new Rectangle(labelX, labelY, bounds.width, bounds.height);
    }
    
    
    /**
     * Paint labels for the RecordCreationGem's fields, beside its corresponding input.
     * @param dGem 
     * @param font
     * @param g2d
     */
    private static List<Rectangle> paintFieldNameTag(DisplayedGem dGem, Font font, Graphics2D g2d){
        // the rectangle which will be the union of all rectangles
        List<Rectangle> nameTagBounds = new ArrayList<Rectangle>();

        // Set the text font & color
        g2d.setFont(font);
        g2d.setColor(Color.black);
        
        FontMetrics fm = g2d.getFontMetrics();
        // Determine the baseline for vertically centered text
        float baselineToCentre = (float)((fm.getAscent() - fm.getDescent()) / 2.0);
        
        // Get the fieldName to labelImage map for the gem 
        Map<String, BufferedImage> textToImageCache = fieldNameTagLabelCache.get(dGem);
        if (textToImageCache == null){
            textToImageCache = new HashMap<String, BufferedImage>();
        }
        
        // used for adjusting the X point for the label
        int inBoundsWidth = dGem.getDisplayedGemShape().getInBounds().width;
        
        // Clip to triangle region, this _could_ be the inner triangle for the label...
        Shape oldClip = g2d.getClip();
        g2d.clip(dGem.getBodyShape());
        
        // Draw the label for each field name
        List<String> names = ((RecordCreationGem)dGem.getGem()).getCopyOfFieldsList();
        for (int i = 0; i < names.size(); i++) {
            
            String fieldNameText = names.get(i);

            BufferedImage labelImage = textToImageCache.get(fieldNameText);
            // Create the label if doesn't already exist in the cache
            if (labelImage == null) {
                int tearMarginMedian = (DisplayConstants.INPUT_OUTPUT_LABEL_MARGIN - 1) / 2;
                Rectangle fullLabelSize = fm.getStringBounds(fieldNameText, g2d).getBounds();
                labelImage = SimpleEffects.makeRippedTapeStripLabel(fullLabelSize.getSize(), tearMarginMedian, DisplayConstants.LABEL_COLOUR);
                textToImageCache.put(fieldNameText, labelImage);
            }

            // Draw the label image.
            Point connectPoint = dGem.getDisplayedGemShape().getInputConnectPoint(i);
            int labelX = connectPoint.x + inBoundsWidth + DisplayConstants.BEVEL_WIDTH_X ;
            int labelY = connectPoint.y - labelImage.getHeight()/2; 
            g2d.drawImage(labelImage, null, labelX, labelY);

            // draw the title, location is adjusted so the string is in the center of the label image
            g2d.drawString(fieldNameText, (float)labelX + DisplayConstants.INPUT_OUTPUT_LABEL_MARGIN, (float)connectPoint.y + baselineToCentre );
            
            // The rectangle of the whole label 
            Rectangle nameTagBound = new Rectangle(labelX, labelY, labelImage.getWidth(), labelImage.getHeight());
            nameTagBounds.add(nameTagBound);
        }
        
        // Place the map for this gem in the cache 
        fieldNameTagLabelCache.put(dGem, textToImageCache);
    
        // Restore clip
        g2d.setClip(oldClip);
        
        return nameTagBounds;
    
    }
    
    /**
     * Get an InnerComponentInfo representing the painting of a name in bold text.
     * @param displayedGem the displayed gem into which to paint.
     * @return the corresponding InnerComponentInfo.
     */
    static DisplayedGemShape.InnerComponentInfo getBoldNameInfo(final DisplayedGem displayedGem) {
        return new DisplayedGemShape.InnerComponentInfo() {

            public Rectangle getBounds() {
                Graphics2D g2d = (Graphics2D)DisplayedGemShape.getGraphics();
                return paintBoldName(displayedGem, Color.BLACK, null, g2d);
            }

            public void paint(TableTop tableTop, Graphics2D g2d) {
                Color textColour = getTextColour(displayedGem);
                Color outlineColour = tableTop.getTargetDisplayedCollector() == displayedGem ? TARGET_TEXT_OUTLINE_COLOUR : null;
                paintBoldName(displayedGem, textColour, outlineColour, g2d);
            }

            public List<Rectangle> getInputNameLabelBounds() {
                return null;
            }
        };
    }
    
    /**
     * Paint the gem's display text in bold, in the middle of the gem.
     * @param displayedGem the gem for which the name will be painted.
     * @param textColour the colour of the text.
     * @param outlineColour the colour of the outline, if non-null.
     * @param g2d
     */
    private static Rectangle paintBoldName(DisplayedGem displayedGem, Color textColour, Color outlineColour, Graphics2D g2d) {
        
        String displayText = displayedGem.getDisplayText();
        Rectangle bodyRect = displayedGem.getDisplayedBodyPart().getBounds();

        // Determine the baseline for vertically centred text
        g2d.setFont(GemCutterPaintHelper.getBoldFont());
        FontMetrics fm = g2d.getFontMetrics();

        float baselineToCentre = (float)((fm.getAscent() - fm.getDescent()) / 2.0);
        float centredBaseline = (float)(bodyRect.getCenterY() + baselineToCentre);
        
        float textX = bodyRect.x + DisplayConstants.LET_LABEL_MARGIN;
        float textY = centredBaseline - 1;      // seems to be off by 1 for some reason.
        
        if (outlineColour != null) {
            g2d.setColor(outlineColour);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            
            float outlineX = textX - DisplayConstants.OUTLINE_SIZE;
            float outlineY = textY - DisplayConstants.OUTLINE_SIZE;
            
            for (int x = 0, xmax = 2 * DisplayConstants.OUTLINE_SIZE; x <= xmax; x++) {
                for (int y = 0, ymax = 2 * DisplayConstants.OUTLINE_SIZE; y <= ymax; y++) {
                    g2d.drawString(displayText, outlineX + x, outlineY + y);                            
                }
            }
            
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        }
        
        // Draw the title
        g2d.setColor(textColour);
        g2d.drawString(displayText, textX, textY);
        
        Rectangle bounds = fm.getStringBounds(displayText, g2d).getBounds();
        
        return new Rectangle((int)textX, (int)textY, bounds.width, bounds.height);
    }
    
    /**
     * Get a new BufferedImage with concentric rings alternating between two given colours.
     * Note: due to a bug in GradientPaint, if paint1 is a GradientPaint, and paint2 is an instance of Color, any transparency
     *   in paint1 will show paint2.
     * @param width the width of the image.
     * @param height the height of the image.
     * @param paint1 ring paint.  This will be the paint for the centre ring.
     * @param paint2 ring paint.
     * @return the resulting image.
     */
    private static BufferedImage getTargetImage(int width, int height, Paint paint1, Paint paint2) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g2d = image.createGraphics();

        // Fill with paint2.
        g2d.setPaint(paint2);
        g2d.fillRect(0, 0, width, height);
        
        int nRings = (((width+1) / 2) / DisplayConstants.TARGET_RING_WIDTH) + 1;
        Point centerPoint = new Point(width/2, height/2);
        g2d.setPaint(paint1);

        // Draw circles with paint1.
        for (int i = nRings - 1; i > -1; i--) {
            if (i % 2 == 0) {
                drawCircle(g2d, (i+1) * DisplayConstants.TARGET_RING_WIDTH, DisplayConstants.TARGET_RING_WIDTH, centerPoint);
            }
        }
        
        return image;
    }
    
    /**
     * Draw a circle into the given graphics object.
     * @param g2d the graphics object into which to draw.
     * @param radius the radius of the circle.
     * @param lineWidth the width of the line used to draw the circle.
     * @param centerPoint the centre of the circle.
     */
    private static void drawCircle(Graphics2D g2d, int radius, int lineWidth, Point centerPoint) {
        int xcenter = centerPoint.x;
        int ycenter = centerPoint.y;
        
        Stroke oldStroke = g2d.getStroke();
        g2d.setStroke(new BasicStroke(lineWidth));
        
        // save rendering hints, turn on anti-aliasing so that round edges look round.
        Object oldAntiAliasRenderingHint = g2d.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.drawOval(xcenter-radius, ycenter-radius, 2*radius, 2*radius);//fill circles
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldAntiAliasRenderingHint);
        
        g2d.setStroke(oldStroke);
    }

    /**
     * Paint the body of the displayed gem.
     * @param displayedGem
     * @param g2d
     */
    private void paintBody(DisplayedGem displayedGem, Graphics2D g2d) {
        
        DisplayedGemShape displayedGemShape = displayedGem.getDisplayedGemShape();
        
        if (displayedGemShape instanceof DisplayedGemShape.Triangular) {
            paintBodyTriangle(displayedGem, g2d);

        } else if (displayedGemShape instanceof DisplayedGemShape.Oval) {

            Color gemColour = getBaseGemColour(displayedGem);
            Paint paint;
            
            if (tableTop.getTargetDisplayedCollector() == displayedGem) {
                Rectangle bodyRect = displayedGem.getDisplayedBodyPart().getBounds();
                Paint redGradientPaint = new GradientPaint(0, 0, Color.RED, 25, 32, getTableTopBaseColour(), true);
                Paint gemColourGradientPaint = new GradientPaint(0, 0, new Color(5, 5, 5, 200), 25, 32, new Color(5, 5, 5, 75), true);

                BufferedImage image = getTargetImage(bodyRect.width, bodyRect.height, gemColourGradientPaint, redGradientPaint);
                paint = new TexturePaint(image, bodyRect);
            
            } else {
                paint = gemColour;
            }
            paintBodyOval(displayedGem, paint, g2d);
                
        } else if (displayedGemShape instanceof DisplayedGemShape.Rectangular){
            paintBodyRectangle(displayedGem, g2d);
        
        } else {
            throw new IllegalArgumentException("Don't know how to paint this gem: " + displayedGem.getClass());
        }
    }
    
    /**
     * Paint the body of the displayed gem as an oval shape.
     * @param displayedGem
     * @param g2d
     */
    private void paintBodyOval(DisplayedGem displayedGem, Paint paint, Graphics2D g2d) {
        // The main Gem shape
        Shape bodyShape = displayedGem.getBodyShape();

        // save rendering hints, turn on anti-aliasing so that round edges look round.
        Object oldAntiAliasRenderingHint = g2d.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fill the Gem
        g2d.setPaint(paint);
        g2d.fill(bodyShape);

        // Draw the solid outline of the Gem
        g2d.setColor(Color.black);
        g2d.draw(bodyShape);

        // restore old rendering hint
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldAntiAliasRenderingHint);
    }

    /**
     * Paint the body of the displayed gem as an triangle (trillion?) shape.
     * @param displayedGem
     * @param g2d
     */
    private void paintBodyTriangle(DisplayedGem displayedGem, Graphics2D g2d) {
        Color gemColour = getGemColour(displayedGem);
        Color baseColour = getTableTopBaseColour();

        // The main Gem triangle 
        Polygon gemTri = (Polygon)displayedGem.getBodyShape();
        int[] triAxs = gemTri.xpoints;
        int[] triAys = gemTri.ypoints;

        int width = triAxs[1] - triAxs[2];
        int height = triAys[2] - triAys[1];
        
        // The 'facets' triangle
        Polygon facetTri = new Polygon();
        
        // Calculate the distance between the right outer vertex, and the right 'facet' vertex
        int rightVertexDifference = (int) (Math.sqrt( width * width + height * height ) * (DisplayConstants.DIAGONAL_BEVEL_SIZE) / height);
        
        // Find the Left Hand Side Perpendicular difference
        int LHSPerpendicular = (DisplayConstants.BEVEL_WIDTH_X + rightVertexDifference) * height / width;
        
        // Find the points for the inner triangle
        Point triB1 = new Point(triAxs[0] + DisplayConstants.BEVEL_WIDTH_X, triAys[0] + LHSPerpendicular);
        Point triB2 = new Point(triAxs[1] - rightVertexDifference, triAys[1]);
        Point triB3 = new Point(triAxs[2] + DisplayConstants.BEVEL_WIDTH_X, triAys[2] - LHSPerpendicular);
        
        facetTri.addPoint(triB1.x, triB1.y);
        facetTri.addPoint(triB2.x, triB2.y);
        facetTri.addPoint(triB3.x, triB3.y);

        // Fill the Gem
        Shape oldClip = g2d.getClip();
        Area clipArea = new Area(oldClip);
        clipArea.subtract(new Area(facetTri));
        g2d.setClip(clipArea); 
        Paint gradient = new GradientPaint(0, 0, gemColour, 15, 25, baseColour, true);
        g2d.setPaint(gradient);
        g2d.fill(gemTri);
        g2d.setClip(oldClip);
        
        // Draw and fill the facet triangle
        Color highlightColour = getGemHighlightColour(displayedGem);
        if (tableTop.getTableTopPanel().isPhotoLook()) {
            g2d.setPaint(highlightColour);
        } else {    
            Paint gradient2 = new GradientPaint(0, 0, highlightColour, 25, 32, baseColour, true);
            g2d.setPaint(gradient2);
        }
        g2d.fill(facetTri);
        
        g2d.setColor(baseColour);
        
        // NOTE: Using the default line width or thinner seems to cause the vertical left edge of the 
        //      gem and the vertical left edge of the facet triangle to be drawn improperly (or not at all)
        //      when using JDK 1.4.0.  Making the line width slightly greater than the default seems 
        //      to fix this problem???
        // Set the stroke to have a line width that is slightly wider than the default.  
        g2d.setStroke(new BasicStroke(1.000001f));

        g2d.draw(facetTri);

        // Draw the three 'edges'
        g2d.drawLine(triAxs[0], triAys[0], triB1.x, triB1.y);
        g2d.drawLine(triAxs[1], triAys[1], triB2.x, triB2.y);
        g2d.drawLine(triAxs[2], triAys[2], triB3.x, triB3.y);

        // Draw the solid outline of the Gem
        if (tableTop.getTableTopPanel().isPhotoLook()) {
            g2d.setColor(baseColour);
        } else {    
            g2d.setColor(Color.black);
        }
        g2d.draw(gemTri);
    }
    
    /**
     * Paint the body of the displayed gem as an triangle (emerald?) shape.
     * @param displayedGem
     * @param g2d
     */
    private void paintBodyRectangle(DisplayedGem displayedGem, Graphics2D g2d) {
        // The main Gem rectangle 
        Polygon gemRect = (Polygon)displayedGem.getBodyShape();
        int[] rectAxs = gemRect.xpoints;
        int[] rectAys = gemRect.ypoints;

        // Fill the Gem
        Color baseColour = getTableTopBaseColour();
        Paint gradient = new GradientPaint(0, 0, getGemColour(displayedGem), 15, 25, baseColour, true);
        g2d.setPaint(gradient);
        g2d.fill(gemRect);

        // Draw the 'facets'
        Polygon facetRect = new Polygon();
        Point rectB1 = new Point(rectAxs[0] + DisplayConstants.BEVEL_WIDTH_X, rectAys[0] + DisplayConstants.BEVEL_WIDTH_Y);
        Point rectB2 = new Point(rectAxs[1] - DisplayConstants.BEVEL_WIDTH_X, rectAys[1] + DisplayConstants.BEVEL_WIDTH_Y);
        Point rectB3 = new Point(rectAxs[2] - DisplayConstants.BEVEL_WIDTH_X, rectAys[2] - DisplayConstants.BEVEL_WIDTH_Y);
        Point rectB4 = new Point(rectAxs[3] + DisplayConstants.BEVEL_WIDTH_X, rectAys[3] - DisplayConstants.BEVEL_WIDTH_Y);
        facetRect.addPoint(rectB1.x, rectB1.y);
        facetRect.addPoint(rectB2.x, rectB2.y);
        facetRect.addPoint(rectB3.x, rectB3.y);
        facetRect.addPoint(rectB4.x, rectB4.y);

        // Draw and fill the facet rectangle
        Paint gradient2 = new GradientPaint(0, 0, getGemHighlightColour(displayedGem), 25, 32, baseColour, true);
        g2d.setPaint(gradient2);
        g2d.fill(facetRect);
        g2d.setColor(baseColour);
        g2d.draw(facetRect);

        // Draw the four 'edges'
        g2d.drawLine(rectAxs[0], rectAys[0], rectB1.x, rectB1.y);
        g2d.drawLine(rectAxs[1], rectAys[1], rectB2.x, rectB2.y);
        g2d.drawLine(rectAxs[2], rectAys[2], rectB3.x, rectB3.y);
        g2d.drawLine(rectAxs[3], rectAys[3], rectB4.x, rectB4.y);

        // Draw the solid outline of the Gem
        if (tableTop.getTableTopPanel().isPhotoLook()) {
            g2d.setColor(DisplayConstants.TRANSPARENT_WHITE);
        } else {
            g2d.setColor(Color.black);
        }
        g2d.draw(gemRect);

    }
    
    /**
     * Paint the indicated input part on this displayed gem.
     * @param displayedGem the displayed gem in question.
     * @param inputIndex the index of the input to paint.
     * @param graphics2D the graphics context into which to paint.
     */
    private void paintInputPart(DisplayedGem displayedGem, int inputIndex, Graphics2D graphics2D) {
        
        // Create a local copy of the graphics object, which we can modify safely.
        Graphics2D g2d = (Graphics2D)graphics2D.create();
        
        // Set the colour based on the type
        g2d.setColor(tableTop.getTypeColour(displayedGem.getDisplayedInputPart(inputIndex)));
        
        Point connectPoint = displayedGem.getDisplayedGemShape().getInputConnectPoint(inputIndex);
        int x = connectPoint.x;
        int y = connectPoint.y;
        int inBoundsWidth = displayedGem.getDisplayedGemShape().getInBounds().width;

        Gem inputGem = displayedGem.getGem();
        Gem.PartInput input = inputGem.getInputPart(inputIndex);
        
        // Draw the input burnt if it's, er... burnt
        if (input.isBurnt()) {
            // Burnt
            // Draw the 'split ends' effect
            g2d.setColor(Color.black);
            g2d.drawArc(x, y - DisplayConstants.ARGUMENT_SPACING/2, inBoundsWidth * 2, DisplayConstants.ARGUMENT_SPACING/2, -135, 45);
            g2d.drawArc(x, y, inBoundsWidth * 2, DisplayConstants.ARGUMENT_SPACING/2, 135, -45);
            
        } else {
            // Not burnt    
            // The line
            g2d.drawLine((int)(x + DisplayConstants.BIND_POINT_RADIUS * 2.5), y, x + inBoundsWidth, y);

            // If the current inputPart that we are looking at is not the intellicutPart, or if we are not 
            // pulsing, then we go with normal input drawing, else we draw the input a bit more emphasized.
            Shape inputShape;
            IntellicutManager intellicutManager = tableTop.getIntellicutManager();
            if ((intellicutManager.getIntellicutPart() == displayedGem.getDisplayedInputPart(inputIndex)) && 
                (intellicutManager.getIntellicutMode() == IntellicutMode.PART_INPUT)) {

                // Use special polygon for this input drawing.
                inputShape = getIntellicutInputShape(connectPoint);
                
            } else {
                // Normal input shape.
                inputShape = DisplayedGemShape.getInputShape(connectPoint);
            }
            
            // Antialias input rendering.
            // Also, set stroke control to pure, otherwise circles look deformed.
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            
            if (input.isConnected()) {
                // fill in the shape.
                g2d.fill(inputShape);
                
            } else {
                CollectorGem inputArgumentTarget = GemGraph.getInputArgumentTarget(input);
                if (inputArgumentTarget == tableTop.getTargetCollector()) {
                    
                    // fill in the shape, and circle it red.
                    g2d.fill(inputShape);
                    g2d.setColor(Color.RED);
                    drawTargetMarker(connectPoint, g2d);
                    
                } else {
                    
                    CollectorGem rootCollector = input.getGem().getRootCollectorGem();
                    boolean isReflecting = rootCollector != null && rootCollector == inputArgumentTarget;
                    
                    if (isReflecting) {
                        // Just draw the outline of the shape.
                        g2d.draw(inputShape);
                        
                    } else {
                        // fill in the shape.
                        g2d.fill(inputShape);
                        
                        // circle it black.
                        if (inputArgumentTarget != null) {
                            g2d.setColor(Color.BLACK);
                            drawTargetMarker(connectPoint, g2d);
                        }
                    }
                }
            }
            
            // Highlight if photolook
            if (tableTop.getTableTopPanel().isPhotoLook()) {
                g2d.setColor(getTableTopBaseColour());
                g2d.draw(inputShape);
            }       
        }
        
        g2d.dispose();
    }
    
    /**
     * Draw the Shape representing the target marker for this Gem's nth input.
     * @param bindPoint the location of the bind point
     * @param g2d the graphics object to use.
     */
    private void drawTargetMarker(Point bindPoint, Graphics2D g2d) {
        // Copy the graphics object
        g2d = (Graphics2D)g2d.create();
        
        // Note: we want to use drawOval(), which takes int arguments.
        //   However, we want to draw in between pixels, so we will multiply all coordinates by two, and scale by 0.5.
        
        g2d.scale(0.5, 0.5);
        
        int x = (2 * bindPoint.x);
        int y = (2 * bindPoint.y) - (DisplayConstants.BIND_POINT_RADIUS * 3);   // 3 == 1.5 * 2;
        int diameter = DisplayConstants.BIND_POINT_RADIUS * 6;                  // 6 == 3 * 2
        
        // Draw the shape.
        g2d.drawOval(x, y, diameter, diameter);
        
        // Dispose the graphics copy.
        g2d.dispose();
    }

    /**
     * Get the shape representing an input when affected by Intellicut.
     * @param bindPoint the location of the bind point
     * @return Shape the shape of the bind point
     */
    private Shape getIntellicutInputShape(Point bindPoint){

        int radius = (int)(0.75 * DisplayConstants.BIND_POINT_RADIUS);

        // Where does this blob go vertically
        int x = bindPoint.x + radius;
        int y = bindPoint.y;

        Polygon diamond = new Polygon();
        diamond.addPoint(x + radius, y - 2 * radius);
        diamond.addPoint(x - radius, y);
        diamond.addPoint(x + radius, y + 2 * radius);
        diamond.addPoint(x + 3 * radius, y);

        return diamond;
    }

    /**
     * Paint the output part if any.
     * @param displayedGem DisplayedGem the gem to paint
     * @param g2d Graphics2D the graphics context 
     */
    private void paintOutputPart(DisplayedGem displayedGem, Graphics2D g2d) {

        DisplayedPartOutput displayedOutputPart = displayedGem.getDisplayedOutputPart();
        if (displayedOutputPart == null) {
            return;
        }
        
        Color prevColour = g2d.getColor();
        
        Rectangle outRect = displayedGem.getDisplayedGemShape().getOutBounds();
        int centreY = outRect.y + (outRect.height/2);
        
        // Draw the output line and bind point if output connected (colour based on output type)
        g2d.setColor(tableTop.getTypeColour(displayedOutputPart));
        g2d.drawLine(outRect.x - 1, centreY, outRect.x + outRect.width, centreY);
        Polygon bindPoint = displayedGem.getDisplayedGemShape().getOutputShape();
        
        g2d.fill(bindPoint);
        
        if (tableTop.getTableTopPanel().isPhotoLook()) {
            // Highlight arrow
            g2d.setColor(DisplayConstants.TRANSPARENT_WHITE);
            g2d.draw(bindPoint);
        }   

        g2d.setColor(prevColour);
    }
    
    /**
     * Paint a Connection in the given graphics context
     * @param dConn DisplayedConnection the connection to paint
     * @param g2d Graphics2D the graphics context 
     */
    void paintConnection(DisplayedConnection dConn, Graphics2D g2d) {
        // Make a ConnectionRoute
        ConnectionRoute route = dConn.getConnectionRoute();

        // Get the ConnectionRoute's bounding rectangle
        Rectangle bounds = route.getBoundingRectangle();
        
        if (bounds.intersects(g2d.getClipBounds())) {
            // Need to draw this connection
            // The colour is the type colour of the source part
            if (!tableTop.isBadDisplayedConnection(dConn)) {
                g2d.setColor(tableTop.getTypeColour(dConn.getSource()));
            } else {
                g2d.setColor(Color.red);
            }
            
            // Finally draw it
            route.draw(g2d);
        }   
    }   
    
    /**
     * Get the base colour used for the given displayed gem.
     * @param displayedGem the displayed gem in question.
     * @return the gem colour.
     */
    private Color getBaseGemColour(DisplayedGem displayedGem) {

        Gem gem = displayedGem.getGem();

        if (gem instanceof CodeGem) {
            // Code gems are green.
            return Color.green;
            
        } else if (gem instanceof CollectorGem) {
            // Collectors are black.
            return Color.BLACK;

        } else if (gem instanceof RecordFieldSelectionGem) {
            // RecordFieldSelection gems are indigo-ish.
            return Color.decode("0x8000F0");

        } else if (gem instanceof RecordCreationGem) {
            // Record creation gems are magenta 
            return Color.MAGENTA;
            
        } else if (gem instanceof FunctionalAgentGem) {
            // Data constructors are orange.
            if (((FunctionalAgentGem)gem).getGemEntity().isDataConstructor()) {
                return Color.orange;
            } 

            // Other functional agents are red
            return Color.red;
            
        } else if (gem instanceof ReflectorGem) {
            // Reflectors reflecting the target are pink (somewhere between red and white)..
            if (((ReflectorGem)gem).getCollector() == tableTop.getTargetCollector()) {
                return Color.pink;
            }
            
            // Normal reflectors are white.
            return Color.white;

        } else if (gem instanceof ValueGem) {
            // if the value gem is not editable display it in gray
            if (!tableTop.getTableTopPanel().getValueEntryPanel((ValueGem)gem).isEditable()) {
                return Color.gray;
            }

            // editable value gems are blue
            return Color.blue;

        } else {
            throw new IllegalArgumentException("Unrecognized displayed gem type: " + displayedGem.getClass());
        }
    }
    
    /**
     * Get the colour used as the body colour for the given displayed gem.
     * @param displayedGem the displayed gem in question.
     * @return the gem colour.
     */
    private Color getGemColour(DisplayedGem displayedGem) {

        Color baseGemColour = getBaseGemColour(displayedGem);

        if (tableTop.getTableTopPanel().isPhotoLook()) {
            return GemCutterPaintHelper.getAlphaColour(baseGemColour, DisplayConstants.GEM_FACET_TRANSPARENCY);

        } else {
            return baseGemColour;
        }
    }
    
    /**
     * Get the colour used as the base colour for the tabletop.
     * @return the base colour.
     */
    private Color getTableTopBaseColour() {
        if (tableTop.getTableTopPanel().isPhotoLook()) {
            return DisplayConstants.TRANSPARENT_WHITE;
        } else {
            return Color.WHITE;
        }
    }
    /**
     * Get the colour used as the base colour for the given displayed gem.
     * @param displayedGem the displayed gem in question.
     * @return the base colour.
     */
    private Color getCrackColour(DisplayedGem displayedGem) {

        if (tableTop.getTableTopPanel().isPhotoLook()) {
        
            if (displayedGem.getGem() instanceof ReflectorGem) {
                return GemCutterPaintHelper.getAlphaColour(Color.LIGHT_GRAY, DisplayConstants.GEM_TRANSPARENCY);

            } else {
                return getTableTopBaseColour();
            }

        } else {
            return PLAIN_CRACK_COLOUR;
        }   
    }
    
    /**
     * Get the colour used for highlighting the given displayed gem.
     * @param displayedGem the displayed gem in question.
     * @return the highlight colour.
     */
    private Color getGemHighlightColour(DisplayedGem displayedGem) {
        // Need to come up with a lighter colour of gemColour for highlighting purposes, 
        //   and as the inner colour of the Gem (to make it easier to read the Gem name).
        // TEMP: Michael - Need to come up with a better way of brightening up colours.
        Color baseGemColour = getBaseGemColour(displayedGem);
        
        Color gemHighlightColour = new Color(Math.max(110, baseGemColour.getRed()), Math.max(110, baseGemColour.getGreen()), Math.max(110, baseGemColour.getBlue()));
        if (tableTop.getTableTopPanel().isPhotoLook()) {
            gemHighlightColour = GemCutterPaintHelper.getAlphaColour(gemHighlightColour, DisplayConstants.GEM_TRANSPARENCY);
        }
        
        return gemHighlightColour;
    }
    
    /**
     * Get the colour used for the halo for the given displayed gem.
     * @param displayedGem the displayed gem in question.
     * @return the halo colour.
     */
    private Color getGemHaloColour(DisplayedGem displayedGem) {
        if (tableTop.getTableTopPanel().isPhotoLook()) {
            return DisplayConstants.TRANSPARENT_WHITE;
        } else {
            return getGemHighlightColour(displayedGem);
        }
    }
    
    /**
     * Return the color with which to display the gem's text.
     * @param displayedGem the displayed gem in question.
     * @return the gem colour.
     */
    private static Color getTextColour(DisplayedGem displayedGem) {
        if (displayedGem.getGem() instanceof CollectorGem) {
            return Color.WHITE;
        } else {
            return Color.BLACK;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void gemLocationChanged(DisplayedGemLocationEvent e) {
        // Invalidate cache.  Only really needed for input/output gems
        // Our local use of absolute coords in the 'crazy paving' for broken FunctionalAgentGems
        // forces us to override this and flush the cached object on a move as well as a resize.
        DisplayedGem dGemSource = (DisplayedGem)e.getSource();
        crazyPavingCache.remove(dGemSource);
    }

    /**
     * {@inheritDoc}
     */
    public void gemSizeChanged(DisplayedGemSizeEvent e) {
        DisplayedGem displayedGem = (DisplayedGem)e.getSource();

        tapeLabelCache.remove(displayedGem);
        runHaloCache.remove(displayedGem);
        selectHaloCache.remove(displayedGem);
    }
}
