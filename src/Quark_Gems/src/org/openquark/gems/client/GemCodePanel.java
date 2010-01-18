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
 * GemCodePanel.java
 * Creation date: (1/18/01 1:17:15 PM)
 * By: Luke Evans
 */
package org.openquark.gems.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.TooManyListenersException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.TransferHandler;
import javax.swing.text.BadLocationException;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.Highlight;

import org.openquark.cal.compiler.CodeAnalyser;
import org.openquark.cal.compiler.ModuleTypeInfo;
import org.openquark.cal.services.CALWorkspace;
import org.openquark.cal.services.Perspective;
import org.openquark.gems.client.EditorScrollPane.ErrorOffset;
import org.openquark.gems.client.caleditor.AdvancedCALEditor;
import org.openquark.gems.client.caleditor.AdvancedCALEditor.AmbiguityOffset;
import org.openquark.gems.client.utilities.MouseClickDragAdapter;


/**
 * The GemCodePanel is a set of components, most importantly containing an instance of the
 * CALEditor.  It is designed to allow the definition of a new Gem through the use of CAL
 * code.  The user types only the RHS of a supercombinator definition into the editor, and the
 * CodeGemEditor which contains this panel does the rest.
 * Creation date: (1/18/01 1:17:15 PM)
 * @author Luke Evans
 */
class GemCodePanel extends JPanel {
    private static final long serialVersionUID = -47798849290936051L;

    /** Whether the output type is considered good.  We keep track of this only to persist undo state. */
    private boolean outputGood = true;

    /** The displayed icon when the output is locked. */
    private static final ImageIcon lockedIcon;

    /** The display area for variables in the code panel. */
    private VariablesDisplay variablesDisplay;
    
    /** The display are for qualifications in the code panel. */
    private QualificationsDisplay qualificationsDisplay;

    /** The CAL editor into which code is entered. */
    private AdvancedCALEditor ivjCALEditorPane = null;
    
    /** The bottom panel that contains the message labels. */
    private JPanel ivjBottomPanel = null;
    
    /** The panel that displays the output type. */
    private JLabel ivjOutputTypeLabel = null;
    
    /** The scroll pane that contains the editor text area. */
    private EditorScrollPane ivjEditorScrollPane = null;
    
    /** The label for the error message. */
    private JLabel ivjMessageLabel = null;
    
    /** The split pane that contains the editor pane and the panel displays. */
    private JSplitPane ivjJSplitPane1 = null;
    
    /** The split pane that contains the qualifications & variables displays. */
    private JSplitPane displaysSplitPane = null;
    
    /** The perspective this panel was initialized with. */
    private final Perspective perspective;
    
    static {
        lockedIcon = new ImageIcon(GemCodePanel.class.getResource("/Resources/smallkey.gif"));
    }

    /**
     * A class that can hold the different elements needed for saving and restoring the state.
     * @author Edward Lam
     */
    private static class PanelState {

        final String errorMessage;
        final String outputTypeText;
        final String outputToolTipText;
        final boolean outputGood;
        final boolean outputLocked;

        private PanelState(GemCodePanel gemCodePanel) {

            String errorMessage = gemCodePanel.getMessageLabel().getText();
            if (errorMessage != null && errorMessage.length() == 0) {
                errorMessage = null;
            }
            this.errorMessage = errorMessage;

            JLabel typeLabel = gemCodePanel.getOutputTypeLabel();
            String outputTypeText = typeLabel.getText();
            if (outputTypeText != null && outputTypeText.length() == 0) {
                outputTypeText = null;
            }
            this.outputTypeText = outputTypeText;

            String outputToolTipText = typeLabel.getToolTipText();
            if (outputToolTipText != null && outputToolTipText.length() == 0) {
                outputToolTipText = null;
            }
            this.outputToolTipText = outputToolTipText;

            this.outputGood = gemCodePanel.outputGood;
            outputLocked = (typeLabel.getIcon() != null);
        }

        @Override
        public boolean equals(Object obj) {

            if (!(obj instanceof PanelState)) {
                return false;
            }

            PanelState ps = (PanelState)obj;

            return (errorMessage != null ? errorMessage.equals(ps.errorMessage) : true) &&
                    (outputTypeText != null ? outputTypeText.equals(ps.outputTypeText) : true) &&
                    (outputToolTipText != null ? outputToolTipText.equals(ps.outputToolTipText) : true) &&
                    outputGood == ps.outputGood &&
                    outputLocked == ps.outputLocked;
        }
        
        @Override
        public int hashCode() {
            return 325 * (257 + (errorMessage != null ? errorMessage.hashCode() : 0)) +
                   197 * (145 + (outputTypeText != null ? outputTypeText.hashCode() : 0)) +
                   101 * (65 + (outputToolTipText != null ? outputToolTipText.hashCode() : 0)) + 
                    37 * (17 + (outputGood? Boolean.TRUE : Boolean.FALSE).hashCode()) + 
                    (outputLocked ? Boolean.TRUE : Boolean.FALSE).hashCode();
        }
    }
    
    /**
     * A specialized CALEditor that displays a tooltip if the mouse hovers over source errors 
     * @author Frank Worsley
     */
    private class GemCodeEditor extends AdvancedCALEditor {
        private static final long serialVersionUID = -8391489120285354572L;

        public GemCodeEditor(ModuleTypeInfo moduleTypeInfo, CALWorkspace workspace) {
            super(moduleTypeInfo, workspace);
            setToolTipText("GemCodeEditor");
        }
        
        @Override
        public String getToolTipText(MouseEvent e) {

            try {
                
                int textOffset = getUI().viewToModel(this, e.getPoint());
                
                // See how closely the returned text offset actually matches the cursor position.
                // If the mouse hovers anywhere to the right of the last character on a line, then
                // the returned offset will be the offset of that character. This is because that
                // character is closest to the mouse position.
                Rectangle offsetRect = getUI().modelToView(this, textOffset);
                
                // The offset rectangle is very small, we need to make it a 
                // little bigger so that it is not to difficult to get a tooltip.
                offsetRect.x -= 5;
                offsetRect.y -= 5;
                offsetRect.width += 10;
                offsetRect.height += 10;
                
                if (!offsetRect.contains(e.getPoint())) {
                    // If the mouse is just hovering on empty space to the right
                    // of a line, then don't show a tooltip.
                    return null;
                }
                    
                // Build tooltip if there is an error at this position

                List<ErrorOffset> errorOffsets = ivjEditorScrollPane.getErrorOffsets();
                for (final ErrorOffset offset : errorOffsets) {
                
                    if (textOffset >= offset.getStartOffset() && textOffset <= offset.getEndOffset()) {
                        return "<html><body>" + ivjEditorScrollPane.getMessageToolTip(offset.getCompilerMessage()) + "</body></html>";
                    }
                }
                
                // There is no error at this position; so show metadata tooltip via superclass
                return super.getToolTipText(e);
                    
            } catch (BadLocationException ex) {
                throw new IllegalStateException("bad location displaying tooltip");
            }        
        }
    }

    /**
     * Default GemCodePanel constructor.
     * @param codeGem the related code gem.
     * @param gemCodeSyntaxListener a gem code syntax listener for the code panel
     * @param perspective the perspective to use to resolve entity names for tooltips
     */
    public GemCodePanel(CodeGem codeGem, GemCodeSyntaxListener gemCodeSyntaxListener, Perspective perspective) {

        this.perspective = perspective;

        // set the code we're editing
        String source = codeGem.getVisibleCode();
        getCALEditorPane().setText(source);
        
        setLayout(new BorderLayout());
        add(getBottomPanel(), "South");
        add(getJSplitPane1(), "Center");
        
        // Push focus onto the editor when we receive focus.
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                getCALEditorPane().requestFocus();    
            }
        });

        // Add a style listener
        try {
            getCALEditorPane().addCALSyntaxStyleListener(gemCodeSyntaxListener);
        } catch (TooManyListenersException e) {
            throw new IllegalStateException();
        }    
    }

    /**
     * Return the source code.
     * Creation date: (1/26/01 4:46:19 PM)
     * @return the source code
     */
    public String getCode() {
        return getCALEditorPane().getText();
    }

    /**
     * Adds an indicator for the given compiler message. An indicator will put a little
     * 'x' into the side panel next to the line the error occurs in and will highlight
     * the source that caused the error.
     * @param message
     */
    void addErrorIndicator(CodeAnalyser.OffsetCompilerMessage message) {
        getEditorScrollPane().addErrorIndicator(message);
        setErrorMessage(GemCutter.getResourceString("CGE_Broken"));
    }
    
    /**
     * Removes all error indicators.
     */
    void clearErrorIndicators() {
        getEditorScrollPane().clearErrorIndicators();
        setErrorMessage(null);
    }
    
    /**
     * Refreshes the ambiguity indicators on the editor panel
     */
    public void updateAmbiguityIndicators() {
        ivjCALEditorPane.updateAmbiguityIndicators();
    }
    
    /**
     * Displays the given error message.
     * @param message the error message to display (use null to clear)
     */
    void setErrorMessage(String message) {
        getMessageLabel().setText(message);
        getMessageLabel().setToolTipText(ToolTipHelpers.wrapTextToHTMLLines(message, this));
    }
    
    /**
     * Set the list of analyzed identifiers from the code
     * @param sourceIdentifiers (List of AnalysedIdentifier)
     */
    void setSourceIdentifiers(List<CodeAnalyser.AnalysedIdentifier> sourceIdentifiers) {
        ivjCALEditorPane.setSourceIdentifiers(sourceIdentifiers);
    }
    
    /*
     * Methods to access GUI components *******************************************************************
     */

    /**
     * Return the BottomPanel property value.
     * @return JPanel
     */
    private JPanel getBottomPanel() {
        if (ivjBottomPanel == null) {
            try {
                ivjBottomPanel = new JPanel();
                ivjBottomPanel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
                ivjBottomPanel.setLayout(new BoxLayout(ivjBottomPanel, BoxLayout.X_AXIS));
                ivjBottomPanel.add(getOutputTypeLabel());
                ivjBottomPanel.add(Box.createHorizontalGlue());
                ivjBottomPanel.add(getMessageLabel());
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjBottomPanel;
    }

    /**
     * Return the CALEditorPane property value.
     * @return org.openquark.gems.client.CALEditor
     */
    AdvancedCALEditor getCALEditorPane() {
        if (ivjCALEditorPane == null) {
            try {
                ivjCALEditorPane = new GemCodeEditor(perspective.getWorkingModuleTypeInfo(), perspective.getWorkspace());
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjCALEditorPane;
    }

    /**
     * @return EditorScrollPane the scrollpane for the CALEditor. 
     */
    private EditorScrollPane getEditorScrollPane() {
        if (ivjEditorScrollPane == null) {
            try {
                ivjEditorScrollPane = new EditorScrollPane(getCALEditorPane());
                
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjEditorScrollPane;
    }

    /**
     * Get the variables display associated with this panel.
     * Creation date: (Jul 16, 2002 4:36:52 PM)
     * @return VariablesDisplay the associated VariablesDisplay.
     */
    VariablesDisplay getVariablesDisplay() {
        if (variablesDisplay == null) {
            variablesDisplay = new VariablesDisplay();
            variablesDisplay.addListFocusListener(new FocusAdapter() {
                 @Override
                public void focusLost(FocusEvent e) {
                    // deselect item if focus lost
                    variablesDisplay.clearSelection();
                }
            });
        }
        return variablesDisplay;
    }
    
    /**
     * Get the qualifications display associated with this panel.
     * @return VariablesDisplay the associated VariablesDisplay.
     */
    QualificationsDisplay getQualificationsDisplay() {
        if (qualificationsDisplay == null) {
            qualificationsDisplay = new QualificationsDisplay(perspective.getWorkspace());
            qualificationsDisplay.addListFocusListener(new FocusAdapter() {
                 @Override
                public void focusLost(FocusEvent e) {
                    // deselect item if focus lost
                    qualificationsDisplay.clearSelection();
                }
            });
        }
        return qualificationsDisplay;
    }
    
    /**
     * Return the variables JSplitPane1 .
     * @return JSplitPane
     */
    private JSplitPane getDisplaysSplitPane() {
        if (displaysSplitPane == null) {
            try {
                displaysSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
                displaysSplitPane.setName("JSplitPane1");
                displaysSplitPane.setOneTouchExpandable(true);
                displaysSplitPane.setTopComponent(getVariablesDisplay()); 
                displaysSplitPane.setBottomComponent(getQualificationsDisplay()); 
                displaysSplitPane.setDividerLocation(0.5);

            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return displaysSplitPane;
    }

    /**
     * Return the main JSplitPane1 property value.
     * @return JSplitPane
     */
    private JSplitPane getJSplitPane1() {
        if (ivjJSplitPane1 == null) {
            try {
                ivjJSplitPane1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
                ivjJSplitPane1.setName("JSplitPane1");
                ivjJSplitPane1.setDividerSize(2);
                getJSplitPane1().add(getEditorScrollPane(), "right");
                getJSplitPane1().add(getDisplaysSplitPane(), "left");

            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJSplitPane1;
    }

    /**
     * Return the Message property value.
     * @return JLabel
     */
    private JLabel getMessageLabel() {
        if (ivjMessageLabel == null) {
            try {
                ivjMessageLabel = new JLabel();
                ivjMessageLabel.setName("Message");
                ivjMessageLabel.setText("");
                ivjMessageLabel.setForeground(Color.red);

            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjMessageLabel;
    }

    /**
     * Return the OutputTypeLabel property value.
     * @return JLabel
     */
    private JLabel getOutputTypeLabel() {
        if (ivjOutputTypeLabel == null) {
            try {
                ivjOutputTypeLabel = new JLabel();
                ivjOutputTypeLabel.setName("OutputType");
                ivjOutputTypeLabel.setText("-> " + GemCutter.getResourceString("CGE_Undefined_Type"));

            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjOutputTypeLabel;
    }

    /**
     * Update the label that displays the output type of the code gem.
     * @param typeText the text of the label.  If non-null, can optionally be wrapped in \<html\> tags
     * @param toolTipText the text to display for the tooltip.
     * @param locked whether the output is locked
     * @param good whether the output is considered good
     */
    void updateOutputTypeLabel(String typeText, String toolTipText, boolean locked, boolean good) {

        JLabel typeLabel = getOutputTypeLabel();

        typeLabel.setIcon(locked ? lockedIcon : null);
        typeLabel.setText(typeText);
        if (toolTipText == null) {
            typeLabel.setToolTipText(typeText);
        } else {
            typeLabel.setToolTipText(toolTipText);
        }

        outputGood = good;
        if (good) {
            typeLabel.setBackground(Color.black);
            typeLabel.setForeground(Color.black);
        } else {
            typeLabel.setBackground(Color.red);
            typeLabel.setForeground(Color.red);
        }
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

    /*
     * Methods supporting javax.swing.undo.StateEditable ********************************************
     */

    /**
     * Restore the stored state.
     * This will make the panel consistent with the code gem and displayed code gem, so update those first!
     * Creation date: (04/02/2002 6:12:00 PM)
     * @param state Hashtable the stored state
     */
    public void restoreState(Hashtable<?, ?> state) {

        PanelState panelState = (PanelState)state.get(this);
        if (panelState != null) {
            setErrorMessage(panelState.errorMessage);

            updateOutputTypeLabel(panelState.outputTypeText, panelState.outputToolTipText,
                    panelState.outputLocked, panelState.outputGood);
        }
    }

    /**
     * Save the state.
     * Creation date: (04/02/2002 6:12:00 PM)
     * @param state Hashtable the table in which to store the state
     */
    public void storeState(Hashtable<Object, Object> state) {

        state.put(this, new PanelState(this));
    }
}

/**
 * A special scrollpane to use for the CALEditor component. It manages the side panel
 * that displays little error icons for lines with errors in them.
 * @author Frank Worsley
 */
class EditorScrollPane extends JScrollPane {

    private static final long serialVersionUID = -3660183287664833150L;

    /**
     * This class implements a little side panel that displays error icons
     * next to the line of code that has an error in it.
     * @author Frank Worsley
     */
    private class SidePanel extends JPanel {
    
        private static final long serialVersionUID = -5172117175135917290L;

        /**
         * Mouse handler to receive clicks on error icons.
         */
        private class MouseHandler extends MouseClickDragAdapter {
            /**
             * Constructor for the Mouse Handler
             */
            private MouseHandler() {
            }
            
            /**
             * Surrogate method for mouseClicked.  Called only when our definition of click occurs.
             * 
             * Clicking on an ambiguity indicator causes the editor to select the first
             * ambiguity on the line, and display a menu for that identifier.
             * 
             * @param e MouseEvent the relevant event
             * @return boolean true if the click was a double click
             */
            @Override
            public boolean mouseReallyClicked(MouseEvent e){

                AdvancedCALEditor editor = EditorScrollPane.this.getEditor();
                
                boolean doubleClicked = super.mouseReallyClicked(e);
                
                for (final AmbiguityOffset offset : getEditor().getAmbiguityOffsets()) {

                    int y = offset.getLineNumber() * lineHeight + lineOffset;
                    Rectangle iconRect = new Rectangle(2, y+2, ambiguityIcon.getIconWidth()+2, ambiguityIcon.getIconHeight()+2);
                    
                    if (iconRect.contains(e.getPoint())) {
                        // Select ambiguity
                        editor.select(offset.getStartOffset(), offset.getEndOffset());
                        
                        // Display menu at ambiguity location 
                        
                        AdvancedCALEditor.IdentifierPopupMenuProvider popupProvider = editor.getPopupMenuProvider();
                        if (popupProvider == null) {
                            break;
                        }
                        
                        Point menuPoint;
                        try {
                            Rectangle offsetRect = editor.getUI().modelToView(editor, offset.getStartOffset());
                            menuPoint = new Point(offsetRect.x, offsetRect.y + offsetRect.height);
                        } catch (BadLocationException ex) {
                            throw new IllegalStateException("bad location displaying ambiguity popup");
                        }
                        
                        CodeAnalyser.AnalysedIdentifier identifier = offset.getIdentifier();
                        AdvancedCALEditor.PositionlessIdentifier positionlessIdentifier = 
                            new AdvancedCALEditor.PositionlessIdentifier(identifier.getName(), identifier.getRawModuleName(), identifier.getResolvedModuleName(), identifier.getMinimallyQualifiedModuleName(), identifier.getCategory(), identifier.getQualificationType());
                        
                        JPopupMenu menu = popupProvider.getPopupMenu(positionlessIdentifier);
                        menu.show(getEditor(), menuPoint.x, menuPoint.y);
                        
                        break;
                        
                    }
                }
                
                return doubleClicked;
            }
        }
        
        /** The height in pixels of a single line of text. */
        private final int lineHeight;
    
        /** The offset from the top to draw the error icon at. */
        private final int lineOffset;
        
        /**
         * Constructor for a new SidePanel.
         */
        public SidePanel() {

            FontMetrics metrics = getEditor().getFontMetrics(getEditor().getFont());
            Insets margin = getEditor().getMargin();
        
            this.lineHeight = metrics.getHeight();
            this.lineOffset = margin != null ? margin.top : 0;
        
            setOpaque(true);
            setToolTipText("SidePanel");
            setBackground(Color.LIGHT_GRAY);
            setPreferredSize(new Dimension(errorIcon.getIconWidth(), getEditor().getHeight()));
            addMouseListener(new MouseHandler());
            
            // We need to grow to the same size if the editor resizes
            getEditor().addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    setPreferredSize(new Dimension(errorIcon.getIconWidth(), getEditor().getHeight()));
                }
            });
        }
    
        /**
         * Draws the error and ambiguity for the side panel.
         * If multiple errors or ambiguities occur on the same line, the icons 
         * will indicate multiplicity.
         * If ambiguity icons occur on a line, the line will contain typecheck
         * errors, but only the ambiguity icons will be displayed.
         * 
         * @param g the graphics object to draw with
         */
        @Override
        public void paintComponent(Graphics g) {
        
            super.paintComponent(g);
            
            // Show ambiguity indicators
            List<Integer> ambiguityLines = new ArrayList<Integer>();
            for (final AmbiguityOffset offset : getEditor().getAmbiguityOffsets()) {
                
                Integer lineNum = Integer.valueOf(offset.getLineNumber());
                
                int y = offset.getLineNumber() * lineHeight + lineOffset;
                if (!ambiguityLines.contains(lineNum)) {
                    g.drawImage(ambiguityIcon.getImage(), 2, y+2, this);
                    ambiguityLines.add(lineNum);
                } else {
                    g.drawImage(ambiguityIcon.getImage(), 4, y+4, this);
                }
            }
            
            // Show error indicators
            List<Integer> errorLines = new ArrayList<Integer>();
            for (final ErrorOffset offset : errorOffsets) {
                
                Integer lineNum = Integer.valueOf(offset.getLineNumber());

                if (!ambiguityLines.contains(lineNum)) {
                    // Ambiguity does not have 
                    int y = offset.getLineNumber() * lineHeight + lineOffset;
                    if (!errorLines.contains(lineNum)) {
                        g.drawImage(errorIcon.getImage(), -1, y, this);
                        errorLines.add(lineNum);
                    } else {
                        g.drawImage(errorIcon.getImage(), 1, y+1, this);
                    }
                }
            }
        }
    
        /**
         * @param e the MouseEvent that triggered the tooltip
         * @return the error description within a tooltip if the mouse is over an error icon.
         */
        @Override
        public String getToolTipText(MouseEvent e) {
            return getToolTipText(e.getPoint());
        }
        
        /**
         * @param mousePoint the location of the mouse
         * @return the error description within a tooltip if the point is over an error icon.
         */
        private String getToolTipText(Point mousePoint) {

            List<String> messages = new ArrayList<String>();

            for (final ErrorOffset offset : errorOffsets) {

                int y = offset.getLineNumber() * lineHeight + lineOffset;
                Rectangle iconRect = new Rectangle(0, y, errorIcon.getIconWidth(), errorIcon.getIconHeight());
        
                if (iconRect.contains(mousePoint)) {
                    messages.add(getMessageToolTip(offset.getCompilerMessage()));
                }
            }

            if (messages.isEmpty()) {
                return null;
                
            } else if (messages.size() == 1) {
                return "<html><body>" + messages.get(0) + "</body></html>";
                
            } else {
                StringBuilder buffer = new StringBuilder();
                
                buffer.append("<html><body>");
                buffer.append("<b>" + GemCutter.getResourceString("CGE_MultipleErrors") + "</b>");
                
                for (final String message : messages) {
                    buffer.append("<br><br>");
                    buffer.append(message);
                }

                buffer.append("</html></body>");                
                return buffer.toString();
            }
        }
    }

    /** 
     * A convenience class for storing a compiler message and the associated error position.
     * @author Frank Worsley
     */
    static class ErrorOffset {
        
        private final CodeAnalyser.OffsetCompilerMessage message;
        private final AdvancedCALEditor.EditorLocation errorLocation;
        
        private ErrorOffset(CodeAnalyser.OffsetCompilerMessage message, AdvancedCALEditor.EditorLocation offset) {
            
            if (message == null) {
                throw new NullPointerException();
            }
            
            this.message = message;
            this.errorLocation = offset;
        }
        
        public int getStartOffset() {
            return errorLocation.getStartOffset();
        }
        
        public int getEndOffset() {
            return errorLocation.getEndOffset();
        }
        
        public int getLineNumber() {
            return errorLocation.getLineNumber();
        }
        
        public CodeAnalyser.OffsetCompilerMessage getCompilerMessage() {
            return message;
        }
    }
    

    /** The icon to draw to indicate there is an error on a line. */
    private static final ImageIcon errorIcon = new ImageIcon(GemCodePanel.class.getResource("/Resources/error.gif"));
    
    /** The icon to draw to indicate there is an ambiguity on a line. */
    private static final ImageIcon ambiguityIcon = new ImageIcon(GemCodePanel.class.getResource("/Resources/smallquery.gif"));

    /** The side panel that displays the error icons. */
    private final SidePanel sidePanel = new SidePanel();
    
    /** The set of error offsets that must be indicated. */
    private final List<ErrorOffset> errorOffsets = new ArrayList<ErrorOffset>();
        
    /**
     * Constructs a new scrollpane for the given editor.
     * @param editor the editor
     */
    public EditorScrollPane(AdvancedCALEditor editor) {
        super (editor);

        setRowHeaderView(sidePanel);
        setTransferHandler(new TransferHandler("text"));        
        setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    }

    /**
     * @return the editor this scrollpane is for
     */
    public AdvancedCALEditor getEditor() {
        return (AdvancedCALEditor) getViewport().getView();
    }
    
    /**
     * Adds an error indicate for the given compiler message.
     * @param message the message to display an indicator for
     */
    public void addErrorIndicator(CodeAnalyser.OffsetCompilerMessage message) {
        
        ErrorOffset offset = getErrorOffset(message);
        
        if (offset == null) {
            return;
        }
        
        errorOffsets.add(offset);
        sidePanel.repaint();
                
        try {
            Highlighter highlighter = getEditor().getHighlighter();
            highlighter.addHighlight(offset.getStartOffset(), offset.getEndOffset(), new ErrorUnderlineHighlightPainter());
            
        } catch (BadLocationException ex) {
            throw new IllegalStateException("bad location adding highlight");
        }
    }
    
    /**
     * Clears all messages and removes all error indicators.
     */
    public void clearErrorIndicators() {
        
        // We can't simply remove all highlighters, since if the user has text selected,
        // there will be a highlighter to indicate the text selection. This fixes a bug
        // where selected text becomes invisible after the syntax smarts run, because
        // the selection highlight was removed here.
        Highlighter highlighter = getEditor().getHighlighter();
        Highlight[] highlights = highlighter.getHighlights();
        
        for (final Highlight highlight : highlights) {
            if (highlight.getPainter() instanceof ErrorUnderlineHighlightPainter) {
                highlighter.removeHighlight(highlight);
            }
        }

        errorOffsets.clear();
        sidePanel.repaint();
    }
    
    /**
     * @return the ErrorOffsets for the error indicators
     */
    public List<ErrorOffset> getErrorOffsets() {
        return Collections.unmodifiableList(errorOffsets);
    }

    /**
     * @param message the message to build a tooltip for
     * @return an HTML formatted tooltip for the given message
     */    
    public String getMessageToolTip(CodeAnalyser.OffsetCompilerMessage message) {
            
        StringBuilder buffer = new StringBuilder();

        buffer.append("<b>");
        buffer.append(ToolTipHelpers.wrapTextToHTMLLines(message.getMessage().replaceAll("<", "&lt;").replaceAll(">", "&gt;"), this));

        buffer.append("</b>");
        
        Exception ex = message.getException();
        if (ex != null) {

            // Add caused by message.
            buffer.append("<br>");
            buffer.append(ToolTipHelpers.wrapTextToHTMLLines(ex.getMessage().replaceAll("<", "&lt;").replaceAll(">", "&gt;"), this));
        }

        return buffer.toString();
    }

    /**
     * Converts the source position of the error into a text offset, length, and line number.
     * @param message the compiler message to convert the source position for.
     */
    private ErrorOffset getErrorOffset(CodeAnalyser.OffsetCompilerMessage message) {

        if (!message.hasPosition()) {
            return null;
        }

        try {

            AdvancedCALEditor.EditorLocation offset = getEditor().getEditorTokenOffset(message.getOffsetLine(), message.getOffsetColumn(), -1);
            return new ErrorOffset(message, offset);
            
        } catch (BadLocationException ex) {
            throw new IllegalArgumentException("invalid location trying to convert compiler message source position");
        }
    }

}

/** Highlight painter for errors */
class ErrorUnderlineHighlightPainter extends AdvancedCALEditor.UnderlineHighlightPainter {

    private static final Color LINE_COLOR = Color.RED;
    @Override
    public Color getLineColor() {
        return LINE_COLOR;
    }
}