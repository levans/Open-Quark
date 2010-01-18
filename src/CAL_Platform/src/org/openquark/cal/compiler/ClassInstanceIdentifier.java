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
 * ClassInstanceName.java
 * Creation date: (Nov 6, 2002)
 * By: Bo Ilic
 */

package org.openquark.cal.compiler;

import java.util.Arrays;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * A helper class for holding onto enough information to easily look up a class instance 
 * given a module context defining the visibility. 
 * <p>
 * For example, for instance Prelude.Ord Prelude.Date, this is Prelude.Ord Prelude.Date.
 * <p> 
 * For instance (Prelude.Eq a, Prelude.Eq b) => Prelude.Eq (Prelude.Tuple2 a b)
 * this is Prelude.Eq Prelude.Tuple2. The point is that we don't need the constraining context because
 * overlapping instances are not allowed. We also don't need the module in which the instance is defined
 * because instance definitions have different scoping rules from most CAL entities and can't be hidden if
 * the module in which the instance is defined is imported either directly or indirectly. 
 * 
 * Creation date (Nov 6, 2002).
 * @author Bo Ilic
 */
public abstract class ClassInstanceIdentifier {
    
    /** a String representation of this ClassInstanceIdentier. */
    private /* final */ transient String instanceIdentifier;
    
    private final QualifiedName typeClassName;     
    
    /**
     * Represents instances whose instance type is rooted in a type constructor.
     * For example,
     * instance Num Int
     * instance Eq a => Eq [a] 
     * instance Enum Ordering
     * 
     * @author Bo Ilic     
     */
    public static class TypeConstructorInstance extends ClassInstanceIdentifier {
        private final QualifiedName typeConsName;
        
        public TypeConstructorInstance (QualifiedName typeClassName, QualifiedName typeConsName) {            
            super (typeClassName);
            
            if (typeConsName == null) {
                throw new NullPointerException();
            }
            
            if (!LanguageInfo.isValidTypeConstructorName(typeConsName.getUnqualifiedName())) {
                throw new IllegalArgumentException();
            }
                        
            this.typeConsName = typeConsName;
        }
        
        public QualifiedName getTypeConsName() {
            return typeConsName;
        }
        
        @Override
        public boolean equals(Object other) {
            
            if (other instanceof TypeConstructorInstance) {
    
                TypeConstructorInstance otherName = (TypeConstructorInstance) other;
                
                return super.typeClassName.equals(otherName.getTypeClassName()) &&
                    typeConsName.equals(otherName.typeConsName);                     
            }
    
            return false;
        } 
        
        @Override
        public String getTypeIdentifier() {
            return typeConsName.getQualifiedName();
        }
    }
    
 
    /**
     * Represents instances whose instance type is a record-polymorphic record.
     *  It is "universal" because the instance definition applies to *all* records whose fields satisfy the specified
     *  constraints i.e. it does not make any restrictions on the allowable field names.
     *  instance Outputable a => Outputable {a}   
     * 
     * @author Bo Ilic     
     */  
    public static class UniversalRecordInstance extends ClassInstanceIdentifier {
        
        private static final String UNIVERSAL = "$UniveralRecord";
        
        public UniversalRecordInstance (QualifiedName typeClassName) {
            super (typeClassName);
        }
        
        @Override
        public boolean equals(Object other) {
            
            if (other instanceof UniversalRecordInstance) {
    
                UniversalRecordInstance otherName = (UniversalRecordInstance) other;
                
                return super.typeClassName.equals(otherName.getTypeClassName());     
            }
    
            return false;
        }
        
        @Override
        public String getTypeIdentifier() {
            return UNIVERSAL;
        }
    }
    
    /**
     * Represents instances whose instance type is a non record-polymorphic record.
     *  It is "ad hoc" in the sense that an explicit set of field names are given for the instance definition
     *  to apply to- and the instance definition does not apply to any other field name set.
     *  instance (Show a, Show b) => Show {#1 :: a, #2 :: b} 
     * 
     * @author Bo Ilic     
     */    
    public static class AdHocRecordInstance extends ClassInstanceIdentifier {
        
        private final SortedSet<FieldName> fieldNames;
        
        private static final String ADHOC = "$AdHocRecord";
        
        public AdHocRecordInstance (QualifiedName typeClassName, SortedSet<FieldName> fieldNames) {
            super (typeClassName);
            if (fieldNames == null) {
                throw new NullPointerException();
            }
            
            this.fieldNames = fieldNames;
        }
        
        @Override
        public boolean equals(Object other) {
            
            if (other instanceof AdHocRecordInstance) {
    
                AdHocRecordInstance otherName = (AdHocRecordInstance) other;
                
                return super.typeClassName.equals(otherName.getTypeClassName()) &&
                    fieldNames.equals(otherName.fieldNames);     
            }
    
            return false;
        }
        
        public SortedSet<FieldName> getFieldNames () {
            return Collections.unmodifiableSortedSet(fieldNames);
        }
        
        @Override
        public String getTypeIdentifier() {
            StringBuilder result = new StringBuilder(ADHOC);
            for (final FieldName fieldName : fieldNames) {
                result.append("$");
                if (fieldName instanceof FieldName.Ordinal) {
                    result.append(((FieldName.Ordinal)fieldName).getOrdinal());
                } else {
                    result.append(fieldName.getCalSourceForm());
                }                
            }
            
            return result.toString();
        }        
    }
    
    private ClassInstanceIdentifier(QualifiedName typeClassName) {
        if (typeClassName == null) {
            throw new NullPointerException();
        }
        
        if (!LanguageInfo.isValidTypeClassName(typeClassName.getUnqualifiedName())) {
            throw new IllegalArgumentException();
        }
                      
        this.typeClassName = typeClassName;        
    }
    
    static ClassInstanceIdentifier make(ClassInstance classInstance) {
        
        if (classInstance.isTypeConstructorInstance()) {
            
            return new TypeConstructorInstance(classInstance.getTypeClass().getName(),
                ((TypeConsApp)classInstance.getType()).getName());
                
        } 
        
        if (classInstance.isUniversalRecordInstance()) {
            return new UniversalRecordInstance(classInstance.getTypeClass().getName());
        }
                      
        throw new IllegalArgumentException();             
    }
    
    static ClassInstanceIdentifier make(QualifiedName typeClassName, TypeExpr instanceType) {
        if (instanceType instanceof RecordType) {
            
            RecordType recordType = (RecordType)instanceType;
            if (recordType.isRecordPolymorphic()) {
                return new UniversalRecordInstance(typeClassName);
            }
            
            return new AdHocRecordInstance(typeClassName, recordType.getHasFields());            
        }
        
        if (instanceType instanceof TypeConsApp) {
            return new TypeConstructorInstance(typeClassName, ((TypeConsApp)instanceType).getName());
        }
        
        throw new IllegalArgumentException();        
    }
    
    public static ClassInstanceIdentifier make(QualifiedName typeClassName, String typeIdentifier) {
       
        if (typeIdentifier.equals(UniversalRecordInstance.UNIVERSAL)) {
            return new UniversalRecordInstance(typeClassName);
        }
            
        if (typeIdentifier.startsWith(AdHocRecordInstance.ADHOC)) {
                                               
            String[] fieldNameStrings = typeIdentifier.substring(AdHocRecordInstance.ADHOC.length()).split("$");
            int nFields = fieldNameStrings.length;
            FieldName[] fieldNames = new FieldName[nFields];
            for (int i = 0; i < nFields; ++i) {
                
                String fieldNameString = fieldNameStrings[i];
                char firstChar = fieldNameString.charAt(0);
                if (firstChar >= '1' && firstChar <= '9') {
                    fieldNameString = "#" + fieldNameString;
                }
                
                FieldName fieldName = FieldName.make(fieldNameString);
                if (fieldName == null) {
                    //invalid field name
                    throw new IllegalArgumentException("invalid field name.");
                }
                
                fieldNames[i] = fieldName;
                
                if (i > 0) {
                    if (fieldNames[i-1].compareTo(fieldName) >= 0) {
                        //fieldNames must be ordered in FieldName order with no duplicates
                        throw new IllegalArgumentException("fieldNames must be ordered in FieldName order with no duplicates");
                    }
                }
            }
            
            SortedSet<FieldName> fieldNamesSet = new TreeSet<FieldName>(Arrays.asList(fieldNames));
            
            return new AdHocRecordInstance(typeClassName, fieldNamesSet);
        }
                              
        QualifiedName typeConsName = QualifiedName.makeFromCompoundName(typeIdentifier);
        return new TypeConstructorInstance(typeClassName, typeConsName);            
    }   
    
    /**
     * @return a canonical form of the class instance identifier, defined to be:
     *    for type constructor instances:
     *    <qualified class name><#><qualified type cons name> 
     *    e.g. Prelude.Eq#Prelude.Maybe
     *    
     *    for universal record instances:
     *    <qualified class name><#><$UniversalRecord> 
     *    e.g. Prelude.Ord#$UniversalRecord
     * 
     *    for ad hoc record instances
     *    <qualified class name><#><$AdHocRecord><$fieldName1><$fieldName2>...<$fieldNameN>
     *    where the field names are in FieldName order, and the '#' is dropped form ordinal
     *    fieldNames.
     *    e.g. Prelude.Foo#$AdHocRecord$1$2$date$quantity
     */
    public String getInstanceIdentifier() {
        if (instanceIdentifier == null) {
            instanceIdentifier = new StringBuilder(typeClassName.getQualifiedName()).append('#').append(getTypeIdentifier()).toString();
        }
                
        return instanceIdentifier;
    }     
    
    /**
     * @return the name of the type class this instance is for
     */
    public QualifiedName getTypeClassName() {
        return typeClassName;
    }
    
    /**     
     * @return a canonical form of the type of this identifier. Note this String does not
     *  include information about the type class only the type.
     *    for type constructor instances:
     *    <qualified type cons name> 
     *    e.g. Prelude.Maybe
     *    
     *    for universal record instances:
     *    <$UniversalRecord> 
     *    e.g. $UniversalRecord
     * 
     *    for ad hoc record instances
     *    <$AdHocRecord><$fieldName1><$fieldName2>...<$fieldNameN>
     *    where the field names are in FieldName order, and the '#' is dropped form ordinal
     *    fieldNames.
     *    e.g. $AdHocRecord$1$2$date$quantity     
     */
    public abstract String getTypeIdentifier();
       
    
    @Override
    public String toString() {
         return getInstanceIdentifier();
    }
    
    @Override
    public abstract boolean equals(Object other);
    
    @Override
    public int hashCode() {
        return getInstanceIdentifier().hashCode();
    }       
}
