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
 * ValueEditorManager.java
 * Creation date: Dec 23, 2002.
 * By: Edward Lam
 */
package org.openquark.gems.client.valueentry;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.FontMetrics;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.border.Border;

import org.openquark.cal.compiler.DataConstructor;
import org.openquark.cal.compiler.FieldName;
import org.openquark.cal.compiler.ModuleTypeInfo;
import org.openquark.cal.compiler.PreludeTypeConstants;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.RecordType;
import org.openquark.cal.compiler.ScopedEntityNamingPolicy;
import org.openquark.cal.compiler.TypeConsApp;
import org.openquark.cal.compiler.TypeConstructor;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.compiler.ScopedEntityNamingPolicy.UnqualifiedUnlessAmbiguous;
import org.openquark.cal.compiler.TypeChecker.TypeCheckInfo;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.module.Cal.Graphics.CAL_Color;
import org.openquark.cal.module.Cal.IO.CAL_File;
import org.openquark.cal.module.Cal.Utilities.CAL_Range;
import org.openquark.cal.module.Cal.Utilities.CAL_RelativeTime;
import org.openquark.cal.module.Cal.Utilities.CAL_Time;
import org.openquark.cal.services.CALWorkspace;
import org.openquark.cal.services.MetaModule;
import org.openquark.cal.services.Perspective;
import org.openquark.cal.valuenode.ForeignValueNode;
import org.openquark.cal.valuenode.LiteralValueNode;
import org.openquark.cal.valuenode.ValueNode;
import org.openquark.cal.valuenode.ValueNodeBuilderHelper;
import org.openquark.cal.valuenode.ValueNodeCommitHelper;
import org.openquark.cal.valuenode.ValueNodeProvider;
import org.openquark.cal.valuenode.ValueNodeTransformer;
import org.openquark.gems.client.TypeColourProvider;
import org.openquark.gems.client.utilities.SmoothHighlightBorder;
import org.openquark.util.TextEncodingUtilities;
import org.openquark.util.UnsafeCast;


/**
 * This class is used to help during the construction and management of ValueEditors.
 * @author Edward Lam
 */
public final class ValueEditorManager implements TypeColourProvider {

    /** The namespace for log messages from the value editors. */
    public static final String VALUEENTRY_LOGGER_NAMESPACE = "org.openquark.gems.client.valueentry";
    
    /** An instance of a Logger for value editor messages. */
    static final Logger VALUEENTRY_LOGGER = Logger.getLogger(VALUEENTRY_LOGGER_NAMESPACE);
    
    static {
        VALUEENTRY_LOGGER.setLevel(Level.FINEST);
    }
    
    /** The maximum preferred width allowed for any type. */
    private static final int MAX_PREFERRED_WIDTH = 400;

    /** The BuilderHelper used throughout the ValueEditors. */
    private final ValueNodeBuilderHelper valueNodeBuilderHelper;

    /** The type colour provider to provide type colours to the value editors. */
    private final TypeColourProvider typeColourProvider;    
    
    /** The type check info used by the value editor manager. */
    private final TypeCheckInfo typeCheckInfo;
    
    /** The ValueNodeTransformer used to convert data between ValueNodes. */
    private ValueNodeTransformer valueNodeTransformer;
    
    /** The value node commit helper for this value editor manager. */
    private final ValueNodeCommitHelper valueNodeCommitHelper;

    /** The director is consulted when a new ValueEditor is needed in order to determine which ValueEditor to use. */
    private ValueEditorDirector valueEditorDirector;

    /** Value editor providers that can be used to lookup and create value editors. */
    private final List<ValueEditorProvider<?>> valueEditorProviders = new ArrayList<ValueEditorProvider<?>>();
    
    /** Value node provider classes that are used to init value node builder helpers. */
    private final List<Class<ValueNodeProvider<?>>> valueNodeProviderClasses = new ArrayList<Class<ValueNodeProvider<?>>>();
    
    /**
     * Basically allows ValueEditors to access the Info associated with a particular ValueNode.
     * We use a weak hash map so that value nodes no longer in use are garbage collected (along with their info).
     */
    private final Map<ValueNode, ValueEditor.Info> valueEditorInfoMap = new WeakHashMap<ValueNode, ValueEditor.Info>();
    
    /** The clipboard ValueNode. */
    private ValueNode clipboardValueNode = null;

    /** The associated workspace */
    private final CALWorkspace workspace;

    /**
     * Constructor for a ValueEditorManager.
     * @param valueNodeBuilderHelper the valueNodeBuilderHelper
     * @param typeColourProvider the type colour provider
     */
    public ValueEditorManager(ValueNodeBuilderHelper valueNodeBuilderHelper, CALWorkspace workspace, TypeColourProvider typeColourProvider, TypeCheckInfo typeCheckInfo) 
            throws ValueEntryException {

        if (valueNodeBuilderHelper == null || typeColourProvider == null || typeCheckInfo == null) {
            throw new NullPointerException();
        }
        
        this.valueNodeBuilderHelper = valueNodeBuilderHelper;
        this.workspace = workspace;
        this.typeColourProvider = typeColourProvider;
        this.typeCheckInfo = typeCheckInfo;

        initProviders();
        
        // This needs the value node transformer to be initialized..
        this.valueNodeCommitHelper = new ValueNodeCommitHelper(valueNodeBuilderHelper, valueNodeTransformer);
    }

    /**
     * Constructor for a ValueEditorManager.  This constructor can be used to overide the value editor
     * director dictated by the 'registry' file.  This is needed so that clients of the value entry system
     * can explicitly define the director they wish to use.
     * @param valueNodeBuilderHelper the valueNodeBuilderHelper
     * @param typeColourProvider the type colour provider
     * @param valueEditorDirector the specific director that this manager should be using
     */
    public ValueEditorManager(ValueNodeBuilderHelper valueNodeBuilderHelper,
                              CALWorkspace workspace,
                              TypeColourProvider typeColourProvider,
                              TypeCheckInfo typeCheckInfo,
                              ValueEditorDirector valueEditorDirector) throws ValueEntryException {

        this(valueNodeBuilderHelper, workspace, typeColourProvider, typeCheckInfo);
        this.valueEditorDirector = valueEditorDirector;
    }
    
    /**
     * Returns the current valueNodeCommitHelper
     * @return ValueNodeCommitHelper
     */
    public ValueNodeCommitHelper getValueNodeCommitHelper() {
        return valueNodeCommitHelper;
    }
    
    /**
     * Returns the current valueTransformer
     * @return ValueNodeTransformer
     */
    public ValueNodeTransformer getValueNodeTransformer() { 
        return valueNodeTransformer;
    }

    /**
     * Returns an input stream for accessing the value type registry data.
     */
    protected InputStream getValueTypeRegistryData() throws IOException {
        String registryResource = System.getProperty("org.openquark.gems.client.valueentry.defaulttyperegistry", "/valueTypeRegistry.gvr");
        InputStream stream = getClass().getResourceAsStream(registryResource);
        if (stream == null) {
            throw new IOException("System property \"" + "org.openquark.gems.client.valueentry.defaulttyperegistry" + "\" refers to nonexistent resource \"" + registryResource + "\"");
        }
        return stream;
    }

    /**
     * Initialize the value entry and value node providers.
     */
    private void initProviders() throws ValueEntryException {

        BufferedReader reader = null;
        try {
            // Alrighty, time to parse the registry text to get the handlers.
            // Here are the parsing rules:
            // "//" at the beginning of the line denotes comments which will be ignored.
            // Completely Empty lines will be ignored
            // Sections are:
            // "ValueNode:" followed by the list of ValueNode handler classes, then
            // "ValueEditor:" followed by the list of ValueEditor handler classes, then
            // "ValueEditorDirector:" followed by the ValueEditorDirector handler class, then
            // "ValueNodeTransformer:" followed by the ValueNodeTransformer handler class.

            String handlerListFileLoc = System.getProperty("org.openquark.gems.client.valueentry.typeregistry", null);

            if (handlerListFileLoc != null) {
                File preludeFile = new File(handlerListFileLoc);
                try {
                    reader = new BufferedReader(new FileReader(preludeFile));
                    
                } catch (FileNotFoundException e) {
                    throw new IOException("System property \"" + "org.openquark.gems.client.valueentry.typeregistry" + "\" refers to nonexistent file \"" + handlerListFileLoc + "\"");
                }

            } else {
                InputStream is = getValueTypeRegistryData();
                reader = new BufferedReader(TextEncodingUtilities.makeUTF8Reader(is));
            }


            String nextLine = reader.readLine();

            // Ignore comments and completely empty lines.
            while (nextLine != null && (nextLine.equals("") || nextLine.startsWith("//"))) {
                nextLine = reader.readLine();
            }

            // We expect the 'ValueNode:' label here.
            if ("ValueNode:".equals(nextLine)) {
                nextLine = reader.readLine();

            } else {
                throw new Exception("Error in valueTypeRegistry.txt format.  'ValueNode:' label missing.");
            }

            // Flag to indicate if we're in the next section now.
            boolean nextSection = false;

            // While we're in the ValueNode section, we keep taking the fully qualified ValueNode class name,
            // and performing the set-up stuff.
            // Also, if we hit the 'ValueEditor:' label, then we're in the next section.
            while (!nextSection) {

                // Ignore comments and completely empty lines.
                while (nextLine != null && (nextLine.equals("") || nextLine.startsWith("//"))) {
                    nextLine = reader.readLine();
                }

                if (nextLine == null) {
                    throw new Exception("Error in valueTypeRegistry.txt format. 'ValueEditor:' label is missing.");

                } else if ("ValueEditor:".equals(nextLine)) {
                    nextSection = true;

                } else {
                    registerValueNodeProvider(nextLine);
                }

                nextLine = reader.readLine();
            }

            nextSection = false;

            // We have different parameters for the ValueEditor constructors.

            // While we're in the ValueEditor section, we keep taking the fully qualified ValueEditor class name,
            // and performing the set-up stuff.
            // Also, if we hit the 'ValueEditorDirector:' label, then we're in the next section.
            while (!nextSection) {

                // Ignore comments and completely empty lines.
                while (nextLine != null && (nextLine.equals("") || nextLine.startsWith("//"))) {
                    nextLine = reader.readLine();
                }

                if (nextLine == null) {
                    throw new Exception("Error in valueTypeRegistry.txt format. 'ValueEditorDirector:' label is missing.");

                } else if ("ValueEditorDirector:".equals(nextLine)) {
                    nextSection = true;

                } else {
                    registerValueEditorProvider(nextLine);
                    
                }

                nextLine = reader.readLine();
            }

            // Ignore comments and completely empty lines.
            while (nextLine != null && (nextLine.equals("") || nextLine.startsWith("//"))) {
                nextLine = reader.readLine();
            }

            if (nextLine == null || nextLine.equals("ValueNodeTransformer:")) {
                throw new Exception("Error in valueTypeRegistry.txt format.  Expecting the ValueEditorDirector class.");

            } else {
                Class<?> handlerClass = Class.forName(nextLine);
                Constructor<?> constructor = handlerClass.getConstructor(new Class[] {ValueNodeBuilderHelper.class});
                Object newInstance = constructor.newInstance(new Object[]{valueNodeBuilderHelper});
                if (newInstance instanceof ValueEditorDirector) {
                    this.valueEditorDirector = (ValueEditorDirector)newInstance;
                } else {
                    throw new Exception("Error in valueTypeRegistry.txt format.  " + nextLine + " is not an instance of ValueEditorDirector.");
                }

                nextLine = reader.readLine();
            }

            // Ignore comments and completely empty lines.
            while (nextLine != null && (nextLine.equals("") || nextLine.startsWith("//"))) {
                nextLine = reader.readLine();
            }

            if ("ValueNodeTransformer:".equals(nextLine)) {
                nextLine = reader.readLine();

            } else {
                throw new Exception("Error in valueTypeRegistry.txt format.  Expecting the 'ValueNodeTransformer:' label.");
            }

            // Ignore comments and completely empty lines.
            while (nextLine != null && (nextLine.equals("") || nextLine.startsWith("//"))) {
                nextLine = reader.readLine();
            }

            if (nextLine == null) {
                throw new Exception("Error in valueTypeRegistry.txt format.  Expecting the ValueNodeTransformer class.");

            } else {
                Class<?> handlerClass = Class.forName(nextLine);
                Object newInstance = handlerClass.newInstance();
                if (newInstance instanceof ValueNodeTransformer) {
                    this.valueNodeTransformer = (ValueNodeTransformer)newInstance;
                } else {
                    throw new Exception("Error in valueTypeRegistry.txt format.  " + nextLine + " is not an instance of ValueNodeTransformer.");
                }

                nextLine = reader.readLine();
            }

            // Ignore comments and completely empty lines.
            while (nextLine != null) {

                if (!nextLine.equals("") || !nextLine.startsWith("//")) {
                    throw new Exception("Nothing more expected in HandlerList.txt");
                }
                
                nextLine = reader.readLine();
            }

        } catch (Exception e) {
            throw new ValueEntryException("Error in ValueEditor handler initialization.", e);
            
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                }
            }
        }        
    }
    
    /**
     * Registers a new value editor provider and adds it to the list of providers.
     * Does nothing if the given name is not a valid class name.
     * @param className the qualified class name of the editor provider
     */
    private void registerValueEditorProvider(String className) {
 
        try {
            
            Class<?> providerClass = Class.forName(className);
            
            if (!ValueEditorProvider.class.isAssignableFrom(providerClass)) {
                VALUEENTRY_LOGGER.log(Level.WARNING, "Class \"" + providerClass + "\" is not a value editor provider.");
                return;
            }
                        
            Constructor<? extends ValueEditorProvider<?>> providerConstructor = 
                UnsafeCast.unsafeCast(providerClass.getConstructor(new Class[] {ValueEditorManager.class}));    // safety of cast checked above.

            if (providerConstructor != null) {
                
                providerConstructor.setAccessible(true);
                
                ValueEditorProvider<?> editorProvider = providerConstructor.newInstance(new Object[] {this});
                
                valueEditorProviders.add(editorProvider);

            } else {
                VALUEENTRY_LOGGER.log(Level.WARNING, "Could not find value editor provider \"" + className + "\"");
            }

        } catch (Exception e) {
            VALUEENTRY_LOGGER.log(Level.WARNING, "Exception encountered initializing the value editor manager: " + e);
        }
    }
    
    /**
     * Registers a value node provider and adds its class to the provider list.
     * Does nothing if the class name is not a valid class name.
     * @param className the qualified class name of the provider
     */
    private void registerValueNodeProvider(String className) {

        try {
            
            Class<?> providerClass = Class.forName(className);
            
            if (!ValueNodeProvider.class.isAssignableFrom(providerClass)) {
                VALUEENTRY_LOGGER.log(Level.WARNING, "Class \"" + providerClass + "\" is not a value node provider.");
                return;
            }
                        
            Constructor<? extends ValueNodeProvider<?>> providerConstructor = 
                UnsafeCast.unsafeCast(providerClass.getConstructor(new Class[] {ValueNodeBuilderHelper.class}));        // safety of cast checked above.

            if (providerConstructor == null) {
                VALUEENTRY_LOGGER.log(Level.WARNING, "Could not find value node provider \"" + className + "\"");
                return;
            }
            
            valueNodeProviderClasses.add(UnsafeCast.<Class<ValueNodeProvider<?>>>unsafeCast(providerClass));            // safety of cast checked above.

        } catch (Exception e) {
            VALUEENTRY_LOGGER.log(Level.WARNING, "Exception encountered initializing the value editor manager: " + e);
        }
    }

    /**
     * Initialize a ValueNodeBuilderHelper based on info gained from parsing the value type registry.
     * @param valueNodeBuilderHelper
     */
    public void initValueNodeBuilderHelper(ValueNodeBuilderHelper valueNodeBuilderHelper) {
        for (final Class<ValueNodeProvider<?>> clazz : valueNodeProviderClasses) {
            valueNodeBuilderHelper.registerValueNodeProvider(clazz);
        }
    }
    
    /**
     * Get the ValueEditorProvider to handle a given value node.
     * 
     * @param valueNode the value node to find a value editor provider for
     * @param providerSupportInfo the support info object threaded through nested invocations.
     * @param outputEditorsOnly whether we should only return editors that are suitable for output
     * @return the value editor provider for the value node, or null if there is no provider
     */
    private ValueEditorProvider<?> getValueEditorProvider(ValueNode valueNode,
                                                          ValueEditorProvider.SupportInfo providerSupportInfo,
                                                          boolean outputEditorsOnly) {
        
        if (valueNode == null) {
            return null;
        }
        
        // Search in reverse order since most recently registered providers take precedence.
        List<ValueEditorProvider<?>> valueEditorProviders = getValueEditorProviders();
        for (int i = valueEditorProviders.size() - 1; i >= 0; i--) {
            ValueEditorProvider<?> provider = valueEditorProviders.get(i);
            if (provider.canHandleValue(valueNode, providerSupportInfo)) {
                // Enforce that either all editors are allowed, or the editor is suitable for output
                if (!outputEditorsOnly || provider.usableForOutput()) {
                   return provider;
                }
            }
        }
        
        return null;
    }
    
    /**
     * @return Returns a list of all the registered value editor providers
     */
    protected List<ValueEditorProvider<?>> getValueEditorProviders() {
        return valueEditorProviders;
    }
    
    /**
     * Returns the ValueEditorDirector used.
     * @return ValueEditorDirector
     */
    public ValueEditorDirector getValueEditorDirector() {
        return valueEditorDirector;
    }
    
    /**
     * Returns the ValueNodeBuilderHelper used.
     * @return ValueNodeBuilderHelper
     */
    public ValueNodeBuilderHelper getValueNodeBuilderHelper() {
        return valueNodeBuilderHelper;
    }
    
    /**
     * Returns a ValueEditor which can handle the indicated TypeExpr (from valueNode.getTypeExpr()).
     * If no such ValueEditor is found, then returns null.
     * @param valueEditorHierarchyManager
     * @param valueNode 
     * @param onlyIncludeOutputVEPs whether we should return VEPs that are not suitable for output
     * @return ValueEditor
     */
    public ValueEditor getValueEditorForValueNode(ValueEditorHierarchyManager valueEditorHierarchyManager,
                                                  ValueNode valueNode,
                                                  boolean onlyIncludeOutputVEPs) {

        ValueEditorProvider<?> provider = getValueEditorProvider(valueNode, new ValueEditorProvider.SupportInfo(), onlyIncludeOutputVEPs);
        List<ValueEditorProvider<?>> recommendedValueEditorList = new ArrayList<ValueEditorProvider<?>>();

        if (provider != null) {
            recommendedValueEditorList.add(provider);
        }

        return valueEditorDirector.getValueEditor(valueNode, recommendedValueEditorList, valueEditorHierarchyManager);
    }
        
    /**
     * Indicates whether the editor for this type can be launched when
     * viewing output (non-editable).
     */
    public boolean usableForOutput(ValueNode valueNode) {
        return getValueEditorProvider(valueNode, new ValueEditorProvider.SupportInfo(), true) != null;
    }
    
    /**
     * Get the current module.
     * @return MetaModule the current module, or null if there is no current module.
     */
    public MetaModule getCurrentModule() {
        return getPerspective().getWorkingModule();
    }
    
    /**
     * Get the perspective used by this manager.
     * @return Perspective
     */
    public Perspective getPerspective() {
        return valueNodeBuilderHelper.getPerspective();
    }

    /**
     * @return the associated workspace
     */
    public CALWorkspace getWorkspace() {
        return workspace;
    }
    /**
     * @return the type check info used by this manager
     */
    public TypeCheckInfo getTypeCheckInfo() {
        return typeCheckInfo;
    }
    
    /**
     * Returns the resource name of the icon for a particular type.
     * Creation date: (03/07/01 3:33:18 PM)
     * @return String
     * @param typeExpr 
     */
    public String getTypeIconName(TypeExpr typeExpr) {

        TypeConsApp typeConsApp = typeExpr.rootTypeConsApp();

        String iconName = "/Resources/sometype.gif";

        if (typeConsApp != null) {
            
            QualifiedName typeIde = typeConsApp.getName();

            // Note: The bool check should come before the enumerated check.  Since bool can count as an enumerated type.
            if (typeIde.equals(CAL_Prelude.TypeConstructors.Boolean)) {
                iconName = "/Resources/bool.gif";

            } else if (typeIde.equals(CAL_Prelude.TypeConstructors.Double) ||
                       typeIde.equals(CAL_Prelude.TypeConstructors.Float) ||
                       typeIde.equals(CAL_Prelude.TypeConstructors.Decimal)) {
                iconName = "/Resources/float.gif";

            } else if (typeIde.equals(CAL_Prelude.TypeConstructors.Int) ||
                       typeIde.equals(CAL_Prelude.TypeConstructors.Integer) ||
                       typeIde.equals(CAL_Prelude.TypeConstructors.Byte) ||
                       typeIde.equals(CAL_Prelude.TypeConstructors.Short) ||
                       typeIde.equals(CAL_Prelude.TypeConstructors.Long)) {
                iconName = "/Resources/integer.gif";

            } else if (typeIde.equals(CAL_Prelude.TypeConstructors.Char)) {
                iconName = "/Resources/char.gif";

            } else if (typeIde.equals(CAL_RelativeTime.TypeConstructors.RelativeDate)) {
                iconName = "/Resources/date.gif";

            } else if (typeIde.equals(CAL_RelativeTime.TypeConstructors.RelativeTime)) {
                iconName = "/Resources/time.gif";

            } else if (typeIde.equals(CAL_RelativeTime.TypeConstructors.RelativeDateTime) ||
                       typeIde.equals(CAL_Time.TypeConstructors.Time)) {
                iconName = "/Resources/datetime.gif";

            } else if (typeIde.equals(CAL_Color.TypeConstructors.Color)) {
                iconName = "/Resources/colour.gif";

            } else if (typeIde.equals(CAL_File.TypeConstructors.FileName)) {
                iconName = "/Resources/file.gif";

            } else if (typeIde.equals(CAL_Prelude.TypeConstructors.String)) { 
                iconName = "/Resources/string.gif";
                
            } else if (typeIde.equals(CAL_Prelude.TypeConstructors.List)) {
               
                TypeExpr elementTypeExpr = typeConsApp.getArg(0);

                TypeConsApp elementTypeConsApp = elementTypeExpr.rootTypeConsApp();

                if (elementTypeConsApp != null) {

                    QualifiedName elementIde = elementTypeConsApp.getName();

                    if (elementIde.equals(CAL_Prelude.TypeConstructors.Char)) {
                        iconName = "/Resources/string.gif";

                    } else {
                        iconName = "/Resources/list.gif";
                    }

                } else {
                    iconName = "/Resources/list.gif";
                }

            } else if (typeIde.equals(CAL_Prelude.TypeConstructors.Maybe)) {
                iconName = "/Resources/maybetype.gif";

            } else if (typeIde.equals(CAL_Prelude.TypeConstructors.Either)) {
                iconName = "/Resources/eithertype.gif";
           
            } else if (getPerspective().isEnumDataType(typeIde)) {
                iconName = "/Resources/enum.gif";
            
            // TODO:  This is painful to write!  This should be refactored somehow (like having the value
            // nodes return the appropriate icon rather than comparing strings).
            } else if (typeIde.equals(CAL_Range.TypeConstructors.Range)) {
                iconName = "/Resources/range.gif";
            }

        } else if (typeExpr.rootRecordType() != null) {
            
            if (typeExpr.isTupleType()) {
                //a tuple-record
                iconName = "/Resources/tuple.gif";
            } else {
                // A record
                iconName = "/Resources/record.gif";
            }
            
        } else {
            
            // A parametric.
            iconName = "/Resources/notype.gif";
        }

        return iconName;
    }
    
    /**
     * @param typeExpr the expression to get the name for
     * @return the display name of the type expression
     */
    public String getTypeName(TypeExpr typeExpr) {
        ScopedEntityNamingPolicy namingPolicy = new UnqualifiedUnlessAmbiguous(getCurrentModule().getTypeInfo());
        return typeExpr.toString(namingPolicy);
    }
    
    /**
     * Calculates a preferred width for text fields that display the text value of the given value node.
     * @param valueNode the value node
     * @param fontMetrics the font metrics of the text field
     * @param isEditable whether or not the value is editable
     * @return the preferred width in pixels
     */
    public int getValuePreferredWidth(ValueNode valueNode, FontMetrics fontMetrics, boolean isEditable) {

        if (!isEditable) {
            // If we're not editable, then the value can never change.
            // So the baseline is the exact text value.
            int prefWidth = fontMetrics.stringWidth(valueNode.getTextValue()) + 5;
            
            return prefWidth < MAX_PREFERRED_WIDTH ? prefWidth : MAX_PREFERRED_WIDTH;
        }
        
        return getTypePreferredWidth(valueNode.getTypeExpr(), fontMetrics);
    }

    /**
     * Calculates a preferred width for text fields that display a text value of the given type.
     * @param typeExpr the type expression of the value
     * @param fontMetrics the font metrics for the text field
     * @return the preferred width in pixels
     */
    public int getTypePreferredWidth(TypeExpr typeExpr, FontMetrics fontMetrics) {

        String baseline = null;
        int fixedWidth = 0;
                
        
        if (typeExpr instanceof TypeConsApp) {
            
            TypeConsApp typeConsApp = typeExpr.rootTypeConsApp();

            // Estimate a reasonable preferred width for the various types.
                        
            QualifiedName typeConsName = typeConsApp.getName();

            if (typeConsName.equals(CAL_Prelude.TypeConstructors.Double) ||
                typeConsName.equals(CAL_Prelude.TypeConstructors.Float) ||
                typeConsName.equals(CAL_Prelude.TypeConstructors.Byte) ||
                typeConsName.equals(CAL_Prelude.TypeConstructors.Int) ||
                typeConsName.equals(CAL_Prelude.TypeConstructors.Integer) ||
                typeConsName.equals(CAL_Prelude.TypeConstructors.Decimal) ||
                typeConsName.equals(CAL_Prelude.TypeConstructors.Long) ||
                typeConsName.equals(CAL_Prelude.TypeConstructors.Short)) {
                
                baseline = "999.99";

            } else if (typeConsName.equals(CAL_Prelude.TypeConstructors.Char)) {
                baseline = "W";

            } else if (typeConsName.equals(CAL_Prelude.TypeConstructors.String)) {
                baseline = "A reasonably long string value.";
                
            } else if (typeConsName.equals(CAL_RelativeTime.TypeConstructors.RelativeDate)) {
                baseline = "Wednesday, September 30, 1970";
                
            } else if (typeConsName.equals(CAL_RelativeTime.TypeConstructors.RelativeTime)) {
                baseline = "12:00:00 AM";

            } else if (typeConsName.equals(CAL_RelativeTime.TypeConstructors.RelativeDateTime)) {
                baseline = "Wednesday, September 30, 1970 12:00:00 AM";
            
            } else if (typeConsName.equals(CAL_Time.TypeConstructors.Time)) {
                baseline = "Wednesday, September 30, 1970 12:00:00 AM UTC";

            } else if (typeConsName.equals(CAL_Color.TypeConstructors.Color)) {
                fixedWidth = 30;

            } else if (typeConsName.equals(CAL_File.TypeConstructors.FileName)) {
                baseline = "/home/frank/depots/Quark/CAL";
                
            } else if (typeConsName.equals(CAL_Prelude.TypeConstructors.Function)) {
                baseline = "Prelude.FunctionName";

            } else if (typeConsName.equals(CAL_Prelude.TypeConstructors.List)) {

                TypeExpr elementTypeExpr = typeConsApp.getArg(0);
                TypeConsApp elementTypeConsApp = elementTypeExpr.rootTypeConsApp();

                if (elementTypeConsApp != null) {

                    QualifiedName elementTypeConsName = elementTypeConsApp.getName();

                    if (elementTypeConsName.equals(CAL_Prelude.TypeConstructors.Char)) {
                        // This is just a String, so use same width.
                        baseline = "A reasonably long string value.";

                    } else {
                        fixedWidth = getTypePreferredWidth(elementTypeExpr, fontMetrics);
                        
                        // Add space for the [].
                        baseline = "[]";
                    }

                } else {
                    baseline = "[]      ";
                }                            

            } else {
                
                // This must be an algebraic or enumerated type. Use the width required for the longest
                // data constructor name as the preferred width.

                baseline = "()";
                
                DataConstructor[] visibleDataConstructors = getPerspective().getDataConstructorsForType(typeConsName);
                ScopedEntityNamingPolicy namingPolicy = new UnqualifiedUnlessAmbiguous(getPerspective().getWorkingModuleTypeInfo());
                
                if (visibleDataConstructors == null || visibleDataConstructors.length == 0) {

                    // Include non-visible constructors if there are no visible ones.
                    
                    ModuleTypeInfo moduleTypeInfo = getPerspective().getMetaModule(typeConsName.getModuleName()).getTypeInfo();
                    TypeConstructor typeCons = moduleTypeInfo.getTypeConstructor(typeConsName.getUnqualifiedName());
                    
                    for (int n = 0, count = typeCons.getNDataConstructors(); n < count; n++) {
                        
                        DataConstructor dataCons = typeCons.getNthDataConstructor(n);
                        int nameLength = fontMetrics.stringWidth(dataCons.getAdaptedName(namingPolicy));
                        if (nameLength > fixedWidth) {
                            fixedWidth = nameLength;
                        }
                    }
            
                } else {

                    // Only include visible constructors, since the user can never switch to a non-visible
                    // constructor which may have a larger preferred width.
                    
                    for (int i = 0; visibleDataConstructors != null && i < visibleDataConstructors.length; i++) {
    
                        DataConstructor dataCons = visibleDataConstructors[i];
                        
                        int nameLength = fontMetrics.stringWidth(dataCons.getAdaptedName(namingPolicy));
                        if (nameLength > fixedWidth) {
                            fixedWidth = nameLength;
                        }
                    }
                }
            }
            
        } else if (typeExpr instanceof RecordType) {
                   
            //todoBI preferred width is not handled
            //should probably do this for records in general

//            } else if (TypeExpr.getTupleDimension(typeConsName) >= 2) {
//
//                for (int i = 0, nArgs = typeConstructor.getNArgs(); i < nArgs; ++i) {
//                    TypeExpr innerTypeExpr = typeConstructor.getArg(i);
//                    fixedWidth += getTypePreferredWidth(innerTypeExpr, fontMetrics);
//                }
//
//                // Add space for the ().
//                baseline = "()";
        }

        // Catch all for parametric types and anything else that is somehow not handled.
        if (baseline == null) {
            baseline = (fixedWidth != 0) ? "" : "?    ";
        }
        
        // Add 5 pixels for some extra space (if the text field fits exactly it looks weird).
        int preferredWidth = 5 + fixedWidth + fontMetrics.stringWidth(baseline);
        
        // Enforce a sane maximum size.
        if (preferredWidth > MAX_PREFERRED_WIDTH) {
            preferredWidth = MAX_PREFERRED_WIDTH;
        }

        return preferredWidth;
    }

    /**
     * {@inheritDoc}
     * This simply defers to the type colour provider held by this manager.
     */
    public Color getTypeColour(TypeExpr typeExpr) {
        return typeColourProvider.getTypeColour(typeExpr);
    }

    /**
     * Creates a focus cycle where one ValueEditor in listValueEditor will
     * tab to another ValueEditor in listValueEditor (the focus shift will be circular).
     * @param valueEditorList
     */
    public static void createFocusCycle(final List<? extends ValueEditor> valueEditorList) {
        
        FocusTraversalPolicy focusTraversalPolicy = new FocusTraversalPolicy () {
            @Override
            public Component getComponentAfter(
                Container focusCycleRoot,
                Component aComponent) {
                int index = valueEditorList.indexOf(aComponent);
                return valueEditorList.get((index + 1) % valueEditorList.size());
            }

            @Override
            public Component getComponentBefore(
                Container focusCycleRoot,
                Component aComponent) {
                int index = valueEditorList.indexOf(aComponent);
                return valueEditorList.get((index - 1) % valueEditorList.size());
            }

            @Override
            public Component getDefaultComponent(Container focusCycleRoot) {
                return null;
            }

            @Override
            public Component getFirstComponent(Container focusCycleRoot) {
                return null;
            }

            @Override
            public Component getLastComponent(Container focusCycleRoot) {
                return null;
            }
        };
        
        for (int i = 0, listSize = valueEditorList.size(); i < listSize; i++) {
            // If the editor is a value entry panel then include it in the focus cycle.  If it
            // is some other value editor then it won't be included.
            Object editor = valueEditorList.get(i);
            if (editor instanceof ValueEntryPanel) {
                ((ValueEntryPanel)editor).getValueField().setFocusTraversalPolicy(focusTraversalPolicy);
            }
        }
    }

    /**
     * Makes a copy of the ValueNode to the manager's clipboard.
     * @param valueNode the value to copy to the clipboard
     */
    public void copyToClipboard(ValueNode valueNode) {
        clipboardValueNode = valueNodeBuilderHelper.buildValueNode(valueNode.getValue(), null, valueNode.getTypeExpr());
    }

    /**
     * Get (a copy of..) the clipboard value if any.
     * @return ValueNode the clipboard value node, or null if none.
     */
    public ValueNode getClipboardValue() {
        if (clipboardValueNode == null) {
            return null;
        }
        
        TypeExpr newTypeExpr = clipboardValueNode.getTypeExpr().copyTypeExpr();

        // return a copy
        return valueNodeBuilderHelper.buildValueNode(clipboardValueNode.getValue(), null, newTypeExpr);    
    }

    /**
     * Associate a valueNode with some info.
     * @param valueNode
     * @param info
     * @return ValueEditor.Info any previously associated info.
     */
    public ValueEditor.Info associateInfo(ValueNode valueNode, ValueEditor.Info info) {
        return valueEditorInfoMap.put(valueNode, info);
    }
    
    /**
     * Get the info (if any) associated with a value node.
     * @param valueNode
     * @return ValueEditor.Info
     */
    public ValueEditor.Info getInfo(ValueNode valueNode) {
        return valueEditorInfoMap.get(valueNode);
    }
    
    /**
     * Finds all types available for input.
     * Available for input means that the type is handleable by a value editor and also that
     * the type is visible from the current module. 
     * @return a set of all types available for input.
     */
    public Set<TypeExpr> getAvailableInputTypes() {
        
        TypeConstructor[] entities = getPerspective().getTypeConstructors();
        Set<TypeExpr> types = new HashSet<TypeExpr>();
        
        ValueEditorProvider.SupportInfo supportInfo = new ValueEditorProvider.SupportInfo();
        
        for (final TypeConstructor typeCons : entities) {

            TypeConsApp typeConsApp = getValueNodeBuilderHelper().getTypeConstructorForEntity(typeCons);

            if (typeConsApp != null) {
       
                ValueNode valueNode = valueNodeBuilderHelper.getValueNodeForTypeExpr(typeConsApp);
                
                if (getValueEditorProvider(valueNode, supportInfo, false) != null) {
                    types.add(typeConsApp);
                }
            }
        }
        
        // The Record type is special since it has no type constructor entity, but
        // there is a value node / value editor provider to handle it.
        Set<FieldName> fieldNames = new HashSet<FieldName>();
        TypeExpr recordType = TypeExpr.makeFreeRecordType(fieldNames);
        ValueNode valueNode = valueNodeBuilderHelper.getValueNodeForTypeExpr(recordType);
        if (getValueEditorProvider(valueNode, supportInfo, false) != null) {
            types.add(recordType);
        }

        // Prelude.Char is special since it can be directly entered into a VEP, but
        // there is no actual value editor provider for the Char type.
        types.add(valueNodeBuilderHelper.getPreludeTypeConstants().getCharType());
        
        return types;
    }

    /**
     * Check if a default value of the given type is available to be input by a value editor.
     * This checks for default editor providability and input type editability.
     * @param typeToCheck the type to check availability for
     * @return true if a default value for the type can be input by a value editor, false otherwise
     */    
    public boolean canInputDefaultValue(TypeExpr typeToCheck) {
        if (canEditInputType(typeToCheck)) {
            return !(valueNodeBuilderHelper.getValueNodeForTypeExpr(typeToCheck) instanceof ForeignValueNode);
        }
        return false;
    }
    
    /**
     * Check if the given type can be edited by a value editor.
     * This means that the type is handleable by a value editor and also that the type is visible from the current module.
     * @param typeToCheck the type to check availability for
     * @return true if the type can be input by a value editor, false otherwise
     */    
    public boolean canEditInputType(TypeExpr typeToCheck) {
        return canEditInputType(typeToCheck, new ValueEditorProvider.SupportInfo());
    }

    /**
     * Check if the given type can be edited by a value editor.
     * This means that the type is handleable by a value editor and also that the type is visible from the current module.
     * @param typeToCheck the type to check availability for
     * @param supportInfo the support info object threaded through nested invocations.
     * @return true if the type can be input by a value editor, false otherwise
     */    
    public boolean canEditInputType(TypeExpr typeToCheck, ValueEditorProvider.SupportInfo supportInfo) {

        if (typeToCheck == null) {
            return false;
         
        } else if (typeToCheck.sameType(valueNodeBuilderHelper.getPreludeTypeConstants().getCharType())) {
            // Char type is special, since it can be directly entered into a VEP,
            // but does not have an actual value editor.
            return true;
            
        } else if (supportInfo.isSupported(typeToCheck)) {
            return true;
            
        } else {
            // Return whether there is a value editor for default value node of the given type.
            ValueNode valueNode = valueNodeBuilderHelper.getValueNodeForTypeExpr(typeToCheck);
            return valueNode != null && getValueEditorProvider(valueNode, supportInfo, false) != null;
        }
    }
    
    /**
     * Checks if the given value node is supported by a value editor.
     * @param valueNode the value node to check
     * @return true if the value node is supported by a value editor
     */
    public boolean isSupportedValueNode(ValueNode valueNode, ValueEditorProvider.SupportInfo supportInfo) {
        
        if (supportInfo.isSupported(valueNode.getTypeExpr())) {
            return true;
        }
        
        if (valueNode instanceof LiteralValueNode &&
            valueNode.getTypeExpr().sameType(valueNodeBuilderHelper.getPreludeTypeConstants().getCharType())) {

            // Char type is special, since it can be directly entered into a VEP,
            // but does not have an actual value editor.
            return true;
            
        } else {
            return getValueEditorProvider(valueNode, supportInfo, false) != null;
        }
    }
    
    /**
     * Clamps the actual value between the min and max value.
     * @param min the minimum allowed value
     * @param actual the actual value
     * @param max the maximum allowed value
     * @return If actual falls within range, then actual is returned. 
     *         If actual > max, then max is returned.
     *         If actual < min, then min is returned.
     */
    public static int clamp(int min, int actual, int max) {
        return actual < min ? min : actual > max ? max : actual;
    }

    /**
     * Using the type colour and moveable flag this method will construct a border object suitable for
     * a value editor object.  The parameters may be ignored by some implementations
     * @param valueEditor The value editor that we are creating a border for.  Must be non-null
     * and should be initialized before making this call.
     * @return The base value editor manager will return a default etched border for value entry panels
     * or a smooth highlight border that is customized to use the value editor's background colour and
     * moveable state for all other editors.  Note that subclasses can return null to indicate there
     * should be no border.
     */
    public Border getValueEditorBorder(ValueEditor valueEditor) {
        // Value entry panels and pick list value editors always use an etched border
        if (valueEditor instanceof ValueEntryPanel || valueEditor instanceof PickListValueEditor) {
            return BorderFactory.createEtchedBorder();
        } else {
            return new SmoothHighlightBorder(valueEditor.getBackground(), valueEditor.isMoveable());
        }
    }
    
    /**
     * Returns true if the value entry panels should use borders around the type icon and
     * field components.  Returns false if no borders should be used.
     * @return The base value editor manager always returns true.
     */
    public boolean useValueEntryPanelBorders() {
        return true;
    }

    /**
     * Returns true if the value entry panels should show their type icon and false if it should
     * be hidden.
     * @return The base value editor manager always returns true.
     */
    public boolean showValueEntryPanelTypeIcon() {
        return true;
    }

    /**
     * Returns true if the type colour hinting should be used.  In particular, this applies to
     * value editors which may choose to change their background colour and internal borders.
     * @return The base value editor manager always returns true.
     */
    public boolean useTypeColour() {
        return true;
    }
    
    /**     
     * @return handy TypeExpr constants for common Prelude types.
     */    
    public PreludeTypeConstants getPreludeTypeConstants() {
        return valueNodeBuilderHelper.getPreludeTypeConstants();
    }
    
    /**
     * @param typeExpr the type expression to check
     * @return whether values of the type could be edited directly by editing the text in a text field.
     */
    public boolean isTextEditable(TypeExpr typeExpr) {
        PreludeTypeConstants typeConstants = getPreludeTypeConstants();
        
        return typeExpr.sameType(typeConstants.getCharType()) ||
            typeExpr.sameType(typeConstants.getByteType()) ||
            typeExpr.sameType(typeConstants.getShortType()) ||
            typeExpr.sameType(typeConstants.getIntType()) ||
            typeExpr.sameType(typeConstants.getIntegerType()) ||
            typeExpr.sameType(typeConstants.getDecimalType()) ||
            typeExpr.sameType(typeConstants.getLongType()) ||
            typeExpr.sameType(typeConstants.getDoubleType()) ||
            typeExpr.sameType(typeConstants.getStringType()) ||
            typeExpr.sameType(typeConstants.getCharListType());
    }
    
    /**
     * @param typeExpr the type expression to check
     * @return whether values of the type could be edited directly from a text field. This is more
     * general than text editability, since it could just mean the up/down arrow keys can change the
     * value, as opposed to freeform typing of a value.
     */
    public boolean isFieldEditable(TypeExpr typeExpr) {

        TypeConsApp typeConsApp = typeExpr.rootTypeConsApp();
        
        return isTextEditable(typeExpr) ||            
        typeExpr.isNonParametricType(CAL_RelativeTime.TypeConstructors.RelativeDate) ||
        typeExpr.isNonParametricType(CAL_RelativeTime.TypeConstructors.RelativeTime) ||
        typeExpr.isNonParametricType(CAL_RelativeTime.TypeConstructors.RelativeDateTime) ||
        typeConsApp != null && getPerspective().isEnumDataType(typeConsApp.getName());
    }
}
