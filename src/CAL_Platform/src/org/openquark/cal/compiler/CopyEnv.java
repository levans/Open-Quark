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
 * CopyEnv.java
 * Created: July 28, 2000
 * By: Bo Ilic
 */
  
package org.openquark.cal.compiler;

import java.util.HashMap;
import java.util.Map;


/**
 * A helper class used for making copies of type expressions. For example, if a is a generic and
 * b is a non-generic type variable, then a->b->a->b is copied into a'->b->a'->b. In other words,
 * the non-generic variables are shared, but the generic variables are given new instances, which
 * are common throughout the type expression however.
 *
 * <P>Creation date: (July 28, 2000)
 * @author Bo Ilic
 */
class CopyEnv {
    
    /** 
     * (TypeVar -> TypeVar) map from the old TypeVar to the new TypeVar that should
     * replace it in the copied TypeExpr.
     */
    private final Map<TypeVar, TypeVar> typeVarMap;
    
    /**
     * (RecordVar -> RecordVar) map from the old RecordVar to the new RecordVar that should replace
     * it in the copied TypeExpr;
     */
    private final Map<RecordVar, RecordVar> recordVarMap;
      
    /**
     * A helper class used to return 2 arrays of type expressions.
     * @author Bo Ilic
     */
    static class TypesPair {
        private final TypeExpr[] types1;
        private final TypeExpr[] types2;
        
        TypesPair (TypeExpr[] types1, TypeExpr[] types2) {
            if (types1 == null || types2 == null) {
                throw new NullPointerException();
            }
            this.types1 = types1;
            this.types2 = types2;
        }
        
        TypeExpr[] getTypes1() {
            return types1;
        }
        
        TypeExpr[] getTypes2() {
            return types2;
        }
    }    
    
    /**
     * Constucts a CopyEnv.
     *
     * Creation date: (7/28/00 2:36:30 PM)    
     */
    private CopyEnv() {
        this.typeVarMap = new HashMap<TypeVar, TypeVar>();  
        this.recordVarMap = new HashMap<RecordVar, RecordVar>();    
    }      
    
    /**
     * A helper function for freshType.
     *
     * Creation date: (7/28/00 3:00:22 PM)
     * @return TypeExpr
     * @param typeExpr TypeExpr
     * @param nonGenericVars NonGenericVars
     */
    private TypeExpr fresh(TypeExpr typeExpr, NonGenericVars nonGenericVars) {

        if (typeExpr == null) {
            return null;
        }

        typeExpr = typeExpr.prune();

        if (typeExpr instanceof TypeVar) {

            TypeVar typeVar = (TypeVar) typeExpr;

            if (nonGenericVars == null || nonGenericVars.isGenericTypeVar(typeVar)) {
                return freshTypeVar(typeVar);
            } else {
                return typeVar;
            }

        } else if (typeExpr instanceof TypeConsApp) {

            TypeConsApp typeConsApp = (TypeConsApp) typeExpr;

            return new TypeConsApp(typeConsApp.getRoot(), freshArgList(typeConsApp, nonGenericVars));
            
        } else if (typeExpr instanceof TypeApp) {
            
            TypeApp typeApp = (TypeApp)typeExpr;
            return new TypeApp(fresh(typeApp.getOperatorType(), nonGenericVars), fresh(typeApp.getOperandType(), nonGenericVars));
            
        } else if (typeExpr instanceof RecordType) {

            RecordType recordType = (RecordType) typeExpr;
            
            RecordVar recordVar = recordType.getPrunedRecordVar();
            RecordVar newRecordVar;
            if (nonGenericVars == null || nonGenericVars.isGenericRecordVar(recordVar)) {
                newRecordVar = freshRecordVar(recordVar);
            } else {
                newRecordVar = recordVar;
            }           

            Map<FieldName, TypeExpr> hasFieldsMap = recordType.getHasFieldsMap();
            Map<FieldName, TypeExpr> newHasFieldsMap = new HashMap<FieldName, TypeExpr>();

            for (final Map.Entry<FieldName, TypeExpr> entry : hasFieldsMap.entrySet()) {
                
                FieldName hasFieldName = entry.getKey();
                TypeExpr fieldTypeExpr = entry.getValue();

                TypeExpr newFieldTypeExpr = fresh(fieldTypeExpr, nonGenericVars);
                
                newHasFieldsMap.put(hasFieldName, newFieldTypeExpr);
            }
            
            return new RecordType(newRecordVar, newHasFieldsMap);        
        }

        throw new IllegalArgumentException("Programming error in CopyEnv::fresh.");
    }
           
    /**
     * A helper function for freshType.
     * @param typeConsApp
     * @param nonGenericVars
     * @return TypeExpr[]
     */
    private TypeExpr[] freshArgList(TypeConsApp typeConsApp, NonGenericVars nonGenericVars) {
        
        int nArgs = typeConsApp.getNArgs();
        TypeExpr[] args = new TypeExpr[nArgs];
        
        for (int i = 0; i < nArgs; ++i) {
            args[i] = fresh(typeConsApp.getArg(i), nonGenericVars);
        }
        
        return args;               
    }
    
    /**
     * Make a copy of a type expression, duplicating the generic variables, and sharing
     * the non-generic ones.
     *
     * Creation date: (7/31/00 3:55:44 PM)
     * @return TypeExpr
     * @param typeExpr TypeExpr
     * @param nonGenericVars NonGenericVars
     */
    public static TypeExpr freshType(TypeExpr typeExpr, NonGenericVars nonGenericVars) {
        
        if (typeExpr == null) {
            throw new NullPointerException();
        }

        CopyEnv copyEnv = new CopyEnv();
        return copyEnv.fresh(typeExpr, nonGenericVars);
    }
    
    /**
     * Makes a copy of a list of type expressions. The list may share type variables.
     * For example, [a -> (b, c, d), Either a d] is copied to [a' -> (b', c', d'), Either a' d']
     * However, if the nonGenericVars list includes a, then the copy is: [a -> (b', c', d'), Either a d'].
     *
     * @param typePieces
     * @param nonGenericVars
     * @return TypeExpr[]
     */
    public static TypeExpr[] freshTypePieces(TypeExpr[] typePieces, NonGenericVars nonGenericVars) {
        
        CopyEnv copyEnv = new CopyEnv();        
        return copyEnv.freshTypePiecesHelper(typePieces, nonGenericVars);
    }

    public static TypesPair freshTypesPair(TypeExpr[] typePieces1, TypeExpr[] typePieces2, NonGenericVars nonGenericVars) {
        
        CopyEnv copyEnv = new CopyEnv();
        TypeExpr[] copyTypePieces1 = copyEnv.freshTypePiecesHelper(typePieces1, nonGenericVars);
        TypeExpr[] copyTypePieces2 = copyEnv.freshTypePiecesHelper(typePieces2, nonGenericVars);
        return new TypesPair(copyTypePieces1, copyTypePieces2);             
    }        
    
    private TypeExpr[] freshTypePiecesHelper(TypeExpr[] typePieces, NonGenericVars nonGenericVars) {
        int nTypePieces = typePieces.length;
        TypeExpr[] copiedTypePieces = new TypeExpr[nTypePieces];
              
        for (int i = 0; i < nTypePieces; ++i) {
            copiedTypePieces[i] = fresh(typePieces[i], nonGenericVars);
        }
        
        return copiedTypePieces;
    }
     
    /**
     * Try to find typeVar in the copy environment and return the corresponding newTypeVar. If it does
     * not exist, return a new typeVar, and extend the copy environment with the typeVar->newTypeVar
     * mapping.
     *
     * Creation date: (7/28/00 2:49:26 PM)
     * @return TypeVar
     * @param typeVar TypeVar
     */
    private TypeVar freshTypeVar(TypeVar typeVar) {

        TypeVar newTypeVar = typeVarMap.get(typeVar);
        if (newTypeVar != null) {
            return newTypeVar;       
        }
        
        newTypeVar = typeVar.copyUninstantiatedTypeVar();        
      
        typeVarMap.put(typeVar, newTypeVar);

        return newTypeVar;
    }
    
    RecordVar freshRecordVar(RecordVar recordVar) {

        RecordVar newRecordVar = recordVarMap.get(recordVar);
        if (newRecordVar != null) {
            return newRecordVar;       
        }
        
        newRecordVar = recordVar.copyUninstantiatedRecordVar();
        
        recordVarMap.put(recordVar, newRecordVar);

        return newRecordVar;
    }
    
}