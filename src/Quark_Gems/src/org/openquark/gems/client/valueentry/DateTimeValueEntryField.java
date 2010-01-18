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
 * DateTimeValueEntryField.java
 * Creation date: (09/03/01 10:28:18 AM) 
 * By: Michael Cheng
 */
/*
 * Based on the DateTimeEditor created by David M. Karr of Best Consulting and TCSI Corporation.
 * The DateTimeEditor is provided as sample code in the book “Swing” by Matthew Robinson and 
 * Pavel Vorobiev.  The books's website (which includes this sample code) is at http://www.manning.com/sbe/ 
 * 
 * No specific license statement appears to be present anywhere in the book or on the website.
 * However a comment from Matt Robinson on the forum dated Nov 14, 2002 appears to indicate that this code 
 * is freely-available, with the condition that the book be recommended if found useful.
 * We certainly found it useful for this class.
 * 
 * The following is from http://www.manning-sandbox.com/thread.jspa?messageID=9314&#9314 
 * 
 * 
 * source code availability
 * Posted: Oct 20, 2002 11:00 PM
 * [Originally posted by dan_can]
 * 
 * Is the code used in the Swing book covered under a General Public License and
 * available for use in non-commercial projects? 
 * 
 * 
 * Re: source code availability
 * Posted: Oct 22, 2002 11:00 PM
 * [Originally posted by pavelv]
 * 
 * Yes, it's available.
 * 
 * Pavel
 * 
 * 
 * Re: source code availability
 * Posted: Nov 11, 2002 11:00 PM
 * [Originally posted by me1dif]
 * 
 * > Yes, it's available.
 * >
 * > Pavel
 * Please can you clarify, is the code available under the GNU license, or some
 * other terms? I want to check this carefully before using it in my work.
 * 
 * Thanks,
 * 
 * David.
 * 
 * 
 * Re: source code availability
 * Posted: Nov 14, 2002 11:00 PM
 * [Originally posted by matt]
 * 
 * David, pls feel free to use the code as you see fit - the only thing we ask is
 * that you recommend our book to others if you find it useful
 * 
 * regards,
 * -Matt
 */
package org.openquark.gems.client.valueentry;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.text.FieldPosition;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.NoSuchElementException;

import javax.swing.AbstractAction;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.Caret;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;
import javax.swing.text.Keymap;
import javax.swing.text.TextAction;

import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.module.Cal.Utilities.CAL_RelativeTime;
import org.openquark.cal.module.Cal.Utilities.CAL_Time;
import org.openquark.cal.valuenode.RelativeTemporalValueNode;
import org.openquark.cal.valuenode.ValueNode;

import com.ibm.icu.text.DateFormat;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.TimeZone;

/**
 * Special ValueEntryField used when handling Date or Time or DateTime data types.
 * Current applications are for RelativeTime.RelativeDate. RelativeTime.RelativeTime, RelativeTime.RelativeDateTime and Time.Time. 
 * 
 * Based on the DateTimeEditor created by David M. Karr of Best Consulting and TCSI Corporation.
 * The DateTimeEditor is provided as sample code in the book “Swing” by Matthew Robinson and 
 * Pavel Vorobiev.  The books's website (which includes this sample code) is at http://www.manning.com/sbe/ 
 * 
 * Creation date: (09/03/01 10:28:18 AM) 
 * 
 * @author Michael Cheng
 */
class DateTimeValueEntryField extends ValueEntryField {

    private static final long serialVersionUID = -1376225814257856569L;

    public static final long ONE_SECOND = 1000;
    public static final long ONE_MINUTE = 60 * ONE_SECOND;
    public static final long ONE_HOUR = 60 * ONE_MINUTE;
    public static final long ONE_DAY = 24 * ONE_HOUR;
    public static final long ONE_WEEK = 7 * ONE_DAY;
    
    //constants for the different types handled by this entry field.
    public final static int RELATIVETIME = 0;
    public final static int RELATIVEDATE = 1;
    public final static int RELATIVEDATETIME = 2;
    public final static int JTIME = 3;
    
    private int m_timeOrDateType;
    private DateFormat m_format;
    private Calendar m_calendar;
    private final ArrayList<FieldPosition> m_fieldPositions;
    private Date m_lastDate;
    private Caret m_caret;
    private int m_curField = -1;
    private JTextField m_textField;
    private final AbstractAction m_upAction = new UpDownAction(1, "up");
    private final AbstractAction m_downAction = new UpDownAction(-1, "down");
    private final int[] m_fieldTypes =
        {
            DateFormat.ERA_FIELD,
            DateFormat.YEAR_FIELD,
            DateFormat.MONTH_FIELD,
            DateFormat.DATE_FIELD,
            DateFormat.HOUR_OF_DAY1_FIELD,
            DateFormat.HOUR_OF_DAY0_FIELD,
            DateFormat.MINUTE_FIELD,
            DateFormat.SECOND_FIELD,
            DateFormat.MILLISECOND_FIELD,
            DateFormat.DAY_OF_WEEK_FIELD,
            DateFormat.DAY_OF_YEAR_FIELD,
            DateFormat.DAY_OF_WEEK_IN_MONTH_FIELD,
            DateFormat.WEEK_OF_YEAR_FIELD,
            DateFormat.WEEK_OF_MONTH_FIELD,
            DateFormat.TIMEZONE_FIELD,
            DateFormat.AM_PM_FIELD,
            DateFormat.HOUR1_FIELD,
            DateFormat.HOUR0_FIELD };

    protected class UpDownAction extends AbstractAction {
        private static final long serialVersionUID = 5245800495509559943L;
        
        int m_direction; // +1 = up; -1 = down

        public UpDownAction(int direction, String name) {
            super(name);
            m_direction = direction;
        }

        public void actionPerformed(ActionEvent evt) {
            if (!this.isEnabled()) {
                return;
            }
            boolean dateSet = true;
            switch (m_curField) {
                case DateFormat.AM_PM_FIELD :
                    m_lastDate.setTime(m_lastDate.getTime() + (m_direction * 12 * ONE_HOUR));
                    break;
                case DateFormat.DATE_FIELD :
                case DateFormat.DAY_OF_WEEK_FIELD :
                case DateFormat.DAY_OF_WEEK_IN_MONTH_FIELD :
                case DateFormat.DAY_OF_YEAR_FIELD :
                    m_lastDate.setTime(m_lastDate.getTime() + (m_direction * ONE_DAY));
                    break;
                case DateFormat.ERA_FIELD :
                case DateFormat.TIMEZONE_FIELD :
                    dateSet = false;
                    break;
                case DateFormat.HOUR0_FIELD :
                case DateFormat.HOUR1_FIELD :
                case DateFormat.HOUR_OF_DAY0_FIELD :
                case DateFormat.HOUR_OF_DAY1_FIELD :
                    m_lastDate.setTime(m_lastDate.getTime() + (m_direction * ONE_HOUR));
                    break;
                case DateFormat.MILLISECOND_FIELD :
                    m_lastDate.setTime(m_lastDate.getTime() + (m_direction * 1));
                    break;
                case DateFormat.MINUTE_FIELD :
                    m_lastDate.setTime(m_lastDate.getTime() + (m_direction * ONE_MINUTE));
                    break;
                case DateFormat.MONTH_FIELD :
                    m_calendar.set(Calendar.MONTH, m_calendar.get(Calendar.MONTH) + m_direction);
                    m_lastDate = m_calendar.getTime();
                    break;
                case DateFormat.SECOND_FIELD :
                    m_lastDate.setTime(m_lastDate.getTime() + (m_direction * ONE_SECOND));
                    break;
                case DateFormat.WEEK_OF_MONTH_FIELD :
                    m_calendar.set(Calendar.WEEK_OF_MONTH, m_calendar.get(Calendar.WEEK_OF_MONTH) + m_direction);
                    m_lastDate = m_calendar.getTime();
                    break;
                case DateFormat.WEEK_OF_YEAR_FIELD :
                    m_calendar.set(Calendar.WEEK_OF_MONTH, m_calendar.get(Calendar.WEEK_OF_MONTH) + m_direction);
                    m_lastDate = m_calendar.getTime();
                    break;
                case DateFormat.YEAR_FIELD :
                    m_calendar.set(Calendar.YEAR, m_calendar.get(Calendar.YEAR) + m_direction);
                    m_lastDate = m_calendar.getTime();
                    break;
                default :
                    dateSet = false;
            }

            if (dateSet) {
                int fieldId = m_curField;
                setDate(m_lastDate);
                FieldPosition fieldPosition = getFieldPosition(fieldId);
                m_caret.setDot(fieldPosition.getBeginIndex());

                m_textField.requestFocus();
                repaint();
            }
        }
    }

    protected class BackwardAction extends TextAction {
        private static final long serialVersionUID = -2108060287476804972L;

        BackwardAction(String name) {
            super(name);
        }

        public void actionPerformed(ActionEvent e) {
            JTextComponent target = getTextComponent(e);
            if (target != null) {
                int dot = target.getCaretPosition();
                if (dot > 0) {
                    FieldPosition position = getPrevField(dot);
                    if (position != null) {
                        target.setCaretPosition(position.getBeginIndex());
                    } else {
                        position = getFirstField();
                        if (position != null) {
                            target.setCaretPosition(position.getBeginIndex());
                        }
                    }
                } else {
                    target.getToolkit().beep();
                }
                target.getCaret().setMagicCaretPosition(null);
            }
        }
    }

    protected class ForwardAction extends TextAction {
        private static final long serialVersionUID = 6714055439023582206L;

        ForwardAction(String name) {
            super(name);
        }

        public void actionPerformed(ActionEvent e) {
            JTextComponent target = getTextComponent(e);
            if (target != null) {
                FieldPosition position = getNextField(target.getCaretPosition());
                if (position != null) {
                    target.setCaretPosition(position.getBeginIndex());
                } else {
                    position = getLastField();
                    if (position != null) {
                        target.setCaretPosition(position.getBeginIndex());
                    }
                }
                target.getCaret().setMagicCaretPosition(null);
            }
        }
    }

    protected class BeginAction extends TextAction {
        private static final long serialVersionUID = 130846739211334762L;

        BeginAction(String name) {
            super(name);
        }

        public void actionPerformed(ActionEvent e) {
            JTextComponent target = getTextComponent(e);
            if (target != null) {
                FieldPosition position = getFirstField();
                if (position != null) {
                    target.setCaretPosition(position.getBeginIndex());
                }
            }
        }
    }

    protected class EndAction extends TextAction {
        private static final long serialVersionUID = 4181056171567621736L;

        EndAction(String name) {
            super(name);
        }

        public void actionPerformed(ActionEvent e) {
            JTextComponent target = getTextComponent(e);
            if (target != null) {
                FieldPosition position = getLastField();
                if (position != null) {
                    target.setCaretPosition(position.getBeginIndex());
                }
            }
        }
    }

    /**
     * DateTimeValueEntryField constructor.
     * The type (Date/Time/DateTime) will depend on the valueNode of the ValueEntryPanel passed as an argument.
     * @param valueEntryPanel The ValueEntryPanel which uses this ValueEntryField.
     */
    public DateTimeValueEntryField(ValueEntryPanel valueEntryPanel) {

        super(valueEntryPanel);
        
        ValueNode valueNode = valueEntryPanel.getValueNode();
        
        TypeExpr typeExpr = valueNode.getTypeExpr();

        if (typeExpr.isNonParametricType(CAL_RelativeTime.TypeConstructors.RelativeTime)) {

            m_timeOrDateType = RELATIVETIME; 

        } else if (typeExpr.isNonParametricType(CAL_RelativeTime.TypeConstructors.RelativeDate)) {

            m_timeOrDateType = RELATIVEDATE;

        } else if (typeExpr.isNonParametricType(CAL_RelativeTime.TypeConstructors.RelativeDateTime)) {

            m_timeOrDateType = RELATIVEDATETIME;

        } else if(typeExpr.isNonParametricType(CAL_Time.TypeConstructors.Time)) {
            m_timeOrDateType = JTIME;
        } else {

            throw new IllegalArgumentException("Error in constructor DateTimeValueEntryField:\nThe param valueNode is not of type Date/Time/DateTime.");
        }

        if(m_timeOrDateType == JTIME) {
            m_calendar = Calendar.getInstance(TimeZone.getDefault());
        } else {
            m_calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        }
        
        m_fieldPositions = new ArrayList<FieldPosition>();
        m_lastDate = new Date();

        init();
    }
   
    public Date getDate() {
        return (m_lastDate);
    }
    
    private FieldPosition getField(int caretLoc) {
        FieldPosition fieldPosition = null;
        for (final FieldPosition chkFieldPosition : m_fieldPositions) {
            if ((chkFieldPosition.getBeginIndex() <= caretLoc) && (chkFieldPosition.getEndIndex() > caretLoc)) {
                fieldPosition = chkFieldPosition;
                break;
            }
        }
        return (fieldPosition);
    }
    
    private FieldPosition getFieldPosition(int fieldNum) {
        FieldPosition result = null;
        for (final FieldPosition fieldPosition : m_fieldPositions) {
            if (fieldPosition.getField() == fieldNum) {
                result = fieldPosition;
                break;
            }
        }
        return (result);
    }
    
    private void getFieldPositions() {
        m_fieldPositions.clear();
        for (int ctr = 0; ctr < m_fieldTypes.length; ++ctr) {
            int fieldId = m_fieldTypes[ctr];
            FieldPosition fieldPosition = new FieldPosition(fieldId);
            StringBuffer formattedField = new StringBuffer();
            m_format.format(m_lastDate, formattedField, fieldPosition);
            if (fieldPosition.getEndIndex() > 0) {
                m_fieldPositions.add(fieldPosition);
            }
        }
        m_fieldPositions.trimToSize();
        Collections.sort(m_fieldPositions, new Comparator<FieldPosition>() {
            public int compare(FieldPosition o1, FieldPosition o2) {
                return ((o1).getBeginIndex() - (o2).getBeginIndex());
            }
        });
    }
    
    private FieldPosition getFirstField() {
        FieldPosition result = null;
        try {
            result = m_fieldPositions.get(0);
        } catch (NoSuchElementException ex) {
        }
        return (result);
    }
    
    private FieldPosition getLastField() {
        FieldPosition result = null;
        try {
            result = m_fieldPositions.get(m_fieldPositions.size() - 1);
        } catch (NoSuchElementException ex) {
        }
        return (result);
    }
    
    private FieldPosition getNextField(int caretLoc) {
        FieldPosition fieldPosition = null;
        for (final FieldPosition chkFieldPosition : m_fieldPositions) {
            if (chkFieldPosition.getBeginIndex() > caretLoc) {
                fieldPosition = chkFieldPosition;
                break;
            }
        }
        return (fieldPosition);
    }
    
    private FieldPosition getPrevField(int caretLoc) {
        FieldPosition fieldPosition = null;
        for (int ctr = m_fieldPositions.size() - 1; ctr > -1; --ctr) {
            FieldPosition chkFieldPosition = m_fieldPositions.get(ctr);
            if (chkFieldPosition.getEndIndex() <= caretLoc) {
                fieldPosition = chkFieldPosition;
                break;
            }
        }
        return (fieldPosition);
    }
    
    public int getTimeOrDateType() {
        return m_timeOrDateType;
    }
    
    private void init() {
        m_textField = this;
        m_caret = m_textField.getCaret();
        m_caret.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent evt) {
                setCurField();
            }
        });
        setupKeymap();
    }
    
    private void setCurField() {
        FieldPosition fieldPosition = getField(m_caret.getDot());
        if (fieldPosition != null) {
            if (m_caret.getDot() != fieldPosition.getBeginIndex()) {
                m_caret.setDot(fieldPosition.getBeginIndex());
            }
        } else {
            fieldPosition = getPrevField(m_caret.getDot());
            if (fieldPosition != null) {
                m_caret.setDot(fieldPosition.getBeginIndex());
            } else {
                fieldPosition = getFirstField();
                if (fieldPosition != null) {
                    m_caret.setDot(fieldPosition.getBeginIndex());
                }
            }
        }

        if (fieldPosition != null) {
            m_curField = fieldPosition.getField();
        } else {
            m_curField = -1;
        }
    }
    
    /**
     * Indicates the type of the underlying date.  The display format will be
     * set to the corresponding style.
     * @param formatCode One of DateTimeValueEntryField.RELATIVEDATE, DateTimeValueEntryField.RELATIVETIME,
     *                    DateTimeValueEntryField.RELATIVEDATETIME, or DateTimeValueEntryField.JTIME.
     */
    public void setFormat(int formatCode) {
        // Short-circuit return for unchanged format
        if(formatCode==m_timeOrDateType) {
            return;
        }
        
        switch(formatCode) {
            case RELATIVEDATE:
            case RELATIVETIME:
            case RELATIVEDATETIME:
                m_timeOrDateType = formatCode;
                m_calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                this.setDate(m_lastDate);
                break;
            case JTIME:
                m_timeOrDateType = formatCode;
                m_calendar = Calendar.getInstance(TimeZone.getDefault());
                this.setDate(m_lastDate);
                break;
            default:
                throw new IllegalArgumentException("Illegal format code "+formatCode+ " passed to DateTimeValueEntryField.setFormat");
        }
    }
    
    public void setDate(Date date) {
        setupFormat();
        getFieldPositions();
        m_lastDate = date;
        m_calendar.setTime(m_lastDate);
        m_textField.setText(m_format.format(m_lastDate));
        m_caret.setDot(0);
        setCurField();
        repaint();
    }
    
    protected void setupFormat() {
        switch (m_timeOrDateType) {
            case RELATIVETIME :
                m_format = RelativeTemporalValueNode.getDateFormat(-1, DateFormat.MEDIUM);
                break;
            case RELATIVEDATE :
                m_format = RelativeTemporalValueNode.getDateFormat(DateFormat.FULL, -1);
                break;
            case RELATIVEDATETIME :
                m_format = RelativeTemporalValueNode.getDateFormat(DateFormat.FULL, DateFormat.MEDIUM);
                break;
            case JTIME :
                m_format = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.LONG);
                m_format.setTimeZone(TimeZone.getDefault());
                break;
        }
    }
    
    protected void setupKeymap() {
        Keymap keymap = JTextComponent.addKeymap("DateTimeKeymap", null);
        keymap.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), m_upAction);
        keymap.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), m_downAction);
        keymap.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), new BackwardAction(DefaultEditorKit.backwardAction));
        keymap.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), new ForwardAction(DefaultEditorKit.forwardAction));
        keymap.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_HOME, 0), new BeginAction(DefaultEditorKit.beginAction));
        keymap.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_END, 0), new EndAction(DefaultEditorKit.endAction));
        m_textField.setKeymap(keymap);
    }

}
