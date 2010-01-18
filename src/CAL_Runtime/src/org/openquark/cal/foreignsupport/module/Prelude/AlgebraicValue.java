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
 * AlgebraicCALValue.java
 * Creation date (Aug 1, 2002).
 * By: Bo Ilic
 */
package org.openquark.cal.foreignsupport.module.Prelude;

import java.util.List;

import org.openquark.cal.compiler.QualifiedName;


/**
 * Base class for supporting CAL types with derived instances of Prelude.Inputable or Prelude.Outputable.
 * Values of such types will output to a Java object of type AlgebraicValue.
 * 
 * @author Bo Ilic
 */
public abstract class AlgebraicValue {
    
    /** CAL name of the data constructor at the root of the value graph. */
    private final QualifiedName dataConstructorName;

    /** index of the data constructor at the root of the value graph. */
    private final int dataConstructorOrdinal;    
       
    private AlgebraicValue(QualifiedName dataConstructorName, int dataConstructorOrdinal) {
        if (dataConstructorName == null) {
            throw new NullPointerException("argument dataConstructorName cannot be null.");
        }
        if (dataConstructorOrdinal < 0) {
            throw new IllegalArgumentException();
        }
            
        
        this.dataConstructorName = dataConstructorName;
        this.dataConstructorOrdinal = dataConstructorOrdinal;
        
    }
    
    /**
     * Zero-argument-datacons factory method for AlgebraicValue objects.
     * 
     * @param dataConstructorName QualifiedName of the data constructor that constructs this value
     * @param dataConstructorOrdinal Index of the data constructor that constructs this value
     * @return An AlgebraicValue object that represents the value returned by the specified data constructor 
     */
    public static AlgebraicValue makeZeroArgumentAlgebraicValue(QualifiedName dataConstructorName, int dataConstructorOrdinal) {
        return new ZeroArgumentAlgebraicValue(dataConstructorName, dataConstructorOrdinal);
    }
    
    /**
     * Zero-argument-datacons factory method for AlgebraicValue objects.
     * 
     * @param dataConstructorName Fully-qualified name of the data constructor (that constructs this value (eg "Cal.Core.Prelude.Just")
     * @param dataConstructorOrdinal Ordinal of the data constructor that constructs this value
     * @return An AlgebraicValue object that represents the value returned by the specified data constructor 
     */
    public static AlgebraicValue makeZeroArgumentAlgebraicValue(String dataConstructorName, int dataConstructorOrdinal) {
        return makeZeroArgumentAlgebraicValue(QualifiedName.makeFromCompoundName(dataConstructorName), dataConstructorOrdinal);
    }
    
    /**
     * Single-argument-datacons factory method for AlgebraicValue objects.
     * 
     * @param dataConstructorName QualifiedName of the data constructor that constructs this value
     * @param dataConstructorOrdinal Index of the data constructor that constructs this value
     * @param argumentValue The single argument to the data constructor
     * @return An AlgebraicValue object that represents the value returned by applying the specified argument to the specified data constructor 
     */
    public static AlgebraicValue makeSingleArgumentAlgebraicValue(QualifiedName dataConstructorName, int dataConstructorOrdinal, Object argumentValue) {
        return new SingleArgumentAlgebraicValue(dataConstructorName, dataConstructorOrdinal, argumentValue);
    }
    
    /**
     * Single-argument-datacons factory method for AlgebraicValue objects.
     * 
     * @param dataConstructorName Fully-qualified name of the data constructor (that constructs this value (eg "Cal.Core.Prelude.Just")
     * @param dataConstructorOrdinal Ordinal of the data constructor that constructs this value
     * @param argumentValue The single argument to the data constructor
     * @return An AlgebraicValue object that represents the value returned by applying the specified argument to the specified data constructor 
     */
    public static AlgebraicValue makeSingleArgumentAlgebraicValue(String dataConstructorName, int dataConstructorOrdinal, Object argumentValue) {
        return makeSingleArgumentAlgebraicValue(QualifiedName.makeFromCompoundName(dataConstructorName), dataConstructorOrdinal, argumentValue);
    }
    
    /**
     * Factory method for general AlgebraicValue objects.
     * 
     * @param dataConstructorName QualifiedName of the data constructor that constructs this value
     * @param dataConstructorOrdinal Index of the data constructor that constructs this value
     * @param argumentValuesList List (Object) of arguments to the data constructor
     * @return An AlgebraicValue object that represents the value returns by applying the specified arguments to the specified data constructor 
     */
    public static AlgebraicValue makeGeneralAlgebraicValue(QualifiedName dataConstructorName, int dataConstructorOrdinal, List<?> argumentValuesList) {
        switch(argumentValuesList.size()) {
        case 0:
            return new ZeroArgumentAlgebraicValue(dataConstructorName, dataConstructorOrdinal);
            
        case 1:
            return new SingleArgumentAlgebraicValue(dataConstructorName, dataConstructorOrdinal, argumentValuesList.get(0));
            
        default:
            return new GeneralAlgebraicValue(dataConstructorName, dataConstructorOrdinal, argumentValuesList);
        }
    }
        
    /**
     * Factory method for general AlgebraicValue objects.
     * 
     * @param dataConstructorName Fully-qualified name of the data constructor (that constructs this value (eg "Cal.Core.Prelude.Just")
     * @param dataConstructorOrdinal Ordinal of the data constructor that constructs this value
     * @param argumentValues List (Object) of arguments to the data constructor
     * @return An AlgebraicValue object that represents the value returned by applying the specified arguments to the specified data constructor 
     */
    public static AlgebraicValue makeGeneralAlgebraicValue(String dataConstructorName, int dataConstructorOrdinal, List<?> argumentValues) {
        return makeGeneralAlgebraicValue(QualifiedName.makeFromCompoundName(dataConstructorName), dataConstructorOrdinal, argumentValues);
    }


    
    /** @return name of the data constructor corresponding to this algebraic value. */
    public final QualifiedName getName() {
        return dataConstructorName;
    }
    
    /** @return name of the data constructor corresponding to this value expanded to a String. */
    public final String getDataConstructorName() {
        return getName().getQualifiedName();
    }
    
    
    /** @return ordinal of the data constructor corresponding to this value */
    public final int getDataConstructorOrdinal() {
        return dataConstructorOrdinal;
    }
    
    /** @return number of arguments that this data constructor is holding. */
    public abstract int getNArguments();
    
    /**
     * @param argN zero-based index to an argument held by this data constructor.
     * @return argument value corresponding the argNth argument.
     */
    public abstract Object getNthArgument(int argN);
    
    /**
     * {@inheritDoc}
     */    
    @Override
    public final String toString() {
         
        int nValues = getNArguments(); 
        if (nValues == 0) {
            return getDataConstructorName();
        }
        
        StringBuilder result = new StringBuilder("(");         
        result.append(getDataConstructorName());

        for (int i = 0; i < nValues; ++i) {           
            
            result.append(' ').append(getNthArgument(i));
        }
        
        result.append(')');
        
        return result.toString();
    }       
    

    /**
     * This class can represent any algebraic value.  For certain special cases
     * (eg, for data constructors that take no arguments) it is less efficient
     * than the more specialized AlgebraicValue subclasses.
     * 
     * Creation date: (Jul 19, 2005)
     * @author Jawright
     */
    private static final class GeneralAlgebraicValue extends AlgebraicValue {        
        
        /** arguments to which the data constructor is applied. */
        private final Object[] argumentValues;
        
        private static final Object[] NO_ARGUMENTS = new Object[0];
        
        /**  
         * @param dataConstructorName CAL name of the data constructor that wraps this value at the outermost level.
         * @param dataConstructorOrdinal the ordinal of the data constructor that wraps this value at the outermost level.
         * @param argumentValuesList arguments to which the data constructor is applied.
         * @throws NullPointerException if dataConstructorName is null.
         */
        public GeneralAlgebraicValue(QualifiedName dataConstructorName, int dataConstructorOrdinal, List<?> argumentValuesList) {
            super (dataConstructorName, dataConstructorOrdinal);
            
            if (argumentValuesList == null) {
                this.argumentValues = NO_ARGUMENTS;
            } else {
                argumentValues = argumentValuesList.toArray(NO_ARGUMENTS);
            }               
        }
                       
        /**
         * {@inheritDoc}
         */
        @Override
        public int getNArguments() {
            return argumentValues.length;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public Object getNthArgument(int argN) {
            return argumentValues[argN];
        }
    }
    
    /**
     * This class can represent any algebraic value whose data constructor does not
     * accept any arguments.
     * 
     * Creation date: (Jul 20, 2005)
     * @author Jawright
     */
    private static final class ZeroArgumentAlgebraicValue extends AlgebraicValue {
       
        /**
         * Construct a ZeroArgumentAlgebraicValue.  Note that no arguments are
         * accepted since the data constructor has no parameters.
         * 
         * @param dataConstructorName Name of the data constuctor
         * @param dataConstructorOrdinal Ordinal of the data constructor
         */
        public ZeroArgumentAlgebraicValue(QualifiedName dataConstructorName, int dataConstructorOrdinal) {
           super(dataConstructorName, dataConstructorOrdinal);
        }
                
        
        /**
         * {@inheritDoc}
         */
        @Override
        public int getNArguments() {
            return 0;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public Object getNthArgument(int n) {
            throw new IndexOutOfBoundsException();
        }
    }

    /**
     * This class can represent any algebraic value whose data constructor accepts
     * exactly 1 parameter.
     * 
     * Creation date: (Jul 20, 2005)
     * @author Jawright
     */
    private static final class SingleArgumentAlgebraicValue extends AlgebraicValue {
             
        private final Object argumentValue;
        
        /**
         * Construct a SingleArgumentAlgebraicValue.
         * 
         * @param dataConstructorName Name of the data constuctor
         * @param dataConstructorOrdinal Ordinal of the data constructor
         */
        public SingleArgumentAlgebraicValue(QualifiedName dataConstructorName, int dataConstructorOrdinal, Object argumentValue) {
            
            super (dataConstructorName, dataConstructorOrdinal);
            this.argumentValue = argumentValue;
        }
               
        /**
         * {@inheritDoc}
         */
        @Override
        public int getNArguments() {
            return 1;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public Object getNthArgument(int n) {
            
            if(n != 0) {
                throw new IndexOutOfBoundsException();            
            } 
            
            return argumentValue;            
        }
    }     
}
