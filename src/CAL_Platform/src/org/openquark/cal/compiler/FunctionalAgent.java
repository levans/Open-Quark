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
 * FunctionalAgent.java
 * Created: April 16, 2001
 * By: Bo Ilic
 */
package org.openquark.cal.compiler;

import java.io.IOException;

import org.openquark.cal.internal.serialization.ModuleSerializationTags;
import org.openquark.cal.internal.serialization.RecordInputStream;
import org.openquark.cal.internal.serialization.RecordOutputStream;
import org.openquark.cal.internal.serialization.RecordInputStream.RecordHeaderInfo;


/**
 * Descibes a function-like entity with CAL. Functions (algebraic, foreign and primitive), class methods and data constructors
 * all are capable of "function-like" behavior in that they can be called, return a value, have a type etc.
 * 
 * Within the GemCutter, functional agents are called Gems.
 * 
 * Note that FunctionalAgent is immutable with respect to external clients.
 *
 * @author Bo Ilic
 */
public abstract class FunctionalAgent extends ScopedEntity {
    
    private static final int serializationSchema = 0;
    
    /** the type of the functional agent. */
    private TypeExpr typeExpr;
   
    /** the explicitly specified arguments of this functional agent. Useful for UI. */
    private String[] argumentNames;

    /**
     * entity form enum pattern.
     * Creation date: (6/4/01 1:12:46 PM)
     * @author Bo Ilic
     */
    static final class Form {
       
        private static final byte FORM_TYPE_CLASS_METHOD = 1;
        private static final byte FORM_TYPE_DATA_CONSTRUCTOR = 2;
        private static final byte FORM_TYPE_FUNCTION = 3;
        private static final byte FORM_TYPE_PATTERNVAR = 4;
        
        private String name;
        private Form(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }

        /**
         * Write this instance of Form to the RecordOutputStream.
         * @param s
         * @throws IOException
         */
        final void write (RecordOutputStream s) throws IOException {
            if (this == CLASS_METHOD) {
                s.writeByte(FORM_TYPE_CLASS_METHOD);
            } else if (this == DATA_CONSTRUCTOR) {
                s.writeByte(FORM_TYPE_DATA_CONSTRUCTOR);
            } else if (this == FUNCTION) {
                s.writeByte(FORM_TYPE_FUNCTION);
            } else if (this == PATTERNVAR) {
                s.writeByte(FORM_TYPE_PATTERNVAR);
            } else {
                throw new IOException("Unknown FunctionalAgent.Form instance: " + this);
            }
        }
        
        /**
         * Load an instance of Form from the RecordInputStream.
         * Read position is before the record header.
         * @param s
         * @return an instance of Form.
         * @throws IOException
         */
        static final Form load (RecordInputStream s) throws IOException {
            int key = s.readByte();
            switch (key) {
            case FORM_TYPE_CLASS_METHOD:
                return CLASS_METHOD;
            case FORM_TYPE_DATA_CONSTRUCTOR:
                return DATA_CONSTRUCTOR;
            case FORM_TYPE_FUNCTION:
                return FUNCTION;
            case FORM_TYPE_PATTERNVAR:
                return PATTERNVAR;
            default:
                throw new IOException ("Unable to resolve FunctionalAgent.Form with key: " + key);
            }
        }
        
        /** top-level or local function. */
        public static final Form FUNCTION = new Form("function");
        public static final Form DATA_CONSTRUCTOR = new Form("data constructor");
        public static final Form CLASS_METHOD = new Form("class method");
        /** argument variable of a function or lambda expression or a variable bound by a case expression. */
        public static final Form PATTERNVAR = new Form("pattern bound variable");
    }

    /**
     * Construct an FunctionalAgent.    
     *   
     * @param entityName the name of the entity
     * @param scope the scope of the entity     
     * @param argumentNames the explicitly specified arguments of the entity.
     *   Can be null for entities with no explicitly specified argument names,
     *   or contain nulls for argument names which aren't specified.
     * @param typeExpr the type of the entity
     * @param calDocComment the CALDoc associated with this entity, or null if there is none.
     */
    FunctionalAgent(QualifiedName entityName, Scope scope, String[] argumentNames, TypeExpr typeExpr, CALDocComment calDocComment) {
       
        super (entityName, scope, calDocComment);
        
        if (typeExpr == null) {
            throw new NullPointerException ("FunctionalAgent constructor: the argument 'typeExpr' cannot be null.");
        }
                                                                                                          
        this.argumentNames = argumentNames != null ? argumentNames : new String[0];
        this.typeExpr = typeExpr;
    } 

    /** Zero argument constructor used for serialization. */
    FunctionalAgent() {
        
    }
    
    /**
     * Returns the kind of entity this is. "Form" is used as a synonym for "kind" or "type"
     * because of the overloaded meanings of the last 2 terms in the type checker!
     * Creation date: (6/4/01 1:49:57 PM)
     * @return FunctionalAgent.Form
     */
    abstract public FunctionalAgent.Form getForm();
   
    /**
     * Returns a copy of the TypeExpr of this entity. For example, if the TypeExpr held
     * by the entity is (a -> (Int, b)) -> (a, b) then the returned TypeExpr is
     * (a' -> (Int, b')) -> (a', b').
     *
     * Creation date: (4/16/01 1:47:46 PM)
     * @return TypeExpr
     */
    public final TypeExpr getTypeExpr() {
        return getTypeExpr(null);
    }
    
    /**
     * Returns a copy of the TypeExpr of this entity, with the nonGenericVars in the
     * TypeExpr copied exactly (the same object). For example, if the TypeExpr held
     * by the entity is (a -> (Int, b)) -> (a, b) and the nonGeneric vars is just a
     * then the returned TypeExpr is (a -> (Int, b')) -> (a, b').
     *
     * Creation date: (4/16/01 1:54:13 PM)
     * @return TypeExpr
     */
    TypeExpr getTypeExpr(NonGenericVars nonGenericVars) {

        //todoBI it would be nice if when typeCheckingDone is true, then we return
        //CopyEnv.freshType(typeExpr, null) instead i.e. we can then ensure that the
        //typeExpr of this entity will not be modified by the caller. Unfortunately,
        //this doesn't work at this time...                                          
        
        //TODO Type expressions are immutable for clients. In the future, we may want
        //to create a public, immutable, API and an internal API for type expressions.
        //Doing so would allow us to return typeExpr directly rather than returning a copy,
        //since clients would not be able to modify the returned type exression.
        //Also, we would then be able to remove the caching that is done by GemEntity.
        return CopyEnv.freshType(typeExpr, nonGenericVars);
    }

    /**
     * Returns the typeExpr of the entity without making a copy. This is generally not what
     * you want to do! It corresponds to treating all type variables that occur in the typeExpr
     * as non-generic.
     * 
     * @return TypeExpr
     */
    TypeExpr getTypeExprExact() {
        
        //todoBI it would be very nice to try to eliminate this method.
        //See the comment in getTypeExpr.
        return typeExpr;
    }       

    /**
     * Returns the number of arguments explicitly specified in the definition of this entity, 
     * for example, as specified in the CAL source. 
     * The number of arguments will be less than or equal to the number of actual arguments for the entity.
     * For example, if a function takes 5 arguments, and has 3 named arguments, then the 4th and 5th argument are unnamed.
     * For a data constructor, all arguments are nameable.  ie. if it has 3 arguments, all 3 are nameable.  
     *   However some or all of its arguments may not have names.
     * @return int number of named arguments
     */
    public final int getNArgumentNames() {
        return argumentNames.length;
    }

    /**
     * Returns the name of the given argument, explicitly specified in the definition of this entity.
     * @param argN int index into the named arguments.
     * @return unqualified name of the named argument, or null if the argument isn't named.
     */

    public final String getArgumentName(int argN) {
        return argumentNames[argN];
    }
    
    /**
     * Returns the nesting level at which the entity is defined. Top level entities are defined at
     * level 0.   
     * @return int
     */
    int getNestingLevel() {
        return 0;
    }
    
    /**  
     * Creation date: (6/6/01 2:48:40 PM)
     * @return boolean returns false if the entity is in the process of being type checked
     *     and thus subject to a change in type.
     */
    boolean isTypeCheckingDone() {
        return true;
    }
    
    /**
     * Finished type checking this entity, and so its type should now only
     * ever be used in a copied form.
     * Creation date: (6/6/01 2:49:12 PM)
     */
    void setTypeCheckingDone() {
        if (!isTypeCheckingDone()) {
            //replace the type expression with the simplest type expression in its
            //equivalence class.        
            typeExpr = typeExpr.deepPrune();                  
        }                  
    }    
    
    /**
     * Insert the method's description here.
     * Creation date: (6/6/01 2:53:31 PM)
     * @return String
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();       
        sb.append(super.toString());       
        sb.append(" :: ");
        sb.append(typeExpr.toString());
        sb.append(" [");
        sb.append(getForm().toString());             
        sb.append("]");

        sb.append(" [");
               
        for (int i = 0, nNamedArguments = getNArgumentNames(); i < nNamedArguments; ++i) {
            sb.append(getArgumentName(i));
            if (i < nNamedArguments - 1) {
                sb.append(' ');
            }
        }
       
        sb.append("]");

        return sb.toString();
    }
    
    /**
     * Implemented by concrete classes to serialize to a RecordOutputStream.
     * @param s
     * @throws IOException
     */
    @Override
    abstract void write (RecordOutputStream s) throws IOException;

    /**
     * Serialize the content owned by this class to a RecordOutputStream.
     * @param s
     * @throws IOException
     */
    @Override
    void writeContent (RecordOutputStream s) throws IOException {
        s.startRecord(ModuleSerializationTags.FUNCTIONAL_AGENT, serializationSchema);
        super.writeContent(s);
        s.writeShortCompressed(argumentNames.length);
        for (int i = 0; i < argumentNames.length; ++i) {
            // Write out empty strings for null argument names.
            String argName = argumentNames[i];
            s.writeUTF(argName == null ? "" : argName);
        }
        typeExpr.write(s);
        
        s.endRecord();
    }
    
    /**
     * Read the content of an instance of an FunctionalAgent
     * @param s
     * @param mti
     * @param msgLogger the logger to which to log deserialization messages.
     * @throws IOException
     */
    @Override
    void readContent (RecordInputStream s, ModuleTypeInfo mti, CompilerMessageLogger msgLogger) throws IOException {
        // Look for record header.
        RecordHeaderInfo rhi = s.findRecord(ModuleSerializationTags.FUNCTIONAL_AGENT);
        if (rhi == null) {
            throw new IOException ("Unable to find FunctionalAgent record header.");
        }
        DeserializationHelper.checkSerializationSchema(rhi.getSchema(), serializationSchema, mti.getModuleName(), "FunctionalAgent", msgLogger);

        super.readContent(s, mti, msgLogger);
        
        int nArgs = s.readShortCompressed();
        argumentNames = new String[nArgs];
        for (int i = 0; i < nArgs; ++i) {
            // Convert empty strings to nulls.
            String nextString = s.readUTF();
            argumentNames[i] = nextString.equals("") ? null : nextString;
        }
        typeExpr = TypeExpr.load(s, mti, msgLogger);

        s.skipRestOfRecord();
    }
}