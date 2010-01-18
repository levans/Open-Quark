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
 * TypeClassChecker.java
 * Creation date (Aug 26, 2002).
 * By: Bo Ilic
 */
package org.openquark.cal.compiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.openquark.cal.util.Graph;
import org.openquark.cal.util.VertexBuilder;
import org.openquark.cal.util.VertexBuilderList;

/**
 * A class used to check that type class definitions within a module are valid, and to then
 * add them to the the environment.
 * 
 * <p>
 * Creation date (Aug 26, 2002).
 * @author Bo Ilic
 */
final class TypeClassChecker {

    /** Set to true to have debug info printed while running the type class checker. */
    private static final boolean DEBUG_INFO = false;

    private final CALCompiler compiler;

    /** Type classes for the current module are added here. */
    private final ModuleTypeInfo currentModuleTypeInfo;
                   
    /**
     * Method TypeClassChecker.
     * @param currentModuleTypeInfo
     * @param compiler
     */
    TypeClassChecker(ModuleTypeInfo currentModuleTypeInfo, CALCompiler compiler) {
        
        if (currentModuleTypeInfo == null || compiler == null) {
            throw new NullPointerException();
        }
                         
        this.currentModuleTypeInfo = currentModuleTypeInfo;
        this.compiler = compiler;         
    }
    
    /**
     * Method checkTypeClassDefinitions.
     * Does static analysis on the type classes defined within the current module, and adds a
     * TypeClass object for each of them to the ModuleTypeInfo for the current module.
     * Note that class methods are not checked here.
     * @param typeClassDefnNodes
     */
    void checkTypeClassDefinitions(List<ParseTreeNode> typeClassDefnNodes) {
                                              
        checkNamesUsed(typeClassDefnNodes);            
        checkClassDependencies(typeClassDefnNodes);
    }
        
    /**
     * This method does an initial pass over the type classes and checks that each type class is 
     * defined at most once, and that each class method is defined at most once.
     * 
     * @param typeClassDefnNodes
     */
    private void checkNamesUsed(List<ParseTreeNode> typeClassDefnNodes) {
                                                       
        //Haskell also has the restriction that type class names and type constructor names must not be the same
        //within the same scope e.g. the Prelude module can't have a type class named Int because it has the type Int.
        //This is necessary in Haskell because of the syntax for importing and exporting symbols from modules,
        //but is not a necessary restriction for CAL.
                                             
        //the names of all the classes defined in this module                                     
        Set<String> classNamesSet = new HashSet<String>();                                     
                                             
        //the names of all the class methods defined in this module
        Set<String> classMethodNamesSet = new HashSet<String>();                
                    
        final ModuleName currentModule = currentModuleTypeInfo.getModuleName();             

        for (final ParseTreeNode classNode : typeClassDefnNodes) {
                             
            classNode.verifyType(CALTreeParserTokenTypes.TYPE_CLASS_DEFN);   
                                           
            ParseTreeNode optionalDocComment = classNode.firstChild();
            ParseTreeNode classScopeNode = optionalDocComment.nextSibling();
            Scope classScope = CALTypeChecker.getScopeModifier(classScopeNode);
            
            ParseTreeNode contextListNode = classScopeNode.nextSibling();
            contextListNode.verifyType(CALTreeParserTokenTypes.CLASS_CONTEXT_LIST, CALTreeParserTokenTypes.CLASS_CONTEXT_SINGLETON, CALTreeParserTokenTypes.CLASS_CONTEXT_NOTHING);
                                           
            ParseTreeNode classNameNode = contextListNode.nextSibling();
            classNameNode.verifyType(CALTreeParserTokenTypes.CONS_ID);              
            String className = classNameNode.getText();
                           
            if (!classNamesSet.add(className)) {
                 //class name is defined more than once
                compiler.logMessage(new CompilerMessage(classNameNode, new MessageKind.Error.RepeatedDefinitionOfClass(className)));
            }
            
            final ParseTreeNode typeClassTypeVarNameNode = classNameNode.nextSibling();
            typeClassTypeVarNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID);
            final String typeClassTypeVarName = typeClassTypeVarNameNode.getText();
            
            ModuleName usingModuleName = currentModuleTypeInfo.getModuleOfUsingTypeClass(className); 
            if (usingModuleName != null) {
                //there is already an 
                //import 'usingModuleName' using typeClass = 'className';
                compiler.logMessage(new CompilerMessage(classNameNode, new MessageKind.Error.TypeClassNameAlreadyUsedInImportUsingDeclaration(classNameNode.toString(), usingModuleName)));
            }                 
            
            TypeClass typeClass = new TypeClass (QualifiedName.make(currentModule, className), classScope, new KindExpr.KindVar(), typeClassTypeVarName);
            currentModuleTypeInfo.addTypeClass(typeClass);              
                                                                                           
            ParseTreeNode classMethodListNode = classNameNode.nextSibling().nextSibling();
            classMethodListNode.verifyType(CALTreeParserTokenTypes.CLASS_METHOD_LIST);
            
            for (final ParseTreeNode classMethodNode : classMethodListNode) {
                    
                 classMethodNode.verifyType(CALTreeParserTokenTypes.CLASS_METHOD);   
                                 
                 ParseTreeNode classMethodNameNode = classMethodNode.getChild(2);                     
                 classMethodNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID);
                 String classMethodName = classMethodNameNode.getText();                     
                                 
                 if (compiler.getTypeChecker().isTopLevelFunctionName(classMethodName)) {
                     // The name {classMethodName} is already used as a function name in this module.
                     compiler.logMessage (new CompilerMessage(classMethodNode, new MessageKind.Error.NameAlreadyUsedAsFunctionName(classMethodName)));
                 }
                 
                 if(!classMethodNamesSet.add(classMethodName)) {
                     //the class method is defined more than once
                     compiler.logMessage(new CompilerMessage(classMethodNameNode, new MessageKind.Error.RepeatedDefinitionOfClassMethod(classMethodName)));
                 }
                 
                 ModuleName usingModuleNameForClassMethod = currentModuleTypeInfo.getModuleOfUsingFunctionOrClassMethod(classMethodName); 
                 if (usingModuleNameForClassMethod != null) {
                     //there is already an 
                     //import 'usingModuleNameForClassMethod' using function = 'classMethodName';
                     compiler.logMessage(new CompilerMessage(classMethodNameNode, new MessageKind.Error.ClassMethodNameAlreadyUsedInImportUsingDeclaration(classMethodName, usingModuleNameForClassMethod)));
                 }                          
            }                                                                                            
            
        }       
    } 
        
    /**
     * Checks that dependencies between type classes are valid. 
     * In particular, in the course of this method we check:
     * <ol>
     *   <li> that all the type classes referred to in the class contexts are resolvable
     *   <li> that all the type variables in the class context and class are the same i.e. (Eq a => Ord a, the a's are the same)
     *   <li> that the inheritance graph of type classes is acyclic
     * </ol>
     * <p>
     * Once this check is passed, the only thing that can fail in type checking the type classes is information
     * derived from the class methods.
     * 
     * @param typeClassDefnNodes
     */
    private void checkClassDependencies(List<ParseTreeNode> typeClassDefnNodes) {
                                                   
        VertexBuilderList<String> vertexBuilderList = new VertexBuilderList<String>();
                      
        for (final ParseTreeNode classDefnNode : typeClassDefnNodes) {
                            
            classDefnNode.verifyType(CALTreeParserTokenTypes.TYPE_CLASS_DEFN);
               
            ParseTreeNode contextListNode = classDefnNode.getChild(2);
            
            ParseTreeNode classNameNode = contextListNode.nextSibling();
            classNameNode.verifyType(CALTreeParserTokenTypes.CONS_ID);              
            String className = classNameNode.getText();
                                                                 
            ParseTreeNode typeVarNameNode = classNameNode.nextSibling();
            typeVarNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID);
                            
            Set<String> dependeeClassSet = checkClassContext (contextListNode, currentModuleTypeInfo.getTypeClass(className), typeVarNameNode.getText());
            vertexBuilderList.add (new VertexBuilder<String>(className, dependeeClassSet));                      
        }
        
        checkClassDependenciesHelper(vertexBuilderList);
    } 
    
    /**
     * Check the inheritance graph for cyclic dependencies and give an appropriate error message if there
     * are any. Note: to check for cyclic dependencies we only look at superclasses of a class that belong
     * to the current module. This is because we cannot have mutually recursive modules in CAL, and we've
     * already verified by this point that that is the case.
     * 
     * @param vertexBuilderList list of classes defined in this module, along with their dependee classes.
     */
    private void checkClassDependenciesHelper(VertexBuilderList<String> vertexBuilderList) {

        // should never fail. It is a redundant check since makeModuleDependencyGraph should throw an exception otherwise.
        if (!vertexBuilderList.makesValidGraph()) {
            throw new IllegalStateException("Internal coding error during dependency analysis."); 
        }

        Graph<String> g = new Graph<String>(vertexBuilderList);

        g = g.calculateStronglyConnectedComponents(); 
            
        int nTypeClasses = vertexBuilderList.size();
        int nComponents = g.getNStronglyConnectedComponents();
        if (nTypeClasses != nComponents) {

            //There is a recursive type class dependency. This is not allowed.

            for (int i = 0; i < nComponents; ++i) {

                Graph<String>.Component component = g.getStronglyConnectedComponent(i);
                int componentSize = component.size();

                if (componentSize > 1) {

                    StringBuilder cyclicNames = new StringBuilder();
                    String cyclicClassName = null;

                    for (int j = 0; j < componentSize; ++j) {

                        if (j > 0) {
                            cyclicNames.append(", ");
                        }

                        cyclicClassName = component.getVertex(j).getName();
                        cyclicNames.append(cyclicClassName);
                    }
                   
                    // Cyclic class context dependencies between classes: {cyclicNames}.
                    compiler.logMessage(new CompilerMessage(new MessageKind.Fatal.CyclicClassContextDependenciesBetweenClasses(cyclicNames.toString())));
                }
            }

            throw new IllegalStateException("TypeChecker: Programming error.");
            
        }         
    }
    
    /**
     * Checks the class context i.e. the list of superclasses of the class being defined.
     * For example, the superclasses:
     * <ol>
     *    <li> must resolve to a type class that actually exists (either within the current module or
     *         an imported module).
     *    <li> must be distinct
     *    <li> must depend on the same type variable as does the type class itself
     * </ol>
     * 
     * @param contextListNode    
     * @param typeClass the type class whose context is being checked.
     * @param typeVarName name of the class type variable e.g. in "class (Eq a) => Ord a" it is "a".
     * @return Set (String) the classes within the current module upon which this class directly depends.
     */
    private Set<String> checkClassContext(ParseTreeNode contextListNode, TypeClass typeClass, String typeVarName) {

        contextListNode.verifyType(CALTreeParserTokenTypes.CLASS_CONTEXT_LIST, CALTreeParserTokenTypes.CLASS_CONTEXT_SINGLETON, CALTreeParserTokenTypes.CLASS_CONTEXT_NOTHING);

        //QualifiedName set of the names of the classes in the context.
        Set<QualifiedName> contextClassSet = new HashSet<QualifiedName>();
               
        //String set of the names of the classes in the context that belong to the current module.
        Set<String> dependeeClassSet = new HashSet<String>();
        
        ModuleName currentModuleName = currentModuleTypeInfo.getModuleName();
                              
        for (final ParseTreeNode contextNode : contextListNode) {

            contextNode.verifyType(CALTreeParserTokenTypes.CLASS_CONTEXT);

            ParseTreeNode contextClassNameNode = contextNode.firstChild();
                      
            //make sure that the class name referred to in the context actually exists, and
            //resolve unqualified names to their fully module qualified versions.
            TypeClass contextTypeClass = resolveClassName(contextClassNameNode);
                                                
            //check that there is not a duplicate appearance of a class constraint in the constraint list.
            QualifiedName contextClassName = contextTypeClass.getName();
            if (!contextClassSet.add(contextClassName)) {
                //a repeated constraint such as 
                //(Eq a, Eq a)
                //is allowed in Hugs, but is not allowed in CAL to be consistent with other restrictions
                //on repeated declarations and definitions.
                compiler.logMessage(new CompilerMessage(contextClassNameNode, new MessageKind.Error.RepeatedClassConstraint(contextClassName.getQualifiedName())));
            }
                         
                                 
            ParseTreeNode typeVarNameNode = contextClassNameNode.nextSibling();
            typeVarNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID);
          
            if (!typeVarName.equals(typeVarNameNode.getText())) {
                //the type variable used in the context part must be the same as the type variable used in the class part   
                //For example, can't have "Eq b => Ord a"             
                compiler.logMessage(new CompilerMessage(typeVarNameNode, new MessageKind.Error.ContextTypeVariableUndefined(typeVarNameNode.getText(), typeVarName)));
            } 
            
            
            if (contextClassName.equals(typeClass.getName())) {
                //todoBI the reason the following error is FATAL instead of just an ERROR, is that continuing compilation after this
                //error will hang CAL, because further analysis gets stuck in a cyclic loop. We will need to adopt a more sophisticated
                //approach to recovery after an error, including possibly a timer to terminate compilation after the first error has
                //been reported.
                
                //a type class cannot include itself in its context e.g. "(Eq a, Ord a) => Ord a".
                compiler.logMessage(new CompilerMessage(contextClassNameNode, new MessageKind.Fatal.TypeClassCannotIncludeItselfInClassConstraintList(contextClassName.getQualifiedName())));
            }
            
            //add the context class as a parent class.
            //todoBI there may be redundant parent classes e.g. (Eq a, Ord a) => Num a, then Eq a is a redundant parent class
            //we need to remove these after performing cylic dependency analysis.
            typeClass.addParentClass(contextTypeClass); 
            
            if (contextClassName.getModuleName().equals(currentModuleName)) {
                dependeeClassSet.add(contextClassName.getUnqualifiedName());
            }        
        }
        
        return dependeeClassSet;  
    } 
    
    /**
     * The kind of a type class is determined by kind inference. This algorithm looks at the kinds of the superclasses as
     * well as the kinds of the types of the class methods. Class methods are allowed to have constrained type signatures.
     * In particular, this means that we need to do kind checking in dependency groups where a type class depends on
     * <ol>
     *    <li> each type class in its superclass context 
     *    <li> each type class in the context of each of its class methods
     * </ol>
     * <p>
     * Since module imports cannot be cyclic, we can ignore type classes defined in other than the current module in
     * determining the dependency graph for kind checking.
     *    
     * @return Graph vertices in the graph are String names of type classes in this module. Kind Checking must be done
     *      one connected component at a time.
     */
    private Graph<String> calculateClassDependencyGraphForKindChecking(List<ParseTreeNode> typeClassDefnNodes) {
        VertexBuilderList<String> vertexBuilderList = new VertexBuilderList<String>();
                      
        for (final ParseTreeNode classDefnNode : typeClassDefnNodes) {
                           
            classDefnNode.verifyType(CALTreeParserTokenTypes.TYPE_CLASS_DEFN);
               
            ParseTreeNode contextListNode = classDefnNode.getChild(2);
            
            ParseTreeNode classNameNode = contextListNode.nextSibling();
            classNameNode.verifyType(CALTreeParserTokenTypes.CONS_ID);              
            String className = classNameNode.getText();
                                                                 
            ParseTreeNode typeVarNameNode = classNameNode.nextSibling();
            typeVarNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID);
              
            Set<String> dependeeClassSet = new HashSet<String>();
            
            addContextDependencies(dependeeClassSet, contextListNode);
                       
            ParseTreeNode classMethodListNode = typeVarNameNode.nextSibling();
            classMethodListNode.verifyType(CALTreeParserTokenTypes.CLASS_METHOD_LIST);                      
            
            for (final ParseTreeNode classMethodNode : classMethodListNode) {
                    
                 classMethodNode.verifyType(CALTreeParserTokenTypes.CLASS_METHOD);                                 
                 
                 ParseTreeNode typeSignatureNode = classMethodNode.getChild(3);
                 typeSignatureNode.verifyType(CALTreeParserTokenTypes.TYPE_SIGNATURE);
                 
                 ParseTreeNode methodContextListNode = typeSignatureNode.firstChild();
                 methodContextListNode.verifyType(CALTreeParserTokenTypes.TYPE_CONTEXT_LIST, CALTreeParserTokenTypes.TYPE_CONTEXT_NOTHING, CALTreeParserTokenTypes.TYPE_CONTEXT_SINGLETON);   
                 
                 addContextDependencies(dependeeClassSet, methodContextListNode);                                  
            }
            
            vertexBuilderList.add (new VertexBuilder<String>(className, dependeeClassSet));                      
        }
        
        Graph<String> g = new Graph<String>(vertexBuilderList);
        
        return g.calculateStronglyConnectedComponents();     
    }
    
    /**
     * Adds the names of the type classes appearing in a type class's context or a class method's context that are also 
     * defined within the current module to the classDependencies argument Set.
     * 
     * Some minimal static checking is done- basically that the type class is resolvable.
     * 
     * @param classDependencies (String set) of type 
     * @param contextListNode a type class or class method context
     */
    private final void addContextDependencies(
        final Set<String> classDependencies,
        final ParseTreeNode contextListNode) {       
        
        contextListNode.verifyType(CALTreeParserTokenTypes.TYPE_CONTEXT_LIST, CALTreeParserTokenTypes.CLASS_CONTEXT_LIST, CALTreeParserTokenTypes.TYPE_CONTEXT_NOTHING, CALTreeParserTokenTypes.TYPE_CONTEXT_SINGLETON, CALTreeParserTokenTypes.CLASS_CONTEXT_SINGLETON, CALTreeParserTokenTypes.CLASS_CONTEXT_NOTHING);
        
        ModuleName currentModuleName = currentModuleTypeInfo.getModuleName();
        
        for (final ParseTreeNode contextNode : contextListNode) {
                        
            switch (contextNode.getType())
            {             
                case CALTreeParserTokenTypes.CLASS_CONTEXT :
                {
                    
                    ParseTreeNode typeClassNameNode = contextNode.firstChild();
                    typeClassNameNode.verifyType(CALTreeParserTokenTypes.QUALIFIED_CONS);
                    
                    //verify that the type class referred to actually exists, and supply its inferred module name
                    //if it was omitted in the user's CAL code.           
                    TypeClass typeClass = resolveClassName(typeClassNameNode);   
                    
                    QualifiedName typeClassName = typeClass.getName();
                    if (typeClassName.getModuleName().equals(currentModuleName)) {
                        classDependencies.add(typeClassName.getUnqualifiedName());
                    }
                                                           
                    break;                                      
                }
                
                case CALTreeParserTokenTypes.LACKS_FIELD_CONTEXT:
                {                    
                    break;
                }
                
                default:
                {                    
                    contextNode.unexpectedParseTreeNode();                    
                    return;                    
                }
            }
        }
    }
    
    /**
     * Adds the class methods to their owning type class, and checks that
     * each class method can be assigned a valid type.    
     * <p>
     * In addition, this method determines the kind of the class. The kind of a class must be the
     * same as the kind of its dependee superclasses. In addition, it must satisfy the constraints
     * introduced by kind-checking of the class methods within the class. Otherwise a CAL compilation
     * error will be reported.
     * <p>
     * Finally, this method verifies that any default class methods are actually resolvable to visible
     * functions (in particular, not class methods or data constructors). Checking that the default class
     * methods have a valid type is be done later, since the types of all functions defined in the module
     * must first be known before this can be done.    
     * 
     * @param typeClassDefnNodes
     */
    void checkClassMethods(final List<ParseTreeNode> typeClassDefnNodes) {
        
        final ModuleName currentModuleName = currentModuleTypeInfo.getModuleName();
        final CALTypeChecker typeChecker = compiler.getTypeChecker();
        
        Map<String, ParseTreeNode> classNameToParseTreeNodeMap = new HashMap<String, ParseTreeNode>(); //String -> ParseTreeNode
        for (final ParseTreeNode classDefnNode : typeClassDefnNodes) {
                            
            classDefnNode.verifyType(CALTreeParserTokenTypes.TYPE_CLASS_DEFN);
               
            ParseTreeNode contextListNode = classDefnNode.getChild(2);
            
            ParseTreeNode classNameNode = contextListNode.nextSibling();
            classNameNode.verifyType(CALTreeParserTokenTypes.CONS_ID);              
            String className = classNameNode.getText();
            classNameToParseTreeNodeMap.put(className, classDefnNode);
        }
        
        final Graph<String> g = calculateClassDependencyGraphForKindChecking(typeClassDefnNodes);
        
        //Kind check the type class definitions. Proceed one strongly connected component at a time.

        for (int componentN = 0, nComponents = g.getNStronglyConnectedComponents(); componentN < nComponents; ++componentN) {

            Graph<String>.Component component = g.getStronglyConnectedComponent(componentN);
           
            int componentSize = component.size();

            for (int i = 0; i < componentSize; ++i) {

                String className = component.getVertex(i).getName();
                                     
                ParseTreeNode classNode = classNameToParseTreeNodeMap.get(className); 
                classNode.verifyType(CALTreeParserTokenTypes.TYPE_CLASS_DEFN);
                                   
                TypeClass typeClass = currentModuleTypeInfo.getTypeClass(className);
                          
                //classTypeVar refers to the type variable that is scoped over the entire type class definition
                //including the class methods.
                
                ParseTreeNode classTypeVarNode = classNode.getChild(4);
                classTypeVarNode.verifyType(CALTreeParserTokenTypes.VAR_ID);            
                final String classTypeVarName = classTypeVarNode.getText();
                                          
                final int nParentClasses = typeClass.getNParentClasses();
                
                if (nParentClasses > 0) {
                    
                    //for class (C1 t, C2 t, ..., Cn t) => C t,
                    //we unify kinds
                    //C1 and C2
                    //C2 and C3
                    //.. 
                    //Cn and C
                    //
                    //this will provide us with the best error message in the common cases that the superclasses are not
                    //in the same kind-checking dependency group as the class itself
                
                    if (nParentClasses > 1) {
                    
                        for (int parentClassN = 1; parentClassN < nParentClasses; ++parentClassN) {
                            
                            TypeClass previousParentClass = typeClass.getNthParentClass(parentClassN - 1);
                            TypeClass currentParentClass = typeClass.getNthParentClass(parentClassN);
                            
                            try {                          
                                KindExpr.unifyKind(previousParentClass.getKindExpr(), currentParentClass.getKindExpr());
                            } catch (TypeException typeException) {
                                //The kind of the type class Foo (* -> *) must be the same as the kind of the type class Bar ((* -> * -> *)) 
                                //"The kinds of all classes constraining the type variable '{0}' must be the same. Class {1} has kind {2} while class {3} has kind {4}."
                                compiler.logMessage(new CompilerMessage(classNode, 
                                    new MessageKind.Error.KindErrorInClassConstraints(
                                        classTypeVarName,
                                        previousParentClass,
                                        currentParentClass)));                                       
                            }
                        }   
                    }
                    
                    TypeClass lastParentClass = typeClass.getNthParentClass(nParentClasses - 1);
                
                    try {
                        KindExpr.unifyKind(lastParentClass.getKindExpr(), typeClass.getKindExpr());
                    } catch (TypeException typeException) {
                        //The kind of the type class Foo (* -> *) must be the same as the kind of the type class Bar ((* -> * -> *)) 
                        //"The kinds of all classes constraining the type variable '{0}' must be the same. Class {1} has kind {2} while class {3} has kind {4}."
                        compiler.logMessage(new CompilerMessage(classNode, 
                            new MessageKind.Error.KindErrorInClassConstraints(
                                classTypeVarName,
                                lastParentClass,
                                typeClass)));     
                    }
                }
                                      
                SortedSet<TypeClass> typeClassConstraintSet = TypeClass.makeNewClassConstraintSet();
                typeClassConstraintSet.add(typeClass);                                                           
                final TypeVar classTypeVar = TypeVar.makeTypeVar(classTypeVarName, typeClassConstraintSet, true);               
            
                ParseTreeNode classMethodListNode = classTypeVarNode.nextSibling();
                classMethodListNode.verifyType(CALTreeParserTokenTypes.CLASS_METHOD_LIST);
                
                int ordinal = 0;
                
                final int nClassMethods = classMethodListNode.getNumberOfChildren();
                final int nAncestors = typeClass.calculateAncestorClassList().size();
                
                final KindExpr classTypeVarKindExpr = typeClass.getKindExpr();                
                
                for (final ParseTreeNode classMethodNode : classMethodListNode) {
                        
                     classMethodNode.verifyType(CALTreeParserTokenTypes.CLASS_METHOD);
                     
                     ParseTreeNode classMethodOptionalCALDocNode = classMethodNode.firstChild();
                     ParseTreeNode classMethodAccessModifierNode = classMethodOptionalCALDocNode.nextSibling();
                     Scope classMethodScope = CALTypeChecker.getScopeModifier(classMethodAccessModifierNode);
                                              
                     ParseTreeNode classMethodNameNode = classMethodAccessModifierNode.nextSibling();
                     classMethodNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID);
                     String classMethodName = classMethodNameNode.getText();
                                                                                                                        
                     TypeExpr typeExpr = typeChecker.calculateDeclaredClassMethodType(classMethodNode, classTypeVarName, classTypeVar, classTypeVarKindExpr);
                     
                     //the index of the class type variable in the ordered list of overloaded record and type vars in the class
                     //method's type. This is used during overload resolution.
                     int classTypeVarPolymorphicIndex = new ArrayList<PolymorphicVar>(typeExpr.getConstrainedPolymorphicVars()).indexOf(classTypeVar);                      
              
                     //the index for the class method in the dictionary of its type class. This has a special value
                     //of -1 if the class method is the single class method of a type class with no ancestors.
                     //Otherwise it is equal to the number of ancestors classes (i.e. including grandparents etc) +
                     //the ordinal of the method. 
                     int dictionaryIndex;
                     if (nAncestors == 0 && nClassMethods == 1) {
                         dictionaryIndex = -1;
                     } else {
                         dictionaryIndex = nAncestors + ordinal;
                     }
                     
                     ParseTreeNode defaultClassMethodNameNode = (ParseTreeNode)classMethodNameNode.getNextSibling().getNextSibling();
                     final QualifiedName defaultClassMethodName;
                     if (defaultClassMethodNameNode != null) {
                         defaultClassMethodName = ClassInstanceChecker.resolveMethodName(defaultClassMethodNameNode, compiler, currentModuleTypeInfo);
                     } else {
                         //no default supplied. This is OK.
                         defaultClassMethodName = null;
                     }
                                              
                     ClassMethod classMethod = new ClassMethod (QualifiedName.make(currentModuleName, classMethodName),
                        classMethodScope, new String[] {}, typeExpr, ordinal, dictionaryIndex, classTypeVarPolymorphicIndex, defaultClassMethodName); 
                         
                     typeClass.addClassMethod(classMethod);
                     
                     ++ordinal;
                }
                                  
            }
            
            for (int i = 0; i < componentSize; ++i) {

                String typeClassName = component.getVertex(i).getName();
                TypeClass typeClass = currentModuleTypeInfo.getTypeClass(typeClassName);
                typeClass.finishedKindChecking();                
            }            
        }
                         
        if (DEBUG_INFO) {           
            for (int i = 0, nTypeClasses = currentModuleTypeInfo.getNTypeClasses(); i < nTypeClasses; ++i) {
                TypeClass typeClass = currentModuleTypeInfo.getNthTypeClass(i);
                System.out.println(typeClass);
            }     
        }         
    }
    
    /**
     * Checks that the type of each default class method is the same as the type of the class method,
     * and logs an error otherwise. This check must be done after type checking for the module is complete.
     * 
     * @param typeClassDefnNodes
     */
    void checkDefaultClassMethodTypes(List<ParseTreeNode> typeClassDefnNodes) {
        
        for (final ParseTreeNode classDefnNode : typeClassDefnNodes) {
                                    
            classDefnNode.verifyType(CALTreeParserTokenTypes.TYPE_CLASS_DEFN);
                           
            ParseTreeNode classNameNode = classDefnNode.getChild(3);
            classNameNode.verifyType(CALTreeParserTokenTypes.CONS_ID);              
            String className = classNameNode.getText();            
            TypeClass typeClass = currentModuleTypeInfo.getTypeClass(className); 
                                               
            ParseTreeNode classMethodListNode = classNameNode.nextSibling().nextSibling();
            classMethodListNode.verifyType(CALTreeParserTokenTypes.CLASS_METHOD_LIST);                      
            
            for (final ParseTreeNode classMethodNode : classMethodListNode) {
                    
                 classMethodNode.verifyType(CALTreeParserTokenTypes.CLASS_METHOD);   
                 
                 ParseTreeNode classMethodNameNode = classMethodNode.getChild(2);
                 classMethodNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID); 
                 
                 String classMethodName = classMethodNameNode.getText();
                 ClassMethod classMethod = typeClass.getClassMethod(classMethodName);
                 
                 QualifiedName defaultClassMethodName = classMethod.getDefaultClassMethodName();
                 if (defaultClassMethodName != null) {
                     
                     Function defaultClassMethod = currentModuleTypeInfo.getVisibleFunction(defaultClassMethodName);
                     
                     if (!classMethod.getTypeExpr().sameType(defaultClassMethod.getTypeExpr())) {
                         
                         ParseTreeNode defaultClassMethodNameNode = classMethodNameNode.nextSibling().nextSibling();
                         
                         compiler.logMessage(
                             new CompilerMessage(
                                 defaultClassMethodNameNode,
                                 new MessageKind.Error.InvalidDefaultClassMethodType(
                                     defaultClassMethod.getName(),
                                     classMethodName,
                                     classMethod.getTypeExpr().toString(),
                                     defaultClassMethod.getTypeExpr().toString())));                                                 
                     } 
                 }
            }            
        }
    }
    
    /**
     * Verifies that the type class referred to actually exists.
     * If the type class referred to has its module name omitted, but the module name 
     * can be inferred, then the module name is written into the ParseTreeNode as
     * a side effect, so that subsequent code can assume a fully qualified name.   
     * @param qualifiedClassNameNode
     * @return TypeClass the TypeClass object (possibly not fully constructed) or null if a resolution can't be made.
     */
    TypeClass resolveClassName(ParseTreeNode qualifiedClassNameNode) {
        return resolveClassName(qualifiedClassNameNode, currentModuleTypeInfo, compiler);
    }
    
    /**
     * Verifies that the type class referred to actually exists.
     * If the type class referred to has its module name omitted, but the module name 
     * can be inferred, then the module name is written into the ParseTreeNode as
     * a side effect, so that subsequent code can assume a fully qualified name.   
     * @param qualifiedClassNameNode the node containing the qualified type class name to be resolved.
     * @param currentModuleTypeInfo the type info for the current module.
     * @param compiler the CALCompiler instance for error reporting.
     * @return TypeClass the TypeClass object (possibly not fully constructed) or null if a resolution can't be made.
     */
    static TypeClass resolveClassName(ParseTreeNode qualifiedClassNameNode, ModuleTypeInfo currentModuleTypeInfo, CALCompiler compiler) {
        return resolveClassName(qualifiedClassNameNode, currentModuleTypeInfo, compiler, false);
    }
    
    /**
     * Verifies that the type class referred to actually exists.
     * If the type class referred to has its module name omitted, but the module name 
     * can be inferred, then the module name is written into the ParseTreeNode as
     * a side effect, so that subsequent code can assume a fully qualified name.   
     * @param qualifiedClassNameNode the node containing the qualified type class name to be resolved.
     * @param currentModuleTypeInfo the type info for the current module.
     * @param compiler the CALCompiler instance for error reporting.
     * @param suppressErrorMessageLogging whether to suppress the logging of error messages to the CALCompiler instance
     * @return TypeClass the TypeClass object (possibly not fully constructed) or null if a resolution can't be made.
     */
    static TypeClass resolveClassName(ParseTreeNode qualifiedClassNameNode, ModuleTypeInfo currentModuleTypeInfo, CALCompiler compiler, boolean suppressErrorMessageLogging) {
        
        qualifiedClassNameNode.verifyType(CALTreeParserTokenTypes.QUALIFIED_CONS);

        ParseTreeNode moduleNameNode = qualifiedClassNameNode.firstChild();
        String maybeModuleName = ModuleNameUtilities.resolveMaybeModuleNameInParseTree(moduleNameNode, currentModuleTypeInfo, compiler, suppressErrorMessageLogging);
        ParseTreeNode classNameNode = moduleNameNode.nextSibling();
        String className = classNameNode.getText();

        ModuleName currentModuleName = currentModuleTypeInfo.getModuleName();
       
        if (maybeModuleName.length() > 0) {

            //an explicitly qualified type class name
            ModuleName moduleName = ModuleName.make(maybeModuleName);
            
            if (moduleName.equals(currentModuleName)) {

                //the type class must be in the current module

                TypeClass typeClass = currentModuleTypeInfo.getTypeClass(className);
                if (typeClass == null) {
                    if (!suppressErrorMessageLogging) {
                        // The class {qualifiedClassName} does not exist in {currentModuleName}.
                        QualifiedName qualifiedClassName = QualifiedName.make(moduleName, className);
                        compiler.logMessage(new CompilerMessage(classNameNode, new MessageKind.Error.ClassDoesNotExistInModule(qualifiedClassName.getQualifiedName(), currentModuleName)));
                    }
                    return null;
                }

                checkResolvedTypeClassReference(compiler, qualifiedClassNameNode, typeClass.getName(), suppressErrorMessageLogging);
                
                //Success- the type class was found in the current module's environment
                return typeClass;
            }

            //the class must be in an imported module

            ModuleTypeInfo importedModuleTypeInfo = currentModuleTypeInfo.getImportedModule(moduleName);

            if (importedModuleTypeInfo == null) {
                if (!suppressErrorMessageLogging) {
                    // The module {moduleName} has not been imported into {currentModuleName}.
                    compiler.logMessage(new CompilerMessage(moduleNameNode, new MessageKind.Error.ModuleHasNotBeenImported(moduleName, currentModuleName)));
                }
                return null;
            }

            TypeClass typeClass = importedModuleTypeInfo.getTypeClass(className);
            if (typeClass == null) {

                if (!suppressErrorMessageLogging) {
                    QualifiedName qualifiedClassName = QualifiedName.make(moduleName, className);
                    // The class {qualifiedClassName} does not exist.
                    compiler.logMessage(new CompilerMessage(classNameNode, new MessageKind.Error.TypeClassDoesNotExist(qualifiedClassName.getQualifiedName())));
                }                    
                return null;
                    
            } else if (!currentModuleTypeInfo.isEntityVisible(typeClass)) {
                
                if (!suppressErrorMessageLogging) {
                    //"The class {0} is not visible in module {1}."
                    compiler.logMessage(new CompilerMessage(classNameNode, new MessageKind.Error.TypeClassNotVisible(typeClass, currentModuleName)));
                }
                return null;                    
            }

            checkResolvedTypeClassReference(compiler, qualifiedClassNameNode, typeClass.getName(), suppressErrorMessageLogging);
            
            //Success- the module is indeed imported and has a visible type class of the specified name.
            return typeClass;
        }

        //An unqualified type class

        TypeClass typeClass = currentModuleTypeInfo.getTypeClass(className);
        if (typeClass != null) {

            checkResolvedTypeClassReference(compiler, qualifiedClassNameNode, typeClass.getName(), suppressErrorMessageLogging);
            
            //The type class is defined in the current module         
            ModuleNameUtilities.setModuleNameIntoParseTree(moduleNameNode, currentModuleName);
            return typeClass;
        }
        
        //We now know that the type class can't be defined within the current module.
        //check if it is a "using typeClass" and then patch up the module name.
        
        ModuleName usingModuleName = currentModuleTypeInfo.getModuleOfUsingTypeClass(className);
        if (usingModuleName != null) {
            final QualifiedName qualifiedClassName = QualifiedName.make(usingModuleName, className);
            
            checkResolvedTypeClassReference(compiler, qualifiedClassNameNode, qualifiedClassName, suppressErrorMessageLogging);
            
            ModuleNameUtilities.setModuleNameIntoParseTree(moduleNameNode, usingModuleName);
            //this call is guaranteed to return a type class due to earlier static checks on the using clause            
            return currentModuleTypeInfo.getVisibleTypeClass(qualifiedClassName);
        }
        
        if (!suppressErrorMessageLogging) {
            //We now know that the type class can't be defined within the current module and is not a "using typeClass".
            //Check if it is defined in another module.        
            //This will be an error since the user must supply a module qualification, but we
            //can attempt to give a good error message.         
            
            List<QualifiedName> candidateEntityNames = new ArrayList<QualifiedName>();
            int nImportedModules = currentModuleTypeInfo.getNImportedModules();
            for (int i = 0; i < nImportedModules; ++i) {
                
                TypeClass candidate = currentModuleTypeInfo.getNthImportedModule(i).getTypeClass(className);
                if (candidate != null && currentModuleTypeInfo.isEntityVisible(candidate)) {
                    candidateEntityNames.add(candidate.getName());
                }
            }
            
            int numCandidates = candidateEntityNames.size();
            if(numCandidates == 0) {
                // Attempt to use undefined identifier {varName}.
                compiler.logMessage(new CompilerMessage(classNameNode, new MessageKind.Error.AttemptToUseUndefinedClass(className)));
                
            } else if(numCandidates == 1) {
                
                QualifiedName candidateName = candidateEntityNames.get(0);
                
                // Attempt to use undefined class {className}. Was {candidateName} intended?
                compiler.logMessage(new CompilerMessage(classNameNode, new MessageKind.Error.AttemptToUseUndefinedClassSuggestion(className, candidateName)));                
                
            } else {
                // The reference to the class {className} is ambiguous. It could mean any of {candidateEntityNames}. 
                compiler.logMessage(new CompilerMessage(classNameNode, new MessageKind.Error.AmbiguousClassReference(className, candidateEntityNames)));
            }
        }

        return null;
    }                         
    
    /**
     * Performs late static checks on a type class reference that has already been resolved.
     * 
     * Currently, this performs a deprecation check on a type class reference, logging a warning message if the type class is deprecated.
     * @param compiler the compiler instance.
     * @param nameNode the parse tree node representing the reference.
     * @param qualifiedName the qualified name of the reference.
     * @param suppressCompilerMessageLogging whether to suppress message logging.
     */
    static void checkResolvedTypeClassReference(final CALCompiler compiler, final ParseTreeNode nameNode, final QualifiedName qualifiedName, final boolean suppressCompilerMessageLogging) {
        if (!suppressCompilerMessageLogging) {
            if (compiler.getDeprecationScanner().isTypeClassDeprecated(qualifiedName)) {
                compiler.logMessage(new CompilerMessage(nameNode, new MessageKind.Warning.DeprecatedTypeClass(qualifiedName)));
            }
        }
    }
}
