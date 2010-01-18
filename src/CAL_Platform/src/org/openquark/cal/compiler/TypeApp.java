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
 * TypeApp.java
 * Created: May 11, 2006
 * By: Bo Ilic
 */

package org.openquark.cal.compiler;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.openquark.cal.compiler.SourceModel.TypeExprDefn;
import org.openquark.cal.internal.serialization.ModuleSerializationTags;
import org.openquark.cal.internal.serialization.RecordInputStream;
import org.openquark.cal.internal.serialization.RecordOutputStream;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;


/**
 * Models a type application of two type expressions i.e.
 * <code>operatorType operandType</code>
 * <p>
 * Note that TypeApp was originally introduced to deal with the sorts of types 
 * that arise in the declaration of certain type classes where higher-kinded type variables
 * are involved. For example, the type of the map method of the Functor type class:
 * <pre>
 * map :: Functor f => (a -> b) -> (f a -> f b)
 * </pre>
 * The type application 'f a' does not have a TypeConsApp as its root.
 * <p>
 * One consequence of this is that there is an ambiguity in how types are represented, even not
 * considering instantiated type variables. For example, [a] could be represented as
 * <pre>
 * TypeConsApp Prelude.List (TypeVar a)
 * </pre>
 * or
 * <pre>
 * TypeApp (TypeConsApp Prelude.List) (TypeVar a)
 * </pre> 
 * 
 * @author Bo Ilic
 */
public final class TypeApp extends TypeExpr {
    
    private static final int serializationSchema = 0;
    
    /** the type operatorType in the type application (operatorType operandType). Cannot be null. */
    private TypeExpr operatorType;
    
    /** the type operandType in the type application (operatorType operandType). Cannot be null. */
    private TypeExpr operandType;
        
    TypeApp(TypeExpr operatorType, TypeExpr operandType) {
        if (operatorType == null) {
            throw new NullPointerException("operatorType cannot be null");
        }
        if (operandType == null) {
            throw new NullPointerException("operandType cannot be null");
        }
        
        this.operatorType = operatorType;
        this.operandType = operandType;       
    }
    
    /** private constructor for use by the serialization code only */
    private TypeApp() {          
    }    
    
    /** @return the type operatorType in type application (operatorType operandType). Cannot be null. */
    public TypeExpr getOperatorType() {
        return operatorType;
    }
    
    /** @return the type operandType in type application (operatorType operandType). Cannot be null. */
    public TypeExpr getOperandType() {
        return operandType;
    }

    /** {@inheritDoc} */
    @Override
    public boolean containsTypeExpr(TypeExpr searchTypeExpr) {
        if (this == searchTypeExpr) {
            return true;
        }
        
        return operatorType.containsTypeExpr(searchTypeExpr) ||
            operandType.containsTypeExpr(searchTypeExpr);
    }

    /** {@inheritDoc} */
    @Override
    boolean containsRecordVar(RecordVar searchRecordVar) {
        return operatorType.containsRecordVar(searchRecordVar) ||
            operandType.containsRecordVar(searchRecordVar);               
    }

    /** {@inheritDoc} */
    @Override
    public boolean isPolymorphic() {
        return operatorType.isPolymorphic() || operandType.isPolymorphic();
    }

    /** {@inheritDoc} */
    @Override
    void getGenericClassConstrainedPolymorphicVars(Set<PolymorphicVar> varSet, NonGenericVars nonGenericVars) {
        operatorType.getGenericClassConstrainedPolymorphicVars(varSet, nonGenericVars);
        operandType.getGenericClassConstrainedPolymorphicVars(varSet, nonGenericVars);       
    }

    /** {@inheritDoc} */
    @Override
    void getUninstantiatedTypeVars(Set<TypeVar> varSet) {
        operatorType.getUninstantiatedTypeVars(varSet);
        operandType.getUninstantiatedTypeVars(varSet);      
    }

    /** {@inheritDoc} */
    @Override
    void getUninstantiatedRecordVars(Set<RecordVar> varSet) {
        operatorType.getUninstantiatedRecordVars(varSet);
        operandType.getUninstantiatedRecordVars(varSet);      
    }

    /** {@inheritDoc} */    
    @Override
    public TypeExpr getCorrespondingTypeExpr(TypeExpr correspondingSuperType, TypeExpr typeToFind) {
        if (this == typeToFind) {
            return correspondingSuperType;
        }
        
        TypeApp correspondingTypeApp = correspondingSuperType.rootTypeApp();
        if (correspondingTypeApp == null) {
            return null;
        }
        
        TypeExpr correspondingType = operatorType.getCorrespondingTypeExpr(correspondingTypeApp.operatorType, typeToFind);
        if (correspondingType != null) {
            return correspondingType;
        }
        
        return operandType.getCorrespondingTypeExpr(correspondingTypeApp.operandType, typeToFind);
    }

    /** {@inheritDoc} */    
    @Override
    public int getArity() {   
        
        //recognize the special forms:
        
        //(TypeApp (TypeConsApp Prelude.Function te1) te2)
        //and
        //(TypeApp (TypeApp (TypeConsApp Prelude.Function) te1) te2)     
        
        TypeExpr operatorType = this.getOperatorType().prune();
        
        if (operatorType instanceof TypeConsApp) {
            
            //recognize the form (TypeApp (TypeConsApp Prelude.Function e1) (e2))
            
            TypeConsApp typeConsApp = (TypeConsApp)operatorType;
            
            if (typeConsApp.getName().equals(CAL_Prelude.TypeConstructors.Function) && typeConsApp.getNArgs() == 1) {    
                return 1 + this.getOperandType().getArity();              
            }     
            
        } else if (operatorType instanceof TypeApp) {
            
            //recognize the form (TypeApp (TypeApp (TypeConsApp Prelude.Function) te1) te2)
            
            TypeApp typeApp = (TypeApp)operatorType;
            
            TypeExpr nextOperatorType = typeApp.getOperatorType().prune();
            
            if (nextOperatorType instanceof TypeConsApp) {
                
                TypeConsApp typeConsApp = (TypeConsApp)nextOperatorType;
                
                if (typeConsApp.getName().equals(CAL_Prelude.TypeConstructors.Function) && typeConsApp.getNArgs() == 0) {    
                    return 1 + this.getOperandType().getArity();              
                }                   
            }
        }
                
        return 0;
    }

    /** {@inheritDoc} */    
    @Override
    void patternMatch(TypeExpr anotherTypeExpr, PatternMatchContext patternMatchContext) throws TypeException {
        
        if (anotherTypeExpr instanceof TypeVar) {

            TypeVar anotherTypeVar = (TypeVar) anotherTypeExpr;

            //anotherTypeVar must be uninstantiated.
            if (anotherTypeVar.getInstance()!= null) {
                throw new IllegalArgumentException("TypeApp.patternMatch: programming error.");
            }
            
            //Can't instantiate a type variable from the declared type expression.
            //For example, this error occurs with the following declaration.
            //funnyHead :: [a] -> Prelude.Char;
            //public funnyHead = List.head;
            if (patternMatchContext.isUninstantiableTypeVar(anotherTypeVar)) {
                throw new TypeException("Attempt to instantiate a type variable from the declared type.");
            }            
            
            //can't pattern match a nongeneric type variable to a type involving uninstantiated type variables.
            //For example, for the function:
            //g x = let y :: [a]; y = x; in y;
            //we attempt to instantiate a non-generic type variables (corresponding to x) to [a].
            //It is OK though to instantiate to a concrete type e.g.
            //g x = let y :: [Int]; y = x; in y;  
            NonGenericVars nonGenericVars = patternMatchContext.getNonGenericVars();                
            if (nonGenericVars != null &&
                !nonGenericVars.isGenericTypeVar(anotherTypeVar) &&
                !this.getUninstantiatedTypeVars().isEmpty()) {
                    
                throw new TypeException("Attempt to match a non-generic type variable to a type involving type variables.");
            }            

            // check for infinite types e.g. an attempt to pattern match a to (a->Int).
            if (containsUninstantiatedTypeVar(anotherTypeVar)) {
                throw new TypeException("Type clash: attempt to create an infinite type.");
            }
            
            if (anotherTypeVar.noClassConstraints()) {
                anotherTypeVar.setInstance(this);
                return;
            }  
            
            //type variable with constraints needs more complicated handling
                        
            int nArgs = 1;
            TypeExpr operatorType = getOperatorType().prune();           
            while (operatorType instanceof TypeApp) {
                ++nArgs;
                operatorType = ((TypeApp)operatorType).getOperatorType().prune();                
            }
            
            if (!(operatorType instanceof TypeConsApp)) {
                //for example,
                //patternMatch (TypeApp a b, Eq c => c)
                //will result in a type clash.
                throw new TypeException("Type clash: a constrained type variable cannot match a type application not rooted in a type constructor.");
            }
            
            TypeConsApp rootTypeConsApp = (TypeConsApp)operatorType;
            nArgs += rootTypeConsApp.getNArgs();
            
            TypeExpr[] args = new TypeExpr[nArgs];     
            //fill in the arguments from the chain of TypeApp nodes.
            int currentArgIndex = nArgs - 1;
            operatorType = getOperatorType().prune();
            while (operatorType instanceof TypeApp) {
                TypeApp typeAppOperatorType = (TypeApp)operatorType;
                args[currentArgIndex] = typeAppOperatorType.getOperandType();
                operatorType = typeAppOperatorType.getOperatorType().prune();
                --currentArgIndex;
            }   
            //fill in the renaming arguments from the partially saturated TypeConsApp.
            System.arraycopy(rootTypeConsApp.getArgs(), 0, args, 0, rootTypeConsApp.getNArgs());
            
            TypeConsApp typeConsApp = new TypeConsApp(rootTypeConsApp.getRoot(), args);
            
            typeConsApp.patternMatch(anotherTypeVar, patternMatchContext);                                  
            return;                                     
            
        } else if (anotherTypeExpr instanceof TypeConsApp) {        

            TypeConsApp anotherTypeConsApp = (TypeConsApp) anotherTypeExpr;            
            int nArgs = anotherTypeConsApp.getNArgs();            
            if (nArgs == 0) {
                throw new TypeException("Type clash:  type " + toString() + " is not a specialization of " + anotherTypeConsApp + "."); 
            }                                  
                      
            TypeExpr[] partialArgs = new TypeExpr[nArgs - 1];
            System.arraycopy(anotherTypeConsApp.getArgs(),0, partialArgs, 0, nArgs - 1);
            
            TypeConsApp partialTypeConsApp = new TypeConsApp(anotherTypeConsApp.getRoot(), partialArgs);
            getOperatorType().prune().patternMatch(partialTypeConsApp, patternMatchContext);    
            getOperandType().prune().patternMatch(anotherTypeConsApp.getArg(nArgs - 1).prune(), patternMatchContext);            
            return;
            
        } else if (anotherTypeExpr instanceof TypeApp) {
            
            TypeApp anotherTypeApp = (TypeApp)anotherTypeExpr;
            
            getOperatorType().prune().patternMatch(anotherTypeApp.getOperatorType().prune(), patternMatchContext);
            getOperandType().prune().patternMatch(anotherTypeApp.getOperandType().prune(), patternMatchContext);
            
            return;
                        
        } else if (anotherTypeExpr instanceof RecordType) {
            
            throw new TypeException("Type clash: The type declaration " + toString() + " does not match the record type " + anotherTypeExpr + ".");
        }
        
        throw new IllegalStateException();      
        
    }
    
    /**
     * Given (e1 e2 ... en) represented using TypeApp objects i.e.
     * (TypeApp ... (TypeApp (TypeApp e1 e2) e3 ... en)
     * Then we return the array [e1, e2, ..., en]. Note that e1, ..., en will be pruned and that this method prunes operators
     * in recognizing the form (e1 e2 ... en)
     * @return the expressions being applied (including the leftmost operator) in a chain of TypeApp nodes.
     */
    private TypeExpr[] buildAppExpressions() {
        
        int nArgs = 1;
        TypeExpr operatorType = getOperatorType().prune();           
        while (operatorType instanceof TypeApp) {
            ++nArgs;
            operatorType = ((TypeApp)operatorType).getOperatorType().prune();                
        }                
        TypeExpr[] appExpressions = new TypeExpr[nArgs + 1];    
        
        //fill in the arguments from the chain of TypeApp nodes.
        int currentArgIndex = nArgs;
        operatorType = this;
        while (operatorType instanceof TypeApp) {
            TypeApp typeAppOperatorType = (TypeApp)operatorType;
            appExpressions[currentArgIndex] = typeAppOperatorType.getOperandType();
            operatorType = typeAppOperatorType.getOperatorType().prune();
            --currentArgIndex;
        } 
        //fill in the leftmost operator
        appExpressions[0] = operatorType.prune();
        
        return appExpressions;
    }

    /** {@inheritDoc} */
    @Override
    TypeExpr prune() {        
        return this;
    }
    
    /** {@inheritDoc} */
    @Override
    TypeExpr deepPrune() {
        operatorType = operatorType.deepPrune();
        operandType = operandType.deepPrune();
        return this;
    }

    /** {@inheritDoc} */
    @Override
    TypeExpr normalize() {  
                
        //we attempt to convert an TypeApp chain rooted in a TypeConstructor into a TypeConsApp.
        //this is the normal form.
        
        TypeExpr[] appExprs = buildAppExpressions();       
        TypeExpr rootExpr = appExprs[0];
               
        if (rootExpr instanceof TypeConsApp) {
            TypeConsApp rootTypeConsApp = (TypeConsApp)rootExpr;
            final int nTypeConsArgs = rootTypeConsApp.getNArgs();
            final int nTypeAppArgs = appExprs.length - 1;            
            final int nNormalizedArgs = nTypeConsArgs + nTypeAppArgs;
            TypeExpr[] normalizedArgs = new TypeExpr[nNormalizedArgs];
            for (int i = 0; i < nTypeConsArgs; ++i) {
                normalizedArgs[i] = rootTypeConsApp.getArg(i).normalize();
            }
            for (int i = 0; i < nTypeAppArgs; ++i) {
                normalizedArgs[i + nTypeConsArgs] = appExprs[i + 1].normalize();
            }           
            return new TypeConsApp(rootTypeConsApp.getRoot(), normalizedArgs);                     
        }
                      
        TypeExpr normalizedTypeApp = new TypeApp(appExprs[0].normalize(), appExprs[1].normalize());
        for (int i = 2, nApps = appExprs.length; i < nApps; ++i) {
            normalizedTypeApp = new TypeApp(normalizedTypeApp, appExprs[i].normalize());
            
        }
        return normalizedTypeApp;
    }

    /** {@inheritDoc} */    
    @Override
    public boolean sameType(TypeExpr anotherTypeExpr) {
        
        anotherTypeExpr = anotherTypeExpr.prune();
        
        if (anotherTypeExpr instanceof TypeVar ||
            anotherTypeExpr instanceof RecordType) {
            
            return false;
        }
        
        if (anotherTypeExpr instanceof TypeApp ||
            anotherTypeExpr instanceof TypeConsApp) {
            
            return this.toString().equals(anotherTypeExpr.toString());
        }
            
        throw new IllegalStateException();       
    }

    /** {@inheritDoc} */    
    @Override
    void toSourceText(StringBuilder sb, PolymorphicVarContext polymorphicVarContext, ParenthesizationInfo parenthesizationInfo, ScopedEntityNamingPolicy namingPolicy) {
        //There is an added complexity in representing forms such as
        //(TypeApp Prelude.List Prelude.Int)
        //(TypeApp (TypeConsApp Prelude.Function Prelude.Int) Prelude.Char)
        //where special notation is used in the fully staturated case, but it is not apparent that we are in that case without 
        //examining deeper in the tree.
        
        TypeExpr[] appExprs = buildAppExpressions();       
        TypeExpr rootExpr = appExprs[0];
        
        if (rootExpr instanceof TypeConsApp) {
            TypeConsApp rootTypeConsApp = (TypeConsApp)rootExpr;
            TypeExpr[] augmentedArgs = new TypeExpr[rootTypeConsApp.getNArgs() + appExprs.length - 1];
            System.arraycopy(rootTypeConsApp.getArgs(), 0, augmentedArgs, 0, rootTypeConsApp.getNArgs());
            System.arraycopy(appExprs, 1, augmentedArgs, rootTypeConsApp.getNArgs(), appExprs.length - 1);
            TypeConsApp augmentedRootTypeConsApp = new TypeConsApp(rootTypeConsApp.getRoot(), augmentedArgs);
            augmentedRootTypeConsApp.toSourceText(sb, polymorphicVarContext, parenthesizationInfo, namingPolicy);
            return;
        }
        
        // if here: type var application
        final boolean reallyParenthesize = parenthesizationInfo == ParenthesizationInfo.ARG_OF_TEXTUAL_TYPE_CONS_OR_VAR;
        if (reallyParenthesize) {
            sb.append('(');
        }
        
        rootExpr.toSourceText(sb, polymorphicVarContext, ParenthesizationInfo.NONE, namingPolicy);   
        for (int i = 1, nArgs = appExprs.length; i < nArgs; ++i) {
          
            sb.append(' ');            
            appExprs[i].toSourceText(sb, polymorphicVarContext, ParenthesizationInfo.ARG_OF_TEXTUAL_TYPE_CONS_OR_VAR, namingPolicy);
        }
                       
        if (reallyParenthesize) {
            sb.append(')');
        }                                            
    }

    /** {@inheritDoc} */    
    @Override
    TypeExprDefn makeDefinitionSourceModel(PolymorphicVarContext polymorphicVarContext, ScopedEntityNamingPolicy namingPolicy) {
        
        //There is an added complexity in representing forms such as
        //(TypeApp Prelude.List Prelude.Int)
        //(TypeApp (TypeConsApp Prelude.Function Prelude.Int) Prelude.Char)
        //where special notation is used in the fully staturated case, but it is not apparent that we are in that case without 
        //examining deeper in the tree.
                       
        TypeExpr[] appExprs = buildAppExpressions();       
        TypeExpr rootExpr = appExprs[0];
        final int nAppExprs = appExprs.length; 
        
        if (rootExpr instanceof TypeConsApp) {
            TypeConsApp rootTypeConsApp = (TypeConsApp)rootExpr;
            TypeExpr[] augmentedArgs = new TypeExpr[rootTypeConsApp.getNArgs() + nAppExprs - 1];
            System.arraycopy(rootTypeConsApp.getArgs(), 0, augmentedArgs, 0, rootTypeConsApp.getNArgs());
            System.arraycopy(appExprs, 1, augmentedArgs, rootTypeConsApp.getNArgs(), nAppExprs - 1);
            TypeConsApp augmentedRootTypeConsApp = new TypeConsApp(rootTypeConsApp.getRoot(), augmentedArgs);
            return augmentedRootTypeConsApp.makeDefinitionSourceModel(polymorphicVarContext, namingPolicy);           
        }        
               
        SourceModel.TypeExprDefn[] typeExprs = new SourceModel.TypeExprDefn[nAppExprs];
        for (int i = 0; i < nAppExprs; ++i) {
            typeExprs[i] = appExprs[i].makeDefinitionSourceModel(polymorphicVarContext, namingPolicy);
        }
        
        return SourceModel.TypeExprDefn.Application.make(typeExprs);   
    }

    /** {@inheritDoc} */    
    @Override
    int unifyType(TypeExpr anotherTypeExpr, ModuleTypeInfo contextModuleTypeInfo) throws TypeException {
        
       if (anotherTypeExpr instanceof TypeVar || anotherTypeExpr instanceof TypeConsApp) {
            
            return TypeExpr.unifyType(anotherTypeExpr, this, contextModuleTypeInfo);
            
        } else if (anotherTypeExpr instanceof TypeApp) {
            
            TypeApp anotherTypeApp = (TypeApp)anotherTypeExpr;
            int typeCloseness = 1;
            typeCloseness += TypeExpr.unifyType(operatorType, anotherTypeApp.operatorType, contextModuleTypeInfo);
            typeCloseness += TypeExpr.unifyType(operandType, anotherTypeApp.operandType, contextModuleTypeInfo);
            return typeCloseness;
            
        } else if (anotherTypeExpr instanceof RecordType) {
            throw new TypeException("Type clash: type application " + this + " does not match a record type " + anotherTypeExpr + ".");
        }
       
        throw new IllegalStateException();
    }

    /** {@inheritDoc} */    
    @Override
    public boolean usesForeignType() {       
       return operatorType.usesForeignType() || operandType.usesForeignType();             
    }

    /** {@inheritDoc} */    
    @Override
    void writeActual(RecordOutputStream s, Map<TypeExpr, Short> visitedTypeExpr, Map<RecordVar, Short> visitedRecordVar) throws IOException {
        s.startRecord(ModuleSerializationTags.TYPE_APP, serializationSchema);        
        operatorType.write(s, visitedTypeExpr, visitedRecordVar);
        operandType.write(s, visitedTypeExpr, visitedRecordVar);       
        s.endRecord();
    }
    
    /**
     * Read an instance of TypeAppfrom the RecordInputStream.
     * Read position will be after the record header.
     * @param s
     * @param schema
     * @param mti
     * @param visitedTypeExpr
     * @param visitedRecordVar
     * @param msgLogger the logger to which to log deserialization messages.
     * @return an instance of TypeConsApp.
     * @throws IOException
     */
    final static TypeExpr load (RecordInputStream s, int schema, ModuleTypeInfo mti, Map<Short, TypeExpr> visitedTypeExpr, Map<Short, RecordVar> visitedRecordVar, CompilerMessageLogger msgLogger) throws IOException {
        DeserializationHelper.checkSerializationSchema(schema, serializationSchema, mti.getModuleName(), "TypeApp", msgLogger);
                
        // We now have enough information to create the TypeApp instance and
        // add it to the visitedTypeExpr map.  We must do this before loading the
        // argument types as they may refer to this TypeApp instance.
        TypeApp typeApp = new TypeApp();
        visitedTypeExpr.put(new Short((short)visitedTypeExpr.size()), typeApp);
        
        typeApp.operatorType = TypeExpr.load(s, mti, visitedTypeExpr, visitedRecordVar, msgLogger);
        typeApp.operandType = TypeExpr.load(s, mti, visitedTypeExpr, visitedRecordVar, msgLogger);
        
        s.skipRestOfRecord();
        
        return typeApp;  
    }    

}
