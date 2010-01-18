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
 * RelativeDateValueEditor.java
 * Creation date: September 22, 2006
 * By: Neil Corkum
 */
package org.openquark.gems.client.valueentry;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultFormatter;

import org.openquark.cal.valuenode.RelativeDateValueNode;
import org.openquark.cal.valuenode.ValueNode;

import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.TimeZone;

/**
 * A RelativeDateValueEditor displays a calendar-like window which allows the user
 * to select a date.
 * 
 * @author Neil Corkum
 */
class RelativeDateValueEditor extends ValueEditor {

    private static final long serialVersionUID = 8737584309664754353L;

    /**
     * A custom value editor provider for the RelativeDateValueEditor.
     */
    public static class RelativeDateValueEditorProvider extends ValueEditorProvider<RelativeDateValueEditor> {
        public RelativeDateValueEditorProvider(ValueEditorManager valueEditorManager) {
            super(valueEditorManager);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean canHandleValue(ValueNode valueNode, SupportInfo providerSupportInfo) {
            return valueNode instanceof RelativeDateValueNode;
        }
        
        /**
         * @see org.openquark.gems.client.valueentry.ValueEditorProvider#getEditorInstance(ValueEditorHierarchyManager, ValueNode)
         */
        @Override
        public RelativeDateValueEditor getEditorInstance(ValueEditorHierarchyManager valueEditorHierarchyManager,
                                             ValueNode valueNode) {
            
            RelativeDateValueEditor editor = new RelativeDateValueEditor(valueEditorHierarchyManager);
            editor.setOwnerValueNode(valueNode);
            return editor;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public boolean usableForOutput() {
            return false;
        }
    }
    
    /**
     * Key listener for the buttons in this value editor. 
     * Allows use of keyboard arrows to move around the date portion of 
     * the calendar.
     * Gives the enter key the same effect as the space bar (presses current button).
     * 
     * 
     * @author Neil Corkum
     */
    private class RelativeDateValueEditorKeyListener extends ValueEditorKeyListener {
        @Override
        public void keyPressed(KeyEvent evt) {
            int keyCode = evt.getKeyCode();
         
            // check for escape pressed
            if (keyCode == KeyEvent.VK_ESCAPE) {
                // close editor without saving changes, unless month selector
                // popup menu is open, in which case do nothing (the combo box
                // will handle closing the popup menu
                if (!monthSelector.isPopupVisible()) {
                    handleCancelGesture();
                    evt.consume();
                }
                return;
            }
            
            // if enter pressed exit and save changes
            if (keyCode == KeyEvent.VK_ENTER) {
                handleCommitGesture();
                evt.consume();
                return;
            }
                      
            Component component = evt.getComponent();
            
            if (component instanceof JToggleButton) {
                JToggleButton button = (JToggleButton)component;

                int deltaDays; // difference between currently focused day and new day

                // check for arrow keys pressed
                if (keyCode == KeyEvent.VK_DOWN) {
                    deltaDays = DAYS_IN_WEEK;                   
                } else if (keyCode == KeyEvent.VK_UP) {
                    deltaDays = -DAYS_IN_WEEK;
                } else if (keyCode == KeyEvent.VK_LEFT) {
                    deltaDays = -1;
                } else if (keyCode == KeyEvent.VK_RIGHT) {
                    deltaDays = 1;
                } else {
                    // no relevant key pressed; do nothing
                    return;
                }
                
                // We must get the currently focused button's date, then convert
                // to a Calendar and increment/decrement the Calendar by a certain
                // number of days. The new day is then obtained from the calendar.
                // Just using an integer and adding/subtracting will not work for
                // a month in which each day is not one greater than the day before
                // it (eg. October 1582, in the Gregorian calendar)
                int day = dayButtons.indexOf(button) + 1;
                Calendar calendar = getUtcCalendar();
                calendar.set(getDisplayedYear(), getDisplayedMonth(), day);
                
                // Add the day delta.  Do nothing if it changes the month.
                calendar.add(Calendar.DAY_OF_MONTH, deltaDays);
                
                if (calendar.get(Calendar.MONTH) == getDisplayedMonth()) {
                    int newDay = calendar.get(Calendar.DAY_OF_MONTH);
                    JToggleButton newFocus = dayButtons.get(newDay - 1);
                    newFocus.doClick(0);
                }
            }
        }
    }
    
    /**
     * Listener for action events from the date buttons and the month selector
     * combo box. 
     * 
     * @author Neil Corkum
     */
    private class RelativeDateValueEditorActionListener implements ActionListener {
        /**
         * {@inheritDoc}
         */
        public void actionPerformed(ActionEvent e) {
            // check if month changed
            if (e.getActionCommand().equals(monthSelector.getActionCommand())) {
                // update selected date so that it is in the month selected
                // use roll instead of set because roll handles going from 
                // May 31 to June 30, for example, while set would go to July 1
                int oldMonth = selectedDate.get(Calendar.MONTH);
                selectedDate.roll(Calendar.MONTH, getDisplayedMonth() - oldMonth);
  
                updateCalendar();
            } else {
                // date was changed
                // The action command from a button is a string representing 
                // the date the button shows.
                // Get this date and use it to set the new date.
                try {
                    int dateSelected = Integer.parseInt(e.getActionCommand());
                    Calendar newDate = getUtcCalendar();
                    newDate.set(getDisplayedYear(), getDisplayedMonth(), dateSelected);
                    setCalendar(newDate);            
                } catch (NumberFormatException exc) {
                
                }
            }
        }
    }
    
    /**
     * Listener for change events from the year selector combo box. 
     * @author Neil Corkum
     */
    private class RelativeDateValueEditorChangeListener implements ChangeListener {
        /**
         * {@inheritDoc}
         */
        public void stateChanged(ChangeEvent e) {
            // update selected date so that it is in the new selected year
            // use add instead of set because add handles going from 
            // Feb 29 one leap year to Feb 28 the next year, whereas set will
            // say March 1
            int oldYear = selectedDate.get(Calendar.YEAR);
            selectedDate.add(Calendar.YEAR, getDisplayedYear() - oldYear);

            updateCalendar();
        }
    }

    /** Minimum year displayable and selectable in editor. */
    private static final int MINIMUM_YEAR = 1;
    
    /** Days in week */
    private static final int DAYS_IN_WEEK = 7;
    
    /** Months in year */
    private static final int MONTHS_IN_YEAR = 12;

    /** Combo box holding list of months. */
    private JComboBox monthSelector;
    
    /** Spinner field displaying year. */
    private JSpinner yearSelector;
    
    /** List of buttons usable in UI. */
    private List <JToggleButton> dayButtons;
    
    /** Panel holding layout of buttons representing days in month. */
    private JPanel calendarPanel;
    
    /** Date currently selected. */
    private Calendar selectedDate;
    
    /**
     * Constructor for RelativeDateValueEditor. 
     * @param valueEditorHierarchyManager
     */
    protected RelativeDateValueEditor(ValueEditorHierarchyManager valueEditorHierarchyManager) {
        super(valueEditorHierarchyManager);
        initialize();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void commitValue() {
        
        // Update the value in the ValueNode.
        ValueNode returnVN = new RelativeDateValueNode(getCalendar().getTime(), getValueNode().getTypeExpr());
        replaceValueNode(returnVN, false);

        notifyValueCommitted();
    }

    /** 
     * {@inheritDoc}
     */
    @Override
    public Component getDefaultFocusComponent() {
        return monthSelector;
    }

    /** 
     * {@inheritDoc}
     */
    @Override
    public void setInitialValue() {
        Calendar calendar = ((RelativeDateValueNode)getValueNode()).getCalendarValue();
        setCalendar(calendar);
    }
    
    /**
     * Function to initialize the editor.
     */
    private void initialize() {
        // set locale
        // TODO: get locale from GemCutter instead of using default
        Locale locale = Locale.getDefault();
        setLocale(locale);
        
        // create listeners
        RelativeDateValueEditorKeyListener keyListener = new RelativeDateValueEditorKeyListener();
        RelativeDateValueEditorActionListener actionListener = new RelativeDateValueEditorActionListener();
        RelativeDateValueEditorChangeListener changeListener = new RelativeDateValueEditorChangeListener();
        
        selectedDate = getUtcCalendar();  // set to current date
        int maxDaysInMonth = selectedDate.getMaximum(Calendar.DAY_OF_MONTH);
        
        // set up year selector spinner field
        SpinnerNumberModel numberModel = new SpinnerNumberModel();
        numberModel.setMinimum(Integer.valueOf(MINIMUM_YEAR));
        numberModel.setMaximum(Integer.valueOf(selectedDate.getActualMaximum(Calendar.YEAR)));
        yearSelector = new JSpinner(numberModel);
        yearSelector.setLocale(locale);
        yearSelector.setEditor(new JSpinner.NumberEditor(yearSelector, "0"));
        
        // set up month selector field
        monthSelector = new JComboBox(getMonthStrings());
        monthSelector.setLocale(locale);

        // initialize buttons for all possible days in month
        dayButtons = new ArrayList<JToggleButton>(maxDaysInMonth);
        for (int day = 1; day <= maxDaysInMonth; day++) {
            JToggleButton button = new JToggleButton(getDayString(day));
            button.setLocale(locale);
            
            // set buttons' action command to a string containing the date they represent
            button.setActionCommand(String.valueOf(day));
            button.addActionListener(actionListener);
            button.addKeyListener(keyListener);
            dayButtons.add(button);
        }
        
        // initialize panels
        JPanel monthPanel = new JPanel();
        JPanel yearPanel = new JPanel();
        JPanel weekdayLabelPanel = new JPanel();
        calendarPanel = new JPanel();

        // set up month panel
        monthPanel.setLayout(new BoxLayout(monthPanel, BoxLayout.X_AXIS));
        monthPanel.add(monthSelector);
        
        // set up year panel
        yearPanel.setLayout(new BoxLayout(yearPanel, BoxLayout.X_AXIS));
        yearPanel.add(yearSelector);
        
        // set up calendar panel
        GridLayout calendarLayout = new GridLayout(0, DAYS_IN_WEEK);
        calendarPanel.setLayout(calendarLayout);   
        
        // set up weekday label panel
        weekdayLabelPanel.setLayout(new GridLayout(1, DAYS_IN_WEEK));
        String[] weekdayStrings = getWeekdayStrings();
        for (int i = 0; i < DAYS_IN_WEEK; i++) {
            JLabel weekday = new JLabel(weekdayStrings[i], SwingConstants.CENTER);
            weekday.setLocale(locale);
            weekdayLabelPanel.add(weekday);
        }
        
        // set up main panel
        GridBagLayout gridBag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        
        setLayout(gridBag);
        
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1.0;

        gridBag.addLayoutComponent(monthPanel, constraints);
        add(monthPanel);
        
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBag.addLayoutComponent(yearPanel, constraints);
        add(yearPanel);
        
        gridBag.addLayoutComponent(weekdayLabelPanel, constraints);
        add(weekdayLabelPanel);
        
        gridBag.addLayoutComponent(calendarPanel, constraints);
        add(calendarPanel);   
        
        // set up calendar to display current date
        setDisplayedMonth(selectedDate.get(Calendar.MONTH));
        setDisplayedYear(selectedDate.get(Calendar.YEAR));
        updateCalendar();
        
        // set up listeners 
        monthSelector.addActionListener(actionListener);
        monthSelector.addKeyListener(keyListener);
        
        yearSelector.addChangeListener(changeListener);
        
        JComponent yearEditor = yearSelector.getEditor();
        if (yearEditor instanceof JSpinner.DefaultEditor) {
            final JFormattedTextField yearTextField = ((JSpinner.DefaultEditor)yearEditor).getTextField();
            // add listener to text field of year selector spinner
            yearTextField.addKeyListener(keyListener);
            
            // listener to select year text when spinner is "spun" 
            PropertyChangeListener spinListener = new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent e) {
                    yearTextField.selectAll();
                }
            };
            yearTextField.addPropertyChangeListener("value", spinListener);
            
            // make calendar update whenever an edit is made to the year selection
            JFormattedTextField.AbstractFormatter formatter = yearTextField.getFormatter();
            if (formatter instanceof DefaultFormatter) {
                DefaultFormatter defaultFormatter = (DefaultFormatter)formatter;
                defaultFormatter.setCommitsOnValidEdit(true);
                defaultFormatter.setAllowsInvalid(false);
            }
        }
        
        // set visible size of this editor
        setSize(getPreferredSize());
    }

    /**
     * Gets the names of all months in the locale.
     * @return array of strings representing each month 
     */
    private String[] getMonthStrings() {
        String[] months = new String[MONTHS_IN_YEAR];
        String[] allMonths = new DateFormatSymbols(getLocale()).getMonths();
        for (int month = 0; month < MONTHS_IN_YEAR; month++) {
            months[month] = allMonths[month];
        }
        return months;
    }
    
    /**
     * Gets the names of all weekdays in the locale.
     * The names are ordered in the correct order for the locale.
     * @return array of weekday names
     */
    private String[] getWeekdayStrings() {
        String[] orderedNames = new String[DAYS_IN_WEEK];
        String[] names = new DateFormatSymbols(getLocale()).getShortWeekdays();
        
        // set day names in proper order for locale
        int day = selectedDate.getFirstDayOfWeek();
        for (int i = 0; i < DAYS_IN_WEEK; i++) {
            orderedNames[i] = names[day];
            day = (day % DAYS_IN_WEEK) + 1; // day value is 1-based
        }
        
        return orderedNames;
    }
    
    /**
     * Gets a string representing a given day of the month
     * @param date int value of date
     * @return corresponding to date
     */
    private String getDayString(int date) {
        return String.valueOf(date);
    }
    
    /**
     * Updates calendar display and buttons to match month and year displayed.
     */
    private void updateCalendar() {
        calendarPanel.invalidate();
        calendarPanel.removeAll();
        
        Calendar firstDayOfMonth = getUtcCalendar();
        
        int displayedMonth = getDisplayedMonth();
        int displayedYear = getDisplayedYear();
        firstDayOfMonth.set(displayedYear, displayedMonth, 1);
        int firstDayOfWeek = firstDayOfMonth.getFirstDayOfWeek();     
        int firstDayOfWeekOfThisMonth = firstDayOfMonth.get(Calendar.DAY_OF_WEEK);
        List<Integer> daysInMonth = getDaysInMonth(displayedYear, displayedMonth);
        
        // calculate the number of blank spaces to leave at start of grid that will contain calendar
        int blankStartDays = firstDayOfWeekOfThisMonth - firstDayOfWeek;
        if (blankStartDays < 0) {
            blankStartDays += DAYS_IN_WEEK;
        }
        
        // insert blank sections in grid
        for (int i = 0; i < blankStartDays; i++) {
            calendarPanel.add(getCalendarSpacerComponent());
        }
        
        // Need calendar to validate its internal fields so that no dates past
        // the end of the "valid" range are accepted.
        // This is a grotesque hack
        // add method seems to make calendar validate itself
        selectedDate.add(Calendar.MILLISECOND, -1);
        selectedDate.add(Calendar.MILLISECOND, 1);

        // add buttons to grid
        for (final Integer dayInMonth : daysInMonth) {
            int date = dayInMonth.intValue();
            int dateSelected = selectedDate.get(Calendar.DAY_OF_MONTH);
            
            JToggleButton button = (dayButtons.get(date - 1));
            
            // check for selected date button
            // set only the selected button to be focusable, this makes using 
            // the TAB key to change focus work more intuitively
            if (date == dateSelected) {
                button.setSelected(true);
                button.setFocusable(true);
                button.setText("<html><b><u>" + getDayString(date) + "</u></b></html>");
            } else {
                button.setSelected(false);
                button.setFocusable(false);
                button.setText("<html>" + getDayString(date) + "</html>");
            }
            calendarPanel.add(button);
        }

        // add extra blank sections to ensure that grid is always 6 rows of dates
        final int necessaryGridComponents = DAYS_IN_WEEK * 5 + 1;
        for (int componentsInGrid = blankStartDays + daysInMonth.size(); 
             componentsInGrid < necessaryGridComponents;
             componentsInGrid++) {
            calendarPanel.add(getCalendarSpacerComponent());
        }

        // redraw calendar panel
        calendarPanel.validate();
        calendarPanel.repaint();
    }
    
    /**
     * Function to set the selected date
     */
    public void setCalendar(Calendar date) {
        selectedDate = date;
        setDisplayedMonth(date.get(Calendar.MONTH));
        setDisplayedYear(date.get(Calendar.YEAR));
        updateCalendar();
        
        // give focus to selected date button
        int day = selectedDate.get(Calendar.DAY_OF_MONTH);
        JToggleButton button = dayButtons.get(day - 1);
        button.requestFocus();
    }
    
    /**
     * Gets the month displayed
     * @return the month currently displayed in the window
     */
    private int getDisplayedMonth() {
        return monthSelector.getSelectedIndex();
    }
    
    /**
     * Gets the year displayed
     * @return the year currently displayed in the window
     */
    private int getDisplayedYear() {
        return ((Integer)yearSelector.getValue()).intValue();
    }
    
    /**
     * Sets the month displayed
     * @param month the month
     */
    private void setDisplayedMonth(int month) {
        monthSelector.setSelectedIndex(month);
    }
    
    /**
     * Sets the year displayed
     * @param year the year
     */
    private void setDisplayedYear(int year) {
        yearSelector.setValue(Integer.valueOf(year));
    }
    
    /**
     * Gets a list of days in the selected month.
     * @param year the year
     * @param month the month
     * @return List of dates in month
     */
    private List<Integer> getDaysInMonth(int year, int month) {
        // set up calendar to first day of month
        Calendar calendar = getUtcCalendar();
        calendar.clear();
        calendar.set(year, month, 1);
        
        ArrayList<Integer> dayList = new ArrayList<Integer>(31);
        
        // lastCalendar is used to check the situation where the calendar hits
        // its maximum value and can no longer be increased
        Calendar lastCalendar = (Calendar)calendar.clone();
        lastCalendar.add(Calendar.DAY_OF_MONTH, -1);
        
        // increase date and add to list until the next month is reached
        while ((calendar.get(Calendar.MONTH) == month) && !calendar.equals(lastCalendar)) {
            
            dayList.add(Integer.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            lastCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        
        return dayList;
    }
    
    /**
     * Gets the value of the selected date
     * @return the selected value in the editor
     */
    public Calendar getCalendar() {
        return selectedDate;
    }
    
    /**
     * Gets a Calendar instance with the current locale and UTC time zone.
     * @return Calendar instance
     */
    private Calendar getUtcCalendar() {
        return Calendar.getInstance(TimeZone.getTimeZone("UTC"), getLocale());
    }
    
    /**
     * Gets an instance of an invisible component that can be used as a spacer.
     * Used in the calendar grid to fill in the spaces before and after actual
     * days in the month.
     * @return Invisible spacer component
     */
    private Component getCalendarSpacerComponent() {
        Component glue = Box.createGlue();
        glue.setVisible(false);
        return glue;
    }
}
