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
 * ValueFormat.java
 * Creation date: (1/11/01 9:03:32 AM)
 * By: Luke Evans
 */
package org.openquark.gems.client.valueentry;

import java.awt.Toolkit;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.Date;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import org.openquark.cal.compiler.PreludeTypeConstants;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.module.Cal.Utilities.CAL_RelativeTime;
import org.openquark.cal.module.Cal.Utilities.CAL_Time;
import org.openquark.cal.valuenode.JTimeValueNode;
import org.openquark.cal.valuenode.ListOfCharValueNode;
import org.openquark.cal.valuenode.LiteralValueNode;
import org.openquark.cal.valuenode.RelativeDateTimeValueNode;
import org.openquark.cal.valuenode.RelativeDateValueNode;
import org.openquark.cal.valuenode.RelativeTemporalValueNode;
import org.openquark.cal.valuenode.RelativeTimeValueNode;
import org.openquark.util.time.Time;

import com.ibm.icu.text.DateFormat;
import com.ibm.icu.util.TimeZone;

/**
 * A document type accepting only input valid for various types of CAL constant
 * (e.g. Double, String, List(of Double, String)...).
 * Creation date: (1/11/01 9:03:32 AM)
 * @author Luke Evans
 */
class ValueFormat extends PlainDocument {

    private static final long serialVersionUID = 8382900701900352819L;

    // A flag to denote whether or not 'checking' is in effect.
    // If checking is not in effect, make sure you do not write
    // invalid gibberish into this document.
    private boolean isChecking;

    /** The ValueEntryPanel which uses this ValueFormat. */
    private final ValueEntryPanel valueEntryPanel;

    /**
     * ValueFormat constructor.
     * @param valueEntryPanel The ValueEntryPanel which uses this ValueFormat.
     */
    public ValueFormat(ValueEntryPanel valueEntryPanel) {
        this.isChecking = false;
        this.valueEntryPanel = valueEntryPanel;
    }
    
    /**
     * Get a value which is either a default value or a fixed up value.
     * If no change occurred (original value was okay), then returns a null.
     * Creation date: (1/12/2001 10:39:02 PM)
     * @return String 
     */
    public String getValidOrDefault() {

        String currentText;
        boolean changed = false;
        int len;

        try {

            currentText = getText(0, getLength());

        } catch (BadLocationException e) {

            currentText = "";
        }

        TypeExpr typeExpr = valueEntryPanel.getValueNode().getTypeExpr();
        
        PreludeTypeConstants typeConstants = valueEntryPanel.valueEditorManager.getPreludeTypeConstants();
       
        if (typeExpr.sameType(typeConstants.getDoubleType()) ||
            typeExpr.sameType(typeConstants.getFloatType())) {
                
            // Remove trailing -
            len = currentText.length();
            if (len > 0) {
                char lastChar = currentText.charAt(len - 1);
                if (lastChar == '-') {
                    currentText = currentText.substring(0, len - 1);
                    len--;
                    changed = true;
                }
            }

            // Check the unique case where the user types in a '.' first
            // Then we want to show '0.' instead
            if (currentText.indexOf(".") == 0) {
                currentText = "0" + currentText;
                len++;
                changed = true;
            }

            // Remove trailing 'e' or 'E'
            if (len > 0) {
                char lastChar = currentText.charAt(len - 1);
                if (lastChar == 'e' || lastChar == 'E') {
                    currentText = currentText.substring(0, len - 1);
                    len--;
                    changed = true;
                }
            }

            // If this is empty, set the text to zero
            if (len == 0) {

                currentText = "0.0";
                len = 3;
                changed = true;

            } else {

                // If the value is a perfect whole number, then want the ending ".0".
                int indexOfPeriod = currentText.indexOf(".");

                if (indexOfPeriod == -1) {
                    
                    // Check for an 'e' within the value, or if the value doesn't end with a digit (eg. "Infinity").
                    int indexOfE = Math.max(currentText.indexOf('e'), currentText.indexOf('E'));
                    boolean endsWithDigit = Character.isDigit(currentText.charAt(len - 1));

                    if (indexOfE < 0 && endsWithDigit) {

                        currentText = currentText + ".0";
                        len += 2;
                        changed = true;
                    }

                } else if (indexOfPeriod == (currentText.length() - 1)) {

                    currentText = currentText + "0";
                    len++;
                    changed = true;
                }
            }

        } else if (typeExpr.sameType(typeConstants.getDecimalType())) {
                
            // Remove trailing -
            len = currentText.length();
            if (len > 0) {
                char lastChar = currentText.charAt(len - 1);
                if (lastChar == '-') {
                    currentText = currentText.substring(0, len - 1);
                    len--;
                    changed = true;
                }
            }

            // Check the unique case where the user types in a '.' first
            // Then we want to show '0.' instead
            if (currentText.indexOf(".") == 0) {
                currentText = "0" + currentText;
                len++;
                changed = true;
            }

            // Remove trailing 'e' or 'E'
            if (len > 0) {
                char lastChar = currentText.charAt(len - 1);
                if (lastChar == 'e' || lastChar == 'E') {
                    currentText = currentText.substring(0, len - 1);
                    len--;
                    changed = true;
                }
            }

            // If this is empty, set the text to zero
            if (len == 0) {

                currentText = "0";
                len = 1;
                changed = true;

            } 
            
        } else if (typeExpr.sameType(typeConstants.getIntType()) ||
                   typeExpr.sameType(typeConstants.getIntegerType()) ||
                   typeExpr.sameType(typeConstants.getByteType()) ||
                   typeExpr.sameType(typeConstants.getShortType()) ||
                   typeExpr.sameType(typeConstants.getLongType())) {

            // Remove trailing -
            len = currentText.length();
            if (len > 0) {
                char lastChar = currentText.charAt(len - 1);
                if (lastChar == '-') {
                    currentText = currentText.substring(0, len - 1);
                    len--;
                    changed = true;
                }
            }

            // If this is empty, set the text to zero
            if (len == 0) {
                currentText = "0";
                len = 1;
                changed = true;
            }

        } else if (typeExpr.sameType(typeConstants.getCharType())) {

            len = currentText.length();
            if (len != 1) {
                // Default value is a space.
                currentText = " ";
                changed = true;
            }

        } else if (typeExpr.isNonParametricType(CAL_RelativeTime.TypeConstructors.RelativeDate)) {

            RelativeDateValueNode dateValueNode = (RelativeDateValueNode) valueEntryPanel.getValueNode();

            // Check.  If it's parseable, then it's valid (of course!).
            // If not parseable, then give a default value of the date in ValueNode.
            DateFormat dateFormat = RelativeTemporalValueNode.getDateFormat(DateFormat.FULL, -1);
            
            try {
                dateFormat.parse(currentText);
            } catch (ParseException pe) {
                currentText = dateFormat.format(dateValueNode.getDateValue());
                changed = true;

            }

        } else if (typeExpr.isNonParametricType(CAL_RelativeTime.TypeConstructors.RelativeTime)) {

            RelativeTimeValueNode timeValueNode = (RelativeTimeValueNode) valueEntryPanel.getValueNode();

            // Check.  If it's parseable, then it's valid (of course!).
            // If not parseable, then give a default value of the time in ValueNode.
            DateFormat timeFormat = RelativeTemporalValueNode.getDateFormat(-1, DateFormat.MEDIUM);
            
            try {
                timeFormat.parse(currentText);
            } catch (ParseException pe) {
                currentText = timeFormat.format(timeValueNode.getTimeValue());
                changed = true;

            }

        } else if (typeExpr.isNonParametricType(CAL_RelativeTime.TypeConstructors.RelativeDateTime)) {

            RelativeDateTimeValueNode dateTimeValueNode = (RelativeDateTimeValueNode) valueEntryPanel.getValueNode();

            // Check.  If it's parseable, then it's valid (of course!).
            // If not parseable, then give a default value of the date time in ValueNode.
            DateFormat dateTimeFormat = RelativeTemporalValueNode.getDateFormat(DateFormat.FULL, DateFormat.MEDIUM);
            
            try {
                dateTimeFormat.parse(currentText);
            } catch (ParseException pe) {
                currentText = dateTimeFormat.format(dateTimeValueNode.getDateTimeValue());
                changed = true;
            }

        } else if (typeExpr.isNonParametricType(CAL_Time.TypeConstructors.Time)) {

            JTimeValueNode valueNode = (JTimeValueNode) valueEntryPanel.getValueNode();

            // Check.  If it's parseable, then it's valid (of course!).
            // If not parseable, then give a default value of the date time in ValueNode.
            DateFormat fmt=DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.LONG);
            fmt.setTimeZone(TimeZone.getDefault());

            
            try {
                fmt.parse(currentText);
            } catch (ParseException pe) {
                currentText = fmt.format(valueNode.getJavaDate());
                changed = true;
            }

        } else if (typeExpr.sameType(typeConstants.getCharListType())) {

            // Strings are valid.

        } else if (typeExpr.sameType(typeConstants.getStringType())) {

            // Strings are valid.

        } else {

            // All other types are assumed to be valid.  No need for default.
        }

        // If we changed something, return the new string, else return null
        return changed ? currentText : null;
    }
    
    /**
     * Returns the ValueEntryPanel which uses this ValueFormat.
     * Note: In rare cases, a null can be returned if there are no such ValueEntryPanels.
     * (Eg: The TimeValueEditor uses a ValueFormat, but is not a ValueEntryPanel).
     * Creation date: (16/04/01 11:56:14 AM)
     * @return ValueEntryPanel
     */
    ValueEntryPanel getValueEntryPanel() {
        return valueEntryPanel;
    }
    
    /**
     * Perform insertion edit operation.
     * Creation date: (1/12/2001 8:50:52 PM)
     * @param offs int the location
     * @param str String what to add
     * @param a AttributeSet the attributes
     * @exception BadLocationException Illegal location for insert.
     */
    @Override
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
        // Special check: If this document is not editable, do nothing.
        // Exception: if there is nothing in this document, then we must allow initialization.
        if (isChecking && !isEditable() && getLength() != 0) {
            return;
        }

        // Get pieces and formulate the proposed result
        String currentText = getText(0, getLength());
        String beforeOffset = currentText.substring(0, offs);
        String afterOffset = currentText.substring(offs, currentText.length());
        String proposedResult = beforeOffset + str + afterOffset;

        if (isChecking) {
            // If the proposed result is OK, allow the edit 
            if (isValidTextChange(proposedResult)) {
                                
                super.insertString(offs, str, a);
                updateValueNode();
            } else {
                Toolkit.getDefaultToolkit().beep();
            }
        } else {
            // No checking.  Anything goes.
            super.insertString(offs, str, a);
        }
    }
    
    /**
     * Returns whether or not 'checking' is in effect.
     * If checking is not in effect, make sure you do not write 
     * invalid gibberish into this document.
     * Creation date: (18/07/01 10:05:21 AM)
     * @return boolean
     */
    public boolean isChecking() {
        return isChecking;
    }
    
    /**
     * Returns whether or not this Document is editable.
     * If it isn't, then any text change will be rejected.
     * Creation date: (11/04/01 10:28:35 AM)
     * @return boolean
     */
    private boolean isEditable() {
        return valueEntryPanel.isEditable();
    }
    
    /**
     * Check if this string image is a good char.
     * Creation date: (02/03/2001 8:51:00 AM)
     * @return boolean true if a good representation of a char
     * @param image String the image
     */
    private static boolean isGoodChar(String image) {
        // An empty String (length 0) is okay.
        // A String with length 1 is always okay (since we allow any char).
        // A length > 1 is not okay.
        if (image.length() > 1) {
            return false;
        } else {
            return true;
        }
    }
    
    /**
     * Check if this string image is a good float representation
     * Creation date: (1/12/2001 9:08:55 PM)
     * @return boolean true if a good representation of a float
     * @param image String the image
     */
    private static boolean isGoodDouble(String image) {
        // Image needs a bit of tweaking sometimes
        // If the last character is '-' or 'e' or image is empty, append '0'
        int len = image.length();
        if (len == 0) {
            image = "0";
        } else {
            char lastChar = image.charAt(len - 1);
            if (lastChar == '-' || lastChar == 'e' || lastChar == 'E') {
                image = image + '0';
            } else if (lastChar == '.' && len == 1) {
                // If the user typed in '.' then we change it to "0."
                image = "0.";
            }
        }
        try {
            Double.parseDouble(image);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Check if this string image is a good integer representation.
     * Creation date: (1/12/2001 9:08:55 PM)
     * @return boolean true if a good representation of an integer
     * @param image String the image   
     */
    private static boolean isGoodInteger(String image) {
        // Image needs a bit of tweaking sometimes
        // If the image is empty, append '0'
        // If the last character is '-', append '0'
        int len = image.length();
        if (len == 0) {
            image = "0";
        } else {
            char lastChar = image.charAt(len - 1);
            if (lastChar == '-') {
                image = image + '0';
            }
        }
        try {
            Integer.parseInt(image, 10);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Check if this string image is a good representation of a BigInteger.
     * We need a method separate from isGoodInteger, because Integer.parseInt
     * will reject values that won't fit into a long.
     * @param image String the image to check
     * @return boolean true if the image is a good representation of a BigInteger
     */
    private static boolean isGoodBigInteger(String image) {
       // Image needs a bit of tweaking sometimes
        // If the image is empty, append '0'
        // If the last character is '-', append '0'
        int len = image.length();
        if (len == 0) {
            image = "0";
        } else {
            char lastChar = image.charAt(len - 1);
            if (lastChar == '-') {
                image = image + '0';
            }
        }
        try {
            new BigInteger(image);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
   
    /**
     * Checks to see if it's valid to change to the proposed text.
     * Note: Currently, only Double and Integer types are allowed to be changed.
     * Creation date: (27/2/2001 8:47:06 AM)
     * @return boolean true if a valid
     * @param text String the text to test
     */
    public boolean isValidTextChange(String text) {

        boolean valid = false;

        TypeExpr typeExpr = valueEntryPanel.getValueNode().getTypeExpr();
        
        PreludeTypeConstants typeConstants = valueEntryPanel.valueEditorManager.getPreludeTypeConstants();
                      
        if (typeExpr.sameType(typeConstants.getDoubleType()) ||
            typeExpr.sameType(typeConstants.getFloatType()) ||
            typeExpr.sameType(typeConstants.getDecimalType())) {
            
            //todoBI Float and Double should have different validation to take into account their ranges
                            
            valid = isGoodDouble(text);

        } else if (typeExpr.sameType(typeConstants.getIntType()) ||
                   typeExpr.sameType(typeConstants.getByteType()) ||
                   typeExpr.sameType(typeConstants.getShortType()) ||
                   typeExpr.sameType(typeConstants.getLongType())) {
           
            //todoBI the integral types should have different validation to take into account their ranges
           
            valid = isGoodInteger(text);

        } else if (typeExpr.sameType(typeConstants.getIntegerType())) {

            valid = isGoodBigInteger(text);
    
        } else if (typeExpr.sameType(typeConstants.getCharType())) {

            valid = isGoodChar(text);

        } else if (typeExpr.sameType(typeConstants.getCharListType())) {

            // Strings are always good!
            valid = true;

        } else if (typeExpr.sameType(typeConstants.getStringType())) {

            // Strings are always good!
            valid = true;

        } else if (typeExpr.isNonParametricType(CAL_RelativeTime.TypeConstructors.RelativeDate) ||
                   typeExpr.isNonParametricType(CAL_RelativeTime.TypeConstructors.RelativeTime) ||
                   typeExpr.isNonParametricType(CAL_RelativeTime.TypeConstructors.RelativeDateTime) ||
                   typeExpr.isNonParametricType(CAL_Time.TypeConstructors.Time)) {
                  
            // The specialized ValueEntryField should take care of validation.
            valid = true;

        } else {

            // All other types are currently not allowed to be edited.          
            valid = false;
        }

        return valid;
    }
    
    /**
     * Perform deletion edit operation.
     * Creation date: (1/12/2001 8:57:39 PM)
     * @param offs int the location
     * @param len int the length to delete
     * @exception BadLocationException A bad location.
     */
    @Override
    public void remove(int offs, int len) throws BadLocationException {
        // Special check: If this document is not editable, do nothing.
        if (isChecking && !isEditable()) {
            return;
        }

        // Get pieces and formulate the proposed result
        String currentText = getText(0, getLength());
        String beforeOffset = currentText.substring(0, offs);
        String afterOffset = currentText.substring(len + offs, currentText.length());
        String proposedResult = beforeOffset + afterOffset;

        if (isChecking) {
            // If the proposed result is OK, allow the edit 
            if (isValidTextChange(proposedResult)) {
                super.remove(offs, len);
                updateValueNode();
            } else {
                Toolkit.getDefaultToolkit().beep();
            }
        } else {
            // No checking.  Anything goes.
            super.remove(offs, len);
        }
    }
    
    /**
     * Sets the flag to denote whether or not 'checking' is in effect.
     * If checking is not in effect (checkingPolicy = false), make sure you do not write
     * invalid gibberish into this document.
     * Creation date: (27/02/01 8:40:17 AM)
     * @param checkingPolicy boolean
     */
    public void setChecking(boolean checkingPolicy) {
        isChecking = checkingPolicy;
    }
    
    /**
     * Updates the ValueNode with the value in this document.
     * (Usually call this method upon a successful edit in checking mode).
     * Creation date: (27/02/01 9:27:45 AM)
     */
    private void updateValueNode() {

        // First, check the string value to use.
        String newVal;

        newVal = getValidOrDefault();

        if (newVal == null) {
            // The currentVal is completely valid.  Use that.
            try {
                newVal = getText(0, getLength());
            } catch (BadLocationException e) {
                newVal = "";
            }
        }

        // Now, do the update to the valueNode.
        TypeExpr typeExpr = valueEntryPanel.getValueNode().getTypeExpr();
        PreludeTypeConstants typeConstants = valueEntryPanel.valueEditorManager.getPreludeTypeConstants();
        
        if (typeExpr.sameType(typeConstants.getCharType())) {

            // The first char should be the char we want as the value.
            Character charVal = new Character(newVal.charAt(0));
            valueEntryPanel.replaceValueNode(new LiteralValueNode(charVal, typeExpr.copyTypeExpr()), true);
            
        } else if (typeExpr.sameType(typeConstants.getByteType())) {

            Double unRoundedVal = new Double(newVal);
            Byte byteVal = new Byte(unRoundedVal.byteValue());
            valueEntryPanel.replaceValueNode(new LiteralValueNode(byteVal, typeExpr.copyTypeExpr()), true);
      
        } else if (typeExpr.sameType(typeConstants.getShortType())) {

            Double unRoundedVal = new Double(newVal);
            Short shortVal = new Short(unRoundedVal.shortValue());
            valueEntryPanel.replaceValueNode(new LiteralValueNode(shortVal, typeExpr.copyTypeExpr()), true);
              
        } else if (typeExpr.sameType(typeConstants.getIntType())) {

            Double unRoundedVal = new Double(newVal);
            Integer integerVal = Integer.valueOf(unRoundedVal.intValue());
            valueEntryPanel.replaceValueNode(new LiteralValueNode(integerVal, typeExpr.copyTypeExpr()), true);

        } else if (typeExpr.sameType(typeConstants.getIntegerType())) {
            
            BigDecimal unRoundedVal = new BigDecimal(newVal);
            BigInteger bigIntegerVal;
            
            // Math.round uses a rounding strategy that BigDecimal does not provide, so we have to do
            // a little bit of fiddling to round in an equivalent fashion.
            if(unRoundedVal.signum() >= 0) {
                bigIntegerVal = unRoundedVal.setScale(0, BigDecimal.ROUND_HALF_UP).toBigInteger();
            } else {
                bigIntegerVal = unRoundedVal.setScale(0, BigDecimal.ROUND_HALF_DOWN).toBigInteger();
            }
            
            valueEntryPanel.replaceValueNode(new LiteralValueNode(bigIntegerVal, typeExpr.copyTypeExpr()), true);
        
        } else if (typeExpr.sameType(typeConstants.getDecimalType())) {

            BigDecimal decimalVal = new BigDecimal(newVal);
            valueEntryPanel.replaceValueNode(new LiteralValueNode(decimalVal, typeExpr.copyTypeExpr()), true);

        } else if (typeExpr.sameType(typeConstants.getLongType())) {

            Double unRoundedVal = new Double(newVal);
            Long longVal = new Long(unRoundedVal.longValue());
            valueEntryPanel.replaceValueNode(new LiteralValueNode(longVal, typeExpr.copyTypeExpr()), true);

        } else if (typeExpr.sameType(typeConstants.getFloatType())) {

            Float floatVal = new Float(newVal);
            valueEntryPanel.replaceValueNode(new LiteralValueNode(floatVal, typeExpr.copyTypeExpr()), true);

        } else if (typeExpr.sameType(typeConstants.getDoubleType())) {

            Double doubleVal = new Double(newVal);
            valueEntryPanel.replaceValueNode(new LiteralValueNode(doubleVal, typeExpr.copyTypeExpr()), true);

        } else if (typeExpr.sameType(typeConstants.getStringType())) {
            
            newVal = newVal.replace(ListOfCharValueNode.CHAR_RETURN_REPLACE, '\n');
            valueEntryPanel.replaceValueNode(new LiteralValueNode(newVal, typeExpr.copyTypeExpr()), true);
            
        } else if (typeExpr.sameType(typeConstants.getCharListType())) {

            // First, must replace the return replacement chars with return.
            newVal = newVal.replace(ListOfCharValueNode.CHAR_RETURN_REPLACE, '\n');
            valueEntryPanel.replaceValueNode(new ListOfCharValueNode(newVal, typeExpr.copyTypeExpr()), true);
        
        } else if (typeExpr.isNonParametricType(CAL_RelativeTime.TypeConstructors.RelativeDate)) {

            DateFormat dateFormat = RelativeTemporalValueNode.getDateFormat(DateFormat.FULL, -1);

            try {
                Date date = dateFormat.parse(newVal);
                valueEntryPanel.replaceValueNode(new RelativeDateValueNode(date, typeExpr.copyTypeExpr()), true);
            } catch (ParseException pe) {
                System.out.println("Error in updateValueNode: Could not parse the Text into a Date value.");
            }

        } else if (typeExpr.isNonParametricType(CAL_RelativeTime.TypeConstructors.RelativeTime)) {

            DateFormat timeFormat = RelativeTemporalValueNode.getDateFormat(-1, DateFormat.MEDIUM);

            try {
                Date date = timeFormat.parse(newVal);
                valueEntryPanel.replaceValueNode(new RelativeTimeValueNode(date, typeExpr.copyTypeExpr()), true);
            } catch (ParseException pe) {
                System.out.println("Error in updateValueNode: Could not parse the Text into a Date value.");
            }

        } else if (typeExpr.isNonParametricType(CAL_RelativeTime.TypeConstructors.RelativeDateTime)) {

            DateFormat dateTimeFormat = RelativeTemporalValueNode.getDateFormat(DateFormat.FULL, DateFormat.MEDIUM);

            try {
                Date date = dateTimeFormat.parse(newVal);
                valueEntryPanel.replaceValueNode(new RelativeDateTimeValueNode(date, typeExpr.copyTypeExpr()), true);
            } catch (ParseException pe) {
                System.out.println("Error in updateValueNode: Could not parse the Text into a Date value.");
            }

        } else if (typeExpr.isNonParametricType(CAL_Time.TypeConstructors.Time)) {
            DateFormat fmt=DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.LONG);
            fmt.setTimeZone(TimeZone.getDefault());

            try {
                Date date = fmt.parse(newVal);
                Time time = Time.fromDate(date);
                valueEntryPanel.replaceValueNode(new JTimeValueNode(time, typeExpr.copyTypeExpr()), true);
            } catch (ParseException pe) {
                System.out.println("Error in updateValueNode: Could not parse the Text into a Date value.");
            }
        } else {

            throw new IllegalArgumentException("Error in updateValueNode:\nCurrently cannot handle this type.");
        }

        valueEntryPanel.valueChangedCheck();
    }
}
