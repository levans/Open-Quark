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
 * TimeValueEditor.java
 * Creation date: (03/08/05 10:35:00AM)
 * By: James Wright
 */
package org.openquark.gems.client.valueentry;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.module.Cal.Utilities.CAL_RelativeTime;
import org.openquark.cal.valuenode.JTimeValueNode;
import org.openquark.cal.valuenode.RelativeDateValueNode;
import org.openquark.cal.valuenode.RelativeTimeValueNode;
import org.openquark.cal.valuenode.ValueNode;
import org.openquark.util.time.Time;

import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.TimeZone;

/**
 * A ValueEditor for manipulating CAL Time values i.e. values of the Time.Time type.
 * Makes use of the RelativeDateValueEditor and the RelativeTimeValueEditor.
 * Note: This ValueEditor should be treated a one unit/ValueEditor, and not two units (RelativeDateValueEditor and RelativeTimeValueEditor).
 * 
 * It is heavily based upon (read: cut-n-pasted from) RelativeDateTimeValueEditor
 *
 * @author James Wright
 */
class TimeValueEditor extends StructuredValueEditor {
    
    /**
     * A custom value editor provider for the TimeValueEditor.
     */
    public static class TimeValueEditorProvider extends ValueEditorProvider<TimeValueEditor> {
        
        public TimeValueEditorProvider(ValueEditorManager valueEditorManager) {
            super(valueEditorManager);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean canHandleValue(ValueNode valueNode, SupportInfo providerSupportInfo) {
            return valueNode instanceof JTimeValueNode;
        }

        /**
         * @see ValueEditorProvider#getEditorInstance(ValueEditorHierarchyManager, ValueNode)
         */
        @Override
        public TimeValueEditor getEditorInstance(ValueEditorHierarchyManager valueEditorHierarchyManager,
                                             ValueNode valueNode) {
            
            TimeValueEditor editor = new TimeValueEditor(valueEditorHierarchyManager);
            editor.setOwnerValueNode(valueNode);
            return editor;
        }
    }

    private static final long serialVersionUID = 8627342304664001172L;

    private JPanel ivjIntermediatePanel = null;
    private final RelativeDateValueEditor dateValueEditor;
    private final RelativeTimeValueEditor timeValueEditor;
    
    /**
     * TimeValueEditor constructor.
     * @param valueEditorHierarchyManager
     */
    protected TimeValueEditor(ValueEditorHierarchyManager valueEditorHierarchyManager) {
        super(valueEditorHierarchyManager);

        try {
            setName("TimeValueEditor");
            setLayout(new BorderLayout());
            setSize(260, 250);
            add(getIntermediatePanel(), "Center");

        } catch (Throwable ivjExc) {
            handleException(ivjExc);
        }

        dateValueEditor = new RelativeDateValueEditor(valueEditorHierarchyManager);
        dateValueEditor.setParentValueEditor(TimeValueEditor.this);
        dateValueEditor.setBorder(BorderFactory.createEmptyBorder());
        
        timeValueEditor = new RelativeTimeValueEditor(valueEditorHierarchyManager);
        timeValueEditor.setParentValueEditor(TimeValueEditor.this);
        timeValueEditor.setBorder(BorderFactory.createEmptyBorder(0, 4, 4, 4));

        getIntermediatePanel().add(timeValueEditor, BorderLayout.SOUTH);
        getIntermediatePanel().add(dateValueEditor, BorderLayout.CENTER);

        // Give this ValueEditor its preferredSize.
        setSize(getPreferredSize());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void commitValue() {
        // Get the date time values from the two inner value editors.
        Calendar uiCalendar = dateValueEditor.getCalendar();
        timeValueEditor.getUpdatedCalendar(uiCalendar);

        // Convert from UTC to local timezone.
        Calendar localCalendar=Calendar.getInstance(TimeZone.getDefault());  // Get a Calendar in the local tz
        localCalendar.set(
                uiCalendar.get(Calendar.YEAR),
                uiCalendar.get(Calendar.MONTH),
                uiCalendar.get(Calendar.DAY_OF_MONTH),
                uiCalendar.get(Calendar.HOUR_OF_DAY),
                uiCalendar.get(Calendar.MINUTE),
                uiCalendar.get(Calendar.SECOND));
                
        // Update the value in the ValueNode.
        ValueNode returnVN = new JTimeValueNode(Time.fromDate(localCalendar.getTime()), getValueNode().getTypeExpr());
        replaceValueNode(returnVN, false);

        notifyValueCommitted();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void handleElementLaunchingEditor() {
        // children can't launch editors..
    }
    
    /**
     * Returns the JTimeValueNode containing the data for this TimeValueEditor.
     * @return JTimeValueNode
     */
    public JTimeValueNode getJTimeValueNode() {
        return (JTimeValueNode) getValueNode();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Component getDefaultFocusComponent() {
        return dateValueEditor.getDefaultFocusComponent();
    }

    /**
     * Return the IntermediatePanel property value.
     * @return JPanel
     */
    private JPanel getIntermediatePanel() {
        if (ivjIntermediatePanel == null) {
            try {
                ivjIntermediatePanel = new JPanel();
                ivjIntermediatePanel.setName("IntermediatePanel");
                ivjIntermediatePanel.setLayout(new BorderLayout());
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjIntermediatePanel;
    }
    
    /**
     * Called whenever the part throws an exception.
     * @param exception Throwable
     */
    private void handleException(Throwable exception) {

        /* Uncomment the following lines to print uncaught exceptions to stdout */
        System.out.println("--------- UNCAUGHT EXCEPTION ---------");
        exception.printStackTrace(System.out);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setOwnerValueNode(ValueNode newValueNode) {
        super.setOwnerValueNode(newValueNode);
        
        Time timeValue = (Time)getJTimeValueNode().getValue();
        Date dateTimeValue = timeValue.toDate();
        TypeExpr dateType = valueEditorManager.getValueNodeBuilderHelper().getTypeConstructorForName(CAL_RelativeTime.TypeConstructors.RelativeDate);
        TypeExpr timeType = valueEditorManager.getValueNodeBuilderHelper().getTypeConstructorForName(CAL_RelativeTime.TypeConstructors.RelativeTime);
        
        dateValueEditor.setOwnerValueNode(new RelativeDateValueNode(dateTimeValue, dateType));
        timeValueEditor.setOwnerValueNode(new RelativeTimeValueNode(dateTimeValue, timeType));
    }

    /**
     * Sets the initial value in both the RelativeDateValueEditor and RelativeTimeValueEditor 
     * from the Date value in the ValueNode.
     */
    @Override
    public void setInitialValue() {
        Time nodeValue=(Time)getJTimeValueNode().getValue();
        
        // Convert from local timezone to quasi-UTC 
        Calendar localCalendar=Calendar.getInstance(TimeZone.getDefault());
        localCalendar.setTime(nodeValue.toDate());

        Calendar uiCalendar=Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        uiCalendar.set(
              localCalendar.get(Calendar.YEAR),
              localCalendar.get(Calendar.MONTH),
              localCalendar.get(Calendar.DAY_OF_MONTH),
              localCalendar.get(Calendar.HOUR_OF_DAY),
              localCalendar.get(Calendar.MINUTE),
              localCalendar.get(Calendar.SECOND));
        
        dateValueEditor.setCalendar(uiCalendar);
        timeValueEditor.setCalendar(uiCalendar);
        
        valueEditorHierarchyManager.addEditorToHierarchy(dateValueEditor, this);
    }
}
