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
 * RefinedField.java
 * Created: 22-Nov-2003 
 * By: Trevor Daw
 */
package org.openquark.cal.foreignsupport.module.UniqueIdentifier;

import org.openquark.cal.compiler.SourceModel;
import org.openquark.cal.compiler.io.InputPolicy;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.module.Cal.Utilities.CAL_UniqueIdentifier;


/**
 * Abstract base class to represent the different CAL UniqueIdentifierTypes
 */
public abstract class RefinedUniqueIdentifier {
       
    /** 
     * Constructor.  Instantiable only by subclasses
     */
    protected RefinedUniqueIdentifier () {}        

    /**
     * Creates and returns a new RefinedUniqueName  
     */
    public static RefinedUniqueIdentifier MakeUniqueName (String name) {
        return new RefinedUniqueName (name); 
    }        

    /**
     * Creates and returns a new RefinedUniqueSofaMember  
     */
    public static RefinedUniqueIdentifier MakeUniqueSofaMember (Object sofaMember) {
        // TODO Sofa support now moved to CAL Experimental Libraries
        /*
        if (sofaMember instanceof SofaMember) {
            return new RefinedUniqueSofaMember ((SofaMember) sofaMember); 
        } else */
        if (sofaMember instanceof RefinedUniqueIdentifier) {
            // TODO this code is little bit hackish, it is tailored to work with certain scenarios
            return (RefinedUniqueIdentifier) sofaMember;
        } else {
            throw new IllegalArgumentException("Unsupported argument type.");
        }
    }        

    /**
     * Tests whether this object is a UniqueName
     */
    abstract public boolean isUniqueName (); 
    
    /**
     * Tests whether this object is a UniqueSofaMember
     */
    abstract public boolean isUniqueSofaMember (); 
    
    /**
     * Returns a unique name string   
     */
    abstract public String getUniqueName ();
    
    /**
     * Class to represent the CAL UniqueName type
     */
    public static class RefinedUniqueName extends RefinedUniqueIdentifier {

        private final String uniqueName; 
        
        /** 
         * Constructor
         */
        public RefinedUniqueName (String uniqueName) {
                this.uniqueName = uniqueName;
        }
        
        /**
         * Tests whether this object is a UniqueName
         */
        @Override
        public boolean isUniqueName () {
                return true;  
        }
    
        /**
         * Tests whether this object is a UniqueSofaMember
         */
        @Override
        public boolean isUniqueSofaMember () {
                return false;  
        }

        /**
         * Returns the unique name 
         * @return String
         */
        @Override
        public String getUniqueName() {
            return uniqueName;        
        }

        /**
         * Two refined unique names are equal if they are both Unique name identifiers their unique names
         * match.  They are also equal if one is a Sofa identifier and one is a unique name identifier, but
         * they have matching unique names.
         * @param obj The object to compare against
         * @see java.lang.Object#equals(Object)
         * @return true if obj is of type RefinedUniqueName and the unique names match or if obj is
         * of type RefinedSofaMember and the unique name and member names match.
         */
        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            
            // The object must be a Sofa identifer and have matching Sofa members, or be a unique name
            // identifier and have matching unique names. 
            if (obj.getClass() == RefinedUniqueName.class) {
                return uniqueName.equals(((RefinedUniqueName)obj).uniqueName);
            } else if (obj instanceof RefinedUniqueIdentifier) {
                return uniqueName.equals(((RefinedUniqueIdentifier)obj).getUniqueName());
            } else {
                return false;
            }
        }

        /**
         * The hash code for a unique name is simply the hash code of its string.  Note that the hash code
         * must match the hash code returned from an equivalent RefinedUniqueSofaMember.
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            return 37 * uniqueName.hashCode(); 
        }

        /**
         * Returns a human readable string.  Useful for debugging and will also be displayed in a
         * value entry panel.
         * @return String
         */
        @Override
        public String toString() {
            // TODO: This used to returned a more detailed string, but since it is currently being used
            // in the UI for value entry panel pick lists it has been changed to a suitable display string
            // Eventually this should probably be changed back to a debugging string
            // return "UniqueName: " + uniqueName;
            return uniqueName;
        }
        
        public InputPolicy getInputPolicy () {
            // The marshaller is: (\x -> UniqueIdentifier.makeUniqueIdentifierByName (Prelude.input x))
            SourceModel.Expr marshaller =
                SourceModel.Expr.Lambda.make(
                    new SourceModel.Parameter[] { SourceModel.Parameter.make("x", false) },
                    CAL_UniqueIdentifier.Functions.makeUniqueIdentifierByName(
                        CAL_Prelude.Functions.input(
                            SourceModel.Expr.Var.makeUnqualified("x"))));
            
            return InputPolicy.makeWithMarshaler(marshaller);
        }
        public Object[] getInputJavaValues() {
            return new Object[]{uniqueName};
        }
        
    }   
}

