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
 * JavaExpression.java
 * Creation date: Sep 10, 2003.
 * By: Edward Lam
 */
package org.openquark.cal.internal.javamodel;

import java.util.Arrays;

import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.services.Assert;


/**
 * Object representation for a Java expression.
 * 
 * Note that it is important that the classes in the Java Source Model such as JavaExpression be
 * independent of the particular Java Model -> Byte Code tool (such as BCEL or ASM) that we use.
 * In particular, no classes from ASM or BCEL should be used anywhere in the Java Source Model
 * (or Java Source Model to Java source generator). 
 * 
 * @author Edward Lam
 */
public abstract class JavaExpression {

    public static final JavaExpression[] EMPTY_JAVA_EXPRESSION_ARRAY = new JavaExpression[0];
    public static final JavaTypeName[] EMPTY_TYPE_NAME_ARRAY = new JavaTypeName[0];
    
    /**
     * Accepts the visitation of a visitor, which implements the
     * JavaModelVisitor interface. This abstract method is to be overriden
     * by each concrete subclass so that the corrent visit method on the
     * visitor may be called based upon the type of the element being
     * visited. Each concrete subclass of JavaExpression should correspond
     * one-to-one with a visit method declaration in the SourceModelVisitor
     * interface.
     * <p>
     * 
     * As the JavaModelVisitor follows a more general visitor pattern
     * where arguments can be passed into the visit methods and return
     * values obtained from them, this method passes through the argument
     * into the visit method, and returns as its return value the return
     * value of the visit method.
     * <p>
     * 
     * Nonetheless, for a significant portion of the common cases, the state of the
     * visitation can simply be kept as member variables within the visitor itself,
     * thereby eliminating the need to use the argument and return value of the
     * visit methods. In these scenarios, the recommended approach is to use
     * {@link Void} as the type argument for both <code>T</code> and <code>R</code>, and
     * pass in null as the argument, and return null as the return value.
     * <p>
     * 
     * @see JavaModelVisitor
     * 
     * @param <T> the argument type. If the visitation argument is not used, specify {@link Void}.
     * @param <R> the return type. If the return value is not used, specify {@link Void}.
     * 
     * @param visitor
     *            the visitor
     * @param arg
     *            the argument to be passed to the visitor's visitXXX method
     * @return the return value of the visitor's visitXXX method
     */
    public abstract <T, R> R accept(JavaModelVisitor<T, R> visitor, T arg);
    
    /**
     * A helper class to output an expression as a string.
     * @author Edward Lam
     */
    static class Stringifier {

        private static final String EOL = System.getProperty("line.separator");
        private static final String INDENT = "    ";

        /**
         * Return a string representation of an expression.
         * @param javaExpression the expression
         * @return a String representation.
         */
        public static String toString(JavaExpression javaExpression) {
            return toString(javaExpression, 0).toString();
        }
        
        /*
         * Not intended to be instantiated.
         */
        private Stringifier() {
            // Constructor declared private to prevent instantiation.
        }
        
        private static StringBuilder toString(JavaTypeName javaTypeName, int indent) {
            StringBuilder sb = new StringBuilder();
            emitLine(sb, indent, "Type Name: " + javaTypeName.getName());
            return sb;
        }
        
        /**
         * Helper method for the other toString()
         * @param javaExpression
         * @param indent the current indent level.
         * @return StringBuilder the StringBuilder to which to add the representation of the current expression
         */
        private static StringBuilder toString(JavaExpression javaExpression, int indent) {
            StringBuilder sb = new StringBuilder();
            if (javaExpression instanceof Assignment) {
                Assignment assignment = (Assignment)javaExpression;

                emitLine(sb, indent, "Assignment: ");
                sb.append(toString(assignment.getLeftHandSide(), indent + 1));
                sb.append(toString(assignment.getValue(), indent + 1));
                
            } else if (javaExpression instanceof CastExpression) {
                CastExpression castExpression = (CastExpression)javaExpression;

                emitLine(sb, indent, "Cast Expression: ");
                sb.append(toString(castExpression.getCastType(), indent + 1));
                sb.append(toString(castExpression.getExpressionToCast(), indent + 1));
                
            } else if (javaExpression instanceof ArrayCreationExpression) {
                ArrayCreationExpression arrayCreation = (ArrayCreationExpression)javaExpression;
                
                emitLine(sb, indent, "Array Creation Expression: ");
                sb.append(toString(arrayCreation.getArrayElementTypeName(), indent + 1));
                               
                for (int i = 0, nElements = arrayCreation.getNElementValues(); i < nElements; i++) {
                    sb.append(toString(arrayCreation.getElementValue(i), indent + 1));
                }
                
            } else if (javaExpression instanceof ClassInstanceCreationExpression) {
                ClassInstanceCreationExpression classInstanceCreationExpression = (ClassInstanceCreationExpression)javaExpression;

                emitLine(sb, indent, "Class Instance Creation Expression: ");
                sb.append(toString(classInstanceCreationExpression.getClassName(), indent + 1));
                              
                for (int i = 0, nArgs = classInstanceCreationExpression.getNArgs(); i < nArgs; i++) {
                    sb.append(toString(classInstanceCreationExpression.getArg(i), indent + 1));
                }

            } else if (javaExpression instanceof OperatorExpression) {
                OperatorExpression operatorExpression = (OperatorExpression)javaExpression;

                emitLine(sb, indent, "Operator Expression: ");
                emitLine(sb, indent + 1, operatorExpression.getJavaOperator().getSymbol());

                sb.append(toString(operatorExpression.getArgument(0), indent + 1));

                if (!(operatorExpression instanceof OperatorExpression.Unary)) {
                    sb.append(toString(operatorExpression.getArgument(1), indent + 1));
                }
                if (operatorExpression instanceof OperatorExpression.Ternary) {
                    sb.append(toString(operatorExpression.getArgument(2), indent + 1));
                }

            } else if (javaExpression instanceof InstanceOf) {
                InstanceOf instanceOf = (InstanceOf)javaExpression;

                emitLine(sb, indent, "Instance of: ");
                sb.append(toString(instanceOf.getJavaExpression(), indent + 1));
                sb.append(toString(instanceOf.getReferenceType(), indent + 1));

            } else if (javaExpression instanceof MethodInvocation) {
                MethodInvocation methodInvocation = (MethodInvocation)javaExpression;

                emitLine(sb, indent, "Method Invocation: ");
                if (methodInvocation instanceof MethodInvocation.Instance) {
                    
                    JavaExpression invocationTarget = ((MethodInvocation.Instance)methodInvocation).getInvocationTarget();
                    if (invocationTarget != null) {                    
                        sb.append(toString(invocationTarget, indent + 1));
                    }
                } else {
                    JavaTypeName invocationClass = ((MethodInvocation.Static)methodInvocation).getInvocationClass();
                    sb.append(toString(invocationClass, indent + 1));
                }
                emitLine(sb, indent + 1, methodInvocation.getMethodName());
                
                for (int i = 0, nArgs = methodInvocation.getNArgs(); i < nArgs; i++) {
                    sb.append(toString(methodInvocation.getArg(i), indent + 1));
                }

            } else if (javaExpression instanceof LiteralWrapper) {
                LiteralWrapper literalWrapper = (LiteralWrapper)javaExpression;

                emitLine(sb, indent, "Literal: " + literalWrapper.getLiteralObject());

            } else if (javaExpression instanceof ArrayAccess) {
                ArrayAccess arrayAccess = (ArrayAccess)javaExpression;

                emitLine(sb, indent, "Array Access: ");
                sb.append(toString(arrayAccess.getArrayReference(), indent + 1));
                sb.append(toString(arrayAccess.getArrayIndex(), indent + 1));
                
            } else if (javaExpression instanceof ArrayLength) {
                ArrayLength arrayLength = (ArrayLength)javaExpression;

                emitLine(sb, indent, "Array Length: ");
                sb.append(toString(arrayLength.getArrayReference(), indent + 1));
                
            } else if (javaExpression instanceof JavaField) {
                JavaField javaField = (JavaField)javaExpression;

                emitLine(sb, indent, "Field: " + javaField.getFieldName() + ".  Type: " + javaField.getFieldType().getName());

            } else if (javaExpression instanceof LocalVariable) {
                LocalVariable localVariable = (LocalVariable)javaExpression;

                emitLine(sb, indent, "Local var: " + localVariable.getName());

            } else if (javaExpression instanceof MethodVariable) {
                MethodVariable methodVariable = (MethodVariable)javaExpression;

                emitLine(sb, indent, "Method var: " + methodVariable.getName());

            } else if (javaExpression instanceof LocalName) {
                LocalName localName = (LocalName)javaExpression;

                emitLine(sb, indent, "Local name: " + localName.getName());

            } else {
                emitLine(sb, indent, "(Unrecognized expression): " + javaExpression);
            }
            
            return sb;
        }

        /**
         * Emit an indent.
         * @param sb the StringBuilder to which to add an indent.
         * @param indent the number of indents to add.
         * @return StringBuilder sb, returned for convenience.
         */
        private static StringBuilder emitIndent(StringBuilder sb, int indent) {
            for (int i = 0; i < indent; i++) {
                sb.append(INDENT);
            }
            return sb;
        }

        /**
         * Emit an indent, some text, and an EOL.
         * @param sb the StringBuilder to which to add an indent.
         * @param text the text to add.
         * @param indent the number of indents to add.
         */
        private static void emitLine(StringBuilder sb, int indent,String text) {
            emitIndent(sb, indent);
            sb.append(text + EOL);
        }
    }
    
    /**
     * An assignment expression.
     * @author Edward Lam
     */
    public static class Assignment extends JavaExpression {

        /** The left hand side of the assignment. */
        private final Nameable leftHandSide;
        
        /** The value to which the left hand side is being assigned. */
        private final JavaExpression value;
        
        /**
         * Constructor for an Assignment.
         * @param leftHandSide the left hand side of the assingment.
         * @param value the value to which the left hand side is being assigned.
         */
        public Assignment(Nameable leftHandSide, JavaExpression value) {
            Assert.isNotNull(leftHandSide);
            Assert.isNotNull(value);

            this.leftHandSide = leftHandSide;
            this.value = value;
        }
        
        /**
         * Get the left hand side of the assignment.
         * @return Nameable
         */
        public Nameable getLeftHandSide() {
            return leftHandSide;
        }

        /**
         * Get the value being assigned.
         * @return JavaExpression
         */
        public JavaExpression getValue() {
            return value;
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public <T, R> R accept(JavaModelVisitor<T, R> visitor, T arg) {
            return visitor.visitAssignmentExpression(this, arg);
        } 
    }
    
    /**
     * A cast expression.
     * @author Edward Lam
     */
    public static class CastExpression extends JavaExpression {
        
        /** The expression being cast. */
        private final JavaExpression expressionToCast;

        /** The type to which the expression is being cast. */
        private final JavaTypeName castType;
        
        /**
         * Constructor for a CastExpression.
         * @param castType the type to which the expression is being cast.
         * @param expressionToCast the expression being cast.
         */
        public CastExpression(JavaTypeName castType, JavaExpression expressionToCast) {
            Assert.isNotNull(castType);
            Assert.isNotNull(expressionToCast);

            this.castType = castType;
            this.expressionToCast = expressionToCast;
        }

        /**
         * Get the type to which the expression is being cast.
         * @return JavaTypeName
         */
        public JavaTypeName getCastType() {
            return castType;
        }

        /**
         * Get the expression being cast.
         * @return JavaExpression
         */
        public JavaExpression getExpressionToCast() {
            return expressionToCast;
        }
       
        /**
         * {@inheritDoc}
         */
        @Override
        public <T, R> R accept(JavaModelVisitor<T, R> visitor, T arg) {
            return visitor.visitCastExpression(this, arg);
        }     
    }
    
    /**
     * Used to create and initialize a 1-dimensional array. See ClassInstanceCreationExpression for creating (but not initializing)
     * multi-dimensional arrays. For example, this class can be used to generate expressions such as:
     * new String[] {"this", "is", "an", "example"}
     * 
     * @author Bo Ilic   
     */
    public static final class ArrayCreationExpression extends JavaExpression {
        
        /** element type of the array. For example, for new String[] {"this", "is", "an", "example"} this is String. */
        private final JavaTypeName arrayElementTypeName;
        
        /** 
         * array element values. For example, for new String[] {"this", "is", "an", "example"} this is
         * ["this", "is", "an", "example"]. These are assumed to have the *same* type as the arrayElementTypeName.
         */
        private final JavaExpression[] elementValues;
        
        public ArrayCreationExpression(JavaTypeName arrayElementTypeName, JavaExpression[] elementValues) {
            
            Assert.isNotNull(arrayElementTypeName);
            Assert.isNotNull(elementValues);
            
            this.arrayElementTypeName = arrayElementTypeName;
            this.elementValues = elementValues.clone(); 
            
            assert (!checkForNullElements()) : "It is not valid to create an ArrayCreationExpression with a null value for an array element.";
        }

        /**
         * @return true if any of the element values are null.
         */
        private boolean checkForNullElements () {
            for (int i = 0, n = elementValues.length; i < n; ++i) {
                if (elementValues[i] == null) {
                    return true;
                }
            }
            return false;
        }
        
        /**         
         * @return JavaTypeName element type of the array. For example, for new String[] {"this", "is", "an", "example"} this is String.
         */
        public JavaTypeName getArrayElementTypeName() {
            return arrayElementTypeName;
        }
                        
        public int getNElementValues() {
            return elementValues.length;
        }        
        public JavaExpression getElementValue(int n) {
            return elementValues[n];
        }    
        
        /**
         * {@inheritDoc}
         */
        @Override
        public <T, R> R accept(JavaModelVisitor<T, R> visitor, T arg) {
            return visitor.visitArrayCreationExpression(this, arg);
        } 
    }
    
    /**
     * An expression to create an instance of a class.
     * @author Edward Lam
     */
    public static class ClassInstanceCreationExpression extends JavaExpression {
        /** The type of the class instance to create. */
        private final JavaTypeName className;
        
        /** Arguments to the class instance constructor.  For array types, this will be the sizes of the dimensions to instantiate. */
        private final JavaExpression[] args;
        
        /** The types of the parameters to the constructor. */
        private final JavaTypeName[] paramTypes;
        
        /**
         * Constructor for a no-argument ClassInstanceCreationExpression.
         * @param className the type of the class instance to create.
         */
        public ClassInstanceCreationExpression(JavaTypeName className) {
            this(className, EMPTY_JAVA_EXPRESSION_ARRAY, EMPTY_TYPE_NAME_ARRAY);
        }

        /**
         * Constructor for an array creation expression.
         * @param arrayClassName the JavaTypeName for the array type.
         * @param arrayDimensionSizes the sizes of the dimensions to instantiate.
         */
        public ClassInstanceCreationExpression(JavaTypeName arrayClassName, int[] arrayDimensionSizes) {
            this(arrayClassName, getWrappedInts(arrayDimensionSizes), getIntTypeArray(arrayDimensionSizes.length));
        }
        
        /**
         * Convert an array of int to an array of LiteralWrapper, each of which wraps the corresponding int.
         * @param ints the array of ints to wrap.
         * @return LiteralWrapper[] the array of wrapped ints.
         */
        private static LiteralWrapper[] getWrappedInts(int[] ints) {
            LiteralWrapper[] intWrapperArray = new LiteralWrapper[ints.length];
            for (int i = 0; i < ints.length; i++) {
                intWrapperArray[i] = LiteralWrapper.make(Integer.valueOf(ints[i]));
            }
            return intWrapperArray;
        }
        
        /**
         * Get an array of JavaTypeName, filled with JavaTypeName.INT.
         * @param arraySize the size of the array.
         * @return JavaTypeName[] the array.
         */
        private static JavaTypeName[] getIntTypeArray(int arraySize) {
            JavaTypeName[] intTypeArray = new JavaTypeName[arraySize];
            Arrays.fill(intTypeArray, JavaTypeName.INT);
            return intTypeArray;
        }
        
        /**
         * Constructor for a single-argument ClassInstanceCreationExpression.
         * @param className the type of the class instance to create.
         * @param arg the single argument to the class instance constructor.
         * @param paramType the type of the single parameter to the class instance constructor.
         */
        public ClassInstanceCreationExpression(JavaTypeName className, JavaExpression arg, JavaTypeName paramType) {
            this(className, new JavaExpression[] {arg}, new JavaTypeName[] {paramType});
        }
        
        /**
         * Constructor for a ClassInstanceCreationExpression.
         * @param className the type of the class instance to create.
         * @param args the arguments to the class instance constructor.
         * @param paramTypes the types of the parameters to the class instance constructor.
         */
        public ClassInstanceCreationExpression(JavaTypeName className, JavaExpression[] args, JavaTypeName[] paramTypes) {
            Assert.isNotNull(className);
            Assert.isNotNull(args);
            Assert.isNotNull(paramTypes);
            if (args.length != paramTypes.length) {
                throw new IllegalArgumentException("arg array must be the same length as arg types array.");
            }

            this.className = className;
            this.args = args;
            this.paramTypes = paramTypes;
        }

        /**
         * Get the type of the class instance to create.
         * @return JavaTypeName
         */
        public JavaTypeName getClassName() {
            return className;
        }
        
        /**
         * Return whether this is an array instance creation expression.
         * @return boolean
         */
        public boolean isArrayCreationExpression() {
            return className instanceof JavaTypeName.Reference.Array;
        }
        
        /**         
         * @return the number of arguments to be passed to the constructor. For array types, this will be the number of dimensions of the array
         *     that are being initialized by the constructor. This is greater than or equal to 1 and less than or equal to the number of dimensions
         *     of the array. For example for, new String[3][5][] this will be 2. For new String[2][5][4] this will be 3. In both cases the
         *     array being initialized is a 3-dimensional String array.
         */
        public int getNArgs() {
            return args.length;
        }
        
        /**         
         * @param n zero-indexed argument number of the constructor, or zero-indexed dimension of the array for array types.
         * @return The expression for the nth constructor argument. For arrays, this is the int-valued expression setting the size of the nth dimension.
         */
        public JavaExpression getArg(int n) {
            return args[n];
        }
                
        /**         
         * @param n zero-indexed argument number of the constructor, or zero-indexed dimension of the array for array types.
         * @return The type of the nth constructor argument. For arrays, this is the type of the dimension-size setting expression for the
         *     nth dimension i.e. it is always int.
         */        
        public JavaTypeName getParamType(int n) {
            return paramTypes[n];
        }              
    
        /**
         * {@inheritDoc}
         */
        @Override
        public <T, R> R accept(JavaModelVisitor<T, R> visitor, T arg) {
            return visitor.visitClassInstanceCreationExpression(this, arg);
        }     
    }

    /**
     * A JavaExpression that uses symbolic operators.
     *  ConditionalExpression in the Java grammar.
     * 
     * warning: do not add ++ or -- as subtypes of OperatorExpression. Because of their side effects, they are not
     * true operators and should not be treated as subclasses of JavaExpression.OperatorExpression. For example, the
     * argument expression of ++ cannot be an arbitrary expression of the correct type, it must be a variable name.      
     * 
     * @author Edward Lam
     */
    public abstract static class OperatorExpression extends JavaExpression {
        
        /** The operator for this expression. */
        private final JavaOperator javaOp;
        
        /** The expression(s) operated upon by the operator. */
        private final JavaExpression[] args;


        /**
         * Constructor for an OperatorExpression.
         * @param javaOp the operator for this expression.
         * @param args the expression(s) operated upon by the operator.
         */
        OperatorExpression(JavaOperator javaOp, JavaExpression[] args) {
            Assert.isNotNull(javaOp);
            Assert.isNotNull(args);

            this.javaOp = javaOp;
            this.args = args;
        }
        
        /**
         * Get the operator.
         * @return JavaOperator
         */
        public JavaOperator getJavaOperator() {
            return javaOp;
        }              
        
        /**
         * Get the nth argument to the operator in this expression.
         * @param n
         * @return JavaExpression
         */
        public JavaExpression getArgument(int n) {
            return args[n];
        }        

        /**
         * An OperatorExpression with a single expression argument.
         * @author Edward Lam
         */
        public static class Unary extends OperatorExpression {
                             
            /**
             * Constructor for a unary operator expression.
             * @param javaOp the operator.
             * @param arg the argument            
             */
            public Unary(JavaOperator javaOp, JavaExpression arg) {
                super(javaOp, new JavaExpression[] {arg});                
            }
                       
            /**
             * {@inheritDoc}
             */
            @Override
            public <T, R> R accept(JavaModelVisitor<T, R> visitor, T arg) {
                return visitor.visitUnaryOperatorExpression(this, arg);
            }     
        }
        
        /**
         * An OperatorExpression with two expression arguments.
         *   The operator should be infix.
         * @author Edward Lam
         */
        public static class Binary extends OperatorExpression {
            /**
             * Constructor for a binary operator expression
             * @param javaOp the operator.
             * @param args the arguments
             */
            public Binary(JavaOperator javaOp, JavaExpression[] args) {
                super(javaOp, args);
            }

            /**
             * Constructor for a binary operator expression
             * @param javaOp the operator.
             * @param arg1 the first argument
             * @param arg2 the second argument
             */
            public Binary(JavaOperator javaOp, JavaExpression arg1, JavaExpression arg2) {
                super(javaOp, new JavaExpression[] {arg1, arg2});
            }
            
            /**
             * For example, the notComposed operator for LESS_THAN_INT is GREATER_THAN_EQUALS_INT since
             * not (x < y) is equals to x >= y for all x, y that are ints.
             *
             * It is not the case that the notComposed operator for LESS_THAN_DOUBLE is GREATER_THAN_DOUBLE since
             * the above equation does not hold for Double.NAN.
             * 
             * @return the operator expression where the operator is replaces by the not composed operator, or
             *       null if there is no such operator.
             */            
            public OperatorExpression.Binary getNotComposedOperatorExpr() {
                JavaOperator notComposedOperator = super.javaOp.getNotComposedOperator();
                if (notComposedOperator != null) {
                    return new OperatorExpression.Binary(notComposedOperator, super.args);
                }
                
                return null;
            }   
            
            /**
             * {@inheritDoc}
             */
            @Override
            public <T, R> R accept(JavaModelVisitor<T, R> visitor, T arg) {
                return visitor.visitBinaryOperatorExpression(this, arg);
            }     
        }
        
        /**
         * An OperatorExpression representing the ?: ternary operator.
         * @author Edward Lam
         */
        public static class Ternary extends OperatorExpression {         
            
            /**
             * Constructor for a ternary operator expression
             * @param arg1 the first argument. Must have a primitive boolean type.
             * @param arg2 the second argument. arg2 must have exactly the same (static) type as arg3.
             *     The various conversions as described in the Java language specification section 15.25 are 
             *     not supported.            
             * @param arg3 the third argument
             */
            public Ternary(JavaExpression arg1, JavaExpression arg2, JavaExpression arg3) {
                super(JavaOperator.TERNARY, new JavaExpression[] {arg1, arg2, arg3});
            }
            
            /**
             * {@inheritDoc}
             */
            @Override
            public <T, R> R accept(JavaModelVisitor<T, R> visitor, T arg) {
                return visitor.visitTernaryOperatorExpression(this, arg);
            }     
        }
    }
    
    /**
     * A conditional expression for the "instanceof" operator.
     * @author Edward Lam
     */
    public static class InstanceOf extends JavaExpression {
        
        /** The expression to test. */
        private final JavaExpression javaExpression;
        
        /** The type for which the expression will be tested. */
        private final JavaTypeName referenceType;
        
        /**
         * Constructor for an InstanceOf expression
         * @param javaExpression the expression to test.
         * @param referenceType the type for which the expression will be tested.
         */
        public InstanceOf(JavaExpression javaExpression, JavaTypeName referenceType) {
            Assert.isNotNull(javaExpression);
            Assert.isNotNull(referenceType);

            this.javaExpression = javaExpression;
            this.referenceType = referenceType;
        }
        
        /**
         * Get the expression to test.
         * @return JavaExpression
         */
        public JavaExpression getJavaExpression() {
            return javaExpression;
        }

        /**
         * Get the type for which the expression will be tested.
         * @return JavaTypeName.ReferenceType
         */
        public JavaTypeName getReferenceType() {
            return referenceType;
        }
      
        /**
         * {@inheritDoc}
         */
        @Override
        public <T, R> R accept(JavaModelVisitor<T, R> visitor, T arg) {
            return visitor.visitInstanceOfExpression(this, arg);
        }     
    }
    
    /**
     * A method invocation.
     * @author Edward Lam
     */
    public static abstract class MethodInvocation extends JavaExpression {     
        
        /** The name of the method being invoked. */
        private final String methodName;
        
        /** The arguments to the method invocation. */
        private final JavaExpression[] args;
        
        /** The types in the method signature. */
        private final JavaTypeName[] paramTypes;
        
        /** The return type of the method. */
        private final JavaTypeName returnType;
               
        /**
         * Represents a non-static method invocation within an expression.
         * 
         * For example, the calls to myMethod below.
         * this.myMethod(1, "abc")
         * getFoo("abc").myMethod(1, "abc")
         * super.myMethod(1, "abc")
         * 
         * Note: we do not allow invocation of static methods through an object reference.
         * 
         * @author Bo Ilic
         */
        public static final class Instance extends MethodInvocation {
            
            /** 
             * The target of the invocation (ie. the object being invoked).
             * Use null for the current object and invocationType != SPECIAL to invoke via the "this" reference.
             * Use null for the current object and invocationType == SPECIAL to invoke via the "super" reference.            
             */
            private final JavaExpression invocationTarget; 
            
            /** The invocation type. */
            private final InvocationType invocationType;
            
            /** The class/interface declaring the method being invoked. This may be null. */
            private final JavaTypeName declaringClass;
            
            /**
             * Constructor for a method invocation on an object reference. Cannot be used for static methods.
             * @param invocationTarget the target of the invocation (ie. the object being invoked), or null for the current object.
             *   (i.e. make the call using the "this" pointer if invocation type != SPECIAL, and the "super" pointer if invocation type == SPECIAL)
             * @param methodName the name of the method being invoked.
             * @param returnType the return type of the method.
             * @param invocationType the method invocation type.
             */
            public Instance(JavaExpression invocationTarget, String methodName, JavaTypeName returnType, InvocationType invocationType) {
                this(invocationTarget, methodName, EMPTY_JAVA_EXPRESSION_ARRAY, EMPTY_TYPE_NAME_ARRAY, returnType, invocationType);
            }
            
            /**
             * Constructor for a single argument method invocation on an object reference. Cannot be used for static methods.
             * @param invocationTarget the target of the invocation (ie. the object being invoked), or null for the current object.
             *   (i.e. make the call using the "this" pointer if invocation type != SPECIAL, and the "super" pointer if invocation type == SPECIAL)
             * @param methodName the name of the method being invoked.
             * @param arg the argument to the method invocation
             * @param paramType the types of the param in the method signature
             * @param returnType the return type of the method.
             * @param invocationType the method invocation type.
             */
            public Instance(JavaExpression invocationTarget, String methodName, JavaExpression arg, JavaTypeName paramType, JavaTypeName returnType, InvocationType invocationType) {
                this(invocationTarget, methodName, new JavaExpression[]{arg}, new JavaTypeName[]{paramType}, returnType, invocationType);
            }

            /**
             * Most general constructor for a method invocation on an object reference. Cannot be used for static methods.
             * @param invocationTarget the target of the invocation (ie. the object being invoked), or null for the current object.
             *    (i.e. make the call using the "this" pointer if invocation type != SPECIAL, and the "super" pointer if invocation type == SPECIAL)
             * @param methodName the name of the method being invoked.
             * @param args the arguments to the method invocation
             * @param paramTypes the types of the params in the method signature
             * @param returnType the return type of the method
             * @param invocationType the method invocation type.
             */
            public Instance(JavaExpression invocationTarget, String methodName, JavaExpression[] args, JavaTypeName[] paramTypes, JavaTypeName returnType, InvocationType invocationType) {
                this (invocationTarget, methodName, null, args, paramTypes, returnType, invocationType);
            }            

            /**
             * Most general constructor for a method invocation on an object reference. Cannot be used for static methods.
             * @param invocationTarget the target of the invocation (ie. the object being invoked), or null for the current object.
             *    (i.e. make the call using the "this" pointer if invocation type != SPECIAL, and the "super" pointer if invocation type == SPECIAL)
             * @param methodName the name of the method being invoked.
             * @param declaringClass
             * @param args the arguments to the method invocation
             * @param paramTypes the types of the params in the method signature
             * @param returnType the return type of the method
             * @param invocationType the method invocation type.
             */
            public Instance(JavaExpression invocationTarget, String methodName, JavaTypeName declaringClass, JavaExpression[] args, JavaTypeName[] paramTypes, JavaTypeName returnType, InvocationType invocationType) {
                super(methodName, args, paramTypes, returnType);            
                
                if (invocationType == InvocationType.STATIC) {
                    throw new IllegalArgumentException("This MethodInvocation constructor is for non-static methods only.");
                }

                this.declaringClass = declaringClass;
                this.invocationTarget = invocationTarget;           
                this.invocationType = invocationType;                           
            }            
            
            /**
             * Get the target of the invocation i.e. the object reference that this method is invoked on. If this is null,
             * then use the "this" reference as the invocation target.
             * @return JavaExpression
             */
            public JavaExpression getInvocationTarget() {
                return invocationTarget;
            }
                       
            @Override
            public InvocationType getInvocationType() {
                return invocationType;
            }        
            
            public JavaTypeName getDeclaringClass () {
                return declaringClass;
            }
            
            /**
             * {@inheritDoc}
             */
            @Override
            public <T, R> R accept(JavaModelVisitor<T, R> visitor, T arg) {
                return visitor.visitInstanceMethodInvocationExpression(this, arg);
            }     
        }
        
        /**
         * Represents the invocation of a static method within an expression.
         * @author Bo Ilic
         */
        public static final class Static extends MethodInvocation {
                       
            /**
             * Name of the class that the static method is to be invoked from. This could be a subclass of the class in which the
             * static method is defined, and it is sometimes necessary to do this. For example, if package scope class A defines
             * a static public f, and public class B extends A, then B.f in a different package will not result in a compilation
             * error but A.f will.
             */
            private final JavaTypeName invocationClass; 
            
            /**
             * Constructor for invoking a static method via a class reference for a single argument method (e.g. Integer.valueOf("123")).
             * @param invocationClass name of the class that the static method is to be invoked from.
             * @param methodName the name of the method being invoked.
             * @param arg the argument to the method invocation
             * @param paramType the types of the params in the method signature
             * @param returnType the return type of the method        
             */
            public Static(JavaTypeName invocationClass, String methodName, JavaExpression arg, JavaTypeName paramType, JavaTypeName returnType) {
                this(invocationClass, methodName, new JavaExpression[] {arg}, new JavaTypeName[] {paramType}, returnType);          
            }            
            
            /**
             * Most general constructor for invoking a static method via a class reference (e.g. Integer.valueOf("123")).
             * @param invocationClass name of the class that the static method is to be invoked from.
             * @param methodName the name of the method being invoked.
             * @param args the arguments to the method invocation
             * @param paramTypes the types of the params in the method signature
             * @param returnType the return type of the method        
             */
            public Static(JavaTypeName invocationClass, String methodName, JavaExpression[] args, JavaTypeName[] paramTypes, JavaTypeName returnType) {
                super(methodName, args, paramTypes, returnType);
                
                Assert.isNotNull(invocationClass);           
                this.invocationClass = invocationClass;                      
            }            
            
            /** 
             * Name of the class that the static method is to be invoked from. This could be a subclass of the class in which the
             * static method is defined, and it is sometimes necessary to do this. For example, if package scope class A defines
             * a static public f, and public class B extends A, then B.f in a different package will not result in a compilation
             * error but A.f will.
             *         
             * @return name of the class that the static method is to be invoked from. Will be non-null.      
             */
            public JavaTypeName getInvocationClass() {
                return invocationClass;
            }            
           
            @Override
            public InvocationType getInvocationType() {
                return InvocationType.STATIC;
            }
            
            /**
             * {@inheritDoc}
             */
            @Override
            public <T, R> R accept(JavaModelVisitor<T, R> visitor, T arg) {
                return visitor.visitStaticMethodInvocationExpression(this, arg);
            }     
        }
    
        /**
         * Invocation type enum.
         * @author Edward Lam
         */
        public static class InvocationType {
            /** The name of the invocation type. */
            private final String typeName;

            /**
             * Constructor for an invocation type
             * @param typeName the name of the invocation.
             */
            private InvocationType(String typeName) {
                Assert.isNotNull(typeName);
                this.typeName = typeName;
            }
            
            /**
             * {@inheritDoc}
             */
            @Override
            public String toString() {
                return typeName;
            }
            
            /** Invocation with dispatch based on object's runtime type. */
            public static final InvocationType VIRTUAL      = new InvocationType("VIRTUAL");

            /** Invocation of instance initializer, private, superclass methods. */
            public static final InvocationType SPECIAL      = new InvocationType("SPECIAL");

            /** Invocation of class methods. */
            public static final InvocationType STATIC       = new InvocationType("STATIC");

            /** Invocation of interface methods. */
            public static final InvocationType INTERFACE    = new InvocationType("INTERFACE");
        }
     
        /**
         * Constructor for a method invocation.
         * @param methodName the name of the method being invoked.
         * @param args the arguments to the method invocation
         * @param paramTypes the types of the params in the method signature
         * @param returnType the return type of the method     
         */
        MethodInvocation(String methodName, JavaExpression[] args, JavaTypeName[] paramTypes, JavaTypeName returnType) {
            Assert.isNotNull(methodName);
            Assert.isNotNull(args);
            Assert.isNotNull(paramTypes);
            Assert.isNotNull(returnType);
            
            if (args.length != paramTypes.length) {
                throw new IllegalArgumentException("arg array must be the same length as arg types array.");
            }
                     
            this.methodName = methodName;
            this.args = args;
            this.paramTypes = paramTypes;
            this.returnType = returnType;                                
        }
              
        /**
         * Get the name of the method being invoked.
         * @return String
         */
        public String getMethodName() {
            return methodName;
        }
        
        /**         
         * @return the number of arguments of the method.
         */
        public int getNArgs() {
            return args.length;
        }
        
        /**         
         * @param n
         * @return the nth argument to the invocation of the method.
         */
        public JavaExpression getArg(int n) {
            return args[n];
        }
        
        /**         
         * @param n
         * @return the type of the nth argument of the method.
         */
        public JavaTypeName getParamType(int n) {
            return paramTypes[n];
        }     

        /**
         * @return JavaTypeName the return type of the method
         */
        public JavaTypeName getReturnType() {
            return returnType;
        }
        
        /**
         * @return InvocationType the invocation type
         */
        abstract public InvocationType getInvocationType();                      
        
        /**        
         * @return the method descriptor, as specified in the Java Virtual Machine Specification section 4.3. 
         */        
        public String getJVMMethodDescriptor() {
            StringBuilder sb = new StringBuilder("(");
            for (int i = 0, nArgs = args.length; i < nArgs; ++i) {
                sb.append(paramTypes[i].getJVMDescriptor());
            }
            sb.append(')').append(returnType.getJVMDescriptor());
            return sb.toString();
        }
    }
    
    /**
     * An expression that's not composed of other expressions.
     * "Primary" in the grammar.
     * @author Edward Lam
     */
    static abstract class Primary extends JavaExpression {
        // Empty base class for the set of expressions that aren't composed of other expressions.
    }

    /**
     * A primary with a name.
     *   This can be invoked or assigned (ie. it's not a literal).
     * "Name" in the grammar.
     * @author Edward Lam
     */
    public static abstract class Nameable extends Primary {
        // Empty base class for primary expressions that are nameable.
    }

    /**
     * A wrapper around a literal object value.
     * The object is one of: Integer, Double, String, Character, Boolean, Byte, Short, Float, Long, null;
     * @author Edward Lam
     */
    public static final class LiteralWrapper extends Primary {
        
        /** The "null" literal. */
        public static final LiteralWrapper NULL = new LiteralWrapper(null);
        /** The "true" literal. */
        public static final LiteralWrapper TRUE = new LiteralWrapper(Boolean.TRUE);
        /** The "false" literal. */
        public static final LiteralWrapper FALSE = new LiteralWrapper(Boolean.FALSE);
        
        /** The wrapped literal object. */
        private final Object literalObject;
        
        /**
         * Private constructor for a LiteralWrapper.
         * @param literalObject the object to wrap.
         */
        private LiteralWrapper(final Object literalObject) {
            this.literalObject = literalObject;
        }
        
        /**
         * Factory method for obtaining a LiteralWrapper. If the literal object specified is
         * null, {@link Boolean#TRUE} or {@link Boolean#FALSE}, then the constant
         * {@link #NULL}, {@link #TRUE} or {@link #FALSE} is respectively returned.
         * 
         * @param literalObject the object to wrap.
         * @return an instance of this class.
         */
        public static LiteralWrapper make(final Object literalObject) {
            if (literalObject == null) {
                return LiteralWrapper.NULL;
            } else if (Boolean.TRUE.equals(literalObject)) {
                return LiteralWrapper.TRUE;
            } else if (Boolean.FALSE.equals(literalObject)) {
                return LiteralWrapper.FALSE;
            } else {
                return new LiteralWrapper(literalObject);
            }
        }
        
        /**
         * Get the wrapped literal object.
         * @return Object the wrapped literal.  Null for the "null" literal.
         */
        public Object getLiteralObject() {
            return literalObject;
        }      
        
        /**
         * {@inheritDoc}
         */
        @Override
        public <T, R> R accept(JavaModelVisitor<T, R> visitor, T arg) {
            return visitor.visitLiteralWrapperExpression(this, arg);
        }     
    }
    
    /**
     * A Java class literal. For example:
     * <ul>
     * <li> void.class
     * <li> int.class
     * <li> java.lang.String.class
     * <li> Object[].class
     * <li> short[][].class
     * </ul>
     * @author Joseph Wong
     */
    public static final class ClassLiteral extends Primary {
        
        /**
         * The referent type, i.e. the Java type R where this literal corresponds to R.class.
         * It may be a primitive type, a reference type, or an array type.
         */
        private final JavaTypeName referentType;
        
        /**
         * Constructor for a ClassLiteral expression
         * @param referentType the Java type R where this literal corresponds to R.class.
         */
        public ClassLiteral(JavaTypeName referentType) {
            Assert.isNotNull(referentType);

            this.referentType = referentType;
        }
        
        /**
         * Get the Java type R where this literal corresponds to R.class.
         * @return JavaTypeName
         */
        public JavaTypeName getReferentType() {
            return referentType;
        }
      
        /**
         * {@inheritDoc}
         */
        @Override
        public <T, R> R accept(JavaModelVisitor<T, R> visitor, T arg) {
            return visitor.visitClassLiteralExpression(this, arg);
        }     
    }
    
    /**
     * A reference to a field, either static, non-static or the "this" field within an expression.
     * Some examples of cases handled:
     * MyClass.foo //reference to the static field foo of class MyClass
     * getFoo(1, "abc").foo //reference to the non-static field foo through the instance expression getFoo(1, "abc")
     * this.foo //reference to foo in the current instance
     * this //reference to the this field itself
     * 
     * @author Edward Lam
     */
    public static abstract class JavaField extends Nameable {
        
        /** The name of the field. */
        private final String fieldName;

        /** The type of the field. */
        private final JavaTypeName fieldType;        
        
        /**
         * The special "this" field.
         * 
         * @author Bo Ilic
         */
        public static final class This extends JavaField {
            
            public This(JavaTypeName fieldType) {
                super("this", fieldType);
            }   
            
            /**
             * {@inheritDoc}
             */
            @Override
            public <T, R> R accept(JavaModelVisitor<T, R> visitor, T arg) {
                return visitor.visitThisFieldExpression(this, arg);
            }     
        }
        
        /**
         * Reference to a static field such as Foo.foo for the static field foo defined in class Foo.
         * 
         * @author Bo Ilic
         */
        public static final class Static extends JavaField {
            
            /**
             * Name of the class that the static field is to be invoked from. This could be a subclass of the class in which the
             * static field is defined, and it is sometimes necessary to do this. For example, if package scope class A defines
             * a static public f, and public class B extends A, then B.f in a different package will not result in a compilation
             * error but A.f will.
             */
            private final JavaTypeName invocationClass;
            
            /**
             * Constructor for a Java static (class) field.
             * @param invocationClass name of the class that the static field is to be invoked from.
             * @param fieldName the name of the field.
             * @param fieldType the type of the field.
             */
            public Static(JavaTypeName invocationClass, String fieldName, JavaTypeName fieldType) {
                super(fieldName, fieldType);
                
                Assert.isNotNullArgument(invocationClass);               
                this.invocationClass = invocationClass;
            }            
            
            /**
             * Name of the class that the static field is to be invoked from. This could be a subclass of the class in which the
             * static field is defined, and it is sometimes necessary to do this. For example, if package scope class A defines
             * a static public f, and public class B extends A, then B.f in a different package will not result in a compilation
             * error but A.f will.            
             * @return name of the class that the static field is to be invoked from. Will be non-null.
             */
            public JavaTypeName getInvocationClass() {
                return invocationClass;
            }
            
            /**
             * {@inheritDoc}
             */
            @Override
            public <T, R> R accept(JavaModelVisitor<T, R> visitor, T arg) {
                return visitor.visitStaticFieldExpression(this, arg);
            }     
        }
        
        /**
         * Reference to a field through an instance reference. This could be an expression such as
         * The instance reference could be an expression such as getFoo(1, "abc") and then used such as getFoo(1, "abc").foo
         * or the instance expression could simply be this e.g. this.foo.
         * This class cannot be used for static fields.
         * 
         * @author Bo Ilic
         */
        public static final class Instance extends JavaField {
            
            /** The instance in which the field exists. null means to use "this" as the instance expression. */
            private final JavaExpression instance;
            
            /** The QualifiedName of the CAL supercombinator which this field is
             *  an instance of.  May be null.
             */
            private final QualifiedName originatingCALSupercombinator;
            
            public Instance(JavaExpression instance, 
                     String fieldName, 
                     JavaTypeName fieldType) {
                super(fieldName, fieldType);
                this.instance = instance;
                this.originatingCALSupercombinator = null;
            }

            public Instance(JavaExpression instance, 
                    String fieldName, 
                    JavaTypeName fieldType,
                    QualifiedName originatingCALSupercombinator) {
               super(fieldName, fieldType);
               this.instance = instance;
               this.originatingCALSupercombinator = originatingCALSupercombinator;
           }
            
            /**
             * Get the instance in which the field exists.
             * @return JavaExpression the instance in which the field exists.  Null for fields in the current instance (i.e. use "this"
             *   as the Java expression.
             */
            public JavaExpression getInstance() {
                return this.instance;
            }  
            
            /**
             * @return the QualifiedName of the CAL supercombinator which this field is
             *         an instance of.  May be null.
             */
            public QualifiedName getOriginatingCALSupercombinator () {
                return this.originatingCALSupercombinator;
            }
            
            /**
             * {@inheritDoc}
             */
            @Override
            public <T, R> R accept(JavaModelVisitor<T, R> visitor, T arg) {
                return visitor.visitInstanceFieldExpression(this, arg);
            }     
        }
                                                           
        /**
         * Constructor for a JavaField.       
         * @param fieldName the name of the field.
         * @param fieldType the type of the field.
         */
        JavaField(String fieldName, JavaTypeName fieldType) {
            Assert.isNotNull(fieldName, "Unable to create JavaField: fieldName == null");
            Assert.isNotNull(fieldType, "Unable to create JavaField: fieldType == null");                       
            
            this.fieldName = fieldName;
            this.fieldType = fieldType;
        }
        
        /**
         * Get the name of the field.
         * @return String
         */
        public String getFieldName() {
            return fieldName;
        }
        
        /**
         * Get the type of the field
         * @return JavaTypeName
         */
        public JavaTypeName getFieldType() {
            return fieldType;
        }  
              
    }
    
    /**
     * A local name.
     * This could be a local variable, a method variable, or a field in the current instance, depending on the context.
     * @author Edward Lam
     */
    public static class LocalName extends Nameable {
        /** The name of the variable. */
        private final String name;
        
        /** The declared type of the variable. */
        private final JavaTypeName typeName;
        
        /**
         * Constructor for this variable.
         * @param name the name of the variable.
         * @param typeName the declared type of the variable.
         */
        public LocalName(String name, JavaTypeName typeName) {
            Assert.isNotNull(name);
            Assert.isNotNull(typeName);
            this.name = name;
            this.typeName = typeName;
        }
        /**
         * Get the name of the variable.
         * @return String
         */
        public String getName() {
            return name;
        }

        /**
         * Get the declared type of the variable.
         * @return JavaTypeName
         */
        public JavaTypeName getTypeName() {
            return typeName;
        }       
        
        /**
         * {@inheritDoc}
         */
        @Override
        public <T, R> R accept(JavaModelVisitor<T, R> visitor, T arg) {
            return visitor.visitLocalNameExpression(this, arg);
        }     
    }
    
    /**
     * A local variable.
     * @author Edward Lam
     */
    public static class LocalVariable extends Nameable {
        
        /** The name of the variable. */
        private final String name;
        
        /** The declared type of the variable. */
        private final JavaTypeName typeName;
        
        /**
         * Constructor for a local variable.
         * @param name the name of the variable.
         * @param typeName the declared type of the variable.
         */
        public LocalVariable(String name, JavaTypeName typeName) {
            Assert.isNotNull(name);
            Assert.isNotNull(typeName);
            this.name = name;
            this.typeName = typeName;
        }
        /**
         * Get the name of the variable.
         * @return String
         */
        public String getName() {
            return name;
        }

        /**
         * Get the declared type of the variable.
         * @return JavaTypeName
         */
        public JavaTypeName getTypeName() {
            return typeName;
        }
     
        /**
         * {@inheritDoc}
         */
        @Override
        public <T, R> R accept(JavaModelVisitor<T, R> visitor, T arg) {
            return visitor.visitLocalVariableExpression(this, arg);
        }     
    }
    
    /**
     * A variable introduced as a parameter to a method.
     * @author Edward Lam
     */
    public static class MethodVariable extends Nameable {
        
        /** The name of the variable. */
        private final String name;
        
        /**
         * Constructor for a method variable.
         * @param name
         */
        public MethodVariable(String name) {
            Assert.isNotNull(name);
            this.name = name;
        }

        /**
         * Get the name of the variable.
         * @return String
         */
        public String getName() {
            return name;
        }      
        
        /**
         * {@inheritDoc}
         */
        @Override
        public <T, R> R accept(JavaModelVisitor<T, R> visitor, T arg) {
            return visitor.visitMethodVariableExpression(this, arg);
        }     
    }
    
    /**
     * An array access (i.e. subscripting an array).
     * 
     * @author Edward Lam
     */
    public static final class ArrayAccess extends Nameable {

        /** The array valued expression to subscript. */
        private final JavaExpression arrayReference;
        
        /** The index into the array. */
        private final JavaExpression arrayIndex;
        
        /**
         * Constructor for an array access.
         * @param arrayReference the array.
         * @param arrayIndex an expression that evaluates to an int valued index.
         */
        public ArrayAccess(JavaExpression arrayReference, JavaExpression arrayIndex) {
            if (arrayReference == null || arrayIndex == null) {
                throw new IllegalArgumentException ("Unable to create ArrayAccess: null argument.");
            }
            this.arrayReference = arrayReference;
            this.arrayIndex = arrayIndex;
        }
      
        /**
         * @return Nameable
         */
        public JavaExpression getArrayReference() {
            return arrayReference;
        }

        /**
         * @return JavaExpression an expression that evaluates to an int valued index
         */
        public JavaExpression getArrayIndex() {
            return arrayIndex;
        }
    
        /**
         * {@inheritDoc}
         */
        @Override
        public <T, R> R accept(JavaModelVisitor<T, R> visitor, T arg) {
            return visitor.visitArrayAccessExpression(this, arg);
        }     
    }
    
    /**
     * Models the .length "field" on arrays.
     * 
     * @author Bo Ilic
     */
    public static final class ArrayLength extends JavaExpression {

        /** The array valued expression to take the length of. */
        private final JavaExpression arrayReference;
        
        /**     
         * @param arrayReference the array.        
         */
        public ArrayLength(JavaExpression arrayReference) {
            if (arrayReference == null) {
                throw new IllegalArgumentException ();
            }
            this.arrayReference = arrayReference;            
        }
      
        /**
         * @return Nameable
         */
        public JavaExpression getArrayReference() {
            return arrayReference;
        }
      
        /**
         * {@inheritDoc}
         */
        @Override
        public <T, R> R accept(JavaModelVisitor<T, R> visitor, T arg) {
            return visitor.visitArrayLengthExpression(this, arg);
        }     
    }    
    
    /**
     * This is used as a place holder for an expresson that will
     * be determined at a later point in time. 
     * @author rcypher
     */
    public static final class PlaceHolder extends JavaExpression {
        JavaExpression actualExpression;
        String name;
        
        public PlaceHolder (String name) {
            this.name = name;
        }
        
        public JavaExpression getActualExpression () {
            return actualExpression;
        }
        public void setActualExpression (JavaExpression actualExpression) {
            this.actualExpression = actualExpression;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public <T, R> R accept(JavaModelVisitor<T, R> visitor, T arg) {
            return visitor.visitPlaceHolderExpression(this, arg);
        }     
    }
    
    /**
     * @return representation of this JavaExpression for debug purposes only.
     */
    @Override
    public String toString() {
        return JavaSourceGenerator.toDebugString(this);        
    }  
}
