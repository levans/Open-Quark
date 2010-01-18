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
 * NavEditorSection.java
 * Creation date: Jul 9, 2003
 * By: Frank Worsley
 */
package org.openquark.gems.client.navigator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import org.openquark.cal.compiler.CodeQualificationMap;
import org.openquark.cal.compiler.LanguageInfo;
import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.ModuleTypeInfo;
import org.openquark.cal.compiler.ScopedEntityNamingPolicy;
import org.openquark.cal.compiler.ScopedEntityNamingPolicy.UnqualifiedUnlessAmbiguous;
import org.openquark.cal.metadata.ArgumentMetadata;
import org.openquark.cal.metadata.CALExample;
import org.openquark.cal.metadata.CALExpression;
import org.openquark.cal.metadata.CALFeatureMetadata;
import org.openquark.cal.metadata.ClassMethodMetadata;
import org.openquark.cal.metadata.FunctionMetadata;
import org.openquark.cal.metadata.FunctionalAgentMetadata;
import org.openquark.cal.metadata.InstanceMethodMetadata;
import org.openquark.cal.services.CALFeatureName;
import org.openquark.util.Pair;
import org.openquark.util.UnsafeCast;


/**
 * This class implements the basis for a metadata editor section. An editor section
 * is a component that contains one or more editor components that allow metadata to
 * be edited. Editor sections can be expanded or collapsed using an expander widget
 * at the top of the section. Editor sections for specific metadata attributes derive
 * from this base class.
 * 
 * @author Frank Worsley
 */
abstract class NavEditorSection extends JPanel {

    /** The color to use as the background when a section has focus. */
    static final Color FOCUS_COLOR = Color.BLUE.darker().darker();
    
    /** The color to use as the background when a section has an error. */
    static final Color ERROR_COLOR = Color.RED.darker();
    
    /** The color to use for the background for the default state. */
    static final Color NORMAL_COLOR = Color.LIGHT_GRAY;
    
    /** The background color for the error labels. */
    private static final Color ERROR_LABEL_COLOR = new Color(255, 255, 200); 
   
    /** The outside border for the error labels. */
    private static final Border ERROR_LABEL_OUTSIDE_BORDER = BorderFactory.createLineBorder(Color.GRAY, 1);
    
    /** The inside border for the error labels. */
    private static final Border ERROR_LABEL_INSIDE_BORDER = BorderFactory.createEmptyBorder(1, 2, 2, 2);
    
    /** The border for the error labels. */
    private static final Border ERROR_LABEL_BORDER = BorderFactory.createCompoundBorder(ERROR_LABEL_OUTSIDE_BORDER, ERROR_LABEL_INSIDE_BORDER);
   
    /** The icon for the error labels. */
    private static final ImageIcon ERROR_LABEL_ICON = new ImageIcon(NavEditorSection.class.getResource("/Resources/error.gif"));

    /**
     * The map that stores the label used to display the title for an editor. 
     */
    private final Map<NavEditorComponent<?>, JLabel> editorToTitleMap = new HashMap<NavEditorComponent<?>, JLabel>();
   
    /**
     * The map that stores the label used to display error message for an editor.
     */
    private final Map<NavEditorComponent<?>, JLabel> editorToErrorMap = new HashMap<NavEditorComponent<?>, JLabel>();
    
    /**
     * The map that stores the editors with unique keys used in this section.
     */
    private final Map<String, NavEditorComponent<?>> keyToEditorMap = new HashMap<String, NavEditorComponent<?>>();
    
    /**
     * The list of editors in this section in the same order they were added.
     */
    private final List<NavEditorComponent<?>> allEditors = new ArrayList<NavEditorComponent<?>>();

    /** The panel that holds the various editing components. */
    private final JPanel contentPanel = new JPanel();
    
    /** The expander used by this section. */
    private final NavExpander expander;
    
    /** The title of the editor section. */
    private final String title;

    /** The editor panel that is using this editor section. */
    private final NavEditorPanel editorPanel;
    
    /** A label to display placeholder messages in empty sections. */
    private final JLabel placeHolderLabel = new JLabel();

    /** Whether or not this section has the focused look. */
    private boolean hasFocus = false;
    
    /** Whether or not this section has the error look. */ 
    private boolean hasErrors = false;

    /**
     * Constructor for a new editor section.
     * @param editorPanel the editor panel the section belongs to
     * @param title the title of the section
     */
    public NavEditorSection(NavEditorPanel editorPanel, String title) {

        if (editorPanel == null) {
            throw new NullPointerException();
        }

        this.editorPanel = editorPanel;
        this.title = title;
        
        setLayout(new BorderLayout());
        
        expander = new NavExpander(this);
        expander.setExpanded(false);
        add(expander, BorderLayout.NORTH);
        
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setLayout(new GridBagLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(contentPanel, BorderLayout.CENTER);

        setMaximumSize(new Dimension(5000, 1));
        setFocusable(true);
        setFocusedLook(false);
        
        // Add a mouse listener that requests focus when the section is clicked.
        contentPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                requestFocusInWindow();
            }
        });

        // Add a key listener to expand/collapse the section
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    boolean expanded = expander.isExpanded();
                    setExpanded(!expanded);
                }
            }
        });
    }
    
    /**
     * @return the title of the editor section
     */
    String getTitle() {
        return title;
    }
    
    /**
     * @return the editor panel that is using this editor section
     */
    NavEditorPanel getEditorPanel() {
        return editorPanel;
    }
    
    /**
     * @return the content panel of this editor section
     */
    JPanel getContentPanel() {
        return contentPanel;
    }
    
    /**
     * @return the expander used by this editor section
     */
    NavExpander getExpander() {
        return expander;
    }

    /**
     * Convenience method to get the metadata object being edited.
     * @return the metadata object being edited
     */
    CALFeatureMetadata getMetadata() {
        return getEditorPanel().getMetadata();
    }
    
    /**
     * Convenience method to return the address of the entity whose metadata is being edited.
     * @return the address of the entity whose metadata is being edited
     */
    NavAddress getAddress() {
        return getEditorPanel().getAddress();
    }
    
    /**
     * @return the set of editors used in this section
     */
    List<NavEditorComponent<?>> getEditors() {
        return new ArrayList<NavEditorComponent<?>>(allEditors);
    }
    
    /**
     * @param key the key of the editor
     * @return the editor with the given key
     */
    NavEditorComponent<?> getEditor(String key) {
        
        if (!keyToEditorMap.containsKey(key)) {
            throw new IllegalArgumentException("no editor with the given key: " + key);
        }
        
        return keyToEditorMap.get(key);
    }
    
    /**
     * @param key the key of an editor.
     * @return true if this section contains an editor with the given key.
     */
    boolean containsEditor(String key) {
        return keyToEditorMap.containsKey(key);
    }
    
    /**
     * Adds a new editor component to the section.
     * @param editor the editor component to add
     */
    void addEditor(NavEditorComponent<?> editor) {

        // Remove the placeholder label since there now is an actual
        // editor to fill the section.
        if (placeHolderLabel.getParent() != null) {
            contentPanel.remove(placeHolderLabel);
        }
        
        allEditors.add(editor);
        if (editor.getKey() != null) {
            keyToEditorMap.put(editor.getKey(), editor);
        }

        // Setup the basic constraints        
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.WEST;

        constraints.gridx = 0;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.insets = new Insets(5, 5, 2, 5);

        // Add the error label for the editor
        JLabel errorLabel = new JLabel();
        errorLabel.setBackground(ERROR_LABEL_COLOR);
        errorLabel.setBorder(ERROR_LABEL_BORDER);
        errorLabel.setIcon(ERROR_LABEL_ICON);
        errorLabel.setOpaque(true);
        errorLabel.setVisible(false);
        editorToErrorMap.put(editor, errorLabel);
        contentPanel.add(errorLabel, constraints);

        constraints.gridx = 0;
        constraints.gridwidth = 1;
        constraints.insets = new Insets(5, 5, 5, 5);

        // Only add a title label if the editor has a title
        if (editor.getTitle() != null) {
            
            JLabel titleLabel = new JLabel(editor.getTitle());
            titleLabel.setToolTipText(editor.getDescription());
            
            contentPanel.add(titleLabel, constraints);
            editorToTitleMap.put(editor, titleLabel);
            
            // Update the constraints to take into account the title label
            constraints.gridx = 1;
        }

        // Add the actual editor
        constraints.weightx = 1;
        contentPanel.add(editor.getEditorComponent(), constraints);
        
        // For some reason we need to validate the editor panel's parent if
        // we add or remove components after the section is visible. Simply
        // validating the section or editor panel is not enough.
        if (getEditorPanel().getParent() != null) {
            getEditorPanel().getParent().validate();
        }
    }

    /**
     * Removes the given editor from the section.
     * @param editor the editor to remove
     */
    void removeEditor(NavEditorComponent<?> editor) {

        // remove editor from the editor set
        allEditors.remove(editor);
        if (editor.getKey() != null) {
            keyToEditorMap.remove(editor.getKey());
        }

        // remove editor and error + title components
        contentPanel.remove(editor.getEditorComponent());
        contentPanel.remove(editorToErrorMap.remove(editor));
        
        if (editor.getTitle() != null) {
            contentPanel.remove(editorToTitleMap.remove(editor));
        }
        
        maybeAddPlaceHolderLabel();

        if (getEditorPanel().getParent() != null) {
            getEditorPanel().getParent().validate();
        }
    }
    
    /**
     * Removes all editors from the section.
     */
    void removeAllEditors() {
        
        contentPanel.removeAll();
        
        allEditors.clear();
        keyToEditorMap.clear();

        maybeAddPlaceHolderLabel();

        if (getEditorPanel().getParent() != null) {
            getEditorPanel().getParent().validate();
        }
    }
    
    /**
     * Called when the value stored by the given editor has changed.
     * @param editor the editor whose value changed
     */
    void editorChanged(NavEditorComponent<?> editor) {
        editorPanel.sectionChanged(this);
        doValidate();
    }
    
    /**
     * @param key the key of the editor whose value to fetch
     * @return the value stored by the editor component. This may be null if the
     * editor is storing a null value.
     */
    Object getEditorValue(String key) {
        NavEditorComponent<?> editor = keyToEditorMap.get(key);
        
        if (editor == null) {
            throw new IllegalArgumentException("no editor with the given key");
        }
        
        return editor.getValue();
    }

    /**
     * Sets the value of the editor with the given key.
     * @param key the key of the editor
     * @param value the new value for the editor
     */    
    <T> void setEditorValue(String key, Object value) {
        NavEditorComponent<T> editor = UnsafeCast.unsafeCast(keyToEditorMap.get(key));
        
        if (editor == null) {
            throw new IllegalArgumentException("no editor with the given key");
        }
        
        editor.setValue(UnsafeCast.<T>unsafeCast(value));               // ~ unsafe
    }

    /**
     * Sets the focused look of a section. If the section is focused its border
     * and expander appear in blue, otherwise in light gray.
     * @param focused true if the section should look as if it is focused
     */    
    void setFocusedLook(boolean focused) {
    
        if (focused && !hasErrors) {
            // The error look overrides the focus look
            expander.setFocusedLook();
            setBorder(BorderFactory.createLineBorder(FOCUS_COLOR, 2));

        } else if (!focused && !hasErrors) {
            // Only go back to normal look if we don't have errors
            expander.setNormalLook();
            setBorder(BorderFactory.createLineBorder(NORMAL_COLOR, 2));
        }
        
        this.hasFocus = focused;
    }

    /**
     * Sets an error message to be displayed in the section header. Setting an error message
     * causes the outline of the section to be displayed in red. To clear a previous message
     * and restore the normal state pass in null for the message.
     * @param message the error message to display
     */    
    void setErrorMessage(String message) {
        
        if (message != null) {
            // Error look overrides all other looks
            expander.setErrorLook(message);
            setBorder(BorderFactory.createLineBorder(ERROR_COLOR, 2));
        
        } else if (message == null && hasFocus) {
            // Set to focus look if the section has focus
            expander.setFocusedLook();
            setBorder(BorderFactory.createLineBorder(FOCUS_COLOR, 2));
        
        } else {
            // If no focus and no error reset to normal look
            expander.setNormalLook();
            setBorder(BorderFactory.createLineBorder(NORMAL_COLOR, 2));
        }
        
        this.hasErrors = (message != null);
    }
    
    /**
     * Sets an error message for the given editor. Use null to clear the error message.
     * @param editor the editor to set the error message for
     * @param message the error message to use
     */
    void setEditorErrorMessage(NavEditorComponent<?> editor, String message) {
        
        JLabel errorLabel = editorToErrorMap.get(editor);
        
        errorLabel.setText(message);
        errorLabel.setVisible(message != null);
        
        JLabel titleLabel = editorToTitleMap.get(editor);
        
        if (titleLabel != null) {
            titleLabel.setForeground(message != null ? Color.RED : Color.BLACK);
        }
        
        if (getEditorPanel().getParent() != null) {
            getEditorPanel().getParent().validate();
        }        
    }    
    
    /**
     * If the section is empty and does not contain any editor this method can be used
     * to set a placeholder message to be displayed in the empty section. The message is
     * automatically displayed whenever there are no editors in the section.
     * @param message the message to be displayed
     */
    void setPlaceHolderMessage(String message) {
        placeHolderLabel.setText(message);
        maybeAddPlaceHolderLabel();
    }

    /**
     * Adds the placeholder label to the section if no editors are in the section.
     */    
    private void maybeAddPlaceHolderLabel() {
        
        if (contentPanel.getComponentCount() == 0) {
            
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.fill = GridBagConstraints.BOTH;
            constraints.anchor = GridBagConstraints.CENTER;
            constraints.insets = new Insets(15, 5, 15, 5);
            constraints.gridx = 0;
            contentPanel.add(placeHolderLabel, constraints);
        
            if (getEditorPanel().getParent() != null) {
                getEditorPanel().getParent().validate();
            }  
        }
    }
    
    /**
     * @param expanded whether or not the section should be expanded
     */
    void setExpanded(boolean expanded) {
        expander.setExpanded(expanded);
    }

    /**
     * Validates the metadata entered by the user. If any editor in the section contains
     * invalid data this should return false and set the appropriate error message for the
     * editors that contains invalid data.
     * @return true if data is valid, false if there is invalid data
     */
    abstract boolean doValidate();
    
    /**
     * Saves the values stored by the editor components back into the metadata.
     */
    abstract void doSave();
    
    /**
     * Reverts the values stored in the editor components to the values currently
     * stored in the metadata object being edited.
     */
    abstract void doRevert();
}

/**
 * An editor section for editing the basic CAL feature metadata.
 * @author Frank Worsley
 */
class NavFeatureEditorSection extends NavEditorSection {
    
    private static final long serialVersionUID = 3173779274560768632L;
    
    /* Keys for the editors. */
    private static final String DISPLAY_NAME_KEY = "displayName";
    private static final String SHORT_DESCRIPTION_KEY = "shortDescription";
    private static final String LONG_DESCRIPTION_KEY = "longDescription";
    private static final String AUTHOR_KEY = "author";
    private static final String VERSION_KEY = "version";
    private static final String PREFERRED_KEY = "preferred";
    private static final String EXPERT_KEY = "expert";
    private static final String RELATED_FEATURES_KEY = "relatedFeatures";
    
    /**
     * Constructor for a new feature metadata section.
     * @param editorPanel the editor panel the section belongs to
     */
    public NavFeatureEditorSection(NavEditorPanel editorPanel) {
        super(editorPanel, NavigatorMessages.getString("NAV_BasicProperties_Header"));
        
        // Add editor components for the basic feature metadata items
        addEditor(new NavTextFieldEditor(this, DISPLAY_NAME_KEY,      NavigatorMessages.getString("NAV_DisplayName"),      NavigatorMessages.getString("NAV_DisplayNameDescription")));
        addEditor(new NavTextFieldEditor(this, SHORT_DESCRIPTION_KEY, NavigatorMessages.getString("NAV_ShortDescription"), NavigatorMessages.getString("NAV_ShortDescriptionDescription")));
        addEditor(new NavTextAreaEditor (this, LONG_DESCRIPTION_KEY,  NavigatorMessages.getString("NAV_LongDescription"),  NavigatorMessages.getString("NAV_LongDescriptionDescription")));
        addEditor(new NavTextFieldEditor(this, AUTHOR_KEY,            NavigatorMessages.getString("NAV_Author"),           NavigatorMessages.getString("NAV_AuthorDescription")));
        addEditor(new NavTextFieldEditor(this, VERSION_KEY,           NavigatorMessages.getString("NAV_Version"),          NavigatorMessages.getString("NAV_VersionDescription")));
        addEditor(new NavBooleanEditor  (this, PREFERRED_KEY,         NavigatorMessages.getString("NAV_Preferred"),        NavigatorMessages.getString("NAV_PreferredDescription")));
        addEditor(new NavBooleanEditor  (this, EXPERT_KEY,            NavigatorMessages.getString("NAV_Expert"),           NavigatorMessages.getString("NAV_ExpertDescription")));
        addEditor(new NavFeaturesEditor (this, RELATED_FEATURES_KEY,  NavigatorMessages.getString("NAV_RelatedFeatures"),  NavigatorMessages.getString("NAV_RelatedFeaturesDescription")));
    }
    
    /**
     * @see org.openquark.gems.client.navigator.NavEditorSection#doValidate()
     */
    @Override
    boolean doValidate() {
        
        // For arguments the display name has to be a valid CAL identifier.
        if (getMetadata() instanceof ArgumentMetadata) {

            NavEditorComponent<String> editor = UnsafeCast.unsafeCast(getEditor(DISPLAY_NAME_KEY));
            String displayName = editor.getValue();            

            if (displayName != null && !LanguageInfo.isValidFunctionName(displayName)) {
                    
                //setErrorMessage("You have entered invalid values. Your changes have not been saved.");
                setErrorMessage("");
                setEditorErrorMessage(editor, NavigatorMessages.getString("NAV_InvalidArgName_Message"));
                return false;
                
            } else {
                setErrorMessage(null);
                setEditorErrorMessage(editor, null);
            }
        }
        
        return true;
    }
    
    /**
     * @see org.openquark.gems.client.navigator.NavEditorSection#doSave()
     */
    @Override
    void doSave() {
        
        CALFeatureMetadata metadata = getMetadata();
        
        metadata.setDisplayName((String) getEditorValue(DISPLAY_NAME_KEY));
        metadata.setShortDescription((String) getEditorValue(SHORT_DESCRIPTION_KEY));
        metadata.setLongDescription((String) getEditorValue(LONG_DESCRIPTION_KEY));
        metadata.setAuthor((String) getEditorValue(AUTHOR_KEY));
        metadata.setVersion((String) getEditorValue(VERSION_KEY));

        Boolean preferred = (Boolean) getEditorValue(PREFERRED_KEY);
        metadata.setPreferred(preferred.booleanValue());
        
        Boolean expert = (Boolean) getEditorValue(EXPERT_KEY);
        metadata.setExpert(expert.booleanValue());
        
        metadata.clearRelatedFeatures();
        List<CALFeatureName> relatedFeatures = UnsafeCast.unsafeCast(getEditorValue(RELATED_FEATURES_KEY));     // ~ unsafe
        for (final CALFeatureName calFeatureName : relatedFeatures) {
            metadata.addRelatedFeature(calFeatureName);
        }
    }
    
    /**
     * @see org.openquark.gems.client.navigator.NavEditorSection#doRevert()
     */
    @Override
    void doRevert() {
        CALFeatureMetadata metadata = getMetadata();
        
        setEditorValue(DISPLAY_NAME_KEY,      metadata.getDisplayName());
        setEditorValue(SHORT_DESCRIPTION_KEY, metadata.getShortDescription());
        setEditorValue(LONG_DESCRIPTION_KEY,  metadata.getLongDescription());
        setEditorValue(AUTHOR_KEY,            metadata.getAuthor());
        setEditorValue(VERSION_KEY,           metadata.getVersion());
        setEditorValue(PREFERRED_KEY,         Boolean.valueOf(metadata.isPreferred()));
        setEditorValue(EXPERT_KEY,            Boolean.valueOf(metadata.isExpert()));
        
        List<CALFeatureName> relatedFeatures = new ArrayList<CALFeatureName>();
        int count = metadata.getNRelatedFeatures();
        for (int i = 0; i < count; i++) {
            relatedFeatures.add(metadata.getNthRelatedFeature(i));
        }
        setEditorValue(RELATED_FEATURES_KEY, relatedFeatures);
    }
}

/**
 * An editor section for editing gem entity metadata.
 * @author Frank Worsley
 */
class NavGemEntityEditorSection extends NavEditorSection {
    
    private static final long serialVersionUID = 1812523907521402162L;
    
    /* Keys for the editors. */
    private static final String CATEGORIES_KEY = "categories";
    
    /**
     * Constructor for a new feature metadata section.
     * @param editorPanel the editor panel the section belongs to
     */
    public NavGemEntityEditorSection(NavEditorPanel editorPanel) {
        super(editorPanel, NavigatorMessages.getString("NAV_GemProperties_Header"));
        addEditor(new NavListEditor(this, CATEGORIES_KEY, NavigatorMessages.getString("NAV_Categories"), NavigatorMessages.getString("NAV_CategoriesDescription")));
    }
    
    /**
     * @see org.openquark.gems.client.navigator.NavEditorSection#doValidate()
     */
    @Override
    boolean doValidate() {
        return true;
    }

    /**
     * @see org.openquark.gems.client.navigator.NavEditorSection#doSave()
     */
    @Override
    void doSave() {

        List<String> categories = UnsafeCast.unsafeCast(getEditorValue(CATEGORIES_KEY));                // ~ unsafe
        String[] catArray = new String[categories.size()];
        
        for (int i = 0; i < catArray.length; i++) {
            catArray[i] = categories.get(i);
        }
        
        setCategoriesIntoMetadata(catArray);
    }
    
    /**
     * @see org.openquark.gems.client.navigator.NavEditorSection#doRevert()
     */
    @Override
    void doRevert() {
        List<String> categories = Arrays.asList(getCategoriesFromMetadata());
        setEditorValue(CATEGORIES_KEY, categories);
    }
    
    /** Set the specified categories into the metadata. */
    private void setCategoriesIntoMetadata(String[] catArray) {
        if (getMetadata() instanceof InstanceMethodMetadata) {
            ((InstanceMethodMetadata)getMetadata()).setCategories(catArray);
        } else {
            ((FunctionalAgentMetadata)getMetadata()).setCategories(catArray);
        }
    }

    /** Get the categories from the metadata. */
    private String[] getCategoriesFromMetadata() {
        if (getMetadata() instanceof InstanceMethodMetadata) {
            return ((InstanceMethodMetadata)getMetadata()).getCategories();
        } else {
            return ((FunctionalAgentMetadata)getMetadata()).getCategories();
        }
    }
}

/**
 * A base class for a section that may contain zero or more editors. This base
 * class provides handling for the adding and removing of editors.
 * 
 * @author Joseph Wong
 */
abstract class NavMultiEditorSection extends NavEditorSection {
    /** The icon for the new button. */
    private static final ImageIcon newIcon = new ImageIcon(NavMultiEditorSection.class.getResource("/Resources/new.gif"));

    /** The button for adding new editors. */
    private final JButton addButton;

    /**
     * Constructor for a new multi-editor section.
     * @param editorPanel the editor panel this section belongs to
     * @param title the editor's title
     * @param addButtonToolTipText
     */
    public NavMultiEditorSection (NavEditorPanel editorPanel, String title, String addButtonToolTipText) {
        super(editorPanel, title);
        
        // Add the 'add editor' button to the expander
        NavExpander expander = getExpander();
        addButton = new JButton(newIcon);
        addButton.setMargin(new Insets(0, 0, 0, 0));
        addButton.setToolTipText(addButtonToolTipText);
        expander.addButton(addButton);
        
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                NavEditorComponent<?> editor = makeNewEditor(); 
                addEditor(editor);
                setExpanded(true);
                
                editor.getEditorComponent().requestFocusInWindow();
            }
        });
    }
    
    /** Creates a new editor component to be added to the section. */
    abstract NavEditorComponent<?> makeNewEditor();

    /**
     * Override this to indicate the section has changed if an 
     * example editor is removed.
     */
    @Override
    void removeEditor(NavEditorComponent<?> editor) {
        
        // figure out the index of this editor, so we can focus the adjacent one
        // after it is removed from the section
        int index = getEditors().indexOf(editor);
        
        editorChanged(editor);
        super.removeEditor(editor);
        
        if (getEditors().size() == 0) {
            addButton.requestFocusInWindow();
        } else if (index == 0) {
            getEditors().get(0).getEditorComponent().requestFocusInWindow();
        } else {
            getEditors().get(index - 1).getEditorComponent().requestFocusInWindow();
        }
    }
    
    /**
     * Override this to indicate the section has changed if an
     * example editor is added.
     */
    @Override
    void addEditor(NavEditorComponent<?> editor) {
        editorChanged(editor);
        super.addEditor(editor);
    }
}

/**
 * An editor section for editing metadata examples.
 * @author Frank Worsley
 */
class NavExampleEditorSection extends NavMultiEditorSection {

    private static final long serialVersionUID = -6958553476841209895L;

    /**
     * Constructor for a new example editor section.
     * @param editorPanel the editor panel this section belongs to
     */
    public NavExampleEditorSection (NavEditorPanel editorPanel) {
        super(editorPanel, NavigatorMessages.getString("NAV_UsageExamples_Header"), NavigatorMessages.getString("NAV_AddExampleButtonToolTip"));
        
        // Set the placeholder message we would like to appear
        setPlaceHolderMessage(NavigatorMessages.getString("NAV_AddExample_Message"));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    NavExampleEditor makeNewEditor() {
        // Create a default expression and example
        ModuleName moduleName = getEditorPanel().getNavigatorOwner().getPerspective().getWorkingModuleName();
        CALExpression expression = new CALExpression(moduleName, "", new CodeQualificationMap(), "");
        CALExample example = new CALExample(expression, "", true);
        
        return new NavExampleEditor(this, example); 
    }

    /**
     * @see org.openquark.gems.client.navigator.NavEditorSection#doValidate()
     */
    @Override
    boolean doValidate() {
        return true;
    }

    /**
     * @see org.openquark.gems.client.navigator.NavEditorSection#doSave()
     */
    @Override
    void doSave() {

        CALExample[] examples = new CALExample[getEditors().size()];
        
        int i = 0;
        for (final NavEditorComponent<?> editor : getEditors()) {
            examples[i] = (CALExample) editor.getValue(); 
            i++;
        }
        
        setExamplesIntoMetadata(examples);
    }

    /**
     * @see org.openquark.gems.client.navigator.NavEditorSection#doRevert()
     */
    @Override
    void doRevert() {

        removeAllEditors();

        CALExample[] examples = getExamplesFromMetadata();
        
        for (final CALExample element : examples) {
            addEditor(new NavExampleEditor(this, element));
        }
    }
    
    /** Set the specified examples into the metadata. */
    private void setExamplesIntoMetadata(CALExample[] examples) {
        if (getMetadata() instanceof InstanceMethodMetadata) {
            ((InstanceMethodMetadata)getMetadata()).setExamples(examples);
        } else {
            ((FunctionalAgentMetadata)getMetadata()).setExamples(examples);
        }
    }

    /** Get the examples from the metadata. */
    private CALExample[] getExamplesFromMetadata() {
        if (getMetadata() instanceof InstanceMethodMetadata) {
            return ((InstanceMethodMetadata)getMetadata()).getExamples();
        } else {
            return ((FunctionalAgentMetadata)getMetadata()).getExamples();
        }
    }
}

/**
 * An editor section for editing custom metadata attributes.
 * @author Joseph Wong
 */
class NavCustomAttributeEditorSection extends NavMultiEditorSection {
    
    private static final long serialVersionUID = -1424974125556259742L;

    /**
     * Constructor for a new custom attribute editor section.
     * @param editorPanel the editor panel this section belongs to
     */
    public NavCustomAttributeEditorSection(NavEditorPanel editorPanel) {
        super(editorPanel, NavigatorMessages.getString("NAV_CustomAttributes_Header"), NavigatorMessages.getString("NAV_AddCustomAttributeButtonToolTip"));
        
        // Set the placeholder message we would like to appear
        setPlaceHolderMessage(NavigatorMessages.getString("NAV_AddCustomAttribute_Message"));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    NavEditorComponent<Pair<String, String>> makeNewEditor() {
        return new NavCustomAttributeEditor(this, "", ""); 
    }

    /**
     * {@inheritDoc}
     */
    @Override
    boolean doValidate() {
        
        Set<String> attributeNames = new HashSet<String>();
        
        for (final NavEditorComponent<?> editor : getEditors()) {
            Pair<String, String> entry = UnsafeCast.unsafeCast(editor.getValue());
            if (attributeNames.contains(entry.fst())) {
                return false;
            } else {
                attributeNames.add(entry.fst());
            }
        }
        
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void doSave() {
        
        // clear the existing custom attributes in the metadata object
        getMetadata().clearAttributes();
        
        // add (back) the attributes that are encapsulated by editors
        for (final NavEditorComponent<?> editor : getEditors()) {
            Pair<String, String> entry = UnsafeCast.unsafeCast(editor.getValue());
            String attributeName = entry.fst();
            String attributeValue = entry.snd();
            
            if (attributeName != null) {
                getMetadata().setAttribute(attributeName, attributeValue);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void doRevert() {

        // clear the existing editors
        removeAllEditors();

        // add the editors representing custom attributes in the metadata object
        for (Iterator<String> it = getMetadata().getAttributeNames(); it.hasNext(); ) {
            String attributeName = it.next();
            String attributeValue = getMetadata().getAttribute(attributeName);
            addEditor(new NavCustomAttributeEditor(this, attributeName, attributeValue));
        }
    }
}

/**
 * An editor section for editing entity arguments. This section lists
 * each argument along with its name, type and an edit button. Clicking the
 * edit button will actually edit the metadata for the argument.
 * @author Frank Worsley
 */
class NavEntityArgumentEditorSection extends NavEditorSection {
    
    private static final long serialVersionUID = 7241341746779529968L;

    /** The key for the return value description editor. */
    private static final String RETURN_VALUE_KEY = "returnValue";
    
    /** Whether the entity has a return value description. */
    private final boolean hasReturnValue;
    
    /**
     * Constructs a new argument editor section.
     * @param editorPanel the editor panel this section belongs to
     * @param hasReturnValue whether the entity has a return value description.
     */
    public NavEntityArgumentEditorSection(NavEditorPanel editorPanel, boolean hasReturnValue) {
        super(editorPanel, NavigatorMessages.getString(hasReturnValue ? "NAV_GemArgumentsAndReturnValue_Header" : "NAV_GemArguments_Header"));
        setPlaceHolderMessage(NavigatorMessages.getString("NAV_NoArgumentsPlaceholder_Message"));
        this.hasReturnValue = hasReturnValue;
    }

    /**
     * @see org.openquark.gems.client.navigator.NavEditorSection#doValidate()
     */
    @Override
    boolean doValidate() {

        // For arguments the display name has to be a valid CAL identifier.
        
        for (final NavEditorComponent<?> listItem : getEditors()) {
            
            if (listItem instanceof NavEntityArgumentEditor) {
                NavEntityArgumentEditor editor = (NavEntityArgumentEditor) listItem;
                ArgumentMetadata metadata = editor.getValue();
                
                if (metadata.getDisplayName() != null &&
                        !LanguageInfo.isValidFunctionName(metadata.getDisplayName())) {
                    
                    setEditorErrorMessage(editor, NavigatorMessages.getString("NAV_InvalidArgName_Message"));
                    return false;
                    
                } else {
                    setEditorErrorMessage(editor, null);
                }
                
            } else if (listItem instanceof NavEntityReturnValueEditor) {
                // no need to validate the return value description
            }
        }
        
        return true;
    }

    /**
     * @see org.openquark.gems.client.navigator.NavEditorSection#doSave()
     */
    @Override
    void doSave() {
        
        List<ArgumentMetadata> argMetadata = new ArrayList<ArgumentMetadata>();
        String returnValueDesc = null;
        
        for (final Object editor : getEditors()) {    // NOTE: If listItem type is NavEditorComponent<?>, javac doesn't like the instanceof tests.
            
            if (editor instanceof NavEntityArgumentEditor) {
                argMetadata.add(((NavEntityArgumentEditor)editor).getValue());
                
            } else if (editor instanceof NavEntityReturnValueEditor) {
                returnValueDesc = ((NavEntityReturnValueEditor)editor).getValue();
            }
        }
        
        setArgumentsIntoMetadata(argMetadata.toArray(new ArgumentMetadata[argMetadata.size()]));
        
        setReturnValueDescriptionIntoMetadata(returnValueDesc);
    }

    /**
     * @see org.openquark.gems.client.navigator.NavEditorSection#doRevert()
     */
    @Override
    void doRevert() {
        
        // We want to use the unqualified names if possible
        NavFrameOwner owner = getEditorPanel().getNavigatorOwner();
        ModuleTypeInfo moduleInfo = owner.getPerspective().getWorkingModuleTypeInfo();
        ScopedEntityNamingPolicy namingPolicy = new UnqualifiedUnlessAmbiguous(moduleInfo);

        // Determine the information needed to display argument information
        ArgumentMetadata[] arguments = getArgumentsFromMetadata();
        ArgumentMetadata[] adjusted = getArgumentsFromMetadata();
        String[] typeStrings = NavAddressHelper.getTypeStrings(owner, getAddress(), namingPolicy);
        
        String returnValueDesc = getReturnValueDescriptionFromMetadata();

        NavAddressHelper.adjustArgumentNames(owner, getAddress(), adjusted);
        
        // first, remove the return value editor so that the remainder editors should all be argument editors
        if (containsEditor(RETURN_VALUE_KEY)) {
            removeEditor(getEditor(RETURN_VALUE_KEY));
        }
        
        List<NavEditorComponent<?>> allEditors = getEditors();
        boolean haveEditors = allEditors.size() > 0;
        
        int nNeededArgumentEditors = arguments.length;
        
        for (int i = 0; i < nNeededArgumentEditors; i++) {
            
            if (haveEditors && i < allEditors.size()) {
                // update the existing editor
                Object listItem = allEditors.get(i);
                
                if (listItem instanceof NavEntityArgumentEditor) {
                    NavEntityArgumentEditor editor = (NavEntityArgumentEditor) listItem;
                    editor.setValue(arguments[i]);
                    editor.setAdjustedName(adjusted[i].getDisplayName());
                    editor.setType(typeStrings[i]);
                    
                } else if (listItem instanceof NavEntityReturnValueEditor) {
                    throw new IllegalStateException("There should not be any return value editors remaining in this section now.");
                }

            } else {
                // add a new editor
                addEditor(new NavEntityArgumentEditor(this, i, arguments[i], typeStrings[i], adjusted[i].getDisplayName()));
            }
        }
        
        // remove old editors that we don't need anymore
        while (nNeededArgumentEditors < getEditors().size()) {
            removeEditor(getEditors().get(getEditors().size() - 1));
        }
        
        // finally, add back the return value editor if necessary
        if (hasReturnValue) {
            addEditor(new NavEntityReturnValueEditor(this, RETURN_VALUE_KEY, returnValueDesc, typeStrings[typeStrings.length - 1]));
        }
    }
    
    /** Set the specified arguments into the metadata. */
    private void setArgumentsIntoMetadata(ArgumentMetadata[] argMetadata) {
        if (getMetadata() instanceof InstanceMethodMetadata) {
            ((InstanceMethodMetadata)getMetadata()).setArguments(argMetadata);
        } else {
            ((FunctionalAgentMetadata)getMetadata()).setArguments(argMetadata);
        }
    }

    /** Get the arguments from the metadata. */
    private ArgumentMetadata[] getArgumentsFromMetadata() {
        if (getMetadata() instanceof InstanceMethodMetadata) {
            return ((InstanceMethodMetadata)getMetadata()).getArguments();
        } else {
            return ((FunctionalAgentMetadata)getMetadata()).getArguments();
        }
    }
    
    /** Set the specified return value description into the metadata. */
    private void setReturnValueDescriptionIntoMetadata(String returnValueDesc) {
        CALFeatureMetadata metadata = getMetadata();
        if (metadata instanceof FunctionMetadata) {
            ((FunctionMetadata)metadata).setReturnValueDescription(returnValueDesc);
            
        } else if (metadata instanceof ClassMethodMetadata) {
            ((ClassMethodMetadata)metadata).setReturnValueDescription(returnValueDesc);
            
        } else if (metadata instanceof InstanceMethodMetadata) {
            ((InstanceMethodMetadata)metadata).setReturnValueDescription(returnValueDesc);
        }
    }
    
    /** Get the return value description from the metadata. */
    private String getReturnValueDescriptionFromMetadata() {
        CALFeatureMetadata metadata = getMetadata();
        if (metadata instanceof FunctionMetadata) {
            return ((FunctionMetadata)metadata).getReturnValueDescription();
            
        } else if (metadata instanceof ClassMethodMetadata) {
            return ((ClassMethodMetadata)metadata).getReturnValueDescription();
            
        } else if (metadata instanceof InstanceMethodMetadata) {
            return ((InstanceMethodMetadata)metadata).getReturnValueDescription();
            
        } else {
            return null;
        }
    }
}

/**
 * An editor section for editing argument specific metadata.
 * This section actually edits the metadata associated with an argument.
 * @author Frank Worsley
 */
class NavArgumentEditorSection extends NavEditorSection {
    
    private static final long serialVersionUID = 4289470583217943611L;
    
    /* Keys for the editors. */
    private static final String DEFAULTS_ONLY_KEY = "defaultsOnly";
    private static final String DEFAULTS_EXPRESSION_KEY = "defaultsExpression";
    private static final String PROMPTING_EXPRESSION_KEY = "promptingExpression";

    /**
     * Constructs a new argument editor section.
     * @param editorPanel the editor panel this section belongs to.
     */
    public NavArgumentEditorSection(NavEditorPanel editorPanel) {
        super(editorPanel, NavigatorMessages.getString("NAV_ArgumentProperties_Header"));
        addEditor(new NavBooleanEditor   (this, DEFAULTS_ONLY_KEY,        NavigatorMessages.getString("NAV_DefaultsOnly"),        NavigatorMessages.getString("NAV_DefaultsOnlyDescription")));
        addEditor(new NavExpressionEditor(this, DEFAULTS_EXPRESSION_KEY,  NavigatorMessages.getString("NAV_DefaultsExpression"),  NavigatorMessages.getString("NAV_DefaultsExpressionDescription")));
        addEditor(new NavExpressionEditor(this, PROMPTING_EXPRESSION_KEY, NavigatorMessages.getString("NAV_PromptingExpression"), NavigatorMessages.getString("NAV_PromptingExpressionDescription")));
    }

    /**
     * @see org.openquark.gems.client.navigator.NavEditorSection#doValidate()
     */
    @Override
    boolean doValidate() {
        return true;
    }

    /**
     * @see org.openquark.gems.client.navigator.NavEditorSection#doSave()
     */
    @Override
    void doSave() {
        ArgumentMetadata metadata = (ArgumentMetadata) getMetadata();
        metadata.setDefaultValuesOnly(((Boolean) getEditorValue(DEFAULTS_ONLY_KEY)).booleanValue());
        metadata.setDefaultValuesExpression((CALExpression) getEditorValue(DEFAULTS_EXPRESSION_KEY));
        metadata.setPromptingTextExpression((CALExpression) getEditorValue(PROMPTING_EXPRESSION_KEY));
    }

    /**
     * @see org.openquark.gems.client.navigator.NavEditorSection#doRevert()
     */
    @Override
    void doRevert() {
        ArgumentMetadata metadata = (ArgumentMetadata) getMetadata();
        
        setEditorValue(DEFAULTS_ONLY_KEY,        Boolean.valueOf(metadata.useDefaultValuesOnly()));
        setEditorValue(DEFAULTS_EXPRESSION_KEY,  metadata.getDefaultValuesExpression());
        setEditorValue(PROMPTING_EXPRESSION_KEY, metadata.getPromptingTextExpression());
    }
}

/**
 * This class implements the expander component displayed at the top of editor sections.
 * @author Frank Worsley
 */
class NavExpander extends JPanel {
    
    private static final long serialVersionUID = 5726979778057766101L;

    /** The collapsed disclosure icon. */
    private static final ImageIcon collapsedIcon = new ImageIcon(NavExpander.class.getResource("/Resources/disclosure_shut.gif"));
    
    /** The expanded disclosure icon. */
    private static final ImageIcon expandedIcon = new ImageIcon(NavExpander.class.getResource("/Resources/disclosure_open.gif"));
    
    /** The label that expands/collapses the expander. */
    private final JLabel expanderLabel;
    
    /** A label that displays status messages for the section. */
    private final JLabel messageLabel;
    
    /** The editor section this expander is for. */
    private final NavEditorSection editorSection;
    
    /** Whether or not this expanded is expanded. */
    private boolean expanded;
    
    /**
     * Constructor for a new expander.
     * @param editorSection the editor section this expander is for
     */
    public NavExpander(final NavEditorSection editorSection) {
        
        if (editorSection == null) {
            throw new NullPointerException();
        }
        
        this.editorSection = editorSection;

        setFocusable(false);
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        
        expanderLabel = new JLabel(editorSection.getTitle(), collapsedIcon, SwingConstants.LEFT);
        expanderLabel.setFont(getFont().deriveFont(Font.BOLD));
        add(expanderLabel);
        
        messageLabel = new JLabel();
        add(Box.createHorizontalStrut(20));
        add(messageLabel);
        add(Box.createHorizontalGlue());
        
        // Add a click listener to the expander and its message labels to
        // expand or collapse the section if a mouse click occurs
        MouseAdapter expanderListener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setExpanded(!isExpanded());
                editorSection.requestFocusInWindow();
            }
        };

        addMouseListener(expanderListener);
        expanderLabel.addMouseListener(expanderListener);
        messageLabel.addMouseListener(expanderListener);      
    }
    
    /**
     * Adds a new button to the expander bar.
     * @param button the button to add
     */
    void addButton(JButton button) {
        add(Box.createHorizontalStrut(5));
        add(button);
    }
    
    /**
     * Sets whether or not this expander is expanded.
     * @param expanded true to be expanded, false otherwise
     */
    public void setExpanded(boolean expanded) {
        
        this.expanded = expanded;
        
        expanderLabel.setIcon(expanded ? expandedIcon : collapsedIcon);
        editorSection.getContentPanel().setVisible(expanded);
        
        if (expanded) {
            
            // show as much of the section as possible while still showing the title
            editorSection.scrollRectToVisible(editorSection.getBounds());
            scrollRectToVisible(getBounds());
            
            expanderLabel.setToolTipText(NavigatorMessages.getString("NAV_ExpanderExpandedToolTip"));
            
        } else {
            scrollRectToVisible(getBounds());
            expanderLabel.setToolTipText(NavigatorMessages.getString("NAV_ExpanderCollapsedToolTip"));
        }
    }
    
    /**
     * @return whether or not this expander is expanded
     */
    public boolean isExpanded() {
        return expanded;
    }
    
    /**
     * Sets a message to be displayed in the expander.
     * @param message
     */
    void setMessage(String message) {
        messageLabel.setText(message);
    }
    
    /**
     * Sets the expander to its focused look.
     */
    void setFocusedLook() {
        setBorder(BorderFactory.createLineBorder(NavEditorSection.FOCUS_COLOR, 2));
        setBackground(NavEditorSection.FOCUS_COLOR);
        setForeground(Color.WHITE);
        expanderLabel.setForeground(Color.WHITE);
        messageLabel.setForeground(Color.WHITE);
        messageLabel.setText("");
        repaint();
    }

    /**
     * Sets the expander to its normal look.
     */    
    void setNormalLook() {            
        setBorder(BorderFactory.createLineBorder(NavEditorSection.NORMAL_COLOR, 2));
        setBackground(NavEditorSection.NORMAL_COLOR);
        setForeground(Color.BLACK);
        expanderLabel.setForeground(Color.BLACK);
        messageLabel.setForeground(Color.BLACK);
        messageLabel.setText("");
        repaint();
    }

    /**
     * Sets the expander to its error look with the given error message displayed.
     * To clear the error look and message pass in null for the message.
     * @param message the error message to display
     */    
    void setErrorLook(String message) {
        setBorder(BorderFactory.createLineBorder(NavEditorSection.ERROR_COLOR, 2));
        setBackground(NavEditorSection.ERROR_COLOR);
        setForeground(Color.WHITE);
        expanderLabel.setForeground(Color.WHITE);
        messageLabel.setForeground(Color.WHITE);
        messageLabel.setText(message);
        repaint();
    }        
}
