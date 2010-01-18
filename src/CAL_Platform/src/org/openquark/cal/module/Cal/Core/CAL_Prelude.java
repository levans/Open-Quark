/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_Prelude.java)
 * was generated from CAL module: Cal.Core.Prelude.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Core.Prelude module from Java code.
 *  
 * Creation date: Tue Oct 23 19:09:08 PDT 2007
 * --!>
 *  
 */

package org.openquark.cal.module.Cal.Core;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceModel;

/**
 * This is the CAL Prelude module. The Prelude module is the core module of CAL and must be
 * imported by every other CAL module.
 * <p>
 * The Prelude defines the primitive types <code>Cal.Core.Prelude.Char</code>, <code>Cal.Core.Prelude.Boolean</code>, <code>Cal.Core.Prelude.Byte</code>, <code>Cal.Core.Prelude.Short</code>,
 * <code>Cal.Core.Prelude.Int</code>, <code>Cal.Core.Prelude.Long</code>, <code>Cal.Core.Prelude.Float</code> and <code>Cal.Core.Prelude.Double</code> that correspond to the primitive
 * unboxed Java types. It also defines other important CAL types such as <code>Cal.Core.Prelude.String</code>, <code>Cal.Core.Prelude.Function</code>,
 * <code>Cal.Core.Prelude.List</code>, <code>Cal.Core.Prelude.Maybe</code>, <code>Cal.Core.Prelude.Either</code>, <code>Cal.Core.Prelude.Unit</code> and the built-in record and tuple types.
 * <p>
 * The Prelude defines many core type classes: <code>Cal.Core.Prelude.Eq</code>, <code>Cal.Core.Prelude.Ord</code>, <code>Cal.Core.Prelude.Num</code>,
 * <code>Cal.Core.Prelude.Inputable</code>, <code>Cal.Core.Prelude.Outputable</code>, <code>Cal.Core.Prelude.Appendable</code>, <code>Cal.Core.Prelude.Bounded</code>,
 * <code>Cal.Core.Prelude.Enum</code> and <code>Cal.Core.Prelude.Typeable</code>, as well as appropriate instances of these classes for the
 * types described above.
 * <p>
 * Finally, the Prelude contains the definitions of many functions generally useful in writing CAL 
 * programs and working with values of the above types. 
 * <p>
 * Note that although the <code>Cal.Core.Prelude.String</code> and <code>Cal.Core.Prelude.List</code> types are defined in the Prelude module,
 * most of the useful basic functions for working specifically with them are to be found in the
 * <code>Cal.Core.String</code> and <code>Cal.Collections.List</code> modules respectively.
 * <p>
 * It is not safe for users to alter the contents of the Prelude module. The Prelude module
 * contains many basic constructs essential to CAL's internal operation and altering these can result
 * in unpredictable failures.
 * <p>
 * The CAL language is a lazy strongly-typed functional language supporting close integration with the
 * Java platform. Many of the core CAL modules contain features that are adaptations of work specified in
 * the Haskell 98 Language and Libraries report <a href='http://www.haskell.org/onlinereport/'>http://www.haskell.org/onlinereport/</a>. 
 * <p>
 * Some optimized implementations of CAL functions are adapted from versions of the Haskell standard libraries
 * as implemented by the Hugs 98 Haskell interpreter <a href='http://www.haskell.org/hugs/'>http://www.haskell.org/hugs/</a> and the Glasgow Haskell
 * Compiler <a href='http://www.haskell.org/ghc/'>http://www.haskell.org/ghc/</a>.
 * <p>
 * See the file <code>ThirdPartyComponents/ThirdPartyComponents.txt</code> for the Hugs license and
 * the Glasgow Haskell Compiler license.
 * 
 * @author Bo Ilic
 */
public final class CAL_Prelude {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Core.Prelude");

	/**
	 * This inner class (TypeClasses) contains constants
	 * and methods related to binding to CAL TypeClasses in the Cal.Core.Prelude module.
	 */
	public static final class TypeClasses {
		/**
		 * The <code>Cal.Core.Prelude.Appendable</code> type class is used to represent types that support the notion of their value being
		 * appended together or concatenated.
		 * <p>
		 * <code>Cal.Core.Prelude.String</code> and <code>Cal.Core.Prelude.List</code> are both instances of the <code>Cal.Core.Prelude.Appendable</code> type class. In particular, this allows
		 * both the <code>Cal.Core.Prelude.List</code> type and the <code>Cal.Core.Prelude.String</code> type to work with the <code>++</code> operator.
		 * <p>
		 * When appending more than two values it is generally more efficient to use <code>Cal.Core.Prelude.concat</code> instead of 
		 * multiple calls to <code>Cal.Core.Prelude.append</code>
		 * e.g. <code>Cal.Core.Prelude.concat ["Apple", " ", "Pear", " ", "Orange"]</code> 
		 * instead of
		 * <code>"Apple" ++ " " + "Pear" ++ "  " + "Orange".</code>
		 * <p>
		 * <code>Cal.Core.Prelude.concat</code> could be defined in terms of <code>Cal.Core.Prelude.append</code> and <code>Cal.Core.Prelude.empty</code>, but it is included as a class method for reasons 
		 * of efficiency in its implementation for specific instances.
		 * <p>
		 * Instances of <code>Cal.Core.Prelude.Appendable</code> should satisfy various algebraic laws in order for the functions that are defined 
		 * using them to make intuitive sense. These laws basically say that the definitions of the various methods for a particular instance
		 * are mutually consistent. For example,
		 * <ul>
		 *  <li>
		 *   <code>Cal.Core.Prelude.isEmpty Cal.Core.Prelude.empty == Cal.Core.Prelude.True</code>
		 *  </li>
		 *  <li>
		 *   <code>Cal.Core.Prelude.empty ++ list == list</code>
		 *  </li>
		 *  <li>
		 *   <code>list ++ Cal.Core.Prelude.empty == list</code>
		 *  </li>
		 *  <li>
		 *   <code>Cal.Core.Prelude.concat list = Cal.Core.Prelude.foldRight Cal.Core.Prelude.append Cal.Core.Prelude.empty list;</code>
		 *  </li>
		 * </ul>
		 * <p>
		 * The <code>Cal.Core.Prelude.append</code> class method can be used in its built-in operator form <code>++</code>. For example, instead of writing
		 * <code>Cal.Core.Prelude.append "Fred" "Anton"</code> you can write <code>"Fred" ++ "Anton"</code>.
		 */
		public static final QualifiedName Appendable = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "Appendable");

		/**
		 * The <code>Cal.Core.Prelude.Bounded</code> type class is used to represent types that have a minimum bound and a maximum bound.
		 * <p>
		 * For example, the <code>Cal.Core.Prelude.Int</code> type is an instance of <code>Cal.Core.Prelude.Bounded</code>, since <code>Cal.Core.Prelude.Int</code> is a finite precision signed 32
		 * bit integer whereas the <code>Cal.Core.Prelude.Integer</code> type, which is an arbitrary sized integer, is not an instance of <code>Cal.Core.Prelude.Bounded</code>.
		 * <p>
		 * The <code>Cal.Core.Prelude.Bounded</code> type class can be used in <em>deriving</em> clauses for <em>enumeration</em> types. An enumeration type
		 * is an algebraic type having no type arguments, and such that each data constructor has no fields. The derived <code>minBound</code>
		 * class method returns the first data constructor declared in the type definition, while the <code>maxBound</code> class method
		 * returns the last one.
		 */
		public static final QualifiedName Bounded = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "Bounded");

		/**
		 * <code>Cal.Core.Prelude.Enum</code> is a type class intended to represent types whose values can be enumerated
		 * one by one, such as <code>Cal.Core.Prelude.Int</code>, <code>Cal.Core.Prelude.Long</code>, <code>Cal.Core.Prelude.Integer</code> and <code>Cal.Core.Prelude.Ordering</code>. It is also used for types
		 * such as <code>Cal.Core.Prelude.Double</code>, where an enumeration can be defined on a subset of the values,
		 * such as the series of values 1, 1.5, 2, 2.5, 3.
		 * <p>
		 * The <code>Cal.Core.Prelude.Enum</code> type class can be used in <em>deriving</em> clauses for <em>enumeration</em> types. An enumeration type
		 * is an algebraic type having no type arguments, and such that each data constructor has no fields. The derived class methods
		 * take their ordering from the declaration ordering of the data constructors within the type definition.
		 */
		public static final QualifiedName Enum = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "Enum");

		/**
		 * The <code>Cal.Core.Prelude.Eq</code> type class defines the notion of equality in CAL. Many types in CAL are instances of <code>Cal.Core.Prelude.Eq</code>. 
		 * Instances of <code>Cal.Core.Prelude.Eq</code> should satisfy various algebraic laws in order for the functions that are defined 
		 * using them to make intuitive sense. These laws basically say that the definitions of the various methods
		 * for a particular instance are mutually consistent. For example,
		 * <ul>
		 *  <li>
		 *   <code>Cal.Core.Prelude.notEquals x y = Cal.Core.Prelude.not (Cal.Core.Prelude.equals x y);</code>
		 *  </li>
		 *  <li>
		 *   <code>Cal.Core.Prelude.equals x y = Cal.Core.Prelude.not (Cal.Core.Prelude.notEquals x y);</code>
		 *  </li>
		 * </ul>
		 * <p>
		 * The reason we don't simply define <code>Cal.Core.Prelude.notEquals</code> in terms of <code>Cal.Core.Prelude.equals</code> is efficiency: for various instance types,
		 * such as <code>Cal.Core.Prelude.Int</code>, there are primitive definitions of <code>Cal.Core.Prelude.notEquals</code> that are more efficient than negating the result
		 * of equality testing.
		 * <p>
		 * Typically instances of <code>Cal.Core.Prelude.Eq</code> will also define an equivalence relation: i.e.
		 * <ul>
		 *  <li>
		 *   reflexive: <code>Cal.Core.Prelude.equals x x</code> is <code>Cal.Core.Prelude.True</code> for all <code>x</code>.
		 *  </li>
		 *  <li>
		 *   symmetric: if <code>Cal.Core.Prelude.equals x y</code> is <code>Cal.Core.Prelude.True</code> then <code>Cal.Core.Prelude.equals y x</code> is also <code>Cal.Core.Prelude.True</code>
		 *  </li>
		 *  <li>
		 *   transitive: if <code>Cal.Core.Prelude.equals x y</code> is <code>Cal.Core.Prelude.True</code> and <code>Cal.Core.Prelude.equals y z</code> is <code>Cal.Core.Prelude.True</code> then
		 *   <code>Cal.Core.Prelude.equals x z</code> is also <code>Cal.Core.Prelude.True</code>.
		 *  </li>
		 * </ul>
		 * <p>
		 * Some types cannot be instances of <code>Cal.Core.Prelude.Eq</code> - for example the type <code>Cal.Core.Prelude.Int -&gt; Cal.Core.Prelude.Int</code>. This is because
		 * it is not in general computationally practical to tell if two functions are equal; one would need to evaluate each function
		 * on every <code>Cal.Core.Prelude.Int</code> and verify that they produce the same result.
		 * <p>
		 * The <code>Cal.Core.Prelude.equals</code> and <code>Cal.Core.Prelude.notEquals</code> class methods have a built-in operator form (<code>==</code> and <code>!=</code> respectively)
		 * that can be used when using these methods in defining CAL functions. For example, instead of writing <code>Cal.Core.Prelude.equals x y</code>
		 * you can simply write <code>x == y</code>.
		 * <p>
		 * The <code>Cal.Core.Prelude.Eq</code> type class can be used in <em>deriving</em> clauses. For a type with n arguments, <code>T a1 ... an</code>, this will
		 * automatically create an instance definition <code>instance (Eq a1, Eq a2, ..., Eq an) =&gt; Eq (T a1 ... an) where ...</code> using a
		 * canonical boilerplate definition for the instance methods. When it provides the desired behavior, deriving an instance of <code>Cal.Core.Prelude.Eq</code> 
		 * is recommended. However, sometimes it does not and an explicit instance definition needs to be given.
		 * <p>
		 * When T is an algebraic type, the derived <code>equals</code> class method is implemented as follows. Two values of the type T are equal
		 * if they both correspond to the same underlying data constructor, and the field values of the two data constructors are equal, field by field.
		 * The comparison of the field values is done in the order in which the fields are declared within the definition
		 * of the data constructor. The algebraic type must be such that all the types of all the fields within its definition are instances of <code>Cal.Core.Prelude.Eq</code>.
		 * For example, the compiler will report an error when deriving an instance such as
		 * <code>data F = F f::(Int -&gt; Int) deriving Eq;</code> because the type of the f field, <code>Int -&gt; Int</code> is not an instance of <code>Cal.Core.Prelude.Eq</code>.
		 * <p>
		 * When T is a foreign type whose underlying implementation type is a Java object type, the derived <code>Cal.Core.Prelude.equals</code> class method is
		 * implemented by calling the Java method Object.equals(Object) on the underlying Java objects.
		 * <p>
		 * When T is a foreign type whose underlying type is a Java primitive type (char, boolean, byte, short, int, long, float, or double),
		 * the derived <code>Cal.Core.Prelude.equals</code> class method is implemented by calling the primitive Java equality operator.
		 */
		public static final QualifiedName Eq = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "Eq");

		/**
		 * The <code>Cal.Core.Prelude.Inputable</code> type class provides a way to create a CAL value from a corresponding external Java value via
		 * its single <code>Cal.Core.Prelude.input</code> class method.
		 * <p>
		 * Instances of <code>Cal.Core.Prelude.Inputable</code> should comment on what the representation of the CAL value in Java is.
		 * For example, when inputting <code>2 :: Int</code> you need to provide a Java value which is a <code>java.lang.Integer</code> holding
		 * the Java <code>int</code> value 2. When inputting the list <code>[1 :: Int, 2, 3]</code>, one way is to provide a <code>java.lang.List</code> having size
		 * 3, with each element being a <code>java.lang.Integer</code>.
		 * <p>
		 * <code>Cal.Core.Prelude.Outputable</code> and <code>Cal.Core.Prelude.Inputable</code> are intended to be inverses of each other. See the documentation of
		 * <code>Cal.Core.Prelude.Outputable</code> for more details on what this means.
		 * <p>
		 * The <code>Cal.Core.Prelude.Inputable</code> type class can be used in <em>deriving</em> clauses. For a type with n arguments, <code>T a1 ... an</code>, this will
		 * automatically create an instance definition <code>instance (Inputable a1, Inputable a2, ..., Inputable an) =&gt; Inputable (T a1 ... an) where ...</code> using a
		 * canonical boilerplate definition for the <code>Cal.Core.Prelude.input</code> instance method. Although convenient, deriving an instance of Inputable
		 * to a certain extent exposes the implementation details of the instance type. Thus, care must be taken to ensure that it is indeed
		 * an appropriate thing to do. Derived instances of <code>Cal.Core.Prelude.Inputable</code> work well with derived instances of <code>Cal.Core.Prelude.Outputable</code>.
		 * <p>
		 * When T is an algebraic type, the derived <code>input</code> class method inputs a value from a Java object of Java type 
		 * <code>org.openquark.cal.foreignsupport.module.Prelude.AlgebraicValue</code>. This value holds onto the name and ordinal of the
		 * data constructor to be input, as well as all the field values of the data constructor to be input, in the order in which the
		 * fields are declared within the definition of the data constructor. Calling <code>Cal.Core.Prelude.input</code> then creates the corresponding CAL data constructor
		 * and calls the <code>Cal.Core.Prelude.input</code> class method on each of the field values in the AlgebraicValue to initialize the field values of the CAL value.
		 * In general, deriving an instance of <code>Cal.Core.Prelude.Inputable</code> for an algebraic type is mainly intended for prototyping code and non-performance
		 * intense code. This is because marshaling from the Java class AlgebraicValue exposes the names and ordinals of the data constructors of
		 * the type, which breaks the encapsulation of the type. Moreover, such a marshaling is likely not a very efficient Java representation,
		 * nor the most convenient for Java clients to use.
		 * <p>
		 * When T is a foreign type whose underlying implementation type is a Java object type, the derived <code>Cal.Core.Prelude.input</code> class method is
		 * implemented by simply doing a Java cast on the argument object to ensure that it is of the correct implementation type.
		 * <p>
		 * When T is a foreign type whose underlying type is a Java primitive type (char, boolean, byte, short, int, long, float, or double),
		 * the derived <code>Cal.Core.Prelude.input</code> class method inputs from one of the standard Java primitive wrapper types  (java.lang.Character,
		 * java.lang.Boolean, java.lang.Byte, java.lang.Short, java.lang.Integer, java.lang.Long, java.lang.Float or java.lang.Double 
		 * as appropriate).
		 * <p>
		 * Deriving an <code>Cal.Core.Prelude.Inputable</code> instance for a foreign type is often quite handy. The main caution is if the foreign type is mutable,
		 * then this can provide a back-hand way for Java clients to mutate values of the type. In that case, a custom instance declaration that
		 * creates a copy of the value can be safer. 
		 */
		public static final QualifiedName Inputable = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "Inputable");

		/**
		 * Instances of <code>Cal.Core.Prelude.IntEnum</code> are types where there is a one-to-one mapping between values of the type and
		 * (possibly a subset) of the <code>Cal.Core.Prelude.Int</code> type.
		 * <p>
		 * Instances must satisfy the condition that
		 * <code>Cal.Core.Prelude.intToEnum (Cal.Core.Prelude.enumToInt x) == x</code>, for all values <code>x</code>.
		 * <p>
		 * On the other hand, we allow multiple <code>Cal.Core.Prelude.Int</code> values to map to a single value in the type <code>x</code>. Thus for example,
		 * for the <code>Cal.Core.Prelude.Ordering</code> type, <code>Cal.Core.Prelude.intToEnum x == Cal.Core.Prelude.LT</code> whenever <code>x &lt; 0</code>.
		 * <p>
		 * The <code>Cal.Core.Prelude.IntEnum</code> type class can be used in <em>deriving</em> clauses for <em>enumeration</em> types. An enumeration type
		 * is an algebraic type having no type arguments, and such that each data constructor has no fields. The mapping
		 * to Int values for the derived class methods is via the data constructor ordinal. This is the 0-based declaration order of 
		 * the data constructor within the type definition. Generally if you derive the <code>Cal.Core.Prelude.IntEnum</code> instance, you will also want
		 * to derive the <code>Cal.Core.Prelude.Enum</code> class instance. 
		 */
		public static final QualifiedName IntEnum = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "IntEnum");

		/**
		 * The <code>Cal.Core.Prelude.Num</code> type class defines what it means for a type to have numerical support in CAL using the usual arithmetic
		 * operations of <code>+</code>, <code>-</code>, <code>*</code>, and <code>/</code>.
		 * <p>
		 * Useful instances of <code>Cal.Core.Prelude.Num</code> defined in the Prelude are for <code>Cal.Core.Prelude.Int</code>, <code>Cal.Core.Prelude.Long</code>, <code>Cal.Core.Prelude.Double</code>, <code>Cal.Core.Prelude.Integer</code> 
		 * (arbitrary size integers), <code>Cal.Core.Prelude.Byte</code>, <code>Cal.Core.Prelude.Short</code>, <code>Cal.Core.Prelude.Float</code> and <code>Cal.Core.Prelude.Decimal</code>.
		 * <p>
		 * <code>Cal.Core.Prelude.Num</code> derives from <code>Cal.Core.Prelude.Ord</code>, so that CAL's <code>Cal.Core.Prelude.Num</code> type class cannot be used for representing unordered numerical
		 * types such as complex numbers or matrices.
		 * <p>
		 * One important point is that CAL does not do any coercion or promotion of numeric values like in many programming
		 * languages. Rather, it relies on polymorphic integral constants to determine the precise type of a numeric expression.
		 * In particular, integer literals such as <code>2005</code> are really short-hand for <code>Cal.Core.Prelude.fromInt 2005</code>. As a technical
		 * point, if the literal cannot be represented by an <code>Cal.Core.Prelude.Int</code> but can be represented by a <code>Cal.Core.Prelude.Long</code>, then <code>Cal.Core.Prelude.fromLong</code>
		 * is used. Otherwise, <code>Cal.Core.Prelude.fromInteger</code> is used.
		 * <p>
		 * Instances of <code>Cal.Core.Prelude.Num</code> should satisfy various algebraic laws in order for the functions that are defined 
		 * using them to make intuitive sense. These laws basically say that the definitions of the various methods for a particular instance
		 * are mutually consistent. For example, <code>x + y</code> should be the same as <code>x - (-y)</code> for all <code>x</code>, <code>y</code>. 
		 */
		public static final QualifiedName Num = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "Num");

		/**
		 * The <code>Cal.Core.Prelude.Ord</code> type class defines the notion of an ordering of the values of a type in CAL. <code>Cal.Core.Prelude.Eq</code> is a superclass of
		 * <code>Cal.Core.Prelude.Ord</code>, so any type that is an instance of <code>Cal.Core.Prelude.Ord</code> must also be an instance of <code>Cal.Core.Prelude.Eq</code>.
		 * <p>
		 * Many types that are instances of <code>Cal.Core.Prelude.Eq</code> are also instances of <code>Cal.Core.Prelude.Ord</code>. But some are not since there is no reasonable
		 * ordering on them. For example, the <code>Cal.Graphics.Color.Color</code> type is an instance of <code>Cal.Core.Prelude.Eq</code> but is not an
		 * instance of <code>Cal.Core.Prelude.Ord</code>.
		 * <p>
		 * Instances of <code>Cal.Core.Prelude.Ord</code> should satisfy various algebraic laws in order for the functions that are defined 
		 * using them to make intuitive sense. These laws basically say that the definitions of the various methods for a particular instance
		 * are mutually consistent. For example,
		 * <ul>
		 *  <li>
		 *   <code>Cal.Core.Prelude.greaterThanEquals x y = Cal.Core.Prelude.not (y &lt; x);</code>
		 *  </li>
		 *  <li>
		 *   <code>Cal.Core.Prelude.greaterThanEquals x y = Cal.Core.Prelude.compare x y != Cal.Core.Prelude.LT;</code>
		 *  </li>
		 *  <li>
		 *   <code>Cal.Core.Prelude.greaterThanEquals x y = x == y || x &gt; y;</code>
		 *  </li>
		 *  <li>
		 *   <code>Cal.Core.Prelude.max x y = if x &lt;= y then y else x;</code>
		 *  </li>
		 *  <li>
		 *   <code>Cal.Core.Prelude.min x y = if x &lt;= y then x else y;</code>
		 *  </li>
		 * </ul>
		 * <p>
		 * The <code>Cal.Core.Prelude.lessThan</code>, <code>Cal.Core.Prelude.lessThanEquals</code>, <code>Cal.Core.Prelude.greaterThanEquals</code> and <code>Cal.Core.Prelude.greaterThan</code> class methods have a
		 * built-in operator form (<code>&lt;</code>, <code>&lt;=</code>, <code>&gt;=</code>, and <code>&gt;</code>) respectively that can be used when using these
		 * methods in defining CAL functions.
		 * <p>
		 * Using the compare method can be more efficient for complex types since one can precisely characterize which comparison situation
		 * you are in rather than needing potentially two calls to the relational class methods.
		 * <p>
		 * The <code>Cal.Core.Prelude.Ord</code> type class can be used in <em>deriving</em> clauses. For a type with n arguments, <code>T a1 ... an</code>, this will
		 * automatically create an instance definition <code>instance (Ord a1, Ord a2, ..., Ord an) =&gt; Ord (T a1 ... an) where ...</code> using a
		 * canonical boilerplate definition for the instance methods. When it provides the desired behavior, deriving an instance of <code>Cal.Core.Prelude.Ord</code> 
		 * is recommended. However, sometimes it does not and an explicit instance definition needs to be given. Usually, if you derive the
		 * <code>Cal.Core.Prelude.Ord</code> instance then you will also want to derive the <code>Cal.Core.Prelude.Eq</code> instance.
		 * <p>
		 * When T is an algebraic type, the derived <code>Cal.Core.Prelude.compare</code> class method is implemented as follows. When two values are compared, 
		 * first the ordinals of the underlying data constructors are compared. (The ordinal of a data constructor is its 0-based index within
		 * its type definition. For example, the ordinals of the data constructors of the <code>Cal.Core.Prelude.Ordering</code> type, <code>Cal.Core.Prelude.LT</code>, <code>Cal.Core.Prelude.EQ</code>, and
		 * <code>Cal.Core.Prelude.GT</code>, are 0, 1 and 2 respectively). If the ordinals are equal, then both values have the same underlying data constructor,
		 * and the field values of the data constructor are compared lexicographically in the order in which the fields are declared within
		 * the data constructor. The algebraic type must be such that all the types of all the fields within its definition are instances of Ord.
		 * <p>
		 * When T is a foreign type whose underlying implementation type is a Java object type, the derived <code>Cal.Core.Prelude.compare</code> class method is
		 * implemented by calling the Java method <code>java.lang.Comparable.compareTo(Object)</code> on the underlying objects. In particular, the implementation
		 * type must implement the Java interface <code>java.lang.Comparable</code> to avoid a CAL compilation error.
		 * <p>
		 * When T is a foreign type whose underlying type is a Java primitive type (char, byte, short, int, long, float, or double),
		 * the derived <code>Cal.Core.Prelude.compare</code> class method is implemented by calling the primitive Java comparison operators. For the Java primitive
		 * type boolean, the ordering is determined by <code>false &lt; true</code>, even though an ordering for boolean values is not 
		 * supported by Java itself.
		 */
		public static final QualifiedName Ord = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "Ord");

		/**
		 * The <code>Cal.Core.Prelude.Outputable</code> type class provides a way to convert a CAL value to an appropriate Java value via its
		 * single <code>Cal.Core.Prelude.output</code> class method.
		 * <p>
		 * Instances of <code>Cal.Core.Prelude.Outputable</code> should comment on what the representation of the CAL value in Java is.
		 * For example, <code>Cal.Core.Prelude.output (2 :: Int)</code> produces a <code>java.lang.Integer</code> holding the int value 2.
		 * and <code>Cal.Core.Prelude.output [1 :: Int, 2, 3]</code> produces a <code>java.lang.List</code> having size 3, with each element being
		 * a <code>java.lang.Integer</code>.
		 * <p>
		 * The value output should be a complete representation of the CAL value i.e. with enough specific
		 * information to reconstruct the CAL value if necessary.
		 * <p>
		 * <code>Cal.Core.Prelude.Outputable</code> and <code>Cal.Core.Prelude.Inputable</code> are intended to be inverses of each other. What this means is that if
		 * we define <code>outThenIn</code> by:
		 * 
		 * <pre>outThenIn :: (Outputable a, Cal.Core.Prelude.Inputable a) =&gt; a -&gt; a;
		 * outThenIn x = Cal.Core.Prelude.input (Cal.Core.Prelude.output x);</pre>
		 * 
		 * then for <code>x</code>, <code>outThenIn x</code> should work, and not produce a runtime error i.e. the Java classes used
		 * by <code>Cal.Core.Prelude.input</code> and <code>Cal.Core.Prelude.output</code> for a particular type should be compatible.
		 * <p>
		 * If in addition, the type is in the <code>Cal.Core.Prelude.Eq</code> type class, the following algebraic law should hold:
		 * For all x, <code>outThenIn x == x</code>.
		 * <p>
		 * The <code>Cal.Core.Prelude.Outputable</code> type class can be used in <em>deriving</em> clauses. For a type with n arguments, <code>T a1 ... an</code>, this will
		 * automatically create an instance definition <code>instance (Outputable a1, Outputable a2, ..., Outputable an) =&gt; Outputable (T a1 ... an) where ...</code> using a
		 * canonical boilerplate definition for the <code>Cal.Core.Prelude.output</code> instance method. Although convenient, deriving an instance of Outputable
		 * exposes the implementation details of the instance type to a certain extent. Thus, care must be taken to ensure that it is indeed
		 * an appropriate thing to do. Derived instances of <code>Cal.Core.Prelude.Outputable</code> work well with derived instances of <code>Cal.Core.Prelude.Inputable</code>.
		 * <p>
		 * When T is an algebraic type, the derived <code>Cal.Core.Prelude.output</code> class method produces a value of Java type 
		 * <code>org.openquark.cal.foreignsupport.module.Prelude.AlgebraicValue</code>. This value holds onto the name and ordinal of the
		 * data constructor that was output. It also holds onto the outputted values of the fields of the data constructor. These are produced
		 * by calling the <code>Cal.Core.Prelude.output</code> class method on each of the fields of the data constuctor in the order in which they are declared within
		 * the definition of the data constructor. The Java class AlgebraicValue provides methods to query the name and the ordinal of the
		 * data constructor that was output, as well as the outputted values of each of the fields. In general, deriving an instance of
		 * <code>Cal.Core.Prelude.Outputable</code> for an algebraic type is mainly intended for prototyping code and non-performance intense code. This is because marshaling
		 * to the Java class AlgebraicValue exposes the names and ordinals of the data constructors of the type, which breaks the encapsulation of
		 * the type. Moreover, such a marshaling is likely not a very efficient Java representation, nor the most convenient for Java
		 * clients to use.
		 * <p>
		 * When T is a foreign type whose underlying implementation type is a Java object type, the derived <code>Cal.Core.Prelude.output</code> class method is
		 * implemented by simply returning the underlying Java object.
		 * <p>
		 * When T is a foreign type whose underlying type is a Java primitive type (char, boolean, byte, short, int, long, float, or double),
		 * the derived <code>Cal.Core.Prelude.output</code> class method returns the underlying primitive value wrapped by one of the standard Java
		 * primitive wrapper types (java.lang.Character, java.lang.Boolean, java.lang.Byte, java.lang.Short, java.lang.Integer, java.lang.Long,
		 * java.lang.Float or java.lang.Double as appropriate).
		 * <p>
		 * Deriving an <code>Cal.Core.Prelude.Outputable</code> instance for a foreign type is often quite handy. The main caution is if the foreign type is mutable,
		 * then this can provide a back-hand way for Java clients to mutate values of the type. In that case, a custom instance declaration that
		 * creates a copy of the value can be safer. 
		 */
		public static final QualifiedName Outputable = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "Outputable");

		/**
		 * The <code>Cal.Core.Prelude.Typeable</code> type class is primarily intended to support working with types at runtime, for example,
		 * via the use of the <code>Cal.Core.Dynamic.Dynamic</code> type. The <code>Cal.Core.Dynamic.Dynamic</code> type supports
		 * type-discovery of CAL values at runtime in a completely safe way.
		 * <p>
		 * Almost every type defined in CAL is automatically made an instance of <code>Cal.Core.Prelude.Typeable</code> by the compiler.
		 * For example, for the <code>Cal.Core.Prelude.Int</code> type, there is an instance 
		 * <pre>
		 * instance Typeable Int where ...
		 * </pre>
		 * 
		 * <p>
		 * For polymorphic types, the instance is a constrained instance. For example, for the <code>Cal.Core.Prelude.Either</code> type it is 
		 * <code>instance (Typeable a, Typeable b) =&gt; Typeable (Either a b) where ...</code> and for the List type it is 
		 * <code>instance (Typeable a) =&gt; Typeable [a] where ...</code>
		 * <p>
		 * The only types that are not instances of Typeable are parametric types having a type variable that does not have kind *.
		 * These are extremely rare in practice.
		 */
		public static final QualifiedName Typeable = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "Typeable");

	}
	/**
	 * This inner class (TypeConstructors) contains constants
	 * and methods related to binding to CAL TypeConstructors in the Cal.Core.Prelude module.
	 */
	public static final class TypeConstructors {
		/**
		 * This CAL type is primarily for internal use to support derived <code>Cal.Core.Prelude.Inputable</code> and <code>Cal.Core.Prelude.Outputable</code> instances.
		 * <p>
		 * <code>Cal.Core.Prelude.AlgebraicValue</code> can be used as a default way of representing values of a CAL data type defined by a non-foreign
		 * data declaration in a way that is accessible to Java clients. They are often not the most efficient representation
		 * of a value in Java. Note: even though they are not an efficient representation in Java, they are efficient within
		 * CAL itself because of the amount of sharing that CAL supports.
		 */
		public static final QualifiedName AlgebraicValue = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "AlgebraicValue");

		/**
		 * The <code>Cal.Core.Prelude.Boolean</code> type represents a boolean logical quantity. The two possible values are
		 * <code>Cal.Core.Prelude.False</code> and <code>Cal.Core.Prelude.True</code>.
		 * <p>
		 * The <code>Cal.Core.Prelude.Boolean</code> type can correspond to the Java primitive type "boolean" in foreign function declarations.
		 * <p>
		 * Basic operations on <code>Cal.Core.Prelude.Boolean</code> values include:
		 * <ul>
		 *  <li>
		 *   <code>Cal.Core.Prelude.and</code> (operator form: <code>&amp;&amp;</code>) - logical conjunction
		 *  </li>
		 *  <li>
		 *   <code>Cal.Core.Prelude.or</code> (operator form: <code>||</code>) - logical disjunction
		 *  </li>
		 *  <li>
		 *   <code>Cal.Core.Prelude.not</code> - logical complement
		 *  </li>
		 * </ul>
		 * <p>
		 * The derived <code>Cal.Core.Prelude.Ord</code> instance provides an ordering of the two <code>Cal.Core.Prelude.Boolean</code> values, namely
		 * <code>Cal.Core.Prelude.False &lt; Cal.Core.Prelude.True</code>.
		 */
		public static final QualifiedName Boolean = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "Boolean");

		/**
		 * The <code>Cal.Core.Prelude.Byte</code> type represents an 8-bit signed integral value with valid values from -128 to 127.
		 * The <code>Cal.Core.Prelude.Byte</code> type is a foreign type that corresponds to the primitive Java type <code>byte</code>
		 * (and not the reference type <code>java.lang.Byte</code>).
		 * <p>
		 * Constants of type <code>Cal.Core.Prelude.Byte</code> can be created using expression type signatures e.g. <code>20 :: Byte</code>.
		 * <p>
		 * The derived <code>Cal.Core.Prelude.Inputable</code> instance inputs a <code>java.lang.Byte</code> object to a CAL <code>Cal.Core.Prelude.Byte</code> value.
		 * <p>
		 * The derived <code>Cal.Core.Prelude.Outputable</code> instance outputs a CAL <code>Cal.Core.Prelude.Byte</code> value to a <code>java.lang.Byte</code> object.
		 */
		public static final QualifiedName Byte = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "Byte");

		/**
		 * The <code>Cal.Core.Prelude.CalFunction</code> type is a foreign type used to hold a CAL function of type <code>Cal.Core.Prelude.JObject -&gt; Cal.Core.Prelude.JObject</code>.
		 * <p>
		 * This type is mainly intended to support the following use case:
		 * <ol>
		 *  <li>
		 *   a CAL function is being evaluated, producing a partial application, which however requires a currently
		 *   unavailable foreign value to be bound in to finish the transformation. 
		 *  </li>
		 *  <li>
		 *   the partial application is passed to a foreign function. 
		 *  </li>
		 *  <li>
		 *   at some point, the logic in the foreign function can obtain the required foreign value, and is able to
		 *   call back into CAL to finish the evaluation 
		 *  </li>
		 * </ol>
		 * <p>
		 * It can also be used to implement functions such as <code>Cal.Collections.List.sortByExternal</code> in which a pre-existing algorithm
		 * such as <code>java.util.Collections.sort</code> can be used to sort a CAL list using call-backs to a CAL comparison function of type 
		 * a -&gt; a -&gt; Ordering.
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Core.Prelude.makeCalFunction, Cal.Core.Prelude.evaluateCalFunction
		 * </dl>
		 */
		public static final QualifiedName CalFunction = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "CalFunction");

		/**
		 * An opaque representation of an arbitrary CAL value.
		 * <p>
		 * Values in CAL have the Java <code>org.openquark.cal.runtime.CalValue</code> abstract base class as their root.
		 * They are treated specially when input or output from Java using the <code>Cal.Core.Prelude.Inputable Cal.Core.Prelude.CalValue</code> and
		 * <code>Cal.Core.Prelude.Outputable Cal.Core.Prelude.CalValue</code> instance definitions.
		 */
		public static final QualifiedName CalValue = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "CalValue");

		/**
		 * The <code>Cal.Core.Prelude.Char</code> type is a 16-bit unsigned integral value representing a Unicode character.
		 * More precisely, a <code>Cal.Core.Prelude.Char</code> represents a <em>code unit</em> in the UTF-16 encoding.
		 * (For more information on Unicode terminology, please consult the Unicode Glossary at
		 * <a href='http://www.unicode.org/glossary/'>http://www.unicode.org/glossary/</a>.)
		 * <p>
		 * The <code>Cal.Core.Prelude.Char</code> type is a foreign type that corresponds to the primitive Java type <code>char</code>" 
		 * (and not the reference type <code>java.lang.Character</code>).
		 * <p>
		 * Constants of type <code>Cal.Core.Prelude.Char</code> can be created using single quote notation such as <code>'a'</code>
		 * or <code>'b'</code>. The escape characters <code>'\n'</code> (newline), <code>'\r'</code> (carriage return),
		 * <code>'\t'</code> (tab), <code>'\b'</code> (backspace), <code>'\f'</code> (form feed), <code>'\"'</code> (double quote),
		 * <code>'\''</code> (single quote) and <code>'\\'</code> (back slash) are supported.
		 * <p>
		 * In addition, explicit Unicode characters can be encoded using escape characters such as <code>'\u1234'</code>
		 * or <code>'\uABCD'</code> i.e. a "u" followed by 4 hexidecimal digits ranging from 0 to F (or f).
		 * Octal escape sequences are also accepted, as in Java e.g. <code>'\0177'</code>.
		 * <p>
		 * The derived <code>Cal.Core.Prelude.Eq</code> and <code>Cal.Core.Prelude.Ord</code> instances for <code>Cal.Core.Prelude.Char</code> values use the underlying primitive
		 * Java comparison functions on char, which are based on comparing the integral representations. In particular,
		 * they are very fast, but not locale sensitive.
		 * <p>
		 * The derived <code>Cal.Core.Prelude.Inputable</code> instance inputs a <code>java.lang.Character</code> object to a CAL <code>Cal.Core.Prelude.Char</code> value.
		 * <p>
		 * The derived <code>Cal.Core.Prelude.Outputable</code> instance outputs a CAL <code>Cal.Core.Prelude.Char</code> value to a java.lang.Character object.
		 * <p>
		 * Many useful functions for working with characters are defined in the <code>Cal.Core.Char</code> module.
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Modules:</b> Cal.Core.Char
		 * </dl>
		 */
		public static final QualifiedName Char = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "Char");

		/**
		 * The <code>Cal.Core.Prelude.Decimal</code> type represents arbitrary-precision signed decimal numbers.
		 * It is an import of the Java BigDecimal type that provides the usual type
		 * classes for numeric types (viz: <code>Cal.Core.Prelude.Eq</code>, <code>Cal.Core.Prelude.Ord</code>,
		 * <code>Cal.Core.Prelude.Num</code>).  This means that you can use the arithmetic operators
		 * with Decimal values.
		 * <p>
		 * A <code>Cal.Core.Prelude.Decimal</code> value consists of an arbitrary-precision Integer value plus a
		 * 32-bit unsigned "scale", which represents the number of digits to the
		 * right of the decimal place.  So, for example:
		 * <ul>
		 *  <li>
		 *   -3.00 has a scale of 2
		 *  </li>
		 *  <li>
		 *   123.11 has a scale of 2
		 *  </li>
		 *  <li>
		 *   -1.0609 has a scale of 4
		 *  </li>
		 *  <li>
		 *   876134 has a scale of 0
		 *  </li>
		 * </ul>
		 * <p>
		 * The result of an addition or subtraction of two <code>Cal.Core.Prelude.Decimal</code> values will have 
		 * the same scale as the input with the largest scale:
		 * 
		 * <pre>    1.0 + 2 = 3.0
		 *     1.5 - 2.00 = -0.50
		 * </pre>
		 *  
		 * The result of a multiplication will have a scale of the sum of the scales
		 * of the two inputs:
		 * 
		 * <pre>    -6.0 * 7.00 = -42.000
		 *     1.0 * 1.0 * 1.0 * 1.0 = 1.0000
		 * </pre>
		 * 
		 * The result of a division will have the same scale as the numerator:
		 * 
		 * <pre>    15.0 / 5.000 = 3.0
		 *     18.00 / 9 = 2.00
		 *     21.0 / 3 = 7.0 
		 * </pre>
		 * 
		 * The output and input methods for this class convert <code>Cal.Core.Prelude.Decimal</code> CAL values
		 * to and from Java <code>java.math.BigDecimal</code> objects.
		 */
		public static final QualifiedName Decimal = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "Decimal");

		/**
		 * The <code>Cal.Core.Prelude.Double</code> type represents a double-precision 64-bit format IEEE 754 floating-point value.
		 * The <code>Cal.Core.Prelude.Double</code> type is a foreign type that corresponds to the primitive Java type <code>double</code>
		 * (and not the reference type <code>java.lang.Double</code>).
		 * <p>
		 * The derived <code>Cal.Core.Prelude.Inputable</code> instance inputs a <code>java.lang.Double</code> object to a CAL <code>Cal.Core.Prelude.Double</code> value.
		 * <p>
		 * The derived <code>Cal.Core.Prelude.Outputable</code> instance outputs a CAL <code>Cal.Core.Prelude.Double</code> value to a <code>java.lang.Double</code> object.
		 */
		public static final QualifiedName Double = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "Double");

		/**
		 * The <code>Cal.Core.Prelude.Either</code> type represents values with two possibilities. A value of type <code>Either a b</code> is either
		 * <code>Cal.Core.Prelude.Left a</code> or <code>Cal.Core.Prelude.Right b</code>.
		 * <p>
		 * For example, the list <code>[Cal.Core.Prelude.Left "abc", Cal.Core.Prelude.Right 2.0]</code> has type
		 * <code>Either Cal.Core.Prelude.String Cal.Core.Prelude.Double</code>.
		 * <p>
		 * The <code>Cal.Core.Prelude.Either</code> type is sometimes used as an alternative to the <code>Cal.Core.Prelude.Maybe</code> type when representing the return
		 * type of a function that may fail. The <code>Cal.Core.Prelude.Left</code> data constructor is then used to hold failure information (i.e. an
		 * error message for example), and the <code>Cal.Core.Prelude.Right</code> data constructor is then used to hold the successful return value.  
		 */
		public static final QualifiedName Either = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "Either");

		/**
		 * The <code>Cal.Core.Prelude.Float</code> type represents a single-precision 32-bit format IEEE 754 floating-point value.
		 * The <code>Cal.Core.Prelude.Float</code> type is a foreign type that corresponds to the primitive Java type <code>float</code>
		 * (and not the reference type <code>java.lang.Float</code>).
		 * <p>
		 * The derived <code>Cal.Core.Prelude.Inputable</code> instance inputs a <code>java.lang.Float</code> object to a CAL <code>Cal.Core.Prelude.Float</code> value.
		 * <p>
		 * The derived <code>Cal.Core.Prelude.Outputable</code> instance outputs a CAL <code>Cal.Core.Prelude.Float</code> value to a <code>java.lang.Float</code> object.
		 */
		public static final QualifiedName Float = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "Float");

		/** Name binding for TypeConsApp: Function. */
		public static final QualifiedName Function = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "Function");

		/**
		 * The <code>Cal.Core.Prelude.Int</code> type represents a 32-bit signed integral value with valid values from -2<sup>31</sup> to 2<sup>31</sup>-1.
		 * The <code>Cal.Core.Prelude.Int</code> type is a foreign type that corresponds to the primitive Java type <code>int</code>
		 * (and not the reference type <code>java.lang.Int</code>).
		 * <p>
		 * Constants of type <code>Cal.Core.Prelude.Int</code> can be created using expression type signatures e.g. <code>100000 :: Int</code>.
		 * <p>
		 * The derived <code>Cal.Core.Prelude.Inputable</code> instance inputs a <code>java.lang.Integer</code> object to a CAL <code>Cal.Core.Prelude.Int</code> value.
		 * <p>
		 * The derived <code>Cal.Core.Prelude.Outputable</code> instance outputs a CAL <code>Cal.Core.Prelude.Int</code> value to a <code>java.lang.Integer</code> object.
		 */
		public static final QualifiedName Int = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "Int");

		/**
		 * The <code>Cal.Core.Prelude.Integer</code> type represents arbitrary-precision integers.
		 */
		public static final QualifiedName Integer = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "Integer");

		/**
		 * A CAL foreign type corresponding to the Java type <code>java.util.Collection</code>.
		 */
		public static final QualifiedName JCollection = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "JCollection");

		/**
		 * A CAL foreign type corresponding to the Java type <code>java.util.List</code>.
		 */
		public static final QualifiedName JList = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "JList");

		/**
		 * A CAL foreign type corresponding to the Java type <code>java.util.Map</code>.
		 */
		public static final QualifiedName JMap = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "JMap");

		/**
		 * A CAL foreign type corresponding to the Java Object type.
		 */
		public static final QualifiedName JObject = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "JObject");

		/**
		 * The <code>Cal.Core.Prelude.List</code> type constructor has special notation. For example, instead of writing
		 * <code>List Int</code> in a type expression one can write <code>[Int]</code>.
		 * <p>
		 * The <code>Cal.Core.Prelude.Nil</code> data constructor can be written as <code>[]</code> instead within an expression.
		 * <p>
		 * The <code>Cal.Core.Prelude.Cons</code> data constructor has special notation. For example, instead of writing <code>Cons 'a' "def"</code>
		 * one can write <code>'a' : "def"</code>.
		 * <p>
		 * Many useful functions for working with lists are defined in the <code>Cal.Collections.List</code> module.
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Modules:</b> Cal.Collections.List
		 * </dl>
		 */
		public static final QualifiedName List = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "List");

		/**
		 * The <code>Cal.Core.Prelude.Long</code> type represents a 64-bit signed integral value with valid values from -2<sup>63</sup> to 2<sup>63</sup>-1.
		 * The <code>Cal.Core.Prelude.Long</code> type is a foreign type that corresponds to the primitive Java type <code>long</code>
		 * (and not the reference type <code>java.lang.Long</code>).
		 * <p>
		 * Constants of type <code>Cal.Core.Prelude.Long</code> can be created using expression type signatures e.g. <code>10000000000 :: Long</code>.
		 * <p>
		 * The derived <code>Cal.Core.Prelude.Inputable</code> instance inputs a <code>java.lang.Long</code> object to a CAL <code>Cal.Core.Prelude.Long</code> value.
		 * <p>
		 * The derived <code>Cal.Core.Prelude.Outputable</code> instance outputs a CAL <code>Cal.Core.Prelude.Long</code> value to a <code>java.lang.Long</code> object.
		 */
		public static final QualifiedName Long = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "Long");

		/**
		 * The <code>Cal.Core.Prelude.Maybe</code> type can be thought of as representing an optional value.
		 * For example, a value of type <code>Maybe Cal.Core.Prelude.Double</code> can be <code>Cal.Core.Prelude.Just 2.0</code>,
		 * indicating that the value 2.0 was supplied, or it can be
		 * <code>Cal.Core.Prelude.Nothing</code>, indicating that no <code>Cal.Core.Prelude.Double</code> value was supplied.
		 * <p>
		 * It is also common to use the <code>Cal.Core.Prelude.Maybe</code> type as the return type of a function
		 * where the value <code>Cal.Core.Prelude.Nothing</code> returned indicates an exception or error occurred within the
		 * function. As an alternative, one can call the <code>Cal.Core.Prelude.error</code> function, which immediately
		 * terminates execution, but that does not allow the caller to recover from the erroneous
		 * situation, and so is rather severe.
		 */
		public static final QualifiedName Maybe = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "Maybe");

		/**
		 * Represents an ordering relationship between two values: less than, equal to, or greater than.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Core.Prelude.compare
		 * <dd><b>Type Classes:</b> Cal.Core.Prelude.Ord
		 * </dl>
		 */
		public static final QualifiedName Ordering = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "Ordering");

		/**
		 * The <code>Cal.Core.Prelude.Short</code> type represents a 16-bit signed integral value with valid values from -32768 to 32767.
		 * The <code>Cal.Core.Prelude.Short</code> type is a foreign type that corresponds to the primitive Java type <code>short</code>
		 * (and not the reference type <code>java.lang.Short</code>).
		 * <p>
		 * Constants of type <code>Cal.Core.Prelude.Short</code> can be created using expression type signatures e.g. <code>30000 :: Short</code>.
		 * <p>
		 * The derived <code>Cal.Core.Prelude.Inputable</code> instance inputs a <code>java.lang.Short</code> object to a CAL <code>Cal.Core.Prelude.Short</code> value.
		 * <p>
		 * The derived <code>Cal.Core.Prelude.Outputable</code> instance outputs a CAL <code>Cal.Core.Prelude.Short</code> value to a <code>java.lang.Short</code> object.
		 */
		public static final QualifiedName Short = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "Short");

		/**
		 * The <code>Cal.Core.Prelude.String</code> type represents a character string. The <code>Cal.Core.Prelude.String</code> type is a foreign type
		 * that corresponds to the Java type <code>java.lang.String</code>.
		 * <p>
		 * Constants of type <code>Cal.Core.Prelude.String</code> can be created using double quote notation such as <code>"Hello"</code>
		 * or <code>"x"</code>. The escape characters <code>'\n'</code> (newline), <code>'\r'</code> (carriage return),
		 * <code>'\t'</code> (tab), <code>'\b'</code> (backspace), <code>'\f'</code> (form feed), <code>'\"'</code> (double quote),
		 * <code>'\''</code> (single quote) and <code>'\\'</code> (back slash) can appear within string literals.
		 * <p>
		 * In addition, explicit Unicode characters can be encoded using escape characters such as <code>'\u1234'</code>
		 * or <code>'\uABCD'</code> i.e. a "u" followed by 4 hexidecimal digits ranging from 0 to F (or f).
		 * Octal escape sequences are also accepted, as in Java e.g. <code>'\0177'</code>.
		 * <p>
		 * The derived <code>Cal.Core.Prelude.Inputable</code> instance inputs a <code>java.lang.String</code> object to a CAL <code>Cal.Core.Prelude.String</code> value.
		 * <p>
		 * The derived <code>Cal.Core.Prelude.Outputable</code> instance outputs a CAL <code>Cal.Core.Prelude.String</code> value to a <code>java.lang.String</code> object.
		 * <p>
		 * Many useful functions for working with strings are defined in the <code>Cal.Core.String</code> module.
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Modules:</b> Cal.Core.String
		 * </dl>
		 */
		public static final QualifiedName String = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "String");

		/**
		 * A type used to define a representation of a non-polymorphic CAL type.
		 */
		public static final QualifiedName TypeRep = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "TypeRep");

		/**
		 * The <code>Cal.Core.Prelude.Unit</code> type <code>()</code>, which has only one data value, also denoted <code>()</code>.
		 * In foreign function declarations, it is used for functions that return the "void" type in Java.
		 * <p>
		 * The <code>Cal.Core.Prelude.Unit</code> type constructor has special notation. It can be written as <code>()</code> within a type expression
		 * instead of as <code>Cal.Core.Prelude.Unit</code>.
		 * <p>
		 * The <code>Cal.Core.Prelude.Unit</code> data constructor has special notation. It can be written as <code>()</code>
		 * within an expression instead of as <code>Cal.Core.Prelude.Unit</code>.
		 */
		public static final QualifiedName Unit = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "Unit");

	}
	/**
	 * This inner class (DataConstructors) contains constants
	 * and methods related to binding to CAL DataConstructors in the Cal.Core.Prelude module.
	 */
	public static final class DataConstructors {
		/*
		 * DataConstructors for the Cal.Core.Prelude.Boolean data type.
		 */

		/**
		 * Binding for DataConstructor: Cal.Core.Prelude.False.
		 * @return the SourceModule.Expr representing an application of Cal.Core.Prelude.False
		 */
		public static final SourceModel.Expr False() {
			return SourceModel.Expr.DataCons.make(DataConstructors.False);
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Prelude.False.
		 * @see #False()
		 */
		public static final QualifiedName False = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "False");

		/**
		 * Ordinal of DataConstructor Cal.Core.Prelude.False.
		 * @see #False()
		 */
		public static final int False_ordinal = 0;

		/**
		 * Binding for DataConstructor: Cal.Core.Prelude.True.
		 * @return the SourceModule.Expr representing an application of Cal.Core.Prelude.True
		 */
		public static final SourceModel.Expr True() {
			return SourceModel.Expr.DataCons.make(DataConstructors.True);
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Prelude.True.
		 * @see #True()
		 */
		public static final QualifiedName True = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "True");

		/**
		 * Ordinal of DataConstructor Cal.Core.Prelude.True.
		 * @see #True()
		 */
		public static final int True_ordinal = 1;

		/*
		 * DataConstructors for the Cal.Core.Prelude.Either data type.
		 */

		/**
		 * A data constructor that represents a value of the first of two possible types.
		 * @param value (CAL type: <code>a</code>)
		 *          the value to be encapsulated.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr Left(SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.Left), value});
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Prelude.Left.
		 * @see #Left(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName Left = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "Left");

		/**
		 * Ordinal of DataConstructor Cal.Core.Prelude.Left.
		 * @see #Left(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int Left_ordinal = 0;

		/**
		 * A data constructor that represents a value of the second of two possible types.
		 * @param value (CAL type: <code>b</code>)
		 *          the value to be encapsulated.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr Right(SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.Right), value});
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Prelude.Right.
		 * @see #Right(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName Right = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "Right");

		/**
		 * Ordinal of DataConstructor Cal.Core.Prelude.Right.
		 * @see #Right(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int Right_ordinal = 1;

		/*
		 * DataConstructors for the Cal.Core.Prelude.List data type.
		 */

		/**
		 * Binding for DataConstructor: Cal.Core.Prelude.Nil.
		 * @return the SourceModule.Expr representing an application of Cal.Core.Prelude.Nil
		 */
		public static final SourceModel.Expr Nil() {
			return SourceModel.Expr.DataCons.make(DataConstructors.Nil);
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Prelude.Nil.
		 * @see #Nil()
		 */
		public static final QualifiedName Nil = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "Nil");

		/**
		 * Ordinal of DataConstructor Cal.Core.Prelude.Nil.
		 * @see #Nil()
		 */
		public static final int Nil_ordinal = 0;

		/**
		 * Binding for DataConstructor: Cal.Core.Prelude.Cons.
		 * @param head
		 * @param tail
		 * @return the SourceModule.Expr representing an application of Cal.Core.Prelude.Cons
		 */
		public static final SourceModel.Expr Cons(SourceModel.Expr head, SourceModel.Expr tail) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.Cons), head, tail});
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Prelude.Cons.
		 * @see #Cons(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName Cons = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "Cons");

		/**
		 * Ordinal of DataConstructor Cal.Core.Prelude.Cons.
		 * @see #Cons(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int Cons_ordinal = 1;

		/*
		 * DataConstructors for the Cal.Core.Prelude.Maybe data type.
		 */

		/**
		 * A data constructor which represents the fact that a value was not supplied.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr Nothing() {
			return SourceModel.Expr.DataCons.make(DataConstructors.Nothing);
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Prelude.Nothing.
		 * @see #Nothing()
		 */
		public static final QualifiedName Nothing = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "Nothing");

		/**
		 * Ordinal of DataConstructor Cal.Core.Prelude.Nothing.
		 * @see #Nothing()
		 */
		public static final int Nothing_ordinal = 0;

		/**
		 * A data constructor which represents the fact that a value was supplied.
		 * @param value (CAL type: <code>a</code>)
		 *          the supplied value.
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr Just(SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.DataCons.make(DataConstructors.Just), value});
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Prelude.Just.
		 * @see #Just(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName Just = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "Just");

		/**
		 * Ordinal of DataConstructor Cal.Core.Prelude.Just.
		 * @see #Just(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final int Just_ordinal = 1;

		/*
		 * DataConstructors for the Cal.Core.Prelude.Ordering data type.
		 */

		/**
		 * A data constructor that represents the ordering relationship of "less than".
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr LT() {
			return SourceModel.Expr.DataCons.make(DataConstructors.LT);
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Prelude.LT.
		 * @see #LT()
		 */
		public static final QualifiedName LT = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "LT");

		/**
		 * Ordinal of DataConstructor Cal.Core.Prelude.LT.
		 * @see #LT()
		 */
		public static final int LT_ordinal = 0;

		/**
		 * A data constructor that represents the ordering relationship of "equal to".
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr EQ() {
			return SourceModel.Expr.DataCons.make(DataConstructors.EQ);
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Prelude.EQ.
		 * @see #EQ()
		 */
		public static final QualifiedName EQ = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "EQ");

		/**
		 * Ordinal of DataConstructor Cal.Core.Prelude.EQ.
		 * @see #EQ()
		 */
		public static final int EQ_ordinal = 1;

		/**
		 * A data constructor that represents the ordering relationship of "greater than".
		 * @return SourceModel.Expr
		 */
		public static final SourceModel.Expr GT() {
			return SourceModel.Expr.DataCons.make(DataConstructors.GT);
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Prelude.GT.
		 * @see #GT()
		 */
		public static final QualifiedName GT = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "GT");

		/**
		 * Ordinal of DataConstructor Cal.Core.Prelude.GT.
		 * @see #GT()
		 */
		public static final int GT_ordinal = 2;

		/*
		 * DataConstructors for the Cal.Core.Prelude.Unit data type.
		 */

		/**
		 * Binding for DataConstructor: Cal.Core.Prelude.Unit.
		 * @return the SourceModule.Expr representing an application of Cal.Core.Prelude.Unit
		 */
		public static final SourceModel.Expr Unit() {
			return SourceModel.Expr.DataCons.make(DataConstructors.Unit);
		}

		/**
		 * Name binding for DataConstructor: Cal.Core.Prelude.Unit.
		 * @see #Unit()
		 */
		public static final QualifiedName Unit = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "Unit");

		/**
		 * Ordinal of DataConstructor Cal.Core.Prelude.Unit.
		 * @see #Unit()
		 */
		public static final int Unit_ordinal = 0;

	}
	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Core.Prelude module.
	 */
	public static final class Functions {
		/**
		 * Returns the absolute value of the given number.
		 * <p>
		 * The absolute value of a number is its numerical value without repect to sign.
		 * The absolute value of -5 equals the absolute value of 5.
		 * 
		 * @param x (CAL type: <code>Cal.Core.Prelude.Num a => a</code>)
		 *          the number whose absolute value is requested.
		 * @return (CAL type: <code>Cal.Core.Prelude.Num a => a</code>) 
		 *          the absolute value of <code>x</code>.
		 */
		public static final SourceModel.Expr abs(SourceModel.Expr x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.abs), x});
		}

		/**
		 * Name binding for function: abs.
		 * @see #abs(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName abs = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "abs");

		/**
		 * Returns the sum of two numbers.
		 * <p>
		 * The operator form of <code>Cal.Core.Prelude.add</code> is <code>+</code>.
		 * 
		 * @param x (CAL type: <code>Cal.Core.Prelude.Num a => a</code>)
		 *          the first addend.
		 * @param y (CAL type: <code>Cal.Core.Prelude.Num a => a</code>)
		 *          the second addend.
		 * @return (CAL type: <code>Cal.Core.Prelude.Num a => a</code>) 
		 *          the sum of <code>x</code> and <code>y</code>.
		 */
		public static final SourceModel.Expr add(SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.add), x, y});
		}

		/**
		 * Name binding for function: add.
		 * @see #add(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName add = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "add");

		/**
		 * Returns the result of a logical AND operation on the two arguments.
		 * <p>
		 * <code>Cal.Core.Prelude.and</code> can also be used in its operator form (which is <code>&amp;&amp;</code>).
		 * 
		 * @param a (CAL type: <code>Cal.Core.Prelude.Boolean</code>)
		 *          the first <code>Cal.Core.Prelude.Boolean</code> argument.
		 * @param b (CAL type: <code>Cal.Core.Prelude.Boolean</code>)
		 *          the second <code>Cal.Core.Prelude.Boolean</code> argument.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> when both arguments are <code>Cal.Core.Prelude.True</code>; <code>Cal.Core.Prelude.False</code> when one or both arguments are <code>Cal.Core.Prelude.False</code>
		 */
		public static final SourceModel.Expr and(SourceModel.Expr a, SourceModel.Expr b) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.and), a, b});
		}

		/**
		 * @see #and(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param a
		 * @param b
		 * @return the SourceModel.Expr representing an application of and
		 */
		public static final SourceModel.Expr and(boolean a, boolean b) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.and), SourceModel.Expr.makeBooleanValue(a), SourceModel.Expr.makeBooleanValue(b)});
		}

		/**
		 * Name binding for function: and.
		 * @see #and(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName and = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "and");

		/**
		 * Returns the concatenation of the two values.
		 * <p>
		 * The operator form of append is <code>++</code>.
		 * 
		 * @param x (CAL type: <code>Cal.Core.Prelude.Appendable a => a</code>)
		 *          the first value to be concatenated.
		 * @param y (CAL type: <code>Cal.Core.Prelude.Appendable a => a</code>)
		 *          the second value to be concatenated.
		 * @return (CAL type: <code>Cal.Core.Prelude.Appendable a => a</code>) 
		 *          the concatenation of <code>x</code> and <code>y</code>.
		 */
		public static final SourceModel.Expr append(SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.append), x, y});
		}

		/**
		 * Name binding for function: append.
		 * @see #append(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName append = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "append");

		/**
		 * A function application function. This function can also be used in its operator form (which is <code>$</code>).
		 * <p>
		 * This function (and its operator form) can be viewed as redundant, since ordinary application
		 * <code>(f x)</code> means the same as <code>(apply f x)</code> or <code>(f $ x)</code>. However, <code>$</code> has low, right-associative
		 * binding precedence, so it sometimes allows parentheses to be omitted; for example:
		 * <p>
		 * Writing 
		 * <pre> f $ g $ h x
		 * </pre>
		 *  is the same as writing 
		 * <pre> f (g (h x))
		 * </pre>
		 * 
		 * <p>
		 * It is also useful in higher-order situations, such as <code>(Cal.Collections.List.zipWith apply functions listOfValues)</code>.
		 * 
		 * @param functionToApply (CAL type: <code>a -> b</code>)
		 *          the function to be applied.
		 * @param argument (CAL type: <code>a</code>)
		 *          the argument to the function evaluation.
		 * @return (CAL type: <code>b</code>) 
		 *          the result of evaluating <code>(functionToApply argument)</code>.
		 */
		public static final SourceModel.Expr apply(SourceModel.Expr functionToApply, SourceModel.Expr argument) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.apply), functionToApply, argument});
		}

		/**
		 * Name binding for function: apply.
		 * @see #apply(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName apply = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "apply");

		/**
		 * <code>Cal.Core.Prelude.asTypeOf</code> is a type-restricted version of <code>Cal.Core.Prelude.const</code>. It can be used as a sort of a
		 * casting function. Whatever the type of the argument <code>valueToIgnore</code>,
		 * <code>valueToReturn</code> is forced to have that type as well.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Core.Prelude.const
		 * </dl>
		 * 
		 * @param valueToReturn (CAL type: <code>a</code>)
		 *          the value to be returned.
		 * @param valueToIgnore (CAL type: <code>a</code>)
		 *          an ignored value.
		 * @return (CAL type: <code>a</code>) 
		 *          valueToReturn.
		 */
		public static final SourceModel.Expr asTypeOf(SourceModel.Expr valueToReturn, SourceModel.Expr valueToIgnore) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.asTypeOf), valueToReturn, valueToIgnore});
		}

		/**
		 * Name binding for function: asTypeOf.
		 * @see #asTypeOf(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName asTypeOf = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "asTypeOf");

		/**
		 * <code>Cal.Core.Prelude.assert</code> evaluates the given expression. If the expresion equals <code>Cal.Core.Prelude.True</code> then the return value 
		 * will be <code>Cal.Core.Prelude.True</code>. If the expression equals <code>Cal.Core.Prelude.False</code> then the execution will stop. An exception will
		 * be thrown. The exception contains the error message string. In addition, the exception
		 * will contain an error information object that has information about the position in the 
		 * source code of the <code>Cal.Core.Prelude.assert</code> call. Note, this is not the position of the <code>Cal.Core.Prelude.error</code> call in the 
		 * <code>Cal.Core.Prelude.assert</code> function body.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Core.Prelude.undefined, Cal.Core.Prelude.error
		 * </dl>
		 * 
		 * @param expr (CAL type: <code>Cal.Core.Prelude.Boolean</code>)
		 *          An expression that evaluates to a <code>Cal.Core.Prelude.Boolean</code> value.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          The return value will be <code>Cal.Core.Prelude.True</code> iff <code>expr</code> evaluates to <code>Cal.Core.Prelude.True</code>. Otherwise the return value
		 * is <em>bottom</em>.
		 */
		public static final SourceModel.Expr assert_(SourceModel.Expr expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.assert_), expr});
		}

		/**
		 * @see #assert_(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param expr
		 * @return the SourceModel.Expr representing an application of assert
		 */
		public static final SourceModel.Expr assert_(boolean expr) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.assert_), SourceModel.Expr.makeBooleanValue(expr)});
		}

		/**
		 * Name binding for function: assert.
		 * @see #assert_(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName assert_ = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "assert");

		/**
		 * Casts the argument value to the desired return type in a type-safe fashion.
		 * This function makes a runtime test that compares the type of the argument and
		 * the return type. If they are the same type, <code>Cal.Core.Prelude.Just castValue</code> is returned, where
		 * <code>castValue</code> is the argument value cast into the desired type, or <code>Cal.Core.Prelude.Nothing</code>, if
		 * the value cannot be cast into the desired type.
		 * <p>
		 * Type-safe casts let polymorphic functions behave differently depending on the
		 * runtime type of an argument.
		 * 
		 * @param x (CAL type: <code>Cal.Core.Prelude.Typeable a => a</code>)
		 *          the value to be cast.
		 * @return (CAL type: <code>Cal.Core.Prelude.Typeable b => Cal.Core.Prelude.Maybe b</code>) 
		 *          <code>Cal.Core.Prelude.Just castValue</code>, where <code>castValue</code> is the argument value cast into the
		 * desired type, or <code>Cal.Core.Prelude.Nothing</code>, if the value cannot be cast into the
		 * desired type.
		 */
		public static final SourceModel.Expr cast(SourceModel.Expr x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.cast), x});
		}

		/**
		 * Name binding for function: cast.
		 * @see #cast(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName cast = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "cast");

		/**
		 * Returns the smallest integer greater than or equal to <code>x</code>.
		 * <p>
		 * e.g.
		 * <ul>
		 *  <li>
		 *   <code>ceiling (-1.9) = -1</code>
		 *  </li>
		 *  <li>
		 *   <code>ceiling (1.3) = 2</code>
		 *  </li>
		 * </ul>
		 * 
		 * @param x (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 *          the <code>Cal.Core.Prelude.Double</code> value whose ceiling is to be returned.
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 *          the smallest integer greater than or equal to <code>x</code>.
		 */
		public static final SourceModel.Expr ceiling(SourceModel.Expr x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.ceiling), x});
		}

		/**
		 * @see #ceiling(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param x
		 * @return the SourceModel.Expr representing an application of ceiling
		 */
		public static final SourceModel.Expr ceiling(double x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.ceiling), SourceModel.Expr.makeDoubleValue(x)});
		}

		/**
		 * Name binding for function: ceiling.
		 * @see #ceiling(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName ceiling = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "ceiling");

		/**
		 * Combines two comparators to form a new comparator. First comparison is done with <code>comparator1</code>.
		 * If <code>comparator1</code> returns <code>Cal.Core.Prelude.EQ</code> then the comparison is done with <code>comparator2</code>.
		 * @param comparator1 (CAL type: <code>a -> a -> Cal.Core.Prelude.Ordering</code>)
		 *          the main comparator to use for comparison.
		 * @param comparator2 (CAL type: <code>a -> a -> Cal.Core.Prelude.Ordering</code>)
		 *          the comparator to use in the event that <code>comparator1</code> returns <code>Cal.Core.Prelude.EQ</code>.
		 * @param x (CAL type: <code>a</code>)
		 *          the first argument to the combined comparator.
		 * #arg y the second argument to the combined comparator.
		 * @param y (CAL type: <code>a</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Ordering</code>) 
		 *          the result of comparing <code>x</code> and <code>y</code> using the combined comparator.
		 */
		public static final SourceModel.Expr combineComparators(SourceModel.Expr comparator1, SourceModel.Expr comparator2, SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.combineComparators), comparator1, comparator2, x, y});
		}

		/**
		 * Name binding for function: combineComparators.
		 * @see #combineComparators(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName combineComparators = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "combineComparators");

		/**
		 * Converts a comparator to an equality function. 
		 * There are a variety of functions in CAL (usually with names ending in "By" such as "groupBy")
		 * which take an equality function. Such an equality function can be obtained from a comparator
		 * using this function.
		 * @param comparator (CAL type: <code>a -> a -> Cal.Core.Prelude.Ordering</code>)
		 *          the comparison function to be converted.
		 * @param x (CAL type: <code>a</code>)
		 *          the first argument to compare for equality.
		 * @param y (CAL type: <code>a</code>)
		 *          the second argument to compare for equality.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if <code>x</code> and <code>y</code> are equal according to comparator and <code>Cal.Core.Prelude.False</code> otherwise.
		 */
		public static final SourceModel.Expr comparatorToEqualityFunction(SourceModel.Expr comparator, SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.comparatorToEqualityFunction), comparator, x, y});
		}

		/**
		 * Name binding for function: comparatorToEqualityFunction.
		 * @see #comparatorToEqualityFunction(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName comparatorToEqualityFunction = 
			QualifiedName.make(
				CAL_Prelude.MODULE_NAME, 
				"comparatorToEqualityFunction");

		/**
		 * Returns an <code>Cal.Core.Prelude.Ordering</code> based on how the first argument compares to the second argument.
		 * @param x (CAL type: <code>Cal.Core.Prelude.Ord a => a</code>)
		 *          the first argument to compare.
		 * @param y (CAL type: <code>Cal.Core.Prelude.Ord a => a</code>)
		 *          the second argument to compare.
		 * @return (CAL type: <code>Cal.Core.Prelude.Ordering</code>) 
		 *          <code>Cal.Core.Prelude.LT</code>, <code>Cal.Core.Prelude.EQ</code>, or <code>Cal.Core.Prelude.GT</code> if <code>x</code> is respectively less than, equal to, or greater than
		 * <code>y</code>.
		 */
		public static final SourceModel.Expr compare(SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.compare), x, y});
		}

		/**
		 * Name binding for function: compare.
		 * @see #compare(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName compare = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "compare");

		/**
		 * <code>(compose f g)</code> is the function composition of <code>f</code> with <code>g</code>. In other words, <code>(compose f g) x</code>
		 * is equivalent to the nested evaluation <code>(f (g x))</code>.
		 * <p>
		 * This function can also be used in its operator form (which is <code>#</code>).
		 * 
		 * @param f (CAL type: <code>b -> c</code>)
		 *          the outer function of the composition.
		 * @param g (CAL type: <code>a -> b</code>)
		 *          the inner function of the composition.
		 * @param x (CAL type: <code>a</code>)
		 *          the argument to the composite function.
		 * @return (CAL type: <code>c</code>) 
		 *          the result of evaluating <code>(f (g x))</code>.
		 */
		public static final SourceModel.Expr compose(SourceModel.Expr f, SourceModel.Expr g, SourceModel.Expr x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.compose), f, g, x});
		}

		/**
		 * Name binding for function: compose.
		 * @see #compose(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName compose = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "compose");

		/**
		 * Returns the concatenation of the values in the specified list.
		 * @param listOfValues (CAL type: <code>Cal.Core.Prelude.Appendable a => [a]</code>)
		 *          the list of values to be concatenated together.
		 * @return (CAL type: <code>Cal.Core.Prelude.Appendable a => a</code>) 
		 *          the concatenation of the values in the list.
		 */
		public static final SourceModel.Expr concat(SourceModel.Expr listOfValues) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.concat), listOfValues});
		}

		/**
		 * Name binding for function: concat.
		 * @see #concat(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName concat = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "concat");

		/**
		 * A constant function.
		 * @param valueToReturn (CAL type: <code>a</code>)
		 *          the value to be returned.
		 * @param valueToIgnore (CAL type: <code>b</code>)
		 *          an ignored value.
		 * @return (CAL type: <code>a</code>) 
		 *          <code>valueToReturn</code>.
		 */
		public static final SourceModel.Expr const_(SourceModel.Expr valueToReturn, SourceModel.Expr valueToIgnore) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.const_), valueToReturn, valueToIgnore});
		}

		/**
		 * Name binding for function: const.
		 * @see #const_(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName const_ = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "const");

		/**
		 * Converts a function of a single pair argument to a function accepting 2 input arguments.
		 * This function is named after the logician Haskell Curry.
		 * @param f (CAL type: <code>(a, b) -> c</code>)
		 *          the function to be curried.
		 * @param x (CAL type: <code>a</code>)
		 *          the first argument to the curried function.
		 * @param y (CAL type: <code>b</code>)
		 *          the second argument to the curried function.
		 * @return (CAL type: <code>c</code>) 
		 *          the result of evaluating <code>(f (x, y))</code>.
		 */
		public static final SourceModel.Expr curry(SourceModel.Expr f, SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.curry), f, x, y});
		}

		/**
		 * Name binding for function: curry.
		 * @see #curry(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName curry = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "curry");

		/**
		 * The <code>Cal.Core.Prelude.deepSeq</code> function is similar to the <code>Cal.Core.Prelude.seq</code> function, except that <code>deepSeq x y</code> evaluates the
		 * argument <code>x</code> to normal form (instead of weak head normal form). What this means is that it evaluates the argument
		 * <code>x</code> to reduce it to a primitive value (such as an <code>Cal.Core.Prelude.Int</code> or <code>Cal.Core.Prelude.String</code>), a function, or a data constructor.
		 * If it is a data constructor, it then evaluates (i.e. <code>Cal.Core.Prelude.deepSeq</code>'s) all of the data constructor's arguments as well,
		 * in left-to-right order.
		 * <p>
		 * In general, using <code>Cal.Core.Prelude.seq</code> is preferable to using <code>Cal.Core.Prelude.deepSeq</code>. <code>Cal.Core.Prelude.deepSeq</code> is a heavy-weight tool that can
		 * reduce the lazyness of your program more than necessary. However, in certain cases it does come in handy, such as when
		 * performing benchmarks on intermediate computations.
		 * 
		 * @param arg_1 (CAL type: <code>a</code>)
		 * @param arg_2 (CAL type: <code>b</code>)
		 * @return (CAL type: <code>b</code>) 
		 */
		public static final SourceModel.Expr deepSeq(SourceModel.Expr arg_1, SourceModel.Expr arg_2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.deepSeq), arg_1, arg_2});
		}

		/**
		 * Name binding for function: deepSeq.
		 * @see #deepSeq(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName deepSeq = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "deepSeq");

		/**
		 * Strict function application. The argument <code>x</code> of <code>f</code> is reduced to normal form before <code>f</code> is reduced.
		 * @param f (CAL type: <code>a -> b</code>)
		 *          the function to be applied.
		 * @param x (CAL type: <code>a</code>)
		 *          the argument to <code>f</code>, to be reduced to normal form before <code>f</code> is reduced.
		 * @return (CAL type: <code>b</code>) 
		 *          the result of evaluating <code>(f x)</code>.
		 */
		public static final SourceModel.Expr deepStrict(SourceModel.Expr f, SourceModel.Expr x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.deepStrict), f, x});
		}

		/**
		 * Name binding for function: deepStrict.
		 * @see #deepStrict(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName deepStrict = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "deepStrict");

		/**
		 * Simultaneous <code>Cal.Core.Prelude.divide</code> and <code>Cal.Core.Prelude.remainder</code> (or modulus) on <code>Cal.Core.Prelude.Int</code> values.
		 * @param x (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the dividend.
		 * @param y (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the divisor.
		 * @return (CAL type: <code>(Cal.Core.Prelude.Int, Cal.Core.Prelude.Int)</code>) 
		 *          the pair <code>(x / y, x % y)</code>.
		 */
		public static final SourceModel.Expr divMod(SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.divMod), x, y});
		}

		/**
		 * @see #divMod(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param x
		 * @param y
		 * @return the SourceModel.Expr representing an application of divMod
		 */
		public static final SourceModel.Expr divMod(int x, int y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.divMod), SourceModel.Expr.makeIntValue(x), SourceModel.Expr.makeIntValue(y)});
		}

		/**
		 * Name binding for function: divMod.
		 * @see #divMod(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName divMod = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "divMod");

		/**
		 * Returns the result of dividing the first number by the second number.
		 * <p>
		 * The operator form of <code>Cal.Core.Prelude.divide</code> is <code>/</code>.
		 * 
		 * @param x (CAL type: <code>Cal.Core.Prelude.Num a => a</code>)
		 *          the dividend.
		 * @param y (CAL type: <code>Cal.Core.Prelude.Num a => a</code>)
		 *          the divisor.
		 * @return (CAL type: <code>Cal.Core.Prelude.Num a => a</code>) 
		 *          the quotient.
		 */
		public static final SourceModel.Expr divide(SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.divide), x, y});
		}

		/**
		 * Name binding for function: divide.
		 * @see #divide(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName divide = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "divide");

		/**
		 * Converts a <code>Cal.Core.Prelude.Double</code> value to its string representation.
		 * @param value (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 *          the <code>Cal.Core.Prelude.Double</code> value.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          the string representation.
		 */
		public static final SourceModel.Expr doubleToString(SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleToString), value});
		}

		/**
		 * @see #doubleToString(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param value
		 * @return the SourceModel.Expr representing an application of doubleToString
		 */
		public static final SourceModel.Expr doubleToString(double value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doubleToString), SourceModel.Expr.makeDoubleValue(value)});
		}

		/**
		 * Name binding for function: doubleToString.
		 * @see #doubleToString(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName doubleToString = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "doubleToString");

		/**
		 * <code>downFrom start</code> creates the infinite list <code>[start, start - 1, start - 2, ...]</code>.
		 * @param start (CAL type: <code>(Cal.Core.Prelude.Enum a, Cal.Core.Prelude.Num a) => a</code>)
		 *          the start value
		 * @return (CAL type: <code>(Cal.Core.Prelude.Enum a, Cal.Core.Prelude.Num a) => [a]</code>) 
		 *          the infinite list <code>[start, start - 1, start - 2, ...]</code>.
		 */
		public static final SourceModel.Expr downFrom(SourceModel.Expr start) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.downFrom), start});
		}

		/**
		 * Name binding for function: downFrom.
		 * @see #downFrom(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName downFrom = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "downFrom");

		/**
		 * <code>Cal.Core.Prelude.eager</code> is used to force a strict evaluation of its argument to weak-head normal form.
		 * This forced evaluation occurs even when the application of eager is in lazy context. (In a strict context
		 * the expression will be evaluated to weak-head normal form anyways and so calling <code>Cal.Core.Prelude.eager</code> is redundant and will
		 * generate identical underlying code).
		 * <p>
		 * <code>Cal.Core.Prelude.eager</code> should be used with care since forcing the evaluation changes the normal reduction ordering and can affect
		 * laziness. <code>Cal.Core.Prelude.eager</code> should only be used in cases where the argument expression is known to have no side effects
		 * (including throwing an exception).
		 * <p>
		 * With proper care eager can provide a significant performance boost since forcing the strict evaluation 
		 * of the argument means we avoid building a lazy graph for it.
		 * <p>
		 * One way of thinking about <code>Cal.Core.Prelude.eager</code> is that it instructs the compiler to unconditionally generate code to evaluate an
		 * expression to weak-head normal form instead of building a lazy suspension or thunk. The compiler builds such a thunk
		 * when it can't be sure that the resulting expression will be evaluated. There are many examples of the use of <code>Cal.Core.Prelude.eager</code> in 
		 * the <code>Cal.Collections.Array</code> module. Some characteristic uses are commented in the definitions of <code>Cal.Collections.Array.find</code>,
		 * <code>Cal.Collections.Array.zip</code> and <code>Cal.Collections.Array.binarySearchBy</code>. Typically we use <code>Cal.Core.Prelude.eager</code> within an expression e.g.
		 * <code>... expr ...</code> ---&gt; <code>... (eager expr) ...</code> or for a let variable assignment e.g. <code>let x = expr</code> ---&gt;
		 * <code>let x = (eager expr)</code>.
		 * <p>
		 * Some reasons for using eager:
		 * <ol>
		 *  <li>
		 *   we know that <code>expr</code> is going to get evaluated to weak-head normal form later in the function's definition
		 *   (and there is no side-effect or exception risk in doing the evaluation early).
		 *  </li>
		 *  <li>
		 *   we don't know that <code>expr</code> is going to be evaluated eventually, but we do know that evaluating to WHNF is
		 *   less expensive than allocating memory for the suspended computation. For example, perhaps the expression is a call
		 *   to an inexpensive function and all the arguments are known to be evaluated.
		 *  </li>
		 * </ol>
		 * <p>
		 * To get the most use of <code>Cal.Core.Prelude.eager</code>, one needs to know about the reduction order of CAL i.e. left-most outermost reduction,
		 * also known as lazy evaluation, as well as plinging and <code>Cal.Core.Prelude.seq</code>. Note that in contrast to <code>Cal.Core.Prelude.eager</code>, <code>Cal.Core.Prelude.seq</code> in
		 * a lazy context does not evaluate anything - it just enforces sequencing of evaluations when such evaluations occur.
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Core.Prelude.seq, Cal.Core.Prelude.deepSeq
		 * </dl>
		 * 
		 * @param value (CAL type: <code>a</code>)
		 *          the value to compile strictly
		 * @return (CAL type: <code>a</code>) 
		 *          the argument value
		 */
		public static final SourceModel.Expr eager(SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.eager), value});
		}

		/**
		 * Name binding for function: eager.
		 * @see #eager(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName eager = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "eager");

		/**
		 * Applies one of the two specified functions to a value encapsulated by an <code>Cal.Core.Prelude.Either</code> type.
		 * If the value is <code>Cal.Core.Prelude.Left x</code>, then the first function is applied to <code>x</code>; if the value is
		 * <code>Cal.Core.Prelude.Right y</code>, then the second function is applied to <code>y</code>.
		 * @param leftFunction (CAL type: <code>a -> c</code>)
		 *          the function to apply if the <code>Cal.Core.Prelude.Either</code> value is <code>Cal.Core.Prelude.Left x</code>.
		 * @param rightFunction (CAL type: <code>b -> c</code>)
		 *          the function to apply if the <code>Cal.Core.Prelude.Either</code> value is <code>Cal.Core.Prelude.Right y</code>.
		 * @param eitherValue (CAL type: <code>Cal.Core.Prelude.Either a b</code>)
		 *          the <code>Cal.Core.Prelude.Either</code> value.
		 * @return (CAL type: <code>c</code>) 
		 *          the result of the application of one of either <code>leftFunction</code> or <code>rightFunction</code> on
		 * the value encapsulated by the <code>Cal.Core.Prelude.Either</code> value.
		 */
		public static final SourceModel.Expr either(SourceModel.Expr leftFunction, SourceModel.Expr rightFunction, SourceModel.Expr eitherValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.either), leftFunction, rightFunction, eitherValue});
		}

		/**
		 * Name binding for function: either.
		 * @see #either(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName either = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "either");

		/**
		 * Returns the empty value of the instance type.
		 * @return (CAL type: <code>Cal.Core.Prelude.Appendable a => a</code>) 
		 *          the empty value.
		 */
		public static final SourceModel.Expr empty() {
			return SourceModel.Expr.Var.make(Functions.empty);
		}

		/**
		 * Name binding for function: empty.
		 * @see #empty()
		 */
		public static final QualifiedName empty = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "empty");

		/**
		 * Converts a value of type <code>Cal.Core.Prelude.IntEnum a =&gt; a</code> to its canonical underlying representation as an <code>Cal.Core.Prelude.Int</code>.
		 * @param enumValue (CAL type: <code>Cal.Core.Prelude.IntEnum a => a</code>)
		 *          enum value whose cannonical representation as an <code>Cal.Core.Prelude.Int</code> is needed.
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 *          the canonical underlying representation of enumValue as an <code>Cal.Core.Prelude.Int</code>.
		 */
		public static final SourceModel.Expr enumToInt(SourceModel.Expr enumValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.enumToInt), enumValue});
		}

		/**
		 * Name binding for function: enumToInt.
		 * @see #enumToInt(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName enumToInt = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "enumToInt");

		/**
		 * Returns whether the two arguments are equal.
		 * <p>
		 * The operator form of <code>equals</code> is <code>==</code>.
		 * 
		 * @param x (CAL type: <code>Cal.Core.Prelude.Eq a => a</code>)
		 *          the first argument to compare.
		 * @param y (CAL type: <code>Cal.Core.Prelude.Eq a => a</code>)
		 *          the second argument to compare.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if <code>x</code> and <code>y</code> are equal; <code>Cal.Core.Prelude.False</code> otherwise.
		 */
		public static final SourceModel.Expr equals(SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.equals), x, y});
		}

		/**
		 * Name binding for function: equals.
		 * @see #equals(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName equals = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "equals");

		/**
		 * The <code>Cal.Core.Prelude.error</code> function will cause execution to stop immediately. An exception will be thrown.
		 * The exception contains the error message string. In addition, the exception
		 * will contain an error information object that has information about the position in the
		 * source code of the error call.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Core.Prelude.assert, Cal.Core.Prelude.undefined
		 * </dl>
		 * 
		 * @param message (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          A message that will be shown as part of the error message.
		 * @return (CAL type: <code>a</code>) 
		 *          The return value is bottom.
		 */
		public static final SourceModel.Expr error(SourceModel.Expr message) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.error), message});
		}

		/**
		 * @see #error(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param message
		 * @return the SourceModel.Expr representing an application of error
		 */
		public static final SourceModel.Expr error(java.lang.String message) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.error), SourceModel.Expr.makeStringValue(message)});
		}

		/**
		 * Name binding for function: error.
		 * @see #error(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName error = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "error");

		/**
		 * <code>evaluateCalFunction func arg</code> evaluates the CAL function represented by <code>func</code> at the value <code>arg</code>.
		 * <p>
		 * Typically Java code will invoke the org.openquark.cal.runtime.CalFunction.evaluate method
		 * rather than CAL code calling the <code>Cal.Core.Prelude.evaluateCalFunction</code> foreign function.
		 * 
		 * @param func (CAL type: <code>Cal.Core.Prelude.CalFunction</code>)
		 *          the function
		 * @param arg (CAL type: <code>Cal.Core.Prelude.JObject</code>)
		 *          the argument to apply <code>func</code> to
		 * @return (CAL type: <code>Cal.Core.Prelude.JObject</code>) 
		 *          the result of evaluating <code>func arg</code>
		 */
		public static final SourceModel.Expr evaluateCalFunction(SourceModel.Expr func, SourceModel.Expr arg) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.evaluateCalFunction), func, arg});
		}

		/**
		 * Name binding for function: evaluateCalFunction.
		 * @see #evaluateCalFunction(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName evaluateCalFunction = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "evaluateCalFunction");

		/**
		 * Extracts the first field of the specified tuple / the ordinal field <code>#1</code> of the specified record.
		 * @param r (CAL type: <code>r\#1 => {r | #1 :: a}</code>)
		 *          the tuple / record with the ordinal field <code>#1</code>.
		 * @return (CAL type: <code>a</code>) 
		 *          the requested field.
		 */
		public static final SourceModel.Expr field1(SourceModel.Expr r) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.field1), r});
		}

		/**
		 * Name binding for function: field1.
		 * @see #field1(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName field1 = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "field1");

		/**
		 * Extracts the second field of the specified tuple / the ordinal field <code>#2</code> of the specified record.
		 * @param r (CAL type: <code>r\#2 => {r | #2 :: a}</code>)
		 *          the tuple / record with the ordinal field <code>#2</code>.
		 * @return (CAL type: <code>a</code>) 
		 *          the requested field.
		 */
		public static final SourceModel.Expr field2(SourceModel.Expr r) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.field2), r});
		}

		/**
		 * Name binding for function: field2.
		 * @see #field2(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName field2 = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "field2");

		/**
		 * Extracts the third field of the specified tuple / the ordinal field <code>#3</code> of the specified record.
		 * @param r (CAL type: <code>r\#3 => {r | #3 :: a}</code>)
		 *          the tuple / record with the ordinal field <code>#3</code>.
		 * @return (CAL type: <code>a</code>) 
		 *          the requested field.
		 */
		public static final SourceModel.Expr field3(SourceModel.Expr r) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.field3), r});
		}

		/**
		 * Name binding for function: field3.
		 * @see #field3(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName field3 = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "field3");

		/**
		 * Extracts the fourth field of the specified tuple / the ordinal field <code>#4</code> of the specified record.
		 * @param r (CAL type: <code>r\#4 => {r | #4 :: a}</code>)
		 *          the tuple / record with the ordinal field <code>#4</code>.
		 * @return (CAL type: <code>a</code>) 
		 *          the requested field.
		 */
		public static final SourceModel.Expr field4(SourceModel.Expr r) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.field4), r});
		}

		/**
		 * Name binding for function: field4.
		 * @see #field4(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName field4 = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "field4");

		/**
		 * Extracts the fifth field of the specified tuple / the ordinal field <code>#5</code> of the specified record.
		 * @param r (CAL type: <code>r\#5 => {r | #5 :: a}</code>)
		 *          the tuple / record with the ordinal field <code>#5</code>.
		 * @return (CAL type: <code>a</code>) 
		 *          the requested field.
		 */
		public static final SourceModel.Expr field5(SourceModel.Expr r) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.field5), r});
		}

		/**
		 * Name binding for function: field5.
		 * @see #field5(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName field5 = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "field5");

		/**
		 * Extracts the sixth field of the specified tuple / the ordinal field <code>#6</code> of the specified record.
		 * @param r (CAL type: <code>r\#6 => {r | #6 :: a}</code>)
		 *          the tuple / record with the ordinal field <code>#6</code>.
		 * @return (CAL type: <code>a</code>) 
		 *          the requested field.
		 */
		public static final SourceModel.Expr field6(SourceModel.Expr r) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.field6), r});
		}

		/**
		 * Name binding for function: field6.
		 * @see #field6(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName field6 = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "field6");

		/**
		 * Extracts the seventh field of the specified tuple / the ordinal field <code>#7</code> of the specified record.
		 * @param r (CAL type: <code>r\#7 => {r | #7 :: a}</code>)
		 *          the tuple / record with the ordinal field <code>#7</code>.
		 * @return (CAL type: <code>a</code>) 
		 *          the requested field.
		 */
		public static final SourceModel.Expr field7(SourceModel.Expr r) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.field7), r});
		}

		/**
		 * Name binding for function: field7.
		 * @see #field7(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName field7 = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "field7");

		/**
		 * <code>Cal.Core.Prelude.flip</code> converts a function of 2 arguments to another function of 2 arguments that accepts its
		 * arguments in the opposite order. It is useful when making a partial application to the second
		 * argument of a 2 argument function.
		 * @param f (CAL type: <code>a -> b -> c</code>)
		 *          the function whose argument order is to be flipped.
		 * @param x (CAL type: <code>b</code>)
		 *          the second argument to f.
		 * @param y (CAL type: <code>a</code>)
		 *          the first argument to f.
		 * @return (CAL type: <code>c</code>) 
		 *          the result of evaluating <code>(f y x)</code>.
		 */
		public static final SourceModel.Expr flip(SourceModel.Expr f, SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.flip), f, x, y});
		}

		/**
		 * Name binding for function: flip.
		 * @see #flip(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName flip = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "flip");

		/**
		 * Returns the greatest integer less than or equal to <code>x</code>.
		 * @param x (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 *          the <code>Cal.Core.Prelude.Double</code> value whose floor is to be returned.
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 *          the greatest integer less than or equal to <code>x</code>.
		 */
		public static final SourceModel.Expr floor(SourceModel.Expr x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floor), x});
		}

		/**
		 * @see #floor(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param x
		 * @return the SourceModel.Expr representing an application of floor
		 */
		public static final SourceModel.Expr floor(double x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.floor), SourceModel.Expr.makeDoubleValue(x)});
		}

		/**
		 * Name binding for function: floor.
		 * @see #floor(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName floor = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "floor");

		/**
		 * Converts a <code>Cal.Core.Prelude.Byte</code> value to the corresponding value in a type that is an instance of <code>Cal.Core.Prelude.Num</code>.
		 * Conversions to <code>Cal.Core.Prelude.Byte</code>, <code>Cal.Core.Prelude.Short</code>, <code>Cal.Core.Prelude.Int</code>, <code>Cal.Core.Prelude.Long</code>, <code>Cal.Core.Prelude.Float</code> and <code>Cal.Core.Prelude.Double</code>
		 * behave like the corresponding Java primitive cast operator.
		 * @param byteValue (CAL type: <code>Cal.Core.Prelude.Byte</code>)
		 *          the <code>Cal.Core.Prelude.Byte</code> value to be converted.
		 * @return (CAL type: <code>Cal.Core.Prelude.Num a => a</code>) 
		 *          the corresponding value in the result type.
		 */
		public static final SourceModel.Expr fromByte(SourceModel.Expr byteValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fromByte), byteValue});
		}

		/**
		 * @see #fromByte(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param byteValue
		 * @return the SourceModel.Expr representing an application of fromByte
		 */
		public static final SourceModel.Expr fromByte(byte byteValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fromByte), SourceModel.Expr.makeByteValue(byteValue)});
		}

		/**
		 * Name binding for function: fromByte.
		 * @see #fromByte(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fromByte = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "fromByte");

		/**
		 * Converts a <code>Cal.Core.Prelude.Decimal</code> value to a value of the instance type.
		 * @param decimalValue (CAL type: <code>Cal.Core.Prelude.Decimal</code>)
		 *          the <code>Cal.Core.Prelude.Decimal</code> value to convert.
		 * @return (CAL type: <code>Cal.Core.Prelude.Num a => a</code>) 
		 *          the value of the instance type which correspond to the given <code>Cal.Core.Prelude.Integer</code> value.
		 */
		public static final SourceModel.Expr fromDecimal(SourceModel.Expr decimalValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fromDecimal), decimalValue});
		}

		/**
		 * Name binding for function: fromDecimal.
		 * @see #fromDecimal(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fromDecimal = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "fromDecimal");

		/**
		 * Converts a <code>Cal.Core.Prelude.Double</code> value to the corresponding value in a type that is an instance of <code>Cal.Core.Prelude.Num</code>.
		 * Conversions to <code>Cal.Core.Prelude.Byte</code>, <code>Cal.Core.Prelude.Short</code>, <code>Cal.Core.Prelude.Int</code>, <code>Cal.Core.Prelude.Long</code>, <code>Cal.Core.Prelude.Float</code> and <code>Cal.Core.Prelude.Double</code>
		 * behave like the corresponding Java primitive cast operator.
		 * @param doubleValue (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 *          the <code>Cal.Core.Prelude.Double</code> value to be converted.
		 * @return (CAL type: <code>Cal.Core.Prelude.Num a => a</code>) 
		 *          the corresponding value in the result type.
		 */
		public static final SourceModel.Expr fromDouble(SourceModel.Expr doubleValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fromDouble), doubleValue});
		}

		/**
		 * @see #fromDouble(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param doubleValue
		 * @return the SourceModel.Expr representing an application of fromDouble
		 */
		public static final SourceModel.Expr fromDouble(double doubleValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fromDouble), SourceModel.Expr.makeDoubleValue(doubleValue)});
		}

		/**
		 * Name binding for function: fromDouble.
		 * @see #fromDouble(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fromDouble = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "fromDouble");

		/**
		 * Converts a <code>Cal.Core.Prelude.Float</code> value to the corresponding value in a type that is an instance of <code>Cal.Core.Prelude.Num</code>.
		 * Conversions to <code>Cal.Core.Prelude.Byte</code>, <code>Cal.Core.Prelude.Short</code>, <code>Cal.Core.Prelude.Int</code>, <code>Cal.Core.Prelude.Long</code>, <code>Cal.Core.Prelude.Float</code> and <code>Cal.Core.Prelude.Double</code>
		 * behave like the corresponding Java primitive cast operator.
		 * @param floatValue (CAL type: <code>Cal.Core.Prelude.Float</code>)
		 *          the <code>Cal.Core.Prelude.Float</code> value to be converted.
		 * @return (CAL type: <code>Cal.Core.Prelude.Num a => a</code>) 
		 *          the corresponding value in the result type.
		 */
		public static final SourceModel.Expr fromFloat(SourceModel.Expr floatValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fromFloat), floatValue});
		}

		/**
		 * @see #fromFloat(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param floatValue
		 * @return the SourceModel.Expr representing an application of fromFloat
		 */
		public static final SourceModel.Expr fromFloat(float floatValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fromFloat), SourceModel.Expr.makeFloatValue(floatValue)});
		}

		/**
		 * Name binding for function: fromFloat.
		 * @see #fromFloat(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fromFloat = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "fromFloat");

		/**
		 * Converts an <code>Cal.Core.Prelude.Int</code> value to the corresponding value in a type that is an instance of <code>Cal.Core.Prelude.Num</code>.
		 * Conversions to <code>Cal.Core.Prelude.Byte</code>, <code>Cal.Core.Prelude.Short</code>, <code>Cal.Core.Prelude.Int</code>, <code>Cal.Core.Prelude.Long</code>, <code>Cal.Core.Prelude.Float</code> and <code>Cal.Core.Prelude.Double</code>
		 * behave like the corresponding Java primitive cast operator.
		 * @param intValue (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the <code>Cal.Core.Prelude.Int</code> value to be converted.
		 * @return (CAL type: <code>Cal.Core.Prelude.Num a => a</code>) 
		 *          the corresponding value in the result type.
		 */
		public static final SourceModel.Expr fromInt(SourceModel.Expr intValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fromInt), intValue});
		}

		/**
		 * @see #fromInt(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param intValue
		 * @return the SourceModel.Expr representing an application of fromInt
		 */
		public static final SourceModel.Expr fromInt(int intValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fromInt), SourceModel.Expr.makeIntValue(intValue)});
		}

		/**
		 * Name binding for function: fromInt.
		 * @see #fromInt(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fromInt = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "fromInt");

		/**
		 * Converts an <code>Cal.Core.Prelude.Integer</code> value to a value of the instance type.
		 * @param integerValue (CAL type: <code>Cal.Core.Prelude.Integer</code>)
		 *          the <code>Cal.Core.Prelude.Integer</code> value to convert.
		 * @return (CAL type: <code>Cal.Core.Prelude.Num a => a</code>) 
		 *          the value of the instance type which correspond to the given <code>Cal.Core.Prelude.Integer</code> value.
		 */
		public static final SourceModel.Expr fromInteger(SourceModel.Expr integerValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fromInteger), integerValue});
		}

		/**
		 * Name binding for function: fromInteger.
		 * @see #fromInteger(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fromInteger = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "fromInteger");

		/**
		 * Extracts the element out of a <code>Cal.Core.Prelude.Just</code> or terminates in an error if the <code>Cal.Core.Prelude.Maybe</code> value
		 * is <code>Cal.Core.Prelude.Nothing</code>.
		 * @param maybeValue (CAL type: <code>Cal.Core.Prelude.Maybe a</code>)
		 *          the value from which the element is to be extracted.
		 * @return (CAL type: <code>a</code>) 
		 *          the element out of a <code>Cal.Core.Prelude.Just</code>, or terminates in an error if the argument is <code>Cal.Core.Prelude.Nothing</code>.
		 */
		public static final SourceModel.Expr fromJust(SourceModel.Expr maybeValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fromJust), maybeValue});
		}

		/**
		 * Name binding for function: fromJust.
		 * @see #fromJust(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fromJust = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "fromJust");

		/**
		 * Extracts the <code>value</code> field of a <code>Cal.Core.Prelude.Left</code> value or terminates in an error if <code>eitherValue</code> is
		 * a <code>Cal.Core.Prelude.Right</code> value.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Core.Prelude.fromRight, Cal.Core.Prelude.isLeft
		 * </dl>
		 * 
		 * @param eitherValue (CAL type: <code>Cal.Core.Prelude.Either a b</code>)
		 * @return (CAL type: <code>a</code>) 
		 *          the <code>value</code> field of a <code>Cal.Core.Prelude.Left</code> value or terminates in an error if <code>eitherValue</code> is
		 * a <code>Cal.Core.Prelude.Right</code> value.
		 */
		public static final SourceModel.Expr fromLeft(SourceModel.Expr eitherValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fromLeft), eitherValue});
		}

		/**
		 * Name binding for function: fromLeft.
		 * @see #fromLeft(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fromLeft = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "fromLeft");

		/**
		 * Converts a <code>Cal.Core.Prelude.Long</code> value to the corresponding value in a type that is an instance of <code>Cal.Core.Prelude.Num</code>.
		 * Conversions to <code>Cal.Core.Prelude.Byte</code>, <code>Cal.Core.Prelude.Short</code>, <code>Cal.Core.Prelude.Int</code>, <code>Cal.Core.Prelude.Long</code>, <code>Cal.Core.Prelude.Float</code> and <code>Cal.Core.Prelude.Double</code>
		 * behave like the corresponding Java primitive cast operator.
		 * @param longValue (CAL type: <code>Cal.Core.Prelude.Long</code>)
		 *          the <code>Cal.Core.Prelude.Long</code> value to be converted.
		 * @return (CAL type: <code>Cal.Core.Prelude.Num a => a</code>) 
		 *          the corresponding value in the result type.
		 */
		public static final SourceModel.Expr fromLong(SourceModel.Expr longValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fromLong), longValue});
		}

		/**
		 * @see #fromLong(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param longValue
		 * @return the SourceModel.Expr representing an application of fromLong
		 */
		public static final SourceModel.Expr fromLong(long longValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fromLong), SourceModel.Expr.makeLongValue(longValue)});
		}

		/**
		 * Name binding for function: fromLong.
		 * @see #fromLong(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fromLong = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "fromLong");

		/**
		 * Extracts the element out of a <code>Cal.Core.Prelude.Just</code> or returns the specified default value if the <code>Cal.Core.Prelude.Maybe</code> value
		 * is <code>Cal.Core.Prelude.Nothing</code>.
		 * @param defaultValue (CAL type: <code>a</code>)
		 *          the default value to be returned if the argument is <code>Cal.Core.Prelude.Nothing</code>.
		 * @param maybeValue (CAL type: <code>Cal.Core.Prelude.Maybe a</code>)
		 *          the value from which the element is to be extracted.
		 * @return (CAL type: <code>a</code>) 
		 *          the element out of a <code>Cal.Core.Prelude.Just</code>, or the specified default value if the argument is <code>Cal.Core.Prelude.Nothing</code>.
		 */
		public static final SourceModel.Expr fromMaybe(SourceModel.Expr defaultValue, SourceModel.Expr maybeValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fromMaybe), defaultValue, maybeValue});
		}

		/**
		 * Name binding for function: fromMaybe.
		 * @see #fromMaybe(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fromMaybe = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "fromMaybe");

		/**
		 * Extracts the <code>value</code> field of a <code>Cal.Core.Prelude.Right</code> value or terminates in an error if <code>eitherValue</code> is
		 * a <code>Cal.Core.Prelude.Left</code> value.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Core.Prelude.fromLeft, Cal.Core.Prelude.isRight
		 * </dl>
		 * 
		 * @param eitherValue (CAL type: <code>Cal.Core.Prelude.Either a b</code>)
		 * @return (CAL type: <code>b</code>) 
		 *          the <code>value</code> field of a <code>Cal.Core.Prelude.Right</code> value or terminates in an error if <code>eitherValue</code> is
		 * a <code>Cal.Core.Prelude.Left</code> value.
		 */
		public static final SourceModel.Expr fromRight(SourceModel.Expr eitherValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fromRight), eitherValue});
		}

		/**
		 * Name binding for function: fromRight.
		 * @see #fromRight(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fromRight = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "fromRight");

		/**
		 * Converts a <code>Cal.Core.Prelude.Short</code> value to the corresponding value in a type that is an instance of <code>Cal.Core.Prelude.Num</code>.
		 * Conversions to <code>Cal.Core.Prelude.Byte</code>, <code>Cal.Core.Prelude.Short</code>, <code>Cal.Core.Prelude.Int</code>, <code>Cal.Core.Prelude.Long</code>, <code>Cal.Core.Prelude.Float</code> and <code>Cal.Core.Prelude.Double</code>
		 * behave like the corresponding Java primitive cast operator.
		 * @param shortValue (CAL type: <code>Cal.Core.Prelude.Short</code>)
		 *          the <code>Cal.Core.Prelude.Short</code> value to be converted.
		 * @return (CAL type: <code>Cal.Core.Prelude.Num a => a</code>) 
		 *          the corresponding value in the result type.
		 */
		public static final SourceModel.Expr fromShort(SourceModel.Expr shortValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fromShort), shortValue});
		}

		/**
		 * @see #fromShort(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param shortValue
		 * @return the SourceModel.Expr representing an application of fromShort
		 */
		public static final SourceModel.Expr fromShort(short shortValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fromShort), SourceModel.Expr.makeShortValue(shortValue)});
		}

		/**
		 * Name binding for function: fromShort.
		 * @see #fromShort(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fromShort = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "fromShort");

		/**
		 * Extracts the first component of a pair.
		 * @param pair (CAL type: <code>(a, b)</code>)
		 *          the pair.
		 * @return (CAL type: <code>a</code>) 
		 *          the first component of the pair.
		 */
		public static final SourceModel.Expr fst(SourceModel.Expr pair) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fst), pair});
		}

		/**
		 * Name binding for function: fst.
		 * @see #fst(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fst = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "fst");

		/**
		 * Returns whether the first argument is greater than the second argument.
		 * <p>
		 * The operator form of <code>Cal.Core.Prelude.greaterThan</code> is <code>&gt;</code>.
		 * 
		 * @param x (CAL type: <code>Cal.Core.Prelude.Ord a => a</code>)
		 *          the first argument to compare.
		 * @param y (CAL type: <code>Cal.Core.Prelude.Ord a => a</code>)
		 *          the second argument to compare.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if <code>x</code> is greater than <code>y</code>; <code>Cal.Core.Prelude.False</code> otherwise.
		 */
		public static final SourceModel.Expr greaterThan(SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.greaterThan), x, y});
		}

		/**
		 * Name binding for function: greaterThan.
		 * @see #greaterThan(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName greaterThan = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "greaterThan");

		/**
		 * Returns whether the first argument is greater than or equal to the second argument.
		 * <p>
		 * The operator form of <code>Cal.Core.Prelude.greaterThanEquals</code> is <code>&gt;=</code>.
		 * 
		 * @param x (CAL type: <code>Cal.Core.Prelude.Ord a => a</code>)
		 *          the first argument to compare.
		 * @param y (CAL type: <code>Cal.Core.Prelude.Ord a => a</code>)
		 *          the second argument to compare.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if <code>x</code> is greater than or equal to <code>y</code>; <code>Cal.Core.Prelude.False</code> otherwise.
		 */
		public static final SourceModel.Expr greaterThanEquals(SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.greaterThanEquals), x, y});
		}

		/**
		 * Name binding for function: greaterThanEquals.
		 * @see #greaterThanEquals(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName greaterThanEquals = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "greaterThanEquals");

		/**
		 * An identity function.
		 * @param x (CAL type: <code>a</code>)
		 *          the argument.
		 * @return (CAL type: <code>a</code>) 
		 *          <code>x</code>, the supplied argument.
		 */
		public static final SourceModel.Expr id(SourceModel.Expr x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.id), x});
		}

		/**
		 * Name binding for function: id.
		 * @see #id(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName id = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "id");

		/**
		 * Exposes the functionality of "if-then-else" as a function.
		 * <code>Cal.Core.Prelude.iff</code> can be thought of as the function form of the "if-the-else" construct.
		 * @param condition (CAL type: <code>Cal.Core.Prelude.Boolean</code>)
		 *          the condition to test.
		 * @param trueValue (CAL type: <code>a</code>)
		 *          the value to return if the condition holds.
		 * @param falseValue (CAL type: <code>a</code>)
		 *          the value to return if the condition does not hold.
		 * @return (CAL type: <code>a</code>) 
		 *          <code>trueValue</code> if <code>condition</code> evaluates to <code>Cal.Core.Prelude.True</code>, otherwise <code>falseValue</code>.
		 */
		public static final SourceModel.Expr iff(SourceModel.Expr condition, SourceModel.Expr trueValue, SourceModel.Expr falseValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.iff), condition, trueValue, falseValue});
		}

		/**
		 * @see #iff(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param condition
		 * @param trueValue
		 * @param falseValue
		 * @return the SourceModel.Expr representing an application of iff
		 */
		public static final SourceModel.Expr iff(boolean condition, SourceModel.Expr trueValue, SourceModel.Expr falseValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.iff), SourceModel.Expr.makeBooleanValue(condition), trueValue, falseValue});
		}

		/**
		 * Name binding for function: iff.
		 * @see #iff(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName iff = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "iff");

		/**
		 * Induces an comparator on a type <code>a</code> via projecting to a type <code>b</code> and using the <code>Cal.Core.Prelude.Ord</code> instance on <code>b</code>.
		 * @param projectionFunction (CAL type: <code>Cal.Core.Prelude.Ord b => a -> b</code>)
		 *          projects the type <code>a</code> into the type <code>b</code>, where <code>Cal.Core.Prelude.compare</code> is used
		 * for the comparison.
		 * @param x (CAL type: <code>a</code>)
		 *          first argument of the induced comparator.
		 * @param y (CAL type: <code>a</code>)
		 *          second argument of the induced comparator.
		 * @return (CAL type: <code>Cal.Core.Prelude.Ordering</code>) 
		 *          the result of applying the induced comparator on <code>x</code> and <code>y</code>.
		 */
		public static final SourceModel.Expr induceComparator(SourceModel.Expr projectionFunction, SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.induceComparator), projectionFunction, x, y});
		}

		/**
		 * Name binding for function: induceComparator.
		 * @see #induceComparator(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName induceComparator = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "induceComparator");

		/**
		 * Induces an equality function on a type <code>a</code> via projecting to a type <code>b</code> and using the <code>Cal.Core.Prelude.Eq</code> instance
		 * on <code>b</code>.
		 * @param projectionFunction (CAL type: <code>Cal.Core.Prelude.Eq b => a -> b</code>)
		 *          projects the type <code>a</code> into the type <code>b</code>, where <code>Cal.Core.Prelude.equals</code> is used for
		 * the comparison.
		 * @param x (CAL type: <code>a</code>)
		 *          first argument of the induced equality function.
		 * @param y (CAL type: <code>a</code>)
		 *          second argument of the induced equality function.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          the result of applying the induced equality function on <code>x</code> and <code>y</code>.
		 */
		public static final SourceModel.Expr induceEqualityFunction(SourceModel.Expr projectionFunction, SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.induceEqualityFunction), projectionFunction, x, y});
		}

		/**
		 * Name binding for function: induceEqualityFunction.
		 * @see #induceEqualityFunction(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName induceEqualityFunction = 
			QualifiedName.make(
				CAL_Prelude.MODULE_NAME, 
				"induceEqualityFunction");

		/**
		 * Converts a Java value into a value of the instance type.
		 * @param jobject (CAL type: <code>Cal.Core.Prelude.JObject</code>)
		 *          the JObject to be converted.
		 * @return (CAL type: <code>Cal.Core.Prelude.Inputable a => a</code>) 
		 *          the corresponding value in the instance type.
		 */
		public static final SourceModel.Expr input(SourceModel.Expr jobject) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.input), jobject});
		}

		/**
		 * Name binding for function: input.
		 * @see #input(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName input = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "input");

		/**
		 * Converts an <code>Cal.Core.Prelude.Int</code> value to its corresponding value of type <code>Cal.Core.Prelude.IntEnum a =&gt; a</code>.
		 * Terminates in an error if the <code>Cal.Core.Prelude.Int</code> does not correspond to a value of the type <code>IntEnum a =&gt; a</code>.
		 * @param intValue (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          int value to convert to an enum.
		 * @return (CAL type: <code>Cal.Core.Prelude.IntEnum a => a</code>) 
		 *          the corresponding value of type <code>Cal.Core.Prelude.IntEnum a =&gt; a</code>.
		 */
		public static final SourceModel.Expr intToEnum(SourceModel.Expr intValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intToEnum), intValue});
		}

		/**
		 * @see #intToEnum(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param intValue
		 * @return the SourceModel.Expr representing an application of intToEnum
		 */
		public static final SourceModel.Expr intToEnum(int intValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intToEnum), SourceModel.Expr.makeIntValue(intValue)});
		}

		/**
		 * Name binding for function: intToEnum.
		 * @see #intToEnum(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intToEnum = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "intToEnum");

		/**
		 * Converts an <code>Cal.Core.Prelude.Int</code> value to its corresponding value of type <code>Cal.Core.Prelude.IntEnum a =&gt; a</code>, if such a value exists,
		 * or returns <code>Cal.Core.Prelude.Nothing</code> if such a value does not exist.
		 * @param intValue (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          int value to convert to an enum.
		 * @return (CAL type: <code>Cal.Core.Prelude.IntEnum a => Cal.Core.Prelude.Maybe a</code>) 
		 *          the corresponding value of type <code>Cal.Core.Prelude.IntEnum a =&gt; a</code>, wrapped in a <code>Cal.Core.Prelude.Just</code>, if such a value exists,
		 * or <code>Cal.Core.Prelude.Nothing</code> if such a value does not exist.
		 */
		public static final SourceModel.Expr intToEnumChecked(SourceModel.Expr intValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intToEnumChecked), intValue});
		}

		/**
		 * @see #intToEnumChecked(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param intValue
		 * @return the SourceModel.Expr representing an application of intToEnumChecked
		 */
		public static final SourceModel.Expr intToEnumChecked(int intValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intToEnumChecked), SourceModel.Expr.makeIntValue(intValue)});
		}

		/**
		 * Name binding for function: intToEnumChecked.
		 * @see #intToEnumChecked(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intToEnumChecked = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "intToEnumChecked");

		/**
		 * Converts an <code>Cal.Core.Prelude.Int</code> value to an <code>Cal.Core.Prelude.Ordering</code> value. In particular, if the specified <code>Cal.Core.Prelude.Int</code> value is
		 * <code>&lt; 0</code>, <code>Cal.Core.Prelude.LT</code> is returned. If the <code>Cal.Core.Prelude.Int</code> value is <code>&gt; 0</code>, <code>Cal.Core.Prelude.GT</code> is returned. <code>Cal.Core.Prelude.EQ</code>
		 * is returned if the <code>Cal.Core.Prelude.Int</code> value is exactly <code>0</code>.
		 * @param intValue (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the <code>Cal.Core.Prelude.Int</code> value to be converted.
		 * @return (CAL type: <code>Cal.Core.Prelude.Ordering</code>) 
		 *          <code>Cal.Core.Prelude.LT</code>, <code>Cal.Core.Prelude.EQ</code>, or <code>Cal.Core.Prelude.GT</code> if the <code>Cal.Core.Prelude.Int</code> value is respectively less than 0, equal to 0, or
		 * greater than 0.
		 */
		public static final SourceModel.Expr intToOrdering(SourceModel.Expr intValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intToOrdering), intValue});
		}

		/**
		 * @see #intToOrdering(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param intValue
		 * @return the SourceModel.Expr representing an application of intToOrdering
		 */
		public static final SourceModel.Expr intToOrdering(int intValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intToOrdering), SourceModel.Expr.makeIntValue(intValue)});
		}

		/**
		 * Name binding for function: intToOrdering.
		 * @see #intToOrdering(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intToOrdering = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "intToOrdering");

		/**
		 * Converts a <code>Cal.Core.Prelude.Integer</code> value to its string representation.
		 * @param value (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the <code>Cal.Core.Prelude.Integer</code> value.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          the string representation.
		 */
		public static final SourceModel.Expr intToString(SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intToString), value});
		}

		/**
		 * @see #intToString(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param value
		 * @return the SourceModel.Expr representing an application of intToString
		 */
		public static final SourceModel.Expr intToString(int value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.intToString), SourceModel.Expr.makeIntValue(value)});
		}

		/**
		 * Name binding for function: intToString.
		 * @see #intToString(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName intToString = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "intToString");

		/**
		 * Converts an <code>Cal.Core.Prelude.Integer</code> value to its string representation.
		 * @param integerValue (CAL type: <code>Cal.Core.Prelude.Integer</code>)
		 *          the <code>Cal.Core.Prelude.Integer</code> value to be converted.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          the string representation of the given number.
		 */
		public static final SourceModel.Expr integerToString(SourceModel.Expr integerValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.integerToString), integerValue});
		}

		/**
		 * Name binding for function: integerToString.
		 * @see #integerToString(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName integerToString = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "integerToString");

		/**
		 * Returns <code>Cal.Core.Prelude.True</code> if the runtime type representation is that of the <code>Cal.Core.Prelude.Boolean</code> type and <code>Cal.Core.Prelude.False</code> otherwise.
		 * @param typeRep (CAL type: <code>Cal.Core.Prelude.TypeRep</code>)
		 *          runtime type representation of a value, typically obtained by calling <code>Cal.Core.Prelude.typeOf</code> on a value.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the runtime type representation is that of the <code>Cal.Core.Prelude.Boolean</code> type and <code>Cal.Core.Prelude.False</code> otherwise.
		 */
		public static final SourceModel.Expr isBooleanType(SourceModel.Expr typeRep) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isBooleanType), typeRep});
		}

		/**
		 * Name binding for function: isBooleanType.
		 * @see #isBooleanType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isBooleanType = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "isBooleanType");

		/**
		 * Returns <code>Cal.Core.Prelude.True</code> if the runtime type representation is that of the <code>Cal.Core.Prelude.Char</code> type and <code>Cal.Core.Prelude.False</code> otherwise.
		 * @param typeRep (CAL type: <code>Cal.Core.Prelude.TypeRep</code>)
		 *          runtime type representation of a value, typically obtained by calling <code>Cal.Core.Prelude.typeOf</code> on a value.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the runtime type representation is that of the <code>Cal.Core.Prelude.Char</code> type and <code>Cal.Core.Prelude.False</code> otherwise.
		 */
		public static final SourceModel.Expr isCharType(SourceModel.Expr typeRep) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isCharType), typeRep});
		}

		/**
		 * Name binding for function: isCharType.
		 * @see #isCharType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isCharType = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "isCharType");

		/**
		 * Returns <code>Cal.Core.Prelude.True</code> if the runtime type representation is that of the <code>Cal.Core.Prelude.Double</code> type and <code>Cal.Core.Prelude.False</code> otherwise.
		 * @param typeRep (CAL type: <code>Cal.Core.Prelude.TypeRep</code>)
		 *          runtime type representation of a value, typically obtained by calling <code>Cal.Core.Prelude.typeOf</code> on a value.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the runtime type representation is that of the <code>Cal.Core.Prelude.Double</code> type and <code>Cal.Core.Prelude.False</code> otherwise.
		 */
		public static final SourceModel.Expr isDoubleType(SourceModel.Expr typeRep) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isDoubleType), typeRep});
		}

		/**
		 * Name binding for function: isDoubleType.
		 * @see #isDoubleType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isDoubleType = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "isDoubleType");

		/**
		 * Returns whether the specified value is the empty value.
		 * @param value (CAL type: <code>Cal.Core.Prelude.Appendable a => a</code>)
		 *          the value to check.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the argument is the empty value; <code>Cal.Core.Prelude.False</code> otherwise.
		 */
		public static final SourceModel.Expr isEmpty(SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isEmpty), value});
		}

		/**
		 * Name binding for function: isEmpty.
		 * @see #isEmpty(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isEmpty = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "isEmpty");

		/**
		 * Returns whether the argument is even.
		 * @param x (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the <code>Cal.Core.Prelude.Int</code> value whose evenness is to be determined.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the argument is even; <code>Cal.Core.Prelude.False</code> otherwise.
		 */
		public static final SourceModel.Expr isEven(SourceModel.Expr x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isEven), x});
		}

		/**
		 * @see #isEven(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param x
		 * @return the SourceModel.Expr representing an application of isEven
		 */
		public static final SourceModel.Expr isEven(int x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isEven), SourceModel.Expr.makeIntValue(x)});
		}

		/**
		 * Name binding for function: isEven.
		 * @see #isEven(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isEven = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "isEven");

		/**
		 * Returns <code>Cal.Core.Prelude.True</code> if the runtime type representation is that of a function type and <code>Cal.Core.Prelude.False</code> otherwise.
		 * Note that the function type must be fully saturated i.e. <code>Int -&gt; Char</code> returns <code>Cal.Core.Prelude.True</code> but
		 * <code>Function Int</code> and <code>Cal.Core.Prelude.Function</code> return <code>Cal.Core.Prelude.False</code>.
		 * @param typeRep (CAL type: <code>Cal.Core.Prelude.TypeRep</code>)
		 *          runtime type representation of a value, typically obtained by calling <code>Cal.Core.Prelude.typeOf</code> on a value.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the runtime type representation is that of a function type and <code>Cal.Core.Prelude.False</code> otherwise.
		 */
		public static final SourceModel.Expr isFunctionType(SourceModel.Expr typeRep) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isFunctionType), typeRep});
		}

		/**
		 * Name binding for function: isFunctionType.
		 * @see #isFunctionType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isFunctionType = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "isFunctionType");

		/**
		 * Returns <code>Cal.Core.Prelude.True</code> if the runtime type representation is that of the <code>Cal.Core.Prelude.Int</code> type and <code>Cal.Core.Prelude.False</code> otherwise.
		 * @param typeRep (CAL type: <code>Cal.Core.Prelude.TypeRep</code>)
		 *          runtime type representation of a value, typically obtained by calling <code>Cal.Core.Prelude.typeOf</code> on a value.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the runtime type representation is that of the <code>Cal.Core.Prelude.Int</code> type and <code>Cal.Core.Prelude.False</code> otherwise.
		 */
		public static final SourceModel.Expr isIntType(SourceModel.Expr typeRep) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isIntType), typeRep});
		}

		/**
		 * Name binding for function: isIntType.
		 * @see #isIntType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isIntType = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "isIntType");

		/**
		 * Returns <code>Cal.Core.Prelude.True</code> iff the argument is of the form <code>Cal.Core.Prelude.Just _</code>.
		 * @param maybeValue (CAL type: <code>Cal.Core.Prelude.Maybe a</code>)
		 *          the value to be checked.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if <code>maybeValue</code> is of the form <code>Cal.Core.Prelude.Just _</code>; <code>Cal.Core.Prelude.False</code> otherwise.
		 */
		public static final SourceModel.Expr isJust(SourceModel.Expr maybeValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isJust), maybeValue});
		}

		/**
		 * Name binding for function: isJust.
		 * @see #isJust(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isJust = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "isJust");

		/**
		 * Returns <code>Cal.Core.Prelude.True</code> if <code>eitherValue</code> is a <code>Cal.Core.Prelude.Left</code> value and <code>Cal.Core.Prelude.False</code> otherwise.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Core.Prelude.isRight, Cal.Core.Prelude.fromLeft
		 * </dl>
		 * 
		 * @param eitherValue (CAL type: <code>Cal.Core.Prelude.Either a b</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if <code>eitherValue</code> is a <code>Cal.Core.Prelude.Left</code> value and <code>Cal.Core.Prelude.False</code> otherwise.
		 */
		public static final SourceModel.Expr isLeft(SourceModel.Expr eitherValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isLeft), eitherValue});
		}

		/**
		 * Name binding for function: isLeft.
		 * @see #isLeft(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isLeft = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "isLeft");

		/**
		 * Returns <code>Cal.Core.Prelude.True</code> if the runtime type representation is that of a <code>Cal.Core.Prelude.List</code> and <code>Cal.Core.Prelude.False</code> otherwise.
		 * For example, the function returns <code>Cal.Core.Prelude.True</code> for values of type <code>[Double]</code> and <code>[(Char, Double)]</code> 
		 * but <code>Cal.Core.Prelude.False</code> for values of type <code>Cal.Core.Prelude.Double</code> and <code>([Double], Boolean)</code>. Note that the type
		 * <code>([Double], Boolean)</code> involves a <code>Cal.Core.Prelude.List</code> type, but this function looks only at the outermost type,
		 * which is a 2-tuple in this case and thus not a <code>Cal.Core.Prelude.List</code>.
		 * @param typeRep (CAL type: <code>Cal.Core.Prelude.TypeRep</code>)
		 *          runtime type representation of a value, typically obtained by calling <code>Cal.Core.Prelude.typeOf</code> on a value.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the runtime type representation is that of a <code>Cal.Core.Prelude.List</code> and <code>Cal.Core.Prelude.False</code> otherwise.
		 */
		public static final SourceModel.Expr isListType(SourceModel.Expr typeRep) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isListType), typeRep});
		}

		/**
		 * Name binding for function: isListType.
		 * @see #isListType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isListType = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "isListType");

		/**
		 * Returns <code>Cal.Core.Prelude.True</code> if the runtime type representation is that of the <code>Cal.Core.Prelude.Maybe</code> type and <code>Cal.Core.Prelude.False</code> otherwise.
		 * For example, it will return <code>Cal.Core.Prelude.True</code> for <code>Maybe Char</code>, and <code>Maybe Int</code> but <code>Cal.Core.Prelude.False</code> for
		 * <code>[Int]</code> and <code>[Maybe Int]</code>.
		 * @param typeRep (CAL type: <code>Cal.Core.Prelude.TypeRep</code>)
		 *          runtime type representation of a value, typically obtained by calling <code>Cal.Core.Prelude.typeOf</code> on a value.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the runtime type representation is that of the <code>Cal.Core.Prelude.Maybe</code> type and <code>Cal.Core.Prelude.False</code> otherwise.
		 */
		public static final SourceModel.Expr isMaybeType(SourceModel.Expr typeRep) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isMaybeType), typeRep});
		}

		/**
		 * Name binding for function: isMaybeType.
		 * @see #isMaybeType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isMaybeType = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "isMaybeType");

		/**
		 * Checks whether a <code>Cal.Core.Prelude.Double</code> value is the special not-a-number value.
		 * @param doubleValue (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 *          the Double value to be checked.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          True if doubleValue is the special not-a-number value.
		 */
		public static final SourceModel.Expr isNotANumber(SourceModel.Expr doubleValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isNotANumber), doubleValue});
		}

		/**
		 * @see #isNotANumber(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param doubleValue
		 * @return the SourceModel.Expr representing an application of isNotANumber
		 */
		public static final SourceModel.Expr isNotANumber(double doubleValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isNotANumber), SourceModel.Expr.makeDoubleValue(doubleValue)});
		}

		/**
		 * Name binding for function: isNotANumber.
		 * @see #isNotANumber(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isNotANumber = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "isNotANumber");

		/**
		 * Returns <code>Cal.Core.Prelude.True</code> iff the argument is <code>Cal.Core.Prelude.Nothing</code>.
		 * @param maybeValue (CAL type: <code>Cal.Core.Prelude.Maybe a</code>)
		 *          the value to be checked.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if maybeValue is <code>Cal.Core.Prelude.Nothing</code>; <code>Cal.Core.Prelude.False</code> otherwise.
		 */
		public static final SourceModel.Expr isNothing(SourceModel.Expr maybeValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isNothing), maybeValue});
		}

		/**
		 * Name binding for function: isNothing.
		 * @see #isNothing(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isNothing = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "isNothing");

		/**
		 * Returns whether the argument is odd.
		 * @param x (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the <code>Cal.Core.Prelude.Int</code> value whose oddness is to be determined.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the argument is odd; <code>Cal.Core.Prelude.False</code> otherwise.
		 */
		public static final SourceModel.Expr isOdd(SourceModel.Expr x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isOdd), x});
		}

		/**
		 * @see #isOdd(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param x
		 * @return the SourceModel.Expr representing an application of isOdd
		 */
		public static final SourceModel.Expr isOdd(int x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isOdd), SourceModel.Expr.makeIntValue(x)});
		}

		/**
		 * Name binding for function: isOdd.
		 * @see #isOdd(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isOdd = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "isOdd");

		/**
		 * Returns <code>Cal.Core.Prelude.True</code> if the runtime type representation is that of one of the types defined in the Prelude
		 * module as instances of the <code>Cal.Core.Prelude.Num</code> type class (<code>Cal.Core.Prelude.Byte</code>, <code>Cal.Core.Prelude.Short</code>, <code>Cal.Core.Prelude.Int</code>,
		 * <code>Cal.Core.Prelude.Long</code>, <code>Cal.Core.Prelude.Float</code>, <code>Cal.Core.Prelude.Double</code>, <code>Cal.Core.Prelude.Integer</code>) or <code>Cal.Core.Prelude.Decimal</code>
		 * and False otherwise.
		 * @param typeRep (CAL type: <code>Cal.Core.Prelude.TypeRep</code>)
		 *          runtime type representation of a value, typically obtained by calling <code>Cal.Core.Prelude.typeOf</code> on a value.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the runtime type representation is one of <code>Cal.Core.Prelude.Byte</code>, <code>Cal.Core.Prelude.Short</code>, <code>Cal.Core.Prelude.Int</code>,
		 * <code>Cal.Core.Prelude.Long</code>, <code>Cal.Core.Prelude.Float</code>, <code>Cal.Core.Prelude.Double</code>, <code>Cal.Core.Prelude.Integer</code> or <code>Cal.Core.Prelude.Decimal</code>
		 * and False otherwise.
		 */
		public static final SourceModel.Expr isPreludeNumType(SourceModel.Expr typeRep) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isPreludeNumType), typeRep});
		}

		/**
		 * Name binding for function: isPreludeNumType.
		 * @see #isPreludeNumType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isPreludeNumType = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "isPreludeNumType");

		/**
		 * Returns <code>Cal.Core.Prelude.True</code> if the runtime type representation is that of a record type and <code>Cal.Core.Prelude.False</code> otherwise.
		 * Note that record types include tuple types.
		 * @param typeRep (CAL type: <code>Cal.Core.Prelude.TypeRep</code>)
		 *          runtime type representation of a value, typically obtained by calling <code>Cal.Core.Prelude.typeOf</code> on a value.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the runtime type representation is that of a record or tuple type and <code>Cal.Core.Prelude.False</code> otherwise.
		 */
		public static final SourceModel.Expr isRecordType(SourceModel.Expr typeRep) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isRecordType), typeRep});
		}

		/**
		 * Name binding for function: isRecordType.
		 * @see #isRecordType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isRecordType = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "isRecordType");

		/**
		 * Returns <code>Cal.Core.Prelude.True</code> if <code>eitherValue</code> is a <code>Cal.Core.Prelude.Right</code> value and <code>Cal.Core.Prelude.False</code> otherwise.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Core.Prelude.isLeft, Cal.Core.Prelude.fromRight
		 * </dl>
		 * 
		 * @param eitherValue (CAL type: <code>Cal.Core.Prelude.Either a b</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if <code>eitherValue</code> is a <code>Cal.Core.Prelude.Right</code> value and <code>Cal.Core.Prelude.False</code> otherwise.
		 */
		public static final SourceModel.Expr isRight(SourceModel.Expr eitherValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isRight), eitherValue});
		}

		/**
		 * Name binding for function: isRight.
		 * @see #isRight(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isRight = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "isRight");

		/**
		 * Returns <code>Cal.Core.Prelude.True</code> if the runtime type representation is that of the <code>Cal.Core.Prelude.String</code> type and <code>Cal.Core.Prelude.False</code> otherwise.
		 * @param typeRep (CAL type: <code>Cal.Core.Prelude.TypeRep</code>)
		 *          runtime type representation of a value, typically obtained by calling <code>Cal.Core.Prelude.typeOf</code> on a value.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the runtime type representation is that of the <code>Cal.Core.Prelude.String</code> type and <code>Cal.Core.Prelude.False</code> otherwise.
		 */
		public static final SourceModel.Expr isStringType(SourceModel.Expr typeRep) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isStringType), typeRep});
		}

		/**
		 * Name binding for function: isStringType.
		 * @see #isStringType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isStringType = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "isStringType");

		/**
		 * Returns whether the first argument is less than the second argument.
		 * <p>
		 * The operator form of <code>Cal.Core.Prelude.lessThan</code> is <code>&lt;</code>.
		 * 
		 * @param x (CAL type: <code>Cal.Core.Prelude.Ord a => a</code>)
		 *          the first argument to compare.
		 * @param y (CAL type: <code>Cal.Core.Prelude.Ord a => a</code>)
		 *          the second argument to compare.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if <code>x</code> is less than <code>y</code>; <code>Cal.Core.Prelude.False</code> otherwise.
		 */
		public static final SourceModel.Expr lessThan(SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lessThan), x, y});
		}

		/**
		 * Name binding for function: lessThan.
		 * @see #lessThan(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName lessThan = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "lessThan");

		/**
		 * Returns whether the first argument is less than or equal to the second argument.
		 * <p>
		 * The operator form of <code>Cal.Core.Prelude.lessThanEquals</code> is <code>&lt;=</code>.
		 * 
		 * @param x (CAL type: <code>Cal.Core.Prelude.Ord a => a</code>)
		 *          the first argument to compare.
		 * @param y (CAL type: <code>Cal.Core.Prelude.Ord a => a</code>)
		 *          the second argument to compare.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if <code>x</code> is less than or equal to <code>y</code>; <code>Cal.Core.Prelude.False</code> otherwise.
		 */
		public static final SourceModel.Expr lessThanEquals(SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.lessThanEquals), x, y});
		}

		/**
		 * Name binding for function: lessThanEquals.
		 * @see #lessThanEquals(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName lessThanEquals = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "lessThanEquals");

		/**
		 * Returns <code>Cal.Core.Prelude.Nothing</code> on an empty list or <code>Cal.Core.Prelude.Just firstElement</code> where <code>firstElement</code> is
		 * the first element of the list.
		 * @param list (CAL type: <code>[a]</code>)
		 *          the list to be converted to a Maybe value.
		 * @return (CAL type: <code>Cal.Core.Prelude.Maybe a</code>) 
		 *          <code>Cal.Core.Prelude.Nothing</code> if the list is empty, or <code>Cal.Core.Prelude.Just firstElement</code> where <code>firstElement</code> is
		 * the first element of the list.
		 */
		public static final SourceModel.Expr listToMaybe(SourceModel.Expr list) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.listToMaybe), list});
		}

		/**
		 * Name binding for function: listToMaybe.
		 * @see #listToMaybe(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName listToMaybe = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "listToMaybe");

		/**
		 * Converts a <code>Cal.Core.Prelude.Long</code> value to its string representation.
		 * @param value (CAL type: <code>Cal.Core.Prelude.Long</code>)
		 *          the <code>Cal.Core.Prelude.Long</code> value.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          the string representation.
		 */
		public static final SourceModel.Expr longToString(SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longToString), value});
		}

		/**
		 * @see #longToString(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param value
		 * @return the SourceModel.Expr representing an application of longToString
		 */
		public static final SourceModel.Expr longToString(long value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.longToString), SourceModel.Expr.makeLongValue(value)});
		}

		/**
		 * Name binding for function: longToString.
		 * @see #longToString(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName longToString = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "longToString");

		/**
		 * Creates a <code>Cal.Core.Prelude.CalFunction</code> value from a CAL function of type <code>Cal.Core.Prelude.JObject -&gt; Cal.Core.Prelude.JObject</code>.
		 * <p>
		 * By combining arguments into a tuple, it is possible to create a <code>Cal.Core.Prelude.CalFunction</code> that effectively represents
		 * a multi-argument CAL function. By converting arguments to the <code>Cal.Core.Prelude.CalValue</code> type, it is possible to create
		 * a <code>Cal.Core.Prelude.CalFunction</code> that works with CAL values that can not be converted to Java values by typical means
		 * (such as using the <code>Cal.Core.Prelude.output</code> class method).
		 * 
		 * @param func (CAL type: <code>Cal.Core.Prelude.JObject -> Cal.Core.Prelude.JObject</code>)
		 *          the function to represent using the <code>Cal.Core.Prelude.CalFunction</code> type
		 * @return (CAL type: <code>Cal.Core.Prelude.CalFunction</code>) 
		 *          a foreign representation of the CAL function f
		 */
		public static final SourceModel.Expr makeCalFunction(SourceModel.Expr func) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeCalFunction), func});
		}

		/**
		 * Name binding for function: makeCalFunction.
		 * @see #makeCalFunction(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeCalFunction = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "makeCalFunction");

		/**
		 * <code>makeQuery defaultResult f x</code> behaves as follows: if <code>x</code>'s type is the same as <code>f</code>'s
		 * argument type, use <code>f</code> to interrogate <code>x</code>; otherwise return <code>defaultResult</code>.
		 * <p>
		 * For example:
		 * <ul>
		 *  <li>
		 *   <code>makeQuery 22 Cal.Core.Prelude.charToInt 'a'  == 97</code>
		 *  </li>
		 *  <li>
		 *   <code>makeQuery 22 Cal.Core.Prelude.charToInt 'b'  == 98</code>
		 *  </li>
		 *  <li>
		 *   <code>makeQuery 22 Cal.Core.Prelude.charToInt Cal.Core.Prelude.True == 22</code>
		 *  </li>
		 * </ul>
		 * 
		 * @param defaultResult (CAL type: <code>r</code>)
		 *          the default value to return if <code>x</code>'s type is not the same as <code>f</code>'s argument type.
		 * @param f (CAL type: <code>Cal.Core.Prelude.Typeable a => a -> r</code>)
		 *          the query function.
		 * @param x (CAL type: <code>Cal.Core.Prelude.Typeable b => b</code>)
		 *          the argument to be supplied to <code>f</code>.
		 * @return (CAL type: <code>r</code>) 
		 *          <code>(f x)</code> if <code>x</code>'s type is the same as <code>f</code>'s argument type, or <code>defaultResult</code> otherwise.
		 */
		public static final SourceModel.Expr makeQuery(SourceModel.Expr defaultResult, SourceModel.Expr f, SourceModel.Expr x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeQuery), defaultResult, f, x});
		}

		/**
		 * Name binding for function: makeQuery.
		 * @see #makeQuery(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeQuery = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "makeQuery");

		/**
		 * <code>makeTransform f x</code> applies <code>f</code> to <code>x</code> if <code>x</code>'s type is the same as <code>f</code>'s argument type,
		 * and otherwise applies the identity function <code>Cal.Core.Prelude.id</code> to <code>x</code>.
		 * <p>
		 * For example:
		 * <ul>
		 *  <li>
		 *   <code>makeTransform Cal.Core.Prelude.not Cal.Core.Prelude.True == Cal.Core.Prelude.False</code>
		 *  </li>
		 *  <li>
		 *   <code>makeTransform Cal.Core.Prelude.not 'a'  == 'a'</code>
		 *  </li>
		 * </ul>
		 * 
		 * @param f (CAL type: <code>Cal.Core.Prelude.Typeable a => a -> a</code>)
		 *          the function to be applied if its argument type matches that of <code>x</code>.
		 * @param x (CAL type: <code>Cal.Core.Prelude.Typeable b => b</code>)
		 *          the argument to be supplied to <code>f</code>, if its type matches <code>f</code>'s argument type.
		 * @return (CAL type: <code>Cal.Core.Prelude.Typeable b => b</code>) 
		 *          <code>(f x)</code> if <code>f</code>'s argument type is the same as <code>x</code>'s type, or <code>x</code> otherwise.
		 */
		public static final SourceModel.Expr makeTransform(SourceModel.Expr f, SourceModel.Expr x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeTransform), f, x});
		}

		/**
		 * Name binding for function: makeTransform.
		 * @see #makeTransform(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeTransform = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "makeTransform");

		/**
		 * Given the two arguments, returns the value that is the greater of the two.
		 * In other words, the maximum of the two arguments is returned.
		 * @param x (CAL type: <code>Cal.Core.Prelude.Ord a => a</code>)
		 *          the first argument.
		 * @param y (CAL type: <code>Cal.Core.Prelude.Ord a => a</code>)
		 *          the second argument.
		 * @return (CAL type: <code>Cal.Core.Prelude.Ord a => a</code>) 
		 *          the maximum of <code>x</code> and <code>y</code>.
		 */
		public static final SourceModel.Expr max(SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.max), x, y});
		}

		/**
		 * Name binding for function: max.
		 * @see #max(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName max = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "max");

		/**
		 * 
		 * @return (CAL type: <code>Cal.Core.Prelude.Bounded a => a</code>) 
		 *          the maximum bound of the instance type.
		 */
		public static final SourceModel.Expr maxBound() {
			return SourceModel.Expr.Var.make(Functions.maxBound);
		}

		/**
		 * Name binding for function: maxBound.
		 * @see #maxBound()
		 */
		public static final QualifiedName maxBound = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "maxBound");

		/**
		 * Returns <code>Cal.Core.Prelude.Nothing</code> on <code>Cal.Core.Prelude.Nothing</code> or <code>Cal.Core.Prelude.Just (func maybeArg)</code> on <code>Cal.Core.Prelude.Just maybeArg</code>.
		 * @param func (CAL type: <code>a -> b</code>)
		 *          the function to be applied to a <code>Cal.Core.Prelude.Maybe</code> value.
		 * @param maybeArg (CAL type: <code>Cal.Core.Prelude.Maybe a</code>)
		 *          the <code>Cal.Core.Prelude.Maybe</code> value argument.
		 * @return (CAL type: <code>Cal.Core.Prelude.Maybe b</code>) 
		 *          <code>Cal.Core.Prelude.Nothing</code> on <code>Cal.Core.Prelude.Nothing</code> or <code>Cal.Core.Prelude.Just (func maybeArg)</code> on <code>Cal.Core.Prelude.Just maybeArg</code>.
		 */
		public static final SourceModel.Expr maybeApply(SourceModel.Expr func, SourceModel.Expr maybeArg) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.maybeApply), func, maybeArg});
		}

		/**
		 * Name binding for function: maybeApply.
		 * @see #maybeApply(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName maybeApply = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "maybeApply");

		/**
		 * Returns an empty list when given <code>Cal.Core.Prelude.Nothing</code> or a singleton list when not given <code>Cal.Core.Prelude.Nothing</code>.
		 * @param maybeValue (CAL type: <code>Cal.Core.Prelude.Maybe a</code>)
		 *          the <code>Cal.Core.Prelude.Maybe</code> value.
		 * @return (CAL type: <code>[a]</code>) 
		 *          an empty list if maybeValue is <code>Cal.Core.Prelude.Nothing</code>, or a singleton list containing the element out of a <code>Cal.Core.Prelude.Just</code>.
		 */
		public static final SourceModel.Expr maybeToList(SourceModel.Expr maybeValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.maybeToList), maybeValue});
		}

		/**
		 * Name binding for function: maybeToList.
		 * @see #maybeToList(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName maybeToList = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "maybeToList");

		/**
		 * Given the two arguments, returns the value that is the lesser of the two.
		 * In other words, the minimum of the two arguments is returned.
		 * @param x (CAL type: <code>Cal.Core.Prelude.Ord a => a</code>)
		 *          the first argument.
		 * @param y (CAL type: <code>Cal.Core.Prelude.Ord a => a</code>)
		 *          the second argument.
		 * @return (CAL type: <code>Cal.Core.Prelude.Ord a => a</code>) 
		 *          the minimum of <code>x</code> and <code>y</code>.
		 */
		public static final SourceModel.Expr min(SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.min), x, y});
		}

		/**
		 * Name binding for function: min.
		 * @see #min(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName min = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "min");

		/**
		 * 
		 * @return (CAL type: <code>Cal.Core.Prelude.Bounded a => a</code>) 
		 *          the minimum bound of the instance type.
		 */
		public static final SourceModel.Expr minBound() {
			return SourceModel.Expr.Var.make(Functions.minBound);
		}

		/**
		 * Name binding for function: minBound.
		 * @see #minBound()
		 */
		public static final QualifiedName minBound = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "minBound");

		/**
		 * The remainder that corresponds to <code>Cal.Core.Prelude.divide</code>.
		 * <p>
		 * e.g.
		 * <ul>
		 *  <li>
		 *   <code>mod (-24) 5 = -4</code>
		 *  </li>
		 *  <li>
		 *   <code>mod 24 (-5) = 4</code>
		 *  </li>
		 * </ul>
		 * <p>
		 * This is a property of <code>Cal.Core.Prelude.mod</code>: <code>(Cal.Core.Prelude.divide x y) * y + (mod x y) = x</code>
		 * 
		 * @param x (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the dividend.
		 * @param y (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the divisor.
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 *          the remainder that would have resulted from dividing <code>x</code> by <code>y</code>.
		 * @deprecated use <code>Cal.Core.Prelude.remainder</code> instead.
		 */
		public static final SourceModel.Expr mod(SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.mod), x, y});
		}

		/**
		 * @see #mod(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param x
		 * @param y
		 * @return the SourceModel.Expr representing an application of mod
		 */
		public static final SourceModel.Expr mod(int x, int y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.mod), SourceModel.Expr.makeIntValue(x), SourceModel.Expr.makeIntValue(y)});
		}

		/**
		 * Name binding for function: mod.
		 * @see #mod(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName mod = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "mod");

		/**
		 * Returns the product of two numbers.
		 * <p>
		 * The operator form of <code>Cal.Core.Prelude.multiply</code> is <code>*</code>.
		 * 
		 * @param x (CAL type: <code>Cal.Core.Prelude.Num a => a</code>)
		 *          the multiplier.
		 * @param y (CAL type: <code>Cal.Core.Prelude.Num a => a</code>)
		 *          the multiplicand.
		 * @return (CAL type: <code>Cal.Core.Prelude.Num a => a</code>) 
		 *          the product of <code>x</code> and <code>y</code>.
		 */
		public static final SourceModel.Expr multiply(SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.multiply), x, y});
		}

		/**
		 * Name binding for function: multiply.
		 * @see #multiply(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName multiply = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "multiply");

		/**
		 * Returns the number of type arguments. i.e. <code>nTypeArguments typeRep == Cal.Core.Prelude.length (Cal.Core.Prelude.typeArguments typeRep)</code>.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Core.Prelude.typeArguments, Cal.Core.Prelude.sameRootType
		 * </dl>
		 * 
		 * @param typeRep (CAL type: <code>Cal.Core.Prelude.TypeRep</code>)
		 *          runtime type representation of a value, typically obtained by calling <code>Cal.Core.Prelude.typeOf</code> on a value.
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 *          the number of type arguments
		 */
		public static final SourceModel.Expr nTypeArguments(SourceModel.Expr typeRep) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.nTypeArguments), typeRep});
		}

		/**
		 * Name binding for function: nTypeArguments.
		 * @see #nTypeArguments(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName nTypeArguments = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "nTypeArguments");

		/**
		 * Negates the given number.
		 * <p>
		 * The operator form of <code>Cal.Core.Prelude.negate</code> is unary <code>-</code>. This is the only unary operator in CAL.
		 * 
		 * @param x (CAL type: <code>Cal.Core.Prelude.Num a => a</code>)
		 *          the number to negate.
		 * @return (CAL type: <code>Cal.Core.Prelude.Num a => a</code>) 
		 *          the negation of <code>x</code>.
		 */
		public static final SourceModel.Expr negate(SourceModel.Expr x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.negate), x});
		}

		/**
		 * Name binding for function: negate.
		 * @see #negate(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName negate = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "negate");

		/**
		 * The special negative infinity value for a <code>Cal.Core.Prelude.Double</code>.
		 * @return (CAL type: <code>Cal.Core.Prelude.Double</code>) 
		 */
		public static final SourceModel.Expr negativeInfinity() {
			return SourceModel.Expr.Var.make(Functions.negativeInfinity);
		}

		/**
		 * Name binding for function: negativeInfinity.
		 * @see #negativeInfinity()
		 */
		public static final QualifiedName negativeInfinity = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "negativeInfinity");

		/**
		 * Returns the logical negation of the argument.
		 * @param x (CAL type: <code>Cal.Core.Prelude.Boolean</code>)
		 *          the <code>Cal.Core.Prelude.Boolean</code> value to negate.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> when the <code>x</code> is <code>Cal.Core.Prelude.False</code>; <code>Cal.Core.Prelude.False</code> when the <code>x</code> is <code>Cal.Core.Prelude.True</code>.
		 */
		public static final SourceModel.Expr not(SourceModel.Expr x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.not), x});
		}

		/**
		 * @see #not(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param x
		 * @return the SourceModel.Expr representing an application of not
		 */
		public static final SourceModel.Expr not(boolean x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.not), SourceModel.Expr.makeBooleanValue(x)});
		}

		/**
		 * Name binding for function: not.
		 * @see #not(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName not = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "not");

		/**
		 * The special not-a-number value for a <code>Cal.Core.Prelude.Double</code>.
		 * @return (CAL type: <code>Cal.Core.Prelude.Double</code>) 
		 */
		public static final SourceModel.Expr notANumber() {
			return SourceModel.Expr.Var.make(Functions.notANumber);
		}

		/**
		 * Name binding for function: notANumber.
		 * @see #notANumber()
		 */
		public static final QualifiedName notANumber = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "notANumber");

		/**
		 * Returns whether the two arguments not equal.
		 * <p>
		 * The operator form of <code>Cal.Core.Prelude.notEquals</code> is <code>!=</code>.
		 * 
		 * @param x (CAL type: <code>Cal.Core.Prelude.Eq a => a</code>)
		 *          the first argument to compare.
		 * @param y (CAL type: <code>Cal.Core.Prelude.Eq a => a</code>)
		 *          the second argument to compare.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if <code>x</code> and <code>y</code> are not equal; <code>Cal.Core.Prelude.False</code> otherwise.
		 */
		public static final SourceModel.Expr notEquals(SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.notEquals), x, y});
		}

		/**
		 * Name binding for function: notEquals.
		 * @see #notEquals(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName notEquals = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "notEquals");

		/**
		 * Returns the result of a logical OR operation on the two arguments.
		 * <p>
		 * <code>Cal.Core.Prelude.or</code> can also be used in its operator form (which is <code>||</code>).
		 * 
		 * @param a (CAL type: <code>Cal.Core.Prelude.Boolean</code>)
		 *          the first <code>Cal.Core.Prelude.Boolean</code> argument.
		 * @param b (CAL type: <code>Cal.Core.Prelude.Boolean</code>)
		 *          the second <code>Cal.Core.Prelude.Boolean</code> argument.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> when one or both arguments are <code>Cal.Core.Prelude.True</code>; <code>Cal.Core.Prelude.False</code> when both arguments are <code>Cal.Core.Prelude.False</code>
		 */
		public static final SourceModel.Expr or(SourceModel.Expr a, SourceModel.Expr b) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.or), a, b});
		}

		/**
		 * @see #or(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param a
		 * @param b
		 * @return the SourceModel.Expr representing an application of or
		 */
		public static final SourceModel.Expr or(boolean a, boolean b) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.or), SourceModel.Expr.makeBooleanValue(a), SourceModel.Expr.makeBooleanValue(b)});
		}

		/**
		 * Name binding for function: or.
		 * @see #or(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName or = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "or");

		/**
		 * Converts an <code>Cal.Core.Prelude.Ordering</code> value to an <code>Cal.Core.Prelude.Int</code> value. In particular, <code>Cal.Core.Prelude.LT</code> is mapped to -1, <code>Cal.Core.Prelude.EQ</code> to 0,
		 * and <code>Cal.Core.Prelude.GT</code> to 1.
		 * @param orderingValue (CAL type: <code>Cal.Core.Prelude.Ordering</code>)
		 *          the <code>Cal.Core.Prelude.Ordering</code> value to be converted.
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 *          -1, 0, or 1 if the <code>Cal.Core.Prelude.Ordering</code> value is respectively <code>Cal.Core.Prelude.LT</code>, <code>Cal.Core.Prelude.EQ</code>, or <code>Cal.Core.Prelude.GT</code>.
		 */
		public static final SourceModel.Expr orderingToInt(SourceModel.Expr orderingValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.orderingToInt), orderingValue});
		}

		/**
		 * Name binding for function: orderingToInt.
		 * @see #orderingToInt(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName orderingToInt = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "orderingToInt");

		/**
		 * Converts a value of the instance type into a Java value.
		 * @param value (CAL type: <code>Cal.Core.Prelude.Outputable a => a</code>)
		 *          the value to be converted.
		 * @return (CAL type: <code>Cal.Core.Prelude.JObject</code>) 
		 *          the representation of value as a JObject.
		 */
		public static final SourceModel.Expr output(SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.output), value});
		}

		/**
		 * Name binding for function: output.
		 * @see #output(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName output = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "output");

		/**
		 * When <code>Cal.Core.Prelude.outputCalValueStrict calValue</code> is evaluated, the argument <code>calValue</code> is first evaluated to weak-head normal form itself,
		 * and then converted to a Java object that is suitable for external Java clients to use as a handle to the <code>calValue</code>. 
		 * This function is sometimes needed when the external Java client wants to control the order of side effects in a series of CAL computations.
		 * <p>
		 * If you do not want to evaluate the <code>calValue</code> argument to weak-head normal form, use the <code>Cal.Core.Prelude.output</code> function instead.
		 * 
		 * @param calValue (CAL type: <code>Cal.Core.Prelude.CalValue</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.JObject</code>) 
		 *          a Java object that is suitable for external clients to use as a handle to the <code>calValue</code>.
		 */
		public static final SourceModel.Expr outputCalValueStrict(SourceModel.Expr calValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.outputCalValueStrict), calValue});
		}

		/**
		 * Name binding for function: outputCalValueStrict.
		 * @see #outputCalValueStrict(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName outputCalValueStrict = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "outputCalValueStrict");

		/**
		 * Creates a pair (2-tuple).
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Core.Prelude.tuple2, Cal.Core.Prelude.strictTuple2
		 * </dl>
		 * 
		 * @param x (CAL type: <code>a</code>)
		 *          the 1st component of the pair.
		 * @param y (CAL type: <code>b</code>)
		 *          the 2nd component of the pair.
		 * @return (CAL type: <code>(a, b)</code>) 
		 *          the pair <code>(x, y)</code>.
		 */
		public static final SourceModel.Expr pair(SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.pair), x, y});
		}

		/**
		 * Name binding for function: pair.
		 * @see #pair(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName pair = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "pair");

		/**
		 * The special positive infinity value for a <code>Cal.Core.Prelude.Double</code>.
		 * @return (CAL type: <code>Cal.Core.Prelude.Double</code>) 
		 */
		public static final SourceModel.Expr positiveInfinity() {
			return SourceModel.Expr.Var.make(Functions.positiveInfinity);
		}

		/**
		 * Name binding for function: positiveInfinity.
		 * @see #positiveInfinity()
		 */
		public static final QualifiedName positiveInfinity = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "positiveInfinity");

		/**
		 * Returns the result of raising the first argument to the power specified by the second argument.
		 * @param base (CAL type: <code>Cal.Core.Prelude.Integer</code>)
		 *          the <code>Cal.Core.Prelude.Integer</code> value whose power is to be taken.
		 * @param exponent (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          an <code>Cal.Core.Prelude.Int</code> value specifying the exponent in the exponentiation.
		 * @return (CAL type: <code>Cal.Core.Prelude.Integer</code>) 
		 *          <code>base</code><sup><code>exponent</code></sup>.
		 */
		public static final SourceModel.Expr powerInteger(SourceModel.Expr base, SourceModel.Expr exponent) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.powerInteger), base, exponent});
		}

		/**
		 * @see #powerInteger(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param base
		 * @param exponent
		 * @return the SourceModel.Expr representing an application of powerInteger
		 */
		public static final SourceModel.Expr powerInteger(SourceModel.Expr base, int exponent) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.powerInteger), base, SourceModel.Expr.makeIntValue(exponent)});
		}

		/**
		 * Name binding for function: powerInteger.
		 * @see #powerInteger(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName powerInteger = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "powerInteger");

		/**
		 * Returns the remainder from division.
		 * <p>
		 * The operator form of <code>Cal.Core.Prelude.remainder</code> is <code>%</code>.
		 * <p>
		 * For integral types, the identity: <code>x == (x / y) * y + (x % y)</code> must hold for all x and y.
		 * For non-integral types, the identity: <code>x == (truncate (x / y)) * y + (x % y)</code> must hold in general.
		 * 
		 * @param x (CAL type: <code>Cal.Core.Prelude.Num a => a</code>)
		 *          the dividend.
		 * @param y (CAL type: <code>Cal.Core.Prelude.Num a => a</code>)
		 *          the divisor.
		 * @return (CAL type: <code>Cal.Core.Prelude.Num a => a</code>) 
		 *          the remainder.
		 */
		public static final SourceModel.Expr remainder(SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.remainder), x, y});
		}

		/**
		 * Name binding for function: remainder.
		 * @see #remainder(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName remainder = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "remainder");

		/**
		 * Reverses the order of a comparator (also known as a comparison function) so the
		 * comparisons that result in <code>Cal.Core.Prelude.LT</code> become <code>Cal.Core.Prelude.GT</code> and those that result in <code>Cal.Core.Prelude.GT</code> become <code>Cal.Core.Prelude.LT</code>.
		 * @param comparator (CAL type: <code>a -> a -> Cal.Core.Prelude.Ordering</code>)
		 *          the comparison function to be reversed.
		 * @param x (CAL type: <code>a</code>)
		 *          the first argument to the reversed comparator.
		 * @param y (CAL type: <code>a</code>)
		 *          the second argument to the reversed comparator.
		 * @return (CAL type: <code>Cal.Core.Prelude.Ordering</code>) 
		 *          the result of applying the reversed comparison function.
		 */
		public static final SourceModel.Expr reverseComparator(SourceModel.Expr comparator, SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.reverseComparator), comparator, x, y});
		}

		/**
		 * Name binding for function: reverseComparator.
		 * @see #reverseComparator(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName reverseComparator = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "reverseComparator");

		/**
		 * Rounds a number to the nearest integer. In border cases, round towards 0 for even integral parts and away from 0 for odd.
		 * This is the same behavior as in Visual Basic, but differs from Crystal Reports' round function in border cases.
		 * <p>
		 * e.g.
		 * <ul>
		 *  <li>
		 *   <code>round 2.3 = 2</code>
		 *  </li>
		 *  <li>
		 *   <code>round 2.6 = 3</code>
		 *  </li>
		 *  <li>
		 *   <code>round 2.5 = 2</code>
		 *  </li>
		 *  <li>
		 *   <code>round 1.5 = 2</code>
		 *  </li>
		 *  <li>
		 *   <code>round (-2.6) = -3</code>
		 *  </li>
		 *  <li>
		 *   <code>round (-2.5) = -2</code>
		 *  </li>
		 * </ul>
		 * 
		 * @param x (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 *          the <code>Cal.Core.Prelude.Double</code> value to be rounded.
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 *          the <code>Cal.Core.Prelude.Int</code> value nearest <code>x</code>.
		 */
		public static final SourceModel.Expr round(SourceModel.Expr x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.round), x});
		}

		/**
		 * @see #round(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param x
		 * @return the SourceModel.Expr representing an application of round
		 */
		public static final SourceModel.Expr round(double x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.round), SourceModel.Expr.makeDoubleValue(x)});
		}

		/**
		 * Name binding for function: round.
		 * @see #round(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName round = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "round");

		/**
		 * Returns <code>Cal.Core.Prelude.True</code> if the runtime type representation of the root types of the 2 arguments are the same.
		 * <p>
		 * For example, the root type of <code>Cal.Core.Prelude.Int</code>, <code>Maybe String</code>, <code>[Int]</code> and <code>Int -&gt; Char</code> are
		 * <code>Cal.Core.Prelude.Int</code>, <code>Cal.Core.Prelude.Maybe</code>, <code>Cal.Core.Prelude.List</code> and <code>Cal.Core.Prelude.Function</code> respectively.
		 * <p>
		 * For record type, <code>sameRootType</code> returns true if both <code>typeRep1</code> and <code>typeRep2</code> are record types and they
		 * have the exact same fields. 
		 * 
		 * @param typeRep1 (CAL type: <code>Cal.Core.Prelude.TypeRep</code>)
		 *          runtime type representation of a value, typically obtained by calling <code>Cal.Core.Prelude.typeOf</code> on a value.
		 * @param typeRep2 (CAL type: <code>Cal.Core.Prelude.TypeRep</code>)
		 *          runtime type representation of a value, typically obtained by calling <code>Cal.Core.Prelude.typeOf</code> on a value.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the runtime type representation of <code>typeRep1</code> and <code>typeRep2</code> have the same root types
		 * and <code>Cal.Core.Prelude.False</code> otherwise.
		 */
		public static final SourceModel.Expr sameRootType(SourceModel.Expr typeRep1, SourceModel.Expr typeRep2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.sameRootType), typeRep1, typeRep2});
		}

		/**
		 * Name binding for function: sameRootType.
		 * @see #sameRootType(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName sameRootType = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "sameRootType");

		/**
		 * <code>Cal.Core.Prelude.seq</code> is a primitive function used to explicitly control the order of evaluation of CAL.
		 * Its primary application is to limit lazy evaluation in cases where it would result in excess space usage.
		 * <code>Cal.Core.Prelude.seq</code> evaluates its first argument until WHNF (weak head normal form) is reached,
		 * and then returns its second argument.
		 * <p>
		 * "evaluate to weak head normal form" means to evaluate an expression until you get a primitive value (such as an
		 * <code>Cal.Core.Prelude.Int</code> or <code>Cal.Core.Prelude.String</code>), a function, or a data constructor. In particular, it does not evaluate the arguments of
		 * the resulting data constructor. <code>Cal.Core.Prelude.seq</code> is a very fine-grained way of controlling strictness.
		 * <p>
		 * For example,
		 * <ul>
		 *  <li>
		 *   <code>seq (Cal.Core.Prelude.error "error should be called") 2.0            //will call the error function</code>
		 *  </li>
		 *  <li>
		 *   <code>seq (Cal.Core.Prelude.id (Cal.Core.Prelude.error "error should be called")) 2.0      //will call the error function</code>
		 *  </li>
		 *  <li>
		 *   <code>seq (Cal.Core.Prelude.Just (Cal.Core.Prelude.error "error should not be called")) 2.0 //will return 2.0, and not call error. We already have the data constructor Just.</code>
		 *  </li>
		 *  <li>
		 *   <code>seq [Cal.Core.Prelude.error "error should not be called"] 2.0        //will return 2.0, and not call error. We have the data constructor Cons.</code>
		 *  </li>
		 * </ul>
		 * <p>
		 * Note that plinging of arguments is defined in terms of <code>Cal.Core.Prelude.seq</code>. For example, 
		 * <pre> f !x y !z = someExpression;
		 * </pre>
		 *  means the same thing as: 
		 * <pre> f x y z = x `seq` z `seq` someExpression;
		 * </pre>
		 *  i.e. 
		 * <pre> f x y z = seq x (seq z someExpression);
		 * </pre>
		 * 
		 * 
		 * @param x (CAL type: <code>a</code>)
		 *          argument to be evaluated to WHNF prior to evaluating argument <code>y</code>.
		 * @param y (CAL type: <code>b</code>)
		 *          value to be returned after argument <code>x</code> is evaluated to WHNF.
		 * @return (CAL type: <code>b</code>) 
		 *          the argument <code>y</code>.
		 */
		public static final SourceModel.Expr seq(SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.seq), x, y});
		}

		/**
		 * Name binding for function: seq.
		 * @see #seq(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName seq = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "seq");

		/**
		 * Returns the sign of the given number.
		 * <p>
		 * The functions <code>Cal.Core.Prelude.abs</code> and <code>Cal.Core.Prelude.signum</code> should satisfy:
		 * <code>(abs x * signum x) == x</code>
		 * 
		 * @param x (CAL type: <code>Cal.Core.Prelude.Num a => a</code>)
		 *          the number whose sign is requested.
		 * @return (CAL type: <code>Cal.Core.Prelude.Num a => a</code>) 
		 *          one of: -1 (if the number is negative), 0 (if the number is zero), or 1 (if the number is positive).
		 */
		public static final SourceModel.Expr signum(SourceModel.Expr x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.signum), x});
		}

		/**
		 * Name binding for function: signum.
		 * @see #signum(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName signum = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "signum");

		/**
		 * Extracts the second component of a pair.
		 * @param pair (CAL type: <code>(a, b)</code>)
		 *          the pair.
		 * @return (CAL type: <code>b</code>) 
		 *          the second component of the pair.
		 */
		public static final SourceModel.Expr snd(SourceModel.Expr pair) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.snd), pair});
		}

		/**
		 * Name binding for function: snd.
		 * @see #snd(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName snd = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "snd");

		/**
		 * Strict function application. The argument <code>x</code> of <code>f</code> is reduced to WHNF (weak head normal form) before
		 * <code>f</code> is reduced.
		 * @param f (CAL type: <code>a -> b</code>)
		 *          the function to be applied.
		 * @param x (CAL type: <code>a</code>)
		 *          the argument to <code>f</code>, to be reduced to WHNF (weak head normal form) before <code>f</code> is reduced.
		 * @return (CAL type: <code>b</code>) 
		 *          the result of evaluating <code>(f x)</code>.
		 */
		public static final SourceModel.Expr strict(SourceModel.Expr f, SourceModel.Expr x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.strict), f, x});
		}

		/**
		 * Name binding for function: strict.
		 * @see #strict(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName strict = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "strict");

		/**
		 * Creates a 2-tuple value, but ensures that the fields++ are evaluated (to weak-head normal form) prior to returning.
		 * @param x1 (CAL type: <code>a</code>)
		 * @param x2 (CAL type: <code>b</code>)
		 * @return (CAL type: <code>(a, b)</code>) 
		 */
		public static final SourceModel.Expr strictTuple2(SourceModel.Expr x1, SourceModel.Expr x2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.strictTuple2), x1, x2});
		}

		/**
		 * Name binding for function: strictTuple2.
		 * @see #strictTuple2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName strictTuple2 = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "strictTuple2");

		/**
		 * Creates a 3-tuple value, but ensures that the fields are evaluated (to weak-head normal form) prior to returning.
		 * @param x1 (CAL type: <code>a</code>)
		 * @param x2 (CAL type: <code>b</code>)
		 * @param x3 (CAL type: <code>c</code>)
		 * @return (CAL type: <code>(a, b, c)</code>) 
		 */
		public static final SourceModel.Expr strictTuple3(SourceModel.Expr x1, SourceModel.Expr x2, SourceModel.Expr x3) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.strictTuple3), x1, x2, x3});
		}

		/**
		 * Name binding for function: strictTuple3.
		 * @see #strictTuple3(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName strictTuple3 = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "strictTuple3");

		/**
		 * Creates a 4-tuple value, but ensures that the fields are evaluated (to weak-head normal form) prior to returning.
		 * @param x1 (CAL type: <code>a</code>)
		 * @param x2 (CAL type: <code>b</code>)
		 * @param x3 (CAL type: <code>c</code>)
		 * @param x4 (CAL type: <code>d</code>)
		 * @return (CAL type: <code>(a, b, c, d)</code>) 
		 */
		public static final SourceModel.Expr strictTuple4(SourceModel.Expr x1, SourceModel.Expr x2, SourceModel.Expr x3, SourceModel.Expr x4) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.strictTuple4), x1, x2, x3, x4});
		}

		/**
		 * Name binding for function: strictTuple4.
		 * @see #strictTuple4(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName strictTuple4 = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "strictTuple4");

		/**
		 * Creates a 5-tuple value, but ensures that the fields are evaluated (to weak-head normal form) prior to returning.
		 * @param x1 (CAL type: <code>a</code>)
		 * @param x2 (CAL type: <code>b</code>)
		 * @param x3 (CAL type: <code>c</code>)
		 * @param x4 (CAL type: <code>d</code>)
		 * @param x5 (CAL type: <code>e</code>)
		 * @return (CAL type: <code>(a, b, c, d, e)</code>) 
		 */
		public static final SourceModel.Expr strictTuple5(SourceModel.Expr x1, SourceModel.Expr x2, SourceModel.Expr x3, SourceModel.Expr x4, SourceModel.Expr x5) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.strictTuple5), x1, x2, x3, x4, x5});
		}

		/**
		 * Name binding for function: strictTuple5.
		 * @see #strictTuple5(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName strictTuple5 = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "strictTuple5");

		/**
		 * Creates a 6-tuple value, but ensures that the fields are evaluated (to weak-head normal form) prior to returning.
		 * @param x1 (CAL type: <code>a</code>)
		 * @param x2 (CAL type: <code>b</code>)
		 * @param x3 (CAL type: <code>c</code>)
		 * @param x4 (CAL type: <code>d</code>)
		 * @param x5 (CAL type: <code>e</code>)
		 * @param x6 (CAL type: <code>f</code>)
		 * @return (CAL type: <code>(a, b, c, d, e, f)</code>) 
		 */
		public static final SourceModel.Expr strictTuple6(SourceModel.Expr x1, SourceModel.Expr x2, SourceModel.Expr x3, SourceModel.Expr x4, SourceModel.Expr x5, SourceModel.Expr x6) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.strictTuple6), x1, x2, x3, x4, x5, x6});
		}

		/**
		 * Name binding for function: strictTuple6.
		 * @see #strictTuple6(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName strictTuple6 = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "strictTuple6");

		/**
		 * Creates a 7-tuple value, but ensures that the fields are evaluated (to weak-head normal form) prior to returning.
		 * @param x1 (CAL type: <code>a</code>)
		 * @param x2 (CAL type: <code>b</code>)
		 * @param x3 (CAL type: <code>c</code>)
		 * @param x4 (CAL type: <code>d</code>)
		 * @param x5 (CAL type: <code>e</code>)
		 * @param x6 (CAL type: <code>f</code>)
		 * @param x7 (CAL type: <code>g</code>)
		 * @return (CAL type: <code>(a, b, c, d, e, f, g)</code>) 
		 */
		public static final SourceModel.Expr strictTuple7(SourceModel.Expr x1, SourceModel.Expr x2, SourceModel.Expr x3, SourceModel.Expr x4, SourceModel.Expr x5, SourceModel.Expr x6, SourceModel.Expr x7) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.strictTuple7), x1, x2, x3, x4, x5, x6, x7});
		}

		/**
		 * Name binding for function: strictTuple7.
		 * @see #strictTuple7(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName strictTuple7 = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "strictTuple7");

		/**
		 * Parses a string (eg. <code>"34.33"</code>, <code>"1.0e50000"</code>) into a <code>Cal.Core.Prelude.Decimal</code>.
		 * An error will be signalled for invalid strings.
		 * @param stringValue (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          string to parse into a <code>Cal.Core.Prelude.Decimal</code>
		 * @return (CAL type: <code>Cal.Core.Prelude.Decimal</code>) 
		 *          <code>Cal.Core.Prelude.Decimal</code> value represented by <code>stringValue</code>
		 */
		public static final SourceModel.Expr stringToDecimal(SourceModel.Expr stringValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.stringToDecimal), stringValue});
		}

		/**
		 * @see #stringToDecimal(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param stringValue
		 * @return the SourceModel.Expr representing an application of stringToDecimal
		 */
		public static final SourceModel.Expr stringToDecimal(java.lang.String stringValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.stringToDecimal), SourceModel.Expr.makeStringValue(stringValue)});
		}

		/**
		 * Name binding for function: stringToDecimal.
		 * @see #stringToDecimal(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName stringToDecimal = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "stringToDecimal");

		/**
		 * Parses a string representation of a double value into a <code>Cal.Core.Prelude.Double</code> value.
		 * @param stringRep (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the string representation of a double value.
		 * @return (CAL type: <code>Cal.Core.Prelude.Double</code>) 
		 *          the corresponding <code>Cal.Core.Prelude.Double</code> value.
		 */
		public static final SourceModel.Expr stringToDouble(SourceModel.Expr stringRep) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.stringToDouble), stringRep});
		}

		/**
		 * @see #stringToDouble(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param stringRep
		 * @return the SourceModel.Expr representing an application of stringToDouble
		 */
		public static final SourceModel.Expr stringToDouble(java.lang.String stringRep) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.stringToDouble), SourceModel.Expr.makeStringValue(stringRep)});
		}

		/**
		 * Name binding for function: stringToDouble.
		 * @see #stringToDouble(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName stringToDouble = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "stringToDouble");

		/**
		 * Parses a string representation of an integer value into an <code>Cal.Core.Prelude.Int</code> value.
		 * <p>
		 * The characters in the string must all be decimal digits, except that the
		 * first character may be an ASCII minus sign '-' to indicate a negative value.
		 * 
		 * @param stringRep (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the string representation of an integer value.
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 *          the corresponding <code>Cal.Core.Prelude.Int</code> value.
		 */
		public static final SourceModel.Expr stringToInt(SourceModel.Expr stringRep) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.stringToInt), stringRep});
		}

		/**
		 * @see #stringToInt(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param stringRep
		 * @return the SourceModel.Expr representing an application of stringToInt
		 */
		public static final SourceModel.Expr stringToInt(java.lang.String stringRep) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.stringToInt), SourceModel.Expr.makeStringValue(stringRep)});
		}

		/**
		 * Name binding for function: stringToInt.
		 * @see #stringToInt(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName stringToInt = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "stringToInt");

		/**
		 * Parses a string representation of an integer value in the specified radix
		 * into an <code>Cal.Core.Prelude.Int</code> value.
		 * <p>
		 * The characters in the string must all be digits of the specified radix,
		 * except that the first character may be an ASCII minus sign '-' to indicate a
		 * negative value.
		 * 
		 * @param stringRep (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the string representation of an integer value in the specified radix.
		 * @param radix (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the radix to be used while parsing <code>stringRep</code>.
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 *          the corresponding <code>Cal.Core.Prelude.Int</code> value.
		 */
		public static final SourceModel.Expr stringToIntRadix(SourceModel.Expr stringRep, SourceModel.Expr radix) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.stringToIntRadix), stringRep, radix});
		}

		/**
		 * @see #stringToIntRadix(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param stringRep
		 * @param radix
		 * @return the SourceModel.Expr representing an application of stringToIntRadix
		 */
		public static final SourceModel.Expr stringToIntRadix(java.lang.String stringRep, int radix) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.stringToIntRadix), SourceModel.Expr.makeStringValue(stringRep), SourceModel.Expr.makeIntValue(radix)});
		}

		/**
		 * Name binding for function: stringToIntRadix.
		 * @see #stringToIntRadix(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName stringToIntRadix = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "stringToIntRadix");

		/**
		 * Parses a string representation of an integer into an <code>Cal.Core.Prelude.Integer</code> value.
		 * @param stringRep (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the string representation of an integer.
		 * @return (CAL type: <code>Cal.Core.Prelude.Integer</code>) 
		 *          the corresponding <code>Cal.Core.Prelude.Integer</code> value.
		 */
		public static final SourceModel.Expr stringToInteger(SourceModel.Expr stringRep) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.stringToInteger), stringRep});
		}

		/**
		 * @see #stringToInteger(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param stringRep
		 * @return the SourceModel.Expr representing an application of stringToInteger
		 */
		public static final SourceModel.Expr stringToInteger(java.lang.String stringRep) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.stringToInteger), SourceModel.Expr.makeStringValue(stringRep)});
		}

		/**
		 * Name binding for function: stringToInteger.
		 * @see #stringToInteger(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName stringToInteger = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "stringToInteger");

		/**
		 * Parses a string representation of a long integer value into a <code>Cal.Core.Prelude.Long</code> value.
		 * <p>
		 * The characters in the string must all be decimal digits, except that the
		 * first character may be an ASCII minus sign '-' to indicate a negative value.
		 * 
		 * @param stringRep (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the string representation of a long integer value.
		 * @return (CAL type: <code>Cal.Core.Prelude.Long</code>) 
		 *          the corresponding <code>Cal.Core.Prelude.Long</code> value.
		 */
		public static final SourceModel.Expr stringToLong(SourceModel.Expr stringRep) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.stringToLong), stringRep});
		}

		/**
		 * @see #stringToLong(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param stringRep
		 * @return the SourceModel.Expr representing an application of stringToLong
		 */
		public static final SourceModel.Expr stringToLong(java.lang.String stringRep) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.stringToLong), SourceModel.Expr.makeStringValue(stringRep)});
		}

		/**
		 * Name binding for function: stringToLong.
		 * @see #stringToLong(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName stringToLong = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "stringToLong");

		/**
		 * Parses a string representation of a long integer value in the specified radix
		 * into a <code>Cal.Core.Prelude.Long</code> value.
		 * <p>
		 * The characters in the string must all be digits of the specified radix,
		 * except that the first character may be an ASCII minus sign '-' to indicate a
		 * negative value.
		 * 
		 * @param stringRep (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the string representation of a long integer value in the specified radix.
		 * @param radix (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 *          the radix to be used while parsing <code>stringRep</code>.
		 * @return (CAL type: <code>Cal.Core.Prelude.Long</code>) 
		 *          the corresponding <code>Cal.Core.Prelude.Long</code> value.
		 */
		public static final SourceModel.Expr stringToLongRadix(SourceModel.Expr stringRep, SourceModel.Expr radix) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.stringToLongRadix), stringRep, radix});
		}

		/**
		 * @see #stringToLongRadix(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param stringRep
		 * @param radix
		 * @return the SourceModel.Expr representing an application of stringToLongRadix
		 */
		public static final SourceModel.Expr stringToLongRadix(java.lang.String stringRep, int radix) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.stringToLongRadix), SourceModel.Expr.makeStringValue(stringRep), SourceModel.Expr.makeIntValue(radix)});
		}

		/**
		 * Name binding for function: stringToLongRadix.
		 * @see #stringToLongRadix(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName stringToLongRadix = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "stringToLongRadix");

		/**
		 * Returns the result of subtracting the second number from the first number.
		 * <p>
		 * The operator form of <code>Cal.Core.Prelude.subtract</code> is binary <code>-</code>.
		 * 
		 * @param x (CAL type: <code>Cal.Core.Prelude.Num a => a</code>)
		 *          the minuend.
		 * @param y (CAL type: <code>Cal.Core.Prelude.Num a => a</code>)
		 *          the subtrahend.
		 * @return (CAL type: <code>Cal.Core.Prelude.Num a => a</code>) 
		 *          the difference.
		 */
		public static final SourceModel.Expr subtract(SourceModel.Expr x, SourceModel.Expr y) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.subtract), x, y});
		}

		/**
		 * Name binding for function: subtract.
		 * @see #subtract(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName subtract = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "subtract");

		/**
		 * Tests all the examples in the Prelude module.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 */
		public static final SourceModel.Expr testPreludeModule() {
			return SourceModel.Expr.Var.make(Functions.testPreludeModule);
		}

		/**
		 * Name binding for function: testPreludeModule.
		 * @see #testPreludeModule()
		 */
		public static final QualifiedName testPreludeModule = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "testPreludeModule");

		/**
		 * Converts a value of the instance type to a <code>Cal.Core.Prelude.Byte</code> value.
		 * Conversions from <code>Cal.Core.Prelude.Byte</code>, <code>Cal.Core.Prelude.Short</code>, <code>Cal.Core.Prelude.Int</code>, <code>Cal.Core.Prelude.Long</code>, <code>Cal.Core.Prelude.Float</code> and <code>Cal.Core.Prelude.Double</code>
		 * behave like the corresponding Java primitive cast operator.
		 * @param number (CAL type: <code>Cal.Core.Prelude.Num a => a</code>)
		 *          the number to convert.
		 * @return (CAL type: <code>Cal.Core.Prelude.Byte</code>) 
		 *          a <code>Cal.Core.Prelude.Byte</code> value which correspond to the given number.
		 */
		public static final SourceModel.Expr toByte(SourceModel.Expr number) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.toByte), number});
		}

		/**
		 * Name binding for function: toByte.
		 * @see #toByte(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName toByte = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "toByte");

		/**
		 * Converts any value in CAL to a value of type <code>Cal.Core.Prelude.CalValue</code>. This function will always succeed, and is perfectly safe,
		 * since any value in CAL can also be viewed as a <code>Cal.Core.Prelude.CalValue</code>. In effect, this is a way of performing type erasure on CAL types;
		 * the underlying value is not modified.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Core.Prelude.unsafeFromCalValue
		 * </dl>
		 * 
		 * @param x (CAL type: <code>a</code>)
		 *          a value
		 * @return (CAL type: <code>Cal.Core.Prelude.CalValue</code>) 
		 *          the value <code>x</code>, viewed as having type <code>Cal.Core.Prelude.CalValue</code>. The underlying value is not modified.
		 */
		public static final SourceModel.Expr toCalValue(SourceModel.Expr x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.toCalValue), x});
		}

		/**
		 * Name binding for function: toCalValue.
		 * @see #toCalValue(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName toCalValue = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "toCalValue");

		/**
		 * Converts a value of the instance type to a <code>Cal.Core.Prelude.Decimal</code> value.
		 * @param number (CAL type: <code>Cal.Core.Prelude.Num a => a</code>)
		 *          the number to convert.
		 * @return (CAL type: <code>Cal.Core.Prelude.Decimal</code>) 
		 *          a <code>Cal.Core.Prelude.Decimal</code> value which correspond to the given number.
		 */
		public static final SourceModel.Expr toDecimal(SourceModel.Expr number) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.toDecimal), number});
		}

		/**
		 * Name binding for function: toDecimal.
		 * @see #toDecimal(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName toDecimal = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "toDecimal");

		/**
		 * Converts a value of the instance type to a <code>Cal.Core.Prelude.Double</code> value.
		 * Conversions from <code>Cal.Core.Prelude.Byte</code>, <code>Cal.Core.Prelude.Short</code>, <code>Cal.Core.Prelude.Int</code>, <code>Cal.Core.Prelude.Long</code>, <code>Cal.Core.Prelude.Float</code> and <code>Cal.Core.Prelude.Double</code>
		 * behave like the corresponding Java primitive cast operator.
		 * @param number (CAL type: <code>Cal.Core.Prelude.Num a => a</code>)
		 *          the number to convert.
		 * @return (CAL type: <code>Cal.Core.Prelude.Double</code>) 
		 *          a <code>Cal.Core.Prelude.Double</code> value which correspond to the given number.
		 */
		public static final SourceModel.Expr toDouble(SourceModel.Expr number) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.toDouble), number});
		}

		/**
		 * Name binding for function: toDouble.
		 * @see #toDouble(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName toDouble = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "toDouble");

		/**
		 * Converts a value of the instance type to a <code>Cal.Core.Prelude.Float</code> value.
		 * Conversions from <code>Cal.Core.Prelude.Byte</code>, <code>Cal.Core.Prelude.Short</code>, <code>Cal.Core.Prelude.Int</code>, <code>Cal.Core.Prelude.Long</code>, <code>Cal.Core.Prelude.Float</code> and <code>Cal.Core.Prelude.Double</code>
		 * behave like the corresponding Java primitive cast operator.
		 * @param number (CAL type: <code>Cal.Core.Prelude.Num a => a</code>)
		 *          the number to convert.
		 * @return (CAL type: <code>Cal.Core.Prelude.Float</code>) 
		 *          a <code>Cal.Core.Prelude.Float</code> value which correspond to the given number.
		 */
		public static final SourceModel.Expr toFloat(SourceModel.Expr number) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.toFloat), number});
		}

		/**
		 * Name binding for function: toFloat.
		 * @see #toFloat(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName toFloat = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "toFloat");

		/**
		 * Converts a value of the instance type to an <code>Cal.Core.Prelude.Int</code> value.
		 * Conversions from <code>Cal.Core.Prelude.Byte</code>, <code>Cal.Core.Prelude.Short</code>, <code>Cal.Core.Prelude.Int</code>, <code>Cal.Core.Prelude.Long</code>, <code>Cal.Core.Prelude.Float</code> and <code>Cal.Core.Prelude.Double</code>
		 * behave like the corresponding Java primitive cast operator.
		 * @param number (CAL type: <code>Cal.Core.Prelude.Num a => a</code>)
		 *          the number to convert.
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 *          an <code>Cal.Core.Prelude.Int</code> value which correspond to the given number.
		 */
		public static final SourceModel.Expr toInt(SourceModel.Expr number) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.toInt), number});
		}

		/**
		 * Name binding for function: toInt.
		 * @see #toInt(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName toInt = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "toInt");

		/**
		 * Converts a value of the instance type to an <code>Cal.Core.Prelude.Integer</code> value.
		 * @param number (CAL type: <code>Cal.Core.Prelude.Num a => a</code>)
		 *          the number to convert.
		 * @return (CAL type: <code>Cal.Core.Prelude.Integer</code>) 
		 *          an <code>Cal.Core.Prelude.Integer</code> value which correspond to the given number.
		 */
		public static final SourceModel.Expr toInteger(SourceModel.Expr number) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.toInteger), number});
		}

		/**
		 * Name binding for function: toInteger.
		 * @see #toInteger(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName toInteger = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "toInteger");

		/**
		 * Converts a value of the instance type to a <code>Cal.Core.Prelude.Long</code> value.
		 * Conversions from <code>Cal.Core.Prelude.Byte</code>, <code>Cal.Core.Prelude.Short</code>, <code>Cal.Core.Prelude.Int</code>, <code>Cal.Core.Prelude.Long</code>, <code>Cal.Core.Prelude.Float</code> and <code>Cal.Core.Prelude.Double</code>
		 * behave like the corresponding Java primitive cast operator.
		 * @param number (CAL type: <code>Cal.Core.Prelude.Num a => a</code>)
		 *          the number to convert.
		 * @return (CAL type: <code>Cal.Core.Prelude.Long</code>) 
		 *          a <code>Cal.Core.Prelude.Long</code> value which correspond to the given number.
		 */
		public static final SourceModel.Expr toLong(SourceModel.Expr number) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.toLong), number});
		}

		/**
		 * Name binding for function: toLong.
		 * @see #toLong(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName toLong = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "toLong");

		/**
		 * Converts a value of the instance type to a <code>Cal.Core.Prelude.Short</code> value.
		 * Conversions from <code>Cal.Core.Prelude.Byte</code>, <code>Cal.Core.Prelude.Short</code>, <code>Cal.Core.Prelude.Int</code>, <code>Cal.Core.Prelude.Long</code>, <code>Cal.Core.Prelude.Float</code> and <code>Cal.Core.Prelude.Double</code>
		 * behave like the corresponding Java primitive cast operator.
		 * @param number (CAL type: <code>Cal.Core.Prelude.Num a => a</code>)
		 *          the number to convert.
		 * @return (CAL type: <code>Cal.Core.Prelude.Short</code>) 
		 *          a <code>Cal.Core.Prelude.Short</code> value which correspond to the given number.
		 */
		public static final SourceModel.Expr toShort(SourceModel.Expr number) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.toShort), number});
		}

		/**
		 * Name binding for function: toShort.
		 * @see #toShort(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName toShort = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "toShort");

		/**
		 * Creates a triple (3-tuple).
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Core.Prelude.tuple3, Cal.Core.Prelude.strictTuple3
		 * </dl>
		 * 
		 * @param x (CAL type: <code>a</code>)
		 *          the 1st component of the triple.
		 * @param y (CAL type: <code>b</code>)
		 *          the 2nd component of the triple.
		 * @param z (CAL type: <code>c</code>)
		 *          the 3rd component of the triple.
		 * @return (CAL type: <code>(a, b, c)</code>) 
		 *          the triple <code>(x, y, z)</code>.
		 */
		public static final SourceModel.Expr triple(SourceModel.Expr x, SourceModel.Expr y, SourceModel.Expr z) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.triple), x, y, z});
		}

		/**
		 * Name binding for function: triple.
		 * @see #triple(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName triple = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "triple");

		/**
		 * Truncates a number towards 0.
		 * <p>
		 * e.g.
		 * <ul>
		 *  <li>
		 *   <code>truncate 2.6 = 2</code>
		 *  </li>
		 *  <li>
		 *   <code>truncate (-2.6) = -2</code>
		 *  </li>
		 * </ul>
		 * 
		 * @param x (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 *          the <code>Cal.Core.Prelude.Double</code> value to be truncated.
		 * @return (CAL type: <code>Cal.Core.Prelude.Int</code>) 
		 *          the <code>Cal.Core.Prelude.Int</code> value nearest <code>x</code> between 0 and <code>x</code>.
		 */
		public static final SourceModel.Expr truncate(SourceModel.Expr x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.truncate), x});
		}

		/**
		 * @see #truncate(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param x
		 * @return the SourceModel.Expr representing an application of truncate
		 */
		public static final SourceModel.Expr truncate(double x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.truncate), SourceModel.Expr.makeDoubleValue(x)});
		}

		/**
		 * Name binding for function: truncate.
		 * @see #truncate(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName truncate = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "truncate");

		/**
		 * Creates a 2-tuple.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Core.Prelude.strictTuple2
		 * </dl>
		 * 
		 * @param x1 (CAL type: <code>a</code>)
		 *          the 1st component of the tuple.
		 * @param x2 (CAL type: <code>b</code>)
		 *          the 2nd component of the tuple.
		 * @return (CAL type: <code>(a, b)</code>) 
		 *          the tuple <code>(x1, x2)</code>.
		 */
		public static final SourceModel.Expr tuple2(SourceModel.Expr x1, SourceModel.Expr x2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.tuple2), x1, x2});
		}

		/**
		 * Name binding for function: tuple2.
		 * @see #tuple2(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName tuple2 = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "tuple2");

		/**
		 * Creates a 3-tuple.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Core.Prelude.strictTuple3
		 * </dl>
		 * 
		 * @param x1 (CAL type: <code>a</code>)
		 *          the 1st component of the tuple.
		 * @param x2 (CAL type: <code>b</code>)
		 *          the 2nd component of the tuple.
		 * @param x3 (CAL type: <code>c</code>)
		 *          the 3rd component of the tuple.
		 * @return (CAL type: <code>(a, b, c)</code>) 
		 *          the tuple <code>(x1, x2, x3)</code>.
		 */
		public static final SourceModel.Expr tuple3(SourceModel.Expr x1, SourceModel.Expr x2, SourceModel.Expr x3) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.tuple3), x1, x2, x3});
		}

		/**
		 * Name binding for function: tuple3.
		 * @see #tuple3(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName tuple3 = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "tuple3");

		/**
		 * Creates a 4-tuple.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Core.Prelude.strictTuple4
		 * </dl>
		 * 
		 * @param x1 (CAL type: <code>a</code>)
		 *          the 1st component of the tuple.
		 * @param x2 (CAL type: <code>b</code>)
		 *          the 2nd component of the tuple.
		 * @param x3 (CAL type: <code>c</code>)
		 *          the 3rd component of the tuple.
		 * @param x4 (CAL type: <code>d</code>)
		 *          the 4th component of the tuple.
		 * @return (CAL type: <code>(a, b, c, d)</code>) 
		 *          the tuple <code>(x1, x2, x3, x4)</code>.
		 */
		public static final SourceModel.Expr tuple4(SourceModel.Expr x1, SourceModel.Expr x2, SourceModel.Expr x3, SourceModel.Expr x4) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.tuple4), x1, x2, x3, x4});
		}

		/**
		 * Name binding for function: tuple4.
		 * @see #tuple4(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName tuple4 = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "tuple4");

		/**
		 * Creates a 5-tuple.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Core.Prelude.strictTuple5
		 * </dl>
		 * 
		 * @param x1 (CAL type: <code>a</code>)
		 *          the 1st component of the tuple.
		 * @param x2 (CAL type: <code>b</code>)
		 *          the 2nd component of the tuple.
		 * @param x3 (CAL type: <code>c</code>)
		 *          the 3rd component of the tuple.
		 * @param x4 (CAL type: <code>d</code>)
		 *          the 4th component of the tuple.
		 * @param x5 (CAL type: <code>e</code>)
		 *          the 5th component of the tuple.
		 * @return (CAL type: <code>(a, b, c, d, e)</code>) 
		 *          the tuple <code>(x1, x2, x3, x4, x5)</code>.
		 */
		public static final SourceModel.Expr tuple5(SourceModel.Expr x1, SourceModel.Expr x2, SourceModel.Expr x3, SourceModel.Expr x4, SourceModel.Expr x5) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.tuple5), x1, x2, x3, x4, x5});
		}

		/**
		 * Name binding for function: tuple5.
		 * @see #tuple5(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName tuple5 = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "tuple5");

		/**
		 * Creates a 6-tuple.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Core.Prelude.strictTuple6
		 * </dl>
		 * 
		 * @param x1 (CAL type: <code>a</code>)
		 *          the 1st component of the tuple.
		 * @param x2 (CAL type: <code>b</code>)
		 *          the 2nd component of the tuple.
		 * @param x3 (CAL type: <code>c</code>)
		 *          the 3rd component of the tuple.
		 * @param x4 (CAL type: <code>d</code>)
		 *          the 4th component of the tuple.
		 * @param x5 (CAL type: <code>e</code>)
		 *          the 5th component of the tuple.
		 * @param x6 (CAL type: <code>f</code>)
		 *          the 6th component of the tuple.
		 * @return (CAL type: <code>(a, b, c, d, e, f)</code>) 
		 *          the tuple <code>(x1, x2, x3, x4, x5, x6)</code>.
		 */
		public static final SourceModel.Expr tuple6(SourceModel.Expr x1, SourceModel.Expr x2, SourceModel.Expr x3, SourceModel.Expr x4, SourceModel.Expr x5, SourceModel.Expr x6) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.tuple6), x1, x2, x3, x4, x5, x6});
		}

		/**
		 * Name binding for function: tuple6.
		 * @see #tuple6(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName tuple6 = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "tuple6");

		/**
		 * Creates a 7-tuple.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Core.Prelude.strictTuple7
		 * </dl>
		 * 
		 * @param x1 (CAL type: <code>a</code>)
		 *          the 1st component of the tuple.
		 * @param x2 (CAL type: <code>b</code>)
		 *          the 2nd component of the tuple.
		 * @param x3 (CAL type: <code>c</code>)
		 *          the 3rd component of the tuple.
		 * @param x4 (CAL type: <code>d</code>)
		 *          the 4th component of the tuple.
		 * @param x5 (CAL type: <code>e</code>)
		 *          the 5th component of the tuple.
		 * @param x6 (CAL type: <code>f</code>)
		 *          the 6th component of the tuple.
		 * @param x7 (CAL type: <code>g</code>)
		 *          the 7th component of the tuple.
		 * @return (CAL type: <code>(a, b, c, d, e, f, g)</code>) 
		 *          the tuple <code>(x1, x2, x3, x4, x5, x6, x7)</code>.
		 */
		public static final SourceModel.Expr tuple7(SourceModel.Expr x1, SourceModel.Expr x2, SourceModel.Expr x3, SourceModel.Expr x4, SourceModel.Expr x5, SourceModel.Expr x6, SourceModel.Expr x7) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.tuple7), x1, x2, x3, x4, x5, x6, x7});
		}

		/**
		 * Name binding for function: tuple7.
		 * @see #tuple7(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName tuple7 = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "tuple7");

		/**
		 * Returns the type arguments of a type representation. For example, for <code>Cal.Core.Prelude.Int</code>, <code>Maybe String</code>, <code>[Int]</code>
		 * and <code>Int -&gt; Char</code> these are <code>[]</code>, <code>[String]</code>, <code>[Int]</code> and <code>[Int, Char]</code> respectively.
		 * For a record type, it is the list of types of the fields, in field-name order.
		 * Intuitively, the type arguments can be thought of as representing everything in the type except for the root type.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Core.Prelude.sameRootType, Cal.Core.Prelude.nTypeArguments
		 * </dl>
		 * 
		 * @param typeRep (CAL type: <code>Cal.Core.Prelude.TypeRep</code>)
		 *          runtime type representation of a value, typically obtained by calling <code>Cal.Core.Prelude.typeOf</code> on a value.
		 * @return (CAL type: <code>[Cal.Core.Prelude.TypeRep]</code>) 
		 *          list of type arguments of the type
		 */
		public static final SourceModel.Expr typeArguments(SourceModel.Expr typeRep) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.typeArguments), typeRep});
		}

		/**
		 * Name binding for function: typeArguments.
		 * @see #typeArguments(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName typeArguments = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "typeArguments");

		/**
		 * Returns the <code>Cal.Core.Prelude.TypeRep</code> value representing the type of the argument.
		 * @param value (CAL type: <code>Cal.Core.Prelude.Typeable a => a</code>)
		 *          the value whose type is to be returned as a <code>Cal.Core.Prelude.TypeRep</code> value.
		 * @return (CAL type: <code>Cal.Core.Prelude.TypeRep</code>) 
		 *          the <code>Cal.Core.Prelude.TypeRep</code> value representing the type of the argument.
		 */
		public static final SourceModel.Expr typeOf(SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.typeOf), value});
		}

		/**
		 * Name binding for function: typeOf.
		 * @see #typeOf(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName typeOf = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "typeOf");

		/**
		 * Returns the canonical string representation of a <code>Cal.Core.Prelude.TypeRep</code> value.
		 * @param typeRep (CAL type: <code>Cal.Core.Prelude.TypeRep</code>)
		 *          the <code>Cal.Core.Prelude.TypeRep</code> value to be converted into a string.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          the string representation of the <code>Cal.Core.Prelude.TypeRep</code> value.
		 */
		public static final SourceModel.Expr typeRepToString(SourceModel.Expr typeRep) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.typeRepToString), typeRep});
		}

		/**
		 * Name binding for function: typeRepToString.
		 * @see #typeRepToString(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName typeRepToString = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "typeRepToString");

		/**
		 * Converts a function of 2 arguments to a function of a single pair argument.
		 * @param f (CAL type: <code>a -> b -> c</code>)
		 *          the function to be uncurried.
		 * @param pair (CAL type: <code>(a, b)</code>)
		 *          the pair of arguments to the uncurried function.
		 * @return (CAL type: <code>c</code>) 
		 *          the result of evaluating <code>(f pair.#1 pair.#2)</code>.
		 */
		public static final SourceModel.Expr uncurry(SourceModel.Expr f, SourceModel.Expr pair) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.uncurry), f, pair});
		}

		/**
		 * Name binding for function: uncurry.
		 * @see #uncurry(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName uncurry = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "uncurry");

		/**
		 * The <code>Cal.Core.Prelude.undefined</code> function will cause execution to stop immediately. An exception will be thrown.
		 * The exception contains the error message string. In addition, the exception
		 * will contain an error information object that has information about the position in the
		 * source code of the undefined call. Note, this is not the position of the <code>Cal.Core.Prelude.error</code> call
		 * in the undefined function body.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Core.Prelude.error, Cal.Core.Prelude.assert
		 * </dl>
		 * 
		 * @return (CAL type: <code>a</code>) 
		 *          The return value is <em>bottom</em>.
		 */
		public static final SourceModel.Expr undefined() {
			return SourceModel.Expr.Var.make(Functions.undefined);
		}

		/**
		 * Name binding for function: undefined.
		 * @see #undefined()
		 */
		public static final QualifiedName undefined = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "undefined");

		/**
		 * <code>Cal.Core.Prelude.unsafeCoerce</code> is equivalent to the identity function operationally.
		 * It is an unsafe function because it circumvents the type system and is intended as a compiler
		 * hook to implement certain safe functions. Use with extreme caution!
		 * <p>
		 * It is only safe to use <code>Cal.Core.Prelude.unsafeCoerce</code> when you know what the actual type of a value is, and
		 * want to convey this information to the CAL compiler. Note: <code>Cal.Core.Prelude.unsafeCoerce</code> coerces the compiler, it does
		 * not do anything to the value. In particular, <code>Cal.Core.Prelude.unsafeCoerce</code> cannot be used to "cast" a value to a type
		 * that it actually is not, even for innocuous seeming casts as from <code>Cal.Core.Prelude.Short</code> to <code>Cal.Core.Prelude.Int</code>.
		 * <p>
		 * To perform a Java upcast between two foreign Java reference types, use foreign "cast" function, or
		 * the <code>Cal.Core.Prelude.output</code> function. Do not use <code>Cal.Core.Prelude.unsafeCoerce</code> for this purpose since it will interfere
		 * with compiler optimizations.
		 * 
		 * @param value (CAL type: <code>a</code>)
		 *          the value to coerce into another type.
		 * @return (CAL type: <code>b</code>) 
		 *          the value in the coerced type.
		 */
		public static final SourceModel.Expr unsafeCoerce(SourceModel.Expr value) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.unsafeCoerce), value});
		}

		/**
		 * Name binding for function: unsafeCoerce.
		 * @see #unsafeCoerce(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName unsafeCoerce = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "unsafeCoerce");

		/**
		 * Converts a <code>Cal.Core.Prelude.CalValue</code> to a CAL value of type <code>a</code>. The underlying CAL value is not modified in any way. This function
		 * in effect tells the CAL compiler what the type of an untyped <code>Cal.Core.Prelude.CalValue</code> is. The CAL compiler can not check that this assertion
		 * is correct, and the behaviour of CAL is undefined in the event that the type is incorrectly specified.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Core.Prelude.toCalValue
		 * </dl>
		 * 
		 * @param x (CAL type: <code>Cal.Core.Prelude.CalValue</code>)
		 *          a value
		 * @return (CAL type: <code>a</code>) 
		 *          the value <code>x</code>, viewed as having the specified return type. The underlying value is not modified.
		 */
		public static final SourceModel.Expr unsafeFromCalValue(SourceModel.Expr x) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.unsafeFromCalValue), x});
		}

		/**
		 * Name binding for function: unsafeFromCalValue.
		 * @see #unsafeFromCalValue(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName unsafeFromCalValue = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "unsafeFromCalValue");

		/**
		 * Keeps applying <code>iterationFunction</code> until <code>stopConditionFunction</code> holds.
		 * @param stopConditionFunction (CAL type: <code>a -> Cal.Core.Prelude.Boolean</code>)
		 *          the predicate that dictates when the iteration should stop
		 * @param iterationFunction (CAL type: <code>a -> a</code>)
		 *          the function to be iteratively applied.
		 * @param initialValue (CAL type: <code>a</code>)
		 *          the initial value to the iterated application.
		 * @return (CAL type: <code>a</code>) 
		 *          the result of the iterated application.
		 */
		public static final SourceModel.Expr until(SourceModel.Expr stopConditionFunction, SourceModel.Expr iterationFunction, SourceModel.Expr initialValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.until), stopConditionFunction, iterationFunction, initialValue});
		}

		/**
		 * Name binding for function: until.
		 * @see #until(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName until = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "until");

		/**
		 * For numeric types, <code>upFrom start creates</code> the list <code>[start, start + 1, start + 2, ...]</code>.
		 * @param start (CAL type: <code>Cal.Core.Prelude.Enum a => a</code>)
		 *          the start value.
		 * @return (CAL type: <code>Cal.Core.Prelude.Enum a => [a]</code>) 
		 *          the list <code>[start, start + 1, start + 2, ...]</code>.
		 */
		public static final SourceModel.Expr upFrom(SourceModel.Expr start) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFrom), start});
		}

		/**
		 * Name binding for function: upFrom.
		 * @see #upFrom(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName upFrom = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "upFrom");

		/**
		 * For numeric types, <code>upFromThen start next</code> creates the arithmetic sequence with first 2 terms <code>start</code> and
		 * <code>next</code>. and then following with that difference i.e.
		 * <code>[start, next, next + (next - start), next + 2*(next - start),...]</code>.
		 * @param start (CAL type: <code>Cal.Core.Prelude.Enum a => a</code>)
		 *          the start value.
		 * @param next (CAL type: <code>Cal.Core.Prelude.Enum a => a</code>)
		 *          the second value in the returned list.
		 * @return (CAL type: <code>Cal.Core.Prelude.Enum a => [a]</code>) 
		 *          the list <code>[start, next, next + (next - start), next + 2*(next - start),...]</code>.
		 */
		public static final SourceModel.Expr upFromThen(SourceModel.Expr start, SourceModel.Expr next) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromThen), start, next});
		}

		/**
		 * Name binding for function: upFromThen.
		 * @see #upFromThen(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName upFromThen = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "upFromThen");

		/**
		 * For numeric types, <code>upFromThenTo start next edn</code>, creates the arithmetic sequence with first 2 terms <code>start</code>
		 * and <code>next</code>. and then following with that difference i.e.
		 * <code>[start, next, next + (next - start), next + 2*(next - start),..., end]</code>.
		 * @param start (CAL type: <code>Cal.Core.Prelude.Enum a => a</code>)
		 *          the start value.
		 * @param next (CAL type: <code>Cal.Core.Prelude.Enum a => a</code>)
		 *          the second value in the returned list.
		 * @param end (CAL type: <code>Cal.Core.Prelude.Enum a => a</code>)
		 *          the end value.
		 * @return (CAL type: <code>Cal.Core.Prelude.Enum a => [a]</code>) 
		 *          the list <code>[start, next, next + (next - start), next + 2*(next - start),..., end]</code>.
		 */
		public static final SourceModel.Expr upFromThenTo(SourceModel.Expr start, SourceModel.Expr next, SourceModel.Expr end) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromThenTo), start, next, end});
		}

		/**
		 * Name binding for function: upFromThenTo.
		 * @see #upFromThenTo(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName upFromThenTo = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "upFromThenTo");

		/**
		 * For numeric types, <code>upFromTo start end</code> creates the list <code>[start, start + 1, start + 2, ..., end]</code>.
		 * @param start (CAL type: <code>Cal.Core.Prelude.Enum a => a</code>)
		 *          the start value.
		 * @param end (CAL type: <code>Cal.Core.Prelude.Enum a => a</code>)
		 *          the end value.
		 * @return (CAL type: <code>Cal.Core.Prelude.Enum a => [a]</code>) 
		 *          the list <code>[start, start + 1, start + 2, ..., end]</code>.
		 */
		public static final SourceModel.Expr upFromTo(SourceModel.Expr start, SourceModel.Expr end) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.upFromTo), start, end});
		}

		/**
		 * Name binding for function: upFromTo.
		 * @see #upFromTo(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName upFromTo = 
			QualifiedName.make(CAL_Prelude.MODULE_NAME, "upFromTo");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = -2012184564;

}
