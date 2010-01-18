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
 * DisplayConstants.java
 * Creation date: (03/04/02 7:27:00 PM)
 * By: Edward Lam
 */
package org.openquark.gems.client;

import java.awt.Color;

import org.openquark.gems.client.valueentry.ValueEntryPanel;


/**
 * Some display constants.
 * @author Edward Lam
 */
public final class DisplayConstants {
    
    // Prevent this class from being instantiated.
    private DisplayConstants() {}

    // Drag connection constants
    public static final int REVERSE_CONNECTION_HOOK_SIZE = 20;
    public static final int HOOK_STEP_SIZE = 5;

    public static final int LET_LABEL_MARGIN = 6;           // Spare space either side of the name - let gem
    public static final int INPUT_OUTPUT_LABEL_MARGIN = 5;  // Spare space either side of the name - input/output gem
    public static final int BEVEL_WIDTH_X = 6;              // The horizontal thickness of the vertical bevel.
    public static final int BEVEL_WIDTH_Y = 8;              // The vertical thickness of the horizontal bevel.
    public static final int DIAGONAL_BEVEL_SIZE = 5;        // The size of the diagonal bevels in functional gems

    /** Makes it easier for users to click on parts.*/
    public static final int DITHER_FACTOR = 10;
    
    public static final int HALO_SIZE = 6;                  // The size of the selection halo   
    public static final int HALO_BLUR_SIZE = 7;             // How much blur we have
    public static final int HALO_BEVEL_SIZE = 3;            // Selection halo
    public static final int OUT_ARROW_SIZE = 4;             // The size of the output value arrow
    public static final int OUTPUT_AREA_WIDTH = 10;         // Space for outputs on RHS
    public static final int INPUT_AREA_WIDTH = 10;          // Space for inputs on LHS
    public static final int ARGUMENT_SPACING = ValueEntryPanel.PANEL_HEIGHT; // Space between argument inputs
    public static final int BIND_POINT_RADIUS = 3;          // The radius of the argument blobs

    // Layout constants
    public static final int CRACK_POINTS_PER_QTR = 6;       // 'Crackiness' factor in broken gems

    // drag/drop constants - from TableTop
    public static final int MULTI_DROP_OFFSET = 10;
    
    // size for text outline
    public static final int OUTLINE_SIZE = 2;
    
    /** The width of the target rings */
    public static final int TARGET_RING_WIDTH = 5;
    
    // Some colours.
    public static final int GEM_TRANSPARENCY = 120;  // out of 255
    public static final int GEM_FACET_TRANSPARENCY = 200;  // out of 255
    public static final Color TRANSPARENT_WHITE = GemCutterPaintHelper.getAlphaColour(Color.WHITE, GEM_TRANSPARENCY);
    public static final Color LABEL_COLOUR = new Color(255, 253, 247);  // A nice light cream!
    public static final Color RUN_HALO_COLOUR = new Color(70, 70, 255);
}
