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
 * PreludeTypeConstants.java
 * Created: Dec 6, 2005
 * By: Bo Ilic
 */

package org.openquark.cal.compiler;

import org.openquark.cal.module.Cal.Core.CAL_Prelude;

/**
 * Sometimes client code needs to have access to various TypeExpr objects. In general, given a particular
 * workspace, one can construct a type via TypeChecker.getTypeFromString (or a helper than ends up calling this).
 * This is the general approach, and will always work.
 * <p>
 * However, there are many commonly used types defined in the Prelude module and this class holds useful TypeExpr
 * constants for the convenience of clients.
 * <p>
 * Note that these constants are not *static* constants- a workspace is required to create them.
 * The constants below are always available since:
 * <ol>
 * <li>the Prelude module must be imported into every CAL module
 * <li>the types below are all public in scope
 * <li>the Prelude module is stable so these constants will continue to be available
 * </ol>
 * Creating TypeExpr constants using this class is efficient, as values are re-used when possible.
 * 
 * @author Bo Ilic
 */
public class PreludeTypeConstants {
    
    /** ModuleTypeInfo for the Prelude module. */
    private final ModuleTypeInfo preludeModuleTypeInfo;
    
    /** constant for the Prelude.Char type */
    private TypeExpr CHAR_TYPE; 
    
    /** constant for the Prelude.Boolean type */
    private TypeExpr BOOLEAN_TYPE;    
    
    /** constant for the Prelude.Byte type */
    private TypeExpr BYTE_TYPE;
    
    /** constant for the Prelude.Short type */
    private TypeExpr SHORT_TYPE;
    
    /** constant for the Prelude.Int type */
    private TypeExpr INT_TYPE;
    
    /** constant for the Prelude.Long type */
    private TypeExpr LONG_TYPE;
    
    /** constant for the Prelude.Float type */
    private TypeExpr FLOAT_TYPE;
    
    /** constant for the Prelude.Double type */
    private TypeExpr DOUBLE_TYPE;
    
    /** constant for the Prelude.String type */
    private TypeExpr STRING_TYPE;
    
    /** constant for the Prelude.Integer type */
    private TypeExpr INTEGER_TYPE;
    
    /** constant for the Prelude.Decimal type */
    private TypeExpr DECIMAL_TYPE;
    
    /** constant for the type [Prelude.Char] */
    private TypeExpr CHARLIST_TYPE;
    
    /** constant for the type Prelude.Unit, also known as (). */
    private TypeExpr UNIT_TYPE;
    
    /** for the Prelude.List type */
    private TypeConstructor LIST_TYPE_CONS;
    
    
    /**    
     * @param moduleTypeInfo for the current module.
     */
    public PreludeTypeConstants(ModuleTypeInfo moduleTypeInfo) {  
               
        if (moduleTypeInfo.getModuleName().equals(CAL_Prelude.MODULE_NAME)) {
            this.preludeModuleTypeInfo = moduleTypeInfo;
        } else {            
            this.preludeModuleTypeInfo = moduleTypeInfo.getImportedModule(CAL_Prelude.MODULE_NAME);  
            if (preludeModuleTypeInfo == null) {
                throw new IllegalStateException(moduleTypeInfo.getModuleName() + " does not import the Prelude module.");
            }
        }
    }    
    
    /**     
     * @return TypeExpr constant for the Prelude.Char type.
     */
    public final TypeExpr getCharType() {        
        if (CHAR_TYPE == null) {
            CHAR_TYPE = makeNonParametricPreludeType(CAL_Prelude.TypeConstructors.Char.getUnqualifiedName());
            if (CHAR_TYPE == null) {
                throw new IllegalStateException(CAL_Prelude.TypeConstructors.Char.getQualifiedName() + " type not available.");
            }
        }
        
        return CHAR_TYPE;        
    }
    
    /**     
     * @return TypeExpr constant for the Prelude.Boolean type.
     */    
    public final TypeExpr getBooleanType() {
        if (BOOLEAN_TYPE == null) {
            BOOLEAN_TYPE = makeNonParametricPreludeType(CAL_Prelude.TypeConstructors.Boolean.getUnqualifiedName());
            if (BOOLEAN_TYPE == null) {
                throw new IllegalStateException(CAL_Prelude.TypeConstructors.Boolean.getQualifiedName() + " type not available.");
            }
        }
        
        return BOOLEAN_TYPE;
    }
    
    /**     
     * @return TypeExpr constant for the Prelude.Byte type.
     */    
    public final TypeExpr getByteType() {
        if (BYTE_TYPE == null) {
            BYTE_TYPE = makeNonParametricPreludeType(CAL_Prelude.TypeConstructors.Byte.getUnqualifiedName());
            if (BYTE_TYPE == null) {
                throw new IllegalStateException(CAL_Prelude.TypeConstructors.Byte.getQualifiedName() + " type not available.");
            }
        }
        
        return BYTE_TYPE;
    }
    
    /**     
     * @return TypeExpr constant for the Prelude.Short type.
     */    
    public final TypeExpr getShortType() {
        if (SHORT_TYPE == null) {
            SHORT_TYPE = makeNonParametricPreludeType(CAL_Prelude.TypeConstructors.Short.getUnqualifiedName());
            if (SHORT_TYPE == null) {
                throw new IllegalStateException(CAL_Prelude.TypeConstructors.Short.getQualifiedName() + " type not available.");
            }
        }
        
        return SHORT_TYPE;
    }
    
    /**     
     * @return TypeExpr constant for the Prelude.Int type.
     */    
    public final TypeExpr getIntType() {
        if (INT_TYPE == null) {
            INT_TYPE = makeNonParametricPreludeType(CAL_Prelude.TypeConstructors.Int.getUnqualifiedName());
            if (INT_TYPE == null) {
                throw new IllegalStateException(CAL_Prelude.TypeConstructors.Int.getQualifiedName() + " type not available.");
            }
        }
        
        return INT_TYPE;
    }
    
    /**     
     * @return TypeExpr constant for the Prelude.Long type.
     */    
    public final TypeExpr getLongType() {
        if (LONG_TYPE == null) {
            LONG_TYPE = makeNonParametricPreludeType(CAL_Prelude.TypeConstructors.Long.getUnqualifiedName());
            if (LONG_TYPE == null) {
                throw new IllegalStateException(CAL_Prelude.TypeConstructors.Long.getQualifiedName() + " type not available.");
            }
        }
        
        return LONG_TYPE;
    }
    
    /**     
     * @return TypeExpr constant for the Prelude.Float type.
     */    
    public final TypeExpr getFloatType() {
        if (FLOAT_TYPE == null) {
            FLOAT_TYPE = makeNonParametricPreludeType(CAL_Prelude.TypeConstructors.Float.getUnqualifiedName());
            if (FLOAT_TYPE == null) {
                throw new IllegalStateException(CAL_Prelude.TypeConstructors.Float.getQualifiedName() + " type not available.");
            }  
        }
        
        return FLOAT_TYPE;
    }   
    
    /**     
     * @return TypeExpr constant for the Prelude.Double type.
     */    
    public final TypeExpr getDoubleType() {
        if (DOUBLE_TYPE == null) {
            DOUBLE_TYPE = makeNonParametricPreludeType(CAL_Prelude.TypeConstructors.Double.getUnqualifiedName());
            if (DOUBLE_TYPE == null) {
                throw new IllegalStateException(CAL_Prelude.TypeConstructors.Double.getQualifiedName() + " type not available.");
            } 
        }
        
        return DOUBLE_TYPE;
    } 
    
    /**     
     * @return TypeExpr constant for the Prelude.String type.
     */    
    public final TypeExpr getStringType() {
        if (STRING_TYPE == null) {
            STRING_TYPE = makeNonParametricPreludeType(CAL_Prelude.TypeConstructors.String.getUnqualifiedName());
            if (STRING_TYPE == null) {
                throw new IllegalStateException(CAL_Prelude.TypeConstructors.String.getQualifiedName() + " type not available.");
            } 
        }
        
        return STRING_TYPE;
    }
    
    /**     
     * @return TypeExpr constant for the Prelude.Integer type.
     */    
    public final TypeExpr getIntegerType() {
        if (INTEGER_TYPE == null) {
            INTEGER_TYPE = makeNonParametricPreludeType(CAL_Prelude.TypeConstructors.Integer.getUnqualifiedName());
            if (INTEGER_TYPE == null) {
                throw new IllegalStateException(CAL_Prelude.TypeConstructors.Integer.getQualifiedName() + " type not available.");
            }
        }
        
        return INTEGER_TYPE;
    }  
    
    /**     
     * @return TypeExpr constant for the Prelude.Decimal type.
     */    
    public final TypeExpr getDecimalType() {
        if (DECIMAL_TYPE == null) {
            DECIMAL_TYPE = makeNonParametricPreludeType(CAL_Prelude.TypeConstructors.Decimal.getUnqualifiedName());
            if (DECIMAL_TYPE == null) {
                throw new IllegalStateException(CAL_Prelude.TypeConstructors.Decimal.getQualifiedName() + " type not available.");
            }
        }
        
        return DECIMAL_TYPE;
    } 
    
    /**     
     * @return TypeExpr constant for the [Prelude.Char] type.
     */      
    public final TypeExpr getCharListType() {
        if (CHARLIST_TYPE == null) {
            CHARLIST_TYPE = makeListType(getCharType());
        }
        
        return CHARLIST_TYPE;        
    }
    
    /**     
     * @return TypeExpr constant for the Prelude.Unit type, also known as ().
     */      
    public final TypeExpr getUnitType() {
        if (UNIT_TYPE == null) {
            UNIT_TYPE = makeNonParametricPreludeType(CAL_Prelude.TypeConstructors.Unit.getUnqualifiedName());
            if (UNIT_TYPE == null) {
                throw new IllegalStateException(CAL_Prelude.TypeConstructors.Unit.getQualifiedName() + " type not available.");
            }
        }
        
        return UNIT_TYPE;        
    }
    
    private final TypeConstructor getListTypeCons() {
        if (LIST_TYPE_CONS == null) {
            LIST_TYPE_CONS = preludeModuleTypeInfo.getVisibleTypeConstructor(CAL_Prelude.TypeConstructors.List);
            if (LIST_TYPE_CONS == null) {
                throw new IllegalStateException(CAL_Prelude.TypeConstructors.List.getQualifiedName() + " type not available.");
            }
        }
        
        return LIST_TYPE_CONS;
    }
    
    /**
     * Create a type expression for a list.
     *
     * @param elementTypeExpr the type of the element    
     * @return TypeExpr the type expression for the list with given element type    
     */    
    public final TypeExpr makeListType(TypeExpr elementTypeExpr) {               
        return new TypeConsApp(getListTypeCons(), new TypeExpr [] {elementTypeExpr});        
    }
    
    /**
     * Creates a non-parametric TypeExpr from a given Prelude type constructor name.    
     * When both are applicable, this function will be faster than calling getTypeFromString
     * since it doesn't need to parse a type string.
     *     
     * @param preludeTypeConsName
     * @return TypeExpr for the non-parametric type with type constructor name as supplied. Will be null if the typeCons
     *    name is not-valid or not visible.
     */
    private final TypeExpr makeNonParametricPreludeType(String preludeTypeConsName) {
        TypeConstructor typeCons = preludeModuleTypeInfo.getTypeConstructor(preludeTypeConsName);
        if (typeCons == null) {
            return null;
        }
        return TypeExpr.makeNonParametricType(typeCons);
    }                         
}
