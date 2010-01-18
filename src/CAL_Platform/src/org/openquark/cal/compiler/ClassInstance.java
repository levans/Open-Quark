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
 * ClassInstance.java
 * Created: April 5, 2001
 * By: Bo Ilic
 */
package org.openquark.cal.compiler;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.List;
import java.util.ArrayList;

import org.openquark.cal.internal.serialization.ModuleSerializationTags;
import org.openquark.cal.internal.serialization.RecordInputStream;
import org.openquark.cal.internal.serialization.RecordOutputStream;
import org.openquark.cal.internal.serialization.RecordInputStream.RecordHeaderInfo;


/**
 * Models a class instance definition.
 * <p>
 * There are 2 kinds of class instances:
 * <p>
 * <ol>
 *  <li> type constructor instances -the instance type has a type constructor root
 *  e.g. 
 *  <ul>
 *     <li> instance Num Int
 *     <li> instance Eq a => Eq (List a)
 *     <li> instance (Ord a, Ord b) => Ord (Tuple2 a b)
 *     <li> instance Enum Ordering
 *  </ul> 
 * <p> 
 * <li> universal record instances - the instance type is a record-polymorphic record.
 *  It is "universal" because the instance definition applies to *all* records whose fields satisfy the specified
 *  constraints i.e. it does not make any restrictions on the allowable field names. For example,
 *  instance Outputable a => Outputable {a}
 * </ol>
 * <p>
 * @author Bo Ilic
 */
public final class ClassInstance {

    private static final int serializationSchema = 0;
    
    /** the module in which this class instance is defined. */
    private final ModuleName moduleName;

    /** 
     * The type of the instance e.g. if Int is an instance of Num, then this is Int. This is
     * a type (rather than just a string such as "Int") because of constrained instances.
     * For example, "instance Eq a => [a]" and "instance (Ord a, Ord b) => Ord (a, b)" to
     * define Eq on lists, and Ord for pairs.
     */
    private final TypeExpr instanceType;

    /** the TypeClass object that this instance implements */
    private final TypeClass typeClass;
        
    /** the style of the instance - indicates how it was created*/
    private final InstanceStyle instanceStyle;
    
    private transient ClassInstanceIdentifier identifier;    
     
    /**
     * the (QualifiedName) list of names of the concrete methods that implement the class methods.
     * Note, these must follow the same ordering as TypeClass.classMethodsList.
     * For example, in the case of Int as an instance of Num, this would be
     * ["addInt", "subtractInt", "multiplyInt", "divideInt"]
     * Note that an entry in this array will be null if no explicit instance method was supplied in the instance declaration
     * and we default to the default class method.
     */
    private final QualifiedName[] instanceMethods;
    
    /** The CALDoc comment for this instance, or null if there is none. */
    private CALDocComment calDocComment;
    
    /** The CALDoc comments for the instance methods. Each element can be null if the method has no corresponding CALDoc. */
    private final CALDocComment[] methodCALDocComments;
    
    /**
     * The prefix used for defining dictionary functions such as $dictEq#Int.    
     */
    static final String DICTIONARY_FUNCTION_PREFIX = "$dict";  
    
    /**
     * This is an Enumeration that defines the different styles of instances
     * @author Magnus Byne
     */
    final public static class InstanceStyle {
        private final String name;
        private final int key;
        
        private InstanceStyle(String name, int key) { this.name = name; this.key = key; }
        
        @Override
        public String toString() { return name; }
        
        private static final int explicitKey = 1;
        private static final int derivingKey = 2;
        private static final int internalKey = 3;
                        
        
        /** instances that are defined explicitly in full in source. This does 
         * not include instances that are created via a deriving clause*/
        public static final InstanceStyle EXPLICIT = new InstanceStyle("Explicit", explicitKey);

        /** instances that are created via a deriving clause*/
        public static final InstanceStyle DERIVING = new InstanceStyle("Deriving", derivingKey);
    
        /** instances that are created by the compiler. This includes most typeable instances,
         * which are typically created automatically be the compiler.*/
        public static final InstanceStyle INTERNAL = new InstanceStyle("Internal", internalKey);
        
        /**
         * @return this is true if the instance has been defined implicitly,
         * e.g. through a deriving clause, or by the compiler.
         */
        public final boolean isImplicit() {
            return this != EXPLICIT;
        }
                
        /** serialize the style*/
        void write(RecordOutputStream s) throws IOException {
            s.writeIntCompressed(key);
        }
        
        /** un-serialize the style*/
        static InstanceStyle load(RecordInputStream s, CompilerMessageLogger msgLogger) throws IOException {
            int key = s.readIntCompressed();
        
            switch (key) {
            case explicitKey:
                return EXPLICIT;
            case derivingKey:
                return DERIVING;
            case internalKey:
                return INTERNAL;
            default:
                throw new IOException("Unknown " + InstanceStyle.class.getName() + " key encountered: " + key);                 
            }
        }
        
    }
    
    /**
     * ClassInstance constructor.
     * Creation date: (4/5/01 12:25:25 PM)
     * @param moduleName the module in which this class instance is defined
     * @param instanceType the type of the instance e.g. Int
     * @param typeClass the class that this is an instance of e.g. Ord
     * @param instanceMethods instance methods, in order of the type class methods. Can be null if they will be set later,
     *    or if they should be automatically determined for instanceStyle instances.
     * @param instanceStyle whether the class instance is internally defined e.g. most Typeable instances and derived
     *    instances (versus defined in source via an explicit instance declaration).
     */
    ClassInstance(ModuleName moduleName, TypeExpr instanceType, TypeClass typeClass, QualifiedName[] instanceMethods, InstanceStyle instanceStyle) {
        
        if (moduleName == null || instanceType == null || typeClass == null) {
            throw new NullPointerException();
        }
                
        final int nClassMethods = typeClass.getNClassMethods();        

        if (instanceType instanceof TypeConsApp) {
             //do a quick check to verify that all the arguments of the type constructor
             //are uninstantiated type variables.
             TypeConsApp instanceTypeConsApp = (TypeConsApp)instanceType;
             for (int i = 0, nVars = instanceTypeConsApp.getNArgs(); i < nVars; ++i) {
                 TypeVar typeVar = (TypeVar)instanceTypeConsApp.getArg(i);
                 if (typeVar.getInstance() != null) {
                     throw new IllegalStateException();
                 }
             }
             
             if ( (instanceStyle.isImplicit()) && instanceMethods == null) {
                 
                 //the names of the instance methods of implicitly defined instance are determined from the 
                 //class method name and instance type constructor (provided they haven't been pre-supplied via
                 //a non-null instanceMethods argument.
                 
                 instanceMethods = new QualifiedName[nClassMethods];
                 
                 
                 //if this is a built-in Typeable instance, then there is a single class method whose name is derived from the TypeConsApp name
                 //For example, for Either, it will be $typeOfEither.
                 
                 QualifiedName typeConsName = instanceTypeConsApp.getName();
               
                 for (int i = 0; i < nClassMethods; ++i) {
                     ClassMethod classMethod = typeClass.getNthClassMethod(i);
                     instanceMethods[i] = ClassInstance.makeInternalInstanceMethodName(classMethod.getName().getUnqualifiedName(), typeConsName);                                                         
                 }                                      
             }
             
        } else if (instanceType instanceof RecordType) {
            if ((instanceStyle.isImplicit())) {
                throw new IllegalArgumentException("there are no implicitly defined record instances.");
            }            
            //this is OK as well.           
        } else {            
            throw new IllegalArgumentException("instances must be based on type constructors or records.");
        }        

        this.moduleName = moduleName;
        this.instanceType = instanceType;
        this.typeClass = typeClass;
        this.instanceStyle = instanceStyle;
        
        if (instanceMethods == null) {
            //initialize, but do not populate. instanceMethods will be populated by addInstanceMethod calls below.
            this.instanceMethods = new QualifiedName[nClassMethods];
        } else {
            this.instanceMethods = instanceMethods;
        }
        this.identifier = ClassInstanceIdentifier.make(this);
        
        this.calDocComment = null;
        this.methodCALDocComments = new CALDocComment[this.instanceMethods.length];
    }
    
    /**
     * The name of the instance method for an internally defined class instance.   
     *   
     * @param classMethodName for example "equals" or "typeOf"
     * @param typeConsName for example, "Prelude.Either" or "LegacyTuple.Tuple2"
     * @return for example, "Prelude.$equals$Either" or "LegacyTuple.$typeOf$Tuple2"
     */
    static QualifiedName makeInternalInstanceMethodName(String classMethodName, QualifiedName typeConsName) {        
        String instanceMethodName =             
            new StringBuilder("$")
            .append(classMethodName)
            .append('$')
            .append(typeConsName.getUnqualifiedName()).toString();
        return QualifiedName.make(typeConsName.getModuleName(), instanceMethodName);          
    }
    
    /**     
     * @return true if the instance type is a record-polymorphic record type, such as e.g. "instance Eq r => {r}".
     */
    public final boolean isUniversalRecordInstance () {
                        
        return instanceType instanceof RecordType;
    }
         
    /**
     * @return the style of this instance, e.g. internal, deriving, explicit
     */
    public final InstanceStyle getInstanceStyle () {
        return instanceStyle;
    }
    
    /**     
     * @return true if the instance type has a type constructor root, such as e.g. "instance Ord Int", "instance Eq a => Eq (List a)"
     */
    public final boolean isTypeConstructorInstance () {
        return instanceType instanceof TypeConsApp;  
    }    
    
    /** 
     * @return TypeClass the type class that this instance is an instance of. e.g. if Ord Int, then it is the Ord type class.
     */
    public final TypeClass getTypeClass() {
        return typeClass;
    }
    
    /**
     * Add the name of an implementing method to the instance.
     * 
     * @param classMethod the class method that this instance method will implement
     * @param instanceMethodName
     */
    void addInstanceMethod(ClassMethod classMethod, QualifiedName instanceMethodName) {
        
        final int ordinal = classMethod.getOrdinal();
        if (instanceMethods[ordinal] != null) {
            throw new IllegalStateException("attempt to re-initialize an instance method.");
        }
        
        instanceMethods[ordinal] = instanceMethodName;
    }
            
    /**     
     * For example, if this is the Eq-Int instance, and the class method name is equals,
     * this returns equalsInt.
     * @param classMethodName 
     * @return QualifiedName instance method name corresponding to classMethodName, or null if none such exists.  
     */
    QualifiedName getInstanceMethod(QualifiedName classMethodName) {

        if (!classMethodName.getModuleName().equals(typeClass.getName().getModuleName())) {        
            return null;
        }

        int methodN = typeClass.getClassMethodIndex(classMethodName.getUnqualifiedName());
        if (methodN == -1) {
            return null;
        }

        return getInstanceMethod(methodN);
    }    
          
    /**
     * Get the name of the implementing instance method.
     * 
     * @return QualifiedName
     * @param methodN the number of the method in the list of class methods for the type class.
     *      Will be null the instance does not define the method. That is, the intent is to use
     *      the default class method.     
     */
    public final QualifiedName getInstanceMethod(int methodN) {

        return instanceMethods[methodN];
    }
    
    /**
     * Returns the type of the specified instance method. For example, the add method in the instance Num Int
     * would have a type of Int -> Int -> Int.
     * @param methodN the index of the method in the list of class methods for the type class
     * @return type type of the specified instance method.
     */
    public final TypeExpr getInstanceMethodType(int methodN) {
        ClassMethod classMethod = typeClass.getNthClassMethod(methodN);                               
        
        TypeExpr classMethodType = CopyEnv.freshType(classMethod.getTypeExpr(), null);        
        TypeVar classTypeVar = classMethodType.getTypeClassTypeVar(typeClass.getTypeClassTypeVarName());
        classTypeVar.setInstance(this.getType());
        TypeExpr instanceMethodType = classMethodType;
        
        return instanceMethodType;
    }
    
    /**
     * Returns the type of the specified instance method. For example, the add method in the instance Num Int
     * would have a type of Int -> Int -> Int.
     * @param methodName the name of the instance method.
     * @return type type of the specified instance method.
     */
    public final TypeExpr getInstanceMethodType(String methodName) {
        int methodN = typeClass.getClassMethodIndex(methodName);
        if (methodN == -1) {
            throw new IllegalArgumentException("There is no method named " + methodName + " in this instance.");
        }

        return getInstanceMethodType(methodN);
    }
    
    /**
     * @return int the number of instance methods. This is the same as the number of class methods of the correspoding type class.
     */
    public final int getNInstanceMethods() {

        return instanceMethods.length;
    }       
    
    /**
     * The type of the instance e.g. Int, Double, Eq a => [a].
     * Creation date: (4/5/01 1:00:23 PM)
     * @return TypeExpr
     */
    public final TypeExpr getType() {
        //make a copy so that clients don't accidentally mess up ClassInstance's own internal value.
        return CopyEnv.freshType(instanceType, null);
    }
    
    /** 
     * @return the instance identifier, where the type class and type constructor entity name are
     *      fully qualified and separated by a space e.g. "Cal.Core.Prelude.Num Cal.Core.Prelude.Int"
     */
    public final ClassInstanceIdentifier getIdentifier() {        
        return identifier;              
    }
    
    /**     
     * The internal name of the hidden dictionary function corresponding to this class instance.
     * For example, for a C-T instance defined in module M this is M.$dictC#T.
     * 
     * Note the behavior in the case of single method root classes is to return the instance method name
     * (or the default class method name if there is no instance method defined). This is because the single
     * method root class case is optimized and there are no dictionary functions generated for it. The instance
     * (or class) method returned serves an analogous role.
     * 
     * @return the internal name of the dictionary function for this instance.
     */
    QualifiedName getDictionaryFunctionName() {
        //the dictionary function for an instance whose type class is a single class
        //method type classes with no parent classes can be defined as in the example:
        //$dictClass#Type = instanceFunction;
        //so we might as well inline the dictionary function, and just use instanceFunction.
        if (typeClass.internal_isSingleMethodRootClass()) {
            
            QualifiedName instanceMethodName = getInstanceMethod(0);            
            if (instanceMethodName == null) {
                //use the default class method
                ClassMethod classMethod = typeClass.getNthClassMethod(0);                
                return classMethod.getDefaultClassMethodName();                
            }
            return instanceMethodName;
        }
        
        return QualifiedName.make(moduleName, DICTIONARY_FUNCTION_PREFIX + getIdentifier().getInstanceIdentifier());
    }
      
    /**
     * A canonical form of the name of the class instance, including the context.
     * This is essentially what follows the "instance" keyword in the CAL source for the instance declaration if fully
     * qualified names are used.
     *
     * <p>Some examples:
     * <ol>
     *   <li> Cal.Core.Prelude.Ord Cal.Core.Prelude.Int
     *   <li> (Cal.Core.Prelude.Eq a, Cal.Core.Prelude.Eq b) => Cal.Core.Prelude.Eq (Cal.Core.Prelude.Either a b)
     *   <li> Cal.Core.Debug.Show a => Cal.Core.Debug.Show [a]
     * </ol>    
     *          
     * @return a canonical form of the name of the class instance, along with its context.
     */
    public final String getNameWithContext() {
        return getNameWithContext(ScopedEntityNamingPolicy.FULLY_QUALIFIED);
    }
    
    /**
     * The name of the class instance, including the context, using the specified naming policy.
     * This is essentially what follows the "instance" keyword in the CAL source for the instance declaration, modified
     * according to the naming policy.
     *
     * <p>Some examples with the unqualified naming policy:
     * <ol>
     *   <li> Ord Int
     *   <li> (Eq a, Eq b) => Eq (Either a b)
     *   <li> Show a => Show [a]
     * </ol>    
     * 
     * @param namingPolicy used for formatting type class and type constructor names within the class instance name and context      
     * @return the name of the class instance, along with its context, using the specified naming policy.
     */    
    public final String getNameWithContext(ScopedEntityNamingPolicy namingPolicy) {        
        return getNameWithContext(typeClass, instanceType, namingPolicy);     
    }
    
    /**     
     * @return a canonical form of the name of the class instance.
     */
    public final String getName() {
        return getName(ScopedEntityNamingPolicy.FULLY_QUALIFIED);
    }
    
    /**     
     * @param namingPolicy
     * @return the name of the class instance (e.g. "Eq (Either a b)") not including the class context, using the specified naming policy.
     */
    public final String getName(ScopedEntityNamingPolicy namingPolicy) {
        return getNameHelper(typeClass, instanceType, namingPolicy, false);        
    }
            
    /**
     * Sometimes a class instance for typeClass-instanceType is required, but none is supplied. In that case
     * we want to give an error message describing what instance is required. This method provides the 
     * declaration name for such an instance.
     * @param typeClass
     * @param instanceType
     * @param namingPolicy
     * @return String
     */
    static String getNameWithContext(TypeClass typeClass, TypeExpr instanceType, ScopedEntityNamingPolicy namingPolicy) {              
        return getNameHelper(typeClass, instanceType, namingPolicy, true);
    }
       
    static private String getNameHelper(
            final TypeClass typeClass,
            final TypeExpr instanceType,
            final ScopedEntityNamingPolicy namingPolicy,
            final boolean includeContext) {
        
        final String className = typeClass.getAdaptedName(namingPolicy);
        final String typeName = instanceType.toString(namingPolicy);
        final int nArgs;
        if (instanceType instanceof TypeConsApp) {
            nArgs = ((TypeConsApp)instanceType).getNArgs();
        } else {
            nArgs = 0;
        }
        //find if the type has a context
        final int index = typeName.indexOf(" => ");
        final String classInstanceName;
        if (index != -1) {            
            final String rawTypeString = typeName.substring(index + 4);
            if (includeContext) {
                final String contextString = typeName.substring(0, index);
                classInstanceName = contextString + " => " + className + " " + parenthesize(rawTypeString, nArgs);
            } else {
                classInstanceName = className + " " + parenthesize(rawTypeString, nArgs);
            }
        } else {
            classInstanceName = className + " " + parenthesize(typeName, nArgs);
        }

        return classInstanceName; 
    }    
    
    private static String parenthesize (String typeName, int nArgs) {
        //don't print Eq ([a]) or Eq ((a, b)) but rather Eq [a] or Eq (a, b)
        //also don't parenthesize 0-arity constructors i.e. Eq Int not Eq (Int).
        
        if (Character.isLetter(typeName.charAt(0)) && nArgs > 0) {            
            return "(" + typeName + ")";
        }     
        
        return typeName;
    }    
    
    /**
     * @return the module in which this class instance is defined.
     */
    public final ModuleName getModuleName() {
        return moduleName;
    }
    
    /**
     * @return the CALDoc comment for this instance, or null if there is none.
     */
    public final CALDocComment getCALDocComment() {
        return calDocComment;
    }
    
    /**
     * Set the CALDoc comment associated with this instance.
     * @param comment the CALDoc comment for this instance, or null if there is none.
     */
    final void setCALDocComment(CALDocComment comment) {
        calDocComment = comment;
    }
    
    /**
     * @param methodName the name of the method.
     * @return the CALDoc comment for the named method, or null if there is none.
     */
    public final CALDocComment getMethodCALDocComment(String methodName) {
        int methodN = typeClass.getClassMethodIndex(methodName);
        if (methodN == -1) {
            throw new IllegalArgumentException("There is no method named " + methodName + " in this instance.");
        }

        return methodCALDocComments[methodN];
    }
    
    /**
     * Set the CALDoc comment associated with the named method.
     * @param methodName the name of the method.
     * @param comment the CALDoc comment for the named method, or null if there is none.
     */
    void setMethodCALDocComment(String methodName, CALDocComment comment) {
        int methodN = typeClass.getClassMethodIndex(methodName);
        if (methodN == -1) {
            throw new IllegalArgumentException("There is no method named " + methodName + " in this instance.");
        }

        methodCALDocComments[methodN] = comment;
    }
    
    /**
     * Textual description of the instance. This is very close to what Haskell source
     * for the instance declaration would be.
     * Creation date: (4/5/01 1:14:01 PM)
     * @return String
     */
    @Override
    public String toString() {

        StringBuilder result = new StringBuilder("instance ");
        result.append(getNameWithContext());
        result.append(" where\n");

        int nClassMethods = typeClass.getNClassMethods();
        int nInstanceMethods = getNInstanceMethods();

        for (int i = 0; i < nClassMethods; ++i) {

            result.append("    ").append(typeClass.getNthClassMethod(i).getName()).append(" = ");

            if (i < nInstanceMethods) {
                result.append(getInstanceMethod(i));
            } else {
                result.append("???");
            }

            result.append(";\n");

        }

        return result.toString();
    }
    
    /**
     * Gets the constraints on the polymorphic variables of a class instance in a way suitable for processing.
     * For example, for the instance (Eq a, Ord c, Ord d, Enum a, Outputable d) => Ord (Foo a b c d),
     * this would return [{Eq, Enum}, {}, {Eq, Ord}, {Eq, Ord, Outputable}].
     *     
     * For example, for the instance (Eq a) => Eq {a} this would return [{Eq}].
     * 
     * The constraints include the implicit superclass constraints as well.        
     * @return List<Set<TypeClass>> the ith element of the list is the set of all (including implicit superclass) TypeClass constraints
     *         on the ith polymorphic var of the instance type. (T in C-T).
     */
    List<Set<TypeClass>> getSuperclassPolymorphicVarConstraints() {
        
        if (isUniversalRecordInstance()) {            
            List<Set<TypeClass>> varConstraints = new ArrayList<Set<TypeClass>>(1);
            Set<TypeClass> varConstraint = new HashSet<TypeClass>(((RecordType)instanceType).getPrunedRecordVar().getTypeClassConstraintSet());
            varConstraints.add(varConstraint);
            return varConstraints;
        }             
                                                   
        //call getType() in order to work with a copy of the type
        TypeConsApp typeConsApp = (TypeConsApp)getType(); 
        final int nVars = typeConsApp.getNArgs();
        
        List<Set<TypeClass>> varConstraints = new ArrayList<Set<TypeClass>>(nVars);
        for (int i = 0; i < nVars; ++i) {            
            TypeVar typeVar = (TypeVar)(typeConsApp.getArg(i));            
            varConstraints.add(typeVar.flattenTypeClassConstraintSet());
        }        
        
        return varConstraints;                                              
    } 
    
    /**
     * Gets the constraints on the polymorphic variables of a class instance in a way suitable for processing.
     * For example, for the instance (Eq a, Ord c, Ord d, Enum a, Outputable d) => Ord (Foo a b c d),
     * this would return [{Enum, Eq}, {}, {Ord}, {Outputable, Ord}].
     * The ordering of the sets is alphabetical (by fully qualified type class name).  
     * The constraints do not include implicit superclass constraints. 
     * 
     * For example, for the universal record instance (Eq a) => Eq {a} this would return [{Eq}].
     *     
     * @return SortedSet[] the ith element of the array is the set of all TypeClass constraints on the ith constrained
     *      polymorphic var of the instance type. (T in C-T).
     */
    List<SortedSet<TypeClass>> getDeclaredPolymorphicVarConstraints() {

        if (isUniversalRecordInstance()) {
            List<SortedSet<TypeClass>> varConstraints = new ArrayList<SortedSet<TypeClass>>(1);
            varConstraints.add(((RecordType)instanceType).getPrunedRecordVar().getTypeClassConstraintSet());            
            return varConstraints;
        }              

        //call getType() in order to work with a copy of the type
        TypeConsApp typeConsApp = (TypeConsApp)getType(); 
        final int nVars = typeConsApp.getNArgs();
        
        List<SortedSet<TypeClass>> varConstraints = new ArrayList<SortedSet<TypeClass>>(nVars);
        for (int i = 0; i < nVars; ++i) {            
            TypeVar typeVar = (TypeVar)(typeConsApp.getArg(i));            
            varConstraints.add(typeVar.getTypeClassConstraintSet());
        }
        
        return varConstraints;      
    }   
    
    /**
     * Write this ClassInstance to the RecordOutputStream.
     * @param s
     * @throws IOException
     */
    final void write (RecordOutputStream s) throws IOException {
        s.startRecord(ModuleSerializationTags.CLASS_INSTANCE, serializationSchema);
        
        s.writeModuleName(moduleName);
       
        instanceStyle.write(s);
                
        instanceType.write(s);
        s.writeQualifiedName(typeClass.getName());
        s.writeShortCompressed(instanceMethods.length);
        for (int i = 0; i < instanceMethods.length; ++i) {
            s.writeQualifiedName(instanceMethods[i]);
        }

        // CALDoc comment
        boolean hasCALDocComment = (calDocComment != null);
        s.writeBoolean(hasCALDocComment);
        if (hasCALDocComment) {
            calDocComment.write(s);
        }
        
        // CALDoc comments for instance methods
        // 1) write out a bit vector describing which of the instance methods have associated CALDoc comments
        boolean[] hasMethodCALDocComment = new boolean[methodCALDocComments.length];
        for (int i = 0; i < methodCALDocComments.length; i++) {
            hasMethodCALDocComment[i] = (methodCALDocComments[i] != null);
        }
        
        byte[] caldocFlags = RecordOutputStream.booleanArrayToBitArray(hasMethodCALDocComment);
        s.write(caldocFlags);
        
        // 2) write out the actual comments
        for (final CALDocComment methodCALDocComment : methodCALDocComments) {
            if (methodCALDocComment != null) {
                methodCALDocComment.write(s);
            }            
        }
        
        s.endRecord();
    }
    
    /**
     * Load an instance of ClassInstance from the RecordInputStream.
     * The read position will be before the record header.
     * @param s
     * @param mti
     * @param msgLogger the logger to which to log deserialization messages.
     * @return an instance of ClassInstance.
     * @throws IOException
     */
    static final ClassInstance load (RecordInputStream s, ModuleTypeInfo mti, CompilerMessageLogger msgLogger) throws IOException {
        // Look for Record header.
        RecordHeaderInfo rhi = s.findRecord(ModuleSerializationTags.CLASS_INSTANCE);
        if (rhi == null) {
           throw new IOException("Unable to find ClassInstance record header.");
        }
        DeserializationHelper.checkSerializationSchema(rhi.getSchema(), serializationSchema, mti.getModuleName(), "ClassInstance", msgLogger);

        ModuleName moduleName = s.readModuleName();

        InstanceStyle instanceStyle = InstanceStyle.load(s, msgLogger);
        
        TypeExpr instanceType = TypeExpr.load(s, mti, msgLogger);
        
        QualifiedName typeClassName = s.readQualifiedName();
        TypeClass typeClass = mti.getReachableTypeClass(typeClassName);
        if (typeClass == null) {
            throw new IOException("Unable to resolve TypeClass " + typeClassName + " while loading ClassInstance.");
        }
        
        int nInstanceMethods = s.readShortCompressed();
        QualifiedName instanceMethods[] = new QualifiedName[nInstanceMethods];
        for (int i = 0; i < nInstanceMethods; ++i) {
            QualifiedName qn = s.readQualifiedName();
            instanceMethods[i] = qn;
        }
        
        ClassInstance ci = new ClassInstance(moduleName, instanceType, typeClass, instanceMethods, instanceStyle);
        
        
        // CALDoc comment
        boolean hasCALDocComment = s.readBoolean();
        if (hasCALDocComment) {
            ci.calDocComment = CALDocComment.load(s, mti.getModuleName(), msgLogger);
        }
        
        // CALDoc comments for instance methods
        // 1) read the bit vector describing which of the instance methods have associated CALDoc comments
        int nBytesForCALDocFlags = (nInstanceMethods + 7) / 8;
        byte[] caldocFlags = new byte[nBytesForCALDocFlags];
        for (int i = 0; i < nBytesForCALDocFlags; i++) {
            caldocFlags[i] = s.readByte();
        }
        
        // 2) read the actual comments
        for (int i = 0; i < nInstanceMethods; i++) {
            boolean hasMethodCALDocComment = RecordInputStream.booleanFromBitArray(caldocFlags, i);
            if (hasMethodCALDocComment) {
                ci.methodCALDocComments[i] = CALDocComment.load(s, mti.getModuleName(), msgLogger);
            }            
        }        
        
        s.skipRestOfRecord();
        
        return ci;
    }
}
