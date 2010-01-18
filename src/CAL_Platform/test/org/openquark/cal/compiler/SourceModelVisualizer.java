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
 * SourceModelVisualizer.java
 * Created: Sep 4, 2007
 * By: Joseph Wong
 */

package org.openquark.cal.compiler;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import org.openquark.cal.compiler.IdentifierOccurrenceFinder.FinderState;
import org.openquark.cal.compiler.IdentifierResolver.SymbolTable;
import org.openquark.cal.compiler.IdentifierResolver.VisitorArgument;
import org.openquark.cal.services.BasicCALServices;
import org.openquark.cal.services.Status;
import org.openquark.cal.services.WorkspaceManager;
import org.openquark.util.Pair;

/**
 * A visualizer of source ranges for {@link SourceModel} and {@link IdentifierOccurrence}.
 * 
 * This application will likely need -Xmx256m or more to allow the CAL workspace to successfully compile.
 * 
 * To change the workspace being explored, specify it as a command line argument.
 * To change the default workspace being explored, edit the {@link #WORKSPACE_NAME} constant.
 * 
 * @author Joseph Wong
 * @author James Wright
 */
public class SourceModelVisualizer extends JFrame {

    public static final String WORKSPACE_NAME = "cal.platform.test.cws";

    private static final long serialVersionUID = 5738413354269889573L;

    /**
     * A utility class for finding all non static fields (even protected and private ones) in a
     * class and all its superclasses.
     * 
     * @author Joseph Wong
     */
    static final class ReflectiveFieldsFinder {

        /** Memoized results from previous runs. */
        private final Map<Class<?>, Field[]> classToFields = new HashMap<Class<?>, Field[]>();

        /**
         * A map mapping a Field to its original accessibility flag, for use during cleanup when the
         * accessibility flags should be reset back to their original values.
         */
        private final Map<Field, Boolean> fieldAccessibility = new HashMap<Field, Boolean>();

        /** The singleton instance. */
        static final ReflectiveFieldsFinder INSTANCE = new ReflectiveFieldsFinder();

        /** Private constructor. */
        private ReflectiveFieldsFinder() {
        }

        /**
         * Gets an array of the non-static fields of the given class and all its superclasses.
         * 
         * @param cls
         *            the Class to query.
         * @return all the non-static fields (even private and protected ones) of the class and its
         *         superclasses.
         */
        Field[] getAllNonStaticFields(Class<?> cls) {
            Field[] fields = classToFields.get(cls);

            if (fields != null) {
                return fields;
            }

            List<Field> fieldList = new ArrayList<Field>();

            Class<?> currentClass = cls;
            while (currentClass != null) {
                Field[] curClassFields = currentClass.getDeclaredFields();

                for (final Field f : curClassFields) {
                    if (!Modifier.isStatic(f.getModifiers())) {
                        fieldAccessibility.put(f, Boolean.valueOf(f.isAccessible()));

                        f.setAccessible(true);
                        fieldList.add(f);
                    }
                }

                currentClass = currentClass.getSuperclass();
            }

            fields = fieldList.toArray(new Field[0]);

            classToFields.put(cls, fields);

            return fields;
        }

        /**
         * Cleans up by reseting the accessibility flags modified during invocations to
         * getAllNonStaticFields().
         */
        void cleanup() {
            for (final Map.Entry<Field, Boolean> entry : fieldAccessibility.entrySet()) {
                Field field = entry.getKey();
                Boolean isAccessible = entry.getValue();

                field.setAccessible(isAccessible.booleanValue());
            }

            classToFields.clear();
            fieldAccessibility.clear();
        }

        @Override
        public void finalize() {
            cleanup();
        }
    }

    /**
     * Provides a modeless dialogue for displaying the source of a module with a hit highlighted.
     * 
     * @author Jawright
     */
    private static final class SearchResultsDialog extends JDialog {

        private static final long serialVersionUID = -122928860414946366L;

        /** Used for finding and loading/saving modules based on their names */
        private final WorkspaceManager workspaceManager;

        /** Text area where the source is displayed and (potentially) edited */
        private final JTextPane editorPane = new JTextPane();

        /** Provides scroll functionality for the editorPane */
        private final JScrollPane scrollPane = new JScrollPane(editorPane);

        /** Module name of the current search hit */
        private ModuleName currentModuleName = null;

        /** Size of the parent frame */
        private final Rectangle parentBounds;

        /**
         * Construct a new SearchResultsDialog.
         * 
         * @param parent
         *            Parent window for this dialog
         * @param workspaceManager
         *            workspace manager to obtain Workspace from for searching
         */
        SearchResultsDialog(JFrame parent, WorkspaceManager workspaceManager) {
            super(parent);
            this.workspaceManager = workspaceManager;
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
            buttonBox.add(Box.createHorizontalStrut(10));
            buttonBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 2, 2));

            final Container pane = getContentPane();
            pane.add(buttonBox, "South");
            pane.add(scrollPane, "Center");

            editorPane.setFont(new Font("Monospaced", Font.PLAIN, 12));

            scrollPane.setMinimumSize(new Dimension(600, 600));
            scrollPane.setPreferredSize(new Dimension(600, 600));
            scrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

            /*
            addWindowListener(new WindowAdapter() {
                @Override
                public void windowActivated(WindowEvent e) {
                    // Force the focus to the editor component (rather than, say, the
                    // Close button) to ensure that the selection is visible.
                    editorPane.requestFocusInWindow();
                }

            });
            */

            pack();
            setModal(false);

            //setLocation(parentBounds.x + parentBounds.width - getWidth(), parentBounds.y);
        }

        /**
         * Sets the text and selection of the dialog based on a SourcePosition.
         */
        void setResult(ModuleName moduleName, SourceRange sourceRange) {
            
            // Update text if necessary
            if (currentModuleName == null || !currentModuleName.equals(moduleName)) {

                Reader sourceReader = workspaceManager.getWorkspace().getSourceDefinition(moduleName).getSourceReader(new Status("reading source for search hit display"));
                if (sourceReader == null) {
                    System.err.println("Could not read source definition for source: " + moduleName);
                    return;
                }
                sourceReader = new BufferedReader(sourceReader);

                try {
                    editorPane.select(0, 0);
                    editorPane.setCharacterAttributes(SimpleAttributeSet.EMPTY, true);
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

            currentModuleName = moduleName;
            setTitle("Source of " + moduleName.toSourceText());

            if (sourceRange == null) {
                // deselects
                final int previousPosition = editorPane.getSelectionStart();
                editorPane.select(previousPosition, previousPosition);
                return;
            }
            
            selectTargetAtCurrentPosition(sourceRange);

            editorPane.setEditable(false);
        }

        void selectTargetAtCurrentPosition(SourceRange sourceRange) {
            Document doc = editorPane.getDocument();
            SourcePosition startPosition = sourceRange.getStartSourcePosition();
            SourcePosition endPosition = sourceRange.getEndSourcePosition();

            try {

                int len = doc.getLength();
                String text = doc.getText(0, len);
                int startIndex = startPosition.getPosition(text);
                int endIndex = endPosition.getPosition(text, startPosition, startIndex);

                editorPane.select(startIndex, endIndex);
                editorPane.setSelectedTextColor(Color.BLUE);

            } catch (BadLocationException e1) {
                e1.printStackTrace();
            }
        }
        
        void highlightDoc(List<SourceRange> sourceRanges) {
            if (sourceRanges.isEmpty()) {
                return;
            }
            
            Document doc = editorPane.getDocument();
            int len = doc.getLength();
            try {
                String text = doc.getText(0, len);
                
                SourcePosition lastPosition = sourceRanges.get(0).getStartSourcePosition();
                int lastIndex = lastPosition.getPosition(text);
                
                for (final SourceRange sourceRange : sourceRanges) {
                    SourcePosition startPosition = sourceRange.getStartSourcePosition();
                    SourcePosition endPosition = sourceRange.getEndSourcePosition();
                    
                    int startIndex = startPosition.getPosition(text, lastPosition, lastIndex);
                    int endIndex = endPosition.getPosition(text, startPosition, startIndex);
                    
                    editorPane.select(startIndex, endIndex);
                    final SimpleAttributeSet attributeSet = new SimpleAttributeSet(editorPane.getCharacterAttributes());
                    attributeSet.addAttribute(StyleConstants.Foreground, new Color(0x00, 0x99, 0xcc));
                    editorPane.setCharacterAttributes(attributeSet, false);
                    
                    // we keep the start position because those are guaranteed to be monotonically increasing
                    // and at least less than or equal to all subsequent start-end pairs
                    lastPosition = startPosition;
                    lastIndex = startIndex;
                }
                
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * The CAL services to use.
     */
    private final BasicCALServices calServices;

    /*
     * GUI components
     */
    private JSplitPane horizontalSplitPane;
    private JSplitPane verticalSplitPane;
    private JList moduleList;
    private JTree sourceModelTree;
    private JTree identifierOccurrenceTree;
    private SearchResultsDialog dialog;
    private JLabel sourceModelTitleLabel;
    private JLabel identifierOccurrencesTitleLabel;


    /**
     * Constructs an instance of this class.
     */
    private SourceModelVisualizer(String cwsName) {
        super("Source Model Visualizer");

        MessageLogger messageLogger = new MessageLogger();
        calServices = BasicCALServices.makeCompiled(cwsName, messageLogger);

        if (messageLogger.getNErrors() > 0) {
            System.err.println(messageLogger);
            System.exit(1);
        }
    }

    /**
     * Initialize the GUI and hook up the various components with the backend.
     */
    private void init() {

        dialog = new SearchResultsDialog(this, calServices.getWorkspaceManager());

        final ModuleName[] moduleNamesInProgram = calServices.getWorkspaceManager().getModuleNamesInProgram();
        Arrays.sort(moduleNamesInProgram);
        moduleList = new JList(moduleNamesInProgram);
        final JPanel moduleListPane = new JPanel(new BorderLayout());
        moduleListPane.add(new JLabel("Modules"), BorderLayout.NORTH);
        moduleListPane.add(new JScrollPane(moduleList), BorderLayout.CENTER);

        moduleList.setSelectedIndex(moduleNamesInProgram.length - 1);
        final ModuleName firstModule = moduleNamesInProgram[moduleNamesInProgram.length - 1];

        sourceModelTitleLabel = new JLabel("Source Model of " + firstModule);
        identifierOccurrencesTitleLabel = new JLabel("Identifier Occurrences of " + firstModule);
        
        sourceModelTree = new JTree();
        final JPanel sourceModelTreePane = new JPanel(new BorderLayout());
        sourceModelTreePane.add(sourceModelTitleLabel, BorderLayout.NORTH);
        sourceModelTreePane.add(new JScrollPane(sourceModelTree), BorderLayout.CENTER);
        
        identifierOccurrenceTree = new JTree();
        final JPanel identifierOccurrenceTreePane = new JPanel(new BorderLayout());
        identifierOccurrenceTreePane.add(identifierOccurrencesTitleLabel, BorderLayout.NORTH);
        identifierOccurrenceTreePane.add(new JScrollPane(identifierOccurrenceTree), BorderLayout.CENTER);
        
        dialog.setResult((ModuleName)moduleList.getSelectedValue(), null);
        final Pair<TreeModel, TreeModel> treeModels = makeTreeModels(firstModule);
        sourceModelTree.setModel(treeModels.fst());
        identifierOccurrenceTree.setModel(treeModels.snd());

        horizontalSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, moduleListPane, sourceModelTreePane);
        
        verticalSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, horizontalSplitPane, identifierOccurrenceTreePane);

        getContentPane().add(verticalSplitPane, BorderLayout.CENTER);

        moduleList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {

                    if (moduleList.getSelectedIndex() != -1) {
                        final ModuleName moduleName = (ModuleName)moduleList.getSelectedValue();
                        dialog.setResult(moduleName, null);
                        dialog.setVisible(true);
                        final Pair<TreeModel, TreeModel> treeModels = makeTreeModels(moduleName);
                        sourceModelTitleLabel.setText("Source Model of " + moduleName.toSourceText());
                        sourceModelTree.setModel(treeModels.fst());
                        identifierOccurrencesTitleLabel.setText("Identifier Occurrences in " + moduleName.toSourceText());
                        identifierOccurrenceTree.setModel(treeModels.snd());
                    }
                }
            }
        });

        sourceModelTree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                if (sourceModelTree.getSelectionCount() > 0) {
                    final DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)sourceModelTree.getSelectionPath().getLastPathComponent();
                    final Object userObject = selectedNode.getUserObject();
                    if (userObject instanceof SourceModelTreeNodeData) {
                        SourceModelTreeNodeData data = (SourceModelTreeNodeData)userObject;
                        dialog.setResult((ModuleName)moduleList.getSelectedValue(), data.getElement().getSourceRange());
                        dialog.setVisible(true);
                        SourceModelVisualizer.this.requestFocus();
                        
                        if (selectedNode.getChildCount() > 0) {
                            sourceModelTree.expandPath(sourceModelTree.getSelectionPath());
                        }
                    } else {
                        if (selectedNode.getChildCount() > 0) {
                            sourceModelTree.setSelectionPath(sourceModelTree.getSelectionPath().pathByAddingChild(selectedNode.getFirstChild()));
                        }
                    }
                }
            }
        });

        identifierOccurrenceTree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                if (identifierOccurrenceTree.getSelectionCount() > 0) {
                    final DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)identifierOccurrenceTree.getSelectionPath().getLastPathComponent();
                    final Object userObject = selectedNode.getUserObject();
                    if (userObject instanceof IdentifierOccurrenceTreeNodeData) {
                        IdentifierOccurrenceTreeNodeData data = (IdentifierOccurrenceTreeNodeData)userObject;
                        dialog.setResult((ModuleName)moduleList.getSelectedValue(), data.getIdentifierOccurrence().getSourceRange());
                        dialog.setVisible(true);
                        SourceModelVisualizer.this.requestFocus();
                    }
                    
                    if (selectedNode.getChildCount() > 0) {
                        identifierOccurrenceTree.expandPath(identifierOccurrenceTree.getSelectionPath());
                    }
                }
            }
        });

        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/Resources/cal_32x32.jpg")));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                dialog.setVisible(true);
            }});
    }

    /**
     * Builds the two tree models - one for the source model and one for identifier occurrences.
     * @param moduleName the name of the module off which the models will be built.
     * @return the pair of tree models.
     */
    private Pair<TreeModel, TreeModel> makeTreeModels(ModuleName moduleName) {
        ModuleSourceDefinition moduleSource = calServices.getCALWorkspace().getSourceDefinition(moduleName);

        Status status = new Status("Proccessing module " + moduleSource.getModuleName());
        Reader reader = moduleSource.getSourceReader(status);

        BufferedReader bufferedReader = new BufferedReader(reader);
        StringBuilder stringBuf = new StringBuilder();

        String line = null;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                stringBuf.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }

        String origSource = stringBuf.toString();

        SourceModel.SourceElement moduleDefn = SourceModelUtilities.TextParsing.parseModuleDefnIntoSourceModel(origSource);

        // the source model tree
        DefaultMutableTreeNode sourceModelTreeTop = new DefaultMutableTreeNode(new SourceModelTreeNodeData(moduleDefn));
        final DefaultTreeModel sourceModelTreeModel = new DefaultTreeModel(sourceModelTreeTop);
        buildSourceModelTree(sourceModelTreeTop, moduleDefn);
        
        // the identifier occurrence tree
        DefaultMutableTreeNode occurrenceTreeTop = new DefaultMutableTreeNode();
        final DefaultTreeModel occurrenceTreeModel = new DefaultTreeModel(occurrenceTreeTop);
        
        IdentifierOccurrenceCollector<Void> collector = new IdentifierOccurrenceCollector<Void>(moduleName, true, true, true, true, true);
        moduleDefn.accept(collector, VisitorArgument.make(SymbolTable.makeRoot(moduleName, IdentifierResolver.makeContext(calServices.getModuleTypeInfo(moduleName))), FinderState.make()));
        final List<IdentifierOccurrence<?>> occurrences = collector.getCollectedOccurrences();
        
        buildIdentifierOccurrenceTree(occurrenceTreeTop, occurrences);

        return Pair.<TreeModel, TreeModel>make(sourceModelTreeModel, occurrenceTreeModel);
    }

    /**
     * The data object for nodes in the occurrence tree.
     *
     * @author Joseph Wong
     */
    private static final class IdentifierOccurrenceTreeNodeData {
        private final IdentifierOccurrence<?> occurrence;
        private final String prefix;

        IdentifierOccurrenceTreeNodeData(IdentifierOccurrence<?> occurrence, String prefix) {
            this.occurrence = occurrence;
            this.prefix = prefix;
        }

        public String toString() {
            return prefix
                + occurrence.getClass().getName().replaceAll("^org.openquark.cal.compiler.IdentifierOccurrence.", "").replace('$', '.')
                + "<"
                + occurrence.getIdentifierInfo().getClass().getName().replaceAll("^org.openquark.cal.compiler.IdentifierInfo.", "").replace('$', '.')
                + "> "
                + occurrence.getSourceRange();
        }

        IdentifierOccurrence<?> getIdentifierOccurrence() {
            return occurrence;
        }
    }

    /**
     * Builds the occurrence tree.
     * @param parent the parent node.
     * @param occurrences the occurrences to display.
     */
    private void buildIdentifierOccurrenceTree(DefaultMutableTreeNode parent, List<IdentifierOccurrence<?>> occurrences) {

        List<SourceRange> sourceRanges = new ArrayList<SourceRange>();
        
        SourceRange lastSourceRange = new SourceRange(new SourcePosition(0, 0), new SourcePosition(0, 0));
        for (final IdentifierOccurrence<?> occurrence : occurrences) {
            if (occurrence.getSourceRange() != null) {
                if (lastSourceRange.contains(occurrence.getSourceRange())
                        && occurrence.getSourceRange().contains(lastSourceRange)) {
                    System.err.println("Duplicate source range: " + lastSourceRange);
                    System.err.println(occurrence.getSourceElement());
                }
            }

            buildIdentifierOccurrenceNode(parent, occurrence);
            
            if (occurrence.getSourceRange() != null) {
                sourceRanges.add(occurrence.getSourceRange());
            }

            if (occurrence.getSourceRange() != null) {
                lastSourceRange = occurrence.getSourceRange();
            }
        }
        
        dialog.highlightDoc(sourceRanges);
    }

    /**
     * Builds a node for an occurrence.
     * @param parent the parent node.
     * @param occurrence an occurrence.
     */
    private void buildIdentifierOccurrenceNode(DefaultMutableTreeNode parent, final IdentifierOccurrence<?> occurrence) {
        DefaultMutableTreeNode occurrenceNode = new DefaultMutableTreeNode(new IdentifierOccurrenceTreeNodeData(occurrence, ""));
        parent.add(occurrenceNode);
        
        buildSubtreeForObjectWithFieldsForIdentifierOccurrenceTree(occurrenceNode, occurrence);
    }

    /**
     * Builds a subtree for an object with fields for the occurrence tree.
     * @param parent the parent node.
     * @param objectWithFields the object with fields.
     */
    private void buildSubtreeForObjectWithFieldsForIdentifierOccurrenceTree(DefaultMutableTreeNode parent, Object objectWithFields) {
        Class<?> objectClass = objectWithFields.getClass();
        
        Field[] objectFields = ReflectiveFieldsFinder.INSTANCE.getAllNonStaticFields(objectClass);
        
        for (final Field field : objectFields) {
            try {
                final String fieldName = field.getName();
                Object fieldValue = field.get(objectWithFields);
                buildNodeForObjectWithFieldsForIdentifierOccurrenceTree(parent, fieldName, fieldValue);
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        }
    }

    /**
     * Builds a node for an object with fields for the occurrence tree.
     * @param parent the parent node.
     * @param fieldName the field name.
     * @param fieldValue the value.
     */
    private void buildNodeForObjectWithFieldsForIdentifierOccurrenceTree(DefaultMutableTreeNode parent, final String fieldName, Object fieldValue) {
        if (fieldValue instanceof IdentifierOccurrence<?>) {
            final IdentifierOccurrence<?> occurrence = (IdentifierOccurrence<?>)fieldValue;
            DefaultMutableTreeNode fieldNode = new DefaultMutableTreeNode(new IdentifierOccurrenceTreeNodeData(occurrence, fieldName + " = "));
            parent.add(fieldNode);
      
            buildSubtreeForObjectWithFieldsForIdentifierOccurrenceTree(fieldNode, occurrence);
            
        } else if (fieldValue instanceof IdentifierInfo) {
            IdentifierInfo identifierInfo = (IdentifierInfo)fieldValue;
            DefaultMutableTreeNode identifierInfoNode = new DefaultMutableTreeNode(fieldName + " = " + identifierInfo.getClass().getName().replaceAll("^org.openquark.cal.compiler.IdentifierInfo.", "").replace('$', '.'));
            parent.add(identifierInfoNode);
      
            buildSubtreeForObjectWithFieldsForIdentifierOccurrenceTree(identifierInfoNode, identifierInfo);
      
        } else if (fieldValue instanceof SourceModel.SourceElement) {
            DefaultMutableTreeNode fieldNode = new DefaultMutableTreeNode(
                fieldName + " :: " + displayNameForClassOfSourceElement((SourceModel.SourceElement)fieldValue));
            parent.add(fieldNode);
            
        } else if (fieldValue instanceof Iterable<?>){
            DefaultMutableTreeNode listNode = new DefaultMutableTreeNode("[" + fieldName + "]");
            parent.add(listNode);
            
            for (final Object element : (Iterable<?>)fieldValue) {
                buildNodeForObjectWithFieldsForIdentifierOccurrenceTree(listNode, "...", element);
            }
            
        } else {
            DefaultMutableTreeNode fieldNode = new DefaultMutableTreeNode(fieldName + " = " + fieldValue);
            parent.add(fieldNode);
        }
    }

    /**
     * The data object for nodes in the source model tree.
     *
     * @author Joseph Wong
     */
    private static final class SourceModelTreeNodeData {
        private final SourceModel.SourceElement element;

        SourceModelTreeNodeData(SourceModel.SourceElement element) {
            this.element = element;
        }

        public String toString() {
            return displayNameForClassOfSourceElement(element);
        }

        SourceModel.SourceElement getElement() {
            return element;
        }
    }
    
    /**
     * @return the display name for a SourceElement subclass.
     */
    private static String displayNameForClassOfSourceElement(SourceModel.SourceElement sourceElement) {
        return sourceElement.getClass().getName().replaceAll("^org.openquark.cal.compiler.SourceModel.", "").replace('$', '.');
    }

    /**
     * Builds the source model tree.
     * @param node the root node.
     * @param element the source element to represent.
     */
    void buildSourceModelTree(DefaultMutableTreeNode node, SourceModel.SourceElement element) {
        if (element == null) {
            return;
        }

        Class<? extends SourceModel.SourceElement> elementClass = element.getClass();

        Field[] fields = ReflectiveFieldsFinder.INSTANCE.getAllNonStaticFields(elementClass);

        for (final Field field : fields) {
            try {
                Object fieldValue = field.get(element);

                if (fieldValue instanceof SourceModel.SourceElement) {
                    final SourceModel.SourceElement fieldElement = (SourceModel.SourceElement)fieldValue;

                    if (fieldElement != null) {
                        DefaultMutableTreeNode fieldNameNode = new DefaultMutableTreeNode("(" + field.getName() + ")");
                        node.add(fieldNameNode);
                        DefaultMutableTreeNode fieldElementNode = new DefaultMutableTreeNode(new SourceModelTreeNodeData(fieldElement));
                        fieldNameNode.add(fieldElementNode);

                        // recurse
                        buildSourceModelTree(fieldElementNode, fieldElement);
                    }

                } else if (fieldValue instanceof SourceModel.SourceElement[]) {
                    final SourceModel.SourceElement[] fieldElements = (SourceModel.SourceElement[])fieldValue;

                    if (fieldElements.length > 0) {
                        DefaultMutableTreeNode fieldNameNode = new DefaultMutableTreeNode("[" + field.getName() + "]");
                        node.add(fieldNameNode);

                        for (final SourceModel.SourceElement fieldElement : fieldElements) {
                            DefaultMutableTreeNode fieldElementNode = new DefaultMutableTreeNode(new SourceModelTreeNodeData(fieldElement));
                            fieldNameNode.add(fieldElementNode);

                            // recurse
                            buildSourceModelTree(fieldElementNode, fieldElement);
                        }
                    }

                }
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        }
    }

    /**
     * The main method.
     * @param args
     */
    public static void main(String[] args) {
        final String cwsName;
        if (args.length > 0) {
            cwsName = args[0];
        } else {
            cwsName = WORKSPACE_NAME;
        }
        System.out.println("Starting the Source Model Visualizer");
        System.out.println("Initializing CAL workspace...");
        final SourceModelVisualizer instance = new SourceModelVisualizer(cwsName);
        instance.init();
        System.out.println("...done");
        instance.setVisible(true);
    }

}
