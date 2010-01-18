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
 * JavaForeignImportModuleGenerator.java
 * Creation date: Sep 27, 2005.
 * By: Edward Lam
 */
package org.openquark.gems.client.generators;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.LayoutFocusTraversalPolicy;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileView;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.TypeChecker;
import org.openquark.cal.compiler.SourceModel.ModuleDefn;
import org.openquark.cal.services.CALFeatureName;
import org.openquark.cal.services.Perspective;
import org.openquark.cal.services.ResourceName;
import org.openquark.cal.services.Status;
import org.openquark.gems.client.GemCutter;
import org.openquark.gems.client.ValueRunner;
import org.openquark.gems.client.jfit.ForeignImportGenerator;
import org.openquark.gems.client.jfit.JFit;
import org.openquark.gems.client.valueentry.ValueEditorManager;
import org.openquark.util.FileSystemHelper;
import org.openquark.util.UnsafeCast;
import org.openquark.util.ui.DetailsDialog;
import org.openquark.util.ui.DialogBase;
import org.openquark.util.ui.ExtensionFileFilter;
import org.openquark.util.ui.SortedListModel;



/**
 * This is the container class for the generator to create a cal module importing multiple Java functions and types.
 * @author Edward Lam
 */
public final class JavaForeignImportModuleGenerator extends DialogBase {

    private static final long serialVersionUID = -1549293981732061092L;

    /** The value of the "java.home" system property.  This is guaranteed to be defined. */
    private static final String JAVA_HOME = System.getProperty("java.home");

    /** The icon used by the generator. */
    private static final Icon GENERATOR_ICON = new ImageIcon(GemGenerator.class.getResource("/Resources/supercombinator.gif"));
    
    /** The icon to use for error messages. */
    private static final Icon ERROR_ICON = new ImageIcon(GemCutter.class.getResource("/Resources/error.gif"));

    /** The icon to use for warning messages. */
    private static final Icon WARNING_ICON = new ImageIcon(GemCutter.class.getResource("/Resources/warning.gif"));
    
    /** The icon to use if everything is ok. */
    private static final Icon OK_ICON = new ImageIcon(GemCutter.class.getResource("/Resources/checkmark.gif"));

    /** The icon to use to represent folders. */
    private static final Icon FOLDER_ICON = new ImageIcon(GemCutter.class.getResource("/Resources/fldr_obj.gif"));
    
    /** The icon to use to represent .jar files. */
    private static final Icon JAR_ICON = new ImageIcon(GemCutter.class.getResource("/Resources/jar_obj.gif"));

    /** The icon to use for an include pattern. */
    private static final Icon INCLUDE_ICON = new ImageIcon(GemCutter.class.getResource("/Resources/add_exc.gif"));
    
    /** The icon to use for an exclude pattern.*/
    private static final Icon EXCLUDE_ICON = new ImageIcon(GemCutter.class.getResource("/Resources/remove_exc.gif"));

    /** The perspective this UI is running in. */
    private final Perspective perspective;

    /** The JList displaying the roots from which to import the Java classes and members. 
     *  The model consists of elements of type ImportSource. */
    private ImportFromJList importFromJList;

    /** The JList displaying the patterns used to filter the classes being imported. */
    private PatternJList patternsJList;

    /** The panel allowing the generation scope to be selected. */
    private ScopeSelectorPanel scopeSelectorPanel;

    /** The panel allowing methods to be excluded from generation. */
    private MethodExcludeSelectorPanel methodExcludeSelectorPanel;
    
    /** The OK button for the dialog. */
    private JButton okButton = null;
    
    /** The cancel button for the dialog. */
    private JButton cancelButton = null;
    
    /** The text field for entering the name of the new module. */
    private final JTextField moduleNameField = new JTextField();
    
    /** The label for displaying status messages. */
    private final JLabel statusLabel = new JLabel();

    /** Shared dialog instance for selecting imports. */
    private SelectImportDialog selectImportDialog = null;

    
    /**
     * Test target.
     * Comment out check for null Perspective to execute.
     * @param args
     */
    public static void main(String[] args) {
        JavaForeignImportModuleGenerator generator = new JavaForeignImportModuleGenerator(null, null, null);
        JFrame.setDefaultLookAndFeelDecorated(true);
        
        generator.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                System.exit(0);
            }
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        generator.setVisible(true);
    }
    
    /**
     * Provider for a JavaForeignImportModuleGenerator.
     * @author Edward Lam
     */
    public static class Provider implements GemGenerator {
        /**
         * {@inheritDoc}
         */
        public GemGenerator.GeneratedDefinitions launchGenerator(JFrame parent, Perspective perspective, ValueRunner valueRunner, ValueEditorManager valueEditorManager, TypeChecker typeChecker) {
            if (parent == null || perspective == null) {
                throw new NullPointerException();
            }
    
            String title = getGeneratorTitle();
            JavaForeignImportModuleGenerator generatorUI = new JavaForeignImportModuleGenerator(parent, title, perspective);
    
            // Loop around until either:
            // 1) The user accepted the dialog and a module was successfully generated, or
            // 2) The user canceled the dialog.
            while (true) {
                boolean accepted = generatorUI.doModal();
                
                if (accepted) {
                    GeneratedDefinitions sourceDefinitions = generatorUI.getSourceDefinitions();
                    
                    if (sourceDefinitions.getModuleDefn() == null) {
                        // Couldn't successfully generate a module.  Try again.
                        continue;
                    }
                    
                    return sourceDefinitions;
                
                } else {
                    return null;
                }
            }
        }
        
        /**
         * {@inheritDoc}
         */
        public String getGeneratorMenuName() {
            return GeneratorMessages.getString("JFIMF_JavaForeignImportMenuName");
        }
        
        /**
         * {@inheritDoc}
         */
        public String getGeneratorTitle() {
            return GeneratorMessages.getString("JFIMF_JavaForeignImportTitle");
        }
        
        /**
         * {@inheritDoc}
         */
        public Icon getGeneratorIcon() {
            return GENERATOR_ICON;
        }
    }
    
    /**
     * A log handler which will capture logged records to a Status object.
     * @author Edward Lam
     */
    private static class StatusHandler extends Handler {

        /** The status object capturing the published records. */
        private final Status capturingStatus = new Status("Status");
        
        /**
         * {@inheritDoc}
         */
        @Override
        public void flush() {
            // Nothing to flush -- not buffered.
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void close() throws SecurityException {
            // Nothing to close, for now.
            // Later, there may be some way of saying that the capturing status is no longer mutable..
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void publish(LogRecord record) {
            
            // Add the record to the capturing status.
            String message = record.getMessage();
            Level level = record.getLevel();
            int levelValue = level.intValue();
            
            // Only interested in warnings and errors.
            Status.Severity severity = null;
            if (levelValue >= Level.SEVERE.intValue()) {
                severity = Status.Severity.ERROR;
            } else if (levelValue >= Level.WARNING.intValue()) {
                severity = Status.Severity.WARNING;
            }
            
            if (severity != null) {
                capturingStatus.add(new Status(severity, message));
            }
        }
        
        /**
         * @return the status which captured the records published to this handler.
         */
        public Status getStatus() {
            return capturingStatus;
        }
    }
    
    /**
     * A simple class to encapsulate an import source.
     * @author Edward Lam
     */
    private static class ImportSource {
        private final File importFile;
        private final boolean isDir;

        ImportSource(File importFile, boolean isDir) {
            this.importFile = importFile;
            this.isDir = isDir;
        }
        
        /**
         * @return the file object associated with the import source.
         */
        public File getImportFile() {
            return this.importFile;
        }

        /**
         * @return true if the source is a root folder.  False if it is a .jar file.
         */
        public boolean isDir() {
            return this.isDir;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof ImportSource) {
                ImportSource otherSource = (ImportSource)obj;
                return isDir == otherSource.isDir && importFile.equals(otherSource.importFile);
            }
            return false;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            return importFile.hashCode() + (isDir ? 17 : 37);
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return "(" + (isDir ? "directory: " : "file: ") + importFile.getPath() + ")";
        }
    }

    /**
     * Sorts import sources in the import source list.
     * Folders come first, then files.
     * @author Edward Lam
     */
    static class ImportSourceComparator implements Comparator<ImportSource> {
     
        /** Singleton instance. */
        public static final ImportSourceComparator INSTANCE = new ImportSourceComparator();
        
        /**
         * {@inheritDoc}
         */
        public int compare(ImportSource import1, ImportSource import2) {
            
            // import 1 is a folder?
            if (import1.isDir()) {
                if (!import2.isDir()) {
                    return -1;
                }

                // import 2 is also a folder.
                return import1.getImportFile().compareTo(import2.getImportFile());
            
            } else {
                // If here, import 1 is a file.
                
                if (import2.isDir()) {
                    return 1;
                }
                
                // import 2 is also a file.
                return import1.getImportFile().compareTo(import2.getImportFile());
            }
        }
    }

    /**
     * The custom cell renderer for displaying the import sources in the importFromJList.
     * @author Edward Lam
     */
    private static class ImportFromJListCellRenderer extends DefaultListCellRenderer {
        
        private static final long serialVersionUID = -8242855895470608433L;
        /** Shared renderer instance. */
        public static final ImportFromJListCellRenderer INSTANCE = new ImportFromJListCellRenderer();
        
        /**
         * {@inheritDoc}
         */
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            ImportSource importSource = (ImportSource)value;
            File importFile = importSource.getImportFile();
            boolean isDir = importSource.isDir();
            
            String displayText = importFile.getPath();
            if (importFile.getAbsolutePath().startsWith(JAVA_HOME) && importFile.getName().equals("rt.jar")) {
                displayText += "  " + GeneratorMessages.getString("JFIMF_RTJarDescription");
            }
            
            // Get the default renderer, displaying the import file.
            JLabel defaultComponent = (JLabel)super.getListCellRendererComponent(list, displayText, index, isSelected, cellHasFocus);
            
            defaultComponent.setIcon(isDir ? FOLDER_ICON : JAR_ICON);
            
            return defaultComponent;
        }
    }
    
    /**
     * The JList for displaying the import sources.
     * 
     * A special renderer to show whether the import source is a folder or a .jar file.
     * The items are maintained in sorted order.
     * Selection of items is automatically handled on add/edit/remove.
     * 
     * To use, do not manipulate the list model.  Instead, call the appropriate (add/edit/removeSelected)ImportSource() method.
     * 
     * @author Edward Lam
     */
    private static final class ImportFromJList extends JList {
        private static final long serialVersionUID = 2899009970173033431L;

        /**
         * Constructor for a new list.
         */
        private ImportFromJList() {
            super(new SortedListModel<ImportSource>(ImportSourceComparator.INSTANCE));
            
            setCellRenderer(ImportFromJListCellRenderer.INSTANCE);
            setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        }

        /**
         * @return the SortedListModel backing this JList.
         */
        SortedListModel<ImportSource> getListModel() {
            return UnsafeCast.unsafeCast(getModel());
        }
        
        /**
         * @return the currently selected ImportSource, or null if the list has no selection
         */
        public ImportSource getSelectedImport() {
            return (ImportSource)getSelectedValue();
        }
        
        /**
         * Add an import source to this JList.
         * @param importSource the import source to add.
         */
        void addImport(ImportSource importSource) {
            getListModel().addElement(importSource);

            // Select the import that was just added.
            selectImport(importSource);
        }
        
        /**
         * Select an import in this JList.
         * @param importToSelect the import to select.
         */
        private void selectImport(ImportSource importToSelect) {
            int importIndex = 0;
            for (Iterator<ImportSource> it = getListModel().iterator(); it.hasNext(); ) {
                if (it.next().equals(importToSelect)) {
                    getSelectionModel().setSelectionInterval(importIndex, importIndex);
                    break;
                }
                importIndex++;
            }
        }
        
        /**
         * Edit an import in this JList.
         * @param oldImport the old import.
         * @param newImport the import with which oldImport will be replaced.
         */
        void editImport(ImportSource oldImport, ImportSource newImport) {
            // The easiest way to edit an import is to remove the old import, and add the new one.
            getListModel().removeElement(oldImport);
            getListModel().addElement(newImport);
            
            // Select the import which was just edited.
            selectImport(newImport);
        }
        
        /**
         * Remove the import currently selected in this JList.
         */
        void removeSelectedImport() {
            ListSelectionModel selectionModel = getSelectionModel();
            
            // Remove the selected element.
            int selectionIndex = selectionModel.getMinSelectionIndex();
            if (selectionIndex < 0) {
                return;
            }
            getListModel().removeElement(getSelectedImport());
            
            // Select another element if any.
            int modelSize = getModel().getSize();
            if (selectionIndex < modelSize) {
                // Select the next element.
                selectionModel.setSelectionInterval(selectionIndex, selectionIndex);
            } else if (modelSize > 0) {
                // Select the last element.
                selectionModel.setSelectionInterval(modelSize - 1, modelSize - 1);
            } else {
                selectionModel.clearSelection();
            }
        }
    }
    
    /**
     * Sorts patterns in the pattern list.
     * Includes come first, then excludes.
     * @author Edward Lam
     */
    static class JFitPatternComparator implements Comparator<Object> {
     
        /** Singleton instance. */
        public static final JFitPatternComparator INSTANCE = new JFitPatternComparator();
        
        /**
         * {@inheritDoc}
         */
        public int compare(Object o1, Object o2) {
            
            if (o1 instanceof String) {
                if (o2 instanceof String) {
                    return ((String)o1).compareTo((String)o2);
                }
                return -1;
            }
            if (o2 instanceof String) {
                return 1;
            }
            
            JFit.Pattern pat1 = (JFit.Pattern)o1;
            JFit.Pattern pat2 = (JFit.Pattern)o2;
            
            // pattern 1 is an include pattern?
            if (pat1.isInclude()) {
                if (!pat2.isInclude()) {
                    return -1;
                }

                // pattern 2 is also an include pattern.
                return pat1.getPattern().compareTo(pat2.getPattern());
            
            } else {
                // If here, pattern 1 is an exclude pattern.
                
                if (pat2.isInclude()) {
                    return 1;
                }
                
                // pattern 2 is also an exclude pattern.
                return pat1.getPattern().compareTo(pat2.getPattern());
            }
        }
    }
    
    /**
     * The custom cell renderer for displaying include and exclude patterns in the patternJList.
     * @author Edward Lam
     */
    private static class PatternJListCellRenderer extends DefaultListCellRenderer {
        
        private static final long serialVersionUID = 3352312909749939764L;
        /** Shared renderer instance. */
        public static final PatternJListCellRenderer INSTANCE = new PatternJListCellRenderer();
        
        /**
         * {@inheritDoc}
         */
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            if (value instanceof String) {
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
            
            // Get the default renderer, displaying the pattern string.
            JFit.Pattern pattern = (JFit.Pattern)value;
            JLabel defaultComponent = (JLabel)super.getListCellRendererComponent(list, pattern.getPattern(), index, isSelected, cellHasFocus);
            
            defaultComponent.setIcon(pattern.isInclude() ? INCLUDE_ICON : EXCLUDE_ICON);
            
            return defaultComponent;
        }
    }
    
    /**
     * The JList for displaying the include/exclude patterns.
     * 
     * A special renderer to show whether a pattern is included or excluded.
     * The items are maintained in sorted order.
     * Selection of items is automatically handled on add/edit/remove.
     * 
     * To use, do not manipulate the list model.  Instead, call the appropriate (add/edit/removeSelected)Pattern() method.
     * 
     * @author Edward Lam
     */
    private static final class PatternJList extends JList {
        
        private static final long serialVersionUID = 4724753052424581727L;
        // The string to display when no patterns have been specified.
        private static final String NO_PATTERNS_ELEMENT = GeneratorMessages.getString("JFIMF_NoPatternsElement"); 
        
        /**
         * Constructor for a new list.
         */
        private PatternJList() {
            super(new SortedListModel<Object>(JFitPatternComparator.INSTANCE));
            
            setCellRenderer(PatternJListCellRenderer.INSTANCE);
            setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            getListModel().addElement(NO_PATTERNS_ELEMENT);
        }

        /**
         * @return the SortedListModel backing this JList.
         */
        SortedListModel<Object> getListModel() {
            return UnsafeCast.unsafeCast(getModel());
        }
        
        /**
         * @return the currently selected JFit.Pattern, or null if:
         * 1) the list has no selection
         * 2) the list selection is the no-patterns element.
         */
        public JFit.Pattern getSelectedPattern() {
            Object selectedValue = getSelectedValue();
            
            if (selectedValue instanceof JFit.Pattern) {
                return (JFit.Pattern)selectedValue;
            }
            
            return null;
        }
        
        /**
         * Add a pattern to this JList.
         * @param jfitPattern the pattern to add.
         */
        void addPattern(JFit.Pattern jfitPattern) {
            // If there were no patterns, we make sure the "No patterns" message is removed.
            if (getListModel().getSize() < 2) {
                // This does nothing if the single element is not the no patterns element.
                getListModel().removeElement(NO_PATTERNS_ELEMENT);
                
                // Clear selection so that the selection changed event will be fired when the first item is selected again.
                getSelectionModel().clearSelection();
            }
            getListModel().addElement(jfitPattern);

            // Select the pattern that was just added.
            selectPattern(jfitPattern);
        }
        
        /**
         * Select a pattern in this JList.
         * @param patternToSelect the pattern to select.
         */
        private void selectPattern(JFit.Pattern patternToSelect) {
            int patternIndex = 0;
            for (Iterator<Object> it = getListModel().iterator(); it.hasNext(); ) {
                if (it.next().equals(patternToSelect)) {
                    getSelectionModel().setSelectionInterval(patternIndex, patternIndex);
                    break;
                }
                patternIndex++;
            }
        }
        
        /**
         * Edit a pattern in this JList.
         * @param oldPattern the old pattern.
         * @param newPattern the pattern with which oldPattern will be replaced.
         */
        void editPattern(JFit.Pattern oldPattern, JFit.Pattern newPattern) {
            // The easiest way to edit a pattern is to remove the old pattern, and add the new one.
            getListModel().removeElement(oldPattern);
            getListModel().addElement(newPattern);
            
            // Select the pattern which was just edited.
            selectPattern(newPattern);
        }
        
        /**
         * Remove the pattern currently selected in this JList.
         */
        void removeSelectedPattern() {
            ListSelectionModel selectionModel = getSelectionModel();
            
            // Remove the selected element.
            int selectionIndex = selectionModel.getMinSelectionIndex();
            if (selectionIndex < 0) {
                return;
            }
            getListModel().removeElement(getSelectedPattern());
            
            // Select another element.
            int modelSize = getModel().getSize();
            if (selectionIndex < modelSize) {
                // Select the next element.
                selectionModel.setSelectionInterval(selectionIndex, selectionIndex);
            } else if (modelSize > 0) {
                // Select the last element.
                selectionModel.setSelectionInterval(modelSize - 1, modelSize - 1);
            } else {
                selectionModel.clearSelection();
            }
            
            // If there are no patterns, display the "No patterns" message.
            if (modelSize == 0) {
                getListModel().addElement(NO_PATTERNS_ELEMENT);
            }
        }

        /**
         * @return true if there are no patterns, false if there are.
         */
        public boolean isEmpty() {
          return (getModel().getSize() == 1) && getModel().getElementAt(0).equals(NO_PATTERNS_ELEMENT);
        }
    }
    
    /**
     * The dialog to select an import source.
     * This is just a dialog with an embedded JFileChooser and a help panel at the top.
     * 
     * To use: instantiate, then call showDialog().
     * 
     * @author Edward Lam
     */
    private static class SelectImportDialog extends DialogBase {
        
        private static final long serialVersionUID = 8113203595172861783L;

        /** Shared file chooser instance for selecting imports. */
        private final JFileChooser fileChooser;

        /** The help panel at the top of the dialog. */
        private final TitlePanel helpPanel;
        
        /** The current return value.  
         *  This mirrors the same value in JFileChooser, which unfortunately has no accessors. */
        private int returnValue = JFileChooser.ERROR_OPTION;
        
        /** The size constrainer for this dialog. */
        private final SizeConstrainer sizeConstrainer;
        
        /**
         * Constructor for a SelectImportDialog.
         * @param parent the parent of this dialog.
         */
        private SelectImportDialog(JDialog parent) {
            super(parent, "");
            
            sizeConstrainer = new SizeConstrainer();
            addComponentListener(sizeConstrainer);
            
            // main panel
            JPanel topPanel = getTopPanel();
            topPanel.setBorder(null);               // cancel the border set by superclass..
            setContentPane(topPanel);
            
            // Keep track of the number of rows.
            int numRows = 0;
            
            // Help panel to the north.
            {
                GridBagConstraints constraints = new GridBagConstraints();
                constraints.anchor = GridBagConstraints.WEST;
                constraints.fill = GridBagConstraints.HORIZONTAL;

                constraints.gridx = 0;
                constraints.gridy = numRows;
                
                helpPanel = new TitlePanel();
                
                topPanel.add(helpPanel, constraints);
                numRows++;
            }
            
            // file chooser to the south.
            {
                GridBagConstraints constraints = new GridBagConstraints();
                constraints.anchor = GridBagConstraints.WEST;
                constraints.fill = GridBagConstraints.BOTH;

                constraints.gridx = 0;
                constraints.gridy = numRows;
                constraints.weightx = 1.0;
                constraints.weighty = 1.0;
                
                // Start out pointing at the current directory.
                File currentDirectory = new File(".");
                
                fileChooser = new JFileChooser(currentDirectory) {
                    private static final long serialVersionUID = 2625522895016359504L;
                    @Override
                    public void approveSelection() {
                        returnValue = JFileChooser.APPROVE_OPTION;
                        super.approveSelection();
                        closeDialog(false);
                    }
                    @Override
                    public void cancelSelection() {
                        returnValue = JFileChooser.CANCEL_OPTION;
                        super.cancelSelection();
                        closeDialog(true);
                    }
                };
                
                // Provide a custom icon for .jar files.
                fileChooser.setFileView(new FileView() {
                    @Override
                    public Icon getIcon(File f) {
                        if (f.getName().endsWith(".jar")) {
                            return JAR_ICON;
                        }
                        return null;
                    }
                });
                
                topPanel.add(fileChooser, constraints);
                numRows++;
            }
            
            // Handle window events.
            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    returnValue = JFileChooser.CANCEL_OPTION;
                    closeDialog(true);
                }
            });
        }

        /**
         * Show the dialog.
         * 
         * @param currentFile
         *            If non-null the file chooser will be specified as "Change"ing
         *            the given import. Otherwise, it will be set up as adding a new
         *            import file.
         * @param chooseDir
         *            if true, this the chooser is to select a directory. If false,
         *            it's so select a file.
         * @return  the return state of the file chooser on popdown:
         * <ul>
         * <li>JFileChooser.CANCEL_OPTION
         * <li>JFileChooser.APPROVE_OPTION
         * <li>JFileCHooser.ERROR_OPTION if an error occurs or the
         *                  dialog is dismissed
         * </ul>
         */
        public int showDialog(File currentFile, boolean chooseDir) {
            
            returnValue = JFileChooser.ERROR_OPTION;
            
            // Customize the existing file chooser.
            {
                // Set the current directory if provided by currentFile.
                if (currentFile != null) {
                    // Change the file chooser's current directory if a file was passed in.
                    File currentDirectory = currentFile.isDirectory() ? currentFile : currentFile.getParentFile();
                    fileChooser.setCurrentDirectory(currentDirectory);
                }
                
                if (chooseDir) {
                    // Set the file selection mode.
                    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    
                } else {
                    // Set the file selection mode.
                    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    
                    // Set a custom file filter.
                    fileChooser.setFileFilter(new ExtensionFileFilter("jar", GeneratorMessages.getString("JFIMF_Chooser_ImportJarFilterDescription")));
                }
                
                // Customize the add button text.
                String buttonText = GeneratorMessages.getString(
                        chooseDir ? (currentFile == null ? "JFIMF_Chooser_AddImportFolder" : "JFIMF_Chooser_ChangeImportFolder") : 
                            (currentFile == null ? "JFIMF_Chooser_AddImportJar" : "JFIMF_Chooser_ChangeImportJar")
                );
                
                fileChooser.setApproveButtonText(buttonText);
                fileChooser.setDialogType(JFileChooser.CUSTOM_DIALOG);
            }
            
            // Set the dialog description and title.
            String title = fileChooser.getUI().getDialogTitle(fileChooser);
            getAccessibleContext().setAccessibleDescription(title);
            setTitle(title);
            
            // Customize the help panel
            {
                String titleText;
                String subtitleText;
                if (chooseDir) {
                    titleText = GeneratorMessages.getString(currentFile == null ? "JFIMF_ChooserHelp_AddFolderTitle" : "JFIMF_ChooserHelp_ChangeFolderTitle");
                    subtitleText = GeneratorMessages.getString("JFIMF_ChooserHelp_ChooseFolderSubtitle");
                } else {
                    titleText = GeneratorMessages.getString(currentFile == null ? "JFIMF_ChooserHelp_AddJarTitle" : "JFIMF_ChooserHelp_ChangeJarTitle");
                    subtitleText = GeneratorMessages.getString("JFIMF_ChooserHelp_ChooseJarSubtitle");
                }
                
                helpPanel.setTitleText(titleText);
                helpPanel.setSubtitleText(subtitleText);
            }
            
            // Decorate like a file chooser dialog.
            if (JDialog.isDefaultLookAndFeelDecorated() && UIManager.getLookAndFeel().getSupportsWindowDecorations()) {
                getRootPane().setWindowDecorationStyle(JRootPane.FILE_CHOOSER_DIALOG);
            }
            
            pack();
            sizeConstrainer.setMinimumSize(getSize());
            doModal();
            
            return returnValue;
        }
        
        /**
         * @return the file selected in the embedded file chooser.
         */
        public File getSelectedFile() {
            return fileChooser.getSelectedFile();
        }
    }
    
    /**
     * The dialog to enter a pattern to add.
     * 
     * @author Edward Lam
     */
    private static class AddEditPatternDialog extends DialogBase {
        
        private static final long serialVersionUID = 3566552733651814629L;

        /** The radio button for selecting an include pattern. */
        private final JRadioButton includeButton = new JRadioButton(GeneratorMessages.getString("JFIMF_Include"));
        
        /** The radio button for selecting an exclude pattern. */
        private final JRadioButton excludeButton = new JRadioButton(GeneratorMessages.getString("JFIMF_Exclude"));

        /** The button group for the radio buttons. */
        private final ButtonGroup buttonGroup = new ButtonGroup();
        
        /** The text field for entering the pattern. */
        private final JTextField patternField = new JTextField();
        
        /** The OK button for the dialog. */
        private JButton okButton = null;
        
        /** The cancel button for the dialog. */
        private JButton cancelButton = null;

        /** The help panel at the top of the dialog. */
        private final TitlePanel helpPanel;
        
        /**
         * Constructor for an AddEditPatternDialog.
         * @param owner the owner dialog.
         * @param selectedPattern the previously existing pattern, if any.
         * If non-null, this is an edit dialog, and dialog will be initialized with this pattern.
         * If null, this is an add dialog.
         */
        private AddEditPatternDialog(Dialog owner, JFit.Pattern selectedPattern) {
            super(owner, GeneratorMessages.getString(selectedPattern == null ? "JFIMF_AddPatternDialogTitle" : "JFIMF_EditPatternDialogTitle"));

            // main panel
            JPanel topPanel = getTopPanel();
            topPanel.setBorder(null);               // cancel the border set by superclass..
            setContentPane(topPanel);
            
            // Keep track of the number of rows.
            int numRows = 0;
            
            int iconTextGap = includeButton.getIconTextGap();
            
            // Add the help panel.
            {
                GridBagConstraints constraints = new GridBagConstraints();
                constraints.anchor = GridBagConstraints.WEST;
                constraints.fill = GridBagConstraints.HORIZONTAL;

                constraints.gridx = 0;
                constraints.gridy = numRows;
                
                this.helpPanel = new TitlePanel();
                helpPanel.setTitleText(getTitle());
                helpPanel.setSubtitleText(GeneratorMessages.getString("JFIMF_AddPatternDialogSubtitle"));

                topPanel.add(helpPanel, constraints);
                numRows++;
            }
            
            // Add the radio panel
            {
                GridBagConstraints constraints = new GridBagConstraints();
                constraints.anchor = GridBagConstraints.WEST;
                constraints.fill = GridBagConstraints.HORIZONTAL;

                constraints.gridx = 0;
                constraints.gridy = numRows;

                // Set up the buttons in a button group.
                buttonGroup.add(includeButton);
                buttonGroup.add(excludeButton);
                if (selectedPattern != null) {
                    if (selectedPattern.isInclude()) {
                        buttonGroup.setSelected(includeButton.getModel(), true);
                    } else {
                        buttonGroup.setSelected(excludeButton.getModel(), true);
                    }
                } else {
                    buttonGroup.setSelected(includeButton.getModel(), true);
                }

                // Create the panel with the two radio buttons and some glue..
                JPanel radioPanel = new JPanel();
                radioPanel.setBorder(BorderFactory.createEmptyBorder(5, 5 - iconTextGap, 5, 5));
                radioPanel.setLayout(new BoxLayout(radioPanel, BoxLayout.X_AXIS));
                
                radioPanel.add(includeButton);
                radioPanel.add(Box.createHorizontalStrut(5));
                radioPanel.add(excludeButton);
                radioPanel.add(Box.createHorizontalGlue());

                topPanel.add(radioPanel, constraints);
                numRows++;
            }
            
            // Add the Pattern entry area
            {
                GridBagConstraints constraints = new GridBagConstraints ();
                constraints.anchor = GridBagConstraints.WEST;
                constraints.fill = GridBagConstraints.HORIZONTAL;
                constraints.insets = new Insets(0, 0, 0, 5);
                
                constraints.gridx = 0;
                constraints.gridy = numRows;
                constraints.gridwidth = GridBagConstraints.REMAINDER;
                

                JPanel patternLabelAndFieldPanel = new JPanel();
                patternLabelAndFieldPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 0));
                patternLabelAndFieldPanel.setLayout(new BoxLayout(patternLabelAndFieldPanel, BoxLayout.X_AXIS));
                patternLabelAndFieldPanel.add(new JLabel(GeneratorMessages.getString("JFIMF_PatternHeader")));
                patternLabelAndFieldPanel.add(Box.createHorizontalStrut(15));
                
                patternField.setColumns(40);
                if (selectedPattern != null) {
                    patternField.setText(selectedPattern.getPattern());
                }
                patternLabelAndFieldPanel.add(patternField);
                patternLabelAndFieldPanel.add(Box.createHorizontalGlue());
                
                topPanel.add(patternLabelAndFieldPanel, constraints);
                numRows++;
            }
            
            // Add the label for the wildcard explanation.
            {
                GridBagConstraints constraints = new GridBagConstraints ();
                constraints.anchor = GridBagConstraints.WEST;
                constraints.fill = GridBagConstraints.HORIZONTAL;
                constraints.insets = new Insets(0, 5, 10, 5);
                
                constraints.gridx = 0;
                constraints.gridy = numRows;
                constraints.gridwidth = GridBagConstraints.REMAINDER;
                

                JLabel wildcardExplanationLabel = new JLabel(GeneratorMessages.getString("JFIMF_WildcardExplanation"));
                
                topPanel.add(wildcardExplanationLabel, constraints);
                numRows++;
            }
            
            // Add the ok/cancel button area.
            {
                GridBagConstraints constraints = new GridBagConstraints();
                constraints.gridy = numRows;
                constraints.gridwidth = GridBagConstraints.REMAINDER;
                constraints.anchor = GridBagConstraints.SOUTHEAST;

                Box buttonBox = new Box (BoxLayout.X_AXIS);
                buttonBox.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 5));
                
                this.okButton = makeOKButton();
                this.cancelButton = makeCancelButton();
                
                getRootPane().setDefaultButton(okButton);
                
                buttonBox.add (Box.createHorizontalGlue());
                buttonBox.add (okButton);
                buttonBox.add (Box.createHorizontalStrut(5));
                buttonBox.add (cancelButton);

                topPanel.add(buttonBox, constraints);
                
                numRows++;
            }
            
            // Make the pattern field have default focus
            setFocusTraversalPolicy(new LayoutFocusTraversalPolicy() {
                private static final long serialVersionUID = 2187420438565327738L;

                @Override
                public Component getDefaultComponent(Container c) {
                    return patternField;
                }
            });

            // Esc cancels the dialog.
            KeyListener dismissKeyListener = new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        dispose();
                        e.consume();
                    }
                }
            };
            
            // The ok button is only enabled if there is a pattern entered.
            okButton.setEnabled(false);
            patternField.addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    okButton.setEnabled(patternField.getText().length() > 0);
                }
            });

            // Cancel the dialog if the user presses ESC
            addKeyListener(dismissKeyListener);
            patternField.addKeyListener(dismissKeyListener);
            okButton.addKeyListener(dismissKeyListener);
            cancelButton.addKeyListener(dismissKeyListener);

            this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            
            pack();

            // Not resizeable.
            setResizable(false);
            
            // position relative to the owner frame.
            setLocationRelativeTo(owner);
        }
        
        /**
         * @return the JFit.Pattern entered by the user.
         * Note: this will return a non-null result even if the dialog was cancelled.
         */
        JFit.Pattern getFitPattern() {
            return new JFit.Pattern(patternField.getText().trim(), includeButton.isSelected());
        }
    }

    /**
     * A panel displaying some radio buttons allowing the user to select a generation scope.
     * @author Edward Lam
     */
    private static class ScopeSelectorPanel extends JPanel {
        private static final long serialVersionUID = -5400480868412313409L;
        private final JRadioButton allPrivateButton = new JRadioButton(GeneratorMessages.getString("JFIMF_ScopeAllPrivate"));
        private final JRadioButton partialPublicButton = new JRadioButton(GeneratorMessages.getString("JFIMF_ScopePartialPublic"));
        private final JRadioButton allPublicButton = new JRadioButton(GeneratorMessages.getString("JFIMF_ScopeAllPublic"));
        
        ScopeSelectorPanel() {
            // Set up the buttons in a button group.
            ButtonGroup buttonGroup = new ButtonGroup();

            buttonGroup.add(allPrivateButton);
            buttonGroup.add(partialPublicButton);
            buttonGroup.add(allPublicButton);
            
            // Default to all private.
            buttonGroup.setSelected(allPrivateButton.getModel(), true);

            // Set the layout.
            this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

            // Add the buttons.
            this.add(allPrivateButton);
            this.add(partialPublicButton);
            this.add(allPublicButton);
        }

        /**
         * @return the selected scope.
         */
        ForeignImportGenerator.GenerationScope getSelectedScope() {
            if (allPrivateButton.isSelected()) {
                return ForeignImportGenerator.GenerationScope.ALL_PRIVATE;
            }
            
            if (partialPublicButton.isSelected()) {
                return ForeignImportGenerator.GenerationScope.PARTIAL_PUBLIC;
            }
            
            if (allPublicButton.isSelected()) {
                return ForeignImportGenerator.GenerationScope.ALL_PUBLIC;
            }
            
            return null;
        }
    }
    
    /**
     * A panel allowing the user to indicate methods to exclude.
     * @author Edward Lam
     */
    private static class MethodExcludeSelectorPanel extends JPanel {
        private static final long serialVersionUID = 6319968816481922026L;
        private final JCheckBox equalsHashCodeButton = new JCheckBox(GeneratorMessages.getString("JFIMF_OptionalEqualsHashCode"));
        private final JCheckBox waitNotifyButton = new JCheckBox(GeneratorMessages.getString("JFIMF_OptionalWaitNotify"));
        private final JCheckBox getClassButton = new JCheckBox(GeneratorMessages.getString("JFIMF_OptionalGetClass"));
        
        MethodExcludeSelectorPanel() {

            // Put the check boxes in a column in a panel
            
            // Set the layout.
            this.setLayout(new GridLayout(0, 1));

            // Add the check boxes
            add(equalsHashCodeButton);
            add(waitNotifyButton);
            add(getClassButton);
        }
        
        String[] getExcludeMethods() {
            List<String> excludeMethodList = new ArrayList<String>();
            
            if (!equalsHashCodeButton.isSelected()) {
                excludeMethodList.add("java.lang.Object.equals");
                excludeMethodList.add("java.lang.Object.hashCode");
            }
            if (!waitNotifyButton.isSelected()) {
                excludeMethodList.add("java.lang.Object.wait");
                excludeMethodList.add("java.lang.Object.notify");
                excludeMethodList.add("java.lang.Object.notifyAll");

            }
            if (!getClassButton.isSelected()) {
                excludeMethodList.add("java.lang.Object.getClass");
            }
            

            return excludeMethodList.toArray(new String[excludeMethodList.size()]);
        }
    }
    
    /**
     * Constructor for a new generator ui.
     * Use a factory method to get an instance.
     * 
     * @param owner the owner of the dialog
     * @param dialogTitle if null, the default title will be used
     * @param perspective the perspective the UI should use
     */
    private JavaForeignImportModuleGenerator(Frame owner, String dialogTitle, Perspective perspective) {
        super(owner, dialogTitle);
        
        // (Java bug workaround) ensure JFileChooser will load..
        ensureJFileChooserLoadable();
        
        if (perspective == null) {
            throw new NullPointerException();
        }
        
        this.perspective = perspective;

        if (dialogTitle == null) {
            setTitle(GeneratorMessages.getString("JFIMF_JavaForeignImportTitle"));
        }
        
        // main panel
        JPanel topPanel = getTopPanel();
        topPanel.setBorder(null);               // cancel the border set by superclass..
        setContentPane(topPanel);
        
        // Keep track of the number of rows.
        int numRows = 0;
        
        // Add the title panel - the white panel at the top of the dialog.
        {
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.anchor = GridBagConstraints.NORTH;
            constraints.fill = GridBagConstraints.HORIZONTAL;

            constraints.gridx = 0;
            constraints.gridy = numRows;
            
            TitlePanel titlePanel = new TitlePanel(true);
            titlePanel.setTitleText(GeneratorMessages.getString("JFIMF_JavaForeignImportTitle"));
            titlePanel.setSubtitleText(GeneratorMessages.getString("JFIMF_JavaForeignImportSubTitle"));

            topPanel.add(titlePanel, constraints);
            numRows++;
        }
        // Add the main panel
        {
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.anchor = GridBagConstraints.CENTER;
            constraints.fill = GridBagConstraints.BOTH;

            constraints.gridx = 0;
            constraints.gridy = numRows;
            constraints.weightx = 1;
            constraints.weighty = 1;
            topPanel.add(getMainPanel(), constraints);
            numRows++;
        }
        // Add a separator.
        {
            addSeparator(topPanel, null, numRows, new Insets(10, 0, 5, 0));
            numRows++;
        }
        // Add the button panel.
        {
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.anchor = GridBagConstraints.SOUTH;
            constraints.fill = GridBagConstraints.HORIZONTAL;

            constraints.gridx = 0;
            constraints.gridy = numRows;
            
            this.okButton = makeOKButton();
            this.cancelButton = makeCancelButton();
            
            JPanel buttonPanel = new JPanel();
            
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
            
            buttonPanel.add(Box.createHorizontalGlue());
            buttonPanel.add(okButton);
            buttonPanel.add(Box.createHorizontalStrut(5));
            buttonPanel.add(cancelButton);
            
            topPanel.add(buttonPanel, constraints);
            numRows++;
        }

        getRootPane().setDefaultButton(okButton);

        // Add default import source(s).
        addDefaultImports();

        // Update the state of the dialog.
        updateState();
        
        // Add listeners to update the error message if things change
        moduleNameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                updateState();
            }
        });
        
        importFromJList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                updateState();
            }
        });

        patternsJList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                updateState();
            }
        });

        // Esc cancels the dialog.
        KeyListener dismissKeyListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    dispose();
                    e.consume();
                }
            }
        };

        // Cancel the dialog if the user presses ESC
        addKeyListener(dismissKeyListener);
        moduleNameField.addKeyListener(dismissKeyListener);
        okButton.addKeyListener(dismissKeyListener);
        cancelButton.addKeyListener(dismissKeyListener);

        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        
        pack();
        setSize(600, getSize().height);
        
        addComponentListener(new SizeConstrainer(getSize()));
        
        // position relative to the owner frame.
        setLocationRelativeTo(owner);
    }

    /**
     * @return the generated module definition.
     * Note: this method may display a modal dialog if problems were encountered.
     */
    public GemGenerator.GeneratedDefinitions getSourceDefinitions() {
        
        // Create a logger.  Its handler will only handle warnings and errors.
        Logger generatorLogger = Logger.getLogger(getClass().getPackage().getName());
        generatorLogger.setLevel(Level.FINEST);
        generatorLogger.setUseParentHandlers(false);
        
        StatusHandler statusHandler = new StatusHandler();

        // Only interested in warnings and errors.
        statusHandler.setLevel(Level.WARNING);
        generatorLogger.addHandler(statusHandler);
        
        
        // Get the module name.
        String moduleName = moduleNameField.getText();

        // Get the patterns.
        JFit.Pattern[] patterns = getPatterns();
        
        // Get the selected generation scope
        ForeignImportGenerator.GenerationScope generationScope = scopeSelectorPanel.getSelectedScope();
        
        // Get the exclude methods.
        String[] excludeMethods = methodExcludeSelectorPanel.getExcludeMethods();
        
        //
        // Get the input files and dirs.
        //

        List<File> inputFileList = new ArrayList<File>();
        List<File> inputDirList = new ArrayList<File>();
        
        for (Iterator<ImportSource> it = importFromJList.getListModel().iterator(); it.hasNext(); ) {
            ImportSource importSource = it.next();
            if (importSource.isDir()) {
                inputDirList.add(importSource.getImportFile());
            } else {
                inputFileList.add(importSource.getImportFile());
            }
        }
        File[] inputFiles = inputFileList.toArray(new File[inputFileList.size()]);
        File[] inputDirs = inputDirList.toArray(new File[inputDirList.size()]);
        
        // Instantiate the fit options.
        JFit.Options options = JFit.Options.makeOptions(moduleName, inputFiles, inputDirs, null, patterns, generationScope, excludeMethods, generatorLogger);
        
        // Create the fitter and fit.
        JFit jfit = new JFit(options, perspective.getWorkspace(), generatorLogger);
        final ModuleDefn defn = jfit.autoFit();         // null if an error occurred.

        // Deal with warnings/errors.
        Status capturedStatus = statusHandler.getStatus();
        if (capturedStatus.getSeverity().compareTo(Status.Severity.WARNING) >= 0) {
            String title = GeneratorMessages.getString("JFIMF_JavaForeignImportTitle");
            String message = GeneratorMessages.getString("JFIMF_ProblemsGeneratingModule");
            String details = capturedStatus.getDebugMessage();
            
            DetailsDialog.MessageType messageType = capturedStatus.getSeverity() == Status.Severity.WARNING ? 
                        DetailsDialog.MessageType.WARNING : DetailsDialog.MessageType.ERROR;
            
            DetailsDialog dialog = new DetailsDialog(this, title, message, details, messageType);
            dialog.doModal();
        }
        
        return new GemGenerator.GeneratedDefinitions() {

            public ModuleDefn getModuleDefn() {
                return defn;
            }

            public Map<String, String> getSourceElementMap() {
                return null;
            }
        };
    }

    /**
     * @return the patterns currently entered in the pattern list.
     */
    private JFit.Pattern[] getPatterns() {
        SortedListModel<Object> listModel = patternsJList.getListModel();
        List<JFit.Pattern> patternList = new ArrayList<JFit.Pattern>(listModel.getSize());

        for (Iterator<Object> it = listModel.iterator(); it.hasNext(); ) {
            Object nextElem = it.next();
            if (nextElem instanceof JFit.Pattern) {
                patternList.add((JFit.Pattern)nextElem);
            }
        }
        
        return patternList.toArray(new JFit.Pattern[patternList.size()]);
    }
    

    /**
     * @return the main panel that shows the contents of the dialog
     */
    private JPanel getMainPanel() {
        
        JPanel mainPanel = new JPanel();
        
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        mainPanel.setLayout(new GridBagLayout());

        // Keep track of the number of rows.
        int numRows = 0;
        
        // Status label.
        {
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.anchor = GridBagConstraints.NORTHWEST;
            constraints.fill = GridBagConstraints.HORIZONTAL;

            constraints.gridx = 0;
            constraints.gridy = numRows;
            constraints.gridwidth = GridBagConstraints.REMAINDER;
            constraints.insets = new Insets(5, 5, 10, 5);
            mainPanel.add(statusLabel, constraints);
            numRows++;
            
            Font font = getFont();
            if (font == null) {
                font = UIManager.getFont("Label.font");
            }
            if (font != null) {
                statusLabel.setFont(font.deriveFont(Font.BOLD));
            }
        }
        
        // Module name entry
        {
            GridBagConstraints constraints = new GridBagConstraints ();
            constraints.anchor = GridBagConstraints.WEST;
            constraints.fill = GridBagConstraints.HORIZONTAL;
            constraints.insets = new Insets(0, 0, 15, 5);
            
            constraints.gridx = 0;
            constraints.gridy = numRows;
            constraints.gridwidth = GridBagConstraints.REMAINDER;
            
            JPanel moduleLabelAndFieldPanel = new JPanel();
            moduleLabelAndFieldPanel.setLayout(new BoxLayout(moduleLabelAndFieldPanel, BoxLayout.X_AXIS));
            moduleLabelAndFieldPanel.add(new JLabel(GeneratorMessages.getString("JFIMF_ModuleNameHeader")));
            moduleLabelAndFieldPanel.add(Box.createHorizontalStrut(15));
            
            moduleNameField.setColumns(20);
            moduleLabelAndFieldPanel.add(moduleNameField);
            moduleLabelAndFieldPanel.add(Box.createHorizontalGlue());
            
            mainPanel.add(moduleLabelAndFieldPanel, constraints);
            numRows++;
            
            // Make the module field have default focus
            setFocusTraversalPolicy(new LayoutFocusTraversalPolicy() {
                private static final long serialVersionUID = -8536707255639997877L;

                @Override
                public Component getDefaultComponent(Container c) {
                    return moduleNameField;
                }
            });
        }


        // Import From Message.
        {
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.anchor = GridBagConstraints.NORTHWEST;
            constraints.fill = GridBagConstraints.HORIZONTAL;

            constraints.gridx = 0;
            constraints.gridy = numRows;
            constraints.gridwidth = GridBagConstraints.REMAINDER;
            
            JLabel messageLabel = new JLabel(GeneratorMessages.getString("JFIMF_ImportFromHeader"));

            JPanel messagePanel = new JPanel();
            messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.X_AXIS));
            messagePanel.add(messageLabel);
            messagePanel.add(Box.createHorizontalStrut(200));
            messagePanel.add(Box.createHorizontalGlue());
            mainPanel.add(messagePanel, constraints);
            numRows++;
        }
        
        {
            // The buttons..
            final JButton addJarButton;
            final JButton addFolderButton;
            final JButton editImportButton;
            final JButton removeImportButton;
            
            // Import From List
            {
                GridBagConstraints constraints = new GridBagConstraints();
                constraints.anchor = GridBagConstraints.NORTHWEST;
                constraints.fill = GridBagConstraints.BOTH;
                
                constraints.gridx = 0;
                constraints.gridy = numRows;
                constraints.gridheight = 4;     // the number of buttons.
                constraints.weightx = 1;
                constraints.weighty = 1;
                constraints.gridwidth = GridBagConstraints.RELATIVE;
                constraints.insets = new Insets(5, 15, 10, 5);
                
                // A mutable list model.  Only one item selectable.
                importFromJList = new ImportFromJList();
                JScrollPane importFromScrollPane = new JScrollPane(importFromJList);
                
                importFromScrollPane.setMinimumSize(new Dimension(1000, 1));
                
                mainPanel.add(importFromScrollPane, constraints);
                // Do not increment numRows.
            }
            // Import button area
            {
                GridBagConstraints constraints = new GridBagConstraints();
                constraints.anchor = GridBagConstraints.NORTH;
                constraints.fill = GridBagConstraints.HORIZONTAL;
                
                constraints.gridx = 2;
                constraints.gridwidth = 1;

                // It would have been nice to create a Box, but this doesn't make buttons the same size.
                // A GridLayout makes all cells the same size, which doesn't allow for the extra space between the 2nd and 3rd buttons.
                // So keep using the grid bag layout.  This also allows us to easily make the buttons here the same
                //   size as those in the patterns aprea.
                {
                    addJarButton = new JButton(getAddImportJarAction());
                    constraints.gridy = numRows;
                    constraints.insets = new Insets(5, 5, 5, 5);
                    mainPanel.add(addJarButton, constraints);
                    numRows++;
                    
                    addFolderButton = new JButton(getAddImportFolderAction());
                    constraints.gridy = numRows;
                    constraints.insets = new Insets(0, 5, 10, 5);
                    mainPanel.add(addFolderButton, constraints);
                    numRows++;
                    
                    editImportButton = new JButton(getChangeImportAction());
                    constraints.gridy = numRows;
                    constraints.insets = new Insets(0, 5, 5, 5);
                    mainPanel.add(editImportButton, constraints);
                    numRows++;
                    
                    removeImportButton = new JButton(getRemoveImportAction());
                    constraints.gridy = numRows;
                    constraints.insets = new Insets(0, 5, 5, 5);
                    mainPanel.add(removeImportButton, constraints);
                    numRows++;
                }
            }
            
            //
            // Configure the buttons.
            //
            
            // Edit and remove start out disabled.
            editImportButton.setEnabled(false);
            removeImportButton.setEnabled(false);
            
            // When the list selection changes, change enabling of the edit and remove buttons.
            importFromJList.addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    boolean emptySelection = importFromJList.getSelectionModel().isSelectionEmpty();
                    editImportButton.setEnabled(!emptySelection);
                    removeImportButton.setEnabled(!emptySelection);
                }
            });
        }
        
        
        // Patterns Message.
        {
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.anchor = GridBagConstraints.NORTHWEST;
            constraints.fill = GridBagConstraints.HORIZONTAL;

            constraints.gridx = 0;
            constraints.gridy = numRows;
            constraints.gridwidth = GridBagConstraints.REMAINDER;

            
            JLabel messageLabel = new JLabel(GeneratorMessages.getString("JFIMF_PatternsHeader"));

            JPanel messagePanel = new JPanel();
            messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.X_AXIS));
            messagePanel.add(messageLabel);
            messagePanel.add(Box.createHorizontalStrut(200));
            messagePanel.add(Box.createHorizontalGlue());
            mainPanel.add(messagePanel, constraints);
            numRows++;
        }
        
        {
            // The buttons.
            final JButton addPatternButton;
            final JButton editPatternButton;
            final JButton removePatternButton;
            
            // Patterns List.
            {
                GridBagConstraints constraints = new GridBagConstraints();
                constraints.anchor = GridBagConstraints.NORTHWEST;
                constraints.fill = GridBagConstraints.BOTH;
                
                constraints.gridx = 0;
                constraints.gridy = numRows;
                constraints.gridheight = 3;     // the number of buttons.
                constraints.weightx = 1;
                constraints.weighty = 1;
                constraints.gridwidth = 1;
                constraints.insets = new Insets(5, 15, 15, 5);

                patternsJList = new PatternJList();
                JScrollPane patternsScrollPane = new JScrollPane(patternsJList);
                
                patternsScrollPane.setMinimumSize(new Dimension(1000, 1));
                
                mainPanel.add(patternsScrollPane, constraints);
                // Do not increment numRows.
            }
            // Patterns button area
            {
                GridBagConstraints constraints = new GridBagConstraints();
                constraints.anchor = GridBagConstraints.NORTH;
                constraints.fill = GridBagConstraints.HORIZONTAL;
                
                constraints.gridx = 2;
                constraints.gridwidth = 1;

                // It would have been nice to create a Box, but this doesn't make buttons the same size.
                // A GridLayout makes all cells the same size, which doesn't allow for the extra space between the 2nd and 3rd buttons.
                // So keep using the grid bag layout.  This also allows us to easily make the buttons here the same
                //   size as those in the patterns area.
                {
                    addPatternButton = new JButton(getAddPatternAction());
                    constraints.gridy = numRows;
                    constraints.insets = new Insets(5, 5, 10, 5);
                    mainPanel.add(addPatternButton, constraints);
                    numRows++;
                    
                    editPatternButton = new JButton(getEditPatternAction());
                    constraints.gridy = numRows;
                    constraints.insets = new Insets(0, 5, 5, 5);
                    mainPanel.add(editPatternButton, constraints);
                    numRows++;
                    
                    removePatternButton = new JButton(getRemovePatternAction());
                    constraints.gridy = numRows;
                    constraints.insets = new Insets(0, 5, 5, 5);
                    mainPanel.add(removePatternButton, constraints);
                    numRows++;
                }
            }
            //
            // Configure the buttons.
            //
            
            // Edit and remove start out disabled.
            editPatternButton.setEnabled(false);
            removePatternButton.setEnabled(false);
            
            // When the list selection changes, change enabling of the edit and remove buttons.
            patternsJList.addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    boolean emptyPatternSelection = patternsJList.getSelectedPattern() == null;
                    editPatternButton.setEnabled(!emptyPatternSelection);
                    removePatternButton.setEnabled(!emptyPatternSelection);
                }
            });
        }
        
        // Scope Label.
        {
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.anchor = GridBagConstraints.NORTHWEST;
            constraints.fill = GridBagConstraints.HORIZONTAL;

            constraints.gridx = 0;
            constraints.gridy = numRows;
            constraints.gridwidth = GridBagConstraints.REMAINDER;

            
            JLabel messageLabel = new JLabel(GeneratorMessages.getString("JFIMF_ScopeHeader"));

            JPanel messagePanel = new JPanel();
            messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.X_AXIS));
            messagePanel.add(messageLabel);
            messagePanel.add(Box.createHorizontalStrut(200));
            messagePanel.add(Box.createHorizontalGlue());
            mainPanel.add(messagePanel, constraints);
            numRows++;
        }
        
        // Scope selection
        {
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.anchor = GridBagConstraints.WEST;
            constraints.fill = GridBagConstraints.HORIZONTAL;

            constraints.gridx = 0;
            constraints.gridy = numRows;
            constraints.insets = new Insets(0, 15, 0, 0);

            scopeSelectorPanel = new ScopeSelectorPanel();
            
            mainPanel.add(scopeSelectorPanel, constraints);
            numRows++;
        }
        
        // Object optional methods Label.
        {
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.anchor = GridBagConstraints.NORTHWEST;
            constraints.fill = GridBagConstraints.HORIZONTAL;

            constraints.gridx = 0;
            constraints.gridy = numRows;
            constraints.gridwidth = GridBagConstraints.REMAINDER;

            constraints.insets = new Insets(10, 0, 0, 0);
            
            JLabel messageLabel = new JLabel(GeneratorMessages.getString("JFIMF_OptionalObjectMethodsHeader"));

            JPanel messagePanel = new JPanel();
            messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.X_AXIS));
            messagePanel.add(messageLabel);
            messagePanel.add(Box.createHorizontalStrut(200));
            messagePanel.add(Box.createHorizontalGlue());
            mainPanel.add(messagePanel, constraints);
            numRows++;
        }
        
        // Check boxes for optional Object methods.
        {
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.anchor = GridBagConstraints.WEST;
            constraints.fill = GridBagConstraints.HORIZONTAL;

            constraints.gridx = 0;
            constraints.gridy = numRows;

            constraints.insets = new Insets(0, 15, 0, 0);

            methodExcludeSelectorPanel = new MethodExcludeSelectorPanel();

            mainPanel.add(methodExcludeSelectorPanel, constraints);
            numRows++;
        }
        
        return mainPanel;
    }
    
    /**
     * @return the action to return in response to pressing the "Add Pattern..." button.
     */
    private Action getAddPatternAction() {
        String buttonText = GeneratorMessages.getString("JFIMF_AddPattern");
        return new AbstractAction(buttonText) {
            
            private static final long serialVersionUID = 8776068822378523687L;

            // Add an element.
            public void actionPerformed(ActionEvent e) {

                // Show the pattern dialog.
                AddEditPatternDialog addPatternDialog = new AddEditPatternDialog(JavaForeignImportModuleGenerator.this, null);
                boolean accepted = addPatternDialog.doModal();

                // Add the pattern if accepted.
                if (accepted) {
                    patternsJList.addPattern(addPatternDialog.getFitPattern());
                }
            }
        };
    }
    
    /**
     * Workaround for sun bug id 4711700
     * A bug in the native image loading code sometimes causes the Windows file chooser to throw a 
     *   NullPointerException when instantiated in jdk 1.4.2.
     */
    private static void ensureJFileChooserLoadable() {

        if (UIManager.getLookAndFeel().getName().startsWith("Windows")) {
            JFileChooser fileChooser = null;
            
            while (fileChooser == null) {
                try {
                    fileChooser = new JFileChooser();
                } catch (NullPointerException e) {
                }
            }
        }
    }

    /**
     * Display the File chooser for selecting an import source.
     * 
     * @param currentFile
     *            If non-null the file chooser will be specified as "Change"ing
     *            the given import. Otherwise, it will be set up as adding a new
     *            import file.
     * @param chooseDir
     *            if true, this the chooser is to select a directory. If false,
     *            it's so select a file.
     * @return the File selected by the file chooser, or null if the chooser was
     *         not approved (eg. canceled).
     */
    private File showSelectImportFileChooser(File currentFile, boolean chooseDir) {

        if (selectImportDialog == null) {
            selectImportDialog = new SelectImportDialog(JavaForeignImportModuleGenerator.this);
        }
        int result = selectImportDialog.showDialog(currentFile, chooseDir);
        
        // Return the result..
        if (result == JFileChooser.APPROVE_OPTION) {
            return selectImportDialog.getSelectedFile();
        }
        
        return null;
    }
    
    /**
     * @return the action to return in response to pressing the "Add Jar..." button.
     */
    private Action getAddImportJarAction() {
        String buttonText = GeneratorMessages.getString("JFIMF_AddImportJar");
        return new AbstractAction(buttonText) {
            
            private static final long serialVersionUID = -1327110975197857640L;

            public void actionPerformed(ActionEvent e) {
                File chosenFile = showSelectImportFileChooser(null, false);
                if (chosenFile != null) {
                    ImportSource importSource = new ImportSource(chosenFile, false);
                    importFromJList.addImport(importSource);
                }
            }
        };
    }
    
    /**
     * @return the action to return in response to pressing the "Add Folder..." button.
     */
    private Action getAddImportFolderAction() {
        String buttonText = GeneratorMessages.getString("JFIMF_AddImportFolder");
        return new AbstractAction(buttonText) {
            
            private static final long serialVersionUID = -7399609649158755721L;

            public void actionPerformed(ActionEvent e) {
                File chosenFile = showSelectImportFileChooser(null, true);
                if (chosenFile != null) {
                    ImportSource importSource = new ImportSource(chosenFile, true);
                    importFromJList.addImport(importSource);
                }
            }
        };
    }
    
    /**
     * @return the action to return in response to pressing the "Change..." button for imports.
     */
    private Action getChangeImportAction() {
        String buttonText = GeneratorMessages.getString("JFIMF_ChangeImport");
        return new AbstractAction(buttonText) {
            
            private static final long serialVersionUID = -2922337374842017884L;

            public void actionPerformed(ActionEvent e) {
                ImportSource selectedImportSource = (ImportSource)importFromJList.getSelectedValue();
                
                if (selectedImportSource != null) {
                    File chosenFile = showSelectImportFileChooser(selectedImportSource.getImportFile(), selectedImportSource.isDir());
                    if (chosenFile != null) {
                        ImportSource newImportSource = new ImportSource(chosenFile, selectedImportSource.isDir());
                        importFromJList.editImport(selectedImportSource, newImportSource);
                    }
                }
            }
        };
    }
    
    /**
     * @return the action to return in response to pressing the "Remove" button for imports.
     */
    private Action getRemoveImportAction() {
        String buttonText = GeneratorMessages.getString("JFIMF_RemoveImport");
        return new AbstractAction(buttonText) {
            
            private static final long serialVersionUID = -6469392988956528085L;

            public void actionPerformed(ActionEvent e) {
                importFromJList.removeSelectedImport();
            }
        };
    }
    
    /**
     * @return the action to return in response to pressing the "Edit..." button for patterns.
     */
    private Action getEditPatternAction() {
        String buttonText = GeneratorMessages.getString("JFIMF_EditPattern");
        return new AbstractAction(buttonText) {
            
            private static final long serialVersionUID = -527928622059171444L;

            // Edit selected element.
            public void actionPerformed(ActionEvent e) {
                JFit.Pattern selectedPattern = patternsJList.getSelectedPattern();
                
                // Show the pattern dialog.
                AddEditPatternDialog editPatternDialog = new AddEditPatternDialog(JavaForeignImportModuleGenerator.this, selectedPattern);
                boolean accepted = editPatternDialog.doModal();

                // Edit the pattern if accepted.
                if (accepted) {
                    patternsJList.editPattern(selectedPattern, editPatternDialog.getFitPattern());
                }
            }
        };
    }
    
    /**
     * @return the action to return in response to pressing the "Remove" button for patterns.
     */
    private Action getRemovePatternAction() {
        String buttonText = GeneratorMessages.getString("JFIMF_RemovePattern");
        return new AbstractAction(buttonText) {
            
            private static final long serialVersionUID = -1965662784577019107L;

            public void actionPerformed(ActionEvent e) {
                // Remove the selected pattern.
                patternsJList.removeSelectedPattern();
            }
        };
    }
    
    /**
     * Add default imports to the Import From area.
     */
    private void addDefaultImports() {

        // Add the runtime jar if it exists.
        // (java.home)/lib/rt.jar
        File javaRuntimeJar = new File(JAVA_HOME, "lib" + System.getProperty("file.separator") + "rt.jar");
        
        if (FileSystemHelper.fileExists(javaRuntimeJar)) {
            ImportSource importSource = new ImportSource(javaRuntimeJar, false);
            importFromJList.addImport(importSource);
        }
    }

    /**
     * Updates the state of the Ok button to only be enabled if the user has entered
     * all required information. Also updates the information message displayed.
     */
    private void updateState() {

        // Make sure that you check for errors first, then for warnings.
        // Be careful about the order of checks, more important checks come first.
        
        /*
         * Errors.
         */
        
        // Check for a valid module name.
        String moduleNameString = moduleNameField.getText();
        if (moduleNameString.length() == 0) {
            
            statusLabel.setText(GeneratorMessages.getString("JFIMF_NoModuleName"));
            statusLabel.setIcon(ERROR_ICON);
            okButton.setEnabled(false);
            return;
        }
        ModuleName moduleName = ModuleName.maybeMake(moduleNameString);
        if (moduleName == null) {
            
            statusLabel.setText(GeneratorMessages.getString("JFIMF_InvalidModuleName"));
            statusLabel.setIcon(ERROR_ICON);
            okButton.setEnabled(false);
            return;
        }
        
        // Check for a location to import from.
        if (importFromJList.getModel().getSize() < 1) {
            
            String message = GeneratorMessages.getString("JFIMF_NoImportFrom");
            
            statusLabel.setText(message);
            statusLabel.setToolTipText(message);
            statusLabel.setIcon(ERROR_ICON);
            okButton.setEnabled(false);
            return;
        }
        
        // Check that the patterns pane is non-empty.
        if (patternsJList.isEmpty()) {
            
            String message = GeneratorMessages.getString("JFIMF_NoPatternsMessage");
            
            statusLabel.setText(message);
            statusLabel.setToolTipText(message);
            statusLabel.setIcon(ERROR_ICON);
            okButton.setEnabled(false);
            return;
        }
        
        // If here, no errors.  Warnings at worst.
        okButton.setEnabled(true);
        
        /*
         * Warnings.
         */
        
        // Check whether the module name exists.
        // This goes to the workspace source manager -- so it's a bit hacky.
        // We can't just ask the workspace if it has the module though, since it might be the case where the workspace isn't
        //   using a module in the nullary case.
        if (perspective.getWorkspace().getSourceManager(moduleName).getResourceStore().hasFeature(
                new ResourceName(CALFeatureName.getModuleFeatureName(moduleName)))) {
            
            String message = GeneratorMessages.getString("JFIMF_ModuleExists");
            
            statusLabel.setText(message);
            statusLabel.setToolTipText(message);
            statusLabel.setIcon(WARNING_ICON);
            return;
        }
        
        // everything is fine.
        String message = GeneratorMessages.getString("JFIMF_OkGenerateModule");
        statusLabel.setText(message);
        statusLabel.setToolTipText(message);
        statusLabel.setIcon(OK_ICON);
    }    
    
}
