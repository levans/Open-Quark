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
 * TypeColourManager.java
 * Created: Nov 29, 2000
 * By: Luke Evans
 */

package org.openquark.gems.client;

import java.awt.Color;

import org.openquark.cal.compiler.TypeConsApp;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;


/**
 * The TypeColourManager keeps track on colour mappings to the unique types
 * which are currently in use on the TableTop.
 * The goal of the TypeColourManager is to distribute unique types 
 * uniformly across the colour space, so as to differentiate them.
 *
 * When types are looked up for which there is no current mapping, a new
 * colour mapping is created for the type.
 *
 * Creation date: (11/29/00 9:24:54 AM)
 * @author Luke Evans
 */
public class TypeColourManager implements TypeColourProvider {

    private static final float PHI = 0.618F;  // phi, the golden angle
    private static final float SAT_DELTA = 0.64F; // How much we move into the middle
    private final java.util.AbstractMap<String, TypeColour> colourMap;  // content addressible map
    private int count = 0; // Which colour to issue next

    /**
     * An array containing a bunch of fairly distinct colours.
     */
    private static final int[] COLOUR_ARRAY = {        
        0xFF0000,     // Red
        0x00FF00,     // Green
        0x0000FF,     // Blue
        0xFF00FF,     // Magenta
        0x00FFFF,     // Cyan
        0xFFFF00,     // Yellow
        0xFF7F00,     // Orange
        0xB5A642,     // Brass
        0x5F9F9F,     // Cadet Blue
        0xB87333,     // Copper
        0x38B0DE,     // Summer Sky
        0x9932CD,     // Dark Orchide
        0xDB9370,     // Tan
        0x855E42,    // Dark Wood
        0xEAADEA,    // Plum
        0x8E2323,    // Firebrick
        0xF5CCB0,    // Flesh
        0x238E23,    // Forest Green
        0xADEAEA,    // Turquoise
        0xDBDB70,    // Orchid
        0xC0C0C0,    // Grey
        0x527F76,    // Green Copper
        0x9F9F5F,    // Khaki
        0x8E236B,    // Maroon
        0xCC3299,    // Violet Red
        0xEBC79E,    // New Tan
        0xCFB53B,    // Old Gold
        0x70DB93,    // Aquamarine
        0x236B8E,    // Steel Blue
        0xD9D9F3,    // Quartz
        0x5959AB,    // Rich Blue
        0xE47833,    // Mandarin Orange
        0x238E68,    // Sea Green
        0x871F78,    // Dark Purple
        0x8E6B23,    // Sienna
        0xFF1CAE,    // Spicy Pink
    };

    /**
     * TypeColour is a single mapping from a type to a colour.
     * Creation date: (11/29/00 9:35:35 AM)
     * @author Luke Evans
     */
    static class TypeColour {
        final String typeString;
        final Color colour;

        /**
         * Construct TypeColour from a type only (colour defaults to black)
         * @param type the type expression describing the type
         */
        public TypeColour(TypeExpr type) {
            this(type, Color.black);
        }

        /**
         * Construct TypeColour from a type and a colour
         * @param type the type expression describing the type
         * @param colour the colour which represents the type
         */
        public TypeColour(TypeExpr type, Color colour) {
            this(type.toString(), colour);           
        }
        
        TypeColour(String typeString, Color colour) {
            this.typeString = typeString;
            this.colour = colour;
        }

        /**
         * Generate a hash code for this TypeColour
         * Creation date: (11/29/00 9:39:01 AM)
         * @return the hash code
         */
        @Override
        public int hashCode() {
            // The hash code of this TypeColour is the same of the hash code
            // for its name
            return typeString.hashCode();                
        }

        /**
         * Describe this TypeColour
         * Creation date: (11/29/00 7:07:44 PM)
         * @return the description of the TypeColour mapping
         */
        @Override
        public String toString() {
            return typeString + "->" + colour;
        }     
        
        /**
         * Test for equality of this TypeColour with another.
         * @param obj the other object 
         * @return boolean whether equivalent
         */
        @Override
        public boolean equals(Object obj) {
            // Must be type equivalent object and have the same type
            if (obj instanceof TypeColour) {
                if (((TypeColour)obj).typeString.equals(typeString)) {
                    // They are the same
                    return true;
                }    
            }
            // Not equal
            return false;    
        }     
    }    

    /**
     * Default constructor for the TypeColourManager.
     */
    public TypeColourManager() {
        super();
    
        // Create the actual colour map
        colourMap = new java.util.HashMap<String, TypeColour>();
    
        // Automatically assign colours to basic Types (this ensures consistent
        // colours for these types).
    
        addTypeString(CAL_Prelude.TypeConstructors.Boolean.getQualifiedName());
    
        addTypeString(CAL_Prelude.TypeConstructors.Char.getQualifiedName());
    
        addTypeString(CAL_Prelude.TypeConstructors.Int.getQualifiedName());
    
        addTypeString(CAL_Prelude.TypeConstructors.Double.getQualifiedName());
        
        addType(TypeExpr.makeParametricType());
    
        addTypeString("[" + CAL_Prelude.TypeConstructors.Boolean.getQualifiedName() + "]");    
        
        addTypeString(CAL_Prelude.TypeConstructors.String.getQualifiedName());

        addTypeString("[" + CAL_Prelude.TypeConstructors.Char.getQualifiedName() + "]");
    
        addTypeString("[" + CAL_Prelude.TypeConstructors.Int.getQualifiedName() + "]");
    
        addTypeString("[" + CAL_Prelude.TypeConstructors.Double.getQualifiedName() + "]");
    
        addTypeString("[a]");
    
        addTypeString("[" + CAL_Prelude.TypeConstructors.String.getQualifiedName() + "]"); //lists of strings
        addTypeString("[" + CAL_Prelude.TypeConstructors.Char.getQualifiedName() + "]"); //lists of strings
    
        addType(TypeExpr.makeTupleType(2)); // (a, b)
    
        addType(TypeExpr.makeTupleType(3)); // (a, b, c)
    
        //todoBI fix this up later.
        //addType (TypeChecker.createNumTypeVar()); // Num a => a
    }

    /**
     * Add a given type to the TypeColourManager.
     * Note that types are added as needed to the TypeColourManager as their colours are requested in getTypeColour().
     * One reason to use this method is to ensure that types are added to the manager in a certain order.
     * @param type the type to add
     */
    private void addType(TypeExpr type) {
        
        if (type == null) {
            throw new NullPointerException();
        }        
            
        // Check if this type is already present, otherwise add it
        if (!colourMap.containsKey(type.toString())) {
    
            // If it wasn't present, it will need to be allocated a new colour
            TypeColour newTC = new TypeColour(type, getNextColour());
            colourMap.put(type.toString(), newTC);        
        }
             
        //add argument types of type constructors
        TypeConsApp typeConsApp = type.rootTypeConsApp();        
        if (typeConsApp != null) {                                  
            for (int i = 0, nArgs = typeConsApp.getNArgs(); i < nArgs; ++i) {
                addType(typeConsApp.getArg(i));   
            }           
        }
        
    }
    
    /**
     * Adds a given type to the TypeColourManager. Unlike addType, this does not add type arguments, since it
     * doesn't inspect the type. It is primarily intended for initialization of this class.
     * @param typeString
     */
    private void addTypeString(String typeString) {
        
        if (typeString == null) {
            throw new NullPointerException();
        }        
            
        // Check if this type is already present, otherwise add it
        if (!colourMap.containsKey(typeString)) {
    
            // If it wasn't present, it will need to be allocated a new colour
            TypeColour newTC = new TypeColour(typeString, getNextColour());
            colourMap.put(typeString, newTC);        
        }                   
    }    

    /**
     * Return the next colour to use for a type.
     * @return Color the colour to use
     */
    private Color getNextColour() {    
    
        // Note: The first batch of colours are pre-determined in the COLOUR_ARRAY and are fairly distinguishable from each other.
        // After that, use the algorithm.
        
        if (count >= COLOUR_ARRAY.length) {
    
            /*
                We use an interesting algorithm to define what the next colour is to
                use:
                Phi (the golden angle of 137.5 degrees, or 0.618 of a turn) is used
                to spiral around the HSV colour circle to generate different colours.
                The Hue is canonically attributed to the angle of the polar coordinate
                of the colour, and the length of the coordinate is the saturation.
                The value is always 100%
            */    
        
            // Calculate the hue and saturation
            float hue = PHI * count;
            float satFalloff = count * SAT_DELTA - (int) (count * SAT_DELTA); 
            float sat = 1.0F - satFalloff;
    
            // Increment the colour count and return the new colour
            count++;
            return Color.getHSBColor(hue, sat, 1.0F);
    
        }
    
        int colourCode = COLOUR_ARRAY[count];
    
        count++;
        
        return new Color(colourCode);
    
    }

    /**
     * Obtain a colour which can represent this type uniquely
     * @param type the type expression to find a colour for
     * @return Color the colour to use for this type, or black if the type is null.
     */
    public Color getTypeColour(TypeExpr type) {
    
        if (type == null) {
            // bound inputs and outputs do not have an associated type
            return Color.black;
        }

        // add to the colour mapping if it's not already there        
        addType(type);

        // Get the matching TypeColour
        TypeColour thisTC = colourMap.get(type.toString());    
        
        // Return the mapped colour or black if it's all gone horribly wrong!
        return (thisTC != null)? thisTC.colour : Color.black;
    }
}

