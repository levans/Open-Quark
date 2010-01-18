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
 * Plotter.java
 * Created: Sep 15, 2002
 * By: LEvans
 */
package org.openquark.cal.samples.plotter;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

/** 
 * A Plotter is a component which is capable of displaying a plotted diagram.
 * A PlotDiagram represents the plotted diagram and this is displayed by a 
 * PlotPanel.  
 * PlotDiagrams can be updated on-the-fly and the PlotPanel associated with 
 * a diagram will update to show the changes.
 * 
 * TODO:
 * -- Initialise charting extents (and optionally lock)
 * - Axis control
 * - Grid control
 *
 * <p>
 * Creation: Sep 17, 2002 at 12:05:49 AM
 */
public class Plotter extends JFrame {

    private static final long serialVersionUID = 7617414987956545587L;

    private static Font areaFont = new Font("Dialog", Font.PLAIN, 10);

    private JPanel ivjJFrameContentPane = null;
    private JScrollPane ivjScrollPane = null;
    private PlotPanel ivjPlotPanel = null;
    private Rectangle range; // Initial/limited
    private boolean rangeLocked; // Whether limited

    /**
     * Construct a Plotter from a title string
     * @param title java.lang.String
     */
    public Plotter(String title) {
        super();
        initialize();
        setTitle(title);
    }

    /**
     * Construct a Plotter from a title string,a range and whether locked
     * @param title java.lang.String 
     * @param range the initial/fixed range
     * @param rangeLocked whether the range is locked (true) or allowed to expand (false)
     */
    public Plotter(String title, Rectangle range, boolean rangeLocked) {
        this.range = range;
        this.rangeLocked = rangeLocked;
        initialize();
        setTitle(title);
    }

    /**
     * Returns the scroll pane.
     * @return JScrollPane
     */
    private JScrollPane getScrollPane() {
        if (ivjScrollPane == null) {
            ivjScrollPane = new JScrollPane();
            ivjScrollPane.setBackground(Color.white);

            ivjScrollPane.setViewportView(getPanel());
        }
        return ivjScrollPane;
    }

    /**
     * Returns the plot panel.
     * @return JPanel
     */
    private JPanel getPanel() {
        if (ivjPlotPanel == null) {
            if (range != null) {
                ivjPlotPanel = new PlotPanel(range, rangeLocked);
            } else {
                ivjPlotPanel = new PlotPanel();
            }
            ivjPlotPanel.setSize(0, 0);
            ivjPlotPanel.setPreferredSize(new Dimension(0, 0));
            ivjPlotPanel.setBackground(Color.white);
        }
        return ivjPlotPanel;
    }
    /**
     * Return the JFrameContentPane property value.
     * @return javax.swing.JPanel
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JPanel getJFrameContentPane() {
        if (ivjJFrameContentPane == null) {
            try {
                // user code begin {1}
                ivjJFrameContentPane = new JPanel();
                ivjJFrameContentPane.setName("JFrameContentPane");
                ivjJFrameContentPane.setLayout(new java.awt.BorderLayout());

                ivjJFrameContentPane.add(getScrollPane(), "Center");

                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJFrameContentPane;
    }

    /**
     * Called whenever the part throws an exception.
     * @param exception java.lang.Throwable
     */
    private void handleException(java.lang.Throwable exception) {

        /* Uncomment the following lines to print uncaught exceptions to stdout */
        System.out.println("--------- UNCAUGHT EXCEPTION ---------");
        exception.printStackTrace(System.out);
    }

    /**
     * Initializes connections
     * @exception java.lang.Exception The exception description.
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void initConnections() throws java.lang.Exception {
    }

    /**
     * Initialize the class.
     */
    private void initialize() {
        try {

            /* Calculate the screen size */
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

            // Center frame on the screen 
            // Shrink if needbe
            //pack();

            // Get size, enforce maximum     
            Dimension frameSize = new Dimension((int) (screenSize.width * 0.5), (int) (screenSize.height * 0.5));
            setSize(frameSize);

            // Set the location     
            setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
            setVisible(true);

            // Set the icon
            //setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/Resources/gemscope.gif")));        

            setName("CALPlotter");
            setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
            setTitle("Plotter");
            setContentPane(getJFrameContentPane());

            initConnections();

            validate();
            repaint();

        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    /**
     * Starts the application.
     * @param args an array of command-line arguments
     */
    public static void main(java.lang.String[] args) {
        try {
            /* Set native look and feel */
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            // Make stuff even more like Windows
            if (UIManager.getSystemLookAndFeelClassName().endsWith("WindowsLookAndFeel")) {
                Font systemPlain11font = new Font("System", Font.PLAIN, 11);
                //set menu fonts
                UIManager.put("Menu.font", systemPlain11font);
                UIManager.put("MenuItem.font", systemPlain11font);
                UIManager.put("CheckBoxMenuItem.font", systemPlain11font);
                UIManager.put("RadioButtonMenuItem.font", systemPlain11font);

                //set fonts for buttons & check boxes
                UIManager.put("Button.font", systemPlain11font);
                UIManager.put("RadioButton.font", systemPlain11font);
                UIManager.put("ToggleButton.font", systemPlain11font);
                UIManager.put("CheckBox.font", systemPlain11font);

                //set fonts for text
                UIManager.put("Label.font", systemPlain11font);
                UIManager.put("TabbedPane.font", systemPlain11font);
                UIManager.put("List.font", systemPlain11font);
                UIManager.put("Tree.font", systemPlain11font);
                UIManager.put("TextField.font", systemPlain11font);
                UIManager.put("TextArea.font", systemPlain11font);
                UIManager.put("PasswordField.font", systemPlain11font);
                UIManager.put("ComboBox.font", systemPlain11font);
                UIManager.put("Slider.font", systemPlain11font);
            }

            // Create the frame //
            Plotter plotter = new Plotter("Test Plotter");
            /// Add a windowListener for the windowClosedEvent
            plotter.addWindowListener(new java.awt.event.WindowAdapter() {
                public void windowClosed(java.awt.event.WindowEvent e) {
                    System.exit(0);
                }
            });
            plotter.repaint();

            // Run some nice test plots
            plotter.drawTest(3);

        } catch (Throwable exception) {
            System.err.println("Exception occured in main.");
            exception.printStackTrace(System.out);
        }
    }

    /**
     * Get the PlotDocument in use
     */
    public PlotDocument getPlotDocument() {
        // Get the plot document
        PlotPanel plotPanel = (PlotPanel) ivjScrollPane.getViewport().getView();
        return plotPanel.getPlotDocument();
    }

    /**
     * Perform a draw test
     * @param testNumber the test number
     */
    private void drawTest(int testNumber) {
        // Get the plot document
        PlotDocument plotDoc = getPlotDocument();

        switch (testNumber) {
            case 0 :
            default :
                // Draw a diagonal line from -200 to 200 with point plots only
                // To optimise plotter page sizing, we plot the end points first
                plotDoc.plotPoint(-200, -200, Color.black);
                plotDoc.plotPoint(200, 200, Color.black);
                plotDoc.plotPoint(-199, -199, Color.red);
                for (int i = 0; i < 199; i++) {
                    plotDoc.plotPoint(i, i, Color.red);
                }
                break;
            case 1:
                // Draw some rectangles at -100, 
                plotDoc.plotRect(-200, -200, 200, 200, Color.red);
                plotDoc.plotRect(100, 100, 100, 100, Color.blue);
                break;
            case 2:
                // Draw some rectangles at -100, 
                plotDoc.plotSector(50, 50, 100, 0, 45, Color.red);
                plotDoc.plotSector(50, 50, 100, 180, 45, Color.blue);
                break;        
            case 3:
                // Draw some box labels
                plotDoc.plotRect(-400,-400,10,10);
                plotDoc.plotRect(400,400,10,10);
                plotDoc.plotBoxLabel(50, 50, 100, 80, 80, "Hello", Color.red, Color.yellow, Color.black);
                plotDoc.plotBoxLabel(-50, -50, 100, 80, 10, "Goodbye", Color.blue, Color.white, Color.black);

        }
    }

    /** 
     * A PlotterException is thrown on errors within the Plotter 
     *
     * <p>
     * Creation: Sep 15, 2002 at 6:24:31 PM
     */
    public class PlotterException extends Exception {

        private static final long serialVersionUID = -6385354219917954488L;

        /**
         * Constructor for PlotterException.
         */
        public PlotterException() {
            super();
        }

        /**
         * Constructor for PlotterException.
         * @param message
         */
        public PlotterException(String message) {
            super(message);
        }

        /**
         * Constructor for PlotterException.
         * @param message
         * @param cause
         */
        public PlotterException(String message, Throwable cause) {
            super(message, cause);
        }

        /**
         * Constructor for PlotterException.
         * @param cause
         */
        public PlotterException(Throwable cause) {
            super(cause);
        }

    }

    /** 
     * Drawable defines the interface for all things which can draw themselves
     * as part of a plot.
     *
     * <p>
     * Creation: Sep 15, 2002 at 7:50:23 PM
     */
    public interface Drawable {
        /**
         * Draw the representation of this 'plot' into the given 
         * graphics context
         * @param gc the graphics context
         */
        public void draw(Graphics2D gc);

        /**
         * Get the extents of this Drawable (i.e. enclosing rectangle) 
         * @return Rectangle the enclosing extents rectangle
         */
        public Rectangle getExtents();

        /**
         * Get the origin of this Drawable.  This is the x,y from the 
         * extents (without size information) 
         * @return Point the origin point
         */
        public Point getOrigin();
    }

    /** 
     * This is the listener interface for classes wishing to listen
     * to PlotDocument changes.
     *
     * <p>
     * Creation: Sep 15, 2002 at 8:12:11 PM
     */
    public interface PlotDocumentListener {
        /**
         * Informs the listener that the full extents of the PlotDocument 
         * have changed when a document addition occured
         * @param plotObject object causing the update
         */
        public void update(Drawable plotObject);

        /**
         * Informs the listener that the full extents of the PlotDocument 
         * have changed when a document addition occured
         * @param plotObject object causing the update
         * @param newExtents the new document extents (minX, minY, width, height)
         */
        public void updateWithExtentsChange(Drawable plotObject, Rectangle newExtents);
    }

    /** 
     * A PlotDocument represents a plot diagram.  These can be built up 
     * by a seried of plot operations.
     *
     * <p>
     * Creation: Sep 15, 2002 at 6:50:49 PM
     */
    public class PlotDocument implements Drawable {

        /** 
         * The representation of a single point of given colour.
         *
         * <p>
         * Creation: Sep 15, 2002 at 7:11:46 PM
         */
        public class PlotPoint implements Drawable {
            private int x, y; // location
            private Color colour;

            /**
             * Create a point of given colour
             * @param x the x coord (document coords)
             * @param y the y coord (documnet coords)
             * @param colour the colour of the point
             */
            public PlotPoint(int x, int y, Color colour) {
                this.x = x;
                this.y = -y; // Use canonical +y 'up'
                this.colour = colour;
            }

            /**
             * @see org.openquark.cal.samples.plotter.Plotter.Drawable#draw(Graphics2D)
             */
            public void draw(Graphics2D gc) {
                // Just plot the point
                gc.setColor(colour);

                // Points are lines with no length
                gc.drawLine(x, y, x, y);
            }

            /**
             * @see org.openquark.cal.samples.plotter.Plotter.Drawable#getExtents()
             */
            public Rectangle getExtents() {
                // A point has extents at x,y and no width/height
                return new Rectangle(x, y, 0, 0);
            }

            /**
                 * @see org.openquark.cal.samples.plotter.Plotter.Drawable#getOrigin()
                 */
            public Point getOrigin() {
                // A point has origin at x,y
                return new Point(x, y);
            }

        }

        /** 
         * The representation of a circle of given colour.
         *
         * <p>
         * Creation: Sep 15, 2002 at 7:11:46 PM
         */
        public class PlotCircle implements Drawable {
            private int x, y, r; // location, radius
            private Color colour;

            /**
             * Create a point of given colour
             * @param x the x coord of the centre (document coords)
             * @param y the y coord of the centre (document coords)
             * @param r the radius (documnet coords)
             * @param colour the colour of the point
             */
            public PlotCircle(int x, int y, int r, Color colour) {
                this.x = x;
                this.y = -y; // Use canonical +y 'up'
                this.r = r;
                this.colour = colour;
            }

            /**
             * @see org.openquark.cal.samples.plotter.Plotter.Drawable#draw(Graphics2D)
             */
            public void draw(Graphics2D gc) {
                // Just plot the circle
                gc.setColor(colour);

                // Draw circle
                gc.drawOval(x - r, y - r, r * 2, r * 2);
            }

            /**
             * @see org.openquark.cal.samples.plotter.Plotter.Drawable#getExtents()
             */
            public Rectangle getExtents() {
                // The circle lives at x-r,y-r and it's size is the diameter
                return new Rectangle(x - r, y - r, 2 * r, 2 * r);
            }

            /**
                 * @see org.openquark.cal.samples.plotter.Plotter.Drawable#getOrigin()
                 */
            public Point getOrigin() {
                // A circle has origin at x-r, y-r
                return new Point(x - r, y - r);
            }

        }

        /** 
         * The representation of a line of given colour.
         *
         * <p>
         * Creation: Sep 15, 2002 at 7:11:46 PM
         */
        public class PlotLine implements Drawable {
            private int x1, y1, x2, y2; // location
            private Color colour;

            /**
             * Create a line of given colour
             * @param x1 the x coord of the start (document coords)
             * @param y1 the y coord of the start (document coords)
             * @param x2 the x coord of the end (document coords)
             * @param y2 the y coord of the end (document coords)
             * @param colour the colour of the point
             */
            public PlotLine(int x1, int y1, int x2, int y2, Color colour) {
                this.x1 = x1;
                this.y1 = -y1; // Use canonical +y 'up'
                this.x2 = x2;
                this.y2 = -y2; // Use canonical +y 'up'
                this.colour = colour;
            }

            /**
             * @see org.openquark.cal.samples.plotter.Plotter.Drawable#draw(Graphics2D)
             */
            public void draw(Graphics2D gc) {
                // Just plot the line
                gc.setColor(colour);

                // Draw line
                gc.drawLine(x1, y1, x2, y2);
            }

            /**
             * @see org.openquark.cal.samples.plotter.Plotter.Drawable#getExtents()
             */
            public Rectangle getExtents() {
                // The line lives in its bounding rectangle
                return new Rectangle(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2 - x1), Math.abs(y2 - y1));
            }

            /**
                 * @see org.openquark.cal.samples.plotter.Plotter.Drawable#getOrigin()
                 */
            public Point getOrigin() {
                // A line has origin at least x,y
                return new Point(Math.min(x1, x2), Math.min(y1, y2));
            }

        }

        /** 
         * The representation of a glyph of given type and colour.
         *
         * <p>
         * Creation: Sep 15, 2002 at 7:11:46 PM
         */
        public class PlotGlyph implements Drawable {
            public final int GLYPH_TYPE_X = 0;
            public final int GLYPH_TYPE_PLUS = 1;
            public final int GLYPH_TYPE_O = 2;
            public final int GLYPH_TYPE_SQUARE = 3;
            public final int GLYPH_TYPE_TRIANGLE = 4;
            private int type;
            private int x, y, size; // location, size
            private Color colour;

            /**
             * Create a point of given colour
             * @param x the x coord of the centre (document coords)
             * @param y the y coord of the centre (document coords)
             * @param size the size: half width/height (document coords)
             * @param type the type of the glyph: one of GLYPH_TYPE_* 
             * @param colour the colour of the point
             */
            public PlotGlyph(int x, int y, int size, int type, Color colour) {
                this.x = x;
                this.y = -y; // Use canonical +y 'up'
                this.size = size;
                this.type = type;
                this.colour = colour;
            }

            /**
             * @see org.openquark.cal.samples.plotter.Plotter.Drawable#draw(Graphics2D)
             */
            public void draw(Graphics2D gc) {
                // Just plot the glyph
                gc.setColor(colour);

                // Depends on glyph type
                switch (type) {
                    case GLYPH_TYPE_X :
                    default :
                        // Draw an 'X'
                        gc.drawLine(x - size, y - size, x + size, y + size);
                        gc.drawLine(x - size, y + size, x + size, y - size);
                        break;

                    case GLYPH_TYPE_PLUS :
                        // Draw a '+'
                        gc.drawLine(x - size, y, x + size, y);
                        gc.drawLine(x, y - size, x, y + size);
                        break;

                    case GLYPH_TYPE_O :
                        // Draw an 'O'
                        gc.drawOval(x - size, y - size, size * 2, size * 2);
                        break;

                    case GLYPH_TYPE_SQUARE :
                        // Draw a square
                        gc.drawLine(x - size, y - size, x + size, y - size);
                        gc.drawLine(x + size, y - size, x + size, y + size);
                        gc.drawLine(x + size, y + size, x - size, y + size);
                        gc.drawLine(x - size, y + size, x - size, y - size);
                        break;

                    case GLYPH_TYPE_TRIANGLE :
                        // Draw a triangle
                        gc.drawLine(x, y - size, x + size, y + size);
                        gc.drawLine(x + size, y + size, x - size, y - size);
                        gc.drawLine(x - size, y - size, x, y - size);
                        break;

                }

            }

            /**
             * @see org.openquark.cal.samples.plotter.Plotter.Drawable#getExtents()
             */
            public Rectangle getExtents() {
                // The glyph lives at x-size, y-size and it's size is 'size'
                return new Rectangle(x - size, y - size, 2 * size, 2 * size);
            }

            /**
                 * @see org.openquark.cal.samples.plotter.Plotter.Drawable#getOrigin()
                 */
            public Point getOrigin() {
                // A glyph has origin at x-size, y-size
                return new Point(x - size, y - size);
            }

        }

        /** 
         * The representation of text of given size, rotation and colour.
         *
         * <p>
         * Creation: Sep 15, 2002 at 7:11:46 PM
         */
        public class PlotText implements Drawable {
            private int x, y, size; // location, rotation, size
            private String text;
            private Color colour;
            private Rectangle cachedExtents;

            /**
             * Create a point of given colour
             * @param x the x coord of the centre (document coords)
             * @param y the y coord of the centre (document coords)
             * @param text the text the text
             * @param size the size of the text (points)
             * @param colour the colour of the point
             */
            public PlotText(int x, int y, String text, int size, Color colour) {
                this.x = x;
                this.y = -y; // Use canonical +y 'up'
                this.text = text;
                this.size = size;
                this.colour = colour;
            }

            /**
             * @see org.openquark.cal.samples.plotter.Plotter.Drawable#draw(Graphics2D)
             */
            public void draw(Graphics2D gc) {
                // Just draw the text
                gc.setColor(colour);

                // Set the size
                Font textFont = areaFont;
                textFont = new Font(textFont.getName(), textFont.getStyle(), size);

                // Draw text
                gc.setFont(textFont);
                gc.drawString(text, x, y);
            }

            /**
             * Determine the extents of the text and cache these
             * @return Rectangle the extents
             */
            private Rectangle getCachedExtents() {
                if (cachedExtents == null) {
                    // Determine the extents
                    BufferedImage bi = new BufferedImage(1, 1, 1);
                    Graphics2D g2c = (Graphics2D) bi.getGraphics();
                    Font textFont = areaFont;
                    FontMetrics fm = g2c.getFontMetrics(new Font(textFont.getName(), textFont.getStyle(), size));
                    Rectangle stringRect = (fm.getStringBounds(text, g2c)).getBounds();
                    stringRect.translate(x, y);
                    cachedExtents = stringRect;
                    g2c.dispose();
                }
                return cachedExtents;
            }

            /**
             * @see org.openquark.cal.samples.plotter.Plotter.Drawable#getExtents()
             */
            public Rectangle getExtents() {
                return getCachedExtents();
            }

            /**
                 * @see org.openquark.cal.samples.plotter.Plotter.Drawable#getOrigin()
                 */
            public Point getOrigin() {
                // Get the origin of the extents
                return getCachedExtents().getLocation();
            }

        }
        
        /** 
         * The representation of a rectangle of given colour.
         *
         * <p>
         * Creation: Dec 14, 2005 at 12:11:46 PM
         */
        public class PlotRect implements Drawable {
            private int x, y, w, h; // location, size
            private Color colour;

            /**
             * Create a rectangle of given colour
             * @param x the x coord (document coords)
             * @param y the y coord (document coords)
             * @param w the width of the rectangle
             * @param h the height of the rectangle
             * @param colour the colour of the point
             */
            public PlotRect(int x, int y, int w, int h, Color colour) {
                this.x = x;
                this.y = -y; // Use canonical +y 'up'
                this.w = w;
                this.h = h; 
                this.colour = colour;
            }

            /**
             * @see org.openquark.cal.samples.plotter.Plotter.Drawable#draw(Graphics2D)
             */
            public void draw(Graphics2D gc) {
                // Just plot the rectangle
                gc.setColor(colour);

                // Draw line
                gc.fillRect(x, y, w, h);
            }

            /**
             * @see org.openquark.cal.samples.plotter.Plotter.Drawable#getExtents()
             */
            public Rectangle getExtents() {
                // Get the bounding rectangle
                return new Rectangle(x, y, w, h);
            }

            /**
                 * @see org.openquark.cal.samples.plotter.Plotter.Drawable#getOrigin()
                 */
            public Point getOrigin() {
                // Get the rectangle origin
                return new Point(x,y);
            }

        }

        /** 
         * The representation of a circle sector of given colour.
         *
         * <p>
         * Creation: Dec 14, 2005 at 12:11:46 PM
         */
        public class PlotSector implements Drawable {
            private int x, y, r; // location, radius length, 
            private double sa, ia; // start angle, interval angle
            private Arc2D arc2D;
            private Color colour;

            /**
             * Create a sector of given colour
             * @param x the x coord of circle centre (document coords)
             * @param y the y coord of circle centre (document coords)
             * @param r the radius length
             * @param sa the angle of the starting radius
             * @param ia the angle of the interval (to the ending radius)
             * @param colour the colour of the point
             */
            public PlotSector(int x, int y, int r, double sa, double ia, Color colour) {
                this.x = x;
                this.y = -y; // Use canonical +y 'up'
                this.r = r;
                this.sa = sa;
                this.ia = ia;
                this.colour = colour;
                this.arc2D = new Arc2D.Double();
                arc2D.setArcByCenter((double)this.x,(double)this.y,(double)this.r,this.sa,this.ia, Arc2D.PIE);
            }

            /**
             * @see org.openquark.cal.samples.plotter.Plotter.Drawable#draw(Graphics2D)
             */
            public void draw(Graphics2D gc) {
                // Just plot the sector
                gc.setColor(colour);

                // Draw line
                gc.fill(arc2D);
            }

            /**
             * @see org.openquark.cal.samples.plotter.Plotter.Drawable#getExtents()
             */
            public Rectangle getExtents() {
                // Get the bounding rectangle of this sector
                return arc2D.getBounds2D().getBounds();  
            }

            /**
                 * @see org.openquark.cal.samples.plotter.Plotter.Drawable#getOrigin()
                 */
            public Point getOrigin() {
                // Get the origin of the bounding rectangle
                return getExtents().getLocation();
            }
        }
        
        
        /** 
         * The representation of a text label in a box (of given colours).
         *
         * <p>
         * Creation: Dec 19, 2005 at 12:11:46 PM
         */
        public class PlotBoxLabel implements Drawable {
            private int x, y, w, h; // location, size
            private int size; // font size
            private String text;
            private Color textColour;
            private Color backColour;
            private Color borderColour;

            /**
             * Create a rectangle of given colour
             * @param x the x coord (document coords)
             * @param y the y coord (document coords)
             * @param w the width of the rectangle
             * @param h the height of the rectangle
             * @param size the size of the font
             * @param text the text of the label
             * @param textColour the colour of the point
             * @param backColour the colour of the background (box)
             * @param borderColour the colour of the box border
             */
            public PlotBoxLabel(int x, int y, int w, int h, int size, String text, Color textColour, Color backColour, Color borderColour) {
                this.x = x;
                this.y = -y; // Use canonical +y 'up'
                this.w = w;
                this.h = h; 
                this.size = size;
                this.text = text;
                this.textColour = textColour;
                this.backColour = backColour;
                this.borderColour = borderColour;
            }

            /**
             * @see org.openquark.cal.samples.plotter.Plotter.Drawable#draw(Graphics2D)
             */
            public void draw(Graphics2D gc) {
                // Just plot the rectangle of the right size
                gc.setColor(backColour);
                gc.fillRect(x, y, w, h);
                
                // Set the clip rectangle for text drawing
                Shape oldClipRegion = gc.getClip();
                gc.setClip(x, y, w, h);
                
                // Set the font size
                Font textFont = areaFont;
                textFont = new Font(textFont.getName(), textFont.getStyle(), size);
                gc.setFont(textFont);
                
                // Determine the text size
                FontMetrics fm = gc.getFontMetrics();
                Rectangle stringRect = (fm.getStringBounds(text, gc)).getBounds();
                
                // Justify the stringRect within the rectangle x,y,w,h, centred or left-justified if oversize
                int txtX, txtY;
                int stringHeight = (int)stringRect.getHeight();
                int stringWidth = (int)stringRect.getWidth();
                if (stringHeight > h) {
                        // Align bottom of rectangles
                        txtY = y + (stringHeight - h);
                } else {
                          // Centred vertically
                        txtY = y + ((h - stringHeight) / 2);   // -
                }
                
                if (stringWidth > w) {
                        // Align left of rectangles
                        txtX = x;
                } else {
                          // Centred horizontally
                        txtX = x + ((w - stringWidth) / 2);   
                }
                
                // TEMP
                //gc.setColor(Color.black);
                //int txtX2 = txtX + stringWidth;
                //int txtY2 = txtY + stringHeight;
                //gc.drawLine(txtX, txtY, txtX2, txtY);
                //gc.drawLine(txtX2, txtY, txtX2, txtY2);
                //gc.drawLine(txtX2, txtY2, txtX, txtY2);
                //gc.drawLine(txtX, txtY2, txtX, txtY);
                
                // Draw text
                gc.setColor(textColour);
                gc.drawString(text, txtX, txtY + fm.getAscent());
                
                // Reset the clip
                gc.setClip(oldClipRegion);
                
                // Plot the border lines of the right colour
                gc.setColor(borderColour);
                int x2 = x + w;
                int y2 = y + h;
                gc.drawLine(x, y, x2, y);
                gc.drawLine(x2, y, x2, y2);
                gc.drawLine(x2, y2, x, y2);
                gc.drawLine(x, y2, x, y);
            }

            /**
             * @see org.openquark.cal.samples.plotter.Plotter.Drawable#getExtents()
             */
            public Rectangle getExtents() {
                // Get the bounding rectangle
                return new Rectangle(x, y, w, h);
            }

            /**
                 * @see org.openquark.cal.samples.plotter.Plotter.Drawable#getOrigin()
                 */
            public Point getOrigin() {
                // Get the rectangle origin
                return new Point(x,y);
            }
        }
        

        private int minX, minY = minX = Integer.MAX_VALUE; // Minimal extents in each dimension
        private int maxX, maxY = maxX = -Integer.MAX_VALUE; // Maximal extents in each dimension

        private List<Drawable> ops; // Plot operations (Drawables) 
        private HashSet<PlotDocumentListener> listeners;
        private boolean rangeLocked = false;

        /**
         * Create an empty plot document
         */
        public PlotDocument() {
            // Create the plot operations list - initialise to 100 operations 
            ops = new ArrayList<Drawable>(100);

            // Create the listeners set (we usually have at least one)
            listeners = new HashSet<PlotDocumentListener>();
        }

        /**
         * Create an empty plot document with initial size and lock status
         * @param initialRange the initial range/limits
         * @param lockedRange if initialRange is given, fixes this if true
         * otherwise the range is allowed to grow
         */
        public PlotDocument(Rectangle initialRange, boolean lockedRange) {
            this();

            // Set initial limits
            if (initialRange != null) {
                minX = initialRange.x;
                minY = initialRange.y;
                maxX = minX + initialRange.width;
                maxY = minY + initialRange.height;
                // Set whether this is locked 
                rangeLocked = lockedRange;
            }
        }

        /**
         * Get the extents of the current document.  
         * This is the rectange which encloses the diagram.
         * @return Rectangle the extents rectangle (document coords)
         */
        public Rectangle getExtents() {
            return new Rectangle(minX, minY, 1 + maxX - minX, 1 + maxY - minY);
        }

        /**
         * @see org.openquark.cal.samples.plotter.Plotter.Drawable#getOrigin()
         */
        public Point getOrigin() {
            return new Point(minX, minY);
        }

        /**
         * Add a listener
         * @param listener the new listener
         */
        public void addListener(PlotDocumentListener listener) {
            synchronized (listeners) {
                listeners.add(listener);
            }
        }

        /**
         * Remove a listener
         * @param listener the listener to remove
         */
        public void removeListener(PlotDocumentListener listener) {
            synchronized (listeners) {
                listeners.remove(listener);
            }
        }

        /**
         * Plot a point in the given colour
         * @param x the x coord of the point (document coords)
         * @param y the y coord of the point (document coords)
         * @param colour the colour of the point
         * @return PlotDocument the document (for chaining convenience)
         */
        public PlotDocument plotPoint(int x, int y, Color colour) {
            addOp(new PlotPoint(x, y, colour));
            return this;
        }

        /**
         * Plot a point in black
         * @param x the x coord of the point (document coords)
         * @param y the y coord of the point (document coords)
         * @return PlotDocument the document (for chaining convenience)
         */
        public PlotDocument plotPoint(int x, int y) {
            // Defer to more general routine
            return plotPoint(x, y, Color.black);
        }

        /**
         * Plot a circle in the given colour
         * @param x the x coord of the circle centre (document coords)
         * @param y the y coord of the circle centre (document coords)
         * @param r the radius of the circle (document coords)
         * @param colour the colour of the circle
         * @return PlotDocument the document (for chaining convenience)
         */
        public PlotDocument plotCircle(int x, int y, int r, Color colour) {
            addOp(new PlotCircle(x, y, r, colour));
            return this;
        }

        /**
         * Plot a circle in black
         * @param x the x coord of the circle centre (document coords)
         * @param y the y coord of the circle centre (document coords)
         * @param r the radius of the circle (document coords)
         * @return PlotDocument the document (for chaining convenience)
         */
        public PlotDocument plotCircle(int x, int y, int r) {
            // Defer to the more general routine
            return plotCircle(x, y, r, Color.black);
        }

        /**
         * Plot a line in the given colour
         * @param x1 the x coord of the start (document coords)
         * @param y1 the y coord of the start (document coords)
         * @param x2 the x coord of the end (document coords)
         * @param y2 the y coord of the end (document coords)
         * @param colour the colour of the line
         * @return PlotDocument the document (for chaining convenience)
         */
        public PlotDocument plotLine(int x1, int y1, int x2, int y2, Color colour) {
            addOp(new PlotLine(x1, y1, x2, y2, colour));
            return this;
        }

        /**
         * Plot a line in black
         * @param x1 the x coord of the start (document coords)
         * @param y1 the y coord of the start (document coords)
         * @param x2 the x coord of the end (document coords)
         * @param y2 the y coord of the end (document coords)
         * @return PlotDocument the document (for chaining convenience)
         */
        public PlotDocument plotLine(int x1, int y1, int x2, int y2) {
            // Defer to the more general routine
            return plotLine(x1, y1, x2, y2, Color.black);
        }

        /**
         * Plot a glyph in the given colour
         * @param x the x coord of the glyph (document coords)
         * @param y the y coord of the glyph (document coords)
         * @param size the size of the glyph (document coords)
         * @param type the type of the glyph (PlotGlyph.GLYPH_TYPE.*)
         * @param colour the colour of the glyph
         * @return PlotDocument the document (for chaining convenience)
         */
        public PlotDocument plotGlyph(int x, int y, int size, int type, Color colour) {
            addOp(new PlotGlyph(x, y, size, type, colour));
            return this;
        }

        /**
         * Plot a glyph in black
         * @param x the x coord of the glyph (document coords)
         * @param y the y coord of the glyph (document coords)
         * @param size the size of the glyph (document coords)
         * @param type the type of the glyph (PlotGlyph.GLYPH_TYPE.*)
         * @return PlotDocument the document (for chaining convenience)
         */
        public PlotDocument plotGlyph(int x, int y, int size, int type) {
            // Defer to the more general routine
            return plotGlyph(x, y, size, type, Color.black);
        }

        /**
         * Plot some text in the given colour
         * @param x the x coord of the text  (document coords)
         * @param y the y coord of the text (document coords)
         * @param text the text
         * @param size the size of the text (points)
         * @param colour the colour of the glyph
         * @return PlotDocument the document (for chaining convenience)
         */
        public PlotDocument plotText(int x, int y, String text, int size, Color colour) {
            addOp(new PlotText(x, y, text, size, colour));
            return this;
        }

        /**
         * Plot some text in black
         * @param x the x coord of the text  (document coords)
         * @param y the y coord of the text (document coords)
         * @param text the text
         * @param size the size of the text (points)
         * @return PlotDocument the document (for chaining convenience)
         */
        public PlotDocument plotText(int x, int y, String text, int size) {
            // Defer to the more general routine
            return plotText(x, y, text, size, Color.black);
        }
        
        /**
         * Plot a rectangle in the given colour
         * @param x the x coord of the rectangle  (document coords)
         * @param y the y coord of the rectangle (document coords)
         * @param w the width of the rectangle
         * @param h the height of the rectangle
         * @param colour the colour of the rectangle
         * @return PlotDocument the document (for chaining convenience)
         */
        public PlotDocument plotRect(int x, int y, int w, int h, Color colour) {
            addOp(new PlotRect(x, y, w, h, colour));
            return this;
        }

        /**
         * Plot a rectangle in black
         * @param x the x coord of the rectangle  (document coords)
         * @param y the y coord of the rectangle (document coords)
         * @param w the width of the rectangle
         * @param h the height of the rectangle
         * @return PlotDocument the document (for chaining convenience)
         */
        public PlotDocument plotRect(int x, int y, int w, int h) {
            // Defer to the more general routine
            return plotRect(x, y, w, h, Color.black);
        }
        
        /**
         * Plot a sector in the given colour
         * @param x the x coord of the sector's circle centre (document coords)
         * @param y the y coord of the sector's circle centre (document coords)
         * @param r the radius length of the sector
         * @param sa the angle of the starting bounding radius
         * @param ia the angle of the interval (to the ending radius)
         * @param colour the colour of the rectangle
         * @return PlotDocument the document (for chaining convenience)
         */
        public PlotDocument plotSector(int x, int y, int r, double sa, double ia, Color colour) {
            addOp(new PlotSector(x, y, r, sa, ia, colour));
            return this;
        }

        /**
         * Plot a sector in black
         * @param x the x coord of the sector's circle centre (document coords)
         * @param y the y coord of the sector's circle centre (document coords)
         * @param r the radius length of the sector
         * @param sa the angle of the starting bounding radius
         * @param ia the angle of the interval (to the ending radius)
         * @return PlotDocument the document (for chaining convenience)
         */
        public PlotDocument plotSector(int x, int y, int r, double sa, double ia) {
            // Defer to the more general routine
            return plotSector(x, y, r, sa, ia, Color.black);
        }

        /**
         * Plot a box label in given colours
         * @param x the x coord of the box label (document coords)
         * @param y the y coord of the box label (document coords)
         * @param w the width of the box label
         * @param h the height of the box label
         * @param size the size of the font in the label
         * @param text the label text
         * @param textColour the colour of the text
         * @param backColour the colour of the background
         * @param borderColour the colour of the border
         * @return PlotDocument the document (for chaining convenience)
         */
        public PlotDocument plotBoxLabel(int x, int y, int w, int h, int size, String text, Color textColour, Color backColour, Color borderColour) {
            addOp(new PlotBoxLabel(x, y, w, h, size, text, textColour, backColour, borderColour));
            return this;
        }

        /**
         * Plot a box label in black and white
         * @param x the x coord of the box label (document coords)
         * @param y the y coord of the box label (document coords)
         * @param w the width of the box label
         * @param h the height of the box label
         * @param size the size of the font in the label
         * @param text the label text
         * @return PlotDocument the document (for chaining convenience)
         */
        public PlotDocument plotBoxLabel(int x, int y, int w, int h, int size, String text) {
            // Defer to the more general routine
            return plotBoxLabel(x, y, w, h, size, text, Color.black, Color.white, Color.black);
        }
        
        
        
        /**
         * @see org.openquark.cal.samples.plotter.Plotter.Drawable#draw(Graphics2D)
         */
        public void draw(Graphics2D gc) {
            // Draw all the drawables in this PlotDocument
            synchronized (ops) {
                for (Iterator<Drawable> it = ops.iterator(); it.hasNext();) {
                    it.next().draw(gc);
                }
            }
        }

        /**
         * Get the number of plot objects currently being plotted
         * @return int the number of plot objects
         */
        public int getNumPlotObjects() {
            return ops.size();
        }

        /**
         * Normalise the origin of the rectangle rec as if the origin of the 
         * document was at (0,0).  This is typically used to determine where in
         * a regular zero origin coord space a given document rectangle might be
         * located
         * @param rec the rectangle which is OVERWRITTEN
         * @return Rectangle the same rectangle (for convenience)
         */
        public Rectangle translateRect(Rectangle rec) {
            rec.translate(-minX, -minY);
            return rec;
        }

        /**
         * Normalise the origin of a point as if the origin of the 
         * document was at (0,0).  This is typically used to determine where in
         * a regular zero origin coord space a given document point might be
         * located
         * @param point the point which is OVERWRITTEN
         * @return Point the same point (for convenience)
         */
        public Point translatePoint(Point point) {
            point.x -= minX;
            point.y -= minY;
            return point;
        }

        /**
         * Add a plot operation to this document.
         * The given plot operation is added to the end of he plot operations list.
         */
        private void addOp(Drawable plotOp) {
            // Update the document extents
            Rectangle extents = plotOp.getExtents();
            boolean extentsChanged = false;
            if (!rangeLocked) {
                if (extents.x < minX) {
                    minX = extents.x;
                    extentsChanged = true;
                }
                if (extents.y < minY) {
                    minY = extents.y;
                    extentsChanged = true;
                }
                int eMaxX = extents.x + extents.width;
                if (eMaxX > maxX) {
                    maxX = eMaxX;
                    extentsChanged = true;
                }
                int eMaxY = extents.y + extents.height;
                if (eMaxY > maxY) {
                    maxY = eMaxY;
                    extentsChanged = true;
                }
            }

            // If the range is locked and the plot object lies outside of the
            // plot range, then we ignore it - it can never be seen
            if (rangeLocked) {
                if (extents.width == 0 || extents.height == 0) {
                    // Special handing for zero size
                    if (!(extents.x >= minX) && (extents.x <= maxX)) {
                        return;
                    }
                    if (!(extents.y >= minY) && (extents.y <= maxY)) {
                        return;
                    }

                } else if (!extents.intersects(minX, minY, maxX - minX, maxY - minY)) {
                    // Reject this
                    return;
                }
            }

            // Add the plot object
            synchronized (ops) {
                ops.add(plotOp);
            }

            // If the extents changed, then tell whoever needs to know
            if (extentsChanged) {
                extentsChanged(plotOp);
            } else {
                // Otherwise we tell whoever that some but updated
                changed(plotOp);
            }
        }

        /**
         * Inform all interested parties that the content of the PlotDocument
         * changed
         * @param plotOp the Drawable (plot operation) which caused the change
         */
        private void changed(Drawable plotOp) {
            // Tell subscribed listeners
            for (Iterator<PlotDocumentListener> it = listeners.iterator(); it.hasNext();) {
                it.next().update(plotOp);
            }
        }

        /**
         * Inform all interested parties that the extents of the PlotDocument
         * changed
         * @param plotOp the Drawable (plot operation) which caused the change
         */
        private void extentsChanged(Drawable plotOp) {
            // Tell subscribed listeners
            Rectangle extents = getExtents();
            for (Iterator<PlotDocumentListener> it = listeners.iterator(); it.hasNext();) {
                it.next().updateWithExtentsChange(plotOp, extents);
            }
        }

    }

    /** 
     * The PlotPanel is a panel which can draw a PlotDocument
     *
     * <p>
     * Creation: Sep 15, 2002 at 6:39:34 PM
     */
    private class PlotPanel extends JPanel implements PlotDocumentListener {

        private static final long serialVersionUID = -1733420467952757267L;
        
        private static final int AXIS_TYPE_NONE = 0;
        private static final int AXIS_TYPE_AUTOXY = 1;

        private static final int GRID_TYPE_NONE = 0;
        private static final int GRID_TYPE_AUTOXY = 1;

        private PlotDocument plotDocument; // The document we are viewing
        private BufferedImage bi; // The image of the plot
        private Color backgroundColour = Color.white;
        private int axisType = AXIS_TYPE_AUTOXY;
        private int gridType = GRID_TYPE_AUTOXY;
        private int gridSize = 10;

        /**
         * Create a PlotPanel with an empty PlotDocument
         */
        public PlotPanel() {
            setPlotDocument(new PlotDocument());
            sizePanel();
        }

        /**
         * Create a PlotPanel with an empty PlotDocument which is range limited
         */
        public PlotPanel(Rectangle range, boolean rangeLocked) {
            setPlotDocument(new PlotDocument(range, rangeLocked));
            setSize(range.getSize());
            sizePanel();
        }

        /**
         * Create a PlotPanel with a given background colour
         */
        public PlotPanel(Color background) {
            this();
            setBackgroundColour(background);
        }

        /**
         * Create a PlotPanel from a given PlotDocument
         */
        public PlotPanel(PlotDocument document) {
            setPlotDocument(document);
        }

        /**
         * Create a PlotPanel from a given PlotDocument and a background colour
         */
        public PlotPanel(PlotDocument document, Color backgroundColour) {
            setBackgroundColour(backgroundColour);
            setPlotDocument(document);
        }

        /**
         * Configure this panel so that it is the correct size etc.
         * for the containing document
         * @param docExtents the document extents (when known)
         */
        private void sizePanel(Rectangle docExtents) {
            // Size the panel and revalidate (to get scroll bar updated)
            setPreferredSize(docExtents.getSize());
            revalidate(); //seems to do nothing

        }

        /**
         * Configure this panel so that it is the correct size etc.
         * for the containing document
         */
        private void sizePanel() {
            // Defer to more general routine
            sizePanel(plotDocument.getExtents());
        }

        /**
         * Set a Graphics2D coord space to map the document onto the context
         * origin (document paints at (0,0)).
         * This is achieved by setting an AffineTransform into the GC
         * Creation date: (11/6/00 3:44:40 PM)
         * @param g2c java.awt.Graphics2D the graphics context to map
         */
        private void setDocCoordTransform(Graphics2D g2c) {
            Point docOrigin = plotDocument.getOrigin();
            AffineTransform transform = g2c.getTransform();
            transform.setToTranslation(- (docOrigin.x), - (docOrigin.y));
            g2c.setTransform(transform);
        }

        /**
         * Draw a grid of the appropriate (set) type into this BufferedImage
         * @param bi the BufferedImage
         * @param extents the extents of the grid
         */
        private void drawGrid(BufferedImage bi, Rectangle extents) {
            // Get the GC
            Graphics2D g2c = (Graphics2D) bi.getGraphics();
            int height = bi.getHeight();
            int width = bi.getWidth();

            // Always draw a nice border around the edge
            g2c.setColor(Color.lightGray);
            g2c.drawRect(1, 1, width - 2, height - 2);

            // Depends on grid type
            switch (gridType) {
                case GRID_TYPE_AUTOXY :
                    // Get start point registered to document origin
                    Point startPoint = new Point((extents.x / gridSize) * gridSize, (extents.y / gridSize) * gridSize);
                    plotDocument.translatePoint(startPoint);

                    Point tensRegister = new Point(0, 0);
                    plotDocument.translatePoint(tensRegister);

                    // For verticals 
                    for (int i = startPoint.x; i < width; i += gridSize) {
                        if ((i - tensRegister.x) % (gridSize * 10) == 0) {
                            g2c.setColor(Color.gray);
                        } else {
                            g2c.setColor(Color.lightGray);
                        }
                        g2c.drawLine(i, 0, i, height);
                    }

                    // For horizontals
                    for (int i = startPoint.y; i < height; i += gridSize) {
                        if ((i - tensRegister.y) % (gridSize * 10) == 0) {
                            g2c.setColor(Color.gray);
                        } else {
                            g2c.setColor(Color.lightGray);
                        }
                        g2c.drawLine(0, i, width, i);
                    }

                    break;

                case GRID_TYPE_NONE :
                default :
                    break;
            }

            // Dispose of the GC
            g2c.dispose();
        }

        /**
         * Draw axes of the appropriate (set) type into this BufferedImage
         * @param bi the BufferedImage
         */
        private void drawAxes(BufferedImage bi) {
            // Nothing to do if no axes
            if (axisType == AXIS_TYPE_NONE) {
                return;
            }

            // Determine where the origin is
            Point origin = new Point(0, 0);
            plotDocument.translatePoint(origin);

            // Get the GC
            Graphics2D g2c = (Graphics2D) bi.getGraphics();
            int height = bi.getHeight();
            int width = bi.getWidth();

            switch (axisType) {
                case AXIS_TYPE_AUTOXY :
                    // Draw  axes
                    g2c.setColor(Color.black);

                    g2c.drawLine(0, origin.y, width, origin.y);
                    g2c.drawLine(origin.x, 0, origin.x, height);
                    break;

                case AXIS_TYPE_NONE :
                default :
                    break;
            }

            // Dispose of the GC
            g2c.dispose();
        }

        /**
         * Paint the given part of this MapPanel.
         * Creation date: (11/6/00 3:44:40 PM)
         * @param g java.awt.Graphics the paint info
         */
        public void paintComponent(java.awt.Graphics g) {
            super.paintComponent(g);
            Graphics2D g2c = (Graphics2D) g;
//            Dimension dim = getSize();
//            Insets insets = getInsets();

            // Paint in the image if we have one
            if (bi != null) {
                synchronized (bi) {
                    g2c.drawImage(bi, null, 0, 0);
                }
            }

            // Dispose of GC
            g2c.dispose();
        }

        /**
         * Returns the plotDocument.
         * @return PlotDocument the document this panel is viewing
         */
        public PlotDocument getPlotDocument() {
            return plotDocument;
        }

        /**
         * Sets the plotDocument.
         * @param plotDocument the new PlotDocument to view
         */
        public void setPlotDocument(PlotDocument plotDocument) {
            // Unregister as listener from any previous document
            if (this.plotDocument != null) {
                this.plotDocument.removeListener(this);
            }

            // Clear image info
            synchronized (this) {
                bi = null;
            }

            // Set and register as listener
            this.plotDocument = plotDocument;
            this.plotDocument.addListener(this);

            // Update
            updateSizeAndImage(this.plotDocument, this.plotDocument.getExtents());
        }

        /**
         * Update the buffered image with this plot object (paint into image)
         * @param plotObject the object to draw into the image
         */
        private void updateImage(Drawable plotObject) {
            // Get the extents of the object
            Rectangle bounds = plotObject.getExtents();

            // Minimum extents of 1x1
            if (bounds.width <= 0) {
                bounds.width = 1;
            }
            if (bounds.height <= 0) {
                bounds.height = 1;
            }

            // If there's no current image, create one
            if (bi == null) {
                // Create image
                synchronized (this) {
                    bi = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_INT_RGB);

                    // Get a graphics context and paint it the background colour
                    Graphics2D g2c = (Graphics2D) bi.getGraphics();
                    g2c.setColor(backgroundColour);
                    g2c.fillRect(0, 0, bounds.width, bounds.height);

                    // Draw its grid and axes
                    drawGrid(bi, bounds);
                    drawAxes(bi);

                    // Dispose of gc
                    g2c.dispose();
                }

                // Get the panel to resize to these extents
                sizePanel(bounds);
            }

            // Update the image
            synchronized (bi) {
                // Get a graphics context
                Graphics2D g2c = (Graphics2D) bi.getGraphics();

                // Set up the coord space correctly
                setDocCoordTransform(g2c);
                plotObject.draw(g2c);

                // Dispose of GC
                g2c.dispose();
            }

            // Schedule a repaint for the effected area
            plotDocument.translateRect(bounds);
            repaint(bounds.x, bounds.y, bounds.width, bounds.height);
        }
        
        /**
         * @see org.openquark.cal.samples.plotter.Plotter.PlotDocumentListener#update(Plotter.Drawable)
         */
        public void update(Drawable plotObject) {
            // The document has been updated without changing the document's
            // overall extents
            // The bounding rectangle defines where the change occured within 
            // the document
            updateImage(plotObject);
        }

        /**
         * Update the buffered image to take account of the new size
         * and this new plot object (paint into image)
         * @param plotObject the object to draw into the image
         * @param newExtents the new bounds of the document
         */
        private synchronized void updateSizeAndImage(Drawable plotObject, Rectangle newExtents) {
            // Get the extents of the object
            //Rectangle bounds = plotObject.getExtents();

            // Minimum extents of 1x1
            if (newExtents.width <= 0) {
                newExtents.width = 1;
            }
            if (newExtents.height <= 0) {
                newExtents.height = 1;
            }

            // Remember the old image    
            BufferedImage oldImage = bi;

            // Create new image
            bi = new BufferedImage(newExtents.width, newExtents.height, BufferedImage.TYPE_INT_RGB);

            // Get a graphics context and paint it the background colour
            Graphics2D g2c = (Graphics2D) bi.getGraphics();
            g2c.setColor(backgroundColour);
            g2c.fillRect(0, 0, newExtents.width, newExtents.height);

            // Draw its grid and axes
            drawGrid(bi, newExtents);
            drawAxes(bi);

            // If we had an old image, copy this in
            if (false && oldImage != null) {
                // TODO - copy old image
                // Use imageExtents (of the old image) to do this

                // Update with this drawable
                updateImage(plotObject);
            } else {
                // Draw everything back in
                synchronized (bi) {
                    // Set up the coord space correctly
                    setDocCoordTransform(g2c);
                    plotDocument.draw(g2c);
                }

                // Schedule repaint of everything
                repaint();
            }

            // Dispose of the image GC
            g2c.dispose();

            // Size the panel
            sizePanel(newExtents);

        }

        /**
         * @see org.openquark.cal.samples.plotter.Plotter.PlotDocumentListener#updateWithExtentsChange(Plotter.Drawable, Rectangle)
         */
        public void updateWithExtentsChange(Drawable plotObject, Rectangle newExtents) {
            // The document is indicating that it's overall size has changed
            // The size of this panel needs to changed accordingly and the 
            // draw buffer should be cleared
            updateSizeAndImage(plotObject, newExtents);
        }

        /**
         * Sets the backgroundColour.
         * @param backgroundColour The backgroundColor to set
         */
        public void setBackgroundColour(Color backgroundColour) {
            this.backgroundColour = backgroundColour;
        }
    }
}
