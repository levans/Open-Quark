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
 * PreferencesHelper.java
 * Creation date: Dec 11, 2003
 * By: Frank Worsley
 */
package org.openquark.gems.client.utilities;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.util.prefs.Preferences;

/**
 * A class that providers helper methods for storing common preferences.
 * @author Frank Worsley
 */
public class PreferencesHelper {

    /**
     * Saves size/location information for the given frame.
     * @param prefs the preferences to save to
     * @param key the preference name
     * @param frame the frame to save information for
     */
    public static void putFrameProperties(Preferences prefs, String key, Frame frame) {

        Dimension size = frame.getSize();
        Point location = frame.getLocation();
        boolean maximized = (frame.getExtendedState() & Frame.MAXIMIZED_BOTH) != 0;
        
        putSizeLocation(prefs, key, size, location, maximized);
    }
    
    /**
     * Loads size/location information and applies it to the given frame. 
     * @param prefs the preferences to load from
     * @param key the name of the preference
     * @param frame the frame to load the information for
     * @param size the default size
     * @param location the default location
     */
    public static void getFrameProperties(Preferences prefs, String key, Frame frame, Dimension size, Point location) {

        Dimension defaultSize = new Dimension(size);
        Point defaultLocation = new Point(location);
        
        Dimension sizeCopy = new Dimension(size);
        Point locationCopy = new Point(location);
        
        boolean maximized = getSizeLocation(prefs, key, sizeCopy, locationCopy);

        // If the frame is maximized set the size and location to defaults.
        // This is done so that if the user unmaximizes the frame, its size will actually change.
        if (maximized) {
            frame.setSize(defaultSize);
            frame.setLocation(defaultLocation);
            frame.setExtendedState(frame.getExtendedState() | Frame.MAXIMIZED_BOTH);
            
        } else {
            frame.setSize(sizeCopy);
            frame.setLocation(locationCopy);
        }
    }
    
    /**
     * Saves size/location information for the given dialog.
     * @param prefs the preferences to save to
     * @param key the preference name
     * @param dialog the dialog to save information for
     */
    public static void putDialogProperties(Preferences prefs, String key, Dialog dialog) {
        Dimension size = dialog.getSize();
        Point location = dialog.getLocation();
        putSizeLocation(prefs, key, size, location, false);
    }
    
    /**
     * Loads size/location information and applies it to the given dialog. 
     * @param prefs the preferences to load from
     * @param key the name of the preference
     * @param dialog the dialog to load the information for
     * @param size the default size
     * @param location the default location
     */
    public static void getDialogProperties(Preferences prefs, String key, Dialog dialog, Dimension size, Point location) {
        
        Dimension defaultSize = new Dimension(size);
        Point defaultLocation = new Point(location);
        
        getSizeLocation(prefs, key, defaultSize, defaultLocation);
        dialog.setSize(defaultSize);
        dialog.setLocation(defaultLocation);
    }
    
    /**
     * Saves the given size/location information to a preference.
     * @param prefs the preferences to save to
     * @param key the name of the preference
     * @param size the size to save
     * @param location the location to save
     * @param maximized whether the window is maximized
     */
    private static void putSizeLocation(Preferences prefs, String key, Dimension size, Point location, boolean maximized) {
        String value = size.width + "," + size.height + "," + location.x + "," + location.y + "," + (maximized ? "1" : "0");
        prefs.put(key, value);        
    }
    
    /**
     * Reads a preference that contains size/location information and stores the info in the given objects.
     * @param prefs the preferences to load from
     * @param key the key name of the preference
     * @param size the Dimension that provides the default size and will store the loaded size
     * @param location the Point that provides the default location and will store the loaded location
     * @return whether the window is maximized
     */
    private static boolean getSizeLocation(Preferences prefs, String key, Dimension size, Point location) {
        Dimension originalSize = size.getSize();
        Point originalLocation = location.getLocation();
        
        String def = size.width + "," + size.height + "," + location.x + "," + location.y + ",0";
        String value = prefs.get(key, def);
        
        String[] values = value.split(",");
        
        boolean maximized = false;
        
        try {
            size.width = Integer.parseInt(values[0]);
            size.height = Integer.parseInt(values[1]);
            location.x = Integer.parseInt(values[2]);
            location.y = Integer.parseInt(values[3]);
            maximized = values[4].equals("1");
        } catch (Exception ex) {
            // There was a problem parsing the preferences.
            size.setSize(originalSize);
            location.setLocation(originalLocation);
        }
        
        return maximized;
    }
}
