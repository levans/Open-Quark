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
 * GaussianKernel.java
 * Creation date: Dec 2, 2003
 * By: Frank Worsley
 */
package org.openquark.gems.client.internal.effects;

import java.awt.image.Kernel;

/**
 * A kernel that implements a Gaussian blur. To be used with the ConvolveOp class in the AWT.
 * @author Frank Worsley
 */
public class GaussianKernel extends Kernel {

    /**
     * Constructor for a new GaussianBlurKernel.
     * @param radius the radius of the blur
     */    
    public GaussianKernel(int radius) {
        super(2 * radius + 1, 2 * radius + 1, getKernel(radius));
    }
    
    /**
     * @return the kernel for a gaussian blur with the given radiu
     */
    private static float[] getKernel(int radius) {
        
        // Formula for a gaussian blur:
        //
        // v = e ^ ( -(x*x + y*y) / (2 * sd * sd) )
        //
        // where sd is the standard deviation
        
        if (radius <= 0) {
            throw new IllegalArgumentException("invalid radius");
        }

        int size = 2 * radius + 1;
        float kernel[] = new float[size * size];
        double sum = 0.0;

        double deviation = radius / 3.0;
        double devSqr2 = 2 * Math.pow(deviation, 2);

        for (int y = 0; y < size; y++) {
            
            for(int x = 0; x < size; x++) {
                
                int index = x * size + y;
                int p1 = (x - radius) * (x - radius);
                int p2 = (y - radius) * (y - radius);
                
                kernel[index] = (float) Math.pow(Math.E, -(p1 + p2) / devSqr2);
                
                sum += kernel[index];               
            }
        }

        for (int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                kernel[i * size + j] /= sum;
            }
        }

        return kernel;
    }
}
