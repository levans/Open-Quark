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
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (SqlType.java)
 * was generated from CAL type constructor: Cal.Data.SqlType.SqlType.
 *  
 * Creation date: Tue Aug 07 14:17:06 PDT 2007
 * --!>
 *  
 */
package org.openquark.util.database;


/**
 * This class (SqlType) provides a Java data class corresponding to
 * the CAL type constructor Cal.Data.SqlType.SqlType.
 * Because the type constructor has only one data constructor, with the same name
 * as the type constructor this class also represents instances of the data constructor.
 */
public abstract class SqlType {
	public SqlType() {
	}

	/**
	 * @return the name of the data constructor corresponding to this instance of SqlType
	 */
	public java.lang.String getDCName() {
		return null;
	}

	/**
	 * @return the ordinal of this instance of SqlType
	 */
	public int getDCOrdinal() {
		return -1;
	}

	/**
	 * This class represents instances of the CAL data constructor Cal.Data.SqlType.SqlType_TinyInt.
	 */
	public static final class SqlType_TinyInt extends SqlType {
		/**
		 * Lazily initialized, cached hashCode.
		 */
		private volatile int hashCode = 0;

		public SqlType_TinyInt() {
		}

		/**
		 * @return the name of the data constructor corresponding to this instance of SqlType
		 */
		public final java.lang.String getDCName() {
			return "SqlType_TinyInt";
		}

		/**
		 * @return the ordinal of this instance of SqlType
		 */
		public int getDCOrdinal() {
			return 0;
		}

		public final java.lang.String toString() {
			return getDCName() + "\n";
		}

		public final boolean equals(java.lang.Object object) {
			if (this == object) {
				return true;
			}
			if (object == null) {
				return false;
			}
			if (!(((java.lang.Object)object) instanceof SqlType_TinyInt)) {
				return false;
			}

			SqlType_TinyInt castobject = 
				((SqlType_TinyInt)(java.lang.Object)object);

			if (!getDCName().equals(castobject.getDCName())) {
				return false;
			}
			return true;
		}

		public final int hashCode() {
			if (hashCode == 0) {
				int result = 17;

				result = ((37 * result) + getDCName().hashCode());
				hashCode = result;
			}
			return hashCode;
		}

	}
	/**
	 * This class represents instances of the CAL data constructor Cal.Data.SqlType.SqlType_SmallInt.
	 */
	public static final class SqlType_SmallInt extends SqlType {
		/**
		 * Lazily initialized, cached hashCode.
		 */
		private volatile int hashCode = 0;

		public SqlType_SmallInt() {
		}

		/**
		 * @return the name of the data constructor corresponding to this instance of SqlType
		 */
		public final java.lang.String getDCName() {
			return "SqlType_SmallInt";
		}

		/**
		 * @return the ordinal of this instance of SqlType
		 */
		public int getDCOrdinal() {
			return 1;
		}

		public final java.lang.String toString() {
			return getDCName() + "\n";
		}

		public final boolean equals(java.lang.Object object) {
			if (this == object) {
				return true;
			}
			if (object == null) {
				return false;
			}
			if (!(((java.lang.Object)object) instanceof SqlType_SmallInt)) {
				return false;
			}

			SqlType_SmallInt castobject = 
				((SqlType_SmallInt)(java.lang.Object)object);

			if (!getDCName().equals(castobject.getDCName())) {
				return false;
			}
			return true;
		}

		public final int hashCode() {
			if (hashCode == 0) {
				int result = 17;

				result = ((37 * result) + getDCName().hashCode());
				hashCode = result;
			}
			return hashCode;
		}

	}
	/**
	 * This class represents instances of the CAL data constructor Cal.Data.SqlType.SqlType_Integer.
	 */
	public static final class SqlType_Integer extends SqlType {
		/**
		 * Lazily initialized, cached hashCode.
		 */
		private volatile int hashCode = 0;

		public SqlType_Integer() {
		}

		/**
		 * @return the name of the data constructor corresponding to this instance of SqlType
		 */
		public final java.lang.String getDCName() {
			return "SqlType_Integer";
		}

		/**
		 * @return the ordinal of this instance of SqlType
		 */
		public int getDCOrdinal() {
			return 2;
		}

		public final java.lang.String toString() {
			return getDCName() + "\n";
		}

		public final boolean equals(java.lang.Object object) {
			if (this == object) {
				return true;
			}
			if (object == null) {
				return false;
			}
			if (!(((java.lang.Object)object) instanceof SqlType_Integer)) {
				return false;
			}

			SqlType_Integer castobject = 
				((SqlType_Integer)(java.lang.Object)object);

			if (!getDCName().equals(castobject.getDCName())) {
				return false;
			}
			return true;
		}

		public final int hashCode() {
			if (hashCode == 0) {
				int result = 17;

				result = ((37 * result) + getDCName().hashCode());
				hashCode = result;
			}
			return hashCode;
		}

	}
	/**
	 * This class represents instances of the CAL data constructor Cal.Data.SqlType.SqlType_BigInt.
	 */
	public static final class SqlType_BigInt extends SqlType {
		/**
		 * Lazily initialized, cached hashCode.
		 */
		private volatile int hashCode = 0;

		public SqlType_BigInt() {
		}

		/**
		 * @return the name of the data constructor corresponding to this instance of SqlType
		 */
		public final java.lang.String getDCName() {
			return "SqlType_BigInt";
		}

		/**
		 * @return the ordinal of this instance of SqlType
		 */
		public int getDCOrdinal() {
			return 3;
		}

		public final java.lang.String toString() {
			return getDCName() + "\n";
		}

		public final boolean equals(java.lang.Object object) {
			if (this == object) {
				return true;
			}
			if (object == null) {
				return false;
			}
			if (!(((java.lang.Object)object) instanceof SqlType_BigInt)) {
				return false;
			}

			SqlType_BigInt castobject = 
				((SqlType_BigInt)(java.lang.Object)object);

			if (!getDCName().equals(castobject.getDCName())) {
				return false;
			}
			return true;
		}

		public final int hashCode() {
			if (hashCode == 0) {
				int result = 17;

				result = ((37 * result) + getDCName().hashCode());
				hashCode = result;
			}
			return hashCode;
		}

	}
	/**
	 * This class represents instances of the CAL data constructor Cal.Data.SqlType.SqlType_Decimal.
	 */
	public static final class SqlType_Decimal extends SqlType {
		/**
		 * Lazily initialized, cached hashCode.
		 */
		private volatile int hashCode = 0;

		private final int _precision;

		private final int _scale;

		public SqlType_Decimal(int _precision$, int _scale$) {
			_precision = _precision$;
			_scale = _scale$;
		}

		/**
		 * @return the name of the data constructor corresponding to this instance of SqlType
		 */
		public final java.lang.String getDCName() {
			return "SqlType_Decimal";
		}

		public final int getPrecision() {
			return _precision;
		}

		public final int getScale() {
			return _scale;
		}

		/**
		 * @return the ordinal of this instance of SqlType
		 */
		public int getDCOrdinal() {
			return 4;
		}

		public final java.lang.String toString() {
			return 
				((((((getDCName() + "\n") + "    precision = ") + 
				_precision) + 
				"\n") + 
				"    scale = ") + 
				_scale) + 
				"\n";
		}

		public final boolean equals(java.lang.Object object) {
			if (this == object) {
				return true;
			}
			if (object == null) {
				return false;
			}
			if (!(((java.lang.Object)object) instanceof SqlType_Decimal)) {
				return false;
			}

			SqlType_Decimal castobject = 
				((SqlType_Decimal)(java.lang.Object)object);

			if (!getDCName().equals(castobject.getDCName())) {
				return false;
			}
			return 
				(_precision == castobject._precision) && 
				(_scale == castobject._scale);
		}

		public final int hashCode() {
			if (hashCode == 0) {
				int result = 17;

				result = ((37 * result) + getDCName().hashCode());
				result = ((37 * result) + _precision);
				result = ((37 * result) + _scale);
				hashCode = result;
			}
			return hashCode;
		}

	}
	/**
	 * This class represents instances of the CAL data constructor Cal.Data.SqlType.SqlType_Numeric.
	 */
	public static final class SqlType_Numeric extends SqlType {
		/**
		 * Lazily initialized, cached hashCode.
		 */
		private volatile int hashCode = 0;

		private final int _precision;

		private final int _scale;

		public SqlType_Numeric(int _precision$, int _scale$) {
			_precision = _precision$;
			_scale = _scale$;
		}

		/**
		 * @return the name of the data constructor corresponding to this instance of SqlType
		 */
		public final java.lang.String getDCName() {
			return "SqlType_Numeric";
		}

		public final int getPrecision() {
			return _precision;
		}

		public final int getScale() {
			return _scale;
		}

		/**
		 * @return the ordinal of this instance of SqlType
		 */
		public int getDCOrdinal() {
			return 5;
		}

		public final java.lang.String toString() {
			return 
				((((((getDCName() + "\n") + "    precision = ") + 
				_precision) + 
				"\n") + 
				"    scale = ") + 
				_scale) + 
				"\n";
		}

		public final boolean equals(java.lang.Object object) {
			if (this == object) {
				return true;
			}
			if (object == null) {
				return false;
			}
			if (!(((java.lang.Object)object) instanceof SqlType_Numeric)) {
				return false;
			}

			SqlType_Numeric castobject = 
				((SqlType_Numeric)(java.lang.Object)object);

			if (!getDCName().equals(castobject.getDCName())) {
				return false;
			}
			return 
				(_precision == castobject._precision) && 
				(_scale == castobject._scale);
		}

		public final int hashCode() {
			if (hashCode == 0) {
				int result = 17;

				result = ((37 * result) + getDCName().hashCode());
				result = ((37 * result) + _precision);
				result = ((37 * result) + _scale);
				hashCode = result;
			}
			return hashCode;
		}

	}
	/**
	 * This class represents instances of the CAL data constructor Cal.Data.SqlType.SqlType_Real.
	 */
	public static final class SqlType_Real extends SqlType {
		/**
		 * Lazily initialized, cached hashCode.
		 */
		private volatile int hashCode = 0;

		public SqlType_Real() {
		}

		/**
		 * @return the name of the data constructor corresponding to this instance of SqlType
		 */
		public final java.lang.String getDCName() {
			return "SqlType_Real";
		}

		/**
		 * @return the ordinal of this instance of SqlType
		 */
		public int getDCOrdinal() {
			return 6;
		}

		public final java.lang.String toString() {
			return getDCName() + "\n";
		}

		public final boolean equals(java.lang.Object object) {
			if (this == object) {
				return true;
			}
			if (object == null) {
				return false;
			}
			if (!(((java.lang.Object)object) instanceof SqlType_Real)) {
				return false;
			}

			SqlType_Real castobject = 
				((SqlType_Real)(java.lang.Object)object);

			if (!getDCName().equals(castobject.getDCName())) {
				return false;
			}
			return true;
		}

		public final int hashCode() {
			if (hashCode == 0) {
				int result = 17;

				result = ((37 * result) + getDCName().hashCode());
				hashCode = result;
			}
			return hashCode;
		}

	}
	/**
	 * This class represents instances of the CAL data constructor Cal.Data.SqlType.SqlType_Float.
	 */
	public static final class SqlType_Float extends SqlType {
		/**
		 * Lazily initialized, cached hashCode.
		 */
		private volatile int hashCode = 0;

		public SqlType_Float() {
		}

		/**
		 * @return the name of the data constructor corresponding to this instance of SqlType
		 */
		public final java.lang.String getDCName() {
			return "SqlType_Float";
		}

		/**
		 * @return the ordinal of this instance of SqlType
		 */
		public int getDCOrdinal() {
			return 7;
		}

		public final java.lang.String toString() {
			return getDCName() + "\n";
		}

		public final boolean equals(java.lang.Object object) {
			if (this == object) {
				return true;
			}
			if (object == null) {
				return false;
			}
			if (!(((java.lang.Object)object) instanceof SqlType_Float)) {
				return false;
			}

			SqlType_Float castobject = 
				((SqlType_Float)(java.lang.Object)object);

			if (!getDCName().equals(castobject.getDCName())) {
				return false;
			}
			return true;
		}

		public final int hashCode() {
			if (hashCode == 0) {
				int result = 17;

				result = ((37 * result) + getDCName().hashCode());
				hashCode = result;
			}
			return hashCode;
		}

	}
	/**
	 * This class represents instances of the CAL data constructor Cal.Data.SqlType.SqlType_Double.
	 */
	public static final class SqlType_Double extends SqlType {
		/**
		 * Lazily initialized, cached hashCode.
		 */
		private volatile int hashCode = 0;

		public SqlType_Double() {
		}

		/**
		 * @return the name of the data constructor corresponding to this instance of SqlType
		 */
		public final java.lang.String getDCName() {
			return "SqlType_Double";
		}

		/**
		 * @return the ordinal of this instance of SqlType
		 */
		public int getDCOrdinal() {
			return 8;
		}

		public final java.lang.String toString() {
			return getDCName() + "\n";
		}

		public final boolean equals(java.lang.Object object) {
			if (this == object) {
				return true;
			}
			if (object == null) {
				return false;
			}
			if (!(((java.lang.Object)object) instanceof SqlType_Double)) {
				return false;
			}

			SqlType_Double castobject = 
				((SqlType_Double)(java.lang.Object)object);

			if (!getDCName().equals(castobject.getDCName())) {
				return false;
			}
			return true;
		}

		public final int hashCode() {
			if (hashCode == 0) {
				int result = 17;

				result = ((37 * result) + getDCName().hashCode());
				hashCode = result;
			}
			return hashCode;
		}

	}
	/**
	 * This class represents instances of the CAL data constructor Cal.Data.SqlType.SqlType_Bit.
	 */
	public static final class SqlType_Bit extends SqlType {
		/**
		 * Lazily initialized, cached hashCode.
		 */
		private volatile int hashCode = 0;

		public SqlType_Bit() {
		}

		/**
		 * @return the name of the data constructor corresponding to this instance of SqlType
		 */
		public final java.lang.String getDCName() {
			return "SqlType_Bit";
		}

		/**
		 * @return the ordinal of this instance of SqlType
		 */
		public int getDCOrdinal() {
			return 9;
		}

		public final java.lang.String toString() {
			return getDCName() + "\n";
		}

		public final boolean equals(java.lang.Object object) {
			if (this == object) {
				return true;
			}
			if (object == null) {
				return false;
			}
			if (!(((java.lang.Object)object) instanceof SqlType_Bit)) {
				return false;
			}

			SqlType_Bit castobject = ((SqlType_Bit)(java.lang.Object)object);

			if (!getDCName().equals(castobject.getDCName())) {
				return false;
			}
			return true;
		}

		public final int hashCode() {
			if (hashCode == 0) {
				int result = 17;

				result = ((37 * result) + getDCName().hashCode());
				hashCode = result;
			}
			return hashCode;
		}

	}
	/**
	 * This class represents instances of the CAL data constructor Cal.Data.SqlType.SqlType_Boolean.
	 */
	public static final class SqlType_Boolean extends SqlType {
		/**
		 * Lazily initialized, cached hashCode.
		 */
		private volatile int hashCode = 0;

		public SqlType_Boolean() {
		}

		/**
		 * @return the name of the data constructor corresponding to this instance of SqlType
		 */
		public final java.lang.String getDCName() {
			return "SqlType_Boolean";
		}

		/**
		 * @return the ordinal of this instance of SqlType
		 */
		public int getDCOrdinal() {
			return 10;
		}

		public final java.lang.String toString() {
			return getDCName() + "\n";
		}

		public final boolean equals(java.lang.Object object) {
			if (this == object) {
				return true;
			}
			if (object == null) {
				return false;
			}
			if (!(((java.lang.Object)object) instanceof SqlType_Boolean)) {
				return false;
			}

			SqlType_Boolean castobject = 
				((SqlType_Boolean)(java.lang.Object)object);

			if (!getDCName().equals(castobject.getDCName())) {
				return false;
			}
			return true;
		}

		public final int hashCode() {
			if (hashCode == 0) {
				int result = 17;

				result = ((37 * result) + getDCName().hashCode());
				hashCode = result;
			}
			return hashCode;
		}

	}
	/**
	 * This class represents instances of the CAL data constructor Cal.Data.SqlType.SqlType_Char.
	 */
	public static final class SqlType_Char extends SqlType {
		/**
		 * Lazily initialized, cached hashCode.
		 */
		private volatile int hashCode = 0;

		private final int _length;

		public SqlType_Char(int _length$) {
			_length = _length$;
		}

		/**
		 * @return the name of the data constructor corresponding to this instance of SqlType
		 */
		public final java.lang.String getDCName() {
			return "SqlType_Char";
		}

		public final int getLength() {
			return _length;
		}

		/**
		 * @return the ordinal of this instance of SqlType
		 */
		public int getDCOrdinal() {
			return 11;
		}

		public final java.lang.String toString() {
			return (((getDCName() + "\n") + "    length = ") + _length) + "\n";
		}

		public final boolean equals(java.lang.Object object) {
			if (this == object) {
				return true;
			}
			if (object == null) {
				return false;
			}
			if (!(((java.lang.Object)object) instanceof SqlType_Char)) {
				return false;
			}

			SqlType_Char castobject = 
				((SqlType_Char)(java.lang.Object)object);

			if (!getDCName().equals(castobject.getDCName())) {
				return false;
			}
			return _length == castobject._length;
		}

		public final int hashCode() {
			if (hashCode == 0) {
				int result = 17;

				result = ((37 * result) + getDCName().hashCode());
				result = ((37 * result) + _length);
				hashCode = result;
			}
			return hashCode;
		}

	}
	/**
	 * This class represents instances of the CAL data constructor Cal.Data.SqlType.SqlType_VarChar.
	 */
	public static final class SqlType_VarChar extends SqlType {
		/**
		 * Lazily initialized, cached hashCode.
		 */
		private volatile int hashCode = 0;

		private final int _length;

		public SqlType_VarChar(int _length$) {
			_length = _length$;
		}

		/**
		 * @return the name of the data constructor corresponding to this instance of SqlType
		 */
		public final java.lang.String getDCName() {
			return "SqlType_VarChar";
		}

		public final int getLength() {
			return _length;
		}

		/**
		 * @return the ordinal of this instance of SqlType
		 */
		public int getDCOrdinal() {
			return 12;
		}

		public final java.lang.String toString() {
			return (((getDCName() + "\n") + "    length = ") + _length) + "\n";
		}

		public final boolean equals(java.lang.Object object) {
			if (this == object) {
				return true;
			}
			if (object == null) {
				return false;
			}
			if (!(((java.lang.Object)object) instanceof SqlType_VarChar)) {
				return false;
			}

			SqlType_VarChar castobject = 
				((SqlType_VarChar)(java.lang.Object)object);

			if (!getDCName().equals(castobject.getDCName())) {
				return false;
			}
			return _length == castobject._length;
		}

		public final int hashCode() {
			if (hashCode == 0) {
				int result = 17;

				result = ((37 * result) + getDCName().hashCode());
				result = ((37 * result) + _length);
				hashCode = result;
			}
			return hashCode;
		}

	}
	/**
	 * This class represents instances of the CAL data constructor Cal.Data.SqlType.SqlType_LongVarChar.
	 */
	public static final class SqlType_LongVarChar extends SqlType {
		/**
		 * Lazily initialized, cached hashCode.
		 */
		private volatile int hashCode = 0;

		public SqlType_LongVarChar() {
		}

		/**
		 * @return the name of the data constructor corresponding to this instance of SqlType
		 */
		public final java.lang.String getDCName() {
			return "SqlType_LongVarChar";
		}

		/**
		 * @return the ordinal of this instance of SqlType
		 */
		public int getDCOrdinal() {
			return 13;
		}

		public final java.lang.String toString() {
			return getDCName() + "\n";
		}

		public final boolean equals(java.lang.Object object) {
			if (this == object) {
				return true;
			}
			if (object == null) {
				return false;
			}
			if (!(((java.lang.Object)object) instanceof SqlType_LongVarChar)) {
				return false;
			}

			SqlType_LongVarChar castobject = 
				((SqlType_LongVarChar)(java.lang.Object)object);

			if (!getDCName().equals(castobject.getDCName())) {
				return false;
			}
			return true;
		}

		public final int hashCode() {
			if (hashCode == 0) {
				int result = 17;

				result = ((37 * result) + getDCName().hashCode());
				hashCode = result;
			}
			return hashCode;
		}

	}
	/**
	 * This class represents instances of the CAL data constructor Cal.Data.SqlType.SqlType_Clob.
	 */
	public static final class SqlType_Clob extends SqlType {
		/**
		 * Lazily initialized, cached hashCode.
		 */
		private volatile int hashCode = 0;

		public SqlType_Clob() {
		}

		/**
		 * @return the name of the data constructor corresponding to this instance of SqlType
		 */
		public final java.lang.String getDCName() {
			return "SqlType_Clob";
		}

		/**
		 * @return the ordinal of this instance of SqlType
		 */
		public int getDCOrdinal() {
			return 14;
		}

		public final java.lang.String toString() {
			return getDCName() + "\n";
		}

		public final boolean equals(java.lang.Object object) {
			if (this == object) {
				return true;
			}
			if (object == null) {
				return false;
			}
			if (!(((java.lang.Object)object) instanceof SqlType_Clob)) {
				return false;
			}

			SqlType_Clob castobject = 
				((SqlType_Clob)(java.lang.Object)object);

			if (!getDCName().equals(castobject.getDCName())) {
				return false;
			}
			return true;
		}

		public final int hashCode() {
			if (hashCode == 0) {
				int result = 17;

				result = ((37 * result) + getDCName().hashCode());
				hashCode = result;
			}
			return hashCode;
		}

	}
	/**
	 * This class represents instances of the CAL data constructor Cal.Data.SqlType.SqlType_Binary.
	 */
	public static final class SqlType_Binary extends SqlType {
		/**
		 * Lazily initialized, cached hashCode.
		 */
		private volatile int hashCode = 0;

		private final int _length;

		public SqlType_Binary(int _length$) {
			_length = _length$;
		}

		/**
		 * @return the name of the data constructor corresponding to this instance of SqlType
		 */
		public final java.lang.String getDCName() {
			return "SqlType_Binary";
		}

		public final int getLength() {
			return _length;
		}

		/**
		 * @return the ordinal of this instance of SqlType
		 */
		public int getDCOrdinal() {
			return 15;
		}

		public final java.lang.String toString() {
			return (((getDCName() + "\n") + "    length = ") + _length) + "\n";
		}

		public final boolean equals(java.lang.Object object) {
			if (this == object) {
				return true;
			}
			if (object == null) {
				return false;
			}
			if (!(((java.lang.Object)object) instanceof SqlType_Binary)) {
				return false;
			}

			SqlType_Binary castobject = 
				((SqlType_Binary)(java.lang.Object)object);

			if (!getDCName().equals(castobject.getDCName())) {
				return false;
			}
			return _length == castobject._length;
		}

		public final int hashCode() {
			if (hashCode == 0) {
				int result = 17;

				result = ((37 * result) + getDCName().hashCode());
				result = ((37 * result) + _length);
				hashCode = result;
			}
			return hashCode;
		}

	}
	/**
	 * This class represents instances of the CAL data constructor Cal.Data.SqlType.SqlType_VarBinary.
	 */
	public static final class SqlType_VarBinary extends SqlType {
		/**
		 * Lazily initialized, cached hashCode.
		 */
		private volatile int hashCode = 0;

		private final int _length;

		public SqlType_VarBinary(int _length$) {
			_length = _length$;
		}

		/**
		 * @return the name of the data constructor corresponding to this instance of SqlType
		 */
		public final java.lang.String getDCName() {
			return "SqlType_VarBinary";
		}

		public final int getLength() {
			return _length;
		}

		/**
		 * @return the ordinal of this instance of SqlType
		 */
		public int getDCOrdinal() {
			return 16;
		}

		public final java.lang.String toString() {
			return (((getDCName() + "\n") + "    length = ") + _length) + "\n";
		}

		public final boolean equals(java.lang.Object object) {
			if (this == object) {
				return true;
			}
			if (object == null) {
				return false;
			}
			if (!(((java.lang.Object)object) instanceof SqlType_VarBinary)) {
				return false;
			}

			SqlType_VarBinary castobject = 
				((SqlType_VarBinary)(java.lang.Object)object);

			if (!getDCName().equals(castobject.getDCName())) {
				return false;
			}
			return _length == castobject._length;
		}

		public final int hashCode() {
			if (hashCode == 0) {
				int result = 17;

				result = ((37 * result) + getDCName().hashCode());
				result = ((37 * result) + _length);
				hashCode = result;
			}
			return hashCode;
		}

	}
	/**
	 * This class represents instances of the CAL data constructor Cal.Data.SqlType.SqlType_LongVarBinary.
	 */
	public static final class SqlType_LongVarBinary extends SqlType {
		/**
		 * Lazily initialized, cached hashCode.
		 */
		private volatile int hashCode = 0;

		public SqlType_LongVarBinary() {
		}

		/**
		 * @return the name of the data constructor corresponding to this instance of SqlType
		 */
		public final java.lang.String getDCName() {
			return "SqlType_LongVarBinary";
		}

		/**
		 * @return the ordinal of this instance of SqlType
		 */
		public int getDCOrdinal() {
			return 17;
		}

		public final java.lang.String toString() {
			return getDCName() + "\n";
		}

		public final boolean equals(java.lang.Object object) {
			if (this == object) {
				return true;
			}
			if (object == null) {
				return false;
			}
			if (!(((java.lang.Object)object) instanceof SqlType_LongVarBinary)) {
				return false;
			}

			SqlType_LongVarBinary castobject = 
				((SqlType_LongVarBinary)(java.lang.Object)object);

			if (!getDCName().equals(castobject.getDCName())) {
				return false;
			}
			return true;
		}

		public final int hashCode() {
			if (hashCode == 0) {
				int result = 17;

				result = ((37 * result) + getDCName().hashCode());
				hashCode = result;
			}
			return hashCode;
		}

	}
	/**
	 * This class represents instances of the CAL data constructor Cal.Data.SqlType.SqlType_Blob.
	 */
	public static final class SqlType_Blob extends SqlType {
		/**
		 * Lazily initialized, cached hashCode.
		 */
		private volatile int hashCode = 0;

		public SqlType_Blob() {
		}

		/**
		 * @return the name of the data constructor corresponding to this instance of SqlType
		 */
		public final java.lang.String getDCName() {
			return "SqlType_Blob";
		}

		/**
		 * @return the ordinal of this instance of SqlType
		 */
		public int getDCOrdinal() {
			return 18;
		}

		public final java.lang.String toString() {
			return getDCName() + "\n";
		}

		public final boolean equals(java.lang.Object object) {
			if (this == object) {
				return true;
			}
			if (object == null) {
				return false;
			}
			if (!(((java.lang.Object)object) instanceof SqlType_Blob)) {
				return false;
			}

			SqlType_Blob castobject = 
				((SqlType_Blob)(java.lang.Object)object);

			if (!getDCName().equals(castobject.getDCName())) {
				return false;
			}
			return true;
		}

		public final int hashCode() {
			if (hashCode == 0) {
				int result = 17;

				result = ((37 * result) + getDCName().hashCode());
				hashCode = result;
			}
			return hashCode;
		}

	}
	/**
	 * This class represents instances of the CAL data constructor Cal.Data.SqlType.SqlType_Date.
	 */
	public static final class SqlType_Date extends SqlType {
		/**
		 * Lazily initialized, cached hashCode.
		 */
		private volatile int hashCode = 0;

		public SqlType_Date() {
		}

		/**
		 * @return the name of the data constructor corresponding to this instance of SqlType
		 */
		public final java.lang.String getDCName() {
			return "SqlType_Date";
		}

		/**
		 * @return the ordinal of this instance of SqlType
		 */
		public int getDCOrdinal() {
			return 19;
		}

		public final java.lang.String toString() {
			return getDCName() + "\n";
		}

		public final boolean equals(java.lang.Object object) {
			if (this == object) {
				return true;
			}
			if (object == null) {
				return false;
			}
			if (!(((java.lang.Object)object) instanceof SqlType_Date)) {
				return false;
			}

			SqlType_Date castobject = 
				((SqlType_Date)(java.lang.Object)object);

			if (!getDCName().equals(castobject.getDCName())) {
				return false;
			}
			return true;
		}

		public final int hashCode() {
			if (hashCode == 0) {
				int result = 17;

				result = ((37 * result) + getDCName().hashCode());
				hashCode = result;
			}
			return hashCode;
		}

	}
	/**
	 * This class represents instances of the CAL data constructor Cal.Data.SqlType.SqlType_Time.
	 */
	public static final class SqlType_Time extends SqlType {
		/**
		 * Lazily initialized, cached hashCode.
		 */
		private volatile int hashCode = 0;

		public SqlType_Time() {
		}

		/**
		 * @return the name of the data constructor corresponding to this instance of SqlType
		 */
		public final java.lang.String getDCName() {
			return "SqlType_Time";
		}

		/**
		 * @return the ordinal of this instance of SqlType
		 */
		public int getDCOrdinal() {
			return 20;
		}

		public final java.lang.String toString() {
			return getDCName() + "\n";
		}

		public final boolean equals(java.lang.Object object) {
			if (this == object) {
				return true;
			}
			if (object == null) {
				return false;
			}
			if (!(((java.lang.Object)object) instanceof SqlType_Time)) {
				return false;
			}

			SqlType_Time castobject = 
				((SqlType_Time)(java.lang.Object)object);

			if (!getDCName().equals(castobject.getDCName())) {
				return false;
			}
			return true;
		}

		public final int hashCode() {
			if (hashCode == 0) {
				int result = 17;

				result = ((37 * result) + getDCName().hashCode());
				hashCode = result;
			}
			return hashCode;
		}

	}
	/**
	 * This class represents instances of the CAL data constructor Cal.Data.SqlType.SqlType_TimeStamp.
	 */
	public static final class SqlType_TimeStamp extends SqlType {
		/**
		 * Lazily initialized, cached hashCode.
		 */
		private volatile int hashCode = 0;

		public SqlType_TimeStamp() {
		}

		/**
		 * @return the name of the data constructor corresponding to this instance of SqlType
		 */
		public final java.lang.String getDCName() {
			return "SqlType_TimeStamp";
		}

		/**
		 * @return the ordinal of this instance of SqlType
		 */
		public int getDCOrdinal() {
			return 21;
		}

		public final java.lang.String toString() {
			return getDCName() + "\n";
		}

		public final boolean equals(java.lang.Object object) {
			if (this == object) {
				return true;
			}
			if (object == null) {
				return false;
			}
			if (!(((java.lang.Object)object) instanceof SqlType_TimeStamp)) {
				return false;
			}

			SqlType_TimeStamp castobject = 
				((SqlType_TimeStamp)(java.lang.Object)object);

			if (!getDCName().equals(castobject.getDCName())) {
				return false;
			}
			return true;
		}

		public final int hashCode() {
			if (hashCode == 0) {
				int result = 17;

				result = ((37 * result) + getDCName().hashCode());
				hashCode = result;
			}
			return hashCode;
		}

	}
	/**
	 * This class represents instances of the CAL data constructor Cal.Data.SqlType.SqlType_Array.
	 */
	public static final class SqlType_Array extends SqlType {
		/**
		 * Lazily initialized, cached hashCode.
		 */
		private volatile int hashCode = 0;

		public SqlType_Array() {
		}

		/**
		 * @return the name of the data constructor corresponding to this instance of SqlType
		 */
		public final java.lang.String getDCName() {
			return "SqlType_Array";
		}

		/**
		 * @return the ordinal of this instance of SqlType
		 */
		public int getDCOrdinal() {
			return 22;
		}

		public final java.lang.String toString() {
			return getDCName() + "\n";
		}

		public final boolean equals(java.lang.Object object) {
			if (this == object) {
				return true;
			}
			if (object == null) {
				return false;
			}
			if (!(((java.lang.Object)object) instanceof SqlType_Array)) {
				return false;
			}

			SqlType_Array castobject = 
				((SqlType_Array)(java.lang.Object)object);

			if (!getDCName().equals(castobject.getDCName())) {
				return false;
			}
			return true;
		}

		public final int hashCode() {
			if (hashCode == 0) {
				int result = 17;

				result = ((37 * result) + getDCName().hashCode());
				hashCode = result;
			}
			return hashCode;
		}

	}
	/**
	 * This class represents instances of the CAL data constructor Cal.Data.SqlType.SqlType_Datalink.
	 */
	public static final class SqlType_Datalink extends SqlType {
		/**
		 * Lazily initialized, cached hashCode.
		 */
		private volatile int hashCode = 0;

		public SqlType_Datalink() {
		}

		/**
		 * @return the name of the data constructor corresponding to this instance of SqlType
		 */
		public final java.lang.String getDCName() {
			return "SqlType_Datalink";
		}

		/**
		 * @return the ordinal of this instance of SqlType
		 */
		public int getDCOrdinal() {
			return 23;
		}

		public final java.lang.String toString() {
			return getDCName() + "\n";
		}

		public final boolean equals(java.lang.Object object) {
			if (this == object) {
				return true;
			}
			if (object == null) {
				return false;
			}
			if (!(((java.lang.Object)object) instanceof SqlType_Datalink)) {
				return false;
			}

			SqlType_Datalink castobject = 
				((SqlType_Datalink)(java.lang.Object)object);

			if (!getDCName().equals(castobject.getDCName())) {
				return false;
			}
			return true;
		}

		public final int hashCode() {
			if (hashCode == 0) {
				int result = 17;

				result = ((37 * result) + getDCName().hashCode());
				hashCode = result;
			}
			return hashCode;
		}

	}
	/**
	 * This class represents instances of the CAL data constructor Cal.Data.SqlType.SqlType_Distinct.
	 */
	public static final class SqlType_Distinct extends SqlType {
		/**
		 * Lazily initialized, cached hashCode.
		 */
		private volatile int hashCode = 0;

		public SqlType_Distinct() {
		}

		/**
		 * @return the name of the data constructor corresponding to this instance of SqlType
		 */
		public final java.lang.String getDCName() {
			return "SqlType_Distinct";
		}

		/**
		 * @return the ordinal of this instance of SqlType
		 */
		public int getDCOrdinal() {
			return 24;
		}

		public final java.lang.String toString() {
			return getDCName() + "\n";
		}

		public final boolean equals(java.lang.Object object) {
			if (this == object) {
				return true;
			}
			if (object == null) {
				return false;
			}
			if (!(((java.lang.Object)object) instanceof SqlType_Distinct)) {
				return false;
			}

			SqlType_Distinct castobject = 
				((SqlType_Distinct)(java.lang.Object)object);

			if (!getDCName().equals(castobject.getDCName())) {
				return false;
			}
			return true;
		}

		public final int hashCode() {
			if (hashCode == 0) {
				int result = 17;

				result = ((37 * result) + getDCName().hashCode());
				hashCode = result;
			}
			return hashCode;
		}

	}
	/**
	 * This class represents instances of the CAL data constructor Cal.Data.SqlType.SqlType_JavaObject.
	 */
	public static final class SqlType_JavaObject extends SqlType {
		/**
		 * Lazily initialized, cached hashCode.
		 */
		private volatile int hashCode = 0;

		public SqlType_JavaObject() {
		}

		/**
		 * @return the name of the data constructor corresponding to this instance of SqlType
		 */
		public final java.lang.String getDCName() {
			return "SqlType_JavaObject";
		}

		/**
		 * @return the ordinal of this instance of SqlType
		 */
		public int getDCOrdinal() {
			return 25;
		}

		public final java.lang.String toString() {
			return getDCName() + "\n";
		}

		public final boolean equals(java.lang.Object object) {
			if (this == object) {
				return true;
			}
			if (object == null) {
				return false;
			}
			if (!(((java.lang.Object)object) instanceof SqlType_JavaObject)) {
				return false;
			}

			SqlType_JavaObject castobject = 
				((SqlType_JavaObject)(java.lang.Object)object);

			if (!getDCName().equals(castobject.getDCName())) {
				return false;
			}
			return true;
		}

		public final int hashCode() {
			if (hashCode == 0) {
				int result = 17;

				result = ((37 * result) + getDCName().hashCode());
				hashCode = result;
			}
			return hashCode;
		}

	}
	/**
	 * This class represents instances of the CAL data constructor Cal.Data.SqlType.SqlType_Ref.
	 */
	public static final class SqlType_Ref extends SqlType {
		/**
		 * Lazily initialized, cached hashCode.
		 */
		private volatile int hashCode = 0;

		public SqlType_Ref() {
		}

		/**
		 * @return the name of the data constructor corresponding to this instance of SqlType
		 */
		public final java.lang.String getDCName() {
			return "SqlType_Ref";
		}

		/**
		 * @return the ordinal of this instance of SqlType
		 */
		public int getDCOrdinal() {
			return 26;
		}

		public final java.lang.String toString() {
			return getDCName() + "\n";
		}

		public final boolean equals(java.lang.Object object) {
			if (this == object) {
				return true;
			}
			if (object == null) {
				return false;
			}
			if (!(((java.lang.Object)object) instanceof SqlType_Ref)) {
				return false;
			}

			SqlType_Ref castobject = ((SqlType_Ref)(java.lang.Object)object);

			if (!getDCName().equals(castobject.getDCName())) {
				return false;
			}
			return true;
		}

		public final int hashCode() {
			if (hashCode == 0) {
				int result = 17;

				result = ((37 * result) + getDCName().hashCode());
				hashCode = result;
			}
			return hashCode;
		}

	}
	/**
	 * This class represents instances of the CAL data constructor Cal.Data.SqlType.SqlType_Struct.
	 */
	public static final class SqlType_Struct extends SqlType {
		/**
		 * Lazily initialized, cached hashCode.
		 */
		private volatile int hashCode = 0;

		public SqlType_Struct() {
		}

		/**
		 * @return the name of the data constructor corresponding to this instance of SqlType
		 */
		public final java.lang.String getDCName() {
			return "SqlType_Struct";
		}

		/**
		 * @return the ordinal of this instance of SqlType
		 */
		public int getDCOrdinal() {
			return 27;
		}

		public final java.lang.String toString() {
			return getDCName() + "\n";
		}

		public final boolean equals(java.lang.Object object) {
			if (this == object) {
				return true;
			}
			if (object == null) {
				return false;
			}
			if (!(((java.lang.Object)object) instanceof SqlType_Struct)) {
				return false;
			}

			SqlType_Struct castobject = 
				((SqlType_Struct)(java.lang.Object)object);

			if (!getDCName().equals(castobject.getDCName())) {
				return false;
			}
			return true;
		}

		public final int hashCode() {
			if (hashCode == 0) {
				int result = 17;

				result = ((37 * result) + getDCName().hashCode());
				hashCode = result;
			}
			return hashCode;
		}

	}
	/**
	 * This class represents instances of the CAL data constructor Cal.Data.SqlType.SqlType_Null.
	 */
	public static final class SqlType_Null extends SqlType {
		/**
		 * Lazily initialized, cached hashCode.
		 */
		private volatile int hashCode = 0;

		public SqlType_Null() {
		}

		/**
		 * @return the name of the data constructor corresponding to this instance of SqlType
		 */
		public final java.lang.String getDCName() {
			return "SqlType_Null";
		}

		/**
		 * @return the ordinal of this instance of SqlType
		 */
		public int getDCOrdinal() {
			return 28;
		}

		public final java.lang.String toString() {
			return getDCName() + "\n";
		}

		public final boolean equals(java.lang.Object object) {
			if (this == object) {
				return true;
			}
			if (object == null) {
				return false;
			}
			if (!(((java.lang.Object)object) instanceof SqlType_Null)) {
				return false;
			}

			SqlType_Null castobject = 
				((SqlType_Null)(java.lang.Object)object);

			if (!getDCName().equals(castobject.getDCName())) {
				return false;
			}
			return true;
		}

		public final int hashCode() {
			if (hashCode == 0) {
				int result = 17;

				result = ((37 * result) + getDCName().hashCode());
				hashCode = result;
			}
			return hashCode;
		}

	}
	/**
	 * This class represents instances of the CAL data constructor Cal.Data.SqlType.SqlType_Other.
	 */
	public static final class SqlType_Other extends SqlType {
		/**
		 * Lazily initialized, cached hashCode.
		 */
		private volatile int hashCode = 0;

		public SqlType_Other() {
		}

		/**
		 * @return the name of the data constructor corresponding to this instance of SqlType
		 */
		public final java.lang.String getDCName() {
			return "SqlType_Other";
		}

		/**
		 * @return the ordinal of this instance of SqlType
		 */
		public int getDCOrdinal() {
			return 29;
		}

		public final java.lang.String toString() {
			return getDCName() + "\n";
		}

		public final boolean equals(java.lang.Object object) {
			if (this == object) {
				return true;
			}
			if (object == null) {
				return false;
			}
			if (!(((java.lang.Object)object) instanceof SqlType_Other)) {
				return false;
			}

			SqlType_Other castobject = 
				((SqlType_Other)(java.lang.Object)object);

			if (!getDCName().equals(castobject.getDCName())) {
				return false;
			}
			return true;
		}

		public final int hashCode() {
			if (hashCode == 0) {
				int result = 17;

				result = ((37 * result) + getDCName().hashCode());
				hashCode = result;
			}
			return hashCode;
		}

	}
}
