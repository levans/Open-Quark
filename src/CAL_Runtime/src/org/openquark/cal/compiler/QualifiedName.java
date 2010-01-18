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
 * QualifiedName.java
 * Created: July 16, 2001
 * By: Bo Ilic
 */
 
package org.openquark.cal.compiler;

/**
 * A simple wrapper class for holding onto names qualified by their module source.
 * There are also a variety of static constants representing the names of well known
 * Prelude type constructors, data constructors and functions.
 *
 * This class must remain immutable.
 *
 * @author Bo Ilic
 */
public final class QualifiedName implements Name, Comparable<QualifiedName> {
    
    /** the name of the module. Must not be null. */
    private final ModuleName moduleName;
    
    /** the name of the entity, without its module part. Must not be null. */
    private final String unqualifiedName;
           
    /**
     * Constructs a QualifiedName from a module name and an unqualified name.
     * @param moduleName the module name.
     * @param unqualifiedName the unqualified name.
     */
    private QualifiedName(ModuleName moduleName, String unqualifiedName) {
        
        if (moduleName == null) {
            throw new NullPointerException ("QualifiedName constructor: moduleName can't be null.");
        }

        if (unqualifiedName == null) {
            throw new NullPointerException ("QualifiedName constructor: unqualifiedName can't be null.");
        }

        this.moduleName = moduleName;
        this.unqualifiedName = unqualifiedName;
    }

    /**
     * Factory method for constructing a QualifiedName from a module name and an unqualified name.
     * @param moduleName the module name.
     * @param unqualifiedName the unqualified name.
     * @return an instance of QualifiedName.
     */
    public static QualifiedName make(ModuleName moduleName, String unqualifiedName) {
        return new QualifiedName(moduleName, unqualifiedName);
    }
    
    /**
     * Factory method for constructing a QualifiedName from a module name and an unqualified name.
     * @param moduleName the module name.
     * @param unqualifiedName the unqualified name.
     * @return an instance of QualifiedName.
     */
    public static QualifiedName make(String moduleName, String unqualifiedName) {
        return make(ModuleName.make(moduleName), unqualifiedName);
    }

    /**
     * Provides an ordering on QualifiedNames where the module part is compared first, as String values, followed by
     * the unqualified part.
     * @param otherName    
     * @return int     
     */
    public int compareTo(QualifiedName otherName) {

        int cmp = moduleName.compareTo(otherName.moduleName);
        if (cmp != 0) {
            return cmp;
        }

        return unqualifiedName.compareTo(otherName.unqualifiedName);
    }

    /**    
     * @param other 
     * @return boolean true if other is an instance of QualifiedName and both the module and unqualified parts are the same
     *     as that of this QualifiedName.
     */
    @Override
    public boolean equals(Object other) {

        if (other instanceof QualifiedName) {            
            return equals((QualifiedName) other);
        }

        return false;
    }
    
    /**     
     * @param otherName
     * @return true if both the module name and unqualified parts for otherName are that same as that of this QualifiedName.
     */
    public boolean equals(QualifiedName otherName) { 
        if (otherName == null) {
            return false;
        }
        
        if (this == otherName) {
            return true;
        }
        
        return unqualifiedName.equals(otherName.unqualifiedName) && moduleName.equals(otherName.moduleName);        
    }    

    /**
     * {@inheritDoc}
     * @return the module part of the qualified name e.g. for "List.filter" this would be "List".
     */
    public ModuleName getModuleName() {
        return moduleName;
    }

    /**     
     * @return the full name, represented as a string e.g. "List.filter".
     */
    public String getQualifiedName() {
        return moduleName.toSourceText() + '.' + unqualifiedName;
    }

    /**     
     * {@inheritDoc}
     * @return the full name, represented as a string e.g. "List.filter".
     */
    public String toSourceText() {
        return getQualifiedName();
    }

    /**    
     * @return the name, without its module part e.g. "filter".
     */
    public String getUnqualifiedName() {
        return unqualifiedName;
    }
    
    /**    
     * @return int
     */
    @Override
    public int hashCode() {
        
        //Joshua Bloch in Effective Java has a tip on computing hash codes for compound objects.
        //In this case, it reduces to:
        //int result = 17;
        //result = 37*result + unqualifiedName.hashCode();
        //result = 37*result + moduleName.hashCode();
        //return result;
        //
        //this simplifies to:
        //37*(37 * 17 +  unqualifiedName.hashCode()) + moduleName.hasCode();        

        return 37 * (37 * 17 + unqualifiedName.hashCode()) + moduleName.hashCode();
    }

    /**
     * A helper function that makes a QualifiedName from a compound name String.
     * <p>
     * Creation date: (7/16/01 2:34:12 PM)
     * 
     * @param compoundName a name of the form <em>moduleName.unqualifiedName</em>,
     *                     where <em>moduleName</em> can be a hierarchical module name containing
     *                     one or more components separated by '.'.
     * @return a new QualifiedName
     */
    static public QualifiedName makeFromCompoundName(String compoundName) {
        // look for the last dot because the other dots are part of the hierarchical module name
        int periodPos = compoundName.lastIndexOf('.');
        if (periodPos == -1) {
            throw new IllegalArgumentException("QualifiedName.makeFromCompoundName: " + compoundName + " is not a compound name.");
        }

        ModuleName moduleName = ModuleName.make(compoundName.substring(0, periodPos));
        String unqualifiedName = compoundName.substring(periodPos + 1);

        return make(moduleName, unqualifiedName);
    }

    /**
     * Return whether a string is a valid qualified name.
     *
     * @param compoundName a name of the form <em>moduleName.unqualifiedName</em>,
     *                     where <em>moduleName</em> can be a hierarchical module name containing
     *                     one or more components separated by '.'.
     * @return boolean whether the name is a valid compound name.
     */
    static public boolean isValidCompoundName(String compoundName) {

        try {
            makeFromCompoundName(compoundName);
        } catch (IllegalArgumentException iae) {
            return false;
        } catch (NullPointerException npe) {
            return false;
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getQualifiedName();
    }

    /**
     * Return a clone of this object.
     * @return a clone of this QualifiedName
     * @throws CloneNotSupportedException
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        return make(moduleName, unqualifiedName);
    }
    
    /**  
     * Can be used to quickly triage whether a qualified name is in the lowercase namespace 
     * (functions, class methods) or the uppercase namespace (type classes, type constructors, data constructors).
     *    
     * @return true if the unqualified name has a lowercase ascii letter as its first letter. False otherwise.
     */
    public boolean lowercaseFirstChar() {
        return unqualifiedName.length() > 0 && LanguageInfo.isCALVarStart(unqualifiedName.charAt(0));
    }
}