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
 * CommonDialog.java
 * Created: Jul 26, 2004
 * By: Kevin Sit
 */
package org.openquark.util.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.FocusEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;


/**
 * This class subclasses from <code>BasicDialog</code> to provide a common look and
 * feel for all the dialogs.  Subclasses can override certain methods in this class 
 * to alter the layout.
 */
public abstract class CommonDialog extends DialogBase {
    
    /** The headline string: show this if it is set (optional) */
    private String headline;
    
    /** The description string: show this if it is set (optional) */
    private String description;
    
    /** The icon for this dialog (optional) */
    private Icon icon;
    
    /** Sets this flag to true once the initializeUI() method is called. */
    private boolean initialized;
    
    /** The OK button. */
    private JButton okButton;
    
    /**
     * @param frame
     * @param title
     */
    public CommonDialog(Frame frame, String title) {
        super(frame, title);
    }
    
    /**
     * Returns the description of this dialog, or <code>null</code> if there
     * is no description.
     * @return String 
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Sets the description for this dialog.
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * Returns the icon for this dialog, or <code>null</code> if there is no icon.
     * @return Icon 
     */
    public Icon getIcon() {
        return icon;
    }
    
    /**
     * Sets the icon for this dialog.
     * @param icon
     */
    public void setIcon(Icon icon) {
        this.icon = icon;
    }
    
    /**
     * Returns the OK button.
     * @return okButton
     */
    protected JButton getOkButton() {
        return okButton;
    }

    /**
     * Returns the headline for this dialog.  A headline is an one-line text that
     * describes the operation that the dialog is performing.  By default, this
     * is the same as the title.
     * @return String
     */
    public String getHeadline() {
        if (headline == null) {
            return getTitle();
        } else {
            return headline;            
        }
    }
    
    /**
     * Sets the headline for this dialog.
     * @param headline
     */
    public void setHeadline(String headline) {
        this.headline = headline;
    }
    
    /**
     * @see java.awt.Dialog#show()
     */
    @Override
    public void show() {
        // initialize the UI lazily
        if (!initialized) {
            initializeUI();
            initializeDefaults();
            installListeners();
            initialized = true;
        }
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
     * Enables or disables the OK button.  Subclasses can use this method
     * to disable the OK button if some operations are not completed.
     * @param enabled
     */
    protected void setOKEnabled(boolean enabled) {
        if (okButton != null) {
            okButton.setEnabled(enabled);
        }
    }
    
    /**
     * Creates and initializes the UI of this dialog.  This initialization method
     * will be invoked when the dialog is first shown.
     * <p>
     * Subclasses should not override this method.
     */
    protected void initializeUI() {
        JPanel container = new JPanel(new BorderLayout());
        
        // add the headline/title component if it exists
        Component c = createHeadlineComponent();
        if (c != null) {
            container.add(c, BorderLayout.NORTH);
        }
        
        // Creates a container that wraps around the content and the command bar
        JPanel contentContainer = new JPanel(new BorderLayout(0, 5));
        contentContainer.setBorder(BorderFactory.createEmptyBorder(12, 12, 11, 11));
        contentContainer.add(createContentComponent(), BorderLayout.CENTER);
        contentContainer.add(createCommandBarComponent(), BorderLayout.SOUTH);
        container.add(contentContainer, BorderLayout.CENTER);
        
        // Sets the default values, installs the listeners and set the container
        setContentPane(container);
    }
    
    
    /**
     * Initializes the UI components with default value.  This method is called 
     * immediately after the UI components are created.
     * <p>
     * Subclasses are encouraged to override this method. 
     */
    protected void initializeDefaults() {
    }
    
    /**
     * Installs listeners to the interactive components present in this dialog.
     * This method is called once after the UI components are initialized and
     * the default values are set.
     * <p>
     * Subclasses can override this method.
     */
    protected void installListeners() {
    }
    
    /**
     * Creates and returns the component that is used to display the summary,
     * description and icon for the dialog.  If this method returns <code>null</code>,
     * then no summary component will be displayed.
     * <p>
     * Subclasses can override this method.
     * @return Component
     */
    protected Component createHeadlineComponent() {
        JPanel container = null;
        if (getHeadline() != null) {
            container = new JPanel(new GridBagLayout());
            container.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            container.setBackground(Color.WHITE); // TODO hardcoded color
            
            // Create the grid bag constraints
            GridBagConstraints gbc = new GridBagConstraints ();
            gbc.anchor = GridBagConstraints.NORTHWEST;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1;
            gbc.weighty = 0;
            
            // Add the dialog summary
            gbc.gridx = 0;
            gbc.gridy = 0;
            JLabel dialogSummary = new JLabel(getHeadline());
            UIUtilities.setFontBold(dialogSummary, true);
            Font orgFont = dialogSummary.getFont();
            dialogSummary.setFont(orgFont.deriveFont((float)orgFont.getSize()+1));
            container.add(dialogSummary, gbc);
    
            // Display the description if necessary
            String description = getDescription();
            if (description != null && description.length() > 0) {
                gbc.gridx = 0;
                gbc.gridy = 1;
                gbc.weightx = 1;
                gbc.weighty = 1;
                gbc.fill = GridBagConstraints.BOTH;
                JTextArea pageDescription = new JTextArea();
                pageDescription.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                pageDescription.setFont(new JLabel().getFont());
                pageDescription.setLineWrap(true);
                pageDescription.setWrapStyleWord(true);
                pageDescription.setOpaque(false);
                pageDescription.setEditable(false);
                pageDescription.setText(description);
                container.add(pageDescription, gbc);
            }
            
            // Display the icon for the editor page if necessary
            Icon icon = getIcon();
            if (icon != null) {
                gbc.gridx = 1;
                gbc.gridy = 0;
                gbc.gridheight = 2;
                gbc.weightx = 0;
                gbc.weighty = 0;
                gbc.fill = GridBagConstraints.NONE;
                JLabel pageImage = new JLabel();
                pageImage.setIcon(icon);
                container.add(pageImage, gbc);
            }
        }
        return container;
    }
    
    /**
     * Creates and returns the main component that contains most UI editing
     * components.
     * <p>
     * Subclasses must override this method to provide details for this
     * dialog.
     * @return Component
     */
    protected abstract Component createContentComponent();
    
    /**
     * Creates and returns the component that contains one or more command button
     * that usually appears at the bottom of the dialog.  By default, two buttons
     * are provided: OK and Cancel.
     * <p>
     * Subclasses can override this method.
     * @return Component
     */
    protected Component createCommandBarComponent() {
        Box container = Box.createHorizontalBox();

        // the buttons should be right aligned
        container.add(Box.createHorizontalGlue());
        
        // add the ok/cancel buttons
        okButton = makeOKButton();
        container.add(okButton);
        container.add(Box.createHorizontalStrut(5)); // filler
        container.add(makeCancelButton());
        getRootPane().setDefaultButton(okButton);
        return container; 
    }
    
    /** 
     * Called when the component will no longer be used to allow it to release any resources.
     * Should be overridden by subclasses to remove listeners etc.
     */
    @Override
    public void dispose() {
    }
    
    /**
     * Changes the focus to the given component.  Useful to be called right before show()
     * to set the initial focus of the dialog.
     * 
     * @param target The component the should get the focus.
     */
    protected void changeFocus(final Component target) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                target.dispatchEvent(new FocusEvent(target,
                        FocusEvent.FOCUS_GAINED));
            }
        });
    }

}
