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
 * SourceModel_Test.java
 * Creation date: Mar 2, 2005.
 * By: Joseph Wong
 */
package org.openquark.cal.compiler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.extensions.TestSetup;
import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openquark.cal.runtime.MachineType;
import org.openquark.cal.services.BasicCALServices;
import org.openquark.cal.services.CALServicesTestUtilities;
import org.openquark.cal.services.Status;


/**
 * A set of JUnit test cases for verifying the correctness of the source model.
 *
 * @author Joseph Wong
 */
public class SourceModel_Test extends TestCase {
    /**
     * A copy of CAL services for use in the test cases.
     */
    private static BasicCALServices leccCALServices;

    /**
     * Set this flag to true if debugging output is desired regardless of
     * whether a test fails or succeeds.
     */
    private static final boolean SHOW_DEBUGGING_OUTPUT = false;
    
    /**
     * @return a test suite containing all the test cases for testing CAL source
     *         generation.
     */
    public static Test suite() {

        TestSuite suite = new TestSuite(SourceModel_Test.class);

        return new TestSetup(suite) {

            @Override
            protected void setUp() {
                oneTimeSetUp();
                
            }
    
            @Override
            protected void tearDown() {
                oneTimeTearDown();
            }
        };
    }
    
    /**
     * Performs the setup for the test suite.
     */
    private static void oneTimeSetUp() {
        leccCALServices = CALServicesTestUtilities.getCommonCALServices(MachineType.LECC, "cal.platform.test.cws");
    }
    
    /**
     * Performs the tear down for the test suite.
     */
    private static void oneTimeTearDown() {
        leccCALServices = null;
        ReflectiveFieldsFinder.INSTANCE.cleanup();
    }
    
    /**
     * Constructor for SourceModel_Test.
     * 
     * @param name
     *            the name of the test
     */
    public SourceModel_Test(String name) {
        super(name);
    }
    
    /**
     * REGRESSION TEST case for the SourceModel/SourceModelBuilder correctly constructing a model
     * which contains a representation of a friend declaration.
     */
    public void testSourceModelFriendDecl() {
        String src = "module Foo; import Prelude; friend FooFriend;";
        
        SourceModel.ModuleDefn sourceModel = SourceModelUtilities.TextParsing.parseModuleDefnIntoSourceModel(src);
        
        assertEquals(sourceModel.getNFriendModules(), 1);
        
        SourceModel.Friend friend = sourceModel.getNthFriendModule(0);
        assertEquals(ModuleName.make("FooFriend"), SourceModel.Name.Module.toModuleName(friend.getFriendModuleName()));
        
        assertTrue("The source model should have modeled the 'friend FooFriend;' declaration", getSourceText(sourceModel).indexOf("friend FooFriend;") > 0);
        
        ParseTreeNode friendParseTreeNode = friend.toParseTreeNode();
        assertEquals(friendParseTreeNode.getType(), CALTreeParserTokenTypes.LITERAL_friend);
        
        assertEquals(friendParseTreeNode.getNumberOfChildren(), 1);
        
        assertEquals(friendParseTreeNode.firstChild().getType(), CALTreeParserTokenTypes.HIERARCHICAL_MODULE_NAME);
    }
    
    public void testSourceModelTraversalWithImportAugmenter() {
        ModuleSourceDefinitionGroup moduleDefnGroup = CALServicesTestUtilities.getModuleSourceDefinitionGroup(leccCALServices);
        int nModules = moduleDefnGroup.getNModules();
        
        for (int i = 0; i < nModules; i++) {
            
            // For each module source definition, obtain its definition in CAL text form
            // by reading its contents into a string
            
            ModuleSourceDefinition moduleSource = moduleDefnGroup.getModuleSource(i);
            
            Status status = new Status("Proccessing module " + moduleSource.getModuleName());
            Reader reader = moduleSource.getSourceReader(status);
            
            assertTrue("Module " + moduleSource.getModuleName() + " failed to provide a reader", status.isOK());
            
            BufferedReader bufferedReader = new BufferedReader(reader);
            StringBuilder stringBuf = new StringBuilder();
            
            String line = null;
            try {
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuf.append(line).append('\n');
                }
            } catch (IOException e) {
                fail("Failed to read module " + moduleSource.getModuleName());
            }
            
            // Step 1: Parse the CAL text for the source of the module
            //         source text -> parse tree
            ParseTreeNode parseTreeNode = CompilerTestUtilities.parseModuleDefnIntoParseTreeNode(stringBuf.toString());

            // If the parse was successful, the parseTreeNode will be non-null and be the root
            // of the parse tree. Otherwise, the module is broken, and is not suitable for the
            // purpose of testing the source model.
            if (parseTreeNode != null) {
                
                // Step 2: parse tree -> source model
                SourceModel.ModuleDefn sourceModel = SourceModelBuilder.buildModuleDefn(parseTreeNode);
                
                // Step 3: obtain a new source model augmented with all the required imports
                SourceModel.ModuleDefn newSourceModel = SourceModelUtilities.ImportAugmenter.augmentWithImports(sourceModel);
                
                // Since these modules are expected to compile without problems, they should have
                // all the imports that are required, and hence the new source model should be identical
                // to that of the original one.
                assertEquals(getSourceText(sourceModel), getSourceText(newSourceModel));
            }
        }
    }

    /**
     * A reflection-based test case for verifying that the SourceModelCopier copies every single SourceElement
     * in a source model, and leaves nothing uncopied.
     */
    public void testSourceModelCopier() {
        ModuleSourceDefinitionGroup moduleDefnGroup = CALServicesTestUtilities.getModuleSourceDefinitionGroup(leccCALServices);
        int nModules = moduleDefnGroup.getNModules();
        
        for (int i = 0; i < nModules; i++) {
            
            // For each module source definition, obtain its definition in CAL text form
            // by reading its contents into a string
            
            ModuleSourceDefinition moduleSource = moduleDefnGroup.getModuleSource(i);
            
            Status status = new Status("Proccessing module " + moduleSource.getModuleName());
            Reader reader = moduleSource.getSourceReader(status);
            
            assertTrue("Module " + moduleSource.getModuleName() + " failed to provide a reader", status.isOK());
            
            BufferedReader bufferedReader = new BufferedReader(reader);
            StringBuilder stringBuf = new StringBuilder();
            
            String line = null;
            try {
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuf.append(line).append('\n');
                }
            } catch (IOException e) {
                fail("Failed to read module " + moduleSource.getModuleName());
            }
            
            // Step 1: Parse the CAL text for the source of the module
            //         source text -> parse tree
            ParseTreeNode parseTreeNode = CompilerTestUtilities.parseModuleDefnIntoParseTreeNode(stringBuf.toString());

            // If the parse was successful, the parseTreeNode will be non-null and be the root
            // of the parse tree. Otherwise, the module is broken, and is not suitable for the
            // purpose of testing the source model.
            if (parseTreeNode != null) {
                
                // Step 2: parse tree -> source model
                SourceModel.ModuleDefn sourceModel = SourceModelBuilder.buildModuleDefn(parseTreeNode);
                
                // Step 3: get new source model with copier
                SourceModel.ModuleDefn newSourceModel = (SourceModel.ModuleDefn)sourceModel.accept(new SourceModelCopier<Void>(), null);
                
                // Step 4: compare for structural equality
                ReflectiveSourceModelStructuralComparer comparer = new ReflectiveSourceModelStructuralComparer();
                
                assertTrue("Structural comparison failed for module " + moduleSource.getModuleName() + " in testSourceModelCopier", comparer.areStructurallyEqual(sourceModel, newSourceModel));
            }
        }
    }
    
    /**
     * A dynamic test of the source model, getting the source of each CAL module
     * in the workspace. The source text is first converted to source model
     * form. Then the source model is copied via the SourceModelCopier. The
     * resulting source model is converted back to source text, and another
     * source model is constructed from this generated source text. The
     * assertion is that these source models are identical to one another.
     * 
     * We want to test the functionality:
     * SourceModelBuilder.buildModuleDefn :: ParseTreeNode -> SourceModel    
     * SourceModel.toParseTreeNode :: SourceModel -> ParseTreeNode
     * SourceModelCopier.visit<source model element> :: SourceModel -> SourceModel
     * SourceModel.toSourceText :: SourceModel -> String
     * 
     * on the CAL modules defined in the CAL workspace.
     * 
     * This is done by taking independent paths to a textual representation and verifying
     * that they are equal.     
     */
    public void testRoundTrip_ModuleDefnsFromWorkspace() {

        ModuleSourceDefinitionGroup moduleDefnGroup = CALServicesTestUtilities.getModuleSourceDefinitionGroup(leccCALServices);
        int nModules = moduleDefnGroup.getNModules();
        
        for (int i = 0; i < nModules; i++) {
            
            // For each module source definition, obtain its definition in CAL text form
            // by reading its contents into a string
            
            ModuleSourceDefinition moduleSource = moduleDefnGroup.getModuleSource(i);
        
            if (SHOW_DEBUGGING_OUTPUT) {
                System.out.println("Testing module: " + moduleSource.getModuleName().toString());
            }
            
            Status status = new Status("Proccessing module " + moduleSource.getModuleName());
            Reader reader = moduleSource.getSourceReader(status);
            
            assertTrue("Module " + moduleSource.getModuleName() + " failed to provide a reader", status.isOK());
            
            BufferedReader bufferedReader = new BufferedReader(reader);
            StringBuilder stringBuf = new StringBuilder();
            
            String line = null;
            try {
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuf.append(line).append('\n');
                }
            } catch (IOException e) {
                fail("Failed to read module " + moduleSource.getModuleName());
            }
            
            String origSource = stringBuf.toString();
            
            // Step 1: Parse the CAL text for the source of the module
            //         source text (1) -> parse tree (1)
            List<SourceEmbellishment> emebellishments = new ArrayList<SourceEmbellishment>();
            ParseTreeNode parseTreeNode = CompilerTestUtilities.parseModuleDefnIntoParseTreeNode(stringBuf.toString(), emebellishments);

            // If the parse was successful, the parseTreeNode will be non-null and be the root
            // of the parse tree. Otherwise, the module is broken, and is not suitable for the
            // purpose of testing the source model.
            if (parseTreeNode != null) {
                
                // Step 2: parse tree (1) -> source model (1)
                SourceModel.ModuleDefn sourceModel = SourceModelBuilder.buildModuleDefn(parseTreeNode);
                
                //** CHECK 1: SourceModelBuilder.buildModuleDefn :: ParseTreeNode -> SourceModel
                //   - SourceModelBuilder.buildModuleDefn() should return a non-null value
                assertNotNull(sourceModel);
                
                // Step 2.5: source model (1) -> source text (2)
                String sourceModelToSourceText = getSourceText(sourceModel);
                
                //** CHECK 2: SourceModel.toSourceText :: SourceModel -> String 
                //   - SourceModel.toSourceText() should return a non-empty string
                assertTrue(sourceModelToSourceText.length() > 0);
                
                // Step 3: source model (1) -> source model (2) via the SourceModelCopier
                SourceModel.ModuleDefn sourceModelCopy = (SourceModel.ModuleDefn)sourceModel.accept(new SourceModelCopier<Void>(), null);
                
                //** CHECK 3: SourceModelCopier.visit<source model element> :: SourceModel -> SourceModel
                //   - the original source model and the copy should be identical
                //   - the check is done via comparing their generated source texts
                assertEquals(sourceModelToSourceText, getSourceText(sourceModelCopy));
                
                // Step 4: source model (1) -> parse tree (2)
                ParseTreeNode newParseTreeNode = sourceModel.toParseTreeNode();
                
                // Step 5: parse tree (2) -> source model (3)
                SourceModel.ModuleDefn sourceModelFromNewParseTree = SourceModelBuilder.buildModuleDefn(newParseTreeNode);
                
                //** CHECK 4: SourceModel.toParseTreeNode :: SourceModel -> ParseTreeNode
                //   - the original source model and the one that is obtained via a round-trip of
                //     SourceModel->ParseTreeNode->SourceModel should be identical
                //   - the check is done via comparing their generated source texts
                assertEquals(sourceModelToSourceText, getSourceText(sourceModelFromNewParseTree));
                
                // Step 4: source text (2) -> parse tree (3)
                ParseTreeNode newParseTreeNodeFromGeneratedText = CompilerTestUtilities.parseModuleDefnIntoParseTreeNode(sourceModelToSourceText);
                
                // Step 5: parse tree (3) -> source model (4)
                SourceModel.ModuleDefn sourceModelFromParseTreeFromGeneratedText = SourceModelBuilder.buildModuleDefn(newParseTreeNodeFromGeneratedText);

                //TODO-MB The following tests have been disabled - there
                //are some errors in the formatter which breaks its idempotent property,
                //Also some wrapped words can be 'jiggled' around when the 
                //source model description of CalDoc changes, which 
                //affects the running of these tests.
                
                //** CHECK 5: SourceModel.toSourceText :: SourceModel -> String
                //   - SourceModel.toSourceText() should generate parsable text
                //   - the original source model and the one that is obtained via a round-trip of
                //     SourceModel->text->ParseTreeNode->SourceModel should be identical
                //   - the check is done via comparing their generated source texts
                assertEquals(sourceModelToSourceText, getSourceText(sourceModelFromParseTreeFromGeneratedText));
                
                if (sourceModelToSourceText.compareTo(getSourceText(sourceModelFromParseTreeFromGeneratedText)) != 0) {
                    System.err.println("Broken");
                }

                /// Step 6: check formatting with comments
                //here we check that the formatted module can be parsed, and that the
                //toString of the parsed formatted module is correct 
                String sourceModelToSourceTextWithComments = getSourceText(sourceModel, emebellishments);
                ParseTreeNode newParseTreeNodeFromGeneratedTextWithComments = CompilerTestUtilities.parseModuleDefnIntoParseTreeNode(sourceModelToSourceTextWithComments);
                SourceModel.ModuleDefn sourceModelFromParseTreeFromGeneratedTextWithComments = SourceModelBuilder.buildModuleDefn(newParseTreeNodeFromGeneratedTextWithComments);               
                assertEquals( getSourceText(sourceModelFromParseTreeFromGeneratedTextWithComments), sourceModelToSourceText);
                 
                //step 7: check that pretty printing is idempotent 
                //reformat the formatted text and check that is unchanged
                assertEquals(reformat(sourceModelToSourceTextWithComments), sourceModelToSourceTextWithComments);
                   
                //step 8: check that the reformatted code has the same number of parens
                //as the original source.
                if (countParen(sourceModelToSourceTextWithComments) != countParen(origSource)) {
                    int str1 = 1;
                    str1++;
                }
                assertEquals(countParen(sourceModelToSourceTextWithComments),countParen(origSource));
                
                if (SHOW_DEBUGGING_OUTPUT) {
                    System.out.println("////// Source of " + moduleSource.getModuleName());
                    System.out.println(sourceModelToSourceText);
                }
            }
      
        }
    }
    
    /**
     * count the number of opening paren in a string.
     * @param s1
     * @return the number of opening paren
     */
    private int countParen(String s1) {
        int i=0, numParen=0;
        
        for(i=0; i<s1.length(); i++) {
            if (s1.charAt(i) == '(') {
                numParen++;
            }
        }
        
        return numParen;
    }
    
    
    /**
     * this helper reformats a source module string, including comments
     * @param input
     * @return formatted source
     */
    private String reformat(String input) {
        List<SourceEmbellishment> emebellishments = new ArrayList<SourceEmbellishment>();
        ParseTreeNode parseTreeNode = CompilerTestUtilities.parseModuleDefnIntoParseTreeNode(input, emebellishments);

        // Step 5: parse tree (2) -> source model (3)
        SourceModel.ModuleDefn sourceModel = SourceModelBuilder.buildModuleDefn(parseTreeNode);

        return getSourceText(sourceModel, emebellishments);
        
    }
    
    /**
     * Tests that each concrete subclass of SourceElement has a public static make* factory method.
     * The classes which are exempt from this check are listed in the variable <code>exemptClasses</code>.
     */
    public void testPublicFactoryMethods() {
        final Set<Class<?>> concreteSourceElementSubclasses = getConcreteSourceElementSubclasses(SourceModel.class);
        
        final Class<?>[] exemptClasses = new Class<?>[] { SourceModel.FunctionDefn.Primitive.class }; 
        
        final List<Class<?>> classesWithoutPublicFactoryMethods = new ArrayList<Class<?>>();
        
        for (final Class<?> concreteSubclass : concreteSourceElementSubclasses) {
            final Method[] methods = concreteSubclass.getMethods();
            
            boolean foundPublicStaticMake = false;
            for (final Method method : methods) {
                final int modifiers = method.getModifiers();
                if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers)) {
                    if (method.getName().startsWith("make")) {
                        foundPublicStaticMake = true;
                        break;
                    }
                }
            }
            
            if (!foundPublicStaticMake) {
                classesWithoutPublicFactoryMethods.add(concreteSubclass);
            }
        }
        
        classesWithoutPublicFactoryMethods.removeAll(Arrays.asList(exemptClasses));
        
        assertEquals(Collections.EMPTY_LIST, classesWithoutPublicFactoryMethods);
    }
    
    /**
     * Retrieves all the concrete subclasses of SourceElement that appear as inner classes of the given class.
     * @param rootClass the root of the containment hierarchy to search.
     * @return a Set of Class objects.
     */
    private Set<Class<?>> getConcreteSourceElementSubclasses(final Class<?> rootClass) {
        
        final Set<Class<?>> result = new HashSet<Class<?>>();
        
        final Class<?>[] innerClasses = rootClass.getDeclaredClasses();
        for (final Class<?> innerClass : innerClasses) {
            final int modifiers = innerClass.getModifiers();
            
            if (Modifier.isPublic(modifiers) && !Modifier.isAbstract(modifiers)) {
                if (SourceModel.SourceElement.class.isAssignableFrom(innerClass)) {
                    result.add(innerClass);
                }
            }
            
            result.addAll(getConcreteSourceElementSubclasses(innerClass));
        }
        
        return result;
    }
    
    /**
     * Converts a source model element into source text without embellishments
     * @param sourceElement
     * @return the corresponding source text.
     */
    private static String getSourceText(final SourceModel.SourceElement sourceElement) {
        return SourceModelCodeFormatter.formatCode(sourceElement, SourceModelCodeFormatter.DEFAULT_OPTIONS, Collections.EMPTY_LIST);
    }
    
    /**
     * Converts a source model element into source text with embellishments
     * @param sourceElement
     * @return the corresponding source text.
     */
    private static String getSourceText(final SourceModel.SourceElement sourceElement, List<SourceEmbellishment> embellishments) {
        return SourceModelCodeFormatter.formatCode(sourceElement, SourceModelCodeFormatter.DEFAULT_OPTIONS, embellishments);
    }
    
    /**
     * Asserts that two strings are equal, and print out both strings in their entirety
     * when they are not equal.
     */
    public static void assertEquals(String a, String b) {
        Assert.assertEquals("Strings differ\narg1:\n" + a + "\narg2:\n" + b, a, b);
    }
    
    /**
     * A utility class for finding all non static fields (even protected and private ones)
     * in a class and all its superclasses.
     *
     * @author Joseph Wong
     */
    static final class ReflectiveFieldsFinder {
        
        /** Memoized results from previous runs. */
        private final Map<Class<?>, Field[]> classToFields = new HashMap<Class<?>, Field[]>();
        
        /**
         * A map mapping a Field to its original accessibility flag, for use
         * during cleanup when the accessibility flags should be reset back
         * to their original values.
         */ 
        private final Map<Field, Boolean> fieldAccessibility = new HashMap<Field, Boolean>();
        
        /** The singleton instance. */
        static final ReflectiveFieldsFinder INSTANCE = new ReflectiveFieldsFinder();
        
        /** Private constructor. */
        private ReflectiveFieldsFinder() {}
        
        /**
         * Gets an array of the non-static fields of the given class and all its superclasses.
         * @param cls the Class to query.
         * @return all the non-static fields (even private and protected ones) of the class and its superclasses.
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
         * Cleans up by reseting the accessibility flags modified during invocations to getAllNonStaticFields().
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
     * A utility for comparing two SourceElements using structural equivalence.
     *
     * @author Joseph Wong
     */
    static final class ReflectiveSourceModelStructuralComparer {
        
        /**
         * @return true if the two SourceElements are structually equal.
         */
        boolean areStructurallyEqual(SourceModel.SourceElement a, SourceModel.SourceElement b) {
            if (a == null || b == null) {
                return a == null && b == null;
            }
            
            Class<? extends SourceModel.SourceElement> aClass = a.getClass();
            Class<? extends SourceModel.SourceElement> bClass = b.getClass();
            
            if (!aClass.equals(bClass)) {
                fail("Not equal:\n" + a + "\nand:\n" + b);
                return false;
            }
            
            Field[] fields = ReflectiveFieldsFinder.INSTANCE.getAllNonStaticFields(aClass);
            
            for (final Field field : fields) {
                try {
                    Object aFieldValue = field.get(a);
                    Object bFieldValue = field.get(b);
                    
                    if (!areFieldValuesStructurallyEqual(aFieldValue, bFieldValue)) {
                        fail("Not equal:\n" + aFieldValue + "\nand:\n" + bFieldValue);
                        return false;
                    }
                } catch (Exception e) {
                    fail("Exception thrown: " + e);
                }
            }
            
            return true;
        }
        
        /**
         * @return true if the two Objects are structually equal, in that if they are SourceElements, then
         * they are compared via areStructurallyEqual(). Otherwise, regular equals() is employed. (For classes
         * which overrides toString() but not equals(), the string representations are compared as a last resort).
         */
        <T> boolean areFieldValuesStructurallyEqual(T a, T b) {
            // handle null case
            if (a == null || b == null) {
                if (a == null && b == null) {
                    return true;
                } else {
                    final Class<?> cls = (a == null) ? b.getClass() : a.getClass();
                    fail("Not equal (" + cls + "):\n" + a + "\nand:\n" + b);
                    return false;
                }
            }
            
            // handle case when both are SourceElements
            if (a instanceof SourceModel.SourceElement && b instanceof SourceModel.SourceElement) {
                return areStructurallyEqual((SourceModel.SourceElement)a, (SourceModel.SourceElement)b);
            }
            
            // handle case when both are arrays of objects
            if (a instanceof Object[] && b instanceof Object[]) {
                Object[] aArray = (Object[])a;
                Object[] bArray = (Object[])b;
                if (aArray.length != bArray.length) {
                    fail("Not equal:\n" + Arrays.asList(aArray) + "\nand:\n" + Arrays.asList(bArray));
                    return false;
                }
                
                for (int j = 0; j < aArray.length; j++) {
                    if (!areFieldValuesStructurallyEqual(aArray[j], bArray[j])) {
                        fail("Not equal:\n" + aArray[j] + "\nand:\n" + bArray[j]);
                        return false;
                    }
                }
                return true;
            }
            
            // a and/or b may be a primitive, a primitive array, or an object outside the SourceElement hierarchy  
            boolean areEqual = false;
            
            if (a instanceof boolean[] && b instanceof boolean[]) {
                areEqual = Arrays.equals((boolean[])a, (boolean[])b);
                
            } else if (a instanceof byte[] && b instanceof byte[]) {
                areEqual = Arrays.equals((byte[])a, (byte[])b);
                
            } else if (a instanceof char[] && b instanceof char[]) {
                areEqual = Arrays.equals((char[])a, (char[])b);
                
            } else if (a instanceof double[] && b instanceof double[]) {
                areEqual = Arrays.equals((double[])a, (double[])b);
                
            } else if (a instanceof float[] && b instanceof float[]) {
                areEqual = Arrays.equals((float[])a, (float[])b);
                
            } else if (a instanceof int[] && b instanceof int[]) {
                areEqual = Arrays.equals((int[])a, (int[])b);
                
            } else if (a instanceof long[] && b instanceof long[]) {
                areEqual = Arrays.equals((long[])a, (long[])b);
                
            } else if (a instanceof short[] && b instanceof short[]) {
                areEqual = Arrays.equals((short[])a, (short[])b);
                
            } else {
                // default to standard equals(), or string comparison if equals() still refers to Object.equals()
                areEqual = a.equals(b) || a.toString().equals(b.toString());
            }
            
            if (!areEqual) {
                fail("Not equal:\n" + a + "\nand:\n" + b);
            }
            
            return areEqual;
        }
    }
}
