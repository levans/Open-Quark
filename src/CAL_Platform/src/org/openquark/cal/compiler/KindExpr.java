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
 * KindExpr.java
 * Creation date: (January 8, 2001)
 * By: Bo Ilic
 */
package org.openquark.cal.compiler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.openquark.cal.internal.serialization.ModuleSerializationTags;
import org.openquark.cal.internal.serialization.RecordInputStream;
import org.openquark.cal.internal.serialization.RecordOutputStream;
import org.openquark.cal.internal.serialization.RecordInputStream.RecordHeaderInfo;


/**
 * The internal representation of kind expressions. Each type constructor, type class and type
 * expression in a CAL program has an associated kind. 
 * 
 * <p>
 * Kind inference is the process of determining the kind of user-supplied type expressions, and is used
 * to verify that type expressions are correct. User-supplied type expressions occur 
 * <ol>
 *  <li> in function type declarations and expression type signatures
 *  <li> in algebraic data declarations 
 *  <li> in type class definitions 
 * </ol>
 * 
 * <p>
 * This is analogous to an expression having an associated type,
 * with type inference being used to determine that the expression is correct. For example, Double
 * and Boolean have kind *, while Maybe has kind * -> * and Prelude.Function has kind * -> * -> *. 
 * 
 * <p>
 * Thus, for example, type expressions such as "Int Double", "Function Char Int Double" will result
 * in kind errors. However, depending on the context, an type constructor can correctly be used in an
 * undersaturated form. For example, List, Maybe and "Map k" are instances of the Functor type class.
 * 
 * <p>
 * Creation date: (January 8, 2001)
 * @author Bo Ilic
 */
abstract class KindExpr {
    
    static final KindConstant STAR = new KindConstant();

    /**
     * The array of possible record tags used in calls to {@link RecordInputStream#findRecord(short[])} by
     * the {@link #load} method.
     */
    private static final short[] KIND_EXPR_RECORD_TAGS = new short[]{
        ModuleSerializationTags.KIND_EXPR_KIND_CONSTANT,
        ModuleSerializationTags.KIND_EXPR_KIND_FUNCTION   
    };
    
    /**
     * A kind variable. It is uninstantiated when the instance field is null and instantiated
     * otherwise. An instantiated kind variable behaves like its instantiation.
     */
    static final class KindVar extends KindExpr {
             
        private KindExpr instance;

        KindVar() {
            instance = null;
        }

        KindExpr getInstance() {
            return instance;
        }
        
        void setInstance(KindExpr instance) {
            if (this.instance != null) {            
                throw new IllegalStateException("KindExpr.setInstance: Programming Error- can't set the instance of an instantiated kind variable.");
            }
            this.instance = instance;
        }

        /**
         * Whether an uninstantiated kind variable occurs in a kind expression.
         * For example, "k" occurs in "* -> k -> *". We do not allow unifications where
         * a kind variable is specialized to a kind expression in which the variable
         * itself occurs since this leads to infinite kinds. 
         */
        boolean occursInKindExpr(KindExpr kindExpr) {

            kindExpr = kindExpr.prune();

            if (kindExpr instanceof KindVar) {
                return this == kindExpr;

            } else if (kindExpr instanceof KindFunction) {

                return occursInKindExpr(((KindFunction) kindExpr).getDomain()) || occursInKindExpr(((KindFunction) kindExpr).getCodomain());

            } else if (kindExpr instanceof KindConstant) {
                return false;
            }

            throw new IllegalArgumentException("Programming error");
        }
        
        /** {@inheritDoc} */
        @Override
        int getNArguments() {
            
            if (instance != null) {
                return instance.getNArguments();
            }
            
            return 0;           
        } 
        
        /** {@inheritDoc} */
        @Override
        boolean isSimpleKindChain() {
           if (instance != null) {
               return instance.isSimpleKindChain();
           }
           return false;
        }        

        /** {@inheritDoc} */
        @Override
        KindExpr prune() {

            if (instance != null) {
                return instance.prune();               
            }

            return this;
        }
        
        /** {@inheritDoc} */
        @Override
        boolean containsKindVars() {
            return true;
        }            

        @Override
        String toString(Map<KindVar, Integer> kindVarToIndexMap) {

            if (instance != null) {
                return instance.toString(kindVarToIndexMap);
            }

            Object indexObject = kindVarToIndexMap.get(this);
            int index;
            if (indexObject == null) {
                index = kindVarToIndexMap.size() + 1;
                kindVarToIndexMap.put(this, Integer.valueOf(index));
            } else {
                index = ((Integer) indexObject).intValue();
            }

            return indexToVarName(index);
        }

        private static String indexToVarName(int index) {

            if (index > 0 && index <= 26) {
                return String.valueOf((char) ('a' + index - 1));
            }

            return "a" + String.valueOf(index);
        } 
        
        @Override
        final void write (RecordOutputStream s) throws IOException {
            throw new IOException("we should not be serializing kind variables- they are transient during type checking.");
        }
         
    }

    /**
     * A singleton. KindConstant is the kind *.
     */
    static final class KindConstant extends KindExpr {

        private static final int serializationSchema = 0;
        
        /** should only be used to construct the KindExpr.STAR. */
        private KindConstant() {
        }
        
        /** {@inheritDoc} */
        @Override
        int getNArguments() {
            return 0; 
        }
        
        /** {@inheritDoc} */
        @Override
        boolean isSimpleKindChain() {
           return true;
        }              

        /** {@inheritDoc} */
        @Override
        KindExpr prune() {
            return this;
        }
        
        /** {@inheritDoc} */
        @Override
        boolean containsKindVars() {
            return false;
        }           

        @Override
        String toString(Map<KindVar, Integer> kindVarToIndexMap) {
            return "*";
        }
        
        @Override
        final void write (RecordOutputStream s) throws IOException {
            s.startRecord(ModuleSerializationTags.KIND_EXPR_KIND_CONSTANT, serializationSchema);
            s.endRecord();
        }
        
        static final KindConstant load (RecordInputStream s, int schema, ModuleName moduleName, CompilerMessageLogger msgLogger) throws IOException {
            DeserializationHelper.checkSerializationSchema(schema, serializationSchema, moduleName, "KindExpr.KindConstant", msgLogger);
            s.skipRestOfRecord();
            
            return KindExpr.STAR;
        }
    }

    /**
     * KindFunction is the kind k1 -> k2 where k1 and k2 are kinds.
     */
    static final class KindFunction extends KindExpr {

        private static final int serializationSchema = 0;
        private KindExpr domain;
        private KindExpr codomain;

        KindFunction(KindExpr domain, KindExpr codomain) {
            if (domain == null || codomain == null) {
                throw new NullPointerException();
            }
            this.domain = domain;
            this.codomain = codomain;
        }

        KindExpr getDomain() {
            return domain;
        }

        KindExpr getCodomain() {
            return codomain;
        }
        
        /** {@inheritDoc} */
        @Override
        int getNArguments() {
            return 1 + codomain.getNArguments(); 
        }
        
        /** {@inheritDoc} */
        @Override
        boolean isSimpleKindChain() {
            return (domain.prune() instanceof KindConstant) && codomain.isSimpleKindChain();
        }         

        /** {@inheritDoc} */        
        @Override
        KindExpr prune() {
            return this;
        }
        
        /** {@inheritDoc} */
        @Override
        boolean containsKindVars() {
            return domain.containsKindVars() || codomain.containsKindVars();
        }         

        @Override
        String toString(Map<KindVar, Integer> kindVarToIndexMap) {

            StringBuilder buf = new StringBuilder();

            String domainString = domain.toString(kindVarToIndexMap);
            if (domainString.length() == 1) {
                buf.append(domainString);
            } else {
                buf.append("(").append(domainString).append(")");
            }

            buf.append(" -> ");

            //parentheses around the codomain are not necessary since -> is right associative.
            buf.append(codomain.toString(kindVarToIndexMap));

            return buf.toString();
        }
        
        @Override
        final void write (RecordOutputStream s) throws IOException {
            s.startRecord(ModuleSerializationTags.KIND_EXPR_KIND_FUNCTION, serializationSchema);
            domain.write(s);
            codomain.write(s);
            s.endRecord();
        }

        /**
         * Load an instance of KindFunction from the RecordInputStream.
         * Read position will be after the KindFunction record header.
         * @param s
         * @param schema
         * @param moduleName the name of the module being loaded
         * @param msgLogger the logger to which to log deserialization messages.
         * @return a KindFunction instance.
         * @throws IOException
         */
        final static KindFunction load (RecordInputStream s, int schema, ModuleName moduleName, CompilerMessageLogger msgLogger) throws IOException {
            DeserializationHelper.checkSerializationSchema(schema, serializationSchema, moduleName, "KindExpr.KindFunction", msgLogger);
            KindExpr ke1 = KindExpr.load(s, moduleName, msgLogger);
            KindExpr ke2 = KindExpr.load(s, moduleName, msgLogger);
            s.skipRestOfRecord();
            return new KindFunction(ke1, ke2);
        }
    }

    /**
     * Converts all the kind variables in a kind expression to the default binding *. e.g the
     * kind expression (k1 -> *) -> k2 is converted to  (* -> *) -> *. The returned KindExpr
     * will have no kind variables in it (instantiated or not). this kindExpr may be pruned,
     * but will otherwise not be altered.
     */
    KindExpr bindKindVariablesToConstant() {

        KindExpr kindExpr = this.prune();

        if (kindExpr instanceof KindVar) {

            //will be uninstantiated because of the pruning
            return STAR;

        } else if (kindExpr instanceof KindConstant) {

            return kindExpr;

        } else if (kindExpr instanceof KindFunction) {
            
            if (kindExpr.containsKindVars()) {
                KindExpr domainKindExpr = ((KindFunction) kindExpr).getDomain().bindKindVariablesToConstant();
                KindExpr codomainKindExpr = ((KindFunction) kindExpr).getCodomain().bindKindVariablesToConstant();
                return new KindExpr.KindFunction(domainKindExpr, codomainKindExpr);
            } else {
                //no kind variables, so don't need to make a copy.
                return kindExpr;
            }
        }

        throw new IllegalStateException("Programming error. Unrecognized KindExpr subtype.");
    }
    
    /**
     * A helper function to create kind expressions of the form *->...->*->*.
     * Creation date: (3/7/01 1:36:36 PM)
     * @return KindExpr
     * @param chainLength int the number of *'s in the chain
     */
    static KindExpr makeKindChain(int chainLength) {

        if (chainLength < 0) {
            throw new IllegalArgumentException("KindExpr.makeKindChain: negative chainLength.");
        }
       
        KindExpr kindExpr = KindExpr.STAR;

        while (--chainLength > 0) {
            kindExpr = new KindExpr.KindFunction(KindExpr.STAR, kindExpr);
        }

        return kindExpr;
    }
    
    /**     
     * @return int the number of type arguments taken by the kind. For example, for the kind
     * * -> * -> * this is 2, for the kind (* -> *) -> (* -> *) -> *, this is also 2.
     */
    abstract int getNArguments(); 
    
    
    /**     
     * @return boolean true if this KindExpr is of the form * -> * -> * ... -> *. In other words,
     * if this is the kind of a TypeConsApp of arity n, then all type variable arguments have kind *.
     * For example, will return false for (* -> *) -> * -> *.
     */
    abstract boolean isSimpleKindChain();     
    
    /**
     * Eliminate redundant instantiated kind variables at the top of this KindExpr.
     * The result of prune is always a non-instantiated kind variable, a kind
     * constant or a kind function. 
     * <p>
     * This function has no side-effects on the invocation object.   
     * <p>
     * Creation date: (1/24/01 3:27:07 PM)
     * @return KindExpr
     */
    abstract KindExpr prune();   
    
    /**    
     * @return true if this KindExpr contains a KindVar, whether instantiated or not.
     */
    abstract boolean containsKindVars();
    
    /**
     * Returns a string representation of this KindExpr, where kind variable addresses are
     * replaced by lower case letters: a, b, c, ..., z, a1, a2, a3, ...
     * <p>
     * Since kinds are an internal implementation technique, this is mainly intended for
     * debugging purposes, in contrast to the analogous method in the TypeExpr class.
     * <p>
     * Creation date: (1/24/01 4:26:11 PM)
     * @return String
     */
    @Override
    public String toString() {
        return toString(new HashMap<KindVar, Integer>());
    }
    
    /**
     * Insert the method's description here.
     * Creation date: (1/24/01 4:24:29 PM)
     * @return String
     * @param typeVarToIndexMap
     */
    abstract String toString(Map<KindVar, Integer> typeVarToIndexMap);
    
    /**
     * Write the object to a record output stream.
     * @param s
     * @throws IOException
     */
    abstract void write (RecordOutputStream s) throws IOException;
    
    /**
     * Read an instance of one of the concrete derived classes from 
     * the RecordInputStream.
     * @param s
     * @param moduleName the name of the module being loaded
     * @param msgLogger the logger to which to log deserialization messages.
     * @return and instance of KindExpr.
     * @throws IOException
     */
    static KindExpr load (RecordInputStream s, ModuleName moduleName, CompilerMessageLogger msgLogger) throws IOException {
        
        RecordHeaderInfo rhi = s.findRecord(KIND_EXPR_RECORD_TAGS);
        
        switch (rhi.getRecordTag()) {
            case ModuleSerializationTags.KIND_EXPR_KIND_CONSTANT:           
                return KindConstant.load(s, rhi.getSchema(), moduleName, msgLogger); 
            
            case ModuleSerializationTags.KIND_EXPR_KIND_FUNCTION:
                return KindFunction.load(s, rhi.getSchema(), moduleName, msgLogger);
                           
            default:
                throw new IOException ("Unexpected record tag while loading KindExpr: " + rhi.getRecordTag());
        }
    }
    
    /**
     * Unify two kind expressions. What this means is to find the most general kind to which both
     * kindExpr1 and kindExpr2 can be specialized to. Unification fails when trying to a kind constant
     * to a kind function (such as * and *->*) or when trying to instantiate a kind variable to a term
     * containing that kind variable (like a and a->b, where a circular structure would be built.)
     * As a general point of interest, this process is essentially the same as unification in Prolog.
     * 
     * <p>
     * An important point is that this function has side-effects on its KindExpr arguments, including
     * potentially when the unification fails in a TypeException. 
     * 
     * <p>
     * Creation date: (1/24/01 5:49:01 PM)
     * @param kindExpr1
     * @param kindExpr2
     */
    static void unifyKind(KindExpr kindExpr1, KindExpr kindExpr2) throws TypeException {

        kindExpr1 = kindExpr1.prune();
        kindExpr2 = kindExpr2.prune();

        if (kindExpr1 instanceof KindVar) {

            KindExpr.KindVar kindVar = (KindVar) kindExpr1;

            if (kindVar.occursInKindExpr(kindExpr2)) {

                if (kindVar != kindExpr2) {
                    throw new TypeException("Kind clash: attempt to create an infinite kind");
                }

            } else {
                kindVar.setInstance(kindExpr2);
            }

        } else if (kindExpr1 instanceof KindConstant) {

            if (kindExpr2 instanceof KindVar) {
                unifyKind(kindExpr2, kindExpr1);
            } else if (kindExpr2 instanceof KindFunction) {
                throw new TypeException("Kinding clash");
            } else if (kindExpr2 instanceof KindConstant) {
                //do nothing if kindExpr2 instanceof KindConstant
            } else {
                throw new IllegalStateException();
            }

        } else if (kindExpr1 instanceof KindFunction) {

            if (kindExpr2 instanceof KindVar || kindExpr2 instanceof KindConstant) {
                unifyKind(kindExpr2, kindExpr1);
            } else {
                //kindExpr2 instanceof KindFunction
                unifyKind(((KindFunction) kindExpr1).getDomain(), ((KindFunction) kindExpr2).getDomain());
                unifyKind(((KindFunction) kindExpr1).getCodomain(), ((KindFunction) kindExpr2).getCodomain());
            }
            
        }  else {
            throw new IllegalStateException();                                         
        }
    }
    
    /**
     * In certain cases there are multiple options as to how to break up a KindExpr into argument kinds and
     * return kind. 
     * For example, if KindExpr is (* -> *) -> (* -> * -> *) -> *, then getKindPieces(2) returns
     * [* -> *, * -> * -> *, *]
     * and getKindPieces(1) returns [* -> *, (* -> * -> *) -> *]     
     *         
     * @param arity must be less than or equal to this.getNArguments()
     * @return kind pieces array of length arity + 1. The return kind is the final element of the array.
     */
    final KindExpr[] getKindPieces(int arity) {
               
        if (arity < 0 || arity > getNArguments()) {
            throw new IllegalArgumentException("arity arguments must be non-negative and less than or equal to KindExpr.getNArguments()");
        }
        
        KindExpr[] kindPieces = new KindExpr[arity + 1];

        KindExpr kindExpr = this.prune();

        for (int i = 0; i < arity; ++i) {
                                   
            if (kindExpr instanceof KindFunction) {
                
                KindFunction kindFunction = (KindFunction)kindExpr;
                                                
                kindPieces[i] = kindFunction.getDomain().prune();
                kindExpr = kindFunction.getCodomain().prune();
                           
            } else {
                throw new IllegalStateException();
            }         
        }

        kindPieces[arity] = kindExpr;

        return kindPieces;
    }    
    
}