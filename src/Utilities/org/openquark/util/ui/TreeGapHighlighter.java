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
 * TreeGapHighlighter.java
 * Created: 26-Mar-2003
 * By: Rick Cameron
 */

package org.openquark.util.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.SystemColor;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

public class TreeGapHighlighter extends TreeHighlighter {
    
    public final static class HitTestInfo {
        public final int row;
        public final boolean upperHalf;
        public final boolean inIcon;
        
        public HitTestInfo (int row, boolean upperHalf, boolean inIcon) {
            this.row = row;
            this.upperHalf = upperHalf;
            this.inIcon = inIcon;
        }
        
        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj) {
            return obj instanceof HitTestInfo 
                && ((HitTestInfo)obj).row == row
                && ((HitTestInfo)obj).upperHalf == upperHalf
                && ((HitTestInfo)obj).inIcon == inIcon;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            return row * 4 + (upperHalf ? 2 : 0) + (inIcon ? 1 : 0);
        }

        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "TreeHighlighter.HitTestInfo (" + row + ", " + upperHalf + ", " + inIcon + ")";  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$//$NON-NLS-4$
        }

    }
    
    private static final int blebWidth = 4;
    private static final int blebHeight = 3;
    private static final int iconWidth = 16;
    
    private HitTestInfo curHitTestInfo = null;

    /**
     * Constructor TreeGapHighlighter
     * 
     * @param tree
     */
    public TreeGapHighlighter(HighlightTree tree) {
        super(tree);
    }

    /**
     * @see org.openquark.util.ui.TreeHighlighter#showHighlight(java.awt.Point)
     */
    @Override
    void showHighlight(final Point point) {
        TreePath path = getTree().getClosestPathForLocation(point.x, point.y);

        if (path != null) {
            int childCount =
                ((TreeNode)path.getLastPathComponent()).getChildCount();

            if (childCount > 0 && getTree().isCollapsed(path)) {
                eraseHighlight();

                // let the next call really show the highlight...
                return;
            }

            HitTestInfo hitTestInfo = hitTest(point);

            if (!hitTestInfo.equals(curHitTestInfo)) {
                eraseHighlight();

                drawHighlight(hitTestInfo);

                curHitTestInfo = hitTestInfo;
            }
        }
    }

    /**
     * @see org.openquark.util.ui.TreeHighlighter#eraseHighlight()
     */
    @Override
    void eraseHighlight() {
        if (curHitTestInfo != null) {
            drawHighlight(curHitTestInfo);
        }
        
        curHitTestInfo = null;
    }

    /**
     * @see org.openquark.util.ui.TreeHighlighter#restore()
     */
    @Override
    void restore() {
        eraseHighlight();
    }
    
    /**
     * Method hitTest
     *
     * hitTest finds the row that is nearest to the given point. It returns
     * a HitTestInfo that tells which row was hit, whether the point was in 
     * the upper or lower half of the row, and whether it was over the icon
     * 
     * @param point
     * @return a HitTestInfo describing where the point is, 
     *         or null if it's not over a row
     */
    public static HitTestInfo hitTest (JTree tree, Point point) {
        TreePath path = tree.getClosestPathForLocation(point.x, point.y);
        
        if (path != null) {
            int row = tree.getRowForPath(path);
            
            Rectangle pathBounds = tree.getPathBounds(path);
            
            boolean upperHalf = point.y < pathBounds.y + pathBounds.height / 2,
                    inIcon    = point.x >= pathBounds.x && point.x < pathBounds.x + iconWidth;
                
            return new HitTestInfo (row, upperHalf, inIcon);
        } else {
            return null;
        }
    }
    
    private HitTestInfo hitTest (Point point) {
        return hitTest(getTree (), point);
    }
    
    private void drawHighlight (final HitTestInfo hitTestInfo) {

        // Perform on the swing thread, since some highlighters update by changing the tree model.
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                // Attempt to get the graphics for the tree.
                // Since this is performed later on the swing thread, the tree may
                // not be displayable anymore, in which case we don't need to
                // draw the highlight.
                Graphics2D g2 = (Graphics2D)getTree ().getGraphics();
                if (g2 == null) {
                    return;
                }
                
                int x = 0, y = 0, width = 0;
                
                Rectangle treeBounds = getTree ().getBounds();
                
                if (hitTestInfo.row >= getTree().getRowCount()) {
                    return;
                }
                
                Rectangle rowRect = getTree().getRowBounds(hitTestInfo.row);
                
                if (hitTestInfo.inIcon && canDropOnIcon (hitTestInfo.row)) {
                    g2.setXORMode(SystemColor.textHighlight);
                    g2.setColor(Color.WHITE);
                    
                    g2.fillRect(rowRect.x, rowRect.y, iconWidth, rowRect.height);
                    
                    g2.dispose();
                } else {        
                    if (getTree().isRootVisible() && hitTestInfo.row == 0) {
                        // on root node - put highlight under the node & shifted to the right             
                        x = rowRect.x + iconWidth;
                        y = rowRect.y + rowRect.height;
                    } else {
                        x = Math.max(rowRect.x - blebWidth, treeBounds.x);
                        y = hitTestInfo.upperHalf ? rowRect.y 
                                                  : rowRect.y + rowRect.height;
                    }
                    
                    width = treeBounds.width - (x - treeBounds.x);
                    
                    g2.setXORMode(SystemColor.textHighlight);
                    g2.setColor(Color.WHITE);
                    
                    // an additional bleb at the front
                    Polygon poly = new Polygon();
                    poly.addPoint(x, y - blebHeight);
                    poly.addPoint(x, y + blebHeight);
                    poly.addPoint(x + blebWidth, y + 1);
                    poly.addPoint(x + width, y + 1);
                    poly.addPoint(x + width, y - 1);
                    poly.addPoint(x + blebWidth, y - 1);
                    poly.addPoint(x, y - blebHeight);
            
                    g2.fill (poly);                
                    
                    g2.dispose();
                }
            }
        });
    }

    /**
     * Method canDropOnIcon
     * 
     * @param row
     * @return true iff the host allows dropping on the icon of the given row 
     */
    private boolean canDropOnIcon(int row) {
        return getTree() != null && getTree().canDropOnIcon(row);
    }
}
