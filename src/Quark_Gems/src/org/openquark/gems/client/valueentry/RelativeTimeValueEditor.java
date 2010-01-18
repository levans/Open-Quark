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
 * RelativeTimeValueEditor.java
 * Created: June 3, 2001
 * By: Michael Cheng
 */

package org.openquark.gems.client.valueentry;

import java.awt.Component;
import java.awt.Font;
import java.util.Date;

import javax.swing.JPanel;

import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.module.Cal.Utilities.CAL_RelativeTime;
import org.openquark.cal.valuenode.RelativeTimeValueNode;
import org.openquark.cal.valuenode.ValueNode;

import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.TimeZone;

/**
 * ValueEditor for the value entry of time types.
 * (Hour:Minute:Second:AM/PM).
 * Creation date: (06/03/01 10:25:30 AM)
 * @author Michael Cheng
 */
class RelativeTimeValueEditor extends ValueEditor {

    private static final long serialVersionUID = 2237426931598580736L;

    /**
     * A custom value editor provider for the RelativeTimeValueEditor.
     */
    public static class RelativeTimeValueEditorProvider extends ValueEditorProvider<RelativeTimeValueEditor> {

        public RelativeTimeValueEditorProvider(ValueEditorManager valueEditorManager) {
            super(valueEditorManager);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean canHandleValue(ValueNode valueNode, SupportInfo providerSupportInfo) {
            return valueNode instanceof RelativeTimeValueNode;
        }
        
        /**
         * @see org.openquark.gems.client.valueentry.ValueEditorProvider#getEditorInstance(ValueEditorHierarchyManager, ValueNode)
         */
        @Override
        public RelativeTimeValueEditor getEditorInstance(ValueEditorHierarchyManager valueEditorHierarchyManager,
                                             ValueNode valueNode) {
            
            RelativeTimeValueEditor editor = new RelativeTimeValueEditor(valueEditorHierarchyManager);
            editor.setOwnerValueNode(valueNode);
            return editor;
        }
    }

    private JPanel ivjIntermediatePanel = null;
    private DateTimeValueEntryField valueField = null;
    
    /**
     * RelativeTimeValueEditor constructor.
     * @param valueEditorHierarchyManager
     */
    protected RelativeTimeValueEditor(ValueEditorHierarchyManager valueEditorHierarchyManager) {
        super(valueEditorHierarchyManager);
        initialize();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void commitValue() {
        // Take the previous date.
        Calendar calendar = getTimeValueNode().getCalendarValue(); 

        // Update the date.
        getUpdatedCalendar(calendar);

        // Save the updated date.
        ValueNode returnVN = new RelativeTimeValueNode(calendar.getTime(), getValueNode().getTypeExpr());
        replaceValueNode(returnVN, false);

        notifyValueCommitted();
    }

    /**
     * {@inheritDoc}
     * This sets the caret position to the beginning of the value field.
     */
    @Override
    public void editorActivated() {
        valueField.setCaretPosition(0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Component getDefaultFocusComponent() {
        return valueField;
    }

    /**
     * Return the IntermediatePanel property value.
     * Note: Extra set-up code has been added.
     * @return JPanel
     */
    private JPanel getIntermediatePanel() {
        if (ivjIntermediatePanel == null) {
            try {
                ivjIntermediatePanel = new JPanel();
                ivjIntermediatePanel.setName("IntermediatePanel");
                ivjIntermediatePanel.setLayout(new java.awt.BorderLayout());
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjIntermediatePanel;
    }
    
    /**
     * Returns the RelativeTimeValueNode containing the data for this RelativeTimeValueEditor.
     * Creation date: (04/07/01 10:59:26 AM)
     * @return org.openquark.gems.client.valueentry.RelativeTimeValueNode
     */
    public RelativeTimeValueNode getTimeValueNode() {

        return (RelativeTimeValueNode) getValueNode();
    }

    /**
     * Updates the passed in param, calendar, with the Hour, Minute, and Second
     * according to the user input in the display.
     * Creation date: (08/03/01 9:06:51 AM)
     * @param updateCalendar Calendar
     */
    public void getUpdatedCalendar(Calendar updateCalendar) {
        // Use the date from the value field to determine the new time.
        Date time = valueField.getDate();
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.setTime(time);

        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        int ampmFlag = calendar.get(Calendar.AM_PM);

        // Update the updateCalendar.
        updateCalendar.set(Calendar.HOUR, hour);
        updateCalendar.set(Calendar.MINUTE, minute);
        updateCalendar.set(Calendar.SECOND, second);
        updateCalendar.set(Calendar.AM_PM, ampmFlag);
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
     * Initialize the class.
     * Note: Extra Set-up code has been added.
     */
    private void initialize() {
        try {
            setName("RelativeTimeValueEditor");
            setLayout(new java.awt.BorderLayout());
            setSize(130, 40);
            add(getIntermediatePanel(), "Center");
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }

        // Create a value tree node to use with the value field.
        // The value will be the current time.
        TypeExpr timeTypeExpr = valueEditorManager.getValueNodeBuilderHelper().getTypeConstructorForName(CAL_RelativeTime.TypeConstructors.RelativeTime);
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        Date time = calendar.getTime();
        RelativeTimeValueNode timeVN = new RelativeTimeValueNode(time, timeTypeExpr);

        valueField = new DateTimeValueEntryField(new ValueEntryPanel(valueEditorHierarchyManager, timeVN));

        // Give it a larger font.
        valueField.setFont(new java.awt.Font("sansserif", Font.BOLD, 18));

        // Make sure that the user's commit and cancel commands are carried out.
        valueField.addKeyListener(new ValueEditorKeyListener());

        // Finally, add the value field to the display.
        getIntermediatePanel().add(valueField, "Center");

        // Make sure the ValueFormat is checking properly (of correct type and data).
        ValueFormat valueFormat = (ValueFormat) valueField.getDocument();
        valueFormat.setChecking(true);

        // Make sure that mouse clicks don't 'leak' thru.
        addMouseListener(new java.awt.event.MouseAdapter() {
        });

        // Make the value field display its initial value.
        valueField.setDate(time);
        
        validate();
        setSize(getPreferredSize());
    }
    
    /**
     * Updates the diplay with the value in calendar.
     * Creation date: (08/03/01 9:14:29 AM)
     * @param calendar Calendar
     */
    public void setCalendar(Calendar calendar) {
        valueField.setDate(calendar.getTime());
    }

    /**
     * Sets the initial value from the date value in its ValueNode.
     * Creation date: (06/03/01 10:25:30 AM)
     */
    @Override
    public void setInitialValue() {

        Calendar calendar = getTimeValueNode().getCalendarValue(); 

        setCalendar(calendar);
    }
}