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
 * ViewPreferencePanel.java
 * Creation date: Oct 30th 2002
 * By: Ken Wong
 */
package org.openquark.gems.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.KeyEventDispatcher;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.openquark.cal.services.LocaleUtilities;
import org.openquark.util.Pair;


/**
 * A specialized panel for handling the Intellicut preference settings.
 * Creation date: (25/06/01 2:40:23 PM)
 * @author Michael Cheng
 */
class IntellicutPreferencePanel extends JPanel {
    
    private static final long serialVersionUID = -2771401968220790644L;

    /** The checkbox for enabling/disabling the automatic intellicut popup. */
    private JCheckBox automaticPopupCheckBox = null;
    
    /** The slider for setting the popup timeout. */
    private JSlider popupDelaySlider = null;
    
    /**
     * Constructor for the IntellicutPreferencesPanel.
     */
    public IntellicutPreferencePanel() {
        initialize();
        
        boolean enabled = GemCutter.getPreferences().getBoolean(IntellicutManager.INTELLICUT_POPUP_ENABLED_PREF_KEY,
                                                                IntellicutManager.INTELLICUT_POPUP_ENABLED_DEFAULT);
        
        int delay = GemCutter.getPreferences().getInt(IntellicutManager.INTELLICUT_POPUP_DELAY_PREF_KEY,
                                                      IntellicutManager.INTELLICUT_POPUP_DELAY_DEFAULT);
        
        getAutomaticPopupCheckBox().setSelected(enabled);
        getPopupDelaySlider().setEnabled(enabled);
        getPopupDelaySlider().setValue(delay);  
    }
   
    /**
     * @return the check box for enabling/disabling the automatic intellicut popup
     */
    private JCheckBox getAutomaticPopupCheckBox() {
        
        if (automaticPopupCheckBox == null) {

            automaticPopupCheckBox = new JCheckBox();
            automaticPopupCheckBox.setText(GemCutter.getResourceString("PD_Enable_Automatic_Popup"));
            
            automaticPopupCheckBox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    getPopupDelaySlider().setEnabled(automaticPopupCheckBox.isSelected());
                }
            });
        }
        
        return automaticPopupCheckBox;
    }
    
    /**
     * @return the slider for setting the automatic intellicut timeout
     */
    private JSlider getPopupDelaySlider() {

        if (popupDelaySlider == null) {
            popupDelaySlider = new JSlider();
            popupDelaySlider.setToolTipText(GemCutter.getResourceString("PD_Automatic_Popup_Slider_ToolTip"));
            popupDelaySlider.setPaintLabels(true);
            popupDelaySlider.setPaintTicks(true);
            popupDelaySlider.setMajorTickSpacing(1);
            popupDelaySlider.setSnapToTicks(true);
            popupDelaySlider.setMaximum(10);
            popupDelaySlider.setMinimum(2);
            popupDelaySlider.setMinorTickSpacing(0);
        }
        
        return popupDelaySlider;
    }
    
    /**
     * Initializes the UI.
     */
    private void initialize() {

        setLayout(new BorderLayout());

        String title = GemCutter.getResourceString("PD_Intellicut_Preferences_Heading");
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), title));

        JPanel contents = new JPanel();
        contents.setLayout(new GridBagLayout());
        add(contents, BorderLayout.NORTH);

        GridBagConstraints constraints = new GridBagConstraints();

        constraints.gridx = 1;
        constraints.weightx = 1;
        constraints.insets = new Insets(5, 5, 5, 5);
        constraints.anchor = GridBagConstraints.WEST;

        contents.add(getAutomaticPopupCheckBox(), constraints);
        contents.add(new JLabel(GemCutter.getResourceString("PD_Automatic_Popup_Slider")), constraints);
        contents.add(getPopupDelaySlider(), constraints);
        contents.add(Box.createVerticalGlue(), constraints);
    }
    
    /**
     * Called when the user pressed the OK button on the preferences dialog.
     */
    public void okayButton_ActionEvents() {
        GemCutter.getPreferences().putBoolean(IntellicutManager.INTELLICUT_POPUP_ENABLED_PREF_KEY, getAutomaticPopupCheckBox().isSelected());
        GemCutter.getPreferences().putInt(IntellicutManager.INTELLICUT_POPUP_DELAY_PREF_KEY, getPopupDelaySlider().getValue());
    }
}


/**
 * A class to handle the various view preference settings that the user may choose to make. Currently,
 * this includes only choosing the background. (A limited selection there too!)
 * Creation Date: Oct 26th 2002
 * @author Ken Wong
 */
class ViewPreferencePanel extends JPanel {
    
    private static final long serialVersionUID = 3465297293053717245L;

    /** The panel containing the three selection radio buttons. */
    private final JPanel contentPanel;
    
    /** The button group for the radio button. */
    private final ButtonGroup buttonGroup = new ButtonGroup();
    
    private final GemCutter gemCutter;
    
    /**
     * The default constructor for View Preference Panel
     * @param gemCutter the GemCutter this panel is for
     */
    ViewPreferencePanel(GemCutter gemCutter) {
        
        this.gemCutter = gemCutter;

        setLayout(new BorderLayout());
        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createTitledBorder(GemCutter.getResourceString("PD_Background_SubHeading")));

        String fileName = GemCutter.getPreferences().get(GemCutter.BACKGROUND_FILE_NAME_PREF_KEY,
                                                         GemCutter.BACKGROUND_FILE_NAME_DEFAULT);
        
        // Add a radio button for every available background option.
        for (final Pair<String, String> bkground : GemCutter.backgrounds) {
            
            String name = bkground.fst();
            String background = bkground.snd();
            
            JRadioButton radioButton = new JRadioButton(name);
            radioButton.getModel().setActionCommand(background);
            
            if (fileName.equals(background)) {
                radioButton.setSelected(true);
            }
            
            buttonGroup.add(radioButton);
            contentPanel.add(radioButton);
            contentPanel.add(Box.createVerticalStrut(5));
        }
        
        add(contentPanel, BorderLayout.CENTER);
    }
    
    /**
     * The complete the okay actions for this panel
     */
    public void okayButton_ActionEvents() {

        ButtonModel selected = buttonGroup.getSelection();
        String fileName = selected.getActionCommand();

        GemCutter.getPreferences().put(GemCutter.BACKGROUND_FILE_NAME_PREF_KEY, fileName);
        
        gemCutter.resetBackground();
    }
}

/**
 * A specialized panel for handling the language preference settings.
 *
 * @author Joseph Wong
 */
class LanguagePreferencePanel extends JPanel {
    
    private static final long serialVersionUID = 1681754761225800347L;
    /** The language selection combo box. */
    private final JComboBox comboBox;
    
    /**
     * Represents an entry to be displayed in the language selection combo box.
     *
     * @author Joseph Wong
     */
    private static final class LanguageEntry implements Comparable<Object> {
        /** The locale represented by this language entry. */
        private final Locale locale;
        
        /**
         * Constructs a LanguageEntry.
         * @param locale the Locale value to be encapsulated by this instance.
         */
        private LanguageEntry(Locale locale) {
            if (locale == null) {
                throw new NullPointerException();
            }
            this.locale = locale;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            if (LocaleUtilities.isInvariantLocale(locale)) {
                return GemCutter.getResourceString("PD_Invariant_Locale_Name");
            } else {
                return locale.getDisplayName();
            }
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(Object o) {
            if (o instanceof LanguageEntry) {
                LanguageEntry other = (LanguageEntry)o;
                if (LocaleUtilities.isInvariantLocale(locale)) {
                    return LocaleUtilities.isInvariantLocale(other.locale);
                } else {
                    return locale.equals(other.locale);
                }
            }
            return false;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            return LocaleUtilities.isInvariantLocale(locale) ? 0 : locale.hashCode();
        }
        
        /**
         * {@inheritDoc}
         */
        public int compareTo(Object o) {
            if (o == null) {
                return 1;
            } else {
                // sorting order based on display name
                return this.toString().compareTo(o.toString());
            }
        }
    }
    
    /**
     * The default constructor for Language Preference Panel
     */
    LanguagePreferencePanel() {
        
        setBorder(BorderFactory.createTitledBorder(GemCutter.getResourceString("PD_Language_Preferences_Heading")));
        
        JPanel innerPanel = new JPanel();
        innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.X_AXIS));
        
        Locale[] standardLocales = Locale.getAvailableLocales();
        
        Locale currentLocale = GemCutter.getLocaleFromPreferences();
        boolean needCustomEntryForCurrentLocale = currentLocale != null && !Arrays.asList(standardLocales).contains(currentLocale); 
        
        LanguageEntry[] languageEntries = new LanguageEntry[standardLocales.length + (needCustomEntryForCurrentLocale ? 2 : 1)];
        
        languageEntries[0] = new LanguageEntry(LocaleUtilities.INVARIANT_LOCALE);
        for (int i = 0; i < standardLocales.length; i++) {
            languageEntries[i + 1] = new LanguageEntry(standardLocales[i]);
        }
        
        if (needCustomEntryForCurrentLocale) {
            languageEntries[languageEntries.length - 1] = new LanguageEntry(currentLocale);
        }
        
        Arrays.sort(languageEntries, 1, languageEntries.length);
        
        comboBox = new JComboBox(languageEntries);
        comboBox.setSelectedItem(new LanguageEntry(currentLocale));
        
        innerPanel.add(new JLabel(GemCutter.getResourceString("PD_Select_Language")));
        innerPanel.add(comboBox);
        innerPanel.add(Box.createGlue());
        
        add(innerPanel);
    }
    
    /**
     * Complete the okay actions for this panel.
     */
    public void okayButton_ActionEvents() {
        LanguageEntry entry = (LanguageEntry)comboBox.getSelectedItem();
        if (entry != null) {
            GemCutter.setLocaleToPreferences(entry.locale);
        }
    }
}

/**
 * PreferencesDialog is consolidated dialog that should handle all of the preference settings
 * available for the GemCutter (currently only View Preference and Intellicut Preferences
 * @author Ken Wong
 * Creation Date: Oct 31 2002
 */
class PreferencesDialog extends JDialog implements ActionListener {
    
    
    private static final long serialVersionUID = 5441296399270141016L;

    /** The main panel of the dialog. */
    private JPanel dialogContentPane = null;
    
    /** The panel handling the view preferences */
    private ViewPreferencePanel viewPreferencePanel = null;
    
    /** The panel handling the intellicut preferences */
    private IntellicutPreferencePanel intellicutPreferencePanel = null;

    /** The panel handling the language preferences */
    private LanguagePreferencePanel languagePreferencePanel = null;
    
    /** 
     * The split pane that separates the catgory list on the left from the pane of properties and
     * values on the right.
     */
    private JSplitPane categoryPropertySplitPane = null;
    
    /** The list of categories. */
    private JList categoryList = null;
    
    /* The Panel containing the ok and cancel buttons */
    private JPanel buttonPanel = null;
    private JButton okButton = null;
    private JButton cancelButton = null;
    
    private final GemCutter gemCutter;
    
    private boolean dialogAccepted = false;
        
    /** A key event dispatcher that watches for ESC key presses. */
    private KeyEventDispatcher cancelKeyEventDispatcher = null;
    
    /** The strings to use in the list of categories. */
    private static final String[] categoryTitles;
    
    /** A private class for representing a lazily instantiated property panel. For use with the propertyPanels array field. */
    private static abstract class PropertyPanelHolder {
        /** @return the encapsulated property panel. */
        abstract JPanel get();
    }
    
    /** 
     * An array holding the panels for the different property sections.  The position of each panel in 
     * the array co-insides with its associated category title in the category list.  
     * i.e. - category at index 2 of the list matches up with the panel at index 2.
     */
    private final PropertyPanelHolder[] propertyPanels;
    
    /** The property panel that is currently being displayed. */
    private JPanel currentPropertyPanel = null;
        
    // Set up the minimum dimensions for the dialog here
    private static final int MIN_DIALOG_WIDTH = 500;
    private static final int MIN_DIALOG_HEIGHT = 350;
    
    /** The colour to use for the backgrounds of fields that are not editable. */
    private static Color nonEditableColor;

    static {
        // The titles to display in the category list.
        categoryTitles = new String[] {
            GemCutter.getResourceString("PD_View_Preferences_Heading"),
            GemCutter.getResourceString("PD_Intellicut_Preferences_Heading"),
            GemCutter.getResourceString("PD_Language_Preferences_Heading")};
        
        // The background color to indicate a field is not editable.
        UIDefaults defaults = UIManager.getDefaults();
        nonEditableColor = (Color)defaults.get("TextField.inactiveBackground");
        if (nonEditableColor == null) {
            nonEditableColor = new Color(212, 208, 200);
        }        
    }
    
    /**
     * The constructor for the GemPropertiesDialog class.
     * @param gemCutter the GemCutter the preferences dialog is for
     */
    public PreferencesDialog(GemCutter gemCutter) {
        
        super(gemCutter);
        this.gemCutter = gemCutter;
        // Set up the property panels array and make sure that we have a panel for each category
        
        setName("PreferencesDialog");
        setTitle(GemCutter.getResourceString("PD_Dialog_Title"));
        
        propertyPanels = new PropertyPanelHolder[] {
            new PropertyPanelHolder() {
                @Override
                JPanel get() {
                    return getViewPreferencePanel();
                }},
            new PropertyPanelHolder() {
                @Override
                JPanel get() {
                    return getIntellicutPreferencePanel();
                }},
            new PropertyPanelHolder() {
                @Override
                JPanel get() {
                    return getLanguagePreferencePanel();
                }}};

        // Add a component listener that will maintain a minimum size for the dialog
        addComponentListener(new ComponentAdapter() {
            
            @Override
            public void componentResized(ComponentEvent e) {

                // Make sure the new size is not smaller than the minimum size
                Dimension dialogDim = getSize();
                if (dialogDim.width < MIN_DIALOG_WIDTH || dialogDim.height < MIN_DIALOG_HEIGHT) {
                    // The resizing has gone below the minimum so we need to resize up to the minimum
                    setSize(Math.max(dialogDim.width, MIN_DIALOG_WIDTH), Math.max(dialogDim.height, MIN_DIALOG_HEIGHT));
                } 

            }
        });

        // Add a key event dispatcher that will watch for ESC key events.
        cancelKeyEventDispatcher = new KeyEventDispatcher() {
            
            public boolean dispatchKeyEvent(java.awt.event.KeyEvent evt) {
                
                // Only worry if the event is an ESC key press (not release, etc) and the table is 
                // not editing
                if (evt.getID() == KeyEvent.KEY_PRESSED && evt.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE) {

                    evt.consume();
                    closeDialog(false);
                    return true;
                }
            
                return false;
           }
        };

        try {
            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            setSize(MIN_DIALOG_WIDTH, MIN_DIALOG_HEIGHT);
            setModal(true);
            setResizable(true);
            setContentPane(getDialogContentPane());

            // Get a string for the qualified name (module and name) of this gem and set the title.    
        
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    @Override
    public void show() {
        java.awt.KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(cancelKeyEventDispatcher);
        getViewPreferencePanel().setVisible(true);
        superDotShow();
    }
    
    /**
     * Call super.show() with warning suppressed.
     */
    @SuppressWarnings("deprecation")
    private void superDotShow() {
        super.show();
    }
    
    /**
    * Return the dialogContentPane property value.
    * Creation Date (02/14/2002 8:30:00 AM)
    * @return JPanel
    */
    private JPanel getDialogContentPane() {
        
        if (dialogContentPane == null) {
            try {
                dialogContentPane = new JPanel();
                dialogContentPane.setName("DialogContentPane");
                dialogContentPane.setLayout(new BorderLayout());    

                dialogContentPane.add(getCategoryPropertySplitPane(), "Center");
                dialogContentPane.add(getButtonPanel(), "South");
            
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return dialogContentPane;
    }
    
    /*
     * The panels of different categories of properties
     */
    
    
    /**
     * Returns the viewPreferencePanel, which currently only handles the background
     * @return ViewPreferencePanel;
     */
    private ViewPreferencePanel getViewPreferencePanel() {
        if (viewPreferencePanel == null) {
            viewPreferencePanel = new ViewPreferencePanel(gemCutter);
        }
        return viewPreferencePanel;
    }
    
    /**
     * Returns the intellicutPreferencePanel
     * @return IntellicutPreferencePanel;
     */
    IntellicutPreferencePanel getIntellicutPreferencePanel() {
        if (intellicutPreferencePanel == null) {
            intellicutPreferencePanel = new IntellicutPreferencePanel();
        }
        
        return intellicutPreferencePanel;
    }
    
    /**
     * @return the languagePreferencePanel.
     */
    private LanguagePreferencePanel getLanguagePreferencePanel() {
        if (languagePreferencePanel == null) {
            languagePreferencePanel = new LanguagePreferencePanel();
        }
        return languagePreferencePanel;
    }
    
    /**
     * Returns the split pane that divides the dialog vertically with the list of categories
     * on the left and the pane of properties and values on the right.
     * Creation date: (07/10/2002 2:38:00 PM).
     * @return JSplitPane
     */
    private JSplitPane getCategoryPropertySplitPane() {
        
        if (categoryPropertySplitPane == null) {
            try {
                // Build and set up the panels that will go in either side of the split pane.
                JScrollPane categoryScrollPane = new JScrollPane(getCategoryList());
                currentPropertyPanel = propertyPanels[0].get();
                
                // Actually build and set up the split pane.
                categoryPropertySplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, categoryScrollPane, currentPropertyPanel);
                categoryPropertySplitPane.setOneTouchExpandable(true);
                categoryPropertySplitPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
                int dividerSize = 6;
                categoryPropertySplitPane.setDividerSize(dividerSize); 
                
                // Base the divider location on the width of the category scroll pane plus a little extra.
                int width = categoryScrollPane.getPreferredSize().width;
                Border border = categoryScrollPane.getBorder();
                if (border != null) {
                    Insets insets = border.getBorderInsets(categoryScrollPane);
                    width += insets.left + insets.right;
                }
                categoryPropertySplitPane.setDividerLocation(width + dividerSize + 2);               

            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return categoryPropertySplitPane;
    }
    
    /**
     * Returns the list property for all the category titles.
     * Creation date: (07/10/2002 3:47:00 PM).
     * @return JList
     */
    private JList getCategoryList() {
        
        if (categoryList == null) {
           try {
                categoryList = new JList(categoryTitles);
                categoryList.setSelectedIndex(0);
                categoryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                
                // Add a listener that will update the property panel displayed on the other side of the
                // split pane.
                categoryList.addListSelectionListener(new ListSelectionListener() {
                    
                    public void valueChanged(ListSelectionEvent evt) {

                        if (evt.getValueIsAdjusting()) {
                            return;
                        }
                        
                        int index = ((JList)evt.getSource()).getSelectedIndex();
                        int dividerLoc = getCategoryPropertySplitPane().getDividerLocation();

                        currentPropertyPanel = propertyPanels[index].get();
                        getCategoryPropertySplitPane().setRightComponent(currentPropertyPanel);
                        getCategoryPropertySplitPane().setDividerLocation(dividerLoc);
                    }
                });
                
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return categoryList;
    }
    
    /**
     * Return the buttonPanel property value.
     * Creation Date (02/14/2002 8:30:00 AM).
     * @return JPanel
     */
    private JPanel getButtonPanel() {
        
        if (buttonPanel == null) {
            try {
                buttonPanel = new JPanel();
                buttonPanel.setLayout(new BorderLayout());
                buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 0));
                buttonPanel.setName("ButtonPanel");
                          
                // Create the panel
                JPanel buttonPanel2 = new JPanel();

                // Actually add the buttons here
                
                buttonPanel2.setLayout(new FlowLayout(FlowLayout.RIGHT, 20, 0));
                buttonPanel2.add(getOKButton());
                buttonPanel2.add(getCancelButton()); 
                buttonPanel.add(buttonPanel2, BorderLayout.EAST);
                        
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return buttonPanel;
    }
    
    /**
     * Returns the OK button property value.
     * Creation date: (03/13/2002 2:57:00 PM).
     * @return JButton
     */
    private JButton getOKButton() {
        
        if (okButton == null) {
            try {
                okButton = new JButton(GemCutter.getResourceString("PD_OK_Button"));
                okButton.setActionCommand("OK");
                okButton.addActionListener(this);
                okButton.setMnemonic(KeyEvent.VK_S);
                okButton.setDefaultCapable(true);
                getRootPane().setDefaultButton(getOKButton()); 
                            
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return okButton;
    }
    
    /**
     * Returns the Cancel button property value.
     * Creation date: (03/20/2002 12:35:00 PM).
     * @return Steve Norton
     */
    private JButton getCancelButton() {
        
        if (cancelButton == null) {
            try {
                cancelButton = new JButton(GemCutter.getResourceString("PD_Cancel_Button"));
                cancelButton.setActionCommand("Cancel");
                cancelButton.addActionListener(this);
                cancelButton.setMnemonic(KeyEvent.VK_C);
                
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return cancelButton;
    }
    
    /**
     * Returns true if the Ok button was pressed or false otherwise.
     * Creation Date (02/14/2002 8:30:00 AM).
     * @return boolean 
     */
    public boolean isDialogAccepted() {
        
        return dialogAccepted;
    }
    
    /**
     * Invoked when the Ok or Cancel buttons are pressed.
     * Creation Date (02/14/2002 8:30:00 AM).
     */
    public void actionPerformed(java.awt.event.ActionEvent evt) {
        
        if (evt.getActionCommand().equals("OK")) {
            closeDialog(true);
        } else if (evt.getActionCommand().equals("Cancel")) { 
            closeDialog(false);
        } else {
            throw new IllegalArgumentException("No action to take for event: " + evt);
        }
    }
    
    /**
     * Close this dialog.  If the argument value is true then the new properties will be saved to the collector gem.
     * Creation date: (06/24/2002 9:40:00 AM).
     * @param isAccepted boolean true if the dialog has been accepted (OK button pressed), false otherwise.
     */
    private void closeDialog(boolean isAccepted) {
        
        dialogAccepted = isAccepted;
        
        // If the dialog has been accepted update the properties map with the values in the editable components
        // in case their values have been changed.
        if (isAccepted) {
            // Stuff Here
            getIntellicutPreferencePanel().okayButton_ActionEvents();
            getViewPreferencePanel().okayButton_ActionEvents();
            getLanguagePreferencePanel().okayButton_ActionEvents();
            gemCutter.repaint();
           
        }
        
        // Remove the key event dispatcher
        java.awt.KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(cancelKeyEventDispatcher);
        setVisible(false);
    }

    /**
     * Called whenever an exception is thrown.
     * Creation Date (02/14/2002 8:30:00 AM).
     * @param exception java.lang.Throwable
     */
    void handleException(java.lang.Throwable exception) {

        /* Uncomment the following lines to print uncaught exceptions to stdout */
         System.out.println("--------- UNCAUGHT EXCEPTION ---------");
         exception.printStackTrace(System.out);
    }
}
