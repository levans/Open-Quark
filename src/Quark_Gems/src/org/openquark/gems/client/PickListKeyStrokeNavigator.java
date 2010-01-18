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
 * PickListKeyStrokeNavigator.java
 * Creation date: (11/09/2001 2:59:39 PM)
 * By: Edward Lam
 */
package org.openquark.gems.client;

/**
 * Handler for KeyStroke events in a pick list that allows navigation by typing in the first few letters of the
 * list item that you want.
 * Creation date: (11/09/2001 2:59:39 PM)
 * @author Edward Lam
 */
public abstract class PickListKeyStrokeNavigator implements java.awt.KeyEventDispatcher{

    /** StringBuilder to hold the typed-in text */
    private final StringBuilder typeText = new StringBuilder();

    /** Timer to take care of resetting the typed-in text after a certain amount of time */
    private final javax.swing.Timer typeTextResetTimer;

    /** The amount of time (in ms) before the typed-in text is reset */
    private static final int resetTime = 1100;
    
    /** The component that this listener is navigating. */
    private final java.awt.Component navigableComponent;

    /**
     * Handles resetting the text which was typed in after a certain interval.
     * Creation date: (11/09/2001 12:18:58 PM)
     * @author Edward Lam
     */
    private class TypeTextHandler implements java.awt.event.ActionListener {
        /**
         * Invoked when an action occurs.
         * Creation date: (11/09/2001 12:18:58 PM)
         * @param e java.awt.event.ActionEvent the relevant key event to handle.
         */
        public void actionPerformed(java.awt.event.ActionEvent e) {
            resetSearchInfo();
        }
    }

    /**
     * Constructor for a PickListKeyStrokeNavigator.
     * Creation date: (11/09/2001 11:45:41 AM)
     * @param component java.awt.Component - the component that this listener is navigating.
     */
    public PickListKeyStrokeNavigator(java.awt.Component component){

        if (component == null) {
            throw new IllegalArgumentException("Value of argument 'component' cannot be null");
        }
        
        navigableComponent = component;
        
        // initialize the type text reset timer
        typeTextResetTimer = new javax.swing.Timer(resetTime, new TypeTextHandler());
        typeTextResetTimer.setRepeats(false);
    }
    /**
     * Get the String text displayed in the nth row of the pick list.
     * @param index the index of the string to return
     * @return the text displayed in the nth row
     */
    public abstract String getNthRowString(int index);
    
    /**
     * Get the number of rows in the pick list.
     * Creation date: (11/09/2001 3:03:52 PM)
     * @return int the number of rows in the pick list
     */
    public abstract int getNumRows();
    
    /**
     * Get the index of the row in the pick list which is currently selected.
     * Creation date: (11/09/2001 3:03:52 PM)
     * @return int the index of the currently selected row
     */
    public abstract int getSelectedRowIndex();
    
    /**
     * Invoked when a key has been pressed.
     * Creation date: (11/09/2001 12:35:40 PM)
     * @param e java.awt.event.KeyEvent the relevant key event to handle.
     */
    public void keyPressed(java.awt.event.KeyEvent e) {

        // reset the typeTextTimer if it's running
        if (typeTextResetTimer.isRunning()){
            typeTextResetTimer.restart();
        }
        
        // reset the search buffer and timer if it's some sort of control character
        char charPressed = e.getKeyChar();
        if (e.isActionKey() || 
            (charPressed != java.awt.event.KeyEvent.CHAR_UNDEFINED && Character.isISOControl(charPressed))) {

            resetSearchInfo();
        }
    }
    
    /**
     * Invoked when a key has been typed. This event occurs when a key press is followed by a key release.
     * Right now what we do is zip to the row which begins with the text we type.
     * Creation date: (10/09/2001 5:55:01 PM)
     * @param e java.awt.event.KeyEvent the relevant key event to handle.
     */
    public void keyTyped(java.awt.event.KeyEvent e) {
        // get the character that was typed, in lower case
        char keyTyped = Character.toLowerCase(e.getKeyChar());

        // reset the typetext timer
        typeTextResetTimer.restart();

        // ignore if it's a control character.
        if (Character.isISOControl(keyTyped)) {
            return;
        }

        // if the current search string is one character, and the typed in character is the same, 
        //   just look for the next instance of a row string beginning with that character.
        //   We can do this by just retaining the one character.
        // otherwise add the character to the text that's typed in so far
        if (!(typeText.length() == 1 && typeText.charAt(0) == keyTyped)){
            typeText.append(keyTyped);
        }

        // the starting row is the most recently selected row, or the next row if there is only one character.
        // this also takes care of the case where nothing is selected, since -1 is returned if the leadPath is null
        int startRow = getSelectedRowIndex();
        if (typeText.length() < 2) {
            startRow++;
        }

        // Convert the search text to a string.
        String searchString = typeText.toString();

        // Go through the rows, looking for a match.
        int numRows = getNumRows();
        int rowFound = -1;
        for (int i = 0; i < numRows; i++) {

            // calculate the row to search
            int searchRow = (i + startRow) % numRows;

            // Get the text for that row, in lower case
            String rowString = getNthRowString(searchRow).toLowerCase();

            // Is it a match?
            if (rowString.startsWith(searchString)) {
                rowFound = searchRow;
                break;
            }
        }

        // If we found a match, select it and make sure it's visible
        if (rowFound > -1) {
            selectRow(rowFound);
        }
    }
    
    /**
     * Move the selection in the pick list to a given row.
     * Creation date: (11/09/2001 3:03:52 PM)
     * @param index int the index of the row to set selected
     */
    public abstract void selectRow(int index);
    
    /**
     * This function is called whenever a key event is fired.
     * Creation date: (07/08/2002 4:22:00 PM).
     * @param evt java.awt.event.KeyEvent - the key event that was fired.
     * @return boolean - true if the key event was handled and false otherwise.
     */
    public boolean dispatchKeyEvent(java.awt.event.KeyEvent evt) {
        
        // Make sure the navigable component has focus and is enabled so we don't end up stealing
        // key events intended for other components.
        if (navigableComponent.isFocusOwner() && navigableComponent.isEnabled()) {

            // We only care about key pressed or key typed events.
            if (evt.getID() == java.awt.event.KeyEvent.KEY_TYPED) {
                keyTyped(evt);
                // Return true so that no other listeners get this event.
                return true;
                
            } else if (evt.getID() == java.awt.event.KeyEvent.KEY_PRESSED){
                keyPressed(evt);
                // Return false so that other listeners and key bindings get this event.
                return false;
            }
        }
        return false;
    }

    /**
     * Prepare the navigator for a new search.
     * Clear the buffer and stop the reset timer.
     */
    private void resetSearchInfo() {
        typeText.setLength(0);
        typeTextResetTimer.stop();
    }

}
