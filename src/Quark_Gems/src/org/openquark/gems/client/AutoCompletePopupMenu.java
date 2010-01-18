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
 * AutoCompletePopupMenu.java
 * Creation date: Dec 10th 2002
 * By: Ken Wong
 */
package org.openquark.gems.client;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.ModuleNameResolver.ResolutionResult;
import org.openquark.cal.compiler.ScopedEntityNamingPolicy.UnqualifiedUnlessAmbiguous;
import org.openquark.cal.services.AutoCompleteHelper;
import org.openquark.cal.services.GemEntity;
import org.openquark.cal.services.MetaModule;
import org.openquark.cal.services.Perspective;
import org.openquark.gems.client.AutoburnLogic.AutoburnUnifyStatus;
import org.openquark.gems.client.IntellicutListModelAdapter.IntellicutListEntry;
import org.openquark.gems.client.IntellicutManager.IntellicutInfo;
import org.openquark.gems.client.IntellicutManager.IntellicutMode;
import org.openquark.gems.client.utilities.MouseClickDragAdapter;
import org.openquark.util.Pair;

/**
 * A popupmenu that assists the entry of unqualifiedNames by providing a sequential-search styled access to the 
 * unqualified names of gems
 * @author Ken Wong
 * Creation Date: December 3rd 2002
 */
public class AutoCompletePopupMenu extends JPopupMenu {
    private static final long serialVersionUID = -7001891717459680744L;

    /** The exception that is thrown when no autocomplete entry is found */
    public static class AutoCompleteException extends Exception {
        
        private static final long serialVersionUID = -5115617110940452405L;

        /**
         * Default constructor
         * @param msg
         */
        AutoCompleteException (String msg) { 
            super(msg);
        }

    }
    
    /**
     * The adapter used by the IntellicutList for the auto-complete manager. 
     * It includes all entities that can be substituted to complete/replace a
     * portion of typed text. If the typed text is preceded by a module name,
     * only the valid entities from the specific module will be shown as completions.
     * 
     * @author Iulian Radu
     */
    static class AutoCompleteIntellicutAdapter extends IntellicutListModelAdapter {
        
        /** 
         *  Module which is searched for matching entities for completion.
         *  If null, entities are retrieved from all modules via the perspective.
         */
        private ResolutionResult moduleResolutions = null;
        
        /**
         * Perspective to use for retrieving entities which are not
         * qualified to a module.
         */
        private final Perspective perspective;
        
        /**
         * Constructor
         * @param perspective
         */
        public AutoCompleteIntellicutAdapter(Perspective perspective) {
            this.perspective = perspective;
        }

        /**
         * @see org.openquark.gems.client.IntellicutListModelAdapter#getDataObjects()
         */
        @Override
        protected Set<GemEntity> getDataObjects() {

            if (moduleResolutions == null) {
                setNamingPolicy(new UnqualifiedUnlessAmbiguous(perspective.getWorkingModuleTypeInfo()));
                return getVisibleGemsFromPerspective(perspective);
                
            } else {
//                setNamingPolicy(ScopedEntityNamingPolicy.UNQUALIFIED);
                ModuleName[] matches = moduleResolutions.getPotentialMatches();
                HashSet<GemEntity> results = new HashSet<GemEntity>();
                for(int i = 0; i < matches.length; ++i){
                    ModuleName moduleName = matches[i];
                    MetaModule metaModule = (perspective.isVisibleModule(moduleName)? perspective.getMetaModule(moduleName) : null);
                    if (metaModule != null) {
                        results.addAll(perspective.getVisibleGemEntities(metaModule));
                    }
                }
                return results;
            }
        }
        
        /**
         * Sets the module to retrieve list objects from.
         * If null, objects are retrieved from all imported modules via the perspective. 
         * @param moduleName
         */
        public void setModule(ResolutionResult moduleName) {
            this.moduleResolutions = moduleName;
        }
        
        /**
         * @see org.openquark.gems.client.IntellicutListModelAdapter#getIntellicutInfo(org.openquark.gems.client.IntellicutListModelAdapter.IntellicutListEntry)
         */
        @Override
        protected IntellicutInfo getIntellicutInfo(IntellicutListEntry listEntry) {
            return new IntellicutInfo(AutoburnUnifyStatus.NOT_NECESSARY, -1, 1);
        }
    }
    
    /** A list of the gems that are currently being displayed in the panel */
    private IntellicutList visibleGems;
    
    /** The intellicut adapter used for modeling the auto-completion list */
    private AutoCompleteIntellicutAdapter listAdapter;
    
    /** The whether or not the user commited */
    private boolean userCommited;
        
    /** The text field that the user types in */
    private JTextComponent textComponent;
    
    /** The context within the workspace */
    private Perspective perspective;
    
    /** The panel that stores the various UI components */
    private final JPanel mainPanel;
    
    /** all the listeners that were removed from the preview panel (add back when we close this popup) */
    private KeyListener[] oldKeyListeners;
    
    /** The length of the word that we're trying to complete */
    private int userInputWordLength;
    
    /** The length of the symbol that is being auto-completed. */
    private int userInputQualifiedNameLength;
    
    /** The AutoCompleteManager that handles this instance */
    private final AutoCompleteManager autoCompleteManager;
    
    /** The scrollpane that contains the autocomplete list*/
    private final JScrollPane scrollPane;
    
    /** The listener used to change the suggestion list that is shown. */
    private final DocumentListener documentListener = new DocumentListener() {
        /**
         * @see javax.swing.event.DocumentListener#insertUpdate(DocumentEvent)
         */
        public void insertUpdate(DocumentEvent e) {
            refreshVisibleList(textComponent.getCaretPosition() + e.getLength());
        }

        /**
         * @see javax.swing.event.DocumentListener#removeUpdate(DocumentEvent)
         */
        public void removeUpdate(DocumentEvent e) {
            refreshVisibleList(textComponent.getCaretPosition() - e.getLength());
        }

        /**
         * @see javax.swing.event.DocumentListener#changedUpdate(DocumentEvent)
         */
        public void changedUpdate(DocumentEvent e) {
            refreshVisibleList(textComponent.getCaretPosition() + e.getLength());
        }
    };
    
    /** 
     * The key listener that deals with the accept or cancel gestures 
     */
    private final KeyListener cancelAcceptKeyListener = new KeyAdapter() {

        @Override
        public void keyPressed(KeyEvent e) {

            int keyCode = e.getKeyCode();

            if (keyCode == KeyEvent.VK_ESCAPE || keyCode == KeyEvent.VK_SPACE) {
                userCommited = false;
                autoCompleteManager.closeAutoCompletePopup();
                e.consume();
                
            } else if (keyCode == KeyEvent.VK_ENTER) {
                userCommited  = true;
                autoCompleteManager.closeAutoCompletePopup();
                e.consume();
            }
        }
    };
    
    /**
     * The listener used to ensure that the up/down arrow keys still work when focus is on the textarea
     * */
    private final KeyListener previewFieldListener = new KeyAdapter() {

        @Override
        public void keyPressed(KeyEvent e) {    

            int keyCode = e.getKeyCode();
            int selectedIndex = visibleGems.getSelectedIndex();

            if (keyCode == KeyEvent.VK_UP) {
                selectedIndex--;
                e.consume();
                
            } else if (keyCode == KeyEvent.VK_DOWN) {
                selectedIndex++;
                e.consume();
            
            } else if (keyCode == KeyEvent.VK_PAGE_UP) {
                selectedIndex -= 10;
                if (selectedIndex < visibleGems.getModel().getSize()) {
                    selectedIndex = 0;
                }
                e.consume();
                
            } else if (keyCode == KeyEvent.VK_PAGE_DOWN) {
                selectedIndex += 10;
                if (selectedIndex > visibleGems.getModel().getSize()) {
                    selectedIndex = visibleGems.getModel().getSize() - 1;
                }
                e.consume();                
            
            } else if (keyCode == KeyEvent.VK_LEFT) {
                if (textComponent.getCaretPosition() > 0) {
                    refreshVisibleList(textComponent.getCaretPosition() - 1);
                    repositionPopup(textComponent.getCaretPosition() - 1);
                }
                
            } else if (keyCode == KeyEvent.VK_RIGHT) {
                if (textComponent.getCaretPosition() < textComponent.getText().length()) {
                    refreshVisibleList(textComponent.getCaretPosition() + 1);
                    repositionPopup(textComponent.getCaretPosition() + 1);
                }
            }
            
            if (selectedIndex >= 0 && selectedIndex < visibleGems.getModel().getSize()) {
                visibleGems.setSelectedIndex(selectedIndex);
                visibleGems.ensureIndexIsVisible(selectedIndex);
            }
        }
    };
    
    /**
     * MouseListener we use to listen for left double clicks to commit the user's choice.
     */
    private class DoubleClickMouseListener extends MouseClickDragAdapter {

        @Override
        public boolean mouseReallyClicked(MouseEvent e){
    
            boolean doubleClicked = super.mouseReallyClicked(e);
            
            if (doubleClicked && SwingUtilities.isLeftMouseButton(e)) {
                userCommited = true;
                autoCompleteManager.closeAutoCompletePopup();
            }
            
            return doubleClicked;
        }
    }

    /**
     * Constructor for AutoCompletePopupMenu.
     */
    public AutoCompletePopupMenu(AutoCompleteManager autoCompleteManager) {
        
        this.autoCompleteManager = autoCompleteManager;
        
        // initialize the mainPanel
        mainPanel = new JPanel(new BorderLayout());
        
        // layout stuff
        setSize(200, 200);
        
        userCommited = false;
         
        // create a scroll pane to display the list
        scrollPane = new JScrollPane();
        
        // layout stuff...
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        add(mainPanel);
        scrollPane.setPreferredSize(new Dimension(200,150));
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    }

    /**
     * Refresh the visible list to reflect the user input so far
     */
    private void refreshVisibleList(int caretPosition) {
        
        // Get the text that was in the text component
        String entireTextField = textComponent.getText();
        
        // Since MS Windows uses two characters for linefeed instead of one. (Unlike every other
        // OS in existence) We have to get rid of line feeds all together to get rid of parsing errors
        String windowsLineSeparator = "\r\n";
        entireTextField = entireTextField.replaceAll(windowsLineSeparator, "\n");
        
        final String textField = entireTextField;
        final AutoCompleteHelper ach = new AutoCompleteHelper(new AutoCompleteHelper.Document() {
            public char getChar(int offset) {
                return textField.charAt(offset);
            }

            public String get(int startIndex, int length) {
                return textField.substring(startIndex, startIndex + length);
            }                    
        });
        
        // Find the text we are completing (ex: no in "Prelude.no")
        String userInput = ach.getLastIncompleteIdentifier(caretPosition);
        userInputWordLength = userInput.length();        
        
        final Pair<String, List<Integer>> scopingAndOffset = ach.getIdentifierScoping(caretPosition);
        final String moduleNameString = scopingAndOffset.fst();
        final List<Integer> componentPositions = scopingAndOffset.snd();
        final int startOfModuleName = (componentPositions.get(0)).intValue();
        // Now if we are completing a qualification, find the module (ex: Prelude in "Prelude.no")
        final boolean qualification = moduleNameString.length() > 0;
        
        if (!qualification) {
            listAdapter.setModule(null);
            listAdapter.clear();
            visibleGems.refreshList(userInput);
            userInputQualifiedNameLength = userInputWordLength;
        } else {
            userInputQualifiedNameLength = caretPosition - startOfModuleName;
            try{
                final ModuleName moduleName = ModuleName.make(moduleNameString);
                final ResolutionResult resolution = perspective.getWorkingModuleTypeInfo().getModuleNameResolver().resolve(moduleName);

                listAdapter.setModule(resolution);
            }
            catch(IllegalArgumentException e){
                // if the module name is not valid then there are no suggestions available.
            }
            
            listAdapter.clear();
            visibleGems.refreshList(userInput);
        }
        
        

        if (visibleGems.getModel().getSize() == 0) {
            userCommited = false;
            autoCompleteManager.closeAutoCompletePopup();
        }
        
        if (visibleGems.getSelectedIndex() == -1) {
            visibleGems.setSelectedIndex(0);
        }
    }

    /**
     * Reposition the list below the specified caret position.
     * @param caretPos the caret position
     */    
    private void repositionPopup(int caretPos) {

        try {
            // reposition the popup below the cursor position
            Point location = textComponent.getUI().modelToView(textComponent, caretPos).getLocation();
            int height = textComponent.getFontMetrics(textComponent.getFont()).getHeight();
            show(textComponent, location.x, location.y + height);
            textComponent.requestFocus();
            
        } catch (BadLocationException ex) {
            // This shouldn't happen   
        }
    }
    
    /**
     * Display the popup menu. 
     * @param perspective the perspective to load gems from
     * @param invoker the component that invoked the popup menu
     * @param location at which location to display the popup menu (in invoker's coordinate space)
     */
    public void start(Perspective perspective, JTextComponent invoker, Point location) throws AutoCompleteException{

        userCommited = false;
        textComponent = invoker;
        oldKeyListeners = textComponent.getKeyListeners();
        this.perspective = perspective;
        
        for (final KeyListener oldKeyListener : oldKeyListeners) {
            textComponent.removeKeyListener(oldKeyListener);
        }
        
        listAdapter = new AutoCompleteIntellicutAdapter(perspective);
        IntellicutListModel listModel = new IntellicutListModel(listAdapter);

        visibleGems = new IntellicutList(IntellicutMode.NOTHING);
        visibleGems.setModel(listModel);
        listModel.load();
        
        // We don't want the list to ever have focus, focus should stay with the editor
        visibleGems.setFocusable(false);
        setFocusable(false);
        
        // Add all the requisite key listeners
        textComponent.addKeyListener(cancelAcceptKeyListener); 
        textComponent.getDocument().addDocumentListener(documentListener);
        textComponent.addKeyListener(previewFieldListener);
        
        visibleGems.addMouseListener(new DoubleClickMouseListener());
        scrollPane.setViewportView(visibleGems); 
        
        refreshVisibleList(textComponent.getCaretPosition());
        
        if (visibleGems.getModel().getSize() == 1) {
            // If there is only one option available, we automatically just choose it for the user.
            userCommited = true;
            visibleGems.setSelectedIndex(0);
            autoCompleteManager.closeAutoCompletePopup();
            
        } else if (visibleGems.getModel().getSize() == 0) {
            // If there are no options available, we display an error message.
            userCommited = false;
            throw (new AutoCompleteException("No Valid Autocomplete Entry"));
        
        } else {
            // If there are valid choices, then show the list.
            show(invoker, location.x, location.y);
            textComponent.requestFocus();
        }
    }
        
    /**
     * Return the user selected string. If the user committed, then the result is the selected GemEntity.
     * if the user cancelled, then the returned value is null.
     * @return IntellicutListEntry
     */
    IntellicutListEntry getSelected() {
        return (userCommited) ? visibleGems.getSelected() : null;
    }

    /**
     * Returns the length of the word that we want to complete
     * @return int
     */
    int getUserInputWordLength() {
        return userInputWordLength;
    }
    
    /**
     * Returns the length of the symbol that we want to complete
     * @return int
     */
    int getUserInputQualifiedNameLength() {
        return userInputQualifiedNameLength;
    }
    
    /** We override this to restore the key listeners we removed from the editor pane.
     * @see java.awt.Component#setVisible(boolean)
     */
    @Override
    public void setVisible(boolean show) {

        super.setVisible(show);

        if (!show) { 

            textComponent.getDocument().removeDocumentListener(documentListener);
            textComponent.removeKeyListener(cancelAcceptKeyListener);
            textComponent.removeKeyListener(previewFieldListener); 

            if (oldKeyListeners != null) {
                for (int i = 0; i < oldKeyListeners.length; i++) {
                    if (!Arrays.asList(textComponent.getKeyListeners()).contains(oldKeyListeners[i])){
                        textComponent.addKeyListener(oldKeyListeners[i]);
                    }
                }
            }
        }
    }
}
