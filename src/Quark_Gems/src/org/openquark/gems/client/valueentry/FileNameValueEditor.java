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
 * FileNameValueEditor.java
 * Creation date: (07/03/01 2:00:19 PM)
 * By: Michael Cheng
 */
package org.openquark.gems.client.valueentry;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JPanel;

import org.openquark.cal.valuenode.FileNameValueNode;
import org.openquark.cal.valuenode.ValueNode;


/**
 * ValueEditor for entering FileName values.  Uses a JFileChooser to look
 * thru the system file system for files.
 * Creation date: (07/03/01 2:00:19 PM)
 * @author Michael Cheng
 */
class FileNameValueEditor extends ValueEditor {

    private static final long serialVersionUID = 299242996366770864L;

    /**
     * A custom value editor provider for the FileNameValueEditor.
     */
    public static class FileNameValueEditorProvider extends ValueEditorProvider<FileNameValueEditor> {
        
        public FileNameValueEditorProvider(ValueEditorManager valueEditorManager) {
            super(valueEditorManager);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean canHandleValue(ValueNode valueNode, SupportInfo providerSupportInfo) {
            return valueNode instanceof FileNameValueNode;
        }

        /**
         * @see org.openquark.gems.client.valueentry.ValueEditorProvider#getEditorInstance(ValueEditorHierarchyManager, ValueNode)
         */
        @Override
        public FileNameValueEditor getEditorInstance(ValueEditorHierarchyManager valueEditorHierarchyManager,
                                             ValueNode valueNode) {
            
            FileNameValueEditor editor = new FileNameValueEditor(valueEditorHierarchyManager);
            editor.setOwnerValueNode(valueNode);
            return editor;
        }
        
    }

    private JPanel ivjIntermediatePanel = null;
    private FileNameChooser fileNameChooser;

    /**
     * A specialized JFileChooser which correctly handles approve selection (commit) and 
     * cancel selection (cancel) user input.
     */
    private class FileNameChooser extends JFileChooser {

        private static final long serialVersionUID = -4742367572560806845L;

        public FileNameChooser() {
            super();

            setFileSelectionMode(FILES_AND_DIRECTORIES); // both file names and directory names are admissable as FileName values
            setApproveButtonText(ValueEditorMessages.getString("VE_OKButtonLabel"));
        }

        @Override
        public void approveSelection() {
            super.approveSelection();
            handleCommitGesture();
        }

        @Override
        public void cancelSelection() {
            super.cancelSelection();
            handleCancelGesture();
        }
    }

    /**
     * FileNameValueEditor constructor.
     * @param valueEditorHierarchyManager
     */
    protected FileNameValueEditor(ValueEditorHierarchyManager valueEditorHierarchyManager) {

        super(valueEditorHierarchyManager);
        initialize();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void commitValue() {
        // If there is a selected file, then update the value, else keep old value.
        File file = fileNameChooser.getSelectedFile();
        if (file != null) {
            ValueNode returnVN = new FileNameValueNode(file.getPath(), getValueNode().getTypeExpr());
            replaceValueNode(returnVN, true);

            notifyValueCommitted();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Component getDefaultFocusComponent() {
        return fileNameChooser;
    }

    /**
     * Returns the FileNameValueNode containing the data for this FileNameValueEditor.
     * Creation date: (04/07/01 10:37:57 AM)
     * @return org.openquark.gems.client.valueentry.FileNameValueNode
     */
    public FileNameValueNode getFileNameValueNode() {
        return (FileNameValueNode) getValueNode();
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
     * Initialize the class.
     * Note: Extra set-up code has been added.
     */
    private void initialize() {
        
        setName("FileNameValueEditor");
        setLayout(new BorderLayout());
        
        add(getIntermediatePanel(), "Center");

        // Add the file chooser to the display.
        fileNameChooser = new FileNameChooser();
        getIntermediatePanel().add(fileNameChooser, BorderLayout.CENTER);

        setSize(getPreferredSize());
        setResizable(true);

        // Make sure that no mouse clicks 'leak' thru 
        // (meaning that the component under this ColourValueEditor may get the mouse click).
        addMouseListener(new MouseAdapter() {
        });
    }
    
    /**
     * Sets the initial value.
     * Creation date: (07/03/01 2:00:19 PM)
     */
    @Override
    public void setInitialValue() {
        fileNameChooser.setSelectedFile(new File(getFileNameValueNode().getFileName()));
    }
}
