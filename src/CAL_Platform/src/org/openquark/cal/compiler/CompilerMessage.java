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
 * CompilerMessage.java
 * Created: July 4, 2002
 * By: Bo Ilic (from Luke's original Compiler.CompilationError)
 */
package org.openquark.cal.compiler;

/**
 * A class to represent messages occurring during compilation such as errors, fatal
 * errors, warnings, and informational messages.
 * In this context 'compilation' refers to the entire process of generating a CAL
 * program from source.  This would include such steps as generation of expression 
 * format and type checking (performed by the CALCompiler), machine specific code 
 * generation, java source/byte code generation, etc.
 * Creation date: (July 4, 2002).
 * @author Bo Ilic (from Luke's original Compiler.CompilationError)
 */
public final class CompilerMessage {

    /** The source position associated with this message. Can be null. */
    private final SourceRange sourceRange;
    /** The entity associated with this message. Can be null. */
    private final Identifier associatedEntity;
    /** The message kind associated with this message. Cannot be null. */
    private final MessageKind messageKind;
    /** The exception associated with this message. Can be null. */
    private final Exception exception;
    
    /**
     * Warning- this class should only be used by the CAL compiler implementation. It is not part of the external API of the CAL platform.
     * 
     * This error is thrown and caught internally by the compiler as a way of signalling that compilation should abort.
     * These may occur if too many errors are logged, or in the presence of certain fatal errors.
     * 
     * This class is intended to be internal to the compiler.
     */     
    public static final class AbortCompilation extends Error {

        private static final long serialVersionUID = -6078350930009279716L;

        AbortCompilation() {
        }
    }    

    /**
     * Type-safe enum pattern to represent the severity of compilation error messages.
     * Creation date: (July 4, 2002).
     * @author Bo Ilic
     */
    public static final class Severity implements Comparable<Severity> {
       
        private final String description;
        private final int level;
        
        /** Purely informational, and not a problem of any sort. */       
        static final public Severity INFO = new Severity ("Info", 1);
               
        static final public Severity WARNING = new Severity ("Warning", 2);
        
        /**
         * Normal level when reporting a user error in CAL source.
         * Compilation can proceed, but should not be able to run the resulting program.
         */
        static final public Severity ERROR = new Severity ("Error", 3);
        
        /**
         * Fatal errors should result in compilation immediately stopping.
         * For example, this level is used for programming errors in the compiler.
         */
        static final public Severity FATAL = new Severity ("Fatal Error", 4);
                   
        private Severity (String description_, int level_) {
            description = description_;
            level = level_;    
        }
         
        @Override
        public String toString() {
            return description;
        } 
        
        /** {@inheritDoc} */
        public int compareTo(Severity o) {
            int anotherLevel = o.level;
            return level < anotherLevel ? -1 : (level == anotherLevel ? 0 : 1);           
        }                     
    }
    
    /**
     * Represents the name of the CAL entity associated with a {@link CompilerMessage}. Having this in a compiler message
     * helps the user in deciphering the message, especially when the message is the result of compiling a programmatically
     * constructed source model (which has no source positions) or the result of loading a compiled module file.
     *
     * @author Joseph Wong
     */
    public static final class Identifier {
        
        /**
         * The category of the name. e.g. top-level function, type cons, data cons...
         */
        private final Category category;
        
        /**
         * The name of the entity associated with the message.
         */
        /*
         * @implementation
         * This may need to be more general than QualifiedName as we associate other kinds of entities
         * with messages, e.g. local functions, class instances.
         */
        private final QualifiedName name;
        
        /**
         * An enumeration class containing constants for the different categories of entities that could be associated with
         * a message.
         *
         * @author Joseph Wong
         */
        public static final class Category {
            
            /**
             * A textual description of the status. For debug purposes.
             */
            private final String description;
            
            /**
             * Private constructor for this enumeration class.
             * @param description a textual description of the status. For debug purposes.
             */
            private Category(final String description) {
                if (description == null) {
                    throw new NullPointerException();
                }
                this.description = description;
            }
            
            /** Category constant for a data constructor. */
            public static final Category
                DATA_CONSTRUCTOR = new Category("DATA_CONSTRUCTOR");
            
            /** Category constant for a type constructor. */
            public static final Category
                TYPE_CONSTRUCTOR = new Category("TYPE_CONSTRUCTOR");
            
            /** Category constant for a type class. */
            public static final Category
                TYPE_CLASS = new Category("TYPE_CLASS");
            
            /** Category constant for a top-level function. */
            public static final Category
                TOP_LEVEL_FUNCTION = new Category("TOP_LEVEL_FUNCTION");
            
            /** Category constant for a class method. */
            public static final Category
                CLASS_METHOD = new Category("CLASS_METHOD");
            
            /** {@inheritDoc} */
            @Override
            public String toString() {
                return description;
            }
        }
        
        /**
         * Private constructor for this class. Instances should be constructed via the factory methods.
         * @param category the category of the name.
         * @param name the name of the entity associated with the message.
         */
        private Identifier(final Category category, final QualifiedName name) {
            if (category == null || name == null) {
                throw new NullPointerException();
            }
            
            this.category = category;
            this.name = name;
        }
        
        /**
         * Factory method for constructing an instance representing a top-level function name.
         * If the name is null, then null is returned.
         * @param name the qualified name of the top-level function. Can be null.
         * @return an instance of this class, or null if the specified name is null.
         */
        static Identifier makeFunction(final QualifiedName name) {
            if (name == null) {
                return null;
            } else {
                return new Identifier(Category.TOP_LEVEL_FUNCTION, name);
            }
        }
        
        /**
         * Factory method for constructing an instance representing a type constructor name.
         * If the name is null, then null is returned.
         * @param name the qualified name of the type constructor. Can be null.
         * @return an instance of this class, or null if the specified name is null.
         */
        static Identifier makeTypeCons(final QualifiedName name) {
            if (name == null) {
                return null;
            } else {
                return new Identifier(Category.TYPE_CONSTRUCTOR, name);
            }
        }
        
        /**
         * Factory method for constructing an instance representing a data constructor name.
         * If the name is null, then null is returned.
         * @param name the qualified name of the data constructor. Can be null.
         * @return an instance of this class, or null if the specified name is null.
         */
        static Identifier makeDataCons(final QualifiedName name) {
            if (name == null) {
                return null;
            } else {
                return new Identifier(Category.DATA_CONSTRUCTOR, name);
            }
        }
        
        /**
         * Factory method for constructing an instance representing a type class name.
         * If the name is null, then null is returned.
         * @param name the qualified name of the type class. Can be null.
         * @return an instance of this class, or null if the specified name is null.
         */
        static Identifier makeTypeClass(final QualifiedName name) {
            if (name == null) {
                return null;
            } else {
                return new Identifier(Category.TYPE_CLASS, name);
            }
        }
        
        /**
         * Factory method for constructing an instance representing a class method name.
         * If the name is null, then null is returned.
         * @param name the qualified name of the class method. Can be null.
         * @return an instance of this class, or null if the specified name is null.
         */
        static Identifier makeClassMethod(final QualifiedName name) {
            if (name == null) {
                return null;
            } else {
                return new Identifier(Category.CLASS_METHOD, name);
            }
        }
        
        /**
         * @return the category of the name.
         */
        public Category getCategory() {
            return category;
        }
        
        /**
         * @return the name of the entity associated with the message.
         */
        public QualifiedName getName() {
            return name;
        }
        
        /**
         * @return the name of the module containing the entity associated with the message. 
         */
        public ModuleName getModuleName() {
            return name.getModuleName();
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return "[Identifier: category=" + category + " name=" + name + "]";
        }
    }

    /**
     * Construct from severity, errorNode and message
     * @param sourcePositionNode the parse tree node associated with the message, or null if there wasn't any.
     */
    CompilerMessage(ParseTreeNode sourcePositionNode, MessageKind messageKind) {
        this(sourcePositionNode == null ? null : sourcePositionNode.getAssemblySourceRange(), messageKind, null);        
    }
    
    CompilerMessage(ParseTreeNode sourcePositionNode, MessageKind messageKind, Exception exception) {
        this(sourcePositionNode == null ? null : sourcePositionNode.getAssemblySourceRange(), messageKind, exception);        
    }    
    
    public CompilerMessage(SourceRange sourceRange, MessageKind messageKind) {
        this(sourceRange, messageKind, null);        
    }
    
    /**
     * Construct from severity and message
     */
    public CompilerMessage(MessageKind messageKind_) {
        this((SourceRange)null, messageKind_, null);
    }

    /**
     * Construct from severity, message and exception
     */
    public CompilerMessage(MessageKind messageKind_, Exception exception_) {
        this((SourceRange)null, messageKind_, exception_);      
    }

    /**
     * Construct from source position, severity, message and exception
     */
    CompilerMessage(SourceRange sourceRange_, MessageKind messageKind_, Exception exception_) {
        this(sourceRange_, null, messageKind_, exception_);
    }

    /**
     * Construct from source position, severity, message and associated entity
     */
    CompilerMessage(SourceRange sourceRange_, Identifier associatedEntity, MessageKind messageKind_) {
        this(sourceRange_, associatedEntity, messageKind_, null);
    }
    
    /**
     * Construct from all compiler message parameters
     */
    CompilerMessage(SourceRange sourceRange_, Identifier associatedEntity, MessageKind messageKind_, Exception exception_) {
        
        if (sourceRange_ == null && associatedEntity != null) {
            throw new IllegalArgumentException("If there is an associated entity, there should be a source range as well.");
        }
        
        this.associatedEntity = associatedEntity;
        messageKind = messageKind_;
        exception = exception_;
        sourceRange = sourceRange_;
    }

    /**
     * Copy the given compiler message and replace the source range with the 
     * given source range.
     * @param sourceRange the new value for the source range
     * @return a new CompilerMessage the same as the old except the source range is updated.
     */
    CompilerMessage copy(SourceRange sourceRange){
        return new CompilerMessage(sourceRange, associatedEntity, messageKind, exception);
    }

    /**
     * @return the CAL source position with which the message is associated.
     */
    public SourceRange getSourceRange() {
        return sourceRange;
    }

    /**
     * @return the severity of the compiler message
     */
    public Severity getSeverity() {
        return messageKind.getSeverity();
    }

    /**
     * Get message
     * Creation date: (2/2/01 5:59:03 PM)
     * @return the summary message of the error
     */
    public String getMessage() {
        return messageKind.getMessage();
    }
    
    /**
     * Get messageKind
     * @return MessageKind the message kind object for the error
     */
    public MessageKind getMessageKind() {
        return messageKind;
    }

    /**
     * Get underlying exception
     * Creation date: (2/2/01 5:59:03 PM)
     * @return Exception the underlying exception which caused the error
     */
    public Exception getException() {
        return exception;
    }

    /**
     * Render the message as a string
     */
    @Override
    public String toString() {
        
        String returnStr = messageKind.getMessage();
        
        // We display the name of the associated entity, if there is one
        if (associatedEntity != null) {
            Identifier.Category category = associatedEntity.getCategory();
            
            final String messageTemplateName;
            if (category == Identifier.Category.TOP_LEVEL_FUNCTION) {
                messageTemplateName = "AssociatedWithFunction";
            } else if (category == Identifier.Category.TYPE_CONSTRUCTOR) {
                messageTemplateName = "AssociatedWithTypeCons";
            } else if (category == Identifier.Category.DATA_CONSTRUCTOR) {
                messageTemplateName = "AssociatedWithDataCons";
            } else if (category == Identifier.Category.TYPE_CLASS) {
                messageTemplateName = "AssociatedWithTypeClass";
            } else if (category == Identifier.Category.CLASS_METHOD) {
                messageTemplateName = "AssociatedWithClassMethod";
            } else {
                messageTemplateName = "AssociatedWithGeneral";
            }
            
            final QualifiedName name = associatedEntity.getName();
            final String displayName;
            
            // we optimize the display of the associated entity: if it's defined in the same module
            // as reported by the source position, then we display only the unqualified name of the entity
            if (sourceRange != null && sourceRange.getSourceName().equals(name.getModuleName().toSourceText())) {
                displayName = name.getUnqualifiedName();
            } else {
                displayName = name.getQualifiedName();
            }
            
            returnStr = CALMessages.getString(messageTemplateName, displayName, returnStr);
        }

        if(sourceRange != null) {
            String sourceName = sourceRange.getSourceName();
            String sourcePositionStr = null;
            int line = sourceRange.getStartLine();
            int column = sourceRange.getStartColumn();
                        
            if (line > 0 && column > 0) {
                sourcePositionStr = CALMessages.getString("SourcePositionFormat", Integer.valueOf(line), Integer.valueOf(column));
            } else if (line > 0) {
                sourcePositionStr = CALMessages.getString("SourcePositionFormatNoColumn", Integer.valueOf(line));
            }                       
            
            if(sourceName != null && sourcePositionStr != null) {
                sourcePositionStr = CALMessages.getString("MessageFormat", sourceName, sourcePositionStr);
                returnStr = CALMessages.getString("MessageFormat", sourcePositionStr, returnStr);
            } else if (sourceName == null && sourcePositionStr != null) {
                returnStr = CALMessages.getString("MessageFormat", sourcePositionStr, returnStr);
            } else if (sourceName != null && sourcePositionStr == null) {
                returnStr = CALMessages.getString("MessageFormat", sourceName, returnStr);                
            }
        }
        
        returnStr = CALMessages.getString("MessageFormat", getSeverity().toString(), returnStr);
        
        // Add information about the exception if present
        if (exception != null) {        
            // Caused by            
            if (exception instanceof TypeException) {
                //todoBI our own internal exception classes to do not need to display their names since the resulting
                //error message is intended to be a "user readable" string. There are others except TypeException...
                returnStr = CALMessages.getString("MessageWithTypeException", returnStr, exception.getLocalizedMessage());
            } else {
                returnStr = CALMessages.getString("MessageWithOtherException", new Object[] {returnStr, exception.getClass().getName(), exception.getLocalizedMessage()});
            }
        }
        
        return returnStr;
    }    
}
