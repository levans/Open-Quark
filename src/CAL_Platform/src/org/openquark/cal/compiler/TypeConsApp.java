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
 * TypeCons.java
 * Created: Sep 24, 2003
 * By: Bo Ilic
 */
package org.openquark.cal.compiler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.openquark.cal.internal.serialization.ModuleSerializationTags;
import org.openquark.cal.internal.serialization.RecordInputStream;
import org.openquark.cal.internal.serialization.RecordOutputStream;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;


/**  
 * Models the application of a type constructor (such as Prelude.Boolean or Prelude.Function) to 0 or more type arguments.
 * 
 * The application need not be fully saturated. For example, Prelude.Either could be applied to 0, 1 or 2 arguments (in a
 * well-typed type expression).
 * 
 * @author Bo Ilic
 */
public final class TypeConsApp extends TypeExpr {

    private static final int serializationSchema = 0;
    
    /** The type constructor at the head or root of this application. cannot be null. */
    private TypeConstructor rootTypeCons;
    
    /** 
     * type arguments applied to the rootTypeCons. For example, if this is [e1, e2, ..., en] then the type represented
     * is (((rootTypeCons e1) e2) ... en), or, since type application is left associative, (rootTypeCons e1 e2 ... en).
     * Might not be saturated i.e. args.length might be less than the type arity.
     * Cannot be null. The elements of the args array also cannot be null. 
     */    
    private TypeExpr[] args;
    
    private static final TypeExpr[] NO_ARGS = new TypeExpr[0];

    TypeConsApp(TypeConstructor rootTypeCons, TypeExpr[] args) {

        if (rootTypeCons == null) {
            throw new NullPointerException("TypeConsApp constructor: the argument 'rootTypeCons' cannot be null.");
        }
        this.rootTypeCons = rootTypeCons;
        
        if (args == null || args.length == 0) {
            this.args = TypeConsApp.NO_ARGS;
        } else {
            if (TypeExpr.hasNullType(args)) {         
                throw new NullPointerException();               
            }
            this.args = args;
        }
    }       
    
    /** private constructor used by serialization code. */
    private TypeConsApp(TypeConstructor rootTypeCons, int nArgs) {
        if (rootTypeCons == null) {
            throw new NullPointerException("TypeConsApp constructor: the argument 'rootTypeCons' cannot be null.");
        }
        this.rootTypeCons = rootTypeCons;
        this.args = nArgs != 0 ? new TypeExpr[nArgs] : TypeConsApp.NO_ARGS;
    }

    /**
     * Returns the internal CAL name of the (root) type constuctor. For some of the built-in types that
     * have special syntax in the CAL source code, this name is purely internal.
     * Note for e.g. "Either Int Boolean" or "Either Char Double" this will return "Either".
     * @return the internal CAL name of the type constructor
     */
    public QualifiedName getName() {
        return rootTypeCons.getName();
    }

    /**
     * @return the root type constructor e.g. for "Either Int Boolean" or "Either Char Double" this
     *         would be the type constructor for "Either".
     */
    public TypeConstructor getRoot() {
        return rootTypeCons;
    }
    
    /**
     * Method getArg.
     * @param argN the zero-based index argument number
     * @return TypeExpr the type of the argN type argument
     */
    public TypeExpr getArg(int argN) {

        return args[argN];
    }

    /**         
     * @return the number of type parameters for this type constructor application.
     *     Note that the root type constructor may not be fully saturated
     *     so that e.g. Prelude.Either will not necessarily have 2 type arguments,
     *     even in correctly type checking code.
     */
    public int getNArgs() {
        return args.length;
    }
    
    TypeExpr[] getArgs(){
        return args;
    }
    void setArg(int i, TypeExpr typeExpr) {
        args[i] = typeExpr;
    }

    /** {@inheritDoc} */
    @Override
    TypeExpr prune() {
        return this;
    }
    
    /** {@inheritDoc} */
    @Override
    TypeExpr deepPrune() {

        // Make sure that the args are deep pruned.
        for (int i = 0, nArgs = getNArgs(); i < nArgs; ++i) {
            args[i] = args[i].deepPrune();
        }

        return this;
    }
    
    /** {@inheritDoc} */
    @Override
    TypeExpr normalize() {

        // Make sure that the args are normalized.
        final int nArgs = getNArgs();
        TypeExpr[] normalizedArgs = new TypeExpr[nArgs];
        
        for (int i = 0; i < nArgs; ++i) {
            normalizedArgs[i] = args[i].normalize();
        }

        return new TypeConsApp(rootTypeCons, normalizedArgs);
    }
    
    @Override
    public boolean sameType(TypeExpr anotherTypeExpr) {
        
        anotherTypeExpr = anotherTypeExpr.prune();
        
        if (anotherTypeExpr instanceof TypeConsApp) {
            
            TypeConsApp anotherTypeConstructorApp = (TypeConsApp)anotherTypeExpr; 
            
            if (this.rootTypeCons == anotherTypeConstructorApp.rootTypeCons) {
                
                int thisNArgs = this.getNArgs();
                if (thisNArgs == anotherTypeConstructorApp.getNArgs()) {
                    
                    if (thisNArgs == 0) {
                        return true;
                    }
                    
                    //we've failed with the quick checks. Need a more expensive test.
                    return this.toString().equals(anotherTypeExpr.toString());
                }
                
                //if the 2 type constructors have different numbers of args, they will produce different string representations
                return false;
            }
            
            //if the 2 type constructors have different roots (which can be compared using referential equality) then they are different.
            return false;
        }
        
        if (anotherTypeExpr instanceof TypeVar ||
            anotherTypeExpr instanceof RecordType) {
            
            return false;
        }
        
        if (anotherTypeExpr instanceof TypeApp) {
            //For example,
            //(TypeConsApp List [Int]) is the same type as (TypeApp (TypeConsApp List []) Int)
            //we could determine this a bit more efficiently, but the case of TypeApp is sufficiently rare that it is not 
            //worth bothering.
            return this.toString().equals(anotherTypeExpr.toString());
        }
    
        throw new IllegalStateException();
    }

    /** {@inheritDoc} */
    @Override
    void getGenericClassConstrainedPolymorphicVars(Set<PolymorphicVar> varSet, NonGenericVars nonGenericVars) {

        for (int i = 0, nArgs = getNArgs(); i < nArgs; ++i) {
            getArg(i).getGenericClassConstrainedPolymorphicVars(varSet, nonGenericVars);
        }
    }

    /** {@inheritDoc} */
    @Override
    void getUninstantiatedTypeVars(Set<TypeVar> varSet) {
        for (int i = 0, nArgs = getNArgs(); i < nArgs; ++i) {
            getArg(i).getUninstantiatedTypeVars(varSet);
        }
    }

    /** {@inheritDoc} */
    @Override
    void getUninstantiatedRecordVars(Set<RecordVar> varSet) {
        for (int i = 0, nArgs = getNArgs(); i < nArgs; ++i) {
            getArg(i).getUninstantiatedRecordVars(varSet);
        }
    }
    
    /** {@inheritDoc} */
    @Override
    public TypeExpr getCorrespondingTypeExpr(TypeExpr correspondingSuperType, TypeExpr typeToFind) {
        if (this == typeToFind) {
            return correspondingSuperType;
        }

        // Search the args   
        TypeConsApp correspondingTypeConsApp = correspondingSuperType.rootTypeConsApp();
        if (correspondingTypeConsApp == null ||
            !getRoot().getName().equals(correspondingTypeConsApp.getRoot().getName())) {
            return null;
        }
        
        for (int i = 0, nArgs = getNArgs(); i < nArgs; ++i) {

            TypeExpr correspondingType = getArg(i).getCorrespondingTypeExpr(correspondingTypeConsApp.getArg(i), typeToFind);
            if (correspondingType != null) {
                return correspondingType;
            }
        }

        return null;
    }

    /** {@inheritDoc} */
    @Override
    void toSourceText(StringBuilder sb,
            PolymorphicVarContext polymorphicVarContext,
            ParenthesizationInfo parenthesizationInfo,
            ScopedEntityNamingPolicy namingPolicy) {
       
        String typeConsName = namingPolicy.getName(rootTypeCons);

        final int nArgs = getNArgs();

        if (nArgs == 0) {
            if (getName().equals(CAL_Prelude.TypeConstructors.Unit)) {
                sb.append("()");
                return;                
            }
            
            sb.append(typeConsName);
            return;           
        }

        if (nArgs == 2 && getName().equals(CAL_Prelude.TypeConstructors.Function)) {

            TypeExpr lhs = getArg(0);
            TypeExpr rhs = getArg(1);
            final boolean reallyParenthesize = 
                parenthesizationInfo == ParenthesizationInfo.ARG_OF_TEXTUAL_TYPE_CONS_OR_VAR ||
                parenthesizationInfo == ParenthesizationInfo.DOMAIN_OF_FUNCTION;                
            if (reallyParenthesize) {
                sb.append('(');
            }
            
            lhs.toSourceText(sb, polymorphicVarContext, ParenthesizationInfo.DOMAIN_OF_FUNCTION, namingPolicy);
            sb.append(" -> ");
            rhs.toSourceText(sb, polymorphicVarContext, ParenthesizationInfo.NONE, namingPolicy);
            
            if (reallyParenthesize) {
                sb.append(')');
            }
            
            return;                       
        }

        if (nArgs == 1 && getName().equals(CAL_Prelude.TypeConstructors.List)) {
            
            sb.append('[');
            getArg(0).toSourceText(sb, polymorphicVarContext, ParenthesizationInfo.NONE, namingPolicy);           
            sb.append(']');
            return;
        }       

        // if here: type constructor application
        final boolean reallyParenthesize = parenthesizationInfo == ParenthesizationInfo.ARG_OF_TEXTUAL_TYPE_CONS_OR_VAR;
        if (reallyParenthesize) {
            sb.append('(');
        }
        
        sb.append(typeConsName);    
        for (int i = 0; i < nArgs; ++i) {
          
            sb.append(' ');            
            getArg(i).toSourceText(sb, polymorphicVarContext, ParenthesizationInfo.ARG_OF_TEXTUAL_TYPE_CONS_OR_VAR, namingPolicy);
        }
                       
        if (reallyParenthesize) {
            sb.append(')');
        }               
    }
    
    /** {@inheritDoc} */
    @Override
    SourceModel.TypeExprDefn makeDefinitionSourceModel(PolymorphicVarContext polymorphicVarContext, ScopedEntityNamingPolicy namingPolicy) {
        
        final int nArgs = getNArgs();

        if (nArgs == 0) {                     
            if (getName().equals(CAL_Prelude.TypeConstructors.Unit)) {
                return SourceModel.TypeExprDefn.Unit.make();
            }
            
            final ModuleName typeConsModuleName = namingPolicy.getModuleNameForScopedEntity(rootTypeCons);
            
            SourceModel.Name.TypeCons typeConsName =
                SourceModel.Name.TypeCons.make(typeConsModuleName, rootTypeCons.getName().getUnqualifiedName());
            
            return SourceModel.TypeExprDefn.TypeCons.make(typeConsName);
        }

        if (nArgs == 2 && getName().equals(CAL_Prelude.TypeConstructors.Function)) {

            return SourceModel.TypeExprDefn.Function.make(
                getArg(0).makeDefinitionSourceModel(polymorphicVarContext, namingPolicy),
                getArg(1).makeDefinitionSourceModel(polymorphicVarContext, namingPolicy));
        }

        if (nArgs == 1 && getName().equals(CAL_Prelude.TypeConstructors.List)) {
                        
            return SourceModel.TypeExprDefn.List.make(getArg(0).makeDefinitionSourceModel(polymorphicVarContext, namingPolicy));
        }       

        // if here: type constructor application
        SourceModel.TypeExprDefn[] typeExprs = new SourceModel.TypeExprDefn[nArgs + 1];
        
        final ModuleName typeConsModuleName = namingPolicy.getModuleNameForScopedEntity(rootTypeCons);
        
        typeExprs[0] = SourceModel.TypeExprDefn.TypeCons.make(typeConsModuleName, rootTypeCons.getName().getUnqualifiedName());
        
        for (int i = 0; i < nArgs; i++) {
            typeExprs[i+1] = getArg(i).makeDefinitionSourceModel(polymorphicVarContext, namingPolicy);
        }
        return SourceModel.TypeExprDefn.Application.make(typeExprs);
    }
   
    /** {@inheritDoc} */
    @Override
    public int getArity() {

        if (getName().equals(CAL_Prelude.TypeConstructors.Function)) {

            //it is incorrect to assume that Prelude.Function is fully saturated.    
            if (getNArgs() == 2) {
                TypeExpr rhs = getArg(1);
                return 1 + rhs.getArity();
            } else {
                //a non-fully saturated (or oversaturated, which will result in a type error) application of Function 
                return 0;
            }
        }

        return 0;
    }

    /**    
     * @return ForeignTypeInfo information about the Java type corresponding to this foreign type if
     *      this is a foreign type, or null if not a foreign type.
     */
    public ForeignTypeInfo getForeignTypeInfo() {
        return getRoot().getForeignTypeInfo();
    } 
    
    /** {@inheritDoc} */     
    @Override
    int unifyType(TypeExpr anotherTypeExpr, ModuleTypeInfo contextModuleTypeInfo) throws TypeException {

        if (anotherTypeExpr instanceof TypeVar) {

            return TypeExpr.unifyType(anotherTypeExpr, this, contextModuleTypeInfo);
            
        } else if (anotherTypeExpr instanceof TypeConsApp) {        

            TypeConsApp typeConsApp = (TypeConsApp) anotherTypeExpr;
            if (getName().equals(typeConsApp.getName())) {
    
                //get 1 type closeness point for an exact match of type constructors +
                //the type closeness measure contributed by the children.                          
                return TypeConsApp.unifyArgs(args, typeConsApp.args, contextModuleTypeInfo) + 1;
    
            }
    
            throw new TypeException("Type clash: type constructor " + getName() + " does not match " + typeConsApp.getName() + ".");
            
        } else if (anotherTypeExpr instanceof TypeApp) {
            
            TypeApp typeApp = (TypeApp)anotherTypeExpr;
            
            //(TypeCons e1 e2 ... en-1 en) unifies with (a1 a2) by
            //1) first check that n >= 1
            //2) next unify (TypeCons e1 e2 ... en-1) with a1
            //3) finally unify en and a2.
            
            final int nArgs = args.length;
            //must have at least a single argument to unify with a type application
            if (nArgs == 0) {
                throw new TypeException("Type clash: typeConstructor " + getName() + " does not match a type application."); 
            }
                                    
            // the type constructor expression, with its final argument dropped.
            TypeExpr[] partialArgs = new TypeExpr[nArgs - 1];
            System.arraycopy(args, 0, partialArgs, 0, nArgs - 1);
            TypeConsApp partialTypeConsApp = new TypeConsApp(rootTypeCons, partialArgs);             
            int typeCloseness = TypeExpr.unifyType(partialTypeConsApp, typeApp.getOperatorType(), contextModuleTypeInfo);
            
            TypeExpr finalArg = args[nArgs - 1];
            return typeCloseness + TypeExpr.unifyType(finalArg, typeApp.getOperandType(), contextModuleTypeInfo);            
            
        } else if (anotherTypeExpr instanceof RecordType) {
            
            throw new TypeException("Type clash: type constructor " + getName() + " does not match a record type.");
        }
        
        throw new IllegalStateException(); 
    }

    /**
     * Unifies 2 argument lists. In particular, the 2 argument lists must have the same length, and must
     * unify element by element.
     * @param args1
     * @param args2
     * @param contextModuleTypeInfo
     * @return int
     * @throws TypeException
     */
    private static int unifyArgs(TypeExpr[] args1, TypeExpr[] args2, ModuleTypeInfo contextModuleTypeInfo) throws TypeException {

        int nArgs = args1.length;

        if (nArgs != args2.length) {
            throw new TypeException("Type clash- different number of type arguments");
        }

        int typeCloseness = 0;

        for (int i = 0; i < nArgs; ++i) {
            typeCloseness += TypeExpr.unifyType(args1[i], args2[i], contextModuleTypeInfo);
        }

        return typeCloseness;
    }

    /** {@inheritDoc} */
    @Override
    void patternMatch(TypeExpr anotherTypeExpr, PatternMatchContext patternMatchContext) throws TypeException {

        if (anotherTypeExpr instanceof TypeVar) {

            TypeVar anotherTypeVar = (TypeVar) anotherTypeExpr;

            //anotherTypeVar must be uninstantiated.
            if (anotherTypeVar.getInstance()!= null) {
                throw new IllegalArgumentException("TypeConsApp.patternMatch: programming error.");
            }
            
            //Can't instantiate a type variable from the declared type expression.
            //For example, this error occurs with the following declaration.
            //funnyHead :: [a] -> Prelude.Char;
            //public funnyHead = List.head;
            if (patternMatchContext.isUninstantiableTypeVar(anotherTypeVar)) {
                throw new TypeException("Attempt to instantiate a type variable from the declared type.");
            }            
            
            //can't pattern match a nongeneric type variable to a type constructor involving uninstantiated type variables.
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
            
            TypeConstructor typeCons = this.rootTypeCons;
    
            final int nArgs = this.getNArgs();
            List<SortedSet<TypeClass>> argConstraints = new ArrayList<SortedSet<TypeClass>>(nArgs);
            for (int i = 0; i < nArgs; ++i) {            
                argConstraints.add(i, TypeClass.makeNewClassConstraintSet());
            }

            for (final TypeClass typeClass : anotherTypeVar.getTypeClassConstraintSet()) {
                
                ClassInstance classInstance = patternMatchContext.getModuleTypeInfo().getVisibleClassInstance(typeClass, rootTypeCons);

                if (classInstance == null) {
                    throw new TypeException("Type clash: type " + toString() + " is not a member of type class " + typeClass.getName() + ".");
                }

                //For example, this could be (Eq a, Eq b) => Tuple2 a b
                TypeConsApp classInstanceType = (TypeConsApp)classInstance.getType();

                //The type arguments of the type constructor must satisfy additional constraints implied by the class instance.
                //Here we calculate what those constraints are.                                                                            
                for (int i = 0; i < nArgs; ++i) {
                    //we know from how instances are defined that the argument types are all uninstantiated
                    //type variables (possibly constrained though).
                    TypeVar classInstanceArgTypeVar = (TypeVar) classInstanceType.getArg(i);
                    if (!classInstanceArgTypeVar.noClassConstraints()) {
                        argConstraints.get(i).addAll(classInstanceArgTypeVar.getTypeClassConstraintSet());
                    }
                }
            }
            
            //initialize args.
            TypeExpr[] args = new TypeExpr[nArgs];
            for (int i = 0; i < nArgs; ++i) {            
                args[i] = TypeVar.makeTypeVar(null, argConstraints.get(i), true);                                    
            }
    
            //pattern matching proceeds in 2 steps e.g. For patternMatch (Tuple2 Char Int, Eq a => a)
            //step1: instantiate Eq a => a to (Eq b Eq c => Tuple2 b c)
            //step2: patternMatch (Tuple2 Char Int, Eq b Eq c => Tuple2 b c)                              
            TypeConsApp constrainedTypeConsApp = new TypeConsApp(typeCons, args);               
            anotherTypeVar.setInstance(constrainedTypeConsApp);
                                                       
            this.patternMatch(constrainedTypeConsApp, patternMatchContext); 
            return;                                          
            
        } else if (anotherTypeExpr instanceof TypeConsApp) {        

            TypeConsApp anotherTypeConsApp = (TypeConsApp) anotherTypeExpr;
    
            if (getName().equals(anotherTypeConsApp.getName())) {
    
                TypeConsApp.patternMatchArgs(args, anotherTypeConsApp.args, patternMatchContext);
                return;
            }
    
            throw new TypeException("Type clash: The type declaration " + toString() + " does not match " + anotherTypeConsApp + ".");
            
        } else if (anotherTypeExpr instanceof TypeApp) {
            
            TypeApp typeApp = (TypeApp)anotherTypeExpr;
                        
            //(TypeCons e1 e2 ... en-1 en) pattern matches with (a1 a2) by
            //1) first check that n >= 1
            //2) next pattern match (TypeCons e1 e2 ... en-1) with a1
            //3) finally pattern match en and a2.
            
            final int nArgs = args.length;
            //must have at least a single argument to pattern match with a type application
            if (nArgs == 0) {
                throw new TypeException("Type clash: typeConstructor " + getName() + " does not match a type application."); 
            }
                                    
            // the type constructor expression, with its final argument dropped.
            TypeExpr[] partialArgs = new TypeExpr[nArgs - 1];
            System.arraycopy(args, 0, partialArgs, 0, nArgs - 1);
            TypeConsApp partialTypeConsApp = new TypeConsApp(rootTypeCons, partialArgs); 
            partialTypeConsApp.patternMatch(typeApp.getOperatorType().prune(), patternMatchContext);
            
            TypeExpr finalArg = args[nArgs - 1].prune();
            finalArg.patternMatch(typeApp.getOperandType().prune(), patternMatchContext); 
            return;
                        
        } else if (anotherTypeExpr instanceof RecordType) {
            
            throw new TypeException("Type clash: The type declaration " + toString() + " does not match the record type " + anotherTypeExpr + ".");
        }
        
        throw new IllegalStateException();
    }

    /**
     * Pattern matches two lists of arguments. 
     * @param declaredTypeArgs
     * @param inferredTypeArgs
     * @param patternMatchContext
     * @throws TypeException
     */
    private static void patternMatchArgs(TypeExpr[] declaredTypeArgs, TypeExpr[] inferredTypeArgs, PatternMatchContext patternMatchContext) throws TypeException {

        int nArgs = declaredTypeArgs.length;
        if (nArgs != inferredTypeArgs.length) {
            throw new TypeException("The declared type does not match the inferred type.");
        }

        for (int i = 0; i < nArgs; ++i) {
            TypeExpr declaredTypeArg = declaredTypeArgs[i].prune();
            TypeExpr inferredTypeArg = inferredTypeArgs[i].prune();
            declaredTypeArg.patternMatch(inferredTypeArg, patternMatchContext);
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean containsTypeExpr(TypeExpr searchTypeExpr) {

        if (this == searchTypeExpr) {
            return true;
        }

        for (int i = 0, nArgs = getNArgs(); i < nArgs; ++i) {
            if (getArg(i).containsTypeExpr(searchTypeExpr)) {
                return true;
            }
        }

        return false;
    }
    
    /** {@inheritDoc} */
    @Override
    boolean containsRecordVar(RecordVar searchRecordVar) {
     
        for (int i = 0, nArgs = getNArgs(); i < nArgs; ++i) {
            if (getArg(i).containsRecordVar(searchRecordVar)) {
                return true;
            }
        }

        return false;
    }    
    
    /** {@inheritDoc} */
    @Override
    public boolean isPolymorphic() {
        
        for (int i = 0, nArgs = getNArgs(); i < nArgs; ++i) {
            if (getArg(i).isPolymorphic()) {
                return true;
            }
        }

        return false;
    }

    /** {@inheritDoc} */
    @Override
    public boolean usesForeignType() {

        if (rootTypeCons.getForeignTypeInfo() != null) {
            return true;
        }

        for (int i = 0, nArgs = getNArgs(); i < nArgs; ++i) {
            if (getArg(i).usesForeignType()) {
                return true;
            }
        }

        return false;
    }

    /** {@inheritDoc} */
    @Override
    final void writeActual (RecordOutputStream s, Map<TypeExpr, Short> visitedTypeExpr, Map<RecordVar, Short> visitedRecordVar) throws IOException {
        s.startRecord(ModuleSerializationTags.TYPE_CONSTRUCTOR, serializationSchema);
        s.writeQualifiedName(rootTypeCons.getName());
        s.writeShortCompressed(args.length);
        for (int i = 0; i < args.length; ++i) {
            args[i].write(s, visitedTypeExpr, visitedRecordVar);
        }
        s.endRecord();
    }
    
    /**
     * Read an instance of TypeConsApp from the RecordInputStream.
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
        DeserializationHelper.checkSerializationSchema(schema, serializationSchema, mti.getModuleName(), "TypeConsApp", msgLogger);
        
        QualifiedName rootTypeConsName = s.readQualifiedName();
        TypeConstructor rootTypeCons = mti.getReachableTypeConstructor(rootTypeConsName);
        // This may be a type constructor in a module that is not directly imported.
        if (rootTypeCons == null) {
            throw new IOException ("Unable to resolve TypeConstructor " + rootTypeConsName + " while loading TypeConsApp.");
        }

        int nArgs = s.readShortCompressed();
        
        // We now have enough information to create the TypeConsApp instance and
        // add it to the visitedTypeExpr map.  We must do this before loading the
        // argument types as they may refer to this TypeConsApp instance.
        TypeConsApp typeConsApp = new TypeConsApp(rootTypeCons, nArgs);
        visitedTypeExpr.put(new Short((short)visitedTypeExpr.size()), typeConsApp);
        
        // Now load the argument types.
        for (int i = 0; i < nArgs; ++i) {
            TypeExpr te = TypeExpr.load(s, mti, visitedTypeExpr, visitedRecordVar, msgLogger);
            typeConsApp.setArg(i, te);
        }
        
        s.skipRestOfRecord();
        
        return typeConsApp;  
    }
}
