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
 * SearchDialog.java
 * Creation date: (Aug 31, 2005)
 * By: Jawright
 */
package org.openquark.gems.client;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.openquark.cal.compiler.CompilerMessage;
import org.openquark.cal.compiler.CompilerMessageLogger;
import org.openquark.cal.compiler.MessageLogger;
import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.Name;
import org.openquark.cal.compiler.SearchResult;
import org.openquark.cal.compiler.SourceMetrics;
import org.openquark.cal.compiler.SourcePosition;
import org.openquark.cal.compiler.SourceRange;
import org.openquark.cal.services.CALFeatureName;
import org.openquark.cal.services.CALSourceManager;
import org.openquark.cal.services.NullaryEnvironment;
import org.openquark.cal.services.Perspective;
import org.openquark.cal.services.ResourceName;
import org.openquark.cal.services.ResourceNullaryStore;
import org.openquark.cal.services.ResourcePathStore;
import org.openquark.cal.services.ResourceStore;
import org.openquark.cal.services.Status;


/**
 * Dialog for prompting the user for search 
 * 
 * @author Jawright
 */
class SearchDialog extends JDialog {
    
    private static final long serialVersionUID = -7152564719622388083L;

    /**
     * Wrapper class for SearchResults that will be placed into a UI element.
     * The toString() method returns a localized string suitable for display
     * to users.
     * 
     * @author Jawright
     */
    private static abstract class SearchResultItem {
        
        /**
         * Wrapper class for precise SearchResults that will be placed into a UI element.
         * The toString() method returns a localized string suitable for display
         * to users.
         * 
         * @author James Wright
         */
        private static final class Precise extends SearchResultItem {
            private final SearchResult.Precise searchResult;
            
            private Precise(SearchResult.Precise searchResult) {
                this.searchResult = searchResult; 
            }
            
            @Override
            public String toString() {
                SourcePosition sourcePosition = searchResult.getSourcePosition();
                return GemCutterMessages.getString("SD_SearchResultTemplateLong", new Object[] {
                    sourcePosition.getSourceName(), 
                    Integer.valueOf(sourcePosition.getLine()), 
                    Integer.valueOf(sourcePosition.getColumn()),
                    searchResult.getName().toSourceText()
                });
            }
            
            SourcePosition getSourcePosition() {
                return searchResult.getSourcePosition();
            }
            
            SourceRange getSourceRange() {
                return searchResult.getSourceRange();
            }
            
            Name getQualifiedName() {
                return searchResult.getName();
            }
        }
        
        /**
         * Wrapper class for search hits based only on frequency information that will be placed into a UI element.
         * The toString() method returns a localized string suitable for display
         * to users.
         * 
         * @author Joseph Wong
         */
        private static final class Frequency extends SearchResultItem {
            private final SearchResult.Frequency searchResult;
            
            private Frequency(SearchResult.Frequency searchResult) {
                this.searchResult = searchResult; 
            }
            
            @Override
            public String toString() {
                SearchResult.Frequency.Type type = searchResult.getType();
                String template = null;
                if (type == SearchResult.Frequency.Type.FUNCTION_REFERENCES) {
                    template = "SD_SearchResultFrequencyTypeFunctionReferences";
                } else if (type == SearchResult.Frequency.Type.INSTANCE_METHOD_REFERENCES) {
                    template = "SD_SearchResultFrequencyTypeInstanceMethodReferences";
                } else if (type == SearchResult.Frequency.Type.CLASS_CONSTRAINTS) {
                    template = "SD_SearchResultFrequencyTypeClassConstraints";
                } else if (type == SearchResult.Frequency.Type.CLASSES) {
                    template = "SD_SearchResultFrequencyTypeClasses";
                } else if (type == SearchResult.Frequency.Type.INSTANCES) {
                    template = "SD_SearchResultFrequencyTypeInstances";
                } else if (type == SearchResult.Frequency.Type.DEFINITION) {
                    template = "SD_SearchResultFrequencyTypeDefinition";
                } else if (type == SearchResult.Frequency.Type.IMPORT) {
                    template = "SD_SearchResultFrequencyTypeImport";
                } else {
                    throw new IllegalStateException("Unknown search result type: " + type);
                }
                
                return GemCutterMessages.getString(template, new Object[] {
                    searchResult.getModuleName(), 
                    Integer.valueOf(searchResult.getFrequency()), 
                    searchResult.getName().toSourceText()
                });
            }
            
            Name getName() {                
                return searchResult.getName();
            }
        }
    }    
    
    /** Used to perform the searches */
    private final SourceMetrics workspaceSourceMetrics;
    
    /** Root of preference keys of the form previousSearches0, previousSearches1, etc.
     * For storing past searches for the combobox. */
    private static final String PREVIOUS_SEARCHES_PREF_KEY_ROOT = "previousSearches";
    
    /** Preference key for the type of search radio button */
    private static final String SEARCH_TYPE_PREF_KEY = "searchType";

    /** Preference key for x position of search dialog */
    private static final String SEARCH_DIALOG_X_PREF_KEY = "searchDialogX";
    
    /** Preference key for y position of search dialog */
    private static final String SEARCH_DIALOG_Y_PREF_KEY = "searchDialogY";
    
    /** Preference key for width of search dialog */
    private static final String SEARCH_DIALOG_WIDTH_PREF_KEY = "searchDialogWidth";
    
    /** Preference key for height of search dialog */
    private static final String SEARCH_DIALOG_HEIGHT_PREF_KEY = "searchDialogHeight";
    
    /** An empty array used for clearing the search results pane. */
    private static final Object[] EMPTY_ARRAY = new Object[0];
    
    /** Number of previous search targets to save */
    private static final int numSavedSearches = 10;
    
    /** UI element (text field w/drop-down) for the user to enter the search text into */
    private final JComboBox searchText = new JComboBox();
    
    /** Manages radio button mutual-exclusivity */
    private final ButtonGroup radioGroup = new ButtonGroup();
    
    /** when selected we will search for all occurrences of an entity */
    private final JRadioButton allOccurrencesButton = new JRadioButton(GemCutter.getResourceString("SD_AllOccurrences"));

    /** when selected we will search for references to a gem */
    private final JRadioButton referencesButton = new JRadioButton(GemCutter.getResourceString("SD_References"));

    /** when selected we will search for definitions of a gem or type */
    private final JRadioButton definitionButton = new JRadioButton(GemCutter.getResourceString("SD_Definition"));

    /** when selected we will search for instances for a class */
    private final JRadioButton instancesButton = new JRadioButton(GemCutter.getResourceString("SD_Instances"));

    /** when selected we will search for instances for a type */
    private final JRadioButton classesButton = new JRadioButton(GemCutter.getResourceString("SD_Classes"));
    
    /** when selected we will search for instances for a type */
    private final JRadioButton constructionsButton = new JRadioButton(GemCutter.getResourceString("SD_Constructions"));
    
    /** List of SourcePositions */
    private final JList searchResultsPane = new JList();

    /** Scroll support for the searchResultsPane */
    private final JScrollPane searchResultsScrollPane = new JScrollPane(searchResultsPane);
    
    /** Area of the dialog for outputting status information to the user */
    private final JLabel statusLabel = new JLabel(" ");
    
    /** OK button for the dialog */
    private final JButton okButton = new JButton(GemCutter.getResourceString("SD_OK"));
    
    /** Cancel button for the dialog */
    private final JButton cancelButton = new JButton(GemCutter.getResourceString("SD_Cancel"));
    
    /** Modeless result display window */
    private final SearchResultsDialog searchResultsDialog;
    
    /** The most recently searched-for target.  This prevents needless repeated searches. */
    private String latestSearchTarget = null;
    
    /** The most recently type of search.  This prevents needless repeated searches. */
    private SearchType latestSearchType = null;
    
    /** 
     * The thread object for the currently-running search, if any.  This field should
     * only be read or modified from the AWT thread.
     */
    private Thread pendingSearchThread = null;
    
    /**
     * The parent window for this dialog
     */
    private final JFrame parent;
    
    /**
     * @param parent Parent window
     */
    SearchDialog(JFrame parent, Perspective perspective) {
        super(parent);
        this.parent = parent;
        this.workspaceSourceMetrics = perspective.getWorkspace().getSourceMetrics();
        searchResultsDialog = new SearchResultsDialog(parent, perspective);
        initialize();
    }
    
    /** Set up the UI */
    private void initialize() {
        // Basic window properties
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(GemCutter.getResourceString("SearchDialog"));
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setContentPane(mainPanel);

        // Widget setup
        searchText.setEditable(true);
        searchText.setMinimumSize(new Dimension(235, 23));
        searchText.setMaximumSize(new Dimension(Integer.MAX_VALUE, 23));
        
        // Layout
        Box radioBox = Box.createHorizontalBox();
        Box radioInternalBox = Box.createVerticalBox();
        
        radioInternalBox.add(allOccurrencesButton);
        radioInternalBox.add(referencesButton);
        radioInternalBox.add(definitionButton);
        radioInternalBox.add(instancesButton);
        radioInternalBox.add(classesButton);
        radioInternalBox.add(constructionsButton);
        
        radioGroup.add(allOccurrencesButton);
        radioGroup.add(referencesButton);
        radioGroup.add(definitionButton);
        radioGroup.add(instancesButton);
        radioGroup.add(classesButton);
        radioGroup.add(constructionsButton);
        
        radioBox.add(radioInternalBox);
        radioBox.add(Box.createHorizontalGlue());
        
        Box statusBox = Box.createHorizontalBox();
        statusBox.add(statusLabel);
        statusBox.add(Box.createHorizontalGlue());

        Box centerBox = Box.createVerticalBox();
        centerBox.add(radioBox);
        centerBox.add(statusBox);
        centerBox.add(searchResultsScrollPane);
        centerBox.add(Box.createVerticalStrut(10));
        
        Box buttonBox = Box.createHorizontalBox();
        buttonBox.add(Box.createHorizontalGlue());
        buttonBox.add(okButton);
        buttonBox.add(Box.createHorizontalStrut(10));
        buttonBox.add(cancelButton);
        
        getContentPane().add(searchText, "North");
        getContentPane().add(centerBox, "Center");
        getContentPane().add(buttonBox, "South");

        // Actions
        okButton.setAction(
                new AbstractAction(GemCutter.getResourceString("SD_OK")) {
                    private static final long serialVersionUID = -2680555515628543174L;

                    public void actionPerformed(ActionEvent e) {
                        performSearch();
                    }
                });

        cancelButton.setAction(
                new AbstractAction(GemCutter.getResourceString("SD_Cancel")) {
                    private static final long serialVersionUID = 508073377921589363L;

                    public void actionPerformed(ActionEvent e) {
                        SearchDialog.this.dispose();
                    }
                });

        searchResultsPane.addListSelectionListener(new ListSelectionListener () {
            public void valueChanged(ListSelectionEvent evt) {
                SearchResultItem searchResultItem = (SearchResultItem)searchResultsPane.getSelectedValue();
                if (searchResultItem instanceof SearchResultItem.Precise) {
                    searchResultsDialog.setResult((SearchResultItem.Precise)searchResultItem);
                    searchResultsDialog.setVisible(true);
                }
            }
        });
        
        searchResultsPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = searchResultsPane.locationToIndex(e.getPoint());
                    SearchResultItem searchResultItem = (SearchResultItem)searchResultsPane.getModel().getElementAt(index);

                    if (searchResultItem instanceof SearchResultItem.Precise) {
                        searchResultsDialog.setResult((SearchResultItem.Precise)searchResultItem);
                        searchResultsDialog.setVisible(true);
                    }
                }
            }
        });

        searchText.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
        
                // Enter typed in the text entry field starts a search
                if(evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    performSearch();
                
                // Escape exits the dialog
                } else if(evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    SearchDialog.this.dispose();
                }
            }
        });
        
        // Enable or disable the OK button depending upon whether a valid target has been entered
        // Enable or disable the class/type-only radio buttons depending upon whether or not an
        // upper-case identifier has been entered.
        Component editor = searchText.getEditor().getEditorComponent();
        if(editor instanceof JTextField) {
            JTextField searchTextField = (JTextField)editor;
            searchTextField.getDocument().addDocumentListener(
                    new DocumentListener() { 
                        
                        public void insertUpdate(DocumentEvent e) {
                            updateButtonState();
                        }
                        public void removeUpdate(DocumentEvent e) {
                            updateButtonState();
                        }
                        public void changedUpdate(DocumentEvent e) {
                            updateButtonState();
                        }
                    });
        }
        
        // If the user changes which radio button is selected, we may need to enable the 
        // OK button again.
        ChangeListener buttonChangeHandler =
            new ChangeListener() {
               public void stateChanged(ChangeEvent evt) {
                   updateButtonState();
               }
        }; 
        
        referencesButton.addChangeListener(buttonChangeHandler);
        definitionButton.addChangeListener(buttonChangeHandler);
        instancesButton.addChangeListener(buttonChangeHandler);
        classesButton.addChangeListener(buttonChangeHandler);
        constructionsButton.addChangeListener(buttonChangeHandler);
        
        // Restore preferences
        for(int i = 0; i < numSavedSearches; i++) {
            String previousSearch = GemCutter.getPreferences().get(PREVIOUS_SEARCHES_PREF_KEY_ROOT + i, null);
            if(previousSearch != null) {
                searchText.addItem(previousSearch);
            } else {
                break;
            }
        }
        searchText.setSelectedIndex(-1);    // Start with a blank text area
        
        
        SearchType searchType;
        try {
            String searchTypeStr = GemCutter.getPreferences().get(SEARCH_TYPE_PREF_KEY, SearchType.REFERENCES.toString());
            searchType = SearchType.fromString(searchTypeStr);
        } catch(IllegalArgumentException e) {
            searchType = SearchType.ALL;
        }
        
        if(searchType == SearchType.ALL) {
            allOccurrencesButton.setSelected(true);
        } else if(searchType == SearchType.REFERENCES) {
            referencesButton.setSelected(true);
        } else if(searchType == SearchType.DEFINITION) {
            definitionButton.setSelected(true);
        } else if(searchType == SearchType.INSTANCES) {
            instancesButton.setSelected(true);
        } else if(searchType == SearchType.CLASSES) {
            classesButton.setSelected(true);
        } else if(searchType == SearchType.CONSTRUCTIONS) {
            constructionsButton.setSelected(true);
        } else {
            throw new IllegalStateException("Unknown searchType");
        }
        
        // Commit
        setModal(false);
        pack();
        setSize(new Dimension(365, 400));
        okButton.setEnabled(false);
    }

    /** Update the enabled/disabled state of the ok button and radio buttons */
    private void updateButtonState() {
        
        String candidateString = getSearchString();

        if(candidateString == null || candidateString.length() == 0) {
            okButton.setEnabled(false);
            instancesButton.setEnabled(true);
            classesButton.setEnabled(true);
            constructionsButton.setEnabled(true);
        
        } else {
            okButton.setEnabled(true);
            instancesButton.setEnabled(true);
            classesButton.setEnabled(true);
            constructionsButton.setEnabled(true);
        }
    }

    /** Save the UI state to be restored next time we show the dialog */
    private void savePreferences() {
        
        for(int i = 0; i < numSavedSearches && i < searchText.getItemCount(); i++) {
            GemCutter.getPreferences().put(PREVIOUS_SEARCHES_PREF_KEY_ROOT + i, searchText.getItemAt(i).toString());
        }
        
        // Save the current search type
        if(latestSearchType != null) {
            GemCutter.getPreferences().put(SEARCH_TYPE_PREF_KEY, latestSearchType.toString());
        }
    }
    
    /** Update the status area with number of hits found */
    private void displayHitCount() {
        int nHits = 0;
        ListModel searchResultItems = searchResultsPane.getModel();
        for (int i = 0, n = searchResultItems.getSize(); i < n; i++) {
            SearchResultItem item = (SearchResultItem)searchResultItems.getElementAt(i);
            if (item instanceof SearchResultItem.Frequency) {
                nHits += ((SearchResultItem.Frequency)item).searchResult.getFrequency();
            } else {
                nHits++;
            }
        }
        
        if(getSearchType() == SearchType.ALL) {
            if(nHits == 1) {
                statusLabel.setText(GemCutterMessages.getString("SD_OneOccurrenceCount", getSearchString()));
            } else {
                statusLabel.setText(GemCutterMessages.getString("SD_OccurrenceCount", Integer.valueOf(nHits), getSearchString()));
            }
        
        } else if(getSearchType() == SearchType.REFERENCES) {
            if(nHits == 1) {
                statusLabel.setText(GemCutterMessages.getString("SD_OneReferenceCount", getSearchString()));
            } else {
                statusLabel.setText(GemCutterMessages.getString("SD_ReferenceCount", Integer.valueOf(nHits), getSearchString()));
            }
        
        } else if(getSearchType() == SearchType.DEFINITION) {
            if(nHits == 1) {
                statusLabel.setText(GemCutterMessages.getString("SD_OneDefinitionCount", getSearchString()));
            } else {
                statusLabel.setText(GemCutterMessages.getString("SD_DefinitionCount", Integer.valueOf(nHits), getSearchString()));
            }

        } else if(getSearchType() == SearchType.INSTANCES) {
            if(nHits == 1) {
                statusLabel.setText(GemCutterMessages.getString("SD_OneInstanceCount", getSearchString()));
            } else {
                statusLabel.setText(GemCutterMessages.getString("SD_InstanceCount", Integer.valueOf(nHits), getSearchString()));
            }
    
        } else if(getSearchType() == SearchType.CLASSES) {
            if(nHits == 1) {
                statusLabel.setText(GemCutterMessages.getString("SD_OneClassCount", getSearchString()));
            } else {
                statusLabel.setText(GemCutterMessages.getString("SD_ClassCount", Integer.valueOf(nHits), getSearchString()));
            }
        } else if(getSearchType() == SearchType.CONSTRUCTIONS) {
            if(nHits == 1) {
                statusLabel.setText(GemCutterMessages.getString("SD_OneConstructionCount", getSearchString()));
            } else {
                statusLabel.setText(GemCutterMessages.getString("SD_ConstructionCount", Integer.valueOf(nHits), getSearchString()));
            }
        } else {
            // This should never happen, since getSearchType will return SearchType.ALL if it
            // doesn't understand the current UI selection. 
            throw new IllegalStateException("invalid current search type");
        }
    }
    
    /** 
     * Perform a new search based on the current UI state.
     * This method should only be called from the AWT thread.
     */
    private void performSearch() {

        if(isSearchPending()) {
            return;
        }
        
        final String searchString = getSearchString(); 
        
        final SearchType searchType = getSearchType();
        if(searchString == null || 
           searchString.equals(latestSearchTarget) && searchType == latestSearchType) {
            return;
        }
        
        latestSearchTarget = searchString;
        latestSearchType = searchType;
        
        // We want the latest search always to appear at the top of the list, but we
        // also want each search to appear in the list only once, so remove any duplicates
        // before re-inserting the new search at the top of the list.
        for(int i = 0; i < searchText.getItemCount(); i++) {
            String itemString = searchText.getItemAt(i).toString();
            if(itemString.equals(searchString)) {
                searchText.removeItemAt(i);
                i--;
            }
        }
        
        searchText.insertItemAt(searchString, 0);
        searchText.setSelectedIndex(0);
        savePreferences();
        
        okButton.setEnabled(false);
        searchText.setEnabled(false);
        statusLabel.setText(GemCutter.getResourceString("SD_Searching"));
        searchResultsPane.setListData(EMPTY_ARRAY);
        
        final Cursor oldCursor = getCursor();
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        // Run the search on its own thread, and then call into the AWT thread
        // to update the UI.
        pendingSearchThread = new Thread("search thread") {
            @Override
            public void run() {
                
                List<SearchResult> newResults = Collections.<SearchResult>emptyList();
                final MessageLogger messageLogger = new MessageLogger();
                try {
                    if(searchType == SearchType.ALL) {
                        newResults = workspaceSourceMetrics.findAllOccurrences(searchString, messageLogger);
                    } else if(searchType == SearchType.REFERENCES) {
                        newResults = workspaceSourceMetrics.findReferences(searchString, messageLogger);
                    } else if(searchType == SearchType.DEFINITION) {
                        newResults = workspaceSourceMetrics.findDefinition(searchString, messageLogger);
                    } else if(searchType == SearchType.INSTANCES) {
                        newResults = workspaceSourceMetrics.findInstancesOfClass(searchString, messageLogger);
                    } else if(searchType == SearchType.CLASSES) {
                        newResults = workspaceSourceMetrics.findTypeInstances(searchString, messageLogger);
                    } else if(searchType == SearchType.CONSTRUCTIONS) {
                        newResults = workspaceSourceMetrics.findConstructions(searchString, messageLogger);
                    } else {
                        throw new IllegalStateException("unknown SearchType");
                    }
                
                } finally {    
                    
                    final Vector<SearchResultItem> newItems = new Vector<SearchResultItem>(newResults.size());
                    for(int i = 0; i < newResults.size(); i++) {
                        SearchResult searchResult = newResults.get(i);
                        SearchResultItem searchResultItem;
                        if (searchResult instanceof SearchResult.Precise) {
                            searchResultItem = new SearchResultItem.Precise((SearchResult.Precise)searchResult);
                        } else if (searchResult instanceof SearchResult.Frequency){
                            searchResultItem = new SearchResultItem.Frequency((SearchResult.Frequency)searchResult);
                        } else {
                            throw new IllegalStateException("unknown SearchResult");
                        }
                        newItems.add(searchResultItem);
                    }
                    
                    // We use invokeLater to ensure that the searchComplete method will be
                    // called on the AWT thread.
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            searchComplete(newItems, messageLogger, oldCursor);
                        }
                    });
                }
           }
        };
        
        pendingSearchThread.start();
    }
    
    /**
     * Perform end-of-search processing (including clearing the current-search thread).
     * This method should only be called from the AWT thread.
     * @param results Vector of SourcePositionItems representing search hits
     * @param messageLogger Logger used during search
     * @param savedCursor Cursor to restore
     */
    private void searchComplete(Vector<SearchResultItem> results, CompilerMessageLogger messageLogger, Cursor savedCursor) {
        searchResultsPane.setListData(results);
        okButton.setEnabled(true);
        searchText.setEnabled(true);
        displayHitCount();
        setCursor(savedCursor);
        pendingSearchThread = null;
        
        if(messageLogger.getMaxSeverity().compareTo(CompilerMessage.Severity.ERROR) >= 0) {

            StringBuilder msgText = new StringBuilder(GemCutter.getResourceString("SD_ParseErrorsDuringSearch") + "\n" + GemCutter.getResourceString("CompileErrorIntro"));
            for (final CompilerMessage message : messageLogger.getCompilerMessages()) {
                msgText.append("\n  ");
                msgText.append(message.toString());
            }
            
            JOptionPane.showMessageDialog(parent, msgText, GemCutter.getResourceString("WindowTitle"), JOptionPane.WARNING_MESSAGE);
        }
        
        // As a convenience, we pop up the results window directly if there is
        // only a single result
        if(results.size() == 1) {
            SearchResultItem searchResultItem = results.get(0);
            
            if (searchResultItem instanceof SearchResultItem.Precise) {
                searchResultsDialog.setResult((SearchResultItem.Precise)searchResultItem);
                searchResultsDialog.setVisible(true);
            }
        }
    }
    
    /** @return SearchType the type of search the user has requested */
    SearchType getSearchType() {
        if(allOccurrencesButton.isSelected()) {
            return SearchType.ALL;
        } else if(referencesButton.isSelected()) {
            return SearchType.REFERENCES;
        } else if(definitionButton.isSelected()) {
            return SearchType.DEFINITION;
        } else if(instancesButton.isSelected()) {
            return SearchType.INSTANCES;
        } else if(classesButton.isSelected()) {
            return SearchType.CLASSES;
        } else if(constructionsButton.isSelected()) {
            return SearchType.CONSTRUCTIONS;
        } else {
            return SearchType.ALL;
        }
    }

    /** Set the current type of search */
    private void setSearchType(SearchType searchType) {
        if(searchType == SearchType.ALL) {
            allOccurrencesButton.setSelected(true);
        } else if(searchType == SearchType.REFERENCES) {
            referencesButton.setSelected(true);
        } else if(searchType == SearchType.DEFINITION) {
            definitionButton.setSelected(true);
        } else if(searchType == SearchType.INSTANCES) {
            instancesButton.setSelected(true);
        } else if(searchType == SearchType.CLASSES) {
            classesButton.setSelected(true);
        } else if(searchType == SearchType.CONSTRUCTIONS) {
            constructionsButton.setSelected(true);
        } else {
            allOccurrencesButton.setSelected(true);
        }
     }
    
    /** @return The current text of the search target field
     */
    String getSearchString() {

        return (String)searchText.getEditor().getItem();
    }

    /**
     * Sets the text of the search target
     * @param newText String to set the search target to
     */
    private void setSearchText(String newText) {
        searchText.getEditor().setItem(newText);
    }
    
    /**
     * Perform a search of the specified type for the specified text
     * @param searchText String text to search for
     * @param searchType SearchType enum value of type of search
     */
    void performSearch(String searchText, SearchType searchType) {
        
        if(isSearchPending()) {
            return;
        }
        
        setSearchText(searchText);
        setSearchType(searchType);
        performSearch();
    }
    
    /**
     * This method should only be called from the AWT thread.
     * @return true if a search is already pending, or false otherwise.
     */
    boolean isSearchPending() {
        return (pendingSearchThread != null);
    }
    
    /**
     * Save the dialog's position and size into the GemCutter's preferences.
     */
    void savePosition() {
        GemCutter.getPreferences().putInt(SEARCH_DIALOG_X_PREF_KEY, getX());
        GemCutter.getPreferences().putInt(SEARCH_DIALOG_Y_PREF_KEY, getY());
        GemCutter.getPreferences().putInt(SEARCH_DIALOG_WIDTH_PREF_KEY, getWidth());
        GemCutter.getPreferences().putInt(SEARCH_DIALOG_HEIGHT_PREF_KEY, getHeight());
    }

    /**
     * @return true if the preferences contain position and size info for this dialog
     */
    boolean hasSavedPosition() {
        return (GemCutter.getPreferences().getInt(SEARCH_DIALOG_X_PREF_KEY, -1) != -1 &&
                GemCutter.getPreferences().getInt(SEARCH_DIALOG_Y_PREF_KEY, -1) != -1 &&
                GemCutter.getPreferences().getInt(SEARCH_DIALOG_WIDTH_PREF_KEY, -1) != -1 &&
                GemCutter.getPreferences().getInt(SEARCH_DIALOG_HEIGHT_PREF_KEY, -1) != -1);
    }

    /**
     * Set the bounds of the dialog to the position and size stored in the preferences
     */
    void setPositionToSaved() {
        setBounds(GemCutter.getPreferences().getInt(SEARCH_DIALOG_X_PREF_KEY, 0),
                  GemCutter.getPreferences().getInt(SEARCH_DIALOG_Y_PREF_KEY, 0),
                  GemCutter.getPreferences().getInt(SEARCH_DIALOG_WIDTH_PREF_KEY, 600),
                  GemCutter.getPreferences().getInt(SEARCH_DIALOG_HEIGHT_PREF_KEY, 800));
    }
    
    /**
     * Provides a modeless dialogue for displaying the source of a module with a
     * hit highlighted.
     * 
     * @author Jawright
     */
    private static final class SearchResultsDialog extends JDialog {
        
        private static final long serialVersionUID = -122928860414946366L;

        /** Preference key for x position of search results dialog */
        private static final String SEARCH_RESULTS_DIALOG_X_PREF_KEY = "searchResultsDialogX";
        
        /** Preference key for y position of search results dialog */
        private static final String SEARCH_RESULTS_DIALOG_Y_PREF_KEY = "searchResultsDialogY";
        
        /** Preference key for width of search results dialog */
        private static final String SEARCH_RESULTS_DIALOG_WIDTH_PREF_KEY = "searchResultsDialogWidth";
        
        /** Preference key for height of search results dialog */
        private static final String SEARCH_RESULTS_DIALOG_HEIGHT_PREF_KEY = "searchResultsDialogHeight";
        
        /** Used for finding and loading/saving modules based on their names */ 
        private final Perspective perspective;

        /** When clicked, we try to save the file */
        private final JButton saveButton = new JButton(GemCutter.getResourceString("SD_Save"));
        
        /** When clicked, we hide the dialog */
        private final JButton closeButton = new JButton(GemCutter.getResourceString("SD_Close"));
        
        /** Text area where the source is displayed and (potentially) edited */
        private final JTextArea editorPane = new JTextArea(); 
        
        /** Provides scroll functionality for the editorPane */
        private final JScrollPane scrollPane = new JScrollPane(editorPane);
        
        /** SourcePosition of the current search hit */
        private SourcePosition currentSourcePosition = null;
        
        /** When true, it is okay for the user to edit this file */
        private boolean isEditable = false;
        
        /** Size of the parent frame */
        private final Rectangle parentBounds; 
        
        /**
         * Construct a new SearchResultsDialog.
         * @param parent Parent window for this dialog
         * @param perspective Perspective to obtain Workspace from for searching 
         */
        SearchResultsDialog(JFrame parent, Perspective perspective) {
            super(parent);
            this.perspective = perspective;
            parentBounds = parent.getBounds();
            initialize();
        }
        
        /**
         * Set up the UI elements
         */
        private void initialize() {
            setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
            JPanel mainPane = new JPanel(new BorderLayout());
            mainPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            setContentPane(mainPane);

            Box buttonBox = Box.createHorizontalBox();
            buttonBox.add(Box.createHorizontalGlue());
            buttonBox.add(saveButton);
            buttonBox.add(Box.createHorizontalStrut(10));
            buttonBox.add(closeButton);
            buttonBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 2, 2));

            getContentPane().add(buttonBox, "South");
            getContentPane().add(scrollPane, "Center");

            editorPane.setFont(new Font("Monospaced", Font.PLAIN, 12));

            scrollPane.setMinimumSize(new Dimension(600, 600));
            scrollPane.setPreferredSize(new Dimension(600, 600));
            scrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

            saveButton.setAction(new AbstractAction(GemCutter.getResourceString("SD_Save")) {
                private static final long serialVersionUID = 1925793734828203467L;

                public void actionPerformed(ActionEvent evt) {
                    saveText();
                }
            });

            closeButton.setAction(new AbstractAction(GemCutter.getResourceString("SD_Close")) {
                private static final long serialVersionUID = 8848595347968035051L;

                public void actionPerformed(ActionEvent evt) {
                    savePosition();
                    setVisible(false);
                }
            });

            editorPane.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent evt) {
                    if(evt.getKeyCode() == KeyEvent.VK_S && (evt.isControlDown() || evt.isAltDown())) {
                        // Ignore Ctrl+S / Alt+S on files that cannot be edited
                        if(canSave()) {
                            saveText();
                        }

                    } else if(evt.getKeyChar() == KeyEvent.VK_C && evt.isAltDown()) {
                        savePosition();
                        setVisible(false);

                    } else if(evt.getKeyChar() != KeyEvent.CHAR_UNDEFINED) {
                        updateSaveButtonStatus();
                        setModifiedTitle(true);
                    }
                }
            });

            addWindowListener(new WindowAdapter() {
               @Override
            public void windowActivated(WindowEvent e) {
                   // Force the focus to the editor component (rather than, say, the
                   // Close button) to ensure that the selection is visible.
                   editorPane.requestFocusInWindow();
               }
               
               @Override
            public void windowClosing(WindowEvent e) {
                   savePosition();
               }
            });
            
            pack(); 
            setModal(false);
            
            if(hasSavedPosition()) {
                setPositionToSaved();
            } else {
                setLocation(parentBounds.x + parentBounds.width - getWidth(), parentBounds.y);
            }
        }

        /**
         * @return boolean True if current module is editable, false otherwise. 
         */
        private boolean canSave() {
            return isEditable;
        }
        
        /** Enable the save button if the module is editable and changed,
         * disable it if the module is not editable or unchanged */
        private void updateSaveButtonStatus() {
            saveButton.setEnabled(isEditable);
        }
        
        /** If the file is modified, include a star before the name. */
        private void setModifiedTitle(boolean fileModified) {
            if (!isEditable) {
                setTitle(GemCutterMessages.getString("SD_SearchResultsTitleReadOnly", currentSourcePosition.getSourceName(), getModulePath()));                
            } else { 
                if (fileModified) {
                    setTitle(GemCutterMessages.getString("SD_SearchResultsTitleModified", currentSourcePosition.getSourceName(), getModulePath()));
                } else {
                    setTitle(GemCutterMessages.getString("SD_SearchResultsTitle", currentSourcePosition.getSourceName(), getModulePath()));            
                }
            }
        }
        
        /** Save changes to the file */
        private void saveText() {
            
            ModuleName moduleName = getModuleNameFromCurrentSourcePosition();
            CALSourceManager sourceManager = perspective.getWorkspace().getSourceManager(moduleName);

            Status saveStatus = new Status("Saving module text");
            sourceManager.saveSource(moduleName, editorPane.getText(), saveStatus);
            if(!saveStatus.isOK()) {
                String errTitle = GemCutter.getResourceString("CannotSaveDialogTitle");
                String errMessage = GemCutter.getResourceString("SaveModuleError");
                JOptionPane.showMessageDialog(this, errMessage, errTitle, JOptionPane.ERROR_MESSAGE);
                System.out.println(saveStatus.getDebugMessage());
                return;
            }
            
            saveButton.setEnabled(false);
            setModifiedTitle(false);
        }

        /**
         * @return true if the module that contains the current source position is writeable,
         * or false otherwise.
         */
        private boolean isModuleWriteable() {
            ModuleName moduleName = getModuleNameFromCurrentSourcePosition();
            CALSourceManager sourceManager = perspective.getWorkspace().getSourceManager(moduleName);
            return sourceManager.isWriteable(moduleName);
        }
        
        /**
         * @return that describes the location of the module.  This will normally
         *          be an absolute path to the file in the filesystem.
         */
        private String getModulePath() {
            
            ModuleName moduleName = getModuleNameFromCurrentSourcePosition();
            CALFeatureName moduleFeatureName = CALFeatureName.getModuleFeatureName(moduleName);
            ResourceName moduleResourceName = new ResourceName(moduleFeatureName);
            
            ResourceStore sourceStore = perspective.getWorkspace().getSourceManager(moduleName).getResourceStore();
            
            // If the source store doesn't know about this module, then we don't know how to find its file
            if (!sourceStore.hasFeature(moduleResourceName)) {
                return null;
            }
            
            if (sourceStore instanceof ResourcePathStore) {
                ResourcePathStore sourcePathStore = (ResourcePathStore)sourceStore;
                
                if (sourcePathStore instanceof ResourceNullaryStore) {
                    File currentFile = NullaryEnvironment.getNullaryEnvironment().getFile(sourcePathStore.getResourcePath(moduleResourceName), false);
                    if(currentFile != null) {
                        return currentFile.getAbsolutePath();
                    }
                }
            
            }
            
            return null;
        }

        /**
         * @return the module name as specified by the current source position.
         */
        private ModuleName getModuleNameFromCurrentSourcePosition() {
            return ModuleName.make(currentSourcePosition.getSourceName());
        }
        
        /**
         * Sets the text and selection of the dialog based on a SourcePosition.
         * @param searchResultItem Specification of the hit to highlight
         */
        void setResult(SearchResultItem.Precise searchResultItem) {
            
            SourcePosition sourcePosition = searchResultItem.getSourcePosition();
            
            // Update text if necessary
            String sourceName = sourcePosition.getSourceName();
            if(currentSourcePosition == null || !currentSourcePosition.getSourceName().equals(sourceName)) {
            
                Reader sourceReader = perspective.getWorkspace().getSourceDefinition(ModuleName.make(sourceName)).getSourceReader(new Status("reading source for search hit display"));
                if (sourceReader == null) {
                    System.err.println("Could not read source definition for source: " + sourceName);
                    return;
                }
                sourceReader = new BufferedReader(sourceReader);
                
                try {
                    editorPane.read(sourceReader, null);
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                } finally {
                    try {
                        sourceReader.close();
                    } catch (IOException e) {
                    }
                }
            }
            
            currentSourcePosition = sourcePosition;
            
            selectTargetAtCurrentPosition(searchResultItem.getSourceRange());
            
            isEditable = isModuleWriteable();
            String modulePath = getModulePath();
            
            // Title depends on whether the module is readOnly
            if(isEditable) {
                setTitle(GemCutterMessages.getString("SD_SearchResultsTitle", sourcePosition.getSourceName(), modulePath));
            } else if (modulePath != null) {
                setTitle(GemCutterMessages.getString("SD_SearchResultsTitleReadOnly", sourcePosition.getSourceName(), modulePath));
            } else {
                setTitle(GemCutterMessages.getString("SD_SearchResultsTitleReadOnlyWithoutFile", sourcePosition.getSourceName()));
            }

            editorPane.setEditable(isEditable);
            saveButton.setEnabled(false);
        }
        
        private void selectTargetAtCurrentPosition(SourceRange sourceRange) {
            Document doc = editorPane.getDocument();
            SourcePosition startPosition = sourceRange.getStartSourcePosition();
            SourcePosition endPosition = sourceRange.getEndSourcePosition();
            
            try {
                
                int len = doc.getLength();
                String text = doc.getText(0, len);
                int startIndex = startPosition.getPosition(text);
                int endIndex = endPosition.getPosition(text, startPosition, startIndex);

                editorPane.select(startIndex, endIndex);
                
            } catch (BadLocationException e1) {
                e1.printStackTrace();
            }
        }
        
        /**
         * Save the dialog's position and size into the GemCutter's preferences.
         */
        void savePosition() {
            GemCutter.getPreferences().putInt(SEARCH_RESULTS_DIALOG_X_PREF_KEY, getX());
            GemCutter.getPreferences().putInt(SEARCH_RESULTS_DIALOG_Y_PREF_KEY, getY());
            GemCutter.getPreferences().putInt(SEARCH_RESULTS_DIALOG_WIDTH_PREF_KEY, getWidth());
            GemCutter.getPreferences().putInt(SEARCH_RESULTS_DIALOG_HEIGHT_PREF_KEY, getHeight());
        }

        /**
         * @return true if the preferences contain position and size info for this dialog
         */
        boolean hasSavedPosition() {
            return (GemCutter.getPreferences().getInt(SEARCH_RESULTS_DIALOG_X_PREF_KEY, -1) != -1 &&
                    GemCutter.getPreferences().getInt(SEARCH_RESULTS_DIALOG_Y_PREF_KEY, -1) != -1 &&
                    GemCutter.getPreferences().getInt(SEARCH_RESULTS_DIALOG_WIDTH_PREF_KEY, -1) != -1 &&
                    GemCutter.getPreferences().getInt(SEARCH_RESULTS_DIALOG_HEIGHT_PREF_KEY, -1) != -1);
        }

        /**
         * Set the bounds of the dialog to the position and size stored in the preferences
         */
        void setPositionToSaved() {
            setBounds(GemCutter.getPreferences().getInt(SEARCH_RESULTS_DIALOG_X_PREF_KEY, 0),
                      GemCutter.getPreferences().getInt(SEARCH_RESULTS_DIALOG_Y_PREF_KEY, 0),
                      GemCutter.getPreferences().getInt(SEARCH_RESULTS_DIALOG_WIDTH_PREF_KEY, 600),
                      GemCutter.getPreferences().getInt(SEARCH_RESULTS_DIALOG_HEIGHT_PREF_KEY, 800));
        }
        
    }
    
    /**
     * Typesafe enumeration representing the type of search to perform
     * 
     * @author Jawright
     */
    static final class SearchType {
        
        static final SearchType ALL = new SearchType("AllOccurrences");
        static final SearchType REFERENCES = new SearchType("References");
        static final SearchType DEFINITION = new SearchType("Declarations");
        static final SearchType INSTANCES = new SearchType("Instances");
        static final SearchType CLASSES = new SearchType("Classes");
        static final SearchType CONSTRUCTIONS = new SearchType("Constructions");
    
        /** Name of this type of search */
        private final String typeName;
        
        /**
         * @param typeName Name of the type of search to be represented
         */
        private SearchType(String typeName) {
            this.typeName = typeName;
        }
        
        /** @return a String representation of this value */
        @Override
        public String toString() {
            return typeName;
        }
        
        /** 
         * @param key String representing a SearchType
         * @return The SearchType that corresponds to key
         */
        static final SearchType fromString(String key) {
            if(key.equals(REFERENCES.typeName)) {
                return REFERENCES;
            
            } else if(key.equals(DEFINITION.typeName)) {
                return DEFINITION;

            } else if(key.equals(INSTANCES.typeName)) {
                return INSTANCES;

            } else if(key.equals(CLASSES.typeName)) {
                return CLASSES;

            } else if(key.equals(CONSTRUCTIONS.typeName)) {
                return CONSTRUCTIONS;

            } else if(key.equals(ALL.typeName)) {
                return ALL;

            } else {
                throw new IllegalArgumentException("unrecognized SearchType string");
            }
        }
    }
}
