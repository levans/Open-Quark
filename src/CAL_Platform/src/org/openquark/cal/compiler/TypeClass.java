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
 * TypeClass.java
 * Created: March 22, 2001
 * By: Bo Ilic
 */
package org.openquark.cal.compiler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.openquark.cal.internal.serialization.ModuleSerializationTags;
import org.openquark.cal.internal.serialization.RecordInputStream;
import org.openquark.cal.internal.serialization.RecordOutputStream;
import org.openquark.cal.internal.serialization.RecordInputStream.RecordHeaderInfo;



/**
 * Models a type class in CAL.
 * <p>
 * Some examples of type classes in CAL are:
 * Prelude.Eq, Prelude.Ord, Prelude.Num, Prelude.Enum, Prelude.Inputable,
 * Prelude.Outputable, Prelude.Appendable and Debug.Show.
 *
 * @author Bo Ilic
 */
public final class TypeClass extends ScopedEntity {

    private static final int serializationSchema = 0;
    
    /** 
     * The immediate superclasses of this type class. The ordering is significant in determining
     * the definitions of the dictionary switching functions and of the dictionary functions.
     */
    private final List<TypeClass> parentTypeClassList;

    /** 
     * The list of class methods that are supported by this class. Note that methods of superclasses
     * are not listed here, even though they are implicitly available. The ordering is significant
     * in determining the definitions of the dictionary functions.
     */
    private final List<ClassMethod> classMethodsList;
    
    /**
     * The kind of the type class. One way to think of this is as the kind of the type class variable.
     * For example, the kind of the Functor class is * -> * and the kind of the Eq class is *.
     * There will be no kind variables in kindExpr.
     */
    private KindExpr kindExpr;
    
    /** 
     * The name of the type class type variable. Can be used to give better error messages. For example, for 
     * class Functor f where ..., this will be "f".
     */ 
    private String typeClassTypeVarName;
    
    /**
     * This comparator is used when working with sorted sets of type classes, where the sort 
     * order is determined by the qualified name of the type class.
     * For example, in the case of the type class constraints on a TypeVar, we need a well-defined
     * order for adding dictionary arguments to the definition of a function whose type depends on
     * constrained type variables.        
     */
    static private final Comparator<TypeClass> COMPARATOR = new Comparator<TypeClass>() {
        /** {@inheritDoc}*/
        public int compare(TypeClass object1, TypeClass object2) {
            return object1.getName().getQualifiedName().compareTo(object2.getName().getQualifiedName());
        }
    };
    
    /** an unmodifiable empty set of type classes, sorted by qualified name. */
    static final SortedSet<TypeClass> NO_CLASS_CONSTRAINTS = Collections.unmodifiableSortedSet(TypeClass.makeNewClassConstraintSet());    
    
        
    /**
     * TypeClass constructor.
     *
     * @param name the name of the type class
     * @param scope the scope of the type class
     * @param typeClassTypeVarName name of the type class type variable parameter. For example, for "class Functor f where..." this is "f".
     */
    TypeClass(QualifiedName name, Scope scope, KindExpr kindExpr, String typeClassTypeVarName) {

        super (name, scope, null);
        
        if (kindExpr == null || typeClassTypeVarName == null) {
            throw new NullPointerException();
        }
        
        this.kindExpr = kindExpr;
        this.typeClassTypeVarName = typeClassTypeVarName;
       
        parentTypeClassList = new ArrayList<TypeClass>();
        classMethodsList = new ArrayList<ClassMethod>();        
    }    
    
    /** Zero argument constructor for serialization. */
    TypeClass() {
        parentTypeClassList = new ArrayList<TypeClass>();
        classMethodsList = new ArrayList<ClassMethod>();        
    }
    
    /**    
     * @return The kind of the type class. One way to think of this is as the kind of the type class variable.
     * For example, the kind of the Functor class is * -> * and the kind of the Eq class is *.
     * There will be no kind variables in kindExpr.
     */
    KindExpr getKindExpr() {
        return kindExpr;
    }
    
    /**
     * Call this method when kind checking for this TypeConstructor is complete and
     * prior to kind checking later dependency groups.
     * The effect is to ground the remaining kind variables by * after kind inference
     * has determined the most general kind of this TypeConstructor. This is because CAL does
     * not support polymorphic kinds.
     */
    void finishedKindChecking() {
        kindExpr = kindExpr.bindKindVariablesToConstant();
    }    
    
    /**
     * Add the class methods in the order that they are declared within the type class. Do not add duplicates!
     * @param classMethod
     */
    void addClassMethod(ClassMethod classMethod) {
        
        if (classMethod == null) {
            throw new NullPointerException("The argument 'classMethod' cannot be null.");
        }

        classMethodsList.add(classMethod);
    }
    
    /**
     * Add the parent classes in the order that they are mentioned in the context of this type class declaration.
     * Do not add duplicates. However, redundant constraints are OK- these will be handled later e.g. (Eq a, Ord a) => Num a,
     * the constraint Eq a is redundant.
     * @param parentTypeClass
     */
    void addParentClass(TypeClass parentTypeClass) {
        
        if (parentTypeClass == null) {
            throw new NullPointerException("The argument 'parentTypeClass' cannot be null.");
        }

        parentTypeClassList.add(parentTypeClass);
    }
    
    /**
     * Returns the ordered list of all ancestors of this TypeClass. The list does not include
     * the class itself. It is ordered in the ordering required for writing dictionary functions.
     * Creation date: (4/4/01 3:11:56 PM)
     * @return List
     */
    List<TypeClass> calculateAncestorClassList() {

        List<TypeClass> ancestorClassList = new ArrayList<TypeClass>();
        calculateAncestorClassList(ancestorClassList);
        return ancestorClassList;
    }
    
    /**
     * Helper function for the public calculateAncestorClassList. The reason for this style of
     * implementation is to limit reallocations of List objects.
     *
     * Creation date: (4/6/01 8:59:36 AM)
     * @param ancestorClassList list of TypeClass objects
     */
    private void calculateAncestorClassList(List<TypeClass> ancestorClassList) {

        for (int i = 0, nParents = getNParentClasses(); i < nParents; ++i) {

            TypeClass parentTypeClass = getNthParentClass(i);
            parentTypeClass.calculateAncestorClassList(ancestorClassList);
            ancestorClassList.add(parentTypeClass);
        }
    } 
    
    /**
     * Provides a deterministic ordering to the ancestors of a type class.    
     * @param ancestorTypeClass
     * @return int the index of the ancestorTypeClass in the list returned by calculateAncestorClassList, or -1 if this is not an ancestor class.
     */
    int getAncestorClassIndex(TypeClass ancestorTypeClass) {
        
        //todoBI this could use a more efficient implementation. However, type class inheritance hierarchies are typically quite small,
        //so we won't bother for now!
                       
        List<TypeClass> ancestorClassList = calculateAncestorClassList();                        
        for (int i = 0, nAncestors = ancestorClassList.size(); i < nAncestors; ++i) {
            TypeClass ancestor = ancestorClassList.get(i);
            if (ancestorTypeClass == ancestor) {
                return i;
            }
        }
        
        return -1;
    }
             
    /**        
     * @param methodN zero-based index.
     * @return ClassMethod the ClassMethod for the given index.
     */
    public ClassMethod getNthClassMethod(int methodN) {
        return classMethodsList.get(methodN);
    }
    
    /**
     * Method getClassMethod.
     * @param classMethodName the unqualified name of the classMethod, assumed to belong to the same module as this type class.
     * @return ClassMethod the ClassMethod or null if there is not one with the given classMethodName.
     */
    public ClassMethod getClassMethod(String classMethodName) {
                
        for (int i = 0, nClassMethods = getNClassMethods(); i < nClassMethods; ++i) {

            ClassMethod classMethod = getNthClassMethod(i);
            if (classMethod.getName().getUnqualifiedName().equals(classMethodName)) {
                return classMethod;
            }
        }

        return null;
    }
    
    /**  
     * Finds the index of the class method in the list of class methods for this type class.
     * The class method can then be retreived with getNthClassMethod.   
     * @param classMethodName name of the class method, assumed to belong to the same module as the type class itself.
     * @return the index of the class method, or -1 if not a class method for this type class.
     */
    int getClassMethodIndex(String classMethodName) {
              
        for (int i = 0, nClassMethods = getNClassMethods(); i < nClassMethods; ++i) {

            ClassMethod classMethod = getNthClassMethod(i);
            if (classMethod.getName().getUnqualifiedName().equals(classMethodName)) {
                return i;
            }
        }

        return -1;
    }     
    
    /**    
     * @return int the number of class methods defined in this TypeClass (does not include superclass methods)
     */
    public int getNClassMethods() {
        return classMethodsList.size();
    }
    
    /**         
     * @return int the number of direct parent classes of this class.
     */
    public int getNParentClasses() {
        return parentTypeClassList.size();
    }
    
    /**         
     * @param n
     * @return TypeClass the nth direct parent class of this class.
     */
    public TypeClass getNthParentClass(int n) {
        return parentTypeClassList.get(n);
    }    
    
    /**
     * Returns true if this TypeClass is the same or a subclass of anotherTypeClass.
     * 
     * Creation date: (3/28/01 1:43:23 PM)
     * @return boolean
     * @param anotherTypeClass
     */
    boolean isSpecializationOf(TypeClass anotherTypeClass) {

        if (this == anotherTypeClass) {
            return true;
        }

        for (int i = 0, nParents = getNParentClasses(); i < nParents; ++i) {

            if (getNthParentClass(i).isSpecializationOf(anotherTypeClass)) {
                return true;
            }
        }

        return false;
    }
    
    /** 
     * For internal compiler use only.
     *   
     * @return true if the class has no superclasses, and only 1 class method. For example,
     *    Prelude.Outputable has only the single class method output.
     */
    public boolean internal_isSingleMethodRootClass() {
        return getNClassMethods() == 1 && getNParentClasses() == 0;
    }
    
    /**     
     * @return The name of the type class type variable. Can be used to give better error messages. For example, for 
     * class Functor f where ..., this will be "f".
     */
    String getTypeClassTypeVarName() {
        return typeClassTypeVarName;
    }
    
    /**
     * Returns an approximation to actual CAL source for a class declaration. Currently, it is
     * only needed for debugging purposes.
     * 
     * @return String
     */
    @Override
    public String toString() {

        StringBuilder result = new StringBuilder(getScope().toString() + " class (");

        int nParents = parentTypeClassList.size();
        if (nParents > 0) {
            
            for (int i = 0; i < nParents; ++i) {
                
                if (i > 0) {                    
                    result.append(", ");
                }                
                result.append(parentTypeClassList.get(i).getName()).append(' ').append(typeClassTypeVarName);
            }
        }

        result.append(") => ").append(getName());
        result.append(' ').append(typeClassTypeVarName).append(" :: ").append(kindExpr).append(" where\n");

        for (int i = 0, nMethods = getNClassMethods(); i < nMethods; ++i) {
            result.append("    ").append(getNthClassMethod(i)).append('\n');
        }
        result.append('\n');

        return result.toString();
    }
    
    /**     
     * @return creates a new empty SortedSet for holding type classes sorted by qualified name.
     *   This set can then be modified by the caller.
     */
    static SortedSet<TypeClass> makeNewClassConstraintSet() {
        return new TreeSet<TypeClass>(TypeClass.COMPARATOR);
    }
    
    /**
     * Removes all those type classes that have child classes in the set from the
     * set. For example, if typeClassConstraintSet = {Eq, Ord, Num, Outputable} then
     * after the call, it would be {Num, Outputable}.     
     * @param typeClassConstraintSet (TypeClass SortedSet)      
     */
    static void removeSuperclassConstraints(SortedSet<TypeClass> typeClassConstraintSet) {
        
        //can't have redundant constraints if there are only 0 or 1 classes in the 
        //constraint set!
        if (typeClassConstraintSet.size() <= 1) {
            return;
        }
        
        Set<TypeClass> superclassSet = new HashSet<TypeClass>();
        
        for (final TypeClass typeClass : typeClassConstraintSet) {
                        
            superclassSet.addAll(typeClass.calculateAncestorClassList());
        }
        
        typeClassConstraintSet.removeAll(superclassSet);
    }    
    
    /**
     * Write this TypeClass instance to the RecordOutputStream.
     * @param s
     * @throws IOException
     */
    @Override
    final void write (RecordOutputStream s) throws IOException {
        s.startRecord(ModuleSerializationTags.TYPE_CLASS, serializationSchema);
        super.writeContent(s);
        
        // parent type classes
        s.writeShortCompressed(parentTypeClassList.size());       
        for (final TypeClass parent : parentTypeClassList) {          
            s.writeQualifiedName(parent.getName());
        }
        
        // class methods
        s.writeShortCompressed(classMethodsList.size());        
        for (final ClassMethod cm : classMethodsList) {            
            cm.write(s);
        }
               
        // kindExpr field
        kindExpr.write(s);
        
        // typeClassTypeVarName field
        s.writeUTF(typeClassTypeVarName);        
        
        s.endRecord();
    }
    
    /**
     * Because type classes can have mutually recursive references we need to
     * do an initial load of the TypeClass objects loading just the members in
     * the base class.  This is enough to build up the map of TypeClass instances
     * for the module and allow resolution of references to other type classes.
     * The TypeClass specific members are loaded by loadFinal().
     * The read position will be before the TypeClass record header.
     * @param s
     * @param mti
     * @param msgLogger the logger to which to log deserialization messages.
     * @return an instance of TypeClass.
     * @throws IOException
     */
    static final TypeClass loadInit(RecordInputStream s, ModuleTypeInfo mti, CompilerMessageLogger msgLogger) throws IOException {
        TypeClass tc = new TypeClass();
        try {
            tc.readInit(s, mti, msgLogger);
        } catch (IOException e) {
            // Add context to error message.
            QualifiedName qn = tc.getName();
            throw new IOException ("Error loading TypeClass " + (qn == null ? "" : qn.getQualifiedName()) + ": " + e.getLocalizedMessage());
        }
        return tc;
    }
    
    /**
     * Load the TypeClass specific members of this TypeClass instance.
     * The read position will be before the TypeClass record header.
     * @param s
     * @param mti
     * @param msgLogger the logger to which to log deserialization messages.
     * @throws IOException
     */
    final void loadFinal(RecordInputStream s, ModuleTypeInfo mti, CompilerMessageLogger msgLogger) throws IOException {
        try {
            readFinal(s, mti, msgLogger);
        } catch (IOException e) {
            // Add context to error message.
            QualifiedName qn = getName();
            throw new IOException ("Error loading TypeClass " + (qn == null ? "" : qn.getQualifiedName()) + ": " + e.getLocalizedMessage());
        }
    }
    
    
    /**
     * Because type classes can have mutually recursive references we need to
     * do an initial load of the TypeClass objects loading just the members in
     * the base class.  This is enough to build up the map of TypeClass instances
     * for the module and allow resolution of references to other type classes.
     * The TypeClass specific members are loaded by readFinal().
     * The read position will be before the TypeClass record header.
     * @param s
     * @param mti    
     * @param msgLogger the logger to which to log deserialization messages.
     * @throws IOException
     */
    private void readInit (RecordInputStream s, ModuleTypeInfo mti, CompilerMessageLogger msgLogger) throws IOException {
        // Look for Record header.
        RecordHeaderInfo rhi = s.findRecord(ModuleSerializationTags.TYPE_CLASS);
        if (rhi == null) {
            throw new IOException("Unable to find TypeClass record header.");
        }
        DeserializationHelper.checkSerializationSchema(rhi.getSchema(), serializationSchema, mti.getModuleName(), "TypeClass", msgLogger);
        
        super.readContent(s, mti, msgLogger);
        
        // Now that we've read/set the name of this TypeClass we can add it to the ModuleTypeInfo.
        mti.addTypeClass(this);
        
        s.skipRestOfRecord();
    }
    
    /**
     * Load the TypeClass specific members of this TypeClass instance.
     * The read position will be before the TypeClass record header.
     * @param s
     * @param mti
     * @param msgLogger the logger to which to log deserialization messages.
     * @throws IOException
     */
    private void readFinal (RecordInputStream s, ModuleTypeInfo mti, CompilerMessageLogger msgLogger) throws IOException {
        // Look for Record header.
        RecordHeaderInfo rhi = s.findRecord(ModuleSerializationTags.TYPE_CLASS);
        if(rhi == null) {
            throw new IOException("Unable to find TypeClass record header.");
        }
        DeserializationHelper.checkSerializationSchema(rhi.getSchema(), serializationSchema, mti.getModuleName(), "TypeClass", msgLogger);
        
        // Now get the record header for the parent members and skip the entire record.
        RecordHeaderInfo parentHeader = s.findRecord(ModuleSerializationTags.SCOPED_ENTITY);
        if(parentHeader == null) {
            throw new IOException("Unable to find ScopedEntityImpl record header while loading TypeClass " + getName() + ".");
        }
        s.skipRestOfRecord();
        
        int nParentTypeClasses = s.readShortCompressed();
        for (int i = 0; i < nParentTypeClasses; ++i) {
            QualifiedName qn = s.readQualifiedName();
            TypeClass parent = mti.getReachableTypeClass(qn);
            // This could be a type from a non-direct dependee module.
            if (parent == null) {
                throw new IOException("Unable to resolve TypeClass " + qn + " while loading TypeClass " + getName());
            }
            
            addParentClass(parent);
        }
        
        int nClassMethods = s.readShortCompressed();
        for (int i = 0; i < nClassMethods; ++i) {
            ClassMethod cm = ClassMethod.load(s, mti, msgLogger);
            addClassMethod(cm);
        }
        
        kindExpr = KindExpr.load(s, mti.getModuleName(), msgLogger);
        
        typeClassTypeVarName = s.readUTF();
        
        s.skipRestOfRecord();
    }
    
}