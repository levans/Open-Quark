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
 * JavaOperator.java
 * Creation date: Sep 10, 2003.
 * By: Edward Lam
 */
package org.openquark.cal.internal.javamodel;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


/**
 * An enum class to provide object representations of symbolic Java operators.
 * 
 * @author Edward Lam
 */
public final class JavaOperator {
    
    // Unary arithmetic operator: -     ("+" not supported for now)
    public static final JavaOperator NEGATE_INT =           new JavaOperator("-", JavaTypeName.INT);
    public static final JavaOperator NEGATE_DOUBLE =        new JavaOperator("-", JavaTypeName.DOUBLE);
    public static final JavaOperator NEGATE_FLOAT =         new JavaOperator("-", JavaTypeName.FLOAT);
    public static final JavaOperator NEGATE_LONG =          new JavaOperator("-", JavaTypeName.LONG);       
    
    // Binary arithmetic operators: + - * / %    
    public static final JavaOperator ADD_INT =              new JavaOperator("+", JavaTypeName.INT);
    public static final JavaOperator ADD_DOUBLE =           new JavaOperator("+", JavaTypeName.DOUBLE);
    public static final JavaOperator ADD_LONG =             new JavaOperator("+", JavaTypeName.LONG);
    public static final JavaOperator ADD_FLOAT =            new JavaOperator("+", JavaTypeName.FLOAT);
    
    public static final JavaOperator SUBTRACT_INT =         new JavaOperator("-", JavaTypeName.INT);
    public static final JavaOperator SUBTRACT_DOUBLE =      new JavaOperator("-", JavaTypeName.DOUBLE);
    public static final JavaOperator SUBTRACT_LONG =        new JavaOperator("-", JavaTypeName.LONG);
    public static final JavaOperator SUBTRACT_FLOAT =       new JavaOperator("-", JavaTypeName.FLOAT);
    
    public static final JavaOperator MULTIPLY_INT =         new JavaOperator("*", JavaTypeName.INT);
    public static final JavaOperator MULTIPLY_DOUBLE =      new JavaOperator("*", JavaTypeName.DOUBLE);
    public static final JavaOperator MULTIPLY_LONG =        new JavaOperator("*", JavaTypeName.LONG);
    public static final JavaOperator MULTIPLY_FLOAT =       new JavaOperator("*", JavaTypeName.FLOAT);
    
    public static final JavaOperator DIVIDE_INT =           new JavaOperator("/", JavaTypeName.INT);
    public static final JavaOperator DIVIDE_DOUBLE =        new JavaOperator("/", JavaTypeName.DOUBLE);
    public static final JavaOperator DIVIDE_LONG =          new JavaOperator("/", JavaTypeName.LONG);
    public static final JavaOperator DIVIDE_FLOAT =         new JavaOperator("/", JavaTypeName.FLOAT);    
    
    public static final JavaOperator REM_INT =              new JavaOperator("%", JavaTypeName.INT);  
    public static final JavaOperator REM_LONG =             new JavaOperator("%", JavaTypeName.LONG);
    public static final JavaOperator REM_DOUBLE =           new JavaOperator("%", JavaTypeName.DOUBLE);     
    public static final JavaOperator REM_FLOAT =            new JavaOperator("%", JavaTypeName.FLOAT);                
    
    // Relational operators: > >= < <= == !=
    public static final JavaOperator GREATER_THAN_INT =             new JavaOperator(">", JavaTypeName.INT);
    public static final JavaOperator GREATER_THAN_DOUBLE =          new JavaOperator(">", JavaTypeName.DOUBLE);
    public static final JavaOperator GREATER_THAN_LONG =            new JavaOperator(">", JavaTypeName.LONG);
    public static final JavaOperator GREATER_THAN_CHAR =            new JavaOperator(">", JavaTypeName.CHAR);
    public static final JavaOperator GREATER_THAN_EQUALS_INT =      new JavaOperator(">=", JavaTypeName.INT);
    public static final JavaOperator GREATER_THAN_EQUALS_DOUBLE =   new JavaOperator(">=", JavaTypeName.DOUBLE);
    public static final JavaOperator GREATER_THAN_EQUALS_LONG =     new JavaOperator(">=", JavaTypeName.LONG);
    public static final JavaOperator GREATER_THAN_EQUALS_CHAR =     new JavaOperator(">=", JavaTypeName.CHAR);
    public static final JavaOperator LESS_THAN_INT =                new JavaOperator("<", JavaTypeName.INT);
    public static final JavaOperator LESS_THAN_DOUBLE =             new JavaOperator("<", JavaTypeName.DOUBLE);
    public static final JavaOperator LESS_THAN_LONG =               new JavaOperator("<", JavaTypeName.LONG);
    public static final JavaOperator LESS_THAN_CHAR =               new JavaOperator("<", JavaTypeName.CHAR);
    public static final JavaOperator LESS_THAN_EQUALS_INT =         new JavaOperator("<=", JavaTypeName.INT);
    public static final JavaOperator LESS_THAN_EQUALS_DOUBLE =      new JavaOperator("<=", JavaTypeName.DOUBLE);
    public static final JavaOperator LESS_THAN_EQUALS_LONG =        new JavaOperator("<=", JavaTypeName.LONG);
    public static final JavaOperator LESS_THAN_EQUALS_CHAR =        new JavaOperator("<=", JavaTypeName.CHAR);
    public static final JavaOperator EQUALS_INT =                   new JavaOperator("==", JavaTypeName.INT);
    public static final JavaOperator EQUALS_DOUBLE =                new JavaOperator("==", JavaTypeName.DOUBLE);
    public static final JavaOperator EQUALS_LONG =                  new JavaOperator("==", JavaTypeName.LONG);
    public static final JavaOperator EQUALS_CHAR =                  new JavaOperator("==", JavaTypeName.CHAR);
    public static final JavaOperator EQUALS_OBJECT =                new JavaOperator("==", JavaTypeName.OBJECT);
    public static final JavaOperator NOT_EQUALS_INT =               new JavaOperator("!=", JavaTypeName.INT);
    public static final JavaOperator NOT_EQUALS_DOUBLE =            new JavaOperator("!=", JavaTypeName.DOUBLE);
    public static final JavaOperator NOT_EQUALS_LONG =              new JavaOperator("!=", JavaTypeName.LONG);
    public static final JavaOperator NOT_EQUALS_CHAR =              new JavaOperator("!=", JavaTypeName.CHAR);
    public static final JavaOperator NOT_EQUALS_OBJECT =            new JavaOperator("!=", JavaTypeName.OBJECT);
    
    public static final JavaOperator GREATER_THAN_SHORT =           new JavaOperator(">", JavaTypeName.SHORT);
    public static final JavaOperator GREATER_THAN_EQUALS_SHORT =    new JavaOperator(">=", JavaTypeName.SHORT);
    public static final JavaOperator LESS_THAN_SHORT =              new JavaOperator("<", JavaTypeName.SHORT);
    public static final JavaOperator LESS_THAN_EQUALS_SHORT =       new JavaOperator("<=", JavaTypeName.SHORT);
    public static final JavaOperator EQUALS_SHORT =                 new JavaOperator("==", JavaTypeName.SHORT);
    public static final JavaOperator NOT_EQUALS_SHORT =             new JavaOperator("!=", JavaTypeName.SHORT);
    
    public static final JavaOperator GREATER_THAN_BYTE =            new JavaOperator(">", JavaTypeName.BYTE);
    public static final JavaOperator GREATER_THAN_EQUALS_BYTE =     new JavaOperator(">=", JavaTypeName.BYTE);
    public static final JavaOperator LESS_THAN_BYTE =               new JavaOperator("<", JavaTypeName.BYTE);
    public static final JavaOperator LESS_THAN_EQUALS_BYTE =        new JavaOperator("<=", JavaTypeName.BYTE);
    public static final JavaOperator EQUALS_BYTE =                  new JavaOperator("==", JavaTypeName.BYTE);
    public static final JavaOperator NOT_EQUALS_BYTE =              new JavaOperator("!=", JavaTypeName.BYTE);
    
    public static final JavaOperator GREATER_THAN_FLOAT =           new JavaOperator(">", JavaTypeName.FLOAT);
    public static final JavaOperator GREATER_THAN_EQUALS_FLOAT =    new JavaOperator(">=", JavaTypeName.FLOAT);
    public static final JavaOperator LESS_THAN_FLOAT =              new JavaOperator("<", JavaTypeName.FLOAT);
    public static final JavaOperator LESS_THAN_EQUALS_FLOAT =       new JavaOperator("<=", JavaTypeName.FLOAT);
    public static final JavaOperator EQUALS_FLOAT =                 new JavaOperator("==", JavaTypeName.FLOAT);
    public static final JavaOperator NOT_EQUALS_FLOAT =             new JavaOperator("!=", JavaTypeName.FLOAT);
    
    
    // Logical operators: & | ^ ! && ||
    public static final JavaOperator LOGICAL_NEGATE =               new JavaOperator("!", JavaTypeName.BOOLEAN);     // unary
    public static final JavaOperator CONDITIONAL_AND =              new JavaOperator("&&", JavaTypeName.BOOLEAN);
    public static final JavaOperator CONDITIONAL_OR =               new JavaOperator("||", JavaTypeName.BOOLEAN);
    
    // binary bitwise operators: & | ^
    public static final JavaOperator BITWISE_AND_INT =              new JavaOperator ("&", JavaTypeName.INT);
    public static final JavaOperator BITWISE_OR_INT =               new JavaOperator ("|", JavaTypeName.INT);
    public static final JavaOperator BITWISE_XOR_INT =              new JavaOperator ("^", JavaTypeName.INT);
    public static final JavaOperator SHIFTL_INT =                   new JavaOperator ("<<", JavaTypeName.INT);
    public static final JavaOperator SHIFTR_INT =                   new JavaOperator (">>", JavaTypeName.INT);
    public static final JavaOperator SHIFTR_UNSIGNED_INT =          new JavaOperator (">>>", JavaTypeName.INT);

    public static final JavaOperator BITWISE_AND_LONG =              new JavaOperator ("&", JavaTypeName.LONG);
    public static final JavaOperator BITWISE_OR_LONG =               new JavaOperator ("|", JavaTypeName.LONG);
    public static final JavaOperator BITWISE_XOR_LONG =              new JavaOperator ("^", JavaTypeName.LONG);
    public static final JavaOperator SHIFTL_LONG =                   new JavaOperator ("<<", JavaTypeName.LONG);
    public static final JavaOperator SHIFTR_LONG =                   new JavaOperator (">>", JavaTypeName.LONG);
    public static final JavaOperator SHIFTR_UNSIGNED_LONG =          new JavaOperator (">>>", JavaTypeName.LONG);
    
    // unary bit operator ~
    public static final JavaOperator COMPLEMENT_INT =                new JavaOperator ("~", JavaTypeName.INT);
    public static final JavaOperator COMPLEMENT_LONG =               new JavaOperator ("~", JavaTypeName.LONG);
    
    // ternary operator: ?:
    //  note: can't use string form in source.  Also, operands can be any object or primitive (as long as their types match).
    public static final JavaOperator TERNARY =                      new JavaOperator("?:", JavaTypeName.OBJECT);
    
    // string concatenation: +
    public static final JavaOperator STRING_CONCATENATION =         new JavaOperator("+", JavaTypeName.STRING);    
    
    /** Set of String representing arithmetic symbols
     * Since this set is only mutated by code in the static initialization block
     * it isn't synchronized.  If, however, the map is ever mutated by code outside
     * the static initilization block it will need to be synchronized.
     */
    private static Set<String> arithmeticSymbols = new HashSet<String>();

    /** Set of String representing relational symbols
     * Since this set is only mutated by code in the static initialization block
     * it isn't synchronized.  If, however, the map is ever mutated by code outside
     * the static initilization block it will need to be synchronized.
     */
    private static Set<String> relationalSymbols = new HashSet<String>();

    /** Set of String representing logical symbols
     * Since this set is only mutated by code in the static initialization block
     * it isn't synchronized.  If, however, the map is ever mutated by code outside
     * the static initilization block it will need to be synchronized.
     */
    private static Set<String> logicalSymbols = new HashSet<String>();

    /** Set of String representing bit operation symbols
     * Since this set is only mutated by code in the static initialization block
     * it isn't synchronized.  If, however, the map is ever mutated by code outside
     * the static initilization block it will need to be synchronized.
     */
    private static Set<String> bitOpSymbols = new HashSet<String>();
    
    static { 
        
        //warning: do not add ++ or -- here. Because of their side effects, they are not
        //true operators and should not be treated as subclasses of JavaExpression.OperatorExpression.    
        
        arithmeticSymbols.addAll(Arrays.asList(new String[] {"+", "-", "*", "/", "%"}));
        relationalSymbols.addAll(Arrays.asList(new String[] {">", ">=", "<", "<=", "==", "!="}));
        logicalSymbols.addAll(Arrays.asList(new String[] {"!", "&&", "||"}));
        bitOpSymbols.addAll (Arrays.asList (new String[]{"&", "|", "^", "~", "<<", ">>", ">>>"}));
        
        GREATER_THAN_INT.notComposedOperator = LESS_THAN_EQUALS_INT;
        GREATER_THAN_EQUALS_INT.notComposedOperator = LESS_THAN_INT;
        LESS_THAN_INT.notComposedOperator = GREATER_THAN_EQUALS_INT;
        LESS_THAN_EQUALS_INT.notComposedOperator = GREATER_THAN_INT;
        EQUALS_INT.notComposedOperator = NOT_EQUALS_INT;
        NOT_EQUALS_INT.notComposedOperator = EQUALS_INT;
        
        GREATER_THAN_CHAR.notComposedOperator = LESS_THAN_EQUALS_CHAR;
        GREATER_THAN_EQUALS_CHAR.notComposedOperator = LESS_THAN_CHAR;
        LESS_THAN_CHAR.notComposedOperator = GREATER_THAN_EQUALS_CHAR;
        LESS_THAN_EQUALS_CHAR.notComposedOperator = GREATER_THAN_CHAR;
        EQUALS_CHAR.notComposedOperator = NOT_EQUALS_CHAR;
        NOT_EQUALS_CHAR.notComposedOperator = EQUALS_CHAR;
        
        GREATER_THAN_LONG.notComposedOperator = LESS_THAN_EQUALS_LONG;
        GREATER_THAN_EQUALS_LONG.notComposedOperator = LESS_THAN_LONG;
        LESS_THAN_LONG.notComposedOperator = GREATER_THAN_EQUALS_LONG;
        LESS_THAN_EQUALS_LONG.notComposedOperator = GREATER_THAN_LONG;
        EQUALS_LONG.notComposedOperator = NOT_EQUALS_LONG;
        NOT_EQUALS_LONG.notComposedOperator = EQUALS_LONG;
        
        //do not add the double comparison operators here!!! They don't work for NAN.
        EQUALS_DOUBLE.notComposedOperator = NOT_EQUALS_DOUBLE;
        NOT_EQUALS_DOUBLE.notComposedOperator = EQUALS_DOUBLE;
        
        EQUALS_OBJECT.notComposedOperator = NOT_EQUALS_OBJECT;
        NOT_EQUALS_OBJECT.notComposedOperator = EQUALS_OBJECT;       
    }      
    
    /** The type of the values operated on by the operator. */
    private final JavaTypeName valueType;
    
    /** The symbol for the operator used in source. */
    private final String symbol; 
    
    /** the operator that you get composing this operator with null, or null if there is none such */
    private JavaOperator notComposedOperator;
    
    /**
     * Constructor for a JavaOperator
     * @param symbol the symbol for the operator used in source.
     * @param valueType the type of the operator.
     */
    private JavaOperator(String symbol, JavaTypeName valueType) {
        if (symbol == null || valueType == null) {
            throw new IllegalArgumentException ("Unable to create JavaOperator: null argument.");
        }
        this.valueType = valueType;
        this.symbol = symbol;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "JavaOperator: " + symbol + ".  Value type: " + valueType;
    }
    
    /**
     * Get the symbol for the operator used in source.
     * @return String
     */
    public String getSymbol() {
        return symbol;
    }
    
    /**
     * @return JavaTypeName The type of the values operated on by the operator.
     */
    public JavaTypeName getValueType() {
        return valueType;
    }

    /**
     * Return whether this operator is an arithmetic operator.
     * @return boolean
     */
    public boolean isArithmeticOp() {
        return arithmeticSymbols.contains(symbol) && !valueType.equals(JavaTypeName.STRING);
    }
    
    /**
     * Return whether this operator is a relational operator.
     * @return boolean
     */
    public boolean isRelationalOp() {
        return relationalSymbols.contains(symbol);
    }
    
    /**
     * Return whether this operator is a logical operator.
     * @return boolean
     */
    public boolean isLogicalOp() {
        return logicalSymbols.contains(symbol);
    }
    
    /**
     * Return whether this operator is a logical operator.
     * @return boolean
     */
    public boolean isBitOp () {
        return bitOpSymbols.contains(symbol);
    }
    
    /**
     * For example, the notComposed operator for LESS_THAN_INT is GREATER_THAN_EQUALS since
     * not (x < y) is equals to x >= y for all x, y that are ints.
     *
     * It is not the case that the notComposed operator for LESS_THAN_DOUBLE is GREATER_THAN_DOUBLE since
     * the above equation does not hold for NAN. For example, not (NAN < 0.0) is true while NAN >= 0.0 is false.
     * 
     * @return the operator composed with not, or null if there is no such operator.
     */
    public JavaOperator getNotComposedOperator() {
        return notComposedOperator;      
    }
    
}
