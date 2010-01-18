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
 * OrientationType.java
 * Created: Oct 1, 2004
 * By: Richard Webster
 */
package org.openquark.util;


/**
 * Constants for specifying an orientation (horizontal/vertical).
 * @author Richard Webster
 */
public final class OrientationType {
    public static final int _default = 0;
    public static final int _horizontal = 1;
    public static final int _vertical = 2;
    public static final int _reverse_horizontal = 3;
    public static final int _reverse_vertical = 4;

    public static final OrientationType horizontal = new OrientationType (_horizontal);
    public static final OrientationType vertical = new OrientationType (_vertical);
    public static final OrientationType reverse_horizontal = new OrientationType (_reverse_horizontal);
    public static final OrientationType reverse_vertical = new OrientationType (_reverse_vertical);

    /** The integer value for the enum value. */
    private final int intVal;

    /**
     * Constructor for OrientationType.
     * @param intVal  integer representation of the enum.  Must be a constant defined by this class.
     */
    private OrientationType(int intVal) {
        this.intVal = intVal;
    }

    /**
     * Returns the enum value corresponding to the int value.
     * @param i             an int value
     * @param defaultValue  the value to be returned for a default or unknown orientation
     * @return the enum value corresponding to the int value
     */
    public static OrientationType fromInt(int i, OrientationType defaultValue) {
        switch (i) {
            case _horizontal :          return horizontal;
            case _vertical :            return vertical;
            case _reverse_horizontal :  return reverse_horizontal;
            case _reverse_vertical :    return reverse_vertical;
            case _default :
            default :
                return defaultValue;
        }
    }

    /**
     * Returns the int value for this enum.
     * @return the int value for this enum
     */
    public int value () {
        return intVal;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString () {
        switch (intVal) {
            case _horizontal :          return "horizontal"; //$NON-NLS-1$
            case _vertical :            return "vertical"; //$NON-NLS-1$
            case _reverse_horizontal :  return "reverse_horizontal"; //$NON-NLS-1$
            case _reverse_vertical :    return "reverse_vertical"; //$NON-NLS-1$
            default :
                assert (false); // this should never happen
                return ""; //$NON-NLS-1$
        }
    }

    /**
     * Returns whether this is a horizontal (or reverse horizontal) orientation.
     * @return True if the orientation is horizontal or reverse horizontal
     */
    public boolean isHorizontal() {
        switch (intVal) {
        case _horizontal :
        case _reverse_horizontal :
            return true;
        default :
            return false;
        }
    }

    /**
     * Returns whether this a vertical (or reverse vertical) orientation.
     * @return True if the orientation is vertical or reverse vertical
     */
    public boolean isVertical() {
        switch (intVal) {
        case _vertical :
        case _reverse_vertical :
            return true;
        default :
            return false;
        }
    }

    /**
     * Returns whether this is a 'reverse' orientation.
     * @return True if the orientation is reverse horizontal or reverse vertical
     */
    public boolean isReversed() {
        switch (intVal) {
        case _reverse_horizontal :
        case _reverse_vertical :
            return true;
        default :
            return false;
        }
    }
}
