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
 * ValueEntryField.java
 * Creation date: (1/11/01 8:58:53 AM)
 * By: Luke Evans
 */
package org.openquark.gems.client.valueentry;

import java.awt.Graphics2D;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.InputMethodEvent;
import java.awt.event.KeyEvent;

import javax.swing.JTextField;
import javax.swing.ToolTipManager;

import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.valuenode.ListOfCharValueNode;
import org.openquark.gems.client.ToolTipHelpers;


/**
 * ValueEntryField is a JTextField which is sensitive to CAL value entry.
 * Creation date: (1/11/01 8:58:53 AM)
 * @author Luke Evans
 */
class ValueEntryField extends JTextField {

    private static final long serialVersionUID = -7721464819080157561L;

    /** Flag to denote whether or not the last key event was an Alt. */
    private transient boolean lastKeyEventWasAlt;
    
    /** 
     * A prefix to use when displaying tool tips. Used to display the name of the argument
     * that this value field is collecting a value for.
     */
    private String toolTipPrefix = null;
    
    /**
     * Default ValueEntryField constructor.
     * @param valueEntryPanel The ValueEntryPanel which uses this ValueEntryField.
     */
    public ValueEntryField(ValueEntryPanel valueEntryPanel) {
        setDocument(new ValueFormat(valueEntryPanel));

        addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                // The user has left this ValueEntryField, the value may need defaulting
                ValueFormat format = (ValueFormat) getDocument();
                String newVal = format.getValidOrDefault();
                if (newVal != null) {
                    setText(newVal);
                }
            }
        });

        lastKeyEventWasAlt = false;
        
        // Enable tooltips for this component.
        ToolTipManager.sharedInstance().registerComponent(this);
    }

    /**
     * Set the prefix to display in front of the tool tip of this value field.
     * Creation date: (08/22/2002 4:29:00 PM).
     * @param prefix String - the prefix to use in tool tips.
     */
    public void setToolTipPrefix(String prefix) {
        toolTipPrefix = prefix;
    }
    
    /**
     * Returns the tooltip text for the text in this text field.
     * Creation date: (20/02/01 3:39:43 PM)
     * @return String
     */
    @Override
    public String getToolTipText() {
        
        ValueFormat valueFormat = (ValueFormat) getDocument();
        TypeExpr typeExpr = valueFormat.getValueEntryPanel().getValueNode().getTypeExpr();
        String toolTipText = getText ();
        
        //it would be nicer to use PreludeTypeConstants but unfortunately, getValueEditor below sometimes returns null.
        //PreludeTypeConstants typeConstants = valueFormat.getValueEntryPanel().getValueEditor().valueEditorManager.getPreludeTypeConstants();
        
        // Strings and character lists get special handling     
        if (typeExpr.isNonParametricType(CAL_Prelude.TypeConstructors.String) ||
            typeExpr.isListTypeOf(CAL_Prelude.TypeConstructors.Char)) {
    
            // Convert the linebreak placeholders back into actual line breaks
            toolTipText = toolTipText.replace(ListOfCharValueNode.CHAR_RETURN_REPLACE, '\n');
    
            // Add the prefix at the top on its own line and make it bold
            toolTipText = (toolTipPrefix != null) ? "<b>" + toolTipPrefix + "</b>\n" + toolTipText : toolTipText;
                
            // Get the tooltip in HTML
            if (toolTipText.length() > 0) {
                toolTipText = ToolTipHelpers.stringToHtmlToolTip (toolTipText, 500, 400, getFont(), ((Graphics2D) getGraphics()).getFontRenderContext());
            }
            
        } else {
            toolTipText = (toolTipPrefix != null) ? "<html><b>" + toolTipPrefix + "</b> " + getText() + "</html>": toolTipText;
        }

        if (toolTipText.length() == 0) {
            return null;
        }

        return toolTipText;
    }
    
    /**
     * @see javax.swing.JComponent#processComponentKeyEvent(java.awt.event.KeyEvent)
     */
    @Override
    protected synchronized void processComponentKeyEvent(KeyEvent anEvent) {
        super.processComponentKeyEvent(anEvent);

        // Set the flag. 
        lastKeyEventWasAlt = anEvent.isAltDown();
    }
    
    /**
     * @see java.awt.Component#processInputMethodEvent(java.awt.event.InputMethodEvent)
     */
    @Override
    protected synchronized void processInputMethodEvent(InputMethodEvent e) {
        // Don't let the normal processing occur with Alt.  Just ignore it.
        if (lastKeyEventWasAlt) {
            e.consume();
        }

        super.processInputMethodEvent(e);
    }
    
    /**
     * Very similar to its superclass's method with the one key difference
     * that value transition is handled more smoothly. (Meaning that ValueEntryPanel's valueChangedCheck only occurs
     * at the end of the value transition, and not twice [1 for remove, 1 for insertion])
     */
    @Override
    public void replaceSelection(String content) {
        ValueFormat vf = (ValueFormat) getDocument();

        ValueEntryPanel vep = vf.getValueEntryPanel();
        vep.setValueTransition(true);

        super.replaceSelection(content);

        vep.setValueTransition(false);
        vep.valueChangedCheck();
    }
}
