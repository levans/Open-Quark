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
 * SearchTextField.java
 * Created: Sept 9, 2004
 * By: Richard Webster
 */
package org.openquark.util.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * A text field component for triggering searches in a searchable component.
 * @author Richard Webster
 */
public class SearchTextField extends JTextField {
    private static final long serialVersionUID = 2681980740137810254L;

    /**
     * The text shown initially in the edit field.
     */
    private static String initialSearchText = UIMessages.instance.getString("Initial_Search_Text"); //$NON-NLS-1$

    /** The timer used for updating the search results if the user stopped typing */
    private final Timer searchUpdateTimer;

    /**
     * SearchTextField constructor.
     * The specified tree will be made searchable if not already.
     * @param tree  the tree to be searched
     */
    public SearchTextField(UtilTree tree) {
        this(tree instanceof SearchableComponent ? (SearchableComponent) tree : new SearchableTree(tree));
    }

    /**
     * SearchTextField constructor.
     * @param searchableComponent  the component in which searches will be triggered
     */
    public SearchTextField(final SearchableComponent searchableComponent) {
        super(initialSearchText);

//        // Trigger a search when Enter is pressed.
//        // TODO: also trigger a search a short time after an edit is made to the text...
//        addActionListener(new ActionListener() {
//                public void actionPerformed(ActionEvent e) {
//                    searchableComponent.showSearchResults(getText());
//                }
//            });

        // Select all the text when focus is gained.
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                setCaretPosition(0);
                setSelectionStart(0);
                setSelectionEnd(getText().length());
            }
        });

        // A timer that runs once the user stops typing.
        // This will execute the model search with the text the user has entered.
        searchUpdateTimer = new Timer(250, new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (e.getSource() == searchUpdateTimer) {
                        searchableComponent.showSearchResults(getText());
                    }
                }
            });

        searchUpdateTimer.setRepeats(false);
        
        // Add a document listener so we can update the search results if the text changes.
        getDocument().addDocumentListener(new DocumentListener () {
            public void insertUpdate(DocumentEvent e) {
                searchUpdateTimer.restart();
            }

            public void removeUpdate(DocumentEvent e) {
                searchUpdateTimer.restart();
            }

            public void changedUpdate(DocumentEvent e) {
            }
        });

    }
}
