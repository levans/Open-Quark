/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_Record.java)
 * was generated from CAL module: Cal.Core.Record.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Core.Record module from Java code.
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
 * Defines many useful functions for working with CAL record types. Since
 * tuples are records, these functions are also useful for working with tuples.
 * @author Bo Ilic
 */
public final class CAL_Record {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Core.Record");

	/**
	 * This inner class (TypeConstructors) contains constants
	 * and methods related to binding to CAL TypeConstructors in the Cal.Core.Record module.
	 */
	public static final class TypeConstructors {
		/**
		 * This data type is used to represent a record dictionary function. A record
		 * dictionary contains specilized versions of a class method for each
		 * field in a record.
		 * <p>
		 * A Dictionary can only be obtained using the <code>Cal.Core.Record.dictionary</code> function.
		 */
		public static final QualifiedName Dictionary = 
			QualifiedName.make(CAL_Record.MODULE_NAME, "Dictionary");

		/**
		 * <code>Cal.Core.Record.JRecordValue</code> is a type mainly of interest to Java clients wanting to work with CAL records in Java code.
		 * <p>
		 * It is a foreign type corresponding to the Java class <code>org.openquark.cal.runtime.RecordValue</code>. 
		 * and it implements the <code>java.util.SortedMap</code> interface. In particular, it retains field-name
		 * information in the resulting Java value. This can be helpful in avoiding bugs in client code, since the default method of
		 * outputing CAL records using <code>Cal.Core.Prelude.output</code> produces a Java list. This is efficient, but can be error-prone.
		 */
		public static final QualifiedName JRecordValue = 
			QualifiedName.make(CAL_Record.MODULE_NAME, "JRecordValue");

	}
	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Core.Record module.
	 */
	public static final class Functions {
		/**
		 * This function is used to obtain the hidden dictionary argument from the enclosing
		 * function. It may only be used from directly within a function that has a single
		 * record dictionary
		 * argument - ie a function which has a single type class constraint on a
		 * single record type variable. The example below shows how to write a function that
		 * obtains the show dictionary. This function can then be used to get the show
		 * dictionary for any record. The functionName argument must always be a string
		 * literal - it is checked and bound at compile time.
		 * <p>
		 * 
		 * <pre> showDict :: Show r =&gt; {r} -&gt; Cal.Core.Record.Dictionary {r};
		 *  showDict r = Prelude.dictionary r "show";</pre>
		 * 
		 * 
		 * @param record (CAL type: <code>{a}</code>)
		 *          the record for which the dictionary is obtained.
		 * @param functionName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the name of the function in the type class that the
		 * dictionary can be used to apply. This must be a literal string. A compile 
		 * time error will thrown if this string does not match an instance function
		 * in the dictionary.
		 * @return (CAL type: <code>Cal.Core.Record.Dictionary {a}</code>) 
		 *          the hidden record dictionary parameter from the enclosing function
		 */
		public static final SourceModel.Expr dictionary(SourceModel.Expr record, SourceModel.Expr functionName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.dictionary), record, functionName});
		}

		/**
		 * @see #dictionary(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param record
		 * @param functionName
		 * @return the SourceModel.Expr representing an application of dictionary
		 */
		public static final SourceModel.Expr dictionary(SourceModel.Expr record, java.lang.String functionName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.dictionary), record, SourceModel.Expr.makeStringValue(functionName)});
		}

		/**
		 * Name binding for function: dictionary.
		 * @see #dictionary(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName dictionary = 
			QualifiedName.make(CAL_Record.MODULE_NAME, "dictionary");

		/**
		 * This function extracts the field names from a record dictionary.
		 * @param dict (CAL type: <code>Cal.Core.Record.Dictionary {r}</code>)
		 *          the record dictionary to get the field names from.
		 * @return (CAL type: <code>[Cal.Core.Prelude.String]</code>) 
		 *          a list of field names from the record dictionary
		 */
		public static final SourceModel.Expr dictionaryFieldNames(SourceModel.Expr dict) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.dictionaryFieldNames), dict});
		}

		/**
		 * Name binding for function: dictionaryFieldNames.
		 * @see #dictionaryFieldNames(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName dictionaryFieldNames = 
			QualifiedName.make(CAL_Record.MODULE_NAME, "dictionaryFieldNames");

		/**
		 * <code>fieldNames</code> returns the ordered list of field names of its argument record.
		 * Ordinal field names, such as <code>"#1"</code>, <code>"#2"</code> etc, are first, in ordinal order.
		 * Textual field names such as <code>"orderDate"</code>, <code>"shipDate"</code> are next, in alphabetical order.
		 * @param recordValue (CAL type: <code>{r}</code>)
		 *          the record whose field names are to be returned.
		 * @return (CAL type: <code>[Cal.Core.Prelude.String]</code>) 
		 *          the field names of the record.
		 */
		public static final SourceModel.Expr fieldNames(SourceModel.Expr recordValue) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fieldNames), recordValue});
		}

		/**
		 * Name binding for function: fieldNames.
		 * @see #fieldNames(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fieldNames = 
			QualifiedName.make(CAL_Record.MODULE_NAME, "fieldNames");

		/**
		 * The CAL record obtained by applying <code>Cal.Core.Prelude.input</code> to each value of the Java map from <code>org.openquark.cal.compiler.FieldName</code>
		 * to <code>java.lang.Object</code>. The nth element of the Java map (in field-name order) is mapped to the nth field (in field-name order) in
		 * the CAL record. For example, if the Java map is <code>{f1 = x1, f2 = x2, f3 = x3}</code>, then the resulting CAL record is
		 * <code>{f1 = input x1, f2 = input x2, f3 = input x3}</code>. Note that the size of the Java map must be the same as the number of fields 
		 * in the CAL record or an exception results. Similarly the field-names of the Java map must exactly match the field-names in the CAL record.
		 * These conditions are mainly intended to help users catch bugs in their code at an early stage.
		 * @param javaMap (CAL type: <code>Cal.Core.Prelude.JMap</code>)
		 *          must be a <code>java.util.Map</code> of <code>org.openquark.cal.compiler.FieldName</code> to <code>java.lang.Object</code>.
		 * @return (CAL type: <code>Cal.Core.Prelude.Inputable r => {r}</code>) 
		 *          the CAL record obtained by applying <code>Cal.Core.Prelude.input</code> to each value of the Java map.
		 */
		public static final SourceModel.Expr fromJMap(SourceModel.Expr javaMap) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fromJMap), javaMap});
		}

		/**
		 * Name binding for function: fromJMap.
		 * @see #fromJMap(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fromJMap = 
			QualifiedName.make(CAL_Record.MODULE_NAME, "fromJMap");

		/**
		 * Function for determining if a record has a field of the given name.
		 * @param recordValue (CAL type: <code>{r}</code>)
		 *          the record to be checked for the presense of a field of the given name.
		 * @param fieldName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the field name to be checked.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the named field is present in <code>recordValue</code>; <code>Cal.Core.Prelude.False</code> otherwise.
		 */
		public static final SourceModel.Expr hasField(SourceModel.Expr recordValue, SourceModel.Expr fieldName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.hasField), recordValue, fieldName});
		}

		/**
		 * @see #hasField(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param recordValue
		 * @param fieldName
		 * @return the SourceModel.Expr representing an application of hasField
		 */
		public static final SourceModel.Expr hasField(SourceModel.Expr recordValue, java.lang.String fieldName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.hasField), recordValue, SourceModel.Expr.makeStringValue(fieldName)});
		}

		/**
		 * Name binding for function: hasField.
		 * @see #hasField(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName hasField = 
			QualifiedName.make(CAL_Record.MODULE_NAME, "hasField");

		/**
		 * Helper binding method for function: jRecordValueToJMap. 
		 * @param arg_1
		 * @return the SourceModule.expr representing an application of jRecordValueToJMap
		 */
		public static final SourceModel.Expr jRecordValueToJMap(SourceModel.Expr arg_1) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jRecordValueToJMap), arg_1});
		}

		/**
		 * Name binding for function: jRecordValueToJMap.
		 * @see #jRecordValueToJMap(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jRecordValueToJMap = 
			QualifiedName.make(CAL_Record.MODULE_NAME, "jRecordValueToJMap");

		/**
		 * <code>Cal.Core.Record.strictRecord record</code> evaluates each field of <code>record</code> to weak-head normal form,
		 * in field-name order, and then returns <code>record</code> itself.
		 * <p>
		 * As with all sequencing functions in CAL, this function is primarily used for two main reasons:
		 * <ol>
		 *  <li>
		 *   controlling evaluation order when working with CAL records that arise
		 *   from external Java sources e.g. via <code>Cal.Core.Prelude.input</code> or <code>Cal.Core.Record.fromJMap</code>.
		 *  </li>
		 *  <li>
		 *   controlling space usage since the record type has non-strict fields by definition. 
		 *  </li>
		 * </ol>
		 * <p>
		 * <code>Cal.Core.Record.strictRecord</code> is analogous to functions like <code>Cal.Collections.List.seqList</code> and <code>Cal.Collections.List.strictList</code> for
		 * sequencing lists, and <code>Cal.Core.Prelude.strictTuple2</code> (and its relatives) for sequencing tuples.
		 * <p>
		 * An alternative to this function is to use the <code>Cal.Core.Prelude.deepSeq</code> function, except that <code>Cal.Core.Prelude.deepSeq</code> is
		 * a much coarser-grained tool since it fully evaluates each field which may be sacrificing valuable lazyness.
		 * <p>
		 * To illustrate the semantics of this function, for a 3-tuple, it is equivalent to the following:
		 * 
		 * <pre> strictTuple3 :: (a, b, c) -&gt; (a, b, c);
		 *  strictTuple3 !tuple =
		 *      case tuple of
		 *      (x1, x2, x3) -&gt; x1 `seq` x2 `seq` x3 `seq` tuple;
		 *      ;
		 * </pre>
		 * 
		 * 
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.Core.Prelude.seq, Cal.Core.Prelude.deepSeq, Cal.Core.Prelude.strictTuple2, Cal.Collections.List.seqList, Cal.Collections.List.strictList
		 * </dl>
		 * 
		 * @param record (CAL type: <code>{r}</code>)
		 * @return (CAL type: <code>{r}</code>) 
		 *          record itself after first evaluating each field of the record to weak-head normal form, in field-name order.
		 */
		public static final SourceModel.Expr strictRecord(SourceModel.Expr record) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.strictRecord), record});
		}

		/**
		 * Name binding for function: strictRecord.
		 * @see #strictRecord(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName strictRecord = 
			QualifiedName.make(CAL_Record.MODULE_NAME, "strictRecord");

		/**
		 * Converts a record into a list of (fieldName, value) pairs.
		 * @param r (CAL type: <code>Cal.Core.Prelude.Outputable r => {r}</code>)
		 *          the record to be converted.
		 * @return (CAL type: <code>[(Cal.Core.Prelude.String, Cal.Core.Prelude.JObject)]</code>) 
		 *          the corresponding list of (field-name, value) pairs.
		 */
		public static final SourceModel.Expr toFieldValueList(SourceModel.Expr r) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.toFieldValueList), r});
		}

		/**
		 * Name binding for function: toFieldValueList.
		 * @see #toFieldValueList(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName toFieldValueList = 
			QualifiedName.make(CAL_Record.MODULE_NAME, "toFieldValueList");

		/**
		 * Converts a CAL record to a Java map from  <code>org.openquark.cal.compiler.FieldName</code>
		 * to <code>java.lang.Object</code>. For example, if the CAL record value is <code>{f1 = x1, f2 = x2, f3 = x3}</code>, then the resulting
		 * Java map is <code>{f1 = Cal.Core.Prelude.output x1, f2 = output x2, f3 = output x3}</code>.
		 * @param record (CAL type: <code>Cal.Core.Prelude.Outputable r => {r}</code>)
		 *          a CAL record.
		 * @return (CAL type: <code>Cal.Core.Record.JRecordValue</code>) 
		 *          a Java representation of the CAL record that includes field-name information.
		 */
		public static final SourceModel.Expr toJRecordValue(SourceModel.Expr record) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.toJRecordValue), record});
		}

		/**
		 * Name binding for function: toJRecordValue.
		 * @see #toJRecordValue(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName toJRecordValue = 
			QualifiedName.make(CAL_Record.MODULE_NAME, "toJRecordValue");

		/**
		 * Converts a record with the ordinal fields <code>#1</code> and <code>#2</code> into a pair.
		 * @param r (CAL type: <code>(r\#1, r\#2) => {r | #1 :: a, #2 :: b}</code>)
		 *          the record to be converted.
		 * @return (CAL type: <code>(a, b)</code>) 
		 *          the corresponding pair.
		 */
		public static final SourceModel.Expr toTuple2(SourceModel.Expr r) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.toTuple2), r});
		}

		/**
		 * Name binding for function: toTuple2.
		 * @see #toTuple2(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName toTuple2 = 
			QualifiedName.make(CAL_Record.MODULE_NAME, "toTuple2");

		/**
		 * Converts a record with the ordinal fields <code>#1</code> through <code>#3</code> into a triple.
		 * @param r (CAL type: <code>(r\#1, r\#2, r\#3) => {r | #1 :: a, #2 :: b, #3 :: c}</code>)
		 *          the record to be converted.
		 * @return (CAL type: <code>(a, b, c)</code>) 
		 *          the corresponding triple.
		 */
		public static final SourceModel.Expr toTuple3(SourceModel.Expr r) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.toTuple3), r});
		}

		/**
		 * Name binding for function: toTuple3.
		 * @see #toTuple3(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName toTuple3 = 
			QualifiedName.make(CAL_Record.MODULE_NAME, "toTuple3");

		/**
		 * Converts a record with the ordinal fields <code>#1</code> through <code>#4</code> into a 4-tuple.
		 * @param r (CAL type: <code>(r\#1, r\#2, r\#3, r\#4) => {r | #1 :: a, #2 :: b, #3 :: c, #4 :: d}</code>)
		 *          the record to be converted.
		 * @return (CAL type: <code>(a, b, c, d)</code>) 
		 *          the corresponding 4-tuple.
		 */
		public static final SourceModel.Expr toTuple4(SourceModel.Expr r) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.toTuple4), r});
		}

		/**
		 * Name binding for function: toTuple4.
		 * @see #toTuple4(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName toTuple4 = 
			QualifiedName.make(CAL_Record.MODULE_NAME, "toTuple4");

		/**
		 * Converts a record with the ordinal fields <code>#1</code> through <code>#5</code> into a 5-tuple.
		 * @param r (CAL type: <code>(r\#1, r\#2, r\#3, r\#4, r\#5) => {r | #1 :: a, #2 :: b, #3 :: c, #4 :: d, #5 :: e}</code>)
		 *          the record to be converted.
		 * @return (CAL type: <code>(a, b, c, d, e)</code>) 
		 *          the corresponding 5-tuple.
		 */
		public static final SourceModel.Expr toTuple5(SourceModel.Expr r) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.toTuple5), r});
		}

		/**
		 * Name binding for function: toTuple5.
		 * @see #toTuple5(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName toTuple5 = 
			QualifiedName.make(CAL_Record.MODULE_NAME, "toTuple5");

		/**
		 * Converts a record with the ordinal fields <code>#1</code> through <code>#6</code> into a 6-tuple.
		 * @param r (CAL type: <code>(r\#1, r\#2, r\#3, r\#4, r\#5, r\#6) => {r | #1 :: a, #2 :: b, #3 :: c, #4 :: d, #5 :: e, #6 :: f}</code>)
		 *          the record to be converted.
		 * @return (CAL type: <code>(a, b, c, d, e, f)</code>) 
		 *          the corresponding 6-tuple.
		 */
		public static final SourceModel.Expr toTuple6(SourceModel.Expr r) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.toTuple6), r});
		}

		/**
		 * Name binding for function: toTuple6.
		 * @see #toTuple6(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName toTuple6 = 
			QualifiedName.make(CAL_Record.MODULE_NAME, "toTuple6");

		/**
		 * Converts a record with the ordinal fields <code>#1</code> through <code>#7</code> into a 7-tuple.
		 * @param r (CAL type: <code>(r\#1, r\#2, r\#3, r\#4, r\#5, r\#6, r\#7) => {r | #1 :: a, #2 :: b, #3 :: c, #4 :: d, #5 :: e, #6 :: f, #7 :: g}</code>)
		 *          the record to be converted.
		 * @return (CAL type: <code>(a, b, c, d, e, f, g)</code>) 
		 *          the corresponding 7-tuple.
		 */
		public static final SourceModel.Expr toTuple7(SourceModel.Expr r) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.toTuple7), r});
		}

		/**
		 * Name binding for function: toTuple7.
		 * @see #toTuple7(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName toTuple7 = 
			QualifiedName.make(CAL_Record.MODULE_NAME, "toTuple7");

		/**
		 * Converts a record into a list of Java objects representing the values held by the record's fields.
		 * @param r (CAL type: <code>Cal.Core.Prelude.Outputable r => {r}</code>)
		 *          the record to be converted.
		 * @return (CAL type: <code>[Cal.Core.Prelude.JObject]</code>) 
		 *          the corresponding list of Java objects representing the values held by the record's fields.
		 */
		public static final SourceModel.Expr toValueList(SourceModel.Expr r) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.toValueList), r});
		}

		/**
		 * Name binding for function: toValueList.
		 * @see #toValueList(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName toValueList = 
			QualifiedName.make(CAL_Record.MODULE_NAME, "toValueList");

		/**
		 * This function takes a record dictionary and a tuple containing either
		 * records and lists and creates a list with one element for each field in the
		 * record dictionary. Each element in the output list is crated by applying
		 * the dictionary function using arguments taken from the values tuple. The
		 * values tuple must contain records or lists which have at leasts as many
		 * elements or fields as the record dictionary. The i<sup>th</sup> element/field of
		 * values.#j is used as the j<sup>th</sup> argument for the i<sup>th</sup> appplication of the
		 * dictionary function to create the i<sup>th</sup> element in the output list.
		 * <p>
		 * This is an unsafe function because it circumvents the type system and can result in 
		 * a runtime type mismatch if the dictionary is not appropriate. 
		 * Use with extreme caution.
		 * 
		 * @param dict (CAL type: <code>Cal.Core.Record.Dictionary {r}</code>)
		 *          a record dictionary
		 * @param values (CAL type: <code>{b}</code>)
		 *          a tuple containing the arguments used to apply the dictionary functions
		 * @return (CAL type: <code>[a]</code>) 
		 *          a new list with one element for each field in the record dictionary.
		 */
		public static final SourceModel.Expr unsafeBuildList(SourceModel.Expr dict, SourceModel.Expr values) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.unsafeBuildList), dict, values});
		}

		/**
		 * Name binding for function: unsafeBuildList.
		 * @see #unsafeBuildList(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName unsafeBuildList = 
			QualifiedName.make(CAL_Record.MODULE_NAME, "unsafeBuildList");

		/**
		 * This function takes a record dictionary and applies it to convert a record
		 * to a homogeneous list with one element for each record field. The dictionary
		 * must be for a function of type <code>r -&gt; a</code> or a runtime error will occur.
		 * <p>
		 * This is an unsafe function because it circumvents the type system and can result in 
		 * a runtime type mismatch if the dictionary function is not appropriate. 
		 * Use with extreme caution.
		 * 
		 * @param dict (CAL type: <code>Cal.Core.Record.Dictionary {r}</code>)
		 *          the record dictionary
		 * @param record (CAL type: <code>{r}</code>)
		 *          the to record to convert to a list
		 * @return (CAL type: <code>[a]</code>) 
		 *          a new list with one element for each field in the input record.
		 */
		public static final SourceModel.Expr unsafeBuildListFromRecord(SourceModel.Expr dict, SourceModel.Expr record) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.unsafeBuildListFromRecord), dict, record});
		}

		/**
		 * Name binding for function: unsafeBuildListFromRecord.
		 * @see #unsafeBuildListFromRecord(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName unsafeBuildListFromRecord = 
			QualifiedName.make(
				CAL_Record.MODULE_NAME, 
				"unsafeBuildListFromRecord");

		/**
		 * This functions creates a record using a record dictionary. Each element i in
		 * the output record is crated by applying the dictionary function using
		 * arguments taken from the values tuple. The values tuple must contain records
		 * or lists, which have at leasts as many elements or fields as the output
		 * record. The i<sup>th</sup> element/field of values.#j is used as the j<sup>th</sup> argument for
		 * the i<sup>th</sup> appplication of the dictionary function to create the i<sup>th</sup> record
		 * field.
		 * <p>
		 * This is an unsafe function because it circumvents the type system and can result in 
		 * a runtime type mismatch if the dictionary function is not appropriate. 
		 * Use with extreme caution.
		 * 
		 * @param dict (CAL type: <code>Cal.Core.Record.Dictionary {r}</code>)
		 *          the record dictionary containing the function to apply to create
		 * the record values. The dictionary function must be compatabile with the
		 * arguments in the values tuple.
		 * @param values (CAL type: <code>{b}</code>)
		 *          This must be a tuple of records/lists which contain the values
		 * used to create the output record. All records and lists must have at least as
		 * many elements/fields as the output record.
		 * @return (CAL type: <code>{r}</code>) 
		 *          a new record created by applying the record dictionary function.
		 */
		public static final SourceModel.Expr unsafeBuildRecord(SourceModel.Expr dict, SourceModel.Expr values) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.unsafeBuildRecord), dict, values});
		}

		/**
		 * Name binding for function: unsafeBuildRecord.
		 * @see #unsafeBuildRecord(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName unsafeBuildRecord = 
			QualifiedName.make(CAL_Record.MODULE_NAME, "unsafeBuildRecord");

		/**
		 * This function takes a record dictionary and a homogenous list and creates a
		 * hetergenous record. The dictionary must be for a method of type b -&gt; r or a
		 * runtime error will occur. Each element in the output record is created by
		 * applying the dictionary function to the ith element in the values list.
		 * <p>
		 * This is an unsafe function because it circumvents the type system and can result in 
		 * a runtime type mismatch if the dictionary function is not appropriate. 
		 * Use with extreme caution.
		 * 
		 * @param dict (CAL type: <code>Cal.Core.Record.Dictionary {r}</code>)
		 *          the record dictionary. The dictionary function must be of type 
		 * <code>b -&gt; r;</code>
		 * @param values (CAL type: <code>[b]</code>)
		 *          a list of values that must be at least as long as there are
		 * fields in the record. A non-existient element runtime error will occur if
		 * this is not so.
		 * @return (CAL type: <code>{r}</code>) 
		 *          a new record created from the dictionary and list of values.
		 */
		public static final SourceModel.Expr unsafeBuildRecordFromList(SourceModel.Expr dict, SourceModel.Expr values) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.unsafeBuildRecordFromList), dict, values});
		}

		/**
		 * Name binding for function: unsafeBuildRecordFromList.
		 * @see #unsafeBuildRecordFromList(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName unsafeBuildRecordFromList = 
			QualifiedName.make(
				CAL_Record.MODULE_NAME, 
				"unsafeBuildRecordFromList");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = -598614094;

}
